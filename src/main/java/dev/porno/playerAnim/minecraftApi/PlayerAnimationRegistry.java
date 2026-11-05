package dev.porno.playerAnim.minecraftApi;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import dev.porno.playerAnim.api.IPlayable;
import dev.porno.playerAnim.minecraftApi.codec.AnimationCodecs;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public final class PlayerAnimationRegistry {
   private static final HashMap<Identifier, IPlayable> animations = new HashMap<>();
   private static final Logger logger = LoggerFactory.getLogger(PlayerAnimationRegistry.class);
   public static final Identifier KEY = Identifier.fromNamespaceAndPath("playeranimator", "animation");

   @Nullable
   public static IPlayable getAnimation(@NotNull Identifier identifier) {
      return animations.get(identifier);
   }

   @NotNull
   public static Optional<IPlayable> getAnimationOptional(@NotNull Identifier identifier) {
      return Optional.ofNullable(getAnimation(identifier));
   }

   public static Map<Identifier, IPlayable> getAnimations() {
      return Map.copyOf(animations);
   }

   @NotNull
   public static Map<String, IPlayable> getModAnimations(@NotNull String modid) {
      HashMap<String, IPlayable> map = new HashMap<>();

      for (Entry<Identifier, IPlayable> entry : animations.entrySet()) {
         if (entry.getKey().getNamespace().equals(modid)) {
            map.put(entry.getKey().getPath(), entry.getValue());
         }
      }

      return map;
   }

   @Internal
   public static void resourceLoaderCallback(@NotNull ResourceManager manager) {
      animations.clear();

      for (Entry<Identifier, Resource> resource : manager.listResources("player_animations", ignore -> true).entrySet()) {
         String extension = AnimationCodecs.getExtension(resource.getKey().getPath());
         if (extension != null) {
            for (IPlayable animation : AnimationCodecs.deserialize(extension, () -> {
               try {
                  return resource.getValue().open();
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            })) {
               animations.put(Identifier.fromNamespaceAndPath(resource.getKey().getNamespace(), serializeTextToString(animation.getName())), animation);
            }
         }
      }

      for (Entry<Identifier, Resource> resource : manager.listResources("player_animation", ignore -> true).entrySet()) {
         String extension = AnimationCodecs.getExtension(resource.getKey().getPath());
         if (extension != null) {
            logger.warn(
               "[WARNING FOR MOD DEVS] Animation {} is in wrong directory: \"player_animation\", please place it in \"player_animations\".",
               resource.getKey().getPath()
            );

            for (IPlayable animation : AnimationCodecs.deserialize(extension, () -> {
               try {
                  return resource.getValue().open();
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            })) {
               animations.put(Identifier.fromNamespaceAndPath(resource.getKey().getNamespace(), serializeTextToString(animation.getName())), animation);
            }
         }
      }
   }

   public static String serializeTextToString(String arg) {
      try {
         JsonElement jsonElement = StrictJsonParser.parse(arg);
         Component component = (Component)ComponentSerialization.CODEC
            .parse(RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE), jsonElement)
            .getOrThrow(JsonParseException::new);
         if (component != null) {
            return component.getString();
         }
      } catch (Exception e) {
      }

      return arg.replace("\"", "");
   }
}

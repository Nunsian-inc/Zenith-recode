package dev.porno.playerAnim.minecraftApi;

import dev.porno.playerAnim.api.layered.AnimationStack;
import dev.porno.playerAnim.api.layered.IAnimation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface PlayerAnimationFactory {
   PlayerAnimationFactory.FactoryHolder ANIMATION_DATA_FACTORY = new PlayerAnimationFactory.FactoryHolder();

   @Nullable
   IAnimation invoke(@NotNull AbstractClientPlayer player);

   class FactoryHolder {
      private static final List<Function<AbstractClientPlayer, PlayerAnimationFactory.FactoryHolder.DataHolder>> factories = new ArrayList<>();

      private FactoryHolder() {
      }

      public void registerFactory(@Nullable Identifier id, int priority, @NotNull PlayerAnimationFactory factory) {
         factories.add(
            player -> Optional.ofNullable(factory.invoke(player))
               .map(animation -> new PlayerAnimationFactory.FactoryHolder.DataHolder(id, priority, animation))
               .orElse(null)
         );
      }

      @Internal
      public void prepareAnimations(AbstractClientPlayer player, AnimationStack playerStack, Map<Identifier, IAnimation> animationMap) {
         for (Function<AbstractClientPlayer, PlayerAnimationFactory.FactoryHolder.DataHolder> factory : factories) {
            PlayerAnimationFactory.FactoryHolder.DataHolder dataHolder = factory.apply(player);
            if (dataHolder != null) {
               playerStack.addAnimLayer(dataHolder.priority(), dataHolder.animation());
               if (dataHolder.id() != null) {
                  animationMap.put(dataHolder.id(), dataHolder.animation());
               }
            }
         }
      }

      @Internal
      private record DataHolder(@Nullable Identifier id, int priority, @NotNull IAnimation animation) {
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.porno.playerAnim.core.data.KeyframeAnimation;
import dev.porno.playerAnim.minecraftApi.codec.EmotecraftGsonCodec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.misc.EmoteInfo;

public final class EmoteRegistry {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Emotes");
   private static final List<String> items = List.of(
      "waving", "clap", "crying", "point", "here", "palm", "backflip", "roblox_potion_dance", "kazotsky_kick", "twerk", "club_penguin_dance"
   );
   private static final List<String> items2 = List.of("/assets/zenith/emotes/spemotes/index.txt");
   private final CompletableFuture<Map<String, EmoteInfo>> completableFuture = CompletableFuture.<Map<String, EmoteInfo>>supplyAsync(
         EmoteRegistry::method1589, task -> {
            Thread thread = new Thread(task, "Zenith Emote Loader");
            thread.setDaemon(true);
            thread.start();
         }
      )
      .exceptionally(throwable -> {
         logger.error("Could not load built-in Zenith emotes", throwable);
         return Map.of();
      });

   private static Map<String, EmoteInfo> method1589() {
      LinkedHashMap map = new LinkedHashMap();

      for (String text : items) {
         method1594(text).ifPresent(emoteInfo -> map.put(emoteInfo.getText(), emoteInfo));
      }

      for (String name : items2) {
         method1593(name).forEach(text -> method1594(text).ifPresent(var1x -> map.put(var1x.getText(), var1x)));
      }

      var unmodifiableMap = Collections.unmodifiableMap(map);
      logger.info("Loaded {} built-in Zenith emotes", unmodifiableMap.size());
      return unmodifiableMap;
   }

   private Map<String, EmoteInfo> method1590() {
      return this.completableFuture.join();
   }

   public Optional<EmoteInfo> method1591(String text) {
      return text != null && !text.isBlank() ? Optional.ofNullable(this.method1590().get(method1596(text))) : Optional.empty();
   }

   public Collection<EmoteInfo> all() {
      return this.method1590().values();
   }

   public List<String> method1592() {
      return List.copyOf(this.method1590().keySet());
   }

   private static List<String> method1593(String name) {
      try {
         List list;
         try (InputStream resourceAsStream = EmoteRegistry.class.getResourceAsStream(name)) {
            if (resourceAsStream == null) {
               logger.warn("Missing built-in emote index {}", name);
               return List.of();
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {
               list = bufferedReader.lines().map(String::trim).filter(var0x -> !var0x.isEmpty() && !var0x.startsWith("#")).toList();
            }
         }

         return list;
      } catch (IOException e) {
         logger.warn("Could not load built-in emote index {}", name, e);
         return List.of();
      }
   }

   private static Optional<EmoteInfo> method1594(String text) {
      String name = "/assets/zenith/emotes/" + text + ".json";

      try {
         Optional optional;
         try (InputStream resourceAsStream = EmoteRegistry.class.getResourceAsStream(name)) {
            if (resourceAsStream == null) {
               logger.warn("Missing built-in emote resource {}", name);
               return Optional.empty();
            }

            var keyframeAnimations = EmotecraftGsonCodec.INSTANCE.decode(resourceAsStream);
            if (keyframeAnimations.isEmpty()) {
               logger.warn("Built-in emote {} contains no animations", text);
               return Optional.empty();
            }

            KeyframeAnimation keyframeAnimation = (KeyframeAnimation)keyframeAnimations.iterator().next();
            optional = Optional.of(
               new EmoteInfo(
                  method1596(text),
                  keyframeAnimation.getUuid(),
                  method1595(keyframeAnimation, "name", method1597(text)),
                  method1595(keyframeAnimation, "author", "Unknown"),
                  Identifier.fromNamespaceAndPath("Zenith".toLowerCase(Locale.ROOT), "emotes/" + text + ".png"),
                  keyframeAnimation
               )
            );
         }

         return optional;
      } catch (RuntimeException | IOException e) {
         logger.warn("Could not load built-in emote {}", text, e);
         return Optional.empty();
      }
   }

   private static String method1595(KeyframeAnimation keyframeAnimation, String key, String str) {
      if (keyframeAnimation.extraData.get(key) instanceof String json && !json.isBlank()) {
         try {
            JsonElement string = JsonParser.parseString(json);
            if (string.isJsonPrimitive() && string.getAsJsonPrimitive().isString()) {
               return string.getAsString();
            }

            if (string.isJsonObject()) {
               JsonObject jsonObject = string.getAsJsonObject();
               if (jsonObject.has("fallback") && jsonObject.get("fallback").isJsonPrimitive()) {
                  return jsonObject.get("fallback").getAsString();
               }
            }
         } catch (RuntimeException e) {
         }

         return json;
      } else {
         return str;
      }
   }

   private static String method1596(String text) {
      return text.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
   }

   private static String method1597(String text) {
      ArrayList elements = new ArrayList();

      for (String str : text.split("_")) {
         elements.add(str.isEmpty() ? str : Character.toUpperCase(str.charAt(0)) + str.substring(1));
      }

      return String.join(" ", elements);
   }
}

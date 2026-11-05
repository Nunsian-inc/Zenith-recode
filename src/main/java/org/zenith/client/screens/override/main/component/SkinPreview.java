package org.zenith.client.screens.override.main.component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.ClientAsset.ResourceTexture;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.zenith.Zenith;
import org.zenith.utility.api.HttpClients;

public final class SkinPreview {
   private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");
   private static final Map<String, PlayerSkin> CACHE = new ConcurrentHashMap<>();
   private static final Map<String, Boolean> PENDING = new ConcurrentHashMap<>();
   private static final HttpClient HTTP = HttpClients.method2670(Duration.ofSeconds(10L));
   private static final String[][] BUNDLED = new String[][]{
      {"dinnerbone", "WIDE"},
      {"grumm", "WIDE"},
      {"technoblade", "WIDE"},
      {"captainsparklez", "WIDE"},
      {"ph1lza", "WIDE"},
      {"vurb", "SLIM"},
      {"purpled", "SLIM"},
      {"punz", "SLIM"}
   };
   private static final PlayerSkin[] FALLBACKS = new PlayerSkin[BUNDLED.length];

   private SkinPreview() {
   }

   public static PlayerSkin textures(String input, UUID profileId) {
      String lowerCase = input == null ? "" : input.toLowerCase(Locale.ROOT);
      PlayerSkin playerSkin = CACHE.get(lowerCase);
      if (playerSkin != null) {
         return playerSkin;
      } else {
         if (input != null && VALID_NAME.matcher(input).matches() && PENDING.putIfAbsent(lowerCase, Boolean.TRUE) == null) {
            resolve(lowerCase, input, profileId);
         }

         return fallback(lowerCase, profileId);
      }
   }

   private static PlayerSkin fallback(String lowerCase, UUID profileId) {
      if (lowerCase.isEmpty()) {
         return DefaultPlayerSkin.get(profileId);
      } else {
         int floorMod = Math.floorMod(lowerCase.hashCode(), FALLBACKS.length);
         PlayerSkin playerSkin = FALLBACKS[floorMod];
         if (playerSkin == null) {
            playerSkin = new PlayerSkin(
               new ResourceTexture(Zenith.method1501("skins/" + BUNDLED[floorMod][0]), Zenith.method1501("skins/" + BUNDLED[floorMod][0] + ".png")),
               null,
               null,
               PlayerModelType.valueOf(BUNDLED[floorMod][1]),
               false
            );
            FALLBACKS[floorMod] = playerSkin;
         }

         return playerSkin;
      }
   }

   private static void resolve(String lowerCase, String input, UUID profileId) {
      Minecraft minecraft = Minecraft.getInstance();
      HttpRequest httpRequest = HttpRequest.newBuilder()
         .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + input))
         .timeout(Duration.ofSeconds(10L))
         .GET()
         .build();
      HTTP.sendAsync(httpRequest, BodyHandlers.ofString())
         .thenApply(var1x -> {
            if (var1x.statusCode() != 200) {
               return profileId;
            } else {
               JsonObject var2x = JsonParser.parseString(var1x.body()).getAsJsonObject();
               return undash(var2x.get("id").getAsString());
            }
         })
         .exceptionally(var1x -> profileId)
         .thenAccept(var3x -> minecraft.execute(() -> minecraft.getSkinManager().get(new GameProfile(var3x, input)).whenComplete((var1xxx, var2xxx) -> {
            if (var2xxx == null && var1xxx != null && var1xxx.isPresent()) {
               CACHE.put(lowerCase, var1xxx.get());
            }

            PENDING.remove(lowerCase);
         })));
   }

   private static UUID undash(String string) {
      return UUID.fromString(string.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
   }
}

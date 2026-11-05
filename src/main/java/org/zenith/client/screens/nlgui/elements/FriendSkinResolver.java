package org.zenith.client.screens.nlgui.elements;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.TextureId;
import org.zenith.utility.render.ShapeRenderer;

public final class FriendSkinResolver implements MinecraftAccess {
   private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "antidumper228-loader");
      thread.setDaemon(true);
      return thread;
   });
   private static final Map<String, Identifier> CACHE = new ConcurrentHashMap<>();
   private static final Map<String, Boolean> LOADING = new ConcurrentHashMap<>();
   private static final Map<String, Long> FAILED_UNTIL = new ConcurrentHashMap<>();
   private static final Set<Identifier> EXTERNAL_TEXTURES = ConcurrentHashMap.newKeySet();

   private FriendSkinResolver() {
   }

   public static Identifier resolveSkin(String anotherString) {
      if (anotherString != null && !anotherString.isBlank()) {
         String lowerCase = anotherString.trim().toLowerCase();
         Identifier onlineSkin = getOnlineSkin(anotherString);
         if (onlineSkin != null) {
            CACHE.put(lowerCase, onlineSkin);
            return onlineSkin;
         } else {
            Identifier id = CACHE.get(lowerCase);
            if (id != null) {
               return id;
            } else {
               long now = System.currentTimeMillis();
               Long l = FAILED_UNTIL.get(lowerCase);
               if (l != null && l > now) {
                  return DefaultPlayerSkin.getDefaultSkin().body().texturePath();
               } else {
                  if (LOADING.putIfAbsent(lowerCase, Boolean.TRUE) == null) {
                     Identifier externalTextureId = getExternalTextureId(lowerCase);
                     EXECUTOR.execute(() -> {
                        boolean flag = false /* VF: Semaphore variable */;

                        label82: {
                           label83: {
                              try {
                                 flag = true;
                                 byte[] var3x = downloadHeadPng(anotherString.trim());
                                 if (var3x == null || var3x.length <= 0) {
                                    FAILED_UNTIL.put(lowerCase, System.currentTimeMillis() + 30000L);
                                    flag = false;
                                 } else {
                                    boolean containsKey;
                                    label61: {
                                       if (LOADING != null) {
                                          if (LOADING.isEmpty()) {
                                             containsKey = false;
                                             break label61;
                                          }
                                       }

                                       containsKey = LOADING.containsKey(lowerCase);
                                    }

                                    if (!containsKey) {
                                       flag = false;
                                       break label82;
                                    }

                                    ShapeRenderer.method7178(new TextureId(externalTextureId), var3x);
                                    CACHE.put(lowerCase, externalTextureId);
                                    EXTERNAL_TEXTURES.add(externalTextureId);
                                    FAILED_UNTIL.remove(lowerCase);
                                    flag = false;
                                 }
                                 break label83;
                              } catch (Exception e) {
                                 FAILED_UNTIL.put(lowerCase, System.currentTimeMillis() + 30000L);
                                 flag = false;
                              } finally {
                                 if (flag) {
                                    LOADING.remove(lowerCase);
                                 }
                              }

                              LOADING.remove(lowerCase);
                              return;
                           }

                           LOADING.remove(lowerCase);
                           return;
                        }

                        LOADING.remove(lowerCase);
                     });
                  }

                  return DefaultPlayerSkin.getDefaultSkin().body().texturePath();
               }
            }
         }
      } else {
         return DefaultPlayerSkin.getDefaultSkin().body().texturePath();
      }
   }

   public static void clearExternalCache() {
      Set<Identifier> ids = Set.copyOf(EXTERNAL_TEXTURES);

      for (Identifier location : ids) {
         try {
            mc.getTextureManager().release(location);
         } catch (Exception e) {
         }
      }

      EXTERNAL_TEXTURES.clear();
      CACHE.entrySet().removeIf(entry -> ids.contains(entry.getValue()));
      FAILED_UNTIL.clear();
      LOADING.clear();
   }

   private static Identifier getOnlineSkin(String anotherString) {
      if (mc.getConnection() == null) {
         return null;
      } else {
         for (PlayerInfo onlinePlayer : mc.getConnection().getOnlinePlayers()) {
            if (onlinePlayer != null && onlinePlayer.getProfile() != null && onlinePlayer.getProfile().name() != null && onlinePlayer.getProfile().name().equalsIgnoreCase(anotherString)) {
               if (onlinePlayer.getSkin() == null) {
                  return null;
               }

               return onlinePlayer.getSkin().body().texturePath();
            }
         }

         return null;
      }
   }

   private static Identifier getExternalTextureId(String lowerCase) {
      UUID nameUUIDFromBytes = UUID.nameUUIDFromBytes(("friend_skin_" + lowerCase).getBytes(StandardCharsets.UTF_8));
      return new TextureId("friend_heads/" + nameUUIDFromBytes.toString().replace("-", "")).getIdentifier();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static byte[] downloadHeadPng(String str) throws Exception {
      String str2 = URLEncoder.encode(str, StandardCharsets.UTF_8).replace("+", "%20");
      String str3 = "https://minotar.net/helm/" + str2 + "/100.png";
      HttpURLConnection openConnection = (HttpURLConnection)URI.create(str3).toURL().openConnection();
      openConnection.setConnectTimeout(2500);
      openConnection.setReadTimeout(2500);
      openConnection.setRequestProperty("User-Agent", "Sasske228");
      int responseCode;
      if ((responseCode = openConnection.getResponseCode()) >= 200 && responseCode < 300) {
         boolean flag = false /* VF: Semaphore variable */;

         byte[] byteArray;
         try {
            flag = true;

            try (
               InputStream inputStream = openConnection.getInputStream();
               ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ) {
               byte[] bytes = new byte[4096];

               int len;
               while ((len = inputStream.read(bytes)) != -1) {
                  byteArrayOutputStream.write(bytes, 0, len);
               }

               byteArray = byteArrayOutputStream.toByteArray();
            }
         } finally {
            if (flag) {
               openConnection.disconnect();
            }
         }

         openConnection.disconnect();
         return byteArray;
      } else {
         return null;
      }
   }
}

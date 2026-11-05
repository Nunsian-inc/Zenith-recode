package org.zenith.client.screens.nlgui.cloud;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.StoreApi;

public final class CloudPreviewTextureCache {
   private static final String NAMESPACE = "zenith";
   private static final String PREFIX = "cloud_config_preview/";
   private static final int MAX_TEXTURES = 32;
   private static final int MAX_IN_FLIGHT = 6;
   private static final long RETRY_DELAY_MS = 15000L;
   private static final int MAX_PREVIEW_BYTES = 4194304;
   private static final int MAX_PREVIEW_SIDE = 2048;
   private static final long MAX_PREVIEW_PIXELS = 2359296L;
   private static final Map<String, Identifier> textures = new LinkedHashMap<>(16, 0.75F, true);
   private static final Set<String> inFlight = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> failures = new ConcurrentHashMap<>();
   private static Identifier draftId;
   private static int draftFingerprint;

   private CloudPreviewTextureCache() {
   }

   public static Identifier get(UUID uuid, String str) {
      String lowerCase = str == null ? null : str.toLowerCase(Locale.ROOT);
      if (uuid != null && validKey(lowerCase)) {
         synchronized (textures) {
            Identifier texture = textures.get(lowerCase);
            if (texture != null) {
               return texture;
            }
         }

         Long failure = failures.get(lowerCase);
         if (failure != null && System.currentTimeMillis() - failure < 15000L) {
            return null;
         } else {
            StoreApi storeApi = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
            if (storeApi != null && !inFlight.contains(lowerCase) && inFlight.size() < 6 && inFlight.add(lowerCase)) {
               storeApi.method1971(uuid).whenComplete((var1x, var2x) -> {
                  try {
                     if (var2x == null && var1x != null && var1x.length != 0) {
                        install(lowerCase, var1x);
                        return;
                     }

                     failures.put(lowerCase, System.currentTimeMillis());
                  } finally {
                     inFlight.remove(lowerCase);
                  }
               });
               return null;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static void install(String lowerCase, byte[] buf) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null) {
         if (!sane(buf)) {
            failures.put(lowerCase, System.currentTimeMillis());
         } else {
            NativeImage image;
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf)) {
               image = NativeImage.read(byteArrayInputStream);
            } catch (Exception e) {
               failures.put(lowerCase, System.currentTimeMillis());
               return;
            }

            minecraft.execute(() -> {
               Identifier var3x = Identifier.fromNamespaceAndPath("zenith", "cloud_config_preview/" + lowerCase);

               try {
                  minecraft.getTextureManager().register(var3x, new DynamicTexture(() -> "zenith", image));
               } catch (Exception e) {
                  image.close();
                  failures.put(lowerCase, System.currentTimeMillis());
                  return;
               }

               failures.remove(lowerCase);
               synchronized (textures) {
                  textures.put(lowerCase, var3x);
                  evictOverflow(minecraft);
               }
            });
         }
      }
   }

   private static boolean validKey(String lowerCase) {
      if (lowerCase != null && !lowerCase.isEmpty() && lowerCase.length() <= 128) {
         for (int i = 0; i < lowerCase.length(); i++) {
            char charAt = lowerCase.charAt(i);
            if ((charAt < '0' || charAt > '9') && (charAt < 'a' || charAt > 'z') && charAt != '_' && charAt != '.' && charAt != '-') {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean sane(byte[] buf) {
      if (buf.length >= 24
         && buf.length <= 4194304
         && (buf[0] & 255) == 137
         && buf[1] == 80
         && buf[2] == 78
         && buf[3] == 71
         && buf[12] == 73
         && buf[13] == 72
         && buf[14] == 68
         && buf[15] == 82) {
         int n = (buf[16] & 255) << 24 | (buf[17] & 255) << 16 | (buf[18] & 255) << 8 | buf[19] & 255;
         int n2 = (buf[20] & 255) << 24 | (buf[21] & 255) << 16 | (buf[22] & 255) << 8 | buf[23] & 255;
         return n > 0 && n2 > 0 && n <= 2048 && n2 <= 2048 && (long)n * n2 <= 2359296L;
      } else {
         return false;
      }
   }

   private static void evictOverflow(Minecraft minecraft) {
      var iterator = textures.entrySet().iterator();

      while (textures.size() > 32 && iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         iterator.remove();

         try {
            minecraft.getTextureManager().release((Identifier)entry.getValue());
         } catch (Exception e) {
         }
      }
   }

   public static synchronized Identifier draft(byte[] buf) {
      if (buf != null && buf.length != 0) {
         int n = Arrays.hashCode(buf);
         if (draftId != null && draftFingerprint == n) {
            return draftId;
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null) {
               return null;
            } else {
               clearDraft();

               try {
                  Identifier location;
                  try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf)) {
                     NativeImage image = NativeImage.read(byteArrayInputStream);
                     Identifier id = Identifier.fromNamespaceAndPath("zenith", "cloud_config_preview/draft_" + Integer.toHexString(n));
                     minecraft.getTextureManager().register(id, new DynamicTexture(() -> "zenith", image));
                     draftId = id;
                     draftFingerprint = n;
                     location = id;
                  }

                  return location;
               } catch (Exception e) {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   public static synchronized void clearDraft() {
      Minecraft minecraft = Minecraft.getInstance();
      if (draftId != null && minecraft != null && minecraft.getTextureManager() != null) {
         try {
            minecraft.getTextureManager().release(draftId);
         } catch (Exception e) {
         }
      }

      draftId = null;
      draftFingerprint = 0;
   }

   public static void clear() {
      Minecraft minecraft = Minecraft.getInstance();
      synchronized (textures) {
         if (minecraft != null && minecraft.getTextureManager() != null) {
            for (Identifier location : textures.values()) {
               try {
                  minecraft.getTextureManager().release(location);
               } catch (Exception e) {
               }
            }
         }

         textures.clear();
      }

      failures.clear();
   }
}

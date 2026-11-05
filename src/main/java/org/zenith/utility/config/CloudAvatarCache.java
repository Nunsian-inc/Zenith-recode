package org.zenith.utility.config;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.misc.ImageLoader;

public final class CloudAvatarCache {
   private static final String text = "zenith";
   private static final String text2 = "cloud_avatar/";
   private static final String text3 = "/user/avatar/";
   private static final int CONST_intValue = 2097152;
   private static final long CONST_longValue = 12582912L;
   private static final long CONST_longValue2 = 2000L;
   private static final LinkedHashMap<String, CloudAvatarCache.Entry> linkedHashMap = new LinkedHashMap<>(32, 0.75F, true);
   private static long longValue;
   private static final Set<String> items = ConcurrentHashMap.newKeySet();
   private static final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> items3 = new ConcurrentHashMap<>();
   private static final long CONST_longValue3 = 30000L;
   private static final long CONST_longValue4 = 120L;
   private static long longValue2;
   private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(64), task -> {
      Thread thread = new Thread(task, "cloud-avatar-loader");
      thread.setDaemon(true);
      return thread;
   });
   private static final long CONST_longValue5 = 86400000L;
   private static final int CONST_intValue2 = 1024;
   private static final Pattern pattern = Pattern.compile("[A-Za-z0-9_-]{1,64}");
   private static final long CONST_longValue6 = 10000L;
   private static volatile long longValue3;

   private CloudAvatarCache() {
   }

   public static Identifier get(String string) {
      if (string != null && !string.isBlank()) {
         synchronized (linkedHashMap) {
            CloudAvatarCache.Entry entry = linkedHashMap.get(string);
            if (entry != null) {
               entry.longValue2 = System.currentTimeMillis();
               return entry.getIdentifier();
            }
         }

         if (items2.contains(string)) {
            return null;
         } else {
            Long l = items3.get(string);
            if (l != null && System.currentTimeMillis() < l) {
               return null;
            } else if (!items.add(string)) {
               return null;
            } else {
               try {
                  threadPoolExecutor.execute(() -> load(string));
               } catch (RejectedExecutionException e) {
                  items.remove(string);
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static void load(String string) {
      if (!pattern.matcher(string).matches()) {
         method2127(string);
         items.remove(string);
      } else {
         Path path = cacheDir().resolve(string + ".img");
         byte[] allBytes = method2128(path);
         boolean flag = allBytes != null && method2129(path);
         if (allBytes != null && flag) {
            NativeImage image = method2132(allBytes);
            if (image != null) {
               Minecraft.getInstance().execute(() -> method2136(string, image));
               return;
            }

            method2131(path);
            allBytes = null;
         }

         try {
            byte[] bytes = method2138(string);
            if (bytes == null || bytes.length == 0) {
               method2127(string);
               items.remove(string);
               return;
            }

            NativeImage image = method2132(bytes);
            if (image == null) {
               method2127(string);
               items.remove(string);
               return;
            }

            method2130(path, bytes);
            Minecraft.getInstance().execute(() -> method2136(string, image));
         } catch (CloudAvatarCache.Entry2 e) {
            method2131(path);
            method2127(string);
            items.remove(string);
         } catch (Throwable e) {
            NativeImage image = allBytes == null ? null : method2132(allBytes);
            if (image != null) {
               Minecraft.getInstance().execute(() -> method2136(string, image));
               return;
            }

            if (items3.size() > 4096) {
               items3.clear();
            }

            items3.put(string, System.currentTimeMillis() + 30000L);
            items.remove(string);
         }
      }
   }

   private static void method2127(String string) {
      if (items2.size() > 4096) {
         items2.clear();
      }

      items2.add(string);
   }

   private static byte[] method2128(Path path) {
      try {
         if (Files.isRegularFile(path)) {
            byte[] allBytes = Files.readAllBytes(path);
            return allBytes.length > 0 ? allBytes : null;
         }
      } catch (Exception e) {
      }

      return null;
   }

   private static boolean method2129(Path path) {
      try {
         return System.currentTimeMillis() - Files.getLastModifiedTime(path).toMillis() < 86400000L;
      } catch (Exception e) {
         return false;
      }
   }

   private static void method2130(Path path, byte[] bytes) {
      try {
         Files.createDirectories(path.getParent());
         Path sibling = path.resolveSibling(path.getFileName() + ".tmp");
         Files.write(sibling, bytes);

         try {
            Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException e) {
            Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception e) {
      }
   }

   private static void method2131(Path path) {
      try {
         Files.deleteIfExists(path);
      } catch (Exception e) {
      }
   }

   private static NativeImage method2132(byte[] allBytes) {
      try {
         int[] ints = ImageLoader.method231(allBytes);
         return ints[0] > 0 && ints[1] > 0 && ints[0] <= 1024 && ints[1] <= 1024 ? method2133(allBytes) : null;
      } catch (Exception e) {
         return null;
      }
   }

   private static NativeImage method2133(byte[] allBytes) throws Exception {
      try {
         NativeImage nativeImage;
         try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(allBytes)) {
            nativeImage = NativeImage.read(byteArrayInputStream);
         }

         return nativeImage;
      } catch (Exception e) {
         return method2134(allBytes);
      }
   }

   private static NativeImage method2134(byte[] allBytes) throws Exception {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(allBytes);

      BufferedImage bufferedImage;
      try {
         bufferedImage = ImageIO.read(byteArrayInputStream);
      } catch (Throwable e) {
         try {
            byteArrayInputStream.close();
         } catch (Throwable ex) {
            e.addSuppressed(ex);
         }

         throw e;
      }

      byteArrayInputStream.close();
      if (bufferedImage == null) {
         throw new IllegalStateException("формат аватара не распознан");
      } else {
         int width = bufferedImage.getWidth();
         int height = bufferedImage.getHeight();
         NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               nativeImage.setPixel(j, i, bufferedImage.getRGB(j, i));
            }
         }

         return nativeImage;
      }
   }

   private static void method2135() throws InterruptedException {
      long now = System.currentTimeMillis();
      long millis;
      synchronized (CloudAvatarCache.class) {
         millis = Math.max(0L, longValue2 - now);
         longValue2 = Math.max(now, longValue2) + 120L;
      }

      if (millis > 0L) {
         Thread.sleep(millis);
      }
   }

   private static void method2136(String string, NativeImage image) {
      DynamicTexture dynamicTexture = null;
      boolean flag = false;

      try {
         Identifier location = Identifier.fromNamespaceAndPath("zenith", "cloud_avatar/" + string.toLowerCase(Locale.ROOT));
         long l = (long)image.getWidth() * image.getHeight() * 4L;
         dynamicTexture = new DynamicTexture(() -> "zenith", image);
         Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);
         flag = true;
         method2137(string, location, l);
      } catch (Throwable e) {
      } finally {
         if (!flag) {
            if (dynamicTexture != null) {
               dynamicTexture.close();
            } else {
               image.close();
            }
         }

         items.remove(string);
      }
   }

   private static void method2137(String string, Identifier location, long l) {
      ArrayList<Identifier> ids = new ArrayList();
      long now = System.currentTimeMillis();
      synchronized (linkedHashMap) {
         CloudAvatarCache.Entry entry = linkedHashMap.put(string, new CloudAvatarCache.Entry(location, l, now));
         longValue = longValue + (l - (entry == null ? 0L : entry.bytes()));
         var iterator = linkedHashMap.entrySet().iterator();

         while (longValue > 12582912L && linkedHashMap.size() > 1 && iterator.hasNext()) {
            Map.Entry entry2 = (Map.Entry)iterator.next();
            if (!((String)entry2.getKey()).equals(string) && now - ((CloudAvatarCache.Entry)entry2.getValue()).longValue2 >= 2000L) {
               longValue = longValue - ((CloudAvatarCache.Entry)entry2.getValue()).bytes();
               ids.add(((CloudAvatarCache.Entry)entry2.getValue()).getIdentifier());
               iterator.remove();
            }
         }
      }

      for (Identifier id : ids) {
         Minecraft.getInstance().getTextureManager().release(id);
      }
   }

   private static byte[] method2138(String string) throws Exception {
      if (System.currentTimeMillis() < longValue3) {
         throw new IllegalStateException("avatar site backoff");
      } else {
         method2135();
         byte[] bytes = null;
         Exception e = null;

         for (String str : RegionUtil.method2037()) {
            try {
               bytes = download(str + "/user/avatar/" + string);
               e = null;
               break;
            } catch (CloudAvatarCache.Entry2 ex) {
               throw ex;
            } catch (Exception ex) {
               e = ex;
            }
         }

         if (e != null) {
            longValue3 = System.currentTimeMillis() + 10000L;
            throw e;
         } else {
            return bytes;
         }
      }
   }

   private static byte[] download(String spec) throws Exception {
      HttpURLConnection openConnection = (HttpURLConnection)new URL(spec).openConnection();
      openConnection.setConnectTimeout(5000);
      openConnection.setReadTimeout(5000);
      openConnection.setRequestProperty("User-Agent", "ZenithDLC");
      openConnection.setRequestProperty("Accept", "image/*");

      Object obj;
      try {
         int responseCode = openConnection.getResponseCode();
         if (responseCode == 404) {
            throw new CloudAvatarCache.Entry2();
         }

         if (responseCode != 200) {
            throw new IllegalStateException("avatar http " + responseCode);
         }

         String contentType = openConnection.getContentType();
         if (contentType == null || contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            try (InputStream inputStream = openConnection.getInputStream()) {
               return inputStream.readNBytes(2097152);
            }
         }

         obj = null;
      } finally {
         openConnection.disconnect();
      }

      return (byte[])obj;
   }

   private static Path cacheDir() {
      return Zenith.file.toPath().resolve("avatars");
   }

   private static final class Entry {
      private final Identifier identifier;
      private final long longValue;
      private long longValue2;

      private Entry(Identifier identifier, long longValue, long now) {
         this.identifier = identifier;
         this.longValue = longValue;
         this.longValue2 = now;
      }

      public Identifier getIdentifier() {
         return this.identifier;
      }

      public long bytes() {
         return this.longValue;
      }
   }

   private static final class Entry2 extends Exception {
   }
}

package org.zenith.utility.config;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
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
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.misc.ImageLoader;
import org.zenith.utility.render.TextureFilterUtils;

public final class ChatImageCache {
   private static final String text = "zenith";
   private static final String text2 = "chat_image/";
   private static final int CONST_intValue = 1048576;
   private static final long CONST_longValue = 30000L;
   private static final long CONST_longValue2 = 60L;
   private static final long CONST_longValue3 = 100663296L;
   private static final long CONST_longValue4 = 201326592L;
   private static final long CONST_longValue5 = 10000L;
   private static final LinkedHashMap<String, ChatImageCache.Entry> linkedHashMap = new LinkedHashMap<>(32, 0.75F, true);
   private static final Set<String> items = ConcurrentHashMap.newKeySet();
   private static final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> items3 = new ConcurrentHashMap<>();
   private static final Map<Identifier, Long> items4 = new ConcurrentHashMap<>();
   private static long longValue;
   private static long longValue2;
   private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
      1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(128), task -> {
         Thread thread = new Thread(task, "zenith-chat-image");
         thread.setDaemon(true);
         return thread;
      }
   );
   private static final int CONST_intValue2 = 2048;

   private ChatImageCache() {
   }

   public static Identifier get(String key) {
      if (key != null && !key.isBlank() && key.startsWith("https://")) {
         synchronized (linkedHashMap) {
            ChatImageCache.Entry entry = linkedHashMap.get(key);
            if (entry != null) {
               entry.longValue2 = System.currentTimeMillis();
               return entry.getIdentifier();
            }
         }

         if (items2.contains(key)) {
            return null;
         } else {
            Long l = items3.get(key);
            if (l != null && System.currentTimeMillis() < l) {
               return null;
            } else if (!items.add(key)) {
               return null;
            } else {
               try {
                  threadPoolExecutor.execute(() -> load(key));
               } catch (RejectedExecutionException e) {
                  items.remove(key);
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static void load(String key) {
      if (items2.size() > 4096) {
         items2.clear();
      }

      try {
         byte[] buf = method1494(key);
         boolean flag = buf != null;
         if (buf == null) {
            method1499();
            buf = method1493(key);
         }

         if (buf == null || buf.length == 0) {
            items2.add(key);
            items.remove(key);
            return;
         }

         if (buf.length >= 1048576) {
            items2.add(key);
            items.remove(key);
            return;
         }

         if (!method1490(buf)) {
            if (flag) {
               method1496(key);
            }

            items2.add(key);
            items.remove(key);
            return;
         }

         NativeImage image;
         try {
            image = method1491(buf);
         } catch (Exception e) {
            if (flag && method1496(key)) {
               items.remove(key);
               return;
            }

            throw e;
         }

         if (!flag) {
            method1495(key, buf);
         }

         Minecraft.getInstance().execute(() -> method1486(key, image));
      } catch (Throwable e) {
         if (items3.size() > 4096) {
            items3.clear();
         }

         items3.put(key, System.currentTimeMillis() + 30000L);
         items.remove(key);
      }
   }

   private static void method1486(String key, NativeImage image) {
      DynamicTexture dynamicTexture = null;
      boolean flag = false;

      try {
         Identifier location = Identifier.fromNamespaceAndPath("zenith", "chat_image/" + method1498(key));
         long l = (long)image.getWidth() * image.getHeight() * 4L;
         int width = image.getWidth();
         int height = image.getHeight();
         dynamicTexture = new DynamicTexture(() -> "zenith", image);
         Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);
         flag = true;
         TextureFilterUtils.smooth(location);
         items4.put(location, (long)width << 32 | height & 4294967295L);
         method1487(key, location, l);
      } catch (Throwable e) {
      } finally {
         if (!flag) {
            if (dynamicTexture != null) {
               dynamicTexture.close();
            } else {
               image.close();
            }
         }

         items.remove(key);
      }
   }

   private static void method1487(String key, Identifier location, long l) {
      ArrayList<Identifier> ids = new ArrayList();
      long now = System.currentTimeMillis();
      synchronized (linkedHashMap) {
         ChatImageCache.Entry entry = linkedHashMap.put(key, new ChatImageCache.Entry(location, l));
         longValue = longValue + (l - (entry == null ? 0L : entry.bytes()));
         var iterator = linkedHashMap.entrySet().iterator();

         while (longValue > 100663296L && linkedHashMap.size() > 1 && iterator.hasNext()) {
            Map.Entry entry2 = (Map.Entry)iterator.next();
            if (!((String)entry2.getKey()).equals(key) && (now - ((ChatImageCache.Entry)entry2.getValue()).longValue2 >= 10000L || longValue > 201326592L)) {
               longValue = longValue - ((ChatImageCache.Entry)entry2.getValue()).bytes();
               ids.add(((ChatImageCache.Entry)entry2.getValue()).getIdentifier());
               iterator.remove();
            }
         }
      }

      for (Identifier id : ids) {
         items4.remove(id);
         Minecraft.getInstance().getTextureManager().release(id);
      }
   }

   public static int method1488(Identifier texture) {
      Long l = texture == null ? null : items4.get(texture);
      return l == null ? 0 : (int)(l >>> 32);
   }

   public static int method1489(Identifier texture) {
      Long l = texture == null ? null : items4.get(texture);
      return l == null ? 0 : (int)(l & 4294967295L);
   }

   private static boolean method1490(byte[] buf) {
      try {
         int[] ints = ImageLoader.method231(buf);
         return ints[0] > 0 && ints[1] > 0 && ints[0] <= 2048 && ints[1] <= 2048;
      } catch (Exception e) {
         return false;
      }
   }

   private static NativeImage method1491(byte[] buf) throws Exception {
      try {
         NativeImage nativeImage;
         try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf)) {
            nativeImage = NativeImage.read(byteArrayInputStream);
         }

         return nativeImage;
      } catch (Exception e) {
         return method1492(buf);
      }
   }

   private static NativeImage method1492(byte[] buf) throws Exception {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);

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
         throw new IllegalStateException("формат картинки не распознан");
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

   private static byte[] method1493(String key) throws Exception {
      URL url = new URL(key);
      if ("https".equalsIgnoreCase(url.getProtocol()) && RegionUtil.method2044(url.getHost())) {
         HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
         openConnection.setInstanceFollowRedirects(false);
         openConnection.setConnectTimeout(6000);
         openConnection.setReadTimeout(10000);
         openConnection.setRequestProperty("User-Agent", "Zenith");

         byte[] nBytes;
         try {
            int responseCode = openConnection.getResponseCode();
            if (responseCode == 404 || responseCode == 410) {
               return null;
            }

            if (responseCode != 200) {
               throw new IllegalStateException("сайт ответил " + responseCode);
            }

            try (InputStream inputStream = openConnection.getInputStream()) {
               nBytes = inputStream.readNBytes(1048576);
            }
         } finally {
            openConnection.disconnect();
         }

         return nBytes;
      } else {
         return null;
      }
   }

   private static byte[] method1494(String key) {
      try {
         Path path = method1497().resolve(method1498(key) + ".img");
         if (Files.isRegularFile(path)) {
            byte[] allBytes = Files.readAllBytes(path);
            return allBytes.length == 0 ? null : allBytes;
         }
      } catch (Exception e) {
      }

      return null;
   }

   private static void method1495(String key, byte[] buf) {
      try {
         Path dir = method1497();
         Files.createDirectories(dir);
         String str = method1498(key) + ".img";
         Path target = dir.resolve(str);
         Path source = dir.resolve(str + ".tmp");
         Files.write(source, buf);

         try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception e) {
      }
   }

   private static boolean method1496(String key) {
      try {
         Files.deleteIfExists(method1497().resolve(method1498(key) + ".img"));
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   private static Path method1497() {
      return Zenith.file.toPath().resolve("chat-images");
   }

   private static String method1498(String key) {
      try {
         MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
         byte[] digest = messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
         byte[] bytes = new byte[8];
         System.arraycopy(digest, 0, bytes, 0, 8);
         return HexFormat.of().formatHex(bytes).toLowerCase(Locale.ROOT);
      } catch (Exception e) {
         return Integer.toHexString(key.hashCode());
      }
   }

   private static void method1499() throws InterruptedException {
      long now = System.currentTimeMillis();
      long millis;
      synchronized (ChatImageCache.class) {
         millis = Math.max(0L, longValue2 - now);
         longValue2 = Math.max(now, longValue2) + 60L;
      }

      if (millis > 0L) {
         Thread.sleep(millis);
      }
   }

   private static final class Entry {
      private final Identifier identifier;
      private final long longValue;
      private volatile long longValue2 = System.currentTimeMillis();

      private Entry(Identifier identifier, long longValue) {
         this.identifier = identifier;
         this.longValue = longValue;
      }

      private Identifier getIdentifier() {
         return this.identifier;
      }

      private long bytes() {
         return this.longValue;
      }
   }
}

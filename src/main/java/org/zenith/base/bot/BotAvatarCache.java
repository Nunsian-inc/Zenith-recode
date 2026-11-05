package org.zenith.base.bot;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class BotAvatarCache {
   private static final String NAMESPACE = "zenith";
   private static final String PREFIX = "bot_avatar/";
   private static final String AVATAR_URL = "https://minotar.net/helm/%s/64.png";
   private static final Map<String, Identifier> nameToId = new ConcurrentHashMap<>();
   private static final Map<String, Long> loading = new ConcurrentHashMap<>();
   private static final AtomicLong generation = new AtomicLong();
   private static final AtomicLong requestIds = new AtomicLong();
   private static final long RETRY_MIN_MS = 30000L;
   private static final long RETRY_MAX_MS = 300000L;
   private static final Map<String, BotAvatarCache.Retry> retryAfter = new ConcurrentHashMap<>();
   private static final ThreadPoolExecutor loader = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(128), task -> {
      Thread thread = new Thread(task, "bot-avatar-loader");
      thread.setDaemon(true);
      return thread;
   });

   private BotAvatarCache() {
   }

   public static Identifier prewarm(String selected) {
      if (selected != null && !selected.isEmpty()) {
         String lowerCase = selected.toLowerCase(Locale.ROOT);
         Identifier id = nameToId.get(lowerCase);
         if (id != null) {
            return id;
         } else {
            requestLoad(selected, lowerCase);
            return null;
         }
      } else {
         return null;
      }
   }

   private static void requestLoad(String selected, String lowerCase) {
      BotAvatarCache.Retry retry = retryAfter.get(lowerCase);
      if (retry == null || System.currentTimeMillis() >= retry.at()) {
         long l = generation.get();
         long incrementAndGet = requestIds.incrementAndGet();
         if (loading.putIfAbsent(lowerCase, incrementAndGet) == null) {
            Runnable command = () -> {
               try {
                  byte[] buf = fetch(selected, lowerCase);
                  if (buf == null || buf.length == 0) {
                     markFailed(lowerCase, l);
                     loading.remove(lowerCase, incrementAndGet);
                     return;
                  }

                  NativeImage var7x;
                  try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf)) {
                     var7x = NativeImage.read(byteArrayInputStream);
                  }

                  Minecraft minecraft = Minecraft.getInstance();

                  try {
                     minecraft.execute(() -> install(lowerCase, var7x, l, incrementAndGet));
                  } catch (Throwable e) {
                     var7x.close();
                     loading.remove(lowerCase, incrementAndGet);
                  }
               } catch (Throwable e) {
                  markFailed(lowerCase, l);
                  loading.remove(lowerCase, incrementAndGet);
               }
            };

            try {
               loader.execute(command);
            } catch (RejectedExecutionException e) {
               loading.remove(lowerCase, incrementAndGet);
            }
         }
      }
   }

   private static void install(String lowerCase, NativeImage image, long l, long incrementAndGet) {
      DynamicTexture dynamicTexture = null;
      boolean flag = false;

      try {
         if (l == generation.get()) {
            Minecraft minecraft = Minecraft.getInstance();
            Identifier location = Identifier.fromNamespaceAndPath("zenith", "bot_avatar/" + Integer.toHexString(lowerCase.hashCode()));
            dynamicTexture = new DynamicTexture(() -> "zenith", image);
            minecraft.getTextureManager().register(location, dynamicTexture);
            flag = true;
            nameToId.put(lowerCase, location);
            retryAfter.remove(lowerCase);
            return;
         }
      } catch (Throwable e) {
         return;
      } finally {
         if (!flag) {
            if (dynamicTexture != null) {
               dynamicTexture.close();
            } else {
               image.close();
            }
         }

         loading.remove(lowerCase, incrementAndGet);
      }
   }

   private static void markFailed(String lowerCase, long l) {
      if (l == generation.get()) {
         BotAvatarCache.Retry retry = retryAfter.get(lowerCase);
         long backoff = retry == null ? 30000L : Math.min(retry.backoff() * 2L, 300000L);
         retryAfter.put(lowerCase, new BotAvatarCache.Retry(System.currentTimeMillis() + backoff, backoff));
      }
   }

   private static byte[] fetch(String selected, String lowerCase) throws Exception {
      Path path = cacheDir().resolve(Integer.toHexString(lowerCase.hashCode()) + ".png");

      try {
         if (Files.isRegularFile(path)) {
            byte[] allBytes = Files.readAllBytes(path);
            if (allBytes.length > 0) {
               return allBytes;
            }
         }
      } catch (Exception e) {
      }

      byte[] bytes = download(String.format("https://minotar.net/helm/%s/64.png", selected));
      if (bytes != null && bytes.length > 0) {
         try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
         } catch (Exception e) {
         }
      }

      return bytes;
   }

   private static Path cacheDir() {
      return Minecraft.getInstance().gameDirectory.toPath().resolve("zenith").resolve("bot_avatars");
   }

   private static byte[] download(String spec) throws Exception {
      HttpURLConnection openConnection = (HttpURLConnection)new URL(spec).openConnection();
      openConnection.setConnectTimeout(5000);
      openConnection.setReadTimeout(5000);
      openConnection.setRequestProperty("User-Agent", "Zenith");

      byte[] allBytes;
      try (InputStream inputStream = openConnection.getInputStream()) {
         allBytes = inputStream.readAllBytes();
      } finally {
         openConnection.disconnect();
      }

      return allBytes;
   }

   public static void clear() {
      generation.incrementAndGet();
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.getTextureManager() != null) {
         for (Identifier location : nameToId.values()) {
            try {
               minecraft.getTextureManager().release(location);
            } catch (Throwable e) {
            }
         }
      }

      nameToId.clear();
      loading.clear();
      retryAfter.clear();
   }

   private static final class Retry {
      private final long at;
      private final long backoff;

      public long at() {
         return this.at;
      }

      public long backoff() {
         return this.backoff;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Retry other)) return false;
         return java.util.Objects.equals(this.at, other.at) && java.util.Objects.equals(this.backoff, other.backoff);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(at, backoff);
      }

      @Override
      public String toString() {
         return "Retry[" + "at=" + this.at + ", " + "backoff=" + this.backoff + "]";
      }

      private Retry(long at, long backoff) {
         this.at = at;
         this.backoff = backoff;
      }
   }
}

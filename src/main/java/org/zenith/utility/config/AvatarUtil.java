package org.zenith.utility.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.avatar.UserData;
import org.zenith.base.figura.avatar.local.LocalAvatarLoader;

public final class AvatarUtil {
   private static final AtomicLong atomicLong = new AtomicLong();
   private static final ConcurrentHashMap<AvatarUtil.Data2, AvatarUtil.Data> concurrentHashMap = new ConcurrentHashMap<>();
   private static final Map<Path, Long> items = new ConcurrentHashMap<>();
   private static final long CONST_longValue = 60000L;
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "Zenith Figura Loader");
      thread.setDaemon(true);
      thread.setPriority(Math.max(1, 4));
      return thread;
   });
   private static final String text = "cosmetics/";

   private AvatarUtil() {
   }

   public static void method7237(Path path) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.player != null) {
         method7240(minecraft.player.getUUID(), path);
      }
   }

   public static UserData method7238(Path path, UUID id) {
      Path path2 = method7260(path);
      if (path2 != null && id != null) {
         UserData userData = new UserData(id);
         LocalAvatarLoader.loadAvatar(path2, userData);
         return userData.getAvatars().isEmpty() ? null : userData;
      } else {
         return null;
      }
   }

   public static void method7239(UUID uUID, String text) {
      method7240(uUID, method7258(text));
   }

   public static void method7240(UUID uUID, Path path) {
      if (uUID != null) {
         Path path2 = method7260(path);
         if (path2 != null) {
            method7266(uUID);
            method7262(uUID, path2, AvatarLayer.BODY, AvatarUtil.Type.REPLACE_ALL);
         }
      }
   }

   public static void method7241(UUID uUID, String string) {
      method7242(uUID, method7258(string));
   }

   public static void method7242(UUID uUID, Path path) {
      if (uUID != null) {
         Path path2 = method7260(path);
         if (path2 != null) {
            method7262(uUID, path2, AvatarLayer.BODY, AvatarUtil.Type.APPEND);
         }
      }
   }

   public static void method7243(UUID key, String body) {
      method7244(key, method7258(body));
   }

   public static void method7244(UUID key, Path path) {
      method7247(key, path, AvatarLayer.BODY);
   }

   public static void method7245(UUID key, String body) {
      method7246(key, method7258(body));
   }

   public static void method7246(UUID key, Path path) {
      method7247(key, path, AvatarLayer.WEAPON);
   }

   private static void method7247(UUID key, Path path, AvatarLayer avatarLayer) {
      if (key != null) {
         Path path2 = method7260(path);
         if (path2 != null) {
            method7262(key, path2, avatarLayer, AvatarUtil.Type.REPLACE_LAYER);
         }
      }
   }

   public static boolean method7248(UUID owner) {
      return AvatarManager.contains(owner);
   }

   public static boolean method7249(UUID uuid) {
      return AvatarManager.contains(uuid, AvatarLayer.BODY);
   }

   public static boolean method7250(UUID uuid) {
      return AvatarManager.contains(uuid, AvatarLayer.WEAPON);
   }

   public static void method7251(UUID uUID) {
      if (uUID != null) {
         method7266(uUID);
         method7261(() -> AvatarManager.clear(uUID));
      }
   }

   public static void method7252(UUID uuid) {
      method7254(uuid, AvatarLayer.BODY);
   }

   public static void method7253(UUID uuid) {
      method7254(uuid, AvatarLayer.WEAPON);
   }

   private static void method7254(UUID uuid, AvatarLayer layer) {
      if (uuid != null) {
         method7265(uuid, layer);
         method7261(() -> AvatarManager.clear(uuid, layer));
      }
   }

   public static void method7255(UUID uuid) {
      method7265(uuid, AvatarLayer.BODY);
   }

   public static void method7256(UUID uuid) {
      method7265(uuid, AvatarLayer.WEAPON);
   }

   public static String method7257(Path path) {
      if (path == null) {
         return "";
      } else {
         String str = BuiltinCosmeticsPath.method4450(path);
         if (!str.isEmpty()) {
            return str;
         } else {
            Path path2 = Zenith.file.toPath().toAbsolutePath().normalize();
            Path path3 = path.toAbsolutePath().normalize();
            return path3.startsWith(path2) ? path2.relativize(path3).toString().replace('\\', '/') : "";
         }
      }
   }

   public static Path method7258(String string) {
      if (string != null && !string.isBlank()) {
         Path path = BuiltinCosmeticsPath.method4451(string);
         if (path != null) {
            return path;
         } else {
            Path path2 = Zenith.file.toPath().toAbsolutePath().normalize();
            Path path3 = path2.resolve(string).normalize();
            if (!path3.startsWith(path2)) {
               return null;
            } else {
               return Files.isRegularFile(path3.resolve("avatar.json")) ? path3 : method7259(string, path3);
            }
         }
      } else {
         return null;
      }
   }

   private static Path method7259(String string, Path path) {
      String str = string.startsWith("cosmetics/") ? string.substring("cosmetics/".length()) : string;
      Path path2 = BuiltinCosmeticsPath.method4451("builtin/" + str);
      return path2 != null && Files.isRegularFile(path2.resolve("avatar.json")) ? path2 : path;
   }

   public static Path method7260(Path path) {
      if (path == null) {
         return null;
      } else {
         Path path2 = path.toAbsolutePath().normalize();
         return Files.isDirectory(path2) && Files.isRegularFile(path2.resolve("avatar.json")) ? path2 : null;
      }
   }

   private static void method7261(Runnable command) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && !minecraft.isSameThread()) {
         minecraft.execute(command);
      } else {
         command.run();
      }
   }

   private static void method7262(UUID uUID, Path path, AvatarLayer avatarLayer, AvatarUtil.Type type) {
      Long l = items.get(path);
      if (l == null || System.currentTimeMillis() >= l) {
         AvatarUtil.Data2 key = new AvatarUtil.Data2(uUID, avatarLayer);
         AvatarUtil.Data data = concurrentHashMap.get(key);
         if (data == null || !data.getPath().equals(path) || data.getType() != type) {
            AvatarUtil.Data data2 = new AvatarUtil.Data(path, type, atomicLong.incrementAndGet());
            concurrentHashMap.put(key, data2);
            executorService.execute(() -> method7263(key, data2));
         }
      }
   }

   private static void method7263(AvatarUtil.Data2 key, AvatarUtil.Data data) {
      if (concurrentHashMap.get(key) == data) {
         UserData userData = new UserData(key.getUUID());

         try {
            LocalAvatarLoader.loadAvatar(data.getPath(), userData, key.getAvatarLayer());
         } catch (Throwable e) {
            FiguraMod.LOGGER.error("Failed to prepare local avatar {} for {}", new Object[]{data.getPath(), key.getUUID(), e});
         }

         boolean anyMatch = userData.getAvatars(key.getAvatarLayer()).stream().anyMatch(var0x -> var0x.loaded && var0x.renderer != null);
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft == null) {
            concurrentHashMap.remove(key, data);
         } else {
            minecraft.execute(() -> method7264(key, data, userData, anyMatch));
         }
      }
   }

   private static void method7264(AvatarUtil.Data2 key, AvatarUtil.Data data, UserData userData, boolean anyMatch) {
      if (concurrentHashMap.get(key) == data && anyMatch) {
         if (data.getType() == AvatarUtil.Type.REPLACE_ALL) {
            AvatarManager.clear(key.getUUID());
         }

         UserData orCreate = AvatarManager.getOrCreate(key.getUUID());
         if (data.getType() == AvatarUtil.Type.REPLACE_LAYER) {
            orCreate.clear(key.getAvatarLayer());
         }

         items.remove(data.getPath());
         var avatars = userData.getAvatars();

         Avatar avatar;
         while ((avatar = (Avatar)avatars.poll()) != null) {
            orCreate.getAvatars().offer(avatar);
         }

         concurrentHashMap.remove(key, data);
      } else {
         concurrentHashMap.remove(key, data);
         userData.clear();
         if (!anyMatch) {
            items.put(data.getPath(), System.currentTimeMillis() + 60000L);
            FiguraMod.LOGGER.warn("Ignoring unusable local avatar {} for {}", data.getPath(), key.getUUID());
         }
      }
   }

   private static void method7265(UUID uuid, AvatarLayer layer) {
      if (uuid != null) {
         concurrentHashMap.remove(new AvatarUtil.Data2(uuid, layer));
      }
   }

   private static void method7266(UUID uUID) {
      if (uUID != null) {
         method7265(uUID, AvatarLayer.BODY);
         method7265(uUID, AvatarLayer.WEAPON);
      }
   }

   private static final class Data {
      private final Path path;
      private final AvatarUtil.Type type;
      private final long longValue;

      public Path path() {
         return this.path;
      }

      public AvatarUtil.Type type() {
         return this.type;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.path, other.path) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(path, type, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "path=" + this.path + ", " + "type=" + this.type + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(Path path, AvatarUtil.Type type, long longValue) {
         this.path = path;
         this.type = type;
         this.longValue = longValue;
      }

      public Path getPath() {
         return this.path;
      }

      public AvatarUtil.Type getType() {
         return this.type;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final UUID uUID;
      private final AvatarLayer avatarLayer;

      public UUID uUID() {
         return this.uUID;
      }

      public AvatarLayer avatarLayer() {
         return this.avatarLayer;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.avatarLayer, other.avatarLayer);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, avatarLayer);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "avatarLayer=" + this.avatarLayer + "]";
      }

      private Data2(UUID uUID, AvatarLayer avatarLayer) {
         this.uUID = uUID;
         this.avatarLayer = avatarLayer;
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public AvatarLayer getAvatarLayer() {
         return this.avatarLayer;
      }
   }

   private static enum Type {
      REPLACE_ALL,
      REPLACE_LAYER,
      APPEND;
   }
}

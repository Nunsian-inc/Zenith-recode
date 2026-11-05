package org.zenith.client.screens.scripts;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;
import org.zenith.client.scripting.Script;
import org.zenith.utility.misc.ImageLoader;

public final class ScriptIcons {
   private static final Map<String, ScriptIcons.Entry> CACHE = new HashMap<>();
   private static final Map<String, Long> CHECKED = new HashMap<>();

   private ScriptIcons() {
   }

   public static Identifier get(Script script) {
      Path path = script.method1412();
      if (path == null) {
         return null;
      } else {
         String id = script.getId();
         long now = System.currentTimeMillis();
         ScriptIcons.Entry entry = CACHE.get(id);
         Long l = CHECKED.get(id);
         if (l != null && now - l < 1000L) {
            return entry == null ? null : entry.id();
         } else {
            CHECKED.put(id, now);

            long millis;
            try {
               millis = Files.isRegularFile(path) ? Files.getLastModifiedTime(path).toMillis() : 0L;
            } catch (Exception e) {
               millis = 0L;
            }

            if (millis == 0L) {
               drop(id);
               return null;
            } else if (entry != null && entry.stamp() == millis) {
               return entry.id();
            } else {
               drop(id);

               try {
                  if (Files.size(path) > 4194304L) {
                     return reject(id, millis);
                  } else {
                     byte[] allBytes = Files.readAllBytes(path);
                     int[] ints = ImageLoader.method231(allBytes);
                     if (ints[0] <= 1024 && ints[1] <= 1024) {
                        NativeImage image = NativeImage.read(new ByteArrayInputStream(allBytes));
                        Identifier location = Zenith.method1501("script_icon/" + id.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_") + "_" + millis);
                        Minecraft.getInstance()
                           .getTextureManager()
                           .register(location, new DynamicTexture(() -> "zenith-script-icon", image));
                        CACHE.put(id, new ScriptIcons.Entry(millis, location));
                        return location;
                     } else {
                        return reject(id, millis);
                     }
                  }
               } catch (Exception e) {
                  return reject(id, millis);
               }
            }
         }
      }
   }

   private static Identifier reject(String id, long millis) {
      CACHE.put(id, new ScriptIcons.Entry(millis, null));
      return null;
   }

   public static void invalidate(Script script) {
      CHECKED.remove(script.getId());
   }

   private static void drop(String id) {
      ScriptIcons.Entry entry = CACHE.remove(id);
      if (entry != null && entry.id() != null) {
         Minecraft.getInstance().getTextureManager().release(entry.id());
      }
   }

   private static final class Entry {
      private final long stamp;
      private final Identifier id;

      public long stamp() {
         return this.stamp;
      }

      public Identifier id() {
         return this.id;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Entry other)) return false;
         return java.util.Objects.equals(this.stamp, other.stamp) && java.util.Objects.equals(this.id, other.id);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(stamp, id);
      }

      @Override
      public String toString() {
         return "Entry[" + "stamp=" + this.stamp + ", " + "id=" + this.id + "]";
      }

      private Entry(long stamp, Identifier id) {
         this.stamp = stamp;
         this.id = id;
      }
   }
}

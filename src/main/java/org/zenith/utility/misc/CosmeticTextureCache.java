package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;

public final class CosmeticTextureCache implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final byte[] CONST_byteValues = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
   private final Map<String, Identifier> items = new HashMap<>();
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "Nexus-Cosmetics-PNG");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });

   public Identifier method2616(String str, byte[] byteValue) {
      Identifier id = this.items.get(str);
      if (id != null) {
         return id;
      } else if (byteValue == null || byteValue.length == 0) {
         return null;
      } else if (!method2620(byteValue)) {
         logger.warn("Косметика: текстура {} не PNG ({} байт)", str, byteValue.length);
         return null;
      } else {
         Identifier location = Identifier.fromNamespaceAndPath("zenith", "cosmetic/" + str);
         mc.getTextureManager().register(location, method2619(new NativeImage(1, 1, true)));
         this.items.put(str, location);
         this.executorService.execute(() -> this.method2617(str, location, byteValue));
         return location;
      }
   }

   private void method2617(String str, Identifier location, byte[] byteValue) {
      NativeImage nativeImage;
      try {
         nativeImage = NativeImage.read(byteValue);
      } catch (RuntimeException | IOException e) {
         logger.warn("Косметика: текстура {} не читается: {}", str, e.toString());
         return;
      }

      mc.execute(() -> this.method2618(str, location, nativeImage));
   }

   private void method2618(String str, Identifier location, NativeImage nativeImage) {
      if (!location.equals(this.items.get(str))) {
         nativeImage.close();
      } else {
         mc.getTextureManager().register(location, method2619(nativeImage));
      }
   }

   private static DynamicTexture method2619(NativeImage nativeImage) {
      return new DynamicTexture(() -> "zenith", nativeImage) {
         {
            this.sampler = RenderSystem.getSamplerCache()
               .getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, false);
         }
      };
   }

   private static boolean method2620(byte[] byteValue) {
      if (byteValue.length < CONST_byteValues.length) {
         return false;
      } else {
         for (int i = 0; i < CONST_byteValues.length; i++) {
            if (byteValue[i] != CONST_byteValues[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public Identifier method2621(String str) {
      return this.items.get(str);
   }

   public void method2622(String anObject) {
      String prefix = anObject + "/";
      ArrayList<String> strings = new ArrayList(2);

      for (String str : this.items.keySet()) {
         if (str.equals(anObject) || str.startsWith(prefix)) {
            strings.add(str);
         }
      }

      for (String str : strings) {
         Identifier location = this.items.remove(str);
         if (location != null) {
            mc.getTextureManager().release(location);
         }
      }
   }

   public static String method2623(String str, String str2) {
      if (str2 != null && !str2.isBlank()) {
         StringBuilder builder = new StringBuilder(str2.length());

         for (int i = 0; i < str2.length(); i++) {
            char lowerCase = Character.toLowerCase(str2.charAt(i));
            builder.append((lowerCase < 'a' || lowerCase > 'z') && (lowerCase < '0' || lowerCase > '9') && lowerCase != '_' && lowerCase != '.' && lowerCase != '-' ? '_' : lowerCase);
         }

         return str + "/" + builder;
      } else {
         return str;
      }
   }

   public void releaseAll() {
      for (String anObject : new ArrayList<>(this.items.keySet())) {
         this.method2622(anObject);
      }
   }

   public int size() {
      return this.items.size();
   }
}

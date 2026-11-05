package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.TextureTarget;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public class HandShaders {
   private static Minecraft mc = Minecraft.getInstance();
   private static ShaderProgram shaderProgram;
   private static final String text = "sirius_aqua";
   private static final Map<String, ShaderProgram> items = new HashMap<>();
   private static final Set<String> items2 = new HashSet<>();
   private static TextureTarget simpleFramebuffer;
   private static boolean initialized = false;
   private static long longValue;

   public static void method6278() {
      if (!initialized) {
         long now = System.currentTimeMillis();
         if (now < longValue) {
            return;
         }

         try {
            shaderProgram = method6280("sirius_aqua");
            initialized = true;
         } catch (Exception e) {
            longValue = now + 5000L;
            System.err.println("Failed to initialize hand shaders!");
            e.printStackTrace();
         }
      }
   }

   public static ShaderProgram method6279(String anObject) {
      if (anObject == null || anObject.isBlank()) {
         return shaderProgram;
      } else if (items2.contains(anObject)) {
         return shaderProgram;
      } else {
         try {
            return method6280(anObject);
         } catch (Exception e) {
            items2.add(anObject);
            System.err.println("Failed to initialize hand shader: " + anObject);
            e.printStackTrace();
            return shaderProgram;
         }
      }
   }

   private static ShaderProgram method6280(String anObject) {
      ShaderProgram shaderProgram2 = items.get(anObject);
      if (shaderProgram2 == null) {
         shaderProgram2 = new ShaderProgram("hand", anObject, "smoke");
         items.put(anObject, shaderProgram2);
      }

      if ("sirius_aqua".equals(anObject)) {
         shaderProgram = shaderProgram2;
      }

      return shaderProgram2;
   }

   public static void method6281() {
      if (mc != null && mc.getWindow() != null) {
         int width = mc.getWindow().getWidth();
         int height = mc.getWindow().getHeight();
         if (simpleFramebuffer == null || simpleFramebuffer.width != width || simpleFramebuffer.height != height) {
            if (simpleFramebuffer != null) {
               simpleFramebuffer.destroyBuffers();
            }

            simpleFramebuffer = BlitUtils.method6448("zenith hand copy", width, height, true);
         }
      }
   }

   public static void method6282() {
      FramebufferUtils.method5654(ShaderProgram.getCurrent(), ImmediateRenderer.method8014());
   }

   @Generated
   public static ShaderProgram getShaderProgram() {
      return shaderProgram;
   }

   @Generated
   public static TextureTarget getSimpleFramebuffer() {
      return simpleFramebuffer;
   }

   @Generated
   public static boolean isInitialized() {
      return initialized;
   }
}

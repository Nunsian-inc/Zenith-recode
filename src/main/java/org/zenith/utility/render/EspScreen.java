package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class EspScreen {
   private static final String text = "/assets/gl/shaders/esp/esp_screen.vsh";
   private static final String text2 = "/assets/gl/shaders/esp/esp_screen_blur.vsh";
   private static final String text3 = "/assets/gl/shaders/esp/esp_preview_tint.fsh";
   private static final String text4 = "/assets/gl/shaders/esp/esp_blur.fsh";
   private static final String text5 = "/assets/gl/shaders/esp/esp_glow.fsh";
   private static TextureTarget simpleFramebuffer;
   private static TextureTarget simpleFramebuffer2;
   private static TextureTarget simpleFramebuffer3;
   private static ShaderProgram shaderProgram;
   private static ShaderProgram shaderProgram2;
   private static ShaderProgram shaderProgram3;
   private static boolean initialized;
   private static boolean flag;
   private static boolean flag2;
   private static final long CONST_longValue = 2000L;
   private static String text6;
   private static long longValue;
   private static boolean flag3;

   private EspScreen() {
   }

   public static void method6551(MenuDrawContext customDrawContext, Runnable runnable) {
      flag3 = false;
      if (method6560()) {
         RoundedRectBatch.method2935();
         customDrawContext.draw();
         EspScreen.Data data = EspScreen.Data.method6568();
         if (data.intValue3 > 0 && data.intValue4 > 0) {
            EspScreen.Entry entry = EspScreen.Entry.method6569();

            try {
               method6561(data.intValue3, data.intValue4);
               BlitUtils.method6456(simpleFramebuffer, 0);
               ImmediateRenderer.method8013(simpleFramebuffer);
               RenderState.method1903(true, true, true, true);
               RenderState.method1882(true);
               RenderState.method1878();
               Minecraft minecraft = Minecraft.getInstance();
               RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget;
               minecraft.gameRenderer.mainRenderTarget = simpleFramebuffer;

               try {
                  runnable.run();
               } finally {
                  minecraft.gameRenderer.mainRenderTarget = mainRenderTarget;
                  ImmediateRenderer.method8013(null);
               }

               flag3 = true;
            } catch (Exception e) {
               method6567(e);
            } finally {
               method6564(data);
               entry.restore();
            }
         } else {
            method6566("no render target (" + data.intValue3 + "x" + data.intValue4 + ")");
         }
      }
   }

   public static void method6552(MenuDrawContext customDrawContext, float floatValue, float y, float width, float height, float previewAlpha) {
      if (!flag3) {
         method6566("mask was not captured this frame");
      } else if (method6560()) {
         EspScreen.Data data = EspScreen.Data.method6568();
         EspScreen.Entry entry = EspScreen.Entry.method6569();

         try {
            method6555(previewAlpha, method6553(customDrawContext, floatValue, y, width, height));
            entry.restore();
            method6556(customDrawContext, data, floatValue, y, width, height, previewAlpha);
         } catch (Exception e) {
            method6567(e);
         } finally {
            flag3 = false;
            method6564(data);
            entry.restore();
         }
      }
   }

   public static void release() {
      flag3 = false;
      if (simpleFramebuffer != null || simpleFramebuffer2 != null || simpleFramebuffer3 != null) {
         method6565(simpleFramebuffer);
         method6565(simpleFramebuffer2);
         method6565(simpleFramebuffer3);
         simpleFramebuffer = null;
         simpleFramebuffer2 = null;
         simpleFramebuffer3 = null;
      }
   }

   private static int[] method6553(MenuDrawContext customDrawContext, float floatValue, float y, float width, float height) {
      Minecraft minecraft = Minecraft.getInstance();
      float f = Math.max(1, minecraft.getWindow().getGuiScaledWidth());
      float f2 = Math.max(1, minecraft.getWindow().getGuiScaledHeight());
      Matrix4f matrix = MatrixUtils.method663(customDrawContext);
      Vector3f transformPosition = matrix.transformPosition(new Vector3f(floatValue, y, 0.0F));
      Vector3f vec = matrix.transformPosition(new Vector3f(floatValue + width, y + height, 0.0F));
      int n = simpleFramebuffer.width;
      int height2 = simpleFramebuffer.height;
      float f3 = Math.min(transformPosition.x, vec.x) / f * n;
      float f4 = Math.max(transformPosition.x, vec.x) / f * n;
      float f5 = height2 - Math.max(transformPosition.y, vec.y) / f2 * height2;
      float f6 = height2 - Math.min(transformPosition.y, vec.y) / f2 * height2;
      return new int[]{(int)Math.floor(f3), (int)Math.floor(f5), (int)Math.ceil(f4), (int)Math.ceil(f6)};
   }

   private static void method6554(int[] ints, int n, int n2) {
      int width = simpleFramebuffer.width;
      int height = simpleFramebuffer.height;
      int x = Math.max(0, ints[0] - n);
      int y = Math.max(0, ints[1] - n2);
      int n3 = Math.min(width, ints[2] + n);
      int n4 = Math.min(height, ints[3] + n2);
      RenderState.method1900(x, y, Math.max(0, n3 - x), Math.max(0, n4 - y));
   }

   private static void method6555(float previewAlpha, int[] ints) {
      ShaderESPModule shaderESPModule = ShaderESPModule.INSTANCE;
      float f = shaderESPModule.method4593();
      int n = (int)Math.ceil(Math.max(0.0F, f)) + 3;
      int n2 = (int)Math.ceil(Math.max(0.0F, shaderESPModule.method4590())) + 3;
      int n3 = Math.max(n + 3, n2);

      try {
         ImmediateRenderer.method8013(simpleFramebuffer3);
         method6559();
         method6554(ints, n3, n3);
         shaderProgram.method5517();
         shaderProgram.method5518("InSampler", 0);
         shaderProgram.method5521("InSize", simpleFramebuffer.width, simpleFramebuffer.height);
         method6562(shaderProgram, "Tint", shaderESPModule.method4588(), previewAlpha);
         RenderState.method1889(0, simpleFramebuffer.getColorTextureView());
         method6558();
         ImmediateRenderer.method8013(simpleFramebuffer2);
         method6559();
         method6554(ints, 3, n);
         shaderProgram2.method5517();
         shaderProgram2.method5518("InSampler", 0);
         shaderProgram2.method5521("InSize", simpleFramebuffer3.width, simpleFramebuffer3.height);
         shaderProgram2.method5521("BlurDir", 1.0F, 0.0F);
         shaderProgram2.method5519("Radius", f);
         RenderState.method1889(0, simpleFramebuffer3.getColorTextureView());
         method6558();
         ImmediateRenderer.method8013(simpleFramebuffer);
         method6559();
         method6554(ints, 3, 3);
         shaderProgram2.method5521("InSize", simpleFramebuffer2.width, simpleFramebuffer2.height);
         shaderProgram2.method5521("BlurDir", 0.0F, 1.0F);
         RenderState.method1889(0, simpleFramebuffer2.getColorTextureView());
         method6558();
         shaderProgram2.unbind();
      } finally {
         RenderState.method1901();
      }

      ImmediateRenderer.method8013(null);
   }

   private static void method6556(MenuDrawContext customDrawContext, EspScreen.Data data, float floatValue, float y, float width, float height, float previewAlpha) {
      method6563(data);
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1871();
      RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA);
      ShaderESPModule shaderESPModule = ShaderESPModule.INSTANCE;
      shaderProgram3.method5517();
      shaderProgram3.method5518("InSampler", 0);
      shaderProgram3.method5518("GlowSampler", 1);
      shaderProgram3.method5521("InSize", simpleFramebuffer3.width, simpleFramebuffer3.height);
      shaderProgram3.method5519("OutlineWidth", shaderESPModule.method4590());
      shaderProgram3.method5519("GlowStrength", shaderESPModule.method4591());
      shaderProgram3.method5519("FillOpacity", shaderESPModule.method4592());
      shaderProgram3.method5519("Pulse", shaderESPModule.method4594());
      RenderState.method1889(0, simpleFramebuffer3.getColorTextureView());
      RenderState.method1889(1, simpleFramebuffer.getColorTextureView());
      method6557(customDrawContext, floatValue, y, width, height);
      shaderProgram3.unbind();
      RenderState.method1897();
   }

   private static void method6557(MenuDrawContext customDrawContext, float floatValue, float y, float width, float height) {
      Minecraft minecraft = Minecraft.getInstance();
      float f = Math.max(1, minecraft.getWindow().getGuiScaledWidth());
      float f2 = Math.max(1, minecraft.getWindow().getGuiScaledHeight());
      Matrix4f matrix = MatrixUtils.method663(customDrawContext);
      Vector3f transformPosition = matrix.transformPosition(new Vector3f(floatValue, y, 0.0F));
      Vector3f vec = matrix.transformPosition(new Vector3f(floatValue + width, y + height, 0.0F));
      float f3 = transformPosition.x / f * 2.0F - 1.0F;
      float f4 = vec.x / f * 2.0F - 1.0F;
      float f5 = 1.0F - transformPosition.y / f2 * 2.0F;
      float f6 = 1.0F - vec.y / f2 * 2.0F;
      FramebufferUtils.method5655(ShaderProgram.getCurrent(), ImmediateRenderer.method8014(), f3, f6, f4, f5);
   }

   private static void method6558() {
      FramebufferUtils.method5654(ShaderProgram.getCurrent(), ImmediateRenderer.method8014());
   }

   private static void method6559() {
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1872();
      RenderState.method1903(true, true, true, true);
   }

   private static boolean method6560() {
      if (initialized) {
         return true;
      } else if (flag) {
         return false;
      } else {
         try {
            shaderProgram = ShaderProgram.method5512("/assets/gl/shaders/esp/esp_screen.vsh", "/assets/gl/shaders/esp/esp_preview_tint.fsh");
            shaderProgram2 = ShaderProgram.method5512("/assets/gl/shaders/esp/esp_screen_blur.vsh", "/assets/gl/shaders/esp/esp_blur.fsh");
            shaderProgram3 = ShaderProgram.method5512("/assets/gl/shaders/esp/esp_screen.vsh", "/assets/gl/shaders/esp/esp_glow.fsh");
            initialized = true;
         } catch (Exception e) {
            System.err.println("Failed to initialize ShaderESP preview shaders");
            e.printStackTrace();
            flag = true;
         }

         return initialized;
      }
   }

   private static void method6561(int width, int height) {
      if (simpleFramebuffer == null
         || simpleFramebuffer2 == null
         || simpleFramebuffer3 == null
         || simpleFramebuffer.width != width
         || simpleFramebuffer.height != height) {
         release();
         simpleFramebuffer = BlitUtils.method6448("zenith esp preview", width, height, true);
         simpleFramebuffer2 = BlitUtils.method6448("zenith esp preview", width, height, false);
         simpleFramebuffer3 = BlitUtils.method6448("zenith esp preview", width, height, false);
      }
   }

   private static void method6562(ShaderProgram shaderProgram, String key, int intValue, float previewAlpha) {
      shaderProgram.method5523(key, ColorUtils.method6424(intValue), ColorUtils.method6425(intValue), ColorUtils.method6426(intValue), ColorUtils.method6427(intValue) * previewAlpha);
   }

   private static void method6563(EspScreen.Data data) {
      ImmediateRenderer.method8013(null);
   }

   private static void method6564(EspScreen.Data data) {
      ImmediateRenderer.method8013(null);
      RenderState.method1903(true, true, true, true);
      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
      RenderState.method1897();
   }

   private static void method6565(TextureTarget simpleFramebuffer) {
      if (simpleFramebuffer != null) {
         simpleFramebuffer.destroyBuffers();
      }
   }

   private static void method6566(String str) {
      long now = System.currentTimeMillis();
      if (!str.equals(text6) || now - longValue >= 2000L) {
         text6 = str;
         longValue = now;
         System.err.println("ShaderESP preview skipped: " + str);
      }
   }

   private static void method6567(Exception e) {
      if (!flag2) {
         System.err.println("ShaderESP preview render failed");
         e.printStackTrace();
         flag2 = true;
      }
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      private Data(int intValue, int n, int n2, int n3) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.intValue4 = n3;
      }

      private static EspScreen.Data method6568() {
         RenderTarget renderTarget = BlitUtils.method6452();
         return new EspScreen.Data(0, 0, Math.max(1, renderTarget.width), Math.max(1, renderTarget.height));
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int width() {
         return this.intValue3;
      }

      public int height() {
         return this.intValue4;
      }
   }

   private static final class Entry {
      private final boolean flag;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private boolean flag2;

      private Entry(boolean flag, int intValue, int y, int width, int height) {
         this.flag = flag;
         this.intValue = intValue;
         this.intValue2 = y;
         this.intValue3 = width;
         this.intValue4 = height;
      }

      private static EspScreen.Entry method6569() {
         ScissorState scissorStateForRenderTypeDraws = RenderSystem.getScissorStateForRenderTypeDraws();
         EspScreen.Entry entry = new EspScreen.Entry(scissorStateForRenderTypeDraws.enabled(), scissorStateForRenderTypeDraws.x(), scissorStateForRenderTypeDraws.y(), scissorStateForRenderTypeDraws.width(), scissorStateForRenderTypeDraws.height());
         RenderState.method1901();
         return entry;
      }

      private void restore() {
         if (!this.flag2) {
            this.flag2 = true;
            if (this.flag) {
               RenderState.method1900(this.intValue, this.intValue2, this.intValue3, this.intValue4);
            }
         }
      }
   }
}

package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.misc.WeatherUniforms;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class WeatherPreviewUtil {
   private static final String text = "/assets/gl/shaders/weather/weather_preview.vsh";
   private static final String text2 = "/assets/gl/shaders/weather/weather_sky.fsh";
   private static final float CONST_floatValue = 82.0F;
   private static final float CONST_floatValue2 = 12.0F;
   private static ShaderProgram shaderProgram;
   private static boolean flag;
   private static boolean flag2;

   private WeatherPreviewUtil() {
   }

   public static void method255(
      MenuDrawContext customDrawContext, float floatValue, float y, float width, float height, WeatherFlakeChannel weatherFlakeChannel, float f, float f2, float f3, float previewAlpha
   ) {
      if (!(previewAlpha <= 0.004F) && !(width <= 1.0F) && !(height <= 1.0F) && method256()) {
         RoundedRectBatch.method2935();
         customDrawContext.draw();

         try {
            shaderProgram.method5517();
            shaderProgram.method5518("InSampler", 0);
            shaderProgram.method5521("InSize", width, height);
            shaderProgram.method5519("Opacity", previewAlpha);
            float f4 = width / height;
            float f5 = (float)Math.tan(Math.toRadians(82.0) * 0.5);
            WeatherUniforms.method799(
               new WeatherPreviewUtil.Data(shaderProgram), weatherFlakeChannel, f, f2, f3, 0.0, 0.0, 0.0, (float)Math.toRadians(12.0), (float) Math.PI, f5, f4
            );
            RenderState.method1879();
            RenderState.method1882(false);
            RenderState.method1871();
            RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA);
            method257(customDrawContext, floatValue, y, width, height);
         } catch (Exception e) {
            if (!flag2) {
               System.err.println("Weather sky preview render failed");
               e.printStackTrace();
               flag2 = true;
            }
         } finally {
            shaderProgram.unbind();
            method258();
         }
      }
   }

   private static boolean method256() {
      if (shaderProgram != null) {
         return true;
      } else if (flag) {
         return false;
      } else {
         try {
            shaderProgram = ShaderProgram.method5512("/assets/gl/shaders/weather/weather_preview.vsh", "/assets/gl/shaders/weather/weather_sky.fsh");
         } catch (Exception e) {
            System.err.println("Failed to initialize weather sky preview shader");
            e.printStackTrace();
            flag = true;
         }

         return shaderProgram != null;
      }
   }

   private static void method257(MenuDrawContext customDrawContext, float floatValue, float y, float width, float height) {
      Minecraft minecraft = Minecraft.getInstance();
      float f = Math.max(1, minecraft.getWindow().getGuiScaledWidth());
      float f2 = Math.max(1, minecraft.getWindow().getGuiScaledHeight());
      Matrix4f matrix = MatrixUtils.method664(customDrawContext.pose());
      Vector3f transformPosition = matrix.transformPosition(new Vector3f(floatValue, y, 0.0F));
      Vector3f vec = matrix.transformPosition(new Vector3f(floatValue + width, y + height, 0.0F));
      float f3 = transformPosition.x / f * 2.0F - 1.0F;
      float f4 = vec.x / f * 2.0F - 1.0F;
      float f5 = 1.0F - transformPosition.y / f2 * 2.0F;
      float f6 = 1.0F - vec.y / f2 * 2.0F;
      shaderProgram.method5523("QuadRect", f3, f6, f4 - f3, f5 - f6);
      FramebufferUtils.method5655(ShaderProgram.getCurrent(), ImmediateRenderer.method8014(), f3, f6, f4, f5);
   }

   private static void method258() {
      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
      GL20.glUseProgram(0);
   }

   private static final class Data implements WeatherUniforms.Handler {
      private final ShaderProgram shaderProgram;

      public ShaderProgram shaderProgram() {
         return this.shaderProgram;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.shaderProgram, other.shaderProgram);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shaderProgram);
      }

      @Override
      public String toString() {
         return "Data[" + "shaderProgram=" + this.shaderProgram + "]";
      }

      private Data(ShaderProgram shaderProgram) {
         this.shaderProgram = shaderProgram;
      }

      @Override
      public void method259(String key, float previewAlpha) {
         this.shaderProgram.method5519(key, previewAlpha);
      }

      @Override
      public void method260(String key, float x, float y, float z) {
         this.shaderProgram.method5524(key, x, y, z);
      }

      public ShaderProgram getShaderProgram() {
         return this.shaderProgram;
      }
   }
}

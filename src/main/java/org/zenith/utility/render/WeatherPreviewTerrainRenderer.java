package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.zenith.MatrixUtils;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class WeatherPreviewTerrainRenderer {
   public static final float CONST_floatValue = 26.0F;
   public static final int CONST_intValue = 5;
   public static final double CONST_doubleValue = 2.0;
   private static final int CONST_intValue2 = -8749180;
   private static final int CONST_intValue3 = -11644329;

   private WeatherPreviewTerrainRenderer() {
   }

   public static PoseStack createPreviewPose(MenuDrawContext drawContext, float x, float y, float scale) {
      PoseStack poseStack = MatrixUtils.method666(drawContext.pose());
      poseStack.translate(x, y, 50.0F);
      poseStack.scale(scale, -scale, scale * 0.4F);
      poseStack.rotate(Axis.XP.rotationDegrees(26.0F));
      return poseStack;
   }

   public static float getPreviewPitch() {
      return -26.0F;
   }

   public static float getPreviewYaw() {
      return 0.0F;
   }

   public static void renderTerrain(Glowboost glowboost, FlakeState terrain, WeatherPreviewTerrainRenderer.Data colorSettings, float previewAlpha) {
      int radius = terrain.getIntValue3();

      for (int x = -radius; x <= radius; x++) {
         for (int z = -radius; z <= radius; z++) {
            double height = terrain.method4544(x, z);
            if (height != Double.NEGATIVE_INFINITY) {
               double distance = Math.sqrt(x * x + z * z);
               float fogFactor = (float)Math.min(1.0, distance / (radius + 1.0)) * 0.6F;
               float brightnessVariation = 0.9F + 0.2F * Glowboost.method3268(hashCell(x, z), 0, 0);
               glowboost.method3237(
                  x, height, z, x + 1, height, z, x + 1, height, z + 1, x, height, z + 1, computeTerrainColor(-8749180, colorSettings, brightnessVariation, fogFactor, previewAlpha)
               );
               double neighborHeight = terrain.method4544(x, z + 1);
               boolean exposedEdge = neighborHeight == Double.NEGATIVE_INFINITY || z == radius;
               double sideBottom = exposedEdge ? height - 1.0 : neighborHeight;
               if (exposedEdge || neighborHeight < height) {
                  glowboost.method3237(
                     x,
                     height,
                     z + 1,
                     x + 1,
                     height,
                     z + 1,
                     x + 1,
                     sideBottom,
                     z + 1,
                     x,
                     sideBottom,
                     z + 1,
                     computeTerrainColor(-11644329, colorSettings, brightnessVariation, fogFactor, previewAlpha)
                  );
               }
            }
         }
      }
   }

   private static int computeTerrainColor(int baseColor, WeatherPreviewTerrainRenderer.Data colorSettings, float brightness, float fogFactor, float previewAlpha) {
      float red = ColorUtils.method6424(baseColor) * brightness;
      float green = ColorUtils.method6425(baseColor) * brightness;
      float blue = ColorUtils.method6426(baseColor) * brightness;
      float luminance = red * 0.299F + green * 0.587F + blue * 0.114F;
      red = lerp(luminance, red, colorSettings.getFloatValue()) * colorSettings.getFloatValue3() * colorSettings.getFloatValue2();
      green = lerp(luminance, green, colorSettings.getFloatValue()) * colorSettings.getFloatValue4() * colorSettings.getFloatValue2();
      blue = lerp(luminance, blue, colorSettings.getFloatValue()) * colorSettings.getFloatValue5() * colorSettings.getFloatValue2();
      float fogBlend = fogFactor * colorSettings.getFloatValue6();
      red = lerp(red, ColorUtils.method6424(colorSettings.getIntValue()), fogBlend);
      green = lerp(green, ColorUtils.method6425(colorSettings.getIntValue()), fogBlend);
      blue = lerp(blue, ColorUtils.method6426(colorSettings.getIntValue()), fogBlend);
      int alpha = (int)(255.0F * Math.clamp(previewAlpha, 0.0F, 1.0F));
      return alpha << 24 | channel(red) << 16 | channel(green) << 8 | channel(blue);
   }

   private static int channel(float value) {
      return Math.clamp((long)((int)(value * 255.0F + 0.5F)), 0, 255);
   }

   private static float lerp(float value, float end, float amount) {
      return value + (end - value) * amount;
   }

   private static long hashCell(int x, int z) {
      return x * -7046029254386353131L ^ z * -4417276706812531889L;
   }

   public static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final int intValue;
      private final float floatValue6;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue6, other.floatValue6);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, intValue, floatValue6);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "intValue=" + this.intValue + ", " + "floatValue6=" + this.floatValue6 + "]";
      }

      public Data(float saturation, float brightness, float redMultiplier, float greenMultiplier, float blueMultiplier, int fogColor, float fogAmount) {
         this.floatValue = saturation;
         this.floatValue2 = brightness;
         this.floatValue3 = redMultiplier;
         this.floatValue4 = greenMultiplier;
         this.floatValue5 = blueMultiplier;
         this.intValue = fogColor;
         this.floatValue6 = fogAmount;
      }

      public static WeatherPreviewTerrainRenderer.Data fromWeather(WeatherFlakeChannel weather, float strength) {
         float blend = Math.clamp(strength, 0.0F, 1.0F);
         return new WeatherPreviewTerrainRenderer.Data(
            WeatherPreviewTerrainRenderer.lerp(1.0F, weather.floatValue16, blend),
            WeatherPreviewTerrainRenderer.lerp(1.0F, weather.floatValue15, blend),
            WeatherPreviewTerrainRenderer.lerp(1.0F, weather.floatValues11[0], blend),
            WeatherPreviewTerrainRenderer.lerp(1.0F, weather.floatValues11[1], blend),
            WeatherPreviewTerrainRenderer.lerp(1.0F, weather.floatValues11[2], blend),
            weather.intValue,
            blend
         );
      }

      public static WeatherPreviewTerrainRenderer.Data createTinted(float saturation, float brightness, int fogColor, float fogAmount) {
         return new WeatherPreviewTerrainRenderer.Data(saturation, brightness, 1.0F, 1.0F, 1.0F, fogColor, fogAmount);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }
   }
}

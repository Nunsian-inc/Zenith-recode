package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.zenith.MatrixUtils;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class Util25 {
   public static final float CONST_floatValue = 26.0F;
   public static final int CONST_intValue = 5;
   public static final double CONST_doubleValue = 2.0;
   private static final int CONST_intValue2 = -8749180;
   private static final int CONST_intValue3 = -11644329;

   private Util25() {
   }

   public static PoseStack method6477(MenuDrawContext customDrawContext, float xo, float yo, float xScale) {
      PoseStack poseStack = MatrixUtils.method666(customDrawContext.pose());
      poseStack.translate(xo, yo, 50.0F);
      poseStack.scale(xScale, -xScale, xScale * 0.4F);
      poseStack.rotate(Axis.XP.rotationDegrees(26.0F));
      return poseStack;
   }

   public static float method6478() {
      return -26.0F;
   }

   public static float method6479() {
      return 0.0F;
   }

   public static void method6480(Glowboost glowboost, FlakeState flakeState2, Util25.Data data, float previewAlpha) {
      int n = flakeState2.getIntValue3();

      for (int i = -n; i <= n; i++) {
         for (int j = -n; j <= n; j++) {
            double d = flakeState2.method4544(i, j);
            if (d != Double.NEGATIVE_INFINITY) {
               double d2 = Math.sqrt(i * i + j * j);
               float f = (float)Math.min(1.0, d2 / (n + 1.0)) * 0.6F;
               float f2 = 0.9F + 0.2F * Glowboost.method3268(method6482(i, j), 0, 0);
               glowboost.method3237(
                  i, d, j, i + 1, d, j, i + 1, d, j + 1, i, d, j + 1, method6481(-8749180, data, f2, f, previewAlpha)
               );
               double d3 = flakeState2.method4544(i, j + 1);
               boolean flag = d3 == Double.NEGATIVE_INFINITY || j == n;
               double d4 = flag ? d - 1.0 : d3;
               if (flag || d3 < d) {
                  glowboost.method3237(
                     i,
                     d,
                     j + 1,
                     i + 1,
                     d,
                     j + 1,
                     i + 1,
                     d4,
                     j + 1,
                     i,
                     d4,
                     j + 1,
                     method6481(-11644329, data, f2, f, previewAlpha)
                  );
               }
            }
         }
      }
   }

   private static int method6481(int intValue, Util25.Data data, float f, float f2, float previewAlpha) {
      float f3 = ColorUtils.method6424(intValue) * f;
      float f4 = ColorUtils.method6425(intValue) * f;
      float f5 = ColorUtils.method6426(intValue) * f;
      float f6 = f3 * 0.299F + f4 * 0.587F + f5 * 0.114F;
      f3 = lerp(f6, f3, data.getFloatValue()) * data.getFloatValue3() * data.getFloatValue2();
      f4 = lerp(f6, f4, data.getFloatValue()) * data.getFloatValue4() * data.getFloatValue2();
      f5 = lerp(f6, f5, data.getFloatValue()) * data.getFloatValue5() * data.getFloatValue2();
      float floatValue = f2 * data.getFloatValue6();
      f3 = lerp(f3, ColorUtils.method6424(data.getIntValue()), floatValue);
      f4 = lerp(f4, ColorUtils.method6425(data.getIntValue()), floatValue);
      f5 = lerp(f5, ColorUtils.method6426(data.getIntValue()), floatValue);
      int n = (int)(255.0F * Math.clamp(previewAlpha, 0.0F, 1.0F));
      return n << 24 | channel(f3) << 16 | channel(f4) << 8 | channel(f5);
   }

   private static int channel(float f) {
      return Math.clamp((long)((int)(f * 255.0F + 0.5F)), 0, 255);
   }

   private static float lerp(float f, float f2, float floatValue) {
      return f + (f2 - f) * floatValue;
   }

   private static long method6482(int n, int n2) {
      return n * -7046029254386353131L ^ n2 * -4417276706812531889L;
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

      public Data(float floatValue, float f, float f2, float f3, float f4, int intValue, float floatValue2) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.intValue = intValue;
         this.floatValue6 = floatValue2;
      }

      public static Util25.Data method6483(WeatherFlakeChannel weatherFlakeChannel, float f) {
         float floatValue = Math.clamp(f, 0.0F, 1.0F);
         return new Util25.Data(
            Util25.lerp(1.0F, weatherFlakeChannel.floatValue16, floatValue),
            Util25.lerp(1.0F, weatherFlakeChannel.floatValue15, floatValue),
            Util25.lerp(1.0F, weatherFlakeChannel.floatValues11[0], floatValue),
            Util25.lerp(1.0F, weatherFlakeChannel.floatValues11[1], floatValue),
            Util25.lerp(1.0F, weatherFlakeChannel.floatValues11[2], floatValue),
            weatherFlakeChannel.intValue,
            floatValue
         );
      }

      public static Util25.Data method6484(float floatValue, float f, int intValue, float f2) {
         return new Util25.Data(floatValue, f, 1.0F, 1.0F, 1.0F, intValue, f2);
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

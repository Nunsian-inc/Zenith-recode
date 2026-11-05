package org.zenith;

import lombok.Generated;
import org.zenith.utility.render.MathUtils;

public interface Easing {
   Easing INSTANCE = method2832(0.45F, 1.45F, 0.49F, 1.15F);
   Easing INSTANCE2 = method2832(0.45F, 1.45F, 0.43F, 0.91F);
   Easing INSTANCE3 = method2832(0.1F, 1.07F, 0.34F, 1.04F);
   Easing INSTANCE4 = method2832(0.27F, 1.09F, 0.49F, 1.06F);
   Easing INSTANCE5 = method2832(0.62, -0.16, 0.8, 0.37);
   Easing INSTANCE6 = method2832(0.25, 1.07, 0.11, 1.1);
   Easing INSTANCE7 = method2832(0.42, 0.0, 0.58, 1.0);
   Easing INSTANCE8 = (f, f2, f3, f4) -> {
      float f5 = f3 * f / f4 + f2;
      return (float)(-2.0 * Math.pow(f5, 3.0) + 3.0 * Math.pow(f5, 2.0));
   };
   Easing INSTANCE9 = (f, f2, f3, f4) -> {
      float f5 = f3 * f / f4 + f2;
      return f5 < 0.5 ? 4.0F * f5 * f5 * f5 : (float)(1.0 - Math.pow(-2.0F * f5 + 2.0F, 3.0) / 2.0);
   };
   Easing INSTANCE10 = (f, f2, f3, f4) -> f3 * f / f4 + f2;
   Easing INSTANCE11 = (f, f2, f3, f4) -> {
      float f5;
      return f3 * (f5 = f / f4) * f5 + f2;
   };
   Easing INSTANCE12 = (f, f2, f3, f4) -> {
      float f5;
      return -f3 * (f5 = f / f4) * (f5 - 2.0F) + f2;
   };
   Easing INSTANCE13 = (f, f2, f3, f4) -> {
      float f5;
      return (f5 = f / (f4 / 2.0F)) < 1.0F ? f3 / 2.0F * f5 * f5 + f2 : -f3 / 2.0F * (--f5 * (f5 - 2.0F) - 1.0F) + f2;
   };
   Easing INSTANCE14 = (f, f2, f3, f4) -> {
      float f5;
      return f3 * (f5 = f / f4) * f5 * f5 + f2;
   };
   Easing INSTANCE15 = (f, f2, f3, f4) -> {
      float f5;
      return f3 * ((f5 = f / f4 - 1.0F) * f5 * f5 + 1.0F) + f2;
   };
   Easing INSTANCE16 = (f, f2, f3, f4) -> {
      float f5;
      float f6;
      return (f5 = f / (f4 / 2.0F)) < 1.0F ? f3 / 2.0F * f5 * f5 * f5 + f2 : f3 / 2.0F * ((f6 = f5 - 2.0F) * f6 * f6 + 2.0F) + f2;
   };
   Easing INSTANCE17 = (f, f2, f3, f4) -> {
      float f5;
      return f3 * (f5 = f / f4) * f5 * f5 * f5 + f2;
   };
   Easing INSTANCE18 = (f, f2, f3, f4) -> {
      float f5;
      return -f3 * ((f5 = f / f4 - 1.0F) * f5 * f5 * f5 - 1.0F) + f2;
   };
   Easing INSTANCE19 = (f, f2, f3, f4) -> {
      float f5;
      float f6;
      return (f5 = f / (f4 / 2.0F)) < 1.0F
         ? f3 / 2.0F * f5 * f5 * f5 * f5 + f2
         : -f3 / 2.0F * ((f6 = f5 - 2.0F) * f6 * f6 * f6 - 2.0F) + f2;
   };
   Easing INSTANCE20 = (f, f2, f3, f4) -> {
      float f5;
      return f3 * (f5 = f / f4) * f5 * f5 * f5 * f5 + f2;
   };
   Easing INSTANCE21 = (f, f2, f3, f4) -> {
      float f5;
      return f3 * ((f5 = f / f4 - 1.0F) * f5 * f5 * f5 * f5 + 1.0F) + f2;
   };
   Easing INSTANCE22 = (f, f2, f3, f4) -> {
      float f5;
      float f6;
      return (f5 = f / (f4 / 2.0F)) < 1.0F
         ? f3 / 2.0F * f5 * f5 * f5 * f5 * f5 + f2
         : f3 / 2.0F * ((f6 = f5 - 2.0F) * f6 * f6 * f6 * f6 + 2.0F) + f2;
   };
   Easing INSTANCE23 = (f, f2, f3, f4) -> -f3 * (float)MathUtils.cos(f / f4 * (Math.PI / 2)) + f3 + f2;
   Easing INSTANCE24 = (f, f2, f3, f4) -> f3 * (float)MathUtils.sin(f / f4 * (Math.PI / 2)) + f2;
   Easing INSTANCE25 = (f, f2, f3, f4) -> -f3 / 2.0F * ((float)MathUtils.cos(Math.PI * f / f4) - 1.0F) + f2;
   Easing INSTANCE26 = (f, f2, f3, f4) -> f == 0.0F ? f2 : f3 * (float)Math.pow(2.0, 10.0F * (f / f4 - 1.0F)) + f2;
   Easing INSTANCE27 = (f, f2, f3, f4) -> f == f4 ? f2 + f3 : f3 * (-((float)Math.pow(2.0, -10.0F * f / f4)) + 1.0F) + f2;
   Easing INSTANCE28 = (f, f2, f3, f4) -> {
      if (f == 0.0F) {
         return f2;
      } else if (f == f4) {
         return f2 + f3;
      } else {
         float f5;
         return (f5 = f / (f4 / 2.0F)) < 1.0F
            ? f3 / 2.0F * (float)Math.pow(2.0, 10.0F * (f5 - 1.0F)) + f2
            : f3 / 2.0F * (-((float)Math.pow(2.0, -10.0F * --f5)) + 2.0F) + f2;
      }
   };
   Easing INSTANCE29 = (f, f2, f3, f4) -> {
      float f5;
      return -f3 * ((float)Math.sqrt(1.0F - (f5 = f / f4) * f5) - 1.0F) + f2;
   };
   Easing INSTANCE30 = (f, f2, f3, f4) -> {
      float f5;
      return f3 * (float)Math.sqrt(1.0F - (f5 = f / f4 - 1.0F) * f5) + f2;
   };
   Easing INSTANCE31 = (f, f2, f3, f4) -> {
      float f5;
      float f6;
      return (f5 = f / (f4 / 2.0F)) < 1.0F
         ? -f3 / 2.0F * ((float)Math.sqrt(1.0F - f5 * f5) - 1.0F) + f2
         : f3 / 2.0F * ((float)Math.sqrt(1.0F - (f6 = f5 - 2.0F) * f6) + 1.0F) + f2;
   };
   Easing.Entry8 entry8 = new Easing.Entry6();
   Easing.Entry8 entry82 = new Easing.Entry4();
   Easing.Entry8 entry83 = new Easing.Entry();
   Easing.Entry2 entry2 = new Easing.Entry5();
   Easing.Entry2 entry22 = new Easing.Entry7();
   Easing.Entry2 entry23 = new Easing.Entry3();
   Easing INSTANCE32 = (f, f2, f3, f4) -> {
      if ((f = f / f4) < 0.36363637F) {
         return f3 * (7.5625F * f * f) + f2;
      } else if (f < 0.72727275F) {
         float f5;
         return f3 * (7.5625F * (f5 = f - 0.54545456F) * f5 + 0.75F) + f2;
      } else {
         float f5;
         float f6;
         return f < 0.90909094F
            ? f3 * (7.5625F * (f5 = f - 0.8181818F) * f5 + 0.9375F) + f2
            : f3 * (7.5625F * (f6 = f - 0.95454544F) * f6 + 0.984375F) + f2;
      }
   };
   Easing INSTANCE33 = (f, f2, f3, f4) -> f3 - INSTANCE32.ease(f4 - f, 0.0F, f3, f4) + f2;
   Easing INSTANCE34 = (f, f2, f3, f4) -> f < f4 / 2.0F
      ? INSTANCE33.ease(f * 2.0F, 0.0F, f3, f4) * 0.5F + f2
      : INSTANCE32.ease(f * 2.0F - f4, 0.0F, f3, f4) * 0.5F + f3 * 0.5F + f2;

   static Easing method2832(final double d, final double d2, final double d3, final double d4) {
      return new Easing() {
         @Override
         public float ease(float f, float var2x, float f2, float var4x) {
            if (var4x <= 0.0F || f <= 0.0F) {
               return var2x;
            } else if (f >= var4x) {
               return var2x + f2;
            } else {
               float f3 = f / var4x;
               float var6x = this.method2833((float)d, (float)d3, f3);
               float f4 = this.method2836(var6x, (float)d2, (float)d4);
               return var2x + f2 * f4;
            }
         }

         private float method2833(float f, float var2x, float f2) {
            float var4x = f2;
            byte b = 8;
            float var6x = 1.0E-5F;

            for (int i = 0; i < 8; i++) {
               float f3 = this.method2834(var4x, f, var2x);
               float f4 = this.method2835(var4x, f, var2x);
               if (Math.abs(f3 - f2) < 1.0E-5F || Math.abs(f4) < 1.0E-6F) {
                  break;
               }

               var4x -= (f3 - f2) / f4;
               var4x = Math.max(0.0F, Math.min(1.0F, var4x));
            }

            return var4x;
         }

         private float method2834(float f, float var2x, float f2) {
            return 3.0F * (1.0F - f) * (1.0F - f) * f * var2x + 3.0F * (1.0F - f) * f * f * f2 + f * f * f;
         }

         private float method2835(float f, float var2x, float f2) {
            return 3.0F * ((1.0F - f) * (1.0F - 3.0F * f) * var2x + (2.0F * f - 3.0F * f * f) * f2) + 3.0F * f * f;
         }

         private float method2836(float f, float var2x, float f2) {
            return 3.0F * (1.0F - f) * (1.0F - f) * f * var2x + 3.0F * (1.0F - f) * f * f * f2 + f * f * f;
         }
      };
   }

   float ease(float f, float f2, float f3, float f4);

   public static class Entry extends Easing.Entry8 {
      public Entry(float floatValue, float f) {
         super(floatValue, f);
      }

      public Entry() {
      }

      @Override
      public float ease(float f, float f2, float f3, float f4) {
         float floatValue = this.getFloatValue();
         float f5 = this.getFloatValue2();
         if (f == 0.0F) {
            return f2;
         } else if ((f = f / (f4 / 2.0F)) == 2.0F) {
            return f2 + f3;
         } else {
            if (f5 == 0.0F) {
               f5 = f4 * 0.45000002F;
            }

            float f6 = 0.0F;
            if (floatValue < Math.abs(f3)) {
               floatValue = f3;
               f6 = f5 / 4.0F;
            } else {
               f6 = f5 / (float) (Math.PI * 2) * (float)Math.asin(f3 / floatValue);
            }

            return f < 1.0F
               ? -0.5F * (floatValue * (float)Math.pow(2.0, 10.0F * --f) * (float)MathUtils.sin((f * f4 - f6) * (Math.PI * 2) / f5)) + f2
               : floatValue * (float)Math.pow(2.0, -10.0F * --f) * (float)MathUtils.sin((f * f4 - f6) * (Math.PI * 2) / f5) * 0.5F + f3 + f2;
         }
      }
   }

   public abstract static class Entry2 implements Easing {
      public static final float CONST_floatValue = 1.70158F;
      private float floatValue;

      public Entry2() {
         this(1.70158F);
      }

      public Entry2(float floatValue) {
         this.floatValue = floatValue;
      }

      @Generated
      public void setFloatValue(float floatValue) {
         this.floatValue = floatValue;
      }

      @Generated
      public float getFloatValue() {
         return this.floatValue;
      }
   }

   public static class Entry3 extends Easing.Entry2 {
      public Entry3() {
      }

      public Entry3(float floatValue) {
         super(floatValue);
      }

      @Override
      public float ease(float f, float f2, float f3, float f4) {
         float floatValue = this.getFloatValue();
         float f5;
         float f6;
         float f7;
         float f8;
         return (f5 = f / (f4 / 2.0F)) < 1.0F
            ? f3 / 2.0F * (f5 * f5 * (((f7 = floatValue * 1.525F) + 1.0F) * f5 - f7)) + f2
            : f3 / 2.0F * ((f6 = f5 - 2.0F) * f6 * (((f8 = floatValue * 1.525F) + 1.0F) * f6 + f8) + 2.0F) + f2;
      }
   }

   public static class Entry4 extends Easing.Entry8 {
      public Entry4(float floatValue, float f) {
         super(floatValue, f);
      }

      public Entry4() {
      }

      @Override
      public float ease(float f, float f2, float f3, float f4) {
         float floatValue = this.getFloatValue();
         float f5 = this.getFloatValue2();
         if (f == 0.0F) {
            return f2;
         } else if ((f = f / f4) == 1.0F) {
            return f2 + f3;
         } else {
            if (f5 == 0.0F) {
               f5 = f4 * 0.3F;
            }

            float f6 = 0.0F;
            if (floatValue < Math.abs(f3)) {
               floatValue = f3;
               f6 = f5 / 4.0F;
            } else {
               f6 = f5 / (float) (Math.PI * 2) * (float)Math.asin(f3 / floatValue);
            }

            return floatValue * (float)Math.pow(2.0, -10.0F * f) * (float)MathUtils.sin((f * f4 - f6) * (Math.PI * 2) / f5) + f3 + f2;
         }
      }
   }

   public static class Entry5 extends Easing.Entry2 {
      public Entry5() {
      }

      public Entry5(float floatValue) {
         super(floatValue);
      }

      @Override
      public float ease(float f, float f2, float f3, float f4) {
         float floatValue = this.getFloatValue();
         float f5;
         return f3 * (f5 = f / f4) * f5 * ((floatValue + 1.0F) * f5 - floatValue) + f2;
      }
   }

   public static class Entry6 extends Easing.Entry8 {
      public Entry6(float floatValue, float f) {
         super(floatValue, f);
      }

      public Entry6() {
      }

      @Override
      public float ease(float f, float f2, float f3, float f4) {
         float floatValue = this.getFloatValue();
         float f5 = this.getFloatValue2();
         if (f == 0.0F) {
            return f2;
         } else if ((f = f / f4) == 1.0F) {
            return f2 + f3;
         } else {
            if (f5 == 0.0F) {
               f5 = f4 * 0.3F;
            }

            float f6 = 0.0F;
            if (floatValue < Math.abs(f3)) {
               floatValue = f3;
               f6 = f5 / 4.0F;
            } else {
               f6 = f5 / (float) (Math.PI * 2) * (float)Math.asin(f3 / floatValue);
            }

            return -(floatValue * (float)Math.pow(2.0, 10.0F * --f) * (float)MathUtils.sin((f * f4 - f6) * (Math.PI * 2) / f5)) + f2;
         }
      }
   }

   public static class Entry7 extends Easing.Entry2 {
      public Entry7() {
      }

      public Entry7(float floatValue) {
         super(floatValue);
      }

      @Override
      public float ease(float f, float f2, float f3, float f4) {
         float floatValue = this.getFloatValue();
         float f5;
         return f3 * ((f5 = f / f4 - 1.0F) * f5 * ((floatValue + 1.0F) * f5 + floatValue) + 1.0F) + f2;
      }
   }

   public abstract static class Entry8 implements Easing {
      private float floatValue;
      private float floatValue2;

      public Entry8(float floatValue, float f) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
      }

      public Entry8() {
         this(-1.0F, 0.0F);
      }

      @Generated
      public void setFloatValue(float floatValue) {
         this.floatValue = floatValue;
      }

      @Generated
      public void setFloatValue2(float f) {
         this.floatValue2 = f;
      }

      @Generated
      public float getFloatValue() {
         return this.floatValue;
      }

      @Generated
      public float getFloatValue2() {
         return this.floatValue2;
      }
   }
}

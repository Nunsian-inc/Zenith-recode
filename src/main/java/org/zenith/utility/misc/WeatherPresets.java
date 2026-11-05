package org.zenith.utility.misc;

public final class WeatherPresets {
   public final String text;
   private final WeatherPresets.Data[] datas;
   public static final WeatherPresets INSTANCE = new WeatherPresets(
      "clear",
      new WeatherPresets.Data[]{
         new WeatherPresets.Data(
            0.0F,
            new float[]{0.018F, 0.03F, 0.075F},
            new float[]{0.055F, 0.08F, 0.15F},
            new float[]{0.02F, 0.028F, 0.05F},
            new float[]{0.13F, 0.16F, 0.235F},
            new float[]{0.04F, 0.052F, 0.085F},
            0.22F,
            0.35F,
            1.05F,
            0.018F,
            0.16F,
            new float[]{0.07F, 0.1F, 0.18F},
            0.8F,
            0.42F,
            0.0F,
            0.22F,
            0.02F,
            0.3F,
            -15985370,
            0.72F,
            0.0F
         ),
         new WeatherPresets.Data(
            4.8F,
            new float[]{0.055F, 0.105F, 0.235F},
            new float[]{0.28F, 0.26F, 0.33F},
            new float[]{0.055F, 0.07F, 0.105F},
            new float[]{0.43F, 0.43F, 0.52F},
            new float[]{0.15F, 0.165F, 0.23F},
            0.24F,
            0.28F,
            1.05F,
            0.019F,
            0.3F,
            new float[]{0.34F, 0.31F, 0.38F},
            0.34F,
            0.16F,
            0.0F,
            0.08F,
            0.022F,
            0.35F,
            -14012862,
            0.8F,
            0.0F
         ),
         new WeatherPresets.Data(
            6.3F,
            new float[]{0.14F, 0.265F, 0.545F},
            new float[]{0.98F, 0.64F, 0.43F},
            new float[]{0.12F, 0.115F, 0.145F},
            new float[]{1.0F, 0.815F, 0.67F},
            new float[]{0.4F, 0.39F, 0.5F},
            0.28F,
            0.18F,
            1.05F,
            0.02F,
            0.42F,
            new float[]{0.98F, 0.68F, 0.48F},
            0.05F,
            0.02F,
            0.0F,
            0.02F,
            0.032F,
            0.95F,
            -9543840,
            0.92F,
            0.0F
         ),
         new WeatherPresets.Data(
            8.0F,
            new float[]{0.15F, 0.35F, 0.74F},
            new float[]{0.76F, 0.83F, 0.93F},
            new float[]{0.23F, 0.29F, 0.38F},
            new float[]{1.0F, 0.985F, 0.955F},
            new float[]{0.62F, 0.685F, 0.805F},
            0.3F,
            0.1F,
            1.05F,
            0.02F,
            0.26F,
            new float[]{0.72F, 0.8F, 0.92F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.03F,
            0.85F,
            -5718572,
            1.02F,
            0.0F
         ),
         new WeatherPresets.Data(
            12.0F,
            new float[]{0.095F, 0.315F, 0.8F},
            new float[]{0.73F, 0.86F, 0.985F},
            new float[]{0.25F, 0.32F, 0.42F},
            new float[]{1.0F, 1.0F, 1.0F},
            new float[]{0.66F, 0.74F, 0.865F},
            0.32F,
            0.07F,
            1.0F,
            0.021F,
            0.2F,
            new float[]{0.76F, 0.865F, 0.965F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.03F,
            0.88F,
            -4929314,
            1.08F,
            0.0F
         ),
         new WeatherPresets.Data(
            16.8F,
            new float[]{0.135F, 0.335F, 0.735F},
            new float[]{0.8F, 0.83F, 0.91F},
            new float[]{0.235F, 0.29F, 0.375F},
            new float[]{1.0F, 0.975F, 0.93F},
            new float[]{0.64F, 0.69F, 0.8F},
            0.3F,
            0.11F,
            1.05F,
            0.02F,
            0.28F,
            new float[]{0.82F, 0.82F, 0.9F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.03F,
            0.88F,
            -5259566,
            1.0F,
            0.0F
         ),
         new WeatherPresets.Data(
            18.3F,
            new float[]{0.13F, 0.23F, 0.5F},
            new float[]{1.0F, 0.58F, 0.36F},
            new float[]{0.115F, 0.105F, 0.135F},
            new float[]{1.0F, 0.76F, 0.6F},
            new float[]{0.395F, 0.36F, 0.46F},
            0.28F,
            0.2F,
            1.05F,
            0.02F,
            0.46F,
            new float[]{1.0F, 0.62F, 0.41F},
            0.06F,
            0.03F,
            0.0F,
            0.03F,
            0.032F,
            0.95F,
            -9152166,
            0.9F,
            0.0F
         ),
         new WeatherPresets.Data(
            20.5F,
            new float[]{0.045F, 0.08F, 0.195F},
            new float[]{0.2F, 0.18F, 0.275F},
            new float[]{0.045F, 0.058F, 0.09F},
            new float[]{0.33F, 0.34F, 0.44F},
            new float[]{0.11F, 0.125F, 0.185F},
            0.24F,
            0.3F,
            1.05F,
            0.019F,
            0.26F,
            new float[]{0.25F, 0.24F, 0.33F},
            0.48F,
            0.22F,
            0.0F,
            0.14F,
            0.022F,
            0.34F,
            -14472640,
            0.78F,
            0.0F
         )
      }
   );
   public static final WeatherPresets INSTANCE2 = new WeatherPresets(
      "storm",
      new WeatherPresets.Data[]{
         new WeatherPresets.Data(
            0.0F,
            new float[]{0.022F, 0.028F, 0.036F},
            new float[]{0.052F, 0.06F, 0.07F},
            new float[]{0.016F, 0.02F, 0.026F},
            new float[]{0.115F, 0.13F, 0.15F},
            new float[]{0.026F, 0.032F, 0.04F},
            0.97F,
            0.9F,
            1.55F,
            0.036F,
            0.44F,
            new float[]{0.06F, 0.068F, 0.08F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.05F,
            -15459808,
            0.66F,
            0.26F
         ),
         new WeatherPresets.Data(
            4.8F,
            new float[]{0.048F, 0.055F, 0.066F},
            new float[]{0.105F, 0.11F, 0.125F},
            new float[]{0.03F, 0.035F, 0.042F},
            new float[]{0.2F, 0.215F, 0.24F},
            new float[]{0.05F, 0.058F, 0.07F},
            0.96F,
            0.88F,
            1.55F,
            0.036F,
            0.48F,
            new float[]{0.115F, 0.12F, 0.135F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.06F,
            -14801876,
            0.72F,
            0.24F
         ),
         new WeatherPresets.Data(
            6.3F,
            new float[]{0.085F, 0.092F, 0.105F},
            new float[]{0.25F, 0.195F, 0.175F},
            new float[]{0.05F, 0.055F, 0.065F},
            new float[]{0.34F, 0.315F, 0.315F},
            new float[]{0.08F, 0.085F, 0.098F},
            0.97F,
            0.86F,
            1.55F,
            0.038F,
            0.56F,
            new float[]{0.245F, 0.205F, 0.19F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.08F,
            -13422034,
            0.8F,
            0.28F
         ),
         new WeatherPresets.Data(
            8.0F,
            new float[]{0.135F, 0.155F, 0.175F},
            new float[]{0.265F, 0.29F, 0.31F},
            new float[]{0.07F, 0.08F, 0.092F},
            new float[]{0.45F, 0.48F, 0.51F},
            new float[]{0.092F, 0.105F, 0.122F},
            0.97F,
            0.85F,
            1.55F,
            0.038F,
            0.52F,
            new float[]{0.265F, 0.29F, 0.312F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.1F,
            -12761522,
            0.88F,
            0.3F
         ),
         new WeatherPresets.Data(
            12.0F,
            new float[]{0.16F, 0.185F, 0.205F},
            new float[]{0.31F, 0.34F, 0.36F},
            new float[]{0.082F, 0.094F, 0.108F},
            new float[]{0.51F, 0.545F, 0.575F},
            new float[]{0.1F, 0.115F, 0.135F},
            0.98F,
            0.85F,
            1.6F,
            0.04F,
            0.55F,
            new float[]{0.305F, 0.335F, 0.358F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.1F,
            -12169128,
            0.92F,
            0.34F
         ),
         new WeatherPresets.Data(
            16.8F,
            new float[]{0.14F, 0.158F, 0.176F},
            new float[]{0.28F, 0.3F, 0.318F},
            new float[]{0.072F, 0.082F, 0.095F},
            new float[]{0.47F, 0.495F, 0.522F},
            new float[]{0.094F, 0.107F, 0.125F},
            0.97F,
            0.86F,
            1.55F,
            0.039F,
            0.54F,
            new float[]{0.278F, 0.298F, 0.318F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.09F,
            -12629937,
            0.88F,
            0.32F
         ),
         new WeatherPresets.Data(
            18.3F,
            new float[]{0.082F, 0.088F, 0.1F},
            new float[]{0.255F, 0.19F, 0.165F},
            new float[]{0.048F, 0.053F, 0.062F},
            new float[]{0.33F, 0.3F, 0.298F},
            new float[]{0.078F, 0.082F, 0.094F},
            0.97F,
            0.87F,
            1.55F,
            0.038F,
            0.58F,
            new float[]{0.25F, 0.2F, 0.18F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.08F,
            -13488340,
            0.8F,
            0.3F
         ),
         new WeatherPresets.Data(
            20.5F,
            new float[]{0.04F, 0.046F, 0.056F},
            new float[]{0.09F, 0.095F, 0.108F},
            new float[]{0.026F, 0.03F, 0.037F},
            new float[]{0.175F, 0.188F, 0.21F},
            new float[]{0.042F, 0.049F, 0.06F},
            0.97F,
            0.89F,
            1.55F,
            0.037F,
            0.46F,
            new float[]{0.098F, 0.104F, 0.118F},
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.06F,
            -14999256,
            0.7F,
            0.28F
         )
      }
   );
   public static final WeatherPresets INSTANCE3 = new WeatherPresets(
      "stars",
      new WeatherPresets.Data[]{
         new WeatherPresets.Data(
            0.0F,
            new float[]{0.012F, 0.02F, 0.058F},
            new float[]{0.05F, 0.078F, 0.15F},
            new float[]{0.014F, 0.02F, 0.04F},
            new float[]{0.3F, 0.36F, 0.5F},
            new float[]{0.055F, 0.075F, 0.13F},
            0.34F,
            0.38F,
            1.2F,
            0.014F,
            0.18F,
            new float[]{0.07F, 0.105F, 0.195F},
            1.15F,
            1.0F,
            0.0F,
            0.55F,
            0.024F,
            0.6F,
            -16117468,
            0.7F,
            0.0F
         ),
         new WeatherPresets.Data(
            4.8F,
            new float[]{0.02F, 0.032F, 0.082F},
            new float[]{0.09F, 0.115F, 0.195F},
            new float[]{0.02F, 0.028F, 0.05F},
            new float[]{0.34F, 0.395F, 0.53F},
            new float[]{0.065F, 0.085F, 0.145F},
            0.34F,
            0.36F,
            1.2F,
            0.014F,
            0.22F,
            new float[]{0.105F, 0.13F, 0.21F},
            1.05F,
            0.92F,
            0.0F,
            0.48F,
            0.024F,
            0.58F,
            -15721934,
            0.74F,
            0.0F
         ),
         new WeatherPresets.Data(
            6.3F,
            new float[]{0.03F, 0.048F, 0.11F},
            new float[]{0.195F, 0.165F, 0.225F},
            new float[]{0.026F, 0.034F, 0.058F},
            new float[]{0.43F, 0.43F, 0.52F},
            new float[]{0.08F, 0.095F, 0.155F},
            0.34F,
            0.34F,
            1.2F,
            0.015F,
            0.28F,
            new float[]{0.185F, 0.165F, 0.23F},
            0.98F,
            0.86F,
            0.0F,
            0.44F,
            0.026F,
            0.66F,
            -14999496,
            0.78F,
            0.0F
         ),
         new WeatherPresets.Data(
            8.0F,
            new float[]{0.04F, 0.062F, 0.145F},
            new float[]{0.15F, 0.185F, 0.3F},
            new float[]{0.032F, 0.042F, 0.07F},
            new float[]{0.48F, 0.52F, 0.64F},
            new float[]{0.09F, 0.11F, 0.175F},
            0.34F,
            0.34F,
            1.2F,
            0.015F,
            0.26F,
            new float[]{0.15F, 0.185F, 0.295F},
            0.95F,
            0.84F,
            0.0F,
            0.44F,
            0.026F,
            0.7F,
            -14800315,
            0.82F,
            0.0F
         ),
         new WeatherPresets.Data(
            12.0F,
            new float[]{0.048F, 0.075F, 0.175F},
            new float[]{0.175F, 0.215F, 0.345F},
            new float[]{0.036F, 0.048F, 0.078F},
            new float[]{0.545F, 0.59F, 0.715F},
            new float[]{0.1F, 0.122F, 0.19F},
            0.34F,
            0.32F,
            1.2F,
            0.016F,
            0.24F,
            new float[]{0.175F, 0.215F, 0.335F},
            0.9F,
            0.8F,
            0.0F,
            0.42F,
            0.028F,
            0.74F,
            -14405556,
            0.86F,
            0.0F
         ),
         new WeatherPresets.Data(
            16.8F,
            new float[]{0.042F, 0.066F, 0.152F},
            new float[]{0.155F, 0.19F, 0.308F},
            new float[]{0.033F, 0.044F, 0.072F},
            new float[]{0.5F, 0.54F, 0.66F},
            new float[]{0.092F, 0.112F, 0.178F},
            0.34F,
            0.33F,
            1.2F,
            0.015F,
            0.26F,
            new float[]{0.155F, 0.19F, 0.3F},
            0.95F,
            0.84F,
            0.0F,
            0.44F,
            0.026F,
            0.7F,
            -14734522,
            0.82F,
            0.0F
         ),
         new WeatherPresets.Data(
            18.3F,
            new float[]{0.03F, 0.048F, 0.112F},
            new float[]{0.2F, 0.16F, 0.22F},
            new float[]{0.026F, 0.034F, 0.058F},
            new float[]{0.43F, 0.42F, 0.51F},
            new float[]{0.08F, 0.095F, 0.155F},
            0.34F,
            0.34F,
            1.2F,
            0.015F,
            0.3F,
            new float[]{0.19F, 0.16F, 0.225F},
            1.0F,
            0.88F,
            0.0F,
            0.48F,
            0.026F,
            0.66F,
            -14934217,
            0.78F,
            0.0F
         ),
         new WeatherPresets.Data(
            20.5F,
            new float[]{0.018F, 0.028F, 0.075F},
            new float[]{0.07F, 0.098F, 0.175F},
            new float[]{0.018F, 0.025F, 0.046F},
            new float[]{0.33F, 0.385F, 0.52F},
            new float[]{0.062F, 0.082F, 0.14F},
            0.34F,
            0.37F,
            1.2F,
            0.014F,
            0.2F,
            new float[]{0.09F, 0.118F, 0.2F},
            1.1F,
            0.96F,
            0.0F,
            0.52F,
            0.024F,
            0.6F,
            -15920088,
            0.72F,
            0.0F
         )
      }
   );

   private WeatherPresets(String text, WeatherPresets.Data[] datas) {
      this.text = text;
      this.datas = datas;
   }

   public WeatherPresets.Data method5994(float floatValue) {
      float f = floatValue % 24.0F;
      if (f < 0.0F) {
         f += 24.0F;
      }

      int n = 0;

      while (n < this.datas.length && this.datas[n].getFloatValue() <= f) {
         n++;
      }

      WeatherPresets.Data data = this.datas[n % this.datas.length];
      WeatherPresets.Data data2 = this.datas[(n - 1 + this.datas.length) % this.datas.length];
      float f2 = method5995(data2.getFloatValue(), data.getFloatValue());
      float f3 = f2 <= 0.0F ? 0.0F : Math.clamp(method5995(data2.getFloatValue(), f) / f2, 0.0F, 1.0F);
      f3 = f3 * f3 * (3.0F - 2.0F * f3);
      return new WeatherPresets.Data(
         f,
         method5997(data2.getFloatValues(), data.getFloatValues(), f3),
         method5997(data2.getFloatValues2(), data.getFloatValues2(), f3),
         method5997(data2.getFloatValues3(), data.getFloatValues3(), f3),
         method5997(data2.getFloatValues4(), data.getFloatValues4(), f3),
         method5997(data2.getFloatValues5(), data.getFloatValues5(), f3),
         method5996(data2.getFloatValue2(), data.getFloatValue2(), f3),
         method5996(data2.getFloatValue3(), data.getFloatValue3(), f3),
         method5996(data2.getFloatValue4(), data.getFloatValue4(), f3),
         method5996(data2.getFloatValue5(), data.getFloatValue5(), f3),
         method5996(data2.getFloatValue6(), data.getFloatValue6(), f3),
         method5997(data2.getFloatValues6(), data.getFloatValues6(), f3),
         method5996(data2.getFloatValue7(), data.getFloatValue7(), f3),
         method5996(data2.getFloatValue8(), data.getFloatValue8(), f3),
         method5996(data2.getFloatValue9(), data.getFloatValue9(), f3),
         method5996(data2.getFloatValue10(), data.getFloatValue10(), f3),
         method5996(data2.getFloatValue11(), data.getFloatValue11(), f3),
         method5996(data2.getFloatValue12(), data.getFloatValue12(), f3),
         method5998(data2.getIntValue(), data.getIntValue(), f3),
         method5996(data2.getFloatValue13(), data.getFloatValue13(), f3),
         method5996(data2.getFloatValue14(), data.getFloatValue14(), f3)
      );
   }

   private static float method5995(float floatValue, float f) {
      float f2 = f - floatValue;
      return f2 <= 0.0F ? f2 + 24.0F : f2;
   }

   private static float method5996(float f, float f2, float f3) {
      return f + (f2 - f) * f3;
   }

   private static float[] method5997(float[] floatValues, float[] floats, float f) {
      return new float[]{method5996(floatValues[0], floats[0], f), method5996(floatValues[1], floats[1], f), method5996(floatValues[2], floats[2], f)};
   }

   private static int method5998(int intValue, int n, float f) {
      int n2 = method5999(intValue >>> 24, n >>> 24, f);
      int n3 = method5999(intValue >> 16 & 0xFF, n >> 16 & 0xFF, f);
      int n4 = method5999(intValue >> 8 & 0xFF, n >> 8 & 0xFF, f);
      int n5 = method5999(intValue & 0xFF, n & 0xFF, f);
      return n2 << 24 | n3 << 16 | n4 << 8 | n5;
   }

   private static int method5999(int n, int n2, float f) {
      return Math.clamp((long)((int)(n + (n2 - n) * f + 0.5F)), 0, 255);
   }

   public static final class Data {
      private final float floatValue;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;
      private final float[] floatValues5;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float[] floatValues6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final int intValue;
      private final float floatValue13;
      private final float floatValue14;

      public float floatValue() {
         return this.floatValue;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      public float[] floatValues4() {
         return this.floatValues4;
      }

      public float[] floatValues5() {
         return this.floatValues5;
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

      public float floatValue6() {
         return this.floatValue6;
      }

      public float[] floatValues6() {
         return this.floatValues6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      public float floatValue10() {
         return this.floatValue10;
      }

      public float floatValue11() {
         return this.floatValue11;
      }

      public float floatValue12() {
         return this.floatValue12;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue13() {
         return this.floatValue13;
      }

      public float floatValue14() {
         return this.floatValue14;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4) && java.util.Objects.equals(this.floatValues5, other.floatValues5) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValues6, other.floatValues6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.floatValue12, other.floatValue12) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue13, other.floatValue13) && java.util.Objects.equals(this.floatValue14, other.floatValue14);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValues, floatValues2, floatValues3, floatValues4, floatValues5, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValues6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, intValue, floatValue13, floatValue14);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + ", " + "floatValues5=" + this.floatValues5 + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValues6=" + this.floatValues6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "floatValue12=" + this.floatValue12 + ", " + "intValue=" + this.intValue + ", " + "floatValue13=" + this.floatValue13 + ", " + "floatValue14=" + this.floatValue14 + "]";
      }

      public Data(
         float floatValue,
         float[] floatValues,
         float[] floatValues2,
         float[] floatValues3,
         float[] floatValues4,
         float[] floatValues5,
         float f,
         float f2,
         float f3,
         float f4,
         float f5,
         float[] floatValues6,
         float f6,
         float f7,
         float f8,
         float f9,
         float f10,
         float f11,
         int intValue,
         float f12,
         float f13
      ) {
         this.floatValue = floatValue;
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         this.floatValues4 = floatValues4;
         this.floatValues5 = floatValues5;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValues6 = floatValues6;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
         this.floatValue10 = f9;
         this.floatValue11 = f10;
         this.floatValue12 = f11;
         this.intValue = intValue;
         this.floatValue13 = f12;
         this.floatValue14 = f13;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public float[] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }

      public float[] getFloatValues4() {
         return this.floatValues4;
      }

      public float[] getFloatValues5() {
         return this.floatValues5;
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

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float[] getFloatValues6() {
         return this.floatValues6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public float getFloatValue12() {
         return this.floatValue12;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }
   }
}

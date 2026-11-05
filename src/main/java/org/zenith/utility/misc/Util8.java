package org.zenith.utility.misc;

public final class Util8 {
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final float CONST_floatValue2 = 0.55F;
   private static final float CONST_floatValue3 = 0.06F;
   private static final float CONST_floatValue4 = -0.38F;
   private static final float CONST_floatValue5 = -0.06F;
   public final float floatValue;
   public final float floatValue2;
   public final float floatValue3;
   public final float floatValue4;
   public final float floatValue5;
   public final float floatValue6;
   public final float[] floatValues;
   public final float[] floatValues2;
   public final float floatValue7;
   public final float floatValue8;
   public final float floatValue9;
   public final float floatValue10;
   public final float floatValue11;
   public final float floatValue12;
   public final boolean flag;
   public final float floatValue13;
   public final float floatValue14;
   public final float floatValue15;
   public final float floatValue16;
   public final float floatValue17;
   private static final Util8.Data[] datas = new Util8.Data[]{
      new Util8.Data(0.0F, 0.15F, 1.0F, 0.8F, 0.86F, 1.14F, 0.6F, 0.68F, 0.92F, 0.38F, 0.92F, 0.5F, 0.82F, 0.68F, 1.12F),
      new Util8.Data(3.0F, 0.14F, 1.0F, 0.79F, 0.85F, 1.15F, 0.58F, 0.66F, 0.92F, 0.36F, 0.95F, 0.52F, 0.8F, 0.66F, 1.12F),
      new Util8.Data(4.4F, 0.2F, 1.0F, 0.82F, 0.86F, 1.12F, 0.66F, 0.7F, 0.9F, 0.42F, 0.78F, 0.42F, 0.88F, 0.7F, 1.14F),
      new Util8.Data(5.4F, 0.34F, 0.9F, 1.0F, 0.9F, 1.02F, 0.78F, 0.78F, 0.9F, 0.6F, 0.34F, 0.14F, 1.2F, 0.78F, 1.16F),
      new Util8.Data(6.2F, 0.58F, 0.82F, 1.16F, 0.94F, 0.88F, 1.26F, 0.66F, 0.42F, 1.5F, 0.08F, 0.03F, 1.42F, 0.85F, 1.18F),
      new Util8.Data(7.4F, 0.82F, 0.9F, 1.1F, 1.0F, 0.9F, 1.18F, 0.84F, 0.58F, 1.35F, 0.01F, 0.0F, 1.32F, 0.93F, 1.1F),
      new Util8.Data(9.6F, 0.93F, 0.97F, 1.04F, 1.0F, 0.95F, 1.08F, 0.92F, 0.74F, 1.14F, 0.0F, 0.0F, 1.14F, 1.0F, 1.03F),
      new Util8.Data(12.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.97F, 0.86F, 1.0F, 0.0F, 0.0F, 1.0F, 1.05F, 1.0F),
      new Util8.Data(14.4F, 0.98F, 1.0F, 1.02F, 1.0F, 0.98F, 1.02F, 0.95F, 0.82F, 1.05F, 0.0F, 0.0F, 1.06F, 1.04F, 1.0F),
      new Util8.Data(16.4F, 0.9F, 0.96F, 1.07F, 0.99F, 0.92F, 1.14F, 0.87F, 0.64F, 1.22F, 0.0F, 0.0F, 1.2F, 0.98F, 1.05F),
      new Util8.Data(17.4F, 0.8F, 0.86F, 1.18F, 0.96F, 0.86F, 1.3F, 0.74F, 0.46F, 1.55F, 0.02F, 0.01F, 1.4F, 0.92F, 1.14F),
      new Util8.Data(18.2F, 0.55F, 0.82F, 1.16F, 0.9F, 0.92F, 0.84F, 0.78F, 0.88F, 0.7F, 0.16F, 0.06F, 1.36F, 0.84F, 1.18F),
      new Util8.Data(19.4F, 0.34F, 0.9F, 0.98F, 0.88F, 1.04F, 0.72F, 0.74F, 0.92F, 0.58F, 0.46F, 0.22F, 1.12F, 0.78F, 1.16F),
      new Util8.Data(20.6F, 0.22F, 0.98F, 0.86F, 0.87F, 1.1F, 0.66F, 0.7F, 0.92F, 0.48F, 0.72F, 0.38F, 0.94F, 0.72F, 1.14F),
      new Util8.Data(22.0F, 0.17F, 1.0F, 0.82F, 0.87F, 1.12F, 0.64F, 0.7F, 0.92F, 0.44F, 0.84F, 0.44F, 0.88F, 0.7F, 1.12F)
   };

   private Util8(float floatValue, Util8.Data data, float[] floatValues, float[] floatValues2, float f, float f2, boolean flag) {
      this.floatValue = floatValue;
      this.floatValue2 = data.getFloatValue2();
      this.floatValue3 = data.getFloatValue3();
      this.floatValue4 = data.getFloatValue4();
      this.floatValue5 = data.getFloatValue5();
      this.floatValue6 = data.getFloatValue6();
      this.floatValues = floatValues;
      this.floatValues2 = floatValues2;
      this.floatValue7 = f;
      this.floatValue8 = data.getFloatValue7();
      this.floatValue9 = data.getFloatValue8();
      this.floatValue10 = data.getFloatValue9();
      this.floatValue11 = data.getFloatValue10();
      this.floatValue12 = f2;
      this.flag = flag;
      this.floatValue13 = data.getFloatValue11();
      this.floatValue14 = data.getFloatValue12();
      this.floatValue15 = data.getFloatValue13();
      this.floatValue16 = data.getFloatValue14();
      this.floatValue17 = data.getFloatValue15();
   }

   public static Util8 method1722(float floatValue) {
      float f = method1723(floatValue);
      Util8.Data data = method1724(f);
      float f2 = (f - 6.0F) / 24.0F * (float) (Math.PI * 2);
      float f3 = (float)Math.sin(f2);
      float f4 = (float)Math.cos(f2);
      float f5 = (float)Math.cos(0.55F);
      float f6 = (float)Math.sin(0.55F);
      float[] floatValues2 = new float[]{f4, f3 * f5, f3 * f6};
      boolean flag = floatValues2[1] < 0.0F;
      float[] floatValues = flag ? new float[]{-floatValues2[0], -floatValues2[1], -floatValues2[2]} : (float[])floatValues2.clone();
      float f7 = Math.clamp(floatValues[1] / 0.06F, 0.0F, 1.0F);
      f7 = f7 * f7 * (3.0F - 2.0F * f7);
      float f8 = Math.clamp((floatValues2[1] - -0.38F) / 0.32F, 0.0F, 1.0F);
      f8 = f8 * f8 * (3.0F - 2.0F * f8);
      return new Util8(f, data, floatValues, floatValues2, f8, f7, flag);
   }

   private static float method1723(float floatValue) {
      float f = floatValue % 24.0F;
      return f < 0.0F ? f + 24.0F : f;
   }

   private static Util8.Data method1724(float floatValue) {
      int n = 0;

      while (n < datas.length && datas[n].getFloatValue() <= floatValue) {
         n++;
      }

      Util8.Data data = datas[n % datas.length];
      Util8.Data data2 = datas[(n - 1 + datas.length) % datas.length];
      float f = method1725(data2.getFloatValue(), data.getFloatValue());
      float f2 = f <= 0.0F ? 0.0F : Math.clamp(method1725(data2.getFloatValue(), floatValue) / f, 0.0F, 1.0F);
      f2 = f2 * f2 * (3.0F - 2.0F * f2);
      return new Util8.Data(
         floatValue,
         method1726(data2.getFloatValue2(), data.getFloatValue2(), f2),
         method1726(data2.getFloatValue3(), data.getFloatValue3(), f2),
         method1726(data2.getFloatValue4(), data.getFloatValue4(), f2),
         method1726(data2.getFloatValue5(), data.getFloatValue5(), f2),
         method1726(data2.getFloatValue6(), data.getFloatValue6(), f2),
         method1726(data2.getFloatValue7(), data.getFloatValue7(), f2),
         method1726(data2.getFloatValue8(), data.getFloatValue8(), f2),
         method1726(data2.getFloatValue9(), data.getFloatValue9(), f2),
         method1726(data2.getFloatValue10(), data.getFloatValue10(), f2),
         method1726(data2.getFloatValue11(), data.getFloatValue11(), f2),
         method1726(data2.getFloatValue12(), data.getFloatValue12(), f2),
         method1726(data2.getFloatValue13(), data.getFloatValue13(), f2),
         method1726(data2.getFloatValue14(), data.getFloatValue14(), f2),
         method1726(data2.getFloatValue15(), data.getFloatValue15(), f2)
      );
   }

   private static float method1725(float floatValue, float f) {
      float f2 = f - floatValue;
      return f2 <= 0.0F ? f2 + 24.0F : f2;
   }

   private static float method1726(float f, float f2, float f3) {
      return f + (f2 - f) * f3;
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final float floatValue13;
      private final float floatValue14;
      private final float floatValue15;

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

      public float floatValue6() {
         return this.floatValue6;
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

      public float floatValue13() {
         return this.floatValue13;
      }

      public float floatValue14() {
         return this.floatValue14;
      }

      public float floatValue15() {
         return this.floatValue15;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.floatValue12, other.floatValue12) && java.util.Objects.equals(this.floatValue13, other.floatValue13) && java.util.Objects.equals(this.floatValue14, other.floatValue14) && java.util.Objects.equals(this.floatValue15, other.floatValue15);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, floatValue13, floatValue14, floatValue15);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "floatValue12=" + this.floatValue12 + ", " + "floatValue13=" + this.floatValue13 + ", " + "floatValue14=" + this.floatValue14 + ", " + "floatValue15=" + this.floatValue15 + "]";
      }

      private Data(
         float floatValue,
         float f,
         float f2,
         float f3,
         float f4,
         float f5,
         float f6,
         float f7,
         float f8,
         float f9,
         float f10,
         float f11,
         float f12,
         float f13,
         float f14
      ) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
         this.floatValue10 = f9;
         this.floatValue11 = f10;
         this.floatValue12 = f11;
         this.floatValue13 = f12;
         this.floatValue14 = f13;
         this.floatValue15 = f14;
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

      public float getFloatValue6() {
         return this.floatValue6;
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

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }

      public float getFloatValue15() {
         return this.floatValue15;
      }
   }
}

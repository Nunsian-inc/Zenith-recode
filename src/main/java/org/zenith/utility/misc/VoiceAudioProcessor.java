package org.zenith.utility.misc;

public final class VoiceAudioProcessor {
   private static final float CONST_floatValue = -100.0F;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10 = -60.0F;
   private float floatValue11 = 1.0F;
   private final float[] floatValues = new float[960];

   public VoiceAudioProcessor() {
      double d = 0.010471975511965976;
      double d2 = Math.sin(d) / 1.4142;
      double d3 = Math.cos(d);
      double d4 = 1.0 + d2;
      this.floatValue = (float)((1.0 + d3) / 2.0 / d4);
      this.floatValue2 = (float)(-(1.0 + d3) / d4);
      this.floatValue3 = (float)((1.0 + d3) / 2.0 / d4);
      this.floatValue4 = (float)(-2.0 * d3 / d4);
      this.floatValue5 = (float)((1.0 - d2) / d4);
   }

   public void reset() {
      this.floatValue6 = this.floatValue7 = this.floatValue8 = this.floatValue9 = 0.0F;
      this.floatValue10 = -60.0F;
      this.floatValue11 = 1.0F;
   }

   public float method1530(short[] shortValues, float f, boolean flag) {
      double d = 0.0;

      for (int i = 0; i < shortValues.length; i++) {
         float f2 = shortValues[i] / 32768.0F;
         float f3 = this.floatValue * f2
            + this.floatValue2 * this.floatValue6
            + this.floatValue3 * this.floatValue7
            - this.floatValue4 * this.floatValue8
            - this.floatValue5 * this.floatValue9;
         this.floatValue7 = this.floatValue6;
         this.floatValue6 = f2;
         this.floatValue9 = this.floatValue8;
         this.floatValue8 = Math.abs(f3) < 1.0E-20F ? 0.0F : f3;
         float f4 = method1533(f3 * f);
         this.floatValues[i] = f4;
         d += f4 * f4;
      }

      float f2 = method1532(Math.sqrt(d / shortValues.length));
      float f3 = 1.0F;
      if (flag) {
         this.floatValue10 = f2 < this.floatValue10 ? f2 : Math.min(-20.0F, this.floatValue10 + 0.05F);
         float f4 = f2 - this.floatValue10;
         float f5 = VoiceAudioProcessor.Entry.clamp((f4 - 6.0F) / 6.0F, 0.0F, 1.0F);
         f3 = 0.18F + 0.82F * f5;
      }

      float f4 = f3 > this.floatValue11 ? f3 : this.floatValue11 + (f3 - this.floatValue11) * 0.13F;
      float f5 = (f4 - this.floatValue11) / shortValues.length;

      for (int i = 0; i < shortValues.length; i++) {
         this.floatValue11 += f5;
         shortValues[i] = (short)Math.round(this.floatValues[i] * this.floatValue11 * 32767.0F);
      }

      this.floatValue11 = f4;
      return f2;
   }

   public static float method1531(short[] shortValues, int length) {
      double d = 0.0;

      for (int i = 0; i < length; i++) {
         float f = shortValues[i] / 32768.0F;
         d += f * f;
      }

      return method1532(Math.sqrt(d / Math.max(1, length)));
   }

   public static float method1532(double d) {
      return d <= 1.0E-5 ? -100.0F : (float)Math.max(-100.0, 20.0 * Math.log10(d));
   }

   private static float method1533(float sign) {
      float f = Math.abs(sign);
      if (f <= 0.6F) {
         return sign;
      } else {
         float x = (f - 0.6F) / 0.4F;
         float f2 = 0.6F + 0.4F * (float)Math.tanh(x);
         return Math.copySign(Math.min(f2, 1.0F), sign);
      }
   }

   private static final class Entry {
      public static float clamp(float f, float f2, float f3) {
         return f < f2 ? f2 : Math.min(f, f3);
      }
   }
}

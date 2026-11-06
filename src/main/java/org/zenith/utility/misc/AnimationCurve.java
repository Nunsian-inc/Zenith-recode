package org.zenith.utility.misc;

import java.util.Map;

public final class AnimationCurve {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   private final String text;
   private final float floatValue;
   private final boolean flag;
   private final AnimationCurve.Entry[][] entrys;

   public AnimationCurve(String text, float floatValue, boolean flag, Map<Integer, AnimationCurve.Entry[]> map, int n) {
      this.text = text;
      this.floatValue = floatValue;
      this.flag = flag;
      this.entrys = new AnimationCurve.Entry[n][];

      for (Map.Entry entry : map.entrySet()) {
         if ((Integer)entry.getKey() >= 0 && (Integer)entry.getKey() < n) {
            this.entrys[(Integer)entry.getKey()] = (AnimationCurve.Entry[])entry.getValue();
         }
      }
   }

   public String name() {
      return this.text;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public float normalizeTime(float f) {
      if (this.floatValue <= 0.0F) {
         return 0.0F;
      } else if (!this.flag) {
         return Math.min(Math.max(f, 0.0F), this.floatValue);
      } else {
         float f2 = f % this.floatValue;
         return f2 < 0.0F ? f2 + this.floatValue : f2;
      }
   }

   public boolean hasBoneTrack(int n) {
      return n >= 0 && n < this.entrys.length && this.entrys[n] != null;
   }

   public void sampleBone(int n, float f, float[] floatValues) {
      floatValues[0] = floatValues[1] = floatValues[2] = 0.0F;
      floatValues[3] = floatValues[4] = floatValues[5] = 0.0F;
      floatValues[6] = floatValues[7] = floatValues[8] = 1.0F;
      if (this.hasBoneTrack(n)) {
         AnimationCurve.Entry[] entry = this.entrys[n];
         if (entry[0] != null) {
            entry[0].sample(f, floatValues, 0, this.flag);
         }

         if (entry[1] != null) {
            entry[1].sample(f, floatValues, 3, this.flag);
         }

         if (entry[2] != null) {
            entry[2].sample(f, floatValues, 6, this.flag);
         }
      }
   }

   public static final class Entry {
      public final float[] floatValues;
      public final float[] floatValues2;
      public final boolean flag;

      public Entry(float[] floatValues, float[] floatValues2, boolean flag) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.flag = flag;
      }

      public void sample(float f, float[] floatValues, int n, boolean flag) {
         int n2 = this.floatValues.length - 1;
         if (n2 >= 0) {
            if (f <= this.floatValues[0] || n2 == 0) {
               this.copyKeyframe(0, floatValues, n);
            } else if (f >= this.floatValues[n2]) {
               this.copyKeyframe(n2, floatValues, n);
            } else {
               int n3 = 0;

               while (n3 < n2 && this.floatValues[n3 + 1] < f) {
                  n3++;
               }

               float floatValue = this.floatValues[n3];
               float floatValue2 = this.floatValues[n3 + 1];
               float f2 = floatValue2 - floatValue <= 1.0E-6F ? 0.0F : (f - floatValue) / (floatValue2 - floatValue);
               if (this.flag) {
                  int n4 = n3 - 1 >= 0 ? n3 - 1 : (flag && n2 >= 2 ? n2 - 1 : 0);
                  int n5 = n3 + 2 <= n2 ? n3 + 2 : (flag && n2 >= 2 ? Math.min(n3 + 2 - n2, n2) : n2);

                  for (int i = 0; i < 3; i++) {
                     floatValues[n + i] = catmullRom(
                        this.floatValues2[n4 * 3 + i],
                        this.floatValues2[n3 * 3 + i],
                        this.floatValues2[(n3 + 1) * 3 + i],
                        this.floatValues2[n5 * 3 + i],
                        f2
                     );
                  }
               } else {
                  for (int i = 0; i < 3; i++) {
                     float f3 = this.floatValues2[n3 * 3 + i];
                     floatValues[n + i] = f3 + (this.floatValues2[(n3 + 1) * 3 + i] - f3) * f2;
                  }
               }
            }
         }
      }

      private void copyKeyframe(int n, float[] floatValues, int n2) {
         floatValues[n2] = this.floatValues2[n * 3];
         floatValues[n2 + 1] = this.floatValues2[n * 3 + 1];
         floatValues[n2 + 2] = this.floatValues2[n * 3 + 2];
      }

      private static float catmullRom(float f, float f2, float f3, float f4, float f5) {
         float f6 = f5 * f5;
         float f7 = f6 * f5;
         return 0.5F
            * (2.0F * f2 + (f3 - f) * f5 + (2.0F * f - 5.0F * f2 + 4.0F * f3 - f4) * f6 + (3.0F * f2 - f - 3.0F * f3 + f4) * f7);
      }
   }
}

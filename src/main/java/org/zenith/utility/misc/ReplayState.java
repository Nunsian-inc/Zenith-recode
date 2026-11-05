package org.zenith.utility.misc;

import java.util.Random;
import org.zenith.utility.api.IntentLibraryRt;
import org.zenith.utility.api.IntentSelector;

public final class ReplayState {
   private static final double CONST_doubleValue = 0.35;
   private static final int CONST_intValue = 16;
   private static final double CONST_doubleValue2 = 0.7;
   private static final double CONST_doubleValue3 = 1.0;
   private static final double CONST_doubleValue4 = 0.0;
   private static final double CONST_doubleValue5 = 0.0;
   private static final double CONST_doubleValue6 = 1.0;
   private static final double CONST_doubleValue7 = 1.0;
   private static final double CONST_doubleValue8 = 1.0;
   private static final int CONST_intValue2 = 40;
   private static final double CONST_doubleValue9 = 1.0;
   private static final double CONST_doubleValue10 = 1.0;
   private static final double CONST_doubleValue11 = 0.6;
   private static final double CONST_doubleValue12 = 0.4;
   private static final double CONST_doubleValue13 = 0.5;
   private static final double CONST_doubleValue14 = 40.0;
   private static final int CONST_intValue3 = 2;
   private static final double CONST_doubleValue15 = 1.25;
   private static final double CONST_doubleValue16 = 25.0;
   private static final double CONST_doubleValue17 = 30.0;
   private static final double CONST_doubleValue18 = 15.0;
   private final IntentSelector intentSelector;
   private final IntentLibraryRt intentLibraryRt;
   private final Random random;
   private IntentLibraryRt.Entry3 entry3;
   private int intValue;
   private double doubleValue;
   private boolean flag;
   private int intValue2;
   private boolean flag2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6 = -1;
   private String text = "idle";

   public ReplayState(IntentSelector intentSelector, IntentLibraryRt intentLibraryRt) {
      this.intentSelector = intentSelector;
      this.intentLibraryRt = intentLibraryRt;
      this.random = new Random();
      this.reset();
   }

   public void reset() {
      this.entry3 = null;
      this.intValue = 0;
      this.doubleValue = 0.0;
      this.flag = false;
      this.intValue2 = 0;
      this.flag2 = false;
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.intValue5 = 0;
      this.intValue6 = -1;
      this.text = "idle";
   }

   public String getText() {
      return this.text;
   }

   public int[] method7983(ReplayState.Entry entry) {
      if (this.entry3 != null && this.method7985(entry)) {
         this.entry3 = null;
         this.intValue = 0;
      }

      if (this.entry3 != null) {
         int intValue = this.entry3.intValues[this.intValue];
         int n = this.entry3.intValues2[this.intValue];
         this.intValue++;
         this.text = "replay:" + IntentLibraryRt.texts[this.entry3.intValue2] + " " + this.intValue + "/" + this.entry3.length();
         if (this.intValue >= this.entry3.length()) {
            this.entry3 = null;
            this.intValue = 0;
         }

         this.method7991(intValue, n);
         return new int[]{intValue, n};
      } else if (this.intValue2 > 0) {
         this.intValue2--;
         if (this.intValue2 <= 0) {
            this.flag2 = true;
         }

         this.text = "rest:" + this.intValue2 + " left";
         this.method7991(0, 0);
         return new int[]{0, 0};
      } else {
         IntentSelector.Entry entry2 = this.intentSelector.method7315(this.method7986(entry));
         double d = method7992((entry2.floatValue + 0.0) / 1.0);
         boolean flag = this.flag2 || this.random.nextDouble() < d;
         this.flag2 = false;
         if (!flag) {
            int n = 1;
            if (entry.doubleValue13 >= 0.5) {
               n = this.method7984(entry2.floatValues3);
               if (n > 1) {
                  this.intValue2 = n - 1;
               } else {
                  this.flag2 = true;
               }
            }

            this.text = "wait dwell=" + n;
            this.method7991(0, 0);
            return new int[]{0, 0};
         } else {
            int n = this.method7989(entry2.floatValues, 0.7);
            double[] doubles = this.method7987(entry2.floatValues2[n]);
            double[] doubles2 = this.method7988(doubles, entry);
            IntentLibraryRt.Entry2 entry22 = new IntentLibraryRt.Entry2();
            entry22.doubleValue = entry.doubleValue;
            entry22.doubleValue2 = entry.doubleValue2;
            entry22.doubleValue3 = entry.doubleValue9;
            entry22.doubleValue4 = entry.doubleValue10;
            entry22.doubleValue5 = (entry.doubleValue4 - entry.doubleValue3) * 0.5;
            entry22.doubleValue6 = (entry.doubleValue6 - entry.doubleValue5) * 0.5;
            entry22.intValue = this.intValue3;
            entry22.intValue2 = this.intValue4;
            IntentLibraryRt.Entry3 entry3 = this.intentLibraryRt
               .method1379(n, doubles2, entry22, this.random, 16, 0.35, this.intValue6, 1.0, 1.0, 0.6, 0.4, 0.5, 40.0);
            this.intValue6 = entry3.intValue;
            this.entry3 = entry3;
            this.intValue = 0;
            this.doubleValue = Math.hypot(entry.doubleValue, entry.doubleValue2);
            this.flag = entry.doubleValue13 >= 0.5;
            int intValue = entry3.intValues[0];
            int n2 = entry3.intValues2[0];
            this.intValue = 1;
            this.text = "start:" + IntentLibraryRt.texts[n] + " #" + entry3.intValue + " len=" + entry3.length();
            if (this.intValue >= entry3.length()) {
               this.entry3 = null;
               this.intValue = 0;
            }

            this.method7991(intValue, n2);
            return new int[]{intValue, n2};
         }
      }
   }

   private int method7984(float[][] floatValues3) {
      float[] floatValues = new float[floatValues3.length];

      for (int i = 0; i < floatValues3.length; i++) {
         floatValues[i] = floatValues3[i][0];
      }

      int n = this.method7989(floatValues, 1.0);
      double d = floatValues3[n][1];
      double d2 = method7993(floatValues3[n][2], -2.5, 0.7F);
      d += Math.exp(d2) * 1.0 * this.random.nextGaussian();
      int n2 = (int)Math.round(Math.expm1(Math.max(d, 0.0)));
      return Math.max(1, Math.min(n2, 40));
   }

   private boolean method7985(ReplayState.Entry entry) {
      if (this.intValue < 2) {
         return false;
      } else {
         double hypot = Math.hypot(entry.doubleValue, entry.doubleValue2);
         boolean flag = hypot > this.doubleValue * 1.25 && hypot > this.doubleValue + 25.0;
         double x = 0.0;
         double y = 0.0;

         for (int i = this.intValue; i < this.entry3.length(); i++) {
            x += this.entry3.intValues[i];
            y += this.entry3.intValues2[i];
         }

         double d = Math.hypot(x, y);
         boolean flag2 = hypot >= 30.0 && d >= 15.0 && x * entry.doubleValue + y * entry.doubleValue2 < 0.0;
         boolean flag3 = this.flag && entry.doubleValue13 < 0.5;
         return flag || flag2 || flag3;
      }
   }

   private float[] method7986(ReplayState.Entry entry) {
      float[] floats = new float[this.intentSelector.intValue];

      for (int i = 0; i < this.intentSelector.intValue; i++) {
         String text = this.intentSelector.texts[i];

         floats[i] = (float)(switch (text) {
            case "center_yaw" -> entry.doubleValue;
            case "center_pitch" -> entry.doubleValue2;
            case "box_min_yaw" -> entry.doubleValue3;
            case "box_max_yaw" -> entry.doubleValue4;
            case "box_min_pitch" -> entry.doubleValue5;
            case "box_max_pitch" -> entry.doubleValue6;
            case "center_yaw_change" -> entry.doubleValue7;
            case "center_pitch_change" -> entry.doubleValue8;
            case "yaw_drift" -> entry.doubleValue9;
            case "pitch_drift" -> entry.doubleValue10;
            case "sim_2t_yaw" -> entry.doubleValue11;
            case "sim_2t_pitch" -> entry.doubleValue12;
            case "inside" -> entry.doubleValue13;
            case "distance" -> entry.doubleValue14;
            case "target_motion_x" -> entry.doubleValue15;
            case "target_motion_y" -> entry.doubleValue16;
            case "target_motion_z" -> entry.doubleValue17;
            case "player_motion_x" -> entry.doubleValue18;
            case "player_motion_y" -> entry.doubleValue19;
            case "player_motion_z" -> entry.doubleValue20;
            case "prev_cmd_yaw" -> this.intValue3;
            case "prev_cmd_pitch" -> this.intValue4;
            case "rest_run_so_far_log" -> this.intValue5;
            case "in_gesture" -> this.intValue3 == 0 && this.intValue4 == 0 ? 0.0 : 1.0;
            default -> throw new IllegalStateException("unknown selector input " + this.intentSelector.texts[i]);
         });
      }

      return floats;
   }

   private double[] method7987(float[][] floatses) {
      float[] floatValues = new float[floatses.length];

      for (int i = 0; i < floatses.length; i++) {
         floatValues[i] = floatses[i][0];
      }

      int n = this.method7989(floatValues, 1.0);
      double[] doubles = new double[4];

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = floatses[n][1 + i];
      }

      return doubles;
   }

   private double[] method7988(double[] doubles, ReplayState.Entry entry) {
      double d = Math.max(Math.expm1(doubles[2]), 1.0);
      double d2 = Math.max(d - 2.0, 0.0);
      double sign = entry.doubleValue11 + entry.doubleValue9 * d2;
      double sign2 = entry.doubleValue12 + entry.doubleValue10 * d2;
      return new double[]{
         doubles[0] + IntentSelector.method7316((float)sign),
         doubles[1] + IntentSelector.method7316((float)sign2),
         doubles[2],
         doubles[3],
         0.0,
         0.0,
         IntentSelector.method7316((float)(entry.doubleValue9 * d)),
         IntentSelector.method7316((float)(entry.doubleValue10 * d))
      };
   }

   private int method7989(float[] floatValues, double d) {
      double d2 = Double.NEGATIVE_INFINITY;

      for (float floatValue : floatValues) {
         d2 = Math.max(d2, (double)floatValue);
      }

      double[] doubles = new double[floatValues.length];
      double d3 = 0.0;

      for (int i = 0; i < floatValues.length; i++) {
         doubles[i] = Math.exp((floatValues[i] - d2) / d);
         d3 += doubles[i];
      }

      double d4 = this.random.nextDouble() * d3;
      double d5 = 0.0;

      for (int i = 0; i < floatValues.length; i++) {
         d5 += doubles[i];
         if (d4 <= d5) {
            return i;
         }
      }

      return floatValues.length - 1;
   }

   private static int method7990(float[] floats) {
      int n = 0;

      for (int i = 1; i < floats.length; i++) {
         if (floats[i] > floats[n]) {
            n = i;
         }
      }

      return n;
   }

   private void method7991(int intValue, int n) {
      this.intValue3 = intValue;
      this.intValue4 = n;
      this.intValue5 = intValue == 0 && n == 0 ? this.intValue5 + 1 : 0;
   }

   private static double method7992(double d) {
      return 1.0 / (1.0 + Math.exp(-d));
   }

   private static double method7993(double d, double d2, double d3) {
      return Math.max(d2, Math.min(d3, d));
   }

   public static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public double doubleValue13;
      public double doubleValue14;
      public double doubleValue15;
      public double doubleValue16;
      public double doubleValue17;
      public double doubleValue18;
      public double doubleValue19;
      public double doubleValue20;
   }
}

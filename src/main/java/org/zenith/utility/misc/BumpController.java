package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.zenith.utility.api.PointClickDqn;

public final class BumpController {
   public static final double CONST_doubleValue = 0.05;
   public static final double CONST_doubleValue2 = 0.4608;
   public static final double CONST_doubleValue3 = 0.2592;
   public static final double CONST_doubleValue4 = 0.257;
   private static final int CONST_intValue = 2;
   private static final double CONST_doubleValue5 = 0.2;
   private static final double CONST_doubleValue6 = 0.02;
   private final PointClickDqn pointClickDqn;
   private final Random random;
   private final BumpController.Entry2 entry2;

   private BumpController(PointClickDqn pointClickDqn, long seed, BumpController.Entry2 entry2) {
      this.pointClickDqn = pointClickDqn;
      this.random = new Random(seed);
      this.entry2 = entry2;
   }

   public static BumpController method953(PointClickDqn pointClickDqn, long seed) {
      return pointClickDqn == null ? null : method955(pointClickDqn, seed);
   }

   public static BumpController method954(long seed) {
      return method955(null, seed);
   }

   private static BumpController method955(PointClickDqn pointClickDqn, long seed) {
      try {
         return new BumpController(pointClickDqn, seed, BumpController.Entry2.method976());
      } catch (Throwable e) {
         System.err.println("[PointClick] failed to load mouse calibration: " + e);
         return null;
      }
   }

   public BumpController.Entry4 method956(BumpController.Entry3 entry3) {
      float[] floats = new float[]{
         (float)entry3.doubleValue10,
         (float)entry3.doubleValue11,
         (float)entry3.doubleValue12,
         (float)entry3.doubleValue13,
         (float)entry3.doubleValue14,
         (float)entry3.doubleValue15,
         (float)entry3.doubleValue16,
         (float)entry3.doubleValue17,
         (float)entry3.doubleValue7,
         (float)entry3.doubleValue18,
         (float)entry3.doubleValue19
      };
      int intValue = this.pointClickDqn.method3226(floats);
      boolean flag = intValue / 25 != 0;
      int n = 2 + intValue % 25 * 2;
      int n2 = flag ? n : 2;
      BumpController.Entry entry = method963(2, entry3.doubleValue3, -entry3.doubleValue5, 0.4608, entry3.doubleValue7);
      BumpController.Entry entry2 = method963(2, entry3.doubleValue4, -entry3.doubleValue6, 0.2592, entry3.doubleValue7);
      entry.doubleValue2 *= -1.0;
      entry2.doubleValue2 *= -1.0;
      double[] doubles = this.method959(entry.doubleValue2, entry2.doubleValue2);
      entry.doubleValue2 = doubles[0];
      entry2.doubleValue2 = doubles[1];
      entry = method963(2, entry.doubleValue, entry.doubleValue2, 0.4608, entry3.doubleValue7);
      entry2 = method963(2, entry2.doubleValue, entry2.doubleValue2, 0.2592, entry3.doubleValue7);
      double doubleValue = entry.doubleValue;
      double d = entry2.doubleValue;
      double d2 = entry.doubleValue2;
      double d3 = entry2.doubleValue2;
      BumpController.Entry entry4 = method963(n, entry.doubleValue, entry.doubleValue2, 0.4608, entry3.doubleValue7);
      BumpController.Entry entry5 = method963(n, entry2.doubleValue, entry2.doubleValue2, 0.2592, entry3.doubleValue7);
      double[][] doubleses = method961(entry3.doubleValue10, entry3.doubleValue12, entry4.doubleValue, entry4.doubleValue2, n, n2);
      double[][] doubleses2 = method961(entry3.doubleValue11, entry3.doubleValue13, entry5.doubleValue, entry5.doubleValue2, n, n2);
      method964(doubleses, 0.4608);
      method964(doubleses2, 0.2592);
      double[] doubles2 = (double[])doubleses[1].clone();
      double[] doubles3 = (double[])doubleses2[1].clone();
      double[] doubles4 = new double[n2 + 1];

      for (int i = 0; i <= n2; i++) {
         doubles4[i] = this.entry2.method977(Math.hypot(doubles2[i], doubles3[i]));
         if (doubles4[i] == 0.0) {
            doubles4[i] = Double.MIN_NORMAL;
         }
      }

      double d4 = 0.0;

      for (int i = 0; i < n2; i++) {
         double x = (doubles2[i + 1] / doubles4[i + 1] - doubles2[i] / doubles4[i]) / 0.05;
         double y = (doubles3[i + 1] / doubles4[i + 1] - doubles3[i] / doubles4[i]) / 0.05;
         d4 += Math.hypot(x, y);
      }

      double[] doubleValues3 = method966(doubles2, doubles4);
      double[] doubleValues4 = method966(doubles3, doubles4);
      BumpController.Entry5 entry52 = this.entry2.method978(doubleValues3, doubleValues4, entry3.doubleValue8, entry3.doubleValue9, doubles4);
      double d5 = entry52.doubleValue - entry3.doubleValue8;
      double d6 = entry52.doubleValue2 - entry3.doubleValue9;
      BumpController.Entry6 entry6 = this.method960(doubleValues3, doubleValues4, n2, 0.0, 0.0, 0.2, 0.02);
      BumpController.Entry5 entry53 = this.entry2
         .method978((double[])entry6.doubleValues3.clone(), (double[])entry6.doubleValues4.clone(), entry3.doubleValue8, entry3.doubleValue9, doubles4);
      double[] doubleValues = method968(method967(entry6.doubleValues3, doubles4));
      double[] doubleValues2 = method968(method967(entry6.doubleValues4, doubles4));
      double d7 = (sum(entry53.doubleValues) - sum(doubleValues)) / (n2 * 0.05);
      double d8 = (sum(entry53.doubleValues2) - sum(doubleValues2)) / (n2 * 0.05);
      BumpController.Entry6 entry62 = this.method960(method967(entry6.doubleValues3, doubles4), method967(entry6.doubleValues4, doubles4), n2, d7, d8, 0.0, 0.0);
      method965(entry62.doubleValues, entry62.doubleValues3, entry3.doubleValue, 0.4608);
      method965(entry62.doubleValues2, entry62.doubleValues4, entry3.doubleValue2, 0.2592);
      BumpController.Entry entry7 = method963(n2, doubleValue, d2, 0.4608, entry3.doubleValue7);
      BumpController.Entry entry8 = method963(n2, d, d3, 0.2592, entry3.doubleValue7);
      return new BumpController.Entry4(
         intValue,
         n2,
         entry62.doubleValues,
         entry62.doubleValues2,
         entry3.doubleValue + doubleses[0][n2] - doubleses[0][0],
         entry3.doubleValue2 + doubleses2[0][n2] - doubleses2[0][0],
         doubleses[1][n2],
         doubleses2[1][n2],
         entry7.doubleValue,
         entry8.doubleValue,
         entry7.doubleValue2,
         entry8.doubleValue2,
         entry53.doubleValue,
         entry53.doubleValue2,
         entry3.doubleValue8 + d5,
         entry3.doubleValue9 + d6,
         d4
      );
   }

   public BumpController.Entry4 method957(BumpController.Entry3 entry3, int n, double doubleValue, double d, double d2, double d3) {
      if (n >= 2 && n <= 50) {
         double[][] doubleses = method961(entry3.doubleValue10, entry3.doubleValue12, doubleValue, d2, n, n);
         double[][] doubleses2 = method961(entry3.doubleValue11, entry3.doubleValue13, d, d3, n, n);
         return this.method958(entry3, n, doubleses, doubleses2);
      } else {
         throw new IllegalArgumentException("BUMP horizon must be in [2, 50]");
      }
   }

   private BumpController.Entry4 method958(BumpController.Entry3 entry3, int n, double[][] doubleses, double[][] doubleses2) {
      double[] doubles = (double[])doubleses[1].clone();
      double[] doubles2 = (double[])doubleses2[1].clone();
      double[] doubles3 = new double[n + 1];

      for (int i = 0; i <= n; i++) {
         doubles3[i] = this.entry2.method977(Math.hypot(doubles[i], doubles2[i]));
         if (doubles3[i] == 0.0) {
            doubles3[i] = Double.MIN_NORMAL;
         }
      }

      double d = 0.0;

      for (int i = 0; i < n; i++) {
         double x = (doubles[i + 1] / doubles3[i + 1] - doubles[i] / doubles3[i]) / 0.05;
         double y = (doubles2[i + 1] / doubles3[i + 1] - doubles2[i] / doubles3[i]) / 0.05;
         d += Math.hypot(x, y);
      }

      double[] doubleValues3 = method966(doubles, doubles3);
      double[] doubleValues4 = method966(doubles2, doubles3);
      BumpController.Entry5 entry5 = this.entry2.method978(doubleValues3, doubleValues4, entry3.doubleValue8, entry3.doubleValue9, doubles3);
      double d2 = entry5.doubleValue - entry3.doubleValue8;
      double d3 = entry5.doubleValue2 - entry3.doubleValue9;
      BumpController.Entry6 entry6 = this.method960(doubleValues3, doubleValues4, n, 0.0, 0.0, 0.2, 0.02);
      BumpController.Entry5 entry52 = this.entry2
         .method978((double[])entry6.doubleValues3.clone(), (double[])entry6.doubleValues4.clone(), entry3.doubleValue8, entry3.doubleValue9, doubles3);
      double[] doubleValues = method968(method967(entry6.doubleValues3, doubles3));
      double[] doubleValues2 = method968(method967(entry6.doubleValues4, doubles3));
      double d4 = (sum(entry52.doubleValues) - sum(doubleValues)) / (n * 0.05);
      double d5 = (sum(entry52.doubleValues2) - sum(doubleValues2)) / (n * 0.05);
      BumpController.Entry6 entry62 = this.method960(method967(entry6.doubleValues3, doubles3), method967(entry6.doubleValues4, doubles3), n, d4, d5, 0.0, 0.0);
      return new BumpController.Entry4(
         -1,
         n,
         entry62.doubleValues,
         entry62.doubleValues2,
         entry3.doubleValue + doubleses[0][n] - doubleses[0][0],
         entry3.doubleValue2 + doubleses2[0][n] - doubleses2[0][0],
         doubleses[1][n],
         doubleses2[1][n],
         entry3.doubleValue14,
         entry3.doubleValue15,
         entry3.doubleValue16,
         entry3.doubleValue17,
         entry52.doubleValue,
         entry52.doubleValue2,
         entry3.doubleValue8 + d2,
         entry3.doubleValue9 + d3,
         d
      );
   }

   private double[] method959(double x, double y) {
      double hypot = Math.hypot(x, y);
      if (hypot <= 0.0) {
         hypot = Double.MIN_NORMAL;
      }

      double d = 2.0 * Math.toDegrees(Math.atan(hypot / 1.26));
      double log = Math.log(1.0 + d / 0.3);
      double exp = Math.exp(log + 0.15 * this.random.nextGaussian());
      double angdeg = Math.max((exp - 1.0) * 0.3, 0.0);
      double d2 = 1.26 * Math.tan(Math.toRadians(angdeg) / 2.0);
      return new double[]{d2 * x / hypot, d2 * y / hypot};
   }

   private BumpController.Entry6 method960(double[] doubleValues3, double[] doubleValues4, int n, double d, double d2, double d3, double d4) {
      double[] doubles = (double[])doubleValues3.clone();
      double[] doubleValues42 = (double[])doubleValues4.clone();

      for (int i = 1; i <= n; i++) {
         double x = doubles[i];
         double y = doubleValues42[i];
         double hypot = Math.hypot(x, y);
         double d5 = hypot == 0.0 ? 0.0 : x / hypot;
         double d6 = hypot == 0.0 ? 0.0 : y / hypot;
         double d7 = -d6;
         double d8 = d3 * hypot * this.random.nextGaussian();
         double d9 = d4 * hypot * this.random.nextGaussian();
         doubles[i] = x + d8 * d5 + d9 * d7 + d;
         doubleValues42[i] = y + d8 * d6 + d9 * d5 + d2;
      }

      return new BumpController.Entry6(method968(doubles), method968(doubleValues42), doubles, doubleValues42);
   }

   private static double[][] method961(double d, double d2, double doubleValue, double d3, int n, int n2) {
      double[][] doubleses = new double[][]{{1.0, 0.05}, {0.0, 1.0}};
      double[] doubles = new double[]{0.0012500000000000002, 0.05};
      double[][] doubleses2 = method969(doubleses, n);
      double[][] doubleses3 = method970(method962(n, n, doubleses, doubles));
      double[] doubles2 = new double[]{d, d2};
      double[] doubles3 = method975(new double[]{doubleValue, d3}, method974(doubleses2, doubles2));
      double[] doubles4 = method974(doubleses3, doubles3);
      double[][] doubleses4 = new double[2][n2 + 1];

      for (int i = 0; i <= n2; i++) {
         double[] doubles5 = method974(method969(doubleses, i), doubles2);
         double[] doubles6 = method974(method962(i, n, doubleses, doubles), doubles4);
         doubleses4[0][i] = doubles5[0] + doubles6[0];
         doubleses4[1][i] = doubles5[1] + doubles6[1];
      }

      return doubleses4;
   }

   private static double[][] method962(int n, int n2, double[][] doubleses, double[] doubles) {
      double[][] doubleses2 = new double[2][2];
      double[][] doubleses3 = new double[][]{{doubles[0] * doubles[0], doubles[0] * doubles[1]}, {doubles[1] * doubles[0], doubles[1] * doubles[1]}};
      double[][] doubleses4 = method971(doubleses);

      for (int i = 0; i < n; i++) {
         doubleses2 = method972(doubleses2, method973(method973(method969(doubleses, i), doubleses3), method969(doubleses4, n2 - n + i)));
      }

      return doubleses2;
   }

   private static BumpController.Entry method963(int n, double doubleValue, double d, double d2, double d3) {
      double d4 = d2 - d3;
      double d5 = d3;

      for (int i = 0; i < n; i++) {
         double d6 = doubleValue + 0.05 * d;
         if (d6 >= d5 && d6 <= d4) {
            doubleValue = d6;
         } else if (d6 > d4) {
            doubleValue = 2.0 * d4 - d6;
            d *= -1.0;
         } else {
            doubleValue = 2.0 * d5 - d6;
            d *= -1.0;
         }
      }

      return new BumpController.Entry(doubleValue, d);
   }

   private static void method964(double[][] doubleses, double d) {
      for (int i = 0; i < doubleses[0].length; i++) {
         if (doubleses[0][i] <= 0.0) {
            doubleses[0][i] = Double.MIN_NORMAL;
            doubleses[1][i] = 0.0;
         } else if (doubleses[0][i] >= d) {
            doubleses[0][i] = d;
            doubleses[1][i] = 0.0;
         }
      }
   }

   private static void method965(double[] doubleValues, double[] doubleValues3, double doubleValue, double d) {
      double d2 = 0.0;

      for (int i = 0; i < doubleValues.length; i++) {
         double d3 = doubleValue + d2 + doubleValues[i];
         if (d3 <= 0.0) {
            doubleValues[i] = -(doubleValue + d2);
            doubleValues3[i + 1] = 0.0;
         } else if (d3 >= d) {
            doubleValues[i] = d - (doubleValue + d2);
            doubleValues3[i + 1] = 0.0;
         }

         d2 += doubleValues[i];
      }
   }

   private static double[] method966(double[] doubles, double[] doubles2) {
      double[] doubles3 = new double[doubles.length];

      for (int i = 0; i < doubles3.length; i++) {
         doubles3[i] = doubles[i] / doubles2[i];
      }

      return doubles3;
   }

   private static double[] method967(double[] doubleValues3, double[] doubles) {
      double[] doubles2 = new double[doubleValues3.length];

      for (int i = 0; i < doubles2.length; i++) {
         doubles2[i] = doubleValues3[i] * doubles[i];
      }

      return doubles2;
   }

   private static double[] method968(double[] doubleValues3) {
      double[] doubles = new double[doubleValues3.length - 1];

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = (doubleValues3[i + 1] + doubleValues3[i]) * 0.5 * 0.05;
      }

      return doubles;
   }

   private static double sum(double[] doubleValues) {
      double d = 0.0;

      for (double doubleValue : doubleValues) {
         d += doubleValue;
      }

      return d;
   }

   private static double[][] method969(double[][] doubleses, int n) {
      double[][] doubleses2 = new double[][]{{1.0, 0.0}, {0.0, 1.0}};
      double[][] doubleses3 = doubleses;

      for (int i = n; i > 0; i >>= 1) {
         if ((i & 1) != 0) {
            doubleses2 = method973(doubleses2, doubleses3);
         }

         doubleses3 = method973(doubleses3, doubleses3);
      }

      return doubleses2;
   }

   private static double[][] method970(double[][] doubleses) {
      double d = doubleses[0][0] * doubleses[1][1] - doubleses[0][1] * doubleses[1][0];
      if (Math.abs(d) < 1.0E-18) {
         throw new IllegalStateException("singular BUMP grammian");
      } else {
         return new double[][]{{doubleses[1][1] / d, -doubleses[0][1] / d}, {-doubleses[1][0] / d, doubleses[0][0] / d}};
      }
   }

   private static double[][] method971(double[][] doubleses) {
      return new double[][]{{doubleses[0][0], doubleses[1][0]}, {doubleses[0][1], doubleses[1][1]}};
   }

   private static double[][] method972(double[][] doubleses, double[][] doubleses2) {
      return new double[][]{{doubleses[0][0] + doubleses2[0][0], doubleses[0][1] + doubleses2[0][1]}, {doubleses[1][0] + doubleses2[1][0], doubleses[1][1] + doubleses2[1][1]}};
   }

   private static double[][] method973(double[][] doubleses, double[][] doubleses2) {
      return new double[][]{
         {doubleses[0][0] * doubleses2[0][0] + doubleses[0][1] * doubleses2[1][0], doubleses[0][0] * doubleses2[0][1] + doubleses[0][1] * doubleses2[1][1]},
         {doubleses[1][0] * doubleses2[0][0] + doubleses[1][1] * doubleses2[1][0], doubleses[1][0] * doubleses2[0][1] + doubleses[1][1] * doubleses2[1][1]}
      };
   }

   private static double[] method974(double[][] doubleses, double[] doubles) {
      return new double[]{doubleses[0][0] * doubles[0] + doubleses[0][1] * doubles[1], doubleses[1][0] * doubles[0] + doubleses[1][1] * doubles[1]};
   }

   private static double[] method975(double[] doubles, double[] doubles2) {
      return new double[]{doubles[0] - doubles2[0], doubles[1] - doubles2[1]};
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;

      public Entry(double doubleValue, double d) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
      }
   }

   private static final class Entry2 {
      private static final String text = "/assets/zenith/pointclick/mouse_gain.txt";
      private static final String text2 = "/assets/zenith/pointclick/rotation_map_rad.csv";
      private final double[] doubleValues;
      private final double[] doubleValues2;
      private final double[][] doubleValues3;

      private Entry2(double[] doubleValues, double[] doubleValues2, double[][] doubleValues3) {
         this.doubleValues = doubleValues;
         this.doubleValues2 = doubleValues2;
         this.doubleValues3 = doubleValues3;
      }

      public static BumpController.Entry2 method976() throws Exception {
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();

         String line;
         try (BufferedReader bufferedReader = method984("/assets/zenith/pointclick/mouse_gain.txt")) {
            while ((line = bufferedReader.readLine()) != null) {
               String[] strings = line.trim().split(":\\s*");
               if (strings.length == 2) {
                  double d = Double.parseDouble(strings[0]);
                  double d2 = Double.parseDouble(strings[1]);
                  double d3 = 0.0254 * (d / 400.0) * 125.0;
                  double d4 = 0.0254 * (d2 / 110.0) * 125.0;
                  arrayList.add(d4);
                  arrayList2.add(d3 == 0.0 ? 0.0 : d4 / d3);
               }
            }
         }

         double[][] doubleValues3 = new double[31][31];

         try (BufferedReader bufferedReader = method984("/assets/zenith/pointclick/rotation_map_rad.csv")) {
            for (int i = 0; i < doubleValues3.length; i++) {
               String str = bufferedReader.readLine();
               if (str == null) {
                  throw new IllegalStateException("rotation map has fewer than 31 rows");
               }

               String[] strings = str.split(",");
               if (strings.length != 31) {
                  throw new IllegalStateException("rotation map row has " + strings.length + " columns");
               }

               for (int j = 0; j < strings.length; j++) {
                  doubleValues3[i][j] = Double.parseDouble(strings[j]);
               }
            }
         }

         return new BumpController.Entry2(method985(arrayList), method985(arrayList2), doubleValues3);
      }

      public double method977(double hypot) {
         return method983(this.doubleValues, this.doubleValues2, hypot);
      }

      public BumpController.Entry5 method978(double[] doubleValues3, double[] doubleValues4, double doubleValue, double d, double[] doubles) {
         double[] doubleValues = BumpController.method968(doubleValues3);
         double[] doubleValues2 = BumpController.method968(doubleValues4);
         double d2 = this.method980(doubleValue, d);

         for (int i = 0; i < doubleValues.length; i++) {
            if (doubleValues[i] != 0.0 || doubleValues2[i] != 0.0) {
               double doubleValue2 = doubleValue;
               double d3 = d;
               double d4 = Math.cos(-d2);
               double d5 = Math.sin(-d2);
               double d6 = d4 * doubleValues[i] - d5 * doubleValues2[i];
               double d7 = d5 * doubleValues[i] + d4 * doubleValues2[i];
               doubleValue += d6;
               d += d7;
               double[] doubles2 = this.method979(doubleValue2, d3, doubleValue, d, doubles[i]);
               doubleValues[i] = doubles2[0];
               doubleValues2[i] = doubles2[1];
            }
         }

         return new BumpController.Entry5(doubleValues, doubleValues2, doubleValue, d);
      }

      private double[] method979(double doubleValue, double d, double doubleValue2, double d2, double d3) {
         double d4 = this.method980(doubleValue2, d2);
         double d5 = this.method980(doubleValue, d);
         double hypot = Math.hypot(doubleValue2 - doubleValue, d2 - d);
         double d6 = d4 - d5;
         if (d6 == 0.0) {
            d6 = Double.MIN_NORMAL;
         }

         double d7 = d3 * hypot * 2.0 * Math.sin(d6 / 2.0) / d6;
         double d8 = hypot == 0.0 ? Double.MIN_NORMAL : hypot;
         double d9 = (doubleValue2 - doubleValue) / d8;
         double d10 = (d2 - d) / d8;
         double d11 = d6 / 2.0 + d5;
         return new double[]{(d9 * Math.cos(d11) - d10 * Math.sin(d11)) * d7, (d9 * Math.sin(d11) + d10 * Math.cos(d11)) * d7};
      }

      private double method980(double doubleValue, double d) {
         double d2 = doubleValue / 0.257;
         double d3 = d / 0.257;
         double d4 = (d2 + 1.5) / 0.1;
         double d5 = (1.5 - d3) / 0.1;
         return method981(this.doubleValues3, d5, d4);
      }

      private static double method981(double[][] doubleValues3, double d, double d2) {
         int n = (int)Math.floor(d);
         int n2 = (int)Math.floor(d2);
         double d3 = d - n;
         double d4 = d2 - n2;
         double[] doubles = new double[4];

         for (int i = -1; i <= 2; i++) {
            int n3 = method986(n + i, 0, doubleValues3.length - 1);
            doubles[i + 1] = method982(
               doubleValues3[n3][method986(n2 - 1, 0, doubleValues3[n3].length - 1)],
               doubleValues3[n3][method986(n2, 0, doubleValues3[n3].length - 1)],
               doubleValues3[n3][method986(n2 + 1, 0, doubleValues3[n3].length - 1)],
               doubleValues3[n3][method986(n2 + 2, 0, doubleValues3[n3].length - 1)],
               d4
            );
         }

         return method982(doubles[0], doubles[1], doubles[2], doubles[3], d3);
      }

      private static double method982(double d, double d2, double d3, double d4, double d5) {
         return 0.5
            * (
               2.0 * d2
                  + (-d + d3) * d5
                  + (2.0 * d - 5.0 * d2 + 4.0 * d3 - d4) * d5 * d5
                  + (-d + 3.0 * d2 - 3.0 * d3 + d4) * d5 * d5 * d5
            );
      }

      private static double method983(double[] doubleValues, double[] doubleValues2, double hypot) {
         int n = 1;

         while (n < doubleValues.length && doubleValues[n] < hypot) {
            n++;
         }

         if (n >= doubleValues.length) {
            n = doubleValues.length - 1;
         }

         int n2 = Math.max(n - 1, 0);
         if (doubleValues[n] == doubleValues[n2]) {
            return doubleValues2[n2];
         } else {
            double d = (hypot - doubleValues[n2]) / (doubleValues[n] - doubleValues[n2]);
            return doubleValues2[n2] + d * (doubleValues2[n] - doubleValues2[n2]);
         }
      }

      private static BufferedReader method984(String name) {
         InputStream resourceAsStream = BumpController.class.getResourceAsStream(name);
         if (resourceAsStream == null) {
            throw new IllegalStateException("missing resource " + name);
         } else {
            return new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
         }
      }

      private static double[] method985(List<Double> doubles) {
         double[] doubles2 = new double[doubles.size()];

         for (int i = 0; i < doubles2.length; i++) {
            doubles2[i] = (Double)doubles.get(i);
         }

         return doubles2;
      }

      private static int method986(int n, int n2, int n3) {
         return Math.max(n2, Math.min(n, n3));
      }
   }

   public static final class Entry3 {
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
   }

   public static final class Entry4 {
      public final int intValue;
      public final int intValue2;
      public final double[] doubleValues;
      public final double[] doubleValues2;
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final double doubleValue4;
      public final double doubleValue5;
      public final double doubleValue6;
      public final double doubleValue7;
      public final double doubleValue8;
      public final double doubleValue9;
      public final double doubleValue10;
      public final double doubleValue11;
      public final double doubleValue12;
      public final double doubleValue13;

      private Entry4(
         int intValue,
         int n,
         double[] doubleValues,
         double[] doubleValues2,
         double doubleValue,
         double d,
         double d2,
         double d3,
         double doubleValue2,
         double doubleValue3,
         double d4,
         double d5,
         double doubleValue4,
         double d6,
         double d7,
         double d8,
         double d9
      ) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.doubleValues = doubleValues;
         this.doubleValues2 = doubleValues2;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.doubleValue4 = d3;
         this.doubleValue5 = doubleValue2;
         this.doubleValue6 = doubleValue3;
         this.doubleValue7 = d4;
         this.doubleValue8 = d5;
         this.doubleValue9 = doubleValue4;
         this.doubleValue10 = d6;
         this.doubleValue11 = d7;
         this.doubleValue12 = d8;
         this.doubleValue13 = d9;
      }
   }

   private static final class Entry5 {
      public final double[] doubleValues;
      public final double[] doubleValues2;
      public final double doubleValue;
      public final double doubleValue2;

      public Entry5(double[] doubleValues, double[] doubleValues2, double doubleValue, double d) {
         this.doubleValues = doubleValues;
         this.doubleValues2 = doubleValues2;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
      }
   }

   private static final class Entry6 {
      public final double[] doubleValues;
      public final double[] doubleValues2;
      public final double[] doubleValues3;
      public final double[] doubleValues4;

      public Entry6(double[] doubleValues, double[] doubleValues2, double[] doubleValues3, double[] doubleValues4) {
         this.doubleValues = doubleValues;
         this.doubleValues2 = doubleValues2;
         this.doubleValues3 = doubleValues3;
         this.doubleValues4 = doubleValues4;
      }
   }
}

package org.zenith.utility.misc;

import java.util.Arrays;

public final class MeshImporter {
   private static final int[][] CONST_intValues = new int[][]{{0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {1, 1, 0}, {0, 0, 1}, {1, 0, 1}, {0, 1, 1}, {1, 1, 1}};
   private static final int[][] CONST_intValues2 = new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}, {0, 2}, {1, 3}, {4, 6}, {5, 7}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};

   private MeshImporter() {
   }

   public static MeshImporter.Entry method658(Class32 class2, FloatArrayBuffer floatArrayBuffer, int n, int n2, int n3, int n4, int n5, int n6) {
      MeshImporter.Entry entry = new MeshImporter.Entry();
      floatArrayBuffer.clear();
      if (n4 > 1 && n5 > 1 && n6 > 1) {
         int[] ints = new int[n4 * n5 * n6];
         Arrays.fill(ints, -1);
         float[] floats = new float[1024];
         float[] floats2 = new float[1024];
         float[] floats3 = new float[1024];
         float[] floats4 = new float[1024];
         float[] floats5 = new float[1024];
         float[] floats6 = new float[1024];
         int intValue = 0;
         float[] floats7 = new float[8];

         for (int i = 0; i < n6 - 1; i++) {
            for (int j = 0; j < n5 - 1; j++) {
               for (int k = 0; k < n4 - 1; k++) {
                  int n7 = 0;

                  for (int m = 0; m < 8; m++) {
                     floats7[m] = class2.method4903(
                        n + k + CONST_intValues[m][0], n2 + j + CONST_intValues[m][1], n3 + i + CONST_intValues[m][2]
                     );
                     if (floats7[m] >= 0.0F) {
                        n7++;
                     }
                  }

                  if (n7 != 0 && n7 != 8) {
                     float f = 0.0F;
                     float f2 = 0.0F;
                     float f3 = 0.0F;
                     int n8 = 0;

                     for (int[] ints2 : CONST_intValues2) {
                        float f4 = floats7[ints2[0]];
                        float f5 = floats7[ints2[1]];
                        if (f4 >= 0.0F != f5 >= 0.0F) {
                           float f6 = f4 / (f4 - f5);
                           f += CONST_intValues[ints2[0]][0] + f6 * (CONST_intValues[ints2[1]][0] - CONST_intValues[ints2[0]][0]);
                           f2 += CONST_intValues[ints2[0]][1] + f6 * (CONST_intValues[ints2[1]][1] - CONST_intValues[ints2[0]][1]);
                           f3 += CONST_intValues[ints2[0]][2] + f6 * (CONST_intValues[ints2[1]][2] - CONST_intValues[ints2[0]][2]);
                           n8++;
                        }
                     }

                     if (n8 != 0) {
                        if (intValue == floats.length) {
                           floats = method662(floats);
                           floats2 = method662(floats2);
                           floats3 = method662(floats3);
                           floats4 = method662(floats4);
                           floats5 = method662(floats5);
                           floats6 = method662(floats6);
                        }

                        floats[intValue] = n + k + f / n8;
                        floats2[intValue] = n2 + j + f2 / n8;
                        floats3[intValue] = n3 + i + f3 / n8;
                        float f4 = floats7[0] + floats7[2] + floats7[4] + floats7[6] - (floats7[1] + floats7[3] + floats7[5] + floats7[7]);
                        float f5 = floats7[0] + floats7[1] + floats7[4] + floats7[5] - (floats7[2] + floats7[3] + floats7[6] + floats7[7]);
                        float f6 = floats7[0] + floats7[1] + floats7[2] + floats7[3] - (floats7[4] + floats7[5] + floats7[6] + floats7[7]);
                        float f7 = (float)Math.sqrt(f4 * f4 + f5 * f5 + f6 * f6);
                        if (f7 < 1.0E-6F) {
                           f4 = 0.0F;
                           f5 = 1.0F;
                           f6 = 0.0F;
                        } else {
                           f4 /= f7;
                           f5 /= f7;
                           f6 /= f7;
                        }

                        floats4[intValue] = f4;
                        floats5[intValue] = f5;
                        floats6[intValue] = f6;
                        ints[method661(k, j, i, n4, n5)] = intValue++;
                     }
                  }
               }
            }
         }

         entry.intValue = intValue;
         float[] floats8 = new float[12];
         float[] floats9 = new float[12];

         for (int i = 1; i < n6 - 1; i++) {
            for (int j = 1; j < n5 - 1; j++) {
               for (int k = 1; k < n4 - 1; k++) {
                  float f = class2.method4903(n + k, n2 + j, n3 + i);
                  boolean flag = f >= 0.0F;
                  if (flag != class2.method4903(n + k + 1, n2 + j, n3 + i) >= 0.0F) {
                     method659(
                        floatArrayBuffer,
                        ints,
                        n4,
                        n5,
                        floats8,
                        floats9,
                        floats,
                        floats2,
                        floats3,
                        floats4,
                        floats5,
                        floats6,
                        k,
                        j - 1,
                        i - 1,
                        k,
                        j,
                        i - 1,
                        k,
                        j,
                        i,
                        k,
                        j - 1,
                        i,
                        flag,
                        entry
                     );
                  }

                  if (flag != class2.method4903(n + k, n2 + j + 1, n3 + i) >= 0.0F) {
                     method659(
                        floatArrayBuffer,
                        ints,
                        n4,
                        n5,
                        floats8,
                        floats9,
                        floats,
                        floats2,
                        floats3,
                        floats4,
                        floats5,
                        floats6,
                        k - 1,
                        j,
                        i - 1,
                        k - 1,
                        j,
                        i,
                        k,
                        j,
                        i,
                        k,
                        j,
                        i - 1,
                        flag,
                        entry
                     );
                  }

                  if (flag != class2.method4903(n + k, n2 + j, n3 + i + 1) >= 0.0F) {
                     method659(
                        floatArrayBuffer,
                        ints,
                        n4,
                        n5,
                        floats8,
                        floats9,
                        floats,
                        floats2,
                        floats3,
                        floats4,
                        floats5,
                        floats6,
                        k - 1,
                        j - 1,
                        i,
                        k,
                        j - 1,
                        i,
                        k,
                        j,
                        i,
                        k - 1,
                        j,
                        i,
                        flag,
                        entry
                     );
                  }
               }
            }
         }

         return entry;
      } else {
         return entry;
      }
   }

   private static void method659(
      FloatArrayBuffer floatArrayBuffer,
      int[] ints,
      int n,
      int n2,
      float[] floats,
      float[] floats2,
      float[] floats3,
      float[] floats4,
      float[] floats5,
      float[] floats6,
      float[] floats7,
      float[] floats8,
      int n3,
      int n4,
      int n5,
      int n6,
      int n7,
      int n8,
      int n9,
      int n10,
      int n11,
      int n12,
      int n13,
      int n14,
      boolean flag,
      MeshImporter.Entry entry
   ) {
      int n15 = method660(ints, n, n2, n3, n4, n5);
      int n16 = method660(ints, n, n2, n6, n7, n8);
      int n17 = method660(ints, n, n2, n9, n10, n11);
      int n18 = method660(ints, n, n2, n12, n13, n14);
      if (n15 >= 0 && n16 >= 0 && n17 >= 0 && n18 >= 0) {
         int[] ints2 = flag ? new int[]{n15, n16, n17, n18} : new int[]{n15, n18, n17, n16};

         for (int i = 0; i < 4; i++) {
            int n19 = ints2[i];
            floats[i * 3] = floats3[n19];
            floats[i * 3 + 1] = floats4[n19];
            floats[i * 3 + 2] = floats5[n19];
            floats2[i * 3] = floats6[n19];
            floats2[i * 3 + 1] = floats7[n19];
            floats2[i * 3 + 2] = floats8[n19];
         }

         floatArrayBuffer.method379(floats, floats2);
      } else {
         entry.skipped++;
      }
   }

   private static int method660(int[] ints, int n, int n2, int n3, int n4, int n5) {
      return n3 >= 0 && n4 >= 0 && n5 >= 0 ? ints[method661(n3, n4, n5, n, n2)] : -1;
   }

   private static int method661(int n, int n2, int n3, int n4, int n5) {
      return (n3 * n5 + n2) * n4 + n;
   }

   private static float[] method662(float[] floats) {
      float[] floats2 = new float[floats.length * 2];
      System.arraycopy(floats, 0, floats2, 0, floats.length);
      return floats2;
   }

   public static final class Entry {
      public int intValue;
      public int skipped;

      @Override
      public String toString() {
         return "вершин " + this.intValue + ", пропущено граней " + this.skipped;
      }
   }
}

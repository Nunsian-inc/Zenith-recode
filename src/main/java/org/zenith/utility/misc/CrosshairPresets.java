package org.zenith.utility.misc;

public final class CrosshairPresets {
   public static final String[] texts = new String[]{
      "crosshair.preset.cross",
      "crosshair.preset.dot",
      "crosshair.preset.crossDot",
      "crosshair.preset.tshape",
      "crosshair.preset.circle",
      "crosshair.preset.diamond",
      "crosshair.preset.brackets",
      "crosshair.preset.chevron"
   };
   private static final int CONST_intValue = -1;
   private static final int CONST_intValue2 = 15;

   private CrosshairPresets() {
   }

   public static int[] method5712(int n) {
      int[] ints = new int[961];
      switch (n) {
         case 1:
            method5715(ints);
            break;
         case 2:
            method5714(ints, 3, 5);
            method5715(ints);
            break;
         case 3:
            method5716(ints);
            break;
         case 4:
            method5717(ints, 6);
            break;
         case 5:
            method5718(ints, 6);
            break;
         case 6:
            method5719(ints, 7, 3);
            break;
         case 7:
            method5720(ints);
            break;
         default:
            method5714(ints, 3, 5);
      }

      return ints;
   }

   private static void method5713(int[] ints, int n, int n2) {
      if (n >= 0 && n2 >= 0 && n < 31 && n2 < 31) {
         ints[n2 * 31 + n] = -1;
      }
   }

   private static void method5714(int[] ints, int n, int n2) {
      for (int i = n; i < n + n2; i++) {
         method5713(ints, 15, 15 - i);
         method5713(ints, 15, 15 + i);
         method5713(ints, 15 - i, 15);
         method5713(ints, 15 + i, 15);
      }
   }

   private static void method5715(int[] ints) {
      method5713(ints, 15, 15);
   }

   private static void method5716(int[] ints) {
      for (int i = 3; i < 8; i++) {
         method5713(ints, 15 - i, 15);
         method5713(ints, 15 + i, 15);
         method5713(ints, 15, 15 + i);
      }
   }

   private static void method5717(int[] ints, int n) {
      int n2 = n;
      int n3 = 0;
      int n4 = 1 - n;

      while (n2 >= n3) {
         method5713(ints, 15 + n2, 15 + n3);
         method5713(ints, 15 + n3, 15 + n2);
         method5713(ints, 15 - n3, 15 + n2);
         method5713(ints, 15 - n2, 15 + n3);
         method5713(ints, 15 - n2, 15 - n3);
         method5713(ints, 15 - n3, 15 - n2);
         method5713(ints, 15 + n3, 15 - n2);
         method5713(ints, 15 + n2, 15 - n3);
         n3++;
         if (n4 < 0) {
            n4 += 2 * n3 + 1;
         } else {
            n2--;
            n4 += 2 * (n3 - n2) + 1;
         }
      }
   }

   private static void method5718(int[] ints, int n) {
      for (int i = 0; i < n; i++) {
         int n2 = n - i;
         method5713(ints, 15 + i, 15 + n2);
         method5713(ints, 15 + i, 15 - n2);
         method5713(ints, 15 - i, 15 + n2);
         method5713(ints, 15 - i, 15 - n2);
      }
   }

   private static void method5719(int[] ints, int n, int n2) {
      for (int i = 0; i < n2; i++) {
         method5713(ints, 15 - n + i, 15 - n);
         method5713(ints, 15 - n, 15 - n + i);
         method5713(ints, 15 + n - i, 15 - n);
         method5713(ints, 15 + n, 15 - n + i);
         method5713(ints, 15 - n + i, 15 + n);
         method5713(ints, 15 - n, 15 + n - i);
         method5713(ints, 15 + n - i, 15 + n);
         method5713(ints, 15 + n, 15 + n - i);
      }
   }

   private static void method5720(int[] ints) {
      for (int i = 0; i <= 6; i++) {
         method5713(ints, 15 - i, 15 + i - 3);
         method5713(ints, 15 + i, 15 + i - 3);
      }
   }
}

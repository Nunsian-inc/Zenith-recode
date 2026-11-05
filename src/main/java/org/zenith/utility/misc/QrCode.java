package org.zenith.utility.misc;

import java.io.ByteArrayOutputStream;

public final class QrCode {
   private static final int[] CONST_intValues = new int[]{0, 26, 44, 70, 100, 134, 172, 196, 242, 292, 346};
   private static final int[] CONST_intValues2 = new int[]{0, 10, 16, 26, 18, 24, 16, 18, 22, 22, 26};
   private static final int[] CONST_intValues3 = new int[]{0, 1, 1, 1, 2, 2, 4, 4, 4, 5, 5};
   private static final int[][] CONST_intValues4 = new int[][]{
      new int[0], new int[0], {6, 18}, {6, 22}, {6, 26}, {6, 30}, {6, 34}, {6, 22, 38}, {6, 24, 42}, {6, 26, 46}, {6, 28, 50}
   };
   private final int intValue;
   private final int intValue2;
   private final boolean[][] flags;
   private final boolean[][] flags2;

   private QrCode(int intValue) {
      this.intValue = intValue;
      this.intValue2 = intValue * 4 + 17;
      this.flags = new boolean[this.intValue2][this.intValue2];
      this.flags2 = new boolean[this.intValue2][this.intValue2];
   }

   public static boolean[][] method3020(byte[] bytes) {
      int intValue;
      for (intValue = 1; intValue <= 10; intValue++) {
         int n = (CONST_intValues[intValue] - CONST_intValues2[intValue] * CONST_intValues3[intValue]) * 8;
         int n2 = 4 + (intValue < 10 ? 8 : 16) + bytes.length * 8;
         if (n2 <= n) {
            break;
         }
      }

      if (intValue > 10) {
         throw new IllegalArgumentException("текст слишком длинный для QR (до 213 байт)");
      } else {
         int n = CONST_intValues[intValue] - CONST_intValues2[intValue] * CONST_intValues3[intValue];
         QrCode.Entry entry = new QrCode.Entry();
         entry.method3033(4, 4);
         entry.method3033(bytes.length, intValue < 10 ? 8 : 16);

         for (int n2 : bytes) {
            entry.method3033(n2 & 255, 8);
         }

         int n2 = n * 8;
         entry.method3033(0, Math.min(4, n2 - entry.length));
         entry.method3033(0, (8 - entry.length % 8) % 8);

         for (short i = 236; entry.length < n2; i ^= 253) {
            entry.method3033(i, 8);
         }

         byte[] bytes2 = entry.method3034();
         QrCode qrCode = new QrCode(intValue);
         qrCode.method3022();
         qrCode.method3030(qrCode.method3026(bytes2));
         int n3 = 0;
         long l = Long.MAX_VALUE;

         for (int i = 0; i < 8; i++) {
            qrCode.method3031(i);
            qrCode.method3025(i);
            long l2 = qrCode.method3032();
            if (l2 < l) {
               l = l2;
               n3 = i;
            }

            qrCode.method3031(i);
         }

         qrCode.method3031(n3);
         qrCode.method3025(n3);
         return qrCode.flags;
      }
   }

   private void method3021(int n, int n2, boolean flag) {
      this.flags[n2][n] = flag;
      this.flags2[n2][n] = true;
   }

   private void method3022() {
      for (int i = 0; i < this.intValue2; i++) {
         this.method3021(6, i, i % 2 == 0);
         this.method3021(i, 6, i % 2 == 0);
      }

      this.method3023(3, 3);
      this.method3023(this.intValue2 - 4, 3);
      this.method3023(3, this.intValue2 - 4);
      int[] ints = CONST_intValues4[this.intValue];

      for (int i = 0; i < ints.length; i++) {
         for (int j = 0; j < ints.length; j++) {
            boolean flag = i == 0 && j == 0 || i == 0 && j == ints.length - 1 || i == ints.length - 1 && j == 0;
            if (!flag) {
               this.method3024(ints[i], ints[j]);
            }
         }
      }

      this.method3025(0);
      if (this.intValue >= 7) {
         int intValue = this.intValue;

         for (int i = 0; i < 12; i++) {
            intValue = intValue << 1 ^ (intValue >>> 11) * 7973;
         }

         int n = this.intValue << 12 | intValue;

         for (int i = 0; i < 18; i++) {
            boolean flag = (n >>> i & 1) != 0;
            int n2 = this.intValue2 - 11 + i % 3;
            int n3 = i / 3;
            this.method3021(n2, n3, flag);
            this.method3021(n3, n2, flag);
         }
      }
   }

   private void method3023(int n, int n2) {
      for (int i = -4; i <= 4; i++) {
         for (int j = -4; j <= 4; j++) {
            int n3 = Math.max(Math.abs(j), Math.abs(i));
            int n4 = n + j;
            int n5 = n2 + i;
            if (n4 >= 0 && n4 < this.intValue2 && n5 >= 0 && n5 < this.intValue2) {
               this.method3021(n4, n5, n3 != 2 && n3 != 4);
            }
         }
      }
   }

   private void method3024(int n, int n2) {
      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            this.method3021(n + j, n2 + i, Math.max(Math.abs(j), Math.abs(i)) != 1);
         }
      }
   }

   private void method3025(int n) {
      int n2 = n;

      for (int i = 0; i < 10; i++) {
         n2 = n2 << 1 ^ (n2 >>> 9) * 1335;
      }

      int n3 = (n << 10 | n2) ^ 21522;

      for (int i = 0; i <= 5; i++) {
         this.method3021(8, i, (n3 >>> i & 1) != 0);
      }

      this.method3021(8, 7, (n3 >>> 6 & 1) != 0);
      this.method3021(8, 8, (n3 >>> 7 & 1) != 0);
      this.method3021(7, 8, (n3 >>> 8 & 1) != 0);

      for (int i = 9; i < 15; i++) {
         this.method3021(14 - i, 8, (n3 >>> i & 1) != 0);
      }

      for (int i = 0; i < 8; i++) {
         this.method3021(this.intValue2 - 1 - i, 8, (n3 >>> i & 1) != 0);
      }

      for (int i = 8; i < 15; i++) {
         this.method3021(8, this.intValue2 - 15 + i, (n3 >>> i & 1) != 0);
      }

      this.method3021(8, this.intValue2 - 8, true);
   }

   private byte[] method3026(byte[] bytes) {
      int n = CONST_intValues3[this.intValue];
      int n2 = CONST_intValues2[this.intValue];
      int n3 = CONST_intValues[this.intValue];
      int n4 = n - n3 % n;
      int n5 = n3 / n;
      byte[] bytes2 = method3027(n2);
      byte[][] byteses = new byte[n][];
      int n6 = 0;

      for (int i = 0; n6 < n; n6++) {
         int n7 = n5 - n2 + (n6 < n4 ? 0 : 1);
         byte[] bytes3 = new byte[n7];
         System.arraycopy(bytes, i, bytes3, 0, n7);
         i += n7;
         byte[] bytes4 = method3028(bytes3, bytes2);
         byte[] bytes5 = new byte[n5 + 1];
         System.arraycopy(bytes3, 0, bytes5, 0, n7);
         System.arraycopy(bytes4, 0, bytes5, bytes5.length - n2, n2);
         byteses[n6] = bytes5;
      }

      byte[] bytes3 = new byte[n3];
      int n7 = 0;

      for (int i = 0; i < byteses[0].length; i++) {
         for (int j = 0; j < byteses.length; j++) {
            if (i != n5 - n2 || j >= n4) {
               bytes3[n7++] = byteses[j][i];
            }
         }
      }

      return bytes3;
   }

   private static byte[] method3027(int n) {
      byte[] bytes = new byte[n];
      bytes[n - 1] = 1;
      int n2 = 1;

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < bytes.length; j++) {
            bytes[j] = (byte)method3029(bytes[j] & 255, n2);
            if (j + 1 < bytes.length) {
               bytes[j] ^= bytes[j + 1];
            }
         }

         n2 = method3029(n2, 2);
      }

      return bytes;
   }

   private static byte[] method3028(byte[] bytes, byte[] bytes2) {
      byte[] bytes3 = new byte[bytes2.length];

      for (int n : bytes) {
         int n2 = (n ^ bytes3[0]) & 0xFF;
         System.arraycopy(bytes3, 1, bytes3, 0, bytes3.length - 1);
         bytes3[bytes3.length - 1] = 0;

         for (int i = 0; i < bytes3.length; i++) {
            bytes3[i] ^= (byte)method3029(bytes2[i] & 255, n2);
         }
      }

      return bytes3;
   }

   private static int method3029(int n, int n2) {
      int n3 = 0;

      for (int i = 7; i >= 0; i--) {
         n3 = n3 << 1 ^ (n3 >>> 7) * 285;
         n3 ^= (n2 >>> i & 1) * n;
      }

      return n3 & 0xFF;
   }

   private void method3030(byte[] bytes) {
      int n = 0;

      for (int i = this.intValue2 - 1; i >= 1; i -= 2) {
         if (i == 6) {
            i = 5;
         }

         for (int j = 0; j < this.intValue2; j++) {
            for (int k = 0; k < 2; k++) {
               int n2 = i - k;
               boolean flag = (i + 1 & 2) == 0;
               int n3 = flag ? this.intValue2 - 1 - j : j;
               if (!this.flags2[n3][n2] && n < bytes.length * 8) {
                  this.flags[n3][n2] = (bytes[n >>> 3] >>> 7 - (n & 7) & 1) != 0;
                  n++;
               }
            }
         }
      }
   }

   private void method3031(int n) {
      for (int i = 0; i < this.intValue2; i++) {
         for (int j = 0; j < this.intValue2; j++) {
            if (!this.flags2[i][j]) {
               boolean flag = switch (n) {
                  case 0 -> (j + i) % 2 == 0;
                  case 1 -> i % 2 == 0;
                  case 2 -> j % 3 == 0;
                  case 3 -> (j + i) % 3 == 0;
                  case 4 -> (j / 3 + i / 2) % 2 == 0;
                  case 5 -> j * i % 2 + j * i % 3 == 0;
                  case 6 -> (j * i % 2 + j * i % 3) % 2 == 0;
                  default -> ((j + i) % 2 + j * i % 3) % 2 == 0;
               };
               this.flags[i][j] = this.flags[i][j] ^ flag;
            }
         }
      }
   }

   private long method3032() {
      long l = 0L;

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < this.intValue2; j++) {
            int n = 1;

            for (int k = 1; k < this.intValue2; k++) {
               boolean flag = i == 0 ? this.flags[j][k] : this.flags[k][j];
               boolean flag2 = i == 0 ? this.flags[j][k - 1] : this.flags[k - 1][j];
               if (flag == flag2) {
                  if (++n == 5) {
                     l += 3L;
                  } else if (n > 5) {
                     l++;
                  }
               } else {
                  n = 1;
               }
            }
         }
      }

      int n = 0;

      for (int i = 0; i < this.intValue2; i++) {
         for (int j = 0; j < this.intValue2; j++) {
            if (this.flags[i][j]) {
               n++;
            }

            if (j + 1 < this.intValue2 && i + 1 < this.intValue2) {
               boolean flag = this.flags[i][j];
               if (flag == this.flags[i][j + 1] && flag == this.flags[i + 1][j] && flag == this.flags[i + 1][j + 1]) {
                  l += 3L;
               }
            }
         }
      }

      int n2 = this.intValue2 * this.intValue2;
      int n3 = (Math.abs(n * 20 - n2 * 10) + n2 - 1) / n2 - 1;
      return l + Math.max(0, n3) * 10L;
   }

   private static final class Entry {
      public final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      public int current;
      public int length;

      public void method3033(int length, int n) {
         for (int i = n - 1; i >= 0; i--) {
            this.current = this.current << 1 | length >>> i & 1;
            this.length++;
            if (this.length % 8 == 0) {
               this.byteArrayOutputStream.write(this.current);
               this.current = 0;
            }
         }
      }

      public byte[] method3034() {
         return this.byteArrayOutputStream.toByteArray();
      }
   }
}

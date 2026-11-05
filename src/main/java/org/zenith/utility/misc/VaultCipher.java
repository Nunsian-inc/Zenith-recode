package org.zenith.utility.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class VaultCipher {
   private static final byte[] CONST_byteValues = new byte[]{90, 86, 76, 84};
   private static final byte CONST_byteValue = 1;
   private static final int CONST_intValue = 16;
   private static final SecureRandom secureRandom = new SecureRandom();

   private VaultCipher() {
   }

   public static byte[] method1852(byte[] bytes, byte[] original, String name) {
      if (original.length < 64) {
         throw new IllegalArgumentException("key must be >= 64 bytes");
      } else {
         try {
            byte[] bytes2 = new byte[16];
            secureRandom.nextBytes(bytes2);
            byte[] src = AesGcmCrypto.method5339((byte[])bytes.clone(), method1854(original, name));
            byte[] input = method1859(original, 8, 40);
            method1855(src, input, bytes2);
            long x = method1857(method1859(original, 0, 8), bytes2);
            method1856(src, x, false);
            int n = 1 + Math.floorMod(x, 48);
            byte[] bytes3 = new byte[n];
            secureRandom.nextBytes(bytes3);
            ByteBuffer order = ByteBuffer.allocate(CONST_byteValues.length + 1 + 16 + 2 + 4 + n + src.length).order(ByteOrder.BIG_ENDIAN);
            order.put(CONST_byteValues);
            order.put((byte)1);
            order.put(bytes2);
            order.putShort((short)n);
            order.putInt(src.length);
            order.put(bytes3);
            order.put(src);
            Arrays.fill(input, (byte)0);
            return order.array();
         } catch (RuntimeException e) {
            throw e;
         } catch (Exception e) {
            throw new RuntimeException("vault seal failed", e);
         }
      }
   }

   public static byte[] method1853(byte[] array, byte[] original, String name) {
      if (original.length < 64) {
         throw new IllegalArgumentException("key must be >= 64 bytes");
      } else {
         try {
            ByteBuffer order = ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN);
            byte[] dst = new byte[CONST_byteValues.length];
            order.get(dst);
            if (!Arrays.equals(dst, CONST_byteValues)) {
               throw new IllegalArgumentException("bad magic");
            } else if (order.get() != 1) {
               throw new IllegalArgumentException("bad version");
            } else {
               byte[] bytes = new byte[16];
               order.get(bytes);
               int unsignedInt = Short.toUnsignedInt(order.getShort());
               int n = order.getInt();
               order.position(order.position() + unsignedInt);
               byte[] src = new byte[n];
               order.get(src);
               long x = method1857(method1859(original, 0, 8), bytes);
               method1856(src, x, true);
               byte[] input = method1859(original, 8, 40);
               method1855(src, input, bytes);
               Arrays.fill(input, (byte)0);
               return AesGcmCrypto.method5340(src, method1854(original, name));
            }
         } catch (RuntimeException e) {
            throw e;
         } catch (Exception e) {
            throw new RuntimeException("vault open failed (wrong key/HWID/session or tampered)", e);
         }
      }
   }

   private static String method1854(byte[] original, String name) {
      byte[] src = method1861(method1860(method1859(original, 40, 24), name.getBytes(StandardCharsets.UTF_8)));
      return Base64.getEncoder().encodeToString(src);
   }

   private static void method1855(byte[] src, byte[] input, byte[] bytes) {
      try {
         MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
         int n = 0;

         for (int i = 0; n < src.length; i++) {
            messageDigest.reset();
            messageDigest.update(input);
            messageDigest.update(bytes);
            messageDigest.update((byte)i);
            messageDigest.update((byte)(i >> 8));
            messageDigest.update((byte)(i >> 16));
            byte[] digest = messageDigest.digest();
            int n2 = Math.min(digest.length, src.length - n);

            for (int j = 0; j < n2; j++) {
               src[n + j] = (byte)(src[n + j] ^ digest[j]);
            }

            n += n2;
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private static void method1856(byte[] src, long x, boolean flag) {
      int length = src.length;
      if (length >= 2) {
         int[] ints = new int[length];
         long l = x == 0L ? -7046029254386353131L : x;

         for (int i = length - 1; i > 0; i--) {
            long l2 = l ^ l << 13;
            long l3 = l2 ^ l2 >>> 7;
            l = l3 ^ l3 << 17;
            int floorMod = Math.floorMod(l, i + 1);
            ints[i] = floorMod;
         }

         if (!flag) {
            for (int i = length - 1; i > 0; i--) {
               method1858(src, i, ints[i]);
            }
         } else {
            for (int i = 1; i < length; i++) {
               method1858(src, i, ints[i]);
            }
         }
      }
   }

   private static long method1857(byte[] original, byte[] bytes) {
      byte[] bytes2 = method1861(method1860(original, bytes));
      long l = 0L;

      for (int i = 0; i < 8; i++) {
         l = l << 8 | bytes2[i] & 255L;
      }

      return l;
   }

   private static void method1858(byte[] src, int n, int n2) {
      byte b = src[n];
      src[n] = src[n2];
      src[n2] = b;
   }

   private static byte[] method1859(byte[] original, int from, int n) {
      return Arrays.copyOfRange(original, from, from + n);
   }

   private static byte[] method1860(byte[] original, byte[] bytes) {
      byte[] bytes2 = Arrays.copyOf(original, original.length + bytes.length);
      System.arraycopy(bytes, 0, bytes2, original.length, bytes.length);
      return bytes2;
   }

   private static byte[] method1861(byte[] input) {
      try {
         return MessageDigest.getInstance("SHA-512").digest(input);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}

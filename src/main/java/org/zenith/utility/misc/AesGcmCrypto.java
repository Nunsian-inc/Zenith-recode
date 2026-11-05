package org.zenith.utility.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesGcmCrypto {
   private static final int CONST_intValue = 256;
   private static final int CONST_intValue2 = 32;
   private static final int CONST_intValue3 = 128;
   private static final int CONST_intValue4 = 12;
   private static final SecureRandom secureRandom = new SecureRandom();
   private static final byte CONST_byteValue = 1;
   private static final int CONST_intValue5 = 64;
   private static final ConcurrentHashMap<String, byte[]> concurrentHashMap = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<String, byte[]> concurrentHashMap2 = new ConcurrentHashMap<>();
   private static final int CONST_intValue6 = 5000;
   private static final int CONST_intValue7 = 12;

   public static String method5331(String str, String key) throws Exception {
      if (str == null) {
         throw new IllegalArgumentException("plainText == null");
      } else if (key == null) {
         throw new IllegalArgumentException("password == null");
      } else {
         byte[] src = method5334(key);
         int iterationCount = method5333(key);
         byte[] original = method5336(key, src, iterationCount);
         byte[] bytes = Arrays.copyOf(original, 32);
         byte[] copyOfRange = Arrays.copyOfRange(original, 32, original.length);
         SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
         byte[] src2 = new byte[12];
         secureRandom.nextBytes(src2);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, src2);
         cipher.init(1, secretKeySpec, gcmParameterSpec);
         byte[] doFinal = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
         byte[] src3 = method5338(copyOfRange, src2, doFinal);
         ByteBuffer allocate = ByteBuffer.allocate(2 + src.length + 1 + src2.length + 4 + src3.length);
         allocate.order(ByteOrder.BIG_ENDIAN);
         allocate.put((byte)1);
         allocate.put((byte)src.length);
         allocate.put(src);
         allocate.put((byte)src2.length);
         allocate.put(src2);
         allocate.putInt(iterationCount);
         allocate.put(src3);
         byte[] array = allocate.array();
         return Base64.getEncoder().encodeToString(array);
      }
   }

   public static String method5332(String line, String key) throws Exception {
      if (line == null) {
         throw new IllegalArgumentException("base64Blob == null");
      } else if (key == null) {
         throw new IllegalArgumentException("password == null");
      } else {
         byte[] array = Base64.getDecoder().decode(line);
         ByteBuffer order = ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN);
         byte b = order.get();
         if (b != 1) {
            throw new IllegalArgumentException("Unsupported version: " + b);
         } else {
            int unsignedInt = Byte.toUnsignedInt(order.get());
            byte[] dst = new byte[unsignedInt];
            order.get(dst);
            int n = Byte.toUnsignedInt(order.get());
            byte[] bytes = new byte[n];
            order.get(bytes);
            int iterationCount = order.getInt();
            byte[] dst2 = new byte[order.remaining()];
            order.get(dst2);
            byte[] original = method5336(key, dst, iterationCount);
            byte[] key2 = Arrays.copyOf(original, 32);
            byte[] copyOfRange = Arrays.copyOfRange(original, 32, original.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key2, "AES");
            byte[] input = method5338(copyOfRange, bytes, dst2);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, bytes);
            cipher.init(2, secretKeySpec, gcmParameterSpec);
            byte[] doFinal = cipher.doFinal(input);
            method5335(key, dst);
            return new String(doFinal, StandardCharsets.UTF_8);
         }
      }
   }

   private static int method5333(String key) {
      int n = Math.max(1, key.length());
      int n2 = 0;
      int n3 = 0;

      for (int i = 0; i < key.length(); i++) {
         char charAt = key.charAt(i);
         n2 += charAt * (i + 1);
         n3 ^= charAt << i % 8;
      }

      long l = n2 * 31L ^ n3 & 255L ^ key.hashCode();
      int n4 = 20000 + (int)(Math.abs(l) % 100000L);
      return Math.max(10000, Math.min(200000, n4));
   }

   private static byte[] method5334(String key) {
      byte[] bytes = concurrentHashMap2.computeIfAbsent(key, var0x -> {
         byte[] var1x = new byte[16];
         secureRandom.nextBytes(var1x);
         return var1x;
      });
      return (byte[])bytes.clone();
   }

   private static void method5335(String key, byte[] dst) {
      if (dst.length == 16) {
         concurrentHashMap2.putIfAbsent(key, (byte[])dst.clone());
      }
   }

   private static byte[] method5336(String key, byte[] src, int iterationCount) {
      String str = key + "\u0000" + Base64.getEncoder().encodeToString(src) + "\u0000" + iterationCount;
      byte[] bytes = concurrentHashMap.get(str);
      if (bytes == null) {
         bytes = method5337(key.toCharArray(), src, iterationCount, 64);
         if (concurrentHashMap.size() >= 64) {
            concurrentHashMap.clear();
         }

         concurrentHashMap.put(str, bytes);
      }

      return (byte[])bytes.clone();
   }

   private static byte[] method5337(char[] charArray, byte[] src, int iterationCount, int n) {
      try {
         PBEKeySpec pbeKeySpec = new PBEKeySpec(charArray, src, iterationCount, n * 8);
         SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
         SecretKey secret = secretKeyFactory.generateSecret(pbeKeySpec);
         return secret.getEncoded();
      } catch (GeneralSecurityException e) {
         throw new RuntimeException("PBKDF2 failure", e);
      }
   }

   private static byte[] method5338(byte[] copyOfRange, byte[] input, byte[] doFinal) {
      try {
         MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
         byte[] bytes = new byte[doFinal.length];
         int n = 0;

         for (int i = 0; i < doFinal.length; n++) {
            messageDigest.reset();
            messageDigest.update(copyOfRange);
            messageDigest.update((byte)(n & 0xFF));
            messageDigest.update((byte)(n >> 8 & 0xFF));
            messageDigest.update(input);
            byte[] digest = messageDigest.digest();
            int n2 = Math.min(digest.length, doFinal.length - i);

            for (int j = 0; j < n2; j++) {
               bytes[i + j] = (byte)(doFinal[i + j] ^ digest[j]);
            }

            i += n2;
         }

         return bytes;
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }
   }

   public static byte[] method5339(byte[] bytes, String password) throws Exception {
      byte[] src = method5334(password);
      int iterationCount = method5333(password);
      byte[] original = method5336(password, src, iterationCount);
      byte[] key = Arrays.copyOf(original, 32);
      byte[] copyOfRange = Arrays.copyOfRange(original, 32, original.length);
      SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
      byte[] src2 = new byte[12];
      secureRandom.nextBytes(src2);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(1, secretKeySpec, new GCMParameterSpec(128, src2));
      byte[] doFinal = cipher.doFinal(bytes);
      byte[] src3 = method5338(copyOfRange, src2, doFinal);
      ByteBuffer order = ByteBuffer.allocate(2 + src.length + 1 + src2.length + 4 + src3.length).order(ByteOrder.BIG_ENDIAN);
      order.put((byte)1);
      order.put((byte)src.length);
      order.put(src);
      order.put((byte)src2.length);
      order.put(src2);
      order.putInt(iterationCount);
      order.put(src3);
      byte[] array = order.array();
      Arrays.fill(original, (byte)0);
      Arrays.fill(key, (byte)0);
      Arrays.fill(copyOfRange, (byte)0);
      Arrays.fill(bytes, (byte)0);
      return Base64.getEncoder().encodeToString(array).getBytes(StandardCharsets.UTF_8);
   }

   public static byte[] method5340(byte[] allBytes, String password) throws Exception {
      String str = new String(allBytes, StandardCharsets.UTF_8);
      byte[] array = Base64.getDecoder().decode(str);
      ByteBuffer order = ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN);
      byte b = order.get();
      if (b != 1) {
         throw new IllegalArgumentException("Unsupported version: " + b);
      } else {
         int unsignedInt = Byte.toUnsignedInt(order.get());
         byte[] dst = new byte[unsignedInt];
         order.get(dst);
         int n = Byte.toUnsignedInt(order.get());
         byte[] bytes = new byte[n];
         order.get(bytes);
         int iterationCount = order.getInt();
         byte[] dst2 = new byte[order.remaining()];
         order.get(dst2);
         byte[] original = method5336(password, dst, iterationCount);
         byte[] key = Arrays.copyOf(original, 32);
         byte[] copyOfRange = Arrays.copyOfRange(original, 32, original.length);
         SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
         byte[] input = method5338(copyOfRange, bytes, dst2);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         cipher.init(2, secretKeySpec, new GCMParameterSpec(128, bytes));
         byte[] doFinal = cipher.doFinal(input);
         method5335(password, dst);
         Arrays.fill(original, (byte)0);
         Arrays.fill(key, (byte)0);
         Arrays.fill(copyOfRange, (byte)0);
         return doFinal;
      }
   }

   public static byte[] method5341(byte[] input, String str) throws Exception {
      byte[] src = new byte[12];
      secureRandom.nextBytes(src);
      byte[] key = method5337(str.toCharArray(), src, 5000, 32);
      SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
      byte[] bytes = new byte[12];
      secureRandom.nextBytes(bytes);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(1, secretKeySpec, new GCMParameterSpec(128, bytes));
      byte[] doFinal = cipher.doFinal(input);
      ByteBuffer allocate = ByteBuffer.allocate(src.length + bytes.length + doFinal.length);
      allocate.put(src);
      allocate.put(bytes);
      allocate.put(doFinal);
      Arrays.fill(key, (byte)0);
      return allocate.array();
   }

   public static byte[] method5342(byte[] array, String str) throws Exception {
      ByteBuffer byteBuffer = ByteBuffer.wrap(array);
      byte[] dst = new byte[12];
      byteBuffer.get(dst);
      byte[] bytes = new byte[12];
      byteBuffer.get(bytes);
      byte[] dst2 = new byte[byteBuffer.remaining()];
      byteBuffer.get(dst2);
      byte[] key = method5337(str.toCharArray(), dst, 5000, 32);
      SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(2, secretKeySpec, new GCMParameterSpec(128, bytes));
      byte[] doFinal = cipher.doFinal(dst2);
      Arrays.fill(key, (byte)0);
      return doFinal;
   }
}

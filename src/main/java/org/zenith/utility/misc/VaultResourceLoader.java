package org.zenith.utility.misc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class VaultResourceLoader {
   private VaultResourceLoader() {
   }

   public static InputStream method2218(String str) {
      return method2219("models/" + str, VaultKeyResolver.Type.ENVELOPE);
   }

   public static InputStream method2219(String name, VaultKeyResolver.Type type) {
      byte[] array = method2221(name + ".vault");
      if (array == null) {
         InputStream resourceAsStream = method2222().getResourceAsStream(name);
         if (resourceAsStream != null) {
            return resourceAsStream;
         } else {
            throw new IllegalStateException("vault resource not found: " + name);
         }
      } else {
         byte[] original = VaultKeyResolver.method2860(type, name);

         ByteArrayInputStream byteArrayInputStream;
         try {
            byte[] buf = VaultCipher.method1853(array, original, name);
            byteArrayInputStream = new ByteArrayInputStream(buf);
         } finally {
            Arrays.fill(original, (byte)0);
         }

         return byteArrayInputStream;
      }
   }

   public static byte[] method2220(String name, VaultKeyResolver.Type type) {
      byte[] array = method2221(name + ".vault");
      if (array == null) {
         throw new IllegalStateException("vault resource not found: " + name + ".vault");
      } else {
         byte[] original = VaultKeyResolver.method2860(type, name);

         byte[] bytes;
         try {
            bytes = VaultCipher.method1853(array, original, name);
         } finally {
            Arrays.fill(original, (byte)0);
         }

         return bytes;
      }
   }

   private static byte[] method2221(String name) {
      try {
         byte[] allBytes;
         try (InputStream resourceAsStream = method2222().getResourceAsStream(name)) {
            allBytes = resourceAsStream == null ? null : resourceAsStream.readAllBytes();
         }

         return allBytes;
      } catch (IOException e) {
         throw new RuntimeException("read " + name, e);
      }
   }

   private static ClassLoader method2222() {
      ClassLoader classLoader = VaultResourceLoader.class.getClassLoader();
      return classLoader != null ? classLoader : ClassLoader.getSystemClassLoader();
   }
}

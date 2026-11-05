package org.zenith.utility.misc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public final class VaultKeyResolver {
   private static volatile VaultKeyResolver.Handler handler = str -> null;

   private VaultKeyResolver() {
   }

   public static void method2859(VaultKeyResolver.Handler handler2) {
      if (handler2 != null) {
         handler = handler2;
      }
   }

   public static byte[] method2860(VaultKeyResolver.Type type, String name) {
      byte[] original = switch (type) {
         case MACHINE -> method2862();
         case ENVELOPE -> {
            byte[] bytes = handler.resolve(name);
            if (bytes == null || bytes.length < 32) {
               throw new IllegalStateException("vault: no server DEK for " + name + " (login/server required)");
            }

            yield bytes;
         }
         default -> throw new IllegalStateException();
      };
      byte[] bytes = method2864(method2863(original, ("zenith/" + type + "/" + name).getBytes(StandardCharsets.UTF_8)));
      Arrays.fill(original, (byte)0);
      return bytes;
   }

   public static byte[] method2861(VaultKeyResolver.Type type, String str, byte[] original) {
      return method2864(method2863(original, ("zenith/" + type + "/" + str).getBytes(StandardCharsets.UTF_8)));
   }

   private static byte[] method2862() {
      return new byte[]{
         90, 126, 17, -61, 66, -99, -16, 104, -95, 59, -41, 12, -124, 85, 46, -69, 25, -26, 71, -112, 124, 42, -11, 97, 8, -51, 51, -78, 111, -104, 20, -34
      };
   }

   private static byte[] method2863(byte[] original, byte[] bytes) {
      byte[] bytes2 = Arrays.copyOf(original, original.length + bytes.length);
      System.arraycopy(bytes, 0, bytes2, original.length, bytes.length);
      return bytes2;
   }

   private static byte[] method2864(byte[] input) {
      try {
         return MessageDigest.getInstance("SHA-512").digest(input);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public interface Handler {
      byte[] resolve(String name);
   }

   public static enum Type {
      MACHINE,
      ENVELOPE;
   }
}

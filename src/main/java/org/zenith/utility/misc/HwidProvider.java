package org.zenith.utility.misc;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

public final class HwidProvider {
   private static volatile byte[] byteValues;

   private HwidProvider() {
   }

   public static byte[] method1369() {
      byte[] bytes = byteValues;
      if (bytes != null) {
         return (byte[])bytes.clone();
      } else {
         synchronized (HwidProvider.class) {
            if (byteValues == null) {
               byteValues = method1370();
            }

            return (byte[])byteValues.clone();
         }
      }
   }

   private static byte[] method1370() {
      StringBuilder builder = new StringBuilder(256);
      method1371(builder, "os", System.getProperty("os.name"));
      method1371(builder, "arch", System.getProperty("os.arch"));
      method1371(builder, "user", System.getProperty("user.name"));
      method1371(builder, "cpu", Integer.toString(Runtime.getRuntime().availableProcessors()));

      try {
         method1371(builder, "host", InetAddress.getLocalHost().getHostName());
      } catch (Exception e) {
         method1371(builder, "host", "?");
      }

      method1371(builder, "mguid", method1372());

      try {
         MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
         return messageDigest.digest(builder.toString().getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
         throw new RuntimeException("SHA-256 unavailable", e);
      }
   }

   private static void method1371(StringBuilder builder, String str, String property) {
      builder.append(str).append('=').append(property == null ? "" : property.toLowerCase(Locale.ROOT)).append('\n');
   }

   private static String method1372() {
      String property = System.getProperty("os.name", "");
      if (!property.toLowerCase(Locale.ROOT).contains("win")) {
         return "nonwin";
      } else {
         try {
            Process start = new ProcessBuilder("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid").redirectErrorStream(true).start();
            byte[] allBytes = start.getInputStream().readAllBytes();
            start.waitFor();
            String str = new String(allBytes, StandardCharsets.UTF_8);
            int indexOf = str.indexOf("REG_SZ");
            if (indexOf >= 0) {
               return str.substring(indexOf + 6).trim();
            }
         } catch (Exception e) {
         }

         return "noguid";
      }
   }
}

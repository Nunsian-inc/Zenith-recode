package org.zenith.utility.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class CosmeticFileLoader {
   private final Map<String, byte[]> items;
   private final String text;
   private final String text2;
   private final String text3;
   private final CosmeticFileLoader.Type type;

   private CosmeticFileLoader(Map<String, byte[]> items, String text, String str, String str2, CosmeticFileLoader.Type type) {
      this.items = items;
      this.text = text;
      this.text2 = str;
      this.text3 = str2;
      this.type = type;
   }

   public CosmeticFileLoader.Type getType() {
      return this.type;
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public byte[] method7338(String text) {
      return text == null ? null : this.items.get(text);
   }

   public String method7339(String text) {
      byte[] bytes = this.method7338(text);
      return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
   }

   public Map<String, byte[]> getItems() {
      return this.items;
   }

   public byte[] method7340(String key) {
      if (key != null && !key.isBlank()) {
         byte[] bytes = this.items.get(key);
         if (bytes != null) {
            return bytes;
         } else {
            String lowerCase = method7342(key).toLowerCase(Locale.ROOT);

            for (Entry entry : this.items.entrySet()) {
               if (method7342((String)entry.getKey()).toLowerCase(Locale.ROOT).equals(lowerCase)) {
                  return (byte[])entry.getValue();
               }
            }

            return this.method7341();
         }
      } else {
         return this.method7341();
      }
   }

   public byte[] method7341() {
      for (Entry entry : this.items.entrySet()) {
         if (method7343((String)entry.getKey())) {
            return (byte[])entry.getValue();
         }
      }

      return null;
   }

   private static String method7342(String key) {
      int n = Math.max(key.lastIndexOf(47), key.lastIndexOf(92));
      return n < 0 ? key : key.substring(n + 1);
   }

   private static boolean method7343(String key) {
      return key.toLowerCase(Locale.ROOT).endsWith(".png");
   }

   public static CosmeticFileLoader method7344(Path path) throws IOException {
      CosmeticFileLoader cosmeticFileLoader;
      try (InputStream inputStream = Files.newInputStream(path)) {
         cosmeticFileLoader = method7345(inputStream);
      }

      return cosmeticFileLoader;
   }

   public static CosmeticFileLoader method7345(InputStream byteArrayInputStream) throws IOException {
      LinkedHashMap map = new LinkedHashMap();

      ZipEntry nextEntry;
      try (ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream)) {
         while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            if (!nextEntry.isDirectory()) {
               map.put(nextEntry.getName(), method7347(zipInputStream));
            }
         }
      }

      String key = null;
      String str = null;
      String key2 = null;
      String key3 = null;
      String key4 = null;

      for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
         Entry entry = (Entry)obj;
         String key5 = (String)entry.getKey();
         String lowerCase = key5.toLowerCase(Locale.ROOT);
         if (lowerCase.endsWith(".obj")) {
            key = key5;
         } else if (lowerCase.endsWith(".mtl")) {
            key4 = key5;
         } else if (lowerCase.endsWith(".json")) {
            String str2 = method7346((byte[])entry.getValue());
            if (str2.contains("minecraft:geometry")) {
               str = key5;
            } else if (str2.contains("\"Techne\"")) {
               key2 = key5;
            } else if (str2.contains("\"animations\"")) {
               key3 = key5;
            }
         }
      }

      String text = key != null ? key : (str != null ? str : key2);
      CosmeticFileLoader.Type type = key != null
         ? CosmeticFileLoader.Type.WAVEFRONT
         : (str != null ? CosmeticFileLoader.Type.BEDROCK : (key2 != null ? CosmeticFileLoader.Type.TECHNE : CosmeticFileLoader.Type.EMPTY));
      return new CosmeticFileLoader(Collections.unmodifiableMap(map), text, key3, key4, type);
   }

   private static String method7346(byte[] bytes) {
      return new String(bytes, 0, Math.min(bytes.length, 512), StandardCharsets.UTF_8);
   }

   private static byte[] method7347(InputStream zipInputStream) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8192);
      byte[] bytes = new byte[8192];

      int len;
      while ((len = zipInputStream.read(bytes)) > 0) {
         byteArrayOutputStream.write(bytes, 0, len);
      }

      return byteArrayOutputStream.toByteArray();
   }

   public static enum Type {
      WAVEFRONT,
      BEDROCK,
      TECHNE,
      EMPTY;
   }
}

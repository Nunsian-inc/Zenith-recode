package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.zenith.Zenith;

public final class Index2 {
   private static final byte[] CONST_byteValues = new byte[]{90, 87, 66, 49};
   private static final long CONST_longValue = TimeUnit.SECONDS.toNanos(20L);
   private static final Duration duration = Duration.ofSeconds(30L);
   private final Path path;
   private final Path path2;
   private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(Redirect.NORMAL).build();
   private final Map<String, byte[]> items = new ConcurrentHashMap<>();
   private volatile Map<String, String> items2;
   private volatile long longValue;
   private volatile boolean flag;
   private volatile long longValue2;

   public Index2() {
      String property = System.getProperty("zenith.wardrobe.src");
      Path path = property != null && !property.isBlank() && !Boolean.getBoolean("zenith.wardrobe.remote") ? Path.of(property) : null;
      this.path = path != null && Files.isDirectory(path.resolve("models")) ? path : null;
      this.path2 = Zenith.file.toPath().resolve("wardrobe").resolve("cache");
   }

   public Index2.Data method6140(String text) throws Exception {
      if (this.path != null) {
         return new Index2.Data(
            Files.readAllBytes(this.path.resolve("models").resolve(text + ".json")), Files.readAllBytes(this.path.resolve("textures").resolve(text + ".png"))
         );
      } else {
         var map = this.method6141();
         String str = (String)map.get(text);
         if (str != null) {
            byte[] key = this.method6142(text, map);
            return method6144(text, key, this.method6143(str));
         } else if (this.flag) {
            synchronized (this) {
               if (this.items2 == map && System.nanoTime() >= this.longValue) {
                  this.items2 = null;
               }
            }

            throw new Index2.Entry("модели нет в кэшированном индексе: " + text);
         } else {
            throw new IOException("модели нет в индексе: " + text);
         }
      }
   }

   private Map<String, String> method6141() throws Exception {
      var map = this.items2;
      if (map != null) {
         return map;
      } else {
         synchronized (this) {
            if (this.items2 != null) {
               return this.items2;
            } else if (System.nanoTime() < this.longValue) {
               throw new Index2.Entry("индекс гардероба недоступен");
            } else {
               Path path = this.path2.resolve("index.json");
               boolean flag = false;

               byte[] allBytes;
               try {
                  allBytes = this.method6146(method6147() + "/wardrobe/index.json");
                  Files.createDirectories(this.path2);
                  Files.write(path, allBytes);
               } catch (Exception e) {
                  if (!Files.isRegularFile(path)) {
                     this.longValue = System.nanoTime() + CONST_longValue;
                     throw new Index2.Entry("индекс гардероба не скачан: " + e.getMessage());
                  }

                  allBytes = Files.readAllBytes(path);
                  flag = true;
               }

               JsonObject jsonObject = JsonParser.parseString(new String(allBytes, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("models");
               HashMap map2 = new HashMap();

               for (Map.Entry entry : jsonObject.entrySet()) {
                  map2.put((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsJsonObject().get("hash").getAsString());
               }

               this.flag = flag;
               if (flag) {
                  this.longValue = System.nanoTime() + CONST_longValue;
               }

               this.items2 = map2;
               return map2;
            }
         }
      }
   }

   private byte[] method6142(String text, Map<String, String> map) throws Exception {
      byte[] bytes = this.items.get(text);
      if (bytes != null) {
         return bytes;
      } else {
         synchronized (this.items) {
            bytes = this.items.get(text);
            if (bytes != null) {
               return bytes;
            } else if (System.nanoTime() < this.longValue2) {
               throw new Index2.Entry("ключи гардероба ещё не получены");
            } else {
               StoreApi storeApi = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();

               try {
                  if (storeApi == null) {
                     throw new IOException("облако не готово");
                  }

                  this.items.putAll(storeApi.method1982(new ArrayList<>(map.keySet())).get(duration.toSeconds(), TimeUnit.SECONDS));
               } catch (Exception e) {
                  this.longValue2 = System.nanoTime() + CONST_longValue;
                  throw new Index2.Entry("ключи гардероба не получены: " + e.getMessage());
               }

               bytes = this.items.get(text);
               if (bytes == null) {
                  throw new IOException("узел не выдал ключ модели " + text);
               } else {
                  return bytes;
               }
            }
         }
      }
   }

   private byte[] method6143(String str) throws Exception {
      Path path = this.path2.resolve(str + ".zwm");
      if (Files.isRegularFile(path)) {
         byte[] allBytes = Files.readAllBytes(path);
         if (str.equals(method6148(allBytes))) {
            return allBytes;
         }

         Files.deleteIfExists(path);
      }

      byte[] allBytes;
      try {
         allBytes = this.method6146(method6147() + "/wardrobe/blobs/" + str + ".zwm");
      } catch (Exception e) {
         throw new Index2.Entry("модель гардероба не скачана: " + e.getMessage());
      }

      if (!str.equals(method6148(allBytes))) {
         throw new IOException("хеш модели гардероба не совпал: " + str);
      } else {
         Files.createDirectories(this.path2);
         Path path2 = this.path2.resolve(str + ".part");
         Files.write(path2, allBytes);
         Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
         return allBytes;
      }
   }

   private static Index2.Data method6144(String text, byte[] key, byte[] src) throws Exception {
      if (src.length >= 32
         && src[0] == CONST_byteValues[0]
         && src[1] == CONST_byteValues[1]
         && src[2] == CONST_byteValues[2]
         && src[3] == CONST_byteValues[3]) {
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         cipher.init(2, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, src, 4, 12));
         cipher.updateAAD(text.getBytes(StandardCharsets.UTF_8));
         byte[] doFinal = cipher.doFinal(src, 16, src.length - 16);
         ByteBuffer byteBuffer = ByteBuffer.wrap(method6145(doFinal));
         byte[] dst = new byte[byteBuffer.getInt()];
         byteBuffer.get(dst);
         byte[] bytes = new byte[byteBuffer.getInt()];
         byteBuffer.get(bytes);
         return new Index2.Data(dst, bytes);
      } else {
         throw new IOException("неизвестный формат модели гардероба");
      }
   }

   private static byte[] method6145(byte[] doFinal) throws Exception {
      Inflater inflater = new Inflater();

      byte[] byteArray;
      try {
         inflater.setInput(doFinal);
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(doFinal.length * 3);
         byte[] output = new byte[16384];

         while (!inflater.finished()) {
            int inflate = inflater.inflate(output);
            if (inflate == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
               throw new IOException("модель гардероба повреждена");
            }

            byteArrayOutputStream.write(output, 0, inflate);
         }

         byteArray = byteArrayOutputStream.toByteArray();
      } finally {
         inflater.end();
      }

      return byteArray;
   }

   private byte[] method6146(String str) throws Exception {
      HttpResponse send = this.httpClient.send(HttpRequest.newBuilder(URI.create(str)).timeout(duration).GET().build(), BodyHandlers.ofByteArray());
      if (send.statusCode() != 200) {
         throw new IOException("узел ответил " + send.statusCode());
      } else {
         return (byte[])send.body();
      }
   }

   private static String method6147() throws IOException {
      StoreApi storeApi = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
      if (storeApi == null) {
         throw new IOException("облако не готово");
      } else {
         return storeApi.method1983();
      }
   }

   private static String method6148(byte[] allBytes) throws Exception {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(allBytes));
   }

   public static final class Data {
      private final byte[] byteValues;
      private final byte[] byteValues2;

      public byte[] byteValues() {
         return this.byteValues;
      }

      public byte[] byteValues2() {
         return this.byteValues2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.byteValues, other.byteValues) && java.util.Objects.equals(this.byteValues2, other.byteValues2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(byteValues, byteValues2);
      }

      @Override
      public String toString() {
         return "Data[" + "byteValues=" + this.byteValues + ", " + "byteValues2=" + this.byteValues2 + "]";
      }

      public Data(byte[] byteValues, byte[] byteValues2) {
         this.byteValues = byteValues;
         this.byteValues2 = byteValues2;
      }

      public byte[] getByteValues() {
         return this.byteValues;
      }

      public byte[] getByteValues2() {
         return this.byteValues2;
      }
   }

   public static final class Entry extends Exception {
      public Entry(String message) {
         super(message, null, false, false);
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Index {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final String text = "index.json";
   private static final String text2 = "/cosmetics/index.json";
   private static final String text3 = "/cosmetics/blobs/";
   private static final String text4 = ".model";
   private static final String text5 = "/cosmetics/overrides.json";
   private static final String text6 = "/cosmetics/overrides";
   private static final Duration duration = Duration.ofSeconds(10L);
   private static final Duration duration2 = Duration.ofSeconds(30L);
   private static final Duration duration3 = Duration.ofSeconds(60L);
   private final Path path;
   private final Path path2;
   private final Supplier<List<String>> supplier;
   private volatile String text7;
   private volatile HttpClient httpClient;
   private volatile String lastError = "";
   private volatile boolean flag;
   private static final int CONST_intValue = 3;
   private static final long CONST_longValue = 1500L;

   public Index(Path path, Supplier<List<String>> supplier) {
      this.path = path;
      this.supplier = supplier;
      this.path2 = path.getParent() == null ? null : path.getParent().resolve("pack");
   }

   private Path method1452() {
      if (this.path2 == null) {
         return null;
      } else {
         Path path = this.path2.resolve("index.json");
         return Files.isRegularFile(path) ? path : null;
      }
   }

   public List<CosmeticItemInfo> method1453() {
      Path path = this.method1452();
      if (path != null) {
         var cosmeticItemInfos = method1471(method1469(path));
         if (!cosmeticItemInfos.isEmpty()) {
            this.lastError = "";
            logger.info("Косметика: каталог из локального пака {} ({} элементов)", path, cosmeticItemInfos.size());
            return cosmeticItemInfos;
         }

         logger.warn("Косметика: локальный каталог {} пуст или битый — идём на узел", path);
      }

      Path path2 = this.path.resolve("index.json");
      boolean regularFile = Files.isRegularFile(path2);
      if (regularFile && !this.flag) {
         var cosmeticItemInfos = method1471(method1469(path2));
         if (!cosmeticItemInfos.isEmpty()) {
            return cosmeticItemInfos;
         }

         logger.warn("Косметика: кеш каталога пуст или битый — перечитываем с узла");
      }

      byte[] bytes = this.method1468("/cosmetics/index.json", duration2);
      var cosmeticItemInfos = method1471(bytes);
      if (!cosmeticItemInfos.isEmpty()) {
         this.flag = false;
         method1470(path2, bytes);
         return cosmeticItemInfos;
      } else {
         if (regularFile) {
            var cosmeticItemInfos2 = method1471(method1469(path2));
            if (!cosmeticItemInfos2.isEmpty()) {
               logger.info("Косметика: узел недоступен, каталог из кеша ({} элементов)", cosmeticItemInfos2.size());
               return cosmeticItemInfos2;
            }
         }

         logger.warn("Косметика: каталог недоступен — ни сети, ни кеша");
         return List.of();
      }
   }

   public void method1454() {
      this.flag = true;
   }

   public byte[] method1455() {
      try {
         return this.method1465("/cosmetics/overrides.json", duration2);
      } catch (IOException e) {
         logger.debug("Косметика: общей подгонки на узле нет ({})", e.getMessage());
         return null;
      }
   }

   public String method1456(byte[] bytes, String str) {
      String str2 = this.method1463("/cosmetics/overrides");

      for (int i = 1; i <= 3; i++) {
         try {
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(str2))
               .timeout(duration2)
               .header("User-Agent", "ZenithDLC")
               .header("Content-Type", "application/json; charset=utf-8")
               .header("X-Zenith-Secret", str == null ? "" : str)
               .POST(BodyPublishers.ofByteArray(bytes))
               .build();
            HttpResponse send = this.method1466().send(httpRequest, BodyHandlers.ofString());
            if (send.statusCode() == 200) {
               this.lastError = "";
               return (String)send.body();
            }

            this.lastError = str2 + " — узел ответил " + send.statusCode();
            logger.warn("Косметика: публикация подгонки — {} {}", send.statusCode(), send.body());
            return null;
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.lastError = "публикация прервана";
            return null;
         } catch (RuntimeException | IOException e) {
            this.lastError = str2 + " — " + e.getMessage();
            if (i >= 3) {
               logger.warn("Косметика: публикация подгонки не дошла после {} попыток: {}", i, e.toString());
               return null;
            }

            logger.warn("Косметика: публикация подгонки — попытка {} не дошла: {}", i, e.toString());
            method1457(1500L * i);
         }
      }

      return null;
   }

   private static void method1457(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   public byte[] method1458(CosmeticItemInfo cosmeticItemInfo) throws IOException {
      if (this.path2 != null) {
         Path path = this.path2.resolve("blobs").resolve(cosmeticItemInfo.getText6() + ".model");
         if (Files.isRegularFile(path)) {
            byte[] allBytes = Files.readAllBytes(path);
            if (method1472(allBytes, cosmeticItemInfo.getText6())) {
               return allBytes;
            }

            logger.warn("Косметика: {} в локальном паке не совпал с хешем — качаем с узла", cosmeticItemInfo.getText6());
         }
      }

      Path path = this.method1462(cosmeticItemInfo);
      if (Files.isRegularFile(path)) {
         byte[] allBytes = Files.readAllBytes(path);
         if (method1472(allBytes, cosmeticItemInfo.getText6())) {
            return allBytes;
         }

         logger.warn("Косметика: {} в кеше битый — перекачиваем", cosmeticItemInfo.getText6());
         Files.deleteIfExists(path);
      }

      byte[] allBytes = this.method1465("/cosmetics/blobs/" + cosmeticItemInfo.getText6() + ".model", duration3);
      if (!method1472(allBytes, cosmeticItemInfo.getText6())) {
         throw new IOException("хеш скачанной модели не совпал: " + cosmeticItemInfo.getText6());
      } else {
         method1470(path, allBytes);
         return allBytes;
      }
   }

   public boolean method1459(CosmeticItemInfo cosmeticItemInfo) {
      return Files.isRegularFile(this.method1462(cosmeticItemInfo));
   }

   public long method1460() {
      long l = 0L;

      try (Stream<java.nio.file.Path> list = Files.list(this.path)) {
         for (Path path : list.toList()) {
            l += Files.size(path);
         }
      } catch (RuntimeException | IOException e) {
      }

      return l;
   }

   public long method1461() {
      long l = 0L;

      try (Stream<java.nio.file.Path> list = Files.list(this.path)) {
         for (Path path : list.toList()) {
            long size = Files.size(path);
            if (Files.deleteIfExists(path)) {
               l += size;
            }
         }
      } catch (RuntimeException | IOException e) {
         logger.warn("Косметика: кеш стёрся не полностью: {}", e.toString());
      }

      this.flag = true;
      return l;
   }

   public Path getPath() {
      return this.path;
   }

   private Path method1462(CosmeticItemInfo cosmeticItemInfo) {
      return this.path.resolve(cosmeticItemInfo.getText6() + ".model");
   }

   private String method1463(String str) {
      var strings = this.method1464();
      return strings.isEmpty() ? str : (String)strings.get(0) + str;
   }

   private List<String> method1464() {
      List<String> strings = this.supplier.get();
      ArrayList arrayList = new ArrayList();
      if (strings != null) {
         for (String str : strings) {
            if (str != null && !str.isBlank()) {
               String str2 = str.strip();

               while (str2.endsWith("/")) {
                  str2 = str2.substring(0, str2.length() - 1);
               }

               if (!arrayList.contains(str2)) {
                  arrayList.add(str2);
               }
            }
         }
      }

      return RegionUtil.method2043(arrayList, this.text7);
   }

   private byte[] method1465(String str, Duration duration2) throws IOException {
      List<String> strings = this.method1464();
      if (strings.isEmpty()) {
         return this.method1467(str, duration2);
      } else {
         IOException ioException = null;

         for (String str2 : strings) {
            try {
               byte[] bytes = this.method1467(str2 + str, duration2);
               this.text7 = str2;
               return bytes;
            } catch (IOException e) {
               if (Thread.currentThread().isInterrupted()) {
                  throw e;
               }

               ioException = e;
            }
         }

         throw ioException;
      }
   }

   private HttpClient method1466() {
      HttpClient httpClient = this.httpClient;
      if (httpClient == null) {
         httpClient = HttpClients.method2671(duration, Redirect.NORMAL);
         this.httpClient = httpClient;
      }

      return httpClient;
   }

   private byte[] method1467(String str, Duration duration2) throws IOException {
      HttpRequest httpRequest;
      try {
         httpRequest = HttpRequest.newBuilder(URI.create(str)).timeout(duration2).header("User-Agent", "ZenithDLC").GET().build();
      } catch (RuntimeException e) {
         throw new IOException("неверный адрес " + str, e);
      }

      try {
         HttpResponse send = this.method1466().send(httpRequest, BodyHandlers.ofByteArray());
         if (send.statusCode() != 200) {
            throw new IOException("узел ответил " + send.statusCode() + " на " + str);
         } else {
            return (byte[])send.body();
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new IOException("загрузка прервана", e);
      }
   }

   private byte[] method1468(String str, Duration duration2) {
      try {
         byte[] bytes = this.method1465(str, duration2);
         this.lastError = "";
         return bytes;
      } catch (IOException e) {
         String str2 = this.method1463(str);
         this.lastError = str2 + " — " + e.getMessage();
         logger.info("Косметика: {} недоступен ({})", str2, e.getMessage());
         return null;
      }
   }

   public String getLastError() {
      return this.lastError;
   }

   private static byte[] method1469(Path path) {
      try {
         return Files.readAllBytes(path);
      } catch (IOException e) {
         logger.warn("Косметика: не читается {}", path, e);
         return null;
      }
   }

   private static void method1470(Path path, byte[] bytes) {
      if (bytes != null) {
         try {
            Files.createDirectories(path.getParent());
            Path sibling = path.resolveSibling(path.getFileName() + ".tmp");
            Files.write(sibling, bytes);
            Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING);
         } catch (IOException e) {
            logger.warn("Косметика: не записать кеш {}", path, e);
         }
      }
   }

   private static List<CosmeticItemInfo> method1471(byte[] bytes) {
      if (bytes != null && bytes.length != 0) {
         try {
            JsonObject jsonObject = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
            if (jsonObject.has("items") && jsonObject.get("items").isJsonArray()) {
               JsonArray jsonArray = jsonObject.getAsJsonArray("items");
               List<CosmeticItemInfo> cosmeticItemInfos = new ArrayList<>(jsonArray.size());

               for (JsonElement json : jsonArray) {
                  if (json.isJsonObject()) {
                     CosmeticItemInfo cosmeticItemInfo = CosmeticItemInfo.method7748(json.getAsJsonObject());
                     if (cosmeticItemInfo != null) {
                        cosmeticItemInfos.add(cosmeticItemInfo);
                     }
                  }
               }

               return cosmeticItemInfos;
            } else {
               return List.of();
            }
         } catch (RuntimeException e) {
            logger.warn("Косметика: каталог не разобрался: {}", e.toString());
            return List.of();
         }
      } else {
         return List.of();
      }
   }

   private static boolean method1472(byte[] allBytes, String anotherString) {
      return anotherString != null && !anotherString.isBlank() ? method1473(allBytes).equalsIgnoreCase(anotherString) : true;
   }

   private static String method1473(byte[] allBytes) {
      try {
         byte[] digest = MessageDigest.getInstance("SHA-256").digest(allBytes);
         StringBuilder builder = new StringBuilder(digest.length * 2);

         for (int n : digest) {
            builder.append(Character.forDigit(n >> 4 & 15, 16));
            builder.append(Character.forDigit(n & 15, 16));
         }

         return builder.toString();
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("SHA-256 недоступен", e);
      }
   }
}

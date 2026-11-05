package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import net.minecraft.util.Util;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.misc.ImageLoader;

public final class OriginUtil {
   public static final String text = ".zjs";
   private static final String text2 = ".origin.json";
   private static final long CONST_longValue = 8388608L;
   private static final long CONST_longValue2 = 8388608L;
   private static final int CONST_intValue = 256;
   private static final int CONST_intValue2 = 128;
   private static final Map<String, OriginUtil.Data3> items = new ConcurrentHashMap<>();
   private static final Map<Path, OriginUtil.Data> items2 = new ConcurrentHashMap<>();
   private static final long CONST_longValue3 = 3000L;
   private static final String text3 = ".package.json";

   private OriginUtil() {
   }

   public static String method6914(Script script) {
      StringBuilder builder = new StringBuilder("zjs");
      method6915(builder, "v", script.getVersion(), 24);
      method6915(builder, "a", script.getText2(), 40);
      method6915(builder, "c", script.getColor() == -1 ? "" : Integer.toHexString(script.getColor()), 8);
      method6915(builder, "t", script.getText3(), 80);
      method6915(builder, "l", script.getText4(), 80);
      String string = builder.toString();
      return string.length() > 250 ? string.substring(0, 250) : string;
   }

   private static void method6915(StringBuilder builder, String str, String version, int endIndex) {
      if (version != null && !version.isBlank()) {
         String str2 = version.strip();
         if (str2.length() > endIndex) {
            str2 = str2.substring(0, endIndex);
         }

         builder.append('|').append(str).append('=').append(URLEncoder.encode(str2, StandardCharsets.UTF_8));
      }
   }

   public static OriginUtil.Data3 method6916(StoreApi.Data2 entry) {
      String server = entry == null ? null : entry.server();
      if (server != null && server.startsWith("zjs")) {
         OriginUtil.Data3 data3 = items.get(server);
         if (data3 != null) {
            return data3;
         } else {
            OriginUtil.Data3 data32 = method6917(server);
            if (items.size() > 512) {
               items.clear();
            }

            items.put(server, data32);
            return data32;
         }
      } else {
         return OriginUtil.Data3.INSTANCE;
      }
   }

   private static OriginUtil.Data3 method6917(String server) {
      LinkedHashMap<String, String> map = new LinkedHashMap<>();

      for (String str : server.split("\\|")) {
         int indexOf = str.indexOf(61);
         if (indexOf > 0) {
            try {
               map.put(str.substring(0, indexOf), URLDecoder.decode(str.substring(indexOf + 1), StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
            }
         }
      }

      ArrayList coll = new ArrayList();

      for (String str : map.getOrDefault("t", "").split(",")) {
         if (!str.isBlank()) {
            coll.add(str.strip());
         }
      }

      int intValue = -1;

      try {
         if (map.containsKey("c")) {
            intValue = (int)Long.parseLong((String)map.get("c"), 16);
         }
      } catch (NumberFormatException e) {
      }

      return new OriginUtil.Data3(((String)map.getOrDefault("v", "")), List.copyOf(coll), intValue, map.getOrDefault("a", ""), map.getOrDefault("l", ""));
   }

   public static boolean method6918(StoreApi.Data2 entry) {
      return entry != null && "script".equalsIgnoreCase(entry.kind()) && entry.fileName().toLowerCase(Locale.ROOT).endsWith(".zjs");
   }

   public static byte[] method6919(Script script) throws IOException {
      ArrayList<Path> paths = new ArrayList();
      Path path;
      if (script.getPath() == null) {
         path = null;
         paths.add(script.getPath2());
      } else {
         path = script.getPath();

         try (Stream<java.nio.file.Path> walk = Files.walk(path, 6)) {
            walk.filter(var0x -> Files.isRegularFile(var0x)).filter(var0x -> {
               String var1x = var0x.getFileName().toString();
               return !var1x.startsWith(".") && !var1x.equals("storage.json") && !var1x.endsWith(".tmp");
            }).sorted().forEach(paths::add);
         }
      }

      if (paths.size() > 256) {
         throw new IOException("слишком много файлов: " + paths.size());
      } else {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

         try {
            for (Path source : paths) {
               String name = path == null ? "main.js" : path.relativize(source).toString().replace('\\', '/');
               zipOutputStream.putNextEntry(new ZipEntry(name));
               Files.copy(source, zipOutputStream);
               zipOutputStream.closeEntry();
            }
         } catch (Throwable e) {
            try {
               zipOutputStream.close();
            } catch (Throwable ex) {
               e.addSuppressed(ex);
            }

            throw e;
         }

         zipOutputStream.close();
         if (byteArrayOutputStream.size() > 8388608L) {
            throw new IOException("скрипт больше 8 МБ");
         } else {
            return byteArrayOutputStream.toByteArray();
         }
      }
   }

   public static byte[] method6920(Script script) {
      Path path = script.method1412();
      if (path != null && Files.isRegularFile(path)) {
         try {
            if (Files.size(path) > 4194304L) {
               return null;
            } else {
               BufferedImage bufferedImage = ImageLoader.method234(Files.readAllBytes(path), 512);
               BufferedImage im = new BufferedImage(128, 128, 2);
               Graphics2D graphics = im.createGraphics();
               graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
               graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               int n = Math.min(bufferedImage.getWidth(), bufferedImage.getHeight());
               int n2 = (bufferedImage.getWidth() - n) / 2;
               int n3 = (bufferedImage.getHeight() - n) / 2;
               graphics.drawImage(bufferedImage, 0, 0, 128, 128, n2, n3, n2 + n, n3 + n, null);
               graphics.dispose();
               ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
               ImageIO.write(im, "png", byteArrayOutputStream);
               return byteArrayOutputStream.toByteArray();
            }
         } catch (Exception e) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static OriginUtil.Data2 method6921(Script script) {
      if (script != null && script.getPath() != null) {
         Path path = script.getPath();
         long now = System.currentTimeMillis();
         OriginUtil.Data data = items2.get(path);
         if (data != null && now - data.checkedAt() < 3000L) {
            return data.getData2();
         } else {
            Path path2 = path.resolve(".origin.json");
            long lastModified = path2.toFile().lastModified();
            if (data != null && data.modified() == lastModified) {
               items2.put(path, new OriginUtil.Data(now, lastModified, data.getData2()));
               return data.getData2();
            } else {
               OriginUtil.Data2 data2 = method6922(path2);
               items2.put(path, new OriginUtil.Data(now, lastModified, data2));
               return data2;
            }
         }
      } else {
         return null;
      }
   }

   private static OriginUtil.Data2 method6922(Path path) {
      if (!Files.isRegularFile(path)) {
         return null;
      } else {
         try {
            JsonObject jsonObject = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            return new OriginUtil.Data2(
               UUID.fromString(jsonObject.get("id").getAsString()),
               jsonObject.has("version") ? jsonObject.get("version").getAsLong() : 1L,
               jsonObject.has("sha") ? jsonObject.get("sha").getAsString() : "",
               jsonObject.has("name") ? jsonObject.get("name").getAsString() : "",
               jsonObject.has("owner") ? jsonObject.get("owner").getAsString() : "",
               jsonObject.has("mine") && jsonObject.get("mine").getAsBoolean(),
               jsonObject.has("installedAt") ? jsonObject.get("installedAt").getAsLong() : 0L
            );
         } catch (Exception e) {
            return null;
         }
      }
   }

   public static void method6923(Path start, StoreApi.Data2 entry, boolean mine) throws IOException {
      JsonObject json = new JsonObject();
      json.addProperty("id", entry.getUUID().toString());
      json.addProperty("version", entry.getLongValue2());
      json.addProperty("sha", entry.getText6());
      json.addProperty("name", entry.name());
      json.addProperty("owner", entry.getText8());
      json.addProperty("mine", mine);
      json.addProperty("installedAt", System.currentTimeMillis());
      Files.writeString(start.resolve(".origin.json"), json.toString(), StandardCharsets.UTF_8);
      items2.remove(start);
   }

   public static void method6924(Script script) {
      if (script != null && script.getPath() != null) {
         try {
            Files.deleteIfExists(script.getPath().resolve(".origin.json"));
            items2.remove(script.getPath());
         } catch (IOException e) {
         }
      }
   }

   public static Map<UUID, Script> method6925(Storage hostOrNull) {
      LinkedHashMap map = new LinkedHashMap();
      if (hostOrNull == null) {
         return map;
      } else {
         for (Script script : hostOrNull.method1293()) {
            OriginUtil.Data2 data2 = method6921(script);
            if (data2 != null) {
               map.put(data2.getUUID(), script);
            }
         }

         return map;
      }
   }

   public static Script method6926(Storage storage, UUID uuid) {
      if (storage == null) {
         return null;
      } else {
         Script script = null;

         for (Script script2 : storage.method1293()) {
            OriginUtil.Data2 data2 = method6921(script2);
            if (data2 != null && data2.getUUID().equals(uuid)) {
               script = script2;
            }
         }

         return script;
      }
   }

   public static boolean method6927(Storage hostOrNull, StoreApi.Data2 entry) {
      if (hostOrNull == null) {
         return false;
      } else {
         OriginUtil.Data2 data2 = null;

         for (Script script : hostOrNull.method1293()) {
            OriginUtil.Data2 data22 = method6921(script);
            if (data22 != null && data22.getUUID().equals(entry.getUUID())) {
               data2 = data22;
            }
         }

         return data2 != null && !data2.mine() && !data2.getText().equals(entry.getText6());
      }
   }

   public static Script method6928(Storage hostOrNull, StoreApi.Data2 entry, byte[] buf) throws IOException {
      if (!method6918(entry)) {
         throw new IOException("это скрипт старого формата");
      } else {
         Script script = method6926(hostOrNull, entry.getUUID());
         Path path = hostOrNull.getPath();
         Path dir = path.resolve(".incoming-" + UUID.randomUUID().toString().substring(0, 8));
         Files.createDirectories(dir);

         Script script2;
         try {
            method6931(buf, dir);
            if (!Files.isRegularFile(dir.resolve("main.js")) && !Files.isRegularFile(dir.resolve("src").resolve("main.js"))) {
               throw new IOException("в пакете нет main.js");
            }

            Path start;
            if (script != null && script.getPath() != null) {
               start = script.getPath();
               var strings = method6929(start);

               try (Stream<java.nio.file.Path> walk = Files.walk(start)) {
                  for (Path path2 : walk.sorted(Comparator.reverseOrder()).toList()) {
                     if (!path2.equals(start)) {
                        String string = path2.getFileName().toString();
                        if (!string.equals("storage.json") && !string.startsWith(".")) {
                           if (Files.isDirectory(path2)) {
                              label230:
                              try (Stream<java.nio.file.Path> list = Files.list(path2)) {
                                 if (!list.findAny().isPresent()) {
                                    break label230;
                                 }
                                 continue;
                              }

                              Files.deleteIfExists(path2);
                           } else {
                              String str = start.relativize(path2).toString().replace('\\', '/');
                              boolean contains = strings != null
                                 ? strings.contains(str)
                                 : str.toLowerCase(Locale.ROOT).endsWith(".js") || str.startsWith("lang/");
                              if (contains) {
                                 Files.deleteIfExists(path2);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               String str = Storage.method1303(entry.name().isBlank() ? "script" : entry.name());
               String argument = str;

               for (int i = 2; hostOrNull.method1294(argument) != null || Files.exists(path.resolve(argument)); i++) {
                  argument = str + "_" + i;
               }

               start = path.resolve(argument);
               Files.createDirectories(start);
            }

            ArrayList arrayList = new ArrayList();

            try (Stream<java.nio.file.Path> walk = Files.walk(dir)) {
               for (Path path2 : walk.sorted().toList()) {
                  if (!path2.equals(dir)) {
                     Path dir2 = start.resolve(dir.relativize(path2).toString());
                     if (Files.isDirectory(path2)) {
                        Files.createDirectories(dir2);
                     } else {
                        Files.move(path2, dir2, StandardCopyOption.REPLACE_EXISTING);
                        arrayList.add(dir.relativize(path2).toString().replace('\\', '/'));
                     }
                  }
               }
            }

            method6930(start, arrayList);
            method6923(start, entry, entry.mine());
            hostOrNull.method1300();
            var iterator = hostOrNull.method1293().iterator();

            Script script3;
            do {
               if (!iterator.hasNext()) {
                  return null;
               }

               script3 = (Script)iterator.next();
            } while (!start.equals(script3.getPath()));

            script2 = script3;
         } finally {
            method6932(dir);
         }

         return script2;
      }
   }

   private static Set<String> method6929(Path start) {
      Path path = start.resolve(".package.json");
      if (!Files.isRegularFile(path)) {
         return null;
      } else {
         try {
            HashSet<String> strings = new HashSet<>();

            for (JsonElement json : JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonArray()) {
               strings.add(json.getAsString());
            }

            return strings;
         } catch (Exception e) {
            return null;
         }
      }
   }

   private static void method6930(Path start, List<String> strings) throws IOException {
      JsonArray jsonArray = new JsonArray();
      strings.forEach(jsonArray::add);
      Files.writeString(start.resolve(".package.json"), jsonArray.toString(), StandardCharsets.UTF_8);
   }

   public static void uninstall(Storage hostOrNull, Script script) throws IOException {
      hostOrNull.method1357(script);
   }

   private static void method6931(byte[] buf, Path dir) throws IOException {
      long l = 0L;
      int n = 0;
      Path path = dir.toAbsolutePath().normalize();

      try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(buf))) {
         byte[] bytes = new byte[8192];

         ZipEntry nextEntry;
         while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            if (++n > 256) {
               throw new IOException("слишком много файлов в пакете");
            }

            String str = nextEntry.getName().replace('\\', '/');
            if (str.startsWith("/") || str.contains("..") || str.contains(":")) {
               throw new IOException("недопустимый путь: " + str);
            }

            String str2 = str.substring(str.lastIndexOf(47) + 1);
            if (!str2.startsWith(".") && !str2.equals("storage.json")) {
               Path dir2 = path.resolve(str).normalize();
               if (!dir2.startsWith(path)) {
                  throw new IOException("недопустимый путь: " + str);
               }

               if (nextEntry.isDirectory()) {
                  Files.createDirectories(dir2);
               } else {
                  Files.createDirectories(dir2.getParent());

                  int len;
                  try (OutputStream outputStream = Files.newOutputStream(dir2)) {
                     while ((len = zipInputStream.read(bytes)) > 0) {
                        l += len;
                        if (l > 8388608L) {
                           throw new IOException("пакет слишком большой после распаковки");
                        }

                        outputStream.write(bytes, 0, len);
                     }
                  }
               }
            }
         }
      }
   }

   private static void method6932(Path dir) {
      if (Files.exists(dir)) {
         try (Stream<java.nio.file.Path> walk = Files.walk(dir)) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).toList()) {
               Files.deleteIfExists(path);
            }
         } catch (IOException e) {
         }
      }
   }

   public static CompletableFuture<StoreApi.Data2> method6933(StoreApi storeApi, Storage hostOrNull, Script script, String str) {
      if (script.getPath() == null) {
         return CompletableFuture.failedFuture(new IOException("сначала переведите скрипт в папку: одиночные .js не публикуются"));
      } else {
         OriginUtil.Data2 data2 = method6921(script);
         UUID uuid = data2 != null && data2.mine() ? data2.getUUID() : null;
         long longValue = uuid == null ? 0L : data2.getLongValue();
         String display = script.method1403();
         String name = script.getId() + ".zjs";
         String description = script.getDescription();
         String str2 = method6914(script);
         return CompletableFuture.<byte[][]>supplyAsync(() -> {
               try {
                  return new byte[][]{method6919(script), method6920(script)};
               } catch (IOException var2x) {
                  throw new CompletionException(var2x);
               }
            }, Util.ioPool())
            .thenCompose(var10x -> storeApi.method1973("script", display, name, description, str, var10x[0], uuid, longValue, str2).thenCompose(var3xx -> {
               byte[] var4xx = var10x[1];

               try {
                  method6923(script.getPath(), var3xx, true);
               } catch (IOException var6xx) {
               }

               if (var4xx != null) {
                  return storeApi.method1976(var3xx.getUUID(), var4xx).exceptionally(var1xxx -> var3xx);
               } else {
                  return var3xx.hasPreview() ? storeApi.method1977(var3xx.getUUID()).exceptionally(var1xxx -> var3xx) : CompletableFuture.completedFuture(var3xx);
               }
            }));
      }
   }

   private static final class Data {
      private final long longValue;
      private final long longValue2;
      private final OriginUtil.Data2 data2;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public OriginUtil.Data2 data2() {
         return this.data2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.data2, other.data2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, data2);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "data2=" + this.data2 + "]";
      }

      private Data(long longValue, long lastModified, OriginUtil.Data2 data2) {
         this.longValue = longValue;
         this.longValue2 = lastModified;
         this.data2 = data2;
      }

      public long checkedAt() {
         return this.longValue;
      }

      public long modified() {
         return this.longValue2;
      }

      public OriginUtil.Data2 getData2() {
         return this.data2;
      }
   }

   public static final class Data2 {
      private final UUID uUID;
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final boolean flag;
      private final long longValue2;

      public UUID uUID() {
         return this.uUID;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public boolean flag() {
         return this.flag;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, longValue, text, text2, text3, flag, longValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "flag=" + this.flag + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data2(UUID uUID, long longValue, String text, String string, String str, boolean flag, long l) {
         this.uUID = uUID;
         this.longValue = longValue;
         this.text = text;
         this.text2 = string;
         this.text3 = str;
         this.flag = flag;
         this.longValue2 = l;
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public boolean mine() {
         return this.flag;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data3 {
      private final String text;
      private final List<String> items;
      private final int intValue;
      private final String text2;
      private final String text3;

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, intValue, text2, text3);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }
      public static final OriginUtil.Data3 INSTANCE = new OriginUtil.Data3("", List.of(), -1, "", "");

      public Data3(String text, List<String> items, int intValue, String str, String str2) {
         this.text = text;
         this.items = items;
         this.intValue = intValue;
         this.text2 = str;
         this.text3 = str2;
      }

      public String version() {
         return this.text;
      }

      public List<String> getItems() {
         return this.items;
      }

      public int color() {
         return this.intValue;
      }

      public String author() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }
}

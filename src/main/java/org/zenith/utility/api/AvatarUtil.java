package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.zenith.Zenith;
import org.zenith.utility.config.BuiltinCosmeticsPath;
import org.zenith.utility.config.FiguraManager;

public final class AvatarUtil {
   private static final String text = "avatar.json";
   private static final String text2 = "cosmetics";
   private static final int CONST_intValue = 8;
   private static final long CONST_longValue = 1000L;
   private static volatile List<AvatarUtil.Data> items = List.of();
   private static volatile long longValue;
   private static final Map<String, AvatarUtil.Type> items2 = new ConcurrentHashMap<>();
   private static final String text3 = "head-";
   private static final String text4 = "weapon-";
   private static final String[] texts = new String[]{
      "weapon",
      "sword",
      "greatsword",
      "blade",
      "scythe",
      "hammer",
      "halberd",
      "axe",
      "spear",
      "dagger",
      "katana",
      "trident",
      "staff",
      "bow",
      "оружие",
      "меч",
      "коса",
      "молот",
      "топор",
      "клинок",
      "копье",
      "кинжал",
      "катана",
      "посох"
   };

   private AvatarUtil() {
   }

   public static Path method6670() {
      try {
         Path dir = Zenith.file.toPath().resolve("cosmetics");
         Files.createDirectories(dir);
         return dir;
      } catch (Exception e) {
         return null;
      }
   }

   public static List<AvatarUtil.Data> method6671() {
      long now = System.currentTimeMillis();
      var datas = items;
      if (now - longValue < 1000L) {
         return datas;
      } else {
         longValue = now;
         items = method6677();
         return items;
      }
   }

   public static void invalidate() {
      longValue = 0L;
      items2.clear();
   }

   public static String method6672() {
      List<AvatarUtil.Data> datas = method6671();
      StringBuilder builder = new StringBuilder(datas.size() * 16);

      for (AvatarUtil.Data data : datas) {
         builder.append(data.getText2()).append('|');
      }

      return builder.toString();
   }

   public static List<AvatarUtil.Data> method6673(AvatarUtil.Type type) {
      ArrayList<AvatarUtil.Data> datas = new ArrayList<>();

      for (AvatarUtil.Data data : method6671()) {
         if (data.getType() == type) {
            datas.add(data);
         }
      }

      return datas;
   }

   public static boolean method6674(AvatarUtil.Data data) {
      FiguraManager figuraManager = method6676();
      if (figuraManager == null) {
         return false;
      } else {
         Path path3 = data.getType() == AvatarUtil.Type.WEAPON ? figuraManager.getPath3() : figuraManager.getPath();
         return method6682(path3, data.getPath());
      }
   }

   public static void method6675(AvatarUtil.Data data) {
      FiguraManager figuraManager = method6676();
      if (figuraManager != null) {
         Path path = method6674(data) ? null : data.getPath();
         if (data.getType() == AvatarUtil.Type.WEAPON) {
            figuraManager.method7673(path);
         } else {
            figuraManager.method7672(path);
         }
      }
   }

   private static FiguraManager method6676() {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? null : zenith.getFiguraManager();
   }

   private static List<AvatarUtil.Data> method6677() {
      ArrayList coll = new ArrayList();
      HashSet hashSet = new HashSet();
      method6678(method6670(), coll, hashSet);
      method6678(BuiltinCosmeticsPath.method4447(), coll, hashSet);
      coll.sort(Comparator.comparing((AvatarUtil.Data var0x) -> var0x.getText2().toLowerCase(Locale.ROOT)));
      return List.copyOf(coll);
   }

   private static void method6678(Path path, List<AvatarUtil.Data> coll, Set<String> strings) {
      if (path != null) {
         Path path2 = path.toAbsolutePath().normalize();
         ArrayList<Path> paths = new ArrayList();
         method6681(path2, paths, 0);

         for (Path path3 : paths) {
            String str = path2.relativize(path3).toString().replace('\\', '/');
            if (strings.add(str.toLowerCase(Locale.ROOT))) {
               Path fileName = path3.getFileName();
               String string = fileName == null ? str : fileName.toString();
               coll.add(new AvatarUtil.Data(string, str, path3, method6679(path3, str, string)));
            }
         }
      }
   }

   private static AvatarUtil.Type method6679(Path path, String str, String string) {
      String string2 = path.toString();
      AvatarUtil.Type type = items2.get(string2);
      if (type != null) {
         return type;
      } else {
         AvatarUtil.Type type2 = AvatarUtil.Type.method6683(str, string, method6680(path));
         items2.put(string2, type2);
         return type2;
      }
   }

   private static String method6680(Path path) {
      String string;
      try {
         String json = Files.readString(path.resolve("avatar.json"), StandardCharsets.UTF_8);
         JsonElement jsonElement = JsonParser.parseString(json);
         if (!jsonElement.isJsonObject()) {
            return null;
         }

         JsonElement jsonElement2 = jsonElement.getAsJsonObject().get("category");
         string = jsonElement2 != null && !jsonElement2.isJsonNull() ? jsonElement2.getAsString() : null;
      } catch (Exception e) {
         return null;
      }

      return string;
   }

   private static void method6681(Path path, List<Path> paths, int n) {
      if (n <= 8 && Files.isDirectory(path) && !isHidden(path)) {
         if (Files.isRegularFile(path.resolve("avatar.json"))) {
            paths.add(path.toAbsolutePath().normalize());
         } else {
            try (Stream<java.nio.file.Path> list = Files.list(path)) {
               list.filter(var0x -> Files.isDirectory(var0x)).forEach(var2x -> method6681(var2x, paths, n + 1));
            } catch (Exception e) {
            }
         }
      }
   }

   private static boolean isHidden(Path path) {
      Path fileName = path.getFileName();
      return (fileName != null && fileName.toString().startsWith("."));
   }

   private static boolean method6682(Path path3, Path path) {
      if (path3 != null && path != null) {
         return path3.toAbsolutePath().normalize().equals(path.toAbsolutePath().normalize());
      } else {
         return (path3 == path);
      }
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final Path path;
      private final AvatarUtil.Type type;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public Path path() {
         return this.path;
      }

      public AvatarUtil.Type type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.path, other.path) && java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, path, type);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "path=" + this.path + ", " + "type=" + this.type + "]";
      }

      public Data(String text, String str, Path path, AvatarUtil.Type type) {
         this.text = text;
         this.text2 = str;
         this.path = path;
         this.type = type;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public Path getPath() {
         return this.path;
      }

      public AvatarUtil.Type getType() {
         return this.type;
      }
   }

   public static enum Type {
      MODEL,
      HAT,
      WEAPON;

      private static AvatarUtil.Type method6683(String str, String string, String str2) {
         if (method6684(str, string, "weapon-")) {
            return WEAPON;
         } else if (method6684(str, string, "head-")) {
            return HAT;
         } else if (method6685(str2)) {
            return WEAPON;
         } else {
            return method6686(str + " " + string) ? WEAPON : MODEL;
         }
      }

      private static boolean method6684(String str, String string, String prefix) {
         return str.toLowerCase(Locale.ROOT).startsWith(prefix) || string.toLowerCase(Locale.ROOT).startsWith(prefix);
      }

      private static boolean method6685(String str) {
         if (str == null) {
            return false;
         } else {
            String lowerCase = str.strip().toLowerCase(Locale.ROOT);
            return lowerCase.equals("weapon") || lowerCase.equals("weapons") || lowerCase.equals("оружие");
         }
      }

      private static boolean method6686(String str) {
         String lowerCase = str.toLowerCase(Locale.ROOT);

         for (String text : AvatarUtil.texts) {
            if (lowerCase.contains(text)) {
               return true;
            }
         }

         return false;
      }
   }
}

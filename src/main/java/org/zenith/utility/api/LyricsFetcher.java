package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.zenith.Zenith;

public final class LyricsFetcher {
   private static final Pattern pattern = Pattern.compile("\\[(\\d{1,2}):(\\d{1,2})(?:[.:](\\d{1,3}))?]");
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build();
   private static final Map<String, LyricsFetcher.Data2> items = new ConcurrentHashMap<>();
   private static final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private static volatile LyricsFetcher.Data data;
   private static final Pattern pattern2 = Pattern.compile("(?i)\\s*[(\\[](feat|ft|prod|explicit|remaster)[^)\\]]*[)\\]]");
   private static final String[] texts = new String[]{
      "а",
      "a",
      "б",
      "b",
      "в",
      "v",
      "г",
      "g",
      "д",
      "d",
      "е",
      "e",
      "ё",
      "e",
      "ж",
      "zh",
      "з",
      "z",
      "и",
      "i",
      "й",
      "y",
      "к",
      "k",
      "л",
      "l",
      "м",
      "m",
      "н",
      "n",
      "о",
      "o",
      "п",
      "p",
      "р",
      "r",
      "с",
      "s",
      "т",
      "t",
      "у",
      "u",
      "ф",
      "f",
      "х",
      "kh",
      "ц",
      "ts",
      "ч",
      "ch",
      "ш",
      "sh",
      "щ",
      "sch",
      "ъ",
      "",
      "ы",
      "y",
      "ь",
      "",
      "э",
      "e",
      "ю",
      "yu",
      "я",
      "ya"
   };

   private LyricsFetcher() {
   }

   public static LyricsFetcher.Data2 method528(String title, String str, double d) {
      if (title != null && !title.isBlank()) {
         String str2 = method530(title, str);
         LyricsFetcher.Data2 data2 = items.get(str2);
         if (data2 != null) {
            return data2;
         } else {
            if (items2.add(str2)) {
               Thread thread = new Thread(() -> {
                  LyricsFetcher.Data2 var5x = method533(str2, title, str, d);
                  items.put(str2, var5x);
                  items2.remove(str2);
               }, "zenith-lyrics");
               thread.setDaemon(true);
               thread.start();
            }

            return LyricsFetcher.Data2.INSTANCE2;
         }
      } else {
         return LyricsFetcher.Data2.INSTANCE;
      }
   }

   public static double method529(String title, String str) {
      if (title != null && !title.isBlank()) {
         LyricsFetcher.Data2 data2 = items.get(method530(title, str));
         return data2 == null ? 0.0 : data2.getDoubleValue();
      } else {
         return 0.0;
      }
   }

   private static String method530(String title, String str) {
      LyricsFetcher.Data data2 = data;
      if (data2 != null && data2.title() == title && data2.getText2() == str) {
         return data2.key();
      } else {
         String lowerCase = (method531(str) + " - " + method532(title)).toLowerCase(Locale.ROOT);
         data = new LyricsFetcher.Data(title, str, lowerCase);
         return lowerCase;
      }
   }

   public static String method531(String str) {
      String str2 = str == null ? "" : str;

      for (String str3 : new String[]{" — ", " – ", " - "}) {
         int indexOf = str2.indexOf(str3);
         if (indexOf > 0) {
            str2 = str2.substring(0, indexOf);
            break;
         }
      }

      return str2.trim();
   }

   public static String method532(String title) {
      return pattern2.matcher(title).replaceAll("").trim();
   }

   private static LyricsFetcher.Data2 method533(String str, String title, String str2, double d) {
      Path path = null;

      try {
         Path dir = Zenith.file.toPath().resolve("media").resolve("lyrics");
         Files.createDirectories(dir);
         path = dir.resolve(Integer.toHexString(str.hashCode()) + ".json");
         if (Files.isRegularFile(path)) {
            LyricsFetcher.Data2 data2 = method539(JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject());
            if (data2 != null) {
               return data2;
            }
         }
      } catch (Throwable e) {
      }

      String anotherString = method532(title);
      String anotherString2 = method531(str2);
      JsonObject jsonObject = null;

      try {
         JsonElement jsonElement = method540(
            "https://lrclib.net/api/get?track_name=" + encode(anotherString) + "&artist_name=" + encode(anotherString2) + (d > 0.0 ? "&duration=" + Math.round(d) : "")
         );
         if (jsonElement != null && jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("id")) {
            jsonObject = jsonElement.getAsJsonObject();
         }

         if (jsonObject == null || !method538(jsonObject, "syncedLyrics")) {
            ArrayList<String> strings = new ArrayList();
            strings.add("track_name=" + encode(anotherString) + "&artist_name=" + encode(anotherString2));
            strings.add("q=" + encode(anotherString2 + " " + anotherString));
            String anotherString3 = method537(anotherString2);
            if (!anotherString3.equalsIgnoreCase(anotherString2)) {
               strings.add("q=" + encode(anotherString3 + " " + anotherString));
               strings.add("q=" + encode(anotherString3 + " " + method537(anotherString)));
               strings.add("q=" + encode(anotherString3));
            }

            strings.add("track_name=" + encode(anotherString));

            for (String str3 : strings) {
               JsonObject json = method534(method540("https://lrclib.net/api/search?" + str3), anotherString, anotherString2, d);
               if (json != null && (jsonObject == null || method538(json, "syncedLyrics"))) {
                  jsonObject = json;
                  if (method538(json, "syncedLyrics")) {
                     break;
                  }
               }
            }
         }
      } catch (Throwable e) {
      }

      if (jsonObject == null) {
         return LyricsFetcher.Data2.INSTANCE;
      } else {
         try {
            if (path != null) {
               Files.writeString(path, jsonObject.toString(), StandardCharsets.UTF_8);
            }
         } catch (Throwable e) {
         }

         LyricsFetcher.Data2 data2 = method539(jsonObject);
         return data2 == null ? LyricsFetcher.Data2.INSTANCE : data2;
      }
   }

   private static JsonObject method534(JsonElement jsonElement, String anotherString, String str, double d) {
      if (jsonElement != null && jsonElement.isJsonArray()) {
         String anObject = method536(anotherString);
         String str2 = method536(method537(str));
         JsonObject jsonObject = null;
         double d2 = Double.MAX_VALUE;

         for (JsonElement json : jsonElement.getAsJsonArray()) {
            if (json.isJsonObject()) {
               JsonObject jsonObject2 = json.getAsJsonObject();
               String str3 = method536(method535(jsonObject2, "trackName"));
               String str4 = method536(method537(method535(jsonObject2, "artistName")));
               boolean flag = !anObject.isEmpty() && (str3.equals(anObject) || str3.contains(anObject) || anObject.contains(str3) && str3.length() > 3);
               if (flag) {
                  double d3 = jsonObject2.has("duration") && !jsonObject2.get("duration").isJsonNull() ? jsonObject2.get("duration").getAsDouble() : 0.0;
                  boolean flag2 = !str2.isEmpty() && (str4.contains(str2) || str2.contains(str4) || str3.contains(str2));
                  boolean flag3 = d > 0.0 && d3 > 0.0 && Math.abs(d3 - d) <= 3.0;
                  if (flag2 || flag3) {
                     double d4 = (d > 0.0 && d3 > 0.0 ? Math.abs(d3 - d) : 20.0)
                        - (method538(jsonObject2, "syncedLyrics") ? 1000 : 0)
                        - (flag2 ? 50 : 0);
                     if (d4 < d2) {
                        d2 = d4;
                        jsonObject = jsonObject2;
                     }
                  }
               }
            }
         }

         return jsonObject;
      } else {
         return null;
      }
   }

   private static String method535(JsonObject json, String memberName) {
      return json.has(memberName) && !json.get(memberName).isJsonNull() ? json.get(memberName).getAsString() : "";
   }

   private static String method536(String anotherString) {
      StringBuilder builder = new StringBuilder();

      for (char ch : method537(anotherString.toLowerCase(Locale.ROOT)).toCharArray()) {
         if (Character.isLetterOrDigit(ch)) {
            builder.append(ch);
         }
      }

      return builder.toString();
   }

   public static String method537(String anotherString) {
      StringBuilder builder = new StringBuilder();

      for (char ch : anotherString.toCharArray()) {
         char lowerCase = Character.toLowerCase(ch);
         String text = null;

         for (byte i = 0; i < texts.length; i += 2) {
            if (texts[i].charAt(0) == lowerCase) {
               text = texts[i + 1];
               break;
            }
         }

         if (text == null) {
            builder.append(ch);
         } else if (Character.isUpperCase(ch) && !text.isEmpty()) {
            builder.append(Character.toUpperCase(text.charAt(0))).append(text.substring(1));
         } else {
            builder.append(text);
         }
      }

      return builder.toString();
   }

   private static boolean method538(JsonObject jsonObject, String memberName) {
      return jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull() && !jsonObject.get(memberName).getAsString().isBlank();
   }

   private static LyricsFetcher.Data2 method539(JsonObject jsonObject) {
      boolean flag = jsonObject.has("instrumental") && !jsonObject.get("instrumental").isJsonNull() && jsonObject.get("instrumental").getAsBoolean();
      double doubleValue = jsonObject.has("duration") && !jsonObject.get("duration").isJsonNull() ? jsonObject.get("duration").getAsDouble() : 0.0;
      if (method538(jsonObject, "syncedLyrics")) {
         ArrayList<LyricsFetcher.Data3> list = new ArrayList<>();

         for (String input : jsonObject.get("syncedLyrics").getAsString().split("\n")) {
            Matcher matcher = pattern.matcher(input);
            ArrayList<Double> doubles = new ArrayList();

            int end;
            for (end = 0; matcher.find(); end = matcher.end()) {
               double d = Double.parseDouble(matcher.group(1));
               double d2 = Double.parseDouble(matcher.group(2));
               String group = matcher.group(3);
               double d3 = group == null ? 0.0 : Double.parseDouble(group) / Math.pow(10.0, group.length());
               doubles.add(d * 60.0 + d2 + d3);
            }

            if (!doubles.isEmpty()) {
               String text = input.substring(end).trim();

               for (double d : doubles) {
                  list.add(new LyricsFetcher.Data3(d, text));
               }
            }
         }

         list.sort((var0x, var1x) -> Double.compare(var0x.time(), var1x.time()));
         return new LyricsFetcher.Data2(Collections.unmodifiableList(list), true, flag, doubleValue);
      } else if (!method538(jsonObject, "plainLyrics")) {
         return flag ? new LyricsFetcher.Data2(List.of(), false, true, doubleValue) : null;
      } else {
         ArrayList list = new ArrayList();

         for (String str : jsonObject.get("plainLyrics").getAsString().split("\n")) {
            list.add(new LyricsFetcher.Data3(-1.0, str.trim()));
         }

         return new LyricsFetcher.Data2(Collections.unmodifiableList(list), false, flag, doubleValue);
      }
   }

   private static JsonElement method540(String str) throws Exception {
      HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(str))
         .timeout(Duration.ofSeconds(8L))
         .header("User-Agent", "ZenithClient/3.5 (https://zenithdlc.com)")
         .GET()
         .build();
      HttpResponse send = httpClient.send(httpRequest, BodyHandlers.ofString(StandardCharsets.UTF_8));
      return send.statusCode() != 200 ? null : JsonParser.parseString((String)send.body());
   }

   private static String encode(String anotherString) {
      return URLEncoder.encode(anotherString, StandardCharsets.UTF_8);
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final String text3;

      public String text() {
         return this.text;
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      private Data(String text, String str, String lowerCase) {
         this.text = text;
         this.text2 = str;
         this.text3 = lowerCase;
      }

      public String title() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String key() {
         return this.text3;
      }
   }

   public static final class Data2 {
      private final List<LyricsFetcher.Data3> items;
      private final boolean flag;
      private final boolean flag2;
      private final double doubleValue;

      public List<LyricsFetcher.Data3> items() {
         return this.items;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, flag, flag2, doubleValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "items=" + this.items + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "doubleValue=" + this.doubleValue + "]";
      }
      public static final LyricsFetcher.Data2 INSTANCE = new LyricsFetcher.Data2(List.of(), false, false, 0.0);
      public static final LyricsFetcher.Data2 INSTANCE2 = new LyricsFetcher.Data2(List.of(), false, false, 0.0);

      public Data2(List<LyricsFetcher.Data3> items, boolean flag, boolean flag2, double doubleValue) {
         this.items = items;
         this.flag = flag;
         this.flag2 = flag2;
         this.doubleValue = doubleValue;
      }

      public int method541(double d) {
         if (this.flag && !this.items.isEmpty()) {
            int n = -1;
            int n2 = 0;

            while (n2 < this.items.size() && this.items.get(n2).time() <= d + 0.15) {
               n = n2++;
            }

            return n;
         } else {
            return -1;
         }
      }

      public List<LyricsFetcher.Data3> lines() {
         return this.items;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }
   }

   public static final class Data3 {
      private final double doubleValue;
      private final String text;

      public double doubleValue() {
         return this.doubleValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, text);
      }

      @Override
      public String toString() {
         return "Data3[" + "doubleValue=" + this.doubleValue + ", " + "text=" + this.text + "]";
      }

      public Data3(double doubleValue, String text) {
         this.doubleValue = doubleValue;
         this.text = text;
      }

      public double time() {
         return this.doubleValue;
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class AimMotionLibrary {
   public static final int CONST_intValue = 6;
   private static final String text = "/assets/zenith/aimpipe/aim_motion_library.json";
   private static final Path path = Path.of("aim_motion_library.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "out", "aim_motion_library_6t.json"),
      Path.of("scripts", "out", "aim_motion_library_6t.json"),
      Path.of("..", "scripts", "test", "aimpipe", "out", "aim_motion_library.json"),
      Path.of("scripts", "test", "aimpipe", "out", "aim_motion_library.json")
   };
   private final List<AimMotionLibrary.Entry> items;

   private AimMotionLibrary(List<AimMotionLibrary.Entry> coll) {
      this.items = List.copyOf(coll);
   }

   public List<AimMotionLibrary.Entry> getItems() {
      return this.items;
   }

   public static AimMotionLibrary method1266() {
      AimMotionLibrary aimMotionLibrary = method1267(path);
      if (aimMotionLibrary != null) {
         return aimMotionLibrary;
      } else {
         for (Path path2 : paths) {
            aimMotionLibrary = method1267(path2);
            if (aimMotionLibrary != null) {
               return aimMotionLibrary;
            }
         }

         return method1268();
      }
   }

   private static AimMotionLibrary method1267(Path path) {
      try {
         if (!Files.isRegularFile(path)) {
            return null;
         } else {
            String string = Files.readString(path, StandardCharsets.UTF_8);
            return method1269(string, path.toAbsolutePath().toString());
         }
      } catch (Throwable e) {
         System.err.println("[AimMotionLibrary] failed to load " + path + ": " + e);
         return null;
      }
   }

   private static AimMotionLibrary method1268() {
      try {
         AimMotionLibrary aimMotionLibrary;
         try (InputStream resourceAsStream = AimMotionLibrary.class.getResourceAsStream("/assets/zenith/aimpipe/aim_motion_library.json")) {
            if (resourceAsStream == null) {
               return null;
            }

            String str = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
            aimMotionLibrary = method1269(str, "/assets/zenith/aimpipe/aim_motion_library.json");
         }

         return aimMotionLibrary;
      } catch (Throwable e) {
         System.err.println("[AimMotionLibrary] failed to load /assets/zenith/aimpipe/aim_motion_library.json: " + e);
         return null;
      }
   }

   private static AimMotionLibrary method1269(String string, String str) {
      JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
      int n = jsonObject.get("format_version").getAsInt();
      if (n != 1) {
         throw new IllegalStateException("unsupported motion-library format_version " + n);
      } else {
         JsonElement jsonElement = jsonObject.get("source_gcd");
         if (jsonElement != null && !jsonElement.isJsonNull()) {
            float f = jsonElement.getAsFloat();
            if (Float.isFinite(f) && !(f <= 0.0F)) {
               JsonArray jsonArray = jsonObject.getAsJsonArray("primitives");
               List<Entry> coll = new ArrayList<>(jsonArray.size());

               for (JsonElement json : jsonArray) {
                  JsonObject jsonObject2 = json.getAsJsonObject();
                  int n2 = jsonObject2.get("length").getAsInt();
                  if (n2 >= 1 && n2 <= 6) {
                     JsonArray jsonArray2 = jsonObject2.getAsJsonArray("commands");
                     if (jsonArray2.size() != n2) {
                        throw new IllegalStateException("primitive length does not match command count");
                     }

                     int[] intValues = new int[n2];
                     int[] intValues2 = new int[n2];
                     int n3 = 0;
                     int n4 = 0;

                     for (int i = 0; i < n2; i++) {
                        JsonArray jsonArray3 = jsonArray2.get(i).getAsJsonArray();
                        if (jsonArray3.size() != 2) {
                           throw new IllegalStateException("primitive command must contain yaw and pitch");
                        }

                        intValues[i] = jsonArray3.get(0).getAsInt();
                        intValues2[i] = jsonArray3.get(1).getAsInt();
                        if (intValues[i] == 0 && intValues2[i] == 0) {
                           throw new IllegalStateException("primitive contains an internal joint-zero command");
                        }

                        n3 += intValues[i];
                        n4 += intValues2[i];
                     }

                     int intValue = jsonObject2.get("sum_yaw").getAsInt();
                     int n5 = jsonObject2.get("sum_pitch").getAsInt();
                     if (n3 == intValue && n4 == n5) {
                        coll.add(new AimMotionLibrary.Entry(intValues, intValues2, intValue, n5));
                        continue;
                     }

                     throw new IllegalStateException("primitive sum does not match commands");
                  }

                  throw new IllegalStateException("primitive length outside 1..6: " + n2);
               }

               if (coll.isEmpty()) {
                  throw new IllegalStateException("motion library contains no primitives");
               } else if (jsonObject.has("count") && jsonObject.get("count").getAsInt() != coll.size()) {
                  throw new IllegalStateException("motion-library count does not match primitive array");
               } else {
                  AimMotionLibrary aimMotionLibrary = new AimMotionLibrary(coll);
                  System.out.println("[AimMotionLibrary] loaded " + str + " (primitives=" + coll.size() + ", maxTicks=6)");
                  return aimMotionLibrary;
               }
            } else {
               throw new IllegalStateException("motion-library source_gcd must be finite and positive");
            }
         } else {
            throw new IllegalStateException("motion library must declare one source_gcd");
         }
      }
   }

   public static final class Entry {
      private final int[] intValues;
      private final int[] intValues2;
      private final int intValue;
      private final int intValue2;

      private Entry(int[] intValues, int[] intValues2, int intValue, int n) {
         this.intValues = intValues;
         this.intValues2 = intValues2;
         this.intValue = intValue;
         this.intValue2 = n;
      }

      public int length() {
         return this.intValues.length;
      }

      public int method1270(int n) {
         return this.intValues[n];
      }

      public int method1271(int n) {
         return this.intValues2[n];
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}

package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ObjParser {
   private ObjParser() {
   }

   public static CosmeticSkeleton method8021(String str, String str2) {
      CosmeticSkeleton.Entry entry = new CosmeticSkeleton.Entry();
      int intValue = entry.method7146("root", -1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      var map = method8025(str2);
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      ArrayList arrayList3 = new ArrayList();
      int n = entry.method7147("");

      for (String str3 : str.split("\n")) {
         String str4 = str3.strip();
         if (!str4.isEmpty() && str4.charAt(0) != '#') {
            String[] strings = str4.split("\\s+");
            String str5 = strings[0];
            switch (str5) {
               case "v":
                  arrayList.add(method8024(strings, 3, 0.0F));
                  break;
               case "vt":
                  arrayList2.add(method8024(strings, 2, 0.0F));
                  break;
               case "vn":
                  arrayList3.add(method8024(strings, 3, 0.0F));
                  break;
               case "usemtl":
                  String str6 = strings.length > 1 ? (String)map.get(strings[1]) : null;
                  if (str6 != null) {
                     n = entry.method7147(str6);
                  }
                  break;
               case "f":
                  method8022(entry, intValue, n, strings, arrayList, arrayList2, arrayList3);
            }
         }
      }

      return entry.method7152();
   }

   private static void method8022(CosmeticSkeleton.Entry entry, int intValue, int n, String[] strings, List<float[]> floatses, List<float[]> floatses2, List<float[]> floatses3) {
      int n2 = strings.length - 1;
      if (n2 >= 3) {
         for (byte i = 2; i <= n2 - 1; i += 2) {
            byte b = 1;
            int n3 = Math.min(i + 1, n2);
            int n4 = Math.min(i + 2, n2);
            float[] floatValues = new float[12];
            float[] floatValues2 = new float[8];
            float[] floats = null;
            int[] ints = new int[]{b, i, n3, n4};

            for (int j = 0; j < 4; j++) {
               String[] strings2 = strings[ints[j]].split("/", -1);
               float[] floats2 = method8023(floatses, strings2, 0);
               if (floats2 == null) {
                  return;
               }

               floatValues[j * 3] = floats2[0];
               floatValues[j * 3 + 1] = floats2[1];
               floatValues[j * 3 + 2] = floats2[2];
               float[] floats3 = method8023(floatses2, strings2, 1);
               if (floats3 != null) {
                  floatValues2[j * 2] = floats3[0];
                  floatValues2[j * 2 + 1] = 1.0F - floats3[1];
               }

               if (floats == null) {
                  float[] floats4 = method8023(floatses3, strings2, 2);
                  if (floats4 != null) {
                     floats = new float[]{floats4[0], floats4[1], floats4[2]};
                  }
               }
            }

            entry.method7148(intValue, n, floatValues, floatValues2, floats);
         }
      }
   }

   private static float[] method8023(List<float[]> floatses, String[] strings, int n) {
      if (!floatses.isEmpty() && n < strings.length && !strings[n].isBlank()) {
         try {
            int n2 = Integer.parseInt(strings[n]);
            int n3 = n2 < 0 ? floatses.size() + n2 : n2 - 1;
            return n3 >= 0 && n3 < floatses.size() ? (float[])floatses.get(n3) : null;
         } catch (NumberFormatException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static float[] method8024(String[] strings, int n, float f) {
      float[] floats = new float[n];

      for (int i = 0; i < n; i++) {
         floats[i] = f;
         if (i + 1 < strings.length) {
            try {
               floats[i] = Float.parseFloat(strings[i + 1]);
            } catch (NumberFormatException e) {
            }
         }
      }

      return floats;
   }

   private static Map<String, String> method8025(String str) {
      HashMap map = new HashMap();
      if (str == null) {
         return map;
      } else {
         String key = null;

         for (String str2 : str.split("\n")) {
            String str3 = str2.strip();
            if (str3.startsWith("newmtl ")) {
               key = str3.substring(7).strip();
            } else if (str3.startsWith("map_Kd ") && key != null) {
               map.put(key, str3.substring(7).strip());
            }
         }

         return map;
      }
   }
}

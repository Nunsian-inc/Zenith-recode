package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;

public final class Util7 {
   private Util7() {
   }

   public static float[] method1610(String name, float f, boolean flag) {
      InputStream resourceAsStream = Util7.class.getResourceAsStream(name);
      if (resourceAsStream == null) {
         return null;
      } else {
         try {
            float[] floats;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {
               ArrayList arrayList = new ArrayList();
               HashMap map = new HashMap();
               float f2 = Float.POSITIVE_INFINITY;
               float f3 = Float.POSITIVE_INFINITY;
               float f4 = Float.POSITIVE_INFINITY;
               float f5 = Float.NEGATIVE_INFINITY;
               float f6 = Float.NEGATIVE_INFINITY;
               float f7 = Float.NEGATIVE_INFINITY;

               String line;
               while ((line = bufferedReader.readLine()) != null) {
                  line = line.trim();
                  if (line.startsWith("v ")) {
                     String[] strings = line.split("\\s+");
                     float x = Float.parseFloat(strings[1]);
                     float y = Float.parseFloat(strings[2]) - f;
                     float z = Float.parseFloat(strings[3]);
                     arrayList.add(new Vector3f(x, y, z));
                     f2 = Math.min(f2, x);
                     f5 = Math.max(f5, x);
                     f3 = Math.min(f3, y);
                     f6 = Math.max(f6, y);
                     f4 = Math.min(f4, z);
                     f7 = Math.max(f7, z);
                  } else if (line.startsWith("f ")) {
                     method1611(line.split("\\s+"), arrayList, map);
                  }
               }

               float f8 = 0.0F;
               float f9 = 0.0F;
               float f10 = 0.0F;
               float f11 = 1.0F;
               if (flag) {
                  f8 = (f2 + f5) * 0.5F;
                  f9 = (f3 + f6) * 0.5F;
                  f10 = (f4 + f7) * 0.5F;
                  float f12;
                  f11 = (f12 = Math.max(f5 - f2, Math.max(f6 - f3, f7 - f4))) > 1.0E-5F ? 1.0F / f12 : 1.0F;
               }

               ArrayList arrayList2 = new ArrayList();

               for (Object obj : (Iterable<?>)(Object)(map.values())) {
                  Util7.Entry entry = (Util7.Entry)obj;
                  if (entry.count == 1 || !entry.flag) {
                     arrayList2.add((entry.floatValue - f8) * f11);
                     arrayList2.add((entry.floatValue2 - f9) * f11);
                     arrayList2.add((entry.floatValue3 - f10) * f11);
                     arrayList2.add((entry.floatValue4 - f8) * f11);
                     arrayList2.add((entry.floatValue5 - f9) * f11);
                     arrayList2.add((entry.floatValue6 - f10) * f11);
                  }
               }

               float[] floats2 = new float[arrayList2.size()];

               for (int i = 0; i < arrayList2.size(); i++) {
                  floats2[i] = (Float)arrayList2.get(i);
               }

               floats = floats2;
            }

            return floats;
         } catch (Exception e) {
            return null;
         }
      }
   }

   private static void method1611(String[] strings, List<Vector3f> vecs, Map<Long, Util7.Entry> map) {
      for (int i = 1; i < strings.length; i++) {
         int n = i == strings.length - 1 ? 1 : i + 1;
         int n2 = Integer.parseInt(strings[i].split("/")[0]) - 1;
         int n3 = Integer.parseInt(strings[n].split("/")[0]) - 1;
         if (n2 != n3) {
            String[] strings2 = strings[i].split("/");
            int intValue = strings2.length > 2 && !strings2[2].isEmpty() ? Integer.parseInt(strings2[2]) - 1 : -1;
            long l = (long)Math.min(n2, n3) << 32 | Math.max(n2, n3) & 4294967295L;
            Util7.Entry entry = (Util7.Entry)map.get(l);
            if (entry == null) {
               Vector3f vec = (Vector3f)vecs.get(n2);
               Vector3f vec2 = (Vector3f)vecs.get(n3);
               map.put(l, new Util7.Entry(vec.x, vec.y, vec.z, vec2.x, vec2.y, vec2.z, intValue));
            } else {
               entry.count++;
               if (entry.intValue != intValue) {
                  entry.flag = false;
               }
            }
         }
      }
   }

   private static final class Entry {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final int intValue;
      private int count = 1;
      private boolean flag = true;

      private Entry(float floatValue, float y, float z, float x, float f, float z2, int intValue) {
         this.floatValue = floatValue;
         this.floatValue2 = y;
         this.floatValue3 = z;
         this.floatValue4 = x;
         this.floatValue5 = f;
         this.floatValue6 = z2;
         this.intValue = intValue;
      }
   }
}

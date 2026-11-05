package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;

public class HolyWorldEvent {
   private static final Pattern pattern = Pattern.compile("(\\([^)]+\\))?\\s*#(\\d+)");
   private final HolyWorldEvent.Type type;
   private final String text;
   private final long longValue;
   private final String text2;

   public HolyWorldEvent(String key, String text, long longValue) {
      this.type = HolyWorldEvent.Type.method7746(key);
      this.text = text;
      this.longValue = longValue;
      this.text2 = this.method7744();
   }

   public String method7742() {
      return this.getType().getText();
   }

   public String getIcon() {
      return this.getType().getIcon();
   }

   public boolean method7743() {
      Matcher matcher = pattern.matcher(this.text);

      while (matcher.find()) {
         String group = matcher.group(2);
         if (group != null && group.length() > 0) {
            try {
               int n = Integer.parseInt(group);
               if (n > 0) {
                  return true;
               }
            } catch (NumberFormatException e) {
               return true;
            }
         }
      }

      return false;
   }

   private String method7744() {
      ArrayList<HolyWorldEvent.Data> datas = new ArrayList();
      Matcher matcher = pattern.matcher(this.text);

      while (matcher.find()) {
         String group = matcher.group(1);
         String str = matcher.group(2);
         if (str != null && str.length() > 0) {
            try {
               int intValue = Integer.parseInt(str);
               if (intValue > 0) {
                  datas.add(new HolyWorldEvent.Data(intValue, group));
               }
            } catch (NumberFormatException e) {
            }
         }
      }

      if (datas.isEmpty()) {
         return "";
      } else {
         datas.sort(Comparator.comparingInt(data -> data.intValue));
         int size = datas.size();
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();
         ArrayList arrayList3 = new ArrayList();
         ArrayList arrayList4 = new ArrayList();

         for (HolyWorldEvent.Data data : datas) {
            if (data.intValue >= 1 && data.intValue <= 14) {
               arrayList.add(data);
            } else if (data.intValue >= 15 && data.intValue <= 32) {
               arrayList2.add(data);
            } else if (data.intValue >= 33 && data.intValue <= 47) {
               arrayList3.add(data);
            } else if (data.intValue >= 48) {
               arrayList4.add(data);
            }
         }

         ArrayList elements = new ArrayList();
         byte b = 4;
         if (size <= 4) {
            for (HolyWorldEvent.Data data : datas) {
               elements.add(this.method7745(data));
            }
         } else {
            ArrayList arrayList5 = new ArrayList();
            arrayList5.add(arrayList);
            arrayList5.add(arrayList2);
            arrayList5.add(arrayList3);
            arrayList5.add(arrayList4);
            int[] ints = new int[4];

            for (int i = 0; i < 4 && elements.size() < b; i++) {
               if (!((List)arrayList5.get(i)).isEmpty()) {
                  elements.add(this.method7745((HolyWorldEvent.Data)((List)arrayList5.get(i)).get(0)));
                  ints[i] = 1;
               }
            }

            for (int i = 0; i < 4 && elements.size() < b; i++) {
               while (ints[i] < ((List)arrayList5.get(i)).size() && elements.size() < b) {
                  elements.add(this.method7745((HolyWorldEvent.Data)((List)arrayList5.get(i)).get(ints[i])));
                  ints[i]++;
               }
            }
         }

         String str = String.join(", ", elements);
         if (size > 4) {
            str = str + "...";
         }

         return str;
      }
   }

   private String method7745(HolyWorldEvent.Data data) {
      return data.text != null && !data.text.isEmpty() ? data.intValue + " " + data.text : String.valueOf(data.intValue);
   }

   @Generated
   public HolyWorldEvent.Type getType() {
      return this.type;
   }

   @Generated
   public String getMessage() {
      return this.text;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   private static final class Data {
      private final int intValue;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + "]";
      }

      private Data(int intValue, String text) {
         this.intValue = intValue;
         this.text = text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String version() {
         return this.text;
      }
   }

   public static enum Type {
      CARGO("Груз", "C"),
      BOSS("Босс", "B"),
      CUBE("Кубик", "B"),
      CONTAINER("Контейнер", "D"),
      GOLD_RUSH("Золотая лихорадка", "E"),
      PACKAGE("Посылка", "F"),
      SHIP("Корабль", "G"),
      FLOWER("Цветочная поляна", "H"),
      FLOWER_FIELD("Цветочная поляна", "H"),
      MINE("Смертельная шахта", "I"),
      DEADLY_MINE("Смертельная шахта", "I"),
      EXPERT("Опытный Тыпо", "J"),
      VOTING("Голосование", "K"),
      UNKNOWN("Неизвестно", "A");

      private final String text;
      private final String text2;

      private Type(String text, String str) {
         this.text = text;
         this.text2 = str;
      }

      public static HolyWorldEvent.Type method7746(String key) {
         if (key == null) {
            return UNKNOWN;
         } else {
            String upperCase = key.toUpperCase(Locale.ROOT);
            switch (upperCase) {
               case "GOLDEN_FORTRESS":
                  return GOLD_RUSH;
               case "PARCELS":
                  return PACKAGE;
               case "SNOWQUARRY":
                  return FLOWER_FIELD;
               case "JAYCOB":
                  return EXPERT;
               default:
                  try {
                     return valueOf(upperCase);
                  } catch (IllegalArgumentException e) {
                     return UNKNOWN;
                  }
            }
         }
      }

      @Generated
      public String getText() {
         return this.text;
      }

      @Generated
      public String getIcon() {
         return this.text2;
      }
   }
}

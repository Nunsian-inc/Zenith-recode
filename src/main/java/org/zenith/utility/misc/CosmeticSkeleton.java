package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CosmeticSkeleton {
   private final CosmeticSkeleton.Data[] datas;
   private final String[] texts;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final int[] intValues;
   private final int[] intValues2;

   private CosmeticSkeleton(CosmeticSkeleton.Data[] datas, String[] texts, float[] floatValues, float[] floatValues2, float[] floatValues3, int[] intValues, int[] intValues2) {
      this.datas = datas;
      this.texts = texts;
      this.floatValues = floatValues;
      this.floatValues2 = floatValues2;
      this.floatValues3 = floatValues3;
      this.intValues = intValues;
      this.intValues2 = intValues2;
   }

   public CosmeticSkeleton.Data[] getDatas() {
      return this.datas;
   }

   public String[] getTexts() {
      return this.texts;
   }

   public int method7138() {
      return this.intValues.length;
   }

   public int method7139(int n) {
      return this.intValues[n];
   }

   public int method7140(int n) {
      return this.intValues2[n];
   }

   public float method7141(int n, int n2, int n3) {
      return this.floatValues[n * 12 + n2 * 3 + n3];
   }

   public float method7142(int n, int n2) {
      return this.floatValues2[n * 8 + n2 * 2];
   }

   public float method7143(int n, int n2) {
      return this.floatValues2[n * 8 + n2 * 2 + 1];
   }

   public float method7144(int n, int n2) {
      return this.floatValues3[n * 3 + n2];
   }

   public float[] method7145() {
      if (this.floatValues.length == 0) {
         return new float[6];
      } else {
         float[] floats = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

         for (byte i = 0; i < this.floatValues.length; i += 3) {
            for (int j = 0; j < 3; j++) {
               float floatValue = this.floatValues[i + j];
               floats[j] = Math.min(floats[j], floatValue);
               floats[j + 3] = Math.max(floats[j + 3], floatValue);
            }
         }

         return floats;
      }
   }

   public static final class Data {
      private final String text;
      private final int intValue;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float rotationX;
      private final float rotationY;
      private final float floatValue4;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float rotationX() {
         return this.rotationX;
      }

      public float rotationY() {
         return this.rotationY;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.rotationX, other.rotationX) && java.util.Objects.equals(this.rotationY, other.rotationY) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, floatValue, floatValue2, floatValue3, rotationX, rotationY, floatValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "rotationX=" + this.rotationX + ", " + "rotationY=" + this.rotationY + ", " + "floatValue4=" + this.floatValue4 + "]";
      }

      public Data(String text, int intValue, float floatValue, float f, float f2, float rotationX, float rotationY, float f3) {
         this.text = text;
         this.intValue = intValue;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.rotationX = rotationX;
         this.rotationY = rotationY;
         this.floatValue4 = f3;
      }

      public String name() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getRotationX() {
         return this.rotationX;
      }

      public float getRotationY() {
         return this.rotationY;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }
   }

   public static final class Entry {
      private final List<CosmeticSkeleton.Data> items = new ArrayList<>();
      private final List<String> items2 = new ArrayList<>();
      private final List<CosmeticSkeleton.Entry.Data> items3 = new ArrayList<>();

      public int method7146(String key, int intValue, float floatValue, float f, float f2, float rotationX, float rotationY, float f3) {
         this.items.add(new CosmeticSkeleton.Data(key, intValue, floatValue, f, f2, rotationX, rotationY, f3));
         return this.items.size() - 1;
      }

      public int method7147(String str) {
         int indexOf = this.items2.indexOf(str);
         if (indexOf >= 0) {
            return indexOf;
         } else {
            this.items2.add(str);
            return this.items2.size() - 1;
         }
      }

      public void method7148(int intValue, int n, float[] floatValues, float[] floatValues2, float[] floats) {
         this.items3.add(new CosmeticSkeleton.Entry.Data(intValue, n, floatValues, floatValues2, floats == null ? method7151(floatValues) : floats));
      }

      public int method7149() {
         return this.items.size();
      }

      public int method7150() {
         return this.items3.size();
      }

      private static float[] method7151(float[] floatValues) {
         float f = floatValues[3] - floatValues[0];
         float f2 = floatValues[4] - floatValues[1];
         float f3 = floatValues[5] - floatValues[2];
         float f4 = floatValues[6] - floatValues[0];
         float f5 = floatValues[7] - floatValues[1];
         float f6 = floatValues[8] - floatValues[2];
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f4 - f * f6;
         float f9 = f * f5 - f2 * f4;
         float f10 = (float)Math.sqrt(f7 * f7 + f8 * f8 + f9 * f9);
         return f10 < 1.0E-6F ? new float[]{0.0F, 1.0F, 0.0F} : new float[]{f7 / f10, f8 / f10, f9 / f10};
      }

      public CosmeticSkeleton method7152() {
         if (this.items.isEmpty()) {
            this.items.add(new CosmeticSkeleton.Data("root", -1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
         }

         if (this.items2.isEmpty()) {
            this.items2.add("");
         }

         this.items3.sort(Comparator.comparingInt(CosmeticSkeleton.Entry.Data::getIntValue2).thenComparingInt(CosmeticSkeleton.Entry.Data::getIntValue));
         int size = this.items3.size();
         float[] floatValues = new float[size * 12];
         float[] floatValues2 = new float[size * 8];
         float[] floatValues3 = new float[size * 3];
         int[] intValues = new int[size];
         int[] intValues2 = new int[size];

         for (int i = 0; i < size; i++) {
            CosmeticSkeleton.Entry.Data data = this.items3.get(i);
            System.arraycopy(data.floatValues, 0, floatValues, i * 12, 12);
            System.arraycopy(data.floatValues2, 0, floatValues2, i * 8, 8);
            System.arraycopy(data.floatValues3, 0, floatValues3, i * 3, 3);
            intValues[i] = data.intValue;
            intValues2[i] = data.intValue2;
         }

         return new CosmeticSkeleton(this.items.toArray(new CosmeticSkeleton.Data[0]), this.items2.toArray(new String[0]), floatValues, floatValues2, floatValues3, intValues, intValues2);
      }

      private static final class Data {
         private final int intValue;
         private final int intValue2;
         private final float[] floatValues;
         private final float[] floatValues2;
         private final float[] floatValues3;

         public int intValue() {
            return this.intValue;
         }

         public int intValue2() {
            return this.intValue2;
         }

         public float[] floatValues() {
            return this.floatValues;
         }

         public float[] floatValues2() {
            return this.floatValues2;
         }

         public float[] floatValues3() {
            return this.floatValues3;
         }

         @Override
         public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof org.zenith.utility.misc.CosmeticSkeleton.Entry.Data other)) return false;
            return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3);
         }

         @Override
         public int hashCode() {
            return java.util.Objects.hash(intValue, intValue2, floatValues, floatValues2, floatValues3);
         }

         @Override
         public String toString() {
            return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + "]";
         }

         private Data(int intValue, int n, float[] floatValues, float[] floatValues2, float[] floatValues3) {
            this.intValue = intValue;
            this.intValue2 = n;
            this.floatValues = floatValues;
            this.floatValues2 = floatValues2;
            this.floatValues3 = floatValues3;
         }

         public int getIntValue() {
            return this.intValue;
         }

         public int getIntValue2() {
            return this.intValue2;
         }

         public float[] getFloatValues() {
            return this.floatValues;
         }

         public float[] getFloatValues2() {
            return this.floatValues2;
         }

         public float[] getFloatValues3() {
            return this.floatValues3;
         }
      }
   }
}

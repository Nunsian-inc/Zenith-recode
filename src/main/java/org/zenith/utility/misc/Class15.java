package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;

public final class Class15 {
   public static final int CONST_intValue = 1;
   public static final int CONST_intValue2 = 2;
   public static final int CONST_intValue3 = 4;
   public static final int CONST_intValue4 = 8;
   public static final int CONST_intValue5 = 16;
   public static final int CONST_intValue6 = 32;
   public static final int CONST_intValue7 = 6;
   public static final int CONST_intValue8 = 1;
   public static final int CONST_intValue9 = 2;
   public static final int CONST_intValue10 = 4;
   public static final int CONST_intValue11 = 8;
   public static final int CONST_intValue12 = 16;
   public final String text;
   public final Identifier identifier;
   public final int intValue;
   public final int intValue2;
   public final String[] texts;
   public final int[] intValues;
   public final float[] floatValues;
   public final float[] floatValues2;
   public final int[] intValues2;
   public final int[] intValues3;
   public final Class15.Type2[] type2s;
   public final float[] floatValues3;
   public final int intValue3;
   public final float[] floatValues4;
   public final float[] floatValues5;
   public final float[] floatValues6;
   public final int[] intValues4;
   public final int[] intValues5;
   public final float[] floatValues7;
   public final Map<String, Class37> items;
   public final List<Class15.Data4> items2;
   public final Class15.Data3 data3;
   public final Class15.Data2 data2;
   public final Class15.Data data;
   public final float floatValue;
   public final int intValue4;
   public final List<Class15.Data4> items3;
   public final Map<Object, float[]> items4 = new HashMap<>();
   private final boolean[] flags = new boolean[7];
   private final int[] intValues6 = new int[7];
   private final int[] intValues7 = new int[7];
   private final int intValue5;

   public Class15(
      String text,
      Identifier identifier,
      int intValue,
      int n,
      String[] texts,
      int[] intValues,
      float[] floatValues,
      float[] floatValues2,
      int[] intValues2,
      int[] intValues3,
      Class15.Type2[] type2s,
      float[] floatValues3,
      float[] floatValues4,
      float[] floatValues5,
      float[] floatValues6,
      int[] intValues4,
      int[] intValues5,
      Map<String, Class37> items,
      List<Class15.Data4> data4s,
      Class15.Data3 data3,
      Class15.Data2 data2,
      Class15.Data data,
      float floatValue,
      float[] floatValues7,
      int n2
   ) {
      this.text = text;
      this.identifier = identifier;
      this.intValue = intValue;
      this.intValue2 = n;
      this.texts = texts;
      this.intValues = intValues;
      this.floatValues = floatValues;
      this.floatValues2 = floatValues2;
      this.intValues2 = intValues2;
      this.intValues3 = intValues3;
      this.type2s = type2s;
      this.floatValues3 = floatValues3;
      this.floatValues4 = floatValues4;
      this.floatValues5 = floatValues5;
      this.floatValues6 = floatValues6;
      this.intValues4 = intValues4;
      this.intValues5 = intValues5;
      this.intValue3 = intValues4.length;
      this.items = items;
      this.items2 = data4s;
      this.data3 = data3;
      this.data2 = data2;
      this.data = data;
      this.floatValue = floatValue;
      this.floatValues7 = floatValues7;
      this.intValue4 = n2;
      int n3 = 0;
      boolean flag = true;
      int n4 = -1;
      Arrays.fill(this.intValues6, -1);

      for (int i = 0; i < intValues4.length; i++) {
         int n5 = intValues3[intValues4[i]];
         this.flags[n5] = true;
         n3 |= intValues5[i];
         if (n5 < n4) {
            flag = false;
         }

         n4 = n5;
         if (this.intValues6[n5] < 0) {
            this.intValues6[n5] = i;
         }

         this.intValues7[n5] = i + 1;
      }

      for (int i = 0; i <= 6; i++) {
         if (!flag) {
            this.intValues6[i] = 0;
            this.intValues7[i] = intValues4.length;
         } else if (this.intValues6[i] < 0) {
            this.intValues6[i] = 0;
            this.intValues7[i] = 0;
         }
      }

      this.intValue5 = n3;
      var items2 = data == null ? null : data.getItems();
      if (items2 == null || items2.isEmpty()) {
         this.items3 = data4s;
      } else if (data4s.isEmpty()) {
         this.items3 = items2;
      } else {
         var data4s2 = new ArrayList<>(data4s);
         data4s2.addAll(items2);
         this.items3 = data4s2;
      }
   }

   public int method2028(int ordinal) {
      return ordinal >= 0 && ordinal < this.intValues6.length ? this.intValues6[ordinal] : 0;
   }

   public int method2029(int ordinal) {
      return ordinal >= 0 && ordinal < this.intValues7.length ? this.intValues7[ordinal] : this.intValue3;
   }

   public int method2030() {
      return this.texts.length;
   }

   public int method2031(String anObject) {
      for (int i = 0; i < this.texts.length; i++) {
         if (this.texts[i].equals(anObject)) {
            return i;
         }
      }

      return -1;
   }

   public Class37 method2032(String text) {
      return this.items.get(text);
   }

   public boolean method2033(int ordinal) {
      return ordinal >= 0 && ordinal < this.flags.length && this.flags[ordinal];
   }

   public boolean method2034(int n) {
      return (this.intValue5 & n) != 0;
   }

   public boolean method2035(Class15.Type type) {
      return this.method2033(type.ordinal());
   }

   public static final class Data {
      private final float[] floatValues;
      private final boolean flag;
      private final List<Class15.Data4> items;

      public float[] floatValues() {
         return this.floatValues;
      }

      public boolean flag() {
         return this.flag;
      }

      public List<Class15.Data4> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, flag, items);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValues=" + this.floatValues + ", " + "flag=" + this.flag + ", " + "items=" + this.items + "]";
      }

      public Data(float[] floatValues, boolean flag, List<Class15.Data4> items) {
         this.floatValues = floatValues;
         this.flag = flag;
         this.items = items;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<Class15.Data4> getItems() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final ParticleOptions particleEffect;
      private final ParticleOptions particleEffect2;
      private final int intValue;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public ParticleOptions particleEffect() {
         return this.particleEffect;
      }

      public ParticleOptions particleEffect2() {
         return this.particleEffect2;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.particleEffect, other.particleEffect) && java.util.Objects.equals(this.particleEffect2, other.particleEffect2) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, particleEffect, particleEffect2, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "particleEffect=" + this.particleEffect + ", " + "particleEffect2=" + this.particleEffect2 + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(float floatValue, float f, float f2, float f3, float f4, float f5, ParticleOptions particleEffect, ParticleOptions particleEffect2, int intValue) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.particleEffect = particleEffect;
         this.particleEffect2 = particleEffect2;
         this.intValue = intValue;
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

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public ParticleOptions getParticleEffect() {
         return this.particleEffect;
      }

      public ParticleOptions getParticleEffect2() {
         return this.particleEffect2;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data3 {
      private final Class15.Type4 type4;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final String text;
      private final ParticleOptions particleEffect;
      private final List<String> items;

      public Class15.Type4 type4() {
         return this.type4;
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

      public String text() {
         return this.text;
      }

      public ParticleOptions particleEffect() {
         return this.particleEffect;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.type4, other.type4) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.particleEffect, other.particleEffect) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type4, floatValue, floatValue2, floatValue3, text, particleEffect, items);
      }

      @Override
      public String toString() {
         return "Data3[" + "type4=" + this.type4 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "text=" + this.text + ", " + "particleEffect=" + this.particleEffect + ", " + "items=" + this.items + "]";
      }

      public Data3(Class15.Type4 type4, float floatValue, float f, float f2, String text, ParticleOptions particleEffect, List<String> items) {
         this.type4 = type4;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.text = text;
         this.particleEffect = particleEffect;
         this.items = items;
      }

      public boolean method2036(String str) {
         return this.items.contains(str);
      }

      public Class15.Type4 getType4() {
         return this.type4;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float hover() {
         return this.floatValue3;
      }

      public String getText() {
         return this.text;
      }

      public ParticleOptions getParticleEffect() {
         return this.particleEffect;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   public static final class Data4 {
      private final ParticleOptions particleEffect;
      private final int intValue;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final Class15.Type3 type3;

      public ParticleOptions particleEffect() {
         return this.particleEffect;
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

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public Class15.Type3 type3() {
         return this.type3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.particleEffect, other.particleEffect) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.type3, other.type3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(particleEffect, intValue, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, type3);
      }

      @Override
      public String toString() {
         return "Data4[" + "particleEffect=" + this.particleEffect + ", " + "intValue=" + this.intValue + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "type3=" + this.type3 + "]";
      }

      public Data4(
         ParticleOptions particleEffect, int intValue, float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7, Class15.Type3 type3
      ) {
         this.particleEffect = particleEffect;
         this.intValue = intValue;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.type3 = type3;
      }

      public ParticleOptions getParticleEffect() {
         return this.particleEffect;
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

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public Class15.Type3 getType3() {
         return this.type3;
      }
   }

   public static enum Type {
      HEAD,
      BODY,
      RIGHT_ARM,
      LEFT_ARM,
      RIGHT_LEG,
      LEFT_LEG;
   }

   public static enum Type2 {
      NONE,
      CAPE,
      LEGS_FRONT,
      LEGS_BACK,
      SPRING,
      SWAY,
      CLOTH,
      CLOCK;
   }

   public static enum Type3 {
      ALWAYS,
      MOVING,
      IDLE,
      AIRBORNE,
      SNEAKING,
      SET_BONUS;
   }

   public static enum Type4 {
      WALKER,
      HOPPER,
      FLYER,
      FLOATER,
      CRAWLER;
   }
}

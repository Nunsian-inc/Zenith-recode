package org.zenith.utility.misc;

import org.zenith.utility.api.CosmeticPartInfo;

public final class CosmeticTransformUtils {
   public static final float CONST_floatValue = 1.501F;
   public static final float CONST_floatValue2 = 16.0F;
   public static final float CONST_floatValue3 = 1.35F;

   public static float[] pivotFor(CosmeticType.Type type) {
      return switch (type) {
         case LEFT_ARM -> new float[]{0.3125F, 0.125F};
         case RIGHT_ARM -> new float[]{-0.3125F, 0.125F};
         case LEFT_LEG -> new float[]{0.11875F, 0.75F};
         case RIGHT_LEG -> new float[]{-0.11875F, 0.75F};
         default -> new float[]{0.0F, 0.0F};
      };
   }

   private static boolean usesBodyTransform(CosmeticType cosmeticType) {
      return cosmeticType != CosmeticType.PET && cosmeticType != CosmeticType.FLYING_PET ? cosmeticType.getType() != CosmeticType.Type.NONE : false;
   }

   private CosmeticTransformUtils() {
   }

   public static CosmeticTransformUtils.Data fromPartData(CosmeticPartInfo.Data data, CosmeticPartInfo.Type type, CosmeticType cosmeticType) {
      if (data == null) {
         return new CosmeticTransformUtils.Data(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, false);
      } else {
         boolean flag = type == CosmeticPartInfo.Type.BEDROCK;
         float f = flag ? 16.0F : 1.0F;
         float f2 = flag ? -1.0F : 1.0F;
         float f3 = flag ? Math.abs(data.getFloatValue8()) : data.getFloatValue8();
         boolean flag2 = usesBodyTransform(cosmeticType);
         return new CosmeticTransformUtils.Data(
            data.getFloatValue(),
            data.getFloatValue2(),
            data.getFloatValue3(),
            data.getFloatValue4(),
            data.getFloatValue5(),
            data.getFloatValue6(),
            data.getFloatValue7() * f,
            f3 * f * f2,
            data.getFloatValue9() * f * f2,
            flag2
         );
      }
   }

   public static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final boolean flag;

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

      public float floatValue9() {
         return this.floatValue9;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "flag=" + this.flag + "]";
      }

      public Data(float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, boolean flag) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
         this.flag = flag;
      }

      public boolean hasZeroScale() {
         return this.floatValue7 == 0.0F || this.floatValue8 == 0.0F || this.floatValue9 == 0.0F;
      }

      public boolean hasRotation() {
         return this.floatValue4 != 0.0F || this.floatValue5 != 0.0F || this.floatValue6 != 0.0F;
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

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

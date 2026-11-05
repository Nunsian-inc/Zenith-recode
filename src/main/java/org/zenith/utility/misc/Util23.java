package org.zenith.utility.misc;

public final class Util23 {
   private static final float[] CONST_floatValues = new float[]{-0.25F, -0.5F, -0.25F, 0.25F, 0.0F, 0.25F};
   private static final float[] CONST_floatValues2 = new float[]{-0.25F, 0.0F, -0.125F, 0.25F, 0.75F, 0.125F};
   private static final float[] CONST_floatValues3 = new float[]{0.25F, 0.0F, -0.125F, 0.5F, 0.75F, 0.125F};
   private static final float[] CONST_floatValues4 = new float[]{-0.5F, 0.0F, -0.125F, -0.25F, 0.75F, 0.125F};
   private static final float[] CONST_floatValues5 = new float[]{-0.00625F, 0.75F, -0.125F, 0.24375F, 1.5F, 0.125F};
   private static final float[] CONST_floatValues6 = new float[]{-0.24375F, 0.75F, -0.125F, 0.00625F, 1.5F, 0.125F};
   private static final float[] CONST_floatValues7 = new float[]{-0.3F, -0.5F, -0.3F, 0.3F, 1.5F, 0.3F};

   private Util23() {
   }

   public static float[] method5987(CosmeticType cosmeticType) {
      return switch (cosmeticType) {
         case HAT, HEAD -> CONST_floatValues;
         case BODY, FULL, WINGS, TAIL -> CONST_floatValues2;
         case LEFT_ARM -> CONST_floatValues3;
         case RIGHT_ARM -> CONST_floatValues4;
         case LEFT_LEG -> CONST_floatValues5;
         case RIGHT_LEG -> CONST_floatValues6;
         case MOUNT, INDEPENDENT -> CONST_floatValues7;
         default -> null;
      };
   }

   public static float method5988(CosmeticType cosmeticType) {
      return switch (cosmeticType) {
         case HAT, FULL -> 0.6F;
         case HEAD, BODY -> 0.5F;
         case WINGS -> 0.9F;
         case TAIL -> 0.7F;
         default -> 0.35F;
         case MOUNT, INDEPENDENT -> 1.5F;
      };
   }

   private static float method5989(CosmeticType cosmeticType) {
      return switch (cosmeticType) {
         case HAT, HEAD -> 2.5F;
         case BODY, FULL -> 3.0F;
         case WINGS -> 7.0F;
         case TAIL -> 4.0F;
         default -> 2.0F;
         case MOUNT, INDEPENDENT -> 5.0F;
      };
   }

   public static Util31.Data method5990(Util31.Data data, CosmeticType cosmeticType, float[] floats) {
      float[] floats2 = method5987(cosmeticType);
      if (floats2 != null && data != null && !data.method7853() && floats != null && floats.length >= 6) {
         float[] floats3 = Util31.method7850(cosmeticType.getType());
         float[] floats4 = method5991(data, floats, floats3);
         if (floats4 == null) {
            return data;
         } else {
            float f = floats3[0] + data.getFloatValue();
            float f2 = floats3[1] + data.getFloatValue2();
            float f3 = data.getFloatValue3();
            float f4 = Math.max(floats4[3] - floats4[0], Math.max(floats4[4] - floats4[1], floats4[5] - floats4[2]));
            float f5 = method5989(cosmeticType);
            float f6 = f4 > f5 && f4 > 1.0E-4F ? f5 / f4 : 1.0F;
            if (f6 < 1.0F) {
               for (int i = 0; i < 3; i++) {
                  float f7 = i == 0 ? f : (i == 1 ? f2 : f3);
                  floats4[i] = f7 + (floats4[i] - f7) * f6;
                  floats4[i + 3] = f7 + (floats4[i + 3] - f7) * f6;
               }
            }

            float f7 = method5988(cosmeticType);
            float[] floats5 = new float[3];

            for (int i = 0; i < 3; i++) {
               float f8 = floats2[i] - f7;
               float f9 = floats2[i + 3] + f7;
               if (floats4[i] > f9) {
                  floats5[i] = f9 - floats4[i];
               } else if (floats4[i + 3] < f8) {
                  floats5[i] = f8 - floats4[i + 3];
               }
            }

            return f6 == 1.0F && floats5[0] == 0.0F && floats5[1] == 0.0F && floats5[2] == 0.0F
               ? data
               : new Util31.Data(
                  data.getFloatValue() + floats5[0],
                  data.getFloatValue2() + floats5[1],
                  data.getFloatValue3() + floats5[2],
                  data.getFloatValue4(),
                  data.getFloatValue5(),
                  data.getFloatValue6(),
                  data.getFloatValue7() * f6,
                  data.getFloatValue8() * f6,
                  data.getFloatValue9() * f6,
                  data.isFlag()
               );
         }
      } else {
         return data;
      }
   }

   private static float[] method5991(Util31.Data data, float[] floats, float[] floats2) {
      double d = Math.toRadians(data.getFloatValue4());
      double d2 = Math.toRadians(data.getFloatValue5());
      double d3 = Math.toRadians(data.getFloatValue6());
      float f = (float)Math.sin(d);
      float f2 = (float)Math.cos(d);
      float f3 = (float)Math.sin(d2);
      float f4 = (float)Math.cos(d2);
      float f5 = (float)Math.sin(d3);
      float f6 = (float)Math.cos(d3);
      float f7 = floats2[0] + (data.isFlag() ? data.getFloatValue() : 0.0F);
      float f8 = floats2[1] + (data.isFlag() ? data.getFloatValue2() : 0.0F);
      float f9 = data.isFlag() ? data.getFloatValue3() : 0.0F;
      float[] floats3 = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

      for (int i = 0; i < 8; i++) {
         float f10 = floats[(i & 1) == 0 ? 0 : 3] * data.getFloatValue7();
         float f11 = floats[(i & 2) == 0 ? 1 : 4] * data.getFloatValue8();
         float f12 = floats[(i & 4) == 0 ? 2 : 5] * data.getFloatValue9();
         float f13 = f11 * f2 - f12 * f;
         float f14 = f11 * f + f12 * f2;
         float f15 = f10 * f4 + f14 * f3;
         float f16 = -f10 * f3 + f14 * f4;
         float f17 = f15 * f6 - f13 * f5;
         float f18 = f15 * f5 + f13 * f6;
         floats3[0] = Math.min(floats3[0], f7 + f17);
         floats3[1] = Math.min(floats3[1], f8 + f18);
         floats3[2] = Math.min(floats3[2], f9 + f16);
         floats3[3] = Math.max(floats3[3], f7 + f17);
         floats3[4] = Math.max(floats3[4], f8 + f18);
         floats3[5] = Math.max(floats3[5], f9 + f16);
      }

      return Float.isFinite(floats3[0]) && Float.isFinite(floats3[3]) ? floats3 : null;
   }
}

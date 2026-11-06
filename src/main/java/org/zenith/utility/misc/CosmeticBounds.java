package org.zenith.utility.misc;

public final class CosmeticBounds {
   private static final float[] HEAD_BOUNDS = new float[]{-0.25F, -0.5F, -0.25F, 0.25F, 0.0F, 0.25F};
   private static final float[] BODY_BOUNDS = new float[]{-0.25F, 0.0F, -0.125F, 0.25F, 0.75F, 0.125F};
   private static final float[] LEFT_ARM_BOUNDS = new float[]{0.25F, 0.0F, -0.125F, 0.5F, 0.75F, 0.125F};
   private static final float[] RIGHT_ARM_BOUNDS = new float[]{-0.5F, 0.0F, -0.125F, -0.25F, 0.75F, 0.125F};
   private static final float[] LEFT_LEG_BOUNDS = new float[]{-0.00625F, 0.75F, -0.125F, 0.24375F, 1.5F, 0.125F};
   private static final float[] RIGHT_LEG_BOUNDS = new float[]{-0.24375F, 0.75F, -0.125F, 0.00625F, 1.5F, 0.125F};
   private static final float[] MOUNT_BOUNDS = new float[]{-0.3F, -0.5F, -0.3F, 0.3F, 1.5F, 0.3F};

   private CosmeticBounds() {
   }

   public static float[] boundsFor(CosmeticType cosmeticType) {
      return switch (cosmeticType) {
         case HAT, HEAD -> HEAD_BOUNDS;
         case BODY, FULL, WINGS, TAIL -> BODY_BOUNDS;
         case LEFT_ARM -> LEFT_ARM_BOUNDS;
         case RIGHT_ARM -> RIGHT_ARM_BOUNDS;
         case LEFT_LEG -> LEFT_LEG_BOUNDS;
         case RIGHT_LEG -> RIGHT_LEG_BOUNDS;
         case MOUNT, INDEPENDENT -> MOUNT_BOUNDS;
         default -> null;
      };
   }

   public static float boundsPadding(CosmeticType cosmeticType) {
      return switch (cosmeticType) {
         case HAT, FULL -> 0.6F;
         case HEAD, BODY -> 0.5F;
         case WINGS -> 0.9F;
         case TAIL -> 0.7F;
         default -> 0.35F;
         case MOUNT, INDEPENDENT -> 1.5F;
      };
   }

   private static float maxDimension(CosmeticType cosmeticType) {
      return switch (cosmeticType) {
         case HAT, HEAD -> 2.5F;
         case BODY, FULL -> 3.0F;
         case WINGS -> 7.0F;
         case TAIL -> 4.0F;
         default -> 2.0F;
         case MOUNT, INDEPENDENT -> 5.0F;
      };
   }

   public static CosmeticTransformUtils.Data fitWithinBounds(CosmeticTransformUtils.Data data, CosmeticType cosmeticType, float[] floats) {
      float[] floats2 = boundsFor(cosmeticType);
      if (floats2 != null && data != null && !data.hasZeroScale() && floats != null && floats.length >= 6) {
         float[] floats3 = CosmeticTransformUtils.pivotFor(cosmeticType.getType());
         float[] floats4 = transformedBounds(data, floats, floats3);
         if (floats4 == null) {
            return data;
         } else {
            float f = floats3[0] + data.getFloatValue();
            float f2 = floats3[1] + data.getFloatValue2();
            float f3 = data.getFloatValue3();
            float f4 = Math.max(floats4[3] - floats4[0], Math.max(floats4[4] - floats4[1], floats4[5] - floats4[2]));
            float f5 = maxDimension(cosmeticType);
            float f6 = f4 > f5 && f4 > 1.0E-4F ? f5 / f4 : 1.0F;
            if (f6 < 1.0F) {
               for (int i = 0; i < 3; i++) {
                  float f7 = i == 0 ? f : (i == 1 ? f2 : f3);
                  floats4[i] = f7 + (floats4[i] - f7) * f6;
                  floats4[i + 3] = f7 + (floats4[i + 3] - f7) * f6;
               }
            }

            float f7 = boundsPadding(cosmeticType);
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
               : new CosmeticTransformUtils.Data(
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

   private static float[] transformedBounds(CosmeticTransformUtils.Data data, float[] floats, float[] floats2) {
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

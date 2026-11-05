package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;

public final class Util3 {
   private static final float CONST_floatValue = 0.0625F;
   private static final int CONST_intValue = 6;
   private static final int CONST_intValue2 = Class15.Type.HEAD.ordinal();
   private static final int CONST_intValue3 = Class15.Type.BODY.ordinal();
   private static final int CONST_intValue4 = Class15.Type.RIGHT_ARM.ordinal();
   private static final int CONST_intValue5 = Class15.Type.LEFT_ARM.ordinal();
   private static final int CONST_intValue6 = Class15.Type.RIGHT_LEG.ordinal();
   private static final int CONST_intValue7 = Class15.Type.LEFT_LEG.ordinal();
   private static final float[] CONST_floatValues = new float[]{0.0F, 0.0F, -0.0625F, 0.0625F, 0.0F, 0.0F};
   private static final float[] CONST_floatValues2 = new float[]{0.25F, 0.25F, 0.125F, 0.125F, 0.125F, 0.125F};
   private static final float[] CONST_floatValues3 = new float[]{0.25F, 0.125F, 0.125F, 0.125F, 0.125F, 0.125F};
   private static final float[] CONST_floatValues4 = new float[]{-0.5F, 0.0F, -0.125F, -0.125F, 0.0F, 0.0F};
   private static final float[] CONST_floatValues5 = new float[]{0.0F, 0.75F, 0.625F, 0.625F, 0.75F, 0.75F};
   private static final float CONST_floatValue2 = 0.09375F;
   private static final float[] CONST_floatValues6 = new float[]{0.0F, 0.0F, -0.03125F, 0.03125F, 0.0F, 0.0F};
   private static final float CONST_floatValue3 = 0.03125F;
   private static final float CONST_floatValue4 = 0.015625F;
   private static final float CONST_floatValue5 = 0.0625F;
   private static final float CONST_floatValue6 = 0.0625F;
   private static final float CONST_floatValue7 = 0.03125F;
   private static final float CONST_floatValue8 = 0.025F;
   private static final float CONST_floatValue9 = 0.05625F;
   private static final float CONST_floatValue10 = 0.0125F;
   private static final float CONST_floatValue11 = 0.00625F;
   private static final float CONST_floatValue12 = 1.9F;
   private static final float CONST_floatValue13 = 1.25F;
   private static final float CONST_floatValue14 = 1.15F;
   private static final float CONST_floatValue15 = 1.35F;
   private static final float CONST_floatValue16 = 1.6F;
   private static final float CONST_floatValue17 = 0.5F;
   private static final float CONST_floatValue18 = 2.5F;
   private static final float CONST_floatValue19 = 0.1875F;
   private static final float CONST_floatValue20 = 0.85F;
   private static final int CONST_intValue8 = 5;
   private static final float[] CONST_floatValues7 = new float[0];
   private static final int CONST_intValue9 = 512;
   private static final Map<Util3.Entry2, Util3.Data[]> items = new LinkedHashMap<Util3.Entry2, Util3.Data[]>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(java.util.Map.Entry<Util3.Entry2, Util3.Data[]> eldest) {
         return this.size() > 512;
      }
   };
   private static final Object obj = new Object();
   private static final Object obj2 = new Object();

   private Util3() {
   }

   public static int method824(FocusSlotEnum focusSlotEnum) {
      return switch (focusSlotEnum) {
         case CHARACTER -> 0;
         case LEGS -> 1;
         case BODY -> 2;
         case HEAD -> 3;
         case BACK, CAPE -> 4;
         case WATCH, NECK -> 5;
         default -> -1;
      };
   }

   private static boolean method825(int ordinal) {
      return ordinal == CONST_intValue4 || ordinal == CONST_intValue5;
   }

   public static Util3.Entry method826(AvatarRenderState state, Wardrobe wardrobe, List<HoveredData> hoveredDatas, HoveredData hoveredData) {
      boolean flag = state.skin != null && state.skin.model() == PlayerModelType.SLIM;
      Util3.Entry entry = Util3.Entry.method843(wardrobe, hoveredDatas, hoveredData, flag);
      if (wardrobe.clothesOverArmor.isEnabled() && !WardrobeRenderer.method6688(state)) {
         method828(state, entry.floatValues);
      }

      if (hoveredData == null) {
         entry.floatValues2[CONST_intValue2] = state.showHat ? 0.03125F : 0.0F;
         entry.floatValues2[CONST_intValue3] = state.showJacket ? 0.015625F : 0.0F;
         entry.floatValues2[CONST_intValue4] = state.showRightSleeve ? 0.015625F : 0.0F;
         entry.floatValues2[CONST_intValue5] = state.showLeftSleeve ? 0.015625F : 0.0F;
         entry.floatValues2[CONST_intValue6] = state.showRightPants ? 0.015625F : 0.0F;
         entry.floatValues2[CONST_intValue7] = state.showLeftPants ? 0.015625F : 0.0F;
      }

      entry.method844();
      return entry;
   }

   public static Util3.Entry method827(Wardrobe wardrobe, List<HoveredData> hoveredDatas, HoveredData hoveredData, boolean flag, int ordinal, boolean flag2) {
      Util3.Entry entry = Util3.Entry.method843(wardrobe, hoveredDatas, hoveredData, flag);
      if (hoveredData == null && flag2 && method825(ordinal)) {
         boolean flag3 = false;

         for (Class15 class2 : entry.class15s) {
            flag3 |= (class2.intValue4 & 4) != 0;
         }

         entry.floatValues2[ordinal] = flag3 ? 0.0F : 0.015625F;
      }

      entry.method844();
      return entry;
   }

   private static void method828(AvatarRenderState state, float[] floatValues) {
      boolean shouldRender = HumanoidArmorLayer.shouldRender(state.headEquipment, EquipmentSlot.HEAD);
      boolean flag = HumanoidArmorLayer.shouldRender(state.chestEquipment, EquipmentSlot.CHEST)
         && !state.chestEquipment.has(DataComponents.GLIDER);
      boolean shouldRender2 = HumanoidArmorLayer.shouldRender(state.legsEquipment, EquipmentSlot.LEGS);
      boolean shouldRender3 = HumanoidArmorLayer.shouldRender(state.feetEquipment, EquipmentSlot.FEET);
      floatValues[CONST_intValue2] = shouldRender ? 0.0625F : 0.0F;
      floatValues[CONST_intValue3] = flag ? 0.0625F : (shouldRender2 ? 0.03125F : 0.0F);
      floatValues[CONST_intValue4] = flag ? 0.0625F : 0.0F;
      floatValues[CONST_intValue5] = floatValues[CONST_intValue4];
      floatValues[CONST_intValue6] = shouldRender3 ? 0.05625F : (shouldRender2 ? 0.025F : 0.0F);
      floatValues[CONST_intValue7] = floatValues[CONST_intValue6];
   }

   private static float[] method829(int ordinal, int n) {
      float f = ordinal == CONST_intValue2 ? 0.0625F : 0.0F;
      float f2 = n == 1 ? CONST_floatValues[ordinal] - CONST_floatValues2[ordinal] : CONST_floatValues4[ordinal] - f;
      float f3 = n == 1 ? CONST_floatValues[ordinal] + CONST_floatValues2[ordinal] : CONST_floatValues5[ordinal];
      float f4 = n == 2 ? CONST_floatValues[ordinal] - CONST_floatValues2[ordinal] : -CONST_floatValues3[ordinal];
      float f5 = n == 2 ? CONST_floatValues[ordinal] + CONST_floatValues2[ordinal] : CONST_floatValues3[ordinal];
      return new float[]{f2, f3, f4, f5};
   }

   private static float method830(int ordinal, int n) {
      float f = n == 0 ? CONST_floatValues2[ordinal] : (n == 2 ? CONST_floatValues3[ordinal] : -CONST_floatValues4[ordinal]);
      return f * 0.85F;
   }

   public static float method831(Class15 class2, int ordinal, int n, float floatValue) {
      Util3.Data3 data3 = new Util3.Data3(ordinal, n, floatValue);
      float[] floats = class2.items4.get(data3);
      if (floats != null) {
         return floats == CONST_floatValues7 ? Float.NaN : floats[0];
      } else {
         float[] floats2 = method829(ordinal, n);
         int n2 = n == 1 ? 0 : 1;
         int n3 = n == 2 ? 0 : 2;
         float f = n == 0 ? CONST_floatValues[ordinal] : 0.0F;
         float f2 = method830(ordinal, n);
         float[] floats3 = new float[Math.max(0, class2.method2029(ordinal) - class2.method2028(ordinal)) * 5];
         int n4 = method837(class2, ordinal, n, floatValue, f, n2, n3, false, floats3);
         float[] floats4 = new float[]{Float.MAX_VALUE};
         method833(floats3, n4, floats3, 0, floats2, (var2x, var3x) -> {
            if (var2x >= f2 && var2x < floats4[0]) {
               floats4[0] = var2x;
            }
         });
         float[] floats5 = floats4[0] < Float.MAX_VALUE ? new float[]{floats4[0]} : CONST_floatValues7;
         class2.items4.put(data3, floats5);
         return floats5 == CONST_floatValues7 ? Float.NaN : floats4[0];
      }
   }

   private static float[] method832(Class15 class2, Class15 class3, int ordinal, int n, float floatValue, float f) {
      Util3.Data2 data2 = new Util3.Data2(class2, ordinal, n, floatValue, f);
      float[] floats = class3.items4.get(data2);
      if (floats != null) {
         return floats == CONST_floatValues7 ? null : floats;
      } else {
         float[] floats2 = method829(ordinal, n);
         int n2 = n == 1 ? 0 : 1;
         int n3 = n == 2 ? 0 : 2;
         float f2 = n == 0 ? CONST_floatValues[ordinal] : 0.0F;
         float f3 = method830(ordinal, n);
         float[] floats3 = new float[Math.max(0, class3.method2029(ordinal) - class3.method2028(ordinal)) * 5];
         float[] floats4 = new float[Math.max(0, class2.method2029(ordinal) - class2.method2028(ordinal)) * 5];
         int n4 = method837(class3, ordinal, n, floatValue, f2, n2, n3, false, floats3);
         int n5 = method837(class2, ordinal, n, floatValue, f2, n2, n3, true, floats4);
         Util3.Entry3 entry3 = new Util3.Entry3();
         if (n5 > 0) {
            method833(floats3, n4, floats4, n5, floats2, (var3x, var4x) -> {
               if (var3x >= f3 && var4x >= f3 && var4x <= var3x * f) {
                  entry3.method851(var3x, var4x);
               }
            });
         }

         float[] floats5 = entry3.method852();
         class3.items4.put(data2, floats5);
         return floats5 == CONST_floatValues7 ? null : floats5;
      }
   }

   private static void method833(float[] floats, int n, float[] floats2, int n2, float[] floats3, Util3.Handler handler) {
      int[] ints = new int[n];
      int[] ints2 = new int[n2];
      float[] floats4 = new float[(n + n2) * 2 + 2];
      float[] floats5 = new float[floats4.length];

      for (int i = 0; i < n; i++) {
         int n3 = i * 5;
         float f = Math.max(floats3[0], floats[n3 + 1]);
         float f2 = Math.min(floats3[1], floats[n3 + 2]);
         float f3 = Math.max(floats3[2], floats[n3 + 3]);
         float f4 = Math.min(floats3[3], floats[n3 + 4]);
         if (!(f >= f2) && !(f3 >= f4)) {
            int n4 = method834(floats, n, f, f2, f3, f4, ints);
            int n5 = method834(floats2, n2, f, f2, f3, f4, ints2);
            int n6 = method835(floats4, f, f2, 1, floats, ints, n4, floats2, ints2, n5);
            int n7 = method835(floats5, f3, f4, 3, floats, ints, n4, floats2, ints2, n5);

            for (int j = 0; j + 1 < n6; j++) {
               float f5 = (floats4[j] + floats4[j + 1]) * 0.5F;

               for (int k = 0; k + 1 < n7; k++) {
                  float f6 = (floats5[k] + floats5[k + 1]) * 0.5F;
                  handler.accept(method838(floats, ints, n4, f5, f6), method838(floats2, ints2, n5, f5, f6));
               }
            }
         }
      }
   }

   private static int method834(float[] floats, int n, float f, float f2, float f3, float f4, int[] ints) {
      int n2 = 0;

      for (int i = 0; i < n; i++) {
         int n3 = i * 5;
         if (floats[n3 + 1] < f2 && floats[n3 + 2] > f && floats[n3 + 3] < f4 && floats[n3 + 4] > f3) {
            ints[n2++] = i;
         }
      }

      return n2;
   }

   private static int method835(float[] floats, float f, float f2, int n, float[] floats2, int[] ints, int n2, float[] floats3, int[] ints2, int n3) {
      int toIndex = 0;
      floats[toIndex++] = f;
      floats[toIndex++] = f2;

      for (int i = 0; i < n2; i++) {
         toIndex = method836(floats, toIndex, f, f2, floats2[ints[i] * 5 + n]);
         toIndex = method836(floats, toIndex, f, f2, floats2[ints[i] * 5 + n + 1]);
      }

      for (int i = 0; i < n3; i++) {
         toIndex = method836(floats, toIndex, f, f2, floats3[ints2[i] * 5 + n]);
         toIndex = method836(floats, toIndex, f, f2, floats3[ints2[i] * 5 + n + 1]);
      }

      Arrays.sort(floats, 0, toIndex);
      int n4 = 1;

      for (int i = 1; i < toIndex; i++) {
         if (floats[i] - floats[n4 - 1] > 1.0E-6F) {
            floats[n4++] = floats[i];
         }
      }

      return n4;
   }

   private static int method836(float[] floats, int toIndex, float f, float f2, float f3) {
      if (f3 > f && f3 < f2) {
         floats[toIndex++] = f3;
      }

      return toIndex;
   }

   private static int method837(Class15 class2, int ordinal, int n, float floatValue, float f, int n2, int n3, boolean flag, float[] floats) {
      float[] floats2 = method841(class2, obj);
      float[] floats3 = method841(class2, obj2);
      boolean flag2 = flag && method840(class2, ordinal);
      int n4 = 0;
      int n5 = class2.method2028(ordinal);

      for (int i = class2.method2029(ordinal); n5 < i; n5++) {
         int n6 = class2.intValues4[n5];
         if (class2.intValues3[n6] == ordinal
            && (class2.intValues5[n5] & 4) == 0
            && (!flag2 || class2.intValues[n6] < 0)
            && !(floats3[n5 * 3 + n] * floatValue < 0.9F)) {
            float f2 = 0.0F;
            float f3 = Float.MAX_VALUE;
            float f4 = -Float.MAX_VALUE;
            float f5 = Float.MAX_VALUE;
            float f6 = -Float.MAX_VALUE;

            for (int j = 0; j < 4; j++) {
               int n7 = n5 * 12 + j * 3;
               f2 += floats2[n7 + n];
               float f7 = floats2[n7 + n2];
               float f8 = floats2[n7 + n3];
               f3 = Math.min(f3, f7);
               f4 = Math.max(f4, f7);
               f5 = Math.min(f5, f8);
               f6 = Math.max(f6, f8);
            }

            int n7 = n4++ * 5;
            floats[n7] = floatValue * (f2 / 4.0F - f);
            floats[n7 + 1] = f3;
            floats[n7 + 2] = f4;
            floats[n7 + 3] = f5;
            floats[n7 + 4] = f6;
         }
      }

      return n4;
   }

   private static float method838(float[] floats, int[] ints, int n, float f, float f2) {
      float f3 = -Float.MAX_VALUE;

      for (int i = 0; i < n; i++) {
         int n2 = ints[i] * 5;
         if (floats[n2] > f3 && f >= floats[n2 + 1] && f <= floats[n2 + 2] && f2 >= floats[n2 + 3] && f2 <= floats[n2 + 4]) {
            f3 = floats[n2];
         }
      }

      return f3;
   }

   private static float method839(float[] floats, int n, float f, float f2) {
      float f3 = -Float.MAX_VALUE;

      for (int i = 0; i < n; i++) {
         int n2 = i * 5;
         if (floats[n2] > f3 && f >= floats[n2 + 1] && f <= floats[n2 + 2] && f2 >= floats[n2 + 3] && f2 <= floats[n2 + 4]) {
            f3 = floats[n2];
         }
      }

      return f3;
   }

   private static boolean method840(Class15 class2, int ordinal) {
      for (int i = 0; i < class2.method2030(); i++) {
         if (class2.intValues[i] < 0 && class2.intValues2[i] == ordinal) {
            return true;
         }
      }

      return false;
   }

   private static float[] method841(Class15 key, Object obj3) {
      float[] floats = key.items4.get(obj3);
      if (floats != null) {
         return floats;
      } else {
         int n = key.method2030();
         Matrix4f[] matrixes = new Matrix4f[n];

         for (int i = 0; i < n; i++) {
            matrixes[i] = new Matrix4f();
            BoneMatrices.method4808(key, null, i, matrixes[i]);
         }

         float[] floats2 = new float[key.intValue3 * 12];
         float[] floats3 = new float[key.intValue3 * 3];
         Vector3f vec = new Vector3f();

         for (int i = 0; i < key.intValue3; i++) {
            Matrix4f matrix = matrixes[key.intValues4[i]];

            for (int j = 0; j < 4; j++) {
               int n2 = i * 12 + j * 3;
               vec.set(key.floatValues4[n2], key.floatValues4[n2 + 1], key.floatValues4[n2 + 2]);
               matrix.transformPosition(vec);
               floats2[n2] = vec.x;
               floats2[n2 + 1] = vec.y;
               floats2[n2 + 2] = vec.z;
            }

            vec.set(key.floatValues6[i * 3], key.floatValues6[i * 3 + 1], key.floatValues6[i * 3 + 2]);
            matrix.transformDirection(vec);
            if (vec.lengthSquared() > 1.0E-8F) {
               vec.normalize();
            }

            floats3[i * 3] = vec.x;
            floats3[i * 3 + 1] = vec.y;
            floats3[i * 3 + 2] = vec.z;
         }

         key.items4.put(obj, floats2);
         key.items4.put(obj2, floats3);
         return obj3 == obj ? floats2 : floats3;
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

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + "]";
      }
      public static final Util3.Data INSTANCE = new Util3.Data(0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);

      public Data(float floatValue, float f, float f2, float f3, float f4, float f5, float f6) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
      }

      public void method842(PoseStack poseStack) {
         if (this != INSTANCE) {
            poseStack.translate(this.floatValue2 + this.floatValue4, 0.0F, this.floatValue7);
            poseStack.scale(this.floatValue3, this.floatValue5, this.floatValue6);
            poseStack.translate(-this.floatValue, 0.0F, 0.0F);
         }
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
   }

   private static final class Data2 {
      private final Class15 class15;
      private final int intValue;
      private final int intValue2;
      private final float floatValue;
      private final float floatValue2;

      public Class15 class15() {
         return this.class15;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(class15, intValue, intValue2, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "class15=" + this.class15 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data2(Class15 class2, int intValue, int n, float floatValue, float f) {
         this.class15 = class2;
         this.intValue = intValue;
         this.intValue2 = n;
         this.floatValue = floatValue;
         this.floatValue2 = f;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data3 {
      private final int intValue;
      private final int intValue2;
      private final float floatValue;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, floatValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data3(int intValue, int n, float floatValue) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.floatValue = floatValue;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }

   public static final class Entry {
      private final HoveredData[] hoveredDatas;
      private final Class15[] class15s;
      private final int[] intValues;
      private final boolean flag;
      private final float[] floatValues = new float[6];
      private final float[] floatValues2 = new float[6];
      private Util3.Data[] datas;

      public Entry(List<HoveredData> hoveredDatas, List<Class15> classes, boolean flag) {
         int size = hoveredDatas.size();
         this.hoveredDatas = hoveredDatas.toArray(new HoveredData[0]);
         this.class15s = classes.toArray(new Class15[0]);
         this.intValues = new int[size];
         boolean flag2 = false;

         for (int i = 0; i < size; i++) {
            this.intValues[i] = Util3.method824(this.hoveredDatas[i].getFocusSlotEnum());
            flag2 |= this.hoveredDatas[i].getFocusSlotEnum() == FocusSlotEnum.CHARACTER;
         }

         this.flag = flag && !flag2;
         this.datas = new Util3.Data[size * 6];
      }

      private static Util3.Entry method843(Wardrobe wardrobe, List<HoveredData> hoveredDatas, HoveredData hoveredData, boolean flag) {
         List<HoveredData> hoveredDatas2 = new ArrayList<>(hoveredDatas.size());
         List<Class15> classes = new ArrayList<>(hoveredDatas.size());

         for (HoveredData hoveredData2 : hoveredDatas) {
            if (Util3.method824(hoveredData2.getFocusSlotEnum()) >= 0 && (hoveredData == null || hoveredData2 == hoveredData || hoveredData2.method1546() == WardrobeCategory.ACCESSORY)) {
               Class15 class2 = wardrobe.getRegistry().method1427(hoveredData2);
               if (class2 != null) {
                  hoveredDatas2.add(hoveredData2);
                  classes.add(class2);
               }
            }
         }

         return new Util3.Entry(hoveredDatas2, classes, flag);
      }

      private void method844() {
         Util3.Entry2 entry2 = new Util3.Entry2(this);
         Util3.Data[] datas = Util3.items.get(entry2);
         if (datas == null) {
            Util3.items.put(entry2, this.datas);
         } else {
            this.datas = datas;
         }
      }

      public Util3.Data method845(HoveredData hoveredData, int ordinal) {
         for (int i = 0; i < this.hoveredDatas.length; i++) {
            if (this.hoveredDatas[i] == hoveredData) {
               return this.method847(i, ordinal);
            }
         }

         return Util3.Data.INSTANCE;
      }

      public void method846(PoseStack poseStack, HoveredData hoveredData, int ordinal) {
         this.method845(hoveredData, ordinal).method842(poseStack);
      }

      private Util3.Data method847(int n, int ordinal) {
         if (ordinal >= 0 && ordinal < 6) {
            int n2 = n * 6 + ordinal;
            Util3.Data data = this.datas[n2];
            if (data == null) {
               data = this.hoveredDatas[n].getFocusSlotEnum() == FocusSlotEnum.CHARACTER ? Util3.Data.INSTANCE : this.method848(n, ordinal);
               this.datas[n2] = data;
            }

            return data;
         } else {
            return Util3.Data.INSTANCE;
         }
      }

      private Util3.Data method848(int n, int ordinal) {
         boolean flag = this.flag && Util3.method825(ordinal) && this.floatValues[ordinal] <= 0.0F;
         float f = flag ? 0.09375F : Util3.CONST_floatValues2[ordinal];
         float f2 = flag ? Util3.CONST_floatValues6[ordinal] : Util3.CONST_floatValues[ordinal];
         float[] floats = this.method849(n, ordinal, 0, f, f / Util3.CONST_floatValues2[ordinal]);
         float[] floats2 = this.method849(n, ordinal, 2, Util3.CONST_floatValues3[ordinal], 1.0F);
         float f3 = ordinal == Util3.CONST_intValue2 ? this.method849(n, ordinal, 1, -Util3.CONST_floatValues4[Util3.CONST_intValue2], 1.0F)[0] : 1.0F;
         return floats[0] == 1.0F && floats[1] == 0.0F && floats2[0] == 1.0F && floats2[1] == 0.0F && f3 == 1.0F && f2 == Util3.CONST_floatValues[ordinal]
            ? Util3.Data.INSTANCE
            : new Util3.Data(Util3.CONST_floatValues[ordinal], f2, floats[0], floats[1], f3, floats2[0], floats2[1]);
      }

      private float[] method849(int n, int ordinal, int n2, float f, float f2) {
         Class15 class2 = this.class15s[n];
         boolean flag = this.hoveredDatas[n].method1546() == WardrobeCategory.ACCESSORY;
         float[][] floatses = new float[2][];
         int[] ints = new int[2];

         for (int i = 0; i < 2; i++) {
            float floatValue = n2 == 1 ? -1.0F : (i == 0 ? -1.0F : 1.0F);
            if (n2 == 1 && i == 1) {
               break;
            }

            float f3 = Util3.method831(class2, ordinal, n2, floatValue);
            if (!Float.isNaN(f3)) {
               float f4 = f + this.floatValues2[ordinal];
               float f5;
               if (flag) {
                  f5 = Util3.method825(ordinal) ? f4 + 0.00625F : Math.max(f3 * f2, f4 + 0.00625F);
               } else {
                  f5 = f3 * f2;
                  if (this.floatValues2[ordinal] > 0.0F) {
                     f5 = Math.max(f5, Math.min(f4 + 0.0125F, f3 * f2 * 1.25F));
                  }
               }

               if (this.floatValues[ordinal] > 0.0F) {
                  f5 = Math.max(f5, Math.min(f + this.floatValues[ordinal] + 0.0125F, f3 * f2 * 1.9F));
               }

               float[] original = new float[8];
               int n3 = 0;
               original[n3++] = f3;
               original[n3++] = f5;
               float f6 = !flag && this.intValues[n] < Util3.method824(FocusSlotEnum.CAPE) ? 1.15F : 1.9F;

               for (int j = 0; j < this.hoveredDatas.length; j++) {
                  if (j != n && this.intValues[j] < this.intValues[n] && this.class15s[j].method2033(ordinal)) {
                     float[] floats = Util3.method832(this.class15s[j], class2, ordinal, n2, floatValue, flag ? 1.6F : 1.35F);
                     if (floats != null) {
                        Util3.Data data = this.method847(j, ordinal);
                        float f7 = n2 == 0 ? data.getFloatValue3() : (n2 == 1 ? data.getFloatValue5() : data.getFloatValue6());
                        float f8 = n2 == 0 ? data.getFloatValue4() : (n2 == 1 ? 0.0F : data.getFloatValue7());
                        if (n3 + floats.length > original.length) {
                           original = Arrays.copyOf(original, Math.max(original.length * 2, n3 + floats.length));
                        }

                        for (byte k = 0; k < floats.length; k += 2) {
                           float f9 = floats[k];
                           float f10 = f7 * floats[k + 1] + floatValue * f8 + 0.0125F;
                           if (f10 <= f9 * f2 * f6) {
                              original[n3++] = f9;
                              original[n3++] = f10;
                           }
                        }
                     }
                  }
               }

               floatses[i] = method850(original, n3);
               ints[i] = floatses[i].length;
            }
         }

         float f3 = 0.0F;
         float f4 = 0.0F;
         if (floatses[0] != null && floatses[1] != null) {
            float[] floats = floatses[0];
            float[] floats2 = floatses[1];

            for (byte i = 0; i < ints[0]; i += 2) {
               for (byte j = 0; j < ints[1]; j += 2) {
                  f3 = Math.max(f3, (floats[i + 1] + floats2[j + 1]) / (floats[i] + floats2[j]));
               }
            }

            float f5 = -Float.MAX_VALUE;
            float f6 = Float.MAX_VALUE;

            for (byte i = 0; i < ints[1]; i += 2) {
               f5 = Math.max(f5, floats2[i + 1] - f3 * floats2[i]);
            }

            for (byte i = 0; i < ints[0]; i += 2) {
               f6 = Math.min(f6, f3 * floats[i] - floats[i + 1]);
            }

            f4 = Math.max(f5, Math.min(f6, 0.0F));
         } else if (floatses[0] == null && floatses[1] == null) {
            f3 = f2;
         } else {
            int n3 = floatses[0] != null ? 0 : 1;

            for (byte i = 0; i < ints[n3]; i += 2) {
               f3 = Math.max(f3, floatses[n3][i + 1] / floatses[n3][i]);
            }
         }

         f3 = Math.max(0.5F, Math.min(2.5F, f3));
         f4 = Math.max(-0.1875F, Math.min(0.1875F, f4));
         if (Math.abs(f3 - 1.0F) < 0.005F) {
            f3 = 1.0F;
         }

         if (Math.abs(f4) < 3.125E-4F) {
            f4 = 0.0F;
         }

         return new float[]{f3, f4};
      }

      private static float[] method850(float[] original, int n) {
         int n2 = n / 2;
         Integer[] integers = new Integer[n2];

         for (int i = 0; i < n2; i++) {
            integers[i] = i;
         }

         Arrays.sort(integers, (var1x, var2x) -> {
            int var3x = Float.compare(original[var1x * 2], original[var2x * 2]);
            return var3x != 0 ? var3x : Float.compare(original[var2x * 2 + 1], original[var1x * 2 + 1]);
         });
         float[] floats = new float[n];
         int newLength = 0;
         float f = -Float.MAX_VALUE;
         Integer[] integers2 = integers;
         int length = integers.length;

         for (int i = 0; i < length; i++) {
            int n3 = integers2[i];
            if (original[n3 * 2 + 1] > f) {
               f = original[n3 * 2 + 1];
               floats[newLength++] = original[n3 * 2];
               floats[newLength++] = f;
            }
         }

         return Arrays.copyOf(floats, newLength);
      }
   }

   private static final class Entry2 {
      private final Class15[] class15s;
      private final FocusSlotEnum[] focusSlotEnums;
      private final boolean flag;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final int intValue;

      public Entry2(Util3.Entry entry) {
         this.class15s = entry.class15s;
         this.focusSlotEnums = new FocusSlotEnum[entry.hoveredDatas.length];

         for (int i = 0; i < this.focusSlotEnums.length; i++) {
            this.focusSlotEnums[i] = entry.hoveredDatas[i].getFocusSlotEnum();
         }

         this.flag = entry.flag;
         this.floatValues = (float[])entry.floatValues.clone();
         this.floatValues2 = (float[])entry.floatValues2.clone();
         int n = Arrays.hashCode((Object[])this.class15s);
         n = n * 31 + Arrays.hashCode((Object[])this.focusSlotEnums);
         n = n * 31 + Boolean.hashCode(this.flag);
         n = n * 31 + Arrays.hashCode(this.floatValues);
         this.intValue = n * 31 + Arrays.hashCode(this.floatValues2);
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof Util3.Entry2 entry2
            && this.intValue == entry2.intValue
            && this.flag == entry2.flag
            && Arrays.equals((Object[])this.class15s, (Object[])entry2.class15s)
            && Arrays.equals((Object[])this.focusSlotEnums, (Object[])entry2.focusSlotEnums)
            && Arrays.equals(this.floatValues, entry2.floatValues)
            && Arrays.equals(this.floatValues2, entry2.floatValues2);
      }

      @Override
      public int hashCode() {
         return this.intValue;
      }
   }

   private static final class Entry3 {
      private float[] floatValues = new float[64];
      private int size;

      public void method851(float f, float f2) {
         if (this.size == this.floatValues.length) {
            this.floatValues = Arrays.copyOf(this.floatValues, this.size * 2);
         }

         this.floatValues[this.size++] = f;
         this.floatValues[this.size++] = f2;
      }

      public float[] method852() {
         if (this.size == 0) {
            return Util3.CONST_floatValues7;
         } else {
            HashSet hashSet = new HashSet();
            float[] original = new float[this.size];
            int newLength = 0;

            for (int i = 0; i < this.size; i += 2) {
               long l = Math.round(this.floatValues[i] * 16.0F * 256.0F);
               long l2 = Math.round(this.floatValues[i + 1] * 16.0F * 256.0F);
               if (hashSet.add(l << 32 | l2 & 4294967295L)) {
                  original[newLength++] = this.floatValues[i];
                  original[newLength++] = this.floatValues[i + 1];
               }
            }

            return Arrays.copyOf(original, newLength);
         }
      }
   }

   private interface Handler {
      void accept(float f, float f2);
   }
}

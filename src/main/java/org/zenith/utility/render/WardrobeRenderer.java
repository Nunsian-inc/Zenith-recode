package org.zenith.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.PlayerModelType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;

public final class WardrobeRenderer implements MinecraftAccess {
   private static final List<WardrobeRenderer.Data> items = new ArrayList<>();

   private WardrobeRenderer() {
   }

   public static boolean method6687(PlayerModel playerModel, AvatarRenderState state) {
      Wardrobe wardrobe = Wardrobe.method2488();
      HoveredData hoveredData = WardrobeFeatureRenderer.method4445(state);
      if (wardrobe != null && hoveredData != null) {
         Class15 class2 = wardrobe.getRegistry().method1427(hoveredData);
         if (class2 == null) {
            return false;
         } else {
            if (class2.data != null && class2.data.getFloatValues() != null) {
               float f = 1.0F - Math.min(1.0F, state.walkAnimationSpeed * 2.5F);
               if (state.swingAnimation > 0.0F || state.isUsingItem || state.isFallFlying || state.isVisuallySwimming || state.isPassenger || state.isCrouching) {
                  f = 0.0F;
               }

               if (f > 0.0F) {
                  float[] floatValues = class2.data.getFloatValues();
                  ModelPart[] modelParts = new ModelPart[]{playerModel.head, playerModel.body, playerModel.rightArm, playerModel.leftArm, playerModel.rightLeg, playerModel.leftLeg};

                  for (int i = 0; i < modelParts.length; i++) {
                     ModelPart modelPart = modelParts[i];
                     float f2 = floatValues[i * 3];
                     float f3 = floatValues[i * 3 + 1];
                     float f4 = floatValues[i * 3 + 2];
                     if (i == 0) {
                        modelPart.xRot += f2 * f;
                        modelPart.yRot += f3 * f;
                        modelPart.zRot += f4 * f;
                     } else if (f2 != 0.0F || f3 != 0.0F || f4 != 0.0F) {
                        modelPart.xRot = modelPart.xRot + (f2 - modelPart.xRot) * f;
                        modelPart.yRot = modelPart.yRot + (f3 - modelPart.yRot) * f;
                        modelPart.zRot = modelPart.zRot + (f4 - modelPart.zRot) * f;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean method6688(AvatarRenderState state) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && !wardrobe.armorOnSkins.isEnabled()) {
         HoveredData hoveredData = WardrobeFeatureRenderer.method4445(state);
         if (hoveredData == null) {
            return false;
         } else {
            Class15 class2 = wardrobe.getRegistry().method1427(hoveredData);
            return class2 != null && (class2.data == null || class2.data.isFlag());
         }
      } else {
         return false;
      }
   }

   public static boolean method6689(AvatarRenderState state) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && !wardrobe.armorOnSkins.isEnabled() && !state.isFallFlying) {
         var hoveredDatas = WardrobeFeatureRenderer.method4444(state);
         return wardrobe.method2499(hoveredDatas, FocusSlotEnum.BACK) != null || wardrobe.method2499(hoveredDatas, FocusSlotEnum.CAPE) != null || method6688(state);
      } else {
         return false;
      }
   }

   public static boolean method6690(
      AvatarRenderer avatarRenderer, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int lightCoords, ModelPart modelPart, boolean flag, boolean flag2
   ) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && mc.player != null && wardrobe.showSelf.isEnabled()) {
         if (avatarRenderer.getModel() instanceof PlayerModel playerModel) {
            List<HoveredData> hoveredDatas = List.copyOf(wardrobe.method2498(mc.player.getGameProfile().name(), true));
            HoveredData hoveredData = wardrobe.method2499(hoveredDatas, FocusSlotEnum.CHARACTER);
            int ordinal = modelPart == playerModel.rightArm ? Class15.Type.RIGHT_ARM.ordinal() : Class15.Type.LEFT_ARM.ordinal();
            if (!flag2 || hoveredData != null && method6696(wardrobe, hoveredData, ordinal)) {
               if (!flag2 && hoveredData != null) {
                  return false;
               } else {
                  boolean flag3 = mc.player.getSkin().model() == PlayerModelType.SLIM;
                  Util3.Entry entry = Util3.method827(wardrobe, hoveredDatas, hoveredData, flag3, ordinal, flag);
                  if (flag2) {
                     modelPart.resetPose();
                     modelPart.visible = true;
                     playerModel.leftArm.zRot = -0.1F;
                     playerModel.rightArm.zRot = 0.1F;
                     method6697(wardrobe, hoveredData, ordinal, poseStack, orderedRenderCommandQueue, lightCoords, modelPart, false, Util3.Data.INSTANCE);
                  }

                  for (HoveredData hoveredData2 : hoveredDatas) {
                     if (hoveredData2 != hoveredData
                        && Util3.method824(hoveredData2.getFocusSlotEnum()) >= 0
                        && method6696(wardrobe, hoveredData2, ordinal)
                        && (hoveredData == null || hoveredData2.method1546() == WardrobeCategory.ACCESSORY)) {
                        method6697(wardrobe, hoveredData2, ordinal, poseStack, orderedRenderCommandQueue, lightCoords, modelPart, hoveredData2.method1546() == WardrobeCategory.ACCESSORY, entry.method845(hoveredData2, ordinal));
                     }
                  }

                  return flag2;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void method6691(PoseStack poseStack, WardrobeRenderer.Data data) {
      data.getModelPart().translateAndRotate(poseStack);
      data.getData().method842(poseStack);
   }

   public static boolean method6692() {
      return !items.isEmpty();
   }

   public static void method6693() {
      items.clear();
   }

   public static void method6694() {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();

      for (WardrobeRenderer.Data data : items) {
         PoseStack poseStack = new PoseStack();
         poseStack.last().pose().set(data.getMatrix4f());
         poseStack.last().normal().set(data.getMatrix3f());
         method6691(poseStack, data);
         modelViewStack.pushMatrix();

         try {
            modelViewStack.set(data.getMatrix4f2());
            OutlineVertexProvider.method1542(orderedRenderCommandQueue -> BoneMatrices.method4787(
               data.getClass15(),
               data.getFloatValues(),
               data.getIntValue(),
               poseStack,
               orderedRenderCommandQueue,
               data.getIntValue2(),
               OverlayTexture.NO_OVERLAY,
               true,
               data.isFlag(),
               -1
            ));
         } finally {
            modelViewStack.popMatrix();
         }
      }

      items.clear();
   }

   public static boolean method6695(SubmitNodeCollector submitNodeCollector) {
      if (items.isEmpty()) {
         return false;
      } else {
         for (WardrobeRenderer.Data data : items) {
            PoseStack poseStack = new PoseStack();
            poseStack.last().pose().set(data.getMatrix4f());
            poseStack.last().normal().set(data.getMatrix3f());
            method6691(poseStack, data);
            BoneMatrices.method4787(
               data.getClass15(),
               data.getFloatValues(),
               data.getIntValue(),
               poseStack,
               submitNodeCollector != null ? submitNodeCollector : data.getOrderedRenderCommandQueue(),
               data.getIntValue2(),
               OverlayTexture.NO_OVERLAY,
               true,
               data.isFlag(),
               -1
            );
         }

         items.clear();
         return true;
      }
   }

   private static boolean method6696(Wardrobe wardrobe, HoveredData hoveredData, int ordinal) {
      Class15 class2 = wardrobe.getRegistry().method1427(hoveredData);
      return class2 != null && class2.method2033(ordinal);
   }

   private static void method6697(
      Wardrobe wardrobe, HoveredData hoveredData, int ordinal, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int lightCoords, ModelPart modelPart, boolean flag, Util3.Data data
   ) {
      Class15 key = wardrobe.getRegistry().method1427(hoveredData);
      AnimationCache.Entry entry = AnimationCache.method7621(mc.player.getId(), hoveredData, false);
      float floatValue = (mc.player.tickCount + mc.getDeltaTracker().getGameTimeDeltaPartialTick(true)) / 20.0F;
      entry.animationState.method6539(key, "idle", floatValue);
      float[] floats = entry.class16.method2139(key, entry.animationState, floatValue, null);
      if (hoveredData.method1546() == WardrobeCategory.CHARACTER) {
         entry.class16.method2140(key);
      }

      if (flag && ShaderHandModule.INSTANCE != null && ShaderHandModule.INSTANCE.isFlag23()) {
         items.add(
            new WardrobeRenderer.Data(
               key,
               (float[])floats.clone(),
               ordinal,
               new Matrix4f(poseStack.last().pose()),
               new Matrix3f(poseStack.last().normal()),
               lightCoords,
               hoveredData.getRarity().isFlag(),
               orderedRenderCommandQueue,
               new Matrix4f(RenderSystem.getModelViewMatrixCopy()),
               modelPart,
               data
            )
         );
      } else {
         boolean flag2 = !flag && hoveredData.method1546() != WardrobeCategory.CHARACTER;
         BoneMatrices.method4791(
            key,
            floats,
            ordinal,
            poseStack,
            orderedRenderCommandQueue,
            lightCoords,
            OverlayTexture.NO_OVERLAY,
            hoveredData.getRarity().isFlag(),
            modelPart,
            data == Util3.Data.INSTANCE ? null : data::method842,
            flag2
         );
      }
   }

   private static final class Data {
      private final Class15 class15;
      private final float[] floatValues;
      private final int intValue;
      private final Matrix4f matrix4f;
      private final Matrix3f matrix3f;
      private final int intValue2;
      private final boolean flag;
      private final SubmitNodeCollector orderedRenderCommandQueue;
      private final Matrix4f matrix4f2;
      private final ModelPart modelPart;
      private final Util3.Data data;

      public Class15 class15() {
         return this.class15;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public int intValue() {
         return this.intValue;
      }

      public Matrix4f matrix4f() {
         return this.matrix4f;
      }

      public Matrix3f matrix3f() {
         return this.matrix3f;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public SubmitNodeCollector orderedRenderCommandQueue() {
         return this.orderedRenderCommandQueue;
      }

      public Matrix4f matrix4f2() {
         return this.matrix4f2;
      }

      public ModelPart modelPart() {
         return this.modelPart;
      }

      public Util3.Data data() {
         return this.data;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.matrix4f, other.matrix4f) && java.util.Objects.equals(this.matrix3f, other.matrix3f) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.orderedRenderCommandQueue, other.orderedRenderCommandQueue) && java.util.Objects.equals(this.matrix4f2, other.matrix4f2) && java.util.Objects.equals(this.modelPart, other.modelPart) && java.util.Objects.equals(this.data, other.data);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(class15, floatValues, intValue, matrix4f, matrix3f, intValue2, flag, orderedRenderCommandQueue, matrix4f2, modelPart, data);
      }

      @Override
      public String toString() {
         return "Data[" + "class15=" + this.class15 + ", " + "floatValues=" + this.floatValues + ", " + "intValue=" + this.intValue + ", " + "matrix4f=" + this.matrix4f + ", " + "matrix3f=" + this.matrix3f + ", " + "intValue2=" + this.intValue2 + ", " + "flag=" + this.flag + ", " + "orderedRenderCommandQueue=" + this.orderedRenderCommandQueue + ", " + "matrix4f2=" + this.matrix4f2 + ", " + "modelPart=" + this.modelPart + ", " + "data=" + this.data + "]";
      }

      private Data(
         Class15 key,
         float[] floatValues,
         int intValue,
         Matrix4f matrix4f,
         Matrix3f matrix3f,
         int lightCoords,
         boolean flag,
         SubmitNodeCollector orderedRenderCommandQueue,
         Matrix4f matrix4f2,
         ModelPart modelPart,
         Util3.Data data
      ) {
         this.class15 = key;
         this.floatValues = floatValues;
         this.intValue = intValue;
         this.matrix4f = matrix4f;
         this.matrix3f = matrix3f;
         this.intValue2 = lightCoords;
         this.flag = flag;
         this.orderedRenderCommandQueue = orderedRenderCommandQueue;
         this.matrix4f2 = matrix4f2;
         this.modelPart = modelPart;
         this.data = data;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public Matrix4f getMatrix4f() {
         return this.matrix4f;
      }

      public Matrix3f getMatrix3f() {
         return this.matrix3f;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public SubmitNodeCollector getOrderedRenderCommandQueue() {
         return this.orderedRenderCommandQueue;
      }

      public Matrix4f getMatrix4f2() {
         return this.matrix4f2;
      }

      public ModelPart getModelPart() {
         return this.modelPart;
      }

      public Util3.Data getData() {
         return this.data;
      }
   }
}

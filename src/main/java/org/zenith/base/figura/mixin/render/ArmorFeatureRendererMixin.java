package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaGroupPart;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaPart;
import org.zenith.base.figura.model.ParentType;
import org.zenith.base.figura.render.FiguraModelEdits;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin {
   @Unique
   private static final String RENDER = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V";
   @Unique
   private final Deque<List<Avatar>> figura$armorFrames = new ArrayDeque<>();

   @Inject(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
      at = @At("HEAD")
   )
   private void figura$beginArmor(
      PoseStack matrices, SubmitNodeCollector queue, int light, HumanoidRenderState state, float limbAngle, float limbDistance, CallbackInfo ci
   ) {
      FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
      this.figura$armorFrames.push(List.copyOf(AvatarManager.getAvatars(extension.figura$getEntityUuid())));
   }

   @Inject(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
      at = @At("RETURN")
   )
   private void figura$endArmor(
      PoseStack matrices, SubmitNodeCollector queue, int light, HumanoidRenderState state, float limbAngle, float limbDistance, CallbackInfo ci
   ) {
      this.figura$armorFrames.poll();
   }

   @WrapOperation(
      method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;II)V"
      )
   )
   private void figura$renderArmor(
      EquipmentLayerRenderer renderer,
      LayerType layer,
      ResourceKey<EquipmentAsset> asset,
      Model<?> model,
      Object state,
      ItemStack stack,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      int outline,
      Operation<Void> original,
      @Local(argsOnly = true) EquipmentSlot slot
   ) {
      List<Avatar> avatars = this.figura$armorFrames.peek();
      if (avatars != null && !avatars.isEmpty() && model instanceof HumanoidModel<?> armorModel) {
         for (Avatar avatar : avatars) {
            if (avatar.luaRuntime != null && !figura$getArmorPart(avatar, slot).checkVisible()) {
               return;
            }
         }

         boolean rendered = false;

         for (ParentType pivot : figura$getArmorPivots(slot)) {
            if (figura$isPivotVisible(avatars, pivot)) {
               for (Avatar avatar : avatars) {
                  rendered |= avatar.pivotPartRender(
                     pivot,
                     pivotStack -> {
                        pivotStack.pushPose();
                        figura$prepareArmorRender(pivotStack);
                        figura$translateToPivot(pivotStack, pivot);
                        FiguraModelEdits.submitWith(
                           figura$pivotEdit(armorModel, pivot),
                           () -> original.call(new Object[]{renderer, layer, asset, model, state, stack, pivotStack, queue, light, outline})
                        );
                        pivotStack.popPose();
                     }
                  );
               }
            }
         }

         if (!rendered) {
            FiguraModelEdits.submitWith(
               FiguraModelEdits.vanillaParts(avatars, avatarx -> figura$getArmorPart(avatarx, slot), armorModel),
               () -> original.call(new Object[]{renderer, layer, asset, model, state, stack, matrices, queue, light, outline})
            );
         }
      } else {
         original.call(new Object[]{renderer, layer, asset, model, state, stack, matrices, queue, light, outline});
      }
   }

   @Unique
   private static FiguraModelEdits.Edit figura$pivotEdit(HumanoidModel<?> model, ParentType pivot) {
      return new FiguraModelEdits.Edit() {
         @Override
         public void apply() {
            ArmorFeatureRendererMixin.figura$resetArmorModel(model);
            ArmorFeatureRendererMixin.figura$setAllVisible(model, false);
            switch (pivot) {
               case HelmetPivot:
                  ArmorFeatureRendererMixin.figura$showPart(model.head);
                  ArmorFeatureRendererMixin.figura$showPart(model.hat);
                  break;
               case ChestplatePivot:
               case LeggingsPivot:
                  ArmorFeatureRendererMixin.figura$showPart(model.body);
                  break;
               case LeftShoulderPivot:
                  ArmorFeatureRendererMixin.figura$showPart(model.leftArm);
                  break;
               case RightShoulderPivot:
                  ArmorFeatureRendererMixin.figura$showPart(model.rightArm);
                  break;
               case LeftLeggingPivot:
               case LeftBootPivot:
                  ArmorFeatureRendererMixin.figura$showPart(model.leftLeg);
                  break;
               case RightLeggingPivot:
               case RightBootPivot:
                  ArmorFeatureRendererMixin.figura$showPart(model.rightLeg);
            }
         }

         @Override
         public void restore() {
            ArmorFeatureRendererMixin.figura$setAllVisible(model, true);
         }
      };
   }

   @Unique
   private static void figura$translateToPivot(PoseStack matrices, ParentType pivot) {
      switch (pivot) {
         case LeggingsPivot:
            matrices.translate(0.0F, -0.75F, 0.0F);
            break;
         case LeftShoulderPivot:
            matrices.translate(-0.375F, 0.0F, 0.0F);
            break;
         case RightShoulderPivot:
            matrices.translate(0.375F, 0.0F, 0.0F);
            break;
         case LeftLeggingPivot:
            matrices.translate(-0.125F, -0.75F, 0.0F);
            break;
         case LeftBootPivot:
            matrices.translate(-0.125F, -1.5F, 0.0F);
            break;
         case RightLeggingPivot:
            matrices.translate(0.125F, -0.75F, 0.0F);
            break;
         case RightBootPivot:
            matrices.translate(0.125F, -1.5F, 0.0F);
      }
   }

   @Unique
   private static void figura$prepareArmorRender(PoseStack matrices) {
      matrices.scale(16.0F, 16.0F, 16.0F);
      matrices.rotate(Axis.XP.rotationDegrees(180.0F));
      matrices.rotate(Axis.YP.rotationDegrees(180.0F));
   }

   // HumanoidModel#setAllVisible was removed in 26.x
   @Unique
   private static void figura$setAllVisible(HumanoidModel<?> model, boolean visible) {
      model.head.visible = visible;
      model.hat.visible = visible;
      model.body.visible = visible;
      model.rightArm.visible = visible;
      model.leftArm.visible = visible;
      model.rightLeg.visible = visible;
      model.leftLeg.visible = visible;
   }

   @Unique
   private static void figura$showPart(ModelPart part) {
      part.visible = true;
      part.xRot = 0.0F;
      part.yRot = 0.0F;
      part.zRot = 0.0F;
   }

   @Unique
   private static void figura$resetArmorModel(HumanoidModel<?> model) {
      model.body.xRot = 0.0F;
      model.rightLeg.z = 0.0F;
      model.leftLeg.z = 0.0F;
      model.rightLeg.y = 12.0F;
      model.leftLeg.y = 12.0F;
      model.head.y = 0.0F;
      model.body.y = 0.0F;
      model.leftArm.y = 2.0F;
      model.rightArm.y = 2.0F;
      model.leftArm.x = 5.0F;
      model.rightArm.x = -5.0F;
      model.leftArm.z = 0.0F;
      model.rightArm.z = 0.0F;
   }

   @Unique
   private static ParentType[] figura$getArmorPivots(EquipmentSlot slot) {
      return switch (slot) {
         case HEAD -> new ParentType[]{ParentType.HelmetPivot};
         case CHEST -> new ParentType[]{ParentType.LeftShoulderPivot, ParentType.ChestplatePivot, ParentType.RightShoulderPivot};
         case LEGS -> new ParentType[]{ParentType.LeftLeggingPivot, ParentType.RightLeggingPivot, ParentType.LeggingsPivot};
         case FEET -> new ParentType[]{ParentType.LeftBootPivot, ParentType.RightBootPivot};
         default -> new ParentType[0];
      };
   }

   @Unique
   private static boolean figura$isPivotVisible(List<Avatar> avatars, ParentType pivot) {
      for (Avatar avatar : avatars) {
         if (avatar.luaRuntime != null) {
            VanillaPart part = figura$getPivotPart(avatar, pivot);
            if (part != null && !part.checkVisible()) {
               return false;
            }
         }
      }

      return true;
   }

   @Unique
   private static VanillaPart figura$getPivotPart(Avatar avatar, ParentType pivot) {
      return switch (pivot) {
         case HelmetPivot -> avatar.luaRuntime.vanilla_model.HELMET_HEAD;
         case ChestplatePivot -> avatar.luaRuntime.vanilla_model.CHESTPLATE_BODY;
         case LeggingsPivot -> avatar.luaRuntime.vanilla_model.LEGGINGS_BODY;
         case LeftShoulderPivot -> avatar.luaRuntime.vanilla_model.CHESTPLATE_LEFT_ARM;
         case RightShoulderPivot -> avatar.luaRuntime.vanilla_model.CHESTPLATE_RIGHT_ARM;
         case LeftLeggingPivot -> avatar.luaRuntime.vanilla_model.LEGGINGS_LEFT_LEG;
         case LeftBootPivot -> avatar.luaRuntime.vanilla_model.BOOTS_LEFT_LEG;
         case RightLeggingPivot -> avatar.luaRuntime.vanilla_model.LEGGINGS_RIGHT_LEG;
         case RightBootPivot -> avatar.luaRuntime.vanilla_model.BOOTS_RIGHT_LEG;
         default -> null;
      };
   }

   @Unique
   private static VanillaGroupPart figura$getArmorPart(Avatar avatar, EquipmentSlot slot) {
      return switch (slot) {
         case HEAD -> avatar.luaRuntime.vanilla_model.HELMET;
         case CHEST -> avatar.luaRuntime.vanilla_model.CHESTPLATE;
         case LEGS -> avatar.luaRuntime.vanilla_model.LEGGINGS;
         case FEET -> avatar.luaRuntime.vanilla_model.BOOTS;
         default -> throw new IllegalArgumentException("Unsupported armor slot: " + slot);
      };
   }
}

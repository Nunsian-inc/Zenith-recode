package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.model.ParentType;
import org.zenith.base.figura.render.FiguraModelEdits;
import org.zenith.base.figura.render.FiguraWings;

@Mixin(WingsLayer.class)
public abstract class ElytraFeatureRendererMixin {
   @WrapOperation(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V"
      )
   )
   private void figura$renderElytra(
      EquipmentLayerRenderer renderer,
      LayerType layer,
      ResourceKey<EquipmentAsset> asset,
      Model<?> model,
      Object state,
      ItemStack stack,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      Identifier texture,
      int outline,
      int order,
      Operation<Void> original
   ) {
      HumanoidRenderState bipedState = (HumanoidRenderState)state;
      FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)bipedState;
      List<Avatar> avatars = List.copyOf(AvatarManager.getAvatars(extension.figura$getEntityUuid()));
      if (!avatars.isEmpty() && model instanceof ElytraModel elytraModel) {
         elytraModel.setupAnim(bipedState);
         Minecraft client = Minecraft.getInstance();
         Entity entity = client.level == null ? null : client.level.getEntity(extension.figura$getEntityId());

         for (Avatar avatar : avatars) {
            if (avatar.luaRuntime != null) {
               avatar.luaRuntime.vanilla_model.ELYTRA.save(elytraModel);
               avatar.luaRuntime.vanilla_model.ELYTRA.preTransform(elytraModel);
            }
         }

         if (entity != null) {
            float delta = extension.figura$getTickDelta();

            for (Avatar avatar : avatars) {
               avatar.elytraRender(entity, queue, matrices, light, delta, elytraModel);
            }
         }

         for (Avatar avatar : avatars) {
            if (avatar.luaRuntime != null) {
               avatar.luaRuntime.vanilla_model.ELYTRA.posTransform(elytraModel);
            }
         }

         for (int i = avatars.size() - 1; i >= 0; i--) {
            Avatar avatar = avatars.get(i);
            if (avatar.luaRuntime != null) {
               avatar.luaRuntime.vanilla_model.ELYTRA.restore(elytraModel);
            }
         }

         if (!figura$hasLuaRuntime(avatars)) {
            original.call(new Object[]{renderer, layer, asset, model, state, stack, matrices, queue, light, texture, outline, order});
         } else if (figura$isVisible(avatars, null)) {
            for (boolean left : new boolean[]{true, false}) {
               FiguraWings.Submit wing = wingMatrices -> FiguraModelEdits.submitWith(
                  FiguraWings.single(elytraModel, left),
                  () -> original.call(new Object[]{renderer, layer, asset, model, state, stack, wingMatrices, queue, light, texture, outline, order})
               );
               figura$renderWingWithFallback(avatars, left ? ParentType.LeftElytraPivot : ParentType.RightElytraPivot, left, matrices, wing);
            }
         }
      } else {
         original.call(new Object[]{renderer, layer, asset, model, state, stack, matrices, queue, light, texture, outline, order});
      }
   }

   @Unique
   private static void figura$renderWingWithFallback(List<Avatar> avatars, ParentType pivot, boolean left, PoseStack matrices, FiguraWings.Submit wing) {
      if (figura$isVisible(avatars, left)) {
         boolean renderedAtPivot = false;

         for (Avatar avatar : avatars) {
            renderedAtPivot |= avatar.pivotPartRender(pivot, pivotStack -> {
               pivotStack.pushPose();
               pivotStack.scale(16.0F, 16.0F, 16.0F);
               pivotStack.rotate(Axis.XP.rotationDegrees(180.0F));
               pivotStack.rotate(Axis.YP.rotationDegrees(180.0F));
               pivotStack.translate(0.0F, 0.0F, 0.125F);
               wing.submit(pivotStack);
               pivotStack.popPose();
            });
         }

         if (!renderedAtPivot) {
            wing.submit(matrices);
         }
      }
   }

   @Unique
   private static boolean figura$hasLuaRuntime(List<Avatar> avatars) {
      for (Avatar avatar : avatars) {
         if (avatar.luaRuntime != null) {
            return true;
         }
      }

      return false;
   }

   @Unique
   private static boolean figura$isVisible(List<Avatar> avatars, Boolean left) {
      for (Avatar avatar : avatars) {
         if (avatar.luaRuntime != null) {
            boolean visible = left == null
               ? avatar.luaRuntime.vanilla_model.ELYTRA.checkVisible()
               : (left ? avatar.luaRuntime.vanilla_model.LEFT_ELYTRA : avatar.luaRuntime.vanilla_model.RIGHT_ELYTRA).checkVisible();
            if (!visible) {
               return false;
            }
         }
      }

      return true;
   }
}

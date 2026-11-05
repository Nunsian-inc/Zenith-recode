package org.zenith.base.figura.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.animal.parrot.ParrotModel.Pose;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.parrot.Parrot.Variant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.model.ParentType;

@Mixin(ParrotOnShoulderLayer.class)
public abstract class ShoulderParrotFeatureRendererMixin {
   @Shadow
   @Final
   private ParrotModel model;

   @Inject(
      method = "submitOnShoulder(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/world/entity/animal/parrot/Parrot$Variant;FFZ)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void figura$renderAtPivot(
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      AvatarRenderState state,
      Variant variant,
      float headYaw,
      float headPitch,
      boolean left,
      CallbackInfo ci
   ) {
      FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
      boolean replaced = false;
      boolean hidden = false;

      for (Avatar avatar : AvatarManager.getAvatars(extension.figura$getEntityUuid())) {
         if (avatar.luaRuntime != null) {
            boolean visible = left ? avatar.luaRuntime.vanilla_model.LEFT_PARROT.checkVisible() : avatar.luaRuntime.vanilla_model.RIGHT_PARROT.checkVisible();
            hidden |= !visible;
         }

         replaced |= avatar.pivotPartRender(
            left ? ParentType.LeftParrotPivot : ParentType.RightParrotPivot,
            pivotStack -> this.figura$renderParrot(pivotStack, queue, light, state, variant, headYaw, headPitch)
         );
      }

      if (hidden || replaced) {
         ci.cancel();
      }
   }

   @Unique
   private void figura$renderParrot(
      PoseStack matrices, SubmitNodeCollector queue, int light, AvatarRenderState state, Variant variant, float headYaw, float headPitch
   ) {
      matrices.translate(0.0, 24.0, 0.0);
      matrices.scale(16.0F, 16.0F, 16.0F);
      matrices.rotate(Axis.XP.rotationDegrees(180.0F));
      matrices.rotate(Axis.YP.rotationDegrees(180.0F));
      ParrotRenderState parrot = new ParrotRenderState();
      parrot.pose = Pose.ON_SHOULDER;
      parrot.ageInTicks = state.ageInTicks;
      parrot.walkAnimationPos = state.walkAnimationPos;
      parrot.walkAnimationSpeed = state.walkAnimationSpeed;
      parrot.yRot = headYaw;
      parrot.xRot = headPitch;
      queue.submitModel(
         this.model,
         parrot,
         matrices,
         this.model.renderType(ParrotRenderer.getVariantTexture(variant)),
         light,
         OverlayTexture.NO_OVERLAY,
         state.outlineColor
      );
   }
}

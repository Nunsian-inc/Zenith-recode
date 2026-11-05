package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.zenith.MinecraftAccess;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.base.figura.ducks.LivingEntityRendererAccessor;
import org.zenith.client.events.LivingRenderColorEvent;
import org.zenith.client.modules.impl.movement.SpeedModule;
import org.zenith.client.modules.impl.render.ChamsModule;
import org.zenith.utility.render.GlowUtil;

@Mixin({LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
   implements MinecraftAccess {
   @ModifyExpressionValue(
      method = {"extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;solveBodyRot(Lnet/minecraft/world/entity/LivingEntity;FF)F"
      )}
   )
   public float changeYaw(float f, LivingEntity livingEntity) {
      RotationManager rotationManager = Zenith.getInstance().getRotationManager();
      return !livingEntity.equals(mc.player) || rotationManager.method7845() || SpeedModule.INSTANCE.isEnabled() && SpeedModule.INSTANCE.method6606()
         ? f
         : Mth.rotLerp(GlowUtil.getTickDelta(), rotationManager.getRotation().getFloatValue(), rotationManager.getCurrentRotation().getFloatValue());
   }

   @ModifyExpressionValue(
      method = {"extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/Mth;rotLerp(FFF)F"
      )}
   )
   public float changeHeadYaw(float f, LivingEntity livingEntity) {
      RotationManager rotationManager = Zenith.getInstance().getRotationManager();
      return !livingEntity.equals(mc.player) || rotationManager.method7845() || SpeedModule.INSTANCE.isEnabled() && SpeedModule.INSTANCE.method6606()
         ? f
         : Mth.rotLerp(GlowUtil.getTickDelta(), rotationManager.getRotation().getFloatValue(), rotationManager.getCurrentRotation().getFloatValue());
   }

   @ModifyExpressionValue(
      method = {"extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getXRot(F)F"
      )}
   )
   public float changePitch(float f, LivingEntity livingEntity) {
      RotationManager rotationManager = Zenith.getInstance().getRotationManager();
      return !livingEntity.equals(mc.player) || rotationManager.method7845() || SpeedModule.INSTANCE.isEnabled() && SpeedModule.INSTANCE.method6606()
         ? f
         : Mth.rotLerp(GlowUtil.getTickDelta(), rotationManager.getRotation().getFloatValue2(), rotationManager.getCurrentRotation().getFloatValue2());
   }

   @Shadow
   @Nullable
   public abstract RenderType getRenderType(LivingEntityRenderState entityRenderState, boolean flag, boolean flag2, boolean flag3);

   @Shadow
   public abstract Identifier getTextureLocation(S value);

   @Redirect(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/renderer/rendertype/RenderType;"
      )
   )
   private RenderType renderHook(LivingEntityRenderer livingEntityRenderer, LivingEntityRenderState entityRenderState, boolean flag, boolean flag2, boolean flag3) {
      ChamsModule chamsModule = ChamsModule.INSTANCE;
      if (chamsModule.method5600(entityRenderState)) {
         chamsModule.method5599();
         if (chamsModule.method5602()) {
            RenderType renderType = chamsModule.method5601(this.getTextureLocation((S)entityRenderState));
            if (renderType != null) {
               return renderType;
            }
         }

         return this.getRenderType(entityRenderState, flag, true, flag3);
      } else {
         if (!flag2 && !flag && entityRenderState.isInvisibleToPlayer && entityRenderState.boundingBoxWidth == 0.6F && EventManager.hasListeners(LivingRenderColorEvent.class)) {
            LivingRenderColorEvent event = new LivingRenderColorEvent(-1);
            EventManager.call(event);
            if (event.isCancelled()) {
               flag2 = true;
            }
         }

         return this.getRenderType(entityRenderState, flag, flag2, flag3);
      }
   }

   @Redirect(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/UvMapping;I)V"
      )
   )
   private void renderModelHook(
      SubmitNodeCollector submitNodeCollector,
      Model model,
      Object state,
      PoseStack poseStack,
      RenderType renderType,
      int lightCoords,
      int overlayCoords,
      int color,
      UvMapping uvMapping,
      int outlineColor,
      @Local(ordinal = 0,argsOnly = true) LivingEntityRenderState entityRenderState
   ) {
      overlayCoords = LivingEntityRendererAccessor.overrideOverlay.orElse(overlayCoords);
      ChamsModule chamsModule = ChamsModule.INSTANCE;
      if (chamsModule.method5600(entityRenderState)) {
         color = chamsModule.method5602() ? chamsModule.method5603(color) : chamsModule.method5604(color);
      } else if (entityRenderState.isInvisibleToPlayer && EventManager.hasListeners(LivingRenderColorEvent.class)) {
         LivingRenderColorEvent event = new LivingRenderColorEvent(color);
         EventManager.call(event);
         color = event.getColor();
      }

      submitNodeCollector.submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords, color, uvMapping, outlineColor);
   }
}

package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.math.matrix.FiguraMat4;
import org.zenith.base.figura.model.rendering.EntityRenderMode;
import org.zenith.base.figura.model.rendering.PartFilterScheme;
import org.zenith.base.figura.render.FiguraModelEdits;
import org.zenith.base.figura.render.FiguraRenderFrames;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
   @Unique
   private static final String RENDER = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V";
   @Unique
   private final Deque<FiguraRenderFrames.Living> figura$frames = new ArrayDeque<>();

   @Shadow
   protected abstract boolean isBodyVisible(S var1);

   @Shadow
   protected abstract float getWhiteOverlayProgress(S var1);

   @Shadow
   public abstract M getModel();

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At("HEAD")
   )
   private void figura$beginRender(S state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
      List<Avatar> avatars = List.copyOf(AvatarManager.getAvatars(extension.figura$getEntityUuid()));
      List<Avatar> weapons = AvatarManager.getWeaponAvatars(extension.figura$getEntityUuid());
      Matrix4f pose = avatars.isEmpty() && weapons.isEmpty() ? null : new Matrix4f(matrices.last().pose());
      this.figura$frames.push(new FiguraRenderFrames.Living(avatars, weapons, pose));
   }

   @WrapOperation(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/UvMapping;I)V"
      )
   )
   private void figura$submitBody(
      SubmitNodeCollector queue,
      Model<?> model,
      Object state,
      PoseStack matrices,
      RenderType layer,
      int light,
      int overlay,
      int color,
      net.minecraft.client.renderer.texture.UvMapping sprite,
      int outline,
      Operation<Void> original
   ) {
      FiguraRenderFrames.Living frame = this.figura$frames.peek();
      if (frame != null && !frame.avatars.isEmpty() && model instanceof EntityModel<?> entityModel) {
         FiguraModelEdits.submitWith(
            FiguraModelEdits.vanillaParts(frame.avatars, avatar -> avatar.luaRuntime.vanilla_model.PLAYER, entityModel),
            () -> original.call(new Object[]{queue, model, state, matrices, layer, light, overlay, color, sprite, outline})
         );
      } else {
         original.call(new Object[]{queue, model, state, matrices, layer, light, overlay, color, sprite, outline});
      }
   }

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;)V", shift = Shift.AFTER)
   )
   private void figura$renderAvatars(S state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      FiguraRenderFrames.Living frame = this.figura$frames.peek();
      if (frame != null && !frame.processed) {
         frame.processed = true;
         List<Avatar> avatars = frame.avatars;
         List<Avatar> weapons = frame.weapons;
         if (frame.initialPose != null && (!avatars.isEmpty() || !weapons.isEmpty())) {
            FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
            Entity entity = figura$resolveEntity(extension);
            if (entity != null) {
               boolean showBody = this.isBodyVisible(state);
               boolean translucent = !showBody && !state.isInvisibleToPlayer;
               boolean glowing = !showBody && state.appearsGlowing();
               boolean invisible = !showBody && !translucent && !glowing;
               PartFilterScheme filter = invisible ? PartFilterScheme.PIVOTS : PartFilterScheme.MODEL;
               int overlay = LivingEntityRenderer.getOverlayCoords(state, this.getWhiteOverlayProgress(state));
               float tickDelta = extension.figura$getTickDelta();
               FiguraMat4 poseMatrix = new FiguraMat4().set(new Matrix4f(frame.initialPose).invert().mul(matrices.last().pose()));
               LivingEntityRenderer<?, ?, ?> renderer = (LivingEntityRenderer<?, ?, ?>)(Object)this;
               EntityModel<?> model = this.getModel();

               for (Avatar avatar : avatars) {
                  avatar.prepareEntity(entity);
                  if (avatar.luaRuntime != null) {
                     avatar.luaRuntime.vanilla_model.PLAYER.save(model);
                     avatar.luaRuntime.vanilla_model.PLAYER.preTransform(model);
                     frame.vanillaTransformed = true;
                  }
               }

               for (Avatar avatar : avatars) {
                  figura$useFallbackMode(avatar);
                  avatar.renderEvent(tickDelta, poseMatrix);
                  avatar.render(
                     entity,
                     state.yRot,
                     tickDelta,
                     translucent ? 0.15F : 1.0F,
                     matrices,
                     queue,
                     state.lightCoords,
                     overlay,
                     renderer,
                     filter,
                     translucent,
                     glowing
                  );
                  avatar.postRenderEvent(tickDelta, poseMatrix);
               }

               for (Avatar avatar : avatars) {
                  if (avatar.luaRuntime != null) {
                     avatar.luaRuntime.vanilla_model.PLAYER.posTransform(model);
                  }
               }

               for (Avatar avatar : weapons) {
                  avatar.prepareEntity(entity);
                  figura$useFallbackMode(avatar);
                  avatar.renderEvent(tickDelta, poseMatrix);
                  avatar.postRenderEvent(tickDelta, poseMatrix);
               }
            }
         }
      }
   }

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
   )
   private void figura$restoreVanillaModel(S state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      FiguraRenderFrames.Living frame = this.figura$frames.poll();
      if (frame != null && frame.vanillaTransformed) {
         EntityModel<?> model = this.getModel();

         for (int i = frame.avatars.size() - 1; i >= 0; i--) {
            Avatar avatar = frame.avatars.get(i);
            if (avatar.luaRuntime != null) {
               avatar.luaRuntime.vanilla_model.PLAYER.restore(model);
            }
         }
      }
   }

   @Unique
   private static Entity figura$resolveEntity(FiguraEntityRenderStateExtension extension) {
      Minecraft client = Minecraft.getInstance();
      if (client.level == null) {
         return null;
      }

      Entity entity = client.level.getEntity(extension.figura$getEntityId());
      if (entity == null && client.player != null && client.player.getUUID().equals(extension.figura$getEntityUuid())) {
         entity = client.player;
      }

      return entity;
   }

   @Unique
   private static void figura$useFallbackMode(Avatar avatar) {
      if (avatar.renderMode == EntityRenderMode.OTHER) {
         avatar.renderMode = EntityRenderMode.RENDER;
      }
   }
}

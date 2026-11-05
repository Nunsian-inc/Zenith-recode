package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.model.rendering.EntityRenderMode;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
   @Shadow
   @Final
   private EntityRenderDispatcher entityRenderDispatcher;

   @Inject(
      method = "render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZZ)V",
      at = @At("HEAD")
   )
   private void figura$beginWorldRender(
      GraphicsResourceAllocator allocator,
      boolean renderBlockOutline,
      net.minecraft.client.renderer.state.level.CameraRenderState camera,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean renderSky,
      boolean consistentDepth,
      CallbackInfo ci
   ) {
      float tickDelta = Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.worldPartialTicks;

      for (Avatar avatar : AvatarManager.getAllAvatars()) {
         avatar.worldRenderEvent(tickDelta);
      }
   }

   @Inject(
      method = "render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZZ)V",
      at = @At("RETURN")
   )
   private void figura$endWorldRender(
      GraphicsResourceAllocator allocator,
      boolean renderBlockOutline,
      net.minecraft.client.renderer.state.level.CameraRenderState camera,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean renderSky,
      boolean consistentDepth,
      CallbackInfo ci
   ) {
      float tickDelta = Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.worldPartialTicks;

      for (Avatar avatar : AvatarManager.getAllAvatars()) {
         avatar.postWorldRenderEvent(tickDelta);
      }
   }

   @Inject(
      method = "submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"
      )
   )
   private void figura$renderWorldParts(
      PoseStack matrices, LevelRenderState renderStates, SubmitNodeCollector queue, CallbackInfo ci, @Local EntityRenderState state
   ) {
      FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
      if (extension.figura$getEntityUuid() != null) {
         Collection<Avatar> avatars = AvatarManager.getAvatars(extension.figura$getEntityUuid());
         if (!avatars.isEmpty()) {
            Minecraft client = Minecraft.getInstance();
            Entity entity = client.level == null ? null : client.level.getEntity(extension.figura$getEntityId());
            if (entity != null) {
               Vec3 camera = renderStates.cameraRenderState.pos;
               float tickDelta = extension.figura$getTickDelta();
               int light = this.entityRenderDispatcher.getPackedLightCoords(entity, tickDelta);

               for (Avatar avatar : avatars) {
                  avatar.prepareEntity(entity);
                  avatar.renderMode = EntityRenderMode.RENDER;
                  avatar.worldRender(entity, camera.x, camera.y, camera.z, matrices, queue, light, tickDelta, EntityRenderMode.WORLD);
               }
            }
         }
      }
   }
}

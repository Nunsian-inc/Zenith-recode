package org.zenith.base.figura.mixin.render;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.UUID;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraSkullStateExtension;
import org.zenith.base.figura.lua.api.world.BlockStateAPI;

@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockEntityRendererMixin {
   @Inject(
      method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/SkullBlockRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
      at = @At("TAIL")
   )
   private void figura$captureOwner(
      SkullBlockEntity skull, SkullBlockRenderState state, float tickDelta, Vec3 camera, CrumblingOverlay crumbling, CallbackInfo ci
   ) {
      ResolvableProfile profile = skull.getOwnerProfile();
      UUID owner = null;
      if (profile != null) {
         GameProfile gameProfile = profile.partialProfile();
         owner = gameProfile == null ? null : gameProfile.id();
      }

      ((FiguraSkullStateExtension)state).figura$setOwner(owner);
   }

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/blockentity/state/SkullBlockRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void figura$renderSkullBlock(SkullBlockRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      UUID owner = ((FiguraSkullStateExtension)state).figura$getOwner();
      if (owner != null) {
         BlockStateAPI block = new BlockStateAPI(state.blockState, state.blockPos);
         // 26.3 bakes facing/rotation into state.transformation; rebuild the 1.21.11 values from the block state
         Direction direction = state.blockState.getBlock() instanceof WallSkullBlock ? state.blockState.getValue(WallSkullBlock.FACING) : null;
         float rotation = RotationSegment.convertToDegrees(
            direction != null ? RotationSegment.convertToSegment(direction.getOpposite()) : state.blockState.getValue(SkullBlock.ROTATION)
         );
         boolean replaced = false;

         for (Avatar avatar : AvatarManager.getAvatars(owner)) {
            replaced |= avatar.skullRenderEvent(1.0F, block, null, null, "BLOCK");
            replaced |= avatar.skullRender(matrices, queue, state.lightCoords, direction, rotation);
         }

         if (replaced) {
            ci.cancel();
         }
      }
   }
}

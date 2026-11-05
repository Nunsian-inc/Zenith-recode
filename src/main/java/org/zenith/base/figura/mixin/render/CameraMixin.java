package org.zenith.base.figura.mixin.render;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.utils.RenderUtils;

// 26.3: Camera.setup(level, entity, ...) became update() -> alignWithEntity(partialTicks)
@Mixin(Camera.class)
public abstract class CameraMixin {
   @Shadow
   private float xRot;
   @Shadow
   private float yRot;
   @Shadow
   private Entity entity;
   @Unique
   private Avatar figura$avatar;

   @Shadow
   protected abstract void setRotation(float var1, float var2);

   @Shadow
   protected abstract void move(float var1, float var2, float var3);

   @Inject(method = "alignWithEntity(F)V", at = @At("HEAD"))
   private void figura$captureAvatar(float tickDelta, CallbackInfo ci) {
      this.figura$avatar = AvatarManager.getAvatar(this.entity);
   }

   @Inject(
      method = "alignWithEntity(F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", shift = Shift.AFTER)
   )
   private void figura$setupRot(float tickDelta, CallbackInfo ci) {
      if (RenderUtils.vanillaModelAndScript(this.figura$avatar)) {
         float x = this.xRot;
         float y = this.yRot;
         FiguraVec3 rot = this.figura$avatar.luaRuntime.renderer.cameraRot;
         if (rot != null && rot.notNaN()) {
            x = (float)rot.x;
            y = (float)rot.y;
         }

         FiguraVec3 offset = this.figura$avatar.luaRuntime.renderer.cameraOffsetRot;
         if (offset != null && offset.notNaN()) {
            x += (float)offset.x;
            y += (float)offset.y;
         }

         this.setRotation(y, x);
      }
   }

   @ModifyArg(
      method = "alignWithEntity(F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"),
      index = 0
   )
   private double figura$setupPivotX(double originalX) {
      if (!RenderUtils.vanillaModelAndScript(this.figura$avatar)) {
         return originalX;
      }

      double x = originalX;
      FiguraVec3 piv = this.figura$avatar.luaRuntime.renderer.cameraPivot;
      if (piv != null && piv.notNaN()) {
         x = piv.x;
      }

      FiguraVec3 offset = this.figura$avatar.luaRuntime.renderer.cameraOffsetPivot;
      if (offset != null && offset.notNaN()) {
         x += offset.x;
      }

      return x;
   }

   @ModifyArg(
      method = "alignWithEntity(F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"),
      index = 1
   )
   private double figura$setupPivotY(double originalY) {
      if (!RenderUtils.vanillaModelAndScript(this.figura$avatar)) {
         return originalY;
      }

      double y = originalY;
      FiguraVec3 piv = this.figura$avatar.luaRuntime.renderer.cameraPivot;
      if (piv != null && piv.notNaN()) {
         y = piv.y;
      }

      FiguraVec3 offset = this.figura$avatar.luaRuntime.renderer.cameraOffsetPivot;
      if (offset != null && offset.notNaN()) {
         y += offset.y;
      }

      return y;
   }

   @ModifyArg(
      method = "alignWithEntity(F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"),
      index = 2
   )
   private double figura$setupPivotZ(double originalZ) {
      if (!RenderUtils.vanillaModelAndScript(this.figura$avatar)) {
         return originalZ;
      }

      double z = originalZ;
      FiguraVec3 piv = this.figura$avatar.luaRuntime.renderer.cameraPivot;
      if (piv != null && piv.notNaN()) {
         z = piv.z;
      }

      FiguraVec3 offset = this.figura$avatar.luaRuntime.renderer.cameraOffsetPivot;
      if (offset != null && offset.notNaN()) {
         z += offset.z;
      }

      return z;
   }

   @Inject(method = "alignWithEntity(F)V", at = @At("RETURN"))
   private void figura$setupPos(float tickDelta, CallbackInfo ci) {
      if (RenderUtils.vanillaModelAndScript(this.figura$avatar)) {
         FiguraVec3 pos = this.figura$avatar.luaRuntime.renderer.cameraPos;
         if (pos != null && pos.notNaN()) {
            this.move((float)(-pos.z), (float)pos.y, (float)(-pos.x));
         }
      }

      this.figura$avatar = null;
   }
}

package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Rotation;
import org.zenith.client.events.CameraPosEvent;
import org.zenith.client.events.CameraUpdateEvent;
import org.zenith.client.modules.impl.movement.SpeedModule;

@Mixin({Camera.class})
public abstract class MixinCamera {
   @Shadow
   private Vec3 position;
   @Shadow
   private Entity entity;
   @Shadow
   @Final
   private MutableBlockPos blockPosition;
   @Shadow
   private float yRot;
   @Shadow
   private float xRot;
   @Shadow
   private float eyeHeight;
   @Shadow
   private float eyeHeightOld;
   @Shadow
   private boolean detached;
   @Shadow
   private net.minecraft.client.renderer.culling.Frustum cullFrustum;

   @Shadow
   public abstract void setRotation(float var1, float var2);

   @Shadow
   public abstract void move(float var1, float var2, float var3);

   @Shadow
   public abstract float getMaxZoom(float var1);

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$keepStandingCameraHeight(CallbackInfo var1) {
      if (this.entity instanceof LocalPlayer var2
         && var2 == Minecraft.getInstance().player
         && SpeedModule.INSTANCE.isEnabled()
         && SpeedModule.INSTANCE.method6606()) {
         float var4 = var2.getEyeHeight(Pose.STANDING);
         this.eyeHeight = var4;
         this.eyeHeightOld = var4;
         var1.cancel();
      }
   }

   // 26.3: Camera.setup(level, entity, detached, mirrored, pt) became update() -> alignWithEntity(pt); detached/mirrored come from options
   @Inject(
      method = {"alignWithEntity(F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
         shift = Shift.AFTER
      )},
      cancellable = true
   )
   private void updateHook(float var5, CallbackInfo var6) {
      Entity var2 = this.entity;
      boolean var3 = !Minecraft.getInstance().options.getCameraType().isFirstPerson();
      boolean var4 = Minecraft.getInstance().options.getCameraType().isMirrored();
      CameraUpdateEvent var7 = new CameraUpdateEvent(false, 4.0F, new Rotation(this.yRot, this.xRot));
      EventManager.call(var7);
      Rotation var8 = var7.getRotation();
      if (var7.isCancelled() && var2 instanceof LocalPlayer var9 && !var9.isSleeping() && var3) {
         float var10 = var4 ? -var8.getFloatValue2() : var8.getFloatValue2();
         float var11 = var8.getFloatValue() - (var4 ? 180 : 0);
         float var12 = var7.getFloatValue();
         this.setRotation(var11, var10);
         this.detached = true;
         this.move(var7.isFlag() ? -var12 : -this.getMaxZoom(var12), 0.0F, 0.0F);
         var6.cancel();
      }
   }

   @Inject(
      method = {"setPosition(Lnet/minecraft/world/phys/Vec3;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void posHook(Vec3 var1, CallbackInfo var2) {
      CameraPosEvent var3 = new CameraPosEvent(var1);
      EventManager.call(var3);
      this.position = var1 = var3.getVec3d();
      this.blockPosition.set(var1.x, var1.y, var1.z);
      var2.cancel();
   }

   // moved from MixinWorldRenderer: 26.3 builds the cull frustum in Camera
   @Inject(method = {"prepareCullFrustum"}, at = {@At("RETURN")})
   private void zenith$captureFrustum(CallbackInfo var1) {
      org.zenith.utility.misc.FrustumHolder.method3035(this.cullFrustum);
   }

   // ---- moved from MixinGameRenderer: 26.3 computes fov and the projection inside Camera ----
   @com.llamalad7.mixinextras.injector.ModifyExpressionValue(
      method = {"calculateFov(F)F"},
      at = {@At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I", remap = false)}
   )
   private int hookGetFov(int var1) {
      org.zenith.client.events.FovEvent var2 = new org.zenith.client.events.FovEvent();
      EventManager.call(var2);
      return var2.isCancelled() ? var2.getIntValue() : var1;
   }

   // script zoom (was MixinScriptCamera on GameRenderer.getFov); runs before the weather capture below
   @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = {"calculateFov(F)F"}, at = {@At("RETURN")})
   private float zenith$scriptZoom(float var1) {
      float var2 = org.zenith.utility.game.Util4.method1281();
      return var2 > 1.001F ? Math.max(1.0F, var1 / var2) : var1;
   }

   @Inject(method = {"calculateFov(F)F"}, at = {@At("RETURN")})
   private void captureWeatherFov(float var1, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Float> var2) {
      org.zenith.utility.misc.Util15.method2703(var2.getReturnValueF());
   }

   // ProjectionMatrixEvent overrides the aspect ratio (old GameRenderer.getProjectionMatrix hook)
   @org.spongepowered.asm.mixin.injection.Redirect(
      method = {"update(Lnet/minecraft/client/DeltaTracker;)V"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setupPerspective(FFFFF)V")
   )
   private void zenith$aspect(Camera var1, float var2, float var3, float var4, float var5, float var6) {
      org.zenith.client.events.ProjectionMatrixEvent var7 = new org.zenith.client.events.ProjectionMatrixEvent();
      EventManager.call(var7);
      this.setupPerspective(var2, var3, var4, var7.isCancelled() ? var7.getFloatValue() * var6 : var5, var6);
   }

   @Shadow
   protected abstract void setupPerspective(float var1, float var2, float var3, float var4, float var5);
}

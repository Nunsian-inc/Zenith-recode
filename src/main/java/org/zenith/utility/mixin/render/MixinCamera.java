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
   public abstract void setRotation(float f, float f2);

   @Shadow
   public abstract void move(float f, float f2, float f3);

   @Shadow
   public abstract float getMaxZoom(float floatValue);

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$keepStandingCameraHeight(CallbackInfo callbackInfo) {
      if (this.entity instanceof LocalPlayer player
         && player == Minecraft.getInstance().player
         && SpeedModule.INSTANCE.isEnabled()
         && SpeedModule.INSTANCE.method6606()) {
         float eyeHeight = player.getEyeHeight(Pose.STANDING);
         this.eyeHeight = eyeHeight;
         this.eyeHeightOld = eyeHeight;
         callbackInfo.cancel();
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
   private void updateHook(float f, CallbackInfo callbackInfo) {
      Entity entity = this.entity;
      boolean flag = !Minecraft.getInstance().options.getCameraType().isFirstPerson();
      boolean mirrored = Minecraft.getInstance().options.getCameraType().isMirrored();
      CameraUpdateEvent event = new CameraUpdateEvent(false, 4.0F, new Rotation(this.yRot, this.xRot));
      EventManager.call(event);
      Rotation rotation = event.getRotation();
      if (event.isCancelled() && entity instanceof LocalPlayer player && !player.isSleeping() && flag) {
         float f2 = mirrored ? -rotation.getFloatValue2() : rotation.getFloatValue2();
         float f3 = rotation.getFloatValue() - (mirrored ? 180 : 0);
         float floatValue = event.getFloatValue();
         this.setRotation(f3, f2);
         this.detached = true;
         this.move(event.isFlag() ? -floatValue : -this.getMaxZoom(floatValue), 0.0F, 0.0F);
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"setPosition(Lnet/minecraft/world/phys/Vec3;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void posHook(Vec3 vec3d, CallbackInfo callbackInfo) {
      CameraPosEvent event = new CameraPosEvent(vec3d);
      EventManager.call(event);
      this.position = vec3d = event.getVec3d();
      this.blockPosition.set(vec3d.x, vec3d.y, vec3d.z);
      callbackInfo.cancel();
   }

   // moved from MixinWorldRenderer: 26.3 builds the cull frustum in Camera
   @Inject(method = {"prepareCullFrustum"}, at = {@At("RETURN")})
   private void zenith$captureFrustum(CallbackInfo callbackInfo) {
      org.zenith.utility.misc.FrustumHolder.method3035(this.cullFrustum);
   }

   // ---- moved from MixinGameRenderer: 26.3 computes fov and the projection inside Camera ----
   @com.llamalad7.mixinextras.injector.ModifyExpressionValue(
      method = {"calculateFov(F)F"},
      at = {@At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I", remap = false)}
   )
   private int hookGetFov(int n) {
      org.zenith.client.events.FovEvent event = new org.zenith.client.events.FovEvent();
      EventManager.call(event);
      return event.isCancelled() ? event.getIntValue() : n;
   }

   // script zoom (was MixinScriptCamera on GameRenderer.getFov); runs before the weather capture below
   @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = {"calculateFov(F)F"}, at = {@At("RETURN")})
   private float zenith$scriptZoom(float f) {
      float f2 = org.zenith.utility.game.ScriptRenderOverrides.getMaxCameraZoom();
      return f2 > 1.001F ? Math.max(1.0F, f / f2) : f;
   }

   @Inject(method = {"calculateFov(F)F"}, at = {@At("RETURN")})
   private void captureWeatherFov(float f, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Float> callbackInfoReturnable) {
      org.zenith.utility.misc.CameraFov.setCapturedFov(callbackInfoReturnable.getReturnValueF());
   }

   // ProjectionMatrixEvent overrides the aspect ratio (old GameRenderer.getProjectionMatrix hook)
   @org.spongepowered.asm.mixin.injection.Redirect(
      method = {"update(Lnet/minecraft/client/DeltaTracker;)V"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setupPerspective(FFFFF)V")
   )
   private void zenith$aspect(Camera camera, float f, float f2, float f3, float f4, float f5) {
      org.zenith.client.events.ProjectionMatrixEvent event = new org.zenith.client.events.ProjectionMatrixEvent();
      EventManager.call(event);
      this.setupPerspective(f, f2, f3, event.isCancelled() ? event.getFloatValue() * f5 : f4, f5);
   }

   @Shadow
   protected abstract void setupPerspective(float f, float f2, float f3, float f4, float f5);
}

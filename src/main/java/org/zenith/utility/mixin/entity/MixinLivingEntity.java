package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.EntityPushEvent;
import org.zenith.client.events.JumpEvent;
import org.zenith.client.modules.impl.movement.AntiLevitationModule;
import org.zenith.client.modules.impl.movement.ElytraBoosterModule;
import org.zenith.client.modules.impl.movement.ElytraTargetModule;
import org.zenith.client.modules.impl.movement.GrimGlideModule;
import org.zenith.client.modules.impl.render.SwingAnimationModule;
import org.zenith.utility.misc.ServerPositionTracker;

@Mixin({LivingEntity.class})
public class MixinLivingEntity implements MinecraftAccess, ServerPositionTracker {
   @Unique
   public float safeYaw = 0.0F;
   @Unique
   public float safePitch = 0.0F;
   @Unique
   private double serverX;
   @Unique
   private double serverY;
   @Unique
   private double serverZ;
   @Unique
   public double prevServerX;
   @Unique
   public double prevServerY;
   @Unique
   public double prevServerZ;
   @Unique
   private static final long SERVER_DELTA_MAX_AGE = 4L;
   @Unique
   private static final double SERVER_DELTA_MAX_SQ = 100.0;
   @Unique
   private boolean serverPosTracked;
   @Unique
   private long serverPosTime;

   @Inject(
      method = {"jumpFromGround()V"},
      at = {@At("RETURN")}
   )
   public void replaceMovePacketPitche3(CallbackInfo callbackInfo) {
      if ((Object)this == mc.player) {
         EventManager.call(new JumpEvent());
      }
   }

   @ModifyVariable(
      method = {"jumpFromGround()V"},
      at = @At("STORE"),
      ordinal = 1
   )
   private float hookJumpSprintRadians(float f) {
      if ((Object)this != mc.player) {
         return f;
      } else {
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return currentRotation == null ? f : currentRotation.getFloatValue() * (float) (Math.PI / 180.0);
      }
   }

   @Inject(
      method = {"aiStep()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V",
         shift = Shift.AFTER
      )}
   )
   public void gownso(CallbackInfo callbackInfo) {
      if ((Object)this == mc.player && Zenith.getInstance().getRotationManager().getCurrentRotation() != null) {
         mc.player.setYRot(this.safeYaw);
         mc.player.setXRot(this.safePitch);
      }
   }

   @Inject(
      method = {"aiStep()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"
      )}
   )
   public void replaceMo(CallbackInfo callbackInfo) {
      if ((Object)this == mc.player) {
         mc.player.setDeltaMovement(ElytraBoosterModule.INSTANCE.method1769(mc.player.getDeltaMovement()));
         if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null) {
            Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
            this.safeYaw = mc.player.getYRot();
            this.safePitch = mc.player.getXRot();
            mc.player.setYRot(currentRotation.getFloatValue());
            mc.player.setXRot(currentRotation.getFloatValue2());
         }
      }
   }

   @Inject(
      method = {"updateFallFlyingMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void hookLightningGrimPlayerTick(Vec3 vec, CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
      if ((Object)this == Minecraft.getInstance().player) {
         RotationManager rotationManager = Zenith.getInstance().getRotationManager();
         Rotation currentRotation = rotationManager.getCurrentRotation();
         Vec3 lookAngle = currentRotation == null ? Minecraft.getInstance().player.getLookAngle() : currentRotation.toDirection();
         Rotation rotation2 = rotationManager.getRotation2();
         Vec3 vec2 = rotation2 == null ? lookAngle : rotation2.toDirection();
         Vec3 returnValue = ElytraBoosterModule.INSTANCE.method1770(vec, (Vec3)callbackInfoReturnable.getReturnValue(), lookAngle, vec2);
         returnValue = GrimGlideModule.INSTANCE.method5976(vec, returnValue, lookAngle);
         returnValue = ElytraTargetModule.INSTANCE.method5249(vec, returnValue, lookAngle);
         callbackInfoReturnable.setReturnValue(returnValue);
      }
   }

   @ModifyExpressionValue(
      method = {"updateFallFlyingMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getXRot()F"
      )}
   )
   private float hookModifyFallFlyingPitch(float f) {
      if ((Object)this != Minecraft.getInstance().player) {
         return f;
      } else {
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return currentRotation == null ? f : currentRotation.getFloatValue2();
      }
   }

   @ModifyExpressionValue(
      method = {"updateFallFlyingMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
      )}
   )
   private Vec3 hookModifyFallFlyingRotationVector(Vec3 vec) {
      if ((Object)this != Minecraft.getInstance().player) {
         return vec;
      } else {
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return currentRotation == null ? vec : currentRotation.toDirection();
      }
   }

   @ModifyReturnValue(
      method = {"getModifiedSwingDuration(Lnet/minecraft/world/item/component/SwingAnimation;)I"},
      at = {@At("RETURN")}
   )
   private int modifySwingDuration(int n) {
      SwingAnimationModule swingAnimationModule = SwingAnimationModule.INSTANCE;
      return (LivingEntity)(Object)this == Minecraft.getInstance().player && swingAnimationModule.active() ? swingAnimationModule.method3269(n) : n;
   }

   @Inject(
      method = {"die(Lnet/minecraft/world/damagesource/DamageSource;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onDead(DamageSource damageSource, CallbackInfo callbackInfo) {
      EventManager.call(new EntityDeathEvent((LivingEntity)(Object)this));
   }

   @Inject(
      method = {"isPushable()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isPushable(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      EntityPushEvent event = new EntityPushEvent(EntityPushEvent.Type.COLLISION);
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   // Скрываем эффект только в расчёте движения: серверный эффект и остальная логика остаются нетронутыми.
   @ModifyExpressionValue(
      method = "travelInAir(Lnet/minecraft/world/phys/Vec3;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEffect(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/effect/MobEffectInstance;")
   )
   private MobEffectInstance ignoreLevitationMovement(MobEffectInstance effect) {
      return (Object)this == mc.player && AntiLevitationModule.INSTANCE.shouldIgnoreEffect() ? null : effect;
   }

   @Inject(method = "travelInAir(Lnet/minecraft/world/phys/Vec3;)V", at = @At("RETURN"))
   private void limitLevitationMovement(Vec3 input, CallbackInfo callbackInfo) {
      if ((Object)this == mc.player) {
         AntiLevitationModule.INSTANCE.limitVerticalMovement();
      }
   }

   @Override
   public double zenithDLC$getPrevServerX() {
      return this.prevServerX;
   }

   @Override
   public double zenithDLC$getPrevServerY() {
      return this.prevServerY;
   }

   @Override
   public double zenithDLC$getPrevServerZ() {
      return this.prevServerZ;
   }

   @Override
   public void zenithDLC$trackServerPos(double prevServerX, double prevServerY, double prevServerZ) {
      if (this.serverPosTracked) {
         this.prevServerX = this.serverX;
         this.prevServerY = this.serverY;
         this.prevServerZ = this.serverZ;
      } else {
         this.prevServerX = prevServerX;
         this.prevServerY = prevServerY;
         this.prevServerZ = prevServerZ;
         this.serverPosTracked = true;
      }

      this.serverX = prevServerX;
      this.serverY = prevServerY;
      this.serverZ = prevServerZ;
      this.serverPosTime = ((LivingEntity)(Object)this).level().getGameTime();
   }

   @Override
   public Vec3 zenithDLC$getServerDelta() {
      if (this.serverPosTracked && ((LivingEntity)(Object)this).level().getGameTime() - this.serverPosTime <= 4L) {
         Vec3 vec = new Vec3(this.serverX - this.prevServerX, this.serverY - this.prevServerY, this.serverZ - this.prevServerZ);
         return vec.lengthSqr() > 100.0 ? Vec3.ZERO : vec;
      } else {
         return Vec3.ZERO;
      }
   }
}

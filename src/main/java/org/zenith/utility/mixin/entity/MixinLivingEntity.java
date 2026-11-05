package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
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
import org.zenith.client.modules.impl.movement.ElytraBoosterModule;
import org.zenith.client.modules.impl.movement.ElytraTargetModule;
import org.zenith.client.modules.impl.movement.GrimGlideModule;
import org.zenith.client.modules.impl.render.SwingAnimationModule;
import org.zenith.utility.misc.Handler6;

@Mixin({LivingEntity.class})
public class MixinLivingEntity implements MinecraftAccess, Handler6 {
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
   public void replaceMovePacketPitche3(CallbackInfo var1) {
      if ((Object)this == mc.player) {
         EventManager.call(new JumpEvent());
      }
   }

   @ModifyVariable(
      method = {"jumpFromGround()V"},
      at = @At("STORE"),
      ordinal = 1
   )
   private float hookJumpSprintRadians(float var1) {
      if ((Object)this != mc.player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.getFloatValue() * (float) (Math.PI / 180.0);
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
   public void gownso(CallbackInfo var1) {
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
   public void replaceMo(CallbackInfo var1) {
      if ((Object)this == mc.player) {
         mc.player.setDeltaMovement(ElytraBoosterModule.INSTANCE.method1769(mc.player.getDeltaMovement()));
         if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null) {
            Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
            this.safeYaw = mc.player.getYRot();
            this.safePitch = mc.player.getXRot();
            mc.player.setYRot(var2.getFloatValue());
            mc.player.setXRot(var2.getFloatValue2());
         }
      }
   }

   @Inject(
      method = {"updateFallFlyingMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void hookLightningGrimPlayerTick(Vec3 var1, CallbackInfoReturnable<Vec3> var2) {
      if ((Object)this == Minecraft.getInstance().player) {
         RotationManager var3 = Zenith.getInstance().getRotationManager();
         Rotation var4 = var3.getCurrentRotation();
         Vec3 var5 = var4 == null ? Minecraft.getInstance().player.getLookAngle() : var4.method3135();
         Rotation var6 = var3.getRotation2();
         Vec3 var7 = var6 == null ? var5 : var6.method3135();
         Vec3 var8 = ElytraBoosterModule.INSTANCE.method1770(var1, (Vec3)var2.getReturnValue(), var5, var7);
         var8 = GrimGlideModule.INSTANCE.method5976(var1, var8, var5);
         var8 = ElytraTargetModule.INSTANCE.method5249(var1, var8, var5);
         var2.setReturnValue(var8);
      }
   }

   @ModifyExpressionValue(
      method = {"updateFallFlyingMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getXRot()F"
      )}
   )
   private float hookModifyFallFlyingPitch(float var1) {
      if ((Object)this != Minecraft.getInstance().player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.getFloatValue2();
      }
   }

   @ModifyExpressionValue(
      method = {"updateFallFlyingMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
      )}
   )
   private Vec3 hookModifyFallFlyingRotationVector(Vec3 var1) {
      if ((Object)this != Minecraft.getInstance().player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.method3135();
      }
   }

   @ModifyReturnValue(
      method = {"getModifiedSwingDuration(Lnet/minecraft/world/item/component/SwingAnimation;)I"},
      at = {@At("RETURN")}
   )
   private int modifySwingDuration(int var1) {
      SwingAnimationModule var2 = SwingAnimationModule.INSTANCE;
      return (LivingEntity)(Object)this == Minecraft.getInstance().player && var2.active() ? var2.method3269(var1) : var1;
   }

   @Inject(
      method = {"die(Lnet/minecraft/world/damagesource/DamageSource;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onDead(DamageSource var1, CallbackInfo var2) {
      EventManager.call(new EntityDeathEvent((LivingEntity)(Object)this));
   }

   @Inject(
      method = {"isPushable()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isPushable(CallbackInfoReturnable<Boolean> var1) {
      EntityPushEvent var2 = new EntityPushEvent(EntityPushEvent.Type.COLLISION);
      EventManager.call(var2);
      if (var2.isCancelled()) {
         var1.setReturnValue(false);
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
   public void zenithDLC$trackServerPos(double var1, double var3, double var5) {
      if (this.serverPosTracked) {
         this.prevServerX = this.serverX;
         this.prevServerY = this.serverY;
         this.prevServerZ = this.serverZ;
      } else {
         this.prevServerX = var1;
         this.prevServerY = var3;
         this.prevServerZ = var5;
         this.serverPosTracked = true;
      }

      this.serverX = var1;
      this.serverY = var3;
      this.serverZ = var5;
      this.serverPosTime = ((LivingEntity)(Object)this).level().getGameTime();
   }

   @Override
   public Vec3 zenithDLC$getServerDelta() {
      if (this.serverPosTracked && ((LivingEntity)(Object)this).level().getGameTime() - this.serverPosTime <= 4L) {
         Vec3 var1 = new Vec3(this.serverX - this.prevServerX, this.serverY - this.prevServerY, this.serverZ - this.prevServerZ);
         return var1.lengthSqr() > 100.0 ? Vec3.ZERO : var1;
      } else {
         return Vec3.ZERO;
      }
   }
}

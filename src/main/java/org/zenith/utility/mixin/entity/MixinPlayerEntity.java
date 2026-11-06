package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.EntityPushEvent;
import org.zenith.client.modules.impl.misc.FastBreakModule;
import org.zenith.client.modules.impl.movement.AutoSprintModule;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({Player.class})
public abstract class MixinPlayerEntity {
   @Inject(
      method = {"isPushedByFluid()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isPushedByFluids(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      EntityPushEvent event = new EntityPushEvent(EntityPushEvent.Type.WATER);
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Redirect(
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
      )
   )
   public Vec3 fixSwing(Player player) {
      return (Object)this != Minecraft.getInstance().player ? player.getLookAngle() : Zenith.getInstance().getRotationManager().getEffectiveRotation().toDirection();
   }

   @Redirect(
      method = {"causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private Vec3 hookSlowVelocity(Vec3 vec, double xScale, double yScale, double zScale) {
      if ((Object)this == Minecraft.getInstance().player && AutoSprintModule.INSTANCE.isEnabled() && AutoSprintModule.INSTANCE.method6417()) {
         xScale = zScale = AutoSprintModule.INSTANCE.method6419();
      }

      return vec.multiply(xScale, yScale, zScale);
   }

   @WrapWithCondition(
      method = {"causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V",
         ordinal = 0
      )}
   )
   private boolean hookSlowVelocity(Player player, boolean flag) {
      return (Object)this != Minecraft.getInstance().player ? true : AutoSprintModule.INSTANCE.isEnabled() && AutoSprintModule.INSTANCE.method6417() || flag;
   }

   @ModifyExpressionValue(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"
      )}
   )
   private boolean hookSlowVelocity(boolean flag) {
      return (Object)this == Minecraft.getInstance().player && AutoSprintModule.INSTANCE.isEnabled() && AutoSprintModule.INSTANCE.method6417()
         ? Minecraft.getInstance().player.isSprinting()
         : flag;
   }

   @Inject(
      method = {"blockInteractionRange()D"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void reachBlock(CallbackInfoReturnable<Double> callbackInfoReturnable) {
      if ((Object)this == Minecraft.getInstance().player && ModuleHookDefinitions.class122.active()) {
         callbackInfoReturnable.setReturnValue(ModuleHookDefinitions.method6118(ModuleHookDefinitions.class122, (Double)callbackInfoReturnable.getReturnValue()));
      }
   }

   @Inject(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At("RETURN")}
   )
   public void eventAttackEnd(Entity entity, CallbackInfo callbackInfo) {
      EventManager.call(new AttackEvent(entity, AttackEvent.Type.POST));
   }

   @Inject(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At("HEAD")}
   )
   public void eventAttackHEad(Entity entity, CallbackInfo callbackInfo) {
      EventManager.call(new AttackEvent(entity, AttackEvent.Type.PRE));
   }

   @Inject(
      method = {"entityInteractionRange()D"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void reach(CallbackInfoReturnable<Double> callbackInfoReturnable) {
      if ((Object)this == Minecraft.getInstance().player && ModuleHookDefinitions.class12.active()) {
         callbackInfoReturnable.setReturnValue(ModuleHookDefinitions.method6118(ModuleHookDefinitions.class12, (Double)callbackInfoReturnable.getReturnValue()));
      }
   }

   @ModifyExpressionValue(
      method = {"causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getYRot()F"
      )}
   )
   private float hookFixRotation(float f) {
      if ((Object)this != Minecraft.getInstance().player) {
         return f;
      } else {
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return currentRotation == null ? f : currentRotation.getFloatValue();
      }
   }

   @ModifyConstant(
      method = {"getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"},
      constant = {@Constant(
         floatValue = 5.0F
      )}
   )
   private float zenith$airBreakDivisor(float f) {
      return (Object)this != Minecraft.getInstance().player ? f : FastBreakModule.INSTANCE.method5660(f);
   }
}

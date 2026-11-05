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
   public void isPushedByFluids(CallbackInfoReturnable<Boolean> var1) {
      EntityPushEvent var2 = new EntityPushEvent(EntityPushEvent.Type.WATER);
      EventManager.call(var2);
      if (var2.isCancelled()) {
         var1.setReturnValue(false);
      }
   }

   @Redirect(
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
      )
   )
   public Vec3 fixSwing(Player var1) {
      return (Object)this != Minecraft.getInstance().player ? var1.getLookAngle() : Zenith.getInstance().getRotationManager().method7844().method3135();
   }

   @Redirect(
      method = {"causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private Vec3 hookSlowVelocity(Vec3 var1, double var2, double var4, double var6) {
      if ((Object)this == Minecraft.getInstance().player && AutoSprintModule.INSTANCE.isEnabled() && AutoSprintModule.INSTANCE.method6417()) {
         var2 = var6 = AutoSprintModule.INSTANCE.method6419();
      }

      return var1.multiply(var2, var4, var6);
   }

   @WrapWithCondition(
      method = {"causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V",
         ordinal = 0
      )}
   )
   private boolean hookSlowVelocity(Player var1, boolean var2) {
      return (Object)this != Minecraft.getInstance().player ? true : AutoSprintModule.INSTANCE.isEnabled() && AutoSprintModule.INSTANCE.method6417() || var2;
   }

   @ModifyExpressionValue(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"
      )}
   )
   private boolean hookSlowVelocity(boolean var1) {
      return (Object)this == Minecraft.getInstance().player && AutoSprintModule.INSTANCE.isEnabled() && AutoSprintModule.INSTANCE.method6417()
         ? Minecraft.getInstance().player.isSprinting()
         : var1;
   }

   @Inject(
      method = {"blockInteractionRange()D"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void reachBlock(CallbackInfoReturnable<Double> var1) {
      if ((Object)this == Minecraft.getInstance().player && ModuleHookDefinitions.class122.active()) {
         var1.setReturnValue(ModuleHookDefinitions.method6118(ModuleHookDefinitions.class122, (Double)var1.getReturnValue()));
      }
   }

   @Inject(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At("RETURN")}
   )
   public void eventAttackEnd(Entity var1, CallbackInfo var2) {
      EventManager.call(new AttackEvent(var1, AttackEvent.Type.POST));
   }

   @Inject(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At("HEAD")}
   )
   public void eventAttackHEad(Entity var1, CallbackInfo var2) {
      EventManager.call(new AttackEvent(var1, AttackEvent.Type.PRE));
   }

   @Inject(
      method = {"entityInteractionRange()D"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void reach(CallbackInfoReturnable<Double> var1) {
      if ((Object)this == Minecraft.getInstance().player && ModuleHookDefinitions.class12.active()) {
         var1.setReturnValue(ModuleHookDefinitions.method6118(ModuleHookDefinitions.class12, (Double)var1.getReturnValue()));
      }
   }

   @ModifyExpressionValue(
      method = {"causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getYRot()F"
      )}
   )
   private float hookFixRotation(float var1) {
      if ((Object)this != Minecraft.getInstance().player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.getFloatValue();
      }
   }

   @ModifyConstant(
      method = {"getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"},
      constant = {@Constant(
         floatValue = 5.0F
      )}
   )
   private float zenith$airBreakDivisor(float var1) {
      return (Object)this != Minecraft.getInstance().player ? var1 : FastBreakModule.INSTANCE.method5660(var1);
   }
}

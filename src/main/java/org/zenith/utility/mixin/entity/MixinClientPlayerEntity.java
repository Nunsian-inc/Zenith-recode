package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.events.Event;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.client.events.CloseHandledScreenEvent;
import org.zenith.client.events.EntityPushEvent;
import org.zenith.client.events.NoSlowEvent;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.SprintPacketEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.misc.NoFriendDamageModule;
import org.zenith.client.modules.impl.misc.ViaFabricFixModule;

@Mixin({LocalPlayer.class})
public abstract class MixinClientPlayerEntity extends AbstractClientPlayer {
   @Shadow
   public ClientInput input;
   @Shadow
   @Final
   public Minecraft minecraft;
   @Unique
   private float zenith$savedYaw;
   @Unique
   private float zenith$savedPitch;
   @Unique
   private boolean zenith$rotationPushed;

   public MixinClientPlayerEntity(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
   }

   @Shadow
   public abstract void sendIsSprintingIfNeeded();

   @Shadow
   public abstract void updateAutoJump(float var1, float var2);

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   public void tick(CallbackInfo var1) {
      dispatchEvent(new UpdateEvent());
   }

   @Inject(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V",
         shift = Shift.AFTER
      )}
   )
   public void tickEnd(CallbackInfo var1) {
      dispatchEvent(new PlayerTickEvent());
   }

   @Inject(
      method = {"sendPosition()V"},
      at = {@At("RETURN")}
   )
   public void motion(CallbackInfo var1) {
      dispatchEvent(new PostMotionEvent());
   }

   @Redirect(
      method = {"sendPosition()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;sendIsSprintingIfNeeded()V"
      )
   )
   public void invokeSprintUpdate(LocalPlayer var1) {
      SprintPacketEvent var2 = new SprintPacketEvent();
      dispatchEvent(var2);
      if (!var2.isCancelled()) {
         this.sendIsSprintingIfNeeded();
      }
   }

   @Inject(
      method = {"isSprintingPossible(Z)Z"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void zenith$canSprint(boolean var1, CallbackInfoReturnable<Boolean> var2) {
      SprintUpdateEvent var3 = new SprintUpdateEvent((Boolean)var2.getReturnValue());
      dispatchEvent(var3);
      var2.setReturnValue(var3.isCancelled() || var3.isFlag2());
   }

   @ModifyExpressionValue(
      method = {"shouldStopRunSprinting()Z"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/player/LocalPlayer;minorHorizontalCollision:Z"
      )}
   )
   private boolean zenith$legacySoftCollision(boolean var1) {
      return ViaFabricFixModule.INSTANCE.isEnabled() ? false : var1;
   }

   @Inject(
      method = {"moveTowardsClosestSpace(DD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pushOutOfBlocks(double var1, double var3, CallbackInfo var5) {
      EntityPushEvent var6 = new EntityPushEvent(EntityPushEvent.Type.BLOCK);
      dispatchEvent(var6);
      if (var6.isCancelled()) {
         var5.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"sendPosition()V", "tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F"
      )}
   )
   private float hookSilentRotationYaw(float var1) {
      Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return var2 == null ? var1 : var2.getFloatValue();
   }

   @ModifyExpressionValue(
      method = {"sendPosition()V", "tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F"
      )}
   )
   private float hookSilentRotationPitch(float var1) {
      Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return var2 == null ? var1 : var2.getFloatValue2();
   }

   @Redirect(
      method = {"modifyInput(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
      ),
      require = 0
   )
   private boolean onIsUsingItemRedirect(LocalPlayer var1) {
      if (var1.isUsingItem()) {
         NoSlowEvent var2 = new NoSlowEvent();
         dispatchEvent(var2);
         return var1.isUsingItem() && var1.getVehicle() == null && !var2.isCancelled();
      } else {
         return var1.isUsingItem() && var1.getVehicle() == null;
      }
   }

   @Inject(
      method = {"closeContainer()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void closeHandledScreenHook(CallbackInfo var1) {
      CloseHandledScreenEvent var2 = new CloseHandledScreenEvent(this.minecraft.gui.screen());
      dispatchEvent(var2);
      if (var2.isCancelled()) {
         var1.cancel();
      }
   }

   @Inject(
      method = {"move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/AbstractClientPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
      )},
      cancellable = true
   )
   public void onMoveHook(MoverType var1, Vec3 var2, CallbackInfo var3) {
      PlayerMoveEvent var4 = new PlayerMoveEvent(var2);
      dispatchEvent(var4);
      double var5 = this.getX();
      double var7 = this.getZ();
      super.move(var1, var4.getVec3d());
      float var9 = (float)(this.getX() - var5);
      float var10 = (float)(this.getZ() - var7);
      this.updateAutoJump(var9, var10);
      this.addWalkedDistance(Mth.length(var9, var10) * 0.6F);
      var3.cancel();
   }

   private static void dispatchEvent(Event var0) {
      EventManager.call(var0);
   }

   @Inject(
      method = {"raycastHitResult(FLnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At("HEAD")}
   )
   private void zenith$pushCrosshairRotation(float var1, Entity var2, CallbackInfoReturnable<HitResult> var3) {
      Rotation var4 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      if (var4 != null && var2 == Minecraft.getInstance().player) {
         this.zenith$savedYaw = var2.getYRot();
         this.zenith$savedPitch = var2.getXRot();
         this.zenith$rotationPushed = true;
         var2.setYRot(var4.getFloatValue());
         var2.setXRot(var4.getFloatValue2());
      }
   }

   @Inject(
      method = {"raycastHitResult(FLnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At("RETURN")}
   )
   private void zenith$popCrosshairRotation(float var1, Entity var2, CallbackInfoReturnable<HitResult> var3) {
      if (this.zenith$rotationPushed) {
         this.zenith$rotationPushed = false;
         var2.setYRot(this.zenith$savedYaw);
         var2.setXRot(this.zenith$savedPitch);
      }
   }

   @ModifyExpressionValue(
      method = {"pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"
      )}
   )
   private static Vec3 zenith$crosshairRotationVector(Vec3 var0, Entity var1, double var2, double var4, float var6) {
      if (var1 != Minecraft.getInstance().player) {
         return var0;
      } else {
         Rotation var7 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var7 != null ? var7.method3135() : var0;
      }
   }

   @ModifyExpressionValue(
      method = {"pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/world/entity/EntitySelector;CAN_BE_PICKED:Ljava/util/function/Predicate;"
      )}
   )
   private static Predicate<Entity> zenith$noFriendDamage(Predicate<Entity> var0) {
      return !NoFriendDamageModule.INSTANCE.isEnabled() ? var0 : var1 -> var0.test(var1) && !Zenith.getInstance().getFriends().method7626(var1);
   }
}

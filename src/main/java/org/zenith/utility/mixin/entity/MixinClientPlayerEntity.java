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

   public MixinClientPlayerEntity(ClientLevel level, GameProfile gameProfile) {
      super(level, gameProfile);
   }

   @Shadow
   public abstract void sendIsSprintingIfNeeded();

   @Shadow
   public abstract void updateAutoJump(float f, float y);

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   public void tick(CallbackInfo callbackInfo) {
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
   public void tickEnd(CallbackInfo callbackInfo) {
      dispatchEvent(new PlayerTickEvent());
   }

   @Inject(
      method = {"sendPosition()V"},
      at = {@At("RETURN")}
   )
   public void motion(CallbackInfo callbackInfo) {
      dispatchEvent(new PostMotionEvent());
   }

   @Redirect(
      method = {"sendPosition()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;sendIsSprintingIfNeeded()V"
      )
   )
   public void invokeSprintUpdate(LocalPlayer player) {
      SprintPacketEvent event = new SprintPacketEvent();
      dispatchEvent(event);
      if (!event.isCancelled()) {
         this.sendIsSprintingIfNeeded();
      }
   }

   @Inject(
      method = {"isSprintingPossible(Z)Z"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void zenith$canSprint(boolean flag, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      SprintUpdateEvent event = new SprintUpdateEvent((Boolean)callbackInfoReturnable.getReturnValue());
      dispatchEvent(event);
      callbackInfoReturnable.setReturnValue(event.isCancelled() || event.isFlag2());
   }

   @ModifyExpressionValue(
      method = {"shouldStopRunSprinting()Z"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/player/LocalPlayer;minorHorizontalCollision:Z"
      )}
   )
   private boolean zenith$legacySoftCollision(boolean flag) {
      return ViaFabricFixModule.INSTANCE.isEnabled() ? false : flag;
   }

   @Inject(
      method = {"moveTowardsClosestSpace(DD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pushOutOfBlocks(double d, double d2, CallbackInfo callbackInfo) {
      EntityPushEvent event = new EntityPushEvent(EntityPushEvent.Type.BLOCK);
      dispatchEvent(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"sendPosition()V", "tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F"
      )}
   )
   private float hookSilentRotationYaw(float f) {
      Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return currentRotation == null ? f : currentRotation.getFloatValue();
   }

   @ModifyExpressionValue(
      method = {"sendPosition()V", "tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F"
      )}
   )
   private float hookSilentRotationPitch(float f) {
      Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return currentRotation == null ? f : currentRotation.getFloatValue2();
   }

   @Redirect(
      method = {"modifyInput(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
      ),
      require = 0
   )
   private boolean onIsUsingItemRedirect(LocalPlayer player) {
      if (player.isUsingItem()) {
         NoSlowEvent event = new NoSlowEvent();
         dispatchEvent(event);
         return player.isUsingItem() && player.getVehicle() == null && !event.isCancelled();
      } else {
         return player.isUsingItem() && player.getVehicle() == null;
      }
   }

   @Inject(
      method = {"closeContainer()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void closeHandledScreenHook(CallbackInfo callbackInfo) {
      CloseHandledScreenEvent event = new CloseHandledScreenEvent(this.minecraft.gui.screen());
      dispatchEvent(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
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
   public void onMoveHook(MoverType moverType, Vec3 vec3d, CallbackInfo callbackInfo) {
      PlayerMoveEvent event = new PlayerMoveEvent(vec3d);
      dispatchEvent(event);
      double x = this.getX();
      double z = this.getZ();
      super.move(moverType, event.getVec3d());
      float f = (float)(this.getX() - x);
      float y = (float)(this.getZ() - z);
      this.updateAutoJump(f, y);
      this.addWalkedDistance(Mth.length(f, y) * 0.6F);
      callbackInfo.cancel();
   }

   private static void dispatchEvent(Event event) {
      EventManager.call(event);
   }

   @Inject(
      method = {"raycastHitResult(FLnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At("HEAD")}
   )
   private void zenith$pushCrosshairRotation(float f, Entity entity, CallbackInfoReturnable<HitResult> callbackInfoReturnable) {
      Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
      if (currentRotation != null && entity == Minecraft.getInstance().player) {
         this.zenith$savedYaw = entity.getYRot();
         this.zenith$savedPitch = entity.getXRot();
         this.zenith$rotationPushed = true;
         entity.setYRot(currentRotation.getFloatValue());
         entity.setXRot(currentRotation.getFloatValue2());
      }
   }

   @Inject(
      method = {"raycastHitResult(FLnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At("RETURN")}
   )
   private void zenith$popCrosshairRotation(float f, Entity entity, CallbackInfoReturnable<HitResult> callbackInfoReturnable) {
      if (this.zenith$rotationPushed) {
         this.zenith$rotationPushed = false;
         entity.setYRot(this.zenith$savedYaw);
         entity.setXRot(this.zenith$savedPitch);
      }
   }

   @ModifyExpressionValue(
      method = {"pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"
      )}
   )
   private static Vec3 zenith$crosshairRotationVector(Vec3 vec, Entity entity, double d, double d2, float f) {
      if (entity != Minecraft.getInstance().player) {
         return vec;
      } else {
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return currentRotation != null ? currentRotation.toDirection() : vec;
      }
   }

   @ModifyExpressionValue(
      method = {"pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/world/entity/EntitySelector;CAN_BE_PICKED:Ljava/util/function/Predicate;"
      )}
   )
   private static Predicate<Entity> zenith$noFriendDamage(Predicate<Entity> predicate) {
      return !NoFriendDamageModule.INSTANCE.isEnabled() ? predicate : player -> predicate.test(player) && !Zenith.getInstance().getFriends().method7626(player);
   }
}

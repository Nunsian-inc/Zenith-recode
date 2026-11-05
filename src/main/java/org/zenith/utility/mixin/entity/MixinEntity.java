package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import java.util.Optional;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.MovementCorrectionEvent;
import org.zenith.client.modules.impl.render.AntiInvisibleModule;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.utility.game.Util4;
import org.zenith.utility.misc.Handler6;

@Mixin({Entity.class})
public abstract class MixinEntity implements MinecraftAccess {
   @Shadow
   private float xRot;
   @Shadow
   public float xRotO;
   @Shadow
   public float yRotO;

   @Shadow
   public abstract void setXRot(float xRot);

   @Shadow
   public abstract void setYRot(float yRot);

   @Shadow
   public abstract float getYRot();

   @Shadow
   public abstract float getXRot();

   @Shadow
   public abstract String getScoreboardName();

   @Inject(
      method = {"moveOrInterpolateTo(Lnet/minecraft/world/entity/PositionPath;FFZ)V"},
      at = {@At("HEAD")}
   )
   private void zenith$trackServerPos(net.minecraft.world.entity.PositionPath positionPath, float f, float f2, boolean flag, CallbackInfo callbackInfo) {
      if (positionPath != null && (Object)this instanceof Handler6 handler6) {
         Vec3 endPosition = positionPath.endPosition();
         handler6.zenithDLC$trackServerPos(endPosition.x, endPosition.y, endPosition.z);
      }
   }

   @Inject(
      method = {"isInvisible()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void bypassSpeed(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (AntiInvisibleModule.INSTANCE.isEnabled() && ((Object)this == mc.player || Zenith.getInstance().getFriends().method7627(this.getScoreboardName()))) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Redirect(
      method = {"moveRelative(FLnet/minecraft/world/phys/Vec3;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getYRot()F"
      )
   )
   public float movementCorrection(Entity entity) {
      return entity instanceof LocalPlayer && Zenith.getInstance().getRotationManager().getCurrentRotation() != null
         ? Zenith.getInstance().getRotationManager().getCurrentRotation().getFloatValue()
         : entity.getYRot();
   }

   @Inject(
      method = {"moveRelative(FLnet/minecraft/world/phys/Vec3;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$movePost(float speed, Vec3 vec3d, CallbackInfo callbackInfo) {
      if (mc.player != null && (Object)this == mc.player) {
         EventManager.call(new MovementCorrectionEvent(speed, vec3d));
      }
   }

   @Inject(
      method = {"isCurrentlyGlowing()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$shaderEspGlowing(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      Entity entity = (Entity)(Object)this;
      if (Util4.method1278(entity.getId())) {
         callbackInfoReturnable.setReturnValue(true);
      } else {
         ShaderESPModule shaderESPModule = ShaderESPModule.INSTANCE;
         if (shaderESPModule != null && shaderESPModule.isEnabled() && shaderESPModule.method4584(entity)) {
            callbackInfoReturnable.setReturnValue(true);
         } else {
            if (PredictionsModule.INSTANCE.method2911(entity)) {
               callbackInfoReturnable.setReturnValue(true);
            }
         }
      }
   }
}

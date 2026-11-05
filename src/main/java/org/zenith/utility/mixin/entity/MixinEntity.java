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
   public abstract void setXRot(float var1);

   @Shadow
   public abstract void setYRot(float var1);

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
   private void zenith$trackServerPos(net.minecraft.world.entity.PositionPath var1, float var2, float var3, boolean var7, CallbackInfo var4) {
      if (var1 != null && (Object)this instanceof Handler6 var5) {
         Vec3 var6 = var1.endPosition();
         var5.zenithDLC$trackServerPos(var6.x, var6.y, var6.z);
      }
   }

   @Inject(
      method = {"isInvisible()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void bypassSpeed(CallbackInfoReturnable<Boolean> var1) {
      if (AntiInvisibleModule.INSTANCE.isEnabled() && ((Object)this == mc.player || Zenith.getInstance().getFriends().method7627(this.getScoreboardName()))) {
         var1.setReturnValue(false);
      }
   }

   @Redirect(
      method = {"moveRelative(FLnet/minecraft/world/phys/Vec3;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getYRot()F"
      )
   )
   public float movementCorrection(Entity var1) {
      return var1 instanceof LocalPlayer && Zenith.getInstance().getRotationManager().getCurrentRotation() != null
         ? Zenith.getInstance().getRotationManager().getCurrentRotation().getFloatValue()
         : var1.getYRot();
   }

   @Inject(
      method = {"moveRelative(FLnet/minecraft/world/phys/Vec3;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$movePost(float var1, Vec3 var2, CallbackInfo var3) {
      if (mc.player != null && (Object)this == mc.player) {
         EventManager.call(new MovementCorrectionEvent(var1, var2));
      }
   }

   @Inject(
      method = {"isCurrentlyGlowing()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$shaderEspGlowing(CallbackInfoReturnable<Boolean> var1) {
      Entity var2 = (Entity)(Object)this;
      if (Util4.method1278(var2.getId())) {
         var1.setReturnValue(true);
      } else {
         ShaderESPModule var3 = ShaderESPModule.INSTANCE;
         if (var3 != null && var3.isEnabled() && var3.method4584(var2)) {
            var1.setReturnValue(true);
         } else {
            if (PredictionsModule.INSTANCE.method2911(var2)) {
               var1.setReturnValue(true);
            }
         }
      }
   }
}

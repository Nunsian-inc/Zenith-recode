package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.BlindnessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.DarknessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.events.BackgroundRenderEvent;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.ModuleHookDefinitions;
import org.zenith.utility.render.Util26;

// 26.3: setupFog returns FogData (its colour is also the world clear colour) and computeFogColor writes into a vector.
// A custom fog colour is applied only when the fog UBO is written, so the clear colour stays vanilla like in 1.21.11.
@Mixin({FogRenderer.class})
public class MixinBackGroundRenderer {
   @Unique
   private static final String SETUP = "setupFog(Lnet/minecraft/client/Camera;ILnet/minecraft/client/DeltaTracker;FLnet/minecraft/client/multiplayer/ClientLevel;)Lnet/minecraft/client/renderer/fog/FogData;";
   @Unique
   private static Vector4f zenith$fogColorOverride;

   @Redirect(
      method = {SETUP, "computeFogColor"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;isApplicable(Lnet/minecraft/world/level/material/FogType;Lnet/minecraft/world/entity/Entity;)Z"
      )
   )
   private boolean zenith$skipEffectFog(FogEnvironment var1, FogType var2, Entity var3) {
      boolean var4 = var1 instanceof BlindnessFogEnvironment || var1 instanceof DarknessFogEnvironment;
      if (var4 && ModuleHookDefinitions.class128.active()) {
         String var5 = var1 instanceof DarknessFogEnvironment ? "darkness" : "blindness";
         if (ModuleHookDefinitions.class128.method1645(new ModuleHookDefinitions.Entry3(var5)).cancelled) {
            return false;
         }
      }

      return var1.isApplicable(var2, var3);
   }

   @Inject(method = {"computeFogColor"}, at = {@At("RETURN")})
   private void zenith$fogColor(Camera var2, float var3, ClientLevel var4, int var5, float var6, Vector4f var1, org.spongepowered.asm.mixin.injection.callback.CallbackInfo var9) {
      BackgroundRenderEvent var7 = new BackgroundRenderEvent();
      var7.setIntValue(pack(var1));
      EventManager.call(var7);
      if (var7.isCancelled()) {
         int var8 = var7.getColor();
         var1.set(ColorUtils.method6424(var8), ColorUtils.method6425(var8), ColorUtils.method6426(var8), ColorUtils.method6427(var8));
      }
   }

   @ModifyReturnValue(method = {SETUP}, at = {@At("RETURN")})
   private FogData zenith$fog(FogData var8) {
      zenith$fogColorOverride = null;
      BackgroundRenderEvent var9 = new BackgroundRenderEvent();
      var9.setIntValue(pack(var8.color));
      var9.setFloatValue2(var8.renderDistanceEnd);
      EventManager.call(var9);
      if (var9.isCancelled()) {
         float var10 = var9.getFloatValue();
         var8.environmentalStart = Float.MAX_VALUE;
         var8.environmentalEnd = Float.MAX_VALUE;
         var8.renderDistanceStart = 2.0F;
         var8.renderDistanceEnd = var10;
         var8.skyEnd = var10;
         var8.cloudEnd = var10;
         int var11 = var9.getColor();
         zenith$fogColorOverride = new Vector4f(ColorUtils.method6424(var11), ColorUtils.method6425(var11), ColorUtils.method6426(var11), ColorUtils.method6427(var11));
      }

      return var8;
   }

   @org.spongepowered.asm.mixin.injection.ModifyArg(
      method = {"updateBuffer(Lnet/minecraft/client/renderer/fog/FogData;)V"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"),
      index = 2
   )
   private Vector4f zenith$captureFog(Vector4f var1, @Local(argsOnly = true) FogData var2) {
      Vector4f var3 = zenith$fogColorOverride != null ? zenith$fogColorOverride : var1;
      Util26.method6982(var3, var2);
      return var3;
   }

   private static int pack(Vector4f var0) {
      return channel(var0.w) << 24 | channel(var0.x) << 16 | channel(var0.y) << 8 | channel(var0.z);
   }

   private static int channel(float var0) {
      return Math.clamp((long)((int)(var0 * 255.0F + 0.5F)), 0, 255);
   }
}

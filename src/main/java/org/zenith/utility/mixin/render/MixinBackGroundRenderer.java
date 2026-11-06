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
import org.zenith.utility.render.FogStateCache;

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
   private boolean zenith$skipEffectFog(FogEnvironment fogEnvironment, FogType fogType, Entity entity) {
      boolean flag = fogEnvironment instanceof BlindnessFogEnvironment || fogEnvironment instanceof DarknessFogEnvironment;
      if (flag && ModuleHookDefinitions.class128.active()) {
         String text = fogEnvironment instanceof DarknessFogEnvironment ? "darkness" : "blindness";
         if (ModuleHookDefinitions.class128.dispatch(new ModuleHookDefinitions.Entry3(text)).cancelled) {
            return false;
         }
      }

      return fogEnvironment.isApplicable(fogType, entity);
   }

   @Inject(method = {"computeFogColor"}, at = {@At("RETURN")})
   private void zenith$fogColor(Camera camera, float f, ClientLevel level, int n, float f2, Vector4f color, org.spongepowered.asm.mixin.injection.callback.CallbackInfo callbackInfo) {
      BackgroundRenderEvent event = new BackgroundRenderEvent();
      event.setIntValue(pack(color));
      EventManager.call(event);
      if (event.isCancelled()) {
         int intValue = event.getColor();
         color.set(ColorUtils.method6424(intValue), ColorUtils.method6425(intValue), ColorUtils.method6426(intValue), ColorUtils.method6427(intValue));
      }
   }

   @ModifyReturnValue(method = {SETUP}, at = {@At("RETURN")})
   private FogData zenith$fog(FogData fogData) {
      zenith$fogColorOverride = null;
      BackgroundRenderEvent event = new BackgroundRenderEvent();
      event.setIntValue(pack(fogData.color));
      event.setFloatValue2(fogData.renderDistanceEnd);
      EventManager.call(event);
      if (event.isCancelled()) {
         float floatValue = event.getFloatValue();
         fogData.environmentalStart = Float.MAX_VALUE;
         fogData.environmentalEnd = Float.MAX_VALUE;
         fogData.renderDistanceStart = 2.0F;
         fogData.renderDistanceEnd = floatValue;
         fogData.skyEnd = floatValue;
         fogData.cloudEnd = floatValue;
         int color = event.getColor();
         zenith$fogColorOverride = new Vector4f(ColorUtils.method6424(color), ColorUtils.method6425(color), ColorUtils.method6426(color), ColorUtils.method6427(color));
      }

      return fogData;
   }

   @org.spongepowered.asm.mixin.injection.ModifyArg(
      method = {"updateBuffer(Lnet/minecraft/client/renderer/fog/FogData;)V"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"),
      index = 2
   )
   private Vector4f zenith$captureFog(Vector4f vec, @Local(argsOnly = true) FogData fogData) {
      Vector4f zenith$fogColorOverride2 = zenith$fogColorOverride != null ? zenith$fogColorOverride : vec;
      FogStateCache.captureFog(zenith$fogColorOverride2, fogData);
      return zenith$fogColorOverride2;
   }

   private static int pack(Vector4f color) {
      return channel(color.w) << 24 | channel(color.x) << 16 | channel(color.y) << 8 | channel(color.z);
   }

   private static int channel(float x) {
      return Math.clamp((long)((int)(x * 255.0F + 0.5F)), 0, 255);
   }
}

package org.zenith.utility.mixin.optimization;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util29;

@Mixin({AvatarRenderer.class})
public class MixinOptPlayerEntityRenderer {
   @Inject(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V"},
      at = {@At("TAIL")}
   )
   private void zenith$farSkinLayers(Avatar avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo callbackInfo) {
      if (OptimizationFlags.flag12 && Util29.flag && !(avatarRenderState.distanceToCameraSq <= OptimizationFlags.doubleValue3)) {
         if (avatar != Minecraft.getInstance().player) {
            avatarRenderState.showHat = false;
            avatarRenderState.showJacket = false;
            avatarRenderState.showLeftSleeve = false;
            avatarRenderState.showRightSleeve = false;
            avatarRenderState.showLeftPants = false;
            avatarRenderState.showRightPants = false;
            if (!avatarRenderState.chestEquipment.has(DataComponents.GLIDER)) {
               avatarRenderState.showCape = false;
            }
         }
      }
   }
}

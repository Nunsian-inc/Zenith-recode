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
   private void zenith$farSkinLayers(Avatar var1, AvatarRenderState var2, float var3, CallbackInfo var4) {
      if (OptimizationFlags.flag12 && Util29.flag && !(var2.distanceToCameraSq <= OptimizationFlags.doubleValue3)) {
         if (var1 != Minecraft.getInstance().player) {
            var2.showHat = false;
            var2.showJacket = false;
            var2.showLeftSleeve = false;
            var2.showRightSleeve = false;
            var2.showLeftPants = false;
            var2.showRightPants = false;
            if (!var2.chestEquipment.has(DataComponents.GLIDER)) {
               var2.showCape = false;
            }
         }
      }
   }
}

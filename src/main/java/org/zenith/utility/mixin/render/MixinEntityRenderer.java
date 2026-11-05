package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.game.Util4;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {
   @ModifyExpressionValue(
      method = {"getPackedLightCoords(Lnet/minecraft/world/entity/Entity;F)I"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/core/BlockPos;containing(Lnet/minecraft/core/Position;)Lnet/minecraft/core/BlockPos;"
      )}
   )
   private BlockPos zenith$liftPetLightSample(BlockPos var1, Entity var2, float var3) {
      if (PetManager.method4559() && PetManager.method4557(var2.getId())) {
         Level var4 = var2.level();
         BlockPos var5 = var1;

         for (int var6 = 0; var6 < 2; var6++) {
            if (var4.getBrightness(LightLayer.SKY, var5) > 0 || var4.getBrightness(LightLayer.BLOCK, var5) > 0) {
               return var5;
            }

            var5 = var5.above();
         }

         return var5;
      } else {
         return var1;
      }
   }

   @Redirect(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"
      )
   )
   private int zenith$shaderEspGlowColor(Entity var1) {
      if (Util4.method1278(var1.getId())) {
         return Util4.method1279(var1.getId(), var1.getTeamColor());
      } else {
         ShaderESPModule var2 = ShaderESPModule.INSTANCE;
         if (var2 != null && var2.isEnabled() && var2.method4584(var1)) {
            return var2.method4587(var1);
         } else {
            PredictionsModule var3 = PredictionsModule.INSTANCE;
            return var3 != null && var3.method2911(var1) ? var3.method2912() : var1.getTeamColor();
         }
      }
   }
}

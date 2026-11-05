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
   private BlockPos zenith$liftPetLightSample(BlockPos pos, Entity entity, float f) {
      if (PetManager.method4559() && PetManager.method4557(entity.getId())) {
         Level level = entity.level();
         BlockPos pos2 = pos;

         for (int i = 0; i < 2; i++) {
            if (level.getBrightness(LightLayer.SKY, pos2) > 0 || level.getBrightness(LightLayer.BLOCK, pos2) > 0) {
               return pos2;
            }

            pos2 = pos2.above();
         }

         return pos2;
      } else {
         return pos;
      }
   }

   @Redirect(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"
      )
   )
   private int zenith$shaderEspGlowColor(Entity entity) {
      if (Util4.method1278(entity.getId())) {
         return Util4.method1279(entity.getId(), entity.getTeamColor());
      } else {
         ShaderESPModule shaderESPModule = ShaderESPModule.INSTANCE;
         if (shaderESPModule != null && shaderESPModule.isEnabled() && shaderESPModule.method4584(entity)) {
            return shaderESPModule.method4587(entity);
         } else {
            PredictionsModule predictionsModule = PredictionsModule.INSTANCE;
            return predictionsModule != null && predictionsModule.method2911(entity) ? predictionsModule.method2912() : entity.getTeamColor();
         }
      }
   }
}

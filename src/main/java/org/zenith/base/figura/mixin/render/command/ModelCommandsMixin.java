package org.zenith.base.figura.mixin.render.command;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.base.figura.render.FiguraModelEdits;

// 26.3: every model (and model part, via Model.Simple) is queued as a ModelFeatureRenderer.Submit created here
@Mixin(SubmitNodeCollection.class)
public abstract class ModelCommandsMixin {
   @ModifyExpressionValue(
      method = "submitModel",
      at = @At(value = "NEW", target = "net/minecraft/client/renderer/feature/ModelFeatureRenderer$Submit")
   )
   private ModelFeatureRenderer.Submit<?> figura$captureEdit(ModelFeatureRenderer.Submit<?> submit) {
      FiguraModelEdits.capture(submit);
      return submit;
   }
}

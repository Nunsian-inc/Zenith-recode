package org.zenith.base.figura.mixin.render.command;

import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.render.FiguraModelEdits;

@Mixin(ModelFeatureRenderer.class)
public abstract class ModelCommandRendererMixin {
   private static final String PREPARE = "prepareModel(Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$Submit;)V";

   @Inject(method = PREPARE, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;setupAnim(Ljava/lang/Object;)V", shift = Shift.AFTER))
   private void figura$applyEdit(ModelFeatureRenderer.Submit<?> submit, CallbackInfo ci) {
      FiguraModelEdits.Edit edit = FiguraModelEdits.get(submit);
      if (edit != null) {
         edit.apply();
      }
   }

   @Inject(method = PREPARE, at = @At("RETURN"))
   private void figura$restoreEdit(ModelFeatureRenderer.Submit<?> submit, CallbackInfo ci) {
      FiguraModelEdits.Edit edit = FiguraModelEdits.get(submit);
      if (edit != null) {
         edit.restore();
      }
   }
}

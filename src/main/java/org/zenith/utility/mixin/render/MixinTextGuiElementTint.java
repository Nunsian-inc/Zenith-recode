package org.zenith.utility.mixin.render;

import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;

@Mixin({GuiTextRenderState.class})
public class MixinTextGuiElementTint {
   @Shadow
   @Final
   @Mutable
   public int color;
   @Shadow
   @Final
   @Mutable
   public int backgroundColor;

   @Inject(
      method = {"<init>(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;Lorg/joml/Matrix3x2fc;IIIIZZLnet/minecraft/client/gui/navigation/ScreenRectangle;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$tint(CallbackInfo callbackInfo) {
      float[] floatValues = GuiVertexBatch.method97();
      if (floatValues != null) {
         this.color = GuiVertexBatch.method100(this.color, floatValues, false);
         this.backgroundColor = GuiVertexBatch.method100(this.backgroundColor, floatValues, false);
      }
   }
}

package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.ScissorStack;

@Mixin({GuiGraphicsExtractor.class})
public abstract class MixinDrawContextScissor {
   @Inject(
      method = {"enableScissor(IIII)V"},
      at = {@At("TAIL")}
   )
   private void zenith$pushScissor(int n, int n2, int n3, int n4, CallbackInfo callbackInfo) {
      ScissorStack.method1750(((GuiGraphicsExtractor)(Object)this).scissorStack.peek());
   }

   @Inject(
      method = {"disableScissor()V"},
      at = {@At("TAIL")}
   )
   private void zenith$popScissor(CallbackInfo callbackInfo) {
      ScissorStack.method1750(((GuiGraphicsExtractor)(Object)this).scissorStack.peek());
   }
}

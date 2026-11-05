package org.zenith.utility.mixin.screen;

import java.util.List;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zenith.utility.misc.ScoreboardParser;

@Mixin({DebugScreenOverlay.class})
public abstract class MixinDebugHud {
   @ModifyVariable(
      method = {"extractLines(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/List;ZI)V"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private List<String> zenith$randomizeCoords(List<String> strings) {
      return ScoreboardParser.isActive() ? ScoreboardParser.method7071(strings) : strings;
   }
}

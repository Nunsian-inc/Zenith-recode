package org.zenith.utility.mixin.world;

import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({StringDecomposer.class})
public class MixinTextVisitFactory {
   @ModifyArg(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/StringDecomposer;iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
         ordinal = 0
      ),
      method = {"iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z"},
      index = 0
   )
   private static String adjustText(String text) {
      return protect(text);
   }

   @Unique
   private static String protect(String text) {
      return text != null && ModuleHookDefinitions.class124.active()
         ? ModuleHookDefinitions.class124.method1645(new ModuleHookDefinitions.Entry5(text)).text
         : text;
   }
}

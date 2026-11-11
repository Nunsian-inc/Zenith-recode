package org.zenith.utility.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.screens.override.main.component.PauseMenuScope;

@Mixin(Gui.class)
public abstract class MixinGuiPauseMenuScope {
   @Shadow
   private Screen screen;

   @WrapOperation(
      method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = Opcodes.PUTFIELD)
   )
   private void zenith$trackPauseMenu(Gui gui, Screen next, Operation<Void> original) {
      // Здесь уже учтены замена экрана событием и ванильная обработка null, но ещё не вызван init.
      PauseMenuScope.onScreenChange(screen, next);
      original.call(gui, next);
   }
}

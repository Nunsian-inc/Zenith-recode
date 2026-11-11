package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.zenith.client.modules.impl.render.BetterMinecraftModule;

@Mixin(Gui.class)
public abstract class MixinGuiDeathScreen {
   // В 26.3 Gui сам создаёт DeathScreen после HEAD-события, когда setScreen(null) вызван у мёртвого игрока.
   @Redirect(
      method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V",
      at = @At(value = "NEW", target = "(Lnet/minecraft/network/chat/Component;ZLnet/minecraft/client/player/LocalPlayer;)Lnet/minecraft/client/gui/screens/DeathScreen;")
   )
   private DeathScreen zenith$createDeathScreen(Component causeOfDeath, boolean hardcore, LocalPlayer player) {
      BetterMinecraftModule module = BetterMinecraftModule.INSTANCE;
      return module.isDeathScreenEnabled()
         ? module.createDeathScreen(causeOfDeath, hardcore, player)
         : new DeathScreen(causeOfDeath, hardcore, player);
   }
}

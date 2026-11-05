package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.client.hud.MusicHud;

@Mixin({ChatScreen.class})
public class MixinChatScreen extends Screen implements MinecraftAccess {
   public MixinChatScreen(Component title) {
      super(title);
   }

   @Inject(
      method = {"handleChatInput(Ljava/lang/String;Z)V"},
      at = {@At("HEAD")},
      cancellable = false
   )
   private void onSendMessage(String str, boolean flag, CallbackInfo callbackInfo) {
   }

   @Inject(
      method = {"mouseScrolled(DDDD)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$musicVolume(double d, double d2, double d3, double d4, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (d4 != 0.0 && MusicHud.method6122(d4)) {
         callbackInfoReturnable.setReturnValue(true);
      }
   }
}

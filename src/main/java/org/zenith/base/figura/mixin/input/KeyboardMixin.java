package org.zenith.base.figura.mixin.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.lua.api.keybind.FiguraKeybind;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"), cancellable = true)
   private void figura$onKey(long window, int action, KeyEvent input, CallbackInfo ci) {
      if (window == this.minecraft.getWindow().handle()) {
         Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
         if (avatar != null && avatar.luaRuntime != null && avatar.luaRuntime.keybinds != null) {
            if (!avatar.keyPressEvent(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), action, org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers())) || !this.minecraft.mouseHandler.isMouseGrabbed() && this.minecraft.gui.screen() != null
               )
             {
               if (avatar.luaRuntime != null
                  && FiguraKeybind.set(avatar.luaRuntime.keybinds.keyBindings, InputConstants.getKey(input), action != 0, org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()))) {
                  ci.cancel();
               }
            } else {
               ci.cancel();
            }
         }
      }
   }

   @Inject(method = "charTyped(JLnet/minecraft/client/input/CharacterEvent;)V", at = @At("HEAD"))
   private void figura$onChar(long window, CharacterEvent input, CallbackInfo ci) {
      Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
      if (avatar != null) {
         avatar.charTypedEvent(Character.toString(input.codepoint()), 0, input.codepoint());
      }
   }
}

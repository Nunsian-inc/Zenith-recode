package org.zenith.utility.mixin.screen;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.CommandSuggestions.SuggestionsList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.zenith.Zenith;

@Mixin({CommandSuggestions.class})
public abstract class MixinChatInputSuggestor {
   @Final
   @Shadow
   public EditBox input;
   @Shadow
   public boolean keepSuggestions;
   @Shadow
   private ParseResults<SharedSuggestionProvider> currentParse;
   @Shadow
   private CompletableFuture<Suggestions> pendingSuggestions;
   @Shadow
   private SuggestionsList suggestions;

   @Shadow
   protected abstract void updateUsageInfo(ParseResults<net.minecraft.client.multiplayer.ClientSuggestionProvider> currentParse, Suggestions pendingSuggestion);

   @Inject(
      method = {"updateCommandInfo()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
         remap = false
      )},
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   public void refreshHook(CallbackInfo callbackInfo, String str, StringReader command) {
      if (command.canRead(Zenith.getInstance().getCommandManager().getPrefix().length())
         && command.getString().startsWith(Zenith.getInstance().getCommandManager().getPrefix(), command.getCursor())) {
         command.setCursor(command.getCursor() + 1);
         if (this.currentParse == null) {
            this.currentParse = Zenith.getInstance().getCommandManager().getDispatcher().parse(command, Zenith.getInstance().getCommandManager().getSource());
         }

         int cursorPosition = this.input.getCursorPosition();
         if (cursorPosition >= 1 && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = Zenith.getInstance().getCommandManager().getDispatcher().getCompletionSuggestions(this.currentParse, cursorPosition);
            this.pendingSuggestions.thenAccept(pendingSuggestion -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo((ParseResults)this.currentParse, pendingSuggestion);
               }
            });
         }

         callbackInfo.cancel();
      }
   }
}

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
   protected abstract void updateUsageInfo(ParseResults<net.minecraft.client.multiplayer.ClientSuggestionProvider> var1, Suggestions var2);

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
   public void refreshHook(CallbackInfo var1, String var2, StringReader var3) {
      if (var3.canRead(Zenith.getInstance().getCommandManager().getPrefix().length())
         && var3.getString().startsWith(Zenith.getInstance().getCommandManager().getPrefix(), var3.getCursor())) {
         var3.setCursor(var3.getCursor() + 1);
         if (this.currentParse == null) {
            this.currentParse = Zenith.getInstance().getCommandManager().getDispatcher().parse(var3, Zenith.getInstance().getCommandManager().getSource());
         }

         int var4 = this.input.getCursorPosition();
         if (var4 >= 1 && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = Zenith.getInstance().getCommandManager().getDispatcher().getCompletionSuggestions(this.currentParse, var4);
            this.pendingSuggestions.thenAccept(var5 -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo((ParseResults)this.currentParse, var5);
               }
            });
         }

         var1.cancel();
      }
   }
}

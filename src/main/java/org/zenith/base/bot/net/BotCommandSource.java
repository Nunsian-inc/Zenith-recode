package org.zenith.base.bot.net;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket.Action;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

public final class BotCommandSource implements SharedSuggestionProvider {
   private final BotPlayHandler handler;
   private int completionId = -1;
   @Nullable
   private CompletableFuture<Suggestions> pendingCommandCompletion;
   private final Set<String> chatSuggestions = new HashSet<>();

   public BotCommandSource(BotPlayHandler var1) {
      this.handler = var1;
   }

   @Override
   public Collection<String> getOnlinePlayerNames() {
      ArrayList<String> var1 = new ArrayList<>();

      for (PlayerInfo var3 : this.handler.getPlayerList()) {
         var1.add(var3.getProfile().name());
      }

      return var1;
   }

   @Override
   public synchronized Collection<String> getCustomTabSuggestions() {
      if (this.chatSuggestions.isEmpty()) {
         return this.getOnlinePlayerNames();
      } else {
         var var1 = new HashSet<>(this.getOnlinePlayerNames());
         var1.addAll(this.chatSuggestions);
         return var1;
      }
   }

   @Override
   public Collection<String> getAllTeams() {
      Scoreboard var1 = this.handler.getScoreboard();
      synchronized (var1) {
         return List.copyOf(var1.getTeamNames());
      }
   }

   @Override
   public Stream<Identifier> getAvailableSounds() {
      return BuiltInRegistries.SOUND_EVENT.keySet().stream();
   }

   @Override
   public Stream<Identifier> getAvailablePostEffects() {
      return Stream.empty();
   }

   @Override
   public PermissionSet permissions() {
      return PermissionSet.ALL_PERMISSIONS;
   }

   @Override
   public <E> CompletableFuture<Suggestions> suggestRegistryElements(
      ResourceKey<? extends Registry<E>> registryRef, ElementSuggestionType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context, java.util.function.Predicate<E> filter
   ) {
      return this.registryAccess().lookup(registryRef).map(var3 -> {
         this.suggestRegistryElements(var3, suggestedIdType, builder, filter);
         return builder.buildFuture();
      }).orElseGet(() -> this.customSuggestion(context));
   }

   @Override
   public synchronized CompletableFuture<Suggestions> customSuggestion(CommandContext<?> context) {
      if (this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.cancel(false);
      }

      CompletableFuture var2 = new CompletableFuture();
      this.pendingCommandCompletion = var2;
      int var3 = ++this.completionId;
      this.handler.sendPacket(new ServerboundCommandSuggestionPacket(var3, context.getInput()));
      return var2;
   }

   @Override
   public Set<ResourceKey<Level>> levels() {
      return this.handler.getWorldKeys();
   }

   @Override
   public RegistryAccess registryAccess() {
      return this.handler.getRegistryManager();
   }

   @Override
   public FeatureFlagSet enabledFeatures() {
      return this.handler.getEnabledFeatures();
   }

   public synchronized void onCommandSuggestions(int var1, Suggestions var2) {
      if (var1 == this.completionId && this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.complete(var2);
         this.pendingCommandCompletion = null;
         this.completionId = -1;
      }
   }

   public synchronized void onChatSuggestions(Action var1, List<String> var2) {
      switch (var1) {
         case ADD:
            this.chatSuggestions.addAll(var2);
            break;
         case REMOVE:
            var2.forEach(this.chatSuggestions::remove);
            break;
         case SET:
            this.chatSuggestions.clear();
            this.chatSuggestions.addAll(var2);
      }
   }
}

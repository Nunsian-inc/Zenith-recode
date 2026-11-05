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

   public BotCommandSource(BotPlayHandler handler) {
      this.handler = handler;
   }

   @Override
   public Collection<String> getOnlinePlayerNames() {
      ArrayList<String> strings = new ArrayList<>();

      for (PlayerInfo player : this.handler.getPlayerList()) {
         strings.add(player.getProfile().name());
      }

      return strings;
   }

   @Override
   public synchronized Collection<String> getCustomTabSuggestions() {
      if (this.chatSuggestions.isEmpty()) {
         return this.getOnlinePlayerNames();
      } else {
         var strings = new HashSet<>(this.getOnlinePlayerNames());
         strings.addAll(this.chatSuggestions);
         return strings;
      }
   }

   @Override
   public Collection<String> getAllTeams() {
      Scoreboard scoreboard = this.handler.getScoreboard();
      synchronized (scoreboard) {
         return List.copyOf(scoreboard.getTeamNames());
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
      return this.registryAccess().lookup(registryRef).map(registry -> {
         this.suggestRegistryElements(registry, suggestedIdType, builder, filter);
         return builder.buildFuture();
      }).orElseGet(() -> this.customSuggestion(context));
   }

   @Override
   public synchronized CompletableFuture<Suggestions> customSuggestion(CommandContext<?> context) {
      if (this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.cancel(false);
      }

      CompletableFuture future = new CompletableFuture();
      this.pendingCommandCompletion = future;
      int id = ++this.completionId;
      this.handler.sendPacket(new ServerboundCommandSuggestionPacket(id, context.getInput()));
      return future;
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

   public synchronized void onCommandSuggestions(int id, Suggestions suggestions) {
      if (id == this.completionId && this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.complete(suggestions);
         this.pendingCommandCompletion = null;
         this.completionId = -1;
      }
   }

   public synchronized void onChatSuggestions(Action action, List<String> entries) {
      switch (action) {
         case ADD:
            this.chatSuggestions.addAll(entries);
            break;
         case REMOVE:
            entries.forEach(this.chatSuggestions::remove);
            break;
         case SET:
            this.chatSuggestions.clear();
            this.chatSuggestions.addAll(entries);
      }
   }
}

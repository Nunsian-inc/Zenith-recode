package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.misc.StreamerModeModule;

public class PlayerArgumentType implements ArgumentType<String>, MinecraftAccess {
   private static final Collection<String> EXAMPLES = List.of("Steve", "Alex", "Bogdan");
   private final boolean displayedNames;

   private PlayerArgumentType(boolean displayedNames) {
      this.displayedNames = displayedNames;
   }

   public static PlayerArgumentType create() {
      return new PlayerArgumentType(false);
   }

   public static PlayerArgumentType createDisplayed() {
      return new PlayerArgumentType(true);
   }

   public String parse(StringReader stringReader) throws CommandSyntaxException {
      return stringReader.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      var values = mc.getConnection().getOnlinePlayers().stream().map(onlinePlayer -> onlinePlayer.getProfile().name());
      if (this.displayedNames && StreamerModeModule.INSTANCE.method6289() && mc.player != null) {
         String scoreboardName = mc.player.getScoreboardName();
         values = values.filter(var1x -> !var1x.equals(scoreboardName)).map(StreamerModeModule.INSTANCE::method6291);
      }

      return SharedSuggestionProvider.suggest(values, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

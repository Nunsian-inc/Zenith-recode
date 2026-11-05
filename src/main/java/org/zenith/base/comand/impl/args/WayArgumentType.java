package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.zenith.Zenith;
import org.zenith.base.filemanager.impl.way.Way;

public class WayArgumentType implements ArgumentType<String> {
   private static final List<String> EXAMPLES = List.of("Zamok", "Event", "Home");

   public static WayArgumentType create() {
      return new WayArgumentType();
   }

   public String parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readString();
      if (!Zenith.getInstance().getWayManager().hasWay(string)) {
         throw new DynamicCommandExceptionType(var1x -> Component.literal("У тебя нет метки " + string)).create(string);
      } else {
         return string;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(Zenith.getInstance().getWayManager().getItems().stream().map(Way::name), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

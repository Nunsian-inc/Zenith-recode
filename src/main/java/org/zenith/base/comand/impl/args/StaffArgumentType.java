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

public class StaffArgumentType implements ArgumentType<String> {
   private static final List<String> EXAMPLES = Zenith.getInstance().getStaffName().getItems().stream().limit(5L).toList();

   public static StaffArgumentType create() {
      return new StaffArgumentType();
   }

   public String parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readString();
      if (!Zenith.getInstance().getStaffName().method1668(string)) {
         throw new DynamicCommandExceptionType(var1x -> Component.literal("Нету в стафе " + string)).create(string);
      } else {
         return string;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(Zenith.getInstance().getStaffName().getItems(), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

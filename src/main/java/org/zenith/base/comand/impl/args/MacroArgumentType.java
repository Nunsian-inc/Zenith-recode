package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.zenith.utility.misc.KeyName;

public class MacroArgumentType implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = List.of("b");

   public static MacroArgumentType create() {
      return new MacroArgumentType();
   }

   public String parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readString();
      if (KeyName.method6704(string) == -1) {
         throw new DynamicCommandExceptionType(var1x -> Component.literal("Нет такой клавиши " + string)).create(string);
      } else {
         return string;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(Arrays.stream(KeyName.values()).map(keyName -> keyName.text), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}

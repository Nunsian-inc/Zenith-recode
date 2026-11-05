package org.zenith.base.comand.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.Generated;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.zenith.ZenithAccess;

public abstract class CommandAbstract implements ZenithAccess {
   private final String command;

   public CommandAbstract(String command) {
      this.command = command;
   }

   public void register(CommandDispatcher<SharedSuggestionProvider> dispatcher) {
      LiteralArgumentBuilder literal = LiteralArgumentBuilder.literal(this.command);
      this.execute(literal);
      dispatcher.register(literal);
   }

   public abstract void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literal);

   @NotNull
   public static <T> RequiredArgumentBuilder<SharedSuggestionProvider, T> arg(String name, ArgumentType<T> word) {
      return RequiredArgumentBuilder.argument(name, word);
   }

   @NotNull
   public static LiteralArgumentBuilder<SharedSuggestionProvider> literal(String name) {
      return LiteralArgumentBuilder.literal(name);
   }

   @Generated
   public String getCommand() {
      return this.command;
   }
}

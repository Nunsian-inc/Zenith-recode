package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.api.MacroManager;
import org.zenith.utility.misc.KeyName;

public class MacroCommand extends CommandAbstract {
   private final SuggestionProvider<SharedSuggestionProvider> SUGGEST_KEYS = (commandContext, suggestionsBuilder) -> {
      for (KeyName keyName : KeyName.values()) {
         if (!keyName.text.isEmpty()) {
            suggestionsBuilder.suggest(keyName.text);
         }
      }

      return suggestionsBuilder.buildFuture();
   };

   public MacroCommand() {
      super("macro");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(
         literal("add")
            .then(
               arg("name", StringArgumentType.word())
                  .then(
                     arg("key", StringArgumentType.word())
                        .suggests(this.SUGGEST_KEYS)
                        .then(arg("command", StringArgumentType.greedyString()).executes(var1x -> {
                           String argument = (String)var1x.getArgument("name", String.class);
                           String string = (String)var1x.getArgument("key", String.class);
                           String str = (String)var1x.getArgument("command", String.class);
                           this.handleAddMacro(argument, string, str);
                           return 1;
                        }))
                  )
            )
      );
      literalArgumentBuilder.then(literal("remove").then(arg("name", StringArgumentType.word()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("name", String.class);
         MacroManager.Entry entry = Zenith.getInstance().getMacroManager().method6986(var1x);
         if (entry == null) {
            ChatLogger.method2021("Макрос не найден!");
            return 1;
         } else {
            Zenith.getInstance().getMacroManager().method6985(var1x);
            ChatLogger.method2019(var1x + " удален");
            return 1;
         }
      })));
      literalArgumentBuilder.then(literal("clear").executes(commandContext -> {
         Zenith.getInstance().getMacroManager().clear();
         ChatLogger.method2019("Все макросы удалены");
         return 1;
      }));
      literalArgumentBuilder.then(literal("list").executes(commandContext -> {
         if (!Zenith.getInstance().getMacroManager().getItems().isEmpty()) {
            for (MacroManager.Entry entry : Zenith.getInstance().getMacroManager().getItems()) {
               mc.player.sendSystemMessage(entry.toText());
            }
         } else {
            ChatLogger.method2019("Список макросов пуст!");
         }

         return 1;
      }));
   }

   private void handleAddMacro(String argument, String string, String str) {
      MacroManager macroManager = Zenith.getInstance().getMacroManager();
      int intValue = KeyName.method6704(string);
      if (intValue == KeyName.KEY_NONE.intValue) {
         ChatLogger.method2021("Неизвестная клавиша: " + string);
      } else {
         MacroManager.Entry entry = macroManager.method6986(argument);
         if (entry != null) {
            ChatLogger.method2021("Макрос с таким именем уже существует!");
            ChatLogger.method2019("Наведи мышку на сообщение ниже чтобы удалить его");
            mc.player.sendSystemMessage(entry.toText());
         } else {
            MacroManager.Entry entry2 = new MacroManager.Entry(argument, intValue, str);
            macroManager.method6983(entry2);
            mc.player.sendSystemMessage(entry2.toText());
            ChatLogger.method2019("Макрос добавлен");
         }
      }
   }
}

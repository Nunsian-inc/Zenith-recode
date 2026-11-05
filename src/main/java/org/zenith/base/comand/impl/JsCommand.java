package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.Util;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.ScriptTestApi;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.misc.ScriptHeaderParser;

public class JsCommand extends CommandAbstract {
   private static final SuggestionProvider<SharedSuggestionProvider> SCRIPTS = (commandContext, builder) -> SharedSuggestionProvider.suggest(
      host().method1293().stream().map(Script::getId), builder
   );

   public JsCommand() {
      super("js");
   }

   private static Storage host() {
      return Zenith.getInstance().getStorage();
   }

   private static void info(String string) {
      ChatLogger.method2020(ChatLogger.Type.INFO, string);
   }

   private static void error(String string) {
      ChatLogger.method2020(ChatLogger.Type.ERROR, string);
   }

   private static Script find(String argument) {
      Script script = host().method1294(argument);
      if (script == null) {
         error("Скрипт «" + argument + "» не найден. Список: .js list");
      }

      return script;
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         info(".js list — скрипты · .js on/off <имя> · .js reload [имя] · .js new <имя> [" + String.join("|", ScriptHeaderParser.items) + "]");
         info(".js eval [имя] <код> · .js log [имя] · .js test <имя> · .js clear · .js folder · .js api");
         return 1;
      });
      literalArgumentBuilder.then(
         literal("list")
            .executes(
               commandContext -> {
                  if (host().method1293().isEmpty()) {
                     info("Скриптов нет. Создай: .js new hello hud — папка: " + host().getPath());
                     return 1;
                  } else {
                     for (Script script : host().method1293()) {
                        String str = switch (script.getType2()) {
                           case RUNNING -> "§aработает";
                           case FAILED -> "§cошибка";
                           default -> host().method1297(script) ? "§eожидает" : "§7выключен";
                        };
                        String str2 = script.getItems3().isEmpty() ? "" : " §7· модулей: " + script.getItems3().size();
                        info(
                           "§f"
                              + script.getId()
                              + (script.getVersion().isBlank() ? "" : " §7v" + script.getVersion())
                              + " §7— "
                              + str
                              + str2
                              + (script.getError().isBlank() ? "" : " §c" + trim(script.getError()))
                        );
                     }

                     return 1;
                  }
               }
            )
      );
      literalArgumentBuilder.then(literal("on").then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(suggest -> {
         Script var1x = find((String)suggest.getArgument("name", String.class));
         if (var1x != null) {
            host().method1298(var1x, true);
            info(var1x.isRunning() ? "Скрипт " + var1x.method1403() + " запущен" : "Не запустился: " + trim(var1x.getError()));
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("off").then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(suggest -> {
         Script var1x = find((String)suggest.getArgument("name", String.class));
         if (var1x != null) {
            host().method1298(var1x, false);
            info("Скрипт " + var1x.method1403() + " выключен");
         }

         return 1;
      })));
      literalArgumentBuilder.then(((LiteralArgumentBuilder)literal("reload").executes(commandContext -> {
         host().method1300();
         info("Скрипты перезагружены");
         return 1;
      })).then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(suggest -> {
         Script var1x = find((String)suggest.getArgument("name", String.class));
         if (var1x != null) {
            host().method1299(var1x);
            info("Перезагружен: " + var1x.method1403());
         }

         return 1;
      })));
      literalArgumentBuilder.then(
         literal("new")
            .then(
               ((RequiredArgumentBuilder)arg("name", StringArgumentType.word())
                     .executes(commandContext -> create((String)commandContext.getArgument("name", String.class), "empty")))
                  .then(
                     arg("template", StringArgumentType.word())
                        .suggests((commandContext, var1x) -> SharedSuggestionProvider.suggest(ScriptHeaderParser.items, var1x))
                        .executes(suggest -> create((String)suggest.getArgument("name", String.class), (String)suggest.getArgument("template", String.class)))
                  )
            )
      );
      literalArgumentBuilder.then(literal("eval").then(arg("code", StringArgumentType.greedyString()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("code", String.class);
         Script workspace = null;
         int indexOf = var1x.indexOf(32);
         if (indexOf > 0) {
            Script script = host().method1294(var1x.substring(0, indexOf));
            if (script != null && script.isRunning()) {
               workspace = script;
               var1x = var1x.substring(indexOf + 1);
            }
         }

         try {
            info("§7> §f" + trim(String.valueOf(host().method1341(workspace, var1x))));
         } catch (Throwable e) {
            error(trim(Storage.method1325(e)));
         }

         return 1;
      })));
      literalArgumentBuilder.then(
         ((LiteralArgumentBuilder)literal("log").executes(commandContext -> printLog(null)))
            .then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(suggest -> printLog((String)suggest.getArgument("name", String.class))))
      );
      literalArgumentBuilder.then(literal("test").then(arg("name", StringArgumentType.word()).suggests(SCRIPTS).executes(suggest -> {
         Script var1x = find((String)suggest.getArgument("name", String.class));
         if (var1x != null) {
            if (!var1x.isRunning()) {
               error("Скрипт " + var1x.method1403() + " не запущен");
            } else {
               info(ScriptTestApi.method5413(host(), var1x));
            }
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("clear").executes(commandContext -> {
         host().method1296();
         info("Консоль скриптов очищена");
         return 1;
      }));
      literalArgumentBuilder.then(literal("folder").executes(commandContext -> {
         com.mojang.blaze3d.Blaze3D.openPath((host().getPath().toFile()).toPath());
         return 1;
      }));
      literalArgumentBuilder.then(literal("api").executes(commandContext -> {
         com.mojang.blaze3d.Blaze3D.openPath((host().getPath().resolve("API.md").toFile()).toPath());
         return 1;
      }));
   }

   private static int create(String argument, String string) {
      try {
         Script workspace = host().method1340(argument, string);
         host().method1298(workspace, true);
         info("Создан " + workspace.getPath2() + (workspace.isRunning() ? " и запущен" : ""));
      } catch (Exception e) {
         error("Не удалось создать: " + e.getMessage());
      }

      return 1;
   }

   private static int printLog(String argument) {
      var data2s = host().method1295();
      Script script = argument == null ? null : host().method1294(argument);
      int n = 0;

      for (int i = data2s.size() - 1; i >= 0 && n < 12; i--) {
         Storage.Data2 data2 = (Storage.Data2)data2s.get(i);
         if (script == null || data2.getText().equals(script.method1403())) {
            n++;
            String str = data2.getText2();

            String str2 = switch (str) {
               case "error" -> "§c";
               case "warn" -> "§e";
               default -> "§7";
            };
            info(str2 + "[" + data2.getText() + "] §f" + trim(data2.text()));
         }
      }

      if (n == 0) {
         info("Консоль пуста");
      }

      return 1;
   }

   private static String trim(String error) {
      return error.length() > 220 ? error.substring(0, 220) + "…" : error;
   }
}

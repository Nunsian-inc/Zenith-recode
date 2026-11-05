package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotClientConfig;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.comand.api.CommandAbstract;

public class HBotCommand extends CommandAbstract {
   public HBotCommand() {
      super("hbot");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(
         literal("connect")
            .then(
               ((RequiredArgumentBuilder)arg("name", StringArgumentType.word())
                     .executes(context -> connect(StringArgumentType.getString(context, "name"), null, null)))
                  .then(
                     ((RequiredArgumentBuilder)arg("address", StringArgumentType.word())
                           .executes(context -> connect(StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "address"), null)))
                        .then(
                           arg("proxy", StringArgumentType.greedyString())
                              .executes(
                                 context -> connect(
                                    StringArgumentType.getString(context, "name"),
                                    StringArgumentType.getString(context, "address"),
                                    StringArgumentType.getString(context, "proxy")
                                 )
                              )
                        )
                  )
            )
      );
      literalArgumentBuilder.then(literal("disconnect").then(arg("name", StringArgumentType.word()).executes(context -> {
         String var1x = StringArgumentType.getString(context, "name");
         if (var1x.equalsIgnoreCase("all")) {
            HeadlessBots.disconnectAll();
            ChatLogger.method2019("Все headless-боты отключены");
         } else if (HeadlessBots.disconnect(var1x)) {
            ChatLogger.method2019("Бот " + var1x + " отключён");
         } else {
            ChatLogger.method2022("Бот " + var1x + " не найден");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("list").executes(commandContext -> {
         if (HeadlessBots.all().isEmpty()) {
            ChatLogger.method2019("Нет активных headless-ботов");
            return 1;
         } else {
            for (BotClient botClient : HeadlessBots.all()) {
               ChatLogger.method2019(botClient.name() + " — " + botClient.getPhase());
            }

            return 1;
         }
      }));
      literalArgumentBuilder.then(literal("say").then(arg("name", StringArgumentType.word()).then(arg("message", StringArgumentType.greedyString()).executes(context -> {
         BotClient var1x = requireBot(StringArgumentType.getString(context, "name"));
         if (var1x == null) {
            return 1;
         } else {
            String string = StringArgumentType.getString(context, "message");
            if (var1x.sendChat(string)) {
               ChatLogger.method2019("Бот " + var1x.name() + " отправил: " + string);
            } else {
               ChatLogger.method2022("Бот " + var1x.name() + " ещё не в игре");
            }

            return 1;
         }
      }))));
      literalArgumentBuilder.then(
         literal("module")
            .then(
               arg("name", StringArgumentType.word())
                  .then(arg("module", StringArgumentType.word()).then(arg("state", StringArgumentType.word()).executes(context -> {
                     BotClient var1x = requireBot(StringArgumentType.getString(context, "name"));
                     if (var1x == null) {
                        return 1;
                     } else {
                        String string = StringArgumentType.getString(context, "module");
                        boolean equalsIgnoreCase = StringArgumentType.getString(context, "state").equalsIgnoreCase("on");
                        var1x.execute(() -> {
                           boolean var3x = var1x.getModules().setEnabled(string, equalsIgnoreCase);
                           report(
                              var3x ? "Бот " + var1x.name() + ": модуль " + string + (equalsIgnoreCase ? " включен" : " выключен") : "Модуль " + string + " не найден",
                              var3x
                           );
                        });
                        return 1;
                     }
                  })))
            )
      );
      literalArgumentBuilder.then(literal("modules").then(arg("name", StringArgumentType.word()).executes(context -> {
         BotClient var1x = requireBot(StringArgumentType.getString(context, "name"));
         if (var1x == null) {
            return 1;
         } else {
            for (BotModule botModule : var1x.getModules().getModules()) {
               ChatLogger.method2019(botModule.getName() + " — " + (botModule.isEnabled() ? "вкл" : "выкл"));
            }

            return 1;
         }
      })));
   }

   private static int connect(String string, String str, String proxy) {
      String input = str;
      if (str == null) {
         ServerData currentServer = Minecraft.getInstance().getCurrentServer();
         if (currentServer == null) {
            ChatLogger.method2022("Не подключён к серверу — укажи адрес: .hbot connect <ник> <host:port>");
            return 1;
         }

         input = currentServer.ip;
      }

      ServerAddress serverAddress = ServerAddress.parseString(input);
      BotClient connect = HeadlessBots.connect(BotClientConfig.offline(string, serverAddress.getHost(), serverAddress.getPort(), proxy, HeadlessBots.getProtocolVersion(string)));
      ChatLogger.method2019("Бот " + connect.name() + " подключается к " + input);
      return 1;
   }

   private static BotClient requireBot(String string) {
      BotClient botClient = HeadlessBots.get(string);
      if (botClient == null) {
         ChatLogger.method2022("Бот " + string + " не найден");
      }

      return botClient;
   }

   private static void report(String status, boolean flag) {
      Minecraft.getInstance().execute(() -> {
         if (flag) {
            ChatLogger.method2019(status);
         } else {
            ChatLogger.method2022(status);
         }
      });
   }
}

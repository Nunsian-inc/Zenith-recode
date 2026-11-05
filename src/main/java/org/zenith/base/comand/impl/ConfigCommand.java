package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.IOException;
import java.util.List;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.CfgArgumentType;
import org.zenith.utility.api.Manager;

public class ConfigCommand extends CommandAbstract {
   public ConfigCommand() {
      super("cfg");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(((LiteralArgumentBuilder)literal("save").executes(commandContext -> {
         boolean var1x = Zenith.getInstance().getManager().method7779("current_config");
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация сохранена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при сохранении конфигурации");
         }

         return 1;
      })).then(arg("name", CfgArgumentType.create()).executes(commandContext -> {
         boolean var1x = Zenith.getInstance().getManager().method7779((String)commandContext.getArgument("name", String.class));
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация сохранена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при сохранении конфигурации");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("list").executes(commandContext -> {
         var var1x = Zenith.getInstance().getManager().method7785();
         if (var1x.isEmpty()) {
            ChatLogger.method2019("Список конфигов пуст!");
         } else {
            var1x.forEach(ChatLogger::method2019);
         }

         return 1;
      }));
      literalArgumentBuilder.then(((LiteralArgumentBuilder)literal("load").executes(commandContext -> {
         boolean var1x = Zenith.getInstance().getManager().method7766("current_config");
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация загружена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при загрузке конфигурации");
         }

         return 1;
      })).then(arg("name", CfgArgumentType.create()).executes(commandContext -> {
         boolean var1x = Zenith.getInstance().getManager().method7766((String)commandContext.getArgument("name", String.class));
         if (var1x) {
            ChatLogger.method2020(ChatLogger.Type.INFO, "Конфигурация загружена");
         } else {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при загрузке конфигурации");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("help").executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .config <list/save/load/help>");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Команды:");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".config save - сохранить конфигурацию");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".config load - загрузить конфигурацию");
         return 1;
      }));
      literalArgumentBuilder.then(literal("dir").executes(commandContext -> {
         try {
            if (Util.getPlatform() == OS.WINDOWS) {
               new ProcessBuilder("explorer", Manager.file.getAbsolutePath()).start();
            } else {
               com.mojang.blaze3d.Blaze3D.openPath((Manager.file).toPath());
            }
         } catch (IOException e) {
         }

         return 1;
      }));
   }
}

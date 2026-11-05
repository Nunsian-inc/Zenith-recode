package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.misc.EmoteAccess;

public final class EmoteCommand extends CommandAbstract {
   public EmoteCommand() {
      super("emote");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .emote <id>, .emote stop или .emote list");
         return 1;
      });
      literalArgumentBuilder.then(literal("list").executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Доступные анимации: " + String.join(", ", EmoteAccess.method7916()));
         return 1;
      }));
      literalArgumentBuilder.then(literal("stop").executes(commandContext -> {
         if (mc.player != null) {
            EmoteAccess.method7922(mc.player.getUUID());
         }

         return 1;
      }));
      literalArgumentBuilder.then(arg("id", StringArgumentType.word()).executes(context -> {
         String var1x = StringArgumentType.getString(context, "id");
         if (!EmoteAccess.method7920(var1x)) {
            ChatLogger.method2020(ChatLogger.Type.ERROR, "Неизвестная анимация: " + var1x + ". Используйте .emote list");
         }

         return 1;
      }));
   }
}

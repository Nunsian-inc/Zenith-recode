package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;

public class GpsCommand extends CommandAbstract {
   public GpsCommand() {
      super("gps");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2019("Используй .way");
         return 1;
      });
      literalArgumentBuilder.then(literal("help").executes(commandContext -> {
         ChatLogger.method2019("Используй .way");
         return 1;
      }));
   }
}

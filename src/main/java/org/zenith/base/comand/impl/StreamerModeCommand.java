package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.modules.impl.misc.StreamerModeModule;

public class StreamerModeCommand extends CommandAbstract {
   public StreamerModeCommand() {
      super("streamermode");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      for (String name : new String[]{"parseAnarchy", "parseanarchy"}) {
         literalArgumentBuilder.then(literal(name).then(arg("anarchy", IntegerArgumentType.integer(1, 999)).executes(context -> {
            StreamerModeModule.INSTANCE.method6283(IntegerArgumentType.getInteger(context, "anarchy"));
            return 1;
         })));
      }

      literalArgumentBuilder.then(literal("clear").executes(commandContext -> {
         StreamerModeModule.INSTANCE.method6284();
         return 1;
      }));
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2019("Использование: .streamermode parseAnarchy <номер анки> и .streamermode clear");
         return 1;
      });
   }
}

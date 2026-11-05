package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.PlayerArgumentType;
import org.zenith.base.comand.impl.args.StaffArgumentType;

public class StaffCommand extends CommandAbstract {
   public StaffCommand() {
      super("staff");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(literal("add").then(arg("player", PlayerArgumentType.create()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("player", String.class);
         if (Zenith.getInstance().getStaffName().getItems().contains(var1x)) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Уже добавлен " + var1x);
            return 1;
         } else {
            Zenith.getInstance().getStaffName().add(var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Добавили " + var1x);
            return 1;
         }
      })));
      literalArgumentBuilder.then(literal("remove").then(arg("player", StaffArgumentType.create()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("player", String.class);
         Zenith.getInstance().getStaffName().remove(var1x);
         ChatLogger.method2020(ChatLogger.Type.INFO, var1x + " удален из стаффа");
         return 1;
      })));
      literalArgumentBuilder.then(literal("list").executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, Zenith.getInstance().getStaffName().getItems().toString());
         return 1;
      }));
   }
}

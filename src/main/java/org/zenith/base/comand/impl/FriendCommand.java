package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.FriendArgumentType;
import org.zenith.base.comand.impl.args.PlayerArgumentType;

public class FriendCommand extends CommandAbstract {
   public FriendCommand() {
      super("friend");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(literal("add").then(arg("player", PlayerArgumentType.create()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("player", String.class);
         if (Zenith.getInstance().getFriends().getItems().contains(var1x)) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Уже добавлен " + var1x);
            return 1;
         } else {
            Zenith.getInstance().getFriends().add(var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Добавили " + var1x);
            return 1;
         }
      })));
      literalArgumentBuilder.then(literal("remove").then(arg("player", FriendArgumentType.create()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("player", String.class);
         Zenith.getInstance().getFriends().method7629(var1x);
         ChatLogger.method2020(ChatLogger.Type.INFO, var1x + " удален из друзей");
         return 1;
      })));
      literalArgumentBuilder.then(literal("list").executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, Zenith.getInstance().getFriends().getItems().toString());
         return 1;
      }));
   }
}

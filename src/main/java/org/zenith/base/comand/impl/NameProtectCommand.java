package org.zenith.base.comand.impl;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.JsonOps;
import java.util.List;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.PlayerArgumentType;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.utility.misc.TextUtils;

public class NameProtectCommand extends CommandAbstract {
   public NameProtectCommand() {
      super("nameprotect");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      ((LiteralArgumentBuilder)literalArgumentBuilder.then(literal("parse").then(arg("player", PlayerArgumentType.createDisplayed()).executes(commandContext -> {
         var var1x = mc.gui.hud.getTabList().getPlayerInfos();
         String argument = (String)commandContext.getArgument("player", String.class);

         for (int i = 0; i < var1x.size(); i++) {
            PlayerInfo info = (PlayerInfo)var1x.get(i);
            String name = info.getProfile().name();
            String str = StreamerModeModule.INSTANCE.method6291(name);
            if (name.equals(argument) || str.equals(argument)) {
               Component nameForDisplay = mc.gui.hud.getTabList().getNameForDisplay(info);
               MutableComponent input = TextUtils.method2113(nameForDisplay, str);
               if (input.getString().isEmpty()) {
                  ChatLogger.method2019("У данного игрока видимо нету доната");
                  return 1;
               }

               JsonElement jsonElement = (JsonElement)ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, input).result().get();
               NameProtectModule.INSTANCE.method5151(jsonElement, i);
            }
         }

         return 1;
      })))).executes(commandContext -> {
         ChatLogger.method2019("Использование .nameprotect parse nick и .nameprotect clear ");
         return 1;
      });
      literalArgumentBuilder.then(literal("clear").executes(commandContext -> {
         NameProtectModule.INSTANCE.method5152();
         return 1;
      }));
   }
}

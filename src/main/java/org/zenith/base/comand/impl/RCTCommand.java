package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.misc.HolyWorldServerTracker;
import org.zenith.utility.misc.HudHolder;

public class RCTCommand extends CommandAbstract implements ZenithAccess {
   public RCTCommand() {
      super("rct");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(var1x -> {
         HolyWorldServerTracker repository = this.repository();
         if (!repository.isHolyWorldHere()) {
            HudHolder.method6969().method6972("0", Component.literal(" Не работает на этом " + ChatFormatting.RED + "сервере"));
            return 1;
         } else if (repository.method6149()) {
            HudHolder.method6969().method6972("️0", Component.literal(" Вы находитесь в режиме " + ChatFormatting.RED + "пвп"));
            return 1;
         } else {
            repository.reconnect(repository.currentAnarchyHere());
            return 1;
         }
      });
      literalArgumentBuilder.then(CommandAbstract.arg("anarchy", IntegerArgumentType.integer(1, 69)).executes(var1x -> {
         HolyWorldServerTracker repository = this.repository();
         if (!repository.isHolyWorldHere()) {
            HudHolder.method6969().method6972("0", Component.literal(" Не работает на этом " + ChatFormatting.RED + "сервере"));
            return 1;
         } else if (repository.method6149()) {
            HudHolder.method6969().method6972("0", Component.literal(" Вы находитесь в режиме " + ChatFormatting.RED + "пвп"));
            return 1;
         } else {
            int argument = (Integer)var1x.getArgument("anarchy", Integer.class);
            repository.reconnect(argument);
            return 1;
         }
      }));
   }

   private HolyWorldServerTracker repository() {
      return Zenith.getInstance().getHolyWorldServerTracker();
   }
}

package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.render.WorldProfiler;

public class WprofCommand extends CommandAbstract {
   public WprofCommand() {
      super("wprof");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(var1x -> this.set(!WorldProfiler.enabled));
      literalArgumentBuilder.then(literal("on").executes(var1x -> this.set(true)));
      literalArgumentBuilder.then(literal("off").executes(var1x -> this.set(false)));
   }

   private int set(boolean flag) {
      WorldProfiler.setEnabled(flag);
      ChatLogger.method2020(ChatLogger.Type.INFO, "Замер погоды " + (flag ? "включён: строки [WPROF] в логе раз в 3 с" : "выключен"));
      return 1;
   }
}

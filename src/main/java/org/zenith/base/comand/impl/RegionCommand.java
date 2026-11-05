package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;

public class RegionCommand extends CommandAbstract {
   private final SuggestionProvider<SharedSuggestionProvider> SUGGEST_X = (commandContext, suggestionsBuilder) -> {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         BlockPos blockPosition = player.blockPosition();
         suggestionsBuilder.suggest(blockPosition.getX());
      }

      return suggestionsBuilder.buildFuture();
   };
   private final SuggestionProvider<SharedSuggestionProvider> SUGGEST_Y = (commandContext, suggestionsBuilder) -> {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         BlockPos blockPosition = player.blockPosition();
         suggestionsBuilder.suggest(blockPosition.getY());
      }

      return suggestionsBuilder.buildFuture();
   };
   private final SuggestionProvider<SharedSuggestionProvider> SUGGEST_Z = (commandContext, suggestionsBuilder) -> {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         BlockPos blockPosition = player.blockPosition();
         suggestionsBuilder.suggest(blockPosition.getZ());
      }

      return suggestionsBuilder.buildFuture();
   };

   public RegionCommand() {
      super("region");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(
         literal("pos1")
            .then(
               arg("x", IntegerArgumentType.integer())
                  .suggests(this.SUGGEST_X)
                  .then(
                     arg("y", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_Y)
                        .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(suggest -> {
                           int var1x = IntegerArgumentType.getInteger(suggest, "x");
                           int integer = IntegerArgumentType.getInteger(suggest, "y");
                           int z = IntegerArgumentType.getInteger(suggest, "z");
                           Zenith.getInstance().getTimedBlockPos().method474(new BlockPos(var1x, integer, z));
                           return 1;
                        }))
                  )
            )
      );
      literalArgumentBuilder.then(
         literal("pos2")
            .then(
               arg("x", IntegerArgumentType.integer())
                  .suggests(this.SUGGEST_X)
                  .then(
                     arg("y", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_Y)
                        .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(suggest -> {
                           int var1x = IntegerArgumentType.getInteger(suggest, "x");
                           int integer = IntegerArgumentType.getInteger(suggest, "y");
                           int z = IntegerArgumentType.getInteger(suggest, "z");
                           Zenith.getInstance().getTimedBlockPos().method475(new BlockPos(var1x, integer, z));
                           return 1;
                        }))
                  )
            )
      );
      literalArgumentBuilder.then(literal("clear").executes(commandContext -> {
         Zenith.getInstance().getTimedBlockPos().clear();
         return 1;
      }));
   }
}

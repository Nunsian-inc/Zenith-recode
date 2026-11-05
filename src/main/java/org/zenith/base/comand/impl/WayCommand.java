package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.WayArgumentType;
import org.zenith.base.filemanager.impl.way.Way;

public class WayCommand extends CommandAbstract {
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

   public WayCommand() {
      super("way");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(
         literal("add")
            .then(
               arg("name", StringArgumentType.word())
                  .then(
                     arg("x", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_X)
                        .then(
                           arg("y", IntegerArgumentType.integer())
                              .suggests(this.SUGGEST_Y)
                              .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(var1x -> {
                                 String argument = (String)var1x.getArgument("name", String.class);
                                 int integer = IntegerArgumentType.getInteger(var1x, "x");
                                 int y = IntegerArgumentType.getInteger(var1x, "y");
                                 int z = IntegerArgumentType.getInteger(var1x, "z");
                                 this.handleAddWay(argument, integer, y, z);
                                 return 1;
                              }))
                        )
                  )
            )
      );
      literalArgumentBuilder.then(literal("event").executes(commandContext -> {
         Zenith.getInstance().getWayManager().startEventCapture();
         ChatLogger.method2019("Ожидание координат из следующих 2 сообщений чата");
         mc.getConnection().sendCommand("event");
         return 1;
      }));
      literalArgumentBuilder.then(literal("clear").executes(commandContext -> {
         Zenith.getInstance().getWayManager().clearList();
         return 1;
      }));
      literalArgumentBuilder.then(literal("remove").then(arg("name", WayArgumentType.create()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("name", String.class);
         Zenith.getInstance().getWayManager().deleteWay(var1x);
         ChatLogger.method2020(ChatLogger.Type.INFO, var1x + " удален ");
         return 1;
      })));
      literalArgumentBuilder.then(literal("list").executes(commandContext -> {
         if (!Zenith.getInstance().getWayManager().getItems().isEmpty()) {
            for (Way way : Zenith.getInstance().getWayManager().getItems()) {
               mc.player.sendSystemMessage(way.toText());
            }
         } else {
            ChatLogger.method2019("Список пуст!");
         }

         return 1;
      }));
   }

   private void handleAddWay(String argument, int integer, int y, int z) {
      Way way = Zenith.getInstance().getWayManager().getWay(argument);
      if (way != null) {
         ChatLogger.method2021("Метка с таким именем уже есть в списке!");
         ChatLogger.method2019("Наведи мышку на сообщение ниже чтобы удалить ее");
         mc.player.sendSystemMessage(way.toText());
      } else {
         String ip = mc.getConnection().getServerData() != null ? mc.getConnection().getServerData().ip : "VANILLA";
         Way way2 = new Way(argument, new BlockPos(integer, y, z), ip);
         mc.player.sendSystemMessage(way2.toText());
         Zenith.getInstance().getWayManager().addWay(way2);
      }
   }
}

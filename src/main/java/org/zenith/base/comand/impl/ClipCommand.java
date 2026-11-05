package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.CoordinateArgumentType;

public class ClipCommand extends CommandAbstract {
   public ClipCommand() {
      super("clip");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(literal("vclip").then(arg("distance", CoordinateArgumentType.create()).executes(commandContext -> {
         double var1x = (Double)commandContext.getArgument("distance", Double.class);
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            player.setPos(player.getX(), player.getY() + var1x + 0.1, player.getZ());
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вертикальный вклип на " + var1x + " блоков");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("hclip").then(arg("distance", CoordinateArgumentType.create()).executes(commandContext -> {
         double var1x = (Double)commandContext.getArgument("distance", Double.class);
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            double d = Math.toRadians(player.getYRot());
            player.setPos(player.getX() - Math.sin(d) * var1x, player.getY() + 0.1, player.getZ() + Math.cos(d) * var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Горизонтальный вклип на " + var1x + " блоков");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("up").then(arg("distance", CoordinateArgumentType.create()).executes(commandContext -> {
         double var1x = (Double)commandContext.getArgument("distance", Double.class);
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            player.setPos(player.getX(), player.getY() + var1x + 0.1, player.getZ());
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип вверх на " + var1x + " блоков");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("down").then(arg("distance", CoordinateArgumentType.create()).executes(commandContext -> {
         double var1x = (Double)commandContext.getArgument("distance", Double.class);
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            player.setPos(player.getX(), player.getY() - var1x + 0.1, player.getZ());
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип вниз на " + var1x + " блоков");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("forward").then(arg("distance", CoordinateArgumentType.create()).executes(commandContext -> {
         double var1x = (Double)commandContext.getArgument("distance", Double.class);
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            double d = Math.toRadians(player.getYRot());
            player.setPos(player.getX() - Math.sin(d) * var1x, player.getY() + 0.1, player.getZ() + Math.cos(d) * var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип вперед на " + var1x + " блоков");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("back").then(arg("distance", CoordinateArgumentType.create()).executes(commandContext -> {
         double var1x = (Double)commandContext.getArgument("distance", Double.class);
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            double d = Math.toRadians(player.getYRot());
            player.setPos(player.getX() + Math.sin(d) * var1x, player.getY() + 0.1, player.getZ() - Math.cos(d) * var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип назад на " + var1x + " блоков");
         }

         return 1;
      })));
      literalArgumentBuilder.then(literal("help").executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .clip <vclip/hclip/up/down/forward/back/help> [расстояние]");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Примеры:");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".clip vclip 10 - вклип вверх на 10 блоков");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".clip hclip 5 - горизонтальный вклип на 5 блоков");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".clip up 3 - вклип вверх на 3 блока");
         return 1;
      }));
   }
}

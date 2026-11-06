package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.game.SnowStats;
import org.zenith.utility.misc.SparseVoxelGrid;
import org.zenith.utility.misc.MeshImporter;
import org.zenith.utility.misc.MeshValidator;
import org.zenith.utility.misc.SnowVisibility;
import org.zenith.utility.render.SnowShader;

public class SnowCommand extends CommandAbstract {
   private static final SparseVoxelGrid VOXELS = new SparseVoxelGrid();

   public SnowCommand() {
      super("snow");
   }

   public static SparseVoxelGrid voxels() {
      return VOXELS;
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "snow fill [радиус] | snow stats | snow check | snow clear");
         return 1;
      });
      literalArgumentBuilder.then(
         ((LiteralArgumentBuilder)literal("fill").executes(var1x -> this.fill(24)))
            .then(arg("radius", IntegerArgumentType.integer(1, 64)).executes(var1x -> this.fill(IntegerArgumentType.getInteger(var1x, "radius"))))
      );
      literalArgumentBuilder.then(literal("stats").executes(var1x -> {
         this.stats();
         return 1;
      }));
      literalArgumentBuilder.then(literal("check").executes(var1x -> {
         this.check();
         return 1;
      }));
      literalArgumentBuilder.then(
         ((LiteralArgumentBuilder)literal("mesh").executes(var1x -> this.mesh(24)))
            .then(arg("radius", IntegerArgumentType.integer(1, 64)).executes(var1x -> this.mesh(IntegerArgumentType.getInteger(var1x, "radius"))))
      );
      literalArgumentBuilder.then(literal("show").executes(commandContext -> {
         SnowVisibility.setVisible(!SnowVisibility.isVisible());
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: отладочная отрисовка " + (SnowVisibility.isVisible() ? "включена" : "выключена"));
         return 1;
      }));
      literalArgumentBuilder.then(
         literal("shader")
            .executes(
               commandContext -> {
                  ChatLogger.method2020(
                     ChatLogger.Type.INFO,
                     SnowShader.method390() ? "Снег: шейдер загружен, слой идёт через него" : "Снег: шейдер НЕ загружен (" + SnowShader.method389() + ")"
                  );
                  return 1;
               }
            )
      );
      literalArgumentBuilder.then(literal("screen").executes(commandContext -> {
         boolean var1x = SnowShader.method388();
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: экранный слой " + (var1x ? "включён" : "выключен"));
         return 1;
      }));
      literalArgumentBuilder.then(literal("clear").executes(commandContext -> {
         VOXELS.clear();
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: хранилище очищено");
         return 1;
      }));
   }

   private int fill(int integer) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.level() != null) {
         VOXELS.clear();
         long nanoTime = System.nanoTime();
         SnowStats.Entry entry = SnowStats.method803(player.level(), VOXELS, player.getBlockX(), player.getBlockY(), player.getBlockZ(), integer, 0.35F);
         long l = (System.nanoTime() - nanoTime) / 1000L;
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: " + entry + ", за " + l + " мкс");
         this.stats();
         return 1;
      } else {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: нет мира");
         return 1;
      }
   }

   private int mesh(int integer) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: нет мира");
         return 1;
      } else if (VOXELS.chunkCount() == 0) {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: хранилище пустое, сначала snow fill");
         return 1;
      } else {
         int n = integer * 2 * 4;
         int n2 = SparseVoxelGrid.blockToVoxel(player.getBlockX() - integer);
         int n3 = SparseVoxelGrid.blockToVoxel(player.getBlockY() - 8);
         int n4 = SparseVoxelGrid.blockToVoxel(player.getBlockZ() - integer);
         byte b = 96;
         long nanoTime = System.nanoTime();
         MeshImporter.Entry entry = MeshImporter.method658(VOXELS, SnowVisibility.getFloatArrayBuffer(), n2, n3, n4, n, b, n);
         long l = (System.nanoTime() - nanoTime) / 1000L;
         SnowVisibility.method2401(n2, n3, n4);
         ChatLogger.method2020(
            ChatLogger.Type.INFO,
            "Снег: сетка — "
               + entry
               + ", квадратов "
               + SnowVisibility.getFloatArrayBuffer().getIntValue()
               + ", память "
               + SnowVisibility.getFloatArrayBuffer().method386() / 1024L
               + " КБ, за "
               + l
               + " мкс"
         );
         return 1;
      }
   }

   private void stats() {
      ChatLogger.method2020(
         ChatLogger.Type.INFO,
         "Снег: чанков "
            + VOXELS.chunkCount()
            + " (однородных "
            + VOXELS.uniformChunkCount()
            + "), вокселей "
            + VOXELS.positiveVoxelCount()
            + ", память "
            + VOXELS.estimatedMemoryBytes() / 1024L
            + " КБ"
      );
   }

   private void check() {
      List<String> strings = MeshValidator.method6095(VOXELS);
      if (strings.isEmpty()) {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: проверка пройдена, ошибок нет");
      } else {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: найдено проблем — " + strings.size());

         for (String str : strings) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "  " + str);
         }
      }
   }
}

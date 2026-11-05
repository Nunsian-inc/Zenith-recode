package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.level.block.state.BlockState;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.PlayerArgumentType;
import org.zenith.client.events.TickEvent;

public class TPCommand extends CommandAbstract {
   private static final int WORLD_LIMIT = 29999984;
   private static final int MOVE_COOLDOWN = 2;
   private final Minecraft mc = Minecraft.getInstance();
   private ClientLevel startWorld;
   private boolean coordinateTeleportActive;
   private int targetX;
   private int targetZ;
   private int cooldown;

   public TPCommand() {
      super("tp");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Использование: .tp <ник> или .tp <x> <z>");
         return 1;
      });
      literalArgumentBuilder.then(arg("player", PlayerArgumentType.create()).executes(var1x -> {
         this.teleportTo((String)var1x.getArgument("player", String.class));
         return 1;
      }));
      literalArgumentBuilder.then(arg("x", IntegerArgumentType.integer(-29999984, 29999984)).then(arg("z", IntegerArgumentType.integer(-29999984, 29999984)).executes(var1x -> {
         this.startCoordinateTeleport((Integer)var1x.getArgument("x", Integer.class), (Integer)var1x.getArgument("z", Integer.class));
         return 1;
      })));
   }

   private void startCoordinateTeleport(int targetX, int targetZ) {
      this.stopCoordinateTeleport();
      if (this.mc.player == null || this.mc.level == null) {
         ChatLogger.method2020(ChatLogger.Type.ERROR, "Мир или игрок ещё не загружен");
      } else if (!this.mc.player.getAbilities().flying) {
         ChatLogger.method2020(ChatLogger.Type.ERROR, "Для телепортации по координатам нужен режим полёта");
      } else {
         this.targetX = targetX;
         this.targetZ = targetZ;
         this.startWorld = this.mc.level;
         this.coordinateTeleportActive = true;
         ChatLogger.method2020(ChatLogger.Type.INFO, "Телепортация на координаты " + targetX + " " + targetZ + " начата");
      }
   }

   @EventTarget
   public void onTick(TickEvent event) {
      if (this.coordinateTeleportActive) {
         if (this.mc.player == null || this.mc.level == null || this.mc.level != this.startWorld || !this.mc.player.getAbilities().flying) {
            this.stopCoordinateTeleport();
            ChatLogger.method2020(ChatLogger.Type.WARN, "Телепортация отменена");
         } else if (this.cooldown > 0) {
            this.cooldown--;
         } else if (this.isChunkLoaded(this.mc.level, this.targetX, this.targetZ)) {
            double highestSolidY = this.findHighestSolidY(this.mc.level, this.targetX, this.targetZ);
            if (highestSolidY == Double.MIN_VALUE) {
               this.stopCoordinateTeleport();
               ChatLogger.method2020(ChatLogger.Type.ERROR, "Не найден твёрдый блок на координатах " + this.targetX + " " + this.targetZ);
            } else {
               this.sendPosition(this.targetX + 0.5, highestSolidY, this.targetZ + 0.5);
               ChatLogger.method2020(ChatLogger.Type.INFO, "Телепортация завершена: " + this.targetX + " " + this.targetZ);
               this.stopCoordinateTeleport();
            }
         } else {
            double x = this.mc.player.getX();
            double z = this.mc.player.getZ();
            double d = this.targetX + 0.5 - x;
            double y = this.targetZ + 0.5 - z;
            double hypot = Math.hypot(d, y);
            if (!(hypot < 1.0)) {
               double maxLoadedStep = this.maxLoadedStep(this.mc.level, x, z, d, y, hypot);
               if (!(maxLoadedStep <= 1.0)) {
                  this.sendPosition(x + d / hypot * maxLoadedStep, this.mc.player.getY(), z + y / hypot * maxLoadedStep);
                  this.cooldown = 2;
               }
            }
         }
      }
   }

   private void teleportTo(String argument) {
      this.stopCoordinateTeleport();
      if (this.mc.player != null && this.mc.level != null) {
         AbstractClientPlayer player = this.findPlayer(argument);
         if (player == null) {
            ChatLogger.method2020(ChatLogger.Type.ERROR, "Игрок " + argument + " не найден");
         } else {
            double d = this.mc.player.distanceTo(player);
            double x = player.getX();
            double z = player.getZ();
            double solidBlockY = this.findSolidBlockY(player);
            if (solidBlockY == Double.MIN_VALUE) {
               ChatLogger.method2020(ChatLogger.Type.ERROR, "Не удалось найти твёрдый блок рядом с игроком " + argument);
            } else {
               int n = Math.max((int)(d / 1000.0), 3);

               for (int i = 0; i < n; i++) {
                  this.mc.player.connection.send(new StatusOnly(this.mc.player.onGround(), this.mc.player.horizontalCollision));
               }

               this.mc.player.connection.send(new Pos(x, solidBlockY, z, false, this.mc.player.horizontalCollision));
               this.mc.player.setPos(x, solidBlockY, z);
               ChatLogger.method2020(ChatLogger.Type.INFO, "Телепортация к " + argument + " выполнена");
               ChatLogger.method2020(ChatLogger.Type.INFO, String.format("Координаты: %.1f %.1f %.1f", x, solidBlockY, z));
            }
         }
      } else {
         ChatLogger.method2020(ChatLogger.Type.ERROR, "Мир или игрок ещё не загружен");
      }
   }

   private double findSolidBlockY(AbstractClientPlayer player) {
      ClientLevel level = this.mc.level;
      if (level == null) {
         return Double.MIN_VALUE;
      } else {
         BlockPos blockPosition = player.blockPosition();

         for (int i = blockPosition.getY() - 1; i >= level.getMinY(); i--) {
            BlockPos pos = new BlockPos(blockPosition.getX(), i, blockPosition.getZ());
            if (this.isSolid(level, pos)) {
               return i + 0.25;
            }
         }

         for (int i = blockPosition.getY() + 1; i <= level.getMaxY(); i++) {
            BlockPos pos = new BlockPos(blockPosition.getX(), i, blockPosition.getZ());
            if (this.isSolid(level, pos)) {
               return i + 0.25;
            }
         }

         return Double.MIN_VALUE;
      }
   }

   private boolean isSolid(ClientLevel level, BlockPos pos) {
      BlockState blockState = level.getBlockState(pos);
      return blockState.isRedstoneConductor(level, pos);
   }

   private double findHighestSolidY(ClientLevel level, int targetX, int targetZ) {
      for (int i = level.getMaxY(); i >= level.getMinY(); i--) {
         BlockPos pos = new BlockPos(targetX, i, targetZ);
         if (this.isSolid(level, pos)) {
            return i + 1.0;
         }
      }

      return Double.MIN_VALUE;
   }

   private boolean isChunkLoaded(ClientLevel level, double targetX, double targetZ) {
      return level.hasChunk((int)Math.floor(targetX) >> 4, (int)Math.floor(targetZ) >> 4);
   }

   private double maxLoadedStep(ClientLevel level, double x, double z, double d, double y, double hypot) {
      double d2 = d / hypot;
      double d3 = y / hypot;
      double d4 = 0.0;
      double d5 = 0.0;

      while (d5 <= hypot && this.isChunkLoaded(level, x + d2 * d5, z + d3 * d5)) {
         d4 = d5++;
      }

      return d4;
   }

   private void sendPosition(double x, double highestSolidY, double z) {
      if (this.mc.player != null) {
         this.mc.player.connection.send(new Pos(x, highestSolidY, z, false, this.mc.player.horizontalCollision));
         this.mc.player.setPos(x, highestSolidY, z);
      }
   }

   private void stopCoordinateTeleport() {
      this.coordinateTeleportActive = false;
      this.startWorld = null;
      this.cooldown = 0;
   }

   private AbstractClientPlayer findPlayer(String argument) {
      if (this.mc.level == null) {
         return null;
      } else {
         for (AbstractClientPlayer player : this.mc.level.players()) {
            if (player.getGameProfile().name().equalsIgnoreCase(argument)) {
               return player;
            }
         }

         return null;
      }
   }
}

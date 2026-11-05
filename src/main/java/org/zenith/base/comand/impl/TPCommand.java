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
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Использование: .tp <ник> или .tp <x> <z>");
         return 1;
      });
      var1.then(arg("player", PlayerArgumentType.create()).executes(var1x -> {
         this.teleportTo((String)var1x.getArgument("player", String.class));
         return 1;
      }));
      var1.then(arg("x", IntegerArgumentType.integer(-29999984, 29999984)).then(arg("z", IntegerArgumentType.integer(-29999984, 29999984)).executes(var1x -> {
         this.startCoordinateTeleport((Integer)var1x.getArgument("x", Integer.class), (Integer)var1x.getArgument("z", Integer.class));
         return 1;
      })));
   }

   private void startCoordinateTeleport(int var1, int var2) {
      this.stopCoordinateTeleport();
      if (this.mc.player == null || this.mc.level == null) {
         ChatLogger.method2020(ChatLogger.Type.ERROR, "Мир или игрок ещё не загружен");
      } else if (!this.mc.player.getAbilities().flying) {
         ChatLogger.method2020(ChatLogger.Type.ERROR, "Для телепортации по координатам нужен режим полёта");
      } else {
         this.targetX = var1;
         this.targetZ = var2;
         this.startWorld = this.mc.level;
         this.coordinateTeleportActive = true;
         ChatLogger.method2020(ChatLogger.Type.INFO, "Телепортация на координаты " + var1 + " " + var2 + " начата");
      }
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      if (this.coordinateTeleportActive) {
         if (this.mc.player == null || this.mc.level == null || this.mc.level != this.startWorld || !this.mc.player.getAbilities().flying) {
            this.stopCoordinateTeleport();
            ChatLogger.method2020(ChatLogger.Type.WARN, "Телепортация отменена");
         } else if (this.cooldown > 0) {
            this.cooldown--;
         } else if (this.isChunkLoaded(this.mc.level, this.targetX, this.targetZ)) {
            double var14 = this.findHighestSolidY(this.mc.level, this.targetX, this.targetZ);
            if (var14 == Double.MIN_VALUE) {
               this.stopCoordinateTeleport();
               ChatLogger.method2020(ChatLogger.Type.ERROR, "Не найден твёрдый блок на координатах " + this.targetX + " " + this.targetZ);
            } else {
               this.sendPosition(this.targetX + 0.5, var14, this.targetZ + 0.5);
               ChatLogger.method2020(ChatLogger.Type.INFO, "Телепортация завершена: " + this.targetX + " " + this.targetZ);
               this.stopCoordinateTeleport();
            }
         } else {
            double var2 = this.mc.player.getX();
            double var4 = this.mc.player.getZ();
            double var6 = this.targetX + 0.5 - var2;
            double var8 = this.targetZ + 0.5 - var4;
            double var10 = Math.hypot(var6, var8);
            if (!(var10 < 1.0)) {
               double var12 = this.maxLoadedStep(this.mc.level, var2, var4, var6, var8, var10);
               if (!(var12 <= 1.0)) {
                  this.sendPosition(var2 + var6 / var10 * var12, this.mc.player.getY(), var4 + var8 / var10 * var12);
                  this.cooldown = 2;
               }
            }
         }
      }
   }

   private void teleportTo(String var1) {
      this.stopCoordinateTeleport();
      if (this.mc.player != null && this.mc.level != null) {
         AbstractClientPlayer var2 = this.findPlayer(var1);
         if (var2 == null) {
            ChatLogger.method2020(ChatLogger.Type.ERROR, "Игрок " + var1 + " не найден");
         } else {
            double var3 = this.mc.player.distanceTo(var2);
            double var5 = var2.getX();
            double var7 = var2.getZ();
            double var9 = this.findSolidBlockY(var2);
            if (var9 == Double.MIN_VALUE) {
               ChatLogger.method2020(ChatLogger.Type.ERROR, "Не удалось найти твёрдый блок рядом с игроком " + var1);
            } else {
               int var11 = Math.max((int)(var3 / 1000.0), 3);

               for (int var12 = 0; var12 < var11; var12++) {
                  this.mc.player.connection.send(new StatusOnly(this.mc.player.onGround(), this.mc.player.horizontalCollision));
               }

               this.mc.player.connection.send(new Pos(var5, var9, var7, false, this.mc.player.horizontalCollision));
               this.mc.player.setPos(var5, var9, var7);
               ChatLogger.method2020(ChatLogger.Type.INFO, "Телепортация к " + var1 + " выполнена");
               ChatLogger.method2020(ChatLogger.Type.INFO, String.format("Координаты: %.1f %.1f %.1f", var5, var9, var7));
            }
         }
      } else {
         ChatLogger.method2020(ChatLogger.Type.ERROR, "Мир или игрок ещё не загружен");
      }
   }

   private double findSolidBlockY(AbstractClientPlayer var1) {
      ClientLevel var2 = this.mc.level;
      if (var2 == null) {
         return Double.MIN_VALUE;
      } else {
         BlockPos var3 = var1.blockPosition();

         for (int var4 = var3.getY() - 1; var4 >= var2.getMinY(); var4--) {
            BlockPos var5 = new BlockPos(var3.getX(), var4, var3.getZ());
            if (this.isSolid(var2, var5)) {
               return var4 + 0.25;
            }
         }

         for (int var6 = var3.getY() + 1; var6 <= var2.getMaxY(); var6++) {
            BlockPos var7 = new BlockPos(var3.getX(), var6, var3.getZ());
            if (this.isSolid(var2, var7)) {
               return var6 + 0.25;
            }
         }

         return Double.MIN_VALUE;
      }
   }

   private boolean isSolid(ClientLevel var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      return var3.isRedstoneConductor(var1, var2);
   }

   private double findHighestSolidY(ClientLevel var1, int var2, int var3) {
      for (int var4 = var1.getMaxY(); var4 >= var1.getMinY(); var4--) {
         BlockPos var5 = new BlockPos(var2, var4, var3);
         if (this.isSolid(var1, var5)) {
            return var4 + 1.0;
         }
      }

      return Double.MIN_VALUE;
   }

   private boolean isChunkLoaded(ClientLevel var1, double var2, double var4) {
      return var1.hasChunk((int)Math.floor(var2) >> 4, (int)Math.floor(var4) >> 4);
   }

   private double maxLoadedStep(ClientLevel var1, double var2, double var4, double var6, double var8, double var10) {
      double var12 = var6 / var10;
      double var14 = var8 / var10;
      double var16 = 0.0;
      double var18 = 0.0;

      while (var18 <= var10 && this.isChunkLoaded(var1, var2 + var12 * var18, var4 + var14 * var18)) {
         var16 = var18++;
      }

      return var16;
   }

   private void sendPosition(double var1, double var3, double var5) {
      if (this.mc.player != null) {
         this.mc.player.connection.send(new Pos(var1, var3, var5, false, this.mc.player.horizontalCollision));
         this.mc.player.setPos(var1, var3, var5);
      }
   }

   private void stopCoordinateTeleport() {
      this.coordinateTeleportActive = false;
      this.startWorld = null;
      this.cooldown = 0;
   }

   private AbstractClientPlayer findPlayer(String var1) {
      if (this.mc.level == null) {
         return null;
      } else {
         for (AbstractClientPlayer var3 : this.mc.level.players()) {
            if (var3.getGameProfile().name().equalsIgnoreCase(var1)) {
               return var3;
            }
         }

         return null;
      }
   }
}

package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.bot.client.BotRctService;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.events.BotUpdateEvent;
import org.zenith.client.modules.impl.player.WarpFarmModule;
import org.zenith.utility.game.Pathfinder;
import org.zenith.utility.misc.ViaCompat;

@ModuleInfo(
   name = "BotWarpFarm",
   category = ModuleCategory.PLAYER,
   description = "module.botwarpfarm.description"
)
public class BotWarpFarm extends BotModule {
   private static final int WARP_COOLDOWN_TICKS = 100;
   private static final int SETTLE_TICKS = 80;
   private static final int RCT_RETRY_COOLDOWN_TICKS = 40;
   private static final int PATH_FAIL_LIMIT = 5;
   private int warpCooldownTicks;
   private int settleTicks;
   private int rctRetryCooldownTicks;
   private int pathFailStreak;
   private int lastAnarchy = Integer.MIN_VALUE;
   private long observedEvacuationSequence;
   private BlockPos walkTarget;
   private BlockPos nextRouteBlock;
   private BotWorld world;
   private BotPlayer player;

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.world = this.bot().getWorld();
      this.player = this.bot().getPlayer();
      this.stopWalking();
      this.applyInput(false, false);
      this.resetState();
      super.onDisable();
   }

   @EventTarget
   public void onBotUpdate(BotUpdateEvent event) {
      this.world = event.getWorld();
      this.player = event.getPlayer();
      BotPlayHandler handler = this.handler();
      if (this.world != null && this.player != null && handler != null) {
         if (!this.handleEmergencyEvacuation(handler)) {
            this.tickCooldowns();
            boolean tickFarm = this.tickFarm(handler);
            if (tickFarm && this.nextRouteBlock != null) {
               this.rotateToRouteBlock();
               this.applyRouteMovement();
            } else {
               this.applyInput(false, false);
            }
         }
      }
   }

   private boolean tickFarm(BotPlayHandler handler) {
      if (this.player.isDeadOrDying() || this.player.getHealth() <= 0.0F) {
         this.stopWalking();
         return false;
      } else if (handler.hasOpenScreen()) {
         return false;
      } else {
         int nextInt = WarpFarmModule.getIntValue17();
         if (nextInt == -1) {
            this.stopWalking();
            return false;
         } else if (this.rct().isActive()) {
            this.stopWalking();
            return false;
         } else {
            int currentAnarchyHere = this.rct().currentAnarchyHere();
            if (currentAnarchyHere != this.lastAnarchy) {
               this.lastAnarchy = currentAnarchyHere;
               this.settleTicks = 80;
               this.stopWalking();
            }

            if (currentAnarchyHere != nextInt) {
               this.stopWalking();
               if (this.rctRetryCooldownTicks > 0) {
                  return false;
               } else {
                  this.rctRetryCooldownTicks = 40;
                  this.bot().systemMessage("BotWarpFarm: иду на анку №" + nextInt + " за маином");
                  this.rct().reconnect(nextInt);
                  return false;
               }
            } else {
               double horizontalDistanceSq = this.horizontalDistanceSq(Vec3.atCenterOf(WarpFarmModule.blockPos));
               if (horizontalDistanceSq > 40000.0) {
                  this.stopWalking();
                  this.sendWarpPvp(handler);
                  return false;
               } else if (this.settleTicks > 0) {
                  this.stopWalking();
                  return false;
               } else {
                  Entity hologram = this.findHologram();
                  if (hologram != null) {
                     this.setWalkTarget(hologram.blockPosition());
                  } else {
                     if (!(horizontalDistanceSq > 4.0)) {
                        this.stopWalking();
                        return false;
                     }

                     this.setWalkTarget(WarpFarmModule.blockPos);
                  }

                  this.updateRoute(handler);
                  return true;
               }
            }
         }
      }
   }

   private boolean handleEmergencyEvacuation(BotPlayHandler handler) {
      long observedEvacuationSequence = WarpFarmModule.method4114();
      if (observedEvacuationSequence == this.observedEvacuationSequence) {
         return false;
      } else {
         this.stopWalking();
         this.applyInput(false, false);
         if (!this.player.isDeadOrDying() && !(this.player.getHealth() <= 0.0F)) {
            this.observedEvacuationSequence = observedEvacuationSequence;
            this.rct().stop();
            handler.sendCommand("hub");
            this.settleTicks = 80;
            return true;
         } else {
            return true;
         }
      }
   }

   private void tickCooldowns() {
      if (this.warpCooldownTicks > 0) {
         this.warpCooldownTicks--;
      }

      if (this.settleTicks > 0) {
         this.settleTicks--;
      }

      if (this.rctRetryCooldownTicks > 0) {
         this.rctRetryCooldownTicks--;
      }
   }

   private void sendWarpPvp(BotPlayHandler handler) {
      if (this.warpCooldownTicks <= 0) {
         handler.sendCommand("warp pvp");
         this.warpCooldownTicks = 100;
         this.settleTicks = 80;
      }
   }

   private void setWalkTarget(BlockPos walkTarget) {
      if (!walkTarget.equals(this.walkTarget)) {
         this.walkTarget = walkTarget;
         this.nextRouteBlock = null;
      }
   }

   private void updateRoute(BotPlayHandler handler) {
      this.nextRouteBlock = null;
      if (this.walkTarget != null) {
         BlockPos blockPosition = WarpFarmModule.method4126(this.world, this.player.position(), this.player.blockPosition());
         Pathfinder.Entry2 entry2 = Pathfinder.method759(this.world, blockPosition, this.walkTarget, WarpFarmModule.data).orElse(null);
         if (entry2 == null) {
            if (!this.player.onGround()) {
               this.nextRouteBlock = this.walkTarget;
               this.pathFailStreak = 0;
            } else {
               if (++this.pathFailStreak >= 5) {
                  this.pathFailStreak = 0;
                  this.stopWalking();
                  this.sendWarpPvp(handler);
               }
            }
         } else {
            this.pathFailStreak = 0;
            this.nextRouteBlock = WarpFarmModule.method4127(entry2, this.player.position());
         }
      }
   }

   private void rotateToRouteBlock() {
      Vec3 vec = Pathfinder.method761(this.nextRouteBlock);
      Vec3 eyePosition = this.player.getEyePosition(1.0F);
      Rotation rotation = Rotation.lookingAt(new Vec3(vec.x, eyePosition.y, vec.z), eyePosition);
      this.player.setYRot(Mth.wrapDegrees(rotation.getFloatValue()));
      this.player.setXRot(Mth.clamp(rotation.getFloatValue2(), -90.0F, 90.0F));
   }

   private void applyRouteMovement() {
      this.applyInput(true, true);
   }

   private void applyInput(boolean forward, boolean jump) {
      if (this.player != null) {
         ViaCompat.method4848(this.player.input, forward ? 1.0F : 0.0F);
         ViaCompat.method4849(this.player.input, 0.0F);
         this.player.input.keyPresses = new Input(forward, false, false, false, jump, false, forward);
      }
   }

   private void stopWalking() {
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }

   private Entity findHologram() {
      String lowerCase = WarpFarmModule.method4115().toLowerCase(Locale.ROOT);
      Entity entity = null;
      double d = 16384.0;

      for (Entity entity2 : this.world.entitiesForRendering()) {
         if (entity2 != this.player) {
            double d2 = this.player.distanceToSqr(entity2);
            if (!(d2 > d)) {
               Component text = entity2 instanceof TextDisplay textDisplay ? textDisplay.getText() : entity2.getCustomName();
               if (text != null && text.getString().toLowerCase(Locale.ROOT).contains(lowerCase)) {
                  entity = entity2;
                  d = d2;
               }
            }
         }
      }

      return entity;
   }

   private double horizontalDistanceSq(Vec3 atCenterOf) {
      double d = this.player.getX() - atCenterOf.x;
      double d2 = this.player.getZ() - atCenterOf.z;
      return d * d + d2 * d2;
   }

   private BotRctService rct() {
      return this.bot().getRct();
   }

   private void resetState() {
      this.warpCooldownTicks = 0;
      this.settleTicks = 0;
      this.rctRetryCooldownTicks = 0;
      this.pathFailStreak = 0;
      this.lastAnarchy = Integer.MIN_VALUE;
      this.observedEvacuationSequence = WarpFarmModule.method4114();
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }
}

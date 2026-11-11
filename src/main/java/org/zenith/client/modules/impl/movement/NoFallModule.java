package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.TimerSpeed;

@ModuleInfo(
   name = "NoFall",
   description = "module.nofall.description",
   category = ModuleCategory.MOVEMENT
)
public final class NoFallModule extends Module {
   public static final NoFallModule INSTANCE = new NoFallModule();
   private static final double FALL_DISTANCE_THRESHOLD = 3.0;
   private final ModeSetting mode = new ModeSetting("module.noFall.mode", "module.noFall.mode.desc", "module.noFall.mode.grim72", "module.noFall.mode.grim73");
   private final StopWatch activationTimer = new StopWatch();
   private final StopWatch fallFlyingTimer = new StopWatch();
   private boolean falling;
   private boolean awaitingCorrection;
   private int correctionTicks;
   private boolean timerModified;

   private NoFallModule() {
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.falling = false;
      this.awaitingCorrection = false;
      this.correctionTicks = 0;
      if (this.timerModified) {
         TimerSpeed.setSpeedMultiplier(1.0F);
         this.timerModified = false;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         this.falling = mc.player.fallDistance > 3.0;
         if (this.isGrim72Mode()) {
            if (this.falling && !mc.player.onGround() && this.fallFlyingTimer.hasElapsed(200L) && PlayerSimulation.method6031(1).flag4) {
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
               this.fallFlyingTimer.reset();
               this.activationTimer.reset();
               this.awaitingCorrection = true;
            }
         } else if (this.correctionTicks == 2) {
            TimerSpeed.setSpeedMultiplier(0.5F);
            this.timerModified = true;
         } else if (this.timerModified) {
            TimerSpeed.setSpeedMultiplier(1.0F);
            this.timerModified = false;
         }

         if (this.correctionTicks > 0) {
            this.correctionTicks--;
         }

         if (this.activationTimer.hasElapsed(300L)) {
            this.awaitingCorrection = false;
            this.correctionTicks = 0;
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null) {
         if (event.isOutgoing()) {
            if (this.isGrim73Mode()
               && event.getPacket() instanceof ServerboundMovePlayerPacket movePlayerPacket
               && !PacketUtils.getItems().contains(movePlayerPacket)
               && this.falling
               && movePlayerPacket.isOnGround()
               && this.correctionTicks == 0) {
               PacketUtils.method3150(new StatusOnly(true, false));
               this.activationTimer.reset();
               this.awaitingCorrection = true;
            }
         } else if (!(event.getPacket() instanceof ClientboundLoginPacket) && !(event.getPacket() instanceof ClientboundRespawnPacket)) {
            if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
               mc.execute(() -> {
                  if (this.awaitingCorrection) {
                     this.correctionTicks = 2;
                     this.awaitingCorrection = false;
                  }
               });
            }
         } else {
            mc.execute(this::reset);
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.awaitingCorrection || this.correctionTicks == 2) {
         event.setJump(false);
      } else if (this.correctionTicks == 1) {
         event.setJump(true);
      }
   }

   private boolean isGrim72Mode() {
      return this.mode.isSelected(0);
   }

   private boolean isGrim73Mode() {
      return this.mode.isSelected(1);
   }
}

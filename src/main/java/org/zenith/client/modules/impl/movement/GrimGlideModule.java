package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ElytraMath;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.TimerSpeed;

@ModuleInfo(
   name = "Grim Glide",
   category = ModuleCategory.MOVEMENT,
   description = "module.grimglide.description"
)
public final class GrimGlideModule extends Module {
   public static final GrimGlideModule INSTANCE = new GrimGlideModule();
   private final SliderSetting timerBoost = new SliderSetting("module.grimGlide.timerBoost", 1.5F, 1.0F, 5.0F, 0.1F, "module.grimGlide.timerBoost.desc", "x");
   private final SliderSetting timerReset = new SliderSetting("module.grimGlide.timerReset", 0.5F, 0.0F, 0.9F, 0.01F, "module.grimGlide.timerReset.desc", "x");
   private int correctionCooldownTicks;
   private boolean skipNextMovementPacket;
   private int glideTicks;
   private boolean timerModified;

   private GrimGlideModule() {
   }

   @Override
   public void onEnable() {
      this.correctionCooldownTicks = 0;
      this.skipNextMovementPacket = false;
      this.glideTicks = 0;
      this.timerModified = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.timerModified) {
         TimerSpeed.setSpeedMultiplier(1.0F);
         this.timerModified = false;
      }

      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.correctionCooldownTicks > 0) {
         this.correctionCooldownTicks--;
      }

      boolean skipNextMovementPacket = mc.player != null && mc.level != null && mc.player.isFallFlying();
      if (!skipNextMovementPacket) {
         if (this.timerModified) {
            TimerSpeed.setSpeedMultiplier(1.0F);
            this.timerModified = false;
         }
      } else {
         TimerSpeed.setSpeedMultiplier(this.glideTicks % 2 == 0 ? this.timerBoost.getValue3() : this.timerReset.getValue3());
         this.timerModified = true;
         this.glideTicks++;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null) {
         if (event.isIncoming() && event.getPacket() instanceof ClientboundPlayerPositionPacket) {
            mc.execute(() -> {
               this.correctionCooldownTicks = 2;
               this.skipNextMovementPacket = true;
               if (this.glideTicks % 2 == 1) {
                  this.glideTicks++;
               }
            });
         } else {
            if (event.isOutgoing() && event.getPacket() instanceof ServerboundMovePlayerPacket movePlayerPacket && !PacketUtils.getItems().contains(movePlayerPacket)) {
               if (mc.player.isFallFlying() && this.correctionCooldownTicks == 0 && !this.skipNextMovementPacket) {
                  event.setPacket(new StatusOnly(true, true));
               }

               this.skipNextMovementPacket = false;
            }
         }
      }
   }

   public Vec3 method5976(Vec3 velocity, Vec3 originalVelocity, Vec3 lookAngle) {
      if (this.isEnabled() && mc.player != null) {
         Vec3 glideVelocity = ElytraMath.method5974(mc.player, velocity, lookAngle, 3.2);
         if (this.correctionCooldownTicks >= 1) {
            double yawRadians = Math.toRadians(mc.player.getYRot());
            return glideVelocity.multiply(0.99F, 0.98F, 0.99F).add(-Math.sin(yawRadians) * 0.09F, 0.03F, Math.cos(yawRadians) * 0.09F);
         } else {
            return glideVelocity.multiply(0.3F, 0.3F, 0.3F);
         }
      } else {
         return originalVelocity;
      }
   }
}

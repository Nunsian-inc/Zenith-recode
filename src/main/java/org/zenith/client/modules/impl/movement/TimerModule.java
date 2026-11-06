package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.util.Mth;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.TimerSpeed;
import org.zenith.utility.misc.ViaCompat;

@ModuleInfo(
   name = "Timer",
   description = "module.timer.description",
   category = ModuleCategory.MOVEMENT
)
public final class TimerModule extends Module {
   public static final TimerModule INSTANCE = new TimerModule();
   private final ModeSetting mode = new ModeSetting("module.timer.mode", "module.timer.mode.desc", "module.timer.mode.normal", "module.timer.mode.grim");
   private final SliderSetting speed = new SliderSetting("module.timer.speed", 2.0F, 0.1F, 10.0F, 0.1F, "module.timer.speed.desc", "x");
   private final BindSetting boostKey = new BindSetting("module.timer.boostKey", "module.timer.boostKey.desc", -1, () -> this.mode.isSelected(1));
   private final ModeSetting onFlag = new ModeSetting(
      "module.timer.onFlag", "module.timer.onFlag.desc", "module.timer.onFlag.reset", "module.timer.onFlag.disable", "module.timer.onFlag.none"
   );
   private float timerSpeed = 1.0F;
   private float energy;
   private long lastEnergyResetTime;
   private long lastCorrectionTime;

   private TimerModule() {
   }

   @Override
   public void onEnable() {
      this.timerSpeed = 1.0F;
      this.energy = 0.0F;
      if (this.mode.isSelected(1)) {
         this.lastEnergyResetTime = System.currentTimeMillis();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.setTimerSpeed(1.0F);
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (this.mode.isSelected(0)) {
            this.setTimerSpeed(this.speed.getValue3());
         } else if (this.mode.isSelected(1)) {
            long timeSinceCorrection = System.currentTimeMillis() - this.lastCorrectionTime;
            if (!(this.energy <= 0.0F) && this.isBoostKeyDown(this.boostKey.getKeyCode()) && timeSinceCorrection >= 2000L) {
               this.energy = Mth.clamp(this.energy - (0.0025F * this.speed.getValue3() - 0.0025F), 0.0F, 1.0F);
               this.setTimerSpeed(Math.max(this.speed.getValue3(), 1.0F));
            } else {
               this.setTimerSpeed(1.0F);
            }
         } else {
            this.setTimerSpeed(1.0F);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null && mc.level != null && event.isIncoming()) {
         Packet packet = event.getPacket();
         if (this.mode.isSelected(1) && packet instanceof ClientboundPingPacket && System.currentTimeMillis() - this.lastCorrectionTime > 2000L) {
            if (System.currentTimeMillis() - this.lastEnergyResetTime > 25000L) {
               this.lastEnergyResetTime = System.currentTimeMillis();
               this.energy = 0.0F;
               return;
            }

            if (!this.hasMovementInput()) {
               this.energy = Mth.clamp(this.energy + 0.005F, 0.0F, 1.0F);
            }

            event.cancel();
         }

         if (packet instanceof ClientboundPlayerPositionPacket) {
            this.lastCorrectionTime = System.currentTimeMillis();
            if (this.onFlag.isSelected(0)) {
               this.setTimerSpeed(1.0F);
               this.energy = 0.0F;
            } else if (this.onFlag.isSelected(1)) {
               this.energy = 0.0F;
               mc.execute(() -> {
                  if (this.isEnabled()) {
                     this.disableWithMessage("Отключён т.к. тебя флагнуло!");
                  }
               });
            }
         }

         if (packet instanceof ClientboundSetEntityMotionPacket setEntityMotionPacket && this.mode.isSelected(1) && setEntityMotionPacket.id() == mc.player.getId()) {
            this.setTimerSpeed(1.0F);
            this.energy = 0.0F;
         }
      }
   }

   public float method130() {
      return this.isEnabled() ? this.timerSpeed : 1.0F;
   }

   public float method131() {
      if (!this.isEnabled()) {
         return 0.0F;
      } else {
         return this.mode.isSelected(0) ? 1.0F : Mth.clamp(this.energy, 0.0F, 1.0F);
      }
   }

   private void setTimerSpeed(float timerSpeed) {
      this.timerSpeed = timerSpeed;
      TimerSpeed.setSpeedMultiplier(timerSpeed);
   }

   private void disableWithMessage(String text) {
      Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Component.nullToEmpty(text));
      this.setToggled(false);
   }

   private boolean hasMovementInput() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F;
   }

   private boolean isBoostKeyDown(int keyCode) {
      return keyCode != -1 && org.zenith.utility.misc.KeyCompat.isKeyDown(keyCode);
   }
}

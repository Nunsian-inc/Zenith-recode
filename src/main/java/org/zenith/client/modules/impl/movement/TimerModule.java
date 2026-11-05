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
import org.zenith.utility.misc.Util13;
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
   private float floatValue = 1.0F;
   private float energy;
   private long longValue;
   private long longValue2;

   private TimerModule() {
   }

   @Override
   public void onEnable() {
      this.floatValue = 1.0F;
      this.energy = 0.0F;
      if (this.mode.isSelected(1)) {
         this.longValue = System.currentTimeMillis();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method132(1.0F);
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (this.mode.isSelected(0)) {
            this.method132(this.speed.getValue3());
         } else if (this.mode.isSelected(1)) {
            long l = System.currentTimeMillis() - this.longValue2;
            if (!(this.energy <= 0.0F) && this.method135(this.boostKey.getKeyCode()) && l >= 2000L) {
               this.energy = Mth.clamp(this.energy - (0.0025F * this.speed.getValue3() - 0.0025F), 0.0F, 1.0F);
               this.method132(Math.max(this.speed.getValue3(), 1.0F));
            } else {
               this.method132(1.0F);
            }
         } else {
            this.method132(1.0F);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null && mc.level != null && event.method7100()) {
         Packet packet3 = event.getPacket3();
         if (this.mode.isSelected(1) && packet3 instanceof ClientboundPingPacket && System.currentTimeMillis() - this.longValue2 > 2000L) {
            if (System.currentTimeMillis() - this.longValue > 25000L) {
               this.longValue = System.currentTimeMillis();
               this.energy = 0.0F;
               return;
            }

            if (!this.method134()) {
               this.energy = Mth.clamp(this.energy + 0.005F, 0.0F, 1.0F);
            }

            event.cancel();
         }

         if (packet3 instanceof ClientboundPlayerPositionPacket) {
            this.longValue2 = System.currentTimeMillis();
            if (this.onFlag.isSelected(0)) {
               this.method132(1.0F);
               this.energy = 0.0F;
            } else if (this.onFlag.isSelected(1)) {
               this.energy = 0.0F;
               mc.execute(() -> {
                  if (this.isEnabled()) {
                     this.method133("Отключён т.к. тебя флагнуло!");
                  }
               });
            }
         }

         if (packet3 instanceof ClientboundSetEntityMotionPacket setEntityMotionPacket && this.mode.isSelected(1) && setEntityMotionPacket.id() == mc.player.getId()) {
            this.method132(1.0F);
            this.energy = 0.0F;
         }
      }
   }

   public float method130() {
      return this.isEnabled() ? this.floatValue : 1.0F;
   }

   public float method131() {
      if (!this.isEnabled()) {
         return 0.0F;
      } else {
         return this.mode.isSelected(0) ? 1.0F : Mth.clamp(this.energy, 0.0F, 1.0F);
      }
   }

   private void method132(float floatValue) {
      this.floatValue = floatValue;
      Util13.method2404(floatValue);
   }

   private void method133(String text) {
      Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Component.nullToEmpty(text));
      this.setToggled(false);
   }

   private boolean method134() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F;
   }

   private boolean method135(int keyCode) {
      return keyCode != -1 && org.zenith.utility.misc.KeyCompat.isKeyDown(keyCode);
   }
}

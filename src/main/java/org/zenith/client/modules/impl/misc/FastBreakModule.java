package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "FastBreak",
   category = ModuleCategory.MISC,
   description = "module.fastbreak.description"
)
public final class FastBreakModule extends Module {
   public static final FastBreakModule INSTANCE = new FastBreakModule();
   private final ModeSetting mode = new ModeSetting("module.fastBreak.mode", "module.fastBreak.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.fastBreak.modeNormal").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.fastBreak.modeAir");
   private final SliderSetting breakDamage = new SliderSetting(
      "module.fastBreak.breakDamage", 0.7F, 0.1F, 1.0F, 0.05F, "module.fastBreak.breakDamage.desc", "%", this.entry2::isSelected, null
   );
   private final SliderSetting airMultiplier = new SliderSetting(
      "module.fastBreak.airMultiplier", 5.0F, 1.0F, 10.0F, 0.1F, "module.fastBreak.airMultiplier.desc", "x", this.entry2::isSelected, null
   );
   private final BooleanSetting bypass = new BooleanSetting("module.fastBreak.bypass", "module.fastBreak.bypass.desc", true);

   private FastBreakModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.entry.isSelected()) {
         mc.gameMode.destroyDelay = 0;
         if (mc.gameMode.destroyProgress > 0.0F) {
            mc.gameMode.destroyProgress = 1.0F;
         }
      } else if (mc.player != null && !mc.player.onGround()) {
         mc.gameMode.destroyDelay = 0;
         if (mc.gameMode.destroyProgress >= this.breakDamage.getValue3()) {
            mc.gameMode.destroyProgress = 1.0F;
         }
      }
   }

   public float method5660(float var1) {
      return this.isEnabled() && this.entry2.isSelected() ? var1 / this.airMultiplier.getValue3() : var1;
   }

   @EventTarget
   public void method5661(PacketEvent var1) {
      if (this.bypass.isEnabled()) {
         if (var1.getPacket3() instanceof ServerboundPlayerActionPacket var3 && var3.getAction() == Action.STOP_DESTROY_BLOCK) {
            BlockPos var4 = var3.getPos();
            if (var4 != null) {
               mc.getConnection().send(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, var4.above(), var3.getDirection()));
            }
         }
      }
   }
}

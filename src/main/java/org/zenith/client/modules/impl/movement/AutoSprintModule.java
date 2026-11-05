package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoSprint",
   category = ModuleCategory.MOVEMENT,
   description = "module.autosprint.description"
)
public final class AutoSprintModule extends Module {
   public static final AutoSprintModule INSTANCE = new AutoSprintModule();
   private final BooleanSetting keepSwing = new BooleanSetting("module.autoSprint.keepSwing", "module.autoSprint.keepSwing.desc", false);
   private boolean sprint = false;

   public boolean method6417() {
      return this.keepSwing.isEnabled();
   }

   private AutoSprintModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.options != null && !UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keySprint))) {
         mc.options.keySprint.setDown(false);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      mc.options.keySprint.setDown(true);
      if (this.keepSwing.isEnabled() && mc.player != null && mc.player.isUnderWater()) {
         mc.player.setSwimming(true);
      }
   }

   @EventTarget
   public void method6418(AttackEvent var1) {
      if (this.keepSwing.isEnabled()) {
         if (var1.getType4() == AttackEvent.Type.PRE) {
            this.sprint = mc.player.isSprinting();
         } else if (this.sprint) {
            mc.player.setSprinting(true);
         }
      }
   }

   public double method6419() {
      return 1.0;
   }
}

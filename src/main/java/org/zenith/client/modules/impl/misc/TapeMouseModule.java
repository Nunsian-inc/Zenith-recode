package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "TapeMouse",
   category = ModuleCategory.MISC,
   description = "module.tapemouse.description"
)
public final class TapeMouseModule extends Module {
   public static final TapeMouseModule INSTANCE = new TapeMouseModule();
   private final ModeSetting mouseButton = new ModeSetting(
      "module.tapeMouse.mouseButton", "module.tapeMouse.mouseButton.desc", "module.tapeMouse.right", "module.tapeMouse.left"
   );
   private final SliderSetting delay = new SliderSetting("module.tapeMouse.delay", 100.0F, 0.0F, 1000.0F, 10.0F, "module.tapeMouse.delay.desc", "ms");
   private final StopWatch stopWatch = new StopWatch();

   private TapeMouseModule() {
   }

   @Override
   public void onDisable() {
      this.method2780();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gui.screen() == null) {
         if (!this.stopWatch.method7546((long)this.delay.getValue3())) {
            this.method2780();
         } else {
            this.method2780();
            this.method2779(this.method2778());
            this.stopWatch.reset();
         }
      } else {
         this.method2780();
      }
   }

   private KeyMapping method2778() {
      return this.mouseButton.isSelected(0) ? mc.options.keyUse : mc.options.keyAttack;
   }

   private void method2779(KeyMapping keyMapping) {
      keyMapping.setDown(true);
      KeyMapping.click(KeyMappingHelper.getBoundKeyOf(keyMapping));
   }

   private void method2780() {
      mc.options.keyUse.setDown(false);
      mc.options.keyAttack.setDown(false);
   }
}

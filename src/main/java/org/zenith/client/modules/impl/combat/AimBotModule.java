package org.zenith.client.modules.impl.combat;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.screens.esp.SectionSettings;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ModuleHeaderSetting;

@ModuleInfo(
   name = "AimBot",
   category = ModuleCategory.COMBAT,
   description = "module.aimbot.description"
)
public final class AimBotModule extends Module {
   public static final AimBotModule INSTANCE = new AimBotModule();
   private final ButtonSetting bow = method1003("module.aimBot.bow", BowAimBotModule.INSTANCE);
   private final ButtonSetting trident = method1003("module.aimBot.trident", TridentAimbotModule.INSTANCE);
   private final List<Module> items = new ArrayList<>();

   private AimBotModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.bow, this.trident);
   }

   @Override
   public void onEnable() {
      super.onEnable();

      for (Module moduleValue : this.items) {
         if (!moduleValue.isEnabled()) {
            moduleValue.toggle();
         }
      }

      this.items.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();

      for (Module moduleValue : method1002()) {
         if (moduleValue.isEnabled()) {
            this.items.add(moduleValue);
            moduleValue.toggle();
         }
      }
   }

   private static List<Module> method1002() {
      return List.of(BowAimBotModule.INSTANCE, TridentAimbotModule.INSTANCE);
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method1002();
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      json.addProperty("enabled", true);
      return json;
   }

   private static ButtonSetting method1003(String key, Module sectionPreviewOwner) {
      var sectionSettings = SectionSettings.of(sectionPreviewOwner);
      return new ModuleHeaderSetting(key, sectionPreviewOwner, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(sectionPreviewOwner.getName(), sectionSettings, null, sectionPreviewOwner));
   }
}

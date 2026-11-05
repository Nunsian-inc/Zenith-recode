package org.zenith.client.modules.impl.render;

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
   name = "WorldTweaks",
   category = ModuleCategory.RENDER,
   description = "module.worldtweaks.description"
)
public final class WorldTweaksModule extends Module {
   public static final WorldTweaksModule INSTANCE = new WorldTweaksModule();
   private final ButtonSetting weather = method6197("module.worldTweaks.weather", WeatherModule.INSTANCE);
   private final ButtonSetting visuals = method6197("module.worldTweaks.visuals", WorldModule.INSTANCE);
   private final List<Module> items = new ArrayList<>();

   private WorldTweaksModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.weather, this.visuals);
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

      for (Module moduleValue : method6196()) {
         if (moduleValue.isEnabled()) {
            this.items.add(moduleValue);
            moduleValue.toggle();
         }
      }
   }

   private static List<Module> method6196() {
      return List.of(WeatherModule.INSTANCE, WorldModule.INSTANCE);
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method6196();
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      json.addProperty("enabled", true);
      return json;
   }

   private static ButtonSetting method6197(String key, Module sectionPreviewOwner) {
      var sectionSettings = SectionSettings.of(sectionPreviewOwner);
      return new ModuleHeaderSetting(key, sectionPreviewOwner, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(sectionPreviewOwner.getName(), sectionSettings, null, sectionPreviewOwner));
   }
}

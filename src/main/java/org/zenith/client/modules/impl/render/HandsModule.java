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
   name = "Hands",
   category = ModuleCategory.RENDER,
   description = "module.hands.description"
)
public final class HandsModule extends Module {
   public static final HandsModule INSTANCE = new HandsModule();
   private final ButtonSetting model = method2256("module.hands.model", ViewModelModule.INSTANCE);
   private final ButtonSetting swing = method2256("module.hands.swing", SwingAnimationModule.INSTANCE);
   private final ButtonSetting shader = method2256("module.hands.shader", ShaderHandModule.INSTANCE);
   private final List<Module> items = new ArrayList<>();

   private HandsModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.model, this.swing, this.shader);
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

      for (Module moduleValue : method2255()) {
         if (moduleValue.isEnabled()) {
            this.items.add(moduleValue);
            moduleValue.toggle();
         }
      }
   }

   private static List<Module> method2255() {
      return List.of(ViewModelModule.INSTANCE, SwingAnimationModule.INSTANCE, ShaderHandModule.INSTANCE);
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method2255();
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      json.addProperty("enabled", true);
      return json;
   }

   private static ButtonSetting method2256(String key, Module sectionPreviewOwner) {
      var sectionSettings = SectionSettings.of(sectionPreviewOwner);
      return new ModuleHeaderSetting(key, sectionPreviewOwner, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(sectionPreviewOwner.getName(), sectionSettings, null, sectionPreviewOwner));
   }
}

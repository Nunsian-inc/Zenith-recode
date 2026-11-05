package org.zenith.client.modules.impl.render;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.esp.SectionSettings;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ModuleHeaderSetting;

@ModuleInfo(
   name = "Particles",
   category = ModuleCategory.RENDER,
   description = "module.particles.description"
)
public final class ParticlesModule extends Module {
   public static final ParticlesModule INSTANCE = new ParticlesModule();
   private final ButtonSetting ambient = method7401("module.particles.ambient", AmbientParticlesModule.INSTANCE, ModulePreviewStage.Kind.PARTICLES_AMBIENT);
   private final ButtonSetting totem = method7401("module.particles.totem", TotemParticlesModule.INSTANCE, ModulePreviewStage.Kind.PARTICLES_TOTEM);
   private final ButtonSetting hit = method7401("module.particles.hit", HitParticlesModule.INSTANCE, ModulePreviewStage.Kind.PARTICLES_HIT);
   private final List<Module> items = new ArrayList<>();

   private ParticlesModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.ambient, this.totem, this.hit);
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

      for (Module moduleValue : method7400()) {
         if (moduleValue.isEnabled()) {
            this.items.add(moduleValue);
            moduleValue.toggle();
         }
      }
   }

   private static List<Module> method7400() {
      return List.of(AmbientParticlesModule.INSTANCE, TotemParticlesModule.INSTANCE, HitParticlesModule.INSTANCE);
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method7400();
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      json.addProperty("enabled", true);
      return json;
   }

   private static ButtonSetting method7401(String key, Module sectionPreviewOwner, ModulePreviewStage.Kind sectionPreview) {
      var sectionSettings = SectionSettings.of(sectionPreviewOwner);
      return new ModuleHeaderSetting(key, sectionPreviewOwner, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(sectionPreviewOwner.getName(), sectionSettings, sectionPreview, sectionPreviewOwner));
   }
}

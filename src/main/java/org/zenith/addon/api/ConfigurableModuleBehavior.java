package org.zenith.addon.api;

import org.zenith.addon.api.setting.AddonSetting;
import org.zenith.addon.api.setting.ModuleSettings;

public abstract class ConfigurableModuleBehavior implements ModuleBehavior, ModuleSettingsProvider {
   private final ModuleSettings settings = new ModuleSettings();

   protected final <S extends AddonSetting<?>> S setting(S setting) {
      return this.settings.add(setting);
   }

   @Override
   public final ModuleSettings getSettings() {
      return this.settings;
   }

   @Override
   public void save(ModuleConfig config) {
      this.getSettings().safe(config);
   }

   @Override
   public void load(ModuleConfig config) {
      this.getSettings().load(config);
   }
}

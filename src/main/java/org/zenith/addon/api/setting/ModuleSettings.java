package org.zenith.addon.api.setting;

import java.util.ArrayList;
import java.util.List;
import org.zenith.addon.api.ModuleConfig;
import org.zenith.addon.api.SettingDefinition;

public final class ModuleSettings {
   private final List<AddonSetting<?>> settings = new ArrayList<>();

   public <S extends AddonSetting<?>> S add(S setting) {
      if (this.settings.stream().anyMatch(existing -> existing.id().equals(setting.id()))) {
         throw new IllegalArgumentException("Duplicate setting id: " + setting.id());
      }

      this.settings.add(setting);
      return setting;
   }

   public List<AddonSetting<?>> values() {
      return List.copyOf(this.settings);
   }

   public List<SettingDefinition> definitions() {
      return List.copyOf(this.settings);
   }

   public void safe(ModuleConfig config) {
      this.settings.forEach(setting -> setting.safe(config));
   }

   public void save(ModuleConfig config) {
      this.safe(config);
   }

   public void load(ModuleConfig config) {
      this.settings.forEach(setting -> setting.load(config));
   }
}

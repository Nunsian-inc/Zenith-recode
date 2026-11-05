package org.zenith.client.screens.esp;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.Setting;

public final class SectionSettings {
   private SectionSettings() {
   }

   public static List<Setting> of(Module sectionPreviewOwner) {
      ArrayList<Setting> settings = new ArrayList<>();
      settings.add(new SectionSettings.ModuleToggle("module.esp.section.enabled", sectionPreviewOwner));
      settings.addAll(sectionPreviewOwner.getSettings());
      return settings;
   }

   private static final class ModuleToggle extends BooleanSetting {
      private final Module module;

      private ModuleToggle(String key, Module sectionPreviewOwner) {
         super(key, sectionPreviewOwner.isEnabled());
         this.module = sectionPreviewOwner;
      }

      @Override
      public boolean isEnabled() {
         return this.module.isEnabled();
      }

      @Override
      public void toggle() {
         this.module.toggle();
      }

      @Override
      public void safe(JsonObject json) {
      }

      @Override
      public void load(JsonObject json) {
      }
   }
}

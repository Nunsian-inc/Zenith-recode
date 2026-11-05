package org.zenith.addon.api.setting;

import org.zenith.addon.api.ModuleConfig;

public final class BooleanSetting extends AddonSetting<Boolean> {
   public BooleanSetting(String id, String name, boolean defaultValue) {
      this(id, name, "", defaultValue);
   }

   public BooleanSetting(String id, String name, String description, boolean defaultValue) {
      super(id, name, description, defaultValue);
   }

   public boolean enabled() {
      return this.get();
   }

   public void toggle() {
      this.set(!this.get());
   }

   @Override
   public void safe(ModuleConfig config) {
      config.setBoolean(this.id(), this.get());
   }

   @Override
   public void load(ModuleConfig config) {
      this.set(config.getBoolean(this.id(), this.defaultValue()));
   }

   @Override
   public void setObject(Object value) {
      if (value instanceof Boolean bool) {
         this.set(bool);
      }
   }

   protected Boolean validate(Boolean value) {
      if (value == null) {
         throw new IllegalArgumentException("Boolean value cannot be null");
      } else {
         return value;
      }
   }
}

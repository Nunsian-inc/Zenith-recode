package org.zenith.addon.api.setting;

import org.zenith.addon.api.ModuleConfig;

public final class ColorSetting extends AddonSetting<Integer> {
   public ColorSetting(String id, String name, int argb) {
      this(id, name, "", argb);
   }

   public ColorSetting(String id, String name, String description, int argb) {
      super(id, name, description, argb);
   }

   public int argb() {
      return this.get();
   }

   @Override
   public void safe(ModuleConfig config) {
      config.setInt(this.id(), this.get());
   }

   @Override
   public void load(ModuleConfig config) {
      this.set(config.getInt(this.id(), this.defaultValue()));
   }

   @Override
   public void setObject(Object value) {
      if (value instanceof Number number) {
         this.set(number.intValue());
      }
   }

   protected Integer validate(Integer value) {
      if (value == null) {
         throw new IllegalArgumentException("Color cannot be null");
      } else {
         return value;
      }
   }
}

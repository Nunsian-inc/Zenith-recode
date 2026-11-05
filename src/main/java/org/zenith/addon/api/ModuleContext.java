package org.zenith.addon.api;

public interface ModuleContext {
   String addonId();

   String moduleId();

   boolean development();

   Object setting(String settingId);

   default boolean booleanSetting(String settingId) {
      return Boolean.TRUE.equals(this.setting(settingId));
   }

   default double numberSetting(String settingId) {
      return this.setting(settingId) instanceof Number number ? number.doubleValue() : 0.0;
   }

   default String stringSetting(String settingId) {
      Object value = this.setting(settingId);
      return value == null ? "" : value.toString();
   }
}

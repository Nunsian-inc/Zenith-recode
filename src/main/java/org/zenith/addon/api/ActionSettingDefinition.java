package org.zenith.addon.api;

public record ActionSettingDefinition(String id, String name, String description, String icon, Runnable action) implements SettingDefinition {
   @Override
   public Object defaultValue() {
      return null;
   }
}

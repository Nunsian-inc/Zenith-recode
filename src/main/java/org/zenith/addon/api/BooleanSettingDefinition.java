package org.zenith.addon.api;

public record BooleanSettingDefinition(String id, String name, String description, boolean defaultEnabled) implements SettingDefinition {
   public Boolean defaultValue() {
      return this.defaultEnabled;
   }
}

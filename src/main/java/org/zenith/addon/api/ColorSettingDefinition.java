package org.zenith.addon.api;

public record ColorSettingDefinition(String id, String name, String description, int defaultArgb) implements SettingDefinition {
   public Integer defaultValue() {
      return this.defaultArgb;
   }
}

package org.zenith.addon.api;

public record NumberSettingDefinition(String id, String name, String description, double defaultNumber, double min, double max, double step, String suffix)
   implements SettingDefinition {
   public Double defaultValue() {
      return this.defaultNumber;
   }
}

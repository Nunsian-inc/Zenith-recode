package org.zenith.addon.api;

public record StringSettingDefinition(String id, String name, String description, String defaultText, String placeholder, int maxLength, boolean secret)
   implements SettingDefinition {
   public String defaultValue() {
      return this.defaultText;
   }
}

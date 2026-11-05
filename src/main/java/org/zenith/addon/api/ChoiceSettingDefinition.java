package org.zenith.addon.api;

import java.util.List;

public record ChoiceSettingDefinition(String id, String name, String description, String defaultChoice, List<String> choices) implements SettingDefinition {
   public ChoiceSettingDefinition {
      choices = List.copyOf(choices);
   }

   public String defaultValue() {
      return this.defaultChoice;
   }
}

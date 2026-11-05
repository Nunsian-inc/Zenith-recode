package org.zenith.addon.api.setting;

import java.util.List;
import org.zenith.addon.api.ModuleConfig;

public final class ChoiceSetting extends AddonSetting<String> {
   private final List<String> choices;

   public ChoiceSetting(String id, String name, String defaultValue, String... choices) {
      this(id, name, "", defaultValue, List.of(choices));
   }

   public ChoiceSetting(String id, String name, String description, String defaultValue, List<String> choices) {
      super(id, name, description, checkedDefault(defaultValue, choices));
      this.choices = List.copyOf(choices);
   }

   public List<String> choices() {
      return this.choices;
   }

   public boolean is(String value) {
      return this.get().equals(value);
   }

   @Override
   public void safe(ModuleConfig config) {
      config.setString(this.id(), this.get());
   }

   @Override
   public void load(ModuleConfig config) {
      this.set(config.getString(this.id(), this.defaultValue()));
   }

   @Override
   public void setObject(Object value) {
      if (value instanceof String text) {
         this.set(text);
      }
   }

   protected String validate(String value) {
      if (this.choices == null) {
         return value;
      } else if (!this.choices.contains(value)) {
         throw new IllegalArgumentException("Unknown choice: " + value);
      } else {
         return value;
      }
   }

   private static String checkedDefault(String value, List<String> choices) {
      if (choices != null && !choices.isEmpty() && choices.contains(value)) {
         return value;
      } else {
         throw new IllegalArgumentException("Invalid choices");
      }
   }
}

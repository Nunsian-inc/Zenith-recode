package org.zenith.addon.api;

import java.util.Arrays;

public final class Settings {
   private Settings() {
   }

   public static BooleanSettingDefinition bool(String id, String name, boolean value) {
      return new BooleanSettingDefinition(id, name, "", value);
   }

   public static NumberSettingDefinition number(String id, String name, double value, double min, double max, double step) {
      return new NumberSettingDefinition(id, name, "", value, min, max, step, "");
   }

   public static ChoiceSettingDefinition choice(String id, String name, String value, String... choices) {
      return new ChoiceSettingDefinition(id, name, "", value, Arrays.asList(choices));
   }

   public static StringSettingDefinition text(String id, String name, String value, String placeholder) {
      return new StringSettingDefinition(id, name, "", value, placeholder, 128, false);
   }

   public static ColorSettingDefinition color(String id, String name, int argb) {
      return new ColorSettingDefinition(id, name, "", argb);
   }

   public static ActionSettingDefinition action(String id, String name, Runnable action) {
      return new ActionSettingDefinition(id, name, "", "t", action);
   }
}

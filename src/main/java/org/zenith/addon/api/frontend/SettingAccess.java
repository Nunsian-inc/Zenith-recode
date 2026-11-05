package org.zenith.addon.api.frontend;

import java.util.List;
import org.zenith.addon.api.i18n.AddonTranslations;

public interface SettingAccess {
   String id();

   String name();

   String description();

   default String translatedDescription() {
      return AddonTranslations.resolve(this.description());
   }

   SettingType type();

   boolean visible();

   Object value();

   boolean value(Object var1);

   default double min() {
      return 0.0;
   }

   default double max() {
      return 0.0;
   }

   default double step() {
      return 0.0;
   }

   default String suffix() {
      return "";
   }

   default List<String> choices() {
      return List.of();
   }

   default String placeholder() {
      return "";
   }

   default int maxLength() {
      return 0;
   }

   default boolean secret() {
      return false;
   }

   default List<? extends SettingAccess> children() {
      return List.of();
   }

   default boolean activate() {
      return this.value(Boolean.TRUE);
   }
}

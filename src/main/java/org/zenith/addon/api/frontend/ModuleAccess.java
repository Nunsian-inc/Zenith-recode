package org.zenith.addon.api.frontend;

import java.util.List;
import org.zenith.addon.api.i18n.AddonTranslations;

public interface ModuleAccess {
   String id();

   String addonId();

   String name();

   String description();

   default String translatedDescription() {
      return AddonTranslations.resolve(this.description());
   }

   String category();

   boolean enabled();

   void enabled(boolean var1);

   default void toggle() {
      this.enabled(!this.enabled());
   }

   int keyCode();

   void keyCode(int var1);

   List<? extends SettingAccess> settings();
}

package org.zenith.addon.api.frontend;

public record ConfigSaveOptions(boolean includeVisuals, boolean includeOther, boolean includeBinds) {
   public static ConfigSaveOptions all() {
      return new ConfigSaveOptions(true, true, true);
   }
}

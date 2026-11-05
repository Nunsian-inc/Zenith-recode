package org.zenith.utility.misc;

import org.zenith.base.Module;
import org.zenith.client.settings.ButtonSetting;

public final class ModuleHeaderSetting extends ButtonSetting {
   private final Module module;
   private final HeaderBindSetting headerBindSetting;

   public ModuleHeaderSetting(String key, Module sectionPreviewOwner, Runnable runnable) {
      super(key, "w", runnable);
      this.module = sectionPreviewOwner;
      this.headerBindSetting = new HeaderBindSetting(sectionPreviewOwner);
   }

   @Override
   public String getName() {
      return this.module.getName();
   }

   public HeaderBindSetting getHeaderBindSetting() {
      return this.headerBindSetting;
   }
}

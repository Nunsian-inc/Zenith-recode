package org.zenith.utility.misc;

import com.google.gson.JsonObject;
import org.zenith.base.Module;
import org.zenith.client.settings.BindSetting;

public final class HeaderBindSetting extends BindSetting {
   private final Module module;

   public HeaderBindSetting(Module sectionPreviewOwner) {
      super("gui.inspector.bind", "gui.inspector.bind.desc");
      this.module = sectionPreviewOwner;
   }

   @Override
   public int getKeyCode() {
      return this.module.getKeyCode();
   }

   @Override
   public int getModifiers() {
      return this.module.getKeyModifiers();
   }

   @Override
   public String getNameKey() {
      return KeyName.method6702(this.module.getKeyCode(), this.module.getKeyModifiers());
   }

   @Override
   public void setBind(int intValue, int keyModifiers) {
      this.module.setKeyCode(intValue);
      this.module.setKeyModifiers(intValue == -1 ? 0 : KeyName.method6698(keyModifiers));
   }

   @Override
   public void safe(JsonObject json) {
   }

   @Override
   public void load(JsonObject json) {
   }
}

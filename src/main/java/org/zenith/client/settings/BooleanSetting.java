package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;

public class BooleanSetting extends Setting {
   private boolean enabled;
   private final String description;

   @Override
   public String getDescription() {
      return Zenith.getInstance().getLocalization().translate(this.description);
   }

   public BooleanSetting(String key, boolean enabled) {
      super(key, "");
      this.enabled = enabled;
      this.description = "";
   }

   public BooleanSetting(String key, String description, boolean enabled) {
      super(key, description);
      this.enabled = enabled;
      this.description = description;
   }

   public BooleanSetting(String key, String description, boolean enabled, Supplier<Boolean> visible) {
      super(key, description);
      this.enabled = enabled;
      this.setVisible(visible);
      this.description = description;
   }

   public BooleanSetting(String key, boolean enabled, Supplier<Boolean> visible) {
      super(key, "");
      this.enabled = enabled;
      this.setVisible(visible);
      this.description = "";
   }

   public static BooleanSetting method477(String key, boolean enabled) {
      return new BooleanSetting(key, enabled);
   }

   public static BooleanSetting method478(String key) {
      return new BooleanSetting(key, true);
   }

   public void toggle() {
      this.enabled = !this.enabled;
   }

   @Override
   public void safe(JsonObject json) {
      json.addProperty(String.valueOf(this.key), this.isEnabled());
   }

   @Override
   public void load(JsonObject jsonObject) {
      this.setEnabled(jsonObject.get(String.valueOf(this.key)).getAsBoolean());
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }

   @Generated
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}

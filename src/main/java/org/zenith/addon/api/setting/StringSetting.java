package org.zenith.addon.api.setting;

import org.zenith.addon.api.ModuleConfig;

public final class StringSetting extends AddonSetting<String> {
   private final String placeholder;
   private final int maxLength;
   private final boolean secret;

   public StringSetting(String id, String name, String value, String placeholder) {
      this(id, name, "", value, placeholder, 128, false);
   }

   public StringSetting(String id, String name, String description, String value, String placeholder, int maxLength, boolean secret) {
      super(id, name, description, checkedDefault(value, maxLength));
      this.placeholder = placeholder == null ? "" : placeholder;
      this.maxLength = maxLength;
      this.secret = secret;
   }

   public String placeholder() {
      return this.placeholder;
   }

   public int maxLength() {
      return this.maxLength;
   }

   public boolean secret() {
      return this.secret;
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
      if (value != null && (this.maxLength <= 0 || value.length() <= this.maxLength)) {
         return value;
      } else {
         throw new IllegalArgumentException("Invalid string value");
      }
   }

   private static String checkedDefault(String value, int maxLength) {
      if (maxLength > 0 && value != null && value.length() <= maxLength) {
         return value;
      } else {
         throw new IllegalArgumentException("Invalid string setting");
      }
   }
}

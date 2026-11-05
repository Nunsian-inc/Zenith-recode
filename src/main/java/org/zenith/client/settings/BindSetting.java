package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.utility.misc.KeyName;

public class BindSetting extends Setting {
   private String nameKey;
   private int keyCode;
   private int modifiers;

   public void setKeyCode(int keyCode) {
      this.setBind(keyCode, 0);
   }

   public void setBind(int keyCode, int keyModifiers) {
      this.keyCode = keyCode;
      this.modifiers = KeyName.method6698(keyModifiers);
      this.nameKey = KeyName.method6702(this.keyCode, this.modifiers);
   }

   public BindSetting(String key, Supplier<Boolean> visible) {
      this(key, "", visible);
   }

   public BindSetting(String key, String description, Supplier<Boolean> visible) {
      super(key, description);
      this.setVisible(visible);
      this.keyCode = -1;
      this.nameKey = KeyName.method6701(this.keyCode);
   }

   public BindSetting(String text, int keyCode, Supplier<Boolean> visible) {
      this(text, "", keyCode, visible);
   }

   public BindSetting(String text, String description, int keyCode, Supplier<Boolean> visible) {
      super(text, description);
      this.setVisible(visible);
      this.keyCode = keyCode;
      this.nameKey = KeyName.method6701(keyCode);
   }

   public BindSetting(String key, int keyCode) {
      this(key, "", keyCode);
   }

   public BindSetting(String key, String description, int keyCode) {
      super(key, description);
      this.keyCode = keyCode;
      this.nameKey = KeyName.method6701(keyCode);
   }

   public BindSetting(String key) {
      this(key, "");
   }

   public BindSetting(String key, String description) {
      super(key, description);
      this.keyCode = -1;
      this.nameKey = "";
   }

   @Override
   public void safe(JsonObject json) {
      json.addProperty(String.valueOf(this.key), this.getKeyCode());
      json.addProperty(this.key + "_mods", this.modifiers);
   }

   @Override
   public void load(JsonObject json) {
      int keyModifiers = json.has(this.key + "_mods") ? json.get(this.key + "_mods").getAsInt() : 0;
      this.setBind(json.get(String.valueOf(this.key)).getAsInt(), keyModifiers);
   }

   @Generated
   public String getNameKey() {
      return this.nameKey;
   }

   @Generated
   public int getKeyCode() {
      return this.keyCode;
   }

   @Generated
   public int getModifiers() {
      return this.modifiers;
   }
}

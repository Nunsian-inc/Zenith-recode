package org.zenith.client.settings;

import com.google.gson.JsonObject;
import lombok.Generated;

public class ButtonSetting extends Setting {
   private String text;
   private Runnable runnable;

   public ButtonSetting(String key, Runnable runnable) {
      this(key, "t", runnable);
   }

   public ButtonSetting(String key, String text, Runnable runnable) {
      this(key, text, "", runnable);
   }

   public ButtonSetting(String key, String text, String description, Runnable runnable) {
      super(key, description);
      this.runnable = runnable;
      this.text = text;
   }

   public void toggle() {
      this.runnable.run();
   }

   @Override
   public void safe(JsonObject json) {
   }

   @Override
   public void load(JsonObject json) {
   }

   @Generated
   public String getIcon() {
      return this.text;
   }

   @Generated
   public Runnable getRunnable() {
      return this.runnable;
   }

   @Generated
   public void setText(String text) {
      this.text = text;
   }

   @Generated
   public void setRunnable(Runnable runnable) {
      this.runnable = runnable;
   }
}

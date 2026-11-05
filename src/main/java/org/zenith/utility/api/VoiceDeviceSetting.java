package org.zenith.utility.api;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Function;
import org.zenith.client.settings.ModeSetting;

public class VoiceDeviceSetting extends ModeSetting {
   private final String text;
   private final Function<String, String> function;
   private String text2 = "";
   private String shown = "";

   public VoiceDeviceSetting(String key, String description, String text, Function<String, String> function) {
      super(key, description);
      this.text = text;
      this.function = function;
      new ModeSetting.Entry(this, text);
   }

   public void method9(List<String> strings) {
      this.method12();
      this.getValues().clear();
      new ModeSetting.Entry(this, this.text);

      for (String text : strings) {
         if (text != null && !text.isBlank() && !text.equals(this.text)) {
            new VoiceDeviceSetting.Entry(this, text);
         }
      }

      this.set(this.text2.isEmpty() ? this.text : this.text2);
      if (this.getValue2() == null || !this.getValues().contains(this.getValue2())) {
         this.getValues().getFirst().method2103();
      }

      this.shown = this.method11();
   }

   public void method10(String key) {
      this.set(key != null && !key.isEmpty() ? key : this.text);
      this.method12();
   }

   public String method11() {
      String str = this.get();
      return str.equals(this.text) ? "" : str;
   }

   public boolean method12() {
      String shown = this.method11();
      if (shown.equals(this.shown)) {
         return false;
      } else {
         this.text2 = shown;
         this.shown = shown;
         return true;
      }
   }

   @Override
   public void safe(JsonObject json) {
      this.method12();
      json.addProperty(String.valueOf(this.key), this.text2);
   }

   @Override
   public void load(JsonObject json) {
      this.text2 = json.get(String.valueOf(this.key)).getAsString();
      this.set(this.text2.isEmpty() ? this.text : this.text2);
      this.shown = this.method11();
   }

   private final class Entry extends ModeSetting.Entry {
      private Entry(ModeSetting modeSetting, String text) {
         super(modeSetting, text);
      }

      @Override
      public String getName() {
         return VoiceDeviceSetting.this.function.apply(this.getKey());
      }
   }
}

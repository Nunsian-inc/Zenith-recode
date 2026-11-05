package org.zenith.utility.api;

import com.google.gson.JsonObject;
import lombok.Generated;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Canvas;

public class CanvasSetting extends Setting {
   private final Canvas canvas = new Canvas();

   public CanvasSetting(String key) {
      super(key, "");
   }

   @Override
   public void safe(JsonObject json) {
      json.addProperty(this.key, this.canvas.method145());
   }

   @Override
   public void load(JsonObject json) {
      if (json.has(this.key)) {
         this.canvas.method147(json.get(this.key).getAsString());
      }
   }

   @Generated
   public Canvas getCanvas() {
      return this.canvas;
   }
}

package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;

public class SliderSetting extends Setting {
   private final String text;
   private final String text2;
   private float floatValue;
   private final float min;
   private final float max;
   private final float floatValue2;
   private SliderSetting.Handler handler;
   private SliderSetting.Handler2 handler2;

   @Override
   public String getDescription() {
      return Zenith.getInstance().getLocalization().translate(this.text2);
   }

   public SliderSetting(
      String key, float floatValue, float min, float max, float f, String description, String str, Supplier<Boolean> visible, SliderSetting.Handler2 handler2
   ) {
      super(key, description);
      this.min = min;
      this.max = max;
      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.text2 = description != null ? description : "";
      this.text = str != null ? str : "";
      this.handler2 = handler2;
      if (visible != null) {
         this.setVisible(visible);
      }
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f) {
      this(key, floatValue, min, max, f, "", "%", null, null);
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f, String description) {
      this(key, floatValue, min, max, f, description, "%", null, null);
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f, String description, String str) {
      this(key, floatValue, min, max, f, description, str, null, null);
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f, Supplier<Boolean> visible) {
      this(key, floatValue, min, max, f, "", "%", visible, null);
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f, SliderSetting.Handler2 handler2) {
      this(key, floatValue, min, max, f, "", "%", null, handler2);
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f, Supplier<Boolean> visible, SliderSetting.Handler2 handler2) {
      this(key, floatValue, min, max, f, "", "%", visible, handler2);
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f, String description, String str, Supplier<Boolean> visible) {
      this(key, floatValue, min, max, f, description, str, visible, null);
   }

   public SliderSetting(String key, float floatValue, float min, float max, float f, String str, Supplier<Boolean> visible, SliderSetting.Handler2 handler2) {
      this(key, floatValue, min, max, f, "", str, visible, handler2);
   }

   public SliderSetting method2399(SliderSetting.Handler handler) {
      this.handler = handler;
      return this;
   }

   public void method2400(float floatValue) {
      float f = this.floatValue;
      this.floatValue = floatValue;
      if (this.handler2 != null) {
         this.handler2.apply(f, floatValue);
      }
   }

   @Override
   public void safe(JsonObject json) {
      json.addProperty(String.valueOf(this.key), this.getValue3());
   }

   @Override
   public void load(JsonObject jsonObject) {
      this.method2400(jsonObject.get(String.valueOf(this.key)).getAsFloat());
   }

   @Generated
   public String getSuffix() {
      return this.text;
   }

   @Generated
   public float getValue3() {
      return this.floatValue;
   }

   @Generated
   public float getMin() {
      return this.min;
   }

   @Generated
   public float getMax() {
      return this.max;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public SliderSetting.Handler getHandler() {
      return this.handler;
   }

   @Generated
   public SliderSetting.Handler2 getHandler2() {
      return this.handler2;
   }

   public interface Handler {
      String text(float numberTextValue);
   }

   public interface Handler2 {
      void apply(float f, float floatValue);
   }
}

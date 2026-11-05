package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.ZenithColor;

public class ColorSetting extends Setting {
   private ZenithColor zenithColor;
   private final ColorSetting.Handler handler;

   public ColorSetting(String key, ZenithColor zenithColor, Supplier<Boolean> visible, ColorSetting.Handler handler) {
      this(key, "", zenithColor, handler);
      this.setVisible(visible);
   }

   public ColorSetting(String key, ZenithColor zenithColor, ColorSetting.Handler handler) {
      this(key, "", zenithColor, handler);
   }

   public ColorSetting(String key, String description, ZenithColor zenithColor, ColorSetting.Handler handler) {
      super(key, description);
      if (zenithColor == null) {
         throw new RuntimeException(key + " color is null");
      } else {
         this.zenithColor = zenithColor;
         this.setZenithColor(zenithColor);
         this.handler = handler;
      }
   }

   public ColorSetting(String key, ZenithColor zenithColor) {
      this(key, "", zenithColor, () -> zenithColor);
   }

   public ColorSetting(String key, String description, ZenithColor zenithColor) {
      this(key, description, zenithColor, () -> zenithColor);
   }

   public ColorSetting(String key, ColorSetting.Handler handler) {
      this(key, "", handler.getDefaultColor(), handler);
   }

   public ColorSetting(String key, ZenithColor zenithColor, Supplier<Boolean> visible) {
      this(key, "", zenithColor, () -> zenithColor);
      this.setVisible(visible);
   }

   public ColorSetting(String key, String description, ZenithColor zenithColor, Supplier<Boolean> visible) {
      this(key, description, zenithColor, () -> zenithColor);
      this.setVisible(visible);
   }

   public int method1445() {
      return this.zenithColor.method7287();
   }

   public void method1446(int intValue) {
      this.zenithColor = new ZenithColor(intValue);
   }

   public void setZenithColor(ZenithColor zenithColor) {
      this.zenithColor = zenithColor;
   }

   public void update() {
   }

   public void reset() {
      this.zenithColor = this.handler.getDefaultColor();
   }

   public ZenithColor method1447(float previewAlpha) {
      return this.zenithColor.method7294(previewAlpha);
   }

   @Override
   public void safe(JsonObject json) {
      json.addProperty(String.valueOf(this.key), this.method1445());
   }

   @Override
   public void load(JsonObject json) {
      this.method1446(json.get(String.valueOf(this.key)).getAsInt());
   }

   @Generated
   public ZenithColor getZenithColor() {
      return this.zenithColor;
   }

   public interface Handler {
      ZenithColor getDefaultColor();
   }
}

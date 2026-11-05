package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;

public abstract class Setting {
   public final String key;
   public final String description;
   public Supplier<Boolean> visible;
   public Supplier<String> titleKey;
   public Supplier<String> tipKey;

   public Setting(String key) {
      this(key, "");
   }

   public Setting(String key, String description) {
      this.key = key;
      this.description = description != null ? description : "";
      this.setVisible(() -> true);
   }

   public String getName() {
      return Zenith.getInstance().getLocalization().translate(this.titleKey == null ? this.key : this.titleKey.get());
   }

   public String getDescription() {
      return Zenith.getInstance().getLocalization().translate(this.tipKey == null ? this.description : this.tipKey.get());
   }

   public abstract void safe(JsonObject jsonObject);

   public abstract void load(JsonObject jsonObject);

   public boolean isVisible() {
      return this.visible.get();
   }

   @Generated
   public String getKey() {
      return this.key;
   }

   @Generated
   public Supplier<Boolean> getVisible() {
      return this.visible;
   }

   @Generated
   public Supplier<String> getTitleKey() {
      return this.titleKey;
   }

   @Generated
   public Supplier<String> getTipKey() {
      return this.tipKey;
   }

   @Generated
   public void setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
   }

   @Generated
   public void setTitleKey(Supplier<String> titleKey) {
      this.titleKey = titleKey;
   }

   @Generated
   public void setTipKey(Supplier<String> tipKey) {
      this.tipKey = tipKey;
   }
}

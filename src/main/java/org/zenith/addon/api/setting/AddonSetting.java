package org.zenith.addon.api.setting;

import java.util.Objects;
import org.zenith.addon.api.ModuleConfig;
import org.zenith.addon.api.SettingDefinition;

public abstract class AddonSetting<T> implements SettingDefinition {
   private final String id;
   private final String name;
   private final String description;
   private final T defaultValue;
   private T value;

   protected AddonSetting(String id, String name, String description, T defaultValue) {
      this.id = Objects.requireNonNull(id, "id");
      this.name = Objects.requireNonNull(name, "name");
      this.description = Objects.requireNonNullElse(description, "");
      this.defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
      this.value = this.defaultValue;
   }

   @Override
   public final String id() {
      return this.id;
   }

   @Override
   public final String name() {
      return this.name;
   }

   @Override
   public final String description() {
      return this.description;
   }

   @Override
   public final T defaultValue() {
      return this.defaultValue;
   }

   public final T get() {
      return this.value;
   }

   public final void set(T value) {
      this.value = this.validate(value);
   }

   public final void reset() {
      this.value = this.defaultValue;
   }

   public abstract void safe(ModuleConfig var1);

   public abstract void load(ModuleConfig var1);

   public abstract void setObject(Object var1);

   protected abstract T validate(T var1);
}

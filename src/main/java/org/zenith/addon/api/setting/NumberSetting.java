package org.zenith.addon.api.setting;

import org.zenith.addon.api.ModuleConfig;

public final class NumberSetting extends AddonSetting<Double> {
   private final double min;
   private final double max;
   private final double step;
   private final String suffix;

   public NumberSetting(String id, String name, double value, double min, double max, double step) {
      this(id, name, "", value, min, max, step, "");
   }

   public NumberSetting(String id, String name, String description, double value, double min, double max, double step, String suffix) {
      super(id, name, description, checkedDefault(value, min, max, step));
      this.min = min;
      this.max = max;
      this.step = step;
      this.suffix = suffix == null ? "" : suffix;
   }

   public double min() {
      return this.min;
   }

   public double max() {
      return this.max;
   }

   public double step() {
      return this.step;
   }

   public String suffix() {
      return this.suffix;
   }

   public float floatValue() {
      return this.get().floatValue();
   }

   @Override
   public void safe(ModuleConfig config) {
      config.setDouble(this.id(), this.get());
   }

   @Override
   public void load(ModuleConfig config) {
      this.set(config.getDouble(this.id(), this.defaultValue()));
   }

   @Override
   public void setObject(Object value) {
      if (value instanceof Number number) {
         this.set(number.doubleValue());
      }
   }

   protected Double validate(Double value) {
      if (value == null) {
         throw new IllegalArgumentException("Number value cannot be null");
      } else if (!Double.isFinite(value)) {
         throw new IllegalArgumentException("Number value must be finite");
      } else {
         return Math.max(this.min, Math.min(this.max, value));
      }
   }

   private static double checkedDefault(double value, double min, double max, double step) {
      if (!(min > max) && !(step <= 0.0) && !(value < min) && !(value > max)) {
         return value;
      } else {
         throw new IllegalArgumentException("Invalid number range");
      }
   }
}

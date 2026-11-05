package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;

public class ModeSetting extends Setting {
   private final List<ModeSetting.Entry> items = new ArrayList<>();
   private ModeSetting.Entry value;

   public ModeSetting(String key, String description) {
      super(key, description);
   }

   public ModeSetting(String key, String description, String... strings) {
      super(key, description);

      for (String text : strings) {
         if (!text.isEmpty()) {
            new ModeSetting.Entry(this, text);
         }
      }

      if (!this.items.isEmpty()) {
         this.value = this.items.getFirst();
      }
   }

   public ModeSetting(String key, Supplier<Boolean> visible, String... strings) {
      this(key, "", visible, strings);
   }

   public ModeSetting(String key, String description, Supplier<Boolean> visible, String... strings) {
      super(key, description);

      for (String text : strings) {
         if (!text.isEmpty()) {
            new ModeSetting.Entry(this, text);
         }
      }

      if (!this.items.isEmpty()) {
         this.value = this.items.getFirst();
      }

      this.setVisible(visible);
   }

   public void set(String text) {
      this.items.stream().filter(var1x -> var1x.getKey().equals(text)).findFirst().ifPresent(var1x -> this.value = var1x);
   }

   public void method2100(ModeSetting.Entry entry) {
      this.items.remove(entry);
      if (this.value == entry && !this.items.isEmpty()) {
         this.value = this.items.getFirst();
      }
   }

   public String get() {
      return this.value != null ? this.value.getKey() : "";
   }

   public boolean isSelected(int n) {
      return this.items.get(n).isSelected();
   }

   public boolean method2101(String anObject) {
      return this.value.getKey().equals(anObject);
   }

   public ModeSetting.Entry method2102() {
      var entries = this.items.stream().filter(ModeSetting.Entry::isSelected).toList();
      return !entries.isEmpty() ? (ModeSetting.Entry)entries.get(new Random().nextInt(entries.size())) : null;
   }

   @Override
   public void safe(JsonObject json) {
      json.addProperty(String.valueOf(this.key), this.get());
   }

   @Override
   public void load(JsonObject json) {
      this.set(json.get(String.valueOf(this.key)).getAsString());
   }

   public int getIndex() {
      return this.items.indexOf(this.value);
   }

   @Generated
   public List<ModeSetting.Entry> getValues() {
      return this.items;
   }

   @Generated
   public ModeSetting.Entry getValue2() {
      return this.value;
   }

   @Generated
   public void setValue(ModeSetting.Entry entry) {
      this.value = entry;
   }

   public static class Entry {
      private final ModeSetting modeSetting;
      private final String text;
      private final String text2;

      public Entry(ModeSetting modeSetting, String text) {
         this.modeSetting = modeSetting;
         this.text = text;
         this.text2 = "";
         if (modeSetting.items.isEmpty()) {
            this.method2103();
         }

         modeSetting.items.add(this);
      }

      public Entry(ModeSetting modeSetting, String text, String str) {
         this.modeSetting = modeSetting;
         this.text = text;
         this.text2 = str;
         if (modeSetting.items.isEmpty()) {
            this.method2103();
         }

         modeSetting.items.add(this);
      }

      public String getName() {
         return Zenith.getInstance().getLocalization().translate(this.text);
      }

      public ModeSetting.Entry method2103() {
         this.modeSetting.setValue(this);
         return this;
      }

      public boolean isSelected() {
         return this.modeSetting.getValue2() == this;
      }

      @Override
      public String toString() {
         return this.text;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            ModeSetting.Entry entry = (ModeSetting.Entry)obj;
            return Objects.equals(this.modeSetting, entry.modeSetting) && Objects.equals(this.text, entry.text) && Objects.equals(this.text2, entry.text2);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.modeSetting, this.text, this.text2);
      }

      @Generated
      public ModeSetting getModeSetting() {
         return this.modeSetting;
      }

      @Generated
      public String getKey() {
         return this.text;
      }

      @Generated
      public String getDescription() {
         return this.text2;
      }
   }
}

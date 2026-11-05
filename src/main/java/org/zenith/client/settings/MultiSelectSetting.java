package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import lombok.Generated;
import org.zenith.Zenith;

public class MultiSelectSetting extends Setting {
   private final List<MultiSelectSetting.Entry> items;
   private final List<List<String>> items2 = new ArrayList<>();
   private static final String text = ".known";

   public MultiSelectSetting(String key) {
      this(key, "");
   }

   public MultiSelectSetting(String key, String description) {
      super(key, description);
      this.items = new ArrayList<>();
   }

   public MultiSelectSetting(String key, MultiSelectSetting.Entry... entries) {
      this(key, "", entries);
   }

   public MultiSelectSetting(String key, String description, MultiSelectSetting.Entry... entries) {
      super(key, description);
      this.items = new ArrayList<>(Arrays.asList(entries));
      this.items.forEach(var1x -> var1x.multiSelectSetting = this);
   }

   public MultiSelectSetting method5220(String... elements) {
      this.items2.add(List.of(elements));
      MultiSelectSetting.Entry entry = null;

      for (String key : elements) {
         MultiSelectSetting.Entry entry2 = this.method5222(key);
         if (entry2 != null && entry2.isEnabled()) {
            if (entry == null) {
               entry = entry2;
            } else {
               entry2.enabled = false;
            }
         }
      }

      return this;
   }

   private void method5221(MultiSelectSetting.Entry entry) {
      for (List list : this.items2) {
         if (list.contains(entry.getKey())) {
            for (Object obj : (Iterable<?>)(Object)(list)) {
               String key = (String)obj;
               if (!key.equals(entry.getKey())) {
                  MultiSelectSetting.Entry entry2 = this.method5222(key);
                  if (entry2 != null) {
                     entry2.enabled = false;
                  }
               }
            }
         }
      }
   }

   public MultiSelectSetting.Entry method5222(String key) {
      int n = 0;

      for (int i = this.items.size(); n < i; n++) {
         MultiSelectSetting.Entry entry = this.items.get(n);
         if (entry.getKey().equalsIgnoreCase(key)) {
            return entry;
         }
      }

      return null;
   }

   public static MultiSelectSetting method5223(String key, List<String> list) {
      return method5224(key, "", list);
   }

   public static MultiSelectSetting method5224(String key, String description, List<String> list) {
      MultiSelectSetting.Entry[] entries = list.stream().map(var0x -> new MultiSelectSetting.Entry(var0x, true)).toArray(MultiSelectSetting.Entry[]::new);
      return new MultiSelectSetting(key, description, entries);
   }

   public MultiSelectSetting.Entry method5225(int n) {
      return this.items.get(n);
   }

   public boolean method5226(String key) {
      MultiSelectSetting.Entry entry = this.method5222(key);
      return entry != null && entry.isEnabled();
   }

   public boolean method5227(int n) {
      if (n >= this.getItems2().size()) {
         return false;
      } else {
         MultiSelectSetting.Entry entry = this.method5225(n);
         return entry != null && entry.isEnabled();
      }
   }

   public List<MultiSelectSetting.Entry> method5228() {
      ArrayList<MultiSelectSetting.Entry> entries = new ArrayList<>();
      int n = 0;

      for (int i = this.items.size(); n < i; n++) {
         MultiSelectSetting.Entry entry = this.items.get(n);
         if (entry.isEnabled()) {
            entries.add(entry);
         }
      }

      return entries;
   }

   @Override
   public void safe(JsonObject json) {
      StringBuilder builder = new StringBuilder();
      int n = 0;

      for (MultiSelectSetting.Entry entry : this.getItems2()) {
         if (this.method5222(entry.getKey()).isEnabled()) {
            builder.append(entry.getKey()).append("\n");
         }

         n++;
      }

      json.addProperty(this.key, builder.toString());
      StringBuilder builder2 = new StringBuilder();

      for (MultiSelectSetting.Entry entry : this.getItems2()) {
         builder2.append(entry.getKey()).append("\n");
      }

      json.addProperty(this.key + ".known", builder2.toString());
   }

   @Override
   public void load(JsonObject json) {
      HashSet hashSet = null;
      if (json.has(this.key + ".known")) {
         hashSet = new HashSet<>(List.of(json.get(this.key + ".known").getAsString().split("\n")));
      }

      for (MultiSelectSetting.Entry entry : this.getItems2()) {
         boolean flag = hashSet != null ? !hashSet.contains(entry.getKey()) : entry.flag2;
         entry.setEnabled(flag && entry.flag);
      }

      String[] strings = json.get(String.valueOf(this.key)).getAsString().split("\n");

      for (String key : strings) {
         MultiSelectSetting.Entry entry = this.method5222(key);
         if (entry != null) {
            this.method5222(key).setEnabled(true);
         }
      }
   }

   public List<String> method5229() {
      ArrayList list = new ArrayList();
      int n = 0;

      for (int i = this.items.size(); n < i; n++) {
         MultiSelectSetting.Entry entry = this.items.get(n);
         if (entry.isEnabled()) {
            list.add(entry.getKey());
         }
      }

      return Collections.unmodifiableList(list);
   }

   @Generated
   public List<MultiSelectSetting.Entry> getItems2() {
      return this.items;
   }

   @Generated
   public List<List<String>> getItems22() {
      return this.items2;
   }

   public static class Entry {
      private boolean enabled;
      private final boolean flag;
      private boolean flag2;
      private MultiSelectSetting multiSelectSetting;
      private final String text;

      public String getName() {
         return Zenith.getInstance().getLocalization().translate(this.text);
      }

      public String getKey() {
         return this.text;
      }

      private Entry(String text, boolean enabled) {
         this.enabled = enabled;
         this.flag = enabled;
         this.text = text;
      }

      public Entry(MultiSelectSetting multiSelectSetting, String text, boolean enabled) {
         this.enabled = enabled;
         this.flag = enabled;
         this.text = text;
         this.multiSelectSetting = multiSelectSetting;
         multiSelectSetting.items.add(this);
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
         if (enabled && this.multiSelectSetting != null) {
            this.multiSelectSetting.method5221(this);
         }
      }

      public MultiSelectSetting.Entry method5230() {
         this.flag2 = true;
         return this;
      }

      public static MultiSelectSetting.Entry method5231(String text, boolean enabled) {
         return new MultiSelectSetting.Entry(text, enabled);
      }

      public static MultiSelectSetting.Entry method5232(String text) {
         return new MultiSelectSetting.Entry(text, true);
      }

      public void toggle() {
         this.setEnabled(!this.enabled);
      }

      @Generated
      public boolean isEnabled() {
         return this.enabled;
      }

      @Generated
      public boolean isFlag() {
         return this.flag;
      }

      @Generated
      public boolean isFlag2() {
         return this.flag2;
      }

      @Generated
      public MultiSelectSetting getMultiSelectSetting() {
         return this.multiSelectSetting;
      }
   }
}

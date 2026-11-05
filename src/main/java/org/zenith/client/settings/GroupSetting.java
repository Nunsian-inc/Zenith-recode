package org.zenith.client.settings;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Generated;

public class GroupSetting extends Setting {
   private final List<Setting> items;
   private boolean active;

   public GroupSetting(String key, Setting... copyOfRange) {
      this(key, "", () -> true, copyOfRange);
   }

   public GroupSetting(String key, String description, Setting... copyOfRange) {
      this(key, description, () -> true, copyOfRange);
   }

   public GroupSetting(String key, Supplier<Boolean> visible, Setting... copyOfRange) {
      this(key, "", visible, copyOfRange);
   }

   public GroupSetting(String key, String description, Supplier<Boolean> visible, Setting... copyOfRange) {
      super(key, description);
      this.setVisible(visible);
      this.items = Lists.newArrayList(Arrays.asList(copyOfRange));
      this.active = false;
   }

   public <T extends Setting> T method2786(int n) {
      return (T)this.items.get(n);
   }

   public void toggle() {
      this.active = !this.active;
   }

   private void method2787(Consumer<Setting> action) {
      this.items.forEach(action);
   }

   @Override
   public void safe(JsonObject jsonObject) {
      this.method2787(var1x -> var1x.safe(jsonObject));
   }

   @Override
   public void load(JsonObject jsonObject) {
      this.method2787(var1x -> {
         if (jsonObject.has(var1x.getKey())) {
            var1x.load(jsonObject);
         }
      });
   }

   @Generated
   public List<Setting> getSettings() {
      return this.items;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }
}

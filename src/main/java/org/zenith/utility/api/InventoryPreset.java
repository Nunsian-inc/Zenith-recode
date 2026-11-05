package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.Zenith;
import org.zenith.utility.game.HolyWorldItems;
import org.zenith.utility.game.ItemMatcher;

public final class InventoryPreset {
   public static final int CONST_intValue = 36;
   public static final float CONST_floatValue = 0.8F;
   public static final String text = "Funtime 1.21";
   public static final String text2 = "HolyWorld";
   public static final String text3 = "SpookyTime";
   private final String text4;
   private final ItemMatcher[] itemMatchers = new ItemMatcher[36];
   private final int[] intValues = new int[36];
   private final long[] longValues = new long[36];
   private final float[] floatValues = new float[36];

   public InventoryPreset(String str) {
      this.text4 = str;
   }

   public String getText4() {
      return this.text4;
   }

   public ItemMatcher method870(int selectedSlot) {
      return method888(selectedSlot) ? this.itemMatchers[selectedSlot] : null;
   }

   public boolean method871(int selectedSlot) {
      ItemMatcher itemMatcher = this.method870(selectedSlot);
      return itemMatcher == null || itemMatcher.getItemStack().isEmpty();
   }

   public boolean isEmpty() {
      for (int i = 0; i < 36; i++) {
         if (!this.method871(i)) {
            return false;
         }
      }

      return true;
   }

   public int method872() {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         if (!this.method871(i)) {
            n++;
         }
      }

      return n;
   }

   public int method873() {
      for (int i = 0; i < 36; i++) {
         if (this.method871(i)) {
            return i;
         }
      }

      return -1;
   }

   public void method874(int selectedSlot, ItemMatcher key) {
      if (method888(selectedSlot)) {
         this.itemMatchers[selectedSlot] = key;
         this.intValues[selectedSlot] = method883(key);
         this.longValues[selectedSlot] = 0L;
         this.floatValues[selectedSlot] = method886(key) ? 0.8F : 0.0F;
      }
   }

   public void method875(int selectedSlot) {
      if (method888(selectedSlot)) {
         this.itemMatchers[selectedSlot] = null;
         this.intValues[selectedSlot] = 0;
         this.longValues[selectedSlot] = 0L;
         this.floatValues[selectedSlot] = 0.0F;
      }
   }

   public void method876() {
      for (int i = 0; i < 36; i++) {
         this.method875(i);
      }
   }

   public int method877(int selectedSlot) {
      ItemMatcher key = this.method870(selectedSlot);
      if (key == null) {
         return 0;
      } else if (!method885(key)) {
         return method883(key);
      } else {
         int intValue = method888(selectedSlot) ? this.intValues[selectedSlot] : 0;
         return intValue > 0 ? Math.min(method884(key), intValue) : method883(key);
      }
   }

   public void method878(int selectedSlot, int count) {
      if (method888(selectedSlot)) {
         ItemMatcher key = this.method870(selectedSlot);
         this.intValues[selectedSlot] = key == null ? 0 : Math.max(1, Math.min(method884(key), count));
      }
   }

   public long method879(int selectedSlot) {
      return method888(selectedSlot) ? Math.max(0L, this.longValues[selectedSlot]) : 0L;
   }

   public void method880(int selectedSlot, long longValue) {
      if (method888(selectedSlot)) {
         this.longValues[selectedSlot] = Math.max(0L, longValue);
      }
   }

   public float method881(int selectedSlot) {
      return method888(selectedSlot) && method886(this.method870(selectedSlot)) ? this.floatValues[selectedSlot] : 0.0F;
   }

   public void method882(int selectedSlot, float floatValue) {
      if (method888(selectedSlot)) {
         this.floatValues[selectedSlot] = Math.max(0.0F, Math.min(1.0F, floatValue));
      }
   }

   public static int method883(ItemMatcher key) {
      return key != null && !key.getItemStack().isEmpty() ? Math.max(1, key.getItemStack().getCount()) : 1;
   }

   public static int method884(ItemMatcher key) {
      if (key == null || key.getItemStack().isEmpty()) {
         return 64;
      } else {
         return method887(key) ? 64 : Math.min(64, Math.max(1, key.getItemStack().getMaxStackSize()));
      }
   }

   public static boolean method885(ItemMatcher itemMatcher) {
      return itemMatcher != null && !itemMatcher.getItemStack().isEmpty() ? itemMatcher.getItemStack().getMaxStackSize() > 1 || method887(itemMatcher) : false;
   }

   public static boolean method886(ItemMatcher itemMatcher) {
      if (itemMatcher == null || itemMatcher.getItemStack().isEmpty()) {
         return false;
      } else {
         return "Шлем Солнца".equals(itemMatcher.getText22()) ? false : itemMatcher.getItemStack().getMaxDamage() > 0;
      }
   }

   private static boolean method887(ItemMatcher key) {
      Item item = key.getItemStack().getItem();
      return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
   }

   private static boolean method888(int selectedSlot) {
      return selectedSlot >= 0 && selectedSlot < 36;
   }

   public JsonObject save() {
      JsonArray jsonArray = new JsonArray();

      for (int i = 0; i < 36; i++) {
         ItemMatcher itemMatcher = this.method870(i);
         if (itemMatcher != null && !itemMatcher.getItemStack().isEmpty()) {
            JsonObject json = new JsonObject();
            json.addProperty("slot", i);
            json.addProperty("search", itemMatcher.getText22());
            json.addProperty("item", BuiltInRegistries.ITEM.getKey(itemMatcher.getItemStack().getItem()).toString());
            json.addProperty("count", this.method877(i));
            json.addProperty("price", this.method879(i));
            json.addProperty("durability", this.method881(i));
            jsonArray.add(json);
         }
      }

      JsonObject json = new JsonObject();
      json.add("slots", jsonArray);
      return json;
   }

   public void load(JsonObject jsonObject) {
      this.method876();
      if (jsonObject != null && jsonObject.has("slots") && jsonObject.get("slots").isJsonArray()) {
         for (JsonElement json : jsonObject.getAsJsonArray("slots")) {
            if (json.isJsonObject()) {
               JsonObject jsonObject2 = json.getAsJsonObject();
               int selectedSlot = jsonObject2.has("slot") ? jsonObject2.get("slot").getAsInt() : -1;
               if (method888(selectedSlot)) {
                  ItemMatcher key = this.method889(
                     jsonObject2.has("search") ? jsonObject2.get("search").getAsString() : null, jsonObject2.has("item") ? jsonObject2.get("item").getAsString() : null
                  );
                  if (key != null) {
                     this.method874(selectedSlot, key);
                     if (jsonObject2.has("count")) {
                        this.method878(selectedSlot, jsonObject2.get("count").getAsInt());
                     }

                     if (jsonObject2.has("price")) {
                        this.method880(selectedSlot, jsonObject2.get("price").getAsLong());
                     }

                     if (jsonObject2.has("durability")) {
                        this.method882(selectedSlot, jsonObject2.get("durability").getAsFloat());
                     }
                  }
               }
            }
         }
      }
   }

   public ItemMatcher method889(String string, String anObject) {
      if (string != null && !string.isBlank()) {
         ItemMatcher itemMatcher = null;

         for (ItemMatcher itemMatcher2 : method891(this.text4)) {
            if (string.equals(itemMatcher2.getText22())) {
               if (anObject == null || anObject.isBlank()) {
                  return itemMatcher2;
               }

               if (BuiltInRegistries.ITEM.getKey(itemMatcher2.getItemStack().getItem()).toString().equals(anObject)) {
                  return itemMatcher2;
               }

               if (itemMatcher == null) {
                  itemMatcher = itemMatcher2;
               }
            }
         }

         return itemMatcher;
      } else {
         return null;
      }
   }

   public ItemMatcher method890(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         ItemMatcher itemMatcher = null;

         for (ItemMatcher itemMatcher2 : method891(this.text4)) {
            if (itemMatcher2.method2672(stack)) {
               if (itemMatcher2.getClass() != ItemMatcher.class) {
                  return itemMatcher2;
               }

               if (itemMatcher == null) {
                  itemMatcher = itemMatcher2;
               }
            }
         }

         return itemMatcher;
      } else {
         return null;
      }
   }

   public static List<ItemMatcher> method891(String anObject) {
      ArrayList<ItemMatcher> itemMatchers = new ArrayList<>();
      HolyWorldItems holyWorldItems = Zenith.getInstance().getHolyWorldItems();
      if (method892(anObject)) {
         itemMatchers.addAll(holyWorldItems.getArrayList2());
      } else {
         itemMatchers.addAll(holyWorldItems.getArrayList3());
      }

      itemMatchers.addAll(holyWorldItems.getArrayList());
      return itemMatchers;
   }

   public static boolean method892(String anObject) {
      return "Funtime 1.21".equals(anObject);
   }
}

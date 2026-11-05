package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SborItem {
   private String text;
   private String text2;
   private int count;
   private int intValue;
   private long longValue;
   private int intValue2;
   private String text3;
   private String text4;

   public SborItem(String string, String str, int count, int intValue, long longValue, int n, String str2) {
      this(string, str, count, intValue, longValue, n, str2, null);
   }

   public SborItem(String text, String str, int count, int intValue, long longValue, int n, String str2, String str3) {
      this.text = text;
      this.text2 = str;
      this.count = count;
      this.intValue = intValue;
      this.longValue = longValue;
      this.intValue2 = n;
      this.text3 = str2;
      this.text4 = str3;
   }

   public SborItem() {
   }

   public static SborItem method1747(ItemStack input, String str, int intValue, long longValue, int n) {
      String string = BuiltInRegistries.ITEM.getKey(input.getItem()).toString();
      String str2 = method1748(input);
      return new SborItem(string, str, input.getCount(), intValue, longValue, n, str2);
   }

   public static String method1748(ItemStack input) {
      try {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level == null) {
            return null;
         }

         RegistryOps serializationContext = level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
         DataResult encodeStart = ItemStack.CODEC.encodeStart(serializationContext, input);
         if (encodeStart.result().isPresent()) {
            return ((JsonElement)encodeStart.result().get()).toString();
         }
      } catch (Exception e) {
      }

      return null;
   }

   public ItemStack method1749() {
      if (this.text3 != null && !this.text3.isEmpty()) {
         try {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null) {
               throw new IllegalStateException("World is not loaded");
            }

            JsonElement string = JsonParser.parseString(this.text3);
            RegistryOps serializationContext = level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
            DataResult dataResult = ItemStack.CODEC.decode(serializationContext, string);
            if (dataResult.result().isPresent()) {
               return (ItemStack)((Pair)dataResult.result().get()).getFirst();
            }
         } catch (Exception e) {
         }
      }

      Item item = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(this.text));
      return new ItemStack(item, this.count);
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      json.addProperty("itemId", this.text);
      if (this.text2 != null) {
         json.addProperty("searchName", this.text2);
      }

      json.addProperty("count", this.count);
      json.addProperty("slotId", this.intValue);
      json.addProperty("maxSumBuy", this.longValue);
      json.addProperty("countBuy", this.intValue2);
      if (this.text3 != null) {
         json.addProperty("componentsJson", this.text3);
      }

      if (this.text4 != null) {
         json.addProperty("extraSettingsJson", this.text4);
      }

      return json;
   }

   public void load(JsonObject jsonObject) {
      if (jsonObject.has("itemId")) {
         this.text = jsonObject.get("itemId").getAsString();
      }

      if (jsonObject.has("searchName")) {
         this.text2 = jsonObject.get("searchName").getAsString();
      }

      if (jsonObject.has("count")) {
         this.count = jsonObject.get("count").getAsInt();
      }

      if (jsonObject.has("slotId")) {
         this.intValue = jsonObject.get("slotId").getAsInt();
      }

      if (jsonObject.has("maxSumBuy")) {
         this.longValue = jsonObject.get("maxSumBuy").getAsLong();
      }

      if (jsonObject.has("countBuy")) {
         this.intValue2 = jsonObject.get("countBuy").getAsInt();
      }

      if (jsonObject.has("componentsJson")) {
         this.text3 = jsonObject.get("componentsJson").getAsString();
      }

      if (jsonObject.has("extraSettingsJson")) {
         this.text4 = jsonObject.get("extraSettingsJson").getAsString();
      }
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   @Generated
   public int getCount() {
      return this.count;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public String getText3() {
      return this.text3;
   }

   @Generated
   public String getText4() {
      return this.text4;
   }

   @Generated
   public void setText(String text) {
      this.text = text;
   }

   @Generated
   public void setText2(String str) {
      this.text2 = str;
   }

   @Generated
   public void setCount(int count) {
      this.count = count;
   }

   @Generated
   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public void setLongValue(long longValue) {
      this.longValue = longValue;
   }

   @Generated
   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   @Generated
   public void setText3(String str) {
      this.text3 = str;
   }

   @Generated
   public void setText4(String str) {
      this.text4 = str;
   }
}

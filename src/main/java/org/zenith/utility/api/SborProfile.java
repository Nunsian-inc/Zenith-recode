package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.world.item.ItemStack;

public class SborProfile {
   private static final String text = "HolyWorld";
   private final String text2;
   private final File file;
   private String text3;
   private List<SborItem> items = new ArrayList<>();

   public SborProfile(String string) {
      this(string, "HolyWorld", new File(KitManager.file, string + "." + "Zenith".toLowerCase()));
   }

   public SborProfile(String string, String str) {
      this(string, str, new File(KitManager.file, method2408(string, str) + "." + "Zenith".toLowerCase()));
   }

   public SborProfile(String string, String str, File file) {
      this.text2 = string;
      this.text3 = method2407(str);
      this.file = file;
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      json.addProperty("name", this.text2);
      json.addProperty("server", this.text3);
      JsonArray jsonArray = new JsonArray();

      for (SborItem sborItem : this.items) {
         jsonArray.add(sborItem.save());
      }

      json.add("items", jsonArray);
      return json;
   }

   public void load(JsonObject json) {
      this.items.clear();
      if (json.has("server")) {
         this.text3 = method2407(json.get("server").getAsString());
      }

      if (json.has("items")) {
         JsonArray jsonArray = json.getAsJsonArray("items");

         for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            SborItem sborItem = new SborItem();
            sborItem.load(jsonObject);
            this.items.add(sborItem);
         }
      }
   }

   public List<ItemStack> method2405() {
      ArrayList<ItemStack> stacks = new ArrayList<>();

      for (SborItem sborItem : this.items) {
         stacks.add(sborItem.method1749());
      }

      return stacks;
   }

   public boolean method2406(String string) {
      return this.text3.equals(method2407(string));
   }

   private static String method2407(String string) {
      return string != null && !string.isBlank() ? string : "HolyWorld";
   }

   private static String method2408(String string, String str) {
      String str2 = method2407(str).replaceAll("[^\\p{L}\\p{N}._-]", "_");
      return str2 + "_" + string;
   }

   @Generated
   public String getName() {
      return this.text2;
   }

   @Generated
   public File getFile() {
      return this.file;
   }

   @Generated
   public String getText3() {
      return this.text3;
   }

   @Generated
   public List<SborItem> getItems() {
      return this.items;
   }

   @Generated
   public void setText3(String str) {
      this.text3 = str;
   }

   @Generated
   public void setItems(List<SborItem> items) {
      this.items = items;
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.bot.client.HeadlessBots;

public class ConfigFile {
   private final String text;
   private final File file;
   private boolean priority;

   public ConfigFile(String text) {
      this.text = text;
      this.file = new File(Manager.file, text + "." + "Zenith".toLowerCase());
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public void method7593() {
      this.priority = !this.priority;
   }

   public JsonObject save() {
      try {
         JsonObject json = new JsonObject();
         JsonObject json2 = new JsonObject();
         json2.addProperty("name", this.text);
         json2.addProperty("priority", this.priority);
         json.add("ConfigData", json2);
         json.add("Styles", Zenith.getInstance().getStyleManager().save());
         JsonObject json3 = new JsonObject();
         json3.addProperty("name", org.zenith.utility.config.AvatarUtil.method7257(Zenith.getInstance().getFiguraManager().getPath()));
         json3.addProperty("weapon", org.zenith.utility.config.AvatarUtil.method7257(Zenith.getInstance().getFiguraManager().getPath3()));
         json.add("FiguraData", json3);
         json.add("PetData", Zenith.getInstance().getPetManager().save());
         Wardrobe wardrobe = Wardrobe.method2488();
         if (wardrobe != null) {
            json.add("Wardrobe", wardrobe.method2511());
         }

         JsonObject json4 = new JsonObject();

         for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
            json4.add(moduleValue.getId(), moduleValue.save());
         }

         json.add("Modules", json4);
         JsonObject json5 = new JsonObject();
         json5.addProperty("language", Zenith.getInstance().getLocalization().getLanguageName());
         json.add("Language", json5);
         json.add("BotData", HeadlessBots.saveState());
         JsonObject json6 = new JsonObject();
         json.add("Interface", json6);
         return json;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public void load(JsonObject json) {
      if (json.has("ConfigData")) {
         JsonObject jsonObject = json.getAsJsonObject("ConfigData");
         if (jsonObject.has("priority")) {
            this.priority = jsonObject.get("priority").getAsBoolean();
         }
      }

      loadState(json);
   }

   public static void loadState(JsonObject json) {
      method7594(json);
      if (json.has("Styles")) {
         Zenith.getInstance().getStyleManager().load(json.getAsJsonObject("Styles"));
      }

      if (json.has("FiguraData")) {
         JsonObject jsonObject = json.getAsJsonObject("FiguraData");
         if (jsonObject.has("name")) {
            Zenith.getInstance().getFiguraManager().method7672(org.zenith.utility.config.AvatarUtil.method7258(jsonObject.get("name").getAsString()));
         }

         if (jsonObject.has("weapon")) {
            Zenith.getInstance().getFiguraManager().method7673(org.zenith.utility.config.AvatarUtil.method7258(jsonObject.get("weapon").getAsString()));
         }
      }

      if (json.has("Wardrobe") && json.get("Wardrobe").isJsonObject() && Wardrobe.method2488() != null) {
         Wardrobe.method2488().method2512(json.getAsJsonObject("Wardrobe"));
      }

      if (json.has("PetData")) {
         JsonObject jsonObject = json.getAsJsonObject("PetData");
         Zenith.getInstance().getPetManager().load(jsonObject);
      }

      if (json.has("Language")) {
         JsonObject jsonObject = json.getAsJsonObject("Language");
         if (jsonObject.has("language")) {
            Zenith.getInstance().getLocalization().method1728(jsonObject.get("language").getAsString());
         }
      }

      if (json.has("Modules")) {
         JsonObject jsonObject = json.getAsJsonObject("Modules");

         for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
            try {
               JsonObject jsonObject2 = jsonObject.getAsJsonObject(moduleValue.getId());
               if (jsonObject2 == null && moduleValue.getId().startsWith("zenith:")) {
                  jsonObject2 = jsonObject.getAsJsonObject(moduleValue.getName());
               }

               moduleValue.load(jsonObject2);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   private static void method7594(JsonObject json) {
      try {
         if (json.has("BotData") && json.get("BotData").isJsonObject()) {
            HeadlessBots.loadState(json.getAsJsonObject("BotData"));
         }
      } catch (Exception e) {
      }
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public File getFile() {
      return this.file;
   }

   @Generated
   public boolean isPriority() {
      return this.priority;
   }
}

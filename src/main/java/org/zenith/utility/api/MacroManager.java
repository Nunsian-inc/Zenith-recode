package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;

public class MacroManager extends JsonListStorage<MacroManager.Entry> implements MinecraftAccess {
   public MacroManager() {
      super("macros", "", (new TypeToken<ArrayList<MacroManager.Entry>>() {}).getType(), ArrayList::new);
      EventManager.register(this);
   }

   @Override
   public Gson createGson() {
      return new GsonBuilder().registerTypeAdapter(MacroManager.Entry.class, new MacroManager.Entry2()).setPrettyPrinting().create();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public void method6983(MacroManager.Entry entry) {
      this.items.add(entry);
   }

   public void method6984(String text, int intValue, String str) {
      this.items.add(new MacroManager.Entry(text, intValue, str));
   }

   public void method6985(String anotherString) {
      this.items.removeIf(var1x -> var1x.name().equalsIgnoreCase(anotherString));
   }

   public MacroManager.Entry method6986(String argument) {
      return this.items.stream().filter(var1x -> var1x.name().equalsIgnoreCase(argument)).findFirst().orElse(null);
   }

   public boolean method6987(String argument) {
      return this.method6986(argument) != null;
   }

   public void clear() {
      this.items.clear();
   }

   @EventTarget
   public void method6988(KeyEvent event) {
      if (mc.player != null) {
         for (MacroManager.Entry entry : this.items) {
            if (event.isPress(entry.getIntValue())) {
               this.method6989(entry);
            }
         }
      }
   }

   private void method6989(MacroManager.Entry entry) {
      if (entry.getText2().startsWith("/")) {
         mc.player.connection.sendCommand(entry.getText2().substring(1));
      } else {
         mc.player.connection.sendChat(entry.getText2());
      }
   }

   public static class Entry {
      @SerializedName("name")
      private final String text;
      @SerializedName("key")
      private final int intValue;
      @SerializedName("command")
      private final String text2;

      public Entry(String text, int intValue, String str) {
         this.text = text;
         this.intValue = intValue;
         this.text2 = str;
      }

      public String name() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText2() {
         return this.text2;
      }

      public Component toText() {
         String str = this.text.replace("\"", "\\\"");
         MutableComponent style = Component.literal(" " + this.text).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
         MutableComponent component = Component.literal(" [  " + Type.KEYBOARD.getOrCreate(org.zenith.utility.misc.KeyCompat.toSdl(this.intValue)).getDisplayName().getString() + "  ]").withStyle(ChatFormatting.AQUA);
         MutableComponent style2 = Component.literal(" -> " + this.text2).withStyle(ChatFormatting.GRAY);
         MutableComponent style3 = Component.literal("  [Удалить]")
            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
            .withStyle(
               var2x -> var2x.withHoverEvent(new ShowText(Component.literal("Подставить команду удаления макроса " + this.text)))
                  .withClickEvent(new SuggestCommand(".macro remove " + str))
            );
         return Component.empty().append(style).append(component).append(style2).append(style3);
      }
   }

   public static class Entry2 implements JsonDeserializer<MacroManager.Entry>, JsonSerializer<MacroManager.Entry> {
      public JsonElement serialize(MacroManager.Entry entry, java.lang.reflect.Type type, JsonSerializationContext serializationContext) {
         JsonObject json = new JsonObject();
         json.addProperty("name", entry.name());
         json.addProperty("key", entry.getIntValue());
         json.addProperty("command", entry.getText2());
         return json;
      }

      public MacroManager.Entry deserialize(JsonElement jsonElement, java.lang.reflect.Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         String string = getString(jsonObject, "name", "");
         int intValue = getInt(jsonObject, "key");
         String str = getString(jsonObject, "command", "");
         return new MacroManager.Entry(string, intValue, str);
      }

      private static String getString(JsonObject jsonObject, String memberName, String str) {
         JsonElement jsonElement = jsonObject.get(memberName);
         return jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsString() : str;
      }

      private static int getInt(JsonObject jsonObject, String memberName) {
         JsonElement jsonElement = jsonObject.get(memberName);
         if (jsonElement != null && !jsonElement.isJsonNull()) {
            return jsonElement.getAsInt();
         } else {
            throw new JsonParseException("Missing key " + memberName);
         }
      }
   }
}

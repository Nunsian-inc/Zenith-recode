package org.zenith.base.filemanager.impl.way;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.core.BlockPos;

public final class WayAdapter implements JsonDeserializer<Way>, JsonSerializer<Way> {
   public JsonElement serialize(Way way, Type type, JsonSerializationContext serializationContext) {
      JsonObject json = new JsonObject();
      json.addProperty("name", way.name());
      json.addProperty("server", way.server());
      JsonObject json2 = new JsonObject();
      BlockPos pos = way.pos();
      json2.addProperty("x", pos.getX());
      json2.addProperty("y", pos.getY());
      json2.addProperty("z", pos.getZ());
      json.add("pos", json2);
      return json;
   }

   public Way deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      String string = getString(jsonObject, "name", "");
      String server = getString(jsonObject, "server", "");
      JsonObject json = jsonObject.getAsJsonObject("pos");
      if (json == null) {
         throw new JsonParseException("Missing 'pos' for Way");
      } else {
         int x = getInt(json, "x");
         int y = getInt(json, "y");
         int z = getInt(json, "z");
         return new Way(string, new BlockPos(x, y, z), server);
      }
   }

   private static String getString(JsonObject jsonObject, String memberName, String str) {
      JsonElement jsonElement = jsonObject.get(memberName);
      return jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsString() : str;
   }

   private static int getInt(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      if (jsonElement != null && !jsonElement.isJsonNull()) {
         return jsonElement.getAsInt();
      } else {
         throw new JsonParseException("Missing int: " + memberName);
      }
   }
}

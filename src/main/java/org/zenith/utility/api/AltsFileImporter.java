package org.zenith.utility.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import org.zenith.utility.config.Data4;

public final class AltsFileImporter {
   private AltsFileImporter() {
   }

   public static Path method4853(Minecraft minecraft) {
      Path path = minecraft.gameDirectory.toPath().resolve("alts");

      try {
         if (!Files.exists(path)) {
            Files.createDirectories(path);
         }
      } catch (Exception e) {
      }

      return path;
   }

   public static List<Data4> method4854(Minecraft minecraft) {
      Path path = method4853(minecraft);
      ArrayList<Data4> data4s = new ArrayList<>();
      if (!Files.isDirectory(path)) {
         return data4s;
      } else {
         try (Stream<java.nio.file.Path> list = Files.list(path)) {
            list.filter(var0x -> Files.isRegularFile(var0x)).forEach(var1x -> {
               String var2x = var1x.getFileName().toString();
               if (var2x.endsWith(".json")) {
                  try {
                     String var3x = Files.readString(var1x, StandardCharsets.UTF_8);
                     JsonObject jsonObject = JsonParser.parseString(var3x).getAsJsonObject();
                     String string = jsonObject.has("username") ? jsonObject.get("username").getAsString() : var2x.replace(".json", "");
                     UUID uUID = null;
                     if (jsonObject.has("uuid")) {
                        try {
                           uUID = UUID.fromString(jsonObject.get("uuid").getAsString());
                        } catch (Exception var8x) {
                        }
                     }

                     String str = jsonObject.has("credential") ? jsonObject.get("credential").getAsString() : var3x;
                     data4s.add(new Data4(string, uUID, var1x, str));
                  } catch (Exception e) {
                  }
               } else if (var2x.endsWith(".txt")) {
                  String text = var2x.replaceAll("\\.txt$", "");
                  data4s.add(new Data4(text, null, var1x, null));
               }
            });
         } catch (Exception e) {
         }

         return data4s;
      }
   }

   public static void method4855(Minecraft minecraft, String str, UUID uuid, String str2) {
      try {
         Path path = method4853(minecraft);
         Path path2 = path.resolve(str + ".json");
         JsonObject json = new JsonObject();
         json.addProperty("username", str);
         if (uuid != null) {
            json.addProperty("uuid", uuid.toString());
         }

         json.addProperty("credential", str2 != null ? str2 : "");
         json.addProperty("savedAt", System.currentTimeMillis());
         Files.writeString(path2, json.toString(), StandardCharsets.UTF_8);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

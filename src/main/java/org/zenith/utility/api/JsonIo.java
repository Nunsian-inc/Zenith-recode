package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonIo {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Json");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private JsonIo() {
   }

   public static Gson getGson() {
      return gson;
   }

   public static JsonObject method1009(Path path) {
      if (!Files.isRegularFile(path)) {
         return null;
      } else {
         try {
            JsonObject jsonObject;
            try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
               jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            }

            return jsonObject;
         } catch (RuntimeException | IOException e) {
            logger.error("Не удалось прочитать {}", path, e);
            return null;
         }
      }
   }

   public static boolean method1010(Path path, JsonObject jsonObject2) {
      try {
         Files.createDirectories(path.getParent());
         Path sibling = path.resolveSibling(path.getFileName() + ".tmp");

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(sibling, StandardCharsets.UTF_8)) {
            gson.toJson(jsonObject2, bufferedWriter);
         }

         Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING);
         return true;
      } catch (RuntimeException | IOException e) {
         logger.error("Не удалось записать {}", path, e);
         return false;
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CloudJsonIo {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private CloudJsonIo() {
   }

   public static JsonObject method5992(Path path) {
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

   public static boolean method5993(Path path, JsonObject json) {
      Path tempFile = null;

      boolean flag;
      try {
         Files.createDirectories(path.getParent());
         tempFile = Files.createTempFile(path.getParent(), path.getFileName() + ".", ".tmp");
         Files.writeString(tempFile, gson.toJson(json), StandardCharsets.UTF_8);

         try (FileChannel fileChannel = FileChannel.open(tempFile, StandardOpenOption.WRITE)) {
            fileChannel.force(true);
         }

         try {
            Files.move(tempFile, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING);
         }

         tempFile = null;
         return true;
      } catch (RuntimeException | IOException e) {
         logger.error("Не удалось записать {}", path, e);
         flag = false;
      } finally {
         if (tempFile != null) {
            try {
               Files.deleteIfExists(tempFile);
            } catch (IOException e) {
            }
         }
      }

      return flag;
   }
}

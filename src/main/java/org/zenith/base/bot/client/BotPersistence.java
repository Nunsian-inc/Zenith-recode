package org.zenith.base.bot.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public final class BotPersistence {
   private static final String FILE_NAME = "bots." + "Zenith".toLowerCase();
   private static final String KEY = "siMunids";

   private BotPersistence() {
   }

   public static JsonObject load() {
      File file2 = file();
      if (!file2.exists()) {
         return null;
      } else {
         try {
            String src = Files.readString(file2.toPath(), StandardCharsets.UTF_8).trim();
            if (src.isBlank()) {
               return null;
            } else {
               byte[] allBytes = Base64.getDecoder().decode(src);
               byte[] bytes = AesGcmCrypto.method5340(allBytes, "siMunids");
               return JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
            }
         } catch (Exception e) {
            return null;
         }
      }
   }

   public static void save(JsonObject saveState) {
      if (saveState != null) {
         try {
            File file2 = file();
            File parentFile = file2.getParentFile();
            if (parentFile != null) {
               parentFile.mkdirs();
            }

            String json = new GsonBuilder().setPrettyPrinting().create().toJson(saveState);
            String encodeToString = Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(json.getBytes(StandardCharsets.UTF_8), "siMunids"));
            Files.writeString(file2.toPath(), encodeToString, StandardCharsets.UTF_8);
         } catch (Exception e) {
         }
      }
   }

   private static File file() {
      return new File(Zenith.file, FILE_NAME);
   }
}

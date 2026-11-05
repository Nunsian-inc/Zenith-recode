package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import org.zenith.Zenith;

public final class OnboardingUtil {
   private static final String text = "onboarding.json";
   private static final int CONST_intValue = 1;
   private static Boolean booleanValue;

   private OnboardingUtil() {
   }

   public static boolean method2705() {
      if (booleanValue != null) {
         return booleanValue;
      } else {
         File file = new File(Zenith.file, "onboarding.json");
         if (!file.isFile()) {
            booleanValue = false;
            return false;
         } else {
            try (FileReader fileReader = new FileReader(file)) {
               JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
               booleanValue = jsonObject.has("version") && jsonObject.get("version").getAsInt() >= 1;
            } catch (Exception e) {
               booleanValue = false;
            }

            return booleanValue;
         }
      }
   }

   public static void complete() {
      booleanValue = true;
      JsonObject json = new JsonObject();
      json.addProperty("version", 1);
      json.addProperty("completedAt", System.currentTimeMillis());
      File file = Zenith.file;
      if (file.isDirectory() || file.mkdirs()) {
         try (FileWriter fileWriter = new FileWriter(new File(file, "onboarding.json"))) {
            new Gson().toJson(json, fileWriter);
         } catch (Exception e) {
         }
      }
   }

   public static void reset() {
      booleanValue = false;
      File file = new File(Zenith.file, "onboarding.json");
      if (file.isFile() && !file.delete()) {
         booleanValue = null;
      }
   }
}

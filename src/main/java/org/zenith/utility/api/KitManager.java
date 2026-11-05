package org.zenith.utility.api;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public class KitManager {
   public static final File file = new File(Zenith.file, "kits");
   private final String text = "kit";
   private final List<SborProfile> items = new ArrayList<>();

   public KitManager() {
      file.mkdirs();
      this.method7274();
   }

   public void method7274() {
      this.items.clear();
      File[] listFiles = file.listFiles();
      if (listFiles != null) {
         for (File listFile : listFiles) {
            if (listFile.getName().endsWith("." + "Zenith".toLowerCase())) {
               SborProfile sborProfile = this.method7276(listFile);
               if (sborProfile != null) {
                  this.items.add(sborProfile);
               }
            }
         }
      }
   }

   public SborProfile method7275(String str) {
      if (str == null) {
         return null;
      } else {
         File listFile = new File(file, str + "." + "Zenith".toLowerCase());
         return !listFile.exists() ? null : this.method7276(listFile);
      }
   }

   private SborProfile method7276(File listFile) {
      if (listFile != null && listFile.exists()) {
         try {
            SborProfile sborProfile;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(listFile))) {
               JsonParser jsonParser = new JsonParser();
               String line = bufferedReader.readLine();
               byte[] allBytes = Base64.getDecoder().decode(line);
               byte[] bytes = AesGcmCrypto.method5340(allBytes, "kit");
               String str = new String(bytes, StandardCharsets.UTF_8);
               JsonObject json = (JsonObject)jsonParser.parse(str);
               String str2 = listFile.getName().replace("." + "Zenith".toLowerCase(), "");
               String string = json.has("name") ? json.get("name").getAsString() : str2;
               String string2 = json.has("server") ? json.get("server").getAsString() : "HolyWorld";
               SborProfile sborProfile2 = new SborProfile(string, string2, listFile);
               sborProfile2.load(json);
               sborProfile = sborProfile2;
            }

            return sborProfile;
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean method7277(SborProfile sborProfile) {
      try {
         if (sborProfile == null) {
            return false;
         } else {
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(sborProfile.save());
            json = Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(json.getBytes(), "kit"));

            try {
               FileWriter fileWriter = new FileWriter(sborProfile.getFile());
               fileWriter.write(json);
               fileWriter.close();
               return true;
            } catch (IOException e) {
               return false;
            }
         }
      } catch (Exception e) {
         return false;
      }
   }

   public SborProfile method7278(String string, List<SborItem> items) {
      SborProfile sborProfile = new SborProfile(string);
      sborProfile.setItems(items);
      if (this.method7277(sborProfile)) {
         this.items.add(sborProfile);
         return sborProfile;
      } else {
         return null;
      }
   }

   public SborProfile method7279(String string, List<SborItem> items, String str) {
      SborProfile sborProfile = new SborProfile(string, str);
      sborProfile.setItems(items);
      if (this.method7277(sborProfile)) {
         this.items.add(sborProfile);
         return sborProfile;
      } else {
         return null;
      }
   }

   public boolean method7280(String anObject) {
      if (anObject == null) {
         return false;
      } else {
         SborProfile sborProfile;
         if ((sborProfile = this.method7282(anObject)) != null) {
            File file = sborProfile.getFile();
            if (file.exists() && file.delete()) {
               this.items.remove(sborProfile);
               return true;
            }
         }

         return false;
      }
   }

   public boolean method7281(SborProfile sborProfile) {
      if (sborProfile == null) {
         return false;
      } else {
         File file = sborProfile.getFile();
         if (file.exists() && file.delete()) {
            this.items.remove(sborProfile);
            return true;
         } else {
            return false;
         }
      }
   }

   public SborProfile method7282(String anObject) {
      return anObject == null ? null : this.items.stream().filter(var1x -> var1x.getName().equals(anObject)).findFirst().orElse(null);
   }

   public SborProfile method7283(String anObject, String string) {
      return anObject == null ? null : this.items.stream().filter(var2x -> var2x.getName().equals(anObject) && var2x.method2406(string)).findFirst().orElse(null);
   }

   public List<SborProfile> method7284(String string) {
      return this.items.stream().filter(var1x -> var1x.method2406(string)).toList();
   }

   public List<String> method7285() {
      ArrayList<String> strings = new ArrayList<>();

      for (SborProfile sborProfile : this.items) {
         if (!strings.contains(sborProfile.getName())) {
            strings.add(sborProfile.getName());
         }
      }

      return strings;
   }

   @Generated
   public String method7286() {
      return "kit";
   }

   @Generated
   public List<SborProfile> getItems() {
      return this.items;
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.Zenith;

public final class ServerNameNormalizer {
   private final List<SborList> items = new ArrayList<>();
   private boolean flag;

   public List<SborList> method1229(String anObject) {
      this.method1236();
      ArrayList<SborList> sborLists = new ArrayList<>();

      for (SborList sborList : this.items) {
         if (sborList.method1751(anObject)) {
            sborLists.add(sborList);
         }
      }

      return sborLists;
   }

   public SborList method1230(String inputName, String anObject) {
      this.method1236();

      for (SborList sborList : this.items) {
         if (sborList.method1751(anObject) && sborList.getName().equalsIgnoreCase(inputName)) {
            return sborList;
         }
      }

      return null;
   }

   public void method1231(SborList sborList) {
      if (sborList != null) {
         this.method1236();
         SborList sborList2 = this.method1230(sborList.getName(), sborList.getText2());
         if (sborList2 != null) {
            this.items.set(this.items.indexOf(sborList2), sborList);
         } else {
            this.items.add(sborList);
         }
      }
   }

   public boolean method1232(SborList sborList) {
      return sborList != null && this.items.remove(sborList);
   }

   public String method1233(String inputName, String anObject) {
      this.method1236();
      if (this.method1230(inputName, anObject) == null) {
         return inputName;
      } else {
         for (int i = 2; i < 1000; i++) {
            String str = inputName + " " + i;
            if (this.method1230(str, anObject) == null) {
               return str;
            }
         }

         return inputName + " " + System.currentTimeMillis();
      }
   }

   public JsonArray method1234() {
      this.method1236();
      JsonArray jsonArray = new JsonArray();

      for (SborList sborList : this.items) {
         jsonArray.add(sborList.save());
      }

      return jsonArray;
   }

   public void method1235(JsonArray jsonArray) {
      this.flag = true;
      this.items.clear();
      if (jsonArray != null) {
         for (JsonElement json : jsonArray) {
            if (json.isJsonObject()) {
               SborList sborList = SborList.method1754(json.getAsJsonObject());
               if (sborList != null) {
                  this.items.add(sborList);
               }
            }
         }
      }
   }

   private void method1236() {
      if (!this.flag) {
         this.flag = true;
         KitManager kitManager = Zenith.getInstance() == null ? null : Zenith.getInstance().getKitManager();
         if (kitManager != null) {
            for (SborProfile sborProfile : kitManager.getItems()) {
               if (sborProfile != null && sborProfile.getName() != null && !sborProfile.getName().isBlank()) {
                  this.items.add(method1237(sborProfile));
               }
            }
         }
      }
   }

   private static SborList method1237(SborProfile sborProfile) {
      ArrayList coll = new ArrayList();

      for (SborItem sborItem : sborProfile.getItems()) {
         if (sborItem != null && sborItem.getText2() != null && !sborItem.getText2().isBlank()) {
            coll.add(new SborList.Data(sborItem.getIntValue(), sborItem.getText2(), sborItem.getText(), sborItem.getIntValue2(), sborItem.getLongValue(), -1.0F));
         }
      }

      return new SborList(sborProfile.getName(), normalize(sborProfile.getText3()), coll);
   }

   private static String normalize(String str) {
      if (str == null) {
         return "HolyWorld";
      } else {
         String lowerCase = str.toLowerCase(Locale.ROOT);
         if (lowerCase.startsWith("funtime")) {
            return "Funtime 1.21";
         } else {
            return lowerCase.startsWith("spooky") ? "SpookyTime" : "HolyWorld";
         }
      }
   }
}

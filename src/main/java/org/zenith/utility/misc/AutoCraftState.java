package org.zenith.utility.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.api.Class1;
import org.zenith.utility.game.CraftRecipe;

public final class AutoCraftState {
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 2;
   private final AutoCraftModule autoCraftModule;
   private String text = "";
   private int intValue = 0;
   private final Set<String> items = new HashSet<>();
   private final Map<String, Integer> items2 = new HashMap<>();

   public AutoCraftState(AutoCraftModule autoCraftModule) {
      this.autoCraftModule = autoCraftModule;
   }

   public void reset() {
      this.items.clear();
      this.items2.clear();
      this.method744();
   }

   public void method744() {
      this.text = "";
      this.intValue = 0;
   }

   public int method745() {
      return 2;
   }

   public boolean method746(CraftRecipe craftRecipe, String key) {
      String text = this.method752(craftRecipe, key);
      if (!text.isBlank() && !text.equals(this.text)) {
         this.text = text;
         this.intValue = 2;
      }

      if (this.intValue > 0) {
         this.intValue--;
         return true;
      } else {
         return false;
      }
   }

   public boolean method747(CraftRecipe craftRecipe, String key) {
      String str = this.method752(craftRecipe, key);
      return !str.isBlank() && this.items.contains(str);
   }

   public boolean method748(CraftRecipe craftRecipe, String key) {
      String str = this.method752(craftRecipe, key);
      return !str.isBlank() && this.items2.getOrDefault(str, 0) >= 2;
   }

   public int method749(CraftRecipe craftRecipe, String key) {
      String str = this.method752(craftRecipe, key);
      if (str.isBlank()) {
         return 0;
      } else {
         int n = this.items2.getOrDefault(str, 0) + 1;
         this.items2.put(str, n);
         return n;
      }
   }

   public void method750(CraftRecipe craftRecipe, String key) {
      String str = this.method752(craftRecipe, key);
      if (!str.isBlank()) {
         this.items2.remove(str);
         this.items.remove(str);
      }
   }

   public void method751(CraftRecipe craftRecipe, String key, String str, String anotherString, boolean flag) {
      String str2 = this.method752(craftRecipe, key);
      if (!str2.isBlank()) {
         boolean flag2 = this.items.add(str2);
         this.items2.put(str2, 2);
         if (flag && flag2) {
            this.autoCraftModule.method1128("Source empty: " + this.autoCraftModule.method1107(str, anotherString));
         }
      }
   }

   private String method752(CraftRecipe craftRecipe, String key) {
      if (craftRecipe != null && key != null && !key.isBlank()) {
         Class1 class2 = craftRecipe.method5398(key);
         long l = class2 != null && class2.method56() ? class2.method57().asLong() : Long.MIN_VALUE;
         return craftRecipe.getText2().toLowerCase(Locale.ROOT) + ":" + craftRecipe.getId().toLowerCase(Locale.ROOT) + ":" + key + ":" + l;
      } else {
         return "";
      }
   }
}

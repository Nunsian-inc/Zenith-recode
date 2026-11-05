package org.zenith.utility.game;

import java.util.Locale;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.misc.CraftPresetData;

public final class CraftPresetSaver {
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final String text = "custom";
   private final AutoCraftModule autoCraftModule;
   private CraftPresetData craftPresetData = CraftPresetData.method6198();

   public CraftPresetSaver(AutoCraftModule autoCraftModule) {
      this.autoCraftModule = autoCraftModule;
   }

   public boolean isActive() {
      return !this.craftPresetData.method6199();
   }

   public String method3189() {
      return this.craftPresetData.method6199() ? "" : this.craftPresetData.method6205(this.autoCraftModule);
   }

   public void reset() {
      this.craftPresetData = CraftPresetData.method6198();
   }

   public void method3190() {
      String str = this.autoCraftModule.method1098();
      if (str == null || str.isBlank()) {
         str = "custom";
      }

      this.autoCraftModule.method1112();
      this.craftPresetData = new CraftPresetData(str);
      this.autoCraftModule.method1113();
      AutoCraftModule.mc.gui.setScreen(null);
   }

   public boolean method3191() {
      if (this.craftPresetData.method6199()) {
         return false;
      } else if (AutoCraftModule.mc.player != null && AutoCraftModule.mc.level != null) {
         CraftingMenu craftingMenu = AutoCraftModule.mc.player.containerMenu instanceof CraftingMenu craftingMenu2 ? craftingMenu2 : null;
         if (craftingMenu != null && this.craftPresetData.method6202() && this.method3193(craftingMenu)) {
            this.method3196();
            return true;
         } else if (craftingMenu == null) {
            if (this.craftPresetData.method6202()) {
               if (this.method3193(null)) {
                  this.method3196();
                  return true;
               }

               this.craftPresetData.method6204();
            }

            return true;
         } else {
            ItemStack stack = craftingMenu.getSlot(0).getItem();
            if (this.craftPresetData.method6202() && stack.isEmpty()) {
               if (this.method3193(craftingMenu)) {
                  this.method3196();
                  return true;
               } else {
                  this.craftPresetData.method6204();
                  return true;
               }
            } else {
               if (!stack.isEmpty() && !this.craftPresetData.method6202()) {
                  this.method3192(craftingMenu, stack);
               }

               if (this.craftPresetData.method6202() && this.method3193(craftingMenu)) {
                  this.method3196();
               }

               return true;
            }
         }
      } else {
         return true;
      }
   }

   private void method3192(CraftingMenu craftingMenu, ItemStack stack) {
      String identifier = method3201(stack.getItem());
      if (!identifier.isBlank()) {
         String anotherString = this.method3195(stack);
         String[] strings = new String[9];
         String[] strings2 = new String[9];
         int n = 0;

         for (int i = 0; i < 9; i++) {
            ItemStack stack2 = craftingMenu.getSlot(1 + i).getItem();
            if (stack2.isEmpty()) {
               strings[i] = "";
               strings2[i] = "";
            } else {
               strings[i] = method3201(stack2.getItem());
               strings2[i] = this.method3195(stack2);
               if (!strings[i].isBlank()) {
                  n++;
               }
            }
         }

         if (n >= 2) {
            int n2 = this.method3194(craftingMenu, identifier, anotherString);
            this.craftPresetData.method6203(identifier, anotherString, strings, strings2, n2);
         }
      }
   }

   private boolean method3193(CraftingMenu craftingMenu) {
      if (!this.craftPresetData.method6199() && this.craftPresetData.method6202()) {
         int n = this.method3194(craftingMenu, this.craftPresetData.getText2(), this.craftPresetData.getText3());
         return n > this.craftPresetData.getIntValue();
      } else {
         return false;
      }
   }

   private int method3194(CraftingMenu craftingMenu, String identifier, String anotherString) {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack carried = AutoCraftModule.mc.player.getInventory().getItem(i);
         if (!carried.isEmpty() && this.autoCraftModule.method1073(carried, identifier, anotherString)) {
            n += carried.getCount();
         }
      }

      if (craftingMenu != null) {
         ItemStack carried = craftingMenu.getCarried();
         if (!carried.isEmpty() && this.autoCraftModule.method1073(carried, identifier, anotherString)) {
            n += carried.getCount();
         }
      }

      return n;
   }

   private String method3195(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return "";
      } else {
         return stack.get(DataComponents.CUSTOM_NAME) == null ? "" : stack.getHoverName().getString();
      }
   }

   private void method3196() {
      CraftRecipe craftRecipe = this.method3197(this.craftPresetData);
      this.craftPresetData = CraftPresetData.method6198();
      if (!this.autoCraftModule.isEnabledRaw()) {
         this.autoCraftModule.method1114();
      }

      if (craftRecipe != null) {
         this.autoCraftModule.method1127("Craft saved: " + craftRecipe.getText3());
      } else {
         this.autoCraftModule.method1128("Unable to save crafted preset");
      }
   }

   private CraftRecipe method3197(CraftPresetData craftPresetData) {
      if (craftPresetData != null && !craftPresetData.method6199() && craftPresetData.method6202()) {
         String text = craftPresetData.getText();
         if (text == null || text.isBlank()) {
            text = "custom";
         }

         String str = this.autoCraftModule.method1107(craftPresetData.getText2(), craftPresetData.getText3());
         String string = this.method3198(text, str);
         String str2 = this.method3200(string, craftPresetData.getText2());
         String text2 = str2;
         int n = 1;

         while (this.autoCraftModule.method1094(text, text2) != null) {
            text2 = str2 + "_" + n++;
         }

         CraftRecipe craftRecipe = new CraftRecipe(text2, text, string);
         craftRecipe.setFlag2(false);
         craftRecipe.setLocked(false);
         craftRecipe.method5403(craftPresetData.getText2());
         craftRecipe.method5404(craftPresetData.getText3());

         for (int i = 0; i < 9; i++) {
            craftRecipe.method5390(i, craftPresetData.method6200(i));
            craftRecipe.method5392(i, craftPresetData.method6201(i));
         }

         this.autoCraftModule.method1104(craftRecipe);
         this.autoCraftModule.method1115(text, text2);
         this.autoCraftModule.method1091();
         Zenith.getInstance().getManager().save();
         return craftRecipe;
      } else {
         return null;
      }
   }

   private String method3198(String text, String str) {
      String str2 = str == null ? "" : str;
      if (str2.isBlank()) {
         str2 = "New Craft";
      }

      String anotherString = str2;
      int n = 2;

      while (this.method3199(text, anotherString)) {
         anotherString = str2 + " " + n++;
      }

      return anotherString;
   }

   private boolean method3199(String text, String anotherString) {
      for (CraftRecipe craftRecipe : this.autoCraftModule.method1092(text)) {
         if (craftRecipe.getText3().equalsIgnoreCase(anotherString)) {
            return true;
         }
      }

      return false;
   }

   private String method3200(String string, String str) {
      String str2 = string == null ? "" : string.toLowerCase(Locale.ROOT).replace(" ", "_").replaceAll("[^a-z0-9_]+", "");
      if (str2.isBlank()) {
         String str3 = str == null ? "" : str;
         int indexOf = str3.indexOf(58);
         if (indexOf != -1 && indexOf + 1 < str3.length()) {
            str3 = str3.substring(indexOf + 1);
         }

         str2 = str3.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]+", "_");
      }

      if (str2.isBlank()) {
         str2 = "item";
      }

      return "captured_" + str2;
   }

   private static String method3201(Item thing) {
      return thing != null && thing != Items.AIR ? BuiltInRegistries.ITEM.getKey(thing).toString() : "";
   }
}

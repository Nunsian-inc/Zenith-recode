package org.zenith.utility.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class PotionItemMatcher extends ItemMatcher {
   private static final Pattern pattern = Pattern.compile(",?UUID:\\[I;[-0-9]+,[-0-9]+,[-0-9]+,[-0-9]+]");
   private static final Pattern pattern2 = Pattern.compile(".*\".*\".*");
   private final Map<String, String> items;

   public PotionItemMatcher(ItemStack itemStack, String text, String str, ItemMatcher.Type type) {
      super(itemStack, text, str, type);
      this.items = new HashMap<>();
   }

   public PotionItemMatcher(ItemStack itemStack, String text, ItemMatcher.Type type, Map<String, String> items) {
      super(itemStack, text, type);
      this.items = items;
   }

   public PotionItemMatcher(ItemStack defaultInstance, String text, ItemMatcher.Type type, String str, String str2) {
      super(defaultInstance, text, type);
      this.items = new HashMap<>();
      this.items.put(str, str2);
   }

   public PotionItemMatcher(ItemStack defaultInstance, String text, String str, ItemMatcher.Type type, String str2, String str3) {
      super(defaultInstance, text, str, type);
      this.items = new HashMap<>();
      this.items.put(str2, str3);
   }

   @Override
   public boolean method2672(ItemStack stack) {
      if (!super.method2672(stack)) {
         return false;
      } else {
         CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
         if (customData == null) {
            return false;
         } else {
            CompoundTag copyTag = customData.copyTag();

            for (Entry entry : this.items.entrySet()) {
               String key = (String)entry.getKey();
               String input = (String)entry.getValue();
               if (!copyTag.contains(key)) {
                  return false;
               }

               if (method2673(key, input)) {
                  if (!method2674(copyTag, key, input)) {
                     return false;
                  }
               } else {
                  String str = pattern.matcher(copyTag.get(key).toString()).replaceAll("");
                  String str2 = pattern.matcher(input).replaceAll("");
                  if (!str.contains(str2)) {
                     return false;
                  }

                  boolean flag = str2.startsWith("{") || str2.startsWith("[");
                  if (!flag && pattern2.matcher(str).matches()) {
                     String str3 = "\"" + str2 + "\"";
                     if (!str.contains(str3)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   private static boolean method2673(String key, String input) {
      return "custom_potion_effects".equals(key) && input.startsWith("minecraft:") && !input.contains("{") && !input.contains("[");
   }

   private static boolean method2674(CompoundTag copyTag, String key, String input) {
      String stringOr = input;
      int n = -1;
      int indexOf = input.indexOf(64);
      if (indexOf > 0) {
         stringOr = input.substring(0, indexOf);

         try {
            n = Integer.parseInt(input.substring(indexOf + 1));
         } catch (NumberFormatException e) {
            n = -1;
         }
      }

      if (!(copyTag.get(key) instanceof ListTag)) {
         return copyTag.get(key).toString().contains(stringOr);
      } else {
         String str = method2675(stringOr);
         ListTag listOrEmpty = copyTag.getListOrEmpty(key);
         boolean flag = false;

         for (int i = 0; i < listOrEmpty.size(); i++) {
            CompoundTag compoundOrEmpty = listOrEmpty.getCompoundOrEmpty(i);
            if (!str.equals(method2675(compoundOrEmpty.getStringOr("id", "")))) {
               return false;
            }

            if (n < 0 || method2676(compoundOrEmpty) >= n) {
               flag = true;
            }
         }

         return flag;
      }
   }

   private static String method2675(String stringOr) {
      return stringOr != null && stringOr.startsWith("minecraft:") ? stringOr.substring("minecraft:".length()) : String.valueOf(stringOr);
   }

   private static int method2676(CompoundTag compoundOrEmpty) {
      return compoundOrEmpty.get("amplifier") instanceof NumericTag numericTag ? numericTag.intValue() : 0;
   }
}

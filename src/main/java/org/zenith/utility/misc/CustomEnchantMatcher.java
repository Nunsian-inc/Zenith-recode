package org.zenith.utility.misc;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.zenith.Zenith;

public class CustomEnchantMatcher extends EnchantMatcher {
   public CustomEnchantMatcher(String stringOr, String str, int intOr) {
      super(stringOr, str, intOr);
   }

   @Override
   public boolean method1746(ItemStack stack) {
      CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
      if (customData == null) {
         return false;
      } else {
         boolean flag = Zenith.getInstance().getServerDetector().method1639();
         String name = flag ? "custom-enchantments" : "Enchantments";
         CompoundTag copyTag = customData.copyTag();
         if (copyTag.contains(name)) {
            ListTag listOrEmpty = copyTag.getListOrEmpty(name);

            for (int i = 0; i < listOrEmpty.size(); i++) {
               CompoundTag compoundOrEmpty = listOrEmpty.getCompoundOrEmpty(i);
               String stringOr = compoundOrEmpty.getStringOr(flag ? "type" : "id", "");
               int intOr = compoundOrEmpty.getIntOr(flag ? "level" : "lvl", 0);
               if (stringOr.equals(this.text2)) {
                  return intOr >= this.intValue;
               }
            }
         }

         return false;
      }
   }
}

package org.zenith.utility.misc;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.zenith.Zenith;

public class CustomEnchantMatcher extends EnchantMatcher {
   public CustomEnchantMatcher(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean method1746(ItemStack var1) {
      CustomData var2 = var1.get(DataComponents.CUSTOM_DATA);
      if (var2 == null) {
         return false;
      } else {
         boolean var3 = Zenith.getInstance().getServerDetector().method1639();
         String var4 = var3 ? "custom-enchantments" : "Enchantments";
         CompoundTag var5 = var2.copyTag();
         if (var5.contains(var4)) {
            ListTag var6 = var5.getListOrEmpty(var4);

            for (int var7 = 0; var7 < var6.size(); var7++) {
               CompoundTag var8 = var6.getCompoundOrEmpty(var7);
               String var9 = var8.getStringOr(var3 ? "type" : "id", "");
               int var10 = var8.getIntOr(var3 ? "level" : "lvl", 0);
               if (var9.equals(this.text2)) {
                  return var10 >= this.intValue;
               }
            }
         }

         return false;
      }
   }
}

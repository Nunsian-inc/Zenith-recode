package org.zenith.utility.misc;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class VanillaEnchantMatcher extends EnchantMatcher {
   private static final Map<ResourceKey<?>, String> items = new ConcurrentHashMap<>();

   public VanillaEnchantMatcher(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean method1746(ItemStack var1) {
      if (this.intValue <= 0) {
         return true;
      } else {
         ItemEnchantments var2 = var1.getEnchantments();

         for (Entry var4 : var2.entrySet()) {
            Optional var5 = ((Holder)var4.getKey()).unwrapKey();
            String var6 = var5.isPresent() ? items.computeIfAbsent((ResourceKey<?>)var5.get(), var0 -> Optional.of(var0).toString()) : "Optional.empty";
            if (var6.contains(this.text2)) {
               return var4.getIntValue() >= this.intValue;
            }
         }

         return false;
      }
   }
}

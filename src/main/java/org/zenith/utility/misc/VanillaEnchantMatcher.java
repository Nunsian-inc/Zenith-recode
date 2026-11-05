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

   public VanillaEnchantMatcher(String string, String str, int intValue) {
      super(string, str, intValue);
   }

   @Override
   public boolean method1746(ItemStack stack) {
      if (this.intValue <= 0) {
         return true;
      } else {
         ItemEnchantments enchantments = stack.getEnchantments();

         for (Entry entry : enchantments.entrySet()) {
            Optional unwrapKey = ((Holder)entry.getKey()).unwrapKey();
            String str = unwrapKey.isPresent() ? items.computeIfAbsent((ResourceKey<?>)unwrapKey.get(), resourceKey -> Optional.of(resourceKey).toString()) : "Optional.empty";
            if (str.contains(this.text2)) {
               return entry.getIntValue() >= this.intValue;
            }
         }

         return false;
      }
   }
}

package org.zenith.utility.game;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class PotionTypeMatcher extends ItemMatcher {
   private final Holder<Potion> registryEntry;

   public PotionTypeMatcher(Item item, Holder<Potion> registryEntry, String text, ItemMatcher.Type type) {
      super(item.getDefaultInstance(), text, type);
      this.registryEntry = registryEntry;
      this.itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(registryEntry), Optional.empty(), List.of(), Optional.empty()));
   }

   @Override
   public boolean method2672(ItemStack stack) {
      if (!super.method2672(stack)) {
         return false;
      } else {
         PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
         if (potionContents == null) {
            return false;
         } else {
            var potion = potionContents.potion();
            return potion.isEmpty() ? false : this.method3301((Holder<Potion>)potion.get());
         }
      }
   }

   private boolean method3301(Holder<Potion> holder) {
      if (holder.equals(this.registryEntry)) {
         return true;
      } else {
         Identifier key = BuiltInRegistries.POTION.getKey(this.registryEntry.value());
         Identifier id = BuiltInRegistries.POTION.getKey((Potion)holder.value());
         return key != null && id != null
            ? key.getNamespace().equals(id.getNamespace()) && method3302(key.getPath()).equals(method3302(id.getPath()))
            : false;
      }
   }

   private static String method3302(String path) {
      if (path.startsWith("long_")) {
         return path.substring("long_".length());
      } else {
         return path.startsWith("strong_") ? path.substring("strong_".length()) : path;
      }
   }
}

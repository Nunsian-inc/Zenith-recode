package org.zenith.utility.game;

import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ArmorSlots {
   public static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

   private ArmorSlots() {
   }

   public static List<ItemStack> method5384(LivingEntity player) {
      return List.of(
         player.getItemBySlot(EquipmentSlot.FEET),
         player.getItemBySlot(EquipmentSlot.LEGS),
         player.getItemBySlot(EquipmentSlot.CHEST),
         player.getItemBySlot(EquipmentSlot.HEAD)
      );
   }

   public static List<ItemStack> method5385(LivingEntity player) {
      return List.of(player.getItemBySlot(EquipmentSlot.MAINHAND), player.getItemBySlot(EquipmentSlot.OFFHAND));
   }
}

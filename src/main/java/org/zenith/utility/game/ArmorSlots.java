package org.zenith.utility.game;

import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ArmorSlots {
   public static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

   private ArmorSlots() {
   }

   public static List<ItemStack> method5384(LivingEntity var0) {
      return List.of(
         var0.getItemBySlot(EquipmentSlot.FEET),
         var0.getItemBySlot(EquipmentSlot.LEGS),
         var0.getItemBySlot(EquipmentSlot.CHEST),
         var0.getItemBySlot(EquipmentSlot.HEAD)
      );
   }

   public static List<ItemStack> method5385(LivingEntity var0) {
      return List.of(var0.getItemBySlot(EquipmentSlot.MAINHAND), var0.getItemBySlot(EquipmentSlot.OFFHAND));
   }
}

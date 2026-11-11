package org.zenith.utility.game;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zenith.MinecraftAccess;

public final class ItemIntegrityTracker implements MinecraftAccess {
   private static Level trackedWorld;
   private static boolean invalidStackCountSeen;
   private static boolean invalidStackDataSeen;

   private ItemIntegrityTracker() {
   }

   public static void recordSuspiciousStack(Player player, ItemStack stack) {
      if (player != null && stack != null && !stack.isEmpty()) {
         if (mc.player != null && player != mc.player) {
            resetForWorldChange();
            if (stack.getCount() > stack.getItem().getDefaultMaxStackSize()) {
               invalidStackCountSeen = true;
               invalidStackDataSeen = true;
            }

            if (stack.has(DataComponents.DAMAGE) && !stack.isDamageableItem()) {
               invalidStackDataSeen = true;
            }
         }
      }
   }

   public static boolean isStackCountValid(Player player) {
      resetForWorldChange();
      return player == mc.player || !invalidStackCountSeen;
   }

   public static boolean isStackDamageValid(Player livingEntity) {
      resetForWorldChange();
      return livingEntity == mc.player || !invalidStackDataSeen;
   }

   public static boolean hasSuspiciousStack() {
      resetForWorldChange();
      return invalidStackDataSeen;
   }

   private static void resetForWorldChange() {
      if (mc.level != trackedWorld) {
         trackedWorld = mc.level;
         invalidStackCountSeen = false;
         invalidStackDataSeen = false;
      }
   }
}

package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.ItemStackTags;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "OffHandManager",
   category = ModuleCategory.COMBAT,
   description = "module.offhandmanager.description"
)
public final class OffHandManagerModule extends Module {
   public static final OffHandManagerModule INSTANCE = new OffHandManagerModule();
   private final ModeSetting swapHealMode = new ModeSetting(
      "module.offhand.swapHealMode",
      "module.offhand.swapHealMode.desc",
      "module.offhand.mode.mythic",
      "module.offhand.mode.legendary",
      "module.offhand.mode.talisman",
      "module.offhand.mode.none"
   );
   private final ModeSetting swapEnemyMode = new ModeSetting(
      "module.offhand.swapEnemyMode",
      "module.offhand.swapEnemyMode.desc",
      "module.offhand.mode.cerberus",
      "module.offhand.mode.mythic",
      "module.offhand.mode.legendary",
      "module.offhand.mode.talisman",
      "module.offhand.mode.none"
   );
   private final StopWatch stopWatch = new StopWatch();

   private OffHandManagerModule() {
   }

   public void reset() {
      this.stopWatch.reset();
   }

   @EventTarget
   public void method7216(MoveInputEvent event) {
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      if (this.method7218()) {
         Slot slot;
         if (!this.swapEnemyMode.isSelected(4) && livingEntity != null && this.method7221(livingEntity)) {
            slot = this.method7219(this.swapEnemyMode);
         } else if (mc.player.isUsingItem() && !this.swapHealMode.isSelected(3)) {
            slot = this.method7219(this.swapHealMode);
         } else {
            slot = null;
         }

         if (slot != null
            && !this.method7217(slot.getItem())
            && KeyBindUtils.method2630(AutoTotemModule.class)
            && KeyBindUtils.method2630(AutoSwapModule.class)
            && KeyBindUtils.method2630(OffHandManagerModule.class)) {
            KeyBindUtils.method2627(OffHandManagerModule.class, () -> {
               if (KeyBindUtils.method2630(AutoTotemModule.class)) {
                  InventoryUtils.method900(slot, InteractionHand.OFF_HAND, true);
               }
            });
         }
      }
   }

   private boolean method7217(ItemStack stack) {
      ItemStack offhandItem = mc.player.getOffhandItem();
      return stack == offhandItem || ItemStack.isSameItemSameComponents(stack, offhandItem);
   }

   private boolean method7218() {
      return this.stopWatch.hasElapsed(1000L);
   }

   private Slot method7219(ModeSetting swapEnemyMode) {
      try {
         if (swapEnemyMode.get().equals("module.offhand.mode.talisman")) {
            return InventoryUtils.method912(
               mc.player.inventoryMenu,
               Items.TOTEM_OF_UNDYING,
               Comparator.<Slot, Boolean>comparing(slot -> !slot.getItem().isEnchanted()).thenComparing((slot, var1x) -> {
                  CustomData var2x = slot.getItem().get(DataComponents.CUSTOM_DATA);
                  CustomData var3x = var1x.getItem().get(DataComponents.CUSTOM_DATA);
                  if (var2x == null && var3x != null) {
                     return -1;
                  } else if (var2x != null && var3x == null) {
                     return 1;
                  } else if (var2x == null) {
                     return 0;
                  } else {
                     boolean contains = var2x.copyTag().contains("sphereEffect");
                     boolean flag = var3x.copyTag().contains("sphereEffect");
                     if (contains && !flag) {
                        return 1;
                     } else if (!contains && flag) {
                        return -1;
                     } else if (!contains) {
                        return 0;
                     } else {
                        String string = var2x.copyTag().get("sphereEffect").toString();
                        String str = var3x.copyTag().get("sphereEffect").toString();
                        return Integer.compare(str.length(), string.length());
                     }
                  }
               }).thenComparingInt(slot -> slot.index).reversed(),
               slot -> true
            );
         } else {
            String str = this.method7220(swapEnemyMode.get());
            return InventoryUtils.method907(Items.PLAYER_HEAD, var1x -> {
               if (!var1x.getItem().isEmpty()) {
                  CustomData var2x = var1x.getItem().get(DataComponents.CUSTOM_DATA);
                  if (var2x != null && var2x.copyTag().contains("SkullOwner") && var2x.copyTag().get("SkullOwner").toString().contains(str)) {
                     return true;
                  }
               }

               return false;
            });
         }
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   private String method7220(String str) {
      return switch (str) {
         case "module.offhand.mode.cerberus" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0=";
         case "module.offhand.mode.mythic" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFmZjJlYjQ5OGU1YzZhMDQ0ODRmMGM5Zjc4NWI0NDg0NzlhYjIxM2RmOTVlYzkxMTc2YTMwOGExMmFkZDcwIn19fQ==";
         case "module.offhand.mode.legendary" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19";
         default -> throw new RuntimeException("Unknown key: " + str);
      };
   }

   private boolean method7221(LivingEntity livingEntity) {
      if (livingEntity.isUsingItem()) {
         return true;
      } else {
         return ItemStackTags.isSword(livingEntity.getMainHandItem())
            ? false
            : livingEntity.getOffhandItem().getItem() != Items.PLAYER_HEAD || this.method7222(livingEntity.getOffhandItem());
      }
   }

   private boolean method7222(ItemStack offhandItem) {
      CustomData customData = offhandItem.get(DataComponents.CUSTOM_DATA);
      return customData != null
         && customData.copyTag().contains("SkullOwner")
         && customData.copyTag()
            .get("SkullOwner")
            .toString()
            .contains(
               "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
            );
   }
}

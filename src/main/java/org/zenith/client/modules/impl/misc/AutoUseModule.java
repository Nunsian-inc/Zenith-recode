package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.StopUsingItemEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoUse",
   category = ModuleCategory.MISC,
   description = "module.autouse.description"
)
public final class AutoUseModule extends Module {
   public static final AutoUseModule INSTANCE = new AutoUseModule();
   private final MultiSelectSetting mode = MultiSelectSetting.method5224(
      "module.autoUse.mode", "", List.of("module.autoUse.invisibility", "module.autoUse.food")
   );
   private int intValue = -1;
   private int intValue2 = -1;
   private boolean usingItem;

   private AutoUseModule() {
   }

   @Override
   public void onDisable() {
      if (this.usingItem && mc.player != null && mc.gameMode != null && mc.player.isUsingItem()) {
         mc.gameMode.releaseUsingItem(mc.player);
      }

      this.method7841();
      super.onDisable();
   }

   @EventTarget(0)
   public void onUpdate(UpdateEvent event) {
      if (mc.player == null || mc.level == null || mc.gameMode == null) {
         this.method7842();
      } else if (this.usingItem) {
         if (!mc.player.isUsingItem()) {
            this.method7841();
         }
      } else if (!mc.player.isUsingItem()) {
         if (!this.mode.method5227(0) || mc.player.isInvisible() || !this.method7840(this::method7843)) {
            if (this.mode.method5227(1) && mc.player.getFoodData().getFoodLevel() <= 7) {
               this.method7840(stack -> stack.getUseAnimation() == ItemUseAnimation.EAT);
            }
         }
      }
   }

   @EventTarget(0)
   public void method7839(StopUsingItemEvent event) {
      if (this.usingItem && mc.player != null && mc.player.isUsingItem()) {
         event.cancel();
      }
   }

   private boolean method7840(Predicate<ItemStack> predicate) {
      if (mc.player == null) {
         return false;
      } else {
         Inventory inventory = mc.player.getInventory();
         int selected = InventoryUtils.method922(var2x -> predicate.test(inventory.getItem(var2x)));
         this.intValue = inventory.getSelectedSlot();
         if (selected != -1) {
            inventory.setSelectedSlot(selected);
         } else {
            Slot slot = InventoryUtils.method910(
               var1x -> var1x.container instanceof Inventory && var1x.getContainerSlot() >= 9 && var1x.getContainerSlot() < 36 && predicate.test(var1x.getItem())
            );
            if (slot == null) {
               this.method7842();
               return false;
            }

            this.intValue2 = slot.getContainerSlot();
            InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, false);
         }

         this.usingItem = true;
         UseItemUtils.useItem(InteractionHand.MAIN_HAND);
         if (!mc.player.isUsingItem()) {
            this.method7841();
         }

         return true;
      }
   }

   private void method7841() {
      if (mc.player != null && this.intValue != -1) {
         if (this.intValue2 != -1) {
            Slot slot = InventoryUtils.method910(var1x -> var1x.container instanceof Inventory && var1x.getContainerSlot() == this.intValue2);
            if (slot != null) {
               InventoryUtils.method901(slot, this.intValue, true);
            }
         }

         mc.player.getInventory().setSelectedSlot(this.intValue);
      }

      this.method7842();
   }

   private void method7842() {
      this.intValue = -1;
      this.intValue2 = -1;
      this.usingItem = false;
   }

   private boolean method7843(ItemStack stack) {
      if (!stack.is(Items.POTION) && !stack.is(Items.SPLASH_POTION) && !stack.is(Items.LINGERING_POTION)) {
         return false;
      } else {
         PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
         if (potionContents == null) {
            return false;
         } else if (potionContents.potion().isPresent() && potionContents.potion().get().value().equals(Potions.INVISIBILITY)) {
            return true;
         } else {
            for (MobEffectInstance allEffect : potionContents.getAllEffects()) {
               if (allEffect.getEffect().equals(MobEffects.INVISIBILITY)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}

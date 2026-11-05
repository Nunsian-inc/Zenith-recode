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
   public void onUpdate(UpdateEvent var1) {
      if (mc.player == null || mc.level == null || mc.gameMode == null) {
         this.method7842();
      } else if (this.usingItem) {
         if (!mc.player.isUsingItem()) {
            this.method7841();
         }
      } else if (!mc.player.isUsingItem()) {
         if (!this.mode.method5227(0) || mc.player.isInvisible() || !this.method7840(this::method7843)) {
            if (this.mode.method5227(1) && mc.player.getFoodData().getFoodLevel() <= 7) {
               this.method7840(var0 -> var0.getUseAnimation() == ItemUseAnimation.EAT);
            }
         }
      }
   }

   @EventTarget(0)
   public void method7839(StopUsingItemEvent var1) {
      if (this.usingItem && mc.player != null && mc.player.isUsingItem()) {
         var1.cancel();
      }
   }

   private boolean method7840(Predicate<ItemStack> var1) {
      if (mc.player == null) {
         return false;
      } else {
         Inventory var2 = mc.player.getInventory();
         int var3 = InventoryUtils.method922(var2x -> var1.test(var2.getItem(var2x)));
         this.intValue = var2.getSelectedSlot();
         if (var3 != -1) {
            var2.setSelectedSlot(var3);
         } else {
            Slot var4 = InventoryUtils.method910(
               var1x -> var1x.container instanceof Inventory && var1x.getContainerSlot() >= 9 && var1x.getContainerSlot() < 36 && var1.test(var1x.getItem())
            );
            if (var4 == null) {
               this.method7842();
               return false;
            }

            this.intValue2 = var4.getContainerSlot();
            InventoryUtils.method900(var4, InteractionHand.MAIN_HAND, false);
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
            Slot var1 = InventoryUtils.method910(var1x -> var1x.container instanceof Inventory && var1x.getContainerSlot() == this.intValue2);
            if (var1 != null) {
               InventoryUtils.method901(var1, this.intValue, true);
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

   private boolean method7843(ItemStack var1) {
      if (!var1.is(Items.POTION) && !var1.is(Items.SPLASH_POTION) && !var1.is(Items.LINGERING_POTION)) {
         return false;
      } else {
         PotionContents var2 = var1.get(DataComponents.POTION_CONTENTS);
         if (var2 == null) {
            return false;
         } else if (var2.potion().isPresent() && var2.potion().get().value().equals(Potions.INVISIBILITY)) {
            return true;
         } else {
            for (MobEffectInstance var4 : var2.getAllEffects()) {
               if (var4.getEffect().equals(MobEffects.INVISIBILITY)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}

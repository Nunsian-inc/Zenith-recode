package org.zenith.client.modules.impl.render;

import net.minecraft.world.item.ItemStack;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.ItemIntegrityTracker;
import org.zenith.utility.misc.ItemStackTags;

@ModuleInfo(
   name = "View Armor Durability",
   category = ModuleCategory.RENDER,
   description = "module.viewarmordurability.description"
)
public class ViewArmorDurabilityModule extends Module {
   public static final ViewArmorDurabilityModule INSTANCE = new ViewArmorDurabilityModule();
   private final BooleanSetting naSebe = new BooleanSetting("module.viewArmorDurability.naSebe", true);
   private final BooleanSetting naEnemies = new BooleanSetting("module.viewArmorDurability.naEnemies", true);

   private ViewArmorDurabilityModule() {
   }

   public boolean method7550(ItemStack renderStack, boolean renderSelf) {
      if (renderStack == null || renderStack.isEmpty()) {
         return false;
      } else if (!ItemStackTags.isArmor(renderStack)) {
         return false;
      } else if (renderSelf && !this.naSebe.isEnabled()) {
         return false;
      } else {
         return !renderSelf && ItemIntegrityTracker.hasSuspiciousStack() ? false : renderSelf || this.naEnemies.isEnabled();
      }
   }

   public int method7551(ItemStack renderStack) {
      return renderStack.getBarColor();
   }
}

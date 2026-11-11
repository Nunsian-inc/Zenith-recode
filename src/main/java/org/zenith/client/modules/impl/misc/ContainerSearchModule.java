package org.zenith.client.modules.impl.misc;

import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;

@ModuleInfo(name = "ContainerSearch", description = "module.containerSearch.description", category = ModuleCategory.MISC)
public final class ContainerSearchModule extends Module {
   public static final ContainerSearchModule INSTANCE = new ContainerSearchModule();
   private final ColorSetting highlight = new ColorSetting(
      "module.containerSearch.highlight", "module.containerSearch.highlight.desc", new ZenithColor(70, 180, 255, 96)
   );
   private final BooleanSetting dimUnmatched = new BooleanSetting(
      "module.containerSearch.dimUnmatched", "module.containerSearch.dimUnmatched.desc", false
   );
   private final BooleanSetting caseSensitive = new BooleanSetting(
      "module.containerSearch.caseSensitive", "module.containerSearch.caseSensitive.desc", false
   );

   private ContainerSearchModule() {
   }

   public boolean supports(AbstractContainerMenu menu) {
      return mc.player != null && menu == mc.player.containerMenu && menu != mc.player.inventoryMenu;
   }

   public EditBox createSearchBox(int left, int top, int imageWidth, int imageHeight, int screenWidth, int screenHeight, String query) {
      int width = Math.min(imageWidth, Math.max(20, screenWidth - 8));
      int x = Math.max(4, Math.min(left, screenWidth - width - 4));
      int y = top + imageHeight + 4;
      if (y + 20 > screenHeight - 4) {
         y = Math.max(4, top - 24);
      }
      Component label = Component.literal(Zenith.getInstance().getLocalization().translate("module.containerSearch.search"));
      EditBox box = new EditBox(mc.font, x, y, width, 20, label);
      box.setHint(label);
      box.setMaxLength(128);
      box.setValue(query);
      this.updateSearchBox(box);
      return box;
   }

   public void updateSearchBox(EditBox box) {
      if (box == null) {
         return;
      }
      box.visible = this.isEnabled();
      box.active = this.isEnabled();
      if (!this.isEnabled()) {
         box.setFocused(false);
      }
   }

   public void renderSlot(GuiGraphicsExtractor graphics, AbstractContainerMenu menu, Slot slot, EditBox box) {
      if (!this.isEnabled() || box == null || !this.supports(menu) || !slot.isActive() || slot.isFake()
         || slot.container == mc.player.getInventory() || !slot.hasItem()) {
         return;
      }
      String query = box.getValue().strip();
      if (query.isEmpty()) {
         return;
      }
      String name = slot.getItem().getHoverName().getString();
      String id = BuiltInRegistries.ITEM.getKey(slot.getItem().getItem()).toString();
      if (!this.caseSensitive.isEnabled()) {
         query = query.toLowerCase(Locale.ROOT);
         name = name.toLowerCase(Locale.ROOT);
         id = id.toLowerCase(Locale.ROOT);
      }
      // Координаты уже относительны контейнеру; рамка остаётся под тултипом и анимацией экрана.
      if (name.contains(query) || id.contains(query)) {
         int color = this.highlight.method1445();
         graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color);
         graphics.outline(slot.x - 1, slot.y - 1, 18, 18, color | 0xFF000000);
      } else if (this.dimUnmatched.isEnabled()) {
         graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x88000000);
      }
   }
}

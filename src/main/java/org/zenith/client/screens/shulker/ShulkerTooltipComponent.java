package org.zenith.client.screens.shulker;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class ShulkerTooltipComponent implements ClientTooltipComponent {
   private final NonNullList<ItemStack> items;
   private static final Identifier shulkerGuiTexture = Identifier.fromNamespaceAndPath("zenith", "textures/container.png");
   private static final int guiWidth = 176;
   private static final int guiHeight = 67;
   private static final int slotOffsetX = 8;
   private static final int slotOffsetY = 7;

   public ShulkerTooltipComponent(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   @Override
   public int getHeight(Font textRenderer) {
      return 67;
   }

   @Override
   public int getWidth(Font textRenderer) {
      return 176;
   }

   @Override
   public void extractText(GuiGraphicsExtractor context, Font textRenderer, int x, int y) {
   }

   @Override
   public void extractImage(Font textRenderer, int x, int y, int width, int height, GuiGraphicsExtractor context) {
      context.blit(RenderPipelines.GUI_TEXTURED, shulkerGuiTexture, x, y, 0.0F, 0.0F, 176, 67, 176, 67);

      for (int var7 = 0; var7 < 27; var7++) {
         ItemStack var8 = this.items.get(var7);
         if (!var8.isEmpty()) {
            int var9 = x + 8 + var7 % 9 * 18;
            int var10 = y + 7 + var7 / 9 * 18;
            context.item(var8, var9, var10);
            context.itemDecorations(textRenderer, var8, var9, var10);
         }
      }
   }
}

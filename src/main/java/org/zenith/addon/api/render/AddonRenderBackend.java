package org.zenith.addon.api.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface AddonRenderBackend {
   default void prepareBlur(GuiGraphicsExtractor context, float strength) {
   }

   default void blur(GuiGraphicsExtractor context, float x, float y, float width, float height, float radius, int argb) {
      this.roundedRect(context, x, y, width, height, radius, argb);
   }

   void rect(GuiGraphicsExtractor var1, float var2, float var3, float var4, float var5, int var6);

   void roundedRect(GuiGraphicsExtractor var1, float var2, float var3, float var4, float var5, float var6, int var7);

   void text(GuiGraphicsExtractor var1, String var2, float var3, float var4, float var5, int var6);

   default void text(GuiGraphicsExtractor context, AddonFont font, String text, float x, float y, float size, int argb) {
      this.text(context, text, x, y, size, argb);
   }

   float textWidth(String var1, float var2);

   default float textWidth(AddonFont font, String text, float size) {
      return this.textWidth(text, size);
   }

   float textHeight(float var1);

   default float textHeight(AddonFont font, float size) {
      return this.textHeight(size);
   }
}

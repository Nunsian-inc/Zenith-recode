package org.zenith.addon.api.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface AddonRenderBackend {
   default void prepareBlur(GuiGraphicsExtractor context, float strength) {
   }

   default void blur(GuiGraphicsExtractor context, float x, float y, float width, float height, float radius, int argb) {
      this.roundedRect(context, x, y, width, height, radius, argb);
   }

   void rect(GuiGraphicsExtractor nonNull, float x, float y, float width, float height, int argb);

   void roundedRect(GuiGraphicsExtractor nonNull, float x, float y, float width, float height, float radius, int argb);

   void text(GuiGraphicsExtractor nonNull, String nonNullElse, float x, float y, float size, int argb);

   default void text(GuiGraphicsExtractor context, AddonFont font, String text, float x, float y, float size, int argb) {
      this.text(context, text, x, y, size, argb);
   }

   float textWidth(String nonNullElse, float size);

   default float textWidth(AddonFont font, String text, float size) {
      return this.textWidth(text, size);
   }

   float textHeight(float size);

   default float textHeight(AddonFont font, float size) {
      return this.textHeight(size);
   }
}

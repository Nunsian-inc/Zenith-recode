package org.zenith.addon.api.render;

public interface AddonRenderContext {
   int width();

   int height();

   float tickDelta();

   void rect(float x, float y, float width, float height, int argb);

   void roundedRect(float x, float y, float width, float height, float radius, int argb);

   void text(String text, float x, float y, float size, int argb);

   float textWidth(String text, float size);

   float textHeight(float size);

   void pushClip(float x, float y, float width, float height);

   void popClip();

   default void centeredText(String text, float centerX, float y, float size, int argb) {
      this.text(text, centerX - this.textWidth(text, size) / 2.0F, y, size, argb);
   }
}

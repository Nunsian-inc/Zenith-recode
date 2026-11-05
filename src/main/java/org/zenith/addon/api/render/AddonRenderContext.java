package org.zenith.addon.api.render;

public interface AddonRenderContext {
   int width();

   int height();

   float tickDelta();

   void rect(float f, float f2, float f3, float f4, int n);

   void roundedRect(float f, float f2, float f3, float f4, float f5, int n);

   void text(String text, float f, float y, float size, int argb);

   float textWidth(String text, float size);

   float textHeight(float f);

   void pushClip(float f, float f2, float f3, float f4);

   void popClip();

   default void centeredText(String text, float centerX, float y, float size, int argb) {
      this.text(text, centerX - this.textWidth(text, size) / 2.0F, y, size, argb);
   }
}

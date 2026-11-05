package org.zenith.addon.api.render;

public interface AddonRenderContext {
   int width();

   int height();

   float tickDelta();

   void rect(float var1, float var2, float var3, float var4, int var5);

   void roundedRect(float var1, float var2, float var3, float var4, float var5, int var6);

   void text(String var1, float var2, float var3, float var4, int var5);

   float textWidth(String var1, float var2);

   float textHeight(float var1);

   void pushClip(float var1, float var2, float var3, float var4);

   void popClip();

   default void centeredText(String text, float centerX, float y, float size, int argb) {
      this.text(text, centerX - this.textWidth(text, size) / 2.0F, y, size, argb);
   }
}

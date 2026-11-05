package org.zenith.client.screens.nlgui.elements;

import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class HoverScrollText {
   private static final float FADE_WIDTH = 10.0F;
   private static final long START_PAUSE = 300L;
   private static final long END_PAUSE = 900L;
   private String text;
   private float availableWidth;
   private float textWidth;
   private float offset;
   private int direction = 1;
   private long lastUpdate;
   private long pauseUntil;
   private boolean scrolling;

   public float render(MenuDrawContext menuDrawContext, Font font, String name, float floatValue, float centeredTextY, float labelLimit, ZenithColor zenithColor, boolean contains) {
      float width = font.width(name);
      float update = this.update(name, width, labelLimit, contains, System.currentTimeMillis());
      if (labelLimit <= 0.0F) {
         return 0.0F;
      } else if (width <= labelLimit) {
         menuDrawContext.drawText(font, name, floatValue, centeredTextY, zenithColor);
         return width;
      } else {
         float f = Math.min(10.0F, labelLimit / 2.0F);
         float maxWidth = labelLimit + update;
         float f2 = (labelLimit - f + update) / maxWidth;
         if (update > 0.0F) {
            menuDrawContext.enableScissor(floatValue, centeredTextY - 1.0F, floatValue + labelLimit, centeredTextY + font.height() + 4.0F);
         }

         try {
            menuDrawContext.drawText(font, name, floatValue - update, centeredTextY, zenithColor, true, f2, 1.0F, maxWidth);
         } finally {
            if (update > 0.0F) {
               menuDrawContext.disableScissor();
            }
         }

         return labelLimit;
      }
   }

   public float update(String text, float textWidth, float availableWidth, boolean scrolling, long lastUpdate) {
      if (!text.equals(this.text) || textWidth != this.textWidth || availableWidth != this.availableWidth || lastUpdate < this.lastUpdate || lastUpdate - this.lastUpdate > 250L) {
         this.text = text;
         this.textWidth = textWidth;
         this.availableWidth = availableWidth;
         this.offset = 0.0F;
         this.direction = 1;
         this.scrolling = false;
         this.pauseUntil = 0L;
         this.lastUpdate = lastUpdate;
      }

      long l = this.lastUpdate;
      this.lastUpdate = lastUpdate;
      if (!(availableWidth <= 0.0F) && !(textWidth <= availableWidth)) {
         if (!this.scrolling) {
            if (!scrolling) {
               return 0.0F;
            }

            this.scrolling = true;
            this.pauseUntil = lastUpdate + 300L;
         }

         if (lastUpdate <= this.pauseUntil) {
            return this.offset;
         } else {
            float f = (float)Math.max(0L, lastUpdate - Math.max(l, this.pauseUntil)) / 1000.0F;
            float f2 = Math.max(28.0F, Math.min(65.0F, textWidth / Math.max(1, text.length()) * 12.0F));
            float offset = textWidth - availableWidth + Math.min(10.0F, availableWidth / 2.0F);
            this.offset = this.offset + this.direction * f2 * f;
            if (this.offset >= offset) {
               this.offset = offset;
               this.direction = -1;
               this.pauseUntil = lastUpdate + 900L;
            } else if (this.offset <= 0.0F) {
               this.offset = 0.0F;
               this.direction = 1;
               this.pauseUntil = lastUpdate + 300L;
               this.scrolling = scrolling;
            }

            return this.offset;
         }
      } else {
         this.offset = 0.0F;
         this.direction = 1;
         this.scrolling = false;
         this.pauseUntil = 0L;
         return 0.0F;
      }
   }
}

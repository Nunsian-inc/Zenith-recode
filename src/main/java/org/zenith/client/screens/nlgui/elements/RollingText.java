package org.zenith.client.screens.nlgui.elements;

import net.minecraft.util.Mth;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class RollingText {
   private static final long DURATION_MS = 230L;
   private static final long STAGGER_MS = 22L;
   private static final float DISTANCE = 3.5F;
   private String current;
   private String previous;
   private long changedAt = -1L;
   private int direction = 1;

   public void snap(String current) {
      this.current = current;
      this.previous = null;
      this.changedAt = -1L;
   }

   public float draw(MenuDrawContext menuDrawContext, Font font, String current, float f, float centeredTextY, ZenithColor zenithColor, int counterDirection) {
      if (current == null) {
         current = "";
      }

      long now = System.currentTimeMillis();
      if (this.current == null) {
         this.current = current;
      } else if (!this.current.equals(current)) {
         this.previous = this.current;
         this.current = current;
         this.changedAt = now;
         this.direction = counterDirection >= 0 ? 1 : -1;
      }

      float width = font.width(this.current);
      if (this.previous == null) {
         menuDrawContext.drawText(font, this.current, f - width, centeredTextY, zenithColor);
         return width;
      } else {
         int length = this.current.length();
         int n = this.previous.length();
         int n2 = 0;

         while (n2 < length && n2 < n && this.current.charAt(length - 1 - n2) == this.previous.charAt(n - 1 - n2)) {
            n2++;
         }

         long l = now - this.changedAt;
         int n3 = Math.max(length, n) - n2;
         if (l >= 230L + 22L * Math.max(0, n3 - 1)) {
            this.previous = null;
            this.changedAt = -1L;
            menuDrawContext.drawText(font, this.current, f - width, centeredTextY, zenithColor);
            return width;
         } else {
            float f2 = zenithColor.getIntValue4() / 255.0F;
            float floatValue = f;

            for (int i = 0; i < length; i++) {
               String text = String.valueOf(this.current.charAt(length - 1 - i));
               floatValue -= font.width(text);
               if (i < n2) {
                  menuDrawContext.drawText(font, text, floatValue, centeredTextY, zenithColor);
               } else {
                  float progress2 = progress(l - (i - n2) * 22L);
                  if (!(progress2 <= 0.0F)) {
                     float easeOut2 = easeOut(progress2);
                     menuDrawContext.drawText(font, text, floatValue, centeredTextY + this.direction * 3.5F * (1.0F - easeOut2), zenithColor.method7292(f2 * progress2));
                  }
               }
            }

            floatValue = f;

            for (int i = 0; i < n; i++) {
               String text = String.valueOf(this.previous.charAt(n - 1 - i));
               floatValue -= font.width(text);
               if (i >= n2) {
                  float progress2 = progress(l - (i - n2) * 22L);
                  if (!(progress2 >= 1.0F)) {
                     float easeOut2 = easeOut(progress2);
                     menuDrawContext.drawText(font, text, floatValue, centeredTextY - this.direction * 3.5F * easeOut2, zenithColor.method7292(f2 * (1.0F - progress2)));
                  }
               }
            }

            return width;
         }
      }
   }

   public boolean isRolling() {
      return this.previous != null;
   }

   private static float progress(long l) {
      return Mth.clamp((float)l / 230.0F, 0.0F, 1.0F);
   }

   private static float easeOut(float f) {
      float f2 = 1.0F - f;
      return 1.0F - f2 * f2 * f2;
   }
}

package org.zenith.client.screens.nlgui.elements;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ChipStrip {
   public static final float CHIP_HEIGHT = 11.0F;
   private static final float GAP = 3.0F;
   private static final float PAD = 5.0F;
   private static final float FONT_SIZE = 4.6F;
   private final String[] labels;
   private final BoundsData[] bounds;
   private final Animation[] hot;
   private int hovered = -1;
   private final Animation plateX = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation plateY = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private int lastSelected = Integer.MIN_VALUE;
   private long pulseAt;

   public ChipStrip(String... labels) {
      this.labels = labels;
      this.bounds = new BoundsData[labels.length];
      this.hot = new Animation[labels.length];

      for (int i = 0; i < labels.length; i++) {
         this.hot[i] = new Animation(130L, 0.0F, Easing.INSTANCE12);
      }
   }

   private static String t(String label) {
      return Zenith.getInstance().getLocalization().translate(label);
   }

   private String text(int hovered) {
      return t(this.labels[hovered]);
   }

   private static Font font() {
      return Fonts.NEW_MEDIUM.getFont(4.6F);
   }

   public float width() {
      Font font2 = font();
      float f = 0.0F;

      for (int i = 0; i < this.labels.length; i++) {
         f += font2.width(this.text(i)) + 10.0F + (i == 0 ? 0.0F : 3.0F);
      }

      return f;
   }

   public float height(float width) {
      return this.layout(0.0F, 0.0F, width);
   }

   private float layout(float floatValue, float y, float width) {
      Font font2 = font();
      float f = floatValue;
      float y2 = y;

      for (int i = 0; i < this.labels.length; i++) {
         float width2 = font2.width(this.text(i)) + 10.0F;
         if (f > floatValue && f + width2 > floatValue + width + 0.01F) {
            f = floatValue;
            y2 += 14.0F;
         }

         this.bounds[i] = new BoundsData(f, y2, width2, 11.0F);
         f += width2 + 3.0F;
      }

      return y2 - y + 11.0F;
   }

   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float width, int lastSelected, float previewAlpha, ZenithStyle zenithStyle) {
      this.layout(floatValue, y, width);
      this.hovered = -1;

      for (int i = 0; i < this.labels.length; i++) {
         if (this.bounds[i].contains(lastClickX, lastClickY)) {
            this.hovered = i;
         }
      }

      if (lastSelected >= 0 && lastSelected < this.labels.length) {
         BoundsData bound = this.bounds[lastSelected];
         float f = bound.getFloatValue() - floatValue;
         float floatValue2 = bound.getFloatValue2() - y;
         if (!this.plateReady) {
            this.plateX.snapTo(f);
            this.plateY.snapTo(floatValue2);
            this.plateWidth.snapTo(bound.width());
            this.plateReady = true;
         }

         if (this.lastSelected != lastSelected) {
            if (this.lastSelected != Integer.MIN_VALUE) {
               this.pulseAt = System.currentTimeMillis();
            }

            this.lastSelected = lastSelected;
         }

         float floatValue3 = floatValue + this.plateX.animateTo(f);
         float y2 = y + this.plateY.animateTo(floatValue2);
         float width2 = this.plateWidth.animateTo(bound.width());
         float f2 = this.pulseAt == 0L ? 0.0F : Math.max(0.0F, 1.0F - (float)(System.currentTimeMillis() - this.pulseAt) / 380.0F);
         f2 *= f2;
         float f3 = (float)Math.sin(f2 * Math.PI) * 1.2F;
         menuDrawContext.drawRoundedRect(
            floatValue3 - f3,
            y2 - f3 * 0.5F,
            width2 + f3 * 2.0F,
            11.0F + f3,
            ProxyDotRadiusData.method8069(5.5F + f3 * 0.5F),
            zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha)
         );
         if (f2 > 0.01F) {
            menuDrawContext.drawRoundedRect(
               floatValue3,
               y2,
               width2,
               11.0F,
               ProxyDotRadiusData.method8069(5.5F),
               zenithStyle.getPrimaryColor().getZenithColor().method7292(0.22F * f2).method7294(previewAlpha)
            );
         }
      }

      Font font2 = font();

      for (int i = 0; i < this.labels.length; i++) {
         BoundsData bound = this.bounds[i];
         boolean flag = i == lastSelected;
         float hover = this.hot[i].animateTo(i == this.hovered && !flag ? 1.0F : 0.0F);
         if (hover > 0.01F) {
            menuDrawContext.drawRoundedRect(
               bound.getFloatValue(),
               bound.getFloatValue2(),
               bound.width(),
               11.0F,
               ProxyDotRadiusData.method8069(5.5F),
               zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * 0.45F * hover)
            );
         }

         ZenithColor zenithColor = flag
            ? zenithStyle.getTextEnable().getZenithColor()
            : zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getTextSecondary().getZenithColor(), hover);
         menuDrawContext.drawText(font2, this.text(i), bound.getFloatValue() + 5.0F, bound.getFloatValue2() + (11.0F - font2.height()) / 2.0F, zenithColor.method7294(previewAlpha));
      }
   }

   public int hovered() {
      return this.hovered;
   }

   public String label(int hovered) {
      return hovered >= 0 && hovered < this.labels.length ? this.text(hovered) : "";
   }
}

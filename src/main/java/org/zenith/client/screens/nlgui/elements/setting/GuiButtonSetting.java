package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiButtonSetting extends GuiSetting<ButtonSetting> {
   private final Animation animationEnable = new Animation(200L, Easing.INSTANCE10);
   private final Animation animationClick = new Animation(350L, Easing.INSTANCE15);
   private boolean clickActive;
   private BoundsData bounds;

   public GuiButtonSetting(ButtonSetting setting) {
      super(166.0F, setting);
   }

   public GuiButtonSetting(ButtonSetting buttonSetting, float width) {
      super(width, buttonSetting);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double anchorMouseX, double lastClickY, Enum3 enum2) {
      if (this.bounds != null && this.bounds.contains(anchorMouseX, lastClickY)) {
         this.setting.toggle();
         this.clickActive = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float centeredTextY, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         this.animationClick.method2807(this.clickActive);
         if (this.clickActive && this.animationClick.getValue() > 0.9F) {
            this.clickActive = false;
         }

         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(
            menuDrawContext, x, y, this.setting.getIcon(), this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, centeredTextY, maxWidth, zenithColor, zenithColor2, zenithColor3
         );
         if (!this.isShort()) {
            float width = 6.0F;
            float height = 6.0F;
            this.bounds = new BoundsData(floatValue + this.width - width, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
            menuDrawContext.drawRoundedRect(
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               width,
               height,
               ProxyDotRadiusData.method8069(1.0F),
               currentStyle.getFieldSurfaceBackground()
                  .getZenithColor()
                  .method7294(2.0F)
                  .method7295(currentStyle.getPrimaryColor().getZenithColor(), this.animationEnable.getValue())
                  .method7294(previewAlpha)
            );
            menuDrawContext.drawRoundedBorder(
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               width,
               height,
               -0.5F,
               ProxyDotRadiusData.method8069(1.0F),
               currentStyle.getFieldBorder().getZenithColor().method7295(ZenithColor.INSTANCE8, this.animationEnable.getValue()).method7294(previewAlpha)
            );
            Font font3 = Fonts.NEW_ICONS.getFont(4.0F);
            menuDrawContext.drawText(
               font3,
               "<",
               this.bounds.getFloatValue() + 1.5F - 0.8F,
               this.bounds.getFloatValue2() + (height - font3.height()) / 2.0F,
               ZenithColor.INSTANCE8.method7295(currentStyle.getPrimaryColor().getZenithColor(), this.animationClick.getValue()).method7294(previewAlpha)
            );
         } else {
            Font font3 = Fonts.NEW_ICONS.getFont(5.5F);
            float width = font3.width("H");
            float height = font3.height();
            float f = floatValue + this.width - width;
            this.bounds = new BoundsData(f, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
            menuDrawContext.drawText(
               font3,
               "H",
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getPrimaryColor().getZenithColor(), this.animationClick.getValue()).method7294(previewAlpha)
            );
         }
      }
   }
}

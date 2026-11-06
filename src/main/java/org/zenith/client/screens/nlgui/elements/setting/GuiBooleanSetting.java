package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiBooleanSetting extends GuiSetting<BooleanSetting> {
   private final Animation animationEnable = new Animation(200L, Easing.INSTANCE10);
   private BoundsData bounds;

   public GuiBooleanSetting(BooleanSetting setting) {
      super(166.0F, setting);
   }

   public GuiBooleanSetting(BooleanSetting booleanSetting, float width) {
      super(width, booleanSetting);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double anchorMouseX, double lastClickY, MouseButton enum2) {
      if (this.bounds != null && this.bounds.contains(anchorMouseX, lastClickY)) {
         this.setting.toggle();
         Zenith.getInstance()
            .getClientSounds()
            .method1561(this.setting.isEnabled() ? Zenith.getInstance().getClientSounds().soundEvent5 : Zenith.getInstance().getClientSounds().soundEvent6);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float centeredTextY, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.animationVisible.updateVisibility(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(menuDrawContext, x, y, "t", this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, centeredTextY, maxWidth, zenithColor, zenithColor2, zenithColor3);
         this.animationEnable.updateVisibility(this.setting.isEnabled());
         if (!this.isShort()) {
            float width = 6.0F;
            float height = 6.0F;
            this.bounds = new BoundsData(floatValue + this.width - width, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
            menuDrawContext.drawRoundedRectBatched(
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
            menuDrawContext.flushRoundedRects();
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
               ZenithColor.INSTANCE8.method7295(currentStyle.getTextEnable().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
            );
         } else {
            float width = 12.0F;
            float height = 7.0F;
            float f = floatValue + this.width - width;
            this.bounds = new BoundsData(f, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
            menuDrawContext.drawRoundedRectBatched(
               f,
               this.bounds.getFloatValue2(),
               width,
               height,
               ProxyDotRadiusData.method8069(2.5F),
               currentStyle.getDisableActiveBg().getZenithColor().method7295(currentStyle.getPrimaryColor().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
            );
            float f2 = Mth.lerp(this.animationEnable.getValue(), 1.0F, width - 1.0F - 5.0F);
            menuDrawContext.drawRoundedRectBatched(
               f + f2,
               this.bounds.getFloatValue2() + 1.0F,
               5.0F,
               5.0F,
               ProxyDotRadiusData.method8069(1.5F),
               currentStyle.getTextTertiary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
            );
            menuDrawContext.flushRoundedRects();
         }
      }
   }
}

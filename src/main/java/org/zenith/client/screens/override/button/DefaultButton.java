package org.zenith.client.screens.override.button;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.MainMenuBlurRenderer;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class DefaultButton extends ButtonScreen {
   private final Animation hoverAnimation = new Animation(300L, Easing.INSTANCE10);
   private final String name;
   private final String icon;
   private final Runnable onClick;
   private final MainMenuBlurRenderer blurRenderer;

   public DefaultButton(String name, String icon, float width, float height, Runnable onClick, MainMenuBlurRenderer blurRenderer) {
      super(width, height);
      this.name = name;
      this.icon = icon;
      this.onClick = onClick;
      this.blurRenderer = blurRenderer;
   }

   @Override
   public void render(MenuDrawContext customDrawContext, float x, float y, float floatValue, float f) {
      super.render(customDrawContext, x, y, floatValue, f);
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.hoverAnimation.updateVisibility(this.bounds.contains(x, y));
      ShapeRenderer.method7167(
         MatrixUtils.method663(customDrawContext),
         floatValue,
         f,
         this.getWidth(),
         this.getHeight(),
         10,
         ProxyDotRadiusData.method8069(4.0F),
         GlowColor.method8097(ZenithColor.INSTANCE8.method7295(ZenithColor.INSTANCE.method7294(0.2F), this.hoverAnimation.getValue()))
      );
      this.blurRenderer
         .render(
            MatrixUtils.method664(customDrawContext.pose()), floatValue, f, this.getWidth(), this.getHeight(), ProxyDotRadiusData.method8069(4.0F), ZenithColor.INSTANCE
         );
      customDrawContext.drawRoundedRect(
         floatValue, f, this.getWidth(), this.getHeight(), ProxyDotRadiusData.method8069(4.0F), currentStyle.getFieldSurfaceBackground().getZenithColor()
      );
      customDrawContext.drawRoundedBorder(floatValue, f, this.getWidth(), this.getHeight(), -0.1F, ProxyDotRadiusData.method8069(4.0F), currentStyle.getFieldBorder().getZenithColor());
      Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font font2 = Fonts.ICONS.getFont(6.0F);
      float f2 = 4.0F;
      float width = font2.width(this.icon);
      float width2 = font.width(this.name);
      float floatValue2 = floatValue + (this.getWidth() - width2) / 2.0F;
      float centeredTextY = f + (this.getHeight() - font.height()) / 2.0F;
      float centeredTextY2 = f + (this.getHeight() - font2.height()) / 2.0F;
      if (!this.name.equals("Exit")) {
         customDrawContext.drawText(
            font2,
            this.icon,
            floatValue + 25.0F,
            centeredTextY2,
            currentStyle.getTextTertiary().getZenithColor().method7295(currentStyle.getPrimaryColor().getZenithColor(), this.hoverAnimation.getValue())
         );
      }

      customDrawContext.drawText(
         font,
         this.name,
         floatValue2,
         centeredTextY,
         currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), this.hoverAnimation.getValue())
      );
   }

   @Override
   public void onClick(double d, double d2, MouseButton enum2) {
      this.onClick.run();
   }
}

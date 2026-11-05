package org.zenith.client.hud;

import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.movement.TimerModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class TimerHud extends Interface {
   private static final float CONST_floatValue = 26.0F;
   private static final float CONST_floatValue2 = 26.0F;
   private static final float CONST_floatValue3 = 2.0F;
   private static final float CONST_floatValue4 = 76.0F;
   private final Animation animation = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(180L, 0.0F, Easing.INSTANCE10);

   public TimerHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
      this.width = 26.0F + GuiStyle.PADDING.intValue() * 2.0F + 76.0F;
      this.height = 26.0F;
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      TimerModule timerModule = TimerModule.INSTANCE;
      boolean enabled = timerModule.isEnabled();
      float floatValue = timerModule.method131();
      String text = "Timer";
      String str = Math.round(floatValue * 100.0F) + "%";
      this.animation.method2807(enabled);
      if (!(this.animation.getValue() <= 0.01F)) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font font = Fonts.NEW_ICONS.getFont(6.2F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font3 = Fonts.NEW_SEMIBOLD.getFont(5.3F);
         float f = Mth.clamp(this.animation2.method2808(floatValue), 0.0F, 1.0F);
         float width = font3.width(str);
         float width2 = Math.max(18.0F, width + GuiStyle.PADDING.intValue() * 1.5F);
         float f2 = Math.max(76.0F, font2.width(text) + GuiStyle.PADDING.intValue() + width2);
         this.width = 26.0F + GuiStyle.PADDING.intValue() * 2.0F + f2;
         this.height = 26.0F;
         float intValue = InterfaceModule.method6801();
         float floatValue2 = this.floatValue + 26.0F + GuiStyle.PADDING.intValue();
         float f3 = this.floatValue + this.width - GuiStyle.PADDING.intValue();
         float floatValue3 = f3 - width2;
         float centeredTextY = this.floatValue2 + 8.0F;
         float height = 8.0F;
         float y = this.floatValue2 + 7.5F;
         float width3 = f3 - floatValue2;
         float y2 = this.floatValue2 + this.height - 5.0F;
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(this.floatValue + this.width / 2.0F, this.floatValue2 + this.height / 2.0F);
         customDrawContext.pose().scale(this.animation.getValue(), this.animation.getValue());
         customDrawContext.pose().translate(-(this.floatValue + this.width / 2.0F), -(this.floatValue2 + this.height / 2.0F));
         customDrawContext.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
         customDrawContext.drawRoundedRect(
            this.floatValue,
            this.floatValue2,
            this.width,
            this.height,
            ProxyDotRadiusData.method8069(intValue),
            currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
         );
         customDrawContext.drawRoundedRect(
            this.floatValue, this.floatValue2, 26.0F, this.height, ProxyDotRadiusData.method8078(intValue, intValue), currentStyle.getHeaderHudBackground().getZenithColor()
         );
         customDrawContext.drawRoundedRect(floatValue3, y, width2, height, ProxyDotRadiusData.method8069(1.4F), currentStyle.getHeaderHudBackground().getZenithColor());
         String icon = timerModule.getCategory().getIcon();
         customDrawContext.drawText(
            font,
            icon,
            this.floatValue + (26.0F - font.width(icon)) / 2.0F,
            this.floatValue2 + (this.height - font.height()) / 2.0F,
            currentStyle.getPrimaryColor().getZenithColor()
         );
         customDrawContext.drawText(font2, text, floatValue2, centeredTextY, currentStyle.getTextEnable().getZenithColor());
         customDrawContext.drawText(font3, str, floatValue3 + (width2 - width) / 2.0F, y + (height - font3.height()) / 2.0F, currentStyle.getPrimaryColor().getZenithColor());
         customDrawContext.drawRoundedRect(floatValue2, y2, width3, 2.0F, ProxyDotRadiusData.method8069(0.6F), currentStyle.getFieldBorder().getZenithColor());
         if (f > 0.0F) {
            customDrawContext.drawRoundedRect(floatValue2, y2, width3 * f, 2.0F, ProxyDotRadiusData.method8069(0.6F), currentStyle.getPrimaryColor().getZenithColor());
         }

         customDrawContext.popMatrix();
      }
   }

   @Override
   public float method254() {
      return this.animation.getValue();
   }
}

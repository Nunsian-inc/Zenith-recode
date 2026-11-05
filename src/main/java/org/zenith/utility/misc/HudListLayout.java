package org.zenith.utility.misc;

import java.util.List;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class HudListLayout {
   public static final float CONST_floatValue = 20.0F;
   public static final float CONST_floatValue2 = 14.0F;
   public static final float CONST_floatValue3 = 3.5F;
   public static final float CONST_floatValue4 = 13.0F;
   public static final float CONST_floatValue5 = 10.0F;
   public static final float CONST_floatValue6 = 6.0F;
   public static final float CONST_floatValue7 = GuiStyle.PADDING.intValue() * 1.5F;
   public static final String text = "m";
   public static final float CONST_floatValue8 = 17.0F;
   private static final float CONST_floatValue9 = 2.0F;

   private HudListLayout() {
   }

   public static ZenithStyle style() {
      return Zenith.getInstance().getStyleManager().getCurrentStyle();
   }

   public static Font method7630() {
      return Fonts.NEW_ICONS.getFont(5.5F);
   }

   public static Font method7631() {
      return Fonts.NEW_MEDIUM.getFont(6.0F);
   }

   public static Font nameFont() {
      return Fonts.NEW_MEDIUM.getFont(5.6F);
   }

   public static Font method7632() {
      return Fonts.NEW_SEMIBOLD.getFont(5.4F);
   }

   public static ProxyDotRadiusData method7633() {
      return ProxyDotRadiusData.method8069(InterfaceModule.method6801());
   }

   public static float dividerWidth() {
      return CONST_floatValue7 * 2.0F + 1.0F;
   }

   public static float method7634(CustomDrawContext customDrawContext, float f, float y1, float f2) {
      float height = f2 * 0.45F;
      customDrawContext.drawRect(f + CONST_floatValue7, y1 + (f2 - height) / 2.0F, 1.0F, height, style().getTextTertiary().getZenithColor().method7294(0.45F));
      return f + dividerWidth();
   }

   public static void method7635(CustomDrawContext customDrawContext, float floatValue, float y1, float width, float height) {
      ProxyDotRadiusData headerRadius = method7633();
      customDrawContext.drawBlurHud(floatValue, y1, width, height, 21.0F, headerRadius, ZenithColor.INSTANCE);
      customDrawContext.drawRoundedRect(floatValue, y1, width, height, headerRadius, style().getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778()));
   }

   public static void method7636(CustomDrawContext customDrawContext, float floatValue, float y1, float width, String text, String str) {
      ZenithStyle zenithStyle = style();
      Font font = method7630();
      Font font2 = method7631();
      float y = y1 + 3.5F;
      customDrawContext.drawRoundedRect(floatValue + 3.5F, y, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.9F), zenithStyle.getPrimaryColor().getZenithColor());
      method7637(customDrawContext, font, text, floatValue + 3.5F, y, 13.0F, ZenithColor.INSTANCE);
      float f = method7634(customDrawContext, floatValue + 3.5F + 13.0F, y1, 20.0F);
      customDrawContext.drawText(font2, str, f, y1 + (20.0F - font2.height()) / 2.0F, zenithStyle.getTextEnable().getZenithColor());
      customDrawContext.drawText(font, "m", floatValue + width - 6.0F - font.width("m"), y1 + (20.0F - font.height()) / 2.0F, zenithStyle.getTextTertiary().getZenithColor());
   }

   public static void method7637(CustomDrawContext customDrawContext, Font font, String text, float f, float y, float px, ZenithColor zenithColor) {
      float[] inkBounds = font.inkBounds(text);
      if (inkBounds == null) {
         customDrawContext.drawText(font, text, f + (px - font.width(text)) / 2.0F, y + (px - font.height()) / 2.0F, zenithColor);
      } else {
         float floatValue = f + (px - inkBounds[0]) / 2.0F;
         float centeredTextY = y + (px - inkBounds[1]) / 2.0F - font.baseline() + inkBounds[2];
         customDrawContext.drawText(font, text, floatValue, centeredTextY, zenithColor);
      }
   }

   public static float method7638(String str, String text) {
      return 16.5F + dividerWidth() + method7631().width(text) + CONST_floatValue7 + method7630().width("m") + 6.0F;
   }

   public static float method7639(CustomDrawContext customDrawContext, float floatValue, float y1, String icon) {
      method7640(customDrawContext, floatValue, y1);
      method7637(customDrawContext, method7630(), icon, method7641(floatValue), method7642(y1), 10.0F, style().getPrimaryColor().getZenithColor());
      return method7643(floatValue);
   }

   public static float method7640(CustomDrawContext customDrawContext, float floatValue, float y1) {
      customDrawContext.drawRoundedRect(
         method7641(floatValue), method7642(y1), 10.0F, 10.0F, ProxyDotRadiusData.method8069(3.0F), style().getHeaderHudBackground().getZenithColor()
      );
      return method7634(customDrawContext, floatValue + 6.0F + 10.0F, y1, 14.0F);
   }

   public static float method7641(float floatValue) {
      return floatValue + 6.0F;
   }

   public static float method7642(float y1) {
      return y1 + 2.0F;
   }

   public static float method7643(float floatValue) {
      return floatValue + method7645();
   }

   public static void method7644(CustomDrawContext customDrawContext, float floatValue, float y1, String text) {
      Font font = nameFont();
      customDrawContext.drawText(font, text, floatValue, y1 + (14.0F - font.height()) / 2.0F, style().getTextEnable().getZenithColor());
   }

   public static float method7645() {
      return 16.0F + dividerWidth();
   }

   public static float method7646(CustomDrawContext customDrawContext, float floatValue, float y1, float width, String text) {
      ZenithStyle zenithStyle = style();
      Font font = method7632();
      float f = font.width(text);
      float width2 = method7647(text);
      float floatValue2 = floatValue + width - 6.0F - width2;
      float y = y1 + 2.0F;
      customDrawContext.drawRoundedRect(floatValue2, y, width2, 10.0F, ProxyDotRadiusData.method8069(3.0F), zenithStyle.getHeaderHudBackground().getZenithColor());
      customDrawContext.drawText(font, text, floatValue2 + (width2 - f) / 2.0F, y + (10.0F - font.height()) / 2.0F, zenithStyle.getTextEnable().getZenithColor());
      return floatValue2;
   }

   public static float method7647(String text) {
      return Math.max(10.0F, method7632().width(text) + GuiStyle.PADDING.intValue());
   }

   public static float method7648(String name, String text) {
      return method7645() + nameFont().width(name) + CONST_floatValue7 + method7647(text) + 6.0F;
   }

   public static float method7649(CustomDrawContext customDrawContext, float floatValue, float y1, float width, String text, float f) {
      ZenithStyle zenithStyle = style();
      Font font = method7632();
      float width2 = font.width(text);
      float width3 = method7650(text);
      float floatValue2 = floatValue + width - 6.0F - width3;
      float y = method7642(y1);
      float width4 = 8.0F;
      float floatValue3 = floatValue2 + width3 - GuiStyle.PADDING.intValue() / 2.0F - width4;
      float y2 = y + (10.0F - width4) / 2.0F;
      customDrawContext.drawRoundedRect(floatValue2, y, width3, 10.0F, ProxyDotRadiusData.method8069(3.0F), zenithStyle.getHeaderHudBackground().getZenithColor());
      customDrawContext.drawText(font, text, floatValue2 + GuiStyle.PADDING.intValue() / 2.0F, y + (10.0F - font.height()) / 2.0F, zenithStyle.getTextEnable().getZenithColor());
      customDrawContext.drawArcBorder(floatValue3, y2, width4, width4, 1.0F, 360.0F, 0.5F, zenithStyle.getFieldBorder().getZenithColor());
      customDrawContext.drawArcBorder(floatValue3, y2, width4, width4, 1.0F, 360.0F * f, 0.5F, zenithStyle.getPrimaryColor().getZenithColor());
      return floatValue2;
   }

   public static float method7650(String text) {
      return GuiStyle.PADDING.intValue() / 2.0F + method7632().width(text) + GuiStyle.PADDING.intValue() / 2.0F + 8.0F + GuiStyle.PADDING.intValue() / 2.0F;
   }

   public static float method7651(String text, String str) {
      return method7645() + nameFont().width(text) + CONST_floatValue7 + method7650(str) + 6.0F;
   }

   public static float method7652() {
      return 7.0F;
   }

   public static void method7653(CustomDrawContext customDrawContext, float f, float f2, float f3, String text, int n) {
      ZenithStyle zenithStyle = style();
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      float f4 = method7652();
      customDrawContext.drawText(font, text, f + 6.0F, f2 + (f4 - font.height()) / 2.0F, zenithStyle.getTextTertiary().getZenithColor());
      String str = String.valueOf(n);
      customDrawContext.drawText(font, str, f + f3 - 6.0F - font.width(str), f2 + (f4 - font.height()) / 2.0F, zenithStyle.getTextTertiary().getZenithColor());
   }

   public static Font method7654() {
      return Fonts.NEW_ICONS.getFont(5.0F);
   }

   public static Font method7655() {
      return Fonts.NEW_MEDIUM.getFont(6.0F);
   }

   public static Font method7656() {
      return Fonts.NEW_REGULAR.getFont(5.4F);
   }

   public static float method7657(HudListLayout.Data data) {
      float f = method7654().width(data.getText()) + 2.0F;

      for (HudListLayout.Data2 data2 : data.getItems()) {
         f += (data2.isFlag() ? method7656() : method7655()).width(data2.text());
      }

      return f;
   }

   public static float method7658(List<HudListLayout.Data> datas) {
      if (datas.isEmpty()) {
         return 0.0F;
      } else {
         float f = 12.0F;

         for (int i = 0; i < datas.size(); i++) {
            f += method7657((HudListLayout.Data)datas.get(i));
            if (i > 0) {
               f += dividerWidth();
            }
         }

         return f;
      }
   }

   public static void method7659(CustomDrawContext customDrawContext, float floatValue, float y1, List<HudListLayout.Data> datas) {
      if (!datas.isEmpty()) {
         ZenithStyle zenithStyle = style();
         Font font = method7654();
         Font font2 = method7655();
         Font font3 = method7656();
         ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor();
         ZenithColor zenithColor2 = zenithStyle.getTextEnable().getZenithColor();
         ZenithColor zenithColor3 = zenithStyle.getTextTertiary().getZenithColor();
         method7635(customDrawContext, floatValue, y1, method7658(datas), 17.0F);
         float f = floatValue + 6.0F;

         for (int i = 0; i < datas.size(); i++) {
            if (i > 0) {
               f = method7634(customDrawContext, f, y1, 17.0F);
            }

            HudListLayout.Data data = (HudListLayout.Data)datas.get(i);
            customDrawContext.drawText(font, data.getText(), f, y1 + (17.0F - font.height()) / 2.0F, zenithColor);
            f += font.width(data.getText()) + 2.0F;

            for (HudListLayout.Data2 data2 : data.getItems()) {
               Font font4 = data2.isFlag() ? font3 : font2;
               ZenithColor zenithColor4 = data2.isFlag() ? zenithColor3 : zenithColor2;
               customDrawContext.drawText(font4, data2.text(), f, y1 + (17.0F - font4.height()) / 2.0F, zenithColor4);
               f += font4.width(data2.text());
            }
         }
      }
   }

   public static final class Data {
      private final String text;
      private final List<HudListLayout.Data2> items;

      public String text() {
         return this.text;
      }

      public List<HudListLayout.Data2> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(String text, List<HudListLayout.Data2> items) {
         this.text = text;
         this.items = items;
      }

      public static HudListLayout.Data2 method7660(String text) {
         return new HudListLayout.Data2(text, false);
      }

      public static HudListLayout.Data2 method7661(String text) {
         return new HudListLayout.Data2(text, true);
      }

      public static HudListLayout.Data method7662(String text, String str, String text2) {
         return new HudListLayout.Data(text, List.of(method7660(str), method7661(text2)));
      }

      public String getText() {
         return this.text;
      }

      public List<HudListLayout.Data2> getItems() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final String text;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, flag);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "flag=" + this.flag + "]";
      }

      public Data2(String text, boolean flag) {
         this.text = text;
         this.flag = flag;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

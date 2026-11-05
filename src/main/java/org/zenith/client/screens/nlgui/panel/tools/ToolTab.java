package org.zenith.client.screens.nlgui.panel.tools;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.panel.SectionPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public abstract class ToolTab extends SectionPanel {
   public static final ZenithColor OK = new ZenithColor(96, 214, 128);
   public static final ZenithColor DANGER = new ZenithColor(242, 96, 110);
   public static final ZenithColor WARN = new ZenithColor(240, 180, 80);
   public static final float CARD_ROUND = GuiStyle.ROUND.intValue() * 0.7F;
   public static final int TONE_GHOST = 0;
   public static final int TONE_PRIMARY = 1;
   public static final int TONE_DANGER = 2;
   private final Map<String, Animation> hovers = new HashMap<>();
   private final Map<String, Animation> switches = new HashMap<>();
   private final Map<String, Long> marks = new HashMap<>();
   private long enteredAt = System.currentTimeMillis();
   private long asideAt = System.currentTimeMillis();
   private long replayAt;
   private int replayFrom = Integer.MAX_VALUE;
   private long asideReplayAt;
   private int asideReplayFrom = Integer.MAX_VALUE;
   private long lastFrame;

   @Override
   public boolean pressFeedback() {
      return true;
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float panelAlpha, float f, float y) {
      long now = System.currentTimeMillis();
      if (now - this.lastFrame > 400L) {
         this.shown();
      }

      this.lastFrame = now;
      super.render(menuDrawContext, lastClickX, lastClickY, panelAlpha, f, y);
   }

   public void shown() {
      this.enteredAt = System.currentTimeMillis();
      this.asideAt = this.enteredAt;
      this.replayFrom = Integer.MAX_VALUE;
      this.asideReplayFrom = Integer.MAX_VALUE;
   }

   public void replayMainFrom(int replayFrom) {
      this.replayAt = System.currentTimeMillis();
      this.replayFrom = replayFrom;
   }

   public void replayAsideFrom(int asideReplayFrom) {
      this.asideReplayAt = System.currentTimeMillis();
      this.asideReplayFrom = asideReplayFrom;
   }

   public float hot(String anObject, boolean contains) {
      return this.hovers.computeIfAbsent(anObject, hover -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(contains ? 1.0F : 0.0F);
   }

   public void mark(String str) {
      this.marks.put(str, System.currentTimeMillis());
   }

   public float since(String str, long l) {
      Long mark = this.marks.get(str);
      return mark == null ? 1.0F : Math.min(1.0F, (float)(System.currentTimeMillis() - mark) / (float)l);
   }

   public float pressed(String key) {
      return Smooth.press(this.marks.getOrDefault(key, 0L), 340L);
   }

   private static float curve(long asideReplayAt, int n) {
      float f = (float)(System.currentTimeMillis() - asideReplayAt - Math.min(n, 14) * 34L) / 320.0F;
      if (f <= 0.0F) {
         return 0.0F;
      } else if (f >= 1.0F) {
         return 1.0F;
      } else {
         float f2 = 1.0F - f;
         return 1.0F - f2 * f2 * f2;
      }
   }

   @Override
   public float entryEnter(int n, boolean flag) {
      if (flag) {
         return n >= this.asideReplayFrom && this.asideReplayAt > this.asideAt
            ? curve(this.asideReplayAt, n - this.asideReplayFrom)
            : curve(this.asideAt, n + 1);
      } else {
         return n >= this.replayFrom && this.replayAt > this.enteredAt ? curve(this.replayAt, n - this.replayFrom) : curve(this.enteredAt, n);
      }
   }

   public SectionPanel.AsideRenderer entering(int n, SectionPanel.AsideRenderer asideRenderer) {
      return asideRenderer;
   }

   public static float pulse(float f) {
      return 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 1000.0 * f * Math.PI * 2.0);
   }

   public static ZenithColor cardFill(ZenithStyle zenithStyle, float hot) {
      return zenithStyle.getFieldSurfaceBackground().getZenithColor().method7295(zenithStyle.getDisableActiveBg().getZenithColor(), 0.35F + hot * 0.55F);
   }

   public static void card(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float width, float height, float hot, float previewAlpha) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(CARD_ROUND);
      menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, cardFill(zenithStyle, hot).method7294(previewAlpha));
      menuDrawContext.drawRoundedBorder(floatValue, y, width, height, 0.15F, headerRadius, zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F + hot * 2.0F).method7294(previewAlpha));
   }

   public static void accentCard(
      MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float width, float height, float hot, float previewAlpha, ZenithColor zenithColor, float hover
   ) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(CARD_ROUND);
      menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, cardFill(zenithStyle, hot).method7295(zenithColor.method7292(0.16F), hover).method7294(previewAlpha));
      menuDrawContext.drawRoundedBorder(
         floatValue,
         y,
         width,
         height,
         0.2F,
         headerRadius,
         zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F + hot * 2.0F).method7295(zenithColor.method7292(0.6F), hover).method7294(previewAlpha)
      );
   }

   public static void tile(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float width, ZenithColor zenithColor, float hover, float previewAlpha) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(width * 0.28F);
      menuDrawContext.drawRoundedRect(
         floatValue, y, width, width, headerRadius, zenithStyle.getPanelLeftBackground().getZenithColor().method7295(zenithColor.method7292(0.3F), hover).method7294(previewAlpha)
      );
   }

   public static float pill(MenuDrawContext menuDrawContext, Font font, float floatValue, float centeredTextY, String text, ZenithColor zenithColor, float previewAlpha) {
      float width = font.width(text) + 8.0F;
      float height = font.height() + 3.6F;
      menuDrawContext.drawRoundedRect(floatValue, centeredTextY, width, height, ProxyDotRadiusData.method8069(height / 2.0F), zenithColor.method7292(0.16F).method7294(previewAlpha));
      menuDrawContext.drawText(font, text, floatValue + 4.0F, centeredTextY + 1.8F, zenithColor.method7294(previewAlpha));
      return width;
   }

   public static float pillWidth(Font font, String text) {
      return font.width(text) + 8.0F;
   }

   public static void dot(MenuDrawContext menuDrawContext, float f, float f2, float width, ZenithColor zenithColor, float pulse, float previewAlpha) {
      menuDrawContext.drawRoundedRect(
         f - width / 2.0F, f2 - width / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor.method7294(previewAlpha * (1.0F - 0.45F * pulse))
      );
   }

   public static void caption(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, String str, float floatValue, float centeredTextY, float previewAlpha) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
      menuDrawContext.drawText(font, HoverInfo.spaced(str.toUpperCase(Locale.ROOT)), floatValue, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   public boolean button(
      MenuDrawContext menuDrawContext,
      ZenithStyle zenithStyle,
      String str,
      BoundsData boundsData,
      MenuGlyph menuGlyph,
      String name,
      int n,
      boolean flag,
      int lastClickX,
      int lastClickY,
      float previewAlpha
   ) {
      boolean contains = flag && boundsData.contains(lastClickX, lastClickY);
      float hot = this.hot("btn:" + str, contains);
      ZenithColor zenithColor = n == 2 ? DANGER : zenithStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(Math.min(CARD_ROUND, boundsData.height() / 2.0F));
      boolean begin = this.pressFx.begin(menuDrawContext, "btn:" + str, boundsData);
      ZenithColor cardFill2 = cardFill(zenithStyle, hot);
      if (!flag) {
         cardFill2 = zenithStyle.getFieldSurfaceBackground().getZenithColor().method7295(zenithStyle.getDisableActiveBg().getZenithColor(), 0.25F);
      } else if (n == 1) {
         cardFill2 = cardFill2.method7295(zenithColor.method7292(0.2F), 0.55F + hot * 0.45F);
      } else if (n == 2) {
         cardFill2 = cardFill2.method7295(zenithColor.method7292(0.14F), 0.25F + hot * 0.5F);
      }

      menuDrawContext.drawRoundedRect(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, cardFill2.method7294(previewAlpha));
      ZenithColor zenithColor2 = !flag
         ? zenithStyle.getFieldBorder().getZenithColor().method7294(1.5F)
         : (n == 0 ? zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F + hot * 2.5F) : zenithColor.method7292(0.22F + hot * 0.3F));
      menuDrawContext.drawRoundedBorder(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.18F, headerRadius, zenithColor2.method7294(previewAlpha));
      this.pressFx.ink(menuDrawContext, "btn:" + str, boundsData, headerRadius.getFloatValue(), (n == 0 ? ZenithColor.INSTANCE : zenithColor).method7292(0.22F), previewAlpha);
      Font font = Fonts.NEW_MEDIUM.getFont(boundsData.height() >= 18.0F ? 5.2F : 4.8F);
      ZenithColor zenithColor3 = !flag
         ? zenithStyle.getTextTertiary().getZenithColor()
         : (
            n == 0
               ? zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hot)
               : zenithColor.method7295(ZenithColor.INSTANCE, 0.2F + hot * 0.45F)
         );
      float f = menuGlyph == null ? 0.0F : 10.0F;
      String clip = font.clip(name, boundsData.width() - 10.0F - f);
      float f2 = font.width(clip) + f;
      float floatValue = boundsData.getFloatValue() + (boundsData.width() - f2) / 2.0F;
      if (menuGlyph != null) {
         menuGlyph.draw(menuDrawContext, floatValue, boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F, zenithColor3.method7294(previewAlpha));
         floatValue += f;
      }

      menuDrawContext.drawText(font, clip, floatValue, boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F, zenithColor3.method7294(previewAlpha));
      this.pressFx.end(menuDrawContext, begin);
      return contains;
   }

   public void pressButton(String str, double lastClickX, double lastClickY) {
      this.pressFx.press("btn:" + str, lastClickX, lastClickY);
   }

   public void shimmer(MenuDrawContext menuDrawContext, BoundsData boundsData, ZenithColor zenithColor, float previewAlpha) {
      float f = (float)(System.currentTimeMillis() % 1600L) / 1600.0F;
      float f2 = boundsData.width() * 0.35F;
      float f3 = boundsData.getFloatValue() - f2 + (boundsData.width() + f2) * f;
      float floatValue = Math.max(boundsData.getFloatValue(), f3);
      float f4 = Math.min(boundsData.getFloatValue() + boundsData.width(), f3 + f2);
      if (!(f4 - floatValue < 1.0F)) {
         float f5 = (floatValue + f4) / 2.0F;
         float f6 = 1.0F - Math.abs(f5 - (f3 + f2 / 2.0F)) / (f2 / 2.0F + 0.01F);
         menuDrawContext.drawRoundedRect(
            floatValue,
            boundsData.getFloatValue2() + 1.0F,
            f4 - floatValue,
            boundsData.height() - 2.0F,
            ProxyDotRadiusData.method8069(Math.min(4.0F, (boundsData.height() - 2.0F) / 2.0F)),
            zenithColor.method7292(0.12F * Math.max(0.3F, f6)).method7294(previewAlpha)
         );
      }
   }

   public void drawSwitch(MenuDrawContext menuDrawContext, String key, float floatValue, float y, float width, float height, boolean enabled, float previewAlpha, ZenithStyle zenithStyle) {
      float hover = this.switches.computeIfAbsent(key, var1x -> new Animation(220L, enabled ? 1.0F : 0.0F, Easing.INSTANCE15)).method2808(enabled ? 1.0F : 0.0F);
      ZenithColor zenithColor = zenithStyle.getFieldBorder().getZenithColor().method7294(3.0F);
      menuDrawContext.drawRoundedRect(
         floatValue, y, width, height, ProxyDotRadiusData.method8069(height / 2.0F), zenithColor.method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover).method7294(previewAlpha)
      );
      float f = height - 2.0F;
      float f2 = floatValue + 1.0F + (width - f - 2.0F) * hover;
      float f3 = (float)Math.sin(hover * Math.PI) * 1.5F;
      menuDrawContext.drawRoundedRect(
         f2 - f3 * 0.5F,
         y + 1.0F,
         f + f3,
         f,
         ProxyDotRadiusData.method8069(f / 2.0F),
         zenithStyle.getTextSecondary().getZenithColor().method7295(ZenithColor.INSTANCE, hover).method7294(previewAlpha)
      );
   }

   public static void progress(
      MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float width, float height, float f, ZenithColor zenithColor, boolean enabled, float previewAlpha
   ) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(height / 2.0F);
      menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha));
      float f2 = Math.max(0.0F, Math.min(1.0F, f));
      if (f2 > 0.001F) {
         float width2 = Math.max(height, width * f2);
         menuDrawContext.drawRoundedRect(floatValue, y, width2, height, headerRadius, zenithColor.method7294(previewAlpha));
         if (enabled) {
            float f3 = (float)(System.currentTimeMillis() % 1200L) / 1200.0F;
            float f4 = floatValue + (width2 + 10.0F) * f3 - 10.0F;
            float floatValue2 = Math.max(floatValue, f4);
            float f5 = Math.min(floatValue + width2, f4 + 10.0F);
            if (f5 > floatValue2) {
               menuDrawContext.drawRoundedRect(floatValue2, y, f5 - floatValue2, height, headerRadius, ZenithColor.INSTANCE.method7292(0.35F).method7294(previewAlpha));
            }
         }
      }
   }

   public static void skeleton(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float width, float height, int n, float f) {
      float f2 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 420.0 - n * 0.7);
      menuDrawContext.drawRoundedRect(
         floatValue,
         y,
         width,
         height,
         ProxyDotRadiusData.method8069(Math.min(height / 2.0F, 3.0F)),
         zenithStyle.getDisableActiveBg().getZenithColor().method7294(f * (0.45F + 0.45F * f2))
      );
   }

   public static void bigGlyph(MenuDrawContext menuDrawContext, MenuGlyph menuGlyph, float x, float y, float f, ZenithColor zenithColor) {
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(x, y);
      menuDrawContext.pose().scale(f, f);
      menuGlyph.draw(menuDrawContext, -3.0F, -3.0F, zenithColor);
      menuDrawContext.popMatrix();
   }

   public void emptyCard(
      MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float width, float height, MenuGlyph menuGlyph, String capitalize, String description, float previewAlpha
   ) {
      card(menuDrawContext, zenithStyle, floatValue, y, width, height, 0.0F, previewAlpha);
      float f = (float)Math.sin(System.currentTimeMillis() / 600.0) * 1.2F;
      float width2 = 20.0F;
      float floatValue2 = floatValue + (width - width2) / 2.0F;
      float y2 = y + 9.0F + f;
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      tile(menuDrawContext, zenithStyle, floatValue2, y2, width2, zenithColor, 0.6F, previewAlpha);
      bigGlyph(menuDrawContext, menuGlyph, floatValue2 + width2 / 2.0F, y2 + width2 / 2.0F, 1.5F, zenithColor.method7294(previewAlpha));
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
      float centeredTextY = y + 9.0F + width2 + 8.0F;
      menuDrawContext.drawText(
         font,
         font.clip(capitalize, width - 12.0F),
         floatValue + (width - Math.min(width - 12.0F, font.width(capitalize))) / 2.0F,
         centeredTextY,
         zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
      );
      centeredTextY += font.height() + 3.0F;

      for (String text : wrapText(description, font2, width - 16.0F, 3)) {
         menuDrawContext.drawText(font2, text, floatValue + (width - font2.width(text)) / 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY += font2.height() + 2.0F;
      }
   }

   public void drawItem(MenuDrawContext menuDrawContext, ItemStack itemStack, float x, float y, float f, float previewAlpha) {
      if (itemStack != null && !itemStack.isEmpty() && !(previewAlpha <= 0.02F) && this.isFullyVisible()) {
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(x, y);
         float x2 = f / 16.0F;
         menuDrawContext.pose().scale(x2, x2);
         menuDrawContext.item(itemStack, 0, 0);
         menuDrawContext.pose().popMatrix();
      }
   }

   public static String fit(Font font, String name, float f) {
      if (name != null && !name.isEmpty() && !(f <= 0.0F)) {
         if (font.width(name) <= f) {
            return name;
         } else {
            String text = "..";
            float width = font.width(text);
            if (width > f) {
               return "";
            } else {
               int length = name.length();

               while (length > 0 && font.width(name.substring(0, length)) + width > f) {
                  length--;
               }

               return length == 0 ? "" : name.substring(0, length) + text;
            }
         }
      } else {
         return "";
      }
   }
}

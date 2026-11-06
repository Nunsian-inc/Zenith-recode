package org.zenith.client.screens.nlgui.cloud;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class CloudCard {
   private static final float PAD = 4.0F;
   private static final float TITLE_SIZE = 5.0F;
   private static final float META_SIZE = 4.2F;
   private static final float BADGE_HEIGHT = 8.0F;
   private static final float PILL_HEIGHT = 10.0F;
   private static final String DOWNLOAD_LABEL = "gui.configs.downloadShort";
   private static final ZenithColor[] TINTS = new ZenithColor[]{
      new ZenithColor("#8E9BFF"),
      new ZenithColor("#63C9FF"),
      new ZenithColor("#7FE0B4"),
      new ZenithColor("#FFC46B"),
      new ZenithColor("#FF97A0"),
      new ZenithColor("#C89BFF")
   };
   private final Animation hoverAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation selectAnimation = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation previewFade = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private final Animation likeAnimation = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private final Animation downloadAnimation = new Animation(120L, 0.0F, Easing.INSTANCE12);
   private boolean downloadHot;
   private boolean likeReady;
   private int likesFor = Integer.MIN_VALUE;
   private String likesText;
   private static final Map<CloudCard.SizeKey, String> SIZES = Collections.synchronizedMap(new LinkedHashMap<CloudCard.SizeKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<CloudCard.SizeKey, String> eldest) {
         return this.size() > 256;
      }
   });
   private static final Map<CloudCard.FitKey, String> FITS = Collections.synchronizedMap(new LinkedHashMap<CloudCard.FitKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<CloudCard.FitKey, String> eldest) {
         return this.size() > 512;
      }
   });

   public static float previewHeight(float width) {
      return (width - 8.0F) * 9.0F / 16.0F;
   }

   public static float height(float width) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      return 4.0F + previewHeight(width) + 5.0F + font.height() + 3.0F + font2.height() + 4.0F;
   }

   public boolean downloadHovered() {
      return this.downloadHot;
   }

   public void render(
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      StoreApi.Data2 data2,
      float floatValue,
      float y,
      float width,
      boolean flag,
      boolean flag2,
      float previewAlpha,
      ZenithStyle zenithStyle
   ) {
      float f = height(width);
      float previewHeight2 = previewHeight(width);
      float f2 = this.hoverAnimation.animateTo(flag2 ? 1.0F : 0.0F);
      float f3 = this.selectAnimation.animateTo(flag ? 1.0F : 0.0F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.75F);
      menuDrawContext.drawRoundedRect(
         floatValue,
         y,
         width,
         f,
         headerRadius,
         zenithStyle.getFieldSurfaceBackground()
            .getZenithColor()
            .method7295(zenithStyle.getDisableActiveBg().getZenithColor(), Math.max(f2 * 0.7F, f3))
            .method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedBorder(
         floatValue,
         y,
         width,
         f,
         0.15F,
         headerRadius,
         zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F).method7295(zenithColor.method7292(0.4F), Math.max(f3, f2 * 0.45F)).method7294(previewAlpha)
      );
      this.renderPreview(menuDrawContext, data2, floatValue + 4.0F, y + 4.0F, width - 8.0F, previewHeight2, previewAlpha, zenithStyle);
      this.renderBadge(menuDrawContext, data2, floatValue + 4.0F + 3.0F, y + 4.0F + 3.0F, previewAlpha, zenithStyle);
      this.renderDownload(menuDrawContext, lastClickX, lastClickY, floatValue + 4.0F, y + 4.0F, width - 8.0F, previewHeight2, f2, previewAlpha, zenithStyle);
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float centeredTextY = y + 4.0F + previewHeight2 + 5.0F;
      ZenithColor zenithColor2 = zenithStyle.getTextSecondary()
         .getZenithColor()
         .method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(f3, f2 * 0.6F))
         .method7294(previewAlpha);
      menuDrawContext.drawText(font, fit(font, data2.name(), width - 8.0F - 2.0F), floatValue + 4.0F + 1.0F, centeredTextY, zenithColor2);
      float centeredTextY2 = centeredTextY + font.height() + 3.0F;
      if (this.likesText == null || this.likesFor != data2.getIntValue()) {
         this.likesFor = data2.getIntValue();
         this.likesText = String.valueOf(this.likesFor);
      }

      String likesText = this.likesText;
      float f4 = 8.0F + font2.width(likesText);
      if (!this.likeReady) {
         this.likeAnimation.snapTo(data2.isFlag3() ? 1.0F : 0.0F);
         this.likeReady = true;
      }

      float hover = this.likeAnimation.animateTo(data2.isFlag3() ? 1.0F : 0.0F);
      ZenithColor zenithColor3 = zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getHeartIcon().getZenithColor(), hover);
      float f5 = floatValue + width - 4.0F - 1.0F - f4;
      float f6 = centeredTextY2 + (font2.height() - 6.0F) / 2.0F;
      float x = 1.0F + 0.35F * (float)Math.sin(Math.PI * hover) * (data2.isFlag3() ? 1.0F : 0.6F);
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(f5 + 3.0F, f6 + 3.0F);
      menuDrawContext.pose().scale(x, x);
      MenuGlyph.HEART.draw(menuDrawContext, -3.0F, -3.0F, zenithColor3.method7294(previewAlpha));
      menuDrawContext.popMatrix();
      menuDrawContext.drawText(font2, likesText, floatValue + width - 4.0F - 1.0F - font2.width(likesText), centeredTextY2, zenithColor3.method7294(previewAlpha));
      String name = data2.getText8() != null && !data2.getText8().isBlank() ? data2.getText8() : size(data2.getLongValue());
      menuDrawContext.drawText(font2, fit(font2, name, width - 8.0F - f4 - 6.0F), floatValue + 4.0F + 1.0F, centeredTextY2, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   private void renderPreview(MenuDrawContext menuDrawContext, StoreApi.Data2 data2, float floatValue, float y, float width, float height, float previewAlpha, ZenithStyle zenithStyle) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
      Identifier texture = data2.hasPreview() ? CloudPreviewTextureCache.get(data2.getUUID(), data2.getText6()) : null;
      ZenithColor zenithColor = TINTS[Math.floorMod(nameSeed(data2.name()), TINTS.length)];
      if (texture != null) {
         float f = this.previewFade.animateTo(1.0F);
         if (f < 1.0F) {
            menuDrawContext.drawRoundedRect(
               floatValue,
               y,
               width,
               height,
               headerRadius,
               zenithStyle.getSurfaceEnableBackground().getZenithColor().method7295(zenithColor, 0.09F).method7294(previewAlpha * 2.0F * (1.0F - f))
            );
         }

         menuDrawContext.drawRoundedTexture(texture, floatValue, y, width, height, headerRadius, new ZenithColor(255, 255, 255).method7294(previewAlpha * f));
      } else {
         menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7295(zenithColor, 0.09F).method7294(previewAlpha * 2.0F));
         if (data2.hasPreview()) {
            Font font = Fonts.NEW_REGULAR.getFont(4.2F);
            float f = 0.55F + 0.45F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 260.0));
            menuDrawContext.drawText(
               font,
               "…",
               floatValue + (width - font.width("…")) / 2.0F,
               y + (height - font.height()) / 2.0F,
               zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * f)
            );
         } else {
            Font font = Fonts.NEW_SEMIBOLD.getFont(Math.max(9.0F, height * 0.36F));
            String text = initial(data2.name());
            menuDrawContext.drawText(
               font,
               text,
               floatValue + (width - font.width(text)) / 2.0F,
               y + (height - font.height()) / 2.0F,
               zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.55F)
            );
         }
      }
   }

   private void renderBadge(MenuDrawContext menuDrawContext, StoreApi.Data2 data2, float floatValue, float y, float previewAlpha, ZenithStyle zenithStyle) {
      if ("script".equals(data2.kind())) {
         Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
         String text = t("gui.configs.chip.script");
         float width = font.width(text) + 8.0F;
         menuDrawContext.drawRoundedRect(floatValue, y, width, 8.0F, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getRightBackground().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawText(font, text, floatValue + 4.0F, y + (8.0F - font.height()) / 2.0F, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      }
   }

   private void renderDownload(
      MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float f4, float f5, float previewAlpha, ZenithStyle zenithStyle
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);
      float width = font.width(t("gui.configs.downloadShort")) + 10.0F;
      float floatValue = f + f3 - 3.0F - width;
      float y = f2 + f4 - 3.0F - 10.0F;
      this.downloadHot = f5 > 0.35F && new BoundsData(floatValue, y, width, 10.0F).contains(lastClickX, lastClickY);
      float f6 = this.downloadAnimation.animateTo(this.downloadHot ? 1.0F : 0.0F);
      if (!(f5 <= 0.01F)) {
         ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(5.0F);
         float f7 = (1.0F - f5) * 4.0F;
         menuDrawContext.drawRoundedRect(floatValue, y + f7, width, 10.0F, headerRadius, zenithStyle.getRightBackground().getZenithColor().method7294(previewAlpha * f5));
         menuDrawContext.drawRoundedBorder(floatValue, y + f7, width, 10.0F, 0.15F, headerRadius, zenithColor.method7292(0.22F + f6 * 0.28F).method7294(previewAlpha * f5));
         menuDrawContext.drawText(
            font,
            t("gui.configs.downloadShort"),
            floatValue + 5.0F,
            y + f7 + (10.0F - font.height()) / 2.0F,
            zenithColor.method7294(previewAlpha * f5 * (0.8F + f6 * 0.2F))
         );
      }
   }

   private static int nameSeed(String name) {
      return name == null ? 0 : name.hashCode();
   }

   private static String initial(String name) {
      return name != null && !name.isBlank() ? name.trim().substring(0, 1).toUpperCase(Locale.ROOT) : "?";
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   public static String size(long longValue) {
      CloudCard.SizeKey sizeKey = new CloudCard.SizeKey(longValue, Zenith.getInstance().getLocalization().getLanguageName());
      String str = SIZES.get(sizeKey);
      if (str != null) {
         return str;
      } else {
         String str2 = longValue < 1024L ? longValue + " " + t("gui.unit.byte") : String.format(Locale.US, "%.1f", (float)longValue / 1024.0F) + " " + t("gui.unit.kb");
         SIZES.put(sizeKey, str2);
         return str2;
      }
   }

   public static String fit(Font font, String name, float room) {
      if (name != null && !(room <= 0.0F)) {
         if (font.width(name) <= room) {
            return name;
         } else {
            CloudCard.FitKey fitKey = new CloudCard.FitKey(name, font, room);
            String str = FITS.get(fitKey);
            if (str != null) {
               return str;
            } else {
               String clip = font.clip(name, room);
               FITS.put(fitKey, clip);
               return clip;
            }
         }
      } else {
         return "";
      }
   }

   private static final class FitKey {
      private final String text;
      private final Font font;
      private final float room;

      public String text() {
         return this.text;
      }

      public Font font() {
         return this.font;
      }

      public float room() {
         return this.room;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof FitKey other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.room, other.room);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, font, room);
      }

      @Override
      public String toString() {
         return "FitKey[" + "text=" + this.text + ", " + "font=" + this.font + ", " + "room=" + this.room + "]";
      }

      private FitKey(String text, Font font, float room) {
         this.text = text;
         this.font = font;
         this.room = room;
      }
   }

   private static final class SizeKey {
      private final long bytes;
      private final String language;

      public long bytes() {
         return this.bytes;
      }

      public String language() {
         return this.language;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SizeKey other)) return false;
         return java.util.Objects.equals(this.bytes, other.bytes) && java.util.Objects.equals(this.language, other.language);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bytes, language);
      }

      @Override
      public String toString() {
         return "SizeKey[" + "bytes=" + this.bytes + ", " + "language=" + this.language + "]";
      }

      private SizeKey(long bytes, String language) {
         this.bytes = bytes;
         this.language = language;
      }
   }
}

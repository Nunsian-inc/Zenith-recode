package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ProfilePanel extends Panel {
   private final Animation animationVisible = new Animation(300L, 0.0F, Easing.INSTANCE13);
   private boolean expanded = false;
   private final GuiBooleanSetting steamMode = new GuiBooleanSetting(new BooleanSetting("panel.profile.setting.streamerMode", false), 112.0F);
   private final GuiBooleanSetting discordRPC = new GuiBooleanSetting(new BooleanSetting("panel.profile.setting.discordRpc", true), 112.0F);
   private final GuiButtonSetting webProfile = new GuiButtonSetting(
      new ButtonSetting("panel.profile.setting.webProfile", "J", () -> org.zenith.utility.misc.OsOpen.uri(RegionUtil.method2038().get(0) + "/dashboard")), 112.0F
   );
   private final GuiButtonSetting openFolder = new GuiButtonSetting(new ButtonSetting("panel.profile.setting.openFolder", "K", () -> {
      try {
         Runtime.getRuntime().exec("explorer " + Manager.file.getAbsolutePath());
      } catch (IOException e) {
      }
   }), 112.0F);
   private final Animation languageLeftHoverAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation languageRightHoverAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation languageSwitchAnimation = new Animation(120L, 1.0F, Easing.INSTANCE12);
   private BoundsData languageLeftBounds;
   private BoundsData languageRightBounds;
   private BoundsData exitBounds;
   private String displayedLanguageName;
   private String outgoingLanguageName;
   private int languageSwitchDirection = 1;
   private boolean languageSwitchActive;

   @Override
   public void render(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float floatValue, float f) {
      this.animationVisible.updateVisibility(this.expanded);
      this.animationVisible.setDuration(200L);
      previewAlpha *= this.animationVisible.getValue();
      float panelWidth = 128.0F;
      float height = 190.0F;
      menuDrawContext.enableScissor(floatValue, f, floatValue + panelWidth + GuiStyle.PADDING * 4, f + height);
      floatValue += (panelWidth + GuiStyle.PADDING.intValue()) * (1.0F - this.animationVisible.getValue());
      if (Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F) {
         RenderShaders.method5287(
            MatrixUtils.method664(menuDrawContext.pose()),
            floatValue,
            f,
            panelWidth,
            height,
            Zenith.getInstance().getNLMenuScreen().getBlurPower(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            ZenithColor.INSTANCE.method7294(previewAlpha),
            true,
            false
         );
      }

      float height2 = 29.0F;
      float height3 = 27.0F;
      float height4 = 56.0F;
      float height5 = 45.0F;
      float height6 = 33.0F;
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.renderHeader(menuDrawContext, x, y, previewAlpha, floatValue, f, panelWidth, height2, currentStyle);
      this.renderTill(menuDrawContext, x, y, previewAlpha, floatValue, f + height2, panelWidth, height3, currentStyle);
      this.renderSettings(menuDrawContext, x, y, previewAlpha, floatValue, f + height2 + height3, panelWidth, height4, currentStyle);
      this.renderInfo(menuDrawContext, x, y, previewAlpha, floatValue, f + height2 + height3 + height4, panelWidth, height5, currentStyle);
      this.renderButton(menuDrawContext, x, y, previewAlpha, floatValue, f + height2 + height3 + height4 + height5, panelWidth, height6, currentStyle);
      menuDrawContext.disableScissor();
   }

   public void renderButton(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float floatValue, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(
         floatValue, f, panelWidth, height, ProxyDotRadiusData.method8077(GuiStyle.ROUND.intValue()), currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
      );
      float floatValue2 = floatValue + GuiStyle.PADDING * 2;
      float y2 = f + GuiStyle.PADDING * 2;
      float width = panelWidth - GuiStyle.PADDING * 4;
      float height2 = height - GuiStyle.PADDING * 4;
      menuDrawContext.drawRoundedRect(
         floatValue2, y2, width, height2, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F), new ZenithColor("#FF97A0").method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_ICONS.getFont(5.0F);
      float width2 = font2.width("I");
      float width3 = font.width("Русская рулетка");
      float f2 = width2 + GuiStyle.PADDING.intValue() + width3;
      float floatValue3 = floatValue2 + (width - f2) / 2.0F;
      menuDrawContext.drawText(font2, "I", floatValue3, y2 + (height2 - font2.height()) / 2.0F, currentStyle.getTextEnable().method1447(previewAlpha));
      menuDrawContext.drawText(
         font, "Русская рулетка", floatValue3 + width2 + GuiStyle.PADDING.intValue(), y2 + (height2 - font.height()) / 2.0F, currentStyle.getTextEnable().method1447(previewAlpha)
      );
   }

   public void renderHeader(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float floatValue, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(
         floatValue, f, panelWidth, height, ProxyDotRadiusData.method8075(GuiStyle.ROUND.intValue()), currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
      );
      float x2 = floatValue + GuiStyle.PADDING * 2;
      float y2 = f + GuiStyle.PADDING * 2;
      float width = 13.0F;
      RenderShaders.method5294(
         MatrixUtils.method664(menuDrawContext.pose()),
         Zenith.method1501("icons/avatar.png"),
         x2,
         y2,
         width,
         width,
         ProxyDotRadiusData.method8069(4.0F),
         ZenithColor.INSTANCE.method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(5.0F);
      float floatValue2 = x2 + width + GuiStyle.PADDING.intValue();
      Zenith.Entry entry = Zenith.getInstance().getEntry();
      menuDrawContext.drawText(font, entry.getUsername(), floatValue2, y2 + 1.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawText(font2, "uid: " + entry.getText2(), floatValue2, y2 + width - font2.height() - 1.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
      Font font3 = Fonts.NEW_ICONS.getFont(4.0F);
      this.exitBounds = new BoundsData(floatValue + panelWidth - font3.width("2") - GuiStyle.PADDING * 2, y2 + 2.0F, 5.0F, 5.0F);
      menuDrawContext.drawText(font3, "2", floatValue + panelWidth - font3.width("2") - GuiStyle.PADDING * 2, y2 + 2.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   public void renderTill(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float floatValue, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(floatValue, f, panelWidth, height, ProxyDotRadiusData.INSTANCE, currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha));
      float floatValue2 = floatValue + GuiStyle.PADDING * 2;
      float centeredTextY = f + GuiStyle.PADDING * 2;
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      Zenith.Entry entry = Zenith.getInstance().getEntry();
      float height2 = 1.4F;
      long l;
      float f2;
      String str;
      if ((l = entry.method1506()) == Long.MAX_VALUE) {
         f2 = 1.0F;
         str = Zenith.getInstance().getLocalization().translate("gui.sub.lifetime");
      } else if (l == -1L) {
         f2 = 0.0F;
         str = Zenith.getInstance().getLocalization().translate("gui.sub.active");
      } else {
         f2 = Math.min(1.0F, Math.max(0.0F, (float)l / 365.0F));
         str = l + " day";
      }

      menuDrawContext.drawText(font, "Active: " + str, floatValue2, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      Font font2 = Fonts.NEW_ICONS.getFont(5.0F);
      float width = font2.width("G");
      menuDrawContext.drawText(
         font,
         "Extend",
         floatValue + panelWidth - GuiStyle.PADDING * 2 - width - font.width("Extend") - GuiStyle.PADDING.intValue(),
         centeredTextY,
         currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawText(font2, "G", floatValue + panelWidth - GuiStyle.PADDING * 2 - width, centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      float animationPosY = f + height - GuiStyle.PADDING * 2 - height2;
      float width2;
      float width3 = (width2 = panelWidth - GuiStyle.PADDING * 4) * f2;
      menuDrawContext.drawRoundedRectBatched(
         floatValue2, animationPosY, width2, height2, ProxyDotRadiusData.method8069(0.01F), currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedRectBatched(floatValue2, animationPosY, width3, height2, ProxyDotRadiusData.method8069(0.01F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      menuDrawContext.flushRoundedRects();
   }

   public void renderSettings(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float floatValue, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(floatValue, f, panelWidth, height, ProxyDotRadiusData.INSTANCE, currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha));
      float floatValue2 = floatValue + GuiStyle.PADDING * 2;
      float centeredTextY = f + GuiStyle.PADDING * 2;
      Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
      Font font2 = Fonts.NEW_REGULAR.getFont(5.01F);
      menuDrawContext.drawText(font2, "Settings", floatValue2, centeredTextY, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      centeredTextY += font2.height() + GuiStyle.PADDING.intValue();
      boolean renderIcon = Zenith.getInstance().getNLMenuScreen().isRenderIcon();
      Zenith.getInstance().getNLMenuScreen().setRenderIcon(false);
      this.steamMode.render(menuDrawContext, x, y, floatValue2, centeredTextY, previewAlpha);
      float centeredTextY2 = centeredTextY + (this.steamMode.getAnimHeight() + GuiStyle.PADDING.intValue());
      this.discordRPC.render(menuDrawContext, x, y, floatValue2, centeredTextY2, previewAlpha);
      Zenith.getInstance().getNLMenuScreen().setRenderIcon(renderIcon);
      float height2 = this.steamMode.getHeight();
      float f2 = centeredTextY2 + this.discordRPC.getAnimHeight() + GuiStyle.PADDING.intValue();
      String text = "A";
      float intValue = GuiStyle.PADDING.intValue();
      float width = 6.0F;
      float f3 = 2.0F;
      Font font3 = Fonts.ICONS.getFont(5.01F);
      float width2 = font3.width(text);
      float languageBlockWidth = this.getLanguageBlockWidth(font2, width, f3);
      float floatValue3;
      float x2;
      float floatValue4 = (x2 = (floatValue3 = floatValue + panelWidth - GuiStyle.PADDING.intValue() * 2.0F - width2) - intValue - languageBlockWidth) - intValue - width2;
      float centeredTextY3 = f2 + (height2 - font.height()) / 2.0F;
      float centeredTextY4;
      float centeredTextY5 = (centeredTextY4 = f2 + (height2 - font3.height()) / 2.0F) + 0.9F;
      float y2 = f2 + 1.4F;
      float floatValue5 = x2 + width + f3;
      float centeredTextY6 = f2 + (height2 - font2.height()) / 2.0F;
      float f4 = 2.0F;
      this.languageLeftBounds = new BoundsData(floatValue4 - f4, f2 - f4, width2 + f4 * 2.0F, height2 + f4 * 2.0F);
      this.languageRightBounds = new BoundsData(floatValue3 - f4, f2 - f4, width2 + f4 * 2.0F, height2 + f4 * 2.0F);
      boolean contains = this.languageLeftBounds.contains(x, y);
      boolean flag = this.languageRightBounds.contains(x, y);
      this.languageLeftHoverAnimation.animateTo(contains ? 1.0F : 0.0F);
      this.languageRightHoverAnimation.animateTo(flag ? 1.0F : 0.0F);
      String languageName = Zenith.getInstance().getLocalization().getLanguageName();
      this.syncDisplayedLanguage(languageName);
      menuDrawContext.drawText(font, "Language", floatValue2, centeredTextY3, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      this.drawLanguageArrow(menuDrawContext, font3, text, floatValue4, centeredTextY5, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha), 180.0F);
      if (this.languageSwitchActive) {
         float clamp01 = this.clamp01(this.languageSwitchAnimation.update());
         float f5 = Math.max(8.0F, languageBlockWidth + intValue);
         float f6 = -this.languageSwitchDirection * clamp01 * f5;
         float f7 = this.languageSwitchDirection * (1.0F - clamp01) * f5;
         menuDrawContext.enableScissor(x2 - 1.0F, f2 - f4, x2 + languageBlockWidth + 1.0F, f2 + height2 + f4);
         this.drawLanguageValue(menuDrawContext, font2, this.outgoingLanguageName, x2 + f6, y2, floatValue5 + f6, centeredTextY6, width, previewAlpha, currentStyle);
         this.drawLanguageValue(menuDrawContext, font2, this.displayedLanguageName, x2 + f7, y2, floatValue5 + f7, centeredTextY6, width, previewAlpha, currentStyle);
         menuDrawContext.disableScissor();
         if (this.languageSwitchAnimation.isDone() || clamp01 >= 0.999F) {
            this.languageSwitchActive = false;
            this.outgoingLanguageName = null;
         }
      } else {
         this.drawLanguageValue(menuDrawContext, font2, this.displayedLanguageName, x2, y2, floatValue5, centeredTextY6, width, previewAlpha, currentStyle);
      }

      this.drawLanguageArrow(menuDrawContext, font3, text, floatValue3, centeredTextY4, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha), 0.0F);
   }

   private void drawLanguageArrow(MenuDrawContext menuDrawContext, Font font, String text, float floatValue, float centeredTextY, ZenithColor zenithColor, float angdeg) {
      float x = floatValue + font.width(text) / 2.0F;
      float y = centeredTextY + font.height() / 2.0F;
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(x, y);
      menuDrawContext.pose().rotate((float)Math.toRadians(angdeg));
      menuDrawContext.pose().translate(-x, -y);
      menuDrawContext.drawText(font, text, floatValue, centeredTextY, zenithColor);
      menuDrawContext.popMatrix();
   }

   private void drawLanguageValue(
      MenuDrawContext menuDrawContext, Font font, String displayedLanguageName, float x, float y, float floatValue, float centeredTextY, float width, float previewAlpha, ZenithStyle currentStyle
   ) {
      String normalizeLanguageName = this.normalizeLanguageName(displayedLanguageName);
      RenderShaders.method5294(
         MatrixUtils.method664(menuDrawContext.pose()),
         Zenith.method1501(this.getLanguageFlagPath(normalizeLanguageName)),
         x,
         y,
         width,
         5.5F,
         ProxyDotRadiusData.method8069(1.0F),
         ZenithColor.INSTANCE.method7294(previewAlpha)
      );
      menuDrawContext.drawText(font, normalizeLanguageName.toUpperCase(Locale.ENGLISH), floatValue, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
   }

   private float getLanguageBlockWidth(Font font, float width, float f) {
      float f2 = Math.max(Math.max(font.width("RU"), font.width("EN")), Math.max(font.width("PL"), font.width("TR")));
      return width + f + f2;
   }

   private String getLanguageFlagPath(String normalizeLanguageName) {
      String str = this.normalizeLanguageName(normalizeLanguageName);

      return switch (str) {
         case "ru" -> "icons/russia.png";
         case "pl" -> "icons/poland.png";
         case "tr" -> "icons/turkey.png";
         default -> "icons/usa.png";
      };
   }

   private void syncDisplayedLanguage(String displayedLanguageName) {
      if (this.displayedLanguageName == null) {
         this.displayedLanguageName = displayedLanguageName;
      } else {
         if (!this.languageSwitchActive && !this.sameLanguage(this.displayedLanguageName, displayedLanguageName)) {
            this.displayedLanguageName = displayedLanguageName;
         }
      }
   }

   private void switchLanguage(boolean flag) {
      String languageName = Zenith.getInstance().getLocalization().getLanguageName();
      Zenith.getInstance().getLocalization().method1730(flag);
      String displayedLanguageName = Zenith.getInstance().getLocalization().getLanguageName();
      if (!this.sameLanguage(languageName, displayedLanguageName)) {
         this.outgoingLanguageName = this.displayedLanguageName == null ? languageName : this.displayedLanguageName;
         this.displayedLanguageName = displayedLanguageName;
         this.languageSwitchDirection = flag ? 1 : -1;
         this.languageSwitchActive = true;
         this.languageSwitchAnimation.reset(0.0F);
         this.languageSwitchAnimation.setTarget(1.0F);
      }
   }

   private boolean sameLanguage(String displayedLanguageName, String str) {
      return this.normalizeLanguageName(displayedLanguageName).equals(this.normalizeLanguageName(str));
   }

   private String normalizeLanguageName(String displayedLanguageName) {
      return displayedLanguageName != null && !displayedLanguageName.isBlank() ? displayedLanguageName.trim().toLowerCase(Locale.ENGLISH) : "ru";
   }

   private float clamp01(float update) {
      return Math.max(0.0F, Math.min(1.0F, update));
   }

   public void renderInfo(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float floatValue, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(floatValue, f, panelWidth, height, ProxyDotRadiusData.INSTANCE, currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha));
      float floatValue2 = floatValue + GuiStyle.PADDING * 2;
      float centeredTextY = f + GuiStyle.PADDING * 2;
      Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font = Fonts.NEW_REGULAR.getFont(5.0F);
      menuDrawContext.drawText(font, "Info", floatValue2, centeredTextY, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      centeredTextY += font.height() + GuiStyle.PADDING.intValue();
      this.webProfile.render(menuDrawContext, x, y, floatValue2, centeredTextY, previewAlpha);
      centeredTextY += this.webProfile.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.openFolder.render(menuDrawContext, x, y, floatValue2, centeredTextY, previewAlpha);
   }

   @Override
   public boolean onMouseClicked(double anchorMouseX, double lastClickY, MouseButton enum2) {
      if (enum2.getGlfwButton() != 0 || !this.expanded) {
         return false;
      } else if (this.steamMode.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else if (this.discordRPC.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else if (this.webProfile.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else if (this.openFolder.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else {
         if (this.exitBounds != null && this.exitBounds.method8092(anchorMouseX, lastClickY, 2.0F)) {
            this.expanded = false;
         }

         if (this.languageLeftBounds != null && this.languageLeftBounds.contains(anchorMouseX, lastClickY)) {
            this.switchLanguage(false);
            return true;
         } else if (this.languageRightBounds != null && this.languageRightBounds.contains(anchorMouseX, lastClickY)) {
            this.switchLanguage(true);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public void toggleExpanded() {
      this.expanded = !this.expanded;
   }

   public boolean isRender() {
      return this.animationVisible.getValue() > 0.0F || this.expanded;
   }

   public float getAnimationProgress() {
      return this.animationVisible.getValue();
   }

   public void safe(JsonObject json) {
      json.addProperty("expanded", this.expanded);
      json.addProperty("language", Zenith.getInstance().getLocalization().getText());
      this.steamMode.getSetting().safe(json);
      this.discordRPC.getSetting().safe(json);
   }

   public void load(JsonObject jsonObject) {
      if (jsonObject.has("expanded")) {
         this.expanded = jsonObject.get("expanded").getAsBoolean();
      }

      if (jsonObject.has("language")) {
         Zenith.getInstance().getLocalization().method1728(jsonObject.get("language").getAsString());
      }

      if (jsonObject.has(this.steamMode.getSetting().getKey())) {
         this.steamMode.getSetting().load(jsonObject);
      }

      if (jsonObject.has(this.discordRPC.getSetting().getKey())) {
         this.discordRPC.getSetting().load(jsonObject);
      }
   }

   @Generated
   public Animation getAnimationVisible() {
      return this.animationVisible;
   }

   @Generated
   public boolean isExpanded() {
      return this.expanded;
   }

   @Generated
   public GuiBooleanSetting getSteamMode() {
      return this.steamMode;
   }

   @Generated
   public GuiBooleanSetting getDiscordRPC() {
      return this.discordRPC;
   }

   @Generated
   public GuiButtonSetting getWebProfile() {
      return this.webProfile;
   }

   @Generated
   public GuiButtonSetting getOpenFolder() {
      return this.openFolder;
   }

   @Generated
   public Animation getLanguageLeftHoverAnimation() {
      return this.languageLeftHoverAnimation;
   }

   @Generated
   public Animation getLanguageRightHoverAnimation() {
      return this.languageRightHoverAnimation;
   }

   @Generated
   public Animation getLanguageSwitchAnimation() {
      return this.languageSwitchAnimation;
   }

   @Generated
   public BoundsData getLanguageLeftBounds() {
      return this.languageLeftBounds;
   }

   @Generated
   public BoundsData getLanguageRightBounds() {
      return this.languageRightBounds;
   }

   @Generated
   public BoundsData getExitBounds() {
      return this.exitBounds;
   }

   @Generated
   public String getDisplayedLanguageName() {
      return this.displayedLanguageName;
   }

   @Generated
   public String getOutgoingLanguageName() {
      return this.outgoingLanguageName;
   }

   @Generated
   public int getLanguageSwitchDirection() {
      return this.languageSwitchDirection;
   }

   @Generated
   public boolean isLanguageSwitchActive() {
      return this.languageSwitchActive;
   }
}

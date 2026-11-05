package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import java.io.IOException;
import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiNumberSetting;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PlayTimeTracker;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ClientPanel extends Panel {
   private final Animation animationVisible = new Animation(300L, 0.0F, Easing.INSTANCE13);
   private boolean expanded = false;
   private final GuiNumberSetting guiScale = new GuiNumberSetting(
      new SliderSetting("panel.client.setting.guiScale", 100.0F, 100.0F, 250.0F, 5.0F, "", "%", () -> true, (f, f2) -> {}), 112.0F
   );
   private final GuiNumberSetting blurStrength = new GuiNumberSetting(
      new SliderSetting("panel.client.setting.backgroundBlur", 20.0F, 0.0F, 30.0F, 5.0F, "", "px"), 112.0F
   );
   private final GuiBooleanSetting renderDescription = new GuiBooleanSetting(new BooleanSetting("panel.client.setting.description", true), 112.0F);
   private final GuiBooleanSetting renderIcon = new GuiBooleanSetting(new BooleanSetting("panel.client.setting.settingIcon", true), 112.0F);
   private final GuiButtonSetting webProfile = new GuiButtonSetting(new ButtonSetting("panel.client.setting.telegram", "W", () -> {
      try {
         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://t.me/zenithdlcdevlog");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }), 112.0F);
   private final GuiButtonSetting openFolder = new GuiButtonSetting(new ButtonSetting("panel.client.setting.chat", "T", () -> {
      try {
         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://t.me/+J-lV24z-Wmg1M2Y6");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }), 112.0F);
   private BoundsData exitBounds;

   @Override
   public void render(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float moduleMenuX, float f) {
      this.animationVisible.method2807(this.expanded);
      this.animationVisible.setDuration(200L);
      previewAlpha *= this.animationVisible.getValue();
      float panelWidth = 128.0F;
      float height = 227.0F;
      menuDrawContext.enableScissor(moduleMenuX - GuiStyle.PADDING * 3, f, moduleMenuX + panelWidth + GuiStyle.PADDING * 4, f + height);
      moduleMenuX -= (panelWidth + GuiStyle.PADDING.intValue()) * (1.0F - this.animationVisible.getValue());
      if (Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F) {
         RenderShaders.method5287(
            MatrixUtils.method664(menuDrawContext.pose()),
            moduleMenuX,
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
      float height3 = 45.0F;
      float height4 = 67.0F;
      float height5 = 56.0F;
      float height6 = 30.0F;
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.renderHeader(menuDrawContext, x, y, previewAlpha, moduleMenuX, f, panelWidth, height2, currentStyle);
      this.renderTill(menuDrawContext, x, y, previewAlpha, moduleMenuX, f + height2, panelWidth, height3, currentStyle);
      this.renderSettings(menuDrawContext, x, y, previewAlpha, moduleMenuX, f + height2 + height3, panelWidth, height4, currentStyle);
      this.renderInfo(menuDrawContext, x, y, previewAlpha, moduleMenuX, f + height2 + height3 + height4, panelWidth, height5, currentStyle);
      this.renderButton(menuDrawContext, x, y, previewAlpha, moduleMenuX, f + height2 + height3 + height4 + height5, panelWidth, height6, currentStyle);
      menuDrawContext.disableScissor();
   }

   public void renderButton(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float moduleMenuX, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(
         moduleMenuX, f, panelWidth, height, ProxyDotRadiusData.method8077(GuiStyle.ROUND.intValue()), currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
      );
      float f2 = moduleMenuX + GuiStyle.PADDING * 2;
      float centeredTextY = f + GuiStyle.PADDING * 2;
      float f3 = 112.0F;
      float f4 = 14.0F;
      Font font = Fonts.NEW_REGULAR.getFont(5.0F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font3 = Fonts.NEW_ICONS.getFont(5.0F);
      float f5 = f3 / 3.0F;
      menuDrawContext.drawRoundedRect(
         f2 + f5, centeredTextY + 1.0F, 0.5F, f4 - 2.0F, ProxyDotRadiusData.INSTANCE, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedRect(
         f2 + f5 * 2.0F, centeredTextY + 1.0F, 0.5F, f4 - 2.0F, ProxyDotRadiusData.INSTANCE, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      this.renderBox(menuDrawContext, font, font2, font3, "Version", "P", "3.5", f2, centeredTextY, f5, f4, currentStyle, previewAlpha);
      this.renderBox(menuDrawContext, font, font2, font3, "Commit", "Q", "129", f2 + f5, centeredTextY, f5, f4, currentStyle, previewAlpha);
      this.renderBox(
         menuDrawContext,
         font,
         font2,
         font3,
         "Build",
         "R",
         Zenith.getInstance().getEntry().method1504().getName(),
         f2 + f5 + f5,
         centeredTextY,
         f5,
         f4,
         currentStyle,
         previewAlpha
      );
   }

   private void renderBox(
      MenuDrawContext menuDrawContext,
      Font font,
      Font font2,
      Font font3,
      String text,
      String str,
      String name,
      float f,
      float centeredTextY,
      float f2,
      float f3,
      ZenithStyle currentStyle,
      float previewAlpha
   ) {
      menuDrawContext.drawText(font, text, f + (f2 - font.width(text)) / 2.0F, centeredTextY, currentStyle.getTextSecondary().method1447(previewAlpha));
      float width;
      float f4 = (width = font3.width(str)) + GuiStyle.PADDING.intValue() / 2.0F + font2.width(name);
      menuDrawContext.drawText(
         font3,
         str,
         f + (f2 - f4) / 2.0F,
         centeredTextY + f3 - font3.height() - GuiStyle.PADDING.intValue() / 2.0F,
         currentStyle.getPrimaryColor().method1447(previewAlpha)
      );
      menuDrawContext.drawText(
         font2,
         name,
         f + (f2 - f4) / 2.0F + width + GuiStyle.PADDING.intValue() / 2.0F,
         centeredTextY + f3 - font2.height() - GuiStyle.PADDING.intValue() / 2.0F,
         currentStyle.getTextEnable().method1447(previewAlpha)
      );
   }

   public void renderHeader(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float moduleMenuX, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(
         moduleMenuX, f, panelWidth, height, ProxyDotRadiusData.method8075(GuiStyle.ROUND.intValue()), currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
      );
      float floatValue = moduleMenuX + GuiStyle.PADDING * 2;
      float f2 = f + GuiStyle.PADDING * 2;
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(5.0F);
      Zenith.getInstance().getEntry();
      menuDrawContext.drawText(font, "Client", floatValue, f2 + 1.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawText(font2, "Global system settings", floatValue, f2 + font.height() + 3.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
      Font font3 = Fonts.NEW_ICONS.getFont(4.0F);
      float floatValue2 = moduleMenuX + panelWidth - font3.width("2") - GuiStyle.PADDING * 2;
      float y2 = f2 + 2.0F;
      this.exitBounds = new BoundsData(floatValue2, y2, 5.0F, 5.0F);
      menuDrawContext.drawText(font3, "2", floatValue2, y2, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   public void renderTill(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float moduleMenuX, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(moduleMenuX, f, panelWidth, height, ProxyDotRadiusData.INSTANCE, currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha));
      float floatValue = moduleMenuX + GuiStyle.PADDING * 2;
      float centeredTextY = f + GuiStyle.PADDING * 2;
      Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font = Fonts.NEW_REGULAR.getFont(5.0F);
      menuDrawContext.drawText(font, "Navigation", floatValue, centeredTextY, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      centeredTextY += font.height() + GuiStyle.PADDING.intValue();
      this.webProfile.render(menuDrawContext, x, y, floatValue, centeredTextY, previewAlpha);
      centeredTextY += this.webProfile.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.openFolder.render(menuDrawContext, x, y, floatValue, centeredTextY, previewAlpha);
   }

   public void renderSettings(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float moduleMenuX, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(moduleMenuX, f, panelWidth, height, ProxyDotRadiusData.INSTANCE, currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha));
      float floatValue = moduleMenuX + GuiStyle.PADDING * 2;
      float centeredTextY = f + GuiStyle.PADDING * 2;
      Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font = Fonts.NEW_REGULAR.getFont(5.0F);
      menuDrawContext.drawText(font, "Settings", floatValue, centeredTextY, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      centeredTextY += font.height() + GuiStyle.PADDING * 2;
      this.guiScale.render(menuDrawContext, x, y, floatValue, centeredTextY, previewAlpha);
      centeredTextY += this.guiScale.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.blurStrength.render(menuDrawContext, x, y, floatValue, centeredTextY, previewAlpha);
      centeredTextY += this.blurStrength.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.renderDescription.render(menuDrawContext, x, y, floatValue, centeredTextY, previewAlpha);
      centeredTextY += this.renderDescription.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.renderIcon.render(menuDrawContext, x, y, floatValue, centeredTextY, previewAlpha);
   }

   public void renderInfo(MenuDrawContext menuDrawContext, int x, int y, float previewAlpha, float moduleMenuX, float f, float panelWidth, float height, ZenithStyle currentStyle) {
      menuDrawContext.drawRoundedRect(moduleMenuX, f, panelWidth, height, ProxyDotRadiusData.INSTANCE, currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha));
      float floatValue = moduleMenuX + GuiStyle.PADDING * 2;
      float centeredTextY = f + GuiStyle.PADDING * 2;
      float f2 = panelWidth - GuiStyle.PADDING * 4;
      Font font = Fonts.NEW_REGULAR.getFont(5.0F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
      String text = Zenith.getInstance().getLocalization().translate("panel.client.playtime");
      menuDrawContext.drawText(font, text, floatValue, centeredTextY, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      centeredTextY += font.height() + GuiStyle.PADDING.intValue();
      PlayTimeTracker playTimeTracker = Zenith.getInstance().getPlayTimeTracker();
      float f3 = font2.height() + GuiStyle.PADDING * 2;
      this.renderTimeRow(
         menuDrawContext,
         font2,
         font2,
         Zenith.getInstance().getLocalization().translate("panel.client.playtime.session"),
         PlayTimeTracker.method1402(playTimeTracker.method1399()),
         floatValue,
         centeredTextY,
         f2,
         currentStyle,
         previewAlpha
      );
      centeredTextY += f3;
      this.renderTimeRow(
         menuDrawContext,
         font2,
         font2,
         Zenith.getInstance().getLocalization().translate("panel.client.playtime.week"),
         PlayTimeTracker.method1402(playTimeTracker.method1400()),
         floatValue,
         centeredTextY,
         f2,
         currentStyle,
         previewAlpha
      );
      centeredTextY += f3;
      this.renderTimeRow(
         menuDrawContext,
         font2,
         font2,
         Zenith.getInstance().getLocalization().translate("panel.client.playtime.total"),
         PlayTimeTracker.method1402(playTimeTracker.method1401()),
         floatValue,
         centeredTextY,
         f2,
         currentStyle,
         previewAlpha
      );
   }

   private void renderTimeRow(
      MenuDrawContext menuDrawContext, Font font, Font font2, String text, String str, float floatValue, float centeredTextY, float f, ZenithStyle currentStyle, float previewAlpha
   ) {
      float intValue = GuiStyle.PADDING.intValue();
      float f2 = GuiStyle.PADDING.intValue() / 2.0F;
      float width = font2.width(str) + intValue * 2.0F;
      float height = font2.height() + f2 * 2.0F;
      float floatValue2 = floatValue + f - width;
      float y = centeredTextY - f2;
      menuDrawContext.drawRoundedRect(floatValue2, y, width, height, ProxyDotRadiusData.method8069(2.0F), GuiStyle.FIELD_SURFACE_BACKGROUND.method7294(previewAlpha));
      menuDrawContext.drawRoundedBorder(floatValue2, y, width, height, 0.5F, ProxyDotRadiusData.method8069(2.0F), GuiStyle.FIELD_BORDER.method7294(previewAlpha));
      menuDrawContext.drawText(font2, str, floatValue2 + intValue, centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawRoundedRect(
         floatValue, centeredTextY + font.height() / 2.0F - 0.6F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawText(font, text, floatValue + 7.0F, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
   }

   @Override
   public boolean onMouseClicked(double anchorMouseX, double lastClickY, Enum3 enum2) {
      if (enum2.getIntValue() != 0) {
         return false;
      } else if (this.expanded && this.exitBounds != null && this.exitBounds.method8092(anchorMouseX, lastClickY, 2.0F)) {
         this.expanded = false;
         return true;
      } else if (this.renderDescription.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else if (this.renderIcon.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else if (this.webProfile.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else if (this.guiScale.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
         return true;
      } else {
         return this.blurStrength.onMouseClicked(anchorMouseX, lastClickY, enum2) ? true : this.openFolder.onMouseClicked(anchorMouseX, lastClickY, enum2);
      }
   }

   @Override
   public boolean onMouseReleased(double x, double y, Enum3 enum2) {
      this.guiScale.onMouseReleased(x, y, enum2);
      this.blurStrength.onMouseReleased(x, y, enum2);
      return super.onMouseReleased(x, y, enum2);
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
      this.guiScale.getSetting().safe(json);
      this.blurStrength.getSetting().safe(json);
      this.renderDescription.getSetting().safe(json);
      this.renderIcon.getSetting().safe(json);
   }

   public void load(JsonObject jsonObject) {
      if (jsonObject.has("expanded")) {
         this.expanded = jsonObject.get("expanded").getAsBoolean();
      }

      if (jsonObject.has(this.guiScale.getSetting().getKey())) {
         this.guiScale.getSetting().load(jsonObject);
      }

      if (jsonObject.has(this.blurStrength.getSetting().getKey())) {
         this.blurStrength.getSetting().load(jsonObject);
      }

      if (jsonObject.has(this.renderDescription.getSetting().getKey())) {
         this.renderDescription.getSetting().load(jsonObject);
      }

      if (jsonObject.has(this.renderIcon.getSetting().getKey())) {
         this.renderIcon.getSetting().load(jsonObject);
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
   public GuiNumberSetting getGuiScale() {
      return this.guiScale;
   }

   @Generated
   public GuiNumberSetting getBlurStrength() {
      return this.blurStrength;
   }

   @Generated
   public GuiBooleanSetting getRenderDescription() {
      return this.renderDescription;
   }

   @Generated
   public GuiBooleanSetting getRenderIcon() {
      return this.renderIcon;
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
   public BoundsData getExitBounds() {
      return this.exitBounds;
   }

   @Generated
   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }
}

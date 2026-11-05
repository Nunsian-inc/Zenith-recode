package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiColorSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiItemSelectSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiKeySetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiModeSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiMultiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiNumberSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiStringSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiWindowSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiStyleElement extends Element {
   private static final float HEADER_HEIGHT = 23.0F;
   private static final int HEADER_SETTINGS_COUNT = 2;
   private final ZenithStyle style;
   private final Animation animationEnable = new Animation(200L, Easing.INSTANCE10);
   private final Animation expandedAnimation = new Animation(200L, Easing.INSTANCE10);
   private final Animation animationPosX = new Animation(150L, Easing.INSTANCE13);
   private final Animation animationPosY = new Animation(150L, Easing.INSTANCE13);
   private BoundsData bounds;
   private boolean expanded;
   private boolean animated = false;
   private boolean positionInitialized = false;
   private float lastX;
   private float lastY;
   private int lastIndex;
   private final List<GuiSetting<?>> settings = new ArrayList<>();

   public GuiStyleElement(ZenithStyle style) {
      this.style = style;
      float width = this.getWidth() - GuiStyle.PADDING * 4;

      for (Setting setting : style.getSettings()) {
         if (setting instanceof SliderSetting sliderSetting) {
            this.settings.add(new GuiNumberSetting(sliderSetting, width));
         } else if (setting instanceof ModeSetting modeSetting) {
            this.settings.add(new GuiModeSetting(modeSetting, width));
         } else if (setting instanceof MultiSelectSetting multiSelectSetting) {
            this.settings.add(new GuiMultiBooleanSetting(multiSelectSetting, width));
         } else if (setting instanceof BooleanSetting booleanSetting) {
            this.settings.add(new GuiBooleanSetting(booleanSetting, width));
         } else if (setting instanceof ColorSetting colorSetting) {
            this.settings.add(new GuiColorSetting(colorSetting, width));
         } else if (setting instanceof ButtonSetting buttonSetting) {
            this.settings.add(new GuiButtonSetting(buttonSetting, width));
         } else if (setting instanceof BlockListSetting blockListSetting) {
            this.settings.add(new GuiItemSelectSetting(blockListSetting, width));
         } else if (setting instanceof BindSetting bindSetting) {
            this.settings.add(new GuiKeySetting(bindSetting, width));
         } else if (setting instanceof TextSetting textSetting) {
            this.settings.add(new GuiStringSetting(textSetting, width));
         } else if (setting instanceof GroupSetting groupSetting) {
            this.settings.add(new GuiWindowSetting(groupSetting, width));
         }
      }
   }

   @Override
   public String getName() {
      return this.style.getName();
   }

   @Override
   public float getHeight() {
      this.expandedAnimation.method2807(this.expanded);
      float f = this.expandedAnimation.getValue();
      return 23.0F + this.getExpandedSettingsHeight() * f;
   }

   public boolean hasSettings() {
      return (!this.settings.isEmpty() && this.settings.stream().anyMatch(GuiSetting::isVisible));
   }

   @Override
   public float getWidth() {
      return 178.0F;
   }

   public boolean isPriority() {
      return (this.expandedAnimation.getValue() > 0.01F);
   }

   public boolean isEnable() {
      return (Zenith.getInstance().getStyleManager().getCurrentStyle() == this.style);
   }

   public void renderPriority(MenuDrawContext menuDrawContext, float x, float y, float f, float f2, float previewAlpha) {
      this.expandedAnimation.method2807(this.expanded);
      float f3 = this.expandedAnimation.getValue();
      this.renderHeaderSettingsPriority(menuDrawContext, x, y, f, f2, previewAlpha);
      if (!(f3 <= 0.01F)) {
         float f4 = f2 + 23.0F;
         float f5 = this.getExpandedSettingsHeight() * f3;
         float f6 = f + GuiStyle.PADDING * 2;
         float f7 = f2 + 23.0F + GuiStyle.PADDING * 2;
         float f8 = GuiStyle.PADDING * 2;
         float f9 = (float)(Mth.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * previewAlpha);

         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting setting = this.settings.get(i);
            if (setting.isVisible()) {
               float f10 = setting.getAnimHeight() + 6.0F * setting.getVisibleProgress();
               Mth.clamp((f5 - f8) / Math.max(f10, 1.0E-4F), 0.0F, 1.0F);
               setting.renderPriority(menuDrawContext, x, y, f6, f7, previewAlpha, f9);
               f7 += f10;
               f8 += f10;
            }
         }
      }
   }

   public void render(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float lastX, float lastY, float previewAlpha, int lastIndex) {
      if (!this.positionInitialized) {
         this.animationPosX.reset(lastX);
         this.animationPosY.reset(lastY);
         this.lastX = lastX;
         this.lastY = lastY;
         this.positionInitialized = true;
         this.lastIndex = lastIndex;
      } else if ((lastX != this.lastX || lastY != this.lastY) && lastIndex != this.lastIndex) {
         this.animated = true;
         this.animationPosX.method2810(lastX);
         this.animationPosY.method2810(lastY);
         this.lastX = lastX;
         this.lastY = lastY;
      }

      if (this.animated) {
         lastX = this.animationPosX.method2808(lastX);
         lastY = this.animationPosY.method2808(lastY);
         if (this.animationPosX.isDone() && this.animationPosY.isDone()) {
            this.animated = false;
         }
      } else {
         this.animationPosX.reset(lastX);
         this.animationPosY.reset(lastY);
      }

      this.lastIndex = lastIndex;
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.expandedAnimation.method2807(this.expanded);
         float f = this.expandedAnimation.getValue();
         this.bounds = new BoundsData(lastX, lastY, this.getWidth(), 23.0F);
         this.animationEnable.method2807((this.style == currentStyle));
         float width = this.bounds.width();
         float height = this.getHeight();
         menuDrawContext.drawRoundedRectBatched(
            lastX,
            lastY,
            width,
            height,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedRectBatched(
            lastX,
            lastY,
            width,
            23.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            currentStyle.getHeaderDisableBackground()
               .getZenithColor()
               .method7295(currentStyle.getSurfaceEnableBackground().getZenithColor(), this.animationEnable.getValue())
               .method7294(previewAlpha)
         );
         menuDrawContext.flushRoundedRects();
         float f2 = 0.5F;
         float f3 = 2.0F;
         float floatValue = 4.0F;
         menuDrawContext.enableScissor(lastX + floatValue, lastY + 23.0F - 2.0F, lastX + width - floatValue, lastY + 23.0F + 10.0F);
         menuDrawContext.drawRoundedBorder(
            lastX,
            lastY,
            width,
            23.0F,
            0.15F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            GlowColor.method8096(
               this.style.getPrimaryColor().method1447(previewAlpha),
               this.style.getPrimaryColor().method1447(previewAlpha),
               this.style.getSecondaryPrimaryColor().method1447(previewAlpha),
               this.style.getSecondaryPrimaryColor().method1447(previewAlpha)
            )
         );
         menuDrawContext.disableScissor();
         floatValue = lastX + GuiStyle.PADDING * 2 + Mth.lerpInt(this.animationEnable.getValue(), 0, 5 + GuiStyle.PADDING);
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font2 = Fonts.NEW_ICONS.getFont(6.0F);
         menuDrawContext.drawText(
            font,
            font.clip(this.style.getName(), lastX + 150.0F - GuiStyle.PADDING.intValue() - floatValue),
            floatValue,
            lastY + (23.0F - font.height()) / 2.0F,
            currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
         );
         menuDrawContext.drawText(
            font2,
            "`",
            lastX + GuiStyle.PADDING * 2,
            lastY + (23.0F - font2.height()) / 2.0F,
            ZenithColor.INSTANCE8.method7295(currentStyle.getPrimaryColor().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
         );
         this.renderHeaderSettings(menuDrawContext, lastClickX, lastClickY, lastX, lastY, previewAlpha);

         try {
            if (f <= 0.01F) {
               return;
            }

            float y = lastY + 23.0F;
            float f4 = this.getExpandedSettingsHeight() * f;
            float f5 = lastX + GuiStyle.PADDING * 2;
            float f6 = lastY + 23.0F + GuiStyle.PADDING * 2;
            float f7 = GuiStyle.PADDING * 2;
            float f8 = (float)(Mth.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * previewAlpha);
            menuDrawContext.enableScissor(lastX, y, lastX + width, y + f4);

            for (int i = 2; i < this.settings.size(); i++) {
               GuiSetting setting = this.settings.get(i);
               if (setting.isVisible()) {
                  float f9 = setting.getAnimHeight() + 6.0F * setting.getVisibleProgress();
                  float f10;
                  if ((f10 = Mth.clamp((f4 - f7) / Math.max(f9, 1.0E-4F), 0.0F, 1.0F)) > 0.0F) {
                     setting.render(menuDrawContext, lastClickX, lastClickY, f5, f6, f8 * f10);
                  }

                  f6 += f9;
                  f7 += f9;
               }
            }

            menuDrawContext.disableScissor();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public boolean onMousePriorityClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      byte b = 0;

      for (int i = 0; i < this.settings.size(); i++) {
         GuiSetting setting = this.settings.get(i);
         if (setting.isVisible() && setting.onMousePriorityClicked(lastClickX, lastClickY, enum2)) {
            b = 1;
         }
      }

      return (b != 0);
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      for (int i = Math.min(2, this.settings.size()) - 1; i >= 0; i--) {
         GuiSetting setting = this.settings.get(i);
         if (setting.isVisible() && setting.onMouseClicked(lastClickX, lastClickY, enum2)) {
            return true;
         }
      }

      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         if (enum2 == Enum3.LEFT) {
            Zenith.getInstance().getStyleManager().setCurrentStyle(this.style);
         } else if (enum2 == Enum3.RIGHT) {
            this.expanded = (!this.expanded);
         }

         return true;
      } else if (this.expandedAnimation.getValue() <= 0.01F) {
         return false;
      } else {
         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting setting = this.settings.get(i);
            if (setting.isVisible() && setting.onMouseClicked(lastClickX, lastClickY, enum2)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseScrolled(double d, double d2, double d3, double d4) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting setting = this.settings.get(i);
         if (setting.isVisible() && setting.onMousePriorityScroll(d, d2, d3, d4)) {
            return true;
         }
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         return false;
      } else {
         byte b = 0;

         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting setting = this.settings.get(i);
            if (setting.isVisible() && setting.onMousePriorityScroll(d, d2, d3, d4)) {
               b = 1;
            }
         }

         return (b != 0);
      }
   }

   @Override
   public void onMouseReleased(double d, double d2, Enum3 enum2) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         this.settings.get(i).onMouseReleased(d, d2, enum2);
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         super.onMouseReleased(d, d2, enum2);
      } else {
         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting setting = this.settings.get(i);
            setting.onMouseReleased(d, d2, enum2);
         }

         super.onMouseReleased(d, d2, enum2);
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting setting = this.settings.get(i);
         if (setting.isVisible() && setting.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
      } else {
         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting setting = this.settings.get(i);
            if (setting.isVisible() && setting.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
      }
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting setting = this.settings.get(i);
         if (setting.isVisible() && setting.charTyped(codepoint, n)) {
            return true;
         }
      }

      if (this.expandedAnimation.getValue() <= 0.01F) {
         return super.charTyped(new CharacterEvent(codepoint));
      } else {
         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting setting = this.settings.get(i);
            if (setting.isVisible() && setting.charTyped(codepoint, n)) {
               return true;
            }
         }

         return super.charTyped(new CharacterEvent(codepoint));
      }
   }

   private float getExpandedSettingsHeight() {
      if (!this.hasSettings()) {
         return 0.0F;
      } else {
         float f = 0.0F;
         int n = 0;

         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting setting = this.settings.get(i);
            if (setting.isVisible()) {
               f += setting.getAnimHeight() + 6.0F * setting.getVisibleProgress();
               n++;
            }
         }

         return n == 0 ? 0.0F : GuiStyle.PADDING * 2 + f - 6.0F + GuiStyle.PADDING * 2;
      }
   }

   private void renderHeaderSettings(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float lastX, float lastY, float previewAlpha) {
      float f = lastX + GuiStyle.PADDING * 2;
      float f2 = lastY + (23.0F - this.settings.getFirst().getHeight()) / 2.0F;
      menuDrawContext.enableScissor(
         lastX + 150.0F, 0.0F, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight()
      );
      GuiSetting setting = this.settings.get(0);
      setting.render(menuDrawContext, lastClickX, lastClickY, f - 10.0F, f2, previewAlpha);
      setting = this.settings.get(1);
      setting.render(menuDrawContext, lastClickX, lastClickY, f, f2, previewAlpha);
      menuDrawContext.disableScissor();
   }

   private void renderHeaderSettingsPriority(MenuDrawContext menuDrawContext, float x, float y, float f, float f2, float previewAlpha) {
      float f3 = (float)(Mth.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * previewAlpha);
      float f4 = f + GuiStyle.PADDING * 2;
      float f5 = f2 + 8.0F;

      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting setting = this.settings.get(i);
         if (setting.isVisible()) {
            setting.renderPriority(menuDrawContext, x, y, f4, f5, previewAlpha, f3);
         }
      }
   }

   @Generated
   public ZenithStyle getStyle() {
      return this.style;
   }

   @Generated
   public void setPositionInitialized(boolean positionInitialized) {
      this.positionInitialized = positionInitialized;
   }
}

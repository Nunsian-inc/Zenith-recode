package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
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
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiModuleElement extends Element {
   private final Module module;
   private final Animation animationEnable = new Animation(200L, Easing.INSTANCE10);
   private final Animation animationHovered = new Animation(200L, Easing.INSTANCE10);
   private final Animation bindAnimation = new Animation(300L, Easing.INSTANCE10);
   private final Animation searchVisibleAnimation = new Animation(180L, 1.0F, Easing.INSTANCE13);
   private float animationPosX = 0.0F;
   private float animationPosY = 0.0F;
   private BoundsData bounds;
   private BoundsData bindBounds;
   private boolean binding;
   private boolean animated = false;
   private boolean positionInitialized = false;
   private boolean searchVisible = true;
   private float lastX;
   private float lastY;
   private int lastIndex;
   private final List<GuiSetting<?>> settings = new ArrayList<>();

   public GuiModuleElement(Module moduleValue) {
      this.module = moduleValue;
      if (moduleValue.isEnabled()) {
         this.animationEnable.snapTo(1.0F);
      }

      if (moduleValue.isRenderSetting()) {
         for (Setting setting : moduleValue.getSettings()) {
            if (setting instanceof SliderSetting sliderSetting) {
               this.settings.add(new GuiNumberSetting(sliderSetting));
            } else if (setting instanceof ModeSetting modeSetting) {
               this.settings.add(new GuiModeSetting(modeSetting));
            } else if (setting instanceof MultiSelectSetting multiSelectSetting) {
               this.settings.add(new GuiMultiBooleanSetting(multiSelectSetting));
            } else if (setting instanceof BooleanSetting booleanSetting) {
               this.settings.add(new GuiBooleanSetting(booleanSetting));
            } else if (setting instanceof ColorSetting colorSetting) {
               this.settings.add(new GuiColorSetting(colorSetting));
            } else if (setting instanceof ButtonSetting buttonSetting) {
               this.settings.add(new GuiButtonSetting(buttonSetting));
            } else if (setting instanceof BlockListSetting blockListSetting) {
               this.settings.add(new GuiItemSelectSetting(blockListSetting));
            } else if (setting instanceof BindSetting bindSetting) {
               this.settings.add(new GuiKeySetting(bindSetting));
            } else if (setting instanceof TextSetting textSetting) {
               this.settings.add(new GuiStringSetting(textSetting));
            } else if (setting instanceof GroupSetting groupSetting) {
               this.settings.add(new GuiWindowSetting(groupSetting));
            }
         }
      }
   }

   @Override
   public String getName() {
      return this.module.getName();
   }

   @Override
   public float getHeight() {
      return (float)(
         23.0
            + (
               this.hasSettings()
                  ? GuiStyle.PADDING * 2
                     + this.settings.stream().filter(GuiSetting::isVisible).mapToDouble(setting -> setting.getAnimHeight() + 6.0F * setting.getVisibleProgress()).sum()
                     + -6.0
                     + GuiStyle.PADDING * 2
                  : 0.0
            )
      );
   }

   public boolean hasSettings() {
      return (!this.settings.isEmpty() && this.settings.stream().anyMatch(GuiSetting::isVisible));
   }

   @Override
   public float getWidth() {
      return 182.0F;
   }

   public boolean isEnable() {
      return this.module.isEnabled();
   }

   public void setSearchVisible(boolean searchVisible) {
      this.searchVisible = searchVisible;
   }

   public void resetSearchVisible(boolean searchVisible) {
      this.searchVisible = searchVisible;
      this.searchVisibleAnimation.snapTo(searchVisible ? 1.0F : 0.0F);
   }

   public float updateSearchVisible() {
      return this.searchVisibleAnimation.animateTo(this.searchVisible ? 1.0F : 0.0F);
   }

   public float getSearchVisibleProgress() {
      return this.searchVisibleAnimation.getValue();
   }

   public boolean shouldRenderInSearch() {
      return !(!this.searchVisible && !(this.searchVisibleAnimation.getValue() > 0.01F));
   }

   public void renderPriority(MenuDrawContext menuDrawContext, float x, float y, float f, float f2, float previewAlpha) {
      float f3 = f + GuiStyle.PADDING * 2;
      float f4 = f2 + 23.0F + GuiStyle.PADDING * 2;
      float f5 = (float)(Mth.lerp((double)this.animationEnable.getValue(), 0.5, 1.0) * previewAlpha);
      float f6 = 6.0F;

      for (GuiSetting setting : this.settings) {
         if (setting.isVisible()) {
            setting.renderPriority(menuDrawContext, x, y, f3, f4, previewAlpha, f5);
            f4 += setting.getAnimHeight() + f6 * setting.getVisibleProgress();
         }
      }
   }

   public void render(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float animationPosX, float animationPosY, float previewAlpha, int lastIndex) {
      this.render(menuDrawContext, lastClickX, lastClickY, animationPosX, animationPosY, previewAlpha, lastIndex, null);
   }

   public void render(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float animationPosX, float animationPosY, float previewAlpha, int lastIndex, BoundsData boundsData) {
      if (!this.positionInitialized) {
         this.animationPosX = animationPosX;
         this.animationPosY = animationPosY;
         this.lastX = animationPosX;
         this.lastY = animationPosY;
         this.positionInitialized = true;
         this.lastIndex = lastIndex;
      } else if ((animationPosX != this.lastX || animationPosY != this.lastY) && lastIndex != this.lastIndex) {
         this.animated = true;
         this.lastX = animationPosX;
         this.lastY = animationPosY;
      }

      if (this.animated) {
         this.animationPosX = Math.round(Mth.lerp(0.4F, this.animationPosX, animationPosX));
         this.animationPosY = Math.round(Mth.lerp(0.4F, this.animationPosY, animationPosY));
         if (Math.abs(this.animationPosX - animationPosX) < 2.0F && Math.abs(this.animationPosY - animationPosY) < 2.0F) {
            this.animated = false;
         } else {
            animationPosX = this.animationPosX;
            animationPosY = this.animationPosY;
         }
      } else {
         this.animationPosX = animationPosX;
         this.animationPosY = animationPosY;
      }

      this.lastIndex = lastIndex;
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.bounds = new BoundsData(animationPosX, animationPosY, this.getWidth(), 23.0F);
         float width = this.bounds.width();
         float height = this.getHeight();
         boolean enable = this.isEnable();
         this.animationEnable.updateVisibility(enable);
         int n = !this.bounds.contains(lastClickX, lastClickY) || boundsData != null && !boundsData.contains(lastClickX, lastClickY) ? 0 : 1;
         this.animationHovered.animateTo(enable ? 1.0F : (n != 0 ? 0.6F : 0.0F));
         float hover = this.animationHovered.getValue();
         float f = animationPosY + GuiStyle.PADDING * 2;
         float lerpInt = Mth.lerpInt(this.animationEnable.getValue(), 0, 5 + GuiStyle.PADDING);
         ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor();
         ZenithColor zenithColor3 = currentStyle.getTextEnable().getZenithColor();
         menuDrawContext.drawRoundedRectBatched(
            animationPosX,
            animationPosY,
            width,
            height,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedRectBatched(
            animationPosX,
            animationPosY,
            width,
            23.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            currentStyle.getHeaderDisableBackground()
               .getZenithColor()
               .method7295(currentStyle.getSurfaceEnableBackground().getZenithColor(), this.animationEnable.getValue())
               .method7294(previewAlpha)
         );
         menuDrawContext.flushRoundedRects();
         float floatValue = animationPosX + GuiStyle.PADDING * 2 + lerpInt;
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font2 = Fonts.NEW_ICONS.getFont(6.0F);
         float width2 = animationPosX + width - GuiStyle.PADDING * 3 - 12.0F - floatValue;
         menuDrawContext.drawText(
            font, font.clip(this.module.getName(), width2), floatValue, animationPosY + (23.0F - font.height()) / 2.0F, zenithColor2.method7295(zenithColor3, hover).method7294(previewAlpha)
         );
         menuDrawContext.drawText(
            font2,
            "O",
            animationPosX + GuiStyle.PADDING * 2,
            animationPosY + (23.0F - font2.height()) / 2.0F,
            ZenithColor.INSTANCE8.method7295(zenithColor, this.animationEnable.getValue()).method7294(previewAlpha)
         );
         float width3 = 12.0F;
         float height2 = 7.0F;
         float floatValue2 = animationPosX + width - GuiStyle.PADDING * 2 - width3;
         menuDrawContext.drawRoundedRectBatched(
            floatValue2,
            f,
            width3,
            height2,
            ProxyDotRadiusData.method8069(2.5F),
            currentStyle.getDisableActiveBg().getZenithColor().method7295(zenithColor, this.animationEnable.getValue()).method7294(previewAlpha)
         );
         float f2 = Mth.lerp(this.animationEnable.getValue(), 1.0F, width3 - 1.0F - 5.0F);
         menuDrawContext.drawRoundedRectBatched(
            floatValue2 + f2,
            f + 1.0F,
            5.0F,
            5.0F,
            ProxyDotRadiusData.method8069(1.5F),
            currentStyle.getTextTertiary().getZenithColor().method7295(zenithColor3, hover).method7294(previewAlpha)
         );
         String bindText = this.getBindText();
         this.bindAnimation.updateVisibility(!(!this.binding && this.module.getKeyCode() == -1));
         Font font3 = Fonts.NEW_MEDIUM.getFont(4.5F);
         Font font4 = Fonts.NEW_ICONS.getFont(4.2F);
         float width4 = font3.width(bindText);
         float width5 = GuiStyle.PADDING.intValue() / 2.0F + width4 + GuiStyle.PADDING.intValue() / 3.0F + 3.75F + GuiStyle.PADDING.intValue() / 2.0F;
         float floatValue3 = floatValue2 - GuiStyle.PADDING.intValue() - 7.0F - GuiStyle.PADDING.intValue() / 2.0F - width5;
         this.bindBounds = new BoundsData(floatValue3, f, width5, height2);
         menuDrawContext.drawRoundedRectBatched(
            floatValue3,
            f,
            width5,
            height2,
            ProxyDotRadiusData.method8069(1.5F),
            currentStyle.getDisableActiveBg().getZenithColor().method7295(zenithColor.method7294(0.15F), this.bindAnimation.getValue()).method7294(previewAlpha)
         );
         menuDrawContext.flushRoundedRects();
         menuDrawContext.drawText(
            font3,
            bindText,
            floatValue3 + GuiStyle.PADDING.intValue() / 2.0F,
            f + (height2 - font3.height()) / 2.0F,
            currentStyle.getTextTertiary().getZenithColor().method7295(zenithColor, this.bindAnimation.getValue()).method7294(previewAlpha)
         );
         menuDrawContext.drawText(
            font4,
            "N",
            floatValue3 + GuiStyle.PADDING.intValue() / 2.0F + width4 + GuiStyle.PADDING.intValue() / 3.0F,
            f + GuiStyle.PADDING.intValue() / 2.0F,
            currentStyle.getTextTertiary().getZenithColor().method7295(zenithColor, this.bindAnimation.getValue()).method7294(previewAlpha)
         );

         try {
            f2 = animationPosX + GuiStyle.PADDING * 2;
            float f3 = animationPosY + 23.0F + GuiStyle.PADDING * 2;
            float previewAlpha2 = Mth.lerp(this.animationEnable.getValue(), Mth.lerp(this.animationHovered.getValue(), 0.5F, 1.0F), 1.0F) * previewAlpha;
            width4 = 6.0F;

            for (GuiSetting setting : this.settings) {
               if (setting.isVisible()) {
                  setting.render(menuDrawContext, lastClickX, lastClickY, f2, f3, previewAlpha2);
                  f3 += setting.getAnimHeight() + width4 * setting.getVisibleProgress();
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public boolean onMousePriorityClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      byte b = 0;

      for (GuiSetting setting : this.settings) {
         if (setting.isVisible() && setting.onMousePriorityClicked(lastClickX, lastClickY, enum2)) {
            b = 1;
         }
      }

      return (b != 0);
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      if (this.bindBounds != null && this.bindBounds.contains(lastClickX, lastClickY)) {
         this.binding = true;
         return true;
      } else if (this.binding && enum2.getGlfwButton() > 2) {
         this.module.setKeyCode(enum2.getGlfwButton());
         this.binding = false;
         return true;
      } else if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         if (enum2 == MouseButton.LEFT || enum2 == MouseButton.RIGHT) {
            this.module.toggle();
         } else if (enum2 == MouseButton.MIDDLE) {
            this.binding = true;
         }

         return true;
      } else {
         for (GuiSetting setting : this.settings) {
            if (setting.isVisible() && setting.onMouseClicked(lastClickX, lastClickY, enum2)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseScrolled(double d, double d2, double d3, double d4) {
      byte b = 0;

      for (GuiSetting setting : this.settings) {
         if (setting.isVisible() && setting.onMousePriorityScroll(d, d2, d3, d4)) {
            b = 1;
         }
      }

      return (b != 0);
   }

   @Override
   public void onMouseReleased(double d, double d2, MouseButton enum2) {
      for (GuiSetting setting : this.settings) {
         setting.onMouseReleased(d, d2, enum2);
      }

      super.onMouseReleased(d, d2, enum2);
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (!this.binding) {
         for (GuiSetting setting : this.settings) {
            if (setting.isVisible() && setting.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
      } else {
         if (keyCode != 256 && keyCode != 261 && keyCode != 259) {
            this.module.setKeyCode(keyCode);
         } else {
            this.module.setKeyCode(-1);
         }

         this.binding = false;
         return true;
      }
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      for (GuiSetting setting : this.settings) {
         if (setting.isVisible() && setting.charTyped(codepoint, n)) {
            return true;
         }
      }

      return super.charTyped(new CharacterEvent(codepoint));
   }

   private String getBindText() {
      if (this.binding) {
         return this.getBindingDots();
      } else {
         String upperCase = "n/a";
         int keyCode;
         if ((keyCode = this.module.getKeyCode()) != -1 && keyCode != 0) {
            try {
               String str = KeyName.method6701(keyCode);
               if (str != null && !str.isBlank()) {
                  upperCase = str.toUpperCase();
               }
            } catch (Exception e) {
            }
         }

         return upperCase;
      }
   }

   private String getBindingDots() {
      int n;
      if ((n = (int)(System.currentTimeMillis() / 500L % 3L)) == 0) {
         return ".";
      } else {
         return n == 1 ? ".." : "...";
      }
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public void setPositionInitialized(boolean positionInitialized) {
      this.positionInitialized = positionInitialized;
   }
}

package org.zenith.client.screens.nlgui.elements.setting;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.input.KeyEvent;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
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
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiWindowSetting extends GuiSetting<GroupSetting> {
   private final Animation animationExpanded = new Animation(200L, Easing.INSTANCE13);
   private final List<GuiSetting<?>> renderableSettings = new ArrayList<>();
   private BoundsData bounds;
   private BoundsData rectBounds;
   private BoundsData exitBounds;
   private boolean expanded;

   public GuiWindowSetting(GroupSetting setting) {
      this(setting, 166.0F);
   }

   public GuiWindowSetting(GroupSetting setting, float width) {
      super(width, setting);
      width = 182.0F - GuiStyle.PADDING * 4;

      for (Setting setting2 : setting.getSettings()) {
         if (setting2 instanceof SliderSetting sliderSetting) {
            this.renderableSettings.add(new GuiNumberSetting(sliderSetting, width));
         } else if (setting2 instanceof ModeSetting modeSetting) {
            this.renderableSettings.add(new GuiModeSetting(modeSetting, width));
         } else if (setting2 instanceof MultiSelectSetting multiSelectSetting) {
            this.renderableSettings.add(new GuiMultiBooleanSetting(multiSelectSetting, width));
         } else if (setting2 instanceof BooleanSetting booleanSetting) {
            this.renderableSettings.add(new GuiBooleanSetting(booleanSetting, width));
         } else if (setting2 instanceof ColorSetting colorSetting) {
            this.renderableSettings.add(new GuiColorSetting(colorSetting, width));
         } else if (setting2 instanceof ButtonSetting buttonSetting) {
            this.renderableSettings.add(new GuiButtonSetting(buttonSetting, width));
         } else if (setting2 instanceof BlockListSetting blockListSetting) {
            this.renderableSettings.add(new GuiItemSelectSetting(blockListSetting, width));
         } else if (setting2 instanceof BindSetting bindSetting) {
            this.renderableSettings.add(new GuiKeySetting(bindSetting, width));
         } else if (setting2 instanceof TextSetting textSetting) {
            this.renderableSettings.add(new GuiStringSetting(textSetting, width));
         } else if (setting2 instanceof GroupSetting groupSetting) {
            this.renderableSettings.add(new GuiWindowSetting(groupSetting, width));
         }
      }
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         if (this.animationExpanded.getFloatValue2() == 0.0F) {
            this.expanded = true;
         } else {
            this.expanded = false;
         }

         return true;
      } else {
         return this.expanded;
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
         float width = 6.0F;
         float height = 6.0F;
         this.bounds = new BoundsData(floatValue + this.width - width, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
         menuDrawContext.drawRoundedRect(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            ProxyDotRadiusData.method8069(1.0F),
            currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            -0.5F,
            ProxyDotRadiusData.method8069(1.0F),
            currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha)
         );
         Font font3 = Fonts.NEW_ICONS.getFont(5.5F);
         menuDrawContext.drawText(
            font3,
            "w",
            this.bounds.getFloatValue() + (width - font3.width("w")) / 2.0F,
            this.bounds.getFloatValue2() + (height - font3.height()) / 2.0F,
            currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   @Override
   public void renderPriority(MenuDrawContext menuDrawContext, float x, float y, float f, float f2, float previewAlpha, float f3) {
      this.animationExpanded.updateVisibility(this.expanded);
      if (!(this.animationExpanded.getValue() <= 0.0F)) {
         float height = GuiStyle.PADDING * 4 + this.getHeight();
         float intValue = GuiStyle.PADDING.intValue();
         float width = 182.0F;
         float f4 = 0.0F;

         for (GuiSetting renderableSetting : this.renderableSettings) {
            if (renderableSetting.isVisible()) {
               f4 += renderableSetting.getAnimHeight() + 6.0F;
            }
         }

         if (f4 > 0.0F) {
            f4 -= 6.0F;
         }

         float height2 = height + intValue * 2.0F + f4;
         float floatValue = this.bounds.getFloatValue() + GuiStyle.PADDING * 2;
         float y2 = this.bounds.getFloatValue2() - height2 / 2.0F;
         this.rectBounds = new BoundsData(floatValue, y2, width, height2);
         previewAlpha *= this.animationExpanded.getValue();
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(this.bounds.getFloatValue(), this.bounds.getFloatValue2());
         menuDrawContext.pose().scale(this.animationExpanded.getValue(), this.animationExpanded.getValue());
         menuDrawContext.pose().translate(-this.bounds.getFloatValue(), -this.bounds.getFloatValue2());
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            RenderShaders.method5289(
               MatrixUtils.method664(menuDrawContext.pose()),
               floatValue,
               y2,
               width,
               height2,
               12.0F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               ZenithColor.INSTANCE.method7294(previewAlpha)
            );
            menuDrawContext.drawRoundedRect(
               floatValue,
               y2 + height,
               width,
               height2 - height,
               ProxyDotRadiusData.method8076(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
            );
            menuDrawContext.drawRoundedRect(
               floatValue,
               y2,
               width,
               height,
               ProxyDotRadiusData.method8074(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
            );
            Font font = Fonts.NEW_ICONS.getFont(4.0F);
            float floatValue2 = floatValue + width - font.width("2") - intValue * 2.0F;
            float y3 = y2 + intValue + font.height();
            this.exitBounds = new BoundsData(floatValue2, y3, 5.0F, 5.0F);
            menuDrawContext.drawText(font, "2", floatValue2, y3, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            Fonts.NEW_MEDIUM.getFont(5.5F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.4F);
            Font font3 = Fonts.NEW_REGULAR.getFont(5.3F);
            ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
            this.drawDefault(
               menuDrawContext,
               x,
               y,
               "w",
               this.setting.getName(),
               this.setting.getDescription(),
               font2,
               font3,
               floatValue + intValue * 2.0F,
               y2 + intValue * 2.0F,
               width / 2.0F,
               zenithColor,
               zenithColor2,
               zenithColor3
            );
            float f5 = floatValue + intValue * 2.0F;
            float f6 = y2 + height + intValue;

            for (GuiSetting renderableSetting : this.renderableSettings) {
               if (renderableSetting.isVisible()) {
                  renderableSetting.render(menuDrawContext, x, y, f5, f6, previewAlpha);
                  f6 += renderableSetting.getAnimHeight() + 6.0F;
               }
            }

            f6 = y2 + height + intValue;

            for (GuiSetting renderableSetting : this.renderableSettings) {
               if (renderableSetting.isVisible()) {
                  renderableSetting.renderPriority(menuDrawContext, x, y, f5, f6, previewAlpha, 1.0F);
                  f6 += renderableSetting.getAnimHeight() + 6.0F;
               }
            }

            menuDrawContext.pose().popMatrix();
         }
      }
   }

   @Override
   public boolean onMousePriorityClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      if (this.expanded && this.rectBounds != null) {
         byte b = 0;

         for (GuiSetting renderableSetting : this.renderableSettings) {
            if (renderableSetting.isVisible() && renderableSetting.onMousePriorityClicked(lastClickX, lastClickY, enum2)) {
               b = 1;
            }
         }

         if (b != 0) {
            return true;
         } else if (!this.rectBounds.contains(lastClickX, lastClickY)) {
            this.expanded = false;
            return false;
         } else if (this.exitBounds != null && this.exitBounds.method8092(lastClickX, lastClickY, 2.0F)) {
            this.expanded = false;
            return true;
         } else {
            for (GuiSetting renderableSetting : this.renderableSettings) {
               if (renderableSetting.isVisible() && renderableSetting.onMouseClicked(lastClickX, lastClickY, enum2)) {
                  System.out.println(renderableSetting);
                  return true;
               }
            }

            return true;
         }
      } else if (this.expanded) {
         this.expanded = false;
         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      for (GuiSetting renderableSetting : this.renderableSettings) {
         if (renderableSetting.isVisible() && renderableSetting.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
   }

   @Override
   public void onMouseReleased(double d, double d2, MouseButton enum2) {
      for (GuiSetting renderableSetting : this.renderableSettings) {
         if (renderableSetting.isVisible()) {
            renderableSetting.onMouseReleased(d, d2, enum2);
         }
      }

      super.onMouseReleased(d, d2, enum2);
   }

   @Override
   public boolean onMousePriorityScroll(double d, double d2, double d3, double d4) {
      if (!this.expanded) {
         return false;
      } else {
         this.expanded = false;

         for (GuiSetting renderableSetting : this.renderableSettings) {
            if (renderableSetting.isVisible() && renderableSetting.onMousePriorityScroll(d, d2, d3, d4)) {
               return true;
            }
         }

         return false;
      }
   }

   @Generated
   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }
}

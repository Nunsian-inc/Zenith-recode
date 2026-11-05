package org.zenith.client.screens.nlgui.elements.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiMultiBooleanSetting extends GuiSetting<MultiSelectSetting> {
   private final Animation animationExpanded = new Animation(200L, Easing.INSTANCE13);
   private final Animation textFadeAnimation = new Animation(130L, 1.0F, Easing.INSTANCE10);
   private final List<GuiMultiBooleanSetting.RenderValue> renderValues = new ArrayList<>();
   private BoundsData bounds;
   private BoundsData rectBounds;
   private boolean expanded;
   private String displayedValueText = "";
   private String pendingValueText = "";
   private boolean textSwitchPending;

   public GuiMultiBooleanSetting(MultiSelectSetting setting) {
      this(setting, 166.0F);
   }

   public GuiMultiBooleanSetting(MultiSelectSetting setting, float width) {
      super(width, setting);

      for (MultiSelectSetting.Entry entry : setting.getItems2()) {
         this.renderValues.add(new GuiMultiBooleanSetting.RenderValue(entry));
      }

      this.displayedValueText = this.collectCurrentValueText();
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         this.expanded = (!this.expanded);
         return true;
      } else if (this.expanded && this.rectBounds != null && this.rectBounds.contains(lastClickX, lastClickY)) {
         return true;
      } else {
         this.expanded = false;
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float centeredTextY, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(menuDrawContext, x, y, "v", this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, centeredTextY, maxWidth, zenithColor, zenithColor2, zenithColor3);
         float width = this.width / 2.0F;
         float height = this.getHeight();
         this.bounds = new BoundsData(floatValue + this.width - width, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
         menuDrawContext.drawRoundedRectBatched(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.flushRoundedRects();
         menuDrawContext.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            0.1F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha)
         );
         String currentValueText = this.collectCurrentValueText();
         if (this.displayedValueText.isEmpty()) {
            this.displayedValueText = currentValueText;
            this.textFadeAnimation.method2809(1.0F);
         }

         if (!this.textSwitchPending && !currentValueText.equals(this.displayedValueText)) {
            this.pendingValueText = currentValueText;
            this.textSwitchPending = true;
            this.textFadeAnimation.method2810(0.0F);
         } else if (this.textSwitchPending && !currentValueText.equals(this.pendingValueText)) {
            this.pendingValueText = currentValueText;
         }

         if (this.textSwitchPending && this.textFadeAnimation.update() <= 0.02F) {
            this.displayedValueText = this.pendingValueText;
            this.textSwitchPending = false;
            this.textFadeAnimation.method2809(0.0F);
            this.textFadeAnimation.method2810(1.0F);
         } else if (!this.textSwitchPending) {
            this.textFadeAnimation.method2808(1.0F);
         }

         Font font3 = Fonts.NEW_MEDIUM.getFont(5.3F);
         maxWidth = width - GuiStyle.PADDING * 2;
         float f = 0.4F;
         float previewAlpha2 = this.textFadeAnimation.getValue();
         MsdfRenderer.renderText(
            font3.getFont(),
            this.displayedValueText,
            font3.getSize(),
            zenithColor.method7294(previewAlpha2).method7287(),
            MatrixUtils.method663(menuDrawContext),
            this.bounds.getFloatValue() + GuiStyle.PADDING.intValue(),
            this.bounds.getFloatValue2() + (height - font3.height()) / 2.0F,
            0.0F,
            true,
            f,
            1.0F,
            maxWidth
         );
      }
   }

   @Override
   public void renderPriority(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float f, float f2, float previewAlpha, float f3) {
      previewAlpha *= this.animationExpanded.getValue() * this.animationVisible.getValue();
      float width = this.width / 2.0F;
      float height = this.getHeight();
      this.bounds = new BoundsData(f + this.width - width, f2 + (this.getHeight() - height) / 2.0F, width, height);
      this.animationExpanded.method2807(this.expanded);
      if (this.animationExpanded.getValue() > 0.0F) {
         this.getWidth();
         float height2 = GuiStyle.PADDING.intValue() / 2.0F + this.renderValues.size() * (10.0F + GuiStyle.PADDING.intValue() / 2.0F);
         float y = this.bounds.getFloatValue2() + height + GuiStyle.PADDING.intValue();
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(this.bounds.getFloatValue() + width / 2.0F, y);
         menuDrawContext.pose().scale(this.animationExpanded.getValue(), this.animationExpanded.getValue());
         menuDrawContext.pose().translate(-(this.bounds.getFloatValue() + width / 2.0F), -y);
         this.rectBounds = new BoundsData(this.bounds.getFloatValue(), y, width, height2);
         RenderShaders.method5289(
            MatrixUtils.method664(menuDrawContext.pose()),
            this.bounds.getFloatValue(),
            y,
            width,
            height2,
            12.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            ZenithColor.INSTANCE.method7294(previewAlpha)
         );
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            menuDrawContext.drawRoundedRectBatched(
               this.bounds.getFloatValue(),
               y,
               width,
               height2,
               ProxyDotRadiusData.method8069(4.0F),
               currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha)
            );
            menuDrawContext.flushRoundedRects();
            menuDrawContext.drawRoundedBorder(
               this.bounds.getFloatValue(),
               y,
               width,
               height2,
               0.1F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F)
            );
         }

         y += GuiStyle.PADDING.intValue() / 2.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(5.3F);
         Font font2 = Fonts.NEW_ICONS.getFont(5.01F);

         for (GuiMultiBooleanSetting.RenderValue renderValue : this.renderValues) {
            renderValue.addRectToBatch(menuDrawContext, lastClickX, lastClickY, this.bounds.getFloatValue(), y, previewAlpha);
            y += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         menuDrawContext.flushRoundedRects();
         float f4 = this.bounds.getFloatValue2() + height + GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() / 2.0F;

         for (GuiMultiBooleanSetting.RenderValue renderValue : this.renderValues) {
            renderValue.renderText(menuDrawContext, this.bounds.getFloatValue(), f4, previewAlpha, font, font2);
            f4 += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         menuDrawContext.pose().popMatrix();
      }
   }

   @Override
   public boolean onMousePriorityClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      if (this.expanded && this.rectBounds != null && this.rectBounds.contains(lastClickX, lastClickY) && enum2.getIntValue() == 0) {
         for (GuiMultiBooleanSetting.RenderValue renderValue : this.renderValues) {
            if (renderValue.onMouseClicked(lastClickX, lastClickY, enum2)) {
               return true;
            }
         }

         return true;
      } else if ((this.bounds == null || !this.bounds.contains(lastClickX, lastClickY)) && this.expanded) {
         this.expanded = false;
         return false;
      } else {
         return super.onMousePriorityClicked(lastClickX, lastClickY, enum2);
      }
   }

   @Override
   public float getHeight() {
      return 14.0F;
   }

   @Override
   public boolean onMousePriorityScroll(double d, double d2, double d3, double d4) {
      this.expanded = false;
      return false;
   }

   private String collectCurrentValueText() {
      String str = this.setting
         .getItems2()
         .stream()
         .filter(MultiSelectSetting.Entry::isEnabled)
         .map(MultiSelectSetting.Entry::getName)
         .collect(Collectors.joining(",  "));
      return str.isEmpty() ? "---------" : str;
   }

   public class RenderValue {
      private final MultiSelectSetting.Entry value;
      private final Animation animationEnable;
      private BoundsData bounds;

      public RenderValue(MultiSelectSetting.Entry entry) {
         this.value = entry;
         this.animationEnable = new Animation(200L, entry.isEnabled() ? 1.0F : 0.0F, Easing.INSTANCE10);
      }

      public void addRectToBatch(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float floatValue, float y, float previewAlpha) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            float width = GuiMultiBooleanSetting.this.width / 2.0F - GuiStyle.PADDING.intValue();
            this.bounds = new BoundsData(floatValue + GuiStyle.PADDING.intValue() / 2.0F, y, width, this.getHeight());
            this.animationEnable.method2808(this.value.isEnabled() ? 1.0F : (this.bounds.contains(lastClickX, lastClickY) ? 0.5F : 0.0F));
            menuDrawContext.drawRoundedRectBatched(
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               this.bounds.width(),
               this.bounds.height(),
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 4.0F),
               ZenithColor.INSTANCE8.method7295(currentStyle.getFieldSurfaceBackground().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
            );
         }
      }

      public void renderText(MenuDrawContext menuDrawContext, float floatValue, float f, float previewAlpha, Font font, Font font2) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            float f2 = GuiMultiBooleanSetting.this.width / 2.0F - GuiStyle.PADDING.intValue();
            float floatValue2 = floatValue + GuiStyle.PADDING.intValue();
            float width = floatValue + GuiStyle.PADDING.intValue() / 2.0F + f2 - font2.width("<") - GuiStyle.PADDING.intValue() - floatValue2;
            menuDrawContext.drawText(
               font,
               font.clip(this.value.getName(), width),
               floatValue2,
               f + (this.getHeight() - font.height()) / 2.0F,
               currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
            );
            menuDrawContext.drawText(
               font2,
               "<",
               floatValue + GuiStyle.PADDING.intValue() / 2.0F + f2 - font2.width("<") - GuiStyle.PADDING.intValue() / 2.0F,
               f + (this.getHeight() - font2.height()) / 2.0F,
               ZenithColor.INSTANCE8.method7295(currentStyle.getPrimaryColor().getZenithColor(), this.animationEnable.getValue()).method7294(previewAlpha)
            );
         }
      }

      public float getHeight() {
         return 10.0F;
      }

      public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
         if (this.bounds != null && this.bounds.method8092(lastClickX, lastClickY, GuiStyle.PADDING.intValue() / 2.0F)) {
            this.value.toggle();
            return true;
         } else {
            return false;
         }
      }
   }
}

package org.zenith.client.screens.nlgui.elements.setting;

import com.mojang.blaze3d.platform.InputConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiNumberSetting extends GuiSetting<SliderSetting> {
   private static final double HANDLE_GRAB = 4.0;
   private static final double FINE_FACTOR = 0.25;
   private static final long SHAKE_DURATION = 320L;
   private final Animation hoverAnimation = new Animation(160L, Easing.INSTANCE12);
   private final Animation dragAnimation = new Animation(160L, Easing.INSTANCE12);
   private final Animation focusAnimation = new Animation(180L, Easing.INSTANCE13);
   private final Animation numberAnimation;
   private final Animation boxWidthAnimation = new Animation(160L, -1.0F, Easing.INSTANCE12);
   private final Animation editAnimation = new Animation(220L, Easing.INSTANCE15);
   private final Animation commitFlash = new Animation(420L, Easing.INSTANCE12);
   private SearchBox valueBox;
   private BoundsData bounds;
   private BoundsData selectedBounds;
   private boolean selected;
   private boolean editing;
   private double anchorPercent;
   private double anchorMouseX;
   private boolean fineDrag;
   private long shakeStart = -1L;
   public float applayValue;

   public GuiNumberSetting(SliderSetting sliderSetting) {
      this(sliderSetting, 166.0F);
   }

   public GuiNumberSetting(SliderSetting sliderSetting, float width) {
      super(width, sliderSetting);
      this.numberAnimation = new Animation(220L, sliderSetting.getValue3(), Easing.INSTANCE27);
      this.applayValue = sliderSetting.getValue3();
   }

   public float getApplayValue() {
      return !this.selected && !this.editing ? this.setting.getValue3() : this.applayValue;
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double anchorMouseX, double lastClickY, Enum3 enum2) {
      if (enum2 != Enum3.LEFT) {
         return false;
      } else if (this.bounds != null && this.valueBox != null && this.bounds.method8092(anchorMouseX, lastClickY, 1.5F)) {
         if (!this.editing) {
            this.beginEditing();
         } else if (!this.valueBox.onMouseClicked(anchorMouseX, lastClickY, enum2)) {
            this.valueBox.setSelected(true);
         }

         return true;
      } else {
         if (this.editing) {
            this.commitEditing();
         }

         if (this.selectedBounds != null && this.selectedBounds.method8092(anchorMouseX, lastClickY, 3.0F)) {
            this.selected = true;
            this.fineDrag = this.isFineDrag();
            double pointerAt = this.pointerAt(anchorMouseX);
            double progress = this.getProgress(this.setting.getValue3());
            this.anchorPercent = Math.abs(pointerAt - progress) * this.selectedBounds.width() <= 4.0 ? progress : pointerAt;
            this.anchorMouseX = anchorMouseX;
            Zenith.getInstance().getNLMenuScreen().lockGuiScale();
            this.updateSlider(anchorMouseX);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void onMouseReleased(double x, double y, Enum3 enum2) {
      this.selected = false;
      if (this.editing) {
         this.updateInputPreview();
      } else {
         this.applayValue = this.setting.getValue3();
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (!this.editing) {
         return false;
      } else if (keyCode == 257 || keyCode == 335) {
         this.commitEditing();
         return true;
      } else if (keyCode == 256) {
         this.cancelEditing();
         return true;
      } else if (keyCode != 265 && keyCode != 264) {
         boolean keyPressed = this.valueBox.keyPressed(keyCode, keycode, modifiers);
         this.updateInputPreview();
         return keyPressed;
      } else {
         Float input = this.parseInput(this.valueBox.getText());
         float normalizeValue = input != null ? this.normalizeValue(input.floatValue()) : this.applayValue;
         float f = Math.abs(this.setting.getFloatValue2()) * ((modifiers & 1) != 0 ? 10.0F : 1.0F);
         float applayValue = this.normalizeValue(normalizeValue + (keyCode == 265 ? f : -f));
         String formatValue = this.formatValue(applayValue);
         this.valueBox.setText(formatValue);
         this.valueBox.setCursor(formatValue.length());
         this.valueBox.setFlag(false);
         this.applayValue = applayValue;
         return true;
      }
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      if (!this.editing) {
         return false;
      } else {
         boolean charTyped = this.valueBox.charTyped(codepoint, n);
         this.updateInputPreview();
         return charTyped;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float centeredTextY, float previewAlpha) {
      if (this.valueBox == null) {
         this.valueBox = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.5F), "", 40.0F);
         this.valueBox.setType(SearchBox.Type.DECIMAL_NUMBER);
         this.valueBox.setMaxLength(32);
      }

      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         if (!this.editing && !this.selected) {
            this.applayValue = this.setting.getValue3();
         }

         this.animationVisible.method2807(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(menuDrawContext, x, y, "u", this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, centeredTextY, maxWidth, zenithColor, zenithColor2, zenithColor3);
         float applayValue = this.editing ? this.applayValue : this.setting.getValue3();
         float f;
         if (this.selected) {
            this.numberAnimation.method2809(applayValue);
            f = applayValue;
         } else {
            f = this.numberAnimation.method2808(applayValue);
         }

         this.editAnimation.method2807(this.editing);
         this.commitFlash.method2808(0.0F);
         String text = this.editing ? this.valueBox.getText() : this.formatValue(this.selected ? applayValue : f);
         if (!this.editing) {
            this.valueBox.setText(text);
            this.valueBox.setCursor(text.length());
         }

         float width = font.width(this.setting.getSuffix());
         float f2 = Math.max(font.width(this.formatValue(this.setting.getMin())), font.width(this.formatValue(this.setting.getMax())));
         float f3;
         float f4 = Math.min(f3 = Math.max(12.0F, maxWidth - GuiStyle.PADDING.intValue() - 12.0F), GuiStyle.PADDING.intValue() + f2 + width + 1.0F);
         float floatValue2 = Math.min(f3, GuiStyle.PADDING.intValue() + Math.max(this.editing ? 8.0F : 4.0F, font.width(text)) + width + 1.0F);
         if (this.boxWidthAnimation.getValue() < 0.0F) {
            this.boxWidthAnimation.method2809(floatValue2);
         }

         float width2 = this.boxWidthAnimation.method2808(floatValue2);
         float height = 9.0F;
         float f5 = floatValue + this.width;
         float shakeOffset = this.shakeOffset();
         this.bounds = new BoundsData(f5 - width2, centeredTextY + (this.getHeight() - height) / 2.0F, width2, height);
         boolean flag = this.bounds.method8092(x, y, 1.5F);
         this.focusAnimation.method2807(this.editing || flag);
         float f6 = this.focusAnimation.getValue();
         float hover = this.editAnimation.getValue();
         float hover2 = this.commitFlash.getValue();
         float hover3 = Math.max(f6, hover2);
         ZenithColor zenithColor4 = currentStyle.getPrimaryColor().getZenithColor();
         if (hover3 > 0.01F) {
            ZenithColor zenithColor5 = currentStyle.getFieldSurfaceBackground()
               .getZenithColor()
               .method7295(zenithColor4.method7294(0.14F + 0.08F * hover), hover)
               .method7295(zenithColor4.method7294(0.4F), hover2)
               .method7294(previewAlpha * hover3);
            ZenithColor zenithColor6 = currentStyle.getFieldBorder().getZenithColor().method7295(zenithColor4.method7294(0.8F), hover).method7294(previewAlpha * hover3);
            float f7 = 0.85F + 0.15F * hover3;
            float width3 = width2 * f7;
            float floatValue3 = f5 - width3 + shakeOffset;
            menuDrawContext.drawRoundedRectBatched(floatValue3, this.bounds.getFloatValue2(), width3, height, ProxyDotRadiusData.method8069(1.5F), zenithColor5);
            menuDrawContext.flushRoundedRects();
            menuDrawContext.drawRoundedBorder(floatValue3, this.bounds.getFloatValue2(), width3, height, 0.1F, ProxyDotRadiusData.method8069(1.5F), zenithColor6);
            if (hover > 0.01F) {
               float width4 = (width3 - 3.0F) * hover;
               menuDrawContext.drawRoundedRectBatched(
                  floatValue3 + (width3 - width4) / 2.0F,
                  this.bounds.getFloatValue2() + height - 0.9F,
                  width4,
                  0.6F,
                  ProxyDotRadiusData.method8069(0.3F),
                  zenithColor4.method7294(previewAlpha * hover)
               );
               menuDrawContext.flushRoundedRects();
            }
         }

         float centeredTextY2 = this.bounds.getFloatValue2() + (height - font.height()) / 2.0F;
         float floatValue3 = f5 - GuiStyle.PADDING.intValue() / 2.0F - width + shakeOffset;
         float width3 = Math.max(1.0F, width2 - GuiStyle.PADDING.intValue() - width - 1.0F);
         float f7 = floatValue3 - font.width(text) - (width > 0.0F ? 0.6F : 0.0F);
         float f8 = this.bounds.getFloatValue() + GuiStyle.PADDING.intValue() / 2.0F + shakeOffset;
         float floatValue4 = Mth.lerp(hover, f7, f8);
         ZenithColor zenithColor5 = currentStyle.getTextSecondary()
            .getZenithColor()
            .method7295(currentStyle.getTextEnable().getZenithColor(), Math.max(Math.max(hover3, hover), this.dragAnimation.getValue()))
            .method7295(zenithColor4, hover2 * 0.6F)
            .method7294(previewAlpha);
         this.valueBox.setFont(font);
         this.valueBox.setWidth(width3);
         this.valueBox.method987(menuDrawContext, floatValue4, centeredTextY2, zenithColor5, zenithColor4.method7294(0.7F * previewAlpha));
         menuDrawContext.drawText(
            font,
            this.setting.getSuffix(),
            floatValue3,
            centeredTextY2,
            currentStyle.getTextTertiary().getZenithColor().method7295(currentStyle.getTextSecondary().getZenithColor(), hover3).method7294(previewAlpha)
         );
         float width4 = Math.max(1.0F, maxWidth - f4 - GuiStyle.PADDING.intValue());
         float height2 = 2.0F;
         float floatValue5 = floatValue + this.width - f4 - width4 - GuiStyle.PADDING.intValue();
         float animationPosY = centeredTextY + (this.getHeight() - height2) / 2.0F;
         this.selectedBounds = new BoundsData(floatValue5, animationPosY, width4, height2);
         this.updateSlider(x);
         this.hoverAnimation.method2807(this.selected || this.selectedBounds.method8092(x, y, 3.0F));
         this.dragAnimation.method2807(this.selected);
         float f9 = this.hoverAnimation.getValue();
         float f10 = this.dragAnimation.getValue();
         float progress = this.getProgress(this.selected ? this.applayValue : f);
         float width5 = width4 * progress;
         ZenithColor zenithColor6 = currentStyle.getFieldSurfaceBackground()
            .getZenithColor()
            .method7295(currentStyle.getFieldBorder().getZenithColor(), 0.35F * f9)
            .method7294(previewAlpha);
         ZenithColor zenithColor7 = zenithColor4.method7294(0.82F + 0.18F * Math.max(f9, f10)).method7294(previewAlpha);
         menuDrawContext.drawRoundedRectBatched(floatValue5, animationPosY, width4, height2, ProxyDotRadiusData.method8069(0.04F), zenithColor6);
         if (width5 > 0.01F) {
            menuDrawContext.drawRoundedRectBatched(floatValue5, animationPosY, width5, height2, ProxyDotRadiusData.method8078(0.04F, 0.04F), zenithColor7);
         }

         menuDrawContext.flushRoundedRects();
      }
   }

   private double pointerAt(double anchorMouseX) {
      return this.selectedBounds != null && !(this.selectedBounds.width() <= 0.0F)
         ? Mth.clamp((anchorMouseX - this.selectedBounds.getFloatValue()) / this.selectedBounds.width(), 0.0, 1.0)
         : 0.0;
   }

   private boolean isFineDrag() {
      return org.zenith.utility.misc.KeyCompat.isKeyDown(340) || org.zenith.utility.misc.KeyCompat.isKeyDown(344);
   }

   private double dragPercent(double anchorMouseX) {
      double d = this.fineDrag ? 0.25 : 1.0;
      return this.anchorPercent + (anchorMouseX - this.anchorMouseX) / this.selectedBounds.width() * d;
   }

   public void updateSlider(double anchorMouseX) {
      if (this.selected && this.selectedBounds != null && !(this.selectedBounds.width() <= 0.0F)) {
         boolean fineDrag;
         if ((fineDrag = this.isFineDrag()) != this.fineDrag) {
            this.anchorPercent = Mth.clamp(this.dragPercent(anchorMouseX), 0.0, 1.0);
            this.anchorMouseX = anchorMouseX;
            this.fineDrag = fineDrag;
         }

         double dragPercent;
         if ((dragPercent = this.dragPercent(anchorMouseX)) < 0.0 || dragPercent > 1.0) {
            this.anchorPercent = Mth.clamp(dragPercent, 0.0, 1.0);
            this.anchorMouseX = anchorMouseX;
         }

         double d = Mth.clamp(dragPercent, 0.0, 1.0);
         float normalizeValue = this.normalizeValue(this.setting.getMin() + (this.setting.getMax() - this.setting.getMin()) * d);
         this.applayValue = normalizeValue;
         if (Float.compare(this.setting.getValue3(), normalizeValue) != 0) {
            Zenith.getInstance().getClientSounds().method1571(this.getProgress(normalizeValue));
            this.setting.method2400(normalizeValue);
         }
      }
   }

   @Override
   public void renderPriority(MenuDrawContext menuDrawContext, float f, float f2, float f3, float f4, float f5, float f6) {
   }

   private void beginEditing() {
      this.editing = true;
      this.selected = false;
      String formatValue = this.formatValue(this.setting.getValue3());
      this.valueBox.setText(formatValue);
      this.valueBox.setCursor(formatValue.length());
      this.valueBox.setSelected(true);
      this.valueBox.setFlag(true);
      this.applayValue = this.setting.getValue3();
   }

   private void commitEditing() {
      String text = this.valueBox.getText();
      Float input = this.parseInput(text);
      if (input != null) {
         float normalizeValue = this.normalizeValue(input.floatValue());
         if (Float.compare(this.setting.getValue3(), normalizeValue) != 0) {
            this.setting.method2400(normalizeValue);
            Zenith.getInstance().getClientSounds().method1571(this.getProgress(normalizeValue));
         }

         this.commitFlash.method2809(1.0F);
         this.finishEditing();
      } else {
         if (text != null && !text.isBlank()) {
            this.shakeStart = System.currentTimeMillis();
         } else {
            this.finishEditing();
         }
      }
   }

   private void cancelEditing() {
      this.finishEditing();
   }

   private void finishEditing() {
      this.editing = false;
      this.valueBox.setSelected(false);
      this.valueBox.setFlag(false);
      this.applayValue = this.setting.getValue3();
      String formatValue = this.formatValue(this.setting.getValue3());
      this.valueBox.setText(formatValue);
      this.valueBox.setCursor(formatValue.length());
   }

   private float shakeOffset() {
      if (this.shakeStart < 0L) {
         return 0.0F;
      } else {
         long l;
         if ((l = System.currentTimeMillis() - this.shakeStart) >= 320L) {
            this.shakeStart = -1L;
            return 0.0F;
         } else {
            float f;
            return (float)Math.sin((f = (float)l / 320.0F) * Math.PI * 6.0) * 1.6F * (1.0F - f);
         }
      }
   }

   private void updateInputPreview() {
      this.sanitizeInput();
      Float input = this.parseInput(this.valueBox.getText());
      if (input != null) {
         this.applayValue = this.normalizeValue(input.floatValue());
      }
   }

   private void sanitizeInput() {
      String text = this.valueBox.getText();
      int n = Mth.clamp(this.valueBox.getCursor(), 0, text.length());
      int cursor = 0;
      boolean flag = false;
      StringBuilder builder = new StringBuilder(text.length());

      for (int i = 0; i < text.length(); i++) {
         char charAt;
         boolean digit = Character.isDigit(charAt = text.charAt(i));
         if ((charAt == '.' || charAt == ',') && !flag) {
            charAt = '.';
            flag = true;
            digit = true;
         } else if (charAt == '-' && builder.isEmpty() && this.setting.getMin() < 0.0F) {
            digit = true;
         }

         if (digit) {
            builder.append(charAt);
            if (i < n) {
               cursor++;
            }
         }
      }

      String string = builder.toString();
      if (!string.equals(text)) {
         this.valueBox.setText(string);
         this.valueBox.setCursor(cursor);
      }
   }

   private Float parseInput(String text) {
      if (text == null) {
         return null;
      } else {
         String str = text.trim().replace(',', '.');
         if (!str.isEmpty() && !str.equals("-") && !str.equals(".") && !str.equals("-.")) {
            Float f;
            try {
               float f2;
               f = Float.isFinite(f2 = Float.parseFloat(str)) ? f2 : null;
            } catch (NumberFormatException e) {
               return null;
            }

            return f;
         } else {
            return null;
         }
      }
   }

   private float normalizeValue(double floatValue) {
      double min = Math.min(this.setting.getMin(), this.setting.getMax());
      double max = Math.max(this.setting.getMin(), this.setting.getMax());
      double d = Math.abs(this.setting.getFloatValue2());
      double floatValue2 = floatValue;
      if (d > 0.0 && Double.isFinite(d)) {
         floatValue2 = BigDecimal.valueOf(Math.round((floatValue - min) / d) * d + min).setScale(this.getDecimals(), RoundingMode.HALF_UP).doubleValue();
      }

      return (float)Mth.clamp(floatValue2, min, max);
   }

   private float getProgress(float normalizeValue) {
      float f;
      return (f = this.setting.getMax() - this.setting.getMin()) == 0.0F ? 0.0F : Mth.clamp((normalizeValue - this.setting.getMin()) / f, 0.0F, 1.0F);
   }

   private String formatValue(float applayValue) {
      int decimals = this.getDecimals();
      double d = 0.5 * Math.pow(10.0, -decimals);
      return BigDecimal.valueOf(Math.abs(applayValue) < d ? 0.0 : applayValue).setScale(decimals, RoundingMode.HALF_UP).toPlainString();
   }

   private int getDecimals() {
      return Float.isFinite(this.setting.getFloatValue2()) && this.setting.getFloatValue2() != 0.0F
         ? Mth.clamp(new BigDecimal(Float.toString(Math.abs(this.setting.getFloatValue2()))).stripTrailingZeros().scale(), 0, 8)
         : 0;
   }
}

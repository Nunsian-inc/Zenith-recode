package org.zenith.client.screens.nlgui.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.Mth;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ColorSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ColorPicker {
   private static final float TOP_PAD = 4.0F;
   private static final float BOTTOM_PAD = 6.0F;
   private static final float GAP = 5.0F;
   private static final float BAR_GAP = 6.0F;
   private static final float SECTION_GAP = 6.0F;
   private static final float BAR_HEIGHT = 4.0F;
   private static final float ROW_HEIGHT = 11.0F;
   private static final float CAPTION_HEIGHT = 8.0F;
   private static final float BUTTON = 11.0F;
   private static final float BUTTON_GAP = 3.0F;
   private static final float SWATCH = 7.0F;
   private static final float SWATCH_GAP = 2.0F;
   private static final float MIN_FIELD = 30.0F;
   private static final float MAX_FIELD = 56.0F;
   private static final int PALETTE_LIMIT = 12;
   private static final int RECENT_LIMIT = 6;
   private static final List<ZenithColor> RECENT = new ArrayList<>();
   private final ColorSetting setting;
   private final List<ZenithColor> palette = new ArrayList<>();
   private final List<BoundsData> paletteBounds = new ArrayList<>();
   private SearchBox hexField;
   private float hue;
   private float saturation;
   private float brightness;
   private int alphaValue = 255;
   private ZenithColor applied;
   private ColorPicker.Drag drag = ColorPicker.Drag.NONE;
   private BoundsData bounds;
   private BoundsData fieldBounds;
   private BoundsData hueBounds;
   private BoundsData alphaBounds;
   private BoundsData hexBounds;
   private BoundsData copyBounds;
   private BoundsData pasteBounds;
   private BoundsData resetBounds;

   public ColorPicker(ColorSetting setting) {
      this.setting = setting;
      this.syncFromSetting();
   }

   public float height(float pickerWidth) {
      float paletteRows = this.paletteRows(pickerWidth);
      return 4.0F + this.fieldHeight(pickerWidth) + 5.0F + 4.0F + 6.0F + 4.0F + 6.0F + 11.0F + 6.0F + 8.0F + paletteRows * 7.0F + (paletteRows - 1.0F) * 2.0F + 6.0F;
   }

   public boolean contains(double lastClickX, double lastClickY) {
      return (this.bounds != null && this.bounds.contains(lastClickX, lastClickY));
   }

   public void remember() {
      remember(this.setting.getZenithColor());
      if (this.hexField != null) {
         this.hexField.setSelected(false);
      }

      this.drag = ColorPicker.Drag.NONE;
   }

   public void render(MenuDrawContext menuDrawContext, float mouseX, float mouseY, float floatValue, float y, float pickerWidth, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null && !(previewAlpha <= 0.001F) && !(pickerWidth <= 0.0F)) {
         this.ensureField();
         this.syncFromSetting();
         this.applyDrag(mouseX, mouseY);
         this.bounds = new BoundsData(floatValue, y, pickerWidth, this.height(pickerWidth));
         ZenithColor zenithColor = this.setting.getZenithColor();
         ZenithColor zenithColor2 = currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 4.0F);
         ZenithColor zenithColor3 = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor4 = currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha);
         float fieldHeight = this.fieldHeight(pickerWidth);
         float f = y + 4.0F;
         this.renderField(menuDrawContext, floatValue, f, pickerWidth, fieldHeight, zenithColor2, previewAlpha);
         float y2 = f + fieldHeight + 5.0F;
         this.renderHueBar(menuDrawContext, floatValue, y2, pickerWidth, previewAlpha);
         float y3 = y2 + 4.0F + 6.0F;
         this.renderAlphaBar(menuDrawContext, floatValue, y3, pickerWidth, zenithColor, previewAlpha);
         float y4 = y3 + 4.0F + 6.0F;
         this.renderHexRow(menuDrawContext, mouseX, mouseY, floatValue, y4, pickerWidth, currentStyle, zenithColor3, zenithColor4, zenithColor2, previewAlpha);
         float centeredTextY = y4 + 11.0F + 6.0F;
         this.renderCaption(menuDrawContext, floatValue, centeredTextY, pickerWidth, currentStyle, previewAlpha);
         this.renderPalette(menuDrawContext, floatValue, centeredTextY + 8.0F, pickerWidth, zenithColor, currentStyle, zenithColor2, previewAlpha);
         this.syncFromField(zenithColor);
      }
   }

   private void renderField(MenuDrawContext menuDrawContext, float floatValue, float y, float pickerWidth, float fieldHeight, ZenithColor zenithColor, float previewAlpha) {
      this.fieldBounds = new BoundsData(floatValue, y, pickerWidth, fieldHeight);
      ZenithColor zenithColor3 = ZenithColor.method7297(this.hue, 1.0F, 1.0F).method7294(previewAlpha);
      ZenithColor clientColor = ZenithColor.INSTANCE.method7294(previewAlpha);
      ZenithColor clientColor2 = ZenithColor.INSTANCE2.method7293(0);
      ZenithColor zenithColor2 = ZenithColor.INSTANCE2.method7294(previewAlpha);
      menuDrawContext.drawRoundedRect(floatValue, y, pickerWidth, fieldHeight, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(clientColor, clientColor, zenithColor3, zenithColor3));
      menuDrawContext.drawRoundedRect(floatValue, y, pickerWidth, fieldHeight, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(clientColor2, zenithColor2, clientColor2, zenithColor2));
      menuDrawContext.drawRoundedBorder(floatValue, y, pickerWidth, fieldHeight, 0.1F, ProxyDotRadiusData.INSTANCE, zenithColor);
      this.drawHandle(menuDrawContext, floatValue + pickerWidth * this.saturation, y + fieldHeight * (1.0F - this.brightness), previewAlpha);
   }

   private void renderHueBar(MenuDrawContext menuDrawContext, float floatValue, float y, float pickerWidth, float previewAlpha) {
      this.hueBounds = new BoundsData(floatValue, y, pickerWidth, 4.0F);
      float width = pickerWidth / 6.0F;

      for (int i = 0; i < 6; i++) {
         ZenithColor clientColor = ZenithColor.method7297(i / 6.0F, 1.0F, 1.0F).method7294(previewAlpha);
         ZenithColor zenithColor3 = ZenithColor.method7297((i + 1) / 6.0F, 1.0F, 1.0F).method7294(previewAlpha);
         menuDrawContext.drawRoundedRect(floatValue + i * width, y, width, 4.0F, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(clientColor, clientColor, zenithColor3, zenithColor3));
      }

      this.drawTick(menuDrawContext, floatValue + pickerWidth * this.hue, y, previewAlpha);
   }

   private void renderAlphaBar(MenuDrawContext menuDrawContext, float floatValue, float y, float pickerWidth, ZenithColor zenithColor, float previewAlpha) {
      this.alphaBounds = new BoundsData(floatValue, y, pickerWidth, 4.0F);
      this.drawChecker(menuDrawContext, floatValue, y, pickerWidth, 4.0F, previewAlpha);
      ZenithColor zenithColor3 = zenithColor.method7293(255).method7294(previewAlpha);
      ZenithColor clientColor = zenithColor.method7293(0);
      menuDrawContext.drawRoundedRect(floatValue, y, pickerWidth, 4.0F, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(clientColor, clientColor, zenithColor3, zenithColor3));
      this.drawTick(menuDrawContext, floatValue + pickerWidth * (this.alphaValue / 255.0F), y, previewAlpha);
   }

   private void renderHexRow(
      MenuDrawContext menuDrawContext,
      float mouseX,
      float mouseY,
      float floatValue,
      float y,
      float pickerWidth,
      ZenithStyle currentStyle,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor3,
      float previewAlpha
   ) {
      float f = 39.0F;
      float width = Math.max(20.0F, pickerWidth - f - 3.0F);
      this.hexBounds = new BoundsData(floatValue, y, width, 11.0F);
      menuDrawContext.drawRect(floatValue, y, width, 11.0F, currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 2.0F));
      menuDrawContext.drawRoundedBorder(
         floatValue,
         y,
         width,
         11.0F,
         0.1F,
         ProxyDotRadiusData.method8069(1.5F),
         this.hexField.isSelected() ? currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha) : zenithColor3
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
      float centeredTextY = y + (11.0F - font.height()) / 2.0F;
      float width2 = font.width("#");
      menuDrawContext.drawText(font, "#", floatValue + 4.0F, centeredTextY, zenithColor2);
      this.hexField.setWidth(width - 8.0F - width2);
      this.hexField.method987(menuDrawContext, floatValue + 4.0F + width2 + 1.0F, centeredTextY, zenithColor, zenithColor2);
      float floatValue2 = floatValue + pickerWidth - f;
      this.copyBounds = new BoundsData(floatValue2, y, 11.0F, 11.0F);
      this.pasteBounds = new BoundsData(floatValue2 + 11.0F + 3.0F, y, 11.0F, 11.0F);
      this.resetBounds = new BoundsData(floatValue2 + 28.0F, y, 11.0F, 11.0F);
      this.drawButton(menuDrawContext, this.copyBounds, MenuGlyph.COPY, mouseX, mouseY, currentStyle, previewAlpha);
      this.drawButton(menuDrawContext, this.pasteBounds, MenuGlyph.PASTE, mouseX, mouseY, currentStyle, previewAlpha);
      this.drawButton(menuDrawContext, this.resetBounds, MenuGlyph.RESET, mouseX, mouseY, currentStyle, previewAlpha);
   }

   private void renderCaption(MenuDrawContext menuDrawContext, float floatValue, float centeredTextY, float pickerWidth, ZenithStyle currentStyle, float previewAlpha) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
      ZenithColor zenithColor = currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.9F);
      String upperCase = Zenith.getInstance().getLocalization().translate("gui.picker.palette").toUpperCase(Locale.ROOT);
      menuDrawContext.drawText(font, HoverInfo.spaced(upperCase), floatValue, centeredTextY, zenithColor);
      String text = "A " + Math.round(this.alphaValue / 255.0F * 100.0F) + "%";
      menuDrawContext.drawText(font, text, floatValue + pickerWidth - font.width(text), centeredTextY, zenithColor);
   }

   private void renderPalette(MenuDrawContext menuDrawContext, float floatValue, float f, float pickerWidth, ZenithColor zenithColor, ZenithStyle currentStyle, ZenithColor zenithColor2, float previewAlpha) {
      this.buildPalette(currentStyle);
      this.paletteBounds.clear();
      int paletteColumns = this.paletteColumns(pickerWidth);

      for (int i = 0; i < this.palette.size(); i++) {
         float floatValue2 = floatValue + i % paletteColumns * 9.0F;
         float y = f + i / paletteColumns * 9.0F;
         this.paletteBounds.add(new BoundsData(floatValue2, y, 7.0F, 7.0F));
         ZenithColor zenithColor3 = this.palette.get(i);
         menuDrawContext.drawRect(floatValue2 + 0.5F, y + 0.5F, 6.0F, 6.0F, zenithColor3.method7294(previewAlpha));
         menuDrawContext.drawRoundedBorder(floatValue2, y, 7.0F, 7.0F, 0.1F, ProxyDotRadiusData.INSTANCE, zenithColor2);
         if (sameRgb(zenithColor3, zenithColor)) {
            menuDrawContext.drawRoundedBorder(
               floatValue2 - 1.5F, y - 1.5F, 10.0F, 10.0F, 0.1F, ProxyDotRadiusData.INSTANCE, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );
         }
      }
   }

   private void drawButton(MenuDrawContext menuDrawContext, BoundsData copyBounds, MenuGlyph menuGlyph, float mouseX, float mouseY, ZenithStyle currentStyle, float previewAlpha) {
      boolean contains;
      if (contains = copyBounds.contains(mouseX, mouseY)) {
         menuDrawContext.drawRect(
            copyBounds.getFloatValue(), copyBounds.getFloatValue2(), copyBounds.width(), copyBounds.height(), currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 3.0F)
         );
      }

      menuDrawContext.drawRoundedBorder(
         copyBounds.getFloatValue(),
         copyBounds.getFloatValue2(),
         copyBounds.width(),
         copyBounds.height(),
         0.1F,
         ProxyDotRadiusData.method8069(1.5F),
         currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 4.0F)
      );
      menuGlyph.draw(
         menuDrawContext,
         copyBounds.getFloatValue() + (copyBounds.width() - 6.0F) / 2.0F,
         copyBounds.getFloatValue2() + (copyBounds.height() - 6.0F) / 2.0F,
         (contains ? currentStyle.getTextEnable() : currentStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
      );
   }

   private void drawHandle(MenuDrawContext menuDrawContext, float f, float f2, float previewAlpha) {
      drawFrame(menuDrawContext, f - 3.5F, f2 - 3.5F, 7.0F, 7.0F, ZenithColor.INSTANCE2.method7294(0.45F * previewAlpha));
      drawFrame(menuDrawContext, f - 2.5F, f2 - 2.5F, 5.0F, 5.0F, ZenithColor.INSTANCE.method7294(previewAlpha));
   }

   private void drawTick(MenuDrawContext menuDrawContext, float f, float y, float previewAlpha) {
      menuDrawContext.drawRect(f - 1.25F, y - 2.0F, 2.5F, 8.0F, ZenithColor.INSTANCE2.method7294(0.5F * previewAlpha));
      menuDrawContext.drawRect(f - 0.75F, y - 1.5F, 1.5F, 7.0F, ZenithColor.INSTANCE.method7294(previewAlpha));
   }

   private void drawChecker(MenuDrawContext menuDrawContext, float floatValue, float y, float pickerWidth, float height, float previewAlpha) {
      float f = 2.0F;
      menuDrawContext.drawRect(floatValue, y, pickerWidth, height, ZenithColor.INSTANCE2.method7294(0.45F * previewAlpha));
      ZenithColor zenithColor = ZenithColor.INSTANCE.method7294(0.2F * previewAlpha);

      for (int i = 0; i * f < height; i++) {
         for (int j = 0; j * f < pickerWidth; j++) {
            if ((i + j) % 2 == 0) {
               float floatValue2 = floatValue + j * f;
               float y2 = y + i * f;
               menuDrawContext.drawRect(floatValue2, y2, Math.min(f, floatValue + pickerWidth - floatValue2), Math.min(f, y + height - y2), zenithColor);
            }
         }
      }
   }

   private static void drawFrame(MenuDrawContext menuDrawContext, float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      menuDrawContext.drawRect(floatValue, y, width, 1.0F, zenithColor);
      menuDrawContext.drawRect(floatValue, y + height - 1.0F, width, 1.0F, zenithColor);
      menuDrawContext.drawRect(floatValue, y, 1.0F, height, zenithColor);
      menuDrawContext.drawRect(floatValue + width - 1.0F, y, 1.0F, height, zenithColor);
   }

   public boolean onMouseClicked(double x, double y, MouseButton enum2) {
      if (this.bounds != null && this.bounds.contains(x, y)) {
         this.ensureField();
         this.hexField.setSelected(false);
         if (this.hexBounds != null && this.hexBounds.contains(x, y)) {
            this.hexField.onMouseClicked(x, y, enum2);
            this.hexField.setSelected(true);
            return true;
         } else {
            for (int i = 0; i < this.paletteBounds.size() && i < this.palette.size(); i++) {
               if (this.paletteBounds.get(i).contains(x, y)) {
                  this.adopt(this.palette.get(i).method7293(this.alphaValue));
                  this.commit();
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            if (this.copyBounds != null && this.copyBounds.contains(x, y)) {
               Minecraft.getInstance().keyboardHandler.setClipboard("#" + ColorUtils.method6434(this.setting.getZenithColor()));
               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            } else if (this.pasteBounds != null && this.pasteBounds.contains(x, y)) {
               this.paste();
               return true;
            } else if (this.resetBounds != null && this.resetBounds.contains(x, y)) {
               this.setting.reset();
               this.applied = null;
               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            } else {
               if (this.fieldBounds != null && this.fieldBounds.contains(x, y)) {
                  this.drag = ColorPicker.Drag.FIELD;
               } else if (this.hueBounds != null && this.hueBounds.method8092(x, y, 3.0F)) {
                  this.drag = ColorPicker.Drag.HUE;
               } else if (this.alphaBounds != null && this.alphaBounds.method8092(x, y, 3.0F)) {
                  this.drag = ColorPicker.Drag.ALPHA;
               }

               if (this.drag != ColorPicker.Drag.NONE) {
                  this.applyDrag((float)x, (float)y);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void onMouseReleased() {
      this.drag = ColorPicker.Drag.NONE;
   }

   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return (this.hexField != null && this.hexField.keyPressed(keyCode, keycode, modifiers));
   }

   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      return (this.hexField != null && this.hexField.charTyped(codepoint, n));
   }

   private void paste() {
      String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
      if (clipboard != null) {
         ZenithColor zenithColor = ColorUtils.method6433(clipboard.replace("#", "").trim(), null);
         if (zenithColor != null) {
            this.adopt(zenithColor.method7293(this.alphaValue));
            this.commit();
            Zenith.getInstance().getClientSounds().method1570(false);
         }
      }
   }

   private void ensureField() {
      if (this.hexField == null) {
         this.hexField = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.01F), "000000", 0.0F);
         this.hexField.setType(SearchBox.Type.ENGLISH_NUMBERS);
         this.hexField.setMaxLength(6);
      }
   }

   private float fieldHeight(float pickerWidth) {
      return Mth.clamp(pickerWidth * 0.44F, 30.0F, 56.0F);
   }

   private int paletteColumns(float pickerWidth) {
      return Math.max(1, (int)((pickerWidth + 2.0F) / 9.0F));
   }

   private int paletteRows(float pickerWidth) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle == null) {
         return 1;
      } else {
         this.buildPalette(currentStyle);
         int paletteColumns = this.paletteColumns(pickerWidth);
         return Math.max(1, (this.palette.size() + paletteColumns - 1) / paletteColumns);
      }
   }

   private void applyDrag(float mouseX, float mouseY) {
      if (this.drag != ColorPicker.Drag.NONE) {
         switch (this.drag) {
            case FIELD:
               if (this.fieldBounds == null) {
                  return;
               }

               this.saturation = Mth.clamp((mouseX - this.fieldBounds.getFloatValue()) / this.fieldBounds.width(), 0.0F, 1.0F);
               this.brightness = 1.0F - Mth.clamp((mouseY - this.fieldBounds.getFloatValue2()) / this.fieldBounds.height(), 0.0F, 1.0F);
               break;
            case HUE:
               if (this.hueBounds == null) {
                  return;
               }

               this.hue = Mth.clamp((mouseX - this.hueBounds.getFloatValue()) / this.hueBounds.width(), 0.0F, 1.0F);
               break;
            case ALPHA:
               if (this.alphaBounds == null) {
                  return;
               }

               this.alphaValue = Math.round(Mth.clamp((mouseX - this.alphaBounds.getFloatValue()) / this.alphaBounds.width(), 0.0F, 1.0F) * 255.0F);
               break;
            default:
               return;
         }

         this.commit();
      }
   }

   private void syncFromField(ZenithColor zenithColor) {
      if (this.hexField.isSelected()) {
         ZenithColor zenithColor2 = ColorUtils.method6433(this.hexField.getText(), null);
         if (zenithColor2 != null && !sameRgb(zenithColor2, zenithColor)) {
            this.adopt(zenithColor2.method7293(this.alphaValue));
            this.commit();
         }
      } else {
         this.hexField.setText(ColorUtils.method6434(zenithColor));
         this.hexField.setCursor(6);
      }
   }

   private void syncFromSetting() {
      ZenithColor zenithColor = this.setting.getZenithColor();
      if (this.applied == null || !zenithColor.equals(this.applied)) {
         this.adopt(zenithColor);
         this.applied = zenithColor;
      }
   }

   private void adopt(ZenithColor zenithColor) {
      float[] floats = Color.RGBtoHSB(zenithColor.getIntValue(), zenithColor.getIntValue2(), zenithColor.getIntValue3(), null);
      if (floats[1] > 0.0F) {
         this.hue = floats[0];
      }

      if (floats[2] > 0.0F) {
         this.saturation = floats[1];
      }

      this.brightness = floats[2];
      this.alphaValue = zenithColor.getIntValue4();
   }

   private void commit() {
      ZenithColor applied = ZenithColor.method7297(this.hue, this.saturation, this.brightness).method7293(this.alphaValue);
      this.setting.setZenithColor(applied);
      this.applied = applied;
   }

   private void buildPalette(ZenithStyle currentStyle) {
      this.palette.clear();
      this.addPaletteColor(currentStyle.getPrimaryColor().getZenithColor());
      this.addPaletteColor(currentStyle.getSecondaryPrimaryColor().getZenithColor());
      this.addPaletteColor(currentStyle.getFriendColor().getZenithColor());
      this.addPaletteColor(ZenithColor.INSTANCE);
      this.addPaletteColor(ZenithColor.INSTANCE2);

      for (ZenithColor zenithColor : RECENT) {
         this.addPaletteColor(zenithColor);
      }
   }

   private void addPaletteColor(ZenithColor zenithColor) {
      if (zenithColor != null && this.palette.size() < 12) {
         ZenithColor zenithColor2 = zenithColor.method7293(255);

         for (ZenithColor zenithColor3 : this.palette) {
            if (sameRgb(zenithColor3, zenithColor2)) {
               return;
            }
         }

         this.palette.add(zenithColor2);
      }
   }

   private static void remember(ZenithColor zenithColor) {
      if (zenithColor != null) {
         ZenithColor zenithColor2 = zenithColor.method7293(255);
         RECENT.removeIf(var1x -> sameRgb(var1x, zenithColor2));
         RECENT.add(0, zenithColor2);

         while (RECENT.size() > 6) {
            RECENT.remove(RECENT.size() - 1);
         }
      }
   }

   private static boolean sameRgb(ZenithColor zenithColor, ZenithColor zenithColor2) {
      return (zenithColor != null
            && zenithColor2 != null
            && zenithColor.getIntValue() == zenithColor2.getIntValue()
            && zenithColor.getIntValue2() == zenithColor2.getIntValue2()
            && zenithColor.getIntValue3() == zenithColor2.getIntValue3());
   }

   private static enum Drag {
      NONE,
      FIELD,
      HUE,
      ALPHA;
   }
}

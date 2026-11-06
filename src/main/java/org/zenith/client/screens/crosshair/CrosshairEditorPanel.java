package org.zenith.client.screens.crosshair;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Canvas;
import org.zenith.utility.misc.CrosshairPresets;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class CrosshairEditorPanel implements MinecraftAccess {
   public static final float WIDTH = 176.0F;
   private static final float TITLE_HEIGHT = 20.0F;
   private static final float TOOL_SIZE = 15.0F;
   private static final float TOOL_GAP = 3.0F;
   private static final float ROW_GAP = 6.0F;
   private static final float BUTTON_HEIGHT = 14.0F;
   private static final float MIN_CELL = 3.0F;
   private static final int TOOL_BRUSH = 0;
   private static final int TOOL_ERASER = 1;
   private static final int TOOL_FILL = 2;
   private static final int TOOL_PICKER = 3;
   private static final int TOOL_UNDO = 4;
   private static final int TOOL_REDO = 5;
   private static final String[][] TOOL_ICONS = new String[][]{
      {".....##", "....##.", "...##..", "..##...", ".##....", "##.....", "#......"},
      {"....##.", "...####", "..####.", ".####..", ".###...", ".##....", "......."},
      {"...#...", "..###..", ".#####.", "#######", "#######", ".#####.", "..###.."},
      {"....###", "....###", "...##..", "..##...", ".##....", "##.....", "#......"},
      {"..#....", ".##....", "###....", ".##....", "..#....", ".......", "......."},
      {"....#..", "....##.", "....###", "....##.", "....#..", ".......", "......."}
   };
   private static final int[] PALETTE = new int[]{-1, -4210753, -16777216, -48060, 40768, 57418, -11870592, -11876097, -6591489, 0};
   private final Animation visible = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final List<InspectorRow> rows = new ArrayList<>();
   private boolean open;
   private Canvas canvas;
   private int[] backup;
   private int tool = 0;
   private int brushColor = -1;
   private int brushSize = 1;
   private boolean painting;
   private boolean erasingStroke;
   private float mouseX;
   private float mouseY;
   private float cell = 4.6F;
   private BoundsData panelBounds;
   private BoundsData canvasBounds;
   private BoundsData clearBounds;
   private BoundsData saveBounds;
   private BoundsData cancelBounds;
   private BoundsData closeBounds;
   private BoundsData brushSliderBounds;
   private boolean draggingBrushSize;
   private final BoundsData[] toolBounds = new BoundsData[TOOL_ICONS.length];
   private final BoundsData[] presetBounds = new BoundsData[CrosshairPresets.texts.length];
   private final BoundsData[] paletteBounds = new BoundsData[PALETTE.length];
   private static int[][] presetPreviews;

   public boolean isOpen() {
      return this.open;
   }

   public boolean isVisible() {
      return this.open || this.visible.getValue() > 0.001F;
   }

   public void open(Canvas canvas, List<Setting> settings) {
      this.canvas = canvas;
      this.backup = canvas.method138();
      this.open = true;
      this.painting = false;
      this.rows.clear();

      for (Setting newNameSetting : settings) {
         this.rows.add(new InspectorRow(newNameSetting, 0));
      }
   }

   public void close() {
      this.open = false;
      this.painting = false;
      this.draggingBrushSize = false;
      this.panelBounds = null;
   }

   private void cancel() {
      if (this.canvas != null && this.backup != null) {
         this.canvas.method139(this.backup);
      }

      this.close();
   }

   public void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float width, float heightPanel, float previewAlpha, float f) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.visible.updateVisibility(this.open);
      float f2 = this.visible.getValue();
      if (currentStyle != null && !(f2 <= 0.001F) && this.canvas != null) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
         previewAlpha *= f2 * f * f;
         if (!(previewAlpha <= 0.01F)) {
            this.panelBounds = this.open ? new BoundsData(floatValue, y, width, heightPanel) : null;
            float floatValue2 = floatValue + GuiStyle.PADDING * 2;
            float f3 = width - GuiStyle.PADDING * 4;
            this.drawHeader(customDrawContext, floatValue, y, width, previewAlpha, currentStyle);
            InspectorRow.alignTracks(this.rows, width);
            float f4 = 72.0F + this.settingsHeight() + 14.0F + GuiStyle.PADDING.intValue() * 3.0F;
            float width2 = Math.min(f3, heightPanel - 20.0F - f4 - GuiStyle.PADDING.intValue() * 2.0F);
            this.cell = Math.max(3.0F, width2 / 31.0F);
            width2 = this.cell * 31.0F;
            float drawBrushSize = y + 20.0F;
            this.drawCanvas(customDrawContext, floatValue + (width - width2) / 2.0F, drawBrushSize, width2, previewAlpha, currentStyle);
            drawBrushSize += width2 + 6.0F;
            this.drawTools(customDrawContext, floatValue + (width - this.toolsWidth()) / 2.0F, drawBrushSize, previewAlpha, currentStyle);
            drawBrushSize += 21.0F;
            drawBrushSize = this.drawBrushSize(customDrawContext, floatValue2, drawBrushSize, f3, previewAlpha, currentStyle);
            drawBrushSize = this.drawPalette(customDrawContext, floatValue2, drawBrushSize, previewAlpha, currentStyle);
            this.drawPresets(customDrawContext, floatValue2, drawBrushSize, previewAlpha, currentStyle);
            drawBrushSize += 22.0F;
            this.drawSettings(customDrawContext, mouseX, mouseY, floatValue, drawBrushSize, width, previewAlpha);
            this.drawButtons(customDrawContext, floatValue, y + heightPanel - 14.0F - GuiStyle.PADDING * 2, width, previewAlpha, currentStyle);
            if (this.painting) {
               this.paintAt(mouseX, mouseY, this.erasingStroke);
            }
         }
      }
   }

   private float toolsWidth() {
      return TOOL_ICONS.length * 15.0F + (TOOL_ICONS.length - 1) * 3.0F;
   }

   private float settingsHeight() {
      float f = 0.0F;

      for (InspectorRow row : this.rows) {
         if (row.isVisible()) {
            f += row.height();
         }
      }

      return f;
   }

   private void drawHeader(MenuDrawContext customDrawContext, float floatValue, float y, float width, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
      float f = floatValue + GuiStyle.PADDING * 2;
      float f2 = y + GuiStyle.PADDING.intValue();
      customDrawContext.drawText(
         font,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.title"),
         f,
         f2 + 1.0F,
         currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
      );
      customDrawContext.drawText(
         font2,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.hint"),
         f,
         f2 + font.height() + 3.0F,
         currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      Font font3 = Fonts.NEW_ICONS.getFont(4.0F);
      float floatValue2 = floatValue + width - font3.width("2") - GuiStyle.PADDING * 2;
      float y2 = f2 + 2.0F;
      this.closeBounds = this.open ? new BoundsData(floatValue2, y2, 5.0F, 5.0F) : null;
      customDrawContext.drawText(font3, "2", floatValue2, y2, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   private void drawSettings(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float drawBrushSize, float width, float previewAlpha) {
      float y = drawBrushSize;

      for (InspectorRow row : this.rows) {
         if (row.isVisible()) {
            row.render(customDrawContext, mouseX, mouseY, floatValue, y, width, previewAlpha);
            y += row.height();
         }
      }
   }

   private void drawTools(MenuDrawContext customDrawContext, float f, float drawBrushSize, float previewAlpha, ZenithStyle currentStyle) {
      float width = 15.0F;

      for (int i = 0; i < TOOL_ICONS.length; i++) {
         float floatValue = f + i * (width + 3.0F);
         this.toolBounds[i] = this.open ? new BoundsData(floatValue, drawBrushSize, width, width) : null;
         boolean flag = i == this.tool && i < 4;
         boolean flag2 = i != 4 ? i != 5 || this.canvas.method143() : this.canvas.method142();
         ZenithColor zenithColor = flag
            ? currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.9F)
            : currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha * 0.75F);
         customDrawContext.drawRoundedRect(floatValue, drawBrushSize, width, width, ProxyDotRadiusData.method8069(4.0F), zenithColor);
         ZenithColor zenithColor2 = flag ? ZenithColor.INSTANCE.method7294(previewAlpha) : currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha * (flag2 ? 0.85F : 0.3F));
         this.drawIcon(customDrawContext, TOOL_ICONS[i], floatValue + 4.0F, drawBrushSize + 4.0F, 1.0F, zenithColor2);
      }
   }

   private void drawIcon(MenuDrawContext customDrawContext, String[] strings, float f, float f2, float cell, ZenithColor zenithColor) {
      customDrawContext.beginRectBatch();

      for (int i = 0; i < strings.length; i++) {
         String str = strings[i];

         for (int j = 0; j < str.length(); j++) {
            if (str.charAt(j) == '#') {
               customDrawContext.drawRectBatched(f + j * cell, f2 + i * cell, cell, cell, zenithColor);
            }
         }
      }

      customDrawContext.flushRects();
   }

   private void drawCanvas(MenuDrawContext customDrawContext, float floatValue, float drawBrushSize, float width, float previewAlpha, ZenithStyle currentStyle) {
      this.canvasBounds = this.open ? new BoundsData(floatValue, drawBrushSize, width, width) : null;
      customDrawContext.drawRoundedRect(
         floatValue - 2.0F,
         drawBrushSize - 2.0F,
         width + 4.0F,
         width + 4.0F,
         ProxyDotRadiusData.method8069(4.0F),
         currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha * 0.8F)
      );
      ZenithColor zenithColor = new ZenithColor(58, 58, 64, 255).method7294(previewAlpha);
      ZenithColor zenithColor2 = new ZenithColor(44, 44, 50, 255).method7294(previewAlpha);
      customDrawContext.beginRectBatch();

      for (int i = 0; i < 31; i++) {
         for (int j = 0; j < 31; j++) {
            customDrawContext.drawRectBatched(floatValue + j * this.cell, drawBrushSize + i * this.cell, this.cell, this.cell, (j + i & 1) == 0 ? zenithColor : zenithColor2);
         }
      }

      for (int i = 0; i < 31; i++) {
         for (int j = 0; j < 31; j++) {
            int shade = this.canvas.method136(j, i);
            if (shade >>> 24 != 0) {
               customDrawContext.drawRectBatched(floatValue + j * this.cell, drawBrushSize + i * this.cell, this.cell, this.cell, ZenithColor.method7291(shade).method7294(previewAlpha));
            }
         }
      }

      customDrawContext.flushRects();
      customDrawContext.drawRoundedBorder(
         floatValue + 15.0F * this.cell,
         drawBrushSize + 15.0F * this.cell,
         this.cell,
         this.cell,
         0.6F,
         ProxyDotRadiusData.method8069(1.0F),
         currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.9F)
      );
   }

   private float drawBrushSize(MenuDrawContext customDrawContext, float floatValue, float drawBrushSize, float f, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_REGULAR.getFont(4.4F);
      customDrawContext.drawText(
         font,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.brushSize"),
         floatValue,
         drawBrushSize,
         currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      float floatValue2 = floatValue + 52.0F;
      float width = Math.max(24.0F, f - 52.0F - 10.0F);
      float y = drawBrushSize + 2.0F;
      this.brushSliderBounds = this.open ? new BoundsData(floatValue2, y - 3.0F, width, 8.0F) : null;
      customDrawContext.drawRoundedRect(floatValue2, y, width, 2.0F, ProxyDotRadiusData.method8069(1.0F), currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha));
      float width2 = width * ((this.brushSize - 1) / 3.0F);
      customDrawContext.drawRoundedRect(floatValue2, y, width2, 2.0F, ProxyDotRadiusData.method8069(1.0F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      customDrawContext.drawRoundedRect(floatValue2 + width2 - 1.5F, y - 1.5F, 3.0F, 5.0F, ProxyDotRadiusData.method8069(1.5F), ZenithColor.INSTANCE.method7294(previewAlpha));
      customDrawContext.drawText(font, String.valueOf(this.brushSize), floatValue2 + width + 5.0F, drawBrushSize, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      return drawBrushSize + 13.0F;
   }

   private float drawPalette(MenuDrawContext customDrawContext, float floatValue, float drawBrushSize, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_REGULAR.getFont(4.4F);
      customDrawContext.drawText(
         font,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.brushColor"),
         floatValue,
         drawBrushSize,
         currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      float width = 10.0F;
      float f = floatValue;
      float y = drawBrushSize + 7.0F;

      for (int i = 0; i < PALETTE.length; i++) {
         float floatValue2 = f + i * (width + 2.5F);
         this.paletteBounds[i] = this.open ? new BoundsData(floatValue2, y, width, width) : null;
         int paletteColor = this.paletteColor(i);
         customDrawContext.drawRoundedRect(floatValue2, y, width, width, ProxyDotRadiusData.method8069(2.5F), ZenithColor.method7291(paletteColor).method7294(previewAlpha));
         if (paletteColor == this.brushColor) {
            customDrawContext.drawRoundedBorder(
               floatValue2 - 1.0F, y - 1.0F, width + 2.0F, width + 2.0F, 0.8F, ProxyDotRadiusData.method8069(3.5F), ZenithColor.INSTANCE.method7294(previewAlpha)
            );
         }
      }

      return drawBrushSize + 21.0F;
   }

   private int paletteColor(int n) {
      return PALETTE[n] != 0 ? PALETTE[n] : Zenith.getInstance().getStyleManager().getClientColor(0).method7287();
   }

   private static int[] presetPreview(int n) {
      if (presetPreviews == null) {
         int[][] intses = new int[CrosshairPresets.texts.length][];

         for (int i = 0; i < intses.length; i++) {
            intses[i] = CrosshairPresets.method5712(i);
         }

         presetPreviews = intses;
      }

      return presetPreviews[n];
   }

   private void drawPresets(MenuDrawContext customDrawContext, float floatValue, float drawBrushSize, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_REGULAR.getFont(4.4F);
      customDrawContext.drawText(
         font,
         Zenith.getInstance().getLocalization().translate("crosshair.editor.presets"),
         floatValue,
         drawBrushSize,
         currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      float width = 17.0F;
      float f = floatValue;
      float y = drawBrushSize + 7.0F;

      for (int i = 0; i < CrosshairPresets.texts.length; i++) {
         float floatValue2 = f + i * (width + 2.0F);
         this.presetBounds[i] = this.open ? new BoundsData(floatValue2, y, width, width) : null;
         customDrawContext.drawRoundedRect(floatValue2, y, width, width, ProxyDotRadiusData.method8069(3.0F), currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha * 0.8F));
         int[] ints = presetPreview(i);
         float f2 = (width - 3.0F) / 31.0F;
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha * 0.9F);
         customDrawContext.beginRectBatch();

         for (int j = 0; j < 31; j++) {
            for (int k = 0; k < 31; k++) {
               if (ints[j * 31 + k] >>> 24 != 0) {
                  customDrawContext.drawRectBatched(floatValue2 + 1.5F + k * f2, y + 1.5F + j * f2, Math.max(f2, 0.6F), Math.max(f2, 0.6F), zenithColor);
               }
            }
         }

         customDrawContext.flushRects();
      }
   }

   private void drawButtons(MenuDrawContext customDrawContext, float floatValue, float y, float width, float previewAlpha, ZenithStyle currentStyle) {
      float f = 4.0F;
      float f2 = width - GuiStyle.PADDING * 4;
      float width2 = (f2 - f * 2.0F) / 3.0F;
      float floatValue2 = floatValue + GuiStyle.PADDING * 2;
      this.clearBounds = this.open ? new BoundsData(floatValue2, y, width2, 14.0F) : null;
      this.saveBounds = this.open ? new BoundsData(floatValue2 + width2 + f, y, width2, 14.0F) : null;
      this.cancelBounds = this.open ? new BoundsData(floatValue2 + (width2 + f) * 2.0F, y, width2, 14.0F) : null;
      this.drawButton(customDrawContext, this.clearBounds, "crosshair.editor.clear", false, previewAlpha, currentStyle);
      this.drawButton(customDrawContext, this.saveBounds, "crosshair.editor.save", true, previewAlpha, currentStyle);
      this.drawButton(customDrawContext, this.cancelBounds, "crosshair.editor.cancel", false, previewAlpha, currentStyle);
   }

   private void drawButton(MenuDrawContext customDrawContext, BoundsData clearBounds, String text, boolean flag, float previewAlpha, ZenithStyle currentStyle) {
      if (clearBounds != null) {
         ZenithColor zenithColor = flag
            ? currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.95F)
            : currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha * 0.85F);
         customDrawContext.drawRoundedRect(clearBounds.getFloatValue(), clearBounds.getFloatValue2(), clearBounds.width(), clearBounds.height(), ProxyDotRadiusData.method8069(4.0F), zenithColor);
         Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
         String str = Zenith.getInstance().getLocalization().translate(text);
         customDrawContext.drawText(
            font,
            str,
            clearBounds.getFloatValue() + (clearBounds.width() - font.width(str)) / 2.0F,
            clearBounds.getFloatValue2() + (clearBounds.height() - font.height()) / 2.0F,
            (flag ? ZenithColor.INSTANCE : currentStyle.getTextEnable().getZenithColor()).method7294(previewAlpha)
         );
      }
   }

   public boolean onMouseClicked(double x, double y, MouseButton enum2) {
      if (this.open && this.canvas != null) {
         for (InspectorRow row : this.rows) {
            if (row.isVisible() && row.onMouseClicked(x, y, enum2)) {
               return true;
            }
         }

         if (hit(this.closeBounds, x, y) || hit(this.cancelBounds, x, y)) {
            this.cancel();
            return true;
         } else if (hit(this.saveBounds, x, y)) {
            this.close();
            return true;
         } else if (hit(this.clearBounds, x, y)) {
            this.canvas.clear();
            return true;
         } else if (hit(this.brushSliderBounds, x, y)) {
            this.draggingBrushSize = true;
            this.updateBrushSize(x);
            return true;
         } else {
            for (int i = 0; i < this.toolBounds.length; i++) {
               if (hit(this.toolBounds[i], x, y)) {
                  if (i == 4) {
                     this.canvas.undo();
                  } else if (i == 5) {
                     this.canvas.redo();
                  } else {
                     this.tool = i;
                  }

                  return true;
               }
            }

            for (int i = 0; i < this.paletteBounds.length; i++) {
               if (hit(this.paletteBounds[i], x, y)) {
                  this.brushColor = this.paletteColor(i);
                  if (this.tool == 1) {
                     this.tool = 0;
                  }

                  return true;
               }
            }

            for (int i = 0; i < this.presetBounds.length; i++) {
               if (hit(this.presetBounds[i], x, y)) {
                  this.canvas.method140();
                  this.canvas.method139(CrosshairPresets.method5712(i));
                  return true;
               }
            }

            if (hit(this.canvasBounds, x, y)) {
               boolean erasingStroke = enum2 == MouseButton.RIGHT;
               this.canvas.method140();
               this.painting = true;
               this.erasingStroke = erasingStroke;
               this.paintAt(x, y, erasingStroke);
               return true;
            } else {
               return hit(this.panelBounds, x, y);
            }
         }
      } else {
         return false;
      }
   }

   public void onMouseReleased(double x, double y, MouseButton enum2) {
      this.painting = false;
      this.draggingBrushSize = false;
      if (this.open) {
         for (InspectorRow row : this.rows) {
            row.onMouseReleased(x, y, enum2);
         }
      }
   }

   public boolean onMouseDragged(double offsetX, double offsetY) {
      if (!this.open) {
         return false;
      } else if (this.draggingBrushSize) {
         this.updateBrushSize(this.mouseX);
         return true;
      } else {
         for (InspectorRow row : this.rows) {
            if (row.isVisible() && row.onMouseDragged(this.mouseX, this.mouseY)) {
               return true;
            }
         }

         return this.painting;
      }
   }

   public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return !this.open ? false : hit(this.panelBounds, mouseX, mouseY);
   }

   public boolean charTyped(char codepoint, int n) {
      if (!this.open) {
         return false;
      } else {
         for (InspectorRow row : this.rows) {
            if (row.isVisible() && row.charTyped(codepoint, n)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.open && this.canvas != null) {
         for (InspectorRow row : this.rows) {
            if (row.isVisible() && row.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         boolean flag = (modifiers & 2) != 0;
         if (flag && keyCode == 90 && (modifiers & 1) != 0) {
            this.canvas.redo();
            return true;
         } else if (flag && keyCode == 90) {
            this.canvas.undo();
            return true;
         } else if (flag && keyCode == 89) {
            this.canvas.redo();
            return true;
         } else if (keyCode == 256) {
            this.cancel();
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private void updateBrushSize(double x) {
      if (this.brushSliderBounds != null) {
         float f = (float)(x - this.brushSliderBounds.getFloatValue()) / this.brushSliderBounds.width();
         this.brushSize = Mth.clamp(Math.round(1.0F + f * 3.0F), 1, 4);
      }
   }

   private void paintAt(double mouseX, double mouseY, boolean erasingStroke) {
      if (this.canvasBounds != null && this.canvasBounds.contains(mouseX, mouseY)) {
         int n = (int)((mouseX - this.canvasBounds.getFloatValue()) / this.cell);
         int n2 = (int)((mouseY - this.canvasBounds.getFloatValue2()) / this.cell);
         if (this.tool == 3) {
            int brushColor = this.canvas.method136(n, n2);
            if (brushColor >>> 24 != 0) {
               this.brushColor = brushColor;
               this.tool = 0;
            }

            this.painting = false;
         } else if (this.tool == 2) {
            this.canvas.method144(n, n2, erasingStroke ? 0 : this.brushColor);
            this.painting = false;
         } else {
            int brushColor = !erasingStroke && this.tool != 1 ? this.brushColor : 0;
            int n3 = this.brushSize - 1;

            for (int i = -n3; i <= n3; i++) {
               for (int j = -n3; j <= n3; j++) {
                  this.canvas.method137(n + j, n2 + i, brushColor);
               }
            }
         }
      }
   }

   private static boolean hit(BoundsData panelBounds, double x, double y) {
      return panelBounds != null && panelBounds.contains(x, y);
   }
}

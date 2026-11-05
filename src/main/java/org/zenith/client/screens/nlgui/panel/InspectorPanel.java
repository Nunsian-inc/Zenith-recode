package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.BindLabel;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.elements.RollingText;
import org.zenith.client.screens.nlgui.elements.Scrollbar;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.HeaderBindSetting;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class InspectorPanel extends Panel implements MouseBindTarget {
   private static final float INSET = 8.0F;
   private static final float SCROLL_SPEED = 18.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private final Map<Module, List<InspectorRow>> cache = new HashMap<>();
   private Module module;
   private float scroll;
   private float scrollTarget;
   private final Animation openAnimation = new Animation(240L, 0.0F, Easing.INSTANCE12);
   private static final float MAX_ROW_DELAY = 0.4F;
   private float entranceShift = 7.0F;
   private final RollingText stateRoll = new RollingText();
   private BindLabel headerBindLabel;
   private HeaderBindSetting headerBind;
   private Module headerBindOwner;
   private boolean headerHasSettings;
   private BoundsData headerBindBounds;
   private String sectionTitle;
   private List<Setting> sectionSettings;
   private ModulePreviewStage.Kind sectionPreview;
   private Module sectionPreviewOwner;
   private List<InspectorRow> sectionRows = new ArrayList<>();
   private BoundsData backBounds;
   private boolean active;
   private BoundsData columnBounds;
   private BoundsData headerToggleBounds;
   private BoundsData contentBounds;
   private final Scrollbar scrollbar = new Scrollbar();

   public void setActive(boolean active) {
      this.active = active;
      if (!active) {
         this.resetHeaderBind();
      }
   }

   public boolean isShowing(Module moduleValue) {
      return this.module != null && this.module == moduleValue;
   }

   public boolean isOpen() {
      return this.module != null;
   }

   public boolean hasPreview() {
      return this.previewKind() != null;
   }

   public ModulePreviewStage.Kind previewKind() {
      if (this.sectionPreview != null) {
         return this.sectionPreview;
      } else {
         return this.module == null ? null : this.module.previewKind();
      }
   }

   public Module previewOwner() {
      return this.sectionPreviewOwner != null ? this.sectionPreviewOwner : this.module;
   }

   public void openSection(String sectionTitle, List<Setting> sectionSettings, ModulePreviewStage.Kind sectionPreview, Module sectionPreviewOwner) {
      this.resetHeaderBind();
      this.sectionTitle = sectionTitle;
      this.sectionSettings = sectionSettings;
      this.sectionPreview = sectionPreview;
      this.sectionPreviewOwner = sectionPreviewOwner;
      this.sectionRows = new ArrayList<>();

      for (Setting sectionSetting : sectionSettings) {
         this.sectionRows.add(new InspectorRow(sectionSetting, 0, sectionPreviewOwner == null ? "" : sectionPreviewOwner.getClass().getSimpleName()));
      }

      this.scroll = 0.0F;
      this.scrollTarget = 0.0F;
      this.entranceShift = 18.0F;
      this.openAnimation.reset(0.0F);
      this.openAnimation.method2810(1.0F);
   }

   public void closeSection() {
      if (this.sectionSettings != null) {
         this.resetHeaderBind();
         this.sectionTitle = null;
         this.sectionSettings = null;
         this.sectionPreview = null;
         this.sectionPreviewOwner = null;
         this.sectionRows = new ArrayList<>();
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.entranceShift = -18.0F;
         this.openAnimation.reset(0.0F);
         this.openAnimation.method2810(1.0F);
      }
   }

   public boolean isSectionOpen() {
      return this.sectionSettings != null;
   }

   public void showFor(Module moduleValue) {
      if (this.module != moduleValue) {
         this.resetHeaderBind();
         this.closeSection();
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.entranceShift = 7.0F;
         this.openAnimation.reset(0.0F);
         this.openAnimation.method2810(1.0F);
      }

      this.module = moduleValue;
   }

   public void toggleFor(Module moduleValue) {
      if (this.isShowing(moduleValue)) {
         this.close();
      } else {
         this.showFor(moduleValue);
      }
   }

   public void close() {
      this.resetHeaderBind();
      this.module = null;
      this.closeSection();
   }

   private Module headerModule() {
      return this.sectionSettings == null ? this.module : this.sectionPreviewOwner;
   }

   private void resetHeaderBind() {
      for (InspectorRow sectionRow : this.sectionRows) {
         sectionRow.cancelBinding();
      }

      List<InspectorRow> inspectorRows = this.cache.get(this.module);
      if (inspectorRows != null) {
         for (InspectorRow inspectorRow : inspectorRows) {
            inspectorRow.cancelBinding();
         }
      }

      BindCapture.end(this);
      this.headerBind = null;
      this.headerBindOwner = null;
      this.headerBindLabel = null;
      this.headerBindBounds = null;
      this.headerToggleBounds = null;
      this.backBounds = null;
   }

   public void forget(Module moduleValue) {
      this.cache.remove(moduleValue);
      if (this.module == moduleValue) {
         this.close();
      }
   }

   private List<InspectorRow> visibleRows(Module key) {
      return this.sectionSettings != null ? this.sectionRows : this.rowsOf(key);
   }

   private List<InspectorRow> rowsOf(Module key) {
      return this.cache.computeIfAbsent(key, sectionPreviewOwner -> {
         ArrayList<InspectorRow> var1x = new ArrayList<>();

         for (Setting setting : sectionPreviewOwner.getSettings()) {
            var1x.add(new InspectorRow(setting, 0, sectionPreviewOwner.getClass().getSimpleName()));
         }

         if (var1x.isEmpty()) {
            var1x.add(new InspectorRow(new HeaderBindSetting(sectionPreviewOwner), 0));
         }

         return var1x;
      });
   }

   @Override
   public void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float previewAlpha, float floatValue, float y) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         float width = 144.0F;
         float heightPanel = NLMenuScreen.heightPanel;
         this.columnBounds = new BoundsData(floatValue, y, width, heightPanel);
         float boxHeight = HoverInfo.boxHeight();
         float f = y + heightPanel - boxHeight;
         this.renderHeader(customDrawContext, mouseX, mouseY, floatValue, y, width, previewAlpha, currentStyle);
         float y2 = y + 23.0F;
         float f2 = f - GuiStyle.PADDING.intValue();
         this.contentBounds = new BoundsData(floatValue, y2, width, f2 - y2);
         this.renderRows(customDrawContext, mouseX, mouseY, floatValue, y2, width, f2 - y2, previewAlpha, currentStyle);
         if (!(boxHeight <= 0.01F)) {
            customDrawContext.drawRect(
               floatValue + 8.0F, f - GuiStyle.PADDING.intValue(), width - 16.0F, 1.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * boxHeight / 34.0F)
            );
            if (!HoverInfo.hasText() && this.module != null) {
               String description = this.module.getDescription();
               if (description != null && !description.isBlank()) {
                  HoverInfo.request(this.module.getName(), description);
               }
            }

            HoverInfo.render(
               customDrawContext, floatValue + 8.0F, f + GuiStyle.PADDING.intValue(), width - 16.0F, Math.max(0.0F, boxHeight - GuiStyle.PADDING.intValue() * 2.0F), previewAlpha
            );
         }
      }
   }

   private void renderHeader(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float width, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.01F);
      float centeredTextY = y + (23.0F - font.height()) / 2.0F;
      this.headerBindBounds = null;
      if (this.module == null) {
         customDrawContext.drawText(font, "-", floatValue + 8.0F, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         this.headerToggleBounds = null;
         this.backBounds = null;
      } else {
         Module sectionPreviewOwner = this.sectionSettings == null ? this.module : this.sectionPreviewOwner;
         if (this.headerBindOwner != sectionPreviewOwner) {
            this.resetHeaderBind();
         }

         if (this.headerBind == null && sectionPreviewOwner != null) {
            boolean headerHasSettings;
            label152: {
               this.headerBind = new HeaderBindSetting(sectionPreviewOwner);
               this.headerBindOwner = sectionPreviewOwner;
               this.headerBindLabel = new BindLabel();
               if (this.sectionSettings == null) {
                  var settings = sectionPreviewOwner.getSettings();
                  if (settings.isEmpty()) {
                     headerHasSettings = false;
                     break label152;
                  }
               }

               headerHasSettings = true;
            }

            this.headerHasSettings = headerHasSettings;
         }

         if (this.sectionSettings != null) {
            float f = floatValue + 8.0F + 11.0F;
            float f2 = floatValue + width - 8.0F;
            boolean binding = BindCapture.owner() == this;
            String text = this.headerBind == null ? null : BindLabel.text(binding, this.headerBind.getKeyCode(), this.headerBind.getModifiers());
            if (this.headerBind != null && text == null) {
               text = "+";
            }

            float f3 = text == null ? f2 : f2 - font2.width(text) - GuiStyle.PADDING.intValue() * 2.0F;
            float width2 = Math.max(0.0F, f3 - f);
            customDrawContext.drawText(
               font,
               font.clip(this.sectionTitle == null ? this.module.getName() : this.sectionTitle, width2),
               f,
               centeredTextY,
               currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
            );
            boolean flag = mouseX >= floatValue && mouseX <= floatValue + width && mouseY >= y && mouseY <= y + 23.0F;
            if (this.headerBind != null) {
               this.headerBindBounds = this.headerBindLabel
                  .render(customDrawContext, mouseX, mouseY, y, 23.0F, f2, text, binding, font2, f3 + GuiStyle.PADDING.intValue(), flag, previewAlpha, null, currentStyle);
               if (this.headerBindBounds != null && this.headerBindBounds.contains(mouseX, mouseY)) {
                  HoverInfo.request(this.headerBind.getName(), this.headerBind.getDescription());
               }
            }

            float f4 = this.headerBindBounds == null ? floatValue + width : this.headerBindBounds.getFloatValue() - 1.0F;
            this.backBounds = new BoundsData(floatValue, y, Math.max(0.0F, f4 - floatValue), 23.0F);
            boolean contains = this.backBounds.contains(mouseX, mouseY);
            ZenithColor zenithColor = currentStyle.getTextSecondary()
               .getZenithColor()
               .method7295(currentStyle.getTextEnable().getZenithColor(), contains ? 1.0F : 0.4F)
               .method7294(previewAlpha);
            MenuGlyph.drawChevron(customDrawContext, floatValue + 8.0F, y + 11.5F, 0.0F, zenithColor);
            this.headerToggleBounds = null;
         } else {
            this.backBounds = null;
            MenuGlyph.forCategory(this.module.getCategory()).draw(customDrawContext, floatValue + 8.0F, y + 8.5F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
            float f = floatValue + 8.0F + 11.0F;
            if (this.headerHasSettings) {
               this.headerToggleBounds = null;
               boolean binding;
               String text = BindLabel.text(binding = BindCapture.owner() == this, this.headerBind.getKeyCode(), this.headerBind.getModifiers());
               if (text == null) {
                  text = "+";
               }

               float f2;
               float f3 = (f2 = floatValue + width - 8.0F) - font2.width(text) - GuiStyle.PADDING.intValue() * 2.0F;
               String clip = font.clip(this.module.getName(), Math.max(0.0F, f3 - f));
               customDrawContext.drawText(font, clip, f, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               boolean flag = mouseX >= floatValue && mouseX <= floatValue + width && mouseY >= y && mouseY <= y + 23.0F;
               this.headerBindBounds = this.headerBindLabel
                  .render(
                     customDrawContext,
                     mouseX,
                     mouseY,
                     y,
                     23.0F,
                     f2,
                     text,
                     binding,
                     font2,
                     f + font.width(clip) + GuiStyle.PADDING.intValue(),
                     flag,
                     previewAlpha,
                     null,
                     currentStyle
                  );
               if (this.headerBindBounds != null && this.headerBindBounds.contains(mouseX, mouseY)) {
                  HoverInfo.request(this.headerBind.getName(), this.headerBind.getDescription());
               }
            } else {
               String text = this.module.isEnabled() ? "ON" : "OFF";
               float width2 = font2.width(text);
               float f2 = floatValue + width - 8.0F - width2;
               customDrawContext.drawText(
                  font,
                  font.clip(this.module.getName(), f2 - GuiStyle.PADDING * 2 - f),
                  f,
                  centeredTextY,
                  currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
               );
               this.headerToggleBounds = new BoundsData(f2 - GuiStyle.PADDING.intValue(), y, width2 + GuiStyle.PADDING.intValue() + 8.0F, 23.0F);
               this.stateRoll
                  .draw(
                     customDrawContext,
                     font2,
                     text,
                     f2 + width2,
                     y + (23.0F - font2.height()) / 2.0F,
                     (this.module.isEnabled() ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getTextTertiary().getZenithColor()).method7294(previewAlpha),
                     this.module.isEnabled() ? 1 : -1
                  );
               if (this.headerToggleBounds.contains(mouseX, mouseY)) {
                  HoverInfo.request(
                     this.module.getName(),
                     Zenith.getInstance().getLocalization().translate(this.module.isEnabled() ? "gui.inspector.toggle.off" : "gui.inspector.toggle.on")
                  );
               }
            }
         }
      }
   }

   private void renderRows(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float width, float max, float previewAlpha, ZenithStyle currentStyle) {
      if (this.module != null) {
         List<InspectorRow> visibleRows = this.visibleRows(this.module);
         InspectorRow.alignTracks(visibleRows, width);
         float shownContentHeight = 0.0F;

         for (InspectorRow visibleRow : visibleRows) {
            if (visibleRow.isVisible()) {
               shownContentHeight += visibleRow.height();
            }
         }

         this.clampScroll(shownContentHeight, max);
         this.scroll = this.scrollbar.isDragging() ? this.scrollTarget : this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
         float f = this.openAnimation.method2808(1.0F);
         customDrawContext.enableScissor(floatValue, y, floatValue + width, y + max);
         float y2 = y + this.scroll;
         int n = 0;

         for (InspectorRow visibleRow : visibleRows) {
            if (visibleRow.isVisible()) {
               float rowEntrance2;
               if ((rowEntrance2 = rowEntrance(f, n++)) > 0.001F) {
                  visibleRow.render(customDrawContext, mouseX, mouseY, floatValue + (1.0F - rowEntrance2) * this.entranceShift, y2, width, previewAlpha * rowEntrance2);
               }

               y2 += visibleRow.height();
            }
         }

         customDrawContext.disableScissor();
         this.scrollbar.render(customDrawContext, floatValue + width - 2.0F, y, max, shownContentHeight, this.scroll, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.6F));
      }
   }

   private static float rowEntrance(float f, int n) {
      if (f >= 1.0F) {
         return 1.0F;
      } else {
         float f2 = Math.min(n * 0.045F, 0.4F);
         return Mth.clamp((f - f2) / 0.6F, 0.0F, 1.0F);
      }
   }

   private void clampScroll(float shownContentHeight, float max) {
      if (shownContentHeight <= max) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float min = max - shownContentHeight;
         this.scrollTarget = Mth.clamp(this.scrollTarget, min, 0.0F);
         this.scroll = Mth.clamp(this.scroll, min, 0.0F);
      }
   }

   @Override
   public boolean bindMouse(Enum3 enum2) {
      if (this.active
         && this.module != null
         && BindCapture.owner() == this
         && this.headerBind != null
         && this.headerBindOwner == (this.sectionSettings == null ? this.module : this.sectionPreviewOwner)
         && MouseBindTarget.bindable(enum2)) {
         this.headerBind.setBind(enum2.getIntValue(), 0);
         BindCapture.end(this);
         this.headerBindLabel.set();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   public void cancelHeaderBindingOutside(double x, double y) {
      if (this.headerBindBounds == null || !this.headerBindBounds.contains(x, y)) {
         BindCapture.end(this);
      }
   }

   @Override
   public boolean onMouseClicked(double x, double y, Enum3 enum2) {
      if (this.bindMouse(enum2)) {
         return true;
      } else {
         this.cancelHeaderBindingOutside(x, y);
         if (this.active && this.module != null && this.columnBounds != null && this.columnBounds.contains(x, y)) {
            if (this.headerBind != null
               && this.headerBindOwner == (this.sectionSettings == null ? this.module : this.sectionPreviewOwner)
               && this.headerBindBounds != null
               && this.headerBindBounds.contains(x, y)) {
               if (enum2 == Enum3.RIGHT) {
                  this.headerBind.setBind(-1, 0);
                  BindCapture.end(this);
                  this.headerBindLabel.cleared();
                  Zenith.getInstance().getClientSounds().method1567();
               } else {
                  BindCapture.begin(this);
                  this.headerBindLabel.waiting();
                  Zenith.getInstance().getClientSounds().method1570(false);
               }

               return true;
            } else if (this.backBounds != null && this.backBounds.contains(x, y)) {
               this.closeSection();
               Zenith.getInstance().getClientSounds().method1563();
               return true;
            } else if (this.headerToggleBounds != null && this.headerToggleBounds.contains(x, y)) {
               this.module.toggle();
               return true;
            } else {
               float scrollTarget = this.scrollbar.onMouseClicked(x, y);
               if (this.scrollbar.isDragging()) {
                  if (!Float.isNaN(scrollTarget)) {
                     this.scrollTarget = scrollTarget;
                  }

                  return true;
               } else {
                  if (this.contentBounds != null && this.contentBounds.contains(x, y)) {
                     for (InspectorRow visibleRow : this.visibleRows(this.module)) {
                        if (visibleRow.isVisible() && visibleRow.onMouseClicked(x, y, enum2)) {
                           return true;
                        }
                     }
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean onMouseReleased(double x, double y, Enum3 enum2) {
      if (this.module == null) {
         return false;
      } else {
         this.scrollbar.release();

         for (InspectorRow visibleRow : this.visibleRows(this.module)) {
            visibleRow.onMouseReleased(x, y, enum2);
         }

         return false;
      }
   }

   public boolean onMouseDragged(double doubleValue, double mouseY) {
      if (this.active && this.module != null) {
         float scrollTarget;
         if (!Float.isNaN(scrollTarget = this.scrollbar.onMouseDragged(mouseY))) {
            this.scrollTarget = scrollTarget;
            return true;
         } else {
            boolean flag = false;

            for (InspectorRow visibleRow : this.visibleRows(this.module)) {
               if (visibleRow.onMouseDragged(doubleValue, mouseY)) {
                  flag = true;
               }
            }

            return flag;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.active && this.module != null && this.columnBounds != null && this.columnBounds.contains(mouseX, mouseY)) {
         this.scrollTarget += (float)verticalAmount * 18.0F;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.active && this.module != null) {
         if (BindCapture.owner() == this
            && this.headerBind != null
            && this.headerBindOwner == (this.sectionSettings == null ? this.module : this.sectionPreviewOwner)) {
            if (keyCode == 256) {
               this.headerBind.setBind(-1, 0);
               this.headerBindLabel.cleared();
               Zenith.getInstance().getClientSounds().method1567();
            } else {
               this.headerBind.setBind(keyCode, KeyName.method6700(keyCode, modifiers));
               this.headerBindLabel.set();
               Zenith.getInstance().getClientSounds().method1566();
            }

            BindCapture.end(this);
            return true;
         } else {
            for (InspectorRow visibleRow : this.visibleRows(this.module)) {
               if (visibleRow.isVisible() && visibleRow.keyPressed(keyCode, keycode, modifiers)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      if (this.active && this.module != null) {
         for (InspectorRow visibleRow : this.visibleRows(this.module)) {
            if (visibleRow.isVisible() && visibleRow.charTyped(codepoint, n)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Generated
   public Module getModule() {
      return this.module;
   }
}

package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.BotModuleUiAdapter;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.GuiModuleRowElement;
import org.zenith.client.screens.nlgui.elements.Scrollbar;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class GuiModulePanel extends ElementPanel {
   private static final float SCROLL_SPEED = 22.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private final Animation animationChangeCategory = new Animation(200L, 1.0F, Easing.INSTANCE13);
   private int categoryDirection = 1;
   private static final float CATEGORY_TRAVEL = 16.0F;
   private static final float INCOMING_TAKEOVER = 0.2F;
   private float shownContentHeight = Float.NaN;
   private final Animation scrollbarHeight = new Animation(200L, Easing.INSTANCE10);
   private final Animation scrollbarPresence = new Animation(160L, 0.0F, Easing.INSTANCE10);
   private final Scrollbar scrollbar = new Scrollbar();
   private static final float SUBMODULE_INDENT = 12.0F;
   private final Map<ModuleCategory, List<GuiModuleRowElement>> categories = new HashMap<>();
   private final Map<GuiModuleRowElement, List<GuiModuleRowElement>> searchChildren = new HashMap<>();
   private final Map<GuiModuleRowElement, GuiModuleRowElement> searchParents = new HashMap<>();
   private final Set<GuiModuleRowElement> searchMatches = new HashSet<>();
   private final Map<GuiModuleRowElement, BoundsData> rowHitBounds = new LinkedHashMap<>();
   private String lastSearchQuery = "";
   private ModuleCategory currentCategory = ModuleCategory.COMBAT;
   private ModuleCategory lastCategory;
   private boolean botOnly;
   private final List<GuiModuleRowElement> botModuleElements = new ArrayList<>();
   private BoundsData scissorBounds;
   private float scroll = 0.0F;
   private float scrollTarget = 0.0F;
   private int caret = -1;

   public GuiModulePanel() {
      this.rebuild();
   }

   public void rebuild() {
      this.searchParents.keySet().forEach(GuiModuleRowElement::cancelBinding);
      this.searchChildren.clear();
      this.searchParents.clear();
      this.searchMatches.clear();
      this.rowHitBounds.clear();
      this.categories.clear();

      for (ModuleCategory moduleCategory : ModuleCategory.values()) {
         this.categories
            .put(
               moduleCategory,
               Zenith.getInstance()
                  .getModuleManager()
                  .getItems()
                  .stream()
                  .filter(moduleValue -> moduleValue.getCategory() == moduleCategory && !moduleValue.isHidden())
                  .map(GuiModuleRowElement::new)
                  .toList()
            );
      }

      this.categories.forEach((category, guiModuleRowElements) -> {
         for (GuiModuleRowElement var4x : guiModuleRowElements) {
            var4x.resetSearchVisible(category == this.currentCategory);
         }
      });
      HashSet hashSet = new HashSet();
      this.categories.values().forEach(guiModuleRowElements -> guiModuleRowElements.forEach(var1x -> hashSet.add(var1x.getModule())));

      for (ModuleCategory moduleCategory : ModuleCategory.values()) {
         for (GuiModuleRowElement guiModuleRowElement : this.categories.get(moduleCategory)) {
            ArrayList arrayList = new ArrayList();

            for (Module surfacedModule : guiModuleRowElement.getModule().getSurfacedModules()) {
               if (hashSet.add(surfacedModule)) {
                  GuiModuleRowElement guiModuleRowElement2 = new GuiModuleRowElement(surfacedModule);
                  guiModuleRowElement2.resetSearchVisible(false);
                  arrayList.add(guiModuleRowElement2);
                  this.searchParents.put(guiModuleRowElement2, guiModuleRowElement);
               }
            }

            if (!arrayList.isEmpty()) {
               this.searchChildren.put(guiModuleRowElement, arrayList);
            }
         }
      }
   }

   @Override
   public boolean isHeaderSearchOnly() {
      return true;
   }

   @Override
   public float getContentWidth() {
      return 236.0F;
   }

   @Override
   public void renderHeader(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3, float f4) {
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int n, int n2, float f, float floatValue, float y) {
      this.rowHitBounds.clear();
      float height = Math.max(0.0F, NLMenuScreen.contentBottom - 20.0F - GuiStyle.PADDING.intValue() - y);
      this.scissorBounds = new BoundsData(floatValue, y, 236.0F, height);
      this.animationChangeCategory.setDuration(MenuModule.INSTANCE.method5347());
      var searchRenderableElements = this.getSearchRenderableElements(this.currentCategory);
      var orderedElements = this.getOrderedElements(searchRenderableElements);
      float orderedContentHeight = this.orderedContentHeight(orderedElements);
      this.clampScroll(orderedContentHeight, height);
      this.scroll = this.scrollbar.isDragging() ? this.scrollTarget : this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float f2 = this.animationChangeCategory.method2808(1.0F);
      if (this.animationChangeCategory.isDone()) {
         this.lastCategory = null;
      }

      float f3 = Mth.clamp((0.45F - f2) / 0.45F, 0.0F, 1.0F);
      float f4 = Mth.clamp((f2 - 0.2F) / 0.8F, 0.0F, 1.0F);
      menuDrawContext.enableScissor(floatValue, y, floatValue + 236.0F, y + height);
      if (this.lastCategory != null && f3 > 0.0F) {
         this.renderElements(
            this.getSearchRenderableElements(this.lastCategory),
            menuDrawContext,
            n,
            n2,
            f * f3,
            floatValue,
            y - 16.0F * this.categoryDirection * (1.0F - f3),
            false
         );
      }

      if (f4 > 0.0F) {
         this.renderElements(searchRenderableElements, orderedElements, menuDrawContext, n, n2, f * f4, floatValue, y + 16.0F * this.categoryDirection * (1.0F - f4), true);
      }

      menuDrawContext.disableScissor();
      this.renderScrollbar(menuDrawContext, floatValue, y, height, orderedContentHeight, f);
   }

   private void renderScrollbar(MenuDrawContext menuDrawContext, float floatValue, float y, float height, float shownContentHeight, float f) {
      boolean flag = shownContentHeight > height;
      float f2 = this.scrollbarPresence.method2808(flag ? 1.0F : 0.0F);
      if (flag) {
         if (!Float.isNaN(this.shownContentHeight) && !(f2 <= 0.001F)) {
            this.shownContentHeight = this.scrollbarHeight.method2808(shownContentHeight);
         } else {
            this.shownContentHeight = shownContentHeight;
            this.scrollbarHeight.reset(shownContentHeight);
         }
      }

      if (!(f2 <= 0.001F) && !Float.isNaN(this.shownContentHeight)) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle == null) {
            this.scrollbar.clear();
         } else {
            this.scrollbar
               .render(
                  menuDrawContext,
                  floatValue + 236.0F - 2.0F,
                  y,
                  height,
                  this.shownContentHeight,
                  shownContentHeight,
                  this.scroll,
                  currentStyle.getTextTertiary().getZenithColor().method7294(f * f2 * 0.6F)
               );
         }
      } else {
         this.scrollbar.clear();
      }
   }

   @Override
   public void renderPriority(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3) {
   }

   private void renderElements(List<GuiModuleRowElement> searchRenderableElements, MenuDrawContext menuDrawContext, int n, int n2, float previewAlpha, float floatValue, float f, boolean flag) {
      this.renderElements(
         searchRenderableElements,
         (searchRenderableElements.isEmpty()) ? searchRenderableElements : this.getOrderedElements(searchRenderableElements),
         menuDrawContext,
         n,
         n2,
         previewAlpha,
         floatValue,
         f,
         flag
      );
   }

   private void renderElements(
      List<GuiModuleRowElement> searchRenderableElements,
      List<GuiModuleRowElement> orderedElements,
      MenuDrawContext menuDrawContext,
      int n,
      int n2,
      float previewAlpha,
      float floatValue,
      float f,
      boolean flag
   ) {
      if (searchRenderableElements.isEmpty()) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            Font font = Fonts.NEW_REGULAR.getFont(5.0F);
            String text = Zenith.getInstance().getLocalization().translate("gui.modules.empty");
            menuDrawContext.drawText(font, text, floatValue + (236.0F - font.width(text)) / 2.0F, f + 30.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         }
      } else {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            boolean flag2 = !this.getSearchQuery().isEmpty();
            float f2;
            float f3 = f2 = f + this.scroll;
            String groupOf = null;
            int n3 = 0;

            for (GuiModuleRowElement orderedElement : orderedElements) {
               String str = this.groupOf(orderedElement, flag2);
               if (!Objects.equals(str, groupOf)) {
                  groupOf = str;
                  if (str != null && !str.isEmpty()) {
                     this.renderGroupHeader(menuDrawContext, floatValue, f3, str, previewAlpha);
                     f3 += 13.0F;
                  }
               }

               float searchVisibleProgress;
               if ((searchVisibleProgress = orderedElement.getSearchVisibleProgress()) <= 0.01F) {
                  orderedElement.forgetSlide();
               } else {
                  boolean containsKey = this.searchParents.containsKey(orderedElement);
                  float f4 = containsKey ? 12.0F : 0.0F;
                  float floatValue2 = floatValue + f4 + (containsKey ? (1.0F - searchVisibleProgress) * 8.0F : 0.0F);
                  float width = 236.0F - (floatValue2 - floatValue);
                  orderedElement.setWidth(width);
                  orderedElement.setShowCategoryIcon(flag2 && !containsKey);
                  float height = 16.0F * searchVisibleProgress;
                  float y = containsKey ? f3 - 16.0F * (1.0F - searchVisibleProgress) : f2 + orderedElement.slideTo(f3 - f2);
                  BoundsData boundsData = new BoundsData(floatValue2, containsKey ? f3 : y, width, height);
                  boolean contains = this.searchMatches.contains(orderedElement);
                  if (this.scissorBounds.method8093(boundsData)) {
                     float floatValue3 = Math.max(this.scissorBounds.getFloatValue(), boundsData.getFloatValue());
                     float y2 = Math.max(this.scissorBounds.getFloatValue2(), boundsData.getFloatValue2());
                     float guiScaledWidth = Math.min(this.scissorBounds.getFloatValue() + this.scissorBounds.width(), boundsData.getFloatValue() + boundsData.width());
                     float guiScaledHeight;
                     if ((guiScaledHeight = Math.min(this.scissorBounds.getFloatValue2() + this.scissorBounds.height(), boundsData.getFloatValue2() + boundsData.height())) <= y2 || guiScaledWidth <= floatValue3) {
                        f3 += height;
                        if (contains) {
                           n3++;
                        }
                        continue;
                     }

                     BoundsData boundsData2 = new BoundsData(floatValue3, y2, guiScaledWidth - floatValue3, guiScaledHeight - y2);
                     boolean flag3;
                     if (flag3 = flag && contains && this.animationChangeCategory.isDone()) {
                        this.rowHitBounds.put(orderedElement, boundsData2);
                     }

                     boolean flag4;
                     if (flag4 = containsKey && searchVisibleProgress < 0.999F) {
                        menuDrawContext.enableScissor(floatValue3, y2, guiScaledWidth, guiScaledHeight);
                     }

                     try {
                        if (flag && contains && n3 == this.caret) {
                           menuDrawContext.drawRect(floatValue2, y + 2.0F, 1.5F, 12.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * searchVisibleProgress));
                        }

                        orderedElement.render(menuDrawContext, flag3 ? n : -10000.0F, flag3 ? n2 : -10000.0F, floatValue2, y, previewAlpha * searchVisibleProgress, boundsData2);
                     } finally {
                        if (flag4) {
                           menuDrawContext.disableScissor();
                        }
                     }
                  }

                  f3 += height;
                  if (contains) {
                     n3++;
                  }
               }
            }
         }
      }
   }

   private String groupOf(GuiModuleRowElement orderedElement, boolean flag) {
      orderedElement = this.searchParents.getOrDefault(orderedElement, orderedElement);
      if (flag) {
         return orderedElement.getModule().getCategory().getName();
      } else {
         return orderedElement.getModule().isPriority() ? Zenith.getInstance().getLocalization().translate("gui.modules.favourites") : orderedElement.getModule().getGroup();
      }
   }

   private void renderGroupHeader(MenuDrawContext menuDrawContext, float floatValue, float f, String str, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
         menuDrawContext.drawText(
            font,
            HoverInfo.spaced(str.toUpperCase(Locale.ROOT)),
            floatValue + 8.0F,
            f + (13.0F - font.height()) / 2.0F + 2.0F,
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   private float getContentHeight(List<GuiModuleRowElement> searchRenderableElements) {
      return (searchRenderableElements.isEmpty()) ? 0.0F : this.orderedContentHeight(this.getOrderedElements(searchRenderableElements));
   }

   private float orderedContentHeight(List<GuiModuleRowElement> orderedElements) {
      if (orderedElements.isEmpty()) {
         return 0.0F;
      } else {
         boolean flag = !this.getSearchQuery().isEmpty();
         float f = 0.0F;
         String groupOf = null;

         for (GuiModuleRowElement orderedElement : orderedElements) {
            String str = this.groupOf(orderedElement, flag);
            if (!Objects.equals(str, groupOf)) {
               groupOf = str;
               if (str != null && !str.isEmpty()) {
                  f += 13.0F;
               }
            }

            float searchVisibleProgress;
            if (!((searchVisibleProgress = orderedElement.getSearchVisibleProgress()) <= 0.01F)) {
               f += 16.0F * searchVisibleProgress;
            }
         }

         return f;
      }
   }

   private void clampScroll(float contentHeight, float height) {
      if (contentHeight <= height) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float min = height - contentHeight;
         this.scrollTarget = Mth.clamp(this.scrollTarget, min, 0.0F);
         this.scroll = Mth.clamp(this.scroll, min, 0.0F);
      }
   }

   @Override
   public boolean mouseScrolled(double lastClickX, double lastClickY, double d, double d2) {
      if (this.scissorBounds != null && this.scissorBounds.contains(lastClickX, lastClickY)) {
         var searchRenderableElements = this.getSearchRenderableElements(this.currentCategory);
         if (searchRenderableElements.isEmpty()) {
            return false;
         } else {
            float contentHeight;
            if ((contentHeight = this.getContentHeight(searchRenderableElements)) <= this.scissorBounds.height()) {
               return false;
            } else {
               this.scrollTarget = (float)(this.scrollTarget + d2 * 22.0);
               this.clampScroll(contentHeight, this.scissorBounds.height());
               return true;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      float f = this.scrollbar.onMouseClicked(lastClickX, lastClickY);
      if (this.scrollbar.isDragging()) {
         this.scrollTarget = Float.isNaN(f) ? this.scroll : f;
         if (this.scissorBounds != null) {
            this.clampScroll(this.getContentHeight(this.getSearchRenderableElements(this.currentCategory)), this.scissorBounds.height());
         }

         return true;
      } else if (!this.animationChangeCategory.isDone()) {
         return false;
      } else if (this.scissorBounds != null && this.scissorBounds.contains(lastClickX, lastClickY)) {
         this.getSearchRenderableElements(this.currentCategory);

         for (Entry entry : this.rowHitBounds.entrySet()) {
            GuiModuleRowElement key = (GuiModuleRowElement)entry.getKey();
            if (this.searchMatches.contains(key) && ((BoundsData)entry.getValue()).contains(lastClickX, lastClickY) && key.onMouseClicked(lastClickX, lastClickY, enum2)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double d, double d2, Enum3 enum2) {
      this.scrollbar.release();
      return false;
   }

   @Override
   public boolean onMouseDragged(double d, double mouseY, int n, double d2, double d3) {
      float scrollTarget;
      if (!Float.isNaN(scrollTarget = this.scrollbar.onMouseDragged(mouseY))) {
         this.scrollTarget = scrollTarget;
         if (this.scissorBounds != null) {
            this.clampScroll(this.getContentHeight(this.getSearchRenderableElements(this.currentCategory)), this.scissorBounds.height());
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      for (GuiModuleRowElement searchRenderableElement : this.getSearchRenderableElements(this.currentCategory)) {
         if (this.searchMatches.contains(searchRenderableElement) && searchRenderableElement.isBinding() && searchRenderableElement.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      return false;
   }

   private List<GuiModuleRowElement> visibleOrdered() {
      ArrayList<GuiModuleRowElement> guiModuleRowElements = new ArrayList<>();

      for (GuiModuleRowElement orderedElement : this.getOrderedElements(this.getSearchRenderableElements(this.currentCategory))) {
         if (this.searchMatches.contains(orderedElement) && orderedElement.getSearchVisibleProgress() > 0.01F) {
            guiModuleRowElements.add(orderedElement);
         }
      }

      return guiModuleRowElements;
   }

   public void moveCaret(int n) {
      var visibleOrdered = this.visibleOrdered();
      if (visibleOrdered.isEmpty()) {
         this.caret = -1;
      } else {
         if (this.caret < 0) {
            this.caret = n > 0 ? 0 : visibleOrdered.size() - 1;
         } else {
            this.caret = Mth.clamp(this.caret + n, 0, visibleOrdered.size() - 1);
         }

         this.ensureCaretVisible(visibleOrdered);
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().showFor(((GuiModuleRowElement)visibleOrdered.get(this.caret)).getModule());
      }
   }

   private void ensureCaretVisible(List<GuiModuleRowElement> visibleOrdered) {
      if (this.scissorBounds != null && this.caret >= 0 && this.caret < visibleOrdered.size()) {
         boolean flag = !this.getSearchQuery().isEmpty();
         float f = 0.0F;
         String groupOf = null;
         GuiModuleRowElement guiModuleRowElement = (GuiModuleRowElement)visibleOrdered.get(this.caret);

         for (GuiModuleRowElement orderedElement : this.getOrderedElements(this.getSearchRenderableElements(this.currentCategory))) {
            String str = this.groupOf(orderedElement, flag);
            if (!Objects.equals(str, groupOf)) {
               groupOf = str;
               if (str != null && !str.isEmpty()) {
                  f += 13.0F;
               }
            }

            float searchVisibleProgress;
            if (!((searchVisibleProgress = orderedElement.getSearchVisibleProgress()) <= 0.01F)) {
               if (orderedElement == guiModuleRowElement) {
                  break;
               }

               f += 16.0F * searchVisibleProgress;
            }
         }

         float height = this.scissorBounds.height();
         float f2 = f + 16.0F * guiModuleRowElement.getSearchVisibleProgress();
         if (f + this.scrollTarget < 0.0F) {
            this.scrollTarget = -f;
         } else if (f2 + this.scrollTarget > height) {
            this.scrollTarget = height - f2;
         }

         this.clampScroll(this.getContentHeight(this.getSearchRenderableElements(this.currentCategory)), height);
      }
   }

   public boolean toggleCaret() {
      var visibleOrdered = this.visibleOrdered();
      if (this.caret >= 0 && this.caret < visibleOrdered.size()) {
         ((GuiModuleRowElement)visibleOrdered.get(this.caret)).getModule().toggle();
         return true;
      } else {
         return false;
      }
   }

   public boolean openCaret() {
      var visibleOrdered = this.visibleOrdered();
      if (this.caret >= 0 && this.caret < visibleOrdered.size()) {
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().showFor(((GuiModuleRowElement)visibleOrdered.get(this.caret)).getModule());
         return true;
      } else {
         return false;
      }
   }

   public void clearCaret() {
      this.caret = -1;
   }

   private List<GuiModuleRowElement> getOrderedElements(List<GuiModuleRowElement> searchRenderableElements) {
      ArrayList<GuiModuleRowElement> guiModuleRowElements = new ArrayList(searchRenderableElements.size());
      boolean flag = !this.getSearchQuery().isEmpty();
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      LinkedHashMap map = new LinkedHashMap();
      HashMap map2 = new HashMap();

      for (GuiModuleRowElement searchRenderableElement : searchRenderableElements) {
         if (!this.searchParents.containsKey(searchRenderableElement)) {
            if (searchRenderableElement.getModule().isPriority()) {
               if (flag) {
                  String groupOf = this.groupOf(searchRenderableElement, true);
                  int n = ((Integer)map2.getOrDefault(groupOf, 0));
                  ((java.util.List)map.computeIfAbsent(groupOf, obj -> new ArrayList<>())).add(n, searchRenderableElement);
                  map2.put(groupOf, n + 1);
               } else {
                  arrayList.add(searchRenderableElement);
               }
            } else if (flag) {
               ((java.util.List)map.computeIfAbsent(this.groupOf(searchRenderableElement, true), obj -> new ArrayList<>())).add(searchRenderableElement);
            } else {
               String group = searchRenderableElement.getModule().getGroup();
               if (group.isEmpty()) {
                  arrayList2.add(searchRenderableElement);
               } else {
                  ((java.util.List)map.computeIfAbsent(group, obj -> new ArrayList<>())).add(searchRenderableElement);
               }
            }
         }
      }

      guiModuleRowElements.addAll(arrayList);
      guiModuleRowElements.addAll(arrayList2);
      map.values().forEach(c -> guiModuleRowElements.addAll((java.util.Collection)c));
      var guiModuleRowElements2 = new HashSet<>(searchRenderableElements);
      List<GuiModuleRowElement> guiModuleRowElements3 = new ArrayList<>(searchRenderableElements.size());

      for (GuiModuleRowElement key : guiModuleRowElements) {
         guiModuleRowElements3.add(key);

         for (GuiModuleRowElement guiModuleRowElement : this.searchChildren.getOrDefault(key, Collections.emptyList())) {
            if (guiModuleRowElements2.contains(guiModuleRowElement)) {
               guiModuleRowElements3.add(guiModuleRowElement);
            }
         }
      }

      return guiModuleRowElements3;
   }

   public void setCategory(ModuleCategory currentCategory) {
      if (this.currentCategory != currentCategory) {
         if (Zenith.getInstance().getNLMenuScreen().getType() == NLMenuScreen.ElementsType.CATEGORY) {
            float f = this.animationChangeCategory.getValue();
            boolean flag;
            if ((flag = this.lastCategory != null && !this.animationChangeCategory.isDone()) && this.lastCategory == currentCategory) {
               this.lastCategory = this.currentCategory;
               this.categoryDirection = -this.categoryDirection;
               this.animationChangeCategory.reset(1.0F - f);
               this.animationChangeCategory.method2810(1.0F);
            } else if (flag && f < 0.2F) {
               this.categoryDirection = currentCategory.ordinal() >= this.lastCategory.ordinal() ? 1 : -1;
            } else {
               this.lastCategory = this.currentCategory;
               this.categoryDirection = currentCategory.ordinal() >= this.currentCategory.ordinal() ? 1 : -1;
               this.animationChangeCategory.reset(0.0F);
               this.animationChangeCategory.method2810(1.0F);
            }

            this.scrollTarget = 0.0F;
         }

         this.currentCategory = currentCategory;
         this.caret = -1;
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().close();
      }
   }

   public boolean isCategorySwitching() {
      return this.lastCategory != null && !this.animationChangeCategory.isDone();
   }

   public void setBotOnly(boolean botOnly) {
      if (this.botOnly != botOnly) {
         this.botOnly = botOnly;
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
         this.caret = -1;
      }
   }

   public void forceCategory(ModuleCategory currentCategory) {
      this.currentCategory = currentCategory;
      this.lastCategory = null;
      this.animationChangeCategory.reset(1.0F);
      this.scrollTarget = 0.0F;
      this.scroll = 0.0F;
      this.caret = -1;
      this.shownContentHeight = Float.NaN;
   }

   public void setBotContext(String botContext) {
      this.botModuleElements.clear();
      BotClient botClient = HeadlessBots.get(botContext);
      if (botClient != null) {
         for (BotModule botModule : botClient.getModules().getModules()) {
            GuiModuleRowElement guiModuleRowElement = new GuiModuleRowElement(new BotModuleUiAdapter(botContext, botModule));
            guiModuleRowElement.resetSearchVisible(true);
            this.botModuleElements.add(guiModuleRowElement);
         }
      }
   }

   private List<GuiModuleRowElement> applyBotFilter(List<GuiModuleRowElement> category) {
      return this.botOnly && (!category.isEmpty())
              ? category.stream().filter(guiModuleRowElement -> guiModuleRowElement.getModule().isBotModule()).toList()
              : category;
   }

   private List<GuiModuleRowElement> withBotElements(List<GuiModuleRowElement> applyBotFilter, boolean flag) {
      if (this.botOnly && flag && !this.botModuleElements.isEmpty()) {
         ArrayList<GuiModuleRowElement> guiModuleRowElements = new ArrayList(applyBotFilter);
         guiModuleRowElements.addAll(this.botModuleElements);
         return guiModuleRowElements;
      } else {
         return applyBotFilter;
      }
   }

   @Override
   public List<GuiModuleRowElement> getElements() {
      return this.getSearchRenderableElements(this.currentCategory);
   }

   public int getVisibleCount() {
      int n = 0;

      for (GuiModuleRowElement searchRenderableElement : this.getSearchRenderableElements(this.currentCategory)) {
         if (this.searchMatches.contains(searchRenderableElement) && searchRenderableElement.getSearchVisibleProgress() > 0.01F) {
            n++;
         }
      }

      return n;
   }

   public Module findModule(String anotherString) {
      for (List<GuiModuleRowElement> guiModuleRowElements : this.categories.values()) {
         for (GuiModuleRowElement guiModuleRowElement : guiModuleRowElements) {
            if (guiModuleRowElement.getName().equalsIgnoreCase(anotherString)) {
               return guiModuleRowElement.getModule();
            }
         }
      }

      return null;
   }

   private List<GuiModuleRowElement> getSearchRenderableElements(ModuleCategory currentCategory) {
      String searchQuery = this.getSearchQuery();
      boolean flag = !searchQuery.isEmpty();
      if (!searchQuery.equals(this.lastSearchQuery)) {
         if (searchQuery.isEmpty()) {
            InspectorPanel inspectorPanel = Zenith.getInstance().getNLMenuScreen().getInspectorPanel();

            for (Entry entry : this.searchParents.entrySet()) {
               if (inspectorPanel.isShowing(((GuiModuleRowElement)entry.getKey()).getModule())) {
                  inspectorPanel.showFor(((GuiModuleRowElement)entry.getValue()).getModule());
                  break;
               }
            }
         }

         this.lastSearchQuery = searchQuery;
         this.caret = -1;
         this.scrollTarget = 0.0F;
         this.rowHitBounds.clear();
      }

      List<GuiModuleRowElement> searchSourceElements = this.getSearchSourceElements(currentCategory);
      var guiModuleRowElements = new HashSet<>(searchSourceElements);
      this.searchMatches.clear();

      for (GuiModuleRowElement guiModuleRowElement : this.searchParents.keySet()) {
         GuiModuleRowElement searchParent = this.searchParents.get(guiModuleRowElement);
         boolean flag2 = !this.botOnly || guiModuleRowElement.getModule().isBotModule();
         boolean searchVisible = flag && guiModuleRowElements.contains(searchParent) && flag2 && (searchParent.matchesOwnSearch(searchQuery) || guiModuleRowElement.matchesOwnSearch(searchQuery));
         guiModuleRowElement.setSearchVisible(searchVisible);
         guiModuleRowElement.updateSearchVisible();
         if (searchVisible) {
            this.searchMatches.add(guiModuleRowElement);
         }
      }

      List<GuiModuleRowElement> guiModuleRowElements2 = new ArrayList<>(searchSourceElements.size());

      for (GuiModuleRowElement searchSourceElement : searchSourceElements) {
         List<GuiModuleRowElement> guiModuleRowElements3 = this.searchChildren.getOrDefault(searchSourceElement, Collections.emptyList());
         boolean searchVisible = !flag || searchSourceElement.matchesOwnSearch(searchQuery);
         boolean flag2 = false;

         for (GuiModuleRowElement guiModuleRowElement : guiModuleRowElements3) {
            searchVisible |= this.searchMatches.contains(guiModuleRowElement);
            flag2 |= guiModuleRowElement.getSearchVisibleProgress() > 0.01F;
         }

         searchSourceElement.setSearchVisible(searchVisible);
         searchSourceElement.updateSearchVisible();
         if (searchVisible) {
            this.searchMatches.add(searchSourceElement);
         }

         if (searchSourceElement.shouldRenderInSearch() || flag2) {
            guiModuleRowElements2.add(searchSourceElement);

            for (GuiModuleRowElement guiModuleRowElement : guiModuleRowElements3) {
               if (guiModuleRowElement.shouldRenderInSearch()) {
                  guiModuleRowElements2.add(guiModuleRowElement);
               }
            }
         }
      }

      return guiModuleRowElements2;
   }

   private List<GuiModuleRowElement> getSearchSourceElements(ModuleCategory currentCategory) {
      if (this.getSearchQuery().isEmpty()) {
         var category = this.categories.get(currentCategory);
         return category == null ? Collections.emptyList() : this.withBotElements(this.applyBotFilter(category), currentCategory == ModuleCategory.PLAYER);
      } else {
         ArrayList category = new ArrayList();

         for (ModuleCategory key : ModuleCategory.values()) {
            category.addAll(this.categories.getOrDefault(key, Collections.emptyList()));
         }

         return this.withBotElements(this.applyBotFilter(category), true);
      }
   }

   private String getSearchQuery() {
      String searchValue = Zenith.getInstance().getNLMenuScreen().getSearchValue();
      return searchValue == null ? "" : searchValue.trim().toLowerCase();
   }

   @Override
   public boolean isRender() {
      return false;
   }

   @Override
   public void close() {
      this.animationChangeCategory.reset(1.0F);
      this.lastCategory = null;
      this.caret = -1;
      this.rowHitBounds.clear();

      for (GuiModuleRowElement guiModuleRowElement : this.searchParents.keySet()) {
         guiModuleRowElement.cancelBinding();
         guiModuleRowElement.resetSearchVisible(false);
      }

      this.shownContentHeight = Float.NaN;
      this.scrollbarPresence.reset(0.0F);
   }

   @Generated
   public ModuleCategory getCurrentCategory() {
      return this.currentCategory;
   }

   @Generated
   public boolean isBotOnly() {
      return this.botOnly;
   }
}

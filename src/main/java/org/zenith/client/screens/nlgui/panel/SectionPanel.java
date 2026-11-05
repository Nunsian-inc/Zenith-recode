package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.elements.PressFx;
import org.zenith.client.screens.nlgui.elements.Scrollbar;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public abstract class SectionPanel extends ElementPanel {
   public static final float INSET = 8.0F;
   private static final float MIN_TAB_PAD = 3.0F;
   private static final float PLATE_INSET = 2.0F;
   private static final float MIN_INLINE_TAB_FONT = 4.5F;
   private static final float SCROLL_TAB_PAD = 6.0F;
   public static final float CAPTION_HEIGHT = 16.0F;
   public static final float LEAD_CAPTION_TRIM = 6.0F;
   public static final float ICON_COLUMN = 9.0F;
   private final List<SectionPanel.Entry> mainEntries = new ArrayList<>();
   private final List<SectionPanel.Entry> asideEntries = new ArrayList<>();
   private final List<String> tabs = new ArrayList<>();
   private final List<MenuGlyph> tabIcons = new ArrayList<>();
   private SectionPanel.AsideRenderer asideFull;
   private final Animation plateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private final Animation switchAnimation = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private boolean built;
   private boolean embedded;
   private int activeTab;
   private float panelAlpha = 1.0F;
   private float scroll;
   private float scrollTarget;
   private BoundsData mainBounds;
   private BoundsData asideBounds;
   private float asideScroll;
   private float asideScrollTarget;
   private BoundsData footerBounds;
   private BoundsData mainHeaderBounds;
   private BoundsData lastRightClickBounds;
   private final List<BoundsData> tabBounds = new ArrayList<>();
   private float tabScroll;
   private float tabScrollTarget;
   private boolean tabsScrollable;
   private int tabScrolledTo = -1;
   private BoundsData tabViewport;
   private final Scrollbar mainScrollbar = new Scrollbar();
   private final Scrollbar asideScrollbar = new Scrollbar();
   public final PressFx pressFx = new PressFx();
   private static final float VALUE_GAP = 6.0F;

   public boolean pressFeedback() {
      return false;
   }

   public float entryEnter(int n, boolean flag) {
      return 1.0F;
   }

   public int mainEntryCount() {
      return this.mainEntries.size();
   }

   public int asideEntryCount() {
      return this.asideEntries.size();
   }

   private void renderEntry(
      SectionPanel.Entry mainEntry,
      String str,
      int n,
      boolean flag,
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      float floatValue,
      float y,
      float mainWidth,
      float previewAlpha,
      ZenithStyle currentStyle
   ) {
      float entryEnter;
      if (!((entryEnter = this.entryEnter(n, flag)) <= 0.001F)) {
         boolean flag2;
         if (flag2 = entryEnter < 0.999F) {
            menuDrawContext.pushMatrix();
            menuDrawContext.pose().translate((1.0F - entryEnter) * 4.0F, (1.0F - entryEnter) * 6.0F);
         }

         float f = previewAlpha * entryEnter;
         boolean flag3;
         BoundsData boundsData = (flag3 = this.pressFeedback() && mainEntry.kind != SectionPanel.Entry.Kind.SETTING && mainEntry.kind != SectionPanel.Entry.Kind.CAPTION)
            ? new BoundsData(floatValue, y, mainWidth, mainEntry.height())
            : null;
         boolean begin = flag3 && this.pressFx.begin(menuDrawContext, str, boundsData);
         mainEntry.render(menuDrawContext, lastClickX, lastClickY, floatValue, y, mainWidth, f, currentStyle);
         if (flag3 && mainEntry.kind == SectionPanel.Entry.Kind.ROW) {
            this.pressFx.ink(menuDrawContext, str, boundsData, 3.0F, currentStyle.getPrimaryColor().getZenithColor().method7292(0.2F), f);
         }

         this.pressFx.end(menuDrawContext, begin);
         if (flag2) {
            menuDrawContext.popMatrix();
         }
      }
   }

   private void markPress(SectionPanel.Entry entry, String str, double lastClickX, double lastClickY) {
      if (this.pressFeedback() && entry.kind != SectionPanel.Entry.Kind.SETTING) {
         this.pressFx.press(str, lastClickX, lastClickY);
      }
   }

   public static String t(String key) {
      return key == null ? "" : Zenith.getInstance().getLocalization().translate(key);
   }

   public abstract void build();

   public abstract String headerTitle();

   @Override
   public float getHeaderTitleWidth() {
      return 19.0F + Fonts.NEW_MEDIUM.getFont(5.5F).width(this.headerTitle());
   }

   public abstract MenuGlyph headerIcon();

   public abstract String sectionDescription();

   public void invalidate() {
      this.built = false;
   }

   public void tabs(String... strings) {
      this.tabs.clear();

      for (String str : strings) {
         this.tabs.add(str);
      }

      if (this.activeTab >= this.tabs.size()) {
         this.activeTab = 0;
      }
   }

   public void tabIcons(MenuGlyph... menuGlyphs) {
      this.tabIcons.clear();

      for (MenuGlyph menuGlyph : menuGlyphs) {
         this.tabIcons.add(menuGlyph);
      }
   }

   private void restartSwitch() {
      this.switchAnimation.reset(0.0F);
      this.switchAnimation.method2810(1.0F);
   }

   public int getActiveTab() {
      return this.activeTab;
   }

   public void setEmbedded(boolean embedded) {
      this.embedded = embedded;
   }

   private float inlineTabsHeight() {
      return this.embedded && !this.tabs.isEmpty() ? 19.0F : 0.0F;
   }

   public String searchQuery() {
      String searchValue = Zenith.getInstance().getNLMenuScreen().getSearchValue();
      return searchValue == null ? "" : searchValue.trim().toLowerCase(Locale.ROOT);
   }

   public void selectTab(int activeTab) {
      if (this.activeTab != activeTab) {
         this.activeTab = activeTab;
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.resetAsideScroll();
         this.restartSwitch();
         this.invalidate();
      }
   }

   public void caption(String key) {
      this.mainEntries
         .add(
            SectionPanel.Entry.caption(
               key,
               this.mainEntries.isEmpty()
                  && this.mainHeaderHeight() <= 0.0F
            )
         );
   }

   public void setting(Setting autoAfk) {
      if (autoAfk != null) {
         this.mainEntries.add(SectionPanel.Entry.setting(new InspectorRow(autoAfk, 0)));
      }
   }

   public void setting(InspectorRow inspectorRow) {
      if (inspectorRow != null) {
         this.mainEntries.add(SectionPanel.Entry.setting(inspectorRow));
      }
   }

   public void info(String username, Supplier<String> supplier, String description) {
      this.row(username).value(supplier).description(description).add();
   }

   public void action(String text, MenuGlyph icon, Supplier<String> supplier, Runnable onClick, String description) {
      this.row(text).icon(icon).value(supplier).onClick(onClick).description(description).add();
   }

   public SectionPanel.RowBuilder row(String text) {
      return new SectionPanel.RowBuilder(text, this.mainEntries);
   }

   public void custom(float hintsHeight, SectionPanel.AsideRenderer entering) {
      this.mainEntries.add(SectionPanel.Entry.custom(hintsHeight, entering));
   }

   public void custom(float customHeight, SectionPanel.AsideRenderer entering, Runnable onClick, BooleanSupplier selected) {
      this.mainEntries.add(SectionPanel.Entry.custom(customHeight, entering, onClick, selected, null));
   }

   public void custom(float hintsHeight, SectionPanel.AsideRenderer entering, Runnable onClick, BooleanSupplier selected, Runnable onRightClick) {
      this.mainEntries.add(SectionPanel.Entry.custom(hintsHeight, entering, onClick, selected, onRightClick));
   }

   public BoundsData footerBounds() {
      return this.footerBounds;
   }

   public float mainHeaderHeight() {
      return 0.0F;
   }

   public void renderMainHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float mainWidth, float previewAlpha, ZenithStyle currentStyle) {
   }

   public BoundsData mainHeaderBounds() {
      return this.mainHeaderBounds;
   }

   public BoundsData lastRightClickBounds() {
      return this.lastRightClickBounds;
   }

   public void gap(float intValue) {
      this.mainEntries.add(SectionPanel.Entry.custom(intValue, (menuDrawContext, var1x, n, f, f2, f3, f4, zenithStyle) -> {}));
   }

   public void asideCaption(String elementName) {
      this.asideEntries
         .add(
            SectionPanel.Entry.caption(elementName, this.asideEntries.isEmpty())
         );
   }

   public void asideSetting(Setting hudBackground) {
      if (hudBackground != null) {
         this.asideEntries.add(SectionPanel.Entry.setting(new InspectorRow(hudBackground, 0)));
      }
   }

   public void asideSetting(InspectorRow codeRow) {
      if (codeRow != null) {
         this.asideEntries.add(SectionPanel.Entry.setting(codeRow));
      }
   }

   public void asideCustom(float customHeight, SectionPanel.AsideRenderer entering, Runnable onClick, BooleanSupplier selected) {
      this.asideEntries.add(SectionPanel.Entry.custom(customHeight, entering, onClick, selected, null));
   }

   public void asideCustom(float customHeight, SectionPanel.AsideRenderer asideEntering, Runnable onClick, BooleanSupplier selected, Runnable onRightClick) {
      this.asideEntries.add(SectionPanel.Entry.custom(customHeight, asideEntering, onClick, selected, onRightClick));
   }

   public void asideInfo(String serverName, Supplier<String> supplier, String description) {
      this.asideEntries.add(SectionPanel.Entry.info(serverName, supplier, description));
   }

   public void asideAction(String label, MenuGlyph icon, Supplier<String> supplier, Runnable onClick, String description) {
      this.asideEntries.add(SectionPanel.Entry.action(label, icon, supplier, onClick, description));
   }

   public void asideCustom(float hintsHeight, SectionPanel.AsideRenderer entering) {
      this.asideEntries.add(SectionPanel.Entry.custom(hintsHeight, entering));
   }

   public void asideFull(SectionPanel.AsideRenderer asideFull) {
      this.asideFull = asideFull;
   }

   private void ensureBuilt() {
      if (!this.built) {
         this.built = true;
         this.mainEntries.clear();
         this.asideEntries.clear();
         this.asideFull = null;
         this.build();
      }
   }

   @Override
   public boolean isHeaderSearchOnly() {
      return false;
   }

   @Override
   public void renderHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, float f, float f2, float f3) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         float floatValue = f + 8.0F;
         this.headerIcon().draw(menuDrawContext, floatValue, f2 + 8.5F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawText(font, this.headerTitle(), floatValue + 11.0F, f2 + (23.0F - font.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      }
   }

   @Override
   public float getButtonWidth() {
      this.ensureBuilt();
      if (!this.tabs.isEmpty() && !this.embedded) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
         float f = 0.0F;

         for (String tab : this.tabs) {
            f += font.width(t(tab)) + 16.0F;
         }

         return f;
      } else {
         return 0.0F;
      }
   }

   @Override
   public void renderHeaderButtons(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, float floatValue, float y, float mainWidth, float f) {
      if (!this.embedded) {
         this.ensureBuilt();
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null && !this.tabs.isEmpty()) {
            this.renderTabStrip(menuDrawContext, lastClickX, lastClickY, previewAlpha, floatValue, y, 23.0F, mainWidth, currentStyle, false);
         }
      }
   }

   private void renderTabStrip(
      MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, float floatValue, float y, float inlineTabsHeight, float mainWidth, ZenithStyle currentStyle, boolean flag
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      this.tabBounds.clear();
      boolean flag2 = !this.tabIcons.isEmpty();
      float f = 8.0F;
      boolean tabsScrollable = false;
      float f2 = flag ? floatValue + 8.0F - 2.0F : floatValue;
      float f3 = flag ? mainWidth - 12.0F : mainWidth;
      if (flag) {
         if (this.tabsWidth(font, flag2, f) > f3) {
            flag2 = false;
            float tabsWidth = this.tabsWidth(font, false, 0.0F);
            float f4;
            if ((f4 = f3 - tabsWidth) >= this.tabs.size() * 3.0F * 2.0F) {
               f = Math.min(8.0F, f4 / (this.tabs.size() * 2.0F));
            } else {
               float f5 = f3 - this.tabs.size() * 3.0F * 2.0F;
               float px;
               if ((px = (float)Math.floor(5.0F * f5 / tabsWidth * 10.0F) / 10.0F) >= 4.5F) {
                  font = Fonts.NEW_MEDIUM.getFont(px);
                  f = 3.0F;
               } else {
                  tabsScrollable = true;
                  f = 6.0F;
               }
            }
         }
      } else {
         float tabsWidth;
         if ((tabsWidth = this.tabsWidth(font, false, 3.0F)) > mainWidth && tabsWidth > 0.0F) {
            float tabsWidth2 = this.tabsWidth(font, false, 0.0F);
            float f4 = mainWidth - this.tabs.size() * 3.0F * 2.0F;
            float px = Math.max(3.8F, Math.round(5.0F * f4 / tabsWidth2 * 10.0F) / 10.0F);
            font = Fonts.NEW_MEDIUM.getFont(px);
         }

         for (int i = 0; i < 2 && !(this.tabsWidth(font, flag2, f) <= mainWidth); i++) {
            if (i != 0 || !flag2) {
               float tabsWidth2 = this.tabsWidth(font, flag2, 0.0F);
               f = Math.max(3.0F, (mainWidth - tabsWidth2) / (this.tabs.size() * 2.0F));
               break;
            }

            flag2 = false;
         }
      }

      float f4 = 0.0F;
      float floatValue2 = 0.0F;
      float floatValue3 = 0.0F;
      float[] floats = new float[this.tabs.size()];
      float[] floats2 = new float[this.tabs.size()];

      for (int i = 0; i < this.tabs.size(); i++) {
         String text = t(this.tabs.get(i));
         boolean flag3 = flag2 && i < this.tabIcons.size() && this.tabIcons.get(i) != null;
         float f5 = font.width(text) + f * 2.0F + (flag3 ? 9.0F : 0.0F);
         floats[i] = f4;
         floats2[i] = f5;
         if (i == this.activeTab) {
            floatValue2 = f4;
            floatValue3 = f5;
         }

         f4 += f5;
      }

      float tabScroll = 0.0F;
      if (tabsScrollable) {
         float max = Math.max(0.0F, f4 - f3);
         if (this.tabScrolledTo != this.activeTab) {
            this.tabScrolledTo = this.activeTab;
            float f5 = 12.0F;
            if (floatValue2 - f5 < this.tabScrollTarget) {
               this.tabScrollTarget = floatValue2 - f5;
            } else if (floatValue2 + floatValue3 + f5 > this.tabScrollTarget + f3) {
               this.tabScrollTarget = floatValue2 + floatValue3 + f5 - f3;
            }
         }

         this.tabScrollTarget = Mth.clamp(this.tabScrollTarget, 0.0F, max);
         this.tabScroll = Mth.clamp(this.tabScroll + (this.tabScrollTarget - this.tabScroll) * 0.25F, 0.0F, max);
         tabScroll = this.tabScroll;
      } else {
         this.tabScroll = 0.0F;
         this.tabScrollTarget = 0.0F;
         this.tabScrolledTo = -1;
      }

      this.tabsScrollable = tabsScrollable;
      this.tabViewport = flag ? new BoundsData(f2 + 2.0F, y, f3 - 4.0F, inlineTabsHeight) : null;

      for (int i = 0; i < this.tabs.size(); i++) {
         this.tabBounds.add(new BoundsData(f2 + floats[i] - tabScroll, y, floats2[i], inlineTabsHeight));
      }

      if (!this.plateReady) {
         this.plateX.method2809(floatValue2);
         this.plateWidth.method2809(floatValue3);
         this.plateReady = true;
      }

      float f5 = f2 - tabScroll + this.plateX.method2808(floatValue2);
      float f6 = this.plateWidth.method2808(floatValue3);
      float height = inlineTabsHeight - 7.0F;
      float width;
      float intValue = Math.min(width = Math.max(0.0F, f6 - 4.0F), height) / 2.0F;
      menuDrawContext.drawRoundedRect(
         f5 + 2.0F,
         y + (inlineTabsHeight - height) / 2.0F,
         width,
         height,
         ProxyDotRadiusData.method8069(intValue),
         currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha)
      );
      float f7 = f2 + f3;

      for (int i = 0; i < this.tabs.size(); i++) {
         BoundsData tabBound = this.tabBounds.get(i);
         String text = t(this.tabs.get(i));
         boolean flag3 = i == this.activeTab;
         boolean flag4 = tabBound.contains(lastClickX, lastClickY) && (this.tabViewport == null || this.tabViewport.contains(lastClickX, lastClickY));
         float f8 = 1.0F;
         if (tabsScrollable) {
            f8 = (
                  f8 = Mth.clamp(
                     (Math.min(tabBound.getFloatValue() + tabBound.width(), f7) - Math.max(tabBound.getFloatValue(), f2)) / tabBound.width(), 0.0F, 1.0F
                  )
               )
               * f8;
         }

         ZenithColor zenithColor = (flag3 ? currentStyle.getTextEnable() : currentStyle.getTextTertiary())
            .getZenithColor()
            .method7295(currentStyle.getTextEnable().getZenithColor(), flag4 && !flag3 ? 0.5F : 0.0F)
            .method7294(previewAlpha * f8);
         float floatValue4 = tabBound.getFloatValue() + f;
         if (flag2 && i < this.tabIcons.size() && this.tabIcons.get(i) != null) {
            this.tabIcons.get(i).draw(menuDrawContext, floatValue4, y + (inlineTabsHeight - 6.0F) / 2.0F, zenithColor);
            floatValue4 += 9.0F;
         }

         menuDrawContext.drawText(font, text, floatValue4, y + (inlineTabsHeight - font.height()) / 2.0F, zenithColor);
      }
   }

   private float tabsWidth(Font font, boolean flag, float f) {
      float f2 = 0.0F;

      for (int i = 0; i < this.tabs.size(); i++) {
         boolean flag2 = flag && i < this.tabIcons.size() && this.tabIcons.get(i) != null;
         f2 += font.width(t(this.tabs.get(i))) + f * 2.0F + (flag2 ? 9.0F : 0.0F);
      }

      return f2;
   }

   @Override
   public boolean onHeaderButtonsClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      return this.clickTabs(lastClickX, lastClickY);
   }

   private boolean clickTabs(double lastClickX, double lastClickY) {
      if (this.tabViewport != null && !this.tabViewport.contains(lastClickX, lastClickY)) {
         return false;
      } else {
         for (int i = 0; i < this.tabBounds.size(); i++) {
            if (this.tabBounds.get(i).contains(lastClickX, lastClickY)) {
               boolean flag = i != this.activeTab;
               this.selectTab(i);
               if (flag) {
                  Zenith.getInstance().getClientSounds().method1564(i, Math.max(1, this.tabBounds.size()));
               }

               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float panelAlpha, float f, float y) {
      this.ensureBuilt();
      this.panelAlpha = panelAlpha;
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         float f2 = NLMenuScreen.contentBottom - y;
         boolean asideFirst = this.asideFirst();
         float asideWidth = this.asideWidth();
         float mainWidth = this.mainWidth();
         boolean flag = asideWidth >= 1.0F;
         float floatValue = asideFirst ? f : f + mainWidth;
         float floatValue2 = asideFirst ? f + asideWidth : f;
         float floatValue3 = asideFirst ? f + asideWidth : f + mainWidth;
         if (flag) {
            menuDrawContext.drawRect(floatValue3, y, 1.0F, f2 - GuiStyle.PADDING.intValue(), currentStyle.getFieldBorder().getZenithColor().method7294(panelAlpha * 2.0F));
         }

         float f3 = this.switchAnimation.method2808(1.0F);
         float previewAlpha = panelAlpha * f3;
         float f4 = (1.0F - f3) * 6.0F;
         this.renderMain(menuDrawContext, lastClickX, lastClickY, floatValue2, y + f4, f2 - f4, previewAlpha, currentStyle);
         if (flag) {
            this.renderAside(menuDrawContext, lastClickX, lastClickY, floatValue, y + f4, f2 - f4, previewAlpha, currentStyle);
         } else if (!HoverInfo.hasText()) {
            HoverInfo.request(this.headerTitle(), t(this.sectionDescription()));
         }

         this.renderFloating(menuDrawContext, lastClickX, lastClickY, previewAlpha, currentStyle);
      }
   }

   public void renderFloating(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, ZenithStyle currentStyle) {
   }

   private void renderMain(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float f, float previewAlpha, ZenithStyle currentStyle) {
      float mainWidth = this.mainWidth();
      float inlineTabsHeight;
      if ((inlineTabsHeight = this.inlineTabsHeight()) > 0.0F) {
         MsdfRenderer.flushBatch();
         menuDrawContext.enableScissor(floatValue + 8.0F, y, floatValue + mainWidth - 8.0F, y + inlineTabsHeight);
         this.renderTabStrip(menuDrawContext, lastClickX, lastClickY, Math.min(1.0F, previewAlpha / Math.max(0.001F, this.switchAnimation.getValue())), floatValue, y, inlineTabsHeight, mainWidth, currentStyle, true);
         MsdfRenderer.flushBatch();
         menuDrawContext.disableScissor();
         menuDrawContext.drawRect(floatValue + 8.0F, y + inlineTabsHeight, mainWidth - 16.0F, 1.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha));
         y += inlineTabsHeight + GuiStyle.PADDING.intValue();
         f -= inlineTabsHeight + GuiStyle.PADDING.intValue();
      }

      float mainHeaderHeight;
      if ((mainHeaderHeight = this.mainHeaderHeight()) > 0.0F) {
         this.mainHeaderBounds = new BoundsData(floatValue, y, mainWidth, mainHeaderHeight);
         this.renderMainHeader(menuDrawContext, lastClickX, lastClickY, floatValue, y, mainWidth, previewAlpha, currentStyle);
         y += mainHeaderHeight;
         f -= mainHeaderHeight;
      } else {
         this.mainHeaderBounds = null;
      }

      float footerHeight = this.footerHeight();
      float height = f - GuiStyle.PADDING.intValue() - footerHeight;
      this.mainBounds = new BoundsData(floatValue, y, mainWidth, height);
      if (footerHeight > 0.0F) {
         this.footerBounds = new BoundsData(floatValue, y + height, mainWidth, footerHeight);
         this.renderFooter(menuDrawContext, lastClickX, lastClickY, floatValue, y + height, mainWidth, previewAlpha, currentStyle);
      } else {
         this.footerBounds = null;
      }

      InspectorRow.alignTracks(settingRows(this.mainEntries), mainWidth);
      float shownContentHeight = 0.0F;

      for (SectionPanel.Entry mainEntry : this.mainEntries) {
         shownContentHeight += mainEntry.height();
      }

      float max = footerHeight > 0.0F ? height - GuiStyle.PADDING.intValue() * 2.0F : height;
      this.clampScroll(shownContentHeight, max);
      this.scroll = this.mainScrollbar.isDragging() ? this.scrollTarget : this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      if (this.mainEntries.isEmpty()) {
         Font font = Fonts.NEW_REGULAR.getFont(5.0F);
         float centeredTextY = y + 40.0F;

         for (String text : wrapText(t(this.emptyText()), font, mainWidth - 16.0F, 4)) {
            menuDrawContext.drawText(font, text, floatValue + (mainWidth - font.width(text)) / 2.0F, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY += font.height() + 2.0F;
         }
      } else {
         float guiScaledHeight = y + max;
         MsdfRenderer.flushBatch();
         menuDrawContext.enableScissor(floatValue, y, floatValue + mainWidth, guiScaledHeight);
         float y2 = y + this.scroll;
         float guiScaledHeight2 = guiScaledHeight;
         int n = 0;

         for (SectionPanel.Entry mainEntry : this.mainEntries) {
            int n2 = n++;
            float height2 = mainEntry.height();
            if (mainEntry.cullable() && (!(y2 + height2 > y) || !(y2 < guiScaledHeight2))) {
               mainEntry.detach();
            } else {
               this.renderEntry(mainEntry, "main:" + n2, n2, false, menuDrawContext, lastClickX, lastClickY, floatValue, y2, mainWidth, previewAlpha, currentStyle);
            }

            y2 += height2;
         }

         this.renderMainOverlay(menuDrawContext);
         MsdfRenderer.flushBatch();
         menuDrawContext.disableScissor();
         this.mainScrollbar.render(menuDrawContext, floatValue + mainWidth - 2.0F, y, max, shownContentHeight, this.scroll, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.6F));
      }
   }

   public static List<String> wrapText(String description, Font font, float shownWidth, int n) {
      ArrayList<String> strings = new ArrayList<>();
      StringBuilder builder = new StringBuilder();

      for (String text : (description == null ? "" : description).split(" ")) {
         while (font.width(text) > shownWidth) {
            if (builder.length() > 0) {
               strings.add(builder.toString());
               if (strings.size() == n) {
                  return strings;
               }

               builder = new StringBuilder();
            }

            int endIndex = 1;

            while (endIndex < text.length() && font.width(text.substring(0, endIndex + 1)) <= shownWidth) {
               endIndex++;
            }

            strings.add(text.substring(0, endIndex));
            if (strings.size() == n) {
               return strings;
            }

            text = text.substring(endIndex);
         }

         String str = builder.length() == 0 ? text : builder + " " + text;
         if (font.width(str) > shownWidth && builder.length() > 0) {
            strings.add(builder.toString());
            if (strings.size() == n) {
               return strings;
            }

            builder = new StringBuilder(text);
         } else {
            builder = new StringBuilder(str);
         }
      }

      if (builder.length() > 0) {
         strings.add(builder.toString());
      }

      return strings;
   }

   public String emptyText() {
      return "gui.section.empty";
   }

   private static List<InspectorRow> settingRows(List<SectionPanel.Entry> mainEntries) {
      ArrayList<InspectorRow> inspectorRows = new ArrayList<>();

      for (SectionPanel.Entry mainEntry : mainEntries) {
         if (mainEntry.row != null) {
            inspectorRows.add(mainEntry.row);
         }
      }

      return inspectorRows;
   }

   public float footerHeight() {
      return 0.0F;
   }

   public void renderFooter(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float f, float mainWidth, float previewAlpha, ZenithStyle currentStyle) {
   }

   public boolean onFooterClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      return false;
   }

   private void renderAside(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float f, float previewAlpha, ZenithStyle currentStyle) {
      float asideWidth = this.asideWidth();
      float boxHeight = HoverInfo.boxHeight();
      float f2;
      float height = (f2 = y + f - boxHeight) - GuiStyle.PADDING.intValue() - y;
      float previewAlpha2 = previewAlpha * this.asideAlpha();
      if (this.asideFull != null) {
         this.asideBounds = null;
         this.asideFull.render(menuDrawContext, lastClickX, lastClickY, floatValue, y, asideWidth, previewAlpha2, currentStyle);
      } else {
         this.asideBounds = new BoundsData(floatValue, y, asideWidth, height);
         InspectorRow.alignTracks(settingRows(this.asideEntries), asideWidth);
         float shownContentHeight = 0.0F;

         for (SectionPanel.Entry asideEntry : this.asideEntries) {
            shownContentHeight += asideEntry.height();
         }

         this.asideScrollTarget = clamp(this.asideScrollTarget, shownContentHeight, height);
         this.asideScroll = clamp(this.asideScroll, shownContentHeight, height);
         this.asideScroll = this.asideScrollbar.isDragging() ? this.asideScrollTarget : this.asideScroll + (this.asideScrollTarget - this.asideScroll) * 0.25F;
         MsdfRenderer.flushBatch();
         menuDrawContext.enableScissor(floatValue, y, floatValue + asideWidth, y + height);
         float y2 = y + this.asideScroll;
         int n = 0;

         for (SectionPanel.Entry asideEntry : this.asideEntries) {
            int n2 = n++;
            float height2 = asideEntry.height();
            if (asideEntry.cullable() && (!(y2 + height2 > y) || !(y2 < y + height))) {
               asideEntry.detach();
            } else {
               this.renderEntry(asideEntry, "aside:" + n2, n2, true, menuDrawContext, lastClickX, lastClickY, floatValue, y2, asideWidth, previewAlpha2, currentStyle);
            }

            y2 += height2;
         }

         MsdfRenderer.flushBatch();
         menuDrawContext.disableScissor();
         this.asideScrollbar
            .render(menuDrawContext, floatValue + asideWidth - 2.0F, y, height, shownContentHeight, this.asideScroll, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.6F));
      }

      if (!(boxHeight <= 0.01F)) {
         menuDrawContext.drawRect(
            floatValue + 8.0F, f2 - GuiStyle.PADDING.intValue(), asideWidth - 16.0F, 1.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * boxHeight / 34.0F)
         );
         if (!HoverInfo.hasText()) {
            HoverInfo.request(this.headerTitle(), t(this.sectionDescription()));
         }

         HoverInfo.render(
            menuDrawContext, floatValue + 8.0F, f2 + GuiStyle.PADDING.intValue(), asideWidth - 16.0F, Math.max(0.0F, boxHeight - GuiStyle.PADDING.intValue() * 2.0F), previewAlpha
         );
      }
   }

   public float mainWidth() {
      return 236.0F;
   }

   public float asideWidth() {
      return 144.0F;
   }

   public boolean asideFirst() {
      return false;
   }

   public float asideAlpha() {
      return 1.0F;
   }

   public boolean isFullyVisible() {
      NLMenuScreen nlMenuScreen = Zenith.getInstance() == null ? null : Zenith.getInstance().getNLMenuScreen();
      float contentDip = nlMenuScreen == null ? 1.0F : nlMenuScreen.getContentDip();
      return this.panelAlpha > 0.995F * contentDip;
   }

   public void renderMainOverlay(MenuDrawContext menuDrawContext) {
   }

   public BoundsData mainViewport() {
      return this.mainBounds;
   }

   public BoundsData asideViewport() {
      return this.asideBounds;
   }

   public void resetScroll() {
      this.scroll = 0.0F;
      this.scrollTarget = 0.0F;
   }

   public void scrollToEnd() {
      this.scrollTarget = -8.5070587E37F;
      this.scroll = this.scrollTarget;
   }

   public void resetAsideScroll() {
      this.asideScroll = 0.0F;
      this.asideScrollTarget = 0.0F;
   }

   private static float clamp(float asideScrollTarget, float shownContentHeight, float height) {
      return shownContentHeight <= height ? 0.0F : Mth.clamp(asideScrollTarget, height - shownContentHeight, 0.0F);
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
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      this.ensureBuilt();
      if (this.embedded && this.clickTabs(lastClickX, lastClickY)) {
         return true;
      } else {
         float scrollTarget = this.mainScrollbar.onMouseClicked(lastClickX, lastClickY);
         if (this.mainScrollbar.isDragging()) {
            if (!Float.isNaN(scrollTarget)) {
               this.scrollTarget = scrollTarget;
            }

            return true;
         } else {
            scrollTarget = this.asideScrollbar.onMouseClicked(lastClickX, lastClickY);
            if (this.asideScrollbar.isDragging()) {
               if (!Float.isNaN(scrollTarget)) {
                  this.asideScrollTarget = scrollTarget;
               }

               return true;
            } else if (this.footerBounds != null && this.footerBounds.contains(lastClickX, lastClickY) && this.onFooterClicked(lastClickX, lastClickY, enum2)) {
               return true;
            } else {
               if (this.asideBounds == null || this.asideBounds.contains(lastClickX, lastClickY)) {
                  int n = 0;

                  for (SectionPanel.Entry entry : new ArrayList<>(this.asideEntries)) {
                     String str = "aside:" + n++;
                     if (entry.onMouseClicked(lastClickX, lastClickY, enum2)) {
                        this.markPress(entry, str, lastClickX, lastClickY);
                        return true;
                     }
                  }
               }

               if (this.mainBounds != null && this.mainBounds.contains(lastClickX, lastClickY)) {
                  int n = 0;

                  for (SectionPanel.Entry entry : new ArrayList<>(this.mainEntries)) {
                     String str = "main:" + n++;
                     if (enum2 == Enum3.RIGHT && entry.bounds != null && entry.bounds.contains(lastClickX, lastClickY)) {
                        this.lastRightClickBounds = entry.bounds;
                     }

                     if (entry.onMouseClicked(lastClickX, lastClickY, enum2)) {
                        this.markPress(entry, str, lastClickX, lastClickY);
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      }
   }

   @Override
   public boolean onMouseReleased(double x, double y, Enum3 enum2) {
      this.mainScrollbar.release();
      this.asideScrollbar.release();

      for (SectionPanel.Entry mainEntry : this.mainEntries) {
         mainEntry.onMouseReleased(x, y, enum2);
      }

      for (SectionPanel.Entry asideEntry : this.asideEntries) {
         asideEntry.onMouseReleased(x, y, enum2);
      }

      return false;
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int mouseToGlfw, double offsetX, double offsetY) {
      float scrollTarget;
      if (!Float.isNaN(scrollTarget = this.mainScrollbar.onMouseDragged(mouseY))) {
         this.scrollTarget = scrollTarget;
         return true;
      } else if (!Float.isNaN(scrollTarget = this.asideScrollbar.onMouseDragged(mouseY))) {
         this.asideScrollTarget = scrollTarget;
         return true;
      } else {
         boolean flag = false;

         for (SectionPanel.Entry mainEntry : this.mainEntries) {
            if (mainEntry.onMouseDragged(mouseX, mouseY)) {
               flag = true;
            }
         }

         for (SectionPanel.Entry asideEntry : this.asideEntries) {
            if (asideEntry.onMouseDragged(mouseX, mouseY)) {
               flag = true;
            }
         }

         return flag;
      }
   }

   @Override
   public boolean mouseScrolled(double lastClickX, double lastClickY, double d, double verticalAmount) {
      if (this.tabsScrollable && this.tabViewport != null && this.tabViewport.contains(lastClickX, lastClickY)) {
         this.tabScrollTarget -= (float)(verticalAmount + d) * 24.0F;
         return true;
      } else if (this.asideBounds != null && this.asideBounds.contains(lastClickX, lastClickY)) {
         this.asideScrollTarget = this.asideScrollTarget + (float)verticalAmount * this.scrollStep();
         return true;
      } else if (this.mainBounds != null && this.mainBounds.contains(lastClickX, lastClickY)) {
         this.scrollTarget = this.scrollTarget + (float)verticalAmount * this.scrollStep();
         return true;
      } else {
         return false;
      }
   }

   public float scrollStep() {
      return 18.0F;
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      for (SectionPanel.Entry mainEntry : this.mainEntries) {
         if (mainEntry.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      for (SectionPanel.Entry asideEntry : this.asideEntries) {
         if (asideEntry.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      for (SectionPanel.Entry mainEntry : this.mainEntries) {
         if (mainEntry.charTyped(codepoint, n)) {
            return true;
         }
      }

      for (SectionPanel.Entry asideEntry : this.asideEntries) {
         if (asideEntry.charTyped(codepoint, n)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public List<? extends Element> getElements() {
      return List.of();
   }

   @Override
   public void close() {
   }

   public interface AsideRenderer {
      void render(MenuDrawContext menuDrawContext, int mouseX, int mouseY, float floatValue, float y, float asideWidth, float previewAlpha, ZenithStyle currentStyle);
   }

   private static final class Entry {
      private final SectionPanel.Entry.Kind kind;
      private final String label;
      private final MenuGlyph icon;
      private final Supplier<String> value;
      private final String description;
      private final Runnable onClick;
      private final Runnable onRightClick;
      private final BooleanSupplier selected;
      private final boolean accentValue;
      private final InspectorRow row;
      private final float customHeight;
      private final SectionPanel.AsideRenderer renderer;
      private BoundsData bounds;
      private boolean lead;
      private SectionPanel.IconRenderer iconRenderer;
      private final Animation hoverAnimation = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private final Animation selectAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private String fitSource;
      private float fitRoom = Float.NaN;
      private Object fitFont;
      private float fitSize;
      private String fitShown;

      private Entry(
         SectionPanel.Entry.Kind kind,
         String label,
         MenuGlyph icon,
         Supplier<String> supplier,
         String description,
         Runnable onClick,
         Runnable onRightClick,
         BooleanSupplier selected,
         boolean accentValue,
         InspectorRow row,
         float customHeight,
         SectionPanel.AsideRenderer renderer
      ) {
         this.kind = kind;
         this.label = label;
         this.icon = icon;
         this.value = supplier;
         this.description = description;
         this.onClick = onClick;
         this.onRightClick = onRightClick;
         this.selected = selected;
         this.accentValue = accentValue;
         this.row = row;
         this.customHeight = customHeight;
         this.renderer = renderer;
      }

      private static SectionPanel.Entry caption(String key, boolean lead) {
         SectionPanel.Entry entry = new SectionPanel.Entry(SectionPanel.Entry.Kind.CAPTION, key, null, null, null, null, null, null, false, null, 0.0F, null);
         entry.lead = lead;
         return entry;
      }

      private static SectionPanel.Entry setting(InspectorRow inspectorRow) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.SETTING, null, null, null, null, null, null, null, false, inspectorRow, 0.0F, null);
      }

      private static SectionPanel.Entry info(String serverName, Supplier<String> supplier, String description) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.ROW, serverName, null, supplier, description, null, null, null, false, null, 0.0F, null);
      }

      private static SectionPanel.Entry action(String label, MenuGlyph icon, Supplier<String> supplier, Runnable onClick, String description) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.ROW, label, icon, supplier, description, onClick, null, null, true, null, 0.0F, null);
      }

      private static SectionPanel.Entry row(
         String label, MenuGlyph icon, Supplier<String> supplier, String description, Runnable onClick, Runnable onRightClick, BooleanSupplier selected, boolean accentValue
      ) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.ROW, label, icon, supplier, description, onClick, onRightClick, selected, accentValue, null, 0.0F, null);
      }

      private static SectionPanel.Entry custom(float hintsHeight, SectionPanel.AsideRenderer entering) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.CUSTOM, null, null, null, null, null, null, null, false, null, hintsHeight, entering);
      }

      private static SectionPanel.Entry custom(float customHeight, SectionPanel.AsideRenderer entering, Runnable onClick, BooleanSupplier selected, Runnable onRightClick) {
         return new SectionPanel.Entry(SectionPanel.Entry.Kind.CUSTOM, null, null, null, null, onClick, onRightClick, selected, false, null, customHeight, entering);
      }

      private float height() {
         return switch (this.kind) {
            case CAPTION -> this.lead ? 10.0F : 16.0F;
            case SETTING -> this.row.height();
            default -> 16.0F;
            case CUSTOM -> this.customHeight;
         };
      }

      private boolean cullable() {
         return this.kind != SectionPanel.Entry.Kind.SETTING;
      }

      private void detach() {
         this.bounds = null;
      }

      private void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float mainWidth, float previewAlpha, ZenithStyle currentStyle) {
         switch (this.kind) {
            case CAPTION:
               Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
               menuDrawContext.drawText(
                  font,
                  HoverInfo.spaced(SectionPanel.t(this.label).toUpperCase(Locale.ROOT)),
                  floatValue + 8.0F,
                  y + (16.0F - font.height()) / 2.0F + 2.0F - (this.lead ? 6.0F : 0.0F),
                  currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               break;
            case SETTING:
               this.row.render(menuDrawContext, lastClickX, lastClickY, floatValue, y, mainWidth, previewAlpha);
               break;
            case ROW:
            default:
               this.renderTextRow(menuDrawContext, lastClickX, lastClickY, floatValue, y, mainWidth, previewAlpha, currentStyle);
               break;
            case CUSTOM:
               if (this.onClick != null || this.onRightClick != null) {
                  this.bounds = new BoundsData(floatValue, y, mainWidth, this.customHeight);
               }

               this.renderer.render(menuDrawContext, lastClickX, lastClickY, floatValue, y, mainWidth, previewAlpha, currentStyle);
         }
      }

      private void renderTextRow(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float mainWidth, float previewAlpha, ZenithStyle currentStyle) {
         this.bounds = new BoundsData(floatValue, y, mainWidth, 16.0F);
         boolean contains = this.bounds.contains(lastClickX, lastClickY);
         boolean flag = this.onClick != null || this.onRightClick != null;
         boolean flag2 = this.selected != null && this.selected.getAsBoolean();
         float f = this.hoverAnimation.method2808(contains && flag ? 1.0F : 0.0F);
         float f2 = this.selectAnimation.method2808(flag2 ? 1.0F : 0.0F);
         float f3 = Math.max(f2, f * 0.45F);
         if (f3 > 0.002F) {
            menuDrawContext.drawRect(floatValue, y, mainWidth, 16.0F, currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * f3));
         }

         if (f2 > 0.002F) {
            float height = 12.0F * f2;
            menuDrawContext.drawRect(floatValue, y + (16.0F - height) / 2.0F, 2.0F, height, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
         }

         if (contains && this.description != null && !this.description.isBlank()) {
            HoverInfo.request(SectionPanel.t(this.label), SectionPanel.t(this.description));
         }

         Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
         ZenithColor zenithColor = currentStyle.getTextSecondary()
            .getZenithColor()
            .method7295(currentStyle.getTextEnable().getZenithColor(), Math.max(f2, f * 0.6F))
            .method7294(previewAlpha);
         float floatValue2 = floatValue + 8.0F;
         boolean renderIcon = Zenith.getInstance().getNLMenuScreen().isRenderIcon();
         if (renderIcon && this.iconRenderer != null && this.iconRenderer.draw(menuDrawContext, floatValue2, y + 8.0F, 14.0F, previewAlpha)) {
            floatValue2 += 14.0F;
         } else if (this.icon != null && renderIcon) {
            this.icon.draw(menuDrawContext, floatValue2, y + 5.0F, zenithColor);
            floatValue2 += 9.0F;
         }

         String text = SectionPanel.t(this.label);
         menuDrawContext.drawText(font, text, floatValue2, y + (16.0F - font.height()) / 2.0F, zenithColor);
         String fitSource = this.value == null ? null : this.value.get();
         if (fitSource != null && !fitSource.isEmpty()) {
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
            float fitRoom = floatValue + mainWidth - 8.0F - (floatValue2 + font.width(text)) - 6.0F;
            String fitShown;
            if (fitRoom == this.fitRoom && font2.getFont() == this.fitFont && font2.getSize() == this.fitSize && fitSource.equals(this.fitSource)) {
               fitShown = this.fitShown;
            } else {
               fitShown = fit(font2, fitSource, fitRoom);
               this.fitSource = fitSource;
               this.fitRoom = fitRoom;
               this.fitFont = font2.getFont();
               this.fitSize = font2.getSize();
               this.fitShown = fitShown;
            }

            if (!fitShown.isEmpty()) {
               menuDrawContext.drawText(
                  font2,
                  fitShown,
                  floatValue + mainWidth - 8.0F - font2.width(fitShown),
                  y + (16.0F - font2.height()) / 2.0F,
                  (this.accentValue ? currentStyle.getPrimaryColor() : currentStyle.getTextEnable()).getZenithColor().method7294(previewAlpha)
               );
               if (contains && !fitShown.equals(fitSource)) {
                  HoverInfo.request(text, fitSource);
               }
            }
         }
      }

      private static String fit(Font font, String fitSource, float fitRoom) {
         if (fitRoom <= 0.0F) {
            return "";
         } else if (font.width(fitSource) <= fitRoom) {
            return fitSource;
         } else {
            String text = "…";
            float width = font.width(text);
            if (width > fitRoom) {
               return "";
            } else {
               int length = fitSource.length();

               while (length > 0 && font.width(fitSource.substring(0, length)) + width > fitRoom) {
                  length--;
               }

               return length == 0 ? "" : fitSource.substring(0, length) + text;
            }
         }
      }

      private boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
         if (this.kind == SectionPanel.Entry.Kind.SETTING) {
            return this.row.onMouseClicked(lastClickX, lastClickY, enum2);
         } else if ((this.kind == SectionPanel.Entry.Kind.ROW || this.kind == SectionPanel.Entry.Kind.CUSTOM)
            && this.bounds != null
            && this.bounds.contains(lastClickX, lastClickY)) {
            if (enum2 == Enum3.RIGHT && this.onRightClick != null) {
               this.onRightClick.run();
               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            } else if (this.onClick != null) {
               this.onClick.run();
               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      private void onMouseReleased(double x, double y, Enum3 enum2) {
         if (this.kind == SectionPanel.Entry.Kind.SETTING) {
            this.row.onMouseReleased(x, y, enum2);
         }
      }

      private boolean onMouseDragged(double mouseX, double mouseY) {
         return this.kind == SectionPanel.Entry.Kind.SETTING && this.row.onMouseDragged(mouseX, mouseY);
      }

      private boolean keyPressed(int keyCode, int keycode, int modifiers) {
         return this.kind == SectionPanel.Entry.Kind.SETTING && this.row.keyPressed(keyCode, keycode, modifiers);
      }

      private boolean charTyped(char codepoint, int n) {
         return this.kind == SectionPanel.Entry.Kind.SETTING && this.row.charTyped(codepoint, n);
      }

      private static enum Kind {
         CAPTION,
         SETTING,
         ROW,
         CUSTOM;
      }
   }

   @FunctionalInterface
   public interface IconRenderer {
      boolean draw(MenuDrawContext menuDrawContext, float f, float f2, float f3, float previewAlpha);
   }

   public final class RowBuilder {
      private SectionPanel.IconRenderer iconRenderer;
      private final String label;
      private final List<SectionPanel.Entry> target;
      private MenuGlyph icon;
      private Supplier<String> value;
      private String description;
      private Runnable onClick;
      private Runnable onRightClick;
      private BooleanSupplier selected;
      private boolean accentValue;

      private RowBuilder(String label, List<SectionPanel.Entry> target) {
         this.label = label;
         this.target = target;
      }

      public SectionPanel.RowBuilder icon(MenuGlyph icon) {
         this.icon = icon;
         return this;
      }

      public SectionPanel.RowBuilder iconRenderer(SectionPanel.IconRenderer iconRenderer) {
         this.iconRenderer = iconRenderer;
         return this;
      }

      public SectionPanel.RowBuilder value(Supplier<String> supplier) {
         this.value = supplier;
         return this;
      }

      public SectionPanel.RowBuilder description(String description) {
         this.description = description;
         return this;
      }

      public SectionPanel.RowBuilder onClick(Runnable onClick) {
         this.onClick = onClick;
         return this;
      }

      public SectionPanel.RowBuilder onRightClick(Runnable onRightClick) {
         this.onRightClick = onRightClick;
         return this;
      }

      public SectionPanel.RowBuilder selected(BooleanSupplier selected) {
         this.selected = selected;
         return this;
      }

      public SectionPanel.RowBuilder accent() {
         this.accentValue = true;
         return this;
      }

      public void add() {
         SectionPanel.Entry row = SectionPanel.Entry.row(
            this.label, this.icon, this.value, this.description, this.onClick, this.onRightClick, this.selected, this.accentValue
         );
         row.iconRenderer = this.iconRenderer;
         this.target.add(row);
      }

      public void addAside() {
         SectionPanel.Entry row = SectionPanel.Entry.row(
            this.label, this.icon, this.value, this.description, this.onClick, this.onRightClick, this.selected, this.accentValue
         );
         row.iconRenderer = this.iconRenderer;
         SectionPanel.this.asideEntries.add(row);
      }
   }
}

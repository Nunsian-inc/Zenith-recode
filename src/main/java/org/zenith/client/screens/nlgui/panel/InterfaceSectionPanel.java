package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.StyleManager;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ClientSounds;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class InterfaceSectionPanel extends SectionPanel {
   private static final int TAB_THEME = 0;
   private static final int TAB_HUD = 1;
   private static final int TAB_MENU = 2;
   private static final int CARD_COLUMNS = 2;
   private static final float CARD_GAP = 5.0F;
   private static final float CARD_PREVIEW = 28.0F;
   private static final float CARD_LABEL = 13.0F;
   private static final float CARD_HEIGHT = 41.0F;
   private static final float HUD_ROW_HEIGHT = 16.0F;
   private static final float HUD_HEADER_HEIGHT = 28.0F;
   private static final float SWITCH_WIDTH = 9.0F;
   private static final float SWITCH_HEIGHT = 5.0F;
   private static final float SWITCH_KNOB = 3.0F;
   private static final float EMPTY_HEIGHT = 22.0F;
   private static final String GROUP_COMBAT = "gui.interface.hud.group.combat";
   private static final String GROUP_INFO = "gui.interface.hud.group.info";
   private static final String GROUP_CONTROL = "gui.interface.hud.group.control";
   private static final String GROUP_CLIENT = "gui.interface.hud.group.client";
   private static final String GROUP_OTHER = "gui.interface.hud.group.other";
   private static final String[] GROUP_ORDER = new String[]{
      "gui.interface.hud.group.combat",
      "gui.interface.hud.group.info",
      "gui.interface.hud.group.control",
      "gui.interface.hud.group.client",
      "gui.interface.hud.group.other"
   };
   private static final Map<String, String> GROUPS = new HashMap<>();
   private static final Map<String, MenuGlyph> GLYPHS = new HashMap<>();
   private static final String[] HINTS = new String[]{"gui.interface.hud.hint.pick", "gui.interface.hud.hint.toggle", "gui.interface.hud.hint.drag"};
   private final Map<ZenithStyle, BoundsData> cardBounds = new LinkedHashMap<>();
   private final Map<ZenithStyle, Animation> cardHover = new HashMap<>();
   private final Map<Interface, BoundsData> switchBounds = new LinkedHashMap<>();
   private final Map<Interface, Animation> switchKnob = new HashMap<>();
   private final Map<Interface, Animation> rowHover = new HashMap<>();
   private final Map<Interface, Animation> rowSelect = new HashMap<>();
   private final Animation countBar = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private Interface selectedElement;
   private String lastSignature = "";
   private ZenithStyle lastStyle;
   private final BindSetting menuBind = new InterfaceSectionPanel.MenuBindSetting();

   @Override
   public String headerTitle() {
      return "Interface";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.INTERFACE;
   }

   @Override
   public String sectionDescription() {
      return "gui.interface.desc";
   }

   @Override
   public String emptyText() {
      return "gui.section.empty";
   }

   public boolean isHudTab() {
      return this.getActiveTab() == 1;
   }

   public void openHudElementSettings(Interface selectedElement) {
      this.selectedElement = selectedElement;
      this.selectTab(1);
      this.invalidate();
   }

   @Override
   public void tick() {
      super.tick();
      String lastSignature = this.getActiveTab() + "|" + this.searchQuery();
      ZenithStyle currentStyle = styles().getCurrentStyle();
      if (!lastSignature.equals(this.lastSignature) || currentStyle != this.lastStyle) {
         this.lastSignature = lastSignature;
         this.lastStyle = currentStyle;
         this.invalidate();
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float panelAlpha, float f, float y) {
      this.cardBounds.clear();
      this.switchBounds.clear();
      super.render(menuDrawContext, lastClickX, lastClickY, panelAlpha, f, y);
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      BoundsData mainViewport = this.mainViewport();
      if (enum2 == MouseButton.LEFT && mainViewport != null && mainViewport.contains(lastClickX, lastClickY)) {
         for (Map.Entry entry : this.switchBounds.entrySet()) {
            if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               this.toggleElement((Interface)entry.getKey());
               return true;
            }
         }

         for (Map.Entry entry : this.cardBounds.entrySet()) {
            if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               styles().setCurrentStyle((ZenithStyle)entry.getKey());
               Zenith.getInstance().getClientSounds().method1570(false);
               this.invalidate();
               return true;
            }
         }
      }

      return super.onMouseClicked(lastClickX, lastClickY, enum2);
   }

   @Override
   public void build() {
      this.tabs(new String[]{"gui.interface.tab.theme", "gui.interface.tab.hud", "gui.interface.tab.menu"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.COLOR, MenuGlyph.INTERFACE, MenuGlyph.WINDOW});
      switch (this.getActiveTab()) {
         case 1:
            this.buildHud();
            break;
         case 2:
            this.buildMenu();
            break;
         default:
            this.buildTheme();
      }
   }

   private void buildMenu() {
      this.caption("gui.interface.caption.menuControl");
      this.setting(this.menuBind);

      for (Setting autoAfk : MenuModule.INSTANCE.method5349()) {
         this.setting(autoAfk);
      }

      this.asideCaption("gui.interface.caption.menuAnimation");

      for (Setting hudBackground : MenuModule.INSTANCE.method5350()) {
         this.asideSetting(hudBackground);
      }
   }

   private static StyleManager styles() {
      return Zenith.getInstance().getStyleManager();
   }

   private void buildTheme() {
      String searchQuery = this.searchQuery();
      StyleManager styleManager = styles();
      ArrayList arrayList = new ArrayList();

      for (ZenithStyle style : styleManager.getStyles()) {
         if (matches(styleName(style), searchQuery)) {
            arrayList.add(style);
         }
      }

      this.caption("gui.interface.caption.styles");
      if (arrayList.isEmpty()) {
         this.custom(22.0F, this::renderNoMatch);
      } else {
         for (byte i = 0; i < arrayList.size(); i += 2) {
            byte b = i;
            this.custom(
               46.0F,
               (var3x, lastClickX, var5x, var6x, y, var8x, var9x, var10x) -> this.renderCardRow(var3x, lastClickX, var5x, var6x, y, var8x, var9x, var10x, arrayList, b)
            );
         }
      }

      this.gap(GuiStyle.PADDING.intValue() * 2.0F);
      this.caption("gui.interface.caption.look");

      for (Setting autoAfk : InterfaceModule.INSTANCE.method6771()) {
         this.setting(autoAfk);
      }

      ZenithStyle currentStyle = styleManager.getCurrentStyle();
      if (currentStyle != null) {
         this.asideCaption("gui.interface.caption.colors");
         this.asideSetting(currentStyle.getPrimaryColor());
         this.asideSetting(currentStyle.getSecondaryPrimaryColor());
         this.asideSetting(currentStyle.getTextEnable());
         this.asideSetting(currentStyle.getTextSecondary());
         this.asideSetting(currentStyle.getTextTertiary());
         this.asideSetting(currentStyle.getLeftBackground());
         this.asideSetting(currentStyle.getRightBackground());
         this.asideSetting(currentStyle.getHudBackground());
         this.asideSetting(currentStyle.getFriendColor());
      }
   }

   private static String styleName(ZenithStyle style) {
      return style.getName() != null && !style.getName().isBlank() ? style.getName() : "Style";
   }

   private void renderCardRow(
      MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float y, float f2, float previewAlpha, ZenithStyle zenithStyle, List<ZenithStyle> zenithStyles, int n
   ) {
      float f3 = f2 - 16.0F;
      float width = (f3 - 5.0F) / 2.0F;

      for (int i = 0; i < 2; i++) {
         int n2 = n + i;
         if (n2 >= zenithStyles.size()) {
            return;
         }

         this.renderCard(menuDrawContext, lastClickX, lastClickY, (ZenithStyle)zenithStyles.get(n2), f + 8.0F + i * (width + 5.0F), y, width, previewAlpha, zenithStyle);
      }
   }

   private void renderCard(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, ZenithStyle style, float floatValue, float y, float width, float previewAlpha, ZenithStyle zenithStyle) {
      boolean flag = style == styles().getCurrentStyle();
      BoundsData boundsData = new BoundsData(floatValue, y, width, 41.0F);
      this.cardBounds.put(style, boundsData);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float f = this.cardHover.computeIfAbsent(style, zenithStyle2 -> new Animation(140L, 0.0F, Easing.INSTANCE12)).animateTo(contains ? 1.0F : 0.0F);
      if (contains) {
         HoverInfo.request(styleName(style), t("gui.interface.style.desc"));
      }

      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F);
      menuDrawContext.drawRoundedRect(
         floatValue,
         y,
         width,
         41.0F,
         headerRadius,
         zenithStyle.getFieldSurfaceBackground().getZenithColor().method7295(zenithStyle.getDisableActiveBg().getZenithColor(), 0.35F + f * 0.5F).method7294(previewAlpha)
      );
      float floatValue2 = floatValue + 3.0F;
      float y2 = y + 3.0F;
      float width2 = width - 6.0F;
      float height = 22.0F;
      ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(3.0F);
      float width3 = width2 * 0.34F;
      menuDrawContext.drawRoundedRect(floatValue2, y2, width2, height, proxyDotRadiusData, style.getRightBackground().getZenithColor().method7293(255).method7294(previewAlpha));
      menuDrawContext.drawRoundedRect(
         floatValue2, y2, width3, height, ProxyDotRadiusData.method8078(3.0F, 3.0F), style.getLeftBackground().getZenithColor().method7293(255).method7294(previewAlpha)
      );
      float width4 = width3 - 5.0F;
      menuDrawContext.drawRoundedRect(
         floatValue2 + 2.5F, y2 + 3.5F, width4, 1.8F, ProxyDotRadiusData.method8069(0.9F), style.getPrimaryColor().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedRect(
         floatValue2 + 2.5F,
         y2 + 8.0F,
         width4 * 0.7F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         style.getTextTertiary().getZenithColor().method7293(255).method7294(previewAlpha * 0.7F)
      );
      menuDrawContext.drawRoundedRect(
         floatValue2 + 2.5F,
         y2 + 12.0F,
         width4 * 0.85F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         style.getTextTertiary().getZenithColor().method7293(255).method7294(previewAlpha * 0.5F)
      );
      float floatValue3 = floatValue2 + width3 + 3.0F;
      float f2 = width2 - width3 - 6.0F;
      menuDrawContext.drawRoundedRect(
         floatValue3,
         y2 + 3.5F,
         f2 * 0.8F,
         1.8F,
         ProxyDotRadiusData.method8069(0.9F),
         style.getTextEnable().getZenithColor().method7293(255).method7294(previewAlpha * 0.8F)
      );
      menuDrawContext.drawRoundedRect(
         floatValue3,
         y2 + 8.0F,
         f2 * 0.55F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         style.getTextSecondary().getZenithColor().method7293(255).method7294(previewAlpha * 0.55F)
      );
      menuDrawContext.drawRoundedRect(
         floatValue3,
         y2 + 12.0F,
         f2 * 0.68F,
         1.3F,
         ProxyDotRadiusData.method8069(0.65F),
         style.getTextTertiary().getZenithColor().method7293(255).method7294(previewAlpha * 0.5F)
      );
      menuDrawContext.drawRoundedRect(
         floatValue3 + f2 - 5.0F,
         y2 + height - 5.5F,
         5.0F,
         2.4F,
         ProxyDotRadiusData.method8069(1.2F),
         style.getSecondaryPrimaryColor().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedBorder(floatValue2, y2, width2, height, 0.3F, proxyDotRadiusData, style.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      float centeredTextY = y + 28.0F + (13.0F - font.height()) / 2.0F - 1.0F;
      float f3 = width - 8.0F - (flag ? 9.0F : 0.0F);
      String text = fit(font, styleName(style), f3);
      menuDrawContext.drawText(
         font,
         text,
         floatValue + 4.0F,
         centeredTextY,
         (flag ? zenithStyle.getTextEnable() : zenithStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha * (flag ? 1.0F : 0.75F + f * 0.25F))
      );
      if (flag) {
         MenuGlyph.CHECK.draw(menuDrawContext, floatValue + width - 4.0F - 6.0F, y + 28.0F + 3.5F - 1.0F, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      }

      ZenithColor zenithColor = flag
         ? zenithStyle.getPrimaryColor().getZenithColor().method7292(0.5F)
         : zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F + f * 1.5F);
      menuDrawContext.drawRoundedBorder(floatValue, y, width, 41.0F, 0.2F, headerRadius, zenithColor.method7294(previewAlpha));
   }

   @Override
   public float mainHeaderHeight() {
      return this.getActiveTab() == 1 ? 28.0F : 0.0F;
   }

   @Override
   public void renderMainHeader(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      List<Interface> interfaceValues = InterfaceModule.INSTANCE.method6794();
      int size = interfaceValues.size();
      int n3 = 0;

      for (Interface interfaceValue : interfaceValues) {
         if (interfaceValue.isEnabled()) {
            n3++;
         }
      }

      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String text = t("gui.interface.hud.shown");
      String str = n3 + " / " + size;
      float centeredTextY = f2 + 3.0F;
      menuDrawContext.drawText(font, text, f + 8.0F, centeredTextY, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawText(font2, str, f + f3 - 8.0F - font2.width(str), centeredTextY, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      float width = f3 - 16.0F;
      float y = centeredTextY + font.height() + 5.0F;
      menuDrawContext.drawRoundedRect(f + 8.0F, y, width, 2.0F, ProxyDotRadiusData.method8069(1.0F), zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha));
      float f4 = this.countBar.animateTo(size == 0 ? 0.0F : (float)n3 / size);
      if (f4 > 0.001F) {
         menuDrawContext.drawRoundedRect(
            f + 8.0F,
            y,
            Math.max(2.0F, width * f4),
            2.0F,
            ProxyDotRadiusData.method8069(1.0F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   private void buildHud() {
      List<Interface> interfaceValues = InterfaceModule.INSTANCE.method6794();
      if (this.selectedElement != null && !interfaceValues.contains(this.selectedElement)) {
         this.selectedElement = null;
      }

      String searchQuery = this.searchQuery();
      LinkedHashMap map = new LinkedHashMap();

      for (String key : GROUP_ORDER) {
         map.put(key, new ArrayList());
      }

      for (Interface selectedElement : interfaceValues) {
         if (matches(elementName(selectedElement), searchQuery) || matches(selectedElement.getName(), searchQuery)) {
            ((List)map.get(GROUPS.getOrDefault(selectedElement.getName(), "gui.interface.hud.group.other"))).add(selectedElement);
         }
      }

      boolean flag = false;

      for (String key : GROUP_ORDER) {
         List<Interface> interfaceValues2 = (List)map.get(key);
         if (!interfaceValues2.isEmpty()) {
            flag = true;
            this.caption(key);

            for (Interface selectedElement : interfaceValues2) {
               this.custom(
                  16.0F,
                  (var2x, var3x, lastClickY, floatValue, y, var7x, var8x, var9x) -> this.renderHudRow(var2x, var3x, lastClickY, floatValue, y, var7x, var8x, var9x, selectedElement),
                  () -> this.toggleElement(selectedElement),
                  () -> selectedElement == this.selectedElement,
                  () -> {
                     this.selectedElement = selectedElement;
                     this.resetAsideScroll();
                     this.invalidate();
                  }
               );
            }
         }
      }

      if (!flag) {
         this.custom(22.0F, this::renderNoMatch);
      }

      this.buildHudAside();
   }

   private void toggleElement(Interface key) {
      key.toggle();
      ClientSounds clientSounds = Zenith.getInstance().getClientSounds();
      clientSounds.method1561(key.isEnabled() ? clientSounds.soundEvent5 : clientSounds.soundEvent6);
   }

   private void renderHudRow(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float width, float previewAlpha, ZenithStyle zenithStyle, Interface selectedElement) {
      BoundsData boundsData = new BoundsData(floatValue, y, width, 16.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float f = this.rowHover.computeIfAbsent(selectedElement, interfaceValue -> new Animation(120L, 0.0F, Easing.INSTANCE12)).animateTo(contains ? 1.0F : 0.0F);
      float f2 = this.rowSelect
         .computeIfAbsent(selectedElement, interfaceValue -> new Animation(140L, 0.0F, Easing.INSTANCE12))
         .animateTo(selectedElement == this.selectedElement ? 1.0F : 0.0F);
      float f3 = Math.max(f2, f * 0.45F);
      if (f3 > 0.002F) {
         menuDrawContext.drawRect(floatValue, y, width, 16.0F, zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * f3));
      }

      if (f2 > 0.002F) {
         float height = 11.0F * f2;
         menuDrawContext.drawRect(floatValue, y + (16.0F - height) / 2.0F, 2.0F, height, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      }

      boolean enabled = selectedElement.isEnabled();
      Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
      String text = t(enabled ? "gui.value.on" : "gui.value.off");
      float width2 = font.width(text);
      float f4 = Math.max(font.width(t("gui.value.on")), font.width(t("gui.value.off")));
      float f5 = floatValue + width - 8.0F;
      float floatValue2 = f5 - f4 - GuiStyle.PADDING.intValue() - 9.0F;
      BoundsData boundsData2 = new BoundsData(floatValue2, y, f5 - floatValue2, 16.0F);
      this.switchBounds.put(selectedElement, boundsData2);
      boolean flag = boundsData2.contains(lastClickX, lastClickY);
      String name = elementName(selectedElement);
      if (contains) {
         HoverInfo.request(name, t(flag ? "gui.interface.hud.show.desc" : "gui.interface.hud.row.desc"));
      }

      ZenithColor zenithColor = zenithStyle.getTextSecondary()
         .getZenithColor()
         .method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(f2, f * 0.6F))
         .method7294(previewAlpha);
      float floatValue3 = floatValue + 8.0F;
      if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
         MenuGlyph menuGlyph = GLYPHS.getOrDefault(selectedElement.getName(), MenuGlyph.INTERFACE);
         menuGlyph.draw(menuDrawContext, floatValue3, y + 5.0F, zenithColor);
         floatValue3 += 9.0F;
      }

      Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font font3 = Fonts.NEW_REGULAR.getFont(4.4F);
      String str = elementTag(selectedElement);
      float f6 = floatValue2 - GuiStyle.PADDING.intValue() - floatValue3;
      float f7 = str.isEmpty() ? 0.0F : font3.width(str) + GuiStyle.PADDING.intValue();
      String fit2 = fit(font2, name, f6 - f7);
      menuDrawContext.drawText(font2, fit2, floatValue3, y + (16.0F - font2.height()) / 2.0F, zenithColor);
      if (!str.isEmpty() && font2.width(fit2) + f7 <= f6) {
         menuDrawContext.drawText(
            font3,
            str,
            floatValue3 + font2.width(fit2) + GuiStyle.PADDING.intValue(),
            y + (16.0F - font3.height()) / 2.0F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.85F)
         );
      }

      menuDrawContext.drawText(
         font,
         text,
         f5 - f4,
         y + (16.0F - font.height()) / 2.0F,
         (enabled ? zenithStyle.getTextEnable() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
      );
      float hover = this.switchKnob.computeIfAbsent(selectedElement, interfaceValue -> new Animation(140L, 0.0F, Easing.INSTANCE10)).animateTo(enabled ? 1.0F : 0.0F);
      this.drawSwitch(menuDrawContext, floatValue2, y + 5.5F, hover, flag, previewAlpha, zenithStyle);
   }

   private void drawSwitch(MenuDrawContext menuDrawContext, float floatValue, float y, float hover, boolean flag, float previewAlpha, ZenithStyle zenithStyle) {
      menuDrawContext.drawRect(floatValue, y, 9.0F, 5.0F, zenithStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 3.0F));
      menuDrawContext.drawRoundedBorder(
         floatValue, y, 9.0F, 5.0F, 0.1F, ProxyDotRadiusData.INSTANCE, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * (flag ? 6.0F : 4.0F))
      );
      float f = floatValue + 1.0F + 4.0F * hover;
      menuDrawContext.drawRect(
         f, y + 1.0F, 3.0F, 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover).method7294(previewAlpha)
      );
   }

   private void buildHudAside() {
      Interface selectedElement = this.selectedElement;
      if (selectedElement == null) {
         this.asideCaption("gui.interface.caption.actions");

         for (Setting hudBackground : InterfaceModule.INSTANCE.method6773()) {
            this.asideSetting(hudBackground);
         }

         this.asideCaption("gui.interface.caption.panels");
         ZenithStyle currentStyle = styles().getCurrentStyle();
         if (currentStyle != null) {
            this.asideSetting(currentStyle.getHudBackground());
         }

         for (Setting hudBackground : InterfaceModule.INSTANCE.method6774()) {
            this.asideSetting(hudBackground);
         }

         this.asideCaption("gui.interface.caption.layout");

         for (Setting hudBackground : InterfaceModule.INSTANCE.method6775()) {
            this.asideSetting(hudBackground);
         }

         this.asideCaption("gui.interface.caption.behaviour");

         for (Setting hudBackground : InterfaceModule.INSTANCE.method6776()) {
            this.asideSetting(hudBackground);
         }

         this.asideCaption("gui.interface.caption.game");

         for (Setting hudBackground : InterfaceModule.INSTANCE.method6772()) {
            this.asideSetting(hudBackground);
         }

         this.asideCaption("gui.interface.caption.element");
         this.asideCustom(this.hintsHeight(), this::renderHints);
      } else {
         List<Setting> settings = selectedElement.method2058();
         if (!settings.isEmpty()) {
            this.asideCaption(elementName(selectedElement));

            for (Setting hudBackground : settings) {
               this.asideSetting(hudBackground);
            }

            this.asideCaption(t("gui.interface.caption.display"));
         } else {
            this.asideCaption(elementName(selectedElement));
         }

         for (Setting hudBackground : selectedElement.method2057()) {
            this.asideSetting(hudBackground);
         }
      }
   }

   private static String elementTag(Interface selectedElement) {
      ArrayList elements = new ArrayList(2);
      if (selectedElement.method2060()) {
         elements.add(t("gui.interface.hud.tag.conditional"));
      }

      if (selectedElement.method2059()) {
         elements.add(t("gui.interface.hud.tag.locked"));
      }

      if (Math.abs(selectedElement.getScale().getValue3() - 100.0F) >= 1.0F) {
         elements.add(Math.round(selectedElement.getScale().getValue3()) + "%");
      }

      return String.join(" · ", elements);
   }

   private static Font hintFont() {
      return Fonts.NEW_REGULAR.getFont(4.4F);
   }

   private float hintsHeight() {
      Font font = hintFont();
      float shownWidth = this.asideWidth() - 16.0F - 7.0F;
      float f = 6.0F;

      for (String key : HINTS) {
         f += wrapText(t(key), font, shownWidth, 3).size() * (font.height() + 2.0F) + 5.0F;
      }

      return f;
   }

   private void renderHints(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      Font font = hintFont();
      float shownWidth = f3 - 16.0F - 7.0F;
      float centeredTextY = f2 + 6.0F;

      for (String key : HINTS) {
         menuDrawContext.drawRoundedRect(
            f + 8.0F,
            centeredTextY + font.height() / 2.0F - 1.0F,
            2.0F,
            2.0F,
            ProxyDotRadiusData.method8069(1.0F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.8F)
         );

         for (String text : wrapText(t(key), font, shownWidth, 3)) {
            menuDrawContext.drawText(font, text, f + 8.0F + 7.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY += font.height() + 2.0F;
         }

         centeredTextY += 5.0F;
      }
   }

   private void renderNoMatch(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      String text = fit(font, t("gui.interface.search.empty"), f3 - 16.0F);
      menuDrawContext.drawText(
         font, text, f + (f3 - font.width(text)) / 2.0F, f2 + (22.0F - font.height()) / 2.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
   }

   private static String elementName(Interface selectedElement) {
      String key = "gui.interface.hud.name." + selectedElement.getName();
      String str = t(key);
      return str.equals(key) ? selectedElement.getName() : str;
   }

   private static boolean matches(String styleName, String searchQuery) {
      return searchQuery.isEmpty() || styleName.toLowerCase(Locale.ROOT).contains(searchQuery);
   }

   private static String fit(Font font, String styleName, float f) {
      if (f <= 0.0F) {
         return "";
      } else if (font.width(styleName) <= f) {
         return styleName;
      } else {
         String text = "…";
         float width = font.width(text);
         if (width > f) {
            return "";
         } else {
            int length = styleName.length();

            while (length > 0 && font.width(styleName.substring(0, length)) + width > f) {
               length--;
            }

            return length == 0 ? "" : styleName.substring(0, length) + text;
         }
      }
   }

   static {
      GROUPS.put("TargetHud", "gui.interface.hud.group.combat");
      GROUPS.put("ArmorHud", "gui.interface.hud.group.combat");
      GROUPS.put("Cooldowns", "gui.interface.hud.group.combat");
      GROUPS.put("Potions", "gui.interface.hud.group.combat");
      GROUPS.put("TargetPotions", "gui.interface.hud.group.combat");
      GROUPS.put("Staffs", "gui.interface.hud.group.combat");
      GROUPS.put("Information", "gui.interface.hud.group.info");
      GROUPS.put("Coordinates", "gui.interface.hud.group.info");
      GROUPS.put("TimerBar", "gui.interface.hud.group.info");
      GROUPS.put("ScoreBoard", "gui.interface.hud.group.info");
      GROUPS.put("AnimatedTab", "gui.interface.hud.group.info");
      GROUPS.put("Events", "gui.interface.hud.group.info");
      GROUPS.put("Inventory", "gui.interface.hud.group.info");
      GROUPS.put("Party", "gui.interface.hud.group.info");
      GROUPS.put("Keybinds", "gui.interface.hud.group.control");
      GROUPS.put("ItemBinds", "gui.interface.hud.group.control");
      GROUPS.put("HootBar", "gui.interface.hud.group.control");
      GROUPS.put("Logo", "gui.interface.hud.group.client");
      GROUPS.put("Watermark", "gui.interface.hud.group.client");
      GROUPS.put("Notifications", "gui.interface.hud.group.client");
      GROUPS.put("MusicInfo", "gui.interface.hud.group.client");
      GLYPHS.put("TargetHud", MenuGlyph.TARGET);
      GLYPHS.put("ArmorHud", MenuGlyph.CHESTPLATE);
      GLYPHS.put("Cooldowns", MenuGlyph.TIMER);
      GLYPHS.put("Potions", MenuGlyph.POTION);
      GLYPHS.put("TargetPotions", MenuGlyph.POTION);
      GLYPHS.put("Staffs", MenuGlyph.SHIELD);
      GLYPHS.put("Information", MenuGlyph.CLIENT);
      GLYPHS.put("Coordinates", MenuGlyph.GPS);
      GLYPHS.put("TimerBar", MenuGlyph.TIMER);
      GLYPHS.put("ScoreBoard", MenuGlyph.CONFIGS);
      GLYPHS.put("AnimatedTab", MenuGlyph.FRIENDS);
      GLYPHS.put("Events", MenuGlyph.WORLD);
      GLYPHS.put("Inventory", MenuGlyph.STORAGE);
      GLYPHS.put("Party", MenuGlyph.FRIENDS);
      GLYPHS.put("Keybinds", MenuGlyph.KEY);
      GLYPHS.put("ItemBinds", MenuGlyph.ITEMS);
      GLYPHS.put("HootBar", MenuGlyph.SLOT);
      GLYPHS.put("Logo", MenuGlyph.CLIENT);
      GLYPHS.put("Watermark", MenuGlyph.PIN);
      GLYPHS.put("Notifications", MenuGlyph.CHAT);
      GLYPHS.put("MusicInfo", MenuGlyph.SOUND);
   }

   private static final class MenuBindSetting extends BindSetting {
      private MenuBindSetting() {
         super("gui.interface.menu.bind", "gui.interface.menu.bind.desc");
      }

      @Override
      public int getKeyCode() {
         return MenuModule.INSTANCE.getKeyCode();
      }

      @Override
      public int getModifiers() {
         return MenuModule.INSTANCE.getKeyModifiers();
      }

      @Override
      public String getNameKey() {
         return KeyName.method6702(MenuModule.INSTANCE.getKeyCode(), MenuModule.INSTANCE.getKeyModifiers());
      }

      @Override
      public void setBind(int keyCode, int keyModifiers) {
         if (keyCode != -1) {
            MenuModule.INSTANCE.setKeyCode(keyCode);
            MenuModule.INSTANCE.setKeyModifiers(KeyName.method6698(keyModifiers));
         }
      }

      @Override
      public void safe(JsonObject json) {
      }

      @Override
      public void load(JsonObject json) {
      }
   }
}

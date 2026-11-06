package org.zenith.client.screens.nlgui.panel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.cloud.CloudCard;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.ConfigSyncService;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ConfigInfoPage {
   public static final int[] PRICES = new int[]{0, 49, 99, 199, 499};
   private static final String[] LOAD_KEYS = new String[]{"gui.configs.load.all", "gui.configs.load.noBinds", "gui.configs.load.themeOnly"};
   private static final String[] VISIBILITY = new String[]{"PRIVATE", "FRIENDS", "CODE"};
   private static final String[] VISIBILITY_KEYS = new String[]{"gui.configs.vis.private", "gui.configs.vis.friends", "gui.configs.vis.code"};
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor WARN = new ZenithColor(247, 206, 59);
   private static final SimpleDateFormat DATE = new SimpleDateFormat("dd.MM HH:mm", Locale.ROOT);
   private final ConfigInfoPage.Host host;
   private final FormField name = new FormField(48, false);
   private final FormField description = new FormField(160, true);
   private final List<FormField> fields = List.of(this.name, this.description);
   private final Map<String, BoundsData> buttons = new LinkedHashMap<>();
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Long> pressed = new HashMap<>();
   private volatile List<StoreApi.Data> codes = List.of();
   private final Smooth scroller = new Smooth(16.0F);
   private final Animation appear = new Animation(320L, 0.0F, Easing.INSTANCE15);
   private String fileName;
   private String display;
   private int loadMode;
   private int priceIndex;
   private boolean busy;
   private String armed = "";
   private long armedAt;
   private BoundsData bounds;
   private volatile String message = "";
   private volatile boolean messageError;
   private volatile long messageAt;
   private static final long FILE_STAT_MS = 1000L;
   private String statFile;
   private String statLanguage;
   private long statAt;
   private String statText = "";

   public void message(String str, boolean messageError) {
      this.message = str == null ? "" : str;
      this.messageError = messageError;
      this.messageAt = System.currentTimeMillis();
   }

   public ConfigInfoPage(ConfigInfoPage.Host host) {
      this.host = host;
   }

   public String fileName() {
      return this.fileName;
   }

   public void open(String fileName, String display) {
      boolean flag = fileName.equals(this.fileName);
      this.fileName = fileName;
      this.display = display;
      if (!flag) {
         this.name.set(display);
         this.name.commit();
         StoreApi.Data2 twin = this.twin();
         this.description.set(twin == null ? "" : twin.description());
         this.description.commit();
         StoreApi.Data5 data5 = twin == null ? StoreApi.Data5.INSTANCE : StoreApi.method1960(twin.getUUID());
         this.priceIndex = 0;

         for (int i = 0; i < PRICES.length; i++) {
            if (PRICES[i] == data5.getIntValue()) {
               this.priceIndex = i;
            }
         }

         this.armed = "";
         this.codes = List.of();
         this.statFile = null;
         this.scroller.reset(0.0F);
         this.appear.reset(0.0F);
         this.appear.setTarget(1.0F);

         for (FormField field : this.fields) {
            field.setFocused(false);
         }

         if (twin != null) {
            this.loadCodes(twin);
         }
      }
   }

   private StoreApi.Data2 twin() {
      return ConfigSyncService.getInstance().method672(this.display);
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   public boolean isDirty() {
      return this.name.changed() || this.description.changed();
   }

   public boolean isTyping() {
      for (FormField field : this.fields) {
         if (field.isFocused()) {
            return true;
         }
      }

      return false;
   }

   private float hot(String key, boolean contains) {
      return this.hover.computeIfAbsent(key, str -> new Animation(150L, 0.0F, Easing.INSTANCE12)).animateTo(contains ? 1.0F : 0.0F);
   }

   private static void card(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float floatValue, float y, float width, float height, float hot, float previewAlpha) {
      ScriptsSectionPanel.card(menuDrawContext, currentStyle, floatValue, y, width, height, hot, previewAlpha);
   }

   private float button(
      MenuDrawContext menuDrawContext,
      ZenithStyle currentStyle,
      String key,
      String str,
      MenuGlyph menuGlyph,
      float floatValue,
      float y,
      float width,
      int lastClickX,
      int lastClickY,
      float previewAlpha,
      int n
   ) {
      BoundsData boundsData = new BoundsData(floatValue, y, width, 16.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.hot(key, contains);
      ZenithColor zenithColor = n == 2 ? ERROR : currentStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      float press = Smooth.press(this.pressed.getOrDefault(key, 0L), 320L);
      ZenithColor zenithColor2 = currentStyle.getFieldSurfaceBackground().getZenithColor().method7295(currentStyle.getDisableActiveBg().getZenithColor(), 0.35F + hot * 0.55F);
      if (n > 0) {
         zenithColor2 = zenithColor2.method7295(zenithColor.method7292(0.14F), 0.3F + hot * 0.35F + (this.armed.equals(key) ? 0.4F : 0.0F));
      }

      menuDrawContext.drawRoundedRect(floatValue, y, width, 16.0F, headerRadius, zenithColor2.method7294(previewAlpha));
      if (press > 0.01F) {
         float f = (1.0F - press) * 3.0F;
         menuDrawContext.drawRoundedRect(floatValue - f * 0.5F, y - f * 0.5F, width + f, 16.0F + f, headerRadius, zenithColor.method7292(0.35F * press).method7294(previewAlpha));
      }

      menuDrawContext.drawRoundedBorder(
         floatValue,
         y,
         width,
         16.0F,
         0.15F,
         headerRadius,
         (n > 0 ? zenithColor.method7292(0.2F + hot * 0.25F) : currentStyle.getFieldBorder().getZenithColor().method7294(2.0F + hot * 2.0F)).method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      float f = floatValue + 7.0F;
      if (menuGlyph != null) {
         menuGlyph.draw(menuDrawContext, f, y + 5.0F, (n > 0 ? zenithColor : currentStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha));
         f += 10.0F;
      }

      String name = this.armed.equals(key) ? t("gui.scripts.info.confirm") : str;
      menuDrawContext.drawText(
         font,
         font.clip(name, floatValue + width - f - 5.0F),
         menuGlyph == null ? floatValue + (width - font.width(font.clip(name, width - 10.0F))) / 2.0F : f,
         y + (16.0F - font.height()) / 2.0F,
         (n > 0 ? zenithColor : currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hot)).method7294(previewAlpha)
      );
      this.buttons.put(key, boundsData);
      return 16.0F;
   }

   private float chips(
      MenuDrawContext menuDrawContext, ZenithStyle currentStyle, String str, String[] strings, int loadMode, float f, float y, float f2, int lastClickX, int lastClickY, float previewAlpha
   ) {
      float f3 = 3.0F;
      float width = (f2 - f3 * (strings.length - 1)) / strings.length;

      for (int i = 0; i < strings.length; i++) {
         this.button(menuDrawContext, currentStyle, str + i, strings[i], null, f + i * (width + f3), y, width, lastClickX, lastClickY, previewAlpha, i == loadMode ? 1 : 0);
      }

      return 16.0F;
   }

   private float field(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, String text, FormField name, String str, float floatValue, float centeredTextY, float shownWidth, float previewAlpha) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      Font shownFont = Fonts.NEW_MEDIUM.getFont(5.0F);
      menuDrawContext.drawText(font, t(text), floatValue, centeredTextY, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      float render = name.render(
         menuDrawContext,
         shownFont,
         floatValue,
         centeredTextY + font.height() + 3.0F,
         shownWidth,
         previewAlpha,
         t(str),
         currentStyle.getFieldSurfaceBackground().getZenithColor().method7295(currentStyle.getDisableActiveBg().getZenithColor(), 0.3F),
         currentStyle.getFieldBorder().getZenithColor().method7294(2.5F),
         currentStyle.getPrimaryColor().getZenithColor().method7292(0.6F),
         currentStyle.getTextEnable().getZenithColor(),
         currentStyle.getTextTertiary().getZenithColor()
      );
      return font.height() + 3.0F + render + 7.0F;
   }

   private static void caption(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, String text, float floatValue, float centeredTextY, float previewAlpha) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);
      menuDrawContext.drawText(font, text, floatValue, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   private static void line(MenuDrawContext menuDrawContext, String text, ZenithColor zenithColor, float floatValue, float centeredTextY, float width, float previewAlpha) {
      Font font = Fonts.NEW_REGULAR.getFont(4.3F);
      menuDrawContext.drawText(font, font.clip(text, width), floatValue, centeredTextY, zenithColor.method7294(previewAlpha));
   }

   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float width, float height, float f, ZenithStyle currentStyle) {
      if (this.fileName != null) {
         float previewAlpha = f * this.appear.animateTo(1.0F);
         floatValue += (1.0F - this.appear.getValue()) * 10.0F;
         this.bounds = new BoundsData(floatValue, y, width, height);
         this.buttons.clear();
         if (!this.armed.isEmpty() && System.currentTimeMillis() - this.armedAt > 3000L) {
            this.armed = "";
         }

         float update = this.scroller.update();
         float f2 = 8.0F;
         float floatValue2 = floatValue + f2;
         float width2 = width - f2 * 2.0F;
         Font font = Fonts.NEW_REGULAR.getFont(4.3F);
         ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
         BoundsData boundsData = new BoundsData(floatValue2 - 2.0F, y + 1.0F, 12.0F, 12.0F);
         boolean contains = boundsData.contains(lastClickX, lastClickY);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
         menuDrawContext.drawText(
            font2,
            "‹",
            boundsData.getFloatValue() + 3.0F,
            boundsData.getFloatValue2() + (12.0F - font2.height()) / 2.0F,
            (contains ? currentStyle.getTextEnable() : currentStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
         );
         this.buttons.put("close", boundsData);
         Font font3 = Fonts.NEW_MEDIUM.getFont(4.2F);
         menuDrawContext.drawText(
            font3,
            t("gui.configs.info.settings"),
            floatValue2 + 12.0F,
            y + 1.0F + (12.0F - font3.height()) / 2.0F,
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
         if (this.isDirty()) {
            String text = t("gui.scripts.ws.save");
            float width3 = font3.width(text) + 12.0F;
            BoundsData boundsData2 = new BoundsData(floatValue + width - f2 - width3, y, width3, 13.0F);
            boolean flag = boundsData2.contains(lastClickX, lastClickY);
            menuDrawContext.drawRoundedRect(
               boundsData2.getFloatValue(),
               boundsData2.getFloatValue2(),
               boundsData2.width(),
               boundsData2.height(),
               ProxyDotRadiusData.method8069(6.5F),
               zenithColor.method7294(previewAlpha * (flag ? 1.0F : 0.8F))
            );
            menuDrawContext.drawText(
               font3, text, boundsData2.getFloatValue() + 6.0F, boundsData2.getFloatValue2() + (13.0F - font3.height()) / 2.0F, ZenithColor.INSTANCE.method7294(previewAlpha)
            );
            this.buttons.put("save", boundsData2);
         }

         float y2 = y + 18.0F;
         MsdfRenderer.flushBatch();
         menuDrawContext.enableScissor(floatValue, y2, floatValue + width, y + height);
         float y3 = y2 + update;
         StoreApi.Data2 twin = this.twin();
         ConfigSyncService.Type type = ConfigSyncService.getInstance().method671(this.display);
         card(menuDrawContext, currentStyle, floatValue2, y3, width2, 44.0F, 0.0F, previewAlpha);
         float width3 = 32.0F;
         ZenithColor letterColor = CloudConfigsSectionPanel.letterColor(this.display);
         menuDrawContext.drawRoundedRect(floatValue2 + 6.0F, y3 + 6.0F, width3, width3, ProxyDotRadiusData.method8069(width3 * 0.28F), letterColor.method7292(0.16F).method7294(previewAlpha));
         Font font4 = Fonts.NEW_SEMIBOLD.getFont(width3 * 0.38F);
         String upperCase = this.display.isEmpty() ? "?" : this.display.substring(0, 1).toUpperCase(Locale.ROOT);
         menuDrawContext.drawText(
            font4, upperCase, floatValue2 + 6.0F + (width3 - font4.width(upperCase)) / 2.0F, y3 + 6.0F + (width3 - font4.height()) / 2.0F, letterColor.method7294(previewAlpha)
         );
         Font font5 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         float floatValue3 = floatValue2 + 6.0F + width3 + 6.0F;
         float width4 = floatValue2 + width2 - floatValue3 - 4.0F;
         menuDrawContext.drawText(
            font5,
            font5.clip(this.name.get().isEmpty() ? this.display : this.name.get(), width4),
            floatValue3,
            y3 + 9.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
         );
         String name = this.fileStat()
            + (twin == null ? "" : "  ·  v" + twin.getLongValue2() + (twin.getIntValue() > 0 ? "  ·  ♥ " + twin.getIntValue() : ""));
         menuDrawContext.drawText(font, font.clip(name, width4), floatValue3, y3 + 11.0F + font5.height(), currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         y3 += 52.0F;
         y3 += this.field(menuDrawContext, currentStyle, "gui.configs.info.name", this.name, "gui.configs.creator.hint", floatValue2, y3, width2, previewAlpha);
         y3 += this.field(menuDrawContext, currentStyle, "gui.configs.info.description", this.description, "gui.configs.info.description.hint", floatValue2, y3, width2, previewAlpha);
         float width5 = (width2 - 4.0F) / 2.0F;
         caption(menuDrawContext, currentStyle, t("gui.configs.caption.apply"), floatValue2, y3, previewAlpha);
         y3 += 9.0F;
         String[] strings = new String[LOAD_KEYS.length];

         for (int i = 0; i < LOAD_KEYS.length; i++) {
            strings[i] = t(LOAD_KEYS[i]);
         }

         y3 += this.chips(menuDrawContext, currentStyle, "load:", strings, this.loadMode, floatValue2, y3, width2, lastClickX, lastClickY, previewAlpha) + 4.0F;
         this.button(menuDrawContext, currentStyle, "apply", t("gui.configs.apply"), MenuGlyph.CHECK, floatValue2, y3, width5, lastClickX, lastClickY, previewAlpha, 1);
         this.button(menuDrawContext, currentStyle, "overwrite", t("gui.configs.overwrite"), MenuGlyph.COPY, floatValue2 + width5 + 4.0F, y3, width5, lastClickX, lastClickY, previewAlpha, 0);
         y3 += 26.0F;
         String str = t("gui.configs.sync.state." + type.name().toLowerCase(Locale.ROOT));
         caption(menuDrawContext, currentStyle, t("gui.configs.caption.cloud") + "  ·  " + str, floatValue2, y3, previewAlpha);
         y3 += 9.0F;
         if (twin != null) {
            String[] strings2 = new String[VISIBILITY_KEYS.length];

            for (int i = 0; i < VISIBILITY_KEYS.length; i++) {
               strings2[i] = t(VISIBILITY_KEYS[i]);
            }

            int loadMode = visibilityIndex(twin.getText7());
            y3 += this.chips(menuDrawContext, currentStyle, "vis:", strings2, loadMode, floatValue2, y3, width2, lastClickX, lastClickY, previewAlpha) + 4.0F;
            this.button(menuDrawContext, currentStyle, "sync", t("gui.configs.syncNow"), MenuGlyph.CLOUD, floatValue2, y3, width5, lastClickX, lastClickY, previewAlpha, 0);
            this.button(menuDrawContext, currentStyle, "code", t("gui.configs.issueCode"), MenuGlyph.KEY, floatValue2 + width5 + 4.0F, y3, width5, lastClickX, lastClickY, previewAlpha, 0);
            y3 += 20.0F;

            for (StoreApi.Data code : this.codes) {
               BoundsData boundsData2 = new BoundsData(floatValue2, y3, width2, 11.0F);
               boolean flag = boundsData2.contains(lastClickX, lastClickY);
               boolean equalsIgnoreCase = "ACTIVE".equalsIgnoreCase(code.getText2());
               line(
                  menuDrawContext,
                  code.getText().isBlank() ? "••••••" : code.getText(),
                  flag ? currentStyle.getTextEnable().getZenithColor() : (equalsIgnoreCase ? currentStyle.getTextSecondary().getZenithColor() : currentStyle.getTextTertiary().getZenithColor()),
                  floatValue2 + 2.0F,
                  y3 + 2.0F,
                  width2 * 0.7F,
                  previewAlpha
               );
               String lowerCase = code.getText2().toLowerCase(Locale.ROOT);
               menuDrawContext.drawText(font, lowerCase, floatValue2 + width2 - font.width(lowerCase), y3 + 2.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               this.buttons.put("copy:" + code.getUUID(), boundsData2);
               if (flag) {
                  HoverInfo.request(t("gui.scripts.market.codes"), t("gui.configs.code.copy.desc"));
               }

               y3 += 12.0F;
            }

            y3 += 4.0F;
         } else {
            line(menuDrawContext, t("gui.configs.info.notInCloud"), currentStyle.getTextTertiary().getZenithColor(), floatValue2, y3, width2, previewAlpha);
            y3 += 10.0F;
            this.button(menuDrawContext, currentStyle, "sync", t("gui.configs.syncNow"), MenuGlyph.CLOUD, floatValue2, y3, width2, lastClickX, lastClickY, previewAlpha, 0);
            y3 += 24.0F;
         }

         caption(menuDrawContext, currentStyle, t("gui.market.caption.publish"), floatValue2, y3, previewAlpha);
         y3 += 9.0F;
         StoreApi.Data5 data5 = twin == null ? StoreApi.Data5.INSTANCE : StoreApi.method1960(twin.getUUID());
         y3 += marketStatus(menuDrawContext, currentStyle, data5, floatValue2, y3, width2, previewAlpha);
         String[] strings2 = new String[PRICES.length];

         for (int i = 0; i < PRICES.length; i++) {
            strings2[i] = PRICES[i] == 0 ? t("gui.market.price.free") : PRICES[i] + " ₽";
         }

         boolean flag = data5.pending() || data5.method2007();
         if (!flag) {
            y3 += this.chips(menuDrawContext, currentStyle, "price:", strings2, this.priceIndex, floatValue2, y3, width2, lastClickX, lastClickY, previewAlpha) + 4.0F;
            String str2 = this.busy
               ? t("gui.scripts.market.busy")
               : (PRICES[this.priceIndex] == 0 ? t("gui.market.publish.free") : t("gui.market.publish.review"));
            this.button(menuDrawContext, currentStyle, "market.submit", str2, MenuGlyph.STORAGE, floatValue2, y3, width2, lastClickX, lastClickY, previewAlpha, 1);
         } else {
            this.button(
               menuDrawContext,
               currentStyle,
               "market.withdraw",
               this.busy ? t("gui.scripts.market.busy") : t("gui.market.withdraw"),
               MenuGlyph.CLOSE,
               floatValue2,
               y3,
               width2,
               lastClickX,
               lastClickY,
               previewAlpha,
               0
            );
         }

         y3 += 26.0F;
         caption(menuDrawContext, currentStyle, t("gui.scripts.info.caption.manage"), floatValue2, y3, previewAlpha);
         y3 += 9.0F;
         this.button(menuDrawContext, currentStyle, "folder", t("gui.configs.openFolder"), MenuGlyph.STORAGE, floatValue2, y3, width5, lastClickX, lastClickY, previewAlpha, 0);
         this.button(menuDrawContext, currentStyle, "delete", t("gui.configs.deleteEverywhere"), MenuGlyph.CLOSE, floatValue2 + width5 + 4.0F, y3, width5, lastClickX, lastClickY, previewAlpha, 2);
         y3 += 22.0F;
         long l = System.currentTimeMillis() - this.messageAt;
         if (!this.message.isEmpty() && l < 8000L) {
            float f3 = l > 7000L ? 1.0F - (float)(l - 7000L) / 1000.0F : Math.min(1.0F, (float)l / 180.0F);

            for (String text : SectionPanel.wrapText(this.message, font, width2, 3)) {
               menuDrawContext.drawText(font, text, floatValue2, y3, (this.messageError ? ERROR : OK).method7294(previewAlpha * f3));
               y3 += font.height() + 2.0F;
            }

            y3 += 4.0F;
         }

         MsdfRenderer.flushBatch();
         menuDrawContext.disableScissor();
         float f3 = y3 - y3 + 6.0F;
         float f4 = y + height - y2;
         this.scroller.clamp(Math.min(0.0F, f4 - f3), 0.0F);
         if (f3 > f4 + 1.0F) {
            float height2 = Math.max(14.0F, f4 * f4 / f3);
            float y4 = y2 + -update / (f3 - f4) * (f4 - height2);
            menuDrawContext.drawRoundedRect(
               floatValue + width - 3.0F, y4, 1.5F, height2, ProxyDotRadiusData.method8069(0.75F), currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.5F)
            );
         }

         for (Entry entry : this.buttons.entrySet()) {
            if (((String)entry.getKey()).startsWith("price:") && ((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               HoverInfo.request(t("gui.market.price"), t("gui.market.price.config.desc"));
            }
         }
      }
   }

   private String fileStat() {
      long now = System.currentTimeMillis();
      String languageName = Zenith.getInstance().getLocalization().getLanguageName();
      if (!this.fileName.equals(this.statFile) || !languageName.equals(this.statLanguage) || now - this.statAt >= 1000L) {
         File file = new File(Manager.file, this.fileName);
         this.statText = CloudCard.size(file.length()) + "  ·  " + DATE.format(new Date(file.lastModified()));
         this.statFile = this.fileName;
         this.statLanguage = languageName;
         this.statAt = now;
      }

      return this.statText;
   }

   public static float marketStatus(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, StoreApi.Data5 data5, float floatValue, float centeredTextY, float shownWidth, float previewAlpha) {
      String description;
      ZenithColor zenithColor;
      if (data5.pending()) {
         description = t("gui.market.status.pending");
         zenithColor = WARN;
      } else if (data5.method2007()) {
         description = t("gui.market.status.approved")
            + "  ·  "
            + (data5.method2009() ? data5.getIntValue() + " ₽" : t("gui.market.price.free"))
            + (data5.isFlag() ? "  ·  " + t("gui.market.status.sealed") : "");
         zenithColor = OK;
      } else if (data5.method2008()) {
         description = t("gui.market.status.rejected") + (data5.getText2().isBlank() ? "" : ": " + data5.getText2());
         zenithColor = ERROR;
      } else {
         description = t("gui.market.status.none");
         zenithColor = currentStyle.getTextTertiary().getZenithColor();
      }

      Font font = Fonts.NEW_REGULAR.getFont(4.3F);
      List<String> wrapText = SectionPanel.wrapText(description, font, shownWidth, 3);
      float f = centeredTextY;

      for (String text : wrapText) {
         menuDrawContext.drawText(font, text, floatValue, f, zenithColor.method7294(previewAlpha));
         f += font.height() + 2.0F;
      }

      return f - centeredTextY + 4.0F;
   }

   private static int visibilityIndex(String anotherString) {
      for (int i = 0; i < VISIBILITY.length; i++) {
         if (VISIBILITY[i].equalsIgnoreCase(anotherString)) {
            return i;
         }
      }

      return -1;
   }

   public boolean mouseClicked(double lastClickX, double lastClickY) {
      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         for (FormField field : this.fields) {
            BoundsData bounds = field.bounds();
            field.setFocused(bounds != null && bounds.contains(lastClickX, lastClickY));
         }

         for (Entry entry : this.buttons.entrySet()) {
            if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               this.pressed.put((String)entry.getKey(), System.currentTimeMillis());
               this.action((String)entry.getKey());
               return true;
            }
         }

         return true;
      } else {
         for (FormField field : this.fields) {
            field.setFocused(false);
         }

         return false;
      }
   }

   public boolean mouseScrolled(double lastClickX, double lastClickY, double verticalAmount) {
      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         this.scroller.add((float)verticalAmount * 28.0F);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      for (FormField field : this.fields) {
         if (field.isFocused()) {
            if (field != this.name || keyCode != 257 && keyCode != 335) {
               field.keyPressed(keyCode, modifiers);
               return true;
            }

            this.save();
            return true;
         }
      }

      return false;
   }

   public boolean charTyped(char codepoint) {
      for (FormField field : this.fields) {
         if (field.isFocused()) {
            return field.charTyped(codepoint);
         }
      }

      return false;
   }

   private boolean confirm(String armed) {
      if (this.armed.equals(armed) && System.currentTimeMillis() - this.armedAt < 3000L) {
         this.armed = "";
         return true;
      } else {
         this.armed = armed;
         this.armedAt = System.currentTimeMillis();
         return false;
      }
   }

   private void action(String key) {
      StoreApi.Data2 twin = this.twin();
      this.statFile = null;
      if (key.startsWith("load:")) {
         this.loadMode = Integer.parseInt(key.substring(5));
      } else if (key.startsWith("price:")) {
         this.priceIndex = Integer.parseInt(key.substring(6));
      } else if (key.startsWith("vis:") && twin != null) {
         this.changeVisibility(twin, VISIBILITY[Integer.parseInt(key.substring(4))]);
      } else if (key.startsWith("copy:")) {
         for (StoreApi.Data code : this.codes) {
            if (key.equals("copy:" + code.getUUID()) && !code.getText().isBlank()) {
               Minecraft.getInstance().keyboardHandler.setClipboard(code.getText());
               this.host.report(t("gui.scripts.market.status.copied"), false);
            }
         }
      } else {
         switch (key) {
            case "close":
               this.host.close();
               break;
            case "save":
               this.save();
               break;
            case "apply":
               this.host.apply(this.fileName, this.loadMode);
               break;
            case "overwrite":
               Zenith.getInstance().getManager().method7779(this.fileName);
               this.host.report(t("gui.configs.report.saved") + " " + this.display, false);
               break;
            case "sync":
               ConfigSyncService.getInstance().method675();
               break;
            case "code":
               if (twin != null) {
                  this.issueCode(twin);
               }
               break;
            case "market.submit":
               this.submit(twin);
               break;
            case "market.withdraw":
               this.withdraw(twin);
               break;
            case "folder":
               com.mojang.blaze3d.Blaze3D.openPath((Manager.file).toPath());
               break;
            case "delete":
               if (this.confirm("delete")) {
                  Zenith.getInstance().getManager().method7786(this.fileName);
                  this.host.report(t("gui.configs.report.deleted") + " " + this.display, false);
                  this.host.close();
               }
         }

         this.host.changed();
      }
   }

   private void save() {
      Manager manager = Zenith.getInstance().getManager();
      String display = this.name.get().strip();
      if (this.name.changed() && !display.isEmpty() && !display.equals(this.display)) {
         if (manager.method7787(this.display, display)) {
            String str = "." + "Zenith".toLowerCase(Locale.ROOT);
            this.fileName = display + str;
            this.display = display;
            this.name.commit();
            this.host.report(t("gui.configs.report.renamed") + " " + display, false);
         } else {
            this.host.report(t("gui.configs.report.renameFailed"), true);
         }
      }

      if (this.description.changed()) {
         StoreApi.Data2 twin = this.twin();
         StoreApi storeApi = store();
         if (twin != null && storeApi != null) {
            String str = this.description.get().strip();
            storeApi.method1974(twin.getUUID(), null, str, null).whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  this.host.report(StoreApi.reason(var2x), true);
               } else {
                  ConfigSyncService.getInstance().method679(var1x);
                  this.host.report(t("gui.scripts.info.saved"), false);
               }

               this.host.changed();
            });
         }

         this.description.commit();
      }

      for (FormField field : this.fields) {
         field.setFocused(false);
      }

      this.host.changed();
   }

   private void changeVisibility(StoreApi.Data2 twin, String str) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1974(twin.getUUID(), null, null, str).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               this.host.report(StoreApi.reason(var2x), true);
            } else {
               ConfigSyncService.getInstance().method679(var1x);
               this.host.report(t("gui.scripts.market.status.visibility"), false);
            }

            this.host.changed();
         });
      }
   }

   private void loadCodes(StoreApi.Data2 twin) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1980(twin.getUUID()).whenComplete((var1x, var2x) -> {
            if (var2x == null) {
               this.codes = Collections.unmodifiableList(new ArrayList<>(var1x));
               this.host.changed();
            }
         });
      }
   }

   private void issueCode(StoreApi.Data2 twin) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1979(twin.getUUID(), 1).whenComplete((var2x, throwable) -> {
            if (throwable != null) {
               this.host.report(StoreApi.reason(throwable), true);
            } else {
               if (!var2x.isEmpty()) {
                  String text = var2x.get(0).getText();
                  Minecraft.getInstance().execute(() -> Minecraft.getInstance().keyboardHandler.setClipboard(text));
                  this.host.report(t("gui.scripts.market.status.codeCreated").replace("{n}", text), false);
               }

               this.loadCodes(twin);
            }
         });
      }
   }

   private void submit(StoreApi.Data2 twin) {
      StoreApi storeApi = store();
      if (storeApi != null && !this.busy) {
         if (twin == null) {
            this.host.report(t("gui.market.needCloud"), true);
            ConfigSyncService.getInstance().method675();
         } else {
            this.busy = true;
            int n = PRICES[this.priceIndex];
            storeApi.method1961(twin.getUUID(), n).whenComplete((var2x, var3x) -> {
               this.busy = false;
               if (var3x != null) {
                  this.host.report(StoreApi.reason(var3x), true);
               } else {
                  ConfigSyncService.getInstance().method679(var2x);
                  this.host.report(t(n == 0 ? "gui.market.report.published" : "gui.market.report.review"), false);
               }

               this.host.changed();
            });
         }
      }
   }

   private void withdraw(StoreApi.Data2 twin) {
      StoreApi storeApi = store();
      if (storeApi != null && twin != null && !this.busy) {
         this.busy = true;
         storeApi.method1962(twin.getUUID()).whenComplete((var1x, var2x) -> {
            this.busy = false;
            if (var2x != null) {
               this.host.report(StoreApi.reason(var2x), true);
            } else {
               ConfigSyncService.getInstance().method679(var1x);
               this.host.report(t("gui.market.report.withdrawn"), false);
            }

            this.host.changed();
         });
      }
   }

   public interface Host {
      void apply(String fileName, int loadMode);

      void report(String reason, boolean flag);

      void close();

      void changed();
   }
}

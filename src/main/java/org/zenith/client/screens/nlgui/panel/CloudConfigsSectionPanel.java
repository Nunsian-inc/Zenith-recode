package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.cloud.CloudCard;
import org.zenith.client.screens.nlgui.cloud.CloudLibrary;
import org.zenith.client.screens.nlgui.cloud.CloudPreviewTextureCache;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.CloudJsonIo;
import org.zenith.utility.api.ConfigFile;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.ConfigSyncService;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CloudConfigsSectionPanel extends SectionPanel {
   private static final int TAB_LOCAL = 0;
   private static final int TAB_CLOUD = 1;
   private static final int TAB_MARKET = 2;
   private static final float ROW = 36.0F;
   private static final float ROW_GAP = 4.0F;
   private static final float AVATAR = 22.0F;
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final String[] SORT_KEYS = new String[]{"gui.configs.sort.fresh", "gui.configs.sort.name", "gui.configs.sort.top", "gui.configs.sort.size"};
   private static final int CARD_COLUMNS = 2;
   private static final float CARD_GAP = 6.0F;
   private static final float BUTTON_HEIGHT = 16.0F;
   private static final float BUTTON_TALL_HEIGHT = 23.0F;
   private static final float FILE_ROW_HEIGHT = 26.0F;
   private static final float CURRENT_HEIGHT = 34.0F;
   private static final float SUGGEST_HEIGHT = 22.0F;
   private static final ZenithColor CHANGE_ON = new ZenithColor(-11870592);
   private static final ZenithColor CHANGE_OFF = new ZenithColor(-495247);
   private static final float FILE_ROW_GAP = 3.0F;
   private static final float TILE = 16.0F;
   private static final int TONE_PLAIN = 0;
   private static final int TONE_PRIMARY = 1;
   private static final int TONE_DANGER = 2;
   private static final String[] VISIBILITY = new String[]{"PRIVATE", "FRIENDS", "PUBLIC", "CODE"};
   private static final String[] VISIBILITY_NAMES = new String[]{
      "gui.configs.vis.private", "gui.configs.vis.friends", "gui.configs.vis.public", "gui.configs.vis.code"
   };
   private static final int SORT_FRESH = 0;
   private static final int SORT_NAME = 1;
   private static final int SORT_LIKES = 2;
   private static final int SORT_SIZE = 3;
   private static final int LOCAL_SORT_NAME = 0;
   private static final int LOCAL_SORT_DATE = 1;
   private static final int LOCAL_SORT_SIZE = 2;
   private static final SimpleDateFormat DATE = new SimpleDateFormat("dd.MM HH:mm", Locale.ROOT);
   private static final long REFRESH_MS = 15000L;
   private static final long STATUS_MS = 6000L;
   private static final long ARM_MS = 4000L;
   private static final int DESCRIPTION_MAX_LINES = 6;
   private final TextSetting newNameSetting = new TextSetting("gui.configs.name", "", "gui.configs.name.hint");
   private final TextSetting codeSetting = new TextSetting("gui.configs.code", "", "gui.configs.code.hint");
   private final InspectorRow newNameRow = new InspectorRow(this.newNameSetting, 0);
   private final InspectorRow codeRow = new InspectorRow(this.codeSetting, 0);
   private int seenSync = -1;
   private BoundsData headerButton;
   private boolean creatorOpen;
   private static final float TEMPLATE_ROW = 19.0F;
   private static final float FIELD_ROW = 26.0F;
   private static final String[] TEMPLATE_KEYS = new String[]{
      "gui.configs.template.full", "gui.configs.template.noBinds", "gui.configs.template.visual", "gui.configs.template.functions"
   };
   private static final boolean[][] TEMPLATE_PARTS = new boolean[][]{{true, true, true}, {true, true, false}, {true, false, false}, {false, true, true}};
   private static final MenuGlyph[] TEMPLATE_GLYPHS = new MenuGlyph[]{MenuGlyph.CONFIGS, MenuGlyph.KEY, MenuGlyph.COLOR, MenuGlyph.MODE};
   private final FormField nameField = new FormField(48, false);
   private final Animation creatorAnimation = new Animation(260L, 0.0F, Easing.INSTANCE15);
   private BoundsData savePill;
   private int seenSummaries = -1;
   private final Set<String> applying = ConcurrentHashMap.newKeySet();
   private final ConfigInfoPage page = new ConfigInfoPage(new ConfigInfoPage.Host() {
      @Override
      public void apply(String appliedName, int loadMode) {
         CloudConfigsSectionPanel.this.loadMode = loadMode;
         CloudConfigsSectionPanel.this.applyLocal(Zenith.getInstance().getManager(), appliedName);
      }

      @Override
      public void report(String str, boolean messageError) {
         CloudConfigsSectionPanel.this.status = messageError ? "!" + str : str;
         CloudConfigsSectionPanel.this.statusAt = System.currentTimeMillis();
         CloudConfigsSectionPanel.this.page.message(str, messageError);
         CloudConfigsSectionPanel.this.bump();
      }

      @Override
      public void close() {
         CloudConfigsSectionPanel.this.selectedLocal = null;
         CloudConfigsSectionPanel.this.bump();
      }

      @Override
      public void changed() {
         CloudConfigsSectionPanel.this.bump();
      }
   });
   private final ChipStrip sortChips = new ChipStrip("gui.configs.sort.fresh", "gui.configs.sort.name", "gui.configs.sort.top", "gui.configs.sort.size");
   private final ChipStrip localSortChips = new ChipStrip("gui.configs.sort.name", "gui.configs.sort.date", "gui.configs.sort.size");
   private final ChipStrip loadModeChips = new ChipStrip("gui.configs.load.all", "gui.configs.load.noBinds", "gui.configs.load.themeOnly");
   private final ChipStrip uploadVisibilityChips = new ChipStrip(VISIBILITY_NAMES);
   private final ChipStrip entryVisibilityChips = new ChipStrip(VISIBILITY_NAMES);
   private int kind;
   private int cloudSort = 0;
   private int localSort = 0;
   private int loadMode;
   private int uploadVisibility;
   private final List<StoreApi.Data2> mine = new CopyOnWriteArrayList<>();
   private volatile List<StoreApi.Data2> library = List.of();
   private volatile List<StoreApi.Data2> market = List.of();
   private volatile List<StoreApi.Data> codes = List.of();
   private final Map<UUID, CloudCard> cardVisuals = new LinkedHashMap<UUID, CloudCard>(16, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<UUID, CloudCard> eldest) {
         return this.size() > 96;
      }
   };
   private final Map<String, Animation> hoverAnimations = new HashMap<>();
   private final Map<UUID, Animation> previewFade = new HashMap<>();
   private final Animation noticeAppear = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private String noticeAnimatedFor = "";
   private CloudLibrary taken;
   private String selectedLocal;
   private String appliedName = "";
   private volatile long sideFetch;
   private UUID selectedCloud;
   private UUID codesFor;
   private String armedKey = "";
   private long armedAt;
   private volatile long lastFetch;
   private volatile int fetchedTab = -1;
   private volatile int fetchedKind = -1;
   private volatile String status = "";
   private volatile long statusAt;
   private volatile int revision;
   private int seenRevision;
   private String seenQuery = "";
   private boolean pillHot;
   private static final int[] LETTER_COLORS = new int[]{-10443270, -757066, -13315175, -278748, -5796870, -495247, -13058568, -290244};
   private static final Map<String, String> DOWNLOADED_FILES = new HashMap<>();
   private static boolean downloadedFilesLoaded;
   private static final String DOWNLOADED_FILES_NAME = "cloud-config-files.json";
   private static final int MAX_DOWNLOADED_FILES = 1024;
   private static final Set<String> RESERVED = Set.of(
      "con",
      "prn",
      "aux",
      "nul",
      "com1",
      "com2",
      "com3",
      "com4",
      "com5",
      "com6",
      "com7",
      "com8",
      "com9",
      "lpt1",
      "lpt2",
      "lpt3",
      "lpt4",
      "lpt5",
      "lpt6",
      "lpt7",
      "lpt8",
      "lpt9"
   );

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private String kind() {
      return "config";
   }

   private CloudLibrary taken() {
      if (this.taken == null) {
         this.taken = new CloudLibrary();
      }

      return this.taken;
   }

   @Override
   public void selectTab(int activeTab) {
      if (activeTab != this.getActiveTab()) {
         this.status = "";
         this.disarm();
         this.selectedCloud = null;
      }

      super.selectTab(activeTab);
   }

   @Override
   public String headerTitle() {
      return t("gui.cloud.tab.configs");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CONFIGS;
   }

   @Override
   public String sectionDescription() {
      return "gui.configs.desc";
   }

   private boolean localSection() {
      return this.getActiveTab() == 0;
   }

   @Override
   public float mainWidth() {
      return 236.0F;
   }

   @Override
   public float asideWidth() {
      return 380.0F - this.mainWidth();
   }

   @Override
   public void tick() {
      ConfigSyncService configSyncService = ConfigSyncService.getInstance();
      configSyncService.method676();
      if (configSyncService.revision() != this.seenSync) {
         this.seenSync = configSyncService.revision();
         this.invalidate();
      }

      if (this.revision != this.seenRevision) {
         this.seenRevision = this.revision;
         this.invalidate();
      }

      int seenSummaries = Zenith.getInstance().getManager().method7770();
      if (seenSummaries != this.seenSummaries) {
         this.seenSummaries = seenSummaries;
         this.invalidate();
      }

      String searchQuery = this.searchQuery();
      if (!searchQuery.equals(this.seenQuery)) {
         this.seenQuery = searchQuery;
         this.invalidate();
      }

      if (!this.status.isEmpty() && System.currentTimeMillis() - this.statusAt > 6000L) {
         this.status = "";
         this.bump();
      }

      if (!this.armedKey.isEmpty() && System.currentTimeMillis() - this.armedAt > 4000L) {
         this.disarm();
         this.bump();
      }

      long now = System.currentTimeMillis();
      if (now - this.lastFetch > 15000L) {
         this.lastFetch = now;
         StoreApi storeApi = store();
         if (storeApi != null) {
            storeApi.method1969("LIBRARY", this.kind())
               .whenComplete((var1x, var2x) -> this.accept(var1xx -> this.library = var1xx, (List<StoreApi.Data2>)var1x, var2x));
         }
      }
   }

   private StoreApi.Data2 findEntry(UUID uuid) {
      for (List list : List.of(this.mine, this.library, this.market)) {
         for (Object obj : (Iterable<?>)(Object)(list)) {
            StoreApi.Data2 data2 = (StoreApi.Data2)obj;
            if (data2.getUUID().equals(uuid)) {
               return data2;
            }
         }
      }

      return this.taken().all().stream().filter(var1x -> var1x.getUUID().equals(uuid)).findFirst().orElse(null);
   }

   private void accept(Consumer<List<StoreApi.Data2>> consumer, List<StoreApi.Data2> data2s, Throwable throwable) {
      if (throwable == null) {
         consumer.accept(Collections.unmodifiableList(new ArrayList(data2s)));
      } else {
         this.status = shortError(throwable);
         this.statusAt = System.currentTimeMillis();
      }

      this.bump();
   }

   private void fetchCodes(UUID codesFor) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         this.codesFor = codesFor;
         this.codes = List.of();
         storeApi.method1980(codesFor).whenComplete((var1x, var2x) -> {
            if (var2x == null) {
               this.codes = Collections.unmodifiableList(new ArrayList<>(var1x));
            } else {
               this.status = shortError(var2x);
               this.statusAt = System.currentTimeMillis();
            }

            this.bump();
         });
      }
   }

   private void refetch() {
      this.lastFetch = 0L;
      this.fetchedTab = -1;
   }

   private void bump() {
      this.revision++;
   }

   private void report(String status) {
      this.status = status;
      this.statusAt = System.currentTimeMillis();
      this.bump();
   }

   @Override
   public void build() {
      this.buildLocal();
   }

   @Override
   public float mainHeaderHeight() {
      return 11.0F + GuiStyle.PADDING.intValue() * 2.0F;
   }

   private String headerLabel() {
      return this.creatorOpen ? t("gui.configs.creator.cancel") : "+  " + t("gui.configs.creator.new");
   }

   private String syncLine() {
      ConfigSyncService configSyncService = ConfigSyncService.getInstance();
      if (configSyncService.method670()) {
         return t("gui.configs.sync.busy");
      } else if (!configSyncService.isFlag()) {
         return configSyncService.getLongValue() == 0L ? t("gui.configs.sync.waiting") : t("gui.configs.sync.offline");
      } else {
         return t("gui.configs.sync.ok");
      }
   }

   private float headerButtonWidth(Font font) {
      return Math.max(font.width("+  " + t("gui.configs.creator.new")), font.width(t("gui.configs.creator.cancel"))) + 16.0F;
   }

   @Override
   public void renderMainHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float y = f2 + GuiStyle.PADDING.intValue();
      Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
      float headerButtonWidth = this.headerButtonWidth(font);
      this.headerButton = new BoundsData(f + f3 - 8.0F - headerButtonWidth, y, headerButtonWidth, 11.0F);
      boolean contains = this.headerButton.contains(lastClickX, lastClickY);
      float f4 = this.animation("hdr:button", 140).method2808(contains ? 1.0F : 0.0F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(5.5F);
      boolean begin = this.pressFx.begin(menuDrawContext, "hdr:button", this.headerButton);
      String headerLabel = this.headerLabel();
      ZenithColor zenithColor2 = !this.creatorOpen
         ? zenithColor.method7292(0.18F + 0.16F * f4)
         : zenithStyle.getDisableActiveBg().getZenithColor().method7294(0.55F + 0.45F * f4);
      ZenithColor zenithColor3 = !this.creatorOpen
         ? zenithColor.method7295(ZenithColor.INSTANCE, 0.25F + 0.4F * f4)
         : zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), 0.4F + 0.6F * f4);
      menuDrawContext.drawRoundedRect(this.headerButton.getFloatValue(), y, headerButtonWidth, 11.0F, headerRadius, zenithColor2.method7294(previewAlpha));
      this.pressFx.ink(menuDrawContext, "hdr:button", this.headerButton, 5.5F, ZenithColor.INSTANCE.method7292(0.22F), previewAlpha);
      menuDrawContext.drawText(
         font, headerLabel, this.headerButton.getFloatValue() + (headerButtonWidth - font.width(headerLabel)) / 2.0F, y + (11.0F - font.height()) / 2.0F, zenithColor3.method7294(previewAlpha)
      );
      this.pressFx.end(menuDrawContext, begin);
      if (contains) {
         HoverInfo.request(headerLabel, t("gui.configs.saveNew.desc"));
      }

      ConfigSyncService configSyncService = ConfigSyncService.getInstance();
      ZenithColor zenithColor4 = configSyncService.method670() ? zenithColor : (configSyncService.isFlag() ? OK : zenithStyle.getTextTertiary().getZenithColor());
      float f5 = configSyncService.method670() ? 0.55F + 0.45F * (float)Math.abs(Math.sin(System.currentTimeMillis() / 300.0)) : 1.0F;
      float f6 = y + 5.5F - 1.5F;
      BoundsData boundsData = new BoundsData(f + 8.0F, y, this.headerButton.getFloatValue() - f - 8.0F - 6.0F, 11.0F);
      this.pressFx.ink(menuDrawContext, "hdr:sync", boundsData, 5.5F, zenithColor.method7292(0.16F), previewAlpha);
      float f7 = Math.max(0.0F, this.pressFx.dip("hdr:sync"));
      menuDrawContext.drawRoundedRect(
         f + 8.0F - f7 * 1.5F,
         f6 - f7 * 1.5F,
         3.0F + f7 * 3.0F,
         3.0F + f7 * 3.0F,
         ProxyDotRadiusData.method8069(1.5F + f7 * 1.5F),
         zenithColor4.method7294(previewAlpha * f5)
      );
      String syncLine = this.syncLine();
      menuDrawContext.drawText(
         font,
         CloudCard.fit(font, syncLine, this.headerButton.getFloatValue() - f - 8.0F - 14.0F),
         f + 8.0F + 6.0F,
         y + (11.0F - font.height()) / 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
      );
      if (new BoundsData(f + 8.0F, y, this.headerButton.getFloatValue() - f - 8.0F, 11.0F).contains(lastClickX, lastClickY)) {
         HoverInfo.request(syncLine, configSyncService.getLastError().isEmpty() ? t("gui.configs.sync.desc") : configSyncService.getLastError());
      }
   }

   @Override
   public boolean pressFeedback() {
      return true;
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      BoundsData mainHeaderBounds = this.mainHeaderBounds();
      if (mainHeaderBounds != null && mainHeaderBounds.contains(lastClickX, lastClickY)) {
         if (enum2 == Enum3.LEFT && this.headerButton != null && this.headerButton.contains(lastClickX, lastClickY)) {
            this.pressFx.press("hdr:button", lastClickX, lastClickY);
            this.toggleCreator();
            Zenith.getInstance().getClientSounds().method1570(false);
         } else if (enum2 == Enum3.LEFT) {
            this.pressFx.press("hdr:sync", lastClickX, lastClickY);
            ConfigSyncService.getInstance().method675();
         }

         return true;
      } else if (this.selectedLocal != null && enum2 == Enum3.LEFT && this.page.mouseClicked(lastClickX, lastClickY)) {
         return true;
      } else {
         if (this.creatorOpen && enum2 == Enum3.LEFT) {
            BoundsData bounds = this.nameField.bounds();
            if (this.savePill != null && this.savePill.contains(lastClickX, lastClickY)) {
               this.pressFx.press("creator:save", lastClickX, lastClickY);
               this.saveTemplate(0);
               return true;
            }

            boolean focused = bounds != null && bounds.contains(lastClickX, lastClickY);
            this.nameField.setFocused(focused);
            if (focused) {
               return true;
            }
         }

         return super.onMouseClicked(lastClickX, lastClickY, enum2);
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.selectedLocal != null && this.page.isTyping() && this.page.keyPressed(keyCode, modifiers)) {
         return true;
      } else if (this.creatorOpen && this.nameField.isFocused()) {
         if (keyCode == 257 || keyCode == 335) {
            this.saveTemplate(0);
         } else if (keyCode == 256) {
            this.nameField.setFocused(false);
         } else {
            this.nameField.keyPressed(keyCode, modifiers);
         }

         return true;
      } else {
         return super.keyPressed(keyCode, keycode, modifiers);
      }
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      if (this.selectedLocal != null && this.page.isTyping() && this.page.charTyped(codepoint)) {
         return true;
      } else if (this.creatorOpen && this.nameField.isFocused()) {
         this.nameField.charTyped(codepoint);
         return true;
      } else {
         return super.charTyped(codepoint, n);
      }
   }

   private void toggleCreator() {
      this.creatorOpen = !this.creatorOpen;
      this.creatorAnimation.reset(0.0F);
      this.creatorAnimation.method2810(1.0F);
      this.nameField.setFocused(this.creatorOpen);
      this.invalidate();
   }

   private void renderCreatorField(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float f4, ZenithStyle currentStyle) {
      float f5 = this.creatorAnimation.method2808(1.0F);
      float previewAlpha = f4 * Math.min(1.0F, f5 * 1.6F);
      float f6 = (1.0F - f5) * -4.0F;
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F + f6, f3 - 16.0F, 22.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hover = this.animation("creator:field", 140).method2808(!contains && !this.nameField.isFocused() ? 0.0F : 1.0F);
      ScriptsSectionPanel.card(menuDrawContext, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hover * 0.6F, previewAlpha);
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      String text = t("gui.configs.creator.save");
      float width = font.width(text) + 14.0F;
      float height = 14.0F;
      this.savePill = new BoundsData(
         boundsData.getFloatValue() + boundsData.width() - width - 4.0F, boundsData.getFloatValue2() + (boundsData.height() - height) / 2.0F, width, height
      );
      boolean flag = this.savePill.contains(lastClickX, lastClickY);
      float f7 = this.animation("creator:save", 120).method2808(flag ? 1.0F : 0.0F);
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      boolean begin = this.pressFx.begin(menuDrawContext, "creator:save", this.savePill);
      menuDrawContext.drawRoundedRect(
         this.savePill.getFloatValue(),
         this.savePill.getFloatValue2(),
         width,
         height,
         ProxyDotRadiusData.method8069(height / 2.0F),
         zenithColor.method7292(0.22F + 0.4F * f7).method7294(previewAlpha)
      );
      this.pressFx.ink(menuDrawContext, "creator:save", this.savePill, height / 2.0F, ZenithColor.INSTANCE.method7292(0.25F), previewAlpha);
      menuDrawContext.drawText(
         font,
         text,
         this.savePill.getFloatValue() + 7.0F,
         this.savePill.getFloatValue2() + (height - font.height()) / 2.0F,
         zenithColor.method7295(ZenithColor.INSTANCE, 0.3F + 0.6F * f7).method7294(previewAlpha)
      );
      this.pressFx.end(menuDrawContext, begin);
      MenuGlyph.TEXT
         .draw(
            menuDrawContext,
            boundsData.getFloatValue() + 6.0F,
            boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F,
            currentStyle.getTextTertiary().getZenithColor().method7295(zenithColor, hover).method7294(previewAlpha)
         );
      float floatValue = boundsData.getFloatValue() + 16.0F;
      float shownWidth = this.savePill.getFloatValue() - 5.0F - floatValue;
      this.nameField
         .render(
            menuDrawContext,
            font,
            floatValue,
            boundsData.getFloatValue2() + (boundsData.height() - 16.0F) / 2.0F,
            shownWidth,
            previewAlpha,
            t("gui.configs.creator.hint"),
            ZenithColor.INSTANCE.method7293(0),
            ZenithColor.INSTANCE.method7293(0),
            zenithColor,
            currentStyle.getTextEnable().getZenithColor(),
            currentStyle.getTextTertiary().getZenithColor()
         );
      if (flag) {
         HoverInfo.request(text, t("gui.configs.template.full.desc"));
      }
   }

   private void renderTemplate(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float f4, ZenithStyle currentStyle, int n) {
      float f5 = this.creatorAnimation.method2808(1.0F);
      float f6 = Math.max(0.0F, Math.min(1.0F, f5 * 1.8F - (n + 1) * 0.15F));
      float previewAlpha = f4 * f6;
      float f7 = (1.0F - f6) * -4.0F;
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F + f7, f3 - 16.0F, 16.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.animation("template:" + n, 140).method2808(contains ? 1.0F : 0.0F);
      ScriptsSectionPanel.card(menuDrawContext, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      TEMPLATE_GLYPHS[n]
         .draw(
            menuDrawContext,
            boundsData.getFloatValue() + 7.0F,
            boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F,
            (contains ? zenithColor : currentStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha)
         );
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
      String text = t(TEMPLATE_KEYS[n]);
      menuDrawContext.drawText(
         font,
         text,
         boundsData.getFloatValue() + 18.0F,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
      );
      String str = contains ? t("gui.configs.template.go") : t(TEMPLATE_KEYS[n] + ".short");
      float f8 = boundsData.getFloatValue() + boundsData.width() - 7.0F - font2.width(str);
      menuDrawContext.drawText(
         font2,
         CloudCard.fit(font2, str, boundsData.width() - 30.0F - font.width(text)),
         Math.max(f8, boundsData.getFloatValue() + 24.0F + font.width(text)),
         boundsData.getFloatValue2() + (boundsData.height() - font2.height()) / 2.0F,
         (contains ? zenithColor : currentStyle.getTextTertiary().getZenithColor()).method7294(previewAlpha)
      );
      if (contains) {
         HoverInfo.request(text, t(TEMPLATE_KEYS[n] + ".desc"));
      }
   }

   private void saveTemplate(int n) {
      String argument = this.nameField.get() == null ? "" : this.nameField.get().strip();
      if (argument.isEmpty()) {
         argument = t("gui.configs.creator.defaultName") + " " + new SimpleDateFormat("dd.MM HH-mm", Locale.ROOT).format(new Date());
      }

      boolean[] flags = TEMPLATE_PARTS[Math.max(0, Math.min(TEMPLATE_PARTS.length - 1, n))];
      boolean flag = Zenith.getInstance().getManager().method7780(argument, flags[0], flags[1], flags[2]);
      if (!flag) {
         this.report(t("gui.configs.report.writeFailed"));
      } else {
         this.selectedLocal = argument + "." + "Zenith".toLowerCase(Locale.ROOT);
         this.selectedCloud = null;
         this.nameField.clear();
         this.nameField.setFocused(false);
         this.creatorOpen = false;
         this.report(t("gui.configs.report.created") + " " + argument);
         this.invalidate();
      }
   }

   private void hero(String text, String description, String[][] stringses) {
      this.custom(
         50.0F,
         (var3x, n, n2, f, f2, f3, previewAlpha, currentStyle) -> {
            float floatValue = f + 8.0F;
            float width = f3 - 16.0F;
            float y = f2 + 2.0F;
            float height = 43.0F;
            ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
            ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.9F);
            var3x.drawRoundedRect(floatValue, y, width, height, headerRadius, ScriptsSectionPanel.cardFill(currentStyle, 0.2F).method7294(previewAlpha));
            ScriptsSectionPanel.flowGlow(var3x, floatValue, y, width, height, headerRadius, zenithColor, 0.45F, previewAlpha);
            var3x.drawRoundedBorder(floatValue, y, width, height, 0.2F, headerRadius, zenithColor.method7292(0.28F).method7294(previewAlpha));
            Font font = Fonts.NEW_SEMIBOLD.getFont(6.4F);
            Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
            Font font3 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
            Font font4 = Fonts.NEW_MEDIUM.getFont(3.8F);
            var3x.drawText(font, text, floatValue + 9.0F, y + 8.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
            float f4 = 0.0F;

            for (String[] strings : stringses) {
               f4 += Math.max(font3.width(strings[0]), font4.width(strings[1])) + 12.0F;
            }

            float centeredTextY = y + 11.0F + font.height();

            for (String str : wrapText(description, font2, width - 18.0F - f4, 2)) {
               var3x.drawText(font2, str, floatValue + 9.0F, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               centeredTextY += font2.height() + 1.5F;
            }

            float f5 = floatValue + width - 9.0F;

            for (int i = stringses.length - 1; i >= 0; i--) {
               float f6 = Math.max(font3.width(stringses[i][0]), font4.width(stringses[i][1]));
               f5 -= f6;
               var3x.drawText(
                  font3,
                  stringses[i][0],
                  f5 + (f6 - font3.width(stringses[i][0])) / 2.0F,
                  y + 10.0F,
                  currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
               );
               var3x.drawText(
                  font4,
                  stringses[i][1],
                  f5 + (f6 - font4.width(stringses[i][1])) / 2.0F,
                  y + 13.0F + font3.height(),
                  currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               f5 -= 12.0F;
            }
         }
      );
   }

   private float drawPill(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, BoundsData boundsData, String pillKey, String pillText, boolean have, float previewAlpha, ZenithStyle zenithStyle) {
      BoundsData pillRect = ScriptMarketTab.pillRect(boundsData, boundsData.getFloatValue() + boundsData.width() - 7.0F, pillText);
      boolean contains = pillRect.contains(lastClickX, lastClickY);
      float hot = this.animation("pill:" + pillKey, 120).method2808(contains ? 1.0F : 0.0F);
      ScriptMarketTab.drawPill(menuDrawContext, zenithStyle, pillRect, pillText, have, hot, false, previewAlpha);
      this.pillHot = this.pillHot || contains;
      return pillRect.getFloatValue();
   }

   private void avatar(MenuDrawContext menuDrawContext, String display, StoreApi.Data2 cloudTwin, float floatValue, float cardAvatarY, float size, float previewAlpha) {
      ScriptMarketTab.cloudAvatar(menuDrawContext, cloudTwin, display, letterColor(display), floatValue, cardAvatarY, size, previewAlpha);
   }

   private void card(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, BoundsData boundsData, String cardKey, String name, boolean contains, boolean flag, float previewAlpha) {
      float hot = this.animation("row:" + cardKey, 140).method2808(contains ? 1.0F : 0.0F);
      float f = this.animation("sel:" + cardKey, 160).method2808(flag ? 1.0F : 0.0F);
      ScriptMarketTab.drawCard(menuDrawContext, currentStyle, boundsData, hot, f, 0.0F, letterColor(name), previewAlpha);
   }

   private void statusRow() {
      this.asideCustom(14.0F, (menuDrawContext, n, n2, f, f2, f3, f4, zenithStyle) -> {
         long l = System.currentTimeMillis() - this.statusAt;
         if (!this.status.isEmpty() && l <= 8000L) {
            float f5 = l > 7000L ? 1.0F - (float)(l - 7000L) / 1000.0F : Math.min(1.0F, (float)l / 180.0F);
            Font font = Fonts.NEW_REGULAR.getFont(4.3F);
            String status = this.status;
            boolean flag = status.equals(t("gui.configs.report.writeFailed")) || status.startsWith("!");
            if (status.startsWith("!")) {
               status = status.substring(1);
            }

            menuDrawContext.drawText(font, CloudCard.fit(font, status, f3 - 16.0F), f + 8.0F, f2 + 4.0F, (flag ? ERROR : OK).method7294(f4 * f5));
         }
      });
   }

   private void buildLocal() {
      Manager manager = Zenith.getInstance().getManager();
      List<CloudConfigsSectionPanel.LocalFile> localFiles = this.localFiles(manager);
      if (this.selectedLocal != null && localFiles.stream().noneMatch(var1x -> var1x.name().equals(this.selectedLocal))) {
         this.selectedLocal = null;
      }

      int n = 0;
      int n2 = 0;

      for (CloudConfigsSectionPanel.LocalFile localFile : localFiles) {
         StoreApi.Data2 cloudTwin = this.cloudTwin(localFile.name());
         if (cloudTwin != null) {
            n++;
            n2 += cloudTwin.getIntValue();
         }
      }

      int[] ints = liveCounts();
      String description = t("gui.configs.current.line").replace("{m}", String.valueOf(ints[0])).replace("{b}", String.valueOf(ints[1]))
         + (this.appliedName.isEmpty() ? "" : " · " + t("gui.configs.current.from").replace("{n}", display(this.appliedName)));
      this.hero(
         t("gui.configs.hero.local"),
         description,
         new String[][]{
            {String.valueOf(localFiles.size()), t("gui.configs.hero.files")},
            {n + "/" + localFiles.size(), t("gui.configs.hero.synced")},
            {String.valueOf(n2), t("gui.configs.hero.likes")}
         }
      );
      if (this.creatorOpen) {
         this.custom(26.0F, this::renderCreatorField);

         for (int i = 0; i < TEMPLATE_KEYS.length; i++) {
            final int var13_i = i;
            this.custom(
               19.0F,
               (var2x, var3x, var4x, f, var6x, var7x, var8x, var9x) -> this.renderTemplate(var2x, var3x, var4x, f, var6x, var7x, var8x, var9x, var13_i),
               () -> this.saveTemplate(var13_i),
               null
            );
         }

         this.gap(6.0F);
      }

      for (CloudConfigsSectionPanel.LocalFile localFile : localFiles) {
         CloudConfigsSectionPanel.FileRow fileRow = new CloudConfigsSectionPanel.FileRow(manager, localFile);
         this.custom(40.0F, fileRow::render, fileRow::apply, () -> localFile.name().equals(this.selectedLocal), fileRow::click);
      }

      if (localFiles.isEmpty()) {
         this.emptyState(
            this.searchQuery().isEmpty() ? "gui.configs.empty.local" : "gui.configs.empty.query",
            this.searchQuery().isEmpty() ? "gui.configs.empty.local.desc" : "gui.configs.empty.query.local.desc"
         );
      }

      List<StoreApi.Data2> arrange = this.arrange(this.sharedEntries());
      if (this.selectedCloud != null && arrange.stream().noneMatch(var1x -> var1x.getUUID().equals(this.selectedCloud))) {
         this.selectedCloud = null;
      }

      if (!arrange.isEmpty()) {
         this.gap(GuiStyle.PADDING.intValue());
         this.caption("gui.configs.caption.shared");

         for (StoreApi.Data2 entry : arrange) {
            CloudConfigsSectionPanel.EntryRow entryRow = new CloudConfigsSectionPanel.EntryRow(entry);
            this.custom(40.0F, entryRow::render, entryRow::primary, () -> entry.getUUID().equals(this.selectedCloud), entryRow::click);
         }
      }

      StoreApi.Data2 cloudTwin = this.find(arrange);
      if (cloudTwin != null) {
         this.selectedLocal = null;
         this.buildEntryAside(cloudTwin, false);
         if (this.taken().contains(cloudTwin.getUUID())) {
            this.asideDanger("forget", "gui.configs.forget", "gui.configs.forget.desc", () -> {
               this.taken().forget(cloudTwin.getUUID());
               this.selectedCloud = null;
               this.report(t("gui.configs.report.forgotten") + " " + cloudTwin.name());
            });
         }

         this.statusRow();
         this.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
            this.selectedCloud = null;
            this.invalidate();
         }, "gui.scripts.market.back.desc");
      } else if (this.selectedLocal != null) {
         this.page.open(this.selectedLocal, display(this.selectedLocal));
         this.asideFull(
            (var1x, var2x, var3x, var4x, y, var6x, var7x, var8x) -> this.page
               .render(
                  var1x,
                  var2x,
                  var3x,
                  var4x,
                  y,
                  var6x,
                  NLMenuScreen.contentBottom - y - GuiStyle.PADDING.intValue() * 4.0F - HoverInfo.boxHeight(),
                  var7x,
                  var8x
               )
         );
      } else {
         this.buildLocalAside(manager, localFiles.size());
      }
   }

   @Override
   public boolean mouseScrolled(double lastClickX, double lastClickY, double d, double verticalAmount) {
      return this.selectedLocal != null && this.page.mouseScrolled(lastClickX, lastClickY, verticalAmount) ? true : super.mouseScrolled(lastClickX, lastClickY, d, verticalAmount);
   }

   private List<StoreApi.Data2> sharedEntries() {
      LinkedHashMap map = new LinkedHashMap();

      for (StoreApi.Data2 data2 : this.taken().all()) {
         if (this.kind().equalsIgnoreCase(data2.kind()) && !data2.mine()) {
            map.put(data2.getUUID(), data2);
         }
      }

      for (StoreApi.Data2 data2 : this.library) {
         if (!data2.mine()) {
            map.put(data2.getUUID(), data2);
         }
      }

      return new ArrayList<>(map.values());
   }

   private static int[] liveCounts() {
      int n = 0;
      int n2 = 0;

      for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
         if (moduleValue.isEnabled()) {
            n++;
         }

         if (moduleValue.getKeyCode() > 0) {
            n2++;
         }
      }

      return new int[]{n, n2};
   }

   public static ZenithColor letterColor(String display) {
      int x = display == null ? 0 : display.toLowerCase(Locale.ROOT).hashCode();
      return new ZenithColor(LETTER_COLORS[Math.floorMod(x, LETTER_COLORS.length)]);
   }

   private static void letterTile(MenuDrawContext menuDrawContext, String display, float floatValue, float y, float width, float previewAlpha, float f) {
      ZenithColor zenithColor = letterColor(display);
      menuDrawContext.drawRoundedRect(floatValue, y, width, width, ProxyDotRadiusData.method8069(width * 0.3F), zenithColor.method7294(previewAlpha * (0.22F + 0.25F * f)));
      Font font = Fonts.NEW_MEDIUM.getFont(width * 0.48F);
      String upperCase = display != null && !display.isEmpty() ? display.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
      menuDrawContext.drawText(
         font,
         upperCase,
         floatValue + (width - font.width(upperCase)) / 2.0F,
         y + (width - font.height()) / 2.0F,
         zenithColor.method7295(new ZenithColor(255, 255, 255), 0.35F).method7294(previewAlpha)
      );
   }

   private StoreApi.Data2 cloudTwin(String name) {
      return ConfigSyncService.getInstance().method672(display(name));
   }

   private void buildLocalAside(Manager manager, int size) {
      if (this.selectedLocal == null) {
         int[] ints = liveCounts();
         ConfigSyncService configSyncService = ConfigSyncService.getInstance();
         this.asideCaption("gui.configs.caption.overview");
         String str = String.valueOf(size);
         this.asideInfo("gui.configs.info.files", () -> str, "gui.configs.info.files.desc");
         this.asideInfo("gui.configs.info.modules", () -> String.valueOf(ints[0]), "gui.configs.info.modules.desc");
         this.asideInfo("gui.configs.info.binds", () -> String.valueOf(ints[1]), "gui.configs.info.binds.desc");
         this.asideInfo("gui.configs.info.applied", () -> this.appliedName.isEmpty() ? "—" : display(this.appliedName), "gui.configs.info.applied.desc");
         this.asideInfo("gui.configs.info.sync", this::syncLine, "gui.configs.sync.desc");
         this.asideCaption("gui.configs.caption.actions");
         this.asideAction("gui.configs.saveNew", MenuGlyph.COPY, () -> this.creatorOpen ? "" : "+", this::toggleCreator, "gui.configs.saveNew.desc");
         this.asideAction(
            "gui.configs.syncNow", MenuGlyph.CLOUD, () -> configSyncService.method670() ? t("gui.scripts.market.busy") : "", configSyncService::method675, "gui.configs.syncNow.desc"
         );
         this.asideAction(
            "gui.configs.openFolder", MenuGlyph.STORAGE, () -> "", () -> org.zenith.utility.misc.OsOpen.file(Manager.file), "gui.configs.openFolder.desc"
         );
         this.asideAction(
            "gui.configs.openMarket", MenuGlyph.CLOUD, () -> "", () -> Zenith.getInstance().getNLMenuScreen().openMarket(2), "gui.configs.openMarket.desc"
         );
         this.asideCaption("gui.configs.caption.redeem");
         this.asideSetting(this.codeRow);
         this.asideAction("gui.configs.redeem", MenuGlyph.KEY, () -> "", this::redeem, "gui.configs.redeem.desc");
         this.statusRow();
      } else {
         String selectedLocal = this.selectedLocal;
         File file = new File(Manager.file, selectedLocal);
         StoreApi.Data2 cloudTwin = this.cloudTwin(selectedLocal);
         this.asideHeader(display(selectedLocal), cloudTwin, () -> CloudCard.size(file.length()) + "  ·  " + DATE.format(new Date(file.lastModified())));
         this.asideCaption("gui.configs.caption.changes");
         Manager.Data data = manager.method7772(selectedLocal);
         if (data == null) {
            this.asideHint("…");
         } else {
            this.changes(data);
         }

         this.asideCaption("gui.configs.caption.apply");
         this.asideChips(this.loadModeChips, () -> this.loadMode, var1x -> this.loadMode = var1x, "gui.configs.load.desc");
         this.asideAction("apply", MenuGlyph.CHECK, "gui.configs.apply", null, 1, "gui.configs.apply.desc", () -> this.applyLocal(manager, selectedLocal));
         this.asideAction("overwrite", MenuGlyph.COPY, "gui.configs.overwrite", null, 0, "gui.configs.overwrite.desc", () -> {
            manager.method7779(selectedLocal);
            this.report(t("gui.configs.report.saved") + " " + display(selectedLocal));
         });
         this.asideCaption("gui.configs.caption.cloud");
         this.asideInfo(
            "gui.configs.info.sync",
            () -> t("gui.configs.sync.state." + ConfigSyncService.getInstance().method671(display(selectedLocal)).name().toLowerCase(Locale.ROOT)),
            "gui.configs.sync.desc"
         );
         if (cloudTwin == null) {
            this.asideAction("gui.configs.syncNow", MenuGlyph.CLOUD, () -> "", ConfigSyncService.getInstance()::method675, "gui.configs.syncNow.desc");
         } else {
            this.asideInfo("gui.configs.inCloud", () -> "v" + cloudTwin.getLongValue2() + " · ♥ " + cloudTwin.getIntValue(), "gui.configs.inCloud.desc");
            this.publication(cloudTwin);
         }

         this.asideDanger("delete-local", "gui.configs.deleteEverywhere", "gui.configs.deleteEverywhere.desc", () -> {
            manager.method7786(selectedLocal);
            this.selectedLocal = null;
            if (selectedLocal.equals(this.appliedName)) {
               this.appliedName = "";
            }

            this.report(t("gui.configs.report.deleted") + " " + display(selectedLocal));
         });
         this.statusRow();
         this.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
            this.selectedLocal = null;
            this.invalidate();
         }, "gui.scripts.market.back.desc");
      }
   }

   private void changes(Manager.Data data) {
      ArrayList<String> strings = new ArrayList();
      ArrayList<String> strings2 = new ArrayList();
      if (this.loadMode != 2) {
         for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
            if (moduleValue != InterfaceModule.INSTANCE && moduleValue != MenuModule.INSTANCE) {
               boolean enabled = moduleValue.isEnabled();
               boolean contains = data.getItems().contains(moduleValue.getId());
               if (contains && !enabled) {
                  strings.add(moduleValue.getName());
               } else if (!contains && enabled) {
                  strings2.add(moduleValue.getName());
               }
            }
         }
      }

      strings.sort(String.CASE_INSENSITIVE_ORDER);
      strings2.sort(String.CASE_INSENSITIVE_ORDER);
      if (this.loadMode == 2) {
         this.asideHint(t(data.isFlag() ? "gui.configs.changes.theme" : "gui.configs.changes.noTheme"));
      } else if (strings.isEmpty() && strings2.isEmpty()) {
         this.asideHint(t("gui.configs.changes.none"));
      } else {
         Font font = Fonts.NEW_REGULAR.getFont(4.6F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
         ArrayList<String[]> stringses = new ArrayList();

         for (String sub : strings.subList(0, Math.min(5, strings.size()))) {
            stringses.add(new String[]{"+", sub});
         }

         for (String sub : strings2.subList(0, Math.min(5, strings2.size()))) {
            stringses.add(new String[]{"−", sub});
         }

         int n = strings.size() + strings2.size() - stringses.size();
         float f = font.height() + 3.0F;
         float f2 = font2.height() + 6.0F + stringses.size() * f + (n > 0 ? f : 0.0F) + 4.0F;
         this.asideCustom(
            f2 + GuiStyle.PADDING.intValue(),
            (var7x, var8x, var9x, var10x, var11x, var12x, previewAlpha, zenithStyle) -> {
               float var15x = var10x + 8.0F;
               String var16x = "+" + strings.size();
               String var17x = "−" + strings2.size();
               var7x.drawText(font2, var16x, var15x, var11x, CHANGE_ON.method7294(previewAlpha));
               var7x.drawText(
                  font,
                  t("gui.configs.changes.on"),
                  var15x + font2.width(var16x) + 3.0F,
                  var11x + 0.5F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               float floatValue = var15x + font2.width(var16x) + 3.0F + font.width(t("gui.configs.changes.on")) + 10.0F;
               var7x.drawText(font2, var17x, floatValue, var11x, CHANGE_OFF.method7294(previewAlpha));
               var7x.drawText(
                  font,
                  t("gui.configs.changes.off"),
                  floatValue + font2.width(var17x) + 3.0F,
                  var11x + 0.5F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               float centeredTextY = var11x + font2.height() + 6.0F;

               for (String[] strings3 : stringses) {
                  boolean flag = strings3[0].equals("+");
                  var7x.drawRoundedRect(
                     var15x, centeredTextY + 1.0F, 3.0F, font.height() - 1.0F, ProxyDotRadiusData.method8069(1.5F), (flag ? CHANGE_ON : CHANGE_OFF).method7294(previewAlpha)
                  );
                  var7x.drawText(
                     font,
                     CloudCard.fit(font, strings3[1], var12x - 16.0F - 8.0F),
                     var15x + 7.0F,
                     centeredTextY,
                     zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
                  );
                  centeredTextY += f;
               }

               if (n > 0) {
                  var7x.drawText(
                     font,
                     t("gui.configs.changes.more").replace("{n}", String.valueOf(n)),
                     var15x + 7.0F,
                     centeredTextY,
                     zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
                  );
               }
            }
         );
      }
   }

   private static String ago(long modified) {
      long l = Math.max(0L, (System.currentTimeMillis() - modified) / 1000L);
      if (l < 60L) {
         return t("gui.party.ago.now");
      } else if (l < 3600L) {
         return t("gui.party.ago.min").replace("{n}", String.valueOf(l / 60L));
      } else {
         return l < 86400L
            ? t("gui.party.ago.hour").replace("{n}", String.valueOf(l / 3600L))
            : t("gui.party.ago.day").replace("{n}", String.valueOf(l / 86400L));
      }
   }

   private void applyLocal(Manager manager, String appliedName) {
      if (this.applying.add(appliedName)) {
         manager.method7768(appliedName, this.loadMode()).whenComplete((var2x, throwable) -> {
            this.applying.remove(appliedName);
            boolean flag = throwable == null && Boolean.TRUE.equals(var2x);
            if (flag) {
               this.appliedName = appliedName;
            }

            String str = t(flag ? "gui.configs.report.applied" : "gui.configs.report.unreadable") + " " + display(appliedName);
            this.report(flag ? str : "!" + str);
            this.page.message(str, !flag);
         });
      }
   }

   private List<CloudConfigsSectionPanel.LocalFile> localFiles(Manager manager) {
      String suffix = "." + "Zenith".toLowerCase(Locale.ROOT);
      String searchQuery = this.searchQuery();
      ArrayList<CloudConfigsSectionPanel.LocalFile> localFiles = new ArrayList<>();

      for (String child : manager.method7785()) {
         if (child.toLowerCase(Locale.ROOT).endsWith(suffix)) {
            File file = new File(Manager.file, child);
            if (file.isFile()) {
               String display = child.substring(0, child.length() - suffix.length());
               if (!display.equalsIgnoreCase("current_config") && (searchQuery.isEmpty() || display.toLowerCase(Locale.ROOT).contains(searchQuery))) {
                  localFiles.add(new CloudConfigsSectionPanel.LocalFile(child, display, "", file.length(), file.lastModified()));
               }
            }
         }
      }

      switch (this.localSort) {
         case 1:
            localFiles.sort(Comparator.comparingLong(CloudConfigsSectionPanel.LocalFile::modified).reversed());
            break;
         case 2:
            localFiles.sort(Comparator.comparingLong(CloudConfigsSectionPanel.LocalFile::size).reversed());
            break;
         default:
            localFiles.sort(Comparator.comparing(localFile -> localFile.display().toLowerCase(Locale.ROOT)));
      }

      return localFiles;
   }

   private List<StoreApi.Data2> arrange(List<StoreApi.Data2> sharedEntries) {
      String searchQuery = this.searchQuery();
      ArrayList<StoreApi.Data2> data2s = new ArrayList<>();

      for (StoreApi.Data2 sharedEntry : sharedEntries) {
         if (searchQuery.isEmpty() || matches(sharedEntry, searchQuery)) {
            data2s.add(sharedEntry);
         }
      }

      switch (this.cloudSort) {
         case 1:
            data2s.sort(Comparator.comparing(data2 -> blank(data2.name()).toLowerCase(Locale.ROOT)));
            break;
         case 2:
            data2s.sort(Comparator.comparingInt(StoreApi.Data2::getIntValue).reversed());
            break;
         case 3:
            data2s.sort(Comparator.comparingLong(StoreApi.Data2::getLongValue).reversed());
            break;
         default:
            data2s.sort(Comparator.comparingLong(StoreApi.Data2::getLongValue4).reversed());
      }

      return data2s;
   }

   private static boolean matches(StoreApi.Data2 sharedEntry, String searchQuery) {
      return blank(sharedEntry.name()).toLowerCase(Locale.ROOT).contains(searchQuery)
         || blank(sharedEntry.getText8()).toLowerCase(Locale.ROOT).contains(searchQuery)
         || blank(sharedEntry.server()).toLowerCase(Locale.ROOT).contains(searchQuery);
   }

   private StoreApi.Data2 find(List<StoreApi.Data2> arrange) {
      return arrange.stream().filter(var1x -> var1x.getUUID().equals(this.selectedCloud)).findFirst().orElse(null);
   }

   private static String cardHover(StoreApi.Data2 entry) {
      StringBuilder builder = new StringBuilder();
      if (entry.description() != null && !entry.description().isBlank()) {
         builder.append(entry.description()).append(' ');
      }

      builder.append(CloudCard.size(entry.getLongValue()));
      if (entry.server() != null && !entry.server().isBlank()) {
         builder.append(" · ").append(entry.server());
      }

      builder.append(" · ").append(DATE.format(new Date(entry.getLongValue4())));
      builder.append(". ").append(t("gui.configs.card.hover"));
      return builder.toString();
   }

   private void buildEntryAside(StoreApi.Data2 cloudTwin, boolean flag) {
      if (cloudTwin == null) {
         this.asideCaption("gui.configs.caption.card");
         this.asideHint(t("gui.configs.aside.pickCard"));
      } else {
         UUID uuid = cloudTwin.getUUID();
         this.asideHeader(
            cloudTwin.name(), cloudTwin, () -> "v" + cloudTwin.getLongValue2() + "  ·  " + blankToDash(cloudTwin.getText8()) + "  ·  " + CloudCard.size(cloudTwin.getLongValue())
         );
         this.preview(cloudTwin);
         this.description(cloudTwin);
         this.asideCaption("gui.scripts.caption.actions");
         this.asideAction(
            "download", MenuGlyph.STORAGE, "gui.configs.download", "gui.configs.download.sub", 1, "gui.configs.download.desc", () -> this.download(cloudTwin)
         );
         this.asideAction(
            "like",
            MenuGlyph.HEART,
            t(cloudTwin.isFlag3() ? "gui.configs.unlike" : "gui.configs.like") + " · " + cloudTwin.getIntValue(),
            null,
            0,
            "gui.configs.like.desc",
            () -> this.toggleLike(uuid)
         );
         this.asideCaption("gui.scripts.market.caption.about");
         this.asideInfo("gui.scripts.market.info.updated", () -> ago(cloudTwin.getLongValue4()), "gui.scripts.market.info.updated.desc");
         if (!flag) {
            this.asideInfo("gui.configs.author", () -> blankToDash(cloudTwin.getText8()), "gui.configs.author.desc");
         }

         this.asideInfo("gui.configs.server", () -> blankToDash(cloudTwin.server()), "gui.configs.server.desc");
      }
   }

   private void publication(StoreApi.Data2 cloudTwin) {
      UUID uuid = cloudTwin.getUUID();
      this.asideCaption("gui.configs.caption.visibility");
      this.asideChips(
         this.entryVisibilityChips, () -> visibilityIndex(cloudTwin.getText7()), var2x -> this.changeVisibility(cloudTwin, var2x), "gui.configs.visibility.desc"
      );
      this.asideAction("push", MenuGlyph.CLOUD, "gui.configs.push", "gui.configs.push.sub", 0, "gui.configs.push.desc", () -> this.pushContent(cloudTwin));
      this.asideCaption("gui.configs.caption.codes");
      this.asideAction("issue-code", MenuGlyph.KEY, "gui.configs.issueCode", "gui.configs.issueCode.sub", 0, "gui.configs.issueCode.desc", () -> {
         StoreApi var2x = store();
         if (var2x != null) {
            var2x.method1979(uuid, 1).whenComplete((var2xx, throwable) -> {
               if (throwable != null) {
                  this.status = shortError(throwable);
                  this.statusAt = System.currentTimeMillis();
               }

               this.fetchCodes(uuid);
            });
         }
      });
      if (uuid.equals(this.codesFor)) {
         for (StoreApi.Data code : this.codes) {
            boolean equalsIgnoreCase = "ACTIVE".equalsIgnoreCase(code.getText2());
            this.row(code.getText())
               .icon(MenuGlyph.KEY)
               .value(() -> lower(code.getText2()))
               .description(equalsIgnoreCase ? "gui.configs.code.active.desc" : "gui.configs.code.used.desc")
               .onRightClick(() -> {
                  StoreApi storeApi = store();
                  if (storeApi != null) {
                     storeApi.method1981(uuid, code.getUUID()).whenComplete((var2xx, var3x) -> {
                        if (var3x != null) {
                           this.status = shortError(var3x);
                           this.statusAt = System.currentTimeMillis();
                        }

                        this.fetchCodes(uuid);
                     });
                  }
               })
               .addAside();
         }

         if (this.codes.isEmpty()) {
            this.asideHint(t("gui.configs.noCodes"));
         }
      }
   }

   private void asideHeader(String display, StoreApi.Data2 cloudTwin, Supplier<String> supplier) {
      this.asideCustom(
         46.0F,
         (menuDrawContext, n, n2, f, f2, f3, previewAlpha, zenithStyle) -> {
            float size = 34.0F;
            this.avatar(menuDrawContext, display, cloudTwin, f + 8.0F, f2 + 5.0F, size, previewAlpha);
            Font font = Fonts.NEW_SEMIBOLD.getFont(5.8F);
            Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
            float floatValue = f + 8.0F + size + 7.0F;
            menuDrawContext.drawText(
               font, CloudCard.fit(font, display, f + f3 - 8.0F - floatValue), floatValue, f2 + 9.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
            );
            menuDrawContext.drawText(
               font2,
               CloudCard.fit(font2, (String)supplier.get(), f + f3 - 8.0F - floatValue),
               floatValue,
               f2 + 11.0F + font.height(),
               zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         }
      );
   }

   private void preview(StoreApi.Data2 cloudTwin) {
      if (cloudTwin.hasPreview()) {
         float f = this.asideWidth() - 16.0F;
         float height = f * 9.0F / 16.0F;
         UUID uuid = cloudTwin.getUUID();
         this.asideCustom(
            height + GuiStyle.PADDING.intValue(),
            (var4x, n, n2, f2, y, f3, f4, zenithStyle) -> {
               float floatValue = f2 + 8.0F;
               float width = f3 - 16.0F;
               Identifier texture = CloudPreviewTextureCache.get(uuid, cloudTwin.getText6());
               ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
               if (texture == null) {
                  float f5 = 0.7F + 0.3F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0));
                  var4x.drawRoundedRect(floatValue, y, width, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(f4 * 2.0F));
                  Font font = Fonts.NEW_REGULAR.getFont(4.6F);
                  String text = t("gui.configs.previewLoading");
                  var4x.drawText(
                     font,
                     text,
                     floatValue + (width - font.width(text)) / 2.0F,
                     y + (height - font.height()) / 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7294(f4 * f5)
                  );
               } else {
                  float f5 = this.previewFade.computeIfAbsent(uuid, uuid2 -> new Animation(260L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
                  if (f5 < 1.0F) {
                     var4x.drawRoundedRect(
                        floatValue, y, width, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(f4 * 2.0F * (1.0F - f5))
                     );
                  }

                  var4x.drawRoundedTexture(texture, floatValue, y, width, height, headerRadius, new ZenithColor(255, 255, 255).method7294(f4 * f5));
               }
            }
         );
      }
   }

   private void description(StoreApi.Data2 cloudTwin) {
      String description = cloudTwin.description();
      if (description != null && !description.isBlank()) {
         Font font = Fonts.NEW_REGULAR.getFont(4.8F);
         float shownWidth = this.asideWidth() - 16.0F;
         List<String> strings = wrapText(description, font, shownWidth, 6);
         float f = font.height() + 1.5F;
         this.asideCustom(strings.size() * f + GuiStyle.PADDING.intValue(), (var3x, var4x, var5x, var6x, f2, f3, previewAlpha, zenithStyle) -> {
            float centeredTextY = f2;

            for (String text : strings) {
               var3x.drawText(font, text, var6x + 8.0F, centeredTextY, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
               centeredTextY += f;
            }
         });
      }
   }

   private void asideHint(String description) {
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      float shownWidth = this.asideWidth() - 16.0F;
      List<String> strings = wrapText(description, font, shownWidth, 8);
      float f = font.height() + 1.5F;
      this.asideCustom(strings.size() * f + GuiStyle.PADDING.intValue(), (var3x, var4x, var5x, f2, f3, f4, previewAlpha, zenithStyle) -> {
         float centeredTextY = f3;

         for (String text : strings) {
            var3x.drawText(font, text, f2 + 8.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY += f;
         }
      });
   }

   private void emptyState(String key, String str) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> strings = wrapText(t(str), font2, this.mainWidth() - 32.0F, 4);
      float f = font2.height() + 2.0F;
      float f2 = 30.0F;
      this.custom(
         f2 + font.height() + 6.0F + strings.size() * f + GuiStyle.PADDING.intValue() * 2.0F, (var6x, var7x, n, f3, f4, f5, previewAlpha, zenithStyle) -> {
            String text = t(key);
            var6x.drawText(font, text, f3 + (f5 - font.width(text)) / 2.0F, f4 + f2, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
            float centeredTextY = f4 + f2 + font.height() + 6.0F;

            for (String text2 : strings) {
               var6x.drawText(font2, text2, f3 + (f5 - font2.width(text2)) / 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               centeredTextY += f;
            }
         }
      );
   }

   private void asideChips(ChipStrip loadModeChips, IntSupplier intSupplier, IntConsumer intConsumer, String key) {
      float width = this.asideWidth() - 16.0F;
      this.asideCustom(loadModeChips.height(width) + GuiStyle.PADDING.intValue(), (var3x, var4x, var5x, f, y, f2, previewAlpha, zenithStyle) -> {
         loadModeChips.render(var3x, var4x, var5x, f + 8.0F, y, f2 - 16.0F, intSupplier.getAsInt(), previewAlpha, zenithStyle);
         if (loadModeChips.hovered() >= 0) {
            HoverInfo.request(loadModeChips.label(loadModeChips.hovered()), t(key));
         }
      }, () -> {
         int var3x = loadModeChips.hovered();
         if (var3x >= 0) {
            intConsumer.accept(var3x);
            this.invalidate();
         }
      }, null);
   }

   private Animation animation(String key, int duration) {
      return this.hoverAnimations.computeIfAbsent(key, var1x -> new Animation(duration, 0.0F, Easing.INSTANCE12));
   }

   private void asideAction(String str, MenuGlyph icon, String label, String str2, int n, String description, Runnable onClick) {
      this.asideAction(label, icon, () -> "", onClick, description);
   }

   private void asideDanger(String armedKey, String label, String description, Runnable runnable) {
      this.asideAction(label, MenuGlyph.CLOSE, () -> armedKey.equals(this.armedKey) ? t("gui.scripts.market.again") : "", () -> {
         if (armedKey.equals(this.armedKey)) {
            this.disarm();
            runnable.run();
         } else {
            this.armedKey = armedKey;
            this.armedAt = System.currentTimeMillis();
            this.invalidate();
         }
      }, description);
   }

   private void disarm() {
      this.armedKey = "";
      this.armedAt = 0L;
   }

   private void upload(String child) {
      StoreApi storeApi = store();
      File file = new File(Manager.file, child);
      if (storeApi != null && file.isFile()) {
         try {
            byte[] allBytes = Files.readAllBytes(file.toPath());
            storeApi.method1972("config", display(child), file.getName(), "", VISIBILITY[this.uploadVisibility], allBytes, null, 0L).whenComplete((var1x, var2x) -> {
               this.status = var2x == null ? t("gui.configs.report.uploaded") + " " + var1x.name() : shortError(var2x);
               ConfigSyncService.getInstance().method675();
               this.statusAt = System.currentTimeMillis();
               this.refetch();
               this.bump();
            });
         } catch (Exception e) {
            this.report(t("gui.configs.report.readFailed"));
         }
      } else {
         this.report(t("gui.configs.report.nothingToSend"));
      }
   }

   private void pushContent(StoreApi.Data2 cloudTwin) {
      StoreApi storeApi = store();
      ConfigFile configFile = Zenith.getInstance().getManager().method7783(cloudTwin.name());
      if (storeApi != null && configFile != null) {
         try {
            byte[] allBytes = Files.readAllBytes(configFile.getFile().toPath());
            storeApi.method1972(cloudTwin.kind(), cloudTwin.name(), cloudTwin.fileName(), cloudTwin.description(), cloudTwin.getText7(), allBytes, cloudTwin.getUUID(), cloudTwin.getLongValue2())
               .whenComplete((var1x, var2x) -> {
                  this.status = var2x == null ? t("gui.configs.report.updatedTo") + " v" + var1x.getLongValue2() : shortError(var2x);
                  this.statusAt = System.currentTimeMillis();
                  this.refetch();
                  this.bump();
               });
         } catch (Exception e) {
            this.report(t("gui.configs.report.readFailed"));
         }
      } else {
         this.report(t("gui.configs.report.noLocal").replace("{n}", cloudTwin.name()));
      }
   }

   private void download(StoreApi.Data2 cloudTwin) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1970(cloudTwin.getUUID()).whenComplete((var2x, throwable) -> {
            if (throwable != null) {
               this.status = shortError(throwable);
               this.statusAt = System.currentTimeMillis();
               this.bump();
            } else {
               try {
                  File file = saveDownloaded(cloudTwin, var2x);
                  if (!cloudTwin.mine()) {
                     this.taken().remember(cloudTwin);
                  }

                  this.status = t("gui.configs.report.downloaded").replace("{n}", display(file.getName()));
               } catch (Exception e) {
                  this.status = t("gui.configs.report.writeFailed");
               }

               this.statusAt = System.currentTimeMillis();
               this.bump();
            }
         });
      }
   }

   private static String extension() {
      return "." + "Zenith".toLowerCase(Locale.ROOT);
   }

   public static File targetFile(StoreApi.Data2 entry) {
      String child = recordedFile(entry.getUUID());
      return child != null ? new File(Manager.file, child) : new File(Manager.file, safeName(entry.name()) + extension());
   }

   public static File downloadTarget(StoreApi.Data2 cloudTwin, byte[] bytes) {
      String child = recordedFile(cloudTwin.getUUID());
      if (child != null) {
         return new File(Manager.file, child);
      } else {
         String str = safeName(cloudTwin.name());
         File file = new File(Manager.file, str + extension());
         if (file.exists() && !sameContent(file, bytes)) {
            for (int i = 2; i < 1000; i++) {
               File file2 = new File(Manager.file, str + " (" + i + ")" + extension());
               if (!file2.exists() || sameContent(file2, bytes)) {
                  return file2;
               }
            }

            return new File(Manager.file, str + " (" + UUID.randomUUID() + ")" + extension());
         } else {
            return file;
         }
      }
   }

   public static File saveDownloaded(StoreApi.Data2 cloudTwin, byte[] bytes) throws IOException {
      synchronized (DOWNLOADED_FILES) {
         File file = downloadTarget(cloudTwin, bytes);
         file.getParentFile().mkdirs();
         Files.write(file.toPath(), bytes);
         recordFile(cloudTwin.getUUID(), file.getName());
         return file;
      }
   }

   private static boolean sameContent(File file, byte[] bytes) {
      try {
         return file.isFile() && file.length() == bytes.length && Arrays.equals(Files.readAllBytes(file.toPath()), bytes);
      } catch (Exception e) {
         return false;
      }
   }

   private static String recordedFile(UUID uuid) {
      if (uuid == null) {
         return null;
      } else {
         synchronized (DOWNLOADED_FILES) {
            loadDownloadedFiles();
            String child = DOWNLOADED_FILES.get(uuid.toString());
            return child != null && !new File(Manager.file, child).isFile() ? null : child;
         }
      }
   }

   private static void recordFile(UUID uuid, String name) {
      if (uuid != null) {
         synchronized (DOWNLOADED_FILES) {
            loadDownloadedFiles();
            if (!name.equals(DOWNLOADED_FILES.get(uuid.toString()))) {
               DOWNLOADED_FILES.put(uuid.toString(), name);

               while (DOWNLOADED_FILES.size() > 1024) {
                  DOWNLOADED_FILES.remove(DOWNLOADED_FILES.keySet().iterator().next());
               }

               JsonObject json = new JsonObject();
               DOWNLOADED_FILES.forEach(json::addProperty);
               CloudJsonIo.method5993(Zenith.file.toPath().resolve("cloud-config-files.json"), json);
            }
         }
      }
   }

   private static void loadDownloadedFiles() {
      if (!downloadedFilesLoaded) {
         downloadedFilesLoaded = true;
         JsonObject json = CloudJsonIo.method5992(Zenith.file.toPath().resolve("cloud-config-files.json"));
         if (json != null) {
            for (Map.Entry entry : json.entrySet()) {
               try {
                  String string = ((JsonElement)entry.getValue()).getAsString();
                  if (!string.isBlank() && string.indexOf(47) < 0 && string.indexOf(92) < 0 && !string.contains("..")) {
                     DOWNLOADED_FILES.put(UUID.fromString((String)entry.getKey()).toString(), string);
                  }
               } catch (RuntimeException e) {
               }
            }
         }
      }
   }

   private void toggleLike(UUID uuid) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1978(uuid).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               this.status = shortError(var2x);
               this.statusAt = System.currentTimeMillis();
            }

            this.refetch();
            this.bump();
         });
      }
   }

   private void redeem() {
      String str = this.codeSetting.getValue();
      StoreApi storeApi = store();
      if (storeApi != null && str != null && !str.isBlank()) {
         storeApi.method1986(str.trim()).whenComplete((var1x, var2x) -> {
            this.status = var2x == null ? t("gui.configs.report.access") + " " + var1x.name() : shortError(var2x);
            this.statusAt = System.currentTimeMillis();
            this.codeSetting.setValue("");
            this.refetch();
            this.bump();
         });
      } else {
         this.report(t("gui.configs.report.enterCode"));
      }
   }

   private void changeVisibility(StoreApi.Data2 cloudTwin, int n) {
      StoreApi storeApi = store();
      if (storeApi != null && n >= 0 && n < VISIBILITY.length) {
         if (visibilityIndex(cloudTwin.getText7()) != n) {
            storeApi.method1974(cloudTwin.getUUID(), null, null, VISIBILITY[n]).whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  this.status = shortError(var2x);
                  this.statusAt = System.currentTimeMillis();
               }

               this.refetch();
               this.bump();
            });
         }
      }
   }

   private Manager.Type loadMode() {
      return switch (this.loadMode) {
         case 1 -> Manager.Type.IGNORE_BINDS;
         case 2 -> Manager.Type.ONLY_THEMES;
         default -> Manager.Type.ALL;
      };
   }

   private static int visibilityIndex(String str) {
      if (str == null) {
         return 0;
      } else {
         String upperCase = str.toUpperCase(Locale.ROOT);

         for (int i = 0; i < VISIBILITY.length; i++) {
            if (VISIBILITY[i].equals(upperCase)) {
               return i;
            }
         }

         return 0;
      }
   }

   private static String display(String selectedLocal) {
      String suffix = "." + "Zenith".toLowerCase(Locale.ROOT);
      return selectedLocal != null && selectedLocal.toLowerCase(Locale.ROOT).endsWith(suffix) ? selectedLocal.substring(0, selectedLocal.length() - suffix.length()) : blank(selectedLocal);
   }

   private static String plural(int n, String str, String str2, String str3) {
      int n2 = n % 100;
      if (n2 >= 11 && n2 <= 14) {
         return str3;
      } else {
         return switch (n % 10) {
            case 1 -> str;
            case 2, 3, 4 -> str2;
            default -> str3;
         };
      }
   }

   private static String blank(String name) {
      return name == null ? "" : name;
   }

   private static String lower(String str) {
      return str != null && !str.isBlank() ? str.toLowerCase(Locale.ROOT) : "-";
   }

   private static String blankToDash(String server) {
      return server != null && !server.isBlank() ? server : "-";
   }

   public static String safeName(String name) {
      StringBuilder builder = new StringBuilder();
      String str = name == null ? "" : name.strip();

      for (int i = 0; i < str.length() && builder.length() < 80; i++) {
         char charAt = str.charAt(i);
         boolean flag = charAt < ' ' || charAt == 127 || "\\/:*?\"<>|".indexOf(charAt) >= 0;
         builder.append(flag ? '_' : charAt);
      }

      String str2 = builder.toString().strip();

      while (str2.endsWith(".") || str2.endsWith(" ")) {
         str2 = str2.substring(0, str2.length() - 1);
      }

      boolean flag = false;

      for (int i = 0; i < str2.length(); i++) {
         if (Character.isLetterOrDigit(str2.charAt(i))) {
            flag = true;
            break;
         }
      }

      if (!flag) {
         return "config";
      } else {
         int indexOf = str2.indexOf(46);
         String str3 = indexOf < 0 ? str2 : str2.substring(0, indexOf);
         return RESERVED.contains(str3.toLowerCase(Locale.ROOT)) ? "_" + str2 : str2;
      }
   }

   private static String shortError(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      String message = throwable2.getMessage();
      if (message == null || message.isBlank()) {
         message = throwable2.getClass().getSimpleName();
      }

      return "!" + (message.length() > 40 ? message.substring(0, 39) + "…" : message);
   }

   public void openCloudTab() {
      this.selectedCloud = null;
      this.invalidate();
   }

   private final class EntryRow {
      private final StoreApi.Data2 entry;
      private final boolean have;
      private final String cardKey;
      private final String pillKey;
      private final String version;
      private final String likes;
      private final String description;
      private boolean downloadHot;

      private EntryRow(StoreApi.Data2 entry) {
         this.entry = entry;
         this.have = CloudConfigsSectionPanel.targetFile(entry).isFile();
         this.cardKey = "cloud:" + entry.getUUID();
         this.pillKey = "dl:" + entry.getUUID();
         this.version = "v" + entry.getLongValue2();
         this.likes = "♥ " + entry.getIntValue();
         this.description = entry.description() != null && !entry.description().isBlank() ? entry.description().replace('\n', ' ') : null;
      }

      private void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float y, float f2, float previewAlpha, ZenithStyle currentStyle) {
         BoundsData boundsData = new BoundsData(f + 8.0F, y, f2 - 16.0F, 36.0F);
         boolean contains = boundsData.contains(lastClickX, lastClickY);
         CloudConfigsSectionPanel.this.card(
            menuDrawContext, currentStyle, boundsData, this.cardKey, this.entry.name(), contains, this.entry.getUUID().equals(CloudConfigsSectionPanel.this.selectedCloud), previewAlpha
         );
         CloudConfigsSectionPanel.this.avatar(menuDrawContext, this.entry.name(), this.entry, boundsData.getFloatValue() + 7.0F, ScriptMarketTab.cardAvatarY(boundsData), 22.0F, previewAlpha);
         CloudConfigsSectionPanel.this.pillHot = false;
         float drawPill = CloudConfigsSectionPanel.this.drawPill(
            menuDrawContext,
            lastClickX,
            lastClickY,
            boundsData,
            this.pillKey,
            SectionPanel.t(this.have ? "gui.market.config.redownload" : "gui.market.config.download"),
            this.have,
            previewAlpha,
            currentStyle
         );
         this.downloadHot = contains && CloudConfigsSectionPanel.this.pillHot;
         float cardTextX = ScriptMarketTab.cardTextX(boundsData);
         float f3 = drawPill - 7.0F;
         ScriptMarketTab.CardLines cardLines = ScriptMarketTab.cardLines(boundsData);
         ScriptMarketTab.drawTitleLine(
            menuDrawContext,
            currentStyle,
            this.entry.name(),
            this.version,
            this.entry.getIntValue() <= 0 && !this.entry.isFlag3() ? "" : this.likes,
            this.entry.isFlag3() ? currentStyle.getHeartIcon().getZenithColor() : null,
            cardTextX,
            f3,
            cardLines.title(),
            previewAlpha
         );
         ScriptMarketTab.drawDescription(menuDrawContext, currentStyle, this.description, null, cardTextX, f3, cardLines.note(), previewAlpha);
         ArrayList elements = new ArrayList();
         elements.add(
            this.entry.mine()
               ? SectionPanel.t(CloudConfigsSectionPanel.VISIBILITY_NAMES[CloudConfigsSectionPanel.visibilityIndex(this.entry.getText7())])
               : CloudConfigsSectionPanel.blankToDash(this.entry.getText8())
         );
         elements.add(CloudCard.size(this.entry.getLongValue()));
         elements.add(CloudConfigsSectionPanel.ago(this.entry.getLongValue4()));
         if (this.entry.server() != null && !this.entry.server().isBlank()) {
            elements.add(this.entry.server());
         }

         ScriptMarketTab.drawMeta(menuDrawContext, String.join("  ·  ", elements), currentStyle.getTextTertiary().getZenithColor(), cardTextX, cardTextX, f3, cardLines.meta(), previewAlpha);
         if (contains) {
            HoverInfo.request(
               this.entry.name(), this.downloadHot ? SectionPanel.t("gui.configs.download.desc") : CloudConfigsSectionPanel.cardHover(this.entry)
            );
         }
      }

      private void primary() {
         CloudConfigsSectionPanel.this.download(this.entry);
      }

      private void click() {
         CloudConfigsSectionPanel.this.selectedCloud = this.entry.getUUID().equals(CloudConfigsSectionPanel.this.selectedCloud) ? null : this.entry.getUUID();
         CloudConfigsSectionPanel.this.selectedLocal = null;
         CloudConfigsSectionPanel.this.disarm();
         CloudConfigsSectionPanel.this.invalidate();
      }
   }

   private final class FileRow {
      private final Manager manager;
      private final CloudConfigsSectionPanel.LocalFile file;
      private final Manager.Data summary;
      private final StoreApi.Data2 twin;
      private final boolean inCloud;
      private final String cardKey;
      private final String pillKey;
      private boolean applyHot;

      private FileRow(Manager manager, CloudConfigsSectionPanel.LocalFile file) {
         this.manager = manager;
         this.file = file;
         this.summary = manager.method7772(file.name());
         this.twin = CloudConfigsSectionPanel.this.cloudTwin(file.name());
         this.inCloud = this.twin != null;
         this.cardKey = "file:" + file.name();
         this.pillKey = "apply:" + file.name();
      }

      private void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float y, float f2, float previewAlpha, ZenithStyle currentStyle) {
         boolean flag = this.file.name().equals(CloudConfigsSectionPanel.this.selectedLocal);
         BoundsData boundsData = new BoundsData(f + 8.0F, y, f2 - 16.0F, 36.0F);
         boolean contains = boundsData.contains(lastClickX, lastClickY);
         CloudConfigsSectionPanel.this.card(menuDrawContext, currentStyle, boundsData, this.cardKey, this.file.display(), contains, flag, previewAlpha);
         CloudConfigsSectionPanel.this.avatar(menuDrawContext, this.file.display(), null, boundsData.getFloatValue() + 7.0F, ScriptMarketTab.cardAvatarY(boundsData), 22.0F, previewAlpha);
         boolean have = this.file.name().equals(CloudConfigsSectionPanel.this.appliedName);
         CloudConfigsSectionPanel.this.pillHot = false;
         float drawPill = CloudConfigsSectionPanel.this.drawPill(
            menuDrawContext, lastClickX, lastClickY, boundsData, this.pillKey, SectionPanel.t(have ? "gui.configs.tag.applied" : "gui.configs.applyShort"), have, previewAlpha, currentStyle
         );
         this.applyHot = contains && CloudConfigsSectionPanel.this.pillHot;
         ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
         float cardTextX = ScriptMarketTab.cardTextX(boundsData);
         float f3 = drawPill - 7.0F;
         ScriptMarketTab.CardLines cardLines = ScriptMarketTab.cardLines(boundsData);
         ScriptMarketTab.drawTitleLine(menuDrawContext, currentStyle, this.file.display(), "", "", null, cardTextX, f3, cardLines.title(), previewAlpha);
         String description = this.summary == null
            ? "…"
            : SectionPanel.t("gui.configs.row.meta")
               .replace("{m}", String.valueOf(this.summary.getItems().size()))
               .replace("{b}", String.valueOf(this.summary.getIntValue()));
         ScriptMarketTab.drawDescription(menuDrawContext, currentStyle, description, null, cardTextX, f3, cardLines.note(), previewAlpha);
         float meta = cardLines.meta();
         ConfigSyncService.Type type = ConfigSyncService.getInstance().method671(this.file.display());
         String text = SectionPanel.t("gui.configs.sync.state." + type.name().toLowerCase(Locale.ROOT));

         ZenithColor zenithColor2 = switch (type) {
            case SYNCED -> CloudConfigsSectionPanel.OK;
            case ERROR -> CloudConfigsSectionPanel.ERROR;
            case LOCAL -> currentStyle.getTextTertiary().getZenithColor();
            default -> zenithColor;
         };
         float drawChip = ScriptMarketTab.drawChip(menuDrawContext, text, zenithColor2.method7292(0.14F), zenithColor2, cardTextX, meta, previewAlpha);
         if (this.inCloud) {
            String name = SectionPanel.t(CloudConfigsSectionPanel.VISIBILITY_NAMES[CloudConfigsSectionPanel.visibilityIndex(this.twin.getText7())])
               + "  ·  v"
               + this.twin.getLongValue2()
               + (this.twin.getIntValue() > 0 ? "  ·  ♥ " + this.twin.getIntValue() : "");
            drawChip = ScriptMarketTab.drawMeta(menuDrawContext, name, zenithColor, drawChip, cardTextX, f3, meta, previewAlpha);
         }

         String name = CloudCard.size(this.file.size()) + "  ·  " + CloudConfigsSectionPanel.ago(this.file.modified());
         ScriptMarketTab.drawMeta(menuDrawContext, name, currentStyle.getTextTertiary().getZenithColor(), drawChip, cardTextX, f3, meta, previewAlpha);
         if (contains) {
            HoverInfo.request(this.file.display(), SectionPanel.t(this.applyHot ? "gui.configs.apply.desc" : "gui.configs.row.desc"));
         }
      }

      private void click() {
         CloudConfigsSectionPanel.this.selectedLocal = this.file.name().equals(CloudConfigsSectionPanel.this.selectedLocal) ? null : this.file.name();
         CloudConfigsSectionPanel.this.selectedCloud = null;
         if (CloudConfigsSectionPanel.this.selectedLocal != null && this.twin != null) {
            CloudConfigsSectionPanel.this.fetchCodes(this.twin.getUUID());
         }

         CloudConfigsSectionPanel.this.disarm();
         CloudConfigsSectionPanel.this.invalidate();
      }

      private void apply() {
         CloudConfigsSectionPanel.this.applyLocal(this.manager, this.file.name());
      }
   }

   private static final class LocalFile {
      private final String name;
      private final String display;
      private final String meta;
      private final long size;
      private final long modified;

      public String name() {
         return this.name;
      }

      public String display() {
         return this.display;
      }

      public String meta() {
         return this.meta;
      }

      public long size() {
         return this.size;
      }

      public long modified() {
         return this.modified;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof LocalFile other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.display, other.display) && java.util.Objects.equals(this.meta, other.meta) && java.util.Objects.equals(this.size, other.size) && java.util.Objects.equals(this.modified, other.modified);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, display, meta, size, modified);
      }

      @Override
      public String toString() {
         return "LocalFile[" + "name=" + this.name + ", " + "display=" + this.display + ", " + "meta=" + this.meta + ", " + "size=" + this.size + ", " + "modified=" + this.modified + "]";
      }

      private LocalFile(String name, String display, String meta, long size, long modified) {
         this.name = name;
         this.display = display;
         this.meta = meta;
         this.size = size;
         this.modified = modified;
      }
   }
}

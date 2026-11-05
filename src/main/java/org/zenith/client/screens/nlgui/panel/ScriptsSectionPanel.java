package org.zenith.client.screens.nlgui.panel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
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
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.CodeArea;
import org.zenith.client.screens.scripts.ScriptIcons;
import org.zenith.client.screens.scripts.ScriptInfoPage;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.client.scripting.Module1;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.AiChatStorage;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.Server;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ScriptsSectionPanel extends SectionPanel {
   private static final int TAB_SCRIPTS = 0;
   private static final int TAB_CONFIGS = 1;
   private static final int TAB_MARKET = 2;
   private static final int TAB_AI = 3;
   private static final float CARD_HEIGHT = 26.0F;
   private static final float LOG_ROW = 9.0F;
   private static final float WORKSPACE_EXTRA = 40.0F;
   private static final float WS_INSPECTOR = 124.0F;
   private static final float WS_CONSOLE = 62.0F;
   private static final float FILE_ROW = 14.0F;
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor WARN = new ZenithColor(247, 206, 59);
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor EDITOR_BG = new ZenithColor(10, 11, 16, 150);
   private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");
   private static final String[][] TEMPLATES = new String[][]{
      {"empty", "gui.scripts.template.empty"},
      {"hud", "gui.scripts.template.hud"},
      {"esp", "gui.scripts.template.esp"},
      {"combat", "gui.scripts.template.combat"},
      {"chat", "gui.scripts.template.chat"},
      {"fun", "gui.scripts.template.fun"}
   };
   private static final float HERO_HEIGHT = 32.0F;
   private static final float TEMPLATE_ROW = 19.0F;
   private static final float EMPTY_HEIGHT = 70.0F;
   private static final float ROW_HEIGHT = 36.0F;
   private static final float ROW_GAP = 4.0F;
   private final List<Object[]> homeHits = new ArrayList<>();
   private final Map<String, Animation> hoverAnimations = new HashMap<>();
   private final Animation creatorAnimation = new Animation(260L, 0.0F, Easing.INSTANCE15);
   private boolean creatorOpen;
   private BoundsData headerBack;
   private BoundsData headerRoot;
   private long lastSignature;
   private long lastCheck;
   private final List<Object[]> toggles = new ArrayList<>();
   private final List<String> evalLines = new ArrayList<>();
   private Script workspace;
   private Script settingsScript;
   private Path file;
   private List<Path> files = new ArrayList<>();
   private CodeArea code;
   private final Animation workspaceAnimation = new Animation(360L, 0.0F, Easing.INSTANCE15);
   private final Animation filePlate = new Animation(220L, 0.0F, Easing.INSTANCE15);
   private boolean filePlateReady;
   private final Map<String, BoundsData> headerButtons = new LinkedHashMap<>();
   private final Map<String, Runnable> headerActions = new LinkedHashMap<>();
   private final List<Object[]> fileRects = new ArrayList<>();
   private BoundsData backRect;
   private BoundsData newFileRect;
   private BoundsData leftBounds;
   private BoundsData codeBounds;
   private BoundsData consoleBounds;
   private BoundsData inputBounds;
   private BoundsData inspectorBounds;
   private final StringBuilder input = new StringBuilder();
   private boolean inputFocused;
   private final List<String> history = new ArrayList<>();
   private int historyIndex = -1;
   private float consoleScroll;
   private float inspectorScroll;
   private float inspectorScrollTarget;
   private String toast = "";
   private long toastAt;
   private boolean toastError;
   private final Map<Module1, List<InspectorRow>> moduleRows = new IdentityHashMap<>();
   private final Map<Module1, Boolean> expanded = new IdentityHashMap<>();
   private final List<Object[]> inspectorHits = new ArrayList<>();
   private InspectorRow activeRow;
   private int filesTick;
   private long filesAt;
   private boolean infoPage;
   private BoundsData infoRect;
   private ScriptInfoPage info;
   private final ScriptMarketTab market = new ScriptMarketTab(this);
   private final ScriptAiTab ai = new ScriptAiTab(this);
   private final CloudConfigsSectionPanel configs = new CloudConfigsSectionPanel();
   private static final float TAB_SHIFT = 14.0F;
   private final Animation tabSwitch = new Animation(220L, 1.0F, Easing.INSTANCE12);
   private float tabDirection = 1.0F;
   private int shownTab = -1;
   private long enteredAt;
   private int enteredTab;
   private long asideEnteredAt;
   private final Map<String, ScriptsSectionPanel.FileCount> fileCounts;
   private final Map<String, Animation> switchAnimations;
   private final Map<String, Long> pressedAt;
   private double lastClickX;
   private double lastClickY;

   public ScriptsSectionPanel() {
      this.configs.setEmbedded(true);
      this.enteredAt = System.currentTimeMillis();
      this.enteredTab = -1;
      this.asideEnteredAt = System.currentTimeMillis();
      this.fileCounts = new HashMap<>();
      this.switchAnimations = new HashMap<>();
      this.pressedAt = new HashMap<>();
      this.lastClickX = -1.0;
      this.lastClickY = -1.0;
   }

   private boolean storeTab() {
      return this.getActiveTab() == 0 || this.getActiveTab() == 2;
   }

   private boolean configsTab() {
      return this.workspace == null && this.getActiveTab() == 1;
   }

   public void openConfigsCloud() {
      this.selectTab(1);
      this.configs.openCloudTab();
   }

   public void openMarket(int n) {
      if (this.workspace != null) {
         this.closeWorkspace();
      }

      this.selectTab(2);
      this.market.openCategory(n);
      this.invalidate();
   }

   public Storage hostOrNull() {
      return host();
   }

   public void openWorkspaceFile(Script workspace, String text) {
      this.openWorkspace(workspace);
      Storage storage = host();
      if (storage != null) {
         try {
            Path path2 = workspace.getPath() == null ? workspace.getPath2() : storage.method1346(workspace, text);
            if (Files.isRegularFile(path2)) {
               this.openFile(path2);
            }
         } catch (Exception e) {
         }
      }
   }

   public void filesDropped(List<Path> paths) {
      if (this.workspace == null && this.getActiveTab() == 3) {
         this.ai.attachFiles(paths);
      }
   }

   public void replayEntrance() {
      this.enteredAt = System.currentTimeMillis();
      this.asideEnteredAt = this.enteredAt;
   }

   public void replayAside() {
      this.asideEnteredAt = System.currentTimeMillis();
   }

   public float asideEnter(int length) {
      float f = (float)(System.currentTimeMillis() - this.asideEnteredAt - Math.min(length, 14) * 38L) / 340.0F;
      if (f <= 0.0F) {
         return 0.0F;
      } else if (f >= 1.0F) {
         return 1.0F;
      } else {
         float f2 = 1.0F - f;
         return 1.0F - f2 * f2 * f2;
      }
   }

   public SectionPanel.AsideRenderer asideEntering(int length, SectionPanel.AsideRenderer asideRenderer) {
      return (menuDrawContext, mouseX, mouseY, f, f2, asideWidth, f3, currentStyle) -> {
         float asideEnter = this.asideEnter(length);
         if (!(asideEnter <= 0.001F)) {
            asideRenderer.render(menuDrawContext, mouseX, mouseY, f + (1.0F - asideEnter) * 4.0F, f2 + (1.0F - asideEnter) * 7.0F, asideWidth, f3 * asideEnter, currentStyle);
         }
      };
   }

   public float enter(int n) {
      float f = (float)(System.currentTimeMillis() - this.enteredAt - Math.min(n, 14) * 38L) / 340.0F;
      if (f <= 0.0F) {
         return 0.0F;
      } else if (f >= 1.0F) {
         return 1.0F;
      } else {
         float f2 = 1.0F - f;
         return 1.0F - f2 * f2 * f2;
      }
   }

   public SectionPanel.AsideRenderer entering(int n, SectionPanel.AsideRenderer asideRenderer) {
      return (menuDrawContext, mouseX, mouseY, f, f2, asideWidth, f3, currentStyle) -> {
         float enter = this.enter(n);
         if (!(enter <= 0.001F)) {
            asideRenderer.render(menuDrawContext, mouseX, mouseY, f + (1.0F - enter) * 4.0F, f2 + (1.0F - enter) * 7.0F, asideWidth, f3 * enter, currentStyle);
         }
      };
   }

   private ScriptInfoPage info() {
      if (this.info == null) {
         this.info = new ScriptInfoPage(host(), this::listToast, settingsScript -> {
            if (settingsScript == null) {
               this.settingsScript = null;
               this.invalidate();
            } else {
               this.openSettings(settingsScript);
            }
         });
      }

      return this.info;
   }

   private void openSettings(Script settingsScript) {
      if (this.settingsScript != null && this.info != null && this.info.isDirty()) {
         this.info.apply();
      }

      this.settingsScript = settingsScript;
      this.info().setOnClose(this::closeSettings);
      this.info().open(settingsScript);
      this.invalidate();
   }

   private void closeSettings() {
      if (this.info != null && this.info.isDirty()) {
         this.info.apply();
      }

      this.settingsScript = null;
      this.invalidate();
   }

   private void showInfo() {
      if (this.workspace != null) {
         if (this.code != null && this.code.isDirty()) {
            this.save();
         }

         this.infoPage = true;
         this.info().open(this.workspace);
      }
   }

   private void showCode() {
      if (this.infoPage && this.info != null && this.info.isDirty()) {
         this.info.apply();
      }

      this.infoPage = false;
   }

   private static Storage host() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStorage();
   }

   @Override
   public String headerTitle() {
      return "Scripts & Configs";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CODE;
   }

   @Override
   public String sectionDescription() {
      return "gui.scripts.description";
   }

   @Override
   public float extraWidth() {
      return this.workspace != null ? 40.0F : 0.0F;
   }

   @Override
   public boolean hasLeftColumn() {
      return this.workspace != null;
   }

   @Override
   public float getContentWidth() {
      return 380.0F + NLMenuScreen.extraWidth;
   }

   @Override
   public float mainWidth() {
      return 236.0F;
   }

   @Override
   public float asideWidth() {
      return 144.0F;
   }

   private long signature() {
      Storage storage = host();
      if (storage == null) {
         return 0L;
      } else {
         long l = this.getActiveTab() * 31L;
         if (this.storeTab()) {
            l = l * 31L + this.market.revision();
         }

         for (Script script : storage.method1293()) {
            l = l * 31L + script.getId().hashCode();
            l = l * 31L + script.getType2().ordinal();
            l = l * 31L + script.getError().hashCode();
            l = l * 31L + script.getItems3().size();
            l = l * 31L + (storage.method1297(script) ? 1 : 0);
         }

         l = l * 31L + storage.method1328() + this.evalLines.size();
         return l * 31L + (this.creatorOpen ? 1 : 0) + (Server.getInstance().isRunning() ? 2 : 0);
      }
   }

   @Override
   public void build() {
      if (this.enteredTab != this.getActiveTab()) {
         this.enteredTab = this.getActiveTab();
         this.replayEntrance();
      }

      this.tabs(new String[]{"gui.scripts.tab.scripts", "gui.scripts.tab.configs", "gui.scripts.tab.market", "gui.scripts.tab.ai"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.CODE, MenuGlyph.CONFIGS, MenuGlyph.STORAGE, MenuGlyph.CHAT});
      if (this.getActiveTab() != 1) {
         Storage hostOrNull = host();
         if (hostOrNull == null) {
            this.caption("gui.scripts.unavailable");
         } else {
            switch (this.getActiveTab()) {
               case 3:
                  this.ai.build(hostOrNull);
                  break;
               default:
                  this.market.syncMode(this.getActiveTab() == 2);
                  this.market.build(hostOrNull);
            }
         }
      }
   }

   private ScriptsSectionPanel.RowInfo rowInfo(Storage storage, Script workspace) {
      long millis = 0L;

      try {
         millis = Files.getLastModifiedTime(workspace.getPath2()).toMillis();
      } catch (Exception e) {
      }

      long now = System.currentTimeMillis();
      ScriptsSectionPanel.FileCount fileCount = this.fileCounts.get(workspace.getId());
      int files;
      if (fileCount != null && fileCount.modified() == millis && now - fileCount.countedAt() < 2000L) {
         files = fileCount.files();
      } else {
         files = storage.method1345(workspace).size();
         this.fileCounts.put(workspace.getId(), new ScriptsSectionPanel.FileCount(millis, files, now));
      }

      return new ScriptsSectionPanel.RowInfo(workspace, files, millis);
   }

   public boolean creatorOpen() {
      return this.creatorOpen;
   }

   public void toggleCreator() {
      this.creatorOpen = !this.creatorOpen;
      this.creatorAnimation.reset(0.0F);
      this.creatorAnimation.method2810(1.0F);
      this.invalidate();
   }

   public void openScriptSettings(Script workspace) {
      if (this.workspace != null) {
         this.closeWorkspace();
      }

      super.selectTab(0);
      this.market.showLocal();
      this.openSettings(workspace);
   }

   public void closeSettingsIfOpen() {
      if (this.settingsScript != null) {
         this.closeSettings();
      }

      this.creatorOpen = false;
   }

   public void buildLocal(Storage hostOrNull) {
      int n = 1;
      ArrayList<Script> scripts = new ArrayList<>(hostOrNull.method1293());
      String str = this.searchQuery().toLowerCase(Locale.ROOT).strip();
      if (!str.isEmpty()) {
         scripts.removeIf(
            var1x -> !var1x.method1403().toLowerCase(Locale.ROOT).contains(str)
               && !var1x.getDescription().toLowerCase(Locale.ROOT).contains(str)
               && !var1x.getText3().toLowerCase(Locale.ROOT).contains(str)
         );
      }

      if (this.creatorOpen) {
         for (int i = 0; i < TEMPLATES.length; i++) {
            final int var5_i = i;
            this.custom(
                19.0F,
               (var2x, var3x, var4x, var5x, var6x, var7x, var8x, currentStyle) -> this.renderTemplate(var2x, var3x, var4x, var5x, var6x, var7x, var8x, currentStyle, var5_i)
            );
         }

         this.gap(6.0F);
      }

      if (scripts.isEmpty() && !this.creatorOpen) {
         this.custom(
            70.0F, this.entering(1, (var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8x) -> this.renderEmpty(var1x, var4x, var5x, var6x, var7x, var8x))
         );
      }

      for (Script workspace : scripts) {
         ScriptsSectionPanel.RowInfo rowInfo = this.rowInfo(hostOrNull, workspace);
         this.custom(
            40.0F,
            this.entering(
               n++,
               (var3x, var4x, var5x, var6x, var7x, var8x, previewAlpha, var10x) -> this.renderRow(var3x, var4x, var5x, var6x, var7x, var8x, previewAlpha, var10x, hostOrNull, rowInfo)
            )
         );
      }

      if (this.settingsScript != null && hostOrNull.method1294(this.settingsScript.getId()) != null) {
         this.asideFull(
            (var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8x) -> this.info()
               .renderCompact(
                  var1x,
                  var2x,
                  var3x,
                  var4x,
                  var5x,
                  var6x,
                  NLMenuScreen.contentBottom - var5x - GuiStyle.PADDING.intValue() * 4.0F - HoverInfo.boxHeight(),
                  var7x,
                  var8x
               )
         );
      } else {
         this.settingsScript = null;
         var scripts2 = hostOrNull.method1293();
         long count = scripts2.stream().filter(Script::isRunning).count();
         long l = scripts2.stream().filter(script -> script.getType2() == Script.Type2.FAILED).count();
         int n2 = scripts2.stream().mapToInt(script -> script.getItems3().size()).sum();
         this.asideCaption("gui.scripts.caption.overview");
         this.asideInfo("gui.scripts.info.running", () -> count + " / " + scripts2.size(), "gui.scripts.info.running.desc");
         this.asideInfo("gui.scripts.info.modules", () -> String.valueOf(n2), "gui.scripts.info.modulesAll.desc");
         if (l > 0L) {
            this.asideInfo("gui.scripts.info.failed", () -> String.valueOf(l), "gui.scripts.info.failed.desc");
         }

         this.asideInfo("gui.scripts.info.api", () -> "v3", "gui.scripts.info.api.desc");
         int updatesCount = this.market.updatesCount(hostOrNull);
         if (updatesCount > 0) {
            this.asideAction(
               "gui.scripts.market.updateAll",
               MenuGlyph.CLOUD,
               () -> String.valueOf(updatesCount),
               () -> this.market.updateAll(hostOrNull),
               "gui.scripts.market.updateAll.desc"
            );
         }

         this.asideCaption("gui.scripts.caption.actions");
         this.asideAction("gui.scripts.home.new", MenuGlyph.ACTION, () -> this.creatorOpen ? "" : "+", this::toggleCreator, "gui.scripts.home.new.desc");
         this.asideAction("gui.scripts.reloadAll", MenuGlyph.RESET, () -> "", () -> {
            hostOrNull.method1300();
            this.invalidate();
         }, "gui.scripts.reloadAll.desc");
         this.asideAction(
            "gui.scripts.folder", MenuGlyph.STORAGE, () -> "", () -> org.zenith.utility.misc.OsOpen.file(hostOrNull.getPath().toFile()), "gui.scripts.folder.desc"
         );
         this.asideAction(
            "gui.scripts.api",
            MenuGlyph.TEXT,
            () -> "",
            () -> org.zenith.utility.misc.OsOpen.file(hostOrNull.getPath().resolve("API.md").toFile()),
            "gui.scripts.api.desc"
         );
         this.asideAction(
            "gui.scripts.docs",
            MenuGlyph.NETWORK,
            () -> "",
            () -> org.zenith.utility.misc.OsOpen.uri(RegionUtil.method2038().get(0) + "/docs/addons"),
            "gui.scripts.docs.desc"
         );
         Server server = Server.getInstance();
         this.asideAction("gui.scripts.mcp", MenuGlyph.NETWORK, () -> t(server.isRunning() ? "gui.scripts.mcp.on" : "gui.scripts.mcp.off"), () -> {
            server.setEnabled(!server.isRunning());
            this.invalidate();
         }, "gui.scripts.mcp.desc");
         this.market.publishSection(hostOrNull);
         this.asideCaption("gui.scripts.caption.log");
         var data2s = hostOrNull.method1295();
         List<Storage.Data2> subList = data2s.subList(Math.max(0, data2s.size() - 7), data2s.size());
         if (subList.isEmpty()) {
            this.paragraph(t("gui.scripts.console.empty"), true);
         }

         for (Storage.Data2 sub : subList) {
            this.asideCustom(
               12.0F,
               (var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8x) -> {
                  Font font = Fonts.NEW_REGULAR.getFont(4.2F);
                  String var10x = sub.getText() + "  ";
                  var1x.drawText(
                     font, font.clip(var10x, var6x * 0.45F), var4x + 8.0F, var5x + 2.0F, var8x.getTextSecondary().getZenithColor().method7294(var7x)
                  );
                  float var11x = var4x + 8.0F + font.width(font.clip(var10x, var6x * 0.45F));
                  var1x.drawText(
                     font,
                     font.clip(sub.text(), var4x + var6x - 8.0F - var11x),
                     var11x,
                     var5x + 2.0F,
                     levelColor(sub.getText2(), var8x).method7294(var7x * 0.9F)
                  );
               }
            );
         }
      }
   }

   public void paragraph(String description, boolean flag) {
      Font font = Fonts.NEW_REGULAR.getFont(4.5F);
      float shownWidth = (flag ? 144.0F : 236.0F) - 16.0F;
      List<String> strings = wrapText(description, font, shownWidth, 10);
      SectionPanel.AsideRenderer entering = (var2x, var3x, var4x, var5x, var6x, var7x, previewAlpha, zenithStyle) -> {
         float centeredTextY = var6x + 3.0F;

         for (String text : strings) {
            var2x.drawText(font, text, var5x + 8.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY += font.height() + 2.0F;
         }
      };
      float hintsHeight = strings.size() * (font.height() + 2.0F) + 8.0F;
      if (flag) {
         this.asideCustom(hintsHeight, entering);
      } else {
         this.custom(hintsHeight, entering);
      }
   }

   public static ZenithColor cardFill(ZenithStyle currentStyle, float hot) {
      return currentStyle.getFieldSurfaceBackground().getZenithColor().method7295(currentStyle.getDisableActiveBg().getZenithColor(), 0.35F + hot * 0.55F);
   }

   public static void card(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float floatValue, float y, float width, float height, float hot, float previewAlpha) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, cardFill(currentStyle, hot).method7294(previewAlpha));
      menuDrawContext.drawRoundedBorder(floatValue, y, width, height, 0.15F, headerRadius, currentStyle.getFieldBorder().getZenithColor().method7294(2.0F + hot * 2.0F).method7294(previewAlpha));
   }

   public static void flowGlow(
      MenuDrawContext menuDrawContext, float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor, float f, float previewAlpha
   ) {
      double d = System.currentTimeMillis() / 1000.0;
      double d2 = d * 0.42 + Math.sin(d * 0.31) * 0.9;
      float f2 = (float)Math.cos(d2);
      float f3 = (float)Math.sin(d2);
      float f4 = f * (0.85F + 0.15F * (float)Math.sin(d * 0.8));
      menuDrawContext.drawRoundedRect(
         floatValue,
         y,
         width,
         height,
         headerRadius,
         GlowColor.method8096(
            glowCorner(zenithColor, -f2 - f3, f4, previewAlpha),
            glowCorner(zenithColor, -f2 + f3, f4, previewAlpha),
            glowCorner(zenithColor, f2 - f3, f4, previewAlpha),
            glowCorner(zenithColor, f2 + f3, f4, previewAlpha)
         )
      );
   }

   private static ZenithColor glowCorner(ZenithColor zenithColor, float f, float f2, float previewAlpha) {
      float f3 = Mth.clamp((f / 1.4142135F + 1.0F) * 0.5F, 0.0F, 1.0F);
      return zenithColor.method7292(f2 * f3 * f3 * previewAlpha);
   }

   public float hot(String key, boolean contains) {
      return this.hoverAnimations.computeIfAbsent(key, hoverAnimation -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(contains ? 1.0F : 0.0F);
   }

   private void renderHero(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle currentStyle, Storage storage) {
      Font font = Fonts.NEW_SEMIBOLD.getFont(6.4F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.5F);
      Font font3 = Fonts.NEW_MEDIUM.getFont(5.0F);
      long count = storage.method1293().stream().filter(Script::isRunning).count();
      long l = storage.method1293().stream().filter(script -> script.getType2() == Script.Type2.FAILED).count();
      menuDrawContext.drawText(font, t("gui.scripts.home.title"), f + 8.0F, f2 + 5.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      String text = storage.method1293().isEmpty()
         ? t("gui.scripts.home.none")
         : t("gui.scripts.home.running").replace("{r}", String.valueOf(count)).replace("{n}", String.valueOf(storage.method1293().size()))
            + (l > 0L ? "  ·  " + t("gui.scripts.home.failed").replace("{n}", String.valueOf(l)) : "");
      menuDrawContext.drawText(font2, text, f + 8.0F, f2 + 7.0F + font.height(), (l > 0L ? WARN : currentStyle.getTextTertiary().getZenithColor()).method7294(previewAlpha));
      String str = this.creatorOpen ? t("gui.scripts.home.cancel") : "+  " + t("gui.scripts.home.new");
      float width = font3.width(str) + 16.0F;
      BoundsData boundsData = new BoundsData(f + f3 - 8.0F - width, f2 + 6.0F, width, 15.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.hot("hero:new", contains);
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      ZenithColor zenithColor2 = cardFill(currentStyle, hot).method7295(zenithColor.method7292(0.14F), (this.creatorOpen ? 0.25F : 0.6F) + hot * 0.35F);
      boolean begin = this.pressFx.begin(menuDrawContext, "hero:new", boundsData);
      menuDrawContext.drawRoundedRect(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, zenithColor2.method7294(previewAlpha));
      this.pressFx.ink(menuDrawContext, "hero:new", boundsData, headerRadius.getFloatValue(), ZenithColor.INSTANCE.method7292(0.18F), previewAlpha);
      menuDrawContext.drawRoundedBorder(
         boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.15F, headerRadius, zenithColor.method7292(0.25F + hot * 0.3F).method7294(previewAlpha)
      );
      menuDrawContext.drawText(
         font3,
         str,
         boundsData.getFloatValue() + 8.0F,
         boundsData.getFloatValue2() + (15.0F - font3.height()) / 2.0F,
         (this.creatorOpen ? currentStyle.getTextSecondary().getZenithColor() : zenithColor).method7294(previewAlpha * (0.85F + hot * 0.15F))
      );
      this.pressFx.end(menuDrawContext, begin);
      this.homeHits.add(new Object[]{boundsData, "new"});
   }

   private void renderTemplate(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float f4, ZenithStyle currentStyle, int n) {
      float f5 = this.creatorAnimation.method2808(1.0F);
      float f6 = Mth.clamp(f5 * 1.8F - n * 0.15F, 0.0F, 1.0F);
      float previewAlpha = f4 * f6;
      float f7 = (1.0F - f6) * -4.0F;
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F + f7, f3 - 16.0F, 16.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.hot("template:" + n, contains);
      card(menuDrawContext, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
      float pressFlash = this.pressFlash("hero:template:" + TEMPLATES[n][0]);
      if (pressFlash > 0.01F) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            currentStyle.getPrimaryColor().getZenithColor().method7292(0.2F * pressFlash).method7294(previewAlpha)
         );
      }

      String str = TEMPLATES[n][0];

      MenuGlyph menuGlyph = switch (str) {
         case "hud" -> MenuGlyph.WINDOW;
         case "esp" -> MenuGlyph.BOX;
         case "combat" -> MenuGlyph.TARGET;
         case "chat" -> MenuGlyph.CHAT;
         default -> MenuGlyph.CODE;
      };
      menuGlyph.draw(
         menuDrawContext,
         boundsData.getFloatValue() + 7.0F,
         boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F,
         (contains ? currentStyle.getPrimaryColor() : currentStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
      String text = t(TEMPLATES[n][1]);
      menuDrawContext.drawText(
         font,
         text,
         boundsData.getFloatValue() + 18.0F,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
      );
      String text2 = contains ? t("gui.scripts.create.value") : t(TEMPLATES[n][1] + ".short");
      float f8 = boundsData.getFloatValue() + boundsData.width() - 7.0F - font2.width(text2);
      menuDrawContext.drawText(
         font2,
         font2.clip(text2, boundsData.width() - 30.0F - font.width(text)),
         Math.max(f8, boundsData.getFloatValue() + 24.0F + font.width(text)),
         boundsData.getFloatValue2() + (boundsData.height() - font2.height()) / 2.0F,
         (contains ? currentStyle.getPrimaryColor() : currentStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
      );
      this.homeHits.add(new Object[]{boundsData, "template:" + TEMPLATES[n][0]});
   }

   private void renderEmpty(MenuDrawContext menuDrawContext, float f, float f2, float f3, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.6F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.5F);
      float y = f2 + 2.0F;
      float height = 62.0F;
      card(menuDrawContext, currentStyle, f + 8.0F, y, f3 - 16.0F, height, 0.0F, previewAlpha);
      String text = t("gui.scripts.home.emptyTitle");
      List<String> strings = wrapText(t("gui.scripts.empty"), font2, f3 - 48.0F, 3);
      float f4 = 6.0F;
      float f5 = 4.0F;
      float f6 = font2.height() + 2.0F;
      float f7 = 6.0F + f4 + font.height() + f5 + strings.size() * f6 - 2.0F;
      float centeredTextY = y + (height - f7) / 2.0F;
      MenuGlyph.CODE.draw(menuDrawContext, f + f3 / 2.0F - 3.0F, centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      float centeredTextY2 = centeredTextY + 6.0F + f4;
      menuDrawContext.drawText(font, text, f + (f3 - font.width(text)) / 2.0F, centeredTextY2, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      float centeredTextY3 = centeredTextY2 + font.height() + f5;

      for (String str : strings) {
         menuDrawContext.drawText(font2, str, f + (f3 - font2.width(str)) / 2.0F, centeredTextY3, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY3 += font2.height() + 2.0F;
      }
   }

   public static String ago(long modified) {
      if (modified <= 0L) {
         return "";
      } else {
         long l = Math.max(0L, (System.currentTimeMillis() - modified) / 1000L);
         if (l < 60L) {
            return t("gui.scripts.ago.now");
         } else if (l < 3600L) {
            return t("gui.scripts.ago.min").replace("{n}", String.valueOf(l / 60L));
         } else {
            return l < 86400L
               ? t("gui.scripts.ago.hour").replace("{n}", String.valueOf(l / 3600L))
               : t("gui.scripts.ago.day").replace("{n}", String.valueOf(l / 86400L));
         }
      }
   }

   private void renderRow(
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      float f,
      float y,
      float f2,
      float previewAlpha,
      ZenithStyle currentStyle,
      Storage hostOrNull,
      ScriptsSectionPanel.RowInfo rowInfo
   ) {
      Script script = rowInfo.script();
      BoundsData cardRect = ScriptMarketTab.cardRect(f, y, f2);
      boolean contains = cardRect.contains(lastClickX, lastClickY);
      boolean flag = this.settingsScript != null && this.settingsScript.getId().equals(script.getId());
      float hot = this.hot("row:" + script.getId(), contains);
      float hot2 = this.hot("rowsel:" + script.getId(), flag);
      boolean flag2 = script.getType2() == Script.Type2.FAILED;
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      int color = script.getColor();
      ZenithColor letterColor = flag2 ? ERROR : (color == -1 ? zenithColor : new ZenithColor(color));
      ScriptMarketTab.drawCard(menuDrawContext, currentStyle, cardRect, hot, hot2, this.pressFlash("row:" + script.getId()), letterColor, previewAlpha);
      float size = 22.0F;
      float floatValue = cardRect.getFloatValue() + 7.0F;
      float cardAvatarY = ScriptMarketTab.cardAvatarY(cardRect);
      Identifier texture = ScriptIcons.get(script);
      if (texture != null) {
         menuDrawContext.drawRoundedTexture(texture, floatValue, cardAvatarY, size, size, ProxyDotRadiusData.method8069(size * 0.3F), ZenithColor.INSTANCE.method7294(previewAlpha));
      } else {
         ScriptMarketTab.letterAvatar(menuDrawContext, letterColor, script.method1403(), floatValue, cardAvatarY, size, previewAlpha);
      }

      ZenithColor zenithColor2 = script.isRunning() ? OK : (flag2 ? ERROR : currentStyle.getTextTertiary().getZenithColor());
      menuDrawContext.drawRoundedRect(floatValue + size - 4.5F, cardAvatarY + size - 4.5F, 6.0F, 6.0F, ProxyDotRadiusData.method8069(3.0F), cardFill(currentStyle, hot).method7294(previewAlpha));
      menuDrawContext.drawRoundedRect(floatValue + size - 3.5F, cardAvatarY + size - 3.5F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), zenithColor2.method7294(previewAlpha));
      float width = 14.0F;
      float height = 8.0F;
      float floatValue2 = cardRect.getFloatValue() + cardRect.width() - 7.0F - width;
      float y2 = cardRect.getFloatValue2() + (cardRect.height() - height) / 2.0F;
      StoreApi.Data2 data2 = this.market.updateFor(hostOrNull, script);
      boolean updating = this.market.updating(hostOrNull, script);
      BoundsData pillRect = null;
      if (data2 != null || updating) {
         String pillText = t(updating ? "gui.scripts.market.busy" : "gui.scripts.market.update");
         pillRect = ScriptMarketTab.pillRect(cardRect, floatValue2 - 7.0F, pillText);
         boolean contains2 = pillRect.contains(lastClickX, lastClickY);
         ScriptMarketTab.drawPill(menuDrawContext, currentStyle, pillRect, pillText, false, this.hot("upd:" + script.getId(), contains2), updating, previewAlpha);
         if (contains2 && data2 != null) {
            HoverInfo.request(t("gui.scripts.market.update"), t("gui.scripts.market.update.row").replace("{v}", String.valueOf(data2.getLongValue2())));
         }
      }

      float cardTextX = ScriptMarketTab.cardTextX(cardRect);
      float f3 = (pillRect != null ? pillRect.getFloatValue() : floatValue2) - 7.0F;
      ScriptMarketTab.CardLines cardLines = ScriptMarketTab.cardLines(cardRect);
      Font cardNoteFont = ScriptMarketTab.cardNoteFont();
      float hot3 = this.hot("open:" + script.getId(), contains && pillRect == null);
      String text = t("gui.scripts.card.open");
      float f4 = f3 - cardNoteFont.width(text);
      float f5 = hot3 > 0.01F ? f4 - 6.0F : f3;
      ScriptMarketTab.drawTitleLine(
         menuDrawContext, currentStyle, script.method1403(), script.getVersion().isBlank() ? "" : "v" + script.getVersion(), "", null, cardTextX, f5, cardLines.title(), previewAlpha
      );
      if (hot3 > 0.01F) {
         float centeredTextY = cardLines.title() + ScriptMarketTab.cardTitleFont().height() - cardNoteFont.height();
         menuDrawContext.drawText(cardNoteFont, text, f4 + (1.0F - hot3) * 6.0F, centeredTextY, zenithColor.method7294(previewAlpha * hot3));
      }

      if (!script.getError().isBlank()) {
         ScriptMarketTab.drawDescription(menuDrawContext, currentStyle, script.getError(), ERROR, cardTextX, f3, cardLines.note(), previewAlpha);
      } else {
         ScriptMarketTab.drawDescription(menuDrawContext, currentStyle, script.getDescription(), null, cardTextX, f3, cardLines.note(), previewAlpha);
      }

      float drawTags = ScriptMarketTab.drawTags(menuDrawContext, currentStyle, Arrays.asList(script.getText3().split(",")), cardTextX, cardTextX, f3, cardLines.meta(), previewAlpha);
      ArrayList elements = new ArrayList();
      elements.add(this.stateText(hostOrNull, script));
      OriginUtil.Data2 data22 = OriginUtil.method6921(script);
      if (data22 != null) {
         elements.add(
            data22.mine()
               ? t("gui.scripts.market.origin.mine")
               : t("gui.scripts.market.origin.from").replace("{n}", data22.getText3().isBlank() ? "?" : data22.getText3())
         );
      }

      if (script.isFlag()) {
         String replacement = ScriptPermissions.method5(script);
         if (!replacement.isEmpty()) {
            elements.add(t("gui.scripts.market.rights").replace("{r}", replacement));
         }
      }

      if (!script.getItems3().isEmpty()) {
         elements.add(t("gui.scripts.card.modules").replace("{n}", String.valueOf(script.getItems3().size())));
      }

      String str = ago(rowInfo.modified());
      if (!str.isEmpty()) {
         elements.add(str);
      }

      ScriptMarketTab.drawMeta(menuDrawContext, String.join("  ·  ", elements), currentStyle.getTextTertiary().getZenithColor(), drawTags, cardTextX, f3, cardLines.meta(), previewAlpha);
      this.drawSwitch(menuDrawContext, "script:" + script.getId(), floatValue2, y2, width, height, hostOrNull.method1297(script), previewAlpha, currentStyle);
      if (contains) {
         HoverInfo.request(script.method1403(), t("gui.scripts.card.hint"));
      }

      this.homeHits.add(new Object[]{new BoundsData(floatValue2 - 5.0F, cardRect.getFloatValue2(), width + 12.0F, cardRect.height()), "toggle", script});
      if (pillRect != null) {
         this.homeHits.add(new Object[]{pillRect, "update", script});
      }

      this.homeHits.add(new Object[]{cardRect, "open", script});
   }

   private String stateText(Storage hostOrNull, Script script) {
      return switch (script.getType2()) {
         case RUNNING -> t("gui.scripts.state.running");
         case FAILED -> t("gui.scripts.state.failed");
         default -> hostOrNull.method1297(script) ? t("gui.scripts.state.waiting") : t("gui.scripts.state.off");
      };
   }

   public void drawSwitch(MenuDrawContext menuDrawContext, String key, float floatValue, float y, float width, float height, boolean enabled, float previewAlpha, ZenithStyle currentStyle) {
      float hover = this.switchAnimations
         .computeIfAbsent(key, var1x -> new Animation(220L, enabled ? 1.0F : 0.0F, Easing.INSTANCE15))
         .method2808(enabled ? 1.0F : 0.0F);
      ZenithColor zenithColor = currentStyle.getFieldBorder().getZenithColor().method7294(3.0F);
      menuDrawContext.drawRoundedRect(
         floatValue, y, width, height, ProxyDotRadiusData.method8069(height / 2.0F), zenithColor.method7295(currentStyle.getPrimaryColor().getZenithColor(), hover).method7294(previewAlpha)
      );
      float f = height - 2.0F;
      float f2 = floatValue + 1.0F + (width - f - 2.0F) * hover;
      float f3 = (float)Math.sin(hover * Math.PI) * 1.5F;
      menuDrawContext.drawRoundedRect(
         f2 - f3 * 0.5F,
         y + 1.0F,
         f + f3,
         f,
         ProxyDotRadiusData.method8069(f / 2.0F),
         currentStyle.getTextSecondary().getZenithColor().method7295(ZenithColor.INSTANCE, hover).method7294(previewAlpha)
      );
   }

   private float pressFlash(String key) {
      return Smooth.press(this.pressedAt.getOrDefault(key, 0L), 340L);
   }

   private boolean handleHome(Object[] objects) {
      Storage storage = host();
      if (storage == null) {
         return false;
      } else {
         String str = (String)objects[1];
         if (objects.length > 2 && objects[2] instanceof Script script) {
            this.pressedAt.put("row:" + script.getId(), System.currentTimeMillis());
         } else {
            this.pressedAt.put("hero:" + str, System.currentTimeMillis());
            if (objects[0] instanceof BoundsData boundsData) {
               this.pressFx
                  .press(
                     "hero:" + str,
                     this.lastClickX >= 0.0 ? this.lastClickX : boundsData.getFloatValue() + boundsData.width() / 2.0F,
                     this.lastClickY >= 0.0 ? this.lastClickY : boundsData.getFloatValue2() + boundsData.height() / 2.0F
                  );
            }
         }

         switch (str) {
            case "new":
               this.creatorOpen = !this.creatorOpen;
               this.creatorAnimation.reset(0.0F);
               this.creatorAnimation.method2810(1.0F);
               this.invalidate();
               break;
            case "reload":
               storage.method1300();
               this.invalidate();
               break;
            case "folder":
               com.mojang.blaze3d.Blaze3D.openPath((storage.getPath().toFile()).toPath());
               break;
            case "api":
               com.mojang.blaze3d.Blaze3D.openPath((storage.getPath().resolve("API.md").toFile()).toPath());
               break;
            case "toggle":
               Script workspace = (Script)objects[2];
               storage.method1298(workspace, !storage.method1297(workspace));
               this.invalidate();
               break;
            case "open":
               this.openWorkspace((Script)objects[2]);
               break;
            case "update":
               this.market.update(storage, (Script)objects[2]);
               break;
            default:
               if (!str.startsWith("template:")) {
                  return false;
               }

               this.creatorOpen = false;
               this.create(str.substring("template:".length()));
         }

         return true;
      }
   }

   private BoundsData asideViewportOrNull() {
      float leftWidthPanel = NLMenuScreen.leftWidthPanel;
      BoundsData mainViewport = this.mainViewport();
      return mainViewport == null
         ? null
         : new BoundsData(
            mainViewport.getFloatValue() + mainViewport.width(), mainViewport.getFloatValue2() - 30.0F, 144.0F, NLMenuScreen.contentBottom - mainViewport.getFloatValue2() + 30.0F
         );
   }

   private void create(String string) {
      Storage storage = host();
      if (storage != null) {
         String argument = "script";

         for (int i = 2; storage.method1294(argument) != null || Files.exists(storage.getPath().resolve(argument)); i++) {
            argument = "script" + i;
         }

         try {
            Script workspace = storage.method1340(argument, string);
            storage.method1298(workspace, true);
            this.invalidate();
            this.openWorkspace(workspace);
         } catch (Exception e) {
            this.evalLines.add("! " + e.getMessage());
         }
      }
   }

   private static ZenithColor levelColor(String str, ZenithStyle currentStyle) {
      return switch (str) {
         case "error" -> ERROR;
         case "warn" -> WARN;
         case "eval" -> currentStyle.getTextEnable().getZenithColor();
         default -> currentStyle.getTextSecondary().getZenithColor();
      };
   }

   @Override
   public float footerHeight() {
      return this.workspace == null && this.getActiveTab() == 3 ? this.ai.footerHeight() : 0.0F;
   }

   @Override
   public void renderFooter(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float y, float f2, float previewAlpha, ZenithStyle zenithStyle) {
      if (this.getActiveTab() == 3) {
         this.ai.renderFooter(menuDrawContext, lastClickX, lastClickY, f, y, f2, previewAlpha, zenithStyle);
      }
   }

   @Override
   public boolean onFooterClicked(double lastMouseX, double lastMouseY, Enum3 enum2) {
      return this.getActiveTab() == 3 && this.ai.clickFooter(lastMouseX, lastMouseY);
   }

   @Override
   public float mainHeaderHeight() {
      if (this.workspace != null) {
         return 0.0F;
      } else if (this.storeTab()) {
         return this.market.headerHeight();
      } else {
         return this.getActiveTab() == 3 ? this.ai.headerHeight() : 0.0F;
      }
   }

   @Override
   public void renderMainHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      if (this.storeTab()) {
         this.market.renderHeader(menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle);
      } else if (this.getActiveTab() == 3) {
         this.ai.renderHeader(menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle);
      }
   }

   @Override
   public void selectTab(int activeTab) {
      if (activeTab != this.getActiveTab()) {
         this.ai.commitFields();
         if (this.settingsScript != null) {
            this.closeSettings();
         }
      }

      super.selectTab(activeTab);
   }

   public void openWorkspace(Script workspace) {
      Storage storage = host();
      if (storage != null) {
         OriginUtil.Data2 data2 = OriginUtil.method6921(workspace);
         if (data2 != null && !data2.mine() && StoreApi.method1960(data2.getUUID()).isFlag()) {
            this.toast(t("gui.market.sealed.locked"), true);
         } else {
            if (this.code == null) {
               this.code = new CodeArea(storage::method1343);
            }

            this.workspace = workspace;
            this.files = storage.method1345(workspace);
            this.moduleRows.clear();
            this.expanded.clear();
            this.filePlateReady = false;
            this.inspectorScroll = this.inspectorScrollTarget = 0.0F;
            this.consoleScroll = 0.0F;
            this.openFile(workspace.getPath2());
            this.infoPage = false;
            this.workspaceAnimation.reset(0.0F);
            this.workspaceAnimation.method2810(1.0F);
            Zenith.getInstance().getClientSounds().method1562();
         }
      }
   }

   private void closeWorkspace() {
      if (this.workspace != null) {
         this.showCode();
         if (this.code != null && this.code.isDirty()) {
            this.save();
         }

         this.workspace = null;
         this.activeRow = null;
         this.inputFocused = false;
         this.invalidate();
         Zenith.getInstance().getClientSounds().method1563();
      }
   }

   private void openFile(Path file) {
      Storage storage = host();
      if (storage != null && this.workspace != null) {
         try {
            this.code.setText(Files.isRegularFile(file) ? storage.method1306(file) : "");
            this.file = file;
            this.code.setErrorLine(Storage.method1348(this.workspace, this.file));
            this.code.setFocused(true);
            this.inputFocused = false;
         } catch (Exception e) {
            this.toast(t("gui.scripts.toast.openFailed") + " " + e.getMessage(), true);
         }
      }
   }

   private void save() {
      Storage storage = host();
      if (storage != null && this.workspace != null && this.file != null) {
         try {
            storage.method1347(this.workspace, this.file, this.code.getText());
            this.code.markSaved();
            storage.method1299(this.workspace);
            this.code.setErrorLine(Storage.method1348(this.workspace, this.file));
            this.moduleRows.clear();
            if (this.workspace.getError().isBlank()) {
               this.toast(t("gui.scripts.toast.saved"), false);
            } else {
               this.toast(this.workspace.getError(), true);
            }
         } catch (Exception e) {
            this.toast(t("gui.scripts.toast.saveFailed") + " " + e.getMessage(), true);
         }
      }
   }

   private void newFile() {
      Storage storage = host();
      if (storage != null && this.workspace != null) {
         if (this.workspace.getPath() == null) {
            this.toast(t("gui.scripts.toast.singleFile"), true);
         } else {
            try {
               for (int i = 1; i < 100; i++) {
                  Path file = storage.method1346(this.workspace, "util" + (i == 1 ? "" : i) + ".js");
                  if (!Files.exists(file)) {
                     storage.method1347(this.workspace, file, "// include(\"" + file.getFileName() + "\")\n");
                     this.files = storage.method1345(this.workspace);
                     if (this.code.isDirty()) {
                        this.save();
                     }

                     this.openFile(file);
                     return;
                  }
               }
            } catch (Exception e) {
               this.toast(e.getMessage(), true);
            }
         }
      }
   }

   private void listToast(String toast, boolean toastError) {
      if (this.workspace != null) {
         this.toast(toast, toastError);
      } else {
         this.evalLines.add((toastError ? "! " : "  ") + toast);
         this.toast(toast, toastError);
         this.invalidate();
      }
   }

   private void toast(String toast, boolean toastError) {
      this.toast = toast;
      this.toastError = toastError;
      this.toastAt = System.currentTimeMillis();
   }

   private void runScriptAction(Runnable runnable) {
      Storage storage = host();
      if (storage != null && this.workspace != null) {
         runnable.run();
         this.moduleRows.clear();
         this.code.setErrorLine(Storage.method1348(this.workspace, this.file));
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float y) {
      if (this.workspace != null) {
         Storage storage = host();
         if (storage != null && storage.method1294(this.workspace.getId()) != null) {
            this.renderWorkspace(menuDrawContext, lastClickX, lastClickY, f, f2, y);
            return;
         }

         this.workspace = null;
         this.invalidate();
      }

      this.homeHits.clear();
      long now = System.currentTimeMillis();
      if (now - this.lastCheck > 250L) {
         this.lastCheck = now;
         long signature = this.signature();
         if (signature != this.lastSignature) {
            this.lastSignature = signature;
            this.invalidate();
         }
      }

      if (this.getActiveTab() == 3 && this.ai.pollChanged()) {
         this.invalidate();
      }

      int activeTab = this.getActiveTab();
      if (this.shownTab != activeTab) {
         if (this.shownTab >= 0 && (this.shownTab == 1 || activeTab == 1)) {
            this.tabDirection = activeTab > this.shownTab ? 1.0F : -1.0F;
            this.tabSwitch.reset(0.0F);
            this.tabSwitch.method2810(1.0F);
         }

         this.shownTab = activeTab;
      }

      float f3 = this.tabSwitch.method2808(1.0F);
      float x = 14.0F * this.tabDirection * (1.0F - f3);
      menuDrawContext.pose().pushMatrix();
      menuDrawContext.pose().translate(x, 0.0F);

      try {
         if (this.configsTab()) {
            this.configs.render(menuDrawContext, lastClickX, lastClickY, f * f3, f2, y);
         } else {
            super.render(menuDrawContext, lastClickX, lastClickY, f * f3, f2, y);
         }
      } finally {
         menuDrawContext.pose().popMatrix();
      }
   }

   @Override
   public void renderPriority(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float floatValue, float f2) {
      if (this.configsTab()) {
         this.configs.renderPriority(customDrawContext, mouseX, mouseY, f, floatValue, f2);
      } else {
         super.renderPriority(customDrawContext, mouseX, mouseY, f, floatValue, f2);
      }
   }

   @Override
   public void tick() {
      if (this.configsTab()) {
         this.configs.tick();
      } else {
         super.tick();
      }
   }

   @Override
   public void renderHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, float f, float f2, float f3) {
      if (this.workspace == null) {
         super.renderHeader(menuDrawContext, lastClickX, lastClickY, previewAlpha, f, f2, f3);
      } else {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            float previewAlpha2 = previewAlpha * this.workspaceAnimation.getValue();
            Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(4.8F);
            float f4 = f2 + 11.5F;
            float floatValue = f + 8.0F;
            ZenithColor zenithColor = currentStyle.getTextTertiary().getZenithColor();
            this.headerBack = new BoundsData(floatValue - 2.0F, f4 - 7.0F, 14.0F, 14.0F);
            boolean contains = this.headerBack.contains(lastClickX, lastClickY);
            menuDrawContext.drawRoundedRect(
               this.headerBack.getFloatValue(),
               this.headerBack.getFloatValue2(),
               this.headerBack.width(),
               this.headerBack.height(),
               ProxyDotRadiusData.method8069(4.0F),
               currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha2 * (contains ? 1.0F : 0.55F))
            );
            menuDrawContext.drawText(
               font,
               "‹",
               this.headerBack.getFloatValue() + (14.0F - font.width("‹")) / 2.0F,
               f4 - font.height() / 2.0F - 0.5F,
               (contains ? currentStyle.getTextEnable() : currentStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha2)
            );
            floatValue += 17.0F;
            this.headerRoot = new BoundsData(floatValue - 2.0F, f4 - 7.0F, font.width("Scripts") + 4.0F, 14.0F);
            floatValue = crumb(menuDrawContext, font, "Scripts", floatValue, f4, this.headerRoot.contains(lastClickX, lastClickY) ? currentStyle.getTextEnable().getZenithColor() : zenithColor, previewAlpha2);
            floatValue = crumb(menuDrawContext, font, "/", floatValue, f4, zenithColor.method7294(0.6F), previewAlpha2);
            floatValue = crumb(menuDrawContext, font, this.workspace.method1403(), floatValue, f4, currentStyle.getTextEnable().getZenithColor(), previewAlpha2);
            if (this.infoPage) {
               floatValue = crumb(menuDrawContext, font, "/", floatValue, f4, zenithColor.method7294(0.6F), previewAlpha2);
               crumb(
                  menuDrawContext,
                  font,
                  t("gui.scripts.ws.about") + (this.info != null && this.info.isDirty() ? "  •" : ""),
                  floatValue,
                  f4,
                  this.info != null && this.info.isDirty() ? WARN : currentStyle.getTextSecondary().getZenithColor(),
                  previewAlpha2
               );
            } else if (this.file != null) {
               Path parent = this.workspace.getPath() == null ? this.file.getParent() : this.workspace.getPath();
               floatValue = crumb(menuDrawContext, font, "/", floatValue, f4, zenithColor.method7294(0.6F), previewAlpha2);
               String text = parent.relativize(this.file).toString().replace('\\', '/') + (this.code.isDirty() ? "  •" : "");
               crumb(menuDrawContext, font, text, floatValue, f4, this.code.isDirty() ? WARN : currentStyle.getTextSecondary().getZenithColor(), previewAlpha2);
            }

            this.headerActions.clear();
            this.headerActions.put("gui.scripts.ws.ai", () -> {
               Script var1x = this.workspace;
               this.closeWorkspace();
               AiChatStorage.method2741().method2751(var1x);
               this.selectTab(3);
               this.invalidate();
            });
            this.headerActions
               .put(
                  "gui.scripts.ws.reload",
                  () -> this.runScriptAction(
                     () -> {
                        if (this.code.isDirty()) {
                           this.save();
                        } else {
                           host().method1299(this.workspace);
                        }

                        this.toast(
                           this.workspace.getError().isBlank() ? t("gui.scripts.toast.reloaded") : this.workspace.getError(),
                           !this.workspace.getError().isBlank()
                        );
                     }
                  )
               );
            this.headerActions
               .put(
                  this.workspace.isRunning() ? "gui.scripts.ws.stop" : "gui.scripts.ws.run",
                  () -> this.runScriptAction(
                     () -> {
                        if (this.code.isDirty()) {
                           this.save();
                        }

                        host().method1298(this.workspace, !this.workspace.isRunning());
                        this.toast(
                           this.workspace.isRunning()
                              ? t("gui.scripts.toast.started")
                              : (this.workspace.getError().isBlank() ? t("gui.scripts.toast.stopped") : this.workspace.getError()),
                           !this.workspace.getError().isBlank()
                        );
                     }
                  )
               );
            if (this.infoPage) {
               this.headerActions.put(this.info != null && this.info.isDirty() ? "gui.scripts.ws.save" : "gui.scripts.ws.saved", () -> this.info().apply());
            } else {
               this.headerActions.put(this.code.isDirty() ? "gui.scripts.ws.save" : "gui.scripts.ws.saved", this::save);
            }

            this.headerButtons.clear();
            float floatValue2 = f + f3;
            var entries = new ArrayList<>(this.headerActions.entrySet());

            for (int i = entries.size() - 1; i >= 0; i--) {
               String key = (String)((Map.Entry)entries.get(i)).getKey();
               String text = t(key);
               float width = font2.width(text) + 14.0F;
               floatValue2 -= width;
               BoundsData boundsData = new BoundsData(floatValue2, f4 - 6.5F, width, 13.0F);
               boolean flag = boundsData.contains(lastClickX, lastClickY);
               boolean flag2 = key.equals("gui.scripts.ws.save");
               ZenithColor zenithColor2 = flag2
                  ? currentStyle.getPrimaryColor().getZenithColor().method7294(flag ? 1.0F : 0.85F)
                  : currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(flag ? 1.0F : 0.6F);
               menuDrawContext.drawRoundedRect(
                  boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), ProxyDotRadiusData.method8069(4.0F), zenithColor2.method7294(previewAlpha2)
               );
               menuDrawContext.drawText(
                  font2,
                  text,
                  boundsData.getFloatValue() + 7.0F,
                  f4 - font2.height() / 2.0F,
                  (flag2 ? ZenithColor.INSTANCE : currentStyle.getTextEnable().getZenithColor()).method7294(previewAlpha2)
               );
               this.headerButtons.put(key, boundsData);
               floatValue2 -= 4.0F;
            }
         }
      }
   }

   private static float crumb(MenuDrawContext menuDrawContext, Font font, String text, float floatValue, float f, ZenithColor zenithColor, float previewAlpha) {
      menuDrawContext.drawText(font, text, floatValue, f - font.height() / 2.0F, zenithColor.method7294(previewAlpha));
      return floatValue + font.width(text) + 5.0F;
   }

   @Override
   public float getButtonWidth() {
      return this.workspace != null ? 0.0F : super.getButtonWidth();
   }

   @Override
   public void renderHeaderButtons(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, float f, float y, float mainWidth, float f2) {
      if (this.workspace == null) {
         super.renderHeaderButtons(menuDrawContext, lastClickX, lastClickY, previewAlpha, f, y, mainWidth, f2);
      }
   }

   @Override
   public boolean onHeaderButtonsClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      if (this.workspace == null) {
         return super.onHeaderButtonsClicked(lastClickX, lastClickY, enum2);
      } else if ((this.headerBack == null || !this.headerBack.contains(lastClickX, lastClickY)) && (this.headerRoot == null || !this.headerRoot.contains(lastClickX, lastClickY))) {
         for (Map.Entry entry : this.headerButtons.entrySet()) {
            if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               Runnable headerAction = this.headerActions.get(entry.getKey());
               if (headerAction != null) {
                  headerAction.run();
               }

               return true;
            }
         }

         return false;
      } else {
         this.closeWorkspace();
         return true;
      }
   }

   @Override
   public void renderLeftColumn(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, float floatValue, float y, float width, float height) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Storage hostOrNull = host();
      if (currentStyle != null && this.workspace != null && hostOrNull != null) {
         if (++this.filesTick % 40 == 0) {
            long now = System.currentTimeMillis();
            if (now - this.filesAt >= 500L) {
               this.filesAt = now;
               this.files = hostOrNull.method1345(this.workspace);
            }
         }

         this.leftBounds = new BoundsData(floatValue, y, width, height);
         Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
         Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
         Font font3 = Fonts.NEW_MEDIUM.getFont(4.2F);
         float f = 10.0F;
         this.backRect = new BoundsData(floatValue + 6.0F, y + 6.0F, width - 12.0F, 14.0F);
         boolean contains = this.backRect.contains(lastClickX, lastClickY);
         if (contains) {
            menuDrawContext.drawRoundedRect(
               this.backRect.getFloatValue(),
               this.backRect.getFloatValue2(),
               this.backRect.width(),
               this.backRect.height(),
               ProxyDotRadiusData.method8069(4.0F),
               currentStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 0.4F)
            );
         }

         menuDrawContext.drawText(
            font,
            "‹  " + t("gui.scripts.ws.back"),
            floatValue + f,
            this.backRect.getFloatValue2() + (14.0F - font.height()) / 2.0F,
            (contains ? currentStyle.getTextEnable() : currentStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
         );
         float centeredTextY = y + 30.0F;
         Font font4 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
         ScriptInfoPage.drawAvatar(menuDrawContext, currentStyle, this.workspace, floatValue + f, centeredTextY - 1.0F, 20.0F, previewAlpha, -2);
         menuDrawContext.drawText(
            font4,
            font4.clip(this.workspace.method1403(), width - f * 2.0F - 25.0F),
            floatValue + f + 25.0F,
            centeredTextY,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
         );
         centeredTextY += font4.height() + 3.0F;
         boolean flag = this.workspace.getType2() == Script.Type2.FAILED;
         ZenithColor zenithColor = this.workspace.isRunning() ? OK : (flag ? ERROR : currentStyle.getTextTertiary().getZenithColor());
         menuDrawContext.drawRoundedRect(
            floatValue + f + 25.0F, centeredTextY + font2.height() / 2.0F - 1.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithColor.method7294(previewAlpha)
         );
         menuDrawContext.drawText(
            font2,
            font2.clip(
               this.stateText(hostOrNull, this.workspace) + (this.workspace.getVersion().isBlank() ? "" : "  ·  v" + this.workspace.getVersion()),
               width - f * 2.0F - 31.0F
            ),
            floatValue + f + 31.0F,
            centeredTextY,
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
         centeredTextY += font2.height() + 10.0F;
         this.infoRect = null;
         menuDrawContext.drawText(font3, t("gui.scripts.ws.files"), floatValue + f, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY += font3.height() + 5.0F;
         this.fileRects.clear();
         Path parent = this.workspace.getPath() == null ? this.workspace.getPath2().getParent() : this.workspace.getPath();
         float f2 = this.workspaceAnimation.getValue();
         float f3 = y + height - 44.0F;
         float floatValue2 = -1.0F;

         for (int i = 0; i < this.files.size(); i++) {
            float f4 = centeredTextY + i * 15.0F;
            if (f4 > f3) {
               break;
            }

            if (!this.infoPage && this.files.get(i).equals(this.file)) {
               floatValue2 = f4;
            }
         }

         if (floatValue2 >= 0.0F) {
            if (!this.filePlateReady) {
               this.filePlate.reset(floatValue2);
               this.filePlateReady = true;
            }

            float y2 = this.filePlate.method2808(floatValue2);
            menuDrawContext.drawRoundedRect(
               floatValue + 6.0F, y2, width - 12.0F, 14.0F, ProxyDotRadiusData.method8069(4.0F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.2F)
            );
            menuDrawContext.drawRoundedRect(
               floatValue + 6.0F, y2 + 3.5F, 1.5F, 7.0F, ProxyDotRadiusData.method8069(0.75F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );
         }

         int n = 0;

         for (Path file : this.files) {
            float y2 = centeredTextY + n * 15.0F;
            if (y2 > f3) {
               break;
            }

            float previewAlpha2 = previewAlpha * Mth.clamp(f2 * 3.0F - n * 0.25F, 0.0F, 1.0F);
            BoundsData boundsData = new BoundsData(floatValue + 6.0F, y2, width - 12.0F, 14.0F);
            boolean flag2 = !this.infoPage && file.equals(this.file);
            if (!flag2 && boundsData.contains(lastClickX, lastClickY)) {
               menuDrawContext.drawRoundedRect(
                  boundsData.getFloatValue(),
                  boundsData.getFloatValue2(),
                  boundsData.width(),
                  boundsData.height(),
                  ProxyDotRadiusData.method8069(4.0F),
                  currentStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha2 * 0.35F)
               );
            }

            String str = parent.relativize(file).toString().replace('\\', '/');
            boolean flag3 = Storage.method1348(this.workspace, file) > 0;
            (str.endsWith(".js") ? MenuGlyph.CODE : MenuGlyph.TEXT)
               .draw(
                  menuDrawContext,
                  boundsData.getFloatValue() + 5.0F,
                  y2 + 4.0F,
                  (flag2 ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getTextTertiary().getZenithColor()).method7294(previewAlpha2)
               );
            menuDrawContext.drawText(
               font2,
               font2.clip(str + (flag2 && this.code.isDirty() ? "  •" : ""), boundsData.width() - 22.0F),
               boundsData.getFloatValue() + 15.0F,
               y2 + (14.0F - font2.height()) / 2.0F,
               (flag3 ? ERROR : (flag2 ? currentStyle.getTextEnable().getZenithColor() : currentStyle.getTextSecondary().getZenithColor())).method7294(previewAlpha2)
            );
            this.fileRects.add(new Object[]{boundsData, file});
            n++;
         }

         this.newFileRect = null;
         if (this.workspace.getPath() != null) {
            float y2 = centeredTextY + n * 15.0F + 2.0F;
            this.newFileRect = new BoundsData(floatValue + 6.0F, y2, width - 12.0F, 14.0F);
            boolean contains2 = this.newFileRect.contains(lastClickX, lastClickY);
            menuDrawContext.drawText(
               font2,
               "+  " + t("gui.scripts.ws.newFile"),
               floatValue + 11.0F,
               y2 + (14.0F - font2.height()) / 2.0F,
               (contains2 ? currentStyle.getTextEnable() : currentStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
            );
         }

         float centeredTextY2 = y + height - 74.0F;
         if (!this.infoPage && this.code != null && centeredTextY2 > centeredTextY + n * 15.0F + 24.0F) {
            menuDrawContext.drawText(font3, t("gui.scripts.ws.stats"), floatValue + f, centeredTextY2, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY2 += font3.height() + 4.0F;
            String[] strings = new String[]{
               t("gui.scripts.ws.lines").replace("{n}", String.valueOf(this.code.lineCount())),
               t("gui.scripts.ws.caret").replace("{l}", String.valueOf(this.code.caretLine())).replace("{c}", String.valueOf(this.code.caretColumn())),
               t("gui.scripts.ws.modulesCount").replace("{n}", String.valueOf(this.workspace.getItems3().size()))
            };

            for (String name : strings) {
               menuDrawContext.drawText(font2, font2.clip(name, width - f * 2.0F), floatValue + f, centeredTextY2, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
               centeredTextY2 += font2.height() + 2.5F;
            }
         }

         float centeredTextY3 = y + height - 36.0F;
         menuDrawContext.drawRect(floatValue + f, centeredTextY3 - 6.0F, width - f * 2.0F, 1.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
         String[] strings = new String[]{
            "Ctrl+S  " + t("gui.scripts.hint.save"), "Ctrl+Space  " + t("gui.scripts.hint.complete"), "Esc  " + t("gui.scripts.hint.back")
         };

         for (String name : strings) {
            menuDrawContext.drawText(font2, font2.clip(name, width - f * 2.0F), floatValue + f, centeredTextY3, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY3 += font2.height() + 2.5F;
         }
      }
   }

   private void renderWorkspace(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float y) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         float f3 = this.workspaceAnimation.method2808(1.0F);
         float previewAlpha = f * f3;
         float f4 = (1.0F - f3) * 8.0F;
         float f5 = 380.0F + NLMenuScreen.extraWidth;
         float height = NLMenuScreen.contentBottom - y - GuiStyle.PADDING.intValue() * 2.0F;
         float y2 = y + f4;
         if (this.infoPage) {
            this.codeBounds = new BoundsData(f2 + 8.0F, y2, f5 - 16.0F, height);
            this.inspectorBounds = null;
            this.consoleBounds = null;
            this.inputBounds = null;
            this.info().render(menuDrawContext, lastClickX, lastClickY, f2 + 8.0F, y2, f5 - 16.0F, height, previewAlpha, currentStyle);
            this.renderToast(menuDrawContext, previewAlpha, currentStyle);
         } else {
            float y3 = y2 + height - 62.0F;
            this.codeBounds = new BoundsData(f2 + 8.0F, y2, f5 - 16.0F, y3 - y2 - 6.0F);
            this.code
               .render(
                  menuDrawContext,
                  this.codeBounds.getFloatValue(),
                  this.codeBounds.getFloatValue2(),
                  this.codeBounds.width(),
                  this.codeBounds.height(),
                  lastClickX,
                  lastClickY,
                  previewAlpha,
                  EDITOR_BG
               );
            this.inspectorBounds = null;
            this.consoleBounds = new BoundsData(f2 + 8.0F, y3, f5 - 16.0F, 62.0F);
            this.renderWorkspaceConsole(menuDrawContext, previewAlpha, currentStyle);
            this.renderToast(menuDrawContext, previewAlpha, currentStyle);
         }
      }
   }

   private void renderToast(MenuDrawContext menuDrawContext, float previewAlpha, ZenithStyle currentStyle) {
      if (!this.toast.isEmpty()) {
         long l = System.currentTimeMillis() - this.toastAt;
         float f = l < 200L ? (float)l / 200.0F : (l > 3600L ? Math.max(0.0F, 1.0F - (float)(l - 3600L) / 400.0F) : 1.0F);
         if (f <= 0.01F) {
            this.toast = "";
         } else {
            Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
            String clip = font.clip(this.toast, this.codeBounds.width() - 40.0F);
            float width = font.width(clip) + 22.0F;
            float floatValue = this.codeBounds.getFloatValue() + (this.codeBounds.width() - width) / 2.0F;
            float y = this.codeBounds.getFloatValue2() + this.codeBounds.height() - 22.0F + (1.0F - f) * 6.0F;
            menuDrawContext.drawRoundedRect(floatValue, y, width, 15.0F, ProxyDotRadiusData.method8069(7.5F), new ZenithColor(18, 20, 28, 240).method7294(previewAlpha * f));
            menuDrawContext.drawRoundedRect(
               floatValue + 7.0F, y + 6.0F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), (this.toastError ? ERROR : OK).method7294(previewAlpha * f)
            );
            menuDrawContext.drawText(
               font,
               clip,
               floatValue + 14.0F,
               y + (15.0F - font.height()) / 2.0F,
               (this.toastError ? ERROR.method7303(0.3F) : currentStyle.getTextEnable().getZenithColor()).method7294(previewAlpha * f)
            );
         }
      }
   }

   private void renderInspector(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, ZenithStyle currentStyle) {
      BoundsData inspectorBounds = this.inspectorBounds;
      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font3 = Fonts.NEW_REGULAR.getFont(4.4F);
      this.inspectorHits.clear();
      this.inspectorScroll = this.inspectorScroll + (this.inspectorScrollTarget - this.inspectorScroll) * 0.3F;
      MsdfRenderer.flushBatch();
      menuDrawContext.enableScissor(inspectorBounds.getFloatValue(), inspectorBounds.getFloatValue2(), inspectorBounds.getFloatValue() + inspectorBounds.width(), inspectorBounds.getFloatValue2() + inspectorBounds.height());
      float y = inspectorBounds.getFloatValue2() + 2.0F + this.inspectorScroll;
      float f = y;
      if (!this.workspace.getError().isBlank()) {
         menuDrawContext.drawText(font, t("gui.scripts.ws.error"), inspectorBounds.getFloatValue() + 2.0F, y, ERROR.method7294(previewAlpha));
         y += font.height() + 4.0F;
         List<String> strings = wrapText(this.workspace.getError(), font3, inspectorBounds.width() - 12.0F, 6);
         float height = strings.size() * (font3.height() + 2.0F) + 8.0F;
         BoundsData boundsData = new BoundsData(inspectorBounds.getFloatValue(), y, inspectorBounds.width(), height);
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), ProxyDotRadiusData.method8069(4.0F), ERROR.method7294(previewAlpha * 0.12F)
         );
         float centeredTextY = y + 4.0F;

         for (String text : strings) {
            menuDrawContext.drawText(font3, text, inspectorBounds.getFloatValue() + 6.0F, centeredTextY, ERROR.method7303(0.3F).method7294(previewAlpha));
            centeredTextY += font3.height() + 2.0F;
         }

         int n = Storage.method1348(this.workspace, this.file);
         if (n > 0) {
            this.inspectorHits.add(new Object[]{boundsData, (Runnable)() -> {
               this.code.goToLine(n);
               this.code.setFocused(true);
            }});
         }

         y += height + 8.0F;
      }

      menuDrawContext.drawText(font, t("gui.scripts.ws.modules"), inspectorBounds.getFloatValue() + 2.0F, y, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
      y += font.height() + 5.0F;
      if (this.workspace.getItems3().isEmpty()) {
         for (String text : wrapText(t("gui.scripts.ws.noModules"), font3, inspectorBounds.width() - 4.0F, 5)) {
            menuDrawContext.drawText(font3, text, inspectorBounds.getFloatValue() + 2.0F, y, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            y += font3.height() + 2.0F;
         }

         y += 6.0F;
      }

      for (Module1 key : this.workspace.getItems3().values()) {
         BoundsData boundsData = new BoundsData(inspectorBounds.getFloatValue(), y, inspectorBounds.width(), 16.0F);
         boolean contains = boundsData.contains(lastClickX, lastClickY);
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            ProxyDotRadiusData.method8069(4.0F),
            currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha * (contains ? 0.9F : 0.55F))
         );
         boolean flag = this.expanded.getOrDefault(key, true);
         menuDrawContext.drawText(
            font2,
            font2.clip(key.getName(), inspectorBounds.width() - 40.0F),
            inspectorBounds.getFloatValue() + 6.0F,
            y + (16.0F - font2.height()) / 2.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
         );
         float width = 13.0F;
         float height = 7.0F;
         float floatValue = inspectorBounds.getFloatValue() + inspectorBounds.width() - width - 5.0F;
         float y2 = y + (16.0F - height) / 2.0F;
         this.drawSwitch(menuDrawContext, "module:" + key.getId(), floatValue, y2, width, height, key.isEnabled(), previewAlpha, currentStyle);
         this.inspectorHits.add(new Object[]{new BoundsData(floatValue - 4.0F, y, width + 9.0F, 16.0F), (Runnable)key::toggle});
         this.inspectorHits.add(new Object[]{boundsData, (Runnable)() -> this.expanded.put(key, !flag)});
         y += 18.0F;
         if (flag) {
            List<InspectorRow> settingRows = this.moduleRows.computeIfAbsent(key, moduleRow -> {
               ArrayList var1x = new ArrayList();

               for (Setting var3x : moduleRow.getSettings()) {
                  var1x.add(new InspectorRow(var3x, 0));
               }

               return var1x;
            });
            InspectorRow.alignTracks(settingRows, inspectorBounds.width());

            for (InspectorRow settingRow : settingRows) {
               if (settingRow.isVisible()) {
                  float height2 = settingRow.height();
                  settingRow.render(menuDrawContext, lastClickX, lastClickY, inspectorBounds.getFloatValue(), y, inspectorBounds.width(), previewAlpha);
                  y += height2;
               }
            }

            y += 4.0F;
         }
      }

      MsdfRenderer.flushBatch();
      menuDrawContext.disableScissor();
      float f2 = y - f;
      this.inspectorScrollTarget = Mth.clamp(this.inspectorScrollTarget, Math.min(0.0F, inspectorBounds.height() - f2 - 6.0F), 0.0F);
   }

   private void renderWorkspaceConsole(MenuDrawContext menuDrawContext, float previewAlpha, ZenithStyle currentStyle) {
      BoundsData consoleBounds = this.consoleBounds;
      Storage storage = host();
      menuDrawContext.drawRoundedRect(
         consoleBounds.getFloatValue(), consoleBounds.getFloatValue2(), consoleBounds.width(), consoleBounds.height(), ProxyDotRadiusData.method8069(4.0F), EDITOR_BG.method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
      menuDrawContext.drawText(
         font, t("gui.scripts.ws.console"), consoleBounds.getFloatValue() + 6.0F, consoleBounds.getFloatValue2() + 5.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      var list = storage != null ? storage.method1349(this.workspace) : List.of();
      int n = list.size() + this.evalLines.size();
      float f = font2.height() + 2.5F;
      float f2 = consoleBounds.getFloatValue2() + 13.0F;
      float height = 13.0F;
      float f3 = consoleBounds.getFloatValue2() + consoleBounds.height() - height - 6.0F;
      int n2 = Math.max(1, (int)((f3 - f2) / f));
      this.consoleScroll = Mth.clamp(this.consoleScroll, 0.0F, (float)Math.max(0, n - n2));
      int n3 = Math.max(0, n - n2 - (int)this.consoleScroll);
      float centeredTextY = f2;

      for (int i = n3; i < Math.min(n, n3 + n2); i++) {
         String evalLine;
         String str;
         if (i < list.size()) {
            Storage.Data2 data2 = (Storage.Data2)list.get(i);
            evalLine = TIME.format(new Date(data2.time())) + "  " + data2.text();
            str = data2.getText2();
         } else {
            evalLine = this.evalLines.get(i - list.size());
            str = evalLine.startsWith("! ") ? "error" : "eval";
         }

         menuDrawContext.drawText(font2, font2.clip(evalLine, consoleBounds.width() - 12.0F), consoleBounds.getFloatValue() + 6.0F, centeredTextY, levelColor(str, currentStyle).method7294(previewAlpha));
         centeredTextY += f;
      }

      this.inputBounds = new BoundsData(consoleBounds.getFloatValue() + 4.0F, consoleBounds.getFloatValue2() + consoleBounds.height() - height - 4.0F, consoleBounds.width() - 8.0F, height);
      this.renderInput(menuDrawContext, this.inputBounds, previewAlpha, currentStyle, this.workspace.isRunning() ? t("gui.scripts.ws.inputHint") : t("gui.scripts.console.hint"));
   }

   private void renderInput(MenuDrawContext menuDrawContext, BoundsData inputBounds, float previewAlpha, ZenithStyle currentStyle, String name) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      menuDrawContext.drawRoundedRect(
         inputBounds.getFloatValue(),
         inputBounds.getFloatValue2(),
         inputBounds.width(),
         inputBounds.height(),
         ProxyDotRadiusData.method8069(3.0F),
         currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha * (this.inputFocused ? 1.0F : 0.55F))
      );
      boolean empty = this.input.isEmpty();
      String clip;
      if (empty && !this.inputFocused) {
         clip = font.clip(name, inputBounds.width() - 12.0F);
      } else {
         String string = this.input.toString();
         String str = this.inputFocused && System.currentTimeMillis() / 530L % 2L == 0L ? "▏" : "";

         while (font.width("›  " + string + str) > inputBounds.width() - 12.0F && string.length() > 1) {
            string = string.substring(1);
         }

         clip = "›  " + string + str;
      }

      menuDrawContext.drawText(
         font,
         clip,
         inputBounds.getFloatValue() + 6.0F,
         inputBounds.getFloatValue2() + (inputBounds.height() - font.height()) / 2.0F,
         (empty && !this.inputFocused ? currentStyle.getTextTertiary() : currentStyle.getTextEnable()).getZenithColor().method7294(previewAlpha)
      );
   }

   private void runInput() {
      Storage storage = host();
      String source = this.input.toString().trim();
      if (storage != null && !source.isEmpty()) {
         this.history.add(source);
         this.historyIndex = -1;
         this.input.setLength(0);
         this.evalLines.add("> " + source);
         Script workspace = this.workspace != null && this.workspace.isRunning() ? this.workspace : null;

         try {
            this.evalLines.add("  " + storage.method1341(workspace, source));
         } catch (Throwable e) {
            this.evalLines.add("! " + Storage.method1325(e));
         }

         while (this.evalLines.size() > 200) {
            this.evalLines.remove(0);
         }

         this.consoleScroll = 0.0F;
         if (this.workspace == null) {
            this.invalidate();
            this.scrollToEnd();
         }
      }
   }

   @Override
   public boolean pressFeedback() {
      return true;
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      return this.handleClick(lastClickX, lastClickY, enum2);
   }

   private boolean handleClick(double lastClickX, double lastClickY, Enum3 enum2) {
      this.lastClickX = lastClickX;
      this.lastClickY = lastClickY;
      if (this.configsTab()) {
         return this.configs.onMouseClicked(lastClickX, lastClickY, enum2);
      } else if (this.workspace == null) {
         if (this.storeTab()) {
            this.market.mouse(lastClickX, lastClickY);
            BoundsData mainHeaderBounds = this.mainHeaderBounds();
            if (mainHeaderBounds != null && mainHeaderBounds.contains(lastClickX, lastClickY) && this.market.clickHeader()) {
               return true;
            }

            if (this.market.clickRedeem(lastClickX, lastClickY)) {
               return true;
            }
         }

         if (this.getActiveTab() == 3) {
            BoundsData mainHeaderBounds = this.mainHeaderBounds();
            if (mainHeaderBounds != null && mainHeaderBounds.contains(lastClickX, lastClickY) && this.ai.clickHeader(lastClickX, lastClickY)) {
               return true;
            }

            this.ai.mouse(lastClickX, lastClickY);
            this.ai.blurAll(lastClickX, lastClickY);
         }

         if (this.storeTab() && this.settingsScript != null) {
            BoundsData asideViewportOrNull = this.asideViewportOrNull();
            if (asideViewportOrNull != null && asideViewportOrNull.contains(lastClickX, lastClickY)) {
               this.info().mouseClicked(lastClickX, lastClickY);
               return true;
            }
         }

         if (this.storeTab() && enum2 == Enum3.RIGHT) {
            BoundsData mainViewport = this.mainViewport();
            if (mainViewport == null || mainViewport.contains(lastClickX, lastClickY)) {
               for (Object[] objects : new ArrayList<>(this.homeHits)) {
                  if ("open".equals(objects[1]) && ((BoundsData)objects[0]).contains(lastClickX, lastClickY)) {
                     this.openSettings((Script)objects[2]);
                     return true;
                  }
               }
            }
         }

         if (this.storeTab() && enum2 == Enum3.LEFT) {
            BoundsData mainViewport = this.mainViewport();
            if (mainViewport == null || mainViewport.contains(lastClickX, lastClickY)) {
               for (Object[] objects : new ArrayList<>(this.homeHits)) {
                  if (((BoundsData)objects[0]).contains(lastClickX, lastClickY) && this.handleHome(objects)) {
                     return true;
                  }
               }
            }
         }

         BoundsData footerBounds = this.footerBounds();
         if (this.inputFocused && (footerBounds == null || !footerBounds.contains(lastClickX, lastClickY))) {
            this.inputFocused = false;
         }

         return super.onMouseClicked(lastClickX, lastClickY, enum2);
      } else if (this.leftBounds != null && this.leftBounds.contains(lastClickX, lastClickY)) {
         if (this.backRect != null && this.backRect.contains(lastClickX, lastClickY)) {
            this.closeWorkspace();
            return true;
         } else if (this.infoRect != null && this.infoRect.contains(lastClickX, lastClickY)) {
            if (!this.infoPage) {
               this.showInfo();
            }

            return true;
         } else if (this.newFileRect != null && this.newFileRect.contains(lastClickX, lastClickY)) {
            this.newFile();
            return true;
         } else {
            for (Object[] fileRect : this.fileRects) {
               if (((BoundsData)fileRect[0]).contains(lastClickX, lastClickY)) {
                  Path file = (Path)fileRect[1];
                  if (this.infoPage) {
                     this.showCode();
                     if (file.equals(this.file)) {
                        return true;
                     }
                  }

                  if (!file.equals(this.file)) {
                     if (this.code.isDirty()) {
                        this.save();
                     }

                     this.openFile(file);
                  }

                  return true;
               }
            }

            return true;
         }
      } else if (this.infoPage) {
         this.info().mouseClicked(lastClickX, lastClickY);
         return true;
      } else if (this.inputBounds != null && this.inputBounds.contains(lastClickX, lastClickY)) {
         this.inputFocused = true;
         this.code.setFocused(false);
         return true;
      } else {
         this.inputFocused = false;
         if (this.inspectorBounds != null && this.inspectorBounds.contains(lastClickX, lastClickY)) {
            this.code.setFocused(false);

            for (List list : this.moduleRows.values()) {
               for (Object obj : (Iterable<?>)(Object)(list)) {
                  InspectorRow activeRow = (InspectorRow)obj;
                  if (activeRow.isVisible() && activeRow.onMouseClicked(lastClickX, lastClickY, enum2)) {
                     this.activeRow = activeRow;
                     return true;
                  }
               }
            }

            for (Object[] inspectorHit : this.inspectorHits) {
               if (((BoundsData)inspectorHit[0]).contains(lastClickX, lastClickY)) {
                  ((Runnable)inspectorHit[1]).run();
                  return true;
               }
            }

            return true;
         } else if (this.codeBounds != null && this.codeBounds.contains(lastClickX, lastClickY)) {
            return this.code.mouseClicked(lastClickX, lastClickY, enum2 == Enum3.LEFT ? 0 : 1);
         } else {
            this.code.setFocused(false);
            return this.consoleBounds != null && this.consoleBounds.contains(lastClickX, lastClickY);
         }
      }
   }

   @Override
   public boolean onMouseReleased(double x, double y, Enum3 enum2) {
      if (this.configsTab()) {
         return this.configs.onMouseReleased(x, y, enum2);
      } else if (this.workspace == null) {
         return super.onMouseReleased(x, y, enum2);
      } else {
         this.code.mouseReleased();

         for (List list : this.moduleRows.values()) {
            for (Object obj : (Iterable<?>)(Object)(list)) {
               InspectorRow inspectorRow = (InspectorRow)obj;
               inspectorRow.onMouseReleased(x, y, enum2);
            }
         }

         return true;
      }
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int mouseToGlfw, double offsetX, double offsetY) {
      if (this.configsTab()) {
         return this.configs.onMouseDragged(mouseX, mouseY, mouseToGlfw, offsetX, offsetY);
      } else if (this.workspace == null) {
         return super.onMouseDragged(mouseX, mouseY, mouseToGlfw, offsetX, offsetY);
      } else {
         for (List list : this.moduleRows.values()) {
            for (Object obj : (Iterable<?>)(Object)(list)) {
               InspectorRow inspectorRow = (InspectorRow)obj;
               if (inspectorRow.onMouseDragged(mouseX, mouseY)) {
                  return true;
               }
            }
         }

         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double lastClickX, double lastClickY, double d, double verticalAmount) {
      if (this.configsTab()) {
         return this.configs.mouseScrolled(lastClickX, lastClickY, d, verticalAmount);
      } else if (this.workspace == null) {
         if (this.settingsScript != null) {
            BoundsData asideViewportOrNull = this.asideViewportOrNull();
            if (asideViewportOrNull != null && asideViewportOrNull.contains(lastClickX, lastClickY)) {
               this.info().mouseScrolled(lastClickX, lastClickY, verticalAmount);
               return true;
            }
         }

         return super.mouseScrolled(lastClickX, lastClickY, d, verticalAmount);
      } else if (this.infoPage) {
         this.info().mouseScrolled(lastClickX, lastClickY, verticalAmount);
         return true;
      } else if (this.code.mouseScrolled(lastClickX, lastClickY, verticalAmount)) {
         return true;
      } else if (this.inspectorBounds != null && this.inspectorBounds.contains(lastClickX, lastClickY)) {
         this.inspectorScrollTarget += (float)verticalAmount * 18.0F;
         return true;
      } else {
         if (this.consoleBounds != null && this.consoleBounds.contains(lastClickX, lastClickY)) {
            this.consoleScroll += (float)verticalAmount;
         }

         return true;
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.configsTab() && !this.inputFocused) {
         return this.configs.keyPressed(keyCode, keycode, modifiers);
      } else {
         boolean flag = (modifiers & 2) != 0;
         if (this.inputFocused) {
            switch (keyCode) {
               case 86:
                  if (flag) {
                     String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
                     if (clipboard != null) {
                        this.input.append(clipboard.replace("\n", " "));
                     }
                  }
                  break;
               case 256:
                  this.inputFocused = false;
                  break;
               case 257:
               case 335:
                  this.runInput();
                  break;
               case 259:
                  if (!this.input.isEmpty()) {
                     this.input.setLength(this.input.length() - 1);
                  }
                  break;
               case 264:
                  if (this.historyIndex >= 0) {
                     this.historyIndex++;
                     this.input.setLength(0);
                     if (this.historyIndex < this.history.size()) {
                        this.input.append(this.history.get(this.historyIndex));
                     } else {
                        this.historyIndex = -1;
                     }
                  }
                  break;
               case 265:
                  if (!this.history.isEmpty()) {
                     this.historyIndex = this.historyIndex < 0 ? this.history.size() - 1 : Math.max(0, this.historyIndex - 1);
                     this.input.setLength(0);
                     this.input.append(this.history.get(this.historyIndex));
                  }
            }

            return true;
         } else if (this.workspace == null) {
            if (this.getActiveTab() == 3 && this.ai.keyPressed(keyCode, modifiers)) {
               return true;
            } else if (this.storeTab() && this.market.redeem.isFocused()) {
               if (keyCode != 257 && keyCode != 335) {
                  this.market.redeem.keyPressed(keyCode, modifiers);
               } else {
                  this.market.doRedeem();
               }

               return true;
            } else {
               if (this.settingsScript != null) {
                  if (flag && keyCode == 83) {
                     this.info().apply();
                     return true;
                  }

                  if (this.info().keyPressed(keyCode, modifiers)) {
                     return true;
                  }

                  if (keyCode == 256) {
                     this.closeSettings();
                     return true;
                  }
               }

               return super.keyPressed(keyCode, keycode, modifiers);
            }
         } else if (flag && keyCode == 83) {
            if (this.infoPage) {
               this.info().apply();
            } else {
               this.save();
            }

            return true;
         } else if (this.infoPage) {
            if (this.info().keyPressed(keyCode, modifiers)) {
               return true;
            } else if (keyCode == 256) {
               this.showCode();
               return true;
            } else {
               return keyCode < 290;
            }
         } else if (this.activeRow != null && this.activeRow.isEditing()) {
            this.activeRow.keyPressed(keyCode, keycode, modifiers);
            return true;
         } else if (keyCode == 256) {
            if (this.code.hasCompletions()) {
               this.code.closeCompletions();
            } else {
               this.closeWorkspace();
            }

            return true;
         } else {
            if (!this.code.isFocused()) {
               this.code.setFocused(true);
            }

            return this.code.keyPressed(keyCode, modifiers);
         }
      }
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      if (this.configsTab() && !this.inputFocused) {
         return this.configs.charTyped(codepoint, n);
      } else if (this.inputFocused) {
         if (codepoint >= ' ' && this.input.length() < 2000) {
            this.input.append(codepoint);
         }

         return true;
      } else if (this.workspace == null) {
         if (this.getActiveTab() == 3 && this.ai.charTyped(codepoint)) {
            return true;
         } else if (this.storeTab() && this.market.redeem.charTyped(codepoint)) {
            return true;
         } else {
            return this.settingsScript != null && this.info().charTyped(codepoint) ? true : super.charTyped(codepoint, n);
         }
      } else if (this.infoPage) {
         this.info().charTyped(codepoint);
         return true;
      } else if (this.activeRow != null && this.activeRow.isEditing()) {
         this.activeRow.charTyped(codepoint, n);
         return true;
      } else {
         this.code.setFocused(true);
         return this.code.charTyped(codepoint);
      }
   }

   @Override
   public void close() {
      this.configs.close();
      this.ai.commitFields();
      if (this.workspace != null && this.code != null && this.code.isDirty()) {
         this.save();
      }

      super.close();
   }

   private static final class FileCount {
      private final long modified;
      private final int files;
      private final long countedAt;

      public long modified() {
         return this.modified;
      }

      public int files() {
         return this.files;
      }

      public long countedAt() {
         return this.countedAt;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof FileCount other)) return false;
         return java.util.Objects.equals(this.modified, other.modified) && java.util.Objects.equals(this.files, other.files) && java.util.Objects.equals(this.countedAt, other.countedAt);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(modified, files, countedAt);
      }

      @Override
      public String toString() {
         return "FileCount[" + "modified=" + this.modified + ", " + "files=" + this.files + ", " + "countedAt=" + this.countedAt + "]";
      }

      private FileCount(long modified, int files, long countedAt) {
         this.modified = modified;
         this.files = files;
         this.countedAt = countedAt;
      }
   }

   private static final class RowInfo {
      private final Script script;
      private final int files;
      private final long modified;

      public Script script() {
         return this.script;
      }

      public int files() {
         return this.files;
      }

      public long modified() {
         return this.modified;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof RowInfo other)) return false;
         return java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.files, other.files) && java.util.Objects.equals(this.modified, other.modified);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(script, files, modified);
      }

      @Override
      public String toString() {
         return "RowInfo[" + "script=" + this.script + ", " + "files=" + this.files + ", " + "modified=" + this.modified + "]";
      }

      private RowInfo(Script script, int files, long modified) {
         this.script = script;
         this.files = files;
         this.modified = modified;
      }
   }
}

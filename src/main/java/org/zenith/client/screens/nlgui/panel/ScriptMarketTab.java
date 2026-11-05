package org.zenith.client.screens.nlgui.panel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.cloud.CloudLibrary;
import org.zenith.client.screens.nlgui.cloud.CloudPreviewTextureCache;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.screens.scripts.ScriptInfoPage;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptMarketTab {
   public static final int VIEW_LOCAL = 0;
   public static final int VIEW_CATALOG = 1;
   public static final int VIEW_LIBRARY = 2;
   public static final int VIEW_MINE = 3;
   private static final String[] VISIBILITY = new String[]{"PUBLIC", "FRIENDS", "CODE", "PRIVATE"};
   private static final String[] VISIBILITY_KEYS = new String[]{
      "gui.scripts.market.vis.public", "gui.scripts.market.vis.friends", "gui.scripts.market.vis.code", "gui.scripts.market.vis.private"
   };
   public static final float ROW = 36.0F;
   public static final float GAP = 4.0F;
   public static final float CARD_PAD = 7.0F;
   public static final float AVATAR = 22.0F;
   public static final float PILL_HEIGHT = 14.0F;
   public static final float PILL_MIN = 44.0F;
   private static final float TITLE_GAP = 2.0F;
   private static final float NOTE_GAP = 2.5F;
   private static final int MAX_TAGS = 3;
   private static final float LOCAL_ROW = 24.0F;
   private static final float INSET = 8.0F;
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor LIKE = new ZenithColor(255, 107, 129);
   private static final long REFRESH_MS = 45000L;
   private final ScriptsSectionPanel panel;
   public static final int CATEGORY_ALL = 0;
   public static final int CATEGORY_SCRIPTS = 1;
   public static final int CATEGORY_CONFIGS = 2;
   private final ChipStrip views = new ChipStrip("gui.scripts.market.view.catalog", "gui.scripts.market.view.library", "gui.scripts.market.view.mine");
   private final ChipStrip categories = new ChipStrip("gui.market.category.all", "gui.market.category.scripts", "gui.market.category.configs");
   private int category;
   private final CloudLibrary taken = new CloudLibrary();
   private final ChipStrip sorts = new ChipStrip("gui.scripts.market.sort.fresh", "gui.scripts.market.sort.top", "gui.scripts.market.sort.name");
   private final ChipStrip publishVisibility = new ChipStrip(VISIBILITY_KEYS);
   private final ChipStrip entryVisibility = new ChipStrip(VISIBILITY_KEYS);
   private final List<StoreApi.Data2> catalog = new CopyOnWriteArrayList<>();
   private final List<StoreApi.Data2> library = new CopyOnWriteArrayList<>();
   private final List<StoreApi.Data2> mine = new CopyOnWriteArrayList<>();
   private final Set<UUID> busy = ConcurrentHashMap.newKeySet();
   private final Set<String> publishing = ConcurrentHashMap.newKeySet();
   private final Map<UUID, List<StoreApi.Data>> codes = new ConcurrentHashMap<>();
   private final Map<Object, Boolean> pillHover = new ConcurrentHashMap<>();
   private final Map<UUID, Long> pressedAt = new ConcurrentHashMap<>();
   public final FormField redeem = new FormField(48, false);
   private BoundsData redeemButton;
   private BoundsData rightButton;
   private static final String[] SORT_KEYS = new String[]{"gui.scripts.market.sort.fresh", "gui.scripts.market.sort.top", "gui.scripts.market.sort.name"};
   private Map<UUID, Script> installed = Map.of();
   private int view;
   private int sort;
   private int publishVis;
   private UUID selected;
   private final AtomicInteger revision = new AtomicInteger();
   private volatile long fetchedAt;
   private final AtomicInteger loading = new AtomicInteger();
   private volatile String status = "";
   private volatile boolean statusError;
   private volatile long statusAt;
   private String armed = "";
   private long armedAt;
   private final Map<UUID, ScriptMarketTab.Presence> presence = new ConcurrentHashMap<>();
   private double lastX;
   private double lastY;
   private static final String[] HERO_TITLES = new String[]{
      "gui.scripts.market.hero.local", "gui.scripts.market.hero.catalog", "gui.scripts.market.hero.library", "gui.scripts.market.hero.mine"
   };

   private float rightWidth(Font font) {
      float f = Math.max(font.width("+  " + t("gui.scripts.home.new")), font.width(t("gui.scripts.home.cancel")));

      for (String key : SORT_KEYS) {
         f = Math.max(f, font.width(t(key)));
      }

      return f + 16.0F;
   }

   public ScriptMarketTab(ScriptsSectionPanel panel) {
      this.panel = panel;
   }

   public int revision() {
      return this.revision.get() * 31
         + this.loading.get() * 7
         + this.busy.size() * 3
         + this.publishing.size()
         + this.view * 101
         + this.category * 1009
         + (this.selected == null ? 0 : this.selected.hashCode());
   }

   private static String t(String key) {
      return SectionPanel.t(key);
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private void report(String reason, boolean statusError) {
      this.status = reason == null ? "" : reason;
      this.statusError = statusError;
      this.statusAt = System.currentTimeMillis();
      this.revision.incrementAndGet();
   }

   private static String reason(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      String message = throwable2.getMessage();
      return message != null && !message.isBlank() ? message : throwable2.getClass().getSimpleName();
   }

   public void fetch(boolean flag) {
      long now = System.currentTimeMillis();
      if (flag || now - this.fetchedAt >= 45000L) {
         StoreApi storeApi = store();
         if (storeApi != null) {
            this.fetchedAt = now;
            this.loading.addAndGet(6);

            for (String kind : List.of("script", "config")) {
               storeApi.method1969("PUBLIC", kind).whenComplete((var2x, throwable) -> this.accept(this.catalog, kind, (List<StoreApi.Data2>)var2x, throwable));
               storeApi.method1969("LIBRARY", kind).whenComplete((var2x, throwable) -> this.accept(this.library, kind, (List<StoreApi.Data2>)var2x, throwable));
               storeApi.method1968(kind).whenComplete((var2x, throwable) -> this.accept(this.mine, kind, (List<StoreApi.Data2>)var2x, throwable));
            }
         }
      }
   }

   private void accept(List<StoreApi.Data2> catalog, String kind, List<StoreApi.Data2> data2s, Throwable throwable) {
      this.loading.updateAndGet(n -> Math.max(0, n - 1));
      if (throwable != null) {
         this.report(t("gui.scripts.market.status.offline") + " " + reason(throwable), true);
      } else {
         ArrayList arrayList = new ArrayList();

         for (StoreApi.Data2 entry : data2s) {
            if (isConfig(entry) || OriginUtil.method6918(entry)) {
               arrayList.add(entry);
            }
         }

         synchronized (catalog) {
            catalog.removeIf(var1x -> kind.equalsIgnoreCase(var1x.kind()));
            catalog.addAll(arrayList);
         }

         this.revision.incrementAndGet();
      }
   }

   public static boolean isConfig(StoreApi.Data2 entry) {
      return "config".equalsIgnoreCase(entry.kind());
   }

   private boolean inCategory(StoreApi.Data2 entry) {
      return switch (this.category) {
         case 1 -> !isConfig(entry);
         case 2 -> isConfig(entry);
         default -> true;
      };
   }

   private static File configFile(StoreApi.Data2 entry) {
      return CloudConfigsSectionPanel.targetFile(entry);
   }

   private boolean configPresent(StoreApi.Data2 entry) {
      return configFile(entry).isFile();
   }

   private boolean configPresentCached(StoreApi.Data2 entry) {
      long now = System.currentTimeMillis();
      ScriptMarketTab.Presence presence = this.presence.get(entry.getUUID());
      if (presence != null && now - presence.checkedAt() < 1000L && Objects.equals(presence.name(), entry.name())) {
         return presence.present();
      } else {
         boolean configPresent = this.configPresent(entry);
         this.presence.put(entry.getUUID(), new ScriptMarketTab.Presence(entry.name(), configPresent, now));
         return configPresent;
      }
   }

   public void syncMode(boolean flag) {
      if (flag && this.view == 0) {
         this.view = 1;
         this.selected = null;
      } else if (!flag && this.view != 0) {
         this.view = 0;
         this.selected = null;
      }
   }

   public void openCategory(int n) {
      this.category = Math.max(0, Math.min(2, n));
      if (this.view == 0) {
         this.view = 1;
      }

      this.selected = null;
      this.revision.incrementAndGet();
   }

   private void replace(StoreApi.Data2 data2) {
      for (List list : List.of(this.catalog, this.library, this.mine)) {
         for (int i = 0; i < list.size(); i++) {
            if (((StoreApi.Data2)list.get(i)).getUUID().equals(data2.getUUID())) {
               list.set(i, data2);
            }
         }
      }

      this.revision.incrementAndGet();
   }

   private StoreApi.Data2 entry(UUID uuid) {
      if (uuid == null) {
         return null;
      } else {
         for (List list : List.of(this.mine, this.library, this.catalog)) {
            for (Object obj : (Iterable<?>)(Object)(list)) {
               StoreApi.Data2 data2 = (StoreApi.Data2)obj;
               if (data2.getUUID().equals(uuid)) {
                  return data2;
               }
            }
         }

         return null;
      }
   }

   private boolean armed(String armed) {
      if (this.armed.equals(armed) && System.currentTimeMillis() - this.armedAt < 3000L) {
         this.armed = "";
         return true;
      } else {
         this.armed = armed;
         this.armedAt = System.currentTimeMillis();
         this.report(t("gui.scripts.market.status.confirm"), false);
         return false;
      }
   }

   public float headerHeight() {
      float f = 11.0F + GuiStyle.PADDING.intValue() * 2.0F;
      return this.view == 0 ? f : f + 11.0F + GuiStyle.PADDING.intValue();
   }

   public void renderHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float y = f2 + GuiStyle.PADDING.intValue();
      Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
      float rightWidth = this.rightWidth(font);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(5.5F);
      this.rightButton = new BoundsData(f + f3 - 8.0F - rightWidth, y, rightWidth, 11.0F);
      float hot = this.panel.hot("hdr:right", this.rightButton.contains(lastClickX, lastClickY));
      if (this.view == 0) {
         boolean creatorOpen = this.panel.creatorOpen();
         String text = creatorOpen ? t("gui.scripts.home.cancel") : "+  " + t("gui.scripts.home.new");
         menuDrawContext.drawRoundedRect(
            this.rightButton.getFloatValue(),
            y,
            rightWidth,
            11.0F,
            headerRadius,
            (creatorOpen ? zenithStyle.getDisableActiveBg().getZenithColor().method7294(0.6F + 0.4F * hot) : zenithColor.method7292(0.18F + 0.16F * hot)).method7294(previewAlpha)
         );
         menuDrawContext.drawText(
            font,
            text,
            this.rightButton.getFloatValue() + (rightWidth - font.width(text)) / 2.0F,
            y + (11.0F - font.height()) / 2.0F,
            (creatorOpen ? zenithStyle.getTextSecondary().getZenithColor() : zenithColor.method7295(ZenithColor.INSTANCE, 0.25F + 0.4F * hot)).method7294(previewAlpha)
         );
         if (this.rightButton.contains(lastClickX, lastClickY)) {
            HoverInfo.request(t("gui.scripts.home.new"), t("gui.scripts.home.new.desc"));
         }
      } else {
         String text = t(SORT_KEYS[this.sort]);
         menuDrawContext.drawRoundedRect(
            this.rightButton.getFloatValue(), y, rightWidth, 11.0F, headerRadius, zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (0.55F + 0.45F * hot))
         );
         menuDrawContext.drawText(
            font,
            text,
            this.rightButton.getFloatValue() + (rightWidth - font.width(text)) / 2.0F,
            y + (11.0F - font.height()) / 2.0F,
            zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), 0.4F + 0.6F * hot).method7294(previewAlpha)
         );
         if (this.rightButton.contains(lastClickX, lastClickY)) {
            HoverInfo.request(t("gui.scripts.market.sort"), t("gui.scripts.market.sort.desc"));
         }
      }

      if (this.view == 0) {
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
         String text = t("gui.scripts.home.title").replace("{n}", String.valueOf(this.installed.size()));
         menuDrawContext.drawText(font2, text, f + 8.0F, y + (11.0F - font2.height()) / 2.0F, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      } else {
         this.views.render(menuDrawContext, lastClickX, lastClickY, f + 8.0F, y, f3 - 16.0F - rightWidth - 8.0F, this.view - 1, previewAlpha, zenithStyle);
         this.categories.render(menuDrawContext, lastClickX, lastClickY, f + 8.0F, y + 11.0F + GuiStyle.PADDING.intValue(), f3 - 16.0F, this.category, previewAlpha, zenithStyle);
         if (this.categories.hovered() >= 0) {
            HoverInfo.request(t("gui.market.category"), t("gui.market.category.desc"));
         }
      }

      if (this.loading.get() > 0 && this.view != 0) {
         float f4 = (float)(System.currentTimeMillis() % 900L) / 900.0F;
         menuDrawContext.drawRoundedRect(
            f + 8.0F + (f3 - 16.0F - 30.0F) * f4,
            f2 + this.headerHeight() - 1.5F,
            30.0F,
            1.0F,
            ProxyDotRadiusData.method8069(0.5F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.8F)
         );
      }

      if (this.view != 0 && this.views.hovered() >= 0) {
         HoverInfo.request(t("gui.scripts.market.views"), t("gui.scripts.market.views.desc"));
      }
   }

   public boolean clickHeader() {
      if (this.view != 0) {
         int hovered = this.categories.hovered();
         if (hovered >= 0) {
            if (hovered != this.category) {
               this.category = hovered;
               this.selected = null;
               this.panel.resetScroll();
               this.panel.replayEntrance();
               this.panel.invalidate();
            }

            return true;
         }
      }

      int view = this.view == 0 ? -1 : this.views.hovered() + 1;
      if (view > 0) {
         if (view != this.view) {
            this.view = view;
            this.selected = null;
            this.panel.closeSettingsIfOpen();
            this.panel.resetScroll();
            this.panel.resetAsideScroll();
            this.panel.replayEntrance();
            this.panel.invalidate();
         }

         return true;
      } else if (this.rightButton != null && this.rightButton.contains(this.lastX, this.lastY)) {
         if (this.view == 0) {
            this.panel.toggleCreator();
         } else {
            this.sort = (this.sort + 1) % SORT_KEYS.length;
            this.panel.replayEntrance();
            this.panel.invalidate();
         }

         return true;
      } else {
         return false;
      }
   }

   public void showLocal() {
      if (this.view != 0) {
         this.view = 0;
         this.selected = null;
         this.panel.invalidate();
      }
   }

   public void mouse(double lastX, double lastY) {
      this.lastX = lastX;
      this.lastY = lastY;
   }

   public void build(Storage hostOrNull) {
      this.fetch(false);
      this.installed = OriginUtil.method6925(hostOrNull);
      this.hero(hostOrNull);
      if (this.view == 0) {
         this.selected = null;
         this.panel.buildLocal(hostOrNull);
      } else {
         switch (this.view) {
            case 2:
               this.entries(hostOrNull, this.arrange(this.library), "gui.scripts.market.empty.library", "gui.scripts.market.empty.library.desc");
               break;
            case 3:
               this.entries(hostOrNull, this.arrange(this.mine), "gui.scripts.market.empty.mine", "gui.scripts.market.empty.mine.desc");
               break;
            default:
               this.entries(hostOrNull, this.arrange(this.catalog), "gui.scripts.market.empty.catalog", "gui.scripts.market.empty.catalog.desc");
         }

         StoreApi.Data2 entry = this.entry(this.selected);
         if (entry != null) {
            this.entryAside(hostOrNull, entry);
         } else {
            this.selected = null;
            this.overviewAside(hostOrNull);
         }
      }
   }

   private UUID idOf(Script script) {
      for (Entry entry : this.installed.entrySet()) {
         if (entry.getValue() == script || ((Script)entry.getValue()).getId().equals(script.getId())) {
            return (UUID)entry.getKey();
         }
      }

      return null;
   }

   public StoreApi.Data2 updateFor(Storage storage, Script script) {
      UUID idOf = this.idOf(script);
      StoreApi.Data2 entry = this.entry(idOf);
      return entry != null && !this.busy.contains(idOf) && OriginUtil.method6927(storage, entry) ? entry : null;
   }

   public boolean updating(Storage storage, Script script) {
      UUID idOf = this.idOf(script);
      return idOf != null && this.busy.contains(idOf);
   }

   public int updatesCount(Storage storage) {
      int n = 0;

      for (Script script : storage.method1293()) {
         if (this.updateFor(storage, script) != null) {
            n++;
         }
      }

      return n;
   }

   public void update(Storage storage, Script script) {
      StoreApi.Data2 entry = this.updateFor(storage, script);
      if (entry != null) {
         this.install(storage, entry);
      }
   }

   public void updateAll(Storage storage) {
      for (Script script : new ArrayList<>(storage.method1293())) {
         this.update(storage, script);
      }
   }

   private List<StoreApi.Data2> arrange(List<StoreApi.Data2> library) {
      String str = this.panel.searchQuery().toLowerCase(Locale.ROOT).strip();
      ArrayList<StoreApi.Data2> data2s = new ArrayList<>();

      for (StoreApi.Data2 entry : library) {
         if (this.inCategory(entry)
            && (
               str.isEmpty()
                  || entry.name().toLowerCase(Locale.ROOT).contains(str)
                  || entry.description().toLowerCase(Locale.ROOT).contains(str)
                  || entry.getText8().toLowerCase(Locale.ROOT).contains(str)
                  || String.join(",", OriginUtil.method6916(entry).getItems()).toLowerCase(Locale.ROOT).contains(str)
            )) {
            data2s.add(entry);
         }
      }
      Comparator<StoreApi.Data2> comparator = switch (this.sort) {
         case 1 -> Comparator.comparingInt(StoreApi.Data2::getIntValue)
            .reversed()
            .thenComparing(Comparator.comparingLong(StoreApi.Data2::getLongValue4).reversed());
         case 2 -> Comparator.comparing(data2 -> data2.name().toLowerCase(Locale.ROOT));
         default -> Comparator.comparingLong(StoreApi.Data2::getLongValue4).reversed();
      };
      data2s.sort(comparator);
      return data2s;
   }

   private String[][] heroStats(Storage storage) {
      if (this.view == 0) {
         var scripts = storage.method1293();
         long count = scripts.stream().filter(Script::isRunning).count();
         int n = scripts.stream().mapToInt(script -> script.getItems3().size()).sum();
         return new String[][]{
            {count + "/" + scripts.size(), t("gui.scripts.market.hero.running")},
            {String.valueOf(n), t("gui.scripts.market.hero.modules")},
            {String.valueOf(this.updatesCount(storage)), t("gui.scripts.market.hero.updates")}
         };
      } else {
         int n = 0;
         int n2 = 0;
         HashSet hashSet = new HashSet();

         for (StoreApi.Data2 entry : this.catalog) {
            if (this.inCategory(entry)) {
               n2++;
               n += entry.getIntValue();
               hashSet.add(entry.getLongValue5());
            }
         }

         return new String[][]{
            {
                  String.valueOf(n2),
                  t(this.category == 2 ? "gui.market.hero.configs" : (this.category == 1 ? "gui.scripts.market.hero.scripts" : "gui.market.hero.items"))
            },
            {String.valueOf(hashSet.size()), t("gui.scripts.market.hero.authors")},
            {String.valueOf(n), t("gui.scripts.market.hero.likes")}
         };
      }
   }

   private String heroNote(Storage storage) {
      if (this.view != 0) {
         return t(HERO_TITLES[this.view] + ".desc");
      } else {
         var scripts = storage.method1293();
         long count = scripts.stream().filter(script -> script.getType2() == Script.Type2.FAILED).count();
         return count > 0L ? t("gui.scripts.home.failed").replace("{n}", String.valueOf(count)) : t(HERO_TITLES[this.view] + ".desc");
      }
   }

   private void hero(Storage hostOrNull) {
      this.panel
         .custom(
            50.0F,
            (menuDrawContext, n, n2, f, f2, f3, previewAlpha, currentStyle) -> {
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 2.0F;
               float height = 43.0F;
               ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
               ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.9F);
               menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, ScriptsSectionPanel.cardFill(currentStyle, 0.2F).method7294(previewAlpha));
               ScriptsSectionPanel.flowGlow(menuDrawContext, floatValue, y, width, height, headerRadius, zenithColor, 0.45F, previewAlpha);
               menuDrawContext.drawRoundedBorder(floatValue, y, width, height, 0.2F, headerRadius, zenithColor.method7292(0.28F).method7294(previewAlpha));
               Font font = Fonts.NEW_SEMIBOLD.getFont(6.4F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
               Font font3 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
               Font font4 = Fonts.NEW_MEDIUM.getFont(3.8F);
               menuDrawContext.drawText(font, t(HERO_TITLES[this.view]), floatValue + 9.0F, y + 8.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               String heroNote = this.heroNote(hostOrNull);
               boolean flag = this.view == 0 && hostOrNull.method1293().stream().anyMatch(script -> script.getType2() == Script.Type2.FAILED);
               String[][] heroStats = this.heroStats(hostOrNull);
               float f4 = 0.0F;

               for (String[] heroStat : heroStats) {
                  f4 += Math.max(font3.width(heroStat[0]), font4.width(heroStat[1])) + 12.0F;
               }

               List<String> wrapText = SectionPanel.wrapText(heroNote, font2, width - 18.0F - f4, 2);
               float centeredTextY = y + 11.0F + font.height();

               for (String text : wrapText) {
                  menuDrawContext.drawText(
                     font2, text, floatValue + 9.0F, centeredTextY, (flag ? new ZenithColor(247, 206, 59) : currentStyle.getTextTertiary().getZenithColor()).method7294(previewAlpha)
                  );
                  centeredTextY += font2.height() + 1.5F;
               }

               float f5 = floatValue + width - 9.0F;

               for (int i = heroStats.length - 1; i >= 0; i--) {
                  float f6 = Math.max(font3.width(heroStats[i][0]), font4.width(heroStats[i][1]));
                  f5 -= f6;
                  menuDrawContext.drawText(
                     font3,
                     heroStats[i][0],
                     f5 + (f6 - font3.width(heroStats[i][0])) / 2.0F,
                     y + 10.0F,
                     currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
                  );
                  menuDrawContext.drawText(
                     font4,
                     heroStats[i][1],
                     f5 + (f6 - font4.width(heroStats[i][1])) / 2.0F,
                     y + 13.0F + font3.height(),
                     currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
                  );
                  f5 -= 12.0F;
               }
            }
         );
   }

   private void skeleton(int n) {
      for (int i = 0; i < n; i++) {
         int n2 = i;
         this.panel
            .custom(
               40.0F,
               this.panel
                  .entering(
                     i + 1,
                     (var1x, var2x, var3x, f, y, f2, previewAlpha, currentStyle) -> {
                        BoundsData boundsData = new BoundsData(f + 8.0F, y, f2 - 16.0F, 36.0F);
                        ScriptsSectionPanel.card(var1x, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.0F, previewAlpha);
                        float f3 = (float)((System.currentTimeMillis() + n2 * 140L) % 1400L) / 1400.0F;
                        float f4 = 0.5F + 0.5F * (float)Math.sin(f3 * Math.PI * 2.0);
                        ZenithColor zenithColor = currentStyle.getDisableActiveBg().getZenithColor().method7294(0.5F + 0.5F * f4);
                        var1x.drawRoundedRect(
                           boundsData.getFloatValue() + 7.0F,
                           boundsData.getFloatValue2() + 7.0F,
                           22.0F,
                           22.0F,
                           ProxyDotRadiusData.method8069(6.6000004F),
                           zenithColor.method7294(previewAlpha)
                        );
                        float floatValue = boundsData.getFloatValue() + 7.0F + 22.0F + 7.0F;
                        float f5 = boundsData.getFloatValue2() + 18.0F;
                        var1x.drawRoundedRect(floatValue, f5 - 9.0F, boundsData.width() * 0.3F, 5.0F, ProxyDotRadiusData.method8069(2.5F), zenithColor.method7294(previewAlpha));
                        var1x.drawRoundedRect(
                           floatValue, f5 - 1.5F, boundsData.width() * 0.48F, 4.0F, ProxyDotRadiusData.method8069(2.0F), zenithColor.method7294(previewAlpha * 0.7F)
                        );
                        var1x.drawRoundedRect(
                           floatValue, f5 + 5.5F, boundsData.width() * 0.24F, 4.0F, ProxyDotRadiusData.method8069(2.0F), zenithColor.method7294(previewAlpha * 0.5F)
                        );
                        var1x.drawRoundedRect(
                           boundsData.getFloatValue() + boundsData.width() - 7.0F - 44.0F,
                           f5 - 7.0F,
                           44.0F,
                           14.0F,
                           ProxyDotRadiusData.method8069(7.0F),
                           zenithColor.method7294(previewAlpha * 0.6F)
                        );
                     }
                  )
            );
      }
   }

   private void empty(String str, String key) {
      if (this.loading.get() > 0) {
         this.skeleton(4);
      } else {
         this.panel.custom(76.0F, (menuDrawContext, n, n2, f, f2, f3, previewAlpha, zenithStyle) -> {
            Font font = Fonts.NEW_SEMIBOLD.getFont(5.6F);
            Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
            float centeredTextY = f2 + 16.0F;
            String text = t(this.loading.get() > 0 ? "gui.scripts.market.loading" : str);
            menuDrawContext.drawText(font, text, f + (f3 - font.width(text)) / 2.0F, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
            List<String> wrapText = SectionPanel.wrapText(t(key), font2, f3 - 48.0F, 3);
            float centeredTextY2 = centeredTextY + font.height() + 6.0F;

            for (String text2 : wrapText) {
               menuDrawContext.drawText(font2, text2, f + (f3 - font2.width(text2)) / 2.0F, centeredTextY2, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               centeredTextY2 += font2.height() + 2.0F;
            }
         });
      }
   }

   private void entries(Storage hostOrNull, List<StoreApi.Data2> arrange, String str, String key) {
      if (arrange.isEmpty()) {
         this.empty(str, key);
      } else {
         int n = 1;

         for (StoreApi.Data2 data2 : arrange) {
            UUID uuid = data2.getUUID();
            this.panel.custom(40.0F, this.panel.entering(n++, (var3x, var4x, var5x, f, var7x, var8x, previewAlpha, zenithStyle) -> {
               StoreApi.Data2 entry = this.entry(uuid);
               if (entry != null) {
                  this.renderEntry(var3x, var4x, var5x, f, var7x, var8x, previewAlpha, zenithStyle, hostOrNull, entry);
               }
            }), () -> {
               StoreApi.Data2 var3x = this.entry(uuid);
               if (var3x != null) {
                  if (Boolean.TRUE.equals(this.pillHover.get(uuid))) {
                     this.pressedAt.put(uuid, System.currentTimeMillis());
                     this.primary(hostOrNull, var3x);
                  } else {
                     this.selected = uuid.equals(this.selected) ? null : uuid;
                     if (this.selected != null && var3x.mine()) {
                        this.loadCodes(var3x);
                     }

                     this.panel.invalidate();
                  }
               }
            }, () -> uuid.equals(this.selected), () -> {
               this.selected = uuid;
               this.panel.invalidate();
            });
         }
      }
   }

   private boolean needsPurchase(StoreApi.Data2 entry) {
      if (!entry.mine() && StoreApi.method1960(entry.getUUID()).method2009()) {
         for (StoreApi.Data2 data2 : this.library) {
            if (data2.getUUID().equals(entry.getUUID())) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void buy(StoreApi.Data2 entry) {
      com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create(RegionUtil.method2038().get(0) + "/market/" + entry.getUUID()));
      this.report(t("gui.market.buy.opened"), false);
   }

   private String marketLabel(StoreApi.Data2 entry) {
      StoreApi.Data5 data5 = StoreApi.method1960(entry.getUUID());
      if (data5.pending()) {
         return t("gui.market.status.pending");
      } else if (data5.method2008()) {
         return t("gui.market.status.rejected");
      } else {
         return data5.method2007()
            ? t(data5.method2009() ? "gui.market.tag.paid" : "gui.market.tag.free").replace("{p}", String.valueOf(data5.getIntValue()))
            : t(VISIBILITY_KEYS[visibilityIndex(entry.getText7())]);
      }
   }

   private String pillText(StoreApi.Data2 entry, boolean flag, boolean flag2) {
      if (this.busy.contains(entry.getUUID())) {
         return t("gui.scripts.market.busy");
      } else if (this.needsPurchase(entry)) {
         return t("gui.market.buy").replace("{p}", String.valueOf(StoreApi.method1960(entry.getUUID()).getIntValue()));
      } else if (isConfig(entry)) {
         return t(flag ? "gui.market.config.redownload" : "gui.market.config.download");
      } else {
         Script script = this.installed.get(entry.getUUID());
         if (script == null) {
            return t(entry.mine() ? "gui.scripts.market.download" : "gui.scripts.market.install");
         } else {
            return flag2 ? t("gui.scripts.market.update") : t("gui.scripts.market.open");
         }
      }
   }

   private void primary(Storage hostOrNull, StoreApi.Data2 entry) {
      if (!this.busy.contains(entry.getUUID())) {
         if (this.needsPurchase(entry)) {
            this.buy(entry);
         } else if (isConfig(entry)) {
            this.downloadConfig(entry);
         } else {
            Script workspace = this.installed.get(entry.getUUID());
            if (workspace != null && !OriginUtil.method6927(hostOrNull, entry)) {
               this.panel.openWorkspace(workspace);
            } else {
               this.install(hostOrNull, entry);
            }
         }
      }
   }

   private void renderEntry(
      MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float y, float f2, float previewAlpha, ZenithStyle currentStyle, Storage hostOrNull, StoreApi.Data2 entry
   ) {
      BoundsData boundsData = cardRect(f, y, f2);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      boolean flag = entry.getUUID().equals(this.selected);
      float hot = this.panel.hot("m:" + entry.getUUID(), contains);
      float hot2 = this.panel.hot("msel:" + entry.getUUID(), flag);
      float press = Smooth.press(this.pressedAt.getOrDefault(entry.getUUID(), 0L), 360L);
      OriginUtil.Data3 data3 = OriginUtil.method6916(entry);
      drawCard(menuDrawContext, currentStyle, boundsData, hot, hot2, press, tint(entry, data3), previewAlpha);
      this.avatar(menuDrawContext, entry, data3, boundsData.getFloatValue() + 7.0F, cardAvatarY(boundsData), 22.0F, previewAlpha);
      boolean config = isConfig(entry);
      boolean flag2 = config && this.configPresentCached(entry);
      boolean flag3 = !config && this.installed.containsKey(entry.getUUID()) && OriginUtil.method6927(hostOrNull, entry);
      String pillText = this.pillText(entry, flag2, flag3);
      BoundsData pillRect2 = pillRect(boundsData, boundsData.getFloatValue() + boundsData.width() - 7.0F, pillText);
      boolean contains2 = pillRect2.contains(lastClickX, lastClickY);
      this.pillHover.put(entry.getUUID(), contains2);
      float cardTextX2 = cardTextX(boundsData);
      float f3 = pillRect2.getFloatValue() - 7.0F;
      ScriptMarketTab.CardLines cardLines2 = cardLines(boundsData);
      drawTitleLine(
         menuDrawContext,
         currentStyle,
         entry.name(),
         data3.version().isBlank() ? "" : "v" + data3.version(),
         entry.getIntValue() <= 0 && !entry.isFlag3() ? "" : "♥ " + entry.getIntValue(),
         entry.isFlag3() ? LIKE : null,
         cardTextX2,
         f3,
         cardLines2.title(),
         previewAlpha
      );
      drawDescription(menuDrawContext, currentStyle, entry.description(), null, cardTextX2, f3, cardLines2.note(), previewAlpha);
      float drawChip = drawTags(menuDrawContext, currentStyle, data3.getItems(), cardTextX2, cardTextX2, f3, cardLines2.meta(), previewAlpha);
      ArrayList elements = new ArrayList();
      if (this.category == 0) {
         elements.add(t(config ? "gui.market.kind.config" : "gui.market.kind.script"));
      }

      elements.add(entry.getText8().isBlank() ? "?" : entry.getText8());
      elements.add(size(entry.getLongValue()));
      String ago = ScriptsSectionPanel.ago(entry.getLongValue4());
      if (!ago.isEmpty()) {
         elements.add(ago);
      }

      if (entry.mine()) {
         elements.add(this.marketLabel(entry));
      } else if (StoreApi.method1960(entry.getUUID()).method2009()) {
         elements.add(StoreApi.method1960(entry.getUUID()).getIntValue() + " ₽");
      }

      drawMeta(menuDrawContext, String.join("  ·  ", elements), currentStyle.getTextTertiary().getZenithColor(), drawChip, cardTextX2, f3, cardLines2.meta(), previewAlpha);
      boolean containsKey = config ? flag2 : this.installed.containsKey(entry.getUUID());
      boolean flag4 = containsKey && !config && flag3;
      drawPill(menuDrawContext, currentStyle, pillRect2, pillText, containsKey && !flag4, this.panel.hot("mp:" + entry.getUUID(), contains2), this.busy.contains(entry.getUUID()), previewAlpha);
      if (contains && !contains2) {
         HoverInfo.request(entry.name(), t("gui.scripts.market.row.hint"));
      }
   }

   public static Font cardTitleFont() {
      return Fonts.NEW_MEDIUM.getFont(5.0F);
   }

   public static Font cardNoteFont() {
      return Fonts.NEW_REGULAR.getFont(4.3F);
   }

   public static Font cardMetaFont() {
      return Fonts.NEW_REGULAR.getFont(4.1F);
   }

   public static Font cardTagFont() {
      return Fonts.NEW_MEDIUM.getFont(3.8F);
   }

   public static Font cardButtonFont() {
      return Fonts.NEW_MEDIUM.getFont(4.4F);
   }

   public static BoundsData cardRect(float f, float y, float f2) {
      return new BoundsData(f + 8.0F, y, f2 - 16.0F, 36.0F);
   }

   public static float cardTextX(BoundsData boundsData) {
      return boundsData.getFloatValue() + 7.0F + 22.0F + 7.0F;
   }

   public static float cardAvatarY(BoundsData boundsData) {
      return boundsData.getFloatValue2() + (boundsData.height() - 22.0F) / 2.0F;
   }

   public static ScriptMarketTab.CardLines cardLines(BoundsData boundsData) {
      Font font = cardTitleFont();
      Font cardNoteFont2 = cardNoteFont();
      float f = font.height() + 2.0F + cardNoteFont2.height() + 2.5F + cardMetaFont().height();
      float title = boundsData.getFloatValue2() + (boundsData.height() - f) / 2.0F;
      float note = title + font.height() + 2.0F;
      return new ScriptMarketTab.CardLines(title, note, note + cardNoteFont2.height() + 2.5F);
   }

   public static void drawCard(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, BoundsData boundsData, float hot, float f, float press, ZenithColor letterColor, float previewAlpha) {
      ScriptsSectionPanel.card(menuDrawContext, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), Math.max(hot, f * 0.6F), previewAlpha);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      if (hot > 0.01F) {
         ZenithColor clientColor = letterColor.method7292(0.09F * hot * previewAlpha);
         ZenithColor zenithColor3 = letterColor.method7292(0.0F);
         menuDrawContext.drawRoundedRect(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, GlowColor.method8096(clientColor, clientColor, zenithColor3, zenithColor3));
      }

      if (f > 0.01F) {
         menuDrawContext.drawRoundedBorder(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.25F, headerRadius, zenithColor.method7294(previewAlpha * 0.7F * f));
      }

      if (press > 0.01F) {
         menuDrawContext.drawRoundedRect(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, zenithColor.method7292(0.16F * press).method7294(previewAlpha));
      }
   }

   public static float drawTitleLine(
      MenuDrawContext menuDrawContext, ZenithStyle currentStyle, String name, String version, String likes, ZenithColor zenithColor, float cardTextX, float f, float title, float previewAlpha
   ) {
      Font font = cardTitleFont();
      Font cardNoteFont2 = cardNoteFont();
      float centeredTextY = title + font.height() - cardNoteFont2.height();
      float f2 = (version.isEmpty() ? 0.0F : cardNoteFont2.width(version) + 4.0F) + (likes.isEmpty() ? 0.0F : cardNoteFont2.width(likes) + 5.0F);
      String clip = font.clip(name, Math.max(20.0F, f - cardTextX - f2));
      menuDrawContext.drawText(font, clip, cardTextX, title, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      float f3 = cardTextX + font.width(clip);
      if (!version.isEmpty() && f3 + 4.0F + cardNoteFont2.width(version) <= f) {
         menuDrawContext.drawText(cardNoteFont2, version, f3 + 4.0F, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         f3 += 4.0F + cardNoteFont2.width(version);
      }

      if (!likes.isEmpty() && f3 + 5.0F + cardNoteFont2.width(likes) <= f) {
         menuDrawContext.drawText(cardNoteFont2, likes, f3 + 5.0F, centeredTextY, (zenithColor == null ? currentStyle.getTextTertiary().getZenithColor() : zenithColor).method7294(previewAlpha));
         f3 += 5.0F + cardNoteFont2.width(likes);
      }

      return f3;
   }

   public static void drawDescription(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, String description, ZenithColor zenithColor, float cardTextX, float f, float note, float previewAlpha) {
      Font font = cardNoteFont();
      String str = description == null ? "" : description.replace('\r', ' ').replace('\n', ' ').strip();
      boolean empty = str.isEmpty();
      ZenithColor zenithColor2 = empty ? currentStyle.getTextTertiary().getZenithColor().method7294(0.7F) : (zenithColor == null ? currentStyle.getTextSecondary().getZenithColor() : zenithColor);
      menuDrawContext.drawText(font, font.clip(empty ? t("gui.scripts.info.noDescription") : str, f - cardTextX), cardTextX, note, zenithColor2.method7294(previewAlpha));
   }

   public static float drawChip(MenuDrawContext menuDrawContext, String text, ZenithColor zenithColor, ZenithColor zenithColor2, float cardTextX, float meta, float previewAlpha) {
      Font font = cardTagFont();
      float height = font.height() + 2.6F;
      float y = meta + (cardMetaFont().height() - height) / 2.0F;
      float width = font.width(text) + 6.0F;
      menuDrawContext.drawRoundedRect(cardTextX, y, width, height, ProxyDotRadiusData.method8069(height / 2.0F), zenithColor.method7294(previewAlpha));
      menuDrawContext.drawText(font, text, cardTextX + 3.0F, y + (height - font.height()) / 2.0F, zenithColor2.method7294(previewAlpha));
      return cardTextX + width + 3.0F;
   }

   public static float chipWidth(String text) {
      return cardTagFont().width(text) + 6.0F;
   }

   public static float drawTags(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, List<String> items, float cardTextX, float f, float f2, float meta, float previewAlpha) {
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      float f3 = f + (f2 - f) * 0.5F;
      float cardTextX2 = cardTextX;
      int n = 0;

      for (String str : items) {
         String str2 = str.trim();
         if (!str2.isEmpty()) {
            String text = "#" + str2;
            if (n >= 3 || cardTextX2 + chipWidth(text) > f3) {
               break;
            }

            cardTextX2 = drawChip(menuDrawContext, text, zenithColor.method7292(0.14F), zenithColor, cardTextX2, meta, previewAlpha);
            n++;
         }
      }

      int n2 = 0;

      for (String str : items) {
         if (!str.isBlank()) {
            n2++;
         }
      }

      if (n > 0 && n < n2) {
         String text = "+" + (n2 - n);
         if (cardTextX2 + chipWidth(text) <= f3 + 10.0F) {
            cardTextX2 = drawChip(menuDrawContext, text, currentStyle.getDisableActiveBg().getZenithColor(), currentStyle.getTextTertiary().getZenithColor(), cardTextX2, meta, previewAlpha);
         }
      }

      return cardTextX2;
   }

   public static float drawMeta(MenuDrawContext menuDrawContext, String name, ZenithColor zenithColor, float drawChip, float cardTextX, float f, float meta, float previewAlpha) {
      Font font = cardMetaFont();
      float floatValue = drawChip + (drawChip > cardTextX ? 2.0F : 0.0F);
      if (!name.isEmpty() && !(floatValue >= f - 16.0F)) {
         String clip = font.clip(name, f - floatValue);
         menuDrawContext.drawText(font, clip, floatValue, meta, zenithColor.method7294(previewAlpha));
         return floatValue + font.width(clip) + 4.0F;
      } else {
         return drawChip;
      }
   }

   public static float pillWidth(String pillText) {
      return Math.max(44.0F, cardButtonFont().width(pillText) + 14.0F);
   }

   public static BoundsData pillRect(BoundsData boundsData, float f, String pillText) {
      float width = pillWidth(pillText);
      return new BoundsData(f - width, boundsData.getFloatValue2() + (boundsData.height() - 14.0F) / 2.0F, width, 14.0F);
   }

   public static void drawPill(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, BoundsData pillRect, String pillText, boolean have, float hot, boolean contains, float previewAlpha) {
      Font font = cardButtonFont();
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(pillRect.height() / 2.0F);
      ZenithColor zenithColor2 = have ? currentStyle.getFieldSurfaceBackground().getZenithColor() : zenithColor.method7292(0.2F);
      menuDrawContext.drawRoundedRect(
         pillRect.getFloatValue(),
         pillRect.getFloatValue2(),
         pillRect.width(),
         pillRect.height(),
         headerRadius,
         zenithColor2.method7295(zenithColor.method7292(0.35F), hot * 0.6F).method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedBorder(
         pillRect.getFloatValue(), pillRect.getFloatValue2(), pillRect.width(), pillRect.height(), 0.15F, headerRadius, zenithColor.method7292(0.12F + 0.25F * hot).method7294(previewAlpha)
      );
      ZenithColor zenithColor3 = have ? (hot > 0.5F ? currentStyle.getTextEnable().getZenithColor() : OK) : zenithColor.method7295(ZenithColor.INSTANCE, hot * 0.5F);
      if (contains) {
         float f = (float)(System.currentTimeMillis() % 1000L) / 1000.0F;
         zenithColor3 = zenithColor3.method7294(0.55F + 0.45F * (float)Math.abs(Math.sin(f * Math.PI)));
      }

      menuDrawContext.drawText(
         font,
         pillText,
         pillRect.getFloatValue() + (pillRect.width() - font.width(pillText)) / 2.0F,
         pillRect.getFloatValue2() + (pillRect.height() - font.height()) / 2.0F,
         zenithColor3.method7294(previewAlpha)
      );
   }

   private static ZenithColor tint(StoreApi.Data2 entry, OriginUtil.Data3 data3) {
      if (data3.color() != -1) {
         return new ZenithColor(data3.color() | 0xFF000000);
      } else {
         String string = entry.name().isBlank() ? entry.getUUID().toString() : entry.name().toLowerCase(Locale.ROOT);
         float hue = (string.hashCode() & 65535) / 65536.0F;
         return ZenithColor.method7297(hue, isConfig(entry) ? 0.45F : 0.55F, 0.95F);
      }
   }

   private static String initial(String display) {
      if (display != null) {
         for (int i = 0; i < display.length(); i++) {
            char charAt = display.charAt(i);
            if (Character.isLetterOrDigit(charAt)) {
               return String.valueOf(charAt).toUpperCase(Locale.ROOT);
            }
         }
      }

      return "?";
   }

   public static void letterAvatar(MenuDrawContext menuDrawContext, ZenithColor letterColor, String display, float floatValue, float cardAvatarY, float size, float previewAlpha) {
      ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(size * 0.3F);
      menuDrawContext.drawRoundedRect(
         floatValue,
         cardAvatarY,
         size,
         size,
         proxyDotRadiusData,
         GlowColor.method8096(letterColor.method7292(0.26F * previewAlpha), letterColor.method7292(0.13F * previewAlpha), letterColor.method7292(0.18F * previewAlpha), letterColor.method7292(0.08F * previewAlpha))
      );
      menuDrawContext.drawRoundedBorder(floatValue, cardAvatarY, size, size, 0.15F, proxyDotRadiusData, letterColor.method7292(0.3F * previewAlpha));
      Font font = Fonts.NEW_SEMIBOLD.getFont(size * 0.42F);
      String text = initial(display);
      ZenithColor zenithColor = letterColor.method7295(ZenithColor.INSTANCE, 0.2F).method7294(previewAlpha);
      float[] inkBounds = font.inkBounds(text);
      if (inkBounds == null) {
         menuDrawContext.drawText(font, text, floatValue + (size - font.width(text)) / 2.0F, cardAvatarY + (size - font.height()) / 2.0F, zenithColor);
      } else {
         menuDrawContext.drawText(font, text, floatValue + (size - inkBounds[0]) / 2.0F, cardAvatarY + (size - inkBounds[1]) / 2.0F - font.baseline() + inkBounds[2], zenithColor);
      }
   }

   public static void cloudAvatar(MenuDrawContext menuDrawContext, StoreApi.Data2 cloudTwin, String display, ZenithColor letterColor, float floatValue, float cardAvatarY, float size, float previewAlpha) {
      Identifier texture = cloudTwin != null && cloudTwin.hasPreview() ? CloudPreviewTextureCache.get(cloudTwin.getUUID(), cloudTwin.getText6()) : null;
      if (texture != null) {
         menuDrawContext.drawRoundedTexture(texture, floatValue, cardAvatarY, size, size, ProxyDotRadiusData.method8069(size * 0.3F), ZenithColor.INSTANCE.method7294(previewAlpha));
      } else {
         letterAvatar(menuDrawContext, letterColor, display, floatValue, cardAvatarY, size, previewAlpha);
      }
   }

   private void avatar(MenuDrawContext menuDrawContext, StoreApi.Data2 entry, OriginUtil.Data3 data3, float floatValue, float cardAvatarY, float size, float previewAlpha) {
      cloudAvatar(menuDrawContext, entry, entry.name(), tint(entry, data3), floatValue, cardAvatarY, size, previewAlpha);
   }

   private void header(StoreApi.Data2 entry) {
      this.panel
         .asideCustom(
            46.0F,
            (menuDrawContext, n, n2, f, f2, f3, previewAlpha, zenithStyle) -> {
               OriginUtil.Data3 data3 = OriginUtil.method6916(entry);
               float size = 34.0F;
               this.avatar(menuDrawContext, entry, data3, f + 8.0F, f2 + 5.0F, size, previewAlpha);
               Font font = Fonts.NEW_SEMIBOLD.getFont(5.8F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
               float floatValue = f + 8.0F + size + 7.0F;
               menuDrawContext.drawText(
                  font, font.clip(entry.name(), f + f3 - 8.0F - floatValue), floatValue, f2 + 9.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
               );
               String name = (data3.version().isBlank() ? "" : "v" + data3.version() + "  ·  ") + (entry.getText8().isBlank() ? "?" : entry.getText8());
               menuDrawContext.drawText(
                  font2,
                  font2.clip(name, f + f3 - 8.0F - floatValue),
                  floatValue,
                  f2 + 11.0F + font.height(),
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               if (!data3.getItems().isEmpty()) {
                  Font font3 = Fonts.NEW_MEDIUM.getFont(3.9F);
                  float floatValue2 = floatValue;
                  float centeredTextY = f2 + 14.0F + font.height() + font2.height();

                  for (String str : data3.getItems()) {
                     String text = "#" + str;
                     float width = font3.width(text) + 6.0F;
                     if (floatValue2 + width > f + f3 - 8.0F) {
                        break;
                     }

                     menuDrawContext.drawRoundedRect(
                        floatValue2,
                        centeredTextY - 1.2F,
                        width,
                        font3.height() + 2.4F,
                        ProxyDotRadiusData.method8069((font3.height() + 2.4F) / 2.0F),
                        zenithStyle.getPrimaryColor().getZenithColor().method7292(0.14F).method7294(previewAlpha)
                     );
                     menuDrawContext.drawText(font3, text, floatValue2 + 3.0F, centeredTextY, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
                     floatValue2 += width + 3.0F;
                  }
               }
            }
         );
   }

   private void configAside(StoreApi.Data2 entry) {
      this.header(entry);
      if (!entry.description().isBlank()) {
         this.panel.paragraph(entry.description(), true);
      }

      this.panel.asideCaption("gui.scripts.caption.actions");
      if (this.needsPurchase(entry)) {
         this.panel
            .asideAction(
               "gui.market.buy.action",
               MenuGlyph.STORAGE,
               () -> StoreApi.method1960(entry.getUUID()).getIntValue() + " ₽",
               () -> this.buy(entry),
               "gui.market.buy.desc"
            );
      }

      boolean configPresent = this.configPresent(entry);
      this.panel
         .asideAction(
            configPresent ? "gui.market.config.redownload" : "gui.market.config.download",
            MenuGlyph.STORAGE,
            () -> this.busy.contains(entry.getUUID()) ? t("gui.scripts.market.busy") : "",
            () -> this.downloadConfig(entry),
            "gui.market.config.download.desc"
         );
      if (configPresent) {
         this.panel.asideAction("gui.market.config.apply", MenuGlyph.CHECK, () -> "", () -> this.applyConfig(entry), "gui.market.config.apply.desc");
      }

      if (!entry.mine()) {
         this.panel
            .asideAction(
               "gui.scripts.market.like",
               MenuGlyph.HEART,
               () -> (this.entry(entry.getUUID()) != null && this.entry(entry.getUUID()).isFlag3() ? "♥ " : "") + this.likesOf(entry),
               () -> this.like(entry),
               "gui.scripts.market.like.desc"
            );
      }

      this.panel.asideCaption("gui.scripts.market.caption.about");
      this.panel.asideInfo("gui.scripts.market.info.author", () -> entry.getText8().isBlank() ? "?" : entry.getText8(), "gui.scripts.market.info.author.desc");
      this.panel.asideInfo("gui.configs.server", () -> entry.server().isBlank() ? "—" : entry.server(), "gui.configs.server.desc");
      this.panel.asideInfo("gui.scripts.market.info.revision", () -> "#" + entry.getLongValue2(), "gui.scripts.market.info.revision.desc");
      this.panel.asideInfo("gui.scripts.market.info.size", () -> size(entry.getLongValue()), "gui.scripts.market.info.size.desc");
      this.panel.asideInfo("gui.scripts.market.info.updated", () -> ScriptsSectionPanel.ago(entry.getLongValue4()), "gui.scripts.market.info.updated.desc");
      if (entry.mine()) {
         this.panel.asideCaption("gui.scripts.market.caption.publication");
         this.chips(
            this.entryVisibility,
            () -> visibilityIndex(this.entry(entry.getUUID()) == null ? entry.getText7() : this.entry(entry.getUUID()).getText7()),
            var2x -> this.changeVisibility(entry, var2x)
         );
         this.panel
            .asideAction(
               "gui.scripts.market.codes.create",
               MenuGlyph.KEY,
               () -> String.valueOf(this.codes.getOrDefault(entry.getUUID(), List.of()).size()),
               () -> this.createCode(entry),
               "gui.scripts.market.codes.create.desc"
            );
         this.panel
            .asideAction(
               "gui.scripts.market.unpublish",
               MenuGlyph.CLOSE,
               () -> this.armed.equals("delete:" + entry.getUUID()) ? t("gui.scripts.market.again") : "",
               () -> {
                  if (this.armed("delete:" + entry.getUUID())) {
                     this.unpublish(null, entry);
                  }
               },
               "gui.scripts.market.unpublish.desc"
            );
      }

      this.statusRow();
      this.panel.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
         this.selected = null;
         this.panel.invalidate();
      }, "gui.scripts.market.back.desc");
   }

   private void downloadConfig(StoreApi.Data2 entry) {
      StoreApi storeApi = store();
      if (storeApi != null && this.busy.add(entry.getUUID())) {
         this.revision.incrementAndGet();
         storeApi.method1970(entry.getUUID()).whenComplete((var2x, throwable) -> {
            try {
               if (throwable == null) {
                  File saveDownloaded = CloudConfigsSectionPanel.saveDownloaded(entry, var2x);
                  if (!entry.mine()) {
                     this.taken.remember(entry);
                  }

                  String name = saveDownloaded.getName();
                  int lastIndexOf = name.lastIndexOf(46);
                  this.report(t("gui.configs.report.downloaded").replace("{n}", lastIndexOf > 0 ? name.substring(0, lastIndexOf) : name), false);
                  return;
               }

               this.report(reason(throwable), true);
            } catch (Exception e) {
               this.report(t("gui.configs.report.writeFailed"), true);
               return;
            } finally {
               this.presence.remove(entry.getUUID());
               this.busy.remove(entry.getUUID());
               Minecraft.getInstance().execute(this.panel::invalidate);
            }
         });
      }
   }

   private void applyConfig(StoreApi.Data2 entry) {
      Zenith.getInstance().getManager().method7768(configFile(entry).getName(), Manager.Type.ALL).whenComplete((flag, throwable) -> {
         boolean flag2 = throwable == null && Boolean.TRUE.equals(flag);
         this.report(flag2 ? t("gui.market.config.applied").replace("{n}", entry.name()) : t("gui.configs.report.unreadable"), !flag2);
         this.panel.invalidate();
      });
   }

   private void entryAside(Storage hostOrNull, StoreApi.Data2 entry) {
      if (isConfig(entry)) {
         this.configAside(entry);
      } else {
         this.header(entry);
         if (!entry.description().isBlank()) {
            this.panel.paragraph(entry.description(), true);
         }

         Script script = this.installed.get(entry.getUUID());
         boolean flag = script != null && OriginUtil.method6927(hostOrNull, entry);
         this.panel.asideCaption("gui.scripts.caption.actions");
         if (this.needsPurchase(entry)) {
            this.panel
               .asideAction(
                  "gui.market.buy.action",
                  MenuGlyph.STORAGE,
                  () -> StoreApi.method1960(entry.getUUID()).getIntValue() + " ₽",
                  () -> this.buy(entry),
                  "gui.market.buy.desc"
               );
         }

         String label = script == null
            ? (entry.mine() ? "gui.scripts.market.download" : "gui.scripts.market.install")
            : (flag ? "gui.scripts.market.update" : "gui.scripts.market.open");
         this.panel
            .asideAction(
               label,
               MenuGlyph.CLOUD,
               () -> this.busy.contains(entry.getUUID()) ? t("gui.scripts.market.busy") : "",
               () -> this.primary(hostOrNull, entry),
               "gui.scripts.market.primary.desc"
            );
         if (script != null && !flag) {
            this.panel
               .asideAction("gui.scripts.market.reinstall", MenuGlyph.RESET, () -> "", () -> this.install(hostOrNull, entry), "gui.scripts.market.reinstall.desc");
         }

         if (!entry.mine()) {
            this.panel
               .asideAction(
                  "gui.scripts.market.like",
                  MenuGlyph.HEART,
                  () -> (this.entry(entry.getUUID()) != null && this.entry(entry.getUUID()).isFlag3() ? "♥ " : "") + this.likesOf(entry),
                  () -> this.like(entry),
                  "gui.scripts.market.like.desc"
               );
         }

         if (script != null) {
            this.panel
               .asideAction(
                  "gui.scripts.market.uninstall",
                  MenuGlyph.CLOSE,
                  () -> this.armed.equals("uninstall:" + entry.getUUID()) ? t("gui.scripts.market.again") : "",
                  () -> {
                     if (this.armed("uninstall:" + entry.getUUID())) {
                        this.uninstall(hostOrNull, script);
                     }
                  },
                  "gui.scripts.market.uninstall.desc"
               );
         }

         this.panel.asideCaption("gui.scripts.market.caption.about");
         this.panel.asideInfo("gui.scripts.market.info.author", () -> entry.getText8().isBlank() ? "?" : entry.getText8(), "gui.scripts.market.info.author.desc");
         this.panel.asideInfo("gui.scripts.market.info.revision", () -> "#" + entry.getLongValue2(), "gui.scripts.market.info.revision.desc");
         this.panel.asideInfo("gui.scripts.market.info.size", () -> size(entry.getLongValue()), "gui.scripts.market.info.size.desc");
         this.panel.asideInfo("gui.scripts.market.info.updated", () -> ScriptsSectionPanel.ago(entry.getLongValue4()), "gui.scripts.market.info.updated.desc");
         String str = OriginUtil.method6916(entry).getText3();
         if (!str.isBlank() && (str.startsWith("https://") || str.startsWith("http://"))) {
            this.panel
               .asideAction("gui.scripts.market.link", MenuGlyph.NETWORK, () -> "", () -> com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create(str)), "gui.scripts.market.link.desc");
         }

         if (entry.mine()) {
            this.panel.asideCaption("gui.scripts.market.caption.publication");
            this.panel
               .asideInfo(
                  "gui.market.caption.publish",
                  () -> this.marketLabel(this.entry(entry.getUUID()) == null ? entry : this.entry(entry.getUUID())),
                  "gui.market.status.desc"
               );
            this.chips(
               this.entryVisibility,
               () -> visibilityIndex(this.entry(entry.getUUID()) == null ? entry.getText7() : this.entry(entry.getUUID()).getText7()),
               var2x -> this.changeVisibility(entry, var2x)
            );
            if (script != null) {
               this.panel
                  .asideAction(
                     "gui.scripts.market.push",
                     MenuGlyph.CLOUD,
                     () -> this.publishing.contains(script.getId()) ? t("gui.scripts.market.busy") : "",
                     () -> this.publish(hostOrNull, script, visibilityIndex(entry.getText7())),
                     "gui.scripts.market.push.desc"
                  );
            }

            this.panel
               .asideAction(
                  "gui.scripts.market.codes.create",
                  MenuGlyph.KEY,
                  () -> String.valueOf(this.codes.getOrDefault(entry.getUUID(), List.of()).size()),
                  () -> this.createCode(entry),
                  "gui.scripts.market.codes.create.desc"
               );

            for (StoreApi.Data data : this.codes.getOrDefault(entry.getUUID(), List.of())) {
               this.panel
                  .asideCustom(
                     12.0F,
                     (var1x, var2x, var3x, var4x, var5x, var6x, previewAlpha, var8x) -> {
                        Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
                        boolean contains = new BoundsData(var4x, var5x, var6x, 12.0F).contains(var2x, var3x);
                        ZenithColor zenithColor = "ACTIVE".equalsIgnoreCase(data.getText2())
                           ? var8x.getTextSecondary().getZenithColor()
                           : var8x.getTextTertiary().getZenithColor();
                        var1x.drawText(
                           font,
                           data.getText().isBlank() ? "••••••" : data.getText(),
                           var4x + 8.0F,
                           var5x + 3.0F,
                           (contains ? var8x.getTextEnable().getZenithColor() : zenithColor).method7294(previewAlpha)
                        );
                        String lowerCase = data.getText2().toLowerCase(Locale.ROOT);
                        var1x.drawText(
                           font, lowerCase, var4x + var6x - 8.0F - font.width(lowerCase), var5x + 3.0F, var8x.getTextTertiary().getZenithColor().method7294(previewAlpha)
                        );
                        if (contains) {
                           HoverInfo.request(t("gui.scripts.market.codes"), t("gui.scripts.market.codes.row"));
                        }
                     },
                     () -> {
                        if (!data.getText().isBlank()) {
                           Minecraft.getInstance().keyboardHandler.setClipboard(data.getText());
                           this.report(t("gui.scripts.market.status.copied"), false);
                        }
                     },
                     null,
                     () -> this.revokeCode(entry, data)
                  );
            }

            this.panel
               .asideAction(
                  "gui.scripts.market.unpublish",
                  MenuGlyph.CLOSE,
                  () -> this.armed.equals("delete:" + entry.getUUID()) ? t("gui.scripts.market.again") : "",
                  () -> {
                     if (this.armed("delete:" + entry.getUUID())) {
                        this.unpublish(hostOrNull, entry);
                     }
                  },
                  "gui.scripts.market.unpublish.desc"
               );
         }

         this.statusRow();
         this.panel.asideAction("gui.scripts.market.back", MenuGlyph.RESET, () -> "", () -> {
            this.selected = null;
            this.panel.invalidate();
         }, "gui.scripts.market.back.desc");
      }
   }

   private int count(List<StoreApi.Data2> catalog, boolean flag) {
      int n = 0;

      for (StoreApi.Data2 entry : catalog) {
         if (isConfig(entry) == flag) {
            n++;
         }
      }

      return n;
   }

   private int likesOf(StoreApi.Data2 entry) {
      StoreApi.Data2 data2 = this.entry(entry.getUUID());
      return data2 == null ? entry.getIntValue() : data2.getIntValue();
   }

   private void overviewAside(Storage hostOrNull) {
      this.panel.asideCaption("gui.scripts.market.caption.overview");
      this.panel.asideInfo("gui.market.info.scripts", () -> String.valueOf(this.count(this.catalog, false)), "gui.market.info.scripts.desc");
      this.panel.asideInfo("gui.market.info.configs", () -> String.valueOf(this.count(this.catalog, true)), "gui.market.info.configs.desc");
      this.panel.asideInfo("gui.scripts.market.info.library", () -> String.valueOf(this.library.size()), "gui.scripts.market.info.library.desc");
      this.panel.asideInfo("gui.scripts.market.info.mine", () -> String.valueOf(this.mine.size()), "gui.scripts.market.info.mine.desc");
      this.panel.asideCaption("gui.scripts.market.caption.redeem");
      this.panel
         .asideCustom(
            22.0F,
            (var1x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
               Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
               String text = t("gui.scripts.market.redeem.go");
               float width = font.width(text) + 14.0F;
               float floatValue = f + 8.0F;
               float shownWidth = f3 - 16.0F - width - 4.0F;
               this.redeem
                  .render(
                     var1x,
                     font,
                     floatValue,
                     f2 + 3.0F,
                     shownWidth,
                     previewAlpha,
                     t("gui.scripts.market.redeem.hint"),
                     zenithStyle.getFieldSurfaceBackground().getZenithColor(),
                     zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F),
                     zenithStyle.getPrimaryColor().getZenithColor(),
                     zenithStyle.getTextEnable().getZenithColor(),
                     zenithStyle.getTextTertiary().getZenithColor()
                  );
               this.redeemButton = new BoundsData(floatValue + shownWidth + 4.0F, f2 + 3.0F, width, 16.0F);
               boolean begin = this.panel.pressFx.begin(var1x, "redeem", this.redeemButton);
               boolean contains = this.redeemButton.contains(lastClickX, lastClickY);
               float hot = this.panel.hot("redeem", contains);
               ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
               var1x.drawRoundedRect(
                  this.redeemButton.getFloatValue(),
                  this.redeemButton.getFloatValue2(),
                  width,
                  16.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  zenithColor.method7292(0.18F + 0.2F * hot).method7294(previewAlpha)
               );
               this.panel.pressFx.ink(var1x, "redeem", this.redeemButton, 4.0F, ZenithColor.INSTANCE.method7292(0.2F), previewAlpha);
               var1x.drawText(
                  font,
                  text,
                  this.redeemButton.getFloatValue() + 7.0F,
                  this.redeemButton.getFloatValue2() + (16.0F - font.height()) / 2.0F,
                  zenithColor.method7295(ZenithColor.INSTANCE, hot * 0.5F).method7294(previewAlpha)
               );
               this.panel.pressFx.end(var1x, begin);
            },
            () -> {},
            null
         );
      if (this.category != 2) {
         this.publishSection(hostOrNull);
      }

      if (this.category != 1) {
         this.panel.asideCaption("gui.market.caption.publishConfig");
         this.panel.paragraph(t("gui.market.publishConfig.hint"), true);
         this.panel.asideAction("gui.market.publishConfig.go", MenuGlyph.CONFIGS, () -> "", this.panel::openConfigsCloud, "gui.market.publishConfig.go.desc");
      }

      this.panel
         .asideAction(
            "gui.scripts.market.refresh",
            MenuGlyph.RESET,
            () -> this.loading.get() > 0 ? t("gui.scripts.market.busy") : "",
            () -> this.fetch(true),
            "gui.scripts.market.refresh.desc"
         );
      this.statusRow();
   }

   public void publishSection(Storage hostOrNull) {
      this.panel.asideCaption("gui.scripts.market.caption.publish");
      this.chips(this.publishVisibility, () -> this.publishVis, var1x -> this.publishVis = var1x);
      ArrayList<Script> scripts = new ArrayList();

      for (Script script : hostOrNull.method1293()) {
         OriginUtil.Data2 data2 = OriginUtil.method6921(script);
         if (data2 == null || data2.mine()) {
            scripts.add(script);
         }
      }

      if (scripts.isEmpty()) {
         this.panel.paragraph(t("gui.scripts.market.publish.none"), true);
      }

      for (Script script : scripts) {
         this.panel
            .asideCustom(
               24.0F,
               (var2x, lastClickX, var4x, var5x, f, var7x, previewAlpha, currentStyle) -> this.renderCandidate(var2x, lastClickX, var4x, var5x, f, var7x, previewAlpha, currentStyle, script),
               () -> this.publish(hostOrNull, script, this.publishVis),
               null
            );
      }

      if (this.view == 0) {
         this.statusRow();
      }
   }

   private void renderCandidate(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle currentStyle, Script script) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 21.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.panel.hot("pub:" + script.getId(), contains);
      ScriptsSectionPanel.card(menuDrawContext, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
      ScriptInfoPage.drawAvatar(menuDrawContext, currentStyle, script, boundsData.getFloatValue() + 4.0F, boundsData.getFloatValue2() + 3.0F, boundsData.height() - 6.0F, previewAlpha, -2);
      Font font = Fonts.NEW_MEDIUM.getFont(4.7F);
      Font font2 = Fonts.NEW_REGULAR.getFont(3.9F);
      OriginUtil.Data2 data2 = OriginUtil.method6921(script);
      boolean flag = this.publishing.contains(script.getId());
      String text = t(flag ? "gui.scripts.market.busy" : (data2 != null ? "gui.scripts.market.publish.update" : "gui.scripts.market.publish.go"));
      float width = font2.width(text) + 10.0F;
      float floatValue = boundsData.getFloatValue() + boundsData.height() + 2.0F;
      float f4 = boundsData.getFloatValue() + boundsData.width() - width - 8.0F;
      menuDrawContext.drawText(
         font, font.clip(script.method1403(), f4 - floatValue), floatValue, boundsData.getFloatValue2() + 3.5F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
      );
      String name = script.getPath() == null
         ? t("gui.scripts.market.publish.single")
         : (data2 != null ? t("gui.scripts.market.publish.live").replace("{n}", String.valueOf(data2.getLongValue())) : t("gui.scripts.market.publish.draft"));
      menuDrawContext.drawText(
         font2,
         font2.clip(name, f4 - floatValue),
         floatValue,
         boundsData.getFloatValue2() + 5.0F + font.height(),
         currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      float floatValue2 = boundsData.getFloatValue() + boundsData.width() - width - 4.0F;
      float y = boundsData.getFloatValue2() + (boundsData.height() - 10.0F) / 2.0F;
      menuDrawContext.drawRoundedRect(floatValue2, y, width, 10.0F, ProxyDotRadiusData.method8069(5.0F), zenithColor.method7292(0.16F + 0.2F * hot).method7294(previewAlpha));
      menuDrawContext.drawText(font2, text, floatValue2 + 5.0F, y + (10.0F - font2.height()) / 2.0F, zenithColor.method7295(ZenithColor.INSTANCE, hot * 0.5F).method7294(previewAlpha));
   }

   private void chips(ChipStrip entryVisibility, IntSupplier intSupplier, IntConsumer intConsumer) {
      this.panel.asideCustom(entryVisibility.height(128.0F) + 6.0F, (var2x, var3x, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
         entryVisibility.render(var2x, var3x, lastClickY, f + 8.0F, f2 + 3.0F, f3 - 16.0F, intSupplier.getAsInt(), previewAlpha, zenithStyle);
         if (entryVisibility.hovered() >= 0) {
            HoverInfo.request(t("gui.scripts.market.visibility"), t("gui.scripts.market.visibility.desc"));
         }
      }, () -> {
         int var3x = entryVisibility.hovered();
         if (var3x >= 0) {
            intConsumer.accept(var3x);
            this.panel.invalidate();
         }
      }, null);
   }

   private void statusRow() {
      this.panel.asideCustom(14.0F, (menuDrawContext, n, n2, f, f2, f3, f4, zenithStyle) -> {
         long l = System.currentTimeMillis() - this.statusAt;
         if (!this.status.isEmpty() && l <= 8000L) {
            float f5 = l > 7000L ? 1.0F - (float)(l - 7000L) / 1000.0F : Math.min(1.0F, (float)l / 180.0F);
            Font font = Fonts.NEW_REGULAR.getFont(4.3F);
            menuDrawContext.drawText(font, font.clip(this.status, f3 - 16.0F), f + 8.0F, f2 + 4.0F, (this.statusError ? ERROR : OK).method7294(f4 * f5));
         }
      });
   }

   public boolean clickRedeem(double lastClickX, double lastClickY) {
      BoundsData bounds = this.redeem.bounds();
      boolean focused = bounds != null && bounds.contains(lastClickX, lastClickY);
      this.redeem.setFocused(focused);
      if (this.redeemButton != null && this.redeemButton.contains(lastClickX, lastClickY)) {
         this.panel.pressFx.press("redeem", lastClickX, lastClickY);
         this.doRedeem();
         return true;
      } else {
         return focused;
      }
   }

   public void doRedeem() {
      String kind = this.redeem.get();
      StoreApi storeApi = store();
      if (!kind.isEmpty() && storeApi != null) {
         storeApi.method1986(kind).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               this.report(reason(var2x), true);
            } else {
               Minecraft.getInstance().execute(() -> {
                  this.redeem.set("");
                  this.report(t("gui.scripts.market.status.redeemed").replace("{n}", var1x.name()), false);
                  this.view = 2;
                  this.category = isConfig(var1x) ? 2 : 1;
                  this.selected = var1x.getUUID();
                  this.fetch(true);
                  this.panel.invalidate();
               });
            }
         });
      }
   }

   private void install(Storage hostOrNull, StoreApi.Data2 entry) {
      StoreApi storeApi = store();
      if (storeApi != null && this.busy.add(entry.getUUID())) {
         this.revision.incrementAndGet();
         storeApi.method1970(entry.getUUID())
            .whenComplete(
               (var3x, throwable) -> {
                  if (throwable != null) {
                     this.busy.remove(entry.getUUID());
                     this.report(reason(throwable), true);
                  } else {
                     Minecraft.getInstance()
                        .execute(
                           () -> {
                              try {
                                 Script var4x = OriginUtil.method6928(hostOrNull, entry, var3x);
                                 this.installed = OriginUtil.method6925(hostOrNull);
                                 String replacement = var4x == null ? "" : ScriptPermissions.method5(var4x);
                                 this.report(
                                    t("gui.scripts.market.status.installed").replace("{n}", var4x == null ? entry.name() : var4x.method1403())
                                       + (replacement.isEmpty() ? "" : " · " + t("gui.scripts.market.rights").replace("{r}", replacement)),
                                    false
                                 );
                              } catch (Exception e) {
                                 this.report(reason(e), true);
                              } finally {
                                 this.busy.remove(entry.getUUID());
                                 this.panel.invalidate();
                              }
                           }
                        );
                  }
               }
            );
      }
   }

   private void uninstall(Storage hostOrNull, Script script) {
      try {
         OriginUtil.uninstall(hostOrNull, script);
         this.installed = OriginUtil.method6925(hostOrNull);
         this.report(t("gui.scripts.market.status.removed").replace("{n}", script.method1403()), false);
      } catch (Exception e) {
         this.report(reason(e), true);
      }

      this.panel.invalidate();
   }

   private void publish(Storage hostOrNull, Script script, int visibilityIndex) {
      StoreApi storeApi = store();
      if (storeApi != null && this.publishing.add(script.getId())) {
         this.revision.incrementAndGet();
         String anObject = VISIBILITY[Math.max(0, Math.min(VISIBILITY.length - 1, visibilityIndex))];
         OriginUtil.Data2 data2 = OriginUtil.method6921(script);
         boolean flag = "PUBLIC".equals(anObject) && (data2 == null || !StoreApi.method1960(data2.getUUID()).method2007());
         OriginUtil.method6933(storeApi, hostOrNull, script, flag ? "PRIVATE" : anObject)
            .thenCompose(var2x -> flag ? storeApi.method1961(var2x.getUUID(), 0) : CompletableFuture.completedFuture(var2x))
            .whenComplete((var3x, var4x) -> {
               this.publishing.remove(script.getId());
               if (var4x != null) {
                  this.report(reason(var4x), true);
               } else {
                  this.report(flag ? t("gui.market.report.review") : t("gui.scripts.market.status.published").replace("{n}", var3x.name()), false);
                  Minecraft.getInstance().execute(() -> {
                     this.fetch(true);
                     this.selected = var3x.getUUID();
                     this.view = 3;
                     this.panel.invalidate();
                  });
               }
            });
      }
   }

   private void unpublish(Storage hostOrNull, StoreApi.Data2 entry) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1975(entry.getUUID()).whenComplete((var3x, throwable) -> {
            if (throwable != null) {
               this.report(reason(throwable), true);
            } else {
               Minecraft.getInstance().execute(() -> {
                  Script var3xx = this.installed.get(entry.getUUID());
                  if (var3xx != null) {
                     OriginUtil.method6924(var3xx);
                  }

                  this.mine.removeIf(var1xxx -> var1xxx.getUUID().equals(entry.getUUID()));
                  this.catalog.removeIf(var1xxx -> var1xxx.getUUID().equals(entry.getUUID()));
                  this.selected = null;
                  if (hostOrNull != null) {
                     this.installed = OriginUtil.method6925(hostOrNull);
                  }

                  this.report(t("gui.scripts.market.status.unpublished"), false);
                  this.panel.invalidate();
               });
            }
         });
      }
   }

   private void like(StoreApi.Data2 entry) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1978(entry.getUUID())
            .whenComplete(
               (var2x, throwable) -> {
                  if (throwable != null) {
                     this.report(reason(throwable), true);
                  } else {
                     StoreApi.Data2 data2 = this.entry(entry.getUUID());
                     if (data2 == null) {
                        data2 = entry;
                     }

                     this.replace(
                        new StoreApi.Data2(
                           data2.getUUID(),
                           data2.kind(),
                           data2.name(),
                           data2.fileName(),
                           data2.description(),
                           data2.server(),
                           data2.getLongValue(),
                           data2.getText6(),
                           data2.getText7(),
                           data2.getLongValue2(),
                           data2.getLongValue3(),
                           data2.getLongValue4(),
                           data2.getLongValue5(),
                           data2.getText8(),
                           data2.mine(),
                           data2.hasPreview(),
                           var2x[0],
                           var2x[1] == 1,
                           data2.getIntValue2()
                        )
                     );
                     Minecraft.getInstance().execute(this.panel::invalidate);
                  }
               }
            );
      }
   }

   private void changeVisibility(StoreApi.Data2 entry, int n) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         boolean flag = "PUBLIC".equals(VISIBILITY[n]) && !isConfig(entry) && !StoreApi.method1960(entry.getUUID()).method2007();
         (flag ? storeApi.method1961(entry.getUUID(), 0) : storeApi.method1974(entry.getUUID(), null, null, VISIBILITY[n])).whenComplete((var2x, var3x) -> {
            if (var3x != null) {
               this.report(reason(var3x), true);
            } else {
               this.replace(var2x);
               this.report(t(flag ? "gui.market.report.review" : "gui.scripts.market.status.visibility"), false);
               Minecraft.getInstance().execute(this.panel::invalidate);
            }
         });
      }
   }

   private void loadCodes(StoreApi.Data2 entry) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1980(entry.getUUID()).whenComplete((var2x, throwable) -> {
            if (throwable == null) {
               this.codes.put(entry.getUUID(), (List<StoreApi.Data>)var2x);
               this.revision.incrementAndGet();
            }
         });
      }
   }

   private void createCode(StoreApi.Data2 entry) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1979(entry.getUUID(), 1).whenComplete((var2x, throwable) -> {
            if (throwable != null) {
               this.report(reason(throwable), true);
            } else {
               if (!var2x.isEmpty()) {
                  String text = var2x.get(0).getText();
                  Minecraft.getInstance().execute(() -> Minecraft.getInstance().keyboardHandler.setClipboard(text));
                  this.report(t("gui.scripts.market.status.codeCreated").replace("{n}", text), false);
               }

               this.loadCodes(entry);
            }
         });
      }
   }

   private void revokeCode(StoreApi.Data2 entry, StoreApi.Data data) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1981(entry.getUUID(), data.getUUID()).whenComplete((var2x, var3x) -> {
            if (var3x != null) {
               this.report(reason(var3x), true);
            } else {
               this.report(t("gui.scripts.market.status.revoked"), false);
            }

            this.loadCodes(entry);
         });
      }
   }

   private static int visibilityIndex(String anotherString) {
      for (int i = 0; i < VISIBILITY.length; i++) {
         if (VISIBILITY[i].equalsIgnoreCase(anotherString)) {
            return i;
         }
      }

      return 3;
   }

   private static String size(long longValue) {
      if (longValue < 1024L) {
         return longValue + " B";
      } else {
         return longValue < 1048576L
            ? String.format(Locale.ROOT, "%.1f KB", (float)longValue / 1024.0F)
            : String.format(Locale.ROOT, "%.1f MB", (float)longValue / 1024.0F / 1024.0F);
      }
   }

   public static final class CardLines {
      private final float title;
      private final float note;
      private final float meta;

      public float title() {
         return this.title;
      }

      public float note() {
         return this.note;
      }

      public float meta() {
         return this.meta;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CardLines other)) return false;
         return java.util.Objects.equals(this.title, other.title) && java.util.Objects.equals(this.note, other.note) && java.util.Objects.equals(this.meta, other.meta);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(title, note, meta);
      }

      @Override
      public String toString() {
         return "CardLines[" + "title=" + this.title + ", " + "note=" + this.note + ", " + "meta=" + this.meta + "]";
      }

      public CardLines(float title, float note, float meta) {
         this.title = title;
         this.note = note;
         this.meta = meta;
      }
   }

   private static final class Presence {
      private final String name;
      private final boolean present;
      private final long checkedAt;

      public String name() {
         return this.name;
      }

      public boolean present() {
         return this.present;
      }

      public long checkedAt() {
         return this.checkedAt;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Presence other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.present, other.present) && java.util.Objects.equals(this.checkedAt, other.checkedAt);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, present, checkedAt);
      }

      @Override
      public String toString() {
         return "Presence[" + "name=" + this.name + ", " + "present=" + this.present + ", " + "checkedAt=" + this.checkedAt + "]";
      }

      private Presence(String name, boolean present, long checkedAt) {
         this.name = name;
         this.present = present;
         this.checkedAt = checkedAt;
      }
   }
}

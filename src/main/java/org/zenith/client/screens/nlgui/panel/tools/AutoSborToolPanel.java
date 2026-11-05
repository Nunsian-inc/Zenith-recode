package org.zenith.client.screens.nlgui.panel.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.world.item.ItemStack;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.AutoInventoryModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.InventoryPreset;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.SborList;
import org.zenith.utility.api.ServerNameNormalizer;
import org.zenith.utility.game.ItemEntry;
import org.zenith.utility.game.ItemMatcher;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.HudBatchState;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class AutoSborToolPanel extends ToolTab {
   private static final int LIST_ITEMS = 0;
   private static final int LIST_HISTORY = 1;
   private static final float SLOT_GAP = 3.0F;
   private static final int COLUMNS = 9;
   private static final int ROWS = 4;
   private static final float ICON_SCALE = 0.65F;
   private static final float SET_HEAD = 22.0F;
   private static final float INPUT_HEIGHT = 18.0F;
   private static final float BUTTON_HEIGHT = 17.0F;
   private static final float HINT_HEIGHT = 13.0F;
   private static final float SECTION_HEAD = 20.0F;
   private static final float CATALOG_ROW = 22.0F;
   private static final float PRESET_ROW = 22.0F;
   private static final float START_HEIGHT = 22.0F;
   private static final float RUN_CARD = 74.0F;
   private static final float SLOT_CARD = 34.0F;
   private static final float STEPPER_ROW = 22.0F;
   private static final float STEPPER_BUTTON = 11.0F;
   private static final float STEPPER_PILL_WIDTH = 62.0F;
   private static final long PRICE_STEP = 1000L;
   private static final float DURABILITY_STEP = 0.05F;
   private static final int FAST_STEP = 10;
   private static final long HAVE_REFRESH_MS = 400L;
   private final ChipStrip listStrip = new ChipStrip("gui.tools.inv.list.items", "gui.tools.inv.list.history");
   private final ChipStrip serverStrip = new ChipStrip("gui.tools.inv.server.funtime", "gui.tools.inv.server.holyworld", "gui.tools.inv.server.spookytime");
   private static final String[] SERVERS = new String[]{"Funtime 1.21", "HolyWorld", "SpookyTime"};
   private final BoundsData[] slotBounds = new BoundsData[36];
   private final Map<ItemMatcher, Integer> inSetCounts = new HashMap<>();
   private int listMode = 0;
   private int selectedSlot = -1;
   private int listCount;
   private int listFrom = 7;
   private int slotFrom = 6;
   private BoundsData stripBounds;
   private BoundsData serverStripBounds;
   private SearchBox searchInput;
   private SearchBox nameInput;
   private String contentSignature = "";
   private int haveSlot = -1;
   private int haveCount;
   private long haveAt;
   private BoundsData clearButton;
   private BoundsData saveButton;
   private BoundsData startButton;
   private BoundsData searchField;
   private BoundsData nameField;
   private BoundsData countMinus;
   private BoundsData countPlus;
   private BoundsData priceMinus;
   private BoundsData pricePlus;
   private BoundsData durabilityMinus;
   private BoundsData durabilityPlus;

   private static AutoInventoryModule module() {
      return AutoInventoryModule.INSTANCE;
   }

   private static InventoryPreset set() {
      return module().method207();
   }

   private static ServerNameNormalizer presets() {
      return module().getServerNameNormalizer();
   }

   @Override
   public String headerTitle() {
      return t("gui.tools.tab.inventory");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.STORAGE;
   }

   @Override
   public String sectionDescription() {
      return "gui.tools.inv.desc";
   }

   @Override
   public String emptyText() {
      return "gui.tools.inv.empty";
   }

   public boolean isLive() {
      return module().isEnabled();
   }

   @Override
   public void build() {
      InventoryPreset inventoryPreset = set();
      if (this.selectedSlot >= 0 && inventoryPreset.method871(this.selectedSlot)) {
         this.selectedSlot = -1;
      }

      this.custom(22.0F, this.entering(0, this::renderSetHead));
      this.custom(this.gridHeight() + 2.0F, this.entering(1, this::renderGrid));
      this.custom(13.0F, this.entering(2, this::renderHint));
      this.custom(27.0F, this.entering(3, this::renderButtons));
      this.custom(20.0F, this.entering(4, this::renderServerStrip));
      this.custom(24.0F, this.entering(5, this::renderSearchField));
      this.custom(20.0F, this.entering(6, this::renderListStrip));
      this.listFrom = this.mainEntryCount();
      if (this.listMode == 1) {
         this.buildHistory();
      } else {
         this.buildCatalog();
      }

      this.buildAside();
   }

   private void buildCatalog() {
      String catalogQuery = this.catalogQuery();
      int n = 7;
      int listCount = 0;

      for (ItemMatcher key : InventoryPreset.method891(module().method206())) {
         String name = key.getText2() == null ? key.getText22() : key.getText2();
         if (name != null && !name.isBlank() && (catalogQuery.isEmpty() || name.toLowerCase(Locale.ROOT).contains(catalogQuery))) {
            listCount++;
            this.custom(
               22.0F,
               this.entering(
                  n++,
                  (var3x, lastClickX, var5x, var6x, f, f2, previewAlpha, zenithStyle) -> this.renderCatalogRow(var3x, lastClickX, var5x, var6x, f, f2, previewAlpha, zenithStyle, key, name)
               ),
               () -> this.place(key),
               () -> this.inSetCounts.containsKey(key),
               () -> this.removeAll(key)
            );
         }
      }

      this.listCount = listCount;
      if (listCount == 0) {
         this.custom(
            76.0F,
            this.entering(
               7,
               (var1x, var2x, var3x, f, var5x, var6x, previewAlpha, zenithStyle) -> this.emptyCard(
                  var1x,
                  zenithStyle,
                  f + 8.0F,
                  var5x + 2.0F,
                  var6x - 16.0F,
                  70.0F,
                  MenuGlyph.SEARCH,
                  capitalize(t("gui.tools.inv.catalog.none")),
                  t("gui.tools.inv.catalog.none.desc"),
                  previewAlpha
               )
            )
         );
      }
   }

   private void buildHistory() {
      List<ItemEntry> itemEntries = module().getInventorySnapshot().method1863();
      this.listCount = itemEntries.size();
      if (itemEntries.isEmpty()) {
         this.custom(
            76.0F,
            this.entering(
               7,
               (var1x, var2x, n, var4x, f, f2, previewAlpha, zenithStyle) -> this.emptyCard(
                  var1x,
                  zenithStyle,
                  var4x + 8.0F,
                  f + 2.0F,
                  f2 - 16.0F,
                  70.0F,
                  MenuGlyph.TIMER,
                  capitalize(t("gui.tools.inv.history.none")),
                  t("gui.tools.inv.history.none.desc"),
                  previewAlpha
               )
            )
         );
      } else {
         int n = 7;

         for (ItemEntry itemEntry : itemEntries) {
            this.custom(
               22.0F,
               this.entering(
                  n++, (var2x, lastClickX, var4x, f, f2, f3, previewAlpha, zenithStyle) -> this.renderHistoryRow(var2x, lastClickX, var4x, f, f2, f3, previewAlpha, zenithStyle, itemEntry)
               )
            );
         }
      }
   }

   private void buildAside() {
      this.asideCaption("gui.tools.inv.list.presets");
      this.asideCustom(22.0F, this.entering(1, this::renderNameField));
      this.asideCustom(23.0F, this.entering(2, (var1x, var2x, lastClickY, var4x, f, f2, previewAlpha, zenithStyle) -> {
         BoundsData boundsData = new BoundsData(var4x + 8.0F, f + 2.0F, f2 - 16.0F, 17.0F);
         if (this.button(var1x, zenithStyle, "capture", boundsData, MenuGlyph.STORAGE, t("gui.tools.inv.capture"), 0, true, var2x, lastClickY, previewAlpha)) {
            HoverInfo.request(t("gui.tools.inv.capture"), t("gui.tools.inv.capture.desc"));
         }
      }), this::captureInventory, null);
      List<SborList> sborLists = presets().method1229(module().method206());
      if (sborLists.isEmpty()) {
         this.asideCustom(
            30.0F,
            this.entering(
               3,
               (menuDrawContext, var1x, var2x, f, var4x, f2, previewAlpha, zenithStyle) -> {
                  BoundsData boundsData = new BoundsData(f + 8.0F, var4x + 2.0F, f2 - 16.0F, 24.0F);
                  menuDrawContext.drawRoundedBorder(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     boundsData.width(),
                     boundsData.height(),
                     0.3F,
                     ProxyDotRadiusData.method8069(CARD_ROUND),
                     zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 3.0F)
                  );
                  Font font = Fonts.NEW_REGULAR.getFont(4.3F);
                  String clip = font.clip(t("gui.tools.inv.presets.none"), boundsData.width() - 10.0F);
                  menuDrawContext.drawText(
                     font,
                     clip,
                     boundsData.getFloatValue() + (boundsData.width() - font.width(clip)) / 2.0F,
                     boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
                  );
                  if (boundsData.contains(var1x, var2x)) {
                     HoverInfo.request(t("gui.tools.inv.presets.none"), t("gui.tools.inv.presets.none.desc"));
                  }
               }
            )
         );
      } else {
         int n = 3;

         for (SborList sborList : sborLists) {
            this.asideCustom(
               22.0F,
               this.entering(
                  n++, (var2x, lastClickX, var4x, f, f2, f3, previewAlpha, zenithStyle) -> this.renderPreset(var2x, lastClickX, var4x, f, f2, f3, previewAlpha, zenithStyle, sborList)
               ),
               () -> this.loadPreset(sborList),
               null,
               () -> this.deletePreset(sborList)
            );
         }
      }

      this.asideCaption("gui.tools.inv.caption.run");
      this.asideCustom(74.0F, this.entering(4, this::renderRunCard));
      this.buildSlotAside();
   }

   private void buildSlotAside() {
      this.asideCaption("gui.tools.inv.caption.slot");
      this.slotFrom = this.asideEntryCount();
      if (this.selectedSlot < 0) {
         this.asideCustom(
            76.0F,
            this.entering(
               6,
               (var1x, var2x, var3x, f, f2, f3, previewAlpha, zenithStyle) -> this.emptyCard(
                  var1x,
                  zenithStyle,
                  f + 8.0F,
                  f2 + 2.0F,
                  f3 - 16.0F,
                  70.0F,
                  MenuGlyph.SLOT,
                  capitalize(t("gui.tools.inv.slot.none")),
                  t("gui.tools.inv.slot.none.desc"),
                  previewAlpha
               )
            )
         );
      } else {
         int selectedSlot = this.selectedSlot;
         InventoryPreset inventoryPreset = set();
         ItemMatcher itemMatcher = inventoryPreset.method870(selectedSlot);
         this.asideCustom(
            38.0F,
            this.entering(6, (var3x, n, n2, f, f2, f3, previewAlpha, zenithStyle) -> this.renderSlotHead(var3x, f, f2, f3, previewAlpha, zenithStyle, selectedSlot, itemMatcher))
         );
         if (InventoryPreset.method885(itemMatcher)) {
            this.asideCustom(
               22.0F,
               this.entering(
                  7,
                  (var3x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
                     BoundsData[] drawStepper = this.drawStepper(
                        var3x,
                        lastClickX,
                        lastClickY,
                        f,
                        f2,
                        f3,
                        previewAlpha,
                        zenithStyle,
                        "count",
                        t("gui.tools.inv.count"),
                        String.valueOf(inventoryPreset.method877(selectedSlot)),
                        "gui.tools.inv.count.desc"
                     );
                     this.countMinus = drawStepper[0];
                     this.countPlus = drawStepper[1];
                  }
               )
            );
         }

         this.asideCustom(
            22.0F,
            this.entering(
               8,
               (var3x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
                  long l = inventoryPreset.method879(selectedSlot);
                  BoundsData[] drawStepper = this.drawStepper(
                     var3x,
                     lastClickX,
                     lastClickY,
                     f,
                     f2,
                     f3,
                     previewAlpha,
                     zenithStyle,
                     "price",
                     t("gui.tools.inv.price"),
                     l <= 0L ? t("gui.tools.inv.price.any") : String.valueOf(l),
                     "gui.tools.inv.price.desc"
                  );
                  this.priceMinus = drawStepper[0];
                  this.pricePlus = drawStepper[1];
               }
            )
         );
         if (InventoryPreset.method886(itemMatcher)) {
            this.asideCustom(
               22.0F,
               this.entering(
                  9,
                  (var3x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
                     BoundsData[] drawStepper = this.drawStepper(
                        var3x,
                        lastClickX,
                        lastClickY,
                        f,
                        f2,
                        f3,
                        previewAlpha,
                        zenithStyle,
                        "durability",
                        t("gui.tools.inv.durability"),
                        Math.round(inventoryPreset.method881(selectedSlot) * 100.0F) + "%",
                        "gui.tools.inv.durability.desc"
                     );
                     this.durabilityMinus = drawStepper[0];
                     this.durabilityPlus = drawStepper[1];
                  }
               )
            );
         }

         this.asideCustom(25.0F, this.entering(10, (var1x, var2x, var3x, f, f2, f3, previewAlpha, zenithStyle) -> {
            BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 4.0F, f3 - 16.0F, 17.0F);
            if (this.button(var1x, zenithStyle, "remove", boundsData, MenuGlyph.CLOSE, t("gui.tools.inv.remove"), 2, true, var2x, var3x, previewAlpha)) {
               HoverInfo.request(t("gui.tools.inv.remove"), t("gui.tools.inv.remove.desc"));
            }
         }), () -> {
            inventoryPreset.method875(selectedSlot);
            this.selectedSlot = -1;
            this.markDirty();
            this.invalidate();
         }, null);
      }
   }

   private void renderSetHead(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
      float centeredTextY = f2 + (22.0F - font.height()) / 2.0F;
      caption(menuDrawContext, zenithStyle, t("gui.tools.inv.caption.set"), f + 8.0F, centeredTextY, previewAlpha);
      int n3 = set().method872();
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.4F);
      String text = n3 + " / 36";
      float width = font2.width(text);
      float f4 = f + f3 - 8.0F;
      menuDrawContext.drawText(
         font2,
         text,
         f4 - width,
         f2 + (22.0F - font2.height()) / 2.0F,
         (n3 > 0 ? zenithStyle.getTextEnable() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
      );
      float width2 = 46.0F;
      progress(
         menuDrawContext,
         zenithStyle,
         f4 - width - 6.0F - width2,
         f2 + 11.0F - 1.25F,
         width2,
         2.5F,
         n3 / 36.0F,
         zenithStyle.getPrimaryColor().getZenithColor(),
         module().isEnabled(),
         previewAlpha
      );
   }

   private float slotSize() {
      return (this.mainWidth() - 16.0F - 24.0F) / 9.0F;
   }

   private float gridHeight() {
      return 4.0F * this.slotSize() + 9.0F;
   }

   private void renderGrid(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float slotSize = this.slotSize();
      float f4 = f + 8.0F;
      InventoryPreset inventoryPreset = set();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.55F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      int selectedSlot = -1;
      float[] floats = new float[36];
      float[] floats2 = new float[36];

      for (int i = 0; i < 36; i++) {
         float floatValue = f4 + i % 9 * (slotSize + 3.0F);
         float y = f2 + i / 9 * (slotSize + 3.0F);
         BoundsData boundsData = new BoundsData(floatValue, y, slotSize, slotSize);
         this.slotBounds[i] = boundsData;
         boolean contains = boundsData.contains(lastClickX, lastClickY);
         if (contains) {
            selectedSlot = i;
         }

         float hot = this.hot("slot:" + i, contains);
         float hot2 = this.hot("slotsel:" + i, i == this.selectedSlot);
         floats[i] = hot;
         floats2[i] = hot2;
         boolean flag = inventoryPreset.method871(i);
         float pressed = this.pressed("slotp:" + i);
         float f5 = pressed * 1.4F;
         float floatValue2 = floatValue + f5 / 2.0F;
         float y2 = y + f5 / 2.0F - hot * 0.6F;
         float width = slotSize - f5;
         ZenithColor zenithColor2 = flag
            ? zenithStyle.getPanelLeftBackground().getZenithColor().method7295(zenithStyle.getDisableActiveBg().getZenithColor(), hot * 0.8F)
            : cardFill(zenithStyle, hot).method7295(zenithColor.method7292(0.14F), 0.25F + hot2 * 0.5F);
         menuDrawContext.drawRoundedRect(floatValue2, y2, width, width, headerRadius, zenithColor2.method7294(previewAlpha));
         if (hot2 > 0.01F) {
            menuDrawContext.drawRoundedBorder(floatValue2, y2, width, width, 0.4F, headerRadius, zenithColor.method7292(0.85F * hot2).method7294(previewAlpha));
         } else if (!flag) {
            menuDrawContext.drawRoundedBorder(floatValue2, y2, width, width, 0.15F, headerRadius, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * (2.0F + hot * 2.0F)));
         }
      }

      HudBatchState.begin();

      try {
         for (int i = 0; i < 36; i++) {
            ItemMatcher itemMatcher = inventoryPreset.method870(i);
            if (itemMatcher != null && !itemMatcher.getItemStack().isEmpty()) {
               BoundsData slotBound = this.slotBounds[i];
               float since = this.since("place:" + i, 380L);
               float f5 = 1.0F - (1.0F - since) * (1.0F - since) * (1.0F - since);
               float f6 = f5 * (1.0F + (float)Math.sin(since * Math.PI) * 0.18F);
               float f7 = slotSize * 0.65F * f6;
               if (!(f7 < 1.0F)) {
                  float f8 = floats[i] * 0.6F;
                  this.drawItem(
                     menuDrawContext,
                     itemMatcher.getItemStack(),
                     slotBound.getFloatValue() + (slotSize - f7) / 2.0F,
                     slotBound.getFloatValue2() + (slotSize - f7) / 2.0F - f8,
                     f7,
                     previewAlpha
                  );
               }
            }
         }
      } finally {
         HudBatchState.end();
      }

      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);

      for (int i = 0; i < 36; i++) {
         BoundsData slotBound = this.slotBounds[i];
         if (inventoryPreset.method871(i)) {
            float f5 = Math.max(floats[i], floats2[i]);
            float hover = floats2[i];
            if (f5 > 0.02F) {
               this.drawPlus(
                  menuDrawContext,
                  slotBound.getFloatValue() + slotSize / 2.0F,
                  slotBound.getFloatValue2() + slotSize / 2.0F,
                  5.0F + 1.5F * hover,
                  zenithStyle.getTextTertiary().getZenithColor().method7295(zenithColor, hover).method7294(previewAlpha * f5)
               );
            }
         } else {
            int n = inventoryPreset.method877(i);
            if (n > 1) {
               String text = String.valueOf(n);
               menuDrawContext.drawText(
                  font,
                  text,
                  slotBound.getFloatValue() + slotSize - 2.0F - font.width(text),
                  slotBound.getFloatValue2() + slotSize - 2.0F - font.height(),
                  zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
               );
            }

            if (inventoryPreset.method879(i) > 0L) {
               dot(menuDrawContext, slotBound.getFloatValue() + 3.5F, slotBound.getFloatValue2() + 3.5F, 2.2F, zenithColor, 0.0F, previewAlpha);
            }
         }
      }

      if (selectedSlot >= 0) {
         HoverInfo.request(
            t("gui.tools.inv.slot") + " " + (selectedSlot + 1), t(inventoryPreset.method871(selectedSlot) ? "gui.tools.inv.slot.empty.desc" : "gui.tools.inv.slot.filled.desc")
         );
      }
   }

   private void drawPlus(MenuDrawContext menuDrawContext, float f, float f2, float width, ZenithColor zenithColor) {
      menuDrawContext.drawRect(f - width / 2.0F, f2 - 0.5F, width, 1.0F, zenithColor);
      menuDrawContext.drawRect(f - 0.5F, f2 - width / 2.0F, 1.0F, width, zenithColor);
   }

   private void renderHint(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      Font font = Fonts.NEW_REGULAR.getFont(4.4F);
      String text = fit(font, t(this.hintKey()), f3 - 16.0F - 10.0F);
      float width = font.width(text);
      float floatValue = f + (f3 - width) / 2.0F;
      boolean flag = this.selectedSlot >= 0;
      if (flag) {
         dot(menuDrawContext, floatValue - 5.0F, f2 + 6.5F, 2.4F, zenithStyle.getPrimaryColor().getZenithColor(), pulse(1.0F), previewAlpha);
      }

      menuDrawContext.drawText(
         font,
         text,
         floatValue,
         f2 + (13.0F - font.height()) / 2.0F,
         (flag ? zenithStyle.getTextSecondary() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
      );
   }

   private String hintKey() {
      if (this.selectedSlot >= 0) {
         return set().method871(this.selectedSlot) ? "gui.tools.inv.hint.slotEmpty" : "gui.tools.inv.hint.slotFilled";
      } else {
         return set().isEmpty() ? "gui.tools.inv.hint.empty" : "gui.tools.inv.hint.idle";
      }
   }

   private void renderButtons(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float f4 = f3 - 16.0F;
      float width = (f4 - 5.0F) / 2.0F;
      this.clearButton = new BoundsData(f + 8.0F, f2 + 3.0F, width, 17.0F);
      this.saveButton = new BoundsData(f + 8.0F + width + 5.0F, f2 + 3.0F, width, 17.0F);
      boolean empty = set().isEmpty();
      if (this.button(menuDrawContext, zenithStyle, "clear", this.clearButton, MenuGlyph.CLOSE, t("gui.tools.inv.clear"), 2, !empty, lastClickX, lastClickY, previewAlpha)) {
         HoverInfo.request(t("gui.tools.inv.clear"), t("gui.tools.inv.clear.desc"));
      }

      if (this.button(menuDrawContext, zenithStyle, "save", this.saveButton, MenuGlyph.CHECK, t("gui.tools.inv.presets.save"), 1, !empty, lastClickX, lastClickY, previewAlpha)) {
         HoverInfo.request(t("gui.tools.inv.presets.save"), t("gui.tools.inv.presets.save.desc"));
      }

      if (empty) {
         this.clearButton = null;
         this.saveButton = null;
      }
   }

   private void renderServerStrip(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
      caption(menuDrawContext, zenithStyle, t("gui.tools.inv.server"), f + 8.0F, f2 + (20.0F - font.height()) / 2.0F, previewAlpha);
      float width = this.serverStrip.width();
      float floatValue = f + f3 - 8.0F - width;
      float y = f2 + 4.5F;
      this.serverStripBounds = new BoundsData(floatValue, y, width, 11.0F);
      this.serverStrip.render(menuDrawContext, lastClickX, lastClickY, floatValue, y, width + 1.0F, serverIndex(), previewAlpha, zenithStyle);
   }

   private static int serverIndex() {
      String anObject = module().method206();

      for (int i = 0; i < SERVERS.length; i++) {
         if (SERVERS[i].equals(anObject)) {
            return i;
         }
      }

      return -1;
   }

   private void renderSearchField(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      this.searchField = new BoundsData(f + 8.0F, f2 + 2.0F, f3 - 16.0F, 18.0F);
      this.drawField(menuDrawContext, lastClickX, lastClickY, "search", this.searchField, this.searchInput(), MenuGlyph.SEARCH, previewAlpha, zenithStyle);
   }

   private void renderListStrip(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float y = f2 + 4.5F;
      float width = this.listStrip.width();
      this.stripBounds = new BoundsData(f + 8.0F, y, width, 11.0F);
      this.listStrip.render(menuDrawContext, lastClickX, lastClickY, f + 8.0F, y, width + 1.0F, this.listMode, previewAlpha, zenithStyle);
      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);
      String text = String.valueOf(this.listCount);
      menuDrawContext.drawText(
         font,
         text,
         f + f3 - 8.0F - font.width(text),
         f2 + (20.0F - font.height()) / 2.0F,
         zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
   }

   private void drawField(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, String str, BoundsData searchField, SearchBox searchInput, MenuGlyph menuGlyph, float previewAlpha, ZenithStyle zenithStyle) {
      boolean selected = searchInput.isSelected();
      float hot = this.hot("field:" + str, searchField.contains(lastClickX, lastClickY));
      float hover = this.hot("focus:" + str, selected);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(CARD_ROUND);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      menuDrawContext.drawRoundedRect(
         searchField.getFloatValue(),
         searchField.getFloatValue2(),
         searchField.width(),
         searchField.height(),
         headerRadius,
         zenithStyle.getPanelLeftBackground().getZenithColor().method7295(zenithStyle.getDisableActiveBg().getZenithColor(), hot * 0.4F).method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedBorder(
         searchField.getFloatValue(),
         searchField.getFloatValue2(),
         searchField.width(),
         searchField.height(),
         0.2F + hover * 0.15F,
         headerRadius,
         zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F + hot * 2.0F).method7295(zenithColor.method7292(0.7F), hover).method7294(previewAlpha)
      );
      menuGlyph.draw(
         menuDrawContext,
         searchField.getFloatValue() + 6.0F,
         searchField.getFloatValue2() + (searchField.height() - 6.0F) / 2.0F,
         zenithStyle.getTextTertiary().getZenithColor().method7295(zenithColor, hover).method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      searchInput.setFont(font);
      searchInput.setWidth(searchField.width() - 22.0F);
      searchInput.method987(
         menuDrawContext,
         searchField.getFloatValue() + 16.0F,
         searchField.getFloatValue2() + (searchField.height() - font.height()) / 2.0F,
         zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
         zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
   }

   private void renderCatalogRow(
      MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, ItemMatcher key, String name
   ) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 19.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      int n = this.inSetCounts.getOrDefault(key, 0);
      String anObject = "cat:" + System.identityHashCode(key);
      float hot = this.hot(anObject, contains);
      float hot2 = this.hot(anObject + ":on", n > 0);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      if (hot2 > 0.01F) {
         accentCard(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha, zenithColor, hot2 * 0.55F);
      } else {
         card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
      }

      float width = boundsData.height() - 6.0F;
      tile(menuDrawContext, zenithStyle, boundsData.getFloatValue() + 3.0F, boundsData.getFloatValue2() + 3.0F, width, zenithColor, hot2 * 0.5F + hot * 0.2F, previewAlpha);
      this.drawItem(
         menuDrawContext,
         key.getItemStack(),
         boundsData.getFloatValue() + 3.0F + (width - 11.0F) / 2.0F,
         boundsData.getFloatValue2() + 3.0F + (width - 11.0F) / 2.0F - hot * 0.6F,
         11.0F,
         previewAlpha
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.1F);
      float f4 = boundsData.getFloatValue() + boundsData.width() - 5.0F;
      if (n > 0) {
         String text = "×" + n;
         float pillWidth2 = pillWidth(font2, text);
         pill(menuDrawContext, font2, f4 - pillWidth2, boundsData.getFloatValue2() + (boundsData.height() - font2.height() - 3.6F) / 2.0F, text, zenithColor, previewAlpha);
         f4 -= pillWidth2 + 4.0F;
      }

      if (hot > 0.02F) {
         float width2 = 10.0F;
         float floatValue = f4 - width2;
         float y = boundsData.getFloatValue2() + (boundsData.height() - width2) / 2.0F;
         menuDrawContext.drawRoundedRect(floatValue, y, width2, width2, ProxyDotRadiusData.method8069(width2 / 2.0F), zenithColor.method7292(0.2F * hot).method7294(previewAlpha));
         this.drawPlus(menuDrawContext, floatValue + width2 / 2.0F, y + width2 / 2.0F, 4.5F, zenithColor.method7294(previewAlpha * hot));
         f4 -= width2 + 4.0F;
      }

      float floatValue = boundsData.getFloatValue() + 3.0F + width + 6.0F;
      menuDrawContext.drawText(
         font,
         font.clip(name, f4 - floatValue),
         floatValue,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(hot2, hot * 0.7F)).method7294(previewAlpha)
      );
      if (contains) {
         HoverInfo.request(name, t("gui.tools.inv.catalog.desc"));
      }
   }

   private void renderHistoryRow(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, ItemEntry itemEntry) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 19.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      card(
         menuDrawContext,
         zenithStyle,
         boundsData.getFloatValue(),
         boundsData.getFloatValue2(),
         boundsData.width(),
         boundsData.height(),
         this.hot("hist:" + System.identityHashCode(itemEntry), contains),
         previewAlpha
      );
      float width = boundsData.height() - 6.0F;
      tile(menuDrawContext, zenithStyle, boundsData.getFloatValue() + 3.0F, boundsData.getFloatValue2() + 3.0F, width, OK, 0.25F, previewAlpha);
      this.drawItem(
         menuDrawContext, itemEntry.getItemStack(), boundsData.getFloatValue() + 3.0F + (width - 11.0F) / 2.0F, boundsData.getFloatValue2() + 3.0F + (width - 11.0F) / 2.0F, 11.0F, previewAlpha
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.1F);
      String text = String.valueOf(itemEntry.getLongValue());
      float pillWidth2 = pillWidth(font2, text);
      float f4 = boundsData.getFloatValue() + boundsData.width() - 5.0F;
      pill(menuDrawContext, font2, f4 - pillWidth2, boundsData.getFloatValue2() + (boundsData.height() - font2.height() - 3.6F) / 2.0F, text, OK, previewAlpha);
      float floatValue = boundsData.getFloatValue() + 3.0F + width + 6.0F;
      String name = itemEntry.getText() + "  ×" + itemEntry.getIntValue();
      menuDrawContext.drawText(
         font,
         font.clip(name, f4 - pillWidth2 - 5.0F - floatValue),
         floatValue,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
      );
      if (contains) {
         HoverInfo.request(itemEntry.getText(), t("gui.tools.inv.history.desc"));
      }
   }

   private void renderNameField(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      this.nameField = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 18.0F);
      this.drawField(menuDrawContext, lastClickX, lastClickY, "name", this.nameField, this.nameInput(), MenuGlyph.CONFIGS, previewAlpha, zenithStyle);
   }

   private void renderPreset(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, SborList sborList) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 19.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.hot("preset:" + sborList.getName(), contains);
      card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
      float width = boundsData.height() - 6.0F;
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      tile(menuDrawContext, zenithStyle, boundsData.getFloatValue() + 3.0F, boundsData.getFloatValue2() + 3.0F, width, zenithColor, 0.35F + hot * 0.3F, previewAlpha);
      MenuGlyph.CONFIGS
         .draw(menuDrawContext, boundsData.getFloatValue() + 3.0F + (width - 6.0F) / 2.0F, boundsData.getFloatValue2() + 3.0F + (width - 6.0F) / 2.0F, zenithColor.method7294(previewAlpha));
      Font font = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.1F);
      String text = String.valueOf(sborList.size());
      float pillWidth2 = pillWidth(font2, text);
      float f4 = boundsData.getFloatValue() + boundsData.width() - 5.0F;
      pill(
         menuDrawContext,
         font2,
         f4 - pillWidth2,
         boundsData.getFloatValue2() + (boundsData.height() - font2.height() - 3.6F) / 2.0F,
         text,
         zenithStyle.getTextSecondary().getZenithColor(),
         previewAlpha
      );
      float floatValue = boundsData.getFloatValue() + 3.0F + width + 6.0F;
      menuDrawContext.drawText(
         font,
         font.clip(sborList.getName(), f4 - pillWidth2 - 5.0F - floatValue),
         floatValue,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hot).method7294(previewAlpha)
      );
      if (contains) {
         HoverInfo.request(sborList.getName(), t("gui.tools.inv.presets.desc"));
      }
   }

   private void renderRunCard(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      AutoInventoryModule autoInventoryModule = module();
      boolean enabled = autoInventoryModule.isEnabled();
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 70.0F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      accentCard(
         menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.0F, previewAlpha, zenithColor, enabled ? 0.4F + 0.2F * pulse(0.7F) : 0.1F
      );
      this.startButton = new BoundsData(boundsData.getFloatValue() + 5.0F, boundsData.getFloatValue2() + 5.0F, boundsData.width() - 10.0F, 20.0F);
      String name = t(enabled ? "gui.tools.inv.stop" : "gui.tools.inv.start");
      if (this.button(menuDrawContext, zenithStyle, "start", this.startButton, enabled ? MenuGlyph.CLOSE : MenuGlyph.CHECK, name, enabled ? 2 : 1, true, lastClickX, lastClickY, previewAlpha)) {
         HoverInfo.request(name, t(enabled ? "gui.tools.inv.stop.desc" : "gui.tools.inv.start.desc"));
      }

      if (enabled) {
         this.shimmer(menuDrawContext, this.startButton, DANGER, previewAlpha);
      }

      int n = autoInventoryModule.getIntValue4();
      int n2 = autoInventoryModule.getIntValue5();
      Font font = Fonts.NEW_REGULAR.getFont(4.3F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.3F);
      float floatValue = boundsData.getFloatValue() + 7.0F;
      float f4 = boundsData.getFloatValue() + boundsData.width() - 7.0F;
      float centeredTextY = this.startButton.getFloatValue2() + this.startButton.height() + 6.0F;
      menuDrawContext.drawText(font, t("gui.tools.inv.progress"), floatValue, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
      String text = n <= 0 ? "-" : n2 + " / " + n;
      menuDrawContext.drawText(font2, text, f4 - font2.width(text), centeredTextY, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      progress(menuDrawContext, zenithStyle, floatValue, centeredTextY + font.height() + 3.0F, f4 - floatValue, 3.0F, n <= 0 ? 0.0F : (float)n2 / n, zenithColor, enabled, previewAlpha);
      float centeredTextY2 = centeredTextY + font.height() + 11.0F;
      ZenithColor zenithColor2 = enabled ? zenithColor : zenithStyle.getTextTertiary().getZenithColor();
      dot(menuDrawContext, floatValue + 1.5F, centeredTextY2 + font.height() / 2.0F, 3.0F, zenithColor2, enabled ? pulse(1.2F) : 0.0F, previewAlpha);
      String str = autoInventoryModule.method211();
      String str2 = autoInventoryModule.method212();
      String name2 = str + (str2 != null && !str2.isBlank() ? "  ·  " + str2 : "");
      menuDrawContext.drawText(
         font,
         font.clip(name2, f4 - floatValue - 7.0F),
         floatValue + 6.0F,
         centeredTextY2,
         (enabled ? zenithStyle.getTextSecondary() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
      );
      if (new BoundsData(floatValue, centeredTextY2 - 2.0F, f4 - floatValue, font.height() + 4.0F).contains(lastClickX, lastClickY)) {
         HoverInfo.request(t("gui.tools.inv.status"), t("gui.tools.inv.status.desc"));
      }
   }

   private void renderSlotHead(MenuDrawContext menuDrawContext, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, int selectedSlot, ItemMatcher itemMatcher) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 2.0F, f3 - 16.0F, 34.0F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      accentCard(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.0F, previewAlpha, zenithColor, 0.3F);
      float width = boundsData.height() - 8.0F;
      tile(menuDrawContext, zenithStyle, boundsData.getFloatValue() + 4.0F, boundsData.getFloatValue2() + 4.0F, width, zenithColor, 0.5F, previewAlpha);
      float since = this.since("place:" + selectedSlot, 380L);
      float f4 = 16.0F * (1.0F + (float)Math.sin(since * Math.PI) * 0.12F);
      this.drawItem(
         menuDrawContext, itemMatcher.getItemStack(), boundsData.getFloatValue() + 4.0F + (width - f4) / 2.0F, boundsData.getFloatValue2() + 4.0F + (width - f4) / 2.0F, f4, previewAlpha
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      Font font3 = Fonts.NEW_MEDIUM.getFont(4.1F);
      float floatValue = boundsData.getFloatValue() + 4.0F + width + 7.0F;
      float width2 = boundsData.getFloatValue() + boundsData.width() - 6.0F - floatValue;
      String name = itemMatcher.getText2() == null ? itemMatcher.getText22() : itemMatcher.getText2();
      menuDrawContext.drawText(font, font.clip(name, width2), floatValue, boundsData.getFloatValue2() + 6.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      float centeredTextY = boundsData.getFloatValue2() + 8.0F + font.height();
      String text = t("gui.tools.inv.slot") + " " + (selectedSlot + 1);
      menuDrawContext.drawText(font2, text, floatValue, centeredTextY + 1.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
      String str = t("gui.tools.inv.have") + " " + this.haveCount;
      pill(menuDrawContext, font3, floatValue + font2.width(text) + 5.0F, centeredTextY, str, this.haveCount > 0 ? OK : zenithStyle.getTextTertiary().getZenithColor(), previewAlpha);
   }

   private BoundsData[] drawStepper(
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      float f,
      float f2,
      float f3,
      float previewAlpha,
      ZenithStyle zenithStyle,
      String str,
      String name,
      String name2,
      String key
   ) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 19.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), this.hot("step:" + str, contains), previewAlpha);
      if (contains && key != null) {
         HoverInfo.request(name, t(key));
      }

      Font font = Fonts.NEW_MEDIUM.getFont(4.9F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.8F);
      float f4 = boundsData.getFloatValue2() + boundsData.height() / 2.0F;
      float height = 13.0F;
      float floatValue = boundsData.getFloatValue() + boundsData.width() - 4.0F - 62.0F;
      menuDrawContext.drawText(
         font,
         fit(font, name, floatValue - boundsData.getFloatValue() - 10.0F),
         boundsData.getFloatValue() + 7.0F,
         f4 - font.height() / 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedRect(
         floatValue,
         f4 - height / 2.0F,
         62.0F,
         height,
         ProxyDotRadiusData.method8069(height / 2.0F),
         zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
      );
      BoundsData boundsData2 = new BoundsData(floatValue + 1.0F, f4 - 5.5F, 11.0F, 11.0F);
      BoundsData boundsData3 = new BoundsData(floatValue + 62.0F - 11.0F - 1.0F, f4 - 5.5F, 11.0F, 11.0F);
      this.stepperButton(menuDrawContext, lastClickX, lastClickY, str + ":minus", boundsData2, false, previewAlpha, zenithStyle);
      this.stepperButton(menuDrawContext, lastClickX, lastClickY, str + ":plus", boundsData3, true, previewAlpha, zenithStyle);
      float f5 = boundsData2.getFloatValue() + 11.0F;
      float f6 = boundsData3.getFloatValue() - f5;
      String text = fit(font2, name2, f6);
      float hover = Math.max(this.pressed("step:" + str + ":minus"), this.pressed("step:" + str + ":plus"));
      ZenithColor zenithColor = zenithStyle.getTextEnable().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover);
      menuDrawContext.drawText(font2, text, f5 + (f6 - font2.width(text)) / 2.0F, f4 - font2.height() / 2.0F - hover * 0.8F, zenithColor.method7294(previewAlpha));
      return new BoundsData[]{boundsData2, boundsData3};
   }

   private void stepperButton(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, String str, BoundsData boundsData, boolean flag, float previewAlpha, ZenithStyle zenithStyle) {
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.hot("sb:" + str, contains);
      float pressed = this.pressed("step:" + str);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      float f = pressed * 1.2F;
      if (hot > 0.01F || pressed > 0.01F) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue() - f / 2.0F,
            boundsData.getFloatValue2() - f / 2.0F,
            boundsData.width() + f,
            boundsData.height() + f,
            ProxyDotRadiusData.method8069((boundsData.height() + f) / 2.0F),
            zenithColor.method7292(0.14F * hot + 0.3F * pressed).method7294(previewAlpha)
         );
      }

      ZenithColor zenithColor2 = zenithStyle.getTextSecondary()
         .getZenithColor()
         .method7295(zenithColor.method7295(ZenithColor.INSTANCE, 0.3F), Math.max(hot, pressed))
         .method7294(previewAlpha);
      float f2 = boundsData.getFloatValue() + boundsData.width() / 2.0F;
      float f3 = boundsData.getFloatValue2() + boundsData.height() / 2.0F;
      menuDrawContext.drawRect(f2 - 2.5F, f3 - 0.5F, 5.0F, 1.0F, zenithColor2);
      if (flag) {
         menuDrawContext.drawRect(f2 - 0.5F, f3 - 2.5F, 1.0F, 5.0F, zenithColor2);
      }
   }

   private static String capitalize(String str) {
      return str != null && !str.isEmpty() ? str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1) : "";
   }

   private void place(ItemMatcher key) {
      InventoryPreset inventoryPreset = set();
      int selectedSlot = this.selectedSlot;
      if (selectedSlot < 0 || !inventoryPreset.method871(selectedSlot)) {
         selectedSlot = inventoryPreset.method873();
      }

      if (selectedSlot < 0) {
         ChatLogger.method2021(t("gui.tools.inv.full"));
      } else {
         inventoryPreset.method874(selectedSlot, key);
         this.mark("place:" + selectedSlot);
         this.selectedSlot = selectedSlot;
         this.haveAt = 0L;
         this.markDirty();
         this.invalidate();
      }
   }

   private void removeAll(ItemMatcher key) {
      InventoryPreset inventoryPreset = set();
      boolean flag = false;

      for (int i = 0; i < 36; i++) {
         if (inventoryPreset.method870(i) == key) {
            inventoryPreset.method875(i);
            flag = true;
         }
      }

      if (flag) {
         if (this.selectedSlot >= 0 && inventoryPreset.method871(this.selectedSlot)) {
            this.selectedSlot = -1;
         }

         this.markDirty();
         this.invalidate();
      }
   }

   private void loadPreset(SborList sborList) {
      this.selectedSlot = -1;
      int n = sborList.method1753(set());

      for (int i = 0; i < 36; i++) {
         if (!set().method871(i)) {
            this.mark("place:" + i);
         }
      }

      this.markDirty();
      this.invalidate();
      String status = t("gui.tools.inv.presets.loaded").replace("{name}", sborList.getName());
      if (n > 0) {
         ChatLogger.method2021(status + " " + t("gui.tools.inv.presets.lost").replace("{count}", String.valueOf(n)));
      } else {
         ChatLogger.method2019(status);
      }
   }

   private void deletePreset(SborList sborList) {
      if (presets().method1232(sborList)) {
         this.markDirty();
         this.invalidate();
      }
   }

   private void clearSet() {
      set().method876();
      this.selectedSlot = -1;
      this.markDirty();
      this.invalidate();
   }

   private void captureInventory() {
      if (mc.player == null) {
         ChatLogger.method2021(t("gui.tools.inv.capture.noPlayer"));
      } else {
         AutoInventoryModule.Data2 data2 = module().method208();
         this.selectedSlot = -1;
         if (data2.getIntValue() == 0) {
            this.markDirty();
            this.invalidate();
            ChatLogger.method2021(t("gui.tools.inv.capture.none"));
         } else {
            for (int i = 0; i < 36; i++) {
               if (!set().method871(i)) {
                  this.mark("place:" + i);
               }
            }

            String inputName = this.inputName();
            if (inputName.isEmpty()) {
               inputName = presets().method1233(t("gui.tools.inv.capture.name"), module().method206());
            }

            presets().method1231(SborList.method1752(inputName, set()));
            this.clearInput();
            this.markDirty();
            this.invalidate();
            String status = t("gui.tools.inv.capture.done").replace("{name}", inputName).replace("{count}", String.valueOf(data2.getIntValue()));
            int n = data2.getIntValue2() + data2.getIntValue3();
            if (n > 0) {
               status = status + " " + t("gui.tools.inv.capture.missed").replace("{count}", String.valueOf(n));
            }

            ChatLogger.method2019(status);
         }
      }
   }

   private void savePreset() {
      String inputName = this.inputName();
      if (inputName.isEmpty()) {
         ChatLogger.method2021(t("gui.tools.inv.presets.needName"));
      } else {
         InventoryPreset inventoryPreset = set();
         if (inventoryPreset.isEmpty()) {
            ChatLogger.method2021(t("gui.tools.inv.presets.needItems"));
         } else {
            boolean flag = presets().method1230(inputName, module().method206()) != null;
            presets().method1231(SborList.method1752(inputName, inventoryPreset));
            this.clearInput();
            this.markDirty();
            this.invalidate();
            ChatLogger.method2019(t(flag ? "gui.tools.inv.presets.replaced" : "gui.tools.inv.presets.saved").replace("{name}", inputName));
         }
      }
   }

   private String inputName() {
      String text = this.nameInput().getText();
      return text == null ? "" : text.trim();
   }

   private void clearInput() {
      this.nameInput().setText("");
      this.nameInput().setCursor(0);
   }

   private void markDirty() {
      Zenith.getInstance().getManager().method7788();
   }

   @Override
   public void close() {
      Manager manager = Zenith.getInstance().getManager();
      if (manager.method7790()) {
         manager.save();
      }

      super.close();
   }

   private SearchBox searchInput() {
      if (this.searchInput == null) {
         this.searchInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(4.8F), t("gui.tools.inv.search.hint"), 32.0F);
      }

      return this.searchInput;
   }

   private SearchBox nameInput() {
      if (this.nameInput == null) {
         this.nameInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(4.8F), t("gui.tools.inv.presets.hint"), 32.0F);
      }

      return this.nameInput;
   }

   private String catalogQuery() {
      String text = this.searchInput().getText();
      text = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
      return text.isEmpty() ? this.searchQuery() : text;
   }

   private void focus(SearchBox searchInput) {
      this.searchInput().setSelected(searchInput == this.searchInput);
      this.nameInput().setSelected(searchInput == this.nameInput);
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float panelAlpha, float f, float y) {
      Arrays.fill(this.slotBounds, null);
      this.stripBounds = null;
      this.serverStripBounds = null;
      this.clearButton = null;
      this.saveButton = null;
      this.startButton = null;
      this.searchField = null;
      this.nameField = null;
      this.countMinus = null;
      this.countPlus = null;
      this.priceMinus = null;
      this.pricePlus = null;
      this.durabilityMinus = null;
      this.durabilityPlus = null;
      super.render(menuDrawContext, lastClickX, lastClickY, panelAlpha, f, y);
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      if (hit(this.searchField, lastClickX, lastClickY)) {
         this.focus(this.searchInput());
         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else if (hit(this.nameField, lastClickX, lastClickY)) {
         this.focus(this.nameInput());
         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else {
         BoundsData mainViewport = this.mainViewport();
         if (mainViewport == null || !mainViewport.contains(lastClickX, lastClickY)) {
            this.focus(null);
         }

         if (enum2 == Enum3.LEFT && hit(this.startButton, lastClickX, lastClickY)) {
            BoundsData asideViewport = this.asideViewport();
            if (asideViewport == null || asideViewport.contains(lastClickX, lastClickY)) {
               this.pressButton("start", lastClickX, lastClickY);
               module().toggle();
               Zenith.getInstance().getClientSounds().method1570(false);
               this.invalidate();
               return true;
            }
         }

         if (mainViewport != null && mainViewport.contains(lastClickX, lastClickY)) {
            if (this.serverStripBounds != null && this.serverStripBounds.contains(lastClickX, lastClickY)) {
               int hovered = this.serverStrip.hovered();
               if (hovered >= 0) {
                  this.selectServer(hovered);
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            if (this.stripBounds != null && this.stripBounds.contains(lastClickX, lastClickY)) {
               int hovered = this.listStrip.hovered();
               if (hovered >= 0) {
                  if (hovered != this.listMode) {
                     this.listMode = hovered;
                     this.resetScroll();
                     this.replayMainFrom(this.listFrom);
                     this.invalidate();
                  }

                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            if (enum2 == Enum3.LEFT && hit(this.clearButton, lastClickX, lastClickY)) {
               this.pressButton("clear", lastClickX, lastClickY);
               this.clearSet();
               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            }

            if (enum2 == Enum3.LEFT && hit(this.saveButton, lastClickX, lastClickY)) {
               this.pressButton("save", lastClickX, lastClickY);
               this.savePreset();
               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            }

            for (int i = 0; i < 36; i++) {
               BoundsData slotBound = this.slotBounds[i];
               if (slotBound != null && slotBound.contains(lastClickX, lastClickY)) {
                  this.mark("slotp:" + i);
                  this.onSlotClicked(i, enum2);
                  return true;
               }
            }
         }

         return this.clickStepper(lastClickX, lastClickY) ? true : super.onMouseClicked(lastClickX, lastClickY, enum2);
      }
   }

   private void selectServer(int hovered) {
      if (hovered >= 0 && hovered < SERVERS.length && !SERVERS[hovered].equals(module().method206())) {
         module().getServer().set(SERVERS[hovered]);
         this.selectedSlot = -1;
         this.haveAt = 0L;
         this.resetScroll();
         this.replayMainFrom(this.listFrom);
         this.markDirty();
         this.invalidate();
      }
   }

   private void onSlotClicked(int selectedSlot, Enum3 enum2) {
      InventoryPreset inventoryPreset = set();
      if (enum2 == Enum3.RIGHT) {
         if (!inventoryPreset.method871(selectedSlot)) {
            inventoryPreset.method875(selectedSlot);
            if (this.selectedSlot == selectedSlot) {
               this.selectedSlot = -1;
            }

            this.markDirty();
            this.invalidate();
         }

         Zenith.getInstance().getClientSounds().method1570(true);
      } else {
         this.selectedSlot = this.selectedSlot == selectedSlot ? -1 : selectedSlot;
         this.haveAt = 0L;
         Zenith.getInstance().getClientSounds().method1570(false);
         this.replayAsideFrom(this.slotFrom);
         this.invalidate();
      }
   }

   private boolean clickStepper(double lastClickX, double lastClickY) {
      BoundsData asideViewport = this.asideViewport();
      if (asideViewport != null && !asideViewport.contains(lastClickX, lastClickY)) {
         return false;
      } else if (this.selectedSlot < 0) {
         return false;
      } else {
         InventoryPreset inventoryPreset = set();
         int selectedSlot = this.selectedSlot;
         int n = WindowUtils.method5476() ? 10 : 1;
         if (hit(this.countMinus, lastClickX, lastClickY)) {
            inventoryPreset.method878(selectedSlot, inventoryPreset.method877(selectedSlot) - n);
            return this.stepped("count:minus");
         } else if (hit(this.countPlus, lastClickX, lastClickY)) {
            inventoryPreset.method878(selectedSlot, inventoryPreset.method877(selectedSlot) + n);
            return this.stepped("count:plus");
         } else if (hit(this.priceMinus, lastClickX, lastClickY)) {
            inventoryPreset.method880(selectedSlot, inventoryPreset.method879(selectedSlot) - 1000L * n);
            return this.stepped("price:minus");
         } else if (hit(this.pricePlus, lastClickX, lastClickY)) {
            inventoryPreset.method880(selectedSlot, inventoryPreset.method879(selectedSlot) + 1000L * n);
            return this.stepped("price:plus");
         } else if (hit(this.durabilityMinus, lastClickX, lastClickY)) {
            inventoryPreset.method882(selectedSlot, inventoryPreset.method881(selectedSlot) - 0.05F);
            return this.stepped("durability:minus");
         } else if (hit(this.durabilityPlus, lastClickX, lastClickY)) {
            inventoryPreset.method882(selectedSlot, inventoryPreset.method881(selectedSlot) + 0.05F);
            return this.stepped("durability:plus");
         } else {
            return false;
         }
      }
   }

   private boolean stepped(String str) {
      this.mark("step:" + str);
      this.markDirty();
      Zenith.getInstance().getClientSounds().method1570(false);
      return true;
   }

   private static boolean hit(BoundsData searchField, double lastClickX, double lastClickY) {
      return searchField != null && searchField.contains(lastClickX, lastClickY);
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.searchInput().isSelected()) {
         if (keyCode == 256) {
            this.searchInput().setText("");
            this.searchInput().setCursor(0);
            this.searchInput().setSelected(false);
            this.resetScroll();
            this.invalidate();
            return true;
         }

         if (this.searchInput().keyPressed(keyCode, keycode, modifiers)) {
            this.resetScroll();
            this.invalidate();
            return true;
         }
      }

      if (this.nameInput().isSelected()) {
         if (keyCode == 256) {
            this.nameInput().setSelected(false);
            return true;
         }

         if (keyCode == 257 || keyCode == 335) {
            this.savePreset();
            return true;
         }

         if (this.nameInput().keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      return super.keyPressed(keyCode, keycode, modifiers);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      if (this.searchInput().isSelected() && this.searchInput().charTyped(codepoint, n)) {
         this.resetScroll();
         this.invalidate();
         return true;
      } else {
         return this.nameInput().isSelected() && this.nameInput().charTyped(codepoint, n) ? true : super.charTyped(codepoint, n);
      }
   }

   @Override
   public void tick() {
      this.refreshSetCounts();
      this.refreshHave();
      String contentSignature = module().method206()
         + "|"
         + set().method872()
         + "|"
         + this.listMode
         + "|"
         + presets().method1229(module().method206()).size()
         + "|"
         + (this.listMode == 1 ? module().getInventorySnapshot().method1863().size() : 0);
      if (!contentSignature.equals(this.contentSignature)) {
         this.contentSignature = contentSignature;
         this.invalidate();
      }
   }

   private void refreshSetCounts() {
      this.inSetCounts.clear();
      InventoryPreset inventoryPreset = set();

      for (int i = 0; i < 36; i++) {
         ItemMatcher key = inventoryPreset.method870(i);
         if (key != null && !key.getItemStack().isEmpty()) {
            this.inSetCounts.merge(key, inventoryPreset.method877(i), Integer::sum);
         }
      }
   }

   private void refreshHave() {
      if (this.selectedSlot < 0) {
         this.haveSlot = -1;
         this.haveCount = 0;
      } else {
         long now = System.currentTimeMillis();
         if (this.selectedSlot != this.haveSlot || now >= this.haveAt) {
            this.haveSlot = this.selectedSlot;
            this.haveAt = now + 400L;
            this.haveCount = 0;
            ItemMatcher itemMatcher = set().method870(this.selectedSlot);
            if (mc.player != null && itemMatcher != null) {
               for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
                  ItemStack stack = mc.player.getInventory().getItem(i);
                  if (stack != null && !stack.isEmpty() && itemMatcher.method2672(stack)) {
                     this.haveCount = this.haveCount + Math.max(1, stack.getCount());
                  }
               }
            }
         }
      }
   }
}

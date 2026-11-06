package org.zenith.client.screens.nlgui.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.joml.Quaternionf;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.builder.PlayerPreview3D;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.panel.tools.ToolTab;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.WardrobeModel;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.Rarity;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.Stage;
import org.zenith.utility.render.Thumbnails;
import org.zenith.utility.render.WardrobePreview;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class WardrobeSectionPanel extends ToolTab {
   private final TextSetting lookName = new TextSetting(
      "wardrobe.look.name", "wardrobe.look.name.desc", "", "wardrobe.look.name.empty", TextSetting.Entry.method2125(32)
   );
   private final TextSetting lookCode = new TextSetting(
      "wardrobe.look.code", "wardrobe.look.code.desc", "", "wardrobe.look.code.empty", TextSetting.Entry.method2125(8)
   );
   private String lookStatus = "";
   private String lookShared = "";
   private static final int TAB_PETS = 0;
   private static final int TAB_CLOTHING = 1;
   private static final int TAB_ACCESSORIES = 2;
   private static final int TAB_SKINS = 3;
   private static final int TAB_WEAPONS = 4;
   private static final float SUB_ROW = 22.0F;
   private static final FocusSlotEnum[] DRESS = new FocusSlotEnum[]{
      FocusSlotEnum.HEAD, FocusSlotEnum.BODY, FocusSlotEnum.LEGS, FocusSlotEnum.BACK, FocusSlotEnum.CAPE
   };
   private final ChipStrip clothingSubs = new ChipStrip(
      "wardrobe.sub.all", "wardrobe.slot.head", "wardrobe.slot.body", "wardrobe.sub.legs", "wardrobe.slot.back", "wardrobe.sub.capes", "wardrobe.panel.sets"
   );
   private final ChipStrip accessorySubs = new ChipStrip("wardrobe.sub.all", "wardrobe.slot.watch", "wardrobe.sub.bracelets", "wardrobe.sub.neck");
   private final ChipStrip skinSubs = new ChipStrip("wardrobe.sub.all", "wardrobe.panel.characters", "wardrobe.panel.politics");
   private final ChipStrip weaponSubs = new ChipStrip(
      "wardrobe.sub.all",
      "wardrobe.slot.sword",
      "wardrobe.slot.axe",
      "wardrobe.slot.bow",
      "wardrobe.slot.mace",
      "wardrobe.slot.spear",
      "wardrobe.slot.crossbow"
   );
   private final ChipStrip itemSubs = new ChipStrip(
      "wardrobe.sub.all", "wardrobe.slot.gapple", "wardrobe.slot.enchanted_gapple", "wardrobe.slot.totem", "wardrobe.slot.anchor", "wardrobe.slot.crystal"
   );
   private final int[] subs = new int[8];
   private BoundsData subBounds;
   private ChipStrip shownSubs;
   private static final int TAB_ITEMS = 5;
   private static final int TAB_PRESETS = 6;
   private static final int TAB_SETTINGS = 7;
   private static final float SAVE_CARD = 62.0F;
   private static final float PRESET_ROW = 34.0F;
   private static final float TOGGLE_ROW = 30.0F;
   private static final float SLIDER_ROW = 36.0F;
   private SearchBox presetName;
   private BoundsData presetField;
   private BoundsData presetSave;
   private String presetStatus = "";
   private boolean presetStatusError;
   private long presetStatusAt;
   private String armedDelete = "";
   private long armedAt;
   private final Map<String, BoundsData> deleteBounds = new HashMap<>();
   private final Map<SliderSetting, BoundsData> sliderTracks = new HashMap<>();
   private SliderSetting draggingSlider;
   private static final String POLITICS = "politics";
   private static final FocusSlotEnum[] ACCESSORIES = new FocusSlotEnum[]{FocusSlotEnum.WATCH, FocusSlotEnum.NECK};
   private static final FocusSlotEnum[] WEAPONS = new FocusSlotEnum[]{
      FocusSlotEnum.SWORD, FocusSlotEnum.AXE, FocusSlotEnum.BOW, FocusSlotEnum.MACE, FocusSlotEnum.SPEAR, FocusSlotEnum.CROSSBOW
   };
   private static final FocusSlotEnum[] ITEMS = new FocusSlotEnum[]{
      FocusSlotEnum.GAPPLE, FocusSlotEnum.ENCHANTED_GAPPLE, FocusSlotEnum.TOTEM, FocusSlotEnum.ANCHOR, FocusSlotEnum.CRYSTAL
   };
   private static final int COLUMNS = 3;
   private static final float CARD_GAP = 5.0F;
   private static final float CARD_HEIGHT = 84.0F;
   private static final float CARD_LABEL = 19.0F;
   private static final float CARD_RADIUS = 6.0F;
   private static final float CAPTION_BAND = 14.0F;
   private static final float DEFAULT_FOCUS = 1.05F;
   private static final float LEGACY_BASE = 66.0F;
   private float autoZoom = 1.1F;
   private float previewFit = 66.0F;
   private final Thumbnails thumbnails = new Thumbnails();
   private final Stage stage = new Stage();
   private FocusSlotEnum focusSlot;
   private float stageYaw = 210.0F;
   private boolean stageDrag;
   private long stageFrame;
   private float stageX0;
   private float stageY0;
   private float stageX1;
   private float stageY1;
   private final Map<String, Animation> lift = new HashMap<>();
   private final Map<String, Animation> worn = new HashMap<>();
   private PlayerPreview3D preview;
   private HoveredData hovered;
   private int lastVersion = -1;
   private String lastSearch = "";
   private final Map<String, Animation> slides = new HashMap<>();
   private static final float WATCH_YAW = -90.0F;

   @Override
   public boolean pressFeedback() {
      return false;
   }

   @Override
   public float entryEnter(int n, boolean flag) {
      return !flag && this.getActiveTab() == 7 ? super.entryEnter(n, false) : 1.0F;
   }

   @Override
   public String headerTitle() {
      return t("wardrobe.tab");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.COSMETICS;
   }

   @Override
   public String sectionDescription() {
      return "wardrobe.desc";
   }

   @Override
   public String emptyText() {
      return "wardrobe.panel.empty";
   }

   private static Wardrobe manager() {
      return Wardrobe.method2488();
   }

   @Override
   public void tick() {
      Wardrobe wardrobe = manager();
      if (wardrobe != null) {
         String searchQuery = this.searchQuery();
         if (wardrobe.version() != this.lastVersion || !searchQuery.equals(this.lastSearch)) {
            this.lastVersion = wardrobe.version();
            this.lastSearch = searchQuery;
            this.invalidate();
         }
      }
   }

   @Override
   public void selectTab(int activeTab) {
      if (this.preview != null) {
         this.preview.resetRotation();
      }

      this.lift.clear();
      this.hovered = null;
      this.focusSlot = null;
      this.stage.reset();
      super.selectTab(activeTab);
      this.shown();
   }

   @Override
   public void build() {
      this.tabs(
         new String[]{
            "wardrobe.category.pet",
            "wardrobe.category.clothing",
            "wardrobe.category.accessory",
            "wardrobe.category.character",
            "wardrobe.category.weapon",
            "wardrobe.category.item",
            "wardrobe.panel.looks",
            "wardrobe.panel.settings"
         }
      );
      this.tabIcons(
         new MenuGlyph[]{
            MenuGlyph.PVE, MenuGlyph.CHESTPLATE, MenuGlyph.TIMER, MenuGlyph.PROFILE, MenuGlyph.COMBAT, MenuGlyph.POTION, MenuGlyph.CONFIGS, MenuGlyph.INTERFACE
         }
      );
      Wardrobe wardrobe = manager();
      if (wardrobe != null) {
         switch (this.getActiveTab()) {
            case 0:
               this.section(wardrobe, FocusSlotEnum.PET, true);
               break;
            case 1:
               this.subStrip(this.clothingSubs);
               int sub = this.subs[1];
               if (sub == 0) {
                  for (FocusSlotEnum key : DRESS) {
                     this.section(wardrobe, key, false);
                  }

                  this.buildSets(wardrobe);
               } else if (sub <= DRESS.length) {
                  this.section(wardrobe, DRESS[sub - 1], false);
                  if (DRESS[sub - 1] == FocusSlotEnum.CAPE) {
                     this.caption("wardrobe.panel.capeHint");
                  }
               } else {
                  this.buildSets(wardrobe);
               }
               break;
            case 2:
               this.subStrip(this.accessorySubs);
               int n = this.subs[2];
               if (n == 0 || n == 1) {
                  this.section(wardrobe, FocusSlotEnum.WATCH, "wardrobe.slot.watch", hoveredData -> !isBracelet(hoveredData));
               }

               if (n == 0 || n == 2) {
                  this.section(wardrobe, FocusSlotEnum.WATCH, "wardrobe.sub.bracelets", WardrobeSectionPanel::isBracelet);
               }

               if (n == 0 || n == 3) {
                  this.section(wardrobe, FocusSlotEnum.NECK, false);
               }
               break;
            case 3:
               this.subStrip(this.skinSubs);
               int sub2 = this.subs[3];
               if (sub2 != 2) {
                  this.section(wardrobe, FocusSlotEnum.CHARACTER, "wardrobe.panel.characters", false);
               }

               if (sub2 != 1) {
                  this.section(wardrobe, FocusSlotEnum.CHARACTER, "wardrobe.panel.politics", true);
               }

               this.caption("wardrobe.panel.skinHides");
               break;
            case 4:
               this.subStrip(this.weaponSubs);
               int sub3 = this.subs[4];
               if (sub3 == 0) {
                  for (FocusSlotEnum key : WEAPONS) {
                     this.section(wardrobe, key, false);
                  }
               } else {
                  this.section(wardrobe, WEAPONS[Math.min(sub3, WEAPONS.length) - 1], false);
               }
               break;
            case 5:
               this.subStrip(this.itemSubs);
               int sub4 = this.subs[5];
               if (sub4 == 0) {
                  for (FocusSlotEnum key : ITEMS) {
                     this.section(wardrobe, key, false);
                  }
               } else {
                  this.section(wardrobe, ITEMS[Math.min(sub4, ITEMS.length) - 1], false);
               }
               break;
            case 6:
            default:
               this.buildPresets(wardrobe);
               break;
            case 7:
               this.buildSettings(wardrobe);
         }

         this.buildAside(wardrobe);
      }
   }

   private void subStrip(ChipStrip shownSubs) {
      this.shownSubs = shownSubs;
      int activeTab = this.getActiveTab();
      this.custom(22.0F, (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
         float width = f3 - 16.0F;
         float y = f2 + (22.0F - shownSubs.height(width)) / 2.0F;
         this.subBounds = new BoundsData(f + 8.0F, y, width, shownSubs.height(width));
         shownSubs.render(menuDrawContext, lastClickX, lastClickY, f + 8.0F, y, width, this.subs[activeTab], previewAlpha, zenithStyle);
      });
   }

   private FocusSlotEnum subSlot() {
      int activeTab = this.getActiveTab();
      int sub = activeTab >= 0 && activeTab < this.subs.length ? this.subs[activeTab] : 0;
      if (sub <= 0) {
         return null;
      } else {
         return switch (activeTab) {
            case 1 -> sub <= DRESS.length ? DRESS[sub - 1] : null;
            case 2 -> sub == 3 ? FocusSlotEnum.NECK : FocusSlotEnum.WATCH;
            default -> null;
            case 4 -> sub <= WEAPONS.length ? WEAPONS[sub - 1] : null;
            case 5 -> sub <= ITEMS.length ? ITEMS[sub - 1] : null;
         };
      }
   }

   private static boolean isBracelet(HoveredData hoveredData) {
      String text = hoveredData.getText();
      return "bracelet".equals(hoveredData.getText4()) || text.startsWith("watch_tennis") || text.contains("bracelet");
   }

   private void section(Wardrobe wardrobe, FocusSlotEnum key, String str, Predicate<HoveredData> predicate) {
      var hoveredDatas = this.filter(wardrobe, key);
      hoveredDatas.removeIf(var1x -> !predicate.test(var1x));
      HoveredData hoveredData = wardrobe.method2491(key);
      boolean flag = hoveredData != null && predicate.test(hoveredData);
      this.caption(t(str) + " · " + hoveredDatas.size() + (flag ? " · " + t(hoveredData.method1547()) : ""));
      this.cards(wardrobe, hoveredDatas);
   }

   private void section(Wardrobe wardrobe, FocusSlotEnum key, boolean flag) {
      var hoveredDatas = this.filter(wardrobe, key);
      String str = flag ? t(key.getWardrobeCategory().key()) : t(key.key());
      HoveredData hoveredData = wardrobe.method2491(key);
      this.caption(str + " · " + hoveredDatas.size() + (hoveredData != null ? " · " + t(hoveredData.method1547()) : ""));
      this.cards(wardrobe, hoveredDatas);
   }

   private void section(Wardrobe wardrobe, FocusSlotEnum key, String str, boolean flag) {
      var hoveredDatas = this.filter(wardrobe, key);
      hoveredDatas.removeIf(var1x -> "politics".equals(var1x.getText4()) != flag);
      HoveredData hoveredData = wardrobe.method2491(key);
      boolean flag2 = hoveredData != null && "politics".equals(hoveredData.getText4()) == flag;
      this.caption(t(str) + " · " + hoveredDatas.size() + (flag2 ? " · " + t(hoveredData.method1547()) : ""));
      this.cards(wardrobe, hoveredDatas);
   }

   private void cards(Wardrobe wardrobe, List<HoveredData> hoveredDatas) {
      if (hoveredDatas.isEmpty()) {
         this.info("wardrobe.panel.soon", () -> "", "wardrobe.panel.soon.desc");
         this.gap(5.0F);
      } else {
         this.grid(wardrobe, hoveredDatas);
         this.gap(5.0F);
      }
   }

   private List<HoveredData> filter(Wardrobe wardrobe, FocusSlotEnum key) {
      String searchQuery = this.searchQuery();
      ArrayList<org.zenith.utility.misc.HoveredData> hoveredDatas = new ArrayList<>();

      for (HoveredData hoveredData : wardrobe.getRegistry().all()) {
         if (hoveredData.getFocusSlotEnum() == key
            && (searchQuery.isEmpty() || t(hoveredData.method1547()).toLowerCase(Locale.ROOT).contains(searchQuery) || hoveredData.getText().contains(searchQuery))) {
            hoveredDatas.add(hoveredData);
         }
      }

      hoveredDatas.sort((org.zenith.utility.misc.HoveredData hoveredData, org.zenith.utility.misc.HoveredData var1x) -> Integer.compare(var1x.getRarity().getIntValue2(), hoveredData.getRarity().getIntValue2()));
      return hoveredDatas;
   }

   private void buildSets(Wardrobe wardrobe) {
      List<WardrobeSetInfo> wardrobeSetInfos = wardrobe.getRegistry().method1426();
      if (wardrobeSetInfos.isEmpty()) {
         this.caption("wardrobe.panel.sets");
         this.info("wardrobe.panel.soon", () -> "", "wardrobe.panel.soon.desc");
      } else {
         this.caption(t("wardrobe.panel.sets") + " · " + wardrobeSetInfos.size());

         for (WardrobeSetInfo wardrobeSetInfo : wardrobeSetInfos) {
            this.row(wardrobeSetInfo.method7305())
               .icon(MenuGlyph.COSMETICS)
               .value(() -> wardrobeSetInfo.pieces().size() + " " + t("gui.unit.pieces"))
               .description(wardrobeSetInfo.method7306())
               .selected(() -> wardrobe.method2500(wardrobe.method2498(null, true)) == wardrobeSetInfo)
               .onClick(() -> {
                  wardrobe.method2495(wardrobeSetInfo);
                  this.invalidate();
               })
               .add();
         }
      }
   }

   private void buildWorn(Wardrobe wardrobe) {
      ArrayList arrayList = new ArrayList();

      for (FocusSlotEnum key : FocusSlotEnum.values()) {
         HoveredData hoveredData = wardrobe.method2491(key);
         if (hoveredData != null) {
            arrayList.add(hoveredData);
         }
      }

      if (!arrayList.isEmpty()) {
         this.caption(t("wardrobe.panel.worn") + " · " + arrayList.size());
         this.grid(wardrobe, arrayList);
         this.gap(5.0F);
         this.action("wardrobe.panel.undressAll", MenuGlyph.CLOSE, () -> arrayList.size() + " " + t("gui.unit.pieces"), () -> {
            wardrobe.method2496();
            this.invalidate();
         }, "wardrobe.panel.localOnly");
      }
   }

   private void buildLooks(Wardrobe wardrobe, List<HoveredData> hoveredDatas) {
      Cloud cloud = Zenith.getInstance().getCloud();
      this.caption("wardrobe.look.caption");
      if (cloud != null && cloud.method7860()) {
         PartyMember partyMember = cloud.getPartyMember();
         if (!hoveredDatas.isEmpty()) {
            this.setting(this.lookName);
            this.action("wardrobe.look.share", MenuGlyph.COPY, () -> this.lookShared.isEmpty() ? t("wardrobe.look.share.value") : this.lookShared, () -> {
               var var3x = new ArrayList<>(wardrobe.method2504().values());
               this.lookStatus = t("wardrobe.look.saving");
               partyMember.method1167(this.lookName.getValue() == null ? "" : this.lookName.getValue(), var3x, var1xx -> {
                  this.lookShared = var1xx;
                  this.lookStatus = t("wardrobe.look.copied").replace("{c}", var1xx);
                  if (mc.keyboardHandler != null) {
                     mc.keyboardHandler.setClipboard(var1xx);
                  }

                  this.invalidate();
               });
               this.invalidate();
            }, "wardrobe.look.share.desc");
         }

         this.setting(this.lookCode);
         this.action(
            "wardrobe.look.apply",
            MenuGlyph.ACTION,
            () -> t("wardrobe.look.apply.value"),
            () -> {
               String var3x = this.lookCode.getValue() == null ? "" : this.lookCode.getValue().strip();
               if (var3x.length() < 4) {
                  this.lookStatus = t("wardrobe.look.badCode");
                  this.invalidate();
               } else {
                  this.lookStatus = t("wardrobe.look.loading");
                  partyMember.method1168(
                     var3x,
                     var2xx -> {
                        if (var2xx == null) {
                           this.lookStatus = t("wardrobe.look.missing");
                        } else {
                           int var3xx = wardrobe.method2503(var2xx.getItems());
                           this.lookStatus = t("wardrobe.look.applied")
                              .replace("{n}", String.valueOf(var3xx))
                              .replace("{o}", var2xx.getText3().isEmpty() ? "?" : var2xx.getText3())
                              .replace("{t}", var2xx.name().isEmpty() ? var2xx.getText() : var2xx.name());
                        }

                        this.invalidate();
                     }
                  );
                  this.invalidate();
               }
            },
            "wardrobe.look.apply.desc"
         );
         if (!this.lookStatus.isEmpty()) {
            String lookStatus = this.lookStatus;
            this.info(lookStatus, () -> "", "wardrobe.look.status.desc");
         }
      } else {
         this.info("wardrobe.look.offline", () -> "", "wardrobe.look.offline.desc");
      }
   }

   private void buildSettings(Wardrobe wardrobe) {
      this.caption("wardrobe.settings.group.display");
      this.toggleCard(wardrobe.showSelf);
      this.toggleCard(wardrobe.showOthers);
      this.toggleCard(wardrobe.armorOnSkins);
      this.caption("wardrobe.settings.group.effects");
      this.toggleCard(wardrobe.particles);
      this.sliderCard(wardrobe.particleBudget);
      this.caption("wardrobe.settings.group.pet");
      this.sliderCard(wardrobe.petScale);
   }

   private void toggleCard(BooleanSetting showSelf) {
      this.custom(
         30.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 26.0F);
            boolean contains = boundsData.contains(lastClickX, lastClickY);
            float hot = this.hot("tg:" + showSelf.getKey(), contains);
            card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
            float width = 15.0F;
            float height = 8.0F;
            float floatValue = boundsData.getFloatValue() + boundsData.width() - 8.0F - width;
            this.drawSwitch(menuDrawContext, showSelf.getKey(), floatValue, boundsData.getFloatValue2() + (boundsData.height() - height) / 2.0F, width, height, showSelf.isEnabled(), previewAlpha, zenithStyle);
            Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
            Font font2 = Fonts.NEW_REGULAR.getFont(4.1F);
            float width2 = floatValue - boundsData.getFloatValue() - 16.0F;
            menuDrawContext.drawText(
               font,
               font.clip(showSelf.getName(), width2),
               boundsData.getFloatValue() + 8.0F,
               boundsData.getFloatValue2() + 5.0F,
               zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), 0.3F + hot * 0.7F).method7294(previewAlpha)
            );
            menuDrawContext.drawText(
               font2,
               font2.clip(showSelf.getDescription(), width2),
               boundsData.getFloatValue() + 8.0F,
               boundsData.getFloatValue2() + 7.0F + font.height(),
               zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         },
         () -> {
            showSelf.toggle();
            this.invalidate();
         },
         null
      );
   }

   private void sliderCard(SliderSetting particleBudget) {
      this.custom(
         36.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 32.0F);
            boolean contains = boundsData.contains(lastClickX, lastClickY);
            boolean flag = this.draggingSlider == particleBudget;
            float hot = this.hot("sl:" + particleBudget.getKey(), contains || flag);
            card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
            Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(4.4F);
            ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
            String text = format(particleBudget);
            float pillWidth2 = pillWidth(font2, text);
            pill(menuDrawContext, font2, boundsData.getFloatValue() + boundsData.width() - 8.0F - pillWidth2, boundsData.getFloatValue2() + 4.5F, text, zenithColor, previewAlpha);
            menuDrawContext.drawText(
               font,
               font.clip(particleBudget.getName(), boundsData.width() - pillWidth2 - 24.0F),
               boundsData.getFloatValue() + 8.0F,
               boundsData.getFloatValue2() + 5.5F,
               zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), 0.3F + hot * 0.7F).method7294(previewAlpha)
            );
            float floatValue = boundsData.getFloatValue() + 8.0F;
            float width = boundsData.width() - 16.0F;
            float y = boundsData.getFloatValue2() + boundsData.height() - 9.0F;
            float f4 = particleBudget.getMax() - particleBudget.getMin();
            float floatValue2 = f4 <= 0.0F ? 0.0F : (particleBudget.getValue3() - particleBudget.getMin()) / f4;
            float hotValue = this.hotValue("slv:" + particleBudget.getKey(), floatValue2);
            this.sliderTracks.put(particleBudget, new BoundsData(floatValue - 3.0F, y - 5.0F, width + 6.0F, 12.0F));
            menuDrawContext.drawRoundedRect(
               floatValue, y, width, 2.5F, ProxyDotRadiusData.method8069(1.25F), zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * 1.4F)
            );
            menuDrawContext.drawRoundedRect(floatValue, y, Math.max(2.5F, width * hotValue), 2.5F, ProxyDotRadiusData.method8069(1.25F), zenithColor.method7294(previewAlpha));
            float width2 = 6.0F + hot * 1.5F;
            float floatValue3 = floatValue + width * hotValue - width2 / 2.0F;
            menuDrawContext.drawRoundedRect(
               floatValue3,
               y + 1.25F - width2 / 2.0F,
               width2,
               width2,
               ProxyDotRadiusData.method8069(width2 / 2.0F),
               ZenithColor.INSTANCE.method7295(zenithColor, 0.15F).method7294(previewAlpha)
            );
            if (contains) {
               HoverInfo.request(particleBudget.getName(), particleBudget.getDescription());
            }
         }
      );
   }

   private float hotValue(String key, float floatValue) {
      Animation animation = this.slides.computeIfAbsent(key, var1x -> new Animation(140L, floatValue, Easing.INSTANCE12));
      return animation.animateTo(floatValue);
   }

   private static String format(SliderSetting particleBudget) {
      float f = particleBudget.getValue3();
      String suffix = particleBudget.getSuffix() == null ? "" : particleBudget.getSuffix();
      String str = particleBudget.getFloatValue2() >= 1.0F ? String.valueOf(Math.round(f)) : String.format(Locale.ROOT, "%.2f", f);
      return str + suffix;
   }

   private void slide(SliderSetting draggingSlider, double lastClickX) {
      BoundsData sliderTrack = this.sliderTracks.get(draggingSlider);
      if (sliderTrack != null) {
         float f = sliderTrack.getFloatValue() + 3.0F;
         float f2 = sliderTrack.width() - 6.0F;
         float f3 = Math.max(0.0F, Math.min(1.0F, (float)((lastClickX - f) / f2)));
         float f4 = draggingSlider.getMin() + (draggingSlider.getMax() - draggingSlider.getMin()) * f3;
         float f5 = draggingSlider.getFloatValue2() <= 0.0F ? 0.01F : draggingSlider.getFloatValue2();
         float f6 = Math.round(f4 / f5) * f5;
         draggingSlider.method2400(Math.max(draggingSlider.getMin(), Math.min(draggingSlider.getMax(), f6)));
      }
   }

   private SearchBox presetInput() {
      if (this.presetName == null) {
         this.presetName = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(4.8F), t("wardrobe.presets.name.hint"), 32.0F);
      }

      return this.presetName;
   }

   private void buildPresets(Wardrobe wardrobe) {
      this.custom(
         66.0F, (var2x, lastClickX, var4x, f, f2, var7x, var8x, var9x) -> this.renderSaveCard(var2x, wardrobe, lastClickX, var4x, f, f2, var7x, var8x, var9x)
      );
      this.buildWorn(wardrobe);
      List<Wardrobe.Data> datas = wardrobe.method2505();
      this.caption(t("wardrobe.presets.caption") + " · " + datas.size());
      if (datas.isEmpty()) {
         this.custom(
            78.0F,
            (var1x, var2x, n, var4x, f, f2, var7x, var8x) -> this.emptyCard(
               var1x,
               var8x,
               var4x + 8.0F,
               f + 2.0F,
               f2 - 16.0F,
               72.0F,
               MenuGlyph.CONFIGS,
               t("wardrobe.presets.empty"),
               t("wardrobe.presets.empty.desc"),
               var7x
            )
         );
      } else {
         for (Wardrobe.Data data : datas) {
            this.custom(
               34.0F,
               (menuDrawContext, var4x, lastClickY, f, var7x, var8x, var9x, zenithStyle) -> this.renderPreset(menuDrawContext, wardrobe, data, var4x, lastClickY, f, var7x, var8x, var9x, zenithStyle),
               () -> {
                  int n = wardrobe.method2508(data);
                  this.status(t("wardrobe.presets.applied").replace("{n}", data.name()) + " · " + n, false);
                  this.invalidate();
               },
               () -> wardrobe.method2510(data)
            );
         }
      }

      ArrayList arrayList = new ArrayList();

      for (FocusSlotEnum key : FocusSlotEnum.values()) {
         HoveredData hoveredData = wardrobe.method2491(key);
         if (hoveredData != null) {
            arrayList.add(hoveredData);
         }
      }

      this.gap(GuiStyle.PADDING.intValue());
      this.buildLooks(wardrobe, arrayList);
   }

   private void status(String presetStatus, boolean presetStatusError) {
      this.presetStatus = presetStatus;
      this.presetStatusError = presetStatusError;
      this.presetStatusAt = System.currentTimeMillis();
   }

   private void renderSaveCard(MenuDrawContext menuDrawContext, Wardrobe wardrobe, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 2.0F, f3 - 16.0F, 62.0F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      accentCard(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.0F, previewAlpha, zenithColor, 0.18F);
      int n = wardrobe.method2497();
      Font font = Fonts.NEW_SEMIBOLD.getFont(5.8F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
      float floatValue = boundsData.getFloatValue() + 8.0F;
      menuDrawContext.drawText(font, t("wardrobe.presets.current"), floatValue, boundsData.getFloatValue2() + 7.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      String str = n == 0 ? t("wardrobe.presets.nothing") : t("wardrobe.presets.currentCount").replace("{n}", String.valueOf(n));
      long l = System.currentTimeMillis() - this.presetStatusAt;
      boolean flag = !this.presetStatus.isEmpty() && l < 5000L;
      ZenithColor zenithColor2 = flag ? (this.presetStatusError ? DANGER : OK) : zenithStyle.getTextTertiary().getZenithColor();
      float f4 = flag && l > 4000L ? 1.0F - (float)(l - 4000L) / 1000.0F : 1.0F;
      menuDrawContext.drawText(
         font2,
         font2.clip(flag ? this.presetStatus : str, boundsData.width() - 16.0F),
         floatValue,
         boundsData.getFloatValue2() + 9.0F + font.height(),
         zenithColor2.method7294(previewAlpha * f4)
      );
      float y = boundsData.getFloatValue2() + boundsData.height() - 24.0F;
      Font font3 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String name = t("wardrobe.presets.save");
      float width = font3.width(name) + 26.0F;
      this.presetSave = new BoundsData(boundsData.getFloatValue() + boundsData.width() - 7.0F - width, y, width, 17.0F);
      this.presetField = new BoundsData(floatValue - 1.0F, y, this.presetSave.getFloatValue() - 5.0F - (floatValue - 1.0F), 17.0F);
      SearchBox presetInput = this.presetInput();
      boolean selected = presetInput.isSelected();
      float hot = this.hot("pf:focus", selected);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(CARD_ROUND);
      menuDrawContext.drawRoundedRect(
         this.presetField.getFloatValue(),
         this.presetField.getFloatValue2(),
         this.presetField.width(),
         this.presetField.height(),
         headerRadius,
         zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedBorder(
         this.presetField.getFloatValue(),
         this.presetField.getFloatValue2(),
         this.presetField.width(),
         this.presetField.height(),
         0.2F + hot * 0.15F,
         headerRadius,
         zenithStyle.getFieldBorder().getZenithColor().method7294(2.5F).method7295(zenithColor.method7292(0.7F), hot).method7294(previewAlpha)
      );
      MenuGlyph.TEXT
         .draw(
            menuDrawContext,
            this.presetField.getFloatValue() + 5.0F,
            this.presetField.getFloatValue2() + 5.5F,
            zenithStyle.getTextTertiary().getZenithColor().method7295(zenithColor, hot).method7294(previewAlpha)
         );
      Font font4 = Fonts.NEW_MEDIUM.getFont(4.8F);
      presetInput.setFont(font4);
      presetInput.setWidth(this.presetField.width() - 20.0F);
      presetInput.method987(
         menuDrawContext,
         this.presetField.getFloatValue() + 15.0F,
         this.presetField.getFloatValue2() + (17.0F - font4.height()) / 2.0F,
         zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
         zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      if (this.button(menuDrawContext, zenithStyle, "presetSave", this.presetSave, MenuGlyph.CHECK, name, 1, n > 0, lastClickX, lastClickY, previewAlpha)) {
         HoverInfo.request(name, t("wardrobe.presets.save.desc"));
      }

      if (n == 0) {
         this.presetSave = null;
      }
   }

   private void renderPreset(
      MenuDrawContext menuDrawContext, Wardrobe wardrobe, Wardrobe.Data data, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle
   ) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 30.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      boolean flag = wardrobe.method2510(data);
      String anObject = "pr:" + data.name() + ":" + data.getLongValue();
      float hot = this.hot(anObject, contains);
      float hot2 = this.hot(anObject + ":on", flag);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      if (hot2 > 0.01F) {
         accentCard(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha, zenithColor, hot2 * 0.6F);
      } else {
         card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
      }

      ZenithColor zenithColor2 = zenithColor;
      int n = -1;
      ArrayList elements = new ArrayList();

      for (String string : data.getItems().values()) {
         HoveredData hoveredData = wardrobe.getRegistry().method1421(string);
         if (hoveredData != null) {
            elements.add(t(hoveredData.method1547()));
            if (hoveredData.getRarity().getIntValue2() > n) {
               n = hoveredData.getRarity().getIntValue2();
               zenithColor2 = color(hoveredData.getRarity().getIntValue());
            }
         }
      }

      float width = boundsData.height() - 8.0F;
      tile(menuDrawContext, zenithStyle, boundsData.getFloatValue() + 4.0F, boundsData.getFloatValue2() + 4.0F, width, zenithColor2, 0.45F + hot * 0.2F, previewAlpha);
      MenuGlyph.COSMETICS
         .draw(menuDrawContext, boundsData.getFloatValue() + 4.0F + (width - 6.0F) / 2.0F, boundsData.getFloatValue2() + 4.0F + (width - 6.0F) / 2.0F, zenithColor2.method7294(previewAlpha));
      Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.1F);
      Font font3 = Fonts.NEW_MEDIUM.getFont(4.0F);
      float f4 = boundsData.getFloatValue() + boundsData.width() - 6.0F;
      float width2 = 11.0F;
      BoundsData boundsData2 = new BoundsData(f4 - width2, boundsData.getFloatValue2() + (boundsData.height() - width2) / 2.0F, width2, width2);
      this.deleteBounds.put(anObject, boundsData2);
      boolean flag2 = this.armedDelete.equals(anObject) && System.currentTimeMillis() - this.armedAt < 3000L;
      boolean contains2 = boundsData2.contains(lastClickX, lastClickY);
      float hot3 = this.hot(anObject + ":del", contains2 || flag2);
      if (hot > 0.02F || flag2) {
         menuDrawContext.drawRoundedRect(
            boundsData2.getFloatValue(),
            boundsData2.getFloatValue2(),
            width2,
            width2,
            ProxyDotRadiusData.method8069(width2 / 2.0F),
            DANGER.method7292(0.12F + 0.25F * hot3).method7294(previewAlpha * Math.max(hot, flag2 ? 1.0F : 0.0F))
         );
         MenuGlyph.CLOSE
            .draw(
               menuDrawContext,
               boundsData2.getFloatValue() + (width2 - 6.0F) / 2.0F,
               boundsData2.getFloatValue2() + (width2 - 6.0F) / 2.0F,
               DANGER.method7294(previewAlpha * Math.max(hot, flag2 ? 1.0F : 0.0F))
            );
         f4 = boundsData2.getFloatValue() - 4.0F;
      }

      if (flag2) {
         String text = t("wardrobe.presets.again");
         float pillWidth2 = pillWidth(font3, text);
         pill(menuDrawContext, font3, f4 - pillWidth2, boundsData.getFloatValue2() + (boundsData.height() - font3.height() - 3.6F) / 2.0F, text, DANGER, previewAlpha);
         f4 -= pillWidth2 + 4.0F;
      } else if (flag) {
         String text = t("wardrobe.presets.wearing");
         float pillWidth2 = pillWidth(font3, text);
         pill(menuDrawContext, font3, f4 - pillWidth2, boundsData.getFloatValue2() + (boundsData.height() - font3.height() - 3.6F) / 2.0F, text, OK, previewAlpha);
         f4 -= pillWidth2 + 4.0F;
      }

      float floatValue = boundsData.getFloatValue() + 4.0F + width + 7.0F;
      float width3 = f4 - floatValue;
      menuDrawContext.drawText(
         font,
         font.clip(data.name(), width3),
         floatValue,
         boundsData.getFloatValue2() + 5.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(hot2, hot * 0.7F)).method7294(previewAlpha)
      );
      String name = t("wardrobe.presets.items").replace("{n}", String.valueOf(elements.size())) + "  ·  " + String.join(", ", elements);
      menuDrawContext.drawText(
         font2, font2.clip(name, width3), floatValue, boundsData.getFloatValue2() + 7.0F + font.height(), zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      if (contains && !contains2) {
         HoverInfo.request(data.name(), t("wardrobe.presets.row.desc"));
      }

      if (contains2) {
         HoverInfo.request(t("wardrobe.presets.delete"), t("wardrobe.presets.delete.desc"));
      }
   }

   private void savePreset() {
      Wardrobe wardrobe = manager();
      if (wardrobe != null) {
         if (wardrobe.method2497() == 0) {
            this.status(t("wardrobe.presets.nothing"), true);
         } else {
            String replacement = this.presetInput().getText() == null ? "" : this.presetInput().getText().strip();
            if (replacement.isEmpty()) {
               int n = wardrobe.method2505().size() + 1;

               do {
                  replacement = t("wardrobe.presets.defaultName") + " " + n++;
               } while (wardrobe.method2507(replacement));
            }

            boolean flag = wardrobe.method2507(replacement);
            wardrobe.method2506(replacement);
            this.presetInput().setText("");
            this.presetInput().setCursor(0);
            this.presetInput().setSelected(false);
            this.status(t(flag ? "wardrobe.presets.replaced" : "wardrobe.presets.saved").replace("{n}", replacement), false);
            Zenith.getInstance().getManager().method7788();
            this.invalidate();
         }
      }
   }

   private void grid(Wardrobe wardrobe, List<HoveredData> hoveredDatas) {
      for (byte i = 0; i < hoveredDatas.size(); i += 3) {
         HoveredData[] hoveredDatas2 = new HoveredData[Math.min(3, hoveredDatas.size() - i)];

         for (int j = 0; j < hoveredDatas2.length; j++) {
            hoveredDatas2[j] = (HoveredData)hoveredDatas.get(i + j);
         }

         this.custom(
            89.0F,
            (var3x, var4x, var5x, f, y, f2, previewAlpha, zenithStyle) -> this.renderRow(var3x, wardrobe, hoveredDatas2, var4x, var5x, f, y, f2, previewAlpha, zenithStyle),
            () -> this.toggle(wardrobe),
            null
         );
      }
   }

   private void toggle(Wardrobe wardrobe) {
      HoveredData hovered = this.hovered;
      if (hovered != null) {
         wardrobe.method2494(hovered);
         this.invalidate();
      }
   }

   private void renderRow(
      MenuDrawContext menuDrawContext, Wardrobe wardrobe, HoveredData[] hoveredDatas, int n, int n2, float f, float y, float f2, float previewAlpha, ZenithStyle zenithStyle
   ) {
      float width = (f2 - 16.0F - 10.0F) / 3.0F;
      if (n2 >= y && n2 < y + 84.0F + 5.0F) {
         this.hovered = null;
      }

      boolean flag = n2 >= y && n2 < y + 84.0F;

      for (int i = 0; i < hoveredDatas.length; i++) {
         float floatValue = f + 8.0F + (width + 5.0F) * i;
         boolean flag2 = flag && n >= floatValue && n < floatValue + width;
         if (flag2) {
            this.hovered = hoveredDatas[i];
         }

         this.renderCard(menuDrawContext, wardrobe, hoveredDatas[i], flag2, floatValue, y, width, previewAlpha, zenithStyle);
      }
   }

   private void renderCard(
      MenuDrawContext menuDrawContext, Wardrobe wardrobe, HoveredData hoveredData, boolean flag, float floatValue, float y, float width, float previewAlpha, ZenithStyle zenithStyle
   ) {
      boolean flag2 = wardrobe.method2490(hoveredData);
      float f = animate(this.lift, hoveredData.getText(), flag, 150L);
      float x = animate(this.worn, hoveredData.getText(), flag2, 220L);
      Rarity rarity = hoveredData.getRarity();
      ZenithColor zenithColor = color(rarity.getIntValue());
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(6.0F);
      menuDrawContext.drawRoundedRect(
         floatValue, y, width, 84.0F, headerRadius, zenithStyle.getFieldSurfaceBackground().getZenithColor().method7294((2.2F + f * 1.6F + x * 2.2F) * previewAlpha)
      );
      float height = 37.8F;
      menuDrawContext.drawRoundedRect(floatValue, y + 84.0F - height, width, height, headerRadius, zenithColor.method7294((0.07F + f * 0.06F + x * 0.08F) * previewAlpha));
      float f2 = rarity.isFlag() ? 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 420.0 + floatValue * 0.05) : 0.0F;
      ZenithColor zenithColor2 = zenithColor.method7294((0.45F + f * 0.35F + x * 0.2F + f2 * 0.2F) * previewAlpha);
      menuDrawContext.drawRoundedBorder(floatValue, y, width, 84.0F, 1.0F + x * 0.6F, headerRadius, zenithColor2);
      menuDrawContext.drawRoundedRect(floatValue + 6.0F, y, width - 12.0F, 1.6F, ProxyDotRadiusData.method8069(0.8F), zenithColor.method7294((0.8F + f2 * 0.2F) * previewAlpha));
      float f3 = 65.0F;
      float floatValue2 = floatValue + width / 2.0F;
      float f4 = y + f3 / 2.0F + 2.0F;
      float f5 = (Math.min(width, f3) - 10.0F) * (1.0F + f * 0.08F);
      WardrobeModel class2 = wardrobe.getRegistry().method1427(hoveredData);
      boolean flag3 = this.isFullyVisible()
         && this.thumbnails.method7855(hoveredData, class2, floatValue2, f4, f5, floatValue + 1.0F, y + 1.0F, floatValue + width - 1.0F, y + f3, f);
      if (!flag3) {
         MenuGlyph menuGlyph = switch (hoveredData.method1546()) {
            case PET -> MenuGlyph.PVE;
            case WEAPON -> MenuGlyph.COMBAT;
            case ITEM -> MenuGlyph.POTION;
            case CHARACTER -> MenuGlyph.PROFILE;
            case CAPE -> MenuGlyph.SHIELD;
            case ACCESSORY -> MenuGlyph.TIMER;
            default -> MenuGlyph.COSMETICS;
         };
         menuGlyph.draw(menuDrawContext, floatValue2 - 3.0F, f4 - 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.6F));
      }

      if (x > 0.01F) {
         float width2 = 9.0F;
         float f6 = floatValue + width - 4.0F - width2;
         menuDrawContext.pushMatrix();

         try {
            menuDrawContext.pose().translate(f6 + width2 / 2.0F, y + 5.0F + width2 / 2.0F);
            menuDrawContext.pose().scale(x, x);
            menuDrawContext.drawRoundedRect(-width2 / 2.0F, -width2 / 2.0F, width2, width2, ProxyDotRadiusData.method8069(width2 / 2.0F), zenithColor.method7294(previewAlpha));
            MenuGlyph.CHECK.draw(menuDrawContext, -3.0F, -3.0F, new ZenithColor(14, 14, 16).method7294(previewAlpha));
         } finally {
            menuDrawContext.popMatrix();
         }
      }

      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(3.6F);
      String text = fit(font, t(hoveredData.method1547()), width - 8.0F);
      menuDrawContext.drawText(
         font,
         text,
         floatValue2 - font.width(text) / 2.0F,
         y + f3 + 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(x, f * 0.5F)).method7294(previewAlpha)
      );
      String upperCase = t(rarity.key()).toUpperCase(Locale.ROOT);
      menuDrawContext.drawText(font2, upperCase, floatValue2 - font2.width(upperCase) / 2.0F, y + f3 + 2.0F + font.height() + 1.5F, zenithColor.method7294(previewAlpha * 0.9F));
      if (flag) {
         HoverInfo.request(
            t(hoveredData.method1547()),
            t(rarity.key())
               + " · "
               + t(hoveredData.getFocusSlotEnum().key())
               + ". "
               + t(hoveredData.method1548())
               + " "
               + t(flag2 ? "wardrobe.panel.unequip" : "wardrobe.panel.equip")
         );
      }
   }

   private static float animate(Map<String, Animation> lift, String text, boolean flag, long duration) {
      Animation animation = (Animation)lift.get(text);
      if (animation == null) {
         if (!flag) {
            return 0.0F;
         }

         animation = new Animation(duration, 0.0F, Easing.INSTANCE12);
         lift.put(text, animation);
      }

      float f = animation.animateTo(flag ? 1.0F : 0.0F);
      if (f < 0.004F && !flag) {
         lift.remove(text);
         return 0.0F;
      } else {
         return f;
      }
   }

   private static ZenithColor color(int intValue) {
      return new ZenithColor(intValue >> 16 & 0xFF, intValue >> 8 & 0xFF, intValue & 0xFF);
   }

   private void buildAside(Wardrobe wardrobe) {
      this.asideFull(
         (context, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
            context.drawText(
               font, HoverInfo.spaced(t("wardrobe.panel.fitting")), f + 8.0F, f2 + 4.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
            float stageY0 = f2 + 14.0F;
            float f4 = NLMenuScreen.contentBottom - HoverInfo.boxHeight() - GuiStyle.PADDING.intValue();
            float stageX0 = f + 4.0F;
            float width = f3 - 8.0F;
            float height = Math.max(60.0F, f4 - stageY0);
            float previewFit = Math.min(height / 2.55F, width / 1.3F);
            if (this.preview == null) {
               this.preview = new PlayerPreview3D(stageX0, stageY0, width, height);
               this.preview.setIdleSpin(10.0F);
               this.autoZoom = previewFit / 60.0F;
               this.preview.setZoom(this.autoZoom);
            } else {
               this.preview.setBounds(stageX0, stageY0, width, height);
               if (Math.abs(this.preview.getZoom() - this.autoZoom) < 5.0E-4F) {
                  this.autoZoom = previewFit / 60.0F;
                  this.preview.setZoom(this.autoZoom);
               }
            }

            this.previewFit = previewFit;
            this.preview.setMaxSize(previewFit * 2.4F);
            HoveredData hovered = this.mainViewport() != null && this.mainViewport().contains(lastClickX, lastClickY) ? this.hovered : null;
            if (hovered != null) {
               this.focusSlot = hovered.getFocusSlotEnum();
            }

            if (this.getActiveTab() == 0 && mc.player != null) {
               HoveredData hoveredData = hovered != null && hovered.getFocusSlotEnum() == FocusSlotEnum.PET ? hovered : wardrobe.method2491(FocusSlotEnum.PET);
               if (hoveredData == null) {
                  var hoveredDatas = this.filter(wardrobe, FocusSlotEnum.PET);
                  hoveredData = hoveredDatas.isEmpty() ? null : (HoveredData)hoveredDatas.get(0);
               }

               if (hoveredData != null && previewAlpha > 0.01F && this.isFullyVisible()) {
                  long nanoTime = System.nanoTime();
                  float f5 = this.stageFrame == 0L ? 0.0F : Math.min(0.1F, (float)(nanoTime - this.stageFrame) / 1.0E9F);
                  this.stageFrame = nanoTime;
                  if (!this.stageDrag) {
                     this.stageYaw += f5 * 14.0F;
                  }

                  this.stageX0 = stageX0;
                  this.stageY0 = stageY0;
                  this.stageX1 = stageX0 + width;
                  this.stageY1 = stageY0 + height;
                  float f6 = stageY0 + height * 0.8F;
                  context.drawRoundedRect(
                     stageX0 + width * 0.2F,
                     f6 - 4.0F,
                     width * 0.6F,
                     8.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     new ZenithColor(0, 0, 0).method7294(previewAlpha * 0.25F)
                  );
                  this.stage
                     .method893(
                        context, hoveredData, wardrobe.getRegistry().method1427(hoveredData), this.stageX0, this.stageY0, this.stageX1, this.stageY1, this.stageYaw, -14.0F
                     );
                  if (lastClickX >= this.stageX0 && lastClickX <= this.stageX1 && lastClickY >= this.stageY0 && lastClickY <= this.stageY1) {
                     HoverInfo.request(t(hoveredData.method1547()), t("wardrobe.panel.drag"));
                  }

                  return;
               }
            }

            this.applyFocus(wardrobe);
            FocusSlotEnum subSlot = this.subSlot();
            boolean flag = this.getActiveTab() == 4 || this.getActiveTab() == 5;
            FocusSlotEnum focusSlotEnum = hovered != null && hovered.getFocusSlotEnum().method543()
               ? hovered.getFocusSlotEnum()
               : (subSlot != null && subSlot.method543() ? subSlot : equippedHeldSlot(wardrobe, this.getActiveTab() == 5 ? ITEMS : WEAPONS));
            this.preview.setForcedMainHand(flag && focusSlotEnum != null ? focusSlotEnum.method544() : null);
            this.preview.setHideHeldItems(!flag);
            HoveredData hoveredData = hovered != null && hovered.getFocusSlotEnum() == FocusSlotEnum.PET ? hovered : wardrobe.method2491(FocusSlotEnum.PET);
            boolean flag2 = this.getActiveTab() == 1 && (subSlot == FocusSlotEnum.CAPE || hovered != null && hovered.getFocusSlotEnum() == FocusSlotEnum.CAPE);
            this.preview
               .setQueueOverlay(
                  hoveredData != null && this.getActiveTab() != 2 && !flag2
                     ? (var2x, var3x, var4x, var5x, var6x) -> renderPet(wardrobe, hoveredData, var2x, var3x, var4x)
                     : null
               );
            if (mc.player == null) {
               Font font2 = Fonts.NEW_REGULAR.getFont(5.0F);
               String text = t("wardrobe.panel.enterWorld");
               context.drawText(
                  font2,
                  text,
                  f + (f3 - font2.width(text)) / 2.0F,
                  stageY0 + height / 2.0F - 3.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
            } else {
               if (previewAlpha > 0.01F && this.isFullyVisible()) {
                  context.enableScissor(stageX0, stageY0, stageX0 + width, stageY0 + height);
                  RenderState.method1883(1.0F, 1.0F, 1.0F, previewAlpha);
                  wardrobe.setHoveredData(hovered);
                  wardrobe.setFlag(true);

                  try {
                     this.preview.render(context, mc.player, lastClickX, lastClickY);
                  } finally {
                     wardrobe.setFlag(false);
                     wardrobe.setHoveredData(null);
                     RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
                  }

                  context.disableScissor();
               }

               if (lastClickX >= stageX0 && lastClickX <= stageX0 + width && lastClickY >= stageY0 && lastClickY <= stageY0 + height) {
                  HoverInfo.request(t("wardrobe.panel.fitting"), t("wardrobe.panel.drag") + " " + t("wardrobe.panel.tryOn"));
               }
            }
         }
      );
   }

   private void applyFocus(Wardrobe wardrobe) {
      FocusSlotEnum focusSlot = this.focusSlot;
      int activeTab = this.getActiveTab();
      FocusSlotEnum subSlot = this.subSlot();
      if (subSlot != null && (focusSlot == null || focusSlot.getWardrobeCategory() != subSlot.getWardrobeCategory() || focusSlot.method543())) {
         focusSlot = subSlot;
      }

      if (activeTab != 1 || focusSlot != FocusSlotEnum.CAPE && subSlot != FocusSlotEnum.CAPE) {
         if (activeTab == 2) {
            if (focusSlot != FocusSlotEnum.WATCH && focusSlot != FocusSlotEnum.NECK) {
               focusSlot = wardrobe.method2491(FocusSlotEnum.WATCH) != null && wardrobe.method2491(FocusSlotEnum.NECK) == null ? FocusSlotEnum.WATCH : FocusSlotEnum.NECK;
            }
         } else if (activeTab == 1) {
            if (focusSlot == null || focusSlot.getWardrobeCategory() != WardrobeCategory.CLOTHING) {
               focusSlot = null;
            }
         } else if (activeTab == 4) {
            focusSlot = subSlot != null ? subSlot : FocusSlotEnum.SWORD;
         } else if (activeTab == 5) {
            focusSlot = subSlot != null ? subSlot : equippedHeldSlot(wardrobe, ITEMS);
         } else {
            focusSlot = null;
         }
      } else {
         focusSlot = FocusSlotEnum.CAPE;
      }

      if (focusSlot == null) {
         this.preview.setFocus(1.05F, 1.0F, Float.NaN);
      } else {
         switch (focusSlot) {
            case HEAD:
               this.focus(1.62F, 2.3F, 0.0F);
               break;
            case BODY:
               this.focus(1.12F, 1.5F, 25.0F);
               break;
            case LEGS:
               this.focus(0.45F, 1.6F, 25.0F);
               break;
            case BACK:
               this.focus(1.15F, 1.35F, 180.0F);
               break;
            case CAPE:
               this.focus(1.0F, 1.05F, 180.0F);
               break;
            case NECK:
               this.focus(1.4F, 3.2F, 0.0F);
               break;
            case WATCH:
               this.focus(0.98F, 3.6F, -90.0F);
               break;
            default:
               if (focusSlot.getWardrobeCategory() == WardrobeCategory.ITEM) {
                  this.focus(0.95F, 2.3F, -35.0F);
               } else if (focusSlot.method542()) {
                  this.focus(0.98F, 1.7F, -35.0F);
               } else {
                  this.preview.setFocus(1.05F, 1.0F, Float.NaN);
               }
         }
      }
   }

   private void focus(float focusHeight, float f, float focusYaw) {
      this.preview.setFocus(focusHeight, Math.max(1.0F, f * 66.0F / Math.max(1.0F, this.previewFit)), focusYaw);
   }

   public void devTab(int activeTab) {
      this.selectTab(activeTab);
   }

   private static FocusSlotEnum equippedHeldSlot(Wardrobe wardrobe, FocusSlotEnum[] focusSlotEnums) {
      for (FocusSlotEnum key : focusSlotEnums) {
         if (wardrobe.method2491(key) != null) {
            return key;
         }
      }

      return focusSlotEnums[0];
   }

   private static void renderPet(Wardrobe wardrobe, HoveredData hoveredData, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int intValue) {
      WardrobeModel key = wardrobe.getRegistry().method1427(hoveredData);
      if (key != null && key.data3 != null) {
         float xScale = key.data3.getFloatValue() * wardrobe.petScale.getValue3();
         matrixStack.rotate(new Quaternionf().rotationY((float) Math.PI));
         matrixStack.scale(-1.0F, -1.0F, 1.0F);
         matrixStack.translate(0.0F, -1.501F, 0.0F);
         matrixStack.translate(0.95F, 1.5F, 0.35F);
         matrixStack.rotate(new Quaternionf().rotationY((float)Math.toRadians(-35.0)));
         matrixStack.scale(xScale, xScale, xScale);
         matrixStack.translate(0.0F, -1.5F, 0.0F);
         float floatValue = (float)(System.nanoTime() / 1000000L % 1000000L) / 1000.0F;
         WardrobePreview.method3147(hoveredData, key, matrixStack, orderedRenderCommandQueue, intValue, floatValue);
      }
   }

   @Override
   public void renderMainOverlay(MenuDrawContext menuDrawContext) {
      if (!this.isFullyVisible()) {
         this.thumbnails.method7856();
      } else {
         this.thumbnails.method7857(menuDrawContext);
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float panelAlpha, float f, float y) {
      this.subBounds = null;
      this.presetField = null;
      this.presetSave = null;
      this.deleteBounds.clear();
      this.sliderTracks.clear();
      super.render(menuDrawContext, lastClickX, lastClickY, panelAlpha, f, y);
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.presetName != null && this.presetName.isSelected()) {
         if (keyCode == 256) {
            this.presetName.setSelected(false);
            return true;
         }

         if (keyCode == 257 || keyCode == 335) {
            this.savePreset();
            return true;
         }

         if (this.presetName.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      return super.keyPressed(keyCode, keycode, modifiers);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      return this.presetName != null && this.presetName.isSelected() && this.presetName.charTyped(codepoint, n) ? true : super.charTyped(codepoint, n);
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      if (this.subBounds != null && this.shownSubs != null && this.subBounds.contains(lastClickX, lastClickY)) {
         int hovered = this.shownSubs.hovered();
         int activeTab = this.getActiveTab();
         if (hovered >= 0 && activeTab >= 0 && activeTab < this.subs.length) {
            if (hovered != this.subs[activeTab]) {
               this.subs[activeTab] = hovered;
               this.focusSlot = this.subSlot();
               this.hovered = null;
               this.lift.clear();
               this.resetScroll();
               this.invalidate();
            }

            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }
      }

      if (this.getActiveTab() == 6) {
         boolean selected = this.presetField != null && this.presetField.contains(lastClickX, lastClickY);
         this.presetInput().setSelected(selected);
         if (selected) {
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }

         if (enum2 == MouseButton.LEFT && this.presetSave != null && this.presetSave.contains(lastClickX, lastClickY)) {
            this.pressButton("presetSave", lastClickX, lastClickY);
            this.savePreset();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }

         for (Map.Entry entry : this.deleteBounds.entrySet()) {
            if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               Wardrobe wardrobe = manager();
               if (wardrobe == null) {
                  return true;
               }

               if (this.armedDelete.equals(entry.getKey()) && System.currentTimeMillis() - this.armedAt < 3000L) {
                  for (Wardrobe.Data data : wardrobe.method2505()) {
                     if (((String)entry.getKey()).equals("pr:" + data.name() + ":" + data.getLongValue())) {
                        wardrobe.method2509(data);
                        Zenith.getInstance().getManager().method7788();
                        break;
                     }
                  }

                  this.armedDelete = "";
                  this.invalidate();
               } else {
                  this.armedDelete = (String)entry.getKey();
                  this.armedAt = System.currentTimeMillis();
               }

               Zenith.getInstance().getClientSounds().method1570(true);
               return true;
            }
         }
      }

      if (this.getActiveTab() == 7 && enum2 == MouseButton.LEFT) {
         for (Map.Entry entry : this.sliderTracks.entrySet()) {
            if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               this.draggingSlider = (SliderSetting)entry.getKey();
               this.slide(this.draggingSlider, lastClickX);
               return true;
            }
         }
      }

      if (this.getActiveTab() == 0
         && lastClickX >= this.stageX0
         && lastClickX <= this.stageX1
         && lastClickY >= this.stageY0
         && lastClickY <= this.stageY1
         && this.stageX1 > this.stageX0) {
         this.stageDrag = true;
         return true;
      } else {
         return this.preview != null && this.preview.onMouseClicked(lastClickX, lastClickY, enum2.getGlfwButton()) ? true : super.onMouseClicked(lastClickX, lastClickY, enum2);
      }
   }

   @Override
   public boolean onMouseReleased(double x, double y, MouseButton enum2) {
      this.stageDrag = false;
      if (this.draggingSlider != null) {
         this.draggingSlider = null;
         Zenith.getInstance().getManager().method7788();
      }

      if (this.preview != null) {
         this.preview.onMouseReleased(x, y, enum2.getGlfwButton());
      }

      return super.onMouseReleased(x, y, enum2);
   }

   @Override
   public boolean onMouseDragged(double lastClickX, double lastMouseY, int mouseToGlfw, double offsetX, double offsetY) {
      if (this.draggingSlider != null) {
         this.slide(this.draggingSlider, lastClickX);
         return true;
      } else if (this.stageDrag) {
         this.stageYaw += (float)offsetX * 1.4F;
         return true;
      } else {
         return this.preview != null && this.preview.onMouseDragged(lastClickX, lastMouseY, mouseToGlfw, offsetX, offsetY) ? true : super.onMouseDragged(lastClickX, lastMouseY, mouseToGlfw, offsetX, offsetY);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double d, double verticalAmount) {
      return this.preview != null && this.preview.onMouseScrolled(mouseX, mouseY, verticalAmount) ? true : super.mouseScrolled(mouseX, mouseY, d, verticalAmount);
   }
}

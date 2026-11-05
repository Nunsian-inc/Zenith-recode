package org.zenith.client.screens.nlgui.panel.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.CraftRecipe;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class AutoCraftToolPanel extends ToolTab {
   private static final int FILTER_ALL = 0;
   private static final int FILTER_CRAFTABLE = 1;
   private static final float HERO_HEIGHT = 88.0F;
   private static final float CELL = 15.0F;
   private static final float CELL_GAP = 2.0F;
   private static final float CELL_ICON = 11.0F;
   private static final float RESULT = 28.0F;
   private static final float RESULT_ICON = 18.0F;
   private static final float BUTTON_HEIGHT = 22.0F;
   private static final float LIST_HEAD = 20.0F;
   private static final float RECIPE_ROW = 25.0F;
   private static final float STAT_HEIGHT = 36.0F;
   private static final float SOURCE_ROW = 28.0F;
   private static final float TOGGLE_ROW = 22.0F;
   private static final long PROBE_DELAY_MS = 400L;
   private final ChipStrip filterStrip = new ChipStrip("gui.tools.craft.filter.all", "gui.tools.craft.filter.craftable");
   private final Map<String, String> summaries = new HashMap<>();
   private int filter = 0;
   private int recipeCount;
   private int sourcesFrom = 3;
   private int recipesFrom = 3;
   private BoundsData filterBounds;
   private BoundsData buttonBounds;
   private String contentSignature = "";
   private String shownPreset = "";
   private AutoCraftModule.Data2 nearby = new AutoCraftModule.Data2(null, 0);
   private Map<String, Integer> inventory = Map.of();
   private long probedAt;

   private static AutoCraftModule module() {
      return AutoCraftModule.INSTANCE;
   }

   @Override
   public String headerTitle() {
      return "Crafter";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CRAFT;
   }

   @Override
   public String sectionDescription() {
      return "gui.tools.craft.desc";
   }

   public boolean isLive() {
      return module().isEnabled();
   }

   @Override
   public void build() {
      AutoCraftModule autoCraftModule = module();
      CraftRecipe craftRecipe = autoCraftModule.method1097();
      String id = craftRecipe == null ? "" : craftRecipe.getId();
      if (!id.equals(this.shownPreset)) {
         this.shownPreset = id;
         this.mark("preset");
      }

      this.custom(92.0F, this.entering(0, this::renderHero));
      this.custom(32.0F, this.entering(1, this::renderButton));
      this.custom(20.0F, this.entering(2, this::renderListHead));
      this.recipesFrom = this.mainEntryCount();
      this.buildRecipes(autoCraftModule);
      this.buildAside(autoCraftModule, craftRecipe);
   }

   private void buildRecipes(AutoCraftModule autoCraftModule) {
      String searchQuery = this.searchQuery();
      List<CraftRecipe> craftRecipes = autoCraftModule.method1092(autoCraftModule.method1098());
      ArrayList<CraftRecipe> craftRecipes2 = new ArrayList();

      for (CraftRecipe craftRecipe : craftRecipes) {
         String str = craftRecipe.getText3();
         if (str != null
            && !str.isBlank()
            && (searchQuery.isEmpty() || str.toLowerCase(Locale.ROOT).contains(searchQuery))
            && (this.filter != 1 || this.craftableNow(craftRecipe))) {
            craftRecipes2.add(craftRecipe);
         }
      }

      this.recipeCount = craftRecipes2.size();
      if (craftRecipes2.isEmpty()) {
         String key;
         String str;
         if (craftRecipes.isEmpty()) {
            key = "gui.tools.craft.none";
            str = "gui.tools.craft.none.desc";
         } else if (this.filter == 1 && searchQuery.isEmpty()) {
            key = "gui.tools.craft.noneCraftable";
            str = "gui.tools.craft.noneCraftable.desc";
         } else {
            key = "gui.tools.craft.noneFound";
            str = "gui.tools.craft.noneFound.desc";
         }

         this.custom(
            76.0F,
            this.entering(
               3,
               (var3x, var4x, n, var6x, var7x, var8x, var9x, zenithStyle) -> this.emptyCard(
                  var3x, zenithStyle, var6x + 8.0F, var7x + 2.0F, var8x - 16.0F, 70.0F, MenuGlyph.CRAFT, capitalize(t(key)), t(str), var9x
               )
            )
         );
      } else {
         int n = 3;

         for (CraftRecipe craftRecipe : craftRecipes2) {
            this.custom(
               25.0F,
               this.entering(
                  n++,
                  (var2x, var3x, var4x, f, var6x, var7x, var8x, var9x) -> this.renderRecipe(var2x, var3x, var4x, f, var6x, var7x, var8x, var9x, craftRecipe)
               ),
               () -> this.selectPreset(autoCraftModule, craftRecipe),
               () -> craftRecipe.getId().equals(autoCraftModule.getText2())
            );
         }
      }
   }

   private void buildAside(AutoCraftModule autoCraftModule, CraftRecipe craftRecipe) {
      this.asideCaption("gui.tools.craft.caption.around");
      this.asideCustom(36.0F, this.entering(1, this::renderAround));
      this.asideCaption("gui.tools.craft.caption.sources");
      this.sourcesFrom = this.asideEntryCount();
      if (craftRecipe == null) {
         this.asideCustom(
            62.0F,
            this.entering(
               2,
               (var1x, var2x, var3x, var4x, f, var6x, var7x, var8x) -> this.miniEmpty(
                  var1x, var8x, var4x + 8.0F, f + 2.0F, var6x - 16.0F, 56.0F, t("gui.tools.craft.noPreset.desc"), var7x
               )
            )
         );
      } else {
         var map = craftRecipe.method5388();
         if (map.isEmpty()) {
            this.asideCustom(
               62.0F,
               this.entering(
                  2,
                  (var1x, var2x, var3x, var4x, f, var6x, var7x, var8x) -> this.miniEmpty(
                     var1x, var8x, var4x + 8.0F, f + 2.0F, var6x - 16.0F, 56.0F, t("gui.tools.craft.noIngredients.desc"), var7x
                  )
               )
            );
         } else {
            int n = 2;

            for (Map.Entry entry : map.entrySet()) {
               String key = (String)entry.getKey();
               int n2 = Math.max(1, (Integer)entry.getValue());
               this.asideCustom(
                  28.0F,
                  this.entering(
                     n++,
                     (var4x, lastClickX, var6x, var7x, var8x, f, previewAlpha, zenithStyle) -> this.renderSource(
                        var4x, lastClickX, var6x, var7x, var8x, f, previewAlpha, zenithStyle, craftRecipe, key, n2
                     )
                  )
               );
            }
         }
      }

      this.asideCaption("gui.tools.craft.caption.options");
      this.toggle("gui.tools.craft.option.take", "gui.tools.craft.option.take.desc", autoCraftModule.getAutoTake(), 6);
      this.toggle("gui.tools.craft.option.store", "gui.tools.craft.option.store.desc", autoCraftModule.getAutoStore(), 7);
      this.toggle("gui.tools.craft.option.batch", "gui.tools.craft.option.batch.desc", autoCraftModule.getBatch(), 8);
   }

   private void toggle(String key, String str, BooleanSetting autoTake, int n) {
      this.asideCustom(
         22.0F,
         this.entering(
            n,
            (var4x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
               BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 19.0F);
               boolean contains = boundsData.contains(lastClickX, lastClickY);
               float hot = this.hot("tg:" + key, contains);
               card(var4x, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
               float width = 14.0F;
               float height = 7.5F;
               float floatValue = boundsData.getFloatValue() + boundsData.width() - 6.0F - width;
               this.drawSwitch(var4x, key, floatValue, boundsData.getFloatValue2() + (boundsData.height() - height) / 2.0F, width, height, autoTake.isEnabled(), previewAlpha, zenithStyle);
               Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
               var4x.drawText(
                  font,
                  font.clip(t(key), floatValue - boundsData.getFloatValue() - 12.0F),
                  boundsData.getFloatValue() + 7.0F,
                  boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
                  zenithStyle.getTextSecondary()
                     .getZenithColor()
                     .method7295(zenithStyle.getTextEnable().getZenithColor(), hot * 0.7F + (autoTake.isEnabled() ? 0.3F : 0.0F))
                     .method7294(previewAlpha)
               );
               if (contains) {
                  HoverInfo.request(t(key), t(str));
               }
            }
         ),
         () -> {
            autoTake.toggle();
            Zenith.getInstance().getManager().method7788();
         },
         null
      );
   }

   private void selectPreset(AutoCraftModule autoCraftModule, CraftRecipe craftRecipe) {
      if (!craftRecipe.getId().equals(autoCraftModule.getText2())) {
         autoCraftModule.method1100(craftRecipe.getId());
         Zenith.getInstance().getManager().method7788();
         this.resetAsideScroll();
         this.replayAsideFrom(this.sourcesFrom);
         this.invalidate();
      }
   }

   private void renderHero(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      AutoCraftModule autoCraftModule = module();
      CraftRecipe craftRecipe = autoCraftModule.method1097();
      boolean enabled = autoCraftModule.isEnabled();
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 2.0F, f3 - 16.0F, 88.0F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      accentCard(
         menuDrawContext,
         zenithStyle,
         boundsData.getFloatValue(),
         boundsData.getFloatValue2(),
         boundsData.width(),
         boundsData.height(),
         0.0F,
         previewAlpha,
         zenithColor,
         enabled ? 0.45F + 0.2F * pulse(0.7F) : 0.18F
      );
      if (craftRecipe == null) {
         float width = 30.0F;
         float floatValue = boundsData.getFloatValue() + 14.0F;
         float y = boundsData.getFloatValue2() + (boundsData.height() - width) / 2.0F + (float)Math.sin(System.currentTimeMillis() / 600.0) * 1.2F;
         tile(menuDrawContext, zenithStyle, floatValue, y, width, zenithColor, 0.55F, previewAlpha);
         bigGlyph(menuDrawContext, MenuGlyph.CRAFT, floatValue + width / 2.0F, y + width / 2.0F, 2.0F, zenithColor.method7294(previewAlpha));
         Font font = Fonts.NEW_SEMIBOLD.getFont(6.2F);
         Font font2 = Fonts.NEW_REGULAR.getFont(4.5F);
         float floatValue2 = floatValue + width + 12.0F;
         float shownWidth = boundsData.getFloatValue() + boundsData.width() - 10.0F - floatValue2;
         List<String> strings = wrapText(t("gui.tools.craft.pick.hint"), font2, shownWidth, 3);
         float f4 = font.height() + 4.0F + strings.size() * (font2.height() + 2.0F);
         float centeredTextY = boundsData.getFloatValue2() + (boundsData.height() - f4) / 2.0F;
         menuDrawContext.drawText(font, t("gui.tools.craft.pick"), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
         centeredTextY += font.height() + 4.0F;

         for (String text : strings) {
            menuDrawContext.drawText(font2, text, floatValue2, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY += font2.height() + 2.0F;
         }
      } else {
         if (boundsData.contains(lastClickX, lastClickY)) {
            HoverInfo.request(craftRecipe.method5400(autoCraftModule), t("gui.tools.craft.recipe.desc"));
         }

         float since = this.since("preset", 520L);
         float f4 = boundsData.getFloatValue() + 10.0F;
         float f5 = boundsData.getFloatValue2() + 9.0F;
         float f6 = 49.0F;

         for (int i = 0; i < 9; i++) {
            float floatValue = f4 + i % 3 * 17.0F;
            float y = f5 + i / 3 * 17.0F;
            menuDrawContext.drawRoundedRect(
               floatValue, y, 15.0F, 15.0F, ProxyDotRadiusData.method8069(3.5F), zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
            );
         }

         for (int i = 0; i < 9; i++) {
            String identifier = craftRecipe.method5391(i);
            if (identifier != null && !identifier.isBlank()) {
               float f7 = Math.max(0.0F, Math.min(1.0F, since * 1.8F - i * 0.07F));
               float f8 = f7 <= 0.0F ? 0.0F : 1.0F - (1.0F - f7) * (1.0F - f7) * (1.0F - f7);
               float f9 = 1.0F + (float)Math.sin(f7 * Math.PI) * 0.12F;
               float f10 = 11.0F * f8 * f9;
               if (!(f10 < 1.0F)) {
                  float x = f4 + i % 3 * 17.0F + (15.0F - f10) / 2.0F;
                  float y = f5 + i / 3 * 17.0F + (15.0F - f10) / 2.0F;
                  this.drawItem(menuDrawContext, autoCraftModule.method1117(identifier).getDefaultInstance(), x, y, f10, previewAlpha);
               }
            }
         }

         float floatValue = f4 + f6 + 5.0F;
         float floatValue2 = floatValue + 18.0F;
         float y = f5 + (f6 - 28.0F) / 2.0F;
         this.drawFlow(menuDrawContext, floatValue, y + 14.0F, 13.0F, enabled, zenithStyle, previewAlpha);
         tile(menuDrawContext, zenithStyle, floatValue2, y, 28.0F, zenithColor, 0.35F, previewAlpha);
         float f7 = Math.max(0.0F, Math.min(1.0F, since * 1.6F - 0.35F));
         float f8 = 18.0F * (1.0F - (1.0F - f7) * (1.0F - f7)) * (1.0F + (float)Math.sin(f7 * Math.PI) * 0.1F);
         if (f8 > 1.0F) {
            this.drawItem(menuDrawContext, craftRecipe.method5401(autoCraftModule), floatValue2 + (28.0F - f8) / 2.0F, y + (28.0F - f8) / 2.0F, f8, previewAlpha);
         }

         int possibleCrafts = this.possibleCrafts(craftRecipe);
         Font font = Fonts.NEW_SEMIBOLD.getFont(6.2F);
         Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
         Font font3 = Fonts.NEW_MEDIUM.getFont(4.3F);
         float floatValue3 = floatValue2 + 28.0F + 10.0F;
         float width = boundsData.getFloatValue() + boundsData.width() - 9.0F - floatValue3;
         float centeredTextY = f5 + 4.0F;
         menuDrawContext.drawText(font, font.clip(craftRecipe.method5400(autoCraftModule), width), floatValue3, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
         centeredTextY += font.height() + 3.0F;
         menuDrawContext.drawText(font2, font2.clip(this.summary(autoCraftModule, craftRecipe), width), floatValue3, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY += font2.height() + 5.0F;
         String text = possibleCrafts > 0 ? t("gui.tools.craft.stock.enough").replace("{n}", String.valueOf(possibleCrafts)) : t("gui.tools.craft.stock.none");
         pill(menuDrawContext, font3, floatValue3, centeredTextY, text, possibleCrafts > 0 ? OK : zenithStyle.getTextTertiary().getZenithColor(), previewAlpha);
         AutoCraftToolPanel.Readiness readiness = this.readiness(craftRecipe, possibleCrafts);
         ZenithColor stateColor = this.stateColor(readiness, zenithStyle);
         float y2 = boundsData.getFloatValue2() + boundsData.height() - 17.0F;
         menuDrawContext.drawRect(boundsData.getFloatValue() + 8.0F, y2, boundsData.width() - 16.0F, 0.5F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.5F));
         float f9 = y2 + 8.5F;
         boolean flag = enabled || autoCraftModule.isBinding();
         dot(menuDrawContext, boundsData.getFloatValue() + 12.0F, f9, 3.2F, stateColor, flag ? pulse(1.2F) : 0.0F, previewAlpha);
         menuDrawContext.drawText(
            font2,
            font2.clip(this.stateText(readiness), boundsData.width() - 30.0F),
            boundsData.getFloatValue() + 18.0F,
            f9 - font2.height() / 2.0F,
            stateColor.method7294(previewAlpha)
         );
      }
   }

   private void drawFlow(MenuDrawContext menuDrawContext, float floatValue, float f, float f2, boolean enabled, ZenithStyle zenithStyle, float previewAlpha) {
      ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor();
      ZenithColor zenithColor2 = zenithStyle.getPrimaryColor().getZenithColor();
      if (!enabled) {
         menuDrawContext.drawRect(floatValue, f - 0.5F, f2 - 3.0F, 1.0F, zenithColor.method7294(previewAlpha));
         menuDrawContext.drawRect(floatValue + f2 - 5.0F, f - 2.5F, 1.0F, 2.0F, zenithColor.method7294(previewAlpha));
         menuDrawContext.drawRect(floatValue + f2 - 5.0F, f + 0.5F, 1.0F, 2.0F, zenithColor.method7294(previewAlpha));
         menuDrawContext.drawRect(floatValue + f2 - 4.0F, f - 1.5F, 1.0F, 3.0F, zenithColor.method7294(previewAlpha));
      } else {
         float f3 = (float)(System.currentTimeMillis() % 900L) / 900.0F;

         for (int i = 0; i < 3; i++) {
            float f4 = (f3 + i / 3.0F) % 1.0F;
            float f5 = floatValue + f4 * f2;
            float f6 = (float)Math.sin(f4 * Math.PI);
            menuDrawContext.drawRoundedRect(f5 - 1.2F, f - 1.2F, 2.4F, 2.4F, ProxyDotRadiusData.method8069(1.2F), zenithColor2.method7294(previewAlpha * f6));
         }
      }
   }

   private void renderButton(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      AutoCraftModule autoCraftModule = module();
      boolean enabled = autoCraftModule.isEnabled();
      boolean flag = autoCraftModule.method1097() != null || enabled;
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 4.0F, f3 - 16.0F, 22.0F);
      this.buttonBounds = flag ? boundsData : null;
      String name = t(flag ? (enabled ? "gui.tools.craft.stop" : "gui.tools.craft.start") : "gui.tools.craft.start.disabled");
      boolean button = this.button(menuDrawContext, zenithStyle, "start", boundsData, enabled ? MenuGlyph.CLOSE : MenuGlyph.CRAFT, name, enabled ? 2 : 1, flag, lastClickX, lastClickY, previewAlpha);
      if (enabled) {
         this.shimmer(menuDrawContext, boundsData, DANGER, previewAlpha);
      }

      if (button) {
         HoverInfo.request(name, t(enabled ? "gui.tools.craft.stop.desc" : "gui.tools.craft.start.desc"));
      }
   }

   private void renderListHead(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
      float centeredTextY = f2 + (20.0F - font.height()) / 2.0F;
      String str = t("gui.tools.craft.caption.presets");
      caption(menuDrawContext, zenithStyle, str, f + 8.0F, centeredTextY, previewAlpha);
      String text = String.valueOf(this.recipeCount);
      float floatValue = f + 8.0F + font.width(HoverInfo.spaced(str.toUpperCase(Locale.ROOT))) + 5.0F;
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.0F);
      menuDrawContext.drawText(font2, text, floatValue, centeredTextY, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      float width = this.filterStrip.width();
      float floatValue2 = f + f3 - 8.0F - width;
      float y = f2 + 4.5F;
      this.filterBounds = new BoundsData(floatValue2, y, width, 11.0F);
      this.filterStrip.render(menuDrawContext, lastClickX, lastClickY, floatValue2, y, width + 1.0F, this.filter, previewAlpha, zenithStyle);
      if (this.filterStrip.hovered() >= 0) {
         HoverInfo.request(this.filterStrip.label(this.filterStrip.hovered()), t("gui.tools.craft.filter.desc"));
      }
   }

   private void renderRecipe(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, CraftRecipe craftRecipe) {
      AutoCraftModule autoCraftModule = module();
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 22.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      boolean flag = craftRecipe.getId().equals(autoCraftModule.getText2());
      float hot = this.hot("r:" + craftRecipe.getId(), contains);
      float hot2 = this.hot("rs:" + craftRecipe.getId(), flag);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      if (hot2 > 0.01F) {
         accentCard(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha, zenithColor, hot2 * 0.75F);
      } else {
         card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
      }

      float width = boundsData.height() - 6.0F;
      float floatValue = boundsData.getFloatValue() + 3.0F;
      float y = boundsData.getFloatValue2() + 3.0F;
      tile(menuDrawContext, zenithStyle, floatValue, y, width, zenithColor, hot2 * 0.6F + hot * 0.2F, previewAlpha);
      float f4 = hot * 0.8F;
      this.drawItem(menuDrawContext, craftRecipe.method5401(autoCraftModule), floatValue + (width - 12.0F) / 2.0F, y + (width - 12.0F) / 2.0F - f4, 12.0F, previewAlpha);
      boolean craftableNow = this.craftableNow(craftRecipe);
      Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.0F);
      Font font3 = Fonts.NEW_MEDIUM.getFont(4.0F);
      String text = t("gui.tools.craft.list.can");
      float f5 = boundsData.getFloatValue() + boundsData.width() - 6.0F;
      if (craftableNow) {
         float pillWidth2 = pillWidth(font3, text);
         pill(menuDrawContext, font3, f5 - pillWidth2, boundsData.getFloatValue2() + (boundsData.height() - font3.height() - 3.6F) / 2.0F, text, OK, previewAlpha);
         f5 -= pillWidth2 + 5.0F;
      }

      float floatValue2 = floatValue + width + 6.0F;
      float width2 = f5 - floatValue2;
      menuDrawContext.drawText(
         font,
         font.clip(craftRecipe.getText3(), width2),
         floatValue2,
         boundsData.getFloatValue2() + 4.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(hot2, hot * 0.7F)).method7294(previewAlpha)
      );
      menuDrawContext.drawText(
         font2,
         font2.clip(this.summary(autoCraftModule, craftRecipe), width2),
         floatValue2,
         boundsData.getFloatValue2() + 6.0F + font.height(),
         zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      if (contains) {
         HoverInfo.request(craftRecipe.getText3(), t("gui.tools.craft.preset.desc"));
      }
   }

   private String summary(AutoCraftModule autoCraftModule, CraftRecipe craftRecipe) {
      return this.summaries.computeIfAbsent(craftRecipe.getId(), var2x -> {
         ArrayList elements = new ArrayList();

         for (Map.Entry entry : craftRecipe.method5388().entrySet()) {
            elements.add(Math.max(1, (Integer)entry.getValue()) + "× " + craftRecipe.method5397((String)entry.getKey(), autoCraftModule));
         }

         return String.join("  ·  ", elements);
      });
   }

   private void renderAround(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float f4 = f3 - 16.0F;
      float width = (f4 - 4.0F) / 2.0F;
      boolean flag = this.nearby.method1131();
      this.statTile(
         menuDrawContext,
         zenithStyle,
         lastClickX,
         lastClickY,
         f + 8.0F,
         f2 + 1.0F,
         width,
         32.0F,
         Items.CRAFTING_TABLE.getDefaultInstance(),
         t("gui.tools.craft.around.workbench"),
         t(flag ? "gui.tools.craft.near.yes" : "gui.tools.craft.near.no"),
         flag ? OK : DANGER,
         "gui.tools.craft.around.workbench.desc",
         previewAlpha
      );
      int intValue = this.nearby.getIntValue();
      this.statTile(
         menuDrawContext,
         zenithStyle,
         lastClickX,
         lastClickY,
         f + 8.0F + width + 4.0F,
         f2 + 1.0F,
         width,
         32.0F,
         Items.CHEST.getDefaultInstance(),
         t("gui.tools.craft.around.storages"),
         String.valueOf(intValue),
         intValue > 0 ? zenithStyle.getTextEnable().getZenithColor() : zenithStyle.getTextTertiary().getZenithColor(),
         "gui.tools.craft.around.storages.desc",
         previewAlpha
      );
   }

   private void statTile(
      MenuDrawContext menuDrawContext,
      ZenithStyle zenithStyle,
      int lastClickX,
      int lastClickY,
      float floatValue,
      float y,
      float width,
      float height,
      ItemStack defaultInstance,
      String name,
      String str,
      ZenithColor zenithColor,
      String key,
      float previewAlpha
   ) {
      boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
      float hot = this.hot("stat:" + name, contains);
      card(menuDrawContext, zenithStyle, floatValue, y, width, height, hot, previewAlpha);
      float f = 16.0F;
      tile(menuDrawContext, zenithStyle, floatValue + 5.0F, y + (height - f) / 2.0F, f, zenithColor, 0.35F, previewAlpha);
      this.drawItem(menuDrawContext, defaultInstance, floatValue + 7.0F, y + (height - 12.0F) / 2.0F, 12.0F, previewAlpha);
      Font font = Fonts.NEW_REGULAR.getFont(4.1F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
      float floatValue2 = floatValue + 5.0F + f + 5.0F;
      float width2 = floatValue + width - 4.0F - floatValue2;
      float f2 = font.height() + 2.0F + font2.height();
      float centeredTextY = y + (height - f2) / 2.0F;
      menuDrawContext.drawText(font, font.clip(name, width2), floatValue2, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawText(font2, font2.clip(str, width2), floatValue2, centeredTextY + font.height() + 2.0F, zenithColor.method7294(previewAlpha));
      if (contains) {
         HoverInfo.request(name, t(key));
      }
   }

   private void renderSource(
      MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, CraftRecipe craftRecipe, String key, int n
   ) {
      AutoCraftModule autoCraftModule = module();
      String identifier = craftRecipe.method5395(key);
      String name = craftRecipe.method5397(key, autoCraftModule);
      int n2 = autoCraftModule.method1121(identifier, craftRecipe.method5396(key));
      boolean flag = n2 >= n;
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 25.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      card(menuDrawContext, zenithStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), this.hot("src:" + key, contains), previewAlpha);
      float width = 16.0F;
      tile(
         menuDrawContext,
         zenithStyle,
         boundsData.getFloatValue() + 4.0F,
         boundsData.getFloatValue2() + (boundsData.height() - width) / 2.0F,
         width,
         flag ? OK : zenithStyle.getPrimaryColor().getZenithColor(),
         0.3F,
         previewAlpha
      );
      this.drawItem(
         menuDrawContext, autoCraftModule.method1117(identifier).getDefaultInstance(), boundsData.getFloatValue() + 6.0F, boundsData.getFloatValue2() + (boundsData.height() - 12.0F) / 2.0F, 12.0F, previewAlpha
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.4F);
      String text = Math.min(n2, 9999) + " / " + n;
      float floatValue = boundsData.getFloatValue() + 4.0F + width + 5.0F;
      float f4 = boundsData.getFloatValue() + boundsData.width() - 6.0F;
      menuDrawContext.drawText(
         font2, text, f4 - font2.width(text), boundsData.getFloatValue2() + 5.0F, (flag ? OK : zenithStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha)
      );
      menuDrawContext.drawText(
         font,
         font.clip(name, f4 - font2.width(text) - 5.0F - floatValue),
         floatValue,
         boundsData.getFloatValue2() + 4.5F,
         zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
      );
      progress(
         menuDrawContext,
         zenithStyle,
         floatValue,
         boundsData.getFloatValue2() + boundsData.height() - 7.0F,
         f4 - floatValue,
         2.5F,
         (float)n2 / n,
         flag ? OK : zenithStyle.getPrimaryColor().getZenithColor(),
         false,
         previewAlpha
      );
      if (contains) {
         HoverInfo.request(name, t("gui.tools.craft.source.desc"));
      }
   }

   private void miniEmpty(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float width, float height, String name, float previewAlpha) {
      card(menuDrawContext, zenithStyle, floatValue, y, width, height, 0.0F, previewAlpha);

      for (int i = 0; i < 2; i++) {
         float f = y + 7.0F + i * 13.0F;
         skeleton(menuDrawContext, zenithStyle, floatValue + 6.0F, f, 10.0F, 10.0F, i, previewAlpha * 0.8F);
         skeleton(menuDrawContext, zenithStyle, floatValue + 21.0F, f + 1.5F, (width - 30.0F) * (i == 0 ? 0.7F : 0.5F), 3.0F, i, previewAlpha * 0.8F);
         skeleton(menuDrawContext, zenithStyle, floatValue + 21.0F, f + 6.5F, (width - 30.0F) * 0.9F, 2.0F, i + 1, previewAlpha * 0.6F);
      }

      Font font = Fonts.NEW_REGULAR.getFont(4.3F);
      String clip = font.clip(name, width - 12.0F);
      menuDrawContext.drawText(
         font, clip, floatValue + (width - font.width(clip)) / 2.0F, y + height - font.height() - 6.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
   }

   private static String capitalize(String str) {
      return str != null && !str.isEmpty() ? str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1) : "";
   }

   private AutoCraftToolPanel.Readiness readiness(CraftRecipe craftRecipe, int possibleCrafts) {
      if (craftRecipe == null) {
         return AutoCraftToolPanel.Readiness.NO_PRESET;
      } else if (!this.nearby.method1131()) {
         return AutoCraftToolPanel.Readiness.NO_WORKBENCH;
      } else if (possibleCrafts > 0) {
         return AutoCraftToolPanel.Readiness.READY;
      } else {
         return this.nearby.getIntValue() > 0 ? AutoCraftToolPanel.Readiness.FROM_CHESTS : AutoCraftToolPanel.Readiness.NO_RESOURCES;
      }
   }

   private String stateText(AutoCraftToolPanel.Readiness readiness) {
      AutoCraftModule autoCraftModule = module();
      if (autoCraftModule.isBinding()) {
         return t("gui.tools.craft.state.binding");
      } else if (autoCraftModule.isEnabled()) {
         String str = autoCraftModule.method1119();
         return str.isBlank() ? t("gui.tools.craft.state.running") : str;
      } else {
         return switch (readiness) {
            case NO_PRESET -> t("gui.tools.craft.state.noPreset");
            case NO_WORKBENCH -> t("gui.tools.craft.state.noWorkbench");
            case NO_RESOURCES -> t("gui.tools.craft.state.noResources");
            case FROM_CHESTS -> t("gui.tools.craft.state.fromChests");
            case READY -> t("gui.tools.craft.state.ready");
         };
      }
   }

   private ZenithColor stateColor(AutoCraftToolPanel.Readiness readiness, ZenithStyle zenithStyle) {
      AutoCraftModule autoCraftModule = module();
      if (!autoCraftModule.isEnabled() && !autoCraftModule.isBinding()) {
         return switch (readiness) {
            case NO_PRESET -> zenithStyle.getTextTertiary().getZenithColor();
            default -> DANGER;
            case FROM_CHESTS -> zenithStyle.getPrimaryColor().getZenithColor();
            case READY -> OK;
         };
      } else {
         return zenithStyle.getPrimaryColor().getZenithColor();
      }
   }

   private int possibleCrafts(CraftRecipe craftRecipe) {
      AutoCraftModule autoCraftModule = module();
      var map = craftRecipe.method5388();
      if (map.isEmpty()) {
         return 0;
      } else {
         int n = Integer.MAX_VALUE;

         for (Map.Entry entry : map.entrySet()) {
            String key = (String)entry.getKey();
            int n2 = Math.max(1, (Integer)entry.getValue());
            int n3 = autoCraftModule.method1121(craftRecipe.method5395(key), craftRecipe.method5396(key));
            n = Math.min(n, n3 / n2);
            if (n == 0) {
               return 0;
            }
         }

         return n == Integer.MAX_VALUE ? 0 : n;
      }
   }

   private boolean craftableNow(CraftRecipe craftRecipe) {
      var map = craftRecipe.method5388();
      if (!map.isEmpty() && !this.inventory.isEmpty()) {
         for (Map.Entry entry : map.entrySet()) {
            String key = craftRecipe.method5395((String)entry.getKey());
            if (this.inventory.getOrDefault(key, 0) < Math.max(1, (Integer)entry.getValue())) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float panelAlpha, float f, float y) {
      this.buttonBounds = null;
      this.filterBounds = null;
      super.render(menuDrawContext, lastClickX, lastClickY, panelAlpha, f, y);
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      BoundsData mainViewport = this.mainViewport();
      if (mainViewport != null && mainViewport.contains(lastClickX, lastClickY) && enum2 == Enum3.LEFT) {
         if (this.buttonBounds != null && this.buttonBounds.contains(lastClickX, lastClickY)) {
            this.pressButton("start", lastClickX, lastClickY);
            module().toggle();
            Zenith.getInstance().getClientSounds().method1570(false);
            this.invalidate();
            return true;
         }

         if (this.filterBounds != null && this.filterBounds.contains(lastClickX, lastClickY)) {
            int hovered = this.filterStrip.hovered();
            if (hovered >= 0) {
               if (hovered != this.filter) {
                  this.filter = hovered;
                  this.resetScroll();
                  this.replayMainFrom(this.recipesFrom);
                  this.invalidate();
               }

               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            }
         }
      }

      return super.onMouseClicked(lastClickX, lastClickY, enum2);
   }

   @Override
   public void tick() {
      AutoCraftModule autoCraftModule = module();
      long now = System.currentTimeMillis();
      if (now - this.probedAt >= 400L) {
         this.probedAt = now;
         this.nearby = autoCraftModule.method1120();
         this.inventory = autoCraftModule.method1122();
      }

      int size = autoCraftModule.method1092(autoCraftModule.method1098()).size();
      String contentSignature = autoCraftModule.getText2() + "|" + size + "|" + this.filter + "|" + this.searchQuery() + (this.filter == 1 ? "|" + this.inventory.hashCode() : "");
      if (!contentSignature.equals(this.contentSignature)) {
         if (this.summaries.size() > 2048) {
            this.summaries.clear();
         }

         this.contentSignature = contentSignature;
         this.invalidate();
      }
   }

   private static enum Readiness {
      NO_PRESET,
      NO_WORKBENCH,
      NO_RESOURCES,
      FROM_CHESTS,
      READY;
   }
}

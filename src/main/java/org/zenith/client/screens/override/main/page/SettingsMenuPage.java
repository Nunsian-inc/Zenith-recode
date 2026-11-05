package org.zenith.client.screens.override.main.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.ChatOptionsScreen;
import net.minecraft.client.gui.screens.options.FontOptionsScreen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.zenith.MinecraftAccess;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.component.MenuNavigationBar;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.render.SodiumOptionsReader;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class SettingsMenuPage implements MainMenuPage, MinecraftAccess {
   private static final float COLUMN_WIDTH = MenuTheme.px(320.0F);
   private static final float TABS_GAP = MenuTheme.px(16.0F);
   private static final float SEARCH_GAP = MenuTheme.px(12.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(42.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float RAIL_GAP = MenuTheme.px(16.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float SHELL_CHROME = MenuTheme.px(312.0F);
   private static final int MIN_COLUMNS = 2;
   private static final int MAX_COLUMNS = 4;
   private final List<SettingsMenuPage.Category> categories = new ArrayList<>();
   private final Map<String, List<AbstractWidget>> harvested = new LinkedHashMap<>();
   private final Map<String, OptionsSubScreen> sources = new LinkedHashMap<>();
   private final MenuScroll scroll = new MenuScroll();
   private final MenuTextField search = new MenuTextField(MenuIcons.SEARCH, MenuTheme.t("menu.settings.search"));
   private MenuNavigationBar tabs;
   private boolean sodiumAdded;
   private int active;
   private AbstractWidget dragged;
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private int columns = 2;
   private float gridHeight = MenuTheme.px(334.0F);

   public SettingsMenuPage() {
      this.vanilla("menu.settings.video", MenuIcons.VIDEO, () -> new VideoSettingsScreen(mc.gui.screen(), mc, mc.options));
      this.vanilla("menu.settings.controls", MenuIcons.CONTROLS, () -> new ControlsScreen(mc.gui.screen(), mc.options));
      this.vanilla("menu.settings.mouse", MenuIcons.MOUSE, () -> new MouseSettingsScreen(mc.gui.screen(), mc.options));
      this.vanilla("menu.settings.sound", MenuIcons.SOUND, () -> new SoundOptionsScreen(mc.gui.screen(), mc.options));
      this.vanilla("menu.settings.chat", MenuIcons.CHAT, () -> new ChatOptionsScreen(mc.gui.screen(), mc.options));
      this.vanilla("menu.settings.skin", MenuIcons.COSMETICS, () -> new SkinCustomizationScreen(mc.gui.screen(), mc.options));
      this.vanilla("menu.settings.accessibility", MenuIcons.ACCESSIBILITY, () -> new AccessibilityOptionsScreen(mc.gui.screen(), mc.options));
      this.vanilla("menu.settings.online", MenuIcons.WORLD, () -> new OnlineOptionsScreen(mc.gui.screen(), mc.options));
      this.vanilla("menu.settings.font", MenuIcons.TEXT, () -> new FontOptionsScreen(mc.gui.screen(), mc.options));
      this.rebuildTabs();
      this.search.setOnChange(this.scroll::reset);
   }

   private void addSodiumTabs() {
      if (!this.sodiumAdded) {
         List<SodiumOptionsReader.Data> datas = SodiumOptionsReader.method1598();
         if (!datas.isEmpty()) {
            for (SodiumOptionsReader.Data data : datas) {
               this.categories.add(new SettingsMenuPage.Category(data.name(), MenuIcons.SPARKLE, () -> {
                  ArrayList var1x = new ArrayList();

                  for (Object var3x : data.getItems()) {
                     AbstractWidget abstractWidget = SodiumOptionsReader.method1599(var3x);
                     if (abstractWidget != null) {
                        var1x.add(abstractWidget);
                     }
                  }

                  return var1x;
               }));
            }

            this.sodiumAdded = true;
            this.rebuildTabs();
         }
      }
   }

   private void rebuildTabs() {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < this.categories.size(); i++) {
         SettingsMenuPage.Category category = this.categories.get(i);
         int active = i;
         arrayList.add(new MenuNavigationBar.Item(category.icon(), category.label(), () -> this.select(active)));
      }

      this.tabs = new MenuNavigationBar(arrayList);
   }

   private void vanilla(String label, MenuIcon icon, Supplier<OptionsSubScreen> supplier) {
      this.categories.add(new SettingsMenuPage.Category(label, icon, () -> this.harvestVanilla(label, supplier)));
   }

   private List<AbstractWidget> harvestVanilla(String label, Supplier<OptionsSubScreen> supplier) {
      ArrayList<AbstractWidget> abstractWidgets = new ArrayList<>();
      if (mc.options == null) {
         return abstractWidgets;
      } else {
         OptionsSubScreen optionsSubScreen = (OptionsSubScreen)supplier.get();
         optionsSubScreen.init(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
         this.sources.put(label, optionsSubScreen);
         OptionsList list = optionsSubScreen.list;
         if (list != null) {
            for (Object obj : list.children()) {
               if (obj instanceof ContainerEventHandler containerEventHandler) {
                  for (GuiEventListener guiEventListener : containerEventHandler.children()) {
                     if (guiEventListener instanceof AbstractWidget abstractWidget) {
                        abstractWidgets.add(abstractWidget);
                     }
                  }
               }
            }
         }

         return abstractWidgets;
      }
   }

   private void select(int active) {
      if (active != this.active && active >= 0 && active < this.categories.size()) {
         this.active = active;
         this.scroll.reset();
         this.dragged = null;
      }
   }

   @Override
   public void update() {
      int size = this.visible().size();
      float px = mc.getWindow() == null ? MenuTheme.px(1920.0F) : mc.getWindow().getGuiScaledWidth();
      float f = mc.getWindow() == null ? MenuTheme.px(1080.0F) : mc.getWindow().getGuiScaledHeight();
      float f2 = px - MenuTheme.px(96.0F) - RAIL_GAP - RAIL_WIDTH;
      int n = (int)Math.floor((f2 + ROW_GAP) / (COLUMN_WIDTH + ROW_GAP));
      this.columns = Math.max(2, Math.min(4, Math.min(n, Math.max(1, size))));
      float f3 = ROW_HEIGHT + ROW_GAP;
      int n2 = Math.max(1, (size + this.columns - 1) / this.columns);
      float f4 = n2 * f3 - ROW_GAP;
      float f5 = f - SHELL_CHROME - MenuNavigationBar.HEIGHT - TABS_GAP - MenuTextField.HEIGHT - SEARCH_GAP;
      this.gridHeight = Math.max(f3, Math.min(f4, f5));
   }

   private float listWidth() {
      return this.columns * COLUMN_WIDTH + (this.columns - 1) * ROW_GAP;
   }

   @Override
   public float contentWidth() {
      return this.listWidth() + RAIL_GAP + RAIL_WIDTH;
   }

   @Override
   public float contentHeight() {
      return MenuNavigationBar.HEIGHT + TABS_GAP + MenuTextField.HEIGHT + SEARCH_GAP + this.gridHeight;
   }

   private boolean searching() {
      return !this.search.getValue().trim().isEmpty();
   }

   @Override
   public void onShow() {
      this.addSodiumTabs();
      this.widgets();
   }

   @Override
   public void onHide() {
      VanillaOptionStyle.end();

      for (OptionsSubScreen optionsSubScreen : this.sources.values()) {
         optionsSubScreen.removed();
      }

      this.sources.clear();
      this.harvested.clear();
      this.dragged = null;
      if (mc.options != null) {
         mc.options.save();
      }
   }

   private List<AbstractWidget> widgets() {
      return this.widgetsOf(this.categories.get(this.active));
   }

   private List<AbstractWidget> widgetsOf(SettingsMenuPage.Category category) {
      var abstractWidgets = this.harvested.get(category.label());
      if (abstractWidgets != null) {
         return abstractWidgets;
      } else {
         Object obj;
         try {
            obj = category.harvest().get();
         } catch (Exception e) {
            obj = new ArrayList();
         }

         this.harvested.put(category.label(), (List<AbstractWidget>)obj);
         return (List<AbstractWidget>)obj;
      }
   }

   private List<AbstractWidget> visible() {
      String lowerCase = this.search.getValue().trim().toLowerCase(Locale.ROOT);
      if (lowerCase.isEmpty()) {
         return this.widgets();
      } else {
         ArrayList<AbstractWidget> abstractWidgets = new ArrayList<>();

         for (SettingsMenuPage.Category category : this.categories) {
            for (AbstractWidget abstractWidget : this.widgetsOf(category)) {
               if (abstractWidget.getMessage().getString().toLowerCase(Locale.ROOT).contains(lowerCase)) {
                  abstractWidgets.add(abstractWidget);
               }
            }
         }

         return abstractWidgets;
      }
   }

   @Override
   public void layout(BoundsData boundsData) {
      var visible = this.visible();
      float f = boundsData.getFloatValue2() + MenuNavigationBar.HEIGHT + TABS_GAP;
      this.search.setBounds(boundsData.getFloatValue(), f, this.listWidth(), MenuTextField.HEIGHT);
      float y = f + MenuTextField.HEIGHT + SEARCH_GAP;
      this.viewport = new BoundsData(boundsData.getFloatValue(), y, this.listWidth(), this.gridHeight);
      float f2 = ROW_HEIGHT + ROW_GAP;
      int n = (visible.size() + this.columns - 1) / this.columns;
      this.scroll.update(this.gridHeight, n == 0 ? 0.0F : n * f2 - ROW_GAP);
      float f3 = y - this.scroll.offset();

      for (int i = 0; i < visible.size(); i++) {
         AbstractWidget abstractWidget = (AbstractWidget)visible.get(i);
         float floatValue = boundsData.getFloatValue() + i % this.columns * (COLUMN_WIDTH + ROW_GAP);
         abstractWidget.setX(Math.round(MenuTheme.snap(floatValue)));
         abstractWidget.setY(Math.round(MenuTheme.snap(f3 + i / this.columns * f2)));
         abstractWidget.setWidth(Math.round(COLUMN_WIDTH));
      }
   }

   @Override
   public void render(MenuDrawContext graphics, float mouseX, float mouseY, BoundsData boundsData) {
      List<AbstractWidget> visible = this.visible();
      this.search.render(graphics, mouseX, mouseY);
      if (visible.isEmpty()) {
         Font medium = MenuTheme.medium(12.0F);
         String text = this.searching()
            ? MenuTheme.t("menu.settings.noMatch") + " \"" + this.search.getValue().trim() + "\""
            : MenuTheme.t("menu.settings.noOptions");
         graphics.drawText(
            medium,
            text,
            boundsData.getFloatValue() + (this.listWidth() - medium.width(text)) / 2.0F,
            this.viewport.getFloatValue2() + (this.gridHeight - medium.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      } else {
         graphics.enableScissor(
            this.viewport.getFloatValue(),
            this.viewport.getFloatValue2(),
            this.viewport.getFloatValue() + this.listWidth(),
            this.viewport.getFloatValue2() + this.gridHeight
         );
         VanillaOptionStyle.begin();

         try {
            for (AbstractWidget abstractWidget : visible) {
               if (!(abstractWidget.getY() + abstractWidget.getHeight() < this.viewport.getFloatValue2()) && !(abstractWidget.getY() > this.viewport.getFloatValue2() + this.gridHeight)) {
                  abstractWidget.extractRenderState(graphics, (int)mouseX, (int)mouseY, 0.0F);
               }
            }
         } finally {
            VanillaOptionStyle.end();
         }

         graphics.disableScissor();
         this.scroll.renderRail(graphics, boundsData.getFloatValue() + this.listWidth() + RAIL_GAP, this.viewport.getFloatValue2(), this.gridHeight);
      }

      this.tabs.render(graphics, mouseX, mouseY, boundsData.getFloatValue() + boundsData.width() / 2.0F, boundsData.getFloatValue2(), this.active);
   }

   private static MouseButtonEvent zenith$click(double x, double y, int glfwButton) {
      return new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(glfwButton), org.zenith.utility.misc.KeyCompat.modsToSdl(0)));
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int glfwButton) {
      if (this.search.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (this.tabs.mouseClicked(lastClickX, lastClickY)) {
         return true;
      } else if (!this.viewport.contains(lastClickX, lastClickY)) {
         return false;
      } else {
         for (AbstractWidget dragged : this.visible()) {
            if (dragged.mouseClicked(zenith$click(lastClickX, lastClickY, glfwButton), false)) {
               this.dragged = dragged;
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean mouseReleased(double x, double y, int glfwButton) {
      AbstractWidget dragged = this.dragged;
      this.dragged = null;
      if (dragged == null) {
         return false;
      } else {
         dragged.mouseReleased(zenith$click(x, y, glfwButton));
         if (mc.options != null) {
            mc.options.save();
         }

         return true;
      }
   }

   @Override
   public boolean mouseDragged(double x, double y, int glfwButton) {
      return this.dragged == null ? false : this.dragged.mouseDragged(zenith$click(x, y, glfwButton), 0.0, 0.0);
   }

   @Override
   public boolean mouseScrolled(double d, double d2, double d3) {
      return this.scroll.scroll(d3, ROW_HEIGHT + ROW_GAP);
   }

   @Override
   public boolean keyPressed(int n, int n2, int n3) {
      return this.search.keyPressed(n, n2, n3);
   }

   @Override
   public boolean charTyped(char c, int n) {
      return this.search.charTyped(c, n);
   }

   private static final class Category {
      private final String label;
      private final MenuIcon icon;
      private final Supplier<List<AbstractWidget>> harvest;

      public String label() {
         return this.label;
      }

      public MenuIcon icon() {
         return this.icon;
      }

      public Supplier<List<AbstractWidget>> harvest() {
         return this.harvest;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Category other)) return false;
         return java.util.Objects.equals(this.label, other.label) && java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.harvest, other.harvest);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(label, icon, harvest);
      }

      @Override
      public String toString() {
         return "Category[" + "label=" + this.label + ", " + "icon=" + this.icon + ", " + "harvest=" + this.harvest + "]";
      }

      private Category(String label, MenuIcon icon, Supplier<List<AbstractWidget>> harvest) {
         this.label = label;
         this.icon = icon;
         this.harvest = harvest;
      }
   }
}

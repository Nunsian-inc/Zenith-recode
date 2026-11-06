package org.zenith.client.screens.override.main;

import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.component.MenuNavigationBar;
import org.zenith.client.screens.override.main.page.AccountManagerPage;
import org.zenith.client.screens.override.main.page.HomeMenuPage;
import org.zenith.client.screens.override.main.page.MainMenuPage;
import org.zenith.client.screens.override.main.page.MultiplayerMenuPage;
import org.zenith.client.screens.override.main.page.ProxyMenuPage;
import org.zenith.client.screens.override.main.page.SettingsMenuPage;
import org.zenith.client.screens.override.main.page.SingleplayerMenuPage;
import org.zenith.client.screens.override.particle.MenuParticleRenderer;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class MainMenuScreen extends Screen implements MinecraftAccess {
   public static final int HOME_TAB = 0;
   public static final int SINGLEPLAYER_TAB = 1;
   public static final int MULTIPLAYER_TAB = 2;
   public static final int ACCOUNTS_TAB = 3;
   public static final int PROXY_TAB = 4;
   public static final int SETTINGS_TAB = 5;
   private static final float SECTION_GUTTER = MenuTheme.px(48.0F);
   private static final float NAVIGATION_GAP = MenuTheme.px(32.0F);
   private static final float SCREEN_MARGIN = MenuTheme.px(24.0F);
   private final MainMenuBlurRenderer backdrop = new MainMenuBlurRenderer();
   private final List<MainMenuPage> pages = new ArrayList<>();
   private final MenuNavigationBar navigationBar;
   private final Animation pageTransition = new Animation(260L, 1.0F, Easing.INSTANCE7);
   private final Animation openTransition = new Animation(380L, 0.0F, Easing.INSTANCE7);
   private int activeIndex;
   private float fit = 1.0F;

   public MainMenuScreen() {
      super(Component.literal("Zenith"));
      this.pages.add(new HomeMenuPage());
      this.pages.add(new SingleplayerMenuPage(this));
      this.pages.add(new MultiplayerMenuPage(this));
      this.pages.add(new AccountManagerPage());
      this.pages.add(new ProxyMenuPage());
      this.pages.add(new SettingsMenuPage());
      this.navigationBar = new MenuNavigationBar(
         List.of(
            new MenuNavigationBar.Item(MenuIcons.HOME, "menu.nav.home", () -> this.select(0)),
            new MenuNavigationBar.Item(MenuIcons.SINGLEPLAYER, "menu.nav.singleplayer", () -> this.select(1)),
            new MenuNavigationBar.Item(MenuIcons.MULTIPLAYER, "menu.nav.multiplayer", () -> this.select(2)),
            new MenuNavigationBar.Item(MenuIcons.ALT_MANAGER, "menu.nav.accounts", () -> this.select(3)),
            new MenuNavigationBar.Item(MenuIcons.PROXY, "menu.nav.proxy", () -> this.select(4)),
            new MenuNavigationBar.Item(MenuIcons.SETTINGS, "menu.nav.settings", () -> this.select(5)),
            new MenuNavigationBar.Item(MenuIcons.EXIT, "menu.nav.exit", () -> mc.stop())
         )
      );
   }

   public void select(int activeIndex) {
      if (activeIndex != this.activeIndex && activeIndex >= 0 && activeIndex < this.pages.size()) {
         this.pages.get(this.activeIndex).onHide();
         this.activeIndex = activeIndex;
         this.pages.get(this.activeIndex).onShow();
         this.pageTransition.snapTo(0.0F);
      }
   }

   private MainMenuPage page() {
      return this.pages.get(this.activeIndex);
   }

   @Override
   public void init() {
      Zenith.getInstance().getMenuParticleRenderer().init();
      this.openTransition.snapTo(0.0F);
      this.pageTransition.snapTo(0.0F);
      super.init();
   }

   @Override
   public void added() {
      super.added();
      this.openTransition.snapTo(0.0F);
      this.pageTransition.snapTo(0.0F);
      this.page().onShow();
   }

   @Override
   public void tick() {
      this.page().tick();
      super.tick();
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext graphics = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      Window window = mc.getWindow();
      float guiScaledWidth = window.getGuiScaledWidth();
      float guiScaledHeight = window.getGuiScaledHeight();
      MenuParticleRenderer.renderBackgroundAndParticles(graphics);
      this.backdrop.capture(graphics, MenuTheme.px(36.0F));
      MenuSurface.setMenuSource(this.backdrop.texture());
      this.openTransition.animateTo(1.0F);
      this.pageTransition.animateTo(1.0F);
      MainMenuPage page = this.page();
      page.update();
      float footerHeight = page.footerHeight();
      float f = MenuSkin.HEADER_HEIGHT
         + SECTION_GUTTER
         + MenuNavigationBar.HEIGHT
         + NAVIGATION_GAP
         + page.contentHeight()
         + (footerHeight > 0.0F ? SECTION_GUTTER + footerHeight : 0.0F);
      float measureWidth = this.navigationBar.measureWidth(this.activeIndex);
      float f2 = Math.max(measureWidth, Math.max(page.contentWidth(), page.footerWidth()));
      float f3 = Math.min(1.0F, (guiScaledHeight - SCREEN_MARGIN * 2.0F) / Math.max(1.0F, f));
      float f4 = Math.min(1.0F, (guiScaledWidth - SCREEN_MARGIN * 2.0F) / Math.max(1.0F, f2));
      this.fit = Math.max(0.35F, Math.min(f3, f4));
      float f5 = guiScaledWidth / this.fit;
      float f6 = guiScaledHeight / this.fit;
      float snap = MenuTheme.snap(Math.max(SCREEN_MARGIN, (f6 - f) / 2.0F));
      float snap2 = MenuTheme.snap(f5 / 2.0F);
      graphics.pose().pushMatrix();
      graphics.pose().scale(this.fit, this.fit);
      float y = (1.0F - this.openTransition.getValue()) * MenuTheme.px(40.0F);
      graphics.pose().pushMatrix();
      graphics.pose().translate(0.0F, y);
      float mouseX2 = mouseX / this.fit;
      float mouseY2 = mouseY / this.fit;
      this.renderIdentity(graphics, snap2, snap);
      float y2 = snap + MenuSkin.HEADER_HEIGHT + SECTION_GUTTER;
      float floatValue = y2 + MenuNavigationBar.HEIGHT + NAVIGATION_GAP;
      BoundsData boundsData = new BoundsData(MenuTheme.snap(snap2 - page.contentWidth() / 2.0F), MenuTheme.snap(floatValue), page.contentWidth(), page.contentHeight());
      page.layout(boundsData);
      float y3 = (1.0F - this.pageTransition.getValue()) * MenuTheme.px(24.0F);
      graphics.pose().pushMatrix();
      graphics.pose().translate(0.0F, y3);
      page.render(graphics, mouseX2, mouseY2, boundsData);
      if (footerHeight > 0.0F) {
         BoundsData boundsData2 = new BoundsData(
            MenuTheme.snap(snap2 - page.footerWidth() / 2.0F), floatValue + page.contentHeight() + SECTION_GUTTER, page.footerWidth(), footerHeight
         );
         page.renderFooter(graphics, mouseX2, mouseY2, boundsData2);
      }

      graphics.pose().popMatrix();
      this.navigationBar.render(graphics, mouseX2, mouseY2, snap2, y2, this.activeIndex);
      graphics.pose().popMatrix();
      graphics.pose().popMatrix();
   }

   private void renderIdentity(MenuDrawContext graphics, float f, float snap) {
      Font medium = MenuTheme.medium(12.0F);
      Font regular = MenuTheme.regular(12.0F);
      String text = "Zenith";
      String str = MenuTheme.t("menu.welcome");
      String username = Zenith.getInstance().getEntry().getUsername();
      if (username == null || username.isEmpty()) {
         username = MenuTheme.t("menu.guest");
      }

      float height = MenuSkin.HEADER_HEIGHT;
      float width = MenuSkin.TILE_INSET
         + MenuSkin.HEADER_TILE
         + MenuSkin.dividerWidth()
         + medium.width(text)
         + MenuSkin.dividerWidth()
         + regular.width(str)
         + MenuSkin.GAP
         + medium.width(username)
         + MenuSkin.EDGE;
      float floatValue = MenuTheme.snap(f - width / 2.0F);
      MenuSkin.panel(graphics, floatValue, snap, width, height);
      float y = snap + MenuSkin.TILE_INSET;
      MenuSkin.accentTile(graphics, floatValue + MenuSkin.TILE_INSET, y, MenuSkin.HEADER_TILE, MenuIcons.ZENITH);
      float divider = MenuSkin.divider(graphics, floatValue + MenuSkin.TILE_INSET + MenuSkin.HEADER_TILE, snap, height);
      graphics.drawText(medium, text, divider, snap + (height - medium.height()) / 2.0F, MenuTheme.text());
      divider += medium.width(text);
      divider = MenuSkin.divider(graphics, divider, snap, height);
      graphics.drawText(regular, str, divider, snap + (height - regular.height()) / 2.0F, MenuTheme.textSecondary());
      divider += regular.width(str) + MenuSkin.GAP;
      graphics.drawText(medium, username, divider, snap + (height - medium.height()) / 2.0F, MenuTheme.accent());
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseClicked(double x, double y, int mouseToGlfw) {
      double d = x / this.fit;
      double y2 = y / this.fit;
      if (this.navigationBar.mouseClicked(d, y2)) {
         return true;
      } else {
         return this.page().mouseClicked(d, y2, mouseToGlfw) ? true : super.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))), false);
      }
   }

   @Override
   public boolean mouseReleased(MouseButtonEvent click) {
      return this.mouseReleased(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseReleased(double x, double y, int mouseToGlfw) {
      return this.page().mouseReleased(x / this.fit, y / this.fit, mouseToGlfw) ? true : super.mouseReleased(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))));
   }

   @Override
   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      return this.mouseDragged(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()), offsetX, offsetY);
   }

   public boolean mouseDragged(double x, double y, int mouseToGlfw, double offsetX, double offsetY) {
      return this.page().mouseDragged(x / this.fit, y / this.fit, mouseToGlfw)
         ? true
         : super.mouseDragged(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))), offsetX, offsetY);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.page().mouseScrolled(mouseX / this.fit, mouseY / this.fit, verticalAmount)
         ? true
         : super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), input.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return this.page().keyPressed(keyCode, keycode, modifiers) ? true : super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
   }

   @Override
   public boolean charTyped(CharacterEvent input) {
      return this.charTyped((char)input.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      return this.page().charTyped(codepoint, n) ? true : super.charTyped(new CharacterEvent(codepoint));
   }

   @Override
   public void removed() {
      this.page().onHide();
      MenuSurface.setMenuSource(null);
      this.backdrop.close();
      super.removed();
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void extractTransparentBackground(GuiGraphicsExtractor context) {
   }
}

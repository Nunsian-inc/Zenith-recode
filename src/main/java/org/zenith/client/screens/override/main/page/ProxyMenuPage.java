package org.zenith.client.screens.override.main.page;

import java.util.List;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.misc.ProxyConfig;
import org.zenith.utility.network.Proxies;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class ProxyMenuPage implements MainMenuPage {
   private static final float WIDTH = MenuTheme.px(431.0F);
   private static final float LIST_HEIGHT = MenuTheme.px(342.0F);
   private static final float CONTROLS_GAP = MenuTheme.px(48.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(62.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float ROW_RADIUS = MenuTheme.px(12.0F);
   private static final float ROW_PADDING = MenuTheme.px(16.0F);
   private static final float RAIL_GAP = MenuTheme.px(16.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float CONTROL_GAP = MenuTheme.px(8.0F);
   private static final float BUTTON_GAP = MenuTheme.px(4.0F);
   private final MenuTextField addressField = new MenuTextField(MenuIcons.ADDRESS, "host:port[:user:pass]");
   private final MenuButton typeButton = MenuButton.ghost(MenuIcons.PROXY, "SOCKS5");
   private final MenuButton addButton = MenuButton.iconOnly(MenuIcons.ADD, MenuButton.Style.ACCENT);
   private final MenuScroll scroll = new MenuScroll();
   private ProxyConfig.Type pendingType = ProxyConfig.Type.SOCKS5;
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private BoundsData[] rowBounds = new BoundsData[0];
   private BoundsData[] removeBounds = new BoundsData[0];
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

   public ProxyMenuPage() {
      this.addButton.onClick(this::addProxy);
      this.addressField.setOnEnter(this::addProxy);
      this.typeButton.onClick(() -> {
         this.pendingType = this.pendingType == ProxyConfig.Type.SOCKS5 ? ProxyConfig.Type.SOCKS4 : ProxyConfig.Type.SOCKS5;
         this.typeButton.setLabel(this.pendingType.name());
      });
   }

   private Proxies manager() {
      return Zenith.getInstance().getProxies();
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return LIST_HEIGHT + CONTROLS_GAP + MenuTextField.HEIGHT;
   }

   private void addProxy() {
      ProxyConfig name = ProxyConfig.method439(this.addressField.getValue(), this.pendingType);
      if (name != null) {
         this.manager().method6537(name);
         this.addressField.clear();
         this.addressField.setFocused(false);
      }
   }

   @Override
   public void layout(BoundsData boundsData) {
      float f = boundsData.getFloatValue2() + LIST_HEIGHT + CONTROLS_GAP;
      float preferredWidth = this.typeButton.preferredWidth();
      float preferredWidth2 = boundsData.width() - preferredWidth - MenuButton.HEIGHT - CONTROL_GAP - BUTTON_GAP;
      this.addressField.setBounds(boundsData.getFloatValue(), f, preferredWidth2, MenuTextField.HEIGHT);
      this.typeButton.setBounds(boundsData.getFloatValue() + preferredWidth2 + CONTROL_GAP, f, preferredWidth, MenuButton.HEIGHT);
      this.addButton.setBounds(boundsData.getFloatValue() + boundsData.width() - MenuButton.HEIGHT, f, MenuButton.HEIGHT, MenuButton.HEIGHT);
   }

   @Override
   public void render(MenuDrawContext graphics, float mouseX, float mouseY, BoundsData boundsData) {
      var proxyConfigs = this.manager().method6535();
      BoundsData listArea = MenuSkin.listArea(graphics, boundsData, LIST_HEIGHT);
      float width = listArea.width() - RAIL_GAP - RAIL_WIDTH;
      float content = proxyConfigs.isEmpty() ? 0.0F : proxyConfigs.size() * ROW_HEIGHT + (proxyConfigs.size() - 1) * ROW_GAP;
      this.scroll.update(listArea.height(), content);
      this.viewport = new BoundsData(listArea.getFloatValue(), listArea.getFloatValue2(), width, listArea.height());
      this.rowBounds = new BoundsData[proxyConfigs.size()];
      this.removeBounds = new BoundsData[proxyConfigs.size()];
      float f = this.entrance.animateTo(1.0F);
      graphics.enableScissor(listArea.getFloatValue(), listArea.getFloatValue2(), listArea.getFloatValue() + width, listArea.getFloatValue2() + listArea.height());
      float y = listArea.getFloatValue2() - this.scroll.offset();

      for (int i = 0; i < proxyConfigs.size(); i++) {
         this.renderRow(graphics, mouseX, mouseY, (ProxyConfig)proxyConfigs.get(i), i, listArea.getFloatValue(), y + MenuSkin.enterOffset(f, i), width);
         this.rowBounds[i] = new BoundsData(listArea.getFloatValue(), y, width, ROW_HEIGHT);
         y += ROW_HEIGHT + ROW_GAP;
      }

      graphics.disableScissor();
      if (proxyConfigs.isEmpty()) {
         Font medium = MenuTheme.medium(12.0F);
         String text = MenuTheme.t("menu.proxy.empty");
         graphics.drawText(
            medium,
            text,
            listArea.getFloatValue() + (width - medium.width(text)) / 2.0F,
            listArea.getFloatValue2() + (listArea.height() - medium.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRail(graphics, listArea.getFloatValue() + listArea.width() - RAIL_WIDTH, listArea.getFloatValue2(), listArea.height());
      this.addressField.render(graphics, mouseX, mouseY);
      this.typeButton.render(graphics, mouseX, mouseY);
      this.addButton.render(graphics, mouseX, mouseY);
   }

   private void renderRow(MenuDrawContext graphics, float mouseX, float mouseY, ProxyConfig proxyConfig, int n, float floatValue, float snap, float width) {
      BoundsData boundsData = new BoundsData(floatValue, snap, width, ROW_HEIGHT);
      this.rowBounds[n] = boundsData;
      boolean flag = this.manager().getIntValue2() == n;
      boolean contains = boundsData.contains(mouseX, mouseY);
      ZenithColor plate = contains ? MenuTheme.plate().method7295(MenuTheme.track(), 0.5F) : MenuTheme.plate();
      MenuSkin.plate(graphics, floatValue, snap, width, ROW_HEIGHT, flag ? plate.method7295(MenuTheme.accent(), 0.18F) : plate);
      Font medium = MenuTheme.medium(11.0F);
      Font regular = MenuTheme.regular(10.0F);
      float px = MenuTheme.px(30.0F);
      float centeredTextY = snap + (ROW_HEIGHT - px) / 2.0F;
      float f = MenuSkin.ROW_TILE;
      float y = snap + (ROW_HEIGHT - f) / 2.0F;
      if (flag) {
         MenuSkin.accentTile(graphics, floatValue + MenuSkin.EDGE, y, f, MenuIcons.PROXY);
      } else {
         MenuSkin.mutedTile(graphics, floatValue + MenuSkin.EDGE, y, f, MenuIcons.PROXY, MenuTheme.textTertiary());
      }

      float divider = MenuSkin.divider(graphics, floatValue + MenuSkin.EDGE + f, snap, ROW_HEIGHT);
      graphics.drawText(medium, proxyConfig.method441(), divider, centeredTextY, MenuTheme.text());
      String text = proxyConfig.getType().name() + (proxyConfig.method440() ? " - " + MenuTheme.t("menu.proxy.authenticated") : "");
      graphics.drawText(regular, text, divider, centeredTextY + MenuTheme.px(18.0F), MenuTheme.textSecondary());
      String str = MenuTheme.t(flag ? "menu.proxy.active" : "menu.proxy.idle");
      Font font = MenuTheme.medium(10.0F);
      float f2 = floatValue + width - MenuSkin.EDGE;
      float px2 = MenuTheme.px(12.0F);
      float y2 = snap + (ROW_HEIGHT - px2) / 2.0F;
      BoundsData boundsData2 = new BoundsData(f2 - px2, y2, px2, px2);
      this.removeBounds[n] = boundsData2;
      graphics.drawText(
         font,
         str,
         boundsData2.getFloatValue() - MenuTheme.px(8.0F) - font.width(str),
         snap + (ROW_HEIGHT - font.height()) / 2.0F,
         flag ? MenuTheme.accent() : MenuTheme.textTertiary()
      );
      Font font2 = MenuIcons.REMOVE.font(12.0F);
      graphics.drawText(
         font2,
         MenuIcons.REMOVE.glyph(),
         boundsData2.getFloatValue(),
         y2 + (px2 - font2.height()) / 2.0F,
         boundsData2.contains(mouseX, mouseY) ? MenuTheme.danger() : MenuTheme.textTertiary()
      );
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int glfwButton) {
      if (this.addressField.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (this.typeButton.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (this.addButton.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (glfwButton == 0 && this.viewport.contains(lastClickX, lastClickY)) {
         for (int i = 0; i < this.rowBounds.length; i++) {
            if (this.removeBounds[i] != null && this.removeBounds[i].contains(lastClickX, lastClickY)) {
               this.manager().method6538(i);
               return true;
            }

            if (this.rowBounds[i] != null && this.rowBounds[i].contains(lastClickX, lastClickY)) {
               this.manager().select(i);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double d, double d2, double d3) {
      return this.scroll.scroll(d3, ROW_HEIGHT + ROW_GAP);
   }

   @Override
   public boolean keyPressed(int n, int n2, int n3) {
      return this.addressField.keyPressed(n, n2, n3);
   }

   @Override
   public boolean charTyped(char c, int n) {
      return this.addressField.charTyped(c, n);
   }
}

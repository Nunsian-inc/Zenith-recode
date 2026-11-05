package org.zenith.client.screens.override.main.page;

import com.mojang.blaze3d.platform.NativeImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.State;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MultiplayerMenuPage implements MainMenuPage, MinecraftAccess {
   private static final float WIDTH = MenuTheme.px(431.0F);
   private static final float LIST_HEIGHT = MenuTheme.px(342.0F);
   private static final float CONTROLS_GAP = MenuTheme.px(48.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(62.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float ROW_RADIUS = MenuTheme.px(12.0F);
   private static final float ROW_PADDING = MenuTheme.px(16.0F);
   private static final float ICON_SIZE = MenuTheme.px(30.0F);
   private static final float ICON_RADIUS = MenuTheme.px(6.0F);
   private static final float RAIL_GAP = MenuTheme.px(16.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float CONTROL_GAP = MenuTheme.px(8.0F);
   private static final ThreadPoolExecutor PINGER = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
   private final Screen parent;
   private final ServerStatusPinger pinger = new ServerStatusPinger();
   private final List<MultiplayerMenuPage.Row> rows = new ArrayList<>();
   private final MenuScroll scroll = new MenuScroll();
   private final MenuTextField nameField = new MenuTextField(MenuIcons.SERVER, MenuTheme.t("menu.multiplayer.name"));
   private final MenuTextField addressField = new MenuTextField(MenuIcons.ADDRESS, MenuTheme.t("menu.multiplayer.address"));
   private final MenuButton addButton = MenuButton.iconOnly(MenuIcons.ADD, MenuButton.Style.ACCENT);
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private ServerList serverList;
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

   public MultiplayerMenuPage(Screen parent) {
      this.parent = parent;
      this.addButton.onClick(this::addServer);
      this.nameField.setOnEnter(this::addServer);
      this.addressField.setOnEnter(this::addServer);
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return LIST_HEIGHT + CONTROLS_GAP + MenuTextField.HEIGHT;
   }

   @Override
   public void onShow() {
      ensureForeignMenuHooks();
      this.reload();
   }

   private static void ensureForeignMenuHooks() {
      try {
         Class clazz = Class.forName("ru.fiw.proxyserver.ProxyServer");
         Field field = clazz.getField("proxyMenuButton");
         if (field.get(null) == null) {
            field.set(null, Button.builder(Component.empty(), var0x -> {}).bounds(0, 0, 0, 0).build());
         }
      } catch (Throwable e) {
      }
   }

   @Override
   public void onHide() {
      this.pinger.removeAll();
      this.closeRows();
   }

   @Override
   public void tick() {
      this.pinger.tick();
   }

   private void closeRows() {
      for (MultiplayerMenuPage.Row row : this.rows) {
         row.icon.close();
      }

      this.rows.clear();
   }

   private void reload() {
      this.entrance.method2809(0.0F);
      this.closeRows();
      this.serverList = new ServerList(mc);
      this.serverList.load();

      for (int i = 0; i < this.serverList.size(); i++) {
         MultiplayerMenuPage.Row row = new MultiplayerMenuPage.Row(this.serverList.get(i));
         this.rows.add(row);
         row.ping();
      }

      this.scroll.reset();
   }

   private void addServer() {
      String ip = this.nameField.getValue().trim();
      String str = this.addressField.getValue().trim();
      if (!str.isEmpty()) {
         if (ip.isEmpty()) {
            ip = str;
         }

         if (this.serverList == null) {
            this.reload();
         }

         ServerData serverData = new ServerData(ip, str, Type.OTHER);
         this.serverList.add(serverData, false);
         this.serverList.save();
         this.nameField.clear();
         this.addressField.clear();
         this.nameField.setFocused(false);
         this.addressField.setFocused(false);
         this.reload();
      }
   }

   private void removeServer(MultiplayerMenuPage.Row row) {
      if (this.serverList != null) {
         this.serverList.remove(row.info);
         this.serverList.save();
         this.reload();
      }
   }

   private void joinServer(MultiplayerMenuPage.Row row) {
      ConnectScreen.startConnecting(this.parent, mc, ServerAddress.parseString(row.info.ip), row.info, false, null);
   }

   @Override
   public void layout(BoundsData boundsData) {
      float f = boundsData.getFloatValue2() + LIST_HEIGHT + CONTROLS_GAP;
      float preferredWidth = (boundsData.width() - MenuButton.HEIGHT - CONTROL_GAP * 2.0F) / 2.0F;
      this.nameField.setBounds(boundsData.getFloatValue(), f, preferredWidth, MenuTextField.HEIGHT);
      this.addressField.setBounds(boundsData.getFloatValue() + preferredWidth + CONTROL_GAP, f, preferredWidth, MenuTextField.HEIGHT);
      this.addButton.setBounds(boundsData.getFloatValue() + boundsData.width() - MenuButton.HEIGHT, f, MenuButton.HEIGHT, MenuButton.HEIGHT);
   }

   @Override
   public void render(MenuDrawContext graphics, float mouseX, float mouseY, BoundsData boundsData) {
      BoundsData listArea = MenuSkin.listArea(graphics, boundsData, LIST_HEIGHT);
      float width = listArea.width() - RAIL_GAP - RAIL_WIDTH;
      float content = this.rows.isEmpty() ? 0.0F : this.rows.size() * ROW_HEIGHT + (this.rows.size() - 1) * ROW_GAP;
      this.scroll.update(listArea.height(), content);
      this.viewport = new BoundsData(listArea.getFloatValue(), listArea.getFloatValue2(), width, listArea.height());
      float f = this.entrance.method2808(1.0F);
      graphics.enableScissor(listArea.getFloatValue(), listArea.getFloatValue2(), listArea.getFloatValue() + width, listArea.getFloatValue2() + listArea.height());
      float y = listArea.getFloatValue2() - this.scroll.offset();
      int n = 0;

      for (MultiplayerMenuPage.Row row : this.rows) {
         row.bounds = new BoundsData(listArea.getFloatValue(), y, width, ROW_HEIGHT);
         if (y + ROW_HEIGHT >= listArea.getFloatValue2() && y <= listArea.getFloatValue2() + listArea.height()) {
            row.render(graphics, mouseX, mouseY, listArea.getFloatValue(), y + MenuSkin.enterOffset(f, n), width);
         }

         y += ROW_HEIGHT + ROW_GAP;
         n++;
      }

      graphics.disableScissor();
      if (this.rows.isEmpty()) {
         Font medium = MenuTheme.medium(12.0F);
         String text = MenuTheme.t("menu.multiplayer.empty");
         graphics.drawText(
            medium,
            text,
            listArea.getFloatValue() + (width - medium.width(text)) / 2.0F,
            listArea.getFloatValue2() + (listArea.height() - medium.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRail(graphics, listArea.getFloatValue() + listArea.width() - RAIL_WIDTH, listArea.getFloatValue2(), listArea.height());
      this.nameField.render(graphics, mouseX, mouseY);
      this.addressField.render(graphics, mouseX, mouseY);
      this.addButton.render(graphics, mouseX, mouseY);
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int glfwButton) {
      if (this.nameField.mouseClicked(lastClickX, lastClickY, glfwButton) | this.addressField.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (this.addButton.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (glfwButton == 0 && this.viewport.contains(lastClickX, lastClickY)) {
         for (MultiplayerMenuPage.Row row : this.rows) {
            if (row.playBounds.contains(lastClickX, lastClickY)) {
               this.joinServer(row);
               return true;
            }

            if (row.removeBounds.contains(lastClickX, lastClickY)) {
               this.removeServer(row);
               return true;
            }

            if (row.bounds.contains(lastClickX, lastClickY)) {
               this.joinServer(row);
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
      return this.nameField.keyPressed(n, n2, n3) || this.addressField.keyPressed(n, n2, n3);
   }

   @Override
   public boolean charTyped(char c, int n) {
      return this.nameField.charTyped(c, n) || this.addressField.charTyped(c, n);
   }

   private final class Row {
      private final ServerData info;
      private final FaviconTexture icon;
      private byte[] lastFavicon;
      private BoundsData bounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData removeBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData playBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

      private Row(ServerData info) {
         this.info = info;
         this.icon = FaviconTexture.forServer(MinecraftAccess.mc.getTextureManager(), info.ip);
      }

      private void ping() {
         this.info.setState(State.PINGING);
         MultiplayerMenuPage.PINGER.submit(() -> {
            try {
               MultiplayerMenuPage.this.pinger.pingServer(this.info, () -> MinecraftAccess.mc.execute(this::syncFavicon), () -> {}, EventLoopGroupHolder.remote(false));
            } catch (Exception e) {
               this.info.setState(State.UNREACHABLE);
            }
         });
      }

      private void syncFavicon() {
         byte[] iconBytes = this.info.getIconBytes();
         if (iconBytes != null && !Arrays.equals(iconBytes, this.lastFavicon)) {
            try {
               this.icon.upload(NativeImage.read(iconBytes));
               this.lastFavicon = iconBytes;
            } catch (Exception e) {
            }
         }
      }

      private void render(MenuDrawContext graphics, float mouseX, float mouseY, float floatValue, float snap, float width) {
         this.syncFavicon();
         boolean contains = this.bounds.contains(mouseX, mouseY);
         MenuSkin.plate(
            graphics, floatValue, snap, width, MultiplayerMenuPage.ROW_HEIGHT, contains ? MenuTheme.plate().method7295(MenuTheme.track(), 0.5F) : MenuTheme.plate()
         );
         Identifier textureLocation = this.icon.textureLocation();
         float y = snap + (MultiplayerMenuPage.ROW_HEIGHT - MultiplayerMenuPage.ICON_SIZE) / 2.0F;
         RenderShaders.method5294(
            MatrixUtils.method664(graphics.pose()),
            textureLocation,
            floatValue + MenuSkin.EDGE,
            y,
            MultiplayerMenuPage.ICON_SIZE,
            MultiplayerMenuPage.ICON_SIZE,
            ProxyDotRadiusData.method8069(MenuSkin.tileRadius(MultiplayerMenuPage.ICON_SIZE)),
            ZenithColor.INSTANCE
         );
         float divider = MenuSkin.divider(graphics, floatValue + MenuSkin.EDGE + MultiplayerMenuPage.ICON_SIZE, snap, MultiplayerMenuPage.ROW_HEIGHT);
         Font medium = MenuTheme.medium(11.0F);
         Font regular = MenuTheme.regular(10.0F);
         float px = MenuTheme.px(30.0F);
         float centeredTextY = snap + (MultiplayerMenuPage.ROW_HEIGHT - px) / 2.0F;
         graphics.drawText(medium, this.info.name, divider, centeredTextY, MenuTheme.text());
         graphics.drawText(regular, this.info.ip, divider, centeredTextY + MenuTheme.px(18.0F), MenuTheme.textTertiary());
         float f = floatValue + width - MenuSkin.EDGE;
         float px2 = MenuTheme.px(12.0F);
         float y2 = snap + (MultiplayerMenuPage.ROW_HEIGHT - px2) / 2.0F;
         this.playBounds = new BoundsData(f - px2, y2, px2, px2);
         this.removeBounds = new BoundsData(f - px2 * 2.0F - MenuTheme.px(4.0F), y2, px2, px2);
         MenuSkin.valuePill(graphics, this.removeBounds.getFloatValue() - MenuSkin.GAP * 2.0F, snap, MultiplayerMenuPage.ROW_HEIGHT, this.playerCount());
         Font font = MenuIcons.REMOVE.font(12.0F);
         graphics.drawText(
            font,
            MenuIcons.REMOVE.glyph(),
            this.removeBounds.getFloatValue(),
            y2 + (px2 - font.height()) / 2.0F,
            this.removeBounds.contains(mouseX, mouseY) ? MenuTheme.danger() : MenuTheme.textTertiary()
         );
         Font font2 = MenuIcons.PLAY.font(12.0F);
         graphics.drawText(font2, MenuIcons.PLAY.glyph(), this.playBounds.getFloatValue(), y2 + (px2 - font2.height()) / 2.0F, MenuTheme.accent());
      }

      private String playerCount() {
         if (this.info.status != null) {
            String string = this.info.status.getString();
            if (!string.isBlank()) {
               return string;
            }
         }

         return this.info.state() == State.PINGING ? "..." : "-";
      }
   }
}

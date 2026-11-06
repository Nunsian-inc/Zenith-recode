package org.zenith.client.screens.override.main.page;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.component.SkinPreview;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.api.MicrosoftAuth;
import org.zenith.utility.misc.Account;
import org.zenith.utility.misc.Accounts;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class AccountManagerPage implements MainMenuPage {
   private static final float WIDTH = MenuTheme.px(800.0F);
   private static final float STRIP_HEIGHT = MenuTheme.px(314.0F);
   private static final float RAIL_GAP = MenuTheme.px(24.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float CONTROLS_GAP = MenuTheme.px(48.0F);
   private static final float CARD_WIDTH = MenuTheme.px(188.0F);
   private static final float CARD_GAP = MenuTheme.px(16.0F);
   private static final float PANEL_HEIGHT = MenuTheme.px(264.0F);
   private static final float PANEL_GAP = MenuTheme.px(8.0F);
   private static final float PANEL_RADIUS = MenuTheme.px(12.0F);
   private static final float PANEL_PADDING_Y = MenuTheme.px(16.0F);
   private static final float SKIN_HEIGHT = MenuTheme.px(192.0F);
   private static final float SKIN_WINDOW = MenuTheme.px(92.0F);
   private static final float SELECT_WIDTH = MenuTheme.px(138.0F);
   private static final float ACTION_GAP = MenuTheme.px(8.0F);
   private static final float INPUT_WIDTH = MenuTheme.px(256.0F);
   private static final float BUTTON_GAP = MenuTheme.px(4.0F);
   private static final DateTimeFormatter ADDED_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.systemDefault());
   private static final List<String> NAME_PARTS = List.of(
      "Void",
      "Nova",
      "Echo",
      "Frost",
      "Ember",
      "Lunar",
      "Cyber",
      "Neon",
      "Shadow",
      "Prism",
      "Riot",
      "Drift",
      "Pulse",
      "Zen",
      "Flux",
      "Onyx",
      "Vertex",
      "Quartz",
      "Raven",
      "Solar"
   );
   private final MenuTextField usernameField = new MenuTextField(MenuIcons.USER, MenuTheme.t("menu.accounts.username"));
   private final MenuButton addButton = MenuButton.iconOnly(MenuIcons.ADD, MenuButton.Style.ACCENT);
   private final MenuButton microsoftButton = MenuButton.iconOnly(MenuIcons.LAUNCH, MenuButton.Style.GHOST);
   private final MenuButton randomButton = MenuButton.iconOnly(MenuIcons.RANDOM, MenuButton.Style.GHOST);
   private final MenuButton clearButton = MenuButton.iconOnly(MenuIcons.CLEAR, MenuButton.Style.DANGER);
   private final MenuButton cancelLoginButton = MenuButton.ghost(MenuIcons.CLEAR, MenuTheme.t("menu.accounts.cancel"));
   private final MenuScroll scroll = new MenuScroll();
   private final Map<String, PlayerSkinWidget> skinWidgets = new HashMap<>();
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private BoundsData[] cardSelect = new BoundsData[0];
   private BoundsData[] cardRemove = new BoundsData[0];
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private MicrosoftAuth.Data deviceCode;
   private BoundsData deviceCodeBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private boolean loginRunning;
   private volatile int loginGeneration;

   public AccountManagerPage() {
      this.addButton.onClick(this::addOffline);
      this.microsoftButton.onClick(this::startMicrosoftLogin);
      this.randomButton.onClick(() -> this.usernameField.setValue(randomName()));
      this.clearButton.onClick(this.usernameField::clear);
      this.cancelLoginButton.onClick(this::cancelLogin);
      this.usernameField.setOnEnter(this::addOffline);
      this.usernameField.setMaxLength(16);
   }

   private Accounts manager() {
      return Zenith.getInstance().getAccounts();
   }

   private PlayerSkinWidget skinWidget(int n, Account account) {
      String key = account.getName() + "#" + account.getType();
      return this.skinWidgets
         .computeIfAbsent(
            key,
            var1x -> {
               String var2x = account.getName();
               UUID var3x = account.method5472();
               return new PlayerSkinWidget(
                  Math.round(SKIN_WINDOW),
                  Math.round(SKIN_HEIGHT),
                  Minecraft.getInstance().getEntityModels(),
                  () -> SkinPreview.textures(var2x, var3x)
               );
            }
         );
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return STRIP_HEIGHT + RAIL_GAP + RAIL_WIDTH + CONTROLS_GAP + MenuTextField.HEIGHT;
   }

   private void addOffline() {
      String name = this.usernameField.getValue().trim();
      if (!name.isEmpty()) {
         this.manager().method4753(Account.method5470(name));
         this.manager().method4757(MenuTheme.t("menu.accounts.added") + " " + name);
         this.usernameField.clear();
         this.usernameField.setFocused(false);
      }
   }

   private static String randomName() {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      return NAME_PARTS.get(current.nextInt(NAME_PARTS.size())) + NAME_PARTS.get(current.nextInt(NAME_PARTS.size())).toLowerCase(Locale.ROOT) + current.nextInt(10, 100);
   }

   private void startMicrosoftLogin() {
      if (!this.loginRunning) {
         this.loginRunning = true;
         int n = ++this.loginGeneration;
         Accounts manager = this.manager();
         manager.method4757(MenuTheme.t("menu.accounts.contacting"));
         MicrosoftAuth.method3056().whenComplete((deviceCode, throwable) -> Minecraft.getInstance().execute(() -> {
            if (n == this.loginGeneration) {
               if (throwable == null && deviceCode != null) {
                  this.deviceCode = deviceCode;
                  manager.method4757(MenuTheme.t("menu.accounts.waiting"));
                  Minecraft.getInstance().keyboardHandler.setClipboard(deviceCode.getText());
                  com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create(deviceCode.getText2()));
                  this.awaitLogin(deviceCode, n);
               } else {
                  this.loginRunning = false;
                  manager.method4757(MenuTheme.t("menu.accounts.loginFailed") + " " + Accounts.rootMessage(throwable));
               }
            }
         }));
      }
   }

   private void awaitLogin(MicrosoftAuth.Data deviceCode, int n) {
      Accounts manager = this.manager();
      MicrosoftAuth.method3058(deviceCode, var3x -> Minecraft.getInstance().execute(() -> {
         if (n == this.loginGeneration) {
            manager.method4757(var3x);
         }
      }), () -> n != this.loginGeneration).whenComplete((var3x, throwable) -> Minecraft.getInstance().execute(() -> {
         if (n == this.loginGeneration) {
            this.loginRunning = false;
            this.deviceCode = null;
            if (throwable == null && var3x != null) {
               manager.method4753(Account.method5471(var3x.name(), var3x.uuid(), var3x.getText2(), var3x.getText3()));
               manager.method4757(MenuTheme.t("menu.accounts.added") + " " + var3x.name());
            } else {
               manager.method4757(MenuTheme.t("menu.accounts.loginFailed") + " " + Accounts.rootMessage(throwable));
            }
         }
      }));
   }

   private void cancelLogin() {
      this.loginGeneration++;
      this.deviceCode = null;
      this.loginRunning = false;
      this.manager().method4757("Microsoft login cancelled");
   }

   @Override
   public void layout(BoundsData boundsData) {
      float f = boundsData.getFloatValue2() + STRIP_HEIGHT + RAIL_GAP + RAIL_WIDTH + CONTROLS_GAP;
      float f2 = INPUT_WIDTH + ACTION_GAP + MenuButton.HEIGHT * 4.0F + BUTTON_GAP * 3.0F;
      float floatValue = boundsData.getFloatValue() + (boundsData.width() - f2) / 2.0F;
      this.usernameField.setBounds(floatValue, f, INPUT_WIDTH, MenuTextField.HEIGHT);
      floatValue += INPUT_WIDTH + ACTION_GAP;

      for (MenuButton menuButton : List.of(this.addButton, this.microsoftButton, this.randomButton, this.clearButton)) {
         menuButton.setBounds(floatValue, f, MenuButton.HEIGHT, MenuButton.HEIGHT);
         floatValue += MenuButton.HEIGHT + BUTTON_GAP;
      }

      if (this.deviceCode != null) {
         float px = MenuTheme.px(360.0F);
         float height = MenuTheme.px(160.0F);
         this.deviceCodeBounds = new BoundsData(
            boundsData.getFloatValue() + (boundsData.width() - px) / 2.0F, boundsData.getFloatValue2() + (STRIP_HEIGHT - height) / 2.0F, px, height
         );
         float preferredWidth = this.cancelLoginButton.preferredWidth();
         this.cancelLoginButton
            .setBounds(
               this.deviceCodeBounds.getFloatValue() + (px - preferredWidth) / 2.0F,
               this.deviceCodeBounds.getFloatValue2() + height - MenuTheme.px(16.0F) - MenuButton.HEIGHT,
               preferredWidth,
               MenuButton.HEIGHT
            );
      }
   }

   @Override
   public void render(MenuDrawContext graphics, float mouseX, float mouseY, BoundsData boundsData) {
      var accounts = this.manager().method4751();
      float content = accounts.isEmpty() ? 0.0F : accounts.size() * CARD_WIDTH + (accounts.size() - 1) * CARD_GAP;
      this.scroll.update(boundsData.width(), content);
      this.cardSelect = new BoundsData[accounts.size()];
      this.cardRemove = new BoundsData[accounts.size()];
      BoundsData listArea = MenuSkin.listArea(graphics, boundsData, STRIP_HEIGHT);
      this.viewport = new BoundsData(listArea.getFloatValue(), listArea.getFloatValue2(), listArea.width(), listArea.height());
      float f = this.entrance.animateTo(1.0F);
      graphics.enableScissor(listArea.getFloatValue(), listArea.getFloatValue2(), listArea.getFloatValue() + listArea.width(), listArea.getFloatValue2() + listArea.height());
      float floatValue = listArea.getFloatValue() + Math.max(0.0F, (listArea.width() - content) / 2.0F) - this.scroll.offset();

      for (int i = 0; i < accounts.size(); i++) {
         if (floatValue + CARD_WIDTH >= listArea.getFloatValue() && floatValue <= listArea.getFloatValue() + listArea.width()) {
            this.renderCard(graphics, mouseX, mouseY, (Account)accounts.get(i), i, floatValue, listArea.getFloatValue2() + MenuSkin.enterOffset(f, i));
         }

         floatValue += CARD_WIDTH + CARD_GAP;
      }

      graphics.disableScissor();
      if (accounts.isEmpty()) {
         Font medium = MenuTheme.medium(12.0F);
         String text = MenuTheme.t("menu.accounts.empty");
         graphics.drawText(
            medium,
            text,
            listArea.getFloatValue() + (listArea.width() - medium.width(text)) / 2.0F,
            listArea.getFloatValue2() + (listArea.height() - medium.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRailHorizontal(graphics, listArea.getFloatValue(), listArea.getFloatValue2() + listArea.height() - RAIL_WIDTH, listArea.width());
      this.usernameField.render(graphics, mouseX, mouseY);
      this.addButton.render(graphics, mouseX, mouseY);
      this.microsoftButton.render(graphics, mouseX, mouseY);
      this.randomButton.render(graphics, mouseX, mouseY);
      this.clearButton.render(graphics, mouseX, mouseY);
      this.renderStatus(graphics, boundsData);
      if (this.deviceCode != null) {
         this.renderDeviceCode(graphics, mouseX, mouseY);
      }
   }

   private void renderStatus(MenuDrawContext graphics, BoundsData boundsData) {
      String status = this.manager().getStatus();
      if (status != null && !status.isEmpty()) {
         Font regular = MenuTheme.regular(10.0F);
         graphics.drawText(
            regular,
            status,
            boundsData.getFloatValue() + (boundsData.width() - regular.width(status)) / 2.0F,
            boundsData.getFloatValue2() + this.contentHeight() + MenuTheme.px(12.0F),
            MenuTheme.textTertiary()
         );
      }
   }

   private void renderCard(MenuDrawContext graphics, float mouseX, float mouseY, Account account, int n, float floatValue, float y) {
      boolean flag = this.manager().getIntValue() == n;
      ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      MenuSurface.card(graphics, floatValue, y, CARD_WIDTH, PANEL_HEIGHT, PANEL_RADIUS, MenuTheme.surface(), flag ? MenuTheme.border() : MenuTheme.borderSubtle());
      PlayerSkinWidget skinWidget = this.skinWidget(n, account);
      float f = floatValue + (CARD_WIDTH - SKIN_WINDOW) / 2.0F;
      skinWidget.setPosition(Math.round(f), Math.round(y + PANEL_PADDING_Y));
      skinWidget.setSize(Math.round(SKIN_WINDOW), Math.round(SKIN_HEIGHT));
      graphics.enableScissor(f, y + PANEL_PADDING_Y, f + SKIN_WINDOW, y + PANEL_PADDING_Y + SKIN_HEIGHT);
      skinWidget.extractRenderState(graphics, (int)mouseX, (int)mouseY, 0.0F);
      graphics.disableScissor();
      Font medium = MenuTheme.medium(12.0F);
      Font regular = MenuTheme.regular(11.0F);
      float centeredTextY = y + PANEL_PADDING_Y + SKIN_HEIGHT + PANEL_GAP;
      graphics.drawText(medium, account.getName(), floatValue + (CARD_WIDTH - medium.width(account.getName())) / 2.0F, centeredTextY, MenuTheme.text());
      String text = ADDED_FORMAT.format(Instant.ofEpochMilli(account.getLongValue()));
      graphics.drawText(regular, text, floatValue + (CARD_WIDTH - regular.width(text)) / 2.0F, centeredTextY + MenuTheme.px(18.0F), MenuTheme.textSecondary());
      float y2 = y + PANEL_HEIGHT + PANEL_GAP;
      float width = MenuButton.HEIGHT;
      float width2 = Math.min(SELECT_WIDTH, CARD_WIDTH - width - ACTION_GAP);
      BoundsData boundsData = new BoundsData(floatValue, y2, width2, MenuButton.HEIGHT);
      BoundsData boundsData2 = new BoundsData(floatValue + width2 + ACTION_GAP, y2, width, MenuButton.HEIGHT);
      this.cardSelect[n] = boundsData;
      this.cardRemove[n] = boundsData2;
      this.drawAction(graphics, boundsData, flag ? MenuIcons.ACCEPT : MenuIcons.USER, flag ? "Selected" : "Select", flag, mouseX, mouseY);
      this.drawIconAction(graphics, boundsData2, MenuIcons.REMOVE, flag, mouseX, mouseY);
   }

   private void drawAction(MenuDrawContext graphics, BoundsData boundsData, MenuIcon menuIcon, String text, boolean flag, float mouseX, float mouseY) {
      boolean contains = boundsData.contains(mouseX, mouseY);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      if (flag) {
         graphics.drawRoundedRect(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, MenuTheme.accent());
      } else {
         graphics.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            headerRadius,
            contains ? MenuTheme.surface().method7295(MenuTheme.track(), 0.6F) : MenuTheme.surface()
         );
         graphics.drawRoundedBorder(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), -0.1F, headerRadius, MenuTheme.borderSubtle());
      }

      Font medium = MenuTheme.medium(12.0F);
      Font font = menuIcon.font(12.0F);
      float f = menuIcon.width(12.0F) + MenuTheme.px(8.0F) + medium.width(text);
      float floatValue = boundsData.getFloatValue() + (boundsData.width() - f) / 2.0F;
      ZenithColor textTertiary = flag ? MenuTheme.onAccent() : MenuTheme.textTertiary();
      ZenithColor textSecondary = flag ? MenuTheme.onAccent() : MenuTheme.textSecondary();
      graphics.drawText(font, menuIcon.glyph(), floatValue, boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F, textTertiary);
      graphics.drawText(medium, text, floatValue + menuIcon.width(12.0F) + MenuTheme.px(8.0F), boundsData.getFloatValue2() + (boundsData.height() - medium.height()) / 2.0F, textSecondary);
   }

   private void drawIconAction(MenuDrawContext graphics, BoundsData boundsData, MenuIcon menuIcon, boolean flag, float mouseX, float mouseY) {
      boolean contains = boundsData.contains(mouseX, mouseY);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      if (flag) {
         graphics.drawRoundedRect(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, MenuTheme.danger());
      } else {
         graphics.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            headerRadius,
            contains ? MenuTheme.surface().method7295(MenuTheme.track(), 0.6F) : MenuTheme.surface()
         );
         graphics.drawRoundedBorder(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), -0.1F, headerRadius, MenuTheme.borderSubtle());
      }

      Font font = menuIcon.font(12.0F);
      ZenithColor danger = flag ? MenuTheme.onAccent() : (contains ? MenuTheme.danger() : MenuTheme.textTertiary());
      graphics.drawText(
         font,
         menuIcon.glyph(),
         boundsData.getFloatValue() + (boundsData.width() - menuIcon.width(12.0F)) / 2.0F,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         danger
      );
   }

   private void renderDeviceCode(MenuDrawContext graphics, float mouseX, float mouseY) {
      BoundsData deviceCodeBounds = this.deviceCodeBounds;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(PANEL_RADIUS);
      graphics.drawRoundedRect(
         deviceCodeBounds.getFloatValue(), deviceCodeBounds.getFloatValue2(), deviceCodeBounds.width(), deviceCodeBounds.height(), headerRadius, MenuTheme.surface().method7295(ZenithColor.INSTANCE2, 0.85F)
      );
      graphics.drawRoundedBorder(deviceCodeBounds.getFloatValue(), deviceCodeBounds.getFloatValue2(), deviceCodeBounds.width(), deviceCodeBounds.height(), -0.1F, headerRadius, MenuTheme.borderSubtle());
      Font semibold = MenuTheme.semibold(14.0F);
      Font font = MenuTheme.semibold(20.0F);
      Font regular = MenuTheme.regular(10.0F);
      String text = "Sign in to Microsoft";
      graphics.drawText(semibold, text, deviceCodeBounds.getFloatValue() + (deviceCodeBounds.width() - semibold.width(text)) / 2.0F, deviceCodeBounds.getFloatValue2() + MenuTheme.px(16.0F), MenuTheme.text());
      String str = this.deviceCode.getText();
      graphics.drawText(
         font, str, deviceCodeBounds.getFloatValue() + (deviceCodeBounds.width() - font.width(str)) / 2.0F, deviceCodeBounds.getFloatValue2() + MenuTheme.px(46.0F), MenuTheme.accent()
      );
      String text2 = "Code copied - enter it at " + this.deviceCode.getText2();
      graphics.drawText(
         regular, text2, deviceCodeBounds.getFloatValue() + (deviceCodeBounds.width() - regular.width(text2)) / 2.0F, deviceCodeBounds.getFloatValue2() + MenuTheme.px(82.0F), MenuTheme.textSecondary()
      );
      this.cancelLoginButton.render(graphics, mouseX, mouseY);
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int glfwButton) {
      if (this.deviceCode != null) {
         if (this.cancelLoginButton.mouseClicked(lastClickX, lastClickY, glfwButton)) {
            return true;
         }

         if (this.deviceCodeBounds.contains(lastClickX, lastClickY)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.deviceCode.getText());
            return true;
         }
      }

      if (this.usernameField.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (!this.addButton.mouseClicked(lastClickX, lastClickY, glfwButton)
         && !this.microsoftButton.mouseClicked(lastClickX, lastClickY, glfwButton)
         && !this.randomButton.mouseClicked(lastClickX, lastClickY, glfwButton)
         && !this.clearButton.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         if (glfwButton == 0 && this.viewport.contains(lastClickX, lastClickY)) {
            for (int i = 0; i < this.cardSelect.length; i++) {
               if (this.cardRemove[i] != null && this.cardRemove[i].contains(lastClickX, lastClickY)) {
                  this.manager().method4754(i);
                  return true;
               }

               if (this.cardSelect[i] != null && this.cardSelect[i].contains(lastClickX, lastClickY)) {
                  this.manager().select(i);
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double d, double d2, double d3) {
      return this.scroll.scroll(d3, CARD_WIDTH + CARD_GAP);
   }

   @Override
   public boolean keyPressed(int n, int n2, int n3) {
      return this.usernameField.keyPressed(n, n2, n3);
   }

   @Override
   public boolean charTyped(char c, int n) {
      return this.usernameField.charTyped(c, n);
   }
}

package org.zenith.client.screens.override.death;

import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.phys.Vec3;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.SvgIcon;
import org.zenith.base.filemanager.impl.way.Way;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MainMenuBlurRenderer;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.utility.misc.KeyCompat;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ZenithDeathScreen extends DeathScreen implements MinecraftAccess {
   private static final float CARD_WIDTH = MenuTheme.px(480.0F);
   private static final float PADDING = MenuTheme.px(30.0F);
   private static final float GAP = MenuTheme.px(10.0F);
   private final LocalPlayer deathPlayer;
   private final Vec3 deathPosition;
   private final String coordinates;
   private final String serverName;
   private final String serverAddress;
   private final String dimension;
   private final String killerName;
   private final String weaponName;
   private final MainMenuBlurRenderer backdrop = new MainMenuBlurRenderer();
   private final Animation openTransition = new Animation(380L, 0.0F, Easing.INSTANCE7);
   private final MenuButton respawnView = MenuButton.accent(MenuIcons.REFRESH, "");
   private final MenuButton waypointView = MenuButton.ghost(MenuIcons.ADDRESS, "");
   private final MenuButton menuView = MenuButton.ghost(MenuIcons.HOME, "");
   private final MenuButton copyView = MenuButton.ghost(MenuIcon.svg(SvgIcon.COPY), "");
   private List<Button> buttons = List.of();
   private boolean coordinatesCopied;
   private boolean waypointMarked;

   public ZenithDeathScreen(Component causeOfDeath, boolean hardcore, LocalPlayer player) {
      super(causeOfDeath, hardcore, player);
      this.deathPlayer = player;
      this.deathPosition = player.position();
      BlockPos blockPosition = BlockPos.containing(this.deathPosition);
      this.coordinates = blockPosition.getX() + " / " + blockPosition.getY() + " / " + blockPosition.getZ();
      this.serverName = mc.getCurrentServer() == null
         ? Component.translatable("menu.singleplayer").getString()
         : mc.getCurrentServer().name;
      this.serverAddress = mc.getConnection() != null && mc.getConnection().getServerData() != null
         ? mc.getConnection().getServerData().ip : "VANILLA";
      this.dimension = player.level().dimension().identifier().toString();
      // Аргументы ванильного сообщения надёжнее разбора переведённого текста по словам.
      Object[] arguments = causeOfDeath != null && causeOfDeath.getContents() instanceof TranslatableContents translation
         && translation.getKey().startsWith("death.attack.") && !translation.getKey().equals("death.attack.badRespawnPoint.message")
         ? translation.getArgs() : new Object[0];
      this.killerName = arguments.length > 1 && arguments[1] instanceof Component killer ? killer.getString() : "";
      this.weaponName = arguments.length > 2 && arguments[2] instanceof Component weapon ? weapon.getString() : "";
   }

   public boolean isFor(LocalPlayer player) {
      return this.deathPlayer == player;
   }

   public Vec3 getDeathPosition() {
      return this.deathPosition;
   }

   @Override
   protected void init() {
      // Оставляем настоящие кнопки: задержку 20 тиков, spectate, подтверждение выхода и черновик жалобы.
      super.init();
      List<Button> vanillaButtons = this.children().stream().filter(Button.class::isInstance).map(Button.class::cast).toList();
      Button copyButton = this.addRenderableWidget(Button.builder(Component.literal(MenuTheme.t(
         this.coordinatesCopied ? "screen.death.coordinatesCopied" : "screen.death.copyCoordinates")), button -> {
         mc.keyboardHandler.setClipboard(this.coordinates.replace(" / ", " "));
         this.coordinatesCopied = true;
         button.setMessage(Component.literal(MenuTheme.t("screen.death.coordinatesCopied")));
      }).build());
      Button waypointButton = this.addRenderableWidget(Button.builder(Component.literal(MenuTheme.t(
         this.waypointMarked ? "screen.death.locationMarked" : "screen.death.markLocation")), button -> {
         // ponytail: WayManager фильтрует лишь по серверу; измерение в имени, пока у Way нет dimension-поля.
         String name = MenuTheme.t("screen.death.waypointName") + " [" + this.dimension + "]";
         Zenith.getInstance().getWayManager().addWay(new Way(name, BlockPos.containing(this.deathPosition), this.serverAddress));
         this.waypointMarked = true;
         button.setMessage(Component.literal(MenuTheme.t("screen.death.locationMarked")));
         button.active = false;
      }).build());
      waypointButton.active = !this.waypointMarked;
      this.buttons = List.of(vanillaButtons.get(0), waypointButton, vanillaButtons.get(1), copyButton);
      for (int index = 0; index < this.buttons.size(); index++) {
         this.buttons.get(index).setTabOrderGroup(index);
      }
      this.openTransition.snapTo(0.0F);
   }

   @Override
   public Component getNarrationMessage() {
      Component message = this.getTitle().copy();
      if (this.causeOfDeath != null) {
         message = message.copy().append(". ").append(this.causeOfDeath);
      }
      return message;
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext graphics = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      this.backdrop.capture(graphics, MenuTheme.px(28.0F));
      this.backdrop.render(MatrixUtils.method664(graphics.pose()), 0.0F, 0.0F, this.width, this.height,
         ProxyDotRadiusData.INSTANCE, ZenithColor.INSTANCE);
      graphics.drawRect(0.0F, 0.0F, this.width, this.height, ZenithColor.INSTANCE2.method7294(0.35F));

      Font body = MenuTheme.regular(14.0F);
      List<String> causeLines = MenuTheme.wrap(body, this.causeOfDeath == null ? "" : this.causeOfDeath.getString(), CARD_WIDTH - PADDING * 2.0F);
      int lineCount = Math.min(3, causeLines.size());
      float causeHeight = lineCount * MenuTheme.px(19.0F);
      float killerHeight = this.killerName.isEmpty() ? 0.0F : MenuTheme.px(this.weaponName.isEmpty() ? 54.0F : 80.0F);
      float cardHeight = MenuTheme.px(468.0F) + causeHeight + killerHeight;
      float fit = Math.max(0.01F, Math.min(1.0F, Math.min((this.width - MenuTheme.px(32.0F)) / CARD_WIDTH,
         (this.height - MenuTheme.px(32.0F)) / cardHeight)));
      this.openTransition.animateTo(1.0F);
      float left = (this.width / fit - CARD_WIDTH) / 2.0F;
      float top = (this.height / fit - cardHeight) / 2.0F + (1.0F - this.openTransition.getValue()) * MenuTheme.px(30.0F);
      graphics.pose().pushMatrix();
      graphics.pose().scale(fit, fit);
      try {
         float contentLeft = left + PADDING;
         float contentWidth = CARD_WIDTH - PADDING * 2.0F;
         ProxyDotRadiusData corners = ProxyDotRadiusData.method8069(MenuTheme.px(14.0F));
         this.backdrop.render(MatrixUtils.method664(graphics.pose()), left, top, CARD_WIDTH, cardHeight, corners, ZenithColor.INSTANCE);
         graphics.drawRoundedRect(left, top, CARD_WIDTH, cardHeight, corners, MenuTheme.panel());
         graphics.drawRoundedBorder(left, top, CARD_WIDTH, cardHeight, MenuTheme.px(1.0F), corners, MenuTheme.borderSubtle());
         float iconSize = MenuTheme.px(44.0F);
         MenuSkin.mutedTile(graphics, left + (CARD_WIDTH - iconSize) / 2.0F, top + MenuTheme.px(24.0F), iconSize,
            MenuIcon.svg(SvgIcon.HEART), MenuTheme.danger());
         this.drawCentered(graphics, MenuTheme.semibold(28.0F), this.getTitle().getString(), contentLeft,
            top + MenuTheme.px(82.0F), contentWidth, MenuTheme.text());
         float cursorY = top + MenuTheme.px(124.0F);
         for (int line = 0; line < lineCount; line++) {
            this.drawCentered(graphics, body, causeLines.get(line), contentLeft, cursorY, contentWidth, MenuTheme.textSecondary());
            cursorY += MenuTheme.px(19.0F);
         }
         cursorY += MenuTheme.px(18.0F);
         if (!this.killerName.isEmpty()) {
            this.drawCentered(graphics, MenuTheme.medium(11.0F), MenuTheme.t("screen.death.killer"), contentLeft, cursorY,
               contentWidth, MenuTheme.textTertiary());
            ZenithColor killerColor = Zenith.getInstance().getFriends().method7627(this.killerName)
               ? MenuTheme.style().getFriendColor().getZenithColor() : MenuTheme.accent();
            this.drawCentered(graphics, MenuTheme.semibold(20.0F), this.killerName, contentLeft, cursorY + MenuTheme.px(18.0F),
               contentWidth, killerColor);
            if (!this.weaponName.isEmpty()) {
               this.drawCentered(graphics, body, this.weaponName, contentLeft, cursorY + MenuTheme.px(47.0F),
                  contentWidth, MenuTheme.textSecondary());
            }
            cursorY += killerHeight;
         }
         this.drawInformation(graphics, MenuTheme.t("screen.death.coordinates"), this.coordinates, contentLeft, cursorY, contentWidth);
         this.drawInformation(graphics, MenuTheme.t("screen.death.server"), this.serverName, contentLeft, cursorY + MenuTheme.px(32.0F), contentWidth);
         cursorY += MenuTheme.px(82.0F);
         MenuButton[] views = {this.respawnView, this.waypointView, this.menuView, this.copyView};
         for (int index = 0; index < views.length; index++) {
            Button button = this.buttons.get(index);
            MenuButton view = views[index];
            String label = button.getMessage().getString();
            view.setLabel(index == 0 ? label + "  [Enter]" : label);
            view.setEnabled(button.active);
            view.setBounds(contentLeft, cursorY, contentWidth, MenuButton.HEIGHT);
            // Нативные hitbox/focus живут в экранных координатах, включая масштаб и анимацию.
            button.setRectangle(Math.max(1, Math.round(contentWidth * fit)), Math.max(1, Math.round(MenuButton.HEIGHT * fit)),
               Math.round(contentLeft * fit), Math.round(cursorY * fit));
            view.render(graphics, mouseX / fit, mouseY / fit);
            if (button.isFocused()) {
               graphics.drawRoundedBorder(contentLeft, cursorY, contentWidth, MenuButton.HEIGHT, MenuTheme.px(1.0F),
                  ProxyDotRadiusData.method8069(MenuButton.RADIUS), MenuTheme.accent());
            }
            cursorY += MenuButton.HEIGHT + GAP;
         }
         this.drawCentered(graphics, MenuTheme.medium(11.0F), "Zenith · " + this.dimension, contentLeft, top + cardHeight - MenuTheme.px(26.0F),
            contentWidth, MenuTheme.textTertiary());
      } finally {
         graphics.pose().popMatrix();
      }
   }

   private void drawInformation(MenuDrawContext graphics, String label, String value, float x, float y, float width) {
      Font font = MenuTheme.regular(13.0F);
      graphics.drawText(font, label, x, y, MenuTheme.textTertiary());
      float valueWidth = width * 0.65F;
      this.drawCentered(graphics, font, value, x + width - valueWidth, y, valueWidth, MenuTheme.text());
   }

   private void drawCentered(MenuDrawContext graphics, Font font, String text, float x, float y, float width, ZenithColor color) {
      float textWidth = font.width(text);
      float scale = Math.min(1.0F, width / Math.max(1.0F, textWidth));
      graphics.pose().pushMatrix();
      graphics.pose().translate(x + (width - textWidth * scale) / 2.0F, y);
      graphics.pose().scale(scale, scale);
      graphics.drawText(font, text, 0.0F, 0.0F, color);
      graphics.pose().popMatrix();
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      // Не вызываем DeathScreen.mouseClicked: его невидимый текст находится в другом месте и может открывать ссылки.
      for (Button button : this.buttons) {
         if (button.mouseClicked(click, doubled)) {
            this.setFocused(button);
            this.setDragging(true);
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      int key = KeyCompat.toGlfw(input.key());
      if (key == 257 || key == 335) { // Enter и Enter цифровой клавиатуры в KeyCompat.
         if (!this.buttons.isEmpty() && this.buttons.getFirst().active) {
            this.buttons.getFirst().onPress(input);
         }
         return true;
      }
      return super.keyPressed(input);
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void removed() {
      this.backdrop.close();
      super.removed();
   }
}

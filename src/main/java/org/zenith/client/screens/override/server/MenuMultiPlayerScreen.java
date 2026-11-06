package org.zenith.client.screens.override.server;

import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.chat.Component;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.button.ServerButton;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.ThemeStyle;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuMultiPlayerScreen extends Screen implements MinecraftAccess {
   private final Screen parent;
   private final ServerStatusPinger serverListPinger = new ServerStatusPinger();
   private final List<ServerButton> serverButtons = new CopyOnWriteArrayList<>();
   private ServerList serverList;
   private float scrollY = 0.0F;

   public MenuMultiPlayerScreen(Screen parent) {
      super(Component.literal("Multiplayer"));
      this.parent = parent;
   }

   @Override
   public void init() {
      Zenith.getInstance().getMenuParticleRenderer().init();
      this.serverList = new ServerList(mc);
      this.serverList.load();
      this.clearButtons();
      float width = 260.0F;
      float height = 30.0F;

      for (int i = 0; i < this.serverList.size(); i++) {
         this.serverButtons.add(new ServerButton(this, this.serverList.get(i), width, height));
      }
   }

   private void clearButtons() {
      for (ServerButton serverButton : this.serverButtons) {
         serverButton.close();
      }

      this.serverButtons.clear();
   }

   @Override
   public void tick() {
      this.serverListPinger.tick();
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext graphics = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      this.renderBackgroundAndParticles(graphics);
      this.renderServerList(graphics, mouseX, mouseY);
   }

   private void renderBackgroundAndParticles(MenuDrawContext graphics) {
      Window window = mc.getWindow();
      float guiScaledWidth = window.getGuiScaledWidth();
      float guiScaledHeight = window.getGuiScaledHeight();
      graphics.drawTexture(Zenith.method1501("menu/background.png"), 0.0F, 0.0F, guiScaledWidth, guiScaledHeight, ZenithColor.INSTANCE);
      Zenith.getInstance().getMenuParticleRenderer().renderParticles(graphics);
   }

   public void renderServerList(MenuDrawContext graphics, float mouseX, float mouseY) {
      Window window = mc.getWindow();
      float guiScaledWidth = window.getGuiScaledWidth();
      float guiScaledHeight = window.getGuiScaledHeight();
      ThemeStyle themeStyle = ThemeStyle.INSTANCE;
      float f = 265.0F;
      float height = 209.0F;
      float x1 = (guiScaledWidth - f) / 2.0F;
      float y1 = (guiScaledHeight - height) / 2.0F;
      float f2 = 4.0F;
      float f3 = this.serverButtons.size() * (30.0F + f2);
      float f4 = Math.max(0.0F, f3 - height);
      if (this.scrollY > 0.0F) {
         this.scrollY = 0.0F;
      }

      if (this.scrollY < -f4) {
         this.scrollY = -f4;
      }

      float y = y1 + this.scrollY;
      graphics.enableScissor((int)x1, (int)y1, (int)(x1 + f), (int)(y1 + height));

      for (ServerButton serverButton : this.serverButtons) {
         if (y + serverButton.getHeight() > y1 && y < y1 + height) {
            serverButton.render(graphics, mouseX, mouseY, x1 + (f - serverButton.getWidth()) / 2.0F, y);
         }

         y += serverButton.getHeight() + f2;
      }

      graphics.disableScissor();
      if (f3 > height) {
         float width = 1.0F;
         float floatValue = x1 + f + 4.0F - width - 2.0F;
         float height2 = height * (height / f3);
         float y2 = y1 + -this.scrollY / f3 * height;
         graphics.drawRoundedRect(floatValue, y1, width, height, ProxyDotRadiusData.INSTANCE, themeStyle.getZenithColor12());
         graphics.drawRoundedRect(floatValue, y2, width, height2, ProxyDotRadiusData.INSTANCE, themeStyle.getZenithColor13());
      }

      Font font = Fonts.ICONS.getFont(20.0F);
      float height2 = font.height();
      String text = "J";
      float floatValue = (guiScaledWidth - font.width(text)) / 2.0F;
      float f5 = y1 - 32.0F - height2;
      graphics.drawText(font, text, floatValue, f5, Zenith.getInstance().getStyleManager().getColorCycleIcon().method806());
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseClicked(double x, double y, int mouseToGlfw) {
      for (ServerButton serverButton : this.serverButtons) {
         serverButton.onMouseClicked(x, y, MouseButton.fromGlfwButton(mouseToGlfw));
      }

      return super.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))), false);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scrollY += (float)(verticalAmount * 15.0);
      float f = Math.max(0, this.serverButtons.size() * 30);
      if (this.scrollY > 0.0F) {
         this.scrollY = 0.0F;
      }

      if (this.scrollY < -f) {
         this.scrollY = -f;
      }

      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override
   public void onClose() {
      mc.gui.setScreen(this.parent);
   }

   @Override
   public void removed() {
      this.clearButtons();
      this.serverListPinger.removeAll();
   }

   @Generated
   public ServerStatusPinger getServerListPinger() {
      return this.serverListPinger;
   }
}

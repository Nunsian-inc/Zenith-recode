package org.zenith.client.screens.override.button;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.State;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.server.MenuMultiPlayerScreen;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.misc.ThemeStyle;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ServerButton extends ButtonScreen implements MinecraftAccess {
   private final ServerData serverInfo;
   private final FaviconTexture icon;
   private final Animation hoverAnimation = new Animation(250L, Easing.INSTANCE10);
   private final MenuMultiPlayerScreen multiplayerScreen;
   private static final ThreadPoolExecutor PINGER_EXECUTOR = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
   private byte[] lastFavicon;
   private String pingText = "...";

   public ServerButton(MenuMultiPlayerScreen multiplayerScreen, ServerData serverInfo, float width, float height) {
      super(width, height);
      this.multiplayerScreen = multiplayerScreen;
      this.serverInfo = serverInfo;
      this.icon = FaviconTexture.forServer(mc.getTextureManager(), serverInfo.ip);
      this.pingServer();
   }

   public void pingServer() {
      this.serverInfo.setState(State.PINGING);
      PINGER_EXECUTOR.submit(() -> {
         try {
            this.multiplayerScreen.getServerListPinger().pingServer(this.serverInfo, () -> {
               this.pingText = this.serverInfo.ping + "ms";
               this.updateFavicon();
            }, () -> {}, EventLoopGroupHolder.remote(false));
         } catch (Exception e) {
            this.serverInfo.setState(State.UNREACHABLE);
            this.pingText = "Error";
         }
      });
   }

   private void updateFavicon() {
      byte[] iconBytes = this.serverInfo.getIconBytes();
      if (!Arrays.equals(iconBytes, this.lastFavicon) && iconBytes != null) {
         try {
            this.icon.upload(NativeImage.read(iconBytes));
            this.lastFavicon = iconBytes;
         } catch (Exception e) {
         }
      }
   }

   @Override
   public void render(MenuDrawContext graphics, float mouseX, float mouseY, float floatValue, float y) {
      super.render(graphics, mouseX, mouseY, floatValue, y);
      ThemeStyle themeStyle = ThemeStyle.INSTANCE;
      this.hoverAnimation.updateVisibility(this.bounds.contains(mouseX, mouseY));
      graphics.drawRoundedRect(
         floatValue,
         y,
         this.getWidth(),
         this.getHeight(),
         ProxyDotRadiusData.method8069(6.0F),
         themeStyle.getZenithColor12().method7295(themeStyle.getZenithColor8(), this.hoverAnimation.getValue())
      );
      graphics.drawRoundedBorder(floatValue, y, this.getWidth(), this.getHeight(), -0.1F, ProxyDotRadiusData.method8069(4.0F), themeStyle.getZenithColor13());
      this.updateFavicon();
      float width = 14.0F;
      float f = (this.getHeight() - width) / 2.0F;
      Identifier textureLocation = this.icon.textureLocation();
      RenderShaders.method5294(
         MatrixUtils.method664(graphics.pose()), textureLocation, floatValue + f, y + f, width, width, ProxyDotRadiusData.method8069(2.0F), ZenithColor.INSTANCE
      );
      Font font = Fonts.MEDIUM.getFont(8.0F);
      Font font2 = Fonts.MEDIUM.getFont(7.0F);
      float x1 = floatValue + f + width + 6.0F;
      graphics.drawText(font, this.serverInfo.name, x1, y + f - 1.0F, ZenithColor.INSTANCE);
      Component text = TextUtils.method2105(this.serverInfo.motd != null ? this.serverInfo.motd : Component.nullToEmpty(this.serverInfo.ip));
      graphics.enableScissor((int)x1, 0, (int)(x1 + 170.0F), mc.getWindow().getGuiScaledHeight());
      graphics.drawText(font2, text, x1, y + f + width - font2.height(), themeStyle.getZenithColor7().method7287());
      graphics.disableScissor();
      String string = this.serverInfo.status != null ? this.serverInfo.status.getString() : "0/0";
      float width2 = font2.width(this.pingText);
      graphics.drawText(font2, this.pingText, floatValue + this.getWidth() - width2 - 5.0F, y + f - 1.0F, themeStyle.getZenithColor6());
      graphics.drawText(font2, string, floatValue + this.getWidth() - font2.width(string) - 5.0F, y + f + 10.0F, themeStyle.getZenithColor6());
      RenderShaders.method5277(
         MatrixUtils.method664(graphics.pose()),
         floatValue,
         y,
         this.getWidth(),
         this.getHeight(),
         0.1F,
         15.0F,
         ZenithColor.INSTANCE8
            .method7295(Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor(), this.hoverAnimation.getValue()),
         ProxyDotRadiusData.method8069(4.0F)
      );
   }

   @Override
   public void onClick(double d, double d2, MouseButton enum2) {
      if (enum2 == MouseButton.LEFT) {
         ServerAddress string = ServerAddress.parseString(this.serverInfo.ip);
         ConnectScreen.startConnecting(mc.gui.screen(), mc, string, this.serverInfo, false, null);
      }
   }

   public void close() {
      this.icon.close();
   }
}

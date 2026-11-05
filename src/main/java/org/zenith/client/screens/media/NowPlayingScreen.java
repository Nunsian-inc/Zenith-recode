package org.zenith.client.screens.media;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.hud.MusicHud;
import org.zenith.utility.api.LyricsFetcher;
import org.zenith.utility.config.MediaHelperProcess;
import org.zenith.utility.misc.MediaPlayerState;
import org.zenith.utility.misc.MediaSessionManager;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class NowPlayingScreen extends Screen {
   private static final ZenithColor WHITE = ZenithColor.INSTANCE;
   private static final ZenithColor BLACK = new ZenithColor(0, 0, 0);
   private final long openedAt = System.currentTimeMillis();
   private long closingAt = -1L;
   private long lastFrame;
   private int ticks;
   private double phase;
   private float energy;
   private float coverScale = 1.0F;
   private float lyricScroll = Float.NaN;
   private float lyricVelocity;
   private float manualOffset;
   private long manualUntil;
   private String layoutKey = "";
   private float layoutWidth;
   private float layoutSize;
   private final List<NowPlayingScreen.Block> blocks = new ArrayList<>();
   private float blocksHeight;
   private float[] emphasis = new float[0];
   private boolean seekDrag;
   private float seekPreview;
   private boolean volumeDrag;
   private float barHover;
   private float volumeHover;
   private BoundsData seekBar;
   private BoundsData volumeBar;
   private BoundsData closeButton;
   private BoundsData shuffleButton;
   private BoundsData previousButton;
   private BoundsData playButton;
   private BoundsData nextButton;
   private BoundsData repeatButton;
   private BoundsData leftPanel;
   private final List<NowPlayingScreen.Hit> lyricHits = new ArrayList<>();

   public NowPlayingScreen() {
      super(Component.literal("Now Playing"));
   }

   public static boolean coversWorld() {
      Minecraft minecraft = Minecraft.getInstance();
      return !(minecraft.gui.screen() instanceof NowPlayingScreen nowPlayingScreen) ? false : nowPlayingScreen.closingAt < 0L && System.currentTimeMillis() - nowPlayingScreen.openedAt > 400L;
   }

   @Override
   public void tick() {
      if (this.ticks++ % 5 == 0) {
         MediaSessionManager.getInstance().method6000();
      }
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void onClose() {
      if (this.closingAt < 0L) {
         this.closingAt = System.currentTimeMillis();
      }
   }

   private float visibility(long now) {
      float f = Math.min(1.0F, (float)(now - this.openedAt) / 360.0F);
      float f2 = 1.0F - (1.0F - f) * (1.0F - f) * (1.0F - f);
      if (this.closingAt >= 0L) {
         float f3 = Math.min(1.0F, (float)(now - this.closingAt) / 240.0F);
         f2 *= 1.0F - f3 * f3;
      }

      return f2;
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      long now = System.currentTimeMillis();
      float f = this.lastFrame == 0L ? 0.016F : Math.min(0.1F, (float)(now - this.lastFrame) / 1000.0F);
      this.lastFrame = now;
      float visibility = this.visibility(now);
      if (this.closingAt >= 0L && now - this.closingAt >= 240L) {
         if (this.minecraft != null) {
            this.minecraft.gui.setScreen(null);
         }
      } else {
         MenuDrawContext menuDrawContext = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
         MediaSessionManager mediaSessionManager = MediaSessionManager.getInstance();
         float width = this.width;
         float height = this.height;
         this.drawBackdrop(menuDrawContext, mediaSessionManager, width, height, now, f, visibility);
         float x = 0.97F + 0.03F * visibility;
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(width / 2.0F, height / 2.0F);
         menuDrawContext.pose().scale(x, x);
         menuDrawContext.pose().translate(-width / 2.0F, -height / 2.0F);
         float lastClickX = (mouseX - width / 2.0F) / x + width / 2.0F;
         float lastClickY = (mouseY - height / 2.0F) / x + height / 2.0F;
         this.drawClose(menuDrawContext, visibility, lastClickX, lastClickY);
         if (!mediaSessionManager.active()) {
            Font font = Fonts.BOLD.getFont(14.0F);
            String text = t("gui.music.nothing");
            menuDrawContext.drawText(font, text, (width - font.width(text)) / 2.0F, height / 2.0F - font.height() / 2.0F, WHITE.method7292(0.7F * visibility));
            this.clearHits();
         } else {
            boolean flag = width > height * 1.15F;
            if (flag) {
               float maxWidth = Math.min(height * 0.56F, width * 0.29F);
               float f2 = maxWidth + 124.0F;
               float f3 = Math.max(26.0F, (height - f2) / 2.0F - 6.0F);
               this.drawNowPlaying(menuDrawContext, mediaSessionManager, width * 0.28F, f3, maxWidth, now, f, visibility, lastClickX, lastClickY);
               float floatValue = width * 0.49F;
               float layoutWidth = width * 0.4F;
               this.drawLyrics(menuDrawContext, mediaSessionManager, floatValue, 0.0F, layoutWidth, height, now, f, visibility, lastClickX, lastClickY);
            } else {
               float maxWidth = Math.min(height * 0.3F, width * 0.6F);
               float f2 = 30.0F;
               this.drawNowPlaying(menuDrawContext, mediaSessionManager, width / 2.0F, f2, maxWidth, now, f, visibility, lastClickX, lastClickY);
               float y1 = f2 + maxWidth + 130.0F;
               this.drawLyrics(menuDrawContext, mediaSessionManager, width * 0.1F, y1, width * 0.8F, height - y1, now, f, visibility, lastClickX, lastClickY);
            }
         }

         menuDrawContext.pose().popMatrix();
      }
   }

   private void drawBackdrop(MenuDrawContext menuDrawContext, MediaSessionManager mediaSessionManager, float width, float height, long now, float f, float visibility) {
      float f2 = MediaPlayerState.getInstance().method1672();
      float f3 = 0.0F;
      if (mediaSessionManager.method6007() && f2 >= 0.0F) {
         double d = 20.0 * Math.log10(Math.max(f2, 1.0E-4F));
         f3 = (float)Math.max(0.0, Math.min(1.0, (d + 36.0) / 34.0));
      }

      this.energy = this.energy + (f3 - this.energy) * Math.min(1.0F, f * 1.5F);
      this.phase = this.phase + f * (mediaSessionManager.method6007() ? 1.0 : 0.35);
      ZenithColor color = mediaSessionManager.color(0);
      ZenithColor zenithColor = mediaSessionManager.color(1);
      ZenithColor color2 = mediaSessionManager.color(2);
      ZenithColor zenithColor2 = color.method7295(color2, 0.5F).method7296(0.4F);
      menuDrawContext.drawRect(0.0F, 0.0F, width, height, new ZenithColor(8, 8, 10).method7292(visibility));
      RenderShaders.method5262(
         MatrixUtils.method664(menuDrawContext.pose()),
         width,
         height,
         new ZenithColor[]{color, zenithColor, color2, zenithColor2},
         (float)(this.phase % 100000.0),
         this.energy,
         visibility
      );
      float f4 = Math.min(1.0F, (float)(now - mediaSessionManager.getLongValue4()) / 1200.0F);
      Identifier identifier = mediaSessionManager.method6017();
      Identifier id = mediaSessionManager.method6018();
      if (identifier != null) {
         if (id != null && f4 < 1.0F) {
            this.drawSwirl(menuDrawContext, id, width, height, visibility * 0.5F * (1.0F - f4));
         }

         this.drawSwirl(menuDrawContext, identifier, width, height, visibility * 0.5F * (id == null ? 1.0F : f4));
      }

      menuDrawContext.drawRoundedRect(
         0.0F,
         0.0F,
         width,
         height,
         ProxyDotRadiusData.INSTANCE,
         GlowColor.method8096(BLACK.method7292(0.25F * visibility), BLACK.method7292(0.45F * visibility), BLACK.method7292(0.35F * visibility), BLACK.method7292(0.5F * visibility))
      );
   }

   private void drawSwirl(MenuDrawContext menuDrawContext, Identifier identifier, float width, float height, float visibility) {
      if (!(visibility <= 0.01F)) {
         double phase = this.phase;
         float f = Math.max(width, height);
         this.layer(
            menuDrawContext,
            identifier,
            width * 0.3F + (float)Math.sin(phase * 0.05) * width * 0.05F,
            height * 0.45F + (float)Math.cos(phase * 0.04) * height * 0.05F,
            f * 1.9F,
            (float)(phase * 1.6),
            visibility
         );
         this.layer(
            menuDrawContext,
            identifier,
            width * 0.75F + (float)Math.cos(phase * 0.045) * width * 0.06F,
            height * 0.6F + (float)Math.sin(phase * 0.035) * height * 0.06F,
            f * 1.5F,
            (float)(-phase * 2.2 + 140.0),
            visibility * 0.7F
         );
      }
   }

   private void layer(MenuDrawContext menuDrawContext, Identifier identifier, float x, float y, float guiScaledWidth, float angdeg, float visibility) {
      menuDrawContext.pose().pushMatrix();
      menuDrawContext.pose().translate(x, y);
      menuDrawContext.pose().rotate((float)Math.toRadians(angdeg));
      menuDrawContext.drawTexture(identifier, -guiScaledWidth / 2.0F, -guiScaledWidth / 2.0F, guiScaledWidth, guiScaledWidth, WHITE.method7292(visibility));
      menuDrawContext.pose().popMatrix();
   }

   private void drawClose(MenuDrawContext menuDrawContext, float visibility, float lastClickX, float lastClickY) {
      this.closeButton = new BoundsData(10.0F, 10.0F, 18.0F, 18.0F);
      boolean contains = this.closeButton.contains(lastClickX, lastClickY);
      if (contains) {
         menuDrawContext.drawRoundedRect(
            this.closeButton.getFloatValue(),
            this.closeButton.getFloatValue2(),
            18.0F,
            18.0F,
            ProxyDotRadiusData.method8069(9.0F),
            WHITE.method7292(0.14F * visibility)
         );
      }

      MediaGlyphs.close(
         menuDrawContext, this.closeButton.getFloatValue() + 9.0F, this.closeButton.getFloatValue2() + 9.0F, 9.0F, WHITE.method7292((contains ? 1.0F : 0.75F) * visibility)
      );
   }

   private float appear(long now, float f, float f2) {
      float clamp012 = clamp01(((float)(now - this.openedAt) - f) / f2);
      if (clamp012 <= 0.0F) {
         return 0.0F;
      } else {
         float f3 = 1.4F;
         float f4 = f3 + 1.0F;
         float f5 = clamp012 - 1.0F;
         return 1.0F + f4 * f5 * f5 * f5 + f3 * f5 * f5;
      }
   }

   private void drawNowPlaying(
      MenuDrawContext customDrawContext, MediaSessionManager mediaSessionManager, float x, float f, float maxWidth, long now, float f2, float visibility, float lastClickX, float lastClickY
   ) {
      float x1 = x - maxWidth / 2.0F;
      this.leftPanel = new BoundsData(x1 - 10.0F, f - 10.0F, maxWidth + 20.0F, maxWidth + 140.0F);
      float appear = this.appear(now, 40.0F, 700.0F);
      float appear2 = this.appear(now, 150.0F, 600.0F);
      float appear3 = this.appear(now, 240.0F, 600.0F);
      float f3 = visibility * clamp01(appear);
      float f4 = visibility * clamp01(appear2);
      float visibility2 = visibility * clamp01(appear3);
      float f5 = mediaSessionManager.method6007() ? 1.0F : 0.88F;
      this.coverScale = this.coverScale + (f5 - this.coverScale) * Math.min(1.0F, f2 * 6.0F);
      float f6 = Math.min(1.0F, (float)(now - mediaSessionManager.getLongValue4()) / 600.0F);
      float f7 = 1.0F - (1.0F - f6) * (1.0F - f6) * (1.0F - f6);
      customDrawContext.pose().pushMatrix();
      customDrawContext.pose().translate(x, f + maxWidth / 2.0F + (1.0F - appear) * 30.0F);
      customDrawContext.pose().scale(0.82F + 0.18F * appear, 0.82F + 0.18F * appear);
      customDrawContext.pose().translate(-x, -(f + maxWidth / 2.0F));
      float height = maxWidth * this.coverScale;
      float floatValue = x - height / 2.0F;
      float y = f + (maxWidth - height) / 2.0F;
      float intValue = Math.max(4.0F, maxWidth * 0.03F);
      RenderShaders.method5288(
         MatrixUtils.method664(customDrawContext.pose()),
         floatValue,
         y,
         height,
         height,
         0.0F,
         maxWidth * 0.05F,
         maxWidth * 0.16F,
         0.0F,
         ProxyDotRadiusData.method8069(intValue),
         BLACK.method7292(0.45F * f3 * this.coverScale)
      );
      Identifier texture = mediaSessionManager.method6011();
      Identifier id = mediaSessionManager.method6012();
      if (texture == null) {
         customDrawContext.drawRoundedRect(floatValue, y, height, height, ProxyDotRadiusData.method8069(intValue), WHITE.method7292(0.1F * f3));
         MediaGlyphs.playPause(customDrawContext, x, y + height / 2.0F, height * 0.22F, false, WHITE.method7292(0.4F * f3));
      } else {
         if (id != null && f7 < 1.0F) {
            customDrawContext.drawRoundedTexture(id, floatValue, y, height, height, ProxyDotRadiusData.method8069(intValue / 2.0F), WHITE.method7292(f3 * (1.0F - f7)));
         }

         customDrawContext.drawRoundedTexture(
            texture, floatValue, y, height, height, ProxyDotRadiusData.method8069(intValue / 2.0F), WHITE.method7292(f3 * (id == null ? 1.0F : f7))
         );
      }

      customDrawContext.pose().popMatrix();
      customDrawContext.pose().pushMatrix();
      customDrawContext.pose().translate(0.0F, (1.0F - appear2) * 16.0F);
      float centeredTextY = f + maxWidth + 11.0F;
      Font font = Fonts.NEW_SEMIBOLD.getFont(8.0F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(7.5F);
      boolean flag = lastClickX >= x1 && lastClickX <= x1 + maxWidth && lastClickY >= centeredTextY - 2.0F && lastClickY <= centeredTextY + 20.0F;
      this.text(customDrawContext, font, mediaSessionManager.title(), x1, centeredTextY, maxWidth, WHITE.method7292(0.95F * f4), flag);
      this.text(customDrawContext, font2, mediaSessionManager.method6006(), x1, centeredTextY + 10.5F, maxWidth, WHITE.method7292(0.6F * f4), flag);
      customDrawContext.pose().popMatrix();
      customDrawContext.pose().pushMatrix();
      customDrawContext.pose().translate(0.0F, (1.0F - appear3) * 18.0F);
      float f8 = centeredTextY + 30.0F;
      double d = mediaSessionManager.method6008();
      double d2 = mediaSessionManager.method6009();
      if (this.seekDrag) {
         this.seekPreview = clamp01((lastClickX - x1) / maxWidth);
      }

      boolean flag2 = lastClickX >= x1 - 3.0F && lastClickX <= x1 + maxWidth + 3.0F && lastClickY >= f8 - 5.0F && lastClickY <= f8 + 6.0F;
      this.barHover = this.barHover + ((!flag2 && !this.seekDrag ? 0.0F : 1.0F) - this.barHover) * Math.min(1.0F, f2 * 12.0F);
      this.seekBar = new BoundsData(x1 - 3.0F, f8 - 5.0F, maxWidth + 6.0F, 11.0F);
      float seekPreview = this.seekDrag ? this.seekPreview : (d > 0.0 ? clamp01((float)(d2 / d)) : 0.0F);
      this.slider(customDrawContext, x1, f8, maxWidth, seekPreview, 2.2F + 1.4F * this.barHover, 5.0F + 2.0F * this.barHover, visibility2);
      Font font3 = Fonts.NEW_MEDIUM.getFont(5.2F);
      double d3 = this.seekDrag ? this.seekPreview * d : d2;
      String time = MediaGlyphs.time(d3);
      String text = "-" + MediaGlyphs.time(Math.max(0.0, d - d3));
      customDrawContext.drawText(font3, time, x1, f8 + 5.0F, WHITE.method7292(0.45F * visibility2));
      customDrawContext.drawText(font3, text, x1 + maxWidth - font3.width(text), f8 + 5.0F, WHITE.method7292(0.45F * visibility2));
      float y2 = f8 + 27.0F;
      MediaHelperProcess mediaHelperProcess = MediaHelperProcess.getInstance();
      float f9 = Math.min(34.0F, maxWidth * 0.13F);
      this.previousButton = square(x - f9, y2, 20.0F);
      this.playButton = square(x, y2, 24.0F);
      this.nextButton = square(x + f9, y2, 20.0F);
      this.shuffleButton = mediaHelperProcess.isFlag2() ? square(x - f9 * 2.0F, y2, 16.0F) : null;
      this.repeatButton = mediaHelperProcess.isFlag3() ? square(x + f9 * 2.0F, y2, 16.0F) : null;
      this.hoverBack(customDrawContext, this.previousButton, lastClickX, lastClickY, visibility2);
      this.hoverBack(customDrawContext, this.playButton, lastClickX, lastClickY, visibility2);
      this.hoverBack(customDrawContext, this.nextButton, lastClickX, lastClickY, visibility2);
      MediaGlyphs.skip(customDrawContext, x - f9, y2, 13.0F, false, WHITE.method7292(0.92F * visibility2));
      MediaGlyphs.playPause(customDrawContext, x, y2, 15.0F, mediaSessionManager.method6007(), WHITE.method7292(visibility2));
      MediaGlyphs.skip(customDrawContext, x + f9, y2, 13.0F, true, WHITE.method7292(0.92F * visibility2));
      if (this.shuffleButton != null) {
         this.hoverBack(customDrawContext, this.shuffleButton, lastClickX, lastClickY, visibility2);
         MediaGlyphs.shuffle(customDrawContext, x - f9 * 2.0F, y2, 8.0F, WHITE.method7292((mediaHelperProcess.isFlag() ? 1.0F : 0.5F) * visibility2));
      }

      if (this.repeatButton != null) {
         this.hoverBack(customDrawContext, this.repeatButton, lastClickX, lastClickY, visibility2);
         MediaGlyphs.repeat(
            customDrawContext, x + f9 * 2.0F, y2, 8.5F, mediaHelperProcess.getIntValue() == 1, WHITE.method7292((mediaHelperProcess.getIntValue() != 0 ? 1.0F : 0.5F) * visibility2)
         );
      }

      float y3 = y2 + 25.0F;
      float x12 = x1 + 12.0F;
      float maxWidth2 = maxWidth - 12.0F;
      MediaPlayerState mediaPlayerState = MediaPlayerState.getInstance();
      if (this.volumeDrag) {
         mediaPlayerState.method1675(clamp01((lastClickX - x12) / maxWidth2));
      }

      boolean flag3 = lastClickX >= x12 - 3.0F && lastClickX <= x12 + maxWidth2 + 3.0F && lastClickY >= y3 - 5.0F && lastClickY <= y3 + 5.0F;
      this.volumeHover = this.volumeHover + ((!flag3 && !this.volumeDrag ? 0.0F : 1.0F) - this.volumeHover) * Math.min(1.0F, f2 * 12.0F);
      this.volumeBar = new BoundsData(x12 - 3.0F, y3 - 5.0F, maxWidth2 + 6.0F, 10.0F);
      float seekPreview2 = mediaPlayerState.method1673() ? mediaPlayerState.method1679() : 0.0F;
      int n = seekPreview2 < 0.02F ? 0 : (seekPreview2 < 0.5F ? 1 : 2);
      MediaGlyphs.speaker(customDrawContext, x1 + 3.5F, y3, 7.0F, n, WHITE.method7292(0.6F * visibility2));
      this.slider(customDrawContext, x12, y3, maxWidth2, seekPreview2, 2.2F + 1.2F * this.volumeHover, 5.0F + 2.0F * this.volumeHover, visibility2);
      if (mediaPlayerState.isFlag()) {
         Font font4 = Fonts.NEW_MEDIUM.getFont(5.2F);
         String str = t("gui.music.ducked");
         customDrawContext.drawText(font4, str, x - font4.width(str) / 2.0F, y3 + 6.0F, WHITE.method7292(0.45F * visibility2));
      }

      customDrawContext.pose().popMatrix();
   }

   private void slider(MenuDrawContext customDrawContext, float x1, float f, float maxWidth, float seekPreview, float height, float width, float visibility) {
      float y = f - height / 2.0F;
      customDrawContext.drawRoundedRect(x1, y, maxWidth, height, ProxyDotRadiusData.method8069(height / 2.0F), WHITE.method7292(0.22F * visibility));
      customDrawContext.drawRoundedRect(x1, y, Math.max(height, maxWidth * seekPreview), height, ProxyDotRadiusData.method8069(height / 2.0F), WHITE.method7292(0.82F * visibility));
      float f2 = x1 + maxWidth * seekPreview;
      customDrawContext.drawRoundedRect(f2 - width / 2.0F, f - width / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), WHITE.method7292(visibility));
   }

   private void drawLyrics(
      MenuDrawContext menuDrawContext,
      MediaSessionManager mediaSessionManager,
      float floatValue,
      float y1,
      float layoutWidth,
      float height,
      long now,
      float f,
      float visibility,
      float lastClickX,
      float lastClickY
   ) {
      this.lyricHits.clear();
      LyricsFetcher.Data2 data2 = LyricsFetcher.method528(mediaSessionManager.title(), mediaSessionManager.method6006(), mediaSessionManager.method6008());
      float layoutSize = Math.max(15.0F, Math.min(34.0F, height * 0.058F));
      Font font = Fonts.BOLD.getFont(layoutSize);
      if (data2 != LyricsFetcher.Data2.INSTANCE2 && !data2.lines().isEmpty()) {
         String layoutKey = mediaSessionManager.getText() + data2.isFlag();
         if (!layoutKey.equals(this.layoutKey) || Math.abs(layoutWidth - this.layoutWidth) > 0.5F || Math.abs(layoutSize - this.layoutSize) > 0.1F) {
            this.layout(data2, font, layoutWidth, layoutSize);
            this.layoutKey = layoutKey;
            this.layoutWidth = layoutWidth;
            this.layoutSize = layoutSize;
            this.lyricScroll = Float.NaN;
            this.manualOffset = 0.0F;
         }

         double d = mediaSessionManager.method6009() + MusicHud.method6121();
         int activeBlock = data2.isFlag() ? this.activeBlock(d) : -1;
         float f2 = y1 + height * 0.36F;
         float top;
         if (activeBlock >= 0) {
            top = this.blocks.get(activeBlock).top;
         } else if (data2.isFlag()) {
            top = -layoutSize * 2.5F;
         } else {
            top = 0.0F;
            f2 = y1 + height * 0.12F;
         }

         if (now > this.manualUntil) {
            this.manualOffset = this.manualOffset + -this.manualOffset * Math.min(1.0F, f * 4.0F);
         }

         top += this.manualOffset;
         if (Float.isNaN(this.lyricScroll)) {
            this.lyricScroll = top;
         }

         float f3 = 90.0F;
         float f4 = 2.0F * (float)Math.sqrt(f3) * 0.95F;
         this.lyricVelocity = this.lyricVelocity + ((top - this.lyricScroll) * f3 - this.lyricVelocity * f4) * f;
         this.lyricScroll = this.lyricScroll + this.lyricVelocity * f;
         if (this.emphasis.length != this.blocks.size()) {
            this.emphasis = new float[this.blocks.size()];
         }

         menuDrawContext.enableScissor((int)(floatValue - 30.0F), (int)y1, (int)(floatValue + layoutWidth + 40.0F), (int)(y1 + height));
         float f5 = font.height() * 1.08F;
         int n = 0;

         for (int i = 0; i < this.blocks.size(); i++) {
            NowPlayingScreen.Block block = this.blocks.get(i);
            int n2 = activeBlock >= 0 ? i - activeBlock : 2;
            float f6 = activeBlock >= 0 ? Math.min(4, Math.max(0, n2)) * 0.035F : 0.0F;
            float f7 = f2 + block.top - this.lyricScroll + f6 * (top - this.lyricScroll);
            if (!(f7 + block.height < y1 - 20.0F) && !(f7 > y1 + height + 20.0F)) {
               boolean flag = i == activeBlock;
               this.emphasis[i] = this.emphasis[i] + ((flag ? 1.0F : 0.0F) - this.emphasis[i]) * Math.min(1.0F, f * 10.0F);
               float emphasi = this.emphasis[i];
               float appear = this.appear(now, 200.0F + n * 55.0F, 700.0F);
               n++;
               boolean flag2 = lastClickX >= floatValue - 8.0F && lastClickX <= floatValue + layoutWidth && lastClickY >= f7 - 4.0F && lastClickY <= f7 + block.height + 4.0F;
               float f8;
               if (!data2.isFlag()) {
                  f8 = 0.75F;
               } else if (n2 < 0) {
                  f8 = Math.max(0.05F, 0.22F + n2 * 0.06F);
               } else {
                  f8 = Math.max(0.12F, 0.34F - n2 * 0.05F);
               }

               float visibility2 = f8 + (1.0F - f8) * emphasi;
               if (flag2 && data2.isFlag()) {
                  visibility2 = Math.max(visibility2, 0.7F);
               }

               float clamp012 = clamp01(Math.min(f7 - y1, y1 + height - (f7 + block.height)) / (layoutSize * 3.0F) + 0.15F);
               visibility2 *= clamp012 * visibility * clamp01(appear);
               if (data2.isFlag()) {
                  this.lyricHits.add(new NowPlayingScreen.Hit(new BoundsData(floatValue - 8.0F, f7 - 4.0F, layoutWidth + 8.0F, block.height + 8.0F), block.time));
                  if (flag2 && !flag) {
                     menuDrawContext.drawRoundedRect(
                        floatValue - 8.0F, f7 - 5.0F, layoutWidth + 8.0F, block.height + 10.0F, ProxyDotRadiusData.method8069(8.0F), WHITE.method7292(0.08F * visibility)
                     );
                  }
               }

               float f9 = data2.isFlag() && !flag2 ? (1.0F - emphasi) * Math.min(1.0F, 0.45F + Math.abs(n2) * 0.18F) : 0.0F;
               float x = 0.96F + 0.04F * emphasi;
               float f10 = (1.0F - appear) * 50.0F;
               menuDrawContext.pose().pushMatrix();
               menuDrawContext.pose().translate(floatValue + f10, f7 + block.height / 2.0F);
               menuDrawContext.pose().scale(x, x);
               menuDrawContext.pose().translate(-floatValue, -(f7 + block.height / 2.0F));
               if (block.dots) {
                  this.drawDots(menuDrawContext, floatValue, f7 + block.height / 2.0F, layoutSize, flag, now, visibility2);
               } else {
                  float centeredTextY = f7;

                  for (String line : block.lines) {
                     this.drawSoftText(menuDrawContext, font, line, floatValue, centeredTextY, visibility2, f9 * layoutSize * 0.09F);
                     centeredTextY += f5;
                  }
               }

               menuDrawContext.pose().popMatrix();
            }
         }

         if (data2.isFlag() && activeBlock < 0 && !this.blocks.isEmpty() && this.blocks.get(0).time > 2.5) {
            float f6 = f2 - layoutSize * 2.5F - this.lyricScroll + top - this.manualOffset + layoutSize * 0.5F;
            this.drawDots(menuDrawContext, floatValue, f6, layoutSize, true, now, visibility);
         }

         menuDrawContext.disableScissor();
      } else {
         Font font2 = Fonts.BOLD.getFont(layoutSize * 0.8F);
         String text = t(data2 == LyricsFetcher.Data2.INSTANCE2 ? "gui.music.searching" : (data2.isFlag2() ? "gui.music.instrumental" : "gui.music.noLyrics"));
         menuDrawContext.drawText(font2, text, floatValue, y1 + height * 0.42F, WHITE.method7292(0.5F * visibility));
         if (data2 != LyricsFetcher.Data2.INSTANCE2 && !data2.isFlag2()) {
            Font font3 = Fonts.NEW_MEDIUM.getFont(6.5F);
            menuDrawContext.drawText(font3, t("gui.music.source"), floatValue, y1 + height * 0.42F + font2.height() + 4.0F, WHITE.method7292(0.3F * visibility));
         }
      }
   }

   private void drawSoftText(MenuDrawContext menuDrawContext, Font font, String line, float floatValue, float centeredTextY, float visibility, float f) {
      if (!(visibility <= 0.01F)) {
         if (f < 0.25F) {
            menuDrawContext.drawText(font, line, floatValue, centeredTextY, WHITE.method7292(visibility));
         } else {
            ZenithColor zenithColor = WHITE.method7292(visibility * 0.2F);

            for (int i = 1; i <= 2; i++) {
               float f2 = f * i / 2.0F;

               for (int j = 0; j < 6; j++) {
                  double d = (Math.PI * 2) * j / 6.0 + i * 0.5;
                  menuDrawContext.drawText(font, line, floatValue + (float)Math.cos(d) * f2, centeredTextY + (float)Math.sin(d) * f2, zenithColor);
               }
            }

            menuDrawContext.drawText(font, line, floatValue, centeredTextY, WHITE.method7292(visibility * 0.25F));
         }
      }
   }

   private void drawDots(MenuDrawContext menuDrawContext, float floatValue, float f, float layoutSize, boolean flag, long now, float visibility) {
      float f2 = layoutSize * 0.26F;

      for (int i = 0; i < 3; i++) {
         float f3 = flag ? 0.5F + 0.5F * (float)Math.sin(now / 280.0 - i * 0.9) : 0.0F;
         float width = f2 * (1.0F + 0.3F * f3);
         menuDrawContext.drawRoundedRect(
            floatValue + i * f2 * 1.9F + (f2 - width) / 2.0F,
            f - width / 2.0F,
            width,
            width,
            ProxyDotRadiusData.method8069(width / 2.0F),
            WHITE.method7292(visibility * (flag ? 0.5F + 0.5F * f3 : 0.4F))
         );
      }
   }

   private int activeBlock(double d) {
      int n = -1;
      int n2 = 0;

      while (n2 < this.blocks.size() && this.blocks.get(n2).time <= d) {
         n = n2++;
      }

      return n;
   }

   private void layout(LyricsFetcher.Data2 data2, Font font, float layoutWidth, float layoutSize) {
      this.blocks.clear();
      float f = font.height() * 1.08F;
      float f2 = layoutSize * 0.85F;
      float blocksHeight = 0.0F;
      var lines = data2.lines();

      for (int i = 0; i < lines.size(); i++) {
         LyricsFetcher.Data3 data3 = (LyricsFetcher.Data3)lines.get(i);
         String str = data3.text().trim();
         boolean empty = str.isEmpty();
         if (empty && data2.isFlag()) {
            double time = i + 1 < lines.size() ? ((LyricsFetcher.Data3)lines.get(i + 1)).time() : data3.time() + 10.0;
            if (time - data3.time() < 4.0) {
               continue;
            }
         }

         if (empty && !data2.isFlag()) {
            blocksHeight += f2 * 0.5F;
         } else {
            List<String> strings = empty ? List.<String>of() : wrap(font, str, layoutWidth);
            float height = empty ? layoutSize * 0.6F : (strings.size() - 1) * f + font.height();
            this.blocks.add(new NowPlayingScreen.Block(data3.time(), strings, blocksHeight, height, empty));
            blocksHeight += height + f2;
         }
      }

      this.blocksHeight = blocksHeight;
   }

   private static List<String> wrap(Font font, String str, float layoutWidth) {
      ArrayList<String> strings = new ArrayList<>();
      StringBuilder builder = new StringBuilder();

      for (String str2 : str.split(" ")) {
         String text = builder.isEmpty() ? str2 : builder + " " + str2;
         if (!(font.width(text) <= layoutWidth) && !builder.isEmpty()) {
            strings.add(builder.toString());
            builder.setLength(0);
            builder.append(str2);
         } else {
            builder.setLength(0);
            builder.append(text);
         }
      }

      if (!builder.isEmpty()) {
         strings.add(builder.toString());
      }

      return strings;
   }

   private void text(MenuDrawContext customDrawContext, Font font, String title, float x1, float centeredTextY, float maxWidth, ZenithColor zenithColor, boolean flag) {
      if (title != null && !title.isEmpty()) {
         float width = font.width(title);
         if (width <= maxWidth) {
            customDrawContext.drawText(font, title, x1, centeredTextY, zenithColor);
         } else if (!flag) {
            customDrawContext.drawText(font, MediaGlyphs.ellipsize(font, title, maxWidth), x1, centeredTextY, zenithColor);
         } else {
            float f = 30.0F;
            float f2 = width + f;
            long l = (long)(f2 / 30.0F * 1000.0F);
            long l2 = 1200L;
            long l3 = System.currentTimeMillis() % (l + l2);
            float f3 = l3 < l2 ? 0.0F : (float)(l3 - l2) / (float)l * f2;
            customDrawContext.enableScissor((int)x1, (int)(centeredTextY - 2.0F), (int)Math.ceil(x1 + maxWidth), (int)Math.ceil(centeredTextY + font.height() + 3.0F));
            customDrawContext.drawText(font, title, x1 - f3, centeredTextY, zenithColor);
            customDrawContext.drawText(font, title, x1 - f3 + f2, centeredTextY, zenithColor);
            customDrawContext.disableScissor();
         }
      }
   }

   private void hoverBack(MenuDrawContext customDrawContext, BoundsData previousButton, float lastClickX, float lastClickY, float f) {
      if (previousButton != null && previousButton.contains(lastClickX, lastClickY)) {
         customDrawContext.drawRoundedRect(
            previousButton.getFloatValue(),
            previousButton.getFloatValue2(),
            previousButton.width(),
            previousButton.height(),
            ProxyDotRadiusData.method8069(previousButton.width() / 2.0F),
            WHITE.method7292(0.1F * f)
         );
      }
   }

   private static BoundsData square(float x, float f, float width) {
      return new BoundsData(x - width / 2.0F, f - width / 2.0F, width, width);
   }

   private void clearHits() {
      this.seekBar = this.volumeBar = this.shuffleButton = this.previousButton = this.playButton = this.nextButton = this.repeatButton = this.leftPanel = null;
      this.lyricHits.clear();
   }

   private float[] toContent(double x, double y) {
      float visibility = this.visibility(System.currentTimeMillis());
      float f = 0.97F + 0.03F * visibility;
      return new float[]{(float)((x - this.width / 2.0F) / f + this.width / 2.0F), (float)((y - this.height / 2.0F) / f + this.height / 2.0F)};
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      if (org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()) != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         float[] content = this.toContent(click.x(), click.y());
         float lastClickX = content[0];
         float lastClickY = content[1];
         MediaSessionManager mediaSessionManager = MediaSessionManager.getInstance();
         if (this.closeButton != null && this.closeButton.contains(lastClickX, lastClickY)) {
            this.onClose();
            return true;
         } else if (this.seekBar != null && this.seekBar.contains(lastClickX, lastClickY) && mediaSessionManager.method6008() > 0.0) {
            this.seekDrag = true;
            this.seekPreview = clamp01((lastClickX - this.seekBar.getFloatValue() - 3.0F) / (this.seekBar.width() - 6.0F));
            return true;
         } else if (this.volumeBar != null && this.volumeBar.contains(lastClickX, lastClickY)) {
            this.volumeDrag = true;
            MediaPlayerState.getInstance().method1675(clamp01((lastClickX - this.volumeBar.getFloatValue() - 3.0F) / (this.volumeBar.width() - 6.0F)));
            return true;
         } else if (contains(this.playButton, lastClickX, lastClickY)) {
            mediaSessionManager.playPause();
            return true;
         } else if (contains(this.previousButton, lastClickX, lastClickY)) {
            mediaSessionManager.previous();
            return true;
         } else if (contains(this.nextButton, lastClickX, lastClickY)) {
            mediaSessionManager.next();
            return true;
         } else if (contains(this.shuffleButton, lastClickX, lastClickY) && mediaSessionManager.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6864(mediaSessionManager.getIMediaSession().getOwner());
            return true;
         } else if (contains(this.repeatButton, lastClickX, lastClickY) && mediaSessionManager.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6865(mediaSessionManager.getIMediaSession().getOwner());
            return true;
         } else {
            for (NowPlayingScreen.Hit lyricHit : this.lyricHits) {
               if (lyricHit.rect.contains(lastClickX, lastClickY)) {
                  mediaSessionManager.method6019(Math.max(0.0, lyricHit.time - MusicHud.method6121() + 0.05));
                  this.manualOffset = 0.0F;
                  this.manualUntil = 0L;
                  return true;
               }
            }

            return super.mouseClicked(click, doubled);
         }
      }
   }

   @Override
   public boolean mouseReleased(MouseButtonEvent click) {
      if (this.seekDrag) {
         this.seekDrag = false;
         MediaSessionManager.getInstance().method6019(this.seekPreview * MediaSessionManager.getInstance().method6008());
         return true;
      } else if (this.volumeDrag) {
         this.volumeDrag = false;
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      float[] content = this.toContent(mouseX, mouseY);
      if (this.leftPanel != null && this.leftPanel.contains(content[0], content[1])) {
         MediaPlayerState.getInstance().method1676((float)Math.signum(verticalAmount) * 0.05F);
         return true;
      } else if (!this.blocks.isEmpty()) {
         this.manualOffset = this.manualOffset - (float)verticalAmount * this.layoutSize * 2.0F;
         this.manualOffset = Math.max(-this.blocksHeight, Math.min(this.blocksHeight, this.manualOffset));
         this.manualUntil = System.currentTimeMillis() + 3500L;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      int keyCode = org.zenith.utility.misc.KeyCompat.toGlfw(input.key());
      MediaSessionManager mediaSessionManager = MediaSessionManager.getInstance();
      if (keyCode == 32) {
         mediaSessionManager.playPause();
         return true;
      } else if (keyCode == 262) {
         mediaSessionManager.method6019(mediaSessionManager.method6009() + 5.0);
         return true;
      } else if (keyCode == 263) {
         mediaSessionManager.method6019(mediaSessionManager.method6009() - 5.0);
         return true;
      } else if (keyCode != 265 && keyCode != 264) {
         return super.keyPressed(input);
      } else {
         MediaPlayerState.getInstance().method1676(keyCode == 265 ? 0.05F : -0.05F);
         return true;
      }
   }

   private static boolean contains(BoundsData playButton, float lastClickX, float lastClickY) {
      return playButton != null && playButton.contains(lastClickX, lastClickY);
   }

   private static float clamp01(float appear) {
      return Math.max(0.0F, Math.min(1.0F, appear));
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private static final class Block {
      private final double time;
      private final List<String> lines;
      private final float top;
      private final float height;
      private final boolean dots;

      public double time() {
         return this.time;
      }

      public List<String> lines() {
         return this.lines;
      }

      public float top() {
         return this.top;
      }

      public float height() {
         return this.height;
      }

      public boolean dots() {
         return this.dots;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Block other)) return false;
         return java.util.Objects.equals(this.time, other.time) && java.util.Objects.equals(this.lines, other.lines) && java.util.Objects.equals(this.top, other.top) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.dots, other.dots);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(time, lines, top, height, dots);
      }

      @Override
      public String toString() {
         return "Block[" + "time=" + this.time + ", " + "lines=" + this.lines + ", " + "top=" + this.top + ", " + "height=" + this.height + ", " + "dots=" + this.dots + "]";
      }

      private Block(double time, List<String> lines, float top, float height, boolean dots) {
         this.time = time;
         this.lines = lines;
         this.top = top;
         this.height = height;
         this.dots = dots;
      }
   }

   private static final class Hit {
      private final BoundsData rect;
      private final double time;

      public BoundsData rect() {
         return this.rect;
      }

      public double time() {
         return this.time;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Hit other)) return false;
         return java.util.Objects.equals(this.rect, other.rect) && java.util.Objects.equals(this.time, other.time);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rect, time);
      }

      @Override
      public String toString() {
         return "Hit[" + "rect=" + this.rect + ", " + "time=" + this.time + "]";
      }

      private Hit(BoundsData rect, double time) {
         this.rect = rect;
         this.time = time;
      }
   }
}

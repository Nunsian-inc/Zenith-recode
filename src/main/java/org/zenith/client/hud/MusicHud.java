package org.zenith.client.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.resources.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.media.MediaGlyphs;
import org.zenith.client.screens.media.NowPlayingScreen;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.LyricsFetcher;
import org.zenith.utility.config.MediaHelperProcess;
import org.zenith.utility.misc.MediaPlayerState;
import org.zenith.utility.misc.MediaSessionManager;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MusicHud extends Interface {
   private static MusicHud INSTANCE;
   private static final float CONST_floatValue = 122.0F;
   private static final float CONST_floatValue2 = 250.0F;
   private static final float CONST_floatValue3 = 4.0F;
   private static final float CONST_floatValue4 = 17.0F;
   private static final float CONST_floatValue5 = 25.0F;
   private static final float CONST_floatValue6 = 22.0F;
   private static final float CONST_floatValue7 = 15.0F;
   private static final float CONST_floatValue8 = 8.0F;
   private static final long CONST_longValue = 520L;
   private static final float CONST_floatValue9 = 10.0F;
   private static final float CONST_floatValue10 = 300.0F;
   private static final float CONST_floatValue11 = 200.0F;
   private static final float CONST_floatValue12 = 0.6F;
   private final BooleanSetting colors = new BooleanSetting("module.interface.music.colors", "module.interface.music.colors.desc", true);
   private final BooleanSetting equalizer = new BooleanSetting("module.interface.music.equalizer", "module.interface.music.equalizer.desc", true);
   private final BooleanSetting vinyl = new BooleanSetting("module.interface.music.vinyl", "module.interface.music.vinyl.desc", true);
   private final BooleanSetting lyrics = new BooleanSetting("module.interface.music.lyrics", "module.interface.music.lyrics.desc", true);
   private final BooleanSetting duck = new BooleanSetting("module.interface.music.duck", "module.interface.music.duck.desc", true);
   private final SliderSetting duckLevel = new SliderSetting(
      "module.interface.music.duckLevel", 30.0F, 0.0F, 90.0F, 5.0F, "module.interface.music.duckLevel.desc", "%", this.duck::isEnabled, null
   );
   private final SliderSetting lyricsOffset = new SliderSetting(
      "module.interface.music.lyricsOffset", 0.0F, -3.0F, 3.0F, 0.1F, "module.interface.music.lyricsOffset.desc", "s", this.lyrics::isEnabled, null
   );
   private final BindSetting openKey = new BindSetting("module.interface.music.openKey", "module.interface.music.openKey.desc");
   private final Animation animation = new Animation(420L, 0.0F, Easing.INSTANCE4);
   private final Animation animation2 = new Animation(240L, Easing.INSTANCE15);
   private final Animation animation3 = new Animation(160L, Easing.INSTANCE15);
   private final float[] floatValues = new float[3];
   private long lastFrame;
   private long lastPoll;
   private float animWidth = 122.0F;
   private float discRotation;
   private float floatValue3;
   private final MusicHud.Entry entry = new MusicHud.Entry();
   private final MusicHud.Entry entry2 = new MusicHud.Entry();
   private int intValue = -1;
   private String text = "";
   private boolean flag;
   private float seekPreview;
   private boolean flag2;
   private BoundsData boundsData;
   private BoundsData boundsData2;
   private boolean flag3;
   private BoundsData boundsData3;
   private BoundsData boundsData4;
   private BoundsData boundsData5;
   private BoundsData playBounds;
   private BoundsData boundsData6;
   private BoundsData boundsData7;

   public MusicHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
      INSTANCE = this;
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1x -> this.method6120());
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)minecraft -> {
         if (MediaPlayerState.getInstance().isFlag()) {
            MediaPlayerState.getInstance().method1677(false, 0.0F, false);
         }
      });
   }

   private void method6120() {
      if (MediaPlayerState.getInstance().isFlag() && (mc.player == null || !this.isEnabled() || !InterfaceModule.INSTANCE.isEnabled())) {
         MediaPlayerState.getInstance().method1677(false, 0.0F, false);
      }
   }

   public static float method6121() {
      MusicHud musicHud = INSTANCE;
      return 0.6F + (musicHud == null ? 0.0F : musicHud.lyricsOffset.getValue3());
   }

   public static boolean method6122(double d) {
      MusicHud musicHud = INSTANCE;
      if (musicHud != null && musicHud.isEnabled() && MediaSessionManager.getInstance().active() && !(musicHud.animation.getValue() <= 0.01F)) {
         UVPair uvPair = musicHud.method2099();
         if (!musicHud.inside(uvPair.u(), uvPair.v(), musicHud.floatValue, musicHud.floatValue2, musicHud.width, musicHud.height)) {
            return false;
         } else {
            MediaPlayerState.getInstance().method1676((float)Math.signum(d) * 0.05F);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void tick() {
      if (mc.player != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastPoll > 500L) {
            this.lastPoll = now;
            MediaSessionManager.getInstance().method6000();
         }

         Voice voice = Zenith.getInstance().getVoice();
         boolean flag = voice != null && voice.method358();
         MediaPlayerState.getInstance().method1677(flag, this.duckLevel.getValue3() / 100.0F, this.duck.isEnabled());
         boolean flag2 = this.openKey.getKeyCode() > 0 && mc.gui.screen() == null && UseItemUtils.method6521(this.openKey);
         if (flag2 && !this.flag2) {
            mc.gui.setScreen(new NowPlayingScreen());
         }

         this.flag2 = flag2;
      }
   }

   @Override
   public boolean method2056(MouseButtonEvent event) {
      if (event.getIntValue2() == 1 && event.getIntValue() == 0 && MediaSessionManager.getInstance().active()) {
         UVPair uvPair = this.method2099();
         float lastClickX = uvPair.u();
         float lastClickY = uvPair.v();
         MediaSessionManager mediaSessionManager = MediaSessionManager.getInstance();
         if (this.method6138(this.boundsData, lastClickX, lastClickY)) {
            mc.gui.setScreen(new NowPlayingScreen());
            return true;
         } else if (this.boundsData3 != null
            && this.inside(
               lastClickX,
               lastClickY,
               this.boundsData3.getFloatValue(),
               this.boundsData3.getFloatValue2() - 3.0F,
               this.boundsData3.width(),
               this.boundsData3.height() + 6.0F
            )
            && mediaSessionManager.method6008() > 0.0) {
            this.flag = true;
            this.seekPreview = this.method6137(lastClickX);
            return true;
         } else if (this.method6138(this.playBounds, lastClickX, lastClickY) || this.method6138(this.boundsData2, lastClickX, lastClickY)) {
            mediaSessionManager.playPause();
            return true;
         } else if (this.method6138(this.boundsData5, lastClickX, lastClickY)) {
            mediaSessionManager.previous();
            return true;
         } else if (this.method6138(this.boundsData6, lastClickX, lastClickY)) {
            mediaSessionManager.next();
            return true;
         } else if (this.method6138(this.boundsData4, lastClickX, lastClickY) && mediaSessionManager.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6864(mediaSessionManager.getIMediaSession().getOwner());
            return true;
         } else if (this.method6138(this.boundsData7, lastClickX, lastClickY) && mediaSessionManager.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6865(mediaSessionManager.getIMediaSession().getOwner());
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public float method254() {
      return this.animation.getValue();
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      if (!(mc.gui.screen() instanceof PauseScreen)) {
         MediaSessionManager mediaSessionManager = MediaSessionManager.getInstance();
         long now = System.currentTimeMillis();
         if (now - this.lastPoll > 500L) {
            this.lastPoll = now;
            mediaSessionManager.method6000();
         }

         float f = this.lastFrame == 0L ? 0.016F : Math.min(0.1F, (float)(now - this.lastFrame) / 1000.0F);
         this.lastFrame = now;
         boolean flag = mc.gui.screen() instanceof ChatScreen;
         boolean active = mediaSessionManager.active();
         boolean flag2 = active && (mediaSessionManager.method6007() || now - mediaSessionManager.getLongValue9() < 1500L);
         boolean visible = flag2 && now - mediaSessionManager.getSeenAt() < 1200L || this.method2074();
         if (visible && !this.flag3) {
            this.entry.previous = "";
            this.entry.changedAt = now + 120L;
            this.entry2.previous = "";
            this.entry2.changedAt = now + 200L;
         }

         this.flag3 = visible;
         this.animation.method2807(visible);
         if (this.animation.getValue() <= 0.001F) {
            this.flag = false;
            this.clearBounds();
         } else {
            UVPair uvPair = this.method2099();
            float lastClickX = uvPair.u();
            float lastClickY = uvPair.v();
            boolean flag3 = flag || this.method2074();
            boolean visible2 = flag3 && active && (this.inside(lastClickX, lastClickY, this.floatValue, this.floatValue2, this.animWidth, this.height) || this.flag);
            this.animation2.method2807(visible2);
            float previewAlpha = this.animation2.getValue();
            double d = mediaSessionManager.method6009();
            double d2 = mediaSessionManager.method6008();
            String title = active ? mediaSessionManager.title() : Zenith.getInstance().getLocalization().translate("gui.music.nothing");
            String str = this.method6123(mediaSessionManager, active, d, d2);
            this.entry.method6139(title, false, now);
            this.entry2.method6139(str != null ? str : (active ? mediaSessionManager.method6006() : ""), str != null, now);
            Font font = Fonts.NEW_SEMIBOLD.getFont(6.0F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
            float f2 = 63.0F;
            float f3 = Math.max(font.width(this.entry.text), font2.width(this.entry2.text)) + f2 + 2.0F;
            float animWidth = Math.max(122.0F, Math.min(250.0F, f3));
            this.animWidth = this.animWidth + (animWidth - this.animWidth) * Math.min(1.0F, f * 7.0F);
            if (Math.abs(animWidth - this.animWidth) < 0.2F) {
               this.animWidth = animWidth;
            }

            this.width = this.animWidth;
            this.height = 25.0F + 22.0F * previewAlpha;
            ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
            ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
            boolean flag4 = this.colors.isEnabled() && active;
            ZenithColor color = flag4 ? mediaSessionManager.color(0) : zenithColor;
            ZenithColor zenithColor2 = currentStyle.getTextEnable().getZenithColor();
            ZenithColor zenithColor3 = currentStyle.getTextSecondary().getZenithColor().method7295(zenithColor2, 0.35F);
            ZenithColor zenithColor4 = flag4 ? zenithColor2.method7295(color.method7295(ZenithColor.INSTANCE, 0.45F).method7293(zenithColor2.getIntValue4()), 0.4F) : zenithColor2;
            float intValue = InterfaceModule.method6801();
            float f4 = Math.max(0.0F, this.animation.getValue());
            customDrawContext.pushMatrix();
            float x = this.floatValue + this.width / 2.0F;
            float f5 = this.floatValue2 + this.height / 2.0F;
            float x2 = 0.55F + 0.45F * f4;
            float y = 0.35F + 0.65F * f4;
            customDrawContext.pose().translate(x, f5 + (1.0F - Math.min(1.0F, f4)) * 6.0F);
            customDrawContext.pose().scale(x2, y);
            customDrawContext.pose().translate(-x, -f5);
            float width = 25.0F;
            customDrawContext.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
            customDrawContext.drawRoundedRect(
               this.floatValue,
               this.floatValue2,
               this.width,
               this.height,
               ProxyDotRadiusData.method8069(intValue),
               currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
            );
            ZenithColor zenithColor5 = currentStyle.getHeaderHudBackground().getZenithColor();
            if (flag4) {
               zenithColor5 = zenithColor5.method7295(color.method7293(zenithColor5.getIntValue4()), 0.16F);
            }

            customDrawContext.drawRoundedRect(this.floatValue, this.floatValue2, width, 25.0F, ProxyDotRadiusData.method8069(intValue), zenithColor5);
            this.boundsData = new BoundsData(this.floatValue, this.floatValue2, width, 25.0F);
            this.method6128(customDrawContext, mediaSessionManager, this.floatValue + 4.0F, this.floatValue2 + 4.0F, now, f, active);
            float floatValue = this.floatValue + this.width - 4.0F - 15.0F;
            float y2 = this.floatValue2 + 5.0F;
            float floatValue2 = this.floatValue + width + 4.0F;
            boolean flag5 = this.equalizer.isEnabled() && active;
            float f6 = floatValue - 4.0F - 8.0F;
            float f7 = !flag5 && !this.method6131(now) ? floatValue - 3.0F : f6 - 3.0F;
            float maxWidth = Math.max(10.0F, f7 - floatValue2);
            boolean flag6 = visible2 && this.inside(lastClickX, lastClickY, floatValue2, this.floatValue2, maxWidth, 25.0F);
            float centeredTextY = this.floatValue2 + 5.5F;
            float centeredTextY2 = centeredTextY + font.height() + 1.5F;
            this.method6124(customDrawContext, font, this.entry, floatValue2, centeredTextY, maxWidth, this.floatValue2 + 1.0F, centeredTextY2 - 0.5F, zenithColor2, zenithColor2, now, flag6);
            this.method6124(customDrawContext, font2, this.entry2, floatValue2, centeredTextY2, maxWidth, centeredTextY2 - 0.5F, this.floatValue2 + 25.0F - 1.0F, zenithColor3, zenithColor4, now, flag6);
            if (this.method6131(now)) {
               this.method6132(customDrawContext, f6 - 3.0F, y2 + 2.0F, now, zenithColor2, color);
            } else if (flag5) {
               this.method6130(customDrawContext, f6, this.floatValue2 + 8.0F, mediaSessionManager, f, color);
            }

            this.boundsData2 = new BoundsData(floatValue - 1.0F, y2 - 1.0F, 17.0F, 17.0F);
            this.method6133(
               customDrawContext, floatValue, y2, d, d2, color, zenithColor2, currentStyle, active && mediaSessionManager.method6007(), this.method6138(this.boundsData2, lastClickX, lastClickY) && flag
            );
            this.method6134(customDrawContext, mediaSessionManager, previewAlpha, color, zenithColor2, zenithColor3, lastClickX, lastClickY, d, d2);
            customDrawContext.popMatrix();
         }
      }
   }

   private String method6123(MediaSessionManager mediaSessionManager, boolean active, double d, double d2) {
      if (this.lyrics.isEnabled() && active) {
         LyricsFetcher.Data2 data2 = LyricsFetcher.method528(mediaSessionManager.title(), mediaSessionManager.method6006(), d2);
         if (!data2.isFlag()) {
            return null;
         } else {
            if (!mediaSessionManager.getText().equals(this.text)) {
               this.text = mediaSessionManager.getText();
               this.intValue = -1;
            }

            int intValue = data2.method541(d + method6121());
            this.intValue = intValue;
            if (intValue < 0) {
               return null;
            } else {
               String str = data2.lines().get(intValue).text().trim();
               return str.isEmpty() ? null : str;
            }
         }
      } else {
         return null;
      }
   }

   private void method6124(
      CustomDrawContext customDrawContext,
      Font font,
      MusicHud.Entry entry,
      float floatValue,
      float centeredTextY,
      float maxWidth,
      float f,
      float f2,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      long now,
      boolean flag
   ) {
      float f3 = (float)(now - entry.changedAt);
      customDrawContext.enableScissor((int)Math.floor(floatValue - 1.0F), (int)Math.floor(f), (int)Math.ceil(floatValue + maxWidth + 1.0F), (int)Math.ceil(f2));
      if (!entry.previous.isEmpty()) {
         String ellipsize = MediaGlyphs.ellipsize(font, entry.previous, maxWidth);
         if (f3 < 200.0F + ellipsize.length() * 10.0F * 0.5F) {
            this.method6125(customDrawContext, font, ellipsize, floatValue, centeredTextY, entry.flag2 ? zenithColor2 : zenithColor, f3, false);
         }
      }

      if (!entry.text.isEmpty()) {
         float f4 = !entry.previous.isEmpty() && !entry.flag ? 60.0F : 0.0F;
         float f5 = f3 - f4;
         float f6 = 300.0F + entry.text.length() * 10.0F;
         ZenithColor zenithColor3 = entry.flag ? zenithColor2 : zenithColor;
         if (f5 >= f6) {
            this.method6127(customDrawContext, font, entry.text, floatValue, centeredTextY, maxWidth, zenithColor3, flag);
         } else if (f5 > 0.0F) {
            this.method6125(customDrawContext, font, MediaGlyphs.ellipsize(font, entry.text, maxWidth), floatValue, centeredTextY, zenithColor3, f5, true);
         }
      }

      customDrawContext.disableScissor();
   }

   private void method6125(CustomDrawContext customDrawContext, Font font, String ellipsize, float floatValue, float centeredTextY, ZenithColor zenithColor, float f, boolean flag) {
      float floatValue2 = floatValue;

      for (int i = 0; i < ellipsize.length(); i++) {
         String text = String.valueOf(ellipsize.charAt(i));
         float width = font.width(text);
         if (!text.isBlank()) {
            if (flag) {
               float clamp012 = clamp01((f - i * 10.0F) / 300.0F);
               if (clamp012 > 0.0F) {
                  float f2 = method6126(clamp012);
                  float previewAlpha = 1.0F - (1.0F - clamp012) * (1.0F - clamp012);
                  customDrawContext.drawText(font, text, floatValue2 - (1.0F - clamp012) * 1.5F, centeredTextY + (1.0F - f2) * 6.0F, zenithColor.method7294(previewAlpha));
               }
            } else {
               float clamp012 = clamp01((f - i * 10.0F * 0.5F) / 200.0F);
               if (clamp012 < 1.0F) {
                  float f2 = clamp012 * clamp012;
                  customDrawContext.drawText(font, text, floatValue2 + f2 * 1.0F, centeredTextY - f2 * 5.0F, zenithColor.method7294(1.0F - clamp012));
               }
            }
         }

         floatValue2 += width;
      }
   }

   private static float method6126(float visibility) {
      float f = 1.9F;
      float f2 = f + 1.0F;
      float f3 = visibility - 1.0F;
      return 1.0F + f2 * f3 * f3 * f3 + f * f3 * f3;
   }

   private void method6127(CustomDrawContext customDrawContext, Font font, String text, float floatValue, float centeredTextY, float maxWidth, ZenithColor zenithColor, boolean flag) {
      float width = font.width(text);
      if (width <= maxWidth) {
         customDrawContext.drawText(font, text, floatValue, centeredTextY, zenithColor);
      } else if (flag) {
         float f = 18.0F;
         float f2 = width + f;
         long l = (long)(f2 / 22.0F * 1000.0F);
         long l2 = 900L;
         long l3 = System.currentTimeMillis() % (l + l2);
         float f3 = l3 < l2 ? 0.0F : (float)(l3 - l2) / (float)l * f2;
         customDrawContext.drawText(font, text, floatValue - f3, centeredTextY, zenithColor);
         customDrawContext.drawText(font, text, floatValue - f3 + f2, centeredTextY, zenithColor);
      } else {
         customDrawContext.drawText(font, MediaGlyphs.ellipsize(font, text, maxWidth), floatValue, centeredTextY, zenithColor);
      }
   }

   private void method6128(CustomDrawContext customDrawContext, MediaSessionManager mediaSessionManager, float floatValue, float y, long now, float f, boolean active) {
      boolean enabled = this.vinyl.isEnabled();
      float f2 = enabled && mediaSessionManager.method6007() ? 55.0F : 0.0F;
      this.floatValue3 = this.floatValue3 + (f2 - this.floatValue3) * Math.min(1.0F, f * (f2 > this.floatValue3 ? 2.2F : 3.5F));
      this.discRotation = (this.discRotation + this.floatValue3 * f) % 360.0F;
      Identifier texture = active ? mediaSessionManager.method6013() : null;
      Identifier id = mediaSessionManager.method6014();
      float f3 = Math.min(1.0F, (float)(now - mediaSessionManager.getLongValue4()) / 520.0F);
      float visibility = 1.0F - (1.0F - f3) * (1.0F - f3) * (1.0F - f3);
      float x = floatValue + 8.5F;
      float y2 = y + 8.5F;
      if (texture == null) {
         customDrawContext.drawRoundedRect(floatValue, y, 17.0F, 17.0F, ProxyDotRadiusData.method8069(enabled ? 8.5F : 3.0F), ZenithColor.INSTANCE.method7292(0.08F));
         MediaGlyphs.playPause(customDrawContext, x, y2, 7.0F, false, ZenithColor.INSTANCE.method7292(0.45F));
      } else {
         if (visibility < 1.0F && id != null) {
            this.method6129(customDrawContext, id, x, y2, 17.0F * (1.0F - 0.35F * visibility), 1.0F - visibility, enabled);
         }

         this.method6129(customDrawContext, texture, x, y2, 17.0F * (0.65F + 0.35F * method6126(visibility)), visibility, enabled);
         if (enabled) {
            float width = 3.6F;
            customDrawContext.drawArcBorder(x - 6.1200004F, y2 - 6.1200004F, 12.240001F, 12.240001F, 0.4F, 360.0F, 0.5F, ZenithColor.INSTANCE.method7292(0.08F));
            customDrawContext.drawRoundedRect(
               x - width / 2.0F,
               y2 - width / 2.0F,
               width,
               width,
               ProxyDotRadiusData.method8069(width / 2.0F),
               new ZenithColor(14, 14, 18).method7292(0.92F)
            );
            customDrawContext.drawArcBorder(
               x - width / 2.0F - 0.5F, y2 - width / 2.0F - 0.5F, width + 1.0F, width + 1.0F, 0.5F, 360.0F, 0.5F, ZenithColor.INSTANCE.method7292(0.3F)
            );
         }
      }
   }

   private void method6129(CustomDrawContext customDrawContext, Identifier texture, float x, float y, float size, float visibility, boolean enabled) {
      if (!(visibility <= 0.01F)) {
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(x, y);
         if (enabled) {
            customDrawContext.pose().rotate((float)Math.toRadians(this.discRotation));
         }

         customDrawContext.drawRoundedTexture(
            texture, -size / 2.0F, -size / 2.0F, size, size, ProxyDotRadiusData.method8069(enabled ? size / 4.0F : 1.5F), ZenithColor.INSTANCE.method7292(visibility)
         );
         customDrawContext.popMatrix();
      }
   }

   private void method6130(CustomDrawContext customDrawContext, float f, float f2, MediaSessionManager mediaSessionManager, float f3, ZenithColor color) {
      float f4 = MediaPlayerState.getInstance().method1672();
      boolean flag = mediaSessionManager.method6007();
      float f5;
      if (!flag) {
         f5 = 0.0F;
      } else if (f4 >= 0.0F) {
         double d = 20.0 * Math.log10(Math.max(f4, 1.0E-4F));
         f5 = (float)Math.max(0.0, Math.min(1.0, (d + 38.0) / 36.0));
      } else {
         f5 = 0.62F;
      }

      double d = System.currentTimeMillis() / 1000.0;
      float f6 = 9.0F;
      float width = 1.6F;
      float f7 = (8.0F - width * this.floatValues.length) / (this.floatValues.length - 1);
      float f8 = f2 + f6;
      ZenithColor zenithColor = color.method7295(ZenithColor.INSTANCE, 0.2F);

      for (int i = 0; i < this.floatValues.length; i++) {
         double d2 = 0.5 + 0.3 * Math.sin(d * (7.1 + i * 2.3) + i * 1.9) + 0.2 * Math.sin(d * (13.7 - i * 1.7) + i);
         float f9 = (float)(f5 * (0.35 + 0.65 * d2));
         float f10 = f9 > this.floatValues[i] ? 22.0F : 7.0F;
         this.floatValues[i] = this.floatValues[i] + (f9 - this.floatValues[i]) * Math.min(1.0F, f3 * f10);
         float height = 1.6F + (f6 - 1.6F) * clamp01(this.floatValues[i]);
         float floatValue = f + i * (width + f7);
         customDrawContext.drawRoundedRect(floatValue, f8 - height, width, height, ProxyDotRadiusData.method8069(0.8F), zenithColor.method7292(flag ? 0.95F : 0.4F));
      }
   }

   private boolean method6131(long now) {
      return MediaPlayerState.getInstance().method1673() && now - MediaPlayerState.getInstance().getChangedAt() < 1400L;
   }

   private void method6132(CustomDrawContext customDrawContext, float floatValue, float centeredTextY, long now, ZenithColor zenithColor, ZenithColor color) {
      float previewAlpha = Math.min(1.0F, (float)(1400L - (now - MediaPlayerState.getInstance().getChangedAt())) / 250.0F);
      float f = MediaPlayerState.getInstance().method1679();
      float width = 11.0F;
      Font font = Fonts.NEW_SEMIBOLD.getFont(4.5F);
      String text = String.valueOf(Math.round(f * 100.0F));
      customDrawContext.drawText(font, text, floatValue + (width - font.width(text)) / 2.0F, centeredTextY, zenithColor.method7294(previewAlpha));
      float y = centeredTextY + 8.0F;
      customDrawContext.drawRoundedRect(floatValue, y, width, 1.6F, ProxyDotRadiusData.method8069(0.8F), ZenithColor.INSTANCE.method7292(0.18F * previewAlpha));
      customDrawContext.drawRoundedRect(
         floatValue, y, Math.max(1.6F, width * f), 1.6F, ProxyDotRadiusData.method8069(0.8F), color.method7295(ZenithColor.INSTANCE, 0.2F).method7292(previewAlpha)
      );
   }

   private void method6133(
      CustomDrawContext customDrawContext,
      float floatValue,
      float y,
      double d,
      double d2,
      ZenithColor color,
      ZenithColor zenithColor,
      ZenithStyle currentStyle,
      boolean flag,
      boolean flag2
   ) {
      float seekPreview = d2 > 0.0 ? (float)Math.max(0.0, Math.min(1.0, d / d2)) : 0.0F;
      if (this.flag) {
         seekPreview = this.seekPreview;
      }

      if (flag2) {
         customDrawContext.drawRoundedRect(floatValue, y, 15.0F, 15.0F, ProxyDotRadiusData.method8069(7.5F), ZenithColor.INSTANCE.method7292(0.08F));
      }

      customDrawContext.drawArcBorder(floatValue, y, 15.0F, 15.0F, 1.1F, 360.0F, 0.5F, currentStyle.getFieldBorder().getZenithColor());
      if (seekPreview > 0.001F) {
         customDrawContext.drawArcBorder(floatValue, y, 15.0F, 15.0F, 1.1F, 360.0F * seekPreview, 0.5F, color.method7295(ZenithColor.INSTANCE, 0.15F));
      }

      MediaGlyphs.playPause(customDrawContext, floatValue + 7.5F, y + 7.5F, 5.6F, flag, flag2 ? zenithColor : zenithColor.method7294(0.85F));
   }

   private void method6134(
      CustomDrawContext customDrawContext,
      MediaSessionManager mediaSessionManager,
      float previewAlpha,
      ZenithColor color,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      float lastClickX,
      float lastClickY,
      double d,
      double d2
   ) {
      if (this.flag) {
         if (org.zenith.utility.misc.KeyCompat.isMouseDown(0)) {
            this.seekPreview = this.method6137(lastClickX);
         } else {
            this.flag = false;
            MediaSessionManager.getInstance().method6019(this.seekPreview * d2);
         }
      }

      if (previewAlpha <= 0.02F) {
         this.boundsData3 = this.boundsData4 = this.boundsData5 = this.playBounds = this.boundsData6 = this.boundsData7 = null;
      } else {
         float f = (1.0F - previewAlpha) * 4.0F;
         float floatValue = this.floatValue + 4.0F + 2.0F;
         float width = this.width - 8.0F - 4.0F;
         float y = this.floatValue2 + 25.0F + 3.0F - f;
         boolean inside = this.inside(lastClickX, lastClickY, floatValue, y - 3.0F, width, 8.0F);
         this.animation3.method2807(inside || this.flag);
         float height = 1.6F + 1.2F * this.animation3.getValue();
         this.boundsData3 = new BoundsData(floatValue, y, width, height);
         float seekPreview = this.flag ? this.seekPreview : (d2 > 0.0 ? clamp01((float)(d / d2)) : 0.0F);
         float y2 = y + (2.8F - height) / 2.0F;
         customDrawContext.drawRoundedRect(floatValue, y2, width, height, ProxyDotRadiusData.method8069(height / 2.0F), ZenithColor.INSTANCE.method7292(0.14F * previewAlpha));
         customDrawContext.drawRoundedRect(
            floatValue,
            y2,
            Math.max(height, width * seekPreview),
            height,
            ProxyDotRadiusData.method8069(height / 2.0F),
            color.method7295(ZenithColor.INSTANCE, 0.2F).method7294(previewAlpha)
         );
         if (this.animation3.getValue() > 0.02F) {
            float width2 = 4.0F * this.animation3.getValue();
            customDrawContext.drawRoundedRect(
               floatValue + width * seekPreview - width2 / 2.0F,
               y2 + height / 2.0F - width2 / 2.0F,
               width2,
               width2,
               ProxyDotRadiusData.method8069(width2 / 2.0F),
               zenithColor.method7294(previewAlpha)
            );
         }

         float y3 = y + 11.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
         double d3 = this.flag ? this.seekPreview * d2 : d;
         String text = "-" + MediaGlyphs.time(Math.max(0.0, d2 - d3));
         customDrawContext.drawText(font, MediaGlyphs.time(d3), floatValue, y3 - font.height() / 2.0F, zenithColor2.method7294(previewAlpha));
         customDrawContext.drawText(font, text, floatValue + width - font.width(text), y3 - font.height() / 2.0F, zenithColor2.method7294(previewAlpha));
         float f2 = 15.0F;
         float x = this.floatValue + this.width / 2.0F;
         this.boundsData4 = this.method6136(x - f2 * 2.0F, y3, 11.0F);
         this.boundsData5 = this.method6136(x - f2, y3, 11.0F);
         this.playBounds = this.method6136(x, y3, 13.0F);
         this.boundsData6 = this.method6136(x + f2, y3, 11.0F);
         this.boundsData7 = this.method6136(x + f2 * 2.0F, y3, 11.0F);
         MediaHelperProcess mediaHelperProcess = MediaHelperProcess.getInstance();
         ZenithColor zenithColor3 = color.method7295(ZenithColor.INSTANCE, 0.3F).method7294(previewAlpha);
         ZenithColor zenithColor4 = zenithColor.method7294(previewAlpha * 0.5F);
         MediaGlyphs.shuffle(customDrawContext, x - f2 * 2.0F, y3, 6.0F, this.method6135(mediaHelperProcess.isFlag() ? zenithColor3 : zenithColor4, this.boundsData4, lastClickX, lastClickY));
         MediaGlyphs.skip(customDrawContext, x - f2, y3, 7.0F, false, this.method6135(zenithColor.method7294(previewAlpha), this.boundsData5, lastClickX, lastClickY));
         MediaGlyphs.playPause(customDrawContext, x, y3, 8.0F, mediaSessionManager.method6007(), this.method6135(zenithColor.method7294(previewAlpha), this.playBounds, lastClickX, lastClickY));
         MediaGlyphs.skip(customDrawContext, x + f2, y3, 7.0F, true, this.method6135(zenithColor.method7294(previewAlpha), this.boundsData6, lastClickX, lastClickY));
         MediaGlyphs.repeat(
            customDrawContext,
            x + f2 * 2.0F,
            y3,
            6.0F,
            mediaHelperProcess.getIntValue() == 1,
            this.method6135(mediaHelperProcess.getIntValue() != 0 ? zenithColor3 : zenithColor4, this.boundsData7, lastClickX, lastClickY)
         );
      }
   }

   private ZenithColor method6135(ZenithColor zenithColor, BoundsData boundsData4, float lastClickX, float lastClickY) {
      return this.method6138(boundsData4, lastClickX, lastClickY) ? zenithColor.method7295(ZenithColor.INSTANCE.method7293(zenithColor.getIntValue4()), 0.5F) : zenithColor;
   }

   private BoundsData method6136(float x, float f, float width) {
      return new BoundsData(x - width / 2.0F, f - width / 2.0F, width, width);
   }

   private float method6137(float lastClickX) {
      return this.boundsData3 != null && !(this.boundsData3.width() <= 0.0F)
         ? clamp01((lastClickX - this.boundsData3.getFloatValue()) / this.boundsData3.width())
         : 0.0F;
   }

   private static float clamp01(float floatValue) {
      return Math.max(0.0F, Math.min(1.0F, floatValue));
   }

   private void clearBounds() {
      this.boundsData2 = null;
      this.boundsData = this.boundsData3 = this.boundsData4 = this.boundsData5 = this.playBounds = this.boundsData6 = this.boundsData7 = null;
   }

   private boolean method6138(BoundsData boundsData2, float lastClickX, float lastClickY) {
      return boundsData2 != null && boundsData2.contains(lastClickX, lastClickY);
   }

   private boolean inside(double lastClickX, double lastClickY, float floatValue, float f, float width, float height) {
      return lastClickX >= floatValue && lastClickX <= floatValue + width && lastClickY >= f && lastClickY <= f + height;
   }

   private static final class Entry {
      public String text = "";
      public String previous = "";
      public long changedAt;
      public boolean flag;
      public boolean flag2;

      public void method6139(String title, boolean flag, long changedAt) {
         String text = title == null ? "" : title;
         if (!text.equals(this.text)) {
            this.previous = this.text;
            this.flag2 = this.flag;
            this.text = text;
            this.changedAt = changedAt;
         }

         this.flag = flag;
      }
   }
}

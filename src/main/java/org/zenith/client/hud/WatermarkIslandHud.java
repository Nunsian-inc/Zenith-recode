package org.zenith.client.hud;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent.BossBarColor;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.misc.Data2;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.network.Island;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class WatermarkIslandHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 3.5F;
   private static final float CONST_floatValue3 = 13.0F;
   private static final float CONST_floatValue4 = 11.0F;
   private static final float CONST_floatValue5 = GuiStyle.PADDING.intValue() * 1.5F;
   private static final String text = "F";
   private static final String text2 = "H";
   private static final float CONST_floatValue6 = 15.0F;
   private static final float CONST_floatValue7 = 0.22F;
   private static final float CONST_floatValue8 = 7.0F;
   private static final float CONST_floatValue9 = 9.0F;
   private static final float CONST_floatValue10 = 5.0F;
   private static final float CONST_floatValue11 = 2.5F;
   private static final long CONST_longValue = 320L;
   private static final long CONST_longValue2 = 240L;
   private final BooleanSetting island = new BooleanSetting("module.interface.watermark.island", "module.interface.watermark.island.desc", true);
   private final Map<UUID, WatermarkIslandHud.Data> items = new HashMap<>();
   private final Animation animation = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(320L, 0.0F, Easing.INSTANCE4);
   private final Animation animation3 = new Animation(220L, 1.0F, Easing.INSTANCE2);
   private String text3;
   private Island.Entry entry;
   private static volatile long longValue;
   private static Object fitFont;
   private static float fitSize;
   private static float fitWidth;
   private static String text4;
   private static String text5;

   public static boolean method5668() {
      return method5669() && System.currentTimeMillis() - longValue < 500L;
   }

   public static boolean method5669() {
      if (InterfaceModule.INSTANCE.isEnabled() && mc.player != null && mc.level != null && !mc.gui.hud.isHidden()) {
         for (Interface interfaceValue : InterfaceModule.INSTANCE.method6794()) {
            if (interfaceValue instanceof WatermarkIslandHud watermarkIslandHud) {
               return watermarkIslandHud.isEnabled() && watermarkIslandHud.island.isEnabled() && watermarkIslandHud.method2061();
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static ZenithColor method5670(BossBarColor color) {
      return switch (color) {
         case PINK -> new ZenithColor(255, 95, 168, 255);
         case BLUE -> new ZenithColor(90, 169, 255, 255);
         case RED -> new ZenithColor(255, 90, 90, 255);
         case GREEN -> new ZenithColor(93, 214, 107, 255);
         case YELLOW -> new ZenithColor(255, 210, 90, 255);
         case PURPLE -> new ZenithColor(180, 107, 255, 255);
         case WHITE -> ZenithColor.INSTANCE;
      };
   }

   public WatermarkIslandHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font font = Fonts.NEW_ICONS.getFont(8.06F);
      Font font2 = Fonts.NEW_ICONS.getFont(5.0F);
      Font font3 = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font font4 = Fonts.NEW_REGULAR.getFont(5.4F);
      String text = "Zenith";
      String username = Zenith.getInstance().getEntry().getUsername();
      String str = String.valueOf(mc.getFps());
      String str2 = this.method5679();
      long now = System.currentTimeMillis();
      longValue = this.island.isEnabled() ? now : 0L;
      this.method5671();
      Island.Entry entry = this.island.isEnabled() ? Island.method2875(now) : null;
      if (!this.island.isEnabled()) {
         this.animation2.method2809(0.0F);
         this.animation.method2809(0.0F);
         this.animation3.method2809(1.0F);
         this.text3 = null;
         this.entry = null;
      }

      float floatValue = entry == null ? 0.0F : 1.0F;
      if (floatValue == 1.0F) {
         this.animation2.setEasing(Easing.INSTANCE4);
         this.animation2.setDuration(320L);
      } else {
         this.animation2.setEasing(Easing.INSTANCE14);
         this.animation2.setDuration(240L);
      }

      float f = this.animation2.method2808(floatValue);
      if (entry != null) {
         if (!entry.text.equals(this.text3)) {
            if (this.text3 != null) {
               this.animation3.method2809(0.0F);
            }

            float f2 = entry.method2884(now);
            this.animation.method2809(f2 < 0.0F ? 0.0F : f2);
            this.text3 = entry.text;
         }

         this.entry = entry;
      } else if (f < 0.01F) {
         this.text3 = null;
         this.entry = null;
      }

      Island.Entry entry2 = entry != null ? entry : this.entry;
      float f2 = entry != null ? this.animation3.method2808(1.0F) : 1.0F;
      this.width = 16.5F
         + this.dividerWidth()
         + font3.width(text)
         + this.dividerWidth()
         + 11.0F
         + GuiStyle.PADDING.intValue()
         + font3.width(username)
         + this.dividerWidth()
         + this.method5677(font2, font3, font4, "F", str, " fps")
         + this.dividerWidth()
         + this.method5677(font2, font3, font4, "H", str2, " ms")
         + CONST_floatValue5;
      this.height = 20.0F;
      float f3 = 15.0F * f;
      ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 10.0F));
      boolean flag = f3 > 0.5F && entry2 != null;
      float panelHeight = 20.0F + (flag ? f3 : 0.0F);
      if (flag) {
         float f4 = Math.min(InterfaceModule.method6801(), 7.5F);
         RenderShaders.floatValues = new float[]{
            this.method5672(), this.floatValue2 + 20.0F - 9.0F, this.method5673(), f3 + 9.0F, Math.min(f4, f3), this.floatValue2 + 20.0F, 5.0F
         };
      }

      boolean flag2 = (InterfaceModule.INSTANCE.method6817() || InterfaceModule.INSTANCE.method6818()) && RenderShaders.flag;
      boolean flag3 = InterfaceModule.INSTANCE.method6819();
      customDrawContext.drawBlurHudBooleanCheck(this.floatValue, this.floatValue2, this.width, panelHeight, 12.0F, moduleMenuRadius, ZenithColor.INSTANCE, flag2, false);
      customDrawContext.drawRoundedRect(
         this.floatValue,
         this.floatValue2,
         this.width,
         panelHeight,
         moduleMenuRadius,
         currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
      );
      RenderShaders.floatValues = null;
      if (flag3) {
         customDrawContext.drawBlurHudBooleanCheck(this.floatValue, this.floatValue2, this.width, 20.0F, 12.0F, moduleMenuRadius, ZenithColor.INSTANCE, false, true);
         if (flag && f3 > 2.0F) {
            customDrawContext.drawBlurHudBooleanCheck(
               this.method5672(),
               this.floatValue2 + 20.0F - 2.0F,
               this.method5673(),
               f3 + 2.0F,
               12.0F,
               ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 7.5F)),
               ZenithColor.INSTANCE,
               false,
               true
            );
         }
      }

      if (flag) {
         this.method5674(customDrawContext, currentStyle, entry2, f3, f, f2, now);
      }

      float floatValue2 = this.floatValue + 3.5F;
      float y = this.floatValue2 + 3.5F;
      customDrawContext.drawRoundedRect(floatValue2, y, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.9F), currentStyle.getPrimaryColor().getZenithColor());
      HudListLayout.method7637(customDrawContext, font, "1", floatValue2, y, 13.0F, ZenithColor.INSTANCE);
      floatValue2 += 13.0F;
      floatValue2 = this.method5676(customDrawContext, floatValue2, currentStyle);
      customDrawContext.drawText(font3, text, floatValue2, this.floatValue2 + (20.0F - font3.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor());
      floatValue2 += font3.width(text);
      floatValue2 = this.method5676(customDrawContext, floatValue2, currentStyle);
      float y2 = this.floatValue2 + 4.5F;
      customDrawContext.drawPlayerHeadWithRoundedShader(this.method5680(), floatValue2, y2, 11.0F, ProxyDotRadiusData.method8069(5.5F), ZenithColor.INSTANCE);
      customDrawContext.drawRoundedBorder(
         floatValue2 - 1.0F, y2 - 1.0F, 13.0F, 13.0F, 1.0F, ProxyDotRadiusData.method8069(6.5F), currentStyle.getPrimaryColor().getZenithColor().method7294(0.55F)
      );
      floatValue2 += 11.0F + GuiStyle.PADDING.intValue();
      customDrawContext.drawText(font3, username, floatValue2, this.floatValue2 + (20.0F - font3.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor());
      floatValue2 += font3.width(username);
      floatValue2 = this.method5678(customDrawContext, floatValue2, font2, font3, font4, "F", str, " fps", currentStyle);
      this.method5678(customDrawContext, floatValue2, font2, font3, font4, "H", str2, " ms", currentStyle);
   }

   private void method5671() {
      if (this.island.isEnabled() && mc.gui != null && mc.player != null && mc.level != null) {
         var events = mc.gui.hud.getBossOverlay().events;
         this.items.keySet().retainAll(events.keySet());
         LerpingBossEvent event = null;
         Data2 data2 = null;

         for (Map.Entry entry : events.entrySet()) {
            LerpingBossEvent event2 = (LerpingBossEvent)entry.getValue();
            String string = event2.getName().getString();
            WatermarkIslandHud.Data data = this.items.get(entry.getKey());
            if (data == null || !data.text().equals(string)) {
               data = new WatermarkIslandHud.Data(string, Data2.method1515(string));
               this.items.put((UUID)entry.getKey(), data);
            }

            Data2 data22 = data.getData2();
            if (event == null
               || data22.isFlag() && !data2.isFlag()
               || data22.isFlag() && data2.isFlag() && data22.getIntValue() > data2.getIntValue()
               || !data22.isFlag() && !data2.isFlag() && data22.getIntValue() >= 0 && data2.getIntValue() < 0) {
               event = event2;
               data2 = data22;
            }
         }

         if (event == null) {
            Island.drop("boss");
         } else {
            String title = data2.isFlag() ? Zenith.getInstance().getLocalization().translate("hud.island.combat") : data2.title();
            if (events.size() > 1) {
               title = title + "  +" + (events.size() - 1);
            }

            Island.method2872(title, method5670(event.getColor()), event.getProgress(), data2.value());
         }
      } else {
         this.items.clear();
         Island.drop("boss");
      }
   }

   private float method5672() {
      return this.floatValue + this.width * 0.22F / 2.0F;
   }

   private float method5673() {
      return this.width - this.width * 0.22F;
   }

   private void method5674(CustomDrawContext customDrawContext, ZenithStyle currentStyle, Island.Entry entry, float f, float f2, float f3, long now) {
      float f4 = this.method5672();
      float f5 = this.method5673();
      float f6 = this.floatValue2 + 20.0F;
      float previewAlpha = Math.clamp((f2 - 0.45F) / 0.55F, 0.0F, 1.0F) * Math.clamp(f3, 0.0F, 1.0F);
      if (!(previewAlpha <= 0.01F)) {
         float f7 = (1.0F - Math.clamp(f3, 0.0F, 1.0F)) * 3.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         if (entry.flag2) {
            this.method5675(customDrawContext, currentStyle, entry, font, f4, f5, f6, previewAlpha, f7);
         } else {
            Font font2 = Fonts.NEW_SEMIBOLD.getFont(5.0F);
            String text = entry.method2885(now);
            float width = font2.width(text);
            String str = fit(font, entry.title, f5 - 14.0F - width - CONST_floatValue5 * 2.0F);
            float centeredTextY = f6 + 2.0F + f7;
            customDrawContext.drawText(font, str, f4 + 7.0F, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
            if (!text.isEmpty()) {
               customDrawContext.drawText(font2, text, f4 + f5 - 7.0F - width, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            }

            float floatValue = entry.method2884(now);
            if (!(floatValue < 0.0F)) {
               float y = f6 + 15.0F - 2.5F - 3.0F;
               float floatValue2 = f4 + 7.0F;
               float width2 = f5 - 14.0F;
               ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(1.25F);
               customDrawContext.drawRoundedRect(floatValue2, y, width2, 2.5F, headerRadius, currentStyle.getHeaderHudBackground().getZenithColor().method7294(previewAlpha));
               float f8 = Math.clamp(this.animation.method2808(floatValue), 0.0F, 1.0F);
               if (f8 > 0.001F) {
                  ZenithColor zenithColor = currentStyle.getPrimaryColor()
                     .getZenithColor()
                     .method7295(currentStyle.getSecondaryPrimaryColor().getZenithColor(), Math.clamp(f8, 0.0F, 1.0F));
                  ZenithColor zenithColor2 = entry.flag && f8 < 0.2F ? zenithColor.method7295(new ZenithColor(255, 90, 74, 255), 1.0F - f8 / 0.2F) : zenithColor;
                  customDrawContext.drawRoundedRect(floatValue2, y, Math.max(2.5F, width2 * f8), 2.5F, headerRadius, zenithColor2.method7294(previewAlpha));
               }

               float f9 = Math.clamp(f3, 0.0F, 1.0F);
               if (f9 < 0.999F) {
                  float f10 = f5 * 0.28F;
                  float f11 = f4 - f10 + (f5 + f10) * f9;
                  float width3 = Math.min(f11 + f10, f4 + f5) - Math.max(f11, f4);
                  if (width3 > 0.5F) {
                     customDrawContext.drawRoundedRect(
                        Math.max(f11, f4),
                        f6 + 1.5F,
                        width3,
                        12.0F,
                        ProxyDotRadiusData.method8069(6.0F),
                        ZenithColor.INSTANCE.method7294(previewAlpha * 0.1F * (1.0F - f9))
                     );
                  }
               }
            }
         }
      }
   }

   private void method5675(CustomDrawContext customDrawContext, ZenithStyle currentStyle, Island.Entry entry, Font font, float f, float f2, float f3, float previewAlpha, float f4) {
      float f5 = f3 + 7.5F - 0.5F + f4;
      float width = 4.0F;
      ZenithColor zenithColor = entry.zenithColor == null ? currentStyle.getPrimaryColor().getZenithColor() : entry.zenithColor;
      customDrawContext.drawRoundedRect(f + 7.0F, f5 - width / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor.method7294(previewAlpha));
      boolean flag = !Float.isNaN(entry.floatValue2);
      float f6 = flag ? 11.0F : 0.0F;
      float floatValue = f + 7.0F + width + 4.0F;
      float f7 = f + f2 - 7.0F - f6 - (flag ? CONST_floatValue5 : 0.0F) - floatValue;
      String text = fit(font, entry.title, f7);
      customDrawContext.drawText(font, text, floatValue, f5 - font.height() / 2.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      if (flag) {
         Voice.method371(customDrawContext, f + f2 - 7.0F - f6, f5, entry.floatValue2, zenithColor.method7294(previewAlpha));
      }
   }

   private static String fit(Font font, String title, float f) {
      if (f <= 0.0F || font.width(title) <= f) {
         return title;
      } else if (text5 != null && font.getFont() == fitFont && font.getSize() == fitSize && f == fitWidth && title.equals(text4)) {
         return text5;
      } else {
         String str = title;

         while (str.length() > 1 && font.width(str + "...") > f) {
            str = str.substring(0, str.length() - 1);
         }

         fitFont = font.getFont();
         fitSize = font.getSize();
         fitWidth = f;
         text4 = title;
         text5 = str + "...";
         return text5;
      }
   }

   private float dividerWidth() {
      return CONST_floatValue5 * 2.0F + 1.0F;
   }

   private float method5676(CustomDrawContext customDrawContext, float floatValue, ZenithStyle currentStyle) {
      float height = 9.0F;
      customDrawContext.drawRect(floatValue + CONST_floatValue5, this.floatValue2 + (20.0F - height) / 2.0F, 1.0F, height, currentStyle.getTextTertiary().getZenithColor().method7294(0.45F));
      return floatValue + this.dividerWidth();
   }

   private float method5677(Font font, Font font2, Font font3, String text, String str, String text2) {
      return font.width(text) + 2.0F + font2.width(str) + font3.width(text2);
   }

   private float method5678(CustomDrawContext customDrawContext, float floatValue, Font font, Font font2, Font font3, String text, String str, String text2, ZenithStyle currentStyle) {
      floatValue = this.method5676(customDrawContext, floatValue, currentStyle);
      customDrawContext.drawText(font, text, floatValue, this.floatValue2 + (20.0F - font.height()) / 2.0F, currentStyle.getTextTertiary().getZenithColor());
      floatValue += font.width(text) + 2.0F;
      customDrawContext.drawText(font2, str, floatValue, this.floatValue2 + (20.0F - font2.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor());
      floatValue += font2.width(str);
      customDrawContext.drawText(font3, text2, floatValue, this.floatValue2 + (20.0F - font3.height()) / 2.0F, currentStyle.getTextTertiary().getZenithColor());
      return floatValue + font3.width(text2);
   }

   private String method5679() {
      if (mc.player != null && mc.getConnection() != null) {
         PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(mc.player.getUUID());
         return playerInfo == null ? "-" : String.valueOf(Math.max(0, playerInfo.getLatency()));
      } else {
         return "-";
      }
   }

   private Identifier method5680() {
      LocalPlayer player = mc.player;
      return player instanceof AbstractClientPlayer && player.getSkin() != null
         ? player.getSkin().body().texturePath()
         : DefaultPlayerSkin.getDefaultSkin().body().texturePath();
   }

   private static final class Data {
      private final String text;
      private final Data2 data2;

      public String text() {
         return this.text;
      }

      public Data2 data2() {
         return this.data2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.data2, other.data2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, data2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "data2=" + this.data2 + "]";
      }

      private Data(String text, Data2 data2) {
         this.text = text;
         this.data2 = data2;
      }

      public Data2 getData2() {
         return this.data2;
      }
   }
}

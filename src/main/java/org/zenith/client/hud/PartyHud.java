package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.PartyManager;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class PartyHud extends Interface {
   private static final String text = "P";
   private static final float CONST_floatValue = 24.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final float CONST_floatValue3 = 118.0F;
   private static final ZenithColor zenithColor = new ZenithColor(-495247);
   private static final ZenithColor zenithColor2 = new ZenithColor(-278748);
   private final Animation animation = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 118.0F, Easing.INSTANCE13);
   private final Map<Long, Animation> items = new HashMap<>();
   private final Map<String, PlayerInfo> items2 = new HashMap<>();
   private final Map<String, Long> items3 = new HashMap<>();
   private static final long CONST_longValue = 2000L;
   private long longValue;
   private final Map<String, Integer> items4 = new HashMap<>();

   public PartyHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   private List<PartyHud.Data> method6106(Cloud cloud) {
      ArrayList<PartyHud.Data> datas = new ArrayList<>();
      PartyMember.Data6 data6 = cloud == null ? null : cloud.getPartyMember().getData6();
      if (data6 == null) {
         return datas;
      } else {
         for (PartyMember.Data3 data3 : data6.getItems()) {
            if (data3.uid() != cloud.getLongValue()) {
               datas.add(
                  new PartyHud.Data(
                     data3.uid(),
                     data3.username(),
                     data3.getText2(),
                     data3.isFlag(),
                     data3.isFlag() ? cloud.state().method8040(data3.uid()) : null,
                     data6.method1215(data3.uid())
                  )
               );
            }
         }

         datas.sort((data, var1x) -> Boolean.compare(var1x.isFlag(), data.isFlag()));
         return datas;
      }
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      Cloud cloud = Zenith.getInstance().getCloud();
      List<PartyHud.Data> datas = this.method6106(cloud);
      long now = System.currentTimeMillis();
      if (now - this.longValue > 30000L) {
         this.items2.clear();
         this.items3.clear();
         this.longValue = now;
         if (this.items.size() > datas.size()) {
            HashSet hashSet = new HashSet();

            for (PartyHud.Data data : datas) {
               hashSet.add(data.uid());
            }

            this.items.keySet().retainAll(hashSet);
         }
      }

      PartyMember.Data6 data6 = cloud == null ? null : cloud.getPartyMember().getData6();
      String text = data6 == null ? "Party" : data6.name() + " · " + data6.method1217() + "/" + data6.getItems().size();
      Font nameFont = HudListLayout.nameFont();
      Font font = Fonts.NEW_REGULAR.getFont(4.6F);
      float f = HudListLayout.method7638("P", text);

      for (PartyHud.Data data : datas) {
         f = Math.max(f, 26.0F + nameFont.width(data.name()) + 34.0F + 6.0F);
      }

      float width = this.animation2.method2808(Math.max(118.0F, f));
      float height = 20.0F + GuiStyle.PADDING.intValue() / 2.0F + datas.size() * 24.0F + (datas.isEmpty() ? 0.0F : 2.0F);
      float x = this.animation.method2808(datas.isEmpty() && (!(mc.gui.screen() instanceof ChatScreen) || data6 == null) ? 0.0F : 1.0F);
      if (!(x <= 0.01F)) {
         this.width = width;
         this.height = height;
         float floatValue = this.floatValue;
         float y1 = this.floatValue2;
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + height / 2.0F);
         customDrawContext.pose().scale(x, x);
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + height / 2.0F));
         HudListLayout.method7635(customDrawContext, floatValue, y1, width, height);
         HudListLayout.method7636(customDrawContext, floatValue, y1, width, "P", text);
         float f2 = y1 + 20.0F + GuiStyle.PADDING.intValue() / 4.0F;

         for (PartyHud.Data data : datas) {
            this.method6107(customDrawContext, cloud, data, floatValue, f2, width, nameFont, font);
            f2 += 24.0F;
         }

         customDrawContext.popMatrix();
      }
   }

   private void method6107(CustomDrawContext customDrawContext, Cloud cloud, PartyHud.Data data, float floatValue, float f, float width, Font nameFont, Font font) {
      ZenithStyle style = HudListLayout.style();
      float previewAlpha = data.isFlag() ? 1.0F : 0.45F;
      float floatValue2 = floatValue + 6.0F;
      float y = f + 5.0F;
      customDrawContext.drawPlayerHeadWithRoundedShader(
         this.method6114(data.getText2()), floatValue2, y, 14.0F, ProxyDotRadiusData.method8069(4.2000003F), ZenithColor.INSTANCE.method7294(previewAlpha)
      );
      String text = "online";
      if (cloud != null && data.uid() > 0L && cloud.getPartyMember().method1142(data.uid())) {
         text = cloud.getPartyMember().method1141(data.uid()).getText();
      }

      float width2 = 4.0F;
      customDrawContext.drawRoundedRect(
         floatValue2 + 14.0F - width2 + 1.0F,
         y + 14.0F - width2 + 1.0F,
         width2,
         width2,
         ProxyDotRadiusData.method8069(width2 / 2.0F),
         data.isFlag() ? PartyManager.method51(text) : style.getTextTertiary().getZenithColor()
      );
      float floatValue3 = floatValue2 + 14.0F + 5.0F;
      float centeredTextY = f + 3.5F;
      String name = data.isFlag2() ? "★ " + data.name() : data.name();
      customDrawContext.drawText(nameFont, name, floatValue3, centeredTextY, style.getTextEnable().getZenithColor().method7294(previewAlpha));
      String str = this.method6109(cloud, data);
      float f2 = floatValue + width - 6.0F;
      if (!str.isEmpty()) {
         customDrawContext.drawText(font, str, f2 - font.width(str), centeredTextY + 0.5F, style.getTextTertiary().getZenithColor().method7294(previewAlpha));
         float angdeg = this.method6111(cloud, data);
         if (!Float.isNaN(angdeg)) {
            method6112(
               customDrawContext, f2 - font.width(str) - 6.0F, centeredTextY + font.height() / 2.0F + 0.5F, angdeg, style.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );
         }
      }

      Voice voice = Zenith.getInstance().getVoice();
      if (voice != null && data.uid() > 0L && voice.getVoiceState().method5365(data.uid())) {
         Voice.method371(
            customDrawContext,
            floatValue3 + nameFont.width(name) + 4.0F,
            centeredTextY + nameFont.height() / 2.0F,
            voice.getVoiceState().method5366(data.uid()),
            style.getPrimaryColor().getZenithColor()
         );
      }

      float y2 = f + 24.0F - 8.5F;
      float width3 = floatValue + width - 6.0F - floatValue3 - 26.0F;
      PartyPlayerInfo partyPlayerInfo = data.getPartyPlayerInfo();
      float f3 = partyPlayerInfo == null ? 0.0F : method6110(partyPlayerInfo.getFloatValue3(), 0.0F);
      float f4 = partyPlayerInfo == null ? 20.0F : Math.max(1.0F, method6110(partyPlayerInfo.getFloatValue4(), 20.0F));
      float f5 = partyPlayerInfo == null ? 0.0F : method6110(partyPlayerInfo.getFloatValue5(), 0.0F);
      float f6 = this.items
         .computeIfAbsent(data.uid(), l -> new Animation(260L, 0.0F, Easing.INSTANCE12))
         .method2808(Mth.clamp(f3 / f4, 0.0F, 1.0F));
      customDrawContext.drawRoundedRect(floatValue3, y2, width3, 3.0F, ProxyDotRadiusData.method8069(1.5F), style.getHeaderHudBackground().getZenithColor().method7294(previewAlpha));
      customDrawContext.drawRoundedRect(floatValue3, y2, width3 * f6, 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithColor.method7294(previewAlpha));
      if (f5 > 0.0F) {
         float f7 = Mth.clamp(f5 / f4, 0.0F, 1.0F);
         customDrawContext.drawRoundedRect(floatValue3, y2 - 1.5F, width3 * f7, 1.0F, ProxyDotRadiusData.method8069(0.5F), zenithColor2.method7294(previewAlpha));
      }

      String text2 = partyPlayerInfo == null ? (data.isFlag() ? "?" : "—") : String.valueOf(Math.round(f3 + f5));
      customDrawContext.drawText(font, text2, floatValue3 + width3 + 3.0F, y2 - font.height() / 2.0F + 1.5F, style.getTextSecondary().getZenithColor().method7294(previewAlpha));
      this.method6108(customDrawContext, partyPlayerInfo, floatValue + width - 6.0F - 16.0F, y2 - 2.5F, previewAlpha);
   }

   private void method6108(CustomDrawContext customDrawContext, PartyPlayerInfo partyPlayerInfo, float f, float y, float previewAlpha) {
      if (partyPlayerInfo != null) {
         CloudPackets.Data7[] data7s = partyPlayerInfo.getData7s();
         int n = 0;
         int n2 = 0;

         for (int i = 36; i <= 39 && i < data7s.length; i++) {
            CloudPackets.Data7 data7 = data7s[i];
            if (data7 != null && !data7.isEmpty()) {
               n++;
               if (this.items4.size() > 256 && !this.items4.containsKey(data7.getText())) {
                  this.items4.clear();
               }

               int n3 = this.items4.computeIfAbsent(data7.getText(), identifier -> {
                  Identifier var1x = Identifier.tryParse(identifier);
                  if (var1x != null && BuiltInRegistries.ITEM.containsKey(var1x)) {
                     ItemStack var2x = new ItemStack(BuiltInRegistries.ITEM.getValue(var1x));
                     return var2x.isDamageableItem() && var2x.getMaxDamage() > 0 ? var2x.getMaxDamage() : 0;
                  } else {
                     return 0;
                  }
               });
               if (n3 > 0 && data7.getIntValue2() > n3 * 0.75F) {
                  n2++;
               }
            }
         }

         ZenithStyle style = HudListLayout.style();

         for (int i = 0; i < 4; i++) {
            ZenithColor zenithColor2 = i < n
               ? (i < n2 ? zenithColor : style.getPrimaryColor().getZenithColor())
               : style.getHeaderHudBackground().getZenithColor();
            customDrawContext.drawRoundedRect(f + i * 4.0F, y, 3.0F, 3.0F, ProxyDotRadiusData.method8069(0.8F), zenithColor2.method7294(previewAlpha));
         }
      }
   }

   private String method6109(Cloud cloud, PartyHud.Data data) {
      PartyPlayerInfo partyPlayerInfo = data.getPartyPlayerInfo();
      if (cloud != null && partyPlayerInfo != null && mc.player != null) {
         if (!partyPlayerInfo.method3291(cloud.method7911(), cloud.method7913())) {
            String server = partyPlayerInfo.server();
            return server.isEmpty() ? "" : (server.startsWith("singleplayer") ? "SP" : method6113(server));
         } else {
            double d = partyPlayerInfo.getDoubleValue() - mc.player.getX();
            double d2 = partyPlayerInfo.getDoubleValue3() - mc.player.getZ();
            double d3 = partyPlayerInfo.getDoubleValue2() - mc.player.getY();
            double d4 = Math.sqrt(d * d + d3 * d3 + d2 * d2);
            return Double.isFinite(d4) && d4 < 1.0E8 ? Math.round(d4) + "m" : "";
         }
      } else {
         return data.isFlag() ? "" : method6115("hud.party.offline");
      }
   }

   private static float method6110(float f, float f2) {
      return Float.isFinite(f) ? f : f2;
   }

   private float method6111(Cloud cloud, PartyHud.Data data) {
      PartyPlayerInfo partyPlayerInfo = data.getPartyPlayerInfo();
      if (cloud != null && partyPlayerInfo != null && mc.player != null && partyPlayerInfo.method3291(cloud.method7911(), cloud.method7913())) {
         double x = partyPlayerInfo.getDoubleValue() - mc.player.getX();
         double y = partyPlayerInfo.getDoubleValue3() - mc.player.getZ();
         if (x * x + y * y < 4.0) {
            return Float.NaN;
         } else {
            float f = (float)Math.toDegrees(Math.atan2(y, x)) - 90.0F;
            return Mth.wrapDegrees(f - mc.player.getYRot());
         }
      } else {
         return Float.NaN;
      }
   }

   private static void method6112(CustomDrawContext customDrawContext, float x, float y, float angdeg, ZenithColor zenithColor) {
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().rotate((float)Math.toRadians(angdeg));

      for (byte i = -1; i <= 1; i += 2) {
         customDrawContext.pushMatrix();
         customDrawContext.pose().rotate((float)Math.toRadians(35 * i));
         customDrawContext.drawRect(-0.5F, -2.8F, 1.0F, 3.2F, zenithColor);
         customDrawContext.popMatrix();
      }

      customDrawContext.drawRect(-0.5F, -2.6F, 1.0F, 5.0F, zenithColor);
      customDrawContext.popMatrix();
   }

   private static String method6113(String server) {
      int indexOf = server.indexOf(46);
      String str = indexOf > 0 ? server.substring(0, indexOf) : server;
      return str.length() > 10 ? str.substring(0, 10) : str;
   }

   private Identifier method6114(String str) {
      String lowerCase = str == null ? "" : str.toLowerCase(Locale.ROOT);
      PlayerInfo playerInfo = this.items2.get(lowerCase);
      if (playerInfo != null && playerInfo.getSkin() != null) {
         return playerInfo.getSkin().body().texturePath();
      } else {
         long now = System.currentTimeMillis();
         Long l = this.items3.get(lowerCase);
         if (l != null && now - l < 2000L) {
            return DefaultPlayerSkin.getDefaultSkin().body().texturePath();
         } else {
            if (mc.getConnection() != null) {
               for (PlayerInfo onlinePlayer : mc.getConnection().getOnlinePlayers()) {
                  if (onlinePlayer.getProfile() != null && onlinePlayer.getProfile().name().equalsIgnoreCase(lowerCase) && onlinePlayer.getSkin() != null) {
                     this.items2.put(lowerCase, onlinePlayer);
                     this.items3.remove(lowerCase);
                     return onlinePlayer.getSkin().body().texturePath();
                  }
               }
            }

            this.items3.put(lowerCase, now);
            return DefaultPlayerSkin.getDefaultSkin().body().texturePath();
         }
      }
   }

   private static String method6115(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private static final class Data {
      private final long longValue;
      private final String text;
      private final String text2;
      private final boolean flag;
      private final PartyPlayerInfo partyPlayerInfo;
      private final boolean flag2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag() {
         return this.flag;
      }

      public PartyPlayerInfo partyPlayerInfo() {
         return this.partyPlayerInfo;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.partyPlayerInfo, other.partyPlayerInfo) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, flag, partyPlayerInfo, flag2);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "flag=" + this.flag + ", " + "partyPlayerInfo=" + this.partyPlayerInfo + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data(long longValue, String text, String str, boolean flag, PartyPlayerInfo partyPlayerInfo, boolean flag2) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = str;
         this.flag = flag;
         this.partyPlayerInfo = partyPlayerInfo;
         this.flag2 = flag2;
      }

      public long uid() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public PartyPlayerInfo getPartyPlayerInfo() {
         return this.partyPlayerInfo;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }
}

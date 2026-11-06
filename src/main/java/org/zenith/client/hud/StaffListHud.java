package org.zenith.client.hud;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.GameType;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class StaffListHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "P";
   private static final String text2 = "Staffs";
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Map<String, StaffListHud.Entry2> items = new LinkedHashMap<>();
   private final Set<String> items2 = Set.of("helper", "ᴀдмин", "moder", "staff", "admin", "curator", "стажёр", "сотрудник", "помощник", "админ", "модер");
   private final Map<String, PlayerInfo> items3 = new HashMap<>();
   private long longValue = 0L;
   private long longValue2 = 0L;
   private final Set<String> items4 = new HashSet<>();

   public StaffListHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      long now = System.currentTimeMillis();
      if (now - this.longValue > 1000L && mc.getConnection() != null) {
         this.method4481();
         this.longValue = now;
      }

      if (now - this.longValue2 > 30000L) {
         this.items3.clear();
         this.longValue2 = now;
      }

      this.items.entrySet().removeIf(entry -> entry.getValue().method4488());
      if (this.items.isEmpty()) {
         this.animation2.animateTo(0.0F);
      } else {
         StaffListHud.Entry2 entry2 = this.items.values().iterator().next();
         this.animation2.animateTo(this.items.size() == 1 && entry2.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
      }

      float floatValue = this.floatValue;
      float y1 = this.floatValue2;
      float height = (float)(
         20.0F + GuiStyle.PADDING.intValue() / 2.0F
            + this.items.values().stream().mapToDouble(entry2 -> (entry2.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry2.animation.getValue()).sum()
      );
      float width = (float)this.items.values().stream().mapToDouble(StaffListHud.Entry2::method4485).max().orElse(0.0);
      width = this.animation.animateTo(Math.max(HudListLayout.method7638("P", "Staffs"), width));
      this.width = width;
      this.height = height;
      float f = InterfaceModule.method6801();
      this.animation3.updateVisibility(mc.gui.screen() instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + height / 2.0F);
      customDrawContext.pose().scale(this.animation3.getValue(), this.animation3.getValue());
      customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + height / 2.0F));
      HudListLayout.method7635(customDrawContext, floatValue, y1, width, height);
      HudListLayout.method7636(customDrawContext, floatValue, y1, width, "P", "Staffs");
      if (this.animation3.getValue() == 1.0F) {
         float y12 = y1 + 20.0F;
         customDrawContext.enableScissor((int)floatValue, (int)y1, (int)(floatValue + width), (int)(y1 + height));

         for (Map.Entry entry : this.items.entrySet()) {
            StaffListHud.Entry2 entry2 = (StaffListHud.Entry2)entry.getValue();
            entry2.method4487(customDrawContext, floatValue, y12, width, this.items4.contains(entry.getKey()));
            y12 += (entry2.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry2.animation.getValue();
         }

         customDrawContext.disableScissor();
      }

      customDrawContext.popMatrix();
   }

   private void method4481() {
      if (mc.getConnection() != null) {
         this.items4.clear();

         for (PlayerInfo onlinePlayer : mc.getConnection().getOnlinePlayers()) {
            GameProfile profile = onlinePlayer.getProfile();
            Component tabListDisplayName = onlinePlayer.getTabListDisplayName();
            if (profile != null) {
               String name = profile.name();
               boolean flag = Zenith.getInstance().getStaffName().method1668(name);
               if (tabListDisplayName != null || flag) {
                  String string = tabListDisplayName != null ? tabListDisplayName.getString() : name;
                  String str = string.replace(name, "").trim();
                  if (flag || this.method4482(str) && str.length() >= 2) {
                     StaffListHud.Type type = onlinePlayer.getGameMode() == GameType.SPECTATOR ? StaffListHud.Type.VANISHED : StaffListHud.Type.NONE;
                     if (tabListDisplayName != null) {
                        tabListDisplayName = tabListDisplayName.getString().contains(profile.name()) ? TextUtils.method2118(tabListDisplayName, profile.name(), false) : TextUtils.method2108(tabListDisplayName, false);
                     } else {
                        tabListDisplayName = Component.nullToEmpty(name);
                     }

                     Component text = tabListDisplayName;
                     this.items.computeIfAbsent(string, var5x -> new StaffListHud.Entry2(text, string, name, type));
                     this.items4.add(string);
                  }
               }
            }
         }
      }
   }

   public boolean method4482(String str) {
      String lowerCase = str.toLowerCase(Locale.US);

      for (String str2 : this.items2) {
         if (lowerCase.contains(str2)) {
            return true;
         }
      }

      return false;
   }

   private String method4483(long l) {
      long l2 = l / 60000L;
      long l3 = l % 60000L / 1000L;
      return String.format("%d:%02d", l2, l3);
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   public static class Entry {
      private Component text;
      private String name;
      private boolean flag;
      private StaffListHud.Type type;

      @Generated
      public Component getText() {
         return this.text;
      }

      @Generated
      public String getName() {
         return this.name;
      }

      @Generated
      public boolean isFlag() {
         return this.flag;
      }

      @Generated
      public StaffListHud.Type getType() {
         return this.type;
      }

      @Generated
      public void setText(Component text) {
         this.text = text;
      }

      @Generated
      public void setName(String name) {
         this.name = name;
      }

      @Generated
      public void setFlag(boolean flag) {
         this.flag = flag;
      }

      @Generated
      public void setType(StaffListHud.Type type) {
         this.type = type;
      }

      @Generated
      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof StaffListHud.Entry entry)) {
            return false;
         } else if (!entry.method4484(this)) {
            return false;
         } else if (this.isFlag() != entry.isFlag()) {
            return false;
         } else {
            Component text = this.getText();
            Component component = entry.getText();
            if (text == null ? component == null : text.equals(component)) {
               String name = this.getName();
               String anObject = entry.getName();
               if (name == null ? anObject == null : name.equals(anObject)) {
                  StaffListHud.Type type = this.getType();
                  StaffListHud.Type type2 = entry.getType();
                  return type == null ? type2 == null : type.equals(type2);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      public boolean method4484(Object obj) {
         return obj instanceof StaffListHud.Entry;
      }

      @Generated
      @Override
      public int hashCode() {
         byte b = 59;
         int n = 1;
         n = n * 59 + (this.isFlag() ? 79 : 97);
         Component text = this.getText();
         n = n * 59 + (text == null ? 43 : text.hashCode());
         String name = this.getName();
         n = n * 59 + (name == null ? 43 : name.hashCode());
         StaffListHud.Type type = this.getType();
         return n * 59 + (type == null ? 43 : type.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "StaffComponent.Staff(prefix=" + this.getText() + ", name=" + this.getName() + ", isSpec=" + this.isFlag() + ", status=" + this.getType() + ")";
      }

      @Generated
      public Entry(Component text, String name, boolean flag, StaffListHud.Type type) {
         this.text = text;
         this.name = name;
         this.flag = flag;
         this.type = type;
      }
   }

   private class Entry2 {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private final Component text;
      private final String text2;
      private final String text3;
      private final StaffListHud.Type type;
      private final long longValue;
      private long longValue2 = Long.MIN_VALUE;
      private String text4;

      public Entry2(Component text, String string, String name, StaffListHud.Type type) {
         this.text = text;
         this.text2 = string;
         this.text3 = name;
         this.type = type;
         this.longValue = System.currentTimeMillis();
      }

      public float method4485() {
         return HudListLayout.method7645()
            + HudListLayout.nameFont().width(this.text)
            + HudListLayout.CONST_floatValue7
            + HudListLayout.method7647(this.method4486())
            + 6.0F;
      }

      private String method4486() {
         long l = System.currentTimeMillis() - this.longValue;
         long l2 = l / 1000L;
         if (this.text4 == null || l2 != this.longValue2) {
            this.longValue2 = l2;
            this.text4 = StaffListHud.this.method4483(l);
         }

         return this.text4;
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method4487(CustomDrawContext customDrawContext, float floatValue, float y1, float width, boolean contains) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font nameFont = HudListLayout.nameFont();
         this.animation.animateTo(contains ? 1.0F : 0.0F);
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + this.getHeight() / 2.0F);
         customDrawContext.pose().scale(this.animation.getValue(), this.animation.getValue());
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + this.getHeight() / 2.0F));
         Identifier texturePath = null;
         PlayerInfo playerInfo = StaffListHud.this.items3.get(this.text3);
         if (playerInfo == null && MinecraftAccess.mc.getConnection() != null) {
            playerInfo = MinecraftAccess.mc
               .getConnection()
               .getOnlinePlayers()
               .stream()
               .filter(var1x -> var1x.getProfile() != null && this.text3.equals(var1x.getProfile().name()))
               .findFirst()
               .orElse(null);
            if (playerInfo != null && playerInfo.getSkin() != null) {
               StaffListHud.this.items3.put(this.text3, playerInfo);
            }
         }

         if (playerInfo != null && playerInfo.getSkin() != null) {
            texturePath = playerInfo.getSkin().body().texturePath();
         }

         if (texturePath == null) {
            texturePath = DefaultPlayerSkin.getDefaultSkin().body().texturePath();
         }

         float x1 = HudListLayout.method7640(customDrawContext, floatValue, y1);
         customDrawContext.drawPlayerHeadWithRoundedShader(
            texturePath, HudListLayout.method7641(floatValue), HudListLayout.method7642(y1), 10.0F, ProxyDotRadiusData.method8069(3.0F), ZenithColor.INSTANCE
         );
         customDrawContext.drawText(nameFont, this.text, x1, y1 + (this.getHeight() - nameFont.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor().method7287());
         HudListLayout.method7646(customDrawContext, floatValue, y1, width, this.method4486());
         customDrawContext.popMatrix();
      }

      public boolean method4488() {
         return this.animation.getValue() == 0.0F;
      }
   }

   public static enum Type {
      NONE,
      VANISHED;
   }
}

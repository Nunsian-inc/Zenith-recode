package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.modules.impl.render.PartyMarksModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.PartyManager;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class PartyBlock implements MinecraftAccess {
   private static final float INSET = 8.0F;
   private static final float HEADER = 24.0F;
   private static final float SMALL_CHIP = 16.0F;
   private static final float AVATAR = 14.0F;
   private static final float ITEM = 16.0F;
   private static final long DEATH_WINDOW_MS = 1800000L;
   private static final ZenithColor DANGER = new ZenithColor(-495247);
   private static final ZenithColor LEADER = new ZenithColor(-278748);
   private static final ZenithColor DEATH = new ZenithColor(-1096636);
   private final FriendsSectionPanel host;
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Integer> hovered = new HashMap<>();
   private final Map<Long, Animation> avatarFade = new HashMap<>();
   private final Animation appear = new Animation(420L, 1.0F, Easing.INSTANCE12);
   private long member;
   private long mark;
   private long death;
   private boolean inviteOpen;

   public PartyBlock(FriendsSectionPanel host) {
      this.host = host;
   }

   public boolean hasSelection() {
      return this.member != 0L || this.mark != 0L || this.death != 0L;
   }

   public void clearSelection() {
      this.member = 0L;
      this.mark = 0L;
      this.death = 0L;
   }

   public String signature(Cloud cloud) {
      PartyMember.Data4 recentDeath = this.recentDeath(cloud);
      return this.inviteOpen + "|" + this.member + "|" + this.mark + "|" + this.death + "|" + (recentDeath == null ? 0L : recentDeath.getLongValue());
   }

   public void build(Cloud cloud) {
      if (cloud != null && cloud.method7860()) {
         PartyMember partyMember = cloud.getPartyMember();
         PartyMember.Data6 data6 = partyMember.getData6();
         if (data6 == null) {
            var invitable = this.invitable(cloud, null);
            if (!invitable.isEmpty()) {
               this.startCard(cloud, invitable);
            }
         } else {
            this.partyCard(cloud, data6);
         }

         PartyMember.Data4 recentDeath = this.recentDeath(cloud);
         if (recentDeath != null) {
            this.deathRow(cloud, recentDeath);
         }
      }
   }

   private List<PartyPlayerInfo> invitable(Cloud cloud, PartyMember.Data6 data6) {
      ArrayList<PartyPlayerInfo> partyPlayerInfos = new ArrayList<>();
      var iterator = cloud.state().method8038().iterator();

      while (true) {
         PartyPlayerInfo partyPlayerInfo;
         while (true) {
            if (!iterator.hasNext()) {
               partyPlayerInfos.sort((var1x, var2x) -> Boolean.compare(cloud.prefs().method3319(var2x.username()), cloud.prefs().method3319(var1x.username())));
               return partyPlayerInfos;
            }

            partyPlayerInfo = (PartyPlayerInfo)iterator.next();
            if (!cloud.prefs().method3316(partyPlayerInfo.username())) {
               if (data6 == null) {
                  break;
               }

               if (data6.method1216(partyPlayerInfo.uid()) == null) {
                  boolean flag = false;

                  for (PartyMember.Data8 data8 : data6.getItems2()) {
                     flag |= data8.uid() == partyPlayerInfo.uid();
                  }

                  if (!flag) {
                     break;
                  }
               }
            }
         }

         partyPlayerInfos.add(partyPlayerInfo);
      }
   }

   private void startCard(Cloud cloud, List<PartyPlayerInfo> invitable) {
      String key = "party:start";
      var subList = invitable.subList(0, Math.min(5, invitable.size()));
      this.host
         .custom(
            36.0F,
            (var3x, var4x, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
               previewAlpha *= this.appear.animateTo(1.0F);
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 2.0F;
               float height = 32.0F;
               plate(var3x, floatValue, y, width, height, 0.0F, previewAlpha, zenithStyle);
               var3x.drawRoundedRect(
                  floatValue, y, width, height, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.05F)
               );
               float height2 = 18.0F;
               float f4 = 4.0F;
               float f5 = subList.size() * (height2 + f4) - f4;
               float f6 = floatValue + width - 8.0F - f5;
               int n = -1;

               for (int i = 0; i < subList.size(); i++) {
                  PartyPlayerInfo partyPlayerInfo = (PartyPlayerInfo)subList.get(i);
                  float floatValue2 = f6 + i * (height2 + f4);
                  float f7 = y + (height - height2) / 2.0F;
                  boolean contains = new BoundsData(floatValue2 - 1.0F, f7 - 1.0F, height2 + 2.0F, height2 + 2.0F).contains(var4x, lastClickY);
                  if (contains) {
                     n = i;
                     HoverInfo.request(partyPlayerInfo.username(), t("gui.party.start.invite").replace("{n}", partyPlayerInfo.username()));
                  }

                  float hover = this.hover(key + i, contains);
                  float f8 = hover * 1.5F;
                  this.avatar(var3x, partyPlayerInfo.username(), partyPlayerInfo.uid(), floatValue2, f7 - f8, height2, previewAlpha, zenithStyle);
                  float width2 = 7.0F;
                  var3x.drawRoundedRect(
                     floatValue2 + height2 - width2 + 1.0F,
                     f7 + height2 - width2 + 1.0F - f8,
                     width2,
                     width2,
                     ProxyDotRadiusData.method8069(width2 / 2.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * (0.75F + 0.25F * hover))
                  );
                  var3x.drawRect(
                     floatValue2 + height2 - width2 / 2.0F + 1.0F - 0.5F,
                     f7 + height2 - width2 + 2.5F - f8,
                     1.0F,
                     width2 - 3.0F,
                     new ZenithColor(255, 255, 255).method7294(previewAlpha)
                  );
                  var3x.drawRect(
                     floatValue2 + height2 - width2 + 2.5F,
                     f7 + height2 - width2 / 2.0F + 1.0F - 0.5F - f8,
                     width2 - 3.0F,
                     1.0F,
                     new ZenithColor(255, 255, 255).method7294(previewAlpha)
                  );
               }

               this.hovered.put(key, n);
               Font font = Fonts.NEW_MEDIUM.getFont(5.6F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
               float floatValue2 = floatValue + 8.0F;
               float width2 = f6 - 8.0F - floatValue2;
               float centeredTextY = y + (height - font.height() - 3.0F - font2.height()) / 2.0F;
               var3x.drawText(font, fit(font, t("gui.party.start.title"), width2), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               var3x.drawText(
                  font2,
                  fit(font2, t("gui.party.start.note"), width2),
                  floatValue2,
                  centeredTextY + font.height() + 3.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
            },
            () -> {
               int var4x = this.hovered.getOrDefault(key, -1);
               if (var4x >= 0 && var4x < subList.size()) {
                  PartyPlayerInfo partyPlayerInfo = (PartyPlayerInfo)subList.get(var4x);
                  cloud.getPartyMember().method1153(partyPlayerInfo.uid());
                  this.host.partyNotice(t("gui.friends.party.invited").replace("{n}", partyPlayerInfo.username()));
                  this.host.invalidate();
               }
            },
            null
         );
   }

   private void partyCard(Cloud cloud, PartyMember.Data6 data6) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float f = this.host.mainWidth() - 16.0F;
      float f2 = 7.0F;
      float width = f - f2 * 2.0F;
      ArrayList arrayList = new ArrayList();
      ArrayList<PartyMember.Data3> data3s = new ArrayList();

      for (PartyMember.Data3 data3 : data6.getItems()) {
         if (data3.uid() != cloud.getLongValue()) {
            data3s.add(data3);
         }
      }

      data3s.sort((data3, var1x) -> Boolean.compare(var1x.isFlag(), data3.isFlag()));
      float floatValue = 0.0F;
      float y = 20.0F;
      float f3 = 4.0F;
      float height = 18.0F;
      float f4 = font2.width("9999 м") + 6.0F;

      for (PartyMember.Data3 data3 : data3s) {
         float width2 = 20.0F + font.width(data3.username()) + 4.0F + f4;
         if (floatValue > 0.0F && floatValue + width2 > width) {
            floatValue = 0.0F;
            y += height + f3;
         }

         arrayList.add(new PartyBlock.Item(PartyBlock.Kind.MEMBER, new BoundsData(floatValue, y, width2, height), data3));
         floatValue += width2 + f3;
      }

      for (PartyMember.Data8 data8 : data6.getItems2()) {
         String name = data8.name().isEmpty() ? cloud.getPartyMember().method1149(data8.uid()) : data8.name();
         float width2 = 20.0F + font.width(name) + 4.0F + font2.width("…") + 6.0F;
         if (floatValue > 0.0F && floatValue + width2 > width) {
            floatValue = 0.0F;
            y += height + f3;
         }

         arrayList.add(new PartyBlock.Item(PartyBlock.Kind.INVITED, new BoundsData(floatValue, y, width2, height), data8));
         floatValue += width2 + f3;
      }

      if (data6.getItems().size() < 6) {
         if (data3s.isEmpty() && data6.getItems2().isEmpty()) {
            float width2 = 18.0F + font.width(t("gui.party.chip.inviteFriends")) + 8.0F;
            arrayList.add(new PartyBlock.Item(PartyBlock.Kind.INVITE_ALL, new BoundsData(floatValue, y, width2, height), null));
            floatValue += width2 + f3;
         } else {
            if (floatValue > 0.0F && floatValue + height > width) {
               floatValue = 0.0F;
               y += height + f3;
            }

            arrayList.add(new PartyBlock.Item(PartyBlock.Kind.PLUS, new BoundsData(floatValue, y, height, height), null));
            floatValue += height + f3;
         }
      }

      y += height;
      if (this.inviteOpen) {
         List<PartyPlayerInfo> invitable = this.invitable(cloud, data6);
         y += 5.0F;
         floatValue = 0.0F;
         if (invitable.isEmpty()) {
            arrayList.add(new PartyBlock.Item(PartyBlock.Kind.HINT, new BoundsData(0.0F, y, width, 14.0F), t("gui.party.noFriendsOnline")));
            y += 14.0F;
         } else {
            for (PartyPlayerInfo partyPlayerInfo : invitable) {
               float width2 = 17.0F + font.width(partyPlayerInfo.username()) + 12.0F;
               if (floatValue > 0.0F && floatValue + width2 > width) {
                  floatValue = 0.0F;
                  y += 16.0F + f3;
               }

               arrayList.add(new PartyBlock.Item(PartyBlock.Kind.CANDIDATE, new BoundsData(floatValue, y, width2, 16.0F), partyPlayerInfo));
               floatValue += width2 + f3;
            }

            y += 16.0F;
         }
      }

      y += 7.0F;
      float y2 = y;
      floatValue = 0.0F;
      float height2 = 14.0F;
      String text = t("gui.party.marks.here");
      float width2 = 12.0F + font2.width(text) + 6.0F;
      arrayList.add(new PartyBlock.Item(PartyBlock.Kind.HERE, new BoundsData(floatValue, y, width2, height2), null));
      floatValue += width2 + f3;
      ArrayList<PartyMember.Data12> data12s = new ArrayList();
      String anObject = cloud.method7911();

      for (PartyMember.Data12 data12 : cloud.getPartyMember().method1138()) {
         if (data12.server().equals(anObject)) {
            data12s.add(data12);
         }
      }

      data12s.sort(
         (data12, var1x) -> Double.compare(
            distance(data12.getDoubleValue(), data12.getDoubleValue2(), data12.getDoubleValue3()),
            distance(var1x.getDoubleValue(), var1x.getDoubleValue2(), var1x.getDoubleValue3())
         )
      );
      int n = 1;

      for (PartyMember.Data12 data12 : data12s) {
         float width3 = 13.0F + font2.width(data12.name() + " · 9999 м") + 6.0F;
         if (floatValue + width3 > width) {
            if (n >= 2) {
               break;
            }

            n++;
            floatValue = 0.0F;
            y += height2 + f3;
         }

         arrayList.add(new PartyBlock.Item(PartyBlock.Kind.MARK, new BoundsData(floatValue, y, width3, height2), data12));
         floatValue += width3 + f3;
      }

      if (data12s.isEmpty()) {
         String str = markHint();
         if (!str.isEmpty()) {
            arrayList.add(new PartyBlock.Item(PartyBlock.Kind.HINT, new BoundsData(floatValue, y, width - floatValue, height2), str));
         }
      }

      y += height2;
      float height3 = y + f2;
      String key = "party:card";
      this.host
         .custom(
            height3 + 6.0F,
            (var11x, var12x, var13x, var14x, f5, var16x, var17x, var18x) -> {
               var17x *= this.appear.animateTo(1.0F);
               float var19x = var14x + 8.0F;
               float var20x = f5 + 3.0F;
               float width3 = var16x - 16.0F;
               var11x.drawRoundedRect(
                  var19x, var20x, width3, height3, ProxyDotRadiusData.method8069(6.0F), var18x.getDisableActiveBg().getZenithColor().method7294(var17x * 0.5F)
               );
               float var22x = var19x + f2;
               float var23x = var20x + f2 - 3.0F;
               Font var24x = Fonts.NEW_MEDIUM.getFont(5.6F);
               float var25x = var23x + 8.0F;
               MenuGlyph.FRIENDS.draw(var11x, var22x, var25x - 3.0F, var18x.getPrimaryColor().getZenithColor().method7294(var17x));
               String var26x = t("gui.party.strip.title");
               var11x.drawText(var24x, var26x, var22x + 11.0F, var25x - var24x.height() / 2.0F, var18x.getTextEnable().getZenithColor().method7294(var17x));
               var11x.drawText(
                  font2,
                  data6.method1217() + "/" + data6.getItems().size(),
                  var22x + 11.0F + var24x.width(var26x) + 4.0F,
                  var25x - font2.height() / 2.0F,
                  var18x.getTextTertiary().getZenithColor().method7294(var17x)
               );
               Voice var27x = Zenith.getInstance().getVoice();
               boolean var28x = var27x != null && var27x.isEnabled() && var27x.defaultChannel.getIndex() == 1;
               String var29x = t(var28x ? "gui.party.voice.on" : "gui.party.voice.off");
               String var30x = t("gui.party.leaveShort");
               Font var31x = Fonts.NEW_MEDIUM.getFont(4.5F);
               float var32x = 12.0F;
               float var33x = var19x + width3 - f2;
               BoundsData var34x = new BoundsData(var33x - (var31x.width(var30x) + 12.0F), var25x - var32x / 2.0F, var31x.width(var30x) + 12.0F, var32x);
               BoundsData var35x = new BoundsData(
                  var34x.getFloatValue() - 4.0F - (var31x.width(var29x) + 20.0F), var34x.getFloatValue2(), var31x.width(var29x) + 20.0F, var32x
               );
               int n2 = -1;
               if (var35x.contains(var12x, var13x)) {
                  n2 = 1001;
                  HoverInfo.request(t("gui.party.voice"), t("gui.party.voice.desc"));
               } else if (var34x.contains(var12x, var13x)) {
                  n2 = 1002;
                  HoverInfo.request(t("gui.party.leave"), t("gui.party.leave.desc"));
               }

               this.pill(var11x, key + ":v", var35x, var29x, var28x, n2 == 1001, var17x, var18x, null, 15.0F);
               MenuGlyph.MIC
                  .draw(
                     var11x,
                     var35x.getFloatValue() + 5.0F,
                     var35x.getFloatValue2() + (var32x - 6.0F) / 2.0F,
                     (var28x ? new ZenithColor(255, 255, 255) : var18x.getTextSecondary().getZenithColor()).method7294(var17x)
                  );
               this.pill(var11x, key + ":l", var34x, var30x, false, n2 == 1002, var17x, var18x, DANGER, 0.0F);
               var11x.drawRect(var22x, var23x + y2 - 4.0F, width3 - f2 * 2.0F, 0.5F, var18x.getTextTertiary().getZenithColor().method7294(var17x * 0.18F));

               for (int var37x = 0; var37x < arrayList.size(); var37x++) {
                  PartyBlock.Item var38x = (PartyBlock.Item)arrayList.get(var37x);
                  BoundsData var39x = new BoundsData(
                     var22x + var38x.area().getFloatValue(), var23x + var38x.area().getFloatValue2(), var38x.area().width(), var38x.area().height()
                  );
                  boolean var40x = var39x.contains(var12x, var13x) && var38x.kind() != PartyBlock.Kind.HINT;
                  if (var40x) {
                     n2 = var37x;
                  }

                  float hover = this.hover(key + var37x + var38x.kind(), var40x);
                  switch (var38x.kind()) {
                     case MEMBER:
                        this.memberPill(var11x, cloud, data6, (PartyMember.Data3)var38x.value(), var39x, hover, var40x, var17x, var18x);
                        break;
                     case INVITED:
                        this.invitedPill(var11x, cloud, (PartyMember.Data8)var38x.value(), var39x, var17x, var18x);
                        break;
                     case PLUS:
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x * (0.15F + 0.3F * hover + (this.inviteOpen ? 0.25F : 0.0F)))
                        );
                        cross(
                           var11x,
                           var39x.getFloatValue() + var39x.width() / 2.0F,
                           var39x.getFloatValue2() + var39x.height() / 2.0F,
                           7.0F,
                           this.inviteOpen ? 45.0F : 0.0F,
                           var18x.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), hover).method7294(var17x)
                        );
                        if (var40x) {
                           HoverInfo.request(t("gui.party.chip.invite"), t("gui.party.chip.invite.desc"));
                        }
                        break;
                     case INVITE_ALL:
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x * (0.18F + 0.3F * hover + (this.inviteOpen ? 0.2F : 0.0F)))
                        );
                        cross(
                           var11x,
                           var39x.getFloatValue() + 9.0F,
                           var39x.getFloatValue2() + var39x.height() / 2.0F,
                           6.0F,
                           this.inviteOpen ? 45.0F : 0.0F,
                           var18x.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), hover).method7294(var17x)
                        );
                        var11x.drawText(
                           font,
                           t("gui.party.chip.inviteFriends"),
                           var39x.getFloatValue() + 18.0F,
                           var39x.getFloatValue2() + (var39x.height() - font.height()) / 2.0F,
                           var18x.getTextEnable().getZenithColor().method7294(var17x)
                        );
                        break;
                     case CANDIDATE:
                        PartyPlayerInfo var42x = (PartyPlayerInfo)var38x.value();
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getDisableActiveBg()
                              .getZenithColor()
                              .method7295(var18x.getPrimaryColor().getZenithColor(), hover * 0.5F)
                              .method7294(var17x * (0.8F + 0.2F * hover))
                        );
                        this.avatar(
                           var11x, var42x.username(), var42x.uid(), var39x.getFloatValue() + 3.0F, var39x.getFloatValue2() + 3.0F, 10.0F, var17x, var18x
                        );
                        var11x.drawText(
                           font,
                           var42x.username(),
                           var39x.getFloatValue() + 17.0F,
                           var39x.getFloatValue2() + (var39x.height() - font.height()) / 2.0F,
                           var18x.getTextSecondary().getZenithColor().method7295(var18x.getTextEnable().getZenithColor(), hover).method7294(var17x)
                        );
                        cross(
                           var11x,
                           var39x.getFloatValue() + var39x.width() - 6.0F,
                           var39x.getFloatValue2() + var39x.height() / 2.0F,
                           5.0F,
                           0.0F,
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x)
                        );
                        if (var40x) {
                           HoverInfo.request(var42x.username(), t("gui.party.start.invite").replace("{n}", var42x.username()));
                        }
                        break;
                     case HERE:
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x * (0.2F + 0.4F * hover))
                        );
                        MenuGlyph.GPS
                           .draw(
                              var11x,
                              var39x.getFloatValue() + 3.0F,
                              var39x.getFloatValue2() + (var39x.height() - 6.0F) / 2.0F,
                              new ZenithColor(255, 255, 255).method7294(var17x)
                           );
                        var11x.drawText(
                           font2,
                           text,
                           var39x.getFloatValue() + 3.0F + 6.0F + 3.0F,
                           var39x.getFloatValue2() + (var39x.height() - font2.height()) / 2.0F,
                           new ZenithColor(255, 255, 255).method7294(var17x)
                        );
                        if (var40x) {
                           HoverInfo.request(t("gui.party.marks.here"), t("gui.party.marks.here.desc"));
                        }
                        break;
                     case MARK:
                        this.markChip(var11x, (PartyMember.Data12)var38x.value(), var39x, font2, hover, var40x, var17x, var18x);
                        break;
                     case HINT:
                        var11x.drawText(
                           font2,
                           fit(font2, (String)var38x.value(), var39x.width()),
                           var39x.getFloatValue(),
                           var39x.getFloatValue2() + (var39x.height() - font2.height()) / 2.0F,
                           var18x.getTextTertiary().getZenithColor().method7294(var17x * 0.8F)
                        );
                  }
               }

               this.hovered.put(key, n2);
            },
            () -> {
               int var4x = this.hovered.getOrDefault(key, -1);
               if (var4x == 1001) {
                  this.toggleVoice();
               } else if (var4x == 1002) {
                  cloud.getPartyMember().method1156();
                  this.clearSelection();
                  this.inviteOpen = false;
               } else if (var4x >= 0 && var4x < arrayList.size()) {
                  PartyBlock.Item var5x = (PartyBlock.Item)arrayList.get(var4x);
                  switch (var5x.kind()) {
                     case MEMBER:
                        PartyMember.Data3 var9x = (PartyMember.Data3)var5x.value();
                        this.clearSelection();
                        if (cloud.getFriends().method6442(var9x.username())) {
                           this.host.selectFriendFromParty(var9x.username());
                        } else {
                           this.member = var9x.uid();
                           this.host.partySelectionChanged();
                        }
                     case INVITED:
                     default:
                        break;
                     case PLUS:
                     case INVITE_ALL:
                        this.inviteOpen = !this.inviteOpen;
                        break;
                     case CANDIDATE:
                        PartyPlayerInfo var8x = (PartyPlayerInfo)var5x.value();
                        cloud.getPartyMember().method1153(var8x.uid());
                        this.host.partyNotice(t("gui.friends.party.invited").replace("{n}", var8x.username()));
                        break;
                     case HERE:
                        if (PartyMarksModule.INSTANCE.method5424()) {
                           PartyMarksModule.INSTANCE.setToggled(true);
                           this.host.partyNotice(t("gui.party.markPlacedShort"));
                        }
                        break;
                     case MARK:
                        PartyMember.Data12 var6x = (PartyMember.Data12)var5x.value();
                        boolean var7x = this.mark == var6x.getLongValue();
                        this.clearSelection();
                        this.mark = var7x ? 0L : var6x.getLongValue();
                        this.host.partySelectionChanged();
                  }
               }

               this.host.invalidate();
            },
            null,
            () -> {
               int var3x = this.hovered.getOrDefault(key, -1);
               if (var3x >= 0 && var3x < arrayList.size() && ((PartyBlock.Item)arrayList.get(var3x)).kind() == PartyBlock.Kind.MARK) {
                  PartyMember.Data12 var4x = (PartyMember.Data12)((PartyBlock.Item)arrayList.get(var3x)).value();
                  if (PartyMarksModule.INSTANCE.getLongValue5() == var4x.getLongValue()) {
                     PartyMarksModule.INSTANCE.clearFocus();
                  } else {
                     PartyMarksModule.INSTANCE.method5420(var4x.getLongValue());
                  }

                  this.host.invalidate();
               }
            }
         );
   }

   private void toggleVoice() {
      Voice voice = Zenith.getInstance().getVoice();
      if (voice != null) {
         boolean flag = voice.isEnabled() && voice.defaultChannel.getIndex() == 1;
         if (!voice.isEnabled()) {
            voice.setEnabled(true);
         }

         voice.defaultChannel.getValues().get(flag ? 0 : 1).method2103();
         this.host.partyNotice(t(flag ? "gui.party.voice.nowNear" : "gui.party.voice.nowParty"));
      }
   }

   private void memberPill(
      MenuDrawContext menuDrawContext, Cloud cloud, PartyMember.Data6 data6, PartyMember.Data3 data3, BoundsData boundsData, float hover, boolean flag, float previewAlpha, ZenithStyle zenithStyle
   ) {
      boolean flag2 = this.member == data3.uid() || data3.username().equals(this.host.selectedFriendName());
      float f = data3.isFlag() ? 1.0F : 0.5F;
      menuDrawContext.drawRoundedRect(
         boundsData.getFloatValue(),
         boundsData.getFloatValue2(),
         boundsData.width(),
         boundsData.height(),
         ProxyDotRadiusData.method8069(boundsData.height() / 2.0F),
         zenithStyle.getDisableActiveBg()
            .getZenithColor()
            .method7295(zenithStyle.getPrimaryColor().getZenithColor(), flag2 ? 0.3F : hover * 0.12F)
            .method7294(previewAlpha * (0.85F + 0.15F * hover))
      );
      PartyPlayerInfo partyPlayerInfo = data3.isFlag() ? cloud.state().method8040(data3.uid()) : null;
      Voice voice = Zenith.getInstance().getVoice();
      boolean flag3 = voice != null && voice.getVoiceState().method5365(data3.uid());
      float height = 12.0F;
      float floatValue = boundsData.getFloatValue() + 3.0F;
      float cardAvatarY = boundsData.getFloatValue2() + (boundsData.height() - height) / 2.0F;
      if (flag3) {
         menuDrawContext.drawRoundedRect(
            floatValue - 1.5F,
            cardAvatarY - 1.5F,
            height + 3.0F,
            height + 3.0F,
            ProxyDotRadiusData.method8069((height + 3.0F) / 2.0F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
         );
      }

      this.avatar(menuDrawContext, data3.username(), data3.uid(), floatValue, cardAvatarY, height, previewAlpha * f, zenithStyle);
      String text = cloud.getPartyMember().method1141(data3.uid()).getText();
      float width = 3.5F;
      menuDrawContext.drawRoundedRect(
         floatValue + height - width + 1.0F,
         cardAvatarY + height - width + 1.0F,
         width,
         width,
         ProxyDotRadiusData.method8069(width / 2.0F),
         (data3.isFlag() ? PartyManager.method51(text) : zenithStyle.getTextTertiary().getZenithColor()).method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float floatValue2 = floatValue + height + 4.0F;
      float centeredTextY = boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F - (partyPlayerInfo != null ? 1.0F : 0.0F);
      ZenithColor zenithColor = data6.method1215(data3.uid()) ? LEADER : zenithStyle.getTextEnable().getZenithColor();
      menuDrawContext.drawText(font, data3.username(), floatValue2, centeredTextY, zenithColor.method7294(previewAlpha * f));
      String str = partyPlayerInfo != null ? where(cloud, partyPlayerInfo) : (data3.isFlag() ? "" : t("hud.party.offline"));
      if (!str.isEmpty()) {
         menuDrawContext.drawText(
            font2,
            str,
            boundsData.getFloatValue() + boundsData.width() - 7.0F - font2.width(str),
            boundsData.getFloatValue2() + (boundsData.height() - font2.height()) / 2.0F - (partyPlayerInfo != null ? 1.0F : 0.0F),
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      }

      if (partyPlayerInfo != null) {
         float width2 = boundsData.getFloatValue() + boundsData.width() - 7.0F - floatValue2;
         float f2 = Mth.clamp(partyPlayerInfo.getFloatValue3() / Math.max(1.0F, partyPlayerInfo.getFloatValue4()), 0.0F, 1.0F);
         float y = boundsData.getFloatValue2() + boundsData.height() - 4.5F;
         menuDrawContext.drawRoundedRect(floatValue2, y, width2, 1.5F, ProxyDotRadiusData.method8069(0.75F), zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.25F));
         menuDrawContext.drawRoundedRect(floatValue2, y, width2 * f2, 1.5F, ProxyDotRadiusData.method8069(0.75F), DANGER.method7294(previewAlpha));
      }

      if (flag) {
         String description = data3.isFlag() ? PartyManager.method52(cloud.getPartyMember().method1141(data3.uid())) : t("gui.friends.status.offline");
         HoverInfo.request(data3.username() + (data6.method1215(data3.uid()) ? " · " + t("gui.party.role.leader") : ""), description);
      }
   }

   private void invitedPill(MenuDrawContext menuDrawContext, Cloud cloud, PartyMember.Data8 data8, BoundsData boundsData, float f, ZenithStyle zenithStyle) {
      String name = data8.name().isEmpty() ? cloud.getPartyMember().method1149(data8.uid()) : data8.name();
      float f2 = 0.55F + 0.45F * (float)Math.sin(System.currentTimeMillis() / 380.0);
      menuDrawContext.drawRoundedRect(
         boundsData.getFloatValue(),
         boundsData.getFloatValue2(),
         boundsData.width(),
         boundsData.height(),
         ProxyDotRadiusData.method8069(boundsData.height() / 2.0F),
         zenithStyle.getDisableActiveBg().getZenithColor().method7294(f * 0.5F)
      );
      menuDrawContext.drawRoundedBorder(
         boundsData.getFloatValue(),
         boundsData.getFloatValue2(),
         boundsData.width(),
         boundsData.height(),
         0.5F,
         ProxyDotRadiusData.method8069(boundsData.height() / 2.0F),
         zenithStyle.getPrimaryColor().getZenithColor().method7294(f * 0.5F * f2)
      );
      float height = 12.0F;
      this.avatar(menuDrawContext, name, data8.uid(), boundsData.getFloatValue() + 3.0F, boundsData.getFloatValue2() + (boundsData.height() - height) / 2.0F, height, f * 0.5F, zenithStyle);
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      menuDrawContext.drawText(
         font,
         name,
         boundsData.getFloatValue() + 3.0F + height + 4.0F,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7294(f * 0.8F)
      );
      menuDrawContext.drawText(
         font2,
         "…",
         boundsData.getFloatValue() + boundsData.width() - 7.0F - font2.width("…"),
         boundsData.getFloatValue2() + (boundsData.height() - font2.height()) / 2.0F,
         zenithStyle.getPrimaryColor().getZenithColor().method7294(f * f2)
      );
   }

   private void markChip(MenuDrawContext menuDrawContext, PartyMember.Data12 data12, BoundsData boundsData, Font font, float hover, boolean flag, float previewAlpha, ZenithStyle zenithStyle) {
      boolean flag2 = PartyMarksModule.INSTANCE.getLongValue5() == data12.getLongValue() || this.mark == data12.getLongValue();
      PartyMarksModule.Data2 data2 = PartyMarksModule.method5419(data12.getText4());
      ZenithColor zenithColor = new ZenithColor(data12.color() == -1 ? data2.color() : data12.color());
      menuDrawContext.drawRoundedRect(
         boundsData.getFloatValue(),
         boundsData.getFloatValue2(),
         boundsData.width(),
         boundsData.height(),
         ProxyDotRadiusData.method8069(boundsData.height() / 2.0F),
         zenithStyle.getDisableActiveBg().getZenithColor().method7295(zenithColor, flag2 ? 0.3F : hover * 0.15F).method7294(previewAlpha * 0.9F)
      );
      menuDrawContext.drawRoundedRect(
         boundsData.getFloatValue() + 4.0F,
         boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F,
         6.0F,
         6.0F,
         ProxyDotRadiusData.method8069(3.0F),
         zenithColor.method7294(previewAlpha)
      );
      String text = data12.name() + " · " + meters(distance(data12.getDoubleValue(), data12.getDoubleValue2(), data12.getDoubleValue3()));
      menuDrawContext.drawText(
         font,
         fit(font, text, boundsData.width() - 19.0F),
         boundsData.getFloatValue() + 13.0F,
         boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
         zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha)
      );
      if (flag) {
         HoverInfo.request(data12.name(), t("gui.party.mark.chip.desc"));
      }
   }

   private static void cross(MenuDrawContext menuDrawContext, float x, float y, float width, float angdeg, ZenithColor zenithColor) {
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(x, y);
      menuDrawContext.pose().rotate((float)Math.toRadians(angdeg));
      menuDrawContext.drawRect(-width / 2.0F, -0.5F, width, 1.0F, zenithColor);
      menuDrawContext.drawRect(-0.5F, -width / 2.0F, 1.0F, width, zenithColor);
      menuDrawContext.popMatrix();
   }

   private static String markHint() {
      int keyCode = CloudWaypointsModule.INSTANCE.getMarkBind().getKeyCode();
      return keyCode > 0 ? t("gui.party.marks.hint").replace("{k}", Voice.method364(CloudWaypointsModule.INSTANCE.getMarkBind())) : "";
   }

   private PartyMember.Data4 recentDeath(Cloud cloud) {
      if (cloud == null) {
         return null;
      } else {
         long l = System.currentTimeMillis() - 1800000L;

         for (PartyMember.Data4 data4 : cloud.getPartyMember().method1139()) {
            if (data4.time() >= l) {
               return data4;
            }
         }

         return null;
      }
   }

   private void deathRow(Cloud cloud, PartyMember.Data4 recentDeath) {
      String key = "party:death";
      boolean flag = recentDeath.uid() == cloud.getLongValue() || recentDeath.getLongValue() < 0L;
      this.host
         .custom(
            24.0F,
            (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
               previewAlpha *= this.appear.animateTo(1.0F);
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               BoundsData boundsData = new BoundsData(floatValue, f2 + 3.0F, width, 18.0F);
               boolean contains = boundsData.contains(lastClickX, lastClickY);
               this.hovered.put(key, contains ? 0 : -1);
               float hover = this.hover(key, contains);
               menuDrawContext.drawRoundedRect(
                  boundsData.getFloatValue(),
                  boundsData.getFloatValue2(),
                  boundsData.width(),
                  boundsData.height(),
                  ProxyDotRadiusData.method8069(4.0F),
                  DEATH.method7294(previewAlpha * (0.08F + 0.08F * hover + (this.death == recentDeath.getLongValue() ? 0.08F : 0.0F)))
               );
               float width2 = 10.0F;
               menuDrawContext.drawRoundedRect(
                  boundsData.getFloatValue() + 4.0F,
                  boundsData.getFloatValue2() + (boundsData.height() - width2) / 2.0F,
                  width2,
                  width2,
                  ProxyDotRadiusData.method8069(width2 / 2.0F),
                  DEATH.method7294(previewAlpha)
               );
               MenuGlyph.CLOSE
                  .draw(
                     menuDrawContext,
                     boundsData.getFloatValue() + 4.0F + (width2 - 6.0F) / 2.0F,
                     boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F,
                     new ZenithColor(255, 255, 255).method7294(previewAlpha)
                  );
               Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
               String text = recentDeath.getItems().size()
                  + " "
                  + t("gui.party.deaths.items")
                  + " · "
                  + (
                     sameWorld(cloud, recentDeath.server(), recentDeath.dimension())
                        ? meters(distance(recentDeath.getDoubleValue(), recentDeath.getDoubleValue2(), recentDeath.getDoubleValue3()))
                        : ServerNameUtils.display(recentDeath.server())
                  );
               float width3 = font2.width(text);
               menuDrawContext.drawText(
                  font2,
                  text,
                  boundsData.getFloatValue() + boundsData.width() - 6.0F - width3,
                  boundsData.getFloatValue2() + (boundsData.height() - font2.height()) / 2.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               String str = (flag ? t("gui.party.death.yours") : t("gui.party.death.mate").replace("{n}", recentDeath.name())) + " · " + ago(recentDeath.time());
               menuDrawContext.drawText(
                  font,
                  fit(font, str, boundsData.width() - 30.0F - width3),
                  boundsData.getFloatValue() + 18.0F,
                  boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
                  zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
               );
               if (contains) {
                  HoverInfo.request(t("gui.party.death.row"), recentDeath.getText4().isEmpty() ? t("gui.party.death.row.desc") : recentDeath.getText4());
               }
            },
            () -> {
               if (this.hovered.getOrDefault(key, -1) == 0) {
                  boolean var3x = this.death == recentDeath.getLongValue();
                  this.clearSelection();
                  this.death = var3x ? 0L : recentDeath.getLongValue();
                  this.host.partySelectionChanged();
                  this.host.invalidate();
               }
            },
            null
         );
   }

   public void buildAside(Cloud cloud) {
      if (cloud != null) {
         PartyMember partyMember = cloud.getPartyMember();
         if (this.member != 0L) {
            PartyMember.Data6 data6 = partyMember.getData6();
            PartyMember.Data3 data3 = data6 == null ? null : data6.method1216(this.member);
            if (data3 == null) {
               this.member = 0L;
            } else {
               PartyPlayerInfo partyPlayerInfo = data3.isFlag() ? cloud.state().method8040(data3.uid()) : null;
               this.host.asideCaption(data3.username());
               this.host
                  .asideInfo(
                     "gui.friends.status",
                     () -> !data3.isFlag() ? t("gui.friends.status.offline") : PartyManager.method52(partyMember.method1141(data3.uid())),
                     "gui.friends.presence.desc"
                  );
               if (partyPlayerInfo != null) {
                  this.host.asideInfo("gui.friends.playingOn", () -> {
                     String var3x = ServerNameUtils.method5643(partyPlayerInfo.server(), partyMember.method1141(data3.uid()).mode());
                     return var3x.isEmpty() ? "-" : var3x;
                  }, "gui.friends.playingOn.desc");
                  this.host
                     .asideInfo(
                        "gui.friends.health",
                        () -> Math.round(partyPlayerInfo.getFloatValue3() + partyPlayerInfo.getFloatValue5()) + " / " + Math.round(partyPlayerInfo.getFloatValue4()),
                        "gui.party.hp.desc"
                     );
                  if (partyPlayerInfo.method3291(cloud.method7911(), cloud.method7913())) {
                     this.host
                        .asideInfo(
                           "gui.friends.distance",
                           () -> meters(distance(partyPlayerInfo.getDoubleValue(), partyPlayerInfo.getDoubleValue2(), partyPlayerInfo.getDoubleValue3())),
                           "gui.friends.distance.desc"
                        );
                  }
               }

               this.host.asideInfo("gui.friends.mcName", data3::getText2, "gui.friends.mcName.desc");
               this.host.asideHint(t("gui.party.notFriend"));
               this.leaderActions(cloud, data3.uid());
            }
         } else if (this.mark != 0L) {
            PartyMember.Data12 data12 = null;

            for (PartyMember.Data12 data122 : partyMember.method1138()) {
               if (data122.getLongValue() == this.mark) {
                  data12 = data122;
               }
            }

            if (data12 == null) {
               this.mark = 0L;
            } else {
               PartyMember.Data12 data122 = data12;
               this.host.asideCaption(data122.name());
               this.host
                  .asideInfo(
                     "gui.party.mark.coords",
                     () -> coords(data122.getDoubleValue(), data122.getDoubleValue2(), data122.getDoubleValue3()),
                     "gui.party.mark.coords.desc"
                  );
               this.host.asideInfo("gui.party.mark.dim", () -> dimension(data122.dimension()), "gui.party.mark.dim.desc");
               this.host.asideInfo("gui.party.mark.author", data122::getText5, "gui.party.mark.author.desc");
               this.host.asideInfo("gui.party.mark.time", () -> ago(data122.time()), "gui.party.mark.time.desc");
               boolean flag = PartyMarksModule.INSTANCE.getLongValue5() == data122.getLongValue();
               this.host.asideAction(flag ? "gui.party.unfocus" : "gui.party.focus", MenuGlyph.GPS, () -> "", () -> {
                  if (PartyMarksModule.INSTANCE.getLongValue5() == data122.getLongValue()) {
                     PartyMarksModule.INSTANCE.clearFocus();
                  } else {
                     PartyMarksModule.INSTANCE.method5420(data122.getLongValue());
                  }

                  this.host.invalidate();
               }, "gui.party.focus.desc");
               this.host.asideAction("gui.party.copyCoords", MenuGlyph.COPY, () -> "", () -> {
                  copy(coords(data122.getDoubleValue(), data122.getDoubleValue2(), data122.getDoubleValue3()));
                  this.host.partyNotice(t("gui.friends.copied"));
               }, "gui.party.copyCoords.desc");
               PartyMember.Data6 data6 = partyMember.getData6();
               if (data122.getLongValue2() == cloud.getLongValue() || data6 != null && data6.method1215(cloud.getLongValue())) {
                  this.host.asideAction("gui.party.removeMark", MenuGlyph.CLOSE, () -> "", () -> {
                     partyMember.method1161(data122.getLongValue());
                     this.mark = 0L;
                     this.host.invalidate();
                  }, "gui.party.removeMark.desc");
               }
            }
         } else {
            if (this.death != 0L) {
               PartyMember.Data4 data4 = partyMember.method1140(this.death);
               if (data4 == null) {
                  this.death = 0L;
                  return;
               }

               boolean flag = data4.uid() == cloud.getLongValue() || data4.getLongValue() < 0L;
               this.host.asideCaption(flag ? t("cloud.death.mine") : data4.name());
               this.host.asideInfo("gui.party.death.cause", () -> data4.getText4().isEmpty() ? "-" : data4.getText4(), "gui.party.death.cause.desc");
               this.host.asideInfo("gui.party.death.when", () -> ago(data4.time()), "gui.party.death.when.desc");
               this.host
                  .asideInfo(
                     "gui.party.mark.coords", () -> coords(data4.getDoubleValue(), data4.getDoubleValue2(), data4.getDoubleValue3()), "gui.party.mark.coords.desc"
                  );
               this.host
                  .asideInfo(
                     "gui.party.mark.server", () -> ServerNameUtils.display(data4.server()) + " · " + dimension(data4.dimension()), "gui.party.mark.server.desc"
                  );
               if (!data4.getItems().isEmpty()) {
                  this.host.asideCaption(t("gui.party.death.loot") + " · " + data4.getItems().size());
                  this.itemGrid(data4.getItems());
               }

               boolean flag2 = PartyMarksModule.INSTANCE.getLongValue22() == data4.getLongValue();
               this.host.asideAction(flag2 ? "gui.party.unfocus" : "gui.party.focus", MenuGlyph.GPS, () -> "", () -> {
                  if (PartyMarksModule.INSTANCE.getLongValue22() == data4.getLongValue()) {
                     PartyMarksModule.INSTANCE.clearFocus();
                  } else {
                     PartyMarksModule.INSTANCE.method5421(data4.getLongValue());
                  }

                  this.host.invalidate();
               }, "gui.party.focus.desc");
               this.host.asideAction("gui.party.copyCoords", MenuGlyph.COPY, () -> "", () -> {
                  copy(coords(data4.getDoubleValue(), data4.getDoubleValue2(), data4.getDoubleValue3()));
                  this.host.partyNotice(t("gui.friends.copied"));
               }, "gui.party.copyCoords.desc");
               this.host.asideAction("gui.party.forgetDeath", MenuGlyph.CLOSE, () -> "", () -> {
                  partyMember.method1164(data4.getLongValue());
                  this.death = 0L;
                  this.host.invalidate();
               }, "gui.party.forgetDeath.desc");
            }
         }
      }
   }

   public void leaderActions(Cloud cloud, long uid) {
      PartyMember.Data6 data6 = cloud.getPartyMember().getData6();
      if (data6 != null && data6.method1216(uid) != null && uid != cloud.getLongValue()) {
         this.host.asideCaption("gui.party.aside.caption");
         if (data6.method1215(cloud.getLongValue())) {
            this.host.asideAction("gui.party.promote", MenuGlyph.PIN, () -> "", () -> {
               cloud.getPartyMember().method1158(uid);
               this.host.invalidate();
            }, "gui.party.promote.desc");
            this.host.asideAction("gui.party.kick", MenuGlyph.CLOSE, () -> "", () -> {
               cloud.getPartyMember().method1157(uid);
               this.member = 0L;
               this.host.invalidate();
            }, "gui.party.kick.desc");
         } else {
            this.host.asideInfo("gui.party.role", () -> t(data6.method1215(uid) ? "gui.party.role.leader" : "gui.party.role.member"), "gui.party.role.desc");
         }
      }
   }

   private void itemGrid(List<PartyMember.Data11> items) {
      float f = this.host.asideWidth() - 16.0F;
      int n = Math.max(1, (int)((f + 2.0F) / 19.0F));
      int n2 = (items.size() + n - 1) / n;
      var arrayList = new ArrayList<>(items.size());

      for (PartyMember.Data11 data11 : items) {
         Identifier tryParse = Identifier.tryParse(data11.getText());
         ItemStack stack = tryParse == null ? ItemStack.EMPTY : new ItemStack(BuiltInRegistries.ITEM.getValue(tryParse), Math.max(1, data11.count()));
         if (!stack.isEmpty() && stack.isDamageableItem()) {
            stack.setDamageValue(Math.min(data11.getIntValue2(), stack.getMaxDamage()));
         }

         if (!stack.isEmpty() && data11.isFlag()) {
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
         }

         arrayList.add(stack);
      }

      this.host
         .asideCustom(
            n2 * 19.0F + 4.0F,
            (var2x, var3x, var4x, var5x, f2, var7x, var8x, var9x) -> {
               for (int i = 0; i < arrayList.size(); i++) {
                  float x = var5x + 8.0F + i % n * 19.0F;
                  float y = f2 + 2.0F + i / n * 19.0F;
                  var2x.drawRoundedRect(
                     x - 1.0F,
                     y - 1.0F,
                     18.0F,
                     18.0F,
                     ProxyDotRadiusData.method8069(3.0F),
                     var9x.getDisableActiveBg().getZenithColor().method7294(var8x * 0.6F)
                  );
                  ItemStack itemStack = (ItemStack)arrayList.get(i);
                  if (!itemStack.isEmpty()) {
                     var2x.pose().pushMatrix();
                     var2x.pose().translate(x, y);
                     var2x.item(itemStack, 0, 0);
                     var2x.renderItemBar(itemStack, 0, 0);
                     if (itemStack.getCount() > 1) {
                        Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
                        String text = String.valueOf(itemStack.getCount());
                        var2x.drawText(font, text, 16.0F - font.width(text), 16.0F - font.height(), new ZenithColor(255, 255, 255).method7294(var8x));
                     }

                     var2x.pose().popMatrix();
                     if (new BoundsData(x, y, 16.0F, 16.0F).contains(var3x, var4x)) {
                        HoverInfo.request(itemStack.getHoverName().getString(), itemStack.getCount() + " ×");
                     }
                  }
               }
            }
         );
   }

   private static String where(Cloud cloud, PartyPlayerInfo partyPlayerInfo) {
      if (partyPlayerInfo.method3291(cloud.method7911(), cloud.method7913())) {
         return meters(distance(partyPlayerInfo.getDoubleValue(), partyPlayerInfo.getDoubleValue2(), partyPlayerInfo.getDoubleValue3()));
      } else {
         String server = partyPlayerInfo.server();
         if (server.isEmpty()) {
            return t("cloud.presence.menu");
         } else {
            String display = ServerNameUtils.display(server);
            return display.length() > 12 ? display.substring(0, 12) : display;
         }
      }
   }

   private static boolean sameWorld(Cloud cloud, String server, String dimension) {
      return mc.player != null && server.equals(cloud.method7911()) && dimension.equals(cloud.method7913());
   }

   private void avatar(MenuDrawContext menuDrawContext, String username, long uid, float floatValue, float cardAvatarY, float height, float previewAlpha, ZenithStyle zenithStyle) {
      Identifier texture = uid > 0L ? CloudAvatarCache.get(Long.toString(uid)) : null;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(height / 2.0F);
      if (texture != null) {
         float f = this.avatarFade.computeIfAbsent(uid, l -> new Animation(220L, 0.0F, Easing.INSTANCE12)).animateTo(1.0F);
         menuDrawContext.drawRoundedTexture(texture, floatValue, cardAvatarY, height, height, headerRadius, ZenithColor.INSTANCE.method7294(previewAlpha * f));
      } else {
         menuDrawContext.drawRoundedRect(floatValue, cardAvatarY, height, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 2.4F));
         String upperCase = username != null && !username.isEmpty() ? username.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         Font font = Fonts.NEW_MEDIUM.getFont(height * 0.5F);
         menuDrawContext.drawText(
            font,
            upperCase,
            floatValue + (height - font.width(upperCase)) / 2.0F,
            cardAvatarY + (height - font.height()) / 2.0F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   private void pill(
      MenuDrawContext menuDrawContext, String key, BoundsData boundsData, String text, boolean flag, boolean contains, float previewAlpha, ZenithStyle zenithStyle, ZenithColor zenithColor, float f
   ) {
      float hover = this.hover(key, contains);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(boundsData.height() / 2.0F);
      if (flag) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            headerRadius,
            zenithStyle.getPrimaryColor().getZenithColor().method7292(0.75F + hover * 0.25F).method7294(previewAlpha)
         );
      } else if (zenithColor != null) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, zenithColor.method7292(0.1F + hover * 0.2F).method7294(previewAlpha)
         );
      } else {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            headerRadius,
            zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (0.8F + hover * 0.6F))
         );
      }

      Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
      ZenithColor zenithColor2 = flag
         ? new ZenithColor(255, 255, 255).method7294(previewAlpha)
         : (
            zenithColor != null
               ? zenithColor.method7295(new ZenithColor(255, 255, 255), hover * 0.4F).method7294(previewAlpha)
               : zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha)
         );
      float floatValue = f > 0.0F ? boundsData.getFloatValue() + f : boundsData.getFloatValue() + (boundsData.width() - font.width(text)) / 2.0F;
      menuDrawContext.drawText(font, text, floatValue, boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F, zenithColor2);
   }

   private static void plate(MenuDrawContext menuDrawContext, float floatValue, float y, float width, float height, float f, float previewAlpha, ZenithStyle zenithStyle) {
      menuDrawContext.drawRoundedRect(
         floatValue, y, width, height, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (0.5F + 0.25F * f))
      );
   }

   private static void diamond(MenuDrawContext menuDrawContext, float x, float y, float width, ZenithColor zenithColor) {
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(x, y);
      menuDrawContext.pose().rotate((float)Math.toRadians(45.0));
      menuDrawContext.drawRect(-width / 2.0F, -width / 2.0F, width, width, zenithColor);
      menuDrawContext.popMatrix();
   }

   private float hover(String key, boolean contains) {
      return this.hover.computeIfAbsent(key, str -> new Animation(120L, 0.0F, Easing.INSTANCE12)).animateTo(contains ? 1.0F : 0.0F);
   }

   private static String fit(Font font, String text, float width) {
      if (!(width <= 0.0F) && text != null) {
         if (font.width(text) <= width) {
            return text;
         } else {
            int length = text.length();

            while (length > 0 && font.width(text.substring(0, length) + "…") > width) {
               length--;
            }

            return length == 0 ? "" : text.substring(0, length) + "…";
         }
      } else {
         return "";
      }
   }

   private static double distance(double doubleValue, double d, double d2) {
      if (mc.player == null) {
         return -1.0;
      } else {
         double d3 = doubleValue - mc.player.getX();
         double d4 = d - mc.player.getY();
         double d5 = d2 - mc.player.getZ();
         return Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
      }
   }

   private static String meters(double distance) {
      if (distance < 0.0) {
         return "—";
      } else {
         return distance >= 1000.0 ? String.format(Locale.ROOT, "%.1f", distance / 1000.0) + " " + t("gui.unit.km") : Math.round(distance) + " " + t("gui.unit.meter");
      }
   }

   private static String coords(double doubleValue, double d, double d2) {
      return (int)Math.floor(doubleValue) + " " + (int)Math.floor(d) + " " + (int)Math.floor(d2);
   }

   private static String dimension(String dimension) {
      return switch (dimension) {
         case "minecraft:overworld" -> t("gui.party.dim.overworld");
         case "minecraft:the_nether" -> t("gui.party.dim.nether");
         case "minecraft:the_end" -> t("gui.party.dim.end");
         default -> dimension.contains(":") ? dimension.substring(dimension.indexOf(58) + 1) : dimension;
      };
   }

   private static String ago(long time) {
      long l = Math.max(0L, (System.currentTimeMillis() - time) / 1000L);
      if (l < 60L) {
         return t("gui.party.ago.now");
      } else if (l < 3600L) {
         return t("gui.party.ago.min").replace("{n}", String.valueOf(l / 60L));
      } else {
         return l < 86400L
            ? t("gui.party.ago.hour").replace("{n}", String.valueOf(l / 3600L))
            : t("gui.party.ago.day").replace("{n}", String.valueOf(l / 86400L));
      }
   }

   private static void copy(String coords) {
      if (mc.keyboardHandler != null) {
         mc.keyboardHandler.setClipboard(coords);
      }
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private static final class Item {
      private final PartyBlock.Kind kind;
      private final BoundsData area;
      private final Object value;

      public PartyBlock.Kind kind() {
         return this.kind;
      }

      public BoundsData area() {
         return this.area;
      }

      public Object value() {
         return this.value;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Item other)) return false;
         return java.util.Objects.equals(this.kind, other.kind) && java.util.Objects.equals(this.area, other.area) && java.util.Objects.equals(this.value, other.value);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(kind, area, value);
      }

      @Override
      public String toString() {
         return "Item[" + "kind=" + this.kind + ", " + "area=" + this.area + ", " + "value=" + this.value + "]";
      }

      private Item(PartyBlock.Kind kind, BoundsData area, Object obj) {
         this.kind = kind;
         this.area = area;
         this.value = obj;
      }
   }

   private static enum Kind {
      MEMBER,
      INVITED,
      PLUS,
      INVITE_ALL,
      CANDIDATE,
      HERE,
      MARK,
      HINT,
      VOICE,
      LEAVE;
   }
}

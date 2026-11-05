package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Prefs;
import org.zenith.utility.api.Share;
import org.zenith.utility.api.UserSearchClient;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.PartyManager;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class FriendsSectionPanel extends SectionPanel {
   private static final int TAB_FRIENDS = 0;
   private static final int TAB_WEEK = 1;
   private static final int TAB_SHARING = 2;
   private static final int FILTER_ALL = 0;
   private static final int FILTER_ONLINE = 1;
   private static final int FILTER_NEAR = 2;
   private static final int FILTER_OFFLINE = 3;
   private static final int SLOT_OFFHAND = 40;
   private static final int SLOT_ARMOR_FIRST = 36;
   private static final int SLOT_ARMOR_LAST = 39;
   private static final float HERO_HEIGHT = 40.0F;
   private static final float TILE_PAD = 5.0F;
   private static final float HEAD_SIZE = 26.0F;
   private static final float BAR_HEIGHT = 3.0F;
   private static final float ITEM_SIZE = 16.0F;
   private static final float BUTTON_HEIGHT = 15.0F;
   private static final float CARD_HEIGHT = 26.0F;
   private static final float CARD_AVATAR = 16.0F;
   private static final float STATUS_DOT = 4.0F;
   private static final float SMALL_BUTTON = 12.0F;
   private static final float CHIP_HEIGHT = 18.0F;
   private static final float ADD_FIELD_HEIGHT = 22.0F;
   private static final float QUICK_HEIGHT = 30.0F;
   private static final float SHARE_CARD_HEIGHT = 28.0F;
   private static final long REOPEN_GAP_MS = 400L;
   private static final long FLASH_MS = 2600L;
   private static final ZenithColor FAVORITE = new ZenithColor(-278748);
   private static final ZenithColor DANGER = new ZenithColor(-495247);
   private static final long TYPING_PAUSE_MS = 260L;
   private static final int SUGGEST_LIMIT = 6;
   private static final int ADD_LOCAL = -1;
   private static final float NOTICE_PAD_X = 7.0F;
   private static final float NOTICE_PAD_Y = 5.0F;
   private final TextSetting addTargetSetting = new TextSetting("gui.friends.nick", "gui.friends.nick.desc", "", "gui.friends.nick.hint").method2121();
   private final InspectorRow addRow = new InspectorRow(this.addTargetSetting, 0);
   private String selected;
   private boolean selectedLocal;
   private final PartyBlock party = new PartyBlock(this);
   private final WeekBlock week = new WeekBlock(this);
   private int filter = 0;
   private final Animation cardReveal = new Animation(220L, 1.0F, Easing.INSTANCE12);
   private Predicate<String> openChatWith;
   private final Map<Long, Animation> avatarFade = new ConcurrentHashMap<>();
   private final Map<String, Animation> buttonHover = new HashMap<>();
   private final Map<String, Animation> barFill = new HashMap<>();
   private final Animation noticeAppear = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private String noticeAnimatedFor = "";
   private final Animation heroPulse = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private String noticeText = "";
   private String noticeTarget = "";
   private boolean offerOffline;
   private boolean addOpen;
   private final Map<String, Integer> hoveredButton = new HashMap<>();
   private final Map<String, Animation> cardSelect = new HashMap<>();
   private String typed = "";
   private long typedAt;
   private boolean searching;
   private String suggestFor = "";
   private boolean directoryAnswered;
   private List<UserSearchClient.Data> suggestions = List.of();
   private final Animation suggestWave = new Animation(460L, 0.0F, Easing.INSTANCE12);
   private final Animation listWave = new Animation(560L, 1.0F, Easing.INSTANCE12);
   private final Animation asideWave = new Animation(480L, 1.0F, Easing.INSTANCE12);
   private final Map<String, Long> pressedAt = new HashMap<>();
   private boolean submitWhenReady;
   private String flashText = "";
   private long flashAt;
   private String lastSignature = "";
   private String lastQuery = "";
   private String lastShareSignature = "";
   private long lastTickAt;
   private SliderSetting voiceVolume;
   private String voiceVolumeFor;
   private int order;
   private int asideOrder;
   private final TreeSet<String> nearbyNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
   private Object nearbyWorld;
   private int nearbyCount = -1;
   private long nearbyAt;

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   private static CloudConnectionState state() {
      Cloud cloud2 = cloud();
      return cloud2 == null ? null : cloud2.state();
   }

   private static String shareSignature() {
      Share share = Zenith.getInstance().getShare();
      return share == null
         ? ""
         : share.cosmeticsVisibility.getIndex()
            + "|"
            + share.position.isEnabled()
            + "|"
            + share.health.isEnabled()
            + "|"
            + share.inventory.isEnabled()
            + "|"
            + share.server.isEnabled()
            + "|"
            + share.colorSetting.method1445();
   }

   public void openChatWith(Predicate<String> openChatWith) {
      this.openChatWith = openChatWith;
   }

   @Override
   public String headerTitle() {
      return "Friends";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.FRIENDS;
   }

   @Override
   public String sectionDescription() {
      return "gui.friends.desc";
   }

   @Override
   public String emptyText() {
      return "";
   }

   @Override
   public float asideAlpha() {
      return this.selected == null ? 1.0F : this.cardReveal.method2808(1.0F);
   }

   @Override
   public void tick() {
      long now = System.currentTimeMillis();
      if (now - this.lastTickAt > 400L) {
         this.startListWave();
         restart(this.asideWave);
      }

      this.lastTickAt = now;
      this.pollTyping();
      StringBuilder builder = new StringBuilder();
      Cloud cloud2 = cloud();
      CloudConnectionState cloudConnectionState = state();
      if (cloud2 != null && cloudConnectionState != null) {
         builder.append(cloudConnectionState.getType()).append(';');
         String anObject = cloud2.method7911();
         String str = cloud2.method7913();

         for (String selected : cloud2.getFriends().all()) {
            PartyPlayerInfo partyPlayerInfo = cloudConnectionState.method8044(selected);
            builder.append(selected)
               .append(':')
               .append(partyPlayerInfo == null ? 45L : partyPlayerInfo.uid())
               .append((char)(cloudConnectionState.method8061(selected) ? '?' : '.'))
               .append((char)(partyPlayerInfo != null && partyPlayerInfo.method3289() ? 'v' : '.'))
               .append((char)(cloud2.prefs().method3319(selected) ? '*' : '.'))
               .append((char)(partyPlayerInfo != null && partyPlayerInfo.method3291(anObject, str) ? 'w' : '.'))
               .append(';');
         }

         if (this.voiceVolume != null && this.voiceVolumeFor != null) {
            cloud2.prefs().method3324(this.voiceVolumeFor, Math.round(this.voiceVolume.getValue3()));
         }

         for (CloudPackets.Data8 data8 : cloudConnectionState.method8055()) {
            builder.append('R').append(data8.username()).append(';');
         }

         PartyMember partyMember = cloud2.getPartyMember();
         builder.append('S')
            .append(partyMember.version())
            .append(partyMember.method1147())
            .append(partyMember.method1136())
            .append(partyMember.getLongValue())
            .append('P')
            .append(this.party.signature(cloud2))
            .append('W')
            .append(this.week.signature(cloud2));
         this.week.tick(cloud2, this.getActiveTab() == 1);
      }

      var items = Zenith.getInstance().getFriends().getItems();
      if (items != null) {
         for (String str : List.copyOf(items)) {
            builder.append('L').append(str).append((char)(this.isNearby(str) ? '+' : '-')).append(';');
         }
      }

      builder.append('N').append(this.noticeText).append('|').append(this.searching).append(this.addOpen).append('|').append(this.flashing());
      String lastShareSignature = shareSignature();
      if (!lastShareSignature.equals(this.lastShareSignature)) {
         if (!this.lastShareSignature.isEmpty()) {
            Zenith.getInstance().getShare().save();
         }

         this.lastShareSignature = lastShareSignature;
      }

      String searchQuery = this.searchQuery();
      if (!builder.toString().equals(this.lastSignature) || !searchQuery.equals(this.lastQuery)) {
         this.lastSignature = builder.toString();
         this.lastQuery = searchQuery;
         this.invalidate();
      }
   }

   @Override
   public void selectTab(int activeTab) {
      if (activeTab != this.getActiveTab()) {
         this.startListWave();
         restart(this.asideWave);
         if (activeTab == 1) {
            this.week.opened(cloud());
         }
      }

      super.selectTab(activeTab);
   }

   private void startListWave() {
      restart(this.listWave);
   }

   private static void restart(Animation asideWave) {
      asideWave.reset(0.0F);
      asideWave.method2810(1.0F);
   }

   private static float wave(Animation listWave, int n) {
      float f = listWave.method2808(1.0F);
      return Mth.clamp((f - Math.min(n, 12) * 0.06F) / 0.5F, 0.0F, 1.0F);
   }

   @Override
   public void build() {
      this.tabs(new String[]{"gui.friends.tab.list", "gui.friends.tab.week", "gui.friends.tab.share"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.FRIENDS, MenuGlyph.TIMER, MenuGlyph.SHIELD});
      this.order = 0;
      this.asideOrder = 0;
      switch (this.getActiveTab()) {
         case 1:
            this.week.build(cloud());
            break;
         case 2:
            this.buildSharing();
            break;
         default:
            this.buildFriends();
      }
   }

   private void buildFriends() {
      String searchQuery = this.searchQuery();
      Cloud cloud2 = cloud();
      CloudConnectionState cloudConnectionState = state();
      Prefs prefs2 = prefs();
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      ArrayList arrayList3 = new ArrayList();
      ArrayList arrayList4 = new ArrayList();
      if (cloud2 != null) {
         for (String selected : cloud2.getFriends().all()) {
            if (matches(selected, searchQuery)) {
               PartyPlayerInfo partyPlayerInfo = peer(selected);
               if (partyPlayerInfo != null) {
                  arrayList.add(selected);
                  if (partyPlayerInfo.method3291(cloud2.method7911(), cloud2.method7913())) {
                     arrayList2.add(selected);
                  }
               } else if (cloudConnectionState != null && cloudConnectionState.method8061(selected)) {
                  arrayList3.add(selected);
               } else {
                  arrayList4.add(selected);
               }
            }
         }
      }

      ArrayList arrayList5 = new ArrayList();
      ArrayList arrayList6 = new ArrayList();
      var items = Zenith.getInstance().getFriends().getItems();
      if (items != null) {
         for (Object obj : (Iterable<?>)(Object)(new LinkedHashSet(List.copyOf(items)))) {
            String selected = (String)obj;
            if (matches(selected, searchQuery)) {
               (this.isNearby(selected) ? arrayList5 : arrayList6).add(selected);
            }
         }
      }

      if (prefs2 != null) {
         Comparator<String> comparator = (var1x, var2x) -> Boolean.compare(prefs2.method3319(var2x), prefs2.method3319(var1x));
         arrayList.sort(comparator);
         arrayList2.sort(comparator);
         arrayList3.sort(comparator);
         arrayList4.sort(comparator);
      }

      int n = arrayList.size() + arrayList3.size() + arrayList4.size() + arrayList5.size() + arrayList6.size();
      List list = cloudConnectionState == null ? List.of() : new ArrayList<>(cloudConnectionState.method8055());
      this.hero(arrayList.size() + arrayList5.size(), n);
      this.party.build(cloud2);
      this.buildAddBlock();
      if (!list.isEmpty()) {
         this.caption(t("gui.friends.caption.requests") + " · " + list.size());

         for (Object obj : (Iterable<?>)(Object)(list)) {
            CloudPackets.Data8 data8 = (CloudPackets.Data8)obj;
            this.requestCard(data8, this.order++);
         }
      }

      int invites = this.buildInvites(cloud2);
      if (n == 0) {
         if (list.isEmpty() && invites == 0) {
            this.emptyState(searchQuery);
         }

         this.buildFriendAside();
      } else {
         int[] ints = new int[]{n, arrayList.size() + arrayList5.size(), arrayList2.size() + arrayList5.size(), arrayList3.size() + arrayList4.size() + arrayList6.size()};
         this.filterChips(ints);
         int n2 = 0;
         if (this.filter == 0 || this.filter == 1) {
            n2 += this.section("gui.friends.caption.online", arrayList, arrayList5);
         }

         if (this.filter == 2) {
            n2 += this.section("gui.friends.caption.near", arrayList2, arrayList5);
         }

         if (this.filter == 0 || this.filter == 3) {
            n2 += this.section("gui.friends.caption.pending", arrayList3, List.of());
            n2 += this.section("gui.friends.caption.offline", arrayList4, arrayList6);
         }

         if (n2 == 0) {
            this.hint(t("gui.friends.filter.empty"));
         }

         this.buildFriendAside();
      }
   }

   private int buildInvites(Cloud cloud) {
      if (cloud == null) {
         return 0;
      } else {
         PartyMember partyMember = cloud.getPartyMember();
         List<PartyMember.Data7> data7s = partyMember.method1144();
         List<PartyMember.Data16> data16s = partyMember.method1145();
         List<PartyMember.Data13> data13s = partyMember.method1146();
         int n = data7s.size() + data16s.size() + data13s.size();
         if (n == 0) {
            return 0;
         } else {
            this.caption(t("gui.friends.caption.invites") + " · " + n);

            for (PartyMember.Data13 data13 : data13s) {
               String str = ServerNameUtils.method5643(data13.server(), data13.mode());
               this.inviteCard(
                  "join:" + data13.getLongValue(),
                  data13.getText(),
                  data13.getLongValue(),
                  t("gui.friends.invite.join").replace("{w}", str.isEmpty() ? data13.getText3() : str),
                  t("gui.friends.invite.go"),
                  () -> {
                     PartyManager var1x = PartyManager.method17();
                     if (var1x != null) {
                        var1x.method44(data13);
                     }
                  },
                  () -> partyMember.method1178(data13)
               );
            }

            for (PartyMember.Data7 data7 : data7s) {
               this.inviteCard(
                  "party:" + data7.getLongValue(),
                  data7.getText2(),
                  data7.getLongValue2(),
                  t("gui.friends.invite.party").replace("{p}", data7.name()).replace("{n}", String.valueOf(data7.getItems().size())),
                  t("gui.friends.invite.accept"),
                  () -> partyMember.method1154(data7),
                  () -> partyMember.method1155(data7)
               );
            }

            for (PartyMember.Data16 data16 : data16s) {
               this.inviteCard(
                  "room:" + data16.getText(),
                  data16.getText3(),
                  data16.getLongValue(),
                  t("gui.friends.invite.room").replace("{r}", data16.name()),
                  t("gui.friends.invite.enter"),
                  () -> partyMember.method1172(data16.getText()),
                  () -> partyMember.method1173(data16)
               );
            }

            return n;
         }
      }
   }

   private void inviteCard(String key, String text, long longValue, String name, String str, Runnable runnable, Runnable runnable2) {
      int n = this.order++;
      this.custom(
         26.0F,
         (var8x, var9x, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float f4 = this.appear(key) * wave(this.listWave, n);
            if (!(f4 <= 0.002F)) {
               previewAlpha *= f4;
               f += (1.0F - f4) * 8.0F;
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 1.5F;
               float height = 23.0F;
               boolean contains = new BoundsData(floatValue, y, width, height).contains(var9x, lastClickY);
               plate(var8x, floatValue, y, width, height, this.hover(key, contains), 0.0F, previewAlpha, zenithStyle);
               float f5 = 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 420.0));
               var8x.drawRoundedRect(
                  floatValue, y, width, height, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.07F)
               );
               var8x.drawRoundedRect(
                  floatValue + 1.5F,
                  y + 4.0F,
                  2.0F,
                  height - 8.0F,
                  ProxyDotRadiusData.method8069(1.0F),
                  zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * f5)
               );
               float f6 = y + height / 2.0F;
               this.avatar(var8x, text, longValue, floatValue + 7.0F, f6 - 8.0F, 16.0F, previewAlpha, zenithStyle);
               Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
               String text2 = t("gui.friends.invite.hide");
               float width2 = font.width(text2) + 12.0F;
               float width3 = font.width(str) + 12.0F;
               float y2 = f6 - 6.0F;
               BoundsData boundsData = new BoundsData(floatValue + width - 6.0F - width2, y2, width2, 12.0F);
               BoundsData boundsData2 = new BoundsData(boundsData.getFloatValue() - 4.0F - width3, y2, width3, 12.0F);
               int n2 = boundsData2.contains(var9x, lastClickY) ? 0 : (boundsData.contains(var9x, lastClickY) ? 1 : -1);
               this.hoveredButton.put(key, n2);
               Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font font3 = Fonts.NEW_REGULAR.getFont(4.4F);
               float floatValue2 = floatValue + 7.0F + 16.0F + 6.0F;
               float centeredTextY = f6 - (font2.height() + 2.0F + font3.height()) / 2.0F;
               float width4 = boundsData2.getFloatValue() - 4.0F - floatValue2;
               var8x.drawText(font2, fit(font2, text, width4), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               var8x.drawText(font3, fit(font3, name, width4), floatValue2, centeredTextY + font2.height() + 2.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               this.pillButton(var8x, key + ":yes", boundsData2, str, true, n2 == 0, previewAlpha, zenithStyle);
               this.pillButton(var8x, key + ":no", boundsData, text2, false, n2 == 1, previewAlpha, zenithStyle);
            }
         },
         () -> {
            int n2 = this.hoveredButton.getOrDefault(key, -1);
            if (n2 >= 0) {
               this.press(key + (n2 == 0 ? ":yes" : ":no"));
               (n2 == 0 ? runnable : runnable2).run();
               this.invalidate();
            }
         },
         null
      );
   }

   private int section(String key, List<String> strings, List<String> strings2) {
      int n = strings.size() + strings2.size();
      if (n == 0) {
         return 0;
      } else {
         this.caption(t(key) + " · " + n);

         for (String selected : strings) {
            this.friendCard(selected, false, this.order++);
         }

         for (String selected : strings2) {
            this.friendCard(selected, true, this.order++);
         }

         return n;
      }
   }

   private void hero(int n, int n2) {
      int n3 = this.order++;
      String key = "hero";
      this.custom(
         40.0F,
         (customDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.listWave, n3);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float floatValue = f + 8.0F;
               float f4 = f + f3 - 8.0F;
               float y = f2 + 3.0F;
               float height = 34.0F;
               CloudConnectionState cloudConnectionState = state();
               boolean flag = cloudConnectionState != null && cloudConnectionState.method8035();
               float hover = this.heroPulse.method2808(flag ? 1.0F : 0.0F);
               plate(customDrawContext, floatValue, y, f4 - floatValue, height, 0.0F, 0.0F, previewAlpha, zenithStyle);
               customDrawContext.drawRoundedRect(
                  floatValue,
                  y,
                  f4 - floatValue,
                  height,
                  ProxyDotRadiusData.method8069(4.0F),
                  zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.06F * hover)
               );
               ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover);
               bigGlyph(customDrawContext, MenuGlyph.FRIENDS, floatValue + 8.0F, y + (height - 12.0F) / 2.0F, zenithColor.method7294(previewAlpha));
               Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.6F);
               float floatValue2 = floatValue + 8.0F + 12.0F + 8.0F;
               float centeredTextY = y + (height - font.height() - 3.0F - font2.height()) / 2.0F;
               customDrawContext.drawText(font, t("gui.friends.title"), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               float f5 = 1.0F;
               String name;
               ZenithColor zenithColor2;
               if (cloudConnectionState == null) {
                  name = t("gui.cloud.state.missing");
                  zenithColor2 = DANGER;
               } else if (flag) {
                  name = n2 == 0
                     ? t("gui.friends.hero.none")
                     : t("gui.friends.hero.online").replace("{n}", String.valueOf(n)).replace("{m}", String.valueOf(n2));
                  zenithColor2 = zenithStyle.getFriendColor().getZenithColor();
               } else if (cloudConnectionState.getType() == CloudConnectionState.Type.CONNECTING) {
                  name = cloudConnectionState.getType().label();
                  zenithColor2 = new ZenithColor(-680437);
                  f5 = 0.45F + 0.55F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 220.0));
               } else {
                  String note = cloudConnectionState.note();
                  if (note == null || note.isBlank()) {
                     note = cloudConnectionState.getLastError();
                  }

                  name = note != null && !note.isBlank() ? note : cloudConnectionState.getType().label();
                  zenithColor2 = DANGER;
               }

               float width = 3.0F;
               float centeredTextY2 = centeredTextY + font.height() + 3.0F;
               customDrawContext.drawRoundedRect(
                  floatValue2,
                  centeredTextY2 + font2.height() / 2.0F - width / 2.0F,
                  width,
                  width,
                  ProxyDotRadiusData.method8069(width / 2.0F),
                  zenithColor2.method7294(previewAlpha * f5)
               );
               Font font3 = Fonts.NEW_MEDIUM.getFont(5.0F);
               String text = t(this.addOpen ? "gui.friends.hero.hide" : "gui.friends.hero.add");
               float width2 = font3.width(text) + 18.0F;
               float height2 = 13.0F;
               BoundsData boundsData = new BoundsData(f4 - 8.0F - width2, y + (height - height2) / 2.0F, width2, height2);
               boolean contains = boundsData.contains(lastClickX, lastClickY);
               this.hoveredButton.put(key, contains ? 0 : -1);
               if (contains) {
                  HoverInfo.request(t("gui.friends.caption.add"), t("gui.friends.add.desc"));
               } else if (new BoundsData(floatValue, y, f4 - floatValue, height).contains(lastClickX, lastClickY)) {
                  HoverInfo.request(t("gui.friends.title"), t("gui.friends.hero.desc"));
               }

               float hover2 = this.hover(key + ":pill", contains);
               float pick = this.pick(key + ":open", this.addOpen);
               ZenithColor zenithColor3 = zenithStyle.getPrimaryColor().getZenithColor().method7295(zenithStyle.getDisableActiveBg().getZenithColor(), pick);
               customDrawContext.drawRoundedRect(
                  boundsData.getFloatValue() - hover2,
                  boundsData.getFloatValue2() - hover2 * 0.5F,
                  boundsData.width() + hover2 * 2.0F,
                  boundsData.height() + hover2,
                  ProxyDotRadiusData.method8069(height2 / 2.0F),
                  zenithColor3.method7294(previewAlpha)
               );
               plusGlyph(
                  customDrawContext,
                  boundsData.getFloatValue() + 8.0F,
                  boundsData.getFloatValue2() + height2 / 2.0F,
                  pick,
                  new ZenithColor(255, 255, 255).method7295(zenithStyle.getTextEnable().getZenithColor(), pick).method7294(previewAlpha)
               );
               customDrawContext.drawText(
                  font3,
                  text,
                  boundsData.getFloatValue() + 13.0F,
                  boundsData.getFloatValue2() + (height2 - font3.height()) / 2.0F,
                  new ZenithColor(255, 255, 255).method7295(zenithStyle.getTextEnable().getZenithColor(), pick).method7294(previewAlpha)
               );
               float width3 = boundsData.getFloatValue() - 8.0F - (floatValue2 + width + 4.0F);
               customDrawContext.drawText(font2, fit(font2, name, width3), floatValue2 + width + 4.0F, centeredTextY2, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            }
         },
         () -> {
            if (this.hoveredButton.getOrDefault(key, -1) == 0) {
               this.press(key);
               if (this.addOpen) {
                  this.closeAdd();
               } else {
                  this.openAdd();
               }
            }
         },
         null
      );
   }

   private void emptyState(String searchQuery) {
      int n = this.order++;
      String description = searchQuery.isEmpty() ? t("gui.friends.empty.text") : t("gui.friends.emptyQuery").replace("{q}", searchQuery);
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> strings = wrapText(description, font, this.mainWidth() - 16.0F - 30.0F, 4);
      float f = font.height() + 2.0F;
      float hintsHeight = Math.max(48.0F, 30.0F + strings.size() * f);
      this.custom(hintsHeight, (var6x, var7x, n2, f2, f3, f4, previewAlpha, zenithStyle) -> {
         float wave2 = wave(this.listWave, n);
         if (!(wave2 <= 0.002F)) {
            previewAlpha *= wave2;
            float f5 = f2 + f4 / 2.0F;
            float f6 = (float)Math.sin(System.currentTimeMillis() / 700.0) * 1.2F;
            bigGlyph(var6x, MenuGlyph.FRIENDS, f5 - 6.0F, f3 + 6.0F + f6, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F));
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.4F);
            String text = t(searchQuery.isEmpty() ? "gui.friends.empty.title" : "gui.friends.filter.empty");
            var6x.drawText(font2, text, f5 - font2.width(text) / 2.0F, f3 + 22.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
            float centeredTextY = f3 + 22.0F + font2.height() + 3.0F;

            for (String str : strings) {
               var6x.drawText(font, str, f5 - font.width(str) / 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               centeredTextY += f;
            }
         }
      });
   }

   private void filterChips(int[] ints) {
      int n = this.order++;
      String key = "filter";
      String[] strings = new String[]{"gui.friends.filter.all", "gui.friends.filter.online", "gui.friends.filter.near", "gui.friends.filter.offline"};
      this.custom(
         22.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.listWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
               Font font2 = Fonts.NEW_MEDIUM.getFont(4.2F);
               float floatValue = f + 8.0F;
               float y = f2 + 3.0F;
               int n2 = -1;

               for (int i = 0; i < strings.length; i++) {
                  String text = t(strings[i]);
                  String str = String.valueOf(ints[i]);
                  float width = 8.0F + font.width(text) + 4.0F + font2.width(str) + 8.0F;
                  BoundsData boundsData = new BoundsData(floatValue, y, width, 16.0F);
                  boolean contains = boundsData.contains(lastClickX, lastClickY);
                  if (contains) {
                     n2 = i;
                     HoverInfo.request(text, t("gui.friends.filter.desc"));
                  }

                  float hover = this.hover(key + i, contains);
                  float pick = this.pick(key + i, this.filter == i);
                  menuDrawContext.drawRoundedRect(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     boundsData.width(),
                     boundsData.height(),
                     ProxyDotRadiusData.method8069(boundsData.height() / 2.0F),
                     zenithStyle.getDisableActiveBg()
                        .getZenithColor()
                        .method7294(previewAlpha * (0.4F + 0.3F * hover))
                        .method7295(zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.9F), pick)
                  );
                  ZenithColor zenithColor = zenithStyle.getTextSecondary()
                     .getZenithColor()
                     .method7295(zenithStyle.getTextEnable().getZenithColor(), hover)
                     .method7295(new ZenithColor(255, 255, 255), pick);
                  menuDrawContext.drawText(
                     font, text, boundsData.getFloatValue() + 8.0F, boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F, zenithColor.method7294(previewAlpha)
                  );
                  menuDrawContext.drawText(
                     font2,
                     str,
                     boundsData.getFloatValue() + 8.0F + font.width(text) + 4.0F,
                     boundsData.getFloatValue2() + (boundsData.height() - font2.height()) / 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7295(new ZenithColor(255, 255, 255, 200), pick).method7294(previewAlpha)
                  );
                  floatValue += width + 4.0F;
               }

               this.hoveredButton.put(key, n2);
            }
         },
         () -> {
            int var2x = this.hoveredButton.getOrDefault(key, -1);
            if (var2x >= 0 && var2x != this.filter) {
               this.filter = var2x;
               this.startListWave();
               this.invalidate();
            }
         },
         null
      );
   }

   private void buildAddBlock() {
      if (this.addOpen || !this.noticeText.isEmpty()) {
         if (this.addOpen) {
            this.addInputField();
            this.buildSuggestions();
         }

         this.buildNotice();
         this.gap(GuiStyle.PADDING.intValue());
      }
   }

   private void addInputField() {
      int n = this.order++;
      String key = "add.field";
      this.custom(
         26.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.listWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               float floatValue = f + 8.0F + (1.0F - wave2) * 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 2.0F;
               float height = 22.0F;
               boolean editing = this.addRow.isEditing();
               float hover = this.hover(key + ":focus", editing);
               BoundsData boundsData = new BoundsData(floatValue, y, width, height);
               boolean contains = boundsData.contains(lastClickX, lastClickY);
               menuDrawContext.drawRoundedRect(
                  floatValue,
                  y,
                  width,
                  height,
                  ProxyDotRadiusData.method8069(height / 2.0F),
                  zenithStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * (2.0F + this.hover(key + ":hot", contains) * 0.8F))
               );
               menuDrawContext.drawRoundedBorder(
                  floatValue,
                  y,
                  width,
                  height,
                  0.15F,
                  ProxyDotRadiusData.method8069(height / 2.0F),
                  zenithStyle.getFieldBorder().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover).method7294(previewAlpha * (1.4F + hover))
               );
               float floatValue2 = floatValue + 9.0F;
               MenuGlyph.SEARCH
                  .draw(
                     menuDrawContext,
                     floatValue2,
                     y + (height - 6.0F) / 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover).method7294(previewAlpha)
                  );
               String typedName = this.typedName();
               boolean flag = !typedName.isEmpty();
               Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
               String text = t(this.searching ? "gui.friends.add.wait" : "gui.friends.add");
               float width2 = Fonts.NEW_MEDIUM.getFont(4.8F).width(text) + 20.0F;
               float height2 = height - 5.0F;
               BoundsData boundsData2 = new BoundsData(floatValue + width - 3.0F - width2, y + 2.5F, width2, height2);
               float width3 = 11.0F;
               BoundsData boundsData3 = new BoundsData(boundsData2.getFloatValue() - 4.0F - width3, y + (height - width3) / 2.0F, width3, width3);
               float floatValue3 = floatValue2 + 6.0F + 6.0F;
               float f4 = (flag ? boundsData3.getFloatValue() : boundsData2.getFloatValue()) - 6.0F;
               float centeredTextY = y + (height - font.height()) / 2.0F;
               if (flag) {
                  String str = fit(font, typedName, f4 - floatValue3);
                  float f5 = floatValue3 + font.width(str);
                  menuDrawContext.drawText(font, str, floatValue3, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
                  if (editing && System.currentTimeMillis() % 1000L < 550L) {
                     menuDrawContext.drawRoundedRect(
                        f5 + 0.5F,
                        centeredTextY - 0.5F,
                        1.0F,
                        font.height() + 1.0F,
                        ProxyDotRadiusData.method8069(0.5F),
                        zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
                     );
                  }
               } else {
                  float f5 = editing && System.currentTimeMillis() % 1000L < 550L ? 1.0F : 0.0F;
                  if (f5 > 0.0F) {
                     menuDrawContext.drawRoundedRect(
                        floatValue3 + 0.5F,
                        centeredTextY - 0.5F,
                        1.0F,
                        font.height() + 1.0F,
                        ProxyDotRadiusData.method8069(0.5F),
                        zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
                     );
                  }

                  menuDrawContext.drawText(
                     font, t("gui.friends.nick.hint"), floatValue3 + f5 + 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F)
                  );
               }

               byte b = -1;
               if (flag) {
                  boolean contains2 = boundsData3.contains(lastClickX, lastClickY);
                  if (contains2) {
                     b = 1;
                  }

                  float hover2 = this.hover(key + ":clear", contains2);
                  menuDrawContext.drawRoundedRect(
                     boundsData3.getFloatValue(),
                     boundsData3.getFloatValue2(),
                     boundsData3.width(),
                     boundsData3.height(),
                     ProxyDotRadiusData.method8069(width3 / 2.0F),
                     zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (0.4F + hover2 * 0.5F))
                  );
                  plusGlyph(
                     menuDrawContext,
                     boundsData3.getFloatValue() + width3 / 2.0F,
                     boundsData3.getFloatValue2() + width3 / 2.0F,
                     1.0F,
                     zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover2).method7294(previewAlpha)
                  );
                  boolean contains3 = boundsData2.contains(lastClickX, lastClickY);
                  if (contains3 && !this.searching) {
                     b = 0;
                  }

                  float hover3 = this.hover(key + ":pill", contains3 && !this.searching);
                  float f5 = this.searching ? 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0)) : 1.0F;
                  menuDrawContext.drawRoundedRect(
                     boundsData2.getFloatValue(),
                     boundsData2.getFloatValue2(),
                     boundsData2.width(),
                     boundsData2.height(),
                     ProxyDotRadiusData.method8069(height2 / 2.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), hover3 * 0.15F).method7294(previewAlpha * f5)
                  );
                  Font font2 = Fonts.NEW_MEDIUM.getFont(4.8F);
                  menuDrawContext.drawText(
                     font2,
                     text,
                     boundsData2.getFloatValue() + (boundsData2.width() - font2.width(text)) / 2.0F,
                     boundsData2.getFloatValue2() + (boundsData2.height() - font2.height()) / 2.0F,
                     new ZenithColor(255, 255, 255).method7294(previewAlpha * f5)
                  );
               }

               this.hoveredButton.put(key, Integer.valueOf(b));
               if (contains && b < 0) {
                  HoverInfo.request(t("gui.friends.caption.add"), t("gui.friends.add.desc"));
               }
            }
         },
         () -> {
            int var2x = this.hoveredButton.getOrDefault(key, -1);
            if (var2x == 0) {
               this.press(key + ":pill");
               this.submitAdd();
            } else if (var2x == 1) {
               this.press(key + ":clear");
               this.clearInput();
               if (!this.addRow.isEditing()) {
                  this.addRow.beginEdit();
               }

               this.invalidate();
            } else if (!this.addRow.isEditing()) {
               this.addRow.beginEdit();
               this.invalidate();
            }
         },
         null
      );
   }

   private void openAdd() {
      this.addOpen = true;
      this.addRow.beginEdit();
      this.startListWave();
      this.invalidate();
   }

   public void devOpenAdd(String string) {
      this.selectTab(0);
      this.openAdd();
      if (string != null && !string.isEmpty()) {
         this.addTargetSetting.setValue(string);
         this.pollTyping();
      }

      this.invalidate();
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.getActiveTab() == 0 && this.addOpen && this.addRow.isEditing()) {
         switch (keyCode) {
            case 256:
               this.closeAdd();
               return true;
            case 257:
            case 335:
               if (!this.typedName().isEmpty()) {
                  this.submitAdd();
               }

               return true;
            case 258:
               this.completeToSuggestion();
               return true;
            default:
               this.addRow.keyPressed(keyCode, keycode, modifiers);
               this.pollTyping();
               this.invalidate();
               return true;
         }
      } else {
         return super.keyPressed(keyCode, keycode, modifiers);
      }
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      if (this.getActiveTab() == 0 && this.addOpen && this.addRow.isEditing()) {
         this.addRow.charTyped(codepoint, n);
         this.pollTyping();
         this.invalidate();
         return true;
      } else {
         return super.charTyped(codepoint, n);
      }
   }

   private void completeToSuggestion() {
      String typedName = this.typedName();

      for (FriendsSectionPanel.Hint hint : this.ordered(typedName)) {
         if (!hint.name().equalsIgnoreCase(typedName)) {
            this.addTargetSetting.setValue(hint.name());
            this.addRow.endEdit();
            this.addRow.beginEdit();
            this.invalidate();
            return;
         }
      }
   }

   private void closeAdd() {
      this.addOpen = false;
      this.startListWave();
      this.addRow.endEdit();
      this.clearInput();
      this.setNotice("", "", false);
   }

   private void requestCard(CloudPackets.Data8 data8, int n) {
      String username = data8.username();
      String key = "request:" + username;
      String replacement = blankToDash(data8.getText2());
      this.custom(
         26.0F,
         (var5x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float f4 = this.appear(key) * wave(this.listWave, n);
            if (!(f4 <= 0.002F)) {
               previewAlpha *= f4;
               f += (1.0F - f4) * 8.0F;
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 1.5F;
               float height = 23.0F;
               boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
               float hover = this.hover(key, contains);
               plate(var5x, floatValue, y, width, height, hover, 0.0F, previewAlpha, zenithStyle);
               float f5 = 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 420.0));
               var5x.drawRoundedRect(
                  floatValue, y, width, height, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.07F)
               );
               var5x.drawRoundedRect(
                  floatValue + 1.5F,
                  y + 4.0F,
                  2.0F,
                  height - 8.0F,
                  ProxyDotRadiusData.method8069(1.0F),
                  zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * f5)
               );
               if (contains) {
                  HoverInfo.request(username, t("gui.friends.request.desc").replace("{n}", replacement));
               }

               float f6 = y + height / 2.0F;
               this.avatar(var5x, username, floatValue + 7.0F, f6 - 8.0F, 16.0F, previewAlpha, zenithStyle);
               Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
               String text = t("gui.friends.accept.button");
               String str = t("gui.friends.decline");
               float width2 = font.width(str) + 12.0F;
               float width3 = font.width(text) + 12.0F;
               float y2 = f6 - 6.0F;
               BoundsData boundsData = new BoundsData(floatValue + width - 6.0F - width2, y2, width2, 12.0F);
               BoundsData boundsData2 = new BoundsData(boundsData.getFloatValue() - 4.0F - width3, y2, width3, 12.0F);
               int n2 = boundsData2.contains(lastClickX, lastClickY) ? 0 : (boundsData.contains(lastClickX, lastClickY) ? 1 : -1);
               this.hoveredButton.put(key, n2);
               Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font font3 = Fonts.NEW_REGULAR.getFont(4.4F);
               float floatValue2 = floatValue + 7.0F + 16.0F + 6.0F;
               float centeredTextY = f6 - (font2.height() + 2.0F + font3.height()) / 2.0F;
               float width4 = boundsData2.getFloatValue() - 4.0F - floatValue2;
               var5x.drawText(font2, fit(font2, username, width4), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               String name = t("gui.friends.request.want") + ("-".equals(replacement) ? "" : " · " + replacement);
               var5x.drawText(font3, fit(font3, name, width4), floatValue2, centeredTextY + font2.height() + 2.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               this.pillButton(var5x, key + ":yes", boundsData2, text, true, n2 == 0, previewAlpha, zenithStyle);
               this.pillButton(var5x, key + ":no", boundsData, str, false, n2 == 1, previewAlpha, zenithStyle);
            }
         },
         () -> {
            int var3x = this.hoveredButton.getOrDefault(key, -1);
            Cloud var4x = cloud();
            if (var4x != null && var3x >= 0) {
               this.press(key + (var3x == 0 ? ":yes" : ":no"));
               if (var3x == 1) {
                  var4x.method7906(username);
               } else {
                  var4x.method7905(username);
               }

               this.invalidate();
            }
         },
         null
      );
   }

   private void friendCard(String selected, boolean selectedLocal, int n) {
      String key = (selectedLocal ? "local:" : "cloud:") + selected;
      String name = selectedLocal ? selected : displayName(selected);
      String str = selectedLocal ? "gui.friends.row.local.desc" : "gui.friends.row.desc";
      this.custom(
         26.0F,
         (customDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float f4 = this.appear(key) * wave(this.listWave, n);
            if (!(f4 <= 0.002F)) {
               previewAlpha *= f4;
               f += (1.0F - f4) * 8.0F;
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 1.5F;
               float height = 23.0F;
               boolean flag = this.selectedLocal == selectedLocal && selected.equals(this.selected);
               boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
               float hover = this.hover(key, contains);
               float select = this.select(key, flag);
               plate(customDrawContext, floatValue, y, width, height, hover, 0.0F, previewAlpha, zenithStyle);
               if (select > 0.002F) {
                  customDrawContext.drawRoundedRect(
                     floatValue,
                     y,
                     width,
                     height,
                     ProxyDotRadiusData.method8069(4.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.08F * select)
                  );
                  float height2 = (height - 8.0F) * select;
                  customDrawContext.drawRoundedRect(
                     floatValue + 1.5F,
                     y + (height - height2) / 2.0F,
                     2.0F,
                     height2,
                     ProxyDotRadiusData.method8069(1.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
                  );
               }

               PartyPlayerInfo partyPlayerInfo = selectedLocal ? null : peer(selected);
               boolean flag2 = !selectedLocal && !isPendingName(selected);
               BoundsData boundsData = new BoundsData(floatValue + width - 6.0F - 12.0F - 4.0F, y + (height - 12.0F) / 2.0F, 16.0F, 12.0F);
               boolean flag3 = flag2 && contains && boundsData.contains(lastClickX, lastClickY);
               this.hoveredButton.put(key, flag3 ? 1 : -1);
               if (contains) {
                  HoverInfo.request(name, t(flag3 ? "gui.friends.quick.chat.desc" : str));
               }

               float f5 = hover * 1.5F;
               float f6 = y + height / 2.0F;
               float floatValue2 = floatValue + 7.0F + f5;
               float y2 = f6 - 8.0F;
               this.avatar(customDrawContext, selected, floatValue2, y2, 16.0F, previewAlpha, zenithStyle);
               this.drawStatusDot(customDrawContext, key, selected, selectedLocal, floatValue2 + 16.0F - 4.0F + 0.5F, y2 + 16.0F - 4.0F + 0.5F, previewAlpha, zenithStyle);
               float f7 = floatValue + width - 6.0F;
               float hover2 = this.hover(key + ":chat", flag2 && contains);
               if (hover2 > 0.01F) {
                  customDrawContext.drawRoundedRect(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     boundsData.width(),
                     boundsData.height(),
                     ProxyDotRadiusData.method8069(boundsData.height() / 2.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * hover2 * (flag3 ? 0.95F : 0.25F))
                  );
                  MenuGlyph.CHAT
                     .draw(
                        customDrawContext,
                        boundsData.getFloatValue() + (boundsData.width() - 6.0F) / 2.0F,
                        boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F,
                        (flag3 ? new ZenithColor(255, 255, 255) : zenithStyle.getPrimaryColor().getZenithColor()).method7294(previewAlpha * hover2)
                     );
                  f7 = boundsData.getFloatValue() - 4.0F;
               }

               Font font = Fonts.NEW_REGULAR.getFont(4.4F);
               String text = selectedLocal ? "" : sideNote(selected);
               if (!text.isEmpty() && hover2 < 0.99F) {
                  customDrawContext.drawText(
                     font,
                     text,
                     f7 - font.width(text),
                     f6 - font.height() / 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * (1.0F - hover2))
                  );
                  f7 -= (font.width(text) + 5.0F) * (1.0F - hover2);
               }

               if (partyPlayerInfo != null && partyPlayerInfo.method3289()) {
                  Voice voice = Zenith.getInstance().getVoice();
                  boolean flag4 = voice != null && voice.getVoiceState().method5365(partyPlayerInfo.uid());
                  if (flag4) {
                     Voice.method371(
                        customDrawContext, f7 - 11.0F, f6, voice.getVoiceState().method5366(partyPlayerInfo.uid()), zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
                     );
                     f7 -= 15.0F;
                  } else {
                     MenuGlyph.MIC.draw(customDrawContext, f7 - 6.0F, f6 - 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F));
                     f7 -= 11.0F;
                  }
               }

               Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
               float floatValue3 = floatValue2 + 16.0F + 6.0F;
               float width2 = f7 - 4.0F - floatValue3;
               float centeredTextY = f6 - (font2.height() + 2.0F + font.height()) / 2.0F;
               Prefs prefs2 = prefs();
               boolean flag4 = !selectedLocal && prefs2 != null && prefs2.method3319(selected);
               float floatValue4 = floatValue3;
               if (flag4) {
                  diamond(customDrawContext, floatValue3 + 1.5F, centeredTextY + font2.height() / 2.0F, 3.0F, FAVORITE.method7294(previewAlpha));
                  floatValue4 = floatValue3 + 6.0F;
               }

               customDrawContext.drawText(
                  font2,
                  fit(font2, name, width2 - (floatValue4 - floatValue3)),
                  floatValue4,
                  centeredTextY,
                  zenithStyle.getTextSecondary()
                     .getZenithColor()
                     .method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(0.55F, Math.max(select, hover)))
                     .method7294(previewAlpha)
               );
               customDrawContext.drawText(
                  font,
                  fit(font, this.cardNote(selected, selectedLocal), width2),
                  floatValue3,
                  centeredTextY + font2.height() + 2.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
            }
         },
         () -> {
            if (this.hoveredButton.getOrDefault(key, -1) == 1) {
               this.openChat(selected);
            } else {
               if (!selected.equals(this.selected) || selectedLocal != this.selectedLocal) {
                  restart(this.cardReveal);
                  restart(this.asideWave);
               }

               this.party.clearSelection();
               this.selected = selected;
               this.selectedLocal = selectedLocal;
               this.resetAsideScroll();
               this.invalidate();
            }
         },
         null
      );
   }

   public void selectFriendFromParty(String selected) {
      if (!selected.equals(this.selected) || this.selectedLocal) {
         restart(this.cardReveal);
         restart(this.asideWave);
      }

      this.selected = selected;
      this.selectedLocal = false;
      this.resetAsideScroll();
      this.invalidate();
   }

   public void partySelectionChanged() {
      this.selected = null;
      restart(this.asideWave);
      this.resetAsideScroll();
   }

   public void partyNotice(String flashText) {
      this.flashMessage(flashText);
   }

   public String selectedFriendName() {
      return this.selectedLocal ? null : this.selected;
   }

   private String cardNote(String selected, boolean selectedLocal) {
      if (selectedLocal) {
         return t(this.isNearby(selected) ? "gui.friends.nearbyWorld" : "gui.friends.localFriend");
      } else {
         CloudConnectionState cloudConnectionState = state();
         PartyPlayerInfo partyPlayerInfo = peer(selected);
         if (partyPlayerInfo != null) {
            String str = presenceNote(partyPlayerInfo.uid());
            return str.isEmpty() ? whereLine(selected) : str + " · " + whereLine(selected);
         } else {
            return cloudConnectionState != null && cloudConnectionState.method8061(selected) ? t("gui.friends.status.pendingLong") : lastSeen(selected);
         }
      }
   }

   private static String presenceNote(long uid) {
      Cloud cloud2 = cloud();
      if (cloud2 != null && cloud2.getPartyMember().method1142(uid)) {
         PartyMember.Data2 data2 = cloud2.getPartyMember().method1141(uid);
         boolean flag = "online".equals(data2.getText()) || "menu".equals(data2.getText());
         if (flag && data2.text().isEmpty()) {
            return "";
         } else {
            return flag ? data2.text() : PartyManager.method52(data2);
         }
      } else {
         return "";
      }
   }

   private static String sideNote(String selected) {
      PartyPlayerInfo partyPlayerInfo = peer(selected);
      Cloud cloud2 = cloud();
      if (partyPlayerInfo != null && cloud2 != null) {
         return partyPlayerInfo.method3291(cloud2.method7911(), cloud2.method7913()) ? distanceTo(selected) : "";
      } else {
         return "";
      }
   }

   private float appear(String key) {
      return this.buttonHover.computeIfAbsent("appear:" + key, str -> new Animation(240L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
   }

   private void drawStatusDot(MenuDrawContext customDrawContext, String key, String selected, boolean selectedLocal, float floatValue, float y, float previewAlpha, ZenithStyle zenithStyle) {
      float hover = this.barFill
         .computeIfAbsent("dot:" + key, str -> new Animation(320L, 0.0F, Easing.INSTANCE13))
         .method2808(selectedLocal ? (this.isNearby(selected) ? 1.0F : 0.0F) : statusLit(selected));
      ZenithColor zenithColor = selectedLocal ? zenithStyle.getFriendColor().getZenithColor() : statusColor(selected, zenithStyle);
      CloudConnectionState cloudConnectionState = state();
      if (!selectedLocal && cloudConnectionState != null && cloudConnectionState.method8061(selected) && peer(selected) == null) {
         hover *= 0.45F + 0.55F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 420.0));
      }

      float width = 6.0F;
      customDrawContext.drawRoundedRect(
         floatValue - 1.0F,
         y - 1.0F,
         width,
         width,
         ProxyDotRadiusData.method8069(width / 2.0F),
         zenithStyle.getRightBackground().getZenithColor().method7292(1.0F).method7294(previewAlpha)
      );
      customDrawContext.drawRoundedRect(
         floatValue, y, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), zenithStyle.getTextTertiary().getZenithColor().method7295(zenithColor, hover).method7294(previewAlpha)
      );
   }

   private void avatar(MenuDrawContext customDrawContext, String selected, float floatValue, float y, float height, float previewAlpha, ZenithStyle zenithStyle) {
      this.avatar(customDrawContext, selected, 0L, floatValue, y, height, previewAlpha, zenithStyle);
   }

   private void avatar(MenuDrawContext customDrawContext, String text, long longValue, float floatValue, float y, float height, float previewAlpha, ZenithStyle zenithStyle) {
      long key = longValue > 0L ? longValue : uidOf(text);
      Identifier texture = key > 0L ? CloudAvatarCache.get(Long.toString(key)) : null;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(height / 2.0F);
      if (texture != null) {
         float f = this.avatarFade.computeIfAbsent(key, l -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
         if (f < 1.0F) {
            customDrawContext.drawRoundedRect(floatValue, y, height, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 2.4F * (1.0F - f)));
         }

         customDrawContext.drawRoundedTexture(texture, floatValue, y, height, height, headerRadius, ZenithColor.INSTANCE.method7294(previewAlpha * f));
      } else {
         customDrawContext.drawRoundedRect(floatValue, y, height, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 2.4F));
         String upperCase = text != null && !text.isEmpty() ? text.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         Font font = Fonts.NEW_MEDIUM.getFont(height * 0.5F);
         customDrawContext.drawText(
            font,
            upperCase,
            floatValue + (height - font.width(upperCase)) / 2.0F,
            y + (height - font.height()) / 2.0F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   private void submitAdd() {
      String typedName = this.typedName();
      if (typedName.isEmpty()) {
         this.setNotice(t("gui.friends.notice.empty"), "", false);
      } else if (this.isAlreadyFriend(typedName)) {
         this.setNotice(t("gui.friends.notice.already").replace("{n}", typedName), "", false);
      } else {
         UserSearchClient.Data exactMatch = this.exactMatch(typedName);
         if (exactMatch != null) {
            this.addCloud(exactMatch);
         } else if (UserSearchClient.method4759(typedName)) {
            this.setNotice(t("gui.friends.notice.short"), "", false);
         } else if (!this.searching && typedName.equalsIgnoreCase(this.suggestFor)) {
            this.setNotice(t("gui.friends.notice.notFound").replace("{n}", typedName), typedName, true);
         } else {
            this.submitWhenReady = true;
            this.setNotice(t("gui.friends.notice.searching").replace("{n}", typedName), "", false);
            this.askDirectory(typedName);
         }
      }
   }

   private void addCloud(UserSearchClient.Data exactMatch) {
      Cloud cloud2 = cloud();
      if (cloud2 != null) {
         cloud2.method7904(0, exactMatch.name());
         if (cloud2.prefs() != null) {
            cloud2.prefs().method3312(exactMatch.name(), exactMatch.uid());
         }
      }

      this.setNotice((cloud2 != null && cloud2.method7862() ? t("gui.friends.notice.sent") : t("gui.friends.notice.queued")).replace("{n}", exactMatch.name()), "", false);
      this.clearInput();
      UserSearchClient.method4762();
      this.invalidate();
   }

   private void addBlind(String name) {
      Cloud cloud2 = cloud();
      if (cloud2 != null && cloud2.method7904(0, name)) {
         this.setNotice((cloud2.method7862() ? t("gui.friends.notice.sent") : t("gui.friends.notice.queued")).replace("{n}", name), "", false);
         this.clearInput();
         this.invalidate();
      } else {
         this.setNotice(t("gui.friends.notice.already").replace("{n}", name), "", false);
      }
   }

   private void addLocal(String name) {
      Zenith.getInstance().getFriends().add(name);
      Zenith.getInstance().getFriends().save();
      this.setNotice(t("gui.friends.notice.addedLocal").replace("{n}", name), "", false);
      this.clearInput();
      this.invalidate();
   }

   private void clearInput() {
      this.addTargetSetting.setValue("");
      this.typed = "";
      this.suggestFor = "";
      this.suggestions = List.of();
      this.submitWhenReady = false;
   }

   private String typedName() {
      String str = this.addTargetSetting.getValue();
      return str == null ? "" : str.strip();
   }

   private UserSearchClient.Data exactMatch(String typedName) {
      for (UserSearchClient.Data suggestion : this.suggestions) {
         if (suggestion.name().equalsIgnoreCase(typedName)) {
            return suggestion;
         }
      }

      return null;
   }

   private void pollTyping() {
      String typedName = this.typedName();
      if (!typedName.equals(this.typed)) {
         this.typed = typedName;
         this.typedAt = System.currentTimeMillis();
         if (typedName.isEmpty()) {
            this.suggestions = List.of();
            this.suggestFor = "";
            this.invalidate();
         }
      } else if (!typedName.isEmpty()
         && !this.searching
         && !UserSearchClient.method4759(typedName)
         && !typedName.equalsIgnoreCase(this.suggestFor)
         && System.currentTimeMillis() - this.typedAt >= 260L) {
         this.askDirectory(typedName);
      }
   }

   private void askDirectory(String typedName) {
      var datas = UserSearchClient.method4758(typedName);
      if (datas != null) {
         this.applyDirectory(typedName, datas, null);
      } else {
         this.searching = true;
         UserSearchClient.method4760(typedName, 6)
            .whenComplete((var2x, throwable) -> mc.execute(() -> this.applyDirectory(typedName, (List<UserSearchClient.Data>)var2x, throwable)));
      }
   }

   private void applyDirectory(String suggestFor, List<UserSearchClient.Data> datas, Throwable throwable) {
      this.searching = false;
      this.suggestFor = suggestFor;
      this.directoryAnswered = throwable == null;
      boolean flag = !sameNames(this.suggestions, datas);
      this.suggestions = datas == null ? List.of() : datas;
      if (flag) {
         restart(this.suggestWave);
      }

      if (throwable != null) {
         this.suggestions = List.of();
         if (this.submitWhenReady) {
            this.setNotice(t("gui.friends.notice.searchFailed"), suggestFor, true);
         }

         this.submitWhenReady = false;
         this.invalidate();
      } else {
         if (this.submitWhenReady) {
            this.submitWhenReady = false;
            UserSearchClient.Data exactMatch = this.exactMatch(suggestFor);
            if (exactMatch != null) {
               this.addCloud(exactMatch);
               return;
            }

            this.setNotice(t("gui.friends.notice.notFound").replace("{n}", suggestFor), suggestFor, true);
         }

         this.invalidate();
      }
   }

   private void buildSuggestions() {
      String typedName = this.typedName();
      if (!typedName.isEmpty() && !UserSearchClient.method4759(typedName)) {
         List<FriendsSectionPanel.Hint> ordered = this.ordered(typedName);
         if (ordered.isEmpty()) {
            if (this.searching) {
               this.searchingRow();
            }
         } else {
            int n = 0;

            for (FriendsSectionPanel.Hint hint : ordered) {
               this.suggestionCard(hint, typedName, n++);
            }

            if (this.searching) {
               this.searchingRow();
            }
         }
      }
   }

   private List<FriendsSectionPanel.Hint> ordered(String typedName) {
      String lowerCase = typedName.toLowerCase(Locale.ROOT);
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      var onlineNames = this.onlineNames();

      for (UserSearchClient.Data suggestion : this.suggestions) {
         boolean flag = !onlineNames.contains(suggestion.name().toLowerCase(Locale.ROOT));
         this.offer(new FriendsSectionPanel.Hint(suggestion.name(), flag ? "gui.friends.hint.offline" : "", suggestion.uid(), 0), lowerCase, linkedHashSet, arrayList, arrayList2);
      }

      CloudConnectionState cloudConnectionState = state();
      if (cloudConnectionState != null) {
         for (PartyPlayerInfo partyPlayerInfo : cloudConnectionState.method8038()) {
            this.offer(new FriendsSectionPanel.Hint(partyPlayerInfo.username(), "", partyPlayerInfo.uid(), 0), lowerCase, linkedHashSet, arrayList, arrayList2);
         }
      }

      for (String serverPlayer : this.serverPlayers()) {
         this.offer(new FriendsSectionPanel.Hint(serverPlayer, "", 0L, 1), lowerCase, linkedHashSet, arrayList, arrayList2);
      }

      arrayList.addAll(arrayList2);
      Object subList = arrayList.size() > 6 ? arrayList.subList(0, 6) : arrayList;
      FriendsSectionPanel.Hint localHint = this.localHint(typedName, linkedHashSet);
      if (localHint == null) {
         return (List<FriendsSectionPanel.Hint>)subList;
      } else {
         var arrayList3 = new ArrayList<>((Collection)subList);
         arrayList3.add(localHint);
         return arrayList3;
      }
   }

   private FriendsSectionPanel.Hint localHint(String typedName, Set<String> strings) {
      String name = typedName.strip();
      if (!name.isEmpty() && !this.isSelf(name) && !this.isAlreadyFriend(name) && !strings.contains(name.toLowerCase(Locale.ROOT))) {
         boolean flag = this.directoryAnswered && !this.searching && name.equalsIgnoreCase(this.suggestFor);
         return new FriendsSectionPanel.Hint(name, flag ? "gui.friends.hint.local" : "gui.friends.hint.unchecked", 0L, -1);
      } else {
         return null;
      }
   }

   private Set<String> onlineNames() {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      CloudConnectionState cloudConnectionState = state();
      if (cloudConnectionState != null) {
         for (PartyPlayerInfo partyPlayerInfo : cloudConnectionState.method8038()) {
            if (partyPlayerInfo.username() != null) {
               linkedHashSet.add(partyPlayerInfo.username().toLowerCase(Locale.ROOT));
            }
         }
      }

      for (String serverPlayer : this.serverPlayers()) {
         linkedHashSet.add(serverPlayer.toLowerCase(Locale.ROOT));
      }

      return linkedHashSet;
   }

   private void offer(FriendsSectionPanel.Hint hint, String lowerCase, Set<String> strings, List<FriendsSectionPanel.Hint> hints, List<FriendsSectionPanel.Hint> hints2) {
      String name = hint.name();
      if (name != null && !name.isBlank() && !this.isAlreadyFriend(name)) {
         String str = name.toLowerCase(Locale.ROOT);
         if (!this.isSelf(name) && strings.add(str)) {
            if (str.startsWith(lowerCase)) {
               hints.add(hint);
            } else if (str.contains(lowerCase)) {
               hints2.add(hint);
            }
         }
      }
   }

   private boolean isSelf(String name) {
      return mc.getUser() != null && name.equalsIgnoreCase(mc.getUser().getName());
   }

   private List<String> serverPlayers() {
      if (mc.getConnection() == null) {
         return List.of();
      } else {
         ArrayList<String> strings = new ArrayList<>();

         for (PlayerInfo onlinePlayer : mc.getConnection().getOnlinePlayers()) {
            String name = onlinePlayer.getProfile() == null ? null : onlinePlayer.getProfile().name();
            if (name != null && !name.isBlank()) {
               strings.add(name);
            }
         }

         return strings;
      }
   }

   private void suggestionCard(FriendsSectionPanel.Hint hint, String typedName, int n) {
      String key = "suggest:" + hint.name();
      this.custom(
         26.0F,
         (customDrawContext, lastClickX, lastClickY, f, f2, f3, f4, zenithStyle) -> {
            float wave2 = wave(this.suggestWave, n);
            if (!(wave2 <= 0.002F)) {
               float previewAlpha = f4 * wave2;
               float floatValue = f + 8.0F + (1.0F - wave2) * 6.0F;
               float width = f3 - 16.0F - (1.0F - wave2) * 6.0F;
               float y = f2 + 1.5F;
               float height = 23.0F;
               boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
               plate(customDrawContext, floatValue, y, width, height, this.hover(key, contains), 0.0F, previewAlpha, zenithStyle);
               if (contains) {
                  HoverInfo.request(
                     hint.name(), t(hint.addKind() == -1 ? "gui.friends.suggest.descLocal" : "gui.friends.suggest.desc").replace("{n}", hint.name())
                  );
               }

               float f5 = y + height / 2.0F;
               float floatValue2 = floatValue + 7.0F;
               this.avatar(customDrawContext, hint.name(), hint.uid(), floatValue2, f5 - 8.0F, 16.0F, previewAlpha, zenithStyle);
               Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font font2 = Fonts.NEW_MEDIUM.getFont(4.4F);
               String text = t(hint.addKind() == -1 ? "gui.friends.suggest.addLocal" : "gui.friends.suggest.add");
               float width2 = font2.width(text) + 11.0F;
               float height2 = 11.0F;
               BoundsData boundsData = new BoundsData(floatValue + width - 6.0F - width2, f5 - height2 / 2.0F, width2, height2);
               float hover = this.hover(key + ":pill", contains);
               ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
               customDrawContext.drawRoundedRect(
                  boundsData.getFloatValue(),
                  boundsData.getFloatValue2(),
                  boundsData.width(),
                  boundsData.height(),
                  ProxyDotRadiusData.method8069(height2 / 2.0F),
                  zenithColor.method7292(0.12F + hover * 0.7F).method7294(previewAlpha)
               );
               customDrawContext.drawText(
                  font2,
                  text,
                  boundsData.getFloatValue() + (boundsData.width() - font2.width(text)) / 2.0F,
                  boundsData.getFloatValue2() + (height2 - font2.height()) / 2.0F,
                  zenithColor.method7295(new ZenithColor(255, 255, 255), hover).method7294(previewAlpha)
               );
               Font font3 = Fonts.NEW_REGULAR.getFont(4.2F);
               String str = hint.source().isEmpty() ? "" : t(hint.source());
               float width3 = str.isEmpty() ? 0.0F : font3.width(str);
               if (!str.isEmpty()) {
                  customDrawContext.drawText(
                     font3,
                     str,
                     boundsData.getFloatValue() - 5.0F - width3,
                     f5 - font3.height() / 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F)
                  );
               }

               float floatValue3 = floatValue2 + 16.0F + 6.0F;
               float f6 = boundsData.getFloatValue() - 9.0F - width3 - floatValue3;
               this.drawMatchedName(customDrawContext, font, hint.name(), typedName, floatValue3, f5 - font.height() / 2.0F, f6, previewAlpha, zenithStyle);
            }
         },
         () -> {
            this.press(key + ":pill");
            this.addHint(hint);
         },
         null
      );
   }

   private void addHint(FriendsSectionPanel.Hint hint) {
      if (hint.addKind() == -1) {
         this.addLocal(hint.name());
      } else {
         Cloud cloud2 = cloud();
         if (cloud2 != null) {
            if (hint.addKind() == 0) {
               if (hint.uid() > 0L && cloud2.prefs() != null) {
                  cloud2.prefs().method3312(hint.name(), hint.uid());
               }

               this.addBlind(hint.name());
               UserSearchClient.method4762();
            } else if (!cloud2.method7904(1, hint.name())) {
               this.setNotice(t("gui.friends.notice.offlineCloud"), hint.name(), true);
            } else {
               this.setNotice(t("gui.friends.notice.sent").replace("{n}", hint.name()), "", false);
               this.clearInput();
               this.invalidate();
            }
         }
      }
   }

   private void drawMatchedName(MenuDrawContext customDrawContext, Font font, String name, String typedName, float floatValue, float centeredTextY, float width, float previewAlpha, ZenithStyle zenithStyle) {
      ZenithColor zenithColor = zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
      String text = fit(font, name, width);
      int length = !typedName.isEmpty() && text.toLowerCase(Locale.ROOT).startsWith(typedName.toLowerCase(Locale.ROOT)) ? typedName.length() : 0;
      if (length != 0 && length <= text.length()) {
         String str = text.substring(0, length);
         customDrawContext.drawText(font, str, floatValue, centeredTextY, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
         customDrawContext.drawText(font, text.substring(length), floatValue + font.width(str), centeredTextY, zenithColor);
      } else {
         customDrawContext.drawText(font, text, floatValue, centeredTextY, zenithColor);
      }
   }

   private void searchingRow() {
      this.custom(
         16.0F,
         (menuDrawContext, n, n2, f, f2, f3, f4, zenithStyle) -> {
            Font font = Fonts.NEW_REGULAR.getFont(4.8F);
            String text = t("gui.friends.suggest.searching");
            float f5 = 0.55F + 0.45F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 260.0));
            menuDrawContext.drawText(font, text, f + 8.0F, f2 + (16.0F - font.height()) / 2.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(f4 * f5));
            float width = 3.0F;
            float f6 = f + 8.0F + font.width(text) + 4.0F;

            for (int i = 0; i < 3; i++) {
               float f7 = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 220.0 - i * 0.7));
               menuDrawContext.drawRoundedRect(
                  f6 + i * (width + 2.0F),
                  f2 + (16.0F - width) / 2.0F,
                  width,
                  width,
                  ProxyDotRadiusData.method8069(width / 2.0F),
                  zenithStyle.getPrimaryColor().getZenithColor().method7294(f4 * (0.25F + 0.55F * f7))
               );
            }
         }
      );
   }

   private void buildNotice() {
      if (!this.noticeText.isEmpty()) {
         this.noticeBox(this.noticeText);
         if (this.offerOffline) {
            String noticeTarget = this.noticeTarget;
            this.buttonsRow(
               "notice.actions", "gui.friends.notice.addLocal", () -> this.addLocal(noticeTarget), "gui.friends.notice.sendCloud", () -> this.addBlind(noticeTarget)
            );
         }
      }
   }

   private void setNotice(String noticeText, String noticeTarget, boolean flag) {
      this.noticeText = noticeText;
      this.noticeTarget = noticeTarget;
      this.offerOffline = flag && !noticeTarget.isEmpty();
      this.invalidate();
   }

   private static boolean sameNames(List<UserSearchClient.Data> suggestions, List<UserSearchClient.Data> datas) {
      var list = datas == null ? List.of() : datas;
      if (suggestions.size() != list.size()) {
         return false;
      } else {
         for (int i = 0; i < list.size(); i++) {
            if (!((UserSearchClient.Data)suggestions.get(i)).name().equals(((UserSearchClient.Data)list.get(i)).name())) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isAlreadyFriend(String name) {
      Cloud cloud2 = cloud();
      if (cloud2 != null && cloud2.getFriends().method6442(name)) {
         return true;
      } else {
         var items = Zenith.getInstance().getFriends().getItems();
         if (items == null) {
            return false;
         } else {
            for (String str : List.copyOf(items)) {
               if (str.equalsIgnoreCase(name)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private void buildSharing() {
      Share share = Zenith.getInstance().getShare();
      this.shareHero(share);
      this.caption("gui.friends.caption.cosmetics");
      this.shareTiles(share);
      this.caption("gui.friends.caption.shareWhat");
      this.shareToggle(share.position, MenuGlyph.GPS);
      this.shareToggle(share.server, MenuGlyph.WORLD);
      this.shareToggle(share.health, MenuGlyph.HEART);
      this.shareToggle(share.inventory, MenuGlyph.CHESTPLATE);
      this.caption("gui.friends.caption.social");
      this.shareToggle(share.allowJoin, MenuGlyph.WORLD);
      this.shareToggle(share.deaths, MenuGlyph.PIN);
      this.shareToggle(share.stats, MenuGlyph.TIMER);
      this.caption("gui.friends.caption.presence");
      this.shareToggle(share.autoPvp, MenuGlyph.COMBAT);
      this.setting(share.autoAfk);
      this.caption("gui.friends.caption.notify");
      this.shareToggle(share.notifyMessages, MenuGlyph.CHAT);
      this.shareToggle(share.notifyInvites, MenuGlyph.FRIENDS);
      this.caption("gui.friends.caption.accent");
      this.setting(share.colorSetting);
      this.hint(t("gui.friends.share.hint"));
      this.buildCloudAside();
      this.asideHint(t("gui.friends.share.private"));
   }

   private void shareHero(Share share) {
      int n = this.order++;
      this.custom(
         40.0F,
         (customDrawContext, n2, n3, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.listWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float floatValue = f + 8.0F;
               float f4 = f + f3 - 8.0F;
               float y = f2 + 3.0F;
               float height = 34.0F;
               plate(customDrawContext, floatValue, y, f4 - floatValue, height, 0.0F, 0.0F, previewAlpha, zenithStyle);
               bigGlyph(customDrawContext, MenuGlyph.SHIELD, floatValue + 8.0F, y + (height - 12.0F) / 2.0F, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
               Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.6F);
               float floatValue2 = floatValue + 8.0F + 12.0F + 8.0F;
               float centeredTextY = y + (height - font.height() - 3.0F - font2.height()) / 2.0F;
               customDrawContext.drawText(font, t("gui.friends.share.title"), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               customDrawContext.drawText(
                  font2,
                  fit(font2, shareSummary(share), f4 - 8.0F - floatValue2),
                  floatValue2,
                  centeredTextY + font.height() + 3.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
            }
         }
      );
   }

   private static String shareSummary(Share share) {
      ArrayList elements = new ArrayList();
      if (share.position.isEnabled()) {
         elements.add(t("cloud.share.position").toLowerCase(Locale.ROOT));
      }

      if (share.server.isEnabled()) {
         elements.add(t("cloud.share.server").toLowerCase(Locale.ROOT));
      }

      if (share.health.isEnabled()) {
         elements.add(t("cloud.share.health").toLowerCase(Locale.ROOT));
      }

      if (share.inventory.isEnabled()) {
         elements.add(t("cloud.share.inventory").toLowerCase(Locale.ROOT));
      }

      return elements.isEmpty() ? t("gui.friends.share.summary.none") : t("gui.friends.share.summary").replace("{list}", String.join(", ", elements));
   }

   private void shareTiles(Share share) {
      int n = this.order++;
      String key = "cosmetics";
      String[][] stringses = new String[][]{
         {"cloud.share.cosmeticsVisibility.everyone", "gui.friends.share.everyone.note"},
         {"cloud.share.cosmeticsVisibility.friends", "gui.friends.share.friends.note"},
         {"cloud.share.cosmeticsVisibility.nobody", "gui.friends.share.nobody.note"}
      };
      float customHeight = 28.0F;
      this.custom(
         customHeight,
         (customDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.listWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float f4 = 4.0F;
               float f5 = f + 8.0F;
               float width = (f3 - 16.0F - f4 * 2.0F) / 3.0F;
               int n2 = -1;

               for (int i = 0; i < 3; i++) {
                  float floatValue = f5 + i * (width + f4);
                  float y = f2 + 2.0F;
                  float height = customHeight - 4.0F;
                  boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
                  if (contains) {
                     n2 = i;
                     HoverInfo.request(t("cloud.share.cosmeticsVisibility"), t(stringses[i][1]));
                  }

                  float pick = this.pick(key + i, share.cosmeticsVisibility.getIndex() == i);
                  plate(customDrawContext, floatValue, y, width, height, this.hover(key + i, contains), pick, previewAlpha, zenithStyle);
                  Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
                  Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
                  String name = t(stringses[i][0]);
                  name = name.isEmpty() ? name : name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
                  customDrawContext.drawText(
                     font,
                     fit(font, name, width - 10.0F),
                     floatValue + 5.0F,
                     y + 4.0F,
                     zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), pick).method7294(previewAlpha)
                  );
                  customDrawContext.drawText(
                     font2,
                     fit(font2, t(stringses[i][1]), width - 10.0F),
                     floatValue + 5.0F,
                     y + 4.0F + font.height() + 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
                  );
               }

               this.hoveredButton.put(key, n2);
            }
         },
         () -> {
            int var3x = this.hoveredButton.getOrDefault(key, -1);
            if (var3x >= 0) {
               share.cosmeticsVisibility.getValues().get(var3x).method2103();
               this.invalidate();
            }
         },
         null
      );
   }

   private void shareToggle(BooleanSetting position, MenuGlyph menuGlyph) {
      int n = this.order++;
      String key = "share:" + position.getKey();
      this.custom(
         28.0F,
         (customDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.listWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 1.5F;
               float height = 25.0F;
               boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
               float pick = this.pick(key, position.isEnabled());
               plate(customDrawContext, floatValue, y, width, height, this.hover(key, contains), 0.0F, previewAlpha, zenithStyle);
               if (contains) {
                  HoverInfo.request(t(position.getKey()), t(position.getKey() + ".desc"));
               }

               float width2 = 14.0F;
               float floatValue2 = floatValue + 7.0F;
               float y2 = y + (height - width2) / 2.0F;
               customDrawContext.drawRoundedRect(
                  floatValue2,
                  y2,
                  width2,
                  width2,
                  ProxyDotRadiusData.method8069(4.0F),
                  zenithStyle.getTextTertiary()
                     .getZenithColor()
                     .method7294(0.18F)
                     .method7295(zenithStyle.getPrimaryColor().getZenithColor().method7294(0.25F), pick)
                     .method7294(previewAlpha)
               );
               menuGlyph.draw(
                  customDrawContext,
                  floatValue2 + (width2 - 6.0F) / 2.0F,
                  y2 + (width2 - 6.0F) / 2.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), pick).method7294(previewAlpha)
               );
               Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
               float floatValue3 = floatValue2 + width2 + 6.0F;
               float width3 = 18.0F;
               float width4 = floatValue + width - 8.0F - width3 - 6.0F - floatValue3;
               float centeredTextY = y + (height - font.height() - 2.0F - font2.height()) / 2.0F;
               customDrawContext.drawText(font, fit(font, t(position.getKey()), width4), floatValue3, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               customDrawContext.drawText(
                  font2,
                  fit(font2, t(position.getKey() + ".desc"), width4),
                  floatValue3,
                  centeredTextY + font.height() + 2.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               this.drawToggle(customDrawContext, floatValue + width - 8.0F - width3, y + (height - 9.0F) / 2.0F, width3, 9.0F, pick, previewAlpha, zenithStyle);
            }
         },
         () -> {
            position.setEnabled(!position.isEnabled());
            this.invalidate();
         },
         null
      );
   }

   private void buildFriendAside() {
      if (this.party.hasSelection()) {
         this.flashRow();
         this.party.buildAside(cloud());
      } else if (this.selected == null) {
         this.flashRow();
         this.buildMeAside();
         this.buildCloudAside();
         this.asideHint(t("gui.friends.aside.pick"));
      } else {
         if (this.selectedLocal) {
            this.buildLocalAside(this.selected);
         } else {
            this.buildCloudFriendAside(this.selected);
         }
      }
   }

   private void buildMeAside() {
      this.asideCaption("gui.friends.me.caption");
      int n = this.asideOrder++;
      String key = "me";
      this.asideCustom(
         48.0F,
         (var3x, lastClickX, lastClickY, f, y, f2, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.asideWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 6.0F;
               String str = accountName();
               String name = mc.getUser() == null ? "" : mc.getUser().getName();
               long longValue = accountUid();
               float floatValue = f + 8.0F;
               this.avatar(var3x, str.isEmpty() ? name : str, longValue, floatValue, y, 26.0F, previewAlpha, zenithStyle);
               Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.6F);
               float floatValue2 = floatValue + 26.0F + 6.0F;
               float width = f + f2 - 8.0F - floatValue2;
               var3x.drawText(
                  font, fit(font, str.isEmpty() ? name : str, width), floatValue2, y + 4.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
               );
               var3x.drawText(
                  font2,
                  fit(font2, t("gui.friends.me.mc") + ": " + blankToDash(name), width),
                  floatValue2,
                  y + 4.0F + font.height() + 3.0F,
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               Font font3 = Fonts.NEW_MEDIUM.getFont(4.8F);
               boolean flag = this.flashing() && this.flashText.equals(t("gui.friends.copied"));
               String text = t(flag ? "gui.friends.copied" : "gui.friends.me.copy");
               BoundsData boundsData = new BoundsData(floatValue, y + 26.0F + 5.0F, f2 - 16.0F, 14.0F);
               boolean contains = boundsData.contains(lastClickX, lastClickY);
               this.hoveredButton.put(key, contains ? 0 : -1);
               if (contains) {
                  HoverInfo.request(t("gui.friends.me.copy"), t("gui.friends.me.hint"));
               }

               this.pillButton(var3x, key, boundsData, text, true, contains, previewAlpha, zenithStyle);
               if (flag) {
                  MenuGlyph.CHECK
                     .draw(
                        var3x,
                        boundsData.getFloatValue() + (boundsData.width() - font3.width(text)) / 2.0F - 9.0F,
                        boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F,
                        zenithStyle.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), this.hover(key, contains)).method7294(previewAlpha)
                     );
               }
            }
         },
         () -> {
            if (this.hoveredButton.getOrDefault("me", -1) == 0) {
               String var1x = accountName();
               this.copy(var1x.isEmpty() ? (mc.getUser() == null ? "" : mc.getUser().getName()) : var1x);
            }
         },
         null
      );
      Share share = Zenith.getInstance().getShare();
      this.asideCaption("gui.friends.caption.presence");
      this.asideInfo("gui.friends.presence.now", () -> {
         PartyManager partyManager = PartyManager.method17();
         return partyManager == null ? "-" : PartyManager.method50(partyManager.method31());
      }, "gui.friends.presence.now.desc");
      this.asideSetting(share.status);
      this.asideSetting(share.text);
   }

   private void buildCloudAside() {
      this.asideCaption("gui.friends.caption.cloud");
      this.asideInfo("gui.friends.link", () -> {
         CloudConnectionState cloudConnectionState = state();
         return cloudConnectionState == null ? t("gui.cloud.state.missing") : cloudConnectionState.getType().label();
      }, "gui.friends.link.desc");
      this.asideInfo("gui.friends.onlineCount", () -> {
         CloudConnectionState cloudConnectionState = state();
         return cloudConnectionState != null && cloudConnectionState.method8035() ? cloudConnectionState.method8045() + " / " + cloudConnectionState.getIntValue() : "-";
      }, "gui.friends.onlineCount.desc");
      CloudConnectionState cloudConnectionState = state();
      if (cloudConnectionState == null || !cloudConnectionState.method8035()) {
         this.asideInfo("gui.friends.reason", () -> {
            CloudConnectionState state2 = state();
            if (state2 == null) {
               return t("gui.friends.reason.down");
            } else if (state2.method8035()) {
               return "-";
            } else {
               String var1x = state2.note();
               if (var1x == null || var1x.isBlank()) {
                  var1x = state2.getLastError();
               }

               return var1x != null && !var1x.isBlank() ? var1x : state2.getType().label();
            }
         }, "gui.friends.reason.desc");
         this.asideAction("gui.friends.reconnect", MenuGlyph.ACTION, () -> t("gui.friends.reconnect.value"), () -> {
            Cloud cloud2 = cloud();
            if (cloud2 != null) {
               cloud2.connect();
            }
         }, "gui.friends.reconnect.desc");
      }
   }

   private void buildCloudFriendAside(String selected) {
      PartyPlayerInfo partyPlayerInfo = peer(selected);
      Cloud cloud2 = cloud();
      this.profileHead(selected, whereLine(selected), false);
      this.actionTiles(selected, partyPlayerInfo);
      this.flashRow();
      if (partyPlayerInfo != null && cloud2 != null) {
         this.party.leaderActions(cloud2, partyPlayerInfo.uid());
      }

      this.asideCaption("gui.friends.caption.now");
      this.asideInfo("gui.friends.status", () -> statusValue(selected, partyPlayerInfo), "gui.friends.status.desc");
      if (partyPlayerInfo != null) {
         this.asideInfo("gui.friends.playingOn", () -> {
            Cloud var1x = cloud();
            String var2x = var1x == null ? "" : var1x.getPartyMember().method1141(partyPlayerInfo.uid()).mode();
            return blankToDash(ServerNameUtils.method5643(partyPlayerInfo.server(), var2x));
         }, "gui.friends.playingOn.desc");
         this.asideInfo("gui.friends.onlineFor", () -> onlineFor(selected), "gui.friends.onlineFor.desc");
         if (cloud2 != null && partyPlayerInfo.method3291(cloud2.method7911(), cloud2.method7913())) {
            this.asideInfo("gui.friends.distance", () -> distanceTo(selected), "gui.friends.distance.desc");
         }
      } else {
         this.asideInfo("gui.friends.lastSeen", () -> lastSeen(selected), "gui.friends.lastSeen.desc");
      }

      String str = rememberedMcName(selected);
      if (!str.isEmpty()) {
         this.asideInfo("gui.friends.mcName", () -> str, "gui.friends.mcName.desc");
      }

      if (partyPlayerInfo != null) {
         this.asideCaption("gui.friends.caption.state");
         this.bar(selected, t("gui.friends.health"), () -> partyPlayerInfo.getFloatValue3(), () -> Math.max(1.0F, partyPlayerInfo.getFloatValue4()), zenithStyle -> DANGER);
         this.bar(selected, t("gui.friends.food"), () -> (float)partyPlayerInfo.getIntValue(), () -> 20.0F, zenithStyle -> new ZenithColor(-680437));
         List<ItemStack> stacks = equipment(partyPlayerInfo);
         if (!stacks.isEmpty()) {
            this.asideCaption("gui.friends.caption.gear");
            this.asideCustom(
               16.0F + GuiStyle.PADDING.intValue() + 2.0F,
               (var1x, var2x, var3x, var4x, var5x, f, f2, zenithStyle) -> {
                  float x = var4x + 8.0F;

                  for (ItemStack itemStack : stacks) {
                     var1x.drawRoundedRect(
                        x - 1.0F,
                        var5x - 1.0F,
                        18.0F,
                        18.0F,
                        ProxyDotRadiusData.method8069(3.0F),
                        zenithStyle.getDisableActiveBg().getZenithColor().method7294(f2 * 0.6F)
                     );
                     var1x.pose().pushMatrix();
                     var1x.pose().translate(x, var5x);
                     var1x.item(itemStack, 0, 0);
                     var1x.renderItemBar(itemStack, 0, 0);
                     var1x.pose().popMatrix();
                     x += 20.0F;
                  }
               }
            );
         }

         if (!partyPlayerInfo.getItems().isEmpty()) {
            this.asideInfo("gui.friends.cosmetics", () -> partyPlayerInfo.getItems().size() + " " + t("gui.friends.cosmetics.items"), "gui.friends.cosmetics.desc");
         }
      }

      this.voiceRows(selected, partyPlayerInfo);
      this.friendActions(selected);
   }

   private void profileHead(String selected, String whereLine, boolean selectedLocal) {
      int n = this.asideOrder++;
      String str = (selectedLocal ? "local:" : "cloud:") + selected;
      this.asideCustom(
         26.0F + GuiStyle.PADDING.intValue() + 2.0F,
         (customDrawContext, n2, n3, f, y, f2, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.asideWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 6.0F;
               float floatValue = f + 8.0F;
               this.avatar(customDrawContext, selected, floatValue, y, 26.0F, previewAlpha, zenithStyle);
               this.drawStatusDot(customDrawContext, "head:" + str, selected, selectedLocal, floatValue + 26.0F - 4.0F - 0.5F, y + 26.0F - 4.0F - 0.5F, previewAlpha, zenithStyle);
               float floatValue2 = floatValue + 26.0F + 6.0F;
               Font font = Fonts.NEW_MEDIUM.getFont(6.4F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.6F);
               float width = f + f2 - 8.0F - floatValue2;
               Prefs prefs2 = prefs();
               boolean flag = !selectedLocal && prefs2 != null && prefs2.method3319(selected);
               float floatValue3 = floatValue2;
               float centeredTextY = y + 4.0F;
               if (flag) {
                  diamond(customDrawContext, floatValue2 + 2.0F, centeredTextY + font.height() / 2.0F, 3.5F, FAVORITE.method7294(previewAlpha));
                  floatValue3 = floatValue2 + 7.0F;
               }

               customDrawContext.drawText(
                  font,
                  fit(font, selectedLocal ? selected : displayName(selected), width - (floatValue3 - floatValue2)),
                  floatValue3,
                  centeredTextY,
                  zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
               );
               customDrawContext.drawText(font2, fit(font2, whereLine, width), floatValue2, centeredTextY + font.height() + 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            }
         }
      );
   }

   private void actionTiles(String selected, PartyPlayerInfo partyPlayerInfo) {
      Cloud cloud2 = cloud();
      PartyManager partyManager = PartyManager.method17();
      ArrayList arrayList = new ArrayList();
      if (!isPendingName(selected)) {
         arrayList.add(
            new FriendsSectionPanel.Tile("chat", MenuGlyph.CHAT, "gui.friends.quick.chat", "gui.friends.quick.chat.desc", false, () -> this.openChat(selected))
         );
      }

      if (partyPlayerInfo != null && partyManager != null && partyManager.method42(partyPlayerInfo.uid())) {
         arrayList.add(new FriendsSectionPanel.Tile("join", MenuGlyph.WORLD, "gui.friends.quick.join", "gui.friends.quick.join.desc", false, () -> {
            this.flashMessage(t("gui.friends.join.going").replace("{n}", displayName(selected)));
            partyManager.method43(partyPlayerInfo.uid());
         }));
      }

      if (partyPlayerInfo != null && cloud2 != null && cloud2.method7860()) {
         PartyMember partyMember = cloud2.getPartyMember();
         boolean active = partyMember.method1137(partyPlayerInfo.uid());
         boolean flag = partyMember.getData6() != null && partyMember.getData6().getItems().size() >= 6;
         if (active || !flag) {
            arrayList.add(
               new FriendsSectionPanel.Tile(
                  "party", MenuGlyph.FRIENDS, active ? "gui.friends.quick.inParty" : "gui.friends.quick.party", "gui.friends.quick.party.desc", active, () -> {
                     if (!partyMember.method1137(partyPlayerInfo.uid())) {
                        partyMember.method1153(partyPlayerInfo.uid());
                        this.flashMessage(t("gui.friends.party.invited").replace("{n}", displayName(selected)));
                     }
                  }
               )
            );
         }
      }

      if (partyPlayerInfo != null && partyPlayerInfo.method3292()) {
         arrayList.add(
            new FriendsSectionPanel.Tile("mark", MenuGlyph.GPS, "gui.friends.quick.mark", "gui.friends.quick.mark.desc", false, () -> this.markFriend(selected))
         );
      }

      if (!arrayList.isEmpty()) {
         int n = this.asideOrder++;
         String key = "tiles:" + selected;
         this.asideCustom(
            34.0F,
            (var4x, var5x, var6x, var7x, var8x, var9x, var10x, zenithStyle) -> {
               float f = wave(this.asideWave, n);
               if (!(f <= 0.002F)) {
                  var10x *= f;
                  var7x += (1.0F - f) * 6.0F;
                  float f2 = 3.0F;
                  float f3 = var7x + 8.0F;
                  float width = (var9x - 16.0F - f2 * (arrayList.size() - 1)) / arrayList.size();
                  int n2 = -1;

                  for (int i = 0; i < arrayList.size(); i++) {
                     FriendsSectionPanel.Tile tile = (FriendsSectionPanel.Tile)arrayList.get(i);
                     BoundsData boundsData = new BoundsData(f3 + i * (width + f2), var8x, width, 30.0F);
                     boolean contains = boundsData.contains(var5x, var6x);
                     if (contains) {
                        n2 = i;
                        HoverInfo.request(t(tile.label()), t(tile.note()));
                     }

                     float hover = this.hover(key + tile.key(), contains);
                     float flash = this.flash(key + tile.key());
                     var4x.drawRoundedRect(
                        boundsData.getFloatValue(),
                        boundsData.getFloatValue2(),
                        boundsData.width(),
                        boundsData.height(),
                        ProxyDotRadiusData.method8069(4.0F),
                        zenithStyle.getDisableActiveBg().getZenithColor().method7294(var10x * (0.5F + 0.3F * hover + 0.3F * flash))
                     );
                     if (tile.active()) {
                        var4x.drawRoundedRect(
                           boundsData.getFloatValue(),
                           boundsData.getFloatValue2(),
                           boundsData.width(),
                           boundsData.height(),
                           ProxyDotRadiusData.method8069(4.0F),
                           zenithStyle.getPrimaryColor().getZenithColor().method7294(var10x * 0.12F)
                        );
                     }

                     ZenithColor zenithColor = zenithStyle.getTextSecondary()
                        .getZenithColor()
                        .method7295(zenithStyle.getPrimaryColor().getZenithColor(), Math.max(hover, tile.active() ? 1.0F : 0.0F))
                        .method7294(var10x);
                     float f4 = hover * -1.0F;
                     tile.icon().draw(var4x, boundsData.getFloatValue() + (boundsData.width() - 6.0F) / 2.0F, boundsData.getFloatValue2() + 6.0F + f4, zenithColor);
                     Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
                     String text = fit(font, t(tile.label()), boundsData.width() - 4.0F);
                     var4x.drawText(
                        font,
                        text,
                        boundsData.getFloatValue() + (boundsData.width() - font.width(text)) / 2.0F,
                        boundsData.getFloatValue2() + boundsData.height() - font.height() - 5.0F,
                        zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(var10x)
                     );
                  }

                  this.hoveredButton.put(key, n2);
               }
            },
            () -> {
               int var3x = this.hoveredButton.getOrDefault(key, -1);
               if (var3x >= 0 && var3x < arrayList.size()) {
                  this.press(key + ((FriendsSectionPanel.Tile)arrayList.get(var3x)).key());
                  ((FriendsSectionPanel.Tile)arrayList.get(var3x)).action().run();
                  this.invalidate();
               }
            },
            null
         );
      }
   }

   private static String statusValue(String selected, PartyPlayerInfo partyPlayerInfo) {
      if (partyPlayerInfo == null) {
         return t(isPendingName(selected) ? "gui.friends.status.pending" : "gui.friends.status.offline");
      } else {
         String str = presenceNote(partyPlayerInfo.uid());
         return str.isEmpty() ? t("gui.friends.status.online") : str;
      }
   }

   private void flashRow() {
      if (this.flashing()) {
         String flashText = this.flashText;
         Font font = Fonts.NEW_REGULAR.getFont(4.6F);
         this.asideCustom(
            font.height() + 6.0F,
            (menuDrawContext, n, n2, f, f2, f3, f4, zenithStyle) -> {
               long l = System.currentTimeMillis() - this.flashAt;
               float f5 = l < 180L ? (float)l / 180.0F : (l > 2200L ? Math.max(0.0F, (float)(2600L - l) / 400.0F) : 1.0F);
               MenuGlyph.CHECK
                  .draw(menuDrawContext, f + 8.0F, f2 + 2.0F + (font.height() - 6.0F) / 2.0F, zenithStyle.getPrimaryColor().getZenithColor().method7294(f4 * f5));
               menuDrawContext.drawText(
                  font,
                  fit(font, flashText, f3 - 16.0F - 9.0F),
                  f + 8.0F + 9.0F,
                  f2 + 2.0F,
                  zenithStyle.getTextSecondary().getZenithColor().method7294(f4 * f5)
               );
            }
         );
      }
   }

   private void markFriend(String selected) {
      PartyPlayerInfo partyPlayerInfo = peer(selected);
      if (partyPlayerInfo != null && partyPlayerInfo.method3292()) {
         String key = CloudWaypointsModule.INSTANCE
            .method6402(partyPlayerInfo.getDoubleValue(), partyPlayerInfo.getDoubleValue2() + 1.0, partyPlayerInfo.getDoubleValue3(), displayName(selected), "", partyPlayerInfo.server(), partyPlayerInfo.dimension());
         this.flashMessage(key.isEmpty() ? t("gui.friends.mark.done").replace("{n}", displayName(selected)) : t(key));
      } else {
         this.flashMessage(t("gui.friends.mark.noPosition").replace("{n}", displayName(selected)));
      }
   }

   private void voiceRows(String voiceVolumeFor, PartyPlayerInfo partyPlayerInfo) {
      Prefs prefs2 = prefs();
      if (prefs2 != null) {
         this.asideCaption("gui.friends.caption.voice");
         if (partyPlayerInfo != null) {
            this.asideInfo(
               "gui.friends.voiceStatus",
               () -> t(partyPlayerInfo.method3289() ? "gui.friends.voiceStatus.on" : "gui.friends.voiceStatus.off"),
               "gui.friends.voiceStatus.desc"
            );
         }

         this.asideAction(
            "gui.friends.voiceMute", MenuGlyph.MIC, () -> t(prefs2.method3321(voiceVolumeFor) ? "gui.friends.voiceMute.on" : "gui.friends.voiceMute.off"), () -> {
               prefs2.method3322(voiceVolumeFor, !prefs2.method3321(voiceVolumeFor));
               this.invalidate();
            }, "gui.friends.voiceMute.desc"
         );
         Cloud cloud2 = cloud();
         if (partyPlayerInfo != null && partyPlayerInfo.method3289() && cloud2 != null && cloud2.method7860()) {
            this.asideAction(
               "gui.friends.quick.whisper",
               MenuGlyph.SOUND,
               () -> t(cloud2.getPartyMember().getLongValue() == partyPlayerInfo.uid() ? "gui.value.enabled" : "gui.value.disabled"),
               () -> {
                  boolean var3x = cloud2.getPartyMember().getLongValue() == partyPlayerInfo.uid();
                  cloud2.getPartyMember().method1176(var3x ? 0L : partyPlayerInfo.uid());
                  this.invalidate();
               },
               "gui.friends.quick.whisper.desc"
            );
         }

         this.voiceVolume = new SliderSetting(
            "gui.friends.voiceVolume", (float)prefs2.method3323(voiceVolumeFor), 0.0F, 200.0F, 5.0F, "gui.friends.voiceVolume.desc", " %", null
         );
         this.voiceVolumeFor = voiceVolumeFor;
         this.asideSetting(this.voiceVolume);
      }
   }

   private void friendActions(String selected) {
      Prefs prefs2 = prefs();
      if (prefs2 != null) {
         CloudConnectionState cloudConnectionState = state();
         boolean flag = cloudConnectionState != null && cloudConnectionState.method8061(selected);
         this.asideCaption("gui.friends.caption.actions");
         PartyManager partyManager = PartyManager.method17();
         PartyPlayerInfo partyPlayerInfo = peer(selected);
         if (partyManager != null && partyPlayerInfo != null && partyManager.method47()) {
            this.asideAction(
               "gui.friends.quick.invite",
               MenuGlyph.ACTION,
               () -> t("gui.friends.inviteHere.value"),
               () -> partyManager.method48(partyPlayerInfo.uid()),
               "gui.friends.quick.invite.desc"
            );
         }

         this.asideAction(
            "gui.friends.favorite", MenuGlyph.PIN, () -> t(prefs2.method3319(selected) ? "gui.friends.favorite.on" : "gui.friends.favorite.off"), () -> {
               prefs2.method3320(selected, !prefs2.method3319(selected));
               this.invalidate();
            }, "gui.friends.favorite.desc"
         );
         this.asideAction("gui.friends.notify", MenuGlyph.MODE, () -> t(prefs2.method3315(selected) ? "gui.value.disabled" : "gui.value.enabled"), () -> {
            prefs2.method3317(selected, !prefs2.method3315(selected));
            this.invalidate();
         }, "gui.friends.notify.desc");
         String str = rememberedMcName(selected);
         this.asideAction(
            "gui.friends.copyNick",
            MenuGlyph.COPY,
            () -> t("gui.friends.copyNick.value"),
            () -> this.copy(str.isEmpty() ? displayName(selected) : str),
            "gui.friends.copyNick.desc"
         );
         this.asideAction("gui.friends.block", MenuGlyph.CLOSE, () -> t(prefs2.method3316(selected) ? "gui.friends.block.on" : "gui.value.no"), () -> {
            prefs2.method3318(selected, !prefs2.method3316(selected));
            this.invalidate();
         }, "gui.friends.block.desc");
         this.dangerButton(
            flag ? "gui.friends.cancelRequest" : "gui.friends.remove", flag ? "gui.friends.cancelRequest.desc" : "gui.friends.remove.desc", () -> {
               Cloud var2x = cloud();
               if (var2x != null) {
                  var2x.method7907(selected);
               }

               this.selected = null;
               this.invalidate();
            }
         );
      }
   }

   private void dangerButton(String key, String str, Runnable runnable) {
      String key2 = "danger:" + key;
      this.asideCustom(
         21.0F,
         (var4x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 3.0F, f3 - 16.0F, 15.0F);
            boolean contains = boundsData.contains(lastClickX, lastClickY);
            this.hoveredButton.put(key2, contains ? 0 : -1);
            if (contains) {
               HoverInfo.request(t(key), t(str));
            }

            float hover = this.hover(key2, contains);
            var4x.drawRoundedRect(
               boundsData.getFloatValue(),
               boundsData.getFloatValue2(),
               boundsData.width(),
               boundsData.height(),
               ProxyDotRadiusData.method8069(boundsData.height() / 2.0F),
               DANGER.method7292(0.1F + hover * 0.18F).method7294(previewAlpha)
            );
            Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
            String text = t(key);
            var4x.drawText(
               font,
               text,
               boundsData.getFloatValue() + (boundsData.width() - font.width(text)) / 2.0F,
               boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
               DANGER.method7295(new ZenithColor(255, 255, 255), hover * 0.4F).method7294(previewAlpha)
            );
         },
         () -> {
            if (this.hoveredButton.getOrDefault(key2, -1) == 0) {
               runnable.run();
            }
         },
         null
      );
   }

   private void openChat(String selected) {
      if (this.openChatWith != null) {
         if (!this.openChatWith.test(selected)) {
            this.flashMessage(t("gui.friends.notice.noChat").replace("{n}", displayName(selected)));
            if (this.selected == null) {
               this.setNotice(t("gui.friends.notice.noChat").replace("{n}", displayName(selected)), "", false);
            }
         }
      }
   }

   private static Prefs prefs() {
      Cloud cloud2 = cloud();
      return cloud2 == null ? null : cloud2.prefs();
   }

   private static String rememberedMcName(String selected) {
      PartyPlayerInfo partyPlayerInfo = peer(selected);
      if (partyPlayerInfo != null && !partyPlayerInfo.getText2().isBlank()) {
         return partyPlayerInfo.getText2();
      } else {
         Prefs prefs2 = prefs();
         return prefs2 == null ? "" : prefs2.method3313(selected);
      }
   }

   private static String onlineFor(String selected) {
      Prefs prefs2 = prefs();
      long l = prefs2 == null ? 0L : prefs2.method3309(selected);
      return l <= 0L ? t("gui.friends.justJoined") : duration(l);
   }

   private static String lastSeen(String selected) {
      Prefs prefs2 = prefs();
      long l = prefs2 == null ? 0L : prefs2.method3310(selected);
      return l <= 0L ? t("gui.friends.neverSeen") : t("gui.friends.ago").replace("{t}", duration(System.currentTimeMillis() - l));
   }

   private static String duration(long l) {
      long l2 = l / 60000L;
      if (l2 < 1L) {
         return t("gui.time.lessMinute");
      } else if (l2 < 60L) {
         return l2 + " " + t("gui.time.min");
      } else {
         long l3 = l2 / 60L;
         if (l3 < 24L) {
            long l4 = l2 % 60L;
            return l4 == 0L ? l3 + " " + t("gui.time.hour") : l3 + " " + t("gui.time.hour") + " " + l4 + " " + t("gui.time.min");
         } else {
            long l4 = l3 / 24L;
            return l4 + " " + t("gui.time.day");
         }
      }
   }

   private void bar(String selected, String text, Supplier<Float> supplier, Supplier<Float> supplier2, Function<ZenithStyle, ZenithColor> function) {
      Font font = Fonts.NEW_REGULAR.getFont(4.6F);
      this.asideCustom(
         font.height() + 3.0F + 7.0F,
         (menuDrawContext, n, n2, f, centeredTextY, f2, previewAlpha, zenithStyle) -> {
            float f3 = (Float)supplier.get();
            if (!Float.isFinite(f3)) {
               f3 = 0.0F;
            }

            float f4 = (Float)supplier2.get();
            f4 = Float.isFinite(f4) ? Math.max(0.001F, f4) : 20.0F;
            float f5 = this.barFill
               .computeIfAbsent("bar:" + selected + ":" + text, str -> new Animation(260L, 0.0F, Easing.INSTANCE12))
               .method2808(Math.max(0.0F, Math.min(1.0F, f3 / f4)));
            String str = String.valueOf(Math.round(f3));
            menuDrawContext.drawText(font, text, f + 8.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            menuDrawContext.drawText(font, str, f + f2 - 8.0F - font.width(str), centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
            float y = centeredTextY + font.height() + 3.0F;
            float width = f2 - 16.0F;
            menuDrawContext.drawRoundedRect(
               f + 8.0F, y, width, 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha)
            );
            if (f5 > 0.0F) {
               menuDrawContext.drawRoundedRect(
                  f + 8.0F, y, width * f5, 3.0F, ProxyDotRadiusData.method8069(1.5F), ((ZenithColor)function.apply(zenithStyle)).method7294(previewAlpha)
               );
            }
         }
      );
   }

   private void buildLocalAside(String selected) {
      this.profileHead(selected, t(this.isNearby(selected) ? "gui.friends.nearbyWorld" : "gui.friends.localFriend"), true);
      this.flashRow();
      this.asideCaption("gui.friends.caption.now");
      this.asideInfo("gui.friends.storedIn", () -> t("gui.friends.storedIn.value"), "gui.friends.storedIn.desc");
      this.asideInfo("gui.friends.near", () -> t(this.isNearby(selected) ? "gui.value.yes" : "gui.value.no"), "gui.friends.near.desc");
      this.asideCaption("gui.friends.caption.actions");
      this.asideAction("gui.friends.invite", MenuGlyph.CHECK, () -> t("gui.friends.invite.value"), () -> {
         Cloud cloud2 = cloud();
         if (cloud2 != null) {
            cloud2.method7904(0, selected);
         }

         this.flashMessage(t("gui.friends.notice.queued").replace("{n}", selected));
      }, "gui.friends.invite.desc");
      this.asideAction("gui.friends.copyNick", MenuGlyph.COPY, () -> t("gui.friends.copyNick.value"), () -> this.copy(selected), "gui.friends.copyNick.desc");
      this.dangerButton("gui.friends.remove", "gui.friends.remove.local.desc", () -> {
         Zenith.getInstance().getFriends().method7629(selected);
         Zenith.getInstance().getFriends().save();
         this.selected = null;
         this.invalidate();
      });
   }

   private static void plate(MenuDrawContext customDrawContext, float floatValue, float y, float width, float height, float hover, float pick, float previewAlpha, ZenithStyle zenithStyle) {
      customDrawContext.drawRoundedRect(
         floatValue,
         y,
         width,
         height,
         ProxyDotRadiusData.method8069(4.0F),
         zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (0.45F + 0.25F * hover + 0.3F * pick))
      );
      if (pick > 0.002F) {
         float f = (width - 10.0F) * pick;
         customDrawContext.drawRoundedRect(
            floatValue + (width - f) / 2.0F,
            y + height - 2.0F,
            f,
            1.5F,
            ProxyDotRadiusData.method8069(0.75F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * pick)
         );
      }
   }

   private static void bigGlyph(MenuDrawContext customDrawContext, MenuGlyph menuGlyph, float x, float y, ZenithColor zenithColor) {
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().scale(2.0F, 2.0F);
      menuGlyph.draw(customDrawContext, 0.0F, 0.0F, zenithColor);
      customDrawContext.popMatrix();
   }

   private static void plusGlyph(MenuDrawContext customDrawContext, float x, float y, float pick, ZenithColor zenithColor) {
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().rotate((float)((Math.PI / 4) * pick));
      customDrawContext.drawRect(-2.5F, -0.5F, 5.0F, 1.0F, zenithColor);
      customDrawContext.drawRect(-0.5F, -2.5F, 1.0F, 5.0F, zenithColor);
      customDrawContext.popMatrix();
   }

   private static void diamond(MenuDrawContext customDrawContext, float x, float y, float width, ZenithColor zenithColor) {
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().rotate((float)Math.toRadians(45.0));
      customDrawContext.drawRoundedRect(-width / 2.0F, -width / 2.0F, width, width, ProxyDotRadiusData.method8069(0.6F), zenithColor);
      customDrawContext.popMatrix();
   }

   private void drawToggle(MenuDrawContext customDrawContext, float floatValue, float y, float width, float height, float pick, float previewAlpha, ZenithStyle zenithStyle) {
      ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7294(0.35F).method7295(zenithStyle.getPrimaryColor().getZenithColor(), pick);
      customDrawContext.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(height / 2.0F), zenithColor.method7294(previewAlpha));
      float f = height - 3.0F;
      float floatValue2 = floatValue + 1.5F + (width - f - 3.0F) * pick;
      customDrawContext.drawRoundedRect(floatValue2, y + 1.5F, f, f, ProxyDotRadiusData.method8069(f / 2.0F), new ZenithColor(255, 255, 255).method7294(previewAlpha));
   }

   private void pillButton(MenuDrawContext menuDrawContext, String key, BoundsData boundsData, String text, boolean flag, boolean contains, float previewAlpha, ZenithStyle zenithStyle) {
      float hover = Math.min(1.0F, this.hover(key + ":btn", contains) + this.flash(key) * 0.8F);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(boundsData.height() / 2.0F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      if (flag) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, zenithColor.method7292(0.16F + hover * 0.74F).method7294(previewAlpha)
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

      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      ZenithColor zenithColor2 = flag
         ? zenithColor.method7295(new ZenithColor(255, 255, 255), hover).method7294(previewAlpha)
         : zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha);
      menuDrawContext.drawText(
         font, text, boundsData.getFloatValue() + (boundsData.width() - font.width(text)) / 2.0F, boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F, zenithColor2
      );
   }

   public void asideHint(String description) {
      Font font = Fonts.NEW_REGULAR.getFont(4.6F);
      List<String> strings = wrapText(description, font, this.asideWidth() - 16.0F, 4);
      float f = font.height() + 2.0F;
      int n = this.asideOrder++;
      this.asideCustom(strings.size() * f + GuiStyle.PADDING.intValue() * 2.0F, (var5x, n2, n3, f2, f3, f4, f5, zenithStyle) -> {
         float wave2 = wave(this.asideWave, n);
         float centeredTextY = f3 + GuiStyle.PADDING.intValue();

         for (String text : strings) {
            var5x.drawText(font, text, f2 + 8.0F + (1.0F - wave2) * 6.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(f5 * wave2));
            centeredTextY += f;
         }
      });
   }

   private void hint(String description) {
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> strings = wrapText(description, font, this.mainWidth() - 16.0F, 4);
      float f = font.height() + 2.0F;
      int n = this.order++;
      this.custom(strings.size() * f + GuiStyle.PADDING.intValue(), (var5x, n2, n3, f2, f3, f4, f5, zenithStyle) -> {
         float wave2 = wave(this.listWave, n);
         float centeredTextY = f3 + 2.0F;

         for (String text : strings) {
            var5x.drawText(font, text, f2 + 8.0F + (1.0F - wave2) * 8.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(f5 * wave2));
            centeredTextY += f;
         }
      });
   }

   private void noticeBox(String noticeAnimatedFor) {
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> strings = wrapText(noticeAnimatedFor, font, this.mainWidth() - 16.0F - 14.0F, 4);
      float f = font.height() + 1.5F;
      float height = strings.size() * f + 10.0F;
      if (!noticeAnimatedFor.equals(this.noticeAnimatedFor)) {
         this.noticeAnimatedFor = noticeAnimatedFor;
         restart(this.noticeAppear);
      }

      this.custom(
         height + GuiStyle.PADDING.intValue(),
         (var5x, n, n2, f2, y, f3, previewAlpha, zenithStyle) -> {
            float f4 = this.noticeAppear.method2808(1.0F);
            float floatValue = f2 + 8.0F;
            float width = f3 - 16.0F;
            var5x.drawRoundedRect(
               floatValue, y, width, height, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * 0.7F * f4)
            );
            float height2 = (height - 6.0F) * f4;
            var5x.drawRoundedRect(
               floatValue + 1.5F,
               y + 3.0F + (height - 6.0F - height2) / 2.0F,
               2.0F,
               height2,
               ProxyDotRadiusData.method8069(1.0F),
               zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );
            float centeredTextY = y + 5.0F + (1.0F - f4) * 2.0F;

            for (String text : strings) {
               var5x.drawText(font, text, floatValue + 7.0F, centeredTextY, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha * f4));
               centeredTextY += f;
            }
         }
      );
   }

   private void press(String key) {
      this.pressedAt.put(key, System.currentTimeMillis());
   }

   private float flash(String key) {
      long l = this.pressedAt.getOrDefault(key, 0L);
      if (l == 0L) {
         return 0.0F;
      } else {
         float f = (float)(System.currentTimeMillis() - l) / 220.0F;
         return f >= 1.0F ? 0.0F : 1.0F - f;
      }
   }

   private void flashMessage(String flashText) {
      this.flashText = flashText;
      this.flashAt = System.currentTimeMillis();
      this.invalidate();
   }

   private boolean flashing() {
      return !this.flashText.isEmpty() && System.currentTimeMillis() - this.flashAt < 2600L;
   }

   private void copy(String name) {
      if (name != null && !name.isBlank()) {
         mc.keyboardHandler.setClipboard(name);
         this.flashMessage(t("gui.friends.copied"));
      }
   }

   private void buttonsRow(String key, String str, Runnable runnable, String str2, Runnable runnable2) {
      this.buttonsRow(key, str, runnable, str2, runnable2, false);
   }

   private void buttonsRow(String key, String str, Runnable runnable, String key2, Runnable runnable2, boolean flag) {
      this.custom(19.0F, (var5x, var6x, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
         float width = (f3 - 16.0F - 4.0F) / 2.0F;
         BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 2.0F, width, 15.0F);
         BoundsData boundsData2 = new BoundsData(f + 8.0F + width + 4.0F, f2 + 2.0F, width, 15.0F);
         int n = boundsData.contains(var6x, lastClickY) ? 0 : (boundsData2.contains(var6x, lastClickY) ? 1 : -1);
         this.hoveredButton.put(key, n);
         float f4 = flag ? 0.6F + 0.4F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0)) : 1.0F;
         this.pillButton(var5x, key + ":0", boundsData, t(str), false, n == 0, previewAlpha, zenithStyle);
         this.pillButton(var5x, key + ":1", boundsData2, t(key2), true, n == 1 && !flag, previewAlpha * f4, zenithStyle);
      }, () -> {
         int var5x = this.hoveredButton.getOrDefault(key, -1);
         if (var5x == 0) {
            this.press(key + ":0");
            runnable.run();
         } else if (var5x == 1 && !flag) {
            this.press(key + ":1");
            runnable2.run();
         }
      }, null);
   }

   private float hover(String key, boolean contains) {
      return this.buttonHover.computeIfAbsent("hover:" + key, str -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(contains ? 1.0F : 0.0F);
   }

   private float pick(String key, boolean addOpen) {
      return this.cardSelect.computeIfAbsent("pick:" + key, str -> new Animation(180L, 0.0F, Easing.INSTANCE12)).method2808(addOpen ? 1.0F : 0.0F);
   }

   private float select(String key, boolean flag) {
      return this.cardSelect.computeIfAbsent(key, str -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(flag ? 1.0F : 0.0F);
   }

   private static String fit(Font font, String name, float width) {
      return name != null && !name.isEmpty() && !(width <= 0.0F) ? font.clip(name, width) : "";
   }

   private static String accountName() {
      Zenith zenith = Zenith.getInstance();
      return zenith != null && zenith.getEntry() != null && zenith.getEntry().getUsername() != null ? zenith.getEntry().getUsername() : "";
   }

   private static long accountUid() {
      Zenith zenith = Zenith.getInstance();
      if (zenith != null && zenith.getEntry() != null && zenith.getEntry().getText2() != null) {
         try {
            return Long.parseLong(zenith.getEntry().getText2().trim());
         } catch (NumberFormatException e) {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   private static List<ItemStack> equipment(PartyPlayerInfo partyPlayerInfo) {
      CloudPackets.Data7[] data7s = partyPlayerInfo.getData7s();
      if (data7s.length <= 40) {
         return List.of();
      } else {
         ArrayList arrayList = new ArrayList(5);

         for (int i = 39; i >= 36; i--) {
            ItemStack stack = stackOf(data7s[i]);
            if (!stack.isEmpty()) {
               arrayList.add(stack);
            }
         }

         ItemStack stack = stackOf(data7s[40]);
         if (!stack.isEmpty()) {
            arrayList.add(stack);
         }

         return arrayList;
      }
   }

   private static ItemStack stackOf(CloudPackets.Data7 data7) {
      if (data7 != null && !data7.isEmpty()) {
         Identifier tryParse = Identifier.tryParse(data7.getText());
         if (tryParse != null && BuiltInRegistries.ITEM.containsKey(tryParse)) {
            Item item = BuiltInRegistries.ITEM.getValue(tryParse);
            if (item == Items.AIR) {
               return ItemStack.EMPTY;
            } else {
               ItemStack stack = new ItemStack(item, Math.max(1, Math.min(127, data7.count())));
               if (data7.getIntValue2() > 0) {
                  stack.setDamageValue(data7.getIntValue2());
               }

               return stack;
            }
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private static long uidOf(String text) {
      CloudConnectionState cloudConnectionState = state();
      long uidOf = cloudConnectionState == null ? 0L : cloudConnectionState.uidOf(displayName(text));
      if (uidOf > 0L) {
         return uidOf;
      } else {
         Prefs prefs2 = prefs();
         return prefs2 == null ? 0L : prefs2.method3311(text);
      }
   }

   private static PartyPlayerInfo peer(String selected) {
      CloudConnectionState cloudConnectionState = state();
      return cloudConnectionState == null ? null : cloudConnectionState.method8044(selected);
   }

   private static boolean isPendingName(String selected) {
      CloudConnectionState cloudConnectionState = state();
      return cloudConnectionState != null && cloudConnectionState.method8061(selected) && cloudConnectionState.method8044(selected) == null;
   }

   private static String displayName(String selected) {
      PartyPlayerInfo partyPlayerInfo = peer(selected);
      return partyPlayerInfo == null ? selected : partyPlayerInfo.username();
   }

   private static String whereLine(String selected) {
      PartyPlayerInfo partyPlayerInfo = peer(selected);
      CloudConnectionState cloudConnectionState = state();
      if (partyPlayerInfo == null) {
         return t(cloudConnectionState != null && cloudConnectionState.method8061(selected) ? "gui.friends.status.pendingLong" : "gui.friends.status.offline");
      } else {
         String anObject = blankToDash(partyPlayerInfo.method3290());
         String str = shortWorld(partyPlayerInfo.dimension());
         if ("-".equals(anObject)) {
            return t("gui.friends.inMenu");
         } else {
            return !str.isEmpty() && !"-".equals(str) ? anObject + " · " + str : anObject;
         }
      }
   }

   private static float statusLit(String selected) {
      CloudConnectionState cloudConnectionState = state();
      if (cloudConnectionState == null) {
         return 0.0F;
      } else {
         return cloudConnectionState.method8044(selected) == null && !cloudConnectionState.method8061(selected) ? 0.0F : 1.0F;
      }
   }

   private static ZenithColor statusColor(String selected, ZenithStyle zenithStyle) {
      CloudConnectionState cloudConnectionState = state();
      PartyPlayerInfo partyPlayerInfo = cloudConnectionState == null ? null : cloudConnectionState.method8044(selected);
      if (partyPlayerInfo != null) {
         Cloud cloud2 = cloud();
         if (cloud2 != null && cloud2.getPartyMember().method1142(partyPlayerInfo.uid())) {
            String text = cloud2.getPartyMember().method1141(partyPlayerInfo.uid()).getText();
            if (!"online".equals(text) && !text.isEmpty()) {
               return PartyManager.method51(text);
            }
         }

         return zenithStyle.getFriendColor().getZenithColor();
      } else {
         return cloudConnectionState != null && cloudConnectionState.method8061(selected) ? zenithStyle.getPrimaryColor().getZenithColor() : zenithStyle.getTextTertiary().getZenithColor();
      }
   }

   private static String distanceTo(String selected) {
      PartyPlayerInfo partyPlayerInfo = peer(selected);
      Cloud cloud2 = cloud();
      if (partyPlayerInfo != null && cloud2 != null && mc.player != null) {
         if (!partyPlayerInfo.method3291(cloud2.method7911(), cloud2.method7913())) {
            return "-";
         } else {
            double d = partyPlayerInfo.getDoubleValue() - mc.player.getX();
            double d2 = partyPlayerInfo.getDoubleValue2() - mc.player.getY();
            double d3 = partyPlayerInfo.getDoubleValue3() - mc.player.getZ();
            double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
            return Double.isFinite(d4) && !(d4 >= 1.0E8) ? Math.round(d4) + " " + t("gui.unit.meter") : "-";
         }
      } else {
         return "-";
      }
   }

   private boolean isNearby(String selected) {
      if (mc.level != null && selected != null) {
         List<AbstractClientPlayer> players = mc.level.players();
         long nanoTime = System.nanoTime();
         if (this.nearbyWorld != mc.level || this.nearbyCount != players.size() || nanoTime - this.nearbyAt > 4000000L) {
            this.nearbyNames.clear();

            for (AbstractClientPlayer player : players) {
               this.nearbyNames.add(player.getName().getString());
            }

            this.nearbyWorld = mc.level;
            this.nearbyCount = players.size();
            this.nearbyAt = nanoTime;
         }

         return this.nearbyNames.contains(selected);
      } else {
         return false;
      }
   }

   private static boolean matches(String selected, String searchQuery) {
      return searchQuery.isEmpty() || selected != null && selected.toLowerCase(Locale.ROOT).contains(searchQuery);
   }

   private static String blankToDash(String name) {
      return name != null && !name.isBlank() ? name : "-";
   }

   private static String shortWorld(String dimension) {
      if (dimension != null && !dimension.isBlank()) {
         String str = dimension.trim();
         int indexOf = str.indexOf(58);
         return indexOf > 0 && indexOf + 1 < str.length() ? str.substring(indexOf + 1) : str;
      } else {
         return "";
      }
   }

   private static final class Hint {
      private final String name;
      private final String source;
      private final long uid;
      private final int addKind;

      public String name() {
         return this.name;
      }

      public String source() {
         return this.source;
      }

      public long uid() {
         return this.uid;
      }

      public int addKind() {
         return this.addKind;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Hint other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.source, other.source) && java.util.Objects.equals(this.uid, other.uid) && java.util.Objects.equals(this.addKind, other.addKind);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, source, uid, addKind);
      }

      @Override
      public String toString() {
         return "Hint[" + "name=" + this.name + ", " + "source=" + this.source + ", " + "uid=" + this.uid + ", " + "addKind=" + this.addKind + "]";
      }

      private Hint(String name, String source, long uid, int addKind) {
         this.name = name;
         this.source = source;
         this.uid = uid;
         this.addKind = addKind;
      }
   }

   private static final class Tile {
      private final String key;
      private final MenuGlyph icon;
      private final String label;
      private final String note;
      private final boolean active;
      private final Runnable action;

      public String key() {
         return this.key;
      }

      public MenuGlyph icon() {
         return this.icon;
      }

      public String label() {
         return this.label;
      }

      public String note() {
         return this.note;
      }

      public boolean active() {
         return this.active;
      }

      public Runnable action() {
         return this.action;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Tile other)) return false;
         return java.util.Objects.equals(this.key, other.key) && java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.label, other.label) && java.util.Objects.equals(this.note, other.note) && java.util.Objects.equals(this.active, other.active) && java.util.Objects.equals(this.action, other.action);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(key, icon, label, note, active, action);
      }

      @Override
      public String toString() {
         return "Tile[" + "key=" + this.key + ", " + "icon=" + this.icon + ", " + "label=" + this.label + ", " + "note=" + this.note + ", " + "active=" + this.active + ", " + "action=" + this.action + "]";
      }

      private Tile(String key, MenuGlyph icon, String label, String note, boolean active, Runnable action) {
         this.key = key;
         this.icon = icon;
         this.label = label;
         this.note = note;
         this.active = active;
         this.action = action;
      }
   }
}

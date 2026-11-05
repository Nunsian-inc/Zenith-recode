package org.zenith.client.screens.nlgui.panel;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.resources.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.chat.ChatCards;
import org.zenith.client.screens.nlgui.chat.ChatCompose;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.CloudChatClient;
import org.zenith.utility.api.CloudDmClient;
import org.zenith.utility.api.StickerPackService;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.api.UserSearchClient;
import org.zenith.utility.config.ChatImageCache;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.config.SiteSession;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.Coord;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CloudChatSectionPanel extends SectionPanel {
   private static final int TAB_GLOBAL = 0;
   private static final int TAB_DIRECT = 1;
   private static final float INPUT_HEIGHT = 20.0F;
   private static final float ERROR_ROW = 7.0F;
   private static final float BUBBLE_PAD_X = 7.0F;
   private static final float BUBBLE_PAD_Y = 5.0F;
   private static final float BUBBLE_GAP = 3.0F;
   private static final float BUBBLE_ROUND = 5.0F;
   private static final float BUBBLE_TAIL = 1.5F;
   private static final float TIME_GAP = 6.0F;
   private static final float CONTACT_HEIGHT = 24.0F;
   private static final float ASIDE_WIDTH = 138.0F;
   private static final float AVATAR = 16.0F;
   private static final float PEER_BAR = 24.0F;
   private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm", Locale.ROOT);
   private static final ZenithColor ERROR_COLOR = new ZenithColor(232, 120, 108);
   private static final Map<String, SimpleDateFormat> DAY = new HashMap<>();
   private static final long ROSTER_MS = 5000L;
   private static final long POLL_MS = 2000L;
   private static final int MAX_PREVIEWS = 64;
   private static final int TAG_RESERVE = 120;
   private SearchBox input;
   private int lastCount = -1;
   private int lastTab = -1;
   private int lastColumn = -1;
   private String lastStatus = "";
   private String lastQuery = "";
   private boolean stickToEnd = true;
   private final Animation messageArrival = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private final Animation focusAnimation = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation listReveal = new Animation(320L, 1.0F, Easing.INSTANCE12);
   private final Map<Long, Animation> selectFade = new ConcurrentHashMap<>();
   private final Map<Long, Animation> avatarFade = new ConcurrentHashMap<>();
   private final Animation cardReveal = new Animation(240L, 1.0F, Easing.INSTANCE12);
   private boolean cardClosing;
   private final Map<Long, Animation> contactHover = new ConcurrentHashMap<>();
   private final Map<Long, Animation> unreadPop = new ConcurrentHashMap<>();
   private final Map<Long, Animation> onlineFade = new ConcurrentHashMap<>();
   private final Map<String, Animation> glyphHover = new ConcurrentHashMap<>();
   private final Map<String, Animation> cellHover = new ConcurrentHashMap<>();
   private final Map<String, Animation> stickerFade = new ConcurrentHashMap<>();
   private final Animation pickerAnimation = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private final Animation packPlateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation packPlateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean packPlateReady;
   private final Animation pendingAnimation = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private final Animation peerBarAnimation = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private long barUid;
   private String barName = "";
   private final Animation noticeAnimation = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private final Animation errorAnimation = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private String shownNotice = "";
   private String shownError = "";
   private String shownLabel = "";
   private boolean wantFocus;
   private boolean blurred;
   private long peerUid;
   private String peerName = "";
   private long selectedUid;
   private String selectedName = "";
   private String selectedMcName = "";
   private String draft = "";
   private String draftTag = "";
   private String pendingTag = "";
   private String pendingLabel = "";
   private boolean pickerOpen;
   private int pickerPack;
   private BoundsData pickerBounds;
   private BoundsData pickerAt;
   private BoundsData coordsButton;
   private BoundsData inventoryButton;
   private BoundsData stickerButton;
   private BoundsData pendingButton;
   private long deleteMenuMessage;
   private long deleteMenuPeer;
   private BoundsData deleteMenuBounds;
   private float deleteMenuAnchorTop = Float.NaN;
   private final Animation deleteMenuAnimation = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation deleteMenuHover = new Animation(120L, 0.0F, Easing.INSTANCE12);
   private static final long DYING_MS = 240L;
   private static final int DYING_MAX = 4;
   private static final int FEED_NODE_GLOBAL = 0;
   private static final int FEED_NODE_DIRECT = 1;
   private static final int FEED_SITE_GLOBAL = 2;
   private final List<CloudChatSectionPanel.Ghost> ghosts = new ArrayList<>();
   private final Map<Long, CloudPackets.Data13> knownNodeGlobal = new LinkedHashMap<>();
   private final Map<Long, StoreApi.Data3> knownNodeDirect = new LinkedHashMap<>();
   private final Map<Long, CloudChatClient.Data3> knownSiteGlobal = new LinkedHashMap<>();
   private long knownDirectPeer = -1L;
   private volatile List<StoreApi.Data4> online = List.of();
   private volatile List<StoreApi.Data3> conversation = List.of();
   private final Object conversationLock = new Object();
   private final Map<Long, String> previews = new ConcurrentHashMap<>();
   private volatile long lastPoll;
   private int seenDirectSeq;
   private volatile long lastRoster;
   private volatile long polledPeer = -1L;
   private boolean dayBroke;
   private long peerUnderFor = -1L;
   private int peerUnderStamp = -1;
   private String peerUnderText = "";
   private volatile String error = "";
   private volatile String notice = "";
   private volatile long noticeAt;
   private volatile int revision;
   private int seenRevision;
   private int stickersPrepared = -1;
   private int seenSiteRevision;
   private final Map<CloudChatSectionPanel.BubbleKey, CloudChatSectionPanel.BubbleLayout> bubbleLayouts = new LinkedHashMap<CloudChatSectionPanel.BubbleKey, CloudChatSectionPanel.BubbleLayout>(
      256, 0.75F, true
   ) {
      @Override
      public boolean removeEldestEntry(Entry<CloudChatSectionPanel.BubbleKey, CloudChatSectionPanel.BubbleLayout> eldest) {
         return this.size() > 768;
      }
   };
   private static final Map<CloudChatSectionPanel.ClipKey, String> CLIPS = new LinkedHashMap<CloudChatSectionPanel.ClipKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<CloudChatSectionPanel.ClipKey, String> eldest) {
         return this.size() > 256;
      }
   };

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   private static CloudConnectionState state() {
      Cloud cloud2 = cloud();
      return cloud2 == null ? null : cloud2.state();
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private static CloudChatClient global() {
      return CloudChatClient.getInstance();
   }

   private static CloudDmClient direct() {
      return CloudDmClient.getInstance();
   }

   @Override
   public String headerTitle() {
      return t("gui.cloud.tab.chat");
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CHAT;
   }

   @Override
   public String sectionDescription() {
      return "gui.chat.desc";
   }

   @Override
   public String emptyText() {
      if (this.getActiveTab() == 1) {
         if (!siteMode()) {
            CloudConnectionState cloudConnectionState = state();
            if (cloudConnectionState == null) {
               return "gui.friends.reason.down";
            }
         }

         if (!this.hasPeer()) {
            return "gui.chat.empty.pick";
         } else {
            return siteMode() && direct().isFlag2() ? "gui.chat.loading" : "gui.chat.empty.first";
         }
      } else if (siteMode()) {
         if (global().method2166()) {
            return "gui.chat.empty.global";
         } else {
            String error = global().error();
            return error != null && !error.isEmpty() ? clipText(error) : "gui.chat.loading";
         }
      } else {
         CloudConnectionState cloudConnectionState = state();
         if (cloudConnectionState == null) {
            return "gui.friends.reason.down";
         } else if (cloudConnectionState.method8035()) {
            return "gui.chat.empty.global";
         } else {
            String note = cloudConnectionState.note();
            if (note == null || note.isBlank()) {
               note = cloudConnectionState.getLastError();
            }

            return note != null && !note.isBlank() ? cloudConnectionState.getType().label() + " — " + note : cloudConnectionState.getType().label();
         }
      }
   }

   @Override
   public float asideWidth() {
      return 138.0F;
   }

   @Override
   public float asideAlpha() {
      return this.selectedUid == 0L ? 1.0F : this.cardReveal.getValue();
   }

   @Override
   public float mainWidth() {
      return 242.0F;
   }

   private static boolean siteMode() {
      return SiteSession.method2291();
   }

   private boolean hasPeer() {
      return siteMode() ? direct().hasPeer() : this.peerUid != 0L;
   }

   private long openPeerUid() {
      return siteMode() ? direct().getLongValue() : this.peerUid;
   }

   private String openPeerName() {
      return siteMode() ? direct().getPeerName() : this.peerName;
   }

   @Override
   public void tick() {
      this.prepareStickers();
      if (this.cardClosing) {
         this.cardReveal.method2808(0.0F);
      }

      if (this.cardClosing && this.cardReveal.getValue() <= 0.01F) {
         this.cardClosing = false;
         this.selectedUid = 0L;
         this.selectedName = "";
         this.selectedMcName = "";
         this.invalidate();
      }

      if (this.revision != this.seenRevision) {
         this.seenRevision = this.revision;
         this.invalidate();
      }

      long now = System.currentTimeMillis();
      if (siteMode()) {
         if (this.getActiveTab() == 0) {
            global().method2167();
         } else {
            direct().method4863();
         }

         int seenSiteRevision = global().revision() * 31 + direct().revision();
         if (seenSiteRevision != this.seenSiteRevision) {
            this.seenSiteRevision = seenSiteRevision;
            this.revision++;
         }
      } else {
         if (this.getActiveTab() == 0 && now - this.lastRoster > 5000L) {
            this.lastRoster = now;
            this.fetchRoster();
         }

         Cloud cloud2 = cloud();
         if (cloud2 != null && cloud2.getPartyMember().getIntValue() != this.seenDirectSeq) {
            this.seenDirectSeq = cloud2.getPartyMember().getIntValue();
            if (cloud2.getPartyMember().getLongValue2() == this.peerUid) {
               this.lastPoll = 0L;
            }
         }

         if (this.getActiveTab() == 1 && this.hasPeer() && (this.peerUid != this.polledPeer || now - this.lastPoll > 2000L)) {
            this.lastPoll = now;
            this.polledPeer = this.peerUid;
            this.fetchConversation(this.peerUid);
         }
      }

      int countForChange = this.countForChange();
      String statusForChange = this.statusForChange();
      int lastColumn = Math.round(this.mainWidth());
      boolean flag = this.getActiveTab() != this.lastTab;
      boolean flag2 = !statusForChange.equals(this.lastStatus);
      String searchQuery = this.searchQuery() == null ? "" : this.searchQuery();
      boolean flag3 = !searchQuery.equals(this.lastQuery);
      if (countForChange != this.lastCount || flag || lastColumn != this.lastColumn || flag2 || flag3) {
         boolean flag4 = countForChange > this.lastCount && this.lastCount >= 0 && !flag;
         this.lastCount = countForChange;
         this.lastTab = this.getActiveTab();
         this.lastColumn = lastColumn;
         this.lastStatus = statusForChange;
         this.lastQuery = searchQuery;
         if (flag4) {
            this.messageArrival.reset(0.0F);
            this.messageArrival.method2810(1.0F);
         }

         if (flag) {
            this.focusInput();
            this.listReveal.reset(0.0F);
            this.listReveal.method2810(1.0F);
            this.pickerOpen = false;
         }

         if (flag || flag2) {
            this.ghosts.clear();
            this.knownNodeGlobal.clear();
            this.knownNodeDirect.clear();
            this.knownSiteGlobal.clear();
         }

         if (flag3 && this.getActiveTab() == 1 && searchQuery.length() >= 2) {
            UserSearchClient.method4760(searchQuery, 8).whenComplete((var1x, throwable) -> this.revision++);
         }

         this.deleteMenuMessage = 0L;
         this.invalidate();
         if (this.stickToEnd || flag) {
            this.stickToEnd = true;
            this.scrollToEnd();
         }
      }
   }

   private int countForChange() {
      if (this.getActiveTab() == 1) {
         return siteMode() ? direct().method4857().size() * 131 + direct().method4856().size() : this.conversation.size();
      } else if (siteMode()) {
         return global().method2164().size() * 131 + global().getOnline().size();
      } else {
         CloudConnectionState cloudConnectionState = state();
         return cloudConnectionState == null ? 0 : this.countGlobal(cloudConnectionState);
      }
   }

   private int countGlobal(CloudConnectionState cloudConnectionState) {
      Cloud cloud2 = cloud();
      int n = 0;

      for (CloudPackets.Data13 data13 : cloudConnectionState.getArrayDeque()) {
         if (data13.getIntValue() == 0 && (cloud2 == null || !cloud2.prefs().method3316(data13.username()))) {
            n++;
         }
      }

      return n;
   }

   private String statusForChange() {
      if (siteMode()) {
         return (global().isLive() ? "live" : "poll") + (direct().isLive() ? "+" : "-") + (this.hasPeer() ? this.openPeerUid() : 0L);
      } else {
         CloudConnectionState cloudConnectionState = state();
         return cloudConnectionState == null ? "none" : cloudConnectionState.getType().name();
      }
   }

   private void fetchRoster() {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1963().whenComplete((var1x, throwable) -> {
            if (throwable == null) {
               this.online = Collections.unmodifiableList(new ArrayList<>(var1x));
               this.error = "";
            } else {
               this.error = shortError(throwable);
            }

            this.revision++;
         });
      }
   }

   private void fetchConversation(long peerUid) {
      StoreApi storeApi = store();
      if (storeApi != null) {
         storeApi.method1964(peerUid, 200).whenComplete((var3x, throwable) -> {
            if (throwable == null) {
               if (!var3x.isEmpty()) {
                  this.rememberPreview(peerUid, var3x.get(var3x.size() - 1).text());
               }

               if (peerUid != this.peerUid) {
                  return;
               }

               synchronized (this.conversationLock) {
                  this.conversation = Collections.unmodifiableList(new ArrayList<>(var3x));
               }

               this.error = "";
            } else {
               if (peerUid != this.peerUid) {
                  return;
               }

               this.error = shortError(throwable);
            }

            this.revision++;
         });
      }
   }

   private void rememberPreview(long peerUid, String text) {
      if (this.previews.size() >= 64 && !this.previews.containsKey(peerUid)) {
         this.previews.keySet().stream().findFirst().ifPresent(this.previews::remove);
      }

      this.previews.put(peerUid, text);
   }

   private List<CloudPackets.Data13> globalMessages(CloudConnectionState cloudConnectionState) {
      Cloud cloud2 = cloud();
      ArrayList<CloudPackets.Data13> data13s = new ArrayList<>();

      for (CloudPackets.Data13 data13 : cloudConnectionState.getArrayDeque()) {
         if (data13.getIntValue() == 0 && (cloud2 == null || !cloud2.prefs().method3316(data13.username()))) {
            data13s.add(data13);
         }
      }

      return data13s;
   }

   @Override
   public void build() {
      this.dayBroke = false;
      this.tabs(new String[]{"gui.chat.tab.global", "gui.chat.tab.direct"});
      this.tabIcons(new MenuGlyph[]{MenuGlyph.CLOUD, MenuGlyph.FRIENDS});
      if (this.getActiveTab() == 1) {
         this.buildDirect();
         this.buildDirectAside();
      } else {
         this.buildGlobal();
         this.buildGlobalAside();
      }
   }

   private void buildGlobal() {
      if (siteMode()) {
         this.buildSiteGlobal();
      } else {
         CloudConnectionState cloudConnectionState = state();
         Cloud cloud2 = cloud();
         if (cloudConnectionState != null && cloud2 != null) {
            long longValue = cloud2.getLongValue();
            List<CloudPackets.Data13> globalMessages = this.globalMessages(cloudConnectionState);
            CloudPackets.Data13 data13 = globalMessages.isEmpty() ? null : (CloudPackets.Data13)globalMessages.get(globalMessages.size() - 1);
            LinkedHashMap map = new LinkedHashMap();

            for (CloudPackets.Data13 globalMessage : globalMessages) {
               if (globalMessage.getLongValue3() > 0L) {
                  map.put(globalMessage.getLongValue3(), globalMessage);
               }
            }

            int size = this.ghosts.size();
            this.knownNodeGlobal.forEach((key, var5x) -> {
               if (!map.containsKey(key)) {
                  this.ghosts.add(new CloudChatSectionPanel.Ghost(0, var5x.username(), "", var5x.text(), var5x.time(), longValue != 0L && var5x.uid() == longValue));
               }
            });
            this.forgetGone(size);
            this.knownNodeGlobal.clear();
            this.knownNodeGlobal.putAll(map);
            String author = null;
            boolean mine = false;
            long daySeparator = 0L;

            for (Object mergeWithGhost : this.mergeWithGhosts(globalMessages, 0)) {
               if (mergeWithGhost instanceof CloudChatSectionPanel.Ghost ghost) {
                  boolean showName = !ghost.mine && (!ghost.author.equals(author) || mine);
                  daySeparator = this.daySeparator(daySeparator, ghost.time);
                  showName = showName || this.dayBroke && !ghost.mine;
                  this.dayBroke = false;
                  author = ghost.author;
                  mine = ghost.mine;
                  this.ghostBubble(ghost, showName);
               } else {
                  CloudPackets.Data13 mergeWithGhost2 = (CloudPackets.Data13)mergeWithGhost;
                  boolean flag = longValue != 0L && mergeWithGhost2.uid() == longValue;
                  String username = mergeWithGhost2.username();
                  boolean showName = !flag && (!username.equals(author) || mine);
                  daySeparator = this.daySeparator(daySeparator, mergeWithGhost2.time());
                  showName = showName || this.dayBroke && !flag;
                  this.dayBroke = false;
                  author = username;
                  mine = flag;
                  long uid = mergeWithGhost2.uid();
                  long deleteMenuMessage = mergeWithGhost2.getLongValue3();
                  this.bubble(
                     username,
                     uid,
                     "",
                     mergeWithGhost2.text(),
                     mergeWithGhost2.time(),
                     flag,
                     showName,
                     mergeWithGhost2 == data13,
                     false,
                     flag ? null : () -> this.selectFromChat(uid, username),
                     flag && deleteMenuMessage > 0L ? () -> this.openDeleteMenu(deleteMenuMessage) : null
                  );
               }
            }
         } else {
            this.knownNodeGlobal.clear();
         }
      }
   }

   private void forgetGone(int size) {
      if (this.ghosts.size() - size > 4) {
         this.ghosts.subList(size, this.ghosts.size()).clear();
      }
   }

   private void buildSiteGlobal() {
      CloudChatClient cloudChatClient = global();
      long longValue = cloudChatClient.getLongValue();
      List<CloudChatClient.Data3> globalMessages = cloudChatClient.method2164();
      CloudChatClient.Data3 data3 = cloudChatClient.getData3();
      if (data3 != null) {
         this.pinnedBar(data3);
      }

      CloudChatClient.Data3 data32 = globalMessages.isEmpty() ? null : (CloudChatClient.Data3)globalMessages.get(globalMessages.size() - 1);
      LinkedHashMap map = new LinkedHashMap();

      for (CloudChatClient.Data3 globalMessage : globalMessages) {
         if (globalMessage.getLongValue() > 0L) {
            map.put(globalMessage.getLongValue(), globalMessage);
         }
      }

      int size = this.ghosts.size();
      this.knownSiteGlobal
         .forEach(
            (var4x, var5x) -> {
               if (!map.containsKey(var4x)) {
                  this.ghosts
                     .add(
                        new CloudChatSectionPanel.Ghost(
                           2, var5x.name(), var5x.role(), var5x.body(), var5x.getLongValue3(), longValue != 0L && var5x.getLongValue2() == longValue
                        )
                     );
               }
            }
         );
      this.forgetGone(size);
      this.knownSiteGlobal.clear();
      this.knownSiteGlobal.putAll(map);
      String author = null;
      boolean mine = false;
      long daySeparator = 0L;

      for (Object mergeWithGhost : this.mergeWithGhosts(globalMessages, 2)) {
         if (mergeWithGhost instanceof CloudChatSectionPanel.Ghost ghost) {
            boolean showName = !ghost.mine && (!ghost.author.equals(author) || mine);
            daySeparator = this.daySeparator(daySeparator, ghost.time);
            showName = showName || this.dayBroke && !ghost.mine;
            this.dayBroke = false;
            author = ghost.author;
            mine = ghost.mine;
            this.ghostBubble(ghost, showName);
         } else {
            CloudChatClient.Data3 mergeWithGhost2 = (CloudChatClient.Data3)mergeWithGhost;
            boolean flag = longValue != 0L && mergeWithGhost2.getLongValue2() == longValue;
            boolean showName = !flag && (!mergeWithGhost2.name().equals(author) || mine);
            daySeparator = this.daySeparator(daySeparator, mergeWithGhost2.getLongValue3());
            showName = showName || this.dayBroke && !flag;
            this.dayBroke = false;
            author = mergeWithGhost2.name();
            mine = flag;
            long uid = mergeWithGhost2.getLongValue2();
            String name = mergeWithGhost2.name();
            long deleteMenuMessage = mergeWithGhost2.getLongValue();
            boolean flag2 = flag || global().isFlag();
            this.bubble(
               name,
               uid,
               mergeWithGhost2.role(),
               mergeWithGhost2.body(),
               mergeWithGhost2.getLongValue3(),
               flag,
               showName,
               mergeWithGhost2 == data32,
               false,
               flag ? null : () -> this.selectFromChat(uid, name),
               flag2 ? () -> this.openDeleteMenu(deleteMenuMessage) : null
            );
         }
      }
   }

   private void pinnedBar(CloudChatClient.Data3 data3) {
      Font font = Fonts.NEW_REGULAR.getFont(4.4F);
      var strings = wrap(data3.name() + ": " + Coord.method5172(data3.body()).text(), font, this.mainWidth() - 16.0F - 6.0F - 8.0F);
      float height = 6.0F + Math.min(2, strings.size()) * (font.height() + 1.0F);
      this.custom(
         height + 3.0F,
         (var3x, var4x, n, f, y, f2, previewAlpha, zenithStyle) -> {
            var3x.drawRoundedRect(
               f + 8.0F,
               y,
               f2 - 16.0F,
               height,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F),
               zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.16F)
            );
            MenuGlyph.PIN.draw(var3x, f + 8.0F + 3.0F, y + 3.0F, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
            float centeredTextY = y + 3.0F;

            for (int i = 0; i < strings.size() && i < 2; i++) {
               var3x.drawText(
                  font, (String)strings.get(i), f + 8.0F + 3.0F + 6.0F + 4.0F, centeredTextY, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
               );
               centeredTextY += font.height() + 1.0F;
            }
         }
      );
   }

   private void openDeleteMenu(long deleteMenuMessage) {
      this.openDeleteMenu(deleteMenuMessage, 0L);
   }

   private void openDeleteMenu(long deleteMenuMessage, long deleteMenuPeer) {
      if (deleteMenuMessage > 0L) {
         this.deleteMenuMessage = deleteMenuMessage;
         this.deleteMenuPeer = deleteMenuPeer;
         BoundsData lastRightClickBounds = this.lastRightClickBounds();
         this.deleteMenuAnchorTop = lastRightClickBounds == null ? Float.NaN : lastRightClickBounds.getFloatValue2();
      }
   }

   private boolean clickDeleteMenu(double lastClickX, double lastClickY) {
      if (this.deleteMenuMessage != 0L && this.deleteMenuBounds != null && this.deleteMenuBounds.contains(lastClickX, lastClickY)) {
         long deleteMenuMessage = this.deleteMenuMessage;
         long deleteMenuPeer = this.deleteMenuPeer;
         this.deleteMenuMessage = 0L;
         this.deleteMenuPeer = 0L;
         if (siteMode()) {
            this.deleteOwnMessage(deleteMenuMessage);
         } else if (deleteMenuPeer != 0L) {
            this.deleteNodeDirect(deleteMenuPeer, deleteMenuMessage);
         } else {
            this.deleteNodeMessage(deleteMenuMessage);
         }

         return true;
      } else {
         return false;
      }
   }

   private void deleteNodeMessage(long deleteMenuMessage) {
      Cloud cloud2 = cloud();
      if (cloud2 != null && deleteMenuMessage > 0L) {
         cloud2.method7909(deleteMenuMessage);
      }
   }

   private void deleteNodeDirect(long deleteMenuPeer, long deleteMenuMessage) {
      StoreApi storeApi = store();
      if (storeApi != null && deleteMenuMessage > 0L) {
         storeApi.method1967(deleteMenuPeer, deleteMenuMessage).whenComplete((var3x, throwable) -> {
            if (throwable == null) {
               synchronized (this.conversationLock) {
                  var list = new ArrayList<>(this.conversation);
                  list.removeIf(data3 -> data3.getLongValue() == deleteMenuMessage);
                  this.conversation = Collections.unmodifiableList(list);
               }
            }

            this.error = throwable == null ? "" : shortError(throwable);
            this.revision++;
         });
      }
   }

   private void deleteOwnMessage(long deleteMenuMessage) {
      if (siteMode() && deleteMenuMessage > 0L) {
         global().method2169(deleteMenuMessage).whenComplete((var1x, throwable) -> {
            this.error = throwable == null ? "" : siteError(throwable);
            this.revision++;
         });
      }
   }

   private void selectFromChat(long selectedUid, String selectedName) {
      Cloud cloud2 = cloud();
      if (cloud2 == null || selectedUid != cloud2.getLongValue()) {
         if (!siteMode() || selectedUid != global().getLongValue()) {
            if (selectedUid != this.selectedUid || this.cardClosing) {
               this.cardReveal.reset(0.0F);
               this.cardReveal.method2810(1.0F);
            }

            this.cardClosing = false;
            this.selectedUid = selectedUid;
            this.selectedName = selectedName;
            CloudConnectionState cloudConnectionState = state();
            PartyPlayerInfo partyPlayerInfo = cloudConnectionState == null ? null : cloudConnectionState.method8039(selectedUid);
            this.selectedMcName = partyPlayerInfo == null ? "" : partyPlayerInfo.getText2();

            for (StoreApi.Data4 data4 : this.online) {
               if (data4.uid() == selectedUid) {
                  this.selectedMcName = data4.getText2();
                  break;
               }
            }

            this.invalidate();
         }
      }
   }

   private void buildGlobalAside() {
      if (this.selectedUid != 0L) {
         this.buildSelectedAside();
      } else if (siteMode()) {
         this.buildSiteOnlineAside();
      } else {
         this.buildNodeOnlineAside();
      }
   }

   private void buildSelectedAside() {
      Cloud cloud2 = cloud();
      CloudConnectionState cloudConnectionState = state();
      boolean flag = cloud2 != null && cloud2.getFriends().method6442(this.selectedName);
      boolean flag2 = cloudConnectionState != null && cloudConnectionState.method8061(this.selectedName);
      this.asideCustom(
         24.0F + GuiStyle.PADDING.intValue(),
         (var1x, var2x, var3x, var4x, animationPosY, f, f2, zenithStyle) -> {
            float f3 = this.cardReveal.method2808(this.cardClosing ? 0.0F : 1.0F);
            float f4 = (1.0F - f3) * 10.0F;
            float previewAlpha = f2 * f3;
            this.avatar(var1x, this.selectedUid, this.selectedName, var4x + 8.0F + f4, animationPosY, 24.0F, previewAlpha, zenithStyle);
            Font font = Fonts.NEW_MEDIUM.getFont(5.6F);
            Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
            float floatValue = var4x + 8.0F + 24.0F + 5.0F + f4;
            float maxWidth = f - 16.0F - 24.0F - 5.0F;
            var1x.drawText(font, clip(this.selectedName, font, maxWidth), floatValue, animationPosY + 2.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
            String name = this.selectedMcName.isEmpty() ? t("gui.chat.mcUnknown") : this.selectedMcName;
            var1x.drawText(
               font2, clip(name, font2, maxWidth), floatValue, animationPosY + 2.0F + font.height() + 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         }
      );
      if (siteMode() || flag && !flag2) {
         this.asideAction("gui.friends.write", MenuGlyph.CHAT, () -> t("gui.chat.write.direct"), () -> {
            this.openPeer(this.selectedUid, this.selectedName);
            this.selectTab(1);
         }, "gui.chat.write.desc");
      }

      if (flag2) {
         this.asideInfo("gui.chat.request", () -> t("gui.chat.request.sent"), "gui.chat.request.desc");
      } else if (!flag) {
         this.asideAction("gui.chat.addFriend", MenuGlyph.FRIENDS, () -> t("gui.chat.addFriend.value"), () -> {
            Cloud var1x = cloud();
            boolean var2x = var1x != null && var1x.method7904(0, this.selectedName);
            this.notice = t(var2x ? "gui.chat.addFriend.sent" : "gui.chat.addFriend.already");
            this.noticeAt = System.currentTimeMillis();
            this.invalidate();
         }, "gui.chat.addFriend.desc");
      }

      this.asideAction("gui.chat.close", MenuGlyph.CLOSE, () -> t("gui.chat.close.value"), this::closeCard, "gui.chat.close.desc");
   }

   private void closeCard() {
      if (this.selectedUid != 0L && !this.cardClosing) {
         this.cardClosing = true;
         this.cardReveal.method2810(0.0F);
      }
   }

   private void buildSiteOnlineAside() {
      CloudChatClient cloudChatClient = global();
      this.asideCaption("gui.chat.caption.online");
      this.asideInfo("gui.chat.total", () -> String.valueOf(global().getOnline().size()), "gui.chat.online.desc");
      this.asideInfo("gui.chat.messagesTotal", () -> String.valueOf(global().getData2().getIntValue()), "gui.chat.messagesTotal.desc");
      this.asideInfo("gui.chat.link", () -> t(global().isLive() ? "gui.chat.link.live" : "gui.chat.link.slow"), "gui.chat.link.desc");
      if (!cloudChatClient.error().isEmpty()) {
         this.asideInfo("gui.chat.error", () -> clipText(global().error()), "gui.chat.error.site.desc");
      }

      int n = 0;

      for (CloudChatClient.Data data : cloudChatClient.getOnline()) {
         int n2 = n++;
         long longValue = data.getLongValue();
         String name = data.name();
         this.asideCustom(24.0F, (var6x, n3, var8x, f, f2, f3, f4, zenithStyle) -> {
            float reveal = this.reveal(n2);
            this.renderMemberRow(var6x, f + (1.0F - reveal) * 8.0F, f2, f3, f4 * reveal, zenithStyle, longValue, name, data.role());
         }, () -> this.selectFromChat(longValue, name), () -> longValue == this.selectedUid);
      }

      if (cloudChatClient.getOnline().isEmpty()) {
         this.asideInfo("gui.chat.listEmpty", () -> "", "gui.chat.listEmpty.site.desc");
      }
   }

   private void buildNodeOnlineAside() {
      CloudConnectionState cloudConnectionState = state();
      this.asideCaption("gui.chat.caption.nowInCloud");
      this.asideInfo("gui.chat.total", () -> {
         CloudConnectionState state2 = state();
         return state2 != null && state2.method8035() ? String.valueOf(state2.getIntValue()) : "-";
      }, "gui.chat.total.desc");
      this.asideInfo("gui.chat.ofThemFriends", () -> {
         CloudConnectionState state2 = state();
         return state2 == null ? "-" : String.valueOf(state2.method8045());
      }, "gui.chat.ofThemFriends.desc");
      this.asideInfo("gui.chat.site", () -> t("gui.chat.site.absent"), "gui.chat.site.desc");
      if (!this.error.isEmpty()) {
         this.asideInfo("gui.chat.error", () -> this.error, "gui.chat.error.list.desc");
      }

      List<StoreApi.Data4> online = this.online;
      if (!online.isEmpty()) {
         this.asideCaption("gui.chat.caption.online");
         Cloud cloud2 = cloud();
         long longValue = cloud2 == null ? 0L : cloud2.getLongValue();
         int n = 0;

         for (StoreApi.Data4 data4 : online) {
            boolean flag = data4.uid() == longValue;
            int n2 = n++;
            this.asideCustom(24.0F, (var4x, n3, var6x, f, var8x, var9x, var10x, zenithStyle) -> {
               float reveal = this.reveal(n2);
               this.renderOnlineRow(var4x, f + (1.0F - reveal) * 8.0F, var8x, var9x, var10x * reveal, zenithStyle, data4, flag);
            }, () -> this.selectFromChat(data4.uid(), data4.username()), () -> false);
         }
      } else {
         this.asideInfo(cloudConnectionState != null && cloudConnectionState.method8035() ? "gui.chat.listEmpty" : "gui.chat.noLink", () -> "", "gui.chat.listEmpty.desc");
      }
   }

   private void renderMemberRow(MenuDrawContext menuDrawContext, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, long longValue, String name, String role) {
      float floatValue = f + 8.0F;
      this.avatar(menuDrawContext, longValue, name, floatValue, f2 + 4.0F, 16.0F, previewAlpha, zenithStyle);
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float floatValue2 = floatValue + 16.0F + 4.0F;
      float maxWidth = f3 - 16.0F - 16.0F - 4.0F;
      float centeredTextY = f2 + (24.0F - font.height() - font2.height() - 1.0F) / 2.0F;
      menuDrawContext.drawText(font, clip(name, font, maxWidth), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawText(font2, clip(roleLabel(role), font2, maxWidth), floatValue2, centeredTextY + font.height() + 1.0F, roleColor(role, zenithStyle).method7294(previewAlpha));
   }

   private void renderOnlineRow(MenuDrawContext menuDrawContext, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle, StoreApi.Data4 data4, boolean flag) {
      float floatValue = f + 8.0F;
      this.avatar(menuDrawContext, data4.uid(), data4.username(), floatValue, f2 + 4.0F, 16.0F, previewAlpha, zenithStyle);
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float floatValue2 = floatValue + 16.0F + 4.0F;
      float maxWidth = f3 - 16.0F - 16.0F - 4.0F;
      float centeredTextY = f2 + (24.0F - font.height() - font2.height() - 1.0F) / 2.0F;
      menuDrawContext.drawText(
         font,
         clip(data4.username(), font, maxWidth),
         floatValue2,
         centeredTextY,
         (flag ? zenithStyle.getPrimaryColor().getZenithColor() : zenithStyle.getTextEnable().getZenithColor()).method7294(previewAlpha)
      );
      String name = flag ? t("gui.chat.you") : data4.getText2();
      menuDrawContext.drawText(font2, clip(name, font2, maxWidth), floatValue2, centeredTextY + font.height() + 1.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   private void buildDirect() {
      if (!this.hasPeer()) {
         this.knownNodeDirect.clear();
         this.knownDirectPeer = -1L;
         this.ghosts.removeIf(ghost -> ghost.feed == 1);
      } else if (siteMode()) {
         this.buildSiteConversation();
      } else {
         if (this.peerUid != this.knownDirectPeer) {
            this.knownDirectPeer = this.peerUid;
            this.knownNodeDirect.clear();
            this.ghosts.removeIf(ghost -> ghost.feed == 1);
         }

         Cloud cloud2 = cloud();
         long longValue = cloud2 == null ? 0L : cloud2.getLongValue();
         List<StoreApi.Data3> conversation = this.conversation;
         StoreApi.Data3 data3 = conversation.isEmpty() ? null : (StoreApi.Data3)conversation.get(conversation.size() - 1);
         LinkedHashMap map = new LinkedHashMap();

         for (StoreApi.Data3 data32 : conversation) {
            if (data32.getLongValue() > 0L) {
               map.put(data32.getLongValue(), data32);
            }
         }

         int size = this.ghosts.size();
         this.knownNodeDirect.forEach((var4x, var5x) -> {
            if (!map.containsKey(var4x)) {
               this.ghosts.add(new CloudChatSectionPanel.Ghost(1, var5x.getText(), "", var5x.text(), var5x.time(), var5x.getLongValue2() == longValue));
            }
         });
         this.forgetGone(size);
         this.knownNodeDirect.clear();
         this.knownNodeDirect.putAll(map);
         String author = null;
         boolean mine = false;
         long daySeparator = 0L;

         for (Object mergeWithGhost : this.mergeWithGhosts(conversation, 1)) {
            if (mergeWithGhost instanceof CloudChatSectionPanel.Ghost ghost2) {
               boolean showName = !ghost2.mine && (!ghost2.author.equals(author) || mine);
               daySeparator = this.daySeparator(daySeparator, ghost2.time);
               showName = showName || this.dayBroke && !ghost2.mine;
               this.dayBroke = false;
               author = ghost2.author;
               mine = ghost2.mine;
               this.ghostBubble(ghost2, showName);
            } else {
               StoreApi.Data3 mergeWithGhost2 = (StoreApi.Data3)mergeWithGhost;
               boolean flag = mergeWithGhost2.getLongValue2() == longValue;
               String text = mergeWithGhost2.getText();
               boolean showName = !flag && (!text.equals(author) || mine);
               daySeparator = this.daySeparator(daySeparator, mergeWithGhost2.time());
               showName = showName || this.dayBroke && !flag;
               this.dayBroke = false;
               author = text;
               mine = flag;
               long deleteMenuMessage = mergeWithGhost2.getLongValue();
               this.bubble(
                  text,
                  flag ? 0L : this.peerUid,
                  "",
                  mergeWithGhost2.text(),
                  mergeWithGhost2.time(),
                  flag,
                  showName,
                  mergeWithGhost2 == data3,
                  false,
                  null,
                  flag && deleteMenuMessage > 0L ? () -> this.openDeleteMenu(deleteMenuMessage, this.peerUid) : null
               );
            }
         }
      }
   }

   private void buildSiteConversation() {
      CloudDmClient cloudDmClient = direct();
      long longValue = cloudDmClient.getLongValue();
      List<CloudDmClient.Data> datas = cloudDmClient.method4857();
      CloudDmClient.Data data = datas.isEmpty() ? null : (CloudDmClient.Data)datas.get(datas.size() - 1);
      long daySeparator = 0L;
      boolean flag = false;
      String peerName = null;

      for (CloudDmClient.Data data2 : datas) {
         boolean mine = data2.getLongValue2() != longValue;
         String str = mine ? "" : cloudDmClient.getPeerName();
         boolean flag2 = !mine && (!str.equals(peerName) || flag);
         daySeparator = this.daySeparator(daySeparator, data2.getLongValue4());
         flag2 = flag2 || this.dayBroke && !mine;
         this.dayBroke = false;
         peerName = str;
         flag = mine;
         this.bubble(str, mine ? 0L : longValue, "", data2.body(), data2.getLongValue4(), mine, flag2, data2 == data, mine && data2.read(), null);
      }
   }

   private void buildDirectAside() {
      if (siteMode()) {
         this.buildSiteThreads();
      } else {
         this.buildNodeContacts();
      }
   }

   private void buildSiteThreads() {
      CloudDmClient cloudDmClient = direct();
      String typedName = this.searchQuery() == null ? "" : this.searchQuery().trim();
      if (typedName.length() >= 2) {
         List<UserSearchClient.Data> datas = UserSearchClient.method4758(typedName);
         if (datas != null && !datas.isEmpty()) {
            this.asideCaption("gui.chat.caption.firstMessage");
            int n = 0;

            for (UserSearchClient.Data data : datas) {
               int n2 = n++;
               long uid = data.uid();
               String name = data.name();
               this.asideCustom(
                  24.0F,
                  (menuDrawContext, var6x, var7x, var8x, var9x, var10x, var11x, var12x) -> {
                     float reveal = this.reveal(n2);
                     this.renderThreadRow(
                        menuDrawContext, var6x, var7x, var8x + (1.0F - reveal) * 8.0F, var9x, var10x, var11x * reveal, var12x, uid, name, t("gui.chat.startNew"), 0, null
                     );
                  },
                  () -> this.openPeer(uid, name),
                  () -> uid == direct().getLongValue()
               );
            }
         } else if (UserSearchClient.method4759(typedName)) {
            this.asideInfo("gui.chat.search", () -> t("gui.chat.search.short"), "gui.chat.search.desc");
         } else if (datas == null) {
            this.asideInfo("gui.chat.search", () -> t("gui.chat.search.wait"), "gui.chat.search.desc");
         } else {
            this.asideInfo("gui.chat.search", () -> t("gui.chat.search.none"), "gui.chat.search.desc");
         }
      }

      this.asideCaption("gui.chat.caption.threads");
      if (!cloudDmClient.error().isEmpty()) {
         this.asideInfo("gui.chat.error", () -> clipText(direct().error()), "gui.chat.error.site.desc");
      }

      int n = 0;
      boolean flag = false;

      for (CloudDmClient.Data2 data2 : cloudDmClient.method4856()) {
         if (typedName.isEmpty() || data2.getText().toLowerCase(Locale.ROOT).contains(typedName.toLowerCase(Locale.ROOT))) {
            flag = true;
            long longValue = data2.getLongValue();
            String text = data2.getText();
            String str = data2.isFlag() ? t("gui.chat.mePrefix") + " " + previewOf(data2.getText3()) : previewOf(data2.getText3());
            int intValue = data2.getIntValue();
            long date = data2.getLongValue2();
            String text2 = date > 0L ? TIME.format(new Date(date)) : null;
            int n2;
            int n3 = n2 = n++;
            this.asideCustom(
               24.0F,
               (var8x, var9x, var10x, var11x, var12x, width, var14x, var15x) -> {
                  float var16x = this.reveal(n2);
                  this.renderThreadRow(
                     var8x, var9x, var10x, var11x + (1.0F - var16x) * 8.0F, var12x, width, var14x * var16x, var15x, longValue, text, str, intValue, text2
                  );
               },
               () -> this.openPeer(longValue, text),
               () -> longValue == direct().getLongValue()
            );
         }
      }

      if (!flag && typedName.isEmpty()) {
         this.asideInfo("gui.chat.emptyThreads", () -> "", "gui.chat.emptyThreads.desc");
      }
   }

   private void buildNodeContacts() {
      Cloud cloud2 = cloud();
      CloudConnectionState cloudConnectionState = state();
      if (cloud2 != null && cloudConnectionState != null) {
         this.asideCaption("gui.chat.caption.contacts");
         this.asideInfo("gui.chat.threads", () -> t("gui.chat.threads.gameOnly"), "gui.chat.threads.desc");
         if (!this.error.isEmpty()) {
            this.asideInfo("gui.chat.error", () -> this.error, "gui.chat.error.list.desc");
         }

         boolean flag = false;
         int n = 0;

         for (String selected : cloud2.getFriends().all()) {
            if (!cloudConnectionState.method8061(selected)) {
               long uidOf = cloudConnectionState.uidOf(selected);
               if (uidOf <= 0L && cloud2.prefs() != null) {
                  uidOf = cloud2.prefs().method3311(selected);
               }

               if (uidOf > 0L) {
                  long key = uidOf;
                  flag = true;
                  PartyPlayerInfo partyPlayerInfo = cloudConnectionState.method8044(selected);
                  String username = partyPlayerInfo == null ? selected : partyPlayerInfo.username();
                  boolean flag2 = partyPlayerInfo != null;
                  int n2 = n++;
                  this.asideCustom(
                     24.0F,
                     (var6x, var7x, lastClickY, var9x, y, var11x, var12x, var13x) -> {
                        float var14x = this.reveal(n2);
                        this.renderContact(
                           var6x, var7x, lastClickY, var9x + (1.0F - var14x) * 8.0F, y, var11x, var12x * var14x, var13x, key, username, flag2, key == this.peerUid
                        );
                     },
                     () -> this.openPeer(key, username),
                     () -> key == this.peerUid
                  );
               }
            }
         }

         if (!flag) {
            this.asideInfo("gui.chat.emptyContacts", () -> "", "gui.chat.emptyContacts.desc");
         }
      }
   }

   private void renderThreadRow(
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      float floatValue,
      float y,
      float width,
      float previewAlpha,
      ZenithStyle zenithStyle,
      long uid,
      String name,
      String str,
      int intValue,
      String text
   ) {
      float f = this.selectFade
         .computeIfAbsent(uid, l -> new Animation(160L, 0.0F, Easing.INSTANCE12))
         .method2808(uid == this.openPeerUid() ? 1.0F : 0.0F);
      float f2 = this.contactHover
         .computeIfAbsent(uid, l -> new Animation(130L, 0.0F, Easing.INSTANCE12))
         .method2808(new BoundsData(floatValue, y, width, 24.0F).contains(lastClickX, lastClickY) ? 1.0F : 0.0F);
      float f3 = Math.max(f, f2 * 0.45F);
      if (f3 > 0.002F) {
         menuDrawContext.drawRoundedRect(
            floatValue + 8.0F - 2.0F,
            y + 1.0F,
            width - 16.0F + 4.0F,
            22.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F),
            zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * f3)
         );
      }

      float floatValue2 = floatValue + 8.0F;
      this.avatar(menuDrawContext, uid, name, floatValue2, y + 4.0F, 16.0F, previewAlpha, zenithStyle);
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      Font font3 = Fonts.NEW_REGULAR.getFont(3.8F);
      float floatValue3 = floatValue2 + 16.0F + 4.0F;
      float f4 = floatValue + width - 8.0F;
      float centeredTextY = y + (24.0F - font.height() - font2.height() - 1.0F) / 2.0F;
      float maxWidth = f4 - floatValue3;
      if (text != null) {
         float width2 = font3.width(text);
         menuDrawContext.drawText(font3, text, f4 - width2, centeredTextY + 1.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F));
         maxWidth -= width2 + 3.0F;
      }

      menuDrawContext.drawText(font, clip(name, font, maxWidth), floatValue3, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      float maxWidth2 = f4 - floatValue3;
      float hover = this.unreadPop.computeIfAbsent(uid, l -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(intValue > 0 ? 1.0F : 0.0F);
      if (hover > 0.01F) {
         Font font4 = Fonts.NEW_MEDIUM.getFont(4.2F);
         String text2 = intValue > 99 ? "99+" : String.valueOf(intValue);
         float width2 = Math.max(8.0F, font4.width(text2) + 5.0F);
         float height = 7.0F;
         float f5 = centeredTextY + font.height() + 1.0F;
         float x = 0.6F + 0.4F * hover;
         menuDrawContext.pushMatrix();
         menuDrawContext.pose().translate(f4 - width2 / 2.0F, f5 + height / 2.0F);
         menuDrawContext.pose().scale(x, x);
         menuDrawContext.drawRoundedRect(
            -width2 / 2.0F,
            -height / 2.0F,
            width2,
            height,
            ProxyDotRadiusData.method8069(height / 2.0F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * hover)
         );
         menuDrawContext.drawText(font4, text2, -font4.width(text2) / 2.0F, -font4.height() / 2.0F, new ZenithColor(255, 255, 255).method7294(previewAlpha * hover));
         menuDrawContext.popMatrix();
         maxWidth2 -= width2 * hover + 3.0F;
      }

      menuDrawContext.drawText(
         font2,
         clip(str, font2, maxWidth2),
         floatValue3,
         centeredTextY + font.height() + 1.0F,
         zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getTextSecondary().getZenithColor(), hover).method7294(previewAlpha)
      );
   }

   private void renderContact(
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      float floatValue,
      float y,
      float width,
      float previewAlpha,
      ZenithStyle zenithStyle,
      long key,
      String username,
      boolean flag,
      boolean flag2
   ) {
      float f = this.selectFade.computeIfAbsent(key, l -> new Animation(160L, 0.0F, Easing.INSTANCE12)).method2808(flag2 ? 1.0F : 0.0F);
      float f2 = this.contactHover
         .computeIfAbsent(key, l -> new Animation(130L, 0.0F, Easing.INSTANCE12))
         .method2808(new BoundsData(floatValue, y, width, 24.0F).contains(lastClickX, lastClickY) ? 1.0F : 0.0F);
      float f3 = Math.max(f, f2 * 0.45F);
      if (f3 > 0.002F) {
         menuDrawContext.drawRoundedRect(
            floatValue + 8.0F - 2.0F,
            y + 1.0F,
            width - 16.0F + 4.0F,
            22.0F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F),
            zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * f3)
         );
      }

      float floatValue2 = floatValue + 8.0F;
      this.avatar(menuDrawContext, key, username, floatValue2, y + 4.0F, 16.0F, previewAlpha, zenithStyle);
      float f4 = this.onlineFade.computeIfAbsent(key, l -> new Animation(300L, 0.0F, Easing.INSTANCE13)).method2808(flag ? 1.0F : 0.0F);
      if (f4 > 0.01F) {
         float width2 = 4.0F * f4;
         float floatValue3 = floatValue2 + 16.0F - 4.0F + (4.0F - width2) / 2.0F;
         float y2 = y + 20.0F - 4.0F + (4.0F - width2) / 2.0F;
         menuDrawContext.drawRoundedRect(
            floatValue3, y2, width2, width2, ProxyDotRadiusData.method8069(width2 / 2.0F), zenithStyle.getFriendColor().getZenithColor().method7294(previewAlpha * f4)
         );
      }

      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float floatValue3 = floatValue2 + 16.0F + 4.0F;
      float maxWidth = width - 16.0F - 16.0F - 4.0F;
      float centeredTextY = y + (24.0F - font.height() - font2.height() - 1.0F) / 2.0F;
      menuDrawContext.drawText(font, clip(username, font, maxWidth), floatValue3, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      String body = this.previews.getOrDefault(key, t(flag ? "gui.friends.status.online" : "gui.friends.status.offline"));
      menuDrawContext.drawText(font2, clip(previewOf(body), font2, maxWidth), floatValue3, centeredTextY + font.height() + 1.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   public boolean openDirectWith(String displayName) {
      if (displayName != null && !displayName.isBlank()) {
         CloudConnectionState cloudConnectionState = state();
         long uidOf = cloudConnectionState == null ? 0L : cloudConnectionState.uidOf(displayName);
         if (uidOf <= 0L) {
            Cloud cloud2 = cloud();
            uidOf = cloud2 != null && cloud2.prefs() != null ? cloud2.prefs().method3311(displayName) : 0L;
         }

         if (uidOf <= 0L) {
            return false;
         } else {
            this.openPeer(uidOf, displayName);
            this.selectTab(1);
            return true;
         }
      } else {
         return false;
      }
   }

   private void openPeer(long peerUid, String peerName) {
      if (siteMode()) {
         direct().method4865(peerUid, peerName);
      } else {
         if (this.peerUid == peerUid) {
            return;
         }

         this.peerUid = peerUid;
         this.peerName = peerName;
         this.clearConversation();
         this.polledPeer = -1L;
      }

      this.stickToEnd = true;
      this.pickerOpen = false;
      this.focusInput();
      this.invalidate();
   }

   private void clearConversation() {
      synchronized (this.conversationLock) {
         this.conversation = List.of();
      }
   }

   private void focusInput() {
      this.blurred = false;
      if (this.input != null) {
         this.input.setSelected(true);
      } else {
         this.wantFocus = true;
      }
   }

   @Override
   public float mainHeaderHeight() {
      boolean flag = this.getActiveTab() == 1 && this.hasPeer();
      if (flag) {
         this.barUid = this.openPeerUid();
         this.barName = this.openPeerName();
      }

      return 24.0F * this.peerBarAnimation.method2808(flag ? 1.0F : 0.0F);
   }

   @Override
   public void renderMainHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      long barUid = this.barUid;
      String barName = this.barName;
      float f4 = this.peerBarAnimation.getValue();
      previewAlpha *= f4;
      f2 -= (1.0F - f4) * 4.0F;
      this.avatar(menuDrawContext, barUid, barName, f + 8.0F, f2 + 4.0F, 16.0F, previewAlpha, zenithStyle);
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float floatValue = f + 8.0F + 16.0F + 5.0F;
      float centeredTextY = f2 + (24.0F - font.height() - font2.height() - 1.0F) / 2.0F;
      float maxWidth = f3 - 16.0F - 16.0F - 5.0F - 12.0F;
      menuDrawContext.drawText(font, clip(barName, font, maxWidth), floatValue, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawText(
         font2, clip(this.peerUnder(barUid), font2, maxWidth), floatValue, centeredTextY + font.height() + 1.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      float floatValue2 = f + f3 - 8.0F - 6.0F;
      float centeredTextY2 = f2 + 9.0F;
      float hover = this.glyphHover
         .computeIfAbsent("peerClose", str -> new Animation(130L, 0.0F, Easing.INSTANCE12))
         .method2808(new BoundsData(floatValue2 - 3.0F, centeredTextY2 - 3.0F, 12.0F, 12.0F).contains(lastClickX, lastClickY) ? 1.0F : 0.0F);
      MenuGlyph.CLOSE
         .draw(menuDrawContext, floatValue2, centeredTextY2, zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha));
      menuDrawContext.drawRect(f + 8.0F, f2 + 24.0F - 1.0F, f3 - 16.0F, 1.0F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
   }

   private String peerUnder(long peerUnderFor) {
      if (siteMode()) {
         var datas = direct().method4857();
         int peerUnderStamp = datas.size() * 31 + (datas.isEmpty() ? 0 : (int)((CloudDmClient.Data)datas.get(datas.size() - 1)).getLongValue());
         if (peerUnderFor != this.peerUnderFor || peerUnderStamp != this.peerUnderStamp) {
            this.peerUnderFor = peerUnderFor;
            this.peerUnderStamp = peerUnderStamp;
            this.peerUnderText = t("gui.chat.peer.site");

            for (int i = datas.size() - 1; i >= 0; i--) {
               CloudDmClient.Data data = (CloudDmClient.Data)datas.get(i);
               if (data.getLongValue2() == peerUnderFor) {
                  Coord coord = Coord.method5172(data.body()).method5179("at");
                  if (coord != null) {
                     String peerUnderText = ServerNameUtils.method5643(coord.method5169("s", ""), coord.method5169("m", ""));
                     if (!peerUnderText.isEmpty()) {
                        this.peerUnderText = peerUnderText;
                     }
                  }
                  break;
               }
            }
         }

         return this.peerUnderText;
      } else {
         CloudConnectionState cloudConnectionState = state();
         PartyPlayerInfo partyPlayerInfo = cloudConnectionState == null ? null : cloudConnectionState.method8039(peerUnderFor);
         if (partyPlayerInfo == null) {
            return t("gui.friends.status.offline");
         } else {
            String str = partyPlayerInfo.getText2();
            return str.isEmpty() ? t("gui.friends.status.online") : str;
         }
      }
   }

   private long daySeparator(long daySeparator, long time) {
      long l = dayOf(time);
      if (l != daySeparator && time > 0L) {
         this.dayBroke = true;
         String dayLabel = this.dayLabel(time);
         Font font = Fonts.NEW_REGULAR.getFont(4.2F);
         this.custom(
            font.height() + 7.0F,
            (menuDrawContext, var3x, n, var5x, f, var7x, var8x, zenithStyle) -> menuDrawContext.drawText(
               font, dayLabel, var5x + (var7x - font.width(dayLabel)) / 2.0F, f + 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(var8x * 0.8F)
            )
         );
         return l;
      } else {
         return daySeparator == 0L ? l : daySeparator;
      }
   }

   private static long dayOf(long time) {
      if (time <= 0L) {
         return 0L;
      } else {
         LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
         return localDate.getYear() * 1000L + localDate.getDayOfYear();
      }
   }

   private String dayLabel(long time) {
      long l = dayOf(System.currentTimeMillis());
      long dayOf2 = dayOf(time);
      if (dayOf2 == l) {
         return t("gui.chat.today");
      } else if (dayOf2 == l - 1L) {
         return t("gui.chat.yesterday");
      } else {
         String languageName = Zenith.getInstance().getLocalization().getLanguageName();
         return DAY.computeIfAbsent(languageName, languageTag -> new SimpleDateFormat("d MMMM", Locale.forLanguageTag(languageTag))).format(new Date(time));
      }
   }

   private void bubble(String username, long uid, String role, String body, long time, boolean mine, boolean showName, boolean newest, boolean read, Runnable runnable) {
      this.bubble(username, uid, role, body, time, mine, showName, newest, read, runnable, null);
   }

   private void bubble(
      String username, long uid, String role, String text, long time, boolean mine, boolean showName, boolean newest, boolean read, Runnable onClick, Runnable onRightClick
   ) {
      this.bubble(username, uid, role, text, time, mine, showName, newest, read, onClick, onRightClick, 1.0F);
   }

   private void bubble(
      String username,
      long uid,
      String role,
      String text,
      long time,
      boolean mine,
      boolean showName,
      boolean newest,
      boolean read,
      Runnable onClick,
      Runnable onRightClick,
      float factor
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
      Font textFont = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font timeFont = Fonts.NEW_REGULAR.getFont(4.2F);
      float mainWidth = this.mainWidth();
      CloudChatSectionPanel.BubbleKey bubbleKey = new CloudChatSectionPanel.BubbleKey(
         username, uid, role, text, time, mine, showName, newest, read, mainWidth, Zenith.getInstance().getLocalization().getLanguageName(), font, textFont, timeFont
      );
      CloudChatSectionPanel.BubbleLayout bubbleLayout = this.bubbleLayouts.get(bubbleKey);
      if (bubbleLayout == null) {
         bubbleLayout = this.layoutBubble(username, uid, role, text, time, mine, showName, newest, read, mainWidth, font, textFont, timeFont);
         this.bubbleLayouts.put(bubbleKey, bubbleLayout);
      }

      CloudChatSectionPanel.Bubble shape = bubbleLayout.shape();
      float height = shape.height();
      SectionPanel.AsideRenderer entering = (menuDrawContext, var4x, var5x, var6x, animationPosY, var8x, var9x, var10x) -> this.renderBubble(
         menuDrawContext, var6x, animationPosY, var8x, var9x * factor, var10x, shape
      );
      float hintsHeight = (height + 3.0F) * factor;
      if (factor < 0.999F) {
         this.custom(hintsHeight, entering);
      } else {
         Coord coord = bubbleLayout.coord();
         if (coord != null) {
            this.custom(hintsHeight, entering, () -> this.markCoords(coord, username), () -> uid == this.selectedUid, onRightClick != null ? onRightClick : () -> this.copyCoords(coord));
         } else {
            if (onClick == null && onRightClick == null) {
               this.custom(hintsHeight, entering);
            } else {
               this.custom(hintsHeight, entering, onClick, () -> uid == this.selectedUid, onRightClick);
            }
         }
      }
   }

   private CloudChatSectionPanel.BubbleLayout layoutBubble(
      String username,
      long uid,
      String role,
      String text,
      long time,
      boolean mine,
      boolean showName,
      boolean newest,
      boolean read,
      float mainWidth,
      Font font,
      Font textFont,
      Font timeFont
   ) {
      Coord.Data data = Coord.method5172(text);
      ArrayList<Coord> cards = new ArrayList();

      for (Coord coord : data.getItems()) {
         if (ChatCards.isCard(coord)) {
            cards.add(coord);
         }
      }

      Coord coord = data.method5179("at");
      String str = coord == null ? "" : ServerNameUtils.method5643(coord.method5169("s", ""), coord.method5169("m", ""));
      String roleLabel2 = mine ? "" : roleLabel(role);
      float f = mine ? 0.0F : 20.0F;
      float f2 = Math.max(40.0F, (mainWidth - 16.0F - f) * 0.78F);
      List lines = data.text().isEmpty() ? new ArrayList() : wrap(data.text(), textFont, f2 - 14.0F);
      if (data.isBroken()) {
         lines.add(t("gui.chat.attachBroken"));
      }

      String text2 = time > 0L ? TIME.format(new Date(time)) : "";
      float f3 = timeFont.width(text2) + (read ? 8.0F : 0.0F);
      if (!lines.isEmpty() && !text2.isEmpty() && textFont.width((String)lines.get(lines.size() - 1)) + 6.0F + f3 > f2 - 14.0F) {
         lines.add("");
      }

      float f4 = 0.0F;

      for (int i = 0; i < lines.size(); i++) {
         float width = textFont.width((String)lines.get(i));
         if (i == lines.size() - 1) {
            width += 6.0F + f3;
         }

         f4 = Math.max(f4, width);
      }

      if (lines.isEmpty()) {
         f4 = Math.max(f4, f3);
      }

      for (Coord card : cards) {
         f4 = Math.max(f4, ChatCards.width(card));
      }

      float width = 0.0F;
      if (showName) {
         width = font.width(username);
         if (!roleLabel2.isEmpty()) {
            width += 4.0F + Fonts.NEW_REGULAR.getFont(3.8F).width(roleLabel2);
         }
      }

      if (!str.isEmpty()) {
         width = Math.max(width, Fonts.NEW_REGULAR.getFont(3.8F).width(str) + 8.0F);
      }

      f4 = Math.max(f4, width);
      float f5 = f2;

      for (Coord card : cards) {
         f5 = Math.max(f5, ChatCards.width(card) + 14.0F);
      }

      boolean bare = lines.isEmpty() && cards.size() == 1 && str.isEmpty() && ((Coord)cards.get(0)).kind().equals("st");
      float width2 = bare ? Math.max(ChatCards.width((Coord)cards.get(0)), width) : Math.min(f5, f4 + 14.0F);
      float f6 = 0.0F;

      for (Coord card : cards) {
         f6 += ChatCards.height(card) + 2.0F;
      }

      float height = bare
         ? (showName ? font.height() + 2.0F : 0.0F) + f6 + timeFont.height() + 1.0F
         : 10.0F
            + (showName ? font.height() + 2.0F : 0.0F)
            + (str.isEmpty() ? 0.0F : Fonts.NEW_REGULAR.getFont(3.8F).height() + 2.0F)
            + (lines.isEmpty() ? 0.0F : lines.size() * (textFont.height() + 1.5F) - 1.5F)
            + f6;
      if (!bare && lines.isEmpty() && cards.isEmpty()) {
         height += textFont.height();
      }

      CloudChatSectionPanel.Bubble bubble = new CloudChatSectionPanel.Bubble(
         username, uid, roleLabel2, str, (List<String>)lines, cards, text2, read, mine, showName, newest, bare, width2, height, font, textFont, timeFont
      );
      return new CloudChatSectionPanel.BubbleLayout(bubble, data.method5179("coord"));
   }

   private List<Object> mergeWithGhosts(List<? extends Object> globalMessages, int n) {
      boolean flag = false;

      for (CloudChatSectionPanel.Ghost ghost : this.ghosts) {
         if (ghost.feed == n) {
            flag = true;
            break;
         }
      }

      if (!flag) {
         return new ArrayList<>(globalMessages);
      } else {
         ArrayList<Object> arrayList = new ArrayList<>(globalMessages);

         for (CloudChatSectionPanel.Ghost ghost : this.ghosts) {
            if (ghost.feed == n) {
               arrayList.add(ghost);
            }
         }

         arrayList.sort(Comparator.comparingLong(CloudChatSectionPanel::timeOf));
         return arrayList;
      }
   }

   private static long timeOf(Object obj) {
      if (obj instanceof CloudChatSectionPanel.Ghost ghost) {
         return ghost.time;
      } else if (obj instanceof CloudPackets.Data13 data13) {
         return data13.time();
      } else if (obj instanceof CloudChatClient.Data3 data3) {
         return data3.getLongValue3();
      } else {
         return obj instanceof StoreApi.Data3 data32 ? data32.time() : 0L;
      }
   }

   private void ghostBubble(CloudChatSectionPanel.Ghost ghost, boolean showName) {
      this.bubble(ghost.author, 0L, ghost.role, ghost.body, ghost.time, ghost.mine, showName, false, false, null, null, ghost.factor());
   }

   private void markCoords(Coord coord, String username) {
      String str = CloudWaypointsModule.INSTANCE
         .method6402(
            coord.method5170("x", 0) + 0.5,
            coord.method5170("y", 0) + 0.5,
            coord.method5170("z", 0) + 0.5,
            username,
            coord.method5169("n", ""),
            coord.method5169("s", ""),
            coord.method5169("d", "")
         );
      this.notice = t(str.isEmpty() ? "gui.chat.marked" : str);
      this.noticeAt = System.currentTimeMillis();
   }

   private void copyCoords(Coord coord) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.keyboardHandler != null) {
         minecraft.keyboardHandler.setClipboard(ChatCards.coordText(coord));
         this.notice = t("gui.chat.coordsCopied");
         this.noticeAt = System.currentTimeMillis();
      }
   }

   private void renderBubble(MenuDrawContext menuDrawContext, float f, float animationPosY, float f2, float previewAlpha, ZenithStyle zenithStyle, CloudChatSectionPanel.Bubble shape) {
      float f3 = shape.newest() ? this.messageArrival.method2808(1.0F) : 1.0F;
      float f4 = (1.0F - f3) * (shape.mine() ? 12.0F : -12.0F);
      previewAlpha *= 0.25F + 0.75F * f3;
      float f5 = shape.mine() ? 0.0F : 20.0F;
      float floatValue = (shape.mine() ? f + f2 - 8.0F - shape.width() : f + 8.0F + f5) + f4;
      if (!shape.mine() && shape.showName() && shape.authorUid() > 0L) {
         this.avatar(menuDrawContext, shape.authorUid(), shape.author(), f + 8.0F + f4, animationPosY, 16.0F, previewAlpha, zenithStyle);
      }

      ProxyDotRadiusData proxyDotRadiusData = shape.mine() ? new ProxyDotRadiusData(5.0F, 5.0F, 1.5F, 5.0F) : new ProxyDotRadiusData(5.0F, 5.0F, 5.0F, 1.5F);
      ZenithColor zenithColor = shape.mine()
         ? zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.3F)
         : zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 2.2F);
      if (!shape.bare()) {
         menuDrawContext.drawRoundedRectBatched(floatValue, animationPosY, shape.width(), shape.height(), proxyDotRadiusData, zenithColor);
         menuDrawContext.flushRoundedRects();
      }

      float centeredTextY = shape.bare() ? animationPosY : animationPosY + 5.0F;
      float floatValue2 = floatValue + (shape.bare() ? 0.0F : 7.0F);
      if (shape.showName()) {
         menuDrawContext.drawText(shape.nameFont(), shape.author(), floatValue2, centeredTextY, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
         if (!shape.badge().isEmpty()) {
            Font font = Fonts.NEW_REGULAR.getFont(3.8F);
            menuDrawContext.drawText(
               font, shape.badge(), floatValue2 + shape.nameFont().width(shape.author()) + 4.0F, centeredTextY + 0.6F, roleColor(shape.badge(), zenithStyle).method7294(previewAlpha * 0.9F)
            );
         }

         centeredTextY += shape.nameFont().height() + 2.0F;
      }

      if (!shape.where().isEmpty()) {
         Font font = Fonts.NEW_REGULAR.getFont(3.8F);
         MenuGlyph.WORLD.draw(menuDrawContext, floatValue + 7.0F, centeredTextY - 0.5F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F));
         menuDrawContext.drawText(font, shape.where(), floatValue + 7.0F + 6.0F + 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.9F));
         centeredTextY += font.height() + 2.0F;
      }

      for (String line : shape.lines()) {
         menuDrawContext.drawText(shape.textFont(), line, floatValue + 7.0F, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha * 0.92F));
         centeredTextY += shape.textFont().height() + 1.5F;
      }

      for (Coord card : shape.cards()) {
         ChatCards.render(menuDrawContext, card, floatValue + (shape.bare() ? 0.0F : 7.0F), centeredTextY, previewAlpha, zenithStyle);
         centeredTextY += ChatCards.height(card) + 2.0F;
      }

      if (!shape.time().isEmpty()) {
         float floatValue3 = floatValue + shape.width() - (shape.bare() ? 0.0F : 7.0F) - shape.timeFont().width(shape.time());
         if (shape.read()) {
            MenuGlyph.CHECK
               .draw(
                  menuDrawContext,
                  floatValue3 - 6.0F - 1.0F,
                  animationPosY + shape.height() - (shape.bare() ? 0.0F : 5.0F) - 6.0F,
                  zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
               );
         }

         menuDrawContext.drawText(
            shape.timeFont(),
            shape.time(),
            floatValue3,
            animationPosY + shape.height() - (shape.bare() ? 0.0F : 5.0F) - shape.timeFont().height() + 0.5F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   private void avatar(MenuDrawContext menuDrawContext, long uid, String name, float floatValue, float animationPosY, float height, float previewAlpha, ZenithStyle zenithStyle) {
      Identifier texture = uid > 0L ? CloudAvatarCache.get(Long.toString(uid)) : null;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(height / 2.0F);
      if (texture != null) {
         float f = this.avatarFade.computeIfAbsent(uid, l -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
         if (f < 1.0F) {
            menuDrawContext.drawRoundedRect(floatValue, animationPosY, height, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 2.4F * (1.0F - f)));
         }

         menuDrawContext.drawRoundedTexture(texture, floatValue, animationPosY, height, height, headerRadius, ZenithColor.INSTANCE.method7294(previewAlpha * f));
      } else {
         menuDrawContext.drawRoundedRect(floatValue, animationPosY, height, height, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 2.4F));
         String upperCase = name != null && !name.isEmpty() ? name.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         Font font = Fonts.NEW_MEDIUM.getFont(height * 0.5F);
         menuDrawContext.drawText(
            font,
            upperCase,
            floatValue + (height - font.width(upperCase)) / 2.0F,
            animationPosY + (height - font.height()) / 2.0F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   @Override
   public float footerHeight() {
      return 34.0F + GuiStyle.PADDING.intValue() + this.errorRoom();
   }

   private float errorRoom() {
      return this.error.isEmpty() && this.errorAnimation.getValue() <= 0.01F ? 0.0F : 7.0F;
   }

   @Override
   public void renderFooter(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float width, float previewAlpha, ZenithStyle zenithStyle) {
      if (this.input == null) {
         this.input = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.chat.inputHint"), width);
      }

      int n = siteMode() ? (this.getActiveTab() == 1 ? 1000 : 500) : 256;
      int n2 = siteMode() ? 120 : 0;
      int n3 = n - n2 - (this.pendingTag.isEmpty() ? 0 : this.pendingTag.length() + 1);
      this.input.setMaxLength(siteMode() ? Math.max(64, n3) : Math.max(1, n3));
      float errorRoom = this.errorRoom();
      float f = y + GuiStyle.PADDING.intValue() + errorRoom + 7.0F;
      menuDrawContext.drawRect(floatValue + 8.0F, y, width - 16.0F, 1.0F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
      if (!this.error.isEmpty()) {
         this.shownError = this.error;
      }

      float f2 = this.errorAnimation.method2808(this.error.isEmpty() ? 0.0F : 1.0F);
      if (f2 > 0.01F && !this.shownError.isEmpty()) {
         Font font = Fonts.NEW_REGULAR.getFont(4.2F);
         menuDrawContext.drawText(
            font,
            clip(this.shownError, font, width - 16.0F),
            floatValue + 8.0F,
            y + GuiStyle.PADDING.intValue() + (1.0F - f2) * 3.0F,
            ERROR_COLOR.method7294(previewAlpha * f2)
         );
      }

      if (this.wantFocus) {
         this.wantFocus = false;
         this.blurred = false;
         this.input.setSelected(true);
      }

      if (!this.blurred && !this.input.isSelected()) {
         this.input.setSelected(true);
      }

      float f3 = this.focusAnimation.method2808(this.input.isSelected() ? 1.0F : 0.0F);
      menuDrawContext.drawRoundedRectBatched(
         floatValue + 8.0F,
         f + 2.0F,
         width - 16.0F,
         16.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         zenithStyle.getPanelLeftBackground().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), f3 * 0.18F).method7294(previewAlpha)
      );
      menuDrawContext.flushRoundedRects();
      float centeredTextY = f + 7.0F;
      float floatValue2 = floatValue + 8.0F + 4.0F;
      this.coordsButton = this.glyphButton(menuDrawContext, "coords", MenuGlyph.GPS, floatValue2, centeredTextY, lastClickX, lastClickY, false, previewAlpha, zenithStyle);
      floatValue2 += 11.0F;
      this.inventoryButton = this.glyphButton(menuDrawContext, "inventory", MenuGlyph.CHESTPLATE, floatValue2, centeredTextY, lastClickX, lastClickY, false, previewAlpha, zenithStyle);
      floatValue2 += 11.0F;
      this.stickerButton = this.glyphButton(menuDrawContext, "stickers", MenuGlyph.STICKER, floatValue2, centeredTextY, lastClickX, lastClickY, this.pickerOpen, previewAlpha, zenithStyle);
      floatValue2 += 12.0F;
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      String openPeerName = this.getActiveTab() == 1 ? (this.hasPeer() ? this.openPeerName() : t("gui.chat.toWhom")) : t("gui.chat.toAll");
      if (!this.notice.isEmpty() && System.currentTimeMillis() - this.noticeAt > 2000L) {
         this.notice = "";
      }

      if (!this.notice.isEmpty()) {
         this.shownNotice = this.notice;
      }

      float f4 = this.noticeAnimation.method2808(this.notice.isEmpty() ? 0.0F : 1.0F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
      float f5 = Math.max(font2.width(openPeerName), this.shownNotice.isEmpty() ? 0.0F : font2.width(this.shownNotice) * f4);
      float f6 = floatValue + width - 8.0F - 4.0F;
      float floatValue3 = floatValue2;
      if (!this.pendingTag.isEmpty()) {
         this.shownLabel = this.pendingLabel;
      }

      float f7 = this.pendingAnimation.method2808(this.pendingTag.isEmpty() ? 0.0F : 1.0F);
      if (f7 > 0.01F && !this.shownLabel.isEmpty()) {
         Font font3 = Fonts.NEW_REGULAR.getFont(4.0F);
         float width2 = font3.width(this.shownLabel) + 6.0F + 9.0F;
         menuDrawContext.drawRoundedRect(
            floatValue2,
            f + 5.0F,
            width2 * f7,
            10.0F,
            ProxyDotRadiusData.method8069(5.0F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.28F * f7)
         );
         float f8 = f7 * f7;
         menuDrawContext.drawText(
            font3, this.shownLabel, floatValue2 + 4.0F, f + (20.0F - font3.height()) / 2.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha * f8)
         );
         MenuGlyph.CLOSE.draw(menuDrawContext, floatValue2 + width2 * f7 - 6.0F - 3.0F, f + 7.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * f8));
         this.pendingButton = this.pendingTag.isEmpty() ? null : new BoundsData(floatValue2, f, width2, 20.0F);
         floatValue3 = floatValue2 + (width2 + 4.0F) * f7;
      } else {
         this.pendingButton = null;
      }

      this.input.setFont(font);
      float guiScaledWidth = Math.max(floatValue3, f6 - f5 - 6.0F);
      this.input.setWidth(Math.max(20.0F, guiScaledWidth - floatValue3));
      menuDrawContext.enableScissor(floatValue3, f + 2.0F, guiScaledWidth, f + 20.0F - 2.0F);
      this.input
         .method987(
            menuDrawContext,
            floatValue3,
            f + (20.0F - font.height()) / 2.0F,
            zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      menuDrawContext.disableScissor();
      float f8 = f + (20.0F - font2.height()) / 2.0F;
      if (f4 < 0.99F) {
         menuDrawContext.drawText(
            font2, openPeerName, f6 - font2.width(openPeerName), f8 + f4 * 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * (1.0F - f4))
         );
      }

      if (f4 > 0.01F && !this.shownNotice.isEmpty()) {
         menuDrawContext.drawText(
            font2,
            this.shownNotice,
            f6 - font2.width(this.shownNotice),
            f8 - (1.0F - f4) * 3.0F,
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * f4)
         );
      }

      this.pickerAt = new BoundsData(floatValue, f, width, previewAlpha);
   }

   @Override
   public void renderFloating(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, ZenithStyle zenithStyle) {
      if (zenithStyle != null) {
         BoundsData pickerAt = this.pickerAt;
         float f2 = this.pickerAnimation.method2808(this.pickerOpen ? 1.0F : 0.0F);
         if (pickerAt != null && !(f2 <= 0.01F)) {
            ScissorFlusher.flush();
            this.renderPicker(menuDrawContext, lastClickX, lastClickY, pickerAt.getFloatValue(), pickerAt.getFloatValue2(), pickerAt.width(), pickerAt.height(), zenithStyle, f2);
         } else {
            this.pickerBounds = null;
         }

         if (!this.ghosts.isEmpty()) {
            long now = System.currentTimeMillis();
            this.ghosts.removeIf(var2x -> now - var2x.startedAt >= 240L);
            this.invalidate();
         }

         this.renderDeleteMenu(menuDrawContext, lastClickX, lastClickY, f, zenithStyle);
      }
   }

   private void renderDeleteMenu(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, ZenithStyle zenithStyle) {
      float f2 = this.deleteMenuAnimation.method2808(this.deleteMenuMessage != 0L ? 1.0F : 0.0F);
      BoundsData mainViewport = this.mainViewport();
      if (!(f2 <= 0.01F) && mainViewport != null) {
         ScissorFlusher.flush();
         Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
         String text = t("gui.chat.delete");
         float width = font.width(text) + 18.0F;
         float height = 15.0F;
         float floatValue = mainViewport.getFloatValue() + mainViewport.width() - width - 7.0F;
         float f3 = Float.isNaN(this.deleteMenuAnchorTop)
            ? mainViewport.getFloatValue2() + 4.0F
            : Math.max(mainViewport.getFloatValue2() + 4.0F, this.deleteMenuAnchorTop - height - 2.0F);
         float y = f3 - (1.0F - f2) * 5.0F;
         float previewAlpha = f * f2;
         this.deleteMenuBounds = new BoundsData(floatValue, y, width, height);
         menuDrawContext.drawRoundedRect(
            floatValue,
            y,
            width,
            height,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedBorder(
            floatValue,
            y,
            width,
            height,
            0.5F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.5F)
         );
         boolean contains = this.deleteMenuBounds.contains(lastClickX, lastClickY);
         float f4 = this.deleteMenuHover.method2808(contains ? 1.0F : 0.0F);
         if (f4 > 0.01F) {
            menuDrawContext.drawRoundedRect(
               floatValue + 1.0F,
               y + 1.0F,
               width - 2.0F,
               height - 2.0F,
               ProxyDotRadiusData.method8069(Math.max(1.0F, GuiStyle.ROUND.intValue() * 0.6F - 1.0F)),
               zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * f4)
            );
         }

         menuDrawContext.drawText(
            font, text, floatValue + 9.0F, y + (height - font.height()) / 2.0F, ERROR_COLOR.method7295(ZenithColor.INSTANCE, f4 * 0.2F).method7294(previewAlpha)
         );
      } else {
         this.deleteMenuBounds = null;
      }
   }

   private BoundsData glyphButton(
      MenuDrawContext menuDrawContext, String key, MenuGlyph menuGlyph, float floatValue, float centeredTextY, int lastClickX, int lastClickY, boolean pickerOpen, float previewAlpha, ZenithStyle zenithStyle
   ) {
      BoundsData boundsData = new BoundsData(floatValue - 2.0F, centeredTextY - 3.0F, 10.0F, 12.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hover = this.glyphHover.computeIfAbsent(key, str -> new Animation(130L, 0.0F, Easing.INSTANCE12)).method2808(!contains && !pickerOpen ? 0.0F : 1.0F);
      if (hover > 0.01F) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            ProxyDotRadiusData.method8069(boundsData.width() / 2.0F),
            zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * hover * 0.55F)
         );
      }

      menuGlyph.draw(menuDrawContext, floatValue, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha));
      return boundsData;
   }

   private void renderPicker(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float f, float width, float height, ZenithStyle zenithStyle, float f2) {
      StickerPackService stickerPackService = StickerPackService.getInstance();
      stickerPackService.method5508();
      float f3 = (1.0F - f2) * 10.0F;
      height *= f2;
      float width2 = 26.0F;
      int n = Math.max(1, (int)((width - 16.0F - 6.0F) / width2));
      var datas = stickerPackService.getItems2();
      Font font = Fonts.NEW_REGULAR.getFont(4.2F);
      if (datas.isEmpty()) {
         float height2 = 26.0F;
         float y = f - height2 - 4.0F + f3;
         menuDrawContext.drawRoundedRect(
            floatValue + 8.0F,
            y,
            width - 16.0F,
            height2,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            zenithStyle.getPanelLeftBackground().getZenithColor().method7294(height)
         );
         String text = t("gui.chat.stickers.none");
         menuDrawContext.drawText(
            font,
            text,
            floatValue + 8.0F + (width - 16.0F - font.width(text)) / 2.0F,
            y + (height2 - font.height()) / 2.0F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(height)
         );
         this.pickerBounds = new BoundsData(floatValue + 8.0F, y, width - 16.0F, height2);
      } else {
         int floorMod = Math.floorMod(this.pickerPack, datas.size());
         StickerPackService.Data data = (StickerPackService.Data)datas.get(floorMod);
         int n2 = Math.min(4, (data.getItems().size() + n - 1) / n);
         float height2 = 14.0F + n2 * width2;
         float y = f - height2 - 4.0F + f3;
         menuDrawContext.drawRoundedRect(
            floatValue + 8.0F,
            y,
            width - 16.0F,
            height2,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.6F),
            zenithStyle.getPanelLeftBackground().getZenithColor().method7294(height)
         );
         this.pickerBounds = new BoundsData(floatValue + 8.0F, y, width - 16.0F, height2);
         float f4 = floatValue + 8.0F + 4.0F;
         float f5 = f4;
         float floatValue2 = 0.0F;

         for (int i = 0; i < datas.size(); i++) {
            float f6 = font.width(((StickerPackService.Data)datas.get(i)).name()) + 8.0F;
            if (i == floorMod) {
               f5 = f4;
               floatValue2 = f6;
            }

            f4 += f6 + 3.0F;
         }

         if (!this.packPlateReady) {
            this.packPlateX.method2809(f5 - floatValue);
            this.packPlateWidth.method2809(floatValue2);
            this.packPlateReady = true;
         }

         menuDrawContext.drawRoundedRect(
            floatValue + this.packPlateX.method2808(f5 - floatValue),
            y + 2.0F,
            this.packPlateWidth.method2808(floatValue2),
            10.0F,
            ProxyDotRadiusData.method8069(5.0F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(height * 0.3F)
         );
         f4 = floatValue + 8.0F + 4.0F;

         for (int i = 0; i < datas.size(); i++) {
            String name = ((StickerPackService.Data)datas.get(i)).name();
            float f6 = font.width(name) + 8.0F;
            boolean flag = i == floorMod;
            boolean flag2 = !flag && lastClickX >= f4 && lastClickX <= f4 + f6 && lastClickY >= y + 2.0F && lastClickY <= y + 12.0F;
            float hover = this.cellHover.computeIfAbsent("pack:" + i, str -> new Animation(130L, 0.0F, Easing.INSTANCE12)).method2808(flag2 ? 1.0F : 0.0F);
            ZenithColor zenithColor = flag
               ? zenithStyle.getTextEnable().getZenithColor()
               : zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getTextSecondary().getZenithColor(), hover);
            menuDrawContext.drawText(font, name, f4 + 4.0F, y + 2.0F + (10.0F - font.height()) / 2.0F, zenithColor.method7294(height));
            f4 += f6 + 3.0F;
         }

         float f6 = y + 14.0F;

         for (int i = 0; i < data.getItems().size() && i < n2 * n; i++) {
            int n3 = i / n;
            int n4 = i % n;
            float floatValue3 = floatValue + 8.0F + 3.0F + n4 * width2;
            float y2 = f6 + n3 * width2;
            String key = stickerPackService.method5507(data.getText(), data.getItems().get(i));
            Identifier texture = ChatImageCache.get(key);
            if (texture == null) {
               menuDrawContext.drawRoundedRect(
                  floatValue3 + 2.0F,
                  y2 + 2.0F,
                  width2 - 4.0F,
                  width2 - 4.0F,
                  ProxyDotRadiusData.method8069(3.0F),
                  zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(height * 1.4F)
               );
            } else {
               boolean flag = lastClickX >= floatValue3 && lastClickX <= floatValue3 + width2 && lastClickY >= y2 && lastClickY <= y2 + width2;
               float f7 = this.cellHover.computeIfAbsent(key, str -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(flag ? 1.0F : 0.0F);
               if (f7 > 0.01F) {
                  menuDrawContext.drawRoundedRect(
                     floatValue3, y2, width2, width2, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getDisableActiveBg().getZenithColor().method7294(height * f7)
                  );
               }

               float f8 = this.stickerFade.computeIfAbsent(key, str -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
               float height3 = (width2 - 4.0F) * (1.0F + f7 * 0.1F);
               ChatCards.drawSticker(
                  menuDrawContext,
                  data.getItems().get(i),
                  texture,
                  floatValue3 + (width2 - height3) / 2.0F,
                  y2 + (width2 - height3) / 2.0F,
                  height3,
                  ProxyDotRadiusData.method8069(3.0F),
                  ZenithColor.INSTANCE.method7294(height * f8)
               );
            }
         }
      }
   }

   @Override
   public boolean onFooterClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      if (this.pendingButton != null && this.pendingButton.contains(lastClickX, lastClickY)) {
         this.pendingTag = "";
         this.pendingLabel = "";
         return true;
      } else if (this.coordsButton != null && this.coordsButton.contains(lastClickX, lastClickY)) {
         this.attachCoords();
         return true;
      } else if (this.inventoryButton != null && this.inventoryButton.contains(lastClickX, lastClickY)) {
         this.attachInventory();
         return true;
      } else if (this.stickerButton != null && this.stickerButton.contains(lastClickX, lastClickY)) {
         this.pickerOpen = !this.pickerOpen;
         if (this.pickerOpen) {
            StickerPackService.getInstance().method5508();
         }

         return true;
      } else {
         this.focusInput();
         return true;
      }
   }

   private void prepareStickers() {
      StickerPackService stickerPackService = StickerPackService.getInstance();
      stickerPackService.method5508();
      if (stickerPackService.method5506() && stickerPackService.revision() != this.stickersPrepared) {
         this.stickersPrepared = stickerPackService.revision();
         List<StickerPackService.Data> datas = stickerPackService.getItems2();

         for (StickerPackService.Data data : datas) {
            ChatImageCache.get(stickerPackService.method5507(data.getText(), data.getText3()));
         }

         StickerPackService.Data data = (StickerPackService.Data)datas.get(0);

         for (String string : data.getItems()) {
            ChatImageCache.get(stickerPackService.method5507(data.getText(), string));
         }
      }
   }

   private void attachCoords() {
      String coordsHere = ChatCompose.inWorld() ? ChatCompose.coordsHere("") : "";
      if (coordsHere.isEmpty()) {
         this.error = t("gui.chat.notInWorld");
         this.revision++;
      } else {
         this.pendingTag = coordsHere;
         this.pendingLabel = t("gui.chat.attach.coords");
         this.focusInput();
      }
   }

   private void attachInventory() {
      if (!ChatCompose.inWorld()) {
         this.error = t("gui.chat.notInWorld");
         this.revision++;
      } else if (!ChatCompose.hasItems()) {
         this.error = t("gui.chat.emptyInventory");
         this.revision++;
      } else {
         String inventory = ChatCompose.inventory(this.getActiveTab() == 1, siteMode());
         if (inventory.isEmpty()) {
            this.error = t("gui.chat.inventoryTooBig");
            this.revision++;
         } else {
            this.pendingTag = inventory;
            this.pendingLabel = t("gui.chat.attach.inventory");
            this.focusInput();
         }
      }
   }

   private boolean clickPicker(double lastClickX, double lastClickY) {
      if (this.pickerOpen && this.pickerBounds != null && this.pickerBounds.contains(lastClickX, lastClickY)) {
         var datas = StickerPackService.getInstance().getItems2();
         if (datas.isEmpty()) {
            return true;
         } else {
            Font font = Fonts.NEW_REGULAR.getFont(4.2F);
            float f = this.pickerBounds.getFloatValue2();
            if (lastClickY <= f + 12.0F) {
               float f2 = this.pickerBounds.getFloatValue() + 4.0F;

               for (int i = 0; i < datas.size(); i++) {
                  float f3 = font.width(((StickerPackService.Data)datas.get(i)).name()) + 8.0F;
                  if (lastClickX >= f2 && lastClickX <= f2 + f3) {
                     this.pickerPack = i;
                     return true;
                  }

                  f2 += f3 + 3.0F;
               }

               return true;
            } else {
               float f2 = 26.0F;
               int n = Math.max(1, (int)((this.pickerBounds.width() - 6.0F) / f2));
               int n2 = (int)((lastClickX - this.pickerBounds.getFloatValue() - 3.0) / f2);
               int n3 = (int)((lastClickY - f - 14.0) / f2);
               if (n2 >= 0 && n2 < n && n3 >= 0) {
                  StickerPackService.Data data = (StickerPackService.Data)datas.get(Math.floorMod(this.pickerPack, datas.size()));
                  int n4 = n3 * n + n2;
                  if (n4 >= 0 && n4 < data.getItems().size()) {
                     this.sendBody(StickerPackService.method5510(data.getText(), data.getItems().get(n4)));
                     this.pickerOpen = false;
                  }

                  return true;
               } else {
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      if (this.clickDeleteMenu(lastClickX, lastClickY)) {
         return true;
      } else {
         if (this.deleteMenuMessage != 0L) {
            this.deleteMenuMessage = 0L;
         }

         if (this.clickPicker(lastClickX, lastClickY)) {
            return true;
         } else {
            if (this.pickerOpen) {
               BoundsData footerBounds = this.footerBounds();
               if (footerBounds == null || !footerBounds.contains(lastClickX, lastClickY)) {
                  this.pickerOpen = false;
               }
            }

            BoundsData mainHeaderBounds = this.mainHeaderBounds();
            if (mainHeaderBounds != null && this.hasPeer() && mainHeaderBounds.contains(lastClickX, lastClickY)) {
               if (lastClickX >= mainHeaderBounds.getFloatValue() + mainHeaderBounds.width() - 8.0F - 6.0F - 3.0F) {
                  this.closePeer();
               }

               return true;
            } else {
               boolean flag = super.onMouseClicked(lastClickX, lastClickY, enum2);
               this.focusInput();
               return flag;
            }
         }
      }
   }

   private void closePeer() {
      if (siteMode()) {
         direct().close();
      } else {
         this.peerUid = 0L;
         this.peerName = "";
         this.clearConversation();
      }

      this.invalidate();
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.input != null && this.input.isSelected()) {
         if (keyCode == 257 || keyCode == 335) {
            this.send();
            return true;
         }

         if (keyCode == 256) {
            if (this.deleteMenuMessage != 0L) {
               this.deleteMenuMessage = 0L;
               return true;
            }

            if (this.pickerOpen) {
               this.pickerOpen = false;
               return true;
            }

            this.input.setSelected(false);
            this.blurred = true;
            return true;
         }

         if (this.input.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      return super.keyPressed(keyCode, keycode, modifiers);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      if (this.input != null && this.input.isSelected() && this.input.charTyped(codepoint, n)) {
         if (!this.error.isEmpty()) {
            this.error = "";
            this.revision++;
         }

         return true;
      } else {
         return super.charTyped(codepoint, n);
      }
   }

   @Override
   public boolean mouseScrolled(double lastClickX, double lastClickY, double d, double verticalAmount) {
      this.stickToEnd = false;
      this.deleteMenuMessage = 0L;
      return super.mouseScrolled(lastClickX, lastClickY, d, verticalAmount);
   }

   private void send() {
      String text = this.input == null ? "" : this.input.getText();
      String pendingTag = this.pendingTag;
      if (text != null && !text.isBlank() || !pendingTag.isEmpty()) {
         StringBuilder builder = new StringBuilder(text == null ? "" : text.strip());
         if (!pendingTag.isEmpty()) {
            if (builder.length() > 0) {
               builder.append(' ');
            }

            builder.append(pendingTag);
         }

         this.draft = text == null ? "" : text;
         this.draftTag = pendingTag;
         if (this.sendBody(builder.toString())) {
            this.input.setText("");
            this.input.setCursor(0);
            this.pendingTag = "";
            this.pendingLabel = "";
         }
      }
   }

   private void restoreDraft(Throwable throwable, boolean flag) {
      this.error = flag ? siteError(throwable) : shortError(throwable);
      if (this.input != null && this.input.getText().isEmpty() && !this.draft.isEmpty()) {
         this.input.setText(this.draft);
         this.input.setCursor(this.draft.length());
      }

      if (this.pendingTag.isEmpty() && !this.draftTag.isEmpty()) {
         this.pendingTag = this.draftTag;
         this.pendingLabel = this.labelOfTag(this.draftTag);
      }

      this.revision++;
   }

   private String labelOfTag(String draftTag) {
      Coord coord = Coord.method5172(draftTag).getItems().stream().findFirst().orElse(null);
      if (coord == null) {
         return "";
      } else {
         String kind = coord.kind();

         return switch (kind) {
            case "coord" -> t("gui.chat.attach.coords");
            case "inv" -> t("gui.chat.attach.inventory");
            default -> "";
         };
      }
   }

   private boolean sendBody(String string) {
      boolean flag = this.getActiveTab() == 1;
      int n = !siteMode() ? 256 : (flag ? 1000 : 500);
      String body = string;
      if (siteMode()) {
         String whereTag = ChatCompose.whereTag();
         if (!whereTag.isEmpty() && string.length() + whereTag.length() + 1 <= n) {
            body = string + " " + whereTag;
         }
      }

      String text = Coord.method5174(body, n);
      if (!text.isBlank() && (text.length() >= body.length() || Coord.method5172(text).getItems().size() >= Coord.method5172(body).getItems().size())) {
         this.stickToEnd = true;
         if (flag) {
            long openPeerUid = this.openPeerUid();
            if (openPeerUid == 0L) {
               this.error = t("gui.chat.pickPeer");
               this.revision++;
               return false;
            } else if (siteMode()) {
               direct().method4867(openPeerUid, text).whenComplete((var3x, var4x) -> {
                  if (var4x != null) {
                     if (this.openPeerUid() == openPeerUid) {
                        this.restoreDraft(var4x, true);
                     } else {
                        this.error = siteError(var4x);
                        this.revision++;
                     }
                  } else {
                     this.error = "";
                     this.revision++;
                  }
               });
               return true;
            } else {
               StoreApi storeApi = store();
               if (storeApi == null) {
                  this.error = t("gui.chat.storeUnavailable");
                  this.revision++;
                  return false;
               } else {
                  storeApi.method1966(openPeerUid, text).whenComplete((var4x, var5x) -> {
                     boolean var6x = this.openPeerUid() != openPeerUid;
                     if (var5x != null) {
                        if (var6x) {
                           this.error = shortError(var5x);
                           this.revision++;
                        } else {
                           this.restoreDraft(var5x, false);
                        }
                     } else {
                        if (!var6x) {
                           synchronized (this.conversationLock) {
                              var var8x = new ArrayList<>(this.conversation);
                              var8x.add(var4x);
                              this.conversation = Collections.unmodifiableList(var8x);
                           }
                        }

                        this.rememberPreview(openPeerUid, text);
                        this.error = "";
                        this.revision++;
                     }
                  });
                  return true;
               }
            }
         } else if (siteMode()) {
            global().method2168(text).whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  this.restoreDraft(var2x, true);
               } else {
                  this.error = "";
                  this.revision++;
               }
            });
            return true;
         } else {
            Cloud cloud2 = cloud();
            if (cloud2 != null && cloud2.method7908(0, text)) {
               return true;
            } else {
               CloudConnectionState cloudConnectionState = state();
               this.error = cloudConnectionState != null && !cloudConnectionState.method8035() ? cloudConnectionState.getType().label() : t("gui.chat.sendFailed");
               this.revision++;
               return false;
            }
         }
      } else {
         this.error = t("gui.chat.tooLong");
         this.revision++;
         return false;
      }
   }

   private float reveal(int n) {
      float f = this.listReveal.method2808(1.0F);
      float f2 = Math.min(0.5F, n * 0.06F);
      return f <= f2 ? 0.0F : Math.min(1.0F, (f - f2) / Math.max(0.05F, 1.0F - f2));
   }

   private static String roleLabel(String role) {
      if (role != null && !role.isEmpty()) {
         return switch (role) {
            case "Administrator" -> "админ";
            case "Support" -> "саппорт";
            case "Media" -> "медиа";
            case "Private" -> "private";
            case "Opus", "Recode" -> "opus";
            case "Legacy" -> "legacy";
            case "AutoBuy" -> "autobuy";
            case "Alpha" -> "alpha";
            case "User" -> "";
            default -> role;
         };
      } else {
         return "";
      }
   }

   private static ZenithColor roleColor(String role, ZenithStyle zenithStyle) {
      if (role == null) {
         return zenithStyle.getTextTertiary().getZenithColor();
      } else {
         return switch (role) {
            case "Administrator", "админ", "Private", "private" -> zenithStyle.getPrimaryColor().getZenithColor();
            case "Support", "саппорт", "Media", "медиа", "Opus", "opus", "Recode", "recode" -> zenithStyle.getFriendColor().getZenithColor();
            default -> zenithStyle.getTextTertiary().getZenithColor();
         };
      }
   }

   private static String previewOf(String body) {
      Coord.Data data = Coord.method5172(body);
      if (!data.text().isEmpty()) {
         return data.text();
      } else if (data.isBroken()) {
         return t("gui.chat.attachBroken");
      } else if (data.method5180("st")) {
         return "стикер";
      } else if (data.method5180("coord")) {
         return "координаты";
      } else {
         return data.method5180("inv") ? "инвентарь" : "";
      }
   }

   private static List<String> wrap(String text, Font font, float f) {
      ArrayList<String> strings = new ArrayList<>();
      if (text == null) {
         return strings;
      } else {
         StringBuilder builder = new StringBuilder();

         for (String str : text.split(" ")) {
            while (font.width(str) > f) {
               if (!builder.isEmpty()) {
                  strings.add(builder.toString());
                  builder = new StringBuilder();
               }

               int endIndex = fitting(str, font, f);
               strings.add(str.substring(0, endIndex));
               str = str.substring(endIndex);
            }

            String text2 = builder.isEmpty() ? str : builder + " " + str;
            if (font.width(text2) > f && !builder.isEmpty()) {
               strings.add(builder.toString());
               builder = new StringBuilder(str);
            } else {
               builder = new StringBuilder(text2);
            }
         }

         if (!builder.isEmpty()) {
            strings.add(builder.toString());
         }

         if (strings.isEmpty()) {
            strings.add("");
         }

         return strings;
      }
   }

   private static int fitting(String str, Font font, float f) {
      int n = 1;

      while (n < str.length() && font.width(str.substring(0, n + 1)) <= f) {
         n++;
      }

      return n;
   }

   private static String clip(String name, Font font, float maxWidth) {
      if (name != null && !(font.width(name) <= maxWidth)) {
         CloudChatSectionPanel.ClipKey clipKey = new CloudChatSectionPanel.ClipKey(name, font, maxWidth);
         String str = CLIPS.get(clipKey);
         if (str != null) {
            return str;
         } else {
            String clip = font.clip(name, maxWidth);
            CLIPS.put(clipKey, clip);
            return clip;
         }
      } else {
         return name == null ? "" : name;
      }
   }

   private static String clipText(String error) {
      return error != null && error.length() > 40 ? error.substring(0, 39) + "…" : error;
   }

   private static String siteError(Throwable throwable) {
      return !SiteSession.method2291() ? t("gui.chat.site.needLogin") : shortError(throwable);
   }

   private static String shortError(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      String message = throwable2.getMessage();
      if (message == null || message.isBlank()) {
         message = throwable2.getClass().getSimpleName();
      }

      return clipText(message);
   }

   private static final class Bubble {
      private final String author;
      private final long authorUid;
      private final String badge;
      private final String where;
      private final List<String> lines;
      private final List<Coord> cards;
      private final String time;
      private final boolean read;
      private final boolean mine;
      private final boolean showName;
      private final boolean newest;
      private final boolean bare;
      private final float width;
      private final float height;
      private final Font nameFont;
      private final Font textFont;
      private final Font timeFont;

      public String author() {
         return this.author;
      }

      public long authorUid() {
         return this.authorUid;
      }

      public String badge() {
         return this.badge;
      }

      public String where() {
         return this.where;
      }

      public List<String> lines() {
         return this.lines;
      }

      public List<Coord> cards() {
         return this.cards;
      }

      public String time() {
         return this.time;
      }

      public boolean read() {
         return this.read;
      }

      public boolean mine() {
         return this.mine;
      }

      public boolean showName() {
         return this.showName;
      }

      public boolean newest() {
         return this.newest;
      }

      public boolean bare() {
         return this.bare;
      }

      public float width() {
         return this.width;
      }

      public float height() {
         return this.height;
      }

      public Font nameFont() {
         return this.nameFont;
      }

      public Font textFont() {
         return this.textFont;
      }

      public Font timeFont() {
         return this.timeFont;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Bubble other)) return false;
         return java.util.Objects.equals(this.author, other.author) && java.util.Objects.equals(this.authorUid, other.authorUid) && java.util.Objects.equals(this.badge, other.badge) && java.util.Objects.equals(this.where, other.where) && java.util.Objects.equals(this.lines, other.lines) && java.util.Objects.equals(this.cards, other.cards) && java.util.Objects.equals(this.time, other.time) && java.util.Objects.equals(this.read, other.read) && java.util.Objects.equals(this.mine, other.mine) && java.util.Objects.equals(this.showName, other.showName) && java.util.Objects.equals(this.newest, other.newest) && java.util.Objects.equals(this.bare, other.bare) && java.util.Objects.equals(this.width, other.width) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.nameFont, other.nameFont) && java.util.Objects.equals(this.textFont, other.textFont) && java.util.Objects.equals(this.timeFont, other.timeFont);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(author, authorUid, badge, where, lines, cards, time, read, mine, showName, newest, bare, width, height, nameFont, textFont, timeFont);
      }

      @Override
      public String toString() {
         return "Bubble[" + "author=" + this.author + ", " + "authorUid=" + this.authorUid + ", " + "badge=" + this.badge + ", " + "where=" + this.where + ", " + "lines=" + this.lines + ", " + "cards=" + this.cards + ", " + "time=" + this.time + ", " + "read=" + this.read + ", " + "mine=" + this.mine + ", " + "showName=" + this.showName + ", " + "newest=" + this.newest + ", " + "bare=" + this.bare + ", " + "width=" + this.width + ", " + "height=" + this.height + ", " + "nameFont=" + this.nameFont + ", " + "textFont=" + this.textFont + ", " + "timeFont=" + this.timeFont + "]";
      }

      private Bubble(
         String author,
         long authorUid,
         String badge,
         String where,
         List<String> lines,
         List<Coord> cards,
         String time,
         boolean read,
         boolean mine,
         boolean showName,
         boolean newest,
         boolean bare,
         float width,
         float height,
         Font nameFont,
         Font textFont,
         Font timeFont
      ) {
         this.author = author;
         this.authorUid = authorUid;
         this.badge = badge;
         this.where = where;
         this.lines = lines;
         this.cards = cards;
         this.time = time;
         this.read = read;
         this.mine = mine;
         this.showName = showName;
         this.newest = newest;
         this.bare = bare;
         this.width = width;
         this.height = height;
         this.nameFont = nameFont;
         this.textFont = textFont;
         this.timeFont = timeFont;
      }
   }

   private static final class BubbleKey {
      private final String author;
      private final long authorUid;
      private final String role;
      private final String body;
      private final long time;
      private final boolean mine;
      private final boolean showName;
      private final boolean newest;
      private final boolean read;
      private final float column;
      private final String language;
      private final Font nameFont;
      private final Font textFont;
      private final Font timeFont;

      public String author() {
         return this.author;
      }

      public long authorUid() {
         return this.authorUid;
      }

      public String role() {
         return this.role;
      }

      public String body() {
         return this.body;
      }

      public long time() {
         return this.time;
      }

      public boolean mine() {
         return this.mine;
      }

      public boolean showName() {
         return this.showName;
      }

      public boolean newest() {
         return this.newest;
      }

      public boolean read() {
         return this.read;
      }

      public float column() {
         return this.column;
      }

      public String language() {
         return this.language;
      }

      public Font nameFont() {
         return this.nameFont;
      }

      public Font textFont() {
         return this.textFont;
      }

      public Font timeFont() {
         return this.timeFont;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BubbleKey other)) return false;
         return java.util.Objects.equals(this.author, other.author) && java.util.Objects.equals(this.authorUid, other.authorUid) && java.util.Objects.equals(this.role, other.role) && java.util.Objects.equals(this.body, other.body) && java.util.Objects.equals(this.time, other.time) && java.util.Objects.equals(this.mine, other.mine) && java.util.Objects.equals(this.showName, other.showName) && java.util.Objects.equals(this.newest, other.newest) && java.util.Objects.equals(this.read, other.read) && java.util.Objects.equals(this.column, other.column) && java.util.Objects.equals(this.language, other.language) && java.util.Objects.equals(this.nameFont, other.nameFont) && java.util.Objects.equals(this.textFont, other.textFont) && java.util.Objects.equals(this.timeFont, other.timeFont);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(author, authorUid, role, body, time, mine, showName, newest, read, column, language, nameFont, textFont, timeFont);
      }

      @Override
      public String toString() {
         return "BubbleKey[" + "author=" + this.author + ", " + "authorUid=" + this.authorUid + ", " + "role=" + this.role + ", " + "body=" + this.body + ", " + "time=" + this.time + ", " + "mine=" + this.mine + ", " + "showName=" + this.showName + ", " + "newest=" + this.newest + ", " + "read=" + this.read + ", " + "column=" + this.column + ", " + "language=" + this.language + ", " + "nameFont=" + this.nameFont + ", " + "textFont=" + this.textFont + ", " + "timeFont=" + this.timeFont + "]";
      }

      private BubbleKey(
         String author,
         long authorUid,
         String role,
         String body,
         long time,
         boolean mine,
         boolean showName,
         boolean newest,
         boolean read,
         float column,
         String language,
         Font nameFont,
         Font textFont,
         Font timeFont
      ) {
         this.author = author;
         this.authorUid = authorUid;
         this.role = role;
         this.body = body;
         this.time = time;
         this.mine = mine;
         this.showName = showName;
         this.newest = newest;
         this.read = read;
         this.column = column;
         this.language = language;
         this.nameFont = nameFont;
         this.textFont = textFont;
         this.timeFont = timeFont;
      }
   }

   private static final class BubbleLayout {
      private final CloudChatSectionPanel.Bubble shape;
      private final Coord coord;

      public CloudChatSectionPanel.Bubble shape() {
         return this.shape;
      }

      public Coord coord() {
         return this.coord;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BubbleLayout other)) return false;
         return java.util.Objects.equals(this.shape, other.shape) && java.util.Objects.equals(this.coord, other.coord);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shape, coord);
      }

      @Override
      public String toString() {
         return "BubbleLayout[" + "shape=" + this.shape + ", " + "coord=" + this.coord + "]";
      }

      private BubbleLayout(CloudChatSectionPanel.Bubble shape, Coord coord) {
         this.shape = shape;
         this.coord = coord;
      }
   }

   private static final class ClipKey {
      private final String text;
      private final Font font;
      private final float maxWidth;

      public String text() {
         return this.text;
      }

      public Font font() {
         return this.font;
      }

      public float maxWidth() {
         return this.maxWidth;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ClipKey other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.maxWidth, other.maxWidth);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, font, maxWidth);
      }

      @Override
      public String toString() {
         return "ClipKey[" + "text=" + this.text + ", " + "font=" + this.font + ", " + "maxWidth=" + this.maxWidth + "]";
      }

      private ClipKey(String text, Font font, float maxWidth) {
         this.text = text;
         this.font = font;
         this.maxWidth = maxWidth;
      }
   }

   private static final class Ghost {
      private final int feed;
      private final String author;
      private final String role;
      private final String body;
      private final long time;
      private final boolean mine;
      private final long startedAt = System.currentTimeMillis();

      private Ghost(int feed, String author, String role, String body, long time, boolean mine) {
         this.feed = feed;
         this.author = author;
         this.role = role;
         this.body = body;
         this.time = time;
         this.mine = mine;
      }

      private float factor() {
         float f = 1.0F - (float)(System.currentTimeMillis() - this.startedAt) / 240.0F;
         f = Math.max(0.0F, Math.min(1.0F, f));
         return f * f;
      }
   }
}

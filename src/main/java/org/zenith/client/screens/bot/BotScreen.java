package org.zenith.client.screens.bot;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import java.util.Map.Entry;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.bot.BotAvatarCache;
import org.zenith.base.bot.ServerIconCache;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.ChatMessage;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.BotModuleManager;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.via.BotProtocolVersions;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.base.font.Font;
import org.zenith.base.font.FontData;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class BotScreen extends CustomScreen {
   private static final float leftPanelWidth = 240.0F;
   private static final float rightPanelWidth = 240.0F;
   private static final float panelWidth = 480.0F;
   private static final float panelHeight = 320.0F;
   private static final ProxyDotRadiusData panelRadius = ProxyDotRadiusData.method8069(10.0F);
   private static final ProxyDotRadiusData leftPanelRadius = ProxyDotRadiusData.method8078(10.0F, 10.0F);
   private static final ProxyDotRadiusData rightPanelRadius = ProxyDotRadiusData.method8079(10.0F, 10.0F);
   private static final long screenOpenAnimationDuration = 300L;
   private static final long screenCloseAnimationDuration = 220L;
   private static final float screenAnimationEpsilon = 0.001F;
   private static final float screenStartScale = 0.88F;
   private static final float screenStartOffsetY = 10.0F;
   private static final long popupAnimationDuration = 180L;
   private static final float shadowOffsetX = 0.0F;
   private static final float shadowOffsetY = 16.0F;
   private static final float shadowBlur = 32.0F;
   private static final float shadowSpread = -16.0F;
   private static final ZenithColor shadowColor = new ZenithColor(12, 12, 12, 20);
   private static final float blurRadius = 32.0F;
   private static final ZenithColor blurColor = new ZenithColor(255, 255, 255, 255);
   private static final ZenithColor leftColor = new ZenithColor(14, 14, 16, 122);
   private static final float headerOffset = 4.0F;
   private static final float headerWidth = 232.0F;
   private static final float headerHeight = 23.0F;
   private static ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(4.0F);
   private static final ZenithColor headerColor = new ZenithColor(14, 14, 16, 122);
   private static final float addButtonWidth = 23.0F;
   private static final ZenithColor addButtonColor = new ZenithColor(14, 14, 16, 61);
   private static final float searchSplitGap = 4.0F;
   private static final float searchHalfWidth = 114.0F;
   private static final float rowGap = 4.0F;
   private static final float searchOffsetY = 31.0F;
   private static final float listRowWidth = 227.0F;
   private static final float listStartY = 58.0F;
   private static final float listRowGap = 2.0F;
   private static final float listRowStride = 25.0F;
   private static final float listViewportHeight = 211.0F;
   private static final float bulkActionTabsOffsetY = 275.0F;
   private static final float bulkActionModeHeight = 14.0F;
   private static final float bulkActionModeButtonSize = 14.0F;
   private static final float bulkActionModeGap = 3.0F;
   private static final ProxyDotRadiusData bulkActionModeRadius = ProxyDotRadiusData.method8069(4.0F);
   private static final float bulkActionPayloadOffsetY = 292.0F;
   private static final float bulkActionRunWidth = 64.0F;
   private static final int botNameMinLength = 3;
   private static final int botNameMaxLength = 16;
   private static final int rctMinAnarchy = 1;
   private static final int rctMaxAnarchy = 66;
   private static final float listScrollStep = 25.0F;
   private static final float scrollbarGap = 4.0F;
   private static final float scrollbarWidth = 1.0F;
   private static final float scrollbarMinThumb = 16.0F;
   private static final ProxyDotRadiusData scrollbarRadius = ProxyDotRadiusData.method8069(0.5F);
   private static final ZenithColor scrollbarTrackColor = new ZenithColor(255, 255, 255, 5);
   private static final ZenithColor scrollbarThumbColor = new ZenithColor(255, 255, 255, 10);
   private static final float listActionWidth = 64.0F;
   private static final float serverIconSize = 7.0F;
   private static final float serverIconGap = 4.0F;
   private static final ProxyDotRadiusData serverIconRadius = ProxyDotRadiusData.method8069(2.0F);
   private static final ZenithColor listColor = new ZenithColor(14, 14, 16, 61);
   private static final float avatarSize = 15.0F;
   private static final ProxyDotRadiusData avatarRadius = ProxyDotRadiusData.method8069(1.9565217F);
   private static final float rightHeaderWidth = 232.0F;
   private static final float rightHeaderPanelWidth = 157.0F;
   private static final float rightHeaderPanelHeight = 23.0F;
   private static final float rightHeaderPanelX = 319.0F;
   private static final float screenGap = 12.0F;
   private static final float screenOffsetY = 39.0F;
   private static final float screenWidth = 232.0F;
   private static final float screenHeight = 130.5F;
   private static final float screenOffsetX = 244.0F;
   private static final float chatGap = 4.0F;
   private static final float chatHeight = 107.5F;
   private static final float chatPadding = 4.0F;
   private static final float chatLineGap = 1.0F;
   private static final float chatTextOffset = 8.0F;
   private static final float chatScrollStep = 14.0F;
   private static final long chatAppearDuration = 260L;
   private static final float inputGap = 4.0F;
   private static final float inputHeight = 23.0F;
   private static final float inputButtonWidth = 48.0F;
   private static final ZenithColor inputEmptyColor = new ZenithColor(255, 255, 255, 184);
   private static final Font nickFont = Fonts.MEDIUM.getFont(6.0F);
   private static final Font timeFont = Fonts.REGULAR.getFont(5.0F);
   private static final Font botIconFont = Fonts.NEW_ICONS.getFont(7.0F);
   private static final String botIcon = "P";
   private static final String addIcon = "D";
   private static final Font connectIconFont = Fonts.NEW_ICONS.getFont(6.0F);
   private static final String connectIcon = "Z";
   private static final String controlIcon = "P";
   private static final String offlineIcon = "]";
   private static final String sendIcon = "d";
   private static final String rctIcon = "7";
   private static final String disconnectIcon = "]";
   private static final ZenithColor disconnectColor = new ZenithColor(255, 99, 99, 255);
   private static final Font roleFont = Fonts.MEDIUM.getFont(5.0F);
   private static final ProxyDotRadiusData roleBadgeRadius = ProxyDotRadiusData.method8069(3.0F);
   private static final Font moduleMenuFont = Fonts.MEDIUM.getFont(5.0F);
   private static final float moduleMenuWidth = 118.0F;
   private static final float moduleMenuHeaderHeight = 18.0F;
   private static final float moduleMenuRowHeight = 20.0F;
   private static final float moduleMenuPadding = 4.0F;
   private static final float moduleMenuGap = 3.0F;
   private static final ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(6.0F);
   private static final ProxyDotRadiusData moduleMenuRowRadius = ProxyDotRadiusData.method8069(4.0F);
   private static final Font deleteIconFont = Fonts.NEW_ICONS.getFont(8.0F);
   private static final Font proxyDeleteIconFont = Fonts.NEW_ICONS.getFont(4.0F);
   private static final String deleteIcon = "2";
   private static final ZenithColor deleteColor = new ZenithColor(255, 64, 64, 255);
   private static final ZenithColor deleteDarkColor = new ZenithColor(18, 18, 20, 255);
   private static final float deleteDarken = 0.62F;
   private static final float connectIconGap = 4.0F;
   private static final float botIconGap = 3.0F;
   private static final String proxyIcon = "Z";
   private static final float proxyDotSize = 4.0F;
   private static final ProxyDotRadiusData proxyDotRadius = ProxyDotRadiusData.method8069(2.0F);
   private static final float proxyPanelSpace = 4.0F;
   private static final float proxyPanelSpaceHalf = 2.0F;
   private static final float proxyPanelSpaceDouble = 8.0F;
   private static final float proxyPanelWidth = 156.0F;
   private static final float proxyPanelHeight = 222.0F;
   private static final float proxyPanelGap = 8.0F;
   private static final float proxyPanelButtonSize = 18.0F;
   private static final float proxyPanelCloseHitSize = 14.0F;
   private static final float proxyPanelPadding = 4.0F;
   private static final float proxyPanelHeaderHeight = 27.0F;
   private static final float proxyPanelRowHeight = 23.0F;
   private static final float proxyPanelRowGap = 2.0F;
   private static final float proxyPanelInputGap = 4.0F;
   private static final float proxyPanelInputHeight = 18.0F;
   private static final float proxyPanelInputButtonSize = 18.0F;
   private static final float proxyPanelInputAfterGap = 4.0F;
   private static final float proxyPanelInputOffsetY = 31.0F;
   private static final float proxyPanelListOffsetY = 53.0F;
   private static final float proxyPanelListHeight = 161.0F;
   private static final float proxyPanelDeleteWidth = 18.0F;
   private static final float proxyPanelDeleteGap = 2.0F;
   private static final float proxyPanelScrollbarGap = 4.0F;
   private static final float proxyPanelStatusPadX = 4.0F;
   private static final float proxyPanelStatusInnerGap = 4.0F;
   private static final float proxyPanelStatusSeparatorWidth = 0.5F;
   private static final float proxyPanelStatusSeparatorHeight = 6.0F;
   private static final float proxyPanelScrollStep = 25.0F;
   private static final long proxyPanelAnimationDuration = 220L;
   private static final ProxyDotRadiusData proxyPanelRadius = ProxyDotRadiusData.method8069(10.0F);
   private static final ProxyDotRadiusData proxyPanelButtonRadius = ProxyDotRadiusData.method8069(5.0F);
   private static final ZenithColor proxyPingOkColor = new ZenithColor(88, 220, 166, 255);
   private static final ZenithColor proxyPingSlowColor = new ZenithColor(255, 206, 112, 255);
   private static final ZenithColor proxyPingBadColor = new ZenithColor(255, 99, 99, 255);
   private static final float chatLineStepCache = lineHeight(nickFont) + 1.0F;
   private static final float chatTimeTopOffsetCache = nickFont.height() - timeFont.height();
   private static final float chatTimeColumnWidthCache = timeFont.width("00:00:00");
   private static final float statusPadding = 8.0F;
   private static final float statusLineGap = 4.0F;
   private static final Font scoreboardFont = Fonts.NEW_MEDIUM.getFont(5.0F);
   private static final float scoreboardRowGap = 1.5F;
   private static final Font versionFont = Fonts.MEDIUM.getFont(5.0F);
   private static final float versionChipWidth = 34.0F;
   private static final float versionChipGap = 4.0F;
   private static final float versionMenuWidth = 72.0F;
   private static final float versionMenuRowHeight = 14.0F;
   private static final float versionMenuRowGap = 2.0F;
   private static final float versionMenuPadding = 4.0F;
   private static final int versionMenuVisibleRows = 9;
   private final Animation screenAnimation = new Animation(300L, 0.0F, Easing.INSTANCE4);
   private final Animation proxyPanelAnimation = new Animation(220L, 0.0F, Easing.INSTANCE13);
   private final Animation moduleMenuAnimation = new Animation(180L, 0.0F, Easing.INSTANCE15);
   private final Animation versionMenuAnimation = new Animation(180L, 0.0F, Easing.INSTANCE15);
   private final Map<String, BotScreen.RowAnim> rowAnims = new HashMap<>();
   private final Map<String, Animation> proxyHoverAnims = new HashMap<>();
   private final Map<ChatMessage, Animation> chatAnims = new IdentityHashMap<>();
   private final Map<ChatMessage, List<Component>> wrapCache = new IdentityHashMap<>();
   private BotClient chatAnimClient;
   private boolean closing = false;
   private static boolean savedProxyPanelOpen = false;
   private boolean proxyPanelOpen = savedProxyPanelOpen;
   private String selectedName;
   private final LinkedHashSet<String> selectedNames = new LinkedHashSet<>();
   private float chatScroll;
   private float listScroll;
   private float proxyPanelScroll;
   private final SearchBox chatInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Message...", 0.0F);
   private final SearchBox connectInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Connect to...", 0.0F);
   private final SearchBox addInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Add...", 0.0F);
   private final SearchBox searchInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Search...", 0.0F);
   private final SearchBox bulkActionInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Value...", 0.0F);
   private final SearchBox proxyManagerInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Proxy...", 0.0F);
   private BotScreen.BulkAction bulkAction = BotScreen.BulkAction.CONNECT;
   private String moduleMenuBotName;
   private float moduleMenuX;
   private float moduleMenuY;
   private float connectButtonWidth;
   private boolean versionMenuOpen;
   private float versionMenuScroll;
   private BotWorldView previewView;
   private BotClient previewClient;
   private BotClient scoreboardClient;
   private Component scoreboardTitle;
   private List<Component> scoreboardNames = List.of();
   private static final Map<BotScreen.FitKey, String> FIT_CACHE = new LinkedHashMap<BotScreen.FitKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<BotScreen.FitKey, String> eldest) {
         return this.size() > 128;
      }
   };
   private static final Map<String, String> serverLabelCache = new HashMap<>();

   @Override
   public void init() {
      super.init();
      this.closing = false;
      this.screenAnimation.setDuration(300L);
      this.screenAnimation.setEasing(Easing.INSTANCE4);
      this.screenAnimation.snapTo(0.0F);
      this.screenAnimation.animateTo(1.0F);
      this.moduleMenuAnimation.snapTo(this.moduleMenuBotName == null ? 0.0F : 1.0F);
      this.versionMenuAnimation.snapTo(this.versionMenuOpen ? 1.0F : 0.0F);
      this.connectButtonWidth = 23.0F;
      this.chatInput.setText2(tr("module.bot.message"));
      this.connectInput.setText2(tr("module.bot.connectTo"));
      this.connectInput.setType(SearchBox.Type.SERVER_ADDRESS);
      if (this.connectInput.getText().isBlank()) {
         ServerData currentServer = mc.getCurrentServer();
         if (currentServer != null && currentServer.ip != null && !currentServer.ip.isBlank()) {
            this.connectInput.setText(currentServer.ip.trim());
            this.connectInput.setCursor(this.connectInput.getText().length());
         }
      }

      this.addInput.setText2(tr("module.bot.add"));
      this.addInput.setType(SearchBox.Type.ENGLISH_NAME);
      this.addInput.setMaxLength(16);
      this.searchInput.setText2(tr("module.bot.search"));
      this.bulkActionInput.setMaxLength(256);
      this.proxyManagerInput.setText2(tr("module.bot.proxy"));
      this.proxyManagerInput.setMaxLength(256);
      this.proxyManagerInput.setType(SearchBox.Type.ANY);
      if (this.selectedName == null) {
         var allNames = HeadlessBots.allNames();
         if (!allNames.isEmpty()) {
            this.selectedName = (String)allNames.get(0);
         }
      }

      if (this.selectedName != null && this.selectedNames.isEmpty()) {
         this.selectedNames.add(this.selectedName);
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float f, float f2) {
   }

   public void renderTop(MenuDrawContext menuDrawContext, float mouseX, float mouseY) {
      float floatValue = (mc.getWindow().getGuiScaledWidth() - 480.0F) / 2.0F;
      float y = (mc.getWindow().getGuiScaledHeight() - 320.0F) / 2.0F;
      float f = this.screenAnimation.animateTo(this.closing ? 0.0F : 1.0F);
      if (this.closing && this.screenAnimation.isDone() && f <= 0.001F) {
         mc.gui.setScreen(null);
      } else {
         float previewAlpha = clamp01(f);
         float x = floatValue + 240.0F;
         float f2 = y + 160.0F;
         float f3 = this.closing ? previewAlpha : f;
         float x2 = 0.88F + 0.120000005F * f3;
         float f4 = 10.0F * (1.0F - previewAlpha);
         float f5 = (mouseX - x) / x2 + x;
         float f6 = (mouseY - f2 - f4) / x2 + f2;
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(x, f2 + f4);
         menuDrawContext.pose().scale(x2, x2);
         menuDrawContext.pose().translate(-x, -f2);
         RenderState.method1871();
         RenderState.method1873();
         this.renderProxyPanel(menuDrawContext, floatValue, y, previewAlpha, f5, f6);
         this.renderPanels(menuDrawContext, floatValue, y, previewAlpha, f5, f6);
         this.renderProxyPanelButton(menuDrawContext, floatValue, y, previewAlpha, f5, f6);
         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         menuDrawContext.pose().popMatrix();
      }
   }

   private void renderPanels(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha, float f, float f2) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         ServerIconCache.tick();
         RenderShaders.method5288(
            MatrixUtils.method664(menuDrawContext.pose()), floatValue, y, 480.0F, 320.0F, 0.0F, 16.0F, 32.0F, -16.0F, panelRadius, shadowColor.method7294(previewAlpha)
         );
         this.renderPanelBlur(menuDrawContext, floatValue, y, previewAlpha);
         menuDrawContext.drawRoundedRect(floatValue, y, 240.0F, 320.0F, leftPanelRadius, leftColor.method7294(previewAlpha));
         menuDrawContext.drawRoundedRect(floatValue + 240.0F, y, 240.0F, 320.0F, rightPanelRadius, currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawRoundedRect(floatValue + 4.0F, y + 4.0F, 232.0F, 23.0F, headerRadius, headerColor.method7294(previewAlpha));
         float y2 = y + 31.0F;
         float centeredTextY2 = centeredTextY(nickFont, y2, 23.0F);
         float floatValue2 = floatValue + 4.0F;
         menuDrawContext.drawRoundedRect(floatValue2, y2, 114.0F, 23.0F, headerRadius, headerColor.method7294(previewAlpha));
         float floatValue3 = floatValue2 + 114.0F - 23.0F;
         menuDrawContext.drawRoundedRect(floatValue3, y2, 23.0F, 23.0F, headerRadius, addButtonColor.method7294(previewAlpha));
         float width = botIconFont.width("D");
         menuDrawContext.drawText(botIconFont, "D", floatValue3 + (23.0F - width) / 2.0F, centeredTextY(botIconFont, y2, 23.0F), inputEmptyColor.method7294(previewAlpha));
         this.addInput.setWidth(75.0F);
         this.addInput.method987(menuDrawContext, floatValue2 + 8.0F, centeredTextY2, ZenithColor.INSTANCE.method7294(previewAlpha), inputEmptyColor.method7294(previewAlpha));
         float floatValue4 = floatValue2 + 114.0F + 4.0F;
         menuDrawContext.drawRoundedRect(floatValue4, y2, 114.0F, 23.0F, headerRadius, headerColor.method7294(previewAlpha));
         this.searchInput.setWidth(98.0F);
         this.searchInput.method987(menuDrawContext, floatValue4 + 8.0F, centeredTextY2, ZenithColor.INSTANCE.method7294(previewAlpha), inputEmptyColor.method7294(previewAlpha));
         List<String> allNames = HeadlessBots.allNames();
         this.refreshSelection(allNames);
         if (!this.rowAnims.isEmpty()) {
            var iterator = this.rowAnims.keySet().iterator();

            while (iterator.hasNext()) {
               String anObject = (String)iterator.next();
               boolean flag = false;

               for (String allName : allNames) {
                  if (allName.equals(anObject)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  iterator.remove();
               }
            }
         }

         var filterBots = this.filterBots(allNames);
         float y3 = y + 58.0F;
         float guiScaledHeight = y3 + 211.0F;
         this.listScroll = Math.max(0.0F, Math.min(this.listScroll, listMaxScroll(filterBots.size())));
         menuDrawContext.enableScissor(floatValue + 4.0F, y3, floatValue + 4.0F + 227.0F, guiScaledHeight);

         for (int i = 0; i < filterBots.size(); i++) {
            this.renderListRow(menuDrawContext, (String)filterBots.get(i), i, floatValue, y3, guiScaledHeight, previewAlpha, f, f2, currentStyle);
         }

         menuDrawContext.disableScissor();
         float floatValue5 = floatValue + 4.0F + 227.0F + 4.0F;
         menuDrawContext.drawRoundedRect(floatValue5, y3, 1.0F, 211.0F, scrollbarRadius, scrollbarTrackColor.method7294(previewAlpha));
         float listContentHeight2 = listContentHeight(filterBots.size());
         float height = listContentHeight2 <= 211.0F ? 211.0F : Math.max(16.0F, 44521.0F / listContentHeight2);
         float listMaxScroll2 = listMaxScroll(filterBots.size());
         float y4 = y3 + (listMaxScroll2 > 0.0F ? this.listScroll / listMaxScroll2 : 0.0F) * (211.0F - height);
         menuDrawContext.drawRoundedRect(floatValue5, y4, 1.0F, height, scrollbarRadius, scrollbarThumbColor.method7294(previewAlpha));
         this.renderBulkActionTabs(menuDrawContext, floatValue, y + 275.0F, previewAlpha, currentStyle);
         this.renderBulkActionPayload(menuDrawContext, floatValue, y + 292.0F, previewAlpha);
         menuDrawContext.drawRoundedRect(
            floatValue + 240.0F + 4.0F, y + 4.0F, 232.0F, 23.0F, headerRadius, currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedRect(floatValue + 240.0F + 4.0F, y + 4.0F, 23.0F, 23.0F, headerRadius, currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha));
         boolean flag = this.selectedNames.size() > 1;
         if (flag) {
            float width2 = botIconFont.width("P");
            menuDrawContext.drawText(
               botIconFont,
               "P",
               floatValue + 240.0F + 4.0F + (23.0F - width2) / 2.0F,
               centeredTextY(botIconFont, y + 4.0F, 23.0F),
               currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );
            menuDrawContext.drawText(
               nickFont,
               tr("module.bot.selected") + ": " + this.selectedNames.size(),
               floatValue + 240.0F + 4.0F + 23.0F + 8.0F,
               centeredTextY(nickFont, y + 4.0F, 23.0F),
               currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
            );
         } else if (this.selectedName != null) {
            Identifier prewarm = BotAvatarCache.prewarm(this.selectedName);
            if (prewarm != null) {
               float x = floatValue + 240.0F + 4.0F + 4.0F;
               float y5 = y + 4.0F + 4.0F;
               RenderShaders.method5294(
                  MatrixUtils.method664(menuDrawContext.pose()), prewarm, x, y5, 15.0F, 15.0F, avatarRadius, ZenithColor.INSTANCE.method7294(previewAlpha)
               );
            }

            menuDrawContext.drawText(
               nickFont,
               this.selectedName,
               floatValue + 240.0F + 4.0F + 23.0F + 8.0F,
               centeredTextY(nickFont, y + 4.0F, 23.0F),
               currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
            );
         }

         float width2 = nickFont.width("Bot");
         float width3 = botIconFont.width("P");
         float floatValue6 = floatValue + 4.0F + (232.0F - width3 - 3.0F - width2) / 2.0F;
         menuDrawContext.drawText(botIconFont, "P", floatValue6, centeredTextY(botIconFont, y + 4.0F, 23.0F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawText(nickFont, "Bot", floatValue6 + width3 + 3.0F, centeredTextY(nickFont, y + 4.0F, 23.0F), ZenithColor.INSTANCE.method7294(previewAlpha));
         BotClient selectedClient = this.selectedClient();
         boolean flag2 = selectedClient != null && selectedClient.isJoined();
         float f3 = floatValue + 319.0F;
         float y5 = y + 4.0F;
         float floatValue7 = f3 + 157.0F - this.connectButtonWidth;
         float floatValue8 = floatValue7 - 4.0F - 34.0F;
         this.connectInput.setWidth(157.0F - this.connectButtonWidth - 34.0F - 4.0F - 15.0F - 8.0F);
         this.connectInput
            .method987(
               menuDrawContext,
               f3 + 15.0F,
               centeredTextY(nickFont, y5, 23.0F),
               currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
               currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
            );
         menuDrawContext.drawRoundedRect(floatValue7, y5, this.connectButtonWidth, 23.0F, headerRadius, currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha));
         String text = flag2 ? "P" : "Z";
         float width4 = connectIconFont.width(text);
         menuDrawContext.drawText(
            connectIconFont,
            text,
            floatValue7 + (this.connectButtonWidth - width4) / 2.0F,
            centeredTextY(connectIconFont, y5, 23.0F) + 0.5F,
            (flag2 ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedRect(
            floatValue8,
            y5,
            34.0F,
            23.0F,
            headerRadius,
            (this.versionMenuOpen ? currentStyle.getPrimaryColor().getZenithColor().method7293(42) : currentStyle.getPanelLeftBackground().getZenithColor()).method7294(previewAlpha)
         );
         String str = fitText(versionFont, BotProtocolVersions.label(this.selectedProtocolVersion()), 28.0F);
         ZenithColor zenithColor = this.selectedProtocolVersion() > 0 ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getTextSecondary().getZenithColor();
         menuDrawContext.drawText(versionFont, str, floatValue8 + (34.0F - versionFont.width(str)) / 2.0F, centeredTextY(versionFont, y5, 23.0F), zenithColor.method7294(previewAlpha));
         float floatValue9 = floatValue + 244.0F;
         float y6 = y + 39.0F + 130.5F + 4.0F;
         float y7 = y6 + 107.5F + 4.0F;
         if (flag2) {
            menuDrawContext.drawRoundedRect(
               floatValue + 244.0F, y + 39.0F, 232.0F, 130.5F, headerRadius, currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha)
            );
            this.renderPreview(menuDrawContext, selectedClient, floatValue + 244.0F, y + 39.0F, previewAlpha);
            this.renderStatusPanel(menuDrawContext, selectedClient, floatValue + 244.0F, y + 39.0F, previewAlpha, currentStyle);
            menuDrawContext.drawRoundedRect(floatValue9, y6, 232.0F, 107.5F, headerRadius, currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha));
         } else {
            this.closePreview();
            float height2 = 242.0F;
            menuDrawContext.drawRoundedRect(floatValue9, y + 39.0F, 232.0F, height2, headerRadius, currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha));
            String tr2 = tr("module.bot.noConnection");
            float width5 = nickFont.width(tr2);
            menuDrawContext.drawText(
               nickFont,
               tr2,
               floatValue9 + (232.0F - width5) / 2.0F,
               centeredTextY(nickFont, y + 39.0F, height2),
               currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
            );
         }

         if (flag2) {
            float floatValue10 = floatValue9 + 232.0F - 48.0F;
            this.chatInput.setWidth(168.0F);
            this.chatInput
               .method987(
                  menuDrawContext,
                  floatValue9 + 8.0F,
                  centeredTextY(nickFont, y7, 23.0F),
                  currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
                  currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
               );
            menuDrawContext.drawRoundedRect(floatValue10, y7, 48.0F, 23.0F, headerRadius, currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha));
            String tr2 = tr("module.bot.send");
            float width5 = nickFont.width(tr2);
            float width6 = connectIconFont.width("d");
            float f4 = 2.0F;
            float floatValue11 = floatValue10 + (48.0F - width6 - f4 - width5) / 2.0F;
            menuDrawContext.drawText(connectIconFont, "d", floatValue11, centeredTextY(connectIconFont, y7, 23.0F), currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
            menuDrawContext.drawText(
               nickFont, tr2, floatValue11 + width6 + f4, centeredTextY(nickFont, y7, 23.0F), currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
            );
         } else {
            this.chatInput.setSelected(false);
         }

         if (flag2) {
            this.renderChatMessages(menuDrawContext, selectedClient, floatValue9, y6, previewAlpha, currentStyle);
         }

         this.renderModuleMenu(menuDrawContext, previewAlpha, f, f2, currentStyle);
         this.renderVersionMenu(menuDrawContext, floatValue, y, previewAlpha, f, f2, currentStyle);
      }
   }

   private void renderStatusPanel(MenuDrawContext menuDrawContext, BotClient selectedClient, float f, float f2, float previewAlpha, ZenithStyle currentStyle) {
      float floatValue = f + 8.0F;
      float centeredTextY = f2 + 8.0F;
      float f3 = lineHeight(nickFont) + 4.0F;
      menuDrawContext.drawText(nickFont, selectedClient.name(), floatValue, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      String name = selectedClient.getPhase().name();
      float width = roleFont.width(name);
      float width2 = width + 8.0F;
      float floatValue2 = f + 232.0F - 8.0F - width2;
      menuDrawContext.drawRoundedRect(floatValue2, centeredTextY - 1.5F, width2, 10.0F, roleBadgeRadius, currentStyle.getPrimaryColor().getZenithColor().method7293(34).method7294(previewAlpha));
      menuDrawContext.drawText(roleFont, name, floatValue2 + 4.0F, centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      centeredTextY += f3 + 4.0F;
      this.renderBotScoreboard(menuDrawContext, selectedClient, f, f2, previewAlpha, currentStyle);
   }

   private void renderBotScoreboard(MenuDrawContext menuDrawContext, BotClient scoreboardClient, float f, float f2, float previewAlpha, ZenithStyle currentStyle) {
      BotWorld world = scoreboardClient.getWorld();
      if (world != null) {
         Component displayName;
         List scoreboardNames;
         try {
            synchronized (scoreboardClient.getRenderStateLock()) {
               label113: {
                  Scoreboard scoreboard = world.getScoreboard();
                  synchronized (scoreboard) {
                     Objective displayObjective = null;
                     PlayerTeam playersTeam = scoreboard.getPlayersTeam(scoreboardClient.name());
                     if (playersTeam != null) {
                        DisplaySlot slot = playersTeam.getColor().map(net.minecraft.world.scores.TeamColor::displaySlot).orElse(null);
                        if (slot != null) {
                           displayObjective = scoreboard.getDisplayObjective(slot);
                        }
                     }

                     if (displayObjective == null) {
                        displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
                     }

                     if (displayObjective == null) {
                        this.scoreboardTitle = null;
                     } else {
                        ArrayList<PlayerScoreEntry> playerScoreEntries = new ArrayList<>(scoreboard.listPlayerScores(displayObjective));
                        playerScoreEntries.removeIf(PlayerScoreEntry::isHidden);
                        playerScoreEntries.sort(net.minecraft.client.gui.Hud.SCORE_DISPLAY_ORDER);
                        scoreboardNames = new ArrayList(playerScoreEntries.size());

                        for (PlayerScoreEntry playerScoreEntry : playerScoreEntries) {
                           scoreboardNames.add(PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(playerScoreEntry.owner()), playerScoreEntry.ownerName()));
                        }

                        displayName = displayObjective.getDisplayName();
                        break label113;
                     }
                  }

                  return;
               }
            }

            this.scoreboardClient = scoreboardClient;
            this.scoreboardTitle = displayName;
            this.scoreboardNames = (List<Component>)scoreboardNames;
         } catch (RuntimeException e) {
            if (this.scoreboardClient != scoreboardClient || this.scoreboardTitle == null) {
               return;
            }

            displayName = this.scoreboardTitle;
            scoreboardNames = this.scoreboardNames;
         }

         float f3 = lineHeight(scoreboardFont) + 1.5F;
         float y = f2 + 8.0F + 12.0F;
         float f4 = f2 + 130.5F - 8.0F;
         int n = Math.max(0, (int)((f4 - y - f3) / f3));
         if (n > 0) {
            List subList = scoreboardNames.size() > n ? scoreboardNames.subList(0, n) : scoreboardNames;
            if (!subList.isEmpty()) {
               float f5 = f + 232.0F - 8.0F;
               menuDrawContext.drawText(
                  scoreboardFont, displayName, f5 - scoreboardFont.width(displayName), y, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha).method7287()
               );
               float y2 = y + (f3 + 1.5F);

               for (Object sub : (Iterable<?>)(Object)(subList)) {
                  Component text = (Component)sub;
                  menuDrawContext.drawText(
                     scoreboardFont, text, f5 - scoreboardFont.width(text), y2, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha).method7287()
                  );
                  y2 += f3;
               }
            }
         }
      }
   }

   private List<String[]> statusLines(BotClient botClient) {
      ArrayList<String[]> stringses = new ArrayList<>();
      stringses.add(new String[]{"Server", botClient.getAddress()});
      BotPlayer player = botClient.getPlayer();
      if (player != null) {
         stringses.add(new String[]{"Health", String.format(Locale.ROOT, "%.1f", player.getHealth())});
         stringses.add(new String[]{"Pos", (int)player.getX() + " " + (int)player.getY() + " " + (int)player.getZ()});
      }

      BotWorld world = botClient.getWorld();
      if (world != null) {
         stringses.add(new String[]{"World", world.dimension().identifier().getPath()});
      }

      stringses.add(new String[]{"RCT", botClient.getRct().isActive() ? "active" : "idle"});
      StringBuilder builder = new StringBuilder();

      for (BotModule botModule : botClient.getModules().getModules()) {
         if (botModule.isEnabled()) {
            if (builder.length() > 0) {
               builder.append(", ");
            }

            builder.append(botModule.getName());
         }
      }

      stringses.add(new String[]{"Modules", builder.length() == 0 ? "-" : builder.toString()});
      return stringses;
   }

   private void renderPreview(MenuDrawContext menuDrawContext, BotClient previewClient, float floatValue, float y, float previewAlpha) {
      if (this.previewClient != previewClient) {
         this.closePreview();
         this.previewClient = previewClient;
         this.previewView = new BotWorldView(previewClient);
      }

      double guiScale = mc.getWindow().getGuiScale();
      int width = (int)(232.0 * guiScale);
      int height = (int)(130.5 * guiScale);
      ScissorFlusher.flush();
      if (this.previewView.renderToFbo(width, height)) {
         RenderShaders.method5298(
            MatrixUtils.method664(menuDrawContext.pose()),
            this.previewView.getColorAttachment(),
            floatValue,
            y,
            232.0F,
            130.5F,
            headerRadius,
            ZenithColor.INSTANCE.method7294(previewAlpha),
            0.0F,
            1.0F,
            1.0F,
            0.0F
         );
         menuDrawContext.drawRoundedRect(floatValue, y, 232.0F, 130.5F, headerRadius, new ZenithColor(10, 10, 14, 150).method7294(previewAlpha));
      }
   }

   private void closePreview() {
      if (this.previewView != null) {
         this.previewView.close();
         this.previewView = null;
      }

      this.previewClient = null;
   }

   @Override
   public void removed() {
      this.closePreview();
      ServerIconCache.clear();
      BotAvatarCache.clear();
      super.removed();
   }

   private void renderPanelBlur(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha) {
      RenderShaders.method5287(
         MatrixUtils.method664(menuDrawContext.pose()), floatValue, y, 480.0F, 320.0F, 32.0F, panelRadius, blurColor.method7294(previewAlpha), true, false
      );
   }

   private void renderProxyPanel(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha, float f, float f2) {
      float f3 = this.proxyPanelAnimation.animateTo(this.proxyPanelOpen ? 1.0F : 0.0F);
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         if (f3 > 0.001F) {
            float moduleMenuX = proxyPanelRenderX(floatValue, f3);
            float proxyPanelY2 = proxyPanelY(y);
            float previewAlpha2 = previewAlpha * f3;
            RenderShaders.method5299(
               MatrixUtils.method664(menuDrawContext.pose()), moduleMenuX, proxyPanelY2 + 8.0F, 156.0F, 222.0F, 16.0F, proxyPanelRadius, shadowColor.method7294(previewAlpha2)
            );
            RenderShaders.method5287(
               MatrixUtils.method664(menuDrawContext.pose()), moduleMenuX, proxyPanelY2, 156.0F, 222.0F, 32.0F, proxyPanelRadius, blurColor.method7294(previewAlpha2), true, false
            );
            menuDrawContext.drawRoundedRect(moduleMenuX, proxyPanelY2, 156.0F, 222.0F, proxyPanelRadius, currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha2));
            menuDrawContext.drawRoundedRect(
               moduleMenuX, proxyPanelY2, 156.0F, 27.0F, ProxyDotRadiusData.method8075(10.0F), currentStyle.getHeaderDisableBackground().getZenithColor().method7294(previewAlpha2)
            );
            float width = connectIconFont.width("Z");
            float floatValue2 = moduleMenuX + 4.0F + 4.0F;
            menuDrawContext.drawText(connectIconFont, "Z", floatValue2, centeredTextY(connectIconFont, proxyPanelY2, 27.0F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha2));
            menuDrawContext.drawText(
               nickFont,
               tr("module.bot.proxyPanel"),
               floatValue2 + width + 4.0F,
               centeredTextY(nickFont, proxyPanelY2, 27.0F),
               currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha2)
            );
            float proxyPanelCloseHitX2 = proxyPanelCloseHitX(moduleMenuX);
            float proxyPanelCloseHitY2 = proxyPanelCloseHitY(proxyPanelY2);
            boolean flag = f >= proxyPanelCloseHitX2 && f <= proxyPanelCloseHitX2 + 14.0F && f2 >= proxyPanelCloseHitY2 && f2 <= proxyPanelCloseHitY2 + 14.0F;
            float width2 = proxyDeleteIconFont.width("2");
            ZenithColor zenithColor = currentStyle.getTextTertiary().getZenithColor().method7295(currentStyle.getTextSecondary().getZenithColor(), flag ? 0.9F : 0.0F);
            menuDrawContext.drawText(
               proxyDeleteIconFont, "2", proxyPanelCloseHitX2 + (14.0F - width2) / 2.0F, proxyPanelCloseHitY2 + (14.0F - proxyDeleteIconFont.height()) / 2.0F, zenithColor.method7294(previewAlpha2)
            );
            this.renderProxyPanelInput(menuDrawContext, currentStyle, moduleMenuX, proxyPanelY2 + 31.0F, previewAlpha2);
            this.renderProxyPanelList(menuDrawContext, currentStyle, moduleMenuX, proxyPanelY2 + 53.0F, previewAlpha2, f, f2);
         }
      }
   }

   private void renderProxyPanelButton(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha, float f, float f2) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         float f3 = this.proxyPanelAnimation.getValue();
         float proxyPanelButtonX2 = proxyPanelButtonX(floatValue);
         float proxyPanelButtonY2 = proxyPanelButtonY(y);
         boolean flag = f >= proxyPanelButtonX2 && f <= proxyPanelButtonX2 + 18.0F && f2 >= proxyPanelButtonY2 && f2 <= proxyPanelButtonY2 + 18.0F;
         ZenithColor zenithColor = (this.proxyPanelOpen ? currentStyle.getHeaderDisableBackground().getZenithColor() : currentStyle.getDisableActiveBg().getZenithColor())
            .method7295(currentStyle.getPrimaryColor().getZenithColor(), !flag && !this.proxyPanelOpen ? 0.0F : 0.18F);
         menuDrawContext.drawRoundedRect(proxyPanelButtonX2, proxyPanelButtonY2, 18.0F, 18.0F, proxyPanelButtonRadius, zenithColor.method7294(previewAlpha));
         float width = connectIconFont.width("Z");
         menuDrawContext.drawText(
            connectIconFont,
            "Z",
            proxyPanelButtonX2 + (18.0F - width) / 2.0F,
            centeredTextY(connectIconFont, proxyPanelButtonY2, 18.0F),
            currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getPrimaryColor().getZenithColor(), f3 > 0.01F ? 1.0F : 0.0F).method7294(previewAlpha)
         );
      }
   }

   private void renderProxyPanelInput(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float moduleMenuX, float y, float previewAlpha) {
      float floatValue = moduleMenuX + 4.0F;
      float width = 126.0F;
      menuDrawContext.drawRoundedRect(floatValue, y, width, 18.0F, headerRadius, currentStyle.getHeaderHudBackground().getZenithColor().method7294(previewAlpha));
      this.proxyManagerInput.setWidth(width - 16.0F);
      this.proxyManagerInput
         .method987(
            menuDrawContext,
            floatValue + 8.0F,
            centeredTextY(nickFont, y, 18.0F),
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
            currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
         );
      float f = floatValue + width + 4.0F;
      menuDrawContext.drawRoundedRect(f, y, 18.0F, 18.0F, headerRadius, currentStyle.getPrimaryColor().getZenithColor().method7294(0.25F).method7294(previewAlpha));
      float width2 = botIconFont.width("D");
      menuDrawContext.drawText(
         botIconFont,
         "D",
         f + (18.0F - width2) / 2.0F + 1.0F,
         centeredTextY(botIconFont, y, 18.0F),
         currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
      );
   }

   private void renderProxyPanelList(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float moduleMenuX, float y, float previewAlpha, float f, float f2) {
      List<String> proxyPool = HeadlessBots.getProxyPool();
      this.pruneProxyHoverAnimations(proxyPool);
      int n = proxyPool.size() + 1;
      this.proxyPanelScroll = Math.max(0.0F, Math.min(this.proxyPanelScroll, proxyPanelMaxScroll(n)));
      float floatValue = moduleMenuX + 4.0F;
      float width = proxyPanelRowWidth();
      float guiScaledHeight = y + 161.0F;
      menuDrawContext.enableScissor(floatValue, y, floatValue + width, guiScaledHeight);
      float y2 = y - this.proxyPanelScroll;
      this.renderProxyPanelRow(
         menuDrawContext,
         proxyNoneKey(),
         tr("module.bot.proxyNone"),
         this.noProxyCount(),
         this.selectionHasNoProxy(),
         false,
         floatValue,
         y2,
         width,
         y,
         guiScaledHeight,
         previewAlpha,
         f,
         f2,
         currentStyle
      );
      y2 += 25.0F;

      for (String str : proxyPool) {
         this.renderProxyPanelRow(
            menuDrawContext,
            str,
            str,
            HeadlessBots.getProxyUseCount(str),
            this.selectionUsesProxy(str),
            true,
            floatValue,
            y2,
            width,
            y,
            guiScaledHeight,
            previewAlpha,
            f,
            f2,
            currentStyle
         );
         y2 += 25.0F;
      }

      menuDrawContext.disableScissor();
      float proxyPanelScrollbarX2 = proxyPanelScrollbarX(moduleMenuX);
      menuDrawContext.drawRoundedRect(proxyPanelScrollbarX2, y, 1.0F, 161.0F, scrollbarRadius, currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * 0.65F));
      if (proxyPanelMaxScroll(n) > 0.0F) {
         float proxyPanelContentHeight2 = proxyPanelContentHeight(n);
         float height = Math.max(16.0F, 25921.0F / proxyPanelContentHeight2);
         float y3 = y + this.proxyPanelScroll / proxyPanelMaxScroll(n) * (161.0F - height);
         menuDrawContext.drawRoundedRect(proxyPanelScrollbarX2, y3, 1.0F, height, scrollbarRadius, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      }
   }

   private void renderProxyPanelRow(
      MenuDrawContext menuDrawContext,
      String proxyNoneKey,
      String tr,
      int noProxyCount,
      boolean selectionHasNoProxy,
      boolean flag,
      float floatValue,
      float y,
      float width,
      float f,
      float guiScaledHeight,
      float previewAlpha,
      float f2,
      float f3,
      ZenithStyle currentStyle
   ) {
      boolean flag2 = f3 >= f && f3 <= guiScaledHeight && f2 >= floatValue && f2 <= floatValue + width && f3 >= y && f3 <= y + 23.0F;
      boolean flag3 = flag && flag2 && f2 >= floatValue + width - 18.0F;
      Animation animation = this.proxyHoverAnims.computeIfAbsent(proxyNoneKey, proxyHoverAnim -> new Animation(150L, 0.0F, Easing.INSTANCE15));
      float f4 = animation.animateTo(flag2 ? 1.0F : 0.0F);
      ZenithColor zenithColor = currentStyle.getHeaderDisableBackground()
         .getZenithColor()
         .method7295(currentStyle.getSurfaceEnableBackground().getZenithColor(), selectionHasNoProxy ? 0.9F : f4 * 0.45F)
         .method7294(previewAlpha);
      menuDrawContext.drawRoundedRect(floatValue, y, width, 23.0F, headerRadius, zenithColor);
      if (selectionHasNoProxy) {
         float floatValue2 = floatValue + 4.0F;
         float y2 = y + 4.0F;
         float height = 15.0F;
         menuDrawContext.drawRoundedRect(floatValue2, y2, 3.0F, height, scrollbarRadius, currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawRoundedRect(floatValue2, y2, 2.0F, height, scrollbarRadius, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
      }

      String text = String.valueOf(noProxyCount);
      float width2 = roleFont.width(text);
      HeadlessBots.ProxyPingSnapshot proxyPing = flag ? HeadlessBots.getProxyPing(proxyNoneKey) : null;
      String label = proxyPing == null ? "" : proxyPing.label();
      float width3 = proxyPing == null ? 0.0F : roleFont.width(label);
      float width4 = 8.0F + width2;
      if (flag) {
         width4 += width3 + 8.0F + 0.5F;
      }

      float f5 = flag ? 20.0F : 8.0F;
      float floatValue2 = floatValue + width - width4 - f5;
      float height = 10.0F;
      float y2 = y + (23.0F - height) / 2.0F;
      float floatValue3 = floatValue + 8.0F + 2.0F + f4;
      String str = fitText(nickFont, tr, Math.max(12.0F, floatValue2 - floatValue3 - 4.0F));
      menuDrawContext.drawText(nickFont, str, floatValue3, centeredTextY(nickFont, y, 23.0F), currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawRoundedRect(floatValue2, y2, width4, height, roleBadgeRadius, currentStyle.getPrimaryColor().getZenithColor().method7293(34).method7294(previewAlpha));
      if (flag) {
         menuDrawContext.drawText(roleFont, label, floatValue2 + 4.0F, centeredTextY(roleFont, y, 23.0F), proxyPingColor(proxyPing, currentStyle).method7294(previewAlpha));
         float floatValue4 = floatValue2 + 4.0F + width3 + 4.0F;
         float y3 = y + 8.5F;
         menuDrawContext.drawRect(floatValue4, y3, 0.5F, 6.0F, currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha));
      }

      menuDrawContext.drawText(
         roleFont, text, floatValue2 + width4 - 4.0F - width2, centeredTextY(roleFont, y2, height), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
      );
      if (flag) {
         float width5 = proxyDeleteIconFont.width("2");
         float floatValue4 = floatValue + width - width5 - 8.0F;
         float centeredTextY2 = y + (23.0F - proxyDeleteIconFont.height()) / 2.0F;
         ZenithColor zenithColor2 = currentStyle.getTextTertiary().getZenithColor().method7295(currentStyle.getTextSecondary().getZenithColor(), flag3 ? 0.9F : f4 * 0.35F);
         menuDrawContext.drawText(proxyDeleteIconFont, "2", floatValue4, centeredTextY2, zenithColor2.method7294(previewAlpha));
      }
   }

   private static float proxyPanelRenderX(float floatValue, float f) {
      return floatValue - 8.0F - 156.0F * f;
   }

   private static float proxyPanelListWidth() {
      return 148.0F;
   }

   private static float proxyPanelRowWidth() {
      return proxyPanelListWidth() - 1.0F - 4.0F;
   }

   private static float proxyPanelScrollbarX(float moduleMenuX) {
      return moduleMenuX + 156.0F - 4.0F - 1.0F;
   }

   private static float proxyPanelY(float y) {
      return y;
   }

   private static float proxyPanelButtonX(float floatValue) {
      return floatValue + 4.0F + 2.5F;
   }

   private static float proxyPanelButtonY(float y) {
      return y + 4.0F + 2.5F;
   }

   private static float proxyPanelCloseHitX(float moduleMenuX) {
      return moduleMenuX + 156.0F - 4.0F - 14.0F;
   }

   private static float proxyPanelCloseHitY(float f) {
      return f + 6.5F;
   }

   private static float proxyPanelContentHeight(int n) {
      return n <= 0 ? 0.0F : n * 25.0F - 2.0F;
   }

   private static float proxyPanelMaxScroll(int n) {
      return Math.max(0.0F, proxyPanelContentHeight(n) - 161.0F);
   }

   private static ZenithColor proxyPingColor(HeadlessBots.ProxyPingSnapshot proxyPing, ZenithStyle currentStyle) {
      if (proxyPing == null) {
         return currentStyle.getTextTertiary().getZenithColor();
      } else {
         return switch (proxyPing.status()) {
            case OK -> proxyPing.latencyMs() <= 750 ? proxyPingOkColor : proxyPingSlowColor;
            case FAILED -> proxyPingBadColor;
            case CHECKING, UNKNOWN -> currentStyle.getTextTertiary().getZenithColor();
         };
      }
   }

   private static String proxyNoneKey() {
      return "__none__";
   }

   private int noProxyCount() {
      int n = 0;

      for (String allName : HeadlessBots.allNames()) {
         if (!HeadlessBots.hasProxy(allName)) {
            n++;
         }
      }

      return n;
   }

   private boolean selectionHasNoProxy() {
      List<String> selectionNames = this.selectionNames();
      if (selectionNames.isEmpty()) {
         return false;
      } else {
         for (String selectionName : selectionNames) {
            if (HeadlessBots.hasProxy(selectionName)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean selectionUsesProxy(String anotherString) {
      List<String> selectionNames = this.selectionNames();
      if (!selectionNames.isEmpty() && anotherString != null && !anotherString.isBlank()) {
         for (String selectionName : selectionNames) {
            String proxy = HeadlessBots.getProxy(selectionName);
            if (proxy == null || !proxy.equalsIgnoreCase(anotherString)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void pruneProxyHoverAnimations(List<String> proxyPool) {
      var strings = new HashSet<>(proxyPool);
      strings.add(proxyNoneKey());
      this.proxyHoverAnims.keySet().removeIf(var1x -> !strings.contains(var1x));
   }

   private void renderBulkActionTabs(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha, ZenithStyle currentStyle) {
      float f = floatValue + 4.0F;
      menuDrawContext.drawRoundedRect(f, y - 3.0F, 232.0F, 1.0F, scrollbarRadius, scrollbarThumbColor.method7294(previewAlpha));
      float width = connectIconFont.width(this.bulkAction.icon);
      float f2 = 2.0F;
      menuDrawContext.drawText(
         connectIconFont,
         this.bulkAction.icon,
         f + 3.0F,
         centeredTextY(connectIconFont, y, 14.0F),
         currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawText(
         nickFont, tr(this.bulkAction.tabKey), f + 3.0F + width + f2, centeredTextY(nickFont, y, 14.0F), ZenithColor.INSTANCE.method7294(previewAlpha)
      );
      float bulkActionModeStartX2 = bulkActionModeStartX(floatValue);

      for (BotScreen.BulkAction bulkAction : BotScreen.BulkAction.values()) {
         boolean flag = bulkAction == this.bulkAction;
         menuDrawContext.drawRoundedRect(bulkActionModeStartX2, y, 14.0F, 14.0F, bulkActionModeRadius, (flag ? headerColor : addButtonColor).method7294(previewAlpha));
         float width2 = connectIconFont.width(bulkAction.icon);
         menuDrawContext.drawText(
            connectIconFont,
            bulkAction.icon,
            bulkActionModeStartX2 + (14.0F - width2) / 2.0F,
            centeredTextY(connectIconFont, y, 14.0F),
            (flag ? currentStyle.getPrimaryColor().getZenithColor() : inputEmptyColor).method7294(previewAlpha)
         );
         bulkActionModeStartX2 += 17.0F;
      }
   }

   private static float bulkActionModeStartX(float floatValue) {
      float f = BotScreen.BulkAction.values().length * 14.0F + (BotScreen.BulkAction.values().length - 1) * 3.0F;
      return floatValue + 4.0F + 232.0F - f;
   }

   private void focusTextBox(SearchBox proxyManagerInput, double x, double y) {
      proxyManagerInput.onMouseClicked(x, y, MouseButton.LEFT);
      if (!proxyManagerInput.isSelected()) {
         proxyManagerInput.setSelected(true);
         proxyManagerInput.setFlag(false);
      }
   }

   private void renderBulkActionPayload(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha) {
      float f = floatValue + 4.0F;
      this.bulkActionInput.setText2(tr(this.bulkAction.placeholderKey));
      this.syncBulkActionInputRules();
      float width = 164.0F;
      menuDrawContext.drawRoundedRect(f, y, width, 23.0F, headerRadius, headerColor.method7294(previewAlpha));
      this.bulkActionInput.setWidth(width - 16.0F);
      this.bulkActionInput
         .method987(menuDrawContext, f + 8.0F, centeredTextY(nickFont, y, 23.0F), ZenithColor.INSTANCE.method7294(previewAlpha), inputEmptyColor.method7294(previewAlpha));
      float floatValue2 = f + width + 4.0F;
      menuDrawContext.drawRoundedRect(floatValue2, y, 64.0F, 23.0F, headerRadius, addButtonColor.method7294(previewAlpha));
      this.drawCenteredIconText(menuDrawContext, this.bulkAction.icon, tr(this.bulkAction.buttonKey), floatValue2, y, 64.0F, 23.0F, inputEmptyColor.method7294(previewAlpha));
   }

   private void syncBulkActionInputRules() {
      switch (this.bulkAction) {
         case CONNECT:
            this.bulkActionInput.setType(SearchBox.Type.SERVER_ADDRESS);
            this.bulkActionInput.setMaxLength(256);
            break;
         case CHAT:
            this.bulkActionInput.setType(SearchBox.Type.ANY);
            this.bulkActionInput.setMaxLength(256);
            break;
         case RCT:
            this.bulkActionInput.setType(SearchBox.Type.NUMBERS_ONLY);
            this.bulkActionInput.setMaxLength(2);
      }
   }

   private void drawCenteredIconText(MenuDrawContext menuDrawContext, String icon, String tr, float f, float y, float f2, float height, ZenithColor zenithColor) {
      float width = connectIconFont.width(icon);
      float width2 = nickFont.width(tr);
      float f3 = 2.0F;
      float f4 = width + f3 + width2;
      if (f4 <= f2 - 6.0F) {
         float floatValue = f + (f2 - f4) / 2.0F;
         menuDrawContext.drawText(connectIconFont, icon, floatValue, centeredTextY(connectIconFont, y, height), zenithColor);
         menuDrawContext.drawText(nickFont, tr, floatValue + width + f3, centeredTextY(nickFont, y, height), zenithColor);
      } else if (width2 <= f2 - 8.0F) {
         menuDrawContext.drawText(nickFont, tr, f + (f2 - width2) / 2.0F, centeredTextY(nickFont, y, height), zenithColor);
      } else {
         menuDrawContext.drawText(connectIconFont, icon, f + (f2 - width) / 2.0F, centeredTextY(connectIconFont, y, height), zenithColor);
      }
   }

   private void renderListRow(
      MenuDrawContext menuDrawContext, String selected, int n, float floatValue, float f, float guiScaledHeight, float previewAlpha, float f2, float f3, ZenithStyle currentStyle
   ) {
      BotScreen.RowAnim rowAnim = this.rowAnims.computeIfAbsent(selected, str -> new BotScreen.RowAnim());
      BotClient botClient = HeadlessBots.get(selected);
      boolean flag = botClient != null && botClient.isJoined();
      float f4 = f + n * 25.0F - this.listScroll;
      boolean flag2 = this.isMultiSelectDown() || this.isShiftDown();
      boolean flag3 = f3 >= f && f3 <= guiScaledHeight && f2 >= floatValue + 4.0F && f2 <= floatValue + 4.0F + 227.0F && f3 >= f4 && f3 <= f4 + 23.0F;
      float f5 = rowAnim.appear.animateTo(1.0F);
      float f6 = rowAnim.hover.animateTo(flag3 ? 1.0F : 0.0F);
      float previewAlpha2 = previewAlpha * f5;
      float y = f4 + (1.0F - f5) * 6.0F;
      float floatValue2 = floatValue + 4.0F;
      menuDrawContext.drawRoundedRect(floatValue2, y, 227.0F, 23.0F, headerRadius, listColor.method7294(previewAlpha2));
      float hover = rowAnim.select.animateTo(this.selectedNames.contains(selected) ? 1.0F : 0.0F);
      menuDrawContext.drawRoundedRect(floatValue2, y, 23.0F, 23.0F, headerRadius, listColor.method7294(previewAlpha2));
      float floatValue3 = floatValue2 + 227.0F - 64.0F;
      menuDrawContext.drawRoundedRect(floatValue3, y, 64.0F, 23.0F, headerRadius, listColor.method7294(previewAlpha2));
      if (flag) {
         boolean flag4 = !flag2 && f2 >= floatValue3 && f2 <= floatValue3 + 64.0F && f3 >= y && f3 <= y + 23.0F && f3 >= f && f3 <= guiScaledHeight;
         float f7 = rowAnim.disc.animateTo(flag4 ? 1.0F : 0.0F);
         float f8 = 23.0F;
         menuDrawContext.enableScissor(floatValue2, y, floatValue2 + 227.0F, y + 23.0F);
         if (f7 < 0.999F) {
            String address = botClient.getAddress();
            String text = serverLabel(address);
            Identifier prewarm = ServerIconCache.get(address);
            float width = nickFont.width(text);
            float f9 = width + (prewarm != null ? 11.0F : 0.0F);
            float x = floatValue3 + (64.0F - f9) / 2.0F;
            float y2 = y - f7 * f8;
            float previewAlpha3 = previewAlpha2 * (1.0F - f7);
            if (prewarm != null) {
               float y3 = y2 + 8.0F;
               RenderShaders.method5294(
                  MatrixUtils.method664(menuDrawContext.pose()), prewarm, x, y3, 7.0F, 7.0F, serverIconRadius, ZenithColor.INSTANCE.method7294(previewAlpha3)
               );
               x += 11.0F;
            }

            menuDrawContext.drawText(nickFont, text, x, centeredTextY(nickFont, y2, 23.0F), ZenithColor.INSTANCE.method7294(previewAlpha3));
         }

         if (f7 > 0.001F) {
            String text = tr("module.bot.disconnect");
            float width = connectIconFont.width("]");
            float width2 = nickFont.width(text);
            float f9 = 2.0F;
            float floatValue4 = floatValue3 + (64.0F - width - f9 - width2) / 2.0F;
            float y2 = y + (1.0F - f7) * f8;
            float previewAlpha3 = previewAlpha2 * f7;
            menuDrawContext.drawText(connectIconFont, "]", floatValue4, centeredTextY(connectIconFont, y2, 23.0F), disconnectColor.method7294(previewAlpha3));
            menuDrawContext.drawText(nickFont, text, floatValue4 + width + f9, centeredTextY(nickFont, y2, 23.0F), disconnectColor.method7294(previewAlpha3));
         }

         menuDrawContext.disableScissor();
      } else {
         String text = "Offline";
         float width = connectIconFont.width("]");
         float width2 = nickFont.width(text);
         float f7 = 2.0F;
         float floatValue4 = floatValue3 + (64.0F - width - f7 - width2) / 2.0F;
         menuDrawContext.drawText(connectIconFont, "]", floatValue4, centeredTextY(connectIconFont, y, 23.0F), inputEmptyColor.method7294(previewAlpha2));
         menuDrawContext.drawText(nickFont, text, floatValue4 + width + f7, centeredTextY(nickFont, y, 23.0F), inputEmptyColor.method7294(previewAlpha2));
      }

      boolean flag4 = !flag2 && f2 >= floatValue2 && f2 <= floatValue2 + 23.0F && f3 >= y && f3 <= y + 23.0F && f3 >= f && f3 <= guiScaledHeight;
      float f7 = rowAnim.delete.animateTo(flag4 ? 1.0F : 0.0F);
      Identifier prewarm = BotAvatarCache.prewarm(selected);
      if (prewarm != null) {
         float x = floatValue2 + 4.0F;
         float y2 = y + 4.0F;
         ZenithColor zenithColor = f7 > 0.0F ? ZenithColor.INSTANCE.method7295(deleteDarkColor, 0.62F * f7) : ZenithColor.INSTANCE;
         RenderShaders.method5294(MatrixUtils.method664(menuDrawContext.pose()), prewarm, x, y2, 15.0F, 15.0F, avatarRadius, zenithColor.method7294(previewAlpha2));
      }

      if (f7 > 0.001F) {
         float width = deleteIconFont.width("2");
         menuDrawContext.drawText(
            deleteIconFont,
            "2",
            floatValue2 + (23.0F - width) / 2.0F - 0.5F,
            centeredTextY(deleteIconFont, y, 23.0F) + 0.5F,
            deleteColor.method7294(previewAlpha2 * f7)
         );
      }

      if (f7 < 0.5F && HeadlessBots.hasProxy(selected)) {
         float floatValue4 = floatValue2 + 23.0F - 4.0F - 4.0F;
         float y2 = y + 4.0F;
         menuDrawContext.drawRoundedRect(floatValue4, y2, 4.0F, 4.0F, proxyDotRadius, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha2 * (1.0F - f7 * 2.0F)));
      }

      float floatValue4 = floatValue2 + 23.0F + 6.0F + f6 * 2.0F;
      float centeredTextY2 = centeredTextY(nickFont, y, 23.0F);
      ZenithColor zenithColor = hover > 0.001F ? ZenithColor.INSTANCE.method7295(currentStyle.getPrimaryColor().getZenithColor(), hover) : ZenithColor.INSTANCE;
      menuDrawContext.drawText(nickFont, selected, floatValue4, centeredTextY2, zenithColor.method7294(previewAlpha2));
      this.renderRowModuleBadges(menuDrawContext, selected, floatValue4 + nickFont.width(selected) + 6.0F, floatValue3 - 5.0F, y, previewAlpha2, currentStyle);
   }

   private void renderRowModuleBadges(MenuDrawContext menuDrawContext, String selected, float f, float f2, float y, float previewAlpha, ZenithStyle currentStyle) {
      float floatValue = f;
      float y2 = y + 6.5F;
      int n = 0;

      for (String supportedModuleName : BotModuleManager.supportedModuleNames()) {
         if (this.isBotModuleEnabled(selected, supportedModuleName)) {
            String shortModuleName = this.shortModuleName(supportedModuleName);
            float width = moduleMenuFont.width(shortModuleName) + 7.0F;
            if (floatValue + width > f2 || n >= 2) {
               break;
            }

            menuDrawContext.drawRoundedRect(floatValue, y2, width, 10.0F, roleBadgeRadius, currentStyle.getPrimaryColor().getZenithColor().method7293(34).method7294(previewAlpha));
            menuDrawContext.drawText(
               moduleMenuFont, shortModuleName, floatValue + 3.5F, centeredTextY(moduleMenuFont, y2, 10.0F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );
            floatValue += width + 3.0F;
            n++;
         }
      }
   }

   private void renderModuleMenu(MenuDrawContext menuDrawContext, float previewAlpha, float f, float f2, ZenithStyle currentStyle) {
      String moduleMenuBotName = this.moduleMenuBotName();
      List<String> supportedModuleNames = BotModuleManager.supportedModuleNames();
      if (moduleMenuBotName != null && !supportedModuleNames.isEmpty()) {
         float f3 = this.moduleMenuAnimation.animateTo(1.0F);
         float previewAlpha2 = previewAlpha * clamp01(f3);
         float y = this.moduleMenuY + (1.0F - f3) * 6.0F;
         float height = 26.0F + supportedModuleNames.size() * 20.0F + Math.max(0, supportedModuleNames.size() - 1) * 3.0F;
         RenderShaders.method5299(
            MatrixUtils.method664(menuDrawContext.pose()), this.moduleMenuX, y + 5.0F, 118.0F, height, 18.0F, moduleMenuRadius, shadowColor.method7294(previewAlpha2)
         );
         RenderShaders.method5287(
            MatrixUtils.method664(menuDrawContext.pose()), this.moduleMenuX, y, 118.0F, height, 16.0F, moduleMenuRadius, blurColor.method7294(previewAlpha2), true, false
         );
         menuDrawContext.drawRoundedRect(this.moduleMenuX, y, 118.0F, height, moduleMenuRadius, currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha2));
         float y2 = y + 4.0F;
         menuDrawContext.drawText(
            moduleMenuFont,
            fitText(moduleMenuFont, moduleMenuBotName, 110.0F),
            this.moduleMenuX + 4.0F,
            centeredTextY(moduleMenuFont, y2, 18.0F),
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha2)
         );
         float y3 = y2 + 18.0F;

         for (String supportedModuleName : supportedModuleNames) {
            boolean botModuleEnabled = this.isBotModuleEnabled(moduleMenuBotName, supportedModuleName);
            boolean flag = f >= this.moduleMenuX + 4.0F && f <= this.moduleMenuX + 118.0F - 4.0F && f2 >= y3 && f2 <= y3 + 20.0F;
            ZenithColor zenithColor = (botModuleEnabled ? currentStyle.getPrimaryColor().getZenithColor().method7293(42) : currentStyle.getSurfaceDisableBackground().getZenithColor())
               .method7295(currentStyle.getPrimaryColor().getZenithColor(), flag ? 0.12F : 0.0F);
            menuDrawContext.drawRoundedRect(this.moduleMenuX + 4.0F, y3, 110.0F, 20.0F, moduleMenuRowRadius, zenithColor.method7294(previewAlpha2));
            ZenithColor zenithColor2 = botModuleEnabled ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getTextSecondary().getZenithColor();
            menuDrawContext.drawText(
               moduleMenuFont,
               fitText(moduleMenuFont, supportedModuleName, 85.0F),
               this.moduleMenuX + 4.0F + 6.0F,
               centeredTextY(moduleMenuFont, y3, 20.0F),
               zenithColor2.method7294(previewAlpha2)
            );
            float width = 6.0F;
            float floatValue = this.moduleMenuX + 118.0F - 4.0F - 10.0F;
            float y4 = y3 + (20.0F - width) / 2.0F;
            menuDrawContext.drawRoundedRect(
               floatValue,
               y4,
               width,
               width,
               ProxyDotRadiusData.method8069(width / 2.0F),
               (botModuleEnabled ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getDisableActiveBg().getZenithColor()).method7294(previewAlpha2)
            );
            y3 += 23.0F;
         }
      } else {
         this.moduleMenuBotName = null;
      }
   }

   private boolean isBotModuleEnabled(String moduleMenuBotName, String supportedModuleName) {
      BotClient botClient = HeadlessBots.get(moduleMenuBotName);
      if (botClient != null) {
         BotModule botModule = botClient.getModules().getModule(supportedModuleName);
         if (botModule != null) {
            return botModule.isEnabled();
         }
      }

      return HeadlessBots.isModuleEnabledInProfile(moduleMenuBotName, supportedModuleName);
   }

   private String shortModuleName(String supportedModuleName) {
      if (supportedModuleName != null && !supportedModuleName.isBlank()) {
         String str = supportedModuleName.replace("BotAuto", "A").replace("Auto", "A").replace("Bot", "");
         return str.length() <= 6 ? str : str.substring(0, 6);
      } else {
         return "";
      }
   }

   private String moduleMenuBotName() {
      if (this.moduleMenuBotName == null) {
         return null;
      } else {
         for (String allName : HeadlessBots.allNames()) {
            if (allName.equalsIgnoreCase(this.moduleMenuBotName)) {
               return allName;
            }
         }

         return null;
      }
   }

   private void openModuleMenu(String moduleMenuBotName, float f, float moduleMenuY) {
      if (moduleMenuBotName == null) {
         this.closeModuleMenu();
      } else {
         this.versionMenuOpen = false;
         this.moduleMenuAnimation.snapTo(0.0F);
         this.moduleMenuBotName = moduleMenuBotName;
         this.moduleMenuX = f + 227.0F + 3.0F;
         this.moduleMenuY = moduleMenuY;
      }
   }

   private void closeModuleMenu() {
      this.moduleMenuBotName = null;
   }

   private boolean handleModuleMenuClick(double x, double y) {
      String moduleMenuBotName = this.moduleMenuBotName();
      List<String> supportedModuleNames = BotModuleManager.supportedModuleNames();
      if (moduleMenuBotName != null && !supportedModuleNames.isEmpty()) {
         float f = 26.0F + supportedModuleNames.size() * 20.0F + Math.max(0, supportedModuleNames.size() - 1) * 3.0F;
         boolean flag = x >= this.moduleMenuX && x <= this.moduleMenuX + 118.0F && y >= this.moduleMenuY && y <= this.moduleMenuY + f;
         if (!flag) {
            this.closeModuleMenu();
            return false;
         } else {
            float f2 = this.moduleMenuY + 4.0F + 18.0F;

            for (String supportedModuleName : supportedModuleNames) {
               if (x >= this.moduleMenuX + 4.0F && x <= this.moduleMenuX + 118.0F - 4.0F && y >= f2 && y <= f2 + 20.0F) {
                  boolean botModuleEnabled = this.isBotModuleEnabled(moduleMenuBotName, supportedModuleName);
                  HeadlessBots.setModuleEnabled(moduleMenuBotName, supportedModuleName, !botModuleEnabled);
                  return true;
               }

               f2 += 23.0F;
            }

            return true;
         }
      } else {
         this.closeModuleMenu();
         return false;
      }
   }

   private int selectedProtocolVersion() {
      return this.selectedName == null ? -1 : HeadlessBots.getProtocolVersion(this.selectedName);
   }

   private void applyVersionToSelection(int protocol) {
      LinkedHashSet<String> strings = new LinkedHashSet<>(this.selectedNames);
      if (this.selectedName != null) {
         strings.add(this.selectedName);
      }

      for (String name : strings) {
         HeadlessBots.setProtocolVersion(name, protocol);
      }
   }

   private float versionChipLeftX(float floatValue) {
      return floatValue + 319.0F + 157.0F - this.connectButtonWidth - 4.0F - 34.0F;
   }

   private float versionMenuX(float floatValue) {
      return Math.min(this.versionChipLeftX(floatValue), floatValue + 480.0F - 4.0F - 72.0F);
   }

   private float versionMenuY(float y) {
      return y + 4.0F + 23.0F + 3.0F;
   }

   private float versionMenuViewportHeight() {
      int n = Math.min(9, BotProtocolVersions.ENTRIES.size());
      return n * 14.0F + Math.max(0, n - 1) * 2.0F;
   }

   private float versionMenuMaxScroll() {
      float f = 16.0F;
      float f2 = BotProtocolVersions.ENTRIES.size() * f - 2.0F;
      return Math.max(0.0F, f2 - this.versionMenuViewportHeight());
   }

   private void renderVersionMenu(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha, float f, float f2, ZenithStyle currentStyle) {
      if (this.versionMenuOpen) {
         if (this.selectedName == null) {
            this.versionMenuOpen = false;
         } else {
            float versionMenuX = this.versionMenuX(floatValue);
            float f3 = this.versionMenuAnimation.animateTo(1.0F);
            float previewAlpha2 = previewAlpha * clamp01(f3);
            float y2 = this.versionMenuY(y) + (1.0F - f3) * 6.0F;
            float versionMenuViewportHeight = this.versionMenuViewportHeight();
            float height = 8.0F + versionMenuViewportHeight;
            RenderShaders.method5299(
               MatrixUtils.method664(menuDrawContext.pose()), versionMenuX, y2 + 5.0F, 72.0F, height, 18.0F, moduleMenuRadius, shadowColor.method7294(previewAlpha2)
            );
            RenderShaders.method5287(
               MatrixUtils.method664(menuDrawContext.pose()), versionMenuX, y2, 72.0F, height, 16.0F, moduleMenuRadius, blurColor.method7294(previewAlpha2), true, false
            );
            menuDrawContext.drawRoundedRect(versionMenuX, y2, 72.0F, height, moduleMenuRadius, currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha2));
            this.versionMenuScroll = Math.max(0.0F, Math.min(this.versionMenuScroll, this.versionMenuMaxScroll()));
            float f4 = 16.0F;
            float y3 = y2 + 4.0F;
            int selectedProtocolVersion = this.selectedProtocolVersion();
            menuDrawContext.enableScissor(versionMenuX, y3, versionMenuX + 72.0F, y3 + versionMenuViewportHeight);
            float y4 = y3 - this.versionMenuScroll;

            for (BotProtocolVersions.Entry entry : BotProtocolVersions.ENTRIES) {
               if (y4 + 14.0F >= y3 && y4 <= y3 + versionMenuViewportHeight) {
                  boolean flag = entry.protocol() == selectedProtocolVersion;
                  boolean flag2 = f >= versionMenuX + 4.0F
                     && f <= versionMenuX + 72.0F - 4.0F
                     && f2 >= Math.max(y4, y3)
                     && f2 <= Math.min(y4 + 14.0F, y3 + versionMenuViewportHeight);
                  ZenithColor zenithColor = (flag ? currentStyle.getPrimaryColor().getZenithColor().method7293(42) : currentStyle.getSurfaceDisableBackground().getZenithColor())
                     .method7295(currentStyle.getPrimaryColor().getZenithColor(), flag2 ? 0.12F : 0.0F);
                  menuDrawContext.drawRoundedRect(versionMenuX + 4.0F, y4, 64.0F, 14.0F, moduleMenuRowRadius, zenithColor.method7294(previewAlpha2));
                  ZenithColor zenithColor2 = flag ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getTextSecondary().getZenithColor();
                  menuDrawContext.drawText(versionFont, entry.label(), versionMenuX + 4.0F + 5.0F, centeredTextY(versionFont, y4, 14.0F), zenithColor2.method7294(previewAlpha2));
               }

               y4 += f4;
            }

            menuDrawContext.disableScissor();
         }
      }
   }

   private boolean handleVersionMenuClick(double x, double y, float floatValue, float f) {
      if (!this.versionMenuOpen) {
         return false;
      } else {
         float versionMenuX = this.versionMenuX(floatValue);
         float versionMenuY = this.versionMenuY(f);
         float versionMenuViewportHeight = this.versionMenuViewportHeight();
         float f2 = 8.0F + versionMenuViewportHeight;
         boolean flag = x >= versionMenuX && x <= versionMenuX + 72.0F && y >= versionMenuY && y <= versionMenuY + f2;
         if (!flag) {
            this.versionMenuOpen = false;
            return false;
         } else {
            float f3 = 16.0F;
            float f4 = (float)y - (versionMenuY + 4.0F) + this.versionMenuScroll;
            int n = (int)Math.floor(f4 / f3);
            var entries = BotProtocolVersions.ENTRIES;
            if (n >= 0 && n < entries.size() && f4 - n * f3 <= 14.0F) {
               this.applyVersionToSelection(((BotProtocolVersions.Entry)entries.get(n)).protocol());
               this.versionMenuOpen = false;
            }

            return true;
         }
      }
   }

   private void renderChatMessages(MenuDrawContext menuDrawContext, BotClient chatAnimClient, float floatValue, float y, float previewAlpha, ZenithStyle currentStyle) {
      if (chatAnimClient != null) {
         ChatMessage[] elements = chatAnimClient.getChatLog().toArray(new ChatMessage[0]);
         if (chatAnimClient != this.chatAnimClient) {
            this.chatAnimClient = chatAnimClient;
            this.chatAnims.clear();
            this.wrapCache.clear();

            for (ChatMessage chatMessage : elements) {
               this.chatAnims.put(chatMessage, new Animation(260L, 1.0F, Easing.INSTANCE15));
            }
         }

         var setFromMap = Collections.newSetFromMap(new IdentityHashMap(elements.length * 2));
         Collections.addAll(setFromMap, elements);
         var iterator = this.chatAnims.keySet().iterator();

         while (iterator.hasNext()) {
            ChatMessage chatMessage = (ChatMessage)iterator.next();
            if (!setFromMap.contains(chatMessage)) {
               iterator.remove();
               this.wrapCache.remove(chatMessage);
            }
         }

         if (elements.length > 0) {
            float f = chatLineStepCache;
            float chatTimeTopOffsetCache2 = chatTimeTopOffsetCache;
            float x1 = floatValue + 8.0F + chatTimeColumnWidthCache + 8.0F;
            float f2 = floatValue + 232.0F - 8.0F - x1;
            float[] floats = new float[elements.length];
            List[] lists = new List[elements.length];
            float f3 = 99.5F;
            float f4 = 0.0F;

            for (int i = 0; i < elements.length; i++) {
               ChatMessage key = elements[i];
               Animation animation = this.chatAnims.computeIfAbsent(key, chatAnim -> new Animation(260L, 0.0F, Easing.INSTANCE15));
               float f5 = animation.animateTo(1.0F);
               var components = this.wrapCache.get(key);
               if (components == null) {
                  components = wrapText(key.text(), nickFont, f2);
                  this.wrapCache.put(key, components);
               }

               floats[i] = f5;
               lists[i] = components;

               for (int j = 0; j < components.size(); j++) {
                  f4 += f * f5;
               }
            }

            float f5 = Math.max(0.0F, f4 - f3);
            this.chatScroll = Math.max(0.0F, Math.min(this.chatScroll, f5));
            float y2 = y + 107.5F - 4.0F - f4 + this.chatScroll;
            menuDrawContext.enableScissor(floatValue, y, floatValue + 232.0F, y + 107.5F);

            for (int i = 0; i < elements.length; i++) {
               float f6 = floats[i];
               var list = lists[i];

               for (int j = 0; j < list.size(); j++) {
                  if (y2 + f > y && y2 < y + 107.5F) {
                     float previewAlpha2 = previewAlpha * f6;
                     if (j == 0 && elements[i].time() != null) {
                        menuDrawContext.drawText(timeFont, elements[i].time(), floatValue + 8.0F, y2 + chatTimeTopOffsetCache2, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha2));
                     }

                     menuDrawContext.drawText(nickFont, (Component)list.get(j), x1, y2, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha2).method7287());
                  }

                  y2 += f * f6;
               }
            }

            menuDrawContext.disableScissor();
         }
      }
   }

   private static String tr(String tabKey) {
      return Zenith.getInstance().getLocalization().translate(tabKey);
   }

   private static float centeredTextY(Font nickFont, float y, float height) {
      FontData.MetricsData metrics = nickFont.getFont().getMetrics();
      float f = (metrics.ascender() - metrics.descender()) * nickFont.getSize();
      return y + (height - f) / 2.0F;
   }

   private static float clamp01(float f) {
      return Math.max(0.0F, Math.min(1.0F, f));
   }

   private static float lineHeight(Font nickFont) {
      FontData.MetricsData metrics = nickFont.getFont().getMetrics();
      return (metrics.ascender() - metrics.descender()) * nickFont.getSize();
   }

   private static String fitText(Font moduleMenuFont, String label, float maxWidth) {
      if (label != null && !label.isEmpty()) {
         BotScreen.FitKey fitKey = new BotScreen.FitKey(moduleMenuFont, label, maxWidth);
         String str = FIT_CACHE.get(fitKey);
         if (str == null) {
            str = fitTextUncached(moduleMenuFont, label, maxWidth);
            FIT_CACHE.put(fitKey, str);
         }

         return str;
      } else {
         return label == null ? "" : label;
      }
   }

   private static String fitTextUncached(Font moduleMenuFont, String label, float maxWidth) {
      if (moduleMenuFont.width(label) <= maxWidth) {
         return label;
      } else {
         String str = "...";
         int length = label.length();

         while (length > 0 && moduleMenuFont.width(label.substring(0, length) + str) > maxWidth) {
            length--;
         }

         return length <= 0 ? str : label.substring(0, length) + str;
      }
   }

   private static float listContentHeight(int size) {
      return size <= 0 ? 0.0F : size * 25.0F - 2.0F;
   }

   private static float listMaxScroll(int size) {
      return Math.max(0.0F, listContentHeight(size) - 211.0F);
   }

   private List<String> filterBots(List<String> allNames) {
      String str = this.searchInput.getText().trim();
      if (str.isEmpty()) {
         return allNames;
      } else {
         String lowerCase = str.toLowerCase(Locale.ROOT);
         ArrayList<String> strings = new ArrayList<>();

         for (String allName : allNames) {
            if (allName.toLowerCase(Locale.ROOT).contains(lowerCase)) {
               strings.add(allName);
            }
         }

         return strings;
      }
   }

   private List<String> visibleBots() {
      return this.filterBots(HeadlessBots.allNames());
   }

   private BotClient selectedClient() {
      return this.selectedName == null ? null : HeadlessBots.get(this.selectedName);
   }

   private static String serverLabel(String address) {
      if (address != null && !"No Server".equalsIgnoreCase(address)) {
         String str = serverLabelCache.get(address);
         if (str != null) {
            return str;
         } else {
            String serverLabel = computeServerLabel(address);
            serverLabelCache.put(address, serverLabel);
            return serverLabel;
         }
      } else {
         return "";
      }
   }

   private static String computeServerLabel(String address) {
      int indexOf = address.indexOf(58);
      String str = indexOf > 0 ? address.substring(0, indexOf) : address;
      String[] strings = str.split("\\.");
      if (strings.length < 2) {
         return str;
      } else {
         for (String str2 : strings) {
            if (!str2.chars().allMatch(Character::isDigit)) {
               return strings[strings.length - 2];
            }
         }

         return str;
      }
   }

   public boolean isSearch() {
      return this.addInput.isSelected()
         || this.searchInput.isSelected()
         || this.proxyManagerInput.isSelected()
         || this.chatInput.isSelected()
         || this.connectInput.isSelected();
   }

   private static List<Component> wrapText(Component text, Font nickFont, float f) {
      ArrayList<Component> components = new ArrayList<>();
      ArrayList subList = new ArrayList();
      float textWidth2 = 0.0F;
      int toIndex = -1;

      for (BotScreen.StyledTextFragment splitTextToGlyph : splitTextToGlyphs(text)) {
         if (splitTextToGlyph.isLineBreak()) {
            addWrappedLine(components, subList);
            subList.clear();
            textWidth2 = 0.0F;
            toIndex = -1;
         } else if (!subList.isEmpty() || !splitTextToGlyph.isWhitespace()) {
            subList.add(splitTextToGlyph);
            textWidth2 += splitTextToGlyph.width(nickFont);
            if (splitTextToGlyph.isWhitespace()) {
               toIndex = subList.size() - 1;
            }

            if (f > 0.0F && textWidth2 > f && subList.size() > 1) {
               if (toIndex > 0) {
                  addWrappedLine(components, subList.subList(0, toIndex));
                  subList = new ArrayList(subList.subList(toIndex + 1, subList.size()));
                  trimLeadingWhitespace(subList);
               } else {
                  BotScreen.StyledTextFragment styledTextFragment = (BotScreen.StyledTextFragment)subList.remove(subList.size() - 1);
                  addWrappedLine(components, subList);
                  subList = new ArrayList();
                  if (!styledTextFragment.isWhitespace()) {
                     subList.add(styledTextFragment);
                  }
               }

               textWidth2 = textWidth(subList, nickFont);
               toIndex = lastWhitespace(subList);
            }
         }
      }

      addWrappedLine(components, subList);
      return components;
   }

   private static List<BotScreen.StyledTextFragment> splitTextToGlyphs(Component text) {
      ArrayList<BotScreen.StyledTextFragment> styledTextFragments = new ArrayList<>();
      text.visit((var1x, str) -> {
         int endIndex = 0;

         while (endIndex < str.length()) {
            int codePointAt = str.codePointAt(endIndex);
            int n = endIndex + Character.charCount(codePointAt);
            styledTextFragments.add(new BotScreen.StyledTextFragment(str.substring(endIndex, n), var1x));
            endIndex = n;
         }

         return Optional.empty();
      }, Style.EMPTY);
      return styledTextFragments;
   }

   private static void addWrappedLine(List<Component> components, List<BotScreen.StyledTextFragment> subList) {
      var styledTextFragments = new ArrayList<>(subList);
      trimTrailingWhitespace(styledTextFragments);
      if (!styledTextFragments.isEmpty()) {
         components.add(toText(styledTextFragments));
      }
   }

   private static Component toText(List<BotScreen.StyledTextFragment> styledTextFragments) {
      MutableComponent empty = Component.empty();

      for (BotScreen.StyledTextFragment styledTextFragment : styledTextFragments) {
         empty.append(Component.literal(styledTextFragment.value()).setStyle(styledTextFragment.style()));
      }

      return empty;
   }

   private static float textWidth(List<BotScreen.StyledTextFragment> subList, Font nickFont) {
      float f = 0.0F;

      for (BotScreen.StyledTextFragment sub : subList) {
         f += sub.width(nickFont);
      }

      return f;
   }

   private static int lastWhitespace(List<BotScreen.StyledTextFragment> subList) {
      for (int i = subList.size() - 1; i >= 0; i--) {
         if (((BotScreen.StyledTextFragment)subList.get(i)).isWhitespace()) {
            return i;
         }
      }

      return -1;
   }

   private static void trimLeadingWhitespace(List<BotScreen.StyledTextFragment> subList) {
      while (!subList.isEmpty() && ((BotScreen.StyledTextFragment)subList.get(0)).isWhitespace()) {
         subList.remove(0);
      }
   }

   private static void trimTrailingWhitespace(List<BotScreen.StyledTextFragment> styledTextFragments) {
      while (!styledTextFragments.isEmpty() && ((BotScreen.StyledTextFragment)styledTextFragments.get(styledTextFragments.size() - 1)).isWhitespace()) {
         styledTextFragments.remove(styledTextFragments.size() - 1);
      }
   }

   private void sendChatMessage() {
      String text = this.chatInput.getText();
      BotClient selectedClient = this.selectedClient();
      if (selectedClient != null && !text.isBlank()) {
         selectedClient.sendChat(text);
      }

      this.chatInput.setText("");
      this.chatInput.setCursor(0);
   }

   private static boolean isValidBotName(String name) {
      int length = name == null ? 0 : name.trim().length();
      return length >= 3 && length <= 16;
   }

   private void addBotFromSearch() {
      String name = this.addInput.getText().trim();
      if (isValidBotName(name) && HeadlessBots.add(name)) {
         this.addInput.setText("");
         this.addInput.setCursor(0);
         this.selectOnly(name);
         this.listScroll = 0.0F;
      }
   }

   private List<String> selectionNames() {
      var strings = new ArrayList<>(this.selectedNames);
      if (strings.isEmpty() && this.selectedName != null) {
         strings.add(this.selectedName);
      }

      return strings;
   }

   private void applyProxyToSelection(String str) {
      String string = str == null ? "" : str.trim();
      if (!string.isBlank()) {
         HeadlessBots.addProxy(string);
      }

      for (String selectionName : this.selectionNames()) {
         HeadlessBots.setProxy(selectionName, string);
      }

      this.proxyManagerInput.setSelected(false);
   }

   private void addProxyFromManager() {
      String string = this.proxyManagerInput.getText().trim();
      if (!string.isBlank()) {
         HeadlessBots.addProxy(string);
         this.proxyManagerInput.setText("");
         this.proxyManagerInput.setCursor(0);
         this.proxyPanelScroll = 0.0F;
      }
   }

   private boolean handleProxyPanelClick(double x, double y, float floatValue, float f) {
      float proxyPanelButtonX2 = proxyPanelButtonX(floatValue);
      float proxyPanelButtonY2 = proxyPanelButtonY(f);
      if (x >= proxyPanelButtonX2 && x <= proxyPanelButtonX2 + 18.0F && y >= proxyPanelButtonY2 && y <= proxyPanelButtonY2 + 18.0F) {
         this.proxyPanelOpen = !this.proxyPanelOpen;
         savedProxyPanelOpen = this.proxyPanelOpen;
         this.proxyManagerInput.setSelected(false);
         return true;
      } else {
         float f2 = this.proxyPanelAnimation.getValue();
         if (!this.proxyPanelOpen && f2 <= 0.001F) {
            return false;
         } else {
            float moduleMenuX = proxyPanelRenderX(floatValue, Math.max(f2, this.proxyPanelOpen ? 1.0F : 0.0F));
            float proxyPanelY2 = proxyPanelY(f);
            if (!(x < moduleMenuX) && !(x > moduleMenuX + 156.0F) && !(y < proxyPanelY2) && !(y > proxyPanelY2 + 222.0F)) {
               float proxyPanelCloseHitX2 = proxyPanelCloseHitX(moduleMenuX);
               float proxyPanelCloseHitY2 = proxyPanelCloseHitY(proxyPanelY2);
               if (x >= proxyPanelCloseHitX2 && x <= proxyPanelCloseHitX2 + 14.0F && y >= proxyPanelCloseHitY2 && y <= proxyPanelCloseHitY2 + 14.0F) {
                  this.proxyPanelOpen = false;
                  savedProxyPanelOpen = false;
                  this.proxyManagerInput.setSelected(false);
                  return true;
               } else {
                  this.handleProxyPanelBodyClick(x, y, moduleMenuX, proxyPanelY2);
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   private void handleProxyPanelBodyClick(double x, double y, float moduleMenuX, float f) {
      float f2 = f + 31.0F;
      float f3 = moduleMenuX + 4.0F;
      float f4 = 126.0F;
      float f5 = f3 + f4 + 4.0F;
      if (!(y >= f2) || !(y <= f2 + 18.0F)) {
         float f6 = f + 53.0F;
         float proxyPanelRowWidth2 = proxyPanelRowWidth();
         if (!(x < f3) && !(x > f3 + proxyPanelRowWidth2) && !(y < f6) && !(y > f6 + 161.0F)) {
            float f7 = (float)(y - f6 + this.proxyPanelScroll);
            float f8 = 25.0F;
            int n = (int)(f7 / f8);
            if (n >= 0) {
               if (f7 - n * f8 > 23.0F) {
                  this.proxyManagerInput.setSelected(false);
               } else if (n == 0) {
                  this.applyProxyToSelection("");
               } else {
                  var proxyPool = HeadlessBots.getProxyPool();
                  int n2 = n - 1;
                  if (n2 < proxyPool.size()) {
                     String str = (String)proxyPool.get(n2);
                     float f9 = f3 + proxyPanelRowWidth2 - 18.0F;
                     if (x >= f9 && x <= f9 + 18.0F) {
                        if (HeadlessBots.removeProxy(str)) {
                           this.proxyHoverAnims.remove(str);
                           this.proxyPanelScroll = Math.min(this.proxyPanelScroll, proxyPanelMaxScroll(HeadlessBots.getProxyPool().size() + 1));
                        }

                        this.proxyManagerInput.setSelected(false);
                     } else {
                        this.applyProxyToSelection(str);
                     }
                  }
               }
            }
         } else {
            this.proxyManagerInput.setSelected(false);
         }
      } else if (x >= f5 && x <= f5 + 18.0F) {
         this.addProxyFromManager();
         this.proxyManagerInput.setSelected(false);
      } else if (x >= f3 && x <= f3 + f4) {
         this.focusTextBox(this.proxyManagerInput, x, y);
         this.clearMainInputFocus();
      } else {
         this.proxyManagerInput.setSelected(false);
      }
   }

   private void clearMainInputFocus() {
      this.chatInput.setSelected(false);
      this.connectInput.setSelected(false);
      this.addInput.setSelected(false);
      this.searchInput.setSelected(false);
      this.bulkActionInput.setSelected(false);
   }

   private boolean isMultiSelectDown() {
      Window window = mc.getWindow();
      return org.zenith.utility.misc.KeyCompat.isKeyDown(341) || org.zenith.utility.misc.KeyCompat.isKeyDown(345) || org.zenith.utility.misc.KeyCompat.isKeyDown(343) || org.zenith.utility.misc.KeyCompat.isKeyDown(347);
   }

   private boolean isShiftDown() {
      Window window = mc.getWindow();
      return org.zenith.utility.misc.KeyCompat.isKeyDown(340) || org.zenith.utility.misc.KeyCompat.isKeyDown(344);
   }

   private void selectOnly(String selectedName) {
      this.selectedNames.clear();
      if (selectedName != null) {
         this.selectedNames.add(selectedName);
      }

      this.selectedName = selectedName;
   }

   private void toggleSelection(String selectedName) {
      if (this.selectedNames.contains(selectedName)) {
         this.selectedNames.remove(selectedName);
         if (this.selectedName != null && this.selectedName.equals(selectedName)) {
            this.selectedName = null;
            this.chatScroll = 0.0F;
            var iterator = this.selectedNames.iterator();
            if (iterator.hasNext()) {
               String str = (String)iterator.next();
               this.selectedName = str;
            }
         }
      } else {
         this.selectedNames.add(selectedName);
         this.selectedName = selectedName;
         this.chatScroll = 0.0F;
      }
   }

   private void rangeSelect(List<String> visibleBots, int n) {
      int n2 = -1;
      if (this.selectedName != null) {
         for (int i = 0; i < visibleBots.size(); i++) {
            if (((String)visibleBots.get(i)).equalsIgnoreCase(this.selectedName)) {
               n2 = i;
               break;
            }
         }
      }

      if (n2 < 0) {
         this.selectOnly((String)visibleBots.get(n));
         this.chatScroll = 0.0F;
      } else {
         this.selectedNames.clear();

         for (int i = Math.min(n2, n); i <= Math.max(n2, n); i++) {
            this.selectedNames.add((String)visibleBots.get(i));
         }
      }
   }

   private void selectAllVisible() {
      var visibleBots = this.visibleBots();
      if (!visibleBots.isEmpty()) {
         this.selectedNames.clear();
         this.selectedNames.addAll(visibleBots);
         if (this.selectedName == null || !this.selectedNames.contains(this.selectedName)) {
            this.selectedName = (String)visibleBots.get(0);
            this.chatScroll = 0.0F;
         }
      }
   }

   private void refreshSelection(List<String> allNames) {
      if (this.selectedName != null) {
         for (String allName : allNames) {
            if (allName.equalsIgnoreCase(this.selectedName)) {
               if (!allName.equals(this.selectedName)) {
                  this.selectedName = allName;
               }

               return;
            }
         }

         this.selectedName = null;
      }

      for (String selectedName : this.selectedNames) {
         for (String allName : allNames) {
            if (allName.equalsIgnoreCase(selectedName)) {
               this.selectedName = allName;
               return;
            }
         }
      }
   }

   private boolean anyInputSelected() {
      return this.chatInput.isSelected()
         || this.connectInput.isSelected()
         || this.addInput.isSelected()
         || this.searchInput.isSelected()
         || this.bulkActionInput.isSelected()
         || this.proxyManagerInput.isSelected();
   }

   private List<String> offlineSelectedNames() {
      ArrayList<String> strings = new ArrayList<>();

      for (String selectedName : this.selectedNames) {
         if (!HeadlessBots.isOnline(selectedName)) {
            strings.add(selectedName);
         }
      }

      if (strings.isEmpty() && this.selectedName != null && !HeadlessBots.isOnline(this.selectedName)) {
         strings.add(this.selectedName);
      }

      return strings;
   }

   private void doConnect() {
      String lastAddress = this.connectInput.getText().trim();
      if (lastAddress.isBlank()) {
         ServerData currentServer = mc.getCurrentServer();
         if (currentServer != null && currentServer.ip != null) {
            lastAddress = currentServer.ip.trim();
         }
      }

      if (lastAddress.isBlank()) {
         lastAddress = HeadlessBots.getLastAddress();
      }

      if (!lastAddress.isBlank()) {
         ServerAddress string = ServerAddress.parseString(lastAddress);
         String host = string.getHost();
         int port = string.getPort();
         var offlineSelectedNames = this.offlineSelectedNames();
         if (!offlineSelectedNames.isEmpty()) {
            if (offlineSelectedNames.size() == 1) {
               HeadlessBots.connect((String)offlineSelectedNames.get(0), host, port);
            } else {
               HeadlessBots.connectAll(offlineSelectedNames, host, port);
            }

            this.connectInput.setText("");
            this.connectInput.setCursor(0);
            this.connectInput.setSelected(false);
         }
      }
   }

   private void executeBulkAction() {
      switch (this.bulkAction) {
         case CONNECT:
            this.doBulkConnect();
            break;
         case CHAT:
            this.doBulkChat();
            break;
         case RCT:
            this.doBulkRct();
      }
   }

   private void doBulkRct() {
      int rctAnarchy = this.parseRctAnarchy();
      if (rctAnarchy >= 0) {
         for (BotClient botClient : HeadlessBots.all()) {
            if (botClient.isJoined()) {
               botClient.getRct().reconnect(rctAnarchy);
            }
         }

         this.bulkActionInput.setSelected(false);
      }
   }

   private int parseRctAnarchy() {
      String str = this.bulkActionInput.getText().trim();
      if (str.isEmpty()) {
         return -1;
      } else {
         try {
            int n = Integer.parseInt(str);
            return n >= 1 && n <= 66 ? n : -1;
         } catch (NumberFormatException e) {
            return -1;
         }
      }
   }

   private void doBulkConnect() {
      String lastAddress = this.bulkActionInput.getText().trim();
      if (lastAddress.isBlank()) {
         ServerData currentServer = mc.getCurrentServer();
         if (currentServer != null && currentServer.ip != null) {
            lastAddress = currentServer.ip.trim();
         }
      }

      if (lastAddress.isBlank()) {
         lastAddress = HeadlessBots.getLastAddress();
      }

      if (!lastAddress.isBlank()) {
         var offlineBotNames = this.offlineBotNames();
         if (!offlineBotNames.isEmpty()) {
            ServerAddress string = ServerAddress.parseString(lastAddress);
            HeadlessBots.connectAll(offlineBotNames, string.getHost(), string.getPort());
            this.bulkActionInput.setText("");
            this.bulkActionInput.setCursor(0);
            this.bulkActionInput.setSelected(false);
         }
      }
   }

   private void doBulkChat() {
      String text = this.bulkActionInput.getText();
      if (!text.isBlank()) {
         for (BotClient botClient : HeadlessBots.all()) {
            if (botClient.isJoined()) {
               botClient.sendChat(text);
            }
         }

         this.bulkActionInput.setText("");
         this.bulkActionInput.setCursor(0);
         this.bulkActionInput.setSelected(false);
      }
   }

   private List<String> offlineBotNames() {
      ArrayList<String> strings = new ArrayList<>();

      for (String allName : HeadlessBots.allNames()) {
         if (!HeadlessBots.isOnline(allName)) {
            strings.add(allName);
         }
      }

      return strings;
   }

   @Override
   public void onMouseClicked(double x, double y, MouseButton enum2) {
      if (!this.closing && this.screenAnimation.isDone()) {
         if (enum2 == MouseButton.RIGHT) {
            float f = (mc.getWindow().getGuiScaledWidth() - 480.0F) / 2.0F;
            float f2 = (mc.getWindow().getGuiScaledHeight() - 320.0F) / 2.0F;
            var visibleBots = this.visibleBots();
            float f3 = f2 + 58.0F;
            float f4 = f3 + 211.0F;

            for (int i = 0; i < visibleBots.size(); i++) {
               float moduleMenuY = f3 + i * 25.0F - this.listScroll;
               if (y >= f3 && y <= f4 && x >= f + 4.0F && x <= f + 4.0F + 227.0F && y >= moduleMenuY && y <= moduleMenuY + 23.0F) {
                  this.openModuleMenu((String)visibleBots.get(i), f + 4.0F, moduleMenuY);
                  return;
               }
            }

            this.closeModuleMenu();
         }

         if (enum2 == MouseButton.LEFT) {
            float floatValue = (mc.getWindow().getGuiScaledWidth() - 480.0F) / 2.0F;
            float f = (mc.getWindow().getGuiScaledHeight() - 320.0F) / 2.0F;
            if (this.handleProxyPanelClick(x, y, floatValue, f)) {
               return;
            }

            this.proxyManagerInput.setSelected(false);
            if (this.handleModuleMenuClick(x, y)) {
               return;
            }

            if (this.handleVersionMenuClick(x, y, floatValue, f)) {
               return;
            }

            float f2 = f + 31.0F;
            boolean flag = y >= f2 && y <= f2 + 23.0F;
            float f3 = floatValue + 4.0F;
            float f4 = f3 + 114.0F - 23.0F;
            boolean flag2 = flag && x >= f3 && x <= f3 + 114.0F;
            if (flag2 && x >= f4) {
               this.bulkActionInput.setSelected(false);
               this.addBotFromSearch();
               return;
            }

            boolean flag3 = flag2 && x < f4;
            if (flag3) {
               this.focusTextBox(this.addInput, x, y);
               this.chatInput.setSelected(false);
               this.connectInput.setSelected(false);
               this.searchInput.setSelected(false);
               this.bulkActionInput.setSelected(false);
               return;
            }

            this.addInput.setSelected(false);
            float f5 = f3 + 114.0F + 4.0F;
            boolean flag4 = flag && x >= f5 && x <= f5 + 114.0F;
            if (flag4) {
               this.focusTextBox(this.searchInput, x, y);
               this.chatInput.setSelected(false);
               this.connectInput.setSelected(false);
               this.addInput.setSelected(false);
               this.bulkActionInput.setSelected(false);
               return;
            }

            this.searchInput.setSelected(false);
            float f6 = f + 275.0F;
            boolean flag5 = y >= f6 && y <= f6 + 14.0F;
            float bulkActionModeStartX2 = bulkActionModeStartX(floatValue);
            float f7 = floatValue + 4.0F + 232.0F;
            if (flag5 && x >= bulkActionModeStartX2 && x <= f7) {
               for (BotScreen.BulkAction bulkAction : BotScreen.BulkAction.values()) {
                  if (x >= bulkActionModeStartX2 && x <= bulkActionModeStartX2 + 14.0F) {
                     if (this.bulkAction != bulkAction) {
                        this.bulkActionInput.setText("");
                        this.bulkActionInput.setCursor(0);
                     }

                     this.bulkAction = bulkAction;
                     this.syncBulkActionInputRules();
                     this.clearMainInputFocus();
                     this.bulkActionInput.setSelected(true);
                     this.bulkActionInput.setFlag(false);
                     return;
                  }

                  bulkActionModeStartX2 += 17.0F;
               }
            }

            float f8 = f + 292.0F;
            boolean flag6 = y >= f8 && y <= f8 + 23.0F;
            if (flag6 && x >= floatValue + 4.0F && x <= floatValue + 4.0F + 232.0F) {
               float f9 = floatValue + 4.0F;
               float f10 = 164.0F;
               float f11 = f9 + f10 + 4.0F;
               if (x >= f11) {
                  this.executeBulkAction();
                  return;
               }

               boolean flag7 = x < f9 + f10;
               if (flag7) {
                  this.clearMainInputFocus();
                  this.focusTextBox(this.bulkActionInput, x, y);
               } else {
                  this.bulkActionInput.setSelected(false);
               }

               if (flag7) {
                  return;
               }
            }

            if (flag6 && x >= floatValue + 4.0F && x <= floatValue + 4.0F + 232.0F) {
               this.chatInput.setSelected(false);
               this.connectInput.setSelected(false);
               this.addInput.setSelected(false);
               this.searchInput.setSelected(false);
               this.bulkActionInput.setSelected(false);
               return;
            }

            this.bulkActionInput.setSelected(false);
            var visibleBots = this.visibleBots();
            float f9 = f + 58.0F;
            float f10 = f9 + 211.0F;

            for (int i = 0; i < visibleBots.size(); i++) {
               float f11 = f9 + i * 25.0F - this.listScroll;
               if (y >= f9 && y <= f10 && x >= floatValue + 4.0F && x <= floatValue + 4.0F + 227.0F && y >= f11 && y <= f11 + 23.0F) {
                  String selectedName = (String)visibleBots.get(i);
                  if (this.isMultiSelectDown()) {
                     this.toggleSelection(selectedName);
                     return;
                  }

                  if (this.isShiftDown()) {
                     this.rangeSelect(visibleBots, i);
                     return;
                  }

                  if (x <= floatValue + 4.0F + 23.0F) {
                     HeadlessBots.remove(selectedName);
                     this.rowAnims.remove(selectedName);
                     this.selectedNames.remove(selectedName);
                     if (selectedName.equals(this.selectedName)) {
                        this.chatScroll = 0.0F;
                        this.selectedName = null;
                        var iterator = this.selectedNames.iterator();
                        if (iterator.hasNext()) {
                           String str = (String)iterator.next();
                           this.selectedName = str;
                        }

                        if (this.selectedName == null && this.selectedNames.isEmpty()) {
                           var allNames = HeadlessBots.allNames();
                           if (!allNames.isEmpty()) {
                              this.selectOnly((String)allNames.get(0));
                           }
                        }
                     }

                     return;
                  }

                  if (HeadlessBots.isOnline(selectedName) && x >= floatValue + 4.0F + 227.0F - 64.0F) {
                     HeadlessBots.disconnect(selectedName);
                     return;
                  }

                  this.selectOnly(selectedName);
                  this.chatScroll = 0.0F;
                  return;
               }
            }

            float f11 = floatValue + 319.0F;
            float f12 = f + 4.0F;
            boolean flag7 = x >= f11 && x <= f11 + 157.0F && y >= f12 && y <= f12 + 23.0F;
            float f13 = f11 + 157.0F - this.connectButtonWidth;
            if (flag7 && x >= f13) {
               BotClient selectedClient = this.selectedClient();
               if (selectedClient != null && selectedClient.isJoined()) {
                  mc.gui.setScreen(new BotControlScreen(selectedClient.name()));
               } else {
                  this.doConnect();
               }

               return;
            }

            float f14 = f13 - 4.0F - 34.0F;
            if (flag7 && x >= f14) {
               this.versionMenuOpen = !this.versionMenuOpen;
               if (this.versionMenuOpen) {
                  this.versionMenuAnimation.snapTo(0.0F);
               }

               this.versionMenuScroll = 0.0F;
               this.clearMainInputFocus();
               return;
            }

            boolean flag8 = flag7 && x < f14;
            if (flag8) {
               this.focusTextBox(this.connectInput, x, y);
               this.chatInput.setSelected(false);
               return;
            }

            this.connectInput.setSelected(false);
            BotClient selectedClient = this.selectedClient();
            if (selectedClient != null && selectedClient.isJoined()) {
               float f15 = floatValue + 244.0F;
               float f16 = f + 39.0F + 130.5F + 4.0F + 107.5F + 4.0F;
               float f17 = f15 + 232.0F - 48.0F;
               if (x >= f17 && x <= f17 + 48.0F && y >= f16 && y <= f16 + 23.0F) {
                  this.sendChatMessage();
                  return;
               }

               boolean flag9 = x >= f15 && x <= f15 + 232.0F && y >= f16 && y <= f16 + 23.0F;
               if (flag9) {
                  this.focusTextBox(this.chatInput, x, y);
                  return;
               }

               this.chatInput.setSelected(false);
            }
         }

         super.onMouseClicked(x, y, enum2);
      }
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), input.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (!this.closing && this.screenAnimation.isDone()) {
         if (this.proxyManagerInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.addProxyFromManager();
               return true;
            }

            if (keyCode == 256) {
               this.proxyManagerInput.setSelected(false);
               return true;
            }

            if (this.proxyManagerInput.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         if (this.chatInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.sendChatMessage();
               return true;
            }

            if (this.chatInput.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         if (this.connectInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.doConnect();
               return true;
            }

            if (this.connectInput.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         if (this.addInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.addBotFromSearch();
               return true;
            }

            if (this.addInput.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         if (this.searchInput.isSelected() && this.searchInput.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         } else {
            if (this.bulkActionInput.isSelected()) {
               if (keyCode == 257 || keyCode == 335) {
                  this.executeBulkAction();
                  return true;
               }

               if (keyCode == 256) {
                  this.bulkActionInput.setSelected(false);
                  return true;
               }

               if (this.bulkActionInput.keyPressed(keyCode, keycode, modifiers)) {
                  return true;
               }
            }

            if (keyCode == 65 && (modifiers & 10) != 0 && !this.anyInputSelected()) {
               this.selectAllVisible();
               return true;
            } else {
               return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(CharacterEvent input) {
      return this.charTyped((char)input.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      if (this.closing || !this.screenAnimation.isDone()) {
         return false;
      } else if (this.chatInput.charTyped(codepoint, n)) {
         return true;
      } else if (this.connectInput.charTyped(codepoint, n)) {
         return true;
      } else if (this.addInput.charTyped(codepoint, n)) {
         return true;
      } else if (this.searchInput.charTyped(codepoint, n)) {
         return true;
      } else if (this.bulkActionInput.charTyped(codepoint, n)) {
         return true;
      } else {
         return this.proxyManagerInput.charTyped(codepoint, n) ? true : super.charTyped(new CharacterEvent(codepoint));
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.closing && this.screenAnimation.isDone()) {
         float floatValue = (mc.getWindow().getGuiScaledWidth() - 480.0F) / 2.0F;
         float y = (mc.getWindow().getGuiScaledHeight() - 320.0F) / 2.0F;
         if (this.versionMenuOpen) {
            float versionMenuX = this.versionMenuX(floatValue);
            float versionMenuY = this.versionMenuY(y);
            float f = 8.0F + this.versionMenuViewportHeight();
            if (mouseX >= versionMenuX && mouseX <= versionMenuX + 72.0F && mouseY >= versionMenuY && mouseY <= versionMenuY + f) {
               float f2 = 16.0F;
               this.versionMenuScroll = Math.max(0.0F, Math.min(this.versionMenuMaxScroll(), this.versionMenuScroll - (float)verticalAmount * f2));
               return true;
            }
         }

         float f = this.proxyPanelAnimation.getValue();
         if (this.proxyPanelOpen || f > 0.001F) {
            float proxyPanelRenderX2 = proxyPanelRenderX(floatValue, Math.max(f, this.proxyPanelOpen ? 1.0F : 0.0F));
            float proxyPanelY2 = proxyPanelY(y);
            float f2 = proxyPanelY2 + 53.0F;
            if (mouseX >= proxyPanelRenderX2 + 4.0F && mouseX <= proxyPanelRenderX2 + 4.0F + proxyPanelListWidth() && mouseY >= f2 && mouseY <= f2 + 161.0F) {
               int n = HeadlessBots.getProxyPool().size() + 1;
               float proxyPanelMaxScroll2 = proxyPanelMaxScroll(n);
               this.proxyPanelScroll = Math.max(0.0F, Math.min(proxyPanelMaxScroll2, this.proxyPanelScroll - (float)verticalAmount * 25.0F));
               return true;
            }
         }

         float f2 = floatValue + 244.0F;
         float f3 = y + 39.0F + 130.5F + 4.0F;
         if (mouseX >= f2 && mouseX <= f2 + 232.0F && mouseY >= f3 && mouseY <= f3 + 107.5F) {
            this.chatScroll = Math.max(0.0F, this.chatScroll + (float)verticalAmount * 14.0F);
            return true;
         } else {
            float f4 = y + 58.0F;
            float f5 = f4 + 211.0F;
            if (mouseX >= floatValue + 4.0F && mouseX <= floatValue + 4.0F + 227.0F && mouseY >= f4 && mouseY <= f5) {
               float listMaxScroll2 = listMaxScroll(this.visibleBots().size());
               this.listScroll = Math.max(0.0F, Math.min(listMaxScroll2, this.listScroll - (float)verticalAmount * 25.0F));
               return true;
            } else {
               return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   public String getSelectedBotName() {
      return this.selectedName;
   }

   @Override
   public void onClose() {
      if (!this.closing) {
         this.screenAnimation.setDuration(220L);
         this.screenAnimation.setEasing(Easing.INSTANCE16);
         this.closing = true;
      }
   }

   private static enum BulkAction {
      CONNECT("Z", "module.bot.connect", "module.bot.connect", "module.bot.connectTo"),
      CHAT("d", "module.bot.chat", "module.bot.send", "module.bot.message"),
      RCT("7", "module.bot.rct", "module.bot.rct", "module.bot.anarchy");

      public final String icon;
      public final String tabKey;
      public final String buttonKey;
      public final String placeholderKey;

      private BulkAction(String icon, String tabKey, String buttonKey, String placeholderKey) {
         this.icon = icon;
         this.tabKey = tabKey;
         this.buttonKey = buttonKey;
         this.placeholderKey = placeholderKey;
      }
   }

   private static final class FitKey {
      private final Font font;
      private final String text;
      private final float maxWidth;

      public Font font() {
         return this.font;
      }

      public String text() {
         return this.text;
      }

      public float maxWidth() {
         return this.maxWidth;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof FitKey other)) return false;
         return java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.maxWidth, other.maxWidth);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(font, text, maxWidth);
      }

      @Override
      public String toString() {
         return "FitKey[" + "font=" + this.font + ", " + "text=" + this.text + ", " + "maxWidth=" + this.maxWidth + "]";
      }

      private FitKey(Font font, String text, float maxWidth) {
         this.font = font;
         this.text = text;
         this.maxWidth = maxWidth;
      }
   }

   private static final class RowAnim {
      public final Animation appear = new Animation(220L, 0.0F, Easing.INSTANCE15);
      public final Animation hover = new Animation(150L, 0.0F, Easing.INSTANCE15);
      public final Animation delete = new Animation(160L, 0.0F, Easing.INSTANCE15);
      public final Animation disc = new Animation(220L, 0.0F, Easing.INSTANCE15);
      public final Animation select = new Animation(160L, 0.0F, Easing.INSTANCE15);
   }

   private static final class StyledTextFragment {
      private final String value;
      private final Style style;

      public String value() {
         return this.value;
      }

      public Style style() {
         return this.style;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof StyledTextFragment other)) return false;
         return java.util.Objects.equals(this.value, other.value) && java.util.Objects.equals(this.style, other.style);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(value, style);
      }

      @Override
      public String toString() {
         return "StyledTextFragment[" + "value=" + this.value + ", " + "style=" + this.style + "]";
      }

      private StyledTextFragment(String str, Style style) {
         this.value = str;
         this.style = style;
      }

      private Component asText() {
         return Component.literal(this.value).setStyle(this.style);
      }

      private float width(Font nickFont) {
         return nickFont.width(this.asText());
      }

      private boolean isLineBreak() {
         return "\n".equals(this.value) || "\r".equals(this.value);
      }

      private boolean isWhitespace() {
         return !this.value.isEmpty() && !this.isLineBreak() ? Character.isWhitespace(this.value.codePointAt(0)) : false;
      }
   }
}

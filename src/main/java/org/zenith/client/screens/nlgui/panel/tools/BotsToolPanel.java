package org.zenith.client.screens.nlgui.panel.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.ChatLogger;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.bot.BotAvatarCache;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.BotModuleManager;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.SvgIcon;
import org.zenith.client.screens.bot.BotControlScreen;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class BotsToolPanel extends ToolTab {
   private static final float MAIN_WIDTH = 208.0F;
   private static final float ASIDE_WIDTH = 172.0F;
   private static final float INPUT_HEIGHT = 18.0F;
   private static final float SERVER_BAR_HEIGHT = 48.0F;
   private static final float CHIP_HEIGHT = 16.0F;
   private static final float CARD_HEIGHT = 21.0F;
   private static final float AVATAR = 13.0F;
   private static final float POWER = 14.0F;
   private static final float PREVIEW_HEIGHT = 98.0F;
   private static final float PREVIEW_ROUND = 6.0F;
   private static final float ACTION_HEIGHT = 26.0F;
   private static final float VIEW_BUTTON_WIDTH = 30.0F;
   private static final float VITALS_HEIGHT = 22.0F;
   private static final float DOT = 4.0F;
   private static final float EMPTY_LIST_HEIGHT = 202.0F;
   private static final int DEFAULT_PORT = 25565;
   private SearchBox nameInput;
   private SearchBox addressInput;
   private String selected;
   private String contentSignature = "";
   private BotWorldView previewView;
   private BotClient previewClient;
   private String previewOwner;
   private BoundsData previewRect;
   private float previewAlpha;
   private boolean previewHovered;
   private boolean previewHasFrame;
   private final Map<String, BoundsData> powerBounds = new LinkedHashMap<>();
   private BoundsData connectAllBounds;
   private BoundsData disconnectAllBounds;
   private BoundsData asideViewBounds;
   private double lastClickX;
   private double lastClickY;
   private final Map<String, BotsToolPanel.CardAnimations> cardAnimations = new LinkedHashMap<>();

   @Override
   public boolean pressFeedback() {
      return false;
   }

   @Override
   public String headerTitle() {
      return "Bots";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.BOTS;
   }

   @Override
   public String sectionDescription() {
      return "gui.tools.bots.desc";
   }

   @Override
   public String emptyText() {
      return "gui.tools.bots.empty";
   }

   @Override
   public float mainWidth() {
      return 208.0F;
   }

   @Override
   public float asideWidth() {
      return 172.0F;
   }

   public boolean isLive() {
      for (BotClient botClient : HeadlessBots.all()) {
         if (botClient.getPhase() != BotPhase.DISCONNECTED) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void build() {
      List<String> allNames = HeadlessBots.allNames();
      if (this.selected != null && allNames.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(this.selected))) {
         this.selected = null;
      }

      if (this.selected == null && !allNames.isEmpty()) {
         this.selected = (String)allNames.getFirst();
      }

      this.pruneCardAnimations(allNames);
      this.buildServerBar();
      String searchQuery = this.searchQuery();
      ArrayList<String> strings = new ArrayList();

      for (String allName : allNames) {
         if (searchQuery.isEmpty() || allName.toLowerCase(Locale.ROOT).contains(searchQuery)) {
            strings.add(allName);
         }
      }

      this.buildListCaption(allNames.size());
      if (strings.isEmpty()) {
         boolean flag = !allNames.isEmpty();
         if (flag) {
            this.custom(
               44.0F,
               (var1x, var2x, var3x, f, var5x, var6x, previewAlpha, var8x) -> this.drawHint(
                  var1x, f, var5x + 8.0F, var6x, t("gui.tools.bots.notFound"), previewAlpha, var8x
               )
            );
         } else {
            this.custom(202.0F, this::renderEmptyList);
         }
      } else {
         for (String selected : strings) {
            this.buildBotCard(selected);
         }
      }

      this.gap(GuiStyle.PADDING.intValue());
      this.buildAside();
   }

   private void pruneCardAnimations(List<String> allNames) {
      HashSet hashSet = new HashSet();

      for (String allName : allNames) {
         hashSet.add(allName.toLowerCase(Locale.ROOT));
      }

      this.cardAnimations.keySet().retainAll(hashSet);
   }

   private BotsToolPanel.CardAnimations animations(String selected) {
      return this.cardAnimations.computeIfAbsent(selected.toLowerCase(Locale.ROOT), cardAnimation -> new BotsToolPanel.CardAnimations());
   }

   private void buildServerBar() {
      this.custom(
         48.0F,
         (menuDrawContext, lastClickX, lastClickY, floatValue, f, width, previewAlpha, zenithStyle) -> {
            if (this.addressInput == null) {
               this.addressInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.tools.bots.address.hint"), 64.0F);
               this.addressInput.setText(HeadlessBots.getLastAddress());
               this.addressInput.setCursor(this.addressInput.getText().length());
            }

            float y = f + 3.0F;
            this.drawInput(menuDrawContext, this.addressInput, MenuGlyph.NETWORK, floatValue, y, width, previewAlpha, zenithStyle);
            float y2 = y + 18.0F + 5.0F;
            float f2 = width - 16.0F;
            float width2 = (f2 - GuiStyle.PADDING.intValue()) / 2.0F;
            float floatValue2 = floatValue + 8.0F;
            float floatValue3 = floatValue2 + width2 + GuiStyle.PADDING.intValue();
            int size = HeadlessBots.allNames().size();
            int onlineCount = this.onlineCount();
            this.connectAllBounds = new BoundsData(floatValue2, y2, width2, 16.0F);
            this.disconnectAllBounds = new BoundsData(floatValue3, y2, width2, 16.0F);
            this.drawChip(
               menuDrawContext,
               lastClickX,
               lastClickY,
               this.connectAllBounds,
               t("gui.tools.bots.connectAll"),
               String.valueOf(size),
               zenithStyle.getPrimaryColor().getZenithColor(),
               size > 0,
               previewAlpha,
               zenithStyle,
               true
            );
            this.drawChip(
               menuDrawContext,
               lastClickX,
               lastClickY,
               this.disconnectAllBounds,
               t("gui.tools.bots.disconnectAll"),
               String.valueOf(onlineCount),
               zenithStyle.getHeartIcon().getZenithColor(),
               onlineCount > 0,
               previewAlpha,
               zenithStyle,
               false
            );
            if (this.insideMain(lastClickX, lastClickY)) {
               if (this.connectAllBounds.contains(lastClickX, lastClickY)) {
                  HoverInfo.request(t("gui.tools.bots.connectAll"), t("gui.tools.bots.connectAll.desc"));
               } else if (this.disconnectAllBounds.contains(lastClickX, lastClickY)) {
                  HoverInfo.request(t("gui.tools.bots.disconnectAll"), t("gui.tools.bots.disconnectAll.desc"));
               } else if (new BoundsData(floatValue, y, width, 18.0F).contains(lastClickX, lastClickY)) {
                  HoverInfo.request(t("gui.tools.bots.caption.server"), t("gui.tools.bots.address.desc"));
               }
            }
         }
      );
   }

   private void drawChip(
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      BoundsData connectAllBounds,
      String name,
      String text,
      ZenithColor zenithColor,
      boolean flag,
      float previewAlpha,
      ZenithStyle zenithStyle,
      boolean flag2
   ) {
      boolean flag3 = flag && connectAllBounds.contains(lastClickX, lastClickY) && this.insideMain(lastClickX, lastClickY);
      ZenithColor zenithColor2 = flag ? zenithColor : zenithStyle.getTextTertiary().getZenithColor();
      menuDrawContext.drawRoundedRect(
         connectAllBounds.getFloatValue(),
         connectAllBounds.getFloatValue2(),
         connectAllBounds.width(),
         connectAllBounds.height(),
         ProxyDotRadiusData.method8069(connectAllBounds.height() / 2.0F),
         zenithColor2.method7293(flag3 ? 46 : 24).method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      float f = connectAllBounds.getFloatValue() + 7.0F;
      float f2 = connectAllBounds.getFloatValue2() + connectAllBounds.height() / 2.0F;
      ZenithColor zenithColor3 = zenithColor2.method7293(flag ? (flag3 ? 255 : 220) : 130).method7294(previewAlpha);
      if (flag2) {
         drawPlay(menuDrawContext, f, f2 - 3.0F, zenithColor3);
      } else {
         drawStop(menuDrawContext, f, f2 - 2.5F, zenithColor3);
      }

      float floatValue = f + 8.0F;
      float width = font.width(text);
      float f3 = connectAllBounds.getFloatValue() + connectAllBounds.width() - 7.0F - width - 4.0F - floatValue;
      menuDrawContext.drawText(font, fit(font, name, f3), floatValue, f2 - font.height() / 2.0F, zenithColor3);
      menuDrawContext.drawText(font, text, connectAllBounds.getFloatValue() + connectAllBounds.width() - 7.0F - width, f2 - font.height() / 2.0F, zenithColor3.method7294(0.7F));
   }

   private void buildListCaption(int size) {
      this.custom(
         16.0F,
         (menuDrawContext, n, n2, f, f2, f3, previewAlpha, zenithStyle) -> {
            Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
            float centeredTextY = f2 + (16.0F - font.height()) / 2.0F + 2.0F;
            menuDrawContext.drawText(
               font,
               HoverInfo.spaced(t("gui.tools.bots.caption.list").toUpperCase(Locale.ROOT)),
               f + 8.0F,
               centeredTextY,
               zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
            int onlineCount = this.onlineCount();
            String text = onlineCount + " / " + size + " " + t("gui.tools.bots.online");
            menuDrawContext.drawText(
               font,
               text,
               f + f3 - 8.0F - font.width(text),
               centeredTextY,
               (onlineCount > 0 ? zenithStyle.getFriendColor() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha)
            );
         }
      );
   }

   private void buildBotCard(String selected) {
      this.custom(21.0F, (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> this.renderBotCard(menuDrawContext, selected, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle), () -> {
         this.selected = selected;
         this.resetAsideScroll();
         this.invalidate();
      }, null, () -> {
         this.toggleConnection(selected);
         this.invalidate();
      });
   }

   private void renderBotCard(MenuDrawContext menuDrawContext, String selected, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      BotsToolPanel.CardAnimations animations = this.animations(selected);
      boolean insideMain = this.insideMain(lastClickX, lastClickY);
      BoundsData boundsData = new BoundsData(f + 2.0F, f2 + 1.0F, f3 - 4.0F, 19.0F);
      BoundsData boundsData2 = new BoundsData(f + f3 - 8.0F - 14.0F, f2 + 3.5F, 14.0F, 14.0F);
      this.powerBounds.put(selected, boundsData2);
      boolean flag = insideMain && boundsData2.contains(lastClickX, lastClickY);
      boolean flag2 = insideMain && boundsData.contains(lastClickX, lastClickY) && !flag;
      boolean equalsIgnoreCase = selected.equalsIgnoreCase(this.selected);
      boolean online = this.isOnline(selected);
      float f4 = animations.hover.method2808(flag2 ? 1.0F : 0.0F);
      float f5 = animations.select.method2808(equalsIgnoreCase ? 1.0F : 0.0F);
      float f6 = animations.power.method2808(flag ? 1.0F : 0.0F);
      float f7 = Math.max(f5, f4 * 0.5F);
      if (f7 > 0.002F) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            ProxyDotRadiusData.method8069(5.0F),
            zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * f7)
         );
      }

      if (f5 > 0.002F) {
         float height = boundsData.height() * 0.62F * f5;
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2() + (boundsData.height() - height) / 2.0F,
            1.5F,
            height,
            ProxyDotRadiusData.method8069(0.75F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
         );
      }

      if (flag2) {
         HoverInfo.request(selected, t("gui.tools.bots.row.desc"));
      }

      float x = f + 8.0F;
      float y = f2 + 4.0F;
      Identifier prewarm = BotAvatarCache.prewarm(selected);
      if (prewarm != null) {
         RenderShaders.method5294(
            MatrixUtils.method664(menuDrawContext.pose()),
            prewarm,
            x,
            y,
            13.0F,
            13.0F,
            ProxyDotRadiusData.method8069(3.5F),
            ZenithColor.INSTANCE.method7294(previewAlpha)
         );
      } else {
         menuDrawContext.drawRoundedRect(
            x, y, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.5F), zenithStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 4.0F)
         );
      }

      ZenithColor phaseColor = this.phaseColor(selected, zenithStyle);
      float floatValue = x + 13.0F - 4.0F + 1.0F;
      float y2 = y + 13.0F - 4.0F + 1.0F;
      menuDrawContext.drawRoundedRect(
         floatValue - 1.0F,
         y2 - 1.0F,
         6.0F,
         6.0F,
         ProxyDotRadiusData.method8069(3.0F),
         zenithStyle.getRightBackground().getZenithColor().method7293(255).method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedRect(floatValue, y2, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), phaseColor.method7294(previewAlpha));
      String phaseLabel = this.phaseLabel(selected);
      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      float width = font.width(phaseLabel) + 9.0F;
      float floatValue2 = boundsData2.getFloatValue() - 5.0F - width;
      this.drawPill(menuDrawContext, floatValue2, f2 + 6.0F, width, phaseLabel, phaseColor, previewAlpha);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
      ZenithColor zenithColor = zenithStyle.getTextSecondary()
         .getZenithColor()
         .method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(f5, f4 * 0.6F))
         .method7294(previewAlpha);
      float floatValue3 = x + 13.0F + 6.0F;
      menuDrawContext.drawText(font2, fit(font2, selected, floatValue2 - 5.0F - floatValue3), floatValue3, f2 + (21.0F - font2.height()) / 2.0F, zenithColor);
      this.drawPowerButton(menuDrawContext, boundsData2, online, f6, previewAlpha, zenithStyle);
      if (flag) {
         HoverInfo.request(selected, t(online ? "gui.tools.bots.disconnect.desc" : "gui.tools.bots.connect.desc"));
      }
   }

   private void drawPowerButton(MenuDrawContext menuDrawContext, BoundsData boundsData, boolean online, float f, float previewAlpha, ZenithStyle zenithStyle) {
      ZenithColor zenithColor = online ? zenithStyle.getHeartIcon().getZenithColor() : zenithStyle.getPrimaryColor().getZenithColor();
      menuDrawContext.drawRoundedRect(
         boundsData.getFloatValue(),
         boundsData.getFloatValue2(),
         boundsData.width(),
         boundsData.height(),
         ProxyDotRadiusData.method8069(4.0F),
         zenithColor.method7293((int)(18.0F + 34.0F * f)).method7294(previewAlpha)
      );
      ZenithColor zenithColor2 = zenithColor.method7293((int)(170.0F + 85.0F * f)).method7294(previewAlpha);
      if (online) {
         drawStop(menuDrawContext, boundsData.getFloatValue() + (boundsData.width() - 5.0F) / 2.0F, boundsData.getFloatValue2() + (boundsData.height() - 5.0F) / 2.0F, zenithColor2);
      } else {
         drawPlay(menuDrawContext, boundsData.getFloatValue() + (boundsData.width() - 3.0F) / 2.0F + 0.5F, boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F, zenithColor2);
      }
   }

   private void buildAside() {
      String selected = this.selected;
      this.previewOwner = selected;
      if (selected == null) {
         this.asideCustom(
            98.0F, (var1x, lastClickX, var3x, var4x, var5x, f, previewAlpha, zenithStyle) -> this.renderPreviewFrame(var1x, null, lastClickX, var3x, var4x, var5x, f, previewAlpha, zenithStyle)
         );
         this.asideCustom(32.0F, (menuDrawContext, var1x, n, var3x, var4x, var5x, f, zenithStyle) -> {
            float f2 = var5x - 16.0F;
            float width = f2 - 30.0F - GuiStyle.PADDING.intValue();
            ToolTab.skeleton(menuDrawContext, zenithStyle, var3x + 8.0F, var4x + 3.0F, width, 26.0F, 0, f);
            ToolTab.skeleton(menuDrawContext, zenithStyle, var3x + 8.0F + width + GuiStyle.PADDING.intValue(), var4x + 3.0F, 30.0F, 26.0F, 1, f);
         });
         this.asideCaption("gui.tools.bots.caption.state");
         this.asideCustom(22.0F, (menuDrawContext, var1x, n, var3x, var4x, var5x, f, zenithStyle) -> {
            float f2 = var5x - 16.0F;
            float width = (f2 - 6.0F) / 2.0F;
            ToolTab.skeleton(menuDrawContext, zenithStyle, var3x + 8.0F, var4x + 6.0F, width, 4.0F, 2, f);
            ToolTab.skeleton(menuDrawContext, zenithStyle, var3x + 8.0F + width + 6.0F, var4x + 6.0F, width, 4.0F, 3, f);
            ToolTab.skeleton(menuDrawContext, zenithStyle, var3x + 8.0F, var4x + 14.0F, f2 * 0.4F, 3.0F, 4, f * 0.7F);
         });
         this.asideCaption("gui.tools.bots.caption.modules");

         for (int i = 0; i < 4; i++) {
            int n = i;
            this.asideCustom(16.0F, (var1x, n2, var3x, var4x, var5x, f, f2, zenithStyle) -> {
               float f3 = f - 16.0F;
               ToolTab.skeleton(var1x, zenithStyle, var4x + 8.0F, var5x + 6.0F, f3 * (0.35F + 0.12F * (n % 3)), 4.0F, 5 + n, f2 * 0.8F);
               ToolTab.skeleton(var1x, zenithStyle, var4x + f - 8.0F - 12.0F, var5x + 5.0F, 12.0F, 6.0F, 6 + n, f2 * 0.8F);
            });
         }
      } else {
         this.asideCustom(
            98.0F,
            (menuDrawContext, var3x, var4x, var5x, f, f2, previewAlpha, zenithStyle) -> this.renderPreviewFrame(menuDrawContext, selected, var3x, var4x, var5x, f, f2, previewAlpha, zenithStyle),
            () -> this.openPreview(selected),
            null
         );
         this.buildActionRow(selected);
         this.asideCaption("gui.tools.bots.caption.state");
         this.asideCustom(
            22.0F, (menuDrawContext, var3x, var4x, var5x, y, width, previewAlpha, zenithStyle) -> this.renderVitals(menuDrawContext, selected, var3x, var4x, var5x, y, width, previewAlpha, zenithStyle)
         );
         this.asideInfo("gui.tools.bots.pos", () -> {
            BotClient var1x = HeadlessBots.get(selected);
            BotPlayer player = var1x == null ? null : var1x.getPlayer();
            return player == null ? "—" : (int)player.getX() + " " + (int)player.getY() + " " + (int)player.getZ();
         }, "gui.tools.bots.pos.desc");
         this.asideCaption("gui.tools.bots.caption.modules");

         for (String supportedModuleName : BotModuleManager.supportedModuleNames()) {
            this.buildModuleRow(selected, supportedModuleName);
         }

         this.asideCaption("gui.tools.bots.caption.network");
         this.asideInfo("gui.tools.bots.proxy", () -> {
            String var1x = HeadlessBots.getProxy(selected);
            return var1x != null && !var1x.isBlank() ? var1x : t("gui.tools.bots.proxy.none");
         }, "gui.tools.bots.proxy.desc");
         this.asideInfo("gui.tools.bots.protocol", () -> {
            int var1x = HeadlessBots.getProtocolVersion(selected);
            return var1x <= 0 ? t("gui.tools.bots.protocol.native") : String.valueOf(var1x);
         }, "gui.tools.bots.protocol.desc");
         this.asideAction("gui.tools.bots.remove", MenuGlyph.CLOSE, () -> "", () -> {
            HeadlessBots.disconnect(selected);
            HeadlessBots.remove(selected);
            this.selected = null;
            this.invalidate();
         }, "gui.tools.bots.remove.desc");
      }
   }

   private void buildActionRow(String selected) {
      this.asideCustom(
         32.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float y = f2 + 3.0F;
            float f4 = f3 - 16.0F;
            float width = f4 - 30.0F - GuiStyle.PADDING.intValue();
            BoundsData boundsData = new BoundsData(f + 8.0F, y, width, 26.0F);
            BoundsData asideViewBounds = new BoundsData(f + 8.0F + width + GuiStyle.PADDING.intValue(), y, 30.0F, 26.0F);
            this.asideViewBounds = asideViewBounds;
            boolean insideAside = this.insideAside(lastClickX, lastClickY);
            boolean online = this.isOnline(selected);
            boolean flag = insideAside && boundsData.contains(lastClickX, lastClickY);
            boolean flag2 = insideAside && asideViewBounds.contains(lastClickX, lastClickY);
            ZenithColor zenithColor = online ? zenithStyle.getHeartIcon().getZenithColor() : zenithStyle.getPrimaryColor().getZenithColor();
            menuDrawContext.drawRoundedRect(
               boundsData.getFloatValue(),
               boundsData.getFloatValue2(),
               boundsData.width(),
               boundsData.height(),
               ProxyDotRadiusData.method8069(6.0F),
               zenithColor.method7293(flag ? 62 : 34).method7294(previewAlpha)
            );
            Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
            String text = t(online ? "gui.tools.bots.disconnect" : "gui.tools.bots.connect");
            float width2 = font.width(text);
            float f5 = boundsData.getFloatValue() + (boundsData.width() - width2 - 9.0F) / 2.0F;
            ZenithColor zenithColor2 = zenithColor.method7293(flag ? 255 : 225).method7294(previewAlpha);
            if (online) {
               drawStop(menuDrawContext, f5, boundsData.getFloatValue2() + (boundsData.height() - 5.0F) / 2.0F, zenithColor2);
            } else {
               drawPlay(menuDrawContext, f5 + 1.0F, boundsData.getFloatValue2() + (boundsData.height() - 6.0F) / 2.0F, zenithColor2);
            }

            menuDrawContext.drawText(font, text, f5 + 9.0F, boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F, zenithColor2);
            menuDrawContext.drawRoundedRect(
               asideViewBounds.getFloatValue(),
               asideViewBounds.getFloatValue2(),
               asideViewBounds.width(),
               asideViewBounds.height(),
               ProxyDotRadiusData.method8069(6.0F),
               zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (flag2 ? 1.7F : 1.0F))
            );
            MenuGlyph.WORLD
               .draw(
                  menuDrawContext,
                  asideViewBounds.getFloatValue() + (asideViewBounds.width() - 6.0F) / 2.0F,
                  asideViewBounds.getFloatValue2() + (asideViewBounds.height() - 6.0F) / 2.0F,
                  (flag2 ? zenithStyle.getTextEnable() : zenithStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
               );
            if (flag) {
               HoverInfo.request(text, t(online ? "gui.tools.bots.disconnect.desc" : "gui.tools.bots.connect.desc"));
            } else if (flag2) {
               HoverInfo.request(t("gui.tools.bots.view"), t("gui.tools.bots.view.desc"));
            }
         },
         () -> {
            if (this.asideViewBounds != null && this.asideViewBounds.contains(this.lastClickX, this.lastClickY)) {
               mc.gui.setScreen(new BotControlScreen(selected));
            } else {
               this.toggleConnection(selected);
               this.invalidate();
            }
         },
         null
      );
   }

   private void buildModuleRow(String selected, String supportedModuleName) {
      String name = displayModuleName(supportedModuleName);
      this.asideCustom(16.0F, (menuDrawContext, lastClickX, lastClickY, floatValue, y, width, previewAlpha, zenithStyle) -> {
         boolean moduleEnabled = this.isModuleEnabled(selected, supportedModuleName);
         boolean flag = this.insideAside(lastClickX, lastClickY) && new BoundsData(floatValue, y, width, 16.0F).contains(lastClickX, lastClickY);
         if (flag) {
            menuDrawContext.drawRect(floatValue, y, width, 16.0F, zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * 0.45F));
            HoverInfo.request(name, this.moduleDescription(supportedModuleName));
         }

         Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
         ZenithColor zenithColor = (moduleEnabled ? zenithStyle.getTextEnable() : zenithStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha);
         menuDrawContext.drawText(font, name, floatValue + 8.0F, y + (16.0F - font.height()) / 2.0F, zenithColor);
         float f = 9.0F;
         float height = 5.0F;
         this.drawSwitch(menuDrawContext, floatValue + width - 8.0F - f, y + (16.0F - height) / 2.0F, f, height, moduleEnabled, previewAlpha, zenithStyle);
      }, () -> {
         HeadlessBots.setModuleEnabled(selected, supportedModuleName, !this.isModuleEnabled(selected, supportedModuleName));
         Zenith.getInstance().getClientSounds().method1570(false);
      }, null);
   }

   private void renderVitals(MenuDrawContext menuDrawContext, String selected, int lastClickX, int lastClickY, float floatValue, float y, float width, float previewAlpha, ZenithStyle zenithStyle) {
      BotClient botClient = HeadlessBots.get(selected);
      BotPlayer player = botClient == null ? null : botClient.getPlayer();
      float health = player == null ? 0.0F : player.getHealth();
      float f = player == null ? 20.0F : Math.max(1.0F, player.getMaxHealth());
      float f2 = Mth.clamp(health / f, 0.0F, 1.0F);
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      String text = t("gui.tools.bots.health");
      menuDrawContext.drawText(font, text, floatValue + 8.0F, y + 3.0F, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      String str = player == null ? "—" : String.format(Locale.ROOT, "%.1f", health);
      menuDrawContext.drawText(
         font,
         str,
         floatValue + width - 8.0F - font.width(str),
         y + 3.0F,
         (player == null ? zenithStyle.getTextTertiary() : zenithStyle.getTextEnable()).getZenithColor().method7294(previewAlpha)
      );
      float y2 = y + 3.0F + font.height() + 4.0F;
      float width2 = width - 16.0F;
      menuDrawContext.drawRoundedRect(
         floatValue + 8.0F, y2, width2, 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 4.0F)
      );
      if (f2 > 0.0F) {
         ZenithColor zenithColor = f2 > 0.35F ? zenithStyle.getFriendColor().getZenithColor() : zenithStyle.getHeartIcon().getZenithColor();
         menuDrawContext.drawRoundedRect(floatValue + 8.0F, y2, Math.max(3.0F, width2 * f2), 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithColor.method7294(previewAlpha));
      }

      if (this.insideAside(lastClickX, lastClickY) && new BoundsData(floatValue, y, width, 22.0F).contains(lastClickX, lastClickY)) {
         HoverInfo.request(text, t("gui.tools.bots.health.desc"));
      }
   }

   private void renderPreviewFrame(MenuDrawContext menuDrawContext, String selected, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float floatValue = f + 8.0F;
      float y = f2 + 2.0F;
      float width = f3 - 16.0F;
      float height = 90.0F;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(6.0F);
      BoundsData boundsData = new BoundsData(floatValue, y, width, height);
      boolean previewHovered = selected != null && this.insideAside(lastClickX, lastClickY) && boundsData.contains(lastClickX, lastClickY);
      menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, zenithStyle.getPanelLeftBackground().getZenithColor().method7293(190).method7294(previewAlpha));
      menuDrawContext.drawRoundedBorder(floatValue, y, width, height, 0.1F, headerRadius, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * (previewHovered ? 6.0F : 3.0F)));
      if (selected == null) {
         this.previewRect = null;
         this.renderEmptyPreview(menuDrawContext, floatValue, y, width, height, previewAlpha, zenithStyle);
      } else {
         boolean joined = this.isJoined(selected);
         if (!joined) {
            Identifier prewarm = BotAvatarCache.prewarm(selected);
            float width2 = 22.0F;
            float y2 = y + height / 2.0F - width2 / 2.0F - 7.0F;
            if (prewarm != null) {
               RenderShaders.method5294(
                  MatrixUtils.method664(menuDrawContext.pose()),
                  prewarm,
                  floatValue + (width - width2) / 2.0F,
                  y2,
                  width2,
                  width2,
                  ProxyDotRadiusData.method8069(5.0F),
                  ZenithColor.INSTANCE.method7294(previewAlpha * 0.75F)
               );
            }

            Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
            String phaseLabel = this.phaseLabel(selected);
            menuDrawContext.drawText(font, phaseLabel, floatValue + (width - font.width(phaseLabel)) / 2.0F, y2 + width2 + 6.0F, this.phaseColor(selected, zenithStyle).method7294(previewAlpha));
            Font font2 = Fonts.NEW_REGULAR.getFont(4.6F);
            String text = t(this.isOnline(selected) ? "gui.tools.bots.hint.wait" : "gui.tools.bots.hint.connect");
            menuDrawContext.drawText(
               font2,
               text,
               floatValue + (width - font2.width(text)) / 2.0F,
               y2 + width2 + 6.0F + font.height() + 4.0F,
               zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F)
            );
         } else if (!this.previewHasFrame) {
            Font font = Fonts.NEW_REGULAR.getFont(4.8F);
            String text = t("gui.bot.loadingWorld");
            menuDrawContext.drawText(
               font,
               text,
               floatValue + (width - font.width(text)) / 2.0F,
               y + height / 2.0F - font.height() / 2.0F,
               zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         }

         if (previewHovered) {
            String key = joined ? "gui.tools.bots.view.desc" : (this.isOnline(selected) ? "gui.tools.bots.disconnect.desc" : "gui.tools.bots.connect.desc");
            HoverInfo.request(selected, t(key));
         }

         this.previewRect = joined ? boundsData : null;
         this.previewAlpha = previewAlpha;
         this.previewHovered = previewHovered;
      }
   }

   @Override
   public void renderPriority(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3) {
      BoundsData previewRect = this.previewRect;
      this.previewRect = null;
      String previewOwner = this.previewOwner;
      BotClient previewClient = previewOwner == null ? null : HeadlessBots.get(previewOwner);
      if (previewClient == null || !previewClient.isJoined()) {
         this.releasePreview();
         this.previewHasFrame = false;
      } else if (previewRect != null && !(this.previewAlpha < 0.9F) && !(f < 0.9F)) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            if (this.previewClient != previewClient) {
               this.releasePreview();
               this.previewClient = previewClient;
               this.previewView = new BotWorldView(previewClient);
            }

            MsdfRenderer.flushBatch();
            menuDrawContext.draw();
            double d = mc.getWindow().getGuiScale() * Math.max(0.5F, Zenith.getInstance().getNLMenuScreen().getGuiScale());
            int width = Mth.clamp((int)(previewRect.width() * d), 64, 960);
            int height = Mth.clamp((int)(previewRect.height() * d), 64, 960);
            this.previewHasFrame = this.previewView.renderToFbo(width, height);
            if (this.previewHasFrame) {
               BoundsData asideViewport = this.asideViewport();
               boolean flag = asideViewport != null;
               if (flag) {
                  menuDrawContext.enableScissor(
                     asideViewport.getFloatValue(), asideViewport.getFloatValue2(), asideViewport.getFloatValue() + asideViewport.width(), asideViewport.getFloatValue2() + asideViewport.height()
                  );
               }

               RenderShaders.method5298(
                  MatrixUtils.method664(menuDrawContext.pose()),
                  this.previewView.getColorAttachment(),
                  previewRect.getFloatValue(),
                  previewRect.getFloatValue2(),
                  previewRect.width(),
                  previewRect.height(),
                  ProxyDotRadiusData.method8069(6.0F),
                  ZenithColor.INSTANCE.method7294(this.previewAlpha),
                  0.0F,
                  1.0F,
                  1.0F,
                  0.0F
               );
               this.renderPreviewOverlay(menuDrawContext, previewOwner, previewClient, previewRect, currentStyle);
               if (flag) {
                  MsdfRenderer.flushBatch();
                  menuDrawContext.disableScissor();
               }
            }
         }
      }
   }

   private void renderPreviewOverlay(MenuDrawContext menuDrawContext, String previewOwner, BotClient previewClient, BoundsData previewRect, ZenithStyle currentStyle) {
      float height = 15.0F;
      menuDrawContext.drawRoundedRect(
         previewRect.getFloatValue(),
         previewRect.getFloatValue2() + previewRect.height() - height,
         previewRect.width(),
         height,
         ProxyDotRadiusData.method8077(6.0F),
         new ZenithColor(10, 10, 14, 195).method7294(this.previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      float centeredTextY = previewRect.getFloatValue2() + previewRect.height() - height / 2.0F - font.height() / 2.0F;
      BotPlayer player = previewClient.getPlayer();
      String text = player == null ? "" : String.format(Locale.ROOT, "%.0f HP", player.getHealth());
      float width = font.width(text);
      menuDrawContext.drawText(font, fit(font, previewOwner, previewRect.width() - 16.0F - width), previewRect.getFloatValue() + 6.0F, centeredTextY, ZenithColor.INSTANCE.method7294(this.previewAlpha));
      if (!text.isEmpty()) {
         menuDrawContext.drawText(
            font, text, previewRect.getFloatValue() + previewRect.width() - 6.0F - width, centeredTextY, currentStyle.getHeartIcon().getZenithColor().method7294(this.previewAlpha)
         );
      }

      if (this.previewHovered) {
         menuDrawContext.drawRoundedRect(
            previewRect.getFloatValue(),
            previewRect.getFloatValue2(),
            previewRect.width(),
            previewRect.height(),
            ProxyDotRadiusData.method8069(6.0F),
            new ZenithColor(10, 10, 14, 120).method7294(this.previewAlpha)
         );
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
         String str = t("gui.tools.bots.hint.control");
         menuDrawContext.drawText(
            font2,
            str,
            previewRect.getFloatValue() + (previewRect.width() - font2.width(str)) / 2.0F,
            previewRect.getFloatValue2() + previewRect.height() / 2.0F - font2.height() / 2.0F,
            ZenithColor.INSTANCE.method7294(this.previewAlpha)
         );
      }
   }

   private void openPreview(String selected) {
      if (this.isJoined(selected)) {
         mc.gui.setScreen(new BotControlScreen(selected));
      } else {
         this.toggleConnection(selected);
         this.invalidate();
      }
   }

   private void releasePreview() {
      if (this.previewView != null) {
         this.previewView.close();
         this.previewView = null;
      }

      this.previewClient = null;
   }

   private void drawPill(MenuDrawContext menuDrawContext, float floatValue, float y, float width, String phaseLabel, ZenithColor phaseColor, float previewAlpha) {
      float height = 9.0F;
      menuDrawContext.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(height / 2.0F), phaseColor.method7293(34).method7294(previewAlpha));
      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      menuDrawContext.drawText(font, phaseLabel, floatValue + (width - font.width(phaseLabel)) / 2.0F, y + (height - font.height()) / 2.0F, phaseColor.method7294(previewAlpha));
   }

   private void drawSwitch(MenuDrawContext menuDrawContext, float floatValue, float y, float width, float height, boolean moduleEnabled, float previewAlpha, ZenithStyle zenithStyle) {
      menuDrawContext.drawRect(floatValue, y, width, height, zenithStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 3.0F));
      menuDrawContext.drawRoundedBorder(floatValue, y, width, height, 0.1F, ProxyDotRadiusData.INSTANCE, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 4.0F));
      float f = 3.0F;
      float floatValue2 = moduleEnabled ? floatValue + width - f - 1.0F : floatValue + 1.0F;
      menuDrawContext.drawRect(floatValue2, y + 1.0F, f, height - 2.0F, (moduleEnabled ? zenithStyle.getPrimaryColor() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha));
   }

   private void renderEmptyPreview(MenuDrawContext menuDrawContext, float floatValue, float y, float width, float height, float previewAlpha, ZenithStyle zenithStyle) {
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      float f = (float)(System.currentTimeMillis() % 2600L) / 2600.0F;
      float y2 = y + 4.0F + (height - 8.0F) * f;
      float f2 = (float)Math.sin(f * Math.PI);
      menuDrawContext.drawRect(floatValue + 6.0F, y2, width - 12.0F, 0.6F, zenithColor.method7292(0.35F * f2).method7294(previewAlpha));
      menuDrawContext.drawRect(floatValue + 6.0F, y2 - 6.0F, width - 12.0F, 6.0F, zenithColor.method7292(0.04F * f2).method7294(previewAlpha));
      float width2 = 7.0F;
      ZenithColor zenithColor2 = zenithStyle.getTextTertiary().getZenithColor().method7292(0.5F).method7294(previewAlpha);
      float[][] floatses = new float[][]{
         {floatValue + 6.0F, y + 6.0F, 1.0F, 1.0F},
         {floatValue + width - 6.0F, y + 6.0F, -1.0F, 1.0F},
         {floatValue + 6.0F, y + height - 6.0F, 1.0F, -1.0F},
         {floatValue + width - 6.0F, y + height - 6.0F, -1.0F, -1.0F}
      };

      for (float[] floats : floatses) {
         float floatValue2 = floats[2] > 0.0F ? floats[0] : floats[0] - width2;
         float y3 = floats[3] > 0.0F ? floats[1] : floats[1] - 0.6F;
         menuDrawContext.drawRect(floatValue2, y3, width2, 0.6F, zenithColor2);
         float floatValue3 = floats[2] > 0.0F ? floats[0] : floats[0] - 0.6F;
         float y4 = floats[3] > 0.0F ? floats[1] : floats[1] - width2;
         menuDrawContext.drawRect(floatValue3, y4, 0.6F, width2, zenithColor2);
      }

      float width3 = 26.0F;
      float f3 = (float)Math.sin(System.currentTimeMillis() / 650.0) * 1.3F;
      float floatValue2 = floatValue + (width - width3) / 2.0F;
      float y3 = y + height / 2.0F - width3 / 2.0F - 10.0F + f3;
      ToolTab.tile(menuDrawContext, zenithStyle, floatValue2, y3, width3, zenithColor, 0.55F, previewAlpha);
      ToolTab.bigGlyph(menuDrawContext, MenuGlyph.BOTS, floatValue2 + width3 / 2.0F, y3 + width3 / 2.0F, 2.0F, zenithColor.method7294(previewAlpha));
      Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
      String text = t("gui.tools.bots.preview.title");
      float centeredTextY = y3 + width3 + 7.0F - f3;
      menuDrawContext.drawText(font, text, floatValue + (width - font.width(text)) / 2.0F, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      centeredTextY += font.height() + 3.0F;

      for (String str : wrapText(t("gui.tools.bots.preview.hint"), font2, width - 24.0F, 2)) {
         menuDrawContext.drawText(font2, str, floatValue + (width - font2.width(str)) / 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY += font2.height() + 2.0F;
      }
   }

   private void renderEmptyList(MenuDrawContext menuDrawContext, int n, int n2, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      float floatValue = f + 8.0F;
      float width = f3 - 16.0F;
      ToolTab.card(menuDrawContext, zenithStyle, floatValue, f2 + 4.0F, width, 62.0F, 0.0F, previewAlpha);
      float width2 = 22.0F;
      float f4 = (float)Math.sin(System.currentTimeMillis() / 650.0) * 1.2F;
      float floatValue2 = floatValue + 10.0F;
      float y = f2 + 4.0F + (62.0F - width2) / 2.0F + f4;
      ToolTab.tile(menuDrawContext, zenithStyle, floatValue2, y, width2, zenithColor, 0.55F, previewAlpha);
      ToolTab.bigGlyph(menuDrawContext, MenuGlyph.BOTS, floatValue2 + width2 / 2.0F, y + width2 / 2.0F, 1.6F, zenithColor.method7294(previewAlpha));
      Font font = Fonts.NEW_SEMIBOLD.getFont(5.8F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
      float floatValue3 = floatValue2 + width2 + 10.0F;
      float shownWidth = floatValue + width - 8.0F - floatValue3;
      List<String> strings = wrapText(t("gui.tools.bots.empty"), font2, shownWidth, 3);
      float f5 = font.height() + 4.0F + strings.size() * (font2.height() + 2.0F);
      float centeredTextY = f2 + 4.0F + (62.0F - f5) / 2.0F;
      menuDrawContext.drawText(font, font.clip(t("gui.tools.bots.emptyTitle"), shownWidth), floatValue3, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      centeredTextY += font.height() + 4.0F;

      for (String text : strings) {
         menuDrawContext.drawText(font2, text, floatValue3, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY += font2.height() + 2.0F;
      }

      boolean flag = this.addressInput != null && !this.addressInput.getText().isBlank();
      boolean flag2 = this.nameInput != null && !this.nameInput.getText().isBlank();
      String[] strings2 = new String[]{t("gui.tools.bots.step.address"), t("gui.tools.bots.step.name"), t("gui.tools.bots.step.add")};
      boolean[] flags = new boolean[]{flag, flag2, false};
      int n3 = !flag ? 0 : (!flag2 ? 1 : 2);
      float f6 = f2 + 72.0F;
      Font font3 = Fonts.NEW_MEDIUM.getFont(4.7F);
      Font font4 = Fonts.NEW_MEDIUM.getFont(4.3F);

      for (int i = 0; i < strings2.length; i++) {
         float y2 = f6 + i * 18.0F;
         boolean flag3 = i == n3;
         float pulse = flag3 ? ToolTab.pulse(0.8F) : 0.0F;
         ZenithColor zenithColor2 = flags[i] ? ToolTab.OK : (flag3 ? zenithColor : zenithStyle.getTextTertiary().getZenithColor());
         ToolTab.card(menuDrawContext, zenithStyle, floatValue, y2, width, 15.0F, flag3 ? 0.4F : 0.0F, previewAlpha);
         float width3 = 10.0F;
         float floatValue4 = floatValue + 4.0F;
         float y3 = y2 + 2.5F;
         menuDrawContext.drawRoundedRect(floatValue4, y3, width3, width3, ProxyDotRadiusData.method8069(width3 / 2.0F), zenithColor2.method7292(0.18F + 0.12F * pulse).method7294(previewAlpha));
         String text = flags[i] ? "✓" : String.valueOf(i + 1);
         if (flags[i]) {
            MenuGlyph.CHECK.draw(menuDrawContext, floatValue4 + (width3 - 6.0F) / 2.0F, y3 + (width3 - 6.0F) / 2.0F, zenithColor2.method7294(previewAlpha));
         } else {
            menuDrawContext.drawText(font4, text, floatValue4 + (width3 - font4.width(text)) / 2.0F, y3 + (width3 - font4.height()) / 2.0F, zenithColor2.method7294(previewAlpha));
         }

         menuDrawContext.drawText(
            font3,
            font3.clip(strings2[i], width - 22.0F),
            floatValue4 + width3 + 6.0F,
            y2 + (15.0F - font3.height()) / 2.0F,
            (flags[i] ? zenithStyle.getTextTertiary() : (flag3 ? zenithStyle.getTextEnable() : zenithStyle.getTextSecondary())).getZenithColor().method7294(previewAlpha)
         );
      }

      float f7 = f6 + strings2.length * 18.0F + 6.0F;

      for (int i = 0; i < 3; i++) {
         float f8 = f7 + i * 22.0F;
         float f9 = previewAlpha * (0.55F - i * 0.15F);
         ToolTab.skeleton(menuDrawContext, zenithStyle, floatValue, f8 + 4.0F, 13.0F, 13.0F, i, f9);
         ToolTab.skeleton(menuDrawContext, zenithStyle, floatValue + 13.0F + 6.0F, f8 + 10.5F - 2.0F, width * (0.35F - i * 0.05F), 4.0F, i + 1, f9);
         ToolTab.skeleton(menuDrawContext, zenithStyle, floatValue + width - 14.0F, f8 + 3.5F, 14.0F, 14.0F, i + 2, f9);
         ToolTab.skeleton(menuDrawContext, zenithStyle, floatValue + width - 14.0F - 34.0F, f8 + 6.5F, 28.0F, 8.0F, i + 3, f9);
      }
   }

   private void drawHint(MenuDrawContext menuDrawContext, float f, float f2, float f3, String description, float previewAlpha, ZenithStyle zenithStyle) {
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      float centeredTextY = f2;

      for (String text : wrapText(description, font, f3 - 32.0F, 3)) {
         menuDrawContext.drawText(font, text, f + (f3 - font.width(text)) / 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY += font.height() + 2.0F;
      }
   }

   private static void drawPlay(MenuDrawContext menuDrawContext, float f, float centeredTextY, ZenithColor zenithColor) {
      SvgIcon.PLAY.draw(menuDrawContext, f - 1.5F, centeredTextY, 6.0F, zenithColor);
   }

   private static void drawStop(MenuDrawContext menuDrawContext, float f, float f2, ZenithColor zenithColor) {
      SvgIcon.STOP.draw(menuDrawContext, f - 0.5F, f2 - 0.5F, 6.0F, zenithColor);
   }

   private boolean insideMain(double lastClickX, double lastClickY) {
      BoundsData mainViewport = this.mainViewport();
      return mainViewport == null || mainViewport.contains(lastClickX, lastClickY);
   }

   private boolean insideAside(double lastClickX, double lastClickY) {
      BoundsData asideViewport = this.asideViewport();
      return asideViewport == null || asideViewport.contains(lastClickX, lastClickY);
   }

   private static String displayModuleName(String supportedModuleName) {
      return supportedModuleName.startsWith("Bot") && supportedModuleName.length() > 3 ? supportedModuleName.substring(3) : supportedModuleName;
   }

   private String moduleDescription(String supportedModuleName) {
      String supportedModuleDescription = BotModuleManager.supportedModuleDescription(supportedModuleName);
      return supportedModuleDescription != null && !supportedModuleDescription.isBlank() ? supportedModuleDescription : t("gui.tools.bots.module.desc");
   }

   private boolean isModuleEnabled(String selected, String supportedModuleName) {
      BotClient botClient = HeadlessBots.get(selected);
      if (botClient != null) {
         BotModule botModule = botClient.getModules().getModule(supportedModuleName);
         if (botModule != null) {
            return botModule.isEnabled();
         }
      }

      return HeadlessBots.isModuleEnabledInProfile(selected, supportedModuleName);
   }

   private boolean isOnline(String selected) {
      BotClient botClient = HeadlessBots.get(selected);
      return botClient != null && botClient.getPhase() != BotPhase.DISCONNECTED;
   }

   private boolean isJoined(String selected) {
      BotClient botClient = HeadlessBots.get(selected);
      return botClient != null && botClient.isJoined();
   }

   private int onlineCount() {
      int n = 0;

      for (BotClient botClient : HeadlessBots.all()) {
         if (botClient.getPhase() != BotPhase.DISCONNECTED) {
            n++;
         }
      }

      return n;
   }

   private ZenithColor phaseColor(String selected, ZenithStyle zenithStyle) {
      BotClient botClient = HeadlessBots.get(selected);
      BotPhase phase = botClient == null ? BotPhase.DISCONNECTED : botClient.getPhase();

      return switch (phase) {
         case PLAY -> zenithStyle.getFriendColor().getZenithColor();
         case DISCONNECTED -> zenithStyle.getTextTertiary().getZenithColor();
         default -> zenithStyle.getPrimaryColor().getZenithColor();
      };
   }

   private String phaseLabel(String selected) {
      BotClient botClient = HeadlessBots.get(selected);
      if (botClient == null) {
         return t("gui.tools.bots.phase.offline");
      } else {
         return switch (botClient.getPhase()) {
            case PLAY -> t("gui.tools.bots.phase.play");
            case DISCONNECTED -> t("gui.tools.bots.phase.offline");
            case CONNECTING -> t("gui.tools.bots.phase.connecting");
            case LOGIN -> t("gui.tools.bots.phase.login");
            case CONFIGURATION -> t("gui.tools.bots.phase.config");
         };
      }
   }

   private void toggleConnection(String selected) {
      BotClient botClient = HeadlessBots.get(selected);
      if (botClient != null && botClient.getPhase() != BotPhase.DISCONNECTED) {
         HeadlessBots.disconnect(selected);
      } else {
         String[] address = this.resolveAddress();
         if (address != null) {
            HeadlessBots.connect(selected, address[0], Integer.parseInt(address[1]));
         }
      }
   }

   private void connectAll() {
      String[] address = this.resolveAddress();
      if (address != null) {
         HeadlessBots.connectAll(HeadlessBots.allNames(), address[0], Integer.parseInt(address[1]));
         this.invalidate();
      }
   }

   private String[] resolveAddress() {
      String lastAddress = this.addressInput == null ? HeadlessBots.getLastAddress() : this.addressInput.getText();
      if (lastAddress != null && !lastAddress.isBlank()) {
         lastAddress = lastAddress.trim();
         int lastIndexOf = lastAddress.lastIndexOf(58);
         if (lastIndexOf < 0) {
            return new String[]{lastAddress, String.valueOf(25565)};
         } else {
            String str = lastAddress.substring(0, lastIndexOf);
            String str2 = lastAddress.substring(lastIndexOf + 1);

            try {
               int n = Integer.parseInt(str2);
               if (n > 0 && n <= 65535) {
                  return new String[]{str, String.valueOf(n)};
               } else {
                  throw new NumberFormatException(str2);
               }
            } catch (NumberFormatException e) {
               ChatLogger.method2021(t("gui.tools.bots.address.badPort"));
               return null;
            }
         }
      } else {
         ChatLogger.method2021(t("gui.tools.bots.address.missing"));
         return null;
      }
   }

   private void addBot() {
      if (this.nameInput != null) {
         String text = this.nameInput.getText();
         if (text != null && !text.isBlank()) {
            if (!HeadlessBots.add(text.trim())) {
               ChatLogger.method2021(t("gui.tools.bots.add.exists"));
            } else {
               this.selected = text.trim();
               this.nameInput.setText("");
               this.nameInput.setCursor(0);
               this.invalidate();
               this.scrollToEnd();
            }
         }
      }
   }

   @Override
   public float footerHeight() {
      return 34.0F;
   }

   @Override
   public void renderFooter(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float f, float width, float previewAlpha, ZenithStyle zenithStyle) {
      if (this.nameInput == null) {
         this.nameInput = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.tools.bots.add.hint"), 16.0F);
         this.nameInput.setType(SearchBox.Type.ENGLISH_NAME);
         this.nameInput.setType2(SearchBox.Type2.ONLY_ONE_WORLD);
      }

      menuDrawContext.drawRect(floatValue + 8.0F, f - GuiStyle.PADDING.intValue(), width - 16.0F, 1.0F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
      float y = f + 8.0F;
      float width2 = 26.0F;
      float width3 = width - 16.0F - width2 - GuiStyle.PADDING.intValue();
      menuDrawContext.drawRoundedRect(
         floatValue + 8.0F,
         y,
         width3,
         18.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
      );
      boolean contains = this.addButtonBounds(floatValue, y, width, width2).contains(lastClickX, lastClickY);
      menuDrawContext.drawRoundedRect(
         floatValue + width - 8.0F - width2,
         y,
         width2,
         18.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         zenithStyle.getPrimaryColor().getZenithColor().method7293(contains ? 62 : 30).method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      boolean selected = this.nameInput.isSelected();
      MenuGlyph.PROFILE.draw(menuDrawContext, floatValue + 8.0F + 5.0F, y + 6.0F, (selected ? zenithStyle.getTextEnable() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha));
      this.nameInput.setFont(font);
      this.nameInput.setWidth(width3 - 20.0F);
      this.nameInput
         .method987(
            menuDrawContext,
            floatValue + 8.0F + 15.0F,
            y + (18.0F - font.height()) / 2.0F,
            zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      float f2 = floatValue + width - 8.0F - width2 / 2.0F;
      float f3 = y + 9.0F;
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor().method7293(contains ? 255 : 215).method7294(previewAlpha);
      menuDrawContext.drawRect(f2 - 3.0F, f3 - 0.5F, 6.0F, 1.0F, zenithColor);
      menuDrawContext.drawRect(f2 - 0.5F, f3 - 3.0F, 1.0F, 6.0F, zenithColor);
      if (contains) {
         HoverInfo.request(t("gui.tools.bots.caption.list"), t("gui.tools.bots.add.hint"));
      }
   }

   private BoundsData addButtonBounds(float floatValue, float y, float width, float f) {
      return new BoundsData(floatValue + width - 8.0F - f, y, f, 18.0F);
   }

   @Override
   public boolean onFooterClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      BoundsData footerBounds = this.footerBounds();
      if (footerBounds == null) {
         return false;
      } else {
         float y = footerBounds.getFloatValue2() + 8.0F;
         if (this.addButtonBounds(footerBounds.getFloatValue(), y, footerBounds.width(), 26.0F).contains(lastClickX, lastClickY)) {
            this.addBot();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else {
            if (this.nameInput != null) {
               if (this.addressInput != null) {
                  this.addressInput.setSelected(false);
               }

               this.nameInput.setSelected(true);
            }

            return true;
         }
      }
   }

   private void drawInput(MenuDrawContext menuDrawContext, SearchBox addressInput, MenuGlyph menuGlyph, float floatValue, float y, float width, float previewAlpha, ZenithStyle zenithStyle) {
      menuDrawContext.drawRoundedRect(
         floatValue + 8.0F,
         y,
         width - 16.0F,
         18.0F,
         ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
         zenithStyle.getPanelLeftBackground().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), addressInput.isSelected() ? 0.18F : 0.0F).method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      menuGlyph.draw(menuDrawContext, floatValue + 8.0F + 5.0F, y + 6.0F, (addressInput.isSelected() ? zenithStyle.getTextEnable() : zenithStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha));
      addressInput.setFont(font);
      addressInput.setWidth(width - 16.0F - 20.0F);
      addressInput.method987(
         menuDrawContext,
         floatValue + 8.0F + 15.0F,
         y + (18.0F - font.height()) / 2.0F,
         zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
         zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float panelAlpha, float f, float y) {
      this.powerBounds.clear();
      this.connectAllBounds = null;
      this.disconnectAllBounds = null;
      super.render(menuDrawContext, lastClickX, lastClickY, panelAlpha, f, y);
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      this.lastClickX = lastClickX;
      this.lastClickY = lastClickY;
      if (enum2 == Enum3.LEFT && this.insideMain(lastClickX, lastClickY)) {
         if (this.connectAllBounds != null && this.connectAllBounds.contains(lastClickX, lastClickY)) {
            this.connectAll();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         }

         if (this.disconnectAllBounds != null && this.disconnectAllBounds.contains(lastClickX, lastClickY)) {
            HeadlessBots.disconnectAll();
            Zenith.getInstance().getClientSounds().method1570(false);
            this.invalidate();
            return true;
         }

         for (Map.Entry entry : this.powerBounds.entrySet()) {
            if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
               this.toggleConnection((String)entry.getKey());
               Zenith.getInstance().getClientSounds().method1570(false);
               this.invalidate();
               return true;
            }
         }
      }

      if (this.addressInput != null && this.insideMain(lastClickX, lastClickY) && this.addressInput.onMouseClicked(lastClickX, lastClickY, enum2)) {
         if (this.nameInput != null) {
            this.nameInput.setSelected(false);
         }

         return true;
      } else {
         return super.onMouseClicked(lastClickX, lastClickY, enum2);
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      SearchBox focusedInput = this.focusedInput();
      if (focusedInput != null) {
         if (keyCode == 256) {
            focusedInput.setSelected(false);
            return true;
         }

         if (keyCode == 257 || keyCode == 335) {
            if (focusedInput == this.nameInput) {
               this.addBot();
            } else {
               HeadlessBots.setLastAddress(focusedInput.getText());
               focusedInput.setSelected(false);
            }

            return true;
         }

         if (focusedInput.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         }
      }

      return super.keyPressed(keyCode, keycode, modifiers);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      SearchBox focusedInput = this.focusedInput();
      return focusedInput != null && focusedInput.charTyped(codepoint, n) ? true : super.charTyped(codepoint, n);
   }

   private SearchBox focusedInput() {
      if (this.nameInput != null && this.nameInput.isSelected()) {
         return this.nameInput;
      } else {
         return this.addressInput != null && this.addressInput.isSelected() ? this.addressInput : null;
      }
   }

   @Override
   public void tick() {
      String contentSignature = String.join(",", HeadlessBots.allNames())
         + "|"
         + this.onlineCount()
         + "|"
         + this.selected
         + "|"
         + (this.selected == null ? "" : this.phaseLabel(this.selected));
      if (!contentSignature.equals(this.contentSignature)) {
         this.contentSignature = contentSignature;
         this.invalidate();
      }
   }

   @Override
   public void close() {
      this.releasePreview();
   }

   private static final class CardAnimations {
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private final Animation select = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private final Animation power = new Animation(120L, 0.0F, Easing.INSTANCE12);
   }
}

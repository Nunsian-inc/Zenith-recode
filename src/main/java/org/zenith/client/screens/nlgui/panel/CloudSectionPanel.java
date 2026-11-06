package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.List;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.CloudDmClient;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CloudSectionPanel extends ElementPanel {
   private static final int TAB_FRIENDS = 0;
   private static final int TAB_CHAT = 1;
   private static final int TABS = 7;
   private static final float SWITCH_SHIFT = 14.0F;
   private final List<SectionPanel> children = new ArrayList<>();
   private final List<String> names = new ArrayList<>();
   private final List<BoundsData> tabBounds = new ArrayList<>();
   private final List<MenuGlyph> icons = new ArrayList<>();
   private int active;
   private final Animation plateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private final Animation switchAnimation = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private float switchDirection = 1.0F;
   private final int[] badgeCache = new int[7];
   private long badgeStamp;
   private static final long BADGE_REFRESH_MS = 250L;
   private final Animation[] badgeShow = new Animation[7];
   private FriendsSectionPanel friendsPanel;
   private final int[] badgeLast = new int[7];
   private final Animation[] tabHover = new Animation[7];

   public CloudSectionPanel(FriendsSectionPanel friendsPanel, CloudChatSectionPanel cloudChatSectionPanel, VoiceSectionPanel voiceSectionPanel) {
      this.friendsPanel = friendsPanel;
      this.add("gui.cloud.tab.friends", MenuGlyph.FRIENDS, friendsPanel);
      friendsPanel.openChatWith(var2x -> {
         if (!cloudChatSectionPanel.openDirectWith(var2x)) {
            return false;
         } else {
            this.open(1);
            return true;
         }
      });
      this.add("gui.cloud.tab.chat", MenuGlyph.CHAT, cloudChatSectionPanel);
      this.add("gui.cloud.tab.voice", MenuGlyph.MIC, voiceSectionPanel);
   }

   private void add(String str, MenuGlyph menuGlyph, SectionPanel friendsPanel) {
      friendsPanel.setEmbedded(true);
      this.badgeShow[this.names.size()] = new Animation(220L, 0.0F, Easing.INSTANCE12);
      this.tabHover[this.names.size()] = new Animation(130L, 0.0F, Easing.INSTANCE12);
      this.names.add(str);
      this.icons.add(menuGlyph);
      this.children.add(friendsPanel);
   }

   private void switchTo(int active) {
      if (active != this.active && active >= 0 && active < this.children.size()) {
         this.switchDirection = active > this.active ? 1.0F : -1.0F;
         this.active = active;
         this.switchAnimation.reset(0.0F);
         this.switchAnimation.setTarget(1.0F);
         if (this.switchDirection > 0.0F) {
            Zenith.getInstance().getClientSounds().method1562();
         } else {
            Zenith.getInstance().getClientSounds().method1563();
         }
      }
   }

   private int badge(int n) {
      long now = System.currentTimeMillis();
      if (now - this.badgeStamp > 250L) {
         this.badgeStamp = now;
         this.badgeCache[0] = this.computeBadge(0);
         this.badgeCache[1] = this.computeBadge(1);
      }

      return n >= 0 && n < this.badgeCache.length ? this.badgeCache[n] : 0;
   }

   private int computeBadge(int n) {
      if (n == 1) {
         return CloudDmClient.getInstance().method4869();
      } else {
         CloudConnectionState cloudConnectionState = state();
         if (cloudConnectionState == null || !cloudConnectionState.method8035()) {
            return 0;
         } else if (n == 0) {
            Cloud cloud = Zenith.getInstance().getCloud();
            return cloudConnectionState.method8055().size() + (cloud == null ? 0 : cloud.getPartyMember().method1147());
         } else {
            return 0;
         }
      }
   }

   private static CloudConnectionState state() {
      Cloud cloud = Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
      return cloud == null ? null : cloud.state();
   }

   private static String t(String name) {
      return Zenith.getInstance().getLocalization().translate(name);
   }

   private SectionPanel current() {
      return this.children.get(this.active);
   }

   public void open(int active) {
      this.switchTo(active);
   }

   public void openFriendsAdd(String string) {
      this.open(0);
      if (this.friendsPanel != null) {
         this.friendsPanel.devOpenAdd(string);
      }
   }

   @Override
   public float getHeaderTitleWidth() {
      return 19.0F + Fonts.NEW_MEDIUM.getFont(5.5F).width("Cloud");
   }

   @Override
   public float getContentWidth() {
      return 380.0F;
   }

   @Override
   public boolean isHeaderSearchOnly() {
      return false;
   }

   @Override
   public void renderHeader(MenuDrawContext menuDrawContext, int n, int n2, float previewAlpha, float f, float f2, float f3) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         float floatValue = f + 8.0F;
         MenuGlyph.CLOUD.draw(menuDrawContext, floatValue, f2 + 8.5F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawText(font, "Cloud", floatValue + 11.0F, f2 + (23.0F - font.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      }
   }

   @Override
   public float getButtonWidth() {
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      float f = 0.0F;

      for (int i = 0; i < this.names.size(); i++) {
         f += this.tabWidth(font, i);
      }

      return f;
   }

   private float tabWidth(Font font, int n) {
      float f = font.width(t(this.names.get(n))) + 16.0F + 9.0F;
      float badgeShow = this.badgeShow(n);
      if (badgeShow > 0.001F) {
         f += (badgeWidth(this.badgeLast[n]) + 4.0F) * badgeShow;
      }

      return f;
   }

   private float badgeShow(int n) {
      if (n >= 0 && n < this.badgeShow.length && this.badgeShow[n] != null) {
         int badge = this.badge(n);
         if (badge > 0) {
            this.badgeLast[n] = badge;
         }

         return this.badgeShow[n].animateTo(badge > 0 ? 1.0F : 0.0F);
      } else {
         return 0.0F;
      }
   }

   private static float badgeWidth(int n) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      return Math.max(9.0F, font.width(badgeText(n)) + 6.0F);
   }

   private static String badgeText(int n) {
      return n > 99 ? "99+" : String.valueOf(n);
   }

   @Override
   public void renderHeaderButtons(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float previewAlpha, float f, float y, float f2, float f3) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
         this.tabBounds.clear();
         float floatValue = f;
         float floatValue2 = f;
         float tabWidth = 0.0F;

         for (int i = 0; i < this.names.size(); i++) {
            float width = this.tabWidth(font, i);
            this.tabBounds.add(new BoundsData(floatValue, y, width, 23.0F));
            if (i == this.active) {
               floatValue2 = floatValue;
               tabWidth = width;
            }

            floatValue += width;
         }

         if (!this.plateReady) {
            this.plateX.snapTo(floatValue2);
            this.plateWidth.snapTo(tabWidth);
            this.plateReady = true;
         }

         float f4 = this.plateX.animateTo(floatValue2);
         float f5 = this.plateWidth.animateTo(tabWidth);
         float height = 15.0F;
         menuDrawContext.drawRoundedRect(
            f4 + 2.0F,
            y + 4.0F,
            Math.max(0.0F, f5 - 4.0F),
            height,
            ProxyDotRadiusData.method8069(height / 2.0F),
            currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha)
         );

         for (int i = 0; i < this.names.size(); i++) {
            BoundsData tabBound = this.tabBounds.get(i);
            boolean flag = i == this.active;
            float f6 = this.tabHover[i].animateTo(!flag && tabBound.contains(lastClickX, lastClickY) ? 1.0F : 0.0F);
            ZenithColor zenithColor = (flag ? currentStyle.getTextEnable() : currentStyle.getTextTertiary())
               .getZenithColor()
               .method7295(currentStyle.getTextEnable().getZenithColor(), f6 * 0.5F)
               .method7294(previewAlpha);
            float floatValue3 = tabBound.getFloatValue() + 8.0F;
            this.icons.get(i).draw(menuDrawContext, floatValue3, y + 8.5F, zenithColor);
            floatValue3 += 9.0F;
            String text = t(this.names.get(i));
            menuDrawContext.drawText(font, text, floatValue3, y + (23.0F - font.height()) / 2.0F, zenithColor);
            float badgeShow = this.badgeShow(i);
            if (badgeShow > 0.01F) {
               this.drawBadge(menuDrawContext, floatValue3 + font.width(text) + 4.0F, y, this.badgeLast[i], badgeShow, previewAlpha, currentStyle);
            }
         }
      }
   }

   private void drawBadge(MenuDrawContext menuDrawContext, float f, float y, int n, float badgeShow, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      String text = badgeText(n);
      float width = badgeWidth(n);
      float height = 7.0F;
      float f2 = y + (23.0F - height) / 2.0F;
      float x = 0.5F + 0.5F * badgeShow;
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(f + width / 2.0F, f2 + height / 2.0F);
      menuDrawContext.pose().scale(x, x);
      menuDrawContext.drawRoundedRect(
         -width / 2.0F,
         -height / 2.0F,
         width,
         height,
         ProxyDotRadiusData.method8069(height / 2.0F),
         currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * badgeShow)
      );
      menuDrawContext.drawText(font, text, -font.width(text) / 2.0F, -font.height() / 2.0F, new ZenithColor(255, 255, 255).method7294(previewAlpha * badgeShow));
      menuDrawContext.popMatrix();
   }

   @Override
   public boolean onHeaderButtonsClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      for (int i = 0; i < this.tabBounds.size(); i++) {
         if (this.tabBounds.get(i).contains(lastClickX, lastClickY)) {
            this.switchTo(i);
            return true;
         }
      }

      return false;
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float y) {
      float f3 = this.switchAnimation.animateTo(1.0F);
      float x = 14.0F * this.switchDirection * (1.0F - f3);
      menuDrawContext.pose().pushMatrix();
      menuDrawContext.pose().translate(x, 0.0F);
      this.current().render(menuDrawContext, lastClickX, lastClickY, f * f3, f2, y);
      menuDrawContext.pose().popMatrix();
   }

   @Override
   public void renderPriority(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float floatValue, float f2) {
      this.current().renderPriority(customDrawContext, mouseX, mouseY, f, floatValue, f2);
   }

   @Override
   public void tick() {
      this.current().tick();
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      return this.current().onMouseClicked(lastClickX, lastClickY, enum2);
   }

   @Override
   public boolean onMouseReleased(double x, double y, MouseButton enum2) {
      return this.current().onMouseReleased(x, y, enum2);
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int mouseToGlfw, double offsetX, double offsetY) {
      return this.current().onMouseDragged(mouseX, mouseY, mouseToGlfw, offsetX, offsetY);
   }

   @Override
   public boolean mouseScrolled(double lastClickX, double lastClickY, double d, double verticalAmount) {
      return this.current().mouseScrolled(lastClickX, lastClickY, d, verticalAmount);
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return this.current().keyPressed(keyCode, keycode, modifiers);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      return this.current().charTyped(codepoint, n);
   }

   @Override
   public List<? extends Element> getElements() {
      return this.current().getElements();
   }

   @Override
   public void close() {
      for (SectionPanel sectionPanel : this.children) {
         sectionPanel.close();
      }
   }
}

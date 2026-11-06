package org.zenith.client.screens.nlgui.panel.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.panel.SectionPanel;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ToolsSectionPanel extends ElementPanel {
   private static final float SWITCH_SHIFT = 14.0F;
   private static final float LIVE_GAP = 10.0F;
   private final List<SectionPanel> children = new ArrayList<>();
   private final List<String> names = new ArrayList<>();
   private final List<MenuGlyph> icons = new ArrayList<>();
   private final List<BooleanSupplier> liveStates = new ArrayList<>();
   private final List<BoundsData> tabBounds = new ArrayList<>();
   private int active;
   private final Animation plateX = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private final Animation switchAnimation = new Animation(200L, 1.0F, Easing.INSTANCE12);
   private float switchDirection = 1.0F;
   private final Animation liveAnimation = new Animation(320L, 0.0F, Easing.INSTANCE13);
   private boolean liveReady;
   private long lastFrame;

   public ToolsSectionPanel() {
      BotsToolPanel botsToolPanel = new BotsToolPanel();
      AutoSborToolPanel autoSborToolPanel = new AutoSborToolPanel();
      AutoCraftToolPanel autoCraftToolPanel = new AutoCraftToolPanel();
      this.add("gui.tools.tab.bots", MenuGlyph.BOTS, botsToolPanel, botsToolPanel::isLive);
      this.add("gui.tools.tab.inventory", MenuGlyph.STORAGE, autoSborToolPanel, autoSborToolPanel::isLive);
      this.add("gui.tools.tab.craft", MenuGlyph.CRAFT, autoCraftToolPanel, autoCraftToolPanel::isLive);
   }

   private void add(String str, MenuGlyph menuGlyph, SectionPanel botsToolPanel, BooleanSupplier booleanSupplier) {
      botsToolPanel.setEmbedded(false);
      this.names.add(str);
      this.icons.add(menuGlyph);
      this.children.add(botsToolPanel);
      this.liveStates.add(booleanSupplier);
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

   private void switchTo(int active) {
      if (active != this.active && active >= 0 && active < this.children.size()) {
         this.switchDirection = active > this.active ? 1.0F : -1.0F;
         this.active = active;
         if (this.current() instanceof ToolTab toolTab) {
            toolTab.shown();
         }

         this.switchAnimation.reset(0.0F);
         this.switchAnimation.setTarget(1.0F);
         if (this.switchDirection > 0.0F) {
            Zenith.getInstance().getClientSounds().method1562();
         } else {
            Zenith.getInstance().getClientSounds().method1563();
         }
      }
   }

   @Override
   public float getHeaderTitleWidth() {
      return 19.0F + Fonts.NEW_MEDIUM.getFont(5.5F).width("Tools");
   }

   @Override
   public float getContentWidth() {
      return 380.0F;
   }

   @Override
   public void renderHeader(MenuDrawContext menuDrawContext, int n, int n2, float previewAlpha, float f, float f2, float f3) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         float floatValue = f + 8.0F;
         MenuGlyph.TOOLS.draw(menuDrawContext, floatValue, f2 + 8.5F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawText(font, "Tools", floatValue + 11.0F, f2 + (23.0F - font.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      }
   }

   @Override
   public float getButtonWidth() {
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      float f = 0.0F;

      for (int i = 0; i < this.names.size(); i++) {
         f += this.tabWidth(font, i);
      }

      return f + 10.0F + this.liveWidth();
   }

   private float tabWidth(Font font, int n) {
      return font.width(t(this.names.get(n))) + 16.0F + 9.0F;
   }

   private float liveWidth() {
      return 9.0F + Fonts.NEW_REGULAR.getFont(4.6F).width(this.liveText());
   }

   private boolean isLive(int active) {
      BooleanSupplier liveState = this.liveStates.get(active);

      try {
         return liveState != null && liveState.getAsBoolean();
      } catch (Exception e) {
         return false;
      }
   }

   private String liveText() {
      return t(this.isLive(this.active) ? "gui.tools.state.running" : "gui.tools.state.idle");
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
            boolean contains = tabBound.contains(lastClickX, lastClickY);
            ZenithColor zenithColor = (flag ? currentStyle.getTextEnable() : currentStyle.getTextTertiary())
               .getZenithColor()
               .method7295(currentStyle.getTextEnable().getZenithColor(), contains && !flag ? 0.5F : 0.0F)
               .method7294(previewAlpha);
            float floatValue3 = tabBound.getFloatValue() + 8.0F;
            this.icons.get(i).draw(menuDrawContext, floatValue3, y + 8.5F, zenithColor);
            menuDrawContext.drawText(font, t(this.names.get(i)), floatValue3 + 9.0F, y + (23.0F - font.height()) / 2.0F, zenithColor);
            if (!flag && this.isLive(i)) {
               float width = 2.5F;
               menuDrawContext.drawRoundedRect(
                  tabBound.getFloatValue() + tabBound.width() - 6.0F,
                  y + 6.0F,
                  width,
                  width,
                  ProxyDotRadiusData.method8069(width / 2.0F),
                  currentStyle.getFriendColor().getZenithColor().method7294(previewAlpha)
               );
            }
         }

         this.drawLive(menuDrawContext, floatValue + 10.0F, y, previewAlpha, currentStyle);
      }
   }

   private void drawLive(MenuDrawContext menuDrawContext, float floatValue, float y, float previewAlpha, ZenithStyle currentStyle) {
      boolean live = this.isLive(this.active);
      if (!this.liveReady) {
         this.liveAnimation.snapTo(live ? 1.0F : 0.0F);
         this.liveReady = true;
      }

      float hover = this.liveAnimation.animateTo(live ? 1.0F : 0.0F);
      Font font = Fonts.NEW_REGULAR.getFont(4.6F);
      String liveText = this.liveText();
      float width = 3.0F;
      float f = y + 11.5F;
      ZenithColor zenithColor = currentStyle.getTextTertiary().getZenithColor();
      ZenithColor zenithColor2 = currentStyle.getFriendColor().getZenithColor();
      menuDrawContext.drawRoundedRect(
         floatValue, f - width / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor.method7295(zenithColor2, hover).method7294(previewAlpha)
      );
      menuDrawContext.drawText(font, liveText, floatValue + width + 4.0F, f - font.height() / 2.0F, zenithColor.method7295(zenithColor2, hover * 0.65F).method7294(previewAlpha));
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
      long now = System.currentTimeMillis();
      if (now - this.lastFrame > 400L && this.current() instanceof ToolTab toolTab) {
         toolTab.shown();
      }

      this.lastFrame = now;
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

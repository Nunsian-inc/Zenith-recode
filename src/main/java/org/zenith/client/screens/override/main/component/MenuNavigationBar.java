package org.zenith.client.screens.override.main.component;

import java.util.ArrayList;
import java.util.List;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MenuNavigationBar {
   public static final float HEIGHT = MenuSkin.HEADER_HEIGHT;
   private static final float GAP = MenuTheme.px(8.0F);
   private static final float ACTIVE_PADDING = MenuTheme.px(20.0F);
   private static final float ICON_LABEL_GAP = MenuTheme.px(8.0F);
   private static final float ICON_SIZE = 12.0F;
   private static final float LABEL_SIZE = 12.0F;
   private final List<MenuNavigationBar.Entry> entries = new ArrayList<>();

   public MenuNavigationBar(List<MenuNavigationBar.Item> items) {
      for (MenuNavigationBar.Item item : items) {
         this.entries.add(new MenuNavigationBar.Entry(item));
      }
   }

   public float measureWidth(int activeIndex) {
      float f = GAP * (this.entries.size() - 1);

      for (int i = 0; i < this.entries.size(); i++) {
         MenuNavigationBar.Entry entry = this.entries.get(i);
         f += lerp(HEIGHT, this.activeWidth(entry.item), i == activeIndex ? 1.0F : entry.activity.getValue());
      }

      return f;
   }

   public void render(MenuDrawContext graphics, float mouseX, float mouseY, float f, float y, int activeIndex) {
      for (int i = 0; i < this.entries.size(); i++) {
         this.entries.get(i).activity.updateVisibility(i == activeIndex);
      }

      float f2 = GAP * (this.entries.size() - 1);

      for (MenuNavigationBar.Entry entry : this.entries) {
         f2 += lerp(HEIGHT, this.activeWidth(entry.item), entry.activity.getValue());
      }

      float x = f - f2 / 2.0F;
      ZenithColor accent = MenuTheme.accent();

      for (MenuNavigationBar.Entry entry : this.entries) {
         float hover = entry.activity.getValue();
         float activeWidth = this.activeWidth(entry.item);
         float width = lerp(HEIGHT, activeWidth, hover);
         entry.x = x;
         entry.y = y;
         entry.width = width;
         entry.hover.updateVisibility(entry.contains((double)mouseX, (double)mouseY));
         float radius = MenuSkin.radius(HEIGHT);
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(radius);
         ZenithColor zenithColor = MenuTheme.panel().method7295(MenuTheme.plate(), entry.hover.getValue() * (1.0F - hover));
         ZenithColor zenithColor2 = zenithColor.method7295(accent, hover);
         MenuSurface.blur(graphics, x, y, width, HEIGHT, headerRadius, 1.0F - hover);
         if (hover > 0.01F) {
            MenuSurface.glow(graphics, x, y, width, HEIGHT, radius, accent, 0.4F * hover);
         }

         graphics.drawRoundedRect(x, y, width, HEIGHT, headerRadius, zenithColor2);
         this.drawContent(graphics, entry, activeWidth, hover, accent);
         x += width + GAP;
      }
   }

   private void drawContent(MenuDrawContext graphics, MenuNavigationBar.Entry entry, float activeWidth, float hover, ZenithColor accent) {
      MenuIcon icon = entry.item.icon();
      Font font = icon.font(12.0F);
      Font medium = MenuTheme.medium(12.0F);
      String label = entry.item.label();
      float width = icon.width(12.0F);
      float f = medium.width(label);
      float f2 = entry.x + (HEIGHT - width) / 2.0F;
      float f3 = width + ICON_LABEL_GAP + f;
      float activeWidth2 = entry.x + (activeWidth - f3) / 2.0F;
      float floatValue = lerp(f2, activeWidth2, hover);
      ZenithColor zenithColor = MenuTheme.textTertiary().method7295(accent, entry.hover.getValue() * (1.0F - hover));
      ZenithColor zenithColor2 = zenithColor.method7295(MenuTheme.onAccent(), hover);
      graphics.enableScissor(entry.x, entry.y, entry.x + entry.width, entry.y + HEIGHT);
      graphics.drawText(font, icon.glyph(), floatValue, entry.y + (HEIGHT - font.height()) / 2.0F, zenithColor2);
      if (hover > 0.01F) {
         graphics.drawText(medium, label, floatValue + width + ICON_LABEL_GAP, entry.y + (HEIGHT - medium.height()) / 2.0F, MenuTheme.onAccent().method7294(hover));
      }

      graphics.disableScissor();
   }

   private float activeWidth(MenuNavigationBar.Item item) {
      Font medium = MenuTheme.medium(12.0F);
      return ACTIVE_PADDING * 2.0F + item.icon().width(12.0F) + ICON_LABEL_GAP + medium.width(item.label());
   }

   private static float lerp(float f, float activeWidth, float hover) {
      return f + (activeWidth - f) * hover;
   }

   public boolean mouseClicked(double lastClickX, double lastClickY) {
      for (MenuNavigationBar.Entry entry : this.entries) {
         if (entry.contains(lastClickX, lastClickY)) {
            entry.item.action().run();
            return true;
         }
      }

      return false;
   }

   public BoundsData boundsOf(int n) {
      MenuNavigationBar.Entry entry = this.entries.get(n);
      return new BoundsData(entry.x, entry.y, entry.width, HEIGHT);
   }

   private static final class Entry {
      private final MenuNavigationBar.Item item;
      private final Animation activity = new Animation(220L, Easing.INSTANCE7);
      private final Animation hover = new Animation(160L, Easing.INSTANCE12);
      private float x;
      private float y;
      private float width;

      private Entry(MenuNavigationBar.Item item) {
         this.item = item;
      }

      private boolean contains(double mouseX, double mouseY) {
         return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + MenuNavigationBar.HEIGHT;
      }
   }

   public static final class Item {
      private final MenuIcon icon;
      private final String labelKey;
      private final Runnable action;

      public MenuIcon icon() {
         return this.icon;
      }

      public String labelKey() {
         return this.labelKey;
      }

      public Runnable action() {
         return this.action;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Item other)) return false;
         return java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.labelKey, other.labelKey) && java.util.Objects.equals(this.action, other.action);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(icon, labelKey, action);
      }

      @Override
      public String toString() {
         return "Item[" + "icon=" + this.icon + ", " + "labelKey=" + this.labelKey + ", " + "action=" + this.action + "]";
      }

      public Item(MenuIcon icon, String labelKey, Runnable action) {
         this.icon = icon;
         this.labelKey = labelKey;
         this.action = action;
      }

      public String label() {
         return MenuTheme.t(this.labelKey);
      }
   }
}

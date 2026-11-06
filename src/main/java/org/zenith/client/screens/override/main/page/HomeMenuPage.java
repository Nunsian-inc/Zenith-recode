package org.zenith.client.screens.override.main.page;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.utility.api.PlayTimeTracker;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class HomeMenuPage implements MainMenuPage {
   private static final float WIDTH = MenuTheme.px(402.0F);
   private static final float HEADER_HEIGHT = MenuTheme.px(42.0F);
   private static final float HEADER_GAP = MenuTheme.px(8.0F);
   private static final float LIST_HEIGHT = MenuTheme.px(240.0F);
   private static final float CARD_RADIUS = MenuTheme.px(12.0F);
   private static final float CARD_PADDING_X = MenuTheme.px(16.0F);
   private static final float CARD_PADDING_Y = MenuTheme.px(14.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(14.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float MARKER_BOX = MenuTheme.px(14.0F);
   private static final float MARKER_GAP = MenuTheme.px(4.0F);
   private static final float RAIL_GAP = MenuTheme.px(8.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float FOOTER_WIDTH = MenuTheme.px(360.0F);
   private static final float FOOTER_HEIGHT = MenuTheme.px(48.0F);
   private static final float STAT_WIDTH = MenuTheme.px(54.0F);
   private static final float STAT_GAP = MenuTheme.px(48.0F);
   private static List<HomeMenuPage.Entry> changelog;
   private final Animation expansion = new Animation(240L, 1.0F, Easing.INSTANCE7);
   private final MenuScroll scroll = new MenuScroll();
   private boolean expanded = true;
   private BoundsData headerBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private String subscriptionLabel = "-";
   private long subscriptionRevision = Long.MIN_VALUE;
   private long subscriptionCheckedAt;

   private static List<HomeMenuPage.Entry> changelog() {
      if (changelog != null) {
         return changelog;
      } else {
         ArrayList arrayList = new ArrayList();

         try {
            var resource = Minecraft.getInstance().getResourceManager().getResource(Zenith.method1501("menu/changelog.json"));
            if (resource.isPresent()) {
               try (InputStreamReader inputStreamReader = new InputStreamReader(((Resource)resource.get()).open(), StandardCharsets.UTF_8)) {
                  JsonObject jsonObject = JsonParser.parseReader(inputStreamReader).getAsJsonObject();

                  for (JsonElement json : jsonObject.getAsJsonArray("entries")) {
                     JsonObject jsonObject2 = json.getAsJsonObject();
                     arrayList.add(new HomeMenuPage.Entry(jsonObject2.get("text").getAsString(), jsonObject2.has("added") && jsonObject2.get("added").getAsBoolean()));
                  }
               }
            }
         } catch (Exception e) {
         }

         changelog = arrayList;
         return changelog;
      }
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return HEADER_HEIGHT + (HEADER_GAP + LIST_HEIGHT) * this.expansion.getValue();
   }

   @Override
   public float footerWidth() {
      return FOOTER_WIDTH;
   }

   @Override
   public float footerHeight() {
      return FOOTER_HEIGHT;
   }

   @Override
   public void update() {
      this.expansion.animateTo(this.expanded ? 1.0F : 0.0F);
   }

   @Override
   public void layout(BoundsData boundsData) {
      this.headerBounds = new BoundsData(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), HEADER_HEIGHT);
   }

   @Override
   public void render(MenuDrawContext graphics, float lastClickX, float lastClickY, BoundsData boundsData) {
      drawCard(graphics, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), HEADER_HEIGHT);
      this.drawHeader(graphics, boundsData, lastClickX, lastClickY);
      float height = boundsData.height() - HEADER_HEIGHT - HEADER_GAP;
      if (!(height <= 1.0F)) {
         float snap = boundsData.getFloatValue2() + HEADER_HEIGHT + HEADER_GAP;
         drawCard(graphics, boundsData.getFloatValue(), snap, boundsData.width(), height);
         this.drawChangelog(graphics, boundsData.getFloatValue(), snap, boundsData.width(), height);
      }
   }

   private void drawHeader(MenuDrawContext graphics, BoundsData boundsData, float lastClickX, float lastClickY) {
      MenuSkin.header(graphics, boundsData.getFloatValue(), boundsData.getFloatValue2(), MenuIcons.REFRESH, MenuTheme.t("menu.home.version") + " 3.5");
      MenuIcon menuIcon = this.expanded ? MenuIcons.CHEVRON_UP : MenuIcons.CHEVRON_DOWN;
      Font font = menuIcon.font(12.0F);
      boolean contains = this.headerBounds.contains(lastClickX, lastClickY);
      graphics.drawText(
         font,
         menuIcon.glyph(),
         boundsData.getFloatValue() + boundsData.width() - MenuSkin.EDGE - menuIcon.width(12.0F),
         boundsData.getFloatValue2() + (HEADER_HEIGHT - font.height()) / 2.0F,
         contains ? MenuTheme.text() : MenuTheme.textTertiary()
      );
   }

   private void drawChangelog(MenuDrawContext graphics, float floatValue, float snap, float width, float height) {
      float f = floatValue + CARD_PADDING_X;
      float y = snap + CARD_PADDING_Y;
      float f2 = width - CARD_PADDING_X * 2.0F - RAIL_GAP - RAIL_WIDTH;
      float viewport = height - CARD_PADDING_Y * 2.0F;
      List<HomeMenuPage.Entry> entries = changelog();
      float content = entries.isEmpty() ? 0.0F : entries.size() * ROW_HEIGHT + (entries.size() - 1) * ROW_GAP;
      this.scroll.update(viewport, content);
      graphics.enableScissor(f, y, f + f2, y + viewport);
      Font regular = MenuTheme.regular(11.0F);
      float f3 = y - this.scroll.offset();

      for (HomeMenuPage.Entry entry : entries) {
         MenuIcon menuIcon = entry.added() ? MenuIcons.PLUS : MenuIcons.MINUS;
         Font font = menuIcon.font(11.0F);
         ZenithColor accent = entry.added() ? MenuTheme.accent() : MenuTheme.textTertiary();
         ZenithColor text = entry.added() ? MenuTheme.text() : MenuTheme.textSecondary();
         graphics.drawText(font, menuIcon.glyph(), f + (MARKER_BOX - menuIcon.width(11.0F)) / 2.0F, f3 + (ROW_HEIGHT - font.height()) / 2.0F, accent);
         graphics.drawText(regular, MenuTheme.t(entry.text()), f + MARKER_BOX + MARKER_GAP, f3 + (ROW_HEIGHT - regular.height()) / 2.0F, text);
         f3 += ROW_HEIGHT + ROW_GAP;
      }

      graphics.disableScissor();
      this.scroll.renderRail(graphics, floatValue + width - CARD_PADDING_X - RAIL_WIDTH, y, viewport);
   }

   @Override
   public void renderFooter(MenuDrawContext menuDrawContext, float f, float f2, BoundsData boundsData) {
      Zenith.Entry entry = Zenith.getInstance().getEntry();
      List<HomeMenuPage.Stat> stats = List.of(
         new HomeMenuPage.Stat(MenuIcons.SUBSCRIPTION, this.subscriptionDays(entry), MenuTheme.t("menu.home.subscription")),
         new HomeMenuPage.Stat(MenuIcons.UID, entry.getText2() == null ? "-" : entry.getText2(), MenuTheme.t("menu.home.uid")),
         new HomeMenuPage.Stat(MenuIcons.PLAYTIME, playTime(), MenuTheme.t("menu.home.playtime")),
         new HomeMenuPage.Stat(MenuIcons.ONLINE, onlineFriends(), MenuTheme.t("menu.home.online"))
      );
      float floatValue = boundsData.getFloatValue();

      for (HomeMenuPage.Stat stat : stats) {
         this.drawStat(menuDrawContext, stat, floatValue, boundsData.getFloatValue2());
         floatValue += STAT_WIDTH + STAT_GAP;
      }
   }

   private void drawStat(MenuDrawContext menuDrawContext, HomeMenuPage.Stat stat, float floatValue, float f) {
      Font font = stat.icon().font(16.0F);
      Font medium = MenuTheme.medium(10.0F);
      Font regular = MenuTheme.regular(9.0F);
      menuDrawContext.drawText(
         font,
         stat.icon().glyph(),
         floatValue + (STAT_WIDTH - stat.icon().width(16.0F)) / 2.0F,
         f + (MenuTheme.px(16.0F) - font.height()) / 2.0F,
         MenuTheme.accent()
      );
      float centeredTextY = f + MenuTheme.px(24.0F);
      menuDrawContext.drawText(medium, stat.value(), floatValue + (STAT_WIDTH - medium.width(stat.value())) / 2.0F, centeredTextY, MenuTheme.text());
      menuDrawContext.drawText(regular, stat.label(), floatValue + (STAT_WIDTH - regular.width(stat.label())) / 2.0F, centeredTextY + MenuTheme.px(14.0F), MenuTheme.textTertiary());
   }

   private static String playTime() {
      PlayTimeTracker playTimeTracker = Zenith.getInstance().getPlayTimeTracker();
      return playTimeTracker == null ? "0m" : PlayTimeTracker.method1402(playTimeTracker.method1401());
   }

   private static String onlineFriends() {
      Cloud cloud = Zenith.getInstance().getCloud();
      return cloud != null && cloud.method7862() ? String.valueOf(cloud.state().method8045()) : "0";
   }

   private String subscriptionDays(Zenith.Entry entry) {
      long now = System.currentTimeMillis();
      long subscriptionRevision = entry.method1510();
      if (subscriptionRevision == this.subscriptionRevision && now - this.subscriptionCheckedAt < 60000L) {
         return this.subscriptionLabel;
      } else {
         this.subscriptionRevision = subscriptionRevision;
         this.subscriptionCheckedAt = now;
         long l = entry.method1506();
         if (l == Long.MAX_VALUE) {
            this.subscriptionLabel = MenuTheme.t("gui.sub.lifetime");
         } else if (l == -1L) {
            this.subscriptionLabel = MenuTheme.t("gui.sub.active");
         } else {
            this.subscriptionLabel = l + " " + MenuTheme.t("menu.home.daysLeft");
         }

         return this.subscriptionLabel;
      }
   }

   private static void drawCard(MenuDrawContext graphics, float floatValue, float snap, float width, float height) {
      MenuSkin.panel(graphics, floatValue, snap, width, height);
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int n) {
      if (n == 0 && this.headerBounds.contains(lastClickX, lastClickY)) {
         this.expanded = !this.expanded;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double d, double d2, double d3) {
      return this.scroll.scroll(d3, ROW_HEIGHT + ROW_GAP);
   }

   private static final class Entry {
      private final String text;
      private final boolean added;

      public String text() {
         return this.text;
      }

      public boolean added() {
         return this.added;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Entry other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.added, other.added);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, added);
      }

      @Override
      public String toString() {
         return "Entry[" + "text=" + this.text + ", " + "added=" + this.added + "]";
      }

      private Entry(String text, boolean added) {
         this.text = text;
         this.added = added;
      }
   }

   private static final class Stat {
      private final MenuIcon icon;
      private final String value;
      private final String label;

      public MenuIcon icon() {
         return this.icon;
      }

      public String value() {
         return this.value;
      }

      public String label() {
         return this.label;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Stat other)) return false;
         return java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.value, other.value) && java.util.Objects.equals(this.label, other.label);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(icon, value, label);
      }

      @Override
      public String toString() {
         return "Stat[" + "icon=" + this.icon + ", " + "value=" + this.value + ", " + "label=" + this.label + "]";
      }

      private Stat(MenuIcon icon, String subscriptionDays, String label) {
         this.icon = icon;
         this.value = subscriptionDays;
         this.label = label;
      }
   }
}

package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Share;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class WeekBlock {
   private static final float INSET = 8.0F;
   private static final long REFRESH_MS = 30000L;
   private static final ZenithColor UP = new ZenithColor(-11870592);
   private static final ZenithColor DOWN = new ZenithColor(-495247);
   private static final ZenithColor[] MEDALS = new ZenithColor[]{new ZenithColor(-278748), new ZenithColor(-3418655), new ZenithColor(-2525434)};
   private final FriendsSectionPanel host;
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Integer> hovered = new HashMap<>();
   private final Animation fill = new Animation(700L, 0.0F, Easing.INSTANCE12);
   private int week;
   private int sort;
   private long requestedAt;

   public WeekBlock(FriendsSectionPanel host) {
      this.host = host;
   }

   public String signature(Cloud cloud) {
      PartyMember.Data14 data14 = cloud == null ? null : cloud.getPartyMember().getData14();
      return this.week + "|" + this.sort + "|" + (data14 == null ? 0L : data14.getLongValue());
   }

   public void tick(Cloud cloud, boolean flag) {
      if (flag && cloud != null && cloud.method7860() && System.currentTimeMillis() - this.requestedAt > 30000L) {
         this.request(cloud);
      }
   }

   public void opened(Cloud cloud) {
      this.fill.reset(0.0F);
      this.fill.method2810(1.0F);
      if (cloud != null && cloud.method7860()) {
         this.request(cloud);
      }
   }

   private void request(Cloud cloud) {
      this.requestedAt = System.currentTimeMillis();
      PartyMember.Data14 data14 = cloud.getPartyMember().getData14();
      cloud.getPartyMember().method1170(this.week == 1 && data14 != null ? data14.getText2() : "");
   }

   public void build(Cloud cloud) {
      if (cloud != null && cloud.method7860()) {
         PartyMember.Data14 data14 = cloud.getPartyMember().getData14();
         if (data14 == null) {
            this.line(t("gui.party.stats.loading"));
         } else {
            this.chips("week", new String[]{t("gui.party.stats.thisWeek"), t("gui.party.stats.lastWeek")}, () -> this.week, var2x -> {
               this.week = var2x;
               this.request(cloud);
               this.fill.reset(0.0F);
               this.fill.method2810(1.0F);
            });
            PartyMember.Data5 data5 = data14.method1219();
            PartyMember.Data9 data9 = data5 == null ? PartyMember.Data9.INSTANCE : data5.method1213();
            PartyMember.Data9 data92 = data5 == null ? PartyMember.Data9.INSTANCE : data5.method1214();
            this.hero(data9, data92);
            ArrayList<PartyMember.Data5> data5s = new ArrayList<>(data14.getItems2());
            boolean flag = true;

            for (PartyMember.Data5 data52 : data5s) {
               flag &= data52.method1213().getLongValue() < 60000L && data52.method1213().getLongValue2() == 0L && data52.method1213().getLongValue3() == 0L;
            }

            if (flag) {
               this.line(t(this.week == 1 ? "gui.party.stats.emptyLast" : "gui.party.stats.empty"));
            } else if (data5s.size() <= 1) {
               this.line(t("gui.party.stats.alone"));
            } else {
               this.host.caption(t("gui.party.caption.board") + " · " + data5s.size());
               this.chips("sort", new String[]{t("gui.party.stats.byTime"), t("gui.party.stats.byKills")}, () -> this.sort, var1x -> this.sort = var1x);
               data5s.sort(
                  (var1x, var2x) -> this.sort == 1
                     ? Long.compare(var2x.method1213().getLongValue2(), var1x.method1213().getLongValue2())
                     : Long.compare(var2x.method1213().getLongValue(), var1x.method1213().getLongValue())
               );
               long l = 1L;

               for (PartyMember.Data5 data52 : data5s) {
                  l = Math.max(l, this.sort == 1 ? data52.method1213().getLongValue2() : data52.method1213().getLongValue());
               }

               for (int i = 0; i < data5s.size(); i++) {
                  PartyMember.Data5 data52 = (PartyMember.Data5)data5s.get(i);
                  float f = (float)(this.sort == 1 ? data52.method1213().getLongValue2() : data52.method1213().getLongValue()) / (float)l;
                  this.row(i, data52, f);
               }
            }

            this.aside(cloud, data14);
         }
      } else {
         this.line(t(cloud != null && cloud.method7862() ? "gui.party.nodeOld" : "gui.party.offline"));
      }
   }

   private void chips(String key, String[] strings, WeekBlock.Selected selected, WeekBlock.Choose choose) {
      this.host
         .custom(
            18.0F,
            (var4x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
               Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
               float floatValue = f + 8.0F;
               int n = -1;

               for (int i = 0; i < strings.length; i++) {
                  float width = font.width(strings[i]) + 14.0F;
                  BoundsData boundsData = new BoundsData(floatValue, f2 + 3.0F, width, 12.0F);
                  boolean contains = boundsData.contains(lastClickX, lastClickY);
                  if (contains) {
                     n = i;
                  }

                  boolean flag = selected.get() == i;
                  float hover = this.hover(key + i, contains);
                  var4x.drawRoundedRect(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     boundsData.width(),
                     boundsData.height(),
                     ProxyDotRadiusData.method8069(6.0F),
                     (flag
                           ? zenithStyle.getPrimaryColor().getZenithColor().method7292(0.85F)
                           : zenithStyle.getDisableActiveBg().getZenithColor().method7294(0.7F + 0.3F * hover))
                        .method7294(previewAlpha)
                  );
                  var4x.drawText(
                     font,
                     strings[i],
                     boundsData.getFloatValue() + 7.0F,
                     boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
                     (flag ? new ZenithColor(255, 255, 255) : zenithStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha)
                  );
                  floatValue += width + 4.0F;
               }

               this.hovered.put(key, n);
            },
            () -> {
               int var4x = this.hovered.getOrDefault(key, -1);
               if (var4x >= 0 && var4x != selected.get()) {
                  choose.pick(var4x);
                  this.host.invalidate();
               }
            },
            null
         );
   }

   private void hero(PartyMember.Data9 data9, PartyMember.Data9 data92) {
      this.host
         .custom(
            46.0F,
            (var2x, n, n2, f, f2, f3, previewAlpha, zenithStyle) -> {
               float floatValue = f + 8.0F;
               float width = f3 - 16.0F;
               float y = f2 + 3.0F;
               float height = 40.0F;
               var2x.drawRoundedRect(
                  floatValue, y, width, height, ProxyDotRadiusData.method8069(4.0F), zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * 0.5F)
               );
               String[] strings = new String[]{hours(data9.getLongValue()), String.valueOf(data9.getLongValue2()), String.valueOf(data9.getLongValue3()), kd(data9)};
               String[] strings2 = new String[]{t("gui.party.stats.played"), t("gui.party.stats.kills"), t("gui.party.stats.deaths"), t("gui.party.stats.kd")};
               String[] strings3 = new String[]{
                  delta(data9.getLongValue(), data92.getLongValue()),
                  delta(data9.getLongValue2(), data92.getLongValue2()),
                  delta(data9.getLongValue3(), data92.getLongValue3()),
                  ""
               };
               float f4 = width / 4.0F;
               Font font = Fonts.NEW_MEDIUM.getFont(7.5F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);

               for (int i = 0; i < 4; i++) {
                  float floatValue2 = floatValue + f4 * i + 8.0F;
                  var2x.drawText(font, strings[i], floatValue2, y + 7.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
                  float centeredTextY = y + 7.0F + font.height() + 3.0F;
                  var2x.drawText(font2, strings2[i], floatValue2, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
                  if (!strings3[i].isEmpty()) {
                     boolean startsWith = strings3[i].startsWith("+");
                     boolean flag = i == 2 ? !startsWith : startsWith;
                     var2x.drawText(font2, strings3[i], floatValue2 + font2.width(strings2[i]) + 4.0F, centeredTextY, (flag ? UP : DOWN).method7294(previewAlpha));
                  }

                  if (i > 0) {
                     var2x.drawRect(floatValue + f4 * i, y + 8.0F, 0.5F, height - 16.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.3F));
                  }
               }
            }
         );
   }

   private void row(int n, PartyMember.Data5 data5, float f) {
      this.host
         .custom(
            19.0F,
            (menuDrawContext, n2, n3, f2, f3, f4, previewAlpha, zenithStyle) -> {
               float floatValue = f2 + 8.0F;
               float width = f4 - 16.0F;
               float f5 = this.fill.method2808(1.0F) * Mth.clamp(f, 0.0F, 1.0F);
               boolean flag = data5.isFlag();
               menuDrawContext.drawRoundedRect(
                  floatValue,
                  f3 + 2.0F,
                  width,
                  15.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (flag ? 0.9F : 0.5F))
               );
               if (width * f5 >= 8.0F) {
                  menuDrawContext.drawRoundedRect(
                     floatValue,
                     f3 + 2.0F,
                     width * f5,
                     15.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * (flag ? 0.45F : 0.22F))
                  );
               }

               float width2 = 10.0F;
               ZenithColor zenithColor = n < MEDALS.length ? MEDALS[n] : zenithStyle.getTextTertiary().getZenithColor().method7294(0.5F);
               menuDrawContext.drawRoundedRect(
                  floatValue + 3.0F, f3 + 2.0F + (15.0F - width2) / 2.0F, width2, width2, ProxyDotRadiusData.method8069(width2 / 2.0F), zenithColor.method7294(previewAlpha)
               );
               Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
               String text = String.valueOf(n + 1);
               menuDrawContext.drawText(
                  font,
                  text,
                  floatValue + 3.0F + (width2 - font.width(text)) / 2.0F,
                  f3 + 2.0F + (15.0F - font.height()) / 2.0F,
                  new ZenithColor(20, 20, 20).method7294(previewAlpha)
               );
               Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
               Font font3 = Fonts.NEW_REGULAR.getFont(4.4F);
               String str = hours(data5.method1213().getLongValue()) + " · " + data5.method1213().getLongValue2() + "/" + data5.method1213().getLongValue3();
               menuDrawContext.drawText(
                  font3,
                  str,
                  floatValue + width - 6.0F - font3.width(str),
                  f3 + 2.0F + (15.0F - font3.height()) / 2.0F,
                  zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
               );
               String text2 = data5.name() + (flag ? " " + t("gui.party.you") : "");
               menuDrawContext.drawText(
                  font2,
                  text2,
                  floatValue + 18.0F,
                  f3 + 2.0F + (15.0F - font2.height()) / 2.0F,
                  (flag ? zenithStyle.getTextEnable() : zenithStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
               );
               if (data5.isFlag2()) {
                  menuDrawContext.drawRoundedRect(
                     floatValue + 18.0F + font2.width(text2) + 4.0F,
                     f3 + 2.0F + 5.5F,
                     4.0F,
                     4.0F,
                     ProxyDotRadiusData.method8069(2.0F),
                     zenithStyle.getFriendColor().getZenithColor().method7294(previewAlpha)
                  );
               }
            }
         );
   }

   private void aside(Cloud cloud, PartyMember.Data14 data14) {
      this.host.asideCaption("gui.party.stats.report");
      PartyMember.Data5 data5 = data14.method1219();
      if (data5 != null) {
         PartyMember.Data9 data9 = data5.method1213();
         PartyMember.Data9 data92 = data5.method1214();
         String replacement = delta(data9.getLongValue(), data92.getLongValue());
         this.host
            .asideHint(
               "• "
                  + (
                     replacement.isEmpty()
                        ? t("gui.party.report.playedPlain").replace("{h}", hours(data9.getLongValue()))
                        : t("gui.party.report.played").replace("{h}", hours(data9.getLongValue())).replace("{d}", replacement)
                  )
            );
         var entries = new ArrayList<>(data5.getItems().entrySet());
         entries.removeIf(entry -> ((PartyMember.Data9)entry.getValue()).getLongValue() < 60000L);
         entries.sort((entry, var1x) -> Long.compare(((PartyMember.Data9)var1x.getValue()).getLongValue(), ((PartyMember.Data9)entry.getValue()).getLongValue()));
         if (!entries.isEmpty()) {
            this.host.asideCaption("gui.party.caption.servers");
         }

         for (int i = 0; i < Math.min(4, entries.size()); i++) {
            Entry entry = (Entry)entries.get(i);
            this.host
               .asideInfo(serverName((String)entry.getKey()), () -> hours(((PartyMember.Data9)entry.getValue()).getLongValue()), "gui.party.caption.servers");
         }
      }

      PartyMember.Data5 data52 = null;
      PartyMember.Data5 data53 = null;

      for (PartyMember.Data5 data54 : data14.getItems2()) {
         if (data52 == null || data54.method1213().getLongValue() > data52.method1213().getLongValue()) {
            data52 = data54;
         }

         if (data53 == null || data54.method1213().getLongValue2() > data53.method1213().getLongValue2()) {
            data53 = data54;
         }
      }

      if (data52 != null && data14.getItems2().size() > 1 && data52.method1213().getLongValue() > 0L) {
         this.host
            .asideHint(
               "• "
                  + t(data52.isFlag() ? "gui.party.report.leaderYou" : "gui.party.report.leader")
                     .replace("{n}", data52.name())
                     .replace("{h}", hours(data52.method1213().getLongValue()))
            );
      }

      if (data53 != null && data53.method1213().getLongValue2() > 0L && data14.getItems2().size() > 1) {
         this.host
            .asideHint("• " + t("gui.party.report.killer").replace("{n}", data53.name()).replace("{k}", String.valueOf(data53.method1213().getLongValue2())));
      }

      Share share = Zenith.getInstance().getShare();
      this.host.asideCaption("gui.party.stats.privacy");
      this.host.asideAction("cloud.share.stats", MenuGlyph.TIMER, () -> t(share.stats.isEnabled() ? "gui.value.enabled" : "gui.value.disabled"), () -> {
         share.stats.setEnabled(!share.stats.isEnabled());
         this.host.invalidate();
      }, "cloud.share.stats.desc");
      if (!share.server.isEnabled()) {
         this.host.asideHint(t("gui.party.stats.serverHidden"));
      }
   }

   private void line(String description) {
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> wrapText = SectionPanel.wrapText(description, font, this.host.mainWidth() - 16.0F, 4);
      float f = font.height() + 2.0F;
      this.host.custom(wrapText.size() * f + 6.0F, (var3x, var4x, n, f2, f3, f4, previewAlpha, zenithStyle) -> {
         float centeredTextY = f3 + 3.0F;

         for (String text : wrapText) {
            var3x.drawText(font, text, f2 + 8.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY += f;
         }
      });
   }

   private float hover(String key, boolean contains) {
      return this.hover.computeIfAbsent(key, str -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(contains ? 1.0F : 0.0F);
   }

   private static String serverName(String key) {
      if ("hidden".equals(key)) {
         return t("gui.party.stats.hiddenServer");
      } else {
         String display = ServerNameUtils.display(key);
         return display.isEmpty() ? key : display;
      }
   }

   private static String hours(long longValue) {
      long l = longValue / 60000L;
      long l2 = l / 60L;
      return l2 > 0L ? l2 + " " + t("gui.unit.h") + " " + l % 60L + " " + t("gui.unit.min") : l + " " + t("gui.unit.min");
   }

   private static String kd(PartyMember.Data9 data9) {
      return data9.getLongValue3() == 0L
         ? String.valueOf(data9.getLongValue2())
         : String.format(Locale.ROOT, "%.2f", (double)data9.getLongValue2() / data9.getLongValue3());
   }

   private static String delta(long longValue, long l) {
      if (l <= 0L) {
         return "";
      } else {
         long l2 = Math.round((longValue - l) * 100.0 / l);
         return (l2 >= 0L ? "+" : "") + l2 + "%";
      }
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private interface Choose {
      void pick(int n);
   }

   private interface Selected {
      int get();
   }
}

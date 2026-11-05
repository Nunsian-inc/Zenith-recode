package org.zenith.client.hud;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ScoreboardHud extends Interface {
   private static final String text = "k";
   private Component text2;
   private int intValue;
   private Component text3;

   public ScoreboardHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      Scoreboard scoreboard = mc.level.getScoreboard();
      Objective displayObjective = null;
      PlayerTeam playersTeam = scoreboard.getPlayersTeam(mc.player.getScoreboardName());
      if (playersTeam != null) {
         DisplaySlot slot = playersTeam.getColor().map(net.minecraft.world.scores.TeamColor::displaySlot).orElse(null);
         if (slot != null) {
            displayObjective = scoreboard.getDisplayObjective(slot);
         }
      }

      Objective objective = displayObjective != null ? displayObjective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
      if (objective != null) {
         this.method2665(customDrawContext, objective);
      } else {
         this.height = 0.0F;
      }
   }

   private void method2665(CustomDrawContext customDrawContext, Objective objective) {
      Scoreboard scoreboard = objective.getScoreboard();
      Font nameFont = HudListLayout.nameFont();
      Font font = HudListLayout.method7631();
      Font font2 = HudListLayout.method7630();
      NumberFormat numberFormatOrDefault = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
      List<ScoreboardHud.Entry> entries = scoreboard.listPlayerScores(objective)
         .stream()
         .filter(listPlayerScore -> !listPlayerScore.isHidden())
         .sorted(net.minecraft.client.gui.Hud.SCORE_DISPLAY_ORDER)
         .limit(15L)
         .map(var3x -> {
            PlayerTeam var4x = scoreboard.getPlayersTeam(var3x.owner());
            MutableComponent var5x = PlayerTeam.formatNameForTeam(var4x, var3x.ownerName());
            Component var6x = ScoreboardParser.method7076(var5x);
            if (var6x == null) {
               return null;
            } else {
               MutableComponent var7x = var3x.formatValue(numberFormatOrDefault);
               float var8x = nameFont.width(var7x);
               return new ScoreboardHud.Entry(var6x, var7x, (int)var8x);
            }
         })
         .filter(entry -> entry != null)
         .collect(Collectors.toList());
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Component text = this.method2668(objective.getDisplayName(), currentStyle);
      float width = font.width(text);
      float f = 0.0F;

      for (ScoreboardHud.Entry entry : entries) {
         f = Math.max(f, nameFont.width(entry.name()));
      }

      float f2 = 16.5F + HudListLayout.dividerWidth() + width + HudListLayout.CONST_floatValue7 + font2.width("m") + 6.0F;
      float f3 = 12.0F + f;
      int size = entries.size();
      byte b = 9;
      byte b2 = 20;
      int n = size * b;
      int y1 = (int)this.floatValue2;
      int n2 = y1 + b;
      int n3 = y1 + b2;
      int n4 = method2667(entries, n2, b, n3);
      int x1 = (int)(this.floatValue + 6.0F);
      this.width = Math.max(f2, f3);
      this.height = Math.max(b2, n + 10 + n4);
      HudListLayout.method7635(customDrawContext, this.getX(), this.getY(), this.width, this.height);
      this.method2666(customDrawContext, currentStyle, this.getX(), y1, this.width, font, font2, text);
      this.method288(customDrawContext.guiWidth(), customDrawContext.guiHeight());
      int n5 = 0;

      for (int i = 0; i < size; i++) {
         ScoreboardHud.Entry entry = (ScoreboardHud.Entry)entries.get(i);
         int n6 = n2 + i * b + n5;
         Object name = entry.name();
         boolean flag = entry.flag;
         if (NameProtectModule.INSTANCE.isEnabled()) {
            Object obj = name;
            if (entry.text3.contains(mc.player.getScoreboardName())) {
               name = TextUtils.method2110((Component)name, mc.player.getScoreboardName(), NameProtectModule.method5141());
            } else if (NameProtectModule.INSTANCE.getText() != null) {
               if (entry.text3.contains("Группа:")) {
                  name = TextUtils.method2114((Component)name, "Группа:", NameProtectModule.INSTANCE.getText());
               } else if (entry.text3.contains("Ранг:")) {
                  name = TextUtils.method2114((Component)name, "Ранг:", NameProtectModule.INSTANCE.getText());
               }
            }

            if (name != obj) {
               flag = method2669(((net.minecraft.network.chat.Component)name).getString());
            }
         }

         if (flag && n6 < n3) {
            int n7 = n3 - n6;
            n5 += n7;
            n6 += n7;
         }

         customDrawContext.drawText(nameFont, (Component)name, x1, n6 + (b - nameFont.height()) / 2.0F, currentStyle.getTextEnable().method1445());
      }
   }

   private void method2666(CustomDrawContext customDrawContext, ZenithStyle currentStyle, float x, float y1, float width, Font font, Font font2, Component text) {
      float y = y1 + 3.5F;
      customDrawContext.drawRoundedRect(x + 3.5F, y, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.9F), currentStyle.getPrimaryColor().getZenithColor());
      HudListLayout.method7637(customDrawContext, font2, "k", x + 3.5F, y, 13.0F, ZenithColor.INSTANCE);
      float floatValue = HudListLayout.method7634(customDrawContext, x + 3.5F + 13.0F, y1, 20.0F);
      customDrawContext.drawText(font, text, floatValue, y1 + (20.0F - font.height()) / 2.0F);
      customDrawContext.drawText(font2, "m", x + width - 6.0F - font2.width("m"), y1 + (20.0F - font2.height()) / 2.0F, currentStyle.getTextTertiary().getZenithColor());
   }

   private static int method2667(List<ScoreboardHud.Entry> entries, int n, int n2, int n3) {
      int n4 = 0;

      for (int i = 0; i < entries.size(); i++) {
         int n5 = n + i * n2 + n4;
         if (((ScoreboardHud.Entry)entries.get(i)).flag && n5 < n3) {
            n4 += n3 - n5;
         }
      }

      return n4;
   }

   private Component method2668(Component displayName, ZenithStyle currentStyle) {
      int intValue = currentStyle.getTextEnable().method1445();
      if (this.text3 != null && displayName == this.text2 && intValue == this.intValue) {
         return this.text3;
      } else {
         MutableComponent setStyle;
         if (displayName.getSiblings().isEmpty()) {
            setStyle = displayName.copy().setStyle(Style.EMPTY.withColor(currentStyle.getTextEnable().getZenithColor().method7287()));
         } else {
            MutableComponent literal = Component.literal("");
            MutableComponent component = MutableComponent.create(displayName.getContents());
            if (!component.getString().isEmpty()) {
               literal.append(component.setStyle(displayName.getStyle().getColor() == null ? Style.EMPTY.withColor(intValue) : displayName.getStyle()));
            }

            for (Component sibling : displayName.getSiblings()) {
               literal.append(sibling.copy().setStyle(sibling.getStyle().getColor() == null ? Style.EMPTY.withColor(intValue) : sibling.getStyle()));
            }

            setStyle = literal;
         }

         this.text2 = displayName;
         this.intValue = intValue;
         this.text3 = setStyle;
         return setStyle;
      }
   }

   private static boolean method2669(String string) {
      if (string.isBlank()) {
         return false;
      } else {
         for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == 167) {
               i++;
            } else if (!Character.isWhitespace(string.charAt(i))) {
               return true;
            }
         }

         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Entry {
      private final Component text;
      private final Component text2;
      private final int intValue;
      private final String text3;
      private final boolean flag;

      private Entry(Component text, Component component, int intValue) {
         this.text = text;
         this.text2 = component;
         this.intValue = intValue;
         this.text3 = text.getString();
         this.flag = ScoreboardHud.method2669(this.text3);
      }

      public Component name() {
         return this.text;
      }

      public Component score() {
         return this.text2;
      }

      public int scoreWidth() {
         return this.intValue;
      }
   }
}

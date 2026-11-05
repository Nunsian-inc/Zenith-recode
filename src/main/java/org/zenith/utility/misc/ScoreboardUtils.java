package org.zenith.utility.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class ScoreboardUtils {
   private static final Pattern pattern = Pattern.compile("Монеток:\\s*([\\d][\\d\\s\\u00A0.,]*)([kкKК]*)");

   private ScoreboardUtils() {
   }

   public static long method5530(Scoreboard scoreboard, String scoreboardName) {
      if (scoreboard == null) {
         return -1L;
      } else {
         Objective displayObjective = null;
         if (scoreboardName != null) {
            PlayerTeam playersTeam = scoreboard.getPlayersTeam(scoreboardName);
            if (playersTeam != null) {
               DisplaySlot slot = playersTeam.getColor().map(net.minecraft.world.scores.TeamColor::displaySlot).orElse(null);
               if (slot != null) {
                  displayObjective = scoreboard.getDisplayObjective(slot);
               }
            }
         }

         if (displayObjective == null) {
            displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
         }

         if (displayObjective == null) {
            return -1L;
         } else {
            for (PlayerScoreEntry listPlayerScore : scoreboard.listPlayerScores(displayObjective)) {
               if (!listPlayerScore.isHidden()) {
                  PlayerTeam playersTeam = scoreboard.getPlayersTeam(listPlayerScore.owner());
                  String input = method5532(PlayerTeam.formatNameForTeam(playersTeam, listPlayerScore.ownerName()).getString());
                  long l = method5531(input);
                  if (l >= 0L) {
                     return l;
                  }
               }
            }

            return -1L;
         }
      }
   }

   public static long method5531(String input) {
      Matcher matcher = pattern.matcher(input);
      if (!matcher.find()) {
         return -1L;
      } else {
         try {
            String str = matcher.group(1).replaceAll("[\\s\\u00A0]", "").replace(',', '.');
            String group = matcher.group(2);
            long count = str.chars().filter(var0x -> var0x == 46).count();
            if (count > 1L || count == 1L && group.isEmpty() && str.length() - str.indexOf(46) - 1 == 3) {
               str = str.replace(".", "");
            }

            double d = Double.parseDouble(str);

            for (int i = 0; i < group.length(); i++) {
               d *= 1000.0;
            }

            return Math.round(d);
         } catch (Exception e) {
            return -1L;
         }
      }
   }

   public static String method5532(String string) {
      StringBuilder builder = new StringBuilder(string.length());

      for (int i = 0; i < string.length(); i++) {
         char charAt = string.charAt(i);
         if (charAt == 167) {
            i++;
         } else {
            builder.append(charAt);
         }
      }

      return builder.toString();
   }
}

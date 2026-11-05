package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.misc.TextUtils;

@ModuleInfo(
   name = "StreamerMode",
   category = ModuleCategory.MISC,
   description = "module.streamermode.description"
)
public final class StreamerModeModule extends Module {
   public static final StreamerModeModule INSTANCE = new StreamerModeModule();
   private static final String[] texts = new String[]{
      "Dark",
      "Shadow",
      "Night",
      "Ice",
      "Fire",
      "Storm",
      "Ghost",
      "Sky",
      "Red",
      "Black",
      "White",
      "Mega",
      "Pro",
      "Lord",
      "King",
      "Big",
      "Crazy",
      "Toxic",
      "Demon",
      "Star",
      "Moon",
      "Blood",
      "Steel",
      "Frost",
      "Hyper",
      "Mad",
      "Evil",
      "Gold",
      "Neo",
      "Top"
   };
   private static final String[] texts2 = new String[]{
      "Wolf",
      "Killer",
      "Hunter",
      "Player",
      "Gamer",
      "Craft",
      "Boy",
      "Man",
      "Cat",
      "Fox",
      "Knight",
      "Tiger",
      "Ninja",
      "Dragon",
      "Reaper",
      "Master",
      "Warrior",
      "Legend",
      "Sniper",
      "Panda",
      "Bear",
      "Zombie",
      "Creeper",
      "God",
      "PvP",
      "Dog",
      "Bro",
      "Kid",
      "Rex",
      "Ace"
   };
   private final List<StreamerModeModule.Data> items = new ArrayList<>();
   private final Map<String, Integer> items2 = new HashMap<>();
   private boolean flag;
   private StreamerModeModule.Type type = StreamerModeModule.Type.IDLE;
   private int intValue = -1;
   private int intValue2 = -1;
   private int intValue3 = -1;
   @Nullable
   private Component text;
   @Nullable
   private Component text2;
   @Nullable
   private Component text3;
   @Nullable
   private Component text4;
   @Nullable
   private String text5;
   private final StopWatch stopWatch = new StopWatch();
   private static final int CONST_intValue = 1024;
   private final Map<String, String> items3 = new HashMap<>();
   private final List<String> items4 = new ArrayList<>();

   private StreamerModeModule() {
   }

   public void method6283(int intValue) {
      if (!this.isEnabled()) {
         ChatLogger.method2021("Сначала включи StreamerMode");
      } else if (mc.player != null && mc.player.connection != null) {
         if (this.type != StreamerModeModule.Type.IDLE) {
            ChatLogger.method2021("Парс уже идёт");
         } else {
            int anarchy = Zenith.getInstance().getServerDetector().getAnarchy();
            if (anarchy == -1) {
               ChatLogger.method2022("Не вижу номер текущей анархии, зайди на анку");
            } else if (anarchy == intValue) {
               ChatLogger.method2022("Ты уже на ан" + intValue + ", выбери другую");
            } else {
               this.intValue2 = anarchy;
               this.intValue = intValue;
               this.flag = false;
               this.method6285();
               this.type = StreamerModeModule.Type.GO_TARGET;
               this.stopWatch.reset();
               this.method6286(intValue);
               ChatLogger.method2019("Иду на ан" + intValue + " за табом, потом вернусь на ан" + anarchy);
            }
         }
      }
   }

   public void method6284() {
      this.items.clear();
      this.items2.clear();
      this.flag = false;
      this.method6285();
      this.type = StreamerModeModule.Type.IDLE;
      ChatLogger.method2019("Спаршенный таб очищен");
   }

   private void method6285() {
      this.intValue3 = -1;
      this.text = null;
      this.text2 = null;
      this.text3 = null;
      this.text4 = null;
      this.text5 = null;
   }

   public int getIntValue32() {
      return this.intValue3;
   }

   @Nullable
   public Component getText3() {
      return this.text;
   }

   @Nullable
   public Component getText22() {
      return this.text2;
   }

   @Nullable
   public Component getText32() {
      return this.text3;
   }

   @Nullable
   public Component getText4() {
      return this.text4;
   }

   @Nullable
   public String getText5() {
      return this.text5;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.type != StreamerModeModule.Type.IDLE && mc.player != null && mc.level != null) {
         int anarchy = Zenith.getInstance().getServerDetector().getAnarchy();
         switch (this.type) {
            case GO_TARGET:
               if (anarchy == this.intValue) {
                  this.type = StreamerModeModule.Type.WAIT_TAB;
                  this.stopWatch.reset();
               } else if (this.stopWatch.method7546(60000L)) {
                  this.type = StreamerModeModule.Type.IDLE;
                  ChatLogger.method2022("Не дождался захода на ан" + this.intValue);
               }
               break;
            case WAIT_TAB:
               int size = mc.player.connection.getListedOnlinePlayers().size();
               if (this.stopWatch.method7546(5000L) && size > 1 || this.stopWatch.method7546(15000L)) {
                  this.method6287();
                  this.type = StreamerModeModule.Type.RETURN;
                  this.stopWatch.reset();
                  this.method6286(this.intValue2);
               }
               break;
            case RETURN:
               if (anarchy == this.intValue2 || this.stopWatch.method7546(60000L)) {
                  this.type = StreamerModeModule.Type.IDLE;
                  this.flag = !this.items.isEmpty();
                  if (this.flag) {
                     ChatLogger.method2019("Спарсил " + this.items.size() + " ников с ан" + this.intValue + ", таб подменён");
                  } else {
                     ChatLogger.method2022("Таб на ан" + this.intValue + " спарсить не вышло");
                  }
               }
         }
      }
   }

   private void method6286(int intValue) {
      if (Zenith.getInstance().getServerDetector().method1642()) {
         Zenith.getInstance().getHolyWorldServerTracker().reconnect(intValue);
      } else {
         mc.player.connection.sendCommand("an" + intValue);
      }
   }

   private void method6287() {
      this.items.clear();
      this.items2.clear();
      this.method6285();
      String scoreboardName = mc.player.getScoreboardName();

      for (PlayerInfo playerInfo : mc.gui.hud.getTabList().getPlayerInfos()) {
         String name = playerInfo.getProfile().name();
         if (!name.equals(scoreboardName)) {
            MutableComponent setStyle = NameProtectModule.INSTANCE.method5146(playerInfo).copy();
            this.items.add(new StreamerModeModule.Data(name, setStyle, this.method6304(setStyle, name)));
         }
      }

      this.method6288();
   }

   private void method6288() {
      if (mc.player != null && mc.level != null) {
         this.intValue3 = mc.player.connection.getListedOnlinePlayers().size();
         PlayerTabOverlay tabList = mc.gui.hud.getTabList();
         this.text3 = ScoreboardParser.method7082(tabList.header);
         if (this.text3 == null) {
            this.text3 = ScoreboardParser.method7082(tabList.footer);
         }

         this.text4 = ScoreboardParser.method7084(tabList.header);
         if (this.text4 == null) {
            this.text4 = ScoreboardParser.method7084(tabList.footer);
         }

         if (this.text4 != null) {
            int n = ScoreboardParser.method7085(this.text4);
            if (n > 0) {
               this.intValue3 = n;
            }
         }

         Scoreboard scoreboard = mc.level.getScoreboard();
         Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
         if (displayObjective != null) {
            for (PlayerScoreEntry listPlayerScore : scoreboard.listPlayerScores(displayObjective)) {
               if (!listPlayerScore.isHidden()) {
                  PlayerTeam playersTeam = scoreboard.getPlayersTeam(listPlayerScore.owner());
                  MutableComponent formatNameForTeam = PlayerTeam.formatNameForTeam(playersTeam, listPlayerScore.ownerName());
                  if (ScoreboardParser.method7078(formatNameForTeam)) {
                     this.text = formatNameForTeam.copy();
                  } else if (ScoreboardParser.method7079(formatNameForTeam)) {
                     this.text2 = formatNameForTeam.copy();
                     int n = ScoreboardParser.method7085(formatNameForTeam);
                     if (n > 0) {
                        this.intValue3 = n;
                     }
                  }
               }
            }
         }

         this.text5 = ScoreboardParser.method7083(this.text);
         if (this.text5 == null) {
            this.text5 = ScoreboardParser.method7083(this.text3);
         }
      }
   }

   public boolean method6289() {
      return this.isEnabled() && this.flag && this.type == StreamerModeModule.Type.IDLE && !this.items.isEmpty();
   }

   public Component method6290(PlayerInfo playerInfo) {
      if (this.method6289() && playerInfo != null && mc.player != null) {
         String name = playerInfo.getProfile().name();
         if (name.equals(mc.player.getScoreboardName())) {
            return null;
         } else {
            StreamerModeModule.Data data = this.method6297(name);
            return data == null ? null : data.getText2().copy();
         }
      } else {
         return null;
      }
   }

   public String method6291(String name) {
      if (!this.method6289() || name == null || mc.player == null) {
         return name;
      } else if (name.equals(mc.player.getScoreboardName())) {
         return name;
      } else {
         StreamerModeModule.Data data = this.method6297(name);
         return data == null ? name : data.getText();
      }
   }

   public Component method6292(Component nameTag, String name) {
      if (!this.method6289() || nameTag == null || name == null || mc.player == null) {
         return nameTag;
      } else if (name.equals(mc.player.getScoreboardName())) {
         return nameTag;
      } else {
         StreamerModeModule.Data data = this.method6297(name);
         return (Component)(data == null ? nameTag : TextUtils.method2107(nameTag, name, data.getText3().copy()));
      }
   }

   public Component method6293(Component nameTag) {
      if (this.method6289() && nameTag != null && mc.player != null && mc.player.connection != null) {
         String name = this.method6294(nameTag.getString());
         return name == null ? nameTag : this.method6292(nameTag, name);
      } else {
         return nameTag;
      }
   }

   private String method6294(String string) {
      this.method6295();
      if (this.items3.containsKey(string)) {
         return this.items3.get(string);
      } else {
         String str = null;

         for (String str2 : this.items4) {
            if ((str == null || str2.length() > str.length()) && string.contains(str2)) {
               str = str2;
            }
         }

         if (this.items3.size() >= 1024) {
            this.items3.clear();
         }

         this.items3.put(string, str);
         return str;
      }
   }

   private void method6295() {
      Collection<PlayerInfo> listedOnlinePlayers = mc.player.connection.getListedOnlinePlayers();
      boolean flag = listedOnlinePlayers.size() == this.items4.size();
      if (flag) {
         int n = 0;

         for (PlayerInfo listedOnlinePlayer : listedOnlinePlayers) {
            if (!listedOnlinePlayer.getProfile().name().equals(this.items4.get(n++))) {
               flag = false;
               break;
            }
         }
      }

      if (!flag) {
         this.items4.clear();

         for (PlayerInfo listedOnlinePlayer : listedOnlinePlayers) {
            this.items4.add(listedOnlinePlayer.getProfile().name());
         }

         this.items3.clear();
      }
   }

   public void method6296(List<PlayerInfo> playerInfos) {
      if (this.method6289()) {
         for (PlayerInfo playerInfo : playerInfos) {
            this.method6297(playerInfo.getProfile().name());
         }

         playerInfos.sort(Comparator.comparingInt(var1x -> this.items2.getOrDefault(var1x.getProfile().name(), Integer.MAX_VALUE)));
      }
   }

   private StreamerModeModule.Data method6297(String name) {
      if (this.items.isEmpty()) {
         return null;
      } else {
         Integer n = this.items2.get(name);
         if (n == null) {
            n = this.method6298();
            this.items2.put(name, n);
         }

         return this.items.get(n);
      }
   }

   private int method6298() {
      HashSet hashSet = new HashSet();
      if (mc.player != null && mc.player.connection != null) {
         for (PlayerInfo listedOnlinePlayer : mc.player.connection.getListedOnlinePlayers()) {
            Integer n = this.items2.get(listedOnlinePlayer.getProfile().name());
            if (n != null) {
               hashSet.add(n);
            }
         }
      } else {
         hashSet.addAll(this.items2.values());
      }

      for (int i = 0; i < this.items.size(); i++) {
         if (!hashSet.contains(i)) {
            return i;
         }
      }

      this.items.add(this.method6299());
      return this.items.size() - 1;
   }

   private StreamerModeModule.Data method6299() {
      String text = this.method6300();
      MutableComponent setStyle = Component.literal(text).setStyle(this.method6303());
      return new StreamerModeModule.Data(text, setStyle, setStyle);
   }

   private String method6300() {
      ThreadLocalRandom current = ThreadLocalRandom.current();

      for (int i = 0; i < 100; i++) {
         String anotherString = this.method6301(current);
         if (anotherString.length() <= 16 && !this.method6302(anotherString)) {
            return anotherString;
         }
      }

      String anotherString = "Player" + current.nextInt(1000, 10000);

      while (this.method6302(anotherString)) {
         anotherString = "Player" + current.nextInt(1000, 10000);
      }

      return anotherString;
   }

   private String method6301(ThreadLocalRandom current) {
      String text = texts[current.nextInt(texts.length)];
      String str = texts2[current.nextInt(texts2.length)];

      return switch (current.nextInt(7)) {
         case 0 -> text + str;
         case 1 -> text + str + current.nextInt(1, 100);
         case 2 -> text + str + current.nextInt(2007, 2013);
         case 3 -> text + "_" + str;
         case 4 -> (text + str).toLowerCase(Locale.ROOT);
         case 5 -> "_" + text + str + "_";
         default -> "x" + text + str + "x";
      };
   }

   private boolean method6302(String anotherString) {
      for (StreamerModeModule.Data data : this.items) {
         if (data.getText().equalsIgnoreCase(anotherString)) {
            return true;
         }
      }

      if (mc.player != null && mc.player.connection != null) {
         for (PlayerInfo listedOnlinePlayer : mc.player.connection.getListedOnlinePlayers()) {
            if (listedOnlinePlayer.getProfile().name().equalsIgnoreCase(anotherString)) {
               return true;
            }
         }
      }

      return false;
   }

   private Style method6303() {
      for (StreamerModeModule.Data data : this.items) {
         if (data.getText3().getString().equals(data.getText())) {
            Style[] styles = new Style[1];
            data.getText3().getVisualOrderText().accept((n, var2x, var3x) -> {
               styles[0] = var2x;
               return false;
            });
            if (styles[0] != null) {
               return styles[0];
            }
         }
      }

      return Style.EMPTY;
   }

   private Component method6304(Component setStyle, String name) {
      ArrayList arrayList = new ArrayList();
      StringBuilder builder = new StringBuilder();
      setStyle.getVisualOrderText().accept((var2x, var3x, var4x) -> {
         arrayList.add(Component.literal(new String(Character.toChars(var4x))).setStyle(var3x));
         builder.appendCodePoint(var4x);
         return true;
      });
      int indexOf = builder.indexOf(name);
      if (indexOf == -1) {
         return setStyle.copy();
      } else {
         int codePointCount = builder.toString().codePointCount(0, indexOf + name.length());
         MutableComponent empty = Component.empty();

         for (int i = 0; i < codePointCount && i < arrayList.size(); i++) {
            empty.append((Component)arrayList.get(i));
         }

         return empty;
      }
   }

   private static final class Data {
      private final String text;
      private final Component text2;
      private final Component text3;

      public String text() {
         return this.text;
      }

      public Component text2() {
         return this.text2;
      }

      public Component text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      private Data(String text, Component setStyle, Component component) {
         this.text = text;
         this.text2 = setStyle;
         this.text3 = component;
      }

      public String getText() {
         return this.text;
      }

      public Component getText2() {
         return this.text2;
      }

      public Component getText3() {
         return this.text3;
      }
   }

   private static enum Type {
      IDLE,
      GO_TARGET,
      WAIT_TAB,
      RETURN;
   }
}

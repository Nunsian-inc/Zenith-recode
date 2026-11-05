package org.zenith.utility.discord;

import java.io.IOException;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.chat.ChatCompose;
import org.zenith.client.screens.nlgui.panel.ProfilePanel;
import org.zenith.utility.api.CloudChatClient;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.discord.utils.DiscordEventHandlers;
import org.zenith.utility.discord.utils.DiscordRPC;
import org.zenith.utility.discord.utils.DiscordRichPresence;
import org.zenith.utility.discord.utils.RPCButton;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.render.PartyManager;

public class DiscordManager {
   private static final DiscordManager.DiscordInfo OFFLINE = new DiscordManager.DiscordInfo("Unknown", "", "");
   private static final long PUSH_MS = 5000L;
   private final DiscordManager.DiscordDaemonThread discordDaemonThread = new DiscordManager.DiscordDaemonThread();
   private volatile boolean running = true;
   private volatile boolean connected;
   private DiscordManager.DiscordInfo info = OFFLINE;
   private final long sessionStart = System.currentTimeMillis() / 1000L;
   private String lastPushed = "";
   private long lastPushAt;
   private DiscordEventHandlers handlers;

   public void init() {
      String lowerCase = System.getProperty("os.name").toLowerCase();
      if (!lowerCase.contains("linux")) {
         this.discordDaemonThread.start();
      }
   }

   public void stopRPC() {
      this.running = false;
      this.disconnect();
   }

   private boolean wanted() {
      try {
         ProfilePanel profilePanel = Zenith.getInstance().getNLMenuScreen().getProfilePanel();
         return profilePanel == null || profilePanel.getDiscordRPC().getSetting().isEnabled();
      } catch (Exception e) {
         return true;
      }
   }

   private void connect() {
      this.handlers = new DiscordEventHandlers.Builder()
         .ready(
            discordUser -> {
               Zenith.getInstance()
                  .getDiscordManager()
                  .setInfo(
                     new DiscordManager.DiscordInfo(
                        discordUser.username, "https://cdn.discordapp.com/avatars/" + discordUser.userId + "/" + discordUser.avatar + ".png", discordUser.userId
                     )
                  );
               this.lastPushed = "";
               this.lastPushAt = 0L;
               this.push();
            }
         )
         .build();
      DiscordRPC.INSTANCE.Discord_Initialize("1524110600555331776", this.handlers, true, "");
      this.connected = true;
   }

   private void push() {
      String details = identity();
      String status = withStatus(where());
      String lastPushed = details + "\u0000" + status;
      long now = System.currentTimeMillis();
      if (!lastPushed.equals(this.lastPushed) && now - this.lastPushAt >= 5000L) {
         this.lastPushed = lastPushed;
         this.lastPushAt = now;
         DiscordRichPresence discordRichPresence = new DiscordRichPresence.Builder()
            .setStartTimestamp(this.sessionStart)
            .setDetails(details)
            .setState(status)
            .setLargeImage("https://s13.gifyu.com/images/blr1p.png", "GlebVeaneqzhe")
            .setButtons(RPCButton.create("Telegram", "Нету"), RPCButton.create("Сайт", RegionUtil.method2038().get(0) + "/"))
            .build();
         DiscordRPC.INSTANCE.Discord_UpdatePresence(discordRichPresence);
      }
   }

   private static String identity() {
      return "Порт был сделан GlebVeaneqzhe / Специально для YouGame ";
   }

   private static String roleLabel() {
      String name = "";

      try {
         String text = CloudChatClient.getInstance().getText();
         name = text == null ? "" : text.trim();
      } catch (Throwable e) {
      }

      if (name.isEmpty()) {
         try {
            name = Zenith.getInstance().getEntry().method1504().getName();
         } catch (Exception e) {
            return "";
         }
      }

      String lowerCase = name.toLowerCase(Locale.ROOT);

      return switch (lowerCase) {
         case "administrator", "admin" -> "Админ";
         case "support", "moderator" -> "Модератор";
         case "media" -> "Медиа";
         case "private" -> "Private";
         case "opus", "recode" -> "Opus";
         case "opus/private" -> "Opus/Private";
         case "legacy" -> "Legacy";
         case "autobuy" -> "AutoBuy";
         case "alpha" -> "Opus/Private";
         case "user" -> "Игрок";
         default -> name;
      };
   }

   private static String withStatus(String where) {
      try {
         PartyManager partyManager = PartyManager.method17();
         if (partyManager == null) {
            return where;
         } else {
            String anObject = partyManager.method31();
            String str = Zenith.getInstance().getShare().text.getValue();
            String str2 = !"online".equals(anObject) && !"menu".equals(anObject) ? PartyManager.method50(anObject) : "";
            if (str != null && !str.isBlank()) {
               str2 = str2.isEmpty() ? str.strip() : str2 + ": " + str.strip();
            }

            String str3 = str2.isEmpty() ? where : where + " · " + str2;
            return str3.length() > 120 ? str3.substring(0, 120) : str3;
         }
      } catch (Throwable e) {
         return where;
      }
   }

   private static String where() {
      try {
         String server = ChatCompose.server();
         if (server.isEmpty()) {
            return "В главном меню";
         } else if (server.startsWith("singleplayer")) {
            return "Одиночная игра";
         } else {
            String str = ServerNameUtils.method5643(server, ChatCompose.mode());
            return str.isEmpty() ? "В игре" : str;
         }
      } catch (Throwable e) {
         return "В главном меню";
      }
   }

   private void disconnect() {
      if (this.connected) {
         this.connected = false;
         this.lastPushed = "";
         this.lastPushAt = 0L;
         DiscordRPC.INSTANCE.Discord_Shutdown();
         this.handlers = null;
         this.info = OFFLINE;
      }
   }

   public void load() throws IOException {
   }

   @Generated
   public void setRunning(boolean running) {
      this.running = running;
   }

   @Generated
   public void setConnected(boolean connected) {
      this.connected = connected;
   }

   @Generated
   public void setInfo(DiscordManager.DiscordInfo info) {
      this.info = info;
   }

   @Generated
   public void setLastPushed(String lastPushed) {
      this.lastPushed = lastPushed;
   }

   @Generated
   public void setLastPushAt(long lastPushAt) {
      this.lastPushAt = lastPushAt;
   }

   @Generated
   public DiscordManager.DiscordDaemonThread getDiscordDaemonThread() {
      return this.discordDaemonThread;
   }

   @Generated
   public boolean isRunning() {
      return this.running;
   }

   @Generated
   public boolean isConnected() {
      return this.connected;
   }

   @Generated
   public DiscordManager.DiscordInfo getInfo() {
      return this.info;
   }

   @Generated
   public long getSessionStart() {
      return this.sessionStart;
   }

   @Generated
   public String getLastPushed() {
      return this.lastPushed;
   }

   @Generated
   public long getLastPushAt() {
      return this.lastPushAt;
   }

   private class DiscordDaemonThread extends Thread {
      @Override
      public void run() {
         this.setName("Discord-RPC");

         try {
            for (Minecraft minecraft = Minecraft.getInstance(); minecraft == null || minecraft.getTextureManager() == null; minecraft = Minecraft.getInstance()) {
               Thread.sleep(500L);
            }

            for (; DiscordManager.this.running; Thread.sleep(500L)) {
               boolean wanted = DiscordManager.this.wanted();
               if (wanted && !DiscordManager.this.connected) {
                  DiscordManager.this.connect();
               } else if (!wanted && DiscordManager.this.connected) {
                  DiscordManager.this.disconnect();
               }

               if (DiscordManager.this.connected) {
                  DiscordRPC.INSTANCE.Discord_RunCallbacks();
                  DiscordManager.this.push();
                  DiscordManager.this.load();
               }
            }
         } catch (Exception e) {
            DiscordManager.this.stopRPC();
         }

         super.run();
      }
   }

   public static final class DiscordInfo {
      private final String userName;
      private final String avatarUrl;
      private final String userId;

      public String userName() {
         return this.userName;
      }

      public String avatarUrl() {
         return this.avatarUrl;
      }

      public String userId() {
         return this.userId;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof DiscordInfo other)) return false;
         return java.util.Objects.equals(this.userName, other.userName) && java.util.Objects.equals(this.avatarUrl, other.avatarUrl) && java.util.Objects.equals(this.userId, other.userId);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(userName, avatarUrl, userId);
      }

      @Override
      public String toString() {
         return "DiscordInfo[" + "userName=" + this.userName + ", " + "avatarUrl=" + this.avatarUrl + ", " + "userId=" + this.userId + "]";
      }

      public DiscordInfo(String userName, String avatarUrl, String userId) {
         this.userName = userName;
         this.avatarUrl = avatarUrl;
         this.userId = userId;
      }
   }
}

package org.zenith.utility.discord.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface DiscordRPC extends Library {
   DiscordRPC INSTANCE = (DiscordRPC)Native.load("discord-rpc", DiscordRPC.class);

   void Discord_UpdateHandlers(DiscordEventHandlers discordEventHandlers);

   void Discord_UpdatePresence(DiscordRichPresence discordRichPresence);

   void Discord_Respond(String str, int n);

   void Discord_Register(String str, String str2);

   void Discord_Shutdown();

   void Discord_UpdateConnection();

   void Discord_RegisterSteamGame(String str, String str2);

   void Discord_RunCallbacks();

   void Discord_Initialize(String str, DiscordEventHandlers handlers, boolean flag, String str2);

   void Discord_ClearPresence();

   public static enum DiscordReply {
      NO(0),
      IGNORE(2),
      YES(1);

      public final int reply;

      private DiscordReply(int reply) {
         this.reply = reply;
      }

      private static DiscordRPC.DiscordReply[] getReplies() {
         return new DiscordRPC.DiscordReply[]{NO, YES, IGNORE};
      }
   }
}

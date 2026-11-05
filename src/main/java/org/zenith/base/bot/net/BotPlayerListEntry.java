package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;

public final class BotPlayerListEntry extends PlayerInfo {
   public BotPlayerListEntry(GameProfile nonNull, boolean enforcesSecureChat) {
      super(nonNull, enforcesSecureChat);
   }

   public void resetBotSession(boolean enforcesSecureChat) {
      super.clearChatSession(enforcesSecureChat);
   }

   public void setBotGameMode(GameType botGameMode) {
      super.setGameMode(botGameMode);
   }

   public void setBotLatency(int botLatency) {
      super.setLatency(botLatency);
   }
}

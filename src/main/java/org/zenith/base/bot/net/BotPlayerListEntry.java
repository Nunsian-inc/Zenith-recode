package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;

public final class BotPlayerListEntry extends PlayerInfo {
   public BotPlayerListEntry(GameProfile var1, boolean var2) {
      super(var1, var2);
   }

   public void resetBotSession(boolean var1) {
      super.clearChatSession(var1);
   }

   public void setBotGameMode(GameType var1) {
      super.setGameMode(var1);
   }

   public void setBotLatency(int var1) {
      super.setLatency(var1);
   }
}

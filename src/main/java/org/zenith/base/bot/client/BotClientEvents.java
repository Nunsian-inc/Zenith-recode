package org.zenith.base.bot.client;

import net.minecraft.network.chat.Component;

public interface BotClientEvents {
   default void onPhaseChanged(BotClient botClient, BotPhase botPhase, BotPhase phase) {
   }

   default void onJoined(BotClient botClient) {
   }

   default void onChat(BotClient botClient, Component decorate) {
   }

   default void onDisconnected(BotClient botClient, Component literal) {
   }
}

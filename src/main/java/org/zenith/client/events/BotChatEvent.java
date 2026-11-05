package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.network.chat.Component;
import org.zenith.base.bot.client.BotClient;

public class BotChatEvent implements Event {
   private final BotClient botClient;
   private final Component text;

   @Generated
   public BotClient getBot() {
      return this.botClient;
   }

   @Generated
   public Component getText() {
      return this.text;
   }

   @Generated
   public BotChatEvent(BotClient botClient, Component text) {
      this.botClient = botClient;
      this.text = text;
   }
}

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
   public BotChatEvent(BotClient var1, Component var2) {
      this.botClient = var1;
      this.text = var2;
   }
}

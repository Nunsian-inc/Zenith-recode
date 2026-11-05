package org.zenith.base.bot.client;

import com.darkmagician6.eventapi.EventBus;
import com.darkmagician6.eventapi.events.Event;

public final class BotEventBus {
   private final EventBus bus = new EventBus();

   public void register(Object obj) {
      this.bus.register(obj);
   }

   public void register(Object obj, Class<? extends Event> clazz) {
      this.bus.register(obj, clazz);
   }

   public void unregister(Object obj) {
      this.bus.unregister(obj);
   }

   public Event call(Event event) {
      return this.bus.call(event);
   }
}

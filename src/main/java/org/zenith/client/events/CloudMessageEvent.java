package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.network.CloudPackets;

public class CloudMessageEvent implements Event {
   private final CloudPackets.Data13 message;
   private final boolean self;

   public CloudMessageEvent(CloudPackets.Data13 message, boolean self) {
      this.message = message;
      this.self = self;
   }

   public CloudPackets.Data13 getMessage() {
      return this.message;
   }

   public boolean isSelf() {
      return this.self;
   }
}

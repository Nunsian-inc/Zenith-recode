package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.misc.CloudConnectionState;

public class CloudStateEvent implements Event {
   private final CloudConnectionState.Type type;

   public CloudStateEvent(CloudConnectionState.Type type) {
      this.type = type;
   }

   public CloudConnectionState.Type getType() {
      return this.type;
   }

   public boolean method6858() {
      return this.type == CloudConnectionState.Type.ONLINE;
   }
}

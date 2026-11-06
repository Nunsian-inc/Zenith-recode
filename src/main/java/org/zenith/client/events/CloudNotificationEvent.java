package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.network.CloudPackets;

public class CloudNotificationEvent implements Event {
   private final CloudPackets.Data18 notification;

   public CloudNotificationEvent(CloudPackets.Data18 notification) {
      this.notification = notification;
   }

   public CloudPackets.Data18 getNotification() {
      return this.notification;
   }

   public int getKind() {
      return this.notification.kind();
   }
}

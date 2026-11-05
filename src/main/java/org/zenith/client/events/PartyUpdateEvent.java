package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.misc.PartyPlayerInfo;

public class PartyUpdateEvent implements Event {
   private final PartyPlayerInfo partyPlayerInfo;
   private final boolean flag;

   public PartyUpdateEvent(PartyPlayerInfo partyPlayerInfo, boolean flag) {
      this.partyPlayerInfo = partyPlayerInfo;
      this.flag = flag;
   }

   public PartyPlayerInfo getPartyPlayerInfo() {
      return this.partyPlayerInfo;
   }

   public boolean isFlag() {
      return this.flag;
   }
}

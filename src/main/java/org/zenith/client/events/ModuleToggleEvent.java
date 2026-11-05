package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import org.zenith.base.Module;

public class ModuleToggleEvent implements Event {
   private final Module module;
   private final boolean flag;

   @Generated
   public ModuleToggleEvent(Module moduleValue, boolean flag) {
      this.module = moduleValue;
      this.flag = flag;
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public boolean isEnabled() {
      return this.flag;
   }
}

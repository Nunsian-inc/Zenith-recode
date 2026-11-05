package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.client.gui.screens.Screen;

public class SetScreenEvent implements Event {
   private Screen screen;

   @Generated
   public SetScreenEvent(Screen screen) {
      this.screen = screen;
   }

   @Generated
   public Screen getScreen() {
      return this.screen;
   }

   @Generated
   public void setScreen(Screen screen) {
      this.screen = screen;
   }

   @Generated
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof SetScreenEvent event)) {
         return false;
      } else if (!event.method6720(this)) {
         return false;
      } else {
         Screen screen = this.getScreen();
         Screen screen2 = event.getScreen();
         return screen == null ? screen2 == null : screen.equals(screen2);
      }
   }

   @Generated
   public boolean method6720(Object obj) {
      return obj instanceof SetScreenEvent;
   }

   @Generated
   @Override
   public int hashCode() {
      byte b = 59;
      byte b2 = 1;
      Screen screen = this.getScreen();
      return b2 * 59 + (screen == null ? 43 : screen.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "EventSetScreen(screen=" + this.getScreen() + ")";
   }
}

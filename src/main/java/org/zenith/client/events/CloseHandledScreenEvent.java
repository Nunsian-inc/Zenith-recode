package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.client.gui.screens.Screen;
import org.zenith.CancellableEvent;

public class CloseHandledScreenEvent extends CancellableEvent {
   private final Screen screen;

   @Generated
   public CloseHandledScreenEvent(Screen screen) {
      this.screen = screen;
   }
}

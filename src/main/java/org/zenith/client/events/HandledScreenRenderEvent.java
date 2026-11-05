package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.Slot;

public class HandledScreenRenderEvent implements Event {
   private final GuiGraphicsExtractor drawContext;
   private final Slot slot;
   private final int intValue;
   private final int intValue2;

   @Generated
   public GuiGraphicsExtractor getDrawContext() {
      return this.drawContext;
   }

   @Generated
   public Slot getSlot() {
      return this.slot;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public HandledScreenRenderEvent(GuiGraphicsExtractor drawContext, Slot slot, int intValue, int imageHeight) {
      this.drawContext = drawContext;
      this.slot = slot;
      this.intValue = intValue;
      this.intValue2 = imageHeight;
   }
}

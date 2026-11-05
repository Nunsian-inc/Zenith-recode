package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;
import net.minecraft.world.inventory.ContainerInput;

public class ClickSlotEvent extends EventCancellable {
   private int intValue;
   private int intValue2;
   private int intValue3;
   private ContainerInput slotActionType;

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   public ContainerInput getSlotActionType() {
      return this.slotActionType;
   }

   @Generated
   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   @Generated
   public void setIntValue3(int n) {
      this.intValue3 = n;
   }

   @Generated
   public void setSlotActionType(ContainerInput slotActionType) {
      this.slotActionType = slotActionType;
   }

   @Generated
   public ClickSlotEvent(int intValue, int n, int n2, ContainerInput slotActionType) {
      this.intValue = intValue;
      this.intValue2 = n;
      this.intValue3 = n2;
      this.slotActionType = slotActionType;
   }
}

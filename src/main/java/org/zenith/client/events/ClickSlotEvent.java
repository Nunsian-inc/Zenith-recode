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
   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   @Generated
   public void setIntValue2(int var1) {
      this.intValue2 = var1;
   }

   @Generated
   public void setIntValue3(int var1) {
      this.intValue3 = var1;
   }

   @Generated
   public void setSlotActionType(ContainerInput var1) {
      this.slotActionType = var1;
   }

   @Generated
   public ClickSlotEvent(int var1, int var2, int var3, ContainerInput var4) {
      this.intValue = var1;
      this.intValue2 = var2;
      this.intValue3 = var3;
      this.slotActionType = var4;
   }
}

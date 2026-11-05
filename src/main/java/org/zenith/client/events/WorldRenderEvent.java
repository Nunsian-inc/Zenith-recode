package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Generated;

public final class WorldRenderEvent implements Event {
   private final PoseStack matrixStack;
   private final float floatValue;

   @Generated
   public PoseStack getMatrixStack() {
      return this.matrixStack;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public WorldRenderEvent(PoseStack matrixStack, float floatValue) {
      this.matrixStack = matrixStack;
      this.floatValue = floatValue;
   }
}

package org.zenith.base.figura.mixin.render;

import java.util.UUID;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements FiguraEntityRenderStateExtension {
   @Unique
   private UUID figura$entityUuid;
   @Unique
   private int figura$entityId = Integer.MIN_VALUE;
   @Unique
   private float figura$tickDelta;

   @Override
   public UUID figura$getEntityUuid() {
      return this.figura$entityUuid;
   }

   @Override
   public int figura$getEntityId() {
      return this.figura$entityId;
   }

   @Override
   public float figura$getTickDelta() {
      return this.figura$tickDelta;
   }

   @Override
   public void figura$setEntity(UUID uuid, int entityId, float tickDelta) {
      this.figura$entityUuid = uuid;
      this.figura$entityId = entityId;
      this.figura$tickDelta = tickDelta;
   }
}

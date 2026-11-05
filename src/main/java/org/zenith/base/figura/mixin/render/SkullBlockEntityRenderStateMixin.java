package org.zenith.base.figura.mixin.render;

import java.util.UUID;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.base.figura.ducks.FiguraSkullStateExtension;

@Mixin(SkullBlockRenderState.class)
public abstract class SkullBlockEntityRenderStateMixin implements FiguraSkullStateExtension {
   @Unique
   private UUID figura$owner;

   @Override
   public UUID figura$getOwner() {
      return this.figura$owner;
   }

   @Override
   public void figura$setOwner(UUID owner) {
      this.figura$owner = owner;
   }
}

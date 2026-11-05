package dev.porno.playerAnim.mixin;

import dev.porno.playerAnim.impl.IUpperPartHelper;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ModelPart.class)
public class ModelPartMixin implements IUpperPartHelper {
   @Unique
   private boolean playerAnimator$isUpper = false;

   @Override
   public boolean playerAnimator$isUpperPart() {
      return this.playerAnimator$isUpper;
   }

   @Override
   public void playerAnimator$setUpperPart(boolean bl) {
      this.playerAnimator$isUpper = bl;
   }
}

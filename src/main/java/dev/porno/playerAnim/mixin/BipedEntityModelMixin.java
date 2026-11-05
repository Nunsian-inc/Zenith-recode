package dev.porno.playerAnim.mixin;

import dev.porno.playerAnim.core.impl.AnimationProcessor;
import dev.porno.playerAnim.impl.IMutableModel;
import dev.porno.playerAnim.impl.IUpperPartHelper;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends HumanoidRenderState> extends EntityModel<T> implements IMutableModel {
   @Final
   @Shadow
   public ModelPart rightArm;
   @Final
   @Shadow
   public ModelPart leftArm;
   @Unique
   @NotNull
   private AnimationProcessor playerAnimator$animation = new AnimationApplier(null);
   @Final
   @Shadow
   public ModelPart body;
   @Shadow
   @Final
   public ModelPart head;
   @Shadow
   @Final
   public ModelPart hat;

   private BipedEntityModelMixin(Void v, ModelPart modelPart) {
      super(modelPart);
   }

   @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
   private void initBend(ModelPart modelPart, Function<Identifier, RenderType> function, CallbackInfo ci) {
      ((IUpperPartHelper)this.rightArm).playerAnimator$setUpperPart(true);
      ((IUpperPartHelper)this.leftArm).playerAnimator$setUpperPart(true);
      ((IUpperPartHelper)this.head).playerAnimator$setUpperPart(true);
      ((IUpperPartHelper)this.hat).playerAnimator$setUpperPart(true);
   }

   @Override
   public void playerAnimator$setAnimation(@NotNull AnimationProcessor emoteSupplier) {
      this.playerAnimator$animation = emoteSupplier;
   }

   @NotNull
   @Override
   public AnimationProcessor playerAnimator$getAnimation() {
      return this.playerAnimator$animation;
   }
}

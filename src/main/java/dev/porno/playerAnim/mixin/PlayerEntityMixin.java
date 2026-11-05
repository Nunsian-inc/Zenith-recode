package dev.porno.playerAnim.mixin;

import dev.porno.playerAnim.api.layered.AnimationStack;
import dev.porno.playerAnim.api.layered.IAnimation;
import dev.porno.playerAnim.impl.IAnimatedPlayer;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import dev.porno.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.porno.playerAnim.minecraftApi.PlayerAnimationFactory;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin implements IAnimatedPlayer {
   @Unique
   private final Map<Identifier, IAnimation> playerAnimator$modAnimationData = new HashMap<>();
   @Unique
   private final AnimationStack playerAnimator$animationStack = this.playerAnimator$createAnimationStack();
   @Unique
   private final AnimationApplier playerAnimator$animationApplier = new AnimationApplier(this.playerAnimator$animationStack);

   @Unique
   private AnimationStack playerAnimator$createAnimationStack() {
      AnimationStack stack = new AnimationStack();
      if (AbstractClientPlayer.class.isInstance(this)) {
         PlayerAnimationFactory.ANIMATION_DATA_FACTORY.prepareAnimations((AbstractClientPlayer)(Object)this, stack, this.playerAnimator$modAnimationData);
         PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.invoker().registerAnimation((AbstractClientPlayer)(Object)this, stack);
      }

      return stack;
   }

   @NotNull
   @Override
   public AnimationStack playerAnimator$getAnimationStack() {
      return this.playerAnimator$animationStack;
   }

   @Override
   public AnimationApplier playerAnimator_getAnimation() {
      return this.playerAnimator$animationApplier;
   }

   @Nullable
   @Override
   public IAnimation playerAnimator_getAnimation(@NotNull Identifier id) {
      return this.playerAnimator$modAnimationData.get(id);
   }

   @Nullable
   @Override
   public IAnimation playerAnimator_setAnimation(@NotNull Identifier id, @Nullable IAnimation animation) {
      return animation == null ? this.playerAnimator$modAnimationData.remove(id) : this.playerAnimator$modAnimationData.put(id, animation);
   }

   @Inject(method = "tick()V", at = @At("HEAD"))
   private void tick(CallbackInfo ci) {
      if (AbstractClientPlayer.class.isInstance(this)) {
         this.playerAnimator$animationStack.tick();
      }
   }
}

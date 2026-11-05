package dev.porno.playerAnim.mixin.firstPerson;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.core.impl.AnimationProcessor;
import dev.porno.playerAnim.core.util.Vec3f;
import dev.porno.playerAnim.impl.IAnimatedPlayer;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class ItemInHandRendererMixin {
   @Inject(
      method = "submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void disableDefaultItemIfNeeded(
      float f, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, net.minecraft.client.renderer.state.level.PlayerRenderState playerState,
      net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState handsState, CallbackInfo ci
   ) {
      LocalPlayer localPlayer = net.minecraft.client.Minecraft.getInstance().player;
      if (localPlayer instanceof IAnimatedPlayer player && player.playerAnimator_getAnimation().getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) {
         ci.cancel();
      }
   }

   // 26.3: held items are pre-resolved render states; adjust the pose right before each one is submitted
   @Inject(
      method = "submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V")
   )
   void changeItemLocation(
      net.minecraft.client.renderer.state.level.PlayerRenderState playerState,
      net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState handsState,
      float partialTicks,
      float xRot,
      net.minecraft.world.InteractionHand hand,
      float attack,
      ItemStack itemStack,
      float inverseArmHeight,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int i,
      CallbackInfo ci
   ) {
      LivingEntity livingEntity = net.minecraft.client.Minecraft.getInstance().player;
      net.minecraft.client.renderer.entity.state.AvatarRenderState avatarState = playerState.avatarRenderState;
      if (livingEntity instanceof IAnimatedPlayer player && avatarState != null) {
         boolean leftHand = (hand == net.minecraft.world.InteractionHand.MAIN_HAND ? avatarState.mainArm : avatarState.mainArm.getOpposite())
            == net.minecraft.world.entity.HumanoidArm.LEFT;
         AnimationApplier animation = player.playerAnimator_getAnimation();
         if (animation.isActive() && !animation.isFirstPersonAnimationDisabled()) {
            AnimationProcessor anim = player.playerAnimator_getAnimation();
            PartKey key = leftHand ? PartKey.LEFT_ITEM : PartKey.RIGHT_ITEM;
            Vec3f scale = anim.get3DTransform(key, TransformType.SCALE, new Vec3f(1.0F, 1.0F, 1.0F));
            Vec3f rot = anim.get3DTransform(key, TransformType.ROTATION, Vec3f.ZERO);
            Vec3f pos = anim.get3DTransform(key, TransformType.POSITION, Vec3f.ZERO).scale(0.0625F);
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            poseStack.rotate(Axis.ZP.rotation(rot.getZ()));
            poseStack.rotate(Axis.YP.rotation(rot.getY()));
            poseStack.rotate(Axis.XP.rotation(rot.getX()));
         }
      }
   }
}

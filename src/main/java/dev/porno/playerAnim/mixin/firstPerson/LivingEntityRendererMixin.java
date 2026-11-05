package dev.porno.playerAnim.mixin.firstPerson;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingEntityRenderer.class, priority = 2000)
public class LivingEntityRendererMixin {
   @Shadow
   @Final
   protected List<Object> layers;

   @Redirect(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;layers:Ljava/util/List;", opcode = 180)
   )
   private List<Object> filterLayers(
      LivingEntityRenderer<?, ?, ?> instance,
      LivingEntityRenderState entityRenderState,
      PoseStack poseStack,
      SubmitNodeCollector collector,
      CameraRenderState camera
   ) {
      if (entityRenderState instanceof IPlayerAnimationState state && state.playerAnimator$isFirstPersonPass()) {
         boolean showArmor = state.playerAnimator$getAnimationApplier().getFirstPersonConfiguration().isShowArmor();
         return this.layers.stream().filter(layer -> layer instanceof PlayerItemInHandLayer || showArmor && layer instanceof HumanoidArmorLayer).toList();
      } else {
         return this.layers;
      }
   }
}

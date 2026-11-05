package dev.porno.playerAnim.mixin;

import dev.porno.playerAnim.impl.IUpperPartHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>>
   extends RenderLayer<S, M> {
   protected ArmorFeatureRendererMixin(RenderLayerParent<S, M> renderLayerParent) {
      super(renderLayerParent);
   }

   @Inject(
      method = "<init>(Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/renderer/entity/ArmorModelSet;Lnet/minecraft/client/renderer/entity/ArmorModelSet;Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;)V",
      at = @At("RETURN")
   )
   private void initInject(
      RenderLayerParent<S, M> renderLayerParent,
      ArmorModelSet<A> modelSet,
      ArmorModelSet<A> babyModelSet,
      EquipmentLayerRenderer equipmentLayerRenderer,
      CallbackInfo ci
   ) {
      ((IUpperPartHelper)this).playerAnimator$setUpperPart(false);
   }
}

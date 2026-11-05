package org.zenith.utility.mixin.render;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.WardrobeRenderer;

@Mixin({PlayerModel.class})
public class MixinPlayerEntityModel {
   @Unique
   private boolean zenith$hidden;

   @Inject(
      method = {"setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$characterSkin(AvatarRenderState state, CallbackInfo callbackInfo) {
      PlayerModel playerModel = (PlayerModel)(Object)this;
      boolean flag = WardrobeRenderer.method6687(playerModel, state);
      if (flag) {
         zenith$visible(playerModel, false);
         this.zenith$hidden = true;
      } else if (this.zenith$hidden) {
         playerModel.head.visible = true;
         this.zenith$hidden = false;
      }
   }

   @Unique
   private static void zenith$visible(PlayerModel playerModel, boolean visible) {
      for (ModelPart modelPart : new ModelPart[]{playerModel.head, playerModel.body, playerModel.rightArm, playerModel.leftArm, playerModel.rightLeg, playerModel.leftLeg}) {
         modelPart.visible = visible;
      }
   }
}

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
   private void zenith$characterSkin(AvatarRenderState var1, CallbackInfo var2) {
      PlayerModel var3 = (PlayerModel)(Object)this;
      boolean var4 = WardrobeRenderer.method6687(var3, var1);
      if (var4) {
         zenith$visible(var3, false);
         this.zenith$hidden = true;
      } else if (this.zenith$hidden) {
         var3.head.visible = true;
         this.zenith$hidden = false;
      }
   }

   @Unique
   private static void zenith$visible(PlayerModel var0, boolean var1) {
      for (ModelPart var5 : new ModelPart[]{var0.head, var0.body, var0.rightArm, var0.leftArm, var0.rightLeg, var0.leftLeg}) {
         var5.visible = var1;
      }
   }
}

package org.zenith.base.figura.mixin.render;

import java.util.List;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.base.figura.ducks.PlayerModelCapeAccessor;

@Mixin(PlayerCapeModel.class)
public abstract class PlayerCapeModelMixin<T extends AvatarRenderState> extends HumanoidModel<T> implements PlayerModelCapeAccessor {
   @Shadow
   @Final
   private ModelPart cape;
   @Unique
   private final ModelPart figura$fakeCloak = new ModelPart(List.of(), Map.of());

   protected PlayerCapeModelMixin(ModelPart root) {
      super(root);
   }

   @Override
   public ModelPart figura$getCloak() {
      return this.cape;
   }

   @Override
   public ModelPart figura$getFakeCloak() {
      return this.figura$fakeCloak;
   }
}

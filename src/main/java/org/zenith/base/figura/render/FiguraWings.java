package org.zenith.base.figura.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.equipment.ElytraModel;
import org.zenith.base.figura.mixin.render.layers.elytra.ElytraModelAccessor;

public final class FiguraWings {
   private FiguraWings() {
   }

   public static FiguraModelEdits.Edit single(ElytraModel model, boolean left) {
      final ElytraModelAccessor accessor = (ElytraModelAccessor)model;
      return new FiguraModelEdits.Edit() {
         @Override
         public void apply() {
            accessor.getLeftWing().visible = left;
            accessor.getRightWing().visible = !left;
         }

         @Override
         public void restore() {
            ModelPart leftWing = accessor.getLeftWing();
            ModelPart rightWing = accessor.getRightWing();
            leftWing.visible = true;
            rightWing.visible = true;
         }
      };
   }

   public interface Submit {
      void submit(PoseStack pivotStack);
   }
}

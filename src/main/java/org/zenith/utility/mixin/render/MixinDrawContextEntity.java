package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiGraphicsExtractor.class})
public abstract class MixinDrawContextEntity {
   @Unique
   private boolean zenith$entityTransformed;

   @Inject(
      method = {"entity(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLorg/joml/Vector3fc;Lorg/joml/Quaternionfc;Lorg/joml/Quaternionfc;IIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$followPose(
      EntityRenderState renderState, float f, org.joml.Vector3fc translation, org.joml.Quaternionfc rotation, org.joml.Quaternionfc overrideCameraAngle, int x, int y, int n, int y2, CallbackInfo callbackInfo
   ) {
      if (!this.zenith$entityTransformed) {
         GuiGraphicsExtractor guiGraphicsExtractor = (GuiGraphicsExtractor)(Object)this;
         Matrix3x2f matrix3x2f = new Matrix3x2f(guiGraphicsExtractor.pose());
         float f2 = (float)Math.sqrt(matrix3x2f.m00 * matrix3x2f.m00 + matrix3x2f.m01 * matrix3x2f.m01);
         float f3 = (float)Math.sqrt(matrix3x2f.m10 * matrix3x2f.m10 + matrix3x2f.m11 * matrix3x2f.m11);
         boolean flag = Math.abs(f2 - 1.0F) < 0.001F && Math.abs(f3 - 1.0F) < 0.001F && Math.abs(matrix3x2f.m20) < 0.01F && Math.abs(matrix3x2f.m21) < 0.01F;
         if (!flag) {
            Vector2f transformPosition = matrix3x2f.transformPosition(new Vector2f(x, y));
            Vector2f vec = matrix3x2f.transformPosition(new Vector2f(n, y2));
            int x0 = Math.round(Math.min(transformPosition.x, vec.x));
            int y0 = Math.round(Math.min(transformPosition.y, vec.y));
            int x1 = Math.round(Math.max(transformPosition.x, vec.x));
            int y1 = Math.round(Math.max(transformPosition.y, vec.y));
            if (x1 - x0 >= 1 && y1 - y0 >= 1) {
               float f4 = (f2 + f3) / 2.0F;
               this.zenith$entityTransformed = true;
               guiGraphicsExtractor.pose().pushMatrix();

               try {
                  guiGraphicsExtractor.pose().identity();
                  guiGraphicsExtractor.entity(renderState, f * f4, translation, rotation, overrideCameraAngle, x0, y0, x1, y1);
               } finally {
                  guiGraphicsExtractor.pose().popMatrix();
                  this.zenith$entityTransformed = false;
               }

               callbackInfo.cancel();
            } else {
               callbackInfo.cancel();
            }
         }
      }
   }
}

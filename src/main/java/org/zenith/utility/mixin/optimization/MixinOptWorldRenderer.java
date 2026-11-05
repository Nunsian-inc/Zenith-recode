package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.zenith.utility.misc.Util29;

// 26.3 moved entity extraction from LevelRenderer to LevelExtractor (one instance, owned by Minecraft).
// The outline-clear/blit skips were dropped: they hooked a 1.21.11 lambda that no longer exists.
@Mixin({LevelExtractor.class})
public class MixinOptWorldRenderer {
   @WrapMethod(
      method = {"extractVisibleEntities(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;Lnet/minecraft/client/DeltaTracker;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V"}
   )
   private void zenith$markEntityStates(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, Operation<Void> operation) {
      boolean flag = Util29.flag;
      Util29.flag = true;

      try {
         operation.call(new Object[]{camera, frustum, deltaTracker, levelRenderState});
      } finally {
         Util29.flag = flag;
      }
   }
}

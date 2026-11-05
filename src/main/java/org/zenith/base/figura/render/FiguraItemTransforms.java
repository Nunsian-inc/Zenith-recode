package org.zenith.base.figura.render;

import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.mixin.render.ItemRenderStateAccessor;
import org.zenith.base.figura.mixin.render.LayerRenderStateAccessor;

public final class FiguraItemTransforms {
   private FiguraItemTransforms() {
   }

   public static ItemTransform of(ItemStackRenderState state) {
      ItemRenderStateAccessor accessor = (ItemRenderStateAccessor)state;
      return accessor.figura$getLayerCount() <= 0
         ? ItemTransform.NO_TRANSFORM
         : ((LayerRenderStateAccessor)accessor.figura$getLayers()[0]).figura$getTransform();
   }

   public static FiguraVec3 pos(ItemTransform transform) {
      return FiguraVec3.of(transform.translation().x(), transform.translation().y(), transform.translation().z());
   }

   public static FiguraVec3 rot(ItemTransform transform) {
      return FiguraVec3.of(transform.rotation().z(), transform.rotation().y(), transform.rotation().x());
   }

   public static FiguraVec3 scale(ItemTransform transform) {
      return FiguraVec3.of(transform.scale().x(), transform.scale().y(), transform.scale().z());
   }
}

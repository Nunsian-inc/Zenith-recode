package org.zenith.utility.misc;

import net.minecraft.client.renderer.culling.Frustum;
import org.jetbrains.annotations.Nullable;

public final class FrustumHolder {
   @Nullable
   private static volatile Frustum frustum;

   private FrustumHolder() {
   }

   public static void method3035(@Nullable Frustum cullFrustum) {
      frustum = cullFrustum;
   }

   @Nullable
   public static Frustum getFrustum() {
      return frustum;
   }
}

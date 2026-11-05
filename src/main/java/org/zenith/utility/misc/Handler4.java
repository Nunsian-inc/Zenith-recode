package org.zenith.utility.misc;

import java.util.List;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.Nullable;

public interface Handler4 {
   @Nullable
   String zenith$profileName();

   void zenith$setProfileName(@Nullable String var1);

   @Nullable
   List<HoveredData> zenith$wardrobeLoadout();

   void zenith$setWardrobeLoadout(@Nullable List<HoveredData> var1);

   @Nullable
   static String method3142(AvatarRenderState var0) {
      return ((Handler4)var0).zenith$profileName();
   }
}

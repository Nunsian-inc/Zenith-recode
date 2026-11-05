package org.zenith.utility.misc;

import java.util.List;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.Nullable;

public interface Handler4 {
   @Nullable
   String zenith$profileName();

   void zenith$setProfileName(@Nullable String name);

   @Nullable
   List<HoveredData> zenith$wardrobeLoadout();

   void zenith$setWardrobeLoadout(@Nullable List<HoveredData> hoveredDatas);

   @Nullable
   static String method3142(AvatarRenderState state) {
      return ((Handler4)state).zenith$profileName();
   }
}

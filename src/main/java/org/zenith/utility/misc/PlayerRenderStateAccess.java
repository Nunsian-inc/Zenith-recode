package org.zenith.utility.misc;

import java.util.List;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.Nullable;

public interface PlayerRenderStateAccess {
   @Nullable
   String zenith$profileName();

   void zenith$setProfileName(@Nullable String name);

   @Nullable
   List<HoveredData> zenith$wardrobeLoadout();

   void zenith$setWardrobeLoadout(@Nullable List<HoveredData> wardrobeLoadout);

   @Nullable
   static String getProfileName(AvatarRenderState state) {
      return ((PlayerRenderStateAccess)state).zenith$profileName();
   }
}

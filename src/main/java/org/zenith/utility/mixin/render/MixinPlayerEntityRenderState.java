package org.zenith.utility.mixin.render;

import java.util.List;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.PlayerRenderStateAccess;
import org.zenith.utility.misc.HoveredData;

@Mixin({AvatarRenderState.class})
public class MixinPlayerEntityRenderState implements PlayerRenderStateAccess {
   @Unique
   private String zenith$profileName;
   @Unique
   private List<HoveredData> zenith$wardrobeLoadout;

   @Override
   public String zenith$profileName() {
      return this.zenith$profileName;
   }

   @Override
   public void zenith$setProfileName(String zenith$profileName) {
      this.zenith$profileName = zenith$profileName;
   }

   @Override
   public List<HoveredData> zenith$wardrobeLoadout() {
      return this.zenith$wardrobeLoadout;
   }

   @Override
   public void zenith$setWardrobeLoadout(List<HoveredData> zenith$wardrobeLoadout) {
      this.zenith$wardrobeLoadout = zenith$wardrobeLoadout;
   }
}

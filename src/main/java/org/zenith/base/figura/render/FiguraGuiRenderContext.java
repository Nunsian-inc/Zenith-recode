package org.zenith.base.figura.render;

import net.minecraft.world.entity.Entity;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.model.rendering.EntityRenderMode;

public final class FiguraGuiRenderContext {
   private FiguraGuiRenderContext() {
   }

   public static void mark(Entity player, EntityRenderMode renderMode) {
      if (player != null && renderMode != null) {
         for (Avatar avatar : AvatarManager.getAvatars(player.getUUID())) {
            avatar.renderMode = renderMode;
         }

         for (Avatar weaponAvatar : AvatarManager.getWeaponAvatars(player.getUUID())) {
            weaponAvatar.renderMode = renderMode;
         }
      }
   }
}

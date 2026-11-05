package org.zenith.base.figura.render;

import net.minecraft.world.entity.Entity;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.model.rendering.EntityRenderMode;

public final class FiguraGuiRenderContext {
   private FiguraGuiRenderContext() {
   }

   public static void mark(Entity var0, EntityRenderMode var1) {
      if (var0 != null && var1 != null) {
         for (Avatar var3 : AvatarManager.getAvatars(var0.getUUID())) {
            var3.renderMode = var1;
         }

         for (Avatar var5 : AvatarManager.getWeaponAvatars(var0.getUUID())) {
            var5.renderMode = var1;
         }
      }
   }
}

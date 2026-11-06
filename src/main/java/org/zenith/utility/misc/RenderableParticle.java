package org.zenith.utility.misc;

import net.minecraft.world.phys.Vec3;
import org.zenith.ZenithColor;

public interface RenderableParticle {
   Vec3 getPosition();

   Vec3 getPreviousPosition();

   float getSize();

   float getAlpha();

   default float getInterpolatedAlpha(float partialTick) {
      return this.getAlpha();
   }

   ZenithColor getColor();

   String getTextureKey();

   float getRotationDegrees();

   boolean isExpired();
}

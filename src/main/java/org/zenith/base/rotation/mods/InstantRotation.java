package org.zenith.base.rotation.mods;

import org.zenith.Rotation;
import org.zenith.utility.misc.RotationContext;

public class InstantRotation extends RotationContext {
   public Rotation method3534(Rotation rotation) {
      return rotationManager.getEffectiveRotation().add(rotationManager.getEffectiveRotation().wrappedDeltaTo(rotation));
   }
}

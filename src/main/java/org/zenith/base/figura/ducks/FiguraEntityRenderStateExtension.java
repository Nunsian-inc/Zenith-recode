package org.zenith.base.figura.ducks;

import java.util.UUID;

public interface FiguraEntityRenderStateExtension {
   UUID figura$getEntityUuid();

   int figura$getEntityId();

   float figura$getTickDelta();

   void figura$setEntity(UUID var1, int var2, float var3);
}

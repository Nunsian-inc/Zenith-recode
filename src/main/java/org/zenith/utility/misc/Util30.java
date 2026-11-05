package org.zenith.utility.misc;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public final class Util30 {
   private Util30() {
   }

   public interface Handler {
      MapAtlas zenith$getAtlasPool();

      TextureManager zenith$getTextureManager();

      @Nullable
      MapAtlas.Data zenith$getAtlasSlot(int n);
   }

   public interface Handler2 {
      @Nullable
      MapAtlas.Data zenith$getAtlasSlot();
   }

   public interface Handler3 {
      void zenith$setAtlasSlot(@Nullable MapAtlas.Data data);

      @Nullable
      MapAtlas.Data zenith$getAtlasSlot();

      @Nullable
      default MapAtlas.Data method7747(@Nullable Identifier id) {
         MapAtlas.Data zenith$getAtlasSlot = this.zenith$getAtlasSlot();
         return zenith$getAtlasSlot != null && zenith$getAtlasSlot.method6153().equals(id) ? zenith$getAtlasSlot : null;
      }
   }
}

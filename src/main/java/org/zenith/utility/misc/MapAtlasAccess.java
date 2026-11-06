package org.zenith.utility.misc;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public final class MapAtlasAccess {
   private MapAtlasAccess() {
   }

   public interface Handler {
      MapAtlas zenith$getAtlasPool();

      TextureManager zenith$getTextureManager();

      @Nullable
      MapAtlas.Data zenith$getAtlasSlot(int mapId);
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
      default MapAtlas.Data getMatchingAtlasSlot(@Nullable Identifier id) {
         MapAtlas.Data atlasSlot = this.zenith$getAtlasSlot();
         return atlasSlot != null && atlasSlot.method6153().equals(id) ? atlasSlot : null;
      }
   }
}

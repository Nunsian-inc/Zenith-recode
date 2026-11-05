package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.utility.render.MapAtlasPage;

public final class MapAtlas {
   private final List<MapAtlasPage> items = new ArrayList<>();

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Nullable
   public MapAtlas.Data method6151(TextureManager zenith$getTextureManager) {
      MapAtlasPage mapAtlasPage = this.items.isEmpty() ? null : this.items.get(this.items.size() - 1);
      if (mapAtlasPage == null || mapAtlasPage.isFull()) {
         try {
            mapAtlasPage = new MapAtlasPage(zenith$getTextureManager);
         } catch (Throwable e) {
            return null;
         }

         this.items.add(mapAtlasPage);
      }

      int n = mapAtlasPage.method5416();
      if (n < 0) {
         return null;
      } else {
         int intValue = n % 16 * 128;
         int n2 = n / 16 * 128;
         return new MapAtlas.Data(mapAtlasPage, intValue, n2);
      }
   }

   public void method6152(TextureManager textureManager) {
      for (MapAtlasPage mapAtlasPage : this.items) {
         mapAtlasPage.method5418(textureManager);
      }

      this.items.clear();
   }

   public static final class Data {
      private final MapAtlasPage mapAtlasPage;
      private final int intValue;
      private final int intValue2;

      public MapAtlasPage mapAtlasPage() {
         return this.mapAtlasPage;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.mapAtlasPage, other.mapAtlasPage) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(mapAtlasPage, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "mapAtlasPage=" + this.mapAtlasPage + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(MapAtlasPage mapAtlasPage, int intValue, int n) {
         this.mapAtlasPage = mapAtlasPage;
         this.intValue = intValue;
         this.intValue2 = n;
      }

      public Identifier method6153() {
         return this.mapAtlasPage.getIdentifier();
      }

      public float method6154() {
         return this.intValue / 2048.0F;
      }

      public float method6155() {
         return this.intValue2 / 2048.0F;
      }

      public MapAtlasPage getMapAtlasPage() {
         return this.mapAtlasPage;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}

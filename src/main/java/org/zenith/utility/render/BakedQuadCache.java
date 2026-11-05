package org.zenith.utility.render;

import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction.Axis;
import org.joml.Vector3fc;

public final class BakedQuadCache {
   private static final float CONST_floatValue = 0.0626F;
   private static final int CONST_intValue = 8;
   private static final Map<BakedQuad, BakedQuadCache.Data> items = new MapMaker().weakKeys().concurrencyLevel(1).makeMap();

   private BakedQuadCache() {
   }

   public static List<BakedQuad> method5533(List<BakedQuad> bakedQuads) {
      int size = bakedQuads.size();
      if (size < 8) {
         return bakedQuads;
      } else {
         BakedQuad bakedQuad = (BakedQuad)bakedQuads.get(0);
         BakedQuad bakedQuad2 = (BakedQuad)bakedQuads.get(size - 1);
         BakedQuadCache.Data data = items.get(bakedQuad2);
         if (data == null || data.intValue != size || data.bakedQuad != bakedQuad) {
            data = new BakedQuadCache.Data(bakedQuad, size, method5534(bakedQuads));
            items.put(bakedQuad2, data);
         }

         return data.items == null ? bakedQuads : data.items;
      }
   }

   private static List<BakedQuad> method5534(List<BakedQuad> bakedQuads) {
      int initialCapacity = 0;
      int n = 0;

      for (int i = bakedQuads.size(); n < i; n++) {
         BakedQuad bakedQuad = (BakedQuad)bakedQuads.get(n);
         if (bakedQuad.direction().getAxis() == Axis.Z) {
            initialCapacity++;
         } else if (!method5535(bakedQuad)) {
            return null;
         }
      }

      if (initialCapacity != 0 && initialCapacity != bakedQuads.size()) {
         List<BakedQuad> coll = new ArrayList<>(initialCapacity);
         int n2 = 0;

         for (int i = bakedQuads.size(); n2 < i; n2++) {
            BakedQuad bakedQuad = (BakedQuad)bakedQuads.get(n2);
            if (bakedQuad.direction().getAxis() == Axis.Z) {
               coll.add(bakedQuad);
            }
         }

         return List.copyOf(coll);
      } else {
         return null;
      }
   }

   private static boolean method5535(BakedQuad bakedQuad) {
      float f = Float.MAX_VALUE;
      float f2 = -Float.MAX_VALUE;

      for (int i = 0; i < 4; i++) {
         Vector3fc position = bakedQuad.position(i);
         f = Math.min(f, position.z());
         f2 = Math.max(f2, position.z());
      }

      return f2 - f <= 0.0626F;
   }

   private static final class Data {
      private final BakedQuad bakedQuad;
      private final int intValue;
      private final List<BakedQuad> items;

      public BakedQuad bakedQuad() {
         return this.bakedQuad;
      }

      public int intValue() {
         return this.intValue;
      }

      public List<BakedQuad> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.bakedQuad, other.bakedQuad) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bakedQuad, intValue, items);
      }

      @Override
      public String toString() {
         return "Data[" + "bakedQuad=" + this.bakedQuad + ", " + "intValue=" + this.intValue + ", " + "items=" + this.items + "]";
      }

      private Data(BakedQuad bakedQuad, int intValue, List<BakedQuad> items) {
         this.bakedQuad = bakedQuad;
         this.intValue = intValue;
         this.items = items;
      }

      public BakedQuad getBakedQuad() {
         return this.bakedQuad;
      }

      public int size() {
         return this.intValue;
      }

      public List<BakedQuad> getItems() {
         return this.items;
      }
   }
}

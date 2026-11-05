package org.zenith.utility.misc;

import java.util.HashMap;
import java.util.Map;

public final class AnimationCache {
   private static final long CONST_longValue = 10000000000L;
   private static final long CONST_longValue2 = 5000000000L;
   private static final Map<Long, AnimationCache.Entry> items = new HashMap<>();
   private static long lastPrune;

   private AnimationCache() {
   }

   public static AnimationCache.Entry method7621(int id, HoveredData hoveredData, boolean flag) {
      return method7622(id, hoveredData.getText(), flag);
   }

   public static AnimationCache.Entry method7622(int id, String text, boolean flag) {
      long nanoTime = System.nanoTime();
      method7623(nanoTime);
      long key = (long)id << 32 ^ text.hashCode() & 4294967295L ^ (flag ? 6510615553911029760L : 0L);
      AnimationCache.Entry entry = items.computeIfAbsent(key, var0x -> new AnimationCache.Entry());
      entry.longValue = nanoTime;
      return entry;
   }

   public static void clear() {
      items.clear();
   }

   private static void method7623(long nanoTime) {
      if (nanoTime - lastPrune >= 5000000000L) {
         lastPrune = nanoTime;
         items.entrySet().removeIf(entry -> nanoTime - entry.getValue().longValue > 10000000000L);
      }
   }

   public static final class Entry {
      public final AnimationState animationState = new AnimationState();
      public final Class16 class16 = new Class16();
      public final Class2 class2 = new Class2();
      public float[] floatValues = new float[0];
      public float floatValue;
      public long longValue;
      public long longValue2;
      public float floatValue2 = Float.NaN;
      public float floatValue3;

      public float method7624(long nanoTime) {
         float f = this.longValue2 == 0L ? 0.0F : (float)(nanoTime - this.longValue2) / 1.0E9F;
         this.longValue2 = nanoTime;
         return Math.max(0.0F, Math.min(f, 0.25F));
      }

      public float[] method7625(int size) {
         if (this.floatValues.length != size) {
            this.floatValues = new float[size];
         }

         return this.floatValues;
      }
   }
}

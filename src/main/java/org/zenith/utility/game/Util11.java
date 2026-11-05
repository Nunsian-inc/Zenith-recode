package org.zenith.utility.game;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.zenith.utility.misc.FlakeUtil;

public final class Util11 {
   private static final long CONST_longValue = 15000L;
   private static final int CONST_intValue = 12;
   public static final long CONST_longValue2 = 900L;
   private static final int CONST_intValue2 = 96;
   private static final int CONST_intValue3 = 4096;
   private static final Long2ObjectOpenHashMap<Util11.Entry> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   private static final List<Util11.Data> items = new ArrayList<>();
   private static final LongArrayList longArrayList = new LongArrayList();
   private static long longValue;

   private Util11() {
   }

   public static void method2330(Level level, BlockPos pos, BlockState blockState, BlockState blockState2) {
      boolean flag = !blockState.isAir();
      boolean flag2 = !blockState2.isAir();
      if (flag != flag2) {
         int x = pos.getX();
         int z = pos.getZ();
         if (level.hasChunk(x >> 4, z >> 4)) {
            int height = level.getHeight(Types.MOTION_BLOCKING, x, z);
            long now = System.currentTimeMillis();
            if (flag2) {
               if (height != pos.getY() + 1) {
                  return;
               }

               Util11.Entry entry = method2338(x, z);
               entry.longValue = now;
               entry.doubleValue = height;
               longValue++;
            } else {
               if (height > pos.getY()) {
                  return;
               }

               float f = FlakeUtil.method7509(x + 0.5, z + 0.5);
               if (f <= 0.02F) {
                  long2ObjectOpenHashMap.remove(method2339(x, z));
                  longArrayList.add(method2339(x, z));
                  return;
               }

               long2ObjectOpenHashMap.remove(method2339(x, z));
               if (items.size() >= 96) {
                  items.remove(0);
               }

               items.add(new Util11.Data(x, z, pos.getY() + 1 + f, height, now));
               longValue++;
            }

            longArrayList.add(method2339(x, z));
         }
      }
   }

   public static void method2331(long now) {
      if (!items.isEmpty()) {
         items.removeIf(data -> now - data.getLongValue() > 900L);
      }

      if (!long2ObjectOpenHashMap.isEmpty()) {
         long2ObjectOpenHashMap.values().removeIf(entry -> {
            if (now - entry.longValue < 15000L) {
               return false;
            } else {
               longValue++;
               return true;
            }
         });
      }
   }

   public static void method2332(Level level, FlakeState flakeState) {
      if (!longArrayList.isEmpty()) {
         for (int i = 0; i < longArrayList.size(); i++) {
            long z = longArrayList.getLong(i);
            flakeState.method4538(level, (int)(z >> 32), (int)z);
         }

         longArrayList.clear();
      }
   }

   public static List<Util11.Data> getItems() {
      return items;
   }

   public static long method2333(long now) {
      long l = longValue;
      ObjectIterator objectIterator = long2ObjectOpenHashMap.values().iterator();

      while (objectIterator.hasNext()) {
         Util11.Entry entry = (Util11.Entry)objectIterator.next();
         l = l * 31L + method2337(entry, now);
      }

      return l;
   }

   public static float method2334(int x, int z, double d, long now) {
      if (long2ObjectOpenHashMap.isEmpty()) {
         return 1.0F;
      } else {
         Util11.Entry entry = (Util11.Entry)long2ObjectOpenHashMap.get(method2339(x, z));
         if (entry == null) {
            return 1.0F;
         } else {
            return Math.abs(entry.doubleValue - d) > 0.51 ? 1.0F : Math.clamp(method2337(entry, now) / 12.0F, 0.0F, 1.0F);
         }
      }
   }

   public static boolean method2335() {
      return !long2ObjectOpenHashMap.isEmpty();
   }

   public static void method2336() {
      long2ObjectOpenHashMap.clear();
      items.clear();
      longArrayList.clear();
      longValue++;
   }

   private static int method2337(Util11.Entry entry, long now) {
      long l = now - entry.longValue;
      if (l <= 0L) {
         return 0;
      } else if (l >= 15000L) {
         return 12;
      } else {
         float f = (float)l / 15000.0F;
         return (int)(f * f * 12.0F);
      }
   }

   private static Util11.Entry method2338(int x, int z) {
      long l = method2339(x, z);
      Util11.Entry entry = (Util11.Entry)long2ObjectOpenHashMap.get(l);
      if (entry != null) {
         return entry;
      } else {
         if (long2ObjectOpenHashMap.size() >= 4096) {
            long2ObjectOpenHashMap.clear();
         }

         entry = new Util11.Entry();
         long2ObjectOpenHashMap.put(l, entry);
         return entry;
      }
   }

   private static long method2339(int x, int z) {
      return (long)x << 32 | z & 4294967295L;
   }

   public static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final double doubleValue4;
      private final long longValue;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public double doubleValue4() {
         return this.doubleValue4;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.doubleValue4, other.doubleValue4) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, doubleValue4, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "doubleValue4=" + this.doubleValue4 + ", " + "longValue=" + this.longValue + "]";
      }

      public Data(double doubleValue, double z, double d, double height, long longValue) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = z;
         this.doubleValue3 = d;
         this.doubleValue4 = height;
         this.longValue = longValue;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public double getDoubleValue4() {
         return this.doubleValue4;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private long longValue;
      private double doubleValue;
   }
}

package org.zenith.utility.game;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.zenith.utility.misc.Class32;

public final class SnowStats {
   public static final int CONST_intValue = 24;
   public static final int CONST_intValue2 = 48;

   private SnowStats() {
   }

   public static SnowStats.Entry method803(Level level, Class32 class2, int blockX, int blockY, int blockZ, int integer, float f) {
      SnowStats.Entry entry = new SnowStats.Entry();
      if (level != null && class2 != null) {
         int n = Math.max(1, Math.round(f * 4.0F));
         MutableBlockPos mutablePos = new MutableBlockPos();

         for (int i = -integer; i <= integer; i++) {
            for (int j = -integer; j <= integer; j++) {
               if (i * i + j * j <= integer * integer) {
                  int x = blockX + i;
                  int z = blockZ + j;
                  entry.intValue++;
                  int n2 = method804(level, mutablePos, x, blockY, z);
                  if (n2 == Integer.MIN_VALUE) {
                     entry.intValue3++;
                  } else {
                     method805(class2, x, n2, z, n);
                     entry.intValue2++;
                  }
               }
            }
         }

         entry.intValue4 = class2.method4910();
         return entry;
      } else {
         return entry;
      }
   }

   private static int method804(Level level, MutableBlockPos mutablePos, int x, int blockY, int z) {
      int n = blockY + 24;
      int n2 = Math.max(level.getMinY(), blockY - 48);
      boolean air = false;

      for (int i = n; i >= n2; i--) {
         mutablePos.set(x, i, z);
         if (!level.hasChunk(x >> 4, z >> 4)) {
            return Integer.MIN_VALUE;
         }

         BlockState blockState = level.getBlockState(mutablePos);
         boolean flag = !blockState.isAir() && blockState.getFluidState().isEmpty() && blockState.isFaceSturdy(level, mutablePos, Direction.UP);
         if (flag && air) {
            return i;
         }

         air = blockState.isAir();
      }

      return Integer.MIN_VALUE;
   }

   private static void method805(Class32 class2, int x, int n, int z, int n2) {
      int n3 = Class32.method4911(x);
      int n4 = Class32.method4911(z);
      int n5 = Class32.method4911(n + 1);

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            for (int k = 0; k < n2; k++) {
               int n6 = k == n2 - 1 ? 0 : 127;
               class2.method4904(n3 + i, n5 + k, n4 + j, (byte)n6);
            }
         }
      }
   }

   public static final class Entry {
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int intValue4;

      @Override
      public String toString() {
         return "колонок " + this.intValue + ", засыпано " + this.intValue2 + ", пропущено " + this.intValue3 + ", схлопнуто чанков " + this.intValue4;
      }
   }
}

package org.zenith.utility.game;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public final class FlakeState {
   public static final double CONST_doubleValue = Double.NEGATIVE_INFINITY;
   private static final int CONST_intValue = 12;
   private static final int CONST_intValue2 = 30000000;
   private int intValue;
   private int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final double[] doubleValues;
   private final double[] doubleValues2;
   private final boolean[] flags;
   private final boolean[] flags2;
   private static long longValue;
   private long longValue2;
   private LevelChunk worldChunk;
   private int intValue5;
   private int intValue6;

   private FlakeState(int intValue, int n, int n2) {
      this.longValue2 = ++longValue;
      this.intValue = intValue;
      this.intValue2 = n;
      this.intValue3 = n2;
      this.intValue4 = n2 * 2 + 1;
      this.doubleValues = new double[this.intValue4 * this.intValue4];
      this.doubleValues2 = new double[this.intValue4 * this.intValue4];
      this.flags = new boolean[this.intValue4 * this.intValue4];
      this.flags2 = new boolean[this.intValue4 * this.intValue4];
   }

   public static FlakeState method4535(Level level, int intValue, int n, int n2) {
      FlakeState flakeState = new FlakeState(intValue, n, n2);
      flakeState.method4536(level, intValue, n);
      return flakeState;
   }

   public void method4536(Level level, int intValue, int n) {
      boolean flag = this.intValue != intValue || this.intValue2 != n;
      this.intValue = intValue;
      this.intValue2 = n;
      this.worldChunk = null;
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = -this.intValue3; i <= this.intValue3; i++) {
         for (int j = -this.intValue3; j <= this.intValue3; j++) {
            flag |= this.method4540(level, mutablePos, intValue + i, n + j, this.method4550(i, j));
         }
      }

      this.worldChunk = null;
      if (flag) {
         this.longValue2 = ++longValue;
      }
   }

   public void method4537(Level level, int intValue, int n) {
      int n2 = intValue - this.intValue;
      int n3 = n - this.intValue2;
      if (n2 != 0 || n3 != 0) {
         if (Math.abs(n2) < this.intValue4 && Math.abs(n3) < this.intValue4) {
            this.intValue = intValue;
            this.intValue2 = n;
            int n4 = Math.max(0, -n2);
            int n5 = Math.min(this.intValue4 - 1, this.intValue4 - 1 - n2);
            int n6 = Math.max(0, -n3);
            int n7 = Math.min(this.intValue4 - 1, this.intValue4 - 1 - n3);
            int n8 = n7 - n6 + 1;
            int n9 = n6 + n3;
            if (n2 >= 0) {
               for (int i = n4; i <= n5; i++) {
                  this.method4539(i + n2, n9, i, n6, n8);
               }
            } else {
               for (int i = n5; i >= n4; i--) {
                  this.method4539(i + n2, n9, i, n6, n8);
               }
            }

            this.worldChunk = null;
            MutableBlockPos mutablePos = new MutableBlockPos();

            for (int i = 0; i < this.intValue4; i++) {
               boolean flag = i >= n4 && i <= n5;

               for (int j = 0; j < this.intValue4; j++) {
                  if (!flag || j < n6 || j > n7) {
                     this.method4540(level, mutablePos, intValue + i - this.intValue3, n + j - this.intValue3, i * this.intValue4 + j);
                  }
               }
            }

            this.worldChunk = null;
         } else {
            this.method4536(level, intValue, n);
         }
      }
   }

   public void method4538(Level level, int x, int z) {
      int n = x - this.intValue;
      int n2 = z - this.intValue2;
      if (n >= -this.intValue3 && n <= this.intValue3 && n2 >= -this.intValue3 && n2 <= this.intValue3) {
         this.worldChunk = null;
         boolean flag = this.method4540(level, new MutableBlockPos(), x, z, this.method4550(n, n2));
         this.worldChunk = null;
         if (flag) {
            this.longValue2 = ++longValue;
         }
      }
   }

   private void method4539(int n, int n2, int n3, int n4, int n5) {
      int n6 = n * this.intValue4 + n2;
      int n7 = n3 * this.intValue4 + n4;
      System.arraycopy(this.doubleValues, n6, this.doubleValues, n7, n5);
      System.arraycopy(this.doubleValues2, n6, this.doubleValues2, n7, n5);
      System.arraycopy(this.flags, n6, this.flags, n7, n5);
      System.arraycopy(this.flags2, n6, this.flags2, n7, n5);
   }

   private boolean method4540(Level level, MutableBlockPos mutablePos, int x, int z, int n) {
      double doubleValue = this.doubleValues[n];
      double d = this.doubleValues2[n];
      boolean flag = this.flags[n];
      boolean flag2 = this.flags2[n];
      this.method4541(level, mutablePos, x, z, n);
      return this.doubleValues[n] != doubleValue || this.doubleValues2[n] != d || this.flags[n] != flag || this.flags2[n] != flag2;
   }

   private void method4541(Level level, MutableBlockPos mutablePos, int x, int z, int n) {
      this.flags[n] = false;
      this.flags2[n] = false;
      int chunkX = x >> 4;
      int chunkZ = z >> 4;
      if (!level.hasChunk(chunkX, chunkZ)) {
         this.doubleValues[n] = Double.NEGATIVE_INFINITY;
         this.doubleValues2[n] = Double.NEGATIVE_INFINITY;
      } else {
         LevelChunk worldChunk = null;
         int height;
         if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (this.worldChunk == null || this.intValue5 != chunkX || this.intValue6 != chunkZ) {
               this.worldChunk = level.getChunk(chunkX, chunkZ);
               this.intValue5 = chunkX;
               this.intValue6 = chunkZ;
            }

            worldChunk = this.worldChunk;
            height = worldChunk.getHeight(Types.MOTION_BLOCKING, x & 15, z & 15) + 1;
         } else {
            height = level.getHeight(Types.MOTION_BLOCKING, x, z);
         }

         int minY = level.getMinY();
         if (height <= minY) {
            this.doubleValues[n] = Double.NEGATIVE_INFINITY;
            this.doubleValues2[n] = Double.NEGATIVE_INFINITY;
         } else {
            this.doubleValues2[n] = height;
            this.doubleValues[n] = Double.NEGATIVE_INFINITY;
            int n2 = Math.max(minY, height - 12);

            for (int i = height - 1; i >= n2; i--) {
               mutablePos.set(x, i, z);
               BlockState blockState = worldChunk != null ? worldChunk.getBlockState(mutablePos) : level.getBlockState(mutablePos);
               if (!blockState.getFluidState().isEmpty() || blockState.is(BlockTags.ICE)) {
                  this.doubleValues[n] = i + 1;
                  return;
               }

               if (blockState.is(BlockTags.LEAVES)) {
                  this.doubleValues[n] = i + 1;
                  this.flags[n] = true;
                  this.flags2[n] = true;
                  return;
               }

               if (blockState.isRedstoneConductor(level, mutablePos)) {
                  this.doubleValues[n] = i + 1;
                  this.flags[n] = true;
                  return;
               }
            }
         }
      }
   }

   public static FlakeState method4542(int n) {
      FlakeState flakeState = new FlakeState(0, 0, n);

      for (int i = -n; i <= n; i++) {
         for (int j = -n; j <= n; j++) {
            int n2 = flakeState.method4550(i, j);
            flakeState.doubleValues[n2] = method4543(i, j);
            flakeState.doubleValues2[n2] = flakeState.doubleValues[n2];
            flakeState.flags[n2] = true;
         }
      }

      return flakeState;
   }

   private static double method4543(int n, int n2) {
      double d = 0.0;
      if (n2 <= -2) {
         d++;
      }

      if (n2 <= -4 && Math.abs(n) <= 3) {
         d++;
      }

      if (n >= 3 && n2 >= 1) {
         d++;
      }

      return d;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public int getIntValue3() {
      return this.intValue3;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public double method4544(int n, int n2) {
      return n >= -this.intValue3 && n <= this.intValue3 && n2 >= -this.intValue3 && n2 <= this.intValue3
         ? this.doubleValues[this.method4550(n, n2)]
         : Double.NEGATIVE_INFINITY;
   }

   public boolean method4545(int n, int n2) {
      return n >= -this.intValue3 && n <= this.intValue3 && n2 >= -this.intValue3 && n2 <= this.intValue3
         ? this.flags2[this.method4550(n, n2)]
         : false;
   }

   public boolean method4546(int n, int n2) {
      return n >= -this.intValue3 && n <= this.intValue3 && n2 >= -this.intValue3 && n2 <= this.intValue3
         ? this.flags[this.method4550(n, n2)]
         : false;
   }

   public double method4547(double d, double d2) {
      return this.method4544((int)Math.floor(d) - this.intValue, (int)Math.floor(d2) - this.intValue2);
   }

   public double method4548(double d, double d2) {
      return this.method4549((int)Math.floor(d), (int)Math.floor(d2));
   }

   public double method4549(int n, int n2) {
      int n3 = n - this.intValue;
      int n4 = n2 - this.intValue2;
      return n3 >= -this.intValue3 && n3 <= this.intValue3 && n4 >= -this.intValue3 && n4 <= this.intValue3
         ? this.doubleValues2[this.method4550(n3, n4)]
         : Double.NEGATIVE_INFINITY;
   }

   private int method4550(int n, int n2) {
      return (n + this.intValue3) * this.intValue4 + n2 + this.intValue3;
   }
}

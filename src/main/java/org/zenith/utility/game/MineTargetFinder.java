package org.zenith.utility.game;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;

public final class MineTargetFinder {
   private static final MineTargetFinder.Data data = new MineTargetFinder.Data(65363, 65382, 52, 61, 65348, 65369);
   private static final MineTargetFinder.Data data2 = new MineTargetFinder.Data(65389, -127, 51, 62, 65375, 65395);
   private static final Set<Block> items = Set.of(
      Blocks.COAL_ORE,
      Blocks.DEEPSLATE_COAL_ORE,
      Blocks.IRON_ORE,
      Blocks.DEEPSLATE_IRON_ORE,
      Blocks.COPPER_ORE,
      Blocks.DEEPSLATE_COPPER_ORE,
      Blocks.GOLD_ORE,
      Blocks.DEEPSLATE_GOLD_ORE,
      Blocks.REDSTONE_ORE,
      Blocks.DEEPSLATE_REDSTONE_ORE,
      Blocks.EMERALD_ORE,
      Blocks.DEEPSLATE_EMERALD_ORE,
      Blocks.LAPIS_ORE,
      Blocks.DEEPSLATE_LAPIS_ORE,
      Blocks.DIAMOND_ORE,
      Blocks.DEEPSLATE_DIAMOND_ORE,
      Blocks.NETHER_GOLD_ORE,
      Blocks.NETHER_QUARTZ_ORE,
      Blocks.ANCIENT_DEBRIS,
      Blocks.CLAY
   );

   public static MineTargetFinder.Data method1933(boolean flag) {
      return flag ? data2 : data;
   }

   private MineTargetFinder() {
   }

   public static boolean method1934(Block block) {
      return items.contains(block);
   }

   public static boolean method1935(MineTargetFinder.Data data, BlockPos blockPos) {
      return blockPos.getX() >= data.getIntValue()
         && blockPos.getX() <= data.getIntValue2()
         && blockPos.getY() >= data.getIntValue3()
         && blockPos.getY() <= data.getIntValue4()
         && blockPos.getZ() >= data.getIntValue5()
         && blockPos.getZ() <= data.getIntValue6();
   }

   public static boolean method1936(MineTargetFinder.Data data, Vec3 vec) {
      return vec.x >= data.getIntValue() && vec.x <= data.getIntValue2() + 1.0 && vec.z >= data.getIntValue5() && vec.z <= data.getIntValue6() + 1.0;
   }

   public static boolean method1937(MineTargetFinder.Data data, BlockPos pos) {
      return pos.getX() >= data.getIntValue()
         && pos.getX() <= data.getIntValue2()
         && pos.getY() >= data.getIntValue3() - 3
         && pos.getY() <= data.getIntValue4() + 3
         && pos.getZ() >= data.getIntValue5()
         && pos.getZ() <= data.getIntValue6();
   }

   public static boolean method1938(MineTargetFinder.Data selectedMine, Level world) {
      int x = selectedMine.getIntValue() - 4;
      int n = selectedMine.getIntValue2() + 4;
      int z = selectedMine.getIntValue5() - 4;
      int z2 = selectedMine.getIntValue6() + 4;
      int x2 = (selectedMine.getIntValue() + selectedMine.getIntValue2()) / 2;
      int z3 = (selectedMine.getIntValue5() + selectedMine.getIntValue6()) / 2;
      return world.hasChunkAt(new BlockPos(x, selectedMine.getIntValue3(), z))
         && world.hasChunkAt(new BlockPos(x, selectedMine.getIntValue3(), z2))
         && world.hasChunkAt(new BlockPos(n, selectedMine.getIntValue3(), z))
         && world.hasChunkAt(new BlockPos(n, selectedMine.getIntValue3(), z2))
         && world.hasChunkAt(new BlockPos(x2, selectedMine.getIntValue3(), z3));
   }

   public static double method1939(Vec3 eyePosition, BlockPos pos) {
      double x = Math.max((double)pos.getX(), Math.min(pos.getX() + 1.0, eyePosition.x));
      double y = Math.max((double)pos.getY(), Math.min(pos.getY() + 1.0, eyePosition.y));
      double z = Math.max((double)pos.getZ(), Math.min(pos.getZ() + 1.0, eyePosition.z));
      return eyePosition.distanceToSqr(x, y, z);
   }

   public static BlockHitResult method1940(BlockPos pos, double blockInteractionRange, Vec3 eyePosition) {
      Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      double x = 0.49;
      Vec3[] vecs = new Vec3[]{
         atCenterOf,
         atCenterOf.add(x, 0.0, 0.0),
         atCenterOf.add(-x, 0.0, 0.0),
         atCenterOf.add(0.0, x, 0.0),
         atCenterOf.add(0.0, -x, 0.0),
         atCenterOf.add(0.0, 0.0, x),
         atCenterOf.add(0.0, 0.0, -x)
      };

      for (Vec3 vec : vecs) {
         BlockHitResult blockHitResult = TargetUtils.method3205(eyePosition, blockInteractionRange, Rotation.method3128(vec, eyePosition), false);
         if (blockHitResult != null && blockHitResult.getType() != Type.MISS && blockHitResult.getBlockPos().equals(pos)) {
            return blockHitResult;
         }
      }

      return null;
   }

   public static boolean method1941(BlockPos pos, Vec3 vec, Vec3 vec2, double d) {
      Vec3 vec3 = net.minecraft.world.phys.Vec3.atCenterOf(pos).subtract(vec);
      double d2 = vec3.x * vec2.x + vec3.z * vec2.z;
      return d2 > 0.0 && d2 <= d + 0.75 && method1945(pos, vec, vec2) <= 1.2;
   }

   public static boolean method1942(BlockPos pos, Vec3 vec, Vec3 vec2, double d) {
      Vec3 vec3 = net.minecraft.world.phys.Vec3.atCenterOf(pos).subtract(vec);
      double d2 = vec3.x * vec2.x + vec3.z * vec2.z;
      return d2 > -0.35 && d2 <= Math.min(d + 0.75, 4.25) && method1945(pos, vec, vec2) <= 2.25;
   }

   public static BlockPos method1943(BlockPos pos, List<BlockPos> poses, Vec3 eyePosition, Vec3 vec) {
      Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      Vec3 vec2 = new Vec3(atCenterOf.x - vec.x, 0.0, atCenterOf.z - vec.z);
      double lengthSqr = vec2.lengthSqr();
      if (lengthSqr < 0.01) {
         return null;
      } else {
         double d = Math.sqrt(lengthSqr);
         Vec3 vec3 = vec2.scale(1.0 / d);
         return poses.stream()
            .filter(var1x -> !var1x.equals(pos))
            .filter(var4x -> method1942(var4x, vec, vec3, d))
            .min(Comparator.<BlockPos>comparingDouble(var1x -> method1939(eyePosition, var1x)).thenComparingDouble(var2x -> method1945(var2x, vec, vec3)))
            .orElse(null);
      }
   }

   public static BlockPos method1944(BlockPos pos, List<BlockPos> poses, Vec3 eyePosition, Vec3 vec, int n) {
      if (pos.getY() != n) {
         return null;
      } else {
         Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
         Vec3 vec2 = new Vec3(atCenterOf.x - vec.x, 0.0, atCenterOf.z - vec.z);
         double lengthSqr = vec2.lengthSqr();
         if (lengthSqr < 0.01) {
            return null;
         } else {
            double d = Math.sqrt(lengthSqr);
            Vec3 vec3 = vec2.scale(1.0 / d);
            return poses.stream()
               .filter(var1x -> var1x.getY() == pos.getY())
               .filter(var4x -> method1941(var4x, vec, vec3, d))
               .min(Comparator.<BlockPos>comparingDouble(var2x -> method1945(var2x, vec, vec3)).thenComparingDouble(var1x -> method1939(eyePosition, var1x)))
               .orElse(null);
         }
      }
   }

   public static double method1945(BlockPos pos, Vec3 vec, Vec3 vec2) {
      Vec3 vec3 = net.minecraft.world.phys.Vec3.atCenterOf(pos).subtract(vec);
      double d = vec3.x * vec2.x + vec3.z * vec2.z;
      double d2 = vec.x + vec2.x * d;
      double d3 = vec.z + vec2.z * d;
      double d4 = pos.getX() + 0.5 - d2;
      double d5 = pos.getZ() + 0.5 - d3;
      return d4 * d4 + d5 * d5;
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      public int intValue5() {
         return this.intValue5;
      }

      public int intValue6() {
         return this.intValue6;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.intValue5, other.intValue5) && java.util.Objects.equals(this.intValue6, other.intValue6);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, intValue5, intValue6);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "intValue5=" + this.intValue5 + ", " + "intValue6=" + this.intValue6 + "]";
      }

      public Data(int intValue, int n, int n2, int n3, int n4, int n5) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.intValue4 = n3;
         this.intValue5 = n4;
         this.intValue6 = n5;
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

      public int getIntValue4() {
         return this.intValue4;
      }

      public int getIntValue5() {
         return this.intValue5;
      }

      public int getIntValue6() {
         return this.intValue6;
      }
   }
}

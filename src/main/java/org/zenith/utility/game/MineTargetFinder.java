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

   public static MineTargetFinder.Data method1933(boolean netherMine) {
      return netherMine ? data2 : data;
   }

   private MineTargetFinder() {
   }

   public static boolean method1934(Block block) {
      return items.contains(block);
   }

   public static boolean method1935(MineTargetFinder.Data bounds, BlockPos position) {
      return position.getX() >= bounds.getIntValue()
         && position.getX() <= bounds.getIntValue2()
         && position.getY() >= bounds.getIntValue3()
         && position.getY() <= bounds.getIntValue4()
         && position.getZ() >= bounds.getIntValue5()
         && position.getZ() <= bounds.getIntValue6();
   }

   public static boolean method1936(MineTargetFinder.Data bounds, Vec3 position) {
      return position.x >= bounds.getIntValue() && position.x <= bounds.getIntValue2() + 1.0 && position.z >= bounds.getIntValue5() && position.z <= bounds.getIntValue6() + 1.0;
   }

   public static boolean method1937(MineTargetFinder.Data bounds, BlockPos pos) {
      return pos.getX() >= bounds.getIntValue()
         && pos.getX() <= bounds.getIntValue2()
         && pos.getY() >= bounds.getIntValue3() - 3
         && pos.getY() <= bounds.getIntValue4() + 3
         && pos.getZ() >= bounds.getIntValue5()
         && pos.getZ() <= bounds.getIntValue6();
   }

   public static boolean method1938(MineTargetFinder.Data selectedMine, Level world) {
      int minX = selectedMine.getIntValue() - 4;
      int maxX = selectedMine.getIntValue2() + 4;
      int minZ = selectedMine.getIntValue5() - 4;
      int maxZ = selectedMine.getIntValue6() + 4;
      int centerX = (selectedMine.getIntValue() + selectedMine.getIntValue2()) / 2;
      int centerZ = (selectedMine.getIntValue5() + selectedMine.getIntValue6()) / 2;
      return world.hasChunkAt(new BlockPos(minX, selectedMine.getIntValue3(), minZ))
         && world.hasChunkAt(new BlockPos(minX, selectedMine.getIntValue3(), maxZ))
         && world.hasChunkAt(new BlockPos(maxX, selectedMine.getIntValue3(), minZ))
         && world.hasChunkAt(new BlockPos(maxX, selectedMine.getIntValue3(), maxZ))
         && world.hasChunkAt(new BlockPos(centerX, selectedMine.getIntValue3(), centerZ));
   }

   public static double method1939(Vec3 eyePosition, BlockPos pos) {
      double closestX = Math.max((double)pos.getX(), Math.min(pos.getX() + 1.0, eyePosition.x));
      double closestY = Math.max((double)pos.getY(), Math.min(pos.getY() + 1.0, eyePosition.y));
      double closestZ = Math.max((double)pos.getZ(), Math.min(pos.getZ() + 1.0, eyePosition.z));
      return eyePosition.distanceToSqr(closestX, closestY, closestZ);
   }

   public static BlockHitResult method1940(BlockPos pos, double blockInteractionRange, Vec3 eyePosition) {
      Vec3 blockCenter = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      double faceOffset = 0.49;
      Vec3[] aimSamples = new Vec3[]{
         blockCenter,
         blockCenter.add(faceOffset, 0.0, 0.0),
         blockCenter.add(-faceOffset, 0.0, 0.0),
         blockCenter.add(0.0, faceOffset, 0.0),
         blockCenter.add(0.0, -faceOffset, 0.0),
         blockCenter.add(0.0, 0.0, faceOffset),
         blockCenter.add(0.0, 0.0, -faceOffset)
      };

      for (Vec3 aimPoint : aimSamples) {
         BlockHitResult blockHitResult = TargetUtils.method3205(eyePosition, blockInteractionRange, Rotation.lookingAt(aimPoint, eyePosition), false);
         if (blockHitResult != null && blockHitResult.getType() != Type.MISS && blockHitResult.getBlockPos().equals(pos)) {
            return blockHitResult;
         }
      }

      return null;
   }

   public static boolean method1941(BlockPos pos, Vec3 origin, Vec3 direction, double targetDistance) {
      Vec3 offset = net.minecraft.world.phys.Vec3.atCenterOf(pos).subtract(origin);
      double forwardDistance = offset.x * direction.x + offset.z * direction.z;
      return forwardDistance > 0.0 && forwardDistance <= targetDistance + 0.75 && method1945(pos, origin, direction) <= 1.2;
   }

   public static boolean method1942(BlockPos pos, Vec3 origin, Vec3 direction, double targetDistance) {
      Vec3 offset = net.minecraft.world.phys.Vec3.atCenterOf(pos).subtract(origin);
      double forwardDistance = offset.x * direction.x + offset.z * direction.z;
      return forwardDistance > -0.35 && forwardDistance <= Math.min(targetDistance + 0.75, 4.25) && method1945(pos, origin, direction) <= 2.25;
   }

   public static BlockPos method1943(BlockPos pos, List<BlockPos> candidates, Vec3 eyePosition, Vec3 origin) {
      Vec3 targetCenter = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      Vec3 horizontalOffset = new Vec3(targetCenter.x - origin.x, 0.0, targetCenter.z - origin.z);
      double distanceSquared = horizontalOffset.lengthSqr();
      if (distanceSquared < 0.01) {
         return null;
      } else {
         double targetDistance = Math.sqrt(distanceSquared);
         Vec3 direction = horizontalOffset.scale(1.0 / targetDistance);
         return candidates.stream()
            .filter(candidate -> !candidate.equals(pos))
            .filter(candidate -> method1942(candidate, origin, direction, targetDistance))
            .min(Comparator.<BlockPos>comparingDouble(candidate -> method1939(eyePosition, candidate)).thenComparingDouble(candidate -> method1945(candidate, origin, direction)))
            .orElse(null);
      }
   }

   public static BlockPos method1944(BlockPos pos, List<BlockPos> candidates, Vec3 eyePosition, Vec3 origin, int targetY) {
      if (pos.getY() != targetY) {
         return null;
      } else {
         Vec3 targetCenter = net.minecraft.world.phys.Vec3.atCenterOf(pos);
         Vec3 horizontalOffset = new Vec3(targetCenter.x - origin.x, 0.0, targetCenter.z - origin.z);
         double distanceSquared = horizontalOffset.lengthSqr();
         if (distanceSquared < 0.01) {
            return null;
         } else {
            double targetDistance = Math.sqrt(distanceSquared);
            Vec3 direction = horizontalOffset.scale(1.0 / targetDistance);
            return candidates.stream()
               .filter(candidate -> candidate.getY() == pos.getY())
               .filter(candidate -> method1941(candidate, origin, direction, targetDistance))
               .min(Comparator.<BlockPos>comparingDouble(candidate -> method1945(candidate, origin, direction)).thenComparingDouble(candidate -> method1939(eyePosition, candidate)))
               .orElse(null);
         }
      }
   }

   public static double method1945(BlockPos pos, Vec3 origin, Vec3 direction) {
      Vec3 offset = net.minecraft.world.phys.Vec3.atCenterOf(pos).subtract(origin);
      double forwardDistance = offset.x * direction.x + offset.z * direction.z;
      double projectedX = origin.x + direction.x * forwardDistance;
      double projectedZ = origin.z + direction.z * forwardDistance;
      double perpendicularX = pos.getX() + 0.5 - projectedX;
      double perpendicularZ = pos.getZ() + 0.5 - projectedZ;
      return perpendicularX * perpendicularX + perpendicularZ * perpendicularZ;
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

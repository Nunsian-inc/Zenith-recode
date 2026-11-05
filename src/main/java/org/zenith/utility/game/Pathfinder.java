package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.zenith.MinecraftAccess;

public final class Pathfinder implements MinecraftAccess {
   private static final double CONST_doubleValue = Math.sqrt(2.0);
   private static final int[][] CONST_intValues = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
   private static final int[][] CONST_intValues2 = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

   private Pathfinder() {
   }

   public static Optional<Pathfinder.Entry2> method757(BlockPos blockPosition, BlockPos pos) {
      return mc.level == null ? Optional.empty() : method759(mc.level, blockPosition, pos, Pathfinder.Data.INSTANCE);
   }

   public static Optional<Pathfinder.Entry2> method758(BlockPos blockPosition, BlockPos walkTarget, Pathfinder.Data data) {
      return mc.level == null ? Optional.empty() : method759(mc.level, blockPosition, walkTarget, data);
   }

   public static Optional<Pathfinder.Entry2> method759(Level world, BlockPos blockPosition, BlockPos walkTarget, Pathfinder.Data data) {
      if (world != null && blockPosition != null && walkTarget != null && data != null) {
         BlockPos immutable = blockPosition.immutable();
         BlockPos pos = walkTarget.immutable();
         boolean hasChunk = world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
         if (!method771(world, immutable)
            && !method771(world, pos)
            && world.hasChunk(immutable.getX() >> 4, immutable.getZ() >> 4)
            && (data.getType() != Pathfinder.Type.EXACT || hasChunk)) {
            BlockPos key = method768(world, immutable, data.isFlag());
            BlockPos pos2 = data.getIntValue4() == 0 && hasChunk ? method768(world, pos, data.isFlag()) : pos;
            if (pos2 == null && data.getType() == Pathfinder.Type.CLOSEST_REACHABLE) {
               pos2 = pos;
            }

            if (key == null || pos2 == null) {
               return Optional.empty();
            } else if (method772(key, pos2, data.getIntValue4())) {
               return Optional.of(new Pathfinder.Entry2(List.of(key)));
            } else {
               HashMap map = new HashMap();
               PriorityQueue priorityQueue = new PriorityQueue<>(
                  Comparator.comparingDouble(Pathfinder.Data3::getDoubleValue).thenComparingLong(Pathfinder.Data3::getLongValue)
               );
               Pathfinder.Entry entry = new Pathfinder.Entry(key, null, 0.0, method773(key, pos2, data.getIntValue4()));
               Pathfinder.Entry entry2 = entry;
               map.put(key, entry);
               long l = 0L;
               priorityQueue.add(new Pathfinder.Data3(entry, entry.doubleValue3, l++));
               int n = 0;

               while (!priorityQueue.isEmpty() && n < data.getIntValue()) {
                  Pathfinder.Data3 data3 = (Pathfinder.Data3)priorityQueue.poll();
                  Pathfinder.Entry entry3 = data3.getEntry();
                  if (!entry3.closed && data3.getDoubleValue() == entry3.doubleValue3) {
                     entry3.closed = true;
                     n++;
                     if (method774(entry3, entry2)) {
                        entry2 = entry3;
                     }

                     if (method772(entry3.blockPos, pos2, data.getIntValue4())) {
                        return Optional.of(new Pathfinder.Entry2(method775(entry3)));
                     }

                     for (Pathfinder.Data2 data2 : method763(world, entry3.blockPos, key, data)) {
                        Pathfinder.Entry entry4 = (Pathfinder.Entry)map.get(data2.getBlockPos());
                        double d = entry3.doubleValue2 + data2.getDoubleValue();
                        if (entry4 == null || !(d >= entry4.doubleValue2)) {
                           if (entry4 == null) {
                              entry4 = new Pathfinder.Entry(data2.getBlockPos(), entry3, d, method773(data2.getBlockPos(), pos2, data.getIntValue4()));
                              map.put(data2.getBlockPos(), entry4);
                           } else {
                              entry4.entry = entry3;
                              entry4.doubleValue2 = d;
                              entry4.doubleValue3 = d + entry4.doubleValue;
                              entry4.closed = false;
                           }

                           if (method774(entry4, entry2)) {
                              entry2 = entry4;
                           }

                           priorityQueue.add(new Pathfinder.Data3(entry4, entry4.doubleValue3, l++));
                        }
                     }
                  }
               }

               return data.getType() == Pathfinder.Type.CLOSEST_REACHABLE ? Optional.of(new Pathfinder.Entry2(method775(entry2))) : Optional.empty();
            }
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   @Nullable
   public static BlockPos method760(BlockPos blockPosition, BlockPos pos) {
      return method757(blockPosition, pos).map(var1x -> var1x.method778(blockPosition)).orElse(null);
   }

   public static Vec3 method761(BlockPos nextRouteBlock) {
      return new Vec3(nextRouteBlock.getX() + 0.5, nextRouteBlock.getY() + 0.15, nextRouteBlock.getZ() + 0.5);
   }

   public static boolean method762(Level world, BlockPos blockPosition, boolean flag) {
      return world != null && blockPosition != null && !method771(world, blockPosition) && world.hasChunk(blockPosition.getX() >> 4, blockPosition.getZ() >> 4) && method767(world, blockPosition, flag);
   }

   private static List<Pathfinder.Data2> method763(Level world, BlockPos blockPos, BlockPos key, Pathfinder.Data data) {
      List<Data2> data2s = new ArrayList<>(data.isFlag2() ? 8 : 4);
      method764(world, blockPos, key, data, CONST_intValues, data2s);
      if (data.isFlag2()) {
         method764(world, blockPos, key, data, CONST_intValues2, data2s);
      }

      return data2s;
   }

   private static void method764(Level world, BlockPos blockPos, BlockPos key, Pathfinder.Data data, int[][] intses, List<Pathfinder.Data2> data2s) {
      for (int[] ints : intses) {
         int x = ints[0];
         int z = ints[1];
         boolean flag = x != 0 && z != 0;
         double doubleValue = flag ? CONST_doubleValue : 1.0;
         if (!flag || method766(world, blockPos, x, z, data.isFlag())) {
            BlockPos offset = blockPos.offset(x, 0, z);
            if (method765(world, offset, key, data) && method767(world, offset, data.isFlag())) {
               data2s.add(new Pathfinder.Data2(offset.immutable(), doubleValue));
            } else {
               BlockPos pos = blockPos.offset(x, 1, z);
               if (method765(world, pos, key, data) && method769(world, blockPos.above(2), data.isFlag()) && method767(world, pos, data.isFlag())) {
                  data2s.add(new Pathfinder.Data2(pos.immutable(), doubleValue + 0.65));
               } else if (method769(world, offset, data.isFlag()) && method769(world, offset.above(), data.isFlag())) {
                  for (int i = 1; i <= data.getIntValue3(); i++) {
                     BlockPos offset2 = blockPos.offset(x, -i, z);
                     if (!method765(world, offset2, key, data)) {
                        break;
                     }

                     if (method767(world, offset2, data.isFlag())) {
                        data2s.add(new Pathfinder.Data2(offset2.immutable(), doubleValue + 0.2 + i * 0.15));
                        break;
                     }

                     if (!method769(world, offset2, data.isFlag())) {
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean method765(Level world, BlockPos offset, BlockPos key, Pathfinder.Data data) {
      if (!method771(world, offset) && world.hasChunk(offset.getX() >> 4, offset.getZ() >> 4)) {
         int n = offset.getX() - key.getX();
         int n2 = offset.getZ() - key.getZ();
         return Math.max(Math.abs(n), Math.abs(n2)) <= data.getIntValue2();
      } else {
         return false;
      }
   }

   private static boolean method766(Level world, BlockPos blockPos, int x, int z, boolean flag) {
      BlockPos offset = blockPos.offset(x, 0, 0);
      BlockPos above = blockPos.offset(0, 0, z);
      return method769(world, offset, flag) && method769(world, offset.above(), flag) && method769(world, above, flag) && method769(world, above.above(), flag);
   }

   private static boolean method767(Level world, BlockPos blockPosition, boolean flag) {
      if (!method769(world, blockPosition, flag) || !method769(world, blockPosition.above(), flag)) {
         return false;
      } else if (flag && !world.getFluidState(blockPosition).isEmpty()) {
         return true;
      } else {
         BlockPos below = blockPosition.below();
         BlockState blockState = world.getBlockState(below);
         if (method770(blockState)) {
            return false;
         } else {
            VoxelShape collisionShape = blockState.getCollisionShape(world, below);
            if (collisionShape.isEmpty()) {
               return false;
            } else {
               double d = collisionShape.max(Axis.Y);
               return d >= 0.499 && d <= 1.001;
            }
         }
      }
   }

   @Nullable
   private static BlockPos method768(Level world, BlockPos immutable, boolean flag) {
      if (method767(world, immutable, flag)) {
         return immutable.immutable();
      } else {
         BlockPos above = immutable.above();
         return !method771(world, above) && method767(world, above, flag) ? above.immutable() : null;
      }
   }

   private static boolean method769(Level world, BlockPos above, boolean flag) {
      BlockState blockState = world.getBlockState(above);
      if (!method770(blockState) && blockState.getCollisionShape(world, above).isEmpty()) {
         FluidState fluidState = blockState.getFluidState();
         return fluidState.isEmpty() || flag;
      } else {
         return false;
      }
   }

   private static boolean method770(BlockState blockState) {
      return blockState.is(Blocks.LAVA)
         || blockState.is(Blocks.FIRE)
         || blockState.is(Blocks.SOUL_FIRE)
         || blockState.is(Blocks.CACTUS)
         || blockState.is(Blocks.MAGMA_BLOCK)
         || blockState.is(Blocks.CAMPFIRE)
         || blockState.is(Blocks.SOUL_CAMPFIRE)
         || blockState.is(Blocks.SWEET_BERRY_BUSH)
         || blockState.is(Blocks.COBWEB)
         || blockState.is(Blocks.POWDER_SNOW);
   }

   private static boolean method771(Level world, BlockPos immutable) {
      return immutable.getY() < world.getMinY() || immutable.getY() > world.getMaxY() - 1;
   }

   private static boolean method772(BlockPos key, BlockPos pos, int n) {
      return key.getX() == pos.getX() && key.getZ() == pos.getZ() && Math.abs(key.getY() - pos.getY()) <= n;
   }

   private static double method773(BlockPos key, BlockPos pos, int n) {
      int n2 = Math.abs(key.getX() - pos.getX());
      int n3 = Math.abs(key.getZ() - pos.getZ());
      int n4 = Math.min(n2, n3);
      int n5 = Math.max(n2, n3) - n4;
      int n6 = pos.getY() - key.getY();
      int n7 = Math.max(0, Math.abs(n6) - n);
      double d = n6 > 0 ? n7 * 0.65 : n7 * 0.15;
      return n4 * CONST_doubleValue + n5 + d;
   }

   private static boolean method774(Pathfinder.Entry entry, Pathfinder.Entry entry2) {
      int n = Double.compare(entry.doubleValue, entry2.doubleValue);
      return n < 0 || n == 0 && entry.doubleValue2 < entry2.doubleValue2;
   }

   private static List<BlockPos> method775(Pathfinder.Entry entry) {
      ArrayList<BlockPos> list = new ArrayList<>();

      for (Pathfinder.Entry entry2 = entry; entry2 != null; entry2 = entry2.entry) {
         list.add(entry2.blockPos);
      }

      Collections.reverse(list);
      return list;
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final boolean flag;
      private final boolean flag2;
      private final Pathfinder.Type type;

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

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public Pathfinder.Type type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, flag, flag2, type);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "type=" + this.type + "]";
      }
      public static final Pathfinder.Data INSTANCE = new Pathfinder.Data(50000, 192, 3, 0, false, true, Pathfinder.Type.EXACT);

      public Data(int intValue, int n, int n2, int n3, boolean flag, boolean flag2) {
         this(intValue, n, n2, n3, flag, flag2, Pathfinder.Type.EXACT);
      }

      public Data(int intValue, int n, int n2, boolean flag, boolean flag2) {
         this(intValue, n, n2, 0, flag, flag2, Pathfinder.Type.EXACT);
      }

      public Data(int intValue, int n, int n2, int n3, boolean flag, boolean flag2, Pathfinder.Type type) {
         if (intValue <= 0) {
            throw new IllegalArgumentException("maxVisitedNodes must be positive");
         } else if (n <= 0) {
            throw new IllegalArgumentException("maxRange must be positive");
         } else if (n2 < 0) {
            throw new IllegalArgumentException("maxFallDistance cannot be negative");
         } else if (n3 < 0) {
            throw new IllegalArgumentException("goalYTolerance cannot be negative");
         } else if (type == null) {
            throw new IllegalArgumentException("pathMode cannot be null");
         } else {
            this.intValue = intValue;
            this.intValue2 = n;
            this.intValue3 = n2;
            this.intValue4 = n3;
            this.flag = flag;
            this.flag2 = flag2;
            this.type = type;
         }
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

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public Pathfinder.Type getType() {
         return this.type;
      }
   }

   private static final class Data2 {
      private final BlockPos blockPos;
      private final double doubleValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, doubleValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "blockPos=" + this.blockPos + ", " + "doubleValue=" + this.doubleValue + "]";
      }

      private Data2(BlockPos blockPos, double doubleValue) {
         this.blockPos = blockPos;
         this.doubleValue = doubleValue;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }
   }

   private static final class Data3 {
      private final Pathfinder.Entry entry;
      private final double doubleValue;
      private final long longValue;

      public Pathfinder.Entry entry() {
         return this.entry;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.entry, other.entry) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(entry, doubleValue, longValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "entry=" + this.entry + ", " + "doubleValue=" + this.doubleValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data3(Pathfinder.Entry entry, double doubleValue, long longValue) {
         this.entry = entry;
         this.doubleValue = doubleValue;
         this.longValue = longValue;
      }

      public Pathfinder.Entry getEntry() {
         return this.entry;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private final BlockPos blockPos;
      private final double doubleValue;
      private Pathfinder.Entry entry;
      private double doubleValue2;
      private double doubleValue3;
      private boolean closed;

      private Entry(BlockPos blockPos, Pathfinder.Entry entry, double d, double doubleValue) {
         this.blockPos = blockPos;
         this.entry = entry;
         this.doubleValue2 = d;
         this.doubleValue = doubleValue;
         this.doubleValue3 = d + doubleValue;
      }
   }

   public static final class Entry2 {
      private final List<BlockPos> items;
      private int intValue;

      private Entry2(List<BlockPos> coll) {
         this.items = List.copyOf(coll);
         this.intValue = Math.min(1, coll.size());
      }

      public List<BlockPos> getItems() {
         return this.items;
      }

      public BlockPos method776() {
         return this.items.getLast();
      }

      public boolean method777() {
         return this.intValue >= this.items.size();
      }

      @Nullable
      public BlockPos method778(BlockPos blockPosition) {
         for (int i = this.intValue; i < this.items.size(); i++) {
            if (this.items.get(i).equals(blockPosition)) {
               this.intValue = i + 1;
               break;
            }
         }

         return this.method777() ? null : this.items.get(this.intValue);
      }

      @Nullable
      public BlockPos method779(Vec3 position) {
         while (!this.method777()) {
            BlockPos pos = this.items.get(this.intValue);
            double d = position.x - (pos.getX() + 0.5);
            double d2 = position.z - (pos.getZ() + 0.5);
            if (!(d * d + d2 * d2 > 0.2025) && !(Math.abs(position.y - pos.getY()) > 0.75)) {
               this.intValue++;
               continue;
            }
            break;
         }

         return this.method777() ? null : this.items.get(this.intValue);
      }

      @Nullable
      public Vec3 method780(BlockPos blockPosition) {
         BlockPos nextRouteBlock = this.method778(blockPosition);
         return nextRouteBlock == null ? null : Pathfinder.method761(nextRouteBlock);
      }

      @Nullable
      public Vec3 method781(Vec3 position) {
         BlockPos nextRouteBlock = this.method779(position);
         return nextRouteBlock == null ? null : Pathfinder.method761(nextRouteBlock);
      }

      public void reset() {
         this.intValue = Math.min(1, this.items.size());
      }
   }

   public static enum Type {
      EXACT,
      CLOSEST_REACHABLE;
   }
}

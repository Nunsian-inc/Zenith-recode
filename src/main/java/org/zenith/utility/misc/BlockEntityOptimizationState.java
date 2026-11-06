package org.zenith.utility.misc;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockEntityOptimizationState {
   public static long longValue;
   public static int intValue;
   private static Thread markedThread;

   private BlockEntityOptimizationState() {
   }

   public static void advanceCacheEpoch() {
      longValue++;
   }

   public static void invalidateSpriteAtlas() {
      intValue++;
   }

   public static void markCurrentThread() {
      markedThread = Thread.currentThread();
   }

   public static void clearMarkedThread() {
      markedThread = null;
   }

   public static boolean isMarkedThread() {
      Thread currentMarkedThread = markedThread;
      return currentMarkedThread != null && currentMarkedThread == Thread.currentThread();
   }

   public static BlockEntityOptimizationState.Entry getOptimizationData(BlockEntity blockEntity) {
      return ((BlockEntityOptimizationState.Handler)blockEntity).zenith$optData();
   }

   public static final class Entry {
      public long longValue = Long.MIN_VALUE;
      public BlockState blockState;
      public Object source;
      public long longValue2 = Long.MIN_VALUE;
      public int intValue;
      public int intValue2;
      public long longValue3 = Long.MIN_VALUE;
      public int intValue3;
      public BlockState blockState2;
      public boolean flag;
      public long longValue4 = Long.MIN_VALUE;
      public Entity entity;
      public EntityRenderState entityRenderState;
   }

   public interface Handler {
      BlockEntityOptimizationState.Entry zenith$optData();
   }
}

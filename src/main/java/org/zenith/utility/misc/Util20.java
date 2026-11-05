package org.zenith.utility.misc;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class Util20 {
   public static long longValue;
   public static int intValue;
   private static Thread thread;

   private Util20() {
   }

   public static void method5134() {
      longValue++;
   }

   public static void method5135() {
      intValue++;
   }

   public static void method5136() {
      thread = Thread.currentThread();
   }

   public static void method5137() {
      thread = null;
   }

   public static boolean method5138() {
      Thread thread2 = thread;
      return thread2 != null && thread2 == Thread.currentThread();
   }

   public static Util20.Entry method5139(BlockEntity blockEntity) {
      return ((Util20.Handler)blockEntity).zenith$optData();
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
      Util20.Entry zenith$optData();
   }
}

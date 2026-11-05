package org.zenith.utility.render;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.lang.ref.WeakReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.misc.OptimizationFlags;

public final class FluidColorCache {
   private static final Long2ObjectOpenHashMap<FluidState> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   private static long longValue = Long.MIN_VALUE;
   private static WeakReference<ClientLevel> weakReference = new WeakReference<>(null);

   private FluidColorCache() {
   }

   public static double method7101(double d, double d2, double d3) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.gameRenderer == null) {
         return 0.0;
      } else {
         Vec3 position = minecraft.gameRenderer.mainCamera().position();
         if (position == null) {
            return 0.0;
         } else {
            double d4 = d - position.x;
            double d5 = d2 - position.y;
            double d6 = d3 - position.z;
            return d4 * d4 + d5 * d5 + d6 * d6;
         }
      }
   }

   public static boolean method7102(Object obj, double d) {
      double doubleValue = OptimizationFlags.doubleValue;
      if (d < doubleValue) {
         return true;
      } else {
         int n = System.identityHashCode(obj) * -1640531527 >>> 28;
         return d < doubleValue * 4.0 ? (n & 1) == 0 : (n & 3) == 0;
      }
   }

   public static FluidState method7103(ClientLevel referent, BlockPos pos) {
      long gameTime = referent.getGameTime();
      ClientLevel level = weakReference.get();
      if (gameTime == longValue && level == referent) {
         return (FluidState)long2ObjectOpenHashMap.get(pos.asLong());
      } else {
         long2ObjectOpenHashMap.clear();
         longValue = gameTime;
         if (level != referent) {
            weakReference = new WeakReference<>(referent);
         }

         return null;
      }
   }

   public static void method7104(BlockPos pos, FluidState fluidState) {
      long2ObjectOpenHashMap.put(pos.asLong(), fluidState);
   }
}

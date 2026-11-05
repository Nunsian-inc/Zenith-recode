package org.zenith.base.bot.world;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.Rotation;

public final class BotRaytracing {
   private BotRaytracing() {
   }

   public static BlockHitResult rayTracePos(BotWorld var0, BotPlayer var1, Vec3 var2, Rotation var3, double var4, Predicate<BlockHitResult> var6) {
      Vec3 var7 = var3.method3135();
      Vec3 var8 = var2.add(var7.x * var4, var7.y * var4, var7.z * var4);
      ClipContext var9 = new ClipContext(var2, var8, Block.OUTLINE, Fluid.NONE, var1);
      return BlockGetter.traverseBlocks(var9.getFrom(), var9.getTo(), var9, (var2x, var3x) -> {
         BlockState var4x = var0.getBlockState(var3x);
         FluidState var5 = var0.getFluidState(var3x);
         Vec3 var6x = var2x.getFrom();
         Vec3 var7x = var2x.getTo();
         VoxelShape var8x = var2x.getBlockShape(var4x, var0, var3x);
         BlockHitResult var9x = var0.clipWithInteractionOverride(var6x, var7x, var3x, var8x, var4x);
         VoxelShape var10 = var2x.getFluidShape(var5, var0, var3x);
         BlockHitResult var11 = var10.clip(var6x, var7x, var3x);
         double var12 = var9x == null ? Double.MAX_VALUE : var6x.distanceToSqr(var9x.getLocation());
         double var14 = var11 == null ? Double.MAX_VALUE : var6x.distanceToSqr(var11.getLocation());
         BlockHitResult var16 = var12 <= var14 ? var9x : var11;
         return !var6.test(var16) ? null : var16;
      }, var0x -> {
         Vec3 var1x = var0x.getFrom().subtract(var0x.getTo());
         return BlockHitResult.miss(var0x.getTo(), Direction.getApproximateNearest(var1x.x, var1x.y, var1x.z), BlockPos.containing(var0x.getTo()));
      });
   }
}

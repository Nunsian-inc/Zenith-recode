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

   public static BlockHitResult rayTracePos(BotWorld world, BotPlayer player, Vec3 eyePosition, Rotation rotation, double blockInteractionRange, Predicate<BlockHitResult> predicate) {
      Vec3 vec = rotation.method3135();
      Vec3 to = eyePosition.add(vec.x * blockInteractionRange, vec.y * blockInteractionRange, vec.z * blockInteractionRange);
      ClipContext clipContext = new ClipContext(eyePosition, to, Block.OUTLINE, Fluid.NONE, player);
      return BlockGetter.traverseBlocks(clipContext.getFrom(), clipContext.getTo(), clipContext, (var2x, var3x) -> {
         BlockState var4x = world.getBlockState(var3x);
         FluidState fluidState = world.getFluidState(var3x);
         Vec3 var6x = var2x.getFrom();
         Vec3 var7x = var2x.getTo();
         VoxelShape var8x = var2x.getBlockShape(var4x, world, var3x);
         BlockHitResult var9x = world.clipWithInteractionOverride(var6x, var7x, var3x, var8x, var4x);
         VoxelShape fluidShape = var2x.getFluidShape(fluidState, world, var3x);
         BlockHitResult clip = fluidShape.clip(var6x, var7x, var3x);
         double d = var9x == null ? Double.MAX_VALUE : var6x.distanceToSqr(var9x.getLocation());
         double d2 = clip == null ? Double.MAX_VALUE : var6x.distanceToSqr(clip.getLocation());
         BlockHitResult blockHitResult = d <= d2 ? var9x : clip;
         return !predicate.test(blockHitResult) ? null : blockHitResult;
      }, var0x -> {
         Vec3 var1x = var0x.getFrom().subtract(var0x.getTo());
         return BlockHitResult.miss(var0x.getTo(), Direction.getApproximateNearest(var1x.x, var1x.y, var1x.z), BlockPos.containing(var0x.getTo()));
      });
   }
}

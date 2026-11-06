package org.zenith.utility.game;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.zenith.Rotation;
import org.zenith.ZenithAccess;
import org.zenith.client.modules.impl.combat.AuraModule;

public final class TargetUtils implements ZenithAccess {
   public static BlockHitResult method3204(double blockInteractionRange, Rotation rotation, boolean includeFluids) {
      return method3205(Objects.requireNonNull(mc.player).getEyePosition(1.0F), blockInteractionRange, rotation, includeFluids);
   }

   public static BlockHitResult method3205(Vec3 eyePosition, double blockInteractionRange, Rotation rotation, boolean includeFluids) {
      Entity cameraEntity = mc.getCameraEntity();
      if (cameraEntity == null) {
         return null;
      } else {
         Vec3 direction = rotation.toDirection();
         Vec3 to = eyePosition.add(direction.x * blockInteractionRange, direction.y * blockInteractionRange, direction.z * blockInteractionRange);
         ClientLevel level = mc.level;
         if (level == null) {
            return null;
         } else {
            Fluid fluid = includeFluids ? Fluid.ANY : Fluid.NONE;
            ClipContext clipContext = new ClipContext(eyePosition, to, Block.OUTLINE, fluid, cameraEntity);
            return level.clip(clipContext);
         }
      }
   }

   public static HitResult method3206(double range, Rotation rotation, float partialTickTime, boolean includeFluids) {
      Vec3 eyePosition = mc.getCameraEntity().getEyePosition(partialTickTime);
      Vec3 direction = rotation.toDirection();
      Vec3 to = eyePosition.add(direction.x * range, direction.y * range, direction.z * range);
      return mc.level.clip(new ClipContext(eyePosition, to, Block.OUTLINE, includeFluids ? Fluid.ANY : Fluid.NONE, mc.getCameraEntity()));
   }

   public static BlockHitResult method3207(Vec3 from, Vec3 to, Block block) {
      return method3208(from, to, block, mc.player);
   }

   public static BlockHitResult method3208(Vec3 from, Vec3 to, Block block, Entity sourceEntity) {
      return mc.level.clip(new ClipContext(from, to, block, Fluid.NONE, sourceEntity));
   }

   public static boolean method3209(double range, Rotation rotation, LivingEntity target) {
      return method3210(range, rotation, entity -> entity == target) != null;
   }

   public static EntityHitResult method3210(double range, Rotation rotation, Predicate<Entity> predicate) {
      Entity cameraEntity = mc.getCameraEntity();
      if (cameraEntity == null) {
         return null;
      } else {
         Vec3 eyePosition = cameraEntity.getEyePosition(1.0F);
         Vec3 direction = rotation.toDirection();
         Vec3 to = eyePosition.add(direction.x * range, direction.y * range, direction.z * range);
         AABB searchBox = cameraEntity.getBoundingBox().expandTowards(direction.scale(range)).inflate(1.0, 1.0, 1.0);
         return ProjectileUtil.getEntityHitResult(cameraEntity, eyePosition, to, searchBox, entity -> !entity.isSpectator() && predicate.test(entity), range * range);
      }
   }

   public static EntityHitResult method3211(Rotation rotation, Predicate<Entity> predicate) {
      Entity cameraEntity = mc.getCameraEntity();
      if (cameraEntity != null && mc.level != null) {
         Vec3 eyePosition = cameraEntity.getEyePosition(1.0F);
         Vec3 direction = rotation.toDirection();
         Entity closestEntity = null;
         Vec3 closestHitPosition = null;
         double closestDistanceSquared = Double.MAX_VALUE;

         for (Entity candidate : mc.level.entitiesForRendering()) {
            if (candidate != cameraEntity && !candidate.isSpectator() && predicate.test(candidate)) {
               AABB pickBox = candidate.getBoundingBox().inflate(candidate.getPickRadius());
               Vec3 hitPosition;
               if (pickBox.contains(eyePosition)) {
                  hitPosition = eyePosition;
               } else {
                  double rayLength = eyePosition.distanceTo(pickBox.getCenter()) + pickBox.getXsize() + pickBox.getYsize() + pickBox.getZsize();
                  var intersection = pickBox.clip(eyePosition, eyePosition.add(direction.scale(rayLength)));
                  if (intersection.isEmpty()) {
                     continue;
                  }

                  hitPosition = (Vec3)intersection.get();
               }

               double distanceSquared = eyePosition.distanceToSqr(hitPosition);
               if (distanceSquared < closestDistanceSquared) {
                  closestEntity = candidate;
                  closestHitPosition = hitPosition;
                  closestDistanceSquared = distanceSquared;
               }
            }
         }

         return closestEntity == null ? null : new EntityHitResult(closestEntity, closestHitPosition);
      } else {
         return null;
      }
   }

   public static boolean method3212(Rotation rotation, Vec3 eyePosition, AABB boundingBox, double distance, boolean checkBlockOcclusion) {
      double rayLength = Math.max(mc.player.blockInteractionRange(), distance);
      double rayLengthSquared = Mth.square(rayLength);
      BlockHitResult blockHitResult = method3216(eyePosition, rotation, rayLength, AuraModule.INSTANCE::method6362);
      double blockDistanceSquared = blockHitResult.getLocation().distanceToSqr(eyePosition);
      if (checkBlockOcclusion && blockHitResult.getType() != Type.MISS) {
         rayLengthSquared = blockDistanceSquared;
         rayLength = Math.sqrt(blockDistanceSquared);
      }

      Vec3 direction = rotation.toDirection();
      Vec3 to = eyePosition.add(direction.x * rayLength, direction.y * rayLength, direction.z * rayLength);
      Vec3 entityHitPosition = method3214(eyePosition, to, boundingBox, rayLengthSquared);
      return entityHitPosition != null && (entityHitPosition.distanceToSqr(eyePosition) < blockDistanceSquared || !checkBlockOcclusion) && entityHitPosition.closerThan(eyePosition, distance);
   }

   public static HitResult method3213(Vec3 from, Rotation rotation, double range) {
      Vec3 direction = rotation.toDirection();
      Vec3 to = from.add(direction.x * range, direction.y * range, direction.z * range);
      return mc.level.clip(new ClipContext(from, to, Block.OUTLINE, Fluid.NONE, mc.player));
   }

   @Nullable
   public static Vec3 method3214(Vec3 eyePosition, Vec3 to, AABB boundingBox, double maxDistanceSquared) {
      Vec3 hitPosition = null;
      var intersection = boundingBox.clip(eyePosition, to);
      if (boundingBox.contains(eyePosition)) {
         if (maxDistanceSquared >= 0.0) {
            hitPosition = intersection.orElse(eyePosition);
         }
      } else if (intersection.isPresent()) {
         Vec3 intersectionPosition = (Vec3)intersection.get();
         double distanceSquared = eyePosition.distanceToSqr(intersectionPosition);
         if (distanceSquared < maxDistanceSquared || maxDistanceSquared == 0.0) {
            hitPosition = intersectionPosition;
         }
      }

      return hitPosition;
   }

   private static HitResult ensureTargetInRange(HitResult hitResult, Vec3 pos, double distance) {
      Vec3 location = hitResult.getLocation();
      if (!location.closerThan(pos, distance)) {
         Vec3 hitPosition = hitResult.getLocation();
         Direction missDirection = Direction.getApproximateNearest(hitPosition.x - pos.x, hitPosition.y - pos.y, hitPosition.z - pos.z);
         return BlockHitResult.miss(hitPosition, missDirection, BlockPos.containing(hitPosition));
      } else {
         return hitResult;
      }
   }

   public static boolean method3215(Vec3 direction, double range, AABB aabb) {
      Vec3 eyePosition = Objects.requireNonNull(mc.player).getEyePosition();
      return aabb.contains(eyePosition) || aabb.clip(eyePosition, eyePosition.add(direction.scale(range))).isPresent();
   }

   public static BlockHitResult method3216(Vec3 eyePosition, Rotation rotation, double blockInteractionRange, Predicate<BlockHitResult> predicate) {
      Vec3 direction = rotation.toDirection();
      Vec3 to = eyePosition.add(direction.x * blockInteractionRange, direction.y * blockInteractionRange, direction.z * blockInteractionRange);
      return method3217(new ClipContext(eyePosition, to, Block.OUTLINE, Fluid.NONE, mc.player), predicate);
   }

   public static BlockHitResult method3217(ClipContext clipContext, Predicate<BlockHitResult> predicate) {
      return BlockGetter.traverseBlocks(clipContext.getFrom(), clipContext.getTo(), clipContext, (context, pos) -> {
         BlockState blockState = mc.level.getBlockState(pos);
         FluidState fluidState = mc.level.getFluidState(pos);
         Vec3 from = context.getFrom();
         Vec3 to = context.getTo();
         VoxelShape blockShape = context.getBlockShape(blockState, mc.level, pos);
         BlockHitResult blockHit = mc.level.clipWithInteractionOverride(from, to, pos, blockShape, blockState);
         VoxelShape fluidShape = context.getFluidShape(fluidState, mc.level, pos);
         BlockHitResult fluidHit = fluidShape.clip(from, to, pos);
         double blockDistanceSquared = blockHit == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(blockHit.getLocation());
         double fluidDistanceSquared = fluidHit == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(fluidHit.getLocation());
         if (!predicate.test(blockDistanceSquared <= fluidDistanceSquared ? blockHit : fluidHit)) {
            return null;
         } else {
            return blockDistanceSquared <= fluidDistanceSquared ? blockHit : fluidHit;
         }
      }, missContext -> {
         Vec3 context = missContext.getFrom().subtract(missContext.getTo());
         return BlockHitResult.miss(missContext.getTo(), Direction.getApproximateNearest(context.x, context.y, context.z), BlockPos.containing(missContext.getTo()));
      });
   }

   public static BlockHitResult method3218(Vec3 from, Vec3 to, BlockPos pos, VoxelShape voxelShape, BlockState blockState) {
      BlockHitResult shapeHit = voxelShape.clip(from, to, pos);
      if (shapeHit != null) {
         BlockHitResult interactionHit = blockState.getInteractionShape(mc.level, pos).clip(from, to, pos);
         if (interactionHit != null && interactionHit.getLocation().subtract(from).lengthSqr() < shapeHit.getLocation().subtract(from).lengthSqr()) {
            return shapeHit.withDirection(interactionHit.getDirection());
         }
      }

      return shapeHit;
   }

   @Generated
   private TargetUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

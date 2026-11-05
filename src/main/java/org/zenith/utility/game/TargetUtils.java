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
   public static BlockHitResult method3204(double blockInteractionRange, Rotation rotation, boolean flag) {
      return method3205(Objects.requireNonNull(mc.player).getEyePosition(1.0F), blockInteractionRange, rotation, flag);
   }

   public static BlockHitResult method3205(Vec3 eyePosition, double blockInteractionRange, Rotation rotation, boolean flag) {
      Entity cameraEntity = mc.getCameraEntity();
      if (cameraEntity == null) {
         return null;
      } else {
         Vec3 vec = rotation.method3135();
         Vec3 to = eyePosition.add(vec.x * blockInteractionRange, vec.y * blockInteractionRange, vec.z * blockInteractionRange);
         ClientLevel level = mc.level;
         if (level == null) {
            return null;
         } else {
            Fluid fluid = flag ? Fluid.ANY : Fluid.NONE;
            ClipContext clipContext = new ClipContext(eyePosition, to, Block.OUTLINE, fluid, cameraEntity);
            return level.clip(clipContext);
         }
      }
   }

   public static HitResult method3206(double d, Rotation rotation, float partialTickTime, boolean flag) {
      Vec3 eyePosition = mc.getCameraEntity().getEyePosition(partialTickTime);
      Vec3 vec = rotation.method3135();
      Vec3 to = eyePosition.add(vec.x * d, vec.y * d, vec.z * d);
      return mc.level.clip(new ClipContext(eyePosition, to, Block.OUTLINE, flag ? Fluid.ANY : Fluid.NONE, mc.getCameraEntity()));
   }

   public static BlockHitResult method3207(Vec3 from, Vec3 to, Block block) {
      return method3208(from, to, block, mc.player);
   }

   public static BlockHitResult method3208(Vec3 from, Vec3 to, Block block, Entity thrownTrident) {
      return mc.level.clip(new ClipContext(from, to, block, Fluid.NONE, thrownTrident));
   }

   public static boolean method3209(double scale, Rotation rotation, LivingEntity livingEntity) {
      return method3210(scale, rotation, entity -> entity == livingEntity) != null;
   }

   public static EntityHitResult method3210(double scale, Rotation rotation, Predicate<Entity> predicate) {
      Entity cameraEntity = mc.getCameraEntity();
      if (cameraEntity == null) {
         return null;
      } else {
         Vec3 eyePosition = cameraEntity.getEyePosition(1.0F);
         Vec3 vec = rotation.method3135();
         Vec3 to = eyePosition.add(vec.x * scale, vec.y * scale, vec.z * scale);
         AABB inflate = cameraEntity.getBoundingBox().expandTowards(vec.scale(scale)).inflate(1.0, 1.0, 1.0);
         return ProjectileUtil.getEntityHitResult(cameraEntity, eyePosition, to, inflate, entity -> !entity.isSpectator() && predicate.test(entity), scale * scale);
      }
   }

   public static EntityHitResult method3211(Rotation rotation, Predicate<Entity> predicate) {
      Entity cameraEntity = mc.getCameraEntity();
      if (cameraEntity != null && mc.level != null) {
         Vec3 eyePosition = cameraEntity.getEyePosition(1.0F);
         Vec3 vec = rotation.method3135();
         Entity entity = null;
         Vec3 location = null;
         double d = Double.MAX_VALUE;

         for (Entity entity2 : mc.level.entitiesForRendering()) {
            if (entity2 != cameraEntity && !entity2.isSpectator() && predicate.test(entity2)) {
               AABB inflate = entity2.getBoundingBox().inflate(entity2.getPickRadius());
               Vec3 eyePosition2;
               if (inflate.contains(eyePosition)) {
                  eyePosition2 = eyePosition;
               } else {
                  double scale = eyePosition.distanceTo(inflate.getCenter()) + inflate.getXsize() + inflate.getYsize() + inflate.getZsize();
                  var clip = inflate.clip(eyePosition, eyePosition.add(vec.scale(scale)));
                  if (clip.isEmpty()) {
                     continue;
                  }

                  eyePosition2 = (Vec3)clip.get();
               }

               double d2 = eyePosition.distanceToSqr(eyePosition2);
               if (d2 < d) {
                  entity = entity2;
                  location = eyePosition2;
                  d = d2;
               }
            }
         }

         return entity == null ? null : new EntityHitResult(entity, location);
      } else {
         return null;
      }
   }

   public static boolean method3212(Rotation rotation, Vec3 eyePosition, AABB boundingBox, double distance, boolean flag) {
      double x = Math.max(mc.player.blockInteractionRange(), distance);
      double square = Mth.square(x);
      BlockHitResult blockHitResult = method3216(eyePosition, rotation, x, AuraModule.INSTANCE::method6362);
      double d = blockHitResult.getLocation().distanceToSqr(eyePosition);
      if (flag && blockHitResult.getType() != Type.MISS) {
         square = d;
         x = Math.sqrt(d);
      }

      Vec3 vec = rotation.method3135();
      Vec3 to = eyePosition.add(vec.x * x, vec.y * x, vec.z * x);
      Vec3 vec2 = method3214(eyePosition, to, boundingBox, square);
      return vec2 != null && (vec2.distanceToSqr(eyePosition) < d || !flag) && vec2.closerThan(eyePosition, distance);
   }

   public static HitResult method3213(Vec3 from, Rotation rotation, double d) {
      Vec3 vec = rotation.method3135();
      Vec3 to = from.add(vec.x * d, vec.y * d, vec.z * d);
      return mc.level.clip(new ClipContext(from, to, Block.OUTLINE, Fluid.NONE, mc.player));
   }

   @Nullable
   public static Vec3 method3214(Vec3 eyePosition, Vec3 to, AABB boundingBox, double square) {
      Vec3 vec = null;
      var clip = boundingBox.clip(eyePosition, to);
      if (boundingBox.contains(eyePosition)) {
         if (square >= 0.0) {
            vec = clip.orElse(eyePosition);
         }
      } else if (clip.isPresent()) {
         Vec3 vec2 = (Vec3)clip.get();
         double d = eyePosition.distanceToSqr(vec2);
         if (d < square || square == 0.0) {
            vec = vec2;
         }
      }

      return vec;
   }

   private static HitResult ensureTargetInRange(HitResult hitResult, Vec3 pos, double distance) {
      Vec3 location = hitResult.getLocation();
      if (!location.closerThan(pos, distance)) {
         Vec3 vec = hitResult.getLocation();
         Direction approximateNearest = Direction.getApproximateNearest(vec.x - pos.x, vec.y - pos.y, vec.z - pos.z);
         return BlockHitResult.miss(vec, approximateNearest, BlockPos.containing(vec));
      } else {
         return hitResult;
      }
   }

   public static boolean method3215(Vec3 vec, double scale, AABB aabb) {
      Vec3 eyePosition = Objects.requireNonNull(mc.player).getEyePosition();
      return aabb.contains(eyePosition) || aabb.clip(eyePosition, eyePosition.add(vec.scale(scale))).isPresent();
   }

   public static BlockHitResult method3216(Vec3 eyePosition, Rotation rotation, double blockInteractionRange, Predicate<BlockHitResult> predicate) {
      Vec3 vec = rotation.method3135();
      Vec3 to = eyePosition.add(vec.x * blockInteractionRange, vec.y * blockInteractionRange, vec.z * blockInteractionRange);
      return method3217(new ClipContext(eyePosition, to, Block.OUTLINE, Fluid.NONE, mc.player), predicate);
   }

   public static BlockHitResult method3217(ClipContext clipContext, Predicate<BlockHitResult> predicate) {
      return BlockGetter.traverseBlocks(clipContext.getFrom(), clipContext.getTo(), clipContext, (var1x, pos) -> {
         BlockState blockState = mc.level.getBlockState(pos);
         FluidState fluidState = mc.level.getFluidState(pos);
         Vec3 from = var1x.getFrom();
         Vec3 to = var1x.getTo();
         VoxelShape blockShape = var1x.getBlockShape(blockState, mc.level, pos);
         BlockHitResult clipWithInteractionOverride = mc.level.clipWithInteractionOverride(from, to, pos, blockShape, blockState);
         VoxelShape fluidShape = var1x.getFluidShape(fluidState, mc.level, pos);
         BlockHitResult clip = fluidShape.clip(from, to, pos);
         double d = clipWithInteractionOverride == null ? Double.MAX_VALUE : var1x.getFrom().distanceToSqr(clipWithInteractionOverride.getLocation());
         double d2 = clip == null ? Double.MAX_VALUE : var1x.getFrom().distanceToSqr(clip.getLocation());
         if (!predicate.test(d <= d2 ? clipWithInteractionOverride : clip)) {
            return null;
         } else {
            return d <= d2 ? clipWithInteractionOverride : clip;
         }
      }, var0x -> {
         Vec3 var1x = var0x.getFrom().subtract(var0x.getTo());
         return BlockHitResult.miss(var0x.getTo(), Direction.getApproximateNearest(var1x.x, var1x.y, var1x.z), BlockPos.containing(var0x.getTo()));
      });
   }

   public static BlockHitResult method3218(Vec3 from, Vec3 to, BlockPos pos, VoxelShape voxelShape, BlockState blockState) {
      BlockHitResult clip = voxelShape.clip(from, to, pos);
      if (clip != null) {
         BlockHitResult blockHitResult = blockState.getInteractionShape(mc.level, pos).clip(from, to, pos);
         if (blockHitResult != null && blockHitResult.getLocation().subtract(from).lengthSqr() < clip.getLocation().subtract(from).lengthSqr()) {
            return clip.withDirection(blockHitResult.getDirection());
         }
      }

      return clip;
   }

   @Generated
   private TargetUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

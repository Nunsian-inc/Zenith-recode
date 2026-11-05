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
   public static BlockHitResult method3204(double var0, Rotation var2, boolean var3) {
      return method3205(Objects.requireNonNull(mc.player).getEyePosition(1.0F), var0, var2, var3);
   }

   public static BlockHitResult method3205(Vec3 var0, double var1, Rotation var3, boolean var4) {
      Entity var5 = mc.getCameraEntity();
      if (var5 == null) {
         return null;
      } else {
         Vec3 var6 = var3.method3135();
         Vec3 var7 = var0.add(var6.x * var1, var6.y * var1, var6.z * var1);
         ClientLevel var8 = mc.level;
         if (var8 == null) {
            return null;
         } else {
            Fluid var9 = var4 ? Fluid.ANY : Fluid.NONE;
            ClipContext var10 = new ClipContext(var0, var7, Block.OUTLINE, var9, var5);
            return var8.clip(var10);
         }
      }
   }

   public static HitResult method3206(double var0, Rotation var2, float var3, boolean var4) {
      Vec3 var5 = mc.getCameraEntity().getEyePosition(var3);
      Vec3 var6 = var2.method3135();
      Vec3 var7 = var5.add(var6.x * var0, var6.y * var0, var6.z * var0);
      return mc.level.clip(new ClipContext(var5, var7, Block.OUTLINE, var4 ? Fluid.ANY : Fluid.NONE, mc.getCameraEntity()));
   }

   public static BlockHitResult method3207(Vec3 var0, Vec3 var1, Block var2) {
      return method3208(var0, var1, var2, mc.player);
   }

   public static BlockHitResult method3208(Vec3 var0, Vec3 var1, Block var2, Entity var3) {
      return mc.level.clip(new ClipContext(var0, var1, var2, Fluid.NONE, var3));
   }

   public static boolean method3209(double var0, Rotation var2, LivingEntity var3) {
      return method3210(var0, var2, var1 -> var1 == var3) != null;
   }

   public static EntityHitResult method3210(double var0, Rotation var2, Predicate<Entity> var3) {
      Entity var4 = mc.getCameraEntity();
      if (var4 == null) {
         return null;
      } else {
         Vec3 var5 = var4.getEyePosition(1.0F);
         Vec3 var6 = var2.method3135();
         Vec3 var7 = var5.add(var6.x * var0, var6.y * var0, var6.z * var0);
         AABB var8 = var4.getBoundingBox().expandTowards(var6.scale(var0)).inflate(1.0, 1.0, 1.0);
         return ProjectileUtil.getEntityHitResult(var4, var5, var7, var8, var1 -> !var1.isSpectator() && var3.test(var1), var0 * var0);
      }
   }

   public static EntityHitResult method3211(Rotation var0, Predicate<Entity> var1) {
      Entity var2 = mc.getCameraEntity();
      if (var2 != null && mc.level != null) {
         Vec3 var3 = var2.getEyePosition(1.0F);
         Vec3 var4 = var0.method3135();
         Entity var5 = null;
         Vec3 var6 = null;
         double var7 = Double.MAX_VALUE;

         for (Entity var10 : mc.level.entitiesForRendering()) {
            if (var10 != var2 && !var10.isSpectator() && var1.test(var10)) {
               AABB var11 = var10.getBoundingBox().inflate(var10.getPickRadius());
               Vec3 var12;
               if (var11.contains(var3)) {
                  var12 = var3;
               } else {
                  double var13 = var3.distanceTo(var11.getCenter()) + var11.getXsize() + var11.getYsize() + var11.getZsize();
                  var var15 = var11.clip(var3, var3.add(var4.scale(var13)));
                  if (var15.isEmpty()) {
                     continue;
                  }

                  var12 = (Vec3)var15.get();
               }

               double var16 = var3.distanceToSqr(var12);
               if (var16 < var7) {
                  var5 = var10;
                  var6 = var12;
                  var7 = var16;
               }
            }
         }

         return var5 == null ? null : new EntityHitResult(var5, var6);
      } else {
         return null;
      }
   }

   public static boolean method3212(Rotation var0, Vec3 var1, AABB var2, double var3, boolean var5) {
      double var6 = Math.max(mc.player.blockInteractionRange(), var3);
      double var8 = Mth.square(var6);
      BlockHitResult var10 = method3216(var1, var0, var6, AuraModule.INSTANCE::method6362);
      double var11 = var10.getLocation().distanceToSqr(var1);
      if (var5 && var10.getType() != Type.MISS) {
         var8 = var11;
         var6 = Math.sqrt(var11);
      }

      Vec3 var13 = var0.method3135();
      Vec3 var14 = var1.add(var13.x * var6, var13.y * var6, var13.z * var6);
      Vec3 var15 = method3214(var1, var14, var2, var8);
      return var15 != null && (var15.distanceToSqr(var1) < var11 || !var5) && var15.closerThan(var1, var3);
   }

   public static HitResult method3213(Vec3 var0, Rotation var1, double var2) {
      Vec3 var5 = var1.method3135();
      Vec3 var6 = var0.add(var5.x * var2, var5.y * var2, var5.z * var2);
      return mc.level.clip(new ClipContext(var0, var6, Block.OUTLINE, Fluid.NONE, mc.player));
   }

   @Nullable
   public static Vec3 method3214(Vec3 var0, Vec3 var1, AABB var2, double var3) {
      Vec3 var5 = null;
      var var6 = var2.clip(var0, var1);
      if (var2.contains(var0)) {
         if (var3 >= 0.0) {
            var5 = var6.orElse(var0);
         }
      } else if (var6.isPresent()) {
         Vec3 var7 = (Vec3)var6.get();
         double var8 = var0.distanceToSqr(var7);
         if (var8 < var3 || var3 == 0.0) {
            var5 = var7;
         }
      }

      return var5;
   }

   private static HitResult ensureTargetInRange(HitResult var0, Vec3 var1, double var2) {
      Vec3 var4 = var0.getLocation();
      if (!var4.closerThan(var1, var2)) {
         Vec3 var5 = var0.getLocation();
         Direction var6 = Direction.getApproximateNearest(var5.x - var1.x, var5.y - var1.y, var5.z - var1.z);
         return BlockHitResult.miss(var5, var6, BlockPos.containing(var5));
      } else {
         return var0;
      }
   }

   public static boolean method3215(Vec3 var0, double var1, AABB var3) {
      Vec3 var4 = Objects.requireNonNull(mc.player).getEyePosition();
      return var3.contains(var4) || var3.clip(var4, var4.add(var0.scale(var1))).isPresent();
   }

   public static BlockHitResult method3216(Vec3 var0, Rotation var1, double var2, Predicate<BlockHitResult> var4) {
      Vec3 var5 = var1.method3135();
      Vec3 var6 = var0.add(var5.x * var2, var5.y * var2, var5.z * var2);
      return method3217(new ClipContext(var0, var6, Block.OUTLINE, Fluid.NONE, mc.player), var4);
   }

   public static BlockHitResult method3217(ClipContext var0, Predicate<BlockHitResult> var1) {
      return BlockGetter.traverseBlocks(var0.getFrom(), var0.getTo(), var0, (var1x, var2) -> {
         BlockState var3 = mc.level.getBlockState(var2);
         FluidState var4 = mc.level.getFluidState(var2);
         Vec3 var5 = var1x.getFrom();
         Vec3 var6 = var1x.getTo();
         VoxelShape var7 = var1x.getBlockShape(var3, mc.level, var2);
         BlockHitResult var8 = mc.level.clipWithInteractionOverride(var5, var6, var2, var7, var3);
         VoxelShape var9 = var1x.getFluidShape(var4, mc.level, var2);
         BlockHitResult var10 = var9.clip(var5, var6, var2);
         double var11 = var8 == null ? Double.MAX_VALUE : var1x.getFrom().distanceToSqr(var8.getLocation());
         double var13 = var10 == null ? Double.MAX_VALUE : var1x.getFrom().distanceToSqr(var10.getLocation());
         if (!var1.test(var11 <= var13 ? var8 : var10)) {
            return null;
         } else {
            return var11 <= var13 ? var8 : var10;
         }
      }, var0x -> {
         Vec3 var1x = var0x.getFrom().subtract(var0x.getTo());
         return BlockHitResult.miss(var0x.getTo(), Direction.getApproximateNearest(var1x.x, var1x.y, var1x.z), BlockPos.containing(var0x.getTo()));
      });
   }

   public static BlockHitResult method3218(Vec3 var0, Vec3 var1, BlockPos var2, VoxelShape var3, BlockState var4) {
      BlockHitResult var5 = var3.clip(var0, var1, var2);
      if (var5 != null) {
         BlockHitResult var6 = var4.getInteractionShape(mc.level, var2).clip(var0, var1, var2);
         if (var6 != null && var6.getLocation().subtract(var0).lengthSqr() < var5.getLocation().subtract(var0).lengthSqr()) {
            return var5.withDirection(var6.getDirection());
         }
      }

      return var5;
   }

   @Generated
   private TargetUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

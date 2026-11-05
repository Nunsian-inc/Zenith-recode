package org.zenith.utility.game;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.utility.misc.Handler6;
import org.zenith.utility.misc.MovementInputUtils;

public class PlayerSimulation implements ZenithAccess {
   public final Player playerEntity;
   public final PlayerSimulation.Entry entry;
   public Vec3 vec3d;
   public Vec3 vec3d2;
   public AABB box;
   public float floatValue;
   public float floatValue2;
   public boolean flag;
   public float floatValue3;
   public int intValue;
   public boolean flag2;
   public boolean flag3;
   public boolean flag4;
   public boolean flag5;
   public boolean flag6;
   public boolean flag7;
   public boolean flag8;
   public boolean flag9;
   public Pose entityPose;
   public boolean inSneakingPose;
   public boolean flag10;
   private final Object2DoubleMap<TagKey<Fluid>> object2DoubleMap;
   private final HashSet<TagKey<Fluid>> hashSet;
   private int intValue2 = 0;
   private boolean flag11 = false;

   public PlayerSimulation(
      Player var1,
      PlayerSimulation.Entry var2,
      Vec3 var3,
      Vec3 var4,
      AABB var5,
      float var6,
      float var7,
      boolean var8,
      float var9,
      int var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      boolean var15,
      boolean var16,
      boolean var17,
      boolean var18,
      Pose var19,
      boolean var20,
      boolean var21,
      Object2DoubleMap<TagKey<Fluid>> var22,
      HashSet<TagKey<Fluid>> var23
   ) {
      this.playerEntity = var1;
      this.entry = var2;
      this.vec3d = var3;
      this.vec3d2 = var4;
      this.box = var5;
      this.floatValue = var6;
      this.floatValue2 = var7;
      this.flag = var8;
      this.floatValue3 = var9;
      this.intValue = var10;
      this.flag2 = var11;
      this.flag3 = var12;
      this.flag4 = var13;
      this.flag5 = var14;
      this.flag6 = var15;
      this.flag7 = var16;
      this.flag8 = var17;
      this.flag9 = var18;
      this.entityPose = var19;
      this.inSneakingPose = var20;
      this.flag10 = var21;
      this.object2DoubleMap = var22;
      this.hashSet = var23;
   }

   public static PlayerSimulation method6031(int var0) {
      PlayerSimulation var1 = method6033(PlayerSimulation.Entry.method6092(mc.player.input.keyPresses));

      for (int var2 = 0; var2 < var0; var2++) {
         var1.tick();
      }

      return var1;
   }

   public static PlayerSimulation method6032(Player var0, int var1) {
      PlayerSimulation var2 = method6034(var0, PlayerSimulation.Entry.method6093(var0));

      for (int var3 = 0; var3 < var1; var3++) {
         var2.tick();
      }

      return var2;
   }

   public static PlayerSimulation method6033(PlayerSimulation.Entry var0) {
      LocalPlayer var1 = mc.player;
      return new PlayerSimulation(
         var1,
         var0,
         var1.position(),
         var1.getDeltaMovement(),
         var1.getBoundingBox(),
         Zenith.getInstance().getRotationManager().method7844().getFloatValue(),
         Zenith.getInstance().getRotationManager().method7844().getFloatValue2(),
         var1.isSprinting(),
         (float)var1.fallDistance,
         var1.noJumpDelay,
         var1.jumping,
         var1.isFallFlying(),
         var1.onGround(),
         var1.horizontalCollision,
         var1.verticalCollision,
         var1.isInWater(),
         var1.isSwimming(),
         var1.isUnderWater(),
         var1.getPose(),
         var1.isCrouching(),
         var1.isVisuallyCrawling(),
         zenith$fluidHeights(var1),
         zenith$eyeFluids(var1)
      );
   }

   // 26.3 replaced Entity#fluidHeight/#fluidOnEyes with EntityFluidInteraction; rebuild the same snapshot
   private static Object2DoubleArrayMap<TagKey<Fluid>> zenith$fluidHeights(net.minecraft.world.entity.Entity var0) {
      Object2DoubleArrayMap<TagKey<Fluid>> var1 = new Object2DoubleArrayMap<>();
      for (TagKey<Fluid> var2 : java.util.List.of(net.minecraft.tags.FluidTags.WATER, net.minecraft.tags.FluidTags.LAVA)) {
         double var3 = var0.getFluidHeight(var2);
         if (var3 > 0.0) {
            var1.put(var2, var3);
         }
      }

      return var1;
   }

   private static HashSet<TagKey<Fluid>> zenith$eyeFluids(net.minecraft.world.entity.Entity var0) {
      HashSet<TagKey<Fluid>> var1 = new HashSet<>();
      for (TagKey<Fluid> var2 : java.util.List.of(net.minecraft.tags.FluidTags.WATER, net.minecraft.tags.FluidTags.LAVA)) {
         if (var0.isEyeInFluid(var2)) {
            var1.add(var2);
         }
      }

      return var1;
   }

   public static PlayerSimulation method6034(Player var0, PlayerSimulation.Entry var1) {
      Vec3 var2 = var0.getPositionCodec().getBase();
      Vec3 var3 = ((Handler6)var0).zenithDLC$getServerDelta();
      return new PlayerSimulation(
         var0,
         var1,
         var2,
         var3.scale(0.5),
         var0.getDimensions(var0.getPose()).makeBoundingBox(var2),
         var0.getInterpolation() == null || var0.getInterpolation().target() == null ? var0.getYRot() : var0.getInterpolation().target().yRot(),
         var0.getInterpolation() == null || var0.getInterpolation().target() == null ? var0.getXRot() : var0.getInterpolation().target().xRot(),
         var0.isSprinting(),
         (float)var0.fallDistance,
         var0.noJumpDelay,
         var0.jumping,
         var0.isFallFlying(),
         var0.onGround(),
         var0.horizontalCollision,
         var0.verticalCollision,
         var0.isInWater(),
         var0.isSwimming(),
         var0.isUnderWater(),
         var0.getPose(),
         var0.isCrouching(),
         var0.isVisuallyCrawling(),
         zenith$fluidHeights(var0),
         zenith$eyeFluids(var0)
      );
   }

   public Vec3 getVec3d() {
      return this.vec3d;
   }

   public void tick() {
      this.intValue2++;
      this.flag11 = false;
      if (!(this.vec3d.y <= -70.0)) {
         this.method6065();
         this.method6080(FluidTags.LAVA, 0.007);
         this.method6075();
         this.method6066();
         this.method6067();
         this.method6068();
         this.entry.update();
         this.method6070();
         this.method6069();
         if (this.isFlag7() && this.entry.keyPresses.shift() && this.playerEntity.isAffectedByFluids()) {
            this.method6060();
         }

         if (this.intValue > 0) {
            this.intValue--;
         }

         this.flag2 = this.entry.keyPresses.jump();
         double var1 = this.vec3d2.x;
         double var3 = this.vec3d2.y;
         double var5 = this.vec3d2.z;
         if (Math.abs(this.vec3d2.x) < 0.003) {
            var1 = 0.0;
         }

         if (Math.abs(this.vec3d2.y) < 0.003) {
            var3 = 0.0;
         }

         if (Math.abs(this.vec3d2.z) < 0.003) {
            var5 = 0.0;
         }

         if (this.flag4) {
            this.flag3 = false;
         }

         this.vec3d2 = new Vec3(var1, var3, var5);
         if (this.flag2 && this.playerEntity.isAffectedByFluids()) {
            double var7 = this.method6064() ? this.method6079(FluidTags.LAVA) : this.method6079(FluidTags.WATER);
            boolean var9 = this.isFlag7() && var7 > 0.0;
            double var10 = this.method6063();
            if (!var9 || this.flag4 && !(var7 > var10)) {
               if (!this.method6064() || this.flag4 && !(var7 > var10)) {
                  if ((this.flag4 || var9 && var7 <= var10) && this.intValue == 0) {
                     this.method6044();
                     this.intValue = 10;
                  }
               } else {
                  this.method6061(FluidTags.LAVA);
               }
            } else {
               this.method6061(FluidTags.WATER);
            }
         } else {
            this.intValue = 0;
         }

         float var12 = this.entry.floatValue2 * 0.98F;
         float var8 = this.entry.floatValue * 0.98F;
         float var13 = 0.0F;
         if (this.method6084(MobEffects.SLOW_FALLING) || this.method6084(MobEffects.LEVITATION)) {
            this.method6043();
         }

         this.method6035(new Vec3(var12, var13, var8));
      }
   }

   private void method6035(Vec3 var1) {
      if (this.flag8 && !this.playerEntity.isPassenger()) {
         double var2 = this.method6082().y;
         double var4 = var2 < -0.2 ? 0.085 : 0.06;
         BlockPos var6 = new BlockPos(Mth.floor(this.vec3d.x), Mth.floor(this.vec3d.y + 1.0 - 0.1), Mth.floor(this.vec3d.z));
         if (var2 <= 0.0 || this.entry.keyPresses.jump() || !this.playerEntity.level().getBlockState(var6).getFluidState().isEmpty()) {
            this.vec3d2 = this.vec3d2.add(0.0, (var2 - this.vec3d2.y) * var4, 0.0);
         }
      }

      double var18 = this.vec3d2.y;
      double var19 = this.method6057();
      boolean var20 = this.vec3d2.y <= 0.0;
      if (this.isFlag7() && this.playerEntity.isAffectedByFluids()) {
         double var24 = this.vec3d.y;
         float var27 = this.isFlag() ? 0.9F : 0.8F;
         float var29 = 0.02F;
         float var31 = (float)this.method6086(Attributes.WATER_MOVEMENT_EFFICIENCY);
         if (!this.flag4) {
            var31 *= 0.5F;
         }

         if (var31 > 0.0F) {
            var27 += (0.54600006F - var27) * var31;
            var29 += (this.method6040() - var29) * var31;
         }

         if (this.method6084(MobEffects.DOLPHINS_GRACE)) {
            var27 = 0.96F;
         }

         this.method6037(var29, var1);
         this.method6041(this.vec3d2);
         Vec3 var32 = this.vec3d2;
         if (this.flag5 && this.method6046()) {
            var32 = new Vec3(var32.x, 0.2, var32.z);
         }

         this.vec3d2 = var32.multiply(var27, 0.8, var27);
         Vec3 var33 = this.method6058(var19, var20, this.vec3d2);
         this.vec3d2 = var33;
         if (this.flag5 && this.method6055(var33.x, var33.y + 0.6 - this.vec3d.y + var24, var33.z)) {
            this.vec3d2 = new Vec3(var33.x, 0.3, var33.z);
         }
      } else if (this.method6064() && this.playerEntity.isAffectedByFluids()) {
         double var23 = this.vec3d.y;
         this.method6037(0.02F, var1);
         this.method6041(this.vec3d2);
         if (this.method6079(FluidTags.LAVA) <= this.method6063()) {
            this.vec3d2 = this.vec3d2.multiply(0.5, 0.8, 0.5);
            this.vec3d2 = this.method6058(var19, var20, this.vec3d2);
         } else {
            this.vec3d2 = this.vec3d2.scale(0.5);
         }

         if (var19 != 0.0) {
            this.vec3d2 = this.vec3d2.add(0.0, -var19 / 4.0, 0.0);
         }

         if (this.flag5 && this.method6055(this.vec3d2.x, this.vec3d2.y + 0.6 - this.vec3d.y + var23, this.vec3d2.z)) {
            this.vec3d2 = new Vec3(this.vec3d2.x, 0.3, this.vec3d2.z);
         }
      } else if (this.flag3) {
         Vec3 var9 = this.vec3d2;
         Vec3 var10 = this.method6082();
         float var11 = this.floatValue2 * (float) (Math.PI / 180.0);
         double var12 = Math.sqrt(var10.x * var10.x + var10.z * var10.z);
         double var14 = this.vec3d2.horizontalDistance();
         double var16 = Mth.square(Mth.cos(var11));
         var9 = this.vec3d2.add(0.0, var19 * (-1.0 + var16 * 0.75), 0.0);
         if (var9.y < 0.0 && var12 > 0.0) {
            double var7 = var9.y * -0.1 * var16;
            var9 = var9.add(var10.x * var7 / var12, var7, var10.z * var7 / var12);
         }

         if (var11 < 0.0F && var12 > 0.0) {
            double var21 = var14 * -Mth.sin(var11) * 0.04;
            var9 = var9.add(-var10.x * var21 / var12, var21 * 3.2, -var10.z * var21 / var12);
         }

         if (var12 > 0.0) {
            var9 = var9.add((var10.x / var12 * var14 - var9.x) * 0.1, 0.0, (var10.z / var12 * var14 - var9.z) * 0.1);
         }

         this.vec3d2 = var9.multiply(0.99, 0.98, 0.99);
         this.method6041(this.vec3d2);
      } else {
         BlockPos var22 = this.method6062();
         float var8 = this.flag4 ? this.playerEntity.level().getBlockState(var22).getBlock().getFriction() : 1.0F;
         float var26 = var8 * 0.91F;
         Vec3 var28 = this.method6036(var1, var8);
         double var30 = var28.y;
         if (this.method6084(MobEffects.LEVITATION)) {
            MobEffectInstance var13 = this.method6085(MobEffects.LEVITATION);
            if (var13 != null) {
               var30 += (0.05 * (var13.getAmplifier() + 1) - var28.y) * 0.2;
            }
         } else if (this.playerEntity.level().isClientSide() && !this.playerEntity.level().hasChunkAt(var22)) {
            var30 = this.vec3d.y > this.playerEntity.level().getMinY() ? -0.1 : 0.0;
         } else if (var19 != 0.0) {
            var30 -= var19;
         }

         if (this.playerEntity.shouldDiscardFriction()) {
            this.vec3d2 = new Vec3(var28.x, var30, var28.z);
         } else {
            this.vec3d2 = new Vec3(var28.x * var26, var30 * 0.98F, var28.z * var26);
         }
      }

      if (this.playerEntity.getAbilities().flying && !this.playerEntity.isPassenger()) {
         this.vec3d2 = new Vec3(this.vec3d2.x, var18 * 0.6, this.vec3d2.z);
         this.method6043();
      }
   }

   private Vec3 method6036(Vec3 var1, float var2) {
      this.method6037(this.method6038(var2), var1);
      this.vec3d2 = this.method6045(this.vec3d2);
      this.method6041(this.vec3d2);
      Vec3 var3 = this.vec3d2;
      BlockPos var4 = this.method6089(this.vec3d);
      BlockState var5 = this.method6090(var4);
      if ((this.flag5 || this.flag2)
         && (this.method6046() || var5 != null && var5.is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(this.playerEntity))) {
         var3 = new Vec3(var3.x, 0.2, var3.z);
      }

      return var3;
   }

   private void method6037(float var1, Vec3 var2) {
      Vec3 var3 = Entity.getInputVector(var2, var1, this.floatValue);
      this.vec3d2 = this.vec3d2.add(var3);
   }

   private float method6038(float var1) {
      return this.flag4 ? this.method6040() * (0.21600002F / (var1 * var1 * var1)) : this.method6039();
   }

   private float method6039() {
      return 0.02F;
   }

   private float method6040() {
      return (float)this.method6086(Attributes.MOVEMENT_SPEED);
   }

   private void method6041(Vec3 var1) {
      Vec3 var2 = this.method6048(var1);
      Vec3 var3 = this.method6042(var2);
      if (var3.lengthSqr() > 1.0E-7) {
         this.vec3d = this.vec3d.add(var3);
         this.box = this.box.move(var3);
      }

      boolean var4 = !Mth.equal(var2.x, var3.x);
      boolean var5 = !Mth.equal(var2.z, var3.z);
      this.flag5 = var4 || var5;
      this.flag6 = var2.y != var3.y;
      this.flag4 = this.flag6 && var2.y < 0.0;
      if (!this.isFlag7()) {
         this.method6065();
      }

      if (this.flag4) {
         this.method6043();
      } else if (var2.y < 0.0) {
         this.floatValue3 = this.floatValue3 - (float)var2.y;
      }

      Vec3 var6 = this.vec3d2;
      if (this.flag5 || this.flag6) {
         this.vec3d2 = new Vec3(var4 ? 0.0 : var6.x, this.flag4 ? 0.0 : var6.y, var5 ? 0.0 : var6.z);
      }

      float var7 = this.method6059();
      this.vec3d2 = this.vec3d2.multiply(var7, 1.0, var7);
   }

   private Vec3 method6042(Vec3 var1) {
      AABB var2 = this.box;
      java.util.List<net.minecraft.world.phys.shapes.VoxelShape> var3 = Collections.emptyList();
      Vec3 var4;
      if (var1.lengthSqr() == 0.0) {
         var4 = var1;
      } else {
         var4 = Entity.collideBoundingBox(this.playerEntity, var1, var2, this.playerEntity.level(), var3);
      }

      boolean var5 = var1.x != var4.x;
      boolean var6 = var1.y != var4.y;
      boolean var7 = var1.z != var4.z;
      boolean var8 = this.flag4 || var6 && var1.y < 0.0;
      if (this.playerEntity.maxUpStep() > 0.0F && var8 && (var5 || var7)) {
         Vec3 var9 = Entity.collideBoundingBox(
            this.playerEntity, new Vec3(var1.x, this.playerEntity.maxUpStep(), var1.z), var2, this.playerEntity.level(), var3
         );
         Vec3 var10 = Entity.collideBoundingBox(
            this.playerEntity,
            new Vec3(0.0, this.playerEntity.maxUpStep(), 0.0),
            var2.expandTowards(var1.x, 0.0, var1.z),
            this.playerEntity.level(),
            var3
         );
         Vec3 var11 = Entity.collideBoundingBox(
               this.playerEntity, new Vec3(var1.x, 0.0, var1.z), var2.move(var10), this.playerEntity.level(), var3
            )
            .add(var10);
         if (var10.y < this.playerEntity.maxUpStep() && var11.horizontalDistanceSqr() > var9.horizontalDistanceSqr()) {
            var9 = var11;
         }

         if (var9.horizontalDistanceSqr() > var4.horizontalDistanceSqr()) {
            return var9.add(
               Entity.collideBoundingBox(
                  this.playerEntity, new Vec3(0.0, -var9.y + var1.y, 0.0), var2.move(var9), this.playerEntity.level(), var3
               )
            );
         }
      }

      return var4;
   }

   private void method6043() {
      this.floatValue3 = 0.0F;
   }

   public void method6044() {
      float var1 = this.method6052();
      if (!(var1 <= 1.0E-5F)) {
         this.vec3d2 = new Vec3(this.vec3d2.x, Math.max((double)var1, this.vec3d2.y), this.vec3d2.z);
         if (this.isFlag()) {
            float var2 = (float)Math.toRadians(this.floatValue);
            this.vec3d2 = this.vec3d2.add(-Mth.sin(var2) * 0.2, 0.0, Mth.cos(var2) * 0.2);
         }
      }
   }

   private Vec3 method6045(Vec3 var1) {
      if (!this.method6046()) {
         return var1;
      } else {
         this.method6043();
         double var2 = Mth.clamp(var1.x, -0.15F, 0.15F);
         double var4 = Mth.clamp(var1.z, -0.15F, 0.15F);
         double var6 = Math.max(var1.y, -0.15F);
         if (var6 < 0.0 && !this.method6090(this.method6089(this.vec3d)).is(Blocks.SCAFFOLDING) && this.method6072()) {
            var6 = 0.0;
         }

         return new Vec3(var2, var6, var4);
      }
   }

   public boolean method6046() {
      BlockPos var1 = this.method6089(this.vec3d);
      BlockState var2 = this.method6090(var1);
      return var2.is(BlockTags.CLIMBABLE) ? true : var2.getBlock() instanceof TrapDoorBlock && this.method6047(var1, var2);
   }

   private boolean method6047(BlockPos var1, BlockState var2) {
      if (!var2.getValue(TrapDoorBlock.OPEN)) {
         return false;
      } else {
         BlockState var3 = this.playerEntity.level().getBlockState(var1.below());
         return var3.is(Blocks.LADDER) && var3.getValue(LadderBlock.FACING).equals(var2.getValue(TrapDoorBlock.FACING));
      }
   }

   private Vec3 method6048(Vec3 var1) {
      double var2 = this.playerEntity.maxUpStep();
      if (var1.y <= 0.0 && !this.playerEntity.getAbilities().flying && this.method6049() && this.method6050(var2)) {
         double var4 = var1.x;
         double var6 = var1.z;
         double var8 = 0.05;
         double var10 = Math.signum(var4) * var8;

         double var12;
         for (var12 = Math.signum(var6) * var8; var4 != 0.0 && this.method6051(var4, 0.0, var2); var4 -= var10) {
            if (Math.abs(var4) <= var8) {
               var4 = 0.0;
               break;
            }
         }

         while (var6 != 0.0 && this.method6051(0.0, var6, var2)) {
            if (Math.abs(var6) <= var8) {
               var6 = 0.0;
               break;
            }

            var6 -= var12;
         }

         while (var4 != 0.0 && var6 != 0.0 && this.method6051(var4, var6, var2)) {
            if (Math.abs(var4) <= var8) {
               var4 = 0.0;
            } else {
               var4 -= var10;
            }

            if (Math.abs(var6) <= var8) {
               var6 = 0.0;
               break;
            }

            var6 -= var12;
         }

         if (var1.x != var4 || var1.z != var6) {
            this.flag11 = true;
         }

         var1 = new Vec3(var4, var1.y, var6);
      }

      return var1;
   }

   public boolean method6049() {
      return this.entry.keyPresses.shift() || this.entry.flag;
   }

   private boolean method6050(double var1) {
      return this.flag4 || this.floatValue3 < var1 && !this.method6051(0.0, 0.0, var1 - this.floatValue3);
   }

   private boolean method6051(double var1, double var3, double var5) {
      AABB var7 = this.box;
      return this.playerEntity
         .level()
         .noCollision(this.playerEntity, new AABB(var7.minX + var1, var7.minY - var5 - 1.0E-5, var7.minZ + var3, var7.maxX + var1, var7.minY, var7.maxZ + var3));
   }

   private boolean isFlag() {
      return this.flag;
   }

   private float method6052() {
      return (float)this.method6086(Attributes.JUMP_STRENGTH) * this.method6054() + this.method6053();
   }

   private float method6053() {
      if (this.method6084(MobEffects.JUMP_BOOST)) {
         MobEffectInstance var1 = this.method6085(MobEffects.JUMP_BOOST);
         return 0.1F * (var1.getAmplifier() + 1);
      } else {
         return 0.0F;
      }
   }

   private float method6054() {
      float var1 = 0.0F;
      Block var2 = this.method6090(this.method6089(this.vec3d)).getBlock();
      if (var2 != null) {
         var1 = var2.getJumpFactor();
      }

      float var3 = 0.0F;
      Block var4 = this.method6090(this.method6062()).getBlock();
      if (var4 != null) {
         var3 = var4.getJumpFactor();
      }

      return var1 == 1.0F ? var3 : var1;
   }

   private boolean method6055(double var1, double var3, double var5) {
      return this.method6056(this.box.move(var1, var3, var5));
   }

   private boolean method6056(AABB var1) {
      return this.playerEntity.level().noCollision(this.playerEntity, var1) && !this.playerEntity.level().containsAnyLiquid(var1);
   }

   private double method6057() {
      double var1 = this.playerEntity.isNoGravity() ? 0.0 : this.method6086(Attributes.GRAVITY);
      return this.vec3d2.y <= 0.0 && this.method6084(MobEffects.SLOW_FALLING) ? Math.min(var1, 0.01) : var1;
   }

   private Vec3 method6058(double var1, boolean var3, Vec3 var4) {
      if (var1 != 0.0 && !this.isFlag()) {
         double var5 = var3 && Math.abs(var4.y - 0.005) >= 0.003 && Math.abs(var4.y - var1 / 16.0) < 0.003 ? -0.003 : var4.y - var1 / 16.0;
         return new Vec3(var4.x, var5, var4.z);
      } else {
         return var4;
      }
   }

   private float method6059() {
      BlockState var1 = this.method6090(this.method6089(this.vec3d));
      float var2 = var1.getBlock().getSpeedFactor();
      if (!var1.is(Blocks.WATER) && !var1.is(Blocks.BUBBLE_COLUMN)) {
         var2 = var2 == 1.0F ? this.method6090(this.method6062()).getBlock().getSpeedFactor() : var2;
      }

      return Mth.lerp((float)this.method6086(Attributes.MOVEMENT_EFFICIENCY), var2, 1.0F);
   }

   private void method6060() {
      this.vec3d2 = this.vec3d2.add(0.0, -0.04F, 0.0);
   }

   private void method6061(TagKey<Fluid> var1) {
      this.vec3d2 = this.vec3d2.add(0.0, 0.04F, 0.0);
   }

   private BlockPos method6062() {
      return BlockPos.containing(this.vec3d.x, this.box.minY - 0.5000001, this.vec3d.z);
   }

   private double method6063() {
      return this.method6077() < 0.4 ? 0.0 : 0.4;
   }

   private boolean isFlag7() {
      return this.flag7;
   }

   public boolean method6064() {
      return this.object2DoubleMap.getDouble(FluidTags.LAVA) > 0.0;
   }

   private void method6065() {
      if (this.playerEntity.getVehicle() instanceof Boat) {
         Boat var1 = (Boat)this.playerEntity.getVehicle();
         if (!var1.isUnderWater()) {
            this.flag7 = false;
            return;
         }
      }

      if (this.method6080(FluidTags.WATER, 0.014)) {
         this.method6043();
         this.flag7 = true;
      } else {
         this.flag7 = false;
      }
   }

   private void method6066() {
      if (this.flag8) {
         this.flag8 = this.isFlag() && this.isFlag7() && !this.playerEntity.isPassenger();
      } else {
         this.flag8 = this.isFlag()
            && this.method6078()
            && !this.playerEntity.isPassenger()
            && this.playerEntity.level().getFluidState(this.method6089(this.vec3d)).is(FluidTags.WATER);
      }
   }

   private void method6067() {
      if (!this.method6073(Pose.SWIMMING)) {
         this.flag10 = this.entityPose == Pose.SWIMMING && !this.isFlag7();
      } else {
         Pose var1;
         if (this.flag3) {
            var1 = Pose.FALL_FLYING;
         } else if (this.playerEntity.isSleeping()) {
            var1 = Pose.SLEEPING;
         } else if (this.flag8) {
            var1 = Pose.SWIMMING;
         } else if (this.playerEntity.isAutoSpinAttack()) {
            var1 = Pose.SPIN_ATTACK;
         } else if (this.method6072() && !this.playerEntity.getAbilities().flying) {
            var1 = Pose.CROUCHING;
         } else {
            var1 = Pose.STANDING;
         }

         Pose var2;
         if (this.playerEntity.isSpectator() || this.playerEntity.isPassenger() || this.method6073(var1)) {
            var2 = var1;
         } else if (this.method6073(Pose.CROUCHING)) {
            var2 = Pose.CROUCHING;
         } else {
            var2 = Pose.SWIMMING;
         }

         this.method6074(var2);
         this.flag10 = this.entityPose == Pose.SWIMMING && !this.isFlag7();
      }
   }

   private void method6068() {
      this.inSneakingPose = !this.playerEntity.getAbilities().flying
         && !this.flag8
         && !this.playerEntity.isPassenger()
         && this.method6073(Pose.CROUCHING)
         && (this.method6072() || !this.playerEntity.isSleeping() && !this.method6073(Pose.STANDING));
   }

   private void method6069() {
      if (this.playerEntity.isUsingItem() && !this.playerEntity.isPassenger()) {
         this.entry.floatValue2 *= 0.2F;
         this.entry.floatValue *= 0.2F;
      }

      if (this.shouldSlowDown()) {
         float var1 = (float)this.method6086(Attributes.SNEAKING_SPEED);
         this.entry.floatValue2 *= var1;
         this.entry.floatValue *= var1;
      }
   }

   private void method6070() {
      if (this.shouldStopSprinting()) {
         this.flag = false;
      }

      if ((!this.isFlag7() || this.method6078()) && this.entry.keyPresses.sprint() && this.canStartSprinting()) {
         this.flag = true;
      }

      if (this.flag) {
         boolean var1 = !this.method6071() || !this.canSprint();
         boolean var2 = var1 || this.flag5 && !this.playerEntity.minorHorizontalCollision || this.isFlag7() && !this.method6078();
         if (this.flag8) {
            if (!this.flag4 && !this.entry.keyPresses.shift() && var1 || !this.isFlag7()) {
               this.flag = false;
            }
         } else if (var2) {
            this.flag = false;
         }
      }
   }

   private boolean canStartSprinting() {
      return !this.flag
         && this.isWalking()
         && this.canSprint()
         && !this.playerEntity.isUsingItem()
         && !this.method6084(MobEffects.BLINDNESS)
         && !this.playerEntity.isPassenger()
         && !this.flag3
         && (!this.shouldSlowDown() || this.method6078());
   }

   private boolean isWalking() {
      return this.method6078() ? this.method6071() : this.entry.floatValue >= 0.8F;
   }

   private boolean shouldStopSprinting() {
      return this.flag3
         || this.method6084(MobEffects.BLINDNESS)
         || this.shouldSlowDown()
         || this.playerEntity.isPassenger() && !this.isRidingCamel()
         || this.playerEntity.isUsingItem() && !this.playerEntity.isPassenger() && !this.method6078();
   }

   private boolean isRidingCamel() {
      Entity var1 = this.playerEntity.getVehicle();
      return var1 != null && var1.getType() == net.minecraft.world.entity.EntityTypes.CAMEL;
   }

   private boolean method6071() {
      return this.entry.floatValue > 1.0E-5F;
   }

   private boolean canSprint() {
      return this.playerEntity.isPassenger() || this.playerEntity.getFoodData().getFoodLevel() > 6.0F || this.playerEntity.getAbilities().mayfly;
   }

   private boolean shouldSlowDown() {
      return this.inSneakingPose || this.flag10;
   }

   private boolean method6072() {
      return this.entry.keyPresses.shift();
   }

   private boolean method6073(Pose var1) {
      return this.playerEntity.level().noCollision(this.playerEntity, this.playerEntity.getDimensions(var1).makeBoundingBox(this.vec3d).deflate(1.0E-7));
   }

   private void method6074(Pose var1) {
      if (this.entityPose != var1 || !this.box.equals(this.playerEntity.getDimensions(var1).makeBoundingBox(this.vec3d))) {
         this.entityPose = var1;
         this.box = this.playerEntity.getDimensions(var1).makeBoundingBox(this.vec3d);
      }
   }

   private void method6075() {
      this.flag9 = this.hashSet.contains(FluidTags.WATER);
      this.hashSet.clear();
      double var1 = this.method6076() - 0.11111111F;
      if (!(
         this.playerEntity.getVehicle() instanceof Boat var4
            && !var4.isUnderWater()
            && var4.getBoundingBox().maxY >= var1
            && var4.getBoundingBox().minY <= var1
      )) {
         BlockPos var8 = BlockPos.containing(this.vec3d.x, var1, this.vec3d.z);
         FluidState var5 = this.playerEntity.level().getFluidState(var8);
         double var6 = var8.getY() + var5.getHeight(this.playerEntity.level(), var8);
         if (var6 > var1) {
            this.hashSet.addAll(var5.typeHolder().tags().toList());
         }
      }
   }

   private double method6076() {
      return this.vec3d.y + this.method6077();
   }

   private float method6077() {
      return this.playerEntity.getDimensions(this.entityPose).eyeHeight();
   }

   public boolean method6078() {
      return this.flag9 && this.isFlag7();
   }

   private double method6079(TagKey<Fluid> var1) {
      return this.object2DoubleMap.getDouble(var1);
   }

   private boolean method6080(TagKey<Fluid> var1, double var2) {
      if (this.method6081()) {
         return false;
      } else {
         AABB var4 = this.box.deflate(0.001);
         int var5 = Mth.floor(var4.minX);
         int var6 = Mth.ceil(var4.maxX);
         int var7 = Mth.floor(var4.minY);
         int var8 = Mth.ceil(var4.maxY);
         int var9 = Mth.floor(var4.minZ);
         int var10 = Mth.ceil(var4.maxZ);
         double var11 = 0.0;
         boolean var13 = true;
         boolean var14 = false;
         Vec3 var15 = Vec3.ZERO;
         int var16 = 0;
         MutableBlockPos var17 = new MutableBlockPos();

         for (int var18 = var5; var18 < var6; var18++) {
            for (int var19 = var7; var19 < var8; var19++) {
               for (int var20 = var9; var20 < var10; var20++) {
                  var17.set(var18, var19, var20);
                  FluidState var21 = this.playerEntity.level().getFluidState(var17);
                  if (var21.is(var1)) {
                     double var22 = var19 + var21.getHeight(this.playerEntity.level(), var17);
                     if (var22 >= var4.minY) {
                        var14 = true;
                        var11 = Math.max(var22 - var4.minY, var11);
                        if (var13) {
                           Vec3 var24 = var21.getFlow(this.playerEntity.level(), var17);
                           if (var11 < 0.4) {
                              var24 = var24.scale(var11);
                           }

                           var15 = var15.add(var24);
                           var16++;
                        }
                     }
                  }
               }
            }
         }

         if (var15.length() > 0.0) {
            if (var16 > 0) {
               var15 = var15.scale(1.0 / var16);
            }

            var15 = var15.scale(var2);
            if (Math.abs(this.vec3d2.x) < 0.003 && Math.abs(this.vec3d2.z) < 0.003 && var15.length() < 0.0045) {
               var15 = var15.normalize().scale(0.0045);
            }

            this.vec3d2 = this.vec3d2.add(var15);
         }

         this.object2DoubleMap.put(var1, var11);
         return var14;
      }
   }

   private boolean method6081() {
      AABB var1 = this.box.inflate(1.0);
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minZ);
      int var5 = Mth.ceil(var1.maxZ);
      return !this.playerEntity.level().hasChunksAt(var2, var4, var3, var5);
   }

   private Vec3 method6082() {
      return this.method6083(this.floatValue2, this.floatValue);
   }

   private Vec3 method6083(float var1, float var2) {
      float var3 = (float)(var1 * Math.PI / 180.0);
      float var4 = (float)(-var2 * Math.PI / 180.0);
      float var5 = Mth.cos(var4);
      float var6 = Mth.sin(var4);
      float var7 = Mth.cos(var3);
      float var8 = Mth.sin(var3);
      return new Vec3(var6 * var7, -var8, var5 * var7);
   }

   public boolean method6084(Holder<MobEffect> var1) {
      MobEffectInstance var2 = this.playerEntity.getEffect(var1);
      return var2 != null && var2.getDuration() >= this.intValue2;
   }

   private MobEffectInstance method6085(Holder<MobEffect> var1) {
      MobEffectInstance var2 = this.playerEntity.getEffect(var1);
      return var2 != null && var2.getDuration() >= this.intValue2 ? var2 : null;
   }

   public double method6086(Holder<Attribute> var1) {
      return this.playerEntity.getAttributes().getValue(var1);
   }

   public PlayerSimulation method6087() {
      return new PlayerSimulation(
         this.playerEntity,
         this.entry.method6091(),
         this.vec3d,
         this.vec3d2,
         this.box,
         this.floatValue,
         this.floatValue2,
         this.flag,
         this.floatValue3,
         this.intValue,
         this.flag2,
         this.flag3,
         this.flag4,
         this.flag5,
         this.flag6,
         this.flag7,
         this.flag8,
         this.flag9,
         this.entityPose,
         this.inSneakingPose,
         this.flag10,
         new Object2DoubleArrayMap(this.object2DoubleMap),
         new HashSet<>(this.hashSet)
      );
   }

   public BlockPos method6088() {
      return new BlockPos(Mth.floor(this.vec3d.x), Mth.floor(this.vec3d.y), Mth.floor(this.vec3d.z));
   }

   public BlockPos method6089(Vec3 var1) {
      return new BlockPos(Mth.floor(var1.x), Mth.floor(var1.y), Mth.floor(var1.z));
   }

   public BlockState method6090(BlockPos var1) {
      return this.playerEntity.level().getBlockState(var1);
   }

   public static class Entry extends ClientInput {
      public boolean flag = false;
      public float floatValue;
      public float floatValue2;
      public Input keyPresses;
      public static final double CONST_doubleValue = 0.121;

      public Entry(Input var1) {
         this.keyPresses = var1;
      }

      private PlayerSimulation.Entry method6091() {
         PlayerSimulation.Entry var1 = new PlayerSimulation.Entry(this.keyPresses);
         var1.flag = this.flag;
         var1.floatValue = this.floatValue;
         var1.floatValue2 = this.floatValue2;
         return var1;
      }

      public void update() {
         if (this.keyPresses.forward() != this.keyPresses.backward()) {
            this.floatValue = this.keyPresses.forward() ? 1.0F : -1.0F;
         } else {
            this.floatValue = 0.0F;
         }

         if (this.keyPresses.left() == this.keyPresses.right()) {
            this.floatValue2 = 0.0F;
         } else {
            this.floatValue2 = this.keyPresses.left() ? 1.0F : -1.0F;
         }
      }

      @Override
      public String toString() {
         return "SimulatedPlayerInput(forwards={"
            + this.keyPresses.forward()
            + "}, backwards={"
            + this.keyPresses.backward()
            + "}, left={"
            + this.keyPresses.left()
            + "}, right={"
            + this.keyPresses.right()
            + "}, jumping={"
            + this.keyPresses.jump()
            + "}, sprinting="
            + this.keyPresses.sprint()
            + ", slowDown="
            + this.keyPresses.shift()
            + ")";
      }

      public static PlayerSimulation.Entry method6092(Input var0) {
         return new PlayerSimulation.Entry(var0);
      }

      public static PlayerSimulation.Entry method6093(Player var0) {
         Vec3 var1 = var0.position().subtract(new Vec3(var0.xo, var0.yo, var0.zo));
         double var2 = var1.horizontalDistanceSqr();
         Input var4 = new Input(false, false, false, false, !var0.onGround(), var0.isShiftKeyDown(), var2 >= 0.014641);
         if (var2 > 0.0025000000000000005) {
            double var5 = MovementInputUtils.method7424(var1, var0.getYRot());
            double var7 = Mth.wrapDegrees(var5);
            var4 = MovementInputUtils.method7428(var4, var7);
         }

         return new PlayerSimulation.Entry(var4);
      }
   }
}

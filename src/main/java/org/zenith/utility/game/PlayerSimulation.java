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
import org.zenith.utility.misc.ServerPositionTracker;
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
      Player playerEntity,
      PlayerSimulation.Entry entry,
      Vec3 vec3d,
      Vec3 vec3d2,
      AABB box,
      float floatValue,
      float xRot,
      boolean flag,
      float fallDistance,
      int intValue,
      boolean jumping,
      boolean fallFlying,
      boolean flag2,
      boolean horizontalCollision,
      boolean verticalCollision,
      boolean inWater,
      boolean swimming,
      boolean underWater,
      Pose entityPose,
      boolean inSneakingPose,
      boolean visuallyCrawling,
      Object2DoubleMap<TagKey<Fluid>> object2DoubleMap,
      HashSet<TagKey<Fluid>> hashSet
   ) {
      this.playerEntity = playerEntity;
      this.entry = entry;
      this.vec3d = vec3d;
      this.vec3d2 = vec3d2;
      this.box = box;
      this.floatValue = floatValue;
      this.floatValue2 = xRot;
      this.flag = flag;
      this.floatValue3 = fallDistance;
      this.intValue = intValue;
      this.flag2 = jumping;
      this.flag3 = fallFlying;
      this.flag4 = flag2;
      this.flag5 = horizontalCollision;
      this.flag6 = verticalCollision;
      this.flag7 = inWater;
      this.flag8 = swimming;
      this.flag9 = underWater;
      this.entityPose = entityPose;
      this.inSneakingPose = inSneakingPose;
      this.flag10 = visuallyCrawling;
      this.object2DoubleMap = object2DoubleMap;
      this.hashSet = hashSet;
   }

   public static PlayerSimulation method6031(int n) {
      PlayerSimulation playerSimulation = method6033(PlayerSimulation.Entry.method6092(mc.player.input.keyPresses));

      for (int i = 0; i < n; i++) {
         playerSimulation.tick();
      }

      return playerSimulation;
   }

   public static PlayerSimulation method6032(Player player, int scale) {
      PlayerSimulation playerSimulation = method6034(player, PlayerSimulation.Entry.method6093(player));

      for (int i = 0; i < scale; i++) {
         playerSimulation.tick();
      }

      return playerSimulation;
   }

   public static PlayerSimulation method6033(PlayerSimulation.Entry entry) {
      LocalPlayer player = mc.player;
      return new PlayerSimulation(
         player,
         entry,
         player.position(),
         player.getDeltaMovement(),
         player.getBoundingBox(),
         Zenith.getInstance().getRotationManager().getEffectiveRotation().getFloatValue(),
         Zenith.getInstance().getRotationManager().getEffectiveRotation().getFloatValue2(),
         player.isSprinting(),
         (float)player.fallDistance,
         player.noJumpDelay,
         player.jumping,
         player.isFallFlying(),
         player.onGround(),
         player.horizontalCollision,
         player.verticalCollision,
         player.isInWater(),
         player.isSwimming(),
         player.isUnderWater(),
         player.getPose(),
         player.isCrouching(),
         player.isVisuallyCrawling(),
         zenith$fluidHeights(player),
         zenith$eyeFluids(player)
      );
   }

   // 26.3 replaced Entity#fluidHeight/#fluidOnEyes with EntityFluidInteraction; rebuild the same snapshot
   private static Object2DoubleArrayMap<TagKey<Fluid>> zenith$fluidHeights(net.minecraft.world.entity.Entity player) {
      Object2DoubleArrayMap<TagKey<Fluid>> object2DoubleArrayMap = new Object2DoubleArrayMap<>();
      for (TagKey<Fluid> type : java.util.List.of(net.minecraft.tags.FluidTags.WATER, net.minecraft.tags.FluidTags.LAVA)) {
         double fluidHeight = player.getFluidHeight(type);
         if (fluidHeight > 0.0) {
            object2DoubleArrayMap.put(type, fluidHeight);
         }
      }

      return object2DoubleArrayMap;
   }

   private static HashSet<TagKey<Fluid>> zenith$eyeFluids(net.minecraft.world.entity.Entity player) {
      HashSet<TagKey<Fluid>> tagKeys = new HashSet<>();
      for (TagKey<Fluid> type : java.util.List.of(net.minecraft.tags.FluidTags.WATER, net.minecraft.tags.FluidTags.LAVA)) {
         if (player.isEyeInFluid(type)) {
            tagKeys.add(type);
         }
      }

      return tagKeys;
   }

   public static PlayerSimulation method6034(Player player, PlayerSimulation.Entry entry) {
      Vec3 base = player.getPositionCodec().getBase();
      Vec3 zenithDLC$getServerDelta = ((ServerPositionTracker)player).zenithDLC$getServerDelta();
      return new PlayerSimulation(
         player,
         entry,
         base,
         zenithDLC$getServerDelta.scale(0.5),
         player.getDimensions(player.getPose()).makeBoundingBox(base),
         player.getInterpolation() == null || player.getInterpolation().target() == null ? player.getYRot() : player.getInterpolation().target().yRot(),
         player.getInterpolation() == null || player.getInterpolation().target() == null ? player.getXRot() : player.getInterpolation().target().xRot(),
         player.isSprinting(),
         (float)player.fallDistance,
         player.noJumpDelay,
         player.jumping,
         player.isFallFlying(),
         player.onGround(),
         player.horizontalCollision,
         player.verticalCollision,
         player.isInWater(),
         player.isSwimming(),
         player.isUnderWater(),
         player.getPose(),
         player.isCrouching(),
         player.isVisuallyCrawling(),
         zenith$fluidHeights(player),
         zenith$eyeFluids(player)
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
         double x = this.vec3d2.x;
         double y = this.vec3d2.y;
         double z = this.vec3d2.z;
         if (Math.abs(this.vec3d2.x) < 0.003) {
            x = 0.0;
         }

         if (Math.abs(this.vec3d2.y) < 0.003) {
            y = 0.0;
         }

         if (Math.abs(this.vec3d2.z) < 0.003) {
            z = 0.0;
         }

         if (this.flag4) {
            this.flag3 = false;
         }

         this.vec3d2 = new Vec3(x, y, z);
         if (this.flag2 && this.playerEntity.isAffectedByFluids()) {
            double d = this.method6064() ? this.method6079(FluidTags.LAVA) : this.method6079(FluidTags.WATER);
            boolean flag = this.isFlag7() && d > 0.0;
            double d2 = this.method6063();
            if (!flag || this.flag4 && !(d > d2)) {
               if (!this.method6064() || this.flag4 && !(d > d2)) {
                  if ((this.flag4 || flag && d <= d2) && this.intValue == 0) {
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

         float f = this.entry.floatValue2 * 0.98F;
         float z2 = this.entry.floatValue * 0.98F;
         float y2 = 0.0F;
         if (this.method6084(MobEffects.SLOW_FALLING) || this.method6084(MobEffects.LEVITATION)) {
            this.method6043();
         }

         this.method6035(new Vec3(f, y2, z2));
      }
   }

   private void method6035(Vec3 vec) {
      if (this.flag8 && !this.playerEntity.isPassenger()) {
         double y = this.method6082().y;
         double d = y < -0.2 ? 0.085 : 0.06;
         BlockPos pos = new BlockPos(Mth.floor(this.vec3d.x), Mth.floor(this.vec3d.y + 1.0 - 0.1), Mth.floor(this.vec3d.z));
         if (y <= 0.0 || this.entry.keyPresses.jump() || !this.playerEntity.level().getBlockState(pos).getFluidState().isEmpty()) {
            this.vec3d2 = this.vec3d2.add(0.0, (y - this.vec3d2.y) * d, 0.0);
         }
      }

      double y = this.vec3d2.y;
      double d = this.method6057();
      boolean flag = this.vec3d2.y <= 0.0;
      if (this.isFlag7() && this.playerEntity.isAffectedByFluids()) {
         double y2 = this.vec3d.y;
         float xScale = this.isFlag() ? 0.9F : 0.8F;
         float speed = 0.02F;
         float f = (float)this.method6086(Attributes.WATER_MOVEMENT_EFFICIENCY);
         if (!this.flag4) {
            f *= 0.5F;
         }

         if (f > 0.0F) {
            xScale += (0.54600006F - xScale) * f;
            speed += (this.method6040() - speed) * f;
         }

         if (this.method6084(MobEffects.DOLPHINS_GRACE)) {
            xScale = 0.96F;
         }

         this.method6037(speed, vec);
         this.method6041(this.vec3d2);
         Vec3 vec3d2 = this.vec3d2;
         if (this.flag5 && this.method6046()) {
            vec3d2 = new Vec3(vec3d2.x, 0.2, vec3d2.z);
         }

         this.vec3d2 = vec3d2.multiply(xScale, 0.8, xScale);
         Vec3 vec3d22 = this.method6058(d, flag, this.vec3d2);
         this.vec3d2 = vec3d22;
         if (this.flag5 && this.method6055(vec3d22.x, vec3d22.y + 0.6 - this.vec3d.y + y2, vec3d22.z)) {
            this.vec3d2 = new Vec3(vec3d22.x, 0.3, vec3d22.z);
         }
      } else if (this.method6064() && this.playerEntity.isAffectedByFluids()) {
         double y2 = this.vec3d.y;
         this.method6037(0.02F, vec);
         this.method6041(this.vec3d2);
         if (this.method6079(FluidTags.LAVA) <= this.method6063()) {
            this.vec3d2 = this.vec3d2.multiply(0.5, 0.8, 0.5);
            this.vec3d2 = this.method6058(d, flag, this.vec3d2);
         } else {
            this.vec3d2 = this.vec3d2.scale(0.5);
         }

         if (d != 0.0) {
            this.vec3d2 = this.vec3d2.add(0.0, -d / 4.0, 0.0);
         }

         if (this.flag5 && this.method6055(this.vec3d2.x, this.vec3d2.y + 0.6 - this.vec3d.y + y2, this.vec3d2.z)) {
            this.vec3d2 = new Vec3(this.vec3d2.x, 0.3, this.vec3d2.z);
         }
      } else if (this.flag3) {
         Vec3 vec3d2 = this.vec3d2;
         Vec3 vec2 = this.method6082();
         float f = this.floatValue2 * (float) (Math.PI / 180.0);
         double d2 = Math.sqrt(vec2.x * vec2.x + vec2.z * vec2.z);
         double horizontalDistance = this.vec3d2.horizontalDistance();
         double square = Mth.square(Mth.cos(f));
         vec3d2 = this.vec3d2.add(0.0, d * (-1.0 + square * 0.75), 0.0);
         if (vec3d2.y < 0.0 && d2 > 0.0) {
            double y2 = vec3d2.y * -0.1 * square;
            vec3d2 = vec3d2.add(vec2.x * y2 / d2, y2, vec2.z * y2 / d2);
         }

         if (f < 0.0F && d2 > 0.0) {
            double d3 = horizontalDistance * -Mth.sin(f) * 0.04;
            vec3d2 = vec3d2.add(-vec2.x * d3 / d2, d3 * 3.2, -vec2.z * d3 / d2);
         }

         if (d2 > 0.0) {
            vec3d2 = vec3d2.add((vec2.x / d2 * horizontalDistance - vec3d2.x) * 0.1, 0.0, (vec2.z / d2 * horizontalDistance - vec3d2.z) * 0.1);
         }

         this.vec3d2 = vec3d2.multiply(0.99, 0.98, 0.99);
         this.method6041(this.vec3d2);
      } else {
         BlockPos pos = this.method6062();
         float friction = this.flag4 ? this.playerEntity.level().getBlockState(pos).getBlock().getFriction() : 1.0F;
         float f = friction * 0.91F;
         Vec3 vec2 = this.method6036(vec, friction);
         double y2 = vec2.y;
         if (this.method6084(MobEffects.LEVITATION)) {
            MobEffectInstance mobEffectInstance = this.method6085(MobEffects.LEVITATION);
            if (mobEffectInstance != null) {
               y2 += (0.05 * (mobEffectInstance.getAmplifier() + 1) - vec2.y) * 0.2;
            }
         } else if (this.playerEntity.level().isClientSide() && !this.playerEntity.level().hasChunkAt(pos)) {
            y2 = this.vec3d.y > this.playerEntity.level().getMinY() ? -0.1 : 0.0;
         } else if (d != 0.0) {
            y2 -= d;
         }

         if (this.playerEntity.shouldDiscardFriction()) {
            this.vec3d2 = new Vec3(vec2.x, y2, vec2.z);
         } else {
            this.vec3d2 = new Vec3(vec2.x * f, y2 * 0.98F, vec2.z * f);
         }
      }

      if (this.playerEntity.getAbilities().flying && !this.playerEntity.isPassenger()) {
         this.vec3d2 = new Vec3(this.vec3d2.x, y * 0.6, this.vec3d2.z);
         this.method6043();
      }
   }

   private Vec3 method6036(Vec3 vec, float friction) {
      this.method6037(this.method6038(friction), vec);
      this.vec3d2 = this.method6045(this.vec3d2);
      this.method6041(this.vec3d2);
      Vec3 vec3d2 = this.vec3d2;
      BlockPos pos = this.method6089(this.vec3d);
      BlockState blockState = this.method6090(pos);
      if ((this.flag5 || this.flag2)
         && (this.method6046() || blockState != null && blockState.is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(this.playerEntity))) {
         vec3d2 = new Vec3(vec3d2.x, 0.2, vec3d2.z);
      }

      return vec3d2;
   }

   private void method6037(float speed, Vec3 vec) {
      Vec3 inputVector = Entity.getInputVector(vec, speed, this.floatValue);
      this.vec3d2 = this.vec3d2.add(inputVector);
   }

   private float method6038(float friction) {
      return this.flag4 ? this.method6040() * (0.21600002F / (friction * friction * friction)) : this.method6039();
   }

   private float method6039() {
      return 0.02F;
   }

   private float method6040() {
      return (float)this.method6086(Attributes.MOVEMENT_SPEED);
   }

   private void method6041(Vec3 vec3d2) {
      Vec3 movement = this.method6048(vec3d2);
      Vec3 vec = this.method6042(movement);
      if (vec.lengthSqr() > 1.0E-7) {
         this.vec3d = this.vec3d.add(vec);
         this.box = this.box.move(vec);
      }

      boolean flag = !Mth.equal(movement.x, vec.x);
      boolean flag2 = !Mth.equal(movement.z, vec.z);
      this.flag5 = flag || flag2;
      this.flag6 = movement.y != vec.y;
      this.flag4 = this.flag6 && movement.y < 0.0;
      if (!this.isFlag7()) {
         this.method6065();
      }

      if (this.flag4) {
         this.method6043();
      } else if (movement.y < 0.0) {
         this.floatValue3 = this.floatValue3 - (float)movement.y;
      }

      Vec3 vec3d22 = this.vec3d2;
      if (this.flag5 || this.flag6) {
         this.vec3d2 = new Vec3(flag ? 0.0 : vec3d22.x, this.flag4 ? 0.0 : vec3d22.y, flag2 ? 0.0 : vec3d22.z);
      }

      float xScale = this.method6059();
      this.vec3d2 = this.vec3d2.multiply(xScale, 1.0, xScale);
   }

   private Vec3 method6042(Vec3 movement) {
      AABB box = this.box;
      java.util.List<net.minecraft.world.phys.shapes.VoxelShape> emptyList = Collections.emptyList();
      Vec3 vec;
      if (movement.lengthSqr() == 0.0) {
         vec = movement;
      } else {
         vec = Entity.collideBoundingBox(this.playerEntity, movement, box, this.playerEntity.level(), emptyList);
      }

      boolean flag = movement.x != vec.x;
      boolean flag2 = movement.y != vec.y;
      boolean flag3 = movement.z != vec.z;
      boolean flag4 = this.flag4 || flag2 && movement.y < 0.0;
      if (this.playerEntity.maxUpStep() > 0.0F && flag4 && (flag || flag3)) {
         Vec3 collideBoundingBox = Entity.collideBoundingBox(
            this.playerEntity, new Vec3(movement.x, this.playerEntity.maxUpStep(), movement.z), box, this.playerEntity.level(), emptyList
         );
         Vec3 collideBoundingBox2 = Entity.collideBoundingBox(
            this.playerEntity,
            new Vec3(0.0, this.playerEntity.maxUpStep(), 0.0),
            box.expandTowards(movement.x, 0.0, movement.z),
            this.playerEntity.level(),
            emptyList
         );
         Vec3 vec2 = Entity.collideBoundingBox(
               this.playerEntity, new Vec3(movement.x, 0.0, movement.z), box.move(collideBoundingBox2), this.playerEntity.level(), emptyList
            )
            .add(collideBoundingBox2);
         if (collideBoundingBox2.y < this.playerEntity.maxUpStep() && vec2.horizontalDistanceSqr() > collideBoundingBox.horizontalDistanceSqr()) {
            collideBoundingBox = vec2;
         }

         if (collideBoundingBox.horizontalDistanceSqr() > vec.horizontalDistanceSqr()) {
            return collideBoundingBox.add(
               Entity.collideBoundingBox(
                  this.playerEntity, new Vec3(0.0, -collideBoundingBox.y + movement.y, 0.0), box.move(collideBoundingBox), this.playerEntity.level(), emptyList
               )
            );
         }
      }

      return vec;
   }

   private void method6043() {
      this.floatValue3 = 0.0F;
   }

   public void method6044() {
      float f = this.method6052();
      if (!(f <= 1.0E-5F)) {
         this.vec3d2 = new Vec3(this.vec3d2.x, Math.max((double)f, this.vec3d2.y), this.vec3d2.z);
         if (this.isFlag()) {
            float f2 = (float)Math.toRadians(this.floatValue);
            this.vec3d2 = this.vec3d2.add(-Mth.sin(f2) * 0.2, 0.0, Mth.cos(f2) * 0.2);
         }
      }
   }

   private Vec3 method6045(Vec3 vec3d2) {
      if (!this.method6046()) {
         return vec3d2;
      } else {
         this.method6043();
         double x = Mth.clamp(vec3d2.x, -0.15F, 0.15F);
         double z = Mth.clamp(vec3d2.z, -0.15F, 0.15F);
         double y = Math.max(vec3d2.y, -0.15F);
         if (y < 0.0 && !this.method6090(this.method6089(this.vec3d)).is(Blocks.SCAFFOLDING) && this.method6072()) {
            y = 0.0;
         }

         return new Vec3(x, y, z);
      }
   }

   public boolean method6046() {
      BlockPos pos = this.method6089(this.vec3d);
      BlockState blockState = this.method6090(pos);
      return blockState.is(BlockTags.CLIMBABLE) ? true : blockState.getBlock() instanceof TrapDoorBlock && this.method6047(pos, blockState);
   }

   private boolean method6047(BlockPos pos, BlockState blockState) {
      if (!blockState.getValue(TrapDoorBlock.OPEN)) {
         return false;
      } else {
         BlockState blockState2 = this.playerEntity.level().getBlockState(pos.below());
         return blockState2.is(Blocks.LADDER) && blockState2.getValue(LadderBlock.FACING).equals(blockState.getValue(TrapDoorBlock.FACING));
      }
   }

   private Vec3 method6048(Vec3 vec3d2) {
      double maxUpStep = this.playerEntity.maxUpStep();
      if (vec3d2.y <= 0.0 && !this.playerEntity.getAbilities().flying && this.method6049() && this.method6050(maxUpStep)) {
         double x = vec3d2.x;
         double z = vec3d2.z;
         double d = 0.05;
         double d2 = Math.signum(x) * d;

         double d3;
         for (d3 = Math.signum(z) * d; x != 0.0 && this.method6051(x, 0.0, maxUpStep); x -= d2) {
            if (Math.abs(x) <= d) {
               x = 0.0;
               break;
            }
         }

         while (z != 0.0 && this.method6051(0.0, z, maxUpStep)) {
            if (Math.abs(z) <= d) {
               z = 0.0;
               break;
            }

            z -= d3;
         }

         while (x != 0.0 && z != 0.0 && this.method6051(x, z, maxUpStep)) {
            if (Math.abs(x) <= d) {
               x = 0.0;
            } else {
               x -= d2;
            }

            if (Math.abs(z) <= d) {
               z = 0.0;
               break;
            }

            z -= d3;
         }

         if (vec3d2.x != x || vec3d2.z != z) {
            this.flag11 = true;
         }

         vec3d2 = new Vec3(x, vec3d2.y, z);
      }

      return vec3d2;
   }

   public boolean method6049() {
      return this.entry.keyPresses.shift() || this.entry.flag;
   }

   private boolean method6050(double maxUpStep) {
      return this.flag4 || this.floatValue3 < maxUpStep && !this.method6051(0.0, 0.0, maxUpStep - this.floatValue3);
   }

   private boolean method6051(double x, double z, double maxUpStep) {
      AABB box = this.box;
      return this.playerEntity
         .level()
         .noCollision(this.playerEntity, new AABB(box.minX + x, box.minY - maxUpStep - 1.0E-5, box.minZ + z, box.maxX + x, box.minY, box.maxZ + z));
   }

   private boolean isFlag() {
      return this.flag;
   }

   private float method6052() {
      return (float)this.method6086(Attributes.JUMP_STRENGTH) * this.method6054() + this.method6053();
   }

   private float method6053() {
      if (this.method6084(MobEffects.JUMP_BOOST)) {
         MobEffectInstance mobEffectInstance = this.method6085(MobEffects.JUMP_BOOST);
         return 0.1F * (mobEffectInstance.getAmplifier() + 1);
      } else {
         return 0.0F;
      }
   }

   private float method6054() {
      float jumpFactor = 0.0F;
      Block block = this.method6090(this.method6089(this.vec3d)).getBlock();
      if (block != null) {
         jumpFactor = block.getJumpFactor();
      }

      float f = 0.0F;
      Block block2 = this.method6090(this.method6062()).getBlock();
      if (block2 != null) {
         f = block2.getJumpFactor();
      }

      return jumpFactor == 1.0F ? f : jumpFactor;
   }

   private boolean method6055(double x, double ya, double z) {
      return this.method6056(this.box.move(x, ya, z));
   }

   private boolean method6056(AABB move) {
      return this.playerEntity.level().noCollision(this.playerEntity, move) && !this.playerEntity.level().containsAnyLiquid(move);
   }

   private double method6057() {
      double d = this.playerEntity.isNoGravity() ? 0.0 : this.method6086(Attributes.GRAVITY);
      return this.vec3d2.y <= 0.0 && this.method6084(MobEffects.SLOW_FALLING) ? Math.min(d, 0.01) : d;
   }

   private Vec3 method6058(double d, boolean flag, Vec3 vec3d2) {
      if (d != 0.0 && !this.isFlag()) {
         double y = flag && Math.abs(vec3d2.y - 0.005) >= 0.003 && Math.abs(vec3d2.y - d / 16.0) < 0.003 ? -0.003 : vec3d2.y - d / 16.0;
         return new Vec3(vec3d2.x, y, vec3d2.z);
      } else {
         return vec3d2;
      }
   }

   private float method6059() {
      BlockState blockState = this.method6090(this.method6089(this.vec3d));
      float speedFactor = blockState.getBlock().getSpeedFactor();
      if (!blockState.is(Blocks.WATER) && !blockState.is(Blocks.BUBBLE_COLUMN)) {
         speedFactor = speedFactor == 1.0F ? this.method6090(this.method6062()).getBlock().getSpeedFactor() : speedFactor;
      }

      return Mth.lerp((float)this.method6086(Attributes.MOVEMENT_EFFICIENCY), speedFactor, 1.0F);
   }

   private void method6060() {
      this.vec3d2 = this.vec3d2.add(0.0, -0.04F, 0.0);
   }

   private void method6061(TagKey<Fluid> tagKey) {
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
         Boat vehicle = (Boat)this.playerEntity.getVehicle();
         if (!vehicle.isUnderWater()) {
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
         Pose pose;
         if (this.flag3) {
            pose = Pose.FALL_FLYING;
         } else if (this.playerEntity.isSleeping()) {
            pose = Pose.SLEEPING;
         } else if (this.flag8) {
            pose = Pose.SWIMMING;
         } else if (this.playerEntity.isAutoSpinAttack()) {
            pose = Pose.SPIN_ATTACK;
         } else if (this.method6072() && !this.playerEntity.getAbilities().flying) {
            pose = Pose.CROUCHING;
         } else {
            pose = Pose.STANDING;
         }

         Pose entityPose;
         if (this.playerEntity.isSpectator() || this.playerEntity.isPassenger() || this.method6073(pose)) {
            entityPose = pose;
         } else if (this.method6073(Pose.CROUCHING)) {
            entityPose = Pose.CROUCHING;
         } else {
            entityPose = Pose.SWIMMING;
         }

         this.method6074(entityPose);
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
         float f = (float)this.method6086(Attributes.SNEAKING_SPEED);
         this.entry.floatValue2 *= f;
         this.entry.floatValue *= f;
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
         boolean flag = !this.method6071() || !this.canSprint();
         boolean flag2 = flag || this.flag5 && !this.playerEntity.minorHorizontalCollision || this.isFlag7() && !this.method6078();
         if (this.flag8) {
            if (!this.flag4 && !this.entry.keyPresses.shift() && flag || !this.isFlag7()) {
               this.flag = false;
            }
         } else if (flag2) {
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
      Entity vehicle = this.playerEntity.getVehicle();
      return vehicle != null && vehicle.getType() == net.minecraft.world.entity.EntityTypes.CAMEL;
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

   private boolean method6073(Pose pose) {
      return this.playerEntity.level().noCollision(this.playerEntity, this.playerEntity.getDimensions(pose).makeBoundingBox(this.vec3d).deflate(1.0E-7));
   }

   private void method6074(Pose entityPose) {
      if (this.entityPose != entityPose || !this.box.equals(this.playerEntity.getDimensions(entityPose).makeBoundingBox(this.vec3d))) {
         this.entityPose = entityPose;
         this.box = this.playerEntity.getDimensions(entityPose).makeBoundingBox(this.vec3d);
      }
   }

   private void method6075() {
      this.flag9 = this.hashSet.contains(FluidTags.WATER);
      this.hashSet.clear();
      double y = this.method6076() - 0.11111111F;
      if (!(
         this.playerEntity.getVehicle() instanceof Boat boat
            && !boat.isUnderWater()
            && boat.getBoundingBox().maxY >= y
            && boat.getBoundingBox().minY <= y
      )) {
         BlockPos containing = BlockPos.containing(this.vec3d.x, y, this.vec3d.z);
         FluidState fluidState = this.playerEntity.level().getFluidState(containing);
         double d = containing.getY() + fluidState.getHeight(this.playerEntity.level(), containing);
         if (d > y) {
            this.hashSet.addAll(fluidState.typeHolder().tags().toList());
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

   private double method6079(TagKey<Fluid> tagKey) {
      return this.object2DoubleMap.getDouble(tagKey);
   }

   private boolean method6080(TagKey<Fluid> tag, double scale) {
      if (this.method6081()) {
         return false;
      } else {
         AABB deflate = this.box.deflate(0.001);
         int n = Mth.floor(deflate.minX);
         int n2 = Mth.ceil(deflate.maxX);
         int n3 = Mth.floor(deflate.minY);
         int n4 = Mth.ceil(deflate.maxY);
         int n5 = Mth.floor(deflate.minZ);
         int n6 = Mth.ceil(deflate.maxZ);
         double d = 0.0;
         boolean flag = true;
         boolean flag2 = false;
         Vec3 vec = Vec3.ZERO;
         int n7 = 0;
         MutableBlockPos mutablePos = new MutableBlockPos();

         for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
               for (int k = n5; k < n6; k++) {
                  mutablePos.set(i, j, k);
                  FluidState fluidState = this.playerEntity.level().getFluidState(mutablePos);
                  if (fluidState.is(tag)) {
                     double d2 = j + fluidState.getHeight(this.playerEntity.level(), mutablePos);
                     if (d2 >= deflate.minY) {
                        flag2 = true;
                        d = Math.max(d2 - deflate.minY, d);
                        if (flag) {
                           Vec3 flow = fluidState.getFlow(this.playerEntity.level(), mutablePos);
                           if (d < 0.4) {
                              flow = flow.scale(d);
                           }

                           vec = vec.add(flow);
                           n7++;
                        }
                     }
                  }
               }
            }
         }

         if (vec.length() > 0.0) {
            if (n7 > 0) {
               vec = vec.scale(1.0 / n7);
            }

            vec = vec.scale(scale);
            if (Math.abs(this.vec3d2.x) < 0.003 && Math.abs(this.vec3d2.z) < 0.003 && vec.length() < 0.0045) {
               vec = vec.normalize().scale(0.0045);
            }

            this.vec3d2 = this.vec3d2.add(vec);
         }

         this.object2DoubleMap.put(tag, d);
         return flag2;
      }
   }

   private boolean method6081() {
      AABB inflate = this.box.inflate(1.0);
      int x0 = Mth.floor(inflate.minX);
      int x1 = Mth.ceil(inflate.maxX);
      int z0 = Mth.floor(inflate.minZ);
      int z1 = Mth.ceil(inflate.maxZ);
      return !this.playerEntity.level().hasChunksAt(x0, z0, x1, z1);
   }

   private Vec3 method6082() {
      return this.method6083(this.floatValue2, this.floatValue);
   }

   private Vec3 method6083(float f, float floatValue) {
      float f2 = (float)(f * Math.PI / 180.0);
      float f3 = (float)(-floatValue * Math.PI / 180.0);
      float f4 = Mth.cos(f3);
      float f5 = Mth.sin(f3);
      float f6 = Mth.cos(f2);
      float f7 = Mth.sin(f2);
      return new Vec3(f5 * f6, -f7, f4 * f6);
   }

   public boolean method6084(Holder<MobEffect> effect) {
      MobEffectInstance mobEffectInstance = this.playerEntity.getEffect(effect);
      return mobEffectInstance != null && mobEffectInstance.getDuration() >= this.intValue2;
   }

   private MobEffectInstance method6085(Holder<MobEffect> effect) {
      MobEffectInstance mobEffectInstance = this.playerEntity.getEffect(effect);
      return mobEffectInstance != null && mobEffectInstance.getDuration() >= this.intValue2 ? mobEffectInstance : null;
   }

   public double method6086(Holder<Attribute> attribute) {
      return this.playerEntity.getAttributes().getValue(attribute);
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

   public BlockPos method6089(Vec3 vec3d) {
      return new BlockPos(Mth.floor(vec3d.x), Mth.floor(vec3d.y), Mth.floor(vec3d.z));
   }

   public BlockState method6090(BlockPos pos) {
      return this.playerEntity.level().getBlockState(pos);
   }

   public static class Entry extends ClientInput {
      public boolean flag = false;
      public float floatValue;
      public float floatValue2;
      public Input keyPresses;
      public static final double CONST_doubleValue = 0.121;

      public Entry(Input keyPresses) {
         this.keyPresses = keyPresses;
      }

      private PlayerSimulation.Entry method6091() {
         PlayerSimulation.Entry entry = new PlayerSimulation.Entry(this.keyPresses);
         entry.flag = this.flag;
         entry.floatValue = this.floatValue;
         entry.floatValue2 = this.floatValue2;
         return entry;
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

      public static PlayerSimulation.Entry method6092(Input keyPresses) {
         return new PlayerSimulation.Entry(keyPresses);
      }

      public static PlayerSimulation.Entry method6093(Player player) {
         Vec3 vec = player.position().subtract(new Vec3(player.xo, player.yo, player.zo));
         double horizontalDistanceSqr = vec.horizontalDistanceSqr();
         Input input = new Input(false, false, false, false, !player.onGround(), player.isShiftKeyDown(), horizontalDistanceSqr >= 0.014641);
         if (horizontalDistanceSqr > 0.0025000000000000005) {
            double angle = MovementInputUtils.relativeMovementAngle(vec, player.getYRot());
            double d = Mth.wrapDegrees(angle);
            input = MovementInputUtils.inputForDirection(input, d);
         }

         return new PlayerSimulation.Entry(input);
      }
   }
}

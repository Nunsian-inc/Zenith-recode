package org.zenith.base.bot.world;

import java.util.Objects;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.utility.misc.ViaCompat;

public class BotPlayer extends Player {
   public final BotPlayHandler networkHandler;
   public double lastXClient;
   public double lastBaseY;
   public double lastZClient;
   public float lastYawClient;
   public float lastPitchClient;
   public boolean lastOnGround;
   private boolean lastHorizontalCollision;
   private boolean inSneakingPose;
   public boolean lastSneaking;
   public boolean lastSprinting;
   private int ticksSinceLastPositionPacketSent;
   private boolean healthInitialized;
   public ClientInput input = new ClientInput();
   public Input lastPlayerInput = Input.EMPTY;
   private int clientPermissionLevel = 0;
   private boolean usingItem;
   @Nullable
   private InteractionHand activeHand;
   private boolean riding;
   private boolean falling;

   public BotPlayer(BotWorld var1, BotPlayHandler var2, boolean var3, boolean var4) {
      super(var1, var2.getProfile());
      this.networkHandler = var2;
      this.lastSneaking = var3;
      this.lastSprinting = var4;
   }

   public BotWorld getBotWorld() {
      return (BotWorld)this.level();
   }

   @Override
   public void heal(float amount) {
   }

   @Override
   public void removeVehicle() {
      super.removeVehicle();
      this.riding = false;
   }

   @Override
   public float getViewXRot(float tickProgress) {
      return this.getXRot();
   }

   @Override
   public float getViewYRot(float tickProgress) {
      return this.isPassenger() ? super.getViewYRot(tickProgress) : this.getYRot();
   }

   @Override
   public void tick() {
      if (this.networkHandler.isPlayerLoaded()) {
         super.tick();
         this.sendSneakingPacket();
         if (!this.lastPlayerInput.equals(this.input.keyPresses)) {
            this.networkHandler.sendPacket(new ServerboundPlayerInputPacket(this.input.keyPresses));
            this.lastPlayerInput = this.input.keyPresses;
         }

         if (this.isPassenger()) {
            this.networkHandler.sendPacket(new Rot(this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
            Entity var1 = this.getRootVehicle();
            if (var1 != this && var1.isLocalInstanceAuthoritative()) {
               this.networkHandler.sendPacket(ServerboundMoveVehiclePacket.fromEntity(var1));
               this.sendSprintingPacket();
            }
         } else {
            this.sendMovementPackets();
         }
      }
   }

   private void sendMovementPackets() {
      this.sendSprintingPacket();
      double var1 = this.getX() - this.lastXClient;
      double var3 = this.getY() - this.lastBaseY;
      double var5 = this.getZ() - this.lastZClient;
      double var7 = this.getYRot() - this.lastYawClient;
      double var9 = this.getXRot() - this.lastPitchClient;
      this.ticksSinceLastPositionPacketSent++;
      boolean var11 = Mth.lengthSquared(var1, var3, var5) > Mth.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
      boolean var12 = var7 != 0.0 || var9 != 0.0;
      if (var11 && var12) {
         this.networkHandler
            .sendPacket(new PosRot(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
      } else if (var11) {
         this.networkHandler.sendPacket(new Pos(this.getX(), this.getY(), this.getZ(), this.onGround(), this.horizontalCollision));
      } else if (var12) {
         this.networkHandler.sendPacket(new Rot(this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
      } else if (this.lastOnGround != this.onGround() || this.lastHorizontalCollision != this.horizontalCollision) {
         this.networkHandler.sendPacket(new StatusOnly(this.onGround(), this.horizontalCollision));
      }

      if (var11) {
         this.lastXClient = this.getX();
         this.lastBaseY = this.getY();
         this.lastZClient = this.getZ();
         this.ticksSinceLastPositionPacketSent = 0;
      }

      if (var12) {
         this.lastYawClient = this.getYRot();
         this.lastPitchClient = this.getXRot();
      }

      this.lastOnGround = this.onGround();
      this.lastHorizontalCollision = this.horizontalCollision;
   }

   public final void sendSneakingPacket() {
      boolean var1 = this.isShiftKeyDown();
      if (var1 != this.lastSneaking) {
         this.lastSneaking = var1;
      }
   }

   public final void sendSprintingPacket() {
      boolean var1 = this.isSprinting();
      if (var1 != this.lastSprinting) {
         Action var2 = var1 ? Action.START_SPRINTING : Action.STOP_SPRINTING;
         this.networkHandler.sendPacket(new ServerboundPlayerCommandPacket(this, var2));
         this.lastSprinting = var1;
      }
   }

   public boolean dropSelectedItem(boolean var1) {
      net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action var2 = var1 ? net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.DROP_ITEM;
      ItemStack var3 = this.getInventory().removeFromSelected(var1);
      this.networkHandler.sendPacket(new ServerboundPlayerActionPacket(var2, BlockPos.ZERO, Direction.DOWN));
      return !var3.isEmpty();
   }

   // 26.3: no longer an override (vanilla swing takes an animation); keeps the bot's old swing(hand) contract
   public void swing(InteractionHand hand) {
      this.swing(hand, this.getItemInHand(hand).getAttackAnimation(), false);
      if (hand == InteractionHand.MAIN_HAND) {
         this.networkHandler.sendPacket(ServerboundPunchPacket.INSTANCE);
      }
   }

   public void requestRespawn() {
      this.networkHandler.sendPacket(new ServerboundClientCommandPacket(net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
   }

   @Override
   public void closeContainer() {
      this.networkHandler.sendPacket(new ServerboundContainerClosePacket(this.containerMenu.containerId));
      this.closeScreen();
   }

   public void closeScreen() {
      super.closeContainer();
   }

   public void updateHealth(float var1) {
      if (this.healthInitialized) {
         float var2 = this.getHealth() - var1;
         if (var2 <= 0.0F) {
            this.setHealth(var1);
            if (var2 < 0.0F) {
               this.setInvulnerableTime(10);
            }
         } else {
            this.lastHurt = var2;
            this.setInvulnerableTime(20);
            this.setHealth(var1);
            this.hurtDuration = 10;
            this.hurtTime = this.hurtDuration;
         }
      } else {
         this.setHealth(var1);
         this.healthInitialized = true;
      }
   }

   @Override
   public void onUpdateAbilities() {
      this.networkHandler.sendPacket(new ServerboundPlayerAbilitiesPacket(this.getAbilities()));
   }

   @Override
   public boolean isLocalPlayer() {
      return true;
   }

   @Override
   public boolean isSuppressingSlidingDownLadder() {
      return !this.getAbilities().flying && super.isSuppressingSlidingDownLadder();
   }

   @Override
   public boolean canSpawnSprintParticle() {
      return false;
   }

   public int getPermissionLevel() {
      return this.clientPermissionLevel;
   }

   public void setClientPermissionLevel(int var1) {
      this.clientPermissionLevel = var1;
   }

   public void displayClientMessage(Component message, boolean overlay) {
      this.networkHandler.getClient().onChat(message);
   }

   // 26.3 split displayClientMessage into these two
   @Override
   public void sendSystemMessage(Component message) {
      this.displayClientMessage(message, false);
   }

   @Override
   public void sendOverlayMessage(Component message) {
      this.displayClientMessage(message, true);
   }

   private void pushOutOfBlocks(double var1, double var3) {
      BlockPos var5 = BlockPos.containing(var1, this.getY(), var3);
      if (this.wouldCollideAt(var5)) {
         double var6 = var1 - var5.getX();
         double var8 = var3 - var5.getZ();
         Direction var10 = null;
         double var11 = Double.MAX_VALUE;
         Direction[] var13 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

         for (Direction var17 : var13) {
            double var18 = var17.getAxis().choose(var6, 0.0, var8);
            double var20 = var17.getAxisDirection() == AxisDirection.POSITIVE ? 1.0 - var18 : var18;
            if (var20 < var11 && !this.wouldCollideAt(var5.relative(var17))) {
               var11 = var20;
               var10 = var17;
            }
         }

         if (var10 != null) {
            Vec3 var22 = this.getDeltaMovement();
            if (var10.getAxis() == Axis.X) {
               this.setDeltaMovement(0.1 * var10.getStepX(), var22.y, var22.z);
            } else {
               this.setDeltaMovement(var22.x, var22.y, 0.1 * var10.getStepZ());
            }
         }
      }
   }

   private boolean wouldCollideAt(BlockPos var1) {
      AABB var2 = this.getBoundingBox();
      AABB var3 = new AABB(var1.getX(), var2.minY, var1.getZ(), var1.getX() + 1.0, var2.maxY, var1.getZ() + 1.0).deflate(1.0E-7);
      return this.level().collidesWithSuffocatingBlock(this, var3);
   }

   public void setExperience(float var1, int var2, int var3) {
      this.experienceProgress = var1;
      this.totalExperience = var2;
      this.experienceLevel = var3;
   }

   @Override
   public void handleEntityEvent(byte status) {
      if (status >= 24 && status <= 28) {
         this.setClientPermissionLevel(status - 24);
      } else {
         super.handleEntityEvent(status);
      }
   }

   @Override
   public void playSound(SoundEvent sound, float volume, float pitch) {
   }

   public void playSoundToPlayer(SoundEvent var1, SoundSource var2, float var3, float var4) {
   }

   @Override
   public void startUsingItem(InteractionHand hand) {
      ItemStack var2 = this.getItemInHand(hand);
      if (!var2.isEmpty() && !this.isUsingItem()) {
         super.startUsingItem(hand);
         this.usingItem = true;
         this.activeHand = hand;
      }
   }

   @Override
   public boolean isUsingItem() {
      return this.usingItem;
   }

   @Override
   public void stopUsingItem() {
      super.stopUsingItem();
      this.usingItem = false;
   }

   @Override
   public InteractionHand getUsedItemHand() {
      return Objects.requireNonNullElse(this.activeHand, InteractionHand.MAIN_HAND);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
      super.onSyncedDataUpdated(data);
      if (DATA_LIVING_ENTITY_FLAGS.equals(data)) {
         boolean var2 = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
         InteractionHand var3 = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         if (var2 && !this.usingItem) {
            this.startUsingItem(var3);
         } else if (!var2 && this.usingItem) {
            this.stopUsingItem();
         }
      }
   }

   @Nullable
   public PlayerRideableJumping getJumpingMount() {
      return this.getControlledVehicle() instanceof PlayerRideableJumping var1 && var1.canJump() ? var1 : null;
   }

   @Override
   public boolean isShiftKeyDown() {
      return this.input.keyPresses.shift();
   }

   @Override
   public boolean isCrouching() {
      return this.inSneakingPose;
   }

   public boolean shouldSlowDown() {
      return this.isCrouching() || this.isVisuallyCrawling();
   }

   @Override
   public void serverAiStep() {
      super.serverAiStep();
      this.xxa = ViaCompat.method4847(this.input);
      this.zza = ViaCompat.method4846(this.input);
      this.jumping = this.input.keyPresses.jump();
   }

   public void init() {
      this.setPose(Pose.STANDING);
      if (this.level() != null) {
         for (double var1 = this.getY(); var1 > this.level().getMinY() && var1 <= this.level().getMaxY(); var1++) {
            this.setPos(this.getX(), var1, this.getZ());
            if (this.level().noCollision(this)) {
               break;
            }
         }

         this.setDeltaMovement(Vec3.ZERO);
         this.setXRot(0.0F);
      }

      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   @Override
   public void aiStep() {
      this.processPortalCooldown();
      boolean var1 = this.input.keyPresses.jump();
      boolean var2 = this.input.keyPresses.shift();
      boolean var3 = this.isWalking();
      Abilities var4 = this.getAbilities();
      this.inSneakingPose = !var4.flying
         && !this.isSwimming()
         && !this.isPassenger()
         && this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)
         && (this.isShiftKeyDown() || !this.isSleeping() && !this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING));
      this.input.tick();
      if (this.shouldStopSprinting()) {
         this.setSprinting(false);
      }

      if (this.isUsingItem() && !this.isPassenger()) {
         ViaCompat.method4850(this.input, ViaCompat.method4846(this.input) * 0.2F, ViaCompat.method4847(this.input) * 0.2F);
      }

      if (this.shouldSlowDown()) {
         float var5 = (float)this.getAttributeValue(Attributes.SNEAKING_SPEED);
         ViaCompat.method4850(this.input, ViaCompat.method4846(this.input) * var5, ViaCompat.method4847(this.input) * var5);
      }

      if (!this.noPhysics) {
         this.pushOutOfBlocks(this.getX() - this.getBbWidth() * 0.35, this.getZ() + this.getBbWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() - this.getBbWidth() * 0.35, this.getZ() - this.getBbWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getBbWidth() * 0.35, this.getZ() - this.getBbWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getBbWidth() * 0.35, this.getZ() + this.getBbWidth() * 0.35);
      }

      boolean var10 = this.canStartSprinting();
      boolean var6 = this.isPassenger() ? this.getVehicle().onGround() : this.onGround();
      boolean var7 = !var2 && !var3;
      if ((var6 || this.isUnderWater()) && var7 && var10 && this.input.keyPresses.sprint()) {
         this.setSprinting(true);
      }

      if ((!this.isInWater() || this.isUnderWater()) && var10 && this.input.keyPresses.sprint()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean var8 = !this.input.hasForwardImpulse() || !this.canSprint();
         boolean var9 = var8 || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater();
         if (this.isSwimming()) {
            if (!this.onGround() && !this.input.keyPresses.shift() && var8 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (var9) {
            this.setSprinting(false);
         }
      }

      boolean var11 = false;
      if (var4.mayfly) {
         if (this.networkHandler.getInteractionManager().isFlyingLocked()) {
            if (!var4.flying) {
               var4.flying = true;
               var11 = true;
               this.onUpdateAbilities();
            }
         } else if (!var1 && this.input.keyPresses.jump()) {
            if (this.jumpTriggerTime == 0) {
               this.jumpTriggerTime = 7;
            } else if (!this.isSwimming()) {
               var4.flying = !var4.flying;
               if (var4.flying && this.onGround()) {
                  this.jumpFromGround();
               }

               var11 = true;
               this.onUpdateAbilities();
               this.jumpTriggerTime = 0;
            }
         }
      }

      if (this.input.keyPresses.jump() && !var11 && !var1 && !this.onClimbable() && this.tryToStartFallFlying()) {
         this.networkHandler.sendPacket(new ServerboundPlayerCommandPacket(this, Action.START_FALL_FLYING));
      }

      this.falling = this.isFallFlying();
      if (this.isInWater() && this.input.keyPresses.shift() && this.isAffectedByFluids()) {
         this.goDownInWater();
      }

      if (var4.flying) {
         int var12 = 0;
         if (this.input.keyPresses.shift()) {
            var12--;
         }

         if (this.input.keyPresses.jump()) {
            var12++;
         }

         if (var12 != 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, var12 * var4.getFlyingSpeed() * 3.0F, 0.0));
         }
      }

      super.aiStep();
      if (this.onGround() && var4.flying && !this.networkHandler.getInteractionManager().isFlyingLocked()) {
         var4.flying = false;
         this.onUpdateAbilities();
      }
   }

   private boolean shouldStopSprinting() {
      return this.isFallFlying()
         || this.isBlind()
         || this.shouldSlowDown()
         || this.isPassenger() && !this.isRidingCamel()
         || this.isUsingItem() && !this.isPassenger() && !this.isUnderWater();
   }

   private boolean isRidingCamel() {
      return this.getVehicle() != null && this.getVehicle().getType() == net.minecraft.world.entity.EntityTypes.CAMEL;
   }

   public final boolean isBlind() {
      return this.hasEffect(MobEffects.BLINDNESS);
   }

   @Override
   public void tickDeath() {
      this.deathTime++;
      if (this.deathTime == 20) {
         this.remove(RemovalReason.KILLED);
      }
   }

   @Override
   public void rideTick() {
      super.rideTick();
      this.riding = false;
      if (this.getControlledVehicle() instanceof AbstractBoat var1) {
         var1.setInput(this.input.keyPresses.left(), this.input.keyPresses.right(), this.input.keyPresses.forward(), this.input.keyPresses.backward());
         this.riding = this.riding
            | (this.input.keyPresses.left() || this.input.keyPresses.right() || this.input.keyPresses.forward() || this.input.keyPresses.backward());
      }
   }

   public boolean isRiding() {
      return this.riding;
   }

   @Override
   public boolean isHorizontalCollisionMinor(Vec3 adjustedMovement) {
      float var2 = this.getYRot() * (float) (Math.PI / 180.0);
      double var3 = Mth.sin(var2);
      double var5 = Mth.cos(var2);
      double var7 = this.xxa * var5 - this.zza * var3;
      double var9 = this.zza * var5 + this.xxa * var3;
      double var11 = Mth.square(var7) + Mth.square(var9);
      double var13 = Mth.square(adjustedMovement.x) + Mth.square(adjustedMovement.z);
      if (!(var11 < 1.0E-5F) && !(var13 < 1.0E-5F)) {
         double var15 = var7 * adjustedMovement.x + var9 * adjustedMovement.z;
         double var17 = Math.acos(var15 / Math.sqrt(var11 * var13));
         return var17 < 0.13962634F;
      } else {
         return false;
      }
   }

   private boolean canStartSprinting() {
      return !this.isSprinting()
         && this.isWalking()
         && this.canSprint()
         && !this.isUsingItem()
         && !this.isBlind()
         && (!this.isPassenger() || this.canVehicleSprint(this.getVehicle()))
         && !this.isFallFlying()
         && (!this.shouldSlowDown() || this.isUnderWater());
   }

   private boolean canVehicleSprint(Entity var1) {
      return var1.canSprint() && var1.isLocalInstanceAuthoritative();
   }

   public final boolean isWalking() {
      return this.isUnderWater() ? this.input.hasForwardImpulse() : ViaCompat.method4846(this.input) >= 0.8;
   }

   public final boolean canSprint() {
      return this.isPassenger() || this.getFoodData().getFoodLevel() > 6.0F || this.getAbilities().mayfly;
   }

   public void onGameModeChanged(GameType var1) {
      if (var1 == GameType.SPECTATOR) {
         this.setDeltaMovement(this.getDeltaMovement().with(Axis.Y, 0.0));
      }
   }

   @Override
   public boolean isUnderWater() {
      return this.wasUnderwater;
   }

   @Override
   public boolean updateIsUnderwater() {
      super.updateIsUnderwater();
      return this.wasUnderwater;
   }

   @Override
   public float getVisualRotationYInDegrees() {
      return this.getYRot();
   }

   @Override
   public boolean isSpectator() {
      BotInteractionManager var1 = this.networkHandler.getInteractionManagerOrNull();
      return var1 != null && var1.getCurrentGameMode() == GameType.SPECTATOR;
   }

   @Override
   public boolean isCreative() {
      BotInteractionManager var1 = this.networkHandler.getInteractionManagerOrNull();
      return var1 != null && var1.getCurrentGameMode() == GameType.CREATIVE;
   }

   public boolean isSubmergedInWaterFlag() {
      return this.isEyeInFluid(FluidTags.WATER);
   }

   @Override
   public GameType gameMode() {
      PlayerInfo var1 = this.networkHandler.getPlayerListEntry(this.getUUID());
      return var1 != null ? var1.getGameMode() : GameType.SURVIVAL;
   }
}

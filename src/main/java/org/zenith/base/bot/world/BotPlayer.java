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

   public BotPlayer(BotWorld botWorld, BotPlayHandler networkHandler, boolean lastSneaking, boolean lastSprinting) {
      super(botWorld, networkHandler.getProfile());
      this.networkHandler = networkHandler;
      this.lastSneaking = lastSneaking;
      this.lastSprinting = lastSprinting;
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
            Entity rootVehicle = this.getRootVehicle();
            if (rootVehicle != this && rootVehicle.isLocalInstanceAuthoritative()) {
               this.networkHandler.sendPacket(ServerboundMoveVehiclePacket.fromEntity(rootVehicle));
               this.sendSprintingPacket();
            }
         } else {
            this.sendMovementPackets();
         }
      }
   }

   private void sendMovementPackets() {
      this.sendSprintingPacket();
      double x = this.getX() - this.lastXClient;
      double y = this.getY() - this.lastBaseY;
      double z = this.getZ() - this.lastZClient;
      double d = this.getYRot() - this.lastYawClient;
      double d2 = this.getXRot() - this.lastPitchClient;
      this.ticksSinceLastPositionPacketSent++;
      boolean flag = Mth.lengthSquared(x, y, z) > Mth.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
      boolean flag2 = d != 0.0 || d2 != 0.0;
      if (flag && flag2) {
         this.networkHandler
            .sendPacket(new PosRot(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
      } else if (flag) {
         this.networkHandler.sendPacket(new Pos(this.getX(), this.getY(), this.getZ(), this.onGround(), this.horizontalCollision));
      } else if (flag2) {
         this.networkHandler.sendPacket(new Rot(this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
      } else if (this.lastOnGround != this.onGround() || this.lastHorizontalCollision != this.horizontalCollision) {
         this.networkHandler.sendPacket(new StatusOnly(this.onGround(), this.horizontalCollision));
      }

      if (flag) {
         this.lastXClient = this.getX();
         this.lastBaseY = this.getY();
         this.lastZClient = this.getZ();
         this.ticksSinceLastPositionPacketSent = 0;
      }

      if (flag2) {
         this.lastYawClient = this.getYRot();
         this.lastPitchClient = this.getXRot();
      }

      this.lastOnGround = this.onGround();
      this.lastHorizontalCollision = this.horizontalCollision;
   }

   public final void sendSneakingPacket() {
      boolean shiftKeyDown = this.isShiftKeyDown();
      if (shiftKeyDown != this.lastSneaking) {
         this.lastSneaking = shiftKeyDown;
      }
   }

   public final void sendSprintingPacket() {
      boolean sprinting = this.isSprinting();
      if (sprinting != this.lastSprinting) {
         Action action = sprinting ? Action.START_SPRINTING : Action.STOP_SPRINTING;
         this.networkHandler.sendPacket(new ServerboundPlayerCommandPacket(this, action));
         this.lastSprinting = sprinting;
      }
   }

   public boolean dropSelectedItem(boolean all) {
      net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action action = all ? net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.DROP_ITEM;
      ItemStack removeFromSelected = this.getInventory().removeFromSelected(all);
      this.networkHandler.sendPacket(new ServerboundPlayerActionPacket(action, BlockPos.ZERO, Direction.DOWN));
      return !removeFromSelected.isEmpty();
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

   public void updateHealth(float health) {
      if (this.healthInitialized) {
         float lastHurt = this.getHealth() - health;
         if (lastHurt <= 0.0F) {
            this.setHealth(health);
            if (lastHurt < 0.0F) {
               this.setInvulnerableTime(10);
            }
         } else {
            this.lastHurt = lastHurt;
            this.setInvulnerableTime(20);
            this.setHealth(health);
            this.hurtDuration = 10;
            this.hurtTime = this.hurtDuration;
         }
      } else {
         this.setHealth(health);
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

   public void setClientPermissionLevel(int clientPermissionLevel) {
      this.clientPermissionLevel = clientPermissionLevel;
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

   private void pushOutOfBlocks(double x, double z) {
      BlockPos containing = BlockPos.containing(x, this.getY(), z);
      if (this.wouldCollideAt(containing)) {
         double d = x - containing.getX();
         double z2 = z - containing.getZ();
         Direction direction = null;
         double d2 = Double.MAX_VALUE;
         Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

         for (Direction direction2 : directions) {
            double choose = direction2.getAxis().choose(d, 0.0, z2);
            double d3 = direction2.getAxisDirection() == AxisDirection.POSITIVE ? 1.0 - choose : choose;
            if (d3 < d2 && !this.wouldCollideAt(containing.relative(direction2))) {
               d2 = d3;
               direction = direction2;
            }
         }

         if (direction != null) {
            Vec3 deltaMovement = this.getDeltaMovement();
            if (direction.getAxis() == Axis.X) {
               this.setDeltaMovement(0.1 * direction.getStepX(), deltaMovement.y, deltaMovement.z);
            } else {
               this.setDeltaMovement(deltaMovement.x, deltaMovement.y, 0.1 * direction.getStepZ());
            }
         }
      }
   }

   private boolean wouldCollideAt(BlockPos containing) {
      AABB boundingBox = this.getBoundingBox();
      AABB deflate = new AABB(containing.getX(), boundingBox.minY, containing.getZ(), containing.getX() + 1.0, boundingBox.maxY, containing.getZ() + 1.0).deflate(1.0E-7);
      return this.level().collidesWithSuffocatingBlock(this, deflate);
   }

   public void setExperience(float experienceProgress, int totalExperience, int experienceLevel) {
      this.experienceProgress = experienceProgress;
      this.totalExperience = totalExperience;
      this.experienceLevel = experienceLevel;
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

   public void playSoundToPlayer(SoundEvent event, SoundSource soundSource, float f, float f2) {
   }

   @Override
   public void startUsingItem(InteractionHand hand) {
      ItemStack itemInHand = this.getItemInHand(hand);
      if (!itemInHand.isEmpty() && !this.isUsingItem()) {
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
         boolean flag = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
         InteractionHand hand = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         if (flag && !this.usingItem) {
            this.startUsingItem(hand);
         } else if (!flag && this.usingItem) {
            this.stopUsingItem();
         }
      }
   }

   @Nullable
   public PlayerRideableJumping getJumpingMount() {
      return this.getControlledVehicle() instanceof PlayerRideableJumping playerRideableJumping && playerRideableJumping.canJump() ? playerRideableJumping : null;
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
         for (double y = this.getY(); y > this.level().getMinY() && y <= this.level().getMaxY(); y++) {
            this.setPos(this.getX(), y, this.getZ());
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
      boolean jump = this.input.keyPresses.jump();
      boolean shift = this.input.keyPresses.shift();
      boolean walking = this.isWalking();
      Abilities abilities = this.getAbilities();
      this.inSneakingPose = !abilities.flying
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
         float attributeValue = (float)this.getAttributeValue(Attributes.SNEAKING_SPEED);
         ViaCompat.method4850(this.input, ViaCompat.method4846(this.input) * attributeValue, ViaCompat.method4847(this.input) * attributeValue);
      }

      if (!this.noPhysics) {
         this.pushOutOfBlocks(this.getX() - this.getBbWidth() * 0.35, this.getZ() + this.getBbWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() - this.getBbWidth() * 0.35, this.getZ() - this.getBbWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getBbWidth() * 0.35, this.getZ() - this.getBbWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getBbWidth() * 0.35, this.getZ() + this.getBbWidth() * 0.35);
      }

      boolean canStartSprinting = this.canStartSprinting();
      boolean flag = this.isPassenger() ? this.getVehicle().onGround() : this.onGround();
      boolean flag2 = !shift && !walking;
      if ((flag || this.isUnderWater()) && flag2 && canStartSprinting && this.input.keyPresses.sprint()) {
         this.setSprinting(true);
      }

      if ((!this.isInWater() || this.isUnderWater()) && canStartSprinting && this.input.keyPresses.sprint()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean flag3 = !this.input.hasForwardImpulse() || !this.canSprint();
         boolean flag4 = flag3 || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater();
         if (this.isSwimming()) {
            if (!this.onGround() && !this.input.keyPresses.shift() && flag3 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (flag4) {
            this.setSprinting(false);
         }
      }

      boolean flag3 = false;
      if (abilities.mayfly) {
         if (this.networkHandler.getInteractionManager().isFlyingLocked()) {
            if (!abilities.flying) {
               abilities.flying = true;
               flag3 = true;
               this.onUpdateAbilities();
            }
         } else if (!jump && this.input.keyPresses.jump()) {
            if (this.jumpTriggerTime == 0) {
               this.jumpTriggerTime = 7;
            } else if (!this.isSwimming()) {
               abilities.flying = !abilities.flying;
               if (abilities.flying && this.onGround()) {
                  this.jumpFromGround();
               }

               flag3 = true;
               this.onUpdateAbilities();
               this.jumpTriggerTime = 0;
            }
         }
      }

      if (this.input.keyPresses.jump() && !flag3 && !jump && !this.onClimbable() && this.tryToStartFallFlying()) {
         this.networkHandler.sendPacket(new ServerboundPlayerCommandPacket(this, Action.START_FALL_FLYING));
      }

      this.falling = this.isFallFlying();
      if (this.isInWater() && this.input.keyPresses.shift() && this.isAffectedByFluids()) {
         this.goDownInWater();
      }

      if (abilities.flying) {
         int n = 0;
         if (this.input.keyPresses.shift()) {
            n--;
         }

         if (this.input.keyPresses.jump()) {
            n++;
         }

         if (n != 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, n * abilities.getFlyingSpeed() * 3.0F, 0.0));
         }
      }

      super.aiStep();
      if (this.onGround() && abilities.flying && !this.networkHandler.getInteractionManager().isFlyingLocked()) {
         abilities.flying = false;
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
      if (this.getControlledVehicle() instanceof AbstractBoat abstractBoat) {
         abstractBoat.setInput(this.input.keyPresses.left(), this.input.keyPresses.right(), this.input.keyPresses.forward(), this.input.keyPresses.backward());
         this.riding = this.riding
            | (this.input.keyPresses.left() || this.input.keyPresses.right() || this.input.keyPresses.forward() || this.input.keyPresses.backward());
      }
   }

   public boolean isRiding() {
      return this.riding;
   }

   @Override
   public boolean isHorizontalCollisionMinor(Vec3 adjustedMovement) {
      float f = this.getYRot() * (float) (Math.PI / 180.0);
      double d = Mth.sin(f);
      double d2 = Mth.cos(f);
      double x = this.xxa * d2 - this.zza * d;
      double x2 = this.zza * d2 + this.xxa * d;
      double d3 = Mth.square(x) + Mth.square(x2);
      double d4 = Mth.square(adjustedMovement.x) + Mth.square(adjustedMovement.z);
      if (!(d3 < 1.0E-5F) && !(d4 < 1.0E-5F)) {
         double d5 = x * adjustedMovement.x + x2 * adjustedMovement.z;
         double d6 = Math.acos(d5 / Math.sqrt(d3 * d4));
         return d6 < 0.13962634F;
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

   private boolean canVehicleSprint(Entity vehicle) {
      return vehicle.canSprint() && vehicle.isLocalInstanceAuthoritative();
   }

   public final boolean isWalking() {
      return this.isUnderWater() ? this.input.hasForwardImpulse() : ViaCompat.method4846(this.input) >= 0.8;
   }

   public final boolean canSprint() {
      return this.isPassenger() || this.getFoodData().getFoodLevel() > 6.0F || this.getAbilities().mayfly;
   }

   public void onGameModeChanged(GameType gameMode) {
      if (gameMode == GameType.SPECTATOR) {
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
      BotInteractionManager interactionManagerOrNull = this.networkHandler.getInteractionManagerOrNull();
      return interactionManagerOrNull != null && interactionManagerOrNull.getCurrentGameMode() == GameType.SPECTATOR;
   }

   @Override
   public boolean isCreative() {
      BotInteractionManager interactionManagerOrNull = this.networkHandler.getInteractionManagerOrNull();
      return interactionManagerOrNull != null && interactionManagerOrNull.getCurrentGameMode() == GameType.CREATIVE;
   }

   public boolean isSubmergedInWaterFlag() {
      return this.isEyeInFluid(FluidTags.WATER);
   }

   @Override
   public GameType gameMode() {
      PlayerInfo playerListEntry = this.networkHandler.getPlayerListEntry(this.getUUID());
      return playerListEntry != null ? playerListEntry.getGameMode() : GameType.SURVIVAL;
   }
}

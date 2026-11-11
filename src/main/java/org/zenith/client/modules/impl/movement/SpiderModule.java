package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.stream.Stream;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "Spider",
   category = ModuleCategory.MOVEMENT,
   description = "module.spider.description"
)
public final class SpiderModule extends Module {
   public static final SpiderModule INSTANCE = new SpiderModule();
   private static final double WATER_CLIMB_SPEED = 0.3F;
   private static final int WATER_BUCKET_CONTAINER_SLOT = 44;
   private final ModeSetting mode = new ModeSetting("module.spider.mode", "module.spider.mode.desc");
   private final ModeSetting.Entry blocksMode = new ModeSetting.Entry(this.mode, "module.spider.mode.blocks").method2103();
   private final ModeSetting.Entry waterMode = new ModeSetting.Entry(this.mode, "module.spider.mode.water");
   private final ModeSetting.Entry headMode = new ModeSetting.Entry(this.mode, "module.spider.mode.head");
   private long lastHeadUseTime;
   private int previousHotbarSlot = -1;
   private int waterBucketHotbarSlot = -1;
   private boolean waterPlaced;
   private Slot displacedWaterBucketSlot;

   private SpiderModule() {
   }

   @EventTarget
   public void onClimbRotation(PreRotationEvent event) {
      if (mc.player != null && mc.level != null) {
         if (this.waterMode.isSelected()) {
            if (mc.player.horizontalCollision) {
               BlockPos supportPosition = this.findAdjacentClimbBlock();
               if (supportPosition != null) {
                  Rotation rotation = RotationUtils.method6913(this.getWaterPlacementHitPosition(supportPosition));
                  rotationManager.requestRotation(
                     new RotationRequest(
                        rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()
                     ),
                     1,
                     this,
                     2
                  );
               }
            }
         } else if (this.blocksMode.isSelected()) {
            boolean hasOffhandBlock = mc.player.getOffhandItem().getItem() instanceof BlockItem;
            int blockHotbarSlot = InventoryUtils.method922(hotbarIndex -> mc.player.getInventory().getItem(hotbarIndex).getItem() instanceof BlockItem);
            BlockPos supportPosition = this.findPlacementSupport();
            if ((hasOffhandBlock || blockHotbarSlot != -1) && !supportPosition.equals(BlockPos.ZERO)) {
               Vec3 supportCenter = net.minecraft.world.phys.Vec3.atCenterOf(supportPosition).add(0.0, 0.0, 0.0);
               Direction supportDirection = Direction.getApproximateNearest(supportCenter.x - mc.player.getX(), supportCenter.y - mc.player.getY(), supportCenter.z - mc.player.getZ());
               Rotation rotation = new Rotation(
                  mc.player.getYRot(), RotationUtils.method6913(supportCenter.subtract(new Vec3(supportDirection.getUnitVec3i()).scale(0.5))).getFloatValue2()
               );
               rotationManager.requestRotation(
                  new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
                  1,
                  this,
                  2
               );
            }
         }
      }
   }

   @EventTarget
   public void onClimbInteraction(PostRotationEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         if (this.waterMode.isSelected()) {
            if (mc.player.horizontalCollision
               && mc.player.getMainHandItem().is(Items.WATER_BUCKET)
               && this.findAdjacentClimbBlock() != null
               && mc.hitResult instanceof BlockHitResult) {
               InteractionResult useResult = mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
               if (useResult instanceof Success) {
                  this.waterPlaced = true;
               }
            }
         } else if (this.blocksMode.isSelected()) {
            boolean hasOffhandBlock = mc.player.getOffhandItem().getItem() instanceof BlockItem;
            int blockHotbarSlot = InventoryUtils.method922(hotbarIndex -> mc.player.getInventory().getItem(hotbarIndex).getItem() instanceof BlockItem);
            BlockPos supportPosition = this.findPlacementSupport();
            if ((hasOffhandBlock || blockHotbarSlot != -1) && !supportPosition.equals(BlockPos.ZERO)) {
               ItemStack blockStack = hasOffhandBlock ? mc.player.getOffhandItem() : mc.player.getInventory().getItem(blockHotbarSlot);
               InteractionHand hand = hasOffhandBlock ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
               if (this.willPlacedBlockSupportPlayer(blockStack) && mc.hitResult instanceof BlockHitResult blockHitResult) {
                  int previousSelectedSlot = mc.player.inventory.getSelectedSlot();
                  if (!hasOffhandBlock) {
                     mc.player.inventory.setSelectedSlot(blockHotbarSlot);
                  }

                  UseItemUtils.method6514(blockHitResult, hand);
                  if (!hasOffhandBlock) {
                     mc.player.inventory.setSelectedSlot(previousSelectedSlot);
                  }
               }
            }
         }
      }
   }

   private boolean willPlacedBlockSupportPlayer(ItemStack blockStack) {
      BlockPos placementPosition = this.getPredictedFeetPosition();
      BlockItem blockItem = (BlockItem)blockStack.getItem();
      VoxelShape collisionShape = blockItem.getBlock().defaultBlockState().getCollisionShape(mc.level, placementPosition);
      if (collisionShape.isEmpty()) {
         return false;
      } else {
         AABB placedBlockBox = collisionShape.bounds().move(placementPosition);
         return !placedBlockBox.intersects(mc.player.getBoundingBox()) && placedBlockBox.intersects(PlayerSimulation.method6031(2).box);
      }
   }

   private BlockPos findPlacementSupport() {
      BlockPos placementPosition = this.getPredictedFeetPosition();
      return mc.level.getBlockState(placementPosition).isSolid()
         ? BlockPos.ZERO
         : Stream.of(placementPosition.west(), placementPosition.east(), placementPosition.south(), placementPosition.north())
            .filter(neighborPosition -> mc.level.getBlockState(neighborPosition).isSolid())
            .findFirst()
            .orElse(BlockPos.ZERO);
   }

   private BlockPos getPredictedFeetPosition() {
      return BlockPos.containing(PlayerSimulation.method6031(1).vec3d.add(0.0, -0.001, 0.0));
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (mc.player.horizontalCollision && mc.player.lastOnGround) {
         event.setJump(true);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         if (this.waterMode.isSelected()) {
            this.updateWaterClimb();
         } else {
            this.restoreHotbarSelection();
            if (this.headMode.isSelected()) {
               this.updateHeadClimb();
            }
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.displacedWaterBucketSlot = null;
      if (this.waterMode.isSelected()) {
         this.moveWaterBucketToLastSlot();
      }
   }

   @Override
   public void onDisable() {
      this.restoreHotbarSelection();
      this.restoreWaterBucketInventorySlot();
      this.lastHeadUseTime = 0L;
      super.onDisable();
   }

   private void updateWaterClimb() {
      if (!mc.player.horizontalCollision) {
         this.restoreHotbarSelection();
      } else if (this.findAdjacentClimbBlock() == null) {
         this.restoreHotbarSelection();
      } else {
         if (this.waterPlaced && !mc.player.onGround() && mc.player.isInWater()) {
            Vec3 deltaMovement = mc.player.getDeltaMovement();
            if (deltaMovement.y < 0.3F) {
               mc.player.setDeltaMovement(deltaMovement.x, 0.3F, deltaMovement.z);
            }
         }

         int bucketSlot;
         if ((bucketSlot = this.findWaterBucketHotbarSlot()) == -1) {
            this.restoreHotbarSelection();
         } else {
            if (!mc.player.getMainHandItem().is(Items.WATER_BUCKET)) {
               if (this.previousHotbarSlot == -1) {
                  this.previousHotbarSlot = mc.player.getInventory().getSelectedSlot();
               }

               this.waterBucketHotbarSlot = bucketSlot;
               mc.player.getInventory().setSelectedSlot(bucketSlot);
            }
         }
      }
   }

   private void moveWaterBucketToLastSlot() {
      if (mc.player != null && mc.gameMode != null) {
         Slot lastInventorySlot = mc.player.inventoryMenu.getSlot(44);
         if (!lastInventorySlot.getItem().is(Items.WATER_BUCKET)) {
            Slot bucketInventorySlot = InventoryUtils.method908(mc.player.inventoryMenu, Items.WATER_BUCKET);
            if (bucketInventorySlot != null && bucketInventorySlot.index != 44) {
               KeyBindUtils.method2627(SpiderModule.class, () -> {
                  if (this.isEnabled() && this.waterMode.isSelected() && mc.player != null && mc.gameMode != null) {
                     Slot currentLastInventorySlot = mc.player.inventoryMenu.getSlot(44);
                     if (!currentLastInventorySlot.getItem().is(Items.WATER_BUCKET)) {
                        Slot currentBucketInventorySlot = InventoryUtils.method908(mc.player.inventoryMenu, Items.WATER_BUCKET);
                        if (currentBucketInventorySlot != null && currentBucketInventorySlot.index != 44) {
                           this.displacedWaterBucketSlot = currentBucketInventorySlot;
                           InventoryUtils.method934(currentBucketInventorySlot, 44, false, false);
                        }
                     }
                  }
               });
            }
         }
      }
   }

   private void restoreWaterBucketInventorySlot() {
      if (this.displacedWaterBucketSlot != null) {
         Slot originalBucketSlot = this.displacedWaterBucketSlot;
         this.displacedWaterBucketSlot = null;
         KeyBindUtils.method2627(SpiderModule.class, () -> {
            if (mc.player != null && mc.gameMode != null) {
               Slot lastInventorySlot = mc.player.inventoryMenu.getSlot(44);
               InventoryUtils.method934(lastInventorySlot, originalBucketSlot.index, false, false);
            }
         });
      }
   }

   private BlockPos findAdjacentClimbBlock() {
      BlockPos playerBlockPosition = BlockPos.containing(mc.player.position());

      for (int verticalOffset = 1; verticalOffset >= -1; verticalOffset--) {
         BlockPos searchPosition = playerBlockPosition.above(verticalOffset);
         BlockPos nearestClimbBlock = null;
         double nearestDistanceSquared = Double.MAX_VALUE;

         for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            BlockPos neighborPosition = searchPosition.relative(direction);
            VoxelShape collisionShape = mc.level.getBlockState(neighborPosition).getCollisionShape(mc.level, neighborPosition);
            double distanceSquared;
            if (!collisionShape.isEmpty() && (distanceSquared = net.minecraft.world.phys.Vec3.atCenterOf(neighborPosition).distanceToSqr(mc.player.position())) < nearestDistanceSquared) {
               nearestDistanceSquared = distanceSquared;
               nearestClimbBlock = neighborPosition;
            }
         }

         if (nearestClimbBlock != null) {
            return nearestClimbBlock;
         }
      }

      return null;
   }

   private Vec3 getWaterPlacementHitPosition(BlockPos blockPosition) {
      Vec3 blockCenter = net.minecraft.world.phys.Vec3.atCenterOf(blockPosition);
      double deltaX = mc.player.getX() - blockCenter.x;
      double deltaZ = mc.player.getZ() - blockCenter.z;
      double x = Math.max(blockPosition.getX() + 0.1, Math.min(blockPosition.getX() + 0.9, mc.player.getX()));
      double z = Math.max(blockPosition.getZ() + 0.1, Math.min(blockPosition.getZ() + 0.9, mc.player.getZ()));
      if (blockPosition.getY() < Math.floor(mc.player.getY())) {
         return new Vec3(x, blockPosition.getY() + 1.001, z);
      } else {
         if (Math.abs(deltaX) > Math.abs(deltaZ)) {
            x = blockCenter.x + Math.copySign(0.501, deltaX);
         } else {
            z = blockCenter.z + Math.copySign(0.501, deltaZ);
         }

         return new Vec3(x, blockPosition.getY() + 0.75, z);
      }
   }

   private void updateHeadClimb() {
      if (mc.player.horizontalCollision) {
         if (mc.player.onGround()) {
            mc.player.jumpFromGround();
         }

         long now;
         if ((now = System.currentTimeMillis()) - this.lastHeadUseTime >= 150L && mc.player.getOffhandItem().is(Items.PLAYER_HEAD)) {
            float yaw = mc.player.getYRot();
            float pitch = mc.player.getXRot();
            Vec3 eyePosition = mc.player.getEyePosition();
            Vec3 rayEnd = eyePosition.add(this.getLookDirection(pitch, yaw).scale(4.0));
            BlockHitResult blockHit = mc.level.clip(new ClipContext(eyePosition, rayEnd, Block.OUTLINE, Fluid.NONE, mc.player));
            if (blockHit.getType() == Type.BLOCK && mc.level.getBlockState(blockHit.getBlockPos()).isRedstoneConductor(mc.level, blockHit.getBlockPos())) {
               mc.player.connection.send(new Rot(yaw, pitch, mc.player.onGround(), mc.player.horizontalCollision));
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.OFF_HAND);
               mc.gameMode.useItemOn(mc.player, InteractionHand.OFF_HAND, blockHit);
               mc.player.fallDistance = 0.0;
               this.lastHeadUseTime = now;
            }
         }
      }
   }

   private int findWaterBucketHotbarSlot() {
      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         if (mc.player.getInventory().getItem(hotbarSlot).is(Items.WATER_BUCKET)) {
            return hotbarSlot;
         }
      }

      return -1;
   }

   private void restoreHotbarSelection() {
      if (mc.player != null && this.previousHotbarSlot != -1 && (this.waterBucketHotbarSlot == -1 || mc.player.getInventory().getSelectedSlot() == this.waterBucketHotbarSlot)) {
         mc.player.getInventory().setSelectedSlot(this.previousHotbarSlot);
      }

      this.previousHotbarSlot = -1;
      this.waterBucketHotbarSlot = -1;
      this.waterPlaced = false;
   }

   private Vec3 getLookDirection(float pitch, float yaw) {
      float pitchRadians = (float)Math.toRadians(pitch);
      float negativeYawRadians;
      float yawCosine = (float)Math.cos(negativeYawRadians = (float)Math.toRadians(-yaw));
      float yawSine = (float)Math.sin(negativeYawRadians);
      float pitchCosine = (float)Math.cos(pitchRadians);
      float pitchSine = (float)Math.sin(pitchRadians);
      return new Vec3(yawSine * pitchCosine, -pitchSine, yawCosine * pitchCosine);
   }

   @Generated
   public void method7039(long lastHeadUseTime) {
      this.lastHeadUseTime = lastHeadUseTime;
   }

   @Generated
   public void method7040(int previousHotbarSlot) {
      this.previousHotbarSlot = previousHotbarSlot;
   }

   @Generated
   public void method7041(int waterBucketHotbarSlot) {
      this.waterBucketHotbarSlot = waterBucketHotbarSlot;
   }

   @Generated
   public void method7042(boolean waterPlaced) {
      this.waterPlaced = waterPlaced;
   }

   @Generated
   public void setSlot(Slot displacedWaterBucketSlot) {
      this.displacedWaterBucketSlot = displacedWaterBucketSlot;
   }

   @Generated
   public ModeSetting getMode2() {
      return this.mode;
   }

   @Generated
   public ModeSetting.Entry getEntry10() {
      return this.blocksMode;
   }

   @Generated
   public ModeSetting.Entry getEntry22() {
      return this.waterMode;
   }

   @Generated
   public ModeSetting.Entry getEntry32() {
      return this.headMode;
   }

   @Generated
   public long getLongValue6() {
      return this.lastHeadUseTime;
   }

   @Generated
   public int getIntValue19() {
      return this.previousHotbarSlot;
   }

   @Generated
   public int getIntValue24() {
      return this.waterBucketHotbarSlot;
   }

   @Generated
   public boolean isFlag11() {
      return this.waterPlaced;
   }

   @Generated
   public Slot getSlot2() {
      return this.displacedWaterBucketSlot;
   }
}

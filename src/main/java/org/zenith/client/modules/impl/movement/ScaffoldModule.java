package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.SwingCompat;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(name = "Scaffold", description = "module.scaffold.description", category = ModuleCategory.MOVEMENT)
public final class ScaffoldModule extends Module {
   public static final ScaffoldModule INSTANCE = new ScaffoldModule();

   private final ModeSetting rotations = new ModeSetting("module.scaffold.rotations", "module.scaffold.rotations.desc",
      "module.scaffold.rotations.silent", "module.scaffold.rotations.client", "module.scaffold.rotations.off");
   private final ModeSetting blockSlot = new ModeSetting("module.scaffold.blockSlot", "module.scaffold.blockSlot.desc",
      "module.scaffold.blockSlot.auto", "module.scaffold.blockSlot.manual");
   private final BooleanSetting tower = new BooleanSetting("module.scaffold.tower", "module.scaffold.tower.desc", true);
   private final SliderSetting radius = new SliderSetting("module.scaffold.radius", 0.0F, 0.0F, 2.0F, 1.0F,
      "module.scaffold.radius.desc", "b");
   private BlockHitResult plannedHit;
   private BlockPos plannedPosition;
   private ClientLevel plannedLevel;
   private ClientLevel lastAttemptLevel;
   private long lastAttemptTick = Long.MIN_VALUE;

   private ScaffoldModule() {
   }

   @EventTarget
   public void onPreRotation(PreRotationEvent event) {
      this.clearPlan();
      if (!this.canBuild()) {
         return;
      }
      int slot = this.findBlockSlot();
      if (slot == -1) {
         return;
      }
      Vec3 velocity = mc.player.getDeltaMovement();
      // Прогноз ограничен одним коротким шагом: расширение опоры не увеличивает дальность взаимодействия.
      Vec3 ahead = new Vec3(velocity.x, 0.0, velocity.z);
      if (ahead.horizontalDistance() > 0.35) {
         ahead = ahead.normalize().scale(0.35);
      }
      BlockPos target = BlockPos.containing(mc.player.getX() + ahead.x,
         mc.player.getBoundingBox().minY - 0.01, mc.player.getZ() + ahead.z);
      if (!mc.level.getBlockState(target).canBeReplaced()) {
         return;
      }

      ItemStack stack = mc.player.getInventory().getItem(slot);
      int searchRadius = (int)this.radius.getValue3();
      var candidates = new ArrayList<BlockPos>();
      for (int x = -searchRadius; x <= searchRadius; x++) {
         for (int z = -searchRadius; z <= searchRadius; z++) {
            if (x * x + z * z <= searchRadius * searchRadius) {
               candidates.add(target.offset(x, 0, z));
            }
         }
      }
      candidates.sort(Comparator.comparingDouble(position -> position.distSqr(target)));
      for (BlockPos position : candidates) {
         if (this.findPlacement(position, stack)) {
            this.plannedPosition = position;
            this.plannedLevel = mc.level;
            if (!this.rotations.isSelected(2)) {
               Rotation rotation = Rotation.lookingAt(this.plannedHit.getLocation(), mc.player.getEyePosition());
               var config = rotationManager.getBcCameraTestnerv().getRotationConfig();
               rotationManager.requestRotation(new RotationRequest(rotation, () -> rotation, config), 5, this);
            }
            return;
         }
      }
   }

   private boolean findPlacement(BlockPos position, ItemStack stack) {
      if (!mc.level.hasChunkAt(position) || mc.level.isOutsideBuildHeight(position)
         || !mc.level.getWorldBorder().isWithinBounds(position) || !mc.level.getBlockState(position).canBeReplaced()) {
         return false;
      }
      Vec3 eye = mc.player.getEyePosition();
      double reach = mc.player.blockInteractionRange();
      for (Direction direction : Direction.values()) {
         BlockPos support = position.relative(direction);
         var supportState = mc.level.getBlockState(support);
         if (supportState.canBeReplaced() || supportState.getCollisionShape(mc.level, support).isEmpty()
            || supportState.getMenuProvider(mc.level, support) != null) {
            continue;
         }
         Direction face = direction.getOpposite();
         Vec3 faceCenter = Vec3.atCenterOf(support).add(Vec3.atLowerCornerOf(face.getUnitVec3i()).scale(0.5));
         Rotation aim = this.rotations.isSelected(2)
            ? new Rotation(mc.player.getYRot(), mc.player.getXRot()) : Rotation.lookingAt(faceCenter, eye);
         BlockHitResult hit = this.raycast(aim);
         if (hit.getType() == HitResult.Type.BLOCK && !hit.isInside()
            && hit.getBlockPos().equals(support) && hit.getDirection() == face
            && eye.distanceToSqr(hit.getLocation()) <= reach * reach && this.canPlace(stack, hit, position)) {
            this.plannedHit = hit;
            return true;
         }
      }
      return false;
   }

   @EventTarget
   public void onPostRotation(PostRotationEvent event) {
      if (this.plannedHit != null && this.plannedLevel == mc.level && mc.player != null
         && this.rotations.isSelected(1) && rotationManager.getModule() == this) {
         Rotation rotation = rotationManager.getEffectiveRotation();
         mc.player.setYRot(rotation.getFloatValue());
         mc.player.setXRot(rotation.getFloatValue2());
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (mc.player != null && this.rotations.isSelected(0) && rotationManager.getModule() == this) {
         MovementInputUtils.correctMovement(event, rotationManager.getEffectiveRotation().getFloatValue(), mc.player.getYRot());
      }
   }

   @EventTarget
   public void onPostMotion(PostMotionEvent event) {
      if (!this.canBuild() || this.plannedHit == null || this.plannedLevel != mc.level
         || this.lastAttemptLevel == mc.level && this.lastAttemptTick == mc.level.getGameTime()
         || !this.rotations.isSelected(2) && rotationManager.getModule() != this
         || this.rotations.isSelected(2) && rotationManager.getCurrentRotation() != null) {
         return;
      }
      // Установка идёт после обычного sendPosition: искусственных пакетов поворота здесь нет.
      Rotation rotation = this.rotations.isSelected(2)
         ? new Rotation(mc.player.getYRot(), mc.player.getXRot()) : rotationManager.getEffectiveRotation();
      if (!rotation.isWithinAngle(rotationManager.getRotation2(), 0.1F)) {
         return;
      }
      BlockHitResult hit = this.raycast(rotation);
      if (hit.getType() != HitResult.Type.BLOCK || hit.isInside()
         || !hit.getBlockPos().equals(this.plannedHit.getBlockPos()) || hit.getDirection() != this.plannedHit.getDirection()) {
         return;
      }
      int slot = this.findBlockSlot();
      if (slot == -1 || !this.canPlace(mc.player.getInventory().getItem(slot), hit, this.plannedPosition)) {
         return;
      }

      int previousSlot = mc.player.getInventory().getSelectedSlot();
      float previousYaw = mc.player.getYRot();
      float previousPitch = mc.player.getXRot();
      this.lastAttemptLevel = mc.level;
      this.lastAttemptTick = mc.level.getGameTime();
      try {
         mc.player.getInventory().setSelectedSlot(slot);
         mc.gameMode.ensureHasSentCarriedItem();
         // Ванильный BlockPlaceContext использует взгляд игрока при выборе состояния блока.
         mc.player.setYRot(rotation.getFloatValue());
         mc.player.setXRot(rotation.getFloatValue2());
         InteractionResult result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
         if (result instanceof InteractionResult.Success success) {
            if (success.swingSource() == InteractionResult.SwingSource.PREDICTED) {
               SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            }
            mc.player.itemUsed(InteractionHand.MAIN_HAND);
         }
      } finally {
         mc.player.setYRot(previousYaw);
         mc.player.setXRot(previousPitch);
         if (mc.player.getInventory().getSelectedSlot() == slot) {
            mc.player.getInventory().setSelectedSlot(previousSlot);
            mc.gameMode.ensureHasSentCarriedItem();
         }
         this.clearPlan();
      }
   }

   private boolean canBuild() {
      return mc.player != null && mc.level != null && mc.gameMode != null && mc.gui.screen() == null
         && mc.player.isAlive() && !mc.player.isSpectator() && !mc.player.isPassenger()
         && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isFallFlying()
         && !mc.player.getAbilities().flying && !mc.player.isUsingItem() && !mc.gameMode.isDestroying()
         // Башня использует обычный удерживаемый прыжок: блок ставится лишь после освобождения места ногами.
         && (this.tower.isEnabled() || !mc.options.keyJump.isDown() || this.hasHorizontalInput());
   }

   private boolean hasHorizontalInput() {
      return mc.options.keyUp.isDown() || mc.options.keyDown.isDown()
         || mc.options.keyLeft.isDown() || mc.options.keyRight.isDown();
   }

   private int findBlockSlot() {
      int selected = mc.player.getInventory().getSelectedSlot();
      if (this.isBuildingBlock(mc.player.getInventory().getItem(selected))) {
         return selected;
      }
      if (this.blockSlot.isSelected(0)) {
         for (int slot = 0; slot < 9; slot++) {
            if (this.isBuildingBlock(mc.player.getInventory().getItem(slot))) {
               return slot;
            }
         }
      }
      return -1;
   }

   private boolean isBuildingBlock(ItemStack stack) {
      if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem) || blockItem.getBlock() instanceof FallingBlock) {
         return false;
      }
      // Только полноценная опора: песок падает, растения и неполные блоки не гарантируют мост под ногами.
      return blockItem.getBlock().defaultBlockState().isCollisionShapeFullBlock(mc.level, mc.player.blockPosition());
   }

   private boolean canPlace(ItemStack stack, BlockHitResult hit, BlockPos position) {
      if (!this.isBuildingBlock(stack) || mc.level.isOutsideBuildHeight(position)
         || !mc.level.getWorldBorder().isWithinBounds(position)) {
         return false;
      }
      BlockPlaceContext context = new BlockPlaceContext(mc.player, InteractionHand.MAIN_HAND, stack, hit);
      if (!context.canPlace() || !context.getClickedPos().equals(position)) {
         return false;
      }
      var state = ((BlockItem)stack.getItem()).getBlock().getStateForPlacement(context);
      return state != null && state.isCollisionShapeFullBlock(mc.level, position) && state.canSurvive(mc.level, position)
         && mc.level.isUnobstructed(state, position, CollisionContext.of(mc.player));
   }

   private BlockHitResult raycast(Rotation rotation) {
      Vec3 eye = mc.player.getEyePosition();
      return mc.level.clip(new ClipContext(eye, eye.add(rotation.toDirection().scale(mc.player.blockInteractionRange())),
         ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
   }

   private void clearPlan() {
      this.plannedHit = null;
      this.plannedPosition = null;
      this.plannedLevel = null;
   }

   @Override
   public void onDisable() {
      this.clearPlan();
      this.lastAttemptLevel = null;
      this.lastAttemptTick = Long.MIN_VALUE;
      super.onDisable();
   }
}

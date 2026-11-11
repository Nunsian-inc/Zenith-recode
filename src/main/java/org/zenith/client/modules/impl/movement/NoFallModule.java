package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.SwingCompat;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.TimerSpeed;

@ModuleInfo(
   name = "NoFall",
   description = "module.nofall.description",
   category = ModuleCategory.MOVEMENT
)
public final class NoFallModule extends Module {
   public static final NoFallModule INSTANCE = new NoFallModule();
   private static final double FALL_DISTANCE_THRESHOLD = 3.0;
   private final ModeSetting mode = new ModeSetting("module.noFall.mode", "module.noFall.mode.desc", "module.noFall.mode.grim72", "module.noFall.mode.grim73", "module.noFall.mode.mlg");
   private final SliderSetting clutchHeight = new SliderSetting("module.noFall.mlg.height", 4.0F, 3.0F, 30.0F, 0.5F,
      "module.noFall.mlg.height.desc", "b", this::isClutchMode);
   private final SliderSetting clutchRotationSpeed = new SliderSetting("module.noFall.mlg.rotationSpeed", 45.0F, 5.0F, 180.0F, 5.0F,
      "module.noFall.mlg.rotationSpeed.desc", "°/tick", this::isClutchMode);
   private final BooleanSetting clutchWater = new BooleanSetting("module.noFall.mlg.water", "module.noFall.mlg.water.desc", true, this::isClutchMode);
   private final BooleanSetting clutchWeb = new BooleanSetting("module.noFall.mlg.web", "module.noFall.mlg.web.desc", true, this::isClutchMode);
   private final BooleanSetting clutchHay = new BooleanSetting("module.noFall.mlg.hay", "module.noFall.mlg.hay.desc", true, this::isClutchMode);
   private ClientLevel clutchLevel;
   private BlockHitResult clutchHit;
   private boolean clutchPlaced;
   private int nextClutchAttempt;
   private final StopWatch activationTimer = new StopWatch();
   private final StopWatch fallFlyingTimer = new StopWatch();
   private boolean falling;
   private boolean awaitingCorrection;
   private int correctionTicks;
   private boolean timerModified;

   private NoFallModule() {
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.resetClutch();
      this.falling = false;
      this.awaitingCorrection = false;
      this.correctionTicks = 0;
      if (this.timerModified) {
         TimerSpeed.setSpeedMultiplier(1.0F);
         this.timerModified = false;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player == null || mc.level == null || clutchLevel != null && clutchLevel != mc.level) {
         this.reset();
      }
      if (this.isClutchMode()) {
         // Пакетные режимы и изменение таймера не участвуют в обычном размещении.
         this.falling = false;
         this.awaitingCorrection = false;
         this.correctionTicks = 0;
         if (this.timerModified) {
            TimerSpeed.setSpeedMultiplier(1.0F);
            this.timerModified = false;
         }
         return;
      }
      this.resetClutch();
      if (mc.player != null && mc.level != null) {
         this.falling = mc.player.fallDistance > 3.0;
         if (this.isGrim72Mode()) {
            if (this.falling && !mc.player.onGround() && this.fallFlyingTimer.hasElapsed(200L) && PlayerSimulation.method6031(1).flag4) {
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
               this.fallFlyingTimer.reset();
               this.activationTimer.reset();
               this.awaitingCorrection = true;
            }
         } else if (this.correctionTicks == 2) {
            TimerSpeed.setSpeedMultiplier(0.5F);
            this.timerModified = true;
         } else if (this.timerModified) {
            TimerSpeed.setSpeedMultiplier(1.0F);
            this.timerModified = false;
         }

         if (this.correctionTicks > 0) {
            this.correctionTicks--;
         }

         if (this.activationTimer.hasElapsed(300L)) {
            this.awaitingCorrection = false;
            this.correctionTicks = 0;
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null) {
         if (event.isOutgoing()) {
            if (this.isGrim73Mode()
               && event.getPacket() instanceof ServerboundMovePlayerPacket movePlayerPacket
               && !PacketUtils.getItems().contains(movePlayerPacket)
               && this.falling
               && movePlayerPacket.isOnGround()
               && this.correctionTicks == 0) {
               PacketUtils.method3150(new StatusOnly(true, false));
               this.activationTimer.reset();
               this.awaitingCorrection = true;
            }
         } else if (!(event.getPacket() instanceof ClientboundLoginPacket) && !(event.getPacket() instanceof ClientboundRespawnPacket)) {
            if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
               mc.execute(() -> {
                  if (this.awaitingCorrection) {
                     this.correctionTicks = 2;
                     this.awaitingCorrection = false;
                  }
               });
            }
         } else {
            mc.execute(this::reset);
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.awaitingCorrection || this.correctionTicks == 2) {
         event.setJump(false);
      } else if (this.correctionTicks == 1) {
         event.setJump(true);
      }
   }

   @EventTarget
   public void onPreRotation(PreRotationEvent event) {
      this.clutchHit = null;
      if (!this.isClutchMode() || mc.player == null || mc.level == null || mc.gameMode == null) {
         this.resetClutch();
         return;
      }
      if (this.clutchLevel != mc.level) {
         this.resetClutch();
         this.clutchLevel = mc.level;
      }
      if (mc.player.onGround() || mc.player.isInWater() || mc.player.isInLava() || mc.player.getDeltaMovement().y >= 0.0) {
         this.clutchPlaced = false;
         return;
      }
      if (this.clutchPlaced || mc.player.isDeadOrDying() || mc.player.isSpectator() || mc.player.isPassenger()
         || mc.player.isFallFlying() || mc.player.getAbilities().flying || mc.player.isUsingItem() || mc.gui.screen() != null) {
         return;
      }
      // ponytail: прогноз ограничен 20 тиками и полной верхней гранью; сложные опоры требуют отдельного поиска граней.
      PlayerSimulation simulation = PlayerSimulation.method6031(0);
      for (int tick = 0; tick < 20; tick++) {
         Vec3 previous = simulation.vec3d;
         double fallDistance = simulation.floatValue3;
         simulation.tick();
         if (simulation.flag4) {
            if (Math.max(mc.player.fallDistance, fallDistance + previous.y - simulation.vec3d.y) < this.clutchHeight.getValue3()) {
               return;
            }
            BlockPos support = BlockPos.containing(simulation.vec3d.add(0.0, -0.01, 0.0));
            if (Math.abs(simulation.vec3d.y - (support.getY() + 1.0)) > 0.01 || !this.validSupport(support)) {
               return;
            }
            Vec3 aim = new Vec3(simulation.vec3d.x, support.getY() + 1.0, simulation.vec3d.z);
            this.clutchHit = new BlockHitResult(aim, Direction.UP, support, false);
            if (this.findClutchSlot(support.above()) == -1) {
               this.clutchHit = null;
               return;
            }
            Rotation target = Rotation.lookingAt(aim, mc.player.getEyePosition());
            Rotation stepped = rotationManager.getEffectiveRotation().stepTowards(target,
               this.clutchRotationSpeed.getValue3(), this.clutchRotationSpeed.getValue3());
            var config = rotationManager.getBcCameraTestnerv().getRotationConfig();
            rotationManager.requestRotation(new RotationRequest(target, () -> stepped, config), 20, this);
            return;
         }
         if (!mc.level.hasChunkAt(BlockPos.containing(simulation.vec3d)) || simulation.vec3d.y < mc.level.getMinY()) {
            return;
         }
      }
   }

   @EventTarget
   public void onPostRotation(PostRotationEvent event) {
      if (!this.isClutchMode() || mc.player == null || mc.level == null || mc.gameMode == null
         || this.clutchLevel != mc.level || this.clutchHit == null || this.clutchPlaced
         || rotationManager.getModule() != this || mc.player.tickCount < this.nextClutchAttempt) {
         return;
      }
      BlockPos support = this.clutchHit.getBlockPos();
      if (!this.validSupport(support) || mc.player.onGround() || mc.player.isUsingItem() || mc.gui.screen() != null) {
         return;
      }
      Vec3 eye = mc.player.getEyePosition();
      double reach = mc.player.blockInteractionRange();
      Rotation rotation = rotationManager.getEffectiveRotation();
      // Используется реальный луч текущего поворота, а не синтетический результат прогноза.
      BlockHitResult hit = mc.level.clip(new ClipContext(eye, eye.add(rotation.toDirection().scale(reach)),
         ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
      if (hit.getType() != HitResult.Type.BLOCK || hit.isInside() || !hit.getBlockPos().equals(support)
         || hit.getDirection() != Direction.UP || eye.distanceToSqr(hit.getLocation()) > reach * reach) {
         return;
      }
      int slot = this.findClutchSlot(support.above());
      if (slot == -1) {
         return;
      }
      var stack = mc.player.getInventory().getItem(slot);
      boolean water = stack.is(Items.WATER_BUCKET);
      if (!water) {
         BlockPlaceContext context = new BlockPlaceContext(mc.player, InteractionHand.MAIN_HAND, stack, hit);
         if (!context.canPlace() || !context.getClickedPos().equals(support.above())) {
            return;
         }
         var placement = ((BlockItem)stack.getItem()).getBlock().getStateForPlacement(context);
         if (placement == null || !placement.canSurvive(mc.level, support.above())
            || !mc.level.isUnobstructed(placement, support.above(), net.minecraft.world.phys.shapes.CollisionContext.of(mc.player))) {
            return;
         }
      }
      int previousSlot = mc.player.getInventory().getSelectedSlot();
      float previousYaw = mc.player.getYRot();
      float previousPitch = mc.player.getXRot();
      this.nextClutchAttempt = mc.player.tickCount + 2;
      try {
         mc.player.getInventory().setSelectedSlot(slot);
         mc.gameMode.ensureHasSentCarriedItem();
         mc.player.setYRot(rotation.getFloatValue());
         mc.player.setXRot(rotation.getFloatValue2());
         // useItemOn не выливает BucketItem: ведро использует обычный useItem и собственный луч.
         mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(rotation.getFloatValue(), rotation.getFloatValue2(),
            mc.player.onGround(), mc.player.horizontalCollision));
         InteractionResult result = water ? mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND)
            : mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
         if (result instanceof InteractionResult.Success success) {
            this.clutchPlaced = true;
            if (success.swingSource() == InteractionResult.SwingSource.PREDICTED) {
               SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            }
         }
      } finally {
         mc.player.setYRot(previousYaw);
         mc.player.setXRot(previousPitch);
         // Не перезаписываем выбор пользователя/другого обработчика, сделанный во время взаимодействия.
         if (mc.player.getInventory().getSelectedSlot() == slot) {
            mc.player.getInventory().setSelectedSlot(previousSlot);
            mc.gameMode.ensureHasSentCarriedItem();
         }
      }
   }

   private boolean validSupport(BlockPos support) {
      BlockPos placement = support.above();
      if (!mc.level.hasChunkAt(support) || mc.level.isOutsideBuildHeight(support) || mc.level.isOutsideBuildHeight(placement)
         || !mc.level.getWorldBorder().isWithinBounds(support) || !mc.level.getWorldBorder().isWithinBounds(placement)) {
         return false;
      }
      var state = mc.level.getBlockState(support);
      return state.isFaceSturdy(mc.level, support, Direction.UP) && state.getMenuProvider(mc.level, support) == null
         && mc.level.getBlockState(placement).canBeReplaced() && mc.level.getFluidState(placement).isEmpty();
   }

   private int findClutchSlot(BlockPos placement) {
      return InventoryUtils.method922(slot -> {
         var stack = mc.player.getInventory().getItem(slot);
         if (stack.isEmpty() || mc.player.getCooldowns().isOnCooldown(stack)) {
            return false;
         }
         return this.clutchWater.isEnabled() && stack.is(Items.WATER_BUCKET)
               && !(mc.level.getBlockState(placement.below()).getBlock() instanceof net.minecraft.world.level.block.LiquidBlockContainer)
               && !mc.level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, placement)
            || this.clutchWeb.isEnabled() && stack.is(Items.COBWEB)
            || this.clutchHay.isEnabled() && stack.is(Items.HAY_BLOCK);
      });
   }

   private void resetClutch() {
      this.clutchHit = null;
      this.clutchLevel = null;
      this.clutchPlaced = false;
      this.nextClutchAttempt = 0;
   }

   private boolean isClutchMode() {
      return this.mode.isSelected(2);
   }

   private boolean isGrim72Mode() {
      return this.mode.isSelected(0);
   }

   private boolean isGrim73Mode() {
      return this.mode.isSelected(1);
   }
}

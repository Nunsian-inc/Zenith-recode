package org.zenith.base.bot.view;

import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Fail;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.SwingSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.events.BotUpdateEvent;
import org.zenith.utility.misc.ViaCompat;

public final class BotRemoteControl {
   private final BotClient client;
   private volatile boolean forward;
   private volatile boolean backward;
   private volatile boolean left;
   private volatile boolean right;
   private volatile boolean jump;
   private volatile boolean sneak;
   private volatile boolean sprint;
   private volatile boolean attackHeld;
   private volatile boolean useHeld;
   private final AtomicInteger attackPresses = new AtomicInteger();
   private final AtomicInteger usePresses = new AtomicInteger();
   private final AtomicInteger requestedSlot = new AtomicInteger(-1);
   private final AtomicInteger dropRequest = new AtomicInteger();
   private volatile boolean swapHandsRequested;
   private volatile boolean uiScreenOpen;
   private int attackCooldown;
   private int itemUseCooldown;
   private boolean wasMovementActive;
   private boolean wasAttackActive;
   private boolean wasUseActive;
   @Nullable
   private volatile HitResult crosshairTarget;
   private volatile boolean attached;

   public BotRemoteControl(BotClient client) {
      this.client = client;
   }

   public void attach() {
      if (!this.attached) {
         this.attached = true;
         this.client.execute(() -> this.client.getEventBus().register(this, BotUpdateEvent.class));
      }
   }

   public void detach() {
      if (this.attached) {
         this.attached = false;
         this.client.execute(() -> {
            this.client.getEventBus().unregister(this);
            this.releaseAll();
            BotPlayer player = this.client.getPlayer();
            BotPlayHandler playHandler = this.client.getPlayHandler();
            if (player != null && playHandler != null) {
               if (this.wasMovementActive) {
                  clearMovementInput(player);
                  this.wasMovementActive = false;
               }

               try {
                  if (this.wasAttackActive) {
                     playHandler.getInteractionManager().cancelBlockBreaking();
                  }

                  if (this.wasUseActive && player.isUsingItem()) {
                     playHandler.getInteractionManager().stopUsingItem(player);
                  }
               } catch (IllegalStateException e) {
               }

               this.wasAttackActive = false;
               this.wasUseActive = false;
            }
         });
      }
   }

   public void setMovement(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
      this.forward = forward;
      this.backward = backward;
      this.left = left;
      this.right = right;
      this.jump = jump;
      this.sneak = sneak;
      this.sprint = sprint;
   }

   public void releaseAll() {
      this.forward = this.backward = this.left = this.right = this.jump = this.sneak = this.sprint = false;
      this.attackHeld = false;
      this.useHeld = false;
      this.attackPresses.set(0);
      this.usePresses.set(0);
      this.dropRequest.set(0);
      this.swapHandsRequested = false;
   }

   public void setUiScreenOpen(boolean uiScreenOpen) {
      this.uiScreenOpen = uiScreenOpen;
   }

   public void pressAttack() {
      this.attackHeld = true;
      this.attackPresses.incrementAndGet();
   }

   public void releaseAttack() {
      this.attackHeld = false;
   }

   public void pressUse() {
      this.useHeld = true;
      this.usePresses.incrementAndGet();
   }

   public void releaseUse() {
      this.useHeld = false;
   }

   public void requestSlot(int wheelSlot) {
      if (wheelSlot >= 0 && wheelSlot <= 8) {
         this.requestedSlot.set(wheelSlot);
      }
   }

   public void requestDrop(boolean flag) {
      this.dropRequest.set(flag ? 2 : 1);
   }

   public void requestSwapHands() {
      this.swapHandsRequested = true;
   }

   public void turn(double xo, double yo) {
      BotPlayer player = this.client.getPlayer();
      if (player != null && !this.uiScreenOpen) {
         synchronized (this.client.getRenderStateLock()) {
            player.turn(xo, yo);
         }
      }
   }

   public void clickSlot(int index, int buttonNum, ContainerInput containerInput) {
      this.client.execute(() -> {
         BotPlayer player = this.client.getPlayer();
         BotPlayHandler playHandler = this.client.getPlayHandler();
         if (player != null && playHandler != null) {
            try {
               playHandler.getInteractionManager().clickSlot(player.containerMenu.containerId, index, buttonNum, containerInput, player);
            } catch (Exception e) {
            }
         }
      });
   }

   public void closeScreen() {
      this.client.execute(() -> {
         BotPlayer player = this.client.getPlayer();
         if (player != null) {
            player.closeContainer();
         }
      });
   }

   @Nullable
   public HitResult getCrosshairTarget() {
      return this.crosshairTarget;
   }

   @EventTarget
   public void onBotUpdate(BotUpdateEvent event) {
      BotWorld world = event.getWorld();
      BotPlayer player = event.getPlayer();
      BotPlayHandler playHandler = this.client.getPlayHandler();
      if (world != null && player != null && playHandler != null) {
         BotInteractionManager interactionManager;
         try {
            interactionManager = playHandler.getInteractionManager();
         } catch (IllegalStateException e) {
            return;
         }

         if (this.attackCooldown > 0) {
            this.attackCooldown--;
         }

         if (this.itemUseCooldown > 0) {
            this.itemUseCooldown--;
         }

         boolean flag = this.uiScreenOpen || playHandler.hasOpenScreen();
         this.crosshairTarget = findCrosshairTarget(player);
         int andSet = this.requestedSlot.getAndSet(-1);
         if (andSet >= 0) {
            player.getInventory().setSelectedSlot(andSet);
         }

         if (flag) {
            this.attackPresses.set(0);
            this.usePresses.set(0);
            if (this.wasMovementActive) {
               clearMovementInput(player);
               this.wasMovementActive = false;
            }

            if (this.wasAttackActive) {
               interactionManager.cancelBlockBreaking();
               this.wasAttackActive = false;
            }

            if (this.wasUseActive) {
               if (player.isUsingItem()) {
                  interactionManager.stopUsingItem(player);
               }

               this.wasUseActive = false;
            }
         } else {
            boolean wasMovementActive = this.forward || this.backward || this.left || this.right || this.jump || this.sneak || this.sprint;
            if (wasMovementActive || this.wasMovementActive) {
               this.applyMovementInput(player);
            }

            this.wasMovementActive = wasMovementActive;
            int n = this.dropRequest.getAndSet(0);
            if (n > 0 && !player.isSpectator()) {
               player.dropSelectedItem(n == 2);
            }

            if (this.swapHandsRequested) {
               this.swapHandsRequested = false;
               if (!player.isSpectator()) {
                  playHandler.sendPacket(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
               }
            }

            if (player.isUsingItem()) {
               if (!this.useHeld && this.wasUseActive) {
                  interactionManager.stopUsingItem(player);
                  this.wasUseActive = false;
               }

               this.attackPresses.set(0);
               this.usePresses.set(0);
            } else {
               this.wasUseActive = false;
               int andSet2 = this.attackPresses.getAndSet(0);

               for (int i = 0; i < andSet2; i++) {
                  this.doAttack(world, player, interactionManager);
               }

               boolean flag2;
               if (this.useHeld) {
                  flag2 = this.itemUseCooldown == 0;
                  if (flag2) {
                     this.usePresses.set(0);
                  }
               } else {
                  flag2 = this.usePresses.getAndSet(0) > 0 && this.itemUseCooldown == 0;
               }

               if (flag2) {
                  this.doItemUse(world, player, interactionManager);
                  this.wasUseActive = true;
               }
            }

            boolean attackHeld = this.attackHeld;
            if (attackHeld || this.wasAttackActive) {
               this.handleBlockBreaking(world, player, interactionManager, attackHeld);
            }

            this.wasAttackActive = attackHeld;
         }
      }
   }

   private void applyMovementInput(BotPlayer player) {
      player.input.keyPresses = new Input(this.forward, this.backward, this.left, this.right, this.jump, this.sneak, this.sprint);
      ViaCompat.method4848(player.input, movementMultiplier(this.forward, this.backward));
      ViaCompat.method4849(player.input, movementMultiplier(this.left, this.right));
   }

   private static void clearMovementInput(BotPlayer player) {
      player.input.keyPresses = Input.EMPTY;
      ViaCompat.method4848(player.input, 0.0F);
      ViaCompat.method4849(player.input, 0.0F);
   }

   private static float movementMultiplier(boolean forward, boolean backward) {
      if (forward == backward) {
         return 0.0F;
      } else {
         return forward ? 1.0F : -1.0F;
      }
   }

   private void doAttack(BotWorld world, BotPlayer player, BotInteractionManager interactionManager) {
      if (this.attackCooldown <= 0) {
         HitResult crosshairTarget = this.crosshairTarget;
         if (crosshairTarget != null && !player.isRiding()) {
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (itemInHand.isItemEnabled(world.enabledFeatures())) {
               switch (crosshairTarget.getType()) {
                  case ENTITY:
                     interactionManager.attackEntity(player, ((EntityHitResult)crosshairTarget).getEntity());
                     break;
                  case BLOCK:
                     BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
                     BlockPos blockPos = blockHitResult.getBlockPos();
                     if (!world.getBlockState(blockPos).isAir()) {
                        interactionManager.attackBlock(blockPos, blockHitResult.getDirection());
                     } else {
                        this.onAttackMiss(player, interactionManager);
                     }
                     break;
                  case MISS:
                     this.onAttackMiss(player, interactionManager);
               }

               player.swing(InteractionHand.MAIN_HAND);
            }
         }
      }
   }

   private void onAttackMiss(BotPlayer player, BotInteractionManager interactionManager) {
      if (interactionManager.getCurrentGameMode() != GameType.CREATIVE) {
         this.attackCooldown = 10;
      }

      player.resetOnlyAttackStrengthTicker();
   }

   private void doItemUse(BotWorld world, BotPlayer player, BotInteractionManager interactionManager) {
      if (!interactionManager.isBreakingBlock()) {
         this.itemUseCooldown = 4;
         if (!player.isRiding()) {
            HitResult crosshairTarget = this.crosshairTarget;

            for (InteractionHand hand : InteractionHand.values()) {
               ItemStack itemInHand = player.getItemInHand(hand);
               if (!itemInHand.isItemEnabled(world.enabledFeatures())) {
                  return;
               }

               if (crosshairTarget != null) {
                  switch (crosshairTarget.getType()) {
                     case ENTITY:
                        EntityHitResult entityHitResult = (EntityHitResult)crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if (!world.getWorldBorder().isWithinBounds(entity.blockPosition())) {
                           return;
                        }

                        InteractionResult interactEntityAtLocation = interactionManager.interactEntityAtLocation(player, entity, entityHitResult, hand);
                        if (!interactEntityAtLocation.consumesAction()) {
                           interactEntityAtLocation = interactionManager.interactEntity(player, entity, hand);
                        }

                        if (interactEntityAtLocation instanceof Success success) {
                           if (success.swingSource() == SwingSource.PREDICTED) {
                              player.swing(hand);
                           }

                           return;
                        }
                        break;
                     case BLOCK:
                        BlockHitResult hitResult = (BlockHitResult)crosshairTarget;
                        InteractionResult interactBlock = interactionManager.interactBlock(player, hand, hitResult);
                        if (interactBlock instanceof Success success2) {
                           if (success2.swingSource() == SwingSource.PREDICTED) {
                              player.swing(hand);
                           }

                           return;
                        }

                        if (interactBlock instanceof Fail) {
                           return;
                        }
                  }
               }

               if (!itemInHand.isEmpty() && interactionManager.interactItem(player, hand) instanceof Success success3) {
                  if (success3.swingSource() == SwingSource.PREDICTED) {
                     player.swing(hand);
                  }

                  return;
               }
            }
         }
      }
   }

   private void handleBlockBreaking(BotWorld world, BotPlayer player, BotInteractionManager interactionManager, boolean attackHeld) {
      if (!attackHeld) {
         this.attackCooldown = 0;
      }

      if (this.attackCooldown <= 0 && !player.isUsingItem()) {
         HitResult crosshairTarget = this.crosshairTarget;
         if (attackHeld && crosshairTarget != null && crosshairTarget.getType() == Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!world.getBlockState(blockPos).isAir() && interactionManager.updateBlockBreakingProgress(blockPos, blockHitResult.getDirection())) {
               player.swing(InteractionHand.MAIN_HAND);
            }
         } else {
            interactionManager.cancelBlockBreaking();
         }
      }
   }

   private static HitResult findCrosshairTarget(BotPlayer player) {
      double blockInteractionRange = player.blockInteractionRange();
      double entityInteractionRange = player.entityInteractionRange();
      double x = Math.max(blockInteractionRange, entityInteractionRange);
      double square = Mth.square(x);
      Vec3 eyePosition = player.getEyePosition(1.0F);
      HitResult pick = player.pick(x, 1.0F, false);
      double d = pick.getLocation().distanceToSqr(eyePosition);
      if (pick.getType() != Type.MISS) {
         square = d;
         x = Math.sqrt(d);
      }

      Vec3 viewVector = player.getViewVector(1.0F);
      Vec3 to = eyePosition.add(viewVector.x * x, viewVector.y * x, viewVector.z * x);
      AABB inflate = player.getBoundingBox().expandTowards(viewVector.scale(x)).inflate(1.0, 1.0, 1.0);
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(player, eyePosition, to, inflate, EntitySelector.CAN_BE_PICKED, square);
      return entityHitResult != null && entityHitResult.getLocation().distanceToSqr(eyePosition) < d ? ensureTargetInRange(entityHitResult, eyePosition, entityInteractionRange) : ensureTargetInRange(pick, eyePosition, blockInteractionRange);
   }

   private static HitResult ensureTargetInRange(HitResult entityHitResult, Vec3 eyePosition, double entityInteractionRange) {
      Vec3 location = entityHitResult.getLocation();
      if (!location.closerThan(eyePosition, entityInteractionRange)) {
         Direction approximateNearest = Direction.getApproximateNearest(location.x - eyePosition.x, location.y - eyePosition.y, location.z - eyePosition.z);
         return BlockHitResult.miss(location, approximateNearest, BlockPos.containing(location));
      } else {
         return entityHitResult;
      }
   }
}

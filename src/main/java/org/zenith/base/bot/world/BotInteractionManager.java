package org.zenith.base.bot.world;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.TryEmptyHandInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.net.BotPlayHandler;

public final class BotInteractionManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BotPlayHandler networkHandler;
   private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
   private ItemStack selectedStack = ItemStack.EMPTY;
   public float currentBreakingProgress;
   public int blockBreakingCooldown;
   private boolean breakingBlock;
   private GameType gameMode = GameType.DEFAULT_MODE;
   @Nullable
   private GameType previousGameMode;
   private int lastSelectedSlot;

   public BotInteractionManager(BotPlayHandler networkHandler) {
      this.networkHandler = networkHandler;
   }

   @Nullable
   private BotPlayer player() {
      return this.networkHandler.getPlayer();
   }

   @Nullable
   private BotWorld world() {
      return this.networkHandler.getWorld();
   }

   public void copyAbilities(Player botPlayer) {
      this.gameMode.updatePlayerAbilities(botPlayer.getAbilities());
   }

   public void setGameModes(GameType gameMode, @Nullable GameType previousGameMode) {
      this.gameMode = gameMode;
      this.previousGameMode = previousGameMode;
      BotPlayer player = this.player();
      if (player != null) {
         this.gameMode.updatePlayerAbilities(player.getAbilities());
      }
   }

   public void setGameMode(GameType gameMode) {
      if (gameMode != this.gameMode) {
         this.previousGameMode = this.gameMode;
      }

      this.gameMode = gameMode;
      BotPlayer player = this.player();
      if (player != null) {
         this.gameMode.updatePlayerAbilities(player.getAbilities());
      }
   }

   public boolean breakBlock(BlockPos currentBreakingPos) {
      BotPlayer player = this.player();
      BotWorld world = this.world();
      if (player == null || world == null) {
         return false;
      } else if (player.blockActionRestricted(world, currentBreakingPos, this.gameMode)) {
         return false;
      } else {
         BlockState blockState = world.getBlockState(currentBreakingPos);
         if (!player.getMainHandItem().getItem().canDestroyBlock(player.getMainHandItem(), blockState, world, currentBreakingPos, player)) {
            return false;
         } else {
            Block block = blockState.getBlock();
            if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
               return false;
            } else if (blockState.isAir()) {
               return false;
            } else {
               block.playerWillDestroy(world, currentBreakingPos, blockState, player);
               FluidState fluidState = world.getFluidState(currentBreakingPos);
               boolean setBlock = world.setBlock(currentBreakingPos, fluidState.createLegacyBlock(), 11);
               if (setBlock) {
                  block.destroy(world, currentBreakingPos, blockState);
               }

               return setBlock;
            }
         }
      }
   }

   public boolean attackBlock(BlockPos currentBreakingPos, Direction direction) {
      BotPlayer player = this.player();
      BotWorld world = this.world();
      if (player == null || world == null) {
         return false;
      } else if (player.blockActionRestricted(world, currentBreakingPos, this.gameMode)) {
         return false;
      } else if (!world.getWorldBorder().isWithinBounds(currentBreakingPos)) {
         return false;
      } else {
         if (this.gameMode.isCreative()) {
            this.sendSequencedPacket(world, var3x -> {
               this.breakBlock(currentBreakingPos);
               return new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, currentBreakingPos, direction, var3x);
            });
            this.blockBreakingCooldown = 5;
         } else if (!this.breakingBlock || !this.isCurrentlyBreaking(currentBreakingPos)) {
            if (this.breakingBlock) {
               this.networkHandler.sendPacket(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, direction));
            }

            BlockState blockState = world.getBlockState(currentBreakingPos);
            this.sendSequencedPacket(world, sequence -> {
               boolean flag = !blockState.isAir();
               if (flag && this.currentBreakingProgress == 0.0F) {
                  blockState.attack(world, currentBreakingPos, player);
               }

               if (flag && blockState.getDestroyProgress(player, player.level(), currentBreakingPos) >= 1.0F) {
                  this.breakBlock(currentBreakingPos);
               } else {
                  this.breakingBlock = true;
                  this.currentBreakingPos = currentBreakingPos;
                  this.selectedStack = player.getMainHandItem();
                  this.currentBreakingProgress = 0.0F;
               }

               return new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, currentBreakingPos, direction, sequence);
            });
         }

         return true;
      }
   }

   public void cancelBlockBreaking() {
      if (this.breakingBlock) {
         this.networkHandler.sendPacket(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN));
         this.breakingBlock = false;
         this.currentBreakingProgress = 0.0F;
         BotPlayer player = this.player();
         if (player != null) {
            player.resetOnlyAttackStrengthTicker();
         }
      }
   }

   public boolean updateBlockBreakingProgress(BlockPos blockPos, Direction direction) {
      BotPlayer player = this.player();
      BotWorld world = this.world();
      if (player != null && world != null) {
         this.syncSelectedSlot();
         if (this.blockBreakingCooldown > 0) {
            this.blockBreakingCooldown--;
            return true;
         } else if (this.gameMode.isCreative() && world.getWorldBorder().isWithinBounds(blockPos)) {
            this.blockBreakingCooldown = 5;
            this.sendSequencedPacket(world, var3x -> {
               this.breakBlock(blockPos);
               return new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockPos, direction, var3x);
            });
            return true;
         } else if (this.isCurrentlyBreaking(blockPos)) {
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isAir()) {
               this.breakingBlock = false;
               return false;
            } else {
               this.currentBreakingProgress = this.currentBreakingProgress + blockState.getDestroyProgress(player, player.level(), blockPos);
               if (this.currentBreakingProgress >= 1.0F) {
                  this.breakingBlock = false;
                  this.sendSequencedPacket(world, var3x -> {
                     this.breakBlock(blockPos);
                     return new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, blockPos, direction, var3x);
                  });
                  this.currentBreakingProgress = 0.0F;
                  this.blockBreakingCooldown = 5;
               }

               return true;
            }
         } else {
            return this.attackBlock(blockPos, direction);
         }
      } else {
         return false;
      }
   }

   public void sendSequencedPacket(BotWorld world, BotInteractionManager.SequencedPacketCreator sequencedPacketCreator) {
      try (BotPendingUpdateManager incrementSequence = world.getPendingUpdateManager().incrementSequence()) {
         int sequence = incrementSequence.getSequence();
         Packet predict = sequencedPacketCreator.predict(sequence);
         this.networkHandler.sendPacket(predict);
      }
   }

   public void tick() {
      this.syncSelectedSlot();
   }

   private boolean isCurrentlyBreaking(BlockPos currentBreakingPos) {
      BotPlayer player = this.player();
      if (player == null) {
         return false;
      } else {
         ItemStack mainHandItem = player.getMainHandItem();
         return currentBreakingPos.equals(this.currentBreakingPos) && ItemStack.isSameItemSameComponents(mainHandItem, this.selectedStack);
      }
   }

   public void syncSelectedSlot() {
      BotPlayer player = this.player();
      if (player != null) {
         int selectedSlot = player.getInventory().getSelectedSlot();
         if (selectedSlot != this.lastSelectedSlot) {
            this.lastSelectedSlot = selectedSlot;
            this.networkHandler.sendPacket(new ServerboundSetCarriedItemPacket(this.lastSelectedSlot));
         }
      }
   }

   public InteractionResult interactBlock(BotPlayer player, InteractionHand hand, BlockHitResult hitResult) {
      this.syncSelectedSlot();
      BotWorld world = this.world();
      if (world != null && world.getWorldBorder().isWithinBounds(hitResult.getBlockPos())) {
         MutableObject mutableObject = new MutableObject();
         this.sendSequencedPacket(world, var5x -> {
            mutableObject.setValue(this.interactBlockInternal(player, hand, hitResult));
            return new ServerboundUseItemOnPacket(hand, hitResult, var5x);
         });
         return (InteractionResult)mutableObject.getValue();
      } else {
         return InteractionResult.FAIL;
      }
   }

   private InteractionResult interactBlockInternal(BotPlayer player, InteractionHand hand, BlockHitResult hitResult) {
      BotWorld nonNull = Objects.requireNonNull(this.world());
      BlockPos blockPos = hitResult.getBlockPos();
      ItemStack itemInHand = player.getItemInHand(hand);
      if (this.gameMode == GameType.SPECTATOR) {
         return InteractionResult.CONSUME;
      } else {
         boolean flag = !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
         boolean flag2 = player.isSecondaryUseActive() && flag;
         if (!flag2) {
            BlockState blockState = nonNull.getBlockState(blockPos);
            if (!blockState.getBlock().requiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
               return InteractionResult.FAIL;
            }

            InteractionResult useItemOn = blockState.useItemOn(player.getItemInHand(hand), nonNull, player, hand, hitResult);
            if (useItemOn.consumesAction()) {
               return useItemOn;
            }

            if (useItemOn instanceof TryEmptyHandInteraction && hand == InteractionHand.MAIN_HAND) {
               InteractionResult useWithoutItem = blockState.useWithoutItem(nonNull, player, hitResult);
               if (useWithoutItem.consumesAction()) {
                  return useWithoutItem;
               }
            }
         }

         if (!itemInHand.isEmpty() && !player.getCooldowns().isOnCooldown(itemInHand)) {
            UseOnContext useOnContext = new UseOnContext(player, hand, hitResult);
            InteractionResult useOn;
            if (this.gameMode.isCreative()) {
               int count = itemInHand.getCount();
               useOn = itemInHand.useOn(useOnContext);
               itemInHand.setCount(count);
            } else {
               useOn = itemInHand.useOn(useOnContext);
            }

            return useOn;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public InteractionResult interactItem(BotPlayer player, InteractionHand hand) {
      if (this.gameMode == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else {
         BotWorld world = this.world();
         if (world == null) {
            return InteractionResult.PASS;
         } else {
            this.syncSelectedSlot();
            MutableObject mutableObject = new MutableObject();
            this.sendSequencedPacket(world, var4x -> {
               ServerboundUseItemPacket useItemPacket = new ServerboundUseItemPacket(hand, var4x, player.getYRot(), player.getXRot());
               ItemStack itemInHand = player.getItemInHand(hand);
               if (player.getCooldowns().isOnCooldown(itemInHand)) {
                  mutableObject.setValue(InteractionResult.PASS);
                  return useItemPacket;
               } else {
                  InteractionResult use = itemInHand.use(world, player, hand);
                  ItemStack nonNullElseGet;
                  if (use instanceof Success success) {
                     nonNullElseGet = Objects.requireNonNullElseGet(success.heldItemTransformedTo(), () -> player.getItemInHand(hand));
                  } else {
                     nonNullElseGet = player.getItemInHand(hand);
                  }

                  if (nonNullElseGet != itemInHand) {
                     player.setItemInHand(hand, nonNullElseGet);
                  }

                  mutableObject.setValue(use);
                  return useItemPacket;
               }
            });
            return (InteractionResult)mutableObject.getValue();
         }
      }
   }

   public void attackEntity(BotPlayer player, Entity entity) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new net.minecraft.network.protocol.game.ServerboundAttackPacket(entity.getId()));
      if (this.gameMode != GameType.SPECTATOR) {
         player.attack(entity);
         player.resetOnlyAttackStrengthTicker();
      }
   }

   // 26.1 merged interact and interactAt: the packet always carries a location; vanilla sends the hit point,
   // without a hit result the entity origin (zero offset) is used
   public InteractionResult interactEntity(BotPlayer player, Entity entity, InteractionHand hand) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new ServerboundInteractPacket(entity.getId(), hand, Vec3.ZERO, player.isShiftKeyDown()));
      return (InteractionResult)(this.gameMode == GameType.SPECTATOR ? InteractionResult.PASS : player.interactOn(entity, hand, Vec3.ZERO));
   }

   public InteractionResult interactEntityAtLocation(BotPlayer player, Entity entity, EntityHitResult entityHitResult, InteractionHand hand) {
      this.syncSelectedSlot();
      Vec3 location = entityHitResult.getLocation().subtract(entity.getX(), entity.getY(), entity.getZ());
      this.networkHandler.sendPacket(new ServerboundInteractPacket(entity.getId(), hand, location, player.isShiftKeyDown()));
      return (InteractionResult)(this.gameMode == GameType.SPECTATOR ? InteractionResult.PASS : player.interactOn(entity, hand, location));
   }

   public void clickSlot(int containerId, int index, int buttonNum, ContainerInput containerInput, Player player) {
      AbstractContainerMenu containerMenu = player.containerMenu;
      if (containerId != containerMenu.containerId) {
         LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", containerId, containerMenu.containerId);
      } else {
         NonNullList slots = containerMenu.slots;
         int size = slots.size();
         var arrayListWithCapacity = Lists.newArrayListWithCapacity(size);

         for (Object slot : (Iterable<?>)(Object)(slots)) {
            Slot slot2 = (Slot)slot;
            arrayListWithCapacity.add(slot2.getItem().copy());
         }

         containerMenu.clicked(index, buttonNum, containerInput, player);
         Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();

         for (int i = 0; i < size; i++) {
            ItemStack stack = (ItemStack)arrayListWithCapacity.get(i);
            ItemStack itemStack = ((Slot)slots.get(i)).getItem();
            if (!ItemStack.matches(stack, itemStack)) {
               int2ObjectOpenHashMap.put(i, HashedStack.create(itemStack, this.networkHandler.getComponentHasher()));
            }
         }

         this.networkHandler
            .sendPacket(
               new ServerboundContainerClickPacket(
                  containerId,
                  containerMenu.getStateId(),
                  (short)index,
                  (byte)buttonNum,
                  containerInput,
                  int2ObjectOpenHashMap,
                  HashedStack.create(containerMenu.getCarried(), this.networkHandler.getComponentHasher())
               )
            );
      }
   }

   public void clickButton(int containerId, int buttonId) {
      this.networkHandler.sendPacket(new ServerboundContainerButtonClickPacket(containerId, buttonId));
   }

   public void clickCreativeStack(ItemStack itemStack, int slotNum) {
      if (this.gameMode.isCreative() && itemStack.getItem().requiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
         this.networkHandler.sendPacket(new ServerboundSetCreativeModeSlotPacket(slotNum, itemStack));
      }
   }

   public void stopUsingItem(Player player) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new ServerboundPlayerActionPacket(Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
      player.releaseUsingItem();
   }

   public boolean isFlyingLocked() {
      return this.gameMode == GameType.SPECTATOR;
   }

   @Nullable
   public GameType getPreviousGameMode() {
      return this.previousGameMode;
   }

   public GameType getCurrentGameMode() {
      return this.gameMode;
   }

   public boolean isBreakingBlock() {
      return this.breakingBlock;
   }

   public int getBlockBreakingProgress() {
      return this.currentBreakingProgress > 0.0F ? (int)(this.currentBreakingProgress * 10.0F) : -1;
   }

   @FunctionalInterface
   public interface SequencedPacketCreator {
      Packet<ServerGamePacketListener> predict(int sequence);
   }
}

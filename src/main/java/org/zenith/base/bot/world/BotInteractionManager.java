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

   public BotInteractionManager(BotPlayHandler var1) {
      this.networkHandler = var1;
   }

   @Nullable
   private BotPlayer player() {
      return this.networkHandler.getPlayer();
   }

   @Nullable
   private BotWorld world() {
      return this.networkHandler.getWorld();
   }

   public void copyAbilities(Player var1) {
      this.gameMode.updatePlayerAbilities(var1.getAbilities());
   }

   public void setGameModes(GameType var1, @Nullable GameType var2) {
      this.gameMode = var1;
      this.previousGameMode = var2;
      BotPlayer var3 = this.player();
      if (var3 != null) {
         this.gameMode.updatePlayerAbilities(var3.getAbilities());
      }
   }

   public void setGameMode(GameType var1) {
      if (var1 != this.gameMode) {
         this.previousGameMode = this.gameMode;
      }

      this.gameMode = var1;
      BotPlayer var2 = this.player();
      if (var2 != null) {
         this.gameMode.updatePlayerAbilities(var2.getAbilities());
      }
   }

   public boolean breakBlock(BlockPos var1) {
      BotPlayer var2 = this.player();
      BotWorld var3 = this.world();
      if (var2 == null || var3 == null) {
         return false;
      } else if (var2.blockActionRestricted(var3, var1, this.gameMode)) {
         return false;
      } else {
         BlockState var4 = var3.getBlockState(var1);
         if (!var2.getMainHandItem().getItem().canDestroyBlock(var2.getMainHandItem(), var4, var3, var1, var2)) {
            return false;
         } else {
            Block var5 = var4.getBlock();
            if (var5 instanceof GameMasterBlock && !var2.canUseGameMasterBlocks()) {
               return false;
            } else if (var4.isAir()) {
               return false;
            } else {
               var5.playerWillDestroy(var3, var1, var4, var2);
               FluidState var6 = var3.getFluidState(var1);
               boolean var7 = var3.setBlock(var1, var6.createLegacyBlock(), 11);
               if (var7) {
                  var5.destroy(var3, var1, var4);
               }

               return var7;
            }
         }
      }
   }

   public boolean attackBlock(BlockPos var1, Direction var2) {
      BotPlayer var3 = this.player();
      BotWorld var4 = this.world();
      if (var3 == null || var4 == null) {
         return false;
      } else if (var3.blockActionRestricted(var4, var1, this.gameMode)) {
         return false;
      } else if (!var4.getWorldBorder().isWithinBounds(var1)) {
         return false;
      } else {
         if (this.gameMode.isCreative()) {
            this.sendSequencedPacket(var4, var3x -> {
               this.breakBlock(var1);
               return new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, var1, var2, var3x);
            });
            this.blockBreakingCooldown = 5;
         } else if (!this.breakingBlock || !this.isCurrentlyBreaking(var1)) {
            if (this.breakingBlock) {
               this.networkHandler.sendPacket(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, var2));
            }

            BlockState var5 = var4.getBlockState(var1);
            this.sendSequencedPacket(var4, var6 -> {
               boolean var7 = !var5.isAir();
               if (var7 && this.currentBreakingProgress == 0.0F) {
                  var5.attack(var4, var1, var3);
               }

               if (var7 && var5.getDestroyProgress(var3, var3.level(), var1) >= 1.0F) {
                  this.breakBlock(var1);
               } else {
                  this.breakingBlock = true;
                  this.currentBreakingPos = var1;
                  this.selectedStack = var3.getMainHandItem();
                  this.currentBreakingProgress = 0.0F;
               }

               return new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, var1, var2, var6);
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
         BotPlayer var1 = this.player();
         if (var1 != null) {
            var1.resetOnlyAttackStrengthTicker();
         }
      }
   }

   public boolean updateBlockBreakingProgress(BlockPos var1, Direction var2) {
      BotPlayer var3 = this.player();
      BotWorld var4 = this.world();
      if (var3 != null && var4 != null) {
         this.syncSelectedSlot();
         if (this.blockBreakingCooldown > 0) {
            this.blockBreakingCooldown--;
            return true;
         } else if (this.gameMode.isCreative() && var4.getWorldBorder().isWithinBounds(var1)) {
            this.blockBreakingCooldown = 5;
            this.sendSequencedPacket(var4, var3x -> {
               this.breakBlock(var1);
               return new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, var1, var2, var3x);
            });
            return true;
         } else if (this.isCurrentlyBreaking(var1)) {
            BlockState var5 = var4.getBlockState(var1);
            if (var5.isAir()) {
               this.breakingBlock = false;
               return false;
            } else {
               this.currentBreakingProgress = this.currentBreakingProgress + var5.getDestroyProgress(var3, var3.level(), var1);
               if (this.currentBreakingProgress >= 1.0F) {
                  this.breakingBlock = false;
                  this.sendSequencedPacket(var4, var3x -> {
                     this.breakBlock(var1);
                     return new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, var1, var2, var3x);
                  });
                  this.currentBreakingProgress = 0.0F;
                  this.blockBreakingCooldown = 5;
               }

               return true;
            }
         } else {
            return this.attackBlock(var1, var2);
         }
      } else {
         return false;
      }
   }

   public void sendSequencedPacket(BotWorld var1, BotInteractionManager.SequencedPacketCreator var2) {
      try (BotPendingUpdateManager var3 = var1.getPendingUpdateManager().incrementSequence()) {
         int var4 = var3.getSequence();
         Packet var5 = var2.predict(var4);
         this.networkHandler.sendPacket(var5);
      }
   }

   public void tick() {
      this.syncSelectedSlot();
   }

   private boolean isCurrentlyBreaking(BlockPos var1) {
      BotPlayer var2 = this.player();
      if (var2 == null) {
         return false;
      } else {
         ItemStack var3 = var2.getMainHandItem();
         return var1.equals(this.currentBreakingPos) && ItemStack.isSameItemSameComponents(var3, this.selectedStack);
      }
   }

   public void syncSelectedSlot() {
      BotPlayer var1 = this.player();
      if (var1 != null) {
         int var2 = var1.getInventory().getSelectedSlot();
         if (var2 != this.lastSelectedSlot) {
            this.lastSelectedSlot = var2;
            this.networkHandler.sendPacket(new ServerboundSetCarriedItemPacket(this.lastSelectedSlot));
         }
      }
   }

   public InteractionResult interactBlock(BotPlayer var1, InteractionHand var2, BlockHitResult var3) {
      this.syncSelectedSlot();
      BotWorld var4 = this.world();
      if (var4 != null && var4.getWorldBorder().isWithinBounds(var3.getBlockPos())) {
         MutableObject var5 = new MutableObject();
         this.sendSequencedPacket(var4, var5x -> {
            var5.setValue(this.interactBlockInternal(var1, var2, var3));
            return new ServerboundUseItemOnPacket(var2, var3, var5x);
         });
         return (InteractionResult)var5.getValue();
      } else {
         return InteractionResult.FAIL;
      }
   }

   private InteractionResult interactBlockInternal(BotPlayer var1, InteractionHand var2, BlockHitResult var3) {
      BotWorld var4 = Objects.requireNonNull(this.world());
      BlockPos var5 = var3.getBlockPos();
      ItemStack var6 = var1.getItemInHand(var2);
      if (this.gameMode == GameType.SPECTATOR) {
         return InteractionResult.CONSUME;
      } else {
         boolean var7 = !var1.getMainHandItem().isEmpty() || !var1.getOffhandItem().isEmpty();
         boolean var8 = var1.isSecondaryUseActive() && var7;
         if (!var8) {
            BlockState var9 = var4.getBlockState(var5);
            if (!var9.getBlock().requiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
               return InteractionResult.FAIL;
            }

            InteractionResult var10 = var9.useItemOn(var1.getItemInHand(var2), var4, var1, var2, var3);
            if (var10.consumesAction()) {
               return var10;
            }

            if (var10 instanceof TryEmptyHandInteraction && var2 == InteractionHand.MAIN_HAND) {
               InteractionResult var11 = var9.useWithoutItem(var4, var1, var3);
               if (var11.consumesAction()) {
                  return var11;
               }
            }
         }

         if (!var6.isEmpty() && !var1.getCooldowns().isOnCooldown(var6)) {
            UseOnContext var12 = new UseOnContext(var1, var2, var3);
            InteractionResult var13;
            if (this.gameMode.isCreative()) {
               int var14 = var6.getCount();
               var13 = var6.useOn(var12);
               var6.setCount(var14);
            } else {
               var13 = var6.useOn(var12);
            }

            return var13;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public InteractionResult interactItem(BotPlayer var1, InteractionHand var2) {
      if (this.gameMode == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else {
         BotWorld var3 = this.world();
         if (var3 == null) {
            return InteractionResult.PASS;
         } else {
            this.syncSelectedSlot();
            MutableObject var4 = new MutableObject();
            this.sendSequencedPacket(var3, var4x -> {
               ServerboundUseItemPacket var5 = new ServerboundUseItemPacket(var2, var4x, var1.getYRot(), var1.getXRot());
               ItemStack var6 = var1.getItemInHand(var2);
               if (var1.getCooldowns().isOnCooldown(var6)) {
                  var4.setValue(InteractionResult.PASS);
                  return var5;
               } else {
                  InteractionResult var7 = var6.use(var3, var1, var2);
                  ItemStack var8;
                  if (var7 instanceof Success var9) {
                     var8 = Objects.requireNonNullElseGet(var9.heldItemTransformedTo(), () -> var1.getItemInHand(var2));
                  } else {
                     var8 = var1.getItemInHand(var2);
                  }

                  if (var8 != var6) {
                     var1.setItemInHand(var2, var8);
                  }

                  var4.setValue(var7);
                  return var5;
               }
            });
            return (InteractionResult)var4.getValue();
         }
      }
   }

   public void attackEntity(BotPlayer var1, Entity var2) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new net.minecraft.network.protocol.game.ServerboundAttackPacket(var2.getId()));
      if (this.gameMode != GameType.SPECTATOR) {
         var1.attack(var2);
         var1.resetOnlyAttackStrengthTicker();
      }
   }

   // 26.1 merged interact and interactAt: the packet always carries a location; vanilla sends the hit point,
   // without a hit result the entity origin (zero offset) is used
   public InteractionResult interactEntity(BotPlayer var1, Entity var2, InteractionHand var3) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new ServerboundInteractPacket(var2.getId(), var3, Vec3.ZERO, var1.isShiftKeyDown()));
      return (InteractionResult)(this.gameMode == GameType.SPECTATOR ? InteractionResult.PASS : var1.interactOn(var2, var3, Vec3.ZERO));
   }

   public InteractionResult interactEntityAtLocation(BotPlayer var1, Entity var2, EntityHitResult var3, InteractionHand var4) {
      this.syncSelectedSlot();
      Vec3 var5 = var3.getLocation().subtract(var2.getX(), var2.getY(), var2.getZ());
      this.networkHandler.sendPacket(new ServerboundInteractPacket(var2.getId(), var4, var5, var1.isShiftKeyDown()));
      return (InteractionResult)(this.gameMode == GameType.SPECTATOR ? InteractionResult.PASS : var1.interactOn(var2, var4, var5));
   }

   public void clickSlot(int var1, int var2, int var3, ContainerInput var4, Player var5) {
      AbstractContainerMenu var6 = var5.containerMenu;
      if (var1 != var6.containerId) {
         LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", var1, var6.containerId);
      } else {
         NonNullList var7 = var6.slots;
         int var8 = var7.size();
         var var9 = Lists.newArrayListWithCapacity(var8);

         for (Object var11_o : (Iterable<?>)(Object)(var7)) {
            Slot var11 = (Slot)var11_o;
            var9.add(var11.getItem().copy());
         }

         var6.clicked(var2, var3, var4, var5);
         Int2ObjectOpenHashMap var14 = new Int2ObjectOpenHashMap();

         for (int var15 = 0; var15 < var8; var15++) {
            ItemStack var12 = (ItemStack)var9.get(var15);
            ItemStack var13 = ((Slot)var7.get(var15)).getItem();
            if (!ItemStack.matches(var12, var13)) {
               var14.put(var15, HashedStack.create(var13, this.networkHandler.getComponentHasher()));
            }
         }

         this.networkHandler
            .sendPacket(
               new ServerboundContainerClickPacket(
                  var1,
                  var6.getStateId(),
                  (short)var2,
                  (byte)var3,
                  var4,
                  var14,
                  HashedStack.create(var6.getCarried(), this.networkHandler.getComponentHasher())
               )
            );
      }
   }

   public void clickButton(int var1, int var2) {
      this.networkHandler.sendPacket(new ServerboundContainerButtonClickPacket(var1, var2));
   }

   public void clickCreativeStack(ItemStack var1, int var2) {
      if (this.gameMode.isCreative() && var1.getItem().requiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
         this.networkHandler.sendPacket(new ServerboundSetCreativeModeSlotPacket(var2, var1));
      }
   }

   public void stopUsingItem(Player var1) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new ServerboundPlayerActionPacket(Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
      var1.releaseUsingItem();
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
      Packet<ServerGamePacketListener> predict(int var1);
   }
}

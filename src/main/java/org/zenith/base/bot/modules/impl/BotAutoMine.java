package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.SwingSource;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.bot.client.BotRctService;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotRaytracing;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.events.BotUpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.MineTargetFinder;
import org.zenith.utility.game.Pathfinder;
import org.zenith.utility.misc.ItemStackTags;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "BotAutoMine",
   category = ModuleCategory.PLAYER,
   description = "module.botautomine.description"
)
public class BotAutoMine extends BotModule {
   private static final int MINE_DATA_COOLDOWN_TICKS = 40;
   private static final Pathfinder.Data MOVE_PATH_SETTINGS = new Pathfinder.Data(30000, 192, 100, 130, false, true, Pathfinder.Type.CLOSEST_REACHABLE);
   private static final Set<Item> BUYER_ITEMS = Set.of(
      Items.REDSTONE,
      Items.QUARTZ,
      Items.DIAMOND,
      Items.ENDER_PEARL,
      Items.END_STONE,
      Items.LAPIS_LAZULI,
      Items.IRON_INGOT,
      Items.GOLD_INGOT,
      Items.GOLD_NUGGET,
      Items.OBSIDIAN,
      Items.NETHER_BRICK,
      Items.NETHER_BRICKS,
      Items.COAL
   );
   private final ModeSetting mineMode = new ModeSetting(
      "module.autoMine.mode", "module.autoMine.mode.desc", "module.autoMine.modeNormal", "module.autoMine.modeNether", "module.autoMine.modeCombined"
   );
   public static final int airBlocks = 600;
   private final Set<BlockPos> skippedBlocks = new HashSet<>();
   private MineTargetFinder.Data selectedMine;
   private BlockPos nextRouteBlock;
   private int mineDataCooldown;
   private boolean combinedMineSwitched;
   private BotWorld world;
   private BotPlayer player;
   private BotInteractionManager interaction;
   private Rotation currentRotation;
   private static final Comparator<BotAutoMine.BreakCandidate> BREAK_CANDIDATE_ORDER = Comparator.comparingInt(BotAutoMine.BreakCandidate::drillCoverage)
      .reversed()
      .thenComparingDouble(BotAutoMine.BreakCandidate::distanceSquared);

   @Override
   public void onEnable() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.skippedBlocks.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.bindContext();
      this.applyInput(false);
      super.onDisable();
   }

   @EventTarget
   public void onBotUpdate(BotUpdateEvent event) {
      this.world = event.getWorld();
      this.player = event.getPlayer();
      BotPlayHandler handler = this.handler();
      this.interaction = handler == null ? null : handler.getInteractionManager();
      if (this.world != null && this.player != null && this.interaction != null && handler != null) {
         this.currentRotation = new Rotation(this.player.getYRot(), this.player.getXRot());
         this.nextRouteBlock = null;
         var blockMines = this.onTeleportation();
         if (blockMines != null) {
            this.onRepair();
            if (!this.isEnabled()) {
               return;
            }

            this.onDrop();
            this.onBreak(blockMines);
            if (!this.onMove(blockMines)) {
               this.setRotation(this.currentRotation.add((float)MathUtils.method2651(-5.0, 5.0), (float)MathUtils.method2651(-5.0, 5.0)));
            }
         }

         this.applyInput((!this.rct().isActive()));
      }
   }

   private void bindContext() {
      this.world = this.world();
      this.player = this.player();
      this.interaction = this.interaction();
   }

   private boolean onMove(List<BotAutoMine.BlockMine> blockMines) {
      BlockPos bestMovingTarget = this.findBestMovingTarget(blockMines);
      if (bestMovingTarget == null) {
         return false;
      } else {
         Pathfinder.Entry2 entry2 = Pathfinder.method759(this.world, this.player.blockPosition(), bestMovingTarget, MOVE_PATH_SETTINGS).orElse(null);
         this.nextRouteBlock = entry2 == null ? (!this.player.onGround() ? bestMovingTarget : null) : entry2.method779(this.player.position());
         if (this.nextRouteBlock == null) {
            return false;
         } else {
            Vec3 eyePosition = this.player.getEyePosition(1.0F);
            Vec3 vec = Pathfinder.method761(this.nextRouteBlock);
            Rotation rotation = Rotation.lookingAt(new Vec3(vec.x, eyePosition.y, vec.z), eyePosition);
            Rotation currentRotation = this.currentRotation
               .add(this.currentRotation.wrappedDeltaTo(rotation))
               .add((float)MathUtils.method2651(-5.0, 5.0), (float)MathUtils.method2651(-5.0, 5.0));
            this.setRotation(currentRotation);
            return true;
         }
      }
   }

   private void applyInput(boolean forward) {
      if (this.player != null) {
         ViaCompat.method4848(this.player.input, forward ? 1.0F : 0.0F);
         ViaCompat.method4849(this.player.input, 0.0F);
         this.player.input.keyPresses = new Input(forward, false, false, false, false, false, forward);
      }
   }

   private void setRotation(Rotation currentRotation) {
      this.currentRotation = currentRotation;
      this.player.setYRot(Mth.wrapDegrees(currentRotation.getFloatValue()));
      this.player.setXRot(Mth.clamp(currentRotation.getFloatValue2(), -90.0F, 90.0F));
   }

   private void onDrop() {
      BotPlayHandler handler = this.handler();
      Component currentScreenTitle = handler.getCurrentScreenTitle();
      if (currentScreenTitle != null && currentScreenTitle.getString().toLowerCase(Locale.ROOT).contains("для продажи")) {
         if (this.player.containerMenu instanceof ChestMenu chestMenu) {
            int containerSlotCount;
            if (this.moveBuyerResources() && (containerSlotCount = this.getContainerSlotCount(chestMenu)) > 0) {
               this.clickSlot(containerSlotCount - 1, 0, ContainerInput.QUICK_MOVE);
            }

            Slot trashSlot = this.findTrashSlot();
            if (trashSlot != null) {
               this.clickSlot(trashSlot.index, 1, ContainerInput.THROW);
            }
         }
      } else {
         if (this.player.tickCount % 40 == 0) {
            handler.sendCommand("buyer");
         }
      }
   }

   private boolean moveBuyerResources() {
      byte b = 0;
      int n = 0;

      for (Slot slot : this.player.containerMenu.slots) {
         if (this.isPlayerInventorySlot(slot) && this.isBuyerResource(slot.getItem())) {
            if (n > 5) {
               break;
            }

            this.clickSlot(slot.index, 0, ContainerInput.QUICK_MOVE);
            n++;
            b = 1;
         }
      }

      return (b != 0);
   }

   private boolean isBuyerResource(ItemStack stack) {
      return (!stack.isEmpty() && BUYER_ITEMS.contains(stack.getItem()));
   }

   private Slot findTrashSlot() {
      return this.player
         .containerMenu
         .slots
         .stream()
         .filter(this::isPlayerInventorySlot)
         .filter(slot -> this.isTrash(slot.getItem()))
         .findFirst()
         .orElse(null);
   }

   private boolean isPlayerInventorySlot(Slot slot) {
      if (this.player.containerMenu instanceof ChestMenu chestMenu) {
         int containerSlotCount = this.getContainerSlotCount(chestMenu);
         return !(slot.container != this.player.getInventory() && (slot.index < containerSlotCount || slot.index >= containerSlotCount + 36));
      } else {
         return (slot.container == this.player.getInventory() && slot.index >= 9 && slot.index <= 44);
      }
   }

   private int getContainerSlotCount(ChestMenu chestMenu) {
      return Math.min(chestMenu.getContainer().getContainerSize(), Math.max(0, this.player.containerMenu.slots.size() - 36));
   }

   private boolean isTrash(ItemStack stack) {
      if (!stack.isEmpty() && !stack.is(Items.PLAYER_HEAD) && !stack.is(Items.TRIPWIRE_HOOK) && !this.isBuyerResource(stack)) {
         return (stack.getItem() instanceof BlockItem blockItem && !MineTargetFinder.method1934(blockItem.getBlock()));
      } else {
         return false;
      }
   }

   private void clickSlot(int index, int buttonNum, ContainerInput containerInput) {
      this.interaction.clickSlot(this.player.containerMenu.containerId, index, buttonNum, containerInput, this.player);
   }

   private void onBreak(List<BotAutoMine.BlockMine> blockMines) {
      ItemStack mainHandItem = this.player.getMainHandItem();
      if (ItemStackTags.isPickaxe(mainHandItem) && mainHandItem.getMaxDamage() - mainHandItem.getDamageValue() >= 30) {
         ArrayList arrayList = new ArrayList();

         for (BotAutoMine.BlockMine blockMine : blockMines) {
            if (this.isStaticMineable(blockMine)) {
               arrayList.add(blockMine.pos());
            }
         }

         this.skippedBlocks.clear();

         for (int i = 0; i < 6; i++) {
            BotAutoMine.BreakTarget bestBreakTarget = this.findBestBreakTarget(arrayList);
            if (bestBreakTarget == null) {
               break;
            }

            this.grimSuperBypass(0.0, bestBreakTarget.rotation());
            if (this.interaction.updateBlockBreakingProgress(bestBreakTarget.hitResult().getBlockPos(), bestBreakTarget.hitResult().getDirection())) {
               this.skipDrillArea(bestBreakTarget.hitResult().getBlockPos());
            }
         }
      }
   }

   private BotAutoMine.BreakTarget findBestBreakTarget(List<BlockPos> poses) {
      Vec3 eyePosition = this.player.getEyePosition(1.0F);
      double blockInteractionRange = this.player.blockInteractionRange();
      double d = 5.4;
      HashSet<BlockPos> poses2 = new HashSet();

      for (BlockPos pos : poses) {
         if (!this.skippedBlocks.contains(pos)) {
            poses2.add(pos);
         }
      }

      ArrayList<BotAutoMine.BreakCandidate> breakCandidates = new ArrayList();
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (BlockPos pos : poses2) {
         if (this.isWithinMineRange(pos, eyePosition, d, blockInteractionRange)) {
            breakCandidates.add(new BotAutoMine.BreakCandidate(pos, this.countDrillCoverage(pos, poses2, mutablePos), MineTargetFinder.method1939(eyePosition, pos)));
         }
      }

      breakCandidates.sort(BREAK_CANDIDATE_ORDER);

      for (BotAutoMine.BreakCandidate breakCandidate : breakCandidates) {
         BlockPos pos = breakCandidate.pos();
         Rotation rotation = this.currentRotation.add(this.currentRotation.wrappedDeltaTo(Rotation.lookingAt(net.minecraft.world.phys.Vec3.atCenterOf(pos), eyePosition)));
         BlockHitResult rayTracePos = BotRaytracing.rayTracePos(
            this.world, this.player, eyePosition, rotation, blockInteractionRange, var1x -> (var1x != null && pos.equals(var1x.getBlockPos()))
         );
         if (rayTracePos != null && rayTracePos.getType() != Type.MISS) {
            return new BotAutoMine.BreakTarget(rotation, rayTracePos, breakCandidate.drillCoverage(), breakCandidate.distanceSquared());
         }
      }

      return null;
   }

   private BlockPos findBestMovingTarget(List<BotAutoMine.BlockMine> blockMines) {
      Vec3 eyePosition = this.player.getEyePosition(1.0F);
      double blockInteractionRange = this.player.blockInteractionRange();
      BlockPos pos = null;
      double d = Double.MAX_VALUE;

      for (BotAutoMine.BlockMine blockMine : blockMines) {
         if (this.isMineable(blockMine) && !(Math.abs(blockMine.pos().getY() + 0.5 - eyePosition.y) > blockInteractionRange)) {
            double d2 = blockMine.pos().getX() + 0.5 - eyePosition.x;
            double d3 = blockMine.pos().getZ() + 0.5 - eyePosition.z;
            double d4;
            if ((d4 = d2 * d2 + d3 * d3) < d) {
               pos = blockMine.pos();
               d = d4;
            }
         }
      }

      return pos != null ? new BlockPos(pos.getX(), this.player.getBlockY(), pos.getZ()) : null;
   }

   private boolean isMineable(BotAutoMine.BlockMine blockMine) {
      return (!this.skippedBlocks.contains(blockMine.pos()) && this.isStaticMineable(blockMine));
   }

   private boolean isStaticMineable(BotAutoMine.BlockMine blockMine) {
      BlockState state = blockMine.state();
      return (!state.isAir() && state.getFluidState().isEmpty() && state.getDestroySpeed(this.world, blockMine.pos()) >= 0.0F && !state.is(Blocks.OBSIDIAN));
   }

   private boolean isWithinMineRange(BlockPos pos, Vec3 eyePosition, double d, double blockInteractionRange) {
      Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      return (Math.abs(atCenterOf.x - eyePosition.x) <= d && Math.abs(atCenterOf.y - eyePosition.y) <= blockInteractionRange && Math.abs(atCenterOf.z - eyePosition.z) <= d);
   }

   private int countDrillCoverage(BlockPos pos, Set<BlockPos> poses, MutableBlockPos mutablePos) {
      int n = 0;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
               if (poses.contains(mutablePos.setWithOffset(pos, i, j, k))) {
                  n++;
               }
            }
         }
      }

      return n;
   }

   private void skipDrillArea(BlockPos blockPos) {
      for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
         this.skippedBlocks.add(pos.immutable());
      }
   }

   private void grimSuperBypass(double d, Rotation rotation) {
      this.handler()
         .sendPacket(
            new PosRot(
               this.player.getX(),
               this.player.getY() + d,
               this.player.getZ(),
               rotation.getFloatValue(),
               rotation.getFloatValue2(),
               this.player.onGround(),
               this.player.horizontalCollision
            )
         );
   }

   private List<BotAutoMine.BlockMine> onTeleportation() {
      if (this.rct().isActive()) {
         return null;
      } else if (this.rct().currentAnarchyHere() == -1) {
         this.reconnectToRandomAnarchy();
         return null;
      } else if (this.isWaitingForMineData()) {
         return null;
      } else if (this.mineMode.method2101("module.autoMine.modeCombined")) {
         return this.selectCombinedMine();
      } else {
         this.combinedMineSwitched = false;
         this.selectedMine = null;
         MineTargetFinder.Data selectedMine = MineTargetFinder.method1933(this.mineMode.method2101("module.autoMine.modeNether"));
         if (MineTargetFinder.method1938(selectedMine, this.world) && !(this.getMineDistance(selectedMine) > 25.0)) {
            return this.getAvailableMine(selectedMine);
         } else {
            this.handleWarpTp();
            return null;
         }
      }
   }

   private List<BotAutoMine.BlockMine> selectCombinedMine() {
      MineTargetFinder.Data selectedMine = MineTargetFinder.method1933(false);
      MineTargetFinder.Data data = MineTargetFinder.method1933(true);
      if (MineTargetFinder.method1938(selectedMine, this.world) && MineTargetFinder.method1938(data, this.world)) {
         var arena = this.getArena(selectedMine);
         var blockMines = this.getArena(data);
         boolean mineAvailable = this.isMineAvailable(arena);
         boolean flag = this.isMineAvailable(blockMines);
         if (this.selectedMine != null) {
            if (this.getMineDistance(this.selectedMine) > 25.0) {
               this.handleWarpTp();
               return null;
            }

            boolean flag2;
            if ((flag2 = this.selectedMine.equals(data)) && flag) {
               return blockMines;
            }

            if (!flag2 && mineAvailable) {
               return arena;
            }

            if (this.combinedMineSwitched) {
               this.reconnectToRandomAnarchy();
               return null;
            }
         }

         if (!mineAvailable && !flag) {
            MineTargetFinder.Data selectedMine2 = this.selectedMine == null ? selectedMine : this.selectedMine;
            if (MineTargetFinder.method1938(selectedMine2, this.world) && !(this.getMineDistance(selectedMine2) > 25.0)) {
               this.reconnectToRandomAnarchy();
               return null;
            } else {
               this.handleWarpTp();
               return null;
            }
         } else {
            MineTargetFinder.Data selectedMine2 = this.selectedMine;
            int n = !flag || mineAvailable && !(this.getFillPercentage(blockMines) > this.getFillPercentage(arena)) ? 0 : 1;
            this.selectedMine = n != 0 ? data : selectedMine;
            if (!this.selectedMine.equals(selectedMine2)) {
               this.combinedMineSwitched = (selectedMine2 != null);
               this.sendWarpCommand();
               return null;
            } else {
               return n != 0 ? blockMines : arena;
            }
         }
      } else {
         this.handleWarpTp();
         return null;
      }
   }

   private boolean isWaitingForMineData() {
      if (this.player.tickCount >= 40 && this.mineDataCooldown <= 0) {
         return false;
      } else if (this.hasLoadedMineBlocks(this.getExpectedMineBounds())) {
         this.mineDataCooldown = 0;
         return false;
      } else {
         if (this.mineDataCooldown > 0) {
            this.mineDataCooldown--;
         }

         return true;
      }
   }

   private MineTargetFinder.Data getExpectedMineBounds() {
      return this.mineMode.method2101("module.autoMine.modeCombined") && this.selectedMine != null
         ? this.selectedMine
         : MineTargetFinder.method1933(this.mineMode.method2101("module.autoMine.modeNether"));
   }

   private boolean hasLoadedMineBlocks(MineTargetFinder.Data expectedMineBounds) {
      for (BlockPos pos : BlockPos.betweenClosed(
         expectedMineBounds.getIntValue(), expectedMineBounds.getIntValue3(), expectedMineBounds.getIntValue5(), expectedMineBounds.getIntValue2(), expectedMineBounds.getIntValue4(), expectedMineBounds.getIntValue6()
      )) {
         if (this.world.hasChunkAt(pos) && !this.world.getBlockState(pos).isAir()) {
            return true;
         }
      }

      return false;
   }

   private void displayMineDistance(MineTargetFinder.Data selectedMine) {
      this.bot().systemMessage(String.valueOf(this.getMineDistance(selectedMine)));
   }

   private double getMineDistance(MineTargetFinder.Data selectedMine) {
      double d = (selectedMine.getIntValue() + selectedMine.getIntValue2() + 1.0) * 0.5;
      double d2 = (selectedMine.getIntValue5() + selectedMine.getIntValue6() + 1.0) * 0.5;
      double x = Math.abs(this.player.getX() - d);
      double y = Math.abs(this.player.getZ() - d2);
      return Math.hypot(x, y);
   }

   private List<BotAutoMine.BlockMine> getAvailableMine(MineTargetFinder.Data selectedMine) {
      var arena = this.getArena(selectedMine);
      if (!this.isMineAvailable(arena)) {
         this.reconnectToRandomAnarchy();
         return null;
      } else {
         return arena;
      }
   }

   private boolean isMineAvailable(List<BotAutoMine.BlockMine> arena) {
      if (arena.size() - this.countAirBlocks(arena) < 600L) {
         return false;
      } else {
         return !(this.player.onGround() && !this.hasReachableBlockByHeight(arena));
      }
   }

   private boolean hasReachableBlockByHeight(List<BotAutoMine.BlockMine> arena) {
      int n = Mth.floor(this.player.getEyeY() + this.player.blockInteractionRange() - 1.0);
      return arena.stream().anyMatch(var1x -> (!var1x.state().isAir() && !var1x.state().is(Blocks.OBSIDIAN) && var1x.pos().getY() <= n));
   }

   private long countAirBlocks(List<BotAutoMine.BlockMine> arena) {
      return arena.stream().filter(blockMine -> blockMine.state().isAir()).count();
   }

   private double getFillPercentage(List<BotAutoMine.BlockMine> arena) {
      if (arena != null) {
         if (arena.isEmpty()) {
            return 1 != 0 ? 0.0 : 1.0 - (double)this.countAirBlocks(arena) / arena.size();
         }
      }

      return arena.isEmpty() ? 0.0 : 1.0 - (double)this.countAirBlocks(arena) / arena.size();
   }

   public void onRepair() {
      if (this.player.tickCount % 20 == 0) {
         ItemStack mainHandItem = this.player.getMainHandItem();
         if (ItemStackTags.isPickaxe(mainHandItem) && mainHandItem.getMaxDamage() - mainHandItem.getDamageValue() < 30) {
            if (this.player.getItemInHand(InteractionHand.OFF_HAND).is(Items.EXPERIENCE_BOTTLE)) {
               this.useItem(InteractionHand.OFF_HAND);
            } else {
               Slot slot = this.player
                  .containerMenu
                  .slots
                  .stream()
                  .filter(slot2 -> slot2.getItem().is(Items.EXPERIENCE_BOTTLE))
                  .max(Comparator.comparingInt(slot2 -> slot2.index))
                  .orElse(null);
               if (slot == null) {
                  this.bot().systemMessage("AutoMine: в хотбаре закончились пузырьки опыта");
                  this.toggle();
               } else if (slot.container instanceof Inventory || slot.container instanceof PlayerEnderChestContainer) {
                  this.clickSlot(slot.index, 40, ContainerInput.SWAP);
               }
            }
         }
      }
   }

   private void useItem(InteractionHand hand) {
      if (this.interaction.interactItem(this.player, hand) instanceof Success success && success.swingSource() == SwingSource.PREDICTED) {
         this.player.swing(hand);
      }
   }

   private List<BotAutoMine.BlockMine> getArena(MineTargetFinder.Data selectedMine) {
      return this.getBlocksInZone(
         new BlockPos(selectedMine.getIntValue(), selectedMine.getIntValue3(), selectedMine.getIntValue5()),
         new BlockPos(selectedMine.getIntValue2(), selectedMine.getIntValue4(), selectedMine.getIntValue6())
      );
   }

   private List<BotAutoMine.BlockMine> getBlocksInZone(BlockPos pos, BlockPos pos2) {
      ArrayList<BotAutoMine.BlockMine> blockMines = new ArrayList<>();
      int n = Math.min(pos.getX(), pos2.getX());
      int n2 = Math.max(pos.getX(), pos2.getX());
      int n3 = Math.min(pos.getY(), pos2.getY());
      int n4 = Math.max(pos.getY(), pos2.getY());
      int n5 = Math.min(pos.getZ(), pos2.getZ());
      int n6 = Math.max(pos.getZ(), pos2.getZ());

      for (int i = n; i <= n2; i++) {
         for (int j = n3; j <= n4; j++) {
            for (int k = n5; k <= n6; k++) {
               BlockPos pos3 = new BlockPos(i, j, k);
               blockMines.add(new BotAutoMine.BlockMine(pos3, this.world.getBlockState(pos3)));
            }
         }
      }

      return blockMines;
   }

   public void handleWarpTp() {
      if (this.player.tickCount % 60 == 0) {
         this.sendWarpCommand();
      }
   }

   private void sendWarpCommand() {
      boolean flag = this.mineMode.method2101("module.autoMine.modeCombined")
         ? MineTargetFinder.method1933(true).equals(this.selectedMine)
         : this.mineMode.method2101("module.autoMine.modeNether");
      this.handler().sendCommand(flag ? "warp mine2" : "warp mine");
      this.mineDataCooldown = 20;
   }

   private void reconnectToRandomAnarchy() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.skippedBlocks.clear();
      int currentAnarchyHere = this.rct().currentAnarchyHere();

      int nextInt;
      do {
         while ((nextInt = ThreadLocalRandom.current().nextInt(1, 64)) == currentAnarchyHere) {
         }
      } while (nextInt == 2 || nextInt == 17 || nextInt == 33 || nextInt == 49);

      this.rct().reconnect(nextInt);
   }

   private BotRctService rct() {
      return this.bot().getRct();
   }

   @Generated
   public ModeSetting getMineMode() {
      return this.mineMode;
   }

   private static final class BlockMine {
      private final BlockPos pos;
      private final BlockState state;

      public BlockPos pos() {
         return this.pos;
      }

      public BlockState state() {
         return this.state;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BlockMine other)) return false;
         return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.state, other.state);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(pos, state);
      }

      @Override
      public String toString() {
         return "BlockMine[" + "pos=" + this.pos + ", " + "state=" + this.state + "]";
      }

      private BlockMine(BlockPos pos, BlockState state) {
         this.pos = pos;
         this.state = state;
      }
   }

   private static final class BreakCandidate {
      private final BlockPos pos;
      private final int drillCoverage;
      private final double distanceSquared;

      public BlockPos pos() {
         return this.pos;
      }

      public int drillCoverage() {
         return this.drillCoverage;
      }

      public double distanceSquared() {
         return this.distanceSquared;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BreakCandidate other)) return false;
         return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.drillCoverage, other.drillCoverage) && java.util.Objects.equals(this.distanceSquared, other.distanceSquared);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(pos, drillCoverage, distanceSquared);
      }

      @Override
      public String toString() {
         return "BreakCandidate[" + "pos=" + this.pos + ", " + "drillCoverage=" + this.drillCoverage + ", " + "distanceSquared=" + this.distanceSquared + "]";
      }

      private BreakCandidate(BlockPos pos, int drillCoverage, double distanceSquared) {
         this.pos = pos;
         this.drillCoverage = drillCoverage;
         this.distanceSquared = distanceSquared;
      }
   }

   private static final class BreakTarget {
      private final Rotation rotation;
      private final BlockHitResult hitResult;
      private final int drillCoverage;
      private final double distanceSquared;

      public Rotation rotation() {
         return this.rotation;
      }

      public BlockHitResult hitResult() {
         return this.hitResult;
      }

      public int drillCoverage() {
         return this.drillCoverage;
      }

      public double distanceSquared() {
         return this.distanceSquared;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BreakTarget other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.hitResult, other.hitResult) && java.util.Objects.equals(this.drillCoverage, other.drillCoverage) && java.util.Objects.equals(this.distanceSquared, other.distanceSquared);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation, hitResult, drillCoverage, distanceSquared);
      }

      @Override
      public String toString() {
         return "BreakTarget[" + "rotation=" + this.rotation + ", " + "hitResult=" + this.hitResult + ", " + "drillCoverage=" + this.drillCoverage + ", " + "distanceSquared=" + this.distanceSquared + "]";
      }

      private BreakTarget(Rotation rotation, BlockHitResult hitResult, int drillCoverage, double distanceSquared) {
         this.rotation = rotation;
         this.hitResult = hitResult;
         this.drillCoverage = drillCoverage;
         this.distanceSquared = distanceSquared;
      }
   }
}

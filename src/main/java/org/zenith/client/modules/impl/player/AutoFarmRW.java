package org.zenith.client.modules.impl.player;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.Settings.Setting;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "AutoFarmRW",
   category = ModuleCategory.PLAYER,
   description = "module.autofarmrw.description"
)
public final class AutoFarmRW extends Module {
   public static final AutoFarmRW INSTANCE = new AutoFarmRW();
   private static final long CONST_longValue = 15000L;
   private static final long CONST_longValue2 = 10000L;
   private static final int CONST_intValue = 8;
   private static final int CONST_intValue2 = 8;
   private static final Set<Item> items = Set.of(
      Items.WHEAT, Items.BEETROOT, Items.CARROT, Items.POTATO, Items.PUMPKIN, Items.MELON, Items.MELON_SLICE, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS
   );
   private final SliderSetting sellInterval = new SliderSetting(
      "module.autoFarmRW.sellInterval", 180.0F, 30.0F, 600.0F, 15.0F, "module.autoFarmRW.sellInterval.desc", "с"
   );
   private final SliderSetting warpDelay = new SliderSetting("module.autoFarmRW.warpDelay", 8.0F, 3.0F, 30.0F, 1.0F, "module.autoFarmRW.warpDelay.desc", "с");
   private final SliderSetting radius = new SliderSetting("module.autoFarmRW.radius", 48.0F, 16.0F, 96.0F, 8.0F, "module.autoFarmRW.radius.desc", "");
   private final BooleanSetting sellWhenFull = new BooleanSetting("module.autoFarmRW.sellWhenFull", "module.autoFarmRW.sellWhenFull.desc", true);
   private final AutoFarmRW.HarvestProcess harvestProcess = new AutoFarmRW.HarvestProcess();
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private AutoFarmRW.Type type = AutoFarmRW.Type.READY;
   private volatile boolean flag;
   private Level world;
   private BlockPos center;

   private AutoFarmRW() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabled()) {
         if (mc.player != null && ServerHelperModule.INSTANCE.method5181()) {
            this.reset();
         } else {
            ChatLogger.method2021("AutoFarmRW работает только на ReallyWorld");
            this.setToggled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.harvestProcess.close();
      this.type = AutoFarmRW.Type.READY;
      this.flag = false;
      this.world = null;
      this.center = null;
   }

   private void method3881(String str) {
      ChatLogger.method2021("AutoFarmRW: " + str);
      this.setToggled(false);
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && this.type == AutoFarmRW.Type.WARP && event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.flag = true;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.gameMode != null && mc.getConnection() != null) {
         if (!mc.player.isAlive()) {
            this.method3881("сбор остановлен — игрок погиб");
         } else {
            switch (this.type) {
               case READY:
                  if (!method3884()) {
                     return;
                  }

                  this.flag = false;
                  this.stopWatch.reset();
                  this.type = AutoFarmRW.Type.WARP;
                  mc.player.connection.sendCommand("warp farm");
                  break;
               case WARP:
                  if (this.flag) {
                     this.stopWatch.reset();
                     this.type = AutoFarmRW.Type.LOADING;
                  } else if (this.stopWatch.method7546(15000L)) {
                     this.method3881("не дождался /warp farm — проверь доступ к варпу");
                  }
                  break;
               case LOADING:
                  if (!this.stopWatch.method7546((long)this.warpDelay.getValue3() * 1000L)) {
                     return;
                  }

                  this.center = mc.player.blockPosition();
                  this.world = mc.level;
                  if (!mc.level.isLoaded(this.center)) {
                     this.method3881("ферма не загрузилась — включи модуль ещё раз");
                     return;
                  }

                  this.stopWatch2.reset();
                  this.stopWatch3.reset();
                  this.type = AutoFarmRW.Type.FARM;
                  break;
               case FARM:
                  this.method3882();
            }
         }
      }
   }

   private void method3882() {
      if (mc.level != this.world) {
         this.method3881("сбор остановлен — мир сменился");
      } else {
         int n = (int)this.radius.getValue3();
         if (!method3886(this.center, mc.player.blockPosition(), n + 8)) {
            this.method3881("сбор остановлен — игрок ушёл с фермы");
         } else {
            if (this.stopWatch2.method7546((long)this.sellInterval.getValue3() * 1000L)) {
               this.method3883();
            } else if (this.sellWhenFull.isEnabled() && this.stopWatch3.method7546(10000L) && mc.player.getInventory().getFreeSlot() == -1) {
               this.method3883();
            }

            if (mc.isPaused() || !method3884()) {
               this.harvestProcess.pause();
            } else if (!this.harvestProcess.isActive()) {
               this.harvestProcess.start(this.center, n);
            }
         }
      }
   }

   private void method3883() {
      this.stopWatch2.reset();
      this.stopWatch3.reset();
      mc.player.connection.sendCommand("sellfarm");
   }

   private static boolean method3884() {
      return mc.gui.screen() == null
         && mc.gui.overlay() == null
         && mc.player.containerMenu == mc.player.inventoryMenu
         && mc.player.containerMenu.getCarried().isEmpty();
   }

   public static boolean method3885(BlockState blockState) {
      if (blockState.is(Blocks.PUMPKIN) || blockState.is(Blocks.MELON)) {
         return true;
      } else {
         return !blockState.is(Blocks.WHEAT) && !blockState.is(Blocks.CARROTS) && !blockState.is(Blocks.POTATOES) && !blockState.is(Blocks.BEETROOTS)
            ? false
            : blockState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(blockState);
      }
   }

   public static boolean method3886(BlockPos center, BlockPos blockPosition, int radius) {
      if (center != null && blockPosition != null && radius >= 0) {
         long l = (long)blockPosition.getX() - center.getX();
         long l2 = (long)blockPosition.getZ() - center.getZ();
         return l * l + l2 * l2 <= (long)radius * radius && Math.abs((long)blockPosition.getY() - center.getY()) <= 8L;
      } else {
         return false;
      }
   }

   public static boolean isHarvesting() {
      return INSTANCE.isEnabled() && INSTANCE.type == AutoFarmRW.Type.FARM && INSTANCE.harvestProcess.isHarvesting() && mc.gui.screen() == null;
   }

   private static final class HarvestProcess implements IBaritoneProcess {
      private static final int SCAN_PER_TICK = 4096;
      private static final int SCAN_HALF_HEIGHT = 6;
      private static final int TARGET_TIMEOUT_TICKS = 400;
      private static final int BLACKLIST_TICKS = 600;
      private static final int HARVEST_GRACE_TICKS = 6;
      private static final double DROP_PRIORITY_SQ = 36.0;
      private final Set<BlockPos> crops = new HashSet<>();
      private final Map<BlockPos, Integer> blacklist = new HashMap<>();
      private final List<Runnable> settingRestores = new ArrayList<>();
      private IBaritone baritone;
      private Level world;
      private BlockPos center;
      private int radius;
      private int scanCursor;
      private int ticks;
      private int targetSince;
      private int lastHarvestTick = 64536;
      private BlockPos cropTarget;
      private ItemEntity dropTarget;
      private boolean active;

      public void start(BlockPos center, int n) {
         if (MinecraftAccess.mc.player != null && MinecraftAccess.mc.level != null && center != null) {
            if (this.baritone == null) {
               this.baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
               this.baritone.getPathingControlManager().registerProcess(this);
            }

            this.baritone.getPathingBehavior().cancelEverything();
            if (this.settingRestores.isEmpty()) {
               this.configureSettings();
            }

            this.world = MinecraftAccess.mc.level;
            this.center = center.immutable();
            this.radius = Math.max(8, Math.min(96, n));
            this.scanCursor = 0;
            this.ticks = 0;
            this.lastHarvestTick = 64536;
            this.crops.clear();
            this.blacklist.clear();
            this.cropTarget = null;
            this.dropTarget = null;
            this.active = true;

            for (BlockPos pos : BlockPos.betweenClosed(this.center.offset(-6, -6, -6), this.center.offset(6, 6, 6))) {
               if (this.withinFarm(pos, this.radius) && this.world.isLoaded(pos) && AutoFarmRW.method3885(this.world.getBlockState(pos))) {
                  this.crops.add(pos.immutable());
               }
            }
         }
      }

      public void pause() {
         boolean flag = this.active
            || this.baritone != null && this.baritone.getPathingControlManager().mostRecentInControl().filter(var1x -> var1x == this).isPresent();
         this.active = false;
         this.cropTarget = null;
         this.dropTarget = null;
         if (this.baritone != null && flag) {
            this.baritone.getInputOverrideHandler().clearAllKeys();
            this.baritone.getPathingBehavior().cancelEverything();
         }
      }

      public void close() {
         this.pause();

         for (int i = this.settingRestores.size() - 1; i >= 0; i--) {
            this.settingRestores.get(i).run();
         }

         this.settingRestores.clear();
         this.crops.clear();
         this.blacklist.clear();
         this.world = null;
         this.center = null;
      }

      public boolean isHarvesting() {
         return this.active && this.ticks - this.lastHarvestTick <= 6;
      }

      private <T> void override(Setting<T> allowBreak, T value) {
         T value2 = allowBreak.value;
         this.settingRestores.add(() -> allowBreak.value = value2);
         allowBreak.value = value;
      }

      private void configureSettings() {
         Settings settings = BaritoneAPI.getSettings();
         this.override(settings.allowBreak, false);
         this.override(settings.allowPlace, false);
         this.override(settings.allowInventory, false);
         this.override(settings.autoTool, false);
         this.override(settings.assumeExternalAutoTool, true);
         this.override(settings.allowParkour, false);
         this.override(settings.allowParkourPlace, false);
         this.override(settings.allowSprint, false);
         this.override(settings.allowDownward, false);
         this.override(settings.maxFallHeightNoWater, 1);
         this.override(settings.allowWaterBucketFall, false);
         this.override(settings.allowVines, false);
         this.override(settings.walkWhileBreaking, false);
         this.override(settings.freeLook, true);
         this.override(settings.blockFreeLook, true);
         this.override(settings.antiCheatCompatibility, true);
         this.override(settings.disconnectOnArrival, false);
      }

      public PathingCommand onTick(boolean flag, boolean flag2) {
         this.ticks++;
         this.baritone.getInputOverrideHandler().clearAllKeys();
         if (MinecraftAccess.mc.player == null || MinecraftAccess.mc.level != this.world || !MinecraftAccess.mc.player.isAlive()) {
            this.onLostControl();
            return pauseCommand();
         } else if (MinecraftAccess.mc.gui.screen() == null && MinecraftAccess.mc.player.containerMenu == MinecraftAccess.mc.player.inventoryMenu
            )
          {
            if (!this.withinFarm(MinecraftAccess.mc.player.blockPosition(), this.radius + 8)) {
               this.onLostControl();
               return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
            } else {
               this.scan();
               if (this.ticks % 100 == 0) {
                  this.blacklist.values().removeIf(var1x -> var1x <= this.ticks);
               }

               if ((this.cropTarget != null || this.dropTarget != null) && (flag || this.ticks - this.targetSince > 400)) {
                  BlockPos cropTarget = this.cropTarget != null ? this.cropTarget : this.dropTarget.blockPosition();
                  this.blacklist.put(cropTarget.immutable(), this.ticks + 600);
                  this.cropTarget = null;
                  this.dropTarget = null;
               }

               if (this.cropTarget != null && (!this.world.isLoaded(this.cropTarget) || !AutoFarmRW.method3885(this.world.getBlockState(this.cropTarget)))) {
                  this.crops.remove(this.cropTarget);
                  this.cropTarget = null;
               }

               if (this.dropTarget != null
                  && (!this.dropTarget.isAlive() || !this.withinFarm(this.dropTarget.blockPosition(), this.radius) || !this.canFit(this.dropTarget.getItem()))) {
                  this.dropTarget = null;
               }

               if (this.cropTarget == null && this.dropTarget == null) {
                  this.chooseTarget();
               }

               if (this.dropTarget != null) {
                  return path(new GoalBlock(this.dropTarget.blockPosition()));
               } else if (this.cropTarget == null) {
                  return pauseCommand();
               } else {
                  var reachable = RotationUtils.reachable(
                     this.baritone.getPlayerContext(), this.cropTarget, MinecraftAccess.mc.player.blockInteractionRange()
                  );
                  if (reachable.isEmpty()) {
                     return path(new GoalGetToBlock(this.cropTarget));
                  } else {
                     this.baritone.getLookBehavior().updateTarget((Rotation)reachable.get(), true);
                     if (AutoFarmRW.method3885(this.world.getBlockState(this.cropTarget)) && this.baritone.getPlayerContext().isLookingAt(this.cropTarget)) {
                        this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
                        this.lastHarvestTick = this.ticks;
                     }

                     return pauseCommand();
                  }
               }
            }
         } else {
            return pauseCommand();
         }
      }

      private void chooseTarget() {
         double d = Double.MAX_VALUE;
         AABB inflate = new AABB(this.center).inflate(this.radius, 6.0, this.radius);

         for (ItemEntity dropTarget : this.world
            .getEntitiesOfClass(
               ItemEntity.class, inflate, var1x -> var1x.isAlive() && AutoFarmRW.items.contains(var1x.getItem().getItem()) && this.canFit(var1x.getItem())
            )) {
            if (this.withinFarm(dropTarget.blockPosition(), this.radius) && !this.isBlacklisted(dropTarget.blockPosition())) {
               double d2 = MinecraftAccess.mc.player.distanceToSqr(dropTarget);
               if (d2 < d) {
                  d = d2;
                  this.dropTarget = dropTarget;
               }
            }
         }

         double d2 = Double.MAX_VALUE;
         var iterator = this.crops.iterator();

         while (iterator.hasNext()) {
            BlockPos cropTarget = (BlockPos)iterator.next();
            if (!this.world.isLoaded(cropTarget) || !AutoFarmRW.method3885(this.world.getBlockState(cropTarget))) {
               iterator.remove();
            } else if (!this.isBlacklisted(cropTarget)) {
               double d3 = MinecraftAccess.mc.player.distanceToSqr(cropTarget.getX() + 0.5, cropTarget.getY() + 0.5, cropTarget.getZ() + 0.5);
               if (d3 < d2) {
                  d2 = d3;
                  this.cropTarget = cropTarget;
               }
            }
         }

         if (this.dropTarget == null || !(d <= 36.0) && !(d <= d2)) {
            this.dropTarget = null;
         } else {
            this.cropTarget = null;
         }

         this.targetSince = this.ticks;
      }

      private void scan() {
         int n = this.radius * 2 + 1;
         byte b = 13;
         int n2 = n * n * b;
         MutableBlockPos mutablePos = new MutableBlockPos();

         for (int i = 0; i < 4096; i++) {
            int scanCursor = this.scanCursor;
            this.scanCursor = (this.scanCursor + 1) % n2;
            int n3 = scanCursor / (n * n);
            int n4 = scanCursor % (n * n);
            mutablePos.set(this.center.getX() + n4 % n - this.radius, this.center.getY() + n3 - 6, this.center.getZ() + n4 / n - this.radius);
            if (this.withinFarm(mutablePos, this.radius) && this.world.isLoaded(mutablePos)) {
               if (AutoFarmRW.method3885(this.world.getBlockState(mutablePos))) {
                  this.crops.add(mutablePos.immutable());
               } else {
                  this.crops.remove(mutablePos);
               }
            }
         }
      }

      private boolean canFit(ItemStack stack) {
         for (int i = 0; i < 36; i++) {
            ItemStack stack2 = MinecraftAccess.mc.player.getInventory().getItem(i);
            if (stack2.isEmpty() || ItemStack.isSameItemSameComponents(stack2, stack) && stack2.getCount() < stack2.getMaxStackSize()) {
               return true;
            }
         }

         return false;
      }

      private boolean withinFarm(BlockPos blockPosition, int radius) {
         return AutoFarmRW.method3886(this.center, blockPosition, radius);
      }

      private boolean isBlacklisted(BlockPos blockPosition) {
         return this.blacklist.getOrDefault(blockPosition, 0) > this.ticks;
      }

      private static PathingCommand path(Goal goalBlock) {
         return new PathingCommand(goalBlock, PathingCommandType.REVALIDATE_GOAL_AND_PATH);
      }

      private static PathingCommand pauseCommand() {
         return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
      }

      public boolean isActive() {
         return this.active;
      }

      public boolean isTemporary() {
         return false;
      }

      public void onLostControl() {
         boolean active = this.active;
         this.active = false;
         this.cropTarget = null;
         this.dropTarget = null;
         if (this.baritone != null && active) {
            this.baritone.getInputOverrideHandler().clearAllKeys();
         }
      }

      public String displayName0() {
         return "AutoFarmRW";
      }
   }

   private static enum Type {
      READY,
      WARP,
      LOADING,
      FARM;
   }
}

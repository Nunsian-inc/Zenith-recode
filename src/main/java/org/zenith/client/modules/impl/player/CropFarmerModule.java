package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "CropFarmer",
   category = ModuleCategory.PLAYER,
   description = "module.milanafarm.description"
)
public final class CropFarmerModule extends Module {
   public static final CropFarmerModule INSTANCE = new CropFarmerModule();
   private static final long CONST_longValue = 1000000000L;
   private final ModeSetting crop;
   private final BooleanSetting renderUnsuitableBeds;
   private final BooleanSetting renderLowLightBeds;
   private final BooleanSetting renderWaterPositions;
   private final BooleanSetting x;
   private final StopWatch stopWatch;
   private final StopWatch stopWatch2;
   private final StopWatch stopWatch3;
   private final Set<Integer> items;
   private final Map<BlockPos, CropFarmerModule.Entry> items2;
   private List<BlockPos> items3;
   private boolean flag;
   private List<BlockPos> items4;
   private BitSet bitSet;
   private List<BlockPos> items5;
   private String text;
   private CropFarmerModule.Type type;
   private static final int CONST_intValue = 4194304;
   private byte[] byteValues;
   private boolean flag2;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private BlockHitResult blockHitResult;
   private static final Comparator<BlockPos> comparator = Comparator.<BlockPos>comparingInt(Vec3i::getX).thenComparingInt(Vec3i::getZ).thenComparingInt(Vec3i::getY);

   private CropFarmerModule() {
      this.crop = new ModeSetting(
         "module.cropFarmer.crop",
         "module.cropFarmer.crop.desc",
         CropFarmerModule.Type.NETHER_WART.text,
         CropFarmerModule.Type.CARROT.text,
         CropFarmerModule.Type.POTATO.text,
         CropFarmerModule.Type.BEETROOT.text,
         CropFarmerModule.Type.WHEAT.text,
         CropFarmerModule.Type.TORCHFLOWER.text,
         CropFarmerModule.Type.PITCHER_PLANT.text
      );
      this.renderUnsuitableBeds = new BooleanSetting(
         "module.cropFarmer.renderUnsuitableBeds", "module.cropFarmer.renderUnsuitableBeds.desc", false, this::method4429
      );
      this.renderLowLightBeds = new BooleanSetting(
         "module.cropFarmer.renderLowLightBeds", "module.cropFarmer.renderLowLightBeds.desc", false, () -> this.method4430().intValue > 0
      );
      this.renderWaterPositions = new BooleanSetting(
         "module.cropFarmer.renderWaterPositions", "module.cropFarmer.renderWaterPositions.desc", false, this::method4429
      );
      this.x = new BooleanSetting("1.20.x", "module.cropFarmer.legacyBypass.desc", false);
      this.stopWatch = new StopWatch();
      this.stopWatch2 = new StopWatch();
      this.stopWatch3 = new StopWatch();
      this.items = new HashSet<>();
      this.items2 = new HashMap<>();
      this.items3 = Collections.emptyList();
      this.items5 = Collections.emptyList();
      this.byteValues = new byte[0];
      this.blockHitResult = null;
   }

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.items2.clear();
      this.blockHitResult = null;
      this.items3 = Collections.emptyList();
      this.flag = false;
      this.items4 = null;
      this.bitSet = null;
      this.items5 = Collections.emptyList();
   }

   @EventTarget
   public void method4390(WorldRenderEvent event) {
      label60: {
         Item item = this.method4402();
         if (mc.player.inventoryMenu.slots.stream().filter(var1x -> var1x.index != 45 && var1x.getItem().getItem() == item).count() < 20L) {
            if (!(mc.gui.screen() instanceof ContainerScreen containerScreen) || !containerScreen.getTitle().getString().contains("Скупщик")) {
               break label60;
            }

            AbstractContainerMenu containerMenu = mc.player.containerMenu;
            if (!(containerMenu instanceof ChestMenu)) {
               break label60;
            }
         }

         if (!this.stopWatch2.method7546(30L)) {
            return;
         }

         this.stopWatch2.reset();
         if (!(mc.gui.screen() instanceof ContainerScreen) && this.stopWatch.method7546(3000L)) {
            mc.getConnection().sendCommand("buyer");
            this.stopWatch.reset();
         } else if (mc.gui.screen() instanceof ContainerScreen containerScreen2
            && containerScreen2.getTitle().getString().contains("Скупщик")
            && mc.player.containerMenu instanceof ChestMenu chestMenu) {
            for (Slot slot : mc.player.containerMenu.slots) {
               if (!this.items.contains(slot.index) && slot.index >= chestMenu.getContainer().getContainerSize() && slot.getItem().getItem() == item) {
                  this.items.add(slot.index);
                  InventoryUtils.method903(slot, 0, ContainerInput.QUICK_MOVE, false);
                  return;
               }
            }

            InventoryUtils.method904(53, 0, ContainerInput.QUICK_MOVE, false);
            mc.player.closeContainer();
         }

         return;
      }

      this.items.clear();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @EventTarget
   public void method4391(WorldRenderEvent event) {
      if (this.renderUnsuitableBeds.isEnabled() && this.method4429()) {
         BlockPos blockPos = Zenith.getInstance().getTimedBlockPos().getBlockPos();
         BlockPos blockPos2 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
         if (blockPos != null && blockPos2 != null) {
            int rgb = Color.RED.getRGB();
            this.method4423(blockPos, blockPos2);

            try {
               method4426(blockPos, blockPos2, var2x -> {
                  if (this.method4417(var2x) && !this.method4418(var2x)) {
                     GlowUtil.method2230(new AABB(var2x), rgb, 1.0F);
                  }
               });
            } catch (Throwable e) {
               this.method4424();
               throw e;
            }

            this.method4424();
         }
      }
   }

   @EventTarget
   public void method4392(WorldRenderEvent event) {
      if (this.renderLowLightBeds.isEnabled() && this.method4430().intValue != 0) {
         BlockPos blockPos = Zenith.getInstance().getTimedBlockPos().getBlockPos();
         BlockPos blockPos2 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
         if (blockPos != null && blockPos2 != null) {
            int rgb = Color.YELLOW.getRGB();
            method4426(blockPos, blockPos2, var2x -> {
               if (this.method4417(var2x) && !this.method4419(var2x)) {
                  GlowUtil.method2230(new AABB(var2x), rgb, 1.0F);
               }
            });
         }
      }
   }

   @EventTarget
   public void method4393(WorldRenderEvent event) {
      if (this.renderWaterPositions.isEnabled() && this.method4429()) {
         BlockPos blockPos = Zenith.getInstance().getTimedBlockPos().getBlockPos();
         BlockPos blockPos2 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
         if (blockPos != null && blockPos2 != null) {
            if (!this.flag || this.stopWatch3.method7544(1000L)) {
               this.items3 = this.method4420(blockPos, blockPos2);
               this.flag = true;
            }

            this.items3.forEach(pos -> GlowUtil.method2230(new AABB(pos), new Color(0, 120, 255).getRGB(), 1.0F));
         }
      }
   }

   @EventTarget
   public void method4394(PreRotationEvent event) {
      if (Zenith.getInstance().getTimedBlockPos().getBlockPos() == null) {
         if (mc.player.tickCount % 80 == 0) {
            Zenith.getInstance().getHudHolder().method6973("4", Component.nullToEmpty("Отметьте точку .region pos1"), 4000L);
         }
      } else if (Zenith.getInstance().getTimedBlockPos().getBlockPos2() == null) {
         if (mc.player.tickCount % 80 == 0) {
            Zenith.getInstance()
               .getHudHolder()
               .method6973(
                  "4",
                  Component.nullToEmpty("Отметьте точку .region ")
                     .copy()
                     .append(
                        Component.nullToEmpty("pos2")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                     ),
                  4000L
               );
         }
      } else if (!this.method4413()) {
         Slot slot = InventoryUtils.method908(mc.player.inventoryMenu, this.method4401());
         if (!this.method4428() && slot != null && KeyBindUtils.method2630(AutoTotemModule.class) && KeyBindUtils.method2630(CropFarmerModule.class)) {
            KeyBindUtils.method2627(CropFarmerModule.class, () -> {
               if (!(mc.player.containerMenu instanceof InventoryMenu)) {
                  InventoryUtils.closeScreen();
               }

               InventoryUtils.method900(slot, InteractionHand.OFF_HAND, true);
            });
         } else {
            Rotation rotation = this.method4405();
            if (rotation != null) {
               if (this.x.isEnabled()) {
                  this.method4396();
               } else {
                  Rotation rotation2 = this.method4400(rotation);
                  Zenith.getInstance()
                     .getRotationManager()
                     .requestRotation(
                        new RotationRequest(
                           rotation2, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation2), bcCameraTestnerv.getRotationConfig3()
                        ),
                        7,
                        this
                     );
               }
            }
         }
      }
   }

   @EventTarget
   public void method4395(UpdateEvent event) {
      if (!this.method4413()) {
         if (!this.x.isEnabled()) {
            BlockHitResult blockHitResult = this.method4407(zenith.getRotationManager().getEffectiveRotation());
            if (blockHitResult != null) {
               if (this.blockHitResult != null && this.blockHitResult.getBlockPos().equals(blockHitResult.getBlockPos())) {
                  this.blockHitResult = blockHitResult;
                  BlockState blockState = mc.level.getBlockState(blockHitResult.getBlockPos());
                  this.method4409(blockHitResult, blockState);
               } else {
                  this.blockHitResult = blockHitResult;
               }
            }
         }
      }
   }

   public void method4396() {
      this.method4397();
      Rotation rotation = null;
      var emptySet = new HashSet<>(this.items2.keySet());

      for (int i = 0; i < 4; i++) {
         Rotation rotation2 = this.method4406(emptySet);
         if (rotation2 == null) {
            break;
         }

         BlockHitResult blockHitResult = this.method4408(rotation2, emptySet);
         if (blockHitResult == null) {
            rotation = rotation2;
            break;
         }

         BlockPos blockPos = blockHitResult.getBlockPos();
         emptySet.add(blockPos);
         BlockState blockState = mc.level.getBlockState(blockPos);
         CropFarmerModule.Type2 type2 = this.method4399(blockPos, blockState);
         if (type2 != null) {
            UseItemUtils.method6533(0.0, rotation2);
            this.method4410(blockHitResult, blockState);
            this.items2.put(blockPos, new CropFarmerModule.Entry(type2, System.nanoTime()));
         }

         rotation = rotation2;
      }

      Rotation rotation2 = this.method4406(emptySet);
      Rotation rotation3 = rotation2 != null ? rotation2 : (rotation == null ? zenith.getRotationManager().getEffectiveRotation() : rotation);
      Rotation rotation4 = this.method4400(rotation3).add((float)MathUtils.method2651(-5.0, 5.0), (float)MathUtils.method2651(-5.0, 5.0));
      Zenith.getInstance()
         .getRotationManager()
         .requestRotation(
            new RotationRequest(rotation4, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation4), bcCameraTestnerv.getRotationConfig()),
            7,
            this
         );
   }

   private void method4397() {
      long nanoTime = System.nanoTime();
      this.items2.entrySet().removeIf(entry -> nanoTime - entry.getValue().longValue >= 1000000000L || this.method4398(entry.getKey(), entry.getValue().type2));
   }

   private boolean method4398(BlockPos key, CropFarmerModule.Type2 type2) {
      BlockState blockState = mc.level.getBlockState(key);
      BlockState blockState2 = mc.level.getBlockState(key.above());

      return switch (type2) {
         case BREAK -> !this.method4404(blockState);
         case TILL -> !this.method4415(blockState, blockState2);
         case PLACE -> !blockState2.isAir();
      };
   }

   private CropFarmerModule.Type2 method4399(BlockPos blockPos, BlockState blockState) {
      if (this.method4404(blockState)) {
         return CropFarmerModule.Type2.BREAK;
      } else {
         BlockState blockState2 = mc.level.getBlockState(blockPos.above());
         if (this.method4427() && this.method4415(blockState, blockState2)) {
            return CropFarmerModule.Type2.TILL;
         } else {
            return this.method4414(blockPos, blockState, blockState2) ? CropFarmerModule.Type2.PLACE : null;
         }
      }
   }

   private Rotation method4400(Rotation rotation) {
      Rotation rotation2 = zenith.getRotationManager().getEffectiveRotation();
      return rotation2.add(rotation2.wrappedDeltaTo(rotation));
   }

   public Item method4401() {
      return this.method4430().item;
   }

   public Item method4402() {
      return this.method4430().item2;
   }

   public boolean method4403(BlockState blockState, BlockState blockState2) {
      return blockState.is(this.method4430().block) && blockState2.isAir();
   }

   public boolean method4404(BlockState blockState) {
      return switch (this.method4430()) {
         case NETHER_WART -> blockState.is(Blocks.NETHER_WART) && blockState.getValue(NetherWartBlock.AGE) == 3;
         case CARROT -> blockState.is(Blocks.CARROTS) && blockState.getValue(CropBlock.AGE) == 7;
         case POTATO -> blockState.is(Blocks.POTATOES) && blockState.getValue(CropBlock.AGE) == 7;
         case BEETROOT -> blockState.is(Blocks.BEETROOTS) && blockState.getValue(BeetrootBlock.AGE) == 3;
         case WHEAT -> blockState.is(Blocks.WHEAT) && blockState.getValue(CropBlock.AGE) == 7;
         case TORCHFLOWER -> blockState.is(Blocks.TORCHFLOWER);
         case PITCHER_PLANT -> blockState.is(Blocks.PITCHER_CROP)
            && blockState.getValue(PitcherCropBlock.AGE) == 4
            && blockState.getValue(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER;
      };
   }

   public Rotation method4405() {
      return this.method4406(Collections.emptySet());
   }

   private Rotation method4406(Set<BlockPos> emptySet) {
      BlockPos blockPos = Zenith.getInstance().getTimedBlockPos().getBlockPos();
      BlockPos blockPos2 = Zenith.getInstance().getTimedBlockPos().getBlockPos2();
      boolean flag = this.method4428();
      boolean flag2 = this.method4427();
      BlockPos immutable = null;
      BlockPos pos = null;
      BlockPos immutable2 = null;
      double d = Double.MAX_VALUE;
      double d2 = Double.MAX_VALUE;
      double d3 = Double.MAX_VALUE;
      int n = Math.min(blockPos.getX(), blockPos2.getX());
      int n2 = Math.max(blockPos.getX(), blockPos2.getX());
      int n3 = Math.min(blockPos.getY(), blockPos2.getY());
      int n4 = Math.max(blockPos.getY(), blockPos2.getY());
      int n5 = Math.min(blockPos.getZ(), blockPos2.getZ());
      int n6 = Math.max(blockPos.getZ(), blockPos2.getZ());
      MutableBlockPos mutablePos = new MutableBlockPos();
      MutableBlockPos mutablePos2 = new MutableBlockPos();
      this.method4423(blockPos, blockPos2);

      try {
         for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
               for (int k = n5; k <= n6; k++) {
                  mutablePos.set(i, j, k);
                  if (!emptySet.contains(mutablePos)) {
                     BlockState blockState = mc.level.getBlockState(mutablePos);
                     BlockState blockState2 = mc.level.getBlockState(mutablePos2.set(i, j + 1, k));
                     if (flag && this.method4414(mutablePos, blockState, blockState2)) {
                        double d4;
                        if ((d4 = mc.player.distanceToSqr(Vec3.atLowerCornerOf(mutablePos))) < d) {
                           d = d4;
                           immutable = mutablePos.immutable();
                        }
                     } else {
                        double d4;
                        if (flag2 && this.method4415(blockState, blockState2) && (d4 = mc.player.distanceToSqr(Vec3.atLowerCornerOf(mutablePos))) < d2) {
                           d2 = d4;
                           pos = mutablePos.immutable();
                        }
                     }

                     double d4;
                     if (this.method4404(blockState) && (d4 = mc.player.distanceToSqr(Vec3.atCenterOf(mutablePos))) < d3) {
                        d3 = d4;
                        immutable2 = mutablePos.immutable();
                     }
                  }
               }
            }
         }
      } finally {
         this.method4424();
      }

      Vec3 eyePosition = mc.player.getEyePosition();
      Rotation rotation;
      if (flag && immutable != null) {
         rotation = RotationUtils.method6912(net.minecraft.world.phys.Vec3.atCenterOf(immutable).add(0.0, 0.5, 0.0).subtract(eyePosition));
      } else if (pos == null || !(net.minecraft.world.phys.Vec3.atCenterOf(pos).distanceToSqr(eyePosition) < 20.0) && immutable2 != null) {
         if (immutable2 != null) {
            rotation = RotationUtils.method6912(net.minecraft.world.phys.Vec3.atCenterOf(immutable2).add(0.0, -0.5, 0.0).subtract(eyePosition));
         } else {
            rotation = null;
         }
      } else {
         rotation = RotationUtils.method6912(net.minecraft.world.phys.Vec3.atCenterOf(pos).add(0.0, 0.5, 0.0).subtract(eyePosition));
      }

      return rotation == null ? null : zenith.getRotationManager().getEffectiveRotation().add(rotationManager.getEffectiveRotation().wrappedDeltaTo(rotation));
   }

   public BlockHitResult method4407(Rotation rotation) {
      return this.method4408(rotation, Collections.emptySet());
   }

   private BlockHitResult method4408(Rotation rotation, Set<BlockPos> emptySet) {
      BlockHitResult blockHitResult = TargetUtils.method3216(mc.player.getEyePosition(1.0F), rotation, mc.player.blockInteractionRange(), var2x -> {
         if (var2x == null) {
            return false;
         } else {
            BlockPos var3x = var2x.getBlockPos();
            if (emptySet.contains(var3x)) {
               return false;
            } else {
               BlockState blockState = mc.level.getBlockState(var3x);
               BlockState blockState2 = mc.level.getBlockState(var3x.above());
               return this.method4404(blockState) || this.method4414(var3x, blockState, blockState2) || this.method4427() && this.method4415(blockState, blockState2);
            }
         }
      });
      return blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS ? blockHitResult : null;
   }

   public void method4409(BlockHitResult blockHitResult, BlockState blockState) {
      if (this.method4404(blockState)) {
         mc.gameMode.continueDestroyBlock(blockHitResult.getBlockPos(), blockHitResult.getDirection());
         org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
         this.method4411(blockHitResult.getBlockPos());
      } else if (this.method4415(blockState, mc.level.getBlockState(blockHitResult.getBlockPos().above()))) {
         if (this.method4427()) {
            UseItemUtils.method6514(blockHitResult, InteractionHand.MAIN_HAND);
         }
      } else if (this.method4414(blockHitResult.getBlockPos(), blockState, mc.level.getBlockState(blockHitResult.getBlockPos().above()))) {
         UseItemUtils.method6514(this.method4412(blockHitResult.getBlockPos()), InteractionHand.OFF_HAND);
      }
   }

   public void method4410(BlockHitResult blockHitResult, BlockState blockState) {
      if (this.method4404(blockState)) {
         mc.gameMode.continueDestroyBlock(blockHitResult.getBlockPos(), blockHitResult.getDirection());
         this.method4411(blockHitResult.getBlockPos());
      } else if (this.method4415(blockState, mc.level.getBlockState(blockHitResult.getBlockPos().above()))) {
         if (this.method4427()) {
            UseItemUtils.method6514(blockHitResult, InteractionHand.MAIN_HAND);
         }
      } else if (this.method4414(blockHitResult.getBlockPos(), blockState, mc.level.getBlockState(blockHitResult.getBlockPos().above()))) {
         UseItemUtils.method6514(this.method4412(blockHitResult.getBlockPos()), InteractionHand.OFF_HAND);
      }
   }

   private void method4411(BlockPos blockPos) {
      BlockPos below = blockPos.below();
      BlockState blockState = mc.level.getBlockState(below);
      if (this.method4428() && blockState.is(this.method4430().block) && (!this.method4429() || this.method4418(below))) {
         UseItemUtils.method6514(this.method4412(below), InteractionHand.OFF_HAND);
      }
   }

   private BlockHitResult method4412(BlockPos blockPos) {
      return new BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(blockPos).add(0.0, 0.5, 0.0), Direction.UP, blockPos, false);
   }

   public boolean method4413() {
      return Zenith.getInstance().getTimedBlockPos().getBlockPos() != null && Zenith.getInstance().getTimedBlockPos().getBlockPos2() != null
         ? mc.player.isUsingItem()
            || mc.gui.screen() instanceof ChatScreen
            || mc.gui.screen() instanceof InventoryScreen
            || !(mc.player.containerMenu instanceof InventoryMenu)
         : true;
   }

   private boolean method4414(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
      return this.method4428() && this.method4403(blockState, blockState2) && (!this.method4429() || this.method4418(blockPos));
   }

   private boolean method4415(BlockState blockState, BlockState blockState2) {
      return this.method4429() && blockState2.isAir() && this.method4416(blockState);
   }

   private boolean method4416(BlockState blockState) {
      return blockState.is(Blocks.GRASS_BLOCK)
         || blockState.is(Blocks.DIRT_PATH)
         || blockState.is(Blocks.DIRT)
         || blockState.is(Blocks.COARSE_DIRT)
         || blockState.is(Blocks.ROOTED_DIRT);
   }

   private boolean method4417(BlockPos pos) {
      BlockState blockState = mc.level.getBlockState(pos);
      return blockState.is(Blocks.FARMLAND) || this.method4416(blockState) && mc.level.getBlockState(pos.above()).isAir();
   }

   private boolean method4418(BlockPos below) {
      if (!this.method4429()) {
         return true;
      } else {
         BlockPos above = below.above();
         if (!this.method4422(below) && !mc.level.isRainingAt(above)) {
            return false;
         } else {
            BlockState blockState = mc.level.getBlockState(above.above());
            return this.method4430() != CropFarmerModule.Type.PITCHER_PLANT || blockState.isAir() || blockState.is(Blocks.PITCHER_CROP);
         }
      }
   }

   private boolean method4419(BlockPos pos) {
      return mc.level.getRawBrightness(pos.above(), 0) >= this.method4430().intValue;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private List<BlockPos> method4420(BlockPos blockPos, BlockPos blockPos2) {
      TreeMap map = new TreeMap();
      HashMap map2 = new HashMap();
      ArrayList arrayList = new ArrayList();
      BitSet bitSet = new BitSet();
      this.method4423(blockPos, blockPos2);

      try {
         for (BlockPos pos : getBlocksInZone(blockPos, blockPos2)) {
            if (this.method4417(pos)) {
               ((java.util.List)map.computeIfAbsent(pos.getY(), obj -> new ArrayList<>())).add(pos);
               if (!this.method4422(pos)) {
                  bitSet.set(arrayList.size());
                  ((java.util.List)map2.computeIfAbsent(pos.getY(), obj -> new ArrayList<>())).add(pos);
               }

               arrayList.add(pos);
            }
         }
      } catch (Throwable e) {
         this.method4424();
         throw e;
      }

      this.method4424();
      if (arrayList.equals(this.items4) && bitSet.equals(this.bitSet)) {
         return this.items5;
      } else {
         ArrayList<BlockPos> poses = new ArrayList<>();

         for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
            Map.Entry entry = (Map.Entry)obj;
            poses.addAll(this.method4421((List<BlockPos>)entry.getValue(), (List<BlockPos>)map2.getOrDefault(entry.getKey(), Collections.emptyList())));
         }

         this.items4 = arrayList;
         this.bitSet = bitSet;
         this.items5 = poses;
         return poses;
      }
   }

   private List<BlockPos> method4421(List<BlockPos> poses, List<BlockPos> poses2) {
      var poses3 = poses2.stream().sorted(comparator).toList();
      if (poses3.isEmpty()) {
         return Collections.emptyList();
      } else {
         LinkedHashMap map = new LinkedHashMap();

         for (BlockPos pos : poses.stream().sorted(comparator).toList()) {
            BitSet bitSet = new BitSet(poses3.size());

            for (int i = 0; i < poses3.size(); i++) {
               BlockPos pos2 = (BlockPos)poses3.get(i);
               if (Math.abs(pos.getX() - pos2.getX()) <= 4 && Math.abs(pos.getZ() - pos2.getZ()) <= 4) {
                  bitSet.set(i);
               }
            }

            if (!bitSet.isEmpty()) {
               map.putIfAbsent(bitSet, pos);
            }
         }

         var items = new ArrayList<>(map.values());
         var arrayList = new ArrayList<>(map.keySet());
         CropFarmerModule.Entry2 entry2 = new CropFarmerModule.Entry2(items, arrayList, poses3.size(), System.nanoTime() + 25000000L);
         return entry2.method4433();
      }
   }

   private boolean method4422(BlockPos below) {
      int x = below.getX();
      int y = below.getY();
      int z = below.getZ();
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = -4; i <= 4; i++) {
         for (int j = 0; j <= 1; j++) {
            for (int k = -4; k <= 4; k++) {
               if (this.method4425(mutablePos.set(x + k, y + j, z + i))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private void method4423(BlockPos blockPos, BlockPos blockPos2) {
      int intValue = Math.min(blockPos.getX(), blockPos2.getX()) - 4;
      int n = Math.max(blockPos.getX(), blockPos2.getX()) + 4;
      int n2 = Math.min(blockPos.getY(), blockPos2.getY());
      int n3 = Math.max(blockPos.getY(), blockPos2.getY()) + 1;
      int n4 = Math.min(blockPos.getZ(), blockPos2.getZ()) - 4;
      int n5 = Math.max(blockPos.getZ(), blockPos2.getZ()) + 4;
      long l;
      if ((l = (long)(n - intValue + 1) * (n3 - n2 + 1) * (n5 - n4 + 1)) > 0L && l <= 4194304L) {
         int toIndex = (int)l;
         if (this.byteValues.length < toIndex) {
            this.byteValues = new byte[toIndex];
         } else {
            Arrays.fill(this.byteValues, 0, toIndex, (byte)0);
         }

         this.intValue = intValue;
         this.intValue2 = n2;
         this.intValue3 = n4;
         this.intValue4 = n - intValue + 1;
         this.intValue5 = n3 - n2 + 1;
         this.intValue6 = n5 - n4 + 1;
         this.flag2 = true;
      } else {
         this.flag2 = false;
      }
   }

   private void method4424() {
      this.flag2 = false;
   }

   private boolean method4425(BlockPos set) {
      if (this.flag2) {
         int n = set.getX() - this.intValue;
         int n2 = set.getY() - this.intValue2;
         int n3 = set.getZ() - this.intValue3;
         if (n >= 0 && n < this.intValue4 && n2 >= 0 && n2 < this.intValue5 && n3 >= 0 && n3 < this.intValue6) {
            int n4 = (n * this.intValue5 + n2) * this.intValue6 + n3;
            int byteValue;
            if ((byteValue = this.byteValues[n4]) == 0) {
               byteValue = mc.level.getFluidState(set).is(FluidTags.WATER) ? 1 : 2;
               this.byteValues[n4] = (byte)byteValue;
            }

            return byteValue == 1;
         }
      }

      return mc.level.getFluidState(set).is(FluidTags.WATER);
   }

   private static void method4426(BlockPos blockPos, BlockPos blockPos2, Consumer<BlockPos> consumer) {
      int n = Math.min(blockPos.getX(), blockPos2.getX());
      int n2 = Math.max(blockPos.getX(), blockPos2.getX());
      int n3 = Math.min(blockPos.getY(), blockPos2.getY());
      int n4 = Math.max(blockPos.getY(), blockPos2.getY());
      int n5 = Math.min(blockPos.getZ(), blockPos2.getZ());
      int n6 = Math.max(blockPos.getZ(), blockPos2.getZ());
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = n; i <= n2; i++) {
         for (int j = n3; j <= n4; j++) {
            for (int k = n5; k <= n6; k++) {
               consumer.accept(mutablePos.set(i, j, k));
            }
         }
      }
   }

   private boolean method4427() {
      return mc.player.getMainHandItem().is(net.minecraft.tags.ItemTags.HOES);
   }

   private boolean method4428() {
      return mc.player.getItemInHand(InteractionHand.OFF_HAND).getItem() == this.method4401();
   }

   private boolean method4429() {
      return this.method4430().block == Blocks.FARMLAND;
   }

   private CropFarmerModule.Type method4430() {
      String text = this.crop.get();
      CropFarmerModule.Type type = this.type;
      if (type == null || !text.equals(this.text)) {
         type = CropFarmerModule.Type.method4432(text);
         this.type = type;
         this.text = text;
      }

      return type;
   }

   @EventTarget(0)
   public void method4431(MoveInputEvent event) {
      if (!this.method4413() && this.method4405() != null) {
         event.method3038(true);
      }
   }

   public static List<BlockPos> getBlocksInZone(BlockPos blockPos, BlockPos blockPos2) {
      ArrayList<BlockPos> poses = new ArrayList<>();
      int n = Math.min(blockPos.getX(), blockPos2.getX());
      int n2 = Math.max(blockPos.getX(), blockPos2.getX());
      int n3 = Math.min(blockPos.getY(), blockPos2.getY());
      int n4 = Math.max(blockPos.getY(), blockPos2.getY());
      int n5 = Math.min(blockPos.getZ(), blockPos2.getZ());
      int n6 = Math.max(blockPos.getZ(), blockPos2.getZ());

      for (int i = n; i <= n2; i++) {
         for (int j = n3; j <= n4; j++) {
            for (int k = n5; k <= n6; k++) {
               poses.add(new BlockPos(i, j, k));
            }
         }
      }

      return poses;
   }

   private static final class Entry {
      private final CropFarmerModule.Type2 type2;
      private final long longValue;

      private Entry(CropFarmerModule.Type2 type2, long longValue) {
         this.type2 = type2;
         this.longValue = longValue;
      }
   }

   private static final class Entry2 {
      private final List<BlockPos> items;
      private final List<BitSet> items2;
      private final List<List<Integer>> items3;
      private final Map<BitSet, Integer> items4 = new HashMap<>();
      private final long longValue;
      private List<Integer> items5;

      private Entry2(List<BlockPos> items, List<BitSet> bitSets, int size, long longValue) {
         this.items = items;
         this.items2 = bitSets;
         this.longValue = longValue;
         this.items3 = new ArrayList<>(size);

         for (int i = 0; i < size; i++) {
            this.items3.add(new ArrayList<>());
         }

         for (int i = 0; i < bitSets.size(); i++) {
            for (int j = ((BitSet)bitSets.get(i)).nextSetBit(0); j >= 0; j = ((BitSet)bitSets.get(i)).nextSetBit(j + 1)) {
               this.items3.get(j).add(i);
            }
         }
      }

      private List<BlockPos> method4433() {
         BitSet bitSet = new BitSet(this.items3.size());
         bitSet.set(0, this.items3.size());
         this.items5 = this.method4435(bitSet);
         this.method4434(bitSet, new ArrayList<>());
         return this.items5.stream().map(this.items::get).toList();
      }

      private void method4434(BitSet bitSet, List<Integer> integers) {
         if (System.nanoTime() < this.longValue && integers.size() < this.items5.size()) {
            if (bitSet.isEmpty()) {
               this.items5 = new ArrayList<>(integers);
            } else {
               Integer n = this.items4.get(bitSet);
               if (n == null || n > integers.size()) {
                  this.items4.put((BitSet)bitSet.clone(), integers.size());
                  int n2 = 0;

                  for (BitSet bitSet2 : this.items2) {
                     n2 = Math.max(n2, this.method4437(bitSet2, bitSet));
                  }

                  if (n2 != 0 && integers.size() + (bitSet.cardinality() + n2 - 1) / n2 < this.items5.size()) {
                     int n3 = this.method4436(bitSet);
                     ArrayList<Integer> integers2 = new ArrayList<>(this.items3.get(n3));
                     integers2.sort(Comparator.<Integer>comparingInt(var2x -> this.method4437(this.items2.get(var2x), bitSet)).reversed());

                     for (int n4 : integers2) {
                        BitSet bitSet2 = (BitSet)bitSet.clone();
                        bitSet2.andNot(this.items2.get(n4));
                        integers.add(n4);
                        this.method4434(bitSet2, integers);
                        integers.remove(integers.size() - 1);
                        if (System.nanoTime() >= this.longValue) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      private List<Integer> method4435(BitSet bitSet) {
         BitSet set = (BitSet)bitSet.clone();
         ArrayList<Integer> integers = new ArrayList<>();

         while (!set.isEmpty()) {
            int n = -1;
            int n2 = 0;

            for (int i = 0; i < this.items2.size(); i++) {
               int n3 = this.method4437(this.items2.get(i), set);
               if (n3 > n2) {
                  n2 = n3;
                  n = i;
               }
            }

            if (n == -1) {
               break;
            }

            integers.add(n);
            set.andNot(this.items2.get(n));
         }

         return integers;
      }

      private int method4436(BitSet bitSet) {
         int nextSetBit = bitSet.nextSetBit(0);
         int size = Integer.MAX_VALUE;

         for (int i = nextSetBit; i >= 0; i = bitSet.nextSetBit(i + 1)) {
            int n = this.items3.get(i).size();
            if (n < size) {
               size = n;
               nextSetBit = i;
            }
         }

         return nextSetBit;
      }

      private int method4437(BitSet bitSet, BitSet set) {
         BitSet bitSet2 = (BitSet)bitSet.clone();
         bitSet2.and(set);
         return bitSet2.cardinality();
      }
   }

   private static enum Type {
      NETHER_WART("module.cropFarmer.crop.netherWart", Items.NETHER_WART, Items.NETHER_WART, Blocks.SOUL_SAND, 0),
      CARROT("module.cropFarmer.crop.carrot", Items.CARROT, Items.CARROT, Blocks.FARMLAND, 9),
      POTATO("module.cropFarmer.crop.potato", Items.POTATO, Items.POTATO, Blocks.FARMLAND, 9),
      BEETROOT("module.cropFarmer.crop.beetroot", Items.BEETROOT_SEEDS, Items.BEETROOT, Blocks.FARMLAND, 9),
      WHEAT("module.cropFarmer.crop.wheat", Items.WHEAT_SEEDS, Items.WHEAT, Blocks.FARMLAND, 9),
      TORCHFLOWER("module.cropFarmer.crop.torchflower", Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, Blocks.FARMLAND, 9),
      PITCHER_PLANT("module.cropFarmer.crop.pitcherPlant", Items.PITCHER_POD, Items.PITCHER_PLANT, Blocks.FARMLAND, 8);

      private final String text;
      private final Item item;
      private final Item item2;
      private final Block block;
      private final int intValue;
      private static final CropFarmerModule.Type[] types = values();

      private Type(String text, Item item, Item item2, Block block, int intValue) {
         this.text = text;
         this.item = item;
         this.item2 = item2;
         this.block = block;
         this.intValue = intValue;
      }

      private static CropFarmerModule.Type method4432(String text) {
         for (CropFarmerModule.Type type : types) {
            if (type.text.equals(text)) {
               return type;
            }
         }

         return NETHER_WART;
      }
   }

   private static enum Type2 {
      BREAK,
      TILL,
      PLACE;
   }
}

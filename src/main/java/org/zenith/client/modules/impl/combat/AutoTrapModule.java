package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.SwingSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.Class33;
import org.zenith.utility.network.Island;
import org.zenith.utility.network.PlacementQueue;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "AutoTrap",
   description = "module.autotrap.description",
   category = ModuleCategory.COMBAT
)
public final class AutoTrapModule extends Module {
   public static final AutoTrapModule INSTANCE = new AutoTrapModule();
   private static final double[] CONST_doubleValues = new double[]{0.5, 0.3, 0.7, 0.15, 0.85};
   private static final double[] CONST_doubleValues2 = new double[]{0.5, 0.25, 0.75};
   private static final double CONST_doubleValue = 0.03;
   private static final double CONST_doubleValue2 = 3.0;
   private static final int CONST_intValue = 4;
   private static final int CONST_intValue2 = 20;
   private static final long CONST_longValue = 350L;
   private static final long CONST_longValue2 = 2000L;
   private final ModeSetting activation = new ModeSetting(
      "module.autoTrap.activation", "module.autoTrap.activation.desc", "module.autoTrap.activationAlways", "module.autoTrap.activationBind"
   );
   private final BindSetting bind = new BindSetting("module.autoTrap.bind", "module.autoTrap.bind.desc", -1, () -> this.activation.isSelected(1));
   private final MultiSelectSetting traps = new MultiSelectSetting("module.autoTrap.traps", "module.autoTrap.traps.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapWeb", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapSnow", false);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapLava", false);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapFire", false);
   private final MultiSelectSetting.Entry entry5 = new MultiSelectSetting.Entry(this.traps, "module.autoTrap.trapAnvil", false);
   private final ModeSetting layout = new ModeSetting(
      "module.autoTrap.layout",
      "module.autoTrap.layout.desc",
      () -> this.entry.isEnabled() || this.entry2.isEnabled(),
      "module.autoTrap.layoutFeet",
      "module.autoTrap.layoutFull"
   );
   private final BooleanSetting lavaPickup = new BooleanSetting("module.autoTrap.lavaPickup", "module.autoTrap.lavaPickup.desc", true, this.entry3::isEnabled);
   private final SliderSetting lavaPickupDelay = new SliderSetting(
      "module.autoTrap.lavaPickupDelay",
      20.0F,
      2.0F,
      100.0F,
      1.0F,
      "module.autoTrap.lavaPickupDelay.desc",
      "t",
      () -> this.entry3.isEnabled() && this.lavaPickup.isEnabled()
   );
   private final ModeSetting predict = new ModeSetting(
      "module.autoTrap.predict",
      "module.autoTrap.predict.desc",
      "module.autoTrap.predictTrajectory",
      "module.autoTrap.predictZone",
      "module.autoTrap.predictLook",
      "module.autoTrap.predictOff"
   );
   private final SliderSetting predictTicks = new SliderSetting(
      "module.autoTrap.predictTicks", 2.0F, 0.0F, 10.0F, 1.0F, "module.autoTrap.predictTicks.desc", "t", () -> !this.predict.isSelected(3)
   );
   private final SliderSetting zoneSpread = new SliderSetting(
      "module.autoTrap.zoneSpread", 40.0F, 10.0F, 90.0F, 5.0F, "module.autoTrap.zoneSpread.desc", "°", () -> this.predict.isSelected(1)
   );
   private final SliderSetting range = new SliderSetting("module.autoTrap.range", 4.5F, 2.0F, 6.0F, 0.1F, "module.autoTrap.range.desc", "b");
   private final SliderSetting delay = new SliderSetting("module.autoTrap.delay", 0.0F, 0.0F, 10.0F, 1.0F, "module.autoTrap.delay.desc", "t");
   private final ModeSetting swap = new ModeSetting(
      "module.autoTrap.swap", "module.autoTrap.swap.desc", "module.autoTrap.swapHotbar", "module.autoTrap.swapInventory"
   );
   private final BooleanSetting protectSelf = new BooleanSetting("module.autoTrap.protectSelf", "module.autoTrap.protectSelf.desc", true);
   private final BooleanSetting pauseOnUse = new BooleanSetting("module.autoTrap.pauseOnUse", "module.autoTrap.pauseOnUse.desc", true);
   private final BooleanSetting hideSwing = new BooleanSetting("module.autoTrap.hideSwing", "module.autoTrap.hideSwing.desc", false);
   private final BooleanSetting render = new BooleanSetting("module.autoTrap.render", "module.autoTrap.render.desc", true);
   private final Map<BlockPos, AutoTrapModule.Data> items = new HashMap<>();
   private final Map<BlockPos, Integer> items2 = new HashMap<>();
   private final Queue<AutoTrapModule.Data3> queue = new ConcurrentLinkedQueue<>();
   private volatile boolean flag;
   private final ArrayDeque<Vec3> arrayDeque = new ArrayDeque<>();
   private final List<AutoTrapModule.Data5> items3 = new ArrayList<>();
   private final PlacementQueue placementQueue = new PlacementQueue();
   private final Class33 class33 = new Class33();
   private Player playerEntity;
   private List<BlockPos> items4 = List.of();
   private List<AABB> items5 = List.of();
   private List<AutoTrapModule.Data6> items6 = List.of();
   private AutoTrapModule.Data2 data2;
   private BlockPos blockPos;
   private Rotation rotation;
   private int intValue = -32768 << 16 | 0;
   private int intValue2 = -32768 << 16 | 0;
   private int intValue3 = -16384 << 16 | 0;
   private int intValue4 = -16384 << 16 | 0;
   private int intValue5 = -32768 << 16 | 0;
   private long longValue;

   private AutoTrapModule() {
   }

   public boolean method2427() {
      return this.rotation != null || this.data2 != null;
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

   @EventTarget
   public void method2428(Event5 event) {
      if (!UseItemUtils.method6526() && mc.gameMode != null) {
         int tickCount = mc.player.tickCount;
         if (this.intValue != (-32768 << 16 | 0) && tickCount < this.intValue) {
            this.reset();
         }

         if (this.intValue != tickCount) {
            this.intValue = tickCount;
            this.method2431(tickCount);
         }

         Rotation rotation = this.rotation;
         if (rotation != null) {
            rotationManager.method7849(
               new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
               10,
               this
            );
         }
      }
   }

   @EventTarget
   public void method2429(Event9 event) {
      AutoTrapModule.Data2 data2 = this.data2;
      if (data2 != null && mc.player != null && mc.gameMode != null) {
         int tickCount = mc.player.tickCount;
         if (this.intValue2 != tickCount) {
            this.intValue2 = tickCount;
            this.data2 = null;
            if (this.intValue5 != tickCount && this.method2455(data2.getData6())) {
               AutoTrapModule.Type type = data2.getData6().getType();
               BlockPos pos = data2.getData6().pos();
               this.flag = true;
               if (this.method2459(data2, tickCount)) {
                  if (type == AutoTrapModule.Type.PICKUP) {
                     this.items2.remove(pos);
                  }

                  if (type == AutoTrapModule.Type.ANVIL) {
                     this.intValue4 = tickCount;
                  }

                  this.items.put(pos, new AutoTrapModule.Data(type, tickCount + this.method2446()));
                  this.intValue3 = tickCount;
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100()) {
         this.placementQueue.method2815(event.getPacket3());
         if (this.flag) {
            if (event.getPacket3() instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
               this.queue.add(new AutoTrapModule.Data3(blockUpdatePacket.getPos().immutable(), blockUpdatePacket.getBlockState()));
            } else if (event.getPacket3() instanceof ClientboundSectionBlocksUpdatePacket blocksUpdatePacket) {
               blocksUpdatePacket.runUpdates((var1x, var2x) -> this.queue.add(new AutoTrapModule.Data3(var1x.immutable(), var2x)));
            }
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      this.class33.onMoveInput(event);
      if (this.rotation != null && mc.player != null) {
         MovementInputUtils.method7426(event, rotationManager.method7844().getFloatValue(), mc.player.getYRot());
      }
   }

   @EventTarget
   public void method2430(WorldRenderEvent event) {
      if (this.render.isEnabled() && mc.player != null) {
         int color = zenith.getStyleManager().getClientColor(0).method7287();
         if (this.playerEntity != null) {
            for (AABB aabb : this.items5) {
               Vec3 vec = new Vec3((aabb.minX + aabb.maxX) * 0.5, aabb.minY + 0.05, (aabb.minZ + aabb.maxZ) * 0.5);
               GlowUtil.method2230(new AABB(vec.subtract(0.05, 0.05, 0.05), vec.add(0.05, 0.05, 0.05)), ColorUtils.method6439(color, 0.6F), 1.0F);
            }
         }

         for (BlockPos blockPos : this.items.keySet()) {
            GlowUtil.method2230(method2475(blockPos, 0.9), ColorUtils.method6439(color, 0.45F), 1.0F);
         }

         if (this.blockPos != null) {
            GlowUtil.method2230(method2475(this.blockPos, 0.96), color, 1.5F);
         }

         long now = System.currentTimeMillis();
         var iterator = this.items3.iterator();

         while (iterator.hasNext()) {
            AutoTrapModule.Data5 data5 = (AutoTrapModule.Data5)iterator.next();
            float f;
            if ((f = (float)(now - data5.time()) / 350.0F) >= 1.0F) {
               iterator.remove();
            } else {
               GlowUtil.method2230(method2475(data5.pos(), 0.5 + 0.5 * f), ColorUtils.method6439(color, 1.0F - f), 1.5F);
            }
         }
      } else {
         this.items3.clear();
      }
   }

   private void method2431(int tickCount) {
      this.data2 = null;
      this.blockPos = null;
      this.rotation = null;
      this.method2469(tickCount);
      this.placementQueue.method2816(tickCount);
      this.method2470(tickCount);
      this.flag = !this.items.isEmpty();
      this.items2.keySet().removeIf(var1x -> !this.method2458(var1x));
      if (!this.method2464()) {
         this.class33.cancel();
         this.method2472();
         this.items6 = List.of();
      } else {
         if (this.class33.method5304()) {
            this.intValue5 = tickCount;
         }

         Player playerEntity = this.method2465() ? this.method2466() : null;
         if (playerEntity != this.playerEntity) {
            this.arrayDeque.clear();
         }

         this.playerEntity = playerEntity;
         if (this.playerEntity == null) {
            this.method2472();
         } else {
            this.arrayDeque.addLast(this.playerEntity.position());

            while (this.arrayDeque.size() > 4) {
               this.arrayDeque.removeFirst();
            }

            this.method2437();
         }

         this.items6 = this.method2432(tickCount);
         this.method2436();
         if (!this.items6.isEmpty()
            && !(tickCount - this.intValue3 <= this.delay.getValue3())) {
            this.data2 = this.method2451();
            if (this.data2 != null && this.data2.getData6().getType().flag2) {
               this.blockPos = this.data2.getData6().pos();
               this.rotation = rotationManager.getRotation2();
            } else if (this.data2 == null || !this.data2.getData6().getType().flag || !this.layout.isSelected(0)) {
               BlockPos pos = this.data2 == null ? null : this.data2.getData6().pos();

               for (AutoTrapModule.Data6 data6 : this.items6) {
                  if (!data6.pos().equals(pos)) {
                     AutoTrapModule.Data4 data4 = this.method2448(data6);
                     if (data4 != null) {
                        this.blockPos = data6.pos();
                        this.rotation = data4.rotation();
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private List<AutoTrapModule.Data6> method2432(int tickCount) {
      ArrayList<AutoTrapModule.Data6> data6s = new ArrayList<>();
      if (this.entry3.isEnabled() && this.lavaPickup.isEnabled() && this.method2460(AutoTrapModule.Type.PICKUP)) {
         int n = Math.round(this.lavaPickupDelay.getValue3());

         for (Entry entry : this.items2.entrySet()) {
            if (this.playerEntity == null || tickCount - (Integer)entry.getValue() >= n) {
               data6s.add(new AutoTrapModule.Data6(AutoTrapModule.Type.PICKUP, (BlockPos)entry.getKey()));
            }
         }
      }

      if (this.playerEntity == null) {
         return data6s;
      } else {
         AutoTrapModule.Type type = this.method2433();
         if (type != null) {
            for (BlockPos blockPos : this.items4) {
               data6s.add(new AutoTrapModule.Data6(type, blockPos));
            }
         }

         AABB boundingBox = this.playerEntity.getBoundingBox();
         if ((type != null || this.entry.isEnabled() || this.entry2.isEnabled()) && !this.method2440(boundingBox)) {
            return data6s;
         } else {
            List<BlockPos> poses = this.method2441(boundingBox);
            boolean flag = this.playerEntity.fireImmune() || this.playerEntity.hasEffect(MobEffects.FIRE_RESISTANCE);
            boolean flag2 = this.playerEntity.isInLava() || this.method2435(boundingBox);
            if (this.entry3.isEnabled() && !flag && !flag2 && this.method2460(AutoTrapModule.Type.LAVA)) {
               for (BlockPos blockPos : poses) {
                  data6s.add(new AutoTrapModule.Data6(AutoTrapModule.Type.LAVA, blockPos));
               }

               for (BlockPos pos : poses) {
                  data6s.add(new AutoTrapModule.Data6(AutoTrapModule.Type.LAVA, pos.above()));
               }
            }

            if (this.entry4.isEnabled()
               && !flag
               && !flag2
               && !this.playerEntity.isOnFire()
               && !this.playerEntity.isInWater()
               && this.method2460(AutoTrapModule.Type.FIRE)) {
               for (BlockPos blockPos : poses) {
                  data6s.add(new AutoTrapModule.Data6(AutoTrapModule.Type.FIRE, blockPos));
               }
            }

            if (this.entry5.isEnabled()
               && tickCount - this.intValue4 >= 20
               && this.method2460(AutoTrapModule.Type.ANVIL)
               && (!poses.isEmpty())) {
               BlockPos pos = (BlockPos)poses.getFirst();

               for (int i = 6; i >= 3; i--) {
                  BlockPos above = pos.above(i);
                  if (this.method2434(pos.above(), above)) {
                     data6s.add(new AutoTrapModule.Data6(AutoTrapModule.Type.ANVIL, above));
                  }
               }
            }

            return data6s;
         }
      }
   }

   private AutoTrapModule.Type method2433() {
      if (this.entry.isEnabled() && this.method2460(AutoTrapModule.Type.WEB)) {
         return AutoTrapModule.Type.WEB;
      } else {
         return this.entry2.isEnabled() && this.method2460(AutoTrapModule.Type.SNOW) ? AutoTrapModule.Type.SNOW : null;
      }
   }

   private boolean method2434(BlockPos above, BlockPos pos) {
      for (int i = above.getY(); i < pos.getY(); i++) {
         if (!FallingBlock.isFree(mc.level.getBlockState(new BlockPos(above.getX(), i, above.getZ())))) {
            return false;
         }
      }

      return true;
   }

   private boolean method2435(AABB boundingBox) {
      AABB inflate = boundingBox.inflate(1.0);

      for (BlockPos pos : this.items2.keySet()) {
         if (inflate.intersects(new AABB(pos))) {
            return true;
         }
      }

      for (Entry entry : this.items.entrySet()) {
         if (((AutoTrapModule.Data)entry.getValue()).getType() == AutoTrapModule.Type.LAVA && inflate.intersects(new AABB((BlockPos)entry.getKey()))) {
            return true;
         }
      }

      return false;
   }

   private void method2436() {
      if (this.swap.isSelected(1)
         && !this.class33.pending()
         && (this.playerEntity != null || !this.items2.isEmpty())) {
         ArrayList<AutoTrapModule.Type> types = new ArrayList();
         if (this.playerEntity != null) {
            if (this.entry.isEnabled()) {
               types.add(AutoTrapModule.Type.WEB);
            } else if (this.entry2.isEnabled()) {
               types.add(AutoTrapModule.Type.SNOW);
            }

            if (this.entry3.isEnabled()) {
               types.add(AutoTrapModule.Type.LAVA);
            }

            if (this.entry4.isEnabled()) {
               types.add(AutoTrapModule.Type.FIRE);
            }

            if (this.entry5.isEnabled()) {
               types.add(AutoTrapModule.Type.ANVIL);
            }
         }

         if (!this.items2.isEmpty() && this.lavaPickup.isEnabled()) {
            types.add(AutoTrapModule.Type.PICKUP);
         }

         for (AutoTrapModule.Type type : types) {
            if (!this.method2460(type) && this.class33.method5302(var1x -> method2462(type, var1x))) {
               this.class33.method5303(var1x -> method2462(type, var1x));
               return;
            }
         }
      }
   }

   private void method2437() {
      ArrayList<List> lists = new ArrayList();
      Vec3 vec = this.method2444();
      int n = this.method2445();
      if (this.predict.isSelected(3) || n == 0) {
         lists.add(List.of(this.playerEntity.getBoundingBox()));
      } else if (this.predict.isSelected(2)) {
         double scale = method2473(vec);
         Vec3 vec2 = Vec3.directionFromRotation(0.0F, this.playerEntity.getYHeadRot()).scale(scale);
         lists.add(this.method2443(new Vec3(vec2.x, vec.y, vec2.z), n));
      } else if (this.predict.isSelected(1)) {
         float f = this.zoneSpread.getValue3();

         for (float angdeg : new float[]{0.0F, -f * 0.5F, f * 0.5F, -f, f}) {
            lists.add(this.method2443(method2474(vec, angdeg), n));
            if (method2473(vec) < 0.03) {
               break;
            }
         }
      } else {
         lists.add(this.method2443(vec, n));
      }

      this.items5 = (List<AABB>)lists.getFirst();
      AABB boundingBox = this.playerEntity.getBoundingBox();
      AABB aabb = this.items5.getLast();
      if (!this.method2439(boundingBox) && !this.method2439(aabb)) {
         LinkedHashSet linkedHashSet = new LinkedHashSet();

         for (List list : lists) {
            this.method2438(linkedHashSet, (AABB)list.getLast());
         }

         for (int i = this.items5.size() - 2; i >= 0; i--) {
            this.method2438(linkedHashSet, this.items5.get(i));
         }

         this.items4 = new ArrayList<>(linkedHashSet);
      } else {
         this.items4 = List.of();
      }
   }

   private void method2438(Set<BlockPos> poses, AABB boundingBox) {
      List<BlockPos> poses2 = this.method2441(boundingBox);
      boolean selected = this.layout.isSelected(1);
      boolean anyMatch = poses2.stream().anyMatch(this::method2457);
      boolean flag = !selected || poses2.stream().map(BlockPos::above).anyMatch(this::method2457);
      if (!anyMatch) {
         poses.addAll(poses2);
      }

      if (!flag) {
         for (BlockPos pos : poses2) {
            poses.add(pos.above());
         }
      }
   }

   private boolean method2439(AABB boundingBox) {
      var poses = this.method2441(boundingBox);
      boolean anyMatch = poses.stream().anyMatch(this::method2457);
      return !this.layout.isSelected(1) ? anyMatch : anyMatch && poses.stream().map(BlockPos::above).anyMatch(this::method2457);
   }

   private boolean method2440(AABB boundingBox) {
      return this.method2441(boundingBox).stream().anyMatch(this::method2457);
   }

   private List<BlockPos> method2441(AABB boundingBox) {
      int y = Mth.floor(boundingBox.minY + 0.001);
      int n = Mth.floor(boundingBox.minX + 0.001);
      int n2 = Mth.floor(boundingBox.maxX - 0.001);
      int n3 = Mth.floor(boundingBox.minZ + 0.001);
      int n4 = Mth.floor(boundingBox.maxZ - 0.001);
      double d = (boundingBox.minX + boundingBox.maxX) * 0.5;
      double d2 = (boundingBox.minZ + boundingBox.maxZ) * 0.5;
      ArrayList arrayList = new ArrayList(4);

      for (int i = n; i <= n2; i++) {
         for (int j = n3; j <= n4; j++) {
            BlockPos pos = this.method2442(new BlockPos(i, y, j));
            if (!arrayList.contains(pos)) {
               arrayList.add(pos);
            }
         }
      }

      arrayList.sort(Comparator.comparingDouble((net.minecraft.core.BlockPos var4x) -> {
         double var5x = var4x.getX() + 0.5 - d;
         double var7x = var4x.getZ() + 0.5 - d2;
         return var5x * var5x + var7x * var7x;
      }));
      return arrayList;
   }

   private BlockPos method2442(BlockPos pos) {
      BlockState blockState = mc.level.getBlockState(pos);
      if (!blockState.canBeReplaced() && !blockState.is(Blocks.COBWEB) && !blockState.is(Blocks.POWDER_SNOW)) {
         VoxelShape collisionShape = blockState.getCollisionShape(mc.level, pos);
         if (!collisionShape.isEmpty() && collisionShape.max(Axis.Y) < 1.0) {
            return pos.above();
         }
      }

      return pos;
   }

   private List<AABB> method2443(Vec3 vec, int n) {
      List<AABB> aabbs = new ArrayList<>(n + 1);
      AABB boundingBox = this.playerEntity.getBoundingBox();
      aabbs.add(boundingBox);
      double x = vec.x;
      double y = this.playerEntity.onGround() && vec.y <= 0.0 ? -0.0784 : vec.y;
      double z = vec.z;

      for (int i = 0; i < n; i++) {
         Vec3 movement = new Vec3(x, y, z);
         Vec3 collideBoundingBox = Entity.collideBoundingBox(this.playerEntity, movement, boundingBox, mc.level, List.of());
         boolean flag = y < 0.0 && collideBoundingBox.y > y + 1.0E-4;
         boolean flag2 = y > 0.0 && collideBoundingBox.y < y - 1.0E-4;
         if (Math.abs(collideBoundingBox.x - x) > 1.0E-4) {
            x = 0.0;
         }

         if (Math.abs(collideBoundingBox.z - z) > 1.0E-4) {
            z = 0.0;
         }

         boundingBox = boundingBox.move(collideBoundingBox);
         aabbs.add(boundingBox);
         y = flag ? -0.0784 : ((flag2 ? 0.0 : y) - 0.08) * 0.98;
      }

      return aabbs;
   }

   private Vec3 method2444() {
      if (this.arrayDeque.size() < 2) {
         return Vec3.ZERO;
      } else {
         Vec3[] vecs = this.arrayDeque.toArray(new Vec3[0]);
         double x = 0.0;
         double y = 0.0;
         double d = 0.0;

         for (int i = 1; i < vecs.length; i++) {
            double d2 = i;
            x += (vecs[i].x - vecs[i - 1].x) * d2;
            y += (vecs[i].z - vecs[i - 1].z) * d2;
            d += d2;
         }

         x /= d;
         y /= d;
         double y2 = vecs[vecs.length - 1].y - vecs[vecs.length - 2].y;
         if (Math.hypot(x, y) < 0.03) {
            x = 0.0;
            y = 0.0;
         }

         return new Vec3(x, y2, y);
      }
   }

   private int method2445() {
      return this.predict.isSelected(3) ? 0 : Mth.clamp(Math.round(this.predictTicks.getValue3()) + Math.round(this.method2447() / 50.0F), 0, 15);
   }

   private int method2446() {
      return Mth.clamp((int)Math.ceil(this.method2447() / 50.0) + 4, 4, 24);
   }

   private int method2447() {
      if (mc.getConnection() == null) {
         return 0;
      } else {
         PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(mc.player.getUUID());
         return playerInfo == null ? 0 : Math.max(0, playerInfo.getLatency());
      }
   }

   private AutoTrapModule.Data4 method2448(AutoTrapModule.Data6 data6) {
      if (this.method2460(data6.getType()) && this.method2455(data6)) {
         if (data6.getType() == AutoTrapModule.Type.PICKUP) {
            return this.method2449(data6.pos());
         } else {
            BlockPos pos = data6.pos();
            Vec3 eyePosition = mc.player.getEyePosition();
            double blockInteractionRange = this.method2468();
            Rotation rotation2 = rotationManager.getRotation2();
            AutoTrapModule.Data4 data4 = null;
            double d = Double.MAX_VALUE;

            for (Direction direction : Direction.values()) {
               BlockPos relative = pos.relative(direction);
               BlockState blockState = mc.level.getBlockState(relative);
               if (this.method2450(data6.getType(), relative, blockState)) {
                  VoxelShape shape = blockState.getShape(mc.level, relative);
                  if (!shape.isEmpty()) {
                     Direction opposite = direction.getOpposite();

                     for (AABB aabb : shape.toAabbs()) {
                        AABB move = aabb.move(relative);

                        for (double alpha1 : CONST_doubleValues) {
                           for (double d2 : CONST_doubleValues) {
                              Vec3 vec = this.method2454(move, opposite, alpha1, d2);
                              if (!(eyePosition.distanceToSqr(vec) > blockInteractionRange * blockInteractionRange)) {
                                 Rotation rotation = RotationUtils.method6913(vec);
                                 BlockHitResult blockHitResult = this.method2453(eyePosition, rotation, blockInteractionRange, Fluid.NONE);
                                 double d3;
                                 if (blockHitResult.getType() == HitResult.Type.BLOCK
                                    && !blockHitResult.isInside()
                                    && blockHitResult.getBlockPos().equals(relative)
                                    && blockHitResult.getDirection() == opposite
                                    && (
                                          d3 = rotation2.method3131(rotation).method5327()
                                             + eyePosition.distanceTo(vec) * 2.0
                                             + (direction == Direction.DOWN ? 0 : 3)
                                             + (Math.abs(alpha1 - 0.5) + Math.abs(d2 - 0.5)) * 6.0
                                       )
                                       < d) {
                                    d = d3;
                                    data4 = new AutoTrapModule.Data4(rotation);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return data4;
         }
      } else {
         return null;
      }
   }

   private AutoTrapModule.Data4 method2449(BlockPos pos) {
      Vec3 eyePosition = mc.player.getEyePosition();
      double d = this.method2468();
      double blockInteractionRange = mc.player.blockInteractionRange();
      Rotation rotation2 = rotationManager.getRotation2();
      AutoTrapModule.Data4 data4 = null;
      double d2 = Double.MAX_VALUE;

      for (double d3 : CONST_doubleValues2) {
         for (double d4 : CONST_doubleValues2) {
            for (double d5 : CONST_doubleValues2) {
               Vec3 vec = new Vec3(pos.getX() + d3, pos.getY() + d5 * 0.85, pos.getZ() + d4);
               if (!(eyePosition.distanceToSqr(vec) > d * d)) {
                  Rotation rotation = RotationUtils.method6913(vec);
                  BlockHitResult blockHitResult = this.method2453(eyePosition, rotation, blockInteractionRange, Fluid.SOURCE_ONLY);
                  double d6;
                  if (blockHitResult.getType() == HitResult.Type.BLOCK && blockHitResult.getBlockPos().equals(pos) && (d6 = rotation2.method3131(rotation).method5327()) < d2) {
                     d2 = d6;
                     data4 = new AutoTrapModule.Data4(rotation);
                  }
               }
            }
         }
      }

      return data4;
   }

   private boolean method2450(AutoTrapModule.Type type, BlockPos relative, BlockState blockState) {
      if (blockState.canBeReplaced()) {
         return false;
      } else {
         return type != AutoTrapModule.Type.FIRE || !CampfireBlock.canLight(blockState) && !CandleBlock.canLight(blockState) && !CandleCakeBlock.canLight(blockState)
            ? type.flag2 || mc.player.isShiftKeyDown() || blockState.getMenuProvider(mc.level, relative) == null
            : false;
      }
   }

   private AutoTrapModule.Data2 method2451() {
      Rotation rotation2 = rotationManager.getRotation2();
      if (rotation2 == null) {
         return null;
      } else {
         Vec3 eyePosition = mc.player.getEyePosition();
         double blockInteractionRange = mc.player.blockInteractionRange();
         BlockHitResult blockHitResult = this.method2453(eyePosition, rotation2, this.method2468(), Fluid.NONE);
         BlockHitResult blockHitResult2 = this.method2453(eyePosition, rotation2, blockInteractionRange, Fluid.NONE);
         BlockHitResult blockHitResult3 = this.method2453(eyePosition, rotation2, blockInteractionRange, Fluid.SOURCE_ONLY);

         for (AutoTrapModule.Data6 data6 : this.items6) {
            if (this.method2460(data6.getType()) && this.method2455(data6)) {
               BlockHitResult blockHitResult4 = switch (data6.getType()) {
                  case LAVA -> blockHitResult2;
                  case PICKUP -> blockHitResult3;
                  default -> blockHitResult;
               };
               if (blockHitResult4.getType() == HitResult.Type.BLOCK
                  && !blockHitResult4.isInside()
                  && this.method2452(data6.getType(), blockHitResult4).equals(data6.pos())
                  && (
                     data6.getType() == AutoTrapModule.Type.PICKUP
                        || mc.level.getBlockState(blockHitResult4.getBlockPos()).canBeReplaced()
                        || this.method2450(data6.getType(), blockHitResult4.getBlockPos(), mc.level.getBlockState(blockHitResult4.getBlockPos()))
                  )) {
                  return new AutoTrapModule.Data2(data6, blockHitResult4);
               }
            }
         }

         return null;
      }
   }

   private BlockPos method2452(AutoTrapModule.Type type, BlockHitResult blockHitResult) {
      return switch (type) {
         case LAVA, FIRE -> blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
         default -> mc.level.getBlockState(blockHitResult.getBlockPos()).canBeReplaced() ? blockHitResult.getBlockPos() : blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
         case PICKUP -> blockHitResult.getBlockPos();
      };
   }

   private BlockHitResult method2453(Vec3 eyePosition, Rotation rotation2, double blockInteractionRange, Fluid fluid) {
      Vec3 to = eyePosition.add(rotation2.method3135().scale(blockInteractionRange));
      return mc.level.clip(new ClipContext(eyePosition, to, Block.OUTLINE, fluid, mc.player));
   }

   private Vec3 method2454(AABB move, Direction opposite, double alpha1, double d) {
      double x = Mth.lerp(alpha1, move.minX, move.maxX);
      double y = Mth.lerp(d, move.minY, move.maxY);
      double z = Mth.lerp(d, move.minZ, move.maxZ);
      double z2 = Mth.lerp(alpha1, move.minZ, move.maxZ);

      return switch (opposite) {
         case UP -> new Vec3(x, move.maxY, z);
         case DOWN -> new Vec3(x, move.minY, z);
         case NORTH -> new Vec3(x, y, move.minZ);
         case SOUTH -> new Vec3(x, y, move.maxZ);
         case WEST -> new Vec3(move.minX, y, z2);
         case EAST -> new Vec3(move.maxX, y, z2);
      };
   }

   private boolean method2455(AutoTrapModule.Data6 data6) {
      BlockPos pos = data6.pos();
      if (!mc.level.isInWorldBounds(pos) || !mc.level.getWorldBorder().isWithinBounds(pos)) {
         return false;
      } else if (data6.getType() == AutoTrapModule.Type.PICKUP) {
         return this.method2458(pos);
      } else {
         boolean containsKey = this.items.containsKey(pos);

         if (containsKey) {
            return false;
         } else {
            BlockState blockState = mc.level.getBlockState(pos);
            switch (data6.getType()) {
               case LAVA:
                  if (!blockState.isAir() && (!blockState.canBeReplaced() || !blockState.getFluidState().isEmpty())) {
                     return false;
                  }

                  if (!this.method2456(pos)) {
                     return false;
                  }
                  break;
               case FIRE:
                  BlockPos below = pos.below();
                  if (!blockState.isAir() || !mc.level.getBlockState(below).isFaceSturdy(mc.level, below, Direction.UP)) {
                     return false;
                  }
                  break;
               default:
                  if (!blockState.canBeReplaced()) {
                     return false;
                  }
            }

            if (this.protectSelf.isEnabled()) {
               AABB inflate = mc.player.getBoundingBox().expandTowards(mc.player.getDeltaMovement()).inflate(0.2, 0.0, 0.2);
               return !inflate.intersects(new AABB(pos));
            } else {
               return true;
            }
         }
      }
   }

   private boolean method2456(BlockPos pos) {
      if (!this.protectSelf.isEnabled()) {
         return true;
      } else {
         double x = pos.getX() + 0.5 - mc.player.getX();
         double y = pos.getZ() + 0.5 - mc.player.getZ();
         return Math.hypot(x, y) >= 3.0 || pos.getY() + 1 < mc.player.getBlockY();
      }
   }

   private boolean method2457(BlockPos pos) {
      AutoTrapModule.Data data = this.items.get(pos);
      if (data != null && data.getType().flag) {
         return true;
      } else {
         BlockState blockState = mc.level.getBlockState(pos);
         return blockState.is(Blocks.COBWEB) || blockState.is(Blocks.POWDER_SNOW);
      }
   }

   private boolean method2458(BlockPos pos) {
      return mc.level.getFluidState(pos).is(Fluids.LAVA);
   }

   private boolean method2459(AutoTrapModule.Data2 data2, int tickCount) {
      AutoTrapModule.Type type = data2.getData6().getType();
      InteractionHand hand = InteractionHand.MAIN_HAND;
      int selectedSlot = -1;
      if (method2462(type, mc.player.getOffhandItem())) {
         hand = InteractionHand.OFF_HAND;
      } else if (!method2462(type, mc.player.getMainHandItem())) {
         int selected;
         if ((selected = this.method2461(type)) == -1) {
            return false;
         }

         selectedSlot = mc.player.getInventory().getSelectedSlot();
         mc.player.getInventory().setSelectedSlot(selected);
      }

      int intValue = PlacementQueue.method2811(hand);
      Item rawType = mc.player.getItemInHand(hand).getItem();

      InteractionResult useItem;
      try {
         useItem = type.flag2
            ? mc.gameMode.useItem(mc.player, hand)
            : mc.gameMode.useItemOn(mc.player, hand, data2.getBlockHitResult());
      } finally {
         if (selectedSlot != -1) {
            mc.player.getInventory().setSelectedSlot(selectedSlot);
            mc.gameMode.ensureHasSentCarriedItem();
         }
      }

      if (!useItem.consumesAction()) {
         return false;
      } else {
         if (type == AutoTrapModule.Type.WEB || type == AutoTrapModule.Type.ANVIL || rawType == Items.FIRE_CHARGE) {
            this.placementQueue.method2812(data2.getData6().pos().asLong(), intValue, rawType, tickCount + this.method2446() + 2);
         }

         if (useItem instanceof Success success && success.swingSource() == SwingSource.PREDICTED) {
            if (this.hideSwing.isEnabled()) {
               mc.getConnection().send(ServerboundPunchPacket.INSTANCE);
            } else {
               org.zenith.utility.game.SwingCompat.swing(mc.player, hand);
            }
         }

         return true;
      }
   }

   private boolean method2460(AutoTrapModule.Type type) {
      return !method2462(type, mc.player.getOffhandItem()) && !method2462(type, mc.player.getMainHandItem()) ? this.method2461(type) != -1 : true;
   }

   private int method2461(AutoTrapModule.Type type) {
      for (int i = 0; i < 9; i++) {
         if (method2462(type, mc.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return -1;
   }

   private static boolean method2462(AutoTrapModule.Type type, ItemStack offhandItem) {
      return switch (type) {
         case WEB -> offhandItem.is(Items.COBWEB);
         case SNOW -> offhandItem.is(Items.POWDER_SNOW_BUCKET);
         case LAVA -> offhandItem.is(Items.LAVA_BUCKET);
         case FIRE -> offhandItem.is(Items.FLINT_AND_STEEL) || offhandItem.is(Items.FIRE_CHARGE);
         case ANVIL -> offhandItem.is(ItemTags.ANVIL);
         case PICKUP -> offhandItem.is(Items.BUCKET);
      };
   }

   private static boolean method2463(AutoTrapModule.Type type, BlockState state) {
      return switch (type) {
         case WEB -> state.is(Blocks.COBWEB);
         case SNOW -> state.is(Blocks.POWDER_SNOW);
         case LAVA -> state.getFluidState().is(Fluids.LAVA);
         case FIRE -> state.is(BlockTags.FIRE);
         case ANVIL -> state.is(BlockTags.ANVIL);
         case PICKUP -> !state.getFluidState().is(Fluids.LAVA);
      };
   }

   private boolean method2464() {
      if (mc.gui.screen() != null || mc.player.isSpectator() || mc.player.isDeadOrDying()) {
         return false;
      } else if (mc.player.containerMenu != mc.player.inventoryMenu) {
         return false;
      } else {
         return this.pauseOnUse.isEnabled() && mc.player.isUsingItem() ? false : !FreeCamModule.INSTANCE.isEnabled();
      }
   }

   private boolean method2465() {
      return !this.activation.isSelected(1) || UseItemUtils.method6521(this.bind);
   }

   private Player method2466() {
      double d = this.range.getValue3() + 2.0;
      LivingEntity livingEntity22 = AuraModule.INSTANCE.getLivingEntity22();
      Player player = null;
      double d2 = Double.MAX_VALUE;

      for (Player player2 : mc.level.players()) {
         double d3;
         double d4;
         if (this.method2467(player2)
            && !((d3 = mc.player.distanceTo(player2)) > d)
            && (d4 = d3 - (player2 == livingEntity22 ? 100 : 0) - (player2 == this.playerEntity ? 0.5 : 0.0)) < d2) {
            d2 = d4;
            player = player2;
         }
      }

      return player;
   }

   private boolean method2467(Player player) {
      return player != mc.player
         && CrystalAuraModule.method5901(player)
         && !Zenith.getInstance().getFriends().method7626(player)
         && !AntiBotModule.INSTANCE.method2785(player);
   }

   private double method2468() {
      return Math.min((double)this.range.getValue3(), mc.player.blockInteractionRange());
   }

   private void method2469(int tickCount) {
      AutoTrapModule.Data3 data3;
      while ((data3 = this.queue.poll()) != null) {
         AutoTrapModule.Data data = this.items.remove(data3.pos());
         if (data != null) {
            long l = data3.pos().asLong();
            if (!method2463(data.getType(), data3.state())) {
               this.placementQueue.method2814(l);
            } else {
               this.placementQueue.method2813(l);
               if (data.getType() == AutoTrapModule.Type.LAVA) {
                  this.items2.put(data3.pos(), tickCount);
               }

               if (data.getType().flag) {
                  this.method2471();
               }

               if (this.render.isEnabled() && data.getType() != AutoTrapModule.Type.PICKUP) {
                  this.items3.add(new AutoTrapModule.Data5(data3.pos(), System.currentTimeMillis()));
               }
            }
         }
      }
   }

   private void method2470(int tickCount) {
      var iterator = this.items.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         if (((AutoTrapModule.Data)entry.getValue()).getIntValue() < tickCount) {
            if (((AutoTrapModule.Data)entry.getValue()).getType() == AutoTrapModule.Type.LAVA && this.method2458((BlockPos)entry.getKey())) {
               this.items2.put((BlockPos)entry.getKey(), tickCount);
            }

            iterator.remove();
         }
      }
   }

   private void method2471() {
      long now;
      if ((now = System.currentTimeMillis()) - this.longValue >= 2000L) {
         this.longValue = now;
         Island.method2878(Zenith.getInstance().getLocalization().translate("hud.island.trap"));
      }
   }

   private void method2472() {
      this.playerEntity = null;
      this.arrayDeque.clear();
      this.items4 = List.of();
      this.items5 = List.of();
   }

   private void reset() {
      this.method2472();
      this.items6 = List.of();
      this.items.clear();
      this.items2.clear();
      this.flag = false;
      this.queue.clear();
      this.items3.clear();
      this.placementQueue.clear();
      this.class33.cancel();
      this.intValue5 = -32768 << 16 | 0;
      this.data2 = null;
      this.blockPos = null;
      this.rotation = null;
      this.intValue = -32768 << 16 | 0;
      this.intValue2 = -32768 << 16 | 0;
      this.intValue3 = -16384 << 16 | 0;
      this.intValue4 = -16384 << 16 | 0;
   }

   private static double method2473(Vec3 vec) {
      return Math.hypot(vec.x, vec.z);
   }

   private static Vec3 method2474(Vec3 vec, float angdeg) {
      double d;
      double d2 = Math.cos(d = Math.toRadians(angdeg));
      double d3 = Math.sin(d);
      return new Vec3(vec.x * d2 - vec.z * d3, vec.y, vec.x * d3 + vec.z * d2);
   }

   private static AABB method2475(BlockPos blockPos, double d) {
      double x = 0.5 * Mth.clamp(d, 0.05, 1.0);
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos);
      return new AABB(atCenterOf.subtract(x, x, x), atCenterOf.add(x, x, x));
   }

   private static final class Data {
      private final AutoTrapModule.Type type;
      private final int intValue;

      public AutoTrapModule.Type type() {
         return this.type;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "type=" + this.type + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(AutoTrapModule.Type type, int intValue) {
         this.type = type;
         this.intValue = intValue;
      }

      public AutoTrapModule.Type getType() {
         return this.type;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data2 {
      private final AutoTrapModule.Data6 data6;
      private final BlockHitResult blockHitResult;

      public AutoTrapModule.Data6 data6() {
         return this.data6;
      }

      public BlockHitResult blockHitResult() {
         return this.blockHitResult;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.data6, other.data6) && java.util.Objects.equals(this.blockHitResult, other.blockHitResult);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data6, blockHitResult);
      }

      @Override
      public String toString() {
         return "Data2[" + "data6=" + this.data6 + ", " + "blockHitResult=" + this.blockHitResult + "]";
      }

      private Data2(AutoTrapModule.Data6 data6, BlockHitResult blockHitResult) {
         this.data6 = data6;
         this.blockHitResult = blockHitResult;
      }

      public AutoTrapModule.Data6 getData6() {
         return this.data6;
      }

      public BlockHitResult getBlockHitResult() {
         return this.blockHitResult;
      }
   }

   private static final class Data3 {
      private final BlockPos blockPos;
      private final BlockState blockState;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public BlockState blockState() {
         return this.blockState;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.blockState, other.blockState);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, blockState);
      }

      @Override
      public String toString() {
         return "Data3[" + "blockPos=" + this.blockPos + ", " + "blockState=" + this.blockState + "]";
      }

      private Data3(BlockPos blockPos, BlockState blockState) {
         this.blockPos = blockPos;
         this.blockState = blockState;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public BlockState state() {
         return this.blockState;
      }
   }

   private static final class Data4 {
      private final Rotation rotation;

      public Rotation rotation() {
         return this.rotation;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation);
      }

      @Override
      public String toString() {
         return "Data4[" + "rotation=" + this.rotation + "]";
      }

      private Data4(Rotation rotation) {
         this.rotation = rotation;
      }
   }

   private static final class Data5 {
      private final BlockPos blockPos;
      private final long longValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, longValue);
      }

      @Override
      public String toString() {
         return "Data5[" + "blockPos=" + this.blockPos + ", " + "longValue=" + this.longValue + "]";
      }

      private Data5(BlockPos blockPos, long longValue) {
         this.blockPos = blockPos;
         this.longValue = longValue;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public long time() {
         return this.longValue;
      }
   }

   private static final class Data6 {
      private final AutoTrapModule.Type type;
      private final BlockPos blockPos;

      public AutoTrapModule.Type type() {
         return this.type;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.blockPos, other.blockPos);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type, blockPos);
      }

      @Override
      public String toString() {
         return "Data6[" + "type=" + this.type + ", " + "blockPos=" + this.blockPos + "]";
      }

      private Data6(AutoTrapModule.Type type, BlockPos blockPos) {
         this.type = type;
         this.blockPos = blockPos;
      }

      public AutoTrapModule.Type getType() {
         return this.type;
      }

      public BlockPos pos() {
         return this.blockPos;
      }
   }

   private static enum Type {
      WEB(true, false),
      SNOW(true, false),
      LAVA(false, true),
      FIRE(false, false),
      ANVIL(false, false),
      PICKUP(false, true);

      private final boolean flag;
      private final boolean flag2;

      private Type(boolean flag, boolean flag2) {
         this.flag = flag;
         this.flag2 = flag2;
      }
   }
}

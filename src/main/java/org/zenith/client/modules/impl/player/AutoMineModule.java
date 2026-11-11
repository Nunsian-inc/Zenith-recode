package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.MineTargetFinder;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.ItemStackTags;
import org.zenith.utility.misc.Vec2f;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "AutoMine",
   category = ModuleCategory.PLAYER,
   description = "module.automine.description"
)
public final class AutoMineModule extends Module {
   public static final AutoMineModule INSTANCE = new AutoMineModule();
   private static final int CONST_intValue = 40;
   private static final Set<Item> items = Set.of(
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
   private static final int CONST_intValue2 = 30;
   private static final int CONST_intValue3 = 150;
   private static final double CONST_doubleValue = 0.3;
   private static final double CONST_doubleValue2 = 0.2;
   private static final int CONST_intValue4 = 48;
   private static final double CONST_doubleValue3 = 6.0;
   private static final double[][] CONST_doubleValues = new double[][]{{0.5, 0.5}, {0.3, 0.3}, {0.7, 0.3}, {0.3, 0.7}, {0.7, 0.7}};
   private final ModeSetting mode = new ModeSetting(
      "module.autoMine.mode", "module.autoMine.mode.desc", "module.autoMine.modeNormal", "module.autoMine.modeNether", "module.autoMine.modeCombined"
   );
   public static final int CONST_intValue5 = 600;
   private MineTargetFinder.Data selectedMine;
   private MineTargetFinder.Data data;
   private int mineDataCooldown;
   private boolean combinedMineSwitched;
   private int ticks;
   private AutoMineModule.Data3 data3;
   private BlockPos blockPos;
   private boolean flag;
   private int intValue;
   private final List<AutoMineModule.Entry> items2 = new ArrayList<>();
   private int intValue2 = 2;
   private int intValue3 = -1;
   private boolean flag2;
   private boolean flag3;
   private int intValue4;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;

   private AutoMineModule() {
   }

   @Override
   public void onEnable() {
      this.selectedMine = null;
      this.data = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.items2.clear();
      this.intValue2 = 2;
      this.intValue3 = -1;
      this.flag2 = false;
      this.intValue4 = 0;
      this.method4388();
      ChatLogger.method2019("Смените версию на 1.17.1–1.20.6 в ViaFabric, если вы этого не сделали");
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.releaseUse();
      if (mc.player != null) {
         this.method4358();
      }

      super.onDisable();
   }

   public boolean method4352() {
      return this.flag && this.isEnabled();
   }

   @EventTarget
   public void method4353(PreRotationEvent event) {
      if (mc.player != null && mc.level != null) {
         this.ticks++;
         this.flag = false;
         var datas = this.onTeleportation();
         if (datas != null && this.data != null) {
            this.method4373();
            if (this.method4377() || !this.method4379()) {
               this.method4356(null);
            } else if (this.intValue > 0) {
               this.intValue--;
               this.method4356(null);
            } else {
               Vec3 eyePosition = mc.player.getEyePosition();
               double blockInteractionRange = mc.player.blockInteractionRange();
               Rotation rotation = rotationManager.getEffectiveRotation();
               if (this.data3 != null) {
                  this.data3 = this.method4361(this.data3, eyePosition, blockInteractionRange, rotation);
               }

               if (this.data3 == null) {
                  this.data3 = this.method4360(eyePosition, blockInteractionRange, rotation);
               }

               if (this.data3 == null) {
                  this.data3 = this.method4362(eyePosition, blockInteractionRange, rotation);
               }

               this.method4356(this.data3 == null ? null : this.method4359(this.data3, eyePosition, blockInteractionRange, rotation));
            }
         } else {
            this.data = null;
            this.method4388();
            this.method4358();
         }
      }
   }

   @EventTarget
   public void method4354(PostRotationEvent event) {
      this.flag = false;
      this.blockPos = null;
      if (mc.player != null && this.data != null && !this.flag2 && this.intValue <= 0) {
         ItemStack mainHandItem = mc.player.getMainHandItem();
         if (ItemStackTags.isPickaxe(mainHandItem) && method4380(mainHandItem) >= 30) {
            if (mc.missTime <= 0) {
               this.flag = true;
               if (mc.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
                  this.blockPos = blockHitResult.getBlockPos().immutable();
               }
            }
         }
      }
   }

   @EventTarget
   public void method4355(MoveInputEvent event) {
      if (!zenith.getHolyWorldServerTracker().isActive() && mc.player != null) {
         event.setForward(true);
      }
   }

   private void method4356(Rotation rotation) {
      Rotation rotation2 = rotation != null ? rotation : rotationManager.getEffectiveRotation();
      rotationManager.requestRotation(new RotationRequest(rotation2, () -> this.method4357(rotation2), bcCameraTestnerv.getRotationConfig()), 1, this);
   }

   private Rotation method4357(Rotation rotation) {
      Rotation rotation2 = rotationManager.getEffectiveRotation();
      Vec2f vec2f = rotation2.wrappedDeltaTo(rotation);
      ThreadLocalRandom current = ThreadLocalRandom.current();
      float hypot = (float)Math.hypot(vec2f.getFloatValue(), vec2f.getFloatValue2());
      float f = 0.0F;
      float f2 = 0.0F;
      if (hypot > 0.2F) {
         float f3 = Math.min(hypot * (0.5F + current.nextFloat() * 0.18F), 45.0F);
         f = vec2f.getFloatValue() / hypot * f3;
         f2 = vec2f.getFloatValue2() / hypot * f3 * 0.92F;
      }

      float max = 7.0F + hypot * 0.2F;
      this.floatValue = this.floatValue + Mth.clamp(f - this.floatValue, -max, max);
      this.floatValue2 = this.floatValue2 + Mth.clamp(f2 - this.floatValue2, -max, max);
      if (hypot <= 0.2F && Math.abs(this.floatValue) + Math.abs(this.floatValue2) < 0.3F) {
         this.floatValue = this.floatValue2 = 0.0F;
         return rotation2;
      } else {
         float f3 = 0.05F + (Math.abs(this.floatValue) + Math.abs(this.floatValue2)) * 0.012F;
         float f4 = this.floatValue + (float)current.nextGaussian() * f3 + this.floatValue3;
         float f5 = this.floatValue2 + (float)current.nextGaussian() * f3 * 0.7F + this.floatValue4;
         float f6;
         if (!((f6 = Rotation.sensitivityStep()) > 0.0F) || Float.isInfinite(f6)) {
            f6 = 0.15F;
         }

         float f7 = Math.round(f4 / f6) * f6;
         float f8 = Math.round(f5 / f6) * f6;
         this.floatValue3 = f4 - f7;
         this.floatValue4 = f5 - f8;
         return new Rotation(rotation2.getFloatValue() + f7, rotation2.getFloatValue2() + f8, true);
      }
   }

   private void method4358() {
      Rotation currentRotation = rotationManager.getCurrentRotation();
      if (currentRotation != null && rotationManager.getModule() == this) {
         mc.player.setYRot(currentRotation.getFloatValue());
         mc.player.setXRot(currentRotation.getFloatValue2());
         this.floatValue = this.floatValue2 = this.floatValue3 = this.floatValue4 = 0.0F;
      }
   }

   private Rotation method4359(AutoMineModule.Data3 data3, Vec3 eyePosition, double blockInteractionRange, Rotation rotation) {
      if (method4386(method4385(eyePosition, blockInteractionRange, rotation), data3.pos())) {
         return null;
      } else {
         Rotation rotation2 = Rotation.lookingAt(data3.getVec3d(), eyePosition);
         return rotation.add(rotation.wrappedDeltaTo(rotation2));
      }
   }

   private AutoMineModule.Data3 method4360(Vec3 eyePosition, double blockInteractionRange, Rotation rotation) {
      BlockHitResult blockHitResult = method4385(eyePosition, blockInteractionRange, rotation);
      if (blockHitResult != null && blockHitResult.getType() == Type.BLOCK) {
         BlockPos blockPos = blockHitResult.getBlockPos();
         return this.method4382(blockPos) && this.method4372(blockPos) ? new AutoMineModule.Data3(blockPos.immutable(), blockHitResult.getLocation()) : null;
      } else {
         return null;
      }
   }

   private AutoMineModule.Data3 method4361(AutoMineModule.Data3 data3, Vec3 eyePosition, double blockInteractionRange, Rotation rotation) {
      BlockPos pos = data3.pos();
      if (this.method4382(pos) && method4384(pos, eyePosition, blockInteractionRange)) {
         if (method4386(method4385(eyePosition, blockInteractionRange, rotation), pos)) {
            return data3;
         } else if (this.method4364(pos, data3.getVec3d(), eyePosition, blockInteractionRange)) {
            return data3;
         } else {
            Vec3 vec = rotation.toDirection();
            AutoMineModule.Data3 data32 = null;
            double d = Double.MAX_VALUE;

            for (Direction direction : Direction.values()) {
               if (this.method4366(pos, direction, eyePosition)) {
                  AutoMineModule.Data3 data33 = this.method4363(pos, direction, method4367(pos, direction, eyePosition, vec), eyePosition, blockInteractionRange, rotation);
                  double d2;
                  if (data33 != null && (d2 = method4365(rotation, Rotation.lookingAt(data33.getVec3d(), eyePosition))) < d) {
                     d = d2;
                     data32 = data33;
                  }
               }
            }

            return data32;
         }
      } else {
         return null;
      }
   }

   private AutoMineModule.Data3 method4362(Vec3 eyePosition, double blockInteractionRange, Rotation rotation) {
      MineTargetFinder.Data data = this.data;
      int n = (int)Math.ceil(blockInteractionRange) + 1;
      BlockPos containing = BlockPos.containing(eyePosition);
      int n2 = Math.max(data.getIntValue(), containing.getX() - n);
      int n3 = Math.min(data.getIntValue2(), containing.getX() + n);
      int n4 = Math.max(data.getIntValue3(), containing.getY() - n);
      int n5 = Math.min(data.getIntValue4(), containing.getY() + n);
      int n6 = Math.max(data.getIntValue5(), containing.getZ() - n);
      int n7 = Math.min(data.getIntValue6(), containing.getZ() + n);
      if (n2 <= n3 && n4 <= n5 && n6 <= n7) {
         Vec3 vec = rotation.toDirection();
         ArrayList<AutoMineModule.Data2> data2s = new ArrayList();
         MutableBlockPos mutablePos = new MutableBlockPos();

         for (int i = n2; i <= n3; i++) {
            for (int j = n4; j <= n5; j++) {
               for (int k = n6; k <= n7; k++) {
                  mutablePos.set(i, j, k);
                  if (this.method4382(mutablePos) && method4384(mutablePos, eyePosition, blockInteractionRange)) {
                     BlockPos immutable = mutablePos.immutable();
                     if (this.method4372(immutable)) {
                        double d = Math.sqrt(MineTargetFinder.method1939(eyePosition, immutable)) * 6.0;

                        for (Direction direction : Direction.values()) {
                           if (this.method4366(immutable, direction, eyePosition)) {
                              Vec3 vec3d = method4367(immutable, direction, eyePosition, vec);
                              data2s.add(new AutoMineModule.Data2(immutable, direction, vec3d, d + method4365(rotation, Rotation.lookingAt(vec3d, eyePosition))));
                           }
                        }
                     }
                  }
               }
            }
         }

         data2s.sort(Comparator.comparingDouble(AutoMineModule.Data2::getDoubleValue));
         int n8 = 0;

         for (AutoMineModule.Data2 data2 : data2s) {
            if (n8++ >= 48) {
               break;
            }

            AutoMineModule.Data3 data3 = this.method4363(data2.pos(), data2.getDirection(), data2.getVec3d(), eyePosition, blockInteractionRange, rotation);
            if (data3 != null) {
               return data3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private AutoMineModule.Data3 method4363(BlockPos pos, Direction direction, Vec3 vec3d, Vec3 eyePosition, double blockInteractionRange, Rotation rotation) {
      ArrayList<Vec3> vecs = new ArrayList(CONST_doubleValues.length + 1);
      vecs.add(vec3d);

      for (double[] doubles : CONST_doubleValues) {
         vecs.add(method4368(pos, direction, doubles[0], doubles[1]));
      }

      vecs.subList(1, vecs.size()).sort(Comparator.comparingDouble(var2x -> method4365(rotation, Rotation.lookingAt(var2x, eyePosition))));

      for (Vec3 vec : vecs) {
         if (this.method4364(pos, vec, eyePosition, blockInteractionRange)) {
            return new AutoMineModule.Data3(pos, vec);
         }
      }

      return null;
   }

   private boolean method4364(BlockPos pos, Vec3 vec3d, Vec3 eyePosition, double blockInteractionRange) {
      BlockHitResult blockHitResult = method4385(eyePosition, blockInteractionRange, Rotation.lookingAt(vec3d, eyePosition));
      return method4386(blockHitResult, pos) && !this.method4387(eyePosition, blockHitResult.getLocation());
   }

   private static double method4365(Rotation rotation, Rotation rotation2) {
      Vec2f vec2f = rotation.wrappedDeltaTo(rotation2);
      return Math.abs(vec2f.getFloatValue()) + Math.abs(vec2f.getFloatValue2()) * 1.5;
   }

   private boolean method4366(BlockPos pos, Direction direction, Vec3 eyePosition) {
      Vec3i unitVec3i = direction.getUnitVec3i();
      Vec3 vec = net.minecraft.world.phys.Vec3.atCenterOf(pos).add(unitVec3i.getX() * 0.5, unitVec3i.getY() * 0.5, unitVec3i.getZ() * 0.5);
      return (eyePosition.x - vec.x) * unitVec3i.getX() + (eyePosition.y - vec.y) * unitVec3i.getY() + (eyePosition.z - vec.z) * unitVec3i.getZ() <= 0.02
         ? false
         : !mc.level.getBlockState(pos.relative(direction)).isSolidRender();
   }

   private static Vec3 method4367(BlockPos pos, Direction direction, Vec3 eyePosition, Vec3 vec) {
      Axis axis = direction.getAxis();
      double d = method4369(pos, direction);
      double choose = axis.choose(vec.x, vec.y, vec.z);
      Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      double scale;
      if (Math.abs(choose) > 1.0E-4 && (scale = (d - axis.choose(eyePosition.x, eyePosition.y, eyePosition.z)) / choose) > 0.0) {
         atCenterOf = eyePosition.add(vec.scale(scale));
      }

      double d2;
      switch (axis) {
         case X:
            scale = atCenterOf.y - pos.getY();
            d2 = atCenterOf.z - pos.getZ();
            break;
         case Y:
            scale = atCenterOf.x - pos.getX();
            d2 = atCenterOf.z - pos.getZ();
            break;
         default:
            scale = atCenterOf.x - pos.getX();
            d2 = atCenterOf.y - pos.getY();
      }

      return method4368(pos, direction, Mth.clamp(scale, 0.2, 0.8), Mth.clamp(d2, 0.2, 0.8));
   }

   private static Vec3 method4368(BlockPos pos, Direction direction, double d, double d2) {
      double x = method4369(pos, direction) - direction.getAxisDirection().getStep() * 0.01;

      return switch (direction.getAxis()) {
         case X -> new Vec3(x, pos.getY() + d, pos.getZ() + d2);
         case Y -> new Vec3(pos.getX() + d, x, pos.getZ() + d2);
         case Z -> new Vec3(pos.getX() + d, pos.getY() + d2, x);
      };
   }

   private static double method4369(BlockPos pos, Direction direction) {
      int choose = direction.getAxis().choose(pos.getX(), pos.getY(), pos.getZ());
      return direction.getAxisDirection() == AxisDirection.POSITIVE ? choose + 1 : choose;
   }

   private Collection<BlockPos> method4370(BlockPos blockPos) {
      return this.intValue2 > 0 ? method4371(blockPos) : List.of(blockPos);
   }

   private static List<BlockPos> method4371(BlockPos blockPos) {
      ArrayList arrayList = new ArrayList(27);

      for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
         arrayList.add(pos.immutable());
      }

      return arrayList;
   }

   private boolean method4372(BlockPos blockPos) {
      if (!mc.player.onGround()) {
         return true;
      } else {
         var poses = this.method4370(blockPos);
         BlockPos blockPosition = mc.player.blockPosition();
         if (!poses.contains(blockPosition.below())) {
            return true;
         } else {
            MutableBlockPos mutable = blockPosition.below().mutable();
            int n = 0;

            while (n <= 3) {
               if (!poses.contains(mutable) && this.method4383(mutable)) {
                  return true;
               }

               n++;
               mutable.move(Direction.DOWN);
            }

            return false;
         }
      }
   }

   private void method4373() {
      if (this.blockPos != null && mc.level.getBlockState(this.blockPos).isAir()) {
         this.method4374(this.blockPos);
      }

      this.blockPos = null;
      var iterator = this.items2.iterator();

      while (iterator.hasNext()) {
         AutoMineModule.Entry entry = (AutoMineModule.Entry)iterator.next();
         entry.getItems().removeIf(var1x -> {
            if (!mc.level.getBlockState(var1x).isAir()) {
               return false;
            } else {
               entry.flag2 = true;
               return true;
            }
         });
         var items = entry.getItems();
         if ((items.isEmpty()) || this.ticks >= entry.getIntValue()) {
            this.intValue2 = entry.flag2 ? Math.min(3, this.intValue2 + 1) : Math.max(-2, this.intValue2 - 1);
            iterator.remove();
         }
      }
   }

   private void method4374(BlockPos blockPos) {
      if (this.data3 != null && this.data3.pos().equals(blockPos)) {
         this.data3 = null;
      }

      ThreadLocalRandom current = ThreadLocalRandom.current();
      if (current.nextFloat() < 0.005F) {
         this.intValue = 6 + current.nextInt(10);
      }

      if (MineTargetFinder.method1935(this.data, blockPos)) {
         HashSet items = new HashSet();

         for (BlockPos pos : method4371(blockPos)) {
            if (!pos.equals(blockPos) && MineTargetFinder.method1935(this.data, pos) && !mc.level.getBlockState(pos).isAir()) {
               items.add(pos);
            }
         }

         if (!items.isEmpty()) {
            this.items2.add(new AutoMineModule.Entry(items, this.ticks + this.method4375(), this.intValue2 > 0));
         }
      }
   }

   private int method4375() {
      PlayerInfo playerInfo = mc.getConnection() == null ? null : mc.getConnection().getPlayerInfo(mc.player.getUUID());
      return Mth.clamp((playerInfo == null ? 100 : playerInfo.getLatency()) * 2 / 50 + 4, 4, 30);
   }

   private boolean method4376(BlockPos blockPos) {
      for (AutoMineModule.Entry entry : this.items2) {
         if (entry.isFlag() && entry.getItems().contains(blockPos)) {
            return true;
         }
      }

      return false;
   }

   private boolean method4377() {
      Inventory inventory = mc.player.getInventory();
      if (!this.flag2) {
         ItemStack mainHandItem = mc.player.getMainHandItem();
         if (ItemStackTags.isPickaxe(mainHandItem) && method4380(mainHandItem) < 30) {
            this.flag2 = true;
            this.intValue3 = inventory.getSelectedSlot();
            this.intValue4 = 2 + ThreadLocalRandom.current().nextInt(3);
            this.data3 = null;
            return true;
         } else {
            return false;
         }
      } else if (this.intValue4 > 0) {
         this.intValue4--;
         return true;
      } else {
         ItemStack mainHandItem = Inventory.isHotbarSlot(this.intValue3) ? inventory.getItem(this.intValue3) : ItemStack.EMPTY;
         if (ItemStackTags.isPickaxe(mainHandItem) && method4380(mainHandItem) < 150 && mainHandItem.getDamageValue() != 0) {
            if (mc.player.getOffhandItem().is(Items.EXPERIENCE_BOTTLE)) {
               if (inventory.getSelectedSlot() != this.intValue3) {
                  inventory.setSelectedSlot(this.intValue3);
                  this.intValue4 = 2 + ThreadLocalRandom.current().nextInt(3);
                  return true;
               } else {
                  mc.options.keyUse.setDown(true);
                  this.flag3 = true;
                  return true;
               }
            } else {
               this.releaseUse();

               for (int i = 0; i < 9; i++) {
                  if (inventory.getItem(i).is(Items.EXPERIENCE_BOTTLE)) {
                     if (inventory.getSelectedSlot() != i) {
                        inventory.setSelectedSlot(i);
                        this.intValue4 = 2 + ThreadLocalRandom.current().nextInt(3);
                        return true;
                     }

                     mc.getConnection().send(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                     this.intValue4 = 6 + ThreadLocalRandom.current().nextInt(4);
                     return true;
                  }
               }

               Slot slot = InventoryUtils.method906(Items.EXPERIENCE_BOTTLE);
               if (slot == null) {
                  ChatLogger.method2019("AutoMine: закончились пузырьки опыта");
                  this.method4378();
                  this.toggle();
                  return true;
               } else {
                  InventoryUtils.method900(slot, InteractionHand.OFF_HAND, true);
                  this.intValue4 = 6 + ThreadLocalRandom.current().nextInt(4);
                  return true;
               }
            }
         } else {
            this.method4378();
            return false;
         }
      }
   }

   private void method4378() {
      this.releaseUse();
      this.flag2 = false;
      if (Inventory.isHotbarSlot(this.intValue3) && mc.player.getInventory().getSelectedSlot() != this.intValue3) {
         mc.player.getInventory().setSelectedSlot(this.intValue3);
      }
   }

   private void releaseUse() {
      if (this.flag3) {
         mc.options.keyUse.setDown(false);
         this.flag3 = false;
      }
   }

   private boolean method4379() {
      Inventory inventory = mc.player.getInventory();
      ItemStack mainHandItem = mc.player.getMainHandItem();
      if (ItemStackTags.isPickaxe(mainHandItem) && method4380(mainHandItem) >= 30) {
         this.intValue3 = inventory.getSelectedSlot();
         return true;
      } else {
         int selected = -1;

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getItem(i);
            if (ItemStackTags.isPickaxe(stack) && method4380(stack) >= 30 && (selected == -1 || i == this.intValue3)) {
               selected = i;
            }
         }

         if (selected >= 0) {
            inventory.setSelectedSlot(selected);
         }

         return false;
      }
   }

   private static int method4380(ItemStack mainHandItem) {
      return mainHandItem.isDamageableItem() ? mainHandItem.getMaxDamage() - mainHandItem.getDamageValue() : 32767 << 16 | (char)-1;
   }

   private boolean method4381(BlockPos blockPos, BlockState blockState) {
      return !blockState.isAir() && blockState.getFluidState().isEmpty() && blockState.getDestroySpeed(mc.level, blockPos) >= 0.0F && !blockState.is(Blocks.OBSIDIAN);
   }

   private boolean method4382(BlockPos blockPos) {
      return this.data != null && MineTargetFinder.method1935(this.data, blockPos) && this.method4381(blockPos, mc.level.getBlockState(blockPos)) && !this.method4376(blockPos);
   }

   private boolean method4383(BlockPos mutable) {
      return !mc.level.getBlockState(mutable).getCollisionShape(mc.level, mutable).isEmpty();
   }

   private static boolean method4384(BlockPos pos, Vec3 eyePosition, double blockInteractionRange) {
      double d = blockInteractionRange - 0.3;
      return MineTargetFinder.method1939(eyePosition, pos) <= d * d;
   }

   private static BlockHitResult method4385(Vec3 eyePosition, double blockInteractionRange, Rotation rotation) {
      return TargetUtils.method3205(eyePosition, blockInteractionRange, rotation, false);
   }

   private static boolean method4386(BlockHitResult blockHitResult, BlockPos pos) {
      return blockHitResult != null && blockHitResult.getType() == Type.BLOCK && pos.equals(blockHitResult.getBlockPos());
   }

   private boolean method4387(Vec3 eyePosition, Vec3 location) {
      AABB inflate = new AABB(eyePosition, location).inflate(1.0);

      for (Entity entity : mc.level.getEntities(mc.player, inflate, entity2 -> !entity2.isSpectator() && entity2.isPickable())) {
         if (entity.getBoundingBox().inflate(entity.getPickRadius()).clip(eyePosition, location).isPresent()) {
            return true;
         }
      }

      return false;
   }

   private void method4388() {
      this.data3 = null;
      this.blockPos = null;
      this.flag = false;
      this.intValue = 0;
      this.floatValue = this.floatValue2 = this.floatValue3 = this.floatValue4 = 0.0F;
      if (this.flag2 && mc.player != null) {
         this.method4378();
      }

      this.releaseUse();
   }

   private void onDrop() {
      if (mc.gui.screen() != null && mc.gui.screen().getTitle().getString().toLowerCase(Locale.ROOT).contains("для продажи")) {
         if (mc.player.containerMenu instanceof ChestMenu chestMenu) {
            int containerSlotCount;
            if (this.moveBuyerResources() && (containerSlotCount = this.getContainerSlotCount(chestMenu)) > 0) {
               InventoryUtils.method904(containerSlotCount - 1, 0, ContainerInput.QUICK_MOVE, false);
            }

            Slot trashSlot = this.findTrashSlot();
            if (trashSlot != null) {
               InventoryUtils.method903(trashSlot, 1, ContainerInput.THROW, false);
            }
         }
      }
   }

   private boolean moveBuyerResources() {
      boolean flag = false;
      int n = 0;

      for (Slot slot : mc.player.containerMenu.slots) {
         if (this.isPlayerInventorySlot(slot) && this.isBuyerResource(slot.getItem())) {
            if (n > 5) {
               break;
            }

            InventoryUtils.method903(slot, 0, ContainerInput.QUICK_MOVE, false);
            n++;
            flag = true;
         }
      }

      return flag;
   }

   private boolean isBuyerResource(ItemStack stack) {
      return !stack.isEmpty() && items.contains(stack.getItem());
   }

   private Slot findTrashSlot() {
      return mc.player
         .containerMenu
         .slots
         .stream()
         .filter(this::isPlayerInventorySlot)
         .filter(slot -> this.isTrash(slot.getItem()))
         .findFirst()
         .orElse(null);
   }

   private boolean isPlayerInventorySlot(Slot slot) {
      if (mc.player.containerMenu instanceof ChestMenu chestMenu) {
         int containerSlotCount = this.getContainerSlotCount(chestMenu);
         return slot.container == mc.player.getInventory() || slot.index >= containerSlotCount && slot.index < containerSlotCount + 36;
      } else {
         return slot.container == mc.player.getInventory() && slot.index >= 9 && slot.index <= 44;
      }
   }

   private int getContainerSlotCount(ChestMenu chestMenu) {
      return Math.min(chestMenu.getContainer().getContainerSize(), Math.max(0, mc.player.containerMenu.slots.size() - 36));
   }

   private boolean isTrash(ItemStack stack) {
      return !stack.isEmpty() && !stack.is(Items.PLAYER_HEAD) && !stack.is(Items.TRIPWIRE_HOOK) && !this.isBuyerResource(stack)
         ? stack.getItem() instanceof BlockItem blockItem && !MineTargetFinder.method1934(blockItem.getBlock())
         : false;
   }

   private List<AutoMineModule.Data> onTeleportation() {
      this.data = null;
      if (zenith.getHolyWorldServerTracker().isActive()) {
         return null;
      } else if (zenith.getHolyWorldServerTracker().currentAnarchyHere() == -1) {
         this.reconnectToRandomAnarchy();
         return null;
      } else if (this.isWaitingForMineData()) {
         return null;
      } else if (this.mode.method2101("module.autoMine.modeCombined")) {
         return this.selectCombinedMine();
      } else {
         this.combinedMineSwitched = false;
         this.selectedMine = null;
         MineTargetFinder.Data selectedMine = MineTargetFinder.method1933(this.mode.method2101("module.autoMine.modeNether"));
         if (MineTargetFinder.method1938(selectedMine, mc.level) && !(this.getMineDistance(selectedMine) > 25.0)) {
            return this.getAvailableMine(selectedMine);
         } else {
            this.handleWarpTp();
            return null;
         }
      }
   }

   private List<AutoMineModule.Data> selectCombinedMine() {
      MineTargetFinder.Data data = MineTargetFinder.method1933(false);
      MineTargetFinder.Data data2 = MineTargetFinder.method1933(true);
      if (MineTargetFinder.method1938(data, mc.level) && MineTargetFinder.method1938(data2, mc.level)) {
         var arena = this.getArena(data);
         var datas = this.getArena(data2);
         boolean mineAvailable = this.isMineAvailable(arena);
         boolean flag = this.isMineAvailable(datas);
         if (this.selectedMine != null) {
            if (this.getMineDistance(this.selectedMine) > 25.0) {
               this.handleWarpTp();
               return null;
            }

            boolean flag2;
            if ((flag2 = this.selectedMine.equals(data2)) && flag) {
               return this.method4389(data2, datas);
            }

            if (!flag2 && mineAvailable) {
               return this.method4389(data, arena);
            }

            if (this.combinedMineSwitched) {
               this.reconnectToRandomAnarchy();
               return null;
            }
         }

         if (!mineAvailable && !flag) {
            MineTargetFinder.Data selectedMine = this.selectedMine == null ? data : this.selectedMine;
            if (MineTargetFinder.method1938(selectedMine, mc.level) && !(this.getMineDistance(selectedMine) > 25.0)) {
               this.reconnectToRandomAnarchy();
               return null;
            } else {
               this.handleWarpTp();
               return null;
            }
         } else {
            MineTargetFinder.Data selectedMine = this.selectedMine;
            boolean flag2 = flag && (!mineAvailable || this.getFillPercentage(datas) > this.getFillPercentage(arena));
            this.selectedMine = flag2 ? data2 : data;
            if (!this.selectedMine.equals(selectedMine)) {
               this.combinedMineSwitched = selectedMine != null;
               this.sendWarpCommand();
               return null;
            } else {
               return flag2 ? this.method4389(data2, datas) : this.method4389(data, arena);
            }
         }
      } else {
         this.handleWarpTp();
         return null;
      }
   }

   private List<AutoMineModule.Data> method4389(MineTargetFinder.Data data, List<AutoMineModule.Data> arena) {
      this.data = data;
      return arena;
   }

   private boolean isWaitingForMineData() {
      if (mc.player.tickCount >= 40 && this.mineDataCooldown <= 0) {
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
      return this.mode.method2101("module.autoMine.modeCombined") && this.selectedMine != null
         ? this.selectedMine
         : MineTargetFinder.method1933(this.mode.method2101("module.autoMine.modeNether"));
   }

   private boolean hasLoadedMineBlocks(MineTargetFinder.Data expectedMineBounds) {
      for (BlockPos pos : BlockPos.betweenClosed(
         expectedMineBounds.getIntValue(), expectedMineBounds.getIntValue3(), expectedMineBounds.getIntValue5(), expectedMineBounds.getIntValue2(), expectedMineBounds.getIntValue4(), expectedMineBounds.getIntValue6()
      )) {
         if (mc.level.hasChunkAt(pos) && !mc.level.getBlockState(pos).isAir()) {
            return true;
         }
      }

      return false;
   }

   private double getMineDistance(MineTargetFinder.Data selectedMine) {
      double d = (selectedMine.getIntValue() + selectedMine.getIntValue2() + 1.0) * 0.5;
      double d2 = (selectedMine.getIntValue5() + selectedMine.getIntValue6() + 1.0) * 0.5;
      double x = Math.abs(mc.player.getX() - d);
      double y = Math.abs(mc.player.getZ() - d2);
      return Math.hypot(x, y);
   }

   private List<AutoMineModule.Data> getAvailableMine(MineTargetFinder.Data selectedMine) {
      var arena = this.getArena(selectedMine);
      if (!this.isMineAvailable(arena)) {
         this.reconnectToRandomAnarchy();
         return null;
      } else {
         return this.method4389(selectedMine, arena);
      }
   }

   private boolean isMineAvailable(List<AutoMineModule.Data> arena) {
      return arena.size() - this.countAirBlocks(arena) < 600L ? false : !mc.player.onGround() || this.hasReachableBlockByHeight(arena);
   }

   private boolean hasReachableBlockByHeight(List<AutoMineModule.Data> arena) {
      int n = Mth.floor(mc.player.getEyeY() + mc.player.blockInteractionRange() - 1.0);
      return arena.stream().anyMatch(var1x -> !var1x.state().isAir() && !var1x.state().is(Blocks.OBSIDIAN) && var1x.pos().getY() <= n);
   }

   private long countAirBlocks(List<AutoMineModule.Data> arena) {
      return arena.stream().filter(data -> data.state().isAir()).count();
   }

   private double getFillPercentage(List<AutoMineModule.Data> arena) {
      return (arena.isEmpty()) ? 0.0 : 1.0 - (double)this.countAirBlocks(arena) / arena.size();
   }

   private List<AutoMineModule.Data> getArena(MineTargetFinder.Data data) {
      return this.getBlocksInZone(
         new BlockPos(data.getIntValue(), data.getIntValue3(), data.getIntValue5()),
         new BlockPos(data.getIntValue2(), data.getIntValue4(), data.getIntValue6())
      );
   }

   private List<AutoMineModule.Data> getBlocksInZone(BlockPos pos, BlockPos pos2) {
      ArrayList<AutoMineModule.Data> datas = new ArrayList<>();
      int n = Math.min(pos.getX(), pos2.getX());
      int n2 = Math.max(pos.getX(), pos2.getX());
      int n3 = Math.min(pos.getY(), pos2.getY());
      int n4 = Math.max(pos.getY(), pos2.getY());
      int n5 = Math.min(pos.getZ(), pos2.getZ());
      int n6 = Math.max(pos.getZ(), pos2.getZ());

      for (int i = n; i <= n2; i++) {
         for (int j = n3; j <= n4; j++) {
            for (int k = n5; k <= n6; k++) {
               BlockPos blockPos = new BlockPos(i, j, k);
               datas.add(new AutoMineModule.Data(blockPos, mc.level.getBlockState(blockPos)));
            }
         }
      }

      return datas;
   }

   public void handleWarpTp() {
      if (mc.player.tickCount % 60 == 0) {
         this.sendWarpCommand();
      }
   }

   private void sendWarpCommand() {
      boolean flag = this.mode.method2101("module.autoMine.modeCombined")
         ? MineTargetFinder.method1933(true).equals(this.selectedMine)
         : this.mode.method2101("module.autoMine.modeNether");
      mc.getConnection().sendCommand(flag ? "warp mine2" : "warp mine");
      this.mineDataCooldown = 20;
   }

   private void reconnectToRandomAnarchy() {
      this.selectedMine = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.items2.clear();
      this.method4388();
      int currentAnarchyHere = zenith.getHolyWorldServerTracker().currentAnarchyHere();

      int nextInt;
      do {
         while ((nextInt = ThreadLocalRandom.current().nextInt(1, 64)) == currentAnarchyHere) {
         }
      } while (nextInt == 2 || nextInt == 17 || nextInt == 33 || nextInt == 49);

      zenith.getHolyWorldServerTracker().reconnect(nextInt);
   }

   @Generated
   public ModeSetting getMineMode() {
      return this.mode;
   }

   private static final class Data {
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.blockState, other.blockState);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, blockState);
      }

      @Override
      public String toString() {
         return "Data[" + "blockPos=" + this.blockPos + ", " + "blockState=" + this.blockState + "]";
      }

      private Data(BlockPos blockPos, BlockState blockState) {
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

   private static final class Data2 {
      private final BlockPos blockPos;
      private final Direction direction;
      private final Vec3 vec3d;
      private final double doubleValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Direction direction() {
         return this.direction;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.direction, other.direction) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, direction, vec3d, doubleValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "blockPos=" + this.blockPos + ", " + "direction=" + this.direction + ", " + "vec3d=" + this.vec3d + ", " + "doubleValue=" + this.doubleValue + "]";
      }

      private Data2(BlockPos blockPos, Direction direction, Vec3 vec3d, double doubleValue) {
         this.blockPos = blockPos;
         this.direction = direction;
         this.vec3d = vec3d;
         this.doubleValue = doubleValue;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public Direction getDirection() {
         return this.direction;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }
   }

   private static final class Data3 {
      private final BlockPos blockPos;
      private final Vec3 vec3d;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.vec3d, other.vec3d);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, vec3d);
      }

      @Override
      public String toString() {
         return "Data3[" + "blockPos=" + this.blockPos + ", " + "vec3d=" + this.vec3d + "]";
      }

      private Data3(BlockPos blockPos, Vec3 vec3d) {
         this.blockPos = blockPos;
         this.vec3d = vec3d;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }
   }

   private static final class Entry {
      private final Set<BlockPos> items;
      private final int intValue;
      private final boolean flag;
      private boolean flag2;

      private Entry(Set<BlockPos> items, int intValue, boolean flag) {
         this.items = items;
         this.intValue = intValue;
         this.flag = flag;
      }

      public Set<BlockPos> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.stream.Stream;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "Spider",
   category = ModuleCategory.MOVEMENT,
   description = "module.spider.description"
)
public final class SpiderModule extends Module {
   public static final SpiderModule INSTANCE = new SpiderModule();
   private static final double CONST_doubleValue = 0.3F;
   private static final int CONST_intValue = 44;
   private final ModeSetting mode = new ModeSetting("module.spider.mode", "module.spider.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.spider.mode.blocks").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.spider.mode.water");
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.mode, "module.spider.mode.head");
   private long longValue;
   private int intValue = -1;
   private int intValue2 = -1;
   private boolean flag;
   private Slot slot;

   private SpiderModule() {
   }

   @EventTarget
   public void method7024(Event5 event) {
      if (mc.player != null && mc.level != null) {
         if (this.entry2.isSelected()) {
            if (mc.player.horizontalCollision) {
               BlockPos pos = this.method7033();
               if (pos != null) {
                  Rotation rotation = RotationUtils.method6913(this.method7034(pos));
                  rotationManager.method7848(
                     new RotationRequest(
                        rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()
                     ),
                     1,
                     this,
                     2
                  );
               }
            }
         } else if (this.entry.isSelected()) {
            boolean flag = mc.player.getOffhandItem().getItem() instanceof BlockItem;
            int n = InventoryUtils.method922(slot -> mc.player.getInventory().getItem(slot).getItem() instanceof BlockItem);
            BlockPos pos = this.method7027();
            if ((flag || n != -1) && !pos.equals(BlockPos.ZERO)) {
               Vec3 vec = net.minecraft.world.phys.Vec3.atCenterOf(pos).add(0.0, 0.0, 0.0);
               Direction approximateNearest = Direction.getApproximateNearest(vec.x - mc.player.getX(), vec.y - mc.player.getY(), vec.z - mc.player.getZ());
               Rotation rotation = new Rotation(
                  mc.player.getYRot(), RotationUtils.method6913(vec.subtract(new Vec3(approximateNearest.getUnitVec3i()).scale(0.5))).getFloatValue2()
               );
               rotationManager.method7848(
                  new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
                  1,
                  this,
                  2
               );
            }
         }
      }
   }

   @EventTarget
   public void method7025(Event2 event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         if (this.entry2.isSelected()) {
            if (mc.player.horizontalCollision
               && mc.player.getMainHandItem().is(Items.WATER_BUCKET)
               && this.method7033() != null
               && mc.hitResult instanceof BlockHitResult) {
               InteractionResult useItem = mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
               if (useItem instanceof Success) {
                  this.flag = true;
               }
            }
         } else if (this.entry.isSelected()) {
            boolean flag = mc.player.getOffhandItem().getItem() instanceof BlockItem;
            int slot = InventoryUtils.method922(n -> mc.player.getInventory().getItem(n).getItem() instanceof BlockItem);
            BlockPos pos = this.method7027();
            if ((flag || slot != -1) && !pos.equals(BlockPos.ZERO)) {
               ItemStack offhandItem = flag ? mc.player.getOffhandItem() : mc.player.getInventory().getItem(slot);
               InteractionHand hand = flag ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
               if (this.method7026(offhandItem) && mc.hitResult instanceof BlockHitResult blockHitResult) {
                  int selectedSlot = mc.player.inventory.getSelectedSlot();
                  if (!flag) {
                     mc.player.inventory.setSelectedSlot(slot);
                  }

                  UseItemUtils.method6514(blockHitResult, hand);
                  if (!flag) {
                     mc.player.inventory.setSelectedSlot(selectedSlot);
                  }
               }
            }
         }
      }
   }

   private boolean method7026(ItemStack offhandItem) {
      BlockPos pos = this.method7028();
      BlockItem blockItem = (BlockItem)offhandItem.getItem();
      VoxelShape collisionShape = blockItem.getBlock().defaultBlockState().getCollisionShape(mc.level, pos);
      if (collisionShape.isEmpty()) {
         return false;
      } else {
         AABB move = collisionShape.bounds().move(pos);
         return !move.intersects(mc.player.getBoundingBox()) && move.intersects(PlayerSimulation.method6031(2).box);
      }
   }

   private BlockPos method7027() {
      BlockPos pos = this.method7028();
      return mc.level.getBlockState(pos).isSolid()
         ? BlockPos.ZERO
         : Stream.of(pos.west(), pos.east(), pos.south(), pos.north())
            .filter(pos2 -> mc.level.getBlockState(pos2).isSolid())
            .findFirst()
            .orElse(BlockPos.ZERO);
   }

   private BlockPos method7028() {
      return BlockPos.containing(PlayerSimulation.method6031(1).vec3d.add(0.0, -0.001, 0.0));
   }

   @EventTarget
   public void method7029(MoveInputEvent event) {
      if (mc.player.horizontalCollision && mc.player.lastOnGround) {
         event.method3041(true);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         if (this.entry2.isSelected()) {
            this.method7030();
         } else {
            this.method7037();
            if (this.entry3.isSelected()) {
               this.method7035();
            }
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.slot = null;
      if (this.entry2.isSelected()) {
         this.method7031();
      }
   }

   @Override
   public void onDisable() {
      this.method7037();
      this.method7032();
      this.longValue = 0L;
      super.onDisable();
   }

   private void method7030() {
      if (!mc.player.horizontalCollision) {
         this.method7037();
      } else if (this.method7033() == null) {
         this.method7037();
      } else {
         if (this.flag && !mc.player.onGround() && mc.player.isInWater()) {
            Vec3 deltaMovement = mc.player.getDeltaMovement();
            if (deltaMovement.y < 0.3F) {
               mc.player.setDeltaMovement(deltaMovement.x, 0.3F, deltaMovement.z);
            }
         }

         int selected;
         if ((selected = this.method7036()) == -1) {
            this.method7037();
         } else {
            if (!mc.player.getMainHandItem().is(Items.WATER_BUCKET)) {
               if (this.intValue == -1) {
                  this.intValue = mc.player.getInventory().getSelectedSlot();
               }

               this.intValue2 = selected;
               mc.player.getInventory().setSelectedSlot(selected);
            }
         }
      }
   }

   private void method7031() {
      if (mc.player != null && mc.gameMode != null) {
         Slot slot = mc.player.inventoryMenu.getSlot(44);
         if (!slot.getItem().is(Items.WATER_BUCKET)) {
            Slot slot2 = InventoryUtils.method908(mc.player.inventoryMenu, Items.WATER_BUCKET);
            if (slot2 != null && slot2.index != 44) {
               KeyBindUtils.method2627(SpiderModule.class, () -> {
                  if (this.isEnabled() && this.entry2.isSelected() && mc.player != null && mc.gameMode != null) {
                     Slot var1x = mc.player.inventoryMenu.getSlot(44);
                     if (!var1x.getItem().is(Items.WATER_BUCKET)) {
                        Slot var2x = InventoryUtils.method908(mc.player.inventoryMenu, Items.WATER_BUCKET);
                        if (var2x != null && var2x.index != 44) {
                           this.slot = var2x;
                           InventoryUtils.method934(var2x, 44, false, false);
                        }
                     }
                  }
               });
            }
         }
      }
   }

   private void method7032() {
      if (this.slot != null) {
         Slot slot = this.slot;
         this.slot = null;
         KeyBindUtils.method2627(SpiderModule.class, () -> {
            if (mc.player != null && mc.gameMode != null) {
               Slot var1x = mc.player.inventoryMenu.getSlot(44);
               InventoryUtils.method934(var1x, slot.index, false, false);
            }
         });
      }
   }

   private BlockPos method7033() {
      BlockPos containing = BlockPos.containing(mc.player.position());

      for (int i = 1; i >= -1; i--) {
         BlockPos above = containing.above(i);
         BlockPos relative = null;
         double d = Double.MAX_VALUE;

         for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            BlockPos pos = above.relative(direction);
            VoxelShape collisionShape = mc.level.getBlockState(pos).getCollisionShape(mc.level, pos);
            double d2;
            if (!collisionShape.isEmpty() && (d2 = net.minecraft.world.phys.Vec3.atCenterOf(pos).distanceToSqr(mc.player.position())) < d) {
               d = d2;
               relative = pos;
            }
         }

         if (relative != null) {
            return relative;
         }
      }

      return null;
   }

   private Vec3 method7034(BlockPos pos) {
      Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      double sign = mc.player.getX() - atCenterOf.x;
      double d = mc.player.getZ() - atCenterOf.z;
      double x = Math.max(pos.getX() + 0.1, Math.min(pos.getX() + 0.9, mc.player.getX()));
      double z = Math.max(pos.getZ() + 0.1, Math.min(pos.getZ() + 0.9, mc.player.getZ()));
      if (pos.getY() < Math.floor(mc.player.getY())) {
         return new Vec3(x, pos.getY() + 1.001, z);
      } else {
         if (Math.abs(sign) > Math.abs(d)) {
            x = atCenterOf.x + Math.copySign(0.501, sign);
         } else {
            z = atCenterOf.z + Math.copySign(0.501, d);
         }

         return new Vec3(x, pos.getY() + 0.75, z);
      }
   }

   private void method7035() {
      if (mc.player.horizontalCollision) {
         if (mc.player.onGround()) {
            mc.player.jumpFromGround();
         }

         long now;
         if ((now = System.currentTimeMillis()) - this.longValue >= 150L && mc.player.getOffhandItem().is(Items.PLAYER_HEAD)) {
            float yRot = mc.player.getYRot();
            float xRot = mc.player.getXRot();
            Vec3 eyePosition = mc.player.getEyePosition();
            Vec3 to = eyePosition.add(this.method7038(xRot, yRot).scale(4.0));
            BlockHitResult clip = mc.level.clip(new ClipContext(eyePosition, to, Block.OUTLINE, Fluid.NONE, mc.player));
            if (clip.getType() == Type.BLOCK && mc.level.getBlockState(clip.getBlockPos()).isRedstoneConductor(mc.level, clip.getBlockPos())) {
               mc.player.connection.send(new Rot(yRot, xRot, mc.player.onGround(), mc.player.horizontalCollision));
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.OFF_HAND);
               mc.gameMode.useItemOn(mc.player, InteractionHand.OFF_HAND, clip);
               mc.player.fallDistance = 0.0;
               this.longValue = now;
            }
         }
      }
   }

   private int method7036() {
      for (int i = 0; i < 9; i++) {
         if (mc.player.getInventory().getItem(i).is(Items.WATER_BUCKET)) {
            return i;
         }
      }

      return -1;
   }

   private void method7037() {
      if (mc.player != null && this.intValue != -1 && (this.intValue2 == -1 || mc.player.getInventory().getSelectedSlot() == this.intValue2)) {
         mc.player.getInventory().setSelectedSlot(this.intValue);
      }

      this.intValue = -1;
      this.intValue2 = -1;
      this.flag = false;
   }

   private Vec3 method7038(float xRot, float yRot) {
      float f = (float)Math.toRadians(xRot);
      float f2;
      float f3 = (float)Math.cos(f2 = (float)Math.toRadians(-yRot));
      float f4 = (float)Math.sin(f2);
      float f5 = (float)Math.cos(f);
      float f6 = (float)Math.sin(f);
      return new Vec3(f4 * f5, -f6, f3 * f5);
   }

   @Generated
   public void method7039(long longValue) {
      this.longValue = longValue;
   }

   @Generated
   public void method7040(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public void method7041(int n) {
      this.intValue2 = n;
   }

   @Generated
   public void method7042(boolean flag) {
      this.flag = flag;
   }

   @Generated
   public void setSlot(Slot slot) {
      this.slot = slot;
   }

   @Generated
   public ModeSetting getMode2() {
      return this.mode;
   }

   @Generated
   public ModeSetting.Entry getEntry10() {
      return this.entry;
   }

   @Generated
   public ModeSetting.Entry getEntry22() {
      return this.entry2;
   }

   @Generated
   public ModeSetting.Entry getEntry32() {
      return this.entry3;
   }

   @Generated
   public long getLongValue6() {
      return this.longValue;
   }

   @Generated
   public int getIntValue19() {
      return this.intValue;
   }

   @Generated
   public int getIntValue24() {
      return this.intValue2;
   }

   @Generated
   public boolean isFlag11() {
      return this.flag;
   }

   @Generated
   public Slot getSlot2() {
      return this.slot;
   }
}

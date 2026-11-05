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
   public void method7024(Event5 var1) {
      if (mc.player != null && mc.level != null) {
         if (this.entry2.isSelected()) {
            if (mc.player.horizontalCollision) {
               BlockPos var8 = this.method7033();
               if (var8 != null) {
                  Rotation var9 = RotationUtils.method6913(this.method7034(var8));
                  rotationManager.method7848(
                     new RotationRequest(
                        var9, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var9), bcCameraTestnerv.getRotationConfig()
                     ),
                     1,
                     this,
                     2
                  );
               }
            }
         } else if (this.entry.isSelected()) {
            boolean var2 = mc.player.getOffhandItem().getItem() instanceof BlockItem;
            int var3 = InventoryUtils.method922(var0 -> mc.player.getInventory().getItem(var0).getItem() instanceof BlockItem);
            BlockPos var4 = this.method7027();
            if ((var2 || var3 != -1) && !var4.equals(BlockPos.ZERO)) {
               Vec3 var5 = net.minecraft.world.phys.Vec3.atCenterOf(var4).add(0.0, 0.0, 0.0);
               Direction var6 = Direction.getApproximateNearest(var5.x - mc.player.getX(), var5.y - mc.player.getY(), var5.z - mc.player.getZ());
               Rotation var7 = new Rotation(
                  mc.player.getYRot(), RotationUtils.method6913(var5.subtract(new Vec3(var6.getUnitVec3i()).scale(0.5))).getFloatValue2()
               );
               rotationManager.method7848(
                  new RotationRequest(var7, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var7), bcCameraTestnerv.getRotationConfig()),
                  1,
                  this,
                  2
               );
            }
         }
      }
   }

   @EventTarget
   public void method7025(Event2 var1) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         if (this.entry2.isSelected()) {
            if (mc.player.horizontalCollision
               && mc.player.getMainHandItem().is(Items.WATER_BUCKET)
               && this.method7033() != null
               && mc.hitResult instanceof BlockHitResult) {
               InteractionResult var9 = mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
               if (var9 instanceof Success) {
                  this.flag = true;
               }
            }
         } else if (this.entry.isSelected()) {
            boolean var2 = mc.player.getOffhandItem().getItem() instanceof BlockItem;
            int var3 = InventoryUtils.method922(var0 -> mc.player.getInventory().getItem(var0).getItem() instanceof BlockItem);
            BlockPos var4 = this.method7027();
            if ((var2 || var3 != -1) && !var4.equals(BlockPos.ZERO)) {
               ItemStack var5 = var2 ? mc.player.getOffhandItem() : mc.player.getInventory().getItem(var3);
               InteractionHand var6 = var2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
               if (this.method7026(var5) && mc.hitResult instanceof BlockHitResult var7) {
                  int var10 = mc.player.inventory.getSelectedSlot();
                  if (!var2) {
                     mc.player.inventory.setSelectedSlot(var3);
                  }

                  UseItemUtils.method6514(var7, var6);
                  if (!var2) {
                     mc.player.inventory.setSelectedSlot(var10);
                  }
               }
            }
         }
      }
   }

   private boolean method7026(ItemStack var1) {
      BlockPos var2 = this.method7028();
      BlockItem var3 = (BlockItem)var1.getItem();
      VoxelShape var4 = var3.getBlock().defaultBlockState().getCollisionShape(mc.level, var2);
      if (var4.isEmpty()) {
         return false;
      } else {
         AABB var5 = var4.bounds().move(var2);
         return !var5.intersects(mc.player.getBoundingBox()) && var5.intersects(PlayerSimulation.method6031(2).box);
      }
   }

   private BlockPos method7027() {
      BlockPos var1 = this.method7028();
      return mc.level.getBlockState(var1).isSolid()
         ? BlockPos.ZERO
         : Stream.of(var1.west(), var1.east(), var1.south(), var1.north())
            .filter(var0 -> mc.level.getBlockState(var0).isSolid())
            .findFirst()
            .orElse(BlockPos.ZERO);
   }

   private BlockPos method7028() {
      return BlockPos.containing(PlayerSimulation.method6031(1).vec3d.add(0.0, -0.001, 0.0));
   }

   @EventTarget
   public void method7029(MoveInputEvent var1) {
      if (mc.player.horizontalCollision && mc.player.lastOnGround) {
         var1.method3041(true);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
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
            Vec3 var1 = mc.player.getDeltaMovement();
            if (var1.y < 0.3F) {
               mc.player.setDeltaMovement(var1.x, 0.3F, var1.z);
            }
         }

         int var2;
         if ((var2 = this.method7036()) == -1) {
            this.method7037();
         } else {
            if (!mc.player.getMainHandItem().is(Items.WATER_BUCKET)) {
               if (this.intValue == -1) {
                  this.intValue = mc.player.getInventory().getSelectedSlot();
               }

               this.intValue2 = var2;
               mc.player.getInventory().setSelectedSlot(var2);
            }
         }
      }
   }

   private void method7031() {
      if (mc.player != null && mc.gameMode != null) {
         Slot var1 = mc.player.inventoryMenu.getSlot(44);
         if (!var1.getItem().is(Items.WATER_BUCKET)) {
            Slot var2 = InventoryUtils.method908(mc.player.inventoryMenu, Items.WATER_BUCKET);
            if (var2 != null && var2.index != 44) {
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
         Slot var1 = this.slot;
         this.slot = null;
         KeyBindUtils.method2627(SpiderModule.class, () -> {
            if (mc.player != null && mc.gameMode != null) {
               Slot var1x = mc.player.inventoryMenu.getSlot(44);
               InventoryUtils.method934(var1x, var1.index, false, false);
            }
         });
      }
   }

   private BlockPos method7033() {
      BlockPos var1 = BlockPos.containing(mc.player.position());

      for (int var2 = 1; var2 >= -1; var2--) {
         BlockPos var3 = var1.above(var2);
         BlockPos var4 = null;
         double var5 = Double.MAX_VALUE;

         for (Direction var10 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            BlockPos var11 = var3.relative(var10);
            VoxelShape var12 = mc.level.getBlockState(var11).getCollisionShape(mc.level, var11);
            double var13;
            if (!var12.isEmpty() && (var13 = net.minecraft.world.phys.Vec3.atCenterOf(var11).distanceToSqr(mc.player.position())) < var5) {
               var5 = var13;
               var4 = var11;
            }
         }

         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   private Vec3 method7034(BlockPos var1) {
      Vec3 var2 = net.minecraft.world.phys.Vec3.atCenterOf(var1);
      double var3 = mc.player.getX() - var2.x;
      double var5 = mc.player.getZ() - var2.z;
      double var7 = Math.max(var1.getX() + 0.1, Math.min(var1.getX() + 0.9, mc.player.getX()));
      double var9 = Math.max(var1.getZ() + 0.1, Math.min(var1.getZ() + 0.9, mc.player.getZ()));
      if (var1.getY() < Math.floor(mc.player.getY())) {
         return new Vec3(var7, var1.getY() + 1.001, var9);
      } else {
         if (Math.abs(var3) > Math.abs(var5)) {
            var7 = var2.x + Math.copySign(0.501, var3);
         } else {
            var9 = var2.z + Math.copySign(0.501, var5);
         }

         return new Vec3(var7, var1.getY() + 0.75, var9);
      }
   }

   private void method7035() {
      if (mc.player.horizontalCollision) {
         if (mc.player.onGround()) {
            mc.player.jumpFromGround();
         }

         long var1;
         if ((var1 = System.currentTimeMillis()) - this.longValue >= 150L && mc.player.getOffhandItem().is(Items.PLAYER_HEAD)) {
            float var3 = mc.player.getYRot();
            float var4 = mc.player.getXRot();
            Vec3 var5 = mc.player.getEyePosition();
            Vec3 var6 = var5.add(this.method7038(var4, var3).scale(4.0));
            BlockHitResult var7 = mc.level.clip(new ClipContext(var5, var6, Block.OUTLINE, Fluid.NONE, mc.player));
            if (var7.getType() == Type.BLOCK && mc.level.getBlockState(var7.getBlockPos()).isRedstoneConductor(mc.level, var7.getBlockPos())) {
               mc.player.connection.send(new Rot(var3, var4, mc.player.onGround(), mc.player.horizontalCollision));
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.OFF_HAND);
               mc.gameMode.useItemOn(mc.player, InteractionHand.OFF_HAND, var7);
               mc.player.fallDistance = 0.0;
               this.longValue = var1;
            }
         }
      }
   }

   private int method7036() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getItem(var1).is(Items.WATER_BUCKET)) {
            return var1;
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

   private Vec3 method7038(float var1, float var2) {
      float var3 = (float)Math.toRadians(var1);
      float var4;
      float var5 = (float)Math.cos(var4 = (float)Math.toRadians(-var2));
      float var6 = (float)Math.sin(var4);
      float var7 = (float)Math.cos(var3);
      float var8 = (float)Math.sin(var3);
      return new Vec3(var6 * var7, -var8, var5 * var7);
   }

   @Generated
   public void method7039(long var1) {
      this.longValue = var1;
   }

   @Generated
   public void method7040(int var1) {
      this.intValue = var1;
   }

   @Generated
   public void method7041(int var1) {
      this.intValue2 = var1;
   }

   @Generated
   public void method7042(boolean var1) {
      this.flag = var1;
   }

   @Generated
   public void setSlot(Slot var1) {
      this.slot = var1;
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

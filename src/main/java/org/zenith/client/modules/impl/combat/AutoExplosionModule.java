package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.BlockPlacedEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.utility.game.AttackUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoExplosion",
   description = "module.autoexplosion.description",
   category = ModuleCategory.COMBAT
)
public final class AutoExplosionModule extends Module {
   public static final AutoExplosionModule INSTANCE = new AutoExplosionModule();
   private int ticks = 0;
   private BlockPos blockPos = null;
   private BlockPos blockPos2 = null;
   public int intValue = 0;
   private Slot slot = null;

   @EventTarget
   public void method4898(BlockPlacedEvent event) {
      if (event.getBlockState().getBlock() == Blocks.OBSIDIAN) {
         this.ticks = 5;
         Slot slot = InventoryUtils.method906(Items.END_CRYSTAL);
         if (slot == null) {
            return;
         }

         this.blockPos2 = event.getBlockPos();
      }
   }

   @EventTarget
   public void method4899(Event5 event) {
      if (this.blockPos2 != null) {
         Rotation rotation = rotationManager.method7844()
            .method3141(
               Zenith.getInstance().getRotationManager().method7844().method3131(RotationUtils.method6913(net.minecraft.world.phys.Vec3.atCenterOf(this.blockPos2).add(0.0, 0.5, 0.0)))
            );
         rotationManager.method7848(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 4, this, 1);
      }

      if (this.blockPos != null) {
         if (this.intValue > 0) {
            Rotation rotation = rotationManager.method7844()
               .method3141(
                  Zenith.getInstance().getRotationManager().method7844().method3131(RotationUtils.method6913(net.minecraft.world.phys.Vec3.atCenterOf(this.blockPos).add(0.0, 1.2F, 0.0)))
               );
            rotationManager.method7848(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 4, this, 1);
            this.intValue--;
         }
      }
   }

   @EventTarget
   public void method4900(EntityAddEvent event) {
      if (event.getEntity() instanceof EndCrystal endCrystal && endCrystal.blockPosition().equals(this.blockPos)) {
         this.intValue = 4;
      }
   }

   @EventTarget
   public void method4901(Event9 event) {
      if (!event.isCancelled()
         && this.blockPos != null
         && this.intValue > 0
         && mc.hitResult instanceof EntityHitResult entityHitResult
         && entityHitResult.getEntity() instanceof EndCrystal) {
         AttackUtils.method816(entityHitResult.getEntity());
         this.blockPos = null;
         event.setCancelled(true);
      }

      if (this.ticks > 0) {
         this.ticks--;
      }

      if (this.ticks <= 0) {
         this.blockPos2 = null;
         if (this.slot != null && KeyBindUtils.method2630(AutoExplosionModule.class)) {
            Slot slot = this.slot;
            KeyBindUtils.method2627(AutoExplosionModule.class, () -> InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, true));
            this.slot = null;
         }
      } else if (this.blockPos2 != null) {
         Slot slot = InventoryUtils.method906(Items.END_CRYSTAL);
         if (slot != null) {
            if (!event.isCancelled()
               && mc.hitResult instanceof BlockHitResult blockHitResult
               && mc.level.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.OBSIDIAN) {
               if (mc.player.getMainHandItem().getItem() == Items.END_CRYSTAL) {
                  UseItemUtils.method6514(blockHitResult, InteractionHand.MAIN_HAND);
                  event.setCancelled(true);
                  this.blockPos = blockHitResult.getBlockPos().above();
                  this.blockPos2 = null;
                  this.ticks = -4;
               } else if (KeyBindUtils.method2629(AutoExplosionModule.class)) {
                  KeyBindUtils.method2627(AutoExplosionModule.class, () -> InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, true));
                  if (this.slot == null) {
                     this.slot = slot;
                  }
               }
            }
         }
      }
   }
}

package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "ElytraFly",
   category = ModuleCategory.MOVEMENT,
   description = "module.elytrafly.description"
)
public final class ElytraFlyModule extends Module {
   public static final ElytraFlyModule INSTANCE = new ElytraFlyModule();
   public boolean jumpPending = false;

   private ElytraFlyModule() {
   }

   @Override
   public void onDisable() {
      this.jumpPending = false;
      super.onDisable();
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (mc.player != null) {
         ItemStack chestItem = mc.player.getItemBySlot(EquipmentSlot.CHEST);
         if (chestItem.is(Items.ELYTRA)) {
            if (mc.player.onGround()) {
               event.method3041(true);
               this.jumpPending = true;
            } else if (!mc.player.isFallFlying()) {
               if (!this.jumpPending) {
                  UseItemUtils.method6503();
               } else {
                  this.jumpPending = false;
               }
            } else {
               this.jumpPending = false;
            }
         }
      }
   }

   @EventTarget
   public void onRotation(PreRotationEvent event) {
      zenith.getRotationManager()
         .requestRotation(
            new RotationRequest(new Rotation(mc.player.getYRot(), 0.0F), () -> new Rotation(mc.player.getYRot(), 0.0F), bcCameraTestnerv.getRotationConfig()),
            2,
            this
         );
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         ItemStack chestItem = mc.player.getItemBySlot(EquipmentSlot.CHEST);
         if (chestItem.is(Items.ELYTRA)) {
            if (mc.player.isFallFlying()) {
               mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, mc.player.getDeltaMovement().y + 0.0305, mc.player.getDeltaMovement().z);
            }
         }
      }
   }
}

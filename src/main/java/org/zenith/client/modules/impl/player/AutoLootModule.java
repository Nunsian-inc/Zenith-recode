package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.phys.EntityHitResult;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoLoot",
   category = ModuleCategory.PLAYER,
   description = "module.autoloot.description"
)
public final class AutoLootModule extends Module {
   private final BooleanSetting ignoreWalls = new BooleanSetting("module.autoLoot.ignoreWalls", "module.autoLoot.ignoreWalls.desc", true);
   private final BooleanSetting ignoreEntity = new BooleanSetting("module.autoLoot.ignoreEntity", "module.autoLoot.ignoreEntity.desc", true);
   public static final AutoLootModule INSTANCE = new AutoLootModule();
   private AbstractVillager merchantEntity;

   private AutoLootModule() {
   }

   @EventTarget
   public void onRotationUpdate(PreRotationEvent event) {
      UseItemUtils.method6510()
         .filter(AbstractVillager.class::isInstance)
         .map(AbstractVillager.class::cast)
         .filter(abstractVillager -> abstractVillager.hasItemInSlot(EquipmentSlot.MAINHAND) || abstractVillager.hasItemInSlot(EquipmentSlot.OFFHAND))
         .findFirst()
         .ifPresent(villager -> {
            double deltaX = villager.getX() - mc.player.getX();
            double deltaY = villager.getY() - mc.player.getY();
            double deltaZ = villager.getZ() - mc.player.getZ();
            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            double pitch = Mth.wrapDegrees(-(Mth.atan2(deltaY, horizontalDistance) * 180.0 / (float) Math.PI));
            double yaw = Mth.wrapDegrees(Mth.atan2(deltaZ, deltaX) * 180.0 / (float) Math.PI - 90.0);
            Rotation rotation = new Rotation((float)yaw, (float)pitch);
            rotationManager.requestRotation(new RotationRequest(rotation, () -> {
               this.merchantEntity = villager;
               return bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation);
            }, bcCameraTestnerv.getRotationConfig()), 3, this);
         });
   }

   @EventTarget
   public void onInteract(PostRotationEvent event) {
      if (this.merchantEntity != null) {
         if (!this.ignoreWalls.isEnabled() && !this.ignoreEntity.isEnabled()) {
            if (mc.hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() == this.merchantEntity) {
               mc.gameMode.interact(mc.player, hitResult.getEntity(), hitResult, InteractionHand.OFF_HAND);
            }

            this.merchantEntity = null;
         } else {
            EntityHitResult entityHitResult = TargetUtils.method3210(
               3.0, zenith.getRotationManager().getEffectiveRotation(), villager -> !this.ignoreEntity.isEnabled() || this.merchantEntity == villager
            );
            if (!this.ignoreWalls.isEnabled()
               && !TargetUtils.method3212(
                  zenith.getRotationManager().getEffectiveRotation(), mc.player.getEyePosition(1.0F), this.merchantEntity.getBoundingBox(), 3.0, true
               )) {
               this.merchantEntity = null;
            } else {
               if (entityHitResult != null && entityHitResult.getEntity() == this.merchantEntity) {
                  InteractionResult interactionResult = mc.gameMode.interact(mc.player, entityHitResult.getEntity(), entityHitResult, InteractionHand.OFF_HAND);
                  if (interactionResult instanceof Success) {
                     org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.OFF_HAND);
                  }
               }

               this.merchantEntity = null;
            }
         }
      }
   }
}

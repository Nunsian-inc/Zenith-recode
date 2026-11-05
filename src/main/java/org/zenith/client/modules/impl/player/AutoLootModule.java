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
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
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
   public void method4321(Event5 event) {
      UseItemUtils.method6510()
         .filter(AbstractVillager.class::isInstance)
         .map(AbstractVillager.class::cast)
         .filter(abstractVillager -> abstractVillager.hasItemInSlot(EquipmentSlot.MAINHAND) || abstractVillager.hasItemInSlot(EquipmentSlot.OFFHAND))
         .findFirst()
         .ifPresent(var1x -> {
            double x = var1x.getX() - mc.player.getX();
            double y = var1x.getY() - mc.player.getY();
            double d = var1x.getZ() - mc.player.getZ();
            double x2 = Math.sqrt(x * x + d * d);
            double xRot = Mth.wrapDegrees(-(Mth.atan2(y, x2) * 180.0 / (float) Math.PI));
            double yRot = Mth.wrapDegrees(Mth.atan2(d, x) * 180.0 / (float) Math.PI - 90.0);
            Rotation rotation = new Rotation((float)yRot, (float)xRot);
            rotationManager.method7849(new RotationRequest(rotation, () -> {
               this.merchantEntity = var1x;
               return bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation);
            }, bcCameraTestnerv.getRotationConfig()), 3, this);
         });
   }

   @EventTarget
   public void method4322(Event2 event) {
      if (this.merchantEntity != null) {
         if (!this.ignoreWalls.isEnabled() && !this.ignoreEntity.isEnabled()) {
            if (mc.hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() == this.merchantEntity) {
               mc.gameMode.interact(mc.player, hitResult.getEntity(), hitResult, InteractionHand.OFF_HAND);
            }

            this.merchantEntity = null;
         } else {
            EntityHitResult entityHitResult = TargetUtils.method3210(
               3.0, zenith.getRotationManager().method7844(), var1x -> !this.ignoreEntity.isEnabled() || this.merchantEntity == var1x
            );
            if (!this.ignoreWalls.isEnabled()
               && !TargetUtils.method3212(
                  zenith.getRotationManager().method7844(), mc.player.getEyePosition(1.0F), this.merchantEntity.getBoundingBox(), 3.0, true
               )) {
               this.merchantEntity = null;
            } else {
               if (entityHitResult != null && entityHitResult.getEntity() == this.merchantEntity) {
                  InteractionResult interact = mc.gameMode.interact(mc.player, entityHitResult.getEntity(), entityHitResult, InteractionHand.OFF_HAND);
                  if (interact instanceof Success) {
                     org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.OFF_HAND);
                  }
               }

               this.merchantEntity = null;
            }
         }
      }
   }
}

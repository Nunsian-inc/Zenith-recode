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
   public void method4321(Event5 var1) {
      UseItemUtils.method6510()
         .filter(AbstractVillager.class::isInstance)
         .map(AbstractVillager.class::cast)
         .filter(var0 -> var0.hasItemInSlot(EquipmentSlot.MAINHAND) || var0.hasItemInSlot(EquipmentSlot.OFFHAND))
         .findFirst()
         .ifPresent(var1x -> {
            double var2 = var1x.getX() - mc.player.getX();
            double var4 = var1x.getY() - mc.player.getY();
            double var6 = var1x.getZ() - mc.player.getZ();
            double var8 = Math.sqrt(var2 * var2 + var6 * var6);
            double var10 = Mth.wrapDegrees(-(Mth.atan2(var4, var8) * 180.0 / (float) Math.PI));
            double var12 = Mth.wrapDegrees(Mth.atan2(var6, var2) * 180.0 / (float) Math.PI - 90.0);
            Rotation var14 = new Rotation((float)var12, (float)var10);
            rotationManager.method7849(new RotationRequest(var14, () -> {
               this.merchantEntity = var1x;
               return bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var14);
            }, bcCameraTestnerv.getRotationConfig()), 3, this);
         });
   }

   @EventTarget
   public void method4322(Event2 var1) {
      if (this.merchantEntity != null) {
         if (!this.ignoreWalls.isEnabled() && !this.ignoreEntity.isEnabled()) {
            if (mc.hitResult instanceof EntityHitResult var4 && var4.getEntity() == this.merchantEntity) {
               mc.gameMode.interact(mc.player, var4.getEntity(), var4, InteractionHand.OFF_HAND);
            }

            this.merchantEntity = null;
         } else {
            EntityHitResult var2 = TargetUtils.method3210(
               3.0, zenith.getRotationManager().method7844(), var1x -> !this.ignoreEntity.isEnabled() || this.merchantEntity == var1x
            );
            if (!this.ignoreWalls.isEnabled()
               && !TargetUtils.method3212(
                  zenith.getRotationManager().method7844(), mc.player.getEyePosition(1.0F), this.merchantEntity.getBoundingBox(), 3.0, true
               )) {
               this.merchantEntity = null;
            } else {
               if (var2 != null && var2.getEntity() == this.merchantEntity) {
                  InteractionResult var3 = mc.gameMode.interact(mc.player, var2.getEntity(), var2, InteractionHand.OFF_HAND);
                  if (var3 instanceof Success) {
                     org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.OFF_HAND);
                  }
               }

               this.merchantEntity = null;
            }
         }
      }
   }
}

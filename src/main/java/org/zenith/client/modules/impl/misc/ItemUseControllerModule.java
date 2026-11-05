package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.StopUsingItemEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "ItemUseController",
   description = "module.itemusecontroller.description",
   category = ModuleCategory.MISC
)
public final class ItemUseControllerModule extends Module {
   public static final ItemUseControllerModule INSTANCE = new ItemUseControllerModule();
   private final BooleanSetting newVersion = new BooleanSetting("module.itemUseController.newVersion", "module.itemUseController.newVersion.desc", false);
   private final BooleanSetting aim = new BooleanSetting("module.itemUseController.aim", "module.itemUseController.aim.desc", false);
   private final MultiSelectSetting items = MultiSelectSetting.method5224(
      "module.itemUseController.items",
      "module.itemUseController.items.desc",
      List.of("module.itemUseController.bow", "module.itemUseController.crossbow", "module.itemUseController.snowball", "module.itemUseController.trident")
   );
   private final SliderSetting predictSetting = new SliderSetting(
      "module.itemUseController.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.itemUseController.predictSetting.desc", "t"
   );
   public boolean flag = false;
   public boolean flag2 = false;

   public boolean method4810() {
      ItemStack mainHandItem = mc.player.getMainHandItem();
      Item item = mainHandItem.getItem();
      if (this.items.method5226("module.itemUseController.bow") && item == Items.BOW) {
         return true;
      } else if (this.items.method5226("module.itemUseController.crossbow") && item == Items.CROSSBOW) {
         return true;
      } else {
         return this.items.method5226("module.itemUseController.snowball") && item == Items.SNOWBALL
            ? true
            : this.items.method5226("module.itemUseController.trident") && item == Items.TRIDENT;
      }
   }

   private ItemUseControllerModule() {
   }

   @EventTarget
   public void method4811(Event5 event) {
      if (!this.newVersion.isEnabled()) {
         this.flag = false;
         if (mc.gui.overlay() == null
            && mc.gui.screen() == null
            && (Zenith.getInstance().getRotationManager().getCurrentRotation() != null || this.aim.isEnabled() && this.method4810())
            && !Zenith.getInstance().getRotationManager().method7844().method3131(new Rotation(mc.player.getYRot(), mc.player.getXRot())).method5329(5.0F)
            && (
               mc.options.keyUse.isDown()
                     && mc.rightClickDelay == 0
                     && !mc.player.isUsingItem()
                     && mc.player.getMainHandItem().getUseDuration(mc.player) == 0
                  || Zenith.getInstance().getTaskScheduler().method5118(HandleInputEvent.class)
            )) {
            Rotation rotation = this.method4813();
            Rotation rotation2 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation);
            Zenith.getInstance().getRotationManager().method7849(new RotationRequest(rotation2, () -> rotation2, bcCameraTestnerv.getRotationConfig()), 20, this);
            this.flag = true;
         }
      } else if (mc.gui.overlay() == null
         && mc.gui.screen() == null
         && (Zenith.getInstance().getRotationManager().getCurrentRotation() != null || this.aim.isEnabled() && this.method4810())
         && (
            mc.options.keyUse.isDown() && mc.rightClickDelay == 0 && !mc.player.isUsingItem() && mc.player.getMainHandItem().getUseDuration(mc.player) == 0
               || Zenith.getInstance().getTaskScheduler().method5118(HandleInputEvent.class)
         )) {
         Rotation rotation = this.method4813();
         Rotation rotation2 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation);
         Zenith.getInstance().getRotationManager().method7849(new RotationRequest(rotation2, () -> rotation2, bcCameraTestnerv.getRotationConfig()), 20, this);
      }

      this.flag2 = false;
      if (!mc.options.keyUse.isDown()
         && mc.player.isUsingItem()
         && (Zenith.getInstance().getRotationManager().getCurrentRotation() != null || this.aim.isEnabled() && this.method4810())) {
         Rotation rotation = this.method4813();
         if (!Zenith.getInstance().getRotationManager().method7844().method3131(rotation).method5329(5.0F)) {
            Rotation rotation2 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation);
            Zenith.getInstance().getRotationManager().method7849(new RotationRequest(rotation2, () -> rotation2, bcCameraTestnerv.getRotationConfig()), 20, this);
            this.flag2 = true;
         }
      }
   }

   @EventTarget
   public void method4812(Event9 event) {
      if (!this.newVersion.isEnabled() && this.flag) {
         event.cancel();
      }
   }

   private Rotation method4813() {
      Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());

      try {
         if (this.aim.isEnabled() && this.method4810()) {
            ItemStack mainHandItem = mc.player.getMainHandItem();
            LivingEntity livingEntity22 = AuraModule.INSTANCE.getLivingEntity22();
            if (!(livingEntity22 instanceof Player player)) {
               return rotation;
            } else {
               Rotation rotation2 = RotationUtils.method6912(
                  this.method4814(
                        this.predictSetting.getValue3() == 0.0F
                           ? player.getBoundingBox()
                           : PlayerSimulation.method6032(player, (int)this.predictSetting.getValue3()).box
                     )
                     .subtract(PlayerSimulation.method6031(1).vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0))
               );
               float floatValue = rotation2.getFloatValue();
               int n = Math.round(rotation2.getFloatValue2());
               float f = Float.MAX_VALUE;
               Rotation rotation3 = rotation;

               for (int i = 0; i < 90; i++) {
                  int[] ints = new int[]{n + i, n - i};

                  for (int xRot : ints) {
                     Rotation rotation4 = new Rotation(floatValue, xRot);
                     var hitResults = PredictionsModule.INSTANCE.method2903(mainHandItem, mainHandItem.getItem(), rotation4);
                     if (hitResults != null && !hitResults.isEmpty() && hitResults.getFirst() instanceof EntityHitResult entityHitResult && entityHitResult.getEntity().equals(livingEntity22)) {
                        float f2 = Math.abs(rotation2.getFloatValue2() - xRot);
                        if (f2 <= 5.0F) {
                           return rotation4;
                        }

                        if (f2 < f) {
                           f = f2;
                           rotation3 = rotation4;
                        }
                     }
                  }
               }

               return rotation3;
            }
         } else {
            return rotation;
         }
      } catch (Exception e) {
         e.printStackTrace();
         return rotation;
      }
   }

   public Vec3 method4814(AABB boundingBox) {
      return new Vec3(Mth.lerp(0.5, boundingBox.minX, boundingBox.maxX), Mth.lerp(0.8, boundingBox.minY, boundingBox.maxY), Mth.lerp(0.5, boundingBox.minZ, boundingBox.maxZ));
   }

   @EventTarget
   public void method4815(StopUsingItemEvent event) {
      if (this.flag2) {
         event.cancel();
      }
   }
}

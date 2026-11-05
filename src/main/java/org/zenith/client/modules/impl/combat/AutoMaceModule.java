package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoMace",
   category = ModuleCategory.COMBAT,
   description = "module.autoMace.description"
)
public final class AutoMaceModule extends Module {
   public static final AutoMaceModule INSTANCE = new AutoMaceModule();
   private static final float CONST_floatValue = 1.5F;
   private static final double CONST_doubleValue = 0.5;
   private final SliderSetting minFall = new SliderSetting("module.autoMace.minFall", 3.0F, 1.5F, 30.0F, 0.5F, "module.autoMace.minFall.desc", "b");
   private final SliderSetting predictTicks = new SliderSetting(
      "module.autoMace.predictTicks", 40.0F, 5.0F, 100.0F, 1.0F, "module.autoMace.predictTicks.desc", "t"
   );
   private final SliderSetting swapRadius = new SliderSetting("module.autoMace.swapRadius", 6.0F, 3.0F, 20.0F, 0.5F, "module.autoMace.swapRadius.desc", "b");
   private final BooleanSetting holdAura = new BooleanSetting("module.autoMace.holdAura", "module.autoMace.holdAura.desc", true);
   private final BooleanSetting fullCooldown = new BooleanSetting("module.autoMace.fullCooldown", "module.autoMace.fullCooldown.desc", true);
   private final BooleanSetting swapBack = new BooleanSetting("module.autoMace.swapBack", "module.autoMace.swapBack.desc", true);
   private boolean flag;
   private int intValue = -1;
   private float floatValue;
   private Vec3 vec3d;
   private final Map<Integer, AABB> items = new HashMap<>();
   private int intValue2 = -1;
   private int intValue3 = -1;
   private AutoMaceModule.Type type = AutoMaceModule.Type.NONE;

   private AutoMaceModule() {
   }

   @EventTarget(0)
   public void method308(Event5 event) {
      this.method316();
      if (mc.player != null && mc.level != null && !mc.player.isDeadOrDying()) {
         int selected = InventoryUtils.method922(slot -> mc.player.getInventory().getItem(slot).is(Items.MACE));
         if (AuraModule.INSTANCE.isEnabled() && selected != -1 && this.method311() && this.method312()) {
            this.flag = true;
            int selectedSlot = mc.player.getInventory().getSelectedSlot();
            if (selectedSlot != selected && !mc.player.isUsingItem()) {
               if (this.intValue2 == -1) {
                  this.intValue2 = selectedSlot;
               }

               mc.player.getInventory().setSelectedSlot(selected);
               mc.gameMode.ensureHasSentCarriedItem();
            }

            if (this.intValue == 1 && this.fullCooldown.isEnabled() && mc.player.getMainHandItem().is(Items.MACE)) {
               mc.player.attackStrengthTicker = Mth.ceil(mc.player.getCurrentItemAttackStrengthDelay()) + 1;
            }
         } else {
            if (!this.method314()) {
               this.method315();
            }
         }
      }
   }

   public AutoMaceModule.Type method309(LivingEntity livingEntity) {
      if (this.isEnabled() && this.flag && livingEntity != null && mc.player != null && mc.player.getMainHandItem().is(Items.MACE)) {
         if (this.intValue3 != livingEntity.getId()) {
            this.intValue3 = livingEntity.getId();
            this.type = this.method310(livingEntity);
         }

         return this.type;
      } else {
         return AutoMaceModule.Type.NONE;
      }
   }

   private AutoMaceModule.Type method310(LivingEntity livingEntity) {
      if (this.intValue == 1) {
         return AutoMaceModule.Type.HIT;
      } else if (!this.holdAura.isEnabled()) {
         return AutoMaceModule.Type.NONE;
      } else {
         AABB boundingBox = this.items.get(livingEntity.getId());
         if (boundingBox == null) {
            boundingBox = this.method313(livingEntity, this.intValue - 1);
         }

         return AuraModule.INSTANCE.method6332(boundingBox, this.vec3d) ? AutoMaceModule.Type.HOLD : AutoMaceModule.Type.NONE;
      }
   }

   private boolean method311() {
      if (!this.method314()) {
         return false;
      } else {
         float eyeHeight = mc.player.getEyeHeight(mc.player.getPose());
         PlayerSimulation playerSimulation = PlayerSimulation.method6031(0);
         Vec3 vec3d = playerSimulation.vec3d.add(0.0, eyeHeight, 0.0);
         float floatValue = playerSimulation.floatValue3;
         int n = (int)this.predictTicks.getValue3();

         for (int i = 1; i <= n; i++) {
            playerSimulation.tick();
            if (playerSimulation.flag4) {
               if (!(floatValue <= 1.5F) && !(floatValue < this.minFall.getValue3())) {
                  this.intValue = i;
                  this.floatValue = floatValue;
                  this.vec3d = vec3d;
                  return true;
               }

               return false;
            }

            if (playerSimulation.flag3 || playerSimulation.flag7 || playerSimulation.method6046() || playerSimulation.method6064()) {
               return false;
            }

            vec3d = playerSimulation.vec3d.add(0.0, eyeHeight, 0.0);
            floatValue = playerSimulation.floatValue3;
         }

         return false;
      }
   }

   private boolean method312() {
      int scale = this.intValue - 1;
      double d = this.swapRadius.getValue3();
      double d2 = d + 0.5 * scale;
      double d3 = d2 * d2;
      double d4 = d * d;
      boolean flag = false;

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof LivingEntity livingEntity
            && !(entity.getBoundingBox().distanceToSqr(this.vec3d) > d3)
            && AuraModule.INSTANCE.method6331(livingEntity)
            && !UseItemUtils.method6530(livingEntity)) {
            AABB aabb = this.method313(livingEntity, scale);
            this.items.put(entity.getId(), aabb);
            if (aabb.distanceToSqr(this.vec3d) <= d4) {
               flag = true;
            }
         }
      }

      return flag;
   }

   private AABB method313(LivingEntity livingEntity, int scale) {
      return livingEntity instanceof Player player ? PlayerSimulation.method6032(player, scale).box : livingEntity.getBoundingBox();
   }

   private boolean method314() {
      return !mc.player.onGround()
         && !mc.player.isFallFlying()
         && !mc.player.isInWater()
         && !mc.player.onClimbable()
         && !mc.player.isPassenger()
         && !mc.player.getAbilities().flying;
   }

   private void method315() {
      if (this.intValue2 != -1) {
         if (this.swapBack.isEnabled() && mc.player != null && mc.player.getInventory().getSelectedSlot() != this.intValue2) {
            mc.player.getInventory().setSelectedSlot(this.intValue2);
         }

         this.intValue2 = -1;
      }
   }

   private void method316() {
      this.flag = false;
      this.intValue = -1;
      this.floatValue = 0.0F;
      this.vec3d = null;
      this.items.clear();
      this.intValue3 = -1;
      this.type = AutoMaceModule.Type.NONE;
   }

   @Override
   public void onDisable() {
      this.method315();
      this.method316();
      super.onDisable();
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   public static enum Type {
      NONE,
      HOLD,
      HIT;
   }
}

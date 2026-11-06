package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.InteractionHand;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.NoSlowEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "NoSlow",
   category = ModuleCategory.MOVEMENT,
   description = "module.noslow.description"
)
public final class NoSlowModule extends Module {
   private static final int[] SLOWDOWN_CANCEL_INTERVALS = new int[]{2, 3, 3};
   public static final NoSlowModule INSTANCE = new NoSlowModule();
   private int itemUseTicks;
   private int cancelIntervalIndex;
   private final ModeSetting mode = new ModeSetting("module.noSlow.mode", "module.noSlow.mode.desc");
   private final ModeSetting.Entry grimNewMode = new ModeSetting.Entry(this.mode, "module.noSlow.mode.grimNew");
   private final ModeSetting.Entry reallyWorldMode = new ModeSetting.Entry(this.mode, "module.noSlow.mode.realyWorld");
   private final ModeSetting.Entry grimOldMode = new ModeSetting.Entry(this.mode, "module.noSlow.mode.grimOld").method2103();
   private final BooleanSetting sprint = new BooleanSetting("module.noSlow.sprint", "module.noSlow.sprint.desc", true, this.grimOldMode::isSelected);

   private NoSlowModule() {
   }

   @Override
   public void onEnable() {
      this.resetItemUseTicks();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetItemUseTicks();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.reallyWorldMode.isSelected() && mc.player != null && mc.level != null && !mc.player.isFallFlying() && mc.player.isUsingItem()) {
         this.itemUseTicks++;
      } else {
         this.resetItemUseTicks();
      }
   }

   @EventTarget
   public void onNoSlow(NoSlowEvent event) {
      if (this.grimNewMode.isSelected() && mc.player.getTicksUsingItem() % 2 == 0) {
         event.setCancelled(true);
      }

      if (this.reallyWorldMode.isSelected() && !mc.player.isFallFlying()) {
         int requiredUseTicks = SLOWDOWN_CANCEL_INTERVALS[this.cancelIntervalIndex % SLOWDOWN_CANCEL_INTERVALS.length];
         if (this.itemUseTicks >= requiredUseTicks) {
            event.setCancelled(true);
            this.itemUseTicks = 0;
            this.cancelIntervalIndex++;
         }
      }

      if (this.grimOldMode.isSelected()) {
         InteractionHand usedItemHand = mc.player.getUsedItemHand();
         if (this.sprint.isEnabled()) {
            mc.player
               .setSprinting(
                  mc.player.isSprintingPossible(true)
                     && (mc.player.isUnderWater() ? mc.player.input.hasForwardImpulse() : ViaCompat.method4846(mc.player.input) >= 0.8F)
                     && !mc.player.isFallFlying()
                     && (!mc.player.isMovingSlowly() || mc.player.isUnderWater())
               );
         }

         UseItemUtils.useItem(usedItemHand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
         event.setCancelled(true);
      }
   }

   private void resetItemUseTicks() {
      this.itemUseTicks = 0;
      this.cancelIntervalIndex = 0;
   }
}

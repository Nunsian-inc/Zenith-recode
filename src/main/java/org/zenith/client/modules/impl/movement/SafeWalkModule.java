package org.zenith.client.modules.impl.movement;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;

@ModuleInfo(name = "SafeWalk", description = "module.safeWalk.description", category = ModuleCategory.MOVEMENT)
public final class SafeWalkModule extends Module {
   public static final SafeWalkModule INSTANCE = new SafeWalkModule();

   private final ModeSetting activation = new ModeSetting("module.safeWalk.activation", "module.safeWalk.activation.desc",
      "module.safeWalk.activation.always", "module.safeWalk.activation.sneak", "module.safeWalk.activation.block");
   private final BooleanSetting pauseScaffold = new BooleanSetting("module.safeWalk.pauseScaffold", "module.safeWalk.pauseScaffold.desc", true);

   private SafeWalkModule() {
   }

   public boolean shouldClipEdge(Player player) {
      if (!this.isEnabled() || player == null || player != mc.player || mc.level == null
         || !player.isAlive() || player.isSpectator() || player.isPassenger()
         || player.getAbilities().flying || player.isFallFlying() || player.isInWater() || player.isInLava()
         || player.onClimbable() || this.pauseScaffold.isEnabled() && ScaffoldModule.INSTANCE.isEnabled()) {
         return false;
      }

      return this.activation.isSelected(0)
         || this.activation.isSelected(1) && player.isShiftKeyDown()
         || this.activation.isSelected(2) && (player.getMainHandItem().getItem() instanceof BlockItem
            || player.getOffhandItem().getItem() instanceof BlockItem);
   }
}

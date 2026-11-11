package org.zenith.client.modules.impl.movement;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.combat.CriticalsModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;

@ModuleInfo(name = "AntiLevitation", description = "module.antiLevitation.description", category = ModuleCategory.MOVEMENT)
public final class AntiLevitationModule extends Module {
   public static final AntiLevitationModule INSTANCE = new AntiLevitationModule();
   private final ModeSetting mode = new ModeSetting("module.antiLevitation.mode", "module.antiLevitation.mode.desc");
   private final ModeSetting.Entry ignore = new ModeSetting.Entry(this.mode, "module.antiLevitation.ignore");
   private final ModeSetting.Entry limit = new ModeSetting.Entry(this.mode, "module.antiLevitation.limit");
   private final ModeSetting.Entry down = new ModeSetting.Entry(this.mode, "module.antiLevitation.down");
   private final BooleanSetting respectCriticals = new BooleanSetting("module.antiLevitation.respectCriticals",
      "module.antiLevitation.respectCriticals.desc", true);

   private AntiLevitationModule() {
   }

   public boolean shouldSuppressLevitation() {
      return this.isEnabled() && mc.player != null && mc.player.isAlive() && !mc.player.isSpectator()
         && !mc.player.isFallFlying() && !mc.player.getAbilities().flying && !mc.player.isPassenger()
         && mc.player.hasEffect(MobEffects.LEVITATION)
         && !(this.respectCriticals.isEnabled() && CriticalsModule.INSTANCE.isEnabled()
            && CriticalsModule.INSTANCE.getMode().method5226("module.criticals.levitation"));
   }

   public boolean shouldIgnoreEffect() {
      return this.shouldSuppressLevitation() && this.ignore.isSelected();
   }

   public void limitVerticalMovement() {
      if (this.shouldSuppressLevitation() && !this.ignore.isSelected()) {
         Vec3 movement = mc.player.getDeltaMovement();
         mc.player.setDeltaMovement(movement.x, Math.min(movement.y, this.down.isSelected() ? -0.1 : 0.0), movement.z);
      }
   }
}

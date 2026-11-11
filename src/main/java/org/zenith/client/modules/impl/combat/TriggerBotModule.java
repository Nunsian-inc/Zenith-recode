package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.InteractionInputEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.NeuroClicks;
import org.zenith.utility.game.AttackUtils;
import org.zenith.utility.game.AuraUtil;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "TriggerBot",
   category = ModuleCategory.COMBAT,
   description = "module.triggerbot.description"
)
public final class TriggerBotModule extends Module {
   public static final TriggerBotModule INSTANCE = new TriggerBotModule();
   private final GroupSetting safeTarget = new GroupSetting(
      "module.aura.targetWindow",
      "module.aura.targetWindow.desc",
      () -> true,
      MultiSelectSetting.method5224(
         "module.aura.targetTypeSetting",
         "module.aura.targetTypeSetting.desc",
         List.of("module.aura.targetPlayers", "module.aura.noarmor", "module.aura.targetHostile", "module.aura.targetPeaceful")
      ),
      MultiSelectSetting.method5224(
         "module.aura.targetSortSetting",
         "module.aura.targetSortSetting.desc",
         List.of("module.aura.targetFov", "module.aura.targetArmor", "module.aura.targetHp", "module.aura.targetDistance")
      ),
      new BooleanSetting("module.aura.safeTarget", "module.aura.safeTarget.desc", false)
   );
   private final SliderSetting distance = new SliderSetting("module.aura.distance", 3.0F, 0.5F, 6.0F, 0.1F, "module.aura.distance.desc", "b");
   private final BooleanSetting onlyCrit = new BooleanSetting("module.aura.onlyCrit", "module.aura.onlyCrit.desc", true);
   private final BooleanSetting smartCrit = new BooleanSetting("module.aura.smartCrit", "module.aura.smartCrit.desc", false, this.onlyCrit::isEnabled);
   private final ModeSetting cooldownMode = new ModeSetting("module.aura.cooldownMode", "module.aura.cooldownMode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.cooldownMode, "module.aura.slowMode");
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.cooldownMode, "module.aura.speedMode").method2103();
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.cooldownMode, "module.aura.customMode");
   private final ModeSetting.Entry entry4 = new ModeSetting.Entry(this.cooldownMode, "module.triggerbot.neuroMode");
   private final ModeSetting neuroProfile = new ModeSetting("module.triggerbot.neuroProfile", "module.triggerbot.neuroProfile.desc", this.entry4::isSelected);
   private final ModeSetting.Entry entry5 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileStrong").method2103();
   private final ModeSetting.Entry entry6 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileEarly");
   private final ModeSetting.Entry entry7 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileExact");
   private final ModeSetting.Entry entry8 = new ModeSetting.Entry(this.neuroProfile, "module.triggerbot.profileRelaxed");
   private final SliderSetting timeCooldown = new SliderSetting(
      "module.aura.timeCooldown", 0.0F, -1.0F, 1.0F, 0.5F, "module.aura.timeCooldown.desc", "t", this.entry3::isSelected, null
   );
   private final SliderSetting attackCooldown = new SliderSetting(
      "module.aura.attackCooldown", 1.0F, 0.0F, 1.0F, 0.01F, "module.aura.attackCooldown.desc", "%", this.entry3::isSelected, null
   );
   private final BooleanSetting randomDelay = new BooleanSetting("module.aura.randomDelay", "module.aura.randomDelay.desc", false);
   private final ModeSetting sprintMode = new ModeSetting("module.aura.sprintMode", "module.aura.sprintMode.desc");
   private final ModeSetting.Entry entry9 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintHvh");
   private final ModeSetting.Entry entry10 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNormal").method2103();
   private final ModeSetting.Entry entry11 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintLegit");
   private final ModeSetting.Entry entry12 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintSuperLegit");
   private final ModeSetting.Entry entry13 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNone");
   private final ModeSetting.Entry entry14 = new ModeSetting.Entry(this.sprintMode, "module.triggerbot.sprintNeuro");
   private final BooleanSetting neuroWtap = new BooleanSetting(
      "module.triggerbot.neuroWtap", "module.triggerbot.neuroWtap.desc", false, this.entry14::isSelected
   );
   private final MultiSelectSetting settings = new MultiSelectSetting("module.aura.settings", "module.aura.settings.desc");
   private final MultiSelectSetting.Entry entry15 = new MultiSelectSetting.Entry(this.settings, "module.aura.shieldBreak", true);
   private final MultiSelectSetting.Entry entry16 = new MultiSelectSetting.Entry(this.settings, "module.aura.shielRealese", true);
   private final MultiSelectSetting.Entry entry17 = new MultiSelectSetting.Entry(this.settings, "module.aura.eatUseAttack", true);
   private final MultiSelectSetting.Entry entry18 = new MultiSelectSetting.Entry(this.settings, "module.aura.attackIgnoreWals", true);
   private LivingEntity livingEntity = null;
   private int intValue = -1;
   private LivingEntity livingEntity2 = null;
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag = false;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private StopWatch stopWatch2;
   private final NeuroClicks neuroClicks = NeuroClicks.INSTANCE;
   public int intValue2 = 0;
   public int intValue3 = 0;

   private TriggerBotModule() {
   }

   @EventTarget
   public void method5574(EntityAddEvent event) {
      if (event.getEntity() instanceof LocalPlayer) {
         this.livingEntity = null;
         this.livingEntity2 = null;
      }
   }

   @EventTarget
   public void method5575(PostRotationEvent event) {
      if (this.livingEntity != null) {
         if (this.flag2 && this.method5580() && (!this.method5577() || this.neuroClicks.method451(this.method5591(), this.method5578()))) {
            this.flag = false;
            this.flag2 = false;
            this.flag3 = true;
         }
      }
   }

   @EventTarget
   public void method5576(UpdateEvent event) {
      if (this.stopWatch2 == null) {
         this.stopWatch2 = new StopWatch();
      }

      if (mc.player.isDeadOrDying() || this.livingEntity == null) {
         this.flag3 = false;
      }

      if (this.flag3) {
         if (this.entry9.isSelected() && Zenith.getInstance().getServerDetector().isFlag()) {
            mc.player.setSprinting(false);
            mc.player.sendIsSprintingIfNeeded();
         }

         boolean flag = Zenith.getInstance().getServerDetector().isFlag();
         AttackUtils.method816(this.livingEntity);
         this.intValue2 = mc.player.tickCount - this.intValue3;
         this.intValue3 = mc.player.tickCount;
         if (this.entry14.isSelected() && this.neuroClicks.method442()) {
            this.neuroClicks.method452(flag, mc.options.keyUp.isDown(), this.neuroWtap.isEnabled());
         }
      }
   }

   private boolean method5577() {
      return this.entry4.isSelected() && this.neuroClicks.method442();
   }

   private String method5578() {
      if (this.entry6.isSelected()) {
         return "early";
      } else if (this.entry7.isSelected()) {
         return "exact";
      } else {
         return this.entry8.isSelected() ? "relaxed" : "strong";
      }
   }

   @EventTarget
   public void method5579(PreRotationEvent event) {
      this.flag3 = false;
      if (mc.player.isDeadOrDying()) {
         this.livingEntity = null;
         this.neuroClicks.reset();
      } else {
         if ((this.entry4.isSelected() || this.entry14.isSelected()) && this.neuroClicks.method442()) {
            this.neuroClicks.method447();
         }

         this.livingEntity = this.method5597();
         if (mc.crosshairPickEntity instanceof LivingEntity livingEntity
            && AuraUtil.method7447(((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(), livingEntity)) {
            this.livingEntity = livingEntity;
         }

         if (this.livingEntity != null) {
            if (mc.level.getEntity(this.livingEntity.getId()) instanceof LivingEntity livingEntity2) {
               this.livingEntity = livingEntity2;
            } else {
               this.livingEntity = null;
            }

            this.livingEntity2 = this.livingEntity;
         }

         this.flag = false;
         this.flag2 = false;
         if (this.livingEntity == null) {
            this.method5581();
         } else {
            this.flag = this.method5589();
            this.flag2 = this.method5592();
            int selected = InventoryUtils.method922(slot -> mc.player.getInventory().getItem(slot).is(net.minecraft.tags.ItemTags.AXES));
            if (this.entry15.isEnabled()
               && this.stopWatch.hasElapsed(300L)
               && Zenith.getInstance().getTaskScheduler().method5117(TriggerBotModule.class)
               && selected != -1
               && UseItemUtils.method6530(this.livingEntity)) {
               if (mc.player.getMainHandItem().is(net.minecraft.tags.ItemTags.AXES)) {
                  if (this.method5580()) {
                     if (this.entry16.isEnabled() && mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof ShieldItem) {
                        mc.gameMode.releaseUsingItem(mc.player);
                     }

                     AttackUtils.method816(this.livingEntity);
                     this.stopWatch.reset();
                     this.flag = false;
                     this.flag2 = false;
                  }
               } else {
                  if (this.intValue == -1) {
                     this.intValue = mc.player.getInventory().getSelectedSlot();
                  }

                  mc.player.getInventory().setSelectedSlot(selected);
               }
            } else {
               this.method5581();
            }

            if (this.flag && this.entry16.isEnabled() && mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof ShieldItem) {
               mc.gameMode.releaseUsingItem(mc.player);
            }
         }
      }
   }

   public boolean method5580() {
      return this.method5582(rotationManager.getEffectiveRotation());
   }

   private void method5581() {
      if (this.intValue != -1) {
         mc.player.getInventory().setSelectedSlot(this.intValue);
         this.intValue = -1;
      }
   }

   public boolean method5582(Rotation rotation) {
      return this.method5583(rotation, this.method5584(), this.livingEntity.getBoundingBox());
   }

   public boolean method5583(Rotation rotation, Vec3 eyePosition, AABB boundingBox) {
      return TargetUtils.method3212(rotation, eyePosition, boundingBox, this.distance.getValue3(), !this.entry18.isEnabled());
   }

   public Vec3 method5584() {
      return mc.player.getEyePosition(1.0F);
   }

   public Vec3 method5585(AABB aabb, float alpha1) {
      return new Vec3(
         Mth.lerp(0.5, aabb.minX, aabb.maxX), Mth.lerp((double)alpha1, aabb.minY, aabb.maxY), Mth.lerp(0.5, aabb.minZ, aabb.maxZ)
      );
   }

   public AABB method5586() {
      return this.livingEntity.getBoundingBox();
   }

   @EventTarget
   public void method5587(MoveInputEvent event) {
      if (this.livingEntity != null) {
         this.method5594(event);
      }
   }

   @EventTarget(0)
   public void method5588(InteractionInputEvent event) {
      if (this.flag3) {
         event.cancel();
      }
   }

   private boolean method5589() {
      return this.method5590(1);
   }

   private boolean method5590(int n) {
      if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem()
         && (!this.entry16.isEnabled() || !(mc.player.getUseItem().getItem() instanceof ShieldItem))
         && !this.entry17.isEnabled()) {
         return false;
      } else {
         if (this.onlyCrit.isEnabled()) {
            PlayerSimulation playerSimulation = PlayerSimulation.method6031(n);
            if (!AttackUtils.method818(playerSimulation) && !AttackUtils.method822(playerSimulation) && (!this.smartCrit.isEnabled() || mc.options.keyJump.isDown())) {
               return false;
            }
         }

         return this.method5593(1.0F, !this.method5591());
      }
   }

   public boolean method5591() {
      if (mc.player.isFallFlying() || AttackUtils.method817()) {
         return false;
      } else {
         return !this.onlyCrit.isEnabled() ? false : !this.smartCrit.isEnabled() || mc.options.keyJump.isDown();
      }
   }

   public boolean method5592() {
      if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem() && !this.entry17.isEnabled()) {
         return false;
      } else if (!this.method5591()
         || AttackUtils.method819() && (!Zenith.getInstance().getServerDetector().isFlag() || this.entry13.isSelected() || this.entry9.isSelected())) {
         long l;
         return this.randomDelay.isEnabled()
               && !this.method5577()
               && MathUtils.method2650((float)(l = mc.player.tickCount - this.intValue3), this.intValue2) == 0.0F
            ? false
            : this.method5593(0.0F, !this.method5591() || this.flag);
      } else {
         return false;
      }
   }

   public boolean method5593(float f, boolean flag) {
      if (this.method5577()) {
         return this.neuroClicks.method450(this.method5591(), this.method5578(), (int)f);
      } else if (this.cooldownMode.isSelected(1)) {
         return !AttackUtils.method817() && !mc.player.onGround()
            ? mc.player.getAttackStrengthScale(0.5F) > 0.9
            : mc.player.getAttackStrengthScale(0.5F) >= 1.0F;
      } else {
         if (!this.entry.isSelected()) {
            flag = false;
         }

         float f2 = this.entry3.isSelected() ? this.timeCooldown.getValue3() : 1.0F;
         float f3 = this.entry3.isSelected() ? this.attackCooldown.getValue3() : 1.0F;
         return flag && !mc.player.isUnderWater()
            ? mc.player.getAttackStrengthScale(0.0F + f) == 1.0F
            : mc.player.getAttackStrengthScale(f2 + f) >= f3;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.intValue != -1 && mc.player != null) {
         mc.player.getInventory().setSelectedSlot(this.intValue);
      }

      this.intValue = -1;
      this.flag3 = false;
      this.flag = false;
      this.flag2 = false;
      this.livingEntity = null;
      this.livingEntity2 = null;
      this.neuroClicks.reset();
      super.onDisable();
   }

   public void method5594(MoveInputEvent event) {
      if (this.method5596()) {
         if (this.entry14.isSelected()) {
            if (this.neuroClicks.method442()) {
               boolean forward = event.getPlayerInput().forward();
               boolean flag = mc.player.isSprinting() || Zenith.getInstance().getServerDetector().isFlag();
               boolean flag2 = this.method5591() && !mc.player.onGround() && this.method5589();
               boolean flag3;
               if (!(flag3 = this.neuroClicks.method453(forward, flag, flag2)) && forward) {
                  event.setForward(false);
               }

               this.neuroClicks.method454(true, forward, flag3, flag, this.intValue3 == mc.player.tickCount);
            }
         } else {
            boolean down = mc.options.keySprint.isDown();
            boolean flag = mc.options.keyUp.isDown();
            if (this.entry12.isSelected()) {
               if (!mc.player.onGround()) {
                  down = false;
                  if (mc.player.isSprinting()) {
                     flag = false;
                  }
               }
            } else if (!this.flag3 && !mc.player.onGround() && this.method5589()) {
               if (this.entry11.isSelected()) {
                  down = false;
                  if (mc.player.isSprinting()) {
                     flag = false;
                  }
               }

               if (this.entry10.isSelected()) {
                  if (mc.player.isSprinting()) {
                     mc.player.setSprinting(false);
                  }

                  down = false;
               }
            }

            if (!down) {
               mc.options.keySprint.setDown(false);
               event.setSprint(false);
            }

            if (!flag) {
               event.setForward(false);
            }
         }
      }
   }

   @EventTarget
   public void method5595(SprintUpdateEvent event) {
      if (this.livingEntity != null && this.entry10.isSelected() && !this.flag3 && !mc.player.onGround() && this.method5589()) {
         event.setFlag2(false);
      }
   }

   public boolean method5596() {
      return !this.entry13.isSelected() && !AttackUtils.method817();
   }

   private LivingEntity method5597() {
      return AuraUtil.method7446(
         mc.level.entitiesForRendering(),
         this.distance.getValue3() + 0.5F,
         this.entry18.isEnabled(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(1)).method5229(),
         ((BooleanSetting)this.safeTarget.getSettings().get(2)).isEnabled()
      );
   }

   public LivingEntity method5598() {
      return this.isEnabled() ? this.livingEntity : null;
   }

   @Generated
   public LivingEntity getLivingEntity2() {
      return this.livingEntity2;
   }

   @Generated
   public boolean isFlag8() {
      return this.flag;
   }

   @Generated
   public boolean isFlag25() {
      return this.flag2;
   }

   @Generated
   public StopWatch getStopWatch23() {
      return this.stopWatch2;
   }
}

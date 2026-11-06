package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.misc.ElytraHelperModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ItemStackTags;

@ModuleInfo(
   name = "AutoElytraArmor",
   description = "module.autoelytraarmor.description",
   category = ModuleCategory.MOVEMENT
)
public final class AutoElytraArmorModule extends Module {
   public static final AutoElytraArmorModule INSTANCE = new AutoElytraArmorModule();
   private final ModeSetting mode = new ModeSetting(
      "module.autoElytraArmor.mode", "module.autoElytraArmor.mode.desc", "module.autoElytraArmor.mode.onSwap", "module.autoElytraArmor.mode.always"
   );
   private final SliderSetting range = new SliderSetting("module.autoElytraArmor.range", 5.0F, 1.0F, 10.0F, 1.0F, "module.autoElytraArmor.range.desc", "b");
   private final BooleanSetting autoAirStuck = new BooleanSetting("module.autoElytraArmor.autoAirStuck", "module.autoElytraArmor.autoAirStuck.desc", true);
   private final BooleanSetting reverseSwap = new BooleanSetting("module.autoElytraArmor.reverseSwap", "module.autoElytraArmor.reverseSwap.desc", false);
   private Player trackedTarget;
   private boolean targetWasWearingElytra;
   private boolean targetWasWearingChestplate;
   private AutoElytraArmorModule.SwapType pendingSwap = AutoElytraArmorModule.SwapType.NONE;

   private AutoElytraArmorModule() {
   }

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         this.resetState();
      } else if (this.mode.isSelected(1)) {
         this.updateAlwaysMode(player);
      } else {
         this.pendingSwap = AutoElytraArmorModule.SwapType.NONE;
         this.handleTargetEquipmentChange(player);
         this.updateTrackedTarget(this.findTrackedOrGlidingTarget(player));
      }
   }

   private Player findTrackedOrGlidingTarget(Player player) {
      if (this.isValidArmoredTarget(player, this.trackedTarget)) {
         return this.trackedTarget;
      } else if (AuraModule.INSTANCE.method6358() instanceof Player auraTarget && this.isValidGlidingTarget(player, auraTarget)) {
         return auraTarget;
      } else {
         ClientLevel level = mc.level;
         return level == null
            ? null
            : level.players()
               .stream()
               .filter(candidate -> candidate != player)
               .filter(candidate -> this.isValidGlidingTarget(player, candidate))
               .filter(candidate -> !Zenith.getInstance().getFriends().method7626(candidate))
               .min(Comparator.comparingDouble(candidate -> this.getHorizontalDistanceSquared(player, candidate)))
               .orElse(null);
      }
   }

   private void updateAlwaysMode(Player player) {
      Player target = this.findArmoredTarget(player);
      if (target == null) {
         this.pendingSwap = AutoElytraArmorModule.SwapType.NONE;
         this.setAirStuckEnabled(false);
      } else {
         ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
         boolean wearingElytra = chestStack.is(Items.ELYTRA);
         boolean wearingChestplate = ItemStackTags.isChestArmor(chestStack);
         if (this.pendingSwap == AutoElytraArmorModule.SwapType.ELYTRA && wearingElytra
            || this.pendingSwap == AutoElytraArmorModule.SwapType.CHESTPLATE && wearingChestplate) {
            this.pendingSwap = AutoElytraArmorModule.SwapType.NONE;
         }

         boolean targetWearingElytra = target.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
         this.setAirStuckEnabled(!targetWearingElytra);
         if (this.pendingSwap == AutoElytraArmorModule.SwapType.NONE) {
            if (targetWearingElytra) {
               if (wearingChestplate) {
                  this.pendingSwap = AutoElytraArmorModule.SwapType.ELYTRA;
                  ElytraHelperModule.INSTANCE.method7454();
               }
            } else if (wearingElytra) {
               this.pendingSwap = AutoElytraArmorModule.SwapType.CHESTPLATE;
               this.swapToChestplate();
            }
         }
      }
   }

   private Player findArmoredTarget(Player player) {
      if (AuraModule.INSTANCE.method6358() instanceof Player auraTarget && this.isValidArmoredTarget(player, auraTarget)) {
         return auraTarget;
      } else {
         ClientLevel level = mc.level;
         return level == null
            ? null
            : level.players()
               .stream()
               .filter(candidate -> candidate != player)
               .filter(candidate -> this.isValidArmoredTarget(player, candidate))
               .filter(candidate -> !Zenith.getInstance().getFriends().method7626(candidate))
               .min(Comparator.comparingDouble(candidate -> this.getHorizontalDistanceSquared(player, candidate)))
               .orElse(null);
      }
   }

   private boolean isValidArmoredTarget(Player player, Player target) {
      if (target != null && target.isAlive() && !target.isRemoved() && !(this.getHorizontalDistanceSquared(player, target) > this.getRangeSquared())) {
         ItemStack chestStack = target.getItemBySlot(EquipmentSlot.CHEST);
         return chestStack.is(Items.ELYTRA) || ItemStackTags.isChestArmor(chestStack);
      } else {
         return false;
      }
   }

   private boolean isValidGlidingTarget(Player player, Player target) {
      return target != null
         && target.isAlive()
         && !target.isRemoved()
         && target.isFallFlying()
         && target.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)
         && this.getHorizontalDistanceSquared(player, target) <= this.getRangeSquared();
   }

   private void updateTrackedTarget(Player target) {
      if (target == null) {
         this.clearTrackedTarget();
      } else if (this.trackedTarget != target) {
         this.trackedTarget = target;
         this.targetWasWearingElytra = target.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
         this.targetWasWearingChestplate = ItemStackTags.isChestArmor(target.getItemBySlot(EquipmentSlot.CHEST));
      } else if (target.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         this.targetWasWearingElytra = true;
         this.targetWasWearingChestplate = false;
      } else if (ItemStackTags.isChestArmor(target.getItemBySlot(EquipmentSlot.CHEST))) {
         this.targetWasWearingElytra = false;
         this.targetWasWearingChestplate = true;
      }
   }

   private void handleTargetEquipmentChange(Player player) {
      Player target = this.trackedTarget;
      if (target != null) {
         if (!this.isValidArmoredTarget(player, target)) {
            this.clearTrackedTarget();
         } else {
            ItemStack chestStack = target.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.is(Items.ELYTRA)) {
               boolean wasWearingChestplate = this.targetWasWearingChestplate;
               this.targetWasWearingElytra = true;
               this.targetWasWearingChestplate = false;
               if (this.reverseSwap.isEnabled() && wasWearingChestplate && ItemStackTags.isChestArmor(player.getItemBySlot(EquipmentSlot.CHEST))) {
                  ElytraHelperModule.INSTANCE.method7454();
                  this.setAirStuckEnabled(false);
               }
            } else if (!ItemStackTags.isChestArmor(chestStack)) {
               this.targetWasWearingElytra = false;
               this.targetWasWearingChestplate = false;
            } else {
               boolean wasWearingElytra = this.targetWasWearingElytra;
               this.targetWasWearingElytra = false;
               this.targetWasWearingChestplate = true;
               if (wasWearingElytra && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
                  this.swapToChestplate();
               }
            }
         }
      }
   }

   private void swapToChestplate() {
      ElytraHelperModule.INSTANCE.method7454();
      this.setAirStuckEnabled(true);
   }

   private void setAirStuckEnabled(boolean enabled) {
      if (this.autoAirStuck.isEnabled() && AirStuckModule.INSTANCE.isEnabled() != enabled) {
         AirStuckModule.INSTANCE.setToggled(enabled);
      }
   }

   private double getHorizontalDistanceSquared(Player player, Player target) {
      Vec3 targetPosition = target.getPositionCodec().getBase();
      double deltaX = player.getX() - targetPosition.x;
      double deltaZ = player.getZ() - targetPosition.z;
      return deltaX * deltaX + deltaZ * deltaZ;
   }

   private double getRangeSquared() {
      double range = this.range.getValue3();
      return range * range;
   }

   private void resetState() {
      this.clearTrackedTarget();
      this.pendingSwap = AutoElytraArmorModule.SwapType.NONE;
   }

   private void clearTrackedTarget() {
      this.trackedTarget = null;
      this.targetWasWearingElytra = false;
      this.targetWasWearingChestplate = false;
   }

   private static enum SwapType {
      NONE,
      CHESTPLATE,
      ELYTRA;
   }
}

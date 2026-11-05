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
import org.zenith.utility.misc.Util10;

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
   private Player playerEntity;
   private boolean flag;
   private boolean flag2;
   private AutoElytraArmorModule.Type type = AutoElytraArmorModule.Type.NONE;

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
         this.method412(player);
      } else {
         this.type = AutoElytraArmorModule.Type.NONE;
         this.method417(player);
         this.method416(this.method411(player));
      }
   }

   private Player method411(Player player) {
      if (this.method414(player, this.playerEntity)) {
         return this.playerEntity;
      } else if (AuraModule.INSTANCE.method6358() instanceof Player playerEntity && this.method415(player, playerEntity)) {
         return playerEntity;
      } else {
         ClientLevel level = mc.level;
         return level == null
            ? null
            : level.players()
               .stream()
               .filter(var1x -> var1x != player)
               .filter(player2 -> this.method415(player, player2))
               .filter(player2 -> !Zenith.getInstance().getFriends().method7626(player2))
               .min(Comparator.comparingDouble(playerEntity2 -> this.method420(player, playerEntity2)))
               .orElse(null);
      }
   }

   private void method412(Player player) {
      Player player2 = this.method413(player);
      if (player2 == null) {
         this.type = AutoElytraArmorModule.Type.NONE;
         this.method419(false);
      } else {
         ItemStack itemBySlot = player.getItemBySlot(EquipmentSlot.CHEST);
         boolean is = itemBySlot.is(Items.ELYTRA);
         boolean flag = Util10.method2182(itemBySlot);
         if (this.type == AutoElytraArmorModule.Type.ELYTRA && is || this.type == AutoElytraArmorModule.Type.CHESTPLATE && flag) {
            this.type = AutoElytraArmorModule.Type.NONE;
         }

         boolean is2 = player2.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
         this.method419(!is2);
         if (this.type == AutoElytraArmorModule.Type.NONE) {
            if (is2) {
               if (flag) {
                  this.type = AutoElytraArmorModule.Type.ELYTRA;
                  ElytraHelperModule.INSTANCE.method7454();
               }
            } else if (is) {
               this.type = AutoElytraArmorModule.Type.CHESTPLATE;
               this.method418();
            }
         }
      }
   }

   private Player method413(Player player) {
      if (AuraModule.INSTANCE.method6358() instanceof Player playerEntity && this.method414(player, playerEntity)) {
         return playerEntity;
      } else {
         ClientLevel level = mc.level;
         return level == null
            ? null
            : level.players()
               .stream()
               .filter(var1x -> var1x != player)
               .filter(player2 -> this.method414(player, player2))
               .filter(player2 -> !Zenith.getInstance().getFriends().method7626(player2))
               .min(Comparator.comparingDouble(playerEntity2 -> this.method420(player, playerEntity2)))
               .orElse(null);
      }
   }

   private boolean method414(Player player, Player playerEntity) {
      if (playerEntity != null && playerEntity.isAlive() && !playerEntity.isRemoved() && !(this.method420(player, playerEntity) > this.method421())) {
         ItemStack itemBySlot = playerEntity.getItemBySlot(EquipmentSlot.CHEST);
         return itemBySlot.is(Items.ELYTRA) || Util10.method2182(itemBySlot);
      } else {
         return false;
      }
   }

   private boolean method415(Player player, Player playerEntity) {
      return playerEntity != null
         && playerEntity.isAlive()
         && !playerEntity.isRemoved()
         && playerEntity.isFallFlying()
         && playerEntity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)
         && this.method420(player, playerEntity) <= this.method421();
   }

   private void method416(Player playerEntity) {
      if (playerEntity == null) {
         this.method422();
      } else {
         if (this.playerEntity != playerEntity) {
            this.playerEntity = playerEntity;
            this.flag = playerEntity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
            this.flag2 = Util10.method2182(playerEntity.getItemBySlot(EquipmentSlot.CHEST));
         } else if (playerEntity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            this.flag = true;
            this.flag2 = false;
         } else if (Util10.method2182(playerEntity.getItemBySlot(EquipmentSlot.CHEST))) {
            this.flag = false;
            this.flag2 = true;
         }
      }
   }

   private void method417(Player player) {
      Player playerEntity = this.playerEntity;
      if (playerEntity != null) {
         if (!this.method414(player, playerEntity)) {
            this.method422();
         } else {
            ItemStack itemBySlot = playerEntity.getItemBySlot(EquipmentSlot.CHEST);
            if (itemBySlot.is(Items.ELYTRA)) {
               boolean flag = this.flag2;
               this.flag = true;
               this.flag2 = false;
               if (this.reverseSwap.isEnabled() && flag && Util10.method2182(player.getItemBySlot(EquipmentSlot.CHEST))) {
                  ElytraHelperModule.INSTANCE.method7454();
                  this.method419(false);
               }
            } else if (!Util10.method2182(itemBySlot)) {
               this.flag = false;
               this.flag2 = false;
            } else {
               boolean flag = this.flag;
               this.flag = false;
               this.flag2 = true;
               if (flag && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
                  this.method418();
               }
            }
         }
      }
   }

   private void method418() {
      ElytraHelperModule.INSTANCE.method7454();
      this.method419(true);
   }

   private void method419(boolean toggled) {
      if (this.autoAirStuck.isEnabled() && AirStuckModule.INSTANCE.isEnabled() != toggled) {
         AirStuckModule.INSTANCE.setToggled(toggled);
      }
   }

   private double method420(Player player, Player playerEntity) {
      Vec3 base = playerEntity.getPositionCodec().getBase();
      double d = player.getX() - base.x;
      double d2 = player.getZ() - base.z;
      return d * d + d2 * d2;
   }

   private double method421() {
      double d;
      return (d = this.range.getValue3()) * d;
   }

   private void resetState() {
      this.method422();
      this.type = AutoElytraArmorModule.Type.NONE;
   }

   private void method422() {
      this.playerEntity = null;
      this.flag = false;
      this.flag2 = false;
   }

   private static enum Type {
      NONE,
      CHESTPLATE,
      ELYTRA;
   }
}

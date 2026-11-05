package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.InventoryMenu;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PotionUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoPotion",
   category = ModuleCategory.COMBAT,
   description = "module.autopotion.description"
)
public final class AutoPotionModule extends Module {
   public static final AutoPotionModule INSTANCE = new AutoPotionModule();
   private static final int CONST_intValue = 20;
   private final MultiSelectSetting potions = new MultiSelectSetting("module.autoPotion.potions", "module.autoPotion.potions.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.strength", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.speed", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.fireResistance", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.heal", false);
   private final SliderSetting healHealth = new SliderSetting(
      "module.autoPotion.healHealth", 10.0F, 1.0F, 20.0F, 0.5F, "module.autoPotion.healHealth.desc", "hp", this.entry4::isEnabled
   );
   private int intValue;
   private int intValue2;

   private AutoPotionModule() {
   }

   @Override
   public void onEnable() {
      this.intValue = 20;
      this.intValue2 = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.intValue2 = 0;
      super.onDisable();
   }

   @EventTarget
   public void method510(Event5 event) {
      if (this.method512() && this.intValue >= 20 && !this.method513().isEmpty()) {
         Rotation rotation = new Rotation(mc.player.getYRot(), 90.0F);
         rotationManager.method7849(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 8, this);
      }
   }

   @EventTarget
   public void method511(HandleInputEvent event) {
      if (!this.method512()) {
         this.intValue2 = 0;
      } else {
         this.intValue++;
         if (this.intValue >= 20) {
            List<Integer> integers = this.method513();
            if (integers.isEmpty()) {
               this.intValue2 = 0;
            } else {
               Rotation currentRotation = rotationManager.getCurrentRotation();
               Rotation rotation2 = rotationManager.getRotation2();
               if (currentRotation != null && !(currentRotation.getFloatValue2() < 89.0F) && rotation2 != null && !(rotation2.getFloatValue2() < 89.0F)) {
                  if (this.intValue2++ >= 1) {
                     int selectedSlot = mc.player.getInventory().getSelectedSlot();
                     boolean flag = false;

                     for (int slot : integers) {
                        if (PotionUtils.method2323(mc.player.getInventory().getItem(slot))) {
                           if (slot < 9) {
                              PotionUtils.method2329(slot);
                              UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                           } else {
                              PotionUtils.method2329(selectedSlot);
                              PotionUtils.method2328(slot, selectedSlot);
                              UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                              PotionUtils.method2328(slot, selectedSlot);
                           }

                           flag = true;
                        }
                     }

                     PotionUtils.method2329(selectedSlot);
                     this.intValue2 = 0;
                     if (flag) {
                        this.intValue = 0;
                     }
                  }
               } else {
                  this.intValue2 = 0;
               }
            }
         }
      }
   }

   private boolean method512() {
      return mc.player != null
         && mc.level != null
         && mc.gameMode != null
         && mc.getConnection() != null
         && !mc.player.isFallFlying()
         && !mc.player.isUsingItem()
         && mc.player.containerMenu instanceof InventoryMenu;
   }

   private List<Integer> method513() {
      ArrayList<Integer> integers = new ArrayList<>();
      this.method514(integers, this.entry, MobEffects.STRENGTH);
      this.method514(integers, this.entry2, MobEffects.SPEED);
      this.method514(integers, this.entry3, MobEffects.FIRE_RESISTANCE);
      if (this.entry4.isEnabled() && mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.healHealth.getValue3()) {
         int n = PotionUtils.method2325(useItem -> PotionUtils.method2323(useItem) && PotionUtils.method2322(useItem, MobEffects.INSTANT_HEALTH));
         if (n != -1 && !integers.contains(n)) {
            integers.add(n);
         }
      }

      return integers;
   }

   private void method514(List<Integer> integers, MultiSelectSetting.Entry entry, Holder<MobEffect> effect) {
      if (entry.isEnabled() && !mc.player.hasEffect(effect)) {
         int n = PotionUtils.method2325(var1x -> PotionUtils.method2323(var1x) && PotionUtils.method2322(var1x, effect));
         if (n != -1 && !integers.contains(n)) {
            integers.add(n);
         }
      }
   }
}

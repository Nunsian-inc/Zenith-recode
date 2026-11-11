package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "ElytraHelper",
   description = "module.elytrahelper.description",
   category = ModuleCategory.MISC
)
public final class ElytraHelperModule extends Module {
   public static final ElytraHelperModule INSTANCE = new ElytraHelperModule();
   private final BindSetting elytraSetting = new BindSetting("module.elytraHelper.elytraSetting", "module.elytraHelper.elytraSetting.desc");
   private final BindSetting fireworkSetting = new BindSetting("module.elytraHelper.fireworkSetting", "module.elytraHelper.fireworkSetting.desc");
   private final BooleanSetting startSetting = new BooleanSetting("module.elytraHelper.startSetting", "module.elytraHelper.startSetting.desc", false);
   private final BooleanSetting sprintSetting = new BooleanSetting(
      "module.elytraHelper.sprintSetting", "module.elytraHelper.sprintSetting.desc", false, this.startSetting::isEnabled
   );
   private final BooleanSetting onlyHotBar = new BooleanSetting("module.elytraHelper.onlyHotBar", "module.elytraHelper.onlyHotBar.desc", false);

   @Override
   public void onEnable() {
      ChatLogger.method2019("Скорость больше с вкл спринтом + надо быть на версии 1.21.1 и ниже но на хв банит");
      super.onEnable();
   }

   private ElytraHelperModule() {
   }

   @EventTarget
   public void method7452(MoveInputEvent event) {
      if (mc.player.isAlive() && mc.player.containerMenu instanceof InventoryMenu) {
         if (Zenith.getInstance().getTaskScheduler().method5116()
            && this.startSetting.isEnabled()
            && Objects.requireNonNull(mc.player).getItemBySlot(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
            if (mc.player.onGround()) {
               event.setJump(true);
            } else if (!mc.player.isFallFlying()) {
               if (this.sprintSetting.isEnabled()
                  && mc.player.isSprintingPossible(true)
                  && !mc.player.horizontalCollision
                  && !mc.player.isUsingItem()
                  && !mc.player.isUnderWater()) {
                  mc.player.setSprinting(true);
               }

               event.setJump(!mc.player.lastSentInput.jump());
            } else if (mc.player.isFallFlying() && mc.player.horizontalCollision) {
               event.setPlayerInput(
                  new Input(
                     false,
                     event.getPlayerInput().backward(),
                     event.getPlayerInput().left(),
                     event.getPlayerInput().right(),
                     event.getPlayerInput().jump(),
                     event.getPlayerInput().shift(),
                     event.getPlayerInput().sprint()
                  )
               );
            }
         }
      }
   }

   @EventTarget
   public void method7453(KeyEvent event) {
      if (event.isPress(this.elytraSetting.getKeyCode())) {
         this.method7454();
      } else if (event.isPress(this.fireworkSetting.getKeyCode()) && mc.player.isFallFlying()) {
         InventoryUtils.useFireworkInFlight();
      }
   }

   public void method7454() {
      Slot slot = this.method7455();
      if (slot != null) {
         InventoryUtils.method934(slot, 6, true, false);
      }
   }

   private Slot method7455() {
      return Objects.requireNonNull(mc.player).getItemBySlot(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)
         ? InventoryUtils.method916(
            List.of(
               Items.NETHERITE_CHESTPLATE,
               Items.DIAMOND_CHESTPLATE,
               Items.CHAINMAIL_CHESTPLATE,
               Items.IRON_CHESTPLATE,
               Items.GOLDEN_CHESTPLATE,
               Items.LEATHER_CHESTPLATE
            ),
            Comparator.comparingInt(slot -> slot.index)
         )
         : InventoryUtils.method912(mc.player.inventoryMenu, Items.ELYTRA, Comparator.comparingInt(slot -> slot.index), slot -> true);
   }

   public boolean method7456() {
      return this.onlyHotBar.isEnabled();
   }
}

package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.CrystalAuraModule;
import org.zenith.client.modules.impl.misc.ClickActionModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(name = "AutoMend", description = "module.autoMend.description", category = ModuleCategory.PLAYER)
public final class AutoMendModule extends Module {
   public static final AutoMendModule INSTANCE = new AutoMendModule();
   private final SliderSetting threshold = new SliderSetting("module.autoMend.threshold", 80.0F, 1.0F, 100.0F, 1.0F,
      "module.autoMend.threshold.desc");
   private final BooleanSetting armor = new BooleanSetting("module.autoMend.armor", "module.autoMend.armor.desc", true);
   private final BooleanSetting hands = new BooleanSetting("module.autoMend.hands", "module.autoMend.hands.desc", true);
   private final BooleanSetting pauseCombat = new BooleanSetting("module.autoMend.pauseCombat", "module.autoMend.pauseCombat.desc", true);
   private final SliderSetting delay = new SliderSetting("module.autoMend.delay", 150.0F, 50.0F, 1000.0F, 10.0F,
      "module.autoMend.delay.desc", "ms");
   private final BooleanSetting restoreSwap = new BooleanSetting("module.autoMend.restoreSwap", "module.autoMend.restoreSwap.desc", true);
   private final StopWatch throwTimer = new StopWatch();
   private LocalPlayer repairPlayer;
   private ClientLevel repairLevel;
   private RotationRequest rotationRequest;
   private int originalSlot = -1;
   private int bottleSlot = -1;

   private AutoMendModule() {
   }

   @Override
   public void onEnable() {
      this.finishRepair();
      this.repairPlayer = mc.player;
      this.repairLevel = mc.level;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.finishRepair();
      this.repairPlayer = null;
      this.repairLevel = null;
      super.onDisable();
   }

   @EventTarget
   public void onTick(TickEvent event) {
      // UpdateEvent не приходит после отключения от сервера, поэтому отслеживаем смену мира отдельно.
      if (this.repairPlayer != mc.player || this.repairLevel != mc.level) {
         this.finishRepair();
         this.repairPlayer = mc.player;
         this.repairLevel = mc.level;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.originalSlot != -1 && mc.player != null && mc.player.getInventory().getSelectedSlot() != this.bottleSlot) {
         this.finishRepair();
         return;
      }
      if (this.isPaused() || !this.needsRepair() || this.findBottles() == null) {
         this.finishRepair();
         return;
      }
      if (!this.throwTimer.hasElapsed((long)this.delay.getValue3())) {
         return;
      }
      if (this.rotationRequest == null) {
         Rotation rotation = new Rotation(mc.player.getYRot(), 90.0F);
         this.rotationRequest = new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig());
      }
      rotationManager.requestRotation(this.rotationRequest, 4, this);
   }

   @EventTarget(4)
   public void onInput(HandleInputEvent event) {
      if (event.isCancelled() || this.rotationRequest == null) {
         return;
      }
      if (this.isPaused() || !this.needsRepair()) {
         this.finishRepair();
         return;
      }
      if (rotationManager.getModule() != this || rotationManager.getRotationRequest() != this.rotationRequest
         || !rotationManager.getEffectiveRotation().wrappedDeltaTo(this.rotationRequest.getRotation()).isWithinBounds(180.0F, 5.0F)) {
         return;
      }
      Slot bottles = this.findBottles();
      if (bottles == null) {
         this.finishRepair();
         return;
      }
      if (mc.player.getCooldowns().isOnCooldown(bottles.getItem()) || !this.throwTimer.hasElapsed((long)this.delay.getValue3())) {
         return;
      }

      // Предмет в основной руке должен снова быть надет до появления сфер опыта.
      boolean restoreImmediately = this.restoreSwap.isEnabled() || this.hands.isEnabled() && this.needsRepair(mc.player.getMainHandItem());
      int hotbarSlot = bottles.getContainerSlot();
      if (hotbarSlot == 40) {
         UseItemUtils.useItem(InteractionHand.OFF_HAND);
      } else if (!restoreImmediately && hotbarSlot >= 0 && hotbarSlot < 9) {
         if (this.originalSlot == -1) {
            this.originalSlot = mc.player.getInventory().getSelectedSlot();
         }
         this.bottleSlot = hotbarSlot;
         mc.player.getInventory().setSelectedSlot(hotbarSlot);
         UseItemUtils.useItem(InteractionHand.MAIN_HAND);
      } else if (mc.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE)) {
         UseItemUtils.useItem(InteractionHand.MAIN_HAND);
      } else {
         // Слоты основного инвентаря не оставляем переставленными даже при отключённом возврате.
         InventoryUtils.method900(bottles, InteractionHand.MAIN_HAND, false);
         try {
            UseItemUtils.useItem(InteractionHand.MAIN_HAND);
         } finally {
            InventoryUtils.method900(bottles, InteractionHand.MAIN_HAND, false);
         }
      }
      event.cancel();
      this.throwTimer.reset();
      this.releaseRotation();
   }

   private boolean isPaused() {
      return mc.player == null || mc.level == null || mc.gameMode == null || mc.player.isDeadOrDying() || mc.player.isSpectator()
         || mc.gui.screen() != null || mc.player.isUsingItem() || mc.options.keyUse.isDown() || mc.options.keyAttack.isDown()
         || mc.player.containerMenu != mc.player.inventoryMenu || !mc.player.containerMenu.getCarried().isEmpty()
         || ClickActionModule.INSTANCE.isEnabled() && UseItemUtils.method6521(ClickActionModule.INSTANCE.getExpBind())
         || this.pauseCombat.isEnabled() && (AuraModule.INSTANCE.isEnabled() || CrystalAuraModule.INSTANCE.isEnabled());
   }

   private Slot findBottles() {
      return InventoryUtils.method910(slot -> slot.container == mc.player.getInventory() && slot.getContainerSlot() >= 0
         && (slot.getContainerSlot() < 36 || slot.getContainerSlot() == 40) && slot.getItem().is(Items.EXPERIENCE_BOTTLE));
   }

   private boolean needsRepair() {
      if (this.hands.isEnabled() && (this.needsRepair(mc.player.getMainHandItem()) || this.needsRepair(mc.player.getOffhandItem()))) {
         return true;
      }
      if (this.armor.isEnabled()) {
         for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (this.needsRepair(mc.player.getItemBySlot(slot))) {
               return true;
            }
         }
      }
      return false;
   }

   private boolean needsRepair(ItemStack stack) {
      if (stack.isEmpty() || !stack.isDamageableItem() || stack.getMaxDamage() <= 0 || stack.getDamageValue() <= 0
         || (stack.getMaxDamage() - (double)stack.getDamageValue()) * 100.0 / stack.getMaxDamage() >= this.threshold.getValue3()) {
         return false;
      }
      return stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
         .getLevel(mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING)) > 0;
   }

   private void finishRepair() {
      // В другом мире слоты прежнего игрока не трогаем; ручное переключение также не отменяем.
      if (this.repairPlayer != null && mc.player == this.repairPlayer && mc.level == this.repairLevel
         && this.originalSlot != -1 && mc.player.getInventory().getSelectedSlot() == this.bottleSlot) {
         mc.player.getInventory().setSelectedSlot(this.originalSlot);
      }
      this.originalSlot = -1;
      this.bottleSlot = -1;
      this.releaseRotation();
      this.throwTimer.reset();
   }

   private void releaseRotation() {
      if (this.rotationRequest != null) {
         // У очереди нет удаления по владельцу: заменяем только свою заявку уже истёкшей.
         rotationManager.requestRotation(this.rotationRequest, 4, this, 0);
         rotationManager.getTickingList().peekEntry();
         if (rotationManager.getModule() == this && rotationManager.getRotationRequest() == this.rotationRequest) {
            rotationManager.setCurrentRotation(null);
         }
         this.rotationRequest = null;
      }
   }
}

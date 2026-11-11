package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.game.PotionUtils;
import org.zenith.utility.misc.KeyCompat;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

// Полный порт предоставленного ThunderHack MiddleClick.java; другие действия не вынесены в отдельные модули.
@ModuleInfo(name = "MiddleClick", description = "module.middleClick.description", category = ModuleCategory.MISC)
public final class MiddleClickModule extends Module {
   public static final MiddleClickModule INSTANCE = new MiddleClickModule();
   private final ModeSetting onBlock = this.actionSetting("onBlock", Action.Xp);
   private final ModeSetting onAir = this.actionSetting("onAir", Action.Pearl);
   private final ModeSetting onEntity = this.actionSetting("onEntity", Action.Friend);
   private final ModeSetting onFlying = this.actionSetting("onElytra", Action.Firework);
   private final BooleanSetting silent = new BooleanSetting("module.middleClick.silent", "module.middleClick.silent.desc", true);
   private final BooleanSetting inventory = new BooleanSetting("module.middleClick.inventory", "module.middleClick.inventory.desc", true);
   private final SliderSetting swapDelay = new SliderSetting("module.middleClick.swapDelay", 100.0F, 0.0F, 1000.0F, 1.0F,
      "module.middleClick.swapDelay.desc", "ms", () -> !this.silent.isEnabled());
   private final BooleanSetting antiWaste = new BooleanSetting("module.middleClick.antiWaste", "module.middleClick.antiWaste.desc", true);
   private final SliderSetting durability = new SliderSetting("module.middleClick.stopOn", 90.0F, 0.0F, 100.0F, 1.0F,
      "module.middleClick.stopOn.desc", "%");
   private final GroupSetting antiWasteGroup = new GroupSetting("module.middleClick.antiWasteGroup", this.antiWaste, this.durability);
   public final BooleanSetting antiPickUp = new BooleanSetting("module.middleClick.antiPickUp", "module.middleClick.antiPickUp.desc", true);
   private final BooleanSetting feetExp = new BooleanSetting("module.middleClick.feetXP", "module.middleClick.feetXP.desc", false);
   private final BooleanSetting autoXp = new BooleanSetting("module.middleClick.autoRepair", "module.middleClick.autoRepair.desc", false);
   private final StopWatch timer = new StopWatch();
   private final List<DelayedSwap> delayedSwaps = new ArrayList<>();
   private String state = "none";
   private LocalPlayer postXpPlayer;
   private ClientLevel postXpLevel;

   private MiddleClickModule() {
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.onBlock, this.onAir, this.onEntity, this.onFlying, this.silent, this.inventory, this.swapDelay,
         this.antiWasteGroup, this.antiPickUp, this.feetExp, this.autoXp);
   }

   public String getDisplayInfo() {
      return this.state;
   }

   private ModeSetting actionSetting(String name, Action action) {
      ModeSetting setting = new ModeSetting("module.middleClick." + name, "module.middleClick." + name + ".desc");
      for (Action value : Action.values()) {
         new ModeSetting.Entry(setting, "module.middleClick.action." + value.name());
      }
      setting.set("module.middleClick.action." + action.name());
      return setting;
   }

   private Action action(ModeSetting setting) {
      return Action.values()[setting.getIndex()];
   }

   @Override
   public void onDisable() {
      this.postXpPlayer = null;
      this.postXpLevel = null;
      for (int i = this.delayedSwaps.size() - 1; i >= 0; i--) {
         this.delayedSwaps.get(i).restore();
      }
      this.delayedSwaps.clear();
      super.onDisable();
   }

   @EventTarget
   public void onInput(HandleInputPostEvent event) {
      if (mc.player != null && mc.level != null && mc.gui.screen() == null && this.antiPickUp.isEnabled()) {
         // Съедаем только клики vanilla pick, оставляя isDown: TH проверяет удержание и допускает переназначение клавиши.
         while (mc.options.keyPickItem.consumeClick()) {
         }
      }
   }

   @EventTarget
   public void onSync(UpdateEvent event) {
      this.postXpPlayer = null;
      this.postXpLevel = null;
      this.advanceDelayedSwaps();
      if (mc.player == null || mc.level == null || mc.gameMode == null) {
         return;
      }

      // Автопочинка TH предшествует проверке GUI и не зависит от удержания клавиши или AntiWaste.
      if (this.autoXp.isEnabled() && this.needXp()) {
         int xpSlot = this.findOrRefillXp();
         if (xpSlot != -1) {
            this.useXp(xpSlot);
            this.state = "Auto Repairing";
            return;
         }
      }

      if (mc.gui.screen() == null) {
         Action action;
         if (mc.player.isFallFlying()) {
            action = this.action(this.onFlying);
         } else if (mc.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof Player) {
            action = this.action(this.onEntity);
         } else if (mc.hitResult instanceof BlockHitResult hit) {
            action = this.action(mc.level.isEmptyBlock(hit.getBlockPos()) ? this.onAir : this.onBlock);
         } else {
            action = this.action(this.onAir);
         }
         if (mc.options.keyPickItem.isDown() && !this.clickActionOwnsPickKey()) {
            this.doAction(action);
         }
         this.state = action.name();
      }
   }

   @EventTarget
   public void onPostSync(PostMotionEvent event) {
      LocalPlayer player = this.postXpPlayer;
      ClientLevel level = this.postXpLevel;
      this.postXpPlayer = null;
      this.postXpLevel = null;
      // EventSync.addPostAction из TH соответствует завершению sendPosition, а не следующему тику.
      if (player != null && player == mc.player && level == mc.level && mc.gameMode != null
         && mc.options.keyPickItem.isDown() && !this.clickActionOwnsPickKey()) {
         int slot = this.findOrRefillXp();
         if (slot != -1) {
            this.useXp(slot);
         }
      }
   }

   private boolean clickActionOwnsPickKey() {
      ClickActionModule clickAction = ClickActionModule.INSTANCE;
      // KeyEvent ClickAction приходит раньше мыши и использует отпускание; отдаём ему весь жест, не только press.
      if (!clickAction.isEnabled()) {
         return false;
      }
      // Учитываем переназначение vanilla pick и те же GLFW-коды, что использует KeyEvent ClickAction.
      InputConstants.Key key = InputConstants.getKey(mc.options.keyPickItem.saveString());
      int code = key.getType() == InputConstants.Type.MOUSE ? KeyCompat.mouseToGlfw(key.getValue()) : KeyCompat.toGlfw(key.getValue());
      return clickAction.getSettings().stream().filter(setting -> setting instanceof BindSetting)
         .anyMatch(setting -> ((BindSetting)setting).getKeyCode() == code && code != -1);
   }

   private void doAction(Action action) {
      switch (action) {
         case HealingPot -> {
            if (this.timer.hasElapsedAndReset(250L)) {
               int hotbarSlot = InventoryUtils.method922(slot -> this.isHealingPotion(mc.player.getInventory().getItem(slot)));
               if (hotbarSlot != -1) {
                  // У TH для зелья из хотбара нет пакета взмаха, даже при Silent=false.
                  this.useHotbar(hotbarSlot, false, false);
               } else {
                  int slot = -1;
                  for (int i = 36; i >= 0; i--) {
                     if (this.isHealingPotion(mc.player.getInventory().getItem(i))) {
                        slot = i < 9 ? i + 36 : i;
                        break;
                     }
                  }
                  if (slot != -1) {
                     this.useInventory(slot, true);
                  }
               }
            }
         }
         case Firework -> {
            if (this.timer.hasElapsedAndReset(250L)) {
               this.useThrowable(Items.FIREWORK_ROCKET);
            }
         }
         case Friend -> {
            if (this.timer.hasElapsedAndReset(800L) && mc.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof Player player) {
               String name = player.getName().getString();
               var friends = Zenith.getInstance().getFriends();
               boolean friend = friends.getItems().contains(name);
               if (friend) {
                  friends.method7629(name);
               } else {
                  friends.add(name);
               }
               friends.save();
               String message = Zenith.getInstance().getLocalization().translate("module.middleClick.friend." + (friend ? "removed" : "added"));
               ChatLogger.method2019(message.replace("%s", name));
            }
         }
         case Pearl -> {
            if (this.timer.hasElapsedAndReset(500L)) {
               this.sendCameraRotation();
               this.useThrowable(Items.ENDER_PEARL);
            }
         }
         case Xp -> {
            if (this.antiWaste.isEnabled() && !this.needXp()) {
               return;
            }
            if (this.feetExp.isEnabled()) {
               mc.player.setXRot(90.0F);
            }
            this.postXpPlayer = mc.player;
            this.postXpLevel = mc.level;
         }
         case None -> {
         }
      }
   }

   private boolean needXp() {
      for (ItemStack stack : ArmorSlots.method5384(mc.player)) {
         if (!stack.isEmpty() && stack.getMaxDamage() > 0
            && (stack.getMaxDamage() - stack.getDamageValue()) * 100.0 / stack.getMaxDamage() < this.durability.getValue3()) {
            return true;
         }
      }
      return false;
   }

   private boolean isHealingPotion(ItemStack stack) {
      return PotionUtils.method2323(stack) && PotionUtils.method2322(stack, MobEffects.INSTANT_HEALTH);
   }

   private int findHotbar(Item item) {
      return InventoryUtils.method922(slot -> mc.player.getInventory().getItem(slot).is(item));
   }

   private Slot findInventory(Item item) {
      return InventoryUtils.method907(item, slot -> slot.container == mc.player.getInventory()
         && slot.getContainerSlot() >= 9 && slot.getContainerSlot() < 36);
   }

   private int findOrRefillXp() {
      int slot = this.findHotbar(Items.EXPERIENCE_BOTTLE);
      if (slot == -1) {
         Slot inventorySlot = this.findInventory(Items.EXPERIENCE_BOTTLE);
         if (inventorySlot != null) {
            int emptySlot = InventoryUtils.method922(index -> mc.player.getInventory().getItem(index).isEmpty());
            if (emptySlot != -1) {
               InventoryUtils.method902(inventorySlot, emptySlot);
               if (mc.player.getInventory().getItem(emptySlot).is(Items.EXPERIENCE_BOTTLE)) {
                  slot = emptySlot;
               }
            }
         }
      }
      return slot;
   }

   private void useXp(int slot) {
      if (this.feetExp.isEnabled()) {
         mc.player.setXRot(90.0F);
      }
      int originalSlot = mc.player.getInventory().getSelectedSlot();
      this.selectSlot(slot);
      try {
         this.sendUse(true);
      } finally {
         if (this.silent.isEnabled()) {
            this.selectSlot(originalSlot);
         }
      }
   }

   private void useThrowable(Item item) {
      int hotbarSlot = this.findHotbar(item);
      if (this.silent.isEnabled()) {
         if (hotbarSlot != -1) {
            this.useHotbar(hotbarSlot, true, true);
         } else if (item == Items.ENDER_PEARL || this.inventory.isEnabled()) {
            // Сохраняем TH: Inventory ограничивает Firework, но не Pearl, HealingPot и XP.
            Slot slot = this.findInventory(item);
            if (slot != null) {
               this.useInventory(slot.index, true);
            }
         }
      } else if (hotbarSlot != -1) {
         DelayedSwap swap = new DelayedSwap(hotbarSlot, mc.player.getInventory().getSelectedSlot(),
            Math.max(0, Math.min(1000, Math.round(this.swapDelay.getValue3()))), false);
         if (!swap.advance()) {
            this.delayedSwaps.add(swap);
         }
      }
   }

   private void useHotbar(int slot, boolean swing, boolean explicitPacket) {
      int originalSlot = mc.player.getInventory().getSelectedSlot();
      try {
         if (explicitPacket) {
            this.selectSlotWithPacket(slot);
         } else {
            this.selectSlot(slot);
         }
         this.sendUse(swing);
      } finally {
         if (explicitPacket) {
            this.selectSlotWithPacket(originalSlot);
         } else {
            this.selectSlot(originalSlot);
         }
      }
   }

   private void useInventory(int slot, boolean swing) {
      int originalSlot = mc.player.getInventory().getSelectedSlot();
      ItemStack expected = mc.player.containerMenu.getSlot(slot).getItem().copy();
      if (expected.isEmpty()) {
         return;
      }
      InventoryUtils.method904(slot, originalSlot, ContainerInput.SWAP, false);
      // Отменённый ClickSlotEvent не меняет руку: не используем чужой предмет и не делаем ложный обратный свап.
      if (!ItemStack.isSameItemSameComponents(mc.player.getMainHandItem(), expected)) {
         return;
      }
      try {
         this.sendUse(swing);
      } finally {
         InventoryUtils.method904(slot, originalSlot, ContainerInput.SWAP, false);
      }
   }

   private void selectSlotWithPacket(int slot) {
      mc.player.getInventory().setSelectedSlot(slot);
      mc.player.connection.send(new ServerboundSetCarriedItemPacket(slot));
   }

   private void selectSlot(int slot) {
      mc.player.getInventory().setSelectedSlot(slot);
      mc.gameMode.ensureHasSentCarriedItem();
   }

   private void sendCameraRotation() {
      if (AuraModule.INSTANCE.method6358() != null) {
         mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(mc.player.getYRot(), mc.player.getXRot(),
            mc.player.onGround(), mc.player.horizontalCollision));
      }
   }

   private void sendUse(boolean swing) {
      float yaw = mc.player.getYRot();
      float pitch = mc.player.getXRot();
      // useItem в Zenith подменяет направление ротацией Aura; TH явно отправляет направление камеры.
      UseItemUtils.method6502(sequence -> new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, sequence, yaw, pitch));
      if (swing) {
         mc.player.connection.send(ServerboundPunchPacket.INSTANCE);
      }
   }

   private void advanceDelayedSwaps() {
      this.delayedSwaps.removeIf(DelayedSwap::advance);
   }

   // Аналог PearlThread: обе задержки исполняются в клиентском потоке; перекрывающиеся задания TH сохраняются.
   private final class DelayedSwap {
      private final LocalPlayer player = mc.player;
      private final ClientLevel level = mc.level;
      private final int slot;
      private final int originalSlot;
      private final int delay;
      private final boolean inventorySwap;
      private final boolean swapApplied;
      private long stageAt;
      private boolean thrown;

      private DelayedSwap(int slot, int originalSlot, int delay, boolean inventorySwap) {
         this.slot = slot;
         this.originalSlot = originalSlot;
         this.delay = delay;
         this.inventorySwap = inventorySwap;
         if (inventorySwap) {
            ItemStack expected = mc.player.containerMenu.getSlot(slot).getItem().copy();
            InventoryUtils.method904(slot, originalSlot, ContainerInput.SWAP, false);
            this.swapApplied = !expected.isEmpty() && ItemStack.isSameItemSameComponents(mc.player.getMainHandItem(), expected);
         } else {
            MiddleClickModule.this.selectSlot(slot);
            this.swapApplied = true;
         }
         this.stageAt = System.nanoTime() / 1_000_000L + delay;
      }

      private boolean advance() {
         if (!this.swapApplied || mc.player != this.player || mc.level != this.level || mc.gameMode == null) {
            return true;
         }
         if (MiddleClickModule.this.clickActionOwnsPickKey()) {
            this.restore();
            return true;
         }
         if (System.nanoTime() / 1_000_000L < this.stageAt) {
            return false;
         }
         if (this.thrown) {
            this.restore();
            return true;
         }
         try {
            if (this.inventorySwap) {
               MiddleClickModule.this.sendCameraRotation();
            }
            MiddleClickModule.this.sendUse(true);
            this.thrown = true;
            this.stageAt = System.nanoTime() / 1_000_000L + this.delay;
         } catch (RuntimeException exception) {
            this.restore();
            throw exception;
         }
         if (this.delay == 0) {
            this.restore();
            return true;
         }
         return false;
      }

      private void restore() {
         if (this.swapApplied && mc.player == this.player && mc.level == this.level && mc.gameMode != null) {
            if (this.inventorySwap) {
               InventoryUtils.method904(this.slot, this.originalSlot, ContainerInput.SWAP, false);
            } else {
               MiddleClickModule.this.selectSlot(this.originalSlot);
            }
         }
      }
   }

   public enum Action {
      HealingPot, Firework, Friend, Pearl, Xp, None
   }
}

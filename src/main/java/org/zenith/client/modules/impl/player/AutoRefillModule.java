package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.SendPacketEvent;
import org.zenith.client.events.SetScreenEvent;
import org.zenith.client.events.SlotClickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(name = "AutoRefill", description = "module.autoRefill.description", category = ModuleCategory.PLAYER)
public final class AutoRefillModule extends Module {
   public static final AutoRefillModule INSTANCE = new AutoRefillModule();
   private final SliderSetting threshold = new SliderSetting(
      "module.autoRefill.threshold", 8, 0, 32, 1, "module.autoRefill.threshold.desc", ""
   );
   private final SliderSetting delay = new SliderSetting(
      "module.autoRefill.delay", 250, 100, 1000, 50, "module.autoRefill.delay.desc", "ms"
   );
   private final ModeSetting filter = new ModeSetting(
      "module.autoRefill.filter", "module.autoRefill.filter.desc",
      "module.autoRefill.all", "module.autoRefill.selected", "module.autoRefill.consumables"
   );
   // Выбор предметов через удобный список с поиском (как в BlockESP), вместо ручного ввода ID.
   private final BlockListSetting selectedItems = new BlockListSetting(
      "module.autoRefill.items", "module.autoRefill.items.desc", new ArrayList<>(), () -> this.filter.isSelected(1)
   );
   private final BooleanSetting pauseWhileUsing = new BooleanSetting(
      "module.autoRefill.pauseWhileUsing", "module.autoRefill.pauseWhileUsing.desc", true
   );
   private final StopWatch watch = new StopWatch();
   private final ItemStack[] remembered = new ItemStack[9];
   private final long[] emptySince = new long[9];
   private final AtomicLong inventoryChanges = new AtomicLong();
   private long observedChanges;
   private LocalPlayer owner;
   private ClientLevel world;
   private Screen screen;
   private boolean ownClick;
   private SlotClickEvent ownSlotClick;

   private AutoRefillModule() {
      this.clearHistory();
      this.selectedItems.setVisible(() -> this.filter.isSelected(1));
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   @EventTarget
   public void onScreen(SetScreenEvent event) {
      this.inventoryChanges.incrementAndGet();
   }

   @EventTarget
   public void onSlotClick(SlotClickEvent event) {
      if (event != this.ownSlotClick) {
         this.inventoryChanges.incrementAndGet();
      }
   }

   @EventTarget
   public void onReceive(PacketReceiveEvent event) {
      // Полная синхронизация, в том числе откат сервером, отменяет старые намерения.
      if (event.getPacket() instanceof ClientboundContainerSetContentPacket) {
         this.inventoryChanges.incrementAndGet();
      } else if (event.getPacket() instanceof ClientboundContainerSetSlotPacket packet
         && packet.getContainerId() == 0 && packet.getSlot() >= 36 && packet.getSlot() <= 44) {
         this.forgetChangedHotbarItem(packet.getSlot() - 36, packet.getItem());
      } else if (event.getPacket() instanceof ClientboundSetPlayerInventoryPacket packet && packet.slot() >= 0 && packet.slot() < 9) {
         this.forgetChangedHotbarItem(packet.slot(), packet.contents());
      }
   }

   private void forgetChangedHotbarItem(int hotbar, ItemStack incoming) {
      // Пустота после расходования стека сохраняет цель рефила, а новая вещь инвалидирует её.
      if (!incoming.isEmpty() && !this.remembered[hotbar].isEmpty()
         && !ItemStack.isSameItemSameComponents(incoming, this.remembered[hotbar])) {
         this.remembered[hotbar] = ItemStack.EMPTY;
         this.emptySince[hotbar] = 0;
      }
   }

   @EventTarget
   public void onSend(SendPacketEvent event) {
      if (this.ownClick) {
         return;
      }
      var packet = event.getPacket2();
      if (packet instanceof ServerboundContainerClickPacket
         || packet instanceof ServerboundContainerClosePacket
         || packet instanceof ServerboundSetCreativeModeSlotPacket
         || packet instanceof ServerboundPlayerActionPacket action
            && (action.getAction() == ServerboundPlayerActionPacket.Action.DROP_ITEM
               || action.getAction() == ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS
               || action.getAction() == ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND)) {
         this.inventoryChanges.incrementAndGet();
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (!this.canRefill()) {
         this.reset();
         return;
      }
      long changes = this.inventoryChanges.get();
      if (this.owner != mc.player || this.world != mc.level || this.screen != mc.gui.screen() || this.observedChanges != changes) {
         this.reset();
         this.owner = mc.player;
         this.world = mc.level;
         this.screen = mc.gui.screen();
         this.observedChanges = changes;
         this.rememberHotbar();
         return;
      }
      this.rememberHotbar();
      if (this.pauseWhileUsing.isEnabled() && mc.player.isUsingItem()) {
         this.watch.reset();
         return;
      }
      if (!this.watch.hasElapsed((long)this.delay.getValue3())) {
         return;
      }
      for (int hotbar = 0; hotbar < 9; hotbar++) {
         ItemStack current = mc.player.getInventory().getItem(hotbar);
         ItemStack wanted = current.isEmpty() ? this.remembered[hotbar] : current;
         if (wanted.isEmpty() || !this.accepts(wanted) || current.getCount() > this.threshold.getValue3()) {
            continue;
         }
         Slot source = InventoryUtils.method911(
            slot -> slot.container == mc.player.getInventory() && slot.getContainerSlot() >= 9 && slot.getContainerSlot() < 36
               && slot.getItem().getCount() > current.getCount() && ItemStack.isSameItemSameComponents(wanted, slot.getItem()),
            Comparator.comparingInt(slot -> slot.getItem().getCount())
         );
         if (source != null && this.refill(source, hotbar, current.copy(), changes)) {
            this.watch.reset();
            this.clearHistory();
            this.rememberHotbar();
            return;
         }
      }
   }

   private boolean canRefill() {
      return mc.player != null && mc.level != null && mc.gameMode != null && mc.player.isAlive() && !mc.player.isSpectator()
         && !mc.player.getAbilities().instabuild && mc.player.containerMenu == mc.player.inventoryMenu
         && mc.player.inventoryMenu.getCarried().isEmpty()
         && (mc.gui.screen() == null || mc.gui.screen() instanceof InventoryScreen);
   }

   private boolean refill(Slot source, int hotbar, ItemStack current, long changes) {
      InventoryMenu menu = mc.player.inventoryMenu;
      ItemStack replacement = source.getItem().copy();
      Slot target = menu.getSlot(36 + hotbar);
      int stateId = menu.getStateId();
      if (!source.mayPickup(mc.player) || !target.mayPickup(mc.player) || !target.mayPlace(replacement)
         || !source.mayPlace(current) || replacement.getCount() > target.getMaxStackSize(replacement)
         || current.getCount() > source.getMaxStackSize(current)) {
         return false;
      }
      SlotClickEvent click = new SlotClickEvent(menu.containerId, source.index, hotbar, ContainerInput.SWAP);
      this.ownSlotClick = click;
      try {
         EventManager.call(click);
      } finally {
         this.ownSlotClick = null;
      }
      // Слушатели клика могли сменить экран, меню или содержимое. Отложенных кликов нет.
      if (click.isCancelled() || !this.canRefill() || mc.player != this.owner || mc.level != this.world || mc.gui.screen() != this.screen
         || mc.player.inventoryMenu != menu || menu.getStateId() != stateId || this.inventoryChanges.get() != changes
         || !ItemStack.matches(current, target.getItem()) || !ItemStack.matches(replacement, source.getItem())) {
         return false;
      }
      this.ownClick = true;
      try {
         // Один атомарный SWAP: остаток возвращается в исходный слот, курсор не используется.
         InventoryUtils.method901(source, hotbar, false);
      } finally {
         this.ownClick = false;
      }
      return true;
   }

   private void rememberHotbar() {
      long now = System.currentTimeMillis();
      for (int slot = 0; slot < 9; slot++) {
         ItemStack stack = mc.player.getInventory().getItem(slot);
         if (!stack.isEmpty()) {
            this.remembered[slot] = stack.isStackable() ? stack.copy() : ItemStack.EMPTY;
            this.emptySince[slot] = 0;
         } else if (!this.remembered[slot].isEmpty()) {
            if (this.emptySince[slot] == 0) {
               this.emptySince[slot] = now;
            } else if (now - this.emptySince[slot] > 2000) {
               // Не восстанавливаем давно освобождённый слот по устаревшей памяти.
               this.remembered[slot] = ItemStack.EMPTY;
            }
         }
      }
   }

   private boolean accepts(ItemStack stack) {
      if (!stack.isStackable()) {
         return false;
      }
      if (this.filter.isSelected(1)) {
         // Выбранные предметы: проверяем по списку (поддержка старых записей "minecraft:id" и коротких id).
         if (this.selectedItems.method1622(stack.getItem())) {
            return true;
         }
         String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
         String shortId = id.startsWith("minecraft:") ? id.substring("minecraft:".length()) : id;
         return this.selectedItems.contains(id) || this.selectedItems.contains(shortId);
      }
      return !this.filter.isSelected(2) || stack.getItem() instanceof BlockItem || stack.has(DataComponents.CONSUMABLE)
         || stack.is(Items.ENDER_PEARL) || stack.is(Items.SNOWBALL) || stack.is(Items.EGG)
         || stack.is(Items.EXPERIENCE_BOTTLE) || stack.is(Items.FIREWORK_ROCKET) || stack.is(Items.END_CRYSTAL);
   }

   private void clearHistory() {
      Arrays.fill(this.remembered, ItemStack.EMPTY);
      Arrays.fill(this.emptySince, 0);
   }

   private void reset() {
      this.clearHistory();
      this.owner = null;
      this.world = null;
      this.screen = null;
      this.watch.reset();
   }
}
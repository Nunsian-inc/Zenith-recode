package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.api.SortManager;

public class SortCommand extends CommandAbstract {
   private static final int INVENTORY_SIZE = 36;
   private static final int MAX_PACKETS_PER_TICK = 10;
   private String activeLayoutName;
   private SortCommand.SortTask sortTask;
   private final SuggestionProvider<SharedSuggestionProvider> layoutSuggestions = (commandContext, builder) -> SharedSuggestionProvider.suggest(
      this.getSortManager().method7002().stream().map(SortManager.Entry2::getName), builder
   );

   public SortCommand() {
      super("sort");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2019("Использование: .sort <save/load/list/delete>");
         return 1;
      });
      literalArgumentBuilder.then(literal("save").then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.save(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
      literalArgumentBuilder.then(((LiteralArgumentBuilder)literal("load").executes(var1x -> {
         this.load(null);
         return 1;
      })).then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.load(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
      literalArgumentBuilder.then(literal("list").executes(var1x -> {
         this.list();
         return 1;
      }));
      literalArgumentBuilder.then(literal("delete").then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.delete(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
   }

   private void save(String activeLayoutName) {
      if (this.isInvalidName(activeLayoutName)) {
         ChatLogger.method2021("Имя может содержать только буквы, цифры, '.', '_' и '-'");
      } else {
         LocalPlayer availablePlayer = this.getAvailablePlayer();
         if (availablePlayer != null) {
            ArrayList arrayList = new ArrayList();
            Inventory inventory = availablePlayer.getInventory();

            for (int i = 0; i < 36; i++) {
               ItemStack stack = inventory.getItem(i);
               if (!stack.isEmpty()) {
                  arrayList.add(new SortManager.Entry(i, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), stack.isEnchanted(), this.getCustomDataKeys(stack)));
               }
            }

            if (this.getSortManager().method7004(activeLayoutName, arrayList)) {
               this.activeLayoutName = activeLayoutName;
               ChatLogger.method2019("Расположение инвентаря '" + activeLayoutName + "' сохранено");
            } else {
               ChatLogger.method2021("Не удалось сохранить расположение '" + activeLayoutName + "'");
            }
         }
      }
   }

   private void load(String string) {
      LocalPlayer availablePlayer = this.getAvailablePlayer();
      if (availablePlayer != null) {
         if (!availablePlayer.containerMenu.getCarried().isEmpty()) {
            ChatLogger.method2021("Освободи курсор перед сортировкой инвентаря");
         } else {
            SortManager.Entry2 layout = this.findLayout(string);
            if (layout == null) {
               String str = string == null ? "" : " '" + string + "'";
               ChatLogger.method2021("Сохраненное расположение" + str + " не найдено");
            } else {
               var desiredItems = layout.method7009()
                  .stream()
                  .filter(entry -> entry.getSlot() >= 0 && entry.getSlot() < 36)
                  .sorted(Comparator.comparingInt(SortManager.Entry::getSlot))
                  .toList();
               this.activeLayoutName = layout.getName();
               this.sortTask = new SortCommand.SortTask(layout.getName(), availablePlayer, desiredItems);
               ChatLogger.method2019("Сортировка '" + layout.getName() + "' запущена");
            }
         }
      }
   }

   private void list() {
      var entry2s = this.getSortManager().method7002().stream().sorted(Comparator.comparing(SortManager.Entry2::getName, String.CASE_INSENSITIVE_ORDER)).toList();
      if (entry2s.isEmpty()) {
         ChatLogger.method2021("Сохраненных расположений нет");
      } else {
         ChatLogger.method2019("Сохраненные расположения (" + entry2s.size() + "):");
         entry2s.forEach(entry2 -> ChatLogger.method2019("- " + entry2.getName()));
      }
   }

   private void delete(String string) {
      SortManager sortManager = this.getSortManager();
      SortManager.Entry2 entry2 = sortManager.method7003(string);
      if (entry2 == null) {
         ChatLogger.method2021("Сохраненное расположение '" + string + "' не найдено");
      } else if (!sortManager.method7005(string)) {
         ChatLogger.method2021("Не удалось удалить расположение '" + string + "'");
      } else {
         if (this.sortTask != null && this.sortTask.name.equalsIgnoreCase(string)) {
            this.sortTask = null;
         }

         if (this.activeLayoutName != null && this.activeLayoutName.equalsIgnoreCase(string)) {
            this.activeLayoutName = null;
         }

         ChatLogger.method2019("Расположение '" + entry2.getName() + "' удалено");
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      SortCommand.SortTask sortTask = this.sortTask;
      if (sortTask != null) {
         LocalPlayer player = mc.player;
         MultiPlayerGameMode gameMode = mc.gameMode;
         if (player == null || player != sortTask.player || gameMode == null || player.containerMenu != player.inventoryMenu) {
            this.cancelSort("Сортировка остановлена: инвентарь игрока недоступен");
         } else if (!player.containerMenu.getCarried().isEmpty()) {
            this.cancelSort("Сортировка остановлена: на курсоре находится предмет");
         } else {
            int n = 0;
            Inventory inventory = player.getInventory();

            while (sortTask.itemIndex < sortTask.desiredItems.size()) {
               SortManager.Entry desiredItem = sortTask.desiredItems.get(sortTask.itemIndex);
               int slot = desiredItem.getSlot();
               if (this.matchesItem(inventory.getItem(slot), desiredItem)) {
                  sortTask.fixedSlots.add(slot);
                  sortTask.itemIndex++;
               } else {
                  int matchingSlot = this.findMatchingSlot(inventory, desiredItem, slot, sortTask.fixedSlots);
                  if (matchingSlot == -1) {
                     sortTask.missing++;
                     sortTask.itemIndex++;
                  } else {
                     int swapPacketCount = this.getSwapPacketCount(matchingSlot, slot);
                     if (n + swapPacketCount > 10) {
                        break;
                     }

                     int swapSlots = this.swapSlots(player, gameMode, matchingSlot, slot);
                     n += swapSlots;
                     sortTask.sentPackets += swapSlots;
                     sortTask.fixedSlots.add(slot);
                     sortTask.moved++;
                     sortTask.itemIndex++;
                  }
               }
            }

            if (sortTask.itemIndex >= sortTask.desiredItems.size()) {
               this.finishSort(sortTask);
            }
         }
      }
   }

   private boolean matchesItem(ItemStack stack, SortManager.Entry desiredItem) {
      if (stack.isEmpty()) {
         return false;
      } else if (!desiredItem.getText().equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
         return false;
      } else {
         return desiredItem.isFlag() != stack.isEnchanted() ? false : desiredItem.method7007().equals(this.getCustomDataKeys(stack));
      }
   }

   private Set<String> getCustomDataKeys(ItemStack stack) {
      CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
      return customData == null ? Set.of() : Set.copyOf(customData.copyTag().keySet());
   }

   private int findMatchingSlot(Inventory inventory, SortManager.Entry desiredItem, int slot, Set<Integer> fixedSlots) {
      int n = -1;
      int swapPacketCount = Integer.MAX_VALUE;

      for (int i = 0; i < 36; i++) {
         if (!fixedSlots.contains(i) && this.matchesItem(inventory.getItem(i), desiredItem)) {
            int swapPacketCount2 = this.getSwapPacketCount(i, slot);
            if (swapPacketCount2 < swapPacketCount) {
               n = i;
               swapPacketCount = swapPacketCount2;
            }
         }
      }

      return n;
   }

   private int swapSlots(LocalPlayer player, MultiPlayerGameMode gameMode, int matchingSlot, int slot) {
      int handlerSlot = this.toHandlerSlot(matchingSlot);
      int slotNum = this.toHandlerSlot(slot);
      int containerId = player.inventoryMenu.containerId;
      if (this.isHotbarSlot(slot)) {
         gameMode.handleContainerInput(containerId, handlerSlot, slot, ContainerInput.SWAP, player);
         return 1;
      } else if (this.isHotbarSlot(matchingSlot)) {
         gameMode.handleContainerInput(containerId, slotNum, matchingSlot, ContainerInput.SWAP, player);
         return 1;
      } else {
         int selectedSlot = player.getInventory().getSelectedSlot();
         gameMode.handleContainerInput(containerId, handlerSlot, selectedSlot, ContainerInput.SWAP, player);
         gameMode.handleContainerInput(containerId, slotNum, selectedSlot, ContainerInput.SWAP, player);
         gameMode.handleContainerInput(containerId, handlerSlot, selectedSlot, ContainerInput.SWAP, player);
         return 3;
      }
   }

   private int getSwapPacketCount(int matchingSlot, int slot) {
      return !this.isHotbarSlot(matchingSlot) && !this.isHotbarSlot(slot) ? 3 : 1;
   }

   private boolean isHotbarSlot(int slot) {
      return slot >= 0 && slot < 9;
   }

   private int toHandlerSlot(int matchingSlot) {
      return matchingSlot < 9 ? 36 + matchingSlot : matchingSlot;
   }

   private SortManager.Entry2 findLayout(String string) {
      SortManager sortManager = this.getSortManager();
      if (string != null) {
         return sortManager.method7003(string);
      } else {
         if (this.activeLayoutName != null) {
            SortManager.Entry2 entry2 = sortManager.method7003(this.activeLayoutName);
            if (entry2 != null) {
               return entry2;
            }
         }

         return sortManager.method7002().stream().max(Comparator.comparingLong(SortManager.Entry2::getLongValue)).orElse(null);
      }
   }

   private SortManager getSortManager() {
      return Zenith.getInstance().getSortManager();
   }

   private LocalPlayer getAvailablePlayer() {
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null || mc.gameMode == null) {
         ChatLogger.method2021("Сначала зайди в мир");
         return null;
      } else if (player.containerMenu != player.inventoryMenu) {
         ChatLogger.method2021("Закрой открытый контейнер перед сортировкой");
         return null;
      } else {
         return player;
      }
   }

   private boolean isInvalidName(String activeLayoutName) {
      return activeLayoutName == null || activeLayoutName.equals(".") || activeLayoutName.equals("..") || !activeLayoutName.matches("[\\p{L}\\p{N}._-]+");
   }

   private void finishSort(SortCommand.SortTask sortTask) {
      this.sortTask = null;
      if (sortTask.missing == 0) {
         ChatLogger.method2019("Расположение '" + sortTask.name + "' загружено, перемещено: " + sortTask.moved + ", пакетов: " + sortTask.sentPackets);
      } else {
         ChatLogger.method2021(
            "Расположение '"
               + sortTask.name
               + "' загружено частично: перемещено "
               + sortTask.moved
               + ", отсутствует предметов: "
               + sortTask.missing
               + ", пакетов: "
               + sortTask.sentPackets
         );
      }
   }

   private void cancelSort(String string) {
      this.sortTask = null;
      ChatLogger.method2021(string);
   }

   private static final class SortTask {
      private final String name;
      private final LocalPlayer player;
      private final List<SortManager.Entry> desiredItems;
      private final Set<Integer> fixedSlots = new HashSet<>();
      private int itemIndex;
      private int moved;
      private int missing;
      private int sentPackets;

      private SortTask(String name, LocalPlayer player, List<SortManager.Entry> desiredItems) {
         this.name = name;
         this.player = player;
         this.desiredItems = desiredItems;
      }
   }
}

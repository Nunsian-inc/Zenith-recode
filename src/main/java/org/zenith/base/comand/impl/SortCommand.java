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
   private final SuggestionProvider<SharedSuggestionProvider> layoutSuggestions = (var1, var2) -> SharedSuggestionProvider.suggest(
      this.getSortManager().method7002().stream().map(SortManager.Entry2::getName), var2
   );

   public SortCommand() {
      super("sort");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2019("Использование: .sort <save/load/list/delete>");
         return 1;
      });
      var1.then(literal("save").then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.save(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
      var1.then(((LiteralArgumentBuilder)literal("load").executes(var1x -> {
         this.load(null);
         return 1;
      })).then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.load(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
      var1.then(literal("list").executes(var1x -> {
         this.list();
         return 1;
      }));
      var1.then(literal("delete").then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.delete(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
   }

   private void save(String var1) {
      if (this.isInvalidName(var1)) {
         ChatLogger.method2021("Имя может содержать только буквы, цифры, '.', '_' и '-'");
      } else {
         LocalPlayer var2 = this.getAvailablePlayer();
         if (var2 != null) {
            ArrayList var3 = new ArrayList();
            Inventory var4 = var2.getInventory();

            for (int var5 = 0; var5 < 36; var5++) {
               ItemStack var6 = var4.getItem(var5);
               if (!var6.isEmpty()) {
                  var3.add(new SortManager.Entry(var5, BuiltInRegistries.ITEM.getKey(var6.getItem()).toString(), var6.isEnchanted(), this.getCustomDataKeys(var6)));
               }
            }

            if (this.getSortManager().method7004(var1, var3)) {
               this.activeLayoutName = var1;
               ChatLogger.method2019("Расположение инвентаря '" + var1 + "' сохранено");
            } else {
               ChatLogger.method2021("Не удалось сохранить расположение '" + var1 + "'");
            }
         }
      }
   }

   private void load(String var1) {
      LocalPlayer var2 = this.getAvailablePlayer();
      if (var2 != null) {
         if (!var2.containerMenu.getCarried().isEmpty()) {
            ChatLogger.method2021("Освободи курсор перед сортировкой инвентаря");
         } else {
            SortManager.Entry2 var3 = this.findLayout(var1);
            if (var3 == null) {
               String var5 = var1 == null ? "" : " '" + var1 + "'";
               ChatLogger.method2021("Сохраненное расположение" + var5 + " не найдено");
            } else {
               var var4 = var3.method7009()
                  .stream()
                  .filter(var0 -> var0.getSlot() >= 0 && var0.getSlot() < 36)
                  .sorted(Comparator.comparingInt(SortManager.Entry::getSlot))
                  .toList();
               this.activeLayoutName = var3.getName();
               this.sortTask = new SortCommand.SortTask(var3.getName(), var2, var4);
               ChatLogger.method2019("Сортировка '" + var3.getName() + "' запущена");
            }
         }
      }
   }

   private void list() {
      var var1 = this.getSortManager().method7002().stream().sorted(Comparator.comparing(SortManager.Entry2::getName, String.CASE_INSENSITIVE_ORDER)).toList();
      if (var1.isEmpty()) {
         ChatLogger.method2021("Сохраненных расположений нет");
      } else {
         ChatLogger.method2019("Сохраненные расположения (" + var1.size() + "):");
         var1.forEach(var0 -> ChatLogger.method2019("- " + var0.getName()));
      }
   }

   private void delete(String var1) {
      SortManager var2 = this.getSortManager();
      SortManager.Entry2 var3 = var2.method7003(var1);
      if (var3 == null) {
         ChatLogger.method2021("Сохраненное расположение '" + var1 + "' не найдено");
      } else if (!var2.method7005(var1)) {
         ChatLogger.method2021("Не удалось удалить расположение '" + var1 + "'");
      } else {
         if (this.sortTask != null && this.sortTask.name.equalsIgnoreCase(var1)) {
            this.sortTask = null;
         }

         if (this.activeLayoutName != null && this.activeLayoutName.equalsIgnoreCase(var1)) {
            this.activeLayoutName = null;
         }

         ChatLogger.method2019("Расположение '" + var3.getName() + "' удалено");
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      SortCommand.SortTask var2 = this.sortTask;
      if (var2 != null) {
         LocalPlayer var3 = mc.player;
         MultiPlayerGameMode var4 = mc.gameMode;
         if (var3 == null || var3 != var2.player || var4 == null || var3.containerMenu != var3.inventoryMenu) {
            this.cancelSort("Сортировка остановлена: инвентарь игрока недоступен");
         } else if (!var3.containerMenu.getCarried().isEmpty()) {
            this.cancelSort("Сортировка остановлена: на курсоре находится предмет");
         } else {
            int var5 = 0;
            Inventory var6 = var3.getInventory();

            while (var2.itemIndex < var2.desiredItems.size()) {
               SortManager.Entry var7 = var2.desiredItems.get(var2.itemIndex);
               int var8 = var7.getSlot();
               if (this.matchesItem(var6.getItem(var8), var7)) {
                  var2.fixedSlots.add(var8);
                  var2.itemIndex++;
               } else {
                  int var9 = this.findMatchingSlot(var6, var7, var8, var2.fixedSlots);
                  if (var9 == -1) {
                     var2.missing++;
                     var2.itemIndex++;
                  } else {
                     int var10 = this.getSwapPacketCount(var9, var8);
                     if (var5 + var10 > 10) {
                        break;
                     }

                     int var11 = this.swapSlots(var3, var4, var9, var8);
                     var5 += var11;
                     var2.sentPackets += var11;
                     var2.fixedSlots.add(var8);
                     var2.moved++;
                     var2.itemIndex++;
                  }
               }
            }

            if (var2.itemIndex >= var2.desiredItems.size()) {
               this.finishSort(var2);
            }
         }
      }
   }

   private boolean matchesItem(ItemStack var1, SortManager.Entry var2) {
      if (var1.isEmpty()) {
         return false;
      } else if (!var2.getText().equals(BuiltInRegistries.ITEM.getKey(var1.getItem()).toString())) {
         return false;
      } else {
         return var2.isFlag() != var1.isEnchanted() ? false : var2.method7007().equals(this.getCustomDataKeys(var1));
      }
   }

   private Set<String> getCustomDataKeys(ItemStack var1) {
      CustomData var2 = var1.get(DataComponents.CUSTOM_DATA);
      return var2 == null ? Set.of() : Set.copyOf(var2.copyTag().keySet());
   }

   private int findMatchingSlot(Inventory var1, SortManager.Entry var2, int var3, Set<Integer> var4) {
      int var5 = -1;
      int var6 = Integer.MAX_VALUE;

      for (int var7 = 0; var7 < 36; var7++) {
         if (!var4.contains(var7) && this.matchesItem(var1.getItem(var7), var2)) {
            int var8 = this.getSwapPacketCount(var7, var3);
            if (var8 < var6) {
               var5 = var7;
               var6 = var8;
            }
         }
      }

      return var5;
   }

   private int swapSlots(LocalPlayer var1, MultiPlayerGameMode var2, int var3, int var4) {
      int var5 = this.toHandlerSlot(var3);
      int var6 = this.toHandlerSlot(var4);
      int var7 = var1.inventoryMenu.containerId;
      if (this.isHotbarSlot(var4)) {
         var2.handleContainerInput(var7, var5, var4, ContainerInput.SWAP, var1);
         return 1;
      } else if (this.isHotbarSlot(var3)) {
         var2.handleContainerInput(var7, var6, var3, ContainerInput.SWAP, var1);
         return 1;
      } else {
         int var8 = var1.getInventory().getSelectedSlot();
         var2.handleContainerInput(var7, var5, var8, ContainerInput.SWAP, var1);
         var2.handleContainerInput(var7, var6, var8, ContainerInput.SWAP, var1);
         var2.handleContainerInput(var7, var5, var8, ContainerInput.SWAP, var1);
         return 3;
      }
   }

   private int getSwapPacketCount(int var1, int var2) {
      return !this.isHotbarSlot(var1) && !this.isHotbarSlot(var2) ? 3 : 1;
   }

   private boolean isHotbarSlot(int var1) {
      return var1 >= 0 && var1 < 9;
   }

   private int toHandlerSlot(int var1) {
      return var1 < 9 ? 36 + var1 : var1;
   }

   private SortManager.Entry2 findLayout(String var1) {
      SortManager var2 = this.getSortManager();
      if (var1 != null) {
         return var2.method7003(var1);
      } else {
         if (this.activeLayoutName != null) {
            SortManager.Entry2 var3 = var2.method7003(this.activeLayoutName);
            if (var3 != null) {
               return var3;
            }
         }

         return var2.method7002().stream().max(Comparator.comparingLong(SortManager.Entry2::getLongValue)).orElse(null);
      }
   }

   private SortManager getSortManager() {
      return Zenith.getInstance().getSortManager();
   }

   private LocalPlayer getAvailablePlayer() {
      LocalPlayer var1 = mc.player;
      if (var1 == null || mc.level == null || mc.gameMode == null) {
         ChatLogger.method2021("Сначала зайди в мир");
         return null;
      } else if (var1.containerMenu != var1.inventoryMenu) {
         ChatLogger.method2021("Закрой открытый контейнер перед сортировкой");
         return null;
      } else {
         return var1;
      }
   }

   private boolean isInvalidName(String var1) {
      return var1 == null || var1.equals(".") || var1.equals("..") || !var1.matches("[\\p{L}\\p{N}._-]+");
   }

   private void finishSort(SortCommand.SortTask var1) {
      this.sortTask = null;
      if (var1.missing == 0) {
         ChatLogger.method2019("Расположение '" + var1.name + "' загружено, перемещено: " + var1.moved + ", пакетов: " + var1.sentPackets);
      } else {
         ChatLogger.method2021(
            "Расположение '"
               + var1.name
               + "' загружено частично: перемещено "
               + var1.moved
               + ", отсутствует предметов: "
               + var1.missing
               + ", пакетов: "
               + var1.sentPackets
         );
      }
   }

   private void cancelSort(String var1) {
      this.sortTask = null;
      ChatLogger.method2021(var1);
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

      private SortTask(String var1, LocalPlayer var2, List<SortManager.Entry> var3) {
         this.name = var1;
         this.player = var2;
         this.desiredItems = var3;
      }
   }
}

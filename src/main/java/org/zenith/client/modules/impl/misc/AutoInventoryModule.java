package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.api.InventoryPreset;
import org.zenith.utility.api.ServerNameNormalizer;
import org.zenith.utility.game.InventorySnapshot;
import org.zenith.utility.game.ItemMatcher;
import org.zenith.utility.game.ItemNbtUtils;

@ModuleInfo(
   name = "AutoInventory",
   category = ModuleCategory.MISC,
   description = "module.autoinventory.description"
)
public class AutoInventoryModule extends Module {
   public static final AutoInventoryModule INSTANCE = new AutoInventoryModule();
   private static final int CONST_intValue = 36;
   private static final long CONST_longValue = 1000L;
   private static final long CONST_longValue2 = 300L;
   private static final long CONST_longValue3 = 100L;
   private static final long CONST_longValue4 = 700L;
   private static final int CONST_intValue2 = 45;
   private static final int CONST_intValue3 = 50;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 3;
   private static final int CONST_intValue6 = 4;
   private static final int CONST_intValue7 = 27;
   private static final int CONST_intValue8 = 0;
   private static final String text = "[Кyпить]";
   private static final long CONST_longValue5 = 6000L;
   private static final long CONST_longValue6 = 6000L;
   private static final int CONST_intValue9 = 3;
   private static final Pattern pattern = Pattern.compile("Цена за 1 ед\\.?:\\s*([\\d ]+)\\s*¤");
   private static final Pattern pattern2 = Pattern.compile("Цена:\\s*([\\d ]+)\\s*¤");
   private static final Pattern pattern3 = Pattern.compile("\\$\\s*(?:(?:Цена|Ценa):\\s*)?([0-9][\\d, ]*)");
   private static final Pattern pattern4 = Pattern.compile("(\\d+)/(\\d+)");
   private static final String text2 = "следующая страница";
   private static final String text3 = "предыдущая страница";
   private static final String text4 = "нажмите, чтобы забрать";
   private static final int CONST_intValue10 = 4;
   private static final int CONST_intValue11 = 10;
   private static final int CONST_intValue12 = 8;
   private static final int CONST_intValue13 = 200;
   private static final String text5 = "Funtime 1.21";
   private static final String text6 = "HolyWorld";
   private static final String text7 = "SpookyTime";
   private final InventoryPreset inventoryPreset = new InventoryPreset("Funtime 1.21");
   private final InventoryPreset inventoryPreset2 = new InventoryPreset("HolyWorld");
   private final InventoryPreset inventoryPreset3 = new InventoryPreset("SpookyTime");
   private final ModeSetting server = new ModeSetting(
      "module.autoInventory.server", "module.autoInventory.server.desc", "Funtime 1.21", "HolyWorld", "SpookyTime"
   );
   private final ButtonSetting openMenu = new ButtonSetting("module.autoInventory.openMenu", () -> Zenith.getInstance().getNLMenuScreen().openTool(1));
   private final BooleanSetting anarchyHop = new BooleanSetting(
      "module.autoInventory.anarchyHop", "module.autoInventory.anarchyHop.desc", false, () -> this.server.method2101("Funtime 1.21")
   );
   private final InventorySnapshot inventorySnapshot = new InventorySnapshot();
   private final ServerNameNormalizer serverNameNormalizer = new ServerNameNormalizer();
   private final List<AutoInventoryModule.Entry2> items = new ArrayList<>();
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private AutoInventoryModule.Type type = AutoInventoryModule.Type.IDLE;
   private AutoInventoryModule.Entry2 entry2;
   private int intValue = -1;
   private int intValue2;
   private int intValue3;
   private AutoInventoryModule.Entry3 entry3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private long longValue;
   private final AutoInventoryModule.Entry entry = new AutoInventoryModule.Entry();
   private final AutoInventoryModule.Entry entry4 = new AutoInventoryModule.Entry();
   private final List<AutoInventoryModule.Data> items2 = new ArrayList<>();
   private AutoInventoryModule.Data data;
   private volatile int intValue7;
   private int intValue8;
   private int intValue9;
   private volatile boolean flag;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private volatile boolean flag4;
   private boolean flag5;

   private AutoInventoryModule() {
   }

   @Override
   public void onEnable() {
      this.method160();
      this.method163();
      this.intValue4 = this.items.size();
      if (this.items.isEmpty()) {
         if (this.method207().isEmpty()) {
            ChatLogger.method2021("АвтоСбор: набор пуст, добавьте предметы через меню");
         } else {
            ChatLogger.method2019("АвтоСбор: все предметы из набора уже есть в инвентаре");
         }

         this.type = AutoInventoryModule.Type.FINISHED;
         super.onEnable();
      } else {
         super.onEnable();
         if (this.method210()) {
            ChatLogger.method2021("АвтоСбор: берутся самые дешёвые лоты из тех, что есть, — будьте внимательны");
            this.entry.method213();
            this.method184();
         } else {
            this.method164();
         }
      }
   }

   @Override
   public void onDisable() {
      this.method160();
      super.onDisable();
   }

   private void method160() {
      this.items.clear();
      this.stopWatch.reset();
      this.stopWatch2.reset();
      this.type = AutoInventoryModule.Type.IDLE;
      this.entry2 = null;
      this.intValue = -1;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.entry3 = null;
      this.intValue4 = 0;
      this.intValue5 = 0;
      this.intValue6 = 0;
      this.longValue = 0L;
      this.items2.clear();
      this.data = null;
      this.intValue7 = 0;
      this.intValue8 = 0;
      this.intValue9 = 0;
      this.flag = false;
      this.flag2 = false;
      this.flag3 = false;
      this.flag4 = false;
      this.flag5 = false;
   }

   @EventTarget
   public void method161(TickEvent event) {
      if (this.type == AutoInventoryModule.Type.FINISHED) {
         this.method162();
         this.setToggled(false);
      } else if (mc.player == null || mc.level == null || mc.player.connection == null) {
         this.setToggled(false);
      } else if (this.method210()) {
         this.method177();
      } else {
         switch (this.type) {
            case IDLE:
               if (this.stopWatch.hasElapsed(1000L)) {
                  this.method164();
               }
               break;
            case AUCTION:
               if (this.stopWatch.hasElapsed(6000L)) {
                  this.method166();
                  return;
               }

               this.method167();
               break;
            case PURCHASE:
               if (this.stopWatch.hasElapsed(6000L)) {
                  this.method166();
                  return;
               }

               this.method170();
               break;
            case INVENTORY:
               this.method176();
         }
      }
   }

   private void method162() {
      if (this.intValue4 > 0) {
         String status = "АвтоСбор: закуплено " + this.intValue5 + " из " + this.intValue4;
         if (this.intValue6 > 0) {
            status = status + ", пропущено " + this.intValue6;
         }

         if (this.longValue > 0L) {
            status = status + ", потрачено " + this.longValue;
         }

         ChatLogger.method2019(status);
         this.intValue4 = 0;
      }
   }

   private void method163() {
      this.items.clear();
      HashMap map = new HashMap();
      HashMap map2 = new HashMap();
      HashMap map3 = new HashMap();
      InventoryPreset inventoryPreset = this.method207();

      for (int i = 0; i < 36; i++) {
         ItemMatcher itemMatcher = inventoryPreset.method870(i);
         if (itemMatcher != null && !itemMatcher.getItemStack().isEmpty()) {
            String text = itemMatcher.getText22();
            if (text != null && !text.isBlank()) {
               int n = inventoryPreset.method877(i);
               float floatValue = inventoryPreset.method881(i);
               long longValue = inventoryPreset.method879(i);
               String key = this.method203(itemMatcher, floatValue);
               int defaultValue = ((Integer)map.computeIfAbsent(key, var3x -> this.method202(itemMatcher, floatValue)));
               int n2 = ((Integer)map2.getOrDefault(key, 0)) + n;
               int n3 = ((Integer)map3.getOrDefault(key, defaultValue));
               map2.put(key, n2);
               map3.put(key, Math.max(n3, n2));
               if (n3 < n2) {
                  this.items.add(new AutoInventoryModule.Entry2(itemMatcher, text, n2 - n3, n2, floatValue, longValue));
               }
            }
         }
      }
   }

   private void method164() {
      for (this.entry2 = null; !this.items.isEmpty(); this.intValue5++) {
         AutoInventoryModule.Entry2 entry2 = this.items.removeFirst();
         if (this.method202(entry2.itemMatcher, entry2.floatValue) < entry2.intValue2) {
            this.intValue3 = 0;
            this.method165(entry2);
            return;
         }
      }

      this.type = AutoInventoryModule.Type.FINISHED;
   }

   private void method165(AutoInventoryModule.Entry2 entry2) {
      if (mc.player != null && mc.level != null && mc.player.connection != null) {
         this.method205();
         this.entry3 = null;
         this.entry2 = entry2;
         entry2.intValue3 = this.method202(entry2.itemMatcher, entry2.floatValue);
         mc.player.connection.sendCommand("ah search " + entry2.text);
         this.stopWatch.reset();
         this.stopWatch2.reset();
         this.type = AutoInventoryModule.Type.AUCTION;
         this.intValue = -1;
      } else {
         this.items.addFirst(entry2);
         this.entry2 = null;
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      }
   }

   private void method166() {
      if (this.entry2 == null) {
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
         this.method164();
      } else {
         int n;
         if ((n = this.method202(this.entry2.itemMatcher, this.entry2.floatValue)) > this.entry2.intValue3) {
            this.method175();
            this.intValue3 = 0;
         } else {
            this.intValue3++;
         }

         this.entry3 = null;
         if (n >= this.entry2.intValue2) {
            this.intValue5++;
            this.entry2 = null;
            this.type = AutoInventoryModule.Type.IDLE;
            this.stopWatch.reset();
         } else if (this.intValue3 >= 3) {
            ChatLogger.method2021("АвтоСбор: не удалось закупить «" + this.entry2.text + "», пропускаю");
            this.intValue6++;
            this.entry2 = null;
            this.type = AutoInventoryModule.Type.IDLE;
            this.stopWatch.reset();
            this.method164();
         } else {
            this.method165(
               new AutoInventoryModule.Entry2(
                  this.entry2.itemMatcher, this.entry2.text, this.entry2.intValue2 - n, this.entry2.intValue2, this.entry2.floatValue, this.entry2.longValue
               )
            );
         }
      }
   }

   private void method167() {
      if (mc.gameMode != null) {
         if (mc.gui.screen() != null && mc.player.containerMenu != mc.player.inventoryMenu) {
            AbstractContainerMenu containerMenu = mc.player.containerMenu;
            if (ItemNbtUtils.method81(containerMenu)) {
               if (this.entry2 == null) {
                  this.type = AutoInventoryModule.Type.IDLE;
                  this.stopWatch.reset();
               } else if (this.intValue != containerMenu.containerId) {
                  this.intValue = containerMenu.containerId;
                  this.stopWatch2.reset();
               } else if (this.stopWatch2.hasElapsed(300L)) {
                  int n = this.method168(containerMenu, this.entry2);
                  this.intValue = -1;
                  if (n >= 0) {
                     this.type = AutoInventoryModule.Type.PURCHASE;
                     this.intValue2 = this.method210() ? 0 : n;
                     this.intValue = -1;
                     this.stopWatch.reset();
                     this.stopWatch2.reset();
                  } else if (!this.method169(containerMenu)) {
                     ChatLogger.method2021("АвтоСбор: нет подходящих лотов «" + this.entry2.text + "»");
                     this.intValue6++;
                     this.entry2 = null;
                     this.type = AutoInventoryModule.Type.IDLE;
                     this.stopWatch.reset();
                     this.method164();
                  }
               }
            }
         }
      }
   }

   private int method168(AbstractContainerMenu containerMenu, AutoInventoryModule.Entry2 entry2) {
      this.entry3 = null;
      if (entry2 != null && mc.gameMode != null && mc.player != null) {
         Slot slot = null;
         long l = Long.MAX_VALUE;
         int n = Math.min(45, containerMenu.slots.size());

         for (int i = 0; i < n; i++) {
            Slot slot2 = containerMenu.getSlot(i);
            if (slot2 != null && slot2.hasItem()) {
               ItemStack stack = slot2.getItem();
               long l2;
               if (entry2.itemMatcher.method2672(stack)
                  && this.method204(stack, entry2.floatValue)
                  && !ItemNbtUtils.method87(stack)
                  && (l2 = this.method198(stack)) != Long.MAX_VALUE
                  && !this.method197(entry2, stack, l2)
                  && l2 < l) {
                  slot = slot2;
                  l = l2;
               }
            }
         }

         if (slot == null) {
            return -1;
         } else {
            int n2 = ItemNbtUtils.method90(slot.getItem());
            boolean flag = !this.method210() && n2 > entry2.intValue;
            int intValue = Math.max(1, flag ? entry2.intValue : n2);
            String string = slot.getItem().getHoverName().getString();
            this.entry3 = new AutoInventoryModule.Entry3(
               slot.getItem(), string != null && !string.isBlank() ? string : entry2.text, intValue, this.method200(l, intValue)
            );
            int buttonNum = flag ? 1 : 0;
            mc.gameMode.handleContainerInput(containerMenu.containerId, slot.index, buttonNum, ContainerInput.PICKUP, mc.player);
            return flag ? Math.max(0, entry2.intValue - 1) : 0;
         }
      } else {
         return -1;
      }
   }

   private boolean method169(AbstractContainerMenu containerMenu) {
      if (containerMenu.slots.size() <= 50) {
         return false;
      } else if (mc.gameMode != null && mc.player != null) {
         Slot slot = containerMenu.getSlot(50);
         if (slot != null && slot.hasItem()) {
            if (slot.getItem().getItem() != Items.DYE.pick(net.minecraft.world.item.DyeColor.LIME)) {
               return false;
            } else {
               this.entry3 = null;
               mc.gameMode.handleContainerInput(containerMenu.containerId, slot.index, 0, ContainerInput.PICKUP, mc.player);
               this.intValue = -1;
               this.stopWatch.reset();
               this.stopWatch2.reset();
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void method170() {
      if (mc.gameMode != null) {
         if (mc.gui.screen() != null && mc.player.containerMenu != mc.player.inventoryMenu) {
            AbstractContainerMenu containerMenu = mc.player.containerMenu;
            if (ItemNbtUtils.method81(containerMenu)) {
               this.method171(containerMenu);
            } else if (this.method210()) {
               this.method172(containerMenu);
            } else if (mc.gui.screen().getTitle() != null && mc.gui.screen().getTitle().getString().contains("Покупка предмета")) {
               if (containerMenu.slots.size() > 4) {
                  if (this.intValue != containerMenu.containerId) {
                     this.intValue = containerMenu.containerId;
                     this.stopWatch2.reset();
                  } else if (this.stopWatch2.hasElapsed(100L)) {
                     if (this.intValue2 >= 10) {
                        this.method174(containerMenu, 4);
                        this.intValue2 -= 10;
                        this.stopWatch2.reset();
                     } else if (this.intValue2 > 0) {
                        this.method174(containerMenu, 3);
                        this.intValue2--;
                        this.stopWatch2.reset();
                     } else if (containerMenu.slots.size() > 2) {
                        this.method174(containerMenu, 2);
                        this.method175();
                        this.type = AutoInventoryModule.Type.INVENTORY;
                        this.intValue = -1;
                        this.stopWatch.reset();
                        this.stopWatch2.reset();
                     }
                  }
               }
            }
         }
      }
   }

   private void method171(AbstractContainerMenu containerMenu) {
      if (this.entry2 == null) {
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      } else if (this.stopWatch2.hasElapsed(300L)) {
         int n = this.method168(containerMenu, this.entry2);
         this.intValue = -1;
         if (n < 0) {
            if (!this.method169(containerMenu)) {
               this.type = AutoInventoryModule.Type.IDLE;
               this.stopWatch.reset();
               this.method164();
            }
         } else {
            this.intValue2 = this.method210() ? 0 : n;
            this.stopWatch.reset();
            this.stopWatch2.reset();
         }
      }
   }

   private void method172(AbstractContainerMenu containerMenu) {
      if (mc.gui.screen().getTitle() != null) {
         if (mc.gui.screen().getTitle().getString().contains("Подтверждение покупки")) {
            int index;
            if ((index = this.method173(containerMenu)) >= 0) {
               if (this.intValue != containerMenu.containerId) {
                  this.intValue = containerMenu.containerId;
                  this.stopWatch2.reset();
               } else if (this.stopWatch2.hasElapsed(100L)) {
                  this.method174(containerMenu, index);
                  this.type = AutoInventoryModule.Type.INVENTORY;
                  this.intValue = -1;
                  this.stopWatch.reset();
                  this.stopWatch2.reset();
               }
            }
         }
      }
   }

   private int method173(AbstractContainerMenu containerMenu) {
      int n;
      if ((n = Math.max(0, containerMenu.slots.size() - 36)) == 27) {
         return 0;
      } else if (n <= 27) {
         return -1;
      } else {
         int n2 = Math.min(n, containerMenu.slots.size());

         for (int i = 0; i < n2; i++) {
            Slot slot = containerMenu.getSlot(i);
            if (slot != null && slot.hasItem()) {
               ItemStack stack = slot.getItem();
               if (stack.getItem() == Items.PAPER && "[Кyпить]".equals(stack.getHoverName().getString())) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   private void method174(AbstractContainerMenu containerMenu, int index) {
      if (index >= 0 && index < containerMenu.slots.size()) {
         if (mc.gameMode != null && mc.player != null) {
            mc.gameMode.handleContainerInput(containerMenu.containerId, containerMenu.getSlot(index).index, 0, ContainerInput.PICKUP, mc.player);
         }
      }
   }

   private void method175() {
      if (this.entry3 != null) {
         this.inventorySnapshot.method1862(this.entry3.itemStack, this.entry3.text, this.entry3.intValue, this.entry3.longValue);
         this.longValue = this.longValue + Math.max(0L, this.entry3.longValue);
         this.entry3 = null;
      }
   }

   private void method176() {
      if (this.entry2 == null) {
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      } else if (this.method202(this.entry2.itemMatcher, this.entry2.floatValue) < this.entry2.intValue2) {
         if (this.stopWatch2.hasElapsed(700L)) {
            this.method166();
         }
      } else {
         this.method175();
         this.intValue5++;
         this.entry2 = null;
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      }
   }

   private void method177() {
      if (this.flag5) {
         ChatLogger.method2019("АвтоСбор: работа принудительно завершена");
         this.type = AutoInventoryModule.Type.FINISHED;
      } else if (this.flag2) {
         ChatLogger.method2021("АвтоСбор: не хватает монет, выключаюсь");
         this.method192();
         this.type = AutoInventoryModule.Type.FINISHED;
      } else {
         if (this.flag) {
            this.flag = false;
            this.data = null;
            this.items2.clear();
            this.entry3 = null;
         }

         if (this.flag3) {
            this.flag3 = false;
            this.intValue8 = 10;
         }

         if (this.intValue8 > 0) {
            this.intValue8--;
            ThreadLocalRandom current = ThreadLocalRandom.current();
            mc.player.setYRot(mc.player.getYRot() + (float)current.nextDouble(-2.0, 2.0));
            mc.player.setXRot(Math.max(-90.0F, Math.min(90.0F, mc.player.getXRot() + (float)current.nextDouble(-1.0, 1.0))));
         }

         if (this.flag4) {
            this.flag4 = false;
            if (this.anarchyHop.isEnabled()) {
               ThreadLocalRandom current = ThreadLocalRandom.current();
               int nextInt = current.nextBoolean() ? current.nextInt(205, 231) : current.nextInt(305, 325);
               mc.player.connection.sendCommand("an" + nextInt);
               ChatLogger.method2019("АвтоСбор: аукцион тормозит, перехожу на " + nextInt + " анархию");
            }
         }

         this.method182();
         if (this.entry2 == null) {
            this.type = AutoInventoryModule.Type.FINISHED;
         } else if (mc.player.getInventory().getFreeSlot() == -1) {
            ChatLogger.method2021("АвтоСбор: нет свободных слотов в инвентаре, освободите место");
            this.method192();
            this.type = AutoInventoryModule.Type.FINISHED;
         } else {
            this.intValue7++;
            if (mc.gui.screen() instanceof ContainerScreen containerScreen) {
               AbstractContainerMenu menu = containerScreen.getMenu();
               String input = containerScreen.getTitle().getString().replaceAll("§.", "").toLowerCase(Locale.ROOT).trim();
               if (input.contains(this.entry2.text.toLowerCase(Locale.ROOT)) || ItemNbtUtils.method83(menu)) {
                  this.method178(menu, input, this.entry2);
               } else if (!input.contains("подтверждение покупки") && !input.contains("подозрительная цена")) {
                  this.method192();
               } else {
                  this.type = AutoInventoryModule.Type.PURCHASE;
                  if (this.entry.method214(200L, 90L)) {
                     this.method191(menu, "[Кyпить]");
                     this.entry.reset();
                  }
               }
            } else {
               this.type = AutoInventoryModule.Type.AUCTION;
               if (mc.player.tickCount >= 200 && this.entry.method214(1000L, 300L)) {
                  mc.player.connection.sendCommand("ah search " + this.entry2.text);
                  this.entry.reset();
                  this.intValue7 = 0;
               }
            }
         }
      }
   }

   private void method178(AbstractContainerMenu menu, String input, AutoInventoryModule.Entry2 entry2) {
      this.type = AutoInventoryModule.Type.AUCTION;
      int n = this.method202(entry2.itemMatcher, entry2.floatValue);
      if (this.entry.method214(300L, 80L) && n >= entry2.intValue2) {
         ChatLogger.method2019("АвтоСбор: «" + entry2.text + "» куплен, перехожу к следующему");
         this.intValue5++;
         this.method184();
         this.method192();
      } else if (this.entry4.method214(1000L, 150L)) {
         boolean flag = InventoryPreset.method886(entry2.itemMatcher);
         if (this.entry.method214(flag ? 400L : 550L, 120L)) {
            Matcher matcher = pattern4.matcher(input);
            int intValue = 1;
            int n2 = 1;
            if (matcher.find()) {
               intValue = Integer.parseInt(matcher.group(1));
               n2 = Integer.parseInt(matcher.group(2));
            }

            if (flag) {
               this.method179(menu, entry2, n, intValue, Math.min(4, n2));
            } else {
               this.method180(menu, entry2, n, intValue, Math.min(4, n2));
            }

            this.entry.reset();
         }
      }
   }

   private void method179(AbstractContainerMenu menu, AutoInventoryModule.Entry2 entry2, int n, int intValue, int n2) {
      List<Slot> slots = this.method185(menu, entry2);
      if (this.data != null) {
         if (intValue == this.data.intValue) {
            Slot slot = slots.stream()
               .filter(var1x -> this.method190(var1x.getItem()) == this.data.longValue)
               .findFirst()
               .orElseGet(() -> slots.stream().min(Comparator.comparingLong(var1xx -> this.method190(var1xx.getItem()))).orElse(null));
            if (slot != null) {
               this.method181(menu, slot, entry2, n);
            } else {
               ChatLogger.method2019("АвтоСбор: лот пропал, пересканирую");
               this.data = null;
               this.items2.clear();
               this.method192();
            }
         } else {
            this.method191(menu, intValue < this.data.intValue ? "следующая страница" : "предыдущая страница");
         }
      } else {
         if (this.items2.stream().noneMatch(var1x -> var1x.intValue == intValue)) {
            for (Slot slot : slots) {
               this.items2.add(new AutoInventoryModule.Data(intValue, this.method190(slot.getItem())));
            }
         }

         if (intValue >= n2 || !this.method191(menu, "следующая страница")) {
            this.data = this.items2.stream().min(Comparator.comparingLong(AutoInventoryModule.Data::getLongValue)).orElse(null);
            if (this.data == null) {
               this.method183(entry2);
            }
         }
      }
   }

   private void method180(AbstractContainerMenu menu, AutoInventoryModule.Entry2 entry2, int n, int intValue, int n2) {
      var slots = this.method185(menu, entry2);
      long l = slots.stream().mapToLong(var1x -> this.method190(var1x.getItem())).min().orElse(0L);
      int n3;
      int n4 = (n3 = Math.max(1, entry2.intValue2)) + Math.round(n3 * 0.2F) - n;
      int n5 = n3 - n;
      var slots2 = slots.stream()
         .filter(var3x -> this.method190(var3x.getItem()) <= Math.round((float)l * 2.0F))
         .filter(var1x -> ItemNbtUtils.method90(var1x.getItem()) <= n4)
         .toList();
      var comparingLong = Comparator.comparingLong((net.minecraft.world.inventory.Slot var1x) -> this.method190(var1x.getItem()));
      Slot slot = slots2.stream()
         .filter(var1x -> ItemNbtUtils.method90(var1x.getItem()) >= n5)
         .min(comparingLong)
         .orElseGet(() -> (Slot)slots2.stream().min(comparingLong).orElse(null));
      if (slot != null) {
         this.method181(menu, slot, entry2, n);
      } else if (intValue >= n2 || !this.method191(menu, "следующая страница")) {
         this.method183(entry2);
      }
   }

   private void method181(AbstractContainerMenu menu, Slot slot, AutoInventoryModule.Entry2 entry2, int n) {
      ItemStack stack = slot.getItem();
      String string = stack.getHoverName().getString();
      long l = this.method199(stack);
      this.entry3 = new AutoInventoryModule.Entry3(
         stack, string != null && !string.isBlank() ? string : entry2.text, Math.max(1, ItemNbtUtils.method90(stack)), l == Long.MAX_VALUE ? 0L : l
      );
      this.intValue9 = n;
      mc.gameMode.handleContainerInput(menu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, mc.player);
   }

   private void method182() {
      if (this.entry3 != null && this.entry2 != null) {
         if (this.method202(this.entry2.itemMatcher, this.entry2.floatValue) > this.intValue9) {
            this.method175();
         }
      }
   }

   private void method183(AutoInventoryModule.Entry2 entry2) {
      ChatLogger.method2021("АвтоСбор: нет подходящих лотов «" + entry2.text + "», пропускаю");
      this.intValue6++;
      this.method184();
      this.method192();
   }

   private void method184() {
      this.entry2 = null;
      this.entry3 = null;
      this.data = null;
      this.items2.clear();

      while (!this.items.isEmpty()) {
         AutoInventoryModule.Entry2 entry2 = this.items.removeFirst();
         if (this.method202(entry2.itemMatcher, entry2.floatValue) < entry2.intValue2) {
            this.entry2 = entry2;
            this.type = AutoInventoryModule.Type.AUCTION;
            ChatLogger.method2019("АвтоСбор: собираю «" + entry2.text + "»");
            return;
         }

         this.intValue5++;
      }

      this.type = AutoInventoryModule.Type.FINISHED;
   }

   private List<Slot> method185(AbstractContainerMenu menu, AutoInventoryModule.Entry2 entry2) {
      ArrayList<Slot> slots = new ArrayList<>();
      ItemStack stack = this.method187(entry2.itemMatcher);
      int n = Math.max(0, menu.slots.size() - 36);

      for (int i = 0; i < n; i++) {
         Slot slot = menu.getSlot(i);
         if (slot != null && slot.hasItem() && this.method186(slot.getItem(), entry2, stack)) {
            slots.add(slot);
         }
      }

      return slots;
   }

   private boolean method186(ItemStack stack, AutoInventoryModule.Entry2 entry2, ItemStack stack2) {
      if (!entry2.itemMatcher.method2672(stack)) {
         return false;
      } else if (!this.method204(stack, entry2.floatValue)) {
         return false;
      } else if (ItemNbtUtils.method87(stack)) {
         return false;
      } else if (this.method189(stack)) {
         return false;
      } else {
         long l;
         if ((l = this.method190(stack)) <= 0L || l == Long.MAX_VALUE) {
            return false;
         } else if (entry2.longValue > 0L && l > entry2.longValue) {
            return false;
         } else {
            if (!stack2.isEmpty()) {
               if (method188(stack.getItem()) && !Objects.equals(stack.get(DataComponents.POTION_CONTENTS), stack2.get(DataComponents.POTION_CONTENTS))) {
                  return false;
               }

               if (!stack.getHoverName().getString().trim().equalsIgnoreCase(stack2.getHoverName().getString().trim())) {
                  return false;
               }
            }

            return !stack.is(Items.TOTEM_OF_UNDYING) || entry2.itemMatcher.getClass() != ItemMatcher.class || !stack.hasFoil();
         }
      }
   }

   private ItemStack method187(ItemMatcher itemMatcher) {
      for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
         ItemStack stack = mc.player.getInventory().getItem(i);
         if (!stack.isEmpty() && itemMatcher.method2672(stack)) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   private static boolean method188(Item item) {
      return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
   }

   private boolean method189(ItemStack stack) {
      ItemLore itemLore = stack.get(DataComponents.LORE);
      if (itemLore == null) {
         return false;
      } else {
         for (Component line : itemLore.lines()) {
            if (line.getString().toLowerCase(Locale.ROOT).contains("нажмите, чтобы забрать")) {
               return true;
            }
         }

         return false;
      }
   }

   private long method190(ItemStack stack) {
      long l;
      return (l = this.method199(stack)) == Long.MAX_VALUE ? Long.MAX_VALUE : l / Math.max(1, ItemNbtUtils.method90(stack));
   }

   private boolean method191(AbstractContainerMenu menu, String str) {
      String lowerCase = str.toLowerCase(Locale.ROOT);
      int n = Math.max(0, menu.slots.size() - 36);

      for (int i = 0; i < n; i++) {
         Slot slot = menu.getSlot(i);
         if (slot != null && slot.hasItem() && slot.getItem().getHoverName().getString().toLowerCase(Locale.ROOT).contains(lowerCase)) {
            mc.gameMode.handleContainerInput(menu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, mc.player);
            return true;
         }
      }

      return false;
   }

   private void method192() {
      if (mc.player != null && mc.gui.screen() instanceof ContainerScreen) {
         mc.player.closeContainer();
      }
   }

   @EventTarget
   public void method193(PacketEvent event) {
      if (event.isIncoming() && this.entry2 != null && this.method210()) {
         if (event.getPacket() instanceof ClientboundSystemChatPacket systemChatPacket) {
            String lowerCase = systemChatPacket.content().getString().toLowerCase(Locale.ROOT);
            if (lowerCase.contains("этот товар уже купили")) {
               this.flag = true;
            } else if (lowerCase.contains("не хватает монет")) {
               this.flag2 = true;
            } else if (lowerCase.contains("недоступна в режиме afk")) {
               this.flag3 = true;
            }
         }

         if (event.getPacket() instanceof ClientboundOpenScreenPacket && !(mc.gui.screen() instanceof ContainerScreen)) {
            if (this.intValue7 >= 8) {
               this.flag4 = true;
            }

            this.entry4.reset();
         }
      }
   }

   @EventTarget
   public void method194(MoveInputEvent event) {
      if (this.method210() && this.entry2 != null) {
         if (this.intValue8 > 0) {
            ThreadLocalRandom current = ThreadLocalRandom.current();
            event.setMovement(current.nextBoolean() ? 1.0F : -1.0F, current.nextBoolean() ? 1.0F : -1.0F);
         } else if (mc.gui.screen() instanceof AbstractContainerScreen) {
            event.setMovement(0.0F, 0.0F);
         }
      }
   }

   @EventTarget
   public void method195(KeyEvent event) {
      if (this.method210() && this.entry2 != null && event.getKeyCode() == 256 && event.getIntValue() == 1) {
         this.flag5 = true;
      }
   }

   private long method196(ItemStack stack) {
      ItemLore itemLore = stack.get(DataComponents.LORE);
      if (itemLore == null) {
         return Long.MAX_VALUE;
      } else {
         long l = Long.MAX_VALUE;

         for (Component line : itemLore.lines()) {
            String input = line.getString().replace(' ', ' ');
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
               long l2;
               if ((l2 = this.method201(matcher.group(1))) != Long.MAX_VALUE) {
                  return l2;
               }
            } else if (l == Long.MAX_VALUE) {
               Matcher matcher2 = pattern2.matcher(input);
               if (matcher2.find()) {
                  l = this.method201(matcher2.group(1));
               }
            }
         }

         if (l != Long.MAX_VALUE) {
            return Math.max(1L, l / Math.max(1, ItemNbtUtils.method90(stack)));
         } else {
            long l2;
            return (l2 = this.method199(stack)) != Long.MAX_VALUE ? Math.max(1L, l2 / Math.max(1, ItemNbtUtils.method90(stack))) : Long.MAX_VALUE;
         }
      }
   }

   private boolean method197(AutoInventoryModule.Entry2 entry2, ItemStack stack, long l) {
      return entry2.longValue <= 0L ? false : (this.method210() ? Math.max(1L, l / Math.max(1, ItemNbtUtils.method90(stack))) : l) > entry2.longValue;
   }

   private long method198(ItemStack stack) {
      if (!this.method210()) {
         return this.method196(stack);
      } else {
         long l;
         if ((l = this.method199(stack)) != Long.MAX_VALUE) {
            return l;
         } else {
            long l2;
            return (l2 = this.method196(stack)) == Long.MAX_VALUE ? Long.MAX_VALUE : l2 * Math.max(1, ItemNbtUtils.method90(stack));
         }
      }
   }

   private long method199(ItemStack stack) {
      ItemLore itemLore = stack.get(DataComponents.LORE);
      if (itemLore == null) {
         return Long.MAX_VALUE;
      } else {
         for (Component line : itemLore.lines()) {
            String input = line.getString().replace(' ', ' ');
            Matcher matcher = pattern3.matcher(input);
            long l;
            if (matcher.find() && (l = this.method201(matcher.group(1))) != Long.MAX_VALUE) {
               return l;
            }
         }

         return Long.MAX_VALUE;
      }
   }

   private long method200(long l, int intValue) {
      return this.method210() ? l : l * intValue;
   }

   private long method201(String group) {
      try {
         return Long.parseLong(group.replace(" ", "").replace(",", ""));
      } catch (NumberFormatException e) {
         return Long.MAX_VALUE;
      }
   }

   private int method202(ItemMatcher itemMatcher, float floatValue) {
      if (mc.player == null) {
         return 0;
      } else {
         int n = 0;

         for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack != null && !stack.isEmpty() && itemMatcher.method2672(stack) && this.method204(stack, floatValue)) {
               n += Math.max(1, stack.getCount());
            }
         }

         return n;
      }
   }

   private String method203(ItemMatcher itemMatcher, float floatValue) {
      return itemMatcher.getText22() + "|" + itemMatcher.getItemStack().getItem() + "|" + floatValue;
   }

   private boolean method204(ItemStack stack, float floatValue) {
      if (floatValue <= 0.0F) {
         return true;
      } else {
         int maxDamage;
         return (maxDamage = stack.getMaxDamage()) <= 0 ? false : (float)(maxDamage - stack.getDamageValue()) / maxDamage >= floatValue;
      }
   }

   private void method205() {
      if (mc.player != null) {
         if (mc.gui.screen() != null) {
            if (mc.player.containerMenu != mc.player.inventoryMenu) {
               mc.player.closeContainer();
            }

            mc.gui.setScreen(null);
         }
      }
   }

   public InventorySnapshot getInventorySnapshot() {
      return this.inventorySnapshot;
   }

   public ModeSetting getServer() {
      return this.server;
   }

   public String method206() {
      return this.server.get();
   }

   public InventoryPreset method207() {
      if (this.method210()) {
         return this.inventoryPreset;
      } else {
         return "SpookyTime".equals(this.method206()) ? this.inventoryPreset3 : this.inventoryPreset2;
      }
   }

   public ServerNameNormalizer getServerNameNormalizer() {
      return this.serverNameNormalizer;
   }

   public AutoInventoryModule.Data2 method208() {
      if (mc.player == null) {
         return new AutoInventoryModule.Data2(0, 0, 0);
      } else {
         InventoryPreset inventoryPreset = this.method207();
         inventoryPreset.method876();
         Inventory inventory = mc.player.getInventory();
         int intValue = 0;
         int n = 0;
         int n2 = 0;

         for (int i = 0; i < 36 && i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null && !stack.isEmpty()) {
               int selectedSlot = i < 9 ? 27 + i : i - 9;
               switch (method209(inventoryPreset, stack, selectedSlot)) {
                  case 0:
                     n++;
                     break;
                  case 1:
                     intValue++;
                     break;
                  default:
                     n2++;
               }
            }
         }

         for (int i = 36; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null && !stack.isEmpty()) {
               switch (method209(inventoryPreset, stack, inventoryPreset.method873())) {
                  case 0:
                     n++;
                     break;
                  case 1:
                     intValue++;
                     break;
                  default:
                     n2++;
               }
            }
         }

         return new AutoInventoryModule.Data2(intValue, n, n2);
      }
   }

   private static int method209(InventoryPreset inventoryPreset, ItemStack stack, int selectedSlot) {
      ItemMatcher key = inventoryPreset.method890(stack);
      if (key == null) {
         return 0;
      } else if (selectedSlot < 0) {
         return -1;
      } else {
         inventoryPreset.method874(selectedSlot, key);
         inventoryPreset.method878(selectedSlot, stack.getCount());
         return 1;
      }
   }

   private boolean method210() {
      return "Funtime 1.21".equals(this.method206());
   }

   public String method211() {
      return switch (this.type) {
         case IDLE -> this.isEnabled() ? "пауза" : "остановлен";
         case AUCTION -> "поиск";
         case PURCHASE -> "покупка";
         case INVENTORY -> "ожидание";
         case FINISHED -> "готово";
      };
   }

   public String method212() {
      return this.entry2 == null ? "" : this.entry2.text;
   }

   public int getIntValue4() {
      return this.intValue4;
   }

   public int getIntValue5() {
      return this.intValue5;
   }

   public long getLongValue() {
      return this.longValue;
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      json.add("SetFuntime", this.inventoryPreset.save());
      json.add("SetHolyWorld", this.inventoryPreset2.save());
      json.add("SetSpookyTime", this.inventoryPreset3.save());
      json.add("Presets", this.serverNameNormalizer.method1234());
      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json != null) {
         if (json.has("SetFuntime") && json.get("SetFuntime").isJsonObject()) {
            this.inventoryPreset.load(json.getAsJsonObject("SetFuntime"));
         }

         if (json.has("SetHolyWorld") && json.get("SetHolyWorld").isJsonObject()) {
            this.inventoryPreset2.load(json.getAsJsonObject("SetHolyWorld"));
         }

         if (json.has("SetSpookyTime") && json.get("SetSpookyTime").isJsonObject()) {
            this.inventoryPreset3.load(json.getAsJsonObject("SetSpookyTime"));
         }

         if (json.has("Presets") && json.get("Presets").isJsonArray()) {
            this.serverNameNormalizer.method1235(json.getAsJsonArray("Presets"));
         }
      }
   }

   private static final class Data {
      private final int intValue;
      private final long longValue;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(int intValue, long longValue) {
         this.intValue = intValue;
         this.longValue = longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   public static final class Data2 {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3);
      }

      @Override
      public String toString() {
         return "Data2[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + "]";
      }

      public Data2(int intValue, int n, int n2) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   private static final class Entry {
      private volatile long last = System.currentTimeMillis();
      private volatile long longValue;

      public void reset() {
         this.last = System.currentTimeMillis();
         this.longValue = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
      }

      public void method213() {
         this.last = 0L;
      }

      public boolean method214(long l, long l2) {
         return System.currentTimeMillis() - (l + this.longValue % (l2 + 1L)) >= this.last;
      }
   }

   private static class Entry2 {
      private final ItemMatcher itemMatcher;
      private final String text;
      private final int intValue;
      private final int intValue2;
      private final float floatValue;
      private final long longValue;
      private int intValue3;

      private Entry2(ItemMatcher itemMatcher, String text, int intValue, int n, float floatValue, long longValue) {
         this.itemMatcher = itemMatcher;
         this.text = text;
         this.intValue = intValue;
         this.intValue2 = n;
         this.floatValue = floatValue;
         this.longValue = longValue;
      }
   }

   private static class Entry3 {
      private final ItemStack itemStack;
      private final String text;
      private final int intValue;
      private final long longValue;

      private Entry3(ItemStack stack, String text, int intValue, long longValue) {
         this.itemStack = stack.copy();
         this.text = text;
         this.intValue = intValue;
         this.longValue = longValue;
      }
   }

   private static enum Type {
      IDLE,
      AUCTION,
      PURCHASE,
      INVENTORY,
      FINISHED;
   }
}

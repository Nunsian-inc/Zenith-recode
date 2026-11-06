package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.HealthUpdateEvent;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.CombatDamageCalculator;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoTotem",
   category = ModuleCategory.COMBAT,
   description = "module.autototem.description"
)
public final class AutoTotemModule extends Module {
   public static final AutoTotemModule INSTANCE = new AutoTotemModule();
   private final SliderSetting healthSetting = new SliderSetting(
      "module.autoTotem.healthSetting", 4.0F, 2.0F, 20.0F, 0.5F, "module.autoTotem.healthSetting.desc", "hp"
   );
   private final SliderSetting elytraHealthSetting = new SliderSetting(
      "module.autoTotem.elytraHealthSetting", 4.0F, 2.0F, 20.0F, 0.5F, "module.autoTotem.elytraHealthSetting.desc", "hp"
   );
   private final MultiSelectSetting triggerSetting = MultiSelectSetting.method5224(
      "module.autoTotem.triggerSetting",
      "module.autoTotem.triggerSetting.desc",
      List.of(
         "module.autoTotem.triggerCrystal",
         "module.autoTotem.triggerTNT",
         "module.autoTotem.trident",
         "module.autoTotem.minecartTNT",
         "module.autoTotem.triggerPotential",
         "module.autoTotem.triggerCrystalAura",
         "module.autoTotem.triggerCombat"
      )
   );
   private final SliderSetting combatRange = new SliderSetting(
      "module.autoTotem.combatRange", 10.0F, 3.0F, 30.0F, 1.0F, "module.autoTotem.combatRange.desc", "b", () -> this.triggerSetting.method5227(6), null
   );
   private final SliderSetting crystalMargin = new SliderSetting(
      "module.autoTotem.crystalMargin",
      2.0F,
      0.0F,
      10.0F,
      0.5F,
      "module.autoTotem.crystalMargin.desc",
      "hp",
      () -> this.triggerSetting.method5227(0) || this.triggerSetting.method5227(4) || this.triggerSetting.method5227(5),
      null
   );
   private final SliderSetting tNTRangeSetting = new SliderSetting(
      "module.autoTotem.TNTRangeSetting", 8.0F, 0.0F, 50.0F, 4.0F, "module.autoTotem.TNTRangeSetting.desc", "b", () -> this.triggerSetting.method5227(1), null
   );
   private final SliderSetting tridentRangeSetting = new SliderSetting(
      "module.autoTotem.tridentRangeSetting",
      8.0F,
      0.0F,
      50.0F,
      4.0F,
      "module.autoTotem.tridentRangeSetting.desc",
      "b",
      () -> this.triggerSetting.method5227(2),
      null
   );
   private final BooleanSetting predictSetting = new BooleanSetting("module.autoTotem.predictSetting", "module.autoTotem.predictSetting.desc", false);
   private final SliderSetting predictTicksSetting = new SliderSetting(
      "module.autoTotem.predictTicksSetting", 10.0F, 1.0F, 40.0F, 1.0F, "module.autoTotem.predictTicksSetting.desc", "t", this.predictSetting::isEnabled, null
   );
   private final SliderSetting time = new SliderSetting("module.autoTotem.time", 300.0F, 0.0F, 2000.0F, 100.0F, "module.autoTotem.time.desc", "ms");
   private final BooleanSetting hotbarTotems = new BooleanSetting("module.autoTotem.hotbarTotems", "module.autoTotem.hotbarTotems.desc", true);
   private final MultiSelectSetting keepSlots = new MultiSelectSetting("module.autoTotem.keepSlots", "module.autoTotem.keepSlots.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepCrystals", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepObsidian", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepAnchors", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepGlowstone", true);
   private final MultiSelectSetting.Entry entry5 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepPickaxe", true);
   private final MultiSelectSetting.Entry entry6 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepSword", false);
   private final MultiSelectSetting.Entry entry7 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepGapples", false);
   private final MultiSelectSetting.Entry entry8 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepPearls", false);
   private final MultiSelectSetting.Entry entry9 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepWebs", false).method5230();
   private static final int CONST_intValue = 45;
   private static final int CONST_intValue2 = 40;
   private Slot slot = null;
   private StopWatch stopWatch = new StopWatch();
   private int intValue = Integer.MIN_VALUE;
   private volatile Boolean booleanValue;
   private static final int CONST_intValue3 = 3;
   private static final int CONST_intValue4 = 10;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4 = Integer.MIN_VALUE;
   private int intValue5 = -1;
   private volatile boolean flag;
   private volatile boolean flag2;
   private volatile int intValue6;
   private int intValue7 = Integer.MIN_VALUE;
   private final Deque<AutoTotemModule.Entry> deque = new ArrayDeque<>();
   private float floatValue = Float.NaN;
   private int intValue8 = -1;
   private final CombatDamageCalculator class35 = new CombatDamageCalculator();
   private volatile boolean flag3;
   private volatile int intValue9 = Integer.MIN_VALUE;
   private int intValue10 = Integer.MIN_VALUE;
   private int intValue11 = Integer.MIN_VALUE;
   private int intValue12 = Integer.MIN_VALUE;
   private Item item;
   private int intValue13 = -1;
   private int intValue14 = Integer.MIN_VALUE;
   private final Map<Item, Integer> items = new HashMap<>();
   private static final int CONST_intValue5 = 10;
   private boolean flag4;
   private boolean flag5;
   private int intValue15 = Integer.MIN_VALUE;
   private float floatValue2;

   private AutoTotemModule() {
   }

   @Override
   public void onEnable() {
      this.method734();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.deque.clear();
      this.floatValue = Float.NaN;
      this.intValue8 = -1;
      super.onDisable();
   }

   @EventTarget
   public void method700(PacketEvent event) {
      if (event.isIncoming() && mc.player != null) {
         if (event.getPacket() instanceof ClientboundEntityEventPacket entityEventPacket && entityEventPacket.getEventId() == 35 && entityEventPacket.entityId == mc.player.getId()) {
            this.booleanValue = Boolean.FALSE;
            this.flag3 = true;
            this.intValue9 = mc.player.tickCount;
            this.log("поп: левая рука на сервере пуста");
         } else if (event.getPacket() instanceof ClientboundContainerSetSlotPacket containerSetSlotPacket && containerSetSlotPacket.getContainerId() == 0 && containerSetSlotPacket.getSlot() == 45) {
            this.method701(containerSetSlotPacket.getItem(), "слот 45");
         } else if (event.getPacket() instanceof ClientboundContainerSetContentPacket setContentPacket && setContentPacket.containerId() == 0 && setContentPacket.items().size() > 45) {
            this.method701(setContentPacket.items().get(45), "весь инвентарь");
         } else if (event.getPacket() instanceof ClientboundSetPlayerInventoryPacket playerInventoryPacket && playerInventoryPacket.slot() == 40) {
            this.method701(playerInventoryPacket.contents(), "слот 40");
         } else if (event.getPacket() instanceof ClientboundPlayerCombatKillPacket playerCombatKillPacket && playerCombatKillPacket.playerId() == mc.player.getId() && !CrystalAuraModule.INSTANCE.isEnabled()) {
            this.log("СМЕРТЬ: " + playerCombatKillPacket.message().getString() + ", тотемов в инвентаре " + this.method723());
         }
      }
   }

   private void method701(ItemStack contents, String str) {
      boolean is = contents.is(Items.TOTEM_OF_UNDYING);
      this.booleanValue = is;
      if (is && this.flag) {
         this.flag = false;
         this.log("сервер подтвердил тотем в левой руке (" + str + ")");
      } else if (!is) {
         if (this.flag
            && mc.player != null
            && mc.player.tickCount > this.intValue11
            && !this.method702()
            && mc.player.tickCount - this.intValue11 >= Math.max(1, this.method724() - 1)) {
            this.flag = false;
            this.intValue6++;
         }

         this.log("сервер: в левой руке " + (contents.isEmpty() ? "пусто" : contents.getItem().toString()) + " (" + str + ")");
      }
   }

   private boolean method702() {
      int n = this.intValue9;
      return n != Integer.MIN_VALUE && mc.player != null && mc.player.tickCount >= n && mc.player.tickCount - n <= 2 + this.method724();
   }

   public boolean method703() {
      return mc.player != null && InventoryUtils.method906(Items.TOTEM_OF_UNDYING) != null;
   }

   public boolean method704() {
      return mc.player != null
         && this.isEnabled()
         && (
            this.intValue11 != Integer.MIN_VALUE && mc.player.tickCount >= this.intValue11 && mc.player.tickCount - this.intValue11 <= 1
               || !KeyBindUtils.method2629(AutoTotemModule.class)
         );
   }

   public boolean method705() {
      return mc.player != null && this.isEnabled() && !KeyBindUtils.method2629(AutoTotemModule.class);
   }

   public boolean method706() {
      return mc.player != null
         && this.isEnabled()
         && this.intValue11 != Integer.MIN_VALUE
         && mc.player.tickCount >= this.intValue11
         && mc.player.tickCount - this.intValue11 <= this.method724() + 3
         && this.flag5;
   }

   public boolean method707() {
      return mc.player != null && mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING) && this.booleanValue != Boolean.FALSE && !this.flag;
   }

   public boolean isFlag() {
      return this.flag;
   }

   @EventTarget
   public void method708(HealthUpdateEvent event) {
      this.method710();
   }

   @EventTarget
   public void method709(PreRotationEvent event) {
      this.method710();
   }

   public void method710() {
      if (this.stopWatch == null) {
         this.stopWatch = new StopWatch();
      }

      this.method731();
      if (!UseItemUtils.method6526() && !mc.player.isDeadOrDying()) {
         int tickCount = mc.player.tickCount;
         CrystalAuraModule.INSTANCE.method5926();
         int n = 0;

         for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) {
               n++;
            }
         }

         if (n != this.intValue5) {
            this.log("в хотбаре тотемов: " + n + " (было " + Math.max(0, this.intValue5) + "), всего " + this.method723());
            this.intValue5 = n;
         }

         if (this.intValue11 > tickCount) {
            this.intValue11 = Integer.MIN_VALUE;
            this.intValue = Integer.MIN_VALUE;
            this.intValue2 = Integer.MIN_VALUE;
            this.intValue3 = Integer.MIN_VALUE;
            this.intValue4 = Integer.MIN_VALUE;
            this.flag = false;
            this.booleanValue = null;
            this.slot = null;
            this.intValue9 = Integer.MIN_VALUE;
         }

         if (this.flag3) {
            this.flag3 = false;
            this.intValue10 = tickCount;
         }

         boolean flag = this.method728();
         boolean flag2 = this.method725();
         if (flag2) {
            this.intValue = tickCount;
         }

         if (this.flag) {
            if (tickCount <= this.intValue7) {
               return;
            }

            if (!this.flag2) {
               this.flag2 = true;
               InventoryUtils.method899();
               this.intValue7 = tickCount + this.method724() + 4;
               this.log("сервер молчит " + (tickCount - this.intValue11) + " т — запрашиваем инвентарь");
               return;
            }

            this.flag = false;
            this.booleanValue = null;
            this.log("ответа нет и на запрос — верим клиенту");
         }

         if (this.intValue6 > 0) {
            this.log("сервер откатил своп (" + this.intValue6 + ") — повторяем");
            this.intValue6 = 0;
         }

         boolean flag3 = this.method711();
         if (!flag2) {
            if (this.slot != null
               && flag3
               && this.intValue != Integer.MIN_VALUE
               && tickCount - this.intValue >= Math.max(1, Math.round(this.time.getValue3() / 50.0F)) + 20
               && !this.method726()
               && KeyBindUtils.method2629(AutoTotemModule.class)) {
               Slot slot = this.slot;
               this.slot = null;
               this.intValue11 = tickCount;
               this.flag4 = true;
               this.flag5 = false;
               this.method714(slot);
            } else {
               this.method715(tickCount);
            }
         } else if (flag3) {
            this.method715(tickCount);
         } else {
            Slot slot = this.method720();
            if (slot != null) {
               boolean flag4 = flag || this.method727() || this.method726() || mc.player.hurtTime > 0;
               boolean flag5 = slot.index >= 36 && slot.index <= 44;
               if (!flag5 && !flag4 && InventorySettingModule.INSTANCE.method2397()) {
                  if (KeyBindUtils.method2629(AutoTotemModule.class)) {
                     this.intValue12 = tickCount;
                     KeyBindUtils.method2628(AutoTotemModule.class, () -> {
                        Slot var1x = this.method720();
                        if (var1x != null && !this.method711()) {
                           this.method712(var1x, mc.player.tickCount);
                        }
                     }, 100);
                  } else if (this.intValue12 == Integer.MIN_VALUE || tickCount - this.intValue12 > 10) {
                     Zenith.getInstance().getTaskScheduler().method5114(AutoTotemModule.class);
                     this.log("не дождались остановки за " + (tickCount - this.intValue12) + " т — берём тотем на ходу");
                     this.method712(slot, tickCount);
                  }
               } else {
                  if (!flag5 && !flag && !flag4 && !this.stopWatch.method7546(150L)) {
                     return;
                  }

                  if (!KeyBindUtils.method2629(AutoTotemModule.class)) {
                     Zenith.getInstance().getTaskScheduler().method5114(AutoTotemModule.class);
                  }

                  this.method712(slot, tickCount);
               }
            }
         }
      }
   }

   private boolean method711() {
      return mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING) && this.booleanValue != Boolean.FALSE;
   }

   private void method712(Slot slot, int tickCount) {
      ItemStack offhandItem = mc.player.getOffhandItem();
      this.stopWatch.reset();
      this.intValue11 = tickCount;
      int selected = slot.index >= 36 && slot.index <= 44 ? slot.index - 36 : -1;
      this.flag4 = selected == -1;
      this.flag5 = false;
      if (selected != -1) {
         this.method713(selected);
         this.flag5 = true;
         this.log("кладём тотем из хотбара " + selected + " (смена рук, без кликов по инвентарю)");
      } else {
         if (this.method722() && this.intValue3 != Integer.MIN_VALUE && tickCount - this.intValue3 < 10) {
            return;
         }

         this.intValue3 = tickCount;
         this.method714(slot);
         this.flag5 = true;
         this.log("кладём тотем из инвентаря, слот " + slot.index + " (клик: в хотбаре тотемов нет)");
      }

      this.flag = true;
      this.flag2 = false;
      this.intValue7 = tickCount + this.method724() + 4;
      if (this.slot == null && !offhandItem.isEmpty() && !offhandItem.is(Items.TOTEM_OF_UNDYING)) {
         this.slot = slot;
      }
   }

   private void method713(int selected) {
      int selectedSlot = mc.player.getInventory().getSelectedSlot();
      if (selectedSlot != selected) {
         mc.player.getInventory().setSelectedSlot(selected);
         mc.gameMode.ensureHasSentCarriedItem();
      }

      mc.getConnection().send(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
      if (selectedSlot != selected) {
         mc.player.getInventory().setSelectedSlot(selectedSlot);
         mc.gameMode.ensureHasSentCarriedItem();
      }
   }

   private void method714(Slot slot) {
      if (!(mc.player.containerMenu instanceof InventoryMenu)) {
         InventoryUtils.closeScreen();
      }

      InventoryMenu inventoryMenu = mc.player.inventoryMenu;
      mc.gameMode.handleContainerInput(inventoryMenu.containerId, slot.index, 40, ContainerInput.SWAP, mc.player);
      if (mc.gui.screen() == null) {
         mc.getConnection().send(new ServerboundContainerClosePacket(inventoryMenu.containerId));
      }
   }

   private void method715(int tickCount) {
      if (this.hotbarTotems.isEnabled()
         && (this.intValue2 == Integer.MIN_VALUE || tickCount - this.intValue2 >= 3)
         && mc.player.containerMenu == mc.player.inventoryMenu) {
         if (mc.player.hurtTime <= 0 && !this.method727() && (this.intValue4 == Integer.MIN_VALUE || tickCount - this.intValue4 >= 20)) {
            Inventory inventory = mc.player.getInventory();
            boolean[] flags = new boolean[9];

            for (Predicate predicate : this.method716()) {
               int n = -1;

               for (int i = 0; i < 9; i++) {
                  if (!flags[i] && predicate.test(inventory.getItem(i))) {
                     n = i;
                     break;
                  }
               }

               if (n == -1) {
                  int index = this.method717(predicate);
                  if (index != -1) {
                     int buttonNum = method718(flags, inventory);
                     if (buttonNum != -1) {
                        Item key = mc.player.inventoryMenu.getSlot(index).getItem().getItem();
                        if (this.items.getOrDefault(key, Integer.MIN_VALUE) <= tickCount) {
                           if (key != this.item || buttonNum != this.intValue13 || tickCount - this.intValue14 >= 60) {
                              this.item = key;
                              this.intValue13 = buttonNum;
                              this.intValue14 = tickCount;
                              this.method719(index, buttonNum, tickCount, "занесли в хотбар " + key);
                              return;
                           }

                           this.items.put(key, tickCount + 400);
                           this.log("забивка хотбара зациклилась на " + key + " в слот " + buttonNum + " — не трогаем 20 с");
                        }
                     }
                  }
               } else {
                  flags[n] = true;
               }
            }

            int index = this.method717(stack -> stack.is(Items.TOTEM_OF_UNDYING));
            if (index != -1) {
               for (int i = 0; i < 9; i++) {
                  if (!flags[i] && !inventory.getItem(i).is(Items.TOTEM_OF_UNDYING) && (i != inventory.getSelectedSlot() || !mc.player.isUsingItem())) {
                     this.method719(index, i, tickCount, "тотем в хотбар, слот " + i);
                     return;
                  }
               }
            }
         }
      }
   }

   private List<Predicate<ItemStack>> method716() {
      ArrayList<Predicate<ItemStack>> predicates = new ArrayList<>();
      if (this.entry.isEnabled()) {
         predicates.add(stack -> stack.is(Items.END_CRYSTAL));
      }

      if (this.entry2.isEnabled()) {
         predicates.add(stack -> stack.is(Items.OBSIDIAN));
      }

      if (this.entry3.isEnabled()) {
         predicates.add(stack -> stack.is(Items.RESPAWN_ANCHOR));
      }

      if (this.entry4.isEnabled()) {
         predicates.add(stack -> stack.is(Items.GLOWSTONE));
      }

      if (this.entry5.isEnabled()) {
         predicates.add(stack -> stack.is(ItemTags.PICKAXES));
      }

      if (this.entry6.isEnabled() || CrystalAuraModule.INSTANCE.method5744()) {
         predicates.add(stack -> stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES));
      }

      if (this.entry7.isEnabled()) {
         predicates.add(stack -> stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE));
      }

      if (this.entry8.isEnabled()) {
         predicates.add(stack -> stack.is(Items.ENDER_PEARL));
      }

      if (this.entry9.isEnabled() || CrystalAuraModule.INSTANCE.method5769()) {
         predicates.add(stack -> stack.is(Items.COBWEB));
      }

      return predicates;
   }

   private int method717(Predicate<ItemStack> predicate) {
      for (int i = 9; i <= 35; i++) {
         if (predicate.test(mc.player.inventoryMenu.getSlot(i).getItem())) {
            return i;
         }
      }

      return -1;
   }

   private static int method718(boolean[] flags, Inventory inventory) {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            if (!flags[j]) {
               ItemStack stack = inventory.getItem(j);
               boolean empty = i == 0 ? stack.isEmpty() : (i == 1 ? !stack.is(Items.TOTEM_OF_UNDYING) : true);
               if (empty) {
                  return j;
               }
            }
         }
      }

      return -1;
   }

   private void method719(int index, int buttonNum, int tickCount, String str) {
      this.intValue2 = tickCount;
      mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, index, buttonNum, ContainerInput.SWAP, mc.player);
      if (mc.gui.screen() == null) {
         mc.getConnection().send(new ServerboundContainerClosePacket(mc.player.inventoryMenu.containerId));
      }

      this.log(str);
   }

   private Slot method720() {
      Slot slot = this.method721(slot2 -> slot2.index >= 36 && slot2.index <= 44);
      return slot != null ? slot : this.method721(slot2 -> slot2.index != 45);
   }

   private Slot method721(Predicate<Slot> predicate) {
      return InventoryUtils.method912(
         mc.player.inventoryMenu,
         Items.TOTEM_OF_UNDYING,
         Comparator.<Slot, Boolean>comparing(slot -> !slot.getItem().isEnchanted()).thenComparing((slot, var1x) -> {
            CustomData customData = slot.getItem().get(DataComponents.CUSTOM_DATA);
            CustomData customData2 = var1x.getItem().get(DataComponents.CUSTOM_DATA);
            if (customData == null && customData2 != null) {
               return -1;
            } else if (customData != null && customData2 == null) {
               return 1;
            } else if (customData == null) {
               return 0;
            } else {
               boolean contains = customData.copyTag().contains("sphereEffect");
               boolean flag = customData2.copyTag().contains("sphereEffect");
               if (contains && !flag) {
                  return 1;
               } else if (!contains && flag) {
                  return -1;
               } else if (!contains) {
                  return 0;
               } else {
                  String string = customData.copyTag().get("sphereEffect").toString();
                  String str = customData2.copyTag().get("sphereEffect").toString();
                  return Integer.compare(str.length(), string.length());
               }
            }
         }).thenComparing(Comparator.comparingInt(slot -> slot.index)),
         var1x -> var1x.index != 45 && predicate.test(var1x)
      );
   }

   private void log(String str) {
      CrystalAuraModule.INSTANCE.method5927(str);
   }

   private boolean method722() {
      return this.method726() || mc.player.hurtTime > 0 || this.method727();
   }

   private int method723() {
      int n = 0;

      for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
         if (mc.player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) {
            n++;
         }
      }

      return n;
   }

   private int method724() {
      if (mc.getConnection() == null) {
         return 2;
      } else {
         PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(mc.player.getUUID());
         return playerInfo == null ? 2 : Mth.clamp(Math.round(playerInfo.getLatency() / 50.0F), 0, 20);
      }
   }

   public boolean method725() {
      if (UseItemUtils.method6526()) {
         return false;
      } else if (this.isEnabled() && InventoryUtils.method906(Items.TOTEM_OF_UNDYING) != null) {
         boolean flag = mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA);
         float f = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         if (mc.player.getCooldowns().isOnCooldown(Items.TOTEM_OF_UNDYING.getDefaultInstance())) {
            return false;
         } else if (f < (flag ? this.elytraHealthSetting.getValue3() : this.healthSetting.getValue3())) {
            return true;
         } else if (this.predictSetting.isEnabled() && f <= this.method732()) {
            return true;
         } else if (this.method727()) {
            return true;
         } else if (this.triggerSetting.method5227(6) && this.method726()) {
            return true;
         } else if ((this.triggerSetting.method5227(0) || this.triggerSetting.method5227(4) || this.triggerSetting.method5227(5))
            && f - this.method729() <= this.crystalMargin.getValue3()) {
            return true;
         } else {
            return this.triggerSetting.method5227(2)
                  && UseItemUtils.method6510()
                     .anyMatch(
                        var1x -> var1x instanceof ThrownTrident
                           && mc.player.distanceToSqr(var1x) < this.tridentRangeSetting.getValue3() * this.tridentRangeSetting.getValue3()
                     )
               ? true
               : this.triggerSetting.method5227(1)
                  && UseItemUtils.method6510()
                     .anyMatch(
                        var1x -> (var1x instanceof PrimedTnt || var1x instanceof MinecartTNT && this.triggerSetting.method5227(3))
                           && mc.player.distanceToSqr(var1x) < this.tNTRangeSetting.getValue3() * this.tNTRangeSetting.getValue3()
                     );
         }
      } else {
         return false;
      }
   }

   private boolean method726() {
      double d = this.combatRange.getValue3();

      for (Player player : mc.level.players()) {
         if (player != mc.player
            && CrystalAuraModule.method5901(player)
            && mc.player.distanceToSqr(player) <= d * d
            && !Zenith.getInstance().getFriends().method7626(player)
            && !AntiBotModule.INSTANCE.method2785(player)) {
            return true;
         }
      }

      return false;
   }

   private boolean method727() {
      if (this.intValue10 == Integer.MIN_VALUE) {
         return false;
      } else {
         int n = mc.player.tickCount - this.intValue10;
         return n >= 0 && n <= 20;
      }
   }

   private boolean method728() {
      if (this.method727()) {
         return true;
      } else {
         float f = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         return (this.triggerSetting.method5227(0) || this.triggerSetting.method5227(4) || this.triggerSetting.method5227(5))
            && this.method729() >= f - 0.5F;
      }
   }

   private float method729() {
      int tickCount = mc.player.tickCount;
      if (this.intValue15 == tickCount) {
         return this.floatValue2;
      } else {
         this.intValue15 = tickCount;
         this.class35.reset();
         AABB boundingBox = mc.player.getBoundingBox();
         float f = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         float f2 = 0.0F;
         if (this.triggerSetting.method5227(0)) {
            for (EndCrystal endCrystal : mc.level.getEntitiesOfClass(EndCrystal.class, boundingBox.inflate(12.0), Entity::isAlive)) {
               f2 = Math.max(f2, this.class35.method5452(endCrystal.position(), 6.0F, mc.player, boundingBox, null, null));
            }

            if (mc.level.dimension() != Level.NETHER) {
               BlockPos blockPosition = mc.player.blockPosition();

               for (BlockPos pos : BlockPos.betweenClosed(blockPosition.offset(-5, -3, -5), blockPosition.offset(5, 4, 5))) {
                  BlockState blockState = mc.level.getBlockState(pos);
                  if (blockState.is(Blocks.RESPAWN_ANCHOR) && blockState.getValue(RespawnAnchorBlock.CHARGE) > 0) {
                     BlockPos immutable = pos.immutable();
                     f2 = Math.max(f2, this.class35.method5452(Vec3.atCenterOf(immutable), 5.0F, mc.player, boundingBox, immutable, null));
                  }
               }
            }
         }

         if (this.triggerSetting.method5227(5) && CrystalAuraModule.INSTANCE.isEnabled()) {
            f2 = Math.max(f2, CrystalAuraModule.INSTANCE.method5742());
         }

         if (this.triggerSetting.method5227(4) && f2 < f - this.crystalMargin.getValue3()) {
            f2 = Math.max(f2, this.method730(boundingBox, f));
         }

         this.floatValue2 = f2;
         return f2;
      }
   }

   private float method730(AABB boundingBox, float f) {
      ArrayList<Player> players = new ArrayList();

      for (Player player : mc.level.players()) {
         if (player != mc.player
            && CrystalAuraModule.method5901(player)
            && mc.player.distanceToSqr(player) <= 100.0
            && !Zenith.getInstance().getFriends().method7626(player)) {
            players.add(player);
         }
      }

      if (players.isEmpty()) {
         return 0.0F;
      } else {
         float f2 = f - this.crystalMargin.getValue3();
         float f3 = 0.0F;
         BlockPos blockPosition = mc.player.blockPosition();

         for (BlockPos pos : BlockPos.betweenClosed(blockPosition.offset(-4, -2, -4), blockPosition.offset(4, 2, 4))) {
            BlockState blockState = mc.level.getBlockState(pos);
            if ((blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.BEDROCK)) && mc.level.isEmptyBlock(pos.above())) {
               Vec3 vec = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
               boolean flag = false;

               for (Player player : players) {
                  if (player.getEyePosition().distanceToSqr(vec) <= 36.0) {
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  AABB aabb = new AABB(pos.getX(), pos.getY() + 1.0, pos.getZ(), pos.getX() + 1.0, pos.getY() + 3.0, pos.getZ() + 1.0);
                  if (!aabb.intersects(boundingBox)
                     && mc.level.getEntities((Entity) null, aabb, entity -> !(entity instanceof EndCrystal)).isEmpty()
                     && !(this.class35.method5453(vec, 6.0F, mc.player, boundingBox) < Math.max(f3, f2))) {
                     f3 = Math.max(f3, this.class35.method5452(vec, 6.0F, mc.player, boundingBox, null, null));
                     if (f3 >= f2) {
                        return f3;
                     }
                  }
               }
            }
         }

         return f3;
      }
   }

   private void method731() {
      if (mc.player == null) {
         this.deque.clear();
         this.floatValue = Float.NaN;
         this.intValue8 = -1;
      } else {
         int tickCount = mc.player.tickCount;
         float floatValue = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         if (this.intValue8 > tickCount) {
            this.deque.clear();
            this.floatValue = Float.NaN;
         }

         if (!Float.isNaN(this.floatValue)) {
            float f = this.floatValue - floatValue;
            if (f > 0.0F) {
               this.deque.addLast(new AutoTotemModule.Entry(tickCount, f));
               this.intValue4 = tickCount;
            }
         }

         this.floatValue = floatValue;
         this.intValue8 = tickCount;
         this.method733(tickCount);
      }
   }

   private float method732() {
      this.method733(mc.player.tickCount);
      float f = 0.0F;

      for (AutoTotemModule.Entry entry : this.deque) {
         f = Math.max(f, entry.floatValue);
      }

      return f;
   }

   private void method733(int tickCount) {
      int n = tickCount - (int)this.predictTicksSetting.getValue3() + 1;

      while (!this.deque.isEmpty() && this.deque.peekFirst().intValue < n) {
         this.deque.removeFirst();
      }
   }

   private void method734() {
      this.deque.clear();
      if (mc.player == null) {
         this.floatValue = Float.NaN;
         this.intValue8 = -1;
      } else {
         this.floatValue = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         this.intValue8 = mc.player.tickCount;
      }
   }

   @Generated
   public SliderSetting getHealthSetting() {
      return this.healthSetting;
   }

   @Generated
   public SliderSetting getElytraHealthSetting() {
      return this.elytraHealthSetting;
   }

   @Generated
   public MultiSelectSetting getTriggerSetting() {
      return this.triggerSetting;
   }

   @Generated
   public SliderSetting getCombatRange() {
      return this.combatRange;
   }

   @Generated
   public SliderSetting getCrystalMargin() {
      return this.crystalMargin;
   }

   @Generated
   public SliderSetting getTNTRangeSetting() {
      return this.tNTRangeSetting;
   }

   @Generated
   public SliderSetting getTridentRangeSetting() {
      return this.tridentRangeSetting;
   }

   @Generated
   public BooleanSetting getPredictSetting() {
      return this.predictSetting;
   }

   @Generated
   public SliderSetting getPredictTicksSetting() {
      return this.predictTicksSetting;
   }

   @Generated
   public SliderSetting getTime() {
      return this.time;
   }

   @Generated
   public BooleanSetting getHotbarTotems() {
      return this.hotbarTotems;
   }

   @Generated
   public MultiSelectSetting getKeepSlots() {
      return this.keepSlots;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry() {
      return this.entry;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry2() {
      return this.entry2;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry3() {
      return this.entry3;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry4() {
      return this.entry4;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry5() {
      return this.entry5;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry6() {
      return this.entry6;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry7() {
      return this.entry7;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry8() {
      return this.entry8;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry9() {
      return this.entry9;
   }

   @Generated
   public Slot getSlot() {
      return this.slot;
   }

   @Generated
   public StopWatch getStopWatch() {
      return this.stopWatch;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue;
   }

   @Generated
   public Boolean getBooleanValue() {
      return this.booleanValue;
   }

   @Generated
   public int getIntValue22() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   public int getIntValue42() {
      return this.intValue4;
   }

   @Generated
   public int getIntValue52() {
      return this.intValue5;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag;
   }

   @Generated
   public boolean isFlag22() {
      return this.flag2;
   }

   @Generated
   public int getIntValue6() {
      return this.intValue6;
   }

   @Generated
   public int getIntValue7() {
      return this.intValue7;
   }

   @Generated
   public Deque<AutoTotemModule.Entry> getDeque() {
      return this.deque;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue;
   }

   @Generated
   public int getIntValue8() {
      return this.intValue8;
   }

   @Generated
   public CombatDamageCalculator getClass35() {
      return this.class35;
   }

   @Generated
   public boolean isFlag3() {
      return this.flag3;
   }

   @Generated
   public int getIntValue9() {
      return this.intValue9;
   }

   @Generated
   public int getIntValue10() {
      return this.intValue10;
   }

   @Generated
   public int getIntValue11() {
      return this.intValue11;
   }

   @Generated
   public int getIntValue12() {
      return this.intValue12;
   }

   @Generated
   public Item getItem() {
      return this.item;
   }

   @Generated
   public int getIntValue13() {
      return this.intValue13;
   }

   @Generated
   public int getIntValue14() {
      return this.intValue14;
   }

   @Generated
   public Map<Item, Integer> getItems() {
      return this.items;
   }

   @Generated
   public boolean isFlag4() {
      return this.flag4;
   }

   @Generated
   public boolean isFlag5() {
      return this.flag5;
   }

   @Generated
   public int getIntValue15() {
      return this.intValue15;
   }

   @Generated
   public float getFloatValue22() {
      return this.floatValue2;
   }

   private static final class Entry {
      private final int intValue;
      private final float floatValue;

      private Entry(int intValue, float floatValue) {
         this.intValue = intValue;
         this.floatValue = floatValue;
      }
   }
}

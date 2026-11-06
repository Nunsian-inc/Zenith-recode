package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.BcCameraTestnerv;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.AutoPotionBrewingEnum;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoBrewing",
   category = ModuleCategory.PLAYER,
   description = "module.autopotionbrewing.description"
)
public final class AutoBrewingModule extends Module {
   public static final AutoBrewingModule INSTANCE = new AutoBrewingModule();
   private static final long CONST_longValue = 50L;
   private static final long CONST_longValue2 = 75L;
   private static final long CONST_longValue3 = 20000L;
   private static final long CONST_longValue4 = 1500L;
   private static final int CONST_intValue = 5;
   private static final int CONST_intValue2 = 3;
   private static final int CONST_intValue3 = 4;
   private static final int[] CONST_intValues = new int[]{0, 1, 2};
   private static final ModeSetting potion = new ModeSetting(
      "module.autoPotionBrewing.potion",
      "",
      AutoPotionBrewingEnum.FIRE_RESISTANCE.text,
      AutoPotionBrewingEnum.INVISIBILITY.text,
      AutoPotionBrewingEnum.STRENGTH.text,
      AutoPotionBrewingEnum.SPEED.text,
      AutoPotionBrewingEnum.JUMP_BOOST.text
   );
   private final StopWatch stopWatch = new StopWatch();
   private final List<BlockPos> items = new ArrayList<>();
   private final List<BlockPos> items2 = new ArrayList<>();
   private List<Item> items3 = List.of();
   private Map<Item, Integer> items4 = Map.of();
   private AutoBrewingModule.Type2 type2 = AutoBrewingModule.Type2.IDLE;
   private Rotation rotation;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private int intValue7 = -1;
   private int intValue8 = -1;
   private AutoBrewingModule.Type3 type3 = AutoBrewingModule.Type3.IDLE;
   private boolean flag;
   private boolean flag2;
   private AutoBrewingModule.Type type = AutoBrewingModule.Type.SETUP;

   @Override
   public void onEnable() {
      if (mc.player != null && mc.player.connection != null) {
         this.items3 = this.method4274();
         this.items4 = this.method4275(this.items3);
         this.intValue = 0;
         this.intValue2 = 0;
         this.intValue3 = 0;
         this.intValue4 = 0;
         this.intValue5 = 0;
         this.intValue6 = 0;
         this.method4308();
         this.flag = false;
         this.flag2 = false;
         this.type = AutoBrewingModule.Type.SETUP;
         this.blockPos = null;
         this.blockPos2 = null;
         this.rotation = null;
         this.items.clear();
         this.items2.clear();
         this.method4269();
         this.stopWatch.reset();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      this.type2 = AutoBrewingModule.Type2.IDLE;
      this.rotation = null;
      this.blockPos = null;
      this.blockPos2 = null;
      this.items.clear();
      this.items2.clear();
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.type = AutoBrewingModule.Type.SETUP;
      super.onDisable();
   }

   @EventTarget
   public void method4268(PreRotationEvent event) {
      if (this.rotation != null) {
         Zenith.getInstance().getRotationManager().requestRotation(new RotationRequest(this.rotation, () -> {
            BcCameraTestnerv var1x = Zenith.getInstance().getRotationManager().getBcCameraTestnerv();
            return var1x.method1851(var1x.getRotationConfig(), this.rotation);
         }, Zenith.getInstance().getRotationManager().getBcCameraTestnerv().getRotationConfig()), 5, this);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         switch (this.type2) {
            case IDLE:
            default:
               break;
            case SCAN_CHESTS:
               this.method4278();
               break;
            case OPEN_CHEST:
               this.method4280();
               break;
            case LOOT_CHEST:
               this.method4281();
               break;
            case SCAN_OUTPUT_CHESTS:
               this.method4285();
               break;
            case OPEN_OUTPUT_CHEST:
               this.method4288();
               break;
            case STORE_RESULTS:
               this.method4290();
               break;
            case SCAN_STANDS:
               this.method4293();
               break;
            case OPEN_STAND:
               this.method4298();
               break;
            case LOAD_BOTTLES:
               this.method4299();
               break;
            case LOAD_FUEL:
               this.method4300();
               break;
            case LOAD_INGREDIENT:
               this.method4301();
               break;
            case WAIT_PHASE_DELAY:
               this.method4302();
               break;
            case TAKE_RESULT:
               this.method4303();
         }
      } else {
         this.setToggled(false);
      }
   }

   private void method4269() {
      this.intValue = this.method4273();
      int n = this.intValue * 3;
      if (this.intValue <= 0) {
         if (!this.flag) {
            this.flag = true;
            this.flag2 = false;
            this.type2 = AutoBrewingModule.Type2.SCAN_CHESTS;
         } else {
            this.method4277();
            this.setToggled(false);
         }
      } else {
         ChatLogger.method2019("Ресурсов хватает на " + n + " зелий (" + this.intValue + " варки)");
         this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
      }
   }

   private void method4270() {
      this.method4272();
      this.setToggled(false);
   }

   private void method4271() {
      this.type2 = AutoBrewingModule.Type2.OPEN_STAND;
      this.stopWatch.reset();
   }

   private void method4272() {
      AbstractContainerMenu containerMenu = mc.player.containerMenu;
      if (containerMenu != null && !containerMenu.getCarried().isEmpty()) {
         this.method4309(containerMenu);
         if (!containerMenu.getCarried().isEmpty()) {
            return;
         }
      }

      InventoryUtils.closeScreen();
   }

   private int method4273() {
      int n = this.method4316() / 3;

      for (Entry entry : this.items4.entrySet()) {
         n = Math.min(n, this.method4317((Item)entry.getKey()) / (Integer)entry.getValue());
      }

      return n;
   }

   private List<Item> method4274() {
      return AutoPotionBrewingEnum.method7(potion.get()).method6();
   }

   private Map<Item, Integer> method4275(List<Item> items) {
      LinkedHashMap map = new LinkedHashMap();
      this.method4276(map, Items.BLAZE_POWDER, 1);

      for (Item key : items) {
         this.method4276(map, key, 1);
      }

      return map;
   }

   private void method4276(Map<Item, Integer> map, Item key, int n) {
      map.put(key, map.getOrDefault(key, 0) + n);
   }

   private void method4277() {
      ArrayList elements = new ArrayList();
      int n;
      if ((n = this.method4316()) < 3) {
         elements.add(Items.POTION.getName(Items.POTION.getDefaultInstance()).getString() + " " + n + "/3");
      }

      for (Entry entry : this.items4.entrySet()) {
         int n2 = this.method4317((Item)entry.getKey());
         int n3 = (Integer)entry.getValue();
         if (n2 < n3) {
            elements.add(((Item)entry.getKey()).getName(((Item)entry.getKey()).getDefaultInstance()).getString() + " " + n2 + "/" + n3);
         }
      }

      String name = potion.getValue2().getName();
      if (elements.isEmpty()) {
         ChatLogger.method2019("Для зелья " + name + " ресурсов хватает");
      } else {
         ChatLogger.method2021("Для зелья " + name + " не хватает: " + String.join(", ", elements));
      }
   }

   private void method4278() {
      this.method4286();
      if (this.items2.isEmpty()) {
         this.method4277();
         this.setToggled(false);
      } else {
         this.method4279();
      }
   }

   private void method4279() {
      if (this.items2.isEmpty()) {
         this.method4272();
         if (!this.flag2) {
            this.method4277();
            this.setToggled(false);
         } else {
            this.method4269();
         }
      } else {
         this.blockPos2 = this.items2.removeFirst();
         this.type2 = AutoBrewingModule.Type2.OPEN_CHEST;
         this.stopWatch.reset();
      }
   }

   private void method4280() {
      this.method4289(AutoBrewingModule.Type2.LOOT_CHEST, this::method4279);
   }

   private void method4281() {
      ChestMenu chestMenu = this.method4305();
      if (chestMenu == null) {
         this.method4280();
      } else if (this.stopWatch.method7546(50L)) {
         Slot slot = this.method4282(chestMenu);
         if (slot == null) {
            this.method4272();
            this.blockPos2 = null;
            this.method4279();
         } else if (!this.method4284(slot.getItem())) {
            this.method4272();
            this.blockPos2 = null;
            this.method4279();
         } else {
            InventoryUtils.method905(chestMenu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, false);
            this.flag2 = true;
            this.stopWatch.reset();
         }
      }
   }

   private Slot method4282(ChestMenu chestMenu) {
      int containerSize = chestMenu.getContainer().getContainerSize();

      for (int i = 0; i < containerSize; i++) {
         Slot slot = chestMenu.getSlot(i);
         if (this.method4283(slot.getItem())) {
            return slot;
         }
      }

      return null;
   }

   private boolean method4283(ItemStack stack) {
      if (!this.method4314(stack)) {
         boolean containsKey;
         label20: {
            var map = this.items4;
            Item item = stack.getItem();
            if (map != null) {
               if (map.isEmpty()) {
                  containsKey = false;
                  break label20;
               }
            }

            containsKey = map.containsKey(item);
         }

         if (!containsKey) {
            return false;
         }
      }

      return true;
   }

   private boolean method4284(ItemStack carried) {
      for (int i = 0; i < 36; i++) {
         ItemStack stack = mc.player.getInventory().getItem(i);
         if (stack.isEmpty() || this.method4312(stack, carried) && stack.getCount() < stack.getMaxStackSize()) {
            return true;
         }
      }

      return false;
   }

   private void method4285() {
      this.method4286();
      this.method4287();
   }

   private void method4286() {
      this.items2.clear();

      for (BlockPos pos : UseItemUtils.method6506(mc.player.blockPosition(), 5.0F, 3.0F)) {
         BlockPos immutable = pos.immutable();
         if (this.method4320(mc.level.getBlockState(immutable)) && this.method4319(immutable) != null) {
            this.items2.add(immutable);
         }
      }

      this.items2.sort(Comparator.comparingDouble(pos -> net.minecraft.world.phys.Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition())));
   }

   private void method4287() {
      if (this.items2.isEmpty()) {
         this.method4292();
      } else {
         this.blockPos2 = this.items2.removeFirst();
         this.type2 = AutoBrewingModule.Type2.OPEN_OUTPUT_CHEST;
         this.stopWatch.reset();
      }
   }

   private void method4288() {
      this.method4289(AutoBrewingModule.Type2.STORE_RESULTS, this::method4287);
   }

   private void method4289(AutoBrewingModule.Type2 type2, Runnable runnable) {
      if (this.blockPos2 == null) {
         runnable.run();
      } else if (mc.player.containerMenu instanceof ChestMenu) {
         this.type2 = type2;
         this.rotation = null;
         this.stopWatch.reset();
      } else if (this.stopWatch.method7546(1500L)) {
         this.blockPos2 = null;
         runnable.run();
      } else {
         BlockHitResult blockHitResult = this.method4319(this.blockPos2);
         if (blockHitResult == null) {
            this.blockPos2 = null;
            runnable.run();
         } else {
            this.rotation = RotationUtils.method6913(blockHitResult.getLocation());
            if (this.method4318(this.blockPos2) && this.stopWatch.method7546(50L)) {
               UseItemUtils.method6514(blockHitResult, InteractionHand.MAIN_HAND);
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method4290() {
      ChestMenu containerMenu = this.method4305();
      if (containerMenu == null) {
         this.method4288();
      } else if (this.stopWatch.method7546(50L)) {
         Slot slot = this.method4313(containerMenu, this::method4315);
         if (slot == null) {
            this.method4272();
            this.method4292();
         } else if (!this.method4291(containerMenu, slot.getItem())) {
            this.method4272();
            this.blockPos2 = null;
            this.method4287();
         } else {
            InventoryUtils.method905(containerMenu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, false);
            this.stopWatch.reset();
         }
      }
   }

   private boolean method4291(ChestMenu containerMenu, ItemStack carried) {
      int containerSize = containerMenu.getContainer().getContainerSize();

      for (int i = 0; i < containerSize; i++) {
         ItemStack stack = containerMenu.getSlot(i).getItem();
         if (stack.isEmpty() || this.method4312(stack, carried) && stack.getCount() < stack.getMaxStackSize()) {
            return true;
         }
      }

      return false;
   }

   private void method4292() {
      this.method4272();
      this.blockPos2 = null;
      if (this.intValue <= 0) {
         ChatLogger.method2019("Автозельеварение завершено");
         this.setToggled(false);
      } else {
         this.method4294();
      }
   }

   private void method4293() {
      this.items.clear();

      for (BlockPos pos : UseItemUtils.method6506(mc.player.blockPosition(), 5.0F, 3.0F)) {
         BlockPos immutable = pos.immutable();
         if (mc.level.getBlockState(immutable).is(Blocks.BREWING_STAND) && this.method4319(immutable) != null) {
            this.items.add(immutable);
         }
      }

      this.items.sort(Comparator.comparingDouble(pos -> net.minecraft.world.phys.Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition())));
      if (this.items.isEmpty()) {
         ChatLogger.method2021("Рядом нет доступных зельеварок");
         this.setToggled(false);
      } else {
         this.method4294();
      }
   }

   private void method4294() {
      this.intValue2 = Math.min(this.intValue, this.items.size());
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.type = AutoBrewingModule.Type.SETUP;
      this.method4295();
   }

   private void method4295() {
      if (this.intValue3 >= this.intValue2) {
         this.method4297();
      } else {
         this.blockPos = this.items.get(this.intValue3);
         this.intValue5 = 0;
         this.intValue6 = 0;
         this.method4308();
         this.type2 = AutoBrewingModule.Type2.OPEN_STAND;
         this.stopWatch.reset();
      }
   }

   private void method4296() {
      this.method4272();
      this.intValue3++;
      this.method4295();
   }

   private void method4297() {
      this.method4272();
      this.intValue3 = 0;
      this.method4308();
      if (this.type == AutoBrewingModule.Type.SETUP) {
         this.type = AutoBrewingModule.Type.INGREDIENT_WAIT;
         this.blockPos = null;
         this.type2 = AutoBrewingModule.Type2.WAIT_PHASE_DELAY;
         this.stopWatch.reset();
      } else if (this.type == AutoBrewingModule.Type.INGREDIENT_LOAD) {
         this.type = AutoBrewingModule.Type.INGREDIENT_WAIT;
         this.blockPos = null;
         this.type2 = AutoBrewingModule.Type2.WAIT_PHASE_DELAY;
         this.stopWatch.reset();
      } else {
         this.intValue = this.intValue - this.intValue2;
         this.type2 = AutoBrewingModule.Type2.SCAN_OUTPUT_CHESTS;
         this.stopWatch.reset();
      }
   }

   private void method4298() {
      if (this.blockPos == null) {
         this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
      } else if (mc.player.containerMenu instanceof BrewingStandMenu) {
         this.type2 = switch (this.type) {
            case SETUP -> AutoBrewingModule.Type2.LOAD_BOTTLES;
            case INGREDIENT_LOAD -> AutoBrewingModule.Type2.LOAD_INGREDIENT;
            case INGREDIENT_WAIT -> AutoBrewingModule.Type2.LOAD_INGREDIENT;
            case COLLECT -> AutoBrewingModule.Type2.TAKE_RESULT;
         };
         this.intValue5 = 0;
         this.stopWatch.reset();
         this.rotation = null;
      } else if (this.stopWatch.method7546(1500L)) {
         this.blockPos = null;
         this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
         this.rotation = null;
      } else {
         BlockHitResult blockHitResult = this.method4319(this.blockPos);
         if (blockHitResult == null) {
            this.blockPos = null;
            this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
            this.rotation = null;
         } else {
            this.rotation = RotationUtils.method6913(blockHitResult.getLocation());
            if (this.method4318(this.blockPos) && this.stopWatch.method7546(50L)) {
               UseItemUtils.method6514(blockHitResult, InteractionHand.MAIN_HAND);
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method4299() {
      BrewingStandMenu containerMenu = this.method4304();
      if (containerMenu == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(75L)) {
         if (this.type3 == AutoBrewingModule.Type3.IDLE && !containerMenu.getCarried().isEmpty()) {
            this.method4309(containerMenu);
            this.stopWatch.reset();
         } else if (this.intValue5 >= CONST_intValues.length) {
            int n;
            if ((n = this.method4306(containerMenu)) != -1) {
               this.intValue5 = n;
               this.stopWatch.reset();
            } else {
               this.type2 = AutoBrewingModule.Type2.LOAD_FUEL;
               this.stopWatch.reset();
            }
         } else {
            int index = CONST_intValues[this.intValue5];
            if (this.type3 != AutoBrewingModule.Type3.IDLE) {
               this.method4307(containerMenu, this::method4314, index);
               this.stopWatch.reset();
            } else {
               ItemStack stack = containerMenu.getSlot(index).getItem();
               if (this.method4314(stack)) {
                  this.method4308();
                  this.intValue5++;
                  this.stopWatch.reset();
               } else if (!stack.isEmpty()) {
                  this.intValue6 = 0;
                  this.type2 = AutoBrewingModule.Type2.TAKE_RESULT;
                  this.stopWatch.reset();
               } else if (!this.method4307(containerMenu, this::method4314, index)) {
                  ChatLogger.method2021("Не найдены бутылочки с водой");
                  this.method4270();
               } else {
                  this.stopWatch.reset();
               }
            }
         }
      }
   }

   private void method4300() {
      BrewingStandMenu containerMenu = this.method4304();
      if (containerMenu == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(50L)) {
         if (this.type3 == AutoBrewingModule.Type3.IDLE && !containerMenu.getCarried().isEmpty()) {
            this.method4309(containerMenu);
            this.stopWatch.reset();
         } else if (this.type3 != AutoBrewingModule.Type3.IDLE) {
            this.method4307(containerMenu, stack -> stack.is(Items.BLAZE_POWDER), 4);
            this.stopWatch.reset();
         } else if (!containerMenu.getSlot(4).getItem().isEmpty()) {
            int n;
            if ((n = this.method4306(containerMenu)) != -1) {
               this.intValue5 = n;
               this.type2 = AutoBrewingModule.Type2.LOAD_BOTTLES;
               this.stopWatch.reset();
            } else {
               this.method4308();
               this.type2 = AutoBrewingModule.Type2.LOAD_INGREDIENT;
            }
         } else if (!this.method4307(containerMenu, stack -> stack.is(Items.BLAZE_POWDER), 4)) {
            ChatLogger.method2021("Не найдено топливо для зельеварки");
            this.method4270();
         } else {
            this.stopWatch.reset();
         }
      }
   }

   private void method4301() {
      BrewingStandMenu containerMenu = this.method4304();
      if (containerMenu == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(50L)) {
         if (this.type3 == AutoBrewingModule.Type3.IDLE && !containerMenu.getCarried().isEmpty()) {
            this.method4309(containerMenu);
            this.stopWatch.reset();
         } else if (this.intValue4 >= this.items3.size()) {
            this.method4296();
         } else {
            Item rawType = this.items3.get(this.intValue4);
            if (this.type3 != AutoBrewingModule.Type3.IDLE) {
               this.method4307(containerMenu, var1x -> var1x.is(rawType), 3);
               this.stopWatch.reset();
            } else if (!containerMenu.getSlot(3).getItem().isEmpty()) {
               this.method4308();
               this.method4296();
            } else if (!this.method4307(containerMenu, var1x -> var1x.is(rawType), 3)) {
               ChatLogger.method2021("Не найден ингредиент: " + rawType.getName(rawType.getDefaultInstance()).getString());
               this.method4270();
            } else {
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method4302() {
      if (this.stopWatch.method7546(20000L)) {
         this.intValue4++;
         if (this.intValue4 >= this.items3.size()) {
            this.type = AutoBrewingModule.Type.COLLECT;
         } else {
            this.type = AutoBrewingModule.Type.INGREDIENT_LOAD;
         }

         this.method4295();
      }
   }

   private void method4303() {
      BrewingStandMenu brewingStandMenu = this.method4304();
      if (brewingStandMenu == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(50L)) {
         if (this.intValue6 >= CONST_intValues.length) {
            this.method4296();
         } else {
            int index = CONST_intValues[this.intValue6];
            if (brewingStandMenu.getSlot(index).getItem().isEmpty()) {
               this.intValue6++;
               this.stopWatch.reset();
            } else {
               InventoryUtils.method905(brewingStandMenu.containerId, index, 0, ContainerInput.QUICK_MOVE, false);
               this.stopWatch.reset();
            }
         }
      }
   }

   private BrewingStandMenu method4304() {
      return mc.player.containerMenu instanceof BrewingStandMenu brewingStandMenu ? brewingStandMenu : null;
   }

   private ChestMenu method4305() {
      return mc.player.containerMenu instanceof ChestMenu chestMenu ? chestMenu : null;
   }

   private int method4306(BrewingStandMenu containerMenu) {
      if (containerMenu.getCarried().isEmpty() && this.type3 == AutoBrewingModule.Type3.IDLE) {
         for (int i = 0; i < CONST_intValues.length; i++) {
            if (!this.method4314(containerMenu.getSlot(CONST_intValues[i]).getItem())) {
               return i;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   private boolean method4307(AbstractContainerMenu containerMenu, AutoBrewingModule.Handler handler, int index) {
      if (this.type3 == AutoBrewingModule.Type3.IDLE) {
         Slot slot = this.method4313(containerMenu, handler);
         if (slot == null) {
            return false;
         } else {
            this.intValue7 = slot.index;
            this.intValue8 = index;
            this.type3 = AutoBrewingModule.Type3.PICK_SOURCE;
            InventoryUtils.method905(containerMenu.containerId, slot.index, 0, ContainerInput.PICKUP, false);
            return true;
         }
      } else if (this.intValue8 != index) {
         this.method4308();
         return true;
      } else if (this.type3 == AutoBrewingModule.Type3.PICK_SOURCE) {
         if (containerMenu.getCarried().isEmpty()) {
            this.method4308();
            return true;
         } else {
            InventoryUtils.method905(containerMenu.containerId, this.intValue8, 1, ContainerInput.PICKUP, false);
            this.type3 = AutoBrewingModule.Type3.PLACE_ONE;
            return true;
         }
      } else if (this.type3 == AutoBrewingModule.Type3.PLACE_ONE) {
         if (containerMenu.getCarried().isEmpty()) {
            this.method4308();
            return true;
         } else {
            this.method4309(containerMenu);
            this.type3 = AutoBrewingModule.Type3.RETURN_REMAINDER;
            return true;
         }
      } else {
         if (containerMenu.getCarried().isEmpty()) {
            this.method4308();
         } else {
            this.method4309(containerMenu);
         }

         return true;
      }
   }

   private void method4308() {
      this.intValue7 = -1;
      this.intValue8 = -1;
      this.type3 = AutoBrewingModule.Type3.IDLE;
   }

   private void method4309(AbstractContainerMenu containerMenu) {
      if (containerMenu.getCarried().isEmpty()) {
         this.method4308();
      } else {
         Slot slot = this.method4310(containerMenu, this.intValue7);
         if (this.method4311(slot, containerMenu.getCarried())) {
            InventoryUtils.method905(containerMenu.containerId, slot.index, 0, ContainerInput.PICKUP, false);
         } else {
            slot = this.method4313(containerMenu, ItemStack::isEmpty);
            if (slot != null) {
               InventoryUtils.method905(containerMenu.containerId, slot.index, 0, ContainerInput.PICKUP, false);
            } else {
               ChatLogger.method2021("Нет свободного слота, чтобы вернуть предмет с курсора");
            }
         }
      }
   }

   private Slot method4310(AbstractContainerMenu containerMenu, int n) {
      return containerMenu.slots.stream().filter(var1x -> var1x.index == n).findFirst().orElse(null);
   }

   private boolean method4311(Slot slot, ItemStack carried) {
      return slot != null
         && slot.container instanceof Inventory
         && (slot.getItem().isEmpty() || this.method4312(slot.getItem(), carried) && slot.getItem().getCount() < slot.getItem().getMaxStackSize());
   }

   private boolean method4312(ItemStack stack, ItemStack carried) {
      return ItemStack.isSameItemSameComponents(stack, carried);
   }

   private Slot method4313(AbstractContainerMenu containerMenu, AutoBrewingModule.Handler handler) {
      return containerMenu.slots
         .stream()
         .filter(slot -> slot.container instanceof Inventory)
         .filter(var1x -> handler.matches(var1x.getItem()))
         .findFirst()
         .orElse(null);
   }

   private boolean method4314(ItemStack stack) {
      if (!stack.is(Items.POTION)) {
         return false;
      } else {
         PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
         return potionContents != null && potionContents.potion().isPresent() && potionContents.potion().get().equals(Potions.WATER);
      }
   }

   private boolean method4315(ItemStack stack) {
      if (!stack.is(Items.POTION)) {
         return false;
      } else {
         PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
         return potionContents != null && potionContents.potion().isPresent() && !potionContents.potion().get().equals(Potions.WATER);
      }
   }

   private int method4316() {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack stack = mc.player.getInventory().getItem(i);
         if (this.method4314(stack)) {
            n += stack.getCount();
         }
      }

      return n;
   }

   private int method4317(Item key) {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         if (mc.player.getInventory().getItem(i).is(key)) {
            n += mc.player.getInventory().getItem(i).getCount();
         }
      }

      return n;
   }

   private boolean method4318(BlockPos blockPos2) {
      Rotation rotation = Zenith.getInstance().getRotationManager().getEffectiveRotation();
      BlockHitResult blockHitResult = TargetUtils.method3216(
         mc.player.getEyePosition(1.0F), rotation, mc.player.blockInteractionRange(), var1x -> var1x != null && var1x.getBlockPos().equals(blockPos2)
      );
      return blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS && blockHitResult.getBlockPos().equals(blockPos2);
   }

   private BlockHitResult method4319(BlockPos immutable) {
      BlockState blockState = mc.level.getBlockState(immutable);
      if (!blockState.is(Blocks.BREWING_STAND) && !this.method4320(blockState)) {
         return null;
      } else {
         Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(immutable);
         ArrayList<Vec3> vecs = new ArrayList();
         vecs.add(atCenterOf);

         for (Direction direction : Direction.values()) {
            vecs.add(atCenterOf.add(Vec3.atLowerCornerOf(direction.getUnitVec3i()).scale(0.5)));
         }

         Vec3 eyePosition = mc.player.getEyePosition(1.0F);
         double blockInteractionRange = mc.player.blockInteractionRange();

         for (Vec3 location : vecs) {
            Rotation rotation = RotationUtils.method6913(location);
            BlockHitResult blockHitResult = TargetUtils.method3216(eyePosition, rotation, blockInteractionRange, var1x -> var1x != null && var1x.getBlockPos().equals(immutable));
            if (blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS && blockHitResult.getBlockPos().equals(immutable)) {
               return blockHitResult;
            }
         }

         return null;
      }
   }

   private boolean method4320(BlockState blockState) {
      return blockState.is(Blocks.CHEST) || blockState.is(Blocks.TRAPPED_CHEST) || blockState.is(Blocks.BARREL);
   }

   @FunctionalInterface
   private interface Handler {
      boolean matches(ItemStack stack);
   }

   private static enum Type {
      SETUP,
      INGREDIENT_LOAD,
      INGREDIENT_WAIT,
      COLLECT;
   }

   private static enum Type2 {
      IDLE,
      SCAN_CHESTS,
      OPEN_CHEST,
      LOOT_CHEST,
      SCAN_OUTPUT_CHESTS,
      OPEN_OUTPUT_CHEST,
      STORE_RESULTS,
      SCAN_STANDS,
      OPEN_STAND,
      LOAD_BOTTLES,
      LOAD_FUEL,
      LOAD_INGREDIENT,
      WAIT_PHASE_DELAY,
      TAKE_RESULT;
   }

   private static enum Type3 {
      IDLE,
      PICK_SOURCE,
      PLACE_ONE,
      RETURN_REMAINDER;
   }
}

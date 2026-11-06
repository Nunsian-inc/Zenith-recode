package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.BlockPosReference;
import org.zenith.utility.game.CraftBindingController;
import org.zenith.utility.game.CraftPresetSaver;
import org.zenith.utility.game.CraftRecipe;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.AutoCraftState;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "AutoCraft",
   category = ModuleCategory.MISC,
   description = "module.autocraft.description"
)
public final class AutoCraftModule extends Module {
   public static final AutoCraftModule INSTANCE = new AutoCraftModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 36;
   private static final String text = "servers";
   private static final long CONST_longValue = 85L;
   private static final long CONST_longValue2 = 600L;
   private static final float CONST_floatValue = 0.5F;
   private static final int CONST_intValue3 = 30;
   private static final int CONST_intValue4 = 5;
   private static final int CONST_intValue5 = 5;
   private static final Map<Item, String> items = new ConcurrentHashMap<>();
   private static final Pattern pattern = Pattern.compile("[^a-z0-9_]+");
   private final ButtonSetting openEditor = new ButtonSetting("module.autoCraft.openEditor", this::method1011);
   private final BooleanSetting autoTake = new BooleanSetting("module.autoCraft.autoTake", "module.autoCraft.autoTake.desc", true);
   private final BooleanSetting autoStore = new BooleanSetting("module.autoCraft.autoStore", "module.autoCraft.autoStore.desc", true);
   private final BooleanSetting batch = new BooleanSetting("module.autoCraft.batch", "module.autoCraft.batch.desc", true);
   private final List<CraftRecipe> items2 = new ArrayList<>();
   private final Map<String, RecipeDisplayEntry> items3 = new HashMap<>();
   private final Map<BlockPos, AutoCraftModule.Entry> items4 = new HashMap<>();
   private final List<BlockPos> items5 = new ArrayList<>();
   private final Map<String, Integer> items6 = new HashMap<>();
   private final StopWatch stopWatch = new StopWatch();
   private final CraftBindingController craftBindingController = new CraftBindingController(this);
   private final CraftPresetSaver craftPresetSaver = new CraftPresetSaver(this);
   private final AutoCraftState autoCraftState = new AutoCraftState(this);
   private AutoCraftModule.Type type = AutoCraftModule.Type.IDLE;
   private String text2 = "";
   private String statusText = "";
   private String text3 = "";
   private long longValue;
   private List<RecipeCollection> items7;
   private RegistryAccess dynamicRegistryManager;
   private Language language;
   private boolean flag = true;
   private ClientLevel clientWorld;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4 = -1;
   private int intValue5 = -1;
   private int intValue6;
   private int intValue7;
   private int intValue8 = -1;
   private int intValue9;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private AutoCraftModule.Type2 type2 = AutoCraftModule.Type2.IDLE;
   private ItemStack itemStack = ItemStack.EMPTY;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private BlockPos blockPos4;
   private Rotation rotation;

   private AutoCraftModule() {
      for (Setting setting : new Setting[]{this.autoTake, this.autoStore, this.batch}) {
         setting.setVisible(() -> false);
      }

      this.method1091();
   }

   private void method1011() {
      Zenith.getInstance().getNLMenuScreen().openTool(2);
   }

   @Override
   public void onEnable() {
      this.craftBindingController.reset();
      super.onEnable();
      this.statusText = "";
      this.autoCraftState.reset();
      this.method1078();
      this.method1079();
      this.method1018();
      this.method1022();
      this.flag4 = false;
      this.method1036();
      this.clientWorld = mc.level;
      this.method1081(true);
   }

   @Override
   public void onDisable() {
      if (mc.player != null) {
         AbstractContainerMenu containerMenu = mc.player.containerMenu;
         if (containerMenu instanceof AbstractContainerMenu) {
            this.method1075(containerMenu);
         }
      }

      this.type = AutoCraftModule.Type.IDLE;
      this.text3 = "";
      this.intValue = 0;
      this.craftBindingController.reset();
      this.craftPresetSaver.reset();
      this.statusText = "";
      this.autoCraftState.reset();
      this.method1078();
      this.method1079();
      this.method1018();
      this.method1022();
      this.flag4 = false;
      this.method1058();
      this.clientWorld = null;
      super.onDisable();
   }

   @EventTarget
   public void onTick(TickEvent event) {
      if (!this.craftPresetSaver.method3191()) {
         this.method1015();
      }
   }

   @EventTarget
   public void method1012(PreRotationEvent event) {
      if (this.rotation != null) {
         Rotation rotation = this.rotation;
         rotationManager.requestRotation(
            new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
            5,
            this
         );
      }
   }

   @EventTarget
   public void method1013(MouseButtonEvent event) {
      this.craftBindingController.method7228(event);
   }

   @EventTarget
   public void method1014(DrawEvent event) {
      this.method1080(event);
   }

   public void method1015() {
      if (mc.level != this.clientWorld) {
         this.clientWorld = mc.level;
         this.method1036();
      }

      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         if (!this.craftBindingController.isBinding()) {
            this.method1081(false);
            this.method1091();
            CraftRecipe craftRecipe = this.method1096();
            if (craftRecipe == null) {
               this.method1076("В списке нет серверных рецептов");
            } else {
               AbstractContainerMenu containerMenu = mc.player.containerMenu;
               if (containerMenu instanceof CraftingMenu craftingMenu) {
                  this.method1016(craftRecipe, craftingMenu);
               } else if (this.method1019(containerMenu)) {
                  if (this.type != AutoCraftModule.Type.OPENING_OUTPUT && !containerMenu.getCarried().isEmpty() && this.stopWatch.method7546(85L)) {
                     this.method1075(containerMenu);
                     this.stopWatch.reset();
                  } else {
                     this.method1023(craftRecipe, containerMenu);
                  }
               } else if (this.intValue > 0 && this.method1059()) {
                  this.intValue--;

                  this.statusText = switch (this.type) {
                     case OPENING_ANALYZE -> "Анализирую сундуки...";
                     case OPENING_WORKBENCH -> "Открываю верстак...";
                     default -> "Открываю хранилище...";
                  };
               } else if (this.method1059()) {
                  this.method1057();
               } else if (mc.gui.screen() == null) {
                  if (this.flag2 && this.type != AutoCraftModule.Type.SCANNING) {
                     this.method1035(craftRecipe);
                  } else {
                     this.method1037(craftRecipe);
                  }
               }
            }
         }
      }
   }

   private void method1016(CraftRecipe craftRecipe, CraftingMenu containerMenu) {
      this.intValue = 0;
      this.type = AutoCraftModule.Type.CRAFTING;
      this.statusText = "Крафчу " + craftRecipe.method5400(this);
      if (this.stopWatch.method7546(85L)) {
         if (!containerMenu.getCarried().isEmpty()) {
            this.method1075(containerMenu);
            this.stopWatch.reset();
         } else if (!containerMenu.getSlot(0).getItem().isEmpty()) {
            if (!this.method1048(craftRecipe)) {
               this.flag4 = true;
               this.method1077("Инвентарь заполнен, складываю результат", true);
            } else {
               mc.gameMode.handleContainerInput(containerMenu.containerId, 0, 0, ContainerInput.QUICK_MOVE, mc.player);
               this.flag4 = true;
               this.method1018();
               this.stopWatch.reset();
            }
         } else if (this.flag4 && this.method1046(craftRecipe)) {
            this.method1077("Складываю результат крафта", true);
         } else if (this.intValue2 > 0) {
            this.intValue2--;
            if (this.intValue2 == 0) {
               String statusText = this.flag4 ? "Складываю результат крафта" : "Не хватает ресурсов или сервер отклонил рецепт";
               this.method1077(statusText, false);
            }
         } else {
            RecipeDisplayEntry recipeDisplayEntry = this.items3.get(craftRecipe.getId());
            if (recipeDisplayEntry == null) {
               this.method1076("Рецепт больше не доступен в книге");
            } else {
               this.method1017(containerMenu, recipeDisplayEntry);
            }
         }
      }
   }

   private void method1017(CraftingMenu containerMenu, RecipeDisplayEntry recipeDisplayEntry) {
      if (mc.getConnection() == null) {
         this.method1076("Нет сетевого подключения к серверу");
      } else {
         mc.getConnection().send(new ServerboundPlaceRecipePacket(containerMenu.containerId, recipeDisplayEntry.id(), this.method1045()));
         this.intValue2 = 5;
         this.statusText = "Запрашиваю рецепт из книги...";
         this.stopWatch.reset();
      }
   }

   private void method1018() {
      this.intValue2 = 0;
   }

   private boolean method1019(AbstractContainerMenu containerMenu) {
      return containerMenu instanceof ChestMenu || containerMenu instanceof ShulkerBoxMenu;
   }

   private int method1020(AbstractContainerMenu containerMenu) {
      return Math.max(0, containerMenu.slots.size() - 36);
   }

   private boolean method1021(AbstractContainerMenu containerMenu) {
      if (this.intValue4 != containerMenu.containerId) {
         this.intValue4 = containerMenu.containerId;
         this.intValue3 = 5;
      }

      if (this.intValue3 <= 0) {
         return false;
      } else {
         this.intValue3--;

         this.statusText = switch (this.type) {
            case OPENING_ANALYZE -> "Считываю содержимое сундука...";
            case OPENING_SOURCE -> "Синхронизирую сундук с ресурсами...";
            case OPENING_OUTPUT -> "Синхронизирую сундук-склад...";
            default -> "Синхронизирую контейнер...";
         };
         this.stopWatch.reset();
         return true;
      }
   }

   private void method1022() {
      this.intValue4 = -1;
      this.intValue3 = 0;
   }

   private void method1023(CraftRecipe craftRecipe, AbstractContainerMenu containerMenu) {
      if (!this.method1021(containerMenu)) {
         this.intValue = 0;
         if (this.type == AutoCraftModule.Type.OPENING_ANALYZE) {
            this.method1040(containerMenu, this.blockPos2);
            mc.player.closeContainer();
            this.method1022();
            this.blockPos2 = null;
            this.type = AutoCraftModule.Type.SCANNING;
            this.stopWatch.reset();
         } else if (this.type == AutoCraftModule.Type.OPENING_SOURCE) {
            if (!this.text3.isBlank() && this.blockPos3 != null) {
               this.method1024(craftRecipe, containerMenu, this.text3, this.blockPos3);
            } else {
               this.method1077(this.statusText, true);
            }
         } else {
            this.autoCraftState.method744();
            if (this.type == AutoCraftModule.Type.OPENING_OUTPUT) {
               if (this.blockPos4 != null) {
                  this.method1040(containerMenu, this.blockPos4);
               }

               this.method1026(craftRecipe, containerMenu);
            } else {
               mc.player.closeContainer();
               this.method1022();
               this.type = AutoCraftModule.Type.IDLE;
            }
         }
      }
   }

   private void method1024(CraftRecipe craftRecipe, AbstractContainerMenu containerMenu, String key, BlockPos blockPos3) {
      String str = craftRecipe.method5395(key);
      String anotherString = craftRecipe.method5396(key);
      this.statusText = "Забираю " + this.method1107(str, anotherString);
      if (this.stopWatch.method7546(85L)) {
         if (!this.autoCraftState.method746(craftRecipe, key)) {
            int n = this.items6.getOrDefault(key, craftRecipe.method5389(key));
            int n2 = this.method1067(str, anotherString);
            if (n2 < n && this.method1049(str, anotherString)) {
               int n3 = this.method1020(containerMenu);
               int n4 = Math.max(1, n - n2);
               int slotNum = this.method1025(containerMenu, n3, str, anotherString, n4);
               if (slotNum != -1) {
                  mc.gameMode.handleContainerInput(containerMenu.containerId, slotNum, 0, ContainerInput.QUICK_MOVE, mc.player);
                  this.autoCraftState.method750(craftRecipe, key);
                  this.stopWatch.reset();
               } else {
                  int n5 = this.autoCraftState.method749(craftRecipe, key);
                  this.method1040(containerMenu, blockPos3);
                  this.autoCraftState.method751(craftRecipe, key, str, anotherString, n5 <= this.autoCraftState.method745());
                  this.method1077("Источник пуст: " + this.method1107(str, anotherString), true);
               }
            } else {
               this.method1040(containerMenu, blockPos3);
               this.autoCraftState.method750(craftRecipe, key);
               this.method1077(this.statusText, true);
            }
         }
      }
   }

   private int method1025(AbstractContainerMenu containerMenu, int n, String str, String anotherString, int n2) {
      int n3 = -1;
      int count = -1;
      int n4 = -1;
      int count2 = Integer.MAX_VALUE;
      int n5 = -1;
      int count3 = -1;

      for (int i = 0; i < n; i++) {
         Slot slot = containerMenu.getSlot(i);
         if (slot.hasItem() && this.method1073(slot.getItem(), str, anotherString)) {
            int count4 = slot.getItem().getCount();
            if (count4 == n2) {
               return i;
            }

            if (count4 < n2 && count4 > count3) {
               count3 = count4;
               n5 = i;
            }

            if (count4 <= n2 && count4 > count) {
               count = count4;
               n3 = i;
            }

            if (count4 > n2 && count4 < count2) {
               count2 = count4;
               n4 = i;
            }
         }
      }

      if (n3 != -1) {
         return n3;
      } else {
         return n4 != -1 ? n4 : n5;
      }
   }

   private void method1026(CraftRecipe craftRecipe, AbstractContainerMenu containerMenu) {
      this.statusText = "Складываю " + craftRecipe.method5400(this);
      int n = this.method1020(containerMenu);
      if (this.type2 != AutoCraftModule.Type2.IDLE) {
         this.method1028(containerMenu, n);
      } else if (!containerMenu.getCarried().isEmpty()) {
         this.type2 = AutoCraftModule.Type2.DEPOSIT_CURSOR;
         this.method1028(containerMenu, n);
      } else if (this.stopWatch.method7546(85L)) {
         int n2 = this.method1067(craftRecipe.getText4(), craftRecipe.getText5());
         if (n2 <= 0 && this.method1066() <= 1) {
            n2 = this.method1068(craftRecipe);
         }

         if (n2 <= 0) {
            this.method1034();
         } else {
            if (this.intValue5 == -1) {
               this.intValue5 = n2;
               this.intValue6 = 0;
            } else if (n2 < this.intValue5) {
               this.intValue6 = 0;
               this.intValue5 = n2;
            } else if (n2 == this.intValue5) {
               this.intValue6++;
            } else {
               this.intValue6 = 0;
               this.intValue5 = n2;
            }

            if (this.method1069(craftRecipe, containerMenu, n) && this.intValue6 < 3) {
               for (int i = n; i < containerMenu.slots.size(); i++) {
                  Slot slot = containerMenu.getSlot(i);
                  if (slot.hasItem() && craftRecipe.method5399(slot.getItem(), this)) {
                     this.method1027(containerMenu, i);
                     this.stopWatch.reset();
                     return;
                  }
               }

               if (this.method1066() <= 1) {
                  for (int i = n; i < containerMenu.slots.size(); i++) {
                     Slot slot = containerMenu.getSlot(i);
                     if (slot.hasItem() && !this.method1070(craftRecipe, slot.getItem())) {
                        this.method1027(containerMenu, i);
                        this.stopWatch.reset();
                        return;
                     }
                  }
               }

               this.method1034();
            } else {
               this.method1040(containerMenu, this.blockPos4);
               this.method1033();
            }
         }
      }
   }

   private void method1027(AbstractContainerMenu containerMenu, int index) {
      if (!containerMenu.getCarried().isEmpty()) {
         this.type2 = AutoCraftModule.Type2.DEPOSIT_CURSOR;
      } else {
         Slot slot = containerMenu.getSlot(index);
         if (slot.hasItem()) {
            this.itemStack = slot.getItem().copy();
            this.intValue8 = index;
            this.intValue9 = 2;
            this.type2 = AutoCraftModule.Type2.QUICK_MOVE;
            mc.gameMode.handleContainerInput(containerMenu.containerId, index, 0, ContainerInput.PICKUP, mc.player);
         }
      }
   }

   private void method1028(AbstractContainerMenu containerMenu, int n) {
      if (this.type2 == AutoCraftModule.Type2.QUICK_MOVE) {
         int slotNum = this.method1030(containerMenu, n);
         if (slotNum != -1) {
            mc.gameMode.handleContainerInput(containerMenu.containerId, slotNum, 0, ContainerInput.QUICK_MOVE, mc.player);
         }

         this.intValue9--;
         if (this.intValue9 <= 0) {
            this.type2 = AutoCraftModule.Type2.DEPOSIT_CURSOR;
         }
      } else if (this.type2 == AutoCraftModule.Type2.DEPOSIT_CURSOR) {
         if (containerMenu.getCarried().isEmpty()) {
            this.method1029(containerMenu);
         } else {
            int slotNum = this.method1031(containerMenu, n);
            if (slotNum != -1) {
               mc.gameMode.handleContainerInput(containerMenu.containerId, slotNum, 0, ContainerInput.PICKUP, mc.player);
               if (containerMenu.getCarried().isEmpty()) {
                  this.method1029(containerMenu);
               }
            } else {
               this.method1040(containerMenu, this.blockPos4);
               this.method1032(containerMenu);
               if (!containerMenu.getCarried().isEmpty()) {
                  this.statusText = "Нет места под hovered-предмет";
               } else {
                  this.method1033();
               }
            }
         }
      }
   }

   private void method1029(AbstractContainerMenu containerMenu) {
      this.method1040(containerMenu, this.blockPos4);
      this.method1079();
      this.stopWatch.reset();
   }

   private int method1030(AbstractContainerMenu containerMenu, int n) {
      if (this.itemStack.isEmpty()) {
         return -1;
      } else {
         for (int i = n; i < containerMenu.slots.size(); i++) {
            if (i != this.intValue8) {
               Slot slot = containerMenu.getSlot(i);
               if (slot.hasItem() && this.method1074(slot.getItem(), this.itemStack)) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   private int method1031(AbstractContainerMenu containerMenu, int n) {
      ItemStack carried = containerMenu.getCarried();
      if (carried.isEmpty()) {
         return -1;
      } else {
         for (int i = 0; i < n; i++) {
            Slot slot = containerMenu.getSlot(i);
            if (slot.hasItem() && this.method1074(slot.getItem(), carried) && slot.getItem().getCount() < slot.getItem().getMaxStackSize()) {
               return i;
            }
         }

         for (int i = 0; i < n; i++) {
            if (!containerMenu.getSlot(i).hasItem()) {
               return i;
            }
         }

         return -1;
      }
   }

   private void method1032(AbstractContainerMenu containerMenu) {
      if (!containerMenu.getCarried().isEmpty()) {
         if (this.intValue8 >= 0 && this.intValue8 < containerMenu.slots.size()) {
            Slot slot = containerMenu.getSlot(this.intValue8);
            if (!slot.hasItem() || this.method1074(slot.getItem(), containerMenu.getCarried())) {
               mc.gameMode.handleContainerInput(containerMenu.containerId, this.intValue8, 0, ContainerInput.PICKUP, mc.player);
               return;
            }
         }

         int n = this.method1020(containerMenu);

         for (int i = n; i < containerMenu.slots.size(); i++) {
            ItemStack stack = containerMenu.getSlot(i).getItem();
            if (stack.isEmpty() || this.method1074(stack, containerMenu.getCarried()) && stack.getCount() < stack.getMaxStackSize()) {
               mc.gameMode.handleContainerInput(containerMenu.containerId, i, 0, ContainerInput.PICKUP, mc.player);
               return;
            }
         }
      }
   }

   private void method1033() {
      if (mc.player != null && mc.player.containerMenu != mc.player.inventoryMenu) {
         mc.player.closeContainer();
      }

      this.statusText = "Сундук заполнен, ищу другой склад";
      this.type = AutoCraftModule.Type.IDLE;
      this.blockPos4 = null;
      this.flag4 = true;
      this.stopWatch.reset();
      this.method1078();
      this.method1022();
   }

   private void method1034() {
      if (mc.player != null && mc.player.containerMenu != mc.player.inventoryMenu) {
         mc.player.closeContainer();
      }

      this.type = AutoCraftModule.Type.IDLE;
      this.blockPos4 = null;
      this.stopWatch.reset();
      this.method1078();
      this.method1022();
      this.flag4 = false;
   }

   private void method1035(CraftRecipe craftRecipe) {
      this.method1041(craftRecipe);
      this.method1042(craftRecipe);
      boolean flag = this.method1066() <= 1;
      boolean flag2 = this.flag4 && this.method1064(craftRecipe);
      boolean flag3 = flag && this.method1068(craftRecipe) > 0;
      boolean flag4 = this.method1063(craftRecipe);
      if (this.autoStore.isEnabled() && (flag2 || flag4 || flag3)) {
         var optionalPos = this.method1053(craftRecipe);
         if (!optionalPos.isEmpty()) {
            this.blockPos4 = (BlockPos)optionalPos.get();
            craftRecipe.method5407(BlockPosReference.fromBlockPos(this.blockPos4));
            this.statusText = flag ? "Инвентарь полон, складываю..." : "Открываю сундук-склад";
            if (this.method1054(this.blockPos4, AutoCraftModule.Type.OPENING_OUTPUT) == AutoCraftModule.Type3.FAILED) {
               this.items4.remove(this.blockPos4);
               this.blockPos4 = null;
            }

            return;
         }

         if (flag) {
            this.method1126("Автокрафт остановлен: нет сундука со свободным местом.");
            return;
         }

         this.flag4 = false;
         this.statusText = "Нет свободного склада, продолжаю крафт";
      }

      String key = this.method1065(craftRecipe);
      if (this.intValue7 <= 0 && !key.isBlank() && !this.method1044(craftRecipe, 1)) {
         this.method1076("Нет места под ресурсы");
      } else if (!key.isBlank()) {
         if (this.autoTake.isEnabled()) {
            if (!this.method1049(craftRecipe.method5395(key), craftRecipe.method5396(key))) {
               this.method1076("Нет места под ресурсы");
            } else {
               var optionalPos = this.method1052(craftRecipe, key);
               if (optionalPos.isPresent()) {
                  this.blockPos3 = (BlockPos)optionalPos.get();
                  craftRecipe.getItems3().put(key, BlockPosReference.fromBlockPos(this.blockPos3));
                  this.statusText = "Открываю сундук с ресурсами";
                  this.text3 = key;
                  if (this.method1054(this.blockPos3, AutoCraftModule.Type.OPENING_SOURCE) == AutoCraftModule.Type3.FAILED) {
                     this.items4.remove(this.blockPos3);
                     this.blockPos3 = null;
                     this.text3 = "";
                  }
               } else {
                  this.method1076("Не найден ресурс: " + this.method1108(key));
               }
            }
         } else {
            this.method1076("Не хватает ингредиентов");
         }
      } else if (this.blockPos != null) {
         craftRecipe.method5408(BlockPosReference.fromBlockPos(this.blockPos));
         this.statusText = "Открываю верстак";
         if (this.method1054(this.blockPos, AutoCraftModule.Type.OPENING_WORKBENCH) == AutoCraftModule.Type3.FAILED) {
            this.blockPos = null;
         }
      } else {
         this.method1076("Рядом нет доступного верстака");
      }
   }

   private void method1036() {
      this.flag2 = false;
      this.flag3 = false;
      this.items5.clear();
      this.items4.clear();
      this.items6.clear();
      this.intValue7 = 0;
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.blockPos4 = null;
      this.blockPos = null;
      this.flag4 = false;
      this.type = AutoCraftModule.Type.SCANNING;
      this.method1078();
      this.method1058();
      this.method1022();
   }

   private void method1037(CraftRecipe craftRecipe) {
      if (!this.flag3) {
         this.method1038();
      }

      if (this.items5.isEmpty()) {
         this.flag2 = true;
         this.type = AutoCraftModule.Type.IDLE;
         this.method1041(craftRecipe);
         this.statusText = this.items4.isEmpty() ? "Сундуки в зоне досягаемости не найдены" : "Анализ сундуков завершен";
      } else {
         this.blockPos2 = this.items5.getFirst();
         this.statusText = "Анализирую сундук " + this.method1062(this.blockPos2);
         AutoCraftModule.Type3 type3 = this.method1054(this.blockPos2, AutoCraftModule.Type.OPENING_ANALYZE);
         if (type3 == AutoCraftModule.Type3.OPENED || type3 == AutoCraftModule.Type3.FAILED) {
            this.items5.removeFirst();
            if (type3 == AutoCraftModule.Type3.FAILED) {
               this.blockPos2 = null;
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method1038() {
      this.items4.clear();
      this.items5.clear();
      this.blockPos = null;
      this.flag3 = true;
      double d = this.method1061();
      BlockPos blockPosition = mc.player.blockPosition();
      int x = (int)Math.ceil(d);
      ArrayList arrayList = new ArrayList();
      HashSet hashSet = new HashSet();
      BlockPos.betweenClosedStream(blockPosition.offset(-x, -x, -x), blockPosition.offset(x, x, x))
         .map(BlockPos::immutable)
         .forEach(
            var3x -> {
               BlockState var4x = mc.level.getBlockState(var3x);
               if (var4x.getBlock() == Blocks.CRAFTING_TABLE
                  && this.method1056(var3x) != null
                  && (
                     this.blockPos == null
                        || Vec3.atCenterOf(var3x).distanceToSqr(mc.player.getEyePosition())
                           < Vec3.atCenterOf(this.blockPos).distanceToSqr(mc.player.getEyePosition())
                  )) {
                  this.blockPos = var3x;
               }

               if (this.method1060(var3x) && this.method1056(var3x) != null && hashSet.add(this.method1039(var3x, var4x))) {
                  arrayList.add(var3x);
               }
            }
         );
      arrayList.sort(Comparator.comparingDouble((net.minecraft.core.Vec3i pos) -> Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition())));
      this.items5.addAll(arrayList);
   }

   private BlockPos method1039(BlockPos mutablePos, BlockState blockState) {
      if (blockState.hasProperty(ChestBlock.TYPE) && blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
         BlockPos relative = mutablePos.relative(ChestBlock.getConnectedDirection(blockState));
         return relative.asLong() < mutablePos.asLong() ? relative.immutable() : mutablePos.immutable();
      } else {
         return mutablePos.immutable();
      }
   }

   private void method1040(AbstractContainerMenu containerMenu, BlockPos blockPos4) {
      if (blockPos4 != null) {
         int n = this.method1020(containerMenu);
         AutoCraftModule.Entry entry = new AutoCraftModule.Entry(blockPos4.immutable());

         for (int i = 0; i < n; i++) {
            ItemStack forFirstStack = containerMenu.getSlot(i).getItem();
            if (forFirstStack.isEmpty()) {
               entry.intValue++;
            } else {
               entry.method1132(new AutoCraftModule.Data(i, method1116(forFirstStack.getItem()), this.method1090(forFirstStack), forFirstStack.getCount(), forFirstStack.getMaxStackSize()));
            }
         }

         this.items4.put(blockPos4.immutable(), entry);
      }
   }

   private void method1041(CraftRecipe craftRecipe) {
      if (craftRecipe != null) {
         if (this.blockPos != null) {
            craftRecipe.method5408(BlockPosReference.fromBlockPos(this.blockPos));
         }

         for (String key : craftRecipe.method5388().keySet()) {
            this.method1052(craftRecipe, key).ifPresent(blockPos -> craftRecipe.getItems3().put(key, BlockPosReference.fromBlockPos(blockPos)));
         }

         this.method1053(craftRecipe).ifPresent(var1x -> craftRecipe.method5407(BlockPosReference.fromBlockPos(var1x)));
      }
   }

   private void method1042(CraftRecipe craftRecipe) {
      this.items6.clear();
      this.intValue7 = this.method1043(craftRecipe);
      int n = Math.max(1, this.intValue7);

      for (Map.Entry entry : craftRecipe.method5388().entrySet()) {
         int n2 = (Integer)entry.getValue() * n;
         this.items6.put((String)entry.getKey(), n2);
      }
   }

   private int method1043(CraftRecipe craftRecipe) {
      int n = Integer.MAX_VALUE;

      for (Map.Entry entry : craftRecipe.method5388().entrySet()) {
         String key = (String)entry.getKey();
         int n2 = this.method1067(craftRecipe.method5395(key), craftRecipe.method5396(key)) + this.method1051(craftRecipe.method5395(key), craftRecipe.method5396(key));
         n = Math.min(n, n2 / Math.max(1, (Integer)entry.getValue()));
      }

      if (n != Integer.MAX_VALUE && n > 0) {
         if (!this.batch.isEnabled()) {
            return 1;
         } else {
            int n2 = 1;
            int n3 = n;
            int n4 = 0;

            while (n2 <= n3) {
               int n5 = n2 + (n3 - n2) / 2;
               if (this.method1044(craftRecipe, n5)) {
                  n4 = n5;
                  n2 = n5 + 1;
               } else {
                  n3 = n5 - 1;
               }
            }

            return n4;
         }
      } else {
         return 0;
      }
   }

   private boolean method1044(CraftRecipe craftRecipe, int n) {
      int n2 = 0;
      int n3 = this.method1066();

      for (Map.Entry entry : craftRecipe.method5388().entrySet()) {
         String key = (String)entry.getKey();
         String identifier = craftRecipe.method5395(key);
         String anotherString = craftRecipe.method5396(key);
         int n4 = (Integer)entry.getValue() * n;
         int n5 = this.method1067(identifier, anotherString);
         int n6 = Math.max(0, n4 - n5);
         if (n6 != 0) {
            int n7 = this.method1050(identifier, anotherString);
            int n8 = Math.max(0, n6 - n7);
            if (n8 > 0) {
               int n9 = Math.max(1, this.method1129(identifier));
               n2 += (n8 + n9 - 1) / n9;
               if (n2 > n3) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean method1045() {
      return this.batch.isEnabled();
   }

   private boolean method1046(CraftRecipe craftRecipe) {
      return !this.autoStore.isEnabled() ? false : !this.method1047(craftRecipe) || this.method1063(craftRecipe) || !this.method1048(craftRecipe);
   }

   private boolean method1047(CraftRecipe craftRecipe) {
      for (Map.Entry entry : craftRecipe.method5388().entrySet()) {
         String key = (String)entry.getKey();
         if (this.method1067(craftRecipe.method5395(key), craftRecipe.method5396(key)) < (Integer)entry.getValue()) {
            return false;
         }
      }

      return true;
   }

   private boolean method1048(CraftRecipe craftRecipe) {
      return craftRecipe.getText4().isBlank() ? false : this.method1050(craftRecipe.getText4(), craftRecipe.getText5()) > 0 || this.method1066() > 0;
   }

   private boolean method1049(String str, String anotherString) {
      return this.method1050(str, anotherString) > 0 || this.method1066() > 0;
   }

   private int method1050(String identifier, String anotherString) {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack carried = mc.player.getInventory().getItem(i);
         if (!carried.isEmpty() && this.method1073(carried, identifier, anotherString)) {
            n += Math.max(0, carried.getMaxStackSize() - carried.getCount());
         }
      }

      return n;
   }

   private int method1051(String str, String anotherString) {
      int n = 0;

      for (AutoCraftModule.Entry entry : this.items4.values()) {
         n += entry.method1133(str, anotherString);
      }

      return n;
   }

   private Optional<BlockPos> method1052(CraftRecipe craftRecipe, String key) {
      String str = craftRecipe.method5395(key);
      String anotherString = craftRecipe.method5396(key);
      return this.items4
         .values()
         .stream()
         .filter(var2x -> var2x.method1133(str, anotherString) > 0)
         .min(
            Comparator.<AutoCraftModule.Entry>comparingInt(var2x -> var2x.method1133(str, anotherString))
               .reversed()
               .thenComparingDouble(entry -> Vec3.atCenterOf(entry.blockPos).distanceToSqr(mc.player.getEyePosition()))
         )
         .map(entry -> entry.blockPos);
   }

   private Optional<BlockPos> method1053(CraftRecipe craftRecipe) {
      String str = craftRecipe.getText4();
      String anotherString = craftRecipe.getText5();
      return this.items4
         .values()
         .stream()
         .filter(var2x -> var2x.method1134(str, anotherString))
         .min(Comparator.comparingDouble(entry -> Vec3.atCenterOf(entry.blockPos).distanceToSqr(mc.player.getEyePosition())))
         .map(entry -> entry.blockPos);
   }

   private AutoCraftModule.Type3 method1054(BlockPos blockPos4, AutoCraftModule.Type type) {
      if (blockPos4 != null && this.stopWatch.method7546(85L)) {
         BlockHitResult blockHitResult = this.method1056(blockPos4);
         if (blockHitResult == null) {
            this.statusText = "Нет луча до блока " + this.method1062(blockPos4);
            this.method1058();
            return AutoCraftModule.Type3.FAILED;
         } else {
            this.rotation = RotationUtils.method6913(blockHitResult.getLocation());
            if (!this.method1055(blockPos4)) {
               this.statusText = "Поворачиваюсь к " + this.method1062(blockPos4);
               return AutoCraftModule.Type3.WAITING;
            } else {
               UseItemUtils.method6514(blockHitResult, InteractionHand.MAIN_HAND);
               this.type = type;
               this.intValue = 30;
               this.stopWatch.reset();
               this.method1022();
               this.method1058();
               return AutoCraftModule.Type3.OPENED;
            }
         }
      } else {
         return AutoCraftModule.Type3.WAITING;
      }
   }

   private boolean method1055(BlockPos blockPos4) {
      Rotation rotation = rotationManager.getEffectiveRotation();
      BlockHitResult blockHitResult = TargetUtils.method3216(
         mc.player.getEyePosition(1.0F), rotation, this.method1061(), var1x -> var1x != null && var1x.getBlockPos().equals(blockPos4)
      );
      return blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS && blockHitResult.getBlockPos().equals(blockPos4);
   }

   private BlockHitResult method1056(BlockPos blockPos4) {
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos4);
      ArrayList<Vec3> vecs = new ArrayList();
      vecs.add(atCenterOf);

      for (Direction direction : Direction.values()) {
         vecs.add(atCenterOf.add(Vec3.atLowerCornerOf(direction.getUnitVec3i()).scale(0.5)));
      }

      Vec3 eyePosition = mc.player.getEyePosition(1.0F);
      double blockInteractionRange = this.method1061();

      for (Vec3 location : vecs) {
         Rotation rotation = RotationUtils.method6913(location);
         BlockHitResult blockHitResult = TargetUtils.method3216(eyePosition, rotation, blockInteractionRange, var1x -> var1x != null && var1x.getBlockPos().equals(blockPos4));
         if (blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS && blockHitResult.getBlockPos().equals(blockPos4)) {
            return blockHitResult;
         }
      }

      return null;
   }

   private void method1057() {
      this.method1058();
      this.method1022();
      if (this.type == AutoCraftModule.Type.OPENING_ANALYZE) {
         this.blockPos2 = null;
         this.type = AutoCraftModule.Type.SCANNING;
      } else if (this.type == AutoCraftModule.Type.OPENING_SOURCE) {
         this.method1076("Не удалось открыть сундук с ресурсами");
      } else if (this.type == AutoCraftModule.Type.OPENING_OUTPUT) {
         this.method1076("Не удалось открыть сундук-склад");
      } else {
         if (this.type == AutoCraftModule.Type.OPENING_WORKBENCH) {
            this.method1076("Не удалось открыть верстак");
         }
      }
   }

   private void method1058() {
      this.rotation = null;
   }

   private boolean method1059() {
      return this.type == AutoCraftModule.Type.OPENING_ANALYZE
         || this.type == AutoCraftModule.Type.OPENING_SOURCE
         || this.type == AutoCraftModule.Type.OPENING_OUTPUT
         || this.type == AutoCraftModule.Type.OPENING_WORKBENCH;
   }

   private boolean method1060(BlockPos mutablePos) {
      BlockEntity blockEntity = mc.level.getBlockEntity(mutablePos);
      return blockEntity instanceof ChestBlockEntity || blockEntity instanceof BarrelBlockEntity || blockEntity instanceof ShulkerBoxBlockEntity;
   }

   private double method1061() {
      return mc.player.blockInteractionRange() + 0.15;
   }

   private String method1062(BlockPos blockPos4) {
      return blockPos4 == null ? "?" : blockPos4.getX() + " " + blockPos4.getY() + " " + blockPos4.getZ();
   }

   private boolean method1063(CraftRecipe craftRecipe) {
      if (craftRecipe.getText4().isBlank()) {
         return false;
      } else {
         int n = 0;

         for (int i = 0; i < 36; i++) {
            ItemStack carried = mc.player.getInventory().getItem(i);
            if (!carried.isEmpty() && craftRecipe.method5399(carried, this)) {
               n++;
            }
         }

         return n / 36.0F >= 0.5F;
      }
   }

   private boolean method1064(CraftRecipe craftRecipe) {
      return !craftRecipe.getText4().isBlank() && this.method1067(craftRecipe.getText4(), craftRecipe.getText5()) > 0;
   }

   private String method1065(CraftRecipe craftRecipe) {
      for (Map.Entry entry : craftRecipe.method5388().entrySet()) {
         String key = (String)entry.getKey();
         int n = this.items6.getOrDefault(key, (Integer)entry.getValue());
         int n2 = this.method1067(craftRecipe.method5395(key), craftRecipe.method5396(key));
         if (n2 < n) {
            return key;
         }
      }

      return "";
   }

   private int method1066() {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         if (mc.player.getInventory().getItem(i).isEmpty()) {
            n++;
         }
      }

      return n;
   }

   private int method1067(String identifier, String anotherString) {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack carried = mc.player.getInventory().getItem(i);
         if (!carried.isEmpty() && this.method1073(carried, identifier, anotherString)) {
            n += carried.getCount();
         }
      }

      return n;
   }

   private int method1068(CraftRecipe craftRecipe) {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack carried = mc.player.getInventory().getItem(i);
         if (!carried.isEmpty() && !this.method1070(craftRecipe, carried)) {
            n += carried.getCount();
         }
      }

      return n;
   }

   private boolean method1069(CraftRecipe craftRecipe, AbstractContainerMenu containerMenu, int n) {
      String identifier = craftRecipe.getText4();
      String anotherString = craftRecipe.getText5();

      for (int i = 0; i < n; i++) {
         Slot slot = containerMenu.getSlot(i);
         if (!slot.hasItem()) {
            return true;
         }

         ItemStack carried = slot.getItem();
         if (this.method1073(carried, identifier, anotherString) && carried.getCount() < carried.getMaxStackSize()) {
            return true;
         }
      }

      return false;
   }

   private boolean method1070(CraftRecipe craftRecipe, ItemStack carried) {
      for (Map.Entry entry : craftRecipe.method5388().entrySet()) {
         String key = (String)entry.getKey();
         if (this.method1073(carried, craftRecipe.method5395(key), craftRecipe.method5396(key))) {
            return true;
         }
      }

      return false;
   }

   private boolean method1071(Item key, String identifier) {
      return Objects.equals(method1072(key), identifier);
   }

   private static String method1072(Item key) {
      return items.computeIfAbsent(key, var0x -> BuiltInRegistries.ITEM.getKey(var0x).toString());
   }

   public boolean method1073(ItemStack carried, String identifier, String anotherString) {
      if (carried == null || carried.isEmpty() || !this.method1071(carried.getItem(), identifier)) {
         return false;
      } else if (anotherString != null && !anotherString.isBlank()) {
         String string = carried.getHoverName().getString();
         return string.equalsIgnoreCase(anotherString) || string.toLowerCase(Locale.ROOT).contains(anotherString.toLowerCase(Locale.ROOT));
      } else {
         return true;
      }
   }

   private boolean method1074(ItemStack stack, ItemStack carried) {
      if (stack != null && carried != null && !stack.isEmpty() && !carried.isEmpty()) {
         ItemStack stack2 = stack.copy();
         ItemStack stack3 = carried.copy();
         stack2.setCount(1);
         stack3.setCount(1);
         return ItemStack.isSameItem(stack2, stack3) && ItemStack.matches(stack2, stack3);
      } else {
         return false;
      }
   }

   private void method1075(AbstractContainerMenu containerMenu) {
      ItemStack carried = containerMenu.getCarried();
      if (!carried.isEmpty()) {
         for (Slot slot : containerMenu.slots) {
            if (slot.container instanceof Inventory && slot.getContainerSlot() < 36 && slot.getItem().isEmpty()) {
               mc.gameMode.handleContainerInput(containerMenu.containerId, slot.index, 0, ContainerInput.PICKUP, mc.player);
               return;
            }
         }

         mc.gameMode.handleContainerInput(containerMenu.containerId, 64537, 0, ContainerInput.PICKUP, mc.player);
      }
   }

   private void method1076(String statusText) {
      this.statusText = statusText;
      this.text3 = "";
      this.type = AutoCraftModule.Type.IDLE;
      this.autoCraftState.method744();
      this.method1018();
      this.method1058();
      this.method1022();
   }

   private void method1077(String statusText, boolean flag) {
      if (mc.player != null && mc.player.containerMenu != mc.player.inventoryMenu) {
         mc.player.closeContainer();
      }

      this.blockPos3 = null;
      this.method1076(statusText);
      if (flag) {
         this.stopWatch.reset();
      }
   }

   private void method1078() {
      this.intValue5 = -1;
      this.intValue6 = 0;
      this.method1079();
   }

   private void method1079() {
      this.type2 = AutoCraftModule.Type2.IDLE;
      this.itemStack = ItemStack.EMPTY;
      this.intValue8 = -1;
      this.intValue9 = 0;
   }

   private void method1080(DrawEvent event) {
      if (this.isEnabled() || this.craftBindingController.isBinding() || this.craftPresetSaver.isActive()) {
         String statusText;
         if (this.craftBindingController.isBinding()) {
            statusText = this.craftBindingController.method7223();
         } else if (this.craftPresetSaver.isActive()) {
            statusText = this.craftPresetSaver.method3189();
         } else {
            statusText = this.statusText;
         }

         if (statusText != null && !statusText.isBlank()) {
            Font font = Fonts.NEW_REGULAR.getFont(8.0F);
            float width = font.width(statusText) + 14.0F;
            float floatValue = mc.getWindow().getGuiScaledWidth() / 2.0F - width / 2.0F;
            float y = mc.getWindow().getGuiScaledHeight() - 54;
            ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
            ZenithColor zenithColor = currentStyle == null ? new ZenithColor(14, 14, 16, 160) : currentStyle.getPanelLeftBackground().getZenithColor().method7293(160);
            event.getCustomDrawContext().drawRoundedRect(floatValue, y, width, 16.0F, ProxyDotRadiusData.method8069(5.0F), zenithColor);
            float centeredTextY = y + (16.0F - font.height()) / 2.0F - 1.0F;
            event.getCustomDrawContext().drawText(font, statusText, floatValue + width / 2.0F - font.width(statusText) / 2.0F, centeredTextY, ZenithColor.INSTANCE);
         }
      }
   }

   private void method1081(boolean flag) {
      if (mc.player != null && mc.level != null) {
         long now = System.currentTimeMillis();
         if (flag || now - this.longValue >= 600L) {
            this.longValue = now;
            ClientRecipeBook recipeBook = mc.player.getRecipeBook();
            List<RecipeCollection> collections = recipeBook.getCollections();
            RegistryAccess registryAccess = mc.level.registryAccess();
            Language language = Language.getInstance();
            if (!flag && !this.flag && collections == this.items7 && registryAccess == this.dynamicRegistryManager && language == this.language) {
               this.method1091();
            } else {
               this.items7 = collections;
               this.dynamicRegistryManager = registryAccess;
               this.language = language;
               this.flag = false;
               ContextMap context = SlotDisplayContext.fromLevel(mc.level);
               HashSet hashSet = new HashSet();
               HashMap map = new HashMap();
               this.items3.clear();

               for (RecipeCollection collection : collections) {
                  for (RecipeDisplayEntry recipe : collection.getRecipes()) {
                     RecipeDisplay display = recipe.display();
                     if (this.method1088(display)) {
                        ItemStack forFirstStack = display.result().resolveForFirstStack(context);
                        if (!forFirstStack.isEmpty() && this.method1089(forFirstStack)) {
                           AutoCraftModule.Data3 data3 = this.method1082(recipe, forFirstStack, context);
                           String text = data3.getText();
                           int n = (Integer)map.merge(text, 1, (a, b) -> (Integer)a + (Integer)b);
                           if (n > 1) {
                              text = text + "_" + n;
                           }

                           CraftRecipe craftRecipe = this.method1095(text);
                           if (craftRecipe == null) {
                              craftRecipe = new CraftRecipe(text, "servers", data3.getText2());
                              this.items2.add(craftRecipe);
                           }

                           this.method1086(craftRecipe, data3);
                           this.items3.put(text, recipe);
                           hashSet.add(text);
                        }
                     }
                  }
               }

               if (!hashSet.isEmpty()) {
                  this.items2.removeIf(var1x -> var1x.isFlag2() && !hashSet.contains(var1x.getId()));
               }

               this.method1091();
            }
         }
      }
   }

   private AutoCraftModule.Data3 method1082(RecipeDisplayEntry recipe, ItemStack forFirstStack, ContextMap context) {
      String str = method1116(forFirstStack.getItem());
      String anotherString = this.method1090(forFirstStack);
      String str2 = this.method1107(str, anotherString);
      String[] texts = new String[9];
      String[] texts2 = new String[9];

      for (int i = 0; i < 9; i++) {
         texts[i] = "";
         texts2[i] = "";
      }

      RecipeDisplay display = recipe.display();
      if (display instanceof ShapedCraftingRecipeDisplay craftingRecipeDisplay) {
         this.method1083(craftingRecipeDisplay, context, texts, texts2);
      } else if (display instanceof ShapelessCraftingRecipeDisplay craftingRecipeDisplay2) {
         this.method1084(craftingRecipeDisplay2, context, texts, texts2);
      }

      String text = this.method1087(str, anotherString, texts, texts2);
      return new AutoCraftModule.Data3(text, str2, str, anotherString, texts, texts2);
   }

   private void method1083(ShapedCraftingRecipeDisplay craftingRecipeDisplay, ContextMap context, String[] texts, String[] texts2) {
      var ingredients = craftingRecipeDisplay.ingredients();
      int n = Math.min(3, craftingRecipeDisplay.width());
      int n2 = Math.min(3, craftingRecipeDisplay.height());

      for (int i = 0; i < n2; i++) {
         for (int j = 0; j < n; j++) {
            int n3 = i * craftingRecipeDisplay.width() + j;
            int n4 = i * 3 + j;
            if (n3 < ingredients.size()) {
               this.method1085((SlotDisplay)ingredients.get(n3), context, texts, texts2, n4);
            }
         }
      }
   }

   private void method1084(ShapelessCraftingRecipeDisplay craftingRecipeDisplay, ContextMap context, String[] texts, String[] texts2) {
      var ingredients = craftingRecipeDisplay.ingredients();

      for (int i = 0; i < Math.min(9, ingredients.size()); i++) {
         this.method1085((SlotDisplay)ingredients.get(i), context, texts, texts2, i);
      }
   }

   private void method1085(SlotDisplay slotDisplay, ContextMap context, String[] texts, String[] texts2, int n) {
      ItemStack forFirstStack = slotDisplay.resolveForFirstStack(context);
      if (!forFirstStack.isEmpty()) {
         texts[n] = method1116(forFirstStack.getItem());
         texts2[n] = this.method1090(forFirstStack);
      }
   }

   private void method1086(CraftRecipe craftRecipe, AutoCraftModule.Data3 data3) {
      craftRecipe.setFlag2(true);
      craftRecipe.setLocked(true);
      craftRecipe.method5402(data3.getText2());
      craftRecipe.method5403(data3.getText3());
      craftRecipe.method5404(data3.getText4());
      craftRecipe.method5405(data3.getText3());
      craftRecipe.setFlag(false);

      for (int i = 0; i < 9; i++) {
         craftRecipe.method5390(i, data3.getTexts()[i]);
         craftRecipe.method5392(i, data3.getTexts2()[i]);
      }
   }

   private String method1087(String str, String anotherString, String[] texts, String[] texts2) {
      StringBuilder builder = new StringBuilder("servers").append('|').append(str).append('|').append(anotherString);

      for (int i = 0; i < 9; i++) {
         builder.append('|').append(texts[i]).append('#').append(texts2[i]);
      }

      String str2 = str;
      int indexOf = str.indexOf(58);
      if (indexOf != -1 && indexOf + 1 < str.length()) {
         str2 = str.substring(indexOf + 1);
      }

      String input = (anotherString != null && !anotherString.isBlank() ? anotherString : str2).toLowerCase(Locale.ROOT).replace(" ", "_");
      input = pattern.matcher(input).replaceAll("_");
      if (input.isBlank()) {
         input = "recipe";
      }

      return "servers_" + input + "_" + Integer.toHexString(builder.toString().hashCode());
   }

   private boolean method1088(RecipeDisplay display) {
      return display instanceof ShapedCraftingRecipeDisplay || display instanceof ShapelessCraftingRecipeDisplay;
   }

   private boolean method1089(ItemStack forFirstStack) {
      if (forFirstStack.isEmpty()) {
         return false;
      } else {
         Identifier key = BuiltInRegistries.ITEM.getKey(forFirstStack.getItem());
         if (!"minecraft".equals(key.getNamespace())) {
            return true;
         } else {
            String string = forFirstStack.getItem().getName(forFirstStack.getItem().getDefaultInstance()).getString();
            String str = forFirstStack.getHoverName().getString();
            return !str.equals(string)
               || forFirstStack.has(DataComponents.CUSTOM_NAME)
               || forFirstStack.has(DataComponents.LORE)
               || forFirstStack.has(DataComponents.CUSTOM_DATA)
               || forFirstStack.has(DataComponents.CUSTOM_MODEL_DATA)
               || forFirstStack.has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
         }
      }
   }

   private String method1090(ItemStack forFirstStack) {
      if (forFirstStack != null && !forFirstStack.isEmpty()) {
         String string = forFirstStack.getItem().getName(forFirstStack.getItem().getDefaultInstance()).getString();
         String str = forFirstStack.getHoverName().getString();
         return str.equals(string) ? "" : str;
      } else {
         return "";
      }
   }

   public void method1091() {
      if (this.method1095(this.text2) == null) {
         var craftRecipes = this.method1093();
         if (!craftRecipes.isEmpty()) {
            this.text2 = ((CraftRecipe)craftRecipes.getFirst()).getId();
         }
      }
   }

   public List<CraftRecipe> method1092(String text) {
      this.method1081(false);
      return this.method1093();
   }

   private List<CraftRecipe> method1093() {
      return this.items2.stream().filter(craftRecipe -> craftRecipe.getText2().equalsIgnoreCase("servers")).sorted(Comparator.comparing(CraftRecipe::getText3)).toList();
   }

   public CraftRecipe method1094(String text, String str) {
      this.method1081(false);
      return this.method1095(str);
   }

   private CraftRecipe method1095(String text) {
      return text != null && !text.isBlank()
         ? this.items2.stream().filter(var1x -> var1x.getText2().equalsIgnoreCase("servers") && var1x.getId().equalsIgnoreCase(text)).findFirst().orElse(null)
         : null;
   }

   public CraftRecipe method1096() {
      return this.method1094("servers", this.getText2());
   }

   public CraftRecipe method1097() {
      this.method1091();
      return this.method1095(this.getText2());
   }

   public String method1098() {
      return "servers";
   }

   public void method1099(String str) {
      this.autoCraftState.reset();
      this.method1078();
      this.method1018();
   }

   public String getText2() {
      return this.text2;
   }

   public void method1100(String id) {
      String anObject = id == null ? "" : id;
      if (!this.text2.equals(anObject)) {
         this.text2 = anObject;
         this.autoCraftState.reset();
         this.method1078();
         this.method1018();
         this.method1036();
      }
   }

   public CraftRecipe method1101(String str) {
      this.method1128("Ручные пресеты отключены: рецепты берутся из книги");
      return null;
   }

   public boolean method1102(CraftRecipe craftRecipe) {
      return false;
   }

   public boolean method1103(String str) {
      return false;
   }

   public void method1104(CraftRecipe craftRecipe) {
      if (craftRecipe != null) {
         this.items2.add(craftRecipe);
         this.flag = true;
      }
   }

   public void method1105(String str) {
      this.craftBindingController.method7225(str);
   }

   public void method1106() {
      this.autoCraftState.reset();
   }

   public String method1107(String str, String anotherString) {
      return anotherString != null && !anotherString.isBlank() ? anotherString : this.method1118(str);
   }

   public String method1108(String key) {
      int indexOf = key.indexOf(35);
      return indexOf == -1 ? this.method1118(key) : this.method1107(key.substring(0, indexOf), key.substring(indexOf + 1));
   }

   public void method1109() {
      this.craftBindingController.method7226();
   }

   public void method1110() {
      this.craftBindingController.method7227();
   }

   public void method1111() {
      this.method1128("Ручное добавление отключено: используйте список рецептов");
   }

   public void method1112() {
      this.craftBindingController.method7224();
   }

   public void method1113() {
      this.craftBindingController.method7229();
   }

   public void method1114() {
      this.craftBindingController.method7230();
   }

   public void method1115(String text, String str) {
      this.text2 = str == null ? "" : str;
   }

   private static String method1116(Item key) {
      return key != null && key != Items.AIR ? method1072(key) : "";
   }

   public Item method1117(String identifier) {
      if (identifier != null && !identifier.isBlank()) {
         Identifier tryParse = Identifier.tryParse(identifier);
         return tryParse != null && BuiltInRegistries.ITEM.containsKey(tryParse) ? BuiltInRegistries.ITEM.getValue(tryParse) : Items.AIR;
      } else {
         return Items.AIR;
      }
   }

   public String method1118(String str) {
      Item item = this.method1117(str);
      return item == Items.AIR ? "Пусто" : item.getName(item.getDefaultInstance()).getString();
   }

   public BooleanSetting getAutoTake() {
      return this.autoTake;
   }

   public BooleanSetting getAutoStore() {
      return this.autoStore;
   }

   public BooleanSetting getBatch() {
      return this.batch;
   }

   public String method1119() {
      return this.statusText == null ? "" : this.statusText;
   }

   public boolean isBinding() {
      return this.craftBindingController.isBinding();
   }

   public AutoCraftModule.Data2 method1120() {
      if (mc.player != null && mc.level != null) {
         double d = this.method1061();
         double d2 = d * d;
         Vec3 eyePosition = mc.player.getEyePosition();
         BlockPos blockPosition = mc.player.blockPosition();
         int n = (int)Math.ceil(d);
         MutableBlockPos mutablePos = new MutableBlockPos();
         HashSet hashSet = new HashSet();
         BlockPos immutable = null;
         double d3 = Double.MAX_VALUE;

         for (int i = blockPosition.getX() - n; i <= blockPosition.getX() + n; i++) {
            for (int j = blockPosition.getY() - n; j <= blockPosition.getY() + n; j++) {
               for (int k = blockPosition.getZ() - n; k <= blockPosition.getZ() + n; k++) {
                  double d4 = i + 0.5 - eyePosition.x;
                  double d5 = j + 0.5 - eyePosition.y;
                  double d6 = k + 0.5 - eyePosition.z;
                  double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                  if (!(d7 > d2)) {
                     mutablePos.set(i, j, k);
                     BlockState blockState = mc.level.getBlockState(mutablePos);
                     if (blockState.getBlock() == Blocks.CRAFTING_TABLE) {
                        if (d7 < d3) {
                           d3 = d7;
                           immutable = mutablePos.immutable();
                        }
                     } else if (blockState.hasBlockEntity() && this.method1060(mutablePos)) {
                        hashSet.add(this.method1039(mutablePos, blockState));
                     }
                  }
               }
            }
         }

         return new AutoCraftModule.Data2(immutable, hashSet.size());
      } else {
         return new AutoCraftModule.Data2(null, 0);
      }
   }

   public int method1121(String identifier, String anotherString) {
      return mc.player != null && identifier != null && !identifier.isBlank() ? this.method1067(identifier, anotherString) : 0;
   }

   public Map<String, Integer> method1122() {
      HashMap map = new HashMap();
      if (mc.player == null) {
         return map;
      } else {
         for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack != null && !stack.isEmpty()) {
               map.merge(method1116(stack.getItem()), stack.getCount(), (a, b) -> (Integer)a + (Integer)b);
            }
         }

         return map;
      }
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      json.add("AutoCraftData", this.method1123());
      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json == null) {
         this.method1091();
      } else {
         JsonObject jsonObject = null;
         if (json.has("AutoCraftData") && json.get("AutoCraftData").isJsonObject()) {
            jsonObject = json.getAsJsonObject("AutoCraftData");
         } else if (json.has("presets") || json.has("selectedProfile")) {
            jsonObject = json;
         }

         if (jsonObject != null) {
            this.method1124(jsonObject);
         } else {
            this.method1091();
         }
      }
   }

   public JsonObject method1123() {
      JsonObject json = new JsonObject();
      json.addProperty("selectedProfile", "servers");
      json.addProperty("selectedServersPreset", this.text2);
      json.addProperty("batch", this.batch.isEnabled());
      JsonArray jsonArray = new JsonArray();

      for (CraftRecipe craftRecipe : new ArrayList<>(this.items2)) {
         if (this.method1125(craftRecipe)) {
            JsonObject json2 = new JsonObject();
            json2.addProperty("id", craftRecipe.getId());
            json2.addProperty("profile", "servers");
            JsonObject json3 = new JsonObject();

            for (Map.Entry entry : craftRecipe.getItems3().entrySet()) {
               json3.add((String)entry.getKey(), ((BlockPosReference)entry.getValue()).toJson());
            }

            json2.add("sources", json3);
            json2.add("output", craftRecipe.getClass1().toJson());
            json2.add("workbench", craftRecipe.getClass12().toJson());
            jsonArray.add(json2);
         }
      }

      json.add("presets", jsonArray);
      return json;
   }

   public void method1124(JsonObject jsonObject) {
      this.flag = true;
      if (jsonObject.has("selectedServersPreset")) {
         this.text2 = jsonObject.get("selectedServersPreset").getAsString();
      } else if (jsonObject.has("selectedFunTimePreset")) {
         this.text2 = jsonObject.get("selectedFunTimePreset").getAsString();
      }

      if (jsonObject.has("batch")) {
         this.batch.setEnabled(jsonObject.get("batch").getAsBoolean());
      }

      if (jsonObject.has("presets") && jsonObject.get("presets").isJsonArray()) {
         for (JsonElement json : jsonObject.getAsJsonArray("presets")) {
            if (json.isJsonObject()) {
               JsonObject jsonObject2 = json.getAsJsonObject();
               String string = jsonObject2.has("id") ? jsonObject2.get("id").getAsString() : "";
               if (!string.isBlank()) {
                  CraftRecipe craftRecipe = this.method1095(string);
                  if (craftRecipe == null) {
                     craftRecipe = new CraftRecipe(string, "servers", string);
                     craftRecipe.setFlag2(true);
                     craftRecipe.setLocked(true);
                     this.items2.add(craftRecipe);
                  }

                  if (jsonObject2.has("sources") && jsonObject2.get("sources").isJsonObject()) {
                     JsonObject jsonObject3 = jsonObject2.getAsJsonObject("sources");
                     craftRecipe.getItems3().clear();

                     for (Map.Entry entry : jsonObject3.entrySet()) {
                        craftRecipe.getItems3().put((String)entry.getKey(), BlockPosReference.fromJson(((JsonElement)entry.getValue()).getAsJsonObject()));
                     }
                  }

                  if (jsonObject2.has("output") && jsonObject2.get("output").isJsonObject()) {
                     craftRecipe.method5407(BlockPosReference.fromJson(jsonObject2.getAsJsonObject("output")));
                  }

                  if (jsonObject2.has("workbench") && jsonObject2.get("workbench").isJsonObject()) {
                     craftRecipe.method5408(BlockPosReference.fromJson(jsonObject2.getAsJsonObject("workbench")));
                  }
               }
            }
         }
      }

      this.method1091();
   }

   private boolean method1125(CraftRecipe craftRecipe) {
      return craftRecipe != null && (!craftRecipe.getItems3().isEmpty() || craftRecipe.getClass1().isPresent() || craftRecipe.getClass12().isPresent());
   }

   private void method1126(String statusText) {
      this.statusText = statusText;
      this.method1128(statusText);
      if (mc.player != null && mc.player.containerMenu != mc.player.inventoryMenu) {
         mc.player.closeContainer();
      }

      if (this.isEnabledRaw()) {
         this.setToggled(false);
      }
   }

   public void method1127(String text) {
      Zenith.getInstance().getHudHolder().method6972("S", Component.literal(text));
   }

   public void method1128(String statusText) {
      Zenith.getInstance().getHudHolder().method6972("!", Component.literal(statusText));
   }

   private int method1129(String identifier) {
      Item item = this.method1117(identifier);
      return item == Items.AIR ? 64 : item.getDefaultMaxStackSize();
   }

   private boolean method1130(AutoCraftModule.Data data, String str, String anotherString) {
      if (!Objects.equals(data.getText(), str)) {
         return false;
      } else if (anotherString != null && !anotherString.isBlank()) {
         String str2 = data.getText2();
         return str2.equalsIgnoreCase(anotherString) || str2.toLowerCase(Locale.ROOT).contains(anotherString.toLowerCase(Locale.ROOT));
      } else {
         return true;
      }
   }

   private static final class Data {
      private final int intValue;
      private final String text;
      private final String text2;
      private final int intValue2;
      private final int intValue3;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, text2, intValue2, intValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + "]";
      }

      private Data(int intValue, String text, String str, int count, int maxStackSize) {
         this.intValue = intValue;
         this.text = text;
         this.text2 = str;
         this.intValue2 = count;
         this.intValue3 = maxStackSize;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public int count() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   public static final class Data2 {
      private final BlockPos blockPos;
      private final int intValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "blockPos=" + this.blockPos + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(BlockPos blockPos, int intValue) {
         this.blockPos = blockPos;
         this.intValue = intValue;
      }

      public boolean method1131() {
         return this.blockPos != null;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data3 {
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String[] texts;
      private final String[] texts2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      public String[] texts() {
         return this.texts;
      }

      public String[] texts2() {
         return this.texts2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.texts, other.texts) && java.util.Objects.equals(this.texts2, other.texts2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, text4, texts, texts2);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "texts=" + this.texts + ", " + "texts2=" + this.texts2 + "]";
      }

      private Data3(String text, String str, String str2, String anotherString, String[] texts, String[] texts2) {
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.text4 = anotherString;
         this.texts = texts;
         this.texts2 = texts2;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public String getText4() {
         return this.text4;
      }

      public String[] getTexts() {
         return this.texts;
      }

      public String[] getTexts2() {
         return this.texts2;
      }
   }

   private final class Entry {
      private final BlockPos blockPos;
      private final List<AutoCraftModule.Data> items = new ArrayList<>();
      private int intValue;

      private Entry(BlockPos blockPos) {
         this.blockPos = blockPos;
      }

      private void method1132(AutoCraftModule.Data data) {
         if (data.count() > 0) {
            this.items.add(data);
         }
      }

      private int method1133(String str, String anotherString) {
         int n = 0;

         for (AutoCraftModule.Data data : this.items) {
            if (AutoCraftModule.this.method1130(data, str, anotherString)) {
               n += data.count();
            }
         }

         return n;
      }

      private boolean method1134(String str, String anotherString) {
         if (this.intValue > 0) {
            return true;
         } else {
            for (AutoCraftModule.Data data : this.items) {
               if (AutoCraftModule.this.method1130(data, str, anotherString) && data.count() < data.getIntValue3()) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private static enum Type {
      IDLE,
      SCANNING,
      OPENING_ANALYZE,
      OPENING_SOURCE,
      OPENING_OUTPUT,
      OPENING_WORKBENCH,
      CRAFTING;
   }

   private static enum Type2 {
      IDLE,
      QUICK_MOVE,
      DEPOSIT_CURSOR;
   }

   private static enum Type3 {
      WAITING,
      OPENED,
      FAILED;
   }
}

package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.SpawnTracker;

@ModuleInfo(
   name = "TpLoot",
   category = ModuleCategory.PLAYER,
   description = "module.tploot.description"
)
public final class TpLootModule extends Module {
   public static final TpLootModule INSTANCE = new TpLootModule();
   private static final int CONST_intValue = 100;
   private static final double CONST_doubleValue = 4.0;
   private static final Set<Item> items = Set.of(
      Items.ROTTEN_FLESH,
      Items.BONE,
      Items.BONE_MEAL,
      Items.STRING,
      Items.ARROW,
      Items.SPECTRAL_ARROW,
      Items.TIPPED_ARROW,
      Items.SPIDER_EYE,
      Items.FERMENTED_SPIDER_EYE,
      Items.LEATHER,
      Items.RABBIT_HIDE,
      Items.FEATHER,
      Items.WHEAT_SEEDS,
      Items.BEETROOT_SEEDS,
      Items.MELON_SEEDS,
      Items.PUMPKIN_SEEDS,
      Items.DIRT,
      Items.COARSE_DIRT,
      Items.ROOTED_DIRT,
      Items.GRASS_BLOCK,
      Items.COBBLESTONE,
      Items.ANDESITE,
      Items.DIORITE,
      Items.GRANITE,
      Items.COBBLED_DEEPSLATE,
      Items.GRAVEL,
      Items.SAND,
      Items.RED_SAND,
      Items.NETHERRACK,
      Items.STICK,
      Items.BOWL,
      Items.SNOWBALL,
      Items.EGG,
      Items.BLUE_EGG,
      Items.BROWN_EGG,
      Items.CACTUS,
      Items.KELP,
      Items.DRIED_KELP,
      Items.POISONOUS_POTATO,
      Items.GLASS_BOTTLE,
      Items.INK_SAC,
      Items.GLOW_INK_SAC,
      Items.SUGAR_CANE
   );
   private final BooleanSetting leaveUnder = new BooleanSetting("module.tpLoot.leaveUnder", "module.tpLoot.leaveUnder.desc", true);
   private ItemEntity itemEntity;
   private ItemStack itemStack = ItemStack.EMPTY;
   private Vec3 vec3d;
   private ClientLevel clientWorld;
   private LocalPlayer player;
   private int intValue;
   private int intValue2;
   private boolean flag;

   private TpLootModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabled()) {
         this.clear();
         if (!SpawnTracker.method6206()) {
            ChatLogger.method2021("TpLoot: включи /fly");
            this.setToggled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      this.clear();
      super.onDisable();
   }

   private void clear() {
      SpawnTracker.method6209(this);
      this.itemEntity = null;
      this.itemStack = ItemStack.EMPTY;
      this.vec3d = null;
      this.flag = false;
      this.intValue = this.intValue2 = 0;
      this.clientWorld = null;
      this.player = null;
   }

   private void method4056() {
      this.setToggled(false);
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.clientWorld != mc.level || this.player != mc.player) {
         boolean flag = this.itemEntity != null;
         this.clear();
         this.clientWorld = mc.level;
         this.player = mc.player;
         if (flag) {
            this.method4056();
            return;
         }
      }

      if (!SpawnTracker.method6206()) {
         this.method4056();
      } else if (mc.gui.screen() == null && mc.gui.overlay() == null && mc.player.containerMenu == mc.player.inventoryMenu) {
         if (SpawnTracker.isActive()) {
            if (this.itemEntity != null && !SpawnTracker.method6207(this)) {
               this.method4056();
            }
         } else {
            if (this.flag) {
               this.flag = false;
               if (this.vec3d == null || mc.player.position().distanceToSqr(this.vec3d) > 4.0) {
                  ChatLogger.method2021("TpLoot: телепорт не удался");
                  this.method4056();
                  return;
               }

               this.intValue2 = 0;
            }

            if (this.itemEntity == null) {
               ItemEntity itemEntity = this.method4057();
               if (itemEntity == null) {
                  ChatLogger.method2019("TpLoot: рядом нет ценного лута");
                  this.method4056();
               } else {
                  this.itemStack = itemEntity.getItem().copyWithCount(1);
                  this.intValue = this.method4059();
                  Vec3 position = itemEntity.position();
                  if (SpawnTracker.method6211(this, position)) {
                     this.itemEntity = itemEntity;
                     this.vec3d = position;
                     this.flag = true;
                  } else {
                     this.itemStack = ItemStack.EMPTY;
                  }
               }
            } else if (this.method4059() <= this.intValue && method4058(this.itemEntity)) {
               if (++this.intValue2 >= 100) {
                  this.method4056();
               }
            } else {
               ChatLogger.method2019("TpLoot: подобрал " + this.itemStack.getHoverName().getString());
               if (this.leaveUnder.isEnabled()) {
                  SpawnTracker.method6214(this);
               }

               this.method4056();
            }
         }
      } else {
         if (this.itemEntity != null) {
            this.method4056();
         }
      }
   }

   private ItemEntity method4057() {
      ItemEntity itemEntity = null;
      double d = Double.MAX_VALUE;

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof ItemEntity itemEntity2 && method4058(itemEntity2)) {
            ItemStack stack = itemEntity2.getItem();
            if (!items.contains(stack.getItem()) && canFit(stack) && SpawnTracker.method6210(itemEntity2.position())) {
               double d2 = mc.player.distanceToSqr(itemEntity2);
               if (d2 < d) {
                  d = d2;
                  itemEntity = itemEntity2;
               }
            }
         }
      }

      return itemEntity;
   }

   private static boolean method4058(ItemEntity itemEntity) {
      return itemEntity != null && itemEntity.isAlive() && !itemEntity.isRemoved() && !itemEntity.getItem().isEmpty();
   }

   private static boolean canFit(ItemStack stack) {
      Inventory inventory = mc.player.getInventory();

      for (int i = 0; i < 36; i++) {
         ItemStack stack2 = inventory.getItem(i);
         if (stack2.isEmpty() || ItemStack.isSameItemSameComponents(stack2, stack) && stack2.getCount() < stack2.getMaxStackSize()) {
            return true;
         }
      }

      ItemStack stack2 = inventory.getItem(40);
      return ItemStack.isSameItemSameComponents(stack2, stack) && stack2.getCount() < stack2.getMaxStackSize();
   }

   private int method4059() {
      if (this.itemStack.isEmpty()) {
         return 0;
      } else {
         Inventory inventory = mc.player.getInventory();
         int n = 0;

         for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            if (ItemStack.isSameItemSameComponents(this.itemStack, stack)) {
               n += stack.getCount();
            }
         }

         ItemStack stack = inventory.getItem(40);
         if (ItemStack.isSameItemSameComponents(this.itemStack, stack)) {
            n += stack.getCount();
         }

         return n;
      }
   }
}

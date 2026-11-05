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
   public void onUpdate(UpdateEvent var1) {
      if (this.clientWorld != mc.level || this.player != mc.player) {
         boolean var2 = this.itemEntity != null;
         this.clear();
         this.clientWorld = mc.level;
         this.player = mc.player;
         if (var2) {
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
               ItemEntity var4 = this.method4057();
               if (var4 == null) {
                  ChatLogger.method2019("TpLoot: рядом нет ценного лута");
                  this.method4056();
               } else {
                  this.itemStack = var4.getItem().copyWithCount(1);
                  this.intValue = this.method4059();
                  Vec3 var3 = var4.position();
                  if (SpawnTracker.method6211(this, var3)) {
                     this.itemEntity = var4;
                     this.vec3d = var3;
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
      ItemEntity var1 = null;
      double var2 = Double.MAX_VALUE;

      for (Entity var5 : mc.level.entitiesForRendering()) {
         if (var5 instanceof ItemEntity var6 && method4058(var6)) {
            ItemStack var7 = var6.getItem();
            if (!items.contains(var7.getItem()) && canFit(var7) && SpawnTracker.method6210(var6.position())) {
               double var8 = mc.player.distanceToSqr(var6);
               if (var8 < var2) {
                  var2 = var8;
                  var1 = var6;
               }
            }
         }
      }

      return var1;
   }

   private static boolean method4058(ItemEntity var0) {
      return var0 != null && var0.isAlive() && !var0.isRemoved() && !var0.getItem().isEmpty();
   }

   private static boolean canFit(ItemStack var0) {
      Inventory var1 = mc.player.getInventory();

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = var1.getItem(var2);
         if (var3.isEmpty() || ItemStack.isSameItemSameComponents(var3, var0) && var3.getCount() < var3.getMaxStackSize()) {
            return true;
         }
      }

      ItemStack var4 = var1.getItem(40);
      return ItemStack.isSameItemSameComponents(var4, var0) && var4.getCount() < var4.getMaxStackSize();
   }

   private int method4059() {
      if (this.itemStack.isEmpty()) {
         return 0;
      } else {
         Inventory var1 = mc.player.getInventory();
         int var2 = 0;

         for (int var3 = 0; var3 < 36; var3++) {
            ItemStack var4 = var1.getItem(var3);
            if (ItemStack.isSameItemSameComponents(this.itemStack, var4)) {
               var2 += var4.getCount();
            }
         }

         ItemStack var5 = var1.getItem(40);
         if (ItemStack.isSameItemSameComponents(this.itemStack, var5)) {
            var2 += var5.getCount();
         }

         return var2;
      }
   }
}

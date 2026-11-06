package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.PlayerAimUtils;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "AppleFarm",
   category = ModuleCategory.PLAYER,
   description = "module.applefarm.description"
)
public final class AppleFarmModule extends Module {
   public static final AppleFarmModule INSTANCE = new AppleFarmModule();
   private static final double CONST_doubleValue = 10.0;
   private boolean flag;

   private AppleFarmModule() {
   }

   @Override
   public void onDisable() {
      this.flag = false;
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         if (mc.player.tickCount % 600 < 10) {
            if (mc.player.getMainHandItem().is(net.minecraft.tags.ItemTags.AXES)) {
               mc.player.getInventory().setSelectedSlot((int)MathUtils.method2651(0.0, 8.0));
            }
         } else if (!this.method4103(19)) {
            if (KeyBindUtils.method2630(AppleFarmModule.class) && this.method4105()) {
               BlockPos pos = this.method4111(5.0, blockState -> blockState.is(BlockTags.LEAVES), this::method4110);
               if (pos != null) {
                  this.method4087(pos, stack -> stack.is(net.minecraft.tags.ItemTags.HOES));
               } else {
                  BlockPos pos2 = this.method4111(5.0, blockState -> blockState.is(BlockTags.LOGS), Vec3i::getY);
                  if (pos2 != null) {
                     this.method4087(pos2, stack -> stack.is(net.minecraft.tags.ItemTags.AXES));
                  } else if (mc.gui.screen() instanceof ContainerScreen) {
                     this.method4089();
                  } else if (this.method4099(Items.BONE) > 0 && this.method4099(Items.BONE_MEAL) == 0) {
                     this.method4090();
                  } else if (!this.method4101() && !this.method4102()) {
                     BlockPos pos3 = this.method4112(16.0, blockState -> blockState.is(BlockTags.DIRT));
                     if (pos3 != null) {
                        if (this.method4099(Items.STICK) <= 128 && this.method4099(Items.OAK_SAPLING) <= 128) {
                           this.method4088(pos3);
                        } else {
                           this.method4091();
                        }
                     }
                  } else {
                     this.method4089();
                  }
               }
            }
         }
      }
   }

   private void method4087(BlockPos pos, Predicate<ItemStack> predicate) {
      if (!predicate.test(mc.player.getMainHandItem())) {
         this.method4094(predicate);
      } else if (!(mc.player.getMainHandItem().is(net.minecraft.tags.ItemTags.AXES)) || !(mc.player.getAttackStrengthScale(0.0F) <= 0.15F)) {
         Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
         if (this.method4107(atCenterOf)) {
            Vec3 vec = new AABB(pos).clip(mc.player.getEyePosition(), atCenterOf).orElse(atCenterOf);
            mc.gameMode.continueDestroyBlock(pos, Direction.getApproximateNearest(vec.subtract(atCenterOf)));
            org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
         }
      }
   }

   private void method4088(BlockPos pos) {
      boolean flag;
      Predicate<net.minecraft.world.item.ItemStack> predicate = (flag = mc.level.getBlockState(pos.above()).getBlock() instanceof SaplingBlock)
         ? stack -> stack.is(Items.BONE_MEAL)
         : stack -> stack.getItem() instanceof BlockItem var1x && var1x.getBlock() instanceof SaplingBlock;
      if (!predicate.test(mc.player.getMainHandItem())) {
         this.method4094(predicate);
      } else {
         Vec3 vec = new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
         if (this.method4107(vec) && mc.player.tickCount % 4 == 0) {
            mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(vec, Direction.UP, flag ? pos.above() : pos, false));
            org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
         }
      }
   }

   private void method4089() {
      if (!(mc.gui.screen() instanceof ContainerScreen containerScreen)) {
         this.method4092(this.method4112(5.0, var1x -> var1x.is(this.method4101() ? Blocks.CHEST : Blocks.BARREL)));
      } else if (mc.player.tickCount % 50 == 0) {
         mc.player.closeContainer();
      } else if (mc.player.tickCount % 2 == 0) {
         AbstractContainerMenu menu = containerScreen.getMenu();
         int n;
         if (containerScreen.getTitle().getContents() instanceof TranslatableContents translatableContents && translatableContents.getKey().contains("chest")) {
            n = this.method4093(menu, false, 5, stack -> stack.is(Items.APPLE) || stack.is(Items.OAK_LOG));
         } else {
            n = this.method4102() ? this.method4093(menu, true, 3, this::method4100) : 0;
         }

         if (n == 0) {
            mc.player.closeContainer();
         }
      }
   }

   private void method4090() {
      if (mc.player.tickCount % 2 == 0) {
         InventoryMenu inventoryMenu = mc.player.inventoryMenu;
         if (inventoryMenu.getSlot(0).getItem().is(Items.BONE_MEAL)) {
            this.method4096(inventoryMenu, 0, 0, ContainerInput.QUICK_MOVE);
            mc.player.closeContainer();
         } else {
            int index;
            if ((index = this.method4098(Items.BONE)) >= 0) {
               this.method4096(inventoryMenu, index, 0, ContainerInput.PICKUP);
               this.method4096(inventoryMenu, 1, 0, ContainerInput.PICKUP);
            }
         }
      }
   }

   private void method4091() {
      float f;
      PlayerAimUtils.smoothAim((f = this.method4108()) * 10.0F, Mth.clamp(f / 4.0F, -30.0F, 30.0F));
      if (mc.player.tickCount % 5 == 0) {
         InventoryMenu inventoryMenu = mc.player.inventoryMenu;
         int index = -1;
         int count = -1;

         for (Slot slot : inventoryMenu.slots) {
            if (this.method4097(slot) && slot.getItem().is(Items.OAK_SAPLING) && slot.getItem().getCount() > count) {
               count = slot.getItem().getCount();
               index = slot.index;
            }
         }

         for (Slot slot : inventoryMenu.slots) {
            if (this.method4097(slot) && (slot.getItem().is(Items.STICK) || slot.getItem().is(Items.OAK_SAPLING) && slot.index != index)) {
               this.method4096(inventoryMenu, slot.index, 1, ContainerInput.THROW);
            }
         }
      }
   }

   private void method4092(BlockPos pos) {
      if (pos != null && this.method4107(net.minecraft.world.phys.Vec3.atCenterOf(pos)) && mc.player.tickCount % 4 == 0) {
         Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
         Vec3 location = new AABB(pos).clip(mc.player.getEyePosition(), atCenterOf).orElse(atCenterOf);
         mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(location, Direction.getApproximateNearest(location.subtract(atCenterOf)), pos, false));
      }
   }

   private int method4093(AbstractContainerMenu menu, boolean flag, int n, Predicate<ItemStack> predicate) {
      int n2 = 0;

      for (Slot slot : menu.slots) {
         if (n2 >= n) {
            break;
         }

         if (this.method4097(slot) != flag && predicate.test(slot.getItem())) {
            this.method4096(menu, slot.index, 0, ContainerInput.QUICK_MOVE);
            n2++;
         }
      }

      return n2;
   }

   private void method4094(Predicate<ItemStack> predicate) {
      int selected;
      if ((selected = IntStream.range(0, 36).filter(var1x -> predicate.test(mc.player.getInventory().getItem(var1x))).findFirst().orElse(-1)) >= 0) {
         if (selected < 9) {
            mc.player.getInventory().setSelectedSlot(selected);
         } else if (mc.player.tickCount % 4 == 0) {
            int n = IntStream.range(0, 9)
               .filter(slot -> mc.player.getInventory().getItem(slot).isEmpty())
               .findFirst()
               .orElse(mc.player.getInventory().getSelectedSlot());
            InventoryUtils.method935(this.method4095(selected), this.method4095(n), true, false);
            mc.player.getInventory().setSelectedSlot(n);
         }
      }
   }

   private int method4095(int selected) {
      return selected < 9 ? selected + 36 : selected;
   }

   private void method4096(AbstractContainerMenu inventoryMenu, int index, int buttonNum, ContainerInput containerInput) {
      mc.gameMode.handleContainerInput(inventoryMenu.containerId, index, buttonNum, containerInput, mc.player);
   }

   private boolean method4097(Slot slot) {
      return slot.container == mc.player.getInventory();
   }

   private int method4098(Item rawType) {
      for (Slot slot : mc.player.inventoryMenu.slots) {
         if (this.method4097(slot) && slot.getItem().is(rawType)) {
            return slot.index;
         }
      }

      return -1;
   }

   private int method4099(Item item) {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack stack = mc.player.getInventory().getItem(i);
         if (stack.getItem() == item) {
            n += stack.getCount();
         }
      }

      return n;
   }

   private boolean method4100(ItemStack stack) {
      return stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof SaplingBlock)
         ? !stack.is(Items.BONE_MEAL) || stack.is(Items.BONE) && this.method4099(Items.BONE_MEAL) == 0
         : true;
   }

   private boolean method4101() {
      return this.method4099(Items.APPLE) > 128 || this.method4099(Items.OAK_LOG) > 192;
   }

   private boolean method4102() {
      return this.method4099(Items.OAK_SAPLING) == 0 || this.method4099(Items.BONE_MEAL) == 0;
   }

   private boolean method4103(int n) {
      if (mc.player.getFoodData().getFoodLevel() < n && KeyBindUtils.method2630(AppleFarmModule.class)) {
         int selected = -1;
         int n2 = 32767 << 16 | (char)-1;

         for (int i = 0; i < 36 && n2 > 0; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.isEmpty()
               && stack.getUseAnimation() == ItemUseAnimation.EAT
               && !stack.has(DataComponents.CUSTOM_NAME)
               && !stack.is(Items.CHORUS_FRUIT)
               && !stack.is(Items.PUFFERFISH)
               && !stack.is(Items.DRIED_KELP)) {
               int n3;
               if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                  n3 = 3;
               } else if (stack.is(Items.GOLDEN_APPLE)) {
                  n3 = 2;
               } else {
                  n3 = this.method4104(stack) ? 1 : 0;
               }

               if (n3 < n2) {
                  n2 = n3;
                  selected = i;
               }
            }
         }

         if (selected < 0) {
            return false;
         } else if (selected > 8) {
            if (mc.player.tickCount % 5 == 0) {
               this.method4096(mc.player.inventoryMenu, this.method4095(selected), 8, ContainerInput.SWAP);
            }

            return true;
         } else {
            mc.player.getInventory().setSelectedSlot(selected);
            if (!mc.player.isUsingItem() || mc.player.getUsedItemHand() != InteractionHand.MAIN_HAND) {
               mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean method4104(ItemStack stack) {
      return stack.is(Items.BEEF)
         || stack.is(Items.PORKCHOP)
         || stack.is(Items.CHICKEN)
         || stack.is(Items.MUTTON)
         || stack.is(Items.RABBIT)
         || stack.is(Items.COD)
         || stack.is(Items.SALMON)
         || stack.is(Items.POTATO);
   }

   private boolean method4105() {
      ItemStack mainHandItem = mc.player.getMainHandItem();
      if (this.method4106(mainHandItem) > 10.0) {
         this.flag = false;
         return true;
      } else if (mainHandItem.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
            .getLevel(mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING))
         < 1) {
         if (!this.flag) {
            this.flag = true;
            ChatLogger.method2019("На предмете нету починки, отмена.");
         }

         return true;
      } else {
         if (!this.flag) {
            this.flag = true;
            ChatLogger.method2019("Прочность ниже 10%: починка бутылками опыта не перенесена, фарм ждёт");
         }

         return false;
      }
   }

   private double method4106(ItemStack mainHandItem) {
      return mainHandItem.isDamageableItem() && mainHandItem.getMaxDamage() > 0 ? (1.0 - (double)mainHandItem.getDamageValue() / mainHandItem.getMaxDamage()) * 100.0 : 100.0;
   }

   private boolean method4107(Vec3 atCenterOf) {
      PlayerAimUtils.aimAt(atCenterOf);
      return PlayerAimUtils.isWithinAimTolerance(atCenterOf, 20.0F);
   }

   private float method4108() {
      float f;
      return (float)(
         (
               Math.sin((f = mc.player.tickCount + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) * 0.31F) * 0.5
                  + Math.sin(f * 0.73F + 1.1F) * 0.30000003042305273
                  + Math.sin(f * 1.7F + 2.6F) * 0.2000000149681302
            )
            * 8.0
      );
   }

   private double method4109(BlockPos pos) {
      return mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos));
   }

   private double method4110(BlockPos pos) {
      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 viewVector = mc.player.getViewVector(1.0F);
      Vec3 vec = Vec3.atCenterOf(pos).subtract(eyePosition);
      double scale;
      return (scale = vec.dot(viewVector)) <= 0.0 ? 1.7976922776554332E308 : vec.subtract(viewVector.scale(scale)).lengthSqr();
   }

   private BlockPos method4111(double d, Predicate<BlockState> predicate, ToDoubleFunction<BlockPos> toDoubleFunction) {
      return this.method4113(d, predicate, toDoubleFunction, false);
   }

   private BlockPos method4112(double d, Predicate<BlockState> predicate) {
      return this.method4113(d, predicate, null, true);
   }

   private BlockPos method4113(double d, Predicate<BlockState> predicate, ToDoubleFunction<BlockPos> toDoubleFunction, boolean flag) {
      BlockPos blockPosition = mc.player.blockPosition();
      Vec3 eyePosition = mc.player.getEyePosition();
      BlockPos immutable = null;
      double applyAsDouble = 1.7976922776554332E308;
      double d2 = d * d;
      int n = (int)Math.ceil(d);
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = -n; i <= n; i++) {
         for (int j = -n; j <= n; j++) {
            for (int k = -n; k <= n; k++) {
               double d3 = blockPosition.getX() + i + 0.5 - eyePosition.x;
               double d4 = blockPosition.getY() + j + 0.5 - eyePosition.y;
               double d5 = blockPosition.getZ() + k + 0.5 - eyePosition.z;
               double d6;
               if (!((d6 = d3 * d3 + d4 * d4 + d5 * d5) > d2)) {
                  mutablePos.set(blockPosition.getX() + i, blockPosition.getY() + j, blockPosition.getZ() + k);
                  double applyAsDouble2;
                  if (predicate.test(mc.level.getBlockState(mutablePos)) && (applyAsDouble2 = flag ? d6 : toDoubleFunction.applyAsDouble(mutablePos)) < applyAsDouble) {
                     applyAsDouble = applyAsDouble2;
                     immutable = mutablePos.immutable();
                  }
               }
            }
         }
      }

      return immutable;
   }
}

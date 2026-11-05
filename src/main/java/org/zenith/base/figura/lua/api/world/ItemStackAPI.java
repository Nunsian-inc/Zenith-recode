package org.zenith.base.figura.lua.api.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.luaj.vm2.LuaTable;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.NbtToLua;
import org.zenith.base.figura.lua.ReadOnlyLuaTable;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "ItemStack", value = "itemstack")
public final class ItemStackAPI {
   public final ItemStack itemStack;
   public final String id;
   public final LuaTable tag;

   public ItemStackAPI(ItemStack itemStack) {
      this.itemStack = itemStack;
      this.id = BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()).toString();
      LuaTable mutableTag = itemStack.getComponents() != DataComponentMap.EMPTY
         ? (LuaTable)NbtToLua.convert(NbtToLua.convertToNbt(itemStack.getComponents()))
         : new LuaTable();
      LuaUtils.addLegacyNbtNames(mutableTag, mutableTag);
      this.tag = new ReadOnlyLuaTable(mutableTag);
   }

   public ItemStackAPI(ItemStack itemStack, LuaTable tag) {
      this.itemStack = itemStack;
      this.id = BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()).toString();
      this.tag = tag;
   }

   public static ItemStackAPI verify(ItemStack stack) {
      if (stack == null || stack == ItemStack.EMPTY) {
         stack = Items.AIR.getDefaultInstance();
      }

      return new ItemStackAPI(stack);
   }

   @LuaWhitelist
   public String getID() {
      return this.id;
   }

   @LuaWhitelist
   public LuaTable getTag() {
      return this.tag;
   }

   @LuaWhitelist
   public int getCount() {
      return this.itemStack.getCount();
   }

   @LuaWhitelist
   public int getDamage() {
      return this.itemStack.getDamageValue();
   }

   @LuaWhitelist
   public int getPopTime() {
      return this.itemStack.getPopTime();
   }

   @LuaWhitelist
   public boolean hasGlint() {
      return this.itemStack.hasFoil();
   }

   @LuaWhitelist
   public boolean isBlockItem() {
      return this.itemStack.getItem() instanceof BlockItem;
   }

   @LuaWhitelist
   public boolean isFood() {
      return this.itemStack.getComponents().has(DataComponents.FOOD);
   }

   @LuaWhitelist
   public String getUseAction() {
      return this.itemStack.getUseAnimation().name();
   }

   @LuaWhitelist
   public String getName() {
      return this.itemStack.getHoverName().getString();
   }

   @LuaWhitelist
   public int getMaxCount() {
      return this.itemStack.getMaxStackSize();
   }

   @LuaWhitelist
   public String getRarity() {
      return this.itemStack.getRarity().name();
   }

   @LuaWhitelist
   public boolean isEnchantable() {
      return this.itemStack.isEnchantable();
   }

   @LuaWhitelist
   public int getMaxDamage() {
      return this.itemStack.getMaxDamage();
   }

   @LuaWhitelist
   public boolean isDamageable() {
      return this.itemStack.isDamageableItem();
   }

   @LuaWhitelist
   public boolean isStackable() {
      return this.itemStack.isStackable();
   }

   @LuaWhitelist
   public int getRepairCost() {
      return (Integer)this.itemStack.getOrDefault(DataComponents.REPAIR_COST, 0);
   }

   @LuaWhitelist
   public int getUseDuration() {
      return this.itemStack.getUseDuration(Minecraft.getInstance().player);
   }

   @LuaWhitelist
   public String toStackString() {
      return this.id + LuaUtils.getItemStackString(this.itemStack);
   }

   @LuaWhitelist
   public boolean isArmor() {
      return this.itemStack.getItem().components().has(DataComponents.EQUIPPABLE);
   }

   @LuaWhitelist
   public boolean isTool() {
      return this.itemStack.getItem().components().has(DataComponents.TOOL);
   }

   @LuaWhitelist
   public String getEquipmentSlot() {
      return Minecraft.getInstance().player.getEquipmentSlotForItem(this.itemStack).name();
   }

   @LuaWhitelist
   public ItemStackAPI copy() {
      return new ItemStackAPI(this.itemStack.copy(), this.tag);
   }

   @LuaWhitelist
   public List<String> getTags() {
      List<String> result = new ArrayList<>();
      this.itemStack.typeHolder().tags().forEach(tag -> result.add(tag.location().toString()));
      return result;
   }

   @LuaWhitelist
   public BlockStateAPI getBlockstate() {
      return this.itemStack.getItem() instanceof BlockItem blockItem ? new BlockStateAPI(blockItem.getBlock().defaultBlockState(), null) : null;
   }

   @LuaWhitelist
   public boolean __eq(ItemStackAPI other) {
      if (this == other) {
         return true;
      } else if (other == null) {
         return false;
      } else {
         ItemStack theirs = other.itemStack;
         if (this.itemStack.getCount() != theirs.getCount()) {
            return false;
         } else {
            return !this.itemStack.is(theirs.getItem()) ? false : this.itemStack.getComponents().equals(theirs.getComponents());
         }
      }
   }

   @LuaWhitelist
   public Object __index(String key) {
      String str = key == null ? "" : key;

      return switch (str) {
         case "id" -> this.id;
         case "tag" -> this.tag;
         default -> null;
      };
   }

   @Override
   public String toString() {
      return this.id + " x" + this.getCount() + " (ItemStack)";
   }
}

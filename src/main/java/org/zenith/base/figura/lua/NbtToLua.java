package org.zenith.base.figura.lua;

import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.zenith.base.figura.lua.api.world.WorldAPI;

public final class NbtToLua {
   private NbtToLua() {
   }

   public static LuaValue convert(Tag element) {
      if (element == null) {
         return LuaValue.NIL;
      } else if (element instanceof ByteTag number) {
         return LuaValue.valueOf(number.byteValue());
      } else if (element instanceof ShortTag number) {
         return LuaValue.valueOf(number.shortValue());
      } else if (element instanceof IntTag number) {
         return LuaValue.valueOf(number.intValue());
      } else if (element instanceof LongTag number) {
         return LuaValue.valueOf(number.longValue());
      } else if (element instanceof FloatTag number) {
         return LuaValue.valueOf(number.floatValue());
      } else if (element instanceof DoubleTag number) {
         return LuaValue.valueOf(number.doubleValue());
      } else if (element instanceof CompoundTag compound) {
         LuaTable result = new LuaTable();

         for (String key : compound.keySet()) {
            result.set(key, convert(compound.get(key)));
         }

         return result;
      } else if (!(element instanceof CollectionTag list)) {
         return LuaValue.valueOf(element.asString().orElse(""));
      } else {
         LuaTable result = new LuaTable();
         int i = 1;

         for (Tag child : list) {
            result.set(i++, convert(child));
         }

         return result;
      }
   }

   public static CompoundTag convertToNbt(DataComponentMap components) {
      if (components == null) {
         return null;
      }

      CompoundTag tag = new CompoundTag();
      if (WorldAPI.getCurrentWorld() == null) {
         return tag;
      }

      DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, WorldAPI.getCurrentWorld().registryAccess());

      for (TypedDataComponent<?> component : components) {
         Optional<Tag> encoded = component.encodeValue(ops).result();
         Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type());
         if (encoded.isPresent() && id != null) {
            tag.put(id.toString(), encoded.get());
         }
      }

      return tag;
   }
}

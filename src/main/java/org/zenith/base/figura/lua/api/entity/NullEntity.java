package org.zenith.base.figura.lua.api.entity;

import org.luaj.vm2.LuaError;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "NullEntity", value = "")
public class NullEntity {
   public static final NullEntity INSTANCE = new NullEntity();

   protected NullEntity() {
   }

   @LuaWhitelist
   public boolean isLoaded() {
      return false;
   }

   @LuaWhitelist
   public Object __index(Object o) {
      throw new LuaError(
         "Tried to access the Entity API before its initialization in the ENTITY_INIT event. To check if the entity exists, use \"<entity>:isLoaded()\""
      );
   }

   @Override
   public String toString() {
      return "Uninitialized Entity API";
   }
}

package org.zenith.base.figura.lua.api.data;

import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "DataAPI", value = "data")
public class DataAPI {
   private final Avatar parent;

   public DataAPI(Avatar parent) {
      this.parent = parent;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      value = "data.create_buffer",
      overloads = {
            @LuaMethodOverload(returnType = FiguraBuffer.class),
            @LuaMethodOverload(returnType = FiguraBuffer.class, argumentNames = "capacity", argumentTypes = Integer.class)
      }
   )
   public FiguraBuffer createBuffer(Integer len) {
      return len == null ? new FiguraBuffer(this.parent) : new FiguraBuffer(this.parent, len);
   }

   @Override
   public String toString() {
      return "DataAPI";
   }
}

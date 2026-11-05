package org.zenith.base.figura.lua.api.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "ResourcesAPI", value = "resources")
public class ResourcesAPI {
   private final Avatar parent;

   public ResourcesAPI(Avatar parent) {
      this.parent = parent;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      value = "resources.get",
      overloads = @LuaMethodOverload(returnType = FiguraInputStream.class, argumentTypes = String.class, argumentNames = "path")
   )
   public FiguraInputStream get(@LuaNotNil String path) {
      try {
         if (this.parent.resources.containsKey(path)) {
            ByteArrayInputStream bais = new ByteArrayInputStream(this.parent.resources.get(path));
            return new FiguraInputStream(this.parent, new GZIPInputStream(bais));
         } else {
            return null;
         }
      } catch (IOException var3) {
         throw new LuaError(var3);
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(value = "resources.get_paths", overloads = @LuaMethodOverload(returnType = LuaTable.class))
   public List<String> getPaths() {
      return new ArrayList<>(this.parent.resources.keySet());
   }

   @LuaWhitelist
   public FiguraInputStream __index(@LuaNotNil String path) {
      return this.get(path);
   }

   @Override
   public String toString() {
      return "ResourcesAPI";
   }
}

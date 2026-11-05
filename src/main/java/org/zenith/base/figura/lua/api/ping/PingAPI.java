package org.zenith.base.figura.lua.api.ping;

import java.util.HashMap;
import java.util.Map;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "PingAPI", value = "pings")
public class PingAPI {
   private final Avatar owner;
   private final Map<String, PingFunction> pings = new HashMap<>();

   public PingAPI(Avatar owner) {
      this.owner = owner;
   }

   @LuaWhitelist
   public PingFunction __index(@LuaNotNil String key) {
      return this.pings.get(key);
   }

   @LuaWhitelist
   public void __newindex(@LuaNotNil String key, LuaFunction value) {
      if (value == null) {
         this.pings.remove(key);
      } else {
         if (!(value instanceof LuaFunction)) {
            throw new LuaError("pings." + key + " must be a function");
         }

         this.pings.put(key, new PingFunction(this.owner, key, value));
      }
   }

   public Map<String, PingFunction> getPings() {
      return this.pings;
   }

   @Override
   public String toString() {
      return "PingAPI";
   }
}

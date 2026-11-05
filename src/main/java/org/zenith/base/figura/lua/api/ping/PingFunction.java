package org.zenith.base.figura.lua.api.ping;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "PingFunction", value = "ping_function")
public class PingFunction extends LuaFunction {
   private final Avatar owner;
   private final String name;
   private final LuaFunction function;

   public PingFunction(Avatar owner, String name, LuaFunction function) {
      this.owner = owner;
      this.name = name;
      this.function = function;
   }

   public LuaValue call() {
      this.invoke(NONE);
      return NIL;
   }

   public LuaValue call(LuaValue arg) {
      this.invoke(arg);
      return NIL;
   }

   public LuaValue call(LuaValue arg1, LuaValue arg2) {
      this.invoke(arg1, arg2);
      return NIL;
   }

   public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
      this.invoke(arg1, arg2, arg3);
      return NIL;
   }

   public Varargs invoke(Varargs args) {
      if (this.owner.luaRuntime != null && this.function != null && !this.owner.scriptError) {
         this.owner.luaRuntime.setInstructionLimit(this.owner.tick.remaining);

         try {
            this.function.invoke(args);
            this.owner.tick.use(this.owner.luaRuntime.getInstructions());
            return NIL;
         } catch (StackOverflowError | Exception var3) {
            this.owner.luaRuntime.error(var3);
            return NIL;
         }
      } else {
         return NIL;
      }
   }

   @LuaWhitelist
   public String getName() {
      return this.name;
   }

   public String toString() {
      return "PingFunction (" + this.name + ")";
   }
}

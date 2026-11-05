package org.zenith.base.figura.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class HeadlessConfigAPI {
   private HeadlessConfigAPI() {
   }

   public static LuaTable create() {
      final LuaTable var0 = new LuaTable();
      final LuaTable var1 = new LuaTable();
      final LuaValue[] var2 = new LuaValue[]{LuaValue.valueOf("avatar")};
      VarArgFunction var3 = new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            LuaValue var2x = var1.arg(HeadlessConfigAPI.argumentOffset(var1, var0));
            if (!var2x.isnil()) {
               var2[0] = LuaValue.valueOf(var2x.checkjstring());
            }

            return var0;
         }
      };
      var0.set("setName", var3);
      var0.set("name", var3);
      var0.set("getName", new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            return var2[0];
         }
      });
      var0.set("save", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessConfigAPI.argumentOffset(var1x, var0);
            LuaValue var3 = var1x.arg(var2x);
            if (!var3.isnil()) {
               HeadlessConfigAPI.currentStore(var1, var2[0]).rawset(var3, var1x.arg(var2x + 1));
            }

            return var0;
         }
      });
      var0.set("load", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaTable var2x = HeadlessConfigAPI.currentStore(var1, var2[0]);
            LuaValue var3 = var1x.arg(HeadlessConfigAPI.argumentOffset(var1x, var0));
            return (Varargs)(var3.isnil() ? var2x : var2x.rawget(var3));
         }
      });
      return var0;
   }

   private static LuaTable currentStore(LuaTable var0, LuaValue var1) {
      LuaValue var2 = var0.rawget(var1);
      if (var2.istable()) {
         return var2.checktable();
      }

      LuaTable var3 = new LuaTable();
      var0.rawset(var1, var3);
      return var3;
   }

   private static int argumentOffset(Varargs var0, LuaValue var1) {
      return var0.narg() > 0 && var0.arg1().raweq(var1) ? 2 : 1;
   }
}

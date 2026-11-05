package org.zenith.base.figura.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class HeadlessConfigAPI {
   private HeadlessConfigAPI() {
   }

   public static LuaTable create() {
      final LuaTable luaTable = new LuaTable();
      final LuaTable luaTable2 = new LuaTable();
      final LuaValue[] luaValues = new LuaValue[]{LuaValue.valueOf("avatar")};
      VarArgFunction varArgFunction = new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            LuaValue var2x = varargs.arg(HeadlessConfigAPI.argumentOffset(varargs, luaTable));
            if (!var2x.isnil()) {
               luaValues[0] = LuaValue.valueOf(var2x.checkjstring());
            }

            return luaTable;
         }
      };
      luaTable.set("setName", varArgFunction);
      luaTable.set("name", varArgFunction);
      luaTable.set("getName", new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            return luaValues[0];
         }
      });
      luaTable.set("save", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessConfigAPI.argumentOffset(var1x, luaTable);
            LuaValue luaValue = var1x.arg(var2x);
            if (!luaValue.isnil()) {
               HeadlessConfigAPI.currentStore(luaTable2, luaValues[0]).rawset(luaValue, var1x.arg(var2x + 1));
            }

            return luaTable;
         }
      });
      luaTable.set("load", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaTable var2x = HeadlessConfigAPI.currentStore(luaTable2, luaValues[0]);
            LuaValue luaValue = var1x.arg(HeadlessConfigAPI.argumentOffset(var1x, luaTable));
            return (Varargs)(luaValue.isnil() ? var2x : var2x.rawget(luaValue));
         }
      });
      return luaTable;
   }

   private static LuaTable currentStore(LuaTable luaTable, LuaValue luaValue) {
      LuaValue rawget = luaTable.rawget(luaValue);
      if (rawget.istable()) {
         return rawget.checktable();
      }

      LuaTable luaTable2 = new LuaTable();
      luaTable.rawset(luaValue, luaTable2);
      return luaTable2;
   }

   private static int argumentOffset(Varargs varargs, LuaValue luaTable) {
      return varargs.narg() > 0 && varargs.arg1().raweq(luaTable) ? 2 : 1;
   }
}

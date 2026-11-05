package org.zenith.base.figura.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ReadOnlyLuaTable extends LuaTable {
   public ReadOnlyLuaTable(LuaValue table) {
      this.presize(table.length(), 0);

      for (Varargs next = table.next(NIL); !next.arg1().isnil(); next = table.next(next.arg1())) {
         LuaValue key = next.arg1();
         LuaValue value = next.arg(2);
         super.rawset(key, (LuaValue)(value.istable() ? (value == table ? this : new ReadOnlyLuaTable(value)) : value));
      }
   }

   public LuaValue setmetatable(LuaValue metatable) {
      return error("table is read-only");
   }

   public void set(int key, LuaValue value) {
      error("table is read-only");
   }

   public void rawset(int key, LuaValue value) {
      error("table is read-only");
   }

   public void rawset(LuaValue key, LuaValue value) {
      error("table is read-only");
   }

   public LuaValue remove(int pos) {
      return error("table is read-only");
   }
}

package org.zenith.base.figura.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public final class HeadlessActionWheelAPI {
   private HeadlessActionWheelAPI() {
   }

   public static LuaTable create() {
      final LuaTable luaTable = chainTable();
      final LuaTable luaTable2 = new LuaTable();
      final LuaValue[] luaValues = new LuaValue[]{LuaValue.NIL};
      luaTable.set("newPage", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(var1x, luaTable);
            LuaValue luaValue = var1x.arg(var2x);
            LuaTable page = HeadlessActionWheelAPI.newPage(luaValue);
            if (luaValue.isstring()) {
               luaTable2.rawset(luaValue, page);
            }

            return page;
         }
      });
      luaTable.set("newAction", new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            return HeadlessActionWheelAPI.newAction();
         }
      });
      luaTable.set("setPage", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue var2x = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, luaTable));
            luaValues[0] = var2x.isstring() ? luaTable2.get(var2x) : var2x;
            return luaTable;
         }
      });
      luaTable.set("getPage", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue luaValue = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, luaTable));
            return (Varargs)(luaValue.isnil() ? luaTable2 : luaTable2.get(luaValue));
         }
      });
      luaTable.set("getCurrentPage", new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            return luaValues[0];
         }
      });
      luaTable.set("execute", returning(luaTable));
      luaTable.set("isEnabled", constant(LuaValue.FALSE));
      luaTable.set("getSelected", constant(LuaValue.ZERO));
      luaTable.set("getSelectedAction", constant(LuaValue.NIL));
      return luaTable;
   }

   private static LuaTable newPage(LuaValue luaValue) {
      final LuaTable luaTable = chainTable();
      final LuaTable luaTable2 = new LuaTable();
      luaTable.rawset("keepSlots", LuaValue.FALSE);
      luaTable.rawset("__title", luaValue);
      luaTable.rawset("__slotsShift", LuaValue.ONE);
      luaTable.set("newAction", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(var1x, luaTable);
            LuaValue luaValue2 = var1x.arg(var2x);
            int checkint = luaValue2.isint() && luaValue2.checkint() > 0 ? luaValue2.checkint() : HeadlessActionWheelAPI.firstFreeIndex(luaTable2);
            LuaTable action = HeadlessActionWheelAPI.newAction();
            luaTable2.rawset(checkint, action);
            return action;
         }
      });
      luaTable.set("getAction", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue var2x = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, luaTable));
            return var2x.isint() && var2x.checkint() > 0 ? luaTable2.get(var2x) : LuaValue.NIL;
         }
      });
      VarArgFunction varArgFunction = new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(var1x, luaTable);
            LuaValue luaValue2 = var1x.arg(var2x);
            LuaValue luaValue3 = var1x.arg(var2x + 1);
            int checkint = luaValue2.isint() && luaValue2.checkint() > 0 ? luaValue2.checkint() : HeadlessActionWheelAPI.firstFreeIndex(luaTable2);
            luaTable2.rawset(checkint, luaValue3);
            return luaTable;
         }
      };
      luaTable.set("setAction", varArgFunction);
      luaTable.set("action", varArgFunction);
      luaTable.set("getActions", constant(luaTable2));
      luaTable.set("getTitle", constant(luaValue));
      luaTable.set("setKeepSlots", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue luaValue2 = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, luaTable));
            luaTable.rawset("keepSlots", LuaValue.valueOf(luaValue2.toboolean()));
            return luaTable;
         }
      });
      luaTable.set("shouldKeepSlots", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            return luaTable.rawget("keepSlots");
         }
      });
      luaTable.set("setSlotsShift", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue luaValue2 = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, luaTable));
            luaTable.rawset("__slotsShift", (LuaValue)(luaValue2.isint() ? luaValue2 : LuaValue.ONE));
            return luaTable;
         }
      });
      luaTable.set("slotsShift", luaTable.get("setSlotsShift"));
      luaTable.set("getSlotsShift", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            return luaTable.rawget("__slotsShift");
         }
      });
      return luaTable;
   }

   private static LuaTable newAction() {
      final LuaTable luaTable = chainTable();
      luaTable.rawset("__toggled", LuaValue.FALSE);
      String[][] stringses = new String[][]{
         {"Title", "title"},
         {"ToggleTitle", "toggleTitle"},
         {"Item", "item"},
         {"HoverItem", "hoverItem"},
         {"ToggleItem", "toggleItem"},
         {"Color", "color"},
         {"HoverColor", "hoverColor"},
         {"ToggleColor", "toggleColor"},
         {"Texture", "texture"},
         {"HoverTexture", "hoverTexture"},
         {"ToggleTexture", "toggleTexture"},
         {"LeftClick", "leftClick"},
         {"RightClick", "rightClick"},
         {"Toggle", "toggle"},
         {"Untoggle", "untoggle"},
         {"Scroll", "scroll"}
      };

      for (String[] strings : stringses) {
         installProperty(luaTable, strings[0], strings[1]);
      }

      luaTable.set("setToggled", new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            LuaValue luaValue = varargs.arg(HeadlessActionWheelAPI.argumentOffset(varargs, luaTable));
            luaTable.rawset("__toggled", LuaValue.valueOf(luaValue.toboolean()));
            return luaTable;
         }
      });
      luaTable.set("toggled", luaTable.get("setToggled"));
      luaTable.set("isToggled", new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            return luaTable.rawget("__toggled");
         }
      });
      return luaTable;
   }

   private static void installProperty(LuaTable luaTable, String str, String str2) {
      VarArgFunction varArgFunction = new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(varargs, luaTable);
            Object var3x;
            if (varargs.narg() - var2x + 1 <= 1) {
               var3x = varargs.arg(var2x);
            } else {
               LuaTable luaTable2 = new LuaTable();

               for (int i = var2x; i <= varargs.narg(); i++) {
                  luaTable2.rawset(i - var2x + 1, varargs.arg(i));
               }

               var3x = luaTable2;
            }

            luaTable.rawset("__" + str2, (LuaValue)var3x);
            return luaTable;
         }
      };
      luaTable.set("set" + str, varArgFunction);
      luaTable.set(decapitalize(str), varArgFunction);
      if (str.equals("LeftClick") || str.equals("RightClick") || str.equals("Toggle") || str.equals("Untoggle") || str.equals("Scroll")) {
         luaTable.set("on" + str, varArgFunction);
      }

      luaTable.set("get" + str, new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            return luaTable.rawget("__" + str2);
         }
      });
   }

   private static LuaTable chainTable() {
      LuaTable luaTable = new LuaTable();
      LuaTable luaTable2 = new LuaTable();
      luaTable2.set("__index", new TwoArgFunction() {
         public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
            return HeadlessActionWheelAPI.returning(luaValue);
         }
      });
      luaTable.setmetatable(luaTable2);
      return luaTable;
   }

   private static int argumentOffset(Varargs varargs, LuaValue luaValue) {
      return varargs.narg() > 0 && varargs.arg1().raweq(luaValue) ? 2 : 1;
   }

   private static int firstFreeIndex(LuaTable luaTable) {
      int n = 1;

      while (!luaTable.rawget(n).isnil()) {
         n++;
      }

      return n;
   }

   private static VarArgFunction returning(LuaValue luaValue) {
      return new VarArgFunction() {
         public Varargs invoke(Varargs varargs) {
            return luaValue;
         }
      };
   }

   private static VarArgFunction constant(LuaValue luaValue) {
      return returning(luaValue);
   }

   private static String decapitalize(String str) {
      return Character.toLowerCase(str.charAt(0)) + str.substring(1);
   }
}

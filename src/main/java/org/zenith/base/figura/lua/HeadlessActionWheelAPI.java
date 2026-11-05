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
      final LuaTable var0 = chainTable();
      final LuaTable var1 = new LuaTable();
      final LuaValue[] var2 = new LuaValue[]{LuaValue.NIL};
      var0.set("newPage", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(var1x, var0);
            LuaValue var3 = var1x.arg(var2x);
            LuaTable var4 = HeadlessActionWheelAPI.newPage(var3);
            if (var3.isstring()) {
               var1.rawset(var3, var4);
            }

            return var4;
         }
      });
      var0.set("newAction", new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            return HeadlessActionWheelAPI.newAction();
         }
      });
      var0.set("setPage", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue var2x = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, var0));
            var2[0] = var2x.isstring() ? var1.get(var2x) : var2x;
            return var0;
         }
      });
      var0.set("getPage", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue var2 = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, var0));
            return (Varargs)(var2.isnil() ? var1 : var1.get(var2));
         }
      });
      var0.set("getCurrentPage", new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            return var2[0];
         }
      });
      var0.set("execute", returning(var0));
      var0.set("isEnabled", constant(LuaValue.FALSE));
      var0.set("getSelected", constant(LuaValue.ZERO));
      var0.set("getSelectedAction", constant(LuaValue.NIL));
      return var0;
   }

   private static LuaTable newPage(LuaValue var0) {
      final LuaTable var1 = chainTable();
      final LuaTable var2 = new LuaTable();
      var1.rawset("keepSlots", LuaValue.FALSE);
      var1.rawset("__title", var0);
      var1.rawset("__slotsShift", LuaValue.ONE);
      var1.set("newAction", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(var1x, var1);
            LuaValue var3 = var1x.arg(var2x);
            int var4 = var3.isint() && var3.checkint() > 0 ? var3.checkint() : HeadlessActionWheelAPI.firstFreeIndex(var2);
            LuaTable var5 = HeadlessActionWheelAPI.newAction();
            var2.rawset(var4, var5);
            return var5;
         }
      });
      var1.set("getAction", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue var2x = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, var1));
            return var2x.isint() && var2x.checkint() > 0 ? var2.get(var2x) : LuaValue.NIL;
         }
      });
      VarArgFunction var3 = new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(var1x, var1);
            LuaValue var3 = var1x.arg(var2x);
            LuaValue var4 = var1x.arg(var2x + 1);
            int var5 = var3.isint() && var3.checkint() > 0 ? var3.checkint() : HeadlessActionWheelAPI.firstFreeIndex(var2);
            var2.rawset(var5, var4);
            return var1;
         }
      };
      var1.set("setAction", var3);
      var1.set("action", var3);
      var1.set("getActions", constant(var2));
      var1.set("getTitle", constant(var0));
      var1.set("setKeepSlots", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue var2 = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, var1));
            var1.rawset("keepSlots", LuaValue.valueOf(var2.toboolean()));
            return var1;
         }
      });
      var1.set("shouldKeepSlots", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            return var1.rawget("keepSlots");
         }
      });
      var1.set("setSlotsShift", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            LuaValue var2 = var1x.arg(HeadlessActionWheelAPI.argumentOffset(var1x, var1));
            var1.rawset("__slotsShift", (LuaValue)(var2.isint() ? var2 : LuaValue.ONE));
            return var1;
         }
      });
      var1.set("slotsShift", var1.get("setSlotsShift"));
      var1.set("getSlotsShift", new VarArgFunction() {
         public Varargs invoke(Varargs var1x) {
            return var1.rawget("__slotsShift");
         }
      });
      return var1;
   }

   private static LuaTable newAction() {
      final LuaTable var0 = chainTable();
      var0.rawset("__toggled", LuaValue.FALSE);
      String[][] var1 = new String[][]{
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

      for (String[] var5 : var1) {
         installProperty(var0, var5[0], var5[1]);
      }

      var0.set("setToggled", new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            LuaValue var2 = var1.arg(HeadlessActionWheelAPI.argumentOffset(var1, var0));
            var0.rawset("__toggled", LuaValue.valueOf(var2.toboolean()));
            return var0;
         }
      });
      var0.set("toggled", var0.get("setToggled"));
      var0.set("isToggled", new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            return var0.rawget("__toggled");
         }
      });
      return var0;
   }

   private static void installProperty(LuaTable var0, String var1, String var2) {
      VarArgFunction var3 = new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            int var2x = HeadlessActionWheelAPI.argumentOffset(var1, var0);
            Object var3x;
            if (var1.narg() - var2x + 1 <= 1) {
               var3x = var1.arg(var2x);
            } else {
               LuaTable var4 = new LuaTable();

               for (int var5 = var2x; var5 <= var1.narg(); var5++) {
                  var4.rawset(var5 - var2x + 1, var1.arg(var5));
               }

               var3x = var4;
            }

            var0.rawset("__" + var2, (LuaValue)var3x);
            return var0;
         }
      };
      var0.set("set" + var1, var3);
      var0.set(decapitalize(var1), var3);
      if (var1.equals("LeftClick") || var1.equals("RightClick") || var1.equals("Toggle") || var1.equals("Untoggle") || var1.equals("Scroll")) {
         var0.set("on" + var1, var3);
      }

      var0.set("get" + var1, new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            return var0.rawget("__" + var2);
         }
      });
   }

   private static LuaTable chainTable() {
      LuaTable var0 = new LuaTable();
      LuaTable var1 = new LuaTable();
      var1.set("__index", new TwoArgFunction() {
         public LuaValue call(LuaValue var1, LuaValue var2) {
            return HeadlessActionWheelAPI.returning(var1);
         }
      });
      var0.setmetatable(var1);
      return var0;
   }

   private static int argumentOffset(Varargs var0, LuaValue var1) {
      return var0.narg() > 0 && var0.arg1().raweq(var1) ? 2 : 1;
   }

   private static int firstFreeIndex(LuaTable var0) {
      int var1 = 1;

      while (!var0.rawget(var1).isnil()) {
         var1++;
      }

      return var1;
   }

   private static VarArgFunction returning(LuaValue var0) {
      return new VarArgFunction() {
         public Varargs invoke(Varargs var1) {
            return var0;
         }
      };
   }

   private static VarArgFunction constant(LuaValue var0) {
      return returning(var0);
   }

   private static String decapitalize(String var0) {
      return Character.toLowerCase(var0.charAt(0)) + var0.substring(1);
   }
}

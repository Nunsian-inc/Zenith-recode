package org.zenith.base.figura.lua.api.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Varargs;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaFieldDoc;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "Keybind", value = "keybind")
public class FiguraKeybind {
   private final Avatar owner;
   private final String name;
   private final Key defaultKey;
   private Key key;
   private boolean isDown;
   private boolean override;
   private boolean enabled = true;
   private boolean gui;
   @LuaWhitelist
   @LuaFieldDoc("keybind.press")
   public LuaFunction press;
   @LuaWhitelist
   @LuaFieldDoc("keybind.release")
   public LuaFunction release;

   public FiguraKeybind(Avatar owner, String name, Key key) {
      this.owner = owner;
      this.name = name;
      this.defaultKey = key;
      this.key = key;
   }

   public void resetDefaultKey() {
      this.key = this.defaultKey;
   }

   public boolean setDown(boolean pressed, int modifiers) {
      if (this.isDown != pressed) {
         Varargs result = null;
         if (pressed) {
            if (this.press != null) {
               result = this.owner.run(this.press, this.owner.tick, modifiers, this);
            }
         } else if (this.release != null) {
            result = this.owner.run(this.release, this.owner.tick, modifiers, this);
         }

         this.override = result != null && result.arg(1).isboolean() && result.checkboolean(1);
      }

      this.isDown = pressed;
      return this.override;
   }

   public void setKey(Key key) {
      this.key = key;
   }

   public static Key parseStringKey(String key) {
      try {
         return InputConstants.getKey(key);
      } catch (Exception var2) {
         throw new LuaError("Invalid key: " + key);
      }
   }

   public static boolean set(List<FiguraKeybind> bindings, Key key, boolean pressed, int modifiers) {
      boolean overrided = false;

      for (FiguraKeybind keybind : List.copyOf(bindings)) {
         if (keybind.key == key && keybind.enabled && (keybind.gui || Minecraft.getInstance().gui.screen() == null)) {
            overrided = keybind.setDown(pressed, modifiers) || overrided;
         }
      }

      return overrided;
   }

   public static void releaseAll(List<FiguraKeybind> bindings) {
      for (FiguraKeybind keybind : bindings) {
         keybind.setDown(false, -1);
      }
   }

   public static void updateAll(List<FiguraKeybind> bindings) {
      Window handle = Minecraft.getInstance().getWindow();

      for (FiguraKeybind keybind : bindings) {
         int value = keybind.key.getValue();
         if (keybind.enabled && keybind.key.getType() == Type.KEYBOARD && value != InputConstants.UNKNOWN.getValue()) {
            keybind.setDown(InputConstants.isKeyDown(value), -1);
         }
      }
   }

   public static boolean overridesKey(List<FiguraKeybind> bindings, Key key) {
      for (FiguraKeybind binding : bindings) {
         if (binding.key == key && binding.enabled && binding.override) {
            return true;
         }
      }

      return false;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = LuaFunction.class, argumentNames = "function"),
      aliases = "onPress",
      value = "keybind.set_on_press"
   )
   public FiguraKeybind setOnPress(LuaFunction function) {
      this.press = function;
      return this;
   }

   @LuaWhitelist
   public FiguraKeybind onPress(LuaFunction function) {
      return this.setOnPress(function);
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = LuaFunction.class, argumentNames = "function"),
      aliases = "onRelease",
      value = "keybind.set_on_release"
   )
   public FiguraKeybind setOnRelease(LuaFunction function) {
      this.release = function;
      return this;
   }

   @LuaWhitelist
   public FiguraKeybind onRelease(LuaFunction function) {
      return this.setOnRelease(function);
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "key"), aliases = "key", value = "keybind.set_key")
   public FiguraKeybind setKey(@LuaNotNil String key) {
      this.key = parseStringKey(key);
      return this;
   }

   @LuaWhitelist
   public FiguraKeybind key(@LuaNotNil String key) {
      return this.setKey(key);
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.is_default")
   public boolean isDefault() {
      return this.key.equals(this.defaultKey);
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.get_key")
   public String getKey() {
      return this.key.getName();
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.get_key_name")
   public String getKeyName() {
      return this.key.getDisplayName().getString();
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.get_name")
   public String getName() {
      return this.name;
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.get_id")
   public int getID() {
      return this.key.getValue();
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.is_pressed")
   public boolean isPressed() {
      return (this.gui || Minecraft.getInstance().gui.screen() == null) && this.isDown;
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.is_enabled")
   public boolean isEnabled() {
      return this.enabled;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = Boolean.class, argumentNames = "bool"), aliases = "enabled", value = "keybind.set_enabled")
   public FiguraKeybind setEnabled(boolean bool) {
      this.enabled = bool;
      return this;
   }

   @LuaWhitelist
   public FiguraKeybind enabled(boolean bool) {
      return this.setEnabled(bool);
   }

   @LuaWhitelist
   @LuaMethodDoc("keybind.is_gui_enabled")
   public boolean isGuiEnabled() {
      return this.gui;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = Boolean.class, argumentNames = "bool"), aliases = "gui", value = "keybind.set_gui")
   public FiguraKeybind setGUI(boolean bool) {
      this.gui = bool;
      return this;
   }

   @LuaWhitelist
   public FiguraKeybind gui(boolean bool) {
      return this.setGUI(bool);
   }

   @LuaWhitelist
   public Object __index(String arg) {
      if (arg == null) {
         return null;
      }

      return switch (arg) {
         case "press" -> this.press;
         case "release" -> this.release;
         default -> null;
      };
   }

   @LuaWhitelist
   public void __newindex(@LuaNotNil String key, Object value) {
      LuaFunction func = value instanceof LuaFunction f ? f : null;
      switch (key) {
         case "press":
            this.press = func;
            break;
         case "release":
            this.release = func;
            break;
         default:
            throw new LuaError("Cannot assign value on key \"" + key + "\"");
      }
   }

   @Override
   public String toString() {
      return this.name + " (" + this.key.getName() + ") (Keybind)";
   }
}

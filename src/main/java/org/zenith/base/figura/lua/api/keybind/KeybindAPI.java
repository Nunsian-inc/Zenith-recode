package org.zenith.base.figura.lua.api.keybind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaError;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "KeybindAPI", value = "keybinds")
public class KeybindAPI {
   public final List<FiguraKeybind> keyBindings = Collections.synchronizedList(new ArrayList<>());
   public final Avatar owner;

   public KeybindAPI(Avatar owner) {
      this.owner = owner;
   }

   private static KeyMapping findVanillaKey(String id) {
      for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
         if (key.getName().equals(id)) {
            return key;
         }
      }

      return null;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "name"),
            @LuaMethodOverload(argumentTypes = {String.class, String.class}, argumentNames = {"name", "key"}),
            @LuaMethodOverload(argumentTypes = {String.class, String.class, Boolean.class}, argumentNames = {"name", "key", "gui"})
      },
      aliases = "of",
      value = "keybinds.new_keybind"
   )
   public FiguraKeybind newKeybind(@LuaNotNil String name, String key, boolean gui) {
      if (key == null) {
         key = "key.keyboard.unknown";
      }

      FiguraKeybind binding = new FiguraKeybind(this.owner, name, FiguraKeybind.parseStringKey(key)).gui(gui);
      this.keyBindings.add(binding);
      return binding;
   }

   @LuaWhitelist
   public FiguraKeybind of(@LuaNotNil String name, String key, boolean gui) {
      return this.newKeybind(name, key, gui);
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "id"), value = "keybinds.from_vanilla")
   public FiguraKeybind fromVanilla(@LuaNotNil String id) {
      KeyMapping key = findVanillaKey(id);
      if (key == null) {
         throw new LuaError("Failed to find key: \"" + id + "\"");
      } else {
         return this.newKeybind("[Vanilla] " + key.getTranslatedKeyMessage().getString(), key.saveString(), false);
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "id"), value = "keybinds.get_vanilla_key")
   public String getVanillaKey(@LuaNotNil String id) {
      KeyMapping key = findVanillaKey(id);
      return key == null ? null : key.saveString();
   }

   @LuaWhitelist
   @LuaMethodDoc("keybinds.get_keybinds")
   public HashMap<String, FiguraKeybind> getKeybinds() {
      HashMap<String, FiguraKeybind> map = new HashMap<>();

      for (FiguraKeybind keyBinding : this.keyBindings) {
         map.put(keyBinding.getName(), keyBinding);
      }

      return map;
   }

   @Override
   public String toString() {
      return "KeybindAPI";
   }
}

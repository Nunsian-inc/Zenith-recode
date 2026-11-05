package org.zenith.base.figura.lua.api.sound;

import java.util.Collections;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "SoundAPI", value = "sounds")
public class SoundAPI {
   private final Avatar owner;

   public SoundAPI(Avatar owner) {
      this.owner = owner;
   }

   @LuaWhitelist
   public LuaSound playSound(@LuaNotNil String id, Object x, Double y, Double z, Double volume, Double pitch, Boolean loop) {
      return this.newSound(id).play();
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "id"), value = "sounds.is_present")
   public boolean isPresent(String id) {
      if (id == null) {
         return false;
      }

      Identifier identifier = Identifier.tryParse(id);
      return identifier != null && BuiltInRegistries.SOUND_EVENT.containsKey(identifier);
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "id"), value = "sounds.new_sound")
   public LuaSound newSound(@LuaNotNil String id) {
      return new LuaSound(id);
   }

   @LuaWhitelist
   @LuaMethodDoc("sounds.get_custom_sounds")
   public Set<String> getCustomSounds() {
      return Collections.emptySet();
   }

   @LuaWhitelist
   public LuaSound __index(@LuaNotNil String id) {
      return this.newSound(id);
   }

   @LuaWhitelist
   public void stopSound(String id) {
   }

   @Override
   public String toString() {
      return "SoundAPI";
   }
}

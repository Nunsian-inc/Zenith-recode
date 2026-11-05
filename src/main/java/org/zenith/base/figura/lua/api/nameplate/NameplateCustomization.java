package org.zenith.base.figura.lua.api.nameplate;

import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "NameplateCustomization", value = "nameplate_customization")
public class NameplateCustomization {
   private String text;

   @LuaWhitelist
   @LuaMethodDoc("nameplate_customization.get_text")
   public String getText() {
      return this.text;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"), value = "nameplate_customization.set_text")
   public NameplateCustomization setText(String text) {
      this.text = text;
      return this;
   }

   @LuaWhitelist
   public NameplateCustomization text(String text) {
      return this.setText(text);
   }

   @Override
   public String toString() {
      return "NameplateCustomization";
   }
}

package org.zenith.base.figura.lua.api.nameplate;

import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaFieldDoc;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "NameplateAPI", value = "nameplate")
public class NameplateAPI {
   @LuaWhitelist
   @LuaFieldDoc("nameplate.chat")
   public final NameplateCustomization CHAT = new NameplateCustomization();
   @LuaWhitelist
   @LuaFieldDoc("nameplate.entity")
   public final EntityNameplateCustomization ENTITY = new EntityNameplateCustomization();
   @LuaWhitelist
   @LuaFieldDoc("nameplate.list")
   public final NameplateCustomization LIST = new NameplateCustomization();
   @LuaWhitelist
   @LuaFieldDoc("nameplate.all")
   public final NameplateCustomizationGroup ALL = new NameplateCustomizationGroup(this.CHAT, this.ENTITY, this.LIST);

   @LuaWhitelist
   public Object __index(String key) {
      if (key == null) {
         return null;
      }

      String var2 = key.toUpperCase();

      return switch (var2) {
         case "CHAT" -> this.CHAT;
         case "ENTITY" -> this.ENTITY;
         case "LIST" -> this.LIST;
         case "ALL" -> this.ALL;
         default -> null;
      };
   }

   @Override
   public String toString() {
      return "NameplateAPI";
   }
}

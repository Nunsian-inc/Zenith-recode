package org.zenith.base.figura.lua.api.json;

import com.google.gson.GsonBuilder;
import org.luaj.vm2.LuaValue;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaFieldDoc;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "JsonBuilder", value = "json_builder")
public class FiguraJsonBuilder {
   @LuaFieldDoc("json_builder.pretty_printing")
   public boolean prettyPrinting = false;
   @LuaFieldDoc("json_builder.html_escaping")
   public boolean htmlEscaping = true;
   @LuaFieldDoc("json_builder.serialize_nils")
   public boolean serializeNils = false;

   @LuaWhitelist
   @LuaMethodDoc("json_builder.build")
   public FiguraJsonSerializer build() {
      GsonBuilder builder = new GsonBuilder();
      if (this.prettyPrinting) {
         builder.setPrettyPrinting();
      }

      if (!this.htmlEscaping) {
         builder.disableHtmlEscaping();
      }

      if (this.serializeNils) {
         builder.serializeNulls();
      }

      return new FiguraJsonSerializer(builder.create());
   }

   @LuaWhitelist
   public Object __index(LuaValue k) {
      Boolean prettyPrinting;
      if (k.type() != 4) {
         prettyPrinting = null;
      } else {
         String checkjstring = k.checkjstring();
         switch (checkjstring) {
            case "prettyPrinting":
               prettyPrinting = this.prettyPrinting;
               break;
            case "htmlEscaping":
               prettyPrinting = this.htmlEscaping;
               break;
            case "serializeNils":
               prettyPrinting = this.serializeNils;
               break;
            default:
               prettyPrinting = null;
         }
      }

      return prettyPrinting;
   }

   @LuaWhitelist
   public void __newindex(LuaValue k, LuaValue v) {
      if (k.type() == 4) {
         String checkjstring = k.checkjstring();
         switch (checkjstring) {
            case "prettyPrinting":
               this.prettyPrinting = v.checkboolean();
               break;
            case "htmlEscaping":
               this.htmlEscaping = v.checkboolean();
               break;
            case "serializeNils":
               this.serializeNils = v.checkboolean();
         }
      }
   }

   @Override
   public String toString() {
      return "JsonBuilder";
   }
}

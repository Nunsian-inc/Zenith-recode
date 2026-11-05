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
      Boolean var10000;
      if (k.type() != 4) {
         var10000 = null;
      } else {
         String var2 = k.checkjstring();
         switch (var2) {
            case "prettyPrinting":
               var10000 = this.prettyPrinting;
               break;
            case "htmlEscaping":
               var10000 = this.htmlEscaping;
               break;
            case "serializeNils":
               var10000 = this.serializeNils;
               break;
            default:
               var10000 = null;
         }
      }

      return var10000;
   }

   @LuaWhitelist
   public void __newindex(LuaValue k, LuaValue v) {
      if (k.type() == 4) {
         String var3 = k.checkjstring();
         switch (var3) {
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

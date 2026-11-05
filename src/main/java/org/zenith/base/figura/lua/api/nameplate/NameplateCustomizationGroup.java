package org.zenith.base.figura.lua.api.nameplate;

import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "NameplateCustomizationGroup", value = "nameplate_group")
public class NameplateCustomizationGroup {
   private final NameplateCustomization[] customizations;

   public NameplateCustomizationGroup(NameplateCustomization... customizations) {
      this.customizations = customizations;
   }

   @LuaWhitelist
   public NameplateCustomizationGroup setText(String text) {
      for (NameplateCustomization customization : this.customizations) {
         customization.setText(text);
      }

      return this;
   }

   @LuaWhitelist
   public NameplateCustomizationGroup text(String text) {
      return this.setText(text);
   }

   @Override
   public String toString() {
      return "NameplateCustomizationGroup";
   }
}

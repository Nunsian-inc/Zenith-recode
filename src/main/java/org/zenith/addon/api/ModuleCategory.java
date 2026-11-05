package org.zenith.addon.api;

public enum ModuleCategory {
   COMBAT("Combat"),
   MOVEMENT("Movement"),
   MISC("Misc"),
   RENDER("Visuals"),
   PLAYER("PvE");

   private final String displayName;

   ModuleCategory(String displayName) {
      this.displayName = displayName;
   }

   public String displayName() {
      return this.displayName;
   }
}

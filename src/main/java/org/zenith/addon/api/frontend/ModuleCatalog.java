package org.zenith.addon.api.frontend;

import java.util.List;
import java.util.Optional;

public interface ModuleCatalog {
   List<? extends ModuleAccess> modules();

   Optional<? extends ModuleAccess> find(String var1);

   default List<? extends ModuleAccess> modulesByOwner(String addonId) {
      return this.modules().stream().filter(module -> module.addonId().equals(addonId)).toList();
   }
}

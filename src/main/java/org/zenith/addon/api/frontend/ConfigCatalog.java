package org.zenith.addon.api.frontend;

import java.util.List;

public interface ConfigCatalog {
   List<ConfigInfo> configs();

   boolean save(String var1, ConfigSaveOptions var2);

   default boolean save(String name) {
      return this.save(name, ConfigSaveOptions.all());
   }

   boolean load(String var1, ConfigLoadMode var2);

   default boolean load(String name) {
      return this.load(name, ConfigLoadMode.ALL);
   }

   boolean delete(String var1);

   boolean rename(String var1, String var2);
}

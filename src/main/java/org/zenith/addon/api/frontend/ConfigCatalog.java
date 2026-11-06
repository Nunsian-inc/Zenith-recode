package org.zenith.addon.api.frontend;

import java.util.List;

public interface ConfigCatalog {
   List<ConfigInfo> configs();

   boolean save(String name, ConfigSaveOptions options);

   default boolean save(String name) {
      return this.save(name, ConfigSaveOptions.all());
   }

   boolean load(String name, ConfigLoadMode loadMode);

   default boolean load(String name) {
      return this.load(name, ConfigLoadMode.ALL);
   }

   boolean delete(String name);

   boolean rename(String oldName, String newName);
}

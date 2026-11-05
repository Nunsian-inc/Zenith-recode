package org.zenith.addon.api.frontend;

import java.util.List;

public interface ConfigCatalog {
   List<ConfigInfo> configs();

   boolean save(String name, ConfigSaveOptions all);

   default boolean save(String name) {
      return this.save(name, ConfigSaveOptions.all());
   }

   boolean load(String name, ConfigLoadMode configLoadMode);

   default boolean load(String name) {
      return this.load(name, ConfigLoadMode.ALL);
   }

   boolean delete(String str);

   boolean rename(String str, String str2);
}

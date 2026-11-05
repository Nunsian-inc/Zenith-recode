package org.zenith.addon.api;

import org.zenith.addon.api.frontend.ConfigCatalog;
import org.zenith.addon.api.frontend.FriendCatalog;
import org.zenith.addon.api.frontend.ModuleCatalog;

public interface AddonContext {
   String addonId();

   boolean development();

   ModuleRegistrar modules();

   ModuleCatalog moduleCatalog();

   ConfigCatalog configs();

   FriendCatalog friends();
}

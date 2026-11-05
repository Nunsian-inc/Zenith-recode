package org.zenith.addon.api;

@FunctionalInterface
public interface ZenithAddon {
   void initialize(AddonContext addonContext) throws Exception;
}

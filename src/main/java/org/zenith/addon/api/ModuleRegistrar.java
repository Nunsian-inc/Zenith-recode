package org.zenith.addon.api;

@FunctionalInterface
public interface ModuleRegistrar {
   ModuleHandle register(ModuleDefinition moduleDefinition);
}

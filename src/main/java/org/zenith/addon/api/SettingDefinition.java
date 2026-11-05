package org.zenith.addon.api;

public interface SettingDefinition {
   String id();

   String name();

   String description();

   Object defaultValue();
}

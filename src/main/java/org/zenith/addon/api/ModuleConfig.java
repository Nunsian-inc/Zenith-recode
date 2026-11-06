package org.zenith.addon.api;

import java.util.Set;

public interface ModuleConfig {
   Set<String> keys();

   boolean contains(String id);

   void remove(String id);

   void setBoolean(String id, boolean value);

   void setInt(String id, int value);

   void setLong(String id, long value);

   void setDouble(String id, double value);

   void setString(String id, String value);

   boolean getBoolean(String id, boolean defaultValue);

   int getInt(String id, int defaultValue);

   long getLong(String id, long defaultValue);

   double getDouble(String id, double defaultValue);

   String getString(String id, String defaultValue);
}

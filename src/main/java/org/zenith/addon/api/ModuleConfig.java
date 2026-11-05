package org.zenith.addon.api;

import java.util.Set;

public interface ModuleConfig {
   Set<String> keys();

   boolean contains(String str);

   void remove(String str);

   void setBoolean(String id, boolean flag);

   void setInt(String id, int n);

   void setLong(String str, long l);

   void setDouble(String id, double d);

   void setString(String id, String str);

   boolean getBoolean(String id, boolean defaultValue);

   int getInt(String id, int defaultValue);

   long getLong(String str, long l);

   double getDouble(String id, double defaultValue);

   String getString(String id, String defaultValue);
}

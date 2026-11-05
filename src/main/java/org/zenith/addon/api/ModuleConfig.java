package org.zenith.addon.api;

import java.util.Set;

public interface ModuleConfig {
   Set<String> keys();

   boolean contains(String var1);

   void remove(String var1);

   void setBoolean(String var1, boolean var2);

   void setInt(String var1, int var2);

   void setLong(String var1, long var2);

   void setDouble(String var1, double var2);

   void setString(String var1, String var2);

   boolean getBoolean(String var1, boolean var2);

   int getInt(String var1, int var2);

   long getLong(String var1, long var2);

   double getDouble(String var1, double var2);

   String getString(String var1, String var2);
}

package org.zenith.addon.api;

public interface ModuleHandle {
   String id();

   boolean enabled();

   void enabled(boolean var1);

   int keyCode();

   void keyCode(int var1);

   Object setting(String var1);

   void setting(String var1, Object var2);
}

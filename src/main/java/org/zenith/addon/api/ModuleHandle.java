package org.zenith.addon.api;

public interface ModuleHandle {
   String id();

   boolean enabled();

   void enabled(boolean flag);

   int keyCode();

   void keyCode(int n);

   Object setting(String str);

   void setting(String str, Object obj);
}

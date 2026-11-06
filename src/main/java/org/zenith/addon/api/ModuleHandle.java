package org.zenith.addon.api;

public interface ModuleHandle {
   String id();

   boolean enabled();

   void enabled(boolean enabled);

   int keyCode();

   void keyCode(int keyCode);

   Object setting(String settingId);

   void setting(String settingId, Object value);
}

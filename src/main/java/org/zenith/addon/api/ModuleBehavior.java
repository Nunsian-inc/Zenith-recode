package org.zenith.addon.api;

import org.zenith.addon.api.render.AddonRenderContext;

public interface ModuleBehavior {
   default void save(ModuleConfig config) throws Exception {
   }

   default void load(ModuleConfig config) throws Exception {
   }

   default void onEnable(ModuleContext context) throws Exception {
   }

   default void onDisable(ModuleContext context) throws Exception {
   }

   default void onHudRender(ModuleContext context, AddonRenderContext render) throws Exception {
   }

   default void close(ModuleContext context) throws Exception {
   }
}

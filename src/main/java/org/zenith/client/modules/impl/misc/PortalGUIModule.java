package org.zenith.client.modules.impl.misc;

import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;

@ModuleInfo(name = "PortalGUI", description = "module.portalGui.description", category = ModuleCategory.MISC)
public final class PortalGUIModule extends Module {
   public static final PortalGUIModule INSTANCE = new PortalGUIModule();

   private PortalGUIModule() {
   }
}

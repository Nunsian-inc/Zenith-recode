package org.zenith.base.figura;

import net.fabricmc.api.ClientModInitializer;
import org.zenith.base.figura.lua.FiguraAPIManager;
import org.zenith.base.figura.lua.HeadlessActionWheelAPI;
import org.zenith.base.figura.lua.HeadlessConfigAPI;

public final class HeadlessCompatibilityEntrypoint implements ClientModInitializer {
   public void onInitializeClient() {
      FiguraAPIManager.API_GETTERS.put("action_wheel", figuraLuaRuntime -> HeadlessActionWheelAPI.create());
      FiguraAPIManager.API_GETTERS.put("config", figuraLuaRuntime -> HeadlessConfigAPI.create());
   }
}

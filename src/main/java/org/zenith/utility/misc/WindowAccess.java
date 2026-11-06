package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.Window;
import org.zenith.MinecraftAccess;

public interface WindowAccess extends MinecraftAccess {
   Window window = mc.getWindow();
}

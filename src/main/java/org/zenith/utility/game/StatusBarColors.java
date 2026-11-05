package org.zenith.utility.game;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class StatusBarColors {
   private static final int CONST_intValue = 10;
   private static final float CONST_floatValue = 8.0F;
   private static final float CONST_floatValue2 = 2.0F;
   private static final float CONST_floatValue3 = 1.0F;
   private static final float CONST_floatValue4 = 3.0F;
   private static final ZenithColor zenithColor = new ZenithColor(255, 193, 74, 255);
   private static final ZenithColor zenithColor2 = new ZenithColor(255, 107, 74, 255);

   private StatusBarColors() {
   }

   public static void method1755(GuiGraphicsExtractor context, Player player, int n, int n2) {
      if (player != null && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6796()) {
         float f = Math.max(0.0F, player.getFoodData().getSaturationLevel());
         float f2 = Math.min(1.0F, f / 20.0F);
         ZenithStyle style = HudListLayout.style();
         CustomDrawContext customDrawContext = new CustomDrawContext(context);
         float f3 = n2;
         float y = n - 3.0F;
         float width = 79.0F;
         float floatValue = f3 - 80.0F - 1.0F;
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(1.0F);
         customDrawContext.drawRoundedRect(floatValue, y, width, 2.0F, headerRadius, style.getHeaderHudBackground().getZenithColor().method7294(0.85F));
         if (!(f2 <= 0.0F)) {
            ZenithColor zenithColor22 = f2 < 0.15F ? zenithColor2 : zenithColor;
            customDrawContext.drawRoundedRect(floatValue, y, Math.max(2.0F, width * f2), 2.0F, headerRadius, zenithColor22);
         }
      }
   }
}

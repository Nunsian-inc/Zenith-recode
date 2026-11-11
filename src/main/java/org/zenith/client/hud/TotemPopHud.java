package org.zenith.client.hud;

import java.util.ArrayList;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.TotemPopModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class TotemPopHud extends Interface {
   public TotemPopHud(String text, float x, float y, float width, float height, float offsetX, float offsetY, Type type) {
      super(text, x, y, width, height, offsetX, offsetY, type);
      this.width = 150.0F;
      this.height = 26.0F;
   }

   @Override
   public void method8(CustomDrawContext context) {
      TotemPopModule module = TotemPopModule.INSTANCE;
      if (!module.isHudVisible() && !this.method2074()) return;
      var localization = Zenith.getInstance().getLocalization();
      var font = Fonts.NEW_MEDIUM.getFont(5.5F);
      var style = Zenith.getInstance().getStyleManager().getCurrentStyle();
      var counts = module.getCounts();
      var rows = new ArrayList<String>();
      for (var count : counts.stream().limit(8).toList()) rows.add(count.name() + "  " + count.count());
      if (counts.size() > 8) rows.add(localization.translate("hud.totemPop.more").replace("{count}", Integer.toString(counts.size() - 8)));
      if (rows.isEmpty()) rows.add(localization.translate("hud.totemPop.empty"));
      String title = localization.translate("hud.totemPop.title");
      this.width = Math.max(150.0F, font.width(title) + 16.0F);
      for (String row : rows) this.width = Math.max(this.width, font.width(row) + 16.0F);
      this.height = 24.0F + rows.size() * 13.0F;
      float radius = InterfaceModule.method6801();
      context.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(radius), ZenithColor.INSTANCE);
      context.drawRoundedRect(this.floatValue, this.floatValue2, this.width, this.height, ProxyDotRadiusData.method8069(radius), style.getHudBackground().getZenithColor());
      context.drawText(font, title, this.floatValue + 8.0F, this.floatValue2 + 7.0F, style.getPrimaryColor().getZenithColor());
      for (int i = 0; i < rows.size(); i++) {
         context.drawText(font, rows.get(i), this.floatValue + 8.0F, this.floatValue2 + 23.0F + i * 13.0F, style.getTextEnable().getZenithColor());
      }
   }

   @Override
   public float method254() {
      return TotemPopModule.INSTANCE.isHudVisible() || this.method2074() ? 1.0F : 0.0F;
   }
}

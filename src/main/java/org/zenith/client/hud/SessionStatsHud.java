package org.zenith.client.hud;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import org.zenith.KeyEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.utility.misc.KeyName;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.SessionStats;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class SessionStatsHud extends Interface {
   private final BindSetting resetKey = new BindSetting("hud.session.resetKey");
   private final ButtonSetting resetStats = new ButtonSetting("hud.session.reset", () -> {
      SessionStats stats = Zenith.getInstance().getSessionStats();
      if (stats != null) stats.resetCounters();
   });

   @EventTarget
   public void onResetKey(KeyEvent event) {
      if (mc.gui.screen() == null && event.getIntValue() == 1 && resetKey.getKeyCode() >= 0
         && KeyName.method6703(resetKey.getKeyCode(), resetKey.getModifiers(), event.getKeyCode(), event.getModifiers())) {
         SessionStats stats = Zenith.getInstance().getSessionStats();
         if (stats != null) stats.resetCounters();
      }
   }
   public SessionStatsHud(String text, float x, float y, float width, float height, float offsetX, float offsetY, Type type) {
      super(text, x, y, width, height, offsetX, offsetY, type);
      this.width = 150.0F;
      this.height = 26.0F;
      EventManager.register(this);
   }

   @Override
   public void method8(CustomDrawContext context) {
      SessionStats stats = Zenith.getInstance().getSessionStats();
      if (stats == null) return;
      ZenithStyle style = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font label = Fonts.NEW_MEDIUM.getFont(5.5F);
      String kills = Zenith.getInstance().getLocalization().translate("hud.session.kills");
      String deaths = Zenith.getInstance().getLocalization().translate("hud.session.deaths");
      String text = kills + " " + stats.getKills() + "  " + deaths + " " + stats.getDeaths() + "  " + formatTime(stats.getSeconds());
      float padding = GuiStyle.PADDING.intValue();
      this.width = Math.max(150.0F, label.width(text) + padding * 2.0F + 26.0F);
      float radius = InterfaceModule.method6801();
      context.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(radius), ZenithColor.INSTANCE);
      context.drawRoundedRect(this.floatValue, this.floatValue2, this.width, this.height, ProxyDotRadiusData.method8069(radius), style.getHudBackground().getZenithColor());
      context.drawRoundedRect(this.floatValue, this.floatValue2, 26.0F, this.height, ProxyDotRadiusData.method8078(radius, radius), style.getHeaderHudBackground().getZenithColor());
      String icon = "S";
      context.drawText(Fonts.NEW_ICONS.getFont(6.2F), icon, this.floatValue + 9.0F, this.floatValue2 + 8.0F, style.getPrimaryColor().getZenithColor());
      context.drawText(label, text, this.floatValue + 26.0F + padding, this.floatValue2 + 8.0F, style.getTextEnable().getZenithColor());
   }

   private static String formatTime(long seconds) {
      long hours = seconds / 3600L;
      long minutes = seconds / 60L % 60L;
      return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
   }

   @Override
   public float method254() { return 1.0F; }
}

package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class InfoBoxesHud extends Interface {
   private final MultiSelectSetting infoBoxes = new MultiSelectSetting("hud.infoBoxes", "hud.infoBoxes.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.infoBoxes, "TPS", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.infoBoxes, "Ping", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.infoBoxes, "FPS", true);

   public InfoBoxesHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      if (mc.player == null) {
         this.width = 0.0F;
         this.height = 0.0F;
      } else {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle == null) {
            this.width = 0.0F;
            this.height = 0.0F;
         } else {
            String str = String.format(Locale.US, "%.2f", Zenith.getInstance().getServerDetector().method1624());
            String str2 = this.method2260();
            String str3 = String.valueOf(Minecraft.getInstance().getFps());
            boolean flag = !this.entry.isEnabled() && !this.entry2.isEnabled() && !this.entry3.isEnabled();
            ArrayList arrayList = new ArrayList(3);
            if (flag || this.entry.isEnabled()) {
               arrayList.add(HudListLayout.Data.method7662("h", str, " tps"));
            }

            if (flag || this.entry2.isEnabled()) {
               arrayList.add(HudListLayout.Data.method7662("H", str2, " ms"));
            }

            if (flag || this.entry3.isEnabled()) {
               arrayList.add(HudListLayout.Data.method7662("F", str3, " fps"));
            }

            this.width = HudListLayout.method7658(arrayList);
            this.height = arrayList.isEmpty() ? 0.0F : 17.0F;
            HudListLayout.method7659(customDrawContext, this.floatValue, this.floatValue2, arrayList);
         }
      }
   }

   private String method2260() {
      if (mc.player != null && mc.getConnection() != null) {
         PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(mc.player.getUUID());
         return playerInfo == null ? "-" : String.valueOf(Math.max(0, playerInfo.getLatency()));
      } else {
         return "-";
      }
   }
}

package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class InfoBoxesHud2 extends Interface {
   private final MultiSelectSetting infoBoxes = new MultiSelectSetting("hud.infoBoxes", "hud.infoBoxes.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.infoBoxes, "hud.infoBoxes.coordinates", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.infoBoxes, "BPS", true);

   public InfoBoxesHud2(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
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
            double d = Math.hypot(mc.player.getX() - mc.player.xo, mc.player.getZ() - mc.player.zo) * 20.0;
            int x = (int)mc.player.getX();
            int y = (int)mc.player.getY();
            int z = (int)mc.player.getZ();
            if (ScoreboardParser.isActive()) {
               x = ScoreboardParser.method7069(x);
               z = ScoreboardParser.method7070(z);
            }

            String str = String.format(Locale.US, "%.2f", d);
            boolean flag = !this.entry.isEnabled() && !this.entry2.isEnabled();
            ArrayList arrayList = new ArrayList(2);
            if (flag || this.entry.isEnabled()) {
               arrayList.add(
                  new HudListLayout.Data(
                     "Y",
                     List.of(
                        HudListLayout.Data.method7660(String.valueOf(x)),
                        HudListLayout.Data.method7661("x "),
                        HudListLayout.Data.method7660(String.valueOf(y)),
                        HudListLayout.Data.method7661("y "),
                        HudListLayout.Data.method7660(String.valueOf(z)),
                        HudListLayout.Data.method7661("z")
                     )
                  )
               );
            }

            if (flag || this.entry2.isEnabled()) {
               arrayList.add(HudListLayout.Data.method7662("4", str, " bps"));
            }

            this.width = HudListLayout.method7658(arrayList);
            this.height = arrayList.isEmpty() ? 0.0F : 17.0F;
            HudListLayout.method7659(customDrawContext, this.floatValue, this.floatValue2, arrayList);
         }
      }
   }
}

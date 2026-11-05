package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class Hud2 extends Interface {
   private final List<Hud2.Entry> items = new ArrayList<>();

   public Hud2(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
      float height = 22.0F;
      this.width = height * 4.0F;
      this.height = height;

      for (int i = 0; i < 4; i++) {
         this.items.add(new Hud2.Entry(i));
      }
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      float height = 22.0F;
      this.width = height * 4.0F;
      this.height = height;
      float intValue = InterfaceModule.method6801();
      customDrawContext.pushMatrix();
      customDrawContext.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
      customDrawContext.drawRoundedRect(
         this.floatValue,
         this.floatValue2,
         this.width,
         this.height,
         ProxyDotRadiusData.method8069(intValue),
         currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
      );
      float floatValue = this.floatValue;
      float f = this.floatValue2;

      for (Hud2.Entry entry : this.items) {
         entry.method1529(customDrawContext, floatValue, f, currentStyle);
         floatValue += height;
      }

      customDrawContext.popMatrix();
   }

   public class Entry {
      private final int intValue;

      private Entry(int intValue) {
         this.intValue = intValue;
      }

      public void method1529(CustomDrawContext customDrawContext, float floatValue, float f, ZenithStyle currentStyle) {
         float intValue = InterfaceModule.method6801();
         ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(intValue);
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor();
         ItemStack itemStack = ArmorSlots.method5384(MinecraftAccess.mc.player).get(3 - this.intValue);
         if (!itemStack.isEmpty()) {
            customDrawContext.pushMatrix();
            customDrawContext.pose().translate((float)(floatValue + 4.6), (float)(f + 4.6));
            customDrawContext.pose().scale(0.8F, 0.8F);
            customDrawContext.item(itemStack, 0, 0);
            customDrawContext.renderItemBar(itemStack, 0, 0);
            customDrawContext.renderItemCooldown(itemStack, 0, 0);
            customDrawContext.popMatrix();
            if (itemStack.getCount() > 1) {
               String text = "x" + itemStack.getCount();
               float width = font.width(text);
               float floatValue2 = floatValue + 22.0F - width - 3.0F;
               float centeredTextY = f + 22.0F - font.height() - 3.0F;
               customDrawContext.drawText(font, text, floatValue2, centeredTextY, zenithColor);
            }
         } else {
            Font font2 = Fonts.ICONS.getFont(4.5F);
            customDrawContext.drawText(
               font2,
               "M",
               floatValue + (22.0F - font2.width("M")) / 2.0F,
               f + (22.0F - font2.height()) / 2.0F,
               Zenith.getInstance().getStyleManager().getCurrentStyle().getTextTertiary().getZenithColor()
            );
         }
      }
   }
}

package org.zenith.client.hud;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class Hud1 extends Interface {
   private static final float CONST_floatValue = 22.0F;
   private static final ZenithColor zenithColor = new ZenithColor(80, 220, 120, 255);
   private static final ZenithColor zenithColor2 = new ZenithColor(255, 200, 60, 255);
   private static final ZenithColor zenithColor3 = new ZenithColor(255, 70, 70, 255);
   // 26.3: an ItemStack can only be built after item components are bound, i.e. not during mod init
   private static ItemStack itemStack;

   public Hud1(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
      this.width = 44.0F;
      this.height = 22.0F;
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      if (mc.player != null) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         this.width = 44.0F;
         this.height = 22.0F;
         float intValue = InterfaceModule.method6801();
         int n = 0;

         for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
               n += stack.getCount();
            }
         }

         boolean is = mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);
         ZenithColor zenithColor22 = AutoTotemModule.INSTANCE.isFlag() ? zenithColor2 : (is && AutoTotemModule.INSTANCE.method707() ? zenithColor : zenithColor3);
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
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(this.floatValue + 4.6F, this.floatValue2 + 4.6F);
         customDrawContext.pose().scale(0.8F, 0.8F);
         if (itemStack == null) {
            itemStack = new ItemStack(Items.TOTEM_OF_UNDYING);
         }

         customDrawContext.item(itemStack, 0, 0);
         customDrawContext.popMatrix();
         Font font = Fonts.NEW_MEDIUM.getFont(7.0F);
         String text = String.valueOf(n);
         customDrawContext.drawText(
            font,
            text,
            this.floatValue + 22.0F + (22.0F - font.width(text)) / 2.0F,
            this.floatValue2 + (22.0F - font.height()) / 2.0F,
            n > 0 ? currentStyle.getTextEnable().getZenithColor() : zenithColor3
         );
         customDrawContext.drawRoundedRect(this.floatValue + 3.0F, this.floatValue2 + 22.0F - 3.0F, this.width - 6.0F, 1.5F, ProxyDotRadiusData.method8069(0.75F), zenithColor22);
         customDrawContext.popMatrix();
      }
   }
}

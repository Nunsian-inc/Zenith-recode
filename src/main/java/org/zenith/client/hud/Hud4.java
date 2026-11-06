package org.zenith.client.hud;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class Hud4 extends Interface {
   private final Animation animation = new Animation(300L, Easing.INSTANCE25);
   private final Animation animation2 = new Animation(150L, Easing.INSTANCE25);
   private final Item[] items = new Item[27];
   private final int[] intValues = new int[27];
   private float lastWidth = 0.0F;
   private float lastHeight = 0.0F;

   public Hud4(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      if (mc.player == null) {
         this.animation.animateTo(0.0F);
         if (this.animation.getValue() > 0.01F) {
            this.method6476(customDrawContext, this.animation.getValue());
         }
      } else {
         this.animation.animateTo(1.0F);
         if (!(this.animation.getValue() <= 0.01F)) {
            boolean flag = false;

            for (int i = 9; i < 36; i++) {
               ItemStack stack = mc.player.getInventory().getItem(i);
               Item item = stack.getItem();
               int count = stack.getCount();
               int n = i - 9;
               if (item != this.items[n] || count != this.intValues[n]) {
                  this.items[n] = item;
                  this.intValues[n] = count;
                  flag = true;
               }
            }

            if (flag) {
               this.animation2.animateTo(0.0F);
            }

            this.animation2.animateTo(1.0F);
            this.method6476(customDrawContext, this.animation.getValue() * this.animation2.getValue());
         }
      }
   }

   private void method6476(CustomDrawContext customDrawContext, float x) {
      if (mc.player != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
         Font font2 = Fonts.ICONS.getFont(4.5F);
         float f = 14.0F;
         float f2 = 1.0F;
         float f3 = 4.0F;
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         byte b = 9;
         byte b2 = 3;
         float width = b * f + (b - 1) * f2;
         float height = b2 * f + (b2 - 1) * 0.5F;
         this.width = width;
         this.height = height;
         float intValue = InterfaceModule.method6801();
         this.lastWidth = this.width;
         this.lastHeight = this.height;
         customDrawContext.pose().pushMatrix();
         customDrawContext.pose().translate(this.floatValue + this.width / 2.0F, this.floatValue2 + this.height / 2.0F);
         customDrawContext.pose().scale(x, x);
         customDrawContext.pose().translate(-(this.floatValue + this.width / 2.0F), -(this.floatValue2 + this.height / 2.0F));
         customDrawContext.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
         customDrawContext.drawRoundedRect(
            this.floatValue,
            this.floatValue2,
            this.width,
            this.height,
            ProxyDotRadiusData.method8069(intValue),
            currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
         );
         float f4 = (f - font2.width("X")) / 2.0F;

         for (int i = 0; i < b2; i++) {
            for (int j = 0; j < b; j++) {
               int slot = 9 + i * 9 + j;
               ItemStack itemStack = mc.player.getInventory().getItem(slot);
               float f5 = this.floatValue + j * (f + f2);
               float f6 = this.floatValue2 + i * (f + 0.5F);
               float intValue2 = Math.min(intValue, f / 2.0F);
               ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(intValue2);
               if (!itemStack.isEmpty()) {
                  float f7 = 8.0F;
                  float x2 = f7 / 16.0F;
                  customDrawContext.pushMatrix();
                  customDrawContext.pose().translate(f5 + (f - f7) / 2.0F, f6 + (f - f7) / 2.0F);
                  customDrawContext.pose().scale(x2, x2);
                  customDrawContext.item(itemStack, 0, 0);
                  customDrawContext.renderItemBar(itemStack, 0, 0);
                  customDrawContext.renderItemCooldown(itemStack, 0, 0);
                  customDrawContext.popMatrix();
                  if (itemStack.getCount() > 1) {
                     String text = "x" + itemStack.getCount();
                     float width2 = font.width(text);
                     float floatValue = f5 + f - width2 - 1.5F;
                     float centeredTextY = f6 + f - font.height() - 1.0F;
                     customDrawContext.drawText(font, text, floatValue, centeredTextY, currentStyle.getTextEnable().getZenithColor());
                  }
               } else {
                  customDrawContext.drawText(
                     font2,
                     "M",
                     f5 + f4,
                     f6 + (f - font2.height()) / 2.0F,
                     Zenith.getInstance().getStyleManager().getCurrentStyle().getTextTertiary().getZenithColor()
                  );
               }
            }
         }

         customDrawContext.pose().popMatrix();
      }
   }
}

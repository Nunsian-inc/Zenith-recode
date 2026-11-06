package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "SelfFlag",
   category = ModuleCategory.MISC,
   description = "module.selfflag.description"
)
public class SelfFlagModule extends Module {
   public static final SelfFlagModule INSTANCE = new SelfFlagModule();
   private final BooleanSetting overlay = new BooleanSetting("module.selfFlag.overlay", true);
   private final SliderSetting x = new SliderSetting("module.selfFlag.x", 6.0F, 0.0F, 400.0F, 1.0F);
   private final SliderSetting y = new SliderSetting("module.selfFlag.y", 60.0F, 0.0F, 400.0F, 1.0F);
   private final BooleanSetting warn = new BooleanSetting("module.selfFlag.warn", true);
   private static final float CONST_floatValue = 12.0F;
   private static final float CONST_floatValue2 = 30.0F;
   private static final float CONST_floatValue3 = 220.0F;
   private static final float CONST_floatValue4 = 500.0F;
   private static final float CONST_floatValue5 = 3.0F;
   private static final float CONST_floatValue6 = 3.6F;
   private static final long CONST_longValue = 3000L;
   private float floatValue;
   private float floatValue2;
   private boolean flag;
   private long longValue;
   private final Deque<long[]> deque = new ArrayDeque<>();
   private final Deque<float[]> deque2 = new ArrayDeque<>();
   private long longValue2;

   private SelfFlagModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag = false;
      this.longValue = 0L;
      this.deque.clear();
      this.deque2.clear();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null) {
         float yRot = mc.player.getYRot();
         float xRot = mc.player.getXRot();
         if (this.flag) {
            float f = Math.abs(this.method7982(yRot - this.floatValue));
            long now = System.currentTimeMillis();
            this.deque.addLast(new long[]{now, (long)(f * 100.0F)});

            while (!this.deque.isEmpty() && now - ((long[])this.deque.peekFirst())[0] > 1000L) {
               this.deque.removeFirst();
            }
         }

         this.floatValue = yRot;
         this.floatValue2 = xRot;
         this.flag = true;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (!event.isIncoming() && mc.player != null) {
         Packet packet3 = event.getPacket();
         if (packet3 instanceof ServerboundInteractPacket || packet3 instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) {
            long now = System.currentTimeMillis();
            float f = this.flag ? Math.abs(this.method7982(mc.player.getYRot() - this.floatValue)) : 0.0F;
            float f2 = this.longValue == 0L ? -1.0F : (float)(now - this.longValue);
            this.longValue = now;
            float f3 = mc.crosshairPickEntity != null ? mc.player.distanceTo(mc.crosshairPickEntity) : -1.0F;
            float f4 = mc.crosshairPickEntity != null ? 1.0F : 0.0F;
            this.deque2.addLast(new float[]{(float)now, f, f2, f3, f4});

            while (this.deque2.size() > 12) {
               this.deque2.removeFirst();
            }

            if (this.warn.isEnabled() && this.method7979() >= 70.0F && now - this.longValue2 > 4000L) {
               this.longValue2 = now;
               Zenith.getInstance().getHudHolder().method6972("!", Component.nullToEmpty("SelfFlag: риск палева " + (int)this.method7979() + "%"));
            }
         }
      }
   }

   private float method7972() {
      long l = 0L;

      for (long[] longs : this.deque) {
         l += longs[1];
      }

      return (float)l / 100.0F;
   }

   private float method7973(float f, float f2, float f3) {
      if (f <= f2) {
         return 0.0F;
      } else {
         return f >= f3 ? 100.0F : (f - f2) / (f3 - f2) * 100.0F;
      }
   }

   private float method7974() {
      float f = 0.0F;

      for (float[] floats : this.deque2) {
         if (this.method7978(floats) < 3000L) {
            f = Math.max(f, floats[1]);
         }
      }

      return this.method7973(f, 12.0F, 30.0F);
   }

   private float method7975() {
      float f = 0.0F;
      int n = 0;

      for (float[] floats : this.deque2) {
         if (floats[2] > 0.0F && this.method7978(floats) < 9000L) {
            f += floats[2];
            n++;
         }
      }

      if (n < 4) {
         return 0.0F;
      } else {
         f /= n;
         float f2 = 0.0F;

         for (float[] floats : this.deque2) {
            if (floats[2] > 0.0F && this.method7978(floats) < 9000L) {
               f2 += (floats[2] - f) * (floats[2] - f);
            }
         }

         float f3 = (float)Math.sqrt(f2 / n) / Math.max(1.0F, f);
         return this.method7973(0.5F - f3, 0.0F, 0.35F);
      }
   }

   private float method7976() {
      float f = 0.0F;

      for (float[] floats : this.deque2) {
         if (floats[3] > 0.0F && this.method7978(floats) < 3000L) {
            f = Math.max(f, floats[3]);
         }
      }

      return this.method7973(f, 3.0F, 3.6F);
   }

   private float method7977() {
      int n = 0;
      int n2 = 0;

      for (float[] floats : this.deque2) {
         if (this.method7978(floats) < 3000L) {
            n2++;
            if (floats[4] < 0.5F) {
               n++;
            }
         }
      }

      return n2 == 0 ? 0.0F : (float)n / n2 * 100.0F;
   }

   private long method7978(float[] floats) {
      return System.currentTimeMillis() - (long)floats[0];
   }

   private boolean active() {
      return this.longValue != 0L && System.currentTimeMillis() - this.longValue < 3000L;
   }

   public float method7979() {
      if (!this.active()) {
         return 0.0F;
      } else {
         float f = this.method7974();
         float f2 = this.method7973(this.method7972(), 220.0F, 500.0F);
         float f3 = this.method7975();
         float f4 = this.method7976();
         float f5 = this.method7977();
         return Math.min(100.0F, f * 0.3F + f2 * 0.2F + f3 * 0.2F + f4 * 0.2F + f5 * 0.1F);
      }
   }

   @EventTarget
   public void method7980(DrawEvent event) {
      if (this.overlay.isEnabled()) {
         CustomDrawContext customDrawContext = event.getCustomDrawContext();
         if (customDrawContext != null) {
            ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
            Font font = Fonts.NEW_MEDIUM.getFont(7.0F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(6.0F);
            float floatValue = this.x.getValue3();
            float y = this.y.getValue3();
            float width = 150.0F;
            float f = 11.0F;
            float height = 20.0F + f * 5.0F + 6.0F;
            ZenithColor zenithColor = currentStyle.getHudBackground().getZenithColor();
            customDrawContext.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(6.0F), zenithColor);
            float f2 = this.method7979();
            ZenithColor zenithColor2 = f2 >= 70.0F
               ? new ZenithColor(239, 83, 83)
               : (f2 >= 40.0F ? new ZenithColor(242, 169, 59) : new ZenithColor(51, 214, 166));
            customDrawContext.drawText(font, "DETECT RISK", floatValue + 8.0F, y + 7.0F, currentStyle.getTextEnable().getZenithColor());
            customDrawContext.drawText(font, (int)f2 + "%", floatValue + width - 8.0F - font.width((int)f2 + "%"), y + 7.0F, zenithColor2);
            float centeredTextY = y + 20.0F;
            this.method7981(customDrawContext, font2, "Snap  ", this.method7974(), floatValue, centeredTextY, width, currentStyle);
            this.method7981(customDrawContext, font2, "Spin  ", this.method7973(this.method7972(), 220.0F, 500.0F), floatValue, centeredTextY + f, width, currentStyle);
            this.method7981(customDrawContext, font2, "Ритм  ", this.method7975(), floatValue, centeredTextY + f * 2.0F, width, currentStyle);
            this.method7981(customDrawContext, font2, "Reach ", this.method7976(), floatValue, centeredTextY + f * 3.0F, width, currentStyle);
            this.method7981(customDrawContext, font2, "МимоПрицела", this.method7977(), floatValue, centeredTextY + f * 4.0F, width, currentStyle);
         }
      }
   }

   private void method7981(CustomDrawContext customDrawContext, Font font, String text, float f, float floatValue, float centeredTextY, float width, ZenithStyle currentStyle) {
      customDrawContext.drawText(font, text, floatValue + 8.0F, centeredTextY, currentStyle.getTextSecondary().getZenithColor());
      float floatValue2 = floatValue + 62.0F;
      float width2 = width - 62.0F - 30.0F;
      customDrawContext.drawRoundedRect(floatValue2, centeredTextY + 1.5F, width2, 4.0F, ProxyDotRadiusData.method8069(2.0F), currentStyle.getTextTertiary().getZenithColor().method7294(0.25F));
      ZenithColor zenithColor = f >= 70.0F ? new ZenithColor(239, 83, 83) : (f >= 40.0F ? new ZenithColor(242, 169, 59) : new ZenithColor(51, 214, 166));
      customDrawContext.drawRoundedRect(floatValue2, centeredTextY + 1.5F, Math.max(1.0F, width2 * f / 100.0F), 4.0F, ProxyDotRadiusData.method8069(2.0F), zenithColor);
      customDrawContext.drawText(font, (int)f + "", floatValue + width - 8.0F - font.width((int)f + ""), centeredTextY, currentStyle.getTextSecondary().getZenithColor());
   }

   private float method7982(float f) {
      while (f > 180.0F) {
         f -= 360.0F;
      }

      while (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }
}

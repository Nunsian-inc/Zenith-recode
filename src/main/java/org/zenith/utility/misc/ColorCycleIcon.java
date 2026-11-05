package org.zenith.utility.misc;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.utility.render.display.base.GlowColor;

public class ColorCycleIcon {
   private final Animation animation;
   private float floatValue;

   public ColorCycleIcon(long duration) {
      this.animation = new Animation(duration, Easing.INSTANCE10);
   }

   public void update() {
      this.animation.method2808(1.0F);
      this.floatValue = this.animation.getValue() * 360.0F;
      if (this.floatValue >= 360.0F) {
         this.floatValue = 0.0F;
         this.animation.reset();
      }
   }

   public GlowColor method806() {
      ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
      return this.method807(zenithColor);
   }

   public GlowColor method807(ZenithColor zenithColor) {
      return this.method808(zenithColor, zenithColor.method7294(0.4F));
   }

   public GlowColor method808(ZenithColor zenithColor, ZenithColor zenithColor2) {
      float hover = (float)((Math.sin(Math.toRadians(this.floatValue)) + 1.0) / 2.0);
      float f = (float)((Math.sin(Math.toRadians(this.floatValue + 90.0F)) + 1.0) / 2.0);
      float hover2 = (float)((Math.sin(Math.toRadians(this.floatValue + 180.0F)) + 1.0) / 2.0);
      float hover3 = (float)((Math.sin(Math.toRadians(this.floatValue + 270.0F)) + 1.0) / 2.0);
      ZenithColor clientColor = zenithColor.method7295(zenithColor2, hover);
      ZenithColor zenithColor3 = zenithColor.method7295(zenithColor2, f);
      ZenithColor zenithColor4 = zenithColor.method7295(zenithColor2, hover2);
      ZenithColor zenithColor22 = zenithColor.method7295(zenithColor2, hover3);
      return GlowColor.method8096(clientColor, zenithColor22, zenithColor3, zenithColor4);
   }
}

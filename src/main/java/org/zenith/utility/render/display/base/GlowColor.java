package org.zenith.utility.render.display.base;

import java.util.List;
import lombok.Generated;
import org.zenith.ZenithColor;

public class GlowColor {
   public final ZenithColor zenithColor;
   public final ZenithColor zenithColor2;
   public final ZenithColor zenithColor3;
   public final ZenithColor zenithColor4;

   public GlowColor(ZenithColor zenithColor, ZenithColor zenithColor2, ZenithColor zenithColor3, ZenithColor zenithColor4) {
      this.zenithColor = zenithColor;
      this.zenithColor2 = zenithColor2;
      this.zenithColor3 = zenithColor3;
      this.zenithColor4 = zenithColor4;
   }

   public static GlowColor method8096(ZenithColor clientColor, ZenithColor zenithColor2, ZenithColor zenithColor3, ZenithColor zenithColor4) {
      return new GlowColor(clientColor, zenithColor2, zenithColor3, zenithColor4);
   }

   public static GlowColor method8097(ZenithColor zenithColor) {
      return new GlowColor(zenithColor, zenithColor, zenithColor, zenithColor);
   }

   public static GlowColor method8098(List<ZenithColor> zenithColors) {
      return new GlowColor((ZenithColor)zenithColors.get(0), (ZenithColor)zenithColors.get(1), (ZenithColor)zenithColors.get(2), (ZenithColor)zenithColors.get(3));
   }

   public GlowColor method8099() {
      return this;
   }

   public GlowColor method8100(float alpha) {
      return new GlowColor(
         this.zenithColor.method7294(alpha), this.zenithColor2.method7294(alpha), this.zenithColor3.method7294(alpha), this.zenithColor4.method7294(alpha)
      );
   }

   @Generated
   public ZenithColor getZenithColor() {
      return this.zenithColor;
   }

   @Generated
   public ZenithColor getZenithColor2() {
      return this.zenithColor2;
   }

   @Generated
   public ZenithColor getZenithColor3() {
      return this.zenithColor3;
   }

   @Generated
   public ZenithColor getZenithColor4() {
      return this.zenithColor4;
   }
}

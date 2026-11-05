package org.zenith.client.screens.override.main.widget;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import lombok.Generated;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuSlider extends MenuWidget {
   public static final float HEIGHT = MenuSkin.HEADER_HEIGHT;
   private static final float RADIUS = MenuSkin.radius(HEIGHT);
   private static final float PADDING = MenuTheme.px(16.0F);
   private static final float GAP = MenuTheme.px(16.0F);
   private static final float TRACK_HEIGHT = MenuTheme.px(4.0F);
   private static final float KNOB_SIZE = MenuTheme.px(6.0F);
   private static final float TEXT_SIZE = 12.0F;
   private final String label;
   private final DoubleSupplier min;
   private final DoubleSupplier max;
   private final double step;
   private final DoubleSupplier getter;
   private final DoubleConsumer setter;
   private final Function<Double, String> formatter;
   private boolean dragging;

   public MenuSlider(String label, double d, double d2, double step, DoubleSupplier getter, DoubleConsumer setter, Function<Double, String> formatter) {
      this(label, () -> d, () -> d2, step, getter, setter, formatter);
   }

   public MenuSlider(
      String label, DoubleSupplier min, DoubleSupplier max, double step, DoubleSupplier getter, DoubleConsumer setter, Function<Double, String> formatter
   ) {
      this.label = label;
      this.min = min;
      this.max = max;
      this.step = step;
      this.getter = getter;
      this.setter = setter;
      this.formatter = formatter;
   }

   private double value() {
      return this.getter.getAsDouble();
   }

   private float progress() {
      double d = this.min.getAsDouble();
      double d2 = this.max.getAsDouble() - d;
      return d2 <= 0.0 ? 0.0F : (float)Math.min(1.0, Math.max(0.0, (this.value() - d) / d2));
   }

   @Override
   public void draw(MenuDrawContext menuDrawContext, float f, float f2) {
      MenuSurface.plate(menuDrawContext, this.x, this.y, this.width, this.height, RADIUS, MenuTheme.plate());
      Font medium = MenuTheme.medium(12.0F);
      float centeredTextY = this.y + (this.height - medium.height()) / 2.0F;
      String text = this.formatter.apply(this.value());
      float width = medium.width(text);
      menuDrawContext.drawText(medium, this.label, this.x + PADDING, centeredTextY, MenuTheme.text());
      menuDrawContext.drawText(medium, text, this.x + this.width - PADDING - width, centeredTextY, MenuTheme.text());
      float trackStart = this.trackStart();
      float trackEnd = this.trackEnd();
      float width2 = Math.max(0.0F, trackEnd - trackStart);
      float y = this.y + (this.height - TRACK_HEIGHT) / 2.0F;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(TRACK_HEIGHT / 2.0F);
      menuDrawContext.drawRoundedRect(trackStart, y, width2, TRACK_HEIGHT, headerRadius, MenuTheme.track());
      float width3 = width2 * this.progress();
      if (width3 > 0.0F) {
         menuDrawContext.drawRoundedRect(trackStart, y, width3, TRACK_HEIGHT, headerRadius, MenuTheme.accent());
      }

      float floatValue = trackStart + width3 - KNOB_SIZE / 2.0F;
      float y2 = this.y + (this.height - KNOB_SIZE) / 2.0F;
      menuDrawContext.drawRoundedRect(floatValue, y2, KNOB_SIZE, KNOB_SIZE, ProxyDotRadiusData.method8069(KNOB_SIZE / 2.0F), ZenithColor.INSTANCE);
      if (this.dragging) {
         Font semibold = MenuTheme.semibold(9.0F);
         String str = trimNumber(this.value());
         float width4 = semibold.width(str) + MenuTheme.px(8.0F);
         float px = MenuTheme.px(14.0F);
         float floatValue2 = Math.min(Math.max(trackStart, floatValue + KNOB_SIZE / 2.0F - width4 / 2.0F), trackEnd - width4);
         float y3 = this.y + MenuTheme.px(2.0F);
         menuDrawContext.drawRoundedRect(floatValue2, y3, width4, px, ProxyDotRadiusData.method8069(MenuTheme.px(4.0F)), MenuTheme.accent());
         menuDrawContext.drawText(semibold, str, floatValue2 + (width4 - semibold.width(str)) / 2.0F, y3 + (px - semibold.height()) / 2.0F, MenuTheme.onAccent());
      }
   }

   private static String trimNumber(double d) {
      return d == Math.rint(d) ? String.valueOf((long)d) : String.format("%.1f", d);
   }

   private float trackStart() {
      return this.x + PADDING + MenuTheme.medium(12.0F).width(this.label) + GAP;
   }

   private float trackEnd() {
      Font medium = MenuTheme.medium(12.0F);
      return this.x + this.width - PADDING - medium.width(this.formatter.apply(this.value())) - GAP;
   }

   private void applyFromMouse(double lastClickX) {
      float trackStart = this.trackStart();
      float f = Math.max(1.0F, this.trackEnd() - trackStart);
      double d = this.min.getAsDouble();
      double d2 = this.max.getAsDouble();
      double d3 = Math.min(1.0, Math.max(0.0, (lastClickX - trackStart) / f));
      double d4 = d + d3 * (d2 - d);
      if (this.step > 0.0) {
         d4 = Math.round(d4 / this.step) * this.step;
      }

      this.setter.accept(Math.min(d2, Math.max(d, d4)));
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int n) {
      if (this.visible && this.enabled && n == 0 && this.contains(lastClickX, lastClickY)) {
         this.dragging = true;
         this.applyFromMouse(lastClickX);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseDragged(double lastClickX, double d, int n) {
      if (!this.dragging) {
         return false;
      } else {
         this.applyFromMouse(lastClickX);
         return true;
      }
   }

   @Override
   public boolean mouseReleased(double d, double d2, int n) {
      if (!this.dragging) {
         return false;
      } else {
         this.dragging = false;
         return true;
      }
   }

   @Generated
   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }
}

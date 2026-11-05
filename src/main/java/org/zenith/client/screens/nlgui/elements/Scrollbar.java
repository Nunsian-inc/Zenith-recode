package org.zenith.client.screens.nlgui.elements;

import net.minecraft.util.Mth;
import org.zenith.ZenithColor;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class Scrollbar {
   public static final float MIN_THUMB = 12.0F;
   private static final float GRAB = 3.0F;
   private BoundsData track;
   private BoundsData thumb;
   private float span;
   private float travel;
   private boolean dragging;
   private float grab;

   public void render(MenuDrawContext menuDrawContext, float floatValue, float y, float max, float shownContentHeight, float scroll, ZenithColor zenithColor) {
      this.render(menuDrawContext, floatValue, y, max, shownContentHeight, shownContentHeight, scroll, zenithColor);
   }

   public void render(MenuDrawContext menuDrawContext, float floatValue, float y, float max, float shownContentHeight, float f, float scroll, ZenithColor zenithColor) {
      if (!(shownContentHeight <= max) && !(max <= 0.0F) && zenithColor.getIntValue4() != 0) {
         float height = Mth.clamp(max * (max / shownContentHeight), Math.min(12.0F, max), max);
         this.span = Math.max(0.0F, f - max);
         this.travel = Math.max(0.0F, max - height);
         float f2 = this.span > 0.0F ? Mth.clamp(-scroll / this.span, 0.0F, 1.0F) : 0.0F;
         float y2 = y + this.travel * f2;
         if (this.span > 0.0F) {
            this.track = new BoundsData(floatValue - 3.0F, y, 7.0F, max);
            this.thumb = new BoundsData(floatValue, y2, 1.0F, height);
         } else {
            this.clear();
         }

         menuDrawContext.drawRect(floatValue, y2, 1.0F, height, zenithColor);
      } else {
         this.clear();
      }
   }

   public void clear() {
      this.track = null;
      this.thumb = null;
      this.span = 0.0F;
      this.travel = 0.0F;
      this.dragging = false;
   }

   public float onMouseClicked(double lastClickX, double lastClickY) {
      if (this.track != null && this.thumb != null && this.track.contains(lastClickX, lastClickY)) {
         this.dragging = true;
         if (lastClickY >= this.thumb.getFloatValue2() && lastClickY <= this.thumb.getFloatValue2() + this.thumb.height()) {
            this.grab = (float)(lastClickY - this.thumb.getFloatValue2());
            return Float.NaN;
         } else {
            this.grab = this.thumb.height() / 2.0F;
            return this.scrollAt(lastClickY);
         }
      } else {
         return Float.NaN;
      }
   }

   public float onMouseDragged(double mouseY) {
      return this.dragging ? this.scrollAt(mouseY) : Float.NaN;
   }

   private float scrollAt(double lastClickY) {
      return this.travel <= 0.0F ? 0.0F : -Mth.clamp((float)(lastClickY - this.grab - this.track.getFloatValue2()) / this.travel, 0.0F, 1.0F) * this.span;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public void release() {
      this.dragging = false;
   }
}

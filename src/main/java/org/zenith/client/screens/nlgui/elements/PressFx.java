package org.zenith.client.screens.nlgui.elements;

import java.util.HashMap;
import java.util.Map;
import org.zenith.ZenithColor;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class PressFx {
   private static final long DIP_MS = 460L;
   private static final long INK_MS = 620L;
   private static final float SHRINK = 2.4F;
   private final Map<Object, PressFx.Mark> marks = new HashMap<>();

   public void press(Object obj, double lastClickX, double lastClickY) {
      if (this.marks.size() > 64) {
         long now = System.currentTimeMillis();
         this.marks.values().removeIf(var2x -> now - var2x.at() > 620L);
      }

      this.marks.put(obj, new PressFx.Mark(System.currentTimeMillis(), (float)lastClickX, (float)lastClickY));
   }

   public void press(Object obj) {
      this.press(obj, Double.NaN, Double.NaN);
   }

   public float dip(Object obj) {
      PressFx.Mark mark = this.marks.get(obj);
      if (mark == null) {
         return 0.0F;
      } else {
         float f = (float)(System.currentTimeMillis() - mark.at()) / 460.0F;
         if (f >= 1.0F) {
            return 0.0F;
         } else if (f < 0.3F) {
            float f2 = f / 0.3F;
            return f2 * f2 * (3.0F - 2.0F * f2);
         } else {
            float f2 = (f - 0.3F) / 0.7F;
            float f3 = 1.0F - (1.0F - f2) * (1.0F - f2) * (1.0F - f2);
            return 1.0F - f3;
         }
      }
   }

   public boolean begin(MenuDrawContext menuDrawContext, Object obj, BoundsData boundsData) {
      float dip = this.dip(obj);
      if (!(Math.abs(dip) < 0.003F) && boundsData != null) {
         float f = Math.max(1.0F, Math.max(boundsData.width(), boundsData.height()));
         float x = 1.0F - dip * Math.min(0.08F, 2.4F / f);
         float x2 = boundsData.getFloatValue() + boundsData.width() / 2.0F;
         float y = boundsData.getFloatValue2() + boundsData.height() / 2.0F;
         menuDrawContext.pushMatrix();
         menuDrawContext.pose().translate(x2, y);
         menuDrawContext.pose().scale(x, x);
         menuDrawContext.pose().translate(-x2, -y);
         return true;
      } else {
         return false;
      }
   }

   public void end(MenuDrawContext menuDrawContext, boolean begin) {
      if (begin) {
         menuDrawContext.popMatrix();
      }
   }

   public void ink(MenuDrawContext menuDrawContext, Object obj, BoundsData boundsData, float floatValue, ZenithColor zenithColor, float previewAlpha) {
      PressFx.Mark mark = this.marks.get(obj);
      if (mark != null && boundsData != null) {
         float f = (float)(System.currentTimeMillis() - mark.at()) / 620.0F;
         if (!(f >= 1.0F) && !(f < 0.0F)) {
            float f2 = Math.min(1.0F, f * 1.4F);
            float f3 = 1.0F - (1.0F - f2) * (1.0F - f2) * (1.0F - f2) * (1.0F - f2);
            float f4 = Float.isNaN(mark.px())
               ? boundsData.getFloatValue() + boundsData.width() / 2.0F
               : Math.max(boundsData.getFloatValue(), Math.min(boundsData.getFloatValue() + boundsData.width(), mark.px()));
            float f5 = Float.isNaN(mark.py())
               ? boundsData.getFloatValue2() + boundsData.height() / 2.0F
               : Math.max(boundsData.getFloatValue2(), Math.min(boundsData.getFloatValue2() + boundsData.height(), mark.py()));
            float floatValue2 = f4 + (boundsData.getFloatValue() - f4) * f3;
            float y = f5 + (boundsData.getFloatValue2() - f5) * f3;
            float f6 = f4 + (boundsData.getFloatValue() + boundsData.width() - f4) * f3;
            float f7 = f5 + (boundsData.getFloatValue2() + boundsData.height() - f5) * f3;
            float width = f6 - floatValue2;
            float height = f7 - y;
            if (!(width < 0.5F) && !(height < 0.5F)) {
               float f8 = f < 0.35F ? 1.0F : 1.0F - (f - 0.35F) / 0.65F * ((f - 0.35F) / 0.65F) * (3.0F - 2.0F * ((f - 0.35F) / 0.65F));
               float intValue = Math.min(floatValue, Math.min(width, height) / 2.0F);
               menuDrawContext.drawRoundedRect(floatValue2, y, width, height, ProxyDotRadiusData.method8069(intValue), zenithColor.method7294(previewAlpha * f8));
            }
         }
      }
   }

   private static final class Mark {
      private final long at;
      private final float px;
      private final float py;

      public long at() {
         return this.at;
      }

      public float px() {
         return this.px;
      }

      public float py() {
         return this.py;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Mark other)) return false;
         return java.util.Objects.equals(this.at, other.at) && java.util.Objects.equals(this.px, other.px) && java.util.Objects.equals(this.py, other.py);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(at, px, py);
      }

      @Override
      public String toString() {
         return "Mark[" + "at=" + this.at + ", " + "px=" + this.px + ", " + "py=" + this.py + "]";
      }

      private Mark(long at, float px, float py) {
         this.at = at;
         this.px = px;
         this.py = py;
      }
   }
}

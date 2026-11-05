package org.zenith.utility.render.display.base;

import org.zenith.utility.render.MathUtils;

public final class BoundsData {
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;

   public float floatValue() {
      return this.floatValue;
   }

   public float floatValue2() {
      return this.floatValue2;
   }

   public float floatValue3() {
      return this.floatValue3;
   }

   public float floatValue4() {
      return this.floatValue4;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BoundsData other)) return false;
      return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
   }

   @Override
   public String toString() {
      return "BoundsData[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + "]";
   }

   public BoundsData(float floatValue, float y, float width, float height) {
      this.floatValue = floatValue;
      this.floatValue2 = y;
      this.floatValue3 = width;
      this.floatValue4 = height;
   }

   public boolean contains(double lastClickX, double lastClickY) {
      return MathUtils.method2647(lastClickX, lastClickY, this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4);
   }

   public boolean method8092(double x, double lastClickY, float f) {
      return MathUtils.method2647(x, lastClickY, this.floatValue - f, this.floatValue2 - f, this.floatValue3 + f * 2.0F, this.floatValue4 + f * 2.0F);
   }

   public boolean method8093(BoundsData boundsData) {
      return this.floatValue < boundsData.getFloatValue() + boundsData.width()
         && this.floatValue + this.floatValue3 > boundsData.getFloatValue()
         && this.floatValue2 < boundsData.getFloatValue2() + boundsData.height()
         && this.floatValue2 + this.floatValue4 > boundsData.getFloatValue2();
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public float width() {
      return this.floatValue3;
   }

   public float height() {
      return this.floatValue4;
   }
}

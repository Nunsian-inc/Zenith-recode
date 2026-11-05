package org.zenith.utility.render.display.base;

import lombok.Generated;
import org.zenith.utility.render.MathUtils;

public class Bounds {
   public float floatValue;
   public float floatValue2;
   public float width;
   public float height;

   public boolean contains(double x, double y) {
      return MathUtils.method2647(x, y, this.floatValue, this.floatValue2, this.width, this.height);
   }

   @Generated
   public Bounds(float floatValue, float f, float width, float height) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.width = width;
      this.height = height;
   }

   @Generated
   public float getX() {
      return this.floatValue;
   }

   @Generated
   public float getY() {
      return this.floatValue2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public void setX(float floatValue) {
      this.floatValue = floatValue;
   }

   @Generated
   public void setY(float y) {
      this.floatValue2 = y;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }

   @Generated
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Bounds bounds)) {
         return false;
      } else if (!bounds.method8094(this)) {
         return false;
      } else if (Float.compare(this.getX(), bounds.getX()) != 0) {
         return false;
      } else if (Float.compare(this.getY(), bounds.getY()) != 0) {
         return false;
      } else {
         return Float.compare(this.getWidth(), bounds.getWidth()) != 0 ? false : Float.compare(this.getHeight(), bounds.getHeight()) == 0;
      }
   }

   @Generated
   public boolean method8094(Object obj) {
      return obj instanceof Bounds;
   }

   @Generated
   @Override
   public int hashCode() {
      byte b = 59;
      int n = 1;
      n = n * 59 + Float.floatToIntBits(this.getX());
      n = n * 59 + Float.floatToIntBits(this.getY());
      n = n * 59 + Float.floatToIntBits(this.getWidth());
      return n * 59 + Float.floatToIntBits(this.getHeight());
   }

   @Generated
   @Override
   public String toString() {
      return "ChangeRect(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
   }
}

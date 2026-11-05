package org.zenith.utility.render.display.base;

import org.jetbrains.annotations.NotNull;

public final class ProxyDotRadiusData {
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
      if (!(o instanceof ProxyDotRadiusData other)) return false;
      return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
   }
   public static final ProxyDotRadiusData INSTANCE = new ProxyDotRadiusData(0.0F, 0.0F, 0.0F, 0.0F);

   public ProxyDotRadiusData(float floatValue, float intValue, float f, float intValue2) {
      this.floatValue = floatValue;
      this.floatValue2 = intValue;
      this.floatValue3 = f;
      this.floatValue4 = intValue2;
   }

   public static ProxyDotRadiusData method8069(float intValue) {
      return new ProxyDotRadiusData(intValue, intValue, intValue, intValue);
   }

   public static ProxyDotRadiusData method8070(float floatValue) {
      return new ProxyDotRadiusData(floatValue, 0.0F, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8071(float intValue) {
      return new ProxyDotRadiusData(0.0F, intValue, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8072(float f) {
      return new ProxyDotRadiusData(0.0F, 0.0F, f, 0.0F);
   }

   public static ProxyDotRadiusData method8073(float f) {
      return new ProxyDotRadiusData(0.0F, 0.0F, 0.0F, f);
   }

   public static ProxyDotRadiusData method8074(float floatValue, float intValue) {
      return new ProxyDotRadiusData(floatValue, intValue, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8075(float intValue) {
      return new ProxyDotRadiusData(intValue, intValue, 0.0F, 0.0F);
   }

   public static ProxyDotRadiusData method8076(float f, float intValue) {
      return new ProxyDotRadiusData(0.0F, 0.0F, intValue, f);
   }

   public static ProxyDotRadiusData method8077(float intValue) {
      return new ProxyDotRadiusData(0.0F, 0.0F, intValue, intValue);
   }

   public static ProxyDotRadiusData method8078(float intValue, float f) {
      return new ProxyDotRadiusData(intValue, 0.0F, 0.0F, f);
   }

   public static ProxyDotRadiusData method8079(float intValue, float f) {
      return new ProxyDotRadiusData(0.0F, intValue, f, 0.0F);
   }

   @NotNull
   @Override
   public String toString() {
      return "BorderRadius{topLeftRadius="
         + this.floatValue
         + ", topRightRadius="
         + this.floatValue2
         + ", bottomRightRadius="
         + this.floatValue3
         + ", bottomLeftRadius="
         + this.floatValue4
         + "}";
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public float getFloatValue4() {
      return this.floatValue4;
   }
}

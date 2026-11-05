package org.zenith.utility.misc;

import lombok.Generated;

public class Class6 extends RotationConfig {
   private int intValue;
   private boolean flag;
   private boolean flag2;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;

   @Override
   public Enum1 method853() {
      return Enum1.GOWNO;
   }

   @Generated
   private static int method854() {
      return 3;
   }

   @Generated
   private static boolean method855() {
      return true;
   }

   @Generated
   private static boolean method856() {
      return true;
   }

   @Generated
   private static float method857() {
      return 1.75F;
   }

   @Generated
   private static float method858() {
      return 0.4F;
   }

   @Generated
   private static float method859() {
      return 1.25F;
   }

   @Generated
   private static float method860() {
      return 0.75F;
   }

   @Generated
   public Class6(int intValue, boolean flag, boolean flag2, float floatValue, float f, float f2, float f3) {
      this.intValue = intValue;
      this.flag = flag;
      this.flag2 = flag2;
      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.floatValue3 = f2;
      this.floatValue4 = f3;
   }

   @Generated
   public static Class6.Entry method861() {
      return new Class6.Entry();
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag2;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public float getFloatValue3() {
      return this.floatValue3;
   }

   @Generated
   public float getFloatValue4() {
      return this.floatValue4;
   }

   @Generated
   public static class Entry {
      @Generated
      private boolean flag;
      @Generated
      private int intValue;
      @Generated
      private boolean flag2;
      @Generated
      private boolean flag3;
      @Generated
      private boolean flag4;
      @Generated
      private boolean flag5;
      @Generated
      private boolean flag6;
      @Generated
      private float floatValue;
      @Generated
      private boolean flag7;
      @Generated
      private float floatValue2;
      @Generated
      private boolean flag8;
      @Generated
      private float floatValue3;
      @Generated
      private boolean flag9;
      @Generated
      private float floatValue4;

      @Generated
      public Class6.Entry method862(int intValue) {
         this.intValue = intValue;
         this.flag = true;
         return this;
      }

      @Generated
      public Class6.Entry method863(boolean flag) {
         this.flag3 = flag;
         this.flag2 = true;
         return this;
      }

      @Generated
      public Class6.Entry method864(boolean flag) {
         this.flag5 = flag;
         this.flag4 = true;
         return this;
      }

      @Generated
      public Class6.Entry method865(float floatValue) {
         this.floatValue = floatValue;
         this.flag6 = true;
         return this;
      }

      @Generated
      public Class6.Entry method866(float f) {
         this.floatValue2 = f;
         this.flag7 = true;
         return this;
      }

      @Generated
      public Class6.Entry method867(float f) {
         this.floatValue3 = f;
         this.flag8 = true;
         return this;
      }

      @Generated
      public Class6.Entry method868(float f) {
         this.floatValue4 = f;
         this.flag9 = true;
         return this;
      }

      @Generated
      public Class6 method869() {
         int intValue = this.intValue;
         if (!this.flag) {
            intValue = Class6.method854();
         }

         boolean flag = this.flag3;
         if (!this.flag2) {
            flag = Class6.method855();
         }

         boolean flag2 = this.flag5;
         if (!this.flag4) {
            flag2 = Class6.method856();
         }

         float floatValue = this.floatValue;
         if (!this.flag6) {
            floatValue = Class6.method857();
         }

         float f = this.floatValue2;
         if (!this.flag7) {
            f = Class6.method858();
         }

         float f2 = this.floatValue3;
         if (!this.flag8) {
            f2 = Class6.method859();
         }

         float f3 = this.floatValue4;
         if (!this.flag9) {
            f3 = Class6.method860();
         }

         return new Class6(intValue, flag, flag2, floatValue, f, f2, f3);
      }

      @Generated
      @Override
      public String toString() {
         return "GownoRotationConfig.GownoRotationConfigBuilder(tick$value="
            + this.intValue
            + ", multiplyYaw$value="
            + this.flag3
            + ", multiplyPitch$value="
            + this.flag5
            + ", yawAcceleration$value="
            + this.floatValue
            + ", yawDeceleration$value="
            + this.floatValue2
            + ", pitchAcceleration$value="
            + this.floatValue3
            + ", pitchDeceleration$value="
            + this.floatValue4
            + ")";
      }
   }
}

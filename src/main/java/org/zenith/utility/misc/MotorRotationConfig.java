package org.zenith.utility.misc;

import lombok.Generated;

public class MotorRotationConfig extends RotationConfig {
   @Override
   public RotationMode method853() {
      return RotationMode.MOTOR;
   }

   @Generated
   public static MotorRotationConfig.Entry method2781() {
      return new MotorRotationConfig.Entry();
   }

   @Generated
   public static class Entry {
      @Generated
      public MotorRotationConfig method2782() {
         return new MotorRotationConfig();
      }

      @Generated
      @Override
      public String toString() {
         return "MotorRotationConfig.MotorRotationConfigBuilder()";
      }
   }
}

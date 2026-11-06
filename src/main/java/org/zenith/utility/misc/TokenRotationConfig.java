package org.zenith.utility.misc;

import lombok.Generated;

public class TokenRotationConfig extends RotationConfig {
   @Override
   public RotationMode method853() {
      return RotationMode.TOKEN;
   }

   @Generated
   public static TokenRotationConfig.Entry method7548() {
      return new TokenRotationConfig.Entry();
   }

   @Generated
   public static class Entry {
      @Generated
      public TokenRotationConfig method7549() {
         return new TokenRotationConfig();
      }

      @Generated
      @Override
      public String toString() {
         return "TokenRotationConfig.TokenRotationConfigBuilder()";
      }
   }
}

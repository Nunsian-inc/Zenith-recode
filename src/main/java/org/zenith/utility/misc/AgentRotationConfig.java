package org.zenith.utility.misc;

import lombok.Generated;

public class AgentRotationConfig extends RotationConfig {
   @Override
   public RotationMode method853() {
      return RotationMode.AGENT;
   }

   @Generated
   public static AgentRotationConfig.Entry method6490() {
      return new AgentRotationConfig.Entry();
   }

   @Generated
   public static class Entry {
      @Generated
      public AgentRotationConfig method6491() {
         return new AgentRotationConfig();
      }

      @Generated
      @Override
      public String toString() {
         return "AgentRotationConfig.AgentRotationConfigBuilder()";
      }
   }
}

package org.zenith.utility.discord.utils;

import java.io.Serializable;
import lombok.Generated;

public class RPCButton implements Serializable {
   private final String url;
   private final String label;

   public static RPCButton create(String label, String url) {
      label = label.substring(0, Math.min(label.length(), 31));
      return new RPCButton(label, url);
   }

   public RPCButton(String label, String url) {
      this.label = label;
      this.url = url;
   }

   @Generated
   public String getUrl() {
      return this.url;
   }

   @Generated
   public String getLabel() {
      return this.label;
   }
}

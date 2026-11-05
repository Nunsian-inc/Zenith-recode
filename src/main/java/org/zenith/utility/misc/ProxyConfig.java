package org.zenith.utility.misc;

import lombok.Generated;

public class ProxyConfig {
   private String host = "";
   private int port = 1080;
   private ProxyConfig.Type type = ProxyConfig.Type.SOCKS5;
   private String username = "";
   private String password = "";

   public ProxyConfig() {
   }

   public ProxyConfig(String host, int port, ProxyConfig.Type type, String str, String str2) {
      this.host = host;
      this.port = port;
      this.type = type;
      this.username = str == null ? "" : str;
      this.password = str2 == null ? "" : str2;
   }

   public static ProxyConfig method439(String str, ProxyConfig.Type pendingType) {
      if (str == null) {
         return null;
      } else {
         String[] strings = str.trim().split(":", 4);
         if (strings.length < 2) {
            return null;
         } else {
            int port;
            try {
               port = Integer.parseInt(strings[1].trim());
            } catch (NumberFormatException e) {
               return null;
            }

            if (port > 0 && port <= 65535) {
               String str2 = strings.length > 2 ? strings[2] : "";
               String str3 = strings.length > 3 ? strings[3] : "";
               return new ProxyConfig(strings[0].trim(), port, pendingType, str2, str3);
            } else {
               return null;
            }
         }
      }
   }

   public boolean isValid() {
      return this.host != null && !this.host.isBlank() && this.port > 0 && this.port <= 65535;
   }

   public boolean method440() {
      return this.username != null && !this.username.isEmpty();
   }

   public String method441() {
      return this.host + ":" + this.port;
   }

   @Override
   public String toString() {
      return this.type + " " + this.method441();
   }

   @Generated
   public String getHost() {
      return this.host;
   }

   @Generated
   public int getPort() {
      return this.port;
   }

   @Generated
   public ProxyConfig.Type getType() {
      return this.type;
   }

   @Generated
   public String getUsername() {
      return this.username;
   }

   @Generated
   public String getPassword() {
      return this.password;
   }

   @Generated
   public void setHost(String host) {
      this.host = host;
   }

   @Generated
   public void setPort(int port) {
      this.port = port;
   }

   @Generated
   public void setType(ProxyConfig.Type type) {
      this.type = type;
   }

   @Generated
   public void setUsername(String username) {
      this.username = username;
   }

   @Generated
   public void setPassword(String password) {
      this.password = password;
   }

   public static enum Type {
      SOCKS5,
      SOCKS4;
   }
}

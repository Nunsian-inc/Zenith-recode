package org.zenith.base.bot.client;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class BotClientConfig {
   private final String name;
   private final UUID uuid;
   private final String host;
   private final int port;
   private final String proxy;
   private final String brand;
   private final int protocolVersion;

   public String name() {
      return this.name;
   }

   public UUID uuid() {
      return this.uuid;
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public String proxy() {
      return this.proxy;
   }

   public String brand() {
      return this.brand;
   }

   public int protocolVersion() {
      return this.protocolVersion;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BotClientConfig other)) return false;
      return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.uuid, other.uuid) && java.util.Objects.equals(this.host, other.host) && java.util.Objects.equals(this.port, other.port) && java.util.Objects.equals(this.proxy, other.proxy) && java.util.Objects.equals(this.brand, other.brand) && java.util.Objects.equals(this.protocolVersion, other.protocolVersion);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(name, uuid, host, port, proxy, brand, protocolVersion);
   }

   @Override
   public String toString() {
      return "BotClientConfig[" + "name=" + this.name + ", " + "uuid=" + this.uuid + ", " + "host=" + this.host + ", " + "port=" + this.port + ", " + "proxy=" + this.proxy + ", " + "brand=" + this.brand + ", " + "protocolVersion=" + this.protocolVersion + "]";
   }
   public static final String DEFAULT_BRAND = "vanilla";
   public static final int NATIVE_PROTOCOL_VERSION = -1;

   public BotClientConfig(String name, UUID uuid, String host, int port, String proxy, String brand, int protocolVersion) {
      this.name = name;
      this.uuid = uuid;
      this.host = host;
      this.port = port;
      this.proxy = proxy;
      this.brand = brand;
      this.protocolVersion = protocolVersion;
   }

   public static BotClientConfig offline(String name, String host, int port, String proxy) {
      return offline(name, host, port, proxy, -1);
   }

   public static BotClientConfig offline(String name, String host, int port, String proxy, int protocolVersion) {
      return new BotClientConfig(name, offlineUuid(name), host, port, proxy, "vanilla", protocolVersion);
   }

   public static UUID offlineUuid(String name) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
   }
}

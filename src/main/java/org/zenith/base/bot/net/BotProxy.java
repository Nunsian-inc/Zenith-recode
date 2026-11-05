package org.zenith.base.bot.net;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class BotProxy {
   private final BotProxy.Type type;
   private final String host;
   private final int port;
   @Nullable
   private final String username;
   @Nullable
   private final String password;

   public BotProxy.Type type() {
      return this.type;
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public String username() {
      return this.username;
   }

   public String password() {
      return this.password;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BotProxy other)) return false;
      return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.host, other.host) && java.util.Objects.equals(this.port, other.port) && java.util.Objects.equals(this.username, other.username) && java.util.Objects.equals(this.password, other.password);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(type, host, port, username, password);
   }

   @Override
   public String toString() {
      return "BotProxy[" + "type=" + this.type + ", " + "host=" + this.host + ", " + "port=" + this.port + ", " + "username=" + this.username + ", " + "password=" + this.password + "]";
   }

   public BotProxy(BotProxy.Type type, String host, int port, @Nullable String username, @Nullable String password) {
      this.type = type;
      this.host = host;
      this.port = port;
      this.username = username;
      this.password = password;
   }

   @Nullable
   public static BotProxy parse(@Nullable String proxy) {
      if (proxy != null && !proxy.isBlank()) {
         String str = proxy.trim();
         return str.contains("://") ? parseUri(str) : parseLegacy(str);
      } else {
         return null;
      }
   }

   private static BotProxy parseUri(String str) {
      URI uri;
      try {
         uri = URI.create(str);
      } catch (IllegalArgumentException e) {
         throw new IllegalArgumentException("Invalid proxy address", e);
      }

      BotProxy.Type type = BotProxy.Type.fromScheme(uri.getScheme());
      String host = uri.getHost();
      int port = uri.getPort();
      if (host != null && !host.isBlank() && port > 0) {
         BotProxy.Credentials credentials = BotProxy.Credentials.parse(uri.getUserInfo());
         return new BotProxy(type, host, validatePort(port), credentials.username(), credentials.password());
      } else {
         throw new IllegalArgumentException("Invalid proxy address");
      }
   }

   private static BotProxy parseLegacy(String str) {
      String str2 = str;
      BotProxy.Credentials credentials = BotProxy.Credentials.EMPTY;
      int lastIndexOf = str.lastIndexOf(64);
      if (lastIndexOf >= 0) {
         credentials = BotProxy.Credentials.parse(str.substring(0, lastIndexOf));
         str2 = str.substring(lastIndexOf + 1);
      }

      BotProxy.HostPort hostPort = BotProxy.HostPort.parse(str2);
      if (credentials == BotProxy.Credentials.EMPTY && hostPort.tail().length > 0) {
         credentials = BotProxy.Credentials.fromTail(hostPort.tail());
      }

      return new BotProxy(BotProxy.Type.SOCKS5, hostPort.host(), hostPort.port(), credentials.username(), credentials.password());
   }

   public ProxyHandler createHandler() {
      InetSocketAddress inetSocketAddress = new InetSocketAddress(this.host, this.port);
      boolean flag = this.username != null && !this.username.isBlank();
      String password = this.password == null ? "" : this.password;

      return (ProxyHandler)(switch (this.type) {
         case SOCKS5 -> flag ? new Socks5ProxyHandler(inetSocketAddress, this.username, password) : new Socks5ProxyHandler(inetSocketAddress);
         case SOCKS4 -> flag ? new Socks4ProxyHandler(inetSocketAddress, this.username) : new Socks4ProxyHandler(inetSocketAddress);
         case HTTP -> flag ? new HttpProxyHandler(inetSocketAddress, this.username, password) : new HttpProxyHandler(inetSocketAddress);
      });
   }

   private static int validatePort(int port) {
      if (port >= 1 && port <= 65535) {
         return port;
      } else {
         throw new IllegalArgumentException("Invalid proxy port");
      }
   }

   private static final class Credentials {
      @Nullable
      private final String username;
      @Nullable
      private final String password;

      public String username() {
         return this.username;
      }

      public String password() {
         return this.password;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Credentials other)) return false;
         return java.util.Objects.equals(this.username, other.username) && java.util.Objects.equals(this.password, other.password);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(username, password);
      }

      @Override
      public String toString() {
         return "Credentials[" + "username=" + this.username + ", " + "password=" + this.password + "]";
      }
      private static final BotProxy.Credentials EMPTY = new BotProxy.Credentials(null, null);

      private Credentials(@Nullable String username, @Nullable String password) {
         this.username = username;
         this.password = password;
      }

      private static BotProxy.Credentials parse(@Nullable String userInfo) {
         if (userInfo != null && !userInfo.isBlank()) {
            int indexOf = userInfo.indexOf(58);
            return indexOf < 0 ? new BotProxy.Credentials(userInfo, null) : new BotProxy.Credentials(userInfo.substring(0, indexOf), userInfo.substring(indexOf + 1));
         } else {
            return EMPTY;
         }
      }

      private static BotProxy.Credentials fromTail(String[] tail) {
         if (tail.length != 0 && !tail[0].isBlank()) {
            return tail.length == 1
               ? new BotProxy.Credentials(tail[0], null)
               : new BotProxy.Credentials(tail[0], String.join(":", Arrays.copyOfRange(tail, 1, tail.length)));
         } else {
            return EMPTY;
         }
      }
   }

   private static final class HostPort {
      private final String host;
      private final int port;
      private final String[] tail;

      public String host() {
         return this.host;
      }

      public int port() {
         return this.port;
      }

      public String[] tail() {
         return this.tail;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof HostPort other)) return false;
         return java.util.Objects.equals(this.host, other.host) && java.util.Objects.equals(this.port, other.port) && java.util.Objects.equals(this.tail, other.tail);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(host, port, tail);
      }

      @Override
      public String toString() {
         return "HostPort[" + "host=" + this.host + ", " + "port=" + this.port + ", " + "tail=" + this.tail + "]";
      }

      private HostPort(String host, int port, String[] tail) {
         this.host = host;
         this.port = port;
         this.tail = tail;
      }

      private static BotProxy.HostPort parse(String str) {
         if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("Invalid proxy address");
         } else if (str.startsWith("[")) {
            int indexOf = str.indexOf(93);
            if (indexOf >= 0 && indexOf + 1 < str.length() && str.charAt(indexOf + 1) == ':') {
               String host = str.substring(1, indexOf);
               String[] copyOfRange = str.substring(indexOf + 2).split(":", -1);
               return create(host, copyOfRange);
            } else {
               throw new IllegalArgumentException("Invalid proxy address");
            }
         } else {
            String[] original = str.split(":", -1);
            if (original.length < 2) {
               throw new IllegalArgumentException("Invalid proxy address");
            } else {
               return create(original[0], Arrays.copyOfRange(original, 1, original.length));
            }
         }
      }

      private static BotProxy.HostPort create(String host, String[] copyOfRange) {
         if (host != null && !host.isBlank() && copyOfRange.length != 0) {
            int port;
            try {
               port = Integer.parseInt(copyOfRange[0]);
            } catch (NumberFormatException e) {
               throw new IllegalArgumentException("Invalid proxy port", e);
            }

            return new BotProxy.HostPort(host, BotProxy.validatePort(port), Arrays.copyOfRange(copyOfRange, 1, copyOfRange.length));
         } else {
            throw new IllegalArgumentException("Invalid proxy address");
         }
      }
   }

   public static enum Type {
      SOCKS5,
      SOCKS4,
      HTTP;

      private static BotProxy.Type fromScheme(String scheme) {
         if (scheme != null && !scheme.isBlank()) {
            String lowerCase = scheme.toLowerCase(Locale.ROOT);

            return switch (lowerCase) {
               case "socks", "socks5" -> SOCKS5;
               case "socks4" -> SOCKS4;
               case "http", "https" -> HTTP;
               default -> throw new IllegalArgumentException("Unsupported proxy type: " + scheme);
            };
         } else {
            return SOCKS5;
         }
      }
   }
}

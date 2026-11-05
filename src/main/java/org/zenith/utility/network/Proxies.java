package org.zenith.utility.network;

import com.google.gson.reflect.TypeToken;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.misc.ProxyConfig;

public final class Proxies extends JsonListStorage<ProxyConfig> {
   private volatile int intValue = -1;

   public Proxies() {
      super("proxies.json", "", (new TypeToken<List<ProxyConfig>>() {}).getType(), CopyOnWriteArrayList::new);
      CopyOnWriteArrayList items = new CopyOnWriteArrayList<>(this.items);
      this.items = items;
   }

   public List<ProxyConfig> method6535() {
      return new ArrayList<>(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public ProxyConfig method6536() {
      var proxyConfigs = this.method6535();
      if (this.intValue >= 0 && this.intValue < proxyConfigs.size()) {
         ProxyConfig proxyConfig = (ProxyConfig)proxyConfigs.get(this.intValue);
         return proxyConfig != null && proxyConfig.isValid() ? proxyConfig : null;
      } else {
         return null;
      }
   }

   public boolean isEnabled() {
      return this.method6536() != null;
   }

   public ProxyHandler createHandler() {
      ProxyConfig proxyConfig = this.method6536();
      if (proxyConfig == null) {
         return null;
      } else {
         InetSocketAddress inetSocketAddress = new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort());
         boolean flag = proxyConfig.method440();
         String password = proxyConfig.getPassword() == null ? "" : proxyConfig.getPassword();
         if (proxyConfig.getType() == ProxyConfig.Type.SOCKS4) {
            return flag ? new Socks4ProxyHandler(inetSocketAddress, proxyConfig.getUsername()) : new Socks4ProxyHandler(inetSocketAddress);
         } else {
            return flag ? new Socks5ProxyHandler(inetSocketAddress, proxyConfig.getUsername(), password) : new Socks5ProxyHandler(inetSocketAddress);
         }
      }
   }

   public void select(int n) {
      this.intValue = n == this.intValue ? -1 : n;
   }

   public void disable() {
      this.intValue = -1;
   }

   public void method6537(ProxyConfig name) {
      if (name != null && name.isValid()) {
         this.add(name);
         this.save();
      }
   }

   public void method6538(int n) {
      var proxyConfigs = this.method6535();
      if (n >= 0 && n < proxyConfigs.size()) {
         this.remove((ProxyConfig)proxyConfigs.get(n));
         if (this.intValue == n) {
            this.intValue = -1;
         } else if (this.intValue > n) {
            this.intValue--;
         }

         this.save();
      }
   }

   @Generated
   public int getIntValue2() {
      return this.intValue;
   }
}

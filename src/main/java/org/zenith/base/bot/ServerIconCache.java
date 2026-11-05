package org.zenith.base.bot;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.State;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.EventLoopGroupHolder;

public final class ServerIconCache {
   private static final ServerStatusPinger pinger = new ServerStatusPinger();
   private static final Map<String, ServerIconCache.Entry> entries = new ConcurrentHashMap<>();
   private static final ExecutorService pingExecutor = Executors.newFixedThreadPool(2, var0 -> {
      Thread var1 = new Thread(var0, "zenith-bot-server-pinger");
      var1.setDaemon(true);
      return var1;
   });

   private ServerIconCache() {
   }

   public static Identifier get(String var0) {
      if (var0 != null && !var0.isEmpty() && !"No Server".equalsIgnoreCase(var0)) {
         ServerIconCache.Entry var1 = entries.get(var0);
         if (var1 == null) {
            Minecraft var2 = Minecraft.getInstance();
            ServerData var3 = new ServerData(var0, var0, Type.OTHER);
            FaviconTexture var4 = FaviconTexture.forServer(var2.getTextureManager(), var0);
            var1 = new ServerIconCache.Entry(var3, var4);
            ServerIconCache.Entry var5 = entries.putIfAbsent(var0, var1);
            if (var5 != null) {
               var4.close();
               var1 = var5;
            } else {
               startPing(var1);
            }
         }

         return var1.icon.textureLocation();
      } else {
         return null;
      }
   }

   private static void startPing(ServerIconCache.Entry var0) {
      var0.info.setState(State.PINGING);
      pingExecutor.execute(() -> {
         try {
            pinger.pingServer(var0.info, () -> {}, () -> {}, EventLoopGroupHolder.remote(false));
         } catch (Exception var2) {
            var0.info.setState(State.UNREACHABLE);
         }
      });
   }

   public static void tick() {
      try {
         pinger.tick();
      } catch (Throwable var5) {
      }

      for (ServerIconCache.Entry var1 : entries.values()) {
         byte[] var2 = var1.info.getIconBytes();
         if (var2 != null && var2 != var1.lastFavicon) {
            try {
               var1.icon.upload(NativeImage.read(var2));
               var1.lastFavicon = var2;
            } catch (Throwable var4) {
            }
         }
      }
   }

   public static void clear() {
      try {
         pinger.removeAll();
      } catch (Throwable var4) {
      }

      for (ServerIconCache.Entry var1 : entries.values()) {
         try {
            var1.icon.close();
         } catch (Throwable var3) {
         }
      }

      entries.clear();
   }

   private static final class Entry {
      public final ServerData info;
      public final FaviconTexture icon;
      public byte[] lastFavicon;

      public Entry(ServerData var1, FaviconTexture var2) {
         this.info = var1;
         this.icon = var2;
      }
   }
}

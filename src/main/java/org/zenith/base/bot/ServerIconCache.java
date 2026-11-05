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
   private static final ExecutorService pingExecutor = Executors.newFixedThreadPool(2, task -> {
      Thread thread = new Thread(task, "zenith-bot-server-pinger");
      thread.setDaemon(true);
      return thread;
   });

   private ServerIconCache() {
   }

   public static Identifier get(String address) {
      if (address != null && !address.isEmpty() && !"No Server".equalsIgnoreCase(address)) {
         ServerIconCache.Entry entry = entries.get(address);
         if (entry == null) {
            Minecraft minecraft = Minecraft.getInstance();
            ServerData serverData = new ServerData(address, address, Type.OTHER);
            FaviconTexture icon = FaviconTexture.forServer(minecraft.getTextureManager(), address);
            entry = new ServerIconCache.Entry(serverData, icon);
            ServerIconCache.Entry putIfAbsent = entries.putIfAbsent(address, entry);
            if (putIfAbsent != null) {
               icon.close();
               entry = putIfAbsent;
            } else {
               startPing(entry);
            }
         }

         return entry.icon.textureLocation();
      } else {
         return null;
      }
   }

   private static void startPing(ServerIconCache.Entry entry) {
      entry.info.setState(State.PINGING);
      pingExecutor.execute(() -> {
         try {
            pinger.pingServer(entry.info, () -> {}, () -> {}, EventLoopGroupHolder.remote(false));
         } catch (Exception e) {
            entry.info.setState(State.UNREACHABLE);
         }
      });
   }

   public static void tick() {
      try {
         pinger.tick();
      } catch (Throwable e) {
      }

      for (ServerIconCache.Entry entry : entries.values()) {
         byte[] iconBytes = entry.info.getIconBytes();
         if (iconBytes != null && iconBytes != entry.lastFavicon) {
            try {
               entry.icon.upload(NativeImage.read(iconBytes));
               entry.lastFavicon = iconBytes;
            } catch (Throwable e) {
            }
         }
      }
   }

   public static void clear() {
      try {
         pinger.removeAll();
      } catch (Throwable e) {
      }

      for (ServerIconCache.Entry entry : entries.values()) {
         try {
            entry.icon.close();
         } catch (Throwable e) {
         }
      }

      entries.clear();
   }

   private static final class Entry {
      public final ServerData info;
      public final FaviconTexture icon;
      public byte[] lastFavicon;

      public Entry(ServerData info, FaviconTexture icon) {
         this.info = info;
         this.icon = icon;
      }
   }
}

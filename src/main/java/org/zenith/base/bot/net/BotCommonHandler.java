package org.zenith.base.bot.net;

import java.util.List;

import com.mojang.logging.LogUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundCustomReportDetailsPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.ServerLinks.Entry;
import net.minecraft.server.ServerLinks.UntrustedEntry;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.client.events.PacketEvent;

public abstract class BotCommonHandler implements ClientCommonPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   public final BotClient client;
   public final BotConnection connection;
   public final Map<Identifier, byte[]> serverCookies;
   @Nullable
   public String brand;
   public Map<String, String> customReportDetails;
   public ServerLinks serverLinks;

   public BotCommonHandler(BotClient var1, BotConnection var2, BotConnectionState var3) {
      this.client = var1;
      this.connection = var2;
      this.brand = var3.brand();
      this.serverCookies = var3.serverCookies();
      this.customReportDetails = var3.customReportDetails();
      this.serverLinks = var3.serverLinks();
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isOpen();
   }

   public final void firePacketEvent(Packet<?> var1) {
      this.client.getEventBus().call(new PacketEvent(this.client, var1));
   }

   @Override
   public void onPacketError(Packet packet, Exception exception) {
      LOGGER.error(
         "Bot {} failed to handle packet {}, disconnecting",
         new Object[]{this.client.name(), packet == null ? "null" : packet.getClass().getSimpleName(), exception}
      );
      this.connection.disconnect(Component.translatable("disconnect.packetError"));
   }

   // bots have no screen to apply server post effects to
   @Override
   public void handlePostEffects(net.minecraft.network.protocol.common.ClientboundPostEffectsPacket packet) {
   }

   @Override
   public void handleKeepAlive(ClientboundKeepAlivePacket packet) {
      this.sendPacket(new ServerboundKeepAlivePacket(packet.getId()));
   }

   @Override
   public void handlePing(ClientboundPingPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.sendPacket(new ServerboundPongPacket(packet.getId()));
   }

   @Override
   public void handleCustomPayload(ClientboundCustomPayloadPacket packet) {
      CustomPacketPayload var2 = packet.payload();
      if (!(var2 instanceof DiscardedPayload)) {
         PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
         if (var2 instanceof BrandPayload var3) {
            this.brand = var3.brand();
         } else {
            this.onCustomPayload(var2);
         }
      }
   }

   public void onCustomPayload(CustomPacketPayload var1) {
      LOGGER.debug("Bot {} ignored custom payload {}", this.client.name(), var1.type().id());
   }

   @Override
   public void handleDisconnect(ClientboundDisconnectPacket packet) {
      this.connection.disconnect(packet.reason());
   }

   @Override
   public void handleResourcePackPush(ClientboundResourcePackPushPacket packet) {
      this.connection.send(new ServerboundResourcePackPacket(packet.id(), Action.ACCEPTED));
      this.connection.send(new ServerboundResourcePackPacket(packet.id(), Action.DOWNLOADED));
      this.connection.send(new ServerboundResourcePackPacket(packet.id(), Action.SUCCESSFULLY_LOADED));
   }

   @Override
   public void handleResourcePackPop(ClientboundResourcePackPopPacket packet) {
   }

   @Override
   public void handleRequestCookie(ClientboundCookieRequestPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.connection.send(new ServerboundCookieResponsePacket(packet.key(), this.serverCookies.get(packet.key())));
   }

   @Override
   public void handleStoreCookie(ClientboundStoreCookiePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.serverCookies.put(packet.key(), packet.payload());
   }

   @Override
   public void handleCustomReportDetails(ClientboundCustomReportDetailsPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.customReportDetails = packet.details();
   }

   @Override
   public void handleServerLinks(ClientboundServerLinksPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      List<Entry> var2 = new ArrayList<>(packet.links().size());

      for (UntrustedEntry var4 : packet.links()) {
         try {
            URI var5 = Util.parseAndValidateUntrustedUri(var4.link());
            var2.add(new Entry(var4.type(), var5));
         } catch (Exception var6) {
            LOGGER.warn("Bot {} received invalid server link {}: {}", new Object[]{this.client.name(), var4.type(), var4.link()});
         }
      }

      this.serverLinks = new ServerLinks(var2);
   }

   @Override
   public void handleTransfer(ClientboundTransferPacket packet) {
      this.connection.disconnect(Component.literal("Bot transfer not supported"));
   }

   @Override
   public void onDisconnect(DisconnectionDetails info) {
      this.client.onConnectionTerminated(info);
   }

   public void sendPacket(Packet<?> var1) {
      if (this.connection.getPacketListener() == this) {
         this.connection.send(var1);
      }
   }

   public String getBrand() {
      return this.brand == null ? "" : this.brand;
   }

   public BotConnection getConnection() {
      return this.connection;
   }
}

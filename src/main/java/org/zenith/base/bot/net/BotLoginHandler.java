package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.util.Crypt;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;

public final class BotLoginHandler implements ClientLoginPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BotClient client;
   private final BotConnection connection;
   private final Map<Identifier, byte[]> serverCookies = new HashMap<>();

   public BotLoginHandler(BotClient client, BotConnection connection) {
      this.client = client;
      this.connection = connection;
   }

   @Override
   public void handleHello(ClientboundHelloPacket packet) {
      if (packet.shouldAuthenticate()) {
         this.connection.disconnect(Component.literal("Online-mode server is not supported by bots"));
      } else {
         Cipher cipher;
         Cipher cipher2;
         ServerboundKeyPacket serverboundKeyPacket;
         try {
            SecretKey secretKey = Crypt.generateSecretKey();
            PublicKey publicKey = packet.getPublicKey();
            cipher = Crypt.getCipher(2, secretKey);
            cipher2 = Crypt.getCipher(1, secretKey);
            serverboundKeyPacket = new ServerboundKeyPacket(secretKey, publicKey, packet.getChallenge());
         } catch (Exception e) {
            throw new IllegalStateException("Protocol error", e);
         }

         this.connection.send(serverboundKeyPacket, PacketSendListener.thenRun(() -> this.connection.setupEncryption(cipher, cipher2)));
      }
   }

   @Override
   public void handleLoginFinished(ClientboundLoginFinishedPacket packet) {
      if (this.connection.isOpen() && this.connection.getPacketListener() == this) {
         GameProfile gameProfile = packet.gameProfile();
         this.client.setPhase(BotPhase.CONFIGURATION);
         LOGGER.debug("Bot {} login success, profileId={}", this.client.name(), gameProfile.id());
         BotConnectionState botConnectionState = new BotConnectionState(
            gameProfile,
            ClientRegistryLayer.createRegistryAccess().compositeAccess(),
            FeatureFlags.DEFAULT_FLAGS,
            null,
            this.serverCookies,
            Map.of(),
            ServerLinks.EMPTY
         );
         BotConfigHandler botConfigHandler = new BotConfigHandler(this.client, this.connection, botConnectionState);
         this.connection.transitionInbound(ConfigurationProtocols.CLIENTBOUND, botConfigHandler);
         this.connection.send(ServerboundLoginAcknowledgedPacket.INSTANCE);
         this.connection.transitionOutbound(ConfigurationProtocols.SERVERBOUND);
         this.connection.send(new ServerboundCustomPayloadPacket(new BrandPayload(this.client.getConfig().brand())));
         this.connection.send(new ServerboundClientInformationPacket(ClientInformation.createDefault()));
      }
   }

   @Override
   public void handleDisconnect(ClientboundLoginDisconnectPacket packet) {
      this.connection.disconnect(packet.reason());
   }

   @Override
   public void handleCompression(ClientboundLoginCompressionPacket packet) {
      this.connection.setCompressionThreshold(packet.getCompressionThreshold(), false);
   }

   @Override
   public void handleCustomQuery(ClientboundCustomQueryPacket packet) {
      this.connection.send(new ServerboundCustomQueryAnswerPacket(packet.transactionId(), null));
   }

   @Override
   public void handleRequestCookie(ClientboundCookieRequestPacket packet) {
      this.connection.send(new ServerboundCookieResponsePacket(packet.key(), this.serverCookies.get(packet.key())));
   }

   @Override
   public void onDisconnect(DisconnectionDetails info) {
      this.client.onConnectionTerminated(info);
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isOpen();
   }
}

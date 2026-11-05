package org.zenith.base.bot.via;

import com.mojang.logging.LogUtils;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class BotViaVersionProvider extends BaseVersionProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private final VersionProvider delegate;

   public BotViaVersionProvider(@Nullable VersionProvider delegate) {
      this.delegate = delegate;
   }

   public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception {
      Integer targetProtocol = connection.isClientSide() ? BotVia.targetProtocol(connection.getChannel()) : null;
      if (targetProtocol != null) {
         ProtocolInfo nonNull = Objects.requireNonNull(connection.getProtocolInfo());
         if (!ProtocolVersion.isRegistered(targetProtocol)) {
            LOGGER.warn("Bot ViaVersion: protocol {} is not registered, using native", targetProtocol);
            return nonNull.protocolVersion();
         } else {
            ProtocolVersion protocol = ProtocolVersion.getProtocol(targetProtocol);
            boolean flag = protocol.equals(nonNull.protocolVersion()) || Via.getManager().getProtocolManager().getProtocolPath(nonNull.protocolVersion(), protocol) != null;
            if (!flag) {
               LOGGER.warn("Bot ViaVersion: no protocol path {} -> {}, using native", nonNull.protocolVersion(), protocol);
               return nonNull.protocolVersion();
            } else {
               LOGGER.info("Bot ViaVersion: translating {} -> {}", nonNull.protocolVersion(), protocol);
               return protocol;
            }
         }
      } else if (this.delegate != null) {
         return this.delegate.getClosestServerProtocol(connection);
      } else {
         return connection.isClientSide() ? Objects.requireNonNull(connection.getProtocolInfo()).protocolVersion() : super.getClosestServerProtocol(connection);
      }
   }
}

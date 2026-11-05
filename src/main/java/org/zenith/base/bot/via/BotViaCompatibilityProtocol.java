package org.zenith.base.bot.via;

import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.exception.InformativeException;
import com.viaversion.viaversion.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.protocols.v1_19_3to1_19_4.packet.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.storage.ConfigurationState;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.storage.ConfigurationState.BridgePhase;

public final class BotViaCompatibilityProtocol extends AbstractSimpleProtocol {
   public static final BotViaCompatibilityProtocol INSTANCE = new BotViaCompatibilityProtocol();

   private BotViaCompatibilityProtocol() {
   }

   public void registerPackets() {
   }

   public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws InformativeException, CancelException {
      if (direction == Direction.CLIENTBOUND
         && state == State.CONFIGURATION
         && packetWrapper.getId() == ClientboundPackets1_19_4.RESPAWN.getId()
         && ProtocolVersion.v1_19_4.equals(packetWrapper.user().getProtocolInfo().serverProtocolVersion())) {
         ConfigurationState configurationState = (ConfigurationState)packetWrapper.user().get(ConfigurationState.class);
         if (configurationState != null && configurationState.bridgePhase() != BridgePhase.NONE) {
            packetWrapper.passthrough(Types.REMAINING_BYTES);
            packetWrapper.write(Types.VAR_INT, 0);
         }
      }

      super.transform(direction, state, packetWrapper);
   }
}

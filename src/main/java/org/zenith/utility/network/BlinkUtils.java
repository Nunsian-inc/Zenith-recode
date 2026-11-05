package org.zenith.utility.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;

public final class BlinkUtils {
   private BlinkUtils() {
   }

   public static boolean method1444(Packet<?> var0) {
      return var0 instanceof ServerboundMovePlayerPacket || var0 instanceof ServerboundMoveVehiclePacket;
   }
}

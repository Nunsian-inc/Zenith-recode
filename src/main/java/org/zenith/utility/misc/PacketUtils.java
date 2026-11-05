package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import org.zenith.MinecraftAccess;

@Deprecated
public class PacketUtils implements MinecraftAccess {
   private static final List<Packet<?>> items = new ArrayList<>();

   public static void method3150(Packet<?> playerCommandPacket) {
      items.add(playerCommandPacket);

      try {
         mc.getConnection().send(playerCommandPacket);
      } finally {
         items.remove(playerCommandPacket);
      }
   }

   public static void sendPacket(Packet<?> setCarriedItemPacket) {
      mc.getConnection().send(setCarriedItemPacket);
   }

   public static List<Packet<?>> getItems() {
      return items;
   }

   public static void method3151() {
      items.clear();
   }
}

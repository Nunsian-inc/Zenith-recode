package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import org.zenith.MinecraftAccess;

@Deprecated
public class PacketUtils implements MinecraftAccess {
   private static final List<Packet<?>> items = new ArrayList<>();

   public static void method3150(Packet<?> var0) {
      items.add(var0);

      try {
         mc.getConnection().send(var0);
      } finally {
         items.remove(var0);
      }
   }

   public static void sendPacket(Packet<?> var0) {
      mc.getConnection().send(var0);
   }

   public static List<Packet<?>> getItems() {
      return items;
   }

   public static void method3151() {
      items.clear();
   }
}

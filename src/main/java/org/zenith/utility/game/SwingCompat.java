package org.zenith.utility.game;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

// 1.21.11 LivingEntity#swing(hand) animated locally and LocalPlayer sent ServerboundSwingPacket(hand).
// 26.3: swing(hand, animation, sync) only animates; the client tells the server with ServerboundPunchPacket,
// which exists for the main hand only (vanilla sends nothing for off-hand swings).
public final class SwingCompat {
   private SwingCompat() {
   }

   public static void swing(LivingEntity entity, InteractionHand hand) {
      swingLocal(entity, hand);
      if (hand == InteractionHand.MAIN_HAND && entity instanceof LocalPlayer player && player.connection != null) {
         player.connection.send(ServerboundPunchPacket.INSTANCE);
      }
   }

   public static void swingLocal(LivingEntity entity, InteractionHand hand) {
      entity.swing(hand, entity.getItemInHand(hand).getAttackAnimation(), false);
   }
}

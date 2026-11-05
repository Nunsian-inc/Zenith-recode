package org.zenith.utility.game;

import lombok.Generated;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Blocks;
import org.zenith.ZenithAccess;
import org.zenith.base.rotation.mods.BcCamera;
import org.zenith.client.modules.impl.combat.CriticalsModule;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

public final class AttackUtils implements ZenithAccess {
   public static void method816(Entity var0) {
      mc.gameMode.attack(mc.player, var0);
      org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
      BcCamera.method3546();
   }

   public static boolean method817() {
      return mc.player.hasEffect(MobEffects.BLINDNESS)
         || mc.player.hasEffect(MobEffects.LEVITATION)
         || UseItemUtils.method6513(Blocks.COBWEB)
         || mc.player.isUnderWater()
         || mc.player.isSwimming()
         || mc.player.isInLava()
         || mc.player.onClimbable()
         || !UseItemUtils.method6511(Pose.STANDING) && mc.player.isCrouching()
         || mc.player.getAbilities().flying;
   }

   public static boolean method818(PlayerSimulation var0) {
      return var0.method6084(MobEffects.BLINDNESS)
         || var0.method6084(MobEffects.LEVITATION)
         || UseItemUtils.method6515(var0.box, Blocks.COBWEB)
         || var0.method6078()
         || var0.flag8
         || var0.method6064()
         || var0.method6046()
         || !UseItemUtils.method6511(Pose.STANDING) && mc.player.isCrouching()
         || mc.player.getAbilities().flying;
   }

   public static boolean method819() {
      boolean var0 = mc.player.fallDistance > (method821() ? MathUtils.method2643(0.15F, 0.7F) : 0.0F)
         && (mc.player.fallDistance < 0.08 || !PlayerSimulation.method6031(1).flag4);
      return !mc.player.onGround() && var0 || CriticalsModule.INSTANCE.isEnabled() && CriticalsModule.INSTANCE.method4672();
   }

   private static boolean method820() {
      return mc.player.onGround() || mc.player.fallDistance < 0.12F || mc.player.isSwimming();
   }

   private static boolean method821() {
      return false;
   }

   public static boolean method822(PlayerSimulation var0) {
      boolean var1 = var0.floatValue3 > 0.0F && (var0.floatValue3 < 0.08 || !PlayerSimulation.method6031(2).flag4);
      return !var0.flag4 && var1 || CriticalsModule.INSTANCE.isEnabled() && CriticalsModule.INSTANCE.method4672();
   }

   @Generated
   private AttackUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

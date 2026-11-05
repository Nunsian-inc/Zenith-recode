package org.zenith.utility.game;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class Npc extends RemotePlayer {
   public Npc(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
   }

   @Override
   public boolean skipAttackInteraction(Entity attacker) {
      this.animateHurt(this.method7023(attacker));
      Minecraft var2 = Minecraft.getInstance();
      if (var2.player != null) {
         this.level()
            .playSound(
               var2.player,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.PLAYER_HURT,
               this.getSoundSource(),
               1.0F,
               1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
            );
      }

      return false;
   }

   private float method7023(Entity var1) {
      if (var1 == null) {
         return 0.0F;
      } else {
         double var2 = var1.getX() - this.getX();
         double var4 = var1.getZ() - this.getZ();
         return (float)(Mth.atan2(var4, var2) * 180.0 / Math.PI - this.getYRot());
      }
   }

   @Override
   public void knockback(double strength, double x, double z, net.minecraft.world.damagesource.DamageSource source, float damage, boolean comesFromEffect) {
   }

   @Override
   public void lerpMotion(Vec3 clientVelocity) {
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public boolean ignoreExplosion(Explosion explosion) {
      return true;
   }
}

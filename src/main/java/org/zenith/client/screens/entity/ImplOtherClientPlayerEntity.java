package org.zenith.client.screens.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

public class ImplOtherClientPlayerEntity extends RemotePlayer {
   public ImplOtherClientPlayerEntity(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
   }

   @Override
   public boolean isAttackable() {
      return false;
   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public void push(Entity entity) {
   }

   @Override
   public void doPush(Entity entity) {
   }

   @Override
   public boolean canBeHitByProjectile() {
      return false;
   }
}

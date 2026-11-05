package org.zenith.base.bot.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;

public class BotRemotePlayer extends Player {
   private final BotPlayHandler networkHandler;
   private Vec3 clientVelocity = Vec3.ZERO;
   private int velocityLerpDivisor;

   public BotRemotePlayer(BotWorld world, BotPlayHandler networkHandler, GameProfile profile) {
      super(world, profile);
      this.networkHandler = networkHandler;
      this.noPhysics = true;
   }

   @Nullable
   private GameType getListedGameMode() {
      PlayerInfo playerListEntry = this.networkHandler.getPlayerListEntry(this.getUUID());
      return playerListEntry == null ? null : playerListEntry.getGameMode();
   }

   @Override
   public boolean isSpectator() {
      return this.getListedGameMode() == GameType.SPECTATOR;
   }

   @Override
   public boolean isCreative() {
      return this.getListedGameMode() == GameType.CREATIVE;
   }

   @Override
   public boolean hurtClient(DamageSource source) {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      this.calculateEntityAnimation(false);
   }

   @Override
   public void aiStep() {
      if (this.isInterpolating()) {
         this.getInterpolation().interpolate();
      }

      if (this.lerpHeadSteps > 0) {
         this.lerpHeadRotationStep(this.lerpHeadSteps, this.lerpYHeadRot);
         this.lerpHeadSteps--;
      }

      if (this.velocityLerpDivisor > 0) {
         this.addDeltaMovement(
            new Vec3(
               (this.clientVelocity.x - this.getDeltaMovement().x) / this.velocityLerpDivisor,
               (this.clientVelocity.y - this.getDeltaMovement().y) / this.velocityLerpDivisor,
               (this.clientVelocity.z - this.getDeltaMovement().z) / this.velocityLerpDivisor
            )
         );
         this.velocityLerpDivisor--;
      }

      this.pushEntities();
   }

   @Override
   public void lerpMotion(Vec3 clientVelocity) {
      this.clientVelocity = clientVelocity;
      this.velocityLerpDivisor = this.getType().updateInterval() + 1;
   }

   @Override
   public void updatePlayerPose() {
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket packet) {
      super.recreateFromPacket(packet);
      this.setOldPosAndRot();
   }

   @Override
   public GameType gameMode() {
      GameType listedGameMode = this.getListedGameMode();
      return listedGameMode != null ? listedGameMode : GameType.SURVIVAL;
   }
}

package org.zenith.utility.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.ServerPositionTracker;
import org.zenith.utility.misc.PositionResolver;

@Mixin({RemotePlayer.class})
public class MixinOtherClientPlayerEntity extends AbstractClientPlayer implements PositionResolver {
   @Unique
   private double backUpX;
   @Unique
   private double backUpY;
   @Unique
   private double backUpZ;

   public MixinOtherClientPlayerEntity(ClientLevel level, GameProfile gameProfile) {
      super(level, gameProfile);
   }

   @Unique
   @Override
   public void zenithDLC$resolve() {
      this.backUpX = this.getX();
      this.backUpY = this.getY();
      this.backUpZ = this.getZ();
      Vec3 vec = new Vec3(
         ((ServerPositionTracker)(Object)this).zenithDLC$getPrevServerX(), ((ServerPositionTracker)(Object)this).zenithDLC$getPrevServerY(), ((ServerPositionTracker)(Object)this).zenithDLC$getPrevServerZ()
      );
      Vec3 position = this.position();
      if (Minecraft.getInstance().player.distanceToSqr(vec) > Minecraft.getInstance().player.distanceToSqr(position)) {
         this.setPos(position.x, position.y, position.z);
      } else {
         this.setPos(vec.x, vec.y, vec.z);
      }
   }

   @Unique
   @Override
   public void zenithDLC$releaseResolver() {
      if (this.backUpY != -999.0) {
         this.setPos(this.backUpX, this.backUpY, this.backUpZ);
         this.backUpY = -999.0;
      }
   }
}

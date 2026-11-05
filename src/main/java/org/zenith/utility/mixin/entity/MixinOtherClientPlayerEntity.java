package org.zenith.utility.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.Handler6;
import org.zenith.utility.misc.Handler8;

@Mixin({RemotePlayer.class})
public class MixinOtherClientPlayerEntity extends AbstractClientPlayer implements Handler8 {
   @Unique
   private double backUpX;
   @Unique
   private double backUpY;
   @Unique
   private double backUpZ;

   public MixinOtherClientPlayerEntity(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
   }

   @Unique
   @Override
   public void zenithDLC$resolve() {
      this.backUpX = this.getX();
      this.backUpY = this.getY();
      this.backUpZ = this.getZ();
      Vec3 var1 = new Vec3(
         ((Handler6)(Object)this).zenithDLC$getPrevServerX(), ((Handler6)(Object)this).zenithDLC$getPrevServerY(), ((Handler6)(Object)this).zenithDLC$getPrevServerZ()
      );
      Vec3 var2 = this.position();
      if (Minecraft.getInstance().player.distanceToSqr(var1) > Minecraft.getInstance().player.distanceToSqr(var2)) {
         this.setPos(var2.x, var2.y, var2.z);
      } else {
         this.setPos(var1.x, var1.y, var1.z);
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

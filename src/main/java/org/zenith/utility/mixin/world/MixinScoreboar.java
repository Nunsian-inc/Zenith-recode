package org.zenith.utility.mixin.world;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Scoreboard.class})
public abstract class MixinScoreboar {
   @Shadow
   @Nullable
   public abstract PlayerTeam getPlayersTeam(String var1);

   @Inject(
      method = {"removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/world/scores/PlayerTeam;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void remove(String var1, PlayerTeam var2, CallbackInfo var3) {
      if (this.getPlayersTeam(var1) != var2) {
         var3.cancel();
      }
   }
}

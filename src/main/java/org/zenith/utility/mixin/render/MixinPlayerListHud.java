package org.zenith.utility.mixin.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.utility.misc.ScoreboardParser;

@Mixin({PlayerTabOverlay.class})
public abstract class MixinPlayerListHud {
   @Shadow
   public Component header;
   @Shadow
   public Component footer;
   @Unique
   private Component zenith$savedHeader;
   @Unique
   private Component zenith$savedFooter;

   @Shadow
   public abstract Component decorateName(PlayerInfo var1, MutableComponent var2);

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$streamerTabStart(GuiGraphicsExtractor var1, int var2, Scoreboard var3, Objective var4, CallbackInfo var5) {
      if (ScoreboardParser.isActive()) {
         this.zenith$savedHeader = this.header;
         this.zenith$savedFooter = this.footer;
         this.header = ScoreboardParser.method7074(this.header);
         this.footer = ScoreboardParser.method7074(this.footer);
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$streamerTabEnd(GuiGraphicsExtractor var1, int var2, Scoreboard var3, Objective var4, CallbackInfo var5) {
      if (this.zenith$savedHeader != null || this.zenith$savedFooter != null) {
         this.header = this.zenith$savedHeader;
         this.footer = this.zenith$savedFooter;
         this.zenith$savedHeader = null;
         this.zenith$savedFooter = null;
      }
   }

   @Inject(
      method = {"getNameForDisplay(Lnet/minecraft/client/multiplayer/PlayerInfo;)Lnet/minecraft/network/chat/Component;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getPlayerName(PlayerInfo var1, CallbackInfoReturnable<Component> var2) {
      if (NameProtectModule.INSTANCE.isEnabled()) {
         Minecraft var3 = Minecraft.getInstance();
         boolean var4 = var3.player != null && var3.player.getUUID().equals(var1.getProfile().id());
         if (var4 || NameProtectModule.INSTANCE.method5140() && Zenith.getInstance().getFriends().method7627(var1.getProfile().name())) {
            var2.setReturnValue(
               NameProtectModule.INSTANCE
                  .method5142(
                     var1.getTabListDisplayName() != null
                        ? this.decorateName(var1, var1.getTabListDisplayName().copy())
                        : this.decorateName(var1, PlayerTeam.formatNameForTeam(var1.getTeam(), Component.literal(var1.getProfile().name())))
                  )
            );
            return;
         }
      }

      Component var5 = StreamerModeModule.INSTANCE.method6290(var1);
      if (var5 != null) {
         var2.setReturnValue(var5);
      }
   }

   @Inject(
      method = {"getPlayerInfos()Ljava/util/List;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void collectPlayerEntries(CallbackInfoReturnable<List<PlayerInfo>> var1) {
      Minecraft var2 = Minecraft.getInstance();
      if (var2.player != null) {
         List<PlayerInfo> var3 = (List)var1.getReturnValue();
         if (var3 != null && !var3.isEmpty()) {
            if (StreamerModeModule.INSTANCE.isEnabled()) {
               ArrayList var9 = new ArrayList();

               for (Object var11_o : (Iterable<?>)(Object)(var3)) {
                  PlayerInfo var11 = (PlayerInfo)var11_o;
                  if (!var11.getProfile().id().equals(var2.player.getUUID())) {
                     var9.add(var11);
                  }
               }

               StreamerModeModule.INSTANCE.method6296(var9);
               var1.setReturnValue(var9);
            } else if (NameProtectModule.INSTANCE.isEnabled()) {
               int var4 = NameProtectModule.INSTANCE.getIntValue18();
               if (var4 != Integer.MIN_VALUE) {
                  if (var3.size() >= 2) {
                     var var5 = new ArrayList<>(var3);
                     int var6 = -1;

                     for (int var7 = 0; var7 < var5.size(); var7++) {
                        if (((PlayerInfo)var5.get(var7)).getProfile().id().equals(var2.player.getUUID())) {
                           var6 = var7;
                           break;
                        }
                     }

                     if (var6 != -1) {
                        int var12 = Math.max(0, Math.min(var5.size() - 1, var4));
                        if (var12 != var6) {
                           PlayerInfo var8 = (PlayerInfo)var5.remove(var6);
                           if (var12 > var6) {
                              var12--;
                           }

                           var5.add(Math.min(var5.size(), var12), var8);
                           var1.setReturnValue(var5);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}

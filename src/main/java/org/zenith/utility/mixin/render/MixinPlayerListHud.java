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
   public abstract Component decorateName(PlayerInfo playerInfo, MutableComponent formatNameForTeam);

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$streamerTabStart(GuiGraphicsExtractor guiGraphicsExtractor, int n, Scoreboard scoreboard, Objective objective, CallbackInfo callbackInfo) {
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
   private void zenith$streamerTabEnd(GuiGraphicsExtractor guiGraphicsExtractor, int n, Scoreboard scoreboard, Objective objective, CallbackInfo callbackInfo) {
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
   public void getPlayerName(PlayerInfo playerInfo, CallbackInfoReturnable<Component> callbackInfoReturnable) {
      if (NameProtectModule.INSTANCE.isEnabled()) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean flag = minecraft.player != null && minecraft.player.getUUID().equals(playerInfo.getProfile().id());
         if (flag || NameProtectModule.INSTANCE.method5140() && Zenith.getInstance().getFriends().method7627(playerInfo.getProfile().name())) {
            callbackInfoReturnable.setReturnValue(
               NameProtectModule.INSTANCE
                  .method5142(
                     playerInfo.getTabListDisplayName() != null
                        ? this.decorateName(playerInfo, playerInfo.getTabListDisplayName().copy())
                        : this.decorateName(playerInfo, PlayerTeam.formatNameForTeam(playerInfo.getTeam(), Component.literal(playerInfo.getProfile().name())))
                  )
            );
            return;
         }
      }

      Component returnValue = StreamerModeModule.INSTANCE.method6290(playerInfo);
      if (returnValue != null) {
         callbackInfoReturnable.setReturnValue(returnValue);
      }
   }

   @Inject(
      method = {"getPlayerInfos()Ljava/util/List;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void collectPlayerEntries(CallbackInfoReturnable<List<PlayerInfo>> callbackInfoReturnable) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null) {
         List<PlayerInfo> returnValue = (List)callbackInfoReturnable.getReturnValue();
         if (returnValue != null && !returnValue.isEmpty()) {
            if (StreamerModeModule.INSTANCE.isEnabled()) {
               ArrayList arrayList = new ArrayList();

               for (PlayerInfo playerInfo : returnValue) {
                  if (!playerInfo.getProfile().id().equals(minecraft.player.getUUID())) {
                     arrayList.add(playerInfo);
                  }
               }

               StreamerModeModule.INSTANCE.method6296(arrayList);
               callbackInfoReturnable.setReturnValue(arrayList);
            } else if (NameProtectModule.INSTANCE.isEnabled()) {
               int n = NameProtectModule.INSTANCE.getIntValue18();
               if (n != Integer.MIN_VALUE) {
                  if (returnValue.size() >= 2) {
                     var playerInfos = new ArrayList<>(returnValue);
                     int index = -1;

                     for (int i = 0; i < playerInfos.size(); i++) {
                        if (((PlayerInfo)playerInfos.get(i)).getProfile().id().equals(minecraft.player.getUUID())) {
                           index = i;
                           break;
                        }
                     }

                     if (index != -1) {
                        int n2 = Math.max(0, Math.min(playerInfos.size() - 1, n));
                        if (n2 != index) {
                           PlayerInfo playerInfo = (PlayerInfo)playerInfos.remove(index);
                           if (n2 > index) {
                              n2--;
                           }

                           playerInfos.add(Math.min(playerInfos.size(), n2), playerInfo);
                           callbackInfoReturnable.setReturnValue(playerInfos);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}

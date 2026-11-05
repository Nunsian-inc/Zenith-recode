package org.zenith.utility.mixin.network;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.comand.CommandManager;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.ChatSendEvent;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.HealthUpdateEvent;
import org.zenith.client.events.PacketReceiveEvent;

@Mixin({ClientPacketListener.class})
public class MixinClientPlayNetworkHandler {
   @Redirect(
      method = {"handleBundlePacket(Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"
      )
   )
   private void zenith$applyBundled(Packet<?> var1, PacketListener var2) {
      if (!EventManager.hasListeners(PacketReceiveEvent.class) || !((PacketReceiveEvent)EventManager.call(new PacketReceiveEvent(var1))).isCancelled()) {
         zenith$apply(var1, var2);
      }
   }

   private static <T extends PacketListener> void zenith$apply(Packet<?> var0, PacketListener var1) {
      ((Packet)var0).handle(var1);
   }

   @Inject(
      method = {"sendChat(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendChatMessageHook(@NotNull String var1, CallbackInfo var2) {
      CommandManager var3 = Zenith.getInstance().getCommandManager();
      String var4 = var3.getPrefix();
      if (var1.startsWith(var4)) {
         String var10 = var1.substring(var4.length());
         int var6 = var10.indexOf(32);
         String var7 = var6 < 0 ? var10 : var10.substring(0, var6);
         if (var3.getDispatcher().getRoot().getChild(var7) != null) {
            try {
               var3.getDispatcher().execute(var10, var3.getSource());
            } catch (CommandSyntaxException var9) {
            }
         } else {
            EventManager.call(new ChatSendEvent(var1, ChatSendEvent.Type.PRE, ChatSendEvent.Type2.MESSAGE));
         }

         var2.cancel();
      } else {
         ChatSendEvent var5 = new ChatSendEvent(var1, ChatSendEvent.Type.PRE, ChatSendEvent.Type2.MESSAGE);
         EventManager.call(var5);
         if (var5.isCancelled()) {
            var2.cancel();
         }
      }
   }

   @Inject(
      method = {"sendChat(Ljava/lang/String;)V"},
      at = {@At("RETURN")}
   )
   private void sendChatMessageHookPost(@NotNull String var1, CallbackInfo var2) {
      ChatSendEvent var3 = new ChatSendEvent(var1, ChatSendEvent.Type.POST, ChatSendEvent.Type2.MESSAGE);
      EventManager.call(var3);
   }

   @Inject(
      method = {"sendCommand(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendChatCommandMessageHook(@NotNull String var1, CallbackInfo var2) {
      ChatSendEvent var3 = new ChatSendEvent(var1, ChatSendEvent.Type.PRE, ChatSendEvent.Type2.COMMAND);
      EventManager.call(var3);
      if (var3.isCancelled()) {
         var2.cancel();
      }
   }

   @Inject(
      method = {"sendCommand(Ljava/lang/String;)V"},
      at = {@At("RETURN")}
   )
   private void sendChatCommandHookPost(@NotNull String var1, CallbackInfo var2) {
      ChatSendEvent var3 = new ChatSendEvent(var1, ChatSendEvent.Type.POST, ChatSendEvent.Type2.COMMAND);
      EventManager.call(var3);
   }

   @Inject(
      method = {"handleSystemChat(Lnet/minecraft/network/protocol/game/ClientboundSystemChatPacket;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handleSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"
      )},
      cancellable = true
   )
   private void onGameMessage(ClientboundSystemChatPacket var1, CallbackInfo var2) {
      ChatReceiveEvent var3 = new ChatReceiveEvent(var1.content());
      EventManager.call(var3);
      if (var3.isCancelled()) {
         var2.cancel();
         if (var3.method7859()) {
            Minecraft.getInstance().gui.chatListener().handleSystemMessage(var3.getText(), var1.overlay());
         }
      }
   }

   @Inject(
      method = {"handleEntityEvent(Lnet/minecraft/network/protocol/game/ClientboundEntityEventPacket;)V"},
      at = {@At("RETURN")}
   )
   private void updateHealth(ClientboundEntityEventPacket var1, CallbackInfo var2) {
      EventManager.call(new EntityStatusEvent(var1));
   }

   @Inject(
      method = {"handleSetHealth(Lnet/minecraft/network/protocol/game/ClientboundSetHealthPacket;)V"},
      at = {@At("RETURN")}
   )
   private void updateHealth(ClientboundSetHealthPacket var1, CallbackInfo var2) {
      EventManager.call(new HealthUpdateEvent());
   }

   @ModifyExpressionValue(
      method = {"handleMovePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerPositionPacket;)V", "handleRotatePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerRotationPacket;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getYRot()F"
      )}
   )
   private float hookSilentRotationYaw(float var1) {
      Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return var2 == null ? var1 : var2.getFloatValue();
   }

   @ModifyExpressionValue(
      method = {"handleMovePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerPositionPacket;)V", "handleRotatePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerRotationPacket;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getXRot()F"
      )}
   )
   private float hookSilentRotationPitch(float var1) {
      Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return var2 == null ? var1 : var2.getFloatValue2();
   }
}

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
   private void zenith$applyBundled(Packet<?> packet, PacketListener listener) {
      if (!EventManager.hasListeners(PacketReceiveEvent.class) || !((PacketReceiveEvent)EventManager.call(new PacketReceiveEvent(packet))).isCancelled()) {
         zenith$apply(packet, listener);
      }
   }

   private static <T extends PacketListener> void zenith$apply(Packet<?> packet, PacketListener listener) {
      ((Packet)packet).handle(listener);
   }

   @Inject(
      method = {"sendChat(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendChatMessageHook(@NotNull String message, CallbackInfo callbackInfo) {
      CommandManager commandManager = Zenith.getInstance().getCommandManager();
      String prefix = commandManager.getPrefix();
      if (message.startsWith(prefix)) {
         String input = message.substring(prefix.length());
         int indexOf = input.indexOf(32);
         String name = indexOf < 0 ? input : input.substring(0, indexOf);
         if (commandManager.getDispatcher().getRoot().getChild(name) != null) {
            try {
               commandManager.getDispatcher().execute(input, commandManager.getSource());
            } catch (CommandSyntaxException e) {
            }
         } else {
            EventManager.call(new ChatSendEvent(message, ChatSendEvent.Type.PRE, ChatSendEvent.Type2.MESSAGE));
         }

         callbackInfo.cancel();
      } else {
         ChatSendEvent event = new ChatSendEvent(message, ChatSendEvent.Type.PRE, ChatSendEvent.Type2.MESSAGE);
         EventManager.call(event);
         if (event.isCancelled()) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"sendChat(Ljava/lang/String;)V"},
      at = {@At("RETURN")}
   )
   private void sendChatMessageHookPost(@NotNull String message, CallbackInfo callbackInfo) {
      ChatSendEvent event = new ChatSendEvent(message, ChatSendEvent.Type.POST, ChatSendEvent.Type2.MESSAGE);
      EventManager.call(event);
   }

   @Inject(
      method = {"sendCommand(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendChatCommandMessageHook(@NotNull String message, CallbackInfo callbackInfo) {
      ChatSendEvent event = new ChatSendEvent(message, ChatSendEvent.Type.PRE, ChatSendEvent.Type2.COMMAND);
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"sendCommand(Ljava/lang/String;)V"},
      at = {@At("RETURN")}
   )
   private void sendChatCommandHookPost(@NotNull String message, CallbackInfo callbackInfo) {
      ChatSendEvent event = new ChatSendEvent(message, ChatSendEvent.Type.POST, ChatSendEvent.Type2.COMMAND);
      EventManager.call(event);
   }

   @Inject(
      method = {"handleSystemChat(Lnet/minecraft/network/protocol/game/ClientboundSystemChatPacket;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handleSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"
      )},
      cancellable = true
   )
   private void onGameMessage(ClientboundSystemChatPacket systemChatPacket, CallbackInfo callbackInfo) {
      ChatReceiveEvent event = new ChatReceiveEvent(systemChatPacket.content());
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
         if (event.method7859()) {
            Minecraft.getInstance().gui.chatListener().handleSystemMessage(event.getText(), systemChatPacket.overlay());
         }
      }
   }

   @Inject(
      method = {"handleEntityEvent(Lnet/minecraft/network/protocol/game/ClientboundEntityEventPacket;)V"},
      at = {@At("RETURN")}
   )
   private void updateHealth(ClientboundEntityEventPacket entityStatusS2CPacket, CallbackInfo callbackInfo) {
      EventManager.call(new EntityStatusEvent(entityStatusS2CPacket));
   }

   @Inject(
      method = {"handleSetHealth(Lnet/minecraft/network/protocol/game/ClientboundSetHealthPacket;)V"},
      at = {@At("RETURN")}
   )
   private void updateHealth(ClientboundSetHealthPacket setHealthPacket, CallbackInfo callbackInfo) {
      EventManager.call(new HealthUpdateEvent());
   }

   @ModifyExpressionValue(
      method = {"handleMovePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerPositionPacket;)V", "handleRotatePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerRotationPacket;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getYRot()F"
      )}
   )
   private float hookSilentRotationYaw(float f) {
      Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return currentRotation == null ? f : currentRotation.getFloatValue();
   }

   @ModifyExpressionValue(
      method = {"handleMovePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerPositionPacket;)V", "handleRotatePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerRotationPacket;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getXRot()F"
      )}
   )
   private float hookSilentRotationPitch(float f) {
      Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return currentRotation == null ? f : currentRotation.getFloatValue2();
   }
}

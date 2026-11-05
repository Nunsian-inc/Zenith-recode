package org.zenith.utility.mixin.world;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityTagOutput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.BlockESPModule;

@Mixin({ClientChunkCache.class})
public class MixinClientChunkManager {
   @Inject(
      method = {"replaceWithPacketData(IILnet/minecraft/network/protocol/game/ClientboundLevelChunkPacketData;)Lnet/minecraft/world/level/chunk/LevelChunk;"},
      at = {@At("RETURN")}
   )
   private void zenith$onChunkLoaded(
      int x, int z, net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData levelChunkPacketData, CallbackInfoReturnable<LevelChunk> callbackInfoReturnable
   ) {
      LevelChunk returnValue = (LevelChunk)callbackInfoReturnable.getReturnValue();
      if (returnValue != null) {
         BlockESPModule.INSTANCE.method7560(returnValue, x, z);
      }
   }

   @Inject(
      method = {"drop(Lnet/minecraft/world/level/ChunkPos;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$onChunkUnloaded(ChunkPos chunkPos, CallbackInfo callbackInfo) {
      BlockESPModule.INSTANCE.method7561(chunkPos.x(), chunkPos.z());
   }
}

package org.zenith.utility.mixin.accessors;

import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientPacketListener.class})
public interface ClientPlayNetworkHandlerAccessor {
   @Accessor("level")
   ClientLevel zenith$getWorld();

   @Accessor("level")
   void zenith$setWorld(ClientLevel level);

   @Accessor("levelData")
   void zenith$setWorldProperties(ClientLevelData clientLevelData);

   @Accessor("levels")
   void zenith$setWorldKeys(Set<ResourceKey<Level>> resourceKeys);

   @Accessor("serverChunkRadius")
   void zenith$setChunkLoadDistance(int n);

   @Accessor("serverChunkRadius")
   int zenith$getChunkLoadDistance();

   @Accessor("serverSimulationDistance")
   void zenith$setSimulationDistance(int n);

   @Invoker("sendChatAcknowledgement")
   void zenith$sendAcknowledgment();
}

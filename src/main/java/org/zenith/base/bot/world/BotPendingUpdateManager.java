package org.zenith.base.bot.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class BotPendingUpdateManager implements AutoCloseable {
   private final Long2ObjectOpenHashMap<BotPendingUpdateManager.PendingUpdate> blockPosToPendingUpdate = new Long2ObjectOpenHashMap();
   private int sequence;
   private boolean pendingSequence;

   public void addPendingUpdate(BlockPos pos, BlockState blockState, Player botPlayer) {
      this.blockPosToPendingUpdate
         .compute(
            pos.asLong(),
            (var3x, pendingUpdate) -> pendingUpdate != null
               ? pendingUpdate.withSequence(this.sequence)
               : new BotPendingUpdateManager.PendingUpdate(this.sequence, blockState, botPlayer.position())
         );
   }

   public boolean hasPendingUpdate(BlockPos pos, BlockState blockState) {
      BotPendingUpdateManager.PendingUpdate pendingUpdate = (BotPendingUpdateManager.PendingUpdate)this.blockPosToPendingUpdate.get(pos.asLong());
      if (pendingUpdate == null) {
         return false;
      } else {
         pendingUpdate.setBlockState(blockState);
         return true;
      }
   }

   public void processPendingUpdates(int sequence, BotWorld botWorld) {
      ObjectIterator objectIterator = this.blockPosToPendingUpdate.long2ObjectEntrySet().iterator();

      while (objectIterator.hasNext()) {
         Entry entry = (Entry)objectIterator.next();
         BotPendingUpdateManager.PendingUpdate pendingUpdate = (BotPendingUpdateManager.PendingUpdate)entry.getValue();
         if (pendingUpdate.sequence <= sequence) {
            BlockPos pos = BlockPos.of(entry.getLongKey());
            objectIterator.remove();
            botWorld.processPendingUpdate(pos, pendingUpdate.blockState, pendingUpdate.playerPos);
         }
      }
   }

   public BotPendingUpdateManager incrementSequence() {
      this.sequence++;
      this.pendingSequence = true;
      return this;
   }

   @Override
   public void close() {
      this.pendingSequence = false;
   }

   public int getSequence() {
      return this.sequence;
   }

   public boolean hasPendingSequence() {
      return this.pendingSequence;
   }

   private static final class PendingUpdate {
      public final Vec3 playerPos;
      public int sequence;
      public BlockState blockState;

      public PendingUpdate(int sequence, BlockState blockState, Vec3 playerPos) {
         this.sequence = sequence;
         this.blockState = blockState;
         this.playerPos = playerPos;
      }

      public BotPendingUpdateManager.PendingUpdate withSequence(int sequence) {
         this.sequence = sequence;
         return this;
      }

      public void setBlockState(BlockState blockState) {
         this.blockState = blockState;
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;

public class BlockPosReference {
   public static final BlockPosReference INSTANCE = new BlockPosReference(Long.MIN_VALUE);
   private final long packedPosition;

   public BlockPosReference(long packedPosition) {
      this.packedPosition = packedPosition;
   }

   public static BlockPosReference fromBlockPos(BlockPos blockPos) {
      return new BlockPosReference(blockPos.asLong());
   }

   public boolean isPresent() {
      return this.packedPosition != Long.MIN_VALUE;
   }

   public BlockPos toBlockPos() {
      return BlockPos.of(this.packedPosition);
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("packedPos", this.packedPosition);
      return json;
   }

   public static BlockPosReference fromJson(JsonObject jsonObject) {
      return jsonObject.has("packedPos") ? new BlockPosReference(jsonObject.get("packedPos").getAsLong()) : INSTANCE;
   }
}

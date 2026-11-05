package org.zenith.utility.api;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;

public class Class1 {
   public static final Class1 INSTANCE = new Class1(Long.MIN_VALUE);
   private final long longValue;

   public Class1(long longValue) {
      this.longValue = longValue;
   }

   public static Class1 method55(BlockPos blockPos) {
      return new Class1(blockPos.asLong());
   }

   public boolean method56() {
      return this.longValue != Long.MIN_VALUE;
   }

   public BlockPos method57() {
      return BlockPos.of(this.longValue);
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("packedPos", this.longValue);
      return json;
   }

   public static Class1 method58(JsonObject jsonObject) {
      return jsonObject.has("packedPos") ? new Class1(jsonObject.get("packedPos").getAsLong()) : INSTANCE;
   }
}

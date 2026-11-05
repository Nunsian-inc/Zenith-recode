package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.vertex.VertexFormat;

// 26.2 removed com.mojang.blaze3d.vertex.Tesselator; Zenith's immediate renderer still builds
// one mesh at a time, so a single shared buffer is enough (render thread only).
public final class Tesselator {
   private static final Tesselator INSTANCE = new Tesselator();
   private final ByteBufferBuilder buffer = new ByteBufferBuilder(786432);

   public static Tesselator getInstance() {
      return INSTANCE;
   }

   public BufferBuilder begin(PrimitiveTopology mode, VertexFormat format) {
      return new BufferBuilder(this.buffer, mode, format);
   }

   public void clear() {
      this.buffer.clear();
   }
}

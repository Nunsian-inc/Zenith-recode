package org.zenith.base.figura.model.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class FiguraBufferSource {
   private static final PoseStack IDENTITY = new PoseStack();
   private final Map<RenderType, FiguraBufferSource.Recorder> layers = new LinkedHashMap<>();

   public VertexConsumer getBuffer(RenderType layer) {
      return this.layers.computeIfAbsent(layer, ignored -> new FiguraBufferSource.Recorder());
   }

   public boolean isEmpty() {
      return this.layers.isEmpty();
   }

   public void submit(SubmitNodeCollector queue) {
      if (queue != null) {
         for (Entry<RenderType, FiguraBufferSource.Recorder> entry : this.layers.entrySet()) {
            FiguraBufferSource.Recorder recorder = entry.getValue();
            if (recorder.opCount > 0) {
               boolean lineWidth = !recorder.hasLineWidth && entry.getKey().format().contains(DefaultVertexFormat.LINE_WIDTH_SEMANTIC_NAME);
               queue.submitCustomGeometry(IDENTITY, entry.getKey(), (pose, consumer) -> recorder.replay(consumer, lineWidth));
            }
         }
      }

      this.layers.clear();
   }

   private static final class Recorder implements VertexConsumer {
      private static final byte VERTEX = 0;
      private static final byte COLOR = 1;
      private static final byte TEXTURE = 2;
      private static final byte OVERLAY = 3;
      private static final byte LIGHT = 4;
      private static final byte NORMAL = 5;
      private static final byte LINE_WIDTH = 6;
      private byte[] ops = new byte[256];
      private float[] data = new float[1024];
      private int[] ints = new int[512];
      private int opCount;
      private int size;
      private int intSize;
      private boolean hasLineWidth;

      private void op(byte op, int floats) {
         if (this.opCount == this.ops.length) {
            this.ops = Arrays.copyOf(this.ops, this.ops.length * 2);
         }

         if (this.size + floats > this.data.length) {
            this.data = Arrays.copyOf(this.data, Math.max(this.data.length * 2, this.size + floats));
         }

         if (this.intSize + 2 > this.ints.length) {
            this.ints = Arrays.copyOf(this.ints, this.ints.length * 2);
         }

         this.ops[this.opCount++] = op;
      }

      private void put(float value) {
         this.data[this.size++] = value;
      }

      private void putInt(int value) {
         this.ints[this.intSize++] = value;
      }

      public VertexConsumer addVertex(float x, float y, float z) {
         this.op((byte)0, 3);
         this.put(x);
         this.put(y);
         this.put(z);
         return this;
      }

      public VertexConsumer setColor(int red, int green, int blue, int alpha) {
         return this.setColor((alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
      }

      public VertexConsumer setColor(int argb) {
         this.op((byte)1, 0);
         this.putInt(argb);
         return this;
      }

      public VertexConsumer setUv(float u, float v) {
         this.op((byte)2, 2);
         this.put(u);
         this.put(v);
         return this;
      }

      public VertexConsumer setUv1(int u, int v) {
         this.op((byte)3, 0);
         this.putInt(u);
         this.putInt(v);
         return this;
      }

      public VertexConsumer setUv2(int u, int v) {
         this.op((byte)4, 0);
         this.putInt(u);
         this.putInt(v);
         return this;
      }

      public VertexConsumer setUv3(float u, float v) {
         this.op((byte)7, 2);
         this.put(u);
         this.put(v);
         return this;
      }

      public VertexConsumer setNormal(float x, float y, float z) {
         this.op((byte)5, 3);
         this.put(x);
         this.put(y);
         this.put(z);
         return this;
      }

      public VertexConsumer setLineWidth(float width) {
         this.op((byte)6, 1);
         this.hasLineWidth = true;
         this.put(width);
         return this;
      }

      void replay(VertexConsumer out, boolean lineWidth) {
         float[] d = this.data;
         int[] n = this.ints;
         int p = 0;
         int q = 0;

         for (int i = 0; i < this.opCount; i++) {
            switch (this.ops[i]) {
               case 0:
                  out.addVertex(d[p], d[p + 1], d[p + 2]);
                  p += 3;
                  if (lineWidth) {
                     out.setLineWidth(1.0F);
                  }
                  break;
               case 1:
                  out.setColor(n[q++]);
                  break;
               case 2:
                  out.setUv(d[p], d[p + 1]);
                  p += 2;
                  break;
               case 3:
                  out.setUv1(n[q], n[q + 1]);
                  q += 2;
                  break;
               case 4:
                  out.setUv2(n[q], n[q + 1]);
                  q += 2;
                  break;
               case 5:
                  out.setNormal(d[p], d[p + 1], d[p + 2]);
                  p += 3;
                  break;
               case 6:
                  out.setLineWidth(d[p++]);
                  break;
               case 7:
                  out.setUv3(d[p], d[p + 1]);
                  p += 2;
                  break;
               default:
                  throw new IllegalStateException();
            }
         }
      }
   }
}

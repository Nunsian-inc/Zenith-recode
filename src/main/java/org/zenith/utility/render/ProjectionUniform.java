package org.zenith.utility.render;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public final class ProjectionUniform {
   private final GpuBuffer gpuBuffer;
   private final GpuBufferSlice gpuBufferSlice;
   private final Matrix4f matrix4f = new Matrix4f().scale(0.0F);

   public ProjectionUniform(String str) {
      this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Projection matrix UBO " + str, 136, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
      this.gpuBufferSlice = this.gpuBuffer.slice(0L, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
   }

   public GpuBufferSlice method551(Matrix4f matrix4f) {
      if (!this.matrix4f.equals(matrix4f)) {
         this.matrix4f.set(matrix4f);
         MemoryStack stackPush = MemoryStack.stackPush();

         try {
            ByteBuffer data = Std140Builder.onStack(stackPush, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f(matrix4f).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.gpuBuffer.slice(), data);
         } catch (Throwable e) {
            if (stackPush != null) {
               try {
                  stackPush.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }
            }

            throw e;
         }

         if (stackPush != null) {
            stackPush.close();
         }
      }

      return this.gpuBufferSlice;
   }
}

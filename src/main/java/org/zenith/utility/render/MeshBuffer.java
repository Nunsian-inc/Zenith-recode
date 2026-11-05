package org.zenith.utility.render;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.MeshData.DrawState;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.zenith.RenderState;

public final class MeshBuffer implements AutoCloseable {
   private static final Vector3f vector3f = new Vector3f();
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Vector4f vector4f = new Vector4f();
   private final String text;
   @Nullable
   private GpuBuffer gpuBuffer;
   private int intValue;
   private PrimitiveTopology drawMode = PrimitiveTopology.QUADS;

   public MeshBuffer(String text) {
      this.text = text;
   }

   public boolean isEmpty() {
      return this.gpuBuffer == null || this.intValue == 0;
   }

   public void method6583(@Nullable MeshData buffer) {
      if (buffer != null) {
         MeshData meshData = buffer;

         try {
            DrawState drawState = buffer.drawState();
            ByteBuffer vertexBuffer = buffer.vertexBuffer();
            if (this.gpuBuffer != null && !this.gpuBuffer.isClosed() && this.gpuBuffer.size() >= vertexBuffer.remaining()) {
               RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.gpuBuffer.slice(0L, vertexBuffer.remaining()), vertexBuffer);
            } else {
               this.close();
               this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> this.text, 40, vertexBuffer);
            }

            this.intValue = drawState.indexCount();
            this.drawMode = drawState.primitiveTopology();
         } catch (Throwable e) {
            if (buffer != null) {
               try {
                  meshData.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }
            }

            throw e;
         }

         if (buffer != null) {
            buffer.close();
         }
      }
   }

   public void method6584(RenderPipeline pipeline, Matrix4f modelView) {
      this.method6587(pipeline, modelView, null, null);
   }

   public void method6585(ShaderDefinition shaderDefinition, VertexFormat vertexFormat, Matrix4f modelView, @Nullable RenderTarget fbo) {
      this.method6587(shaderDefinition.method5561(vertexFormat, this.drawMode, RenderState.method1904()), modelView, fbo, shaderDefinition.method5562());
   }

   public void method6586(RenderPipeline pipeline, Matrix4f modelView, @Nullable RenderTarget fbo) {
      this.method6587(pipeline, modelView, fbo, null);
   }

   public void method6587(RenderPipeline pipeline, Matrix4f modelView, @Nullable RenderTarget fbo, @Nullable GpuBufferSlice gpuBufferSlice) {
      method6589(List.of(this), pipeline, modelView, fbo, gpuBufferSlice);
   }

   public static void method6588(List<MeshBuffer> layerMeshes, List<Matrix4f> subList, RenderPipeline pipeline, @Nullable RenderTarget fbo) {
      int size = layerMeshes.size();
      int indexCount = 0;
      PrimitiveTopology drawMode = null;

      for (int i = 0; i < size; i++) {
         MeshBuffer meshBuffer = (MeshBuffer)layerMeshes.get(i);
         if (!meshBuffer.isEmpty()) {
            if (drawMode != null && meshBuffer.drawMode != drawMode) {
               for (int j = 0; j < size; j++) {
                  ((MeshBuffer)layerMeshes.get(j)).method6586(pipeline, (Matrix4f)subList.get(j), fbo);
               }

               return;
            }

            drawMode = meshBuffer.drawMode;
            indexCount = Math.max(indexCount, meshBuffer.intValue);
         }
      }

      if (drawMode != null) {
         AutoStorageIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(drawMode);
         GpuBuffer buffer = sequentialBuffer.getBuffer(indexCount);
         vector4f.set(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha());
         GpuBufferSlice[] gpuBufferSlices = new GpuBufferSlice[size];

         for (int i = 0; i < size; i++) {
            if (!((MeshBuffer)layerMeshes.get(i)).isEmpty()) {
               gpuBufferSlices[i] = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f((Matrix4fc)subList.get(i)), vector4f, vector3f, matrix4f);
            }
         }

         RenderTarget renderTarget = fbo != null ? fbo : BlitUtils.method6452();
         GpuTextureView colorTextureView = renderTarget.getColorTextureView();
         GpuTextureView depthTextureView = renderTarget.hasDepth() ? renderTarget.getDepthTextureView() : null;

         try (RenderPass renderPass = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith mesh", colorTextureView, java.util.Optional.empty(), depthTextureView, OptionalDouble.empty())) {
            renderPass.setPipeline(com.mojang.blaze3d.systems.RenderSystem.getCompiledPipeline(pipeline));
            RenderSystem.bindDefaultUniforms(renderPass);

            for (int i = 0; i < 8; i++) {
               GpuTextureView textureView = RenderState.method1894(i);
               if (textureView != null) {
                  String name = LightUtil.texts3[i];
                  if (org.zenith.utility.render.ImmediateRenderer.hasSampler(pipeline, name)) {
                     renderPass.setUniform(name, textureView, RenderState.method1896(i));
                  }
               }
            }

            renderPass.setIndexBuffer(buffer, sequentialBuffer.type());

            for (int i = 0; i < size; i++) {
               MeshBuffer meshBuffer = (MeshBuffer)layerMeshes.get(i);
               if (!meshBuffer.isEmpty()) {
                  renderPass.setUniform("DynamicTransforms", gpuBufferSlices[i]);
                  renderPass.setVertexBuffer(0, meshBuffer.gpuBuffer.slice());
                  renderPass.drawIndexed(meshBuffer.intValue, 1, 0, 0, 0);
               }
            }
         }
      }
   }

   public static void method6589(List<MeshBuffer> meshBuffers, RenderPipeline pipeline, Matrix4f modelView, @Nullable RenderTarget fbo, @Nullable GpuBufferSlice gpuBufferSlice) {
      int indexCount = 0;
      PrimitiveTopology drawMode = null;

      for (MeshBuffer meshBuffer : meshBuffers) {
         if (!meshBuffer.isEmpty()) {
            indexCount = Math.max(indexCount, meshBuffer.intValue);
            drawMode = meshBuffer.drawMode;
         }
      }

      if (drawMode != null) {
         AutoStorageIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(drawMode);
         GpuBuffer buffer = sequentialBuffer.getBuffer(indexCount);
         GpuBufferSlice writeTransform = RenderSystem.getDynamicUniforms()
            .writeTransform(
               modelView,
               vector4f.set(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha()),
               vector3f,
               matrix4f
            );
         RenderTarget renderTarget = fbo != null ? fbo : BlitUtils.method6452();
         GpuTextureView colorTextureView = renderTarget.getColorTextureView();
         GpuTextureView depthTextureView = renderTarget.hasDepth() ? renderTarget.getDepthTextureView() : null;

         try (RenderPass renderPass = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith mesh", colorTextureView, java.util.Optional.empty(), depthTextureView, OptionalDouble.empty())) {
            renderPass.setPipeline(com.mojang.blaze3d.systems.RenderSystem.getCompiledPipeline(pipeline));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", writeTransform);
            if (gpuBufferSlice != null) {
               renderPass.setUniform("ZenithData", gpuBufferSlice);
            }

            for (int i = 0; i < 8; i++) {
               GpuTextureView textureView = RenderState.method1894(i);
               if (textureView != null) {
                  String name = LightUtil.texts3[i];
                  if (org.zenith.utility.render.ImmediateRenderer.hasSampler(pipeline, name)) {
                     renderPass.setUniform(name, textureView, RenderState.method1896(i));
                  }
               }
            }

            renderPass.setIndexBuffer(buffer, sequentialBuffer.type());

            for (MeshBuffer meshBuffer : meshBuffers) {
               if (!meshBuffer.isEmpty() && meshBuffer.drawMode == drawMode) {
                  renderPass.setVertexBuffer(0, meshBuffer.gpuBuffer.slice());
                  renderPass.drawIndexed(meshBuffer.intValue, 1, 0, 0, 0);
               }
            }
         }
      }
   }

   @Override
   public void close() {
      if (this.gpuBuffer != null) {
         this.gpuBuffer.close();
         this.gpuBuffer = null;
      }

      this.intValue = 0;
   }
}

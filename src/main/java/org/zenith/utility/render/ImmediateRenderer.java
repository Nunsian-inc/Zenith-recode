package org.zenith.utility.render;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.MeshData.DrawState;
import com.mojang.renderpearl.api.pipeline.IndexType;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.zenith.RenderState;
import org.zenith.utility.misc.ScissorStack;

public final class ImmediateRenderer {
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector4f vector4f = new Vector4f();
   @Nullable
   private static ShaderDefinition shaderDefinition;
   @Nullable
   private static RenderTarget framebuffer;
   private static int intValue;
   @Nullable
   private static ImmediateRenderer.Ortho projectionMatrix2;
   private static ImmediateRenderer.Type type = ImmediateRenderer.Type.DEFAULT;
   private static float floatValue;
   private static float floatValue2;
   @Nullable
   private static ProjectionUniform projectionUniform;
   @Nullable
   private static ImmediateRenderer.Ortho projectionMatrix22;
   public static final Matrix4f matrix4f2 = new Matrix4f().setTranslation(0.0F, 0.0F, -11000.0F);

   private ImmediateRenderer() {
   }

   public static void method8003() {
      intValue++;
      RenderState.method1881();
   }

   public static void method8004() {
      if (intValue > 0) {
         intValue--;
      }
   }

   public static boolean method8005() {
      return intValue > 0;
   }

   public static void method8006(Runnable runnable) {
      if (intValue <= 0) {
         runnable.run();
      } else {
         RenderSystem.backupProjectionMatrix();
         RenderSystem.setProjectionMatrix(method8019(), ProjectionType.ORTHOGRAPHIC);
         RenderSystem.getModelViewStack().pushMatrix().set(matrix4f2);

         try {
            runnable.run();
         } finally {
            RenderSystem.getModelViewStack().popMatrix();
            RenderSystem.restoreProjectionMatrix();
         }
      }
   }

   public static int method8007() {
      int n = intValue;
      intValue = 0;
      return n;
   }

   public static void method8008(int n) {
      intValue = n;
   }

   public static void method8009() {
      intValue = 0;
      type = ImmediateRenderer.Type.DEFAULT;
      ScissorStack.clear();
   }

   public static void method8010(float width, float height) {
      type = ImmediateRenderer.Type.PIXEL;
      floatValue = Math.max(1.0F, width);
      floatValue2 = Math.max(1.0F, height);
   }

   public static void method8011() {
      type = ImmediateRenderer.Type.NDC;
   }

   public static void method8012(@Nullable ShaderDefinition shaderDefinition2) {
      shaderDefinition = shaderDefinition2;
   }

   public static void method8013(@Nullable RenderTarget simpleFramebuffer2) {
      framebuffer = simpleFramebuffer2;
   }

   @Nullable
   public static RenderTarget method8014() {
      RenderTarget renderTarget = framebuffer;
      framebuffer = null;
      return renderTarget;
   }

   @Nullable
   public static ShaderDefinition getShaderDefinition() {
      return shaderDefinition;
   }

   public static void method8015(MeshData orThrow) {
      if (orThrow != null) {
         if (!RenderSystem.isOnRenderThread()) {
            orThrow.close();
         } else {
            ShaderDefinition shaderDefinition2 = shaderDefinition;
            shaderDefinition = null;
            ImmediateRenderer.Type type2 = type;
            type = ImmediateRenderer.Type.DEFAULT;
            RenderTarget renderTarget = framebuffer;
            framebuffer = null;
            String text = RenderState.getText();
            RenderState.method1899();
            MeshData meshData = orThrow;

            label295: {
               label296: {
                  try {
                     DrawState drawState = orThrow.drawState();
                     if (drawState.indexCount() == 0) {
                        break label295;
                     }

                     boolean flag = type2 != ImmediateRenderer.Type.DEFAULT || intValue > 0;
                     RenderTarget mainRenderTarget = renderTarget != null ? renderTarget : Minecraft.getInstance().gameRenderer.mainRenderTarget();
                     RenderState.Data2 data2 = RenderState.method1904();
                     if (flag) {
                        boolean flag2 = type2 == ImmediateRenderer.Type.DEFAULT && RenderState.isFlag4() && mainRenderTarget.hasDepth();
                        data2 = flag2 ? data2.method1910() : data2.method1909();
                     }

                     RenderPipeline pipeline = shaderDefinition2 != null
                        ? shaderDefinition2.method5561(drawState.format(), drawState.primitiveTopology(), data2)
                        : LightUtil.method6706(text != null ? text : LightUtil.method6719(drawState.format(), drawState.primitiveTopology()), drawState.format(), drawState.primitiveTopology(), data2);
                     if (pipeline == null) {
                        break label296;
                     }

                     // ponytail: one GPU buffer per immediate draw (closed after the pass, Vulkan defers destruction); pool it if draw counts get high
                     GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Zenith immediate vertices", GpuBuffer.USAGE_VERTEX, orThrow.vertexBuffer());
                     GpuBuffer gpuBuffer = null;
                     GpuBuffer indexBuffer;
                     IndexType indexType;
                     if (orThrow.indexBuffer() == null) {
                        AutoStorageIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(drawState.primitiveTopology());
                        indexBuffer = sequentialBuffer.getBuffer(drawState.indexCount());
                        indexType = sequentialBuffer.type();
                     } else {
                        gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Zenith immediate indices", GpuBuffer.USAGE_INDEX, orThrow.indexBuffer());
                        indexBuffer = gpuBuffer;
                        indexType = drawState.indexType();
                     }
                     Matrix4f modelView = switch (type2) {
                        case DEFAULT -> intValue > 0 ? matrix4f2 : RenderSystem.getModelViewMatrixCopy();
                        case PIXEL -> matrix4f2;
                        case NDC -> matrix4f;
                     };
                     GpuBufferSlice writeTransform = RenderSystem.getDynamicUniforms()
                        .writeTransform(
                           modelView,
                           vector4f.set(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha()),
                           vector3f,
                           matrix4f
                        );
                     GpuTextureView colorTextureView = mainRenderTarget.getColorTextureView();
                     GpuTextureView depthTextureView = mainRenderTarget.hasDepth() && pipeline.getDepthStencilState() != null ? mainRenderTarget.getDepthTextureView() : null;
                     if (flag) {
                        RenderSystem.backupProjectionMatrix();
                        RenderSystem.setProjectionMatrix(method8016(type2), ProjectionType.ORTHOGRAPHIC);
                     }

                     GpuBufferSlice gpuBufferSlice = shaderDefinition2 != null ? shaderDefinition2.method5562() : null;
                     CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

                     try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Zenith immediate", colorTextureView, Optional.empty(), depthTextureView, OptionalDouble.empty())) {
                        renderPass.setPipeline(RenderSystem.getCompiledPipeline(pipeline));
                        ScissorState scissorStateForRenderTypeDraws = RenderSystem.getScissorStateForRenderTypeDraws();
                        if (scissorStateForRenderTypeDraws.enabled()) {
                           renderPass.enableScissor(scissorStateForRenderTypeDraws.x(), scissorStateForRenderTypeDraws.y(), scissorStateForRenderTypeDraws.width(), scissorStateForRenderTypeDraws.height());
                        }

                        RenderSystem.bindDefaultUniforms(renderPass);
                        renderPass.setUniform("DynamicTransforms", writeTransform);
                        if (shaderDefinition2 != null) {
                           shaderDefinition2.method5564(renderPass, gpuBufferSlice);
                        }

                        method8020(renderPass, pipeline);
                        renderPass.setVertexBuffer(0, buffer.slice());
                        renderPass.setIndexBuffer(indexBuffer, indexType);
                        renderPass.drawIndexed(drawState.indexCount(), 1, 0, 0, 0);
                     } finally {
                        if (flag) {
                           RenderSystem.restoreProjectionMatrix();
                        }
                        buffer.close();
                        if (gpuBuffer != null) {
                           gpuBuffer.close();
                        }
                     }
                  } catch (Throwable e) {
                     if (orThrow != null) {
                        try {
                           meshData.close();
                        } catch (Throwable ex) {
                           e.addSuppressed(ex);
                        }
                     }

                     throw e;
                  }

                  if (orThrow != null) {
                     orThrow.close();
                  }

                  return;
               }

               if (orThrow != null) {
                  orThrow.close();
               }

               return;
            }

            if (orThrow != null) {
               orThrow.close();
            }
         }
      }
   }

   private static GpuBufferSlice method8016(ImmediateRenderer.Type type) {
      return switch (type) {
         case DEFAULT -> method8019();
         case PIXEL -> method8018(floatValue, floatValue2);
         case NDC -> method8017();
      };
   }

   private static GpuBufferSlice method8017() {
      if (projectionUniform == null) {
         projectionUniform = new ProjectionUniform("zenith ndc");
      }

      return projectionUniform.method551(matrix4f);
   }

   private static GpuBufferSlice method8018(float floatValue, float height) {
      if (projectionMatrix22 == null) {
         projectionMatrix22 = new ImmediateRenderer.Ortho("zenith pixel", 1000.0F, 21000.0F, true);
      }

      return projectionMatrix22.getBuffer(floatValue, height);
   }

   public static GpuBufferSlice method8019() {
      if (projectionMatrix2 == null) {
         projectionMatrix2 = new ImmediateRenderer.Ortho("zenith gui", 1000.0F, 21000.0F, true);
      }

      Window window = Minecraft.getInstance().getWindow();
      return projectionMatrix2.getBuffer((float)window.getWidth() / window.getGuiScale(), (float)window.getHeight() / window.getGuiScale());
   }

   private static void method8020(RenderPass renderPass, RenderPipeline pipeline) {
      for (int i = 0; i < 8; i++) {
         GpuTextureView textureView = RenderState.method1894(i);
         if (textureView != null) {
            String name = LightUtil.texts3[i];
            if (hasSampler(pipeline, name)) {
               renderPass.setUniform(name, textureView, RenderState.method1896(i));
            }
         }
      }
   }

   // 26.2 replaced RenderPipeline#getSamplers with bind group layouts
   public static boolean hasSampler(RenderPipeline pipeline, String name) {
      for (com.mojang.renderpearl.api.pipeline.BindGroupLayout bindGroupLayout : pipeline.getBindGroupLayouts()) {
         for (com.mojang.renderpearl.api.pipeline.BindGroupLayout.UniformDescription uniform : bindGroupLayout.uniforms()) {
            if (uniform.name().equals(name)) {
               return true;
            }
         }
      }

      return false;
   }

   // stand-in for the removed CachedOrthoProjectionMatrixBuffer(name, near, far, invertY)
   private static final class Ortho {
      private final ProjectionMatrixBuffer buffer;
      private final Projection projection = new Projection();
      private final float zNear;
      private final float zFar;
      private final boolean invertY;

      private Ortho(String name, float zNear, float zFar, boolean invertY) {
         this.buffer = new ProjectionMatrixBuffer(name);
         this.zNear = zNear;
         this.zFar = zFar;
         this.invertY = invertY;
      }

      private GpuBufferSlice getBuffer(float floatValue, float height) {
         this.projection.setupOrtho(this.zNear, this.zFar, floatValue, height, this.invertY);
         return this.buffer.getBuffer(this.projection);
      }
   }

   private static enum Type {
      DEFAULT,
      PIXEL,
      NDC;
   }
}

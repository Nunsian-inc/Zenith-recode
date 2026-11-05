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

   public static void method8006(Runnable var0) {
      if (intValue <= 0) {
         var0.run();
      } else {
         RenderSystem.backupProjectionMatrix();
         RenderSystem.setProjectionMatrix(method8019(), ProjectionType.ORTHOGRAPHIC);
         RenderSystem.getModelViewStack().pushMatrix().set(matrix4f2);

         try {
            var0.run();
         } finally {
            RenderSystem.getModelViewStack().popMatrix();
            RenderSystem.restoreProjectionMatrix();
         }
      }
   }

   public static int method8007() {
      int var0 = intValue;
      intValue = 0;
      return var0;
   }

   public static void method8008(int var0) {
      intValue = var0;
   }

   public static void method8009() {
      intValue = 0;
      type = ImmediateRenderer.Type.DEFAULT;
      ScissorStack.clear();
   }

   public static void method8010(float var0, float var1) {
      type = ImmediateRenderer.Type.PIXEL;
      floatValue = Math.max(1.0F, var0);
      floatValue2 = Math.max(1.0F, var1);
   }

   public static void method8011() {
      type = ImmediateRenderer.Type.NDC;
   }

   public static void method8012(@Nullable ShaderDefinition var0) {
      shaderDefinition = var0;
   }

   public static void method8013(@Nullable RenderTarget var0) {
      framebuffer = var0;
   }

   @Nullable
   public static RenderTarget method8014() {
      RenderTarget var0 = framebuffer;
      framebuffer = null;
      return var0;
   }

   @Nullable
   public static ShaderDefinition getShaderDefinition() {
      return shaderDefinition;
   }

   public static void method8015(MeshData var0) {
      if (var0 != null) {
         if (!RenderSystem.isOnRenderThread()) {
            var0.close();
         } else {
            ShaderDefinition var1 = shaderDefinition;
            shaderDefinition = null;
            ImmediateRenderer.Type var2 = type;
            type = ImmediateRenderer.Type.DEFAULT;
            RenderTarget var3 = framebuffer;
            framebuffer = null;
            String var4 = RenderState.getText();
            RenderState.method1899();
            MeshData var5 = var0;

            label295: {
               label296: {
                  try {
                     DrawState var6 = var0.drawState();
                     if (var6.indexCount() == 0) {
                        break label295;
                     }

                     boolean var7 = var2 != ImmediateRenderer.Type.DEFAULT || intValue > 0;
                     RenderTarget var8 = var3 != null ? var3 : Minecraft.getInstance().gameRenderer.mainRenderTarget();
                     RenderState.Data2 var9 = RenderState.method1904();
                     if (var7) {
                        boolean var10 = var2 == ImmediateRenderer.Type.DEFAULT && RenderState.isFlag4() && var8.hasDepth();
                        var9 = var10 ? var9.method1910() : var9.method1909();
                     }

                     RenderPipeline var34 = var1 != null
                        ? var1.method5561(var6.format(), var6.primitiveTopology(), var9)
                        : LightUtil.method6706(var4 != null ? var4 : LightUtil.method6719(var6.format(), var6.primitiveTopology()), var6.format(), var6.primitiveTopology(), var9);
                     if (var34 == null) {
                        break label296;
                     }

                     // ponytail: one GPU buffer per immediate draw (closed after the pass, Vulkan defers destruction); pool it if draw counts get high
                     GpuBuffer var11 = RenderSystem.getDevice().createBuffer(() -> "Zenith immediate vertices", GpuBuffer.USAGE_VERTEX, var0.vertexBuffer());
                     GpuBuffer var36 = null;
                     GpuBuffer var12;
                     IndexType var13;
                     if (var0.indexBuffer() == null) {
                        AutoStorageIndexBuffer var14 = RenderSystem.getSequentialBuffer(var6.primitiveTopology());
                        var12 = var14.getBuffer(var6.indexCount());
                        var13 = var14.type();
                     } else {
                        var36 = RenderSystem.getDevice().createBuffer(() -> "Zenith immediate indices", GpuBuffer.USAGE_INDEX, var0.indexBuffer());
                        var12 = var36;
                        var13 = var6.indexType();
                     }
                     Matrix4f var35 = switch (var2) {
                        case DEFAULT -> intValue > 0 ? matrix4f2 : RenderSystem.getModelViewMatrixCopy();
                        case PIXEL -> matrix4f2;
                        case NDC -> matrix4f;
                     };
                     GpuBufferSlice var15 = RenderSystem.getDynamicUniforms()
                        .writeTransform(
                           var35,
                           vector4f.set(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha()),
                           vector3f,
                           matrix4f
                        );
                     GpuTextureView var16 = var8.getColorTextureView();
                     GpuTextureView var17 = var8.hasDepth() && var34.getDepthStencilState() != null ? var8.getDepthTextureView() : null;
                     if (var7) {
                        RenderSystem.backupProjectionMatrix();
                        RenderSystem.setProjectionMatrix(method8016(var2), ProjectionType.ORTHOGRAPHIC);
                     }

                     GpuBufferSlice var18 = var1 != null ? var1.method5562() : null;
                     CommandEncoder var19 = RenderSystem.getDevice().createCommandEncoder();

                     try (RenderPass var20 = var19.createRenderPass(() -> "Zenith immediate", var16, Optional.empty(), var17, OptionalDouble.empty())) {
                        var20.setPipeline(RenderSystem.getCompiledPipeline(var34));
                        ScissorState var21 = RenderSystem.getScissorStateForRenderTypeDraws();
                        if (var21.enabled()) {
                           var20.enableScissor(var21.x(), var21.y(), var21.width(), var21.height());
                        }

                        RenderSystem.bindDefaultUniforms(var20);
                        var20.setUniform("DynamicTransforms", var15);
                        if (var1 != null) {
                           var1.method5564(var20, var18);
                        }

                        method8020(var20, var34);
                        var20.setVertexBuffer(0, var11.slice());
                        var20.setIndexBuffer(var12, var13);
                        var20.drawIndexed(var6.indexCount(), 1, 0, 0, 0);
                     } finally {
                        if (var7) {
                           RenderSystem.restoreProjectionMatrix();
                        }
                        var11.close();
                        if (var36 != null) {
                           var36.close();
                        }
                     }
                  } catch (Throwable var33) {
                     if (var0 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var29) {
                           var33.addSuppressed(var29);
                        }
                     }

                     throw var33;
                  }

                  if (var0 != null) {
                     var0.close();
                  }

                  return;
               }

               if (var0 != null) {
                  var0.close();
               }

               return;
            }

            if (var0 != null) {
               var0.close();
            }
         }
      }
   }

   private static GpuBufferSlice method8016(ImmediateRenderer.Type var0) {
      return switch (var0) {
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

   private static GpuBufferSlice method8018(float var0, float var1) {
      if (projectionMatrix22 == null) {
         projectionMatrix22 = new ImmediateRenderer.Ortho("zenith pixel", 1000.0F, 21000.0F, true);
      }

      return projectionMatrix22.getBuffer(var0, var1);
   }

   public static GpuBufferSlice method8019() {
      if (projectionMatrix2 == null) {
         projectionMatrix2 = new ImmediateRenderer.Ortho("zenith gui", 1000.0F, 21000.0F, true);
      }

      Window var0 = Minecraft.getInstance().getWindow();
      return projectionMatrix2.getBuffer((float)var0.getWidth() / var0.getGuiScale(), (float)var0.getHeight() / var0.getGuiScale());
   }

   private static void method8020(RenderPass var0, RenderPipeline var1) {
      for (int var2 = 0; var2 < 8; var2++) {
         GpuTextureView var3 = RenderState.method1894(var2);
         if (var3 != null) {
            String var4 = LightUtil.texts3[var2];
            if (hasSampler(var1, var4)) {
               var0.setUniform(var4, var3, RenderState.method1896(var2));
            }
         }
      }
   }

   // 26.2 replaced RenderPipeline#getSamplers with bind group layouts
   public static boolean hasSampler(RenderPipeline var0, String var1) {
      for (com.mojang.renderpearl.api.pipeline.BindGroupLayout var2 : var0.getBindGroupLayouts()) {
         for (com.mojang.renderpearl.api.pipeline.BindGroupLayout.UniformDescription var3 : var2.uniforms()) {
            if (var3.name().equals(var1)) {
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

      private Ortho(String var1, float var2, float var3, boolean var4) {
         this.buffer = new ProjectionMatrixBuffer(var1);
         this.zNear = var2;
         this.zFar = var3;
         this.invertY = var4;
      }

      private GpuBufferSlice getBuffer(float var1, float var2) {
         this.projection.setupOrtho(this.zNear, this.zFar, var1, var2, this.invertY);
         return this.buffer.getBuffer(this.projection);
      }
   }

   private static enum Type {
      DEFAULT,
      PIXEL,
      NDC;
   }
}

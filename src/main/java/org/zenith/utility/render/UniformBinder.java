package org.zenith.utility.render;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public final class UniformBinder {
   private static final Map<RenderPipeline, UniformBinder.Data> items = new IdentityHashMap<>();
   private static final Map<RenderPipeline, GpuBufferSlice> items2 = new IdentityHashMap<>();

   private UniformBinder() {
   }

   public static void method5478(RenderPipeline pipeline, ShaderDefinition kawaseShaderKey2, Runnable runnable) {
      method5479(pipeline, kawaseShaderKey2, runnable, null);
   }

   public static void method5479(@Nullable RenderPipeline pipeline, ShaderDefinition kawaseShaderKey2, Runnable runnable, @Nullable Supplier<GpuTextureView> supplier) {
      method5480(pipeline, kawaseShaderKey2, runnable, supplier, null);
   }

   public static void method5480(
      @Nullable RenderPipeline pipeline, ShaderDefinition kawaseShaderKey2, Runnable runnable, @Nullable Supplier<GpuTextureView> supplier, @Nullable BooleanSupplier booleanSupplier
   ) {
      if (pipeline != null) {
         items.put(pipeline, new UniformBinder.Data(kawaseShaderKey2, runnable, supplier, booleanSupplier));
      }
   }

   public static void method5481() {
      if (!items.isEmpty()) {
         for (Entry entry : items.entrySet()) {
            UniformBinder.Data data = (UniformBinder.Data)entry.getValue();
            if (data.getBooleanSupplier() != null && !data.getBooleanSupplier().getAsBoolean()) {
               items2.remove(entry.getKey());
            } else {
               data.getRunnable().run();
               GpuBufferSlice gpuBufferSlice = data.getShaderDefinition().method5562();
               if (gpuBufferSlice != null) {
                  items2.put((RenderPipeline)entry.getKey(), gpuBufferSlice);
               }
            }
         }
      }
   }

   public static void method5482(RenderPass renderPass, RenderPipeline pipeline) {
      GpuBufferSlice gpuBufferSlice = items2.get(pipeline);
      if (gpuBufferSlice != null) {
         renderPass.setUniform("ZenithData", gpuBufferSlice);
      }

      UniformBinder.Data data = items.get(pipeline);
      if (data != null && data.getSupplier() != null && org.zenith.utility.render.ImmediateRenderer.hasSampler(pipeline, "Sampler0")) {
         GpuTextureView textureView = data.getSupplier().get();
         if (textureView != null) {
            renderPass.setUniform("Sampler0", textureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
         }
      }
   }

   private static final class Data {
      private final ShaderDefinition shaderDefinition;
      private final Runnable runnable;
      @Nullable
      private final Supplier<GpuTextureView> supplier;
      @Nullable
      private final BooleanSupplier booleanSupplier;

      public ShaderDefinition shaderDefinition() {
         return this.shaderDefinition;
      }

      public Runnable runnable() {
         return this.runnable;
      }

      public Supplier<GpuTextureView> supplier() {
         return this.supplier;
      }

      public BooleanSupplier booleanSupplier() {
         return this.booleanSupplier;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.shaderDefinition, other.shaderDefinition) && java.util.Objects.equals(this.runnable, other.runnable) && java.util.Objects.equals(this.supplier, other.supplier) && java.util.Objects.equals(this.booleanSupplier, other.booleanSupplier);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shaderDefinition, runnable, supplier, booleanSupplier);
      }

      @Override
      public String toString() {
         return "Data[" + "shaderDefinition=" + this.shaderDefinition + ", " + "runnable=" + this.runnable + ", " + "supplier=" + this.supplier + ", " + "booleanSupplier=" + this.booleanSupplier + "]";
      }

      private Data(ShaderDefinition shaderDefinition, Runnable runnable, @Nullable Supplier<GpuTextureView> supplier, @Nullable BooleanSupplier booleanSupplier) {
         this.shaderDefinition = shaderDefinition;
         this.runnable = runnable;
         this.supplier = supplier;
         this.booleanSupplier = booleanSupplier;
      }

      public ShaderDefinition getShaderDefinition() {
         return this.shaderDefinition;
      }

      public Runnable getRunnable() {
         return this.runnable;
      }

      @Nullable
      public Supplier<GpuTextureView> getSupplier() {
         return this.supplier;
      }

      @Nullable
      public BooleanSupplier getBooleanSupplier() {
         return this.booleanSupplier;
      }
   }
}

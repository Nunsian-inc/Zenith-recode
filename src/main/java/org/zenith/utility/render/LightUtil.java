package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.RenderPipeline.Builder;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.pipeline.UniformType;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import java.util.Optional;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.RenderState;

public final class LightUtil {
   public static final String text = "core/position_color";
   public static final String text2 = "core/position_tex_color";
   public static final String text3 = "core/rendertype_lines";
   private static final Map<LightUtil.Data, RenderPipeline> items = new ConcurrentHashMap<>();
   private static final String[] texts = new String[]{"Sampler0"};
   private static final String[] texts2 = new String[0];
   private static final String text4 = "Sampler0";
   private static final String text5 = "";
   public static final String[] texts3 = new String[]{"Sampler0", "Sampler1", "Sampler2", "Sampler3", "Sampler4", "Sampler5", "Sampler6", "Sampler7"};

   private LightUtil() {
   }

   public static RenderPipeline method6706(String text, VertexFormat vertexFormat, PrimitiveTopology primitiveTopology, RenderState.Data2 data2) {
      boolean flag = method6718(vertexFormat);
      return method6708(text, vertexFormat, primitiveTopology, data2, flag ? texts : texts2, flag ? "Sampler0" : "");
   }

   public static RenderPipeline method6707(String text, VertexFormat vertexFormat, PrimitiveTopology primitiveTopology, RenderState.Data2 data2, String[] texts) {
      return method6708(text, vertexFormat, primitiveTopology, data2, texts, String.join(",", texts));
   }

   private static RenderPipeline method6708(String text, VertexFormat vertexFormat, PrimitiveTopology primitiveTopology, RenderState.Data2 data2, String[] texts, String str) {
      LightUtil.Data data = new LightUtil.Data(text, vertexFormat, primitiveTopology, data2, str, false);
      RenderPipeline renderPipeline = items.get(data);
      return renderPipeline != null ? renderPipeline : items.computeIfAbsent(data, var1x -> method6711(var1x, texts, null, false));
   }

   @Nullable
   public static RenderPipeline method6709(Identifier identifier, VertexFormat vertexFormat, PrimitiveTopology primitiveTopology, RenderState.Data2 data2, String[] elements, boolean flag) {
      return method6710(identifier, identifier.toString(), String.join(",", elements), vertexFormat, primitiveTopology, data2, elements, flag);
   }

   @Nullable
   public static RenderPipeline method6710(
      Identifier identifier, String string, String str, VertexFormat vertexFormat, PrimitiveTopology primitiveTopology, RenderState.Data2 data2, String[] elements, boolean flag
   ) {
      LightUtil.Data data = new LightUtil.Data(string, vertexFormat, primitiveTopology, data2, str, flag);
      RenderPipeline renderPipeline = items.get(data);
      if (renderPipeline != null) {
         return renderPipeline;
      } else if (!method6712(identifier)) {
         return null;
      } else {
         RenderPipeline renderPipeline2 = method6711(data, elements, identifier, flag);
         items.put(data, renderPipeline2);
         return renderPipeline2;
      }
   }

   private static RenderPipeline method6711(LightUtil.Data data, String[] texts, Identifier identifier, boolean flag) {
      Identifier defaultNamespace;
      Identifier fragmentShader;
      if (identifier != null) {
         String path = identifier.getPath();
         int lastIndexOf = path.lastIndexOf(47);
         path = lastIndexOf < 0 ? path : path.substring(0, lastIndexOf);
         fragmentShader = Identifier.fromNamespaceAndPath(identifier.getNamespace(), path + "/fragment");
         Identifier id = Identifier.fromNamespaceAndPath(identifier.getNamespace(), path + "/vertex");
         boolean contains = data.getVertexFormat().contains("UV0");
         defaultNamespace = method6715(id, ".vsh") ? id : Identifier.fromNamespaceAndPath(identifier.getNamespace(), "core/" + (contains ? "position_texture_color" : "position_color"));
      } else {
         defaultNamespace = Identifier.withDefaultNamespace(data.getText());
         fragmentShader = defaultNamespace;
      }

      // 26.2+: samplers/uniforms are declared through a BindGroupLayout, depth/blend through state records
      BindGroupLayout.Builder uniform = BindGroupLayout.builder()
         .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
         .withUniform("Projection", UniformType.UNIFORM_BUFFER);

      for (String text : texts) {
         uniform.withUniform(text, UniformType.COMBINED_IMAGE_SAMPLER);
      }

      if (method6713(defaultNamespace, fragmentShader, "minecraft:globals.glsl")) {
         uniform.withUniform("Globals", UniformType.UNIFORM_BUFFER);
      }

      if (method6713(defaultNamespace, fragmentShader, "minecraft:fog.glsl")) {
         uniform.withUniform("Fog", UniformType.UNIFORM_BUFFER);
      }

      if (method6713(defaultNamespace, fragmentShader, "minecraft:light.glsl")) {
         uniform.withUniform("Lighting", UniformType.UNIFORM_BUFFER);
      }

      if (flag) {
         uniform.withUniform("ZenithData", UniformType.UNIFORM_BUFFER);
      }

      CompareOp depthTestFunction = data.getData2().getDepthTestFunction();
      boolean depthWrite = data.getData2().isFlag3();
      BlendFunction blendFunction = data.getData2().method1908();
      Builder colorTargetState = RenderPipeline.builder()
         .withLocation(Identifier.fromNamespaceAndPath("zenith", method6716(data)))
         .withVertexShader(defaultNamespace)
         .withFragmentShader(fragmentShader)
         .withVertexBinding(0, data.getVertexFormat())
         .withPrimitiveTopology(data.getDrawMode())
         .withBindGroupLayout(uniform.build())
         .withCull(data.getData2().isFlag2())
         .withDepthStencilState(depthTestFunction == CompareOp.ALWAYS_PASS && !depthWrite ? Optional.empty() : Optional.of(new DepthStencilState(depthTestFunction, depthWrite)))
         .withColorTargetState(new ColorTargetState(Optional.ofNullable(blendFunction), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_ALL));
      return colorTargetState.build();
   }

   // 26.3 moved shader sources into ShaderManager's post chain cache (widened in zenith.accesswidener)
   @Nullable
   public static String shaderSource(Identifier defaultNamespace, ShaderType type) {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft != null && minecraft.getShaderManager() != null ? minecraft.getShaderManager().postChains.configs.getShader(defaultNamespace, type) : null;
   }

   public static void invalidate() {
      items.clear();
   }

   public static boolean method6712(Identifier identifier) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.getShaderManager() != null) {
         String path = identifier.getPath();
         int lastIndexOf = path.lastIndexOf(47);
         path = lastIndexOf < 0 ? path : path.substring(0, lastIndexOf);
         Identifier defaultNamespace = Identifier.fromNamespaceAndPath(identifier.getNamespace(), path + "/fragment");
         return shaderSource(defaultNamespace, ShaderType.FRAGMENT) != null;
      } else {
         return false;
      }
   }

   private static boolean method6713(Identifier defaultNamespace, Identifier fragmentShader, String str) {
      return method6714(defaultNamespace, ShaderType.VERTEX).contains(str) || method6714(fragmentShader, ShaderType.FRAGMENT).contains(str);
   }

   private static String method6714(Identifier defaultNamespace, ShaderType type) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.getShaderManager() != null) {
         String str = shaderSource(defaultNamespace, type);
         return str == null ? "" : str;
      } else {
         return "";
      }
   }

   private static boolean method6715(Identifier id, String str) {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft != null && minecraft.getResourceManager() != null
         ? minecraft.getResourceManager().getResource(Identifier.fromNamespaceAndPath(id.getNamespace(), "shaders/" + id.getPath() + str)).isPresent()
         : false;
   }

   private static String method6716(LightUtil.Data data) {
      String lowerCase = data.getText().replace(':', '_').replace('/', '_').toLowerCase(Locale.ROOT);
      return "pipeline/" + lowerCase + "_" + Integer.toHexString(data.hashCode() & 2147483647);
   }

   public static String[] method6717(VertexFormat vertexFormat) {
      return method6718(vertexFormat) ? new String[]{"Sampler0"} : new String[0];
   }

   private static boolean method6718(VertexFormat vertexFormat) {
      return vertexFormat == com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR
         || vertexFormat == com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX
         || vertexFormat == com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE;
   }

   public static String method6719(VertexFormat vertexFormat, PrimitiveTopology primitiveTopology) {
      if (primitiveTopology == PrimitiveTopology.LINES || primitiveTopology == PrimitiveTopology.DEBUG_LINE_STRIP) {
         return vertexFormat == com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH ? "core/rendertype_lines" : "core/position_color";
      } else {
         return vertexFormat != com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR && vertexFormat != com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX
            ? "core/position_color"
            : "core/position_tex_color";
      }
   }

   private static final class Data {
      private final String text;
      private final VertexFormat vertexFormat;
      private final PrimitiveTopology drawMode;
      private final RenderState.Data2 data2;
      private final String text2;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public VertexFormat vertexFormat() {
         return this.vertexFormat;
      }

      public PrimitiveTopology drawMode() {
         return this.drawMode;
      }

      public RenderState.Data2 data2() {
         return this.data2;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.vertexFormat, other.vertexFormat) && java.util.Objects.equals(this.drawMode, other.drawMode) && java.util.Objects.equals(this.data2, other.data2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, vertexFormat, drawMode, data2, text2, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "vertexFormat=" + this.vertexFormat + ", " + "drawMode=" + this.drawMode + ", " + "data2=" + this.data2 + ", " + "text2=" + this.text2 + ", " + "flag=" + this.flag + "]";
      }

      private Data(String text, VertexFormat vertexFormat, PrimitiveTopology drawMode, RenderState.Data2 data2, String str, boolean flag) {
         this.text = text;
         this.vertexFormat = vertexFormat;
         this.drawMode = drawMode;
         this.data2 = data2;
         this.text2 = str;
         this.flag = flag;
      }

      public String getText() {
         return this.text;
      }

      public VertexFormat getVertexFormat() {
         return this.vertexFormat;
      }

      public PrimitiveTopology getDrawMode() {
         return this.drawMode;
      }

      public RenderState.Data2 getData2() {
         return this.data2;
      }

      public String getText2() {
         return this.text2;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.api.vertex.VertexFormatElement;

public final class VertexFormats {
   // 26.2 rewrote vertex formats: elements are named attributes with a GpuFormat. Same layout as 1.21.11:
   // Position vec3, Color rgba8, UV0 vec2, Radii vec4, Params vec2.
   public static final VertexFormat vertexFormat = VertexFormat.builder(0)
      .addAttribute("Position", GpuFormat.RGB32_FLOAT)
      .addAttribute("Color", GpuFormat.RGBA8_UNORM)
      .addAttribute("UV0", GpuFormat.RG32_FLOAT)
      .addAttribute("Radii", GpuFormat.RGBA32_FLOAT)
      .addAttribute("Params", GpuFormat.RG32_FLOAT)
      .build();
   public static final VertexFormatElement vertexFormatElement = vertexFormat.getElement("Radii");
   public static final VertexFormatElement vertexFormatElement2 = vertexFormat.getElement("Params");

   private VertexFormats() {
   }

   // BufferBuilder#beginElement only knows vanilla semantics; custom attributes are written at their offset
   // in the current vertex (vertexPointer widened in zenith.accesswidener).
   public static long pointer(BufferBuilder var0, VertexFormatElement var1) {
      return var0.vertexPointer + var1.offset();
   }
}

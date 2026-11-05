package org.zenith;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.Arrays;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.utility.render.TextureFilterUtils;

public final class RenderState {
   private static boolean flag;
   private static BlendFactor sourceFactor = BlendFactor.SRC_ALPHA;
   private static BlendFactor destFactor = BlendFactor.ONE_MINUS_SRC_ALPHA;
   private static BlendFactor sourceFactor2 = BlendFactor.ONE;
   private static BlendFactor destFactor2 = BlendFactor.ZERO;
   private static boolean flag2 = true;
   private static CompareOp depthTestFunction = CompareOp.GREATER_THAN_OR_EQUAL;
   private static boolean flag3 = true;
   private static boolean flag4;
   private static float floatValue = 1.0F;
   private static float floatValue2 = 1.0F;
   private static float floatValue3 = 1.0F;
   private static float alpha = 1.0F;
   private static final GpuTextureView[] gpuTextureViews = new GpuTextureView[8];
   private static final FilterMode[] filterModes = new FilterMode[8];
   @Nullable
   private static String text;
   private static float floatValue4 = 1.0F;
   private static boolean flag5 = true;
   private static boolean flag6 = true;

   private RenderState() {
   }

   public static void method1871() {
      flag = true;
   }

   public static void method1872() {
      flag = false;
   }

   public static void method1873() {
      method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO);
   }

   public static void method1874(BlendFactor blendFactor, BlendFactor blendFactor2) {
      method1875(blendFactor, blendFactor2, blendFactor, blendFactor2);
   }

   public static void method1875(BlendFactor blendFactor, BlendFactor destFactor3, BlendFactor sourceFactor22, BlendFactor destFactor22) {
      sourceFactor = blendFactor;
      destFactor = destFactor3;
      sourceFactor2 = sourceFactor22;
      destFactor2 = destFactor22;
   }

   public static void method1876() {
      flag2 = true;
   }

   public static void method1877() {
      flag2 = false;
   }

   public static void method1878() {
      depthTestFunction = CompareOp.GREATER_THAN_OR_EQUAL;
      flag4 = true;
   }

   public static void method1879() {
      depthTestFunction = CompareOp.ALWAYS_PASS;
      flag4 = false;
   }

   public static void method1880(CompareOp compareOp) {
      depthTestFunction = compareOp;
      flag4 = compareOp != CompareOp.ALWAYS_PASS;
   }

   public static boolean isFlag4() {
      return flag4;
   }

   public static void method1881() {
      flag4 = false;
   }

   public static void method1882(boolean flag) {
      flag3 = flag;
   }

   public static void method1883(float f, float floatValue4, float floatValue5, float previewAlpha) {
      floatValue = f;
      floatValue2 = floatValue4;
      floatValue3 = floatValue5;
      alpha = previewAlpha;
   }

   public static void method1884() {
      method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static float[] method1885() {
      return new float[]{floatValue, floatValue2, floatValue3, alpha};
   }

   public static float getFloatValue() {
      return floatValue;
   }

   public static float getFloatValue2() {
      return floatValue2;
   }

   public static float getFloatValue3() {
      return floatValue3;
   }

   public static float getAlpha() {
      return alpha;
   }

   public static boolean method1886() {
      return floatValue == 1.0F && floatValue2 == 1.0F && floatValue3 == 1.0F && alpha == 1.0F;
   }

   public static void method1887(int n, @Nullable GpuTextureView glTextureView) {
      method1888(n, glTextureView, FilterMode.NEAREST);
   }

   public static void method1888(int n, @Nullable GpuTextureView glTextureView, FilterMode filterMode) {
      if (n >= 0 && n < gpuTextureViews.length) {
         gpuTextureViews[n] = glTextureView;
         filterModes[n] = filterMode;
      }
   }

   public static void method1889(int n, @Nullable GpuTextureView colorTextureView) {
      method1888(n, colorTextureView, FilterMode.LINEAR);
   }

   public static void method1890(int n, @Nullable Identifier identifier) {
      method1888(n, TextureFilterUtils.method7269(identifier), TextureFilterUtils.method7268(identifier));
   }

   public static void method1891(int n, @Nullable AbstractTexture texture) {
      method1887(n, TextureFilterUtils.method7270(texture));
   }

   public static void method1892(int n, int n2) {
      method1887(n, (GpuTextureView)null);
   }

   public static void method1893(int n) {
      method1887(n, (GpuTextureView)null);
   }

   @Nullable
   public static GpuTextureView method1894(int n) {
      return n >= 0 && n < gpuTextureViews.length ? gpuTextureViews[n] : null;
   }

   public static FilterMode method1895(int n) {
      FilterMode filterMode = n >= 0 && n < filterModes.length ? filterModes[n] : null;
      return filterMode != null ? filterMode : FilterMode.NEAREST;
   }

   public static GpuSampler method1896(int n) {
      FilterMode minFilter = method1895(n);
      AddressMode addressModeU = TextureFilterUtils.method7271(method1894(n));
      return RenderSystem.getSamplerCache().getSampler(addressModeU, addressModeU, minFilter, minFilter, false);
   }

   public static void method1897() {
      Arrays.fill(gpuTextureViews, null);
      Arrays.fill(filterModes, null);
   }

   public static void method1898(String str) {
      text = str;
   }

   @Nullable
   public static String getText() {
      return text;
   }

   public static void method1899() {
      text = null;
   }

   public static void method1900(int x, int y, int width, int height) {
      RenderSystem.enableScissorForRenderTypeDraws(x, y, width, height);
   }

   public static void method1901() {
      RenderSystem.disableScissorForRenderTypeDraws();
   }

   public static void method1902(float floatValue) {
      floatValue4 = floatValue;
   }

   public static float getFloatValue4() {
      return floatValue4;
   }

   public static void method1903(boolean flag, boolean flag2, boolean flag3, boolean flag4) {
      flag5 = flag || flag2 || flag3;
      flag6 = flag4;
   }

   public static boolean isFlag5() {
      return flag5;
   }

   public static boolean isFlag6() {
      return flag6;
   }

   public static RenderState.Data2 method1904() {
      return new RenderState.Data2(flag, sourceFactor, destFactor, sourceFactor2, destFactor2, flag2, depthTestFunction, flag3);
   }

   public static void method1905(RenderState.Data2 data2) {
      flag = data2.isFlag();
      sourceFactor = data2.getSourceFactor();
      destFactor = data2.getDestFactor();
      sourceFactor2 = data2.getSourceFactor2();
      destFactor2 = data2.getDestFactor2();
      flag2 = data2.isFlag2();
      depthTestFunction = data2.getDepthTestFunction();
      flag3 = data2.isFlag3();
   }

   public static RenderState.Data method1906() {
      return new RenderState.Data(method1904(), flag4, method1885(), (GpuTextureView[])gpuTextureViews.clone(), (FilterMode[])filterModes.clone());
   }

   public static void method1907(RenderState.Data data) {
      method1905(data.getData2());
      flag4 = data.isFlag();
      method1883(data.getFloatValues()[0], data.getFloatValues()[1], data.getFloatValues()[2], data.getFloatValues()[3]);
      System.arraycopy(data.getGpuTextureViews(), 0, gpuTextureViews, 0, gpuTextureViews.length);
      System.arraycopy(data.getFilterModes(), 0, filterModes, 0, filterModes.length);
   }

   public static void reset() {
      flag4 = false;
      flag = false;
      flag2 = true;
      depthTestFunction = CompareOp.GREATER_THAN_OR_EQUAL;
      flag3 = true;
      method1873();
      method1884();
      method1897();
   }

   public static final class Data {
      private final RenderState.Data2 data2;
      private final boolean flag;
      private final float[] floatValues;
      private final GpuTextureView[] gpuTextureViews;
      private final FilterMode[] filterModes;

      public RenderState.Data2 data2() {
         return this.data2;
      }

      public boolean flag() {
         return this.flag;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public GpuTextureView[] gpuTextureViews() {
         return this.gpuTextureViews;
      }

      public FilterMode[] filterModes() {
         return this.filterModes;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.data2, other.data2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.gpuTextureViews, other.gpuTextureViews) && java.util.Objects.equals(this.filterModes, other.filterModes);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data2, flag, floatValues, gpuTextureViews, filterModes);
      }

      @Override
      public String toString() {
         return "Data[" + "data2=" + this.data2 + ", " + "flag=" + this.flag + ", " + "floatValues=" + this.floatValues + ", " + "gpuTextureViews=" + this.gpuTextureViews + ", " + "filterModes=" + this.filterModes + "]";
      }

      public Data(RenderState.Data2 data2, boolean flag, float[] floatValues, GpuTextureView[] gpuTextureViews, FilterMode[] filterModes) {
         this.data2 = data2;
         this.flag = flag;
         this.floatValues = floatValues;
         this.gpuTextureViews = gpuTextureViews;
         this.filterModes = filterModes;
      }

      public RenderState.Data2 getData2() {
         return this.data2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public GpuTextureView[] getGpuTextureViews() {
         return this.gpuTextureViews;
      }

      public FilterMode[] getFilterModes() {
         return this.filterModes;
      }
   }

   public static final class Data2 {
      private final boolean flag;
      private final BlendFactor sourceFactor;
      private final BlendFactor destFactor;
      private final BlendFactor sourceFactor2;
      private final BlendFactor destFactor2;
      private final boolean flag2;
      private final CompareOp depthTestFunction;
      private final boolean flag3;

      public boolean flag() {
         return this.flag;
      }

      public BlendFactor sourceFactor() {
         return this.sourceFactor;
      }

      public BlendFactor destFactor() {
         return this.destFactor;
      }

      public BlendFactor sourceFactor2() {
         return this.sourceFactor2;
      }

      public BlendFactor destFactor2() {
         return this.destFactor2;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public CompareOp depthTestFunction() {
         return this.depthTestFunction;
      }

      public boolean flag3() {
         return this.flag3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.sourceFactor, other.sourceFactor) && java.util.Objects.equals(this.destFactor, other.destFactor) && java.util.Objects.equals(this.sourceFactor2, other.sourceFactor2) && java.util.Objects.equals(this.destFactor2, other.destFactor2) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.depthTestFunction, other.depthTestFunction) && java.util.Objects.equals(this.flag3, other.flag3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, sourceFactor, destFactor, sourceFactor2, destFactor2, flag2, depthTestFunction, flag3);
      }

      @Override
      public String toString() {
         return "Data2[" + "flag=" + this.flag + ", " + "sourceFactor=" + this.sourceFactor + ", " + "destFactor=" + this.destFactor + ", " + "sourceFactor2=" + this.sourceFactor2 + ", " + "destFactor2=" + this.destFactor2 + ", " + "flag2=" + this.flag2 + ", " + "depthTestFunction=" + this.depthTestFunction + ", " + "flag3=" + this.flag3 + "]";
      }

      public Data2(boolean flag, BlendFactor sourceFactor, BlendFactor destFactor, BlendFactor sourceFactor2, BlendFactor destFactor2, boolean flag2, CompareOp depthTestFunction, boolean flag3) {
         this.flag = flag;
         this.sourceFactor = sourceFactor;
         this.destFactor = destFactor;
         this.sourceFactor2 = sourceFactor2;
         this.destFactor2 = destFactor2;
         this.flag2 = flag2;
         this.depthTestFunction = depthTestFunction;
         this.flag3 = flag3;
      }

      public BlendFunction method1908() {
         return this.flag ? new BlendFunction(this.sourceFactor, this.destFactor, this.sourceFactor2, this.destFactor2) : null;
      }

      public RenderState.Data2 method1909() {
         return new RenderState.Data2(
            this.flag, this.sourceFactor, this.destFactor, this.sourceFactor2, this.destFactor2, false, CompareOp.ALWAYS_PASS, false
         );
      }

      public RenderState.Data2 method1910() {
         return new RenderState.Data2(
            this.flag, this.sourceFactor, this.destFactor, this.sourceFactor2, this.destFactor2, false, this.depthTestFunction, this.flag3
         );
      }

      public boolean isFlag() {
         return this.flag;
      }

      public BlendFactor getSourceFactor() {
         return this.sourceFactor;
      }

      public BlendFactor getDestFactor() {
         return this.destFactor;
      }

      public BlendFactor getSourceFactor2() {
         return this.sourceFactor2;
      }

      public BlendFactor getDestFactor2() {
         return this.destFactor2;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public CompareOp getDepthTestFunction() {
         return this.depthTestFunction;
      }

      public boolean isFlag3() {
         return this.flag3;
      }
   }
}

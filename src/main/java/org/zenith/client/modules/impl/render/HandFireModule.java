package org.zenith.client.modules.impl.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL20;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.HandRenderState;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.FramebufferUtils;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ShaderProgram;

@ModuleInfo(
   name = "HandFire",
   category = ModuleCategory.RENDER,
   description = "module.handfire.description"
)
public final class HandFireModule extends Module {
   public static final HandFireModule INSTANCE = new HandFireModule();
   private static final int CONST_intValue = 1048576;
   private static final int CONST_intValue2 = new ZenithColor(255, 255, 255).method7287();
   private static final int CONST_intValue3 = new ZenithColor(24, 12, 36).method7287();
   private static final int[] CONST_intValues = new int[4];
   private static TextureTarget simpleFramebuffer;
   private static TextureTarget simpleFramebuffer2;
   private static TextureTarget simpleFramebuffer3;
   private static TextureTarget simpleFramebuffer4;
   private static ShaderProgram shaderProgram;
   private static ShaderProgram shaderProgram2;
   private static ShaderProgram shaderProgram3;
   private static boolean initialized;
   private static boolean flag;
   private static boolean flag2;
   private static float floatValue = 1.0F;
   private static boolean flag3;
   private static boolean flag4;
   private static boolean flag5;
   private static boolean flag6;
   private static boolean flag7;
   private static float floatValue2;
   private static long longValue;
   private static ByteBufferBuilder bufferAllocator;
   private final int[] intValues = new int[4];
   private static final float CONST_floatValue = 1.0F;
   private static final float CONST_floatValue2 = 0.0F;
   private static final float CONST_floatValue3 = 0.25F;
   private static final float CONST_floatValue4 = 1.0F;
   private static final float CONST_floatValue5 = 255.0F;
   private static final int CONST_intValue4 = new ZenithColor(0, 0, 0).method7287();

   private HandFireModule() {
   }

   public boolean method6612() {
      if (!ShaderHandModule.INSTANCE.method1254()) {
         if ((method6613() || bufferAllocator != null) && mc.isSameThread()) {
            method6614();
         }

         return false;
      } else {
         return mc.player != null && mc.level != null && mc.getWindow() != null;
      }
   }

   private static boolean method6613() {
      return simpleFramebuffer != null || simpleFramebuffer2 != null || simpleFramebuffer3 != null || simpleFramebuffer4 != null;
   }

   private static void method6614() {
      method6637(simpleFramebuffer);
      method6637(simpleFramebuffer2);
      method6637(simpleFramebuffer3);
      method6637(simpleFramebuffer4);
      simpleFramebuffer = null;
      simpleFramebuffer2 = null;
      simpleFramebuffer3 = null;
      simpleFramebuffer4 = null;
      method6640();
      method6636();
   }

   public static float method6615() {
      float f = floatValue;
      floatValue = 1.0F;
      return f;
   }

   public static boolean method6616() {
      return HandRenderState.isActive();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      method6640();
      method6636();
   }

   public void method6617() {
      if (this.method6612() && !HandRenderState.isActive()) {
         method6620();
         if (initialized) {
            HandFireModule.Data data = HandFireModule.Data.method6645();
            if (data.intValue3 > 0 && data.intValue4 > 0) {
               try {
                  method6628(data.intValue3, data.intValue4);
                  if (method6629()) {
                     method6632(simpleFramebuffer);
                     method6632(simpleFramebuffer2);
                     flag3 = true;
                     flag4 = false;
                     flag5 = false;
                     flag6 = false;
                     floatValue = 1.0F;
                     floatValue2 = 0.0F;
                     return;
                  }
               } catch (Exception e) {
                  method6638(e);
                  return;
               } finally {
                  method6633(data);
               }
            }
         }
      }
   }

   public void method6618() {
      if (flag3) {
         HandFireModule.Data data = HandFireModule.Data.method6645();

         try {
            if (ShaderHandModule.INSTANCE.method1256()) {
               if (flag6) {
                  method6621();
                  this.method6622();
               }

               method6639();
            } else if (flag4 || flag7) {
               if (flag4) {
                  method6621();
               }

               GpuTextureView colorTextureView = this.method6622();
               this.method6623(data, colorTextureView);
               method6639();
            }
         } catch (Exception e) {
            method6638(e);
         } finally {
            flag3 = false;
            flag4 = false;
            flag5 = false;
            flag6 = false;
            floatValue = 1.0F;
            method6633(data);
         }
      }
   }

   public void method6619(
      AbstractClientPlayer abstractClientPlayerEntity,
      float f,
      float f2,
      InteractionHand hand,
      float f3,
      ItemStack itemStack,
      float f4,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int n,
      Consumer<SubmitNodeCollector> consumer
   ) {
      if (this.method6612() && !HandRenderState.isActive()) {
         if (!flag3) {
            this.method6617();
         }

         method6620();
         if (initialized && flag3) {
            HandFireModule.Data data = HandFireModule.Data.method6645();
            if (data.intValue3 > 0 && data.intValue4 > 0) {
               float floatValue3 = this.method6641();
               floatValue = floatValue3;
               floatValue2 = Math.max(floatValue2, 1.0F);

               try {
                  RenderState.method1903(true, true, true, true);
                  RenderState.method1882(true);
                  RenderState.method1878();
                  HandRenderState.begin();
                  ByteBufferBuilder byteBufferBuilder = method6635();

                  try {
                     OutlineVertexProvider.method1542(simpleFramebuffer, consumer::accept);

                     flag4 = true;
                     longValue = System.currentTimeMillis();
                  } finally {
                     byteBufferBuilder.discard();
                     HandRenderState.end();
                  }

                  if (ShaderHandModule.INSTANCE.method1256()) {
                     if (flag5) {
                        flag6 = true;
                     } else {
                        method6621();
                        GpuTextureView colorTextureView = this.method6622();
                        this.method6623(data, colorTextureView);
                        flag5 = true;
                        method6633(data);
                     }
                  }

                  if (floatValue3 > 0.0F) {
                     method6631(data);
                     consumer.accept(submitNodeCollector);
                  }
               } catch (Exception e) {
                  method6631(data);
                  if (floatValue3 > 0.0F) {
                     floatValue = floatValue3;
                     consumer.accept(submitNodeCollector);
                  }

                  method6638(e);
               } finally {
                  HandRenderState.end();
                  method6633(data);
               }
            } else {
               consumer.accept(submitNodeCollector);
            }
         } else {
            consumer.accept(submitNodeCollector);
         }
      } else {
         consumer.accept(submitNodeCollector);
      }
   }

   private static void method6620() {
      if (!initialized) {
         try {
            shaderProgram = new ShaderProgram("hand", "hand_fire_mask", "smoke");
            shaderProgram2 = new ShaderProgram("hand", "hand_fire_accumulate", "smoke");
            shaderProgram3 = new ShaderProgram("hand", "hand_fire_composite", "smoke");
            initialized = true;
         } catch (Exception e) {
            if (!flag) {
               System.err.println("Failed to initialize HandFire shaders");
               e.printStackTrace();
               flag = true;
            }

            initialized = false;
         }
      }
   }

   private static void method6621() {
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1872();
      shaderProgram.method5517();
      shaderProgram.method5518("SourceTex", 0);
      RenderState.method1889(0, BlitUtils.method6450(simpleFramebuffer));
      ImmediateRenderer.method8013(simpleFramebuffer2);
      HandShaders.method6282();
      shaderProgram.unbind();
      RenderState.method1893(0);
   }

   private GpuTextureView method6622() {
      if (!flag7) {
         method6632(simpleFramebuffer3);
         method6632(simpleFramebuffer4);
         flag7 = true;
      }

      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1872();
      RenderState.method1903(true, true, true, true);
      int[] ints = this.method6625();
      shaderProgram2.method5517();
      shaderProgram2.method5518("PreviousTex", 0);
      shaderProgram2.method5518("HandTex", 1);
      shaderProgram2.method5518("MaskTex", 2);
      shaderProgram2.method5521("TexelSize", 1.0F / simpleFramebuffer4.width, 1.0F / simpleFramebuffer4.height);
      shaderProgram2.method5519("Time", method6627());
      shaderProgram2.method5519("Decay", 1.0F);
      shaderProgram2.method5519("Intensity", 1.0F);
      shaderProgram2.method5519("Lift", 0.0F);
      shaderProgram2.method5519("Turbulence", 0.25F);
      shaderProgram2.method5519("Alpha", ShaderHandModule.INSTANCE.method1255());
      shaderProgram2.method5519("EmitStrength", floatValue2);
      method6634(shaderProgram2, "ColorHot", ints[0]);
      method6634(shaderProgram2, "ColorMid", ints[1]);
      method6634(shaderProgram2, "ColorEdge", ints[2]);
      method6634(shaderProgram2, "ColorSmoke", ints[3]);
      RenderState.method1889(0, BlitUtils.method6450(simpleFramebuffer3));
      RenderState.method1889(1, BlitUtils.method6450(simpleFramebuffer));
      RenderState.method1889(2, BlitUtils.method6450(simpleFramebuffer2));
      ImmediateRenderer.method8013(simpleFramebuffer4);
      HandShaders.method6282();
      shaderProgram2.unbind();
      method6624();
      TextureTarget textureTarget = simpleFramebuffer3;
      simpleFramebuffer3 = simpleFramebuffer4;
      simpleFramebuffer4 = textureTarget;
      return BlitUtils.method6450(simpleFramebuffer3);
   }

   private void method6623(HandFireModule.Data data, GpuTextureView colorTextureView) {
      method6631(data);
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1871();
      RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
      shaderProgram3.method5517();
      shaderProgram3.method5518("FireTex", 0);
      shaderProgram3.method5518("HandTex", 1);
      shaderProgram3.method5518("MaskTex", 2);
      shaderProgram3.method5521("TexelSize", 1.0F / data.intValue3, 1.0F / data.intValue4);
      shaderProgram3.method5519("Time", method6627());
      shaderProgram3.method5519("Intensity", 1.0F);
      shaderProgram3.method5519("Alpha", ShaderHandModule.INSTANCE.method1255());
      int[] ints = this.method6625();
      method6634(shaderProgram3, "ColorHot", ints[0]);
      method6634(shaderProgram3, "ColorMid", ints[1]);
      method6634(shaderProgram3, "ColorEdge", ints[2]);
      RenderState.method1889(0, colorTextureView);
      RenderState.method1889(1, BlitUtils.method6450(simpleFramebuffer));
      RenderState.method1889(2, BlitUtils.method6450(simpleFramebuffer2));
      HandShaders.method6282();
      shaderProgram3.unbind();
      method6624();
   }

   private static void method6624() {
      RenderState.method1893(0);
      RenderState.method1893(1);
      RenderState.method1893(2);
   }

   private int[] method6625() {
      if (ShaderHandModule.INSTANCE.method1257()) {
         long l = System.currentTimeMillis() / 7L;
         this.intValues[0] = method6642((l + 8L) % 360L, 255);
         this.intValues[1] = method6642((l + 45L) % 360L, 255);
         this.intValues[2] = method6642((l + 120L) % 360L, 255);
         this.intValues[3] = method6642((l + 210L) % 360L, 185);
         return this.intValues;
      } else if (ShaderHandModule.INSTANCE.method1258()) {
         int shade = method6644(Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 255);
         this.method6626(shade, shade);
         return this.intValues;
      } else if (ShaderHandModule.INSTANCE.method1259()) {
         int shade = method6644(ShaderHandModule.INSTANCE.method1260(), 255);
         int n = method6644(ShaderHandModule.INSTANCE.method1261(), 255);
         this.method6626(shade, n);
         return this.intValues;
      } else {
         this.intValues[0] = -1;
         this.intValues[1] = -1;
         this.intValues[2] = -1;
         this.intValues[3] = -1;
         return this.intValues;
      }
   }

   private void method6626(int shade, int n) {
      this.intValues[0] = method6643(shade, CONST_intValue2, 0.25F);
      this.intValues[1] = shade;
      this.intValues[2] = method6643(n, CONST_intValue4, 0.2F);
      this.intValues[3] = method6644(method6643(n, CONST_intValue3, 0.58F), 185);
   }

   private static float method6627() {
      return (float)(System.currentTimeMillis() % 100000L) / 1000.0F;
   }

   private static void method6628(int width, int height) {
      if (simpleFramebuffer == null
         || simpleFramebuffer2 == null
         || simpleFramebuffer3 == null
         || simpleFramebuffer4 == null
         || simpleFramebuffer.width != width
         || simpleFramebuffer.height != height
         || simpleFramebuffer2.width != width
         || simpleFramebuffer2.height != height
         || simpleFramebuffer3.width != width
         || simpleFramebuffer3.height != height
         || simpleFramebuffer4.width != width
         || simpleFramebuffer4.height != height) {
         method6637(simpleFramebuffer);
         method6637(simpleFramebuffer2);
         method6637(simpleFramebuffer3);
         method6637(simpleFramebuffer4);
         simpleFramebuffer = BlitUtils.method6448("zenith handfire", width, height, true);
         simpleFramebuffer2 = BlitUtils.method6448("zenith handfire", width, height, false);
         simpleFramebuffer3 = BlitUtils.method6448("zenith handfire", width, height, false);
         simpleFramebuffer4 = BlitUtils.method6448("zenith handfire", width, height, false);
         method6630(simpleFramebuffer);
         method6630(simpleFramebuffer2);
         method6630(simpleFramebuffer3);
         method6630(simpleFramebuffer4);
         flag7 = false;
      }
   }

   private static boolean method6629() {
      return simpleFramebuffer != null && simpleFramebuffer2 != null && simpleFramebuffer3 != null && simpleFramebuffer4 != null;
   }

   private static void method6630(TextureTarget simpleFramebuffer) {
      if (simpleFramebuffer != null) {
      }
   }

   private static void method6631(HandFireModule.Data data) {
      ImmediateRenderer.method8013(null);
   }

   private static void method6632(TextureTarget simpleFramebuffer3) {
      if (simpleFramebuffer3 != null) {
         BlitUtils.method6456(simpleFramebuffer3, 0);
      }
   }

   private static void method6633(HandFireModule.Data data) {
      RenderState.method1903(true, true, true, true);
      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1873();
      RenderState.method1872();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
      GL20.glUseProgram(0);
      FramebufferUtils.method5653();
   }

   private static void method6634(ShaderProgram shaderProgram2, String key, int intValue) {
      shaderProgram2.method5523(key, ColorUtils.method6424(intValue), ColorUtils.method6425(intValue), ColorUtils.method6426(intValue), ColorUtils.method6427(intValue));
   }

   private static ByteBufferBuilder method6635() {
      if (bufferAllocator == null) {
         bufferAllocator = new ByteBufferBuilder(1048576);
      }

      return bufferAllocator;
   }

   private static void method6636() {
      if (bufferAllocator != null) {
         bufferAllocator.close();
         bufferAllocator = null;
      }
   }

   private static void method6637(TextureTarget simpleFramebuffer) {
      if (simpleFramebuffer != null) {
         simpleFramebuffer.destroyBuffers();
      }
   }

   private static void method6638(Exception e) {
      if (!flag2) {
         System.err.println("HandFire render failed");
         e.printStackTrace();
         flag2 = true;
      }
   }

   private static void method6639() {
      if (floatValue2 <= 0.001F && longValue > 0L && System.currentTimeMillis() - longValue > 2200L) {
         flag7 = false;
         method6632(simpleFramebuffer3);
         method6632(simpleFramebuffer4);
      }
   }

   private static void method6640() {
      flag3 = false;
      flag4 = false;
      flag5 = false;
      flag6 = false;
      flag7 = false;
      floatValue = 1.0F;
      floatValue2 = 0.0F;
      longValue = 0L;
   }

   private float method6641() {
      return Math.max(0.0F, Math.min(255.0F, 255.0F)) / 255.0F;
   }

   private static int method6642(long l, int alphaValue) {
      float hue = (float)((l % 360L + 360L) % 360L) / 360.0F;
      return ZenithColor.method7297(hue, 1.0F, 1.0F).method7293(alphaValue).method7287();
   }

   private static int method6643(int shade, int n, float hover) {
      return ZenithColor.method7291(shade).method7295(ZenithColor.method7291(n), hover).method7287();
   }

   private static int method6644(int shade, int alphaValue) {
      return ZenithColor.method7291(shade).method7293(alphaValue).method7287();
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      private Data(int intValue, int n, int width, int height) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = width;
         this.intValue4 = height;
      }

      public static HandFireModule.Data method6645() {
         RenderTarget renderTarget = BlitUtils.method6452();
         return new HandFireModule.Data(0, 0, renderTarget.width, renderTarget.height);
      }

      public void restore() {
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int width() {
         return this.intValue3;
      }

      public int height() {
         return this.intValue4;
      }
   }
}

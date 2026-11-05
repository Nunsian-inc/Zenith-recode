package org.zenith.utility.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import lombok.Generated;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.ImageBlur;
import org.zenith.utility.misc.TextureId;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ShapeRenderer implements MinecraftAccess {
   public static HashMap<ShapeRenderer.Data, ShapeRenderer.Entry2> hashMap = new HashMap<>();
   public static HashMap<Integer, ShapeRenderer.Entry2> hashMap2 = new HashMap<>();
   public static final Stack<ShapeRenderer.Data3> stack = new Stack<>();
   private static final List<ShapeRenderer.Data2> items = new ArrayList<>();
   private static final int CONST_intValue = 5;
   private static final Set<ShapeRenderer.Data> items2 = ConcurrentHashMap.newKeySet();
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
   private static final HashMap<ShapeRenderer.Data, ShapeRenderer.Data> hashMap3 = new HashMap<>();

   public static void method7153() {
      MsdfRenderer.flushBatch();
      RenderState.method1901();
   }

   public static void method7154(
      BufferBuilder bufferBuilder, Matrix4f pose, float x, float y, float f, float y2, Color color, Color color2, Color color3, Color color4
   ) {
      bufferBuilder.addVertex(pose, x, y2, 0.0F).setColor(color.getRGB());
      bufferBuilder.addVertex(pose, f, y2, 0.0F).setColor(color2.getRGB());
      bufferBuilder.addVertex(pose, f, y, 0.0F).setColor(color3.getRGB());
      bufferBuilder.addVertex(pose, x, y, 0.0F).setColor(color4.getRGB());
   }

   public static boolean method7155(double d, double d2, double d3, double d4, double d5, double d6) {
      return d >= d3 && d - d5 <= d3 && d2 >= d4 && d2 - d6 <= d4;
   }

   public static void method7156(PoseStack poseStack, float f, float y, float f2, float f3, int n, Color color) {
   }

   public static void method7157(GuiGraphicsExtractor customDrawContext) {
      PoseStack poseStack = MatrixUtils.method666(customDrawContext.pose());
      Matrix4f pose = poseStack.last().pose();
      if (!items.isEmpty()) {
         MsdfRenderer.flushBatch();
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1898("core/position_color");
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
         items.forEach(var2x -> method7161(pose, begin, var2x.floatValue, var2x.floatValue2, var2x.floatValue3, var2x.floatValue4, var2x.intValue));
         ImmediateRenderer.method8015(begin.buildOrThrow());
         RenderState.method1872();
         items.clear();
      }
   }

   public static void method7158(float floatValue, float f, float f2, float f3, int color) {
      items.add(new ShapeRenderer.Data2(floatValue, f, f2, f3, ColorUtils.method6439(color, RenderState.getAlpha())));
   }

   public static void method7159(
      PoseStack poseStack,
      double x,
      double y,
      double d,
      double d2,
      float f,
      float f2,
      double d3,
      double d4,
      double d5,
      double d6,
      int color,
      int n,
      int n2,
      int n3
   ) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      method7181(begin, poseStack, x, y, d, d2, f, f2, d3, d4, d5, d6, color, n, n2, n3);
      ImmediateRenderer.method8015(begin.buildOrThrow());
   }

   public static void method7160(Matrix4f pose, BufferBuilder bufferBuilder, float x, float y, float f, float f2) {
      bufferBuilder.addVertex(pose, x, y, 0.0F);
      bufferBuilder.addVertex(pose, x, y + f2, 0.0F);
      bufferBuilder.addVertex(pose, x + f, y + f2, 0.0F);
      bufferBuilder.addVertex(pose, x + f, y, 0.0F);
   }

   public static void method7161(Matrix4f pose, BufferBuilder begin, float floatValue, float y, float width, float height, int intValue) {
      begin.addVertex(pose, floatValue, y, 0.0F).setColor(intValue);
      begin.addVertex(pose, floatValue, y + height, 0.0F).setColor(intValue);
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setColor(intValue);
      begin.addVertex(pose, floatValue + width, y, 0.0F).setColor(intValue);
   }

   public static void method7162(Matrix4f pose, float x, float y, float f, float f2, int color) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, x, y + f2, 0.0F).setUv(0.0F, 0.0F).setColor(color);
      begin.addVertex(pose, x + f, y + f2, 0.0F).setUv(0.0F, 1.0F).setColor(color);
      begin.addVertex(pose, x + f, y, 0.0F).setUv(1.0F, 1.0F).setColor(color);
      begin.addVertex(pose, x, y, 0.0F).setUv(1.0F, 0.0F).setColor(color);
      ImmediateRenderer.method8015(begin.buildOrThrow());
   }

   public static void method7163(Matrix4f pose, BufferBuilder bufferBuilder, float x, float y, float f, float f2, int color) {
      bufferBuilder.addVertex(pose, x, y + f2, 0.0F).setUv(0.0F, 0.0F).setColor(color);
      bufferBuilder.addVertex(pose, x + f, y + f2, 0.0F).setUv(0.0F, 1.0F).setColor(color);
      bufferBuilder.addVertex(pose, x + f, y, 0.0F).setUv(1.0F, 1.0F).setColor(color);
      bufferBuilder.addVertex(pose, x, y, 0.0F).setUv(1.0F, 0.0F).setColor(color);
   }

   private static ShapeRenderer.Data method7164(float width, float height, int px, ProxyDotRadiusData proxyDotRadiusData2) {
      int intValue = Math.max(1, Math.round(width));
      int n = Math.max(1, Math.round(height));
      int n2 = Math.max(1, intValue - px * 2);
      int n3 = Math.max(1, n - px * 2);
      ProxyDotRadiusData proxyDotRadiusData = method7171(n2, n3, proxyDotRadiusData2);
      return new ShapeRenderer.Data(
         intValue,
         n,
         px,
         Math.round(proxyDotRadiusData.getFloatValue()),
         Math.round(proxyDotRadiusData.getFloatValue2()),
         Math.round(proxyDotRadiusData.getFloatValue3()),
         Math.round(proxyDotRadiusData.getFloatValue4())
      );
   }

   private static ShapeRenderer.Data method7165(ShapeRenderer.Data key) {
      ShapeRenderer.Data data = null;
      int n = Integer.MAX_VALUE;

      for (ShapeRenderer.Data data2 : hashMap.keySet()) {
         int n2 = method7173(data2, key);
         if (n2 < n) {
            n = n2;
            data = data2;
         }
      }

      return data;
   }

   public static void method7166(PoseStack poseStack, float floatValue, float y, float width, float height, int px, ProxyDotRadiusData proxyDotRadiusData2, GlowColor glowColor) {
      method7167(poseStack.last().pose(), floatValue, y, width, height, px, proxyDotRadiusData2, glowColor);
   }

   public static void method7167(Matrix4f pose, float floatValue, float y, float width, float height, int px, ProxyDotRadiusData proxyDotRadiusData2, GlowColor glowColor) {
      width += px * 2;
      height += px * 2;
      floatValue -= px;
      y -= px;
      ShapeRenderer.Data key = method7164(width, height, px, proxyDotRadiusData2);
      ShapeRenderer.Entry2 entry2 = hashMap.get(key);
      ShapeRenderer.Data data = key;
      if (entry2 == null) {
         data = hashMap3.get(key);
         if (data == null || !hashMap.containsKey(data)) {
            data = method7165(key);
            if (data != null) {
               if (hashMap3.size() > 512) {
                  hashMap3.clear();
               }

               hashMap3.put(key, data);
            }
         }

         if (data != null) {
            entry2 = hashMap.get(data);
         }
      }

      if (entry2 == null || method7173(data, key) >= 5 || key.intValue7 != data.intValue7) {
         method7168(key);
      }

      if (entry2 != null) {
         entry2.reset();
         RenderShaders.method5281(pose, entry2.textureId.getIdentifier(), floatValue, y, width, height, glowColor);
      }
   }

   private static void method7168(ShapeRenderer.Data key) {
      if (!hashMap.containsKey(key) && items2.add(key)) {
         executorService.execute(() -> {
            NativeImage nativeImage = null;

            try {
               BufferedImage bufferedImage = method7170(key);
               BufferedImage bufferedImage2 = key.getIntValue3() > 0 ? new ImageBlur(key.getIntValue3()).method2287(bufferedImage, null) : bufferedImage;
               nativeImage = method7169(bufferedImage2);
               NativeImage image = nativeImage;
               nativeImage = null;
               mc.execute(() -> {
                  try {
                     if (!hashMap.containsKey(key)) {
                        hashMap.put(key, new ShapeRenderer.Entry2(image));
                     } else {
                        image.close();
                     }
                  } finally {
                     items2.remove(key);
                  }
               });
            } catch (Exception e) {
               if (nativeImage != null) {
                  nativeImage.close();
               }

               items2.remove(key);
               e.printStackTrace();
            }
         });
      }
   }

   private static NativeImage method7169(BufferedImage bufferedImage) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int[] rgb = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
      NativeImage nativeImage = new NativeImage(width, height, false);

      for (int i = 0; i < height; i++) {
         int n = i * width;

         for (int j = 0; j < width; j++) {
            nativeImage.setPixel(j, i, rgb[n + j]);
         }
      }

      return nativeImage;
   }

   private static BufferedImage method7170(ShapeRenderer.Data key) {
      BufferedImage bufferedImage = new BufferedImage(key.width(), key.height(), 2);
      int[] rgbArray = new int[key.width() * key.height()];
      int n = Math.max(1, key.width() - key.getIntValue3() * 2);
      int n2 = Math.max(1, key.height() - key.getIntValue3() * 2);
      float f = n * 0.5F;
      float f2 = n2 * 0.5F;
      float f3 = Math.max(0.0F, f - 1.0F);
      float f4 = Math.max(0.0F, f2 - 1.0F);

      for (int i = 0; i < key.height(); i++) {
         float f5 = i + 0.5F - key.getIntValue3();

         for (int j = 0; j < key.width(); j++) {
            float f6 = j + 0.5F - key.getIntValue3();
            float f7 = f - f6;
            float f8 = f2 - f5;
            float f9 = method7174(f7, f8, f3, f4, key.getIntValue4(), key.getIntValue7(), key.getIntValue5(), key.getIntValue6());
            float f10 = 1.0F - method7175(0.0F, 1.0F, f9);
            if (!(f10 <= 0.0F)) {
               int n3 = Mth.clamp(Math.round(f10 * 255.0F), 0, 255);
               rgbArray[i * key.width() + j] = n3 << 24 | 16777215;
            }
         }
      }

      bufferedImage.setRGB(0, 0, key.width(), key.height(), rgbArray, 0, key.width());
      return bufferedImage;
   }

   private static ProxyDotRadiusData method7171(int n, int n2, ProxyDotRadiusData proxyDotRadiusData2) {
      float f = Math.max(0.0F, n * 0.5F - 1.0F);
      float f2 = Math.max(0.0F, n2 * 0.5F - 1.0F);
      return new ProxyDotRadiusData(
         method7172(proxyDotRadiusData2.getFloatValue(), f, f2),
         method7172(proxyDotRadiusData2.getFloatValue2(), f, f2),
         method7172(proxyDotRadiusData2.getFloatValue3(), f, f2),
         method7172(proxyDotRadiusData2.getFloatValue4(), f, f2)
      );
   }

   private static float method7172(float floatValue, float f, float f2) {
      return Math.max(0.0F, Math.min(floatValue, Math.min(f, f2)));
   }

   private static int method7173(ShapeRenderer.Data data, ShapeRenderer.Data key) {
      return data != null && key != null
         ? Math.abs(data.width() - key.width())
            + Math.abs(data.height() - key.height())
            + Math.abs(data.getIntValue3() - key.getIntValue3())
            + Math.abs(data.getIntValue4() - key.getIntValue4())
            + Math.abs(data.getIntValue5() - key.getIntValue5())
            + Math.abs(data.getIntValue6() - key.getIntValue6())
            + Math.abs(data.getIntValue7() - key.getIntValue7())
         : Integer.MAX_VALUE;
   }

   private static float method7174(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
      float f9 = f > 0.0F ? f5 : f7;
      float f10 = f > 0.0F ? f6 : f8;
      float f11 = f2 > 0.0F ? f9 : f10;
      float f12 = Math.abs(f) - f3 + f11;
      float f13 = Math.abs(f2) - f4 + f11;
      return Math.min(Math.max(f12, f13), 0.0F) + (float)Math.hypot(Math.max(f12, 0.0F), Math.max(f13, 0.0F)) - f11;
   }

   private static float method7175(float f, float f2, float f3) {
      if (f == f2) {
         return f3 < f ? 0.0F : 1.0F;
      } else {
         float f4 = Mth.clamp((f3 - f) / (f2 - f), 0.0F, 1.0F);
         return f4 * f4 * (3.0F - 2.0F * f4);
      }
   }

   public static void method7176(TextureId textureId, BufferedImage im) {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ImageIO.write(im, "png", byteArrayOutputStream);
         byte[] byteArray = byteArrayOutputStream.toByteArray();
         method7178(textureId, byteArray);
      } catch (Exception e) {
      }
   }

   public static void method7177(TextureId textureId, byte[] src) {
      try {
         ByteBuffer bytes = BufferUtils.createByteBuffer(src.length).put(src);
         bytes.flip();
         DynamicTexture dynamicTexture = new DynamicTexture(textureId.getIdentifier()::toString, NativeImage.read(bytes));
         mc.getTextureManager().register(textureId.getIdentifier(), dynamicTexture);
      } catch (Exception e) {
      }
   }

   public static void method7178(TextureId textureId, byte[] byteArray) {
      try {
         ByteBuffer bytes = BufferUtils.createByteBuffer(byteArray.length).put(byteArray);
         bytes.flip();
         NativeImage image = NativeImage.read(bytes);
         mc.execute(() -> mc.getTextureManager().register(textureId.getIdentifier(), new DynamicTexture(textureId.getIdentifier()::toString, image)));
      } catch (Exception e) {
      }
   }

   public static void method7179(
      PoseStack poseStack, double x, double y, double d, double d2, float f, float f2, double d3, double d4, double d5, double d6
   ) {
      MsdfRenderer.flushBatch();
      double x2 = x + d;
      double y2 = y + d2;
      double z = 0.0;
      Matrix4f pose = poseStack.last().pose();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);
      begin.addVertex(pose, (float)x, (float)y2, (float)z).setUv(f / (float)d5, (f2 + (float)d4) / (float)d6);
      begin.addVertex(pose, (float)x2, (float)y2, (float)z).setUv((f + (float)d3) / (float)d5, (f2 + (float)d4) / (float)d6);
      begin.addVertex(pose, (float)x2, (float)y, (float)z).setUv((f + (float)d3) / (float)d5, f2 / (float)d6);
      begin.addVertex(pose, (float)x, (float)y, (float)z).setUv(f / (float)d5, (f2 + 0.0F) / (float)d6);
      ImmediateRenderer.method8015(begin.buildOrThrow());
   }

   public static void method7180(
      PoseStack poseStack,
      double x,
      double y,
      double d,
      double d2,
      float f,
      float f2,
      double d3,
      double d4,
      double d5,
      double d6,
      Color color,
      Color color2,
      Color color3,
      Color color4
   ) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      method7182(begin, poseStack, x, y, d, d2, f, f2, d3, d4, d5, d6, color, color2, color3, color4);
      ImmediateRenderer.method8015(begin.buildOrThrow());
   }

   public static void method7181(
      BufferBuilder begin,
      PoseStack poseStack,
      double x,
      double y,
      double d,
      double d2,
      float f,
      float f2,
      double d3,
      double d4,
      double d5,
      double d6,
      int color,
      int n,
      int color2,
      int color3
   ) {
      double x2 = x + d;
      double y2 = y + d2;
      double z = 0.0;
      Matrix4f pose = poseStack.last().pose();
      begin.addVertex(pose, (float)x, (float)y2, (float)z).setUv(f / (float)d5, (f2 + (float)d4) / (float)d6).setColor(color);
      begin.addVertex(pose, (float)x2, (float)y2, (float)z)
         .setUv((f + (float)d3) / (float)d5, (f2 + (float)d4) / (float)d6)
         .setColor(n);
      begin.addVertex(pose, (float)x2, (float)y, (float)z).setUv((f + (float)d3) / (float)d5, f2 / (float)d6).setColor(color2);
      begin.addVertex(pose, (float)x, (float)y, (float)z).setUv(f / (float)d5, (f2 + 0.0F) / (float)d6).setColor(color3);
   }

   public static void method7182(
      BufferBuilder begin,
      PoseStack poseStack,
      double x,
      double y,
      double d,
      double d2,
      float f,
      float f2,
      double d3,
      double d4,
      double d5,
      double d6,
      Color color,
      Color color2,
      Color color3,
      Color color4
   ) {
      double x2 = x + d;
      double y2 = y + d2;
      double z = 0.0;
      Matrix4f pose = poseStack.last().pose();
      begin.addVertex(pose, (float)x, (float)y2, (float)z).setUv(f / (float)d5, (f2 + (float)d4) / (float)d6).setColor(color.getRGB());
      begin.addVertex(pose, (float)x2, (float)y2, (float)z)
         .setUv((f + (float)d3) / (float)d5, (f2 + (float)d4) / (float)d6)
         .setColor(color2.getRGB());
      begin.addVertex(pose, (float)x2, (float)y, (float)z).setUv((f + (float)d3) / (float)d5, f2 / (float)d6).setColor(color3.getRGB());
      begin.addVertex(pose, (float)x, (float)y, (float)z).setUv(f / (float)d5, (f2 + 0.0F) / (float)d6).setColor(color4.getRGB());
   }

   public static void method7183() {
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void method7184(PoseStack poseStack, float f, float f2, float f3, float f4, float f5, boolean flag, boolean flag2, int n) {
   }

   public static void method7185(PoseStack poseStack, float f, float f2, float f3, Color color) {
   }

   public static void method7186(PoseStack poseStack, float x, float y, float f, float f2, float f3, boolean flag, boolean flag2, int color) {
      MsdfRenderer.flushBatch();
      if (flag2) {
         method7156(poseStack, x - f * f2, y, x + f * f2 - (x - f * f2), f, 10, method7189(new Color(color), 140));
      }

      poseStack.pushPose();
      method7183();
      Matrix4f pose = poseStack.last().pose();
      RenderState.method1898("core/position_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, x, y, 0.0F).setColor(color);
      begin.addVertex(pose, x - f * f2, y + f, 0.0F).setColor(color);
      begin.addVertex(pose, x, y + f - f3, 0.0F).setColor(color);
      begin.addVertex(pose, x, y, 0.0F).setColor(color);
      color = method7197(new Color(color), 0.8F).getRGB();
      begin.addVertex(pose, x, y, 0.0F).setColor(color);
      begin.addVertex(pose, x, y + f - f3, 0.0F).setColor(color);
      begin.addVertex(pose, x + f * f2, y + f, 0.0F).setColor(color);
      begin.addVertex(pose, x, y, 0.0F).setColor(color);
      if (flag) {
         color = method7197(new Color(color), 0.6F).getRGB();
         begin.addVertex(pose, x - f * f2, y + f, 0.0F).setColor(color);
         begin.addVertex(pose, x + f * f2, y + f, 0.0F).setColor(color);
         begin.addVertex(pose, x, y + f - f3, 0.0F).setColor(color);
         begin.addVertex(pose, x - f * f2, y + f, 0.0F).setColor(color);
      }

      ImmediateRenderer.method8015(begin.buildOrThrow());
      method7187();
      poseStack.popPose();
   }

   public static void method7187() {
      RenderState.method1873();
      RenderState.method1872();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static float method7188(float f, float f2, float f3) {
      boolean flag = f > f2;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      } else if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      float f4 = Math.max(f, f2) - Math.min(f, f2);
      float f5 = f4 * f3;
      return f2 + (flag ? f5 : -f5);
   }

   public static Color method7189(Color color, int n) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Mth.clamp(n, 0, 255));
   }

   public static Color method7190(Color color, Color color2, double d, double d2) {
      int n = (int)((System.currentTimeMillis() / d + d2) % 360.0);
      n = (n >= 180 ? 360 - n : n) * 2;
      return method7200(color, color2, n / 360.0F);
   }

   public static Color method7191(boolean flag, int n) {
      float f = flag ? 3500.0F : 3000.0F;
      float f2 = (float)(System.currentTimeMillis() % (int)f + n);
      if (f2 > f) {
         f2 -= f;
      }

      f2 /= f;
      if (f2 > 0.5F) {
         f2 = 0.5F - (f2 - 0.5F);
      }

      f2 += 0.5F;
      return Color.getHSBColor(f2, 0.4F, 1.0F);
   }

   public static Color method7192(int n, float f, float f2) {
      double d = Math.ceil((float)(System.currentTimeMillis() + n) / 16.0F);
      d %= 360.0;
      return Color.getHSBColor((float)(d / 360.0), f, f2);
   }

   public static Color method7193(int n, int n2) {
      int n3 = (int)((System.currentTimeMillis() / n + n2) % 360L);
      int n4;
      return Color.getHSBColor((float)((n4 = n3 % 360) / 360.0) < 0.5 ? -((float)(n4 / 360.0)) : (float)(n4 / 360.0), 0.5F, 1.0F);
   }

   public static Color method7194(Color color) {
      float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
      float f = 0.84F;
      float hue = floats[0] - f;
      return new Color(Color.HSBtoRGB(hue, floats[1], floats[2]));
   }

   public static Color method7195(Color color, float f) {
      f = Math.min(1.0F, Math.max(0.0F, f));
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f));
   }

   public static int method7196(int rgb, float f) {
      f = Math.min(1.0F, Math.max(0.0F, f));
      Color color = new Color(rgb);
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f)).getRGB();
   }

   public static Color method7197(Color color, float f) {
      return new Color(
         Math.max((int)(color.getRed() * f), 0), Math.max((int)(color.getGreen() * f), 0), Math.max((int)(color.getBlue() * f), 0), color.getAlpha()
      );
   }

   public static Color method7198(int n, int n2, float saturation, float brightness, float f) {
      int n3 = (int)((System.currentTimeMillis() / n + n2) % 360L);
      float hue = n3 / 360.0F;
      Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(f * 255.0F))));
   }

   public static Color method7199(int n, int n2, Color color, Color color2, boolean flag) {
      int n3 = (int)((System.currentTimeMillis() / n + n2) % 360L);
      n3 = (n3 >= 180 ? 360 - n3 : n3) * 2;
      return flag ? method7201(color, color2, n3 / 360.0F) : method7200(color, color2, n3 / 360.0F);
   }

   public static Color method7200(Color color, Color color2, float f) {
      f = Math.min(1.0F, Math.max(0.0F, f));
      return new Color(
         method7204(color.getRed(), color2.getRed(), f),
         method7204(color.getGreen(), color2.getGreen(), f),
         method7204(color.getBlue(), color2.getBlue(), f),
         method7204(color.getAlpha(), color2.getAlpha(), f)
      );
   }

   public static Color method7201(Color color, Color color2, float f) {
      f = Math.min(1.0F, Math.max(0.0F, f));
      float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
      float[] floats2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
      Color hsbColor = Color.getHSBColor(method7203(floats[0], floats2[0], f), method7203(floats[1], floats2[1], f), method7203(floats[2], floats2[2], f));
      return new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), method7204(color.getAlpha(), color2.getAlpha(), f));
   }

   public static double method7202(double alpha, double d, double d2) {
      return alpha + (d - alpha) * d2;
   }

   public static float method7203(float alpha, float f, double d) {
      return (float)method7202(alpha, f, (float)d);
   }

   public static int method7204(int alpha, int n, double d) {
      return (int)method7202(alpha, n, (float)d);
   }

   public static BufferBuilder method7205(PoseStack poseStack, float x, float y, float f, float f2) {
      method7183();
      Matrix4f pose = poseStack.last().pose();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION);
      method7206(begin, pose, x, y, x + f, y + f2);
      return begin;
   }

   public static void method7206(BufferBuilder begin, Matrix4f pose, float x, float y, float f, float y2) {
      begin.addVertex(pose, x, y, 0.0F);
      begin.addVertex(pose, x, y2, 0.0F);
      begin.addVertex(pose, f, y2, 0.0F);
      begin.addVertex(pose, f, y, 0.0F);
   }

   public static boolean method7207(Color color) {
      return method7208(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
   }

   public static boolean method7208(float f, float f2, float f3) {
      return method7209(f, f2, f3, 0.0F, 0.0F, 0.0F) < method7209(f, f2, f3, 1.0F, 1.0F, 1.0F);
   }

   public static float method7209(float f, float f2, float f3, float f4, float f5, float f6) {
      float f7 = f4 - f;
      float f8 = f5 - f2;
      float f9 = f6 - f3;
      return (float)Math.sqrt(f7 * f7 + f8 * f8 + f9 * f9);
   }

   @NotNull
   public static Color method7210(@NotNull Color color, @NotNull Color color2, float f, boolean flag) {
      if (!flag) {
         return f >= 0.95 ? color2 : color;
      } else {
         int n = color2.getRed() - color.getRed();
         int n2 = color2.getGreen() - color.getGreen();
         int n3 = color2.getBlue() - color.getBlue();
         int n4 = color2.getAlpha() - color.getAlpha();
         return new Color(
            method7211(color.getRed() + (int)(n * f)),
            method7211(color.getGreen() + (int)(n2 * f)),
            method7211(color.getBlue() + (int)(n3 * f)),
            method7211(color.getAlpha() + (int)(n4 * f))
         );
      }
   }

   private static int method7211(int n) {
      return n > 255 ? 255 : Math.max(n, 0);
   }

   public static void method7212(BufferBuilder begin) {
      MsdfRenderer.flushBatch();
      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }
   }

   @Generated
   private ShapeRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;
      private final int intValue7;

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

      public int intValue5() {
         return this.intValue5;
      }

      public int intValue6() {
         return this.intValue6;
      }

      public int intValue7() {
         return this.intValue7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.intValue5, other.intValue5) && java.util.Objects.equals(this.intValue6, other.intValue6) && java.util.Objects.equals(this.intValue7, other.intValue7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, intValue5, intValue6, intValue7);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "intValue5=" + this.intValue5 + ", " + "intValue6=" + this.intValue6 + ", " + "intValue7=" + this.intValue7 + "]";
      }

      public Data(int intValue, int n, int px, int n2, int n3, int n4, int n5) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = px;
         this.intValue4 = n2;
         this.intValue5 = n3;
         this.intValue6 = n4;
         this.intValue7 = n5;
      }

      public int width() {
         return this.intValue;
      }

      public int height() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public int getIntValue4() {
         return this.intValue4;
      }

      public int getIntValue5() {
         return this.intValue5;
      }

      public int getIntValue6() {
         return this.intValue6;
      }

      public int getIntValue7() {
         return this.intValue7;
      }
   }

   public static final class Data2 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final int intValue;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(float floatValue, float f, float f2, float f3, int intValue) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.intValue = intValue;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float width() {
         return this.floatValue3;
      }

      public float height() {
         return this.floatValue4;
      }

      public int color() {
         return this.intValue;
      }
   }

   public static final class Data3 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
      }

      @Override
      public String toString() {
         return "Data3[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + "]";
      }

      public Data3(float floatValue, float f, float f2, float f3) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
      }

      public boolean contains(double d, double d2) {
         return d >= this.floatValue && d <= this.floatValue3 && d2 >= this.floatValue2 && d2 <= this.floatValue4;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }
   }

   public static class Entry {
      public final int intValue;
      private int intValue2 = 0;

      public Entry(int intValue) {
         this.intValue = intValue;
      }

      public void reset() {
         this.intValue2 = 0;
      }

      public boolean method7213() {
         return ++this.intValue2 > 4;
      }
   }

   public static class Entry2 {
      public final TextureId textureId;
      private int intValue = 0;

      public Entry2(NativeImage image) {
         this.textureId = new TextureId("shadow_" + RandomStringUtils.randomAlphanumeric(8));

         try {
            DynamicTexture dynamicTexture = new DynamicTexture(this.textureId.getIdentifier()::toString, image);
            MinecraftAccess.mc.getTextureManager().register(this.textureId.getIdentifier(), dynamicTexture);
         } catch (Exception e) {
            image.close();
            e.printStackTrace();
         }
      }

      public Entry2(BufferedImage im) {
         this.textureId = new TextureId("shadow_" + RandomStringUtils.randomAlphanumeric(8));

         try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(im, "png", byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            ByteBuffer bytes = BufferUtils.createByteBuffer(byteArray.length).put(byteArray);
            bytes.flip();
            DynamicTexture dynamicTexture = new DynamicTexture(this.textureId.getIdentifier()::toString, NativeImage.read(bytes));
            MinecraftAccess.mc.getTextureManager().register(this.textureId.getIdentifier(), dynamicTexture);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      public void reset() {
         this.intValue = 0;
      }

      public boolean method7214() {
         return ++this.intValue > 300;
      }

      public void method7215() {
         MinecraftAccess.mc.getTextureManager().release(this.textureId.getIdentifier());
      }
   }
}

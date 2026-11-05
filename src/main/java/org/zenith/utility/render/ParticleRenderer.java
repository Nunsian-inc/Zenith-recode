package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.utility.misc.FireflyParticle;
import org.zenith.utility.misc.Handler7;
import org.zenith.utility.misc.ParticleType;
import org.zenith.utility.misc.SpaceStarParticle;

public class ParticleRenderer {
   private static final Minecraft mc = Minecraft.getInstance();
   private static double doubleValue;
   private static double doubleValue2;
   private static double doubleValue3;
   private static final Quaternionf quaternionf = new Quaternionf();
   private static final Quaternionf quaternionf2 = new Quaternionf();
   private static final PoseStack matrixStack = new PoseStack();
   private static final Map<String, List<Handler7>> items = new HashMap<>();

   public static void method1476(Camera camera) {
      Vec3 position = camera.position();
      doubleValue = position.x;
      doubleValue2 = position.y;
      doubleValue3 = position.z;
      float xRot = camera.xRot();
      float yRot = camera.yRot();
      quaternionf.identity();
      quaternionf.rotateX((float)Math.toRadians(xRot));
      quaternionf.rotateY((float)Math.toRadians(yRot + 180.0F));
      quaternionf2.identity();
      quaternionf2.rotateY((float)Math.toRadians(-yRot));
      quaternionf2.rotateX((float)Math.toRadians(xRot));
   }

   @SafeVarargs
   public static void method1477(PoseStack poseStack, float f, List<? extends Handler7>... handler7ses) {
      items.clear();
      int n = 0;

      for (List list : handler7ses) {
         if (list != null) {
            for (Object obj : (Iterable<?>)(Object)(list)) {
               Handler7 handler7 = (Handler7)obj;
               if (!handler7.method1555()) {
                  items.computeIfAbsent(handler7.method1559(), var0x -> new ArrayList<>()).add(handler7);
                  n++;
               }
            }
         }
      }

      if (n != 0) {
         method1484();
         Tesselator tesselator = Tesselator.getInstance();

         for (Entry entry : items.entrySet()) {
            Identifier identifier = ParticleType.method1705((String)entry.getKey());
            if (identifier != null) {
               RenderState.method1890(0, identifier);
               List<Handler7> handler7s = (List)entry.getValue();
               BufferBuilder begin = tesselator.begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
               boolean flag = false;

               for (Handler7 handler7 : handler7s) {
                  if (handler7.method1449(f) >= 0.001F) {
                     method1481(begin, handler7, f);
                     flag = true;
                  }
               }

               if (flag) {
                  ImmediateRenderer.method8015(begin.buildOrThrow());
               }

               handler7s.clear();
            }
         }

         items.clear();
         method1485();
      }
   }

   public static void method1478(PoseStack poseStack, List<FireflyParticle> fireflyParticles, float f, float f2) {
      if (!fireflyParticles.isEmpty()) {
         method1484();
         RenderState.method1890(0, ParticleType.Type.FIREFLY.getSupplier().get());
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder begin = tesselator.begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
         float f3 = f2 + 2.5F;
         boolean flag = false;

         for (FireflyParticle fireflyParticle : fireflyParticles) {
            Vec3 vec = fireflyParticle.method1557().lerp(fireflyParticle.method1556(), f);
            Vec3 vec3d3 = fireflyParticle.getVec3d3();
            double length = vec3d3.length();
            float f4 = 1.0F - (float)fireflyParticle.getIntValue() / fireflyParticle.getIntValue3();
            float f5 = Math.min(f4 / 0.15F, 1.0F);
            float f6 = f4 > 0.8F ? 1.0F - (f4 - 0.8F) / 0.2F : 1.0F;
            float f7 = f5 * f6;
            Vec3 vec2 = length > 0.001 ? vec3d3.normalize() : new Vec3(0.0, 1.0, 0.0);
            float f8 = (float)Math.min(length / 0.2, 1.0);
            float f9 = Math.max(f8 * f7, 0.1F);
            byte b = 80;

            for (int i = 0; i < b; i++) {
               float f10 = (float)i / b;
               if (!(f10 >= f9)) {
                  float f11 = f10 / f9;
                  float f12 = fireflyParticle.getSize() * (1.0F - f11 * 0.5F) * 2.0F * f7;
                  float f13 = (float)Math.pow(1.0F - f11, 0.4) * fireflyParticle.method1449(f) * f7 * 0.8F;
                  if (!(f13 < 0.01F)) {
                     Vec3 vec3 = vec.subtract(vec2.scale(f11 * f9 * f3));
                     float f14 = (float)Math.pow(1.0F - f11, 1.2) * f7;
                     float f15 = f12 * (1.8F + f14 * 0.8F);
                     float f16 = f13 * 0.5F * f14;
                     if (f16 > 0.01F) {
                        method1482(begin, vec3, f15, f16, fireflyParticle.method1558());
                        flag = true;
                     }

                     method1482(begin, vec3, f12, f13, fireflyParticle.method1558());
                     flag = true;
                  }
               }
            }
         }

         if (flag) {
            ImmediateRenderer.method8015(begin.buildOrThrow());
         }

         method1485();
      }
   }

   public static void method1479(PoseStack poseStack, List<SpaceStarParticle> spaceStarParticles, float f, float f2) {
      if (!spaceStarParticles.isEmpty()) {
         method1484();
         Tesselator tesselator = Tesselator.getInstance();
         RenderState.method1890(0, ParticleType.Type.SPACE_GLOW.getSupplier().get());
         BufferBuilder begin = tesselator.begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);

         for (SpaceStarParticle spaceStarParticle : spaceStarParticles) {
            if (!spaceStarParticle.method1555()) {
               Vec3 vec = spaceStarParticle.method1557().lerp(spaceStarParticle.method1556(), f);
               float f3 = spaceStarParticle.method1449(f);
               if (!(f3 < 0.005F)) {
                  method1483(begin, vec, spaceStarParticle.method3081(f), f3 * 0.22F, spaceStarParticle.method1558(), 0.0F);
                  method1483(begin, vec, spaceStarParticle.method3083(f), f3 * 0.48F, spaceStarParticle.method1558(), 0.0F);
                  Vec3 vec3d3 = spaceStarParticle.getVec3d3();
                  double length = vec3d3.length();
                  if (!(length <= 5.0E-4)) {
                     Vec3 vec2 = vec3d3.normalize();
                     float f4 = spaceStarParticle.method3084(f2);
                     byte b = 16;

                     for (int i = 1; i <= b; i++) {
                        float f5 = (float)i / b;
                        float f6 = f3 * (float)Math.pow(1.0F - f5, 1.45F) * 0.42F;
                        if (!(f6 < 0.01F)) {
                           Vec3 vec3 = vec.subtract(vec2.scale(f5 * f4));
                           float f7 = spaceStarParticle.method3083(f) * (1.0F - f5 * 0.72F);
                           method1483(begin, vec3, f7, f6, spaceStarParticle.method1558(), 0.0F);
                        }
                     }
                  }
               }
            }
         }

         ShapeRenderer.method7212(begin);
         RenderState.method1890(0, ParticleType.Type.SPACE_STAR.getSupplier().get());
         BufferBuilder bufferBuilder = tesselator.begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);

         for (SpaceStarParticle spaceStarParticle : spaceStarParticles) {
            if (!spaceStarParticle.method1555()) {
               Vec3 vec = spaceStarParticle.method1557().lerp(spaceStarParticle.method1556(), f);
               float f3 = spaceStarParticle.method1449(f);
               if (!(f3 < 0.005F)) {
                  method1483(bufferBuilder, vec, spaceStarParticle.method3082(f), f3, spaceStarParticle.method1558().method7303(0.35F), spaceStarParticle.method1560());
               }
            }
         }

         ShapeRenderer.method7212(bufferBuilder);
         method1485();
      }
   }

   public static void method1480(PoseStack poseStack, Vec3 vec, Vec3 vec2, float f, float f2, ZenithColor zenithColor, float f3) {
      method1484();
      RenderState.method1890(0, ParticleType.Type.FIREFLY.getSupplier().get());
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder begin = tesselator.begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      double length = vec2.length();
      Vec3 vec3 = length > 0.001 ? vec2.normalize() : new Vec3(0.0, 0.0, -1.0);
      byte b = 60;
      boolean flag = false;

      for (int i = 0; i < b; i++) {
         float f4 = (float)i / b;
         float f5 = f2 * (1.0F - f4 * 0.7F) * 2.0F;
         float f6 = (float)Math.pow(1.0F - f4, 0.5) * 0.9F;
         if (!(f6 < 0.01F) && !(f5 < 0.01F)) {
            Vec3 vec4 = vec.subtract(vec3.scale(f4 * f));
            method1482(begin, vec4, f5 * 1.8F, f6 * 0.4F, zenithColor);
            method1482(begin, vec4, f5, f6, zenithColor);
            flag = true;
         }
      }

      if (flag) {
         ImmediateRenderer.method8015(begin.buildOrThrow());
      }

      method1485();
   }

   private static void method1481(BufferBuilder begin, Handler7 handler7, float f) {
      Vec3 vec = handler7.method1557().lerp(handler7.method1556(), f);
      float xo = (float)(vec.x - doubleValue);
      float yo = (float)(vec.y - doubleValue2);
      float zo = (float)(vec.z - doubleValue3);
      float f2 = handler7.method1449(f);
      if (!(f2 < 0.001F)) {
         float size = handler7.getSize();
         ZenithColor zenithColor = handler7.method1558();
         int color = (int)(f2 * zenithColor.getIntValue4()) << 24 | zenithColor.getIntValue() << 16 | zenithColor.getIntValue2() << 8 | zenithColor.getIntValue3();
         matrixStack.pushPose();
         matrixStack.rotate(quaternionf);
         matrixStack.translate(xo, yo, zo);
         matrixStack.rotate(quaternionf2);
         float angle = handler7.method1560();
         if (angle != 0.0F) {
            matrixStack.rotate(Axis.ZP.rotationDegrees(angle));
         }

         Matrix4f pose = matrixStack.last().pose();
         begin.addVertex(pose, 0.0F, -size, 0.0F).setUv(0.0F, 1.0F).setColor(color);
         begin.addVertex(pose, -size, -size, 0.0F).setUv(1.0F, 1.0F).setColor(color);
         begin.addVertex(pose, -size, 0.0F, 0.0F).setUv(1.0F, 0.0F).setColor(color);
         begin.addVertex(pose, 0.0F, 0.0F, 0.0F).setUv(0.0F, 0.0F).setColor(color);
         matrixStack.popPose();
      }
   }

   private static void method1482(BufferBuilder begin, Vec3 vec, float f, float f2, ZenithColor zenithColor) {
      method1483(begin, vec, f, f2, zenithColor, 0.0F);
   }

   private static void method1483(BufferBuilder begin, Vec3 vec, float f, float f2, ZenithColor zenithColor, float angle) {
      if (!(f2 < 0.001F)) {
         float xo = (float)(vec.x - doubleValue);
         float yo = (float)(vec.y - doubleValue2);
         float zo = (float)(vec.z - doubleValue3);
         int color = (int)(f2 * zenithColor.getIntValue4()) << 24 | zenithColor.getIntValue() << 16 | zenithColor.getIntValue2() << 8 | zenithColor.getIntValue3();
         float x = f / 2.0F;
         matrixStack.pushPose();
         matrixStack.rotate(quaternionf);
         matrixStack.translate(xo, yo, zo);
         matrixStack.rotate(quaternionf2);
         if (angle != 0.0F) {
            matrixStack.rotate(Axis.ZP.rotationDegrees(angle));
         }

         Matrix4f pose = matrixStack.last().pose();
         begin.addVertex(pose, x, -x, 0.0F).setUv(0.0F, 1.0F).setColor(color);
         begin.addVertex(pose, -x, -x, 0.0F).setUv(1.0F, 1.0F).setColor(color);
         begin.addVertex(pose, -x, x, 0.0F).setUv(1.0F, 0.0F).setColor(color);
         begin.addVertex(pose, x, x, 0.0F).setUv(0.0F, 0.0F).setColor(color);
         matrixStack.popPose();
      }
   }

   private static void method1484() {
      RenderState.method1871();
      RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE);
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1898("core/position_tex_color");
   }

   private static void method1485() {
      RenderState.method1882(true);
      RenderState.method1872();
      RenderState.method1873();
   }
}

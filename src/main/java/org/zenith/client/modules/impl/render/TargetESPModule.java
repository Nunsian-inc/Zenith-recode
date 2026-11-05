package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.TargetEspTextures;
import org.zenith.utility.render.TextureFilterUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "TargetESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.targetesp.description"
)
public class TargetESPModule extends Module {
   public static final TargetESPModule INSTANCE = new TargetESPModule();
   private static final Identifier[] identifiers = method2147("vortex");
   private static final Identifier[] identifiers2 = method2147("brackets");
   private static final int CONST_intValue = 3;
   private static final float CONST_floatValue = 22.0F;
   private static final float CONST_floatValue2 = (float) (Math.PI / 180.0);
   private static final long CONST_longValue = 250L;
   private static final float CONST_floatValue3 = 220.0F;
   private static final long CONST_longValue2 = 100L;
   private final ModeSetting mode = new ModeSetting("module.targetESP.mode", "module.targetESP.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.targetESP.texture.vortex").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.targetESP.texture.brackets");
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.mode, "module.targetESP.hearts");
   private final ModeSetting.Entry entry4 = new ModeSetting.Entry(this.mode, "module.targetESP.atom");
   private final ModeSetting.Entry entry5 = new ModeSetting.Entry(this.mode, "module.targetESP.helix");
   private final ModeSetting.Entry entry6 = new ModeSetting.Entry(this.mode, "module.targetESP.saturn");
   private final ModeSetting.Entry entry7 = new ModeSetting.Entry(this.mode, "module.targetESP.stars");
   private final SliderSetting size = new SliderSetting("module.targetESP.size", 1.2F, 0.3F, 3.0F, 0.05F, "module.targetESP.size.desc", "x");
   private final SliderSetting speed = new SliderSetting("module.targetESP.speed", 1.0F, 0.0F, 4.0F, 0.05F, "module.targetESP.speed.desc", "x");
   private final BooleanSetting red = new BooleanSetting("module.targetESP.red", "module.targetESP.red.description", true);
   private final Animation animation = new Animation(250L, Easing.INSTANCE13);
   private final Random random = new Random();
   private final float[] floatValues = new float[3];
   private final float[] floatValues2 = new float[3];
   private final float[] floatValues3 = new float[3];
   private final float[] floatValues4 = new float[3];
   private final float[] floatValues5 = new float[3];
   private final float[] floatValues6 = new float[3];
   private final int[] intValues = new int[3];
   private final int[] intValues2 = new int[3];
   private final Matrix4f matrix4f = new Matrix4f();
   private final TargetEspTextures targetEspTextures = new TargetEspTextures();
   private final int[] intValues3 = new int[4];
   private float floatValue;
   private long longValue;
   private LivingEntity livingEntity;
   private long longValue2;
   private boolean flag;
   private long longValue3;
   private boolean flag2;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private float floatValue2;
   private float floatValue3;
   private long longValue4;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private float floatValue4;
   private float floatValue5;

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.TARGET;
   }

   private TargetESPModule() {
      this.method2163();
   }

   private static Identifier[] method2147(String str) {
      Identifier[] ids = new Identifier[3];
      int[] ints = new int[]{768, 384, 192};

      for (int i = 0; i < ints.length; i++) {
         ids[i] = Zenith.method1501("visuals/targetesp/" + str + "_" + ints[i] + ".png");
         TextureFilterUtils.smooth(ids[i]);
      }

      return ids;
   }

   @EventTarget
   public void method2148(WorldRenderEvent event) {
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358() == null ? AimAssistModule.INSTANCE.method5156() : AuraModule.INSTANCE.method6358();
      if (livingEntity != null && (!livingEntity.isAlive() || livingEntity.isRemoved())) {
         livingEntity = null;
      }

      this.method2162(livingEntity);
      this.method2157();
      if (mc.player != null && mc.level != null && this.livingEntity != null && !(this.animation.getValue() <= 0.01F)) {
         this.method2149(event);
      }
   }

   private void method2149(WorldRenderEvent event) {
      Camera camera = mc.getEntityRenderDispatcher().camera;
      Vec3 position = camera.position();
      Vec3 vec = MathUtils.method2656(this.livingEntity);
      AABB boundingBox = this.livingEntity.getBoundingBox();
      float f = (float)Math.max(boundingBox.getXsize(), boundingBox.getZsize());
      float ysize = (float)boundingBox.getYsize();
      double x = vec.x;
      double y = vec.y;
      double z = vec.z;
      float f2 = f;
      float ysize2 = ysize;
      if (this.longValue4 != 0L) {
         float f3 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.longValue4) / 220.0F);
         float alpha1 = 1.0F - (1.0F - f3) * (1.0F - f3) * (1.0F - f3);
         x = Mth.lerp((double)alpha1, this.doubleValue4, vec.x);
         y = Mth.lerp((double)alpha1, this.doubleValue5, vec.y);
         z = Mth.lerp((double)alpha1, this.doubleValue6, vec.z);
         f2 = Mth.lerp(alpha1, this.floatValue4, f);
         ysize2 = Mth.lerp(alpha1, this.floatValue5, ysize);
         if (f3 >= 1.0F) {
            this.longValue4 = 0L;
         }
      }

      this.flag2 = true;
      this.doubleValue = x;
      this.doubleValue2 = y;
      this.doubleValue3 = z;
      this.floatValue2 = f2;
      this.floatValue3 = ysize2;
      double d = x - position.x;
      double d2 = y - position.y;
      double z2 = z - position.z;
      float f3 = this.livingEntity.hurtTime > 0 ? (this.livingEntity.hurtTime - GlowUtil.getTickDelta()) / 10.0F : 0.0F;
      float f4 = method2155(Mth.clamp(f3, 0.0F, 1.0F));
      float hover = this.red.isEnabled() ? f4 : 0.0F;
      double d3 = Math.sqrt(d * d + (d2 + ysize2 * 0.5) * (d2 + ysize2 * 0.5) + z2 * z2);
      double d4 = 2.0 * Math.tan(Math.toRadians(mc.options.fov().get().intValue()) / 2.0);
      float xScale = (float)(mc.getWindow().getHeight() / d4 / Math.max(0.5, d3));
      Matrix4f pose = event.getMatrixStack().last().pose();
      float f5 = camera.yRot() * (float) (Math.PI / 180.0);
      float ang = camera.xRot() * (float) (Math.PI / 180.0);
      boolean flag = this.method2161();
      float alpha = this.animation.getValue();
      int n = this.method2156();
      if (n >= 0) {
         this.targetEspTextures
            .method5054(
               n,
               pose,
               d,
               d2,
               z2,
               f2,
               ysize2,
               this.floatValue,
               this.method2154(hover),
               alpha,
               f4,
               this.size.getValue3() / 1.2F,
               f5,
               ang,
               flag,
               xScale
            );
      } else {
         ZenithColor clientColor = Zenith.getInstance().getStyleManager().getClientColor(90);
         if (hover > 0.0F) {
            clientColor = clientColor.method7295(ZenithColor.INSTANCE4, hover);
         }

         float f6 = f2 * 5.0F * this.size.getValue3() * method2150(alpha, f4);
         this.method2158(this.floatValue, ColorUtils.method6439(clientColor.method7287(), alpha));
         RenderState.method1871();
         if (flag) {
            RenderState.method1878();
         } else {
            RenderState.method1879();
         }

         RenderState.method1882(false);
         RenderState.method1877();
         this.method2153(pose, d, d2, z2, f6 * 0.5F, method2151(ysize2), this.method2152(f6 * xScale));
         RenderState.method1882(true);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private static float method2150(float alpha, float f) {
      float f2 = 1.0F - (1.0F - alpha) * (1.0F - alpha) * (1.0F - alpha);
      return (0.85F + 0.15F * f2) * (1.0F + 0.08F * f);
   }

   private static float method2151(float f) {
      return f * 0.62F - 0.006F;
   }

   private Identifier method2152(float f) {
      Identifier[] ids = this.entry2.isSelected() ? identifiers2 : identifiers;
      if (f > 420.0F) {
         return ids[0];
      } else {
         return f > 200.0F ? ids[1] : ids[2];
      }
   }

   private void method2153(Matrix4f pose, double x, double d, double z, float f, float f2, Identifier identifier) {
      RenderState.method1898("core/position_tex_color");
      RenderState.method1890(0, identifier);
      RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ZERO, BlendFactor.ONE);
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      float f3 = f2;

      for (int i = 0; i < 3; f3 += 0.006F) {
         this.method2159(pose, begin, x, d, z, f, f3, this.floatValues5[i], this.floatValues6[i], this.intValues[i]);
         i++;
      }

      ImmediateRenderer.method8015(begin.buildOrThrow());
      int n = 0;

      for (int i = 0; i < 3; i++) {
         n = Math.max(n, this.intValues2[i] >>> 24);
      }

      if (n > 0) {
         RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
         begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         f3 = f2;

         for (int i = 0; i < 3; f3 += 0.006F) {
            if (this.intValues2[i] >>> 24 != 0) {
               this.method2159(pose, begin, x, d, z, f, f3, this.floatValues5[i], this.floatValues6[i], this.intValues2[i]);
            }

            i++;
         }

         ImmediateRenderer.method8015(begin.buildOrThrow());
      }
   }

   private int[] method2154(float hover) {
      for (int i = 0; i < this.intValues3.length; i++) {
         ZenithColor clientColor = Zenith.getInstance().getStyleManager().getClientColor(i * 90);
         if (hover > 0.0F) {
            clientColor = clientColor.method7295(ZenithColor.INSTANCE4, hover);
         }

         this.intValues3[i] = clientColor.method7287();
      }

      return this.intValues3;
   }

   private static float method2155(float f) {
      return f * f * (3.0F - 2.0F * f);
   }

   private int method2156() {
      if (this.entry3.isSelected()) {
         return 0;
      } else if (this.entry4.isSelected()) {
         return 1;
      } else if (this.entry5.isSelected()) {
         return 2;
      } else if (this.entry6.isSelected()) {
         return 3;
      } else {
         return this.entry7.isSelected() ? 4 : -1;
      }
   }

   private void method2157() {
      long nanoTime = System.nanoTime();
      if (this.longValue != 0L) {
         float f = Math.min(0.1F, (float)(nanoTime - this.longValue) / 1.0E9F);
         this.floatValue = this.floatValue + f * this.speed.getValue3();
      }

      this.longValue = nanoTime;
   }

   private void method2158(float floatValue, int color) {
      for (int i = 0; i < 3; i++) {
         float alpha1 = (Mth.sin(floatValue * 2.6F + i * (float) (Math.PI * 2.0 / 3.0)) + 1.0F) * 0.5F;
         float alpha = Mth.lerp(alpha1, 0.4F, 1.0F);
         this.intValues[i] = ColorUtils.method6439(color, alpha);
         this.intValues2[i] = ColorUtils.method6439(color, alpha * 0.8F);
         this.floatValues5[i] = -floatValue * 120.0F * this.floatValues4[i] + this.floatValues[i];
         this.floatValues6[i] = Mth.sin(floatValue * this.floatValues3[i] * 0.8F + this.floatValues2[i]) * 22.0F;
      }
   }

   private void method2159(
      Matrix4f pose, BufferBuilder begin, double x, double d, double z, float f, float f2, float f3, float f4, int intValue
   ) {
      this.matrix4f
         .set(pose)
         .translate((float)x, (float)(d + f2), (float)z)
         .rotateY((90.0F + f3) * (float) (Math.PI / 180.0))
         .rotateX(f4 * (float) (Math.PI / 180.0));
      begin.addVertex(this.matrix4f, -f, 0.0F, -f).setUv(0.0F, 0.0F).setColor(intValue);
      begin.addVertex(this.matrix4f, -f, 0.0F, f).setUv(0.0F, 1.0F).setColor(intValue);
      begin.addVertex(this.matrix4f, f, 0.0F, f).setUv(1.0F, 1.0F).setColor(intValue);
      begin.addVertex(this.matrix4f, f, 0.0F, -f).setUv(1.0F, 0.0F).setColor(intValue);
   }

   public void method2160(MenuDrawContext customDrawContext, float xo, float yo, float xScale, float previewAlpha, float angle, float f) {
      this.method2157();
      float alpha = Mth.clamp(previewAlpha, 0.0F, 1.0F);
      float f2 = method2155(Mth.clamp(1.0F - (float)(System.currentTimeMillis() % 3200L) / 500.0F, 0.0F, 1.0F));
      float angle2 = 22.0F + f;
      PoseStack poseStack = MatrixUtils.method666(customDrawContext.pose());
      poseStack.translate(xo, yo, 0.0F);
      poseStack.scale(xScale, -xScale, xScale);
      poseStack.rotate(Axis.XP.rotationDegrees(angle2));
      poseStack.rotate(Axis.YP.rotationDegrees(angle));
      Matrix4f pose = poseStack.last().pose();
      float f3 = angle * (float) (Math.PI / 180.0);
      float ang = -angle2 * (float) (Math.PI / 180.0);
      int n = this.method2156();
      if (n >= 0) {
         this.targetEspTextures
            .method5054(
               n,
               pose,
               0.0,
               0.0,
               0.0,
               0.6F,
               1.8F,
               this.floatValue,
               this.method2154(0.0F),
               alpha,
               f2,
               this.size.getValue3() / 1.2F,
               f3,
               ang,
               false,
               xScale
            );
      } else {
         float f4 = 3.0F * this.size.getValue3() * method2150(alpha, f2);
         this.method2158(this.floatValue, ColorUtils.method6439(Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), alpha));
         RenderState.method1871();
         RenderState.method1879();
         RenderState.method1882(false);
         RenderState.method1877();
         this.method2153(pose, 0.0, 0.0, 0.0, f4 * 0.5F, method2151(1.8F), this.method2152(f4 * xScale));
         RenderState.method1882(true);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private boolean method2161() {
      if (mc.player != null && this.livingEntity != null) {
         long now = System.currentTimeMillis();
         if (now - this.longValue2 >= 100L) {
            this.flag = mc.player.hasLineOfSight(this.livingEntity);
            this.longValue2 = now;
         }

         return this.flag;
      } else {
         return false;
      }
   }

   private void method2162(LivingEntity livingEntity) {
      long now = System.currentTimeMillis();
      if (livingEntity != null) {
         this.longValue3 = now;
      } else if (this.livingEntity != null && this.livingEntity.isAlive() && !this.livingEntity.isRemoved() && now - this.longValue3 < 250L) {
         livingEntity = this.livingEntity;
      }

      if (livingEntity == null) {
         this.animation.method2808(0.0F);
         if (this.animation.getValue() <= 0.01F) {
            this.livingEntity = null;
            this.flag2 = false;
            this.longValue4 = 0L;
            this.longValue2 = 0L;
         }
      } else {
         if (livingEntity != this.livingEntity) {
            if (this.livingEntity != null && this.flag2 && this.animation.getValue() > 0.05F) {
               this.longValue4 = now;
               this.doubleValue4 = this.doubleValue;
               this.doubleValue5 = this.doubleValue2;
               this.doubleValue6 = this.doubleValue3;
               this.floatValue4 = this.floatValue2;
               this.floatValue5 = this.floatValue3;
            } else {
               this.longValue4 = 0L;
               this.method2163();
            }

            this.livingEntity = livingEntity;
            this.longValue2 = 0L;
         }

         this.animation.method2808(1.0F);
      }
   }

   private void method2163() {
      for (int i = 0; i < 3; i++) {
         this.floatValues4[i] = 0.75F + this.random.nextFloat() * 0.5F;
         this.floatValues3[i] = 0.8F + this.random.nextFloat() * 0.8F;
         this.floatValues2[i] = this.random.nextFloat() * (float) (Math.PI * 2);
      }

      for (int i = 0; i < 64; i++) {
         for (int j = 0; j < 3; j++) {
            this.floatValues[j] = -30.0F + this.random.nextFloat() * 60.0F;
         }

         boolean flag = true;

         for (int j = 0; j < 3 && flag; j++) {
            for (int k = j + 1; k < 3 && flag; k++) {
               if (Math.abs(this.floatValues[j] - this.floatValues[k]) < 22.0F) {
                  flag = false;
               }
            }
         }

         if (flag) {
            return;
         }
      }

      this.floatValues[0] = -30.0F + this.random.nextFloat() * 4.0F;
      this.floatValues[1] = -2.0F + this.random.nextFloat() * 4.0F;
      this.floatValues[2] = 30.0F - this.random.nextFloat() * 4.0F;
   }
}

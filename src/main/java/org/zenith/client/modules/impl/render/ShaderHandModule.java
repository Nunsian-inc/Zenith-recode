package org.zenith.client.modules.impl.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.HumanoidArm;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.mixin.accessors.GameRendererAccessor;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ShaderProgram;
import org.zenith.utility.render.WardrobeRenderer;

@ModuleInfo(
   name = "ShaderHand",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.shaderhand.description"
)
public final class ShaderHandModule extends Module {
   public static final ShaderHandModule INSTANCE = new ShaderHandModule();
   private static final float CONST_floatValue = 0.72F;
   private static final float CONST_floatValue2 = 0.72F;
   private static final float CONST_floatValue3 = 0.28F;
   private static final float CONST_floatValue4 = 0.22F;
   private static final float CONST_floatValue5 = 5.0F;
   private static final float CONST_floatValue6 = 1.0F;
   private static final float CONST_floatValue7 = 2.15F;
   private static final float CONST_floatValue8 = 200.0F;
   private static final float CONST_floatValue9 = 1.0F;
   private final ModeSetting shaderMode = new ModeSetting(
      "module.shaderHand.shaderMode", "module.shaderHand.shaderMode.desc", ShaderHandModule.Type.method1265()
   );
   private final SliderSetting fireAlpha = new SliderSetting(
      "module.shaderHand.fireAlpha", 0.86F, 0.1F, 1.0F, 0.01F, "module.shaderHand.fireAlpha.desc", "", this::method1253, null
   );
   private final BooleanSetting fireUnderHand = new BooleanSetting(
      "module.shaderHand.fireUnderHand", "module.shaderHand.fireUnderHand.desc", false, this::method1253
   );
   private final ModeSetting colorMode = new ModeSetting(
      "module.shaderHand.colorMode",
      "module.shaderHand.colorMode.desc",
      this::method1262,
      "module.shaderHand.color.rainbow",
      "module.shaderHand.color.client",
      "module.shaderHand.color.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.shaderHand.customColor", "module.shaderHand.customColor.desc", ZenithColor.INSTANCE, this::method1263
   );
   private final BooleanSetting secondColor = new BooleanSetting("module.shaderHand.secondColor", "module.shaderHand.secondColor.desc", false, this::method1263);
   private final ColorSetting colorSetting = new ColorSetting(
      "module.shaderHand.customSecondColor",
      "module.shaderHand.customSecondColor.desc",
      new ZenithColor(170, 120, 255),
      () -> this.method1263() && this.secondColor.isEnabled()
   );
   private long lastFrameNanos = -1L;
   private float floatValue;
   private float floatValue2 = 0.72F;
   private boolean flag;
   private float floatValue3 = 0.22F;
   private boolean flag2;
   private boolean flag3;
   private TextureTarget simpleFramebuffer;

   private ShaderHandModule() {
      this.colorMode.set("module.shaderHand.color.client");
   }

   public void method1238(Runnable runnable) {
      this.method1239(runnable, 0.0F);
   }

   public boolean isFlag23() {
      return this.flag2;
   }

   public void method1239(Runnable runnable, float cameraEntityPartialTicks) {
      if (!HandShaders.isInitialized()) {
         runnable.run();
      } else {
         WardrobeRenderer.method6693();
         if (this.method1253()) {
            runnable.run();
         } else {
            try {
               this.method1240(runnable);
            } catch (Exception e) {
               if (!this.flag) {
                  this.flag = true;
                  e.printStackTrace();
               }
            } finally {
               WardrobeRenderer.method6693();
               this.method1244();
            }
         }
      }
   }

   private void method1240(Runnable runnable) {
      RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
      TextureTarget simpleFramebuffer = HandShaders.getSimpleFramebuffer();
      ShaderHandModule.Type type = this.method1252();
      ShaderProgram shaderProgram = HandShaders.method6279(type.text2);
      if (mainRenderTarget != null && shaderProgram != null && simpleFramebuffer != null) {
         mc.gameRenderer.mainRenderTarget = simpleFramebuffer;
         this.flag2 = true;

         try {
            BlitUtils.method6456(simpleFramebuffer, 0);
            // 26.3 renderItemInHand prepares and draws its own feature frame into mainRenderTarget
            runnable.run();
         } finally {
            this.flag2 = false;
            mc.gameRenderer.mainRenderTarget = mainRenderTarget;
         }

         TextureTarget textureTarget = this.method1242(simpleFramebuffer, mainRenderTarget);
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1879();
         RenderState.method1889(0, simpleFramebuffer.getColorTextureView());
         RenderState.method1889(1, simpleFramebuffer.getDepthTextureView());

         try {
            shaderProgram.method5517();
            this.method1245(shaderProgram, simpleFramebuffer, type);
            HandShaders.method6282();
            shaderProgram.unbind();
         } finally {
            shaderProgram.unbind();
            RenderState.method1893(0);
            RenderState.method1893(1);
            RenderState.method1873();
            RenderState.method1872();
            RenderState.method1878();
            ImmediateRenderer.method8013(null);
         }

         if (textureTarget != null) {
            this.method1243(textureTarget);
         }
      } else {
         runnable.run();
      }
   }

   public boolean method1241() {
      if (!this.flag2) {
         return false;
      } else {
         this.flag3 = true;
         return true;
      }
   }

   private TextureTarget method1242(TextureTarget simpleFramebuffer, RenderTarget mainRenderTarget) {
      if (!WardrobeRenderer.method6692()) {
         return null;
      } else {
         if (this.simpleFramebuffer == null
            || this.simpleFramebuffer.width != simpleFramebuffer.width
            || this.simpleFramebuffer.height != simpleFramebuffer.height) {
            if (this.simpleFramebuffer != null) {
               this.simpleFramebuffer.destroyBuffers();
            }

            this.simpleFramebuffer = BlitUtils.method6448("zenith hand accessories", simpleFramebuffer.width, simpleFramebuffer.height, true);
         }

         mc.gameRenderer.mainRenderTarget = this.simpleFramebuffer;

         try {
            BlitUtils.method6456(this.simpleFramebuffer, 0);
            if (this.simpleFramebuffer.getDepthTexture() != null) {
               RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.simpleFramebuffer.getDepthTexture(), org.zenith.utility.render.RenderCompat.CLEAR_DEPTH);
            }
            WardrobeRenderer.method6694();
         } finally {
            mc.gameRenderer.mainRenderTarget = mainRenderTarget;
         }

         return this.simpleFramebuffer;
      }
   }

   private void method1243(TextureTarget textureTarget) {
      ShaderProgram shaderProgram = HandShaders.method6279("zenith_passthrough");
      if (shaderProgram != null) {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1879();
         RenderState.method1889(0, textureTarget.getColorTextureView());
         RenderState.method1889(1, textureTarget.getDepthTextureView());

         try {
            shaderProgram.method5517();
            shaderProgram.method5518("ColorTexture", 0);
            shaderProgram.method5518("DepthTexture", 1);
            HandShaders.method6282();
         } finally {
            shaderProgram.unbind();
            RenderState.method1893(0);
            RenderState.method1893(1);
            RenderState.method1873();
            RenderState.method1872();
            RenderState.method1878();
            ImmediateRenderer.method8013(null);
         }
      }
   }

   private void method1244() {
      if (this.flag3) {
         this.flag3 = false;
         if (mc.player != null && mc.level != null) {
            RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
            if (mainRenderTarget != null) {
               float gameTimeDeltaPartialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
               net.minecraft.client.renderer.state.level.LevelRenderState levelRenderState = mc.gameRenderer.gameRenderState().levelRenderState;
               OutlineVertexProvider.method1542(
                  var1x -> ((GameRendererAccessor)(Object)mc.gameRenderer)
                     .zenith$overlayRenderer()
                     .submit(gameTimeDeltaPartialTick, var1x, levelRenderState.playerRenderState, levelRenderState.cameraRenderState, mc.gameRenderer.gameRenderState().guiRenderState.isHudHidden)
               );
            }
         }
      }
   }

   private void method1245(ShaderProgram shaderProgram, TextureTarget simpleFramebuffer, ShaderHandModule.Type type) {
      int width = Math.max(1, simpleFramebuffer.width);
      int height = Math.max(1, simpleFramebuffer.height);
      shaderProgram.method5518("ColorTexture", 0);
      shaderProgram.method5518("DepthTexture", 1);
      shaderProgram.method5521("resolution", width, height);
      shaderProgram.method5519("time", this.method1249() * type.floatValue);
      this.method1250();
      shaderProgram.method5521("handMotion", this.floatValue2, this.floatValue3);
      shaderProgram.method5521("handSway", 0.0F, 0.0F);
      shaderProgram.method5519("effectAlpha", 1.0F);
      int[] ints = this.method1247();
      method1246(shaderProgram, "PrimaryColor", ints[0]);
      method1246(shaderProgram, "SecondaryColor", ints[1]);
      shaderProgram.method5519("Brightness", 2.15F);
      shaderProgram.method5519("ScanDensity", 200.0F);
      shaderProgram.method5519("Glitch", 1.0F);
   }

   private static void method1246(ShaderProgram shaderProgram, String key, int intValue) {
      shaderProgram.method5524(key, ColorUtils.method6424(intValue), ColorUtils.method6425(intValue), ColorUtils.method6426(intValue));
   }

   private int[] method1247() {
      if (this.colorMode.isSelected(0)) {
         float f = (float)(System.currentTimeMillis() % 9000L) / 9000.0F;
         return new int[]{method1248(f), method1248(f + 0.1F)};
      } else if (this.colorMode.isSelected(1)) {
         return new int[]{
            Zenith.getInstance().getStyleManager().getClientColor(0).method7287(), Zenith.getInstance().getStyleManager().getClientColor(180).method7287()
         };
      } else {
         int n = this.customColor.method1445();
         return new int[]{n, this.secondColor.isEnabled() ? this.colorSetting.method1445() : n};
      }
   }

   private static int method1248(float f) {
      float hue = f - (float)Math.floor(f);
      return ZenithColor.method7297(hue, 0.72F, 1.0F).method7287();
   }

   private float method1249() {
      long nanoTime = System.nanoTime();
      if (this.lastFrameNanos < 0L) {
         this.lastFrameNanos = nanoTime;
         return this.floatValue;
      } else {
         float f = Math.min((float)(nanoTime - this.lastFrameNanos) / 1.0E9F, 0.1F);
         this.lastFrameNanos = nanoTime;
         this.floatValue = (this.floatValue + f * 5.0F) % 100000.0F;
         return this.floatValue;
      }
   }

   private void method1250() {
      this.floatValue2 = mc.player != null && mc.player.getMainArm() == HumanoidArm.LEFT ? 0.28F : 0.72F;
      this.floatValue3 = 0.22F;
   }

   private int method1251(TextureTarget textureTarget) {
      int n = Math.max(1, textureTarget.height);
      return Math.min(n, Math.max(1, (int)(n * 0.72F)));
   }

   private ShaderHandModule.Type method1252() {
      return this.shaderMode == null ? ShaderHandModule.Type.ICE : ShaderHandModule.Type.method1264(this.shaderMode.getIndex());
   }

   private boolean method1253() {
      return this.method1252() == ShaderHandModule.Type.FIRE;
   }

   public boolean method1254() {
      return this.isEnabled() && this.method1253();
   }

   public float method1255() {
      return this.fireAlpha.getValue3();
   }

   public boolean method1256() {
      return this.fireUnderHand.isEnabled();
   }

   public boolean method1257() {
      return this.colorMode.isSelected(0);
   }

   public boolean method1258() {
      return this.colorMode.isSelected(1);
   }

   public boolean method1259() {
      return this.colorMode.isSelected(2);
   }

   public int method1260() {
      return this.customColor.method1445();
   }

   public int method1261() {
      return this.secondColor.isEnabled() ? this.colorSetting.method1445() : this.customColor.method1445();
   }

   private boolean method1262() {
      ShaderHandModule.Type type = this.method1252();
      return type == ShaderHandModule.Type.HOLOGRAM || type == ShaderHandModule.Type.MATRIX || type == ShaderHandModule.Type.FIRE;
   }

   private boolean method1263() {
      return this.method1262() && this.colorMode.isSelected(2);
   }

   private static enum Type {
      ICE("module.shaderHand.shader.ice", "zenith_ice", 1.0F),
      HOLOGRAM("module.shaderHand.shader.hologram", "zenith_hologram", 1.0F),
      MATRIX("module.shaderHand.shader.matrix", "zenith_matrix", 1.0F),
      CHROME("module.shaderHand.shader.chrome", "zenith_chrome", 1.0F),
      FIRE("module.shaderHand.shader.fire", null, 1.0F);

      private final String text;
      private final String text2;
      private final float floatValue;

      private Type(String text, String str, float floatValue) {
         this.text = text;
         this.text2 = str;
         this.floatValue = floatValue;
      }

      private static ShaderHandModule.Type method1264(int index) {
         ShaderHandModule.Type[] types = values();
         return index >= 0 && index < types.length ? types[index] : ICE;
      }

      private static String[] method1265() {
         ShaderHandModule.Type[] types = values();
         String[] strings = new String[types.length];

         for (int i = 0; i < types.length; i++) {
            strings[i] = types[i].text;
         }

         return strings;
      }
   }
}

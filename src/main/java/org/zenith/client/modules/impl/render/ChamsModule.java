package org.zenith.client.modules.impl.render;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.CameraType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.combat.AntiBotModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.ChamsRenderLayer;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.LightUtil;
import org.zenith.utility.render.ShaderProgram;
import org.zenith.utility.render.UniformBinder;

@ModuleInfo(
   name = "Chams",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.chams.description"
)
public final class ChamsModule extends Module {
   public static final ChamsModule INSTANCE = new ChamsModule();
   private static final KawaseShaderKey kawaseShaderKey = new KawaseShaderKey(
      Zenith.method1501("core/chams_entity/data"), DefaultVertexFormat.ENTITY
   );
   private static RenderType renderLayer;
   private static final KawaseShaderKey kawaseShaderKey2 = new KawaseShaderKey(
      Zenith.method1501("core/chams_oil/data"), DefaultVertexFormat.ENTITY
   );
   private static final KawaseShaderKey kawaseShaderKey3 = new KawaseShaderKey(
      Zenith.method1501("core/chams_frost/data"), DefaultVertexFormat.ENTITY
   );
   private static final KawaseShaderKey kawaseShaderKey4 = new KawaseShaderKey(
      Zenith.method1501("core/chams_fracture/data"), DefaultVertexFormat.ENTITY
   );
   private static final KawaseShaderKey kawaseShaderKey5 = new KawaseShaderKey(
      Zenith.method1501("core/chams_liquid/data"), DefaultVertexFormat.ENTITY
   );
   private static RenderType renderLayer2;
   private static RenderType renderLayer3;
   private static RenderType renderLayer4;
   private static RenderType renderLayer5;
   private static final float CONST_floatValue = 1.0F;
   private static final float CONST_floatValue2 = 0.0F;
   private static final float CONST_floatValue3 = 1.8F;
   private static final float CONST_floatValue4 = 1.15F;
   private static final float CONST_floatValue5 = 3.4F;
   private static final float CONST_floatValue6 = 0.54F;
   private static final long CONST_longValue = 9000L;
   private static final float CONST_floatValue7 = 0.1F;
   private static final float CONST_floatValue8 = 0.72F;
   private static final int CONST_intValue = 2;
   private static final Object obj = new Object();
   private static final int[] CONST_intValues = new int[4];
   private static TextureTarget simpleFramebuffer;
   private static TextureTarget simpleFramebuffer2;
   private static TextureTarget simpleFramebuffer3;
   private static long longValue = Long.MIN_VALUE;
   private static ShaderProgram shaderProgram;
   private static ShaderProgram shaderProgram2;
   private static boolean flag;
   private static boolean flag2;
   private static final Set<KawaseShaderKey> items = ConcurrentHashMap.newKeySet();
   private final ModeSetting texture = new ModeSetting(
      "module.chams.texture",
      "module.chams.texture.desc",
      "module.chams.texture.glass",
      "module.chams.texture.oil",
      "module.chams.texture.frost",
      "module.chams.texture.fracture",
      "module.chams.texture.liquid"
   );
   private final SliderSetting blurStrength = new SliderSetting(
      "module.chams.blurStrength", 8.3F, 0.0F, 20.0F, 0.05F, "module.chams.blurStrength.desc", "", this::method5615, null
   );
   private final MultiSelectSetting targets = new MultiSelectSetting(
      "module.chams.targets",
      "module.chams.targets.desc",
      MultiSelectSetting.Entry.method5231("module.chams.players", true),
      MultiSelectSetting.Entry.method5231("module.chams.self", false),
      MultiSelectSetting.Entry.method5231("module.chams.friends", true)
   );
   private final ModeSetting colorMode = new ModeSetting(
      "module.chams.colorMode",
      "module.chams.colorMode.desc",
      this::method5614,
      "module.chams.color.rainbow",
      "module.chams.color.client",
      "module.chams.color.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.chams.customColor", "module.chams.customColor.desc", ZenithColor.INSTANCE, this::method5613
   );
   private final BooleanSetting secondColor = new BooleanSetting("module.chams.secondColor", "module.chams.secondColor.desc", false, this::method5613);
   private final ColorSetting colorSetting = new ColorSetting(
      "module.chams.customSecondColor",
      "module.chams.customSecondColor.desc",
      new ZenithColor(170, 120, 255),
      () -> this.method5613() && this.secondColor.isEnabled()
   );
   private final SliderSetting brightness = new SliderSetting("module.chams.brightness", 2.15F, 1.0F, 3.0F, 0.05F, "module.chams.brightness.desc", "");
   public static volatile boolean flag3;
   private static final long CONST_longValue2 = 100000L;

   private ChamsModule() {
   }

   @Override
   public void onDisable() {
      super.onDisable();
      synchronized (obj) {
         method5634(simpleFramebuffer);
         method5634(simpleFramebuffer2);
         method5634(simpleFramebuffer3);
         simpleFramebuffer = null;
         simpleFramebuffer2 = null;
         simpleFramebuffer3 = null;
         longValue = Long.MIN_VALUE;
      }

      method5625();
   }

   public void method5599() {
      this.method5617();
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.CHAMS;
   }

   public boolean method5600(LivingEntityRenderState entityRenderState) {
      if (!flag3) {
         if (this.isEnabled() && mc.player != null && mc.level != null && entityRenderState instanceof AvatarRenderState avatarRenderState) {
            return mc.level.getEntity(avatarRenderState.id) instanceof AbstractClientPlayer entity ? this.method5609(entity) : false;
         } else {
            return false;
         }
      } else {
         return this.isEnabled() && entityRenderState instanceof AvatarRenderState;
      }
   }

   @Nullable
   public RenderType method5601(Identifier textureLocation) {
      if (this.texture.isSelected(1)) {
         return renderLayer2 != null ? renderLayer2 : (renderLayer2 = method5616("zenith_chams_oil", kawaseShaderKey2));
      } else if (this.texture.isSelected(2)) {
         return renderLayer3 != null ? renderLayer3 : (renderLayer3 = method5616("zenith_chams_frost", kawaseShaderKey3));
      } else if (this.texture.isSelected(3)) {
         return renderLayer4 != null ? renderLayer4 : (renderLayer4 = method5616("zenith_chams_fracture", kawaseShaderKey4));
      } else if (this.texture.isSelected(4)) {
         return renderLayer5 != null ? renderLayer5 : (renderLayer5 = method5616("zenith_chams_liquid", kawaseShaderKey5));
      } else {
         if (renderLayer == null) {
            renderLayer = method5616("zenith_chams_glass", kawaseShaderKey);
         }

         return renderLayer;
      }
   }

   public boolean method5602() {
      return this.method5606() && this.method5608();
   }

   public int method5603(int color) {
      int n = Math.clamp((long)Math.round(255.0F), 0, 255);
      return n << 24 | color & 16777215;
   }

   public int method5604(int color) {
      int[] ints = this.method5629();
      int n = ints.length > 0 ? ints[0] : color;
      return this.method5603(n);
   }

   public static ChamsModule.Entry method5605() {
      return ChamsModule.Entry.method5635();
   }

   private boolean method5606() {
      KawaseShaderKey kawaseShaderKey2 = this.method5607();
      if (kawaseShaderKey2 == kawaseShaderKey && flag2) {
         return true;
      } else if (items.contains(kawaseShaderKey2)) {
         return true;
      } else {
         boolean flag = LightUtil.method6712(kawaseShaderKey2.getIdentifier());
         if (kawaseShaderKey2 == kawaseShaderKey) {
            flag2 = flag;
         } else if (flag) {
            items.add(kawaseShaderKey2);
         }

         return flag;
      }
   }

   private KawaseShaderKey method5607() {
      if (this.texture.isSelected(1)) {
         return kawaseShaderKey2;
      } else if (this.texture.isSelected(2)) {
         return kawaseShaderKey3;
      } else if (this.texture.isSelected(3)) {
         return kawaseShaderKey4;
      } else {
         return this.texture.isSelected(4) ? kawaseShaderKey5 : kawaseShaderKey;
      }
   }

   private boolean method5608() {
      synchronized (obj) {
         return longValue == Long.MIN_VALUE ? false : simpleFramebuffer != null && simpleFramebuffer.getColorTexture() != null;
      }
   }

   private boolean method5609(AbstractClientPlayer entity) {
      if (!entity.isAlive() || entity.isSpectator()) {
         return false;
      } else if (entity == mc.player) {
         return this.method5611() && mc.options.getCameraType() != CameraType.FIRST_PERSON;
      } else if (!this.method5610()) {
         return false;
      } else {
         return !this.method5612() && Zenith.getInstance().getFriends().method7626(entity)
            ? false
            : !AntiBotModule.INSTANCE.isEnabled() || !AntiBotModule.INSTANCE.method2785(entity);
      }
   }

   private boolean method5610() {
      return this.targets.method5227(0);
   }

   private boolean method5611() {
      return this.targets.method5227(1);
   }

   private boolean method5612() {
      return this.targets.method5227(2);
   }

   private boolean method5613() {
      return this.method5614() && this.colorMode.isSelected(2);
   }

   private boolean method5614() {
      return !this.method5615();
   }

   private boolean method5615() {
      return this.texture.isSelected(0);
   }

   @Nullable
   private static RenderType method5616(String name, KawaseShaderKey kawaseShaderKey2) {
      RenderState.Data data = RenderState.method1906();
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1877();
      RenderState.method1878();
      RenderState.method1882(true);
      RenderPipeline pipeline = kawaseShaderKey2.method5561(DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderState.method1904());
      RenderState.method1907(data);
      if (kawaseShaderKey2 == kawaseShaderKey) {
         flag2 = kawaseShaderKey2.text3 == null;
      }

      UniformBinder.method5480(pipeline, kawaseShaderKey2, () -> {
         int[] var1x = INSTANCE.method5629();
         method5631(kawaseShaderKey2, "iTime", INSTANCE.method5627());
         method5633(kawaseShaderKey2, "PrimaryColor", var1x[0]);
         method5633(kawaseShaderKey2, "SecondaryColor", var1x[1]);
         method5631(kawaseShaderKey2, "AlphaMul", 1.0F);
         method5631(kawaseShaderKey2, "FresnelPower", 1.8F);
         method5631(kawaseShaderKey2, "Freq", INSTANCE.method5628());
         method5631(kawaseShaderKey2, "ColorTransfer", kawaseShaderKey2 == kawaseShaderKey ? 0.0F : 0.75F);
         method5631(kawaseShaderKey2, "Brightness", INSTANCE.brightness.getValue3());
         method5631(kawaseShaderKey2, "MirrorEnabled", 1.0F);
         method5632(kawaseShaderKey2, mc.getWindow().getWidth(), mc.getWindow().getHeight());
      }, INSTANCE::method5618, INSTANCE::isEnabled);
      return ChamsRenderLayer.method3202(name, pipeline, null, true, true, true);
   }

   private void method5617() {
      if (mc.level != null && mc.getWindow() != null && mc.gameRenderer.mainRenderTarget() != null) {
         synchronized (obj) {
            long l = this.method5626();
            if (longValue != l) {
               ChamsModule.Entry entry = method5605();

               try {
                  int n = Math.max(1, mc.gameRenderer.mainRenderTarget().width);
                  int n2 = Math.max(1, mc.gameRenderer.mainRenderTarget().height);
                  this.method5619(Math.max(1, n / 2), Math.max(1, n2 / 2));
                  if (simpleFramebuffer != null) {
                     BlitUtils.method6453(mc.gameRenderer.mainRenderTarget(), simpleFramebuffer);
                     if (this.method5615()) {
                        this.method5621();
                     }

                     longValue = l;
                     return;
                  }
               } finally {
                  entry.restore();
               }
            }
         }
      }
   }

   private GpuTextureView method5618() {
      synchronized (obj) {
         return BlitUtils.method6450(simpleFramebuffer);
      }
   }

   private void method5619(int width, int height) {
      int n = Math.max(1, width / 2);
      int height2 = Math.max(1, height / 2);
      int width2 = Math.max(1, width / 4);
      int height3 = Math.max(1, height / 4);
      if (simpleFramebuffer == null
         || simpleFramebuffer2 == null
         || simpleFramebuffer3 == null
         || simpleFramebuffer.width != width
         || simpleFramebuffer.height != height
         || simpleFramebuffer2.width != n
         || simpleFramebuffer2.height != height2
         || simpleFramebuffer3.width != width2
         || simpleFramebuffer3.height != height3) {
         method5634(simpleFramebuffer);
         method5634(simpleFramebuffer2);
         method5634(simpleFramebuffer3);
         simpleFramebuffer = BlitUtils.method6448("zenith chams copy", width, height, false);
         simpleFramebuffer2 = BlitUtils.method6448("zenith chams blur 1", n, height2, false);
         simpleFramebuffer3 = BlitUtils.method6448("zenith chams blur 2", width2, height3, false);
         method5620(simpleFramebuffer);
         method5620(simpleFramebuffer2);
         method5620(simpleFramebuffer3);
         longValue = Long.MIN_VALUE;
      }
   }

   private static void method5620(TextureTarget simpleFramebuffer) {
   }

   private void method5621() {
      if (simpleFramebuffer != null && simpleFramebuffer2 != null && simpleFramebuffer3 != null && method5624()) {
         RenderState.method1872();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1882(false);
         ScissorState scissorStateForRenderTypeDraws = RenderSystem.getScissorStateForRenderTypeDraws();
         boolean enabled = scissorStateForRenderTypeDraws.enabled();
         int x = scissorStateForRenderTypeDraws.x();
         int y = scissorStateForRenderTypeDraws.y();
         int width = scissorStateForRenderTypeDraws.width();
         int height = scissorStateForRenderTypeDraws.height();
         RenderState.method1901();

         try {
            this.method5622();
         } finally {
            if (enabled) {
               RenderState.method1900(x, y, width, height);
            }
         }
      }
   }

   private void method5622() {
      float f = Math.max(0.0F, this.blurStrength.getValue3());
      float f2 = Math.max(0.05F, 3.4F) * (0.35F + f * 0.08F) * 0.54F;
      this.method5623(shaderProgram, simpleFramebuffer, simpleFramebuffer2, f2);
      this.method5623(shaderProgram, simpleFramebuffer2, simpleFramebuffer3, f2);
      this.method5623(shaderProgram2, simpleFramebuffer3, simpleFramebuffer2, f2);
      this.method5623(shaderProgram2, simpleFramebuffer2, simpleFramebuffer, f2);
   }

   private void method5623(ShaderProgram shaderProgram, TextureTarget simpleFramebuffer2, TextureTarget textureTarget, float f) {
      if (shaderProgram != null && simpleFramebuffer2 != null && textureTarget != null) {
         shaderProgram.method5517();
         shaderProgram.method5518("image", 0);
         shaderProgram.method5519("offset", f);
         shaderProgram.method5521("resolution", 1.0F / Math.max(1.0F, (float)textureTarget.width), 1.0F / Math.max(1.0F, (float)textureTarget.height));
         RenderState.method1889(0, BlitUtils.method6450(simpleFramebuffer2));
         ImmediateRenderer.method8013(textureTarget);
         HandShaders.method6282();
         RenderState.method1893(0);
         shaderProgram.unbind();
      }
   }

   private static boolean method5624() {
      if (shaderProgram != null && shaderProgram2 != null) {
         return true;
      } else if (flag) {
         return false;
      } else {
         try {
            shaderProgram = new ShaderProgram("chams_kawase", "kawase_down", "kawase");
            shaderProgram2 = new ShaderProgram("chams_kawase", "kawase_up", "kawase");
            return true;
         } catch (RuntimeException e) {
            method5625();
            flag = true;
            return false;
         }
      }
   }

   private static void method5625() {
      if (shaderProgram != null) {
         shaderProgram.method5528();
         shaderProgram = null;
      }

      if (shaderProgram2 != null) {
         shaderProgram2.method5528();
         shaderProgram2 = null;
      }

      flag = false;
   }

   private long method5626() {
      if (mc.level == null) {
         return Long.MIN_VALUE;
      } else {
         long gameTime = mc.level.getGameTime();
         int floatToIntBits = Float.floatToIntBits(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));
         return gameTime << 32 ^ floatToIntBits & 4294967295L;
      }
   }

   private float method5627() {
      if (mc.level == null) {
         return 0.0F;
      } else {
         float gameTimeDeltaPartialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
         double d = (double)(mc.level.getGameTime() % 100000L) + gameTimeDeltaPartialTick;
         return (float)(d * 0.05 * 1.15F);
      }
   }

   private float method5628() {
      return 0.004675F;
   }

   private int[] method5629() {
      if (this.colorMode.isSelected(0)) {
         float f = (float)(System.currentTimeMillis() % 9000L) / 9000.0F;
         return new int[]{method5630(f), method5630(f + 0.1F)};
      } else if (this.colorMode.isSelected(1)) {
         int n = Zenith.getInstance().getStyleManager().getClientColor(0).method7287();
         int n2 = Zenith.getInstance().getStyleManager().getClientColor(180).method7287();
         return new int[]{n, n2};
      } else {
         int n = this.customColor.method1445();
         int n2 = this.secondColor.isEnabled() ? this.colorSetting.method1445() : n;
         return new int[]{n, n2};
      }
   }

   private static int method5630(float f) {
      float hue = f - (float)Math.floor(f);
      return ZenithColor.method7297(hue, 0.72F, 1.0F).method7287();
   }

   private static void method5631(KawaseShaderKey kawaseShaderKey2, String key, float batchRange) {
      kawaseShaderKey2.method5166(key).method7412(batchRange);
   }

   private static void method5632(KawaseShaderKey kawaseShaderKey2, float width, float height) {
      kawaseShaderKey2.method5166("ScreenSize").method7413(width, height);
   }

   private static void method5633(KawaseShaderKey kawaseShaderKey2, String key, int intValue) {
      kawaseShaderKey2.method5166(key).method7414(ColorUtils.method6424(intValue), ColorUtils.method6425(intValue), ColorUtils.method6426(intValue));
   }

   private static void method5634(TextureTarget simpleFramebuffer) {
      if (simpleFramebuffer != null) {
         simpleFramebuffer.destroyBuffers();
      }
   }

   public static final class Entry {
      private final RenderState.Data data = RenderState.method1906();

      private static ChamsModule.Entry method5635() {
         return new ChamsModule.Entry();
      }

      public void restore() {
         RenderState.method1907(this.data);
      }
   }
}

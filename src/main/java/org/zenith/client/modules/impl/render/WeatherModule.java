package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Camera;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BackgroundRenderEvent;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.game.Print;
import org.zenith.utility.game.Util11;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.FlakeUtil;
import org.zenith.utility.misc.FoliageSprites;
import org.zenith.utility.misc.SnowVisibility;
import org.zenith.utility.misc.GroundMistRenderer;
import org.zenith.utility.misc.WeatherReflectionRenderer;
import org.zenith.utility.misc.WeatherTimeProfile;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.misc.WeatherPresets;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.RippleUtil;
import org.zenith.utility.render.SnowShader;
import org.zenith.utility.render.WeatherPreviewTerrainRenderer;
import org.zenith.utility.render.WeatherPreviewUtil;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Weather",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.worldweather.description"
)
public final class WeatherModule extends Module {
   public static final WeatherModule INSTANCE = new WeatherModule();
   private static final int CONST_intValue = 52;
   private static final long CONST_longValue = 400L;
   private static final long CONST_longValue2 = 118049640925L;
   private static final long CONST_longValue3 = System.currentTimeMillis();
   private static final float CONST_floatValue = 0.9F;
   private static final int CONST_intValue2 = 0;
   private static final int CONST_intValue3 = 1;
   private static final int CONST_intValue4 = 2;
   private final ModeSetting mode = new ModeSetting(
      "module.weather.mode", "module.weather.mode.desc", () -> true, "module.weather.mode.rain", "module.weather.mode.fog", "module.weather.mode.snow"
   );
   private static final int CONST_intValue5 = 0;
   private static final int CONST_intValue6 = 1;
   private static final int CONST_intValue7 = 2;
   private static final int CONST_intValue8 = 3;
   private final ModeSetting sky = new ModeSetting(
      "module.weather.sky",
      "module.weather.sky.desc",
      () -> true,
      "module.weather.sky.weather",
      "module.weather.sky.clear",
      "module.weather.sky.storm",
      "module.weather.sky.stars"
   );
   private final SliderSetting time = new SliderSetting("module.weather.time", 12.0F, 0.0F, 24.0F, 0.25F, "module.weather.time.desc", "", () -> true, null)
      .method2399(WeatherModule::method3095);
   private final ModeSetting water = new ModeSetting(
      "module.weather.water", "module.weather.water.desc", this::method3088, "module.weather.water.puddles", "module.weather.water.film"
   );
   private final SliderSetting drops = new SliderSetting(
      "module.weather.drops", 100.0F, 10.0F, 200.0F, 5.0F, "module.weather.drops.desc", "%", this::method3091, null
   );
   private final SliderSetting wind = new SliderSetting(
      "module.weather.wind", 100.0F, 0.0F, 250.0F, 10.0F, "module.weather.wind.desc", "%", this::method3091, null
   );
   private final BooleanSetting storm = new BooleanSetting("module.weather.storm", "module.weather.storm.desc", true, this::method3088);
   private final SliderSetting snowDepth = new SliderSetting(
      "module.weather.snowDepth", 100.0F, 20.0F, 250.0F, 5.0F, "module.weather.snowDepth.desc", "%", this::method3090, null
   );
   private final SliderSetting fogRange = new SliderSetting(
      "module.weather.fogRange", 40.0F, 12.0F, 160.0F, 4.0F, "module.weather.fogRange.desc", "b", this::method3089, null
   );
   private final SliderSetting mist = new SliderSetting(
      "module.weather.mist", 100.0F, 0.0F, 200.0F, 5.0F, "module.weather.mist.desc", "%", this::method3089, null
   );
   private final SliderSetting haze = new SliderSetting(
      "module.weather.haze", 100.0F, 0.0F, 200.0F, 5.0F, "module.weather.haze.desc", "%", this::method3089, null
   );
   private final SliderSetting reflect = new SliderSetting(
      "module.weather.reflect", 100.0F, 0.0F, 200.0F, 5.0F, "module.weather.reflect.desc", "%", () -> true, null
   );
   private WeatherFlakeChannel weatherFlakeChannel;
   private WeatherFlakeChannel weatherFlakeChannel2;
   private float floatValue = Float.NaN;
   private WeatherFlakeChannel.Data data;
   private WeatherPresets weatherPresets;
   private final Glowboost glowboost = new Glowboost();
   private final Glowboost glowboost2 = new Glowboost();
   private final Print print = new Print();
   private final PoseStack matrixStack = new PoseStack();
   private FlakeState flakeState;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private long longValue;
   private FlakeState flakeState2;
   private static final float CONST_floatValue2 = 4500.0F;
   private static final float CONST_floatValue3 = 14000.0F;
   private float floatValue2;
   private long longValue2 = System.currentTimeMillis();
   private static final float CONST_floatValue4 = 900.0F;
   private float floatValue3;
   private long longValue3 = System.currentTimeMillis();
   private boolean flag;

   private WeatherModule() {
      this.drops.setTitleKey(() -> this.method3090() ? "module.weather.flakes" : "module.weather.drops");
      this.drops.setTipKey(() -> this.method3090() ? "module.weather.flakes.desc" : "module.weather.drops.desc");
      this.wind.setTipKey(() -> this.method3090() ? "module.weather.wind.snow.desc" : "module.weather.wind.desc");
      this.reflect.setTitleKey(() -> this.method3090() ? "module.weather.sheen" : "module.weather.reflect");
      this.reflect.setTipKey(() -> this.method3090() ? "module.weather.sheen.desc" : "module.weather.reflect.desc");
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.WORLD_WEATHER;
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(
         this.mode, this.sky, this.time, this.water, this.drops, this.wind, this.storm, this.snowDepth, this.fogRange, this.mist, this.haze, this.reflect
      );
   }

   public WeatherFlakeChannel method3085() {
      WeatherFlakeChannel weatherFlakeChannel2 = this.method3092();
      float floatValue = this.time.getValue3();
      WeatherPresets weatherPresets = this.method3087();
      if (this.weatherFlakeChannel == null
         || weatherFlakeChannel2 != this.weatherFlakeChannel2
         || floatValue != this.floatValue
         || !this.method3086(this.data)
         || weatherPresets != this.weatherPresets) {
         WeatherFlakeChannel.Data data = this.method3093();
         this.weatherFlakeChannel = weatherFlakeChannel2.method7458(WeatherTimeProfile.atTime(floatValue), data, weatherPresets);
         this.weatherFlakeChannel2 = weatherFlakeChannel2;
         this.floatValue = floatValue;
         this.data = data;
         this.weatherPresets = weatherPresets;
      }

      return this.weatherFlakeChannel;
   }

   private boolean method3086(WeatherFlakeChannel.Data data) {
      if (data == null) {
         return false;
      } else {
         boolean flag = this.method3089();
         return Float.compare(data.getFloatValue(), method3094(this.drops)) == 0
            && Float.compare(data.getFloatValue2(), method3094(this.wind)) == 0
            && Float.compare(data.getFloatValue3(), method3094(this.reflect)) == 0
            && Float.compare(data.getFloatValue4(), flag ? method3094(this.mist) : 1.0F) == 0
            && Float.compare(data.getFloatValue5(), flag ? method3094(this.haze) : 1.0F) == 0
            && Float.compare(data.getFloatValue6(), flag ? this.fogRange.getValue3() : -1.0F) == 0
            && data.isFlag() == (this.storm.isEnabled() && this.method3088());
      }
   }

   private WeatherPresets method3087() {
      return switch (this.sky.getIndex()) {
         case 1 -> WeatherPresets.INSTANCE;
         case 2 -> WeatherPresets.INSTANCE2;
         case 3 -> WeatherPresets.INSTANCE3;
         default -> null;
      };
   }

   private boolean method3088() {
      return this.mode.getIndex() == 0;
   }

   private boolean method3089() {
      return this.mode.getIndex() == 1;
   }

   private boolean method3090() {
      return this.mode.getIndex() == 2;
   }

   private boolean method3091() {
      return this.method3088() || this.method3090();
   }

   private WeatherFlakeChannel method3092() {
      if (this.method3090()) {
         return WeatherFlakeChannel.INSTANCE3;
      } else if (this.method3089()) {
         return WeatherFlakeChannel.INSTANCE4;
      } else {
         return this.water.getIndex() == 0 ? WeatherFlakeChannel.INSTANCE : WeatherFlakeChannel.INSTANCE2;
      }
   }

   private WeatherFlakeChannel.Data method3093() {
      boolean flag = this.method3089();
      return new WeatherFlakeChannel.Data(
         method3094(this.drops),
         method3094(this.wind),
         method3094(this.reflect),
         flag ? method3094(this.mist) : 1.0F,
         flag ? method3094(this.haze) : 1.0F,
         flag ? this.fogRange.getValue3() : -1.0F,
         this.storm.isEnabled() && this.method3088()
      );
   }

   private static float method3094(SliderSetting reflect) {
      return reflect.getValue3() / 100.0F;
   }

   private static String method3095(float f) {
      int n = Math.round(f * 60.0F) % 1440;
      if (n < 0) {
         n += 1440;
      }

      return String.format(Locale.US, "%02d:%02d", n / 60, n % 60);
   }

   public float method3096() {
      return (float)(System.currentTimeMillis() - CONST_longValue3) / 1000.0F;
   }

   private float method3097() {
      float f = this.isEnabled() ? 1.0F : 0.0F;
      float f2 = Math.clamp((float)(System.currentTimeMillis() - this.longValue3) / 900.0F, 0.0F, 1.0F);
      f2 = f2 * f2 * (3.0F - 2.0F * f2);
      return this.floatValue3 + (f - this.floatValue3) * f2;
   }

   public float method3098() {
      return this.method3097();
   }

   public boolean method3099() {
      return this.method3098() > 0.01F && mc.level != null && mc.gameRenderer.mainRenderTarget() != null;
   }

   public float method3100() {
      float f = this.isEnabled() ? 1.0F : 0.0F;
      float f2 = f > this.floatValue2 ? 4500.0F : 14000.0F;
      float f3 = Math.clamp((float)(System.currentTimeMillis() - this.longValue2) / f2, 0.0F, 1.0F);
      return this.floatValue2 + (f - this.floatValue2) * f3;
   }

   public float method3101() {
      return this.flag ? 0.85F : 0.0F;
   }

   private void method3102() {
      boolean flag = this.isEnabled() && this.method3092().flag;
      if (flag != this.flag) {
         this.flag = flag;
         FoliageSprites.enabled = flag;
         mc.execute(() -> {
            if (mc.level != null) {
               mc.reloadResourcePacks();
            }
         });
      }
   }

   public boolean method3103() {
      return this.method3100() > 0.01F && this.method3085().floatValue23 > 0.001F && mc.level != null && mc.gameRenderer.mainRenderTarget() != null;
   }

   public float method3104() {
      return method3105(this.method3096(), this.method3085().floatValue13) * this.method3097();
   }

   private static float method3105(float f, float f2) {
      if (f2 <= 0.0F) {
         return 0.0F;
      } else {
         int n = (int)Math.floor(f * f2);
         float f3 = 0.0F;

         for (int i = n - 1; i <= n; i++) {
            float f4 = (i + Glowboost.method3268(118049640925L, i, 0)) / f2;
            float f5 = f - f4;
            if (!(f5 < 0.0F)) {
               float f6 = 0.35F + 0.65F * Glowboost.method3268(118049640925L, i, 1);
               float f7 = method3106(f5, 0.09F) + method3106(f5 - 0.13F, 0.17F) * 0.65F;
               f3 = Math.max(f3, Math.min(1.0F, f7) * f6);
            }
         }

         return f3;
      }
   }

   private static float method3106(float f, float f2) {
      return f < 0.0F ? 0.0F : (float)(Math.exp(-f / f2) * (1.0 - Math.exp(-f / 0.012)));
   }

   public float method3107() {
      return lerp(1.0F, this.method3085().floatValue16, this.method3097());
   }

   public void method3108(float[] floats) {
      float f = this.method3097();
      WeatherFlakeChannel weatherFlakeChannel = this.method3085();
      floats[0] = lerp(1.0F, weatherFlakeChannel.floatValues11[0], f);
      floats[1] = lerp(1.0F, weatherFlakeChannel.floatValues11[1], f);
      floats[2] = lerp(1.0F, weatherFlakeChannel.floatValues11[2], f);
      floats[3] = lerp(1.0F, weatherFlakeChannel.floatValue15, f) * (1.0F + this.method3104() * 0.85F);
   }

   public float method3109() {
      return this.method3097() < 0.15F ? -1.0F : this.method3085().floatValue17;
   }

   public float method3110() {
      float f = this.method3097();
      if (f <= 0.01F) {
         return -1.0F;
      } else {
         WeatherFlakeChannel weatherFlakeChannel = this.method3085();
         return weatherFlakeChannel.intValue2 == 0 ? 0.0F : Math.min(1.0F, weatherFlakeChannel.floatValue18 * f);
      }
   }

   public float method3111() {
      float f = this.method3110();
      if (f < 0.0F) {
         return -1.0F;
      } else {
         return this.method3085().floatValue13 > 0.0F ? f : 0.0F;
      }
   }

   public int method3112() {
      return this.method3110() < 0.0F ? -1 : this.method3085().intValue2;
   }

   public boolean method3113() {
      return this.method3110() > 0.0F;
   }

   public boolean method3114(Entity entity, BlockState blockState) {
      if (this.isEnabled() && entity.level().isClientSide()) {
         SoundType soundType = blockState.getSoundType();
         float f = 0.94F + entity.getRandom().nextFloat() * 0.12F;
         WeatherFlakeChannel weatherFlakeChannel = this.method3085();
         if (weatherFlakeChannel.intValue2 == 1) {
            entity.playSound(SoundEvents.WET_GRASS_STEP, soundType.getVolume() * 0.22F, soundType.getPitch() * f);
            return true;
         } else if (weatherFlakeChannel.flag) {
            entity.playSound(SoundEvents.SNOW_STEP, soundType.getVolume() * 0.2F, soundType.getPitch() * f);
            return true;
         } else if (weatherFlakeChannel.floatValue27 >= 0.9F) {
            entity.playSound(soundType.getStepSound(), soundType.getVolume() * 0.08F, soundType.getPitch() * 0.78F * f);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @EventTarget
   public void method3115(BackgroundRenderEvent event) {
      if (!WorldModule.INSTANCE.method5648()) {
         float f = this.method3097();
         if (!(f <= 0.01F)) {
            WeatherFlakeChannel weatherFlakeChannel = this.method3085();
            float floatValue = event.getFloatValue2() > 0.0F ? lerp(event.getFloatValue2(), weatherFlakeChannel.floatValue14, f) : weatherFlakeChannel.floatValue14;
            event.setFloatValue(floatValue);
            int intValue = method3120(event.getIntValue(), weatherFlakeChannel.intValue, f);
            float f2 = ColorUtils.method6427(intValue) * lerp(1.0F, 0.9F, f);
            event.setColor(Math.clamp((long)((int)(f2 * 255.0F + 0.5F)), 0, 255) << 24 | intValue & 16777215);
            event.setCancelled(true);
         }
      }
   }

   public boolean method3116() {
      return this.method3097() > 0.01F && mc.level != null && mc.player != null;
   }

   public void method3117(Matrix4f zenith$worldMatrix) {
      this.method3102();
      if (this.method3116()) {
         float f = this.method3097();
         PoseStack matrixStack = this.matrixStack;
         matrixStack.setIdentity();
         matrixStack.mulPose(zenith$worldMatrix);
         WeatherFlakeChannel weatherFlakeChannel = this.method3085();
         Camera camera = mc.getEntityRenderDispatcher().camera;
         Vec3 position = camera.position();
         boolean flag = weatherFlakeChannel.flag || weatherFlakeChannel.intValue2 != 0 || !this.method3103();
         FlakeState flakeState2 = flag ? this.method3118(position) : null;
         if (!flag || flakeState2 != null) {
            float f2 = this.method3096();
            long now = System.currentTimeMillis();
            this.glowboost.method3232(camera, 0.0F);
            this.print.method5440(mc.level, mc.level.players(), weatherFlakeChannel, now);
            SnowShader.method403(method3094(this.reflect));
            SnowShader.method392(position, weatherFlakeChannel, weatherFlakeChannel.floatValue23);
            if (SnowVisibility.isVisible()) {
               SnowVisibility.method2402(this.glowboost);
            } else {
               FlakeUtil.method7507(this.glowboost, flakeState2, weatherFlakeChannel, f, this.print, now, position.x, position.y, position.z, method3094(this.snowDepth));
            }

            this.print.method5444(this.glowboost, weatherFlakeChannel, f, now, position.x, position.y, position.z);
            if (flakeState2 != null && !this.method3103()) {
               GroundMistRenderer.renderGroundMist(this.glowboost, flakeState2, weatherFlakeChannel, f, f2, position.x, position.y, position.z);
            }

            if (flakeState2 != null) {
               RippleUtil.method7826(this.glowboost, flakeState2, weatherFlakeChannel, f, f2, position.x, position.y, position.z);
            }

            ImmediateRenderer.method8013(null);
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.identity();

            try {
               SnowShader.method397(matrixStack);
               this.glowboost.method3253(matrixStack);
            } finally {
               modelViewStack.popMatrix();
            }
         }
      }
   }

   private FlakeState method3118(Vec3 position) {
      int intValue = (int)Math.floor(position.x);
      int n = (int)Math.floor(position.z);
      long now = System.currentTimeMillis();
      if (this.flakeState == null) {
         this.flakeState = FlakeState.method4535(mc.level, intValue, n, 52);
         this.longValue = now;
      } else if (now - this.longValue > 400L) {
         this.flakeState.method4536(mc.level, intValue, n);
         this.longValue = now;
      } else if (intValue != this.intValue || n != this.intValue2) {
         this.flakeState.method4537(mc.level, intValue, n);
      }

      Util11.method2332(mc.level, this.flakeState);
      this.intValue = intValue;
      this.intValue2 = n;
      return this.flakeState;
   }

   @Override
   public void onEnable() {
      this.floatValue2 = this.method3100();
      this.floatValue3 = this.method3097();
      this.longValue2 = System.currentTimeMillis();
      this.longValue3 = this.longValue2;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.floatValue2 = this.method3100();
      this.floatValue3 = this.method3097();
      this.longValue2 = System.currentTimeMillis();
      this.longValue3 = this.longValue2;
      super.onDisable();
      if (this.flag) {
         this.flag = false;
         FoliageSprites.enabled = false;
         mc.schedule(mc::reloadResourcePacks);
      }

      this.flakeState = null;
      this.intValue = Integer.MIN_VALUE;
      this.print.clear();
   }

   public void method3119(MenuDrawContext customDrawContext, float floatValue, float y, float width, float height, float previewAlpha) {
      WeatherFlakeChannel weatherFlakeChannel = this.method3085();
      float f = 1.0F;
      float f2 = this.method3096();
      float f3 = method3105(f2, weatherFlakeChannel.floatValue13);
      WeatherPreviewUtil.method255(customDrawContext, floatValue, y, width, height, weatherFlakeChannel, f, f3, f2, previewAlpha);
      if (this.flakeState2 == null) {
         this.flakeState2 = FlakeState.method4542(5);
      }

      float xScale = width / 11.0F * 1.15F;
      float yo = y + height * 0.82F;
      PoseStack matrixStack = WeatherPreviewTerrainRenderer.createPreviewPose(customDrawContext, floatValue + width / 2.0F, yo, xScale);
      this.glowboost.method3233(WeatherPreviewTerrainRenderer.getPreviewPitch(), WeatherPreviewTerrainRenderer.getPreviewYaw(), 0.0F);
      WeatherPreviewTerrainRenderer.renderTerrain(this.glowboost, this.flakeState2, WeatherPreviewTerrainRenderer.Data.fromWeather(weatherFlakeChannel, f), previewAlpha);
      WeatherReflectionRenderer.drawReflections(this.glowboost, this.flakeState2, weatherFlakeChannel, f * previewAlpha, f2, 0.0, 2.0, 0.0);
      FlakeUtil.method7508(this.glowboost, this.flakeState2, weatherFlakeChannel, f * previewAlpha, 0.0, 2.0, 0.0);
      Print.method5446(this.glowboost, weatherFlakeChannel, previewAlpha);
      GroundMistRenderer.renderGroundMist(this.glowboost, this.flakeState2, weatherFlakeChannel, f * previewAlpha, f2, 0.0, 2.0, 0.0);
      RippleUtil.method7827(this.glowboost, this.flakeState2, weatherFlakeChannel, f * previewAlpha, f2, 0.0, 2.0, 0.0);
      this.glowboost.method3253(matrixStack);
   }

   private static float lerp(float f, float f2, float f3) {
      return f + (f2 - f) * Math.clamp(f3, 0.0F, 1.0F);
   }

   private static int method3120(int intValue, int n, float f) {
      if (intValue >>> 24 == 0) {
         return n;
      } else {
         int n2 = method3121(ColorUtils.method6423(intValue), ColorUtils.method6423(n), f);
         int n3 = method3121(ColorUtils.method6420(intValue), ColorUtils.method6420(n), f);
         int n4 = method3121(ColorUtils.method6421(intValue), ColorUtils.method6421(n), f);
         int n5 = method3121(ColorUtils.method6422(intValue), ColorUtils.method6422(n), f);
         return n2 << 24 | n3 << 16 | n4 << 8 | n5;
      }
   }

   private static int method3121(int n, int n2, float f) {
      return Math.clamp((long)((int)(n + (n2 - n) * f + 0.5F)), 0, 255);
   }
}

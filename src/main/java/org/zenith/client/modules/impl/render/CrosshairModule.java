package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.CanvasSetting;
import org.zenith.utility.misc.Canvas;
import org.zenith.utility.misc.CrosshairPresets;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Crosshair",
   category = ModuleCategory.RENDER,
   description = "module.crosshair.description"
)
public final class CrosshairModule extends Module {
   public static final CrosshairModule INSTANCE = new CrosshairModule();
   private static final int ANIMATION_NONE = 0;
   private static final int ANIMATION_SPIN = 1;
   private static final int ANIMATION_PULSE = 2;
   private static final int ANIMATION_RECOIL = 3;
   private static final int ANIMATION_HIT = 4;
   private static final float HIT_ANIMATION_DURATION_MS = 220.0F;
   private static final long ANIMATION_START_TIME_MS = System.currentTimeMillis();
   private final CanvasSetting canvas = new CanvasSetting("module.crosshair.canvas");
   private final ButtonSetting editor = new ButtonSetting("module.crosshair.editor", "w", this::openEditor);
   private final SliderSetting scale = new SliderSetting("module.crosshair.scale", 2.0F, 1.0F, 6.0F, 1.0F, "module.crosshair.scale.desc", "x");
   private final ModeSetting color = new ModeSetting(
      "module.crosshair.color", "module.crosshair.colorMode.desc", "module.crosshair.asDrawn", "module.particles.sync", "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.crosshair.customColor", "module.crosshair.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(2)
   );
   private final ModeSetting animation = new ModeSetting(
      "module.crosshair.animation",
      "module.crosshair.animation.desc",
      "module.crosshair.animNone",
      "module.crosshair.animSpin",
      "module.crosshair.animPulse",
      "module.crosshair.animRecoil",
      "module.crosshair.animHit"
   );
   private final SliderSetting animationSpeed = new SliderSetting(
      "module.crosshair.animationSpeed", 1.0F, 0.2F, 3.0F, 0.1F, "module.crosshair.animationSpeed.desc", "x", () -> !this.animation.isSelected(0), null
   );
   private long lastAttackTime;

   private List<Setting> getEditorSettings() {
      return List.of(this.scale, this.color, this.customColor, this.animation, this.animationSpeed);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.editor, this.canvas);
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      JsonObject settingsJson = json.getAsJsonObject("Settings");

      for (Setting setting : this.getEditorSettings()) {
         setting.safe(settingsJson);
      }

      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json != null) {
         JsonObject settingsJson = json.getAsJsonObject("Settings");
         if (settingsJson != null) {
            for (Setting setting : this.getEditorSettings()) {
               if (settingsJson.has(setting.getKey())) {
                  setting.load(settingsJson);
               }
            }
         }
      }
   }

   private void openEditor() {
      Zenith.getInstance().getNLMenuScreen().getCrosshairEditor().open(this.canvas.getCanvas(), this.getEditorSettings());
   }

   private CrosshairModule() {
      this.canvas.getCanvas().method139(CrosshairPresets.method5712(0));
   }

   @EventTarget
   public void onAttack(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST) {
         this.lastAttackTime = System.currentTimeMillis();
      }
   }

   @EventTarget(0)
   public void onScreenRender(ScreenRenderEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            Canvas canvas = this.canvas.getCanvas();
            if (!canvas.isEmpty()) {
               MenuDrawContext drawContext = event.getMenuDrawContext();
               float x = mc.getWindow().getGuiScaledWidth() / 2.0F;
               float y = mc.getWindow().getGuiScaledHeight() / 2.0F;
               float cell = this.scale.getValue3();
               float animationSpeedMultiplier = this.animationSpeed.getValue3();
               float elapsedSeconds = (float)(System.currentTimeMillis() - ANIMATION_START_TIME_MS) / 1000.0F;
               float rotationDegrees = 0.0F;
               float scaleMultiplier = 1.0F;
               float brightnessMultiplier = 1.0F;
               switch (this.getAnimationMode()) {
                  case 1:
                     rotationDegrees = elapsedSeconds * 60.0F * animationSpeedMultiplier;
                     break;
                  case 2:
                     scaleMultiplier = 1.0F + 0.14F * Mth.sin(elapsedSeconds * 3.0F * animationSpeedMultiplier);
                     break;
                  case 3:
                     float attackStrength = Mth.clamp(mc.player.getAttackStrengthScale(0.0F), 0.0F, 1.0F);
                     scaleMultiplier = 1.0F + 0.45F * (1.0F - attackStrength) * animationSpeedMultiplier;
                     break;
                  case 4:
                     float hitProgress = (float)(System.currentTimeMillis() - this.lastAttackTime) / (220.0F / animationSpeedMultiplier);
                     float hitIntensity = hitProgress >= 1.0F ? 0.0F : (1.0F - hitProgress) * (1.0F - hitProgress);
                     scaleMultiplier = 1.0F + 0.5F * hitIntensity;
                     brightnessMultiplier = 1.0F + 0.8F * hitIntensity;
               }

               drawContext.pushMatrix();
               drawContext.pose().translate(x, y);
               if (rotationDegrees != 0.0F) {
                  drawContext.pose().rotate((float)Math.toRadians(rotationDegrees));
               }

               if (scaleMultiplier != 1.0F) {
                  drawContext.pose().scale(scaleMultiplier, scaleMultiplier);
               }

               drawContext.beginRectBatch();
               float gridOffset = -15.0F * cell;

               for (int row = 0; row < 31; row++) {
                  for (int column = 0; column < 31; column++) {
                     int pixelColor = canvas.method136(column, row);
                     if (pixelColor >>> 24 != 0) {
                        drawContext.drawRectBatched(gridOffset + column * cell, gridOffset + row * cell, cell, cell, this.resolvePixelColor(pixelColor, brightnessMultiplier));
                     }
                  }
               }

               drawContext.flushRects();
               drawContext.popMatrix();
            }
         }
      }
   }

   private ZenithColor resolvePixelColor(int pixelColor, float brightnessMultiplier) {
      int alpha = pixelColor >>> 24;
      int red = pixelColor >> 16 & 0xFF;
      int green = pixelColor >> 8 & 0xFF;
      int blue = pixelColor & 0xFF;
      if (this.color.isSelected(1)) {
         ZenithColor clientColor = Zenith.getInstance().getStyleManager().getClientColor(0);
         red = clientColor.getIntValue();
         green = clientColor.getIntValue2();
         blue = clientColor.getIntValue3();
      } else if (this.color.isSelected(2)) {
         ZenithColor zenithColor = this.customColor.getZenithColor();
         red = zenithColor.getIntValue();
         green = zenithColor.getIntValue2();
         blue = zenithColor.getIntValue3();
      }

      if (brightnessMultiplier != 1.0F) {
         red = Math.min(255, Math.round(red * brightnessMultiplier));
         green = Math.min(255, Math.round(green * brightnessMultiplier));
         blue = Math.min(255, Math.round(blue * brightnessMultiplier));
      }

      return new ZenithColor(red, green, blue, alpha);
   }

   private int getAnimationMode() {
      for (int i = 0; i < 5; i++) {
         if (this.animation.isSelected(i)) {
            return i;
         }
      }

      return 0;
   }
}

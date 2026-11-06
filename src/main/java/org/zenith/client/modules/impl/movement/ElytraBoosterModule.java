package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ModuleHookDefinitions;

@ModuleInfo(
   name = "ElytraBooster",
   category = ModuleCategory.MOVEMENT,
   description = "module.elytrabooster.description"
)
public final class ElytraBoosterModule extends Module {
   public static final ElytraBoosterModule INSTANCE = new ElytraBoosterModule();
   private static final int INITIAL_ACCELERATION_TICKS = 30;
   private static final int INDIVIDUAL_PITCH_COUNT = 56;
   private static final int SETTINGS_PER_GROUP = 25;
   private static final float[] BRAVO_GRIEF_HORIZONTAL_SPEEDS = new float[]{1.67F, 1.68F, 1.69F, 1.72F, 1.72F, 1.73F, 1.74F, 1.75F, 1.76F};
   private static final float[] BRAVO_GRIEF_VERTICAL_SPEEDS = new float[]{1.72F, 1.72F, 1.72F, 1.72F, 1.72F, 1.73F, 1.74F, 1.76F, 1.8F};
   private static final float[] REALLY_WORLD_HORIZONTAL_SPEEDS = new float[]{1.6F, 1.63F, 1.66F, 1.71F, 1.73F, 1.81F, 1.81F, 1.81F, 1.81F};
   private static final float[] REALLY_WORLD_VERTICAL_SPEEDS = new float[]{1.6F, 1.62F, 1.6F, 1.62F, 1.64F, 1.68F, 1.75F, 1.9F, 2.16F};
   private final BooleanSetting acceleration = new BooleanSetting("module.elytraBooster.acceleration", "module.elytraBooster.acceleration.desc", true);
   private final ModeSetting mode = new ModeSetting(
      "module.elytraBooster.mode",
      "module.elytraBooster.mode.desc",
      this.acceleration::isEnabled,
      "module.elytraBooster.mode.reallyWorld",
      "module.elytraBooster.mode.bravo",
      "module.elytraBooster.mode.bravoGrief",
      "module.elytraBooster.mode.lightningGrim",
      "module.elytraBooster.mode.custom"
   );
   private final ModeSetting.Entry reallyWorldMode = this.mode.getValues().get(0);
   private final ModeSetting.Entry bravoMode = this.mode.getValues().get(1);
   private final ModeSetting.Entry bravoGriefMode = this.mode.getValues().get(2);
   private final ModeSetting.Entry lightningGrimMode = this.mode.getValues().get(3);
   private final ModeSetting.Entry customMode = this.mode.getValues().get(4);
   private final SliderSetting lightningGrimMaxVelocity = new SliderSetting(
      "module.elytraBooster.lightningGrimMaxVelocity",
      1.67F,
      0.1F,
      3.0F,
      0.01F,
      "module.elytraBooster.lightningGrimMaxVelocity.desc",
      "",
      () -> this.acceleration.isEnabled() && this.lightningGrimMode.isSelected()
   );
   private final SliderSetting[] horizontalSpeedSettings = createAngleRangeSettings(
      "module.elytraBooster.slider.xz", 9, 1.52F, () -> this.acceleration.isEnabled() && this.customMode.isSelected()
   );
   private final SliderSetting[] verticalSpeedSettings = createAngleRangeSettings(
      "module.elytraBooster.slider.y", 11, 1.51F, () -> this.acceleration.isEnabled() && this.customMode.isSelected() && !this.individualY.isEnabled()
   );
   private final SliderSetting[] individualPitchSpeedSettings = createIndividualPitchSettings(
      "module.elytraBooster.slider.individualY",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled()
   );
   private final SliderSetting[] initialAccelerationSettings = createIndividualPitchSettings(
      "module.elytraBooster.slider.accelerationStart",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled()
   );
   private final SliderSetting[] continuedAccelerationSettings = createIndividualPitchSettings(
      "module.elytraBooster.slider.accelerationFurther",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled()
   );
   private final GroupSetting horizontalSpeedGroup = new GroupSetting(
      "module.elytraBooster.window.xz", () -> this.acceleration.isEnabled() && this.customMode.isSelected(), this.horizontalSpeedSettings
   );
   private final BooleanSetting individualY = new BooleanSetting(
      "module.elytraBooster.individualY", "module.elytraBooster.individualY.desc", false, () -> this.acceleration.isEnabled() && this.customMode.isSelected()
   );
   private final GroupSetting verticalSpeedGroup = new GroupSetting(
      "module.elytraBooster.window.y", () -> this.acceleration.isEnabled() && this.customMode.isSelected() && !this.individualY.isEnabled(), this.verticalSpeedSettings
   );
   private final BooleanSetting customAcceleration = new BooleanSetting(
      "module.elytraBooster.customAcceleration",
      "module.elytraBooster.customAcceleration.desc",
      false,
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled()
   );
   private final GroupSetting individualPitchGroup = new GroupSetting(
      "module.elytraBooster.window.individualY",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.individualPitchSpeedSettings, 0, 25)
   );
   private final GroupSetting individualYContinuation = new GroupSetting(
      "module.elytraBooster.window.individualYContinuation",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.individualPitchSpeedSettings, 25, 50)
   );
   private final GroupSetting individualYContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.individualYContinuationSecond",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.individualPitchSpeedSettings, 50, 56)
   );
   private final GroupSetting accelerationStart = new GroupSetting(
      "module.elytraBooster.window.accelerationStart",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.initialAccelerationSettings, 0, 25)
   );
   private final GroupSetting accelerationStartContinuation = new GroupSetting(
      "module.elytraBooster.window.accelerationStartContinuation",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.initialAccelerationSettings, 25, 50)
   );
   private final GroupSetting accelerationStartContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.accelerationStartContinuationSecond",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.initialAccelerationSettings, 50, 56)
   );
   private final GroupSetting accelerationFurther = new GroupSetting(
      "module.elytraBooster.window.accelerationFurther",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.continuedAccelerationSettings, 0, 25)
   );
   private final GroupSetting accelerationFurtherContinuation = new GroupSetting(
      "module.elytraBooster.window.accelerationFurtherContinuation",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.continuedAccelerationSettings, 25, 50)
   );
   private final GroupSetting accelerationFurtherContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.accelerationFurtherContinuationSecond",
      () -> this.acceleration.isEnabled() && this.customMode.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.continuedAccelerationSettings, 50, 56)
   );
   private final MultiSelectSetting render = new MultiSelectSetting("module.elytraBooster.render", "module.elytraBooster.render.desc");
   private final MultiSelectSetting.Entry renderAngles = new MultiSelectSetting.Entry(this.render, "module.elytraBooster.renderAngles", false);
   private int fireworkAge;

   private ElytraBoosterModule() {
      this.render.setVisible(this.customMode::isSelected);
      ModuleHookDefinitions.class123.subscribe(0, boostEvent -> {
         if (this.isAccelerationActive()) {
            if (this.shouldSkipFireworkBoost(boostEvent.intValue, boostEvent.intValue2, boostEvent.vec3d3)) {
               boostEvent.vec3d = boostEvent.vec3d3;
            } else {
               boostEvent.vec3d = this.applyFireworkBoost(boostEvent.vec3d3, boostEvent.vec3d4);
            }
         }
      });
   }

   public boolean isAccelerationActive() {
      return this.isEnabled() && this.acceleration.isEnabled() && mc.player != null && mc.player.isFallFlying();
   }

   public boolean shouldSkipFireworkBoost(int rocketAge, int rocketLifetime, Vec3 playerVelocity) {
      this.fireworkAge = rocketAge;
      return false;
   }

   public Vec3 method1769(Vec3 deltaMovement) {
      return deltaMovement;
   }

   public Vec3 method1770(Vec3 travelInput, Vec3 fallFlyingVelocity, Vec3 lookAngle, Vec3 previousLookAngle) {
      return fallFlyingVelocity;
   }

   public Vec3 applyFireworkBoost(Vec3 playerVelocity, Vec3 boostDirection) {
      LocalPlayer player = mc.player;
      if (player != null && boostDirection != null) {
         float yaw = Mth.wrapDegrees(player.getYRot());
         float pitch = player.getXRot();
         Vec3 normalizedDirection = boostDirection.normalize();
         if (normalizedDirection.lengthSqr() > 0.0) {
            yaw = (float)Math.toDegrees(Math.atan2(-normalizedDirection.x, normalizedDirection.z));
            pitch = (float)(-Math.toDegrees(Math.asin(Mth.clamp(normalizedDirection.y, -1.0, 1.0))));
         }

         float horizontalSpeed;
         float verticalSpeed;
         if (this.lightningGrimMode.isSelected()) {
            verticalSpeed = horizontalSpeed = this.lightningGrimMaxVelocity.getValue3();
         } else if (this.customMode.isSelected()) {
            boolean initialAcceleration = this.customAcceleration.isEnabled() && this.fireworkAge < 30;
            horizontalSpeed = this.getCustomAngleSpeed(yaw, this.horizontalSpeedSettings);
            verticalSpeed = this.individualY.isEnabled() ? this.getIndividualPitchSpeed(pitch, initialAcceleration) : this.getCustomAngleSpeed(pitch, this.verticalSpeedSettings);
            horizontalSpeed = Math.max(horizontalSpeed, verticalSpeed);
         } else if (this.bravoMode.isSelected()) {
            horizontalSpeed = this.getBravoHorizontalSpeed(pitch, yaw);
            verticalSpeed = this.getBravoVerticalSpeed(pitch);
         } else if (this.bravoGriefMode.isSelected()) {
            horizontalSpeed = getPresetAngleSpeed(BRAVO_GRIEF_HORIZONTAL_SPEEDS, yaw);
            verticalSpeed = getPresetAngleSpeed(BRAVO_GRIEF_VERTICAL_SPEEDS, pitch);
         } else {
            if (!this.reallyWorldMode.isSelected()) {
               return playerVelocity;
            }

            horizontalSpeed = getPresetAngleSpeed(REALLY_WORLD_HORIZONTAL_SPEEDS, yaw);
            verticalSpeed = getPresetAngleSpeed(REALLY_WORLD_VERTICAL_SPEEDS, pitch);
            horizontalSpeed = Math.max(horizontalSpeed, verticalSpeed);
         }

         return playerVelocity.add(
            boostDirection.x * 0.1 + (boostDirection.x * horizontalSpeed - playerVelocity.x) * 0.5, boostDirection.y * 0.1 + (boostDirection.y * verticalSpeed - playerVelocity.y) * 0.5, boostDirection.z * 0.1 + (boostDirection.z * horizontalSpeed - playerVelocity.z) * 0.5
         );
      } else {
         return playerVelocity;
      }
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      if (mc.player != null && mc.level != null && !mc.getDebugOverlay().showDebugScreen() && this.customMode.isSelected() && this.renderAngles.isEnabled()) {
         String pitchText = "Pitch: " + (int)mc.player.getXRot();
         int x = (mc.getWindow().getGuiScaledWidth() - mc.font.width(pitchText)) / 2;
         int textY = mc.getWindow().getGuiScaledHeight() / 2 + 25;
         event.getCustomDrawContext().text(mc.font, pitchText, x, textY, -1, true);
      }
   }

   private float getIndividualPitchSpeed(float pitch, boolean initialAcceleration) {
      int pitchIndex = Mth.clamp(Math.round(foldAngleToNinetyDegrees(pitch)), 0, 55);
      return !this.customAcceleration.isEnabled()
         ? this.individualPitchSpeedSettings[pitchIndex].getValue3()
         : (initialAcceleration ? this.initialAccelerationSettings[pitchIndex] : this.continuedAccelerationSettings[pitchIndex]).getValue3();
   }

   private static float getPresetAngleSpeed(float[] speeds, float angle) {
      int angleIndex = Mth.clamp((int)(foldAngleToFortyFiveDegrees(angle) / 5.0F), 0, speeds.length - 1);
      return speeds[angleIndex];
   }

   private float getBravoVerticalSpeed(float pitch) {
      float absolutePitch;
      if ((absolutePitch = Math.abs(pitch)) >= 37.0F && absolutePitch <= 38.0F) {
         return 2.03F;
      } else if (absolutePitch >= 25.0F && absolutePitch <= 30.0F) {
         return 2.0F;
      } else if (absolutePitch >= 35.0F && absolutePitch <= 45.0F) {
         return 1.99F;
      } else if (absolutePitch >= 40.0F && absolutePitch <= 50.0F) {
         return 1.97F;
      } else if (absolutePitch >= 50.0F && absolutePitch <= 60.0F) {
         return 1.96F;
      } else if (absolutePitch >= 51.0F && absolutePitch <= 61.0F) {
         return 1.85F;
      } else {
         return absolutePitch >= 52.0F && absolutePitch <= 65.0F ? 1.8F : 1.59F;
      }
   }

   private float getBravoHorizontalSpeed(float pitch, float yaw) {
      float absolutePitch = Math.abs(pitch);
      float quadrantYaw = Math.abs(Mth.wrapDegrees(yaw) % 90.0F);
      float speed;
      if (absolutePitch >= 38.0F && absolutePitch <= 52.0F) {
         speed = 2.0F;
      } else if (absolutePitch >= 32.0F && absolutePitch <= 58.0F) {
         speed = 1.96F;
      } else if (absolutePitch >= 28.0F && absolutePitch <= 62.0F) {
         speed = 1.95F;
      } else {
         float upperAngle = 61.0F;
         float lowerAngle = 29.0F;
         if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
            upperAngle = 61.0F;
            lowerAngle = 29.0F;
            if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
               upperAngle = 60.0F;
               lowerAngle = 28.0F;
               if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                  upperAngle = 60.0F;
                  lowerAngle = 28.0F;
                  if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                     upperAngle = 64.0F;
                     lowerAngle = 26.0F;
                     if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                        upperAngle = 64.0F;
                        lowerAngle = 26.0F;
                        if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                           upperAngle = 66.0F;
                           lowerAngle = 24.0F;
                           if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                              upperAngle = 66.0F;
                              lowerAngle = 24.0F;
                              if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                                 upperAngle = 75.0F;
                                 lowerAngle = 15.0F;
                                 if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                                    upperAngle = 75.0F;
                                    lowerAngle = 15.0F;
                                    if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                                       upperAngle = 77.0F;
                                       lowerAngle = 13.0F;
                                       if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                                          upperAngle = 77.0F;
                                          lowerAngle = 13.0F;
                                          if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                                             upperAngle = 78.0F;
                                             lowerAngle = 12.0F;
                                             if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                                                upperAngle = 78.0F;
                                                lowerAngle = 12.0F;
                                                if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                                                   upperAngle = 82.0F;
                                                   lowerAngle = 8.0F;
                                                   if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                                                      upperAngle = 79.0F;
                                                      lowerAngle = 11.0F;
                                                      if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                                                         upperAngle = 85.0F;
                                                         lowerAngle = 5.0F;
                                                         if (!(quadrantYaw >= lowerAngle) || !(quadrantYaw <= upperAngle)) {
                                                            upperAngle = 82.0F;
                                                            lowerAngle = 8.0F;
                                                            if (!(absolutePitch >= lowerAngle) || !(absolutePitch <= upperAngle)) {
                                                               speed = !(quadrantYaw <= 90.0F) && !(absolutePitch <= 90.0F) ? 1.66F : 1.67F;
                                                               return pitch > 15.0F ? speed - 0.068F : speed;
                                                            }
                                                         }

                                                         speed = 1.67F;
                                                         return pitch > 15.0F ? speed - 0.068F : speed;
                                                      }
                                                   }

                                                   speed = 1.75F;
                                                   return pitch > 15.0F ? speed - 0.068F : speed;
                                                }
                                             }

                                             speed = 1.75F;
                                             return pitch > 15.0F ? speed - 0.068F : speed;
                                          }
                                       }

                                       speed = 1.75F;
                                       return pitch > 15.0F ? speed - 0.068F : speed;
                                    }
                                 }

                                 speed = 1.75F;
                                 return pitch > 15.0F ? speed - 0.068F : speed;
                              }
                           }

                           speed = 1.75F;
                           return pitch > 15.0F ? speed - 0.068F : speed;
                        }
                     }

                     speed = 1.874F;
                     return pitch > 15.0F ? speed - 0.068F : speed;
                  }
               }

               speed = 1.954F;
               return pitch > 15.0F ? speed - 0.068F : speed;
            }
         }

         speed = 1.963F;
      }

      return pitch > 15.0F ? speed - 0.068F : speed;
   }

   private float getCustomAngleSpeed(float angle, SliderSetting[] speedSettings) {
      int angleIndex = Mth.clamp((int)(foldAngleToFortyFiveDegrees(angle) / 5.0F), 0, speedSettings.length - 1);
      return speedSettings[angleIndex].getValue3();
   }

   private static float foldAngleToNinetyDegrees(float angle) {
      float absoluteAngle;
      return (absoluteAngle = Math.abs(angle)) > 90.0F ? 180.0F - absoluteAngle : absoluteAngle;
   }

   private static float foldAngleToFortyFiveDegrees(float angle) {
      float foldedAngle;
      return (foldedAngle = foldAngleToNinetyDegrees(angle)) > 45.0F ? 90.0F - foldedAngle : foldedAngle;
   }

   private static boolean isAngleInRange(float angle, float minAngle, float maxAngle) {
      return angle >= minAngle && angle <= maxAngle;
   }

   private static SliderSetting[] createAngleRangeSettings(String settingKeyPrefix, int count, float initialSpeed, Supplier<Boolean> visible) {
      SliderSetting[] settings = new SliderSetting[count];

      for (int i = 0; i < count; i++) {
         settings[i] = new SliderSetting(settingKeyPrefix + "." + i * 5 + "-" + (i + 1) * 5, initialSpeed + i * 0.01F, 1.5F, 3.0F, 0.01F, visible);
      }

      return settings;
   }

   private static SliderSetting[] createIndividualPitchSettings(String settingKeyPrefix, Supplier<Boolean> visible) {
      SliderSetting[] settings = new SliderSetting[56];

      for (int i = 0; i < 56; i++) {
         float defaultSpeed = 1.5F + i / 55.0F * 0.11F;
         settings[i] = new SliderSetting(settingKeyPrefix + "." + i, defaultSpeed, 1.5F, 3.0F, 0.01F, visible);
      }

      return settings;
   }
}

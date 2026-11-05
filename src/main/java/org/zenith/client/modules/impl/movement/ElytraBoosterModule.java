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
   private static final int CONST_intValue = 30;
   private static final int CONST_intValue2 = 56;
   private static final int CONST_intValue3 = 25;
   private static final float[] CONST_floatValues = new float[]{1.67F, 1.68F, 1.69F, 1.72F, 1.72F, 1.73F, 1.74F, 1.75F, 1.76F};
   private static final float[] CONST_floatValues2 = new float[]{1.72F, 1.72F, 1.72F, 1.72F, 1.72F, 1.73F, 1.74F, 1.76F, 1.8F};
   private static final float[] CONST_floatValues3 = new float[]{1.6F, 1.63F, 1.66F, 1.71F, 1.73F, 1.81F, 1.81F, 1.81F, 1.81F};
   private static final float[] CONST_floatValues4 = new float[]{1.6F, 1.62F, 1.6F, 1.62F, 1.64F, 1.68F, 1.75F, 1.9F, 2.16F};
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
   private final ModeSetting.Entry entry = this.mode.getValues().get(0);
   private final ModeSetting.Entry entry2 = this.mode.getValues().get(1);
   private final ModeSetting.Entry entry3 = this.mode.getValues().get(2);
   private final ModeSetting.Entry entry4 = this.mode.getValues().get(3);
   private final ModeSetting.Entry entry5 = this.mode.getValues().get(4);
   private final SliderSetting lightningGrimMaxVelocity = new SliderSetting(
      "module.elytraBooster.lightningGrimMaxVelocity",
      1.67F,
      0.1F,
      3.0F,
      0.01F,
      "module.elytraBooster.lightningGrimMaxVelocity.desc",
      "",
      () -> this.acceleration.isEnabled() && this.entry4.isSelected()
   );
   private final SliderSetting[] sliderSettings = method1781(
      "module.elytraBooster.slider.xz", 9, 1.52F, () -> this.acceleration.isEnabled() && this.entry5.isSelected()
   );
   private final SliderSetting[] sliderSettings2 = method1781(
      "module.elytraBooster.slider.y", 11, 1.51F, () -> this.acceleration.isEnabled() && this.entry5.isSelected() && !this.individualY.isEnabled()
   );
   private final SliderSetting[] sliderSettings3 = method1782(
      "module.elytraBooster.slider.individualY",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled()
   );
   private final SliderSetting[] sliderSettings4 = method1782(
      "module.elytraBooster.slider.accelerationStart",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled()
   );
   private final SliderSetting[] sliderSettings5 = method1782(
      "module.elytraBooster.slider.accelerationFurther",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled()
   );
   private final GroupSetting xz = new GroupSetting(
      "module.elytraBooster.window.xz", () -> this.acceleration.isEnabled() && this.entry5.isSelected(), this.sliderSettings
   );
   private final BooleanSetting individualY = new BooleanSetting(
      "module.elytraBooster.individualY", "module.elytraBooster.individualY.desc", false, () -> this.acceleration.isEnabled() && this.entry5.isSelected()
   );
   private final GroupSetting y = new GroupSetting(
      "module.elytraBooster.window.y", () -> this.acceleration.isEnabled() && this.entry5.isSelected() && !this.individualY.isEnabled(), this.sliderSettings2
   );
   private final BooleanSetting customAcceleration = new BooleanSetting(
      "module.elytraBooster.customAcceleration",
      "module.elytraBooster.customAcceleration.desc",
      false,
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled()
   );
   private final GroupSetting individualY2 = new GroupSetting(
      "module.elytraBooster.window.individualY",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings3, 0, 25)
   );
   private final GroupSetting individualYContinuation = new GroupSetting(
      "module.elytraBooster.window.individualYContinuation",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings3, 25, 50)
   );
   private final GroupSetting individualYContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.individualYContinuationSecond",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && !this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings3, 50, 56)
   );
   private final GroupSetting accelerationStart = new GroupSetting(
      "module.elytraBooster.window.accelerationStart",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings4, 0, 25)
   );
   private final GroupSetting accelerationStartContinuation = new GroupSetting(
      "module.elytraBooster.window.accelerationStartContinuation",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings4, 25, 50)
   );
   private final GroupSetting accelerationStartContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.accelerationStartContinuationSecond",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings4, 50, 56)
   );
   private final GroupSetting accelerationFurther = new GroupSetting(
      "module.elytraBooster.window.accelerationFurther",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings5, 0, 25)
   );
   private final GroupSetting accelerationFurtherContinuation = new GroupSetting(
      "module.elytraBooster.window.accelerationFurtherContinuation",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings5, 25, 50)
   );
   private final GroupSetting accelerationFurtherContinuationSecond = new GroupSetting(
      "module.elytraBooster.window.accelerationFurtherContinuationSecond",
      () -> this.acceleration.isEnabled() && this.entry5.isSelected() && this.individualY.isEnabled() && this.customAcceleration.isEnabled(),
      Arrays.copyOfRange(this.sliderSettings5, 50, 56)
   );
   private final MultiSelectSetting render = new MultiSelectSetting("module.elytraBooster.render", "module.elytraBooster.render.desc");
   private final MultiSelectSetting.Entry entry6 = new MultiSelectSetting.Entry(this.render, "module.elytraBooster.renderAngles", false);
   private int intValue;

   private ElytraBoosterModule() {
      this.render.setVisible(this.entry5::isSelected);
      ModuleHookDefinitions.class123.method1646(0, entry4 -> {
         if (this.method1767()) {
            if (this.method1768(entry4.intValue, entry4.intValue2, entry4.vec3d3)) {
               entry4.vec3d = entry4.vec3d3;
            } else {
               entry4.vec3d = this.method1771(entry4.vec3d3, entry4.vec3d4);
            }
         }
      });
   }

   public boolean method1767() {
      return this.isEnabled() && this.acceleration.isEnabled() && mc.player != null && mc.player.isFallFlying();
   }

   public boolean method1768(int intValue, int n, Vec3 vec3d3) {
      this.intValue = intValue;
      return false;
   }

   public Vec3 method1769(Vec3 deltaMovement) {
      return deltaMovement;
   }

   public Vec3 method1770(Vec3 vec, Vec3 returnValue, Vec3 lookAngle, Vec3 vec2) {
      return returnValue;
   }

   public Vec3 method1771(Vec3 vec3d3, Vec3 vec3d4) {
      LocalPlayer player = mc.player;
      if (player != null && vec3d4 != null) {
         float xRot = Mth.wrapDegrees(player.getYRot());
         float f = player.getXRot();
         Vec3 vec = vec3d4.normalize();
         if (vec.lengthSqr() > 0.0) {
            xRot = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
            f = (float)(-Math.toDegrees(Math.asin(Mth.clamp(vec.y, -1.0, 1.0))));
         }

         float f2;
         float f3;
         if (this.entry4.isSelected()) {
            f3 = f2 = this.lightningGrimMaxVelocity.getValue3();
         } else if (this.entry5.isSelected()) {
            boolean flag = this.customAcceleration.isEnabled() && this.intValue < 30;
            f2 = this.method1777(xRot, this.sliderSettings);
            f3 = this.individualY.isEnabled() ? this.method1773(f, flag) : this.method1777(f, this.sliderSettings2);
            f2 = Math.max(f2, f3);
         } else if (this.entry2.isSelected()) {
            f2 = this.method1776(f, xRot);
            f3 = this.method1775(f);
         } else if (this.entry3.isSelected()) {
            f2 = method1774(CONST_floatValues, xRot);
            f3 = method1774(CONST_floatValues2, f);
         } else {
            if (!this.entry.isSelected()) {
               return vec3d3;
            }

            f2 = method1774(CONST_floatValues3, xRot);
            f3 = method1774(CONST_floatValues4, f);
            f2 = Math.max(f2, f3);
         }

         return vec3d3.add(
            vec3d4.x * 0.1 + (vec3d4.x * f2 - vec3d3.x) * 0.5, vec3d4.y * 0.1 + (vec3d4.y * f3 - vec3d3.y) * 0.5, vec3d4.z * 0.1 + (vec3d4.z * f2 - vec3d3.z) * 0.5
         );
      } else {
         return vec3d3;
      }
   }

   @EventTarget
   public void method1772(DrawEvent event) {
      if (mc.player != null && mc.level != null && !mc.getDebugOverlay().showDebugScreen() && this.entry5.isSelected() && this.entry6.isEnabled()) {
         String str = "Pitch: " + (int)mc.player.getXRot();
         int x = (mc.getWindow().getGuiScaledWidth() - mc.font.width(str)) / 2;
         int y = mc.getWindow().getGuiScaledHeight() / 2 + 25;
         event.getCustomDrawContext().text(mc.font, str, x, y, -1, true);
      }
   }

   private float method1773(float xRot, boolean flag) {
      int n = Mth.clamp(Math.round(method1778(xRot)), 0, 55);
      return !this.customAcceleration.isEnabled()
         ? this.sliderSettings3[n].getValue3()
         : (flag ? this.sliderSettings4[n] : this.sliderSettings5[n]).getValue3();
   }

   private static float method1774(float[] floats, float xRot) {
      int n = Mth.clamp((int)(method1779(xRot) / 5.0F), 0, floats.length - 1);
      return floats[n];
   }

   private float method1775(float f) {
      float f2;
      if ((f2 = Math.abs(f)) >= 37.0F && f2 <= 38.0F) {
         return 2.03F;
      } else if (f2 >= 25.0F && f2 <= 30.0F) {
         return 2.0F;
      } else if (f2 >= 35.0F && f2 <= 45.0F) {
         return 1.99F;
      } else if (f2 >= 40.0F && f2 <= 50.0F) {
         return 1.97F;
      } else if (f2 >= 50.0F && f2 <= 60.0F) {
         return 1.96F;
      } else if (f2 >= 51.0F && f2 <= 61.0F) {
         return 1.85F;
      } else {
         return f2 >= 52.0F && f2 <= 65.0F ? 1.8F : 1.59F;
      }
   }

   private float method1776(float f, float xRot) {
      float f2 = Math.abs(f);
      float f3 = Math.abs(Mth.wrapDegrees(xRot) % 90.0F);
      float f4;
      if (f2 >= 38.0F && f2 <= 52.0F) {
         f4 = 2.0F;
      } else if (f2 >= 32.0F && f2 <= 58.0F) {
         f4 = 1.96F;
      } else if (f2 >= 28.0F && f2 <= 62.0F) {
         f4 = 1.95F;
      } else {
         float f5 = 61.0F;
         float f6 = 29.0F;
         if (!(f3 >= f6) || !(f3 <= f5)) {
            f5 = 61.0F;
            f6 = 29.0F;
            if (!(f2 >= f6) || !(f2 <= f5)) {
               f5 = 60.0F;
               f6 = 28.0F;
               if (!(f3 >= f6) || !(f3 <= f5)) {
                  f5 = 60.0F;
                  f6 = 28.0F;
                  if (!(f2 >= f6) || !(f2 <= f5)) {
                     f5 = 64.0F;
                     f6 = 26.0F;
                     if (!(f3 >= f6) || !(f3 <= f5)) {
                        f5 = 64.0F;
                        f6 = 26.0F;
                        if (!(f2 >= f6) || !(f2 <= f5)) {
                           f5 = 66.0F;
                           f6 = 24.0F;
                           if (!(f3 >= f6) || !(f3 <= f5)) {
                              f5 = 66.0F;
                              f6 = 24.0F;
                              if (!(f2 >= f6) || !(f2 <= f5)) {
                                 f5 = 75.0F;
                                 f6 = 15.0F;
                                 if (!(f3 >= f6) || !(f3 <= f5)) {
                                    f5 = 75.0F;
                                    f6 = 15.0F;
                                    if (!(f2 >= f6) || !(f2 <= f5)) {
                                       f5 = 77.0F;
                                       f6 = 13.0F;
                                       if (!(f3 >= f6) || !(f3 <= f5)) {
                                          f5 = 77.0F;
                                          f6 = 13.0F;
                                          if (!(f2 >= f6) || !(f2 <= f5)) {
                                             f5 = 78.0F;
                                             f6 = 12.0F;
                                             if (!(f3 >= f6) || !(f3 <= f5)) {
                                                f5 = 78.0F;
                                                f6 = 12.0F;
                                                if (!(f2 >= f6) || !(f2 <= f5)) {
                                                   f5 = 82.0F;
                                                   f6 = 8.0F;
                                                   if (!(f3 >= f6) || !(f3 <= f5)) {
                                                      f5 = 79.0F;
                                                      f6 = 11.0F;
                                                      if (!(f2 >= f6) || !(f2 <= f5)) {
                                                         f5 = 85.0F;
                                                         f6 = 5.0F;
                                                         if (!(f3 >= f6) || !(f3 <= f5)) {
                                                            f5 = 82.0F;
                                                            f6 = 8.0F;
                                                            if (!(f2 >= f6) || !(f2 <= f5)) {
                                                               f4 = !(f3 <= 90.0F) && !(f2 <= 90.0F) ? 1.66F : 1.67F;
                                                               return f > 15.0F ? f4 - 0.068F : f4;
                                                            }
                                                         }

                                                         f4 = 1.67F;
                                                         return f > 15.0F ? f4 - 0.068F : f4;
                                                      }
                                                   }

                                                   f4 = 1.75F;
                                                   return f > 15.0F ? f4 - 0.068F : f4;
                                                }
                                             }

                                             f4 = 1.75F;
                                             return f > 15.0F ? f4 - 0.068F : f4;
                                          }
                                       }

                                       f4 = 1.75F;
                                       return f > 15.0F ? f4 - 0.068F : f4;
                                    }
                                 }

                                 f4 = 1.75F;
                                 return f > 15.0F ? f4 - 0.068F : f4;
                              }
                           }

                           f4 = 1.75F;
                           return f > 15.0F ? f4 - 0.068F : f4;
                        }
                     }

                     f4 = 1.874F;
                     return f > 15.0F ? f4 - 0.068F : f4;
                  }
               }

               f4 = 1.954F;
               return f > 15.0F ? f4 - 0.068F : f4;
            }
         }

         f4 = 1.963F;
      }

      return f > 15.0F ? f4 - 0.068F : f4;
   }

   private float method1777(float xRot, SliderSetting[] sliderSettings) {
      int n = Mth.clamp((int)(method1779(xRot) / 5.0F), 0, sliderSettings.length - 1);
      return sliderSettings[n].getValue3();
   }

   private static float method1778(float xRot) {
      float f;
      return (f = Math.abs(xRot)) > 90.0F ? 180.0F - f : f;
   }

   private static float method1779(float xRot) {
      float f;
      return (f = method1778(xRot)) > 45.0F ? 90.0F - f : f;
   }

   private static boolean method1780(float f, float f2, float f3) {
      return f >= f2 && f <= f3;
   }

   private static SliderSetting[] method1781(String str, int n, float f, Supplier<Boolean> visible) {
      SliderSetting[] sliderSettings = new SliderSetting[n];

      for (int i = 0; i < n; i++) {
         sliderSettings[i] = new SliderSetting(str + "." + i * 5 + "-" + (i + 1) * 5, f + i * 0.01F, 1.5F, 3.0F, 0.01F, visible);
      }

      return sliderSettings;
   }

   private static SliderSetting[] method1782(String str, Supplier<Boolean> visible) {
      SliderSetting[] sliderSettings = new SliderSetting[56];

      for (int i = 0; i < 56; i++) {
         float floatValue = 1.5F + i / 55.0F * 0.11F;
         sliderSettings[i] = new SliderSetting(str + "." + i, floatValue, 1.5F, 3.0F, 0.01F, visible);
      }

      return sliderSettings;
   }
}

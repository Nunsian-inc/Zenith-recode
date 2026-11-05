package org.zenith.base.rotation.mods;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.AimPolicy;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Class36;
import org.zenith.utility.misc.Class6;
import org.zenith.utility.misc.Resettable;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public final class TargetDeltaFeatures extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final int CONST_intValue2 = 59;
   private static final int CONST_intValue3 = 0;
   private static final int CONST_intValue4 = 1;
   private static final int CONST_intValue5 = 16;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 176.0F;
   private static final float CONST_floatValue3 = 8.0F;
   private static final float CONST_floatValue4 = 8.0F;
   private static final String[] texts = new String[]{
      "target_center_delta_yaw_count_units",
      "target_center_delta_pitch_count_units",
      "target_box_min_delta_yaw_count_units",
      "target_box_max_delta_yaw_count_units",
      "target_box_min_delta_pitch_count_units",
      "target_box_max_delta_pitch_count_units",
      "target_box_width_yaw_count_units",
      "target_box_height_pitch_count_units",
      "target_center_yaw_by_box_width",
      "target_center_pitch_by_box_height",
      "target_center_angular_distance_count_units",
      "target_center_direction_sin",
      "target_center_direction_cos",
      "aim_x_norm_in_target_box",
      "aim_y_norm_in_target_box",
      "aim_inside_target_box",
      "target_center_distance",
      "target_center_horizontal_distance",
      "target_center_vertical_distance_abs",
      "aim_inside_target_box_ticks_clamped",
      "aim_outside_target_box_ticks",
      "ticks_since_entered_target_box",
      "ticks_since_left_target_box",
      "target_center_delta_yaw_change_count_units",
      "target_center_delta_pitch_change_count_units",
      "target_center_distance_change",
      "yaw_target_drift_count_units",
      "pitch_target_drift_count_units",
      "sim_2t_target_center_delta_yaw_count_units",
      "sim_2t_target_center_delta_pitch_count_units",
      "target_motion_x",
      "target_motion_y",
      "target_motion_z",
      "yaw_mouse_count",
      "pitch_mouse_count",
      "yaw_velocity_mouse_counts",
      "pitch_velocity_mouse_counts",
      "yaw_acceleration_mouse_counts",
      "pitch_acceleration_mouse_counts",
      "yaw_jerk_mouse_counts",
      "pitch_jerk_mouse_counts",
      "mouse_dx_nonzero_streak_ticks",
      "mouse_dy_nonzero_streak_ticks",
      "yaw_sign_flip_ticks_ago",
      "pitch_sign_flip_ticks_ago",
      "yaw_mouse_static_help",
      "pitch_mouse_static_help",
      "yaw_current_to_peak_abs_ratio",
      "pitch_current_to_peak_abs_ratio",
      "yaw_peak_mouse_count_abs",
      "pitch_peak_mouse_count_abs",
      "movement_input_forward",
      "movement_input_backward",
      "movement_input_left",
      "movement_input_right",
      "movement_input_jump",
      "player_motion_x",
      "player_motion_y",
      "player_motion_z"
   };
   private int intValue = Integer.MIN_VALUE;
   private boolean flag = false;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private float floatValue3 = 0.0F;
   private float floatValue4 = 0.0F;
   private float floatValue5 = 0.0F;
   private float floatValue6 = 0.0F;
   private boolean flag2 = false;
   private float floatValue7 = 0.0F;
   private float floatValue8 = 0.0F;
   private float floatValue9 = 0.0F;
   private float floatValue10 = 0.0F;
   private float floatValue11 = 0.0F;
   private float floatValue12 = 0.0F;
   private int intValue2 = 0;
   private int intValue3 = 0;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 0.0F;
   private float floatValue15 = 0.0F;
   private float floatValue16 = 0.0F;
   private TargetDeltaFeatures.Entry entry = null;
   private float[] floatValues = null;
   private final Resettable resettable = method3840();
   public float offset;

   private static Resettable method3840() {
      AimPolicy aimPolicy = AimPolicy.method5432();
      if (aimPolicy != null) {
         return new Class36(aimPolicy);
      } else {
         System.err.println("[AimPolicy] artifact not found; TestRotationMode disabled");
         return null;
      }
   }

   public Rotation method3841(Class6 class2, Rotation rotation) {
      return this.method3842(class2, rotation, this.method3852());
   }

   private Rotation method3842(Class6 class2, Rotation rotation, LivingEntity livingEntity) {
      Rotation rotation2 = rotationManager.method7844();
      if (this.resettable == null) {
         return rotation2;
      } else if (rotation == null || rotation.method3137()) {
         this.method3864();
         return rotation2;
      } else if (livingEntity == null) {
         return RotationUtils.method6911();
      } else if (livingEntity != null && livingEntity.isAlive()) {
         AABB boundingBox = livingEntity.getBoundingBox();
         if (boundingBox == null) {
            this.method3864();
            return rotation2;
         } else {
            int id = livingEntity.getId();
            if (this.intValue != id) {
               this.method3864();
               this.intValue = id;
            }

            float floatValue = rotation2.getFloatValue();
            float f = rotation2.getFloatValue2();
            if (!this.flag) {
               this.floatValue = floatValue;
               this.floatValue2 = f;
               this.flag = true;
               return rotation2;
            } else {
               float f2 = Mth.wrapDegrees(floatValue - this.floatValue);
               float f3 = f - this.floatValue2;
               Vec3 position = mc.player.position();
               Vec3 vec = this.method3854(livingEntity, boundingBox);
               Vec3 vec2 = this.method3853(position);
               float y = 0.21217355F;
               if (!this.isFinite(y)) {
                  this.floatValues = null;
                  this.floatValue = floatValue;
                  this.floatValue2 = f;
                  return rotation2;
               } else {
                  float[] floats = this.method3848(rotation2, mc.player.getEyePosition(), livingEntity, boundingBox, f2, f3, vec, vec2, y);
                  this.floatValue = floatValue;
                  this.floatValue2 = f;
                  if (floats != null && floats.length == 59 && this.method3865(floats)) {
                     this.floatValues = (float[])floats.clone();
                     this.entry = new TargetDeltaFeatures.Entry(floats, position, boundingBox, y);
                     int[] ints = this.resettable.method2865(floats);
                     if (mc.player.attackStrengthTicker < 6) {
                        this.offset = Mth.clamp(this.offset - 0.3F, 0.0F, 1.0F);
                     } else {
                        this.offset = Mth.clamp(this.offset + 1.0F, 0.0F, 1.0F);
                     }

                     int[] ints2 = new int[]{(int)(this.method3843(ints[0], floats[0], 1.0F, 0.9F) * this.offset), ints[1]};
                     return rotation2.method3140(ints2[0] * y, ints2[1] * y);
                  } else {
                     this.floatValues = null;
                     this.resettable.reset();
                     return rotation2;
                  }
               }
            }
         }
      } else {
         this.method3864();
         return rotation2;
      }
   }

   private double method3843(double d, float f, float f2, float f3) {
      boolean flag = Math.signum(d) == Math.signum(f);
      float f4 = flag ? f2 : f3;
      return d * f4;
   }

   public String method3844() {
      return this.resettable == null ? "unavailable" : this.resettable.method2866();
   }

   public float[] method3845() {
      return this.floatValues == null ? null : (float[])this.floatValues.clone();
   }

   public String[] method3846() {
      return (String[])texts.clone();
   }

   public void method3847() {
      this.method3864();
   }

   private float[] method3848(Rotation rotation, Vec3 eyePosition, LivingEntity livingEntity, AABB boundingBox, float f, float f2, Vec3 vec, Vec3 vec2, float y) {
      Vec3[] vecs = new Vec3[]{
         new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
         new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),
         new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.minZ),
         new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ),
         new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
         new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
         new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
         new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)
      };
      Vec3 vec3 = this.method3855(boundingBox);
      Rotation rotation2 = this.method3849(eyePosition, vec3);
      Vec2f vec2f = rotation.method3131(rotation2);
      float floatValue = vec2f.getFloatValue();
      float y2 = vec2f.getFloatValue2();
      if (this.isFinite(floatValue) && this.isFinite(y2)) {
         Vec3 eyePosition2 = this.method3850(rotation, eyePosition);
         AABB aabb = this.method3851(livingEntity, boundingBox, vec);
         Vec2f vec2f2 = rotation.method3131(this.method3849(eyePosition2, this.method3855(aabb)));
         float floatValue2 = vec2f2.getFloatValue();
         float y3 = vec2f2.getFloatValue2();
         if (this.isFinite(floatValue2) && this.isFinite(y3)) {
            float floatValue3 = Float.MAX_VALUE;
            float floatValue4 = -Float.MAX_VALUE;
            float floatValue5 = Float.MAX_VALUE;
            float floatValue6 = -Float.MAX_VALUE;

            for (Vec3 vec4 : vecs) {
               Rotation rotation3 = this.method3849(eyePosition, vec4);
               Vec2f vec2f3 = rotation.method3131(rotation3);
               float floatValue7 = vec2f3.getFloatValue();
               float y4 = vec2f3.getFloatValue2();
               if (this.isFinite(floatValue7) && this.isFinite(y4)) {
                  floatValue7 = this.method3863(floatValue7, floatValue);
                  floatValue3 = Math.min(floatValue3, floatValue7);
                  floatValue4 = Math.max(floatValue4, floatValue7);
                  floatValue5 = Math.min(floatValue5, y4);
                  floatValue6 = Math.max(floatValue6, y4);
               }
            }

            if (floatValue3 == Float.MAX_VALUE) {
               floatValue3 = floatValue;
               floatValue4 = floatValue;
               floatValue5 = y2;
               floatValue6 = y2;
            }

            floatValue = this.method3863(floatValue, floatValue);
            float floatValue7 = floatValue4 - floatValue3;
            float floatValue8 = floatValue6 - floatValue5;
            float f3 = (float)(vec3.x - eyePosition.x);
            float f4 = Math.abs((float)(vec3.y - eyePosition.y));
            float f5 = (float)(vec3.z - eyePosition.z);
            float f6 = (float)eyePosition.distanceTo(vec3);
            float hypot = this.hypot(f3, f5);
            boolean flag = floatValue3 <= 0.0F && floatValue4 >= 0.0F;
            boolean flag2 = floatValue5 <= 0.0F && floatValue6 >= 0.0F;
            float f7 = 0.0F;
            if (flag && Math.abs(floatValue7) > 1.0E-6F) {
               f7 = Mth.clamp((0.0F - floatValue3) / floatValue7, 0.0F, 1.0F);
            }

            float f8 = 0.0F;
            if (flag2 && Math.abs(floatValue8) > 1.0E-6F) {
               f8 = Mth.clamp((0.0F - floatValue5) / floatValue8, 0.0F, 1.0F);
            }

            boolean flag3 = flag && flag2;
            this.floatValue7 = flag3 ? Mth.clamp(this.floatValue7 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.floatValue8 = flag3 ? 0.0F : Math.min(this.floatValue8 + 1.0F, 8.0F);
            float f9 = f - this.floatValue3;
            float f10 = f2 - this.floatValue4;
            float f11 = f9 - this.floatValue5;
            float f12 = f10 - this.floatValue6;
            float f13 = this.entry != null ? this.method3858(this.entry.floatValues[0], this.entry.floatValue) : floatValue;
            float f14 = this.entry != null ? this.method3858(this.entry.floatValues[1], this.entry.floatValue) : y2;
            float floatValue9 = this.entry != null ? this.entry.floatValues[16] : f6;
            float floatValue10 = Mth.wrapDegrees(floatValue - f13);
            float floatValue11 = y2 - f14;
            float f15 = f6 - floatValue9;
            float f16 = floatValue / Math.max(Math.abs(floatValue7), 0.1F);
            float f17 = y2 / Math.max(Math.abs(floatValue8), 0.1F);
            float floatValue12 = (float)Math.sqrt(floatValue * floatValue + y2 * y2);
            float f18 = (float)Math.atan2(y2, floatValue);
            float f19 = (float)Math.sin(f18);
            float f20 = (float)Math.cos(f18);
            float f21 = this.method3857(f, y);
            float f22 = this.method3857(f2, y);
            float f23 = this.method3856(floatValue, y);
            float f24 = this.method3856(y2, y);
            float f25 = 0.0F;
            float f26 = 0.0F;
            float f27 = 0.0F;
            float f28 = 0.0F;
            if (this.entry != null) {
               float floatValue13 = this.entry.floatValues[0];
               float floatValue14 = this.entry.floatValues[1];
               float f29 = floatValue13 - f21;
               float f30 = floatValue14 - f22;
               f25 = Math.abs(floatValue13) - Math.abs(f29);
               f26 = Math.abs(floatValue14) - Math.abs(f30);
               f27 = f23 - f29;
               f28 = f24 - f30;
            }

            this.floatValue11 = f21 != 0.0F ? this.floatValue11 + 1.0F : 0.0F;
            this.floatValue12 = f22 != 0.0F ? this.floatValue12 + 1.0F : 0.0F;
            int n = this.method3859(f21);
            int n2 = this.method3859(f22);
            this.floatValue15 = this.method3861(f21, n, this.intValue2, this.floatValue15);
            this.floatValue16 = this.method3861(f22, n2, this.intValue3, this.floatValue16);
            float f29 = this.method3862(f21, this.floatValue15);
            float f30 = this.method3862(f22, this.floatValue16);
            this.floatValue13 = this.method3860(n, this.intValue2, this.floatValue13);
            this.floatValue14 = this.method3860(n2, this.intValue3, this.floatValue14);
            this.intValue2 = n != 0 ? n : this.intValue2;
            this.intValue3 = n2 != 0 ? n2 : this.intValue3;
            float f31 = mc.player.input.keyPresses.forward() ? 1.0F : 0.0F;
            float f32 = mc.player.input.keyPresses.backward() ? 1.0F : 0.0F;
            float f33 = mc.player.input.keyPresses.left() ? 1.0F : 0.0F;
            float f34 = mc.player.input.keyPresses.right() ? 1.0F : 0.0F;
            float f35 = mc.player.input.keyPresses.jump() ? 1.0F : 0.0F;
            if (flag3) {
               this.floatValue9 = Mth.clamp(this.flag2 ? this.floatValue9 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.floatValue10 = 0.0F;
            } else {
               this.floatValue9 = 0.0F;
               this.floatValue10 = Mth.clamp(this.flag2 ? 1.0F : this.floatValue10 + 1.0F, 0.0F, 8.0F);
            }

            float[] floats = new float[]{
               f23,
               f24,
               this.method3856(floatValue3, y),
               this.method3856(floatValue4, y),
               this.method3856(floatValue5, y),
               this.method3856(floatValue6, y),
               this.method3856(floatValue7, y),
               this.method3856(floatValue8, y),
               f16,
               f17,
               this.method3856(floatValue12, y),
               f19,
               f20,
               f7,
               f8,
               flag3 ? 1.0F : 0.0F,
               f6,
               hypot,
               f4,
               this.floatValue7,
               this.floatValue8,
               this.floatValue9,
               this.floatValue10,
               this.method3856(floatValue10, y),
               this.method3856(floatValue11, y),
               f15,
               f27,
               f28,
               this.method3856(floatValue2, y),
               this.method3856(y3, y),
               (float)vec.x,
               (float)vec.y,
               (float)vec.z,
               f21,
               f22,
               f21,
               f22,
               this.method3857(f9, y),
               this.method3857(f10, y),
               this.method3857(f11, y),
               this.method3857(f12, y),
               this.floatValue11,
               this.floatValue12,
               this.floatValue13,
               this.floatValue14,
               f25,
               f26,
               f29,
               f30,
               this.floatValue15,
               this.floatValue16,
               f31,
               f32,
               f33,
               f34,
               f35,
               (float)vec2.x,
               (float)vec2.y,
               (float)vec2.z
            };
            if (!this.method3865(floats)) {
               return null;
            } else {
               this.floatValue3 = f;
               this.floatValue4 = f2;
               this.floatValue5 = f9;
               this.floatValue6 = f10;
               this.flag2 = flag3;
               return floats;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Rotation method3849(Vec3 eyePosition, Vec3 vec) {
      return RotationUtils.method6912(vec.subtract(eyePosition));
   }

   private Vec3 method3850(Rotation rotation, Vec3 eyePosition) {
      Vec3 position = mc.player.position();
      Vec3 vec = eyePosition.subtract(position);

      try {
         PlayerSimulation playerSimulation = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(mc.player.input.keyPresses));
         playerSimulation.floatValue = rotation.getFloatValue();
         playerSimulation.floatValue2 = rotation.getFloatValue2();
         playerSimulation.tick();
         playerSimulation.tick();
         Vec3 deltaMovement = playerSimulation.vec3d.add(vec);
         return this.method3866(deltaMovement) ? deltaMovement : eyePosition;
      } catch (Throwable e) {
         return eyePosition;
      }
   }

   private AABB method3851(LivingEntity livingEntity, AABB boundingBox, Vec3 vec) {
      if (livingEntity instanceof Player player) {
         try {
            PlayerSimulation playerSimulation = PlayerSimulation.method6034(player, PlayerSimulation.Entry.method6093(player));
            playerSimulation.tick();
            playerSimulation.tick();
            if (playerSimulation.box != null && this.method3866(playerSimulation.vec3d)) {
               return playerSimulation.box;
            }
         } catch (Throwable e) {
         }
      }

      Vec3 deltaMovement = vec == null ? Vec3.ZERO : vec.scale(2.0);
      return this.method3866(deltaMovement) ? boundingBox.move(deltaMovement) : boundingBox;
   }

   private LivingEntity method3852() {
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      return livingEntity != null && livingEntity.isAlive() ? livingEntity : null;
   }

   private Vec3 method3853(Vec3 position) {
      if (this.entry != null && position != null) {
         Vec3 deltaMovement = position.subtract(this.entry.vec3d);
         if (this.method3866(deltaMovement) && deltaMovement.lengthSqr() > 1.0E-10) {
            return deltaMovement;
         }
      }

      Vec3 deltaMovement = mc.player.getDeltaMovement();
      return this.method3866(deltaMovement) ? deltaMovement : Vec3.ZERO;
   }

   private Vec3 method3854(LivingEntity livingEntity, AABB boundingBox) {
      if (this.entry != null && boundingBox != null && this.entry.box != null) {
         Vec3 deltaMovement = this.method3855(boundingBox).subtract(this.method3855(this.entry.box));
         if (this.method3866(deltaMovement) && deltaMovement.lengthSqr() > 1.0E-10) {
            return deltaMovement;
         }
      }

      Vec3 deltaMovement = livingEntity == null ? Vec3.ZERO : livingEntity.getDeltaMovement();
      return this.method3866(deltaMovement) ? deltaMovement : Vec3.ZERO;
   }

   private Vec3 method3855(AABB boundingBox) {
      return new Vec3((boundingBox.minX + boundingBox.maxX) * 0.5, (boundingBox.minY + boundingBox.maxY) * 0.5, (boundingBox.minZ + boundingBox.maxZ) * 0.5);
   }

   private float hypot(float f, float f2) {
      return (float)Math.sqrt(f * f + f2 * f2);
   }

   private float method3856(float floatValue, float y) {
      return floatValue / y;
   }

   private float method3857(float f, float y) {
      return Math.round(f / y);
   }

   private float method3858(float floatValue, float y) {
      return this.isFinite(y) && y > 0.0F ? floatValue * y : floatValue;
   }

   private int method3859(float f) {
      if (f > 0.0F) {
         return 1;
      } else {
         return f < 0.0F ? -1 : 0;
      }
   }

   private float method3860(int n, int n2, float f) {
      return n != 0 && n2 != 0 && n != n2 ? 0.0F : f + 1.0F;
   }

   private float method3861(float f, int n, int n2, float f2) {
      float f3 = Math.abs(f);
      if (n == 0) {
         return 0.0F;
      } else {
         return n2 != 0 && n == n2 ? Math.max(f2, f3) : f3;
      }
   }

   private float method3862(float f, float f2) {
      return f2 > 0.0F ? Math.abs(f) / f2 : 0.0F;
   }

   private float method3863(float floatValue, float f) {
      float angle = floatValue - f;
      return !Float.isFinite(angle) ? floatValue : f + Mth.wrapDegrees(angle);
   }

   private void method3864() {
      this.intValue = Integer.MIN_VALUE;
      this.flag = false;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.flag2 = false;
      this.floatValue7 = 0.0F;
      this.floatValue8 = 0.0F;
      this.floatValue9 = 0.0F;
      this.floatValue10 = 0.0F;
      this.floatValue11 = 0.0F;
      this.floatValue12 = 0.0F;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.floatValue15 = 0.0F;
      this.floatValue16 = 0.0F;
      this.entry = null;
      this.floatValues = null;
      if (this.resettable != null) {
         this.resettable.reset();
      }
   }

   private boolean method3865(float[] floats) {
      if (floats == null) {
         return false;
      } else {
         for (float y : floats) {
            if (!this.isFinite(y)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isFinite(float y) {
      return !Float.isNaN(y) && !Float.isInfinite(y);
   }

   private boolean method3866(Vec3 deltaMovement) {
      return deltaMovement != null && this.isFinite((float)deltaMovement.x) && this.isFinite((float)deltaMovement.y) && this.isFinite((float)deltaMovement.z);
   }

   private static final class Entry {
      private final float[] floatValues;
      private final Vec3 vec3d;
      private final AABB box;
      private final float floatValue;

      private Entry(float[] floats, Vec3 vec3d, AABB box, float floatValue) {
         this.floatValues = (float[])floats.clone();
         this.vec3d = vec3d;
         this.box = box;
         this.floatValue = floatValue;
      }
   }
}

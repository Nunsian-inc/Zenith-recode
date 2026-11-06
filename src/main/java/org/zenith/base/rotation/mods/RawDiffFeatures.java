package org.zenith.base.rotation.mods;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.JointCodebook;
import org.zenith.utility.misc.GownoRotationConfig;
import org.zenith.utility.misc.RotationContext;
import org.zenith.utility.misc.Vec2f;

public class RawDiffFeatures extends RotationContext {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 176.0F;
   private static final float CONST_floatValue3 = 8.0F;
   private static final float CONST_floatValue4 = 8.0F;
   private static final float CONST_floatValue5 = 20.0F;
   private static final float CONST_floatValue6 = 0.18F;
   private static final float CONST_floatValue7 = 0.055F;
   private static final float CONST_floatValue8 = 0.08F;
   private static final float CONST_floatValue9 = 0.14F;
   private static final float CONST_floatValue10 = 6.0F;
   private static final float CONST_floatValue11 = 1.15F;
   private static final float CONST_floatValue12 = 1.0F;
   private static final float CONST_floatValue13 = 8.0F;
   private static final float CONST_floatValue14 = 4.0F;
   private static final float CONST_floatValue15 = 3.0F;
   private static final int CONST_intValue2 = 2;
   private static final float CONST_floatValue16 = 6.0F;
   private static final float CONST_floatValue17 = 1.0F;
   private static final float CONST_floatValue18 = 2.0F;
   private static final float CONST_floatValue19 = 0.12F;
   private static final float CONST_floatValue20 = 18.0F;
   private static final float CONST_floatValue21 = 10.0F;
   private boolean flag = false;
   private int intValue = Integer.MIN_VALUE;
   private float[] floatValues = null;
   private JointCodebook.Entry7 entry7 = null;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private int intValue2 = 0;
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
   private boolean flag3 = false;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 0.0F;
   private float floatValue15 = 0.0F;
   private float floatValue16 = 0.0F;
   private double doubleValue = 0.0;
   private double doubleValue2 = 0.0;
   private double doubleValue3 = 0.0;
   private double doubleValue4 = 0.0;
   private double doubleValue5 = 0.0;
   private double doubleValue6 = 0.0;
   private boolean flag4 = false;
   private float floatValue17 = 0.0F;
   private float floatValue18 = 0.0F;
   private float floatValue19 = 0.0F;
   private float floatValue20 = 0.0F;
   private float floatValue21 = 0.0F;
   private float floatValue22 = 0.0F;

   public Rotation method3699(GownoRotationConfig class2, Rotation rotation) {
      return this.method3700(class2, rotation, this.method3719());
   }

   public Rotation method3700(GownoRotationConfig class2, Rotation rotation, LivingEntity livingEntity) {
      Rotation rotation2 = rotationManager.getEffectiveRotation();
      this.method3720();
      if (!jointCodebook.method2545()) {
         this.method3721();
         return rotation2;
      } else {
         mc.player.tick();
         if (rotation == null || rotation.isInvalid()) {
            this.method3721();
            return rotation2;
         } else if (livingEntity != null && livingEntity.isAlive()) {
            int id = livingEntity.getId();
            if (this.intValue != id) {
               this.method3721();
               this.intValue = id;
            }

            RawDiffFeatures.Entry entry = this.method3705(rotation2, livingEntity);
            if (entry == null) {
               this.method3721();
               return rotation2;
            } else {
               String[] strings = jointCodebook.method2539();
               float[] floatValues = this.method3703(entry, strings);
               int n = jointCodebook.method2538();
               if (floatValues != null && floatValues.length == n && this.method3725(floatValues)) {
                  this.floatValues = (float[])floatValues.clone();
                  JointCodebook.Entry7 entry7 = jointCodebook.method2546(floatValues, entry.floatValue25, entry.floatValue26);
                  if (entry7 == null) {
                     this.entry7 = null;
                     return rotation2;
                  } else {
                     RawDiffFeatures.Entry2 entry2 = this.method3707(entry7.getFloatValue(), entry7.getFloatValue2());
                     if (entry2 == null) {
                        this.entry7 = null;
                        return rotation2;
                     } else {
                        RawDiffFeatures.Entry2 entry22 = this.method3708(entry2);
                        if (entry22 != null && this.isFinite(entry22.floatValue) && this.isFinite(entry22.floatValue2)) {
                           entry22 = this.method3709(entry, entry22);
                           if (entry22 != null && this.isFinite(entry22.floatValue) && this.isFinite(entry22.floatValue2)) {
                              this.entry7 = entry7.method2593(
                                 entry22.floatValue,
                                 entry22.floatValue2,
                                 entry.floatValue25,
                                 entry.floatValue26,
                                 entry22.floatValue3,
                                 entry22.floatValue4,
                                 entry22.floatValue5,
                                 entry22.floatValue6,
                                 entry22.floatValue7,
                                 entry22.floatValue8
                              );
                              this.floatValue5 = this.floatValue3;
                              this.floatValue6 = this.floatValue4;
                              this.floatValue3 = entry22.floatValue5;
                              this.floatValue4 = entry22.floatValue6;
                              Rotation rotation3 = rotation2.add(entry22.floatValue, entry22.floatValue2);
                              return new Rotation(rotation3.getFloatValue(), Mth.clamp(rotation3.getFloatValue2(), -90.0F, 90.0F));
                           } else {
                              this.entry7 = null;
                              return rotation2;
                           }
                        } else {
                           this.entry7 = null;
                           return rotation2;
                        }
                     }
                  }
               } else {
                  this.method3721();
                  return rotation2;
               }
            }
         } else {
            this.method3721();
            return rotation2;
         }
      }
   }

   public JointCodebook.Entry7 getEntry7() {
      return this.entry7;
   }

   public float[] method3701() {
      return this.floatValues == null ? null : (float[])this.floatValues.clone();
   }

   public void method3702() {
      this.method3721();
   }

   private float[] method3703(RawDiffFeatures.Entry entry, String[] strings) {
      if (entry != null && strings != null && strings.length != 0) {
         float[] floats = new float[strings.length];

         for (int i = 0; i < strings.length; i++) {
            float floatValue = this.method3704(entry, strings[i]);
            if (!this.isFinite(floatValue)) {
               return null;
            }

            floats[i] = floatValue;
         }

         return floats;
      } else {
         return null;
      }
   }

   private float method3704(RawDiffFeatures.Entry entry, String str) {
      if (str == null) {
         return Float.NaN;
      } else {
         switch (str) {
            case "raw_center_Diffyaw":
               return this.method3717(entry.floatValue, entry.floatValue45);
            case "raw_center_Diffpitch":
               return this.method3717(entry.floatValue2, entry.floatValue45);
            case "raw_min_Diffyaw":
               return this.method3717(entry.floatValue3, entry.floatValue45);
            case "raw_max_Diffyaw":
               return this.method3717(entry.floatValue4, entry.floatValue45);
            case "raw_min_Diffpitch":
               return this.method3717(entry.floatValue5, entry.floatValue45);
            case "raw_max_Diffpitch":
               return this.method3717(entry.floatValue6, entry.floatValue45);
            case "raw_width_Diffyaw":
               return this.method3717(entry.floatValue7, entry.floatValue45);
            case "raw_height_Diffpitch":
               return this.method3717(entry.floatValue8, entry.floatValue45);
            case "raw_target_x_norm":
               return entry.floatValue9;
            case "raw_target_y_norm":
               return entry.floatValue10;
            case "raw_distance":
               return entry.floatValue12;
            case "raw_delta_center_Diffyaw":
               return this.method3717(entry.floatValue16, entry.floatValue45);
            case "raw_delta_center_Diffpitch":
               return this.method3717(entry.floatValue17, entry.floatValue45);
            case "raw_delta_distance":
               return entry.floatValue18;
            case "raw_inside_box":
               return entry.flag ? 1.0F : 0.0F;
            case "raw_inside_ticks_clamped":
               return entry.floatValue14;
            case "outside_box_ticks":
               return entry.floatValue15;
            case "yaw_vel":
               return this.method3716(entry.floatValue25, entry.floatValue45);
            case "pitch_vel":
               return this.method3716(entry.floatValue26, entry.floatValue45);
            case "yaw_acc":
               return this.method3716(entry.floatValue27, entry.floatValue45);
            case "pitch_acc":
               return this.method3716(entry.floatValue28, entry.floatValue45);
            case "normalized_center_yaw":
               return entry.floatValue31;
            case "normalized_center_pitch":
               return entry.floatValue32;
            case "yaw_jerk":
               return this.method3716(entry.floatValue29, entry.floatValue45);
            case "pitch_jerk":
               return this.method3716(entry.floatValue30, entry.floatValue45);
            case "ang_dist":
               return this.method3717(entry.floatValue33, entry.floatValue45);
            case "dir_sin":
               return entry.floatValue34;
            case "dir_cos":
               return entry.floatValue35;
            case "t_since_entered_box":
               return entry.floatValue36;
            case "t_since_left_box":
               return entry.floatValue37;
            case "effort_fatigue":
               return entry.floatValue38;
            case "active_burst_ticks":
               return entry.floatValue39;
            case "input_forward":
               return entry.floatValue40;
            case "input_backward":
               return entry.floatValue41;
            case "input_left":
               return entry.floatValue42;
            case "input_right":
               return entry.floatValue43;
            case "input_jump":
               return entry.floatValue44;
            case "diff_y":
               return entry.floatValue11;
            case "distance_xz":
               return entry.floatValue13;
            case "target_motion_x":
               return entry.floatValue19;
            case "target_motion_y":
               return entry.floatValue20;
            case "target_motion_z":
               return entry.floatValue21;
            case "player_motion_x":
               return entry.floatValue22;
            case "player_motion_y":
               return entry.floatValue23;
            case "player_motion_z":
               return entry.floatValue24;
            case "v18_geometry_unstable":
               return entry.floatValue46;
            case "v18_geometry_confidence":
               return entry.floatValue47;
            case "v18_inside_confidence":
               return entry.floatValue48;
            case "v18_close_overlap":
               return entry.floatValue49;
            case "v18_center_absmax":
               return entry.floatValue50;
            case "v18_prev_vel_absmax":
               return entry.floatValue51;
            case "v18_nearest_yaw_error":
               return entry.floatValue52;
            case "v18_nearest_pitch_error":
               return entry.floatValue53;
            case "v18_aim_yaw_error":
               return entry.floatValue54;
            case "v18_aim_pitch_error":
               return entry.floatValue55;
            case "prev_action_yaw_count":
               return entry.floatValue56;
            case "prev_action_pitch_count":
               return entry.floatValue57;
            case "prev2_action_yaw_count":
               return entry.floatValue58;
            case "prev2_action_pitch_count":
               return entry.floatValue59;
            default:
               return Float.NaN;
         }
      }
   }

   private RawDiffFeatures.Entry method3705(Rotation rotation, LivingEntity livingEntity) {
      if (rotation != null && livingEntity != null) {
         AABB boundingBox = livingEntity.getBoundingBox();
         Vec3 position = mc.player.position();
         Vec3 eyePosition = mc.player.getEyePosition();
         Vec3 center = boundingBox.getCenter();
         Rotation rotation2 = RotationUtils.method6912(center.subtract(eyePosition));
         Vec2f vec2f = rotation.wrappedDeltaTo(rotation2);
         float floatValue = vec2f.getFloatValue();
         float y = vec2f.getFloatValue2();
         if (this.isFinite(floatValue) && this.isFinite(y)) {
            float f = Float.MAX_VALUE;
            float floatValue2 = -Float.MAX_VALUE;
            float y2 = Float.MAX_VALUE;
            float y3 = -Float.MAX_VALUE;
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

            for (Vec3 vec : vecs) {
               Rotation rotation3 = RotationUtils.method6912(vec.subtract(eyePosition));
               Vec2f vec2f2 = rotation.wrappedDeltaTo(rotation3);
               float floatValue3 = vec2f2.getFloatValue2();
               if (this.isFinite(vec2f2.getFloatValue()) && this.isFinite(floatValue3)) {
                  float f2 = this.method3718(vec2f2.getFloatValue(), floatValue);
                  f = Math.min(f, f2);
                  floatValue2 = Math.max(floatValue2, f2);
                  y2 = Math.min(y2, floatValue3);
                  y3 = Math.max(y3, floatValue3);
               }
            }

            if (f == Float.MAX_VALUE) {
               f = floatValue;
               floatValue2 = floatValue;
               y2 = y;
               y3 = y;
            }

            float f2 = floatValue2 - f;
            float f3 = y3 - y2;
            boolean flag = f <= 0.0F && floatValue2 >= 0.0F;
            boolean flag2 = y2 <= 0.0F && y3 >= 0.0F;
            boolean flag3 = flag && flag2;
            float f4 = 0.0F;
            if (flag && Math.abs(f2) > 1.0E-6F) {
               f4 = Mth.clamp((0.0F - f) / f2, 0.0F, 1.0F);
            }

            float f5 = 0.0F;
            if (flag2 && Math.abs(f3) > 1.0E-6F) {
               f5 = Mth.clamp((0.0F - y2) / f3, 0.0F, 1.0F);
            }

            float f6 = 0.0F;
            float f7 = 0.0F;
            if (this.flag2) {
               f6 = Mth.wrapDegrees(rotation.getFloatValue() - this.floatValue7);
               f7 = rotation.getFloatValue2() - this.floatValue8;
            }

            float f8 = f6 - this.floatValue9;
            float f9 = f7 - this.floatValue10;
            float f10 = f8 - this.floatValue11;
            float f11 = f9 - this.floatValue12;
            this.floatValue17 = flag3 ? Mth.clamp(this.floatValue17 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.floatValue18 = flag3 ? 0.0F : Math.min(this.floatValue18 + 1.0F, 8.0F);
            if (flag3) {
               this.floatValue19 = Mth.clamp(this.flag4 ? this.floatValue19 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.floatValue20 = 0.0F;
            } else {
               this.floatValue19 = 0.0F;
               this.floatValue20 = Mth.clamp(this.flag4 ? 1.0F : this.floatValue20 + 1.0F, 0.0F, 8.0F);
            }

            this.method3706(f2, f3, f6, f7, flag3);
            float hypot = (float)(center.x - eyePosition.x);
            float y4 = Math.abs((float)(center.y - eyePosition.y));
            float y5 = (float)(center.z - eyePosition.z);
            float hypot2 = this.hypot(hypot, y5);
            float hypot3 = this.hypot(hypot2, y4);
            float hypot4 = this.flag3 ? this.hypot(this.floatValue15, this.floatValue16) : hypot3;
            float f12 = this.flag3 ? Mth.wrapDegrees(floatValue - this.floatValue13) : 0.0F;
            float f13 = this.flag3 ? y - this.floatValue14 : 0.0F;
            float f14 = hypot3 - hypot4;
            Vec3 deltaMovement = this.flag3 ? center.subtract(this.doubleValue, this.doubleValue2, this.doubleValue3) : Vec3.ZERO;
            if (!this.method3726(deltaMovement) || deltaMovement.lengthSqr() <= 1.0E-10) {
               Vec3 vec = livingEntity.getDeltaMovement();
               deltaMovement = this.method3726(vec) ? vec : Vec3.ZERO;
            }

            Vec3 vec = this.flag3 ? position.subtract(this.doubleValue4, this.doubleValue5, this.doubleValue6) : Vec3.ZERO;
            if (!this.method3726(vec) || vec.lengthSqr() <= 1.0E-10) {
               Vec3 deltaMovement2 = mc.player.getDeltaMovement();
               vec = this.method3726(deltaMovement2) ? deltaMovement2 : Vec3.ZERO;
            }

            float floatValue3 = jointCodebook.method2540();
            if (this.isFinite(floatValue3) && !(floatValue3 <= 0.0F)) {
               RawDiffFeatures.Entry entry = new RawDiffFeatures.Entry();
               entry.floatValue = floatValue;
               entry.floatValue2 = y;
               entry.floatValue3 = f;
               entry.floatValue4 = floatValue2;
               entry.floatValue5 = y2;
               entry.floatValue6 = y3;
               entry.floatValue7 = f2;
               entry.floatValue8 = f3;
               entry.floatValue9 = f4;
               entry.floatValue10 = f5;
               entry.floatValue11 = y4;
               entry.floatValue12 = hypot3;
               entry.floatValue13 = hypot2;
               entry.flag = flag3;
               entry.floatValue14 = this.floatValue17;
               entry.floatValue15 = this.floatValue18;
               entry.floatValue16 = f12;
               entry.floatValue17 = f13;
               entry.floatValue18 = f14;
               entry.floatValue19 = (float)deltaMovement.x;
               entry.floatValue20 = (float)deltaMovement.y;
               entry.floatValue21 = (float)deltaMovement.z;
               entry.floatValue22 = (float)vec.x;
               entry.floatValue23 = (float)vec.y;
               entry.floatValue24 = (float)vec.z;
               entry.floatValue25 = f6;
               entry.floatValue26 = f7;
               entry.floatValue27 = f8;
               entry.floatValue28 = f9;
               entry.floatValue29 = f10;
               entry.floatValue30 = f11;
               entry.floatValue31 = floatValue / Math.max(Math.abs(f2), 0.1F);
               entry.floatValue32 = y / Math.max(Math.abs(f3), 0.1F);
               entry.floatValue33 = this.hypot(floatValue, y);
               float f15 = (float)Math.atan2(y, floatValue);
               entry.floatValue34 = (float)Math.sin(f15);
               entry.floatValue35 = (float)Math.cos(f15);
               entry.floatValue36 = this.floatValue19;
               entry.floatValue37 = this.floatValue20;
               entry.floatValue38 = this.floatValue21;
               entry.floatValue39 = this.floatValue22;
               entry.floatValue40 = mc.player.input.keyPresses.forward() ? 1.0F : 0.0F;
               entry.floatValue41 = mc.player.input.keyPresses.backward() ? 1.0F : 0.0F;
               entry.floatValue42 = mc.player.input.keyPresses.left() ? 1.0F : 0.0F;
               entry.floatValue43 = mc.player.input.keyPresses.right() ? 1.0F : 0.0F;
               entry.floatValue44 = mc.player.input.keyPresses.jump() ? 1.0F : 0.0F;
               entry.floatValue45 = floatValue3;
               entry.floatValue56 = this.floatValue3;
               entry.floatValue57 = this.floatValue4;
               entry.floatValue58 = this.floatValue5;
               entry.floatValue59 = this.floatValue6;
               this.method3722(entry);
               this.flag2 = true;
               this.floatValue7 = rotation.getFloatValue();
               this.floatValue8 = rotation.getFloatValue2();
               this.floatValue9 = f6;
               this.floatValue10 = f7;
               this.floatValue11 = f8;
               this.floatValue12 = f9;
               this.flag3 = true;
               this.floatValue13 = floatValue;
               this.floatValue14 = y;
               this.floatValue15 = hypot2;
               this.floatValue16 = y4;
               this.doubleValue = center.x;
               this.doubleValue2 = center.y;
               this.doubleValue3 = center.z;
               this.doubleValue4 = position.x;
               this.doubleValue5 = position.y;
               this.doubleValue6 = position.z;
               this.flag4 = flag3;
               return entry;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void method3706(float f, float f2, float f3, float f4, boolean flag) {
      float f5 = Math.max(Math.abs(f) * 0.4F, 0.25F);
      float f6 = Math.max(Math.abs(f2) * 0.4F, 0.18F);
      float f7 = (float)Math.sqrt(this.method3724(f3 / f5) + this.method3724(f4 / f6));
      f7 = Math.min(f7, 1.5F);
      if (f7 > 0.18F) {
         this.floatValue22 = Math.min(this.floatValue22 + 1.0F, 20.0F);
         this.floatValue21 = Math.min(this.floatValue21 + f7 * 0.055F, 1.0F);
      } else {
         this.floatValue22 = Math.max(this.floatValue22 - 2.0F, 0.0F);
         float f8 = flag ? 0.14F : 0.08F;
         this.floatValue21 = Math.max(this.floatValue21 - f8, 0.0F);
      }
   }

   private RawDiffFeatures.Entry2 method3707(float floatValue, float f) {
      return this.isFinite(floatValue) && this.isFinite(f) ? new RawDiffFeatures.Entry2(floatValue, f) : null;
   }

   private RawDiffFeatures.Entry2 method3708(RawDiffFeatures.Entry2 entry2) {
      if (entry2 == null) {
         return null;
      } else {
         float floatValue = jointCodebook.method2540();
         if (this.isFinite(floatValue) && !(floatValue <= 0.0F)) {
            float f = entry2.floatValue / floatValue + this.floatValue;
            float floatValue2 = entry2.floatValue2 / floatValue + this.floatValue2;
            if (this.isFinite(f) && this.isFinite(floatValue2)) {
               int n = Math.round(f);
               int n2 = Math.round(floatValue2);
               this.floatValue = f - n;
               this.floatValue2 = floatValue2 - n2;
               return new RawDiffFeatures.Entry2(n * floatValue, n2 * floatValue, f, floatValue2, n, n2, this.floatValue, this.floatValue2);
            } else {
               this.floatValue = 0.0F;
               this.floatValue2 = 0.0F;
               return null;
            }
         } else {
            this.floatValue = 0.0F;
            this.floatValue2 = 0.0F;
            return null;
         }
      }
   }

   private RawDiffFeatures.Entry2 method3709(RawDiffFeatures.Entry entry, RawDiffFeatures.Entry2 entry2) {
      if (entry != null && entry2 != null) {
         RawDiffFeatures.Entry2 entry22 = this.method3710(entry, entry2);
         if (entry22 != null) {
            return entry22;
         } else {
            RawDiffFeatures.Entry2 entry23 = this.method3713(entry, entry2);
            return entry23 != null ? entry23 : entry2;
         }
      } else {
         return entry2;
      }
   }

   private RawDiffFeatures.Entry2 method3710(RawDiffFeatures.Entry entry, RawDiffFeatures.Entry2 entry2) {
      if (entry != null && entry2 != null && !entry.flag && !(entry.floatValue15 < 3.0F)) {
         float sign = entry.floatValue52;
         float f = entry.floatValue53;
         float floatValue = Math.max(Math.abs(sign), Math.abs(f));
         if (this.isFinite(floatValue) && !(floatValue < 6.0F)) {
            boolean flag = this.method3711(sign, entry2.floatValue5);
            boolean flag2 = this.method3711(f, entry2.floatValue6);
            if (!flag && !flag2) {
               this.intValue2 = 0;
               return null;
            } else {
               this.intValue2++;
               if (this.intValue2 < 2) {
                  return null;
               } else {
                  this.intValue2 = 0;
                  float floatValue2 = flag ? this.method3712(sign, 18.0F) : entry2.floatValue5;
                  float f2 = flag2 ? this.method3712(f, 10.0F) : entry2.floatValue6;
                  return this.method3715(floatValue2, f2);
               }
            }
         } else {
            this.intValue2 = 0;
            return null;
         }
      } else {
         this.intValue2 = 0;
         return null;
      }
   }

   private boolean method3711(float sign, float floatValue) {
      if (!this.isFinite(sign) || Math.abs(sign) < 6.0F) {
         return false;
      } else {
         return this.isFinite(floatValue) && !(Math.abs(floatValue) <= 1.0F) ? Math.signum(sign) != Math.signum(floatValue) : true;
      }
   }

   private float method3712(float sign, float max) {
      float f = Mth.clamp(Math.abs(sign) * 0.12F, 2.0F, max);
      return Math.copySign((float)Math.round(f), sign);
   }

   private RawDiffFeatures.Entry2 method3713(RawDiffFeatures.Entry entry, RawDiffFeatures.Entry2 entry2) {
      if (entry != null && entry2 != null && entry.flag && !(entry.floatValue14 < 6.0F)) {
         float floatValue = entry2.floatValue5;
         float f = entry2.floatValue6;
         boolean flag = false;
         if (this.method3714(floatValue, entry2.floatValue3, entry.floatValue56, 1.15F, 8.0F)) {
            floatValue = 0.0F;
            flag = true;
         }

         if (this.method3714(f, entry2.floatValue4, entry.floatValue57, 1.0F, 4.0F)) {
            f = 0.0F;
            flag = true;
         }

         return flag ? this.method3715(floatValue, f) : null;
      } else {
         return null;
      }
   }

   private boolean method3714(float floatValue, float f, float floatValue2, float f2, float f3) {
      return this.isFinite(floatValue) && this.isFinite(f) && this.isFinite(floatValue2) && Math.abs(floatValue) == 1.0F && Math.abs(f) <= f2 && Math.abs(floatValue2) <= f3;
   }

   private RawDiffFeatures.Entry2 method3715(float floatValue, float f) {
      float floatValue2 = jointCodebook.method2540();
      if (this.isFinite(floatValue2) && !(floatValue2 <= 0.0F) && this.isFinite(floatValue) && this.isFinite(f)) {
         floatValue = Math.round(floatValue);
         f = Math.round(f);
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
         return new RawDiffFeatures.Entry2(floatValue * floatValue2, f * floatValue2, floatValue, f, floatValue, f, 0.0F, 0.0F);
      } else {
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
         return null;
      }
   }

   private float method3716(float floatValue, float f) {
      return this.isFinite(floatValue) && this.isFinite(f) && !(f <= 0.0F) ? Math.round(floatValue / f) : 0.0F;
   }

   private float method3717(float floatValue, float f) {
      return this.isFinite(floatValue) && this.isFinite(f) && !(f <= 0.0F) ? floatValue / f : 0.0F;
   }

   private float method3718(float floatValue, float f) {
      float angle = floatValue - f;
      return !Float.isFinite(angle) ? floatValue : f + Mth.wrapDegrees(angle);
   }

   private LivingEntity method3719() {
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      return livingEntity != null && livingEntity.isAlive() ? livingEntity : null;
   }

   private void method3720() {
      if (!jointCodebook.isLoaded() && !this.flag) {
         this.flag = true;
         jointCodebook.method2529();
      }
   }

   private void method3721() {
      jointCodebook.method2536();
      this.intValue = Integer.MIN_VALUE;
      this.floatValues = null;
      this.entry7 = null;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.intValue2 = 0;
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
      this.flag3 = false;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.floatValue15 = 0.0F;
      this.floatValue16 = 0.0F;
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.doubleValue5 = 0.0;
      this.doubleValue6 = 0.0;
      this.flag4 = false;
      this.floatValue17 = 0.0F;
      this.floatValue18 = 0.0F;
      this.floatValue19 = 0.0F;
      this.floatValue20 = 0.0F;
      this.floatValue21 = 0.0F;
      this.floatValue22 = 0.0F;
   }

   private void method3722(RawDiffFeatures.Entry entry) {
      if (entry != null) {
         float f = jointCodebook.method2541();
         float f2 = jointCodebook.method2542();
         float max = jointCodebook.method2543();
         float f3 = Math.max(0.05F, jointCodebook.method2544());
         float f4 = this.method3717(entry.floatValue, entry.floatValue45);
         float f5 = this.method3717(entry.floatValue2, entry.floatValue45);
         float f6 = this.method3717(entry.floatValue3, entry.floatValue45);
         float f7 = this.method3717(entry.floatValue4, entry.floatValue45);
         float f8 = this.method3717(entry.floatValue5, entry.floatValue45);
         float f9 = this.method3717(entry.floatValue6, entry.floatValue45);
         float f10 = Math.abs(this.method3717(entry.floatValue7, entry.floatValue45));
         float f11 = Math.abs(this.method3717(entry.floatValue8, entry.floatValue45));
         float f12 = Math.max(Math.abs(f4), Math.abs(f5));
         float f13 = Math.max(
            Math.abs(this.method3716(entry.floatValue25, entry.floatValue45)), Math.abs(this.method3716(entry.floatValue26, entry.floatValue45))
         );
         boolean flag = entry.flag;
         float f14 = entry.floatValue13;
         float f15 = Mth.clamp(f / Math.max(f10, 1.0E-6F), 0.0F, 1.0F);
         float f16 = Mth.clamp(f2 / Math.max(f11, 1.0E-6F), 0.0F, 1.0F);
         float f17 = flag ? Mth.clamp(max / Math.max(f12, 1.0E-6F), 0.0F, 1.0F) : 1.0F;
         float f18 = Mth.clamp(f14 / Math.max(f3, 1.0E-6F), 0.0F, 1.0F);
         float f19 = Math.min(Math.min(f15, f16), Math.min(f17, f18));
         boolean flag2 = f19 < 0.999F;
         boolean flag3 = f14 < f3 || flag && flag2;
         float f20 = this.method3723(f6, f7);
         float f21 = this.method3723(f8, f9);
         float f22 = flag3 ? Mth.clamp(f4, -max, max) : f4;
         float f23 = flag3 ? Mth.clamp(f5, -max, max) : f5;
         entry.floatValue46 = flag2 ? 1.0F : 0.0F;
         entry.floatValue47 = f19;
         entry.floatValue48 = (flag ? 1.0F : 0.0F) * f19;
         entry.floatValue49 = flag3 ? 1.0F : 0.0F;
         entry.floatValue50 = f12;
         entry.floatValue51 = f13;
         entry.floatValue52 = f20;
         entry.floatValue53 = f21;
         entry.floatValue54 = f22;
         entry.floatValue55 = f23;
      }
   }

   private float method3723(float f, float f2) {
      if (f <= 0.0F && f2 >= 0.0F) {
         return 0.0F;
      } else {
         return Math.abs(f) <= Math.abs(f2) ? f : f2;
      }
   }

   private float hypot(float hypot, float y) {
      return (float)Math.sqrt(hypot * hypot + y * y);
   }

   private float method3724(float f) {
      return f * f;
   }

   private boolean method3725(float[] floatValues) {
      if (floatValues == null) {
         return false;
      } else {
         for (float floatValue : floatValues) {
            if (!this.isFinite(floatValue)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isFinite(float floatValue) {
      return !Float.isNaN(floatValue) && !Float.isInfinite(floatValue);
   }

   private boolean method3726(Vec3 deltaMovement) {
      return deltaMovement != null && this.isFinite((float)deltaMovement.x) && this.isFinite((float)deltaMovement.y) && this.isFinite((float)deltaMovement.z);
   }

   private static final class Entry {
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public float floatValue5;
      public float floatValue6;
      public float floatValue7;
      public float floatValue8;
      public float floatValue9;
      public float floatValue10;
      public float floatValue11;
      public float floatValue12;
      public float floatValue13;
      public boolean flag;
      public float floatValue14;
      public float floatValue15;
      public float floatValue16;
      public float floatValue17;
      public float floatValue18;
      public float floatValue19;
      public float floatValue20;
      public float floatValue21;
      public float floatValue22;
      public float floatValue23;
      public float floatValue24;
      public float floatValue25;
      public float floatValue26;
      public float floatValue27;
      public float floatValue28;
      public float floatValue29;
      public float floatValue30;
      public float floatValue31;
      public float floatValue32;
      public float floatValue33;
      public float floatValue34;
      public float floatValue35;
      public float floatValue36;
      public float floatValue37;
      public float floatValue38;
      public float floatValue39;
      public float floatValue40;
      public float floatValue41;
      public float floatValue42;
      public float floatValue43;
      public float floatValue44;
      public float floatValue45;
      public float floatValue46;
      public float floatValue47;
      public float floatValue48;
      public float floatValue49;
      public float floatValue50;
      public float floatValue51;
      public float floatValue52;
      public float floatValue53;
      public float floatValue54;
      public float floatValue55;
      public float floatValue56;
      public float floatValue57;
      public float floatValue58;
      public float floatValue59;
   }

   private static final class Entry2 {
      public final float floatValue;
      public final float floatValue2;
      public final float floatValue3;
      public final float floatValue4;
      public final float floatValue5;
      public final float floatValue6;
      public final float floatValue7;
      public final float floatValue8;

      public Entry2(float floatValue, float f) {
         this(floatValue, f, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      }

      public Entry2(float floatValue, float f, float floatValue2, float f2, float floatValue3, float f3, float floatValue4, float f4) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = floatValue2;
         this.floatValue4 = f2;
         this.floatValue5 = floatValue3;
         this.floatValue6 = f3;
         this.floatValue7 = floatValue4;
         this.floatValue8 = f4;
      }
   }
}

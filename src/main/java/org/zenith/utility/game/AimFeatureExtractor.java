package org.zenith.utility.game;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.Vec2f;

public final class AimFeatureExtractor {
   private static final int CONST_intValue = 59;
   private static final int CONST_intValue2 = 0;
   private static final int CONST_intValue3 = 1;
   private static final int CONST_intValue4 = 16;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 176.0F;
   private static final float CONST_floatValue3 = 8.0F;
   private static final float CONST_floatValue4 = 8.0F;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private boolean flag;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private int intValue;
   private int intValue2;
   private float floatValue11;
   private float floatValue12;
   private float floatValue13;
   private float floatValue14;
   private AimFeatureExtractor.Entry entry;

   public float[] method1911(Minecraft minecraft, Rotation rotation, LivingEntity livingEntity, float floatValue, float f, float x) {
      if (minecraft.player != null && livingEntity != null && livingEntity.getBoundingBox() != null && this.isFinite(x) && !(x <= 0.0F)) {
         Vec3 position = minecraft.player.position();
         AABB boundingBox = livingEntity.getBoundingBox();
         Vec3 vec = this.method1916(livingEntity, boundingBox);
         Vec3 vec2 = this.method1915(minecraft, position);
         float[] floats = this.method1912(minecraft, rotation, minecraft.player.getEyePosition(), livingEntity, boundingBox, floatValue, f, vec, vec2, x);
         if (floats != null) {
            this.entry = new AimFeatureExtractor.Entry(floats, position, boundingBox, x);
         }

         return floats;
      } else {
         return null;
      }
   }

   private float[] method1912(
      Minecraft minecraft, Rotation rotation, Vec3 eyePosition, LivingEntity livingEntity, AABB boundingBox, float floatValue, float f, Vec3 vec, Vec3 vec2, float x
   ) {
      Vec3 vec3 = this.method1918(boundingBox);
      Vec2f vec2f = rotation.wrappedDeltaTo(this.method1917(eyePosition, vec3));
      float floatValue2 = vec2f.getFloatValue();
      float y = vec2f.getFloatValue2();
      if (this.isFinite(floatValue2) && this.isFinite(y)) {
         Vec3 eyePosition2 = this.method1913(minecraft, rotation, eyePosition);
         AABB aabb = this.method1914(livingEntity, boundingBox, vec);
         Vec2f vec2f2 = rotation.wrappedDeltaTo(this.method1917(eyePosition2, this.method1918(aabb)));
         float floatValue3 = vec2f2.getFloatValue();
         float x2 = vec2f2.getFloatValue2();
         if (this.isFinite(floatValue3) && this.isFinite(x2)) {
            float y2 = Float.MAX_VALUE;
            float y3 = -Float.MAX_VALUE;
            float y4 = Float.MAX_VALUE;
            float y5 = -Float.MAX_VALUE;

            for (Vec3 vec4 : this.method1919(boundingBox)) {
               Vec2f vec2f3 = rotation.wrappedDeltaTo(this.method1917(eyePosition, vec4));
               float floatValue4 = vec2f3.getFloatValue();
               float x3 = vec2f3.getFloatValue2();
               if (this.isFinite(floatValue4) && this.isFinite(x3)) {
                  floatValue4 = this.method1921(floatValue4, floatValue2);
                  y2 = Math.min(y2, floatValue4);
                  y3 = Math.max(y3, floatValue4);
                  y4 = Math.min(y4, x3);
                  y5 = Math.max(y5, x3);
               }
            }

            if (y2 == Float.MAX_VALUE) {
               y3 = floatValue2;
               y2 = floatValue2;
               y5 = y;
               y4 = y;
            }

            float y6 = y3 - y2;
            float y7 = y5 - y4;
            float f2 = (float)(vec3.x - eyePosition.x);
            float f3 = Math.abs((float)(vec3.y - eyePosition.y));
            float y8 = (float)(vec3.z - eyePosition.z);
            float f4 = (float)eyePosition.distanceTo(vec3);
            float hypot = this.hypot(f2, y8);
            boolean flag = y2 <= 0.0F && y3 >= 0.0F;
            boolean flag2 = y4 <= 0.0F && y5 >= 0.0F;
            float f5 = flag && Math.abs(y6) > 1.0E-6F ? Mth.clamp(-y2 / y6, 0.0F, 1.0F) : 0.0F;
            float f6 = flag2 && Math.abs(y7) > 1.0E-6F ? Mth.clamp(-y4 / y7, 0.0F, 1.0F) : 0.0F;
            boolean flag3 = flag && flag2;
            this.floatValue5 = flag3 ? Mth.clamp(this.floatValue5 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.floatValue6 = flag3 ? 0.0F : Math.min(this.floatValue6 + 1.0F, 8.0F);
            float floatValue4 = floatValue - this.floatValue;
            float floatValue5 = f - this.floatValue2;
            float floatValue6 = floatValue4 - this.floatValue3;
            float floatValue7 = floatValue5 - this.floatValue4;
            float f7 = this.entry == null ? floatValue2 : this.method1924(this.entry.floatValues[0], this.entry.floatValue);
            float f8 = this.entry == null ? y : this.method1924(this.entry.floatValues[1], this.entry.floatValue);
            float floatValue8 = this.entry == null ? f4 : this.entry.floatValues[16];
            float y9 = Mth.wrapDegrees(floatValue2 - f7);
            float y10 = y - f8;
            float f9 = f4 - floatValue8;
            float f10 = floatValue2 / Math.max(Math.abs(y6), 0.1F);
            float f11 = y / Math.max(Math.abs(y7), 0.1F);
            float hypot2 = this.hypot(floatValue2, y);
            float f12 = (float)Math.atan2(y, floatValue2);
            float f13 = this.method1923(floatValue, x);
            float f14 = this.method1923(f, x);
            float f15 = this.method1922(floatValue2, x);
            float f16 = this.method1922(y, x);
            float f17 = 0.0F;
            float f18 = 0.0F;
            float f19 = 0.0F;
            float f20 = 0.0F;
            if (this.entry != null) {
               float floatValue9 = this.entry.floatValues[0];
               float floatValue10 = this.entry.floatValues[1];
               float f21 = floatValue9 - f13;
               float f22 = floatValue10 - f14;
               f17 = Math.abs(floatValue9) - Math.abs(f21);
               f18 = Math.abs(floatValue10) - Math.abs(f22);
               f19 = this.method1920(f15 - f21, x);
               f20 = f16 - f22;
            }

            this.floatValue9 = f13 != 0.0F ? this.floatValue9 + 1.0F : 0.0F;
            this.floatValue10 = f14 != 0.0F ? this.floatValue10 + 1.0F : 0.0F;
            int intValue = this.method1925(f13);
            int n = this.method1925(f14);
            this.floatValue13 = this.method1927(f13, intValue, this.intValue, this.floatValue13);
            this.floatValue14 = this.method1927(f14, n, this.intValue2, this.floatValue14);
            float f21 = this.method1928(f13, this.floatValue13);
            float f22 = this.method1928(f14, this.floatValue14);
            this.floatValue11 = this.method1926(intValue, this.intValue, this.floatValue11);
            this.floatValue12 = this.method1926(n, this.intValue2, this.floatValue12);
            if (intValue != 0) {
               this.intValue = intValue;
            }

            if (n != 0) {
               this.intValue2 = n;
            }

            if (flag3) {
               this.floatValue7 = Mth.clamp(this.flag ? this.floatValue7 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.floatValue8 = 0.0F;
            } else {
               this.floatValue7 = 0.0F;
               this.floatValue8 = Mth.clamp(this.flag ? 1.0F : this.floatValue8 + 1.0F, 0.0F, 8.0F);
            }

            float[] floats = new float[]{
               f15,
               f16,
               this.method1922(y2, x),
               this.method1922(y3, x),
               this.method1922(y4, x),
               this.method1922(y5, x),
               this.method1922(y6, x),
               this.method1922(y7, x),
               f10,
               f11,
               this.method1922(hypot2, x),
               (float)Math.sin(f12),
               (float)Math.cos(f12),
               f5,
               f6,
               flag3 ? 1.0F : 0.0F,
               f4,
               hypot,
               f3,
               this.floatValue5,
               this.floatValue6,
               this.floatValue7,
               this.floatValue8,
               this.method1922(y9, x),
               this.method1922(y10, x),
               f9,
               f19,
               f20,
               this.method1922(floatValue3, x),
               this.method1922(x2, x),
               (float)vec.x,
               (float)vec.y,
               (float)vec.z,
               f13,
               f14,
               f13,
               f14,
               this.method1923(floatValue4, x),
               this.method1923(floatValue5, x),
               this.method1923(floatValue6, x),
               this.method1923(floatValue7, x),
               this.floatValue9,
               this.floatValue10,
               this.floatValue11,
               this.floatValue12,
               f17,
               f18,
               f21,
               f22,
               this.floatValue13,
               this.floatValue14,
               minecraft.player.input.keyPresses.forward() ? 1.0F : 0.0F,
               minecraft.player.input.keyPresses.backward() ? 1.0F : 0.0F,
               minecraft.player.input.keyPresses.left() ? 1.0F : 0.0F,
               minecraft.player.input.keyPresses.right() ? 1.0F : 0.0F,
               minecraft.player.input.keyPresses.jump() ? 1.0F : 0.0F,
               (float)vec2.x,
               (float)vec2.y,
               (float)vec2.z
            };
            if (floats.length == 59 && this.method1929(floats)) {
               this.floatValue = floatValue;
               this.floatValue2 = f;
               this.floatValue3 = floatValue4;
               this.floatValue4 = floatValue5;
               this.flag = flag3;
               return floats;
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

   public void reset() {
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.flag = false;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.floatValue7 = 0.0F;
      this.floatValue8 = 0.0F;
      this.floatValue9 = 0.0F;
      this.floatValue10 = 0.0F;
      this.intValue = 0;
      this.intValue2 = 0;
      this.floatValue11 = 0.0F;
      this.floatValue12 = 0.0F;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.entry = null;
   }

   private Vec3 method1913(Minecraft minecraft, Rotation rotation, Vec3 eyePosition) {
      Vec3 position = minecraft.player.position();
      Vec3 vec = eyePosition.subtract(position);

      try {
         PlayerSimulation playerSimulation = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(minecraft.player.input.keyPresses));
         playerSimulation.floatValue = rotation.getFloatValue();
         playerSimulation.floatValue2 = rotation.getFloatValue2();
         playerSimulation.tick();
         playerSimulation.tick();
         Vec3 deltaMovement = playerSimulation.vec3d.add(vec);
         return this.method1930(deltaMovement) ? deltaMovement : eyePosition;
      } catch (Throwable e) {
         return eyePosition;
      }
   }

   private AABB method1914(LivingEntity livingEntity, AABB boundingBox, Vec3 vec) {
      if (livingEntity instanceof Player player) {
         try {
            PlayerSimulation playerSimulation = PlayerSimulation.method6034(player, PlayerSimulation.Entry.method6093(player));
            playerSimulation.tick();
            playerSimulation.tick();
            if (playerSimulation.box != null && this.method1930(playerSimulation.vec3d)) {
               return playerSimulation.box;
            }
         } catch (Throwable e) {
         }
      }

      Vec3 deltaMovement = vec == null ? Vec3.ZERO : vec.scale(2.0);
      return this.method1930(deltaMovement) ? boundingBox.move(deltaMovement) : boundingBox;
   }

   private Vec3 method1915(Minecraft minecraft, Vec3 position) {
      if (this.entry != null) {
         Vec3 deltaMovement = position.subtract(this.entry.vec3d);
         if (this.method1930(deltaMovement) && deltaMovement.lengthSqr() > 1.0E-10) {
            return deltaMovement;
         }
      }

      Vec3 deltaMovement = minecraft.player.getDeltaMovement();
      return this.method1930(deltaMovement) ? deltaMovement : Vec3.ZERO;
   }

   private Vec3 method1916(LivingEntity livingEntity, AABB boundingBox) {
      if (this.entry != null && this.entry.box != null) {
         Vec3 deltaMovement = this.method1918(boundingBox).subtract(this.method1918(this.entry.box));
         if (this.method1930(deltaMovement) && deltaMovement.lengthSqr() > 1.0E-10) {
            return deltaMovement;
         }
      }

      Vec3 deltaMovement = livingEntity.getDeltaMovement();
      return this.method1930(deltaMovement) ? deltaMovement : Vec3.ZERO;
   }

   private Rotation method1917(Vec3 eyePosition, Vec3 vec) {
      return RotationUtils.method6912(vec.subtract(eyePosition));
   }

   private Vec3 method1918(AABB boundingBox) {
      return new Vec3((boundingBox.minX + boundingBox.maxX) * 0.5, (boundingBox.minY + boundingBox.maxY) * 0.5, (boundingBox.minZ + boundingBox.maxZ) * 0.5);
   }

   private Vec3[] method1919(AABB boundingBox) {
      return new Vec3[]{
         new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
         new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),
         new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.minZ),
         new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ),
         new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
         new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
         new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
         new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)
      };
   }

   private float method1920(float f, float x) {
      float f2 = 360.0F / x;
      float f3 = f % f2;
      if (f3 >= f2 * 0.5F) {
         f3 -= f2;
      } else if (f3 < -f2 * 0.5F) {
         f3 += f2;
      }

      return f3;
   }

   private float method1921(float f, float f2) {
      float angle = f - f2;
      return !Float.isFinite(angle) ? f : f2 + Mth.wrapDegrees(angle);
   }

   private float method1922(float y, float x) {
      return y / x;
   }

   private float method1923(float floatValue, float x) {
      return Math.round(floatValue / x);
   }

   private float method1924(float floatValue, float f) {
      return floatValue * f;
   }

   private float hypot(float f, float y) {
      return (float)Math.sqrt(f * f + y * y);
   }

   private int method1925(float f) {
      return f > 0.0F ? 1 : (f < 0.0F ? -1 : 0);
   }

   private float method1926(int intValue, int n, float f) {
      return intValue != 0 && n != 0 && intValue != n ? 0.0F : f + 1.0F;
   }

   private float method1927(float f, int intValue, int n, float f2) {
      if (intValue == 0) {
         return 0.0F;
      } else {
         return n != 0 && intValue == n ? Math.max(f2, Math.abs(f)) : Math.abs(f);
      }
   }

   private float method1928(float f, float f2) {
      return f2 > 0.0F ? Math.abs(f) / f2 : 0.0F;
   }

   private boolean method1929(float[] floats) {
      for (float x : floats) {
         if (!this.isFinite(x)) {
            return false;
         }
      }

      return true;
   }

   private boolean isFinite(float x) {
      return Float.isFinite(x);
   }

   private boolean method1930(Vec3 deltaMovement) {
      return deltaMovement != null && Double.isFinite(deltaMovement.x) && Double.isFinite(deltaMovement.y) && Double.isFinite(deltaMovement.z);
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

package org.zenith.base.rotation.mods;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public final class Class24 extends Util22 {
   private static final float CONST_floatValue = 1.0E-4F;
   private static final float CONST_floatValue2 = 0.001F;
   private static final float CONST_floatValue3 = 1.75F;
   private static final float CONST_floatValue4 = 1.25F;
   private static final float CONST_floatValue5 = 0.4F;
   private static final float CONST_floatValue6 = 0.75F;
   private static final float CONST_floatValue7 = 2.5F;
   private static final float CONST_floatValue8 = 1.75F;
   private Rotation rotation;
   private float floatValue;
   private float floatValue2;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;

   public Class24.Data method3535(Rotation rotation, Vec3 vec, AABB aabb, float f, boolean flag) {
      Vec2f vec2f = this.method3541(rotation, vec, aabb);
      if (vec2f == null) {
         return new Class24.Data(rotation, false, false, false);
      } else {
         float floatValue = vec2f.getFloatValue() * 1.1F;
         float floatValue2 = vec2f.getFloatValue2();
         boolean flag2 = Math.abs(floatValue) <= 1.0E-4F && Math.abs(floatValue2) <= 1.0E-4F;
         float max = Float.isFinite(f) ? Math.max(0.0F, f) : 0.0F;
         boolean flag3 = Math.abs(floatValue) <= max && Math.abs(floatValue2) <= max;
         if (!flag2 && (flag3 || flag)) {
            Rotation rotation2 = rotation.method3140(this.method3544(floatValue, max), this.method3544(floatValue2, max));
            return new Class24.Data(rotation2, true, false, flag3);
         } else {
            return new Class24.Data(rotation, true, flag2, flag3);
         }
      }
   }

   public Class24.Data method3536(Rotation rotation, Rotation rotation2, Vec3 vec, AABB aabb, float f, int intValue, int tickCount) {
      Vec2f vec2f = this.method3541(rotation, vec, aabb);
      if (vec2f == null) {
         this.method3537();
         return new Class24.Data(rotation, false, false, false);
      } else {
         float max = Float.isFinite(f) ? Math.max(0.0F, f) : 0.0F;
         float f2 = this.method3544(vec2f.getFloatValue() * 1.1F, max);
         float f3 = this.method3544(vec2f.getFloatValue2(), max);
         boolean flag = Math.abs(vec2f.getFloatValue() * 1.1F) <= max && Math.abs(vec2f.getFloatValue2()) <= max;
         boolean flag2 = this.intValue == intValue && (this.intValue2 == tickCount || this.intValue2 == tickCount - 1) && this.rotation != null;
         if (!flag2) {
            this.method3538(rotation, rotation2, max);
         }

         Vec2f vec2f2 = this.rotation.method3131(rotation);
         this.floatValue = this.method3539(this.floatValue, f2, vec2f2.getFloatValue(), false);
         this.floatValue2 = 0.0F;
         this.floatValue = this.method3544(this.floatValue, max);
         this.floatValue2 = this.method3544(this.floatValue2, max);
         Rotation rotation3 = rotation.method3140(this.floatValue, this.floatValue2);
         Vec2f vec2f3 = this.method3541(rotation3, vec, aabb);
         boolean flag3 = vec2f3 != null && Math.abs(vec2f3.getFloatValue()) <= 1.0E-4F && Math.abs(vec2f3.getFloatValue2()) <= 1.0E-4F;
         this.rotation = rotation;
         this.intValue = intValue;
         this.intValue2 = tickCount;
         return new Class24.Data(rotation3, true, flag3, flag);
      }
   }

   public void method3537() {
      this.rotation = null;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
   }

   private void method3538(Rotation rotation, Rotation rotation2, float max) {
      Vec2f vec2f = rotation.method3131(rotation2);
      if (Float.isFinite(vec2f.getFloatValue())
         && Float.isFinite(vec2f.getFloatValue2())
         && Math.abs(vec2f.getFloatValue()) <= max
         && Math.abs(vec2f.getFloatValue2()) <= max) {
         this.floatValue = vec2f.getFloatValue();
         this.floatValue2 = vec2f.getFloatValue2();
      } else {
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
      }

      this.rotation = rotation;
   }

   private float method3539(float floatValue, float f, float floatValue2, boolean flag) {
      if (Math.abs(floatValue2) <= 0.001F) {
         return floatValue;
      } else {
         float f2 = f - floatValue;
         if (Math.abs(f2) <= 1.0E-4F) {
            return f;
         } else {
            boolean flag2 = Math.signum(floatValue2) == Math.signum(f2);
            float f3 = flag2 ? (flag ? 1.25F : 1.75F) : (flag ? 0.75F : 0.4F);
            float max = 100.0F;
            float f4 = Mth.clamp(floatValue2 * (f3 - 1.0F), -max, max);
            return this.method3540(floatValue, floatValue + f4, f);
         }
      }
   }

   private float method3540(float floatValue, float f, float f2) {
      boolean flag = (f2 - floatValue) * (f2 - f) < 0.0F;
      return flag ? f2 : f;
   }

   private Vec2f method3541(Rotation rotation, Vec3 vec, AABB aabb) {
      Vec3 center = aabb.getCenter();
      return rotation.method3131(this.method3542(vec, center));
   }

   private Rotation method3542(Vec3 vec, Vec3 center) {
      return RotationUtils.method6912(center.subtract(vec));
   }

   private float method3543(float f, float f2) {
      return f > 0.0F ? f : Math.min(f2, 0.0F);
   }

   private float method3544(float floatValue, float max) {
      return Mth.clamp(floatValue, -max, max);
   }

   private float method3545(float f, float f2) {
      float angle = f - f2;
      return !Float.isFinite(angle) ? f : f2 + Mth.wrapDegrees(angle);
   }

   public static final class Data {
      private final Rotation rotation;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;

      public Rotation rotation() {
         return this.rotation;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation, flag, flag2, flag3);
      }

      @Override
      public String toString() {
         return "Data[" + "rotation=" + this.rotation + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + "]";
      }

      public Data(Rotation rotation, boolean flag, boolean flag2, boolean flag3) {
         this.rotation = rotation;
         this.flag = flag;
         this.flag2 = flag2;
         this.flag3 = flag3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }
   }
}

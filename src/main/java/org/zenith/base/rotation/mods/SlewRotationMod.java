package org.zenith.base.rotation.mods;

import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.RotationContext;
import org.zenith.utility.misc.Vec2f;

public final class SlewRotationMod extends RotationContext {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final double CONST_doubleValue = 0.15190947;
   private static final double CONST_doubleValue2 = 0.3;
   private static final double CONST_doubleValue3 = 1.8;
   private static double doubleValue = 3.0;
   private static final double CONST_doubleValue4 = 0.8;
   private static final double CONST_doubleValue5 = 0.0;
   private static final double CONST_doubleValue6 = 0.7;
   private static final double CONST_doubleValue7 = 1.6;
   private static final double CONST_doubleValue8 = 4.2;
   private static final int CONST_intValue2 = 2;
   private static final int CONST_intValue3 = 10;
   private static final double CONST_doubleValue9 = 0.233125;
   private static final double CONST_doubleValue10 = 0.424375;
   private static final double CONST_doubleValue11 = 0.0;
   private static final double CONST_doubleValue12 = 2.125;
   private static final double CONST_doubleValue13 = 0.446;
   private static final double CONST_doubleValue14 = 0.0625;
   private static final double CONST_doubleValue15 = 38.0;
   private static final SlewRotationMod.Data data = new SlewRotationMod.Data(
      0.2014583333,
      0.02,
      1.0,
      300.0,
      0.2,
      85.0,
      0.3975,
      0.77,
      0.685,
      800.0,
      0.85,
      0.685,
      0.4166875,
      0.77,
      0.05,
      0.75,
      0.31828125,
      0.281875,
      3.0,
      60.0,
      23.725,
      0.735,
      0.1353125,
      0.049921875,
      1.2,
      801.0,
      0.6,
      24
   );
   private static final SlewRotationMod.Data data2 = new SlewRotationMod.Data(
      0.54953125,
      0.0105625,
      1.0,
      300.0,
      0.2,
      85.0,
      0.4275,
      0.425,
      0.561,
      300.0,
      0.125,
      0.561,
      0.88334375,
      0.43,
      0.5896875,
      0.75,
      0.1363541667,
      0.865,
      3.0,
      60.0,
      2.0,
      0.514609375,
      0.06859375,
      0.006953125,
      1.2,
      312.0,
      0.6,
      24
   );
   private final SlewRotationMod.Entry entry = new SlewRotationMod.Entry(data, 11L);
   private final SlewRotationMod.Entry entry2 = new SlewRotationMod.Entry(data2, 12L);
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private Rotation rotation;
   private Vec3 vec3d;
   private Vec3 vec3d2;
   private double doubleValue2 = Double.NaN;
   private double doubleValue3 = Double.NaN;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private double doubleValue7;
   private boolean flag;
   private boolean flag2;
   private long longValue = 1374496523L;
   private static final double CONST_doubleValue16 = 1184.9162530815229;

   public Rotation method3636() {
      Rotation rotation = rotationManager.getEffectiveRotation();
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      if (mc.player != null && livingEntity != null && livingEntity.isAlive()) {
         int tickCount = mc.player.tickCount;
         if (tickCount == this.intValue3 && this.rotation != null) {
            return this.rotation;
         } else {
            if (livingEntity.getId() != this.intValue || this.intValue2 != Integer.MIN_VALUE && tickCount != this.intValue2 + 1) {
               this.method3640(livingEntity.getId(), tickCount);
            }

            this.intValue2 = tickCount;
            float f = Rotation.sensitivityStep();
            if (Float.isFinite(f) && !(f <= 0.0F)) {
               Vec3 eyePosition = mc.player.getEyePosition();
               Vec3 center = livingEntity.getBoundingBox().getCenter();
               Vec3 vec = center;
               if (this.vec3d2 != null && doubleValue > 0.0) {
                  Vec3 vec2 = center.subtract(this.vec3d2);
                  double length = vec2.length();
                  if (length > 0.8) {
                     vec2 = vec2.scale(0.8 / length);
                  }

                  vec = center.add(vec2.scale(doubleValue));
               }

               Rotation rotation2 = RotationUtils.method6912(vec.subtract(eyePosition));
               Vec2f vec2f = rotation.wrappedDeltaTo(rotation2);
               double sign = vec2f.getFloatValue() / 0.15190947;
               double d = vec2f.getFloatValue2() / 0.15190947;
               double d2 = 0.0;
               double d3 = 0.0;
               if (this.vec3d != null) {
                  Rotation rotation3 = RotationUtils.method6912(vec.subtract(this.vec3d));
                  Vec2f vec2f2 = rotation3.wrappedDeltaTo(rotation2);
                  d2 = vec2f2.getFloatValue() / 0.15190947;
                  d3 = vec2f2.getFloatValue2() / 0.15190947;
               }

               if (Double.isFinite(this.doubleValue2)) {
                  this.doubleValue6 = method3639(this.doubleValue6, sign - this.doubleValue2 + this.doubleValue4);
                  this.doubleValue7 = method3639(this.doubleValue7, d - this.doubleValue3 + this.doubleValue5);
               }

               this.doubleValue2 = sign;
               this.doubleValue3 = d;
               this.vec3d = eyePosition;
               this.vec3d2 = center;
               double d4 = Math.max(0.5, center.distanceTo(eyePosition));
               double d5 = Math.toDegrees(Math.atan(0.3 / d4)) / 0.15190947;
               double d6 = Math.toDegrees(Math.atan(0.9 / d4)) / 0.15190947;
               if (Double.isFinite(sign) && Double.isFinite(d) && Double.isFinite(d2) && Double.isFinite(d3)) {
                  if (this.flag) {
                     this.entry.method3642(this.longValue++);
                     this.flag = false;
                  }

                  double d7 = this.entry.method3649(sign, this.doubleValue6, d5, d2, false);
                  if (this.flag2) {
                     this.entry2.method3642(this.longValue++);
                     this.flag2 = false;
                  }

                  double d8 = this.entry2.method3649(d, this.doubleValue7, d6, d3, d7 == 0.0);
                  this.doubleValue4 = d7;
                  this.doubleValue5 = d8;
                  int n = (int)Math.floor(d7 * 0.15190947 / f + 0.5);
                  int n2 = (int)Math.floor(d8 * 0.15190947 / f + 0.5);
                  if (n == 0 && n2 == 0) {
                     this.method3641(tickCount, rotation);
                     return rotation;
                  } else {
                     doubleValue = 3.0;
                     Rotation rotation3 = rotation.add(n * f, n2 * f);
                     System.out.println("yawCounts=" + d7 + " yawErr=" + sign + " yawDrift=" + this.doubleValue6 + " lead=" + doubleValue);
                     this.method3641(tickCount, rotation3);
                     return rotation3;
                  }
               } else {
                  this.method3640(livingEntity.getId(), tickCount);
                  this.method3641(tickCount, rotation);
                  return rotation;
               }
            } else {
               this.method3641(tickCount, rotation);
               return rotation;
            }
         }
      } else {
         this.reset();
         return rotation;
      }
   }

   private static double method3637(double sign, double d) {
      double d2 = sign * 0.7;
      return Math.copySign(Math.min(Math.abs(d2), d), sign);
   }

   public boolean method3638() {
      return true;
   }

   private static double method3639(double d, double d2) {
      double d3 = 2369.8325061630458;
      double d4 = d2 - d3 * Math.floor((d2 + 1184.9162530815229) / d3);
      double d5 = Math.max(-900.0, Math.min(900.0, d4));
      return 0.6 * d + 0.4 * d5;
   }

   private void method3640(int intValue, int tickCount) {
      long l = Integer.toUnsignedLong(intValue) * -7046029254386353131L ^ Integer.toUnsignedLong(tickCount);
      this.entry.method3642(l * 2L + 1L);
      this.entry2.method3642(l * 2L + 2L);
      this.intValue = intValue;
      this.intValue2 = Integer.MIN_VALUE;
      this.intValue3 = Integer.MIN_VALUE;
      this.rotation = null;
      this.vec3d = null;
      this.vec3d2 = null;
      this.doubleValue2 = Double.NaN;
      this.doubleValue3 = Double.NaN;
      this.doubleValue4 = 0.0;
      this.doubleValue5 = 0.0;
      this.doubleValue6 = 0.0;
      this.doubleValue7 = 0.0;
      this.flag = false;
      this.flag2 = false;
   }

   private void method3641(int tickCount, Rotation rotation) {
      this.intValue3 = tickCount;
      this.rotation = rotation;
   }

   public void reset() {
      this.method3640(Integer.MIN_VALUE, 0);
   }

   private static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final double doubleValue4;
      private final double doubleValue5;
      private final double doubleValue6;
      private final double doubleValue7;
      private final double doubleValue8;
      private final double doubleValue9;
      private final double doubleValue10;
      private final double doubleValue11;
      private final double doubleValue12;
      private final double doubleValue13;
      private final double doubleValue14;
      private final double doubleValue15;
      private final double doubleValue16;
      private final double doubleValue17;
      private final double doubleValue18;
      private final double doubleValue19;
      private final double doubleValue20;
      private final double doubleValue21;
      private final double doubleValue22;
      private final double doubleValue23;
      private final double doubleValue24;
      private final double doubleValue25;
      private final double doubleValue26;
      private final double doubleValue27;
      private final int intValue;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public double doubleValue4() {
         return this.doubleValue4;
      }

      public double doubleValue5() {
         return this.doubleValue5;
      }

      public double doubleValue6() {
         return this.doubleValue6;
      }

      public double doubleValue7() {
         return this.doubleValue7;
      }

      public double doubleValue8() {
         return this.doubleValue8;
      }

      public double doubleValue9() {
         return this.doubleValue9;
      }

      public double doubleValue10() {
         return this.doubleValue10;
      }

      public double doubleValue11() {
         return this.doubleValue11;
      }

      public double doubleValue12() {
         return this.doubleValue12;
      }

      public double doubleValue13() {
         return this.doubleValue13;
      }

      public double doubleValue14() {
         return this.doubleValue14;
      }

      public double doubleValue15() {
         return this.doubleValue15;
      }

      public double doubleValue16() {
         return this.doubleValue16;
      }

      public double doubleValue17() {
         return this.doubleValue17;
      }

      public double doubleValue18() {
         return this.doubleValue18;
      }

      public double doubleValue19() {
         return this.doubleValue19;
      }

      public double doubleValue20() {
         return this.doubleValue20;
      }

      public double doubleValue21() {
         return this.doubleValue21;
      }

      public double doubleValue22() {
         return this.doubleValue22;
      }

      public double doubleValue23() {
         return this.doubleValue23;
      }

      public double doubleValue24() {
         return this.doubleValue24;
      }

      public double doubleValue25() {
         return this.doubleValue25;
      }

      public double doubleValue26() {
         return this.doubleValue26;
      }

      public double doubleValue27() {
         return this.doubleValue27;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.doubleValue4, other.doubleValue4) && java.util.Objects.equals(this.doubleValue5, other.doubleValue5) && java.util.Objects.equals(this.doubleValue6, other.doubleValue6) && java.util.Objects.equals(this.doubleValue7, other.doubleValue7) && java.util.Objects.equals(this.doubleValue8, other.doubleValue8) && java.util.Objects.equals(this.doubleValue9, other.doubleValue9) && java.util.Objects.equals(this.doubleValue10, other.doubleValue10) && java.util.Objects.equals(this.doubleValue11, other.doubleValue11) && java.util.Objects.equals(this.doubleValue12, other.doubleValue12) && java.util.Objects.equals(this.doubleValue13, other.doubleValue13) && java.util.Objects.equals(this.doubleValue14, other.doubleValue14) && java.util.Objects.equals(this.doubleValue15, other.doubleValue15) && java.util.Objects.equals(this.doubleValue16, other.doubleValue16) && java.util.Objects.equals(this.doubleValue17, other.doubleValue17) && java.util.Objects.equals(this.doubleValue18, other.doubleValue18) && java.util.Objects.equals(this.doubleValue19, other.doubleValue19) && java.util.Objects.equals(this.doubleValue20, other.doubleValue20) && java.util.Objects.equals(this.doubleValue21, other.doubleValue21) && java.util.Objects.equals(this.doubleValue22, other.doubleValue22) && java.util.Objects.equals(this.doubleValue23, other.doubleValue23) && java.util.Objects.equals(this.doubleValue24, other.doubleValue24) && java.util.Objects.equals(this.doubleValue25, other.doubleValue25) && java.util.Objects.equals(this.doubleValue26, other.doubleValue26) && java.util.Objects.equals(this.doubleValue27, other.doubleValue27) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, doubleValue4, doubleValue5, doubleValue6, doubleValue7, doubleValue8, doubleValue9, doubleValue10, doubleValue11, doubleValue12, doubleValue13, doubleValue14, doubleValue15, doubleValue16, doubleValue17, doubleValue18, doubleValue19, doubleValue20, doubleValue21, doubleValue22, doubleValue23, doubleValue24, doubleValue25, doubleValue26, doubleValue27, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "doubleValue4=" + this.doubleValue4 + ", " + "doubleValue5=" + this.doubleValue5 + ", " + "doubleValue6=" + this.doubleValue6 + ", " + "doubleValue7=" + this.doubleValue7 + ", " + "doubleValue8=" + this.doubleValue8 + ", " + "doubleValue9=" + this.doubleValue9 + ", " + "doubleValue10=" + this.doubleValue10 + ", " + "doubleValue11=" + this.doubleValue11 + ", " + "doubleValue12=" + this.doubleValue12 + ", " + "doubleValue13=" + this.doubleValue13 + ", " + "doubleValue14=" + this.doubleValue14 + ", " + "doubleValue15=" + this.doubleValue15 + ", " + "doubleValue16=" + this.doubleValue16 + ", " + "doubleValue17=" + this.doubleValue17 + ", " + "doubleValue18=" + this.doubleValue18 + ", " + "doubleValue19=" + this.doubleValue19 + ", " + "doubleValue20=" + this.doubleValue20 + ", " + "doubleValue21=" + this.doubleValue21 + ", " + "doubleValue22=" + this.doubleValue22 + ", " + "doubleValue23=" + this.doubleValue23 + ", " + "doubleValue24=" + this.doubleValue24 + ", " + "doubleValue25=" + this.doubleValue25 + ", " + "doubleValue26=" + this.doubleValue26 + ", " + "doubleValue27=" + this.doubleValue27 + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(
         double doubleValue,
         double d,
         double d2,
         double d3,
         double d4,
         double d5,
         double d6,
         double d7,
         double d8,
         double d9,
         double d10,
         double d11,
         double d12,
         double d13,
         double d14,
         double d15,
         double d16,
         double d17,
         double d18,
         double d19,
         double d20,
         double d21,
         double d22,
         double d23,
         double d24,
         double d25,
         double d26,
         int intValue
      ) {
         if (!(d13 > 0.0) || !(d13 <= 1.0)) {
            throw new IllegalArgumentException("slew must lie in (0, 1]");
         } else if (!(d12 > 0.0) || !(d12 <= 1.0)) {
            throw new IllegalArgumentException("reviseProbability must lie in (0, 1]");
         } else if (d23 > d22) {
            throw new IllegalArgumentException("resting must be likelier when settled than when there is work");
         } else if (!(d25 <= 0.0) && !(d24 < 1.0)) {
            this.doubleValue = doubleValue;
            this.doubleValue2 = d;
            this.doubleValue3 = d2;
            this.doubleValue4 = d3;
            this.doubleValue5 = d4;
            this.doubleValue6 = d5;
            this.doubleValue7 = d6;
            this.doubleValue8 = d7;
            this.doubleValue9 = d8;
            this.doubleValue10 = d9;
            this.doubleValue11 = d10;
            this.doubleValue12 = d11;
            this.doubleValue13 = d12;
            this.doubleValue14 = d13;
            this.doubleValue15 = d14;
            this.doubleValue16 = d15;
            this.doubleValue17 = d16;
            this.doubleValue18 = d17;
            this.doubleValue19 = d18;
            this.doubleValue20 = d19;
            this.doubleValue21 = d20;
            this.doubleValue22 = d21;
            this.doubleValue23 = d22;
            this.doubleValue24 = d23;
            this.doubleValue25 = d24;
            this.doubleValue26 = d25;
            this.doubleValue27 = d26;
            this.intValue = intValue;
         } else {
            throw new IllegalArgumentException("bad rate or hold duration");
         }
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public double getDoubleValue4() {
         return this.doubleValue4;
      }

      public double getDoubleValue5() {
         return this.doubleValue5;
      }

      public double getDoubleValue6() {
         return this.doubleValue6;
      }

      public double getDoubleValue7() {
         return this.doubleValue7;
      }

      public double getDoubleValue8() {
         return this.doubleValue8;
      }

      public double getDoubleValue9() {
         return this.doubleValue9;
      }

      public double getDoubleValue10() {
         return this.doubleValue10;
      }

      public double getDoubleValue11() {
         return this.doubleValue11;
      }

      public double getDoubleValue12() {
         return this.doubleValue12;
      }

      public double getDoubleValue13() {
         return this.doubleValue13;
      }

      public double getDoubleValue14() {
         return this.doubleValue14;
      }

      public double getDoubleValue15() {
         return this.doubleValue15;
      }

      public double getDoubleValue16() {
         return this.doubleValue16;
      }

      public double getDoubleValue17() {
         return this.doubleValue17;
      }

      public double getDoubleValue18() {
         return this.doubleValue18;
      }

      public double getDoubleValue19() {
         return this.doubleValue19;
      }

      public double getDoubleValue20() {
         return this.doubleValue20;
      }

      public double getDoubleValue21() {
         return this.doubleValue21;
      }

      public double getDoubleValue22() {
         return this.doubleValue22;
      }

      public double getDoubleValue23() {
         return this.doubleValue23;
      }

      public double getDoubleValue24() {
         return this.doubleValue24;
      }

      public double getDoubleValue25() {
         return this.doubleValue25;
      }

      public double getDoubleValue26() {
         return this.doubleValue26;
      }

      public double getDoubleValue27() {
         return this.doubleValue27;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Entry {
      private final SlewRotationMod.Data data;
      private Random random;
      private double[] doubleValues = new double[0];
      private int intValue;
      private double doubleValue;
      private double doubleValue2;
      private int intValue2;
      private int intValue3;
      private int intValue4;
      private Boolean booleanValue;

      private Entry(SlewRotationMod.Data data, long seed) {
         this.data = data;
         this.method3642(seed);
      }

      private void method3642(long seed) {
         this.random = new Random(seed);
         this.doubleValues = new double[0];
         this.intValue = 0;
         this.doubleValue = 0.0;
         this.doubleValue2 = 0.0;
         this.intValue2 = 0;
         this.intValue3 = 0;
         this.intValue4 = 0;
         this.booleanValue = null;
      }

      private double method3643() {
         double d = Math.max(1.0E-9, this.data.getDoubleValue14());
         double d2 = this.data.getDoubleValue27() * this.data.getDoubleValue13() / d;
         return Math.min(this.data.getDoubleValue3(), d2);
      }

      private double method3644(double d) {
         return Double.isFinite(d) && !(d <= 0.0)
            ? Math.min(this.data.getDoubleValue20(), Math.max(this.data.getDoubleValue19(), this.data.getDoubleValue18() * d))
            : this.data.getDoubleValue19();
      }

      private int method3645(double d, double d2) {
         double d3 = Math.max(2.0 * Math.abs(d2), 1.0);
         double d4 = Math.log(Math.abs(d) / d3 + 1.0) / Math.log(2.0);
         long l = Math.round(1.6 + 4.2 * d4);
         return (int)Math.max(2L, Math.min(10L, l));
      }

      private double method3646(double d) {
         double d2 = 2.125 - 0.446 * Math.log1p(Math.abs(d));
         if (this.booleanValue != null) {
            d2 += this.booleanValue ? 0.0625 : -0.0625;
         }

         return 1.0 / (1.0 + Math.exp(-Math.max(-30.0, Math.min(30.0, d2))));
      }

      private double method3647(double d) {
         double d2 = this.data.getDoubleValue4();
         double d3 = Math.abs(d);
         return !(d2 <= 0.0) && !(d3 <= d2) ? Math.pow(d2 / d3, this.data.getDoubleValue5()) : 1.0;
      }

      private void method3648(double sign, double d, double d2) {
         double d3 = sign * this.data.getDoubleValue8();
         int n = this.method3645(d3, d);
         double copySign = d3 + this.data.getDoubleValue9() * d2 * n;
         double d4 = this.data.getDoubleValue10();
         double d5 = this.method3647(d2);
         if (d5 < 1.0) {
            d4 = Math.min(d4, this.data.getDoubleValue6() * d5 * 2.0);
         }

         copySign = Math.copySign(Math.min(Math.abs(copySign), d4), copySign);
         double[] doubles = method3651(copySign * 1.0, n + 1);
         this.doubleValues = new double[n];
         System.arraycopy(doubles, 0, this.doubleValues, 0, n);
         this.intValue = 0;
      }

      private double method3649(double sign, double d, double d2, double d3, boolean flag) {
         if (this.intValue2 > 0) {
            this.intValue2--;
            this.doubleValue = 0.0;
            this.doubleValue2 = 0.0;
            return 0.0;
         } else if (this.intValue < this.doubleValues.length) {
            double doubleValue = this.doubleValues[this.intValue++];
            double d4 = 0.233125 * this.random.nextGaussian();
            d4 = Math.max(-0.424375, Math.min(0.424375, d4));
            doubleValue *= 1.0 + d4;
            if (this.intValue >= this.doubleValues.length) {
               this.booleanValue = Boolean.TRUE;
               if (Math.abs(d) >= 38.0) {
                  this.doubleValue = 0.0;
                  this.doubleValue2 = 0.0;
               }
            }

            return this.method3650(doubleValue, d3, d);
         } else {
            double d4 = this.method3644(d2);
            boolean flag2 = Math.abs(sign) < d4 && Math.abs(d) < this.data.getDoubleValue21();
            if (!flag2 && this.random.nextDouble() < this.method3646(d)) {
               double d5 = sign - Math.copySign(Math.min(Math.abs(sign), d4), sign);
               if (Math.abs(d5) >= 1.0) {
                  this.method3648(sign, d2, d);
                  return this.method3649(sign, d, d2, d3, flag);
               }
            }

            this.booleanValue = Boolean.FALSE;
            if (flag2) {
               this.doubleValue = this.doubleValue * (1.0 - this.data.getDoubleValue14());
            } else if (this.random.nextDouble() < Math.min(1.0, this.data.getDoubleValue13())) {
               double d5 = Math.min(this.method3643(), this.data.getDoubleValue() + this.data.getDoubleValue2() * Math.abs(d));
               double d6 = sign - Math.copySign(Math.min(Math.abs(sign), d4), sign);
               double copySign = d5 * d6 * (1.0 + this.data.getDoubleValue17());
               copySign += this.data.getDoubleValue12() * d;
               double d7 = this.data.getDoubleValue10();
               double d8 = this.method3647(d);
               if (d8 < 1.0) {
                  d7 = Math.min(d7, this.data.getDoubleValue6() * d8);
               }

               copySign = Math.copySign(Math.min(Math.abs(copySign), d7), copySign);
               if (this.data.getDoubleValue15() > 0.0) {
                  double d9 = this.data.getDoubleValue15() * this.random.nextGaussian();
                  d9 = Math.max(-this.data.getDoubleValue16(), Math.min(this.data.getDoubleValue16(), d9));
                  copySign *= 1.0 + d9;
               }

               copySign = Math.max(-this.data.getDoubleValue26(), Math.min(this.data.getDoubleValue26(), copySign));
               this.doubleValue = this.doubleValue + this.data.getDoubleValue14() * (copySign - this.doubleValue);
            }

            double d5 = flag2 ? this.data.getDoubleValue23() : this.data.getDoubleValue24();
            if (flag) {
               d5 = Math.max(d5, this.data.getDoubleValue22());
            }

            double d6 = this.method3647(d);
            if (this.data.getDoubleValue7() > d5) {
               d5 += (1.0 - d6) * (this.data.getDoubleValue7() - d5);
            }

            if (d5 > 0.0 && this.random.nextDouble() < d5) {
               double d7 = Math.max(1.0, this.data.getDoubleValue25());
               double d8 = -Math.log1p(-this.random.nextDouble()) * d7;
               this.intValue2 = Math.max(1, (int)Math.round(d8));
               this.doubleValue = 0.0;
               this.doubleValue2 = 0.0;
               return 0.0;
            } else {
               return this.method3650(this.doubleValue, d3, d);
            }
         }
      }

      private double method3650(double doubleValue, double d, double d2) {
         this.doubleValue2 = this.doubleValue2 + (doubleValue + this.data.getDoubleValue11() * d);
         double d3 = this.data.getDoubleValue26();
         double d4 = this.method3647(d2);
         if (d4 < 1.0) {
            d3 = Math.min(d3, this.data.getDoubleValue6() * d4);
         }

         double d5 = Math.max(-d3, Math.min(d3, this.doubleValue2));
         double d6 = Math.floor(d5 + 0.5);
         if (d6 != 0.0 && this.intValue3 >= this.data.getIntValue()) {
            this.doubleValue = 0.0;
            this.doubleValue2 = 0.0;
            this.intValue3 = 0;
            this.intValue4 = 0;
            return 0.0;
         } else {
            if (d6 == 0.0) {
               this.intValue3 = 0;
            } else if (this.intValue4 != 0 && d6 > 0.0 != this.intValue4 > 0) {
               this.intValue3 = 1;
            } else {
               this.intValue3++;
            }

            this.intValue4 = d6 > 0.0 ? 1 : (d6 < 0.0 ? -1 : 0);
            this.doubleValue2 -= d6;
            return d6;
         }
      }

      private static double[] method3651(double d, int n) {
         double[][] doubleses = new double[n + 1][];
         doubleses[0] = new double[]{1.0, 0.0, 0.0, 1.0};

         for (int i = 1; i <= n; i++) {
            doubleses[i] = new double[]{1.0, i, 0.0, 1.0};
         }

         double[] doubles = new double[]{0.25, 0.5, 0.5, 1.0};
         double[][] doubleses2 = new double[n + 1][];

         for (int i = 0; i <= n; i++) {
            double[] doubles2 = new double[]{0.0, 0.0, 0.0, 0.0};

            for (int j = 0; j < i; j++) {
               double[] doubles3 = doubleses[n - i + j];
               double[] doubles4 = new double[]{doubles3[0], doubles3[2], doubles3[1], doubles3[3]};
               double[] doubles5 = method3652(method3652(doubleses[j], doubles), doubles4);

               for (int k = 0; k < 4; k++) {
                  doubles2[k] += doubles5[k];
               }
            }

            doubleses2[i] = doubles2;
         }

         double[] doubles2 = doubleses2[n];
         double d2 = doubles2[0] * doubles2[3] - doubles2[1] * doubles2[2];
         if (Math.abs(d2) < 1.0E-12) {
            return new double[]{d};
         } else {
            double[] doubles3 = new double[]{doubles2[3] / d2, -doubles2[1] / d2, -doubles2[2] / d2, doubles2[0] / d2};
            double[] doubles4 = new double[n];

            for (int i = 1; i <= n; i++) {
               double[] doubles5 = method3652(doubleses2[i], doubles3);
               doubles4[i - 1] = doubles5[2] * d;
            }

            return doubles4;
         }
      }

      private static double[] method3652(double[] doubles, double[] doubles2) {
         return new double[]{
            doubles[0] * doubles2[0] + doubles[1] * doubles2[2],
            doubles[0] * doubles2[1] + doubles[1] * doubles2[3],
            doubles[2] * doubles2[0] + doubles[3] * doubles2[2],
            doubles[2] * doubles2[1] + doubles[3] * doubles2[3]
         };
      }
   }
}

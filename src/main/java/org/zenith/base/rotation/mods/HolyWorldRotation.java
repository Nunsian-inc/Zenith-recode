package org.zenith.base.rotation.mods;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.RotationContext;
import org.zenith.utility.misc.Vec2f;

public class HolyWorldRotation extends RotationContext {
   private int intValue = 1;
   private int intValue2 = 4;
   private int intValue3 = 6;
   private int intValue4 = 16;
   private int intValue5 = 25;
   private float floatValue = 0.12F;
   private int intValue6 = 1;
   private int intValue7 = 4;
   private float floatValue2 = 0.18F;
   private float floatValue3 = 0.22F;
   private float floatValue4 = 1.12F;
   private float floatValue5 = 0.42F;
   public float[] values = new float[]{0.0123967F, 0.053719F, 0.109504F, 0.17562F, 0.21281F, 0.272727F, 0.11157F, 0.0392562F, 0.0103306F, 0.00206612F};
   public int index = 0;
   private final Random random = new Random();
   private HolyWorldRotation.Entry3 entry3;
   private int intValue8 = Integer.MIN_VALUE;
   private int intValue9 = 0;
   private int intValue10 = 1;
   private int intValue11 = 0;
   private boolean flag = true;

   public Rotation method3431(Rotation rotation) {
      return rotation;
   }

   private Rotation method3432(Rotation rotation, Rotation rotation2) {
      if (this.values == null) {
         this.values = new float[]{0.0123967F, 0.053719F, 0.109504F, 0.17562F, 0.21281F, 0.272727F, 0.11157F, 0.0392562F, 0.0103306F, 0.00206612F};
      }

      float f = this.values[this.index] * 2.0F;
      this.index++;
      if (this.index >= this.values.length) {
         this.index = 0;
      }

      Vec2f vec2f = new Vec2f(Mth.wrapDegrees(rotation2.getFloatValue() - rotation.getFloatValue()) * f, (rotation2.getFloatValue2() - rotation.getFloatValue2()) * f);
      return rotation.add(vec2f);
   }

   private void method3433() {
      if (!this.flag && this.intValue9 > 0) {
         this.intValue9++;
      } else {
         this.intValue9 = 1;
         this.intValue10 = this.method3444();
      }
   }

   private Rotation method3434(Rotation rotation, Rotation rotation2) {
      if (this.entry3 != null && !this.entry3.isDone()) {
         HolyWorldRotation.Entry2 entry2 = this.entry3.method3451();
         Vec2f vec2f = rotation.wrappedDeltaTo(rotation2);
         int n = this.method3435(entry2.intValue, vec2f.getFloatValue(), this.entry3.floatValue);
         int n2 = this.method3435(entry2.intValue2, vec2f.getFloatValue2(), this.entry3.floatValue);
         Rotation rotation3 = rotation.add(n * this.entry3.floatValue, n2 * this.entry3.floatValue);
         if (this.entry3.isDone()) {
            this.method3447();
         }

         return rotation3;
      } else {
         this.method3447();
         return rotation;
      }
   }

   private int method3435(int intValue, float floatValue, float f) {
      if (intValue != 0 && this.isFinite(floatValue) && this.isFinite(f) && !(f <= 0.0F)) {
         boolean flag = Math.signum((float)intValue) == Math.signum(floatValue);
         float f2 = flag ? 1.8F : 0.3F;
         int n = Math.round(intValue * f2);
         if (n == 0) {
            n = intValue > 0 ? 1 : -1;
         }

         int n2 = Math.round(floatValue / f);
         return this.method3436(n, n2);
      } else {
         return 0;
      }
   }

   private int method3436(int n, int n2) {
      if (n != 0 && n2 != 0) {
         if (Integer.signum(n) != Integer.signum(n2)) {
            return Math.abs(n) <= 1 ? 0 : n;
         } else {
            return Math.abs(n) > Math.abs(n2) ? n2 : n;
         }
      } else {
         return 0;
      }
   }

   private HolyWorldRotation.Entry3 method3437(Rotation rotation, Rotation rotation2, float floatValue) {
      Vec2f vec2f = rotation.wrappedDeltaTo(rotation2);
      int x = Math.round(vec2f.getFloatValue() / floatValue);
      int n = Math.round(vec2f.getFloatValue2() / floatValue);
      int n2 = Math.abs(x) + Math.abs(n);
      if (n2 <= 0) {
         return null;
      } else {
         int n3 = this.method3445();
         int n4 = Math.max(1, Math.min(n3, n2));
         float f = (this.random.nextFloat() * 2.0F - 1.0F) * 0.1F;
         int[] intValues = this.method3439(x, this.method3438(n4, f));
         int[] intValues2 = this.method3439(n, this.method3438(n4, -f * 0.75F));
         this.method3442(intValues);
         this.method3442(intValues2);
         this.method3441(intValues, intValues2);
         return new HolyWorldRotation.Entry3(intValues, intValues2, floatValue);
      }
   }

   private float[] method3438(int n, float f) {
      float[] floats = new float[n];
      float f2 = Mth.clamp(0.46F + f + (this.random.nextFloat() - 0.5F) * this.floatValue3, 0.28F, 0.7F);
      float f3 = 0.18F + this.random.nextFloat() * 0.12F;
      float f4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.floatValue2;
      float f5 = 2.2F + this.random.nextFloat() * 0.6F;
      float f6 = 2.1F + this.random.nextFloat() * 0.7F;
      float floatValue = 0.0F;

      for (int i = 0; i < n; i++) {
         float f7 = (i + 0.5F) / n;
         float f8 = (f7 - f2) / f3;
         float exp = (float)Math.exp(-0.5F * f8 * f8);
         float f9 = 30.0F * f7 * f7 * (1.0F - f7) * (1.0F - f7);
         float f10 = Mth.clamp(f7 * f5, 0.0F, 1.0F);
         float f11 = Mth.clamp((1.0F - f7) * f6, 0.0F, 1.0F);
         f4 = f4 * 0.72F + (this.random.nextFloat() * 2.0F - 1.0F) * this.floatValue2 * 0.28F;
         float f12 = Mth.clamp(1.0F + f4, 0.58F, 1.48F);
         float f13 = Math.max(0.001F, (exp * 0.62F + f9 * 0.38F) * f10 * f11 * f12);
         floats[i] = f13;
         floatValue += f13;
      }

      if (!(floatValue <= 0.0F) && this.isFinite(floatValue)) {
         for (int i = 0; i < n; i++) {
            floats[i] /= floatValue;
         }

         return floats;
      } else {
         Arrays.fill(floats, 1.0F / Math.max(1, n));
         return floats;
      }
   }

   private int[] method3439(int x, float[] floats) {
      int length = floats.length;
      int[] intValues = new int[length];
      int n = Integer.compare(x, 0);
      int n2 = Math.abs(x);
      if (n2 == 0) {
         return intValues;
      } else {
         float f = 0.0F;
         int n3 = 0;

         for (int i = 0; i < length; i++) {
            f += floats[i];
            int n4 = i == length - 1 ? n2 : Math.round(f * n2);
            intValues[i] = n4 - n3;
            n3 = n4;
         }

         if (n < 0) {
            for (int i = 0; i < length; i++) {
               intValues[i] = -intValues[i];
            }
         }

         this.method3440(intValues);
         return intValues;
      }
   }

   private void method3440(int[] intValues) {
      int n = Integer.compare(this.sum(intValues), 0);
      if (n != 0 && intValues.length >= 3) {
         for (int i = 0; i < 2; i++) {
            for (int j = 1; j < intValues.length - 1; j++) {
               int n2 = Math.abs(intValues[j]);
               int n3 = Math.abs(intValues[j - 1]);
               int n4 = Math.abs(intValues[j + 1]);
               int n5 = Math.max(n3, n4);
               if (n2 > n5 + 4 && n2 > 2) {
                  int n6 = Math.min((n2 - n5) / 2, n2 - 1);
                  int n7 = n3 <= n4 ? j - 1 : j + 1;
                  intValues[j] -= n * n6;
                  intValues[n7] += n * n6;
               }
            }
         }
      }
   }

   private void method3441(int[] intValues, int[] intValues2) {
      for (int i = 0; i < intValues.length; i++) {
         if (intValues[i] == 0 && intValues2[i] == 0 && !this.method3443(intValues, i) && !this.method3443(intValues2, i)) {
            this.method3443(Math.abs(this.sum(intValues)) >= Math.abs(this.sum(intValues2)) ? intValues : intValues2, i);
         }
      }
   }

   private void method3442(int[] intValues) {
      if (Math.abs(this.sum(intValues)) >= intValues.length) {
         for (int i = 0; i < intValues.length; i++) {
            if (intValues[i] == 0) {
               this.method3443(intValues, i);
            }
         }
      }
   }

   private boolean method3443(int[] intValues, int n) {
      int n2 = -1;
      int n3 = 0;

      for (int i = 0; i < intValues.length; i++) {
         int n4 = Math.abs(intValues[i]);
         if (i != n && n4 > n3 && n4 > 1) {
            n2 = i;
            n3 = n4;
         }
      }

      if (n2 < 0) {
         return false;
      } else {
         int n4 = intValues[n2] > 0 ? 1 : -1;
         intValues[n2] -= n4;
         intValues[n] += n4;
         return true;
      }
   }

   private int sum(int[] intValues) {
      int n = 0;

      for (int intValue : intValues) {
         n += intValue;
      }

      return n;
   }

   private int method3444() {
      this.intValue2 = 2;
      int n = Math.max(0, Math.min(this.intValue, this.intValue2));
      int n2 = Math.max(n, Math.max(this.intValue, this.intValue2));
      return n + this.random.nextInt(n2 - n + 1);
   }

   private int method3445() {
      int n = Math.max(1, Math.min(this.intValue3, this.intValue4));
      int n2 = Math.max(n, Math.max(this.intValue3, this.intValue4));
      if (this.random.nextFloat() < this.floatValue) {
         n2 = Math.max(n2, this.intValue5);
      }

      return n + this.random.nextInt(n2 - n + 1);
   }

   private int method3446() {
      int n = Math.max(0, Math.min(this.intValue6, this.intValue7));
      int n2 = Math.max(n, Math.max(this.intValue6, this.intValue7));
      return n + this.random.nextInt(n2 - n + 1);
   }

   private void method3447() {
      this.entry3 = null;
      this.intValue9 = 0;
      this.intValue11 = this.method3446();
      this.intValue10 = this.method3444();
   }

   private HolyWorldRotation.Entry method3448(Rotation rotation, LivingEntity livingEntity) {
      if (rotation != null && livingEntity != null && mc.player != null) {
         AABB boundingBox = livingEntity.getBoundingBox();
         Vec3 eyePosition = mc.player.getEyePosition();
         Vec3 center = boundingBox.getCenter();
         Rotation rotation2 = RotationUtils.method6912(center.subtract(eyePosition));
         Vec2f vec2f = rotation.wrappedDeltaTo(rotation2);
         float floatValue = vec2f.getFloatValue();
         float f = vec2f.getFloatValue2();
         if (this.isFinite(floatValue) && this.isFinite(f)) {
            float floatValue2 = Float.MAX_VALUE;
            float floatValue3 = -Float.MAX_VALUE;
            float f2 = Float.MAX_VALUE;
            float f3 = -Float.MAX_VALUE;
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
               float floatValue4 = vec2f2.getFloatValue2();
               if (this.isFinite(vec2f2.getFloatValue()) && this.isFinite(floatValue4)) {
                  float f4 = this.method3449(vec2f2.getFloatValue(), floatValue);
                  floatValue2 = Math.min(floatValue2, f4);
                  floatValue3 = Math.max(floatValue3, f4);
                  f2 = Math.min(f2, floatValue4);
                  f3 = Math.max(f3, floatValue4);
               }
            }

            if (floatValue2 == Float.MAX_VALUE) {
               floatValue2 = floatValue;
               floatValue3 = floatValue;
               f2 = f;
               f3 = f;
            }

            boolean flag = floatValue2 <= 0.0F && floatValue3 >= 0.0F;
            boolean flag2 = f2 <= 0.0F && f3 >= 0.0F;
            return new HolyWorldRotation.Entry(flag, flag2);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private float method3449(float floatValue, float f) {
      float angle = floatValue - f;
      return !Float.isFinite(angle) ? floatValue : f + Mth.wrapDegrees(angle);
   }

   private void method3450() {
      this.entry3 = null;
      this.intValue9 = 0;
      this.intValue11 = 0;
      this.intValue10 = this.method3444();
      this.flag = true;
      this.intValue8 = Integer.MIN_VALUE;
   }

   private boolean isFinite(float floatValue) {
      return !Float.isNaN(floatValue) && !Float.isInfinite(floatValue);
   }

   private static final class Entry {
      public final boolean flag;

      public Entry(boolean flag, boolean flag2) {
         this.flag = flag && flag2;
      }
   }

   private static final class Entry2 {
      public final int intValue;
      public final int intValue2;

      public Entry2(int intValue, int n) {
         this.intValue = intValue;
         this.intValue2 = n;
      }
   }

   private static final class Entry3 {
      private final int[] intValues;
      private final int[] intValues2;
      private final float floatValue;
      private int index;

      public Entry3(int[] intValues, int[] intValues2, float floatValue) {
         this.intValues = intValues;
         this.intValues2 = intValues2;
         this.floatValue = floatValue;
      }

      public HolyWorldRotation.Entry2 method3451() {
         HolyWorldRotation.Entry2 entry2 = new HolyWorldRotation.Entry2(this.intValues[this.index], this.intValues2[this.index]);
         this.index++;
         return entry2;
      }

      public boolean isDone() {
         return this.index >= this.intValues.length;
      }
   }
}

package org.zenith.base.rotation;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;

public final class RotationSmoother implements MinecraftAccess {
   private static final RotationSmoother.Entry entry = new RotationSmoother.Entry();

   public static Rotation method7954(Vec3 location) {
      return method7955(entry, location);
   }

   public static Rotation method7955(RotationSmoother.Entry entry, Vec3 location) {
      return method7957(entry, RotationUtils.method6913(location));
   }

   public static Rotation method7956(Rotation rotation) {
      return method7957(entry, rotation);
   }

   public static Rotation method7957(RotationSmoother.Entry entry, Rotation rotation) {
      return method7959(entry, rotation, 34.0F, 18.0F, 0.55F);
   }

   public static Rotation method7958(Rotation rotation, float f, float f2, float f3) {
      return method7959(entry, rotation, f, f2, f3);
   }

   public static Rotation method7959(RotationSmoother.Entry entry2, Rotation rotation, float f, float f2, float f3) {
      RotationSmoother.Entry entry3 = entry2 == null ? entry : entry2;
      Rotation rotation2 = entry3.rotation == null ? RotationUtils.method6911() : entry3.rotation;
      Rotation rotation3 = method7961(entry3, rotation2, rotation, f, f2, f3);
      return rotation;
   }

   public static Rotation method7960(Rotation rotation, Rotation rotation2, float f, float f2, float f3) {
      return method7961(entry, rotation, rotation2, f, f2, f3);
   }

   public static Rotation method7961(RotationSmoother.Entry entry2, Rotation rotation, Rotation rotation2, float f, float f2, float f3) {
      RotationSmoother.Entry entry3 = entry2 == null ? entry : entry2;
      method7964(entry3);
      float x = Mth.wrapDegrees(rotation2.getFloatValue() - rotation.getFloatValue());
      float x2 = rotation2.getFloatValue2() - rotation.getFloatValue2();
      float hypot = (float)Math.hypot(x, x2);
      float f4 = Mth.clamp(hypot / 12.0F, 0.0F, 1.0F);
      float f5 = Mth.clamp(hypot / 45.0F, 0.18F, 1.0F);
      method7965(entry3, f4);
      float alpha1 = Mth.clamp(f3 * entry3.smoothScale, 0.08F, 0.95F);
      float max = method7968(x, f * entry3.floatValue3, alpha1, f5);
      float max2 = method7968(x2, f2 * entry3.floatValue3, alpha1, f5);
      float f6 = method7966(x, entry3.floatValue4 + entry3.floatValue6, f4);
      float f7 = method7966(x2, entry3.floatValue5 + entry3.floatValue7, f4);
      float f8 = Mth.clamp(x + f6, -max, max);
      float f9 = Mth.clamp(x2 + f7, -max2, max2);
      entry3.floatValue = Mth.lerp(alpha1, entry3.floatValue, f8);
      entry3.floatValue2 = Mth.lerp(alpha1, entry3.floatValue2, f9);
      entry3.floatValue = method7967(entry3.floatValue, x, hypot);
      entry3.floatValue2 = method7967(entry3.floatValue2, x2, hypot);
      Rotation rotation3 = rotation.method3140(entry3.floatValue, entry3.floatValue2);
      return rotation3.method3139(rotation);
   }

   public static void method7962(Rotation rotation) {
      if (mc.player != null && rotation != null) {
         mc.player.setYRot(rotation.getFloatValue());
         mc.player.setXRot(rotation.getFloatValue2());
      }
   }

   public static void reset() {
      method7963(entry);
   }

   public static void method7963(RotationSmoother.Entry entry2) {
      RotationSmoother.Entry entry3 = entry2 == null ? entry : entry2;
      entry3.reset();
   }

   private static void method7964(RotationSmoother.Entry entry) {
      if (entry.intValue-- <= 0) {
         entry.intValue = method7969(3, 9);
         entry.floatValue3 = method7970(0.86F, 1.18F);
         entry.smoothScale = method7970(0.82F, 1.08F);
         entry.floatValue4 = method7971(0.18F);
         entry.floatValue5 = method7971(0.11F);
      }
   }

   private static void method7965(RotationSmoother.Entry entry, float f) {
      float bound = 0.025F + f * 0.08F;
      entry.floatValue6 = Mth.lerp(0.22F, entry.floatValue6, method7971(bound));
      entry.floatValue7 = Mth.lerp(0.22F, entry.floatValue7, method7971(bound * 0.65F));
   }

   private static float method7966(float x, float f, float f2) {
      float f3 = Math.signum(x);
      float f4 = 0.0F;
      if (Math.abs(x) > 8.0F) {
         f4 = Mth.clamp(x * method7970(0.01F, 0.035F), -1.15F, 1.15F);
      }

      return f4 + f * f2 + f3 * method7970(0.0F, 0.018F) * f2;
   }

   private static float method7967(float floatValue, float x, float hypot) {
      return !(hypot > 2.0F) && Math.signum(floatValue) != Math.signum(x) ? floatValue * method7970(0.35F, 0.7F) : floatValue;
   }

   private static float method7968(float x, float f, float alpha1, float f2) {
      float f3 = Math.abs(x);
      float f4 = Math.max(0.1F, f);
      float f5 = Mth.clamp(f3 / 35.0F, 0.22F, 1.0F);
      float f6 = 0.72F + f2 * method7970(0.22F, 0.38F);
      return Math.max(Rotation.method3138(), f4 * f5 * alpha1 * f6);
   }

   private static int method7969(int origin, int n) {
      return ThreadLocalRandom.current().nextInt(origin, n + 1);
   }

   private static float method7970(float origin, float bound) {
      return (float)ThreadLocalRandom.current().nextDouble(origin, bound);
   }

   private static float method7971(float bound) {
      return method7970(-bound, bound);
   }

   @Generated
   private RotationSmoother() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Entry {
      private Rotation rotation;
      private float floatValue;
      private float floatValue2;
      private float floatValue3 = 1.0F;
      private float smoothScale = 1.0F;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private int intValue;

      private void reset() {
         this.rotation = null;
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
         this.floatValue3 = 1.0F;
         this.smoothScale = 1.0F;
         this.floatValue4 = 0.0F;
         this.floatValue5 = 0.0F;
         this.floatValue6 = 0.0F;
         this.floatValue7 = 0.0F;
         this.intValue = 0;
      }
   }
}

package org.zenith.utility.render;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.zenith.MinecraftAccess;

public final class MathUtils implements MinecraftAccess {
   public static double doubleValue = Math.PI * 2;
   private static final int CONST_intValue = 65536;
   private static final double CONST_doubleValue = Math.PI * 2;
   private static final double[] CONST_doubleValues = new double[65536];

   public static double sin(double d) {
      int n = (int)(d * 10430.378350470453) & 65535;
      return CONST_doubleValues[n];
   }

   public static double cos(double d) {
      int n = (int)(d * 10430.378350470453 + 16384.0) & 65535;
      return CONST_doubleValues[n];
   }

   public static float method2642(float f, float f2, float f3) {
      float f4 = Math.min(f3 / f2, 1.0F);
      return f * (1.0F + f4 * f4 * 0.5F);
   }

   public static float method2643(double d, double d2) {
      return (float)(d + (d2 - d) * Math.random());
   }

   public static double method2644(double d, double x, double x2, double x3, double x4) {
      return Math.pow(1.0 - d, 3.0) * x
         + 3.0 * d * Math.pow(1.0 - d, 2.0) * x2
         + 3.0 * Math.pow(d, 2.0) * (1.0 - d) * x3
         + Math.pow(d, 3.0) * x4;
   }

   public static int method2645(String str, String str2) {
      int length = str.length();
      int n = str2.length();
      int[] ints = new int[n + 1];
      int n2 = 0;

      while (n2 <= n) {
         ints[n2] = n2++;
      }

      for (int i = 1; i <= length; i++) {
         int n3 = ints[0];
         ints[0] = i;

         for (int j = 1; j <= n; j++) {
            int n4 = ints[j];
            int n5 = str.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
            ints[j] = Math.min(Math.min(ints[j] + 1, ints[j - 1] + 1), n3 + n5);
            n3 = n4;
         }
      }

      return ints[n];
   }

   public static float method2646(float f, float f2) {
      float f3 = (f - f2) % 360.0F;
      if (f3 < -180.0F) {
         f3 += 360.0F;
      } else if (f3 > 180.0F) {
         f3 -= 360.0F;
      }

      return f3;
   }

   public static boolean method2647(double x, double y, double floatValue, double d, double width, double height) {
      return x >= floatValue && x <= floatValue + width && y >= d && y <= d + height;
   }

   public static boolean method2648(double d, double d2, int n, int n2, int n3, int n4) {
      return d >= n && d <= n3 && d2 >= n2 && d2 <= n4;
   }

   public static float method2649(double intValue, double d, double hover) {
      return (float)(intValue + (d - intValue) * hover);
   }

   public static float method2650(float y, float f) {
      return Math.max(y, f) - Math.min(y, f);
   }

   public static double method2651(double d, double d2) {
      if (d == d2) {
         return d;
      } else {
         if (d > d2) {
            double d3 = d;
            d = d2;
            d2 = d3;
         }

         return ThreadLocalRandom.current().nextDouble() * (d2 - d) + d;
      }
   }

   public static double round(double d) {
      return (float)Math.round(d * 10.0) / 10.0F;
   }

   public static float round(float x) {
      return Math.round(x * 10.0F) / 10.0F;
   }

   public static double method2652(double cooldownPercent, double d) {
      double d2 = Math.round(cooldownPercent / d) * d;
      return Math.round(d2 * 100.0) / 100.0;
   }

   public static Vec3 method2653(float f, float f2, double floatValue) {
      float f3 = Math.min(f, f2);
      float x = (float)(Math.cos(f3 * doubleValue / f2) * floatValue);
      float z = (float)(-Math.sin(f3 * doubleValue / f2) * floatValue);
      return new Vec3(x, 0.0, z);
   }

   public static Vector3d method2654(Vector3d vec, Vector3d vec2) {
      return new Vector3d(method2658(vec.x, vec2.x), method2658(vec.y, vec2.y), method2658(vec.z, vec2.z));
   }

   public static Vec3 method2655(Vec3 vec3d2, Vec3 vec3d) {
      return new Vec3(method2658(vec3d2.x, vec3d.x), method2658(vec3d2.y, vec3d.y), method2658(vec3d2.z, vec3d.z));
   }

   public static Vec3 method2656(Entity player) {
      return player == null
         ? Vec3.ZERO
         : new Vec3(method2658(player.xo, player.getX()), method2658(player.yo, player.getY()), method2658(player.zo, player.getZ()));
   }

   public static float method2657(float f, float f2) {
      return Mth.lerp(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false), f, f2);
   }

   public static double method2658(double xo, double x) {
      return Mth.lerp((double)mc.getDeltaTracker().getGameTimeDeltaPartialTick(false), xo, x);
   }

   public static int method2659(double d, int n, int n2) {
      return (int)Mth.lerp(mc.getDeltaTracker().getRealtimeDeltaTicks() / d, (double)n, (double)n2);
   }

   public static float method2660(double d, float f, float floatValue) {
      return (float)Mth.lerp(mc.getDeltaTracker().getRealtimeDeltaTicks() / d, (double)f, (double)floatValue);
   }

   public static double method2661(double d, double d2, double d3) {
      return Mth.lerp(mc.getDeltaTracker().getRealtimeDeltaTicks() / d, d2, d3);
   }

   public static double method2662(Vec3 atCenterOf, Vec3 vec) {
      double d = atCenterOf.x() - vec.x();
      double d2 = atCenterOf.y() - vec.y();
      double d3 = atCenterOf.z() - vec.z();
      return Mth.sqrt((float)(d * d + d2 * d2 + d3 * d3));
   }

   public static double method2663(double d) {
      return Math.abs(1.0 + Math.sin(d)) / 2.0;
   }

   public static float method2664(float f) {
      return (float)Math.sin(f * Math.PI / 2.0);
   }

   @Generated
   private MathUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   static {
      for (int i = 0; i < 65536; i++) {
         CONST_doubleValues[i] = Math.sin(i * (Math.PI * 2) / 65536.0);
      }
   }
}

package org.zenith.base.rotation.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.RotationContext;

public final class Motor extends RotationContext {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final String text = "motor.json";
   private static final String text2 = "models/motor.json";
   private static final List<Path> items = List.of(Path.of("motor.json"), Path.of("models", "motor.json"), Path.of("..", "models", "motor.json"));
   private static final String text3 = "neurotest-motor-v1";
   private static final int CONST_intValue2 = 47;
   private static final int CONST_intValue3 = 11;
   private static final double CONST_doubleValue = 1.0E-9;
   private static final double CONST_doubleValue2 = 1.0;
   private static final boolean CONST_flag = false;
   private static final double CONST_doubleValue3 = 0.0;
   private static final double CONST_doubleValue4 = 0.35;
   private static final double CONST_doubleValue5 = 20.0;
   private static final boolean CONST_flag2 = false;
   private static final int CONST_intValue4 = 8;
   private Motor.Entry4 entry4;
   private boolean flag;
   private CompletableFuture<Motor.Entry4> completableFuture;
   private long longValue;
   private final Random random = new Random();
   private double[][] doubleValues;
   private final Motor.Entry[] entrys = new Motor.Entry[8];
   private int intValue;
   private final double[] doubleValues2 = new double[2];
   private final double[] doubleValues3 = new double[2];
   private final double[] doubleValues4 = new double[2];
   private final double[] doubleValues5 = new double[2];
   private final double[] doubleValues6 = new double[2];
   private final double[] doubleValues7 = new double[2];
   private final double[] doubleValues8 = new double[2];
   private float floatValue = 1.0F;
   private boolean flag2;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4 = Integer.MIN_VALUE;
   private Rotation rotation;
   public float offset = 0.0F;

   public Rotation method3659() {
      Rotation rotation = rotationManager.getEffectiveRotation();
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      if (mc.player != null && livingEntity != null && livingEntity.isAlive()) {
         Motor.Entry4 entry4 = this.method3673();
         if (entry4 == null) {
            return rotation;
         } else {
            int tickCount = mc.player.tickCount;
            if (tickCount == this.intValue4 && this.rotation != null) {
               return this.rotation;
            } else {
               if (this.intValue3 != Integer.MIN_VALUE && tickCount != this.intValue3 + 1) {
                  this.method3670(livingEntity.getId(), tickCount);
               } else if (livingEntity.getId() != this.intValue2) {
                  this.method3671(livingEntity.getId());
               }

               this.intValue3 = tickCount;
               float f = Rotation.sensitivityStep();
               if (Float.isFinite(f) && !(f <= 0.0F)) {
                  this.method3669();
                  Motor.Entry entry = this.method3661(rotation, livingEntity);
                  if (entry == null) {
                     this.method3672(tickCount, rotation);
                     return rotation;
                  } else {
                     this.method3662(entry);
                     double[] doubleValues = this.method3664(entry4, rotation);
                     if (!method3679(doubleValues)) {
                        this.method3672(tickCount, rotation);
                        return rotation;
                     } else {
                        Motor.Entry5 entry5 = entry4.method3683(doubleValues, this.doubleValues);
                        double[] doubles = this.method3665(entry4, entry5);
                        double angle = entry.doubleValue3 - rotation.getFloatValue();
                        angle = Mth.wrapDegrees((float)angle);
                        double d = entry.doubleValue4 - rotation.getFloatValue2();
                        boolean flag = false;
                        if (flag) {
                           doubles[0] = method3677(0.35 * angle, -20.0, 20.0);
                           doubles[1] = method3677(0.35 * d, -20.0, 20.0);
                        }

                        if (entry4.method3681()) {
                           for (int i = 0; i < 2; i++) {
                              double d2 = Math.max(Math.abs(this.doubleValues2[i]), Math.abs(this.doubleValues3[i])) / f;
                              double d3 = entry4.method3682(i, d2) * f;
                              doubles[i] = method3677(doubles[i], -d3, d3);
                           }
                        }

                        int n = this.method3667(doubles[0], 0, f, entry4.flag);
                        int n2 = this.method3667(doubles[1], 1, f, entry4.flag);
                        Rotation rotation2 = rotation.add(n * f, n2 * f);
                        double d2 = Mth.wrapDegrees(rotation2.getFloatValue() - rotation.getFloatValue());
                        double d3 = rotation2.getFloatValue2() - rotation.getFloatValue2();
                        this.method3668(d2, d3);
                        if (n == 0 && n2 == 0) {
                           this.method3672(tickCount, rotation);
                           return rotation;
                        } else {
                           this.method3672(tickCount, rotation2);
                           return rotation2;
                        }
                     }
                  }
               } else {
                  this.method3672(tickCount, rotation);
                  return rotation;
               }
            }
         }
      } else {
         this.reset();
         if (mc.player != null) {
            mc.player.setYRot(rotation.getFloatValue());
            mc.player.setXRot(rotation.getFloatValue2());
         }

         return rotation;
      }
   }

   public boolean method3660() {
      return this.method3673() != null;
   }

   private Motor.Entry method3661(Rotation rotation, LivingEntity livingEntity) {
      Vec3 eyePosition = mc.player.getEyePosition();
      AABB boundingBox = livingEntity.getBoundingBox();
      Vec3 center = boundingBox.getCenter();
      double x = center.x - eyePosition.x;
      double d = center.y - eyePosition.y;
      double x2 = center.z - eyePosition.z;
      if (x * x + d * d + x2 * x2 < 1.0E-10) {
         return null;
      } else {
         Motor.Entry entry = new Motor.Entry();
         entry.doubleValue = rotation.getFloatValue();
         entry.doubleValue2 = rotation.getFloatValue2();
         entry.doubleValue3 = Math.toDegrees(Math.atan2(-x, x2));
         entry.doubleValue4 = Math.toDegrees(Math.atan2(-d, Math.hypot(x, x2)));
         entry.doubleValue9 = Math.sqrt(x * x + d * d + x2 * x2);
         double d2 = Double.MAX_VALUE;
         double d3 = -Double.MAX_VALUE;
         double d4 = Double.MAX_VALUE;
         double d5 = -Double.MAX_VALUE;

         for (int i = 0; i < 8; i++) {
            double x3 = ((i & 1) == 0 ? boundingBox.minX : boundingBox.maxX) - eyePosition.x;
            double d6 = ((i & 2) == 0 ? boundingBox.minY : boundingBox.maxY) - eyePosition.y;
            double x4 = ((i & 4) == 0 ? boundingBox.minZ : boundingBox.maxZ) - eyePosition.z;
            double d7 = Math.toDegrees(Math.atan2(-x3, x4));
            double d8 = Math.toDegrees(Math.atan2(-d6, Math.hypot(x3, x4)));
            if (Double.isFinite(d7) && Double.isFinite(d8)) {
               double d9 = Mth.wrapDegrees(d7 - entry.doubleValue3);
               double d10 = d8 - entry.doubleValue4;
               d2 = Math.min(d2, d9);
               d3 = Math.max(d3, d9);
               d4 = Math.min(d4, d10);
               d5 = Math.max(d5, d10);
            }
         }

         if (d2 == Double.MAX_VALUE) {
            return null;
         } else {
            entry.doubleValue5 = d2;
            entry.doubleValue6 = d3;
            entry.doubleValue7 = d4;
            entry.doubleValue8 = d5;
            return entry;
         }
      }
   }

   private void method3662(Motor.Entry entry) {
      System.arraycopy(this.entrys, 0, this.entrys, 1, 7);
      this.entrys[0] = entry;
      if (this.intValue < 8) {
         this.intValue++;
      }
   }

   private Motor.Entry method3663(int n) {
      return this.entrys[Math.min(n, this.intValue - 1)];
   }

   private double[] method3664(Motor.Entry4 entry4, Rotation rotation) {
      int n = entry4.intValue5;
      Motor.Entry entry = this.method3663(n);
      Motor.Entry entry2 = this.method3663(n + 1);
      Motor.Entry entry3 = this.method3663(n + 2);
      double doubleValue = entry4.doubleValue;
      double floatValue = rotation.getFloatValue();
      double d = rotation.getFloatValue2();
      double x = method3675(entry.doubleValue3 - floatValue);
      double y = entry.doubleValue4 - d;
      double d2 = method3675(entry.doubleValue3 - entry.doubleValue);
      double d3 = entry.doubleValue4 - entry.doubleValue2;
      double d4 = method3675(entry.doubleValue3 - entry2.doubleValue3);
      double d5 = entry.doubleValue4 - entry2.doubleValue4;
      double d6 = d4 - method3675(entry2.doubleValue3 - entry3.doubleValue3);
      double d7 = d5 - (entry2.doubleValue4 - entry3.doubleValue4);
      double d8 = method3675(entry.doubleValue3 + (n + 1) * d4 - floatValue);
      double d9 = entry.doubleValue4 + (n + 1) * d5 - d;
      double d10 = Math.max(Math.hypot(x, y), 1.0E-6);
      double d11 = 0.5 * (entry.doubleValue5 + entry.doubleValue6);
      double d12 = 0.5 * (entry.doubleValue7 + entry.doubleValue8);
      double d13 = Math.max(0.5 * (entry.doubleValue6 - entry.doubleValue5), 0.001);
      double d14 = Math.max(0.5 * (entry.doubleValue8 - entry.doubleValue7), 0.001);
      double x2 = (-x - d11) / d13;
      double x3 = (-y - d12) / d14;
      double d15 = method3676(entry4.doubleValue4 * (1.0 - Math.max(Math.abs(x2), Math.abs(x3))));
      double[] doubles = new double[entry4.intValue];
      int n2 = 0;
      doubles[n2++] = asinh(x / doubleValue);
      doubles[n2++] = asinh(y / doubleValue);
      doubles[n2++] = asinh(d2 / doubleValue);
      doubles[n2++] = asinh(d3 / doubleValue);
      doubles[n2++] = asinh(d8 / doubleValue);
      doubles[n2++] = asinh(d9 / doubleValue);
      doubles[n2++] = asinh(d10 / doubleValue);
      doubles[n2++] = x / d10;
      doubles[n2++] = y / d10;
      doubles[n2++] = asinh(d4 / doubleValue);
      doubles[n2++] = asinh(d5 / doubleValue);
      doubles[n2++] = asinh(d6 / doubleValue);
      doubles[n2++] = asinh(d7 / doubleValue);
      doubles[n2++] = method3677(x2, -4.0, 4.0);
      doubles[n2++] = method3677(x3, -4.0, 4.0);
      doubles[n2++] = d15;
      doubles[n2++] = asinh(d13 / doubleValue);
      doubles[n2++] = asinh(d14 / doubleValue);
      doubles[n2++] = Math.log1p(entry.doubleValue9);
      doubles[n2++] = method3677(entry.doubleValue9 - entry2.doubleValue9, -2.0, 2.0);
      doubles[n2++] = d / 90.0;
      doubles[n2++] = (90.0 - Math.abs(d)) / 90.0;
      doubles[n2++] = asinh(this.doubleValues2[0] / doubleValue);
      doubles[n2++] = asinh(this.doubleValues2[1] / doubleValue);
      doubles[n2++] = asinh(this.doubleValues3[0] / doubleValue);
      doubles[n2++] = asinh(this.doubleValues3[1] / doubleValue);
      doubles[n2++] = asinh((this.doubleValues2[0] - this.doubleValues3[0]) / doubleValue);
      doubles[n2++] = asinh((this.doubleValues2[1] - this.doubleValues3[1]) / doubleValue);
      doubles[n2++] = Math.tanh(this.doubleValues4[0] / 8.0);
      doubles[n2++] = Math.tanh(this.doubleValues4[1] / 8.0);
      doubles[n2++] = Math.tanh(this.doubleValues5[0] / 8.0);
      doubles[n2++] = Math.tanh(this.doubleValues5[1] / 8.0);
      doubles[n2++] = method3677(Math.abs(this.doubleValues2[0]) / Math.max(this.doubleValues6[0], 1.0E-6), 0.0, 4.0);
      doubles[n2++] = method3677(Math.abs(this.doubleValues2[1]) / Math.max(this.doubleValues6[1], 1.0E-6), 0.0, 4.0);
      doubles[n2++] = asinh(this.doubleValues6[0] / doubleValue);
      doubles[n2++] = asinh(this.doubleValues6[1] / doubleValue);
      doubles[n2++] = mc.options.keyUp.isDown() ? 1.0 : 0.0;
      doubles[n2++] = mc.options.keyDown.isDown() ? 1.0 : 0.0;
      doubles[n2++] = mc.options.keyLeft.isDown() ? 1.0 : 0.0;
      doubles[n2++] = mc.options.keyRight.isDown() ? 1.0 : 0.0;
      doubles[n2++] = mc.options.keyJump.isDown() ? 1.0 : 0.0;
      doubles[n2++] = mc.player.onGround() ? 1.0 : 0.0;
      doubles[n2++] = mc.player.isSprinting() ? 1.0 : 0.0;
      doubles[n2++] = mc.player.isCrouching() ? 1.0 : 0.0;
      doubles[n2++] = mc.player.getAttackStrengthScale(0.0F);
      doubles[n2++] = Mth.clamp((float)mc.player.attackStrengthTicker, 0.0F, 50.0F);
      doubles[n2] = this.flag2 ? 1.0 : 0.0;
      return doubles;
   }

   private double[] method3665(Motor.Entry4 entry4, Motor.Entry5 entry5) {
      boolean flag = this.random.nextDouble() < method3676(entry5.doubleValue);
      if (flag) {
         return new double[]{0.0, 0.0};
      } else {
         int n = this.method3666(entry5.doubleValues);
         double nextGaussian = this.random.nextGaussian();
         double d = this.random.nextGaussian();
         double d2 = Math.exp(entry5.doubleValues3[n][0]) * 1.0;
         double d3 = Math.exp(entry5.doubleValues3[n][1]) * 1.0;
         double d4 = entry5.doubleValues4[n];
         double x = entry5.doubleValues2[n][0] + d2 * nextGaussian;
         double x2 = entry5.doubleValues2[n][1] + d3 * (d4 * nextGaussian + Math.sqrt(Math.max(1.0 - d4 * d4, 1.0E-6)) * d);
         x = method3677(x, -entry4.doubleValue11, entry4.doubleValue11);
         x2 = method3677(x2, -entry4.doubleValue12, entry4.doubleValue12);
         nextGaussian = method3677(entry4.doubleValue2 * Math.sinh(x), -entry4.doubleValue5, entry4.doubleValue5);
         d = method3677(entry4.doubleValue3 * Math.sinh(x2), -entry4.doubleValue6, entry4.doubleValue6);
         return new double[]{nextGaussian, d};
      }
   }

   private int method3666(double[] doubleValues) {
      double d = Double.NEGATIVE_INFINITY;

      for (double doubleValue : doubleValues) {
         d = Math.max(d, doubleValue);
      }

      double d2 = 0.0;
      double[] doubles = new double[doubleValues.length];

      for (int i = 0; i < doubleValues.length; i++) {
         doubles[i] = Math.exp(doubleValues[i] - d);
         d2 += doubles[i];
      }

      double d3 = this.random.nextDouble() * d2;
      double d4 = 0.0;

      for (int i = 0; i < doubles.length; i++) {
         d4 += doubles[i];
         if (d3 <= d4) {
            return i;
         }
      }

      return doubles.length - 1;
   }

   private int method3667(double d, int n, double d2, boolean flag) {
      double d3 = d + (this.entry4.flag2 ? this.doubleValues8[n] : 0.0);
      double d4 = d3 / d2;
      d4 += Math.signum(d4) * 1.0E-4;
      long l = flag ? Math.round(d4) : (long)d4;
      double d5 = l * d2;
      this.doubleValues8[n] = this.entry4.flag2 ? method3677(d3 - d5, -d2, d2) : 0.0;
      return (int)l;
   }

   private void method3668(double d, double d2) {
      double[] doubles = new double[]{d, d2};

      for (int i = 0; i < 2; i++) {
         double d3 = doubles[i];
         boolean flag = Math.abs(d3) > this.entry4.doubleValue8;
         double d4 = Math.signum(d3);
         boolean flag2 = flag && d4 * this.doubleValues7[i] < 0.0;
         this.doubleValues4[i] = flag ? 0.0 : this.doubleValues4[i] + 1.0;
         this.doubleValues5[i] = flag2 ? 0.0 : this.doubleValues5[i] + 1.0;
         this.doubleValues6[i] = Math.max(this.doubleValues6[i] * this.entry4.doubleValue7, Math.abs(d3));
         if (flag) {
            this.doubleValues7[i] = d4;
         }

         this.doubleValues3[i] = this.doubleValues2[i];
         this.doubleValues2[i] = d3;
      }
   }

   private void method3669() {
      float attackStrengthScale = mc.player.getAttackStrengthScale(0.0F);
      this.flag2 = attackStrengthScale < this.floatValue - 1.0E-4F;
      this.floatValue = attackStrengthScale;
   }

   private void method3670(int id, int tickCount) {
      Motor.Entry4 entry4 = this.entry4;
      this.doubleValues = entry4 == null ? null : new double[entry4.layers][entry4.intValue3];
      Arrays.fill(this.entrys, null);
      this.intValue = 0;
      Arrays.fill(this.doubleValues2, 0.0);
      Arrays.fill(this.doubleValues3, 0.0);
      Arrays.fill(this.doubleValues4, 0.0);
      Arrays.fill(this.doubleValues5, 0.0);
      Arrays.fill(this.doubleValues6, 0.0);
      Arrays.fill(this.doubleValues7, 0.0);
      Arrays.fill(this.doubleValues8, 0.0);
      this.floatValue = 1.0F;
      this.flag2 = false;
      this.intValue2 = id;
      this.intValue3 = Integer.MIN_VALUE;
      this.intValue4 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3671(int id) {
      this.intValue2 = id;
      this.intValue4 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3672(int tickCount, Rotation rotation) {
      this.intValue4 = tickCount;
      this.rotation = rotation;
   }

   public void reset() {
      this.method3670(Integer.MIN_VALUE, 0);
   }

   private Motor.Entry4 method3673() {
      if (this.entry4 != null || this.flag) {
         return this.entry4;
      } else if (this.completableFuture == null) {
         this.longValue = System.currentTimeMillis();
         this.completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
               Motor.Entry4 entry4 = method3674();
               entry4.method3684();
               return entry4;
            } catch (IOException e) {
               throw new CompletionException(e);
            }
         });
         return null;
      } else if (!this.completableFuture.isDone()) {
         return null;
      } else {
         try {
            this.entry4 = this.completableFuture.join();
            this.doubleValues = new double[this.entry4.layers][this.entry4.intValue3];
            System.out
               .println(
                  "v4 motor loaded: "
                     + this.entry4.intValue
                     + " признаков, GRU "
                     + this.entry4.layers
                     + "x"
                     + this.entry4.intValue3
                     + ", смесь "
                     + this.entry4.intValue4
                     + ", latency "
                     + this.entry4.intValue5
                     + ", масштаб действия "
                     + this.entry4.doubleValue2
                     + "/"
                     + this.entry4.doubleValue3
                     + ", огибающая "
                     + (this.entry4.method3681() ? this.entry4.doubleValues.length + " корзин" : "выключена")
                     + ", эталон сошёлся, "
                     + (System.currentTimeMillis() - this.longValue)
                     + " мс"
               );
         } catch (Exception e) {
            Object cause = e instanceof CompletionException && e.getCause() != null ? e.getCause() : e;
            this.entry4 = null;
            this.flag = true;
            System.out.println("v4 не смог загрузить motor.json: " + cause);
         }

         this.completableFuture = null;
         return this.entry4;
      }
   }

   private static Motor.Entry4 method3674() throws IOException {
      for (Path path : items) {
         if (Files.isRegularFile(path)) {
            Motor.Entry4 entry4 = Motor.Entry4.method3687(path);
            System.out.println("v4 motor: " + path.toAbsolutePath());
            return entry4;
         }
      }

      InputStream resourceAsStream = Motor.class.getClassLoader().getResourceAsStream("models/motor.json");
      if (resourceAsStream == null) {
         throw new IOException("motor.json не найден ни рядом с клиентом, ни в jar");
      } else {
         Motor.Entry4 entry4;
         try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
            entry4 = Motor.Entry4.method3688(inputStreamReader);
         }

         return entry4;
      }
   }

   private static double method3675(double angle) {
      return Mth.wrapDegrees((float)angle);
   }

   private static double method3676(double doubleValue) {
      return 1.0 / (1.0 + Math.exp(-doubleValue));
   }

   private static double asinh(double d) {
      return d >= 0.0 ? Math.log(d + Math.sqrt(d * d + 1.0)) : -Math.log(-d + Math.sqrt(d * d + 1.0));
   }

   private static double method3677(double x, double d, double d2) {
      return Math.max(d, Math.min(d2, x));
   }

   private static int method3678(double[] doubles) {
      int n = 0;

      for (int i = 1; i < doubles.length; i++) {
         if (doubles[i] > doubles[n]) {
            n = i;
         }
      }

      return n;
   }

   private static boolean method3679(double[] doubleValues) {
      for (double doubleValue : doubleValues) {
         if (!Double.isFinite(doubleValue)) {
            return false;
         }
      }

      return true;
   }

   private static String method3680(double d) {
      return String.format(Locale.ROOT, "%.3f", d);
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
   }

   private static final class Entry2 {
      public final double[][] doubleValues;
      public final double[][] doubleValues2;
      public final double[] doubleValues3;
      public final double[] doubleValues4;

      public Entry2(double[][] doubleValues, double[][] doubleValues2, double[] doubleValues3, double[] doubleValues4) {
         this.doubleValues = doubleValues;
         this.doubleValues2 = doubleValues2;
         this.doubleValues3 = doubleValues3;
         this.doubleValues4 = doubleValues4;
      }
   }

   private static final class Entry3 {
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[][] doubleValues3;
      public double[][] doubleValues4;
      public double[] doubleValues5;
      public double doubleValue;
      public double[] doubleValues6;
   }

   private static final class Entry4 {
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int layers;
      public int intValue4;
      public int intValue5;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public boolean flag;
      public boolean flag2 = true;
      public double doubleValue9 = -7.0;
      public double doubleValue10 = 2.0;
      public boolean flag3;
      public double doubleValue11;
      public double doubleValue12;
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public Motor.Entry6[] entry6s;
      public Motor.Entry2[] entry2s;
      public Motor.Entry6 entry6;
      public Motor.Entry6 entry62;
      public Motor.Entry6 entry63;
      public Motor.Entry6 entry64;
      public Motor.Entry6 entry65;
      public final List<Motor.Entry3> items = new ArrayList<>();

      public boolean method3681() {
         return this.doubleValues != null && this.doubleValues2 != null && this.doubleValues3 != null;
      }

      public double method3682(int n, double d) {
         int n2 = 0;

         while (n2 < this.doubleValues.length && this.doubleValues[n2] < d) {
            n2++;
         }

         double[] doubleValues2 = n == 0 ? this.doubleValues2 : this.doubleValues3;
         return doubleValues2[Math.min(n2, doubleValues2.length - 1)];
      }

      public Motor.Entry5 method3683(double[] doubleValues, double[][] doubleses) {
         double[] doubles = new double[doubleValues.length];

         for (int i = 0; i < doubles.length; i++) {
            doubles[i] = (doubleValues[i] - this.doubleValues4[i]) / Math.sqrt(this.doubleValues5[i] + 1.0E-4);
         }

         for (Motor.Entry6 entry6 : this.entry6s) {
            doubles = entry6.method3698(doubles);

            for (int i = 0; i < doubles.length; i++) {
               doubles[i] /= 1.0 + Math.exp(-doubles[i]);
            }
         }

         for (int i = 0; i < this.entry2s.length; i++) {
            Motor.Entry2 entry2 = this.entry2s[i];
            double[] doubles2 = doubleses[i];
            double[] doubles3 = method3686(entry2.doubleValues, doubles, entry2.doubleValues3);
            double[] doubles4 = method3686(entry2.doubleValues2, doubles2, entry2.doubleValues4);
            double[] doubles5 = new double[this.intValue3];

            for (int j = 0; j < this.intValue3; j++) {
               double d = Motor.method3676(doubles3[j] + doubles4[j]);
               double d2 = Motor.method3676(doubles3[this.intValue3 + j] + doubles4[this.intValue3 + j]);
               double tanh = Math.tanh(doubles3[2 * this.intValue3 + j] + d * doubles4[2 * this.intValue3 + j]);
               doubles5[j] = (1.0 - d2) * tanh + d2 * doubles2[j];
            }

            doubleses[i] = doubles5;
            doubles = doubles5;
         }

         double[] doubles2 = this.entry62.method3698(doubles);
         double[] doubles3 = this.entry63.method3698(doubles);
         double[][] doubleValues2 = new double[this.intValue4][2];
         double[][] doubleValues3 = new double[this.intValue4][2];

         for (int i = 0; i < this.intValue4; i++) {
            doubleValues2[i][0] = doubles2[2 * i];
            doubleValues2[i][1] = doubles2[2 * i + 1];
            doubleValues3[i][0] = Motor.method3677(doubles3[2 * i], this.doubleValue9, this.doubleValue10);
            doubleValues3[i][1] = Motor.method3677(doubles3[2 * i + 1], this.doubleValue9, this.doubleValue10);
         }

         double[] doubles4 = this.entry64.method3698(doubles);
         double[] doubleValues4 = new double[this.intValue4];

         for (int i = 0; i < this.intValue4; i++) {
            doubleValues4[i] = Math.tanh(doubles4[i]) * 0.95;
         }

         return new Motor.Entry5(this.entry6.method3698(doubles), doubleValues2, doubleValues3, doubleValues4, this.entry65.method3698(doubles)[0]);
      }

      public void method3684() {
         if (this.items.isEmpty()) {
            throw new IllegalStateException("в модели нет эталонных тиков");
         } else {
            double[][] doubleses = new double[this.layers][this.intValue3];

            for (int i = 0; i < this.items.size(); i++) {
               Motor.Entry3 entry3 = this.items.get(i);
               if (entry3.doubleValues.length != this.intValue) {
                  throw new IllegalStateException("эталон " + i + ": " + entry3.doubleValues.length + " признаков вместо " + this.intValue);
               }

               Motor.Entry5 entry5 = this.method3683(entry3.doubleValues, doubleses);
               double[] doubleValues = new double[this.layers * this.intValue3];

               for (int j = 0; j < this.layers; j++) {
                  System.arraycopy(doubleses[j], 0, doubleValues, j * this.intValue3, this.intValue3);
               }

               double d = 0.0;
               String str = "";

               for (int j = 0; j < this.intValue4; j++) {
                  double d2 = method3685(entry5.doubleValues2[j], entry3.doubleValues3[j]);
                  if (d2 > d) {
                     d = d2;
                     str = "mu[" + j + "]";
                  }

                  double d3 = method3685(entry5.doubleValues3[j], entry3.doubleValues4[j]);
                  if (d3 > d) {
                     d = d3;
                     str = "log_sigma[" + j + "]";
                  }
               }

               double d2 = method3685(entry5.doubleValues, entry3.doubleValues2);
               if (d2 > d) {
                  d = d2;
                  str = "pi";
               }

               double d3 = method3685(entry5.doubleValues4, entry3.doubleValues5);
               if (d3 > d) {
                  d = d3;
                  str = "rho";
               }

               double d4 = Math.abs(entry5.doubleValue - entry3.doubleValue);
               if (d4 > d) {
                  d = d4;
                  str = "pause";
               }

               double d5 = method3685(doubleValues, entry3.doubleValues6);
               if (d5 > d) {
                  d = d5;
                  str = "hidden";
               }

               if (!(d <= 1.0E-9)) {
                  throw new IllegalStateException(
                     "эталонный тик "
                        + i
                        + " не сошёлся: max|Δ| = "
                        + d
                        + " в '"
                        + str
                        + "', допуск 1.0E-9. Раскладка весов не та, что ожидает рантайм."
                  );
               }
            }
         }
      }

      private static double method3685(double[] doubleValues, double[] doubleValues2) {
         double d = 0.0;

         for (int i = 0; i < doubleValues.length; i++) {
            d = Math.max(d, Math.abs(doubleValues[i] - doubleValues2[i]));
         }

         return d;
      }

      private static double[] method3686(double[][] doubleValues, double[] doubles, double[] doubleValues3) {
         double[] doubles2 = new double[doubleValues.length];

         for (int i = 0; i < doubleValues.length; i++) {
            double d = doubleValues3[i];
            double[] doubles3 = doubleValues[i];

            for (int j = 0; j < doubles.length; j++) {
               d += doubles3[j] * doubles[j];
            }

            doubles2[i] = d;
         }

         return doubles2;
      }

      public static Motor.Entry4 method3687(Path path) throws IOException {
         Motor.Entry4 entry4;
         try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            entry4 = method3688(bufferedReader);
         }

         return entry4;
      }

      public static Motor.Entry4 method3688(Reader inputStreamReader) throws IOException {
         Motor.Entry4 entry4 = new Motor.Entry4();
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();
         JsonReader jsonReader = new JsonReader(inputStreamReader);

         try {
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
               String nextName = jsonReader.nextName();
               switch (nextName) {
                  case "format":
                     String nextString = jsonReader.nextString();
                     if (!"neurotest-motor-v1".equals(nextString)) {
                        throw new IllegalStateException("формат '" + nextString + "', ожидался 'neurotest-motor-v1'");
                     }
                     break;
                  case "config":
                     entry4.method3689(jsonReader);
                     break;
                  case "norm":
                     jsonReader.beginObject();

                     while (jsonReader.hasNext()) {
                        String str = jsonReader.nextName();
                        switch (str) {
                           case "mean":
                              entry4.doubleValues4 = method3696(jsonReader);
                              break;
                           case "var":
                              entry4.doubleValues5 = method3696(jsonReader);
                              break;
                           default:
                              jsonReader.skipValue();
                        }
                     }

                     jsonReader.endObject();
                     break;
                  case "inp":
                     jsonReader.beginArray();

                     while (jsonReader.hasNext()) {
                        arrayList.add(method3694(jsonReader));
                     }

                     jsonReader.endArray();
                     break;
                  case "gru":
                     jsonReader.beginArray();

                     while (jsonReader.hasNext()) {
                        arrayList2.add(method3695(jsonReader));
                     }

                     jsonReader.endArray();
                     break;
                  case "heads":
                     entry4.method3691(jsonReader);
                     break;
                  case "golden":
                     entry4.method3692(jsonReader);
                     break;
                  default:
                     jsonReader.skipValue();
               }
            }

            jsonReader.endObject();
         } catch (Throwable e) {
            try {
               jsonReader.close();
            } catch (Throwable ex) {
               e.addSuppressed(ex);
            }

            throw e;
         }

         jsonReader.close();
         entry4.entry6s = (Motor.Entry6[])arrayList.toArray(new Motor.Entry6[0]);
         entry4.entry2s = (Motor.Entry2[])arrayList2.toArray(new Motor.Entry2[0]);
         entry4.method3693();
         return entry4;
      }

      private void method3689(JsonReader jsonReader) throws IOException {
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            switch (nextName) {
               case "n_features":
                  this.intValue = jsonReader.nextInt();
                  break;
               case "mlp_hidden":
                  this.intValue2 = jsonReader.nextInt();
                  break;
               case "gru_hidden":
                  this.intValue3 = jsonReader.nextInt();
                  break;
               case "gru_layers":
                  this.layers = jsonReader.nextInt();
                  break;
               case "mix":
                  this.intValue4 = jsonReader.nextInt();
                  break;
               case "latency":
                  this.intValue5 = jsonReader.nextInt();
                  break;
               case "asinh_scale":
                  this.doubleValue = jsonReader.nextDouble();
                  break;
               case "act_scale":
                  double[] doubles = method3696(jsonReader);
                  this.doubleValue2 = doubles[0];
                  this.doubleValue3 = doubles.length > 1 ? doubles[1] : doubles[0];
                  break;
               case "inside_sharp":
                  this.doubleValue4 = jsonReader.nextDouble();
                  break;
               case "max_cmd_yaw":
                  this.doubleValue5 = jsonReader.nextDouble();
                  break;
               case "max_cmd_pitch":
                  this.doubleValue6 = jsonReader.nextDouble();
                  break;
               case "peak_decay":
                  this.doubleValue7 = jsonReader.nextDouble();
                  break;
               case "zero_eps":
                  this.doubleValue8 = jsonReader.nextDouble();
                  break;
               case "act_quant":
                  this.flag = "round".equals(jsonReader.nextString());
                  break;
               case "use_player":
                  this.flag3 = jsonReader.nextBoolean();
                  break;
               case "log_sigma_min":
                  this.doubleValue9 = jsonReader.nextDouble();
                  break;
               case "log_sigma_max":
                  this.doubleValue10 = jsonReader.nextDouble();
                  break;
               case "act_residual":
                  this.flag2 = jsonReader.nextBoolean();
                  break;
               case "envelope":
                  this.method3690(jsonReader);
                  break;
               default:
                  jsonReader.skipValue();
            }
         }

         jsonReader.endObject();
      }

      private void method3690(JsonReader jsonReader) throws IOException {
         if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
         } else {
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
               String nextName = jsonReader.nextName();
               switch (nextName) {
                  case "edges_counts":
                     this.doubleValues = method3696(jsonReader);
                     break;
                  case "caps_yaw_counts":
                     this.doubleValues2 = method3696(jsonReader);
                     break;
                  case "caps_pitch_counts":
                     this.doubleValues3 = method3696(jsonReader);
                     break;
                  default:
                     jsonReader.skipValue();
               }
            }

            jsonReader.endObject();
         }
      }

      private void method3691(JsonReader jsonReader) throws IOException {
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            Motor.Entry6 entry6 = method3694(jsonReader);
            switch (nextName) {
               case "pi":
                  this.entry6 = entry6;
                  break;
               case "mu":
                  this.entry62 = entry6;
                  break;
               case "log_sigma":
                  this.entry63 = entry6;
                  break;
               case "rho":
                  this.entry64 = entry6;
                  break;
               case "pause":
                  this.entry65 = entry6;
            }
         }

         jsonReader.endObject();
      }

      private void method3692(JsonReader jsonReader) throws IOException {
         jsonReader.beginArray();

         while (jsonReader.hasNext()) {
            Motor.Entry3 entry3 = new Motor.Entry3();
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
               String nextName = jsonReader.nextName();
               switch (nextName) {
                  case "features":
                     entry3.doubleValues = method3696(jsonReader);
                     break;
                  case "pi":
                     entry3.doubleValues2 = method3696(jsonReader);
                     break;
                  case "mu":
                     entry3.doubleValues3 = method3697(jsonReader);
                     break;
                  case "log_sigma":
                     entry3.doubleValues4 = method3697(jsonReader);
                     break;
                  case "rho":
                     entry3.doubleValues5 = method3696(jsonReader);
                     break;
                  case "pause":
                     entry3.doubleValue = jsonReader.nextDouble();
                     break;
                  case "hidden_out":
                     entry3.doubleValues6 = method3696(jsonReader);
                     break;
                  default:
                     jsonReader.skipValue();
               }
            }

            jsonReader.endObject();
            this.items.add(entry3);
         }

         jsonReader.endArray();
      }

      private void method3693() {
         if (this.intValue != 47) {
            throw new IllegalStateException("модель ждёт " + this.intValue + " признаков, рантайм собирает 47");
         } else if (this.flag3) {
            throw new IllegalStateException(
               "модель обучена с one-hot игрока (--player-cond on); клиенту неоткуда взять его id. Переобучите с --player-cond none"
            );
         } else if (this.entry6s.length == 0 || this.entry2s.length != this.layers) {
            throw new IllegalStateException("слоёв GRU " + this.entry2s.length + ", в конфиге " + this.layers);
         } else if (this.doubleValues4 == null
            || this.doubleValues4.length != this.intValue
            || this.doubleValues5 == null
            || this.doubleValues5.length != this.intValue) {
            throw new IllegalStateException("нормировка не на " + this.intValue + " признаков");
         } else if (this.entry6 == null || this.entry62 == null || this.entry63 == null || this.entry64 == null || this.entry65 == null) {
            throw new IllegalStateException("в файле не все головы");
         } else if (this.entry62.doubleValues.length == this.intValue4 * 2 && this.entry6.doubleValues.length == this.intValue4) {
            for (Motor.Entry2 entry2 : this.entry2s) {
               if (entry2.doubleValues.length != 3 * this.intValue3 || entry2.doubleValues2.length != 3 * this.intValue3) {
                  throw new IllegalStateException("матрицы GRU не на " + this.intValue3);
               }
            }

            if (this.intValue - 11 != 36) {
               throw new IllegalStateException("ожидалось 36 признаков задачи и руки, получилось " + (this.intValue - 11));
            } else {
               if (this.doubleValue2 <= 0.0) {
                  this.doubleValue2 = this.doubleValue;
               }

               if (this.doubleValue3 <= 0.0) {
                  this.doubleValue3 = this.doubleValue;
               }

               this.doubleValue11 = Motor.asinh(this.doubleValue5 / this.doubleValue2);
               this.doubleValue12 = Motor.asinh(this.doubleValue6 / this.doubleValue3);
            }
         } else {
            throw new IllegalStateException("головы не на " + this.intValue4 + " компонент");
         }
      }

      private static Motor.Entry6 method3694(JsonReader jsonReader) throws IOException {
         double[][] doubleValues = null;
         double[] doubleValues2 = null;
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            switch (nextName) {
               case "w":
                  doubleValues = method3697(jsonReader);
                  break;
               case "b":
                  doubleValues2 = method3696(jsonReader);
                  break;
               default:
                  jsonReader.skipValue();
            }
         }

         jsonReader.endObject();
         if (doubleValues != null && doubleValues2 != null && doubleValues.length == doubleValues2.length) {
            return new Motor.Entry6(doubleValues, doubleValues2);
         } else {
            throw new IllegalStateException("битый линейный слой");
         }
      }

      private static Motor.Entry2 method3695(JsonReader jsonReader) throws IOException {
         double[][] doubleValues = null;
         double[][] doubleValues2 = null;
         double[] doubleValues3 = null;
         double[] doubleValues4 = null;
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            switch (nextName) {
               case "w_ih":
                  doubleValues = method3697(jsonReader);
                  break;
               case "w_hh":
                  doubleValues2 = method3697(jsonReader);
                  break;
               case "b_ih":
                  doubleValues3 = method3696(jsonReader);
                  break;
               case "b_hh":
                  doubleValues4 = method3696(jsonReader);
                  break;
               default:
                  jsonReader.skipValue();
            }
         }

         jsonReader.endObject();
         if (doubleValues != null && doubleValues2 != null && doubleValues3 != null && doubleValues4 != null) {
            return new Motor.Entry2(doubleValues, doubleValues2, doubleValues3, doubleValues4);
         } else {
            throw new IllegalStateException("битый слой GRU");
         }
      }

      private static double[] method3696(JsonReader jsonReader) throws IOException {
         double[] original = new double[32];
         int newLength = 0;
         jsonReader.beginArray();

         for (; jsonReader.hasNext(); original[newLength++] = jsonReader.nextDouble()) {
            if (newLength == original.length) {
               original = Arrays.copyOf(original, newLength * 2);
            }
         }

         jsonReader.endArray();
         return Arrays.copyOf(original, newLength);
      }

      private static double[][] method3697(JsonReader jsonReader) throws IOException {
         ArrayList arrayList = new ArrayList();
         jsonReader.beginArray();

         while (jsonReader.hasNext()) {
            arrayList.add(method3696(jsonReader));
         }

         jsonReader.endArray();
         return (double[][])arrayList.toArray(new double[0][]);
      }
   }

   private static final class Entry5 {
      public final double[] doubleValues;
      public final double[][] doubleValues2;
      public final double[][] doubleValues3;
      public final double[] doubleValues4;
      public final double doubleValue;

      public Entry5(double[] doubleValues, double[][] doubleValues2, double[][] doubleValues3, double[] doubleValues4, double doubleValue) {
         this.doubleValues = doubleValues;
         this.doubleValues2 = doubleValues2;
         this.doubleValues3 = doubleValues3;
         this.doubleValues4 = doubleValues4;
         this.doubleValue = doubleValue;
      }
   }

   private static final class Entry6 {
      public final double[][] doubleValues;
      public final double[] doubleValues2;

      public Entry6(double[][] doubleValues, double[] doubleValues2) {
         this.doubleValues = doubleValues;
         this.doubleValues2 = doubleValues2;
      }

      public double[] method3698(double[] doubles) {
         double[] doubles2 = new double[this.doubleValues.length];

         for (int i = 0; i < this.doubleValues.length; i++) {
            double d = this.doubleValues2[i];
            double[] doubleValue = this.doubleValues[i];

            for (int j = 0; j < doubles.length; j++) {
               d += doubleValue[j] * doubles[j];
            }

            doubles2[i] = d;
         }

         return doubles2;
      }
   }
}

package org.zenith.base.rotation.mods;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Util22;

public final class HandRotationLogger extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final boolean CONST_flag = false;
   private static final boolean CONST_flag2 = true;
   private BufferedWriter bufferedWriter;
   private int intValue;
   private final HandRotationLogger.Entry2 entry2 = new HandRotationLogger.Entry2();
   private final HandRotationLogger.Entry entry = new HandRotationLogger.Entry(this.entry2);
   private final Random random = new Random();
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4 = Integer.MIN_VALUE;
   private Rotation rotation;
   private Vec3 vec3d;
   private Vec3 vec3d2 = Vec3.ZERO;
   private Vec3[] vec3ds;
   private Vec3 vec3d3;
   private AABB box;
   private static final String text = "\n";

   public Rotation method3727() {
      Rotation rotation = rotationManager.method7844();
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      if (mc.player != null && livingEntity != null && livingEntity.isAlive()) {
         int tickCount = mc.player.tickCount;
         if (tickCount == this.intValue4 && this.rotation != null) {
            return this.rotation;
         } else {
            if (this.intValue3 != Integer.MIN_VALUE && tickCount != this.intValue3 + 1) {
               this.method3730(livingEntity.getId());
            } else if (livingEntity.getId() != this.intValue2) {
               this.method3730(livingEntity.getId());
            }

            this.intValue3 = tickCount;
            this.box = livingEntity.getBoundingBox();
            Vec3 center = this.box.getCenter();
            this.vec3d2 = this.vec3d == null ? Vec3.ZERO : center.subtract(this.vec3d);
            this.vec3d = center;
            this.vec3d3 = mc.player.getEyePosition();
            this.vec3ds = null;
            double[] doubles = new double[]{rotation.getFloatValue(), rotation.getFloatValue2()};
            int size = this.entry.items.size();
            double attackStrengthScale = mc.player.getAttackStrengthScale(0.0F);
            int[] ints = this.entry.method3745(doubles, this::predict, this.random, attackStrengthScale);
            double doubleValue = this.entry2.doubleValue;
            Rotation rotation2 = rotation.method3140((float)(ints[0] * doubleValue), (float)(ints[1] * doubleValue));
            this.method3729(tickCount, doubles, ints, size);
            this.method3731(tickCount, rotation2);
            return rotation2;
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

   private void predict(int scale, double[] doubles) {
      scale = Math.max(0, Math.min(scale, this.entry2.intValue2));
      Vec3 vec3d3;
      if (scale == 0) {
         vec3d3 = this.vec3d3;
      } else {
         if (this.vec3ds == null) {
            this.vec3ds = this.method3728(this.entry2.intValue2);
         }

         vec3d3 = this.vec3ds[scale - 1];
      }

      Vec3 vec = this.vec3d2.scale(scale);
      Vec3 vec2 = this.box.getCenter().add(vec);
      double x = vec2.x - vec3d3.x;
      double d = vec2.y - vec3d3.y;
      double x2 = vec2.z - vec3d3.z;
      double d2 = Math.toDegrees(Math.atan2(-x, x2));
      double d3 = Math.toDegrees(Math.atan2(-d, Math.hypot(x, x2)));
      double d4 = Double.MAX_VALUE;
      double d5 = -Double.MAX_VALUE;
      double d6 = Double.MAX_VALUE;
      double d7 = -Double.MAX_VALUE;

      for (int i = 0; i < 8; i++) {
         double x3 = ((i & 1) == 0 ? this.box.minX : this.box.maxX) + vec.x - vec3d3.x;
         double d8 = ((i & 2) == 0 ? this.box.minY : this.box.maxY) + vec.y - vec3d3.y;
         double x4 = ((i & 4) == 0 ? this.box.minZ : this.box.maxZ) + vec.z - vec3d3.z;
         double d9 = Math.toDegrees(Math.atan2(-x3, x4));
         double d10 = Math.toDegrees(Math.atan2(-d8, Math.hypot(x3, x4)));
         if (Double.isFinite(d9) && Double.isFinite(d10)) {
            double d11 = HandRotationLogger.Entry.method3735(d9 - d2);
            double d12 = d10 - d3;
            d4 = Math.min(d4, d11);
            d5 = Math.max(d5, d11);
            d6 = Math.min(d6, d12);
            d7 = Math.max(d7, d12);
         }
      }

      doubles[0] = d2;
      doubles[1] = d3;
      doubles[2] = d4 == Double.MAX_VALUE ? 0.001 : 0.5 * (d5 - d4);
      doubles[3] = d6 == Double.MAX_VALUE ? 0.001 : 0.5 * (d7 - d6);
   }

   private Vec3[] method3728(int n) {
      Vec3[] vecs = new Vec3[n];
      Vec3 vec = this.vec3d3.subtract(mc.player.position());

      try {
         PlayerSimulation playerSimulation = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(mc.player.input.keyPresses));
         Vec3 center = this.box.getCenter();

         for (int i = 0; i < n; i++) {
            playerSimulation.tick();
            Vec3 vec2 = playerSimulation.vec3d.add(vec);
            boolean flag = Double.isFinite(vec2.x) && Double.isFinite(vec2.y) && Double.isFinite(vec2.z);
            if (!flag) {
               vecs[i] = i == 0 ? this.vec3d3 : vecs[i - 1];
            } else {
               Vec3 vec3 = center.add(this.vec3d2.scale(i + 1));
               double x = vec2.x - vec3.x;
               double y = vec2.z - vec3.z;
               double hypot = Math.hypot(x, y);
               if (hypot < this.getEntry2().doubleValue6 && hypot > 1.0E-9) {
                  double d = this.getEntry2().doubleValue6 / hypot;
                  vec2 = new Vec3(vec3.x + x * d, vec2.y, vec3.z + y * d);
                  playerSimulation.vec3d = new Vec3(vec2.x - vec.x, playerSimulation.vec3d.y, vec2.z - vec.z);
               }

               vecs[i] = vec2;
            }
         }
      } catch (Throwable e) {
         for (int i = 0; i < n; i++) {
            vecs[i] = this.vec3d3;
         }
      }

      return vecs;
   }

   private void method3729(int tickCount, double[] doubles, int[] ints, int size) {
      try {
         if (this.bufferedWriter == null) {
            Path path = Path.of("hand_v6_log.csv");
            boolean flag = !Files.exists(path);
            this.bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (flag) {
               this.bufferedWriter
                  .write(
                     "age,cam_yaw,cam_pitch,tgt_yaw1,tgt_pitch1,half_yaw,half_pitch,tgt_yaw4,eye_x,eye_y,eye_z,cen_x,cen_y,cen_z,cy,cp,active_before,active_after,forward,back,left,right,jump,eye4_x,eye4_z,hdist\n"
                  );
            }
         }

         double[] doubles2 = new double[4];
         this.predict(1, doubles2);
         double[] doubles3 = new double[4];
         this.predict(4, doubles3);
         Vec3 center = this.box.getCenter();
         Input keyPresses = mc.player.input.keyPresses;
         if (this.vec3ds == null) {
            this.vec3ds = this.method3728(this.entry2.intValue2);
         }

         Vec3 vec3d = this.vec3ds[3];
         this.bufferedWriter
            .write(
               tickCount
                  + ","
                  + doubles[0]
                  + ","
                  + doubles[1]
                  + ","
                  + doubles2[0]
                  + ","
                  + doubles2[1]
                  + ","
                  + doubles2[2]
                  + ","
                  + doubles2[3]
                  + ","
                  + doubles3[0]
                  + ","
                  + this.vec3d3.x
                  + ","
                  + this.vec3d3.y
                  + ","
                  + this.vec3d3.z
                  + ","
                  + center.x
                  + ","
                  + center.y
                  + ","
                  + center.z
                  + ","
                  + ints[0]
                  + ","
                  + ints[1]
                  + ","
                  + size
                  + ","
                  + this.entry.items.size()
                  + ","
                  + (keyPresses.forward() ? 1 : 0)
                  + ","
                  + (keyPresses.backward() ? 1 : 0)
                  + ","
                  + (keyPresses.left() ? 1 : 0)
                  + ","
                  + (keyPresses.right() ? 1 : 0)
                  + ","
                  + (keyPresses.jump() ? 1 : 0)
                  + ","
                  + vec3d.x
                  + ","
                  + vec3d.z
                  + ","
                  + Math.hypot(center.x - this.vec3d3.x, center.z - this.vec3d3.z)
                  + "\n"
            );
         if (++this.intValue % 40 == 0) {
            this.bufferedWriter.flush();
         }
      } catch (Exception e) {
         if (this.bufferedWriter != null) {
            try {
               this.bufferedWriter.close();
            } catch (Exception ex) {
            }
         }

         this.bufferedWriter = null;
      }
   }

   private HandRotationLogger.Entry2 getEntry2() {
      return this.entry2;
   }

   private void method3730(int id) {
      this.entry.reset();
      this.intValue2 = id;
      this.intValue3 = Integer.MIN_VALUE;
      this.intValue4 = Integer.MIN_VALUE;
      this.rotation = null;
      this.vec3d = null;
      this.vec3d2 = Vec3.ZERO;
      this.vec3ds = null;
   }

   private void method3731(int tickCount, Rotation rotation) {
      this.intValue4 = tickCount;
      this.rotation = rotation;
   }

   public void reset() {
      this.method3730(Integer.MIN_VALUE);
   }

   public boolean method3732() {
      return true;
   }

   public static String method3733() {
      return "без прогиба:\n" + method3734(0.0) + "\nпрогиб c = 0.10:\n" + method3734(0.1);
   }

   private static String method3734(double d) {
      HandRotationLogger.Entry2 entry2 = new HandRotationLogger.Entry2();
      entry2.flag2 = true;
      entry2.doubleValue9 = d;
      HandRotationLogger.Entry entry = new HandRotationLogger.Entry(entry2);
      Random random = new Random(0L);
      double[] doubles = new double[]{0.0, 0.0};
      StringBuilder builder = new StringBuilder();
      StringBuilder builder2 = new StringBuilder();
      double[] doubles2 = new double[]{30.0, -5.0};

      for (int i = 0; i < 40; i++) {
         if (i == 20) {
            doubles2[0] = -20.0;
            doubles2[1] = 10.0;
         }

         HandRotationLogger.Handler handler = (n, var2x) -> {
            var2x[0] = doubles2[0];
            var2x[1] = doubles2[1];
            var2x[2] = 6.0;
            var2x[3] = 17.0;
         };
         int[] ints = entry.method3745(doubles, handler, random, 1.0);
         doubles[0] += ints[0] * entry2.doubleValue;
         doubles[1] = Math.max(-90.0, Math.min(90.0, doubles[1] + ints[1] * entry2.doubleValue));
         builder.append(i > 0 ? " " : "").append(ints[0]);
         builder2.append(i > 0 ? " " : "").append(ints[1]);
      }

      return "yaw:   " + builder + "\npitch: " + builder2;
   }

   public static final class Entry {
      public final HandRotationLogger.Entry2 entry2;
      public final List<double[][]> items = new ArrayList<>();
      public int intValue;
      public int intValue2;
      public double doubleValue;
      public double doubleValue2;

      public Entry(HandRotationLogger.Entry2 entry2) {
         this.entry2 = entry2;
      }

      public void reset() {
         this.items.clear();
         this.intValue = 0;
         this.intValue2 = 0;
         this.doubleValue = 0.0;
         this.doubleValue2 = 0.0;
      }

      public static double method3735(double d) {
         double d2 = (d + 180.0) % 360.0;
         if (d2 < 0.0) {
            d2 += 360.0;
         }

         return d2 - 180.0;
      }

      public static double[] method3736(int n) {
         double[] doubles = new double[n];
         double d = 0.0;

         for (int i = 1; i <= n; i++) {
            double d2 = (double)i / n;
            double d3 = 10.0 * Math.pow(d2, 3.0) - 15.0 * Math.pow(d2, 4.0) + 6.0 * Math.pow(d2, 5.0);
            doubles[i - 1] = d3 - d;
            d = d3;
         }

         return doubles;
      }

      public double method3737(double attackStrengthScale) {
         return Math.max(0.0, Math.min(1.0, (attackStrengthScale - this.entry2.doubleValue24) / Math.max(this.entry2.doubleValue25 - this.entry2.doubleValue24, 1.0E-6)));
      }

      public int method3738(double hypot, Random random, double attackStrengthScale) {
         double d = this.method3737(attackStrengthScale);
         double d2 = this.entry2.doubleValue2
            * Math.pow(Math.max(hypot, 0.5), this.entry2.doubleValue3)
            * (this.entry2.doubleValue22 - (this.entry2.doubleValue22 - this.entry2.doubleValue23) * d);
         double exp = this.entry2.flag2 ? 1.0 : Math.exp(random.nextGaussian() * this.entry2.doubleValue4);
         long l = Math.max(Math.round(Math.rint(d2 * exp)), (long)Math.ceil(hypot / this.entry2.doubleValue5));
         return (int)Math.max((long)this.entry2.intValue, Math.min((long)this.entry2.intValue2, l));
      }

      public double method3739(double hypot, Random random) {
         double d;
         if (hypot >= this.entry2.doubleValue13) {
            d = this.entry2.doubleValue10;
         } else if (hypot <= this.entry2.doubleValue12) {
            d = this.entry2.doubleValue11;
         } else {
            double d2 = (hypot - this.entry2.doubleValue12) / (this.entry2.doubleValue13 - this.entry2.doubleValue12);
            d = this.entry2.doubleValue11 + d2 * (this.entry2.doubleValue10 - this.entry2.doubleValue11);
         }

         return d * (this.entry2.flag2 ? 1.0 : Math.exp(random.nextGaussian() * this.entry2.doubleValue14));
      }

      public double[] method3740() {
         int n = 0;
         double d = 0.0;
         double d2 = 0.0;

         for (double[][] doubleses : this.items) {
            n = Math.max(n, doubleses[0].length);

            for (double d3 : doubleses[0]) {
               d += d3;
            }

            for (double d3 : doubleses[1]) {
               d2 += d3;
            }
         }

         return new double[]{n, d, d2};
      }

      public double[] method3741(double[] doubles, HandRotationLogger.Handler handler, int n) {
         double[] doubles2 = this.method3740();
         double[] doubles3 = new double[4];
         handler.predict(n, doubles3);
         double d = method3735(doubles3[0] - (doubles[0] + doubles2[1]));
         if (Math.abs(d) > this.entry2.doubleValue18) {
            double[] doubles4 = new double[4];
            handler.predict(Math.max(n - 1, 0), doubles4);
            double d2 = method3735(doubles3[0] - doubles4[0]);
            if (Math.abs(d2) > this.entry2.doubleValue19 && Math.signum(d2) != Math.signum(d)) {
               d -= 360.0 * Math.signum(d);
            }
         }

         return new double[]{d, doubles3[1] - (doubles[1] + doubles2[2]), doubles3[2], doubles3[3]};
      }

      public double method3742(double attackStrengthScale) {
         return this.entry2.doubleValue21 + (this.entry2.doubleValue17 - this.entry2.doubleValue21) * this.method3737(attackStrengthScale);
      }

      public double[][] method3743(double x, double y, int n, Random random) {
         double[] doubles = method3736(n);
         double[] doubles2 = new double[n];
         double[] doubles3 = new double[n];
         double hypot = Math.hypot(x, y);
         double d = this.entry2.flag2
            ? this.entry2.doubleValue9
            : Math.max(-this.entry2.doubleValue8, Math.min(this.entry2.doubleValue8, random.nextGaussian() * this.entry2.doubleValue7));
         if (!(hypot < 1.0E-9) && d != 0.0) {
            double d2 = -y / hypot;
            double d3 = x / hypot;
            double d4 = 0.0;
            double d5 = 0.0;

            for (int i = 0; i < n; i++) {
               d4 += doubles[i];
               double d6 = 4.0 * d4 * (1.0 - d4);
               double d7 = hypot * (d6 - d5);
               d5 = d6;
               doubles2[i] = x * doubles[i] + d * d7 * d2;
               doubles3[i] = y * doubles[i] + d * d7 * d3;
            }

            return new double[][]{doubles2, doubles3};
         } else {
            for (int i = 0; i < n; i++) {
               doubles2[i] = x * doubles[i];
               doubles3[i] = y * doubles[i];
            }

            return new double[][]{doubles2, doubles3};
         }
      }

      public void method3744(double[] doubles, HandRotationLogger.Handler handler, Random random, double attackStrengthScale) {
         int n = (int)this.method3740()[0];
         double[] doubles2 = this.method3741(doubles, handler, Math.max(n, this.entry2.intValue3));
         int n2 = this.method3738(Math.hypot(doubles2[0], doubles2[1]), random, attackStrengthScale);
         doubles2 = this.method3741(doubles, handler, Math.max(n, n2));
         double hypot = Math.hypot(doubles2[0], doubles2[1]);
         if (!(hypot < 1.0E-6)) {
            n2 = this.method3738(hypot, random, attackStrengthScale);
            double d = this.method3739(hypot, random);
            double x = d * doubles2[0];
            double y = d * doubles2[1];
            double d2 = this.method3740()[1];
            if (Math.abs(d2) > 1.0E-9 && Math.signum(x) == -Math.signum(d2) && Math.abs(x) > 1.0E-9) {
               double d3 = Math.max(Math.abs(doubles2[0]) / Math.max(doubles2[2], 0.001), Math.abs(doubles2[1]) / Math.max(doubles2[3], 0.001));
               if (d3 < this.entry2.doubleValue20 * this.method3742(attackStrengthScale)) {
                  return;
               }
            }

            if (!this.entry2.flag2) {
               double d3 = doubles2[0] / hypot;
               double d4 = doubles2[1] / hypot;
               double d5 = random.nextGaussian() * this.entry2.doubleValue15 * hypot;
               double d6 = random.nextGaussian() * this.entry2.doubleValue16 * hypot;
               x += d3 * d5 + -d4 * d6;
               y += d4 * d5 + d3 * d6;
            }

            this.items.add(this.method3743(x, y, n2, random));
         }
      }

      public int[] method3745(double[] doubles, HandRotationLogger.Handler handler, Random random, double attackStrengthScale) {
         attackStrengthScale = Math.max(0.0, Math.min(1.0, attackStrengthScale));
         if (this.intValue2 >= this.intValue || this.entry2.flag && this.items.isEmpty()) {
            int n = (int)this.method3740()[0];
            double[] doubles2 = this.method3741(doubles, handler, Math.max(n, this.entry2.intValue3));
            double d = Math.max(Math.abs(doubles2[0]) / Math.max(doubles2[2], 0.001), Math.abs(doubles2[1]) / Math.max(doubles2[3], 0.001));
            if (d > this.method3742(attackStrengthScale)) {
               this.method3744(doubles, handler, random, attackStrengthScale);
            }

            int n2 = this.entry2.flag2
               ? (this.entry2.intValue3 + this.entry2.intValue4) / 2
               : this.entry2.intValue3 + random.nextInt(this.entry2.intValue4 - this.entry2.intValue3 + 1);
            this.intValue = this.intValue2 + n2;
         }

         this.intValue2++;
         if (this.items.isEmpty()) {
            return new int[]{0, 0};
         } else {
            double d = 0.0;
            double d2 = 0.0;
            ArrayList arrayList = new ArrayList();

            for (double[][] doubleses : this.items) {
               d += doubleses[0][0];
               d2 += doubleses[1][0];
               if (doubleses[0].length > 1) {
                  double[] doubles2 = new double[doubleses[0].length - 1];
                  double[] doubles3 = new double[doubleses[1].length - 1];
                  System.arraycopy(doubleses[0], 1, doubles2, 0, doubles2.length);
                  System.arraycopy(doubleses[1], 1, doubles3, 0, doubles3.length);
                  arrayList.add(new double[][]{doubles2, doubles3});
               }
            }

            this.items.clear();
            this.items.addAll(arrayList);
            double d3 = d / this.entry2.doubleValue + this.doubleValue;
            double d4 = d2 / this.entry2.doubleValue + this.doubleValue2;
            int n = (int)(d3 < 0.0 ? Math.ceil(d3) : Math.floor(d3));
            int n2 = (int)(d4 < 0.0 ? Math.ceil(d4) : Math.floor(d4));
            this.doubleValue = d3 - n;
            this.doubleValue2 = d4 - n2;
            return new int[]{n, n2};
         }
      }
   }

   public static final class Entry2 {
      public double doubleValue = 0.15190947;
      public double doubleValue2 = 1.66;
      public double doubleValue3 = 0.32;
      public double doubleValue4 = 0.25;
      public int intValue = 2;
      public int intValue2 = 14;
      public double doubleValue5 = 35.0;
      public double doubleValue6 = 0.6;
      public double doubleValue7 = 0.065;
      public double doubleValue8 = 0.3;
      public double doubleValue9 = 0.0;
      public double doubleValue10 = 0.95;
      public double doubleValue11 = 0.7;
      public double doubleValue12 = 30.0;
      public double doubleValue13 = 60.0;
      public double doubleValue14 = 0.12;
      public double doubleValue15 = 0.05;
      public double doubleValue16 = 0.05;
      public int intValue3 = 2;
      public int intValue4 = 4;
      public double doubleValue17 = 0.05;
      public boolean flag = true;
      public double doubleValue18 = 90.0;
      public double doubleValue19 = 3.0;
      public double doubleValue20 = 2.0;
      public double doubleValue21 = 0.9;
      public double doubleValue22 = 1.2;
      public double doubleValue23 = 0.75;
      public double doubleValue24 = 0.25;
      public double doubleValue25 = 0.55;
      public boolean flag2 = false;

      public HandRotationLogger.Entry2 method3746() {
         HandRotationLogger.Entry2 entry2 = new HandRotationLogger.Entry2();
         entry2.doubleValue = this.doubleValue;
         entry2.doubleValue2 = this.doubleValue2;
         entry2.doubleValue3 = this.doubleValue3;
         entry2.doubleValue4 = this.doubleValue4;
         entry2.intValue = this.intValue;
         entry2.intValue2 = this.intValue2;
         entry2.doubleValue10 = this.doubleValue10;
         entry2.doubleValue11 = this.doubleValue11;
         entry2.doubleValue12 = this.doubleValue12;
         entry2.doubleValue13 = this.doubleValue13;
         entry2.doubleValue14 = this.doubleValue14;
         entry2.doubleValue15 = this.doubleValue15;
         entry2.doubleValue16 = this.doubleValue16;
         entry2.intValue3 = this.intValue3;
         entry2.intValue4 = this.intValue4;
         entry2.doubleValue17 = this.doubleValue17;
         entry2.flag = this.flag;
         entry2.flag2 = this.flag2;
         entry2.doubleValue18 = this.doubleValue18;
         entry2.doubleValue19 = this.doubleValue19;
         entry2.doubleValue20 = this.doubleValue20;
         entry2.doubleValue5 = this.doubleValue5;
         entry2.doubleValue6 = this.doubleValue6;
         entry2.doubleValue21 = this.doubleValue21;
         entry2.doubleValue22 = this.doubleValue22;
         entry2.doubleValue23 = this.doubleValue23;
         entry2.doubleValue24 = this.doubleValue24;
         entry2.doubleValue25 = this.doubleValue25;
         entry2.doubleValue7 = this.doubleValue7;
         entry2.doubleValue8 = this.doubleValue8;
         entry2.doubleValue9 = this.doubleValue9;
         return entry2;
      }
   }

   public interface Handler {
      void predict(int n, double[] doubles);
   }
}

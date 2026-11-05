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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.IntStream;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public final class TokenMotor extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final String text = "token_motor.json";
   private static final String text2 = "models/token_motor.json";
   private static final List<Path> items = List.of(
      Path.of("token_motor.json"), Path.of("models", "token_motor.json"), Path.of("..", "models", "token_motor.json")
   );
   private static final String text3 = "neurotest-token-motor-v2";
   private static final double CONST_doubleValue = 1.0E-9;
   private static final boolean CONST_flag = false;
   private static final int CONST_intValue2 = 8;
   private TokenMotor.Entry4 entry4;
   private boolean flag;
   private CompletableFuture<TokenMotor.Entry4> completableFuture;
   private long longValue;
   private final Random random = new Random();
   private final TokenMotor.Entry[] entrys = new TokenMotor.Entry[8];
   private int intValue;
   private double[][] doubleValues;
   private int[][] intValues;
   private int intValue2;
   private final double[] doubleValues2 = new double[6];
   private double doubleValue;
   private int[][] intValues2;
   private int intValue3;
   private boolean flag2;
   private float floatValue = 1.0F;
   private boolean flag3;
   private int intValue4 = Integer.MIN_VALUE;
   private int intValue5 = Integer.MIN_VALUE;
   private int intValue6 = Integer.MIN_VALUE;
   private Rotation rotation;

   public Rotation method3467() {
      Rotation rotation = rotationManager.method7844();
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      if (mc.player != null && livingEntity != null && livingEntity.isAlive()) {
         TokenMotor.Entry4 entry4 = this.method3481();
         if (entry4 == null) {
            return rotation;
         } else {
            int tickCount = mc.player.tickCount;
            if (tickCount == this.intValue6 && this.rotation != null) {
               return this.rotation;
            } else {
               if (this.intValue5 != Integer.MIN_VALUE && tickCount != this.intValue5 + 1) {
                  this.method3478(livingEntity.getId());
               } else if (livingEntity.getId() != this.intValue4) {
                  this.method3479(livingEntity.getId());
               }

               this.intValue5 = tickCount;
               this.method3477();
               TokenMotor.Entry entry = this.method3472(rotation, livingEntity);
               if (entry == null) {
                  this.method3480(tickCount, rotation);
                  return rotation;
               } else {
                  this.method3473(entry);
                  if (this.intValue < entry4.intValue10 + 3) {
                     this.method3480(tickCount, rotation);
                     return rotation;
                  } else {
                     double[] doubleValues = new double[]{rotation.getFloatValue(), rotation.getFloatValue2()};
                     double[] doubles = new double[6];
                     double[] doubles2 = this.method3476(entry4, doubleValues, doubles);
                     if (!method3485(doubles2)) {
                        this.method3480(tickCount, rotation);
                        return rotation;
                     } else {
                        int[] ints;
                        if (this.flag2) {
                           ints = this.intValues2[this.intValue3++];
                           this.flag2 = this.intValue3 < this.intValues2.length;
                        } else {
                           ints = this.method3470(entry4, doubles2, doubleValues);
                        }

                        double d = entry4.doubleValue4;
                        Rotation rotation2 = rotation.method3140((float)(ints[0] * d), (float)(ints[1] * d));
                        this.method3475(doubles2, new int[]{entry4.method3488(ints[0]), entry4.method3488(ints[1])});
                        System.arraycopy(doubles, 0, this.doubleValues2, 0, 6);
                        this.doubleValue = ints[0] == 0 && ints[1] == 0 ? 0.0 : this.doubleValue + 1.0;
                        this.method3480(tickCount, rotation2);
                        return rotation2;
                     }
                  }
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

   public boolean method3468() {
      return this.method3481() != null;
   }

   public static void method3469(Path path) throws IOException {
      long now = System.currentTimeMillis();
      TokenMotor.Entry4 entry4 = TokenMotor.Entry4.method3507(path);
      entry4.method3504();
      System.out
         .println(
            "v5 token motor: эталон сошёлся, окно "
               + entry4.intValue
               + ", префикс "
               + entry4.intValue2
               + ", словарь "
               + entry4.intValue3
               + ", сенса-константа "
               + entry4.doubleValue4
               + ", "
               + (System.currentTimeMillis() - now)
               + " мс"
         );
      int intValue = entry4.intValue;
      int n = entry4.intValue2;
      double[][] doubleValues10 = new double[intValue][];
      int[][] intValues = new int[intValue][2];

      for (int i = 0; i < intValue; i++) {
         doubleValues10[i] = entry4.entry3.doubleValues10[i];
         intValues[i] = i < n ? (int[])entry4.entry3.intValues[i].clone() : new int[]{entry4.intValue4, entry4.intValue4};
      }

      Random random = new Random(0L);
      double d = -1.0;
      double d2 = -1.0;

      for (int i = 0; i < 4; i++) {
         long nanoTime = System.nanoTime();

         for (int j = 0; j < entry4.intValue12; j++) {
            if (entry4.flag) {
               entry4.method3500(method3471(intValues), doubleValues10, entry4.intValue11, true, random);
            } else {
               entry4.method3499(method3471(intValues), doubleValues10, entry4.intValue13, true, true, random);
            }
         }

         double d3 = (System.nanoTime() - nanoTime) / 1000000.0;
         if (i == 0) {
            d = d3;
         }

         d2 = d3;
      }

      int n2 = entry4.flag ? 2 * entry4.intValue11 : entry4.intValue13;
      System.out
         .println(
            "v5 одно решение ("
               + entry4.intValue12
               + " x "
               + n2
               + " проходов, режим "
               + (entry4.flag ? "first" : "fill")
               + "): холодное "
               + String.format(Locale.ROOT, "%.0f", d)
               + " мс, прогретое "
               + String.format(Locale.ROOT, "%.0f", d2)
               + " мс при бюджете "
               + entry4.intValue11 * 50
               + " мс"
         );
   }

   private int[] method3470(TokenMotor.Entry4 entry4, double[] doubles, double[] doubleValues) {
      int intValue = entry4.intValue;
      int n = entry4.intValue2;
      double[][] doubleValues10 = new double[intValue][];
      int[][] intValues = new int[intValue][2];

      for (int i = 0; i < n; i++) {
         if (i < this.intValue2) {
            int n2 = this.intValue2 == n ? i : i - (n - this.intValue2);
            if (n2 >= 0) {
               doubleValues10[i] = this.doubleValues[n2];
               intValues[i] = (int[])this.intValues[n2].clone();
               continue;
            }
         }

         doubleValues10[i] = entry4.method3491(doubles);
         intValues[i] = new int[]{entry4.intValue4, entry4.intValue4};
      }

      doubleValues10[n] = doubles;

      for (int i = n + 1; i < intValue; i++) {
         doubleValues10[i] = entry4.method3491(doubles);
      }

      for (int i = n; i < intValue; i++) {
         intValues[i] = new int[]{entry4.intValue4, entry4.intValue4};
      }

      TokenMotor.Entry entry = this.method3474(entry4.intValue10);
      TokenMotor.Entry entry2 = this.method3474(entry4.intValue10 + 1);
      long[] longs = new long[entry4.intValue12];

      for (int i = 0; i < entry4.intValue12; i++) {
         longs[i] = this.random.nextLong();
      }

      int[][][] intseses = new int[entry4.intValue12][][];
      double[] doubles2 = new double[entry4.intValue12];
      IntStream.range(0, entry4.intValue12)
         .parallel()
         .forEach(
            var10x -> {
               Random var11x = new Random(longs[var10x]);
               int[][] var12x = entry4.flag
                  ? entry4.method3500(method3471(intValues), doubleValues10, entry4.intValue11, true, var11x)
                  : entry4.method3499(method3471(intValues), doubleValues10, entry4.intValue13, true, true, var11x);
               int[][] var13x = new int[entry4.intValue11][2];
               double var14x = 0.0;
               double var16x = 0.0;

               for (int var18x = 0; var18x < entry4.intValue11; var18x++) {
                  var13x[var18x][0] = entry4.method3490(var12x[n + var18x][0], 0, var11x);
                  var13x[var18x][1] = entry4.method3490(var12x[n + var18x][1], 1, var11x);
                  var14x += var13x[var18x][0] * entry4.doubleValue4;
                  var16x += var13x[var18x][1] * entry4.doubleValue4;
               }

               intseses[var10x] = var13x;
               doubles2[var10x] = TokenMotor.Entry4.method3493(entry4, entry, entry2, doubleValues, var14x, var16x);
            }
         );
      int n2 = 0;

      for (int i = 1; i < entry4.intValue12; i++) {
         if (doubles2[i] < doubles2[n2]) {
            n2 = i;
         }
      }

      int[][] intValues2 = intseses[n2];
      this.intValues2 = intValues2;
      this.intValue3 = 1;
      this.flag2 = intValues2.length > 1;
      return intValues2[0];
   }

   private static int[][] method3471(int[][] intValues) {
      int[][] intses = new int[intValues.length][];

      for (int i = 0; i < intValues.length; i++) {
         intses[i] = (int[])intValues[i].clone();
      }

      return intses;
   }

   private TokenMotor.Entry method3472(Rotation rotation, LivingEntity livingEntity) {
      Vec3 eyePosition = mc.player.getEyePosition();
      AABB boundingBox = livingEntity.getBoundingBox();
      Vec3 center = boundingBox.getCenter();
      double x = center.x - eyePosition.x;
      double d = center.y - eyePosition.y;
      double x2 = center.z - eyePosition.z;
      if (x * x + d * d + x2 * x2 < 1.0E-10) {
         return null;
      } else {
         TokenMotor.Entry entry = new TokenMotor.Entry();
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

   private void method3473(TokenMotor.Entry entry) {
      System.arraycopy(this.entrys, 0, this.entrys, 1, 7);
      this.entrys[0] = entry;
      if (this.intValue < 8) {
         this.intValue++;
      }
   }

   private TokenMotor.Entry method3474(int n) {
      return this.entrys[Math.min(n, this.intValue - 1)];
   }

   private void method3475(double[] doubles, int[] ints) {
      int n = this.entry4.intValue2;
      if (this.intValue2 < n) {
         this.doubleValues[this.intValue2] = doubles;
         this.intValues[this.intValue2] = ints;
         this.intValue2++;
      } else {
         System.arraycopy(this.doubleValues, 1, this.doubleValues, 0, n - 1);
         System.arraycopy(this.intValues, 1, this.intValues, 0, n - 1);
         this.doubleValues[n - 1] = doubles;
         this.intValues[n - 1] = ints;
      }
   }

   private double[] method3476(TokenMotor.Entry4 entry4, double[] doubleValues, double[] doubles) {
      int n = entry4.intValue10;
      TokenMotor.Entry entry = this.method3474(n);
      TokenMotor.Entry entry2 = this.method3474(n + 1);
      TokenMotor.Entry entry3 = this.method3474(n + 2);
      double[] doubleValues6 = new double[]{
         mc.options.keyUp.isDown() ? 1.0 : 0.0,
         mc.options.keyDown.isDown() ? 1.0 : 0.0,
         mc.options.keyLeft.isDown() ? 1.0 : 0.0,
         mc.options.keyRight.isDown() ? 1.0 : 0.0,
         mc.options.keyJump.isDown() ? 1.0 : 0.0,
         mc.player.onGround() ? 1.0 : 0.0,
         mc.player.isSprinting() ? 1.0 : 0.0,
         mc.player.isCrouching() ? 1.0 : 0.0,
         mc.player.getAttackStrengthScale(0.0F),
         Mth.clamp((float)mc.player.attackStrengthTicker, 0.0F, 50.0F),
         this.flag3 ? 1.0 : 0.0
      };
      return TokenMotor.Entry4.method3492(
         entry4,
         doubleValues,
         new double[]{entry.doubleValue3, entry.doubleValue4},
         new double[]{entry2.doubleValue3, entry2.doubleValue4},
         new double[]{entry3.doubleValue3, entry3.doubleValue4},
         new double[]{entry.doubleValue5, entry.doubleValue7, entry.doubleValue6, entry.doubleValue8},
         entry.doubleValue9,
         entry2.doubleValue9,
         doubleValues6,
         this.doubleValue,
         this.doubleValues2,
         doubles
      );
   }

   private void method3477() {
      float attackStrengthScale = mc.player.getAttackStrengthScale(0.0F);
      this.flag3 = attackStrengthScale < this.floatValue - 1.0E-4F;
      this.floatValue = attackStrengthScale;
   }

   private void method3478(int id) {
      TokenMotor.Entry4 entry4 = this.entry4;
      int n = entry4 == null ? 16 : entry4.intValue2;
      this.doubleValues = new double[n][];
      this.intValues = new int[n][];
      this.intValue2 = 0;
      Arrays.fill(this.entrys, null);
      this.intValue = 0;
      Arrays.fill(this.doubleValues2, 0.0);
      this.doubleValue = 0.0;
      this.intValues2 = null;
      this.intValue3 = 0;
      this.flag2 = false;
      this.floatValue = 1.0F;
      this.flag3 = false;
      this.intValue4 = id;
      this.intValue5 = Integer.MIN_VALUE;
      this.intValue6 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3479(int id) {
      this.intValue4 = id;
      this.intValue6 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3480(int tickCount, Rotation rotation) {
      this.intValue6 = tickCount;
      this.rotation = rotation;
   }

   public void reset() {
      this.method3478(Integer.MIN_VALUE);
   }

   private TokenMotor.Entry4 method3481() {
      if (this.entry4 != null || this.flag) {
         return this.entry4;
      } else if (this.completableFuture == null) {
         this.longValue = System.currentTimeMillis();
         this.completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
               TokenMotor.Entry4 entry4 = method3482();
               entry4.method3504();
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
            this.doubleValues = new double[this.entry4.intValue2][];
            this.intValues = new int[this.entry4.intValue2][];
            this.intValue2 = 0;
            System.out
               .println(
                  "v5 token motor loaded: окно "
                     + this.entry4.intValue
                     + ", префикс "
                     + this.entry4.intValue2
                     + ", словарь "
                     + this.entry4.intValue3
                     + ", слоёв "
                     + this.entry4.entry2s.length
                     + ", сенса-константа "
                     + this.entry4.doubleValue4
                     + ", выбор из "
                     + this.entry4.intValue12
                     + " кусками по "
                     + this.entry4.intValue11
                     + ", эталон сошёлся, "
                     + (System.currentTimeMillis() - this.longValue)
                     + " мс"
               );
         } catch (Exception e) {
            Object cause = e instanceof CompletionException && e.getCause() != null ? e.getCause() : e;
            this.entry4 = null;
            this.flag = true;
            System.out.println("v5 не смог загрузить token_motor.json: " + cause);
         }

         this.completableFuture = null;
         return this.entry4;
      }
   }

   private static TokenMotor.Entry4 method3482() throws IOException {
      for (Path path : items) {
         if (Files.isRegularFile(path)) {
            TokenMotor.Entry4 entry4 = TokenMotor.Entry4.method3507(path);
            System.out.println("v5 token motor: " + path.toAbsolutePath());
            return entry4;
         }
      }

      InputStream resourceAsStream = TokenMotor.class.getClassLoader().getResourceAsStream("models/token_motor.json");
      if (resourceAsStream == null) {
         throw new IOException("token_motor.json не найден ни рядом с клиентом, ни в jar");
      } else {
         TokenMotor.Entry4 entry4;
         try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
            entry4 = TokenMotor.Entry4.method3508(inputStreamReader);
         }

         return entry4;
      }
   }

   private static double method3483(double d) {
      return ((d + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
   }

   private static double asinh(double x) {
      return x >= 0.0 ? Math.log(x + Math.sqrt(x * x + 1.0)) : -Math.log(-x + Math.sqrt(x * x + 1.0));
   }

   private static double method3484(double d, double d2, double d3) {
      return Math.max(d2, Math.min(d3, d));
   }

   private static boolean method3485(double[] doubles) {
      for (double d : doubles) {
         if (!Double.isFinite(d)) {
            return false;
         }
      }

      return true;
   }

   private static double method3486(double d) {
      return 0.5 * d * (1.0 + method3487(d / Math.sqrt(2.0)));
   }

   private static double method3487(double d) {
      double d2 = Math.abs(d);
      double d3;
      if (d2 < 2.5) {
         double d4 = d2;
         double d5 = d2;
         double d6 = d2 * d2;

         for (int i = 1; i < 200; i++) {
            d4 *= -d6 / i;
            double d7 = d4 / (2 * i + 1);
            d5 += d7;
            if (Math.abs(d7) < 1.0E-17 * Math.abs(d5)) {
               break;
            }
         }

         d3 = 2.0 / Math.sqrt(Math.PI) * d5;
      } else {
         double d4 = d2 * d2;
         double d5 = d2;
         double d6 = d2;
         double d7 = 0.0;

         for (int i = 1; i < 400; i++) {
            double d8 = i / 2.0;
            double d9 = d2 + d8 * d7;
            d7 = d9 == 0.0 ? 1.0E-300 : 1.0 / d9;
            d6 = d2 + d8 / d6;
            double d10 = d6 * d7;
            d5 *= d10;
            if (Math.abs(d10 - 1.0) < 1.0E-16) {
               break;
            }
         }

         double d8 = Math.exp(-d4) / Math.sqrt(Math.PI) / d5;
         d3 = 1.0 - d8;
      }

      return d < 0.0 ? -d3 : d3;
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
      public double[][] doubleValues;
      public double[] doubleValues2;
      public double[][] doubleValues3;
      public double[] doubleValues4;
      public double[][] doubleValues5;
      public double[] doubleValues6;
      public double[][] doubleValues7;
      public double[] doubleValues8;
      public double[] doubleValues9;
      public double[] doubleValues10;
      public double[] doubleValues11;
      public double[] doubleValues12;
   }

   private static final class Entry3 {
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public double doubleValue;
      public double doubleValue2;
      public double[] doubleValues6;
      public double doubleValue3;
      public double doubleValue4;
      public double[] doubleValues7;
      public double[] doubleValues8;
      public double[] doubleValues9;
      public int[][] intValues;
      public double[][] doubleValues10;
      public double[] doubleValues11;
      public double[] doubleValues12;
      public int[][] intValues2;
      public int[][] intValues3;
      public double doubleValue5;
      public int[][] intValues4;
      public int[][] intValues5;
   }

   private static final class Entry4 {
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int intValue4;
      public int intValue5;
      public int intValue6;
      public int intValue7;
      public int intValue8;
      public int intValue9;
      public int intValue10;
      public int intValue11;
      public int intValue12;
      public int intValue13;
      public boolean flag;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public int intValue14;
      public int intValue15;
      public int intValue16;
      public double[] doubleValues;
      public final Map<Integer, int[]>[] itemss = new Map[]{new HashMap(), new HashMap()};
      public double[][] doubleValues2;
      public double[][] doubleValues3;
      public double[][] doubleValues4;
      public double[][] doubleValues5;
      public double[] doubleValues6;
      public double[][] doubleValues7;
      public double[] doubleValues8;
      public TokenMotor.Entry2[] entry2s;
      public double[] doubleValues9;
      public double[] doubleValues10;
      public double[][] doubleValues11;
      public double[] doubleValues12;
      public TokenMotor.Entry3 entry3;
      public boolean[][] flags;

      public int method3488(int n) {
         int n2 = Math.abs(n);
         if (n2 <= this.intValue14) {
            return n + this.intValue14;
         } else {
            int n3 = 0;

            while (n3 + 1 < this.doubleValues.length && this.doubleValues[n3 + 1] <= n2) {
               n3++;
            }

            n3 = Math.max(0, Math.min(n3, this.intValue16 - 1));
            return n > 0 ? this.intValue15 + n3 : this.intValue15 + this.intValue16 + n3;
         }
      }

      private double[] method3489(double[] doubles, int n) {
         double[] doubles2 = (double[])doubles.clone();
         boolean[] flag = this.flags[n];

         for (int i = 0; i < doubles2.length; i++) {
            if (!flag[i]) {
               doubles2[i] = Double.NEGATIVE_INFINITY;
            }
         }

         return doubles2;
      }

      public int method3490(int n, int n2, Random random) {
         if (n < this.intValue15) {
            return n - this.intValue14;
         } else {
            int[] ints = this.itemss[n2].get(n);
            if (ints != null && ints.length > 0) {
               return random == null ? ints[0] : ints[random.nextInt(ints.length)];
            } else {
               int n3 = n - this.intValue15;
               byte b = 1;
               if (n3 >= this.intValue16) {
                  n3 -= this.intValue16;
                  b = -1;
               }

               return b * (int)Math.round(Math.sqrt(this.doubleValues[n3] * this.doubleValues[n3 + 1]));
            }
         }
      }

      public double[] method3491(double[] doubles) {
         double[] doubles2 = new double[this.intValue7];
         System.arraycopy(doubles, 0, doubles2, 0, this.intValue8);
         return doubles2;
      }

      public static double[] method3492(
         TokenMotor.Entry4 entry4,
         double[] doubleValues,
         double[] doubleValues2,
         double[] doubleValues3,
         double[] doubleValues4,
         double[] doubleValues5,
         double doubleValue,
         double d,
         double[] doubleValues6,
         double doubleValue2,
         double[] doubles,
         double[] doubles2
      ) {
         double x = TokenMotor.method3483(doubleValues2[0] - doubleValues3[0]);
         double x2 = doubleValues2[1] - doubleValues3[1];
         doubles2[0] = entry4.doubleValue * x + (1.0 - entry4.doubleValue) * doubles[0];
         doubles2[1] = entry4.doubleValue * x2 + (1.0 - entry4.doubleValue) * doubles[1];
         doubles2[2] = entry4.doubleValue2 * x + (1.0 - entry4.doubleValue2) * doubles[2];
         doubles2[3] = entry4.doubleValue2 * x2 + (1.0 - entry4.doubleValue2) * doubles[3];
         doubles2[4] = entry4.doubleValue3 * Math.abs(x) + (1.0 - entry4.doubleValue3) * doubles[4];
         doubles2[5] = entry4.doubleValue3 * Math.abs(x2) + (1.0 - entry4.doubleValue3) * doubles[5];
         double d2 = TokenMotor.method3483(doubleValues3[0] - doubleValues4[0]);
         double d3 = doubleValues3[1] - doubleValues4[1];
         double x3 = 0.5 * (doubleValues5[2] - doubleValues5[0]);
         double x4 = 0.5 * (doubleValues5[3] - doubleValues5[1]);
         double[] doubles3 = new double[entry4.intValue7];
         int n = 0;
         doubles3[n++] = TokenMotor.asinh(x);
         doubles3[n++] = TokenMotor.asinh(x2);
         doubles3[n++] = TokenMotor.asinh(x - d2);
         doubles3[n++] = TokenMotor.asinh(x2 - d3);
         doubles3[n++] = TokenMotor.asinh(x3);
         doubles3[n++] = TokenMotor.asinh(x4);
         doubles3[n++] = Math.log1p(doubleValue);
         doubles3[n++] = TokenMotor.method3484(doubleValue - d, -2.0, 2.0);
         doubles3[n++] = Math.log(entry4.doubleValue4);

         for (double d4 : doubleValues6) {
            doubles3[n++] = d4;
         }

         for (int i = 0; i < 6; i++) {
            doubles3[n++] = TokenMotor.asinh(doubles2[i]);
         }

         double x5 = TokenMotor.method3483(doubleValues2[0] - doubleValues[0]);
         double y = doubleValues2[1] - doubleValues[1];
         double x6 = x5 + (entry4.intValue10 + 1) * x;
         double x7 = y + (entry4.intValue10 + 1) * x2;
         double x8 = Math.max(Math.hypot(x5, y), 1.0E-6);
         double d4 = 0.5 * (doubleValues5[0] + doubleValues5[2]);
         double d5 = 0.5 * (doubleValues5[1] + doubleValues5[3]);
         double d6 = Math.max(x3, 0.001);
         double d7 = Math.max(x4, 0.001);
         double d8 = TokenMotor.method3484((-x5 - d4) / d6, -4.0, 4.0);
         double d9 = TokenMotor.method3484((-y - d5) / d7, -4.0, 4.0);
         double d10 = Math.max(Math.abs(d8), Math.abs(d9));
         double d11 = 1.0 / (1.0 + Math.exp(-6.0 * (1.0 - d10)));
         double d12 = -x / d6;
         double d13 = -x2 / d7;
         double d14 = Math.max(1.0 - d8 * (d12 >= 0.0 ? 1.0 : -1.0), 0.0);
         double d15 = Math.max(1.0 - d9 * (d13 >= 0.0 ? 1.0 : -1.0), 0.0);
         double d16 = Math.min(d14 / Math.max(Math.abs(d12), 1.0E-4), d15 / Math.max(Math.abs(d13), 1.0E-4));
         d16 = TokenMotor.method3484(d16, 0.0, 400.0);
         doubles3[n++] = TokenMotor.asinh(x5);
         doubles3[n++] = TokenMotor.asinh(y);
         doubles3[n++] = TokenMotor.asinh(x6);
         doubles3[n++] = TokenMotor.asinh(x7);
         doubles3[n++] = TokenMotor.asinh(x8);
         doubles3[n++] = x5 / x8;
         doubles3[n++] = y / x8;
         doubles3[n++] = d8;
         doubles3[n++] = d9;
         doubles3[n++] = d11;
         doubles3[n++] = Math.tanh(d16 / 8.0);
         doubles3[n++] = 1.0 / (1.0 + d16);
         doubles3[n++] = doubleValues[1] / 90.0;
         doubles3[n++] = (90.0 - Math.abs(doubleValues[1])) / 90.0;
         doubles3[n++] = Math.tanh(doubleValue2 / 32.0);
         doubles3[n] = 1.0;
         return doubles3;
      }

      public static double method3493(TokenMotor.Entry4 entry4, TokenMotor.Entry entry, TokenMotor.Entry entry2, double[] doubleValues, double d, double d2) {
         return method3494(
            entry4,
            new double[]{entry.doubleValue3, entry.doubleValue4},
            new double[]{entry2.doubleValue3, entry2.doubleValue4},
            new double[]{entry.doubleValue5, entry.doubleValue7, entry.doubleValue6, entry.doubleValue8},
            doubleValues,
            d,
            d2
         );
      }

      public static double method3494(TokenMotor.Entry4 entry4, double[] doubleValues2, double[] doubleValues3, double[] doubleValues5, double[] doubleValues, double d, double d2) {
         double d3 = TokenMotor.method3483(doubleValues2[0] - doubleValues3[0]);
         double d4 = doubleValues2[1] - doubleValues3[1];
         int n = entry4.intValue11 - 1 + entry4.intValue10;
         double d5 = TokenMotor.method3483(doubleValues2[0] + n * d3 - (doubleValues[0] + d));
         double d6 = doubleValues2[1] + n * d4 - TokenMotor.method3484(doubleValues[1] + d2, -90.0, 90.0);
         double d7 = 0.5 * (doubleValues5[0] + doubleValues5[2]);
         double d8 = 0.5 * (doubleValues5[1] + doubleValues5[3]);
         double d9 = Math.max(0.5 * (doubleValues5[2] - doubleValues5[0]), 0.001);
         double d10 = Math.max(0.5 * (doubleValues5[3] - doubleValues5[1]), 0.001);
         return Math.max(Math.abs((-d5 - d7) / d9), Math.abs((-d6 - d8) / d10));
      }

      public double[][] method3495(int[][] intValues, double[][] doubleValues10) {
         int length = intValues.length;
         int endExclusive = length * 2;
         int n = this.intValue5;
         double[][] doubleses = new double[endExclusive][n];

         for (int i = 0; i < length; i++) {
            double[] doubles = this.method3496(doubleValues10[i]);

            for (int j = 0; j < 2; j++) {
               double[] doubles2 = doubleses[i * 2 + j];
               double[] doubles3 = this.doubleValues2[intValues[i][j]];
               double[] doubles4 = this.doubleValues3[i];
               double[] doubles5 = this.doubleValues4[j];

               for (int k = 0; k < n; k++) {
                  doubles2[k] = doubles3[k] + doubles4[k] + doubles5[k] + doubles[k];
               }
            }
         }

         int n2 = n / this.intValue6;
         double d = 1.0 / Math.sqrt(n2);

         for (TokenMotor.Entry2 entry2 : this.entry2s) {
            double[][] doubleses2 = new double[endExclusive][n];
            double[][] doubleses3 = new double[endExclusive][n];
            double[][] doubleses4 = new double[endExclusive][n];
            IntStream.range(0, endExclusive).parallel().forEach(var6x -> {
               double[] var7x = method3498(entry2.doubleValues, method3497(doubleses[var6x], entry2.doubleValues9, entry2.doubleValues10), entry2.doubleValues2);
               System.arraycopy(var7x, 0, doubleses2[var6x], 0, n);
               System.arraycopy(var7x, n, doubleses3[var6x], 0, n);
               System.arraycopy(var7x, 2 * n, doubleses4[var6x], 0, n);
            });
            double[][] doubleses5 = new double[endExclusive][n];
            IntStream.range(0, this.intValue6 * endExclusive).parallel().forEach(var8x -> {
               int var9x = var8x / endExclusive;
               int var10x = var8x % endExclusive;
               int var11x = var9x * n2;
               double[] var12x = new double[endExclusive];
               double var13x = Double.NEGATIVE_INFINITY;

               for (int var15x = 0; var15x < endExclusive; var15x++) {
                  double var16x = 0.0;

                  for (int var18x = 0; var18x < n2; var18x++) {
                     var16x += doubleses2[var10x][var11x + var18x] * doubleses3[var15x][var11x + var18x];
                  }

                  var12x[var15x] = var16x * d;
                  var13x = Math.max(var13x, var12x[var15x]);
               }

               double var21x = 0.0;

               for (int var17x = 0; var17x < endExclusive; var17x++) {
                  var12x[var17x] = Math.exp(var12x[var17x] - var13x);
                  var21x += var12x[var17x];
               }

               for (int i = 0; i < n2; i++) {
                  double d2 = 0.0;

                  for (int j = 0; j < endExclusive; j++) {
                     d2 += var12x[j] * doubleses4[j][var11x + i];
                  }

                  doubleses5[var10x][var11x + i] = d2 / var21x;
               }
            });
            IntStream.range(0, endExclusive).parallel().forEach(var4x -> {
               double[] var5x = method3498(entry2.doubleValues3, doubleses5[var4x], entry2.doubleValues4);

               for (int var6x = 0; var6x < n; var6x++) {
                  doubleses[var4x][var6x] = doubleses[var4x][var6x] + var5x[var6x];
               }

               double[] var9x = method3498(entry2.doubleValues5, method3497(doubleses[var4x], entry2.doubleValues11, entry2.doubleValues12), entry2.doubleValues6);

               for (int var7x = 0; var7x < var9x.length; var7x++) {
                  var9x[var7x] = TokenMotor.method3486(var9x[var7x]);
               }

               double[] var10x = method3498(entry2.doubleValues7, var9x, entry2.doubleValues8);

               for (int var8x = 0; var8x < n; var8x++) {
                  doubleses[var4x][var8x] = doubleses[var4x][var8x] + var10x[var8x];
               }
            });
         }

         double[][] doubleses2 = new double[endExclusive][];
         IntStream.range(0, endExclusive)
            .parallel()
            .forEach(
               var3x -> doubleses2[var3x] = method3498(this.doubleValues11, method3497(doubleses[var3x], this.doubleValues9, this.doubleValues10), this.doubleValues12)
            );
         return doubleses2;
      }

      private double[] method3496(double[] doubles) {
         double[] doubles2 = method3498(this.doubleValues5, doubles, this.doubleValues6);

         for (int i = 0; i < doubles2.length; i++) {
            doubles2[i] /= 1.0 + Math.exp(-doubles2[i]);
         }

         return method3498(this.doubleValues7, doubles2, this.doubleValues8);
      }

      private static double[] method3497(double[] doubles, double[] doubleValues9, double[] doubleValues10) {
         double d = 0.0;

         for (double d2 : doubles) {
            d += d2;
         }

         d /= doubles.length;
         double d2 = 0.0;

         for (double d3 : doubles) {
            d2 += (d3 - d) * (d3 - d);
         }

         d2 /= doubles.length;
         double d3 = 1.0 / Math.sqrt(d2 + 1.0E-5);
         double[] doubles2 = new double[doubles.length];

         for (int i = 0; i < doubles.length; i++) {
            doubles2[i] = (doubles[i] - d) * d3 * doubleValues9[i] + doubleValues10[i];
         }

         return doubles2;
      }

      private static double[] method3498(double[][] doubleValues5, double[] doubles, double[] doubleValues6) {
         double[] doubles2 = new double[doubleValues5.length];

         for (int i = 0; i < doubleValues5.length; i++) {
            double d = doubleValues6[i];
            double[] doubles3 = doubleValues5[i];

            for (int j = 0; j < doubles.length; j++) {
               d += doubles3[j] * doubles[j];
            }

            doubles2[i] = d;
         }

         return doubles2;
      }

      public int[][] method3499(int[][] intValues, double[][] doubleValues10, int n, boolean flag, boolean flag2, Random random) {
         int length = intValues.length;
         int n2 = length * 2;

         for (int i = 0; i < Math.max(1, n); i++) {
            int n3 = 0;

            for (int j = 0; j < n2; j++) {
               if (intValues[j / 2][j % 2] == this.intValue4) {
                  n3++;
               }
            }

            if (n3 == 0) {
               break;
            }

            double[][] doubleses = this.method3495(intValues, doubleValues10);
            int[] ints = new int[n2];
            double[] doubles = new double[n2];

            for (int j = 0; j < n2; j++) {
               boolean flag3 = intValues[j / 2][j % 2] == this.intValue4;
               double[] doubles2 = method3501(this.method3489(doubleses[j], j % 2));
               ints[j] = flag2 ? method3502(doubles2, random) : method3503(doubles2);
               doubles[j] = !flag3 ? -1.0 : (flag ? random.nextDouble() : doubles2[ints[j]]);
            }

            int n4 = Math.min(Math.max(1, (int)Math.ceil((double)n3 / Math.max(1, n))), n3);
            boolean[] flags = new boolean[n2];

            for (int j = 0; j < n4; j++) {
               int n5 = -1;

               for (int k = 0; k < n2; k++) {
                  if (!flags[k] && doubles[k] >= 0.0 && (n5 < 0 || doubles[k] > doubles[n5])) {
                     n5 = k;
                  }
               }

               if (n5 < 0) {
                  break;
               }

               flags[n5] = true;
               intValues[n5 / 2][n5 % 2] = ints[n5];
            }
         }

         boolean flag3 = false;

         for (int i = 0; i < n2; i++) {
            if (intValues[i / 2][i % 2] == this.intValue4) {
               flag3 = true;
               break;
            }
         }

         if (flag3) {
            double[][] doubleses = this.method3495(intValues, doubleValues10);

            for (int i = 0; i < n2; i++) {
               if (intValues[i / 2][i % 2] == this.intValue4) {
                  intValues[i / 2][i % 2] = method3503(this.method3489(doubleses[i], i % 2));
               }
            }
         }

         return intValues;
      }

      public int[][] method3500(int[][] intValues, double[][] doubleValues10, int n, boolean flag, Random random) {
         for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2; j++) {
               double[][] doubleses = this.method3495(intValues, doubleValues10);
               double[] doubles = method3501(this.method3489(doubleses[(this.intValue2 + i) * 2 + j], j));
               intValues[this.intValue2 + i][j] = flag ? method3502(doubles, random) : method3503(doubles);
            }
         }

         return intValues;
      }

      private static double[] method3501(double[] doubles) {
         double d = Double.NEGATIVE_INFINITY;

         for (double d2 : doubles) {
            d = Math.max(d, d2);
         }

         double[] doubles2 = new double[doubles.length];
         double d2 = 0.0;

         for (int i = 0; i < doubles.length; i++) {
            doubles2[i] = Math.exp(doubles[i] - d);
            d2 += doubles2[i];
         }

         for (int i = 0; i < doubles.length; i++) {
            doubles2[i] /= d2;
         }

         return doubles2;
      }

      private static int method3502(double[] doubles, Random random) {
         double nextDouble = random.nextDouble();
         double d = 0.0;

         for (int i = 0; i < doubles.length; i++) {
            d += doubles[i];
            if (nextDouble < d) {
               return i;
            }
         }

         return doubles.length - 1;
      }

      private static int method3503(double[] doubles) {
         int n = 0;

         for (int i = 1; i < doubles.length; i++) {
            if (doubles[i] > doubles[n]) {
               n = i;
            }
         }

         return n;
      }

      public void method3504() {
         TokenMotor.Entry3 entry3 = this.entry3;
         if (entry3 == null) {
            throw new IllegalStateException("в модели нет эталонного тика");
         } else {
            double[] doubles = new double[6];
            double[] doubles2 = method3492(
               this,
               entry3.doubleValues,
               entry3.doubleValues2,
               entry3.doubleValues3,
               entry3.doubleValues4,
               entry3.doubleValues5,
               entry3.doubleValue,
               entry3.doubleValue2,
               entry3.doubleValues6,
               entry3.doubleValue4,
               entry3.doubleValues7,
               doubles
            );
            method3505(method3506(doubles2, entry3.doubleValues8), "контекст");
            method3505(method3506(doubles, entry3.doubleValues9), "EMA");
            double[][] doubleses = this.method3495(entry3.intValues, entry3.doubleValues10);
            method3505(method3506(doubleses[this.intValue2 * 2], entry3.doubleValues11), "логиты yaw");
            method3505(method3506(doubleses[this.intValue2 * 2 + 1], entry3.doubleValues12), "логиты pitch");
            int[][] intses = this.method3499(TokenMotor.method3471(entry3.intValues), entry3.doubleValues10, this.intValue13, false, false, null);

            for (int i = 0; i < this.intValue; i++) {
               for (int j = 0; j < 2; j++) {
                  if (intses[i][j] != entry3.intValues2[i][j]) {
                     throw new IllegalStateException(
                        "эталон: заполнение разошлось в позиции " + i + "/" + j + ": " + intses[i][j] + " вместо " + entry3.intValues2[i][j]
                     );
                  }
               }
            }

            double d = 0.0;
            double d2 = 0.0;

            for (int i = 0; i < this.intValue11; i++) {
               int n = this.method3490(intses[this.intValue2 + i][0], 0, null);
               int n2 = this.method3490(intses[this.intValue2 + i][1], 1, null);
               if (n != entry3.intValues3[i][0] || n2 != entry3.intValues3[i][1]) {
                  throw new IllegalStateException("эталон: декод тика " + i + " разошёлся");
               }

               d += n * this.doubleValue4;
               d2 += n2 * this.doubleValue4;
            }

            double d3 = method3494(this, entry3.doubleValues2, entry3.doubleValues3, entry3.doubleValues5, entry3.doubleValues, d, d2);
            method3505(Math.abs(d3 - entry3.doubleValue5), "оценка плана");
            if (entry3.intValues4 == null) {
               if (this.flag) {
                  throw new IllegalStateException("файл требует режим first, но эталона для него нет");
               }
            } else {
               int[][] intses2 = this.method3500(TokenMotor.method3471(entry3.intValues), entry3.doubleValues10, this.intValue11, false, null);

               for (int i = 0; i < this.intValue11; i++) {
                  for (int j = 0; j < 2; j++) {
                     if (intses2[this.intValue2 + i][j] != entry3.intValues4[i][j]) {
                        throw new IllegalStateException(
                           "эталон first: тик "
                              + i
                              + "/"
                              + j
                              + ": "
                              + intses2[this.intValue2 + i][j]
                              + " вместо "
                              + entry3.intValues4[i][j]
                        );
                     }
                  }

                  if (this.method3490(intses2[this.intValue2 + i][0], 0, null) != entry3.intValues5[i][0]
                     || this.method3490(intses2[this.intValue2 + i][1], 1, null) != entry3.intValues5[i][1]) {
                     throw new IllegalStateException("эталон first: декод тика " + i + " разошёлся");
                  }
               }
            }
         }
      }

      private static void method3505(double d, String str) {
         if (!(d <= 1.0E-9)) {
            throw new IllegalStateException(
               "эталон не сошёлся: max|Δ| = " + d + " в '" + str + "', допуск 1.0E-9. Раскладка весов не та, что ожидает рантайм."
            );
         }
      }

      private static double method3506(double[] doubles, double[] doubleValues8) {
         if (doubles.length != doubleValues8.length) {
            return Double.POSITIVE_INFINITY;
         } else {
            double d = 0.0;

            for (int i = 0; i < doubles.length; i++) {
               d = Math.max(d, Math.abs(doubles[i] - doubleValues8[i]));
            }

            return d;
         }
      }

      public static TokenMotor.Entry4 method3507(Path path) throws IOException {
         TokenMotor.Entry4 entry4;
         try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            entry4 = method3508(bufferedReader);
         }

         return entry4;
      }

      public static TokenMotor.Entry4 method3508(Reader inputStreamReader) throws IOException {
         TokenMotor.Entry4 entry4 = new TokenMotor.Entry4();
         JsonReader jsonReader = new JsonReader(inputStreamReader);

         try {
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
               String nextName = jsonReader.nextName();
               switch (nextName) {
                  case "format":
                     String nextString = jsonReader.nextString();
                     if (!"neurotest-token-motor-v2".equals(nextString)) {
                        throw new IllegalStateException("формат '" + nextString + "', ожидался 'neurotest-token-motor-v2'");
                     }
                     break;
                  case "config":
                     entry4.method3509(jsonReader);
                     break;
                  case "decode_pools":
                     entry4.method3511(jsonReader);
                     break;
                  case "support":
                     entry4.method3510(jsonReader);
                     break;
                  case "weights":
                     entry4.method3512(jsonReader);
                     break;
                  case "golden":
                     entry4.method3513(jsonReader);
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
         entry4.method3514();
         return entry4;
      }

      private void method3509(JsonReader jsonReader) throws IOException {
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            switch (nextName) {
               case "window":
                  this.intValue = jsonReader.nextInt();
                  break;
               case "prefix":
                  this.intValue2 = jsonReader.nextInt();
                  break;
               case "vocab":
                  this.intValue3 = jsonReader.nextInt();
                  break;
               case "mask_id":
                  this.intValue4 = jsonReader.nextInt();
                  break;
               case "d_model":
                  this.intValue5 = jsonReader.nextInt();
                  break;
               case "n_head":
                  this.intValue6 = jsonReader.nextInt();
                  break;
               case "n_ctx":
                  this.intValue7 = jsonReader.nextInt();
                  break;
               case "n_ctx_exo":
                  this.intValue8 = jsonReader.nextInt();
                  break;
               case "n_ctx_endo":
                  this.intValue9 = jsonReader.nextInt();
                  break;
               case "latency":
                  this.intValue10 = jsonReader.nextInt();
                  break;
               case "exec_k":
                  this.intValue11 = jsonReader.nextInt();
                  break;
               case "best_of":
                  this.intValue12 = jsonReader.nextInt();
                  break;
               case "steps_fill":
                  this.intValue13 = jsonReader.nextInt();
                  break;
               case "fill_mode":
                  this.flag = "first".equals(jsonReader.nextString());
                  break;
               case "mdpc":
                  this.doubleValue4 = jsonReader.nextDouble();
                  break;
               case "vocab_n_named":
                  this.intValue14 = jsonReader.nextInt();
                  break;
               case "vocab_n_named_tok":
                  this.intValue15 = jsonReader.nextInt();
                  break;
               case "vocab_n_bucket":
                  this.intValue16 = jsonReader.nextInt();
                  break;
               case "vocab_edges":
                  this.doubleValues = method3516(jsonReader);
                  break;
               case "ema":
                  jsonReader.beginObject();

                  while (jsonReader.hasNext()) {
                     String str = jsonReader.nextName();
                     switch (str) {
                        case "a8":
                           this.doubleValue = jsonReader.nextDouble();
                           break;
                        case "a32":
                           this.doubleValue2 = jsonReader.nextDouble();
                           break;
                        case "a16":
                           this.doubleValue3 = jsonReader.nextDouble();
                           break;
                        default:
                           jsonReader.skipValue();
                     }
                  }

                  jsonReader.endObject();
                  break;
               default:
                  jsonReader.skipValue();
            }
         }

         jsonReader.endObject();
      }

      private void method3510(JsonReader jsonReader) throws IOException {
         this.flags = new boolean[2][];
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            int n = nextName.equals("yaw") ? 0 : (nextName.equals("pitch") ? 1 : -1);
            double[] doubles = method3516(jsonReader);
            if (n >= 0) {
               this.flags[n] = new boolean[doubles.length];

               for (int i = 0; i < doubles.length; i++) {
                  this.flags[n][i] = doubles[i] > 0.5;
               }
            }
         }

         jsonReader.endObject();
      }

      private void method3511(JsonReader jsonReader) throws IOException {
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            int n = "yaw".equals(nextName) ? 0 : 1;
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
               int n2 = Integer.parseInt(jsonReader.nextName());
               double[] doubles = method3516(jsonReader);
               int[] ints = new int[doubles.length];

               for (int i = 0; i < doubles.length; i++) {
                  ints[i] = (int)Math.round(doubles[i]);
               }

               this.itemss[n].put(n2, ints);
            }

            jsonReader.endObject();
         }

         jsonReader.endObject();
      }

      private void method3512(JsonReader jsonReader) throws IOException {
         ArrayList arrayList = new ArrayList();
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            switch (nextName) {
               case "tok":
                  this.doubleValues2 = method3518(jsonReader);
                  break;
               case "pos":
                  this.doubleValues3 = method3518(jsonReader);
                  break;
               case "axis":
                  this.doubleValues4 = method3518(jsonReader);
                  break;
               case "ctx0_w":
                  this.doubleValues5 = method3518(jsonReader);
                  break;
               case "ctx0_b":
                  this.doubleValues6 = method3516(jsonReader);
                  break;
               case "ctx2_w":
                  this.doubleValues7 = method3518(jsonReader);
                  break;
               case "ctx2_b":
                  this.doubleValues8 = method3516(jsonReader);
                  break;
               case "norm_w":
                  this.doubleValues9 = method3516(jsonReader);
                  break;
               case "norm_b":
                  this.doubleValues10 = method3516(jsonReader);
                  break;
               case "out_w":
                  this.doubleValues11 = method3518(jsonReader);
                  break;
               case "out_b":
                  this.doubleValues12 = method3516(jsonReader);
                  break;
               case "layers":
                  jsonReader.beginArray();

                  while (jsonReader.hasNext()) {
                     TokenMotor.Entry2 entry2 = new TokenMotor.Entry2();
                     jsonReader.beginObject();

                     while (jsonReader.hasNext()) {
                        String str = jsonReader.nextName();
                        switch (str) {
                           case "in_w":
                              entry2.doubleValues = method3518(jsonReader);
                              break;
                           case "in_b":
                              entry2.doubleValues2 = method3516(jsonReader);
                              break;
                           case "out_w":
                              entry2.doubleValues3 = method3518(jsonReader);
                              break;
                           case "out_b":
                              entry2.doubleValues4 = method3516(jsonReader);
                              break;
                           case "l1_w":
                              entry2.doubleValues5 = method3518(jsonReader);
                              break;
                           case "l1_b":
                              entry2.doubleValues6 = method3516(jsonReader);
                              break;
                           case "l2_w":
                              entry2.doubleValues7 = method3518(jsonReader);
                              break;
                           case "l2_b":
                              entry2.doubleValues8 = method3516(jsonReader);
                              break;
                           case "n1_w":
                              entry2.doubleValues9 = method3516(jsonReader);
                              break;
                           case "n1_b":
                              entry2.doubleValues10 = method3516(jsonReader);
                              break;
                           case "n2_w":
                              entry2.doubleValues11 = method3516(jsonReader);
                              break;
                           case "n2_b":
                              entry2.doubleValues12 = method3516(jsonReader);
                              break;
                           default:
                              jsonReader.skipValue();
                        }
                     }

                     jsonReader.endObject();
                     arrayList.add(entry2);
                  }

                  jsonReader.endArray();
                  break;
               default:
                  jsonReader.skipValue();
            }
         }

         jsonReader.endObject();
         this.entry2s = (TokenMotor.Entry2[])arrayList.toArray(new TokenMotor.Entry2[0]);
      }

      private void method3513(JsonReader jsonReader) throws IOException {
         TokenMotor.Entry3 entry3 = new TokenMotor.Entry3();
         jsonReader.beginObject();

         while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            switch (nextName) {
               case "inputs":
                  jsonReader.beginObject();

                  while (jsonReader.hasNext()) {
                     String str = jsonReader.nextName();
                     switch (str) {
                        case "cam":
                           entry3.doubleValues = method3516(jsonReader);
                           break;
                        case "tgt_lag":
                           entry3.doubleValues2 = method3516(jsonReader);
                           break;
                        case "tgt_lag1":
                           entry3.doubleValues3 = method3516(jsonReader);
                           break;
                        case "tgt_lag2":
                           entry3.doubleValues4 = method3516(jsonReader);
                           break;
                        case "box":
                           entry3.doubleValues5 = method3516(jsonReader);
                           break;
                        case "dist":
                           entry3.doubleValue = jsonReader.nextDouble();
                           break;
                        case "dist_prev":
                           entry3.doubleValue2 = jsonReader.nextDouble();
                           break;
                        case "ctx11":
                           entry3.doubleValues6 = method3516(jsonReader);
                           break;
                        case "mdpc":
                           entry3.doubleValue3 = jsonReader.nextDouble();
                           break;
                        case "since_zero":
                           entry3.doubleValue4 = jsonReader.nextDouble();
                           break;
                        case "slow":
                           entry3.doubleValues7 = method3516(jsonReader);
                           break;
                        default:
                           jsonReader.skipValue();
                     }
                  }

                  jsonReader.endObject();
                  break;
               case "context":
                  entry3.doubleValues8 = method3516(jsonReader);
                  break;
               case "slow_out":
                  entry3.doubleValues9 = method3516(jsonReader);
                  break;
               case "tokens_in":
                  entry3.intValues = method3515(method3518(jsonReader));
                  break;
               case "ctx_window":
                  entry3.doubleValues10 = method3518(jsonReader);
                  break;
               case "logits_yaw_at_p":
                  entry3.doubleValues11 = method3516(jsonReader);
                  break;
               case "logits_pitch_at_p":
                  entry3.doubleValues12 = method3516(jsonReader);
                  break;
               case "filled_greedy":
                  entry3.intValues2 = method3515(method3518(jsonReader));
                  break;
               case "decoded_first":
                  entry3.intValues3 = method3515(method3518(jsonReader));
                  break;
               case "plan_score":
                  entry3.doubleValue5 = jsonReader.nextDouble();
                  break;
               case "first_greedy":
                  entry3.intValues4 = method3515(method3518(jsonReader));
                  break;
               case "decoded_first_mode":
                  entry3.intValues5 = method3515(method3518(jsonReader));
                  break;
               default:
                  jsonReader.skipValue();
            }
         }

         jsonReader.endObject();
         this.entry3 = entry3;
      }

      private void method3514() {
         if (this.doubleValues2 != null && this.entry2s != null && this.doubleValues11 != null && this.doubleValues != null) {
            if (this.doubleValues2.length != this.intValue3 + 1 || this.doubleValues2[0].length != this.intValue5) {
               throw new IllegalStateException(
                  "эмбеддинг токенов "
                     + this.doubleValues2.length
                     + "x"
                     + this.doubleValues2[0].length
                     + ", ожидалось "
                     + (this.intValue3 + 1)
                     + "x"
                     + this.intValue5
               );
            } else if (this.doubleValues5[0].length != this.intValue7 || this.intValue7 != this.intValue8 + this.intValue9) {
               throw new IllegalStateException("ширина контекста не сходится");
            } else if (this.intValue15 + 2 * this.intValue16 != this.intValue3) {
               throw new IllegalStateException("словарь: " + this.intValue15 + " + 2*" + this.intValue16 + " != " + this.intValue3);
            } else if (this.intValue11 < 2 && this.intValue12 > 1) {
               throw new IllegalStateException("выбор из K требует кусков длиннее одного тика");
            } else if (this.flags != null
               && this.flags[0] != null
               && this.flags[1] != null
               && this.flags[0].length == this.intValue3
               && this.flags[1].length == this.intValue3) {
               for (int i = 0; i < 2; i++) {
                  boolean flag = false;

                  for (boolean flag2 : this.flags[i]) {
                     flag |= flag2;
                  }

                  if (!flag) {
                     throw new IllegalStateException("носитель оси " + i + " пуст");
                  }
               }
            } else {
               throw new IllegalStateException("в файле нет носителя `support` на " + this.intValue3 + " токенов");
            }
         } else {
            throw new IllegalStateException("в файле нет весов или словаря");
         }
      }

      private static int[][] method3515(double[][] doubleses) {
         int[][] intses = new int[doubleses.length][];

         for (int i = 0; i < doubleses.length; i++) {
            intses[i] = new int[doubleses[i].length];

            for (int j = 0; j < doubleses[i].length; j++) {
               intses[i][j] = (int)Math.round(doubleses[i][j]);
            }
         }

         return intses;
      }

      private static double[] method3516(JsonReader jsonReader) throws IOException {
         jsonReader.beginArray();
         double[] doubles = method3517(jsonReader);
         jsonReader.endArray();
         return doubles;
      }

      private static double[] method3517(JsonReader jsonReader) throws IOException {
         double[] original = new double[32];
         int newLength = 0;

         while (jsonReader.hasNext()) {
            if (newLength == original.length) {
               original = Arrays.copyOf(original, newLength * 2);
            }

            original[newLength++] = jsonReader.nextDouble();
         }

         return Arrays.copyOf(original, newLength);
      }

      private static double[][] method3518(JsonReader jsonReader) throws IOException {
         ArrayList arrayList = new ArrayList();
         jsonReader.beginArray();

         while (jsonReader.hasNext()) {
            if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
               arrayList.add(method3516(jsonReader));
            } else {
               arrayList.add(method3517(jsonReader));
            }
         }

         jsonReader.endArray();
         return (double[][])arrayList.toArray(new double[0][]);
      }
   }
}

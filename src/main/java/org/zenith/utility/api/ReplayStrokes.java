package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.MouseRotationEvent;

public final class ReplayStrokes implements MinecraftAccess {
   public static final ReplayStrokes INSTANCE = new ReplayStrokes();
   private static final double CONST_doubleValue = 0.15;
   private static final String text = "replay_strokes.json";
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 600;
   private static final int CONST_intValue3 = 160;
   private static final int CONST_intValue4 = 8;
   private static final double CONST_doubleValue2 = 25.0;
   private static final double CONST_doubleValue3 = 40.0;
   private static final double CONST_doubleValue4 = 10.0;
   private static final double CONST_doubleValue5 = 60.0;
   private static final double CONST_doubleValue6 = 50.0;
   private static final double CONST_doubleValue7 = 900.0;
   private static final double CONST_doubleValue8 = 1.0;
   private static final int CONST_intValue5 = 3;
   private static final double CONST_doubleValue9 = 2.5;
   private static final double CONST_doubleValue10 = 3.0;
   private static final double CONST_doubleValue11 = 60.0;
   private static final double CONST_doubleValue12 = 0.4;
   private static final double CONST_doubleValue13 = 2.5;
   private static final double CONST_doubleValue14 = 70.0;
   private static final double CONST_doubleValue15 = 0.4;
   private static final double CONST_doubleValue16 = 1.3;
   private static final int CONST_intValue6 = 6;
   private List<ReplayStrokes.Entry> library = new ArrayList<>();
   private CompletableFuture<List<ReplayStrokes.Entry>> completableFuture;
   private boolean loaded;
   private int intValue;
   private double doubleValue;
   private final ArrayDeque<ReplayStrokes.Entry> arrayDeque = new ArrayDeque<>();
   private double doubleValue2 = Double.NaN;
   private double doubleValue3;
   private double doubleValue4;
   private double doubleValue5 = Double.NaN;
   private boolean flag;
   private final ArrayDeque<double[]> arrayDeque2 = new ArrayDeque<>();
   private final List<double[]> items = new ArrayList<>();
   private double doubleValue6;
   private ReplayStrokes.Entry entry;
   private boolean flag2;
   private double doubleValue7;
   private double doubleValue8;
   private double doubleValue9;
   private double doubleValue10;
   private double doubleValue11;
   private double doubleValue12;
   private double doubleValue13;
   private double doubleValue14;
   private double doubleValue15;
   private double doubleValue16;
   private int intValue2;
   private double doubleValue17;
   private final double[] doubleValues = new double[5];
   private final double[] doubleValues2 = new double[2];
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private double doubleValue18 = Double.NaN;
   private double doubleValue19 = Double.NaN;

   private ReplayStrokes() {
   }

   public void method6649(MouseRotationEvent event, LivingEntity livingEntity, boolean enabled, float f, boolean flag, double d) {
      double d2 = System.nanoTime() / 1000000.0;
      double d3 = Double.isNaN(this.doubleValue2) ? 16.0 : Mth.clamp(d2 - this.doubleValue2, 0.5, 100.0);
      this.doubleValue2 = d2;
      this.method6662();
      double d4 = event.getDoubleValue() * 0.15;
      double d5 = event.getDoubleValue2() * 0.15;
      double d6 = 1.0 - Math.exp(-d3 / 25.0);
      this.doubleValue3 = this.doubleValue3 + (d4 / d3 * 1000.0 - this.doubleValue3) * d6;
      this.doubleValue4 = this.doubleValue4 + (d5 / d3 * 1000.0 - this.doubleValue4) * d6;
      if (d4 != 0.0 || d5 != 0.0 || Double.isNaN(this.doubleValue5)) {
         this.doubleValue5 = d2;
      }

      double yRot = mc.player.getYRot();
      double xRot = mc.player.getXRot();
      boolean flag2 = !mc.options.smoothCamera && !mc.player.isScoping();
      if (!flag2) {
         this.method6653(d2, false);
         this.method6657();
      } else {
         if (this.entry == null && d2 >= this.doubleValue17 && livingEntity != null && d2 - this.doubleValue5 >= d) {
            this.method6650(livingEntity, f, flag, yRot, xRot);
         }

         if (this.entry != null) {
            this.method6652(event, livingEntity, d2, d3);
         } else {
            if (enabled) {
               this.method6655(d2, d3, yRot + d4, xRot + d5, livingEntity);
            } else {
               this.method6657();
            }
         }
      }
   }

   private void method6650(LivingEntity livingEntity, float f, boolean flag, double yRot, double xRot) {
      if (!this.library.isEmpty()) {
         method6659(livingEntity, yRot, xRot, this.doubleValues);
         double doubleValue = this.doubleValues[0];
         double y = flag ? this.doubleValues[1] + this.doubleValue * this.doubleValues[4] : 0.0;
         double d = flag ? this.doubleValues[2] : Math.max(0.0, Math.abs(this.doubleValues[0]) - this.doubleValues[3]);
         double hypot = Math.hypot(doubleValue, y);
         if (!(d < 0.4) && !(Math.hypot(this.doubleValues[0], this.doubleValues[1]) > f) && !(hypot < 0.001)) {
            ReplayStrokes.Entry entry = this.method6651(doubleValue, y, hypot);
            if (entry == null) {
               this.intValue5++;
            } else {
               double d2 = entry.doubleValue3 * Math.signum(doubleValue);
               double d3 = entry.doubleValue4;
               double x = this.doubleValues[0] + d2 * this.doubleValues[3];
               double y2 = flag ? this.doubleValues[1] + d3 * this.doubleValues[4] : 0.0;
               double hypot2 = Math.hypot(x, y2);
               if (!(hypot2 < 0.001)) {
                  double d4 = hypot2 / entry.doubleValue;
                  double d5 = Math.pow(d4, 0.3) * (0.94 + ThreadLocalRandom.current().nextDouble() * 0.12);
                  this.entry = entry;
                  this.flag2 = flag;
                  this.doubleValue7 = 0.0;
                  this.doubleValue8 = d5;
                  this.doubleValue9 = d2;
                  this.doubleValue10 = d3;
                  this.doubleValue13 = x;
                  this.doubleValue14 = y2;
                  this.doubleValue11 = yRot;
                  this.doubleValue12 = xRot;
                  this.doubleValue15 = 0.0;
                  this.doubleValue16 = 0.0;
                  this.intValue2 = livingEntity.getId();
                  this.arrayDeque.addLast(entry);

                  while (this.arrayDeque.size() > 6) {
                     this.arrayDeque.removeFirst();
                  }

                  this.method6657();
                  this.intValue3++;
                  this.doubleValue18 = d4;
                  this.doubleValue19 = Math.toDegrees(Math.abs(method6660(Math.atan2(y, doubleValue), entry.doubleValue2)));
               }
            }
         }
      }
   }

   private ReplayStrokes.Entry method6651(double doubleValue, double y, double hypot) {
      double d = Math.atan2(y, doubleValue);
      ThreadLocalRandom current = ThreadLocalRandom.current();
      ReplayStrokes.Entry entry = null;
      double d2 = Double.MAX_VALUE;

      for (ReplayStrokes.Entry entry2 : this.library) {
         double d3 = hypot / entry2.doubleValue;
         if (!(d3 < 0.4) && !(d3 > 2.5)) {
            double d4 = Math.toDegrees(Math.abs(method6660(d, entry2.doubleValue2)));
            if (!(d4 > 60.0)) {
               double d5 = Math.log(d3) / Math.log(1.6);
               double d6 = d4 / 30.0;
               double d7 = d5 * d5 + d6 * d6 + current.nextDouble() * 0.6;
               if (this.arrayDeque.contains(entry2)) {
                  d7++;
               }

               if (d7 < d2) {
                  d2 = d7;
                  entry = entry2;
               }
            }
         }
      }

      return entry;
   }

   private void method6652(MouseRotationEvent event, LivingEntity livingEntity, double d, double d2) {
      if (livingEntity != null && livingEntity.getId() == this.intValue2 && livingEntity.isAlive()) {
         double hypot = this.flag2 ? Math.hypot(this.doubleValue3, this.doubleValue4) : Math.abs(this.doubleValue3);
         if (hypot >= 40.0) {
            this.intValue4++;
            this.method6653(d, true);
         } else {
            this.doubleValue7 = this.doubleValue7 + d2 / this.doubleValue8;
            method6659(livingEntity, this.doubleValue11, this.doubleValue12, this.doubleValues);
            double d3 = this.doubleValues[0] + this.doubleValue9 * this.doubleValues[3];
            double d4 = this.flag2 ? this.doubleValues[1] + this.doubleValue10 * this.doubleValues[4] : 0.0;
            double d5 = 1.0 - Math.exp(-d2 / 70.0);
            this.doubleValue13 = this.doubleValue13 + (d3 - this.doubleValue13) * d5;
            this.doubleValue14 = this.doubleValue14 + (d4 - this.doubleValue14) * d5;
            this.entry.method6669(this.doubleValue7, this.doubleValues2);
            double d6 = this.doubleValues2[0] * this.doubleValue13 - this.doubleValues2[1] * this.doubleValue14;
            double d7 = this.flag2 ? this.doubleValues2[0] * this.doubleValue14 + this.doubleValues2[1] * this.doubleValue13 : 0.0;
            double x = d6 - this.doubleValue15;
            double y = d7 - this.doubleValue16;
            double d8 = this.entry.doubleValue5 * Math.hypot(this.doubleValue13, this.doubleValue14) / this.doubleValue8 * 1.3 * d2;
            double hypot2 = Math.hypot(x, y);
            if (hypot2 > d8 && hypot2 > 0.0) {
               x *= d8 / hypot2;
               y *= d8 / hypot2;
            }

            double d9 = method6654();
            double d10 = Math.round(x / 0.15 / d9) * d9;
            double d11 = Math.round(y / 0.15 / d9) * d9;
            this.doubleValue15 += d10 * 0.15;
            this.doubleValue16 += d11 * 0.15;
            event.setDoubleValue(event.getDoubleValue() + d10);
            event.setDoubleValue2(event.getDoubleValue2() + d11);
            if (this.doubleValue7 >= this.entry.method6668()) {
               this.method6653(d, true);
            }
         }
      } else {
         this.method6653(d, false);
      }
   }

   private void method6653(double d, boolean flag) {
      if (this.entry != null) {
         this.entry = null;
         if (flag) {
            this.doubleValue17 = d + 90.0 + ThreadLocalRandom.current().nextDouble() * 110.0;
         }

         this.method6657();
      }
   }

   private static double method6654() {
      double d = mc.options.sensitivity().get() * 0.6 + 0.2;
      return d * d * d * 8.0;
   }

   private void method6655(double d, double d2, double d3, double d4, LivingEntity livingEntity) {
      double hypot = Math.hypot(this.doubleValue3, this.doubleValue4);
      double[] doubles = new double[]{d, d3, d4};
      if (this.flag) {
         this.items.add(doubles);
         if (d - this.items.getFirst()[0] > 900.0) {
            this.method6657();
         } else {
            if (hypot < 10.0) {
               this.doubleValue6 += d2;
               if (this.doubleValue6 >= 60.0) {
                  this.method6656(livingEntity);
                  this.method6657();
               }
            } else {
               this.doubleValue6 = 0.0;
            }
         }
      } else {
         this.arrayDeque2.addLast(doubles);

         while (this.arrayDeque2.size() > 3) {
            this.arrayDeque2.removeFirst();
         }

         if (hypot >= 40.0) {
            this.flag = true;
            this.items.clear();
            this.items.addAll(this.arrayDeque2);
            this.arrayDeque2.clear();
            this.doubleValue6 = 0.0;
         }
      }
   }

   private void method6656(LivingEntity livingEntity) {
      int size = this.items.size();
      if (livingEntity != null && size >= 4) {
         double[] doubles = this.items.getFirst();
         double[] doubles2 = this.items.get(size - 1);
         double d = doubles2[0] - doubles[0];
         double x = doubles2[1] - doubles[1];
         double y = doubles2[2] - doubles[2];
         double hypot = Math.hypot(x, y);
         if (!(d < 50.0) && !(hypot < 1.0)) {
            method6659(livingEntity, doubles[1], doubles[2], this.doubleValues);
            double doubleValue = this.doubleValues[2];
            method6659(livingEntity, doubles2[1], doubles2[2], this.doubleValues);
            double doubleValue2 = this.doubleValues[2];
            boolean flag = doubleValue2 <= 2.5 || doubleValue2 <= 0.3 * doubleValue;
            boolean flag2 = doubleValue - doubleValue2 >= 0.5 * hypot;
            if (flag && flag2) {
               double d2 = -this.doubleValues[0] / Math.max(0.001, this.doubleValues[3]);
               double d3 = -this.doubleValues[1] / Math.max(0.001, this.doubleValues[4]);
               if (!(Math.abs(d2) > 3.0) && !(Math.abs(d3) > 3.0)) {
                  double d4 = d2 * (x >= 0.0 ? 1.0 : -1.0);
                  int n = Math.min(size, 160);
                  float[] floatValues = new float[n];
                  float[] floatValues2 = new float[n];
                  float[] floatValues3 = new float[n];
                  double d5 = x / hypot;
                  double d6 = y / hypot;

                  for (int i = 0; i < n; i++) {
                     int n2 = n == size ? i : (int)Math.round((double)i * (size - 1) / (n - 1));
                     double[] doubles3 = this.items.get(n2);
                     double d7 = doubles3[1] - doubles[1];
                     double d8 = doubles3[2] - doubles[2];
                     floatValues[i] = (float)(doubles3[0] - doubles[0]);
                     floatValues2[i] = (float)((d7 * d5 + d8 * d6) / hypot);
                     floatValues3[i] = (float)((-d7 * d6 + d8 * d5) / hypot);
                  }

                  this.library.add(new ReplayStrokes.Entry(hypot, Math.atan2(y, x), d4, d3, floatValues, floatValues2, floatValues3));
                  if (this.library.size() > 600) {
                     this.library.removeFirst();
                  }

                  this.method6658();
                  this.intValue6++;
                  if (++this.intValue >= 8) {
                     this.save();
                  }
               }
            }
         }
      }
   }

   private void method6657() {
      this.flag = false;
      this.items.clear();
      this.arrayDeque2.clear();
      this.doubleValue6 = 0.0;
   }

   private void method6658() {
      double d = 0.0;

      for (ReplayStrokes.Entry entry : this.library) {
         d += entry.doubleValue4;
      }

      this.doubleValue = this.library.isEmpty() ? 0.0 : d / this.library.size();
   }

   private static void method6659(LivingEntity livingEntity, double yRot, double xRot, double[] doubleValues) {
      float gameTimeDeltaPartialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
      Vec3 eyePosition = mc.player.getEyePosition(gameTimeDeltaPartialTick);
      Vec3 position = livingEntity.getPosition(gameTimeDeltaPartialTick);
      AABB move = livingEntity.getBoundingBox().move(position.x - livingEntity.getX(), position.y - livingEntity.getY(), position.z - livingEntity.getZ());
      Vec3 center = move.getCenter();
      Rotation rotation = RotationUtils.method6912(center.subtract(eyePosition));
      double d = Mth.wrapDegrees(rotation.getFloatValue() - yRot);
      double d2 = rotation.getFloatValue2() - xRot;
      double d3 = Math.max(0.3, eyePosition.distanceTo(center));
      double d4 = Math.toDegrees(Math.atan(move.getXsize() * 0.5 / d3));
      double d5 = Math.toDegrees(Math.atan(move.getYsize() * 0.5 / d3));
      doubleValues[0] = d;
      doubleValues[1] = d2;
      doubleValues[2] = Math.hypot(Math.max(0.0, Math.abs(d) - d4), Math.max(0.0, Math.abs(d2) - d5));
      doubleValues[3] = d4;
      doubleValues[4] = d5;
   }

   private static double method6660(double d, double d2) {
      double d3 = d - d2;

      while (d3 > Math.PI) {
         d3 -= Math.PI * 2;
      }

      while (d3 < -Math.PI) {
         d3 += Math.PI * 2;
      }

      return d3;
   }

   private static Path method6661() {
      return Zenith.file.toPath().resolve("assist").resolve("replay_strokes.json");
   }

   private void method6662() {
      if (this.completableFuture == null) {
         this.completableFuture = CompletableFuture.supplyAsync(ReplayStrokes::method6663);
      } else {
         if (this.completableFuture.isDone() && !this.completableFuture.isCompletedExceptionally()) {
            var now = this.completableFuture.getNow(null);
            if (now != null) {
               now.addAll(this.library);

               while (now.size() > 600) {
                  now.removeFirst();
               }

               this.library = now;
               this.loaded = true;
               this.method6658();
               this.completableFuture = CompletableFuture.completedFuture(null);
            }
         }
      }
   }

   private static List<ReplayStrokes.Entry> method6663() {
      ArrayList<ReplayStrokes.Entry> entries = new ArrayList<>();
      Path path = method6661();
      if (!Files.isRegularFile(path)) {
         return entries;
      } else {
         try {
            label73: {
               ArrayList arrayList;
               try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                  JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
                  if (jsonObject.get("format").getAsInt() == 2) {
                     for (JsonElement json : jsonObject.getAsJsonArray("strokes")) {
                        JsonObject jsonObject2 = json.getAsJsonObject();
                        float[] floatValues = method6664(jsonObject2.getAsJsonArray("t"));
                        float[] floatValues2 = method6664(jsonObject2.getAsJsonArray("u"));
                        float[] floatValues3 = method6664(jsonObject2.getAsJsonArray("w"));
                        if (floatValues.length >= 2 && floatValues.length == floatValues2.length && floatValues.length == floatValues3.length) {
                           entries.add(
                              new ReplayStrokes.Entry(
                                 jsonObject2.get("amp").getAsDouble(),
                                 jsonObject2.get("dir").getAsDouble(),
                                 jsonObject2.get("fx").getAsDouble(),
                                 jsonObject2.get("fy").getAsDouble(),
                                 floatValues,
                                 floatValues2,
                                 floatValues3
                              )
                           );
                        }
                     }
                     break label73;
                  }

                  arrayList = entries;
               }

               return arrayList;
            }
         } catch (Exception e) {
            System.err.println("[ReplayAssist] не прочитан " + path + ": " + e);
            entries.clear();
         }

         return entries;
      }
   }

   private static float[] method6664(JsonArray jsonArray) {
      float[] floats = new float[jsonArray.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   public void save() {
      if (this.loaded) {
         this.intValue = 0;
         var entries = new ArrayList<>(this.library);
         CompletableFuture.runAsync(() -> method6665(entries));
      }
   }

   private static synchronized void method6665(List<ReplayStrokes.Entry> entries) {
      Path target = method6661();

      try {
         Files.createDirectories(target.getParent());
         Path sibling = target.resolveSibling("replay_strokes.json.tmp");

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(sibling, StandardCharsets.UTF_8)) {
            JsonWriter jsonWriter = new JsonWriter(bufferedWriter);

            try {
               jsonWriter.beginObject();
               jsonWriter.name("format").value(2L);
               jsonWriter.name("strokes").beginArray();

               for (ReplayStrokes.Entry entry : entries) {
                  jsonWriter.beginObject();
                  jsonWriter.name("amp").value(round(entry.doubleValue));
                  jsonWriter.name("dir").value(round(entry.doubleValue2));
                  jsonWriter.name("fx").value(round(entry.doubleValue3));
                  jsonWriter.name("fy").value(round(entry.doubleValue4));
                  method6666(jsonWriter, "t", entry.floatValues);
                  method6666(jsonWriter, "u", entry.floatValues2);
                  method6666(jsonWriter, "w", entry.floatValues3);
                  jsonWriter.endObject();
               }

               jsonWriter.endArray();
               jsonWriter.endObject();
            } catch (Throwable e) {
               try {
                  jsonWriter.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }

               throw e;
            }

            jsonWriter.close();
         }

         Files.move(sibling, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception e) {
         System.err.println("[ReplayAssist] не записан " + target + ": " + e);
      }
   }

   private static void method6666(JsonWriter jsonWriter, String name, float[] floatValues) throws IOException {
      jsonWriter.name(name).beginArray();

      for (float floatValue : floatValues) {
         jsonWriter.value(round(floatValue));
      }

      jsonWriter.endArray();
   }

   private static double round(double doubleValue) {
      return Math.round(doubleValue * 10000.0) / 10000.0;
   }

   public int size() {
      return this.library.size();
   }

   public void clear() {
      this.library = new ArrayList<>();
      this.arrayDeque.clear();
      this.doubleValue = 0.0;
      this.completableFuture = CompletableFuture.completedFuture(null);
      this.loaded = true;
      this.entry = null;
      this.method6657();
      this.save();
   }

   public void reset() {
      this.entry = null;
      this.doubleValue17 = 0.0;
      this.doubleValue2 = Double.NaN;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.doubleValue5 = Double.NaN;
      this.method6657();
   }

   public void shutdown() {
      this.reset();
      if (this.intValue > 0) {
         this.save();
      }
   }

   public String method6667() {
      String str = String.format(
         Locale.ROOT,
         "replay lib=%d rec+%d played=%d yielded=%d nomatch=%d scale=%.2f ang=%.0f fy=%.2f%s",
         this.library.size(),
         this.intValue6,
         this.intValue3,
         this.intValue4,
         this.intValue5,
         this.doubleValue18,
         this.doubleValue19,
         this.doubleValue,
         this.entry != null ? " [playing]" : ""
      );
      this.intValue6 = 0;
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.intValue5 = 0;
      return str;
   }

   private static final class Entry {
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final double doubleValue4;
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float[] floatValues3;
      public final double doubleValue5;

      public Entry(double doubleValue, double d, double d2, double d3, float[] floatValues, float[] floatValues2, float[] floatValues3) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.doubleValue4 = d3;
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         double d4 = 0.0;

         for (int i = 1; i < floatValues.length; i++) {
            double d5 = floatValues[i] - floatValues[i - 1];
            if (d5 > 0.0) {
               d4 = Math.max(d4, Math.hypot(floatValues2[i] - floatValues2[i - 1], floatValues3[i] - floatValues3[i - 1]) / d5);
            }
         }

         this.doubleValue5 = d4;
      }

      public double method6668() {
         return this.floatValues[this.floatValues.length - 1];
      }

      public void method6669(double d, double[] doubleValues2) {
         int length = this.floatValues.length;
         if (d <= this.floatValues[0]) {
            doubleValues2[0] = this.floatValues2[0];
            doubleValues2[1] = this.floatValues3[0];
         } else if (d >= this.floatValues[length - 1]) {
            doubleValues2[0] = this.floatValues2[length - 1];
            doubleValues2[1] = this.floatValues3[length - 1];
         } else {
            int n = 0;
            int n2 = length - 1;

            while (n2 - n > 1) {
               int n3 = n + n2 >>> 1;
               if (this.floatValues[n3] <= d) {
                  n = n3;
               } else {
                  n2 = n3;
               }
            }

            double d2 = this.floatValues[n2] - this.floatValues[n];
            double d3 = d2 > 0.0 ? (d - this.floatValues[n]) / d2 : 0.0;
            doubleValues2[0] = this.floatValues2[n] + (this.floatValues2[n2] - this.floatValues2[n]) * d3;
            doubleValues2[1] = this.floatValues3[n] + (this.floatValues3[n2] - this.floatValues3[n]) * d3;
         }
      }
   }
}

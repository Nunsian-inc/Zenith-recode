package org.zenith.base.rotation.mods;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.Mth;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.utility.misc.RotationContext;

public class MotorClaude extends RotationContext {
   public static final String text = "motor_claude.json";
   public static final String text2 = "motor_imitation.json";
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final Map<String, MotorClaudeNet> items = new HashMap<>();
   private static final Map<String, String> items2 = new HashMap<>();
   private final String text3;
   private final Random random = new Random();
   private MotorClaudeNet.Entry entry;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3 = 4.0;
   private static final double CONST_doubleValue = 0.4;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private Rotation rotation;
   private double doubleValue4 = Double.NaN;

   public MotorClaude() {
      this("motor_claude.json");
   }

   public MotorClaude(String str) {
      this.text3 = str;
   }

   private MotorClaudeNet method3420() {
      synchronized (items) {
         if (items2.containsKey(this.text3)) {
            return items.get(this.text3);
         } else {
            MotorClaudeNet motorClaudeNet = null;

            String x;
            try (InputStream inputStream = method3421(this.text3)) {
               motorClaudeNet = MotorClaudeNet.method3580(inputStream);
               x = this.text3 + " загружен. " + motorClaudeNet.text2;
            } catch (Exception e) {
               x = this.text3 + " НЕ загружен: " + e.getMessage();
            }

            items.put(this.text3, motorClaudeNet);
            items2.put(this.text3, x);
            System.out.println(x);
            if (mc != null && mc.player != null) {
               String status = x;
               mc.execute(() -> ChatLogger.method2019(status));
            }

            return motorClaudeNet;
         }
      }
   }

   private static InputStream method3421(String first) throws IOException {
      for (Path path : List.of(Path.of(first), Path.of("models", first), Path.of("..", "models", first))) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = MotorClaude.class.getClassLoader().getResourceAsStream("models/" + first);
      if (resourceAsStream == null) {
         throw new IOException(first + " не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   public String method3422() {
      synchronized (items) {
         return items2.getOrDefault(this.text3, "не загружался");
      }
   }

   public boolean method3423() {
      return this.method3420() != null;
   }

   public void setDoubleValue4(double d) {
      this.doubleValue4 = d;
   }

   public void setDoubleValue3(double d) {
      this.doubleValue3 = d;
   }

   public void reset() {
      this.entry = null;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private static double method3424() {
      double d = mc.options.sensitivity().get() * 0.6F + 0.2F;
      return d * d * d * 8.0 * 0.15;
   }

   public Rotation method3425(Rotation rotation) {
      Rotation rotation2 = zenith.getRotationManager().getEffectiveRotation();
      MotorClaudeNet motorClaudeNet = this.method3420();
      if (motorClaudeNet != null && mc.player != null && rotation != null) {
         int tickCount = mc.player.tickCount;
         if (tickCount == this.intValue2 && this.rotation != null) {
            return this.rotation;
         } else {
            if (this.intValue != Integer.MIN_VALUE && tickCount != this.intValue + 1) {
               this.reset();
            }

            this.intValue = tickCount;
            double d = method3424();
            double log = Math.log(Double.isNaN(this.doubleValue4) ? motorClaudeNet.getDoubleValue16() : this.doubleValue4);
            if (this.entry == null) {
               this.entry = motorClaudeNet.method3563();
               this.doubleValue = rotation2.getFloatValue();
               this.doubleValue2 = rotation2.getFloatValue2();
               double[] doubles = new double[]{0.0, 0.0};

               for (int i = 0; i < motorClaudeNet.getIntValue8(); i++) {
                  motorClaudeNet.method3579(this.entry, doubles, doubles, rotation2.getFloatValue2(), d, log, new double[motorClaudeNet.getIntValue9()], doubles);
               }
            }

            double doubleValue = this.doubleValue;
            double d2 = this.doubleValue2;
            if (this.doubleValue3 > 0.0) {
               double d3 = Mth.wrapDegrees(rotation.getFloatValue() - this.doubleValue);
               double d4 = rotation.getFloatValue2() - this.doubleValue2;
               double d5 = Math.sqrt(d3 * d3 + d4 * d4);
               double d6 = d5 < 1.0E-9 ? 0.0 : Math.min(1.0, Math.max(0.4, d5 / this.doubleValue3) / d5);
               this.doubleValue += d3 * d6;
               this.doubleValue2 += d4 * d6;
            } else {
               this.doubleValue = rotation.getFloatValue();
               this.doubleValue2 = rotation.getFloatValue2();
            }

            double[] doubles = new double[]{Mth.wrapDegrees(this.doubleValue - rotation2.getFloatValue()), this.doubleValue2 - rotation2.getFloatValue2()};
            double[] doubles2 = new double[]{Mth.wrapDegrees(this.doubleValue - doubleValue), this.doubleValue2 - d2};
            double[] doubles3 = new double[motorClaudeNet.getIntValue9()];

            for (int i = 0; i < doubles3.length; i++) {
               doubles3[i] = this.random.nextDouble();
            }

            MotorClaudeNet.Entry2 entry2 = motorClaudeNet.method3579(this.entry, doubles, doubles2, rotation2.getFloatValue2(), d, log, doubles3, null);
            Rotation rotation3 = rotation2.add((float)(entry2.doubleValue * d), (float)(entry2.doubleValue2 * d));
            this.intValue2 = tickCount;
            this.rotation = rotation3;
            return rotation3;
         }
      } else {
         this.reset();
         return rotation2;
      }
   }
}

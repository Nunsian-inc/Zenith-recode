package org.zenith.base.rotation.mods;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.utility.misc.RotationContext;

public class MotorHand2 extends RotationContext {
   private static final String text = "motor_hand.json";
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static MotorHand motorHand;
   private static boolean flag;
   private static String status = "не загружался";
   private final Random random = new Random();
   private MotorHand.Entry entry;
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private Rotation rotation;

   private static synchronized MotorHand method3426() {
      if (flag) {
         return motorHand;
      } else {
         flag = true;

         try (InputStream inputStream = method3427()) {
            motorHand = MotorHand.method3593(inputStream);
            status = "MotorClaude (рука без модели) загружен. " + motorHand.text2;
         } catch (Exception e) {
            motorHand = null;
            status = "MotorClaude НЕ загружен: " + e.getMessage();
         }

         System.out.println(status);
         if (mc != null && mc.player != null) {
            String str = status;
            mc.execute(() -> ChatLogger.method2019(str));
         }

         return motorHand;
      }
   }

   private static InputStream method3427() throws IOException {
      for (Path path : List.of(Path.of("motor_hand.json"), Path.of("models", "motor_hand.json"), Path.of("..", "models", "motor_hand.json"))) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = MotorHand2.class.getClassLoader().getResourceAsStream("models/motor_hand.json");
      if (resourceAsStream == null) {
         throw new IOException("motor_hand.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   public static String getStatus() {
      return status;
   }

   public boolean method3428() {
      return method3426() != null;
   }

   public void reset() {
      this.entry = null;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private static double method3429() {
      double d = mc.options.sensitivity().get() * 0.6F + 0.2F;
      return d * d * d * 8.0 * 0.15;
   }

   public Rotation method3430(Rotation rotation) {
      Rotation rotation2 = zenith.getRotationManager().getEffectiveRotation();
      MotorHand motorHand = method3426();
      if (motorHand != null && mc.player != null && rotation != null) {
         int tickCount = mc.player.tickCount;
         if (tickCount == this.intValue2 && this.rotation != null) {
            return this.rotation;
         } else {
            if (this.intValue != Integer.MIN_VALUE && tickCount != this.intValue + 1) {
               this.reset();
            }

            this.intValue = tickCount;
            if (this.entry == null) {
               this.entry = motorHand.method3588();
            }

            double d = method3429();
            double[] doubles = motorHand.method3592(
               this.entry,
               new double[]{rotation.getFloatValue(), rotation.getFloatValue2()},
               new double[]{rotation2.getFloatValue(), rotation2.getFloatValue2()},
               d,
               this.random.nextDouble()
            );
            Rotation rotation3 = rotation2.add((float)(doubles[0] * d), (float)(doubles[1] * d));
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

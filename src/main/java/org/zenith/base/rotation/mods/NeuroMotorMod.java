package org.zenith.base.rotation.mods;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public final class NeuroMotorMod extends Util22 {
   private static final String text = "motor_v10.json";
   private static final String text2 = "models/motor_v10.json";
   private static final List<Path> items = List.of(Path.of("motor_v10.json"), Path.of("models", "motor_v10.json"), Path.of("..", "models", "motor_v10.json"));
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static volatile MotorNet motorNet;
   private static volatile boolean flag;
   private static volatile CompletableFuture<?> completableFuture;
   private static volatile String status = "не загружался";
   private final Random random = new Random();
   private double[][] doubleValues;
   private final double[] doubleValues2 = new double[2];
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private Rotation rotation;

   private static synchronized MotorNet method3875() {
      if (flag) {
         return motorNet;
      } else {
         MotorNet motorNet2 = null;

         try (InputStream inputStream = method3877()) {
            motorNet2 = MotorNet.method3616(inputStream);
            status = "NeuroMotor загружен. " + motorNet2.text2;
         } catch (Exception e) {
            motorNet2 = null;
            status = "NeuroMotor НЕ загружен: " + e.getMessage();
         } finally {
            motorNet = motorNet2;
            flag = true;
         }

         System.out.println(status);
         if (mc != null && mc.player != null) {
            String str = status;
            mc.execute(() -> ChatLogger.method2019(str));
         }

         return motorNet2;
      }
   }

   private static boolean method3876() {
      if (flag) {
         return true;
      } else {
         if (completableFuture == null) {
            synchronized (NeuroMotorMod.class) {
               if (completableFuture == null && !flag) {
                  completableFuture = CompletableFuture.runAsync(NeuroMotorMod::method3875);
               }
            }
         }

         return flag;
      }
   }

   private static InputStream method3877() throws IOException {
      for (Path path : items) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = NeuroMotorMod.class.getClassLoader().getResourceAsStream("models/motor_v10.json");
      if (resourceAsStream == null) {
         throw new IOException("motor_v10.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   public static String getStatus4() {
      return status;
   }

   public boolean method3878() {
      return method3875() != null;
   }

   public void reset() {
      this.doubleValues = null;
      this.doubleValues2[0] = 0.0;
      this.doubleValues2[1] = 0.0;
      this.intValue = Integer.MIN_VALUE;
      this.intValue2 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private static double method3879() {
      double d = mc.options.sensitivity().get() * 0.6F + 0.2F;
      return d * d * d * 8.0 * 0.15;
   }

   public Rotation method3880() {
      Rotation rotation = rotationManager.method7844();
      if (!method3876()) {
         this.reset();
         return rotation;
      } else {
         MotorNet motorNet2 = motorNet;
         if (motorNet2 == null) {
            this.reset();
            return mc.player == null ? rotation : new Rotation(mc.player.getYRot(), mc.player.getXRot(), true);
         } else {
            LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
            if (mc.player != null && livingEntity != null && livingEntity.isAlive()) {
               int tickCount = mc.player.tickCount;
               if (tickCount == this.intValue2 && this.rotation != null) {
                  return this.rotation;
               } else {
                  if (this.intValue != Integer.MIN_VALUE && tickCount != this.intValue + 1) {
                     this.reset();
                  }

                  this.intValue = tickCount;
                  if (this.doubleValues == null) {
                     this.doubleValues = new double[motorNet2.intValue][2];
                  }

                  AABB boundingBox = livingEntity.getBoundingBox();
                  Vec3 eyePosition = mc.player.getEyePosition();
                  Input keyPresses = mc.player.input.keyPresses;
                  double[] doubles = new double[]{
                     keyPresses.forward() ? 1.0 : 0.0, keyPresses.backward() ? 1.0 : 0.0, keyPresses.left() ? 1.0 : 0.0, keyPresses.right() ? 1.0 : 0.0, keyPresses.jump() ? 1.0 : 0.0
                  };
                  double[] original = MotorNet.method3612(
                     rotation.getFloatValue(),
                     rotation.getFloatValue2(),
                     new double[]{eyePosition.x, eyePosition.y, eyePosition.z},
                     new double[]{boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ},
                     doubles,
                     mc.player.onGround() ? 1.0 : 0.0,
                     mc.player.isSprinting() ? 1.0 : 0.0,
                     mc.player.getAttackStrengthScale(0.0F)
                  );
                  Vec3 deltaMovement = mc.player.getDeltaMovement();
                  original = motorNet2.method3613(original, rotation.getFloatValue(), new double[]{deltaMovement.x, deltaMovement.y, deltaMovement.z});
                  double[] doubles2 = new double[motorNet2.intValue8];

                  for (int i = 0; i < doubles2.length; i++) {
                     doubles2[i] = this.random.nextDouble();
                  }

                  MotorNet.Entry entry = motorNet2.method3611(this.doubleValues, original, this.doubleValues2, doubles2);
                  double d = method3879();
                  double floatValue = entry.doubleValue * d;
                  double random = entry.doubleValue2 * d;

                  for (int i = 0; i + 1 < this.doubleValues.length; i++) {
                     this.doubleValues[i] = this.doubleValues[i + 1];
                  }

                  this.doubleValues[this.doubleValues.length - 1] = new double[]{floatValue, random};
                  this.doubleValues2[0] = entry.doubleValue;
                  this.doubleValues2[1] = entry.doubleValue2;
                  Rotation rotation2 = rotation.method3140((float)floatValue, (float)random);
                  this.intValue2 = tickCount;
                  this.rotation = rotation2;
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
      }
   }
}

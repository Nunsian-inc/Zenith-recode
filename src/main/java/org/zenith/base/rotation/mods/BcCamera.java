package org.zenith.base.rotation.mods;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Options;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public class BcCamera extends Util22 {
   private final String text;
   private final int intValue;
   private final double doubleValue;
   private volatile BcCameraNet bcCameraNet;
   private CompletableFuture<Void> completableFuture;
   private volatile String status = "не загружался";
   private static volatile int intValue2;
   private final Random random = new Random();
   private BcCameraNet.Entry2 entry2;
   private long longValue;
   private long longValue2 = Long.MIN_VALUE;
   private long longValue3 = Long.MIN_VALUE;
   private Rotation rotation;
   private Object obj;
   private double doubleValue2;
   private double doubleValue3;
   private boolean flag;
   private float floatValue = 1.0F;
   private int intValue3;
   private static final int CONST_intValue = 72000;
   private BufferedWriter bufferedWriter;
   private int intValue4;
   private int intValue5;
   private volatile KeyFixNet keyFixNet;
   private double[] doubleValues;
   private long longValue4 = Long.MIN_VALUE;
   private int intValue6 = -1;

   public BcCamera() {
      this("bc_camera.json", 1);
   }

   public BcCamera(String text, int intValue) {
      this(text, intValue, 1.0);
   }

   public BcCamera(String text, int intValue, double doubleValue) {
      this.text = text;
      this.intValue = intValue;
      this.doubleValue = doubleValue;
      EventManager.register(this);
   }

   private String name() {
      return this.intValue == 1 ? "NeuroBC" : (this.intValue == 4 ? "TestNerv" : "NeuroBC-" + this.intValue);
   }

   public static void method3546() {
      intValue2++;
   }

   public String getStatus2() {
      return this.status;
   }

   private synchronized BcCameraNet method3547() {
      if (this.bcCameraNet != null) {
         return this.bcCameraNet;
      } else {
         if (this.completableFuture == null) {
            this.completableFuture = CompletableFuture.runAsync(() -> {
               try (InputStream inputStream = this.method3549()) {
                  BcCameraNet bcCameraNet = BcCameraNet.method3387(inputStream);
                  bcCameraNet.doubleValue5 = this.doubleValue;
                  this.status = this.name() + " загружен (" + this.text + ", температура ненулевых ходов " + this.doubleValue + "). " + bcCameraNet.text2;
                  String text = this.method3548();

                  try (InputStream inputStream2 = this.method3550(text)) {
                     KeyFixNet keyFixNet = KeyFixNet.method3562(inputStream2);
                     this.keyFixNet = keyFixNet;
                     this.status = this.status + " | " + keyFixNet.text2;
                  } catch (NoSuchFileException | FileNotFoundException e) {
                     this.status = this.status + " | поправщика клавиш нет (" + text + ")";
                  } catch (Exception e) {
                     this.status = this.status + " | поправщик клавиш НЕ загружен (" + text + "): " + e.getMessage();
                  }

                  this.bcCameraNet = bcCameraNet;
               } catch (Exception e) {
                  this.status = this.name() + " НЕ загружен (" + this.text + "): " + e.getMessage();
               }

               System.out.println(this.status);
               if (mc != null) {
                  String status = this.status;
                  mc.execute(() -> {
                     if (mc.player != null) {
                        ChatLogger.method2019(status);
                     }
                  });
               }
            });
         }

         return null;
      }
   }

   private String method3548() {
      return this.intValue == 1 ? "bc_keyfix.json" : "bc_keyfix_" + this.intValue + ".json";
   }

   private InputStream method3549() throws IOException {
      return this.method3550(this.text);
   }

   private InputStream method3550(String text) throws IOException {
      for (Path path : List.of(Path.of(text), Path.of("models", text), Path.of("..", "models", text))) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = BcCamera.class.getClassLoader().getResourceAsStream("models/" + text);
      if (resourceAsStream == null) {
         throw new FileNotFoundException(text + " не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   public boolean method3551() {
      return this.bcCameraNet != null;
   }

   public void reset() {
      this.entry2 = null;
      this.rotation = null;
      this.doubleValues = null;
      this.flag = false;
      this.obj = null;
   }

   private static double method3552() {
      double d = mc.options.sensitivity().get() * 0.6F + 0.2F;
      return d * d * d * 8.0 * 0.15;
   }

   @EventTarget
   public void onTick(TickEvent event) {
      this.longValue++;
      if (mc.player != null && mc.level != null && AuraModule.INSTANCE.isEnabled() && AuraModule.INSTANCE.method6359(this.intValue)) {
         BcCameraNet bcCameraNet = this.method3547();
         if (bcCameraNet == null) {
            this.reset();
         } else {
            if (this.intValue == 4 && bcCameraNet.doubleValue2 >= 0.0) {
               bcCameraNet.doubleValue2 = AuraModule.INSTANCE.method6361();
            }

            if (this.entry2 == null || mc.level != this.obj) {
               this.entry2 = bcCameraNet.method3355();
               this.flag = false;
            }

            this.obj = mc.level;
            Rotation rotation = zenith.getRotationManager().method7844();
            double floatValue = rotation.getFloatValue();
            double d = rotation.getFloatValue2();
            BcCameraNet.Entry4 entry4 = new BcCameraNet.Entry4();
            entry4.doubleValue = mc.player.getX();
            entry4.doubleValue2 = mc.player.getY();
            entry4.doubleValue3 = mc.player.getZ();
            Input keyPresses = mc.player.input.keyPresses;
            entry4.intValue = (keyPresses.forward() ? 1 : 0)
               | (keyPresses.backward() ? 2 : 0)
               | (keyPresses.left() ? 4 : 0)
               | (keyPresses.right() ? 8 : 0)
               | (keyPresses.jump() ? 16 : 0)
               | (keyPresses.shift() ? 32 : 0)
               | (keyPresses.sprint() ? 64 : 0);
            entry4.flag = mc.player.onGround();
            entry4.flag2 = mc.player.horizontalCollision;
            int n = intValue2;
            float attackStrengthScale = mc.player.getAttackStrengthScale(0.0F);
            entry4.flag3 = n != this.intValue3 || attackStrengthScale < this.floatValue - 1.0E-4F;
            this.intValue3 = n;
            this.floatValue = attackStrengthScale;
            LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
            if (livingEntity != null && livingEntity.isAlive()) {
               entry4.doubleValue4 = livingEntity.getX();
               entry4.doubleValue5 = livingEntity.getY();
               entry4.doubleValue6 = livingEntity.getZ();
               entry4.intValue2 = livingEntity.getId();
               entry4.intValue3 = 1;
               entry4.flag4 = livingEntity.isShiftKeyDown();
            }

            entry4.doubleValue7 = floatValue;
            entry4.doubleValue8 = d;
            if (this.flag) {
               entry4.doubleValue9 = floatValue - this.doubleValue2;
               entry4.doubleValue10 = d - this.doubleValue3;
               entry4.flag5 = Math.abs(entry4.doubleValue9) <= 180.0;
            } else {
               entry4.flag5 = false;
            }

            this.doubleValue2 = floatValue;
            this.doubleValue3 = d;
            this.flag = true;
            this.entry2.method3394(entry4);
            double d2 = method3552();
            if (this.longValue3 != this.longValue - 1L) {
               this.entry2.method3396();
            }

            Options options = mc.options;
            int n2 = (options.keyUp.isDown() ? 1 : 0)
               | (options.keyDown.isDown() ? 2 : 0)
               | (options.keyLeft.isDown() ? 4 : 0)
               | (options.keyRight.isDown() ? 8 : 0)
               | (options.keyJump.isDown() ? 16 : 0)
               | (options.keyShift.isDown() ? 32 : 0)
               | (options.keySprint.isDown() ? 64 : 0);
            double[] doubleValues = bcCameraNet.method3365(this.entry2, d2);
            this.doubleValues = doubleValues;
            this.longValue4 = this.longValue;
            double[] doubles = bcCameraNet.method3379(doubleValues, n2);
            double[] doubles2;
            if (bcCameraNet.method3381()) {
               doubles2 = bcCameraNet.method3383(this.entry2, doubleValues, doubles, d2, this.random);
            } else if (bcCameraNet.entry3 != null) {
               doubles2 = bcCameraNet.method3350(this.entry2, doubleValues, doubles, d2, this.random);
            } else if (bcCameraNet.flag3) {
               doubles2 = bcCameraNet.method3348(this.entry2, doubleValues, doubles, d2, this.random);
            } else {
               doubles2 = bcCameraNet.method3380(this.entry2, doubles, d2, this.random);
            }

            doubles2 = bcCameraNet.method3352(doubleValues, doubles2[0], doubles2[1]);
            double[] doubles3 = BcCameraNet.method3384(this.entry2, doubles2[0], doubles2[1], d2, d);
            this.rotation = rotation.method3140((float)doubles3[0], (float)doubles3[1]);
            this.method3553(entry4, doubles, doubles2, doubles3, d2, attackStrengthScale);
            this.longValue2 = this.longValue;
            this.intValue5 = 0;
         }
      } else {
         this.reset();
      }
   }

   @EventTarget(4)
   public void onMoveInput(MoveInputEvent event) {
      KeyFixNet keyFixNet = this.keyFixNet;
      double[] doubleValues = this.doubleValues;
      this.intValue6 = -1;
      if (keyFixNet != null && doubleValues != null && this.longValue4 == this.longValue && event.getPlayerInput() != null && keyFixNet.method3559(doubleValues)) {
         Input playerInput = event.getPlayerInput();
         int n = (playerInput.forward() ? 1 : 0)
            | (playerInput.backward() ? 2 : 0)
            | (playerInput.left() ? 4 : 0)
            | (playerInput.right() ? 8 : 0)
            | (playerInput.jump() ? 16 : 0)
            | (playerInput.shift() ? 32 : 0)
            | (playerInput.sprint() ? 64 : 0);
         int n2 = keyFixNet.method3560(doubleValues, n, this.random);
         event.setPlayerInput(
            new Input((n2 & 1) != 0, (n2 & 2) != 0, (n2 & 4) != 0, (n2 & 8) != 0, (n2 & 16) != 0, playerInput.shift(), (n2 & 64) != 0)
         );
         this.intValue6 = n;
      }
   }

   private void method3553(BcCameraNet.Entry4 entry4, double[] doubles, double[] doubles2, double[] doubles3, double d, float attackStrengthScale) {
      if (this.intValue4 < 72000) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of(this.intValue == 1 ? "bc_camera_log.csv" : "bc_camera" + this.intValue + "_log.csv"),
                  StandardCharsets.UTF_8,
                  StandardOpenOption.CREATE,
                  StandardOpenOption.APPEND
               );
               StringBuilder builder = new StringBuilder(
                  "tick,age,applied_prev,process_calls_prev,grid,cooldown,x,y,z,keys,on_ground,hcoll,attack,tgt_id,tx,ty,tz,tgt_sneak,yaw,pitch,my,mp,mv_ok,act_yaw,act_pitch,q_yaw,q_pitch,cam_yaw,cam_pitch,model,ukeys_prev"
               );

               for (int i = 0; i < doubles.length; i++) {
                  builder.append(",f").append(i);
               }

               this.bufferedWriter.write(builder.append('\n').toString());
            }

            StringBuilder builder = new StringBuilder(512);
            builder.append(this.longValue)
               .append(',')
               .append(mc.player.tickCount)
               .append(',')
               .append(this.longValue3 == this.longValue - 1L ? 1 : 0)
               .append(',')
               .append(this.intValue5)
               .append(',')
               .append(d)
               .append(',')
               .append(attackStrengthScale)
               .append(',')
               .append(entry4.doubleValue)
               .append(',')
               .append(entry4.doubleValue2)
               .append(',')
               .append(entry4.doubleValue3)
               .append(',')
               .append(entry4.intValue)
               .append(',')
               .append(entry4.flag ? 1 : 0)
               .append(',')
               .append(entry4.flag2 ? 1 : 0)
               .append(',')
               .append(entry4.flag3 ? 1 : 0)
               .append(',')
               .append(entry4.intValue2)
               .append(',')
               .append(entry4.doubleValue4)
               .append(',')
               .append(entry4.doubleValue5)
               .append(',')
               .append(entry4.doubleValue6)
               .append(',')
               .append(entry4.flag4 ? 1 : 0)
               .append(',')
               .append(entry4.doubleValue7)
               .append(',')
               .append(entry4.doubleValue8)
               .append(',')
               .append(entry4.doubleValue9)
               .append(',')
               .append(entry4.doubleValue10)
               .append(',')
               .append(entry4.flag5 ? 1 : 0)
               .append(',')
               .append(doubles2[0])
               .append(',')
               .append(doubles2[1])
               .append(',')
               .append(doubles3[0])
               .append(',')
               .append(doubles3[1])
               .append(',')
               .append(mc.player.getYRot())
               .append(',')
               .append(mc.player.getXRot())
               .append(',')
               .append(this.bcCameraNet != null ? this.bcCameraNet.source : "?")
               .append(',')
               .append(this.intValue6);

            for (double d2 : doubles) {
               builder.append(',').append((float)d2);
            }

            this.bufferedWriter.write(builder.append('\n').toString());
            if (++this.intValue4 % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException e) {
            this.intValue4 = 72000;
         }
      }
   }

   public Rotation method3554(Rotation rotation) {
      Rotation rotation2 = zenith.getRotationManager().method7844();
      this.intValue5++;
      Rotation rotation3 = zenith.getRotationManager().method7844();
      if (this.rotation != null && this.longValue2 == this.longValue) {
         this.longValue3 = this.longValue;
         return this.rotation;
      } else {
         mc.player.setYRot(rotation2.getFloatValue());
         mc.player.setXRot(rotation2.getFloatValue2());
         return rotation2;
      }
   }
}

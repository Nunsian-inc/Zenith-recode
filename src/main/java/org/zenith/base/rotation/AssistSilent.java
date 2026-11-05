package org.zenith.base.rotation;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import org.zenith.ChatLogger;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.ZenithAccess;
import org.zenith.base.Module;
import org.zenith.base.rotation.mods.BcCameraNet;
import org.zenith.base.rotation.mods.SilentAssistNet;
import org.zenith.utility.misc.RotationRequest;

public final class AssistSilent implements ZenithAccess {
   public static final AssistSilent INSTANCE = new AssistSilent("assist_silent.json", "NeuroSilent");
   public static final AssistSilent INSTANCE2 = new AssistSilent("assist_silent_strong.json", "NeuroSilent-strong");
   private static final int CONST_intValue = 1;
   private static final int CONST_intValue2 = 72000;
   private final String text;
   private final String text2;
   private volatile SilentAssistNet silentAssistNet;
   private CompletableFuture<Void> completableFuture;
   private volatile String status = "не загружался";
   private volatile boolean flag;
   private SilentAssistNet.Entry entry;
   private Object obj;
   private boolean flag2;
   private boolean flag3;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private float floatValue = 1.0F;
   private Rotation rotation;
   private BufferedWriter bufferedWriter;
   private int intValue;
   private long longValue;

   private AssistSilent(String text, String str) {
      this.text = text;
      this.text2 = str;
      EventManager.register(this);
   }

   public String getStatus() {
      return this.status;
   }

   public void method6305() {
      this.method6306();
   }

   private synchronized SilentAssistNet method6306() {
      if (this.silentAssistNet != null) {
         return this.silentAssistNet;
      } else {
         if (this.completableFuture == null) {
            this.completableFuture = CompletableFuture.runAsync(() -> {
               try (InputStream inputStream = this.method6307()) {
                  SilentAssistNet silentAssistNet = SilentAssistNet.method3634(inputStream);
                  this.status = this.text2 + " загружен (" + this.text + "). " + silentAssistNet.text2;
                  this.silentAssistNet = silentAssistNet;
               } catch (Exception e) {
                  this.status = this.text2 + " НЕ загружен (" + this.text + "): " + e.getMessage();
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

   private InputStream method6307() throws IOException {
      for (Path path : List.of(Path.of(this.text), Path.of("models", this.text), Path.of("..", "models", this.text))) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = AssistSilent.class.getClassLoader().getResourceAsStream("models/" + this.text);
      if (resourceAsStream == null) {
         throw new IOException(this.text + " не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   public void reset() {
      this.entry = null;
      this.flag2 = false;
      this.rotation = null;
      this.obj = null;
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100() && event.getPacket3() instanceof ClientboundPlayerPositionPacket) {
         this.flag = true;
      }
   }

   private static double method6308() {
      double d = mc.options.sensitivity().get() * 0.6F + 0.2F;
      return d * d * d * 8.0 * 0.15;
   }

   public void method6309(LivingEntity livingEntity, Module moduleValue) {
      this.longValue++;
      if (mc.player != null && mc.level != null && !mc.player.isDeadOrDying()) {
         SilentAssistNet silentAssistNet = this.method6306();
         if (silentAssistNet == null) {
            this.reset();
         } else {
            if (this.entry == null || mc.level != this.obj) {
               this.entry = silentAssistNet.method3623();
               this.flag2 = false;
               this.rotation = null;
            }

            this.obj = mc.level;
            Rotation currentRotation = rotationManager.getCurrentRotation();
            if (this.rotation != null
               && (
                  currentRotation == null
                     || Math.abs(method6310(currentRotation.getFloatValue() - this.rotation.getFloatValue())) > 0.001
                     || Math.abs(currentRotation.getFloatValue2() - this.rotation.getFloatValue2()) > 0.001
               )) {
               this.entry = silentAssistNet.method3623();
            }

            this.rotation = null;
            double yRot = mc.player.getYRot();
            double xRot = mc.player.getXRot();
            float attackStrengthScale = mc.player.getAttackStrengthScale(0.0F);
            boolean flag = attackStrengthScale < this.floatValue - 1.0E-4F;
            this.floatValue = attackStrengthScale;
            boolean flag2 = this.flag;
            this.flag = false;
            if (!this.flag2) {
               this.doubleValue = yRot;
               this.doubleValue2 = xRot;
               this.doubleValue3 = 0.0;
               this.doubleValue4 = 0.0;
               this.flag3 = false;
               this.flag2 = true;
            } else {
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
               entry4.flag3 = flag;
               if (livingEntity != null && livingEntity.isAlive()) {
                  entry4.doubleValue4 = livingEntity.getX();
                  entry4.doubleValue5 = livingEntity.getY();
                  entry4.doubleValue6 = livingEntity.getZ();
                  entry4.intValue2 = livingEntity.getId();
                  entry4.intValue3 = 1;
                  entry4.flag4 = livingEntity.isShiftKeyDown();
               }

               entry4.doubleValue7 = this.doubleValue;
               entry4.doubleValue8 = this.doubleValue2;
               entry4.doubleValue9 = this.doubleValue3;
               entry4.doubleValue10 = this.doubleValue4;
               entry4.flag5 = this.flag3;
               double d = yRot - this.doubleValue;
               double d2 = xRot - this.doubleValue2;
               boolean flag3 = !flag2 && Math.abs(d) <= 180.0 && Math.abs(d2) <= 180.0;
               double d3 = method6308();
               silentAssistNet.method3629(this.entry, entry4);
               double[] doubles = silentAssistNet.method3630(this.entry, entry4, d, d2, flag3, d3, yRot, xRot, null);
               this.doubleValue = yRot;
               this.doubleValue2 = xRot;
               this.doubleValue3 = d;
               this.doubleValue4 = d2;
               this.flag3 = flag3;
               this.method6311(entry4, d, d2, flag3, d3, doubles);
               if (this.entry.isFlag()) {
                  Rotation rotation = new Rotation((float)doubles[0], (float)doubles[1]);
                  rotationManager.method7848(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 1, moduleValue, 1);
                  this.rotation = rotation;
               }
            }
         }
      } else {
         this.reset();
      }
   }

   private static double method6310(double d) {
      double d2 = (d + 180.0) % 360.0;
      if (d2 < 0.0) {
         d2 += 360.0;
      }

      return d2 - 180.0;
   }

   private void method6311(BcCameraNet.Entry4 entry4, double d, double d2, boolean flag, double d3, double[] doubles) {
      if (this.intValue < 72000) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of("neuro_silent_log.csv"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND
               );
               this.bufferedWriter
                  .write(
                     "tick,age,model,grid,x,y,z,keys,on_ground,hcoll,attack,tgt_id,tx,ty,tz,tgt_sneak,yaw_prev,pitch_prev,my_prev,mp_prev,ok_prev,my,mp,ok,control,sent_yaw,sent_pitch,o_yaw,o_pitch,mul_yaw,d_yaw,d_pitch,margin_sent\n"
                  );
            }

            StringBuilder builder = new StringBuilder(320);
            builder.append(this.longValue)
               .append(',')
               .append(mc.player.tickCount)
               .append(',')
               .append(this.text2)
               .append(',')
               .append(d3)
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
               .append(d)
               .append(',')
               .append(d2)
               .append(',')
               .append(flag ? 1 : 0)
               .append(',')
               .append(this.entry.isFlag() ? 1 : 0)
               .append(',')
               .append(doubles[0])
               .append(',')
               .append(doubles[1])
               .append(',')
               .append(this.entry.getDoubleValue3())
               .append(',')
               .append(this.entry.getDoubleValue4())
               .append(',')
               .append(this.entry.doubleValue9)
               .append(',')
               .append(this.entry.doubleValue10)
               .append(',')
               .append(this.entry.doubleValue11)
               .append(',')
               .append(this.entry.doubleValue12)
               .append('\n');
            this.bufferedWriter.write(builder.toString());
            if (++this.intValue % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException e) {
            this.intValue = 72000;
         }
      }
   }

   public String method6312() {
      if (this.silentAssistNet == null) {
         return this.text2 + ": " + this.status;
      } else {
         return this.entry == null
            ? this.text2 + ": нет прохода"
            : String.format(
               "%s: %s, сдвиг yaw %.2f° pitch %.2f°, множитель yaw %.2f",
               this.text2,
               this.entry.isFlag() ? "ведёт" : "прогрев",
               this.entry.getDoubleValue3(),
               this.entry.getDoubleValue4(),
               1.0 + this.entry.doubleValue9
            );
      }
   }
}

package org.zenith.base.rotation.mods;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.player.AutoMineModule;
import org.zenith.utility.misc.RotationContext;

public class BcHand extends RotationContext {
   public static final String text = "bc_hand.json";
   private volatile HandAnchorNet handAnchorNet;
   private CompletableFuture<Void> completableFuture;
   private volatile String status = "не загружался";
   private final Random random = new Random();
   private HandAnchorNet.Entry entry;
   private long longValue;
   private long longValue2 = Long.MIN_VALUE;
   private long longValue3 = Long.MIN_VALUE;
   private Rotation rotation;
   private Object obj;
   private double doubleValue;
   private double doubleValue2;
   private boolean flag;
   private BcCameraNet.Entry4 entry4;
   private BcCameraNet.Entry4 entry42;
   private long longValue4 = Long.MIN_VALUE;
   private Rotation rotation2;
   private double doubleValue3;
   private static final int CONST_intValue = 72000;
   private BufferedWriter bufferedWriter;
   private int intValue;

   public BcHand() {
      EventManager.register(this);
   }

   public String getStatus3() {
      return this.status;
   }

   private synchronized HandAnchorNet method3867() {
      if (this.handAnchorNet != null) {
         return this.handAnchorNet;
      } else {
         if (this.completableFuture == null) {
            this.completableFuture = CompletableFuture.runAsync(() -> {
               try (InputStream inputStream = this.method3868()) {
                  HandAnchorNet handAnchorNet = HandAnchorNet.method3417(inputStream);
                  this.status = "NeuroBC-Hand загружен (bc_hand.json). " + handAnchorNet.text2 + "; модель " + handAnchorNet.source;
                  this.handAnchorNet = handAnchorNet;
               } catch (Exception e) {
                  this.status = "NeuroBC-Hand НЕ загружен (bc_hand.json): " + e.getMessage();
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

   private InputStream method3868() throws IOException {
      for (Path path : List.of(Path.of("bc_hand.json"), Path.of("models", "bc_hand.json"), Path.of("..", "models", "bc_hand.json"))) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = BcHand.class.getClassLoader().getResourceAsStream("models/bc_hand.json");
      if (resourceAsStream == null) {
         throw new FileNotFoundException("bc_hand.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   public boolean method3869() {
      return this.handAnchorNet != null;
   }

   public void reset() {
      this.entry = null;
      this.rotation = null;
      this.entry42 = null;
      this.flag = false;
      this.obj = null;
      this.entry4 = null;
   }

   private static double method3870() {
      double d = mc.options.sensitivity().get() * 0.6F + 0.2F;
      return d * d * d * 8.0 * 0.15;
   }

   private static BcHand.Type method3871() {
      if (AutoMineModule.INSTANCE.isEnabled()) {
         return BcHand.Type.MINE;
      } else {
         return AuraModule.INSTANCE.isEnabled() && AuraModule.INSTANCE.method6359(3) ? BcHand.Type.AURA : BcHand.Type.NONE;
      }
   }

   @EventTarget
   public void onTick(TickEvent event) {
      this.longValue++;
      if (this.entry42 != null) {
         this.method3872(null);
         this.rotation = null;
      }

      BcHand.Type type = method3871();
      if (mc.player != null && mc.level != null && type != BcHand.Type.NONE) {
         HandAnchorNet handAnchorNet = this.method3867();
         if (handAnchorNet == null) {
            this.reset();
         } else {
            if (this.entry == null || mc.level != this.obj) {
               this.entry = handAnchorNet.method3398();
               this.flag = false;
               this.entry4 = null;
            }

            this.obj = mc.level;
            Rotation rotation2 = zenith.getRotationManager().getEffectiveRotation();
            double floatValue = rotation2.getFloatValue();
            double d = rotation2.getFloatValue2();
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
            LivingEntity livingEntity = type == BcHand.Type.AURA ? AuraModule.INSTANCE.method6358() : null;
            if (livingEntity != null && livingEntity.isAlive()) {
               entry4.doubleValue4 = livingEntity.getX();
               entry4.doubleValue5 = livingEntity.getY();
               entry4.doubleValue6 = livingEntity.getZ();
               entry4.intValue2 = livingEntity.getId();
               entry4.flag4 = livingEntity.isShiftKeyDown();
            }

            entry4.doubleValue7 = floatValue;
            entry4.doubleValue8 = d;
            if (this.flag) {
               entry4.doubleValue9 = floatValue - this.doubleValue;
               entry4.doubleValue10 = d - this.doubleValue2;
               entry4.flag5 = Math.abs(entry4.doubleValue9) <= 180.0;
            } else {
               entry4.flag5 = false;
            }

            this.doubleValue = floatValue;
            this.doubleValue2 = d;
            this.flag = true;
            double[] doubles = null;
            boolean flag = entry4.intValue2 >= 0 && BcCameraNet.method3361(entry4.doubleValue4, entry4.doubleValue5, entry4.doubleValue6);
            if (flag) {
               boolean flag2 = this.entry4 != null
                  && this.entry4.intValue2 == entry4.intValue2
                  && BcCameraNet.method3361(this.entry4.doubleValue4, this.entry4.doubleValue5, this.entry4.doubleValue6);
               doubles = handAnchorNet.method3410(
                  new double[]{entry4.doubleValue, entry4.doubleValue2, entry4.doubleValue3},
                  this.entry4 == null ? null : new double[]{this.entry4.doubleValue, this.entry4.doubleValue2, this.entry4.doubleValue3},
                  new double[]{entry4.doubleValue4, entry4.doubleValue5, entry4.doubleValue6},
                  flag2 ? new double[]{this.entry4.doubleValue4, this.entry4.doubleValue5, this.entry4.doubleValue6} : null,
                  flag2,
                  entry4.flag4,
                  (entry4.intValue >> 5 & 1) != 0,
                  floatValue,
                  d
               );
            }

            this.entry4 = entry4;
            this.entry42 = entry4;
            this.longValue4 = this.longValue;
            this.rotation2 = rotation2;
            this.doubleValue3 = method3870();
            if (type == BcHand.Type.AURA) {
               this.method3872(doubles);
            }
         }
      } else {
         this.reset();
      }
   }

   private void method3872(double[] doubles) {
      BcCameraNet.Entry4 entry4 = this.entry42;
      HandAnchorNet handAnchorNet = this.handAnchorNet;
      this.entry42 = null;
      if (entry4 != null && handAnchorNet != null && this.entry != null) {
         double d = this.doubleValue3;
         if (this.longValue3 != this.longValue4 - 1L) {
            this.entry.method3419();
         }

         double[] doubles2 = entry4.flag5 ? new double[]{entry4.doubleValue9, entry4.doubleValue10} : new double[2];
         double[] doubles3 = handAnchorNet.method3414(this.entry, doubles2, doubles, entry4.doubleValue7, entry4.doubleValue8, d, this.random);
         double[] doubles4 = HandAnchorNet.method3415(this.entry, doubles3[0], doubles3[1], d, entry4.doubleValue8);
         this.rotation = this.rotation2.add((float)doubles4[0], (float)doubles4[1]);
         this.method3873(this.longValue4, entry4, doubles, doubles3, doubles4, d);
         this.longValue2 = this.longValue4;
      }
   }

   private void method3873(long lng, BcCameraNet.Entry4 entry4, double[] doubles, double[] doubles2, double[] doubles3, double d) {
      if (this.intValue < 72000) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of("bc_hand_log.csv"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND
               );
               this.bufferedWriter
                  .write(
                     "tick,age,applied_prev,grid,x,y,z,keys,on_ground,tgt_id,tx,ty,tz,tgt_sneak,yaw,pitch,my,mp,mv_ok,cmd_yaw,cmd_pitch,act_yaw,act_pitch,q_yaw,q_pitch,cam_yaw,cam_pitch,model\n"
                  );
            }

            StringBuilder builder = new StringBuilder(320);
            builder.append(lng)
               .append(',')
               .append(mc.player.tickCount)
               .append(',')
               .append(this.longValue3 == lng - 1L ? 1 : 0)
               .append(',')
               .append(d)
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
               .append(doubles == null ? Double.NaN : doubles[0])
               .append(',')
               .append(doubles == null ? Double.NaN : doubles[1])
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
               .append(this.handAnchorNet != null ? this.handAnchorNet.source : "?");
            this.bufferedWriter.write(builder.append('\n').toString());
            if (++this.intValue % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException e) {
            this.intValue = 72000;
         }
      }
   }

   public Rotation method3874(Rotation rotation) {
      if (this.entry42 != null && this.longValue4 == this.longValue) {
         this.method3872(
            rotation == null
               ? null
               : new double[]{
                  BcCameraNet.method3357(rotation.getFloatValue() - this.entry42.doubleValue7),
                  BcCameraNet.method3358(rotation.getFloatValue2(), -90.0, 90.0) - this.entry42.doubleValue8
               }
         );
      }

      Rotation rotation2 = zenith.getRotationManager().getEffectiveRotation();
      if (this.rotation != null && this.longValue2 == this.longValue) {
         this.longValue3 = this.longValue;
         return this.rotation;
      } else {
         return rotation2;
      }
   }

   private static enum Type {
      NONE,
      AURA,
      MINE;
   }
}

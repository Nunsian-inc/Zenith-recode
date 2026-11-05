package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.authlib.GameProfile;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.KeyName;

@ModuleInfo(
   name = "FakePlayer",
   description = "module.fakeplayer.description",
   category = ModuleCategory.MISC
)
public class FakePlayerModule extends Module {
   public static final FakePlayerModule INSTANCE = new FakePlayerModule();
   private final ModeSetting mode = new ModeSetting(
      "module.fakePlayer.mode",
      "module.fakePlayer.mode.desc",
      "module.fakePlayer.mode.default",
      "module.fakePlayer.mode.teleportation",
      "module.fakePlayer.mode.trajectory"
   );
   private final BindSetting recordingKey = new BindSetting("module.fakePlayer.recordingKey", "module.fakePlayer.recordingKey.desc", -1);
   private final BindSetting moveKey = new BindSetting("module.fakePlayer.moveKey", "module.fakePlayer.moveKey.desc", -1);
   private final SliderSetting minDistance = new SliderSetting(
      "module.fakePlayer.minDistance", 1.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.minDistance.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxDistance = new SliderSetting(
      "module.fakePlayer.maxDistance", 4.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.maxDistance.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting minXZOffset = new SliderSetting(
      "module.fakePlayer.minXZOffset", 0.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.minXZOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxXZOffset = new SliderSetting(
      "module.fakePlayer.maxXZOffset", 4.0F, 0.0F, 20.0F, 0.1F, "module.fakePlayer.maxXZOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting minYOffset = new SliderSetting(
      "module.fakePlayer.minYOffset", 0.0F, 0.0F, 10.0F, 0.1F, "module.fakePlayer.minYOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxYOffset = new SliderSetting(
      "module.fakePlayer.maxYOffset", 0.0F, 0.0F, 10.0F, 0.1F, "module.fakePlayer.maxYOffset.desc", "b", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting minIdlingTicks = new SliderSetting(
      "module.fakePlayer.minIdlingTicks", 5.0F, 0.0F, 200.0F, 1.0F, "module.fakePlayer.minIdlingTicks.desc", "t", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting maxIdlingTicks = new SliderSetting(
      "module.fakePlayer.maxIdlingTicks", 20.0F, 0.0F, 200.0F, 1.0F, "module.fakePlayer.maxIdlingTicks.desc", "t", () -> this.mode.isSelected(1), null
   );
   private final ModeSetting pattern = new ModeSetting(
      "module.fakePlayer.pattern",
      "module.fakePlayer.pattern.desc",
      () -> this.mode.isSelected(2),
      "module.fakePlayer.pattern.sine",
      "module.fakePlayer.pattern.sweep",
      "module.fakePlayer.pattern.circle",
      "module.fakePlayer.pattern.step",
      "module.fakePlayer.pattern.static",
      "module.fakePlayer.pattern.random"
   );
   private final SliderSetting yawAmplitude = new SliderSetting(
      "module.fakePlayer.yawAmplitude",
      30.0F,
      0.0F,
      170.0F,
      1.0F,
      "module.fakePlayer.yawAmplitude.desc",
      "d",
      () -> this.mode.isSelected(2) && !this.pattern.isSelected(4),
      null
   );
   private final SliderSetting pitchAmplitude = new SliderSetting(
      "module.fakePlayer.pitchAmplitude",
      0.0F,
      0.0F,
      60.0F,
      1.0F,
      "module.fakePlayer.pitchAmplitude.desc",
      "d",
      () -> this.mode.isSelected(2) && !this.pattern.isSelected(4),
      null
   );
   private final SliderSetting period = new SliderSetting(
      "module.fakePlayer.period",
      40.0F,
      4.0F,
      400.0F,
      1.0F,
      "module.fakePlayer.period.desc",
      "t",
      () -> this.mode.isSelected(2) && (this.pattern.isSelected(0) || this.pattern.isSelected(2)),
      null
   );
   private final SliderSetting sweepFrom = new SliderSetting(
      "module.fakePlayer.sweepFrom",
      200.0F,
      4.0F,
      400.0F,
      1.0F,
      "module.fakePlayer.sweepFrom.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(1),
      null
   );
   private final SliderSetting sweepTo = new SliderSetting(
      "module.fakePlayer.sweepTo",
      8.0F,
      4.0F,
      400.0F,
      1.0F,
      "module.fakePlayer.sweepTo.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(1),
      null
   );
   private final SliderSetting sweepLength = new SliderSetting(
      "module.fakePlayer.sweepLength",
      1200.0F,
      100.0F,
      12000.0F,
      100.0F,
      "module.fakePlayer.sweepLength.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(1),
      null
   );
   private final SliderSetting stepMin = new SliderSetting(
      "module.fakePlayer.stepMin",
      10.0F,
      1.0F,
      180.0F,
      1.0F,
      "module.fakePlayer.stepMin.desc",
      "d",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(3),
      null
   );
   private final SliderSetting stepMax = new SliderSetting(
      "module.fakePlayer.stepMax",
      90.0F,
      1.0F,
      180.0F,
      1.0F,
      "module.fakePlayer.stepMax.desc",
      "d",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(3),
      null
   );
   private final SliderSetting stepHold = new SliderSetting(
      "module.fakePlayer.stepHold",
      30.0F,
      5.0F,
      200.0F,
      1.0F,
      "module.fakePlayer.stepHold.desc",
      "t",
      () -> this.mode.isSelected(2) && this.pattern.isSelected(3),
      null
   );
   private final SliderSetting trajDistance = new SliderSetting(
      "module.fakePlayer.trajDistance", 3.0F, 1.0F, 20.0F, 0.1F, "module.fakePlayer.trajDistance.desc", "b", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting trajDistanceMax = new SliderSetting(
      "module.fakePlayer.trajDistanceMax", 3.0F, 1.0F, 20.0F, 0.1F, "module.fakePlayer.trajDistanceMax.desc", "b", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting ladderSteps = new SliderSetting(
      "module.fakePlayer.ladderSteps", 1.0F, 1.0F, 10.0F, 1.0F, "module.fakePlayer.ladderSteps.desc", "", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting ladderHold = new SliderSetting(
      "module.fakePlayer.ladderHold", 1200.0F, 100.0F, 12000.0F, 100.0F, "module.fakePlayer.ladderHold.desc", "t", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting baseElevation = new SliderSetting(
      "module.fakePlayer.baseElevation", 0.0F, -45.0F, 45.0F, 1.0F, "module.fakePlayer.baseElevation.desc", "d", () -> this.mode.isSelected(2), null
   );
   private final ModeSetting anchor = new ModeSetting(
      "module.fakePlayer.anchor",
      "module.fakePlayer.anchor.desc",
      () -> this.mode.isSelected(2),
      "module.fakePlayer.anchor.player",
      "module.fakePlayer.anchor.world"
   );
   private final ModeSetting motion = new ModeSetting(
      "module.fakePlayer.motion",
      "module.fakePlayer.motion.desc",
      () -> this.mode.isSelected(2),
      "module.fakePlayer.motion.smooth",
      "module.fakePlayer.motion.network",
      "module.fakePlayer.motion.exact"
   );
   private final BooleanSetting record_ = new BooleanSetting("module.fakePlayer.record", "module.fakePlayer.record.desc", false, () -> this.mode.isSelected(2));
   private final BindSetting recordKey = new BindSetting("module.fakePlayer.recordKey", "module.fakePlayer.recordKey.desc", -1);
   private boolean flag = false;
   private boolean flag2 = false;
   private int intValue = 0;
   private int intValue2 = 0;
   private int intValue3 = 0;
   private int intValue4 = 0;
   private int intValue5 = -1;
   private double doubleValue = 0.0;
   private static final double CONST_doubleValue = 0.7;
   private double doubleValue2 = 0.0;
   private double doubleValue3 = 0.0;
   private double doubleValue4 = 0.0;
   private int intValue6 = 0;
   private Vec3 vec3d = null;
   private double doubleValue5 = 0.0;
   private boolean flag3 = false;
   private double doubleValue6 = 0.0;
   private boolean flag4 = false;
   private static final double CONST_doubleValue2 = 15.0;
   private static final Path path = Path.of("rotation_recordings", "fakeplayer_traj_v1.csv");
   private static final String text = "session_id,frame_id,t_ms,dt_ms,tick,sens,raw_dx,raw_dy,ev_dyaw,ev_dpitch,real_dyaw,real_dpitch,yaw,pitch,tgt_az,tgt_el,dist,err_yaw,err_pitch,px,py,pz,vx,vz,on_ground,sprint,sneak,k_w,k_s,k_a,k_d,k_jump,pattern,anchor,traj_tick";
   private BufferedWriter bufferedWriter;
   private boolean flag5 = false;
   private long longValue = 0L;
   private long longValue2 = 0L;
   private long longValue3 = 0L;
   private long longValue4 = 0L;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private boolean flag6 = false;
   private double doubleValue7 = 0.0;
   private double doubleValue8 = 0.0;
   private double doubleValue9 = 0.0;
   private int intValue7 = Integer.MAX_VALUE;
   private RemotePlayer otherClientPlayerEntity;
   private final List<FakePlayerModule.Data> items = new ArrayList<>();

   private FakePlayerModule() {
   }

   private static String method7595(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   @Override
   public void onEnable() {
      if (mc.level != null) {
         if (mc.player != null) {
            this.method7611(mc.player.position(), mc.player.getYRot(), mc.player.getXRot());
         } else {
            this.method7611(Vec3.ZERO, 0.0F, 0.0F);
         }

         this.flag = false;
         this.flag2 = false;
         this.intValue = 0;
         this.intValue2 = 0;
         this.intValue3 = this.mode.getIndex();
         this.intValue5 = this.pattern.getIndex();
         this.method7605();
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.method7612(RemovalReason.KILLED);
      this.flag = false;
      this.flag2 = false;
      this.intValue = 0;
      this.intValue2 = 0;
      this.method7605();
      this.method7604();
      super.onDisable();
   }

   @EventTarget
   public void method7596(AttackEvent event) {
   }

   @EventTarget
   public void method7597(KeyEvent event) {
      if (this.mode.isSelected(2)) {
         if (event.isPress(this.recordKey.getKeyCode())) {
            this.record_.setEnabled(!this.record_.isEnabled());
            if (this.record_.isEnabled()) {
               this.method7604();
               this.flag5 = false;
               this.method7603();
            } else {
               this.method7604();
            }
         }
      } else if (this.mode.isSelected(0)) {
         if (event.isPress(this.recordingKey.getKeyCode())) {
            this.flag = !this.flag;
            if (this.flag) {
               this.items.clear();
            }
         }

         if (event.isPress(this.moveKey.getKeyCode())) {
            this.flag2 = !this.flag2;
         }
      }
   }

   @EventTarget
   public void method7598(ScreenRenderEvent event) {
      if (this.mode.isSelected(2)) {
         double d = this.method7607();
         String str;
         if (this.pattern.isSelected(1)) {
            int n = Math.max(1, Math.round(this.sweepLength.getValue3()));
            str = "sweep " + this.intValue4 % n + "/" + n;
         } else if (this.pattern.isSelected(3)) {
            str = String.format("step %.0fd, %dt", this.doubleValue4, this.intValue6);
         } else {
            str = "T=" + Math.round(this.period.getValue3()) + "t";
         }

         event.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Trajectory " + method7595(this.pattern.get()) + " / " + method7595(this.anchor.get()) + " / " + method7595(this.motion.get()),
               20.0F,
               20.0F,
               ZenithColor.INSTANCE
            );
         event.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F), String.format("dist %.2fb   %s   tick %d", d, str, this.intValue4), 20.0F, 35.0F, ZenithColor.INSTANCE
            );
         event.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               (this.record_.isEnabled() ? "REC " + this.longValue3 + " строк" : "не пишет")
                  + "  "
                  + KeyName.method6701(this.recordKey.getKeyCode())
                  + (this.flag5 ? "  ОШИБКА ЗАПИСИ" : ""),
               20.0F,
               50.0F,
               ZenithColor.INSTANCE
            );
      } else if (this.mode.isSelected(0)) {
         event.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Recoding " + this.flag + "  " + KeyName.method6701(this.recordingKey.getKeyCode()),
               20.0F,
               20.0F,
               ZenithColor.INSTANCE
            );
         event.getMenuDrawContext()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Moving " + this.flag2 + "  " + KeyName.method6701(this.moveKey.getKeyCode()),
               20.0F,
               35.0F,
               ZenithColor.INSTANCE
            );
      } else {
         event.getMenuDrawContext().drawText(Fonts.NEW_MEDIUM.getFont(10.0F), "Teleportation idle: " + this.intValue2 + "t", 20.0F, 20.0F, ZenithColor.INSTANCE);
      }
   }

   @EventTarget
   public void method7599(UpdateEvent event) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         if (this.intValue3 != this.mode.getIndex()) {
            this.intValue3 = this.mode.getIndex();
            this.intValue = 0;
            this.flag = false;
            this.flag2 = false;
            this.intValue2 = 0;
            this.method7605();
         }

         if (this.mode.isSelected(1)) {
            this.method7609();
         } else if (this.mode.isSelected(2)) {
            this.method7606();
         } else if (this.flag) {
            this.items
               .add(
                  new FakePlayerModule.Data(
                     mc.player.getX(),
                     mc.player.getY(),
                     mc.player.getZ(),
                     mc.player.getYRot(),
                     mc.player.getXRot(),
                     mc.player.isSwimming(),
                     mc.player.isShiftKeyDown()
                  )
               );
         } else {
            if (this.flag2 && !this.items.isEmpty()) {
               this.intValue++;
               if (this.intValue >= this.items.size()) {
                  this.intValue = 0;
                  return;
               }

               FakePlayerModule.Data data = this.items.get(this.intValue);
               this.otherClientPlayerEntity.setYRot(data.floatValue);
               this.otherClientPlayerEntity.setXRot(data.floatValue2);
               this.otherClientPlayerEntity.setYHeadRot(data.floatValue);
               this.otherClientPlayerEntity.setSwimming(data.flag);
               this.otherClientPlayerEntity.setShiftKeyDown(data.flag2);
               this.otherClientPlayerEntity.setPose(data.flag ? Pose.SWIMMING : (data.flag2 ? Pose.CROUCHING : Pose.STANDING));
               this.otherClientPlayerEntity.syncPacketPositionCodec(data.doubleValue, data.doubleValue2, data.doubleValue3);
               this.otherClientPlayerEntity
                  .moveOrInterpolateTo(new Vec3(data.doubleValue, data.doubleValue2, data.doubleValue3), data.floatValue, data.floatValue2);
            } else {
               this.intValue = 0;
            }
         }
      }
   }

   @EventTarget
   public void method7600(MouseRotationEvent event) {
      if (this.mode.isSelected(2) && this.record_.isEnabled() && mc.player != null && !this.flag5) {
         if (this.bufferedWriter == null) {
            this.method7603();
            if (this.bufferedWriter == null) {
               return;
            }
         }

         long nanoTime = System.nanoTime();
         double x = this.longValue4 == 0L ? 0.0 : (nanoTime - this.longValue4) / 1000000.0;
         this.longValue4 = nanoTime;
         float gameTimeDeltaPartialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
         this.method7601(gameTimeDeltaPartialTick);
         float yRot = mc.player.getYRot();
         float xRot = mc.player.getXRot();
         double d = this.flag6 ? Mth.wrapDegrees(yRot - this.floatValue) : 0.0;
         double x2 = this.flag6 ? xRot - this.floatValue2 : 0.0;
         this.floatValue = yRot;
         this.floatValue2 = xRot;
         this.flag6 = true;
         double x3 = Mth.wrapDegrees((float)(this.doubleValue7 - yRot));
         double x4 = -this.doubleValue8 - xRot;
         Vec3 deltaMovement = mc.player.getDeltaMovement();
         StringBuilder builder = new StringBuilder(256);
         builder.append(this.longValue)
            .append(',')
            .append(this.longValue2++)
            .append(',')
            .append(nanoTime / 1000000L)
            .append(',')
            .append(method7602(x))
            .append(',')
            .append(mc.player.tickCount)
            .append(',')
            .append(method7602(mc.options.sensitivity().get()))
            .append(',')
            .append(method7602(event.getDoubleValue3()))
            .append(',')
            .append(method7602(event.getDoubleValue4()))
            .append(',')
            .append(method7602(event.getDoubleValue() * 0.15))
            .append(',')
            .append(method7602(event.getDoubleValue2() * 0.15))
            .append(',')
            .append(method7602(d))
            .append(',')
            .append(method7602(x2))
            .append(',')
            .append(method7602(yRot))
            .append(',')
            .append(method7602(xRot))
            .append(',')
            .append(method7602(this.doubleValue7))
            .append(',')
            .append(method7602(this.doubleValue8))
            .append(',')
            .append(method7602(this.doubleValue9))
            .append(',')
            .append(method7602(x3))
            .append(',')
            .append(method7602(x4))
            .append(',')
            .append(method7602(mc.player.getX()))
            .append(',')
            .append(method7602(mc.player.getY()))
            .append(',')
            .append(method7602(mc.player.getZ()))
            .append(',')
            .append(method7602(deltaMovement.x))
            .append(',')
            .append(method7602(deltaMovement.z))
            .append(',')
            .append(mc.player.onGround() ? 1 : 0)
            .append(',')
            .append(mc.player.isSprinting() ? 1 : 0)
            .append(',')
            .append(mc.player.isShiftKeyDown() ? 1 : 0)
            .append(',')
            .append(mc.options.keyUp.isDown() ? 1 : 0)
            .append(',')
            .append(mc.options.keyDown.isDown() ? 1 : 0)
            .append(',')
            .append(mc.options.keyLeft.isDown() ? 1 : 0)
            .append(',')
            .append(mc.options.keyRight.isDown() ? 1 : 0)
            .append(',')
            .append(mc.options.keyJump.isDown() ? 1 : 0)
            .append(',')
            .append(this.pattern.getIndex())
            .append(',')
            .append(this.anchor.getIndex())
            .append(',')
            .append(this.intValue4);

         try {
            this.bufferedWriter.write(builder.toString());
            this.bufferedWriter.newLine();
            this.longValue3++;
            if ((this.longValue3 & 255L) == 0L) {
               this.bufferedWriter.flush();
            }
         } catch (IOException e) {
            this.flag5 = true;
            System.err.println("[FakePlayer] traj log write failed: " + e.getMessage());
            this.method7604();
         }
      }
   }

   private void method7601(float gameTimeDeltaPartialTick) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         AABB boundingBox = this.otherClientPlayerEntity.getBoundingBox();
         double d = (boundingBox.maxY - boundingBox.minY) * 0.5;
         double d2 = Mth.lerp((double)gameTimeDeltaPartialTick, this.otherClientPlayerEntity.xOld, this.otherClientPlayerEntity.getX());
         double d3 = Mth.lerp((double)gameTimeDeltaPartialTick, this.otherClientPlayerEntity.yOld, this.otherClientPlayerEntity.getY()) + d;
         double d4 = Mth.lerp((double)gameTimeDeltaPartialTick, this.otherClientPlayerEntity.zOld, this.otherClientPlayerEntity.getZ());
         double d5 = mc.player.getEyeY() - mc.player.getY();
         double d6 = Mth.lerp((double)gameTimeDeltaPartialTick, mc.player.xOld, mc.player.getX());
         double d7 = Mth.lerp((double)gameTimeDeltaPartialTick, mc.player.yOld, mc.player.getY()) + d5;
         double d8 = Mth.lerp((double)gameTimeDeltaPartialTick, mc.player.zOld, mc.player.getZ());
         double d9 = d2 - d6;
         double y = d3 - d7;
         double x = d4 - d8;
         double x2 = Math.sqrt(d9 * d9 + x * x);
         this.doubleValue7 = Math.toDegrees(Math.atan2(-d9, x));
         this.doubleValue8 = Math.toDegrees(Math.atan2(y, x2));
         this.doubleValue9 = Math.sqrt(x2 * x2 + y * y);
      }
   }

   private static String method7602(double x) {
      return x == 0.0 ? "0" : String.format(Locale.ROOT, "%.5f", x);
   }

   private void method7603() {
      try {
         Files.createDirectories(path.getParent());
         boolean flag = !Files.exists(path) || Files.size(path) == 0L;
         this.bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         if (flag) {
            this.bufferedWriter
               .write(
                  "session_id,frame_id,t_ms,dt_ms,tick,sens,raw_dx,raw_dy,ev_dyaw,ev_dpitch,real_dyaw,real_dpitch,yaw,pitch,tgt_az,tgt_el,dist,err_yaw,err_pitch,px,py,pz,vx,vz,on_ground,sprint,sneak,k_w,k_s,k_a,k_d,k_jump,pattern,anchor,traj_tick"
               );
            this.bufferedWriter.newLine();
         }

         this.longValue = System.currentTimeMillis();
         this.longValue2 = 0L;
         this.longValue3 = 0L;
         this.longValue4 = 0L;
         this.flag6 = false;
      } catch (IOException e) {
         this.flag5 = true;
         this.bufferedWriter = null;
         System.err.println("[FakePlayer] traj log open failed: " + e.getMessage());
      }
   }

   private void method7604() {
      if (this.bufferedWriter != null) {
         try {
            this.bufferedWriter.flush();
            this.bufferedWriter.close();
         } catch (IOException e) {
         }

         this.bufferedWriter = null;
      }
   }

   private void method7605() {
      this.intValue4 = 0;
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.intValue6 = 0;
      this.vec3d = null;
      this.flag3 = false;
      this.flag4 = false;
   }

   private void method7606() {
      if (mc.player != null) {
         if (!this.record_.isEnabled() && this.bufferedWriter != null) {
            this.method7604();
         }

         Vec3 vec3d;
         if (this.anchor.isSelected(1)) {
            if (this.vec3d == null) {
               this.vec3d = mc.player.position();
            }

            vec3d = this.vec3d;
         } else {
            vec3d = mc.player.position();
            this.vec3d = null;
         }

         if (!this.flag3) {
            this.doubleValue5 = mc.player.getYRot();
            this.flag3 = true;
         }

         double d = this.method7607();
         int index = this.pattern.getIndex();
         if (index != this.intValue5) {
            this.intValue5 = index;
            this.doubleValue = 0.0;
            this.doubleValue2 = 0.0;
            this.doubleValue3 = 0.0;
            this.intValue6 = 0;
            this.doubleValue4 = 0.0;
         }

         double d2;
         double d3;
         switch (index) {
            case 1:
               float f = Math.max(1.0F, this.sweepLength.getValue3());
               double d4 = (double)(this.intValue4 % (int)f) / f;
               double d5 = this.sweepFrom.getValue3() + (this.sweepTo.getValue3() - this.sweepFrom.getValue3()) * d4;
               d5 = Math.max(2.0, d5);
               this.doubleValue += (Math.PI * 2) / d5;
               d2 = this.yawAmplitude.getValue3() * Math.sin(this.doubleValue);
               d3 = this.pitchAmplitude.getValue3() * Math.sin(this.doubleValue);
               break;
            case 2:
               double d6 = (Math.PI * 2) * this.intValue4 / Math.max(2.0F, this.period.getValue3());
               d2 = this.yawAmplitude.getValue3() * Math.sin(d6);
               d3 = this.pitchAmplitude.getValue3() * Math.cos(d6);
               break;
            case 3:
               if (this.intValue6 <= 0) {
                  double nextDouble = ThreadLocalRandom.current()
                     .nextDouble(
                        Math.min(this.stepMin.getValue3(), this.stepMax.getValue3()), Math.max(this.stepMin.getValue3(), this.stepMax.getValue3()) + 1.0E-6
                     );
                  this.doubleValue4 = this.method7619(nextDouble);
                  this.intValue6 = Math.max(1, Math.round(this.stepHold.getValue3()));
               }

               this.intValue6--;
               d2 = this.doubleValue4;
               d3 = 0.0;
               break;
            case 4:
               d2 = 0.0;
               d3 = 0.0;
               break;
            case 5:
               double d7 = (Math.PI * 2) / Math.max(2.0F, this.period.getValue3());
               double d8 = Math.max(0.001F, this.yawAmplitude.getValue3());
               double d9 = ThreadLocalRandom.current().nextGaussian() * d8 * d7 * Math.sqrt(1.4 * d7);
               this.doubleValue3 = this.doubleValue3 + (-d7 * d7 * this.doubleValue2 - 1.4 * d7 * this.doubleValue3 + d9);
               this.doubleValue2 = this.doubleValue2 + this.doubleValue3;
               this.doubleValue2 = Mth.clamp(this.doubleValue2, -3.0 * d8, 3.0 * d8);
               d2 = this.doubleValue2;
               d3 = this.pitchAmplitude.getValue3() / d8 * this.doubleValue2 * 0.5;
               break;
            default:
               double d10 = (Math.PI * 2) * this.intValue4 / Math.max(2.0F, this.period.getValue3());
               d2 = this.yawAmplitude.getValue3() * Math.sin(d10);
               d3 = this.pitchAmplitude.getValue3() * Math.sin(d10);
         }

         double angdeg = this.doubleValue5 + d2;
         double d4 = this.baseElevation.getValue3() + d3;
         this.method7608(vec3d, d, angdeg, d4);
         this.intValue4++;
      }
   }

   private double method7607() {
      int n = Math.max(1, Math.round(this.ladderSteps.getValue3()));
      double d = Math.min(this.trajDistance.getValue3(), this.trajDistanceMax.getValue3());
      double d2 = Math.max(this.trajDistance.getValue3(), this.trajDistanceMax.getValue3());
      if (n != 1 && !(d2 - d < 1.0E-6)) {
         int n2 = Math.max(1, Math.round(this.ladderHold.getValue3()));
         int n3 = this.intValue4 / n2 % n;
         return d + (d2 - d) * n3 / (n - 1);
      } else {
         return d;
      }
   }

   private void method7608(Vec3 vec3d, double d, double angdeg, double angdeg2) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         double d2 = Math.toRadians(angdeg);
         double d3 = Math.toRadians(angdeg2);
         double d4 = d * Math.cos(d3);
         double eyeY = this.anchor.isSelected(1) ? vec3d.y + mc.player.getEyeHeight() : mc.player.getEyeY();
         double x = vec3d.x - d4 * Math.sin(d2);
         double z = vec3d.z + d4 * Math.cos(d2);
         AABB boundingBox = this.otherClientPlayerEntity.getBoundingBox();
         double d5 = eyeY + d * Math.sin(d3);
         double y = d5 - (boundingBox.maxY - boundingBox.minY) * 0.5;
         double d6 = this.flag4 ? Math.abs(Mth.wrapDegrees((float)(angdeg - this.doubleValue6))) : 0.0;
         this.doubleValue6 = angdeg;
         this.flag4 = true;
         float yBodyRot = Mth.wrapDegrees((float)angdeg + 180.0F);
         if (this.motion.isSelected(2) || d6 > 15.0) {
            this.otherClientPlayerEntity.snapTo(x, y, z, yBodyRot, 0.0F);
            this.otherClientPlayerEntity.syncPacketPositionCodec(x, y, z);
         } else if (this.motion.isSelected(1)) {
            this.otherClientPlayerEntity.syncPacketPositionCodec(x, y, z);
            this.otherClientPlayerEntity.moveOrInterpolateTo(new Vec3(x, y, z), yBodyRot, 0.0F);
         } else {
            double xo = this.otherClientPlayerEntity.getX();
            double yo = this.otherClientPlayerEntity.getY();
            double zo = this.otherClientPlayerEntity.getZ();
            this.otherClientPlayerEntity.setPos(x, y, z);
            this.otherClientPlayerEntity.syncPacketPositionCodec(x, y, z);
            this.otherClientPlayerEntity.xo = xo;
            this.otherClientPlayerEntity.yo = yo;
            this.otherClientPlayerEntity.zo = zo;
            this.otherClientPlayerEntity.xOld = xo;
            this.otherClientPlayerEntity.yOld = yo;
            this.otherClientPlayerEntity.zOld = zo;
            this.otherClientPlayerEntity.setYRot(yBodyRot);
            this.otherClientPlayerEntity.setXRot(0.0F);
         }

         this.otherClientPlayerEntity.setYHeadRot(yBodyRot);
         this.otherClientPlayerEntity.yBodyRot = yBodyRot;
         this.otherClientPlayerEntity.setPose(Pose.STANDING);
      }
   }

   private void method7609() {
      if (mc.player != null) {
         if (this.intValue2 > 0) {
            this.intValue2--;
         } else {
            Vec3 position = mc.player.position();
            double d = this.method7616(this.minDistance, this.maxDistance);
            Vec3 vec = this.method7614(d);
            Vec3 position2 = position.add(vec.x, this.method7618(this.minYOffset, this.maxYOffset), vec.z);
            float yBodyRot = this.method7615(position2, position);
            float xRot = 0.0F;
            this.method7610(position2, yBodyRot, xRot);
            this.intValue2 = this.method7617(this.minIdlingTicks, this.maxIdlingTicks);
         }
      }
   }

   private void method7610(Vec3 position, float yBodyRot, float xRot) {
      this.method7612(RemovalReason.DISCARDED);
      this.method7611(position, yBodyRot, xRot);
   }

   private void method7611(Vec3 position, float yBodyRot, float xRot) {
      if (mc.level != null) {
         this.otherClientPlayerEntity = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), "Fake"));
         this.otherClientPlayerEntity.setId(this.method7613());
         this.otherClientPlayerEntity.snapTo(position, yBodyRot, xRot);
         this.otherClientPlayerEntity.setYHeadRot(yBodyRot);
         this.otherClientPlayerEntity.yBodyRot = yBodyRot;
         this.otherClientPlayerEntity.yBodyRotO = yBodyRot;
         this.otherClientPlayerEntity.yHeadRotO = yBodyRot;
         mc.level.addEntity(this.otherClientPlayerEntity);
      }
   }

   private void method7612(RemovalReason reason) {
      if (this.otherClientPlayerEntity != null) {
         if (mc.level != null && mc.level.getEntity(this.otherClientPlayerEntity.getId()) == this.otherClientPlayerEntity) {
            mc.level.removeEntity(this.otherClientPlayerEntity.getId(), reason);
         } else {
            this.otherClientPlayerEntity.setRemoved(reason);
            this.otherClientPlayerEntity.onClientRemoval();
         }

         this.otherClientPlayerEntity = null;
      }
   }

   private int method7613() {
      for (int i = 0; i < 1000000; i++) {
         if (this.intValue7 <= 2146483647) {
            this.intValue7 = Integer.MAX_VALUE;
         }

         int id = --this.intValue7;
         if (mc.level == null || mc.level.getEntity(id) == null) {
            return id;
         }
      }

      return Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 1000000);
   }

   private Vec3 method7614(double d) {
      if (d <= 0.0) {
         return Vec3.ZERO;
      } else {
         double nextDouble = Math.min((double)this.method7616(this.minXZOffset, this.maxXZOffset), d);
         double nextDouble2 = Math.sqrt(d * d - nextDouble * nextDouble);
         double x = this.method7619(nextDouble2);
         double z = this.method7619(nextDouble);
         return ThreadLocalRandom.current().nextBoolean() ? new Vec3(x, 0.0, z) : new Vec3(z, 0.0, x);
      }
   }

   private float method7615(Vec3 vec, Vec3 position) {
      double x = position.x - vec.x;
      double y = position.z - vec.z;
      return Mth.wrapDegrees((float)Math.toDegrees(Math.atan2(y, x)) - 90.0F);
   }

   private float method7616(SliderSetting minDistance, SliderSetting maxDistance) {
      float origin = Math.min(minDistance.getValue3(), maxDistance.getValue3());
      float bound = Math.max(minDistance.getValue3(), maxDistance.getValue3());
      return origin == bound ? origin : (float)ThreadLocalRandom.current().nextDouble(origin, bound);
   }

   private int method7617(SliderSetting minIdlingTicks, SliderSetting maxIdlingTicks) {
      int origin = Math.round(Math.min(minIdlingTicks.getValue3(), maxIdlingTicks.getValue3()));
      int n = Math.round(Math.max(minIdlingTicks.getValue3(), maxIdlingTicks.getValue3()));
      return origin >= n ? origin : ThreadLocalRandom.current().nextInt(origin, n + 1);
   }

   private double method7618(SliderSetting minYOffset, SliderSetting maxYOffset) {
      float nextDouble = this.method7616(minYOffset, maxYOffset);
      return nextDouble <= 0.0F ? 0.0 : this.method7619(nextDouble);
   }

   private double method7619(double nextDouble) {
      return ThreadLocalRandom.current().nextBoolean() ? nextDouble : -nextDouble;
   }

   @EventTarget
   public void method7620(AttackEvent event) {
      if (this.otherClientPlayerEntity != null && mc.player != null) {
         if (event.getType4() == AttackEvent.Type.POST && this.otherClientPlayerEntity.isDeadOrDying()) {
            this.otherClientPlayerEntity.setHealth(20.0F);
            new ClientboundEntityEventPacket(this.otherClientPlayerEntity, (byte)35).handle((ClientGamePacketListener)mc.player.connection);
         }
      }
   }

   private static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final float floatValue2;
      private final boolean flag;
      private final boolean flag2;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, floatValue, floatValue2, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data(double doubleValue, double y, double z, float floatValue, float xRot, boolean flag, boolean shiftKeyDown) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = y;
         this.doubleValue3 = z;
         this.floatValue = floatValue;
         this.floatValue2 = xRot;
         this.flag = flag;
         this.flag2 = shiftKeyDown;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }
}

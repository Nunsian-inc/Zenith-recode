package org.zenith.utility.misc;

import baritone.api.BaritoneAPI;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.client.events.UpdateEvent;

public final class PlayerAimUtils implements MinecraftAccess {
   private static final float CONST_floatValue = 47.0F;
   private static final float CONST_floatValue2 = 1.2F;
   private static final long CONST_longValue = 200L;
   private static float targetYaw;
   private static float targetPitch;
   private static long lastRotationTime;
   private static float serverYaw;
   private static float serverPitch;
   private static boolean rotationInitialized;
   private static float renderYaw;
   private static float renderPitch;
   private static float previousRenderYaw;
   private static float previousRenderPitch;
   private static boolean rotationLocked;
   private static float yawNoise;
   private static float pitchNoise;

   private PlayerAimUtils() {
   }

   public static void aimAt(Vec3 targetPosition) {
      float[] floats = anglesTo(targetPosition);
      smoothAim(floats[0], floats[1]);
   }

   public static boolean isWithinAimTolerance(Vec3 targetPosition, float f) {
      float[] floats = anglesTo(targetPosition);
      float f2 = Mth.wrapDegrees(floats[0] - targetYaw);
      float f3 = floats[1] - targetPitch;
      return Math.sqrt(f2 * f2 + f3 * f3) <= f;
   }

   public static float[] anglesTo(Vec3 targetPosition) {
      Vec3 eyePosition = mc.player.getEyePosition();
      double x = targetPosition.x - eyePosition.x;
      double y = targetPosition.y - eyePosition.y;
      double d = targetPosition.z - eyePosition.z;
      double x2 = Math.sqrt(x * x + d * d);
      return new float[]{(float)Math.toDegrees(Math.atan2(d, x)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(y, x2)))};
   }

   public static void smoothAim(float f, float f2) {
      long now = System.currentTimeMillis();
      if (lastRotationTime == 0L || !rotationLocked && now - lastRotationTime > 400L) {
         targetYaw = mc.player.getYRot();
         targetPitch = mc.player.getXRot();
      }

      targetYaw = Mth.wrapDegrees(targetYaw);
      lastRotationTime = now;
      float[] floats = calculateSmoothRotation(targetYaw, targetPitch, f, f2);
      targetYaw = floats[0];
      targetPitch = floats[1];
   }

   public static void rotatePlayerToward(Vec3 targetPosition) {
      float[] floats = anglesTo(targetPosition);
      float[] floats2 = calculateSmoothRotation(Mth.wrapDegrees(mc.player.getYRot()), mc.player.getXRot(), floats[0], floats[1]);
      mc.player.setYRot(floats2[0]);
      mc.player.setXRot(floats2[1]);
      targetYaw = floats2[0];
      targetPitch = floats2[1];
      lastRotationTime = System.currentTimeMillis();
   }

   public static void setRotationLock(boolean rotationInitialized) {
      rotationLocked = rotationInitialized;
      if (rotationInitialized) {
         lastRotationTime = 0L;
      }
   }

   private static float[] calculateSmoothRotation(float targetYaw, float xRot, float f, float f2) {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      yawNoise = Mth.clamp(yawNoise * 0.96F + (float)current.nextGaussian() * 0.15F, -1.2F, 1.2F);
      pitchNoise = Mth.clamp(pitchNoise * 0.96F + (float)current.nextGaussian() * 0.1F, -0.9F, 0.9F);
      float f3 = Mth.wrapDegrees(f + yawNoise - targetYaw);
      float f4 = Mth.clamp(f2 + pitchNoise, -90.0F, 90.0F) - xRot;
      float f5 = (float)Math.sqrt(f3 * f3 + f4 * f4);
      if (f5 < 0.08F) {
         return new float[]{targetYaw, xRot};
      } else {
         float f6 = 0.31F + current.nextFloat() * 0.28F;
         float f7 = Mth.clamp(f5 * f6, Math.min(f5, 1.2F), 47.0F);
         if (f5 < 5.0F && f5 > 1.5F && current.nextFloat() < 0.1F) {
            f7 = Math.min(f5 * 1.2F, 47.0F);
         }

         float f8 = f7 / f5;
         float f9 = f3 * f8 + (float)current.nextGaussian() * 0.27F;
         float f10 = f4 * f8 * 0.85F + (float)current.nextGaussian() * 0.18F;
         return new float[]{targetYaw + snapToMouseSensitivity(f9), Mth.clamp(xRot + snapToMouseSensitivity(f10), -90.0F, 90.0F)};
      }
   }

   public static void setMovementToward(Vec3 targetPosition) {
      double x = targetPosition.x - mc.player.getX();
      double y = targetPosition.z - mc.player.getZ();
      float f = (float)Math.toDegrees(Math.atan2(y, x)) - 90.0F;
      float f2 = Mth.wrapDegrees(f - mc.player.getYRot());
      boolean down = f2 > -67.5F && f2 < 67.5F;
      boolean rotationInitialized = f2 > 112.5F || f2 < -112.5F;
      boolean down2 = f2 < -22.5F && f2 > -157.5F;
      boolean down3 = f2 > 22.5F && f2 < 157.5F;
      mc.options.keyUp.setDown(down);
      mc.options.keyDown.setDown(rotationInitialized);
      mc.options.keyLeft.setDown(down2);
      mc.options.keyRight.setDown(down3);
      mc.options.keySprint.setDown(down);
   }

   public static void clearMovementKeys() {
      mc.options.keyUp.setDown(false);
      mc.options.keyDown.setDown(false);
      mc.options.keyLeft.setDown(false);
      mc.options.keyRight.setDown(false);
      mc.options.keySprint.setDown(false);
   }

   public static boolean isRotationActive() {
      return rotationInitialized && mc.player != null;
   }

   public static float interpolatedYaw(float f) {
      return previousRenderYaw + Mth.wrapDegrees(renderYaw - previousRenderYaw) * f;
   }

   public static float interpolatedPitch(float alpha1) {
      return Mth.lerp(alpha1, previousRenderPitch, renderPitch);
   }

   private static void limitRotationDelta(float yRot, float xRot) {
      float sign = Mth.wrapDegrees(yRot - targetYaw);
      if (Math.abs(sign) > 30.0F) {
         targetYaw = Mth.wrapDegrees(yRot - Math.copySign(30.0F, sign));
      }

      float f = xRot - targetPitch;
      if (Math.abs(f) > 30.0F) {
         targetPitch = Mth.clamp(xRot - Math.copySign(30.0F, f), -90.0F, 90.0F);
      }
   }

   private static boolean isBaritonePathing() {
      try {
         return BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      } catch (Throwable e) {
         return false;
      }
   }

   private static float snapToMouseSensitivity(float f) {
      double d = mc.options.sensitivity().get() * 0.6 + 0.2;
      double d2 = d * d * d * 1.2;
      return d2 <= 0.0 ? f : (float)(Math.round(f / d2) * d2);
   }

   static {
      EventManager.register(new Object() {
         @EventTarget
         public void onPacket(PacketEvent event) {
            if (event.isOutgoing() && MinecraftAccess.mc.player != null && event.getPacket() instanceof ServerboundMovePlayerPacket movePlayerPacket && movePlayerPacket.hasRot) {
               if (System.currentTimeMillis() - PlayerAimUtils.lastRotationTime <= 200L) {
                  movePlayerPacket.yRot = PlayerAimUtils.targetYaw;
                  movePlayerPacket.xRot = PlayerAimUtils.targetPitch;
               } else {
                  float f = Mth.wrapDegrees(movePlayerPacket.yRot - PlayerAimUtils.targetYaw);
                  float f2 = movePlayerPacket.xRot - PlayerAimUtils.targetPitch;
                  if (PlayerAimUtils.isBaritonePathing()) {
                     PlayerAimUtils.targetYaw = movePlayerPacket.yRot;
                     PlayerAimUtils.targetPitch = movePlayerPacket.xRot;
                  } else if (PlayerAimUtils.rotationLocked && PlayerAimUtils.lastRotationTime != 0L) {
                     movePlayerPacket.yRot = PlayerAimUtils.targetYaw;
                     movePlayerPacket.xRot = PlayerAimUtils.targetPitch;
                  } else if (!(Math.abs(f) > 8.0F) && !(Math.abs(f2) > 8.0F)) {
                     PlayerAimUtils.targetYaw = movePlayerPacket.yRot;
                     PlayerAimUtils.targetPitch = movePlayerPacket.xRot;
                  } else {
                     PlayerAimUtils.targetYaw = Mth.wrapDegrees(PlayerAimUtils.targetYaw + Mth.clamp(f, -35.0F, 35.0F));
                     PlayerAimUtils.targetPitch = Mth.clamp(PlayerAimUtils.targetPitch + Mth.clamp(f2, -35.0F, 35.0F), -90.0F, 90.0F);
                     PlayerAimUtils.limitRotationDelta(movePlayerPacket.yRot, movePlayerPacket.xRot);
                     movePlayerPacket.yRot = PlayerAimUtils.targetYaw;
                     movePlayerPacket.xRot = PlayerAimUtils.targetPitch;
                  }

                  PlayerAimUtils.serverYaw = movePlayerPacket.yRot;
                  PlayerAimUtils.serverPitch = movePlayerPacket.xRot;
                  if (!PlayerAimUtils.rotationInitialized) {
                     PlayerAimUtils.rotationInitialized = true;
                     PlayerAimUtils.renderYaw = PlayerAimUtils.previousRenderYaw = PlayerAimUtils.serverYaw;
                     PlayerAimUtils.renderPitch = PlayerAimUtils.previousRenderPitch = PlayerAimUtils.serverPitch;
                  }
               }
            }
         }

         @EventTarget
         public void onUpdate(UpdateEvent event) {
            if (MinecraftAccess.mc.player != null) {
               boolean rotationInitialized = System.currentTimeMillis() - PlayerAimUtils.lastRotationTime <= 200L;
               float targetYaw = rotationInitialized ? PlayerAimUtils.targetYaw : (PlayerAimUtils.rotationInitialized ? PlayerAimUtils.serverYaw : MinecraftAccess.mc.player.getYRot());
               float xRot = rotationInitialized ? PlayerAimUtils.targetPitch : (PlayerAimUtils.rotationInitialized ? PlayerAimUtils.serverPitch : MinecraftAccess.mc.player.getXRot());
               PlayerAimUtils.previousRenderYaw = PlayerAimUtils.renderYaw;
               PlayerAimUtils.previousRenderPitch = PlayerAimUtils.renderPitch;
               float f = Mth.wrapDegrees(targetYaw - PlayerAimUtils.renderYaw);
               float f2 = xRot - PlayerAimUtils.renderPitch;
               PlayerAimUtils.renderYaw = PlayerAimUtils.renderYaw + Mth.clamp(f * 0.45F, -40.0F, 40.0F);
               PlayerAimUtils.renderPitch = Mth.clamp(PlayerAimUtils.renderPitch + Mth.clamp(f2 * 0.45F, -40.0F, 40.0F), -90.0F, 90.0F);
            }
         }
      });
   }
}

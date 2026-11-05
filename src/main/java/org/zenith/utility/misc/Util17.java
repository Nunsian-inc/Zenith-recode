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

public final class Util17 implements MinecraftAccess {
   private static final float CONST_floatValue = 47.0F;
   private static final float CONST_floatValue2 = 1.2F;
   private static final long CONST_longValue = 200L;
   private static float floatValue;
   private static float floatValue2;
   private static long longValue;
   private static float floatValue3;
   private static float floatValue4;
   private static boolean flag;
   private static float floatValue5;
   private static float floatValue6;
   private static float floatValue7;
   private static float floatValue8;
   private static boolean flag2;
   private static float floatValue9;
   private static float floatValue10;

   private Util17() {
   }

   public static void method3329(Vec3 atCenterOf) {
      float[] floats = method3331(atCenterOf);
      method3332(floats[0], floats[1]);
   }

   public static boolean method3330(Vec3 atCenterOf, float f) {
      float[] floats = method3331(atCenterOf);
      float f2 = Mth.wrapDegrees(floats[0] - floatValue);
      float f3 = floats[1] - floatValue2;
      return Math.sqrt(f2 * f2 + f3 * f3) <= f;
   }

   public static float[] method3331(Vec3 atCenterOf) {
      Vec3 eyePosition = mc.player.getEyePosition();
      double x = atCenterOf.x - eyePosition.x;
      double y = atCenterOf.y - eyePosition.y;
      double d = atCenterOf.z - eyePosition.z;
      double x2 = Math.sqrt(x * x + d * d);
      return new float[]{(float)Math.toDegrees(Math.atan2(d, x)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(y, x2)))};
   }

   public static void method3332(float f, float f2) {
      long now = System.currentTimeMillis();
      if (longValue == 0L || !flag2 && now - longValue > 400L) {
         floatValue = mc.player.getYRot();
         floatValue2 = mc.player.getXRot();
      }

      floatValue = Mth.wrapDegrees(floatValue);
      longValue = now;
      float[] floats = method3335(floatValue, floatValue2, f, f2);
      floatValue = floats[0];
      floatValue2 = floats[1];
   }

   public static void method3333(Vec3 atCenterOf) {
      float[] floats = method3331(atCenterOf);
      float[] floats2 = method3335(Mth.wrapDegrees(mc.player.getYRot()), mc.player.getXRot(), floats[0], floats[1]);
      mc.player.setYRot(floats2[0]);
      mc.player.setXRot(floats2[1]);
      floatValue = floats2[0];
      floatValue2 = floats2[1];
      longValue = System.currentTimeMillis();
   }

   public static void method3334(boolean flag) {
      flag2 = flag;
      if (flag) {
         longValue = 0L;
      }
   }

   private static float[] method3335(float floatValue, float xRot, float f, float f2) {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      floatValue9 = Mth.clamp(floatValue9 * 0.96F + (float)current.nextGaussian() * 0.15F, -1.2F, 1.2F);
      floatValue10 = Mth.clamp(floatValue10 * 0.96F + (float)current.nextGaussian() * 0.1F, -0.9F, 0.9F);
      float f3 = Mth.wrapDegrees(f + floatValue9 - floatValue);
      float f4 = Mth.clamp(f2 + floatValue10, -90.0F, 90.0F) - xRot;
      float f5 = (float)Math.sqrt(f3 * f3 + f4 * f4);
      if (f5 < 0.08F) {
         return new float[]{floatValue, xRot};
      } else {
         float f6 = 0.31F + current.nextFloat() * 0.28F;
         float f7 = Mth.clamp(f5 * f6, Math.min(f5, 1.2F), 47.0F);
         if (f5 < 5.0F && f5 > 1.5F && current.nextFloat() < 0.1F) {
            f7 = Math.min(f5 * 1.2F, 47.0F);
         }

         float f8 = f7 / f5;
         float f9 = f3 * f8 + (float)current.nextGaussian() * 0.27F;
         float f10 = f4 * f8 * 0.85F + (float)current.nextGaussian() * 0.18F;
         return new float[]{floatValue + method3343(f9), Mth.clamp(xRot + method3343(f10), -90.0F, 90.0F)};
      }
   }

   public static void method3336(Vec3 vec3d2) {
      double x = vec3d2.x - mc.player.getX();
      double y = vec3d2.z - mc.player.getZ();
      float f = (float)Math.toDegrees(Math.atan2(y, x)) - 90.0F;
      float f2 = Mth.wrapDegrees(f - mc.player.getYRot());
      boolean down = f2 > -67.5F && f2 < 67.5F;
      boolean flag = f2 > 112.5F || f2 < -112.5F;
      boolean down2 = f2 < -22.5F && f2 > -157.5F;
      boolean down3 = f2 > 22.5F && f2 < 157.5F;
      mc.options.keyUp.setDown(down);
      mc.options.keyDown.setDown(flag);
      mc.options.keyLeft.setDown(down2);
      mc.options.keyRight.setDown(down3);
      mc.options.keySprint.setDown(down);
   }

   public static void method3337() {
      mc.options.keyUp.setDown(false);
      mc.options.keyDown.setDown(false);
      mc.options.keyLeft.setDown(false);
      mc.options.keyRight.setDown(false);
      mc.options.keySprint.setDown(false);
   }

   public static boolean method3338() {
      return flag && mc.player != null;
   }

   public static float method3339(float f) {
      return floatValue7 + Mth.wrapDegrees(floatValue5 - floatValue7) * f;
   }

   public static float method3340(float alpha1) {
      return Mth.lerp(alpha1, floatValue8, floatValue6);
   }

   private static void method3341(float yRot, float xRot) {
      float sign = Mth.wrapDegrees(yRot - floatValue);
      if (Math.abs(sign) > 30.0F) {
         floatValue = Mth.wrapDegrees(yRot - Math.copySign(30.0F, sign));
      }

      float f = xRot - floatValue2;
      if (Math.abs(f) > 30.0F) {
         floatValue2 = Mth.clamp(xRot - Math.copySign(30.0F, f), -90.0F, 90.0F);
      }
   }

   private static boolean method3342() {
      try {
         return BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      } catch (Throwable e) {
         return false;
      }
   }

   private static float method3343(float f) {
      double d = mc.options.sensitivity().get() * 0.6 + 0.2;
      double d2 = d * d * d * 1.2;
      return d2 <= 0.0 ? f : (float)(Math.round(f / d2) * d2);
   }

   static {
      EventManager.register(new Object() {
         @EventTarget
         public void onPacket(PacketEvent event) {
            if (event.method7099() && MinecraftAccess.mc.player != null && event.getPacket3() instanceof ServerboundMovePlayerPacket movePlayerPacket && movePlayerPacket.hasRot) {
               if (System.currentTimeMillis() - Util17.longValue <= 200L) {
                  movePlayerPacket.yRot = Util17.floatValue;
                  movePlayerPacket.xRot = Util17.floatValue2;
               } else {
                  float f = Mth.wrapDegrees(movePlayerPacket.yRot - Util17.floatValue);
                  float f2 = movePlayerPacket.xRot - Util17.floatValue2;
                  if (Util17.method3342()) {
                     Util17.floatValue = movePlayerPacket.yRot;
                     Util17.floatValue2 = movePlayerPacket.xRot;
                  } else if (Util17.flag2 && Util17.longValue != 0L) {
                     movePlayerPacket.yRot = Util17.floatValue;
                     movePlayerPacket.xRot = Util17.floatValue2;
                  } else if (!(Math.abs(f) > 8.0F) && !(Math.abs(f2) > 8.0F)) {
                     Util17.floatValue = movePlayerPacket.yRot;
                     Util17.floatValue2 = movePlayerPacket.xRot;
                  } else {
                     Util17.floatValue = Mth.wrapDegrees(Util17.floatValue + Mth.clamp(f, -35.0F, 35.0F));
                     Util17.floatValue2 = Mth.clamp(Util17.floatValue2 + Mth.clamp(f2, -35.0F, 35.0F), -90.0F, 90.0F);
                     Util17.method3341(movePlayerPacket.yRot, movePlayerPacket.xRot);
                     movePlayerPacket.yRot = Util17.floatValue;
                     movePlayerPacket.xRot = Util17.floatValue2;
                  }

                  Util17.floatValue3 = movePlayerPacket.yRot;
                  Util17.floatValue4 = movePlayerPacket.xRot;
                  if (!Util17.flag) {
                     Util17.flag = true;
                     Util17.floatValue5 = Util17.floatValue7 = Util17.floatValue3;
                     Util17.floatValue6 = Util17.floatValue8 = Util17.floatValue4;
                  }
               }
            }
         }

         @EventTarget
         public void onUpdate(UpdateEvent event) {
            if (MinecraftAccess.mc.player != null) {
               boolean flag = System.currentTimeMillis() - Util17.longValue <= 200L;
               float floatValue = flag ? Util17.floatValue : (Util17.flag ? Util17.floatValue3 : MinecraftAccess.mc.player.getYRot());
               float xRot = flag ? Util17.floatValue2 : (Util17.flag ? Util17.floatValue4 : MinecraftAccess.mc.player.getXRot());
               Util17.floatValue7 = Util17.floatValue5;
               Util17.floatValue8 = Util17.floatValue6;
               float f = Mth.wrapDegrees(floatValue - Util17.floatValue5);
               float f2 = xRot - Util17.floatValue6;
               Util17.floatValue5 = Util17.floatValue5 + Mth.clamp(f * 0.45F, -40.0F, 40.0F);
               Util17.floatValue6 = Mth.clamp(Util17.floatValue6 + Mth.clamp(f2 * 0.45F, -40.0F, 40.0F), -90.0F, 90.0F);
            }
         }
      });
   }
}

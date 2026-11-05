package org.zenith.utility.misc;

import java.util.Objects;
import lombok.Generated;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.MoveInputEvent;

public final class MovementInputUtils implements MinecraftAccess {
   public static boolean method7418() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F || mc.player.input.keyPresses.jump();
   }

   public static double[] method7419(double d, float yRot) {
      float f = ViaCompat.method4846(mc.player.input);
      float f2 = ViaCompat.method4847(mc.player.input);
      if (f != 0.0F) {
         if (f2 > 0.0F) {
            yRot += f > 0.0F ? -25.0F : 45.0F;
         } else if (f2 < 0.0F) {
            yRot += f > 0.0F ? 25.0F : -25.0F;
         }

         f2 = 0.0F;
         f = f > 0.0F ? 1.0F : -1.0F;
      }

      double d2 = Math.sin(Math.toRadians(yRot + 90.0F));
      double d3 = Math.cos(Math.toRadians(yRot + 90.0F));
      double d4 = f * d * d3 + f2 * d * d2;
      double d5 = f * d * d2 - f2 * d * d3;
      return new double[]{d4, d5};
   }

   public static double[] method7420(double d, float f, Vec3 vec, double d2) {
      float f2 = ViaCompat.method4846(mc.player.input);
      float f3 = ViaCompat.method4847(mc.player.input);
      double x = vec.x;
      double z = vec.z;
      if (d2 < 1.0E-8) {
         return new double[]{0.0, 0.0};
      } else {
         if (f2 != 0.0F) {
            if (f3 > 0.0F) {
               f += f2 > 0.0F ? -45.0F : 45.0F;
            } else if (f3 < 0.0F) {
               f += f2 > 0.0F ? 45.0F : -45.0F;
            }

            f3 = 0.0F;
            f2 = f2 > 0.0F ? 1.0F : -1.0F;
         }

         double d3 = Math.sin(Math.toRadians(f + 90.0F));
         double d4 = Math.cos(Math.toRadians(f + 90.0F));
         double d5 = f2 * d * d4 + f3 * d * d3;
         double d6 = f2 * d * d3 - f3 * d * d4;
         double d7 = x - d5;
         double d8 = z - d6;
         if (Math.signum(x) != Math.signum(d7)) {
            double d9 = Math.abs(x) / (Math.abs(d5) + 1.0E-8);
            d5 *= d9 * 0.9;
         }

         if (Math.signum(z) != Math.signum(d8)) {
            double d9 = Math.abs(z) / (Math.abs(d6) + 1.0E-8);
            d6 *= d9 * 0.9;
         }

         return new double[]{d5, d6, 1.0};
      }
   }

   public static double[] method7421(double d) {
      return method7419(d, mc.player.getYRot());
   }

   public static void method7422(double d) {
      double[] doubles = method7421(d);
      Objects.requireNonNull(mc.player).setDeltaMovement(doubles[0], mc.player.getDeltaMovement().y(), doubles[1]);
   }

   public static void method7423(double d, double yd) {
      double[] doubles = method7421(d);
      Objects.requireNonNull(mc.player).setDeltaMovement(doubles[0], yd, doubles[1]);
   }

   public static double method7424(Vec3 vec, float yRot) {
      float f = (float)Math.atan2(-vec.x, vec.z);
      double d = Math.toRadians(Mth.wrapDegrees(yRot));
      return Math.toDegrees(Mth.wrapDegrees(f - d));
   }

   public static Input method7425(Input input, double d, float f) {
      boolean forward = input.forward();
      boolean backward = input.backward();
      boolean left = input.left();
      boolean right = input.right();
      if (d >= -90.0F + f && d <= 90.0F - f) {
         forward = true;
      } else if (d < -90.0F - f || d > 90.0F + f) {
         backward = true;
      }

      if (d >= 0.0F + f && d <= 180.0F - f) {
         right = true;
      } else if (d >= -180.0F + f && d <= 0.0F - f) {
         left = true;
      }

      return new Input(forward, backward, left, right, input.jump(), input.shift(), input.sprint());
   }

   public static void method7426(MoveInputEvent event, float floatValue, float yRot) {
      float f = ViaCompat.method4846(mc.player.input);
      float f2 = ViaCompat.method4847(mc.player.input);
      double d = Mth.wrapDegrees(Math.toDegrees(method7427(mc.player.isFallFlying() ? floatValue : yRot, f, f2)));
      if (f != 0.0F || f2 != 0.0F) {
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = Float.MAX_VALUE;

         for (float f6 = -1.0F; f6 <= 1.0F; f6++) {
            for (float f7 = -1.0F; f7 <= 1.0F; f7++) {
               if (f7 != 0.0F || f6 != 0.0F) {
                  double d2 = Mth.wrapDegrees(Math.toDegrees(method7427(floatValue, f6, f7)));
                  double d3 = Math.abs(d - d2);
                  if (d3 < f5) {
                     f5 = (float)d3;
                     f3 = f6;
                     f4 = f7;
                  }
               }
            }
         }

         event.method3042(f3, f4);
      }
   }

   public static double method7427(float yRot, float f, float f2) {
      if (f < 0.0F) {
         yRot += 180.0F;
      }

      float f3 = 1.0F;
      if (f < 0.0F) {
         f3 = -0.5F;
      }

      if (f > 0.0F) {
         f3 = 0.5F;
      }

      if (f2 > 0.0F) {
         yRot -= 90.0F * f3;
      }

      if (f2 < 0.0F) {
         yRot += 90.0F * f3;
      }

      return Math.toRadians(yRot);
   }

   public static Input method7428(Input input, double d) {
      return method7425(input, d, 20.0F);
   }

   public static double method7429(Entity entity) {
      double d = entity.getX() - entity.xo;
      double d2 = entity.getY() - entity.yo;
      double d3 = entity.getZ() - entity.zo;
      return Math.sqrt(d * d + d3 * d3 + d2 * d2);
   }

   @Generated
   private MovementInputUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

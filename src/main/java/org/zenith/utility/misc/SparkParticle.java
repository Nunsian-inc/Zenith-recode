package org.zenith.utility.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.zenith.ZenithColor;

public class SparkParticle extends Particle {
   private static final Minecraft mc = Minecraft.getInstance();
   private static final int CONST_intValue = 10;
   private Vec3 vec3d;
   private float floatValue = 1.0F;

   public SparkParticle(Vec3 vec3d, Vec3 vec3d3, int intValue, float floatValue, ZenithColor zenithColor, String text, float nextFloat, float f) {
      super(vec3d, vec3d3, intValue, floatValue, zenithColor, text, nextFloat, f);
      ThreadLocalRandom current = ThreadLocalRandom.current();
      double d = Math.toRadians(current.nextDouble() * 80.0);
      double d2 = current.nextDouble() * Math.PI * 2.0;
      double d3 = Math.sin(d);
      double y = -Math.cos(d);
      this.vec3d = new Vec3(Math.cos(d2) * d3, y, Math.sin(d2) * d3).normalize();
   }

   public void method1448(Vec3 vec, boolean flag, float scale) {
      this.intValue2 = this.intValue;
      this.method6572(vec);
      if (this.intValue > 0) {
         this.vec3d2 = this.vec3d;
         Vec3 vec2 = this.vec3d3.scale(scale);
         if (mc.level != null) {
            int n = -1;
            Vec3 vec3d = this.vec3d;

            for (int i = 1; i <= 10; i++) {
               vec3d = vec3d.add(vec2);
               if (!mc.level.getBlockState(BlockPos.containing(vec3d)).isAir()) {
                  n = i;
                  break;
               }
            }

            if (n > 0) {
               float f = n / 10.0F;
               this.floatValue = f * f;
               if (n <= 1) {
                  this.intValue = 0;
                  return;
               }
            } else {
               this.floatValue = Math.min(this.floatValue + 0.1F, 1.0F);
            }
         }

         this.vec3d = this.vec3d.add(vec2);
         if (flag) {
            this.vec3d3 = this.vec3d3.scale(0.9);
            this.vec3d3 = new Vec3(this.vec3d3.x * 0.85, this.vec3d3.y - 0.008, this.vec3d3.z * 0.85);
            if (this.vec3d3.y < -0.5) {
               this.vec3d3 = new Vec3(this.vec3d3.x, -0.5, this.vec3d3.z);
            }

            if (this.vec3d.y <= mc.level.getMinY()) {
               this.intValue = 0;
            }
         } else {
            float f = 1.0F - (float)this.intValue / this.intValue3;
            if (f < 0.7F) {
               this.vec3d3 = this.vec3d3.scale(0.995);
            } else {
               this.vec3d3 = this.vec3d3.scale(0.92);
            }

            if (this.vec3d.y <= mc.level.getMinY() || this.vec3d.y > 320.0) {
               this.intValue = 0;
            }
         }

         this.method6570();
      }
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float f) {
      float f2 = this.method6571(f);
      float f3;
      if (f2 < 0.1F) {
         f3 = f2 / 0.1F;
      } else if (f2 > 0.7F) {
         f3 = 1.0F - (f2 - 0.7F) / 0.3F;
      } else {
         f3 = 1.0F;
      }

      return f3 * this.floatValue;
   }
}

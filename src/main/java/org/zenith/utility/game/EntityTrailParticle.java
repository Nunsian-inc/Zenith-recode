package org.zenith.utility.game;

import java.lang.ref.WeakReference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.zenith.ZenithColor;
import org.zenith.utility.misc.Particle;

public class EntityTrailParticle extends Particle {
   private static final Minecraft mc = Minecraft.getInstance();
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private WeakReference<Entity> weakReference;
   private float floatValue;
   private float floatValue2;
   private boolean flag;
   private static final float CONST_floatValue = 0.3F;
   private static final float CONST_floatValue2 = 0.2F;

   public EntityTrailParticle(Vec3 vec3d, Vec3 vec3d3, int intValue, float floatValue, ZenithColor zenithColor, String text, float nextFloat, float f) {
      super(vec3d, vec3d3, intValue, floatValue, zenithColor, text, nextFloat, f);
   }

   public void method5662(Entity referent, float floatValue, float f) {
      this.weakReference = new WeakReference<>(referent);
      this.floatValue = floatValue;
      this.floatValue2 = f;
      Vec3 position = referent.position();
      this.doubleValue = Math.atan2(this.vec3d.z - position.z, this.vec3d.x - position.x);
      this.doubleValue2 = Math.sqrt(Math.pow(this.vec3d.x - position.x, 2.0) + Math.pow(this.vec3d.z - position.z, 2.0));
      this.doubleValue3 = this.vec3d.y - position.y;
   }

   public void method5663(Vec3 vec, int n) {
      this.intValue2 = this.intValue;
      this.method6572(vec);
      if (this.intValue > 0) {
         this.vec3d2 = this.vec3d;
         float f = 1.0F - (float)this.intValue / this.intValue3;
         switch (n) {
            case 1:
               this.method5665(f);
               break;
            case 2:
               this.method5666();
               break;
            default:
               this.method5664();
         }

         this.method6570();
      }
   }

   private void method5664() {
      this.vec3d = this.vec3d.add(this.vec3d3);
      this.vec3d3 = new Vec3(this.vec3d3.x * 0.98, this.vec3d3.y * 0.98 - 3.0E-4, this.vec3d3.z * 0.98);
      if (mc.level != null) {
         BlockPos containing = BlockPos.containing(this.vec3d.x, this.vec3d.y - 0.1, this.vec3d.z);
         if (!mc.level.getBlockState(containing).isAir()) {
            this.vec3d3 = new Vec3(this.vec3d3.x / 1.1, -this.vec3d3.y / 1.1, this.vec3d3.z / 1.1);
         }
      }

      if (this.vec3d.y <= mc.level.getMinY()) {
         this.intValue = 0;
      }
   }

   private void method5665(float f) {
      float scale = (float)Math.sin(f * Math.PI);
      scale = 0.3F + scale * 1.2F;
      this.vec3d = this.vec3d.add(this.vec3d3.scale(scale));
      this.vec3d3 = this.vec3d3.scale(0.96);
   }

   private void method5666() {
      float f = 1.0F - (float)this.intValue / this.intValue3;
      Entity entity = this.weakReference != null ? this.weakReference.get() : null;
      if (entity != null && !entity.isRemoved()) {
         if (!(f > 0.5F) && !this.flag) {
            this.doubleValue = this.doubleValue + this.floatValue / 10.0;
            this.doubleValue2 = this.doubleValue2 + this.floatValue2 / 100.0;
            double x = entity.getX();
            double z = entity.getZ();
            double y = entity.getY();
            double d = x + Math.cos(this.doubleValue) * this.doubleValue2;
            double z2 = z + Math.sin(this.doubleValue) * this.doubleValue2;
            double y2 = y + this.doubleValue3 + this.floatValue * 0.1;
            if (f < 0.3F) {
               this.vec3d = new Vec3(d, y2, z2);
            } else {
               float alpha1 = (f - 0.3F) / 0.2F;
               alpha1 = alpha1 * alpha1 * (3.0F - 2.0F * alpha1);
               if (this.vec3d3.equals(Vec3.ZERO)) {
                  this.method5667();
               }

               Vec3 vec = this.vec3d.add(this.vec3d3);
               this.vec3d3 = new Vec3(this.vec3d3.x * 0.98, this.vec3d3.y * 0.98 - 3.0E-4, this.vec3d3.z * 0.98);
               this.vec3d = new Vec3(
                  Mth.lerp((double)alpha1, d, vec.x), Mth.lerp((double)alpha1, y2, vec.y), Mth.lerp((double)alpha1, z2, vec.z)
               );
            }
         } else {
            if (!this.flag) {
               this.flag = true;
               this.method5667();
            }

            this.method5664();
         }
      } else {
         if (!this.flag) {
            this.flag = true;
            this.method5667();
         }

         this.method5664();
      }
   }

   private void method5667() {
      double d = -Math.sin(this.doubleValue) * this.floatValue * 0.02;
      double d2 = Math.cos(this.doubleValue) * this.floatValue * 0.02;
      double d3 = Math.cos(this.doubleValue) * this.floatValue2 * 0.005;
      double d4 = Math.sin(this.doubleValue) * this.floatValue2 * 0.005;
      this.vec3d3 = new Vec3(d + d3 + (Math.random() - 0.5) * 0.02, 0.015 + Math.random() * 0.025, d2 + d4 + (Math.random() - 0.5) * 0.02);
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float f) {
      float f2 = this.method6571(f);
      if (f2 < 0.1F) {
         return f2 / 0.1F;
      } else {
         float f3 = 0.5F;
         if (!this.flag && !(f2 > f3)) {
            return 1.0F;
         } else {
            float f4 = (f2 - f3) / (1.0F - f3);
            return 1.0F - f4 * f4;
         }
      }
   }
}

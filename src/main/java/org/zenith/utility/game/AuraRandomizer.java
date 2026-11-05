package org.zenith.utility.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import lombok.Generated;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Vec2f;

public class AuraRandomizer implements MinecraftAccess {
   private static final double CONST_doubleValue = 0.15;
   private static final int CONST_intValue = 14;
   private static final int CONST_intValue2 = 14;
   private final Random random = new SecureRandom();
   private Vec3 vec3d = Vec3.ZERO;

   public Vec3 method4673(Vec3 eyePosition, AABB boundingBox, float f, Vec3 vec, boolean enabled) {
      var vecs = this.method4675(eyePosition, boundingBox, f, enabled);
      var vecs2 = this.method4682(vecs, f);
      Vec3 vec2 = this.method4681(eyePosition, vecs2, f, enabled);
      if (vec2 == null) {
         vec2 = this.method4681(eyePosition, vecs, f, enabled);
      }

      if (vec2 == null) {
         vec2 = this.method4685(eyePosition, vecs);
      }

      this.method4686(vec);
      return (vec2 == null ? boundingBox.getCenter() : vec2).add(this.vec3d);
   }

   public Vec3 method4674(Vec3 eyePosition, AABB boundingBox, float f, Vec3 vec, boolean enabled) {
      var vecs = this.method4675(eyePosition, boundingBox, f, enabled);
      var vecs2 = this.method4682(vecs, f);
      Vec3 vec2 = this.method4680(eyePosition, vecs2, f, enabled);
      if (vec2 == null) {
         vec2 = this.method4680(eyePosition, vecs, f, enabled);
      }

      if (vec2 == null) {
         vec2 = this.method4684(eyePosition, vecs);
      }

      this.method4686(vec);
      return (vec2 == null ? boundingBox.getCenter() : vec2).add(this.vec3d);
   }

   public List<Vec3> method4675(Vec3 eyePosition, AABB boundingBox, float f, boolean enabled) {
      if (method4677(eyePosition, boundingBox, f)) {
         return new ArrayList<>();
      } else {
         double xsize = boundingBox.getXsize();
         double ysize = boundingBox.getYsize();
         double zsize = boundingBox.getZsize();
         int n = this.method4688(xsize);
         int n2 = this.method4688(zsize);
         int n3 = Math.max(2, 14);
         double d = ysize / (n3 - 1);
         double minX = boundingBox.minX;
         double minY = boundingBox.minY;
         double minZ = boundingBox.minZ;
         double d2 = n <= 1 ? 0.0 : xsize / (n - 1);
         double d3 = n2 <= 1 ? 0.0 : zsize / (n2 - 1);
         List<Vec3> vecs = new ArrayList<>(n3 * n * n2);
         double d4 = (double)f * f;

         for (int i = 0; i < n3; i++) {
            double y = minY + i * d;

            for (int j = 0; j < n; j++) {
               double x = minX + j * d2;

               for (int k = 0; k < n2; k++) {
                  double z = minZ + k * d3;
                  if (!(method4678(eyePosition, x, y, z) > d4)) {
                     Vec3 vec = new Vec3(x, y, z);
                     if (this.method6838(eyePosition, vec, f, enabled)) {
                        vecs.add(vec);
                     }
                  }
               }
            }
         }

         return vecs;
      }
   }

   public boolean method4676(LivingEntity living, float f, boolean enabled) {
      AABB boundingBox = living.getBoundingBox();
      Vec3 eyePosition = mc.player.getEyePosition();
      if (method4677(eyePosition, boundingBox, f)) {
         return false;
      } else {
         double xsize = boundingBox.getXsize();
         double ysize = boundingBox.getYsize();
         double zsize = boundingBox.getZsize();
         int n = this.method4688(xsize);
         int n2 = this.method4688(zsize);
         int n3 = Math.max(2, 14);
         double d = ysize / (n3 - 1);
         double minX = boundingBox.minX;
         double minY = boundingBox.minY;
         double minZ = boundingBox.minZ;
         double d2 = n <= 1 ? 0.0 : xsize / (n - 1);
         double d3 = n2 <= 1 ? 0.0 : zsize / (n2 - 1);
         double d4 = (double)f * f;

         for (int i = 0; i < n3; i++) {
            double y = minY + i * d;

            for (int j = 0; j < n; j++) {
               double x = minX + j * d2;

               for (int k = 0; k < n2; k++) {
                  double z = minZ + k * d3;
                  if (!(method4678(eyePosition, x, y, z) > d4)) {
                     Vec3 vec = new Vec3(x, y, z);
                     if (this.method6838(eyePosition, vec, f, enabled)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   private static boolean method4677(Vec3 eyePosition, AABB boundingBox, float f) {
      double d = Math.max(0.0, Math.max(boundingBox.minX - eyePosition.x, eyePosition.x - boundingBox.maxX));
      double d2 = Math.max(0.0, Math.max(boundingBox.minY - eyePosition.y, eyePosition.y - boundingBox.maxY));
      double d3 = Math.max(0.0, Math.max(boundingBox.minZ - eyePosition.z, eyePosition.z - boundingBox.maxZ));
      double d4 = Math.abs((double)f) + 1.0E-4;
      return d * d + d2 * d2 + d3 * d3 > d4 * d4;
   }

   private static double method4678(Vec3 eyePosition, double x, double y, double z) {
      double d = x - eyePosition.x;
      double d2 = y - eyePosition.y;
      double d3 = z - eyePosition.z;
      return d * d + d2 * d2 + d3 * d3;
   }

   private boolean method6838(Vec3 eyePosition, Vec3 vec, float f, boolean enabled) {
      if (eyePosition.distanceToSqr(vec) > (double)f * f) {
         return false;
      } else if (enabled) {
         return true;
      } else {
         ClipContext clipContext = new ClipContext(eyePosition, vec, Block.COLLIDER, Fluid.NONE, mc.player);
         BlockHitResult blockHitResult = TargetUtils.method3217(clipContext, AuraModule.INSTANCE::method6362);
         return blockHitResult.getType() != Type.BLOCK;
      }
   }

   private Vec3 method4680(Vec3 eyePosition, List<Vec3> vecs, float f, boolean enabled) {
      if (vecs != null && !vecs.isEmpty()) {
         Vec3 vec = this.method4683(vecs);
         return this.method6838(eyePosition, vec, f, enabled)
            ? vec
            : vecs.stream().filter(var4x -> this.method6838(eyePosition, var4x, f, enabled)).min(Comparator.comparingDouble(var1x -> {
               Vec2f var2x = Zenith.getInstance().getRotationManager().method7844().method3131(Rotation.method3128(var1x, eyePosition));
               return Math.abs(var2x.getFloatValue()) + Math.abs(var2x.getFloatValue2());
            })).orElse(null);
      } else {
         return null;
      }
   }

   private Vec3 method4681(Vec3 eyePosition, List<Vec3> vecs, float f, boolean enabled) {
      if (vecs != null && !vecs.isEmpty()) {
         Vec3 vec = this.method4683(vecs);
         return this.method6838(eyePosition, vec, f, enabled)
            ? vec
            : vecs.stream()
               .filter(var4x -> this.method6838(eyePosition, var4x, f, enabled))
               .min(Comparator.comparingDouble(var1x -> var1x.distanceToSqr(vec)))
               .orElse(null);
      } else {
         return null;
      }
   }

   private List<Vec3> method4682(List<Vec3> vecs, float f) {
      if (vecs != null && !vecs.isEmpty()) {
         Vec3 eyePosition = mc.player.getEyePosition();
         double d = Math.max(0.0, f - 0.3);
         double d2 = d * d;
         ArrayList<Vec3> vecs2 = new ArrayList<>();

         for (Vec3 vec : vecs) {
            if (eyePosition.distanceToSqr(vec) < d2) {
               vecs2.add(vec);
            }
         }

         return vecs2;
      } else {
         return List.of();
      }
   }

   private Vec3 method4683(List<Vec3> vecs) {
      double d = 0.0;
      double d2 = 0.0;
      double d3 = 0.0;
      int size = vecs.size();

      for (Vec3 vec : vecs) {
         d += vec.x;
         d2 += vec.y;
         d3 += vec.z;
      }

      return new Vec3(d / size, d2 / size, d3 / size);
   }

   private Vec3 method4684(Vec3 eyePosition, List<Vec3> vecs) {
      return vecs != null && !vecs.isEmpty() ? vecs.stream().min(Comparator.comparingDouble(var1x -> {
         Vec2f var2x = Zenith.getInstance().getRotationManager().method7844().method3131(Rotation.method3128(var1x, eyePosition));
         return Math.abs(var2x.getFloatValue()) + Math.abs(var2x.getFloatValue2());
      })).orElse(null) : null;
   }

   private Vec3 method4685(Vec3 eyePosition, List<Vec3> vecs) {
      return vecs != null && !vecs.isEmpty() ? vecs.stream().min(Comparator.comparingDouble(var1x -> var1x.distanceToSqr(eyePosition))).orElse(null) : null;
   }

   private void method4686(Vec3 vec) {
      this.vec3d = this.vec3d.add(this.random.nextGaussian(), this.random.nextGaussian(), this.random.nextGaussian()).multiply(vec);
   }

   private double method4687(Vec3 vec, Vec3 vec2, Rotation rotation) {
      if (rotation == null) {
         return Double.POSITIVE_INFINITY;
      } else {
         Rotation rotation2 = RotationUtils.method6912(vec2.subtract(vec));
         Vec2f vec2f = rotation.method3131(rotation2);
         return Math.hypot(vec2f.getFloatValue(), vec2f.getFloatValue2());
      }
   }

   private int method4688(double xsize) {
      if (xsize <= 0.0) {
         return 1;
      } else {
         int n = (int)Math.ceil(xsize / 0.15) + 1;
         int n2 = Math.min(n, 14);
         return Math.max(2, n2);
      }
   }

   @Generated
   public Random getRandom() {
      return this.random;
   }

   @Generated
   public Vec3 getVec3d() {
      return this.vec3d;
   }
}

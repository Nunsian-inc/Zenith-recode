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

   public Vec3 method4673(Vec3 var1, AABB var2, float var3, Vec3 var4, boolean var5) {
      var var6 = this.method4675(var1, var2, var3, var5);
      var var7 = this.method4682(var6, var3);
      Vec3 var8 = this.method4681(var1, var7, var3, var5);
      if (var8 == null) {
         var8 = this.method4681(var1, var6, var3, var5);
      }

      if (var8 == null) {
         var8 = this.method4685(var1, var6);
      }

      this.method4686(var4);
      return (var8 == null ? var2.getCenter() : var8).add(this.vec3d);
   }

   public Vec3 method4674(Vec3 var1, AABB var2, float var3, Vec3 var4, boolean var5) {
      var var6 = this.method4675(var1, var2, var3, var5);
      var var7 = this.method4682(var6, var3);
      Vec3 var8 = this.method4680(var1, var7, var3, var5);
      if (var8 == null) {
         var8 = this.method4680(var1, var6, var3, var5);
      }

      if (var8 == null) {
         var8 = this.method4684(var1, var6);
      }

      this.method4686(var4);
      return (var8 == null ? var2.getCenter() : var8).add(this.vec3d);
   }

   public List<Vec3> method4675(Vec3 var1, AABB var2, float var3, boolean var4) {
      if (method4677(var1, var2, var3)) {
         return new ArrayList<>();
      } else {
         double var5 = var2.getXsize();
         double var7 = var2.getYsize();
         double var9 = var2.getZsize();
         int var11 = this.method4688(var5);
         int var12 = this.method4688(var9);
         int var13 = Math.max(2, 14);
         double var14 = var7 / (var13 - 1);
         double var16 = var2.minX;
         double var18 = var2.minY;
         double var20 = var2.minZ;
         double var22 = var11 <= 1 ? 0.0 : var5 / (var11 - 1);
         double var24 = var12 <= 1 ? 0.0 : var9 / (var12 - 1);
         List<Vec3> var26 = new ArrayList<>(var13 * var11 * var12);
         double var27 = (double)var3 * var3;

         for (int var29 = 0; var29 < var13; var29++) {
            double var30 = var18 + var29 * var14;

            for (int var32 = 0; var32 < var11; var32++) {
               double var33 = var16 + var32 * var22;

               for (int var35 = 0; var35 < var12; var35++) {
                  double var36 = var20 + var35 * var24;
                  if (!(method4678(var1, var33, var30, var36) > var27)) {
                     Vec3 var38 = new Vec3(var33, var30, var36);
                     if (this.method6838(var1, var38, var3, var4)) {
                        var26.add(var38);
                     }
                  }
               }
            }
         }

         return var26;
      }
   }

   public boolean method4676(LivingEntity var1, float var2, boolean var3) {
      AABB var4 = var1.getBoundingBox();
      Vec3 var5 = mc.player.getEyePosition();
      if (method4677(var5, var4, var2)) {
         return false;
      } else {
         double var6 = var4.getXsize();
         double var8 = var4.getYsize();
         double var10 = var4.getZsize();
         int var12 = this.method4688(var6);
         int var13 = this.method4688(var10);
         int var14 = Math.max(2, 14);
         double var15 = var8 / (var14 - 1);
         double var17 = var4.minX;
         double var19 = var4.minY;
         double var21 = var4.minZ;
         double var23 = var12 <= 1 ? 0.0 : var6 / (var12 - 1);
         double var25 = var13 <= 1 ? 0.0 : var10 / (var13 - 1);
         double var27 = (double)var2 * var2;

         for (int var29 = 0; var29 < var14; var29++) {
            double var30 = var19 + var29 * var15;

            for (int var32 = 0; var32 < var12; var32++) {
               double var33 = var17 + var32 * var23;

               for (int var35 = 0; var35 < var13; var35++) {
                  double var36 = var21 + var35 * var25;
                  if (!(method4678(var5, var33, var30, var36) > var27)) {
                     Vec3 var38 = new Vec3(var33, var30, var36);
                     if (this.method6838(var5, var38, var2, var3)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   private static boolean method4677(Vec3 var0, AABB var1, float var2) {
      double var3 = Math.max(0.0, Math.max(var1.minX - var0.x, var0.x - var1.maxX));
      double var5 = Math.max(0.0, Math.max(var1.minY - var0.y, var0.y - var1.maxY));
      double var7 = Math.max(0.0, Math.max(var1.minZ - var0.z, var0.z - var1.maxZ));
      double var9 = Math.abs((double)var2) + 1.0E-4;
      return var3 * var3 + var5 * var5 + var7 * var7 > var9 * var9;
   }

   private static double method4678(Vec3 var0, double var1, double var3, double var5) {
      double var7 = var1 - var0.x;
      double var9 = var3 - var0.y;
      double var11 = var5 - var0.z;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   private boolean method6838(Vec3 var1, Vec3 var2, float var3, boolean var4) {
      if (var1.distanceToSqr(var2) > (double)var3 * var3) {
         return false;
      } else if (var4) {
         return true;
      } else {
         ClipContext var5 = new ClipContext(var1, var2, Block.COLLIDER, Fluid.NONE, mc.player);
         BlockHitResult var6 = TargetUtils.method3217(var5, AuraModule.INSTANCE::method6362);
         return var6.getType() != Type.BLOCK;
      }
   }

   private Vec3 method4680(Vec3 var1, List<Vec3> var2, float var3, boolean var4) {
      if (var2 != null && !var2.isEmpty()) {
         Vec3 var5 = this.method4683(var2);
         return this.method6838(var1, var5, var3, var4)
            ? var5
            : var2.stream().filter(var4x -> this.method6838(var1, var4x, var3, var4)).min(Comparator.comparingDouble(var1x -> {
               Vec2f var2x = Zenith.getInstance().getRotationManager().method7844().method3131(Rotation.method3128(var1x, var1));
               return Math.abs(var2x.getFloatValue()) + Math.abs(var2x.getFloatValue2());
            })).orElse(null);
      } else {
         return null;
      }
   }

   private Vec3 method4681(Vec3 var1, List<Vec3> var2, float var3, boolean var4) {
      if (var2 != null && !var2.isEmpty()) {
         Vec3 var5 = this.method4683(var2);
         return this.method6838(var1, var5, var3, var4)
            ? var5
            : var2.stream()
               .filter(var4x -> this.method6838(var1, var4x, var3, var4))
               .min(Comparator.comparingDouble(var1x -> var1x.distanceToSqr(var5)))
               .orElse(null);
      } else {
         return null;
      }
   }

   private List<Vec3> method4682(List<Vec3> var1, float var2) {
      if (var1 != null && !var1.isEmpty()) {
         Vec3 var3 = mc.player.getEyePosition();
         double var4 = Math.max(0.0, var2 - 0.3);
         double var6 = var4 * var4;
         ArrayList<Vec3> var8 = new ArrayList<>();

         for (Vec3 var10 : var1) {
            if (var3.distanceToSqr(var10) < var6) {
               var8.add(var10);
            }
         }

         return var8;
      } else {
         return List.of();
      }
   }

   private Vec3 method4683(List<Vec3> var1) {
      double var2 = 0.0;
      double var4 = 0.0;
      double var6 = 0.0;
      int var8 = var1.size();

      for (Vec3 var10 : var1) {
         var2 += var10.x;
         var4 += var10.y;
         var6 += var10.z;
      }

      return new Vec3(var2 / var8, var4 / var8, var6 / var8);
   }

   private Vec3 method4684(Vec3 var1, List<Vec3> var2) {
      return var2 != null && !var2.isEmpty() ? var2.stream().min(Comparator.comparingDouble(var1x -> {
         Vec2f var2x = Zenith.getInstance().getRotationManager().method7844().method3131(Rotation.method3128(var1x, var1));
         return Math.abs(var2x.getFloatValue()) + Math.abs(var2x.getFloatValue2());
      })).orElse(null) : null;
   }

   private Vec3 method4685(Vec3 var1, List<Vec3> var2) {
      return var2 != null && !var2.isEmpty() ? var2.stream().min(Comparator.comparingDouble(var1x -> var1x.distanceToSqr(var1))).orElse(null) : null;
   }

   private void method4686(Vec3 var1) {
      this.vec3d = this.vec3d.add(this.random.nextGaussian(), this.random.nextGaussian(), this.random.nextGaussian()).multiply(var1);
   }

   private double method4687(Vec3 var1, Vec3 var2, Rotation var3) {
      if (var3 == null) {
         return Double.POSITIVE_INFINITY;
      } else {
         Rotation var4 = RotationUtils.method6912(var2.subtract(var1));
         Vec2f var5 = var3.method3131(var4);
         return Math.hypot(var5.getFloatValue(), var5.getFloatValue2());
      }
   }

   private int method4688(double var1) {
      if (var1 <= 0.0) {
         return 1;
      } else {
         int var3 = (int)Math.ceil(var1 / 0.15) + 1;
         int var4 = Math.min(var3, 14);
         return Math.max(2, var4);
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

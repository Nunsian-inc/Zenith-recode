package org.zenith.utility.render;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.WardrobeSetInfo;

public final class Util19 {
   private static final Minecraft mc = Minecraft.getInstance();
   private static final RandomSource random = RandomSource.create();
   private static final Vector3f vector3f = new Vector3f();
   private static final Matrix4f matrix4f = new Matrix4f();
   private static float floatValue;
   private static long longValue;

   private Util19() {
   }

   public static boolean enabled() {
      Wardrobe wardrobe = Wardrobe.method2488();
      return wardrobe != null
         && wardrobe.particles.isEnabled()
         && !wardrobe.isFlag()
         && mc.level != null
         && mc.options != null
         && mc.options.particles().get().ordinal() < 2;
   }

   public static boolean method5042() {
      Wardrobe wardrobe = Wardrobe.method2488();
      long nanoTime = System.nanoTime();
      float f = wardrobe == null ? 60.0F : wardrobe.particleBudget.getValue3();
      if (longValue == 0L) {
         longValue = nanoTime;
         floatValue = f * 0.25F;
      }

      floatValue = Math.min(f * 0.5F, floatValue + (float)(nanoTime - longValue) / 1.0E9F * f);
      longValue = nanoTime;
      if (floatValue < 1.0F) {
         return false;
      } else {
         floatValue--;
         return true;
      }
   }

   public static void method5043(ParticleOptions particleEffect, double x, double y, double z, double xd, double yd, double zd) {
      if (mc.level != null && particleEffect != null && method5042()) {
         mc.level.addParticle(particleEffect, x, y, z, xd, yd, zd);
      }
   }

   public static void method5044(
      Class15 key,
      AnimationCache.Entry entry,
      AvatarRenderState state,
      PlayerModel parentModel,
      float[] floats,
      float f,
      boolean flag,
      boolean flag2,
      WardrobeSetInfo wardrobeSetInfo,
      List<Class15.Data4> data4s
   ) {
      if (!data4s.isEmpty() && enabled() && !(state.distanceToCameraSq > 2304.0)) {
         float[] floats2 = entry.method7625(data4s.size());

         for (int i = 0; i < data4s.size(); i++) {
            Class15.Data4 data4 = (Class15.Data4)data4s.get(i);
            if (!method5046(data4.getType3(), flag, flag2, state.isCrouching, wardrobeSetInfo != null)) {
               floats2[i] = 0.0F;
            } else {
               floats2[i] += data4.getFloatValue4() * f;

               while (floats2[i] >= 1.0F) {
                  floats2[i]--;
                  matrix4f.identity();
                  matrix4f.translate((float)state.x, (float)state.y, (float)state.z);
                  matrix4f.rotateY((float)Math.toRadians(180.0 - state.bodyRot));
                  matrix4f.scale(state.scale);
                  matrix4f.scale(-1.0F, -1.0F, 1.0F);
                  matrix4f.translate(0.0F, -1.501F, 0.0F);
                  if (data4.getIntValue() >= 0) {
                     int intValue = data4.getIntValue();

                     while (key.intValues[intValue] >= 0) {
                        intValue = key.intValues[intValue];
                     }

                     ModelPart root = method5047(parentModel, key.intValues2[intValue]);
                     if (root != null) {
                        method5048(parentModel.root(), matrix4f);
                        method5048(root, matrix4f);
                     }

                     BoneMatrices.method4808(key, floats, data4.getIntValue(), matrix4f);
                  }

                  matrix4f.transformPosition(data4.getFloatValue() / 16.0F, data4.getFloatValue2() / 16.0F, data4.getFloatValue3() / 16.0F, vector3f);
                  double d = data4.getFloatValue8();
                  method5043(
                     data4.getParticleEffect(),
                     vector3f.x + (random.nextDouble() - 0.5) * d,
                     vector3f.y + (random.nextDouble() - 0.5) * d,
                     vector3f.z + (random.nextDouble() - 0.5) * d,
                     data4.getFloatValue5() + (random.nextDouble() - 0.5) * d * 0.2,
                     data4.getFloatValue6(),
                     data4.getFloatValue7() + (random.nextDouble() - 0.5) * d * 0.2
                  );
               }
            }
         }
      }
   }

   public static void method5045(WardrobeSetInfo wardrobeSetInfo, AvatarRenderState state, AnimationCache.Entry entry, float f, ParticleOptions particleEffect) {
      if (wardrobeSetInfo != null && particleEffect != null && enabled() && !(state.distanceToCameraSq > 2304.0)) {
         entry.floatValue += 5.0F * f;

         while (entry.floatValue >= 1.0F) {
            entry.floatValue--;
            double d = random.nextDouble() * Math.PI * 2.0;
            double d2 = 0.55 + random.nextDouble() * 0.15;
            method5043(particleEffect, state.x + Math.cos(d) * d2, state.y + 0.1 + random.nextDouble() * 1.7, state.z + Math.sin(d) * d2, 0.0, 0.015, 0.0);
         }
      }
   }

   private static boolean method5046(Class15.Type3 type3, boolean flag, boolean flag2, boolean isCrouching, boolean flag3) {
      return switch (type3) {
         case ALWAYS -> true;
         case MOVING -> flag;
         case IDLE -> !flag;
         case AIRBORNE -> flag2;
         case SNEAKING -> isCrouching;
         case SET_BONUS -> flag3;
      };
   }

   public static ModelPart method5047(PlayerModel parentModel, int n) {
      return switch (n) {
         case 0 -> parentModel.head;
         case 1 -> parentModel.body;
         case 2 -> parentModel.rightArm;
         case 3 -> parentModel.leftArm;
         case 4 -> parentModel.rightLeg;
         case 5 -> parentModel.leftLeg;
         default -> null;
      };
   }

   private static void method5048(ModelPart root, Matrix4f matrix4f) {
      matrix4f.translate(root.x / 16.0F, root.y / 16.0F, root.z / 16.0F);
      if (root.xRot != 0.0F || root.yRot != 0.0F || root.zRot != 0.0F) {
         matrix4f.rotateZYX(root.zRot, root.yRot, root.xRot);
      }

      if (root.xScale != 1.0F || root.yScale != 1.0F || root.zScale != 1.0F) {
         matrix4f.scale(root.xScale, root.yScale, root.zScale);
      }
   }
}

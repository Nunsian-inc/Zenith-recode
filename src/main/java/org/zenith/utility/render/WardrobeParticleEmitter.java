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
import org.zenith.utility.misc.WardrobeModel;
import org.zenith.utility.misc.WardrobeSetInfo;

public final class WardrobeParticleEmitter {
   private static final Minecraft mc = Minecraft.getInstance();
   private static final RandomSource random = RandomSource.create();
   private static final Vector3f particlePosition = new Vector3f();
   private static final Matrix4f particleTransform = new Matrix4f();
   private static float particleTokens;
   private static long lastBudgetUpdateNanos;

   private WardrobeParticleEmitter() {
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

   public static boolean tryConsumeParticleBudget() {
      Wardrobe wardrobe = Wardrobe.method2488();
      long nowNanos = System.nanoTime();
      float particlesPerSecond = wardrobe == null ? 60.0F : wardrobe.particleBudget.getValue3();
      if (lastBudgetUpdateNanos == 0L) {
         lastBudgetUpdateNanos = nowNanos;
         particleTokens = particlesPerSecond * 0.25F;
      }

      particleTokens = Math.min(particlesPerSecond * 0.5F, particleTokens + (float)(nowNanos - lastBudgetUpdateNanos) / 1.0E9F * particlesPerSecond);
      lastBudgetUpdateNanos = nowNanos;
      if (particleTokens < 1.0F) {
         return false;
      } else {
         particleTokens--;
         return true;
      }
   }

   public static void spawnParticle(ParticleOptions particleEffect, double x, double y, double z, double xd, double yd, double zd) {
      if (mc.level != null && particleEffect != null && tryConsumeParticleBudget()) {
         mc.level.addParticle(particleEffect, x, y, z, xd, yd, zd);
      }
   }

   public static void emitModelParticles(
      WardrobeModel modelDefinition,
      AnimationCache.Entry entry,
      AvatarRenderState state,
      PlayerModel parentModel,
      float[] boneTransforms,
      float deltaSeconds,
      boolean moving,
      boolean airborne,
      WardrobeSetInfo setInfo,
      List<WardrobeModel.Data4> emitters
   ) {
      if (!emitters.isEmpty() && enabled() && !(state.distanceToCameraSq > 2304.0)) {
         float[] emissionAccumulators = entry.method7625(emitters.size());

         for (int emitterIndex = 0; emitterIndex < emitters.size(); emitterIndex++) {
            WardrobeModel.Data4 emitter = (WardrobeModel.Data4)emitters.get(emitterIndex);
            if (!matchesEmissionCondition(emitter.getType3(), moving, airborne, state.isCrouching, setInfo != null)) {
               emissionAccumulators[emitterIndex] = 0.0F;
            } else {
               emissionAccumulators[emitterIndex] += emitter.getFloatValue4() * deltaSeconds;

               while (emissionAccumulators[emitterIndex] >= 1.0F) {
                  emissionAccumulators[emitterIndex]--;
                  particleTransform.identity();
                  particleTransform.translate((float)state.x, (float)state.y, (float)state.z);
                  particleTransform.rotateY((float)Math.toRadians(180.0 - state.bodyRot));
                  particleTransform.scale(state.scale);
                  particleTransform.scale(-1.0F, -1.0F, 1.0F);
                  particleTransform.translate(0.0F, -1.501F, 0.0F);
                  if (emitter.getIntValue() >= 0) {
                     int rootBoneIndex = emitter.getIntValue();

                     while (modelDefinition.intValues[rootBoneIndex] >= 0) {
                        rootBoneIndex = modelDefinition.intValues[rootBoneIndex];
                     }

                     ModelPart attachmentPart = getPlayerModelPart(parentModel, modelDefinition.intValues2[rootBoneIndex]);
                     if (attachmentPart != null) {
                        applyModelPartTransform(parentModel.root(), particleTransform);
                        applyModelPartTransform(attachmentPart, particleTransform);
                     }

                     BoneMatrices.method4808(modelDefinition, boneTransforms, emitter.getIntValue(), particleTransform);
                  }

                  particleTransform.transformPosition(emitter.getFloatValue() / 16.0F, emitter.getFloatValue2() / 16.0F, emitter.getFloatValue3() / 16.0F, particlePosition);
                  double spread = emitter.getFloatValue8();
                  spawnParticle(
                     emitter.getParticleEffect(),
                     particlePosition.x + (random.nextDouble() - 0.5) * spread,
                     particlePosition.y + (random.nextDouble() - 0.5) * spread,
                     particlePosition.z + (random.nextDouble() - 0.5) * spread,
                     emitter.getFloatValue5() + (random.nextDouble() - 0.5) * spread * 0.2,
                     emitter.getFloatValue6(),
                     emitter.getFloatValue7() + (random.nextDouble() - 0.5) * spread * 0.2
                  );
               }
            }
         }
      }
   }

   public static void emitSetBonusParticles(WardrobeSetInfo setInfo, AvatarRenderState state, AnimationCache.Entry entry, float deltaSeconds, ParticleOptions particleEffect) {
      if (setInfo != null && particleEffect != null && enabled() && !(state.distanceToCameraSq > 2304.0)) {
         entry.floatValue += 5.0F * deltaSeconds;

         while (entry.floatValue >= 1.0F) {
            entry.floatValue--;
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = 0.55 + random.nextDouble() * 0.15;
            spawnParticle(particleEffect, state.x + Math.cos(angle) * radius, state.y + 0.1 + random.nextDouble() * 1.7, state.z + Math.sin(angle) * radius, 0.0, 0.015, 0.0);
         }
      }
   }

   private static boolean matchesEmissionCondition(WardrobeModel.Type3 condition, boolean moving, boolean airborne, boolean isCrouching, boolean hasSetBonus) {
      return switch (condition) {
         case ALWAYS -> true;
         case MOVING -> moving;
         case IDLE -> !moving;
         case AIRBORNE -> airborne;
         case SNEAKING -> isCrouching;
         case SET_BONUS -> hasSetBonus;
      };
   }

   public static ModelPart getPlayerModelPart(PlayerModel parentModel, int bodyPartIndex) {
      return switch (bodyPartIndex) {
         case 0 -> parentModel.head;
         case 1 -> parentModel.body;
         case 2 -> parentModel.rightArm;
         case 3 -> parentModel.leftArm;
         case 4 -> parentModel.rightLeg;
         case 5 -> parentModel.leftLeg;
         default -> null;
      };
   }

   private static void applyModelPartTransform(ModelPart modelPart, Matrix4f particleTransform) {
      particleTransform.translate(modelPart.x / 16.0F, modelPart.y / 16.0F, modelPart.z / 16.0F);
      if (modelPart.xRot != 0.0F || modelPart.yRot != 0.0F || modelPart.zRot != 0.0F) {
         particleTransform.rotateZYX(modelPart.zRot, modelPart.yRot, modelPart.xRot);
      }

      if (modelPart.xScale != 1.0F || modelPart.yScale != 1.0F || modelPart.zScale != 1.0F) {
         particleTransform.scale(modelPart.xScale, modelPart.yScale, modelPart.zScale);
      }
   }
}

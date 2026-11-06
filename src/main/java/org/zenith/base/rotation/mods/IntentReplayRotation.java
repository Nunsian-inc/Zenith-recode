package org.zenith.base.rotation.mods;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.IntentLibraryRt;
import org.zenith.utility.api.IntentSelector;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.ReplayState;
import org.zenith.utility.misc.RotationContext;
import org.zenith.utility.misc.Vec2f;

public final class IntentReplayRotation extends RotationContext {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final float CONST_floatValue = 0.21217355F;
   private final ReplayState replayState = method3519();
   private int intValue = Integer.MIN_VALUE;
   private boolean flag = false;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private boolean flag2 = false;
   private float floatValue3 = 0.0F;
   private float floatValue4 = 0.0F;
   private Vec3 vec3d = null;
   private AABB box = null;

   private static ReplayState method3519() {
      IntentSelector intentSelector = IntentSelector.method7321();
      IntentLibraryRt intentLibraryRt = IntentLibraryRt.method1382();
      if (intentSelector != null && intentLibraryRt != null) {
         return new ReplayState(intentSelector, intentLibraryRt);
      } else {
         System.err.println("[MotorIntent] artifacts not found; TestRotationModeV2 disabled");
         return null;
      }
   }

   public Rotation method3520(Rotation rotation) {
      Rotation rotation2 = rotationManager.getEffectiveRotation();
      if (this.replayState == null) {
         return rotation2;
      } else if (rotation != null && !rotation.isInvalid()) {
         LivingEntity livingEntity = this.method3527();
         if (livingEntity != null && livingEntity.isAlive()) {
            AABB boundingBox = livingEntity.getBoundingBox();
            if (boundingBox == null) {
               this.method3532();
               return rotation2;
            } else {
               int id = livingEntity.getId();
               if (this.intValue != id) {
                  this.method3532();
                  this.intValue = id;
               }

               float floatValue = rotation2.getFloatValue();
               float f = rotation2.getFloatValue2();
               if (!this.flag) {
                  this.floatValue = floatValue;
                  this.floatValue2 = f;
                  this.flag = true;
                  return rotation2;
               } else {
                  float f2 = Mth.wrapDegrees(floatValue - this.floatValue);
                  float f3 = f - this.floatValue2;
                  int n = Math.round(f2 / 0.21217355F);
                  int n2 = Math.round(f3 / 0.21217355F);
                  this.floatValue = floatValue;
                  this.floatValue2 = f;
                  Vec3 eyePosition = mc.player.getEyePosition();
                  Vec3 position = mc.player.position();
                  Vec3 vec = this.method3529(livingEntity, boundingBox);
                  Vec3 vec2 = this.method3528(position);
                  ReplayState.Entry entry = this.method3522(rotation2, eyePosition, livingEntity, boundingBox, vec, vec2, n, n2);
                  this.vec3d = position;
                  this.box = boundingBox;
                  if (entry == null) {
                     this.flag2 = false;
                     this.replayState.reset();
                     return rotation2;
                  } else {
                     this.flag2 = true;
                     this.floatValue3 = (float)entry.doubleValue;
                     this.floatValue4 = (float)entry.doubleValue2;
                     int[] ints = this.replayState.method7983(entry);
                     return ints[0] == 0 && ints[1] == 0 ? rotation2 : rotation2.add(ints[0] * 0.21217355F, ints[1] * 0.21217355F);
                  }
               }
            }
         } else {
            mc.player.setYRot(rotationManager.getEffectiveRotation().getFloatValue());
            mc.player.setXRot(rotationManager.getEffectiveRotation().getFloatValue2());
            this.method3532();
            return rotation2;
         }
      } else {
         this.method3532();
         return rotation2;
      }
   }

   public String method3521() {
      return this.replayState == null ? "unavailable" : this.replayState.getText();
   }

   private ReplayState.Entry method3522(Rotation rotation, Vec3 eyePosition, LivingEntity livingEntity, AABB boundingBox, Vec3 vec, Vec3 vec2, int n, int n2) {
      Vec3 vec3 = this.method3530(boundingBox);
      Vec2f vec2f = rotation.wrappedDeltaTo(this.method3524(eyePosition, vec3));
      float floatValue = vec2f.getFloatValue();
      float f = vec2f.getFloatValue2();
      if (this.isFinite(floatValue) && this.isFinite(f)) {
         float floatValue2 = Float.MAX_VALUE;
         float floatValue3 = -Float.MAX_VALUE;
         float f2 = Float.MAX_VALUE;
         float f3 = -Float.MAX_VALUE;

         for (Vec3 vec4 : this.method3523(boundingBox)) {
            Vec2f vec2f2 = rotation.wrappedDeltaTo(this.method3524(eyePosition, vec4));
            float floatValue4 = vec2f2.getFloatValue();
            float floatValue5 = vec2f2.getFloatValue2();
            if (this.isFinite(floatValue4) && this.isFinite(floatValue5)) {
               floatValue4 = this.method3531(floatValue4, floatValue);
               floatValue2 = Math.min(floatValue2, floatValue4);
               floatValue3 = Math.max(floatValue3, floatValue4);
               f2 = Math.min(f2, floatValue5);
               f3 = Math.max(f3, floatValue5);
            }
         }

         if (floatValue2 == Float.MAX_VALUE) {
            floatValue2 = floatValue;
            floatValue3 = floatValue;
            f2 = f;
            f3 = f;
         }

         Vec3 eyePosition2 = this.method3525(rotation, eyePosition);
         AABB aabb = this.method3526(livingEntity, boundingBox, vec);
         Vec2f vec2f2 = rotation.wrappedDeltaTo(this.method3524(eyePosition2, this.method3530(aabb)));
         float floatValue4 = vec2f2.getFloatValue();
         float floatValue5 = vec2f2.getFloatValue2();
         if (!this.isFinite(floatValue4) || !this.isFinite(floatValue5)) {
            floatValue4 = floatValue;
            floatValue5 = f;
         }

         float doubleValue = floatValue / 0.21217355F;
         float f4 = f / 0.21217355F;
         int n3 = floatValue2 <= 0.0F && floatValue3 >= 0.0F && f2 <= 0.0F && f3 >= 0.0F ? 1 : 0;
         ReplayState.Entry entry = new ReplayState.Entry();
         entry.doubleValue = doubleValue;
         entry.doubleValue2 = f4;
         entry.doubleValue3 = floatValue2 / 0.21217355F;
         entry.doubleValue4 = floatValue3 / 0.21217355F;
         entry.doubleValue5 = f2 / 0.21217355F;
         entry.doubleValue6 = f3 / 0.21217355F;
         if (this.flag2) {
            entry.doubleValue7 = doubleValue - this.floatValue3;
            entry.doubleValue8 = f4 - this.floatValue4;
            entry.doubleValue9 = doubleValue - (this.floatValue3 - n);
            entry.doubleValue10 = f4 - (this.floatValue4 - n2);
         }

         entry.doubleValue11 = floatValue4 / 0.21217355F;
         entry.doubleValue12 = floatValue5 / 0.21217355F;
         entry.doubleValue13 = n3 != 0 ? 1.0 : 0.0;
         entry.doubleValue14 = eyePosition.distanceTo(vec3);
         entry.doubleValue15 = vec.x;
         entry.doubleValue16 = vec.y;
         entry.doubleValue17 = vec.z;
         entry.doubleValue18 = vec2.x;
         entry.doubleValue19 = vec2.y;
         entry.doubleValue20 = vec2.z;
         return entry;
      } else {
         return null;
      }
   }

   private Vec3[] method3523(AABB boundingBox) {
      return new Vec3[]{
         new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
         new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),
         new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.minZ),
         new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ),
         new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
         new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
         new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
         new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)
      };
   }

   private Rotation method3524(Vec3 eyePosition, Vec3 vec) {
      return RotationUtils.method6912(vec.subtract(eyePosition));
   }

   private Vec3 method3525(Rotation rotation, Vec3 eyePosition) {
      Vec3 position = mc.player.position();
      Vec3 vec = eyePosition.subtract(position);

      try {
         PlayerSimulation playerSimulation = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(mc.player.input.keyPresses));
         playerSimulation.floatValue = rotation.getFloatValue();
         playerSimulation.floatValue2 = rotation.getFloatValue2();
         playerSimulation.tick();
         playerSimulation.tick();
         Vec3 deltaMovement = playerSimulation.vec3d.add(vec);
         return this.method3533(deltaMovement) ? deltaMovement : eyePosition;
      } catch (Throwable e) {
         return eyePosition;
      }
   }

   private AABB method3526(LivingEntity livingEntity, AABB boundingBox, Vec3 vec) {
      if (livingEntity instanceof Player player) {
         try {
            PlayerSimulation playerSimulation = PlayerSimulation.method6034(player, PlayerSimulation.Entry.method6093(player));
            playerSimulation.tick();
            playerSimulation.tick();
            if (playerSimulation.box != null && this.method3533(playerSimulation.vec3d)) {
               return playerSimulation.box;
            }
         } catch (Throwable e) {
         }
      }

      Vec3 deltaMovement = vec == null ? Vec3.ZERO : vec.scale(2.0);
      return this.method3533(deltaMovement) ? boundingBox.move(deltaMovement) : boundingBox;
   }

   private LivingEntity method3527() {
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      return livingEntity != null && livingEntity.isAlive() ? livingEntity : null;
   }

   private Vec3 method3528(Vec3 position) {
      if (this.vec3d != null && position != null) {
         Vec3 deltaMovement = position.subtract(this.vec3d);
         if (this.method3533(deltaMovement) && deltaMovement.lengthSqr() > 1.0E-10) {
            return deltaMovement;
         }
      }

      Vec3 deltaMovement = mc.player.getDeltaMovement();
      return this.method3533(deltaMovement) ? deltaMovement : Vec3.ZERO;
   }

   private Vec3 method3529(LivingEntity livingEntity, AABB boundingBox) {
      if (this.box != null && boundingBox != null) {
         Vec3 deltaMovement = this.method3530(boundingBox).subtract(this.method3530(this.box));
         if (this.method3533(deltaMovement) && deltaMovement.lengthSqr() > 1.0E-10) {
            return deltaMovement;
         }
      }

      Vec3 deltaMovement = livingEntity == null ? Vec3.ZERO : livingEntity.getDeltaMovement();
      return this.method3533(deltaMovement) ? deltaMovement : Vec3.ZERO;
   }

   private Vec3 method3530(AABB boundingBox) {
      return new Vec3((boundingBox.minX + boundingBox.maxX) * 0.5, (boundingBox.minY + boundingBox.maxY) * 0.5, (boundingBox.minZ + boundingBox.maxZ) * 0.5);
   }

   private float method3531(float f, float floatValue) {
      float angle = f - floatValue;
      return !Float.isFinite(angle) ? f : floatValue + Mth.wrapDegrees(angle);
   }

   private void method3532() {
      this.intValue = Integer.MIN_VALUE;
      this.flag = false;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.flag2 = false;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.vec3d = null;
      this.box = null;
      if (this.replayState != null) {
         this.replayState.reset();
      }
   }

   private boolean isFinite(float floatValue) {
      return !Float.isNaN(floatValue) && !Float.isInfinite(floatValue);
   }

   private boolean method3533(Vec3 deltaMovement) {
      return deltaMovement != null && this.isFinite((float)deltaMovement.x) && this.isFinite((float)deltaMovement.y) && this.isFinite((float)deltaMovement.z);
   }
}

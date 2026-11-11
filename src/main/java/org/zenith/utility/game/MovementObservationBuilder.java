package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.Rotation;
import org.zenith.utility.api.WorldStylePlanModel;

public final class MovementObservationBuilder {
   private MovementObservationBuilder() {
   }

   public static MovementObservationBuilder.Observation captureObservation(WorldStylePlanModel model, Rotation rotation) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.level != null && rotation != null && !rotation.isInvalid()) {
         float[][][][] voxelFeatures = captureVoxelFeatures(minecraft, model, rotation.getFloatValue());
         float[] playerFeatures = capturePlayerFeatures(minecraft, model, rotation);
         return voxelFeatures != null && playerFeatures != null ? new MovementObservationBuilder.Observation(voxelFeatures, playerFeatures) : null;
      } else {
         return null;
      }
   }

   private static float[] capturePlayerFeatures(Minecraft minecraft, WorldStylePlanModel model, Rotation rotation) {
      Vec3 deltaMovement = minecraft.player.getDeltaMovement();
      AABB boundingBox = minecraft.player.getBoundingBox();
      double d = Math.toRadians(rotation.getFloatValue());
      float[] floats = new float[]{
         (float)deltaMovement.x,
         (float)deltaMovement.y,
         (float)deltaMovement.z,
         booleanFeature(minecraft.player.onGround()),
         booleanFeature(minecraft.player.horizontalCollision),
         booleanFeature(minecraft.player.verticalCollision),
         booleanFeature(minecraft.player.isSprinting()),
         booleanFeature(minecraft.player.jumping),
         booleanFeature(minecraft.options.keyUp.isDown()),
         booleanFeature(minecraft.options.keyDown.isDown()),
         booleanFeature(minecraft.options.keyLeft.isDown()),
         booleanFeature(minecraft.options.keyRight.isDown()),
         booleanFeature(minecraft.options.keyJump.isDown()),
         booleanFeature(minecraft.options.keySprint.isDown()),
         (float)Math.sin(d),
         (float)Math.cos(d),
         rotation.getFloatValue2() / 90.0F,
         (float)boundingBox.getXsize(),
         (float)boundingBox.getYsize(),
         (float)boundingBox.getZsize(),
         (float)minecraft.player.getAttributeValue(Attributes.MOVEMENT_SPEED),
         (float)minecraft.player.getAttributeValue(Attributes.GRAVITY),
         (float)minecraft.player.getAttributeValue(Attributes.JUMP_STRENGTH),
         minecraft.player.maxUpStep()
      };
      if (floats.length != model.getIntValue6()) {
         return null;
      } else {
         for (float f : floats) {
            if (!Float.isFinite(f)) {
               return null;
            }
         }

         return floats;
      }
   }

   private static float[][][][] captureVoxelFeatures(Minecraft minecraft, WorldStylePlanModel model, float floatValue) {
      int n = model.getIntValue8();
      float[][][][] floatseseses = new float[3][n][n][n];
      Vec3 position = minecraft.player.position();
      AABB boundingBox = minecraft.player.getBoundingBox();
      List<AABB> aabbs = collectCollisionBoxes(minecraft, boundingBox);
      double d = Math.toRadians(floatValue);
      double d2 = Math.sin(d);
      double d3 = Math.cos(d);
      double d4 = 2.0 * model.getFloatValue6() / n;
      double d5 = (model.getFloatValue8() - model.getFloatValue7()) / n;
      int n2 = model.getIntValue13();

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
               double d6 = -model.getFloatValue6() + (k + 0.5) * d4;
               double d7 = model.getFloatValue7() + (i + 0.5) * d5;
               double d8 = -model.getFloatValue6() + (j + 0.5) * d4;
               Vec3 pos = toWorldPosition(position, d3, d2, d6, d7, d8);
               int n3 = 0;

               for (int m = 0; m < n2; m++) {
                  for (int i2 = 0; i2 < n2; i2++) {
                     for (int i3 = 0; i3 < n2; i3++) {
                        double d9 = ((i3 + 0.5) / n2 - 0.5) * d4;
                        double d10 = ((m + 0.5) / n2 - 0.5) * d5;
                        double d11 = ((i2 + 0.5) / n2 - 0.5) * d4;
                        Vec3 vec = toWorldPosition(position, d3, d2, d6 + d9, d7 + d10, d8 + d11);
                        if (isInsideCollision(vec, aabbs)) {
                           n3++;
                        }
                     }
                  }
               }

               floatseseses[0][i][j][k] = (float)n3 / (n2 * n2 * n2);
               double d9 = model.getFloatValue9();

               for (AABB aabb : aabbs) {
                  d9 = Math.min(d9, distanceToBox(pos, aabb));
               }

               floatseseses[1][i][j][k] = (float)(d9 / model.getFloatValue9());
               BlockState blockState = minecraft.level.getBlockState(BlockPos.containing(pos));
               floatseseses[2][i][j][k] = Mth.clamp(blockState.getBlock().getFriction(), 0.0F, 1.0F);
            }
         }
      }

      return floatseseses;
   }

   private static List<AABB> collectCollisionBoxes(Minecraft minecraft, AABB boundingBox) {
      int n = Mth.floor(boundingBox.minX) - 6;
      int n2 = Mth.floor(boundingBox.maxX) + 6;
      int n3 = Mth.floor(boundingBox.minY) - 3;
      int n4 = Mth.floor(boundingBox.maxY) + 5;
      int n5 = Mth.floor(boundingBox.minZ) - 6;
      int n6 = Mth.floor(boundingBox.maxZ) + 6;
      ArrayList<AABB> aabbs = new ArrayList<>();
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = n3; i <= n4; i++) {
         for (int j = n5; j <= n6; j++) {
            for (int k = n; k <= n2; k++) {
               mutablePos.set(k, i, j);
               BlockState blockState = minecraft.level.getBlockState(mutablePos);
               VoxelShape collisionShape = blockState.getCollisionShape(minecraft.level, mutablePos);

               for (AABB aabb : collisionShape.toAabbs()) {
                  aabbs.add(aabb.move(k, i, j));
               }
            }
         }
      }

      return aabbs;
   }

   private static Vec3 toWorldPosition(Vec3 position, double d, double d2, double d3, double d4, double d5) {
      return new Vec3(position.x + d3 * d - d5 * d2, position.y + d4, position.z + d3 * d2 + d5 * d);
   }

   private static boolean isInsideCollision(Vec3 vec, List<AABB> aabbs) {
      for (AABB aabb : aabbs) {
         if (vec.x >= aabb.minX && vec.x < aabb.maxX && vec.y >= aabb.minY && vec.y < aabb.maxY && vec.z >= aabb.minZ && vec.z < aabb.maxZ) {
            return true;
         }
      }

      return false;
   }

   private static double distanceToBox(Vec3 pos, AABB aabb) {
      double d = Math.max(Math.max(aabb.minX - pos.x, 0.0), pos.x - aabb.maxX);
      double d2 = Math.max(Math.max(aabb.minY - pos.y, 0.0), pos.y - aabb.maxY);
      double d3 = Math.max(Math.max(aabb.minZ - pos.z, 0.0), pos.z - aabb.maxZ);
      return Math.sqrt(d * d + d2 * d2 + d3 * d3);
   }

   private static float booleanFeature(boolean down) {
      return down ? 1.0F : 0.0F;
   }

   public static final class Observation {
      private final float[][][][] voxelFeatures;
      private final float[] playerFeatures;

      public float[][][][] voxelFeatures() {
         return this.voxelFeatures;
      }

      public float[] playerFeatures() {
         return this.playerFeatures;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Observation other)) return false;
         return java.util.Objects.equals(this.voxelFeatures, other.voxelFeatures) && java.util.Objects.equals(this.playerFeatures, other.playerFeatures);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(voxelFeatures, playerFeatures);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValues=" + this.voxelFeatures + ", " + "floatValues2=" + this.playerFeatures + "]";
      }

      public Observation(float[][][][] voxelFeatures, float[] playerFeatures) {
         this.voxelFeatures = voxelFeatures;
         this.playerFeatures = playerFeatures;
      }

      public float[][][][] getVoxelFeatures() {
         return this.voxelFeatures;
      }

      public float[] getPlayerFeatures() {
         return this.playerFeatures;
      }
   }
}

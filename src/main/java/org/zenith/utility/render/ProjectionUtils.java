package org.zenith.utility.render;

import com.mojang.blaze3d.platform.Window;
import lombok.Generated;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.FrustumHolder;

public final class ProjectionUtils implements MinecraftAccess {
   private static final int[] CONST_intValues = new int[]{0, 0, 1, 1};
   private static boolean flag = false;
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector4f vector4f = new Vector4f();

   public static void method299() {
      Window window = mc.getWindow();
      CONST_intValues[0] = 0;
      CONST_intValues[1] = 0;
      CONST_intValues[2] = Math.max(1, window.getWidth());
      CONST_intValues[3] = Math.max(1, window.getHeight());
      flag = true;
   }

   public static void method300() {
      flag = false;
   }

   @NotNull
   public static Vec3 method301(Vec3 vec) {
      if (!flag) {
         method299();
      }

      Vec3 position = mc.getEntityRenderDispatcher().camera.position();
      float x = (float)(vec.x - position.x);
      float y = (float)(vec.y - position.y);
      float z = (float)(vec.z - position.z);
      boolean sameThread = mc.isSameThread();
      Vector3f winCoordsDest = sameThread ? vector3f : new Vector3f();
      Vector4f vector4f2 = sameThread ? vector4f : new Vector4f();
      Vector4f vec2 = vector4f2.set(x, y, z, 1.0F).mul(GlowUtil.getMatrix4f3());
      GlowUtil.getMatrix4f().project(vec2.x(), vec2.y(), vec2.z(), CONST_intValues, winCoordsDest);
      double guiScale = mc.getWindow().getGuiScale();
      return new Vec3(winCoordsDest.x / guiScale, (mc.getWindow().getHeight() - winCoordsDest.y) / guiScale, winCoordsDest.z);
   }

   public static boolean method302(Vec3 vec3d) {
      Camera camera = mc.getEntityRenderDispatcher().camera;
      Rotation rotation = RotationUtils.method6913(vec3d);
      return Math.abs(Mth.wrapDegrees(rotation.getFloatValue() - camera.yRot())) < 90.0F
            && Math.abs(Mth.wrapDegrees(rotation.getFloatValue2() - camera.xRot())) < 60.0F
         || method303(new AABB(BlockPos.containing(vec3d)));
   }

   public static boolean method303(AABB move) {
      Frustum frustum = FrustumHolder.getFrustum();
      return move != null && frustum != null && frustum.isVisible(move);
   }

   public static boolean method304(Vector4d vector4d) {
      return vector4d == null || vector4d.x < 0.0 && vector4d.z < 1.0 || vector4d.y < 0.0 && vector4d.w < 1.0;
   }

   public static double method305(Vector4d vector4d) {
      return vector4d.x + (vector4d.z - vector4d.x) / 2.0;
   }

   @NotNull
   public static Vec3[] method306(Entity player, Vec3 vec) {
      AABB boundingBox = player.getBoundingBox();
      AABB aabb = new AABB(
         boundingBox.minX - player.getX() + vec.x - 0.1F,
         boundingBox.minY - player.getY() + vec.y - 0.1F,
         boundingBox.minZ - player.getZ() + vec.z - 0.1F,
         boundingBox.maxX - player.getX() + vec.x + 0.1F,
         boundingBox.maxY - player.getY() + vec.y + 0.1F,
         boundingBox.maxZ - player.getZ() + vec.z + 0.1F
      );
      return new Vec3[]{
         new Vec3(aabb.minX, aabb.minY, aabb.minZ),
         new Vec3(aabb.minX, aabb.maxY, aabb.minZ),
         new Vec3(aabb.maxX, aabb.minY, aabb.minZ),
         new Vec3(aabb.maxX, aabb.maxY, aabb.minZ),
         new Vec3(aabb.minX, aabb.minY, aabb.maxZ),
         new Vec3(aabb.minX, aabb.maxY, aabb.maxZ),
         new Vec3(aabb.maxX, aabb.minY, aabb.maxZ),
         new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ)
      };
   }

   public static Vector4d method307(Entity player) {
      Vector4d vector4d = null;

      for (Vec3 vec : method306(player, MathUtils.method2656(player))) {
         vec = method301(vec);
         if (vec.z > 0.0 && vec.z < 1.0) {
            if (vector4d == null) {
               vector4d = new Vector4d(vec.x, vec.y, vec.z, 0.0);
            }

            vector4d.x = Math.min(vec.x, vector4d.x);
            vector4d.y = Math.min(vec.y, vector4d.y);
            vector4d.z = Math.max(vec.x, vector4d.z);
            vector4d.w = Math.max(vec.y, vector4d.w);
         }
      }

      return vector4d;
   }

   @Generated
   private ProjectionUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

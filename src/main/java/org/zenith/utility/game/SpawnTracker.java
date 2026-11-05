package org.zenith.utility.game;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.UpdateEvent;

public final class SpawnTracker implements MinecraftAccess {
   public static final double CONST_doubleValue = -66.0;
   private static final double CONST_doubleValue2 = 80.0;
   private static final double CONST_doubleValue3 = 200.0;
   private static final double CONST_doubleValue4 = 0.001;
   private static final double CONST_doubleValue5 = 0.5;
   private static final double CONST_doubleValue6 = 16.0;
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 100;
   private static final int CONST_intValue3 = 12000;
   private static final String text = "minecraft:world_spawn";
   private static final SpawnTracker INSTANCE = new SpawnTracker();
   private static Vec3 vec3d;
   private static Vec3 vec3d2;
   private static int cooldown;
   private static int intValue;
   private static int intValue2;
   private static boolean flag;
   private static boolean flag2;
   private static double doubleValue;
   private static double doubleValue2;
   private static Object owner;
   private static ClientLevel clientWorld;
   private static LocalPlayer clientPlayerEntity;

   private SpawnTracker() {
   }

   public static boolean method6206() {
      return mc.player != null
         && mc.level != null
         && mc.player.isAlive()
         && !mc.player.isRemoved()
         && mc.player.getAbilities().flying
         && !"minecraft:world_spawn".equals(mc.level.dimension().identifier().toString());
   }

   public static boolean isActive() {
      if ((vec3d != null || flag) && (!method6206() || clientWorld != mc.level || clientPlayerEntity != mc.player)) {
         reset();
      }

      return vec3d != null || flag;
   }

   public static boolean method6207(Object obj) {
      return isActive() && owner == obj;
   }

   public static boolean method6208() {
      return mc.player != null && method6225(mc.player.getY());
   }

   public static void method6209(Object obj) {
      if (method6207(obj)) {
         reset();
      }
   }

   public static boolean method6210(Vec3 vec3d) {
      if (method6206() && method6232(vec3d) && method6236(vec3d)) {
         Vec3 position = mc.player.position();
         Vec3 vec = method6226(position);
         return method6232(vec) && !method6227(position, vec) && !method6227(vec, vec3d);
      } else {
         return false;
      }
   }

   public static boolean method6211(Object obj, Vec3 vec3d) {
      return method6221(obj) && method6206() && method6223(obj, vec3d);
   }

   public static boolean method6212(Entity entity) {
      reset();
      return entity != null
         && method6206()
         && entity.level() == mc.level
         && !entity.isRemoved()
         && method6239(entity)
         && method6223(null, entity.position());
   }

   public static boolean method6213(Object obj, double x, double z, boolean flag3) {
      if (method6221(obj) && method6206() && method6232(new Vec3(x, -66.0, z))) {
         method6222(obj);
         flag = true;
         flag2 = flag3;
         doubleValue = x;
         doubleValue2 = z;
         Vec3 position = mc.player.position();
         Vec3 vec3d = method6226(position);
         if (method6225(position.y)) {
            vec3d2 = method6229(position.x, position.z);
         } else if (method6227(position, vec3d)) {
            method6224(method6228(position.x, position.y, position.z, -66.0));
         } else {
            method6224(vec3d);
            vec3d2 = method6229(position.x, position.z);
         }

         cooldown = 2;
         return true;
      } else {
         return false;
      }
   }

   public static boolean method6214(Object obj) {
      if (method6221(obj) && method6206()) {
         Vec3 position = mc.player.position();
         Vec3 vec3d = method6226(position);
         if (method6232(vec3d) && !method6227(position, vec3d)) {
            method6224(vec3d);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static Vec3 method6215(Vec3 vec3d) {
      if (method6238(vec3d)) {
         return vec3d;
      } else if (vec3d != null && mc.player != null && mc.level != null && method6232(vec3d)) {
         for (int i = 1; i <= 2; i++) {
            Vec3 vec = vec3d.add(0.0, i, 0.0);
            if (method6238(vec)) {
               return vec;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @EventTarget(0)
   public void onUpdate(UpdateEvent event) {
      if (isActive()) {
         if (++intValue <= 100 && ++intValue2 <= 12000) {
            if (cooldown > 0) {
               cooldown--;
            } else if (flag) {
               method6216();
            } else {
               if (method6232(vec3d) && method6236(vec3d) && !method6227(mc.player.position(), vec3d)) {
                  method6224(vec3d);
               }

               reset();
            }
         } else {
            if (owner == null) {
               ChatLogger.method2022("Телепорт остановлен: маршрут недоступен или чанки не загрузились");
            }

            reset();
         }
      }
   }

   private static void method6216() {
      if (!method6232(new Vec3(doubleValue, -66.0, doubleValue2))) {
         reset();
      } else if (!method6217()) {
         Vec3 position = mc.player.position();
         if (Math.hypot(position.x - doubleValue, position.z - doubleValue2) > 0.5) {
            method6218(position);
         } else if (flag2) {
            if (method6225(position.y)) {
               reset();
            } else {
               method6224(method6228(position.x, position.y, position.z, -66.0));
               cooldown = 2;
            }
         } else {
            method6219(position);
         }
      }
   }

   private static boolean method6217() {
      if (vec3d2 == null) {
         return false;
      } else {
         Vec3 position = mc.player.position();
         if (!method6232(vec3d2) || method6227(position, vec3d2)) {
            vec3d2 = null;
            return true;
         } else if (!method6225(position.y) && method6225(vec3d2.y)) {
            vec3d2 = null;
            method6218(position);
            return true;
         } else {
            if (method6225(vec3d2.y)) {
               if (!method6235(position.x, position.z, vec3d2.x, vec3d2.z)) {
                  vec3d2 = method6230(position.x, position.z);
                  if (vec3d2 == null) {
                     return true;
                  }
               }
            } else if (!method6234(vec3d2.x, vec3d2.z)) {
               return true;
            }

            method6224(vec3d2);
            vec3d2 = null;
            cooldown = 2;
            return true;
         }
      }
   }

   private static void method6218(Vec3 position) {
      if (!method6225(position.y)) {
         method6224(method6228(position.x, position.y, position.z, -66.0));
         cooldown = 2;
      } else {
         Vec3 vec = method6230(position.x, position.z);
         if (vec != null) {
            method6220(vec);
         }
      }
   }

   private static void method6219(Vec3 position) {
      Double d = method6231(doubleValue, doubleValue2);
      if (d != null) {
         if (method6237(doubleValue, position.y, doubleValue2) && Math.abs(position.y - d) <= 1.0) {
            reset();
         } else {
            Vec3 vec = method6228(doubleValue, position.y, doubleValue2, d);
            if (method6225(vec.y)) {
               method6220(vec);
            } else {
               vec3d2 = vec;
               cooldown = 2;
            }
         }
      }
   }

   private static void method6220(Vec3 vec) {
      vec3d2 = vec;
      method6224(method6226(mc.player.position()));
      cooldown = 2;
   }

   private static boolean method6221(Object obj) {
      if (obj != null && isActive() && owner != obj) {
         return false;
      } else {
         reset();
         return true;
      }
   }

   private static void method6222(Object obj) {
      owner = obj;
      clientWorld = mc.level;
      clientPlayerEntity = mc.player;
   }

   private static boolean method6223(Object obj, Vec3 vec) {
      if (!method6210(vec)) {
         return false;
      } else {
         method6222(obj);
         vec3d = vec;
         cooldown = 2;
         method6224(method6226(mc.player.position()));
         return true;
      }
   }

   private static void reset() {
      vec3d = null;
      vec3d2 = null;
      cooldown = 0;
      intValue = 0;
      intValue2 = 0;
      flag = false;
      flag2 = false;
      doubleValue = 0.0;
      doubleValue2 = 0.0;
      owner = null;
      clientWorld = null;
      clientPlayerEntity = null;
   }

   private static void method6224(Vec3 vec3d) {
      if (mc.player.position().distanceToSqr(vec3d) > 1.0E-6) {
         intValue = 0;
      }

      mc.player.setDeltaMovement(Vec3.ZERO);
      mc.player.setPos(vec3d.x, vec3d.y, vec3d.z);
   }

   private static boolean method6225(double y) {
      return Math.abs(y - -66.0) <= 0.001;
   }

   private static Vec3 method6226(Vec3 position) {
      return new Vec3(position.x, -66.0, position.z);
   }

   private static boolean method6227(Vec3 position, Vec3 vec3d) {
      return Math.hypot(position.x - vec3d.x, position.z - vec3d.z) > 80.001 || Math.abs(position.y - vec3d.y) > 200.001;
   }

   private static Vec3 method6228(double x, double y, double z, double d) {
      double sign = d - y;
      return new Vec3(x, Math.abs(sign) <= 200.0 ? d : y + Math.copySign(200.0, sign), z);
   }

   private static Vec3 method6229(double x, double z) {
      double d = doubleValue - x;
      double y = doubleValue2 - z;
      double hypot = Math.hypot(d, y);
      if (hypot <= 0.001) {
         return null;
      } else {
         return hypot <= 80.0 ? new Vec3(doubleValue, -66.0, doubleValue2) : new Vec3(x + d / hypot * 80.0, -66.0, z + y / hypot * 80.0);
      }
   }

   private static Vec3 method6230(double x, double z) {
      Vec3 vec = method6229(x, z);
      if (vec != null && !method6235(x, z, vec.x, vec.z)) {
         double d = vec.x - x;
         double y = vec.z - z;
         double hypot = Math.hypot(d, y);
         double d2 = d / hypot;
         double d3 = y / hypot;
         double d4 = 0.0;

         for (double d5 = 16.0; d5 < hypot && method6235(x, z, x + d2 * d5, z + d3 * d5); d5 += 16.0) {
            d4 = d5;
         }

         return d4 < 16.0 ? null : new Vec3(x + d2 * d4, -66.0, z + d3 * d4);
      } else {
         return vec;
      }
   }

   private static Double method6231(double doubleValue, double z) {
      if (!method6234(doubleValue, z)) {
         return null;
      } else {
         int height = mc.level.getHeight(Types.WORLD_SURFACE, Mth.floor(doubleValue), Mth.floor(z));
         if (height <= mc.level.getMinY() + 1) {
            return null;
         } else {
            while (height <= mc.level.getMaxY()) {
               if (method6237(doubleValue, height, z)) {
                  return (double)height;
               }

               height++;
            }

            return null;
         }
      }
   }

   private static boolean method6232(Vec3 vec3d) {
      if (vec3d != null && Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z)) {
         BlockPos containing = BlockPos.containing(vec3d);
         return Level.isInSpawnableBounds(containing) && mc.level.getWorldBorder().isWithinBounds(containing);
      } else {
         return false;
      }
   }

   private static boolean method6233(int x, int z) {
      return mc.level.getChunkSource().getChunk(x, z, ChunkStatus.FULL, false) instanceof LevelChunk;
   }

   private static boolean method6234(double x, double z) {
      return method6233(Mth.floor(x) >> 4, Mth.floor(z) >> 4);
   }

   private static boolean method6235(double x, double z, double maxX, double maxZ) {
      int n = Math.min(Mth.floor(x), Mth.floor(maxX)) >> 4;
      int n2 = Math.max(Mth.floor(x), Mth.floor(maxX)) >> 4;
      int n3 = Math.min(Mth.floor(z), Mth.floor(maxZ)) >> 4;
      int n4 = Math.max(Mth.floor(z), Mth.floor(maxZ)) >> 4;

      for (int i = n; i <= n2; i++) {
         for (int j = n3; j <= n4; j++) {
            if (!method6233(i, j)) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean method6236(Vec3 vec3d) {
      AABB boundingBox = mc.player.getDimensions(Pose.STANDING).makeBoundingBox(vec3d);
      return method6235(boundingBox.minX, boundingBox.minZ, boundingBox.maxX, boundingBox.maxZ);
   }

   private static boolean method6237(double doubleValue, double y, double z) {
      AABB boundingBox = mc.player.getDimensions(Pose.STANDING).makeBoundingBox(doubleValue, y, z);
      return method6235(boundingBox.minX, boundingBox.minZ, boundingBox.maxX, boundingBox.maxZ) && !mc.level.getBlockCollisions(mc.player, boundingBox).iterator().hasNext();
   }

   private static boolean method6238(Vec3 vec3d) {
      return mc.player != null && mc.level != null && method6232(vec3d) && method6237(vec3d.x, vec3d.y, vec3d.z);
   }

   private static boolean method6239(Entity entity) {
      Vec3 position = entity.position();
      AABB boundingBox = mc.player.getDimensions(Pose.STANDING).makeBoundingBox(position);
      return method6232(position) && method6235(boundingBox.minX, boundingBox.minZ, boundingBox.maxX, boundingBox.maxZ) && !mc.level.getBlockCollisions(mc.player, boundingBox).iterator().hasNext();
   }

   static {
      EventManager.register(INSTANCE);
   }
}

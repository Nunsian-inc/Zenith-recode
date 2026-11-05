package org.zenith.base.figura.lua.api;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.api.entity.EntityAPI;
import org.zenith.base.figura.lua.api.world.BlockStateAPI;
import org.zenith.base.figura.lua.api.world.WorldAPI;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec3;

@LuaWhitelist
@LuaTypeDoc(name = "RaycastAPI", value = "raycast")
public final class RaycastAPI {
   private final Avatar owner;

   public RaycastAPI(Avatar owner) {
      this.owner = owner;
   }

   private static FiguraVec3 vector(Object value, String argument) {
      if (value instanceof FiguraVec3 vector) {
         return vector;
      } else {
         throw new LuaError(argument + " must be a Vector3");
      }
   }

   @LuaWhitelist
   public Object[] block(Object startValue, Object endValue) {
      if (WorldAPI.getCurrentWorld() != null && Minecraft.getInstance().player != null) {
         FiguraVec3 start = vector(startValue, "start");
         FiguraVec3 end = vector(endValue, "end");
         BlockHitResult hit = WorldAPI.getCurrentWorld()
            .clip(new ClipContext(start.asVec3(), end.asVec3(), Block.COLLIDER, Fluid.NONE, Minecraft.getInstance().player));
         BlockPos pos = hit.getBlockPos();
         return new Object[]{
            new BlockStateAPI(WorldAPI.getCurrentWorld().getBlockState(pos), pos),
            FiguraVec3.fromVec3(hit.getLocation()),
            hit.getDirection().getSerializedName()
         };
      } else {
         return null;
      }
   }

   @LuaWhitelist
   public Object[] entity(Object startValue, Object endValue, LuaFunction predicateFunction) {
      if (WorldAPI.getCurrentWorld() != null && Minecraft.getInstance().player != null) {
         Vec3 start = vector(startValue, "start").asVec3();
         Vec3 end = vector(endValue, "end").asVec3();
         Predicate<Entity> predicate = candidate -> {
            if (predicateFunction == null) {
               return true;
            }

            LuaValue result = predicateFunction.call(this.owner.luaRuntime.typeManager.javaToLua(EntityAPI.wrap(candidate)).arg1());
            return !result.isnil() && (!result.isboolean() || result.toboolean());
         };
         EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            Minecraft.getInstance().player, start, end, new AABB(start, end).inflate(1.0), predicate, start.distanceToSqr(end)
         );
         return hit == null ? null : new Object[]{EntityAPI.wrap(hit.getEntity()), FiguraVec3.fromVec3(hit.getLocation())};
      } else {
         return null;
      }
   }

   @LuaWhitelist
   public Object[] aabb(Object startValue, Object endValue, LuaTable boxes) {
      Vec3 start = vector(startValue, "start").asVec3();
      Vec3 end = vector(endValue, "end").asVec3();
      double bestDistance = Double.MAX_VALUE;
      Object[] best = null;

      for (int i = 1; i <= boxes.length(); i++) {
         LuaValue entry = boxes.get(i);
         if (entry.istable()) {
            LuaValue min = entry.get(1);
            LuaValue max = entry.get(2);
            if (min.isuserdata(FiguraVec3.class) && max.isuserdata(FiguraVec3.class)) {
               FiguraVec3 minimum = (FiguraVec3)min.checkuserdata(FiguraVec3.class);
               FiguraVec3 maximum = (FiguraVec3)max.checkuserdata(FiguraVec3.class);
               AABB box = new AABB(minimum.asVec3(), maximum.asVec3());
               Optional<Vec3> hit = box.clip(start, end);
               if (hit.isPresent()) {
                  double distance = start.distanceToSqr(hit.get());
                  if (distance < bestDistance) {
                     bestDistance = distance;
                     best = new Object[]{entry, FiguraVec3.fromVec3(hit.get()), null, i};
                  }
               }
            }
         }
      }

      return best;
   }

   @Override
   public String toString() {
      return "RaycastAPI";
   }
}

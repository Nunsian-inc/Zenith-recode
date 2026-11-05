package org.zenith.base.figura.lua.api.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.NbtToLua;
import org.zenith.base.figura.lua.ReadOnlyLuaTable;
import org.zenith.base.figura.lua.api.world.BlockStateAPI;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.math.vector.FiguraVec3;

@LuaWhitelist
@LuaTypeDoc(name = "EntityAPI", value = "entity")
public class EntityAPI<T extends Entity> {
   protected final UUID entityUUID;
   protected T entity;

   public EntityAPI(T entity) {
      this.entity = entity;
      this.entityUUID = entity.getUUID();
   }

   public static EntityAPI<?> wrap(Entity entity) {
      if (entity instanceof Player player) {
         return new PlayerAPI(player);
      } else if (entity instanceof LivingEntity living) {
         return new LivingEntityAPI<>(living);
      } else {
         return entity == null ? null : new EntityAPI<>(entity);
      }
   }

   protected boolean checkEntity() {
      if (!this.entity.isRemoved()) {
         return true;
      }

      if (Minecraft.getInstance().level != null) {
         for (Player player : Minecraft.getInstance().level.players()) {
            if (player.getUUID().equals(this.entityUUID)) {
               this.entity = (T)player;
               return true;
            }
         }
      }

      return false;
   }

   public T getEntity() {
      return this.entity;
   }

   @LuaWhitelist
   public boolean isLoaded() {
      return this.checkEntity();
   }

   @LuaWhitelist
   public FiguraVec3 getPos(Float delta) {
      this.checkEntity();
      return FiguraVec3.fromVec3(this.entity.getPosition(delta == null ? 1.0F : delta));
   }

   @LuaWhitelist
   public FiguraVec2 getRot(Float delta) {
      this.checkEntity();
      float d = delta == null ? 1.0F : delta;
      return FiguraVec2.of(Mth.lerp(d, this.entity.xRotO, this.entity.getXRot()), Mth.lerp(d, this.entity.yRotO, this.entity.getYRot()));
   }

   @LuaWhitelist
   public String getUUID() {
      return this.entityUUID.toString();
   }

   @LuaWhitelist
   public String getType() {
      this.checkEntity();
      return BuiltInRegistries.ENTITY_TYPE.getKey(this.entity.getType()).toString();
   }

   @LuaWhitelist
   public FiguraVec3 getVelocity() {
      this.checkEntity();
      return FiguraVec3.of(this.entity.getX() - this.entity.xOld, this.entity.getY() - this.entity.yOld, this.entity.getZ() - this.entity.zOld);
   }

   @LuaWhitelist
   public FiguraVec3 getLookDir() {
      this.checkEntity();
      return FiguraVec3.fromVec3(this.entity.getLookAngle());
   }

   @LuaWhitelist
   public String getPose() {
      this.checkEntity();
      return this.entity.getPose().toString();
   }

   @LuaWhitelist
   public EntityAPI<?> getVehicle() {
      this.checkEntity();
      return wrap(this.entity.getVehicle());
   }

   @LuaWhitelist
   public boolean isOnGround() {
      this.checkEntity();
      return this.entity.onGround();
   }

   @LuaWhitelist
   public float getEyeHeight() {
      this.checkEntity();
      return this.entity.getEyeHeight(this.entity.getPose());
   }

   @LuaWhitelist
   public String getName() {
      this.checkEntity();
      return this.entity.getName().getString();
   }

   @LuaWhitelist
   public boolean isWet() {
      this.checkEntity();
      return this.entity.isInWaterOrRain();
   }

   @LuaWhitelist
   public boolean isInWater() {
      this.checkEntity();
      return this.entity.isInWater();
   }

   @LuaWhitelist
   public boolean isUnderwater() {
      this.checkEntity();
      return this.entity.isUnderWater();
   }

   @LuaWhitelist
   public boolean isInLava() {
      this.checkEntity();
      return this.entity.isInLava();
   }

   @LuaWhitelist
   public boolean isSprinting() {
      this.checkEntity();
      return this.entity.isSprinting();
   }

   @LuaWhitelist
   public boolean isCrouching() {
      this.checkEntity();
      return this.entity.isCrouching();
   }

   @LuaWhitelist
   public boolean isGlowing() {
      this.checkEntity();
      return this.entity.isCurrentlyGlowing();
   }

   @LuaWhitelist
   public boolean isInvisible() {
      this.checkEntity();
      return this.entity.isInvisible();
   }

   @LuaWhitelist
   public boolean isAlive() {
      this.checkEntity();
      return this.entity.isAlive();
   }

   @LuaWhitelist
   public boolean isOnFire() {
      this.checkEntity();
      return this.entity.displayFireAnimation();
   }

   @LuaWhitelist
   public boolean hasAvatar() {
      this.checkEntity();
      return AvatarManager.getAvatar(this.entity) != null;
   }

   @LuaWhitelist
   public boolean isInRain() {
      this.checkEntity();
      if (this.entity.level() == null) {
         return false;
      }

      BlockPos pos = this.entity.blockPosition();
      return this.entity.level().isRainingAt(pos)
         || this.entity.level().isRainingAt(BlockPos.containing(this.entity.getX(), this.entity.getBoundingBox().maxY, this.entity.getZ()));
   }

   @LuaWhitelist
   public FiguraVec3 getBoundingBox() {
      this.checkEntity();
      EntityDimensions dimensions = this.entity.getDimensions(this.entity.getPose());
      return FiguraVec3.of(dimensions.width(), dimensions.height(), dimensions.width());
   }

   @LuaWhitelist
   public ItemStackAPI getItem(int index) {
      this.checkEntity();
      if (this.entity instanceof LivingEntity living && index >= 1) {
         int current = 1;

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (current++ == index) {
               return ItemStackAPI.verify(living.getItemBySlot(slot));
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @LuaWhitelist
   public LuaTable getNbt() {
      this.checkEntity();
      TagValueOutput view = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.entity.registryAccess());
      this.entity.saveWithoutId(view);
      return (LuaTable)NbtToLua.convert(view.buildResult());
   }

   @LuaWhitelist
   public Object[] getTargetedBlock(boolean ignoreLiquids, Double distance) {
      this.checkEntity();
      double reach = Math.max(-20.0, Math.min(distance == null ? 20.0 : distance, 20.0));
      if (this.entity.pick(reach, 1.0F, !ignoreLiquids) instanceof BlockHitResult blockHit && this.entity.level() != null) {
         BlockPos pos = blockHit.getBlockPos();
         return new Object[]{
            new BlockStateAPI(this.entity.level().getBlockState(pos), pos),
            FiguraVec3.fromVec3(blockHit.getLocation()),
            blockHit.getDirection().getSerializedName()
         };
      } else {
         return null;
      }
   }

   @LuaWhitelist
   public Object[] getTargetedEntity(Double distance) {
      this.checkEntity();
      double reach = Math.max(0.0, Math.min(distance == null ? 20.0 : distance, 20.0));
      Vec3 start = this.entity.getEyePosition(1.0F);
      HitResult blockHit = this.entity.pick(reach, 1.0F, false);
      double squaredDistance = blockHit.getLocation().distanceToSqr(start);
      Vec3 direction = this.entity.getViewVector(1.0F);
      Vec3 end = start.add(direction.scale(reach));
      AABB box = this.entity.getBoundingBox().expandTowards(direction.scale(reach)).inflate(1.0);
      EntityHitResult hit = ProjectileUtil.getEntityHitResult(this.entity, start, end, box, candidate -> candidate != this.entity, squaredDistance);
      return hit == null ? null : new Object[]{wrap(hit.getEntity()), FiguraVec3.fromVec3(hit.getLocation())};
   }

   @LuaWhitelist
   public List<EntityAPI<?>> getPassengers() {
      List<EntityAPI<?>> result = new ArrayList<>();
      this.entity.getPassengers().forEach(value -> result.add(wrap(value)));
      return result;
   }

   @LuaWhitelist
   public EntityAPI<?> getControllingPassenger() {
      this.checkEntity();
      return wrap(this.entity.getControllingPassenger());
   }

   @LuaWhitelist
   public EntityAPI<?> getControlledVehicle() {
      this.checkEntity();
      return wrap(this.entity.getControlledVehicle());
   }

   @LuaWhitelist
   public EntityAPI<?> getNearestEntity(String type, Double radius) {
      return null;
   }

   @LuaWhitelist
   public boolean isLiving() {
      return this.entity instanceof LivingEntity;
   }

   @LuaWhitelist
   public boolean isPlayer() {
      return this.entity instanceof Player;
   }

   @LuaWhitelist
   public int getFrozenTicks() {
      this.checkEntity();
      return this.entity.getTicksFrozen();
   }

   @LuaWhitelist
   public int getMaxAir() {
      this.checkEntity();
      return this.entity.getMaxAirSupply();
   }

   @LuaWhitelist
   public String getDimensionName() {
      this.checkEntity();
      return this.entity.level() == null ? "minecraft:overworld" : this.entity.level().dimension().identifier().toString();
   }

   @LuaWhitelist
   public double getEyeY() {
      this.checkEntity();
      return this.entity.getEyeY();
   }

   @LuaWhitelist
   public boolean isSilent() {
      this.checkEntity();
      return this.entity.isSilent();
   }

   @LuaWhitelist
   public boolean isSneaking() {
      this.checkEntity();
      return this.entity.isCrouching() || this.entity.isShiftKeyDown();
   }

   @LuaWhitelist
   public boolean isMoving(boolean ignoreY) {
      this.checkEntity();
      return this.entity.getX() != this.entity.xOld || !ignoreY && this.entity.getY() != this.entity.yOld || this.entity.getZ() != this.entity.zOld;
   }

   @LuaWhitelist
   public boolean isFalling() {
      this.checkEntity();
      return !this.entity.onGround() && this.entity.getY() < this.entity.yOld;
   }

   @LuaWhitelist
   public int getPermissionLevel() {
      return 2;
   }

   @LuaWhitelist
   public boolean hasContainer() {
      return false;
   }

   @LuaWhitelist
   public boolean hasInventory() {
      return this.entity instanceof Player;
   }

   @LuaWhitelist
   public LuaValue getVariable(String key) {
      this.checkEntity();
      Avatar avatar = AvatarManager.getAvatar(this.entity);
      LuaTable table = avatar != null && avatar.luaRuntime != null && avatar.luaRuntime.avatar_meta != null
         ? avatar.luaRuntime.avatar_meta.storedStuff
         : new LuaTable();
      LuaTable var4 = new ReadOnlyLuaTable(table);
      return (LuaValue)(key == null ? var4 : var4.get(key));
   }

   @LuaWhitelist
   public boolean __eq(EntityAPI<?> other) {
      return other != null && this.entityUUID.equals(other.entityUUID);
   }

   @LuaWhitelist
   public String __tostring() {
      return this.toString();
   }

   @Override
   public String toString() {
      this.checkEntity();
      return (this.isPlayer() ? this.getName() + " " : "") + this.getType() + " (Entity)";
   }
}

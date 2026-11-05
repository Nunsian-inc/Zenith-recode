package org.zenith.base.figura.lua.api.entity;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "LivingEntityAPI", value = "living_entity")
public class LivingEntityAPI<T extends LivingEntity> extends EntityAPI<T> {
   public LivingEntityAPI(T entity) {
      super(entity);
   }

   @LuaWhitelist
   public double getBodyYaw(Float delta) {
      this.checkEntity();
      float d = delta == null ? 1.0F : delta;
      return Mth.lerp(d, this.entity.yBodyRotO, this.entity.yBodyRot);
   }

   @LuaWhitelist
   public ItemStackAPI getHeldItem(boolean offhand) {
      this.checkEntity();
      return ItemStackAPI.verify(offhand ? this.entity.getOffhandItem() : this.entity.getMainHandItem());
   }

   @LuaWhitelist
   public ItemStackAPI getActiveItem() {
      this.checkEntity();
      return ItemStackAPI.verify(this.entity.getUseItem());
   }

   @LuaWhitelist
   public int getActiveItemTime() {
      this.checkEntity();
      return this.entity.getTicksUsingItem();
   }

   @LuaWhitelist
   public float getHealth() {
      this.checkEntity();
      return this.entity.getHealth();
   }

   @LuaWhitelist
   public float getMaxHealth() {
      this.checkEntity();
      return this.entity.getMaxHealth();
   }

   @LuaWhitelist
   public float getArmor() {
      this.checkEntity();
      return this.entity.getArmorValue();
   }

   @LuaWhitelist
   public float getDeathTime() {
      this.checkEntity();
      return this.entity.deathTime;
   }

   @LuaWhitelist
   public int getArrowCount() {
      this.checkEntity();
      return this.entity.getArrowCount();
   }

   @LuaWhitelist
   public int getStingerCount() {
      this.checkEntity();
      return this.entity.getStingerCount();
   }

   @LuaWhitelist
   public boolean isLeftHanded() {
      this.checkEntity();
      return this.entity.getMainArm() == HumanoidArm.LEFT;
   }

   @LuaWhitelist
   public boolean isUsingItem() {
      this.checkEntity();
      return this.entity.isUsingItem();
   }

   @LuaWhitelist
   public String getActiveHand() {
      this.checkEntity();
      return this.entity.getUsedItemHand().name();
   }

   @LuaWhitelist
   public boolean isClimbing() {
      this.checkEntity();
      return this.entity.onClimbable();
   }

   @LuaWhitelist
   public int getSwingTime() {
      this.checkEntity();
      return this.entity.swingState.ticks;
   }

   @LuaWhitelist
   public boolean isSwingingArm() {
      this.checkEntity();
      return this.entity.isSwinging();
   }

   @LuaWhitelist
   public String getSwingArm() {
      this.checkEntity();
      return this.entity.isSwinging() ? this.entity.getCurrentSwing().hand().name() : null;
   }

   @LuaWhitelist
   public int getSwingDuration() {
      return 6;
   }

   @LuaWhitelist
   public float getAbsorptionAmount() {
      this.checkEntity();
      return this.entity.getAbsorptionAmount();
   }

   @LuaWhitelist
   public boolean isGliding() {
      this.checkEntity();
      return this.entity.isFallFlying();
   }

   @LuaWhitelist
   public boolean isBlocking() {
      this.checkEntity();
      return this.entity.isBlocking();
   }

   @LuaWhitelist
   public boolean isVisuallySwimming() {
      this.checkEntity();
      return this.entity.isVisuallySwimming();
   }

   @LuaWhitelist
   public boolean riptideSpinning() {
      this.checkEntity();
      return this.entity.isAutoSpinAttack();
   }

   @LuaWhitelist
   public boolean isSensitiveToWater() {
      this.checkEntity();
      return this.entity.isSensitiveToWater();
   }

   @LuaWhitelist
   public String getEntityCategory() {
      this.checkEntity();
      EntityType<?> type = this.entity.getType();
      if (type.builtInRegistryHolder().is(EntityTypeTags.ARTHROPOD)) {
         return "ARTHROPOD";
      } else if (type.builtInRegistryHolder().is(EntityTypeTags.UNDEAD)) {
         return "UNDEAD";
      } else if (type.builtInRegistryHolder().is(EntityTypeTags.AQUATIC)) {
         return "WATER";
      } else {
         return type.builtInRegistryHolder().is(EntityTypeTags.ILLAGER) ? "ILLAGER" : "UNDEFINED";
      }
   }

   @Override
   public String toString() {
      return this.getType() + " (LivingEntity)";
   }
}

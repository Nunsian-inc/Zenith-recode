package org.zenith.utility.game;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.MinecraftAccess;

public final class CombatDamageCalculator implements MinecraftAccess {
   public static final float CONST_floatValue = 6.0F;
   public static final float CONST_floatValue2 = 5.0F;
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final BlockState blockState = Blocks.OBSIDIAN.defaultBlockState();
   private final Map<CombatDamageCalculator.Data, Float> items = new HashMap<>();
   private RegistryAccess dynamicRegistryManager;
   private Holder<Enchantment> registryEntry;
   private Holder<Enchantment> registryEntry2;

   public void reset() {
      this.items.clear();
   }

   public float method5452(Vec3 position, float floatValue, LivingEntity player, AABB box2, BlockPos blockPos2, BlockPos blockPos3) {
      return this.method5456(player, this.method5454(position, floatValue, player, box2, blockPos2, blockPos3));
   }

   public float method5453(Vec3 position, float floatValue, LivingEntity playerEntity, AABB box3) {
      return this.method5456(playerEntity, this.method5455(position, floatValue, playerEntity, box3));
   }

   public float method5454(Vec3 position, float floatValue, LivingEntity playerEntity, AABB box, BlockPos blockPos2, BlockPos blockPos3) {
      return this.method5459(position, floatValue, playerEntity, box, blockPos2, blockPos3, true);
   }

   public float method5455(Vec3 position, float floatValue, LivingEntity playerEntity, AABB box3) {
      return this.method5459(position, floatValue, playerEntity, box3, null, null, false);
   }

   public float method5456(LivingEntity playerEntity, float f) {
      if (playerEntity != null && !(f <= 0.0F)) {
         float f2 = method5463(playerEntity, f);
         f2 = method5464(playerEntity, f2);
         f2 = this.method5465(playerEntity, f2);
         return Math.max(0.0F, f2);
      } else {
         return 0.0F;
      }
   }

   public float method5457(LivingEntity playerEntity, float f) {
      if (playerEntity != null && !(f <= 0.0F)) {
         float f2 = method5464(playerEntity, method5463(playerEntity, f));
         this.method5466();
         int n = 0;

         for (EquipmentSlot equipmentSlot : equipmentSlots) {
            ItemStack itemBySlot = playerEntity.getItemBySlot(equipmentSlot);
            if (!itemBySlot.isEmpty() && this.registryEntry != null) {
               n += EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry, itemBySlot);
            }
         }

         return Math.max(0.0F, f2 * (1.0F - Mth.clamp(n, 0, 20) / 25.0F));
      } else {
         return 0.0F;
      }
   }

   public float method5458(LivingEntity playerEntity, float f) {
      if (playerEntity != null && !(f <= 0.0F)) {
         float f2 = f;
         float f3 = f * 25.0F + 2.0F;

         for (int i = 0; i < 24; i++) {
            float f4 = (f2 + f3) * 0.5F;
            if (this.method5456(playerEntity, f4) < f) {
               f2 = f4;
            } else {
               f3 = f4;
            }
         }

         return f3;
      } else {
         return 0.0F;
      }
   }

   private float method5459(Vec3 position, float floatValue, LivingEntity playerEntity, AABB box, BlockPos blockPos2, BlockPos blockPos3, boolean flag) {
      if (playerEntity != null && box != null && mc.level != null && !playerEntity.isRemoved()) {
         double d = floatValue * 2.0;
         Vec3 vec = new Vec3((box.minX + box.maxX) * 0.5, box.minY, (box.minZ + box.maxZ) * 0.5);
         double d2 = vec.distanceTo(position) / d;
         if (d2 > 1.0) {
            return 0.0F;
         } else {
            double d3 = flag ? this.method5460(position, playerEntity, box, blockPos2, blockPos3) : 1.0;
            double d4 = (1.0 - d2) * Math.max(0.0, d3);
            float f = (float)((d4 * d4 + d4) / 2.0 * 7.0 * d + 1.0);
            if (playerEntity instanceof Player) {
               f = this.method5462(f);
            }

            return Math.max(0.0F, f);
         }
      } else {
         return 0.0F;
      }
   }

   private float method5460(Vec3 position, LivingEntity playerEntity, AABB box, BlockPos blockPos2, BlockPos blockPos3) {
      CombatDamageCalculator.Data data = new CombatDamageCalculator.Data(playerEntity.getId(), position, box, blockPos2, blockPos3);
      Float f = this.items.get(data);
      if (f != null) {
         return f;
      } else {
         double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
         double d2 = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
         double d3 = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
         double d4 = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
         double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
         int n = 0;
         int n2 = 0;

         for (double alpha1 = 0.0; alpha1 <= 1.0; alpha1 += d) {
            for (double alpha12 = 0.0; alpha12 <= 1.0; alpha12 += d2) {
               for (double alpha13 = 0.0; alpha13 <= 1.0; alpha13 += d3) {
                  Vec3 vec = new Vec3(
                     Mth.lerp(alpha1, box.minX, box.maxX) + d4,
                     Mth.lerp(alpha12, box.minY, box.maxY),
                     Mth.lerp(alpha13, box.minZ, box.maxZ) + d5
                  );
                  n2++;
                  if (this.method5461(vec, position, blockPos2, blockPos3)) {
                     n++;
                  }
               }
            }
         }

         float f2 = n2 == 0 ? 0.0F : (float)n / n2;
         this.items.put(data, f2);
         return f2;
      }
   }

   private boolean method5461(Vec3 vec, Vec3 position, BlockPos blockPos2, BlockPos blockPos3) {
      return BlockGetter.traverseBlocks(vec, position, Boolean.TRUE, (var4x, pos) -> {
         if (blockPos2 != null && blockPos2.equals(pos)) {
            return null;
         } else {
            BlockState blockState2 = blockPos3 != null && blockPos3.equals(pos) ? blockState : mc.level.getBlockState(pos);
            if (blockState2.isAir()) {
               return null;
            } else {
               VoxelShape collisionShape = blockState2.getCollisionShape(mc.level, pos);
               return !collisionShape.isEmpty() && collisionShape.clip(vec, position, pos) != null ? Boolean.FALSE : null;
            }
         }
      }, flag -> flag);
   }

   private float method5462(float f) {
      return switch (mc.level.getDifficulty()) {
         case PEACEFUL -> 0.0F;
         case EASY -> Math.min(f / 2.0F + 1.0F, f);
         case NORMAL -> f;
         case HARD -> f * 1.5F;
      };
   }

   private static float method5463(LivingEntity playerEntity, float f) {
      float armorValue = playerEntity.getArmorValue();
      if (armorValue <= 0.0F) {
         return f;
      } else {
         float attributeValue = (float)playerEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
         float f2 = 2.0F + attributeValue / 4.0F;
         float f3 = Mth.clamp(armorValue - f / f2, armorValue * 0.2F, 20.0F);
         return f * (1.0F - f3 / 25.0F);
      }
   }

   private static float method5464(LivingEntity playerEntity, float f) {
      MobEffectInstance effect = playerEntity.getEffect(MobEffects.RESISTANCE);
      return effect == null ? f : f * (1.0F - Mth.clamp((effect.getAmplifier() + 1) * 0.2F, 0.0F, 1.0F));
   }

   private float method5465(LivingEntity playerEntity, float f) {
      this.method5466();
      int n = 0;

      for (EquipmentSlot equipmentSlot : equipmentSlots) {
         ItemStack itemBySlot = playerEntity.getItemBySlot(equipmentSlot);
         if (!itemBySlot.isEmpty()) {
            if (this.registryEntry != null) {
               n += EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry, itemBySlot);
            }

            if (this.registryEntry2 != null) {
               n += EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry2, itemBySlot) * 2;
            }
         }
      }

      return f * (1.0F - Mth.clamp(n, 0, 20) / 25.0F);
   }

   private void method5466() {
      RegistryAccess registryAccess = mc.level.registryAccess();
      if (this.dynamicRegistryManager != registryAccess) {
         Registry<net.minecraft.world.item.enchantment.Enchantment> orThrow = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);
         this.registryEntry = orThrow.get(Enchantments.PROTECTION).orElse(null);
         this.registryEntry2 = orThrow.get(Enchantments.BLAST_PROTECTION).orElse(null);
         this.dynamicRegistryManager = registryAccess;
      }
   }

   private static final class Data {
      private final int intValue;
      private final Vec3 vec3d;
      private final AABB box;
      private final BlockPos blockPos;
      private final BlockPos blockPos2;

      public int intValue() {
         return this.intValue;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public AABB box() {
         return this.box;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public BlockPos blockPos2() {
         return this.blockPos2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.box, other.box) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.blockPos2, other.blockPos2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, vec3d, box, blockPos, blockPos2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "vec3d=" + this.vec3d + ", " + "box=" + this.box + ", " + "blockPos=" + this.blockPos + ", " + "blockPos2=" + this.blockPos2 + "]";
      }

      private Data(int intValue, Vec3 vec3d, AABB box, BlockPos blockPos, BlockPos blockPos2) {
         this.intValue = intValue;
         this.vec3d = vec3d;
         this.box = box;
         this.blockPos = blockPos;
         this.blockPos2 = blockPos2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public AABB getBox() {
         return this.box;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public BlockPos getBlockPos2() {
         return this.blockPos2;
      }
   }
}

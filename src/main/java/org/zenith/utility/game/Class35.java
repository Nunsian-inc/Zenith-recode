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

public final class Class35 implements MinecraftAccess {
   public static final float CONST_floatValue = 6.0F;
   public static final float CONST_floatValue2 = 5.0F;
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final BlockState blockState = Blocks.OBSIDIAN.defaultBlockState();
   private final Map<Class35.Data, Float> items = new HashMap<>();
   private RegistryAccess dynamicRegistryManager;
   private Holder<Enchantment> registryEntry;
   private Holder<Enchantment> registryEntry2;

   public void reset() {
      this.items.clear();
   }

   public float method5452(Vec3 var1, float var2, LivingEntity var3, AABB var4, BlockPos var5, BlockPos var6) {
      return this.method5456(var3, this.method5454(var1, var2, var3, var4, var5, var6));
   }

   public float method5453(Vec3 var1, float var2, LivingEntity var3, AABB var4) {
      return this.method5456(var3, this.method5455(var1, var2, var3, var4));
   }

   public float method5454(Vec3 var1, float var2, LivingEntity var3, AABB var4, BlockPos var5, BlockPos var6) {
      return this.method5459(var1, var2, var3, var4, var5, var6, true);
   }

   public float method5455(Vec3 var1, float var2, LivingEntity var3, AABB var4) {
      return this.method5459(var1, var2, var3, var4, null, null, false);
   }

   public float method5456(LivingEntity var1, float var2) {
      if (var1 != null && !(var2 <= 0.0F)) {
         float var3 = method5463(var1, var2);
         var3 = method5464(var1, var3);
         var3 = this.method5465(var1, var3);
         return Math.max(0.0F, var3);
      } else {
         return 0.0F;
      }
   }

   public float method5457(LivingEntity var1, float var2) {
      if (var1 != null && !(var2 <= 0.0F)) {
         float var3 = method5464(var1, method5463(var1, var2));
         this.method5466();
         int var4 = 0;

         for (EquipmentSlot var8 : equipmentSlots) {
            ItemStack var9 = var1.getItemBySlot(var8);
            if (!var9.isEmpty() && this.registryEntry != null) {
               var4 += EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry, var9);
            }
         }

         return Math.max(0.0F, var3 * (1.0F - Mth.clamp(var4, 0, 20) / 25.0F));
      } else {
         return 0.0F;
      }
   }

   public float method5458(LivingEntity var1, float var2) {
      if (var1 != null && !(var2 <= 0.0F)) {
         float var3 = var2;
         float var4 = var2 * 25.0F + 2.0F;

         for (int var5 = 0; var5 < 24; var5++) {
            float var6 = (var3 + var4) * 0.5F;
            if (this.method5456(var1, var6) < var2) {
               var3 = var6;
            } else {
               var4 = var6;
            }
         }

         return var4;
      } else {
         return 0.0F;
      }
   }

   private float method5459(Vec3 var1, float var2, LivingEntity var3, AABB var4, BlockPos var5, BlockPos var6, boolean var7) {
      if (var3 != null && var4 != null && mc.level != null && !var3.isRemoved()) {
         double var8 = var2 * 2.0;
         Vec3 var10 = new Vec3((var4.minX + var4.maxX) * 0.5, var4.minY, (var4.minZ + var4.maxZ) * 0.5);
         double var11 = var10.distanceTo(var1) / var8;
         if (var11 > 1.0) {
            return 0.0F;
         } else {
            double var13 = var7 ? this.method5460(var1, var3, var4, var5, var6) : 1.0;
            double var15 = (1.0 - var11) * Math.max(0.0, var13);
            float var17 = (float)((var15 * var15 + var15) / 2.0 * 7.0 * var8 + 1.0);
            if (var3 instanceof Player) {
               var17 = this.method5462(var17);
            }

            return Math.max(0.0F, var17);
         }
      } else {
         return 0.0F;
      }
   }

   private float method5460(Vec3 var1, LivingEntity var2, AABB var3, BlockPos var4, BlockPos var5) {
      Class35.Data var6 = new Class35.Data(var2.getId(), var1, var3, var4, var5);
      Float var7 = this.items.get(var6);
      if (var7 != null) {
         return var7;
      } else {
         double var8 = 1.0 / ((var3.maxX - var3.minX) * 2.0 + 1.0);
         double var10 = 1.0 / ((var3.maxY - var3.minY) * 2.0 + 1.0);
         double var12 = 1.0 / ((var3.maxZ - var3.minZ) * 2.0 + 1.0);
         double var14 = (1.0 - Math.floor(1.0 / var8) * var8) / 2.0;
         double var16 = (1.0 - Math.floor(1.0 / var12) * var12) / 2.0;
         int var18 = 0;
         int var19 = 0;

         for (double var20 = 0.0; var20 <= 1.0; var20 += var8) {
            for (double var22 = 0.0; var22 <= 1.0; var22 += var10) {
               for (double var24 = 0.0; var24 <= 1.0; var24 += var12) {
                  Vec3 var26 = new Vec3(
                     Mth.lerp(var20, var3.minX, var3.maxX) + var14,
                     Mth.lerp(var22, var3.minY, var3.maxY),
                     Mth.lerp(var24, var3.minZ, var3.maxZ) + var16
                  );
                  var19++;
                  if (this.method5461(var26, var1, var4, var5)) {
                     var18++;
                  }
               }
            }
         }

         float var27 = var19 == 0 ? 0.0F : (float)var18 / var19;
         this.items.put(var6, var27);
         return var27;
      }
   }

   private boolean method5461(Vec3 var1, Vec3 var2, BlockPos var3, BlockPos var4) {
      return BlockGetter.traverseBlocks(var1, var2, Boolean.TRUE, (var4x, var5) -> {
         if (var3 != null && var3.equals(var5)) {
            return null;
         } else {
            BlockState var6 = var4 != null && var4.equals(var5) ? blockState : mc.level.getBlockState(var5);
            if (var6.isAir()) {
               return null;
            } else {
               VoxelShape var7 = var6.getCollisionShape(mc.level, var5);
               return !var7.isEmpty() && var7.clip(var1, var2, var5) != null ? Boolean.FALSE : null;
            }
         }
      }, var0 -> var0);
   }

   private float method5462(float var1) {
      return switch (mc.level.getDifficulty()) {
         case PEACEFUL -> 0.0F;
         case EASY -> Math.min(var1 / 2.0F + 1.0F, var1);
         case NORMAL -> var1;
         case HARD -> var1 * 1.5F;
      };
   }

   private static float method5463(LivingEntity var0, float var1) {
      float var2 = var0.getArmorValue();
      if (var2 <= 0.0F) {
         return var1;
      } else {
         float var3 = (float)var0.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
         float var4 = 2.0F + var3 / 4.0F;
         float var5 = Mth.clamp(var2 - var1 / var4, var2 * 0.2F, 20.0F);
         return var1 * (1.0F - var5 / 25.0F);
      }
   }

   private static float method5464(LivingEntity var0, float var1) {
      MobEffectInstance var2 = var0.getEffect(MobEffects.RESISTANCE);
      return var2 == null ? var1 : var1 * (1.0F - Mth.clamp((var2.getAmplifier() + 1) * 0.2F, 0.0F, 1.0F));
   }

   private float method5465(LivingEntity var1, float var2) {
      this.method5466();
      int var3 = 0;

      for (EquipmentSlot var7 : equipmentSlots) {
         ItemStack var8 = var1.getItemBySlot(var7);
         if (!var8.isEmpty()) {
            if (this.registryEntry != null) {
               var3 += EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry, var8);
            }

            if (this.registryEntry2 != null) {
               var3 += EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry2, var8) * 2;
            }
         }
      }

      return var2 * (1.0F - Mth.clamp(var3, 0, 20) / 25.0F);
   }

   private void method5466() {
      RegistryAccess var1 = mc.level.registryAccess();
      if (this.dynamicRegistryManager != var1) {
         Registry<net.minecraft.world.item.enchantment.Enchantment> var2 = var1.lookupOrThrow(Registries.ENCHANTMENT);
         this.registryEntry = var2.get(Enchantments.PROTECTION).orElse(null);
         this.registryEntry2 = var2.get(Enchantments.BLAST_PROTECTION).orElse(null);
         this.dynamicRegistryManager = var1;
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

      private Data(int var1, Vec3 var2, AABB var3, BlockPos var4, BlockPos var5) {
         this.intValue = var1;
         this.vec3d = var2;
         this.box = var3;
         this.blockPos = var4;
         this.blockPos2 = var5;
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

package org.zenith.client.scripting;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Scriptable;
import org.zenith.Zenith;

public final class ScriptEntityApi {
   private static final float[] CONST_floatValues = new float[]{1.0F};

   private ScriptEntityApi() {
   }

   public static void method6573(float var0) {
      CONST_floatValues[0] = var0;
   }

   public static float method6574() {
      return CONST_floatValues[0];
   }

   public static Minecraft method6575() {
      return Minecraft.getInstance();
   }

   public static Object method6576(Scriptable var0, Entity var1) {
      if (var1 == null) {
         return null;
      } else {
         LivingEntity var2 = var1 instanceof LivingEntity var3 ? var3 : null;
         ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var0);
         var4.method6274("id", var1x -> (double)var1.getId());
         var4.method6274("name", var1x -> var1.getName().getString());
         var4.method6274("displayName", var1x -> var1.getDisplayName() == null ? var1.getName().getString() : var1.getDisplayName().getString());
         var4.method6274("type", var1x -> BuiltInRegistries.ENTITY_TYPE.getKey(var1.getType()).toString());
         var4.method6274("uuid", var1x -> var1.getStringUUID());
         var4.method6274("x", var1x -> var1.getX());
         var4.method6274("y", var1x -> var1.getY());
         var4.method6274("z", var1x -> var1.getZ());
         var4.method6274("pos", var2x -> ScriptValueUtils.method6270(var0, var1.getX(), var1.getY(), var1.getZ()));
         var4.method6274("prevPos", var2x -> ScriptValueUtils.method6270(var0, var1.xo, var1.yo, var1.zo));
         var4.method6274(
            "renderPos",
            var2x -> {
               float var3x = ScriptValueUtils.method6252(var2x, 0) ? (float)ScriptValueUtils.method6256(var2x, 0, 1.0) : method6574();
               return ScriptValueUtils.method6270(
                  var0,
                  var1.xo + (var1.getX() - var1.xo) * var3x,
                  var1.yo + (var1.getY() - var1.yo) * var3x,
                  var1.zo + (var1.getZ() - var1.zo) * var3x
               );
            }
         );
         var4.method6274("velocity", var2x -> {
            Vec3 var3x = var1.getDeltaMovement();
            return ScriptValueUtils.method6270(var0, var3x.x, var3x.y, var3x.z);
         });
         var4.method6274("yaw", var1x -> (double)var1.getYRot());
         var4.method6274("pitch", var1x -> (double)var1.getXRot());
         var4.method6274("headYaw", var1x -> (double)var1.getYHeadRot());
         var4.method6274("eyeY", var1x -> var1.getEyeY());
         var4.method6274("width", var1x -> (double)var1.getBbWidth());
         var4.method6274("height", var1x -> (double)var1.getBbHeight());
         var4.method6274("box", var2x -> method6577(var0, var1.getBoundingBox()));
         var4.method6274("alive", var1x -> var1.isAlive() && !var1.isRemoved());
         var4.method6274("isPlayer", var1x -> var1 instanceof Player);
         var4.method6274("isLiving", var1x -> var2 != null);
         var4.method6274("isSelf", var1x -> var1 == method6575().player);
         var4.method6274("isFriend", var1x -> var1 instanceof Player && Zenith.getInstance().getFriends().method7626(var1));
         var4.method6274("onGround", var1x -> var1.onGround());
         var4.method6274("sneaking", var1x -> var1.isShiftKeyDown());
         var4.method6274("sprinting", var1x -> var1.isSprinting());
         var4.method6274("invisible", var1x -> var1.isInvisible());
         var4.method6274("onFire", var1x -> var1.isOnFire());
         var4.method6274("inWater", var1x -> var1.isInWater());
         var4.method6274("glowing", var1x -> var1.isCurrentlyGlowing());
         var4.method6274("fallDistance", var1x -> var1.fallDistance);
         var4.method6274("age", var1x -> (double)var1.tickCount);
         var4.method6274("distance", var1x -> method6575().player == null ? 0.0 : method6575().player.distanceTo(var1));
         var4.method6274(
            "distanceTo",
            var1x -> Math.sqrt(
               var1.distanceToSqr(
                  ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
               )
            )
         );
         var4.method6274("canSee", var1x -> method6575().player != null && method6575().player.hasLineOfSight(var1));
         var4.method6274("health", var1x -> var2 == null ? 0.0 : var2.getHealth());
         var4.method6274("maxHealth", var1x -> var2 == null ? 0.0 : var2.getMaxHealth());
         var4.method6274("absorption", var1x -> var2 == null ? 0.0 : var2.getAbsorptionAmount());
         var4.method6274("armor", var1x -> var2 == null ? 0.0 : var2.getArmorValue());
         var4.method6274("hurtTime", var1x -> var2 == null ? 0.0 : var2.hurtTime);
         var4.method6274("gliding", var1x -> var2 != null && var2.isFallFlying());
         var4.method6274("usingItem", var1x -> var2 != null && var2.isUsingItem());
         var4.method6274("blocking", var1x -> var2 != null && var2.isBlocking());
         var4.method6274("mainHand", var2x -> var2 == null ? method6579(var0, ItemStack.EMPTY) : method6579(var0, var2.getMainHandItem()));
         var4.method6274("offHand", var2x -> var2 == null ? method6579(var0, ItemStack.EMPTY) : method6579(var0, var2.getOffhandItem()));
         var4.method6274("armorItems", var2x -> {
            ArrayList var3x = new ArrayList();
            if (var2 != null) {
               for (EquipmentSlot var7 : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                  var3x.add(method6579(var0, var2.getItemBySlot(var7)));
               }
            }

            return ScriptValueUtils.method6267(var0, var3x);
         });
         var4.method6274("effects", var2x -> {
            ArrayList var3x = new ArrayList();
            if (var2 != null) {
               for (MobEffectInstance var5 : var2.getActiveEffects()) {
                  var3x.add(method6578(var0, var5));
               }
            }

            return ScriptValueUtils.method6267(var0, var3x);
         });
         var4.method6274("ping", var1x -> {
            if (var1 instanceof Player && method6575().getConnection() != null) {
               PlayerInfo var2x = method6575().getConnection().getPlayerInfo(var1.getUUID());
               return var2x == null ? 0.0 : var2x.getLatency();
            } else {
               return 0.0;
            }
         });
         return var4.getScriptableObject();
      }
   }

   public static Scriptable method6577(Scriptable var0, AABB var1) {
      return ScriptValueUtils.method6247(var0)
         .method6277("x1", var1.minX)
         .method6277("y1", var1.minY)
         .method6277("z1", var1.minZ)
         .method6277("x2", var1.maxX)
         .method6277("y2", var1.maxY)
         .method6277("z2", var1.maxZ)
         .method6277("width", var1.getXsize())
         .method6277("height", var1.getYsize())
         .method6277("depth", var1.getZsize())
         .getScriptableObject();
   }

   public static Scriptable method6578(Scriptable var0, MobEffectInstance var1) {
      return ScriptValueUtils.method6247(var0)
         .method6277("id", var1.getEffect().getRegisteredName())
         .method6277("level", var1.getAmplifier() + 1.0)
         .method6277("amplifier", (double)var1.getAmplifier())
         .method6277("duration", (double)var1.getDuration())
         .method6277("ambient", var1.isAmbient())
         .getScriptableObject();
   }

   public static Object method6579(Scriptable var0, ItemStack var1) {
      ItemStack var2 = var1 == null ? ItemStack.EMPTY : var1;
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var0);
      String var4 = BuiltInRegistries.ITEM.getKey(var2.getItem()).toString();
      var3.method6277("empty", var2.isEmpty());
      var3.method6277("id", var4);
      var3.method6277("count", (double)var2.getCount());
      var3.method6277("name", var2.isEmpty() ? "" : var2.getHoverName().getString());
      var3.method6274("is", var1x -> matches(var4, ScriptValueUtils.method6254(var1x, 0, "")));
      var3.method6274("details", var2x -> ScriptWorldApi.method4818(var0, var2));
      var3.method6274("toString", var1x -> var4);
      if (var2.isEmpty()) {
         return var3.getScriptableObject();
      } else {
         var3.method6277("maxCount", (double)var2.getMaxStackSize());
         var3.method6277("damage", (double)var2.getDamageValue());
         var3.method6277("maxDamage", (double)var2.getMaxDamage());
         var3.method6277("durability", var2.getMaxDamage() <= 0 ? 1.0 : 1.0 - (double)var2.getDamageValue() / var2.getMaxDamage());
         var3.method6277("damageable", var2.isDamageableItem());
         var3.method6277("stackable", var2.isStackable());
         var3.method6277("food", var2.get(DataComponents.FOOD) != null);
         var3.method6277("block", var2.getItem() instanceof BlockItem);
         var3.method6277("customName", var2.get(DataComponents.CUSTOM_NAME) != null);
         var3.method6277("cooldown", method6575().player != null && method6575().player.getCooldowns().isOnCooldown(var2));
         ArrayList var5 = new ArrayList();
         ItemLore var6 = var2.get(DataComponents.LORE);
         if (var6 != null) {
            for (Component var8 : var6.lines()) {
               if (var5.size() >= 64) {
                  break;
               }

               var5.add(var8.getString());
            }
         }

         var3.method6277("lore", ScriptValueUtils.method6267(var0, var5));
         ArrayList var10 = new ArrayList();

         for (Entry var9 : var2.getEnchantments().entrySet()) {
            var10.add(
               ScriptValueUtils.method6247(var0)
                  .method6277("id", ((Holder)var9.getKey()).getRegisteredName())
                  .method6277("level", (double)var9.getIntValue())
                  .getScriptableObject()
            );
         }

         var3.method6277("enchants", ScriptValueUtils.method6267(var0, var10));
         var3.method6274("enchant", var1x -> {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");

            for (Entry var4x : var2.getEnchantments().entrySet()) {
               if (matches(((Holder)var4x.getKey()).getRegisteredName(), var2x)) {
                  return (double)var4x.getIntValue();
               }
            }

            return 0.0;
         });
         return var3.getScriptableObject();
      }
   }

   public static boolean matches(String var0, String var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = var1.toLowerCase(Locale.ROOT);
         return var0.equals(var2) || var0.equals("minecraft:" + var2);
      } else {
         return false;
      }
   }

   public static Object method6580(Scriptable var0, BlockPos var1) {
      if (method6575().level == null) {
         ScriptValueUtils.Entry2 var6 = ScriptValueUtils.method6247(var0)
            .method6277("id", "minecraft:air")
            .method6277("x", (double)var1.getX())
            .method6277("y", (double)var1.getY())
            .method6277("z", (double)var1.getZ())
            .method6277("air", true)
            .method6277("liquid", false)
            .method6277("solid", false)
            .method6277("replaceable", true)
            .method6277("full", false)
            .method6277("hardness", 0.0)
            .method6277("luminance", 0.0)
            .method6277("properties", ScriptValueUtils.method6247(var0).getScriptableObject());
         var6.method6274("is", var0x -> matches("minecraft:air", ScriptValueUtils.method6254(var0x, 0, "")));
         var6.method6274("toString", var0x -> "minecraft:air");
         var6.method6274("valueOf", var0x -> "minecraft:air");
         return var6.getScriptableObject();
      } else {
         BlockState var2 = method6575().level.getBlockState(var1);
         String var3 = BuiltInRegistries.BLOCK.getKey(var2.getBlock()).toString();
         ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var0)
            .method6277("id", var3)
            .method6277("x", (double)var1.getX())
            .method6277("y", (double)var1.getY())
            .method6277("z", (double)var1.getZ())
            .method6277("air", var2.isAir())
            .method6277("liquid", !var2.getFluidState().isEmpty())
            .method6277("solid", var2.isRedstoneConductor(method6575().level, var1))
            .method6277("replaceable", var2.canBeReplaced())
            .method6277("full", var2.isCollisionShapeFullBlock(method6575().level, var1))
            .method6277("hardness", (double)var2.getDestroySpeed(method6575().level, var1))
            .method6277("luminance", (double)var2.getLightEmission());
         var4.method6274("is", var1x -> matches(var3, ScriptValueUtils.method6254(var1x, 0, "")));
         var4.method6274("toString", var1x -> var3);
         var4.method6274("valueOf", var1x -> var3);
         ScriptValueUtils.Entry2 var5 = ScriptValueUtils.method6247(var0);
         var2.getValues().forEach(var1x -> var5.method6277(var1x.property().getName(), var1x.valueName().toLowerCase(Locale.ROOT)));
         var4.method6277("properties", var5.getScriptableObject());
         return var4.getScriptableObject();
      }
   }

   public static Object method6581(Scriptable var0, HitResult var1) {
      if (var1 != null && var1.getType() != Type.MISS) {
         Vec3 var2 = var1.getLocation();
         ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var0).method6277("pos", ScriptValueUtils.method6270(var0, var2.x, var2.y, var2.z));
         if (var1 instanceof BlockHitResult var4) {
            var3.method6277("type", "block");
            var3.method6277("side", var4.getDirection().getSerializedName());
            var3.method6277("block", method6580(var0, var4.getBlockPos()));
         } else if (var1 instanceof EntityHitResult var5) {
            var3.method6277("type", "entity");
            var3.method6277("entity", method6576(var0, var5.getEntity()));
         }

         return var3.getScriptableObject();
      } else {
         return ScriptValueUtils.method6247(var0).method6277("type", "miss").getScriptableObject();
      }
   }

   public static HitResult method6582(Vec3 var0, Vec3 var1, boolean var2) {
      return method6575().level != null && method6575().player != null
         ? method6575().level.clip(new ClipContext(var0, var1, Block.OUTLINE, var2 ? Fluid.ANY : Fluid.NONE, method6575().player))
         : null;
   }
}

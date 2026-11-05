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

   public static void method6573(float tickDelta) {
      CONST_floatValues[0] = tickDelta;
   }

   public static float method6574() {
      return CONST_floatValues[0];
   }

   public static Minecraft method6575() {
      return Minecraft.getInstance();
   }

   public static Object method6576(Scriptable scope, Entity entity) {
      if (entity == null) {
         return null;
      } else {
         LivingEntity livingEntity = entity instanceof LivingEntity livingEntity2 ? livingEntity2 : null;
         ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
         entry2.method6274("id", var1x -> (double)entity.getId());
         entry2.method6274("name", var1x -> entity.getName().getString());
         entry2.method6274("displayName", var1x -> entity.getDisplayName() == null ? entity.getName().getString() : entity.getDisplayName().getString());
         entry2.method6274("type", var1x -> BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
         entry2.method6274("uuid", var1x -> entity.getStringUUID());
         entry2.method6274("x", var1x -> entity.getX());
         entry2.method6274("y", var1x -> entity.getY());
         entry2.method6274("z", var1x -> entity.getZ());
         entry2.method6274("pos", var2x -> ScriptValueUtils.method6270(scope, entity.getX(), entity.getY(), entity.getZ()));
         entry2.method6274("prevPos", var2x -> ScriptValueUtils.method6270(scope, entity.xo, entity.yo, entity.zo));
         entry2.method6274(
            "renderPos",
            var2x -> {
               float var3x = ScriptValueUtils.method6252(var2x, 0) ? (float)ScriptValueUtils.method6256(var2x, 0, 1.0) : method6574();
               return ScriptValueUtils.method6270(
                  scope,
                  entity.xo + (entity.getX() - entity.xo) * var3x,
                  entity.yo + (entity.getY() - entity.yo) * var3x,
                  entity.zo + (entity.getZ() - entity.zo) * var3x
               );
            }
         );
         entry2.method6274("velocity", var2x -> {
            Vec3 var3x = entity.getDeltaMovement();
            return ScriptValueUtils.method6270(scope, var3x.x, var3x.y, var3x.z);
         });
         entry2.method6274("yaw", var1x -> (double)entity.getYRot());
         entry2.method6274("pitch", var1x -> (double)entity.getXRot());
         entry2.method6274("headYaw", var1x -> (double)entity.getYHeadRot());
         entry2.method6274("eyeY", var1x -> entity.getEyeY());
         entry2.method6274("width", var1x -> (double)entity.getBbWidth());
         entry2.method6274("height", var1x -> (double)entity.getBbHeight());
         entry2.method6274("box", var2x -> method6577(scope, entity.getBoundingBox()));
         entry2.method6274("alive", var1x -> entity.isAlive() && !entity.isRemoved());
         entry2.method6274("isPlayer", var1x -> entity instanceof Player);
         entry2.method6274("isLiving", var1x -> livingEntity != null);
         entry2.method6274("isSelf", var1x -> entity == method6575().player);
         entry2.method6274("isFriend", var1x -> entity instanceof Player && Zenith.getInstance().getFriends().method7626(entity));
         entry2.method6274("onGround", var1x -> entity.onGround());
         entry2.method6274("sneaking", var1x -> entity.isShiftKeyDown());
         entry2.method6274("sprinting", var1x -> entity.isSprinting());
         entry2.method6274("invisible", var1x -> entity.isInvisible());
         entry2.method6274("onFire", var1x -> entity.isOnFire());
         entry2.method6274("inWater", var1x -> entity.isInWater());
         entry2.method6274("glowing", var1x -> entity.isCurrentlyGlowing());
         entry2.method6274("fallDistance", var1x -> entity.fallDistance);
         entry2.method6274("age", var1x -> (double)entity.tickCount);
         entry2.method6274("distance", var1x -> method6575().player == null ? 0.0 : method6575().player.distanceTo(entity));
         entry2.method6274(
            "distanceTo",
            var1x -> Math.sqrt(
               entity.distanceToSqr(
                  ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
               )
            )
         );
         entry2.method6274("canSee", var1x -> method6575().player != null && method6575().player.hasLineOfSight(entity));
         entry2.method6274("health", var1x -> livingEntity == null ? 0.0 : livingEntity.getHealth());
         entry2.method6274("maxHealth", var1x -> livingEntity == null ? 0.0 : livingEntity.getMaxHealth());
         entry2.method6274("absorption", var1x -> livingEntity == null ? 0.0 : livingEntity.getAbsorptionAmount());
         entry2.method6274("armor", var1x -> livingEntity == null ? 0.0 : livingEntity.getArmorValue());
         entry2.method6274("hurtTime", var1x -> livingEntity == null ? 0.0 : livingEntity.hurtTime);
         entry2.method6274("gliding", var1x -> livingEntity != null && livingEntity.isFallFlying());
         entry2.method6274("usingItem", var1x -> livingEntity != null && livingEntity.isUsingItem());
         entry2.method6274("blocking", var1x -> livingEntity != null && livingEntity.isBlocking());
         entry2.method6274("mainHand", var2x -> livingEntity == null ? method6579(scope, ItemStack.EMPTY) : method6579(scope, livingEntity.getMainHandItem()));
         entry2.method6274("offHand", var2x -> livingEntity == null ? method6579(scope, ItemStack.EMPTY) : method6579(scope, livingEntity.getOffhandItem()));
         entry2.method6274("armorItems", var2x -> {
            ArrayList var3x = new ArrayList();
            if (livingEntity != null) {
               for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                  var3x.add(method6579(scope, livingEntity.getItemBySlot(slot)));
               }
            }

            return ScriptValueUtils.method6267(scope, var3x);
         });
         entry2.method6274("effects", var2x -> {
            ArrayList var3x = new ArrayList();
            if (livingEntity != null) {
               for (MobEffectInstance activeEffect : livingEntity.getActiveEffects()) {
                  var3x.add(method6578(scope, activeEffect));
               }
            }

            return ScriptValueUtils.method6267(scope, var3x);
         });
         entry2.method6274("ping", var1x -> {
            if (entity instanceof Player && method6575().getConnection() != null) {
               PlayerInfo var2x = method6575().getConnection().getPlayerInfo(entity.getUUID());
               return var2x == null ? 0.0 : var2x.getLatency();
            } else {
               return 0.0;
            }
         });
         return entry2.getScriptableObject();
      }
   }

   public static Scriptable method6577(Scriptable scope, AABB boundingBox) {
      return ScriptValueUtils.method6247(scope)
         .method6277("x1", boundingBox.minX)
         .method6277("y1", boundingBox.minY)
         .method6277("z1", boundingBox.minZ)
         .method6277("x2", boundingBox.maxX)
         .method6277("y2", boundingBox.maxY)
         .method6277("z2", boundingBox.maxZ)
         .method6277("width", boundingBox.getXsize())
         .method6277("height", boundingBox.getYsize())
         .method6277("depth", boundingBox.getZsize())
         .getScriptableObject();
   }

   public static Scriptable method6578(Scriptable scope, MobEffectInstance activeEffect) {
      return ScriptValueUtils.method6247(scope)
         .method6277("id", activeEffect.getEffect().getRegisteredName())
         .method6277("level", activeEffect.getAmplifier() + 1.0)
         .method6277("amplifier", (double)activeEffect.getAmplifier())
         .method6277("duration", (double)activeEffect.getDuration())
         .method6277("ambient", activeEffect.isAmbient())
         .getScriptableObject();
   }

   public static Object method6579(Scriptable scope, ItemStack mainHandItem) {
      ItemStack stack = mainHandItem == null ? ItemStack.EMPTY : mainHandItem;
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      String string = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
      entry2.method6277("empty", stack.isEmpty());
      entry2.method6277("id", string);
      entry2.method6277("count", (double)stack.getCount());
      entry2.method6277("name", stack.isEmpty() ? "" : stack.getHoverName().getString());
      entry2.method6274("is", var1x -> matches(string, ScriptValueUtils.method6254(var1x, 0, "")));
      entry2.method6274("details", var2x -> ScriptWorldApi.method4818(scope, stack));
      entry2.method6274("toString", var1x -> string);
      if (stack.isEmpty()) {
         return entry2.getScriptableObject();
      } else {
         entry2.method6277("maxCount", (double)stack.getMaxStackSize());
         entry2.method6277("damage", (double)stack.getDamageValue());
         entry2.method6277("maxDamage", (double)stack.getMaxDamage());
         entry2.method6277("durability", stack.getMaxDamage() <= 0 ? 1.0 : 1.0 - (double)stack.getDamageValue() / stack.getMaxDamage());
         entry2.method6277("damageable", stack.isDamageableItem());
         entry2.method6277("stackable", stack.isStackable());
         entry2.method6277("food", stack.get(DataComponents.FOOD) != null);
         entry2.method6277("block", stack.getItem() instanceof BlockItem);
         entry2.method6277("customName", stack.get(DataComponents.CUSTOM_NAME) != null);
         entry2.method6277("cooldown", method6575().player != null && method6575().player.getCooldowns().isOnCooldown(stack));
         ArrayList arrayList = new ArrayList();
         ItemLore itemLore = stack.get(DataComponents.LORE);
         if (itemLore != null) {
            for (Component line : itemLore.lines()) {
               if (arrayList.size() >= 64) {
                  break;
               }

               arrayList.add(line.getString());
            }
         }

         entry2.method6277("lore", ScriptValueUtils.method6267(scope, arrayList));
         ArrayList arrayList2 = new ArrayList();

         for (Entry entry : stack.getEnchantments().entrySet()) {
            arrayList2.add(
               ScriptValueUtils.method6247(scope)
                  .method6277("id", ((Holder)entry.getKey()).getRegisteredName())
                  .method6277("level", (double)entry.getIntValue())
                  .getScriptableObject()
            );
         }

         entry2.method6277("enchants", ScriptValueUtils.method6267(scope, arrayList2));
         entry2.method6274("enchant", var1x -> {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");

            for (Entry var4x : stack.getEnchantments().entrySet()) {
               if (matches(((Holder)var4x.getKey()).getRegisteredName(), var2x)) {
                  return (double)var4x.getIntValue();
               }
            }

            return 0.0;
         });
         return entry2.getScriptableObject();
      }
   }

   public static boolean matches(String string, String str) {
      if (str != null && !str.isEmpty()) {
         String lowerCase = str.toLowerCase(Locale.ROOT);
         return string.equals(lowerCase) || string.equals("minecraft:" + lowerCase);
      } else {
         return false;
      }
   }

   public static Object method6580(Scriptable scope, BlockPos containing) {
      if (method6575().level == null) {
         ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope)
            .method6277("id", "minecraft:air")
            .method6277("x", (double)containing.getX())
            .method6277("y", (double)containing.getY())
            .method6277("z", (double)containing.getZ())
            .method6277("air", true)
            .method6277("liquid", false)
            .method6277("solid", false)
            .method6277("replaceable", true)
            .method6277("full", false)
            .method6277("hardness", 0.0)
            .method6277("luminance", 0.0)
            .method6277("properties", ScriptValueUtils.method6247(scope).getScriptableObject());
         entry2.method6274("is", var0x -> matches("minecraft:air", ScriptValueUtils.method6254(var0x, 0, "")));
         entry2.method6274("toString", var0x -> "minecraft:air");
         entry2.method6274("valueOf", var0x -> "minecraft:air");
         return entry2.getScriptableObject();
      } else {
         BlockState blockState = method6575().level.getBlockState(containing);
         String string = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
         ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope)
            .method6277("id", string)
            .method6277("x", (double)containing.getX())
            .method6277("y", (double)containing.getY())
            .method6277("z", (double)containing.getZ())
            .method6277("air", blockState.isAir())
            .method6277("liquid", !blockState.getFluidState().isEmpty())
            .method6277("solid", blockState.isRedstoneConductor(method6575().level, containing))
            .method6277("replaceable", blockState.canBeReplaced())
            .method6277("full", blockState.isCollisionShapeFullBlock(method6575().level, containing))
            .method6277("hardness", (double)blockState.getDestroySpeed(method6575().level, containing))
            .method6277("luminance", (double)blockState.getLightEmission());
         entry2.method6274("is", var1x -> matches(string, ScriptValueUtils.method6254(var1x, 0, "")));
         entry2.method6274("toString", var1x -> string);
         entry2.method6274("valueOf", var1x -> string);
         ScriptValueUtils.Entry2 entry22 = ScriptValueUtils.method6247(scope);
         blockState.getValues().forEach(var1x -> entry22.method6277(var1x.property().getName(), var1x.valueName().toLowerCase(Locale.ROOT)));
         entry2.method6277("properties", entry22.getScriptableObject());
         return entry2.getScriptableObject();
      }
   }

   public static Object method6581(Scriptable scope, HitResult hitResult) {
      if (hitResult != null && hitResult.getType() != Type.MISS) {
         Vec3 location = hitResult.getLocation();
         ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope).method6277("pos", ScriptValueUtils.method6270(scope, location.x, location.y, location.z));
         if (hitResult instanceof BlockHitResult blockHitResult) {
            entry2.method6277("type", "block");
            entry2.method6277("side", blockHitResult.getDirection().getSerializedName());
            entry2.method6277("block", method6580(scope, blockHitResult.getBlockPos()));
         } else if (hitResult instanceof EntityHitResult entityHitResult) {
            entry2.method6277("type", "entity");
            entry2.method6277("entity", method6576(scope, entityHitResult.getEntity()));
         }

         return entry2.getScriptableObject();
      } else {
         return ScriptValueUtils.method6247(scope).method6277("type", "miss").getScriptableObject();
      }
   }

   public static HitResult method6582(Vec3 eyePosition, Vec3 to, boolean flag) {
      return method6575().level != null && method6575().player != null
         ? method6575().level.clip(new ClipContext(eyePosition, to, Block.OUTLINE, flag ? Fluid.ANY : Fluid.NONE, method6575().player))
         : null;
   }
}

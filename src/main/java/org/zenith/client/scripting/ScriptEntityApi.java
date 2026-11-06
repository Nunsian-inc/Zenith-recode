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
   private static final float[] TICK_DELTA = new float[]{1.0F};

   private ScriptEntityApi() {
   }

   public static void method6573(float tickDelta) {
      TICK_DELTA[0] = tickDelta;
   }

   public static float method6574() {
      return TICK_DELTA[0];
   }

   public static Minecraft method6575() {
      return Minecraft.getInstance();
   }

   public static Object method6576(Scriptable scope, Entity entity) {
      if (entity == null) {
         return null;
      } else {
         LivingEntity livingEntity = entity instanceof LivingEntity livingEntity2 ? livingEntity2 : null;
         ScriptValueUtils.Entry2 itemData = ScriptValueUtils.method6247(scope);
         itemData.method6274("id", args -> (double)entity.getId());
         itemData.method6274("name", args -> entity.getName().getString());
         itemData.method6274("displayName", args -> entity.getDisplayName() == null ? entity.getName().getString() : entity.getDisplayName().getString());
         itemData.method6274("type", args -> BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
         itemData.method6274("uuid", args -> entity.getStringUUID());
         itemData.method6274("x", args -> entity.getX());
         itemData.method6274("y", args -> entity.getY());
         itemData.method6274("z", args -> entity.getZ());
         itemData.method6274("pos", args -> ScriptValueUtils.method6270(scope, entity.getX(), entity.getY(), entity.getZ()));
         itemData.method6274("prevPos", args -> ScriptValueUtils.method6270(scope, entity.xo, entity.yo, entity.zo));
         itemData.method6274(
            "renderPos",
            args -> {
               float tickDelta = ScriptValueUtils.method6252(args, 0) ? (float)ScriptValueUtils.method6256(args, 0, 1.0) : method6574();
               return ScriptValueUtils.method6270(
                  scope,
                  entity.xo + (entity.getX() - entity.xo) * tickDelta,
                  entity.yo + (entity.getY() - entity.yo) * tickDelta,
                  entity.zo + (entity.getZ() - entity.zo) * tickDelta
               );
            }
         );
         itemData.method6274("velocity", args -> {
            Vec3 velocity = entity.getDeltaMovement();
            return ScriptValueUtils.method6270(scope, velocity.x, velocity.y, velocity.z);
         });
         itemData.method6274("yaw", args -> (double)entity.getYRot());
         itemData.method6274("pitch", args -> (double)entity.getXRot());
         itemData.method6274("headYaw", args -> (double)entity.getYHeadRot());
         itemData.method6274("eyeY", args -> entity.getEyeY());
         itemData.method6274("width", args -> (double)entity.getBbWidth());
         itemData.method6274("height", args -> (double)entity.getBbHeight());
         itemData.method6274("box", args -> method6577(scope, entity.getBoundingBox()));
         itemData.method6274("alive", args -> entity.isAlive() && !entity.isRemoved());
         itemData.method6274("isPlayer", args -> entity instanceof Player);
         itemData.method6274("isLiving", args -> livingEntity != null);
         itemData.method6274("isSelf", args -> entity == method6575().player);
         itemData.method6274("isFriend", args -> entity instanceof Player && Zenith.getInstance().getFriends().method7626(entity));
         itemData.method6274("onGround", args -> entity.onGround());
         itemData.method6274("sneaking", args -> entity.isShiftKeyDown());
         itemData.method6274("sprinting", args -> entity.isSprinting());
         itemData.method6274("invisible", args -> entity.isInvisible());
         itemData.method6274("onFire", args -> entity.isOnFire());
         itemData.method6274("inWater", args -> entity.isInWater());
         itemData.method6274("glowing", args -> entity.isCurrentlyGlowing());
         itemData.method6274("fallDistance", args -> entity.fallDistance);
         itemData.method6274("age", args -> (double)entity.tickCount);
         itemData.method6274("distance", args -> method6575().player == null ? 0.0 : method6575().player.distanceTo(entity));
         itemData.method6274(
            "distanceTo",
            args -> Math.sqrt(
               entity.distanceToSqr(
                  ScriptValueUtils.method6256(args, 0, 0.0), ScriptValueUtils.method6256(args, 1, 0.0), ScriptValueUtils.method6256(args, 2, 0.0)
               )
            )
         );
         itemData.method6274("canSee", args -> method6575().player != null && method6575().player.hasLineOfSight(entity));
         itemData.method6274("health", args -> livingEntity == null ? 0.0 : livingEntity.getHealth());
         itemData.method6274("maxHealth", args -> livingEntity == null ? 0.0 : livingEntity.getMaxHealth());
         itemData.method6274("absorption", args -> livingEntity == null ? 0.0 : livingEntity.getAbsorptionAmount());
         itemData.method6274("armor", args -> livingEntity == null ? 0.0 : livingEntity.getArmorValue());
         itemData.method6274("hurtTime", args -> livingEntity == null ? 0.0 : livingEntity.hurtTime);
         itemData.method6274("gliding", args -> livingEntity != null && livingEntity.isFallFlying());
         itemData.method6274("usingItem", args -> livingEntity != null && livingEntity.isUsingItem());
         itemData.method6274("blocking", args -> livingEntity != null && livingEntity.isBlocking());
         itemData.method6274("mainHand", args -> livingEntity == null ? method6579(scope, ItemStack.EMPTY) : method6579(scope, livingEntity.getMainHandItem()));
         itemData.method6274("offHand", args -> livingEntity == null ? method6579(scope, ItemStack.EMPTY) : method6579(scope, livingEntity.getOffhandItem()));
         itemData.method6274("armorItems", args -> {
            ArrayList items = new ArrayList();
            if (livingEntity != null) {
               for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                  items.add(method6579(scope, livingEntity.getItemBySlot(slot)));
               }
            }

            return ScriptValueUtils.method6267(scope, items);
         });
         itemData.method6274("effects", args -> {
            ArrayList effects = new ArrayList();
            if (livingEntity != null) {
               for (MobEffectInstance activeEffect : livingEntity.getActiveEffects()) {
                  effects.add(method6578(scope, activeEffect));
               }
            }

            return ScriptValueUtils.method6267(scope, effects);
         });
         itemData.method6274("ping", args -> {
            if (entity instanceof Player && method6575().getConnection() != null) {
               PlayerInfo playerInfo = method6575().getConnection().getPlayerInfo(entity.getUUID());
               return playerInfo == null ? 0.0 : playerInfo.getLatency();
            } else {
               return 0.0;
            }
         });
         return itemData.getScriptableObject();
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

   public static Object method6579(Scriptable scope, ItemStack item) {
      ItemStack stack = item == null ? ItemStack.EMPTY : item;
      ScriptValueUtils.Entry2 itemData = ScriptValueUtils.method6247(scope);
      String registryId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
      itemData.method6277("empty", stack.isEmpty());
      itemData.method6277("id", registryId);
      itemData.method6277("count", (double)stack.getCount());
      itemData.method6277("name", stack.isEmpty() ? "" : stack.getHoverName().getString());
      itemData.method6274("is", args -> matches(registryId, ScriptValueUtils.method6254(args, 0, "")));
      itemData.method6274("details", args -> ScriptWorldApi.method4818(scope, stack));
      itemData.method6274("toString", args -> registryId);
      if (stack.isEmpty()) {
         return itemData.getScriptableObject();
      } else {
         itemData.method6277("maxCount", (double)stack.getMaxStackSize());
         itemData.method6277("damage", (double)stack.getDamageValue());
         itemData.method6277("maxDamage", (double)stack.getMaxDamage());
         itemData.method6277("durability", stack.getMaxDamage() <= 0 ? 1.0 : 1.0 - (double)stack.getDamageValue() / stack.getMaxDamage());
         itemData.method6277("damageable", stack.isDamageableItem());
         itemData.method6277("stackable", stack.isStackable());
         itemData.method6277("food", stack.get(DataComponents.FOOD) != null);
         itemData.method6277("block", stack.getItem() instanceof BlockItem);
         itemData.method6277("customName", stack.get(DataComponents.CUSTOM_NAME) != null);
         itemData.method6277("cooldown", method6575().player != null && method6575().player.getCooldowns().isOnCooldown(stack));
         ArrayList lore = new ArrayList();
         ItemLore itemLore = stack.get(DataComponents.LORE);
         if (itemLore != null) {
            for (Component line : itemLore.lines()) {
               if (lore.size() >= 64) {
                  break;
               }

               lore.add(line.getString());
            }
         }

         itemData.method6277("lore", ScriptValueUtils.method6267(scope, lore));
         ArrayList enchantments = new ArrayList();

         for (Entry entry : stack.getEnchantments().entrySet()) {
            enchantments.add(
               ScriptValueUtils.method6247(scope)
                  .method6277("id", ((Holder)entry.getKey()).getRegisteredName())
                  .method6277("level", (double)entry.getIntValue())
                  .getScriptableObject()
            );
         }

         itemData.method6277("enchants", ScriptValueUtils.method6267(scope, enchantments));
         itemData.method6274("enchant", args -> {
            String enchantmentId = ScriptValueUtils.method6254(args, 0, "");

            for (Entry enchantment : stack.getEnchantments().entrySet()) {
               if (matches(((Holder)enchantment.getKey()).getRegisteredName(), enchantmentId)) {
                  return (double)enchantment.getIntValue();
               }
            }

            return 0.0;
         });
         return itemData.getScriptableObject();
      }
   }

   public static boolean matches(String registryId, String query) {
      if (query != null && !query.isEmpty()) {
         String normalizedQuery = query.toLowerCase(Locale.ROOT);
         return registryId.equals(normalizedQuery) || registryId.equals("minecraft:" + normalizedQuery);
      } else {
         return false;
      }
   }

   public static Object method6580(Scriptable scope, BlockPos position) {
      if (method6575().level == null) {
         ScriptValueUtils.Entry2 blockData = ScriptValueUtils.method6247(scope)
            .method6277("id", "minecraft:air")
            .method6277("x", (double)position.getX())
            .method6277("y", (double)position.getY())
            .method6277("z", (double)position.getZ())
            .method6277("air", true)
            .method6277("liquid", false)
            .method6277("solid", false)
            .method6277("replaceable", true)
            .method6277("full", false)
            .method6277("hardness", 0.0)
            .method6277("luminance", 0.0)
            .method6277("properties", ScriptValueUtils.method6247(scope).getScriptableObject());
         blockData.method6274("is", args -> matches("minecraft:air", ScriptValueUtils.method6254(args, 0, "")));
         blockData.method6274("toString", args -> "minecraft:air");
         blockData.method6274("valueOf", args -> "minecraft:air");
         return blockData.getScriptableObject();
      } else {
         BlockState blockState = method6575().level.getBlockState(position);
         String registryId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
         ScriptValueUtils.Entry2 blockData = ScriptValueUtils.method6247(scope)
            .method6277("id", registryId)
            .method6277("x", (double)position.getX())
            .method6277("y", (double)position.getY())
            .method6277("z", (double)position.getZ())
            .method6277("air", blockState.isAir())
            .method6277("liquid", !blockState.getFluidState().isEmpty())
            .method6277("solid", blockState.isRedstoneConductor(method6575().level, position))
            .method6277("replaceable", blockState.canBeReplaced())
            .method6277("full", blockState.isCollisionShapeFullBlock(method6575().level, position))
            .method6277("hardness", (double)blockState.getDestroySpeed(method6575().level, position))
            .method6277("luminance", (double)blockState.getLightEmission());
         blockData.method6274("is", args -> matches(registryId, ScriptValueUtils.method6254(args, 0, "")));
         blockData.method6274("toString", args -> registryId);
         blockData.method6274("valueOf", args -> registryId);
         ScriptValueUtils.Entry2 properties = ScriptValueUtils.method6247(scope);
         blockState.getValues().forEach(property -> properties.method6277(property.property().getName(), property.valueName().toLowerCase(Locale.ROOT)));
         blockData.method6277("properties", properties.getScriptableObject());
         return blockData.getScriptableObject();
      }
   }

   public static Object method6581(Scriptable scope, HitResult hitResult) {
      if (hitResult != null && hitResult.getType() != Type.MISS) {
         Vec3 location = hitResult.getLocation();
         ScriptValueUtils.Entry2 blockData = ScriptValueUtils.method6247(scope).method6277("pos", ScriptValueUtils.method6270(scope, location.x, location.y, location.z));
         if (hitResult instanceof BlockHitResult blockHitResult) {
            blockData.method6277("type", "block");
            blockData.method6277("side", blockHitResult.getDirection().getSerializedName());
            blockData.method6277("block", method6580(scope, blockHitResult.getBlockPos()));
         } else if (hitResult instanceof EntityHitResult entityHitResult) {
            blockData.method6277("type", "entity");
            blockData.method6277("entity", method6576(scope, entityHitResult.getEntity()));
         }

         return blockData.getScriptableObject();
      } else {
         return ScriptValueUtils.method6247(scope).method6277("type", "miss").getScriptableObject();
      }
   }

   public static HitResult method6582(Vec3 eyePosition, Vec3 to, boolean includeFluids) {
      return method6575().level != null && method6575().player != null
         ? method6575().level.clip(new ClipContext(eyePosition, to, Block.OUTLINE, includeFluids ? Fluid.ANY : Fluid.NONE, method6575().player))
         : null;
   }
}

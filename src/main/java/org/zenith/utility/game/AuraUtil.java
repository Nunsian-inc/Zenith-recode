package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.modules.impl.combat.AntiBotModule;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.network.UseItemUtils;

public final class AuraUtil implements ZenithAccess {
   private static final AuraRandomizer auraRandomizer = new AuraRandomizer();
   private static LivingEntity livingEntity;

   public static LivingEntity method7442(
      Iterable<Entity> entitiesForRendering, float f, boolean enabled, List<String> items, List<String> strings, Predicate<LivingEntity> predicate, boolean flag
   ) {
      if (flag && livingEntity != null && method7445(items, livingEntity, f, enabled, predicate)) {
         return livingEntity;
      } else {
         ArrayList arrayList = new ArrayList();

         for (Entity entity : entitiesForRendering) {
            if (entity instanceof LivingEntity livingEntity2 && method7445(items, entity, f, enabled, predicate)) {
               arrayList.add(livingEntity2);
            }
         }

         arrayList.sort(method7443(Comparator.comparing(var0x -> true), strings));
         livingEntity = arrayList.isEmpty() ? null : (LivingEntity)arrayList.getFirst();
         return livingEntity;
      }
   }

   public static Comparator<LivingEntity> method7443(Comparator<LivingEntity> comparing, List<String> strings) {
      comparing = comparing.thenComparing((var0x, var1x) -> Boolean.compare(!(var0x instanceof Player), !(var1x instanceof Player)));
      if (strings.contains("module.aura.targetFov")) {
         comparing = comparing.thenComparingDouble(AuraUtil::method7444);
      }

      if (strings.contains("module.aura.targetArmor")) {
         comparing = comparing.thenComparing(Comparator.comparingDouble(UseItemUtils::method6529).reversed());
      }

      if (strings.contains("module.aura.targetHp")) {
         comparing = comparing.thenComparingDouble(UseItemUtils::method6527);
      }

      if (strings.contains("module.aura.targetDistance")) {
         comparing = comparing.thenComparingDouble(mc.player::distanceToSqr);
      }

      return comparing;
   }

   private static double method7444(LivingEntity livingEntity) {
      if (mc.player == null) {
         return Double.MAX_VALUE;
      } else {
         Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());
         Vec3 eyePosition = mc.player.getEyePosition();
         Rotation rotation2 = Rotation.method3128(livingEntity.getBoundingBox().getCenter(), eyePosition);
         return rotation.method3130(rotation2);
      }
   }

   public static boolean method7445(List<String> items, Entity livingEntity, float f, boolean enabled, Predicate<LivingEntity> predicate) {
      return livingEntity instanceof LivingEntity livingEntity2 && method7447(items, livingEntity2) && predicate.test((LivingEntity)livingEntity) ? auraRandomizer.method4676(livingEntity2, f, enabled) : false;
   }

   public static LivingEntity method7446(Iterable<Entity> entitiesForRendering, float f, boolean enabled, List<String> items, List<String> strings, boolean flag) {
      return method7442(entitiesForRendering, f, enabled, items, strings, var0x -> true, flag);
   }

   public static boolean method7447(List<String> items, LivingEntity livingEntity) {
      if (method7448(livingEntity)) {
         return false;
      } else if (method7449(livingEntity)) {
         return false;
      } else {
         return method7450(livingEntity) ? false : method7451(items, livingEntity);
      }
   }

   private static boolean method7448(LivingEntity livingEntity) {
      return livingEntity == mc.player;
   }

   private static boolean method7449(LivingEntity livingEntity) {
      return !livingEntity.isAlive() || livingEntity.getHealth() <= 0.0F;
   }

   private static boolean method7450(LivingEntity livingEntity) {
      return livingEntity instanceof Player player && AntiBotModule.INSTANCE.method2785(player);
   }

   private static boolean method7451(List<String> items, LivingEntity livingEntity) {
      if (livingEntity instanceof Player player) {
         if (PetManager.method4557(player.getId())) {
            return false;
         } else if (Zenith.getInstance().getFriends().method7627(player.getGameProfile().name())) {
            return false;
         } else {
            return UseItemUtils.method6529(player) == 0.0F ? items.contains("module.aura.noarmor") : items.contains("module.aura.targetPlayers");
         }
      } else if (livingEntity instanceof Mob) {
         return items.contains("module.aura.targetHostile");
      } else {
         return livingEntity instanceof Animal ? items.contains("module.aura.targetPeaceful") : !(livingEntity instanceof ArmorStand);
      }
   }

   @Generated
   private AuraUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

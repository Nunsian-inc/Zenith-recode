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
      Iterable<Entity> entities, float range, boolean ignoreWalls, List<String> targetTypes, List<String> sortModes, Predicate<LivingEntity> targetFilter, boolean keepCurrentTarget
   ) {
      if (keepCurrentTarget && livingEntity != null && method7445(targetTypes, livingEntity, range, ignoreWalls, targetFilter)) {
         return livingEntity;
      } else {
         ArrayList<LivingEntity> candidates = new ArrayList<>();

         for (Entity entity : entities) {
            if (entity instanceof LivingEntity candidate && method7445(targetTypes, entity, range, ignoreWalls, targetFilter)) {
               candidates.add(candidate);
            }
         }

         candidates.sort(method7443(Comparator.comparing(ignoredTarget -> true), sortModes));
         livingEntity = candidates.isEmpty() ? null : candidates.getFirst();
         return livingEntity;
      }
   }

   public static Comparator<LivingEntity> method7443(Comparator<LivingEntity> comparator, List<String> sortModes) {
      comparator = comparator.thenComparing((first, second) -> Boolean.compare(!(first instanceof Player), !(second instanceof Player)));
      if (sortModes.contains("module.aura.targetFov")) {
         comparator = comparator.thenComparingDouble(AuraUtil::rotationDistanceToTarget);
      }

      if (sortModes.contains("module.aura.targetArmor")) {
         comparator = comparator.thenComparing(Comparator.comparingDouble(UseItemUtils::method6529).reversed());
      }

      if (sortModes.contains("module.aura.targetHp")) {
         comparator = comparator.thenComparingDouble(UseItemUtils::method6527);
      }

      if (sortModes.contains("module.aura.targetDistance")) {
         comparator = comparator.thenComparingDouble(mc.player::distanceToSqr);
      }

      return comparator;
   }

   private static double rotationDistanceToTarget(LivingEntity target) {
      if (mc.player == null) {
         return Double.MAX_VALUE;
      } else {
         Rotation playerRotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());
         Vec3 eyePosition = mc.player.getEyePosition();
         Rotation targetRotation = Rotation.lookingAt(target.getBoundingBox().getCenter(), eyePosition);
         return playerRotation.angularDistanceTo(targetRotation);
      }
   }

   public static boolean method7445(List<String> targetTypes, Entity entity, float range, boolean ignoreWalls, Predicate<LivingEntity> targetFilter) {
      return entity instanceof LivingEntity target && method7447(targetTypes, target) && targetFilter.test(target) ? auraRandomizer.method4676(target, range, ignoreWalls) : false;
   }

   public static LivingEntity method7446(Iterable<Entity> entities, float range, boolean ignoreWalls, List<String> targetTypes, List<String> sortModes, boolean keepCurrentTarget) {
      return method7442(entities, range, ignoreWalls, targetTypes, sortModes, ignoredTarget -> true, keepCurrentTarget);
   }

   public static boolean method7447(List<String> targetTypes, LivingEntity target) {
      if (isLocalPlayer(target)) {
         return false;
      } else if (isDead(target)) {
         return false;
      } else {
         return isBot(target) ? false : matchesTargetTypes(targetTypes, target);
      }
   }

   private static boolean isLocalPlayer(LivingEntity target) {
      return target == mc.player;
   }

   private static boolean isDead(LivingEntity target) {
      return !target.isAlive() || target.getHealth() <= 0.0F;
   }

   private static boolean isBot(LivingEntity target) {
      return target instanceof Player player && AntiBotModule.INSTANCE.method2785(player);
   }

   private static boolean matchesTargetTypes(List<String> targetTypes, LivingEntity target) {
      if (target instanceof Player player) {
         if (PetManager.method4557(player.getId())) {
            return false;
         } else if (Zenith.getInstance().getFriends().method7627(player.getGameProfile().name())) {
            return false;
         } else {
            return UseItemUtils.method6529(player) == 0.0F ? targetTypes.contains("module.aura.noarmor") : targetTypes.contains("module.aura.targetPlayers");
         }
      } else if (target instanceof Mob) {
         return targetTypes.contains("module.aura.targetHostile");
      } else {
         return target instanceof Animal ? targetTypes.contains("module.aura.targetPeaceful") : !(target instanceof ArmorStand);
      }
   }

   @Generated
   private AuraUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

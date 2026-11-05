package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "CrystalOptimizer",
   category = ModuleCategory.MISC,
   description = "module.crystaloptimizer.description"
)
public final class CrystalOptimizerModule extends Module {
   public static final CrystalOptimizerModule INSTANCE = new CrystalOptimizerModule();
   private static final double CONST_doubleValue = 3.0;
   private static final double CONST_doubleValue2 = 3.0;
   private static final double CONST_doubleValue3 = 2.0;
   private static final Set<Item> items = Set.of(
      Items.NETHERITE_HELMET,
      Items.NETHERITE_CHESTPLATE,
      Items.NETHERITE_LEGGINGS,
      Items.NETHERITE_BOOTS,
      Items.DIAMOND_HELMET,
      Items.DIAMOND_CHESTPLATE,
      Items.DIAMOND_LEGGINGS,
      Items.DIAMOND_BOOTS,
      Items.NETHERITE_SWORD,
      Items.DIAMOND_SWORD,
      Items.NETHERITE_PICKAXE,
      Items.DIAMOND_PICKAXE,
      Items.NETHERITE_SHOVEL,
      Items.DIAMOND_SHOVEL,
      Items.NETHERITE_AXE,
      Items.DIAMOND_AXE,
      Items.TOTEM_OF_UNDYING,
      Items.END_CRYSTAL,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.GOLDEN_APPLE,
      Items.ENDER_PEARL,
      Items.TRIDENT,
      Items.CROSSBOW,
      Items.ELYTRA
   );
   private final BooleanSetting protectValuableDrops = new BooleanSetting(
      "module.crystalOptimizer.protectValuableDrops", "module.crystalOptimizer.protectValuableDrops.desc", true
   );
   private EndCrystal endCrystalEntity;

   private CrystalOptimizerModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         this.endCrystalEntity = this.method5234();
         if (this.endCrystalEntity != null && mc.options.keyUse.isDown()) {
            mc.rightClickDelay = 0;
            mc.gameMode.attack(mc.player, this.endCrystalEntity);
            org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
         }
      } else {
         this.endCrystalEntity = null;
      }
   }

   @EventTarget
   public void method5233(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST && event.getEntity() instanceof EndCrystal endCrystal) {
         endCrystal.remove(RemovalReason.DISCARDED);
      }
   }

   public EndCrystal getEndCrystalEntity() {
      return this.endCrystalEntity;
   }

   private EndCrystal method5234() {
      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof EndCrystal endCrystal
            && !(mc.player.distanceTo(endCrystal) > 3.0)
            && this.method5235(endCrystal)
            && (!this.protectValuableDrops.isEnabled() || !this.method5236(endCrystal.blockPosition()))) {
            return endCrystal;
         }
      }

      return null;
   }

   private boolean method5235(EndCrystal endCrystal) {
      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 to = eyePosition.add(mc.player.getViewVector(1.0F).scale(3.0));
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(mc.level, mc.player, eyePosition, to, new AABB(eyePosition, to).inflate(1.0), var1x -> var1x == endCrystal, 0.3F);
      if (entityHitResult != null) {
         return true;
      } else {
         BlockHitResult clip = mc.level.clip(new ClipContext(eyePosition, to, Block.OUTLINE, Fluid.NONE, mc.player));
         return clip.getType() == Type.BLOCK && clip.getBlockPos().equals(endCrystal.blockPosition().below());
      }
   }

   private boolean method5236(BlockPos blockPosition) {
      AABB aabb = new AABB(blockPosition.getX() - 3.0, blockPosition.getY() - 3.0, blockPosition.getZ() - 3.0, blockPosition.getX() + 3.0 + 1.0, blockPosition.getY() + 3.0 + 2.0, blockPosition.getZ() + 3.0 + 1.0);
      return mc.level
         .getEntities(null, aabb)
         .stream()
         .filter(ItemEntity.class::isInstance)
         .map(ItemEntity.class::cast)
         .map(itemEntity -> itemEntity.getItem().getItem())
         .anyMatch(items::contains);
   }
}

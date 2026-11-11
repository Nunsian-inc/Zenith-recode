package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;

@ModuleInfo(name = "Jesus", description = "module.jesus.description", category = ModuleCategory.MOVEMENT)
public final class JesusModule extends Module {
   public static final JesusModule INSTANCE = new JesusModule();
   private final ModeSetting mode = new ModeSetting("module.jesus.mode", "module.jesus.mode.desc",
      "module.jesus.mode.solid", "module.jesus.mode.bounce");
   private final BooleanSetting water = new BooleanSetting("module.jesus.water", "module.jesus.water.desc", true);
   private final BooleanSetting lava = new BooleanSetting("module.jesus.lava", "module.jesus.lava.desc", false);
   private final BooleanSetting sneakToDive = new BooleanSetting("module.jesus.sneakToDive", "module.jesus.sneakToDive.desc", true);

   private JesusModule() {
   }

   private boolean canWalk() {
      return isEnabled() && mc.player != null && mc.level != null && mc.gameMode != null && mc.gui.screen() == null
         && mc.player.isAlive() && !mc.player.isSpectator() && !mc.player.isPassenger()
         && !mc.player.isFallFlying() && !mc.player.getAbilities().flying
         && !(sneakToDive.isEnabled() && mc.player.isShiftKeyDown());
   }

   public VoxelShape collisionShape(BlockGetter level, BlockPos position, BlockState state,
      CollisionContext context, VoxelShape original) {
      if (!canWalk() || level != mc.level || !original.isEmpty()
         || !(context instanceof EntityCollisionContext entityContext) || entityContext.getEntity() != mc.player) {
         return original;
      }
      double height = surfaceHeight(position, state);
      // Только верхняя открытая поверхность: снизу жидкость остаётся проходимой.
      return height > 0.0 && mc.player.getBoundingBox().minY >= position.getY() + height - 1.0E-5
         ? Shapes.box(0.0, 0.0, 0.0, 1.0, height, 1.0) : original;
   }

   private double surfaceHeight(BlockPos position, BlockState state) {
      if (!mc.level.hasChunkAt(position)) return 0.0;
      var fluid = state.getFluidState();
      boolean selected = fluid.is(FluidTags.WATER) && water.isEnabled()
         // Лава включается отдельно и только с огнестойкостью: коллизия не отменяет огонь.
         || fluid.is(FluidTags.LAVA) && lava.isEnabled() && mc.player.hasEffect(MobEffects.FIRE_RESISTANCE);
      if (!selected || !mc.level.getFluidState(position.above()).isEmpty()) return 0.0;
      return fluid.getHeight(mc.level, position);
   }

   @EventTarget
   public void onMove(PlayerMoveEvent event) {
      if (!canWalk() || !mode.isSelected(1) || event.isCancelled() || !mc.player.onGround()
         || event.getVec3d().y > 0.0) {
         return;
      }
      AABB box = mc.player.getBoundingBox();
      BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
      int y = Mth.floor(box.minY - 0.001);
      for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX - 1.0E-7); x++) {
         for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ - 1.0E-7); z++) {
            position.set(x, y, z);
            if (!mc.level.hasChunkAt(position)) continue;
            BlockState state = mc.level.getBlockState(position);
            double height = surfaceHeight(position, state);
            if (height > 0.0 && Math.abs(box.minY - y - height) < 0.01 && state.getCollisionShape(mc.level, position).isEmpty()) {
               Vec3 movement = event.getVec3d();
               event.setVec3d(new Vec3(movement.x, 0.08, movement.z));
               Vec3 velocity = mc.player.getDeltaMovement();
               mc.player.setDeltaMovement(velocity.x, 0.08, velocity.z);
               return;
            }
         }
      }
   }
}

package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AirStuck",
   description = "module.airsuck.description",
   category = ModuleCategory.MOVEMENT
)
public final class AirStuckModule extends Module {
   public static final AirStuckModule INSTANCE = new AirStuckModule();
   private final BooleanSetting onlyFalling = new BooleanSetting("module.airStuck.onlyFalling", "module.airStuck.onlyFalling.desc", false);
   private final ModeSetting mode = new ModeSetting("module.airSuck.mode", "module.airSuck.mode.desc", "1.21.4", "1.17.1");
   private Vec3 velocityBeforeEnable = Vec3.ZERO;
   private boolean frozen;
   private boolean onGroundWhenFrozen;

   private AirStuckModule() {
   }

   @EventTarget
   public void onPlayerMove(PlayerMoveEvent event) {
      if (shouldFreeze()) {
         event.setVec3d(Vec3.ZERO);
      }
   }

   @EventTarget
   public void onTick(TickEvent event) {
      if (mc.player == null) {
         return;
      }

      if (!shouldFreeze()) {
         this.frozen = false;
         mc.player.setNoGravity(false);
         return;
      }

      if (!this.frozen) {
         this.frozen = true;
         this.onGroundWhenFrozen = mc.player.onGround();
      }

      mc.player.setDeltaMovement(Vec3.ZERO);
      mc.player.setNoGravity(true);
   }

   @EventTarget
   public void onPlayerTick(PlayerTickEvent event) {
      if (mc.player != null && this.frozen && this.onGroundWhenFrozen && shouldFreeze()) {
         mc.player.setOnGround(true);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (!shouldFreeze()) {
         return;
      }

      if (isModernMode()) {
         if (event.getPacket() instanceof ServerboundMovePlayerPacket) {
            event.setCancelled(true);
         }
      } else if (event.getPacket() instanceof Pos || event.getPacket() instanceof PosRot || event.getPacket() instanceof StatusOnly) {
         event.setCancelled(true);
      }
   }

   @EventTarget(0)
   public void onUpdate(UpdateEvent event) {
      if (mc.player == null || isModernMode() || !shouldFreeze()) {
         return;
      }

      if (Math.abs(zenith.getRotationManager().getEffectiveRotation().getFloatValue() - mc.player.yRotLast) != 0.0F
         || Math.abs(zenith.getRotationManager().getEffectiveRotation().getFloatValue2() - mc.player.xRotLast) != 0.0F) {
         UseItemUtils.method6533(0.0, zenith.getRotationManager().getEffectiveRotation());
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.player == null) {
         return;
      }

      this.velocityBeforeEnable = mc.player.getDeltaMovement();
      this.frozen = false;
      if (shouldFreeze()) {
         mc.player.setNoGravity(true);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.player == null) {
         return;
      }

      if (!mc.player.onGround() && shouldFreeze() && this.velocityBeforeEnable != null) {
         mc.player.setDeltaMovement(this.velocityBeforeEnable);
      }

      mc.player.setNoGravity(false);
   }

   private boolean isModernMode() {
      return this.mode.method2101("1.21.4");
   }

   private boolean shouldFreeze() {
      return mc.player != null && (!this.onlyFalling.isEnabled() || (!mc.player.onGround() && mc.player.fallDistance > 0.0));
   }

   public void reinitialize() {
      this.onEnable();
   }
}

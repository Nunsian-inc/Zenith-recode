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
   private Vec3 vec3d = Vec3.ZERO;
   private boolean frozen;
   private boolean flag;

   private AirStuckModule() {
   }

   @EventTarget
   public void method1577(PlayerMoveEvent event) {
      if (this.method1581()) {
         event.setVec3d(Vec3.ZERO);
      }
   }

   @EventTarget
   public void onTick(TickEvent event) {
      if (mc.player != null) {
         if (!this.method1581()) {
            this.frozen = false;
            mc.player.setNoGravity(false);
         } else {
            if (!this.frozen) {
               this.frozen = true;
               this.flag = mc.player.onGround();
            }

            mc.player.setDeltaMovement(Vec3.ZERO);
            mc.player.setNoGravity(true);
         }
      }
   }

   @EventTarget
   public void method1578(PlayerTickEvent event) {
      if (mc.player != null && this.frozen && this.flag && this.method1581()) {
         mc.player.setOnGround(true);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null && this.method1581()) {
         if (this.method1580()) {
            if (event.getPacket3() instanceof ServerboundMovePlayerPacket) {
               event.setCancelled(true);
            }
         } else if (event.getPacket3() instanceof Pos || event.getPacket3() instanceof PosRot || event.getPacket3() instanceof StatusOnly) {
            event.setCancelled(true);
         }
      }
   }

   @EventTarget(0)
   public void method1579(UpdateEvent event) {
      if (mc.player != null && !this.method1580() && this.method1581()) {
         if (Math.abs(zenith.getRotationManager().method7844().getFloatValue() - mc.player.yRotLast) != 0.0F
            || Math.abs(zenith.getRotationManager().method7844().getFloatValue2() - mc.player.xRotLast) != 0.0F) {
            UseItemUtils.method6533(0.0, zenith.getRotationManager().method7844());
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.player != null) {
         this.vec3d = mc.player.getDeltaMovement();
         this.frozen = false;
         if (this.method1581()) {
            mc.player.setNoGravity(true);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.player != null) {
         if (!mc.player.onGround() && this.method1581() && this.vec3d != null) {
            mc.player.setDeltaMovement(this.vec3d);
         }

         mc.player.setNoGravity(false);
      }
   }

   private boolean method1580() {
      return this.mode.method2101("1.21.4");
   }

   private boolean method1581() {
      return mc.player != null && (!this.onlyFalling.isEnabled() || !mc.player.onGround() && mc.player.fallDistance > 0.0);
   }

   public void method1582() {
      this.onEnable();
   }
}

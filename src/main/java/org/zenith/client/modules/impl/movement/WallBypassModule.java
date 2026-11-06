package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "WallBypass",
   description = "module.wallbypass.description",
   category = ModuleCategory.MOVEMENT
)
public final class WallBypassModule extends Module {
   public static final WallBypassModule INSTANCE = new WallBypassModule();
   private static final double ROTATION_SEARCH_DISTANCE = 3.0;
   private static final int ROTATION_PRIORITY = 70;
   private static final int[] ROTATION_SEARCH_STEPS = new int[]{15, 5, 1};
   private final SliderSetting attackDistance = new SliderSetting(
      "module.wallBypass.attackDistance", 3.0F, 3.0F, 6.0F, 0.1F, "module.wallBypass.attackDistance.desc", "b"
   );
   private Rotation bypassRotation;
   private boolean bypassAvailable;
   private boolean rotationRequested;
   private int remainingMovementPackets;

   private WallBypassModule() {
   }

   @Override
   public void onEnable() {
      this.bypassRotation = this.findBypassRotation();
      this.bypassAvailable = this.bypassRotation != null;
      this.rotationRequested = false;
      this.remainingMovementPackets = 1;
      if (!this.bypassAvailable) {
         this.setToggled(false);
      } else {
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.bypassRotation = null;
      this.bypassAvailable = false;
      this.rotationRequested = false;
      this.remainingMovementPackets = 0;
      super.onDisable();
   }

   @EventTarget
   public void onPlayerMove(PlayerMoveEvent event) {
      if (this.remainingMovementPackets <= 0) {
         event.setVec3d(Vec3.ZERO);
      }
   }

   @EventTarget
   public void onBypassRotation(PreRotationEvent event) {
      if (!this.bypassAvailable || this.bypassRotation == null || mc.player == null || mc.level == null) {
         this.setToggled(false);
      } else if (!this.rotationRequested) {
         Rotation requestedRotation = this.bypassRotation;
         rotationManager.requestRotation(new RotationRequest(requestedRotation, () -> requestedRotation, bcCameraTestnerv.getRotationConfig()), 70, this, 1);
         this.rotationRequested = true;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.getPacket() instanceof ServerboundMovePlayerPacket) {
         if (this.remainingMovementPackets > 0) {
            if (this.rotationRequested) {
               this.remainingMovementPackets--;
            }

            return;
         }

         event.setCancelled(true);
      }
   }

   public boolean method7501() {
      return this.isEnabled() && this.bypassAvailable && this.bypassRotation != null && this.remainingMovementPackets <= 0;
   }

   public float method7502() {
      return this.attackDistance.getValue3();
   }

   private Rotation findBypassRotation() {
      if (mc.player != null && mc.level != null) {
         Rotation currentRotation = rotationManager.getEffectiveRotation();
         float currentYaw = currentRotation.getFloatValue();
         Rotation[] initialRotations = new Rotation[]{
            new Rotation(currentYaw, -90.0F),
            new Rotation(currentYaw, 90.0F),
            new Rotation(currentYaw, 0.0F),
            new Rotation(mc.player.getYRot(), -90.0F),
            new Rotation(mc.player.getYRot(), 90.0F),
            new Rotation(mc.player.getYRot(), 0.0F)
         };

         for (Rotation candidateRotation : initialRotations) {
            if (this.isBypassRotation(candidateRotation)) {
               return candidateRotation;
            }
         }

         for (int angleStep : ROTATION_SEARCH_STEPS) {
            Rotation candidateRotation = this.searchBypassRotation(currentYaw, angleStep);
            if (candidateRotation != null) {
               return candidateRotation;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Rotation searchBypassRotation(float baseYaw, int angleStep) {
      int pitch = -90;

      while (pitch <= 90) {
         for (int yawOffset = 0; yawOffset < 360; yawOffset += angleStep) {
            Rotation positiveYawRotation = new Rotation(baseYaw + yawOffset, pitch);
            if (this.isBypassRotation(positiveYawRotation)) {
               return positiveYawRotation;
            }

            if (yawOffset != 0) {
               Rotation negativeYawRotation = new Rotation(baseYaw - yawOffset, pitch);
               if (this.isBypassRotation(negativeYawRotation)) {
                  return negativeYawRotation;
               }
            }
         }

         pitch += angleStep;
      }

      return null;
   }

   private boolean isBypassRotation(Rotation candidateRotation) {
      if (mc.player != null && mc.level != null) {
         BlockHitResult hitResult = TargetUtils.method3216(mc.player.getEyePosition(1.0F), candidateRotation, 3.0, candidateHit -> true);
         return hitResult != null && hitResult.getType() == Type.MISS && UseItemUtils.method6523(BlockPos.containing(hitResult.getLocation()));
      } else {
         return false;
      }
   }
}

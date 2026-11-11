package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Input;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "SpearAura",
   description = "module.spearaura.description",
   category = ModuleCategory.COMBAT
)
public final class SpearAuraModule extends Module {
   public static final SpearAuraModule INSTANCE = new SpearAuraModule();

   private final SliderSetting aimRadius = new SliderSetting("module.spearAura.aimRadius", 30.0F, 1.0F, 180.0F, 1.0F, "", "°");
   private final BooleanSetting autoSpearRoute = new BooleanSetting("module.spearAura.autoSpearRoute", true);
   private final SliderSetting climbHeight = new SliderSetting("module.spearAura.climbHeight", 20.0F, 1.0F, 200.0F, 1.0F, "", "b");
   private final SliderSetting attackHeight = new SliderSetting("module.spearAura.attackHeight", 0.6F, 0.1F, 3.0F, 0.1F, "", "b");
   private final BooleanSetting autoPursuit = new BooleanSetting("module.spearAura.autoPursuit", "module.spearAura.autoPursuit.desc", true);
   private final BooleanSetting autoSprint = new BooleanSetting("module.spearAura.autoSprint", "module.spearAura.autoSprint.desc", true);
   private Player target;

   private SpearAuraModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player == null || mc.level == null) {
         target = null;
         return;
      }

      if (target == null || !isValidTarget(target) || !autoPursuit.isEnabled()) {
         target = findTarget();
      }

      if (target == null) {
         return;
      }

      if (autoSpearRoute.isEnabled()) {
         selectSpear();
      }
      if (autoSprint.isEnabled()) {
         mc.player.setSprinting(true);
      }
   }

   @EventTarget
   public void onPreRotation(PreRotationEvent event) {
      if (mc.player == null || target == null || !isValidTarget(target)) {
         return;
      }

      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 targetPosition = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
      Rotation rotation = Rotation.lookingAt(targetPosition, eyePosition);
      var config = Zenith.getInstance().getRotationManager().getBcCameraTestnerv().getRotationConfig();
      Zenith.getInstance().getRotationManager().requestRotation(new RotationRequest(rotation, () -> rotation, config), 10, this);
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (!autoPursuit.isEnabled() || mc.player == null || target == null || !isValidTarget(target)) {
         return;
      }

      Vec3 offset = target.position().subtract(mc.player.position());
      if (offset.horizontalDistanceSqr() > 6.25) {
         Input input = event.getPlayerInput();
         Input idle = new Input(false, false, false, false, input.jump(), input.shift(), input.sprint());
         double angle = MovementInputUtils.relativeMovementAngle(offset, rotationManager.getEffectiveRotation().getFloatValue());
         event.setPlayerInput(MovementInputUtils.inputForDirection(idle, angle));
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (!event.isOutgoing() || !autoSpearRoute.isEnabled() || mc.player == null || mc.level == null
         || target == null || !isValidTarget(target)
         || !(event.getPacket() instanceof ServerboundMovePlayerPacket packet) || !packet.hasPosition()) {
         return;
      }

      double horizontalDistance = target.position().subtract(mc.player.position()).horizontalDistance();
      double routeY = target.getY() + climbHeight.getValue3();
      double y = packet.getY(mc.player.getY());
      if (horizontalDistance > 3.0 && mc.player.getY() + 0.5 < routeY) {
         y = Math.min(routeY, mc.player.getY() + 0.35);
      } else if (horizontalDistance <= 3.0) {
         y = target.getY() + attackHeight.getValue3();
      } else {
         return;
      }
      event.setPacket(new ServerboundMovePlayerPacket.PosRot(
         packet.getX(mc.player.getX()), y, packet.getZ(mc.player.getZ()),
         packet.getYRot(mc.player.getYRot()), packet.getXRot(mc.player.getXRot()),
         packet.isOnGround(), packet.horizontalCollision()
      ));
   }

   private Player findTarget() {
      Player closest = null;
      double closestDistance = Double.MAX_VALUE;
      Vec3 look = mc.player.getViewVector(1.0F);
      double maxAngle = aimRadius.getValue3();

      for (Player candidate : mc.level.players()) {
         if (!isValidTarget(candidate)) {
            continue;
         }

         double distance = mc.player.distanceToSqr(candidate);
         if (distance > 128.0 * 128.0 || distance >= closestDistance) {
            continue;
         }

         Vec3 direction = candidate.position().subtract(mc.player.position());
         if (direction.lengthSqr() < 1.0E-6) {
            continue;
         }
         double angle = Math.toDegrees(Math.acos(Mth.clamp(look.dot(direction.normalize()), -1.0, 1.0)));
         if (angle <= maxAngle) {
            closest = candidate;
            closestDistance = distance;
         }
      }
      return closest;
   }

   private boolean isValidTarget(Player candidate) {
      return candidate != null
         && candidate != mc.player
         && candidate.isAlive()
         && !candidate.isSpectator()
         && !Zenith.getInstance().getFriends().method7626(candidate);
   }

   private void selectSpear() {
      for (int slot = 0; slot < 9; slot++) {
         if (mc.player.getInventory().getItem(slot).is(Items.TRIDENT)) {
            mc.player.getInventory().setSelectedSlot(slot);
            return;
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      target = null;
   }
}
